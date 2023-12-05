(function () {
    if (!mstrmojo.plugins.OffLineMap2) {
        mstrmojo.plugins.OffLineMap2 = {};
    }

    mstrmojo.requiresCls(
        "mstrmojo.CustomVisBase",
        "mstrmojo.models.template.DataInterface",
        "mstrmojo.vi.models.editors.CustomVisEditorModel"
    );

    var $VISUTIL = mstrmojo.VisUtility;
    var $pluginName = "OffLineMap2";
    var isApp = window.webkit ? true : false;
    var libPath = ((mstrApp.getPluginsRoot && mstrApp.getPluginsRoot()) || "../plugins/") + $pluginName;
    var d3Path = libPath + "/lib/d3.v4.min.js";
    var leafletPath = libPath + "/lib/leaflet.js";
    var d3LibFile = isApp ? "//d3js.org/d3.v4.min.js" : d3Path;
    var topoLibFile = isApp ? "//d3js.org/topojson.v1.min.js" : libPath + "/lib/topojson.v1.min.js";

    // function isTrue(value) { return value === "true" || value === true ? true : false; };

    mstrmojo.plugins.OffLineMap2.OffLineMap2 = mstrmojo.declare(mstrmojo.CustomVisBase, null, {
        scriptClass: "mstrmojo.plugins.OffLineMap2.OffLineMap2",
        cssClass: "offLinemap2",
        errorMessage: "Either there is not enough data to display the visualization or the visualization configuration is incomplete.",
        errorDetails: "This visualization requires one or more attributes and one metric.",
        externalLibraries: [{url:leafletPath}, {url:d3Path}],
        useRichTooltip: true,
        supportNEE: true,
        reuseDOMNode: false,
        init: function (props) { 
            this._super(props); 

            var defaultProps = {
                fill: {fillColor:"#1F77B4", fillAlpha:65},
                border: {lineColor:"#6C6C6C", lineStyle:mstrmojo.vi.models.editors.CustomVisEditorModel.ENUM_LINE_STYLE.THIN},
                borderSize: 2,
                displayMode: "region",
                bubbleMin: 5,
                bubbleMax: 30,
                geoJsonFile: "Geo_Sido.json",
                fitRegion: "Data", 
                selectionFill: {fillColor:"#1F77B4", fillAlpha:65},
                selectionBorder: {lineColor:"#1F77B4", lineStyle:1},
                selectionBorderSize: 2, 
                dataLabel: {N:true, V:true},
                hideCollision: "false",
                labelFont: {fontSize:"10pt", fontFamily:"Malgun Gothic", fontWeight:"false", fontColor:"#202020"} // ,
                // zoomLevel: 8,
                // centerPosition: {lat:centerLatLng[0], lng:centerLatLng[1]}
            };
       
            this.setDefaultPropertyValues(defaultProps);  
        },        
        plot: function () {
            function getFontStyle(font) {
                var style = {}; 
    
                style.fontFamily = font.fontFamily;
                style.fontColor = font.fontColor ; 
                style.fontSize = font.fontSize ; 
                style.fontStyle = $VISUTIL.isTrue(font.fontItalic) ? "italic" : "normal";
                style.fontWeight = $VISUTIL.isTrue(font.fontWeight) ? "bold" : "normal";
                style.textDecoration = "";
                if ($VISUTIL.isTrue(font.fontUnderline)) { style.textDecoration += " underline"; }
                if ($VISUTIL.isTrue(font.fontLineThrough)) { style.textDecoration += " line-through"; }
                if (font.textDecoration === "") { style.textDecoration = "none"; }
    
                return style; 
            }
            function getMetricColor(data) { // get first Threshold color Info.
                for (var i = 0; i < data.metrics.length; i++) {
                    if (data.metrics[i].metricColor && data.metrics[i].metricColor != "") {
                        return data.metrics[i].metricColor;
                    }
                }
            }
            function getGeoStyle(overrideColor) { // FILLGROUP {fillColor,fillAlpha}, LINEGROUP {lineColor,lineStyle}
                var style = {};
    
                style.fillColor = overrideColor !== undefined ? overrideColor : _p_fill.fillColor; 
                style.fillOpacity = isNaN(_p_fill.fillAlpha) ? 1 : parseInt(_p_fill.fillAlpha) / 100;
                style.color = _p_border.lineColor;
                style.weight = isNaN(_p_borderSize) ? 1 : parseInt(_p_borderSize);
    
                var lineType = mstrmojo.vi.models.editors.CustomVisEditorModel.ENUM_LINE_STYLE;
                switch (_p_border.lineStyle) {
                    case lineType.NONE:
                        style.weight = 0; break;
                    case lineType.DASHED: 
                        style.dashArray = "5,5"; break;
                    case lineType.DOTTED: 
                        style.dashArray = "3,3"; break;
                    case lineType.THIN: 
                    case lineType.THICK: 
                }
    
                Object.assign(style, {"opacity":0.60});
    
                return style;            
            }
            function getLatLngData(latLngData) {
                return latLngData.map( function(e) { return [parseFloat(e.lat), parseFloat(e.lng)]} );
            }
			function saveMapProperty() {
				try {
					me.setProperty("centerPosition", {lat:mymap.getCenter().lat, lng:mymap.getCenter().lng}, {suppressData:true});
					me.setProperty("zoomLevel", mymap.getZoom(), {suppressData:true});
				} catch (e) {
					console.log("Many interaction could cause invalid state error") ;
				}
			}
            function getAttrIndexByName(name) {
                var zoneId = undefined;

                for (var i = 0; i < me.zonesModel.getCustomDropZones().length; i++) {
                    if (me.zonesModel.getCustomDropZones()[i].name == name) {
                        if (me.zonesModel.getDropZoneObjectsByIndex(i).length > 0) {
                            zoneId = me.zonesModel.getDropZoneObjectsByIndex(i)[0].id; // 데이터 레이블 애트리뷰트는 1개로 제한되어 [0]으로 조회
                        }
                        break;
                    }
                }

                if (zoneId === undefined) { return -1; }

                var zone = DIModel.getUnitById(zoneId);
                return zone ? zone.depth - 1 : {};
            }
            function updateMap() { addLabelText(labelData); }
            function addLabelText(labelData) {
                if (!$VISUTIL.isTrue(_p_dataLabel.N) && !$VISUTIL.isTrue(_p_dataLabel.V)) { return; }

                var mapsvg = d3.select("#" + divId).select("svg");
                var mapG = mapsvg.select("#label" + divId);
                if (mapG.empty()) { mapG = mapsvg.append("g").attr("id", "label" + divId); }
                mapG.selectAll("text").remove();

                // var labelFont = me.getFontStyle("labelFont");
                /*
                fontColor: "#9A3A0A" -> attr
                fontFamily: "Malgun Gothic" -> style, font-family
                fontSize: "14" -> style, font-size
                fontStyle: "italic" -> style, font-style
                fontWeight: "bold" ->  style, font-weight
                textDecoration: "none" -> style, text-decoration, underline, line-through
                */
                var textLabel = mapG.selectAll("text")
                    .data(labelData)
                    .enter()
                    .append("text")
                    .style("font-size", _p_labelFont.fontSize)
                    .style("font-family", _p_labelFont.fontFamily)
                    .style("font-style", _p_labelFont.fontStyle)
                    .style("font-weight", _p_labelFont.fontWeight)
                    .style("text-decoration", _p_labelFont.textDecoration)
                    .attr("fill", _p_labelFont.fontColor)
                    .attr("visibility", "hidden")
                    .attr("id", function(d, i) { return "label_" + i; });

                var valueMargin = 0;
                if ($VISUTIL.isTrue(_p_dataLabel.N)) {
                    textLabel.append("tspan")
                        .style("text-anchor", "middle")
                        .attr("y", 0).attr("x", 0)
                        //.text(function(d) { return d.node[0].nodeValue; });
                        //.text(function(d) { return d.node[d.node.length - 1].nodeValue; });
                        .text(function(d) { return d.node[labelAttrIndex].nodeValue; });
                    valueMargin = parseInt(_p_labelFont.fontSize) + 4;
                }
                if ($VISUTIL.isTrue(_p_dataLabel.V)) {
                    textLabel.append("tspan")
                        .style("text-anchor", "middle")
                        .attr("y", valueMargin).attr("x", 0)
                        .text(function(d) { return d.metrics[0].metricValue; });
                    valueMargin = parseInt(_p_labelFont.fontSize) + 4;
                }
                textLabel.attr("transform", function(d) {
                    return "translate(" +
                        mymap.latLngToLayerPoint(d.LatLng).x + "," +
                        // (mymap.latLngToLayerPoint(d.LatLng).y + valueMargin + radius(d.metrics[0].metricRValue)) +
                        (mymap.latLngToLayerPoint(d.LatLng).y) +
                        ")";
                });
                
                if (_p_hideCollision) {
                    function CreateLabelMatrix(labelID, o) {
                        var g = cellStartEnd(o);
                        for (var i = g.rowStart; i < g.rowEnd; i++) {
                            for (var j = g.colStart; j < g.colEnd; j++) {
                                if (!labelMatrix[i]) { labelMatrix[i] = {}; }
                                if (!labelMatrix[i][j]) { labelMatrix[i][j] = {}; }
                                labelMatrix[i][j][labelID] = true;
                            }
                        }
                    }

                    function cellStartEnd(n) {
                        var g = {};
                        g.rowStart = Math.floor(n.top / matrixSize);
                        g.rowEnd = Math.floor(n.bottom / matrixSize);
                        g.colStart = Math.floor(n.left / matrixSize);
                        g.colEnd = Math.floor(n.right / matrixSize);
                        return g;
                    }

                    var labelNodes = textLabel.nodes();
                    var labelLength = labelNodes.length;

                    var labelRect = {}, labelMatrix = {}, labelDisplayList = {}, labelID, matrixSize = 10;
                    // Create label Rect Matrix
                    for (var l = 0; l < labelLength; l++) {
                        var o = labelNodes[l].getBoundingClientRect();
                        labelID = "label_" + l;
                        labelRect[labelID] = o;
                        CreateLabelMatrix(labelID, o); // Create Label Matrix
                        labelDisplayList[labelID] = true;
                    }

                    // Detect Collision
                    for (var l = 0; l < labelLength; l++) {
                        labelID = "label_" + l;
                        var o = labelRect[labelID];

                        if (!labelDisplayList.hasOwnProperty(labelID)) { continue; } // 지워진 노드는 비교하지 않음}

                        var g = cellStartEnd(o);
                        for (var i = g.rowStart; i < g.rowEnd; i++) {
                            for (var j = g.colStart; j < g.colEnd; j++) {
                                if (!labelMatrix[i] || !labelMatrix[i][j]) { continue; }
                                // if (labelMatrix[i] && labelMatrix[i][j]) {

                                coNodes = labelMatrix[i][j];
                                for (node in coNodes) {
                                    if (labelID == node) { continue; }
                                    if (coNodes.hasOwnProperty(node) && labelDisplayList.hasOwnProperty(node)) { // 중첩 노드인 경우 디스플레이 리스트에서 삭제 ,  지워진 노드와는 비교하지 않음
                                        delete labelDisplayList[node];
                                        coNodes[node] = false;
                                    }
                                }
                                //}
                            }
                        }
                    }

                    var nodesSelector = "", labelDisplayListLength = 0;
                    for (node in labelDisplayList) {
                        nodesSelector += (labelDisplayListLength == 0 ? "" : ",") + "#" + node;
                        labelDisplayListLength += 1;
                    }
                    mapG.selectAll(nodesSelector).attr("visibility", "visible").attr("class", "OffLineMapLabel");
                } else {
                    textLabel.attr("visibility", "visible").attr("class", "OffLineMapLabel");
                }
            }
            
            this.addUseAsFilterMenuItem();
            this.addThresholdMenuItem();  // Threshold
            var DIModel = new mstrmojo.models.template.DataInterface(this.model.data);
            var rawData, treeData;
            var data = [];
            // var centerLatLng = [36.0, 128.0];
			var lineType = mstrmojo.vi.models.editors.CustomVisEditorModel.ENUM_LINE_STYLE; 	
            var me = this;

            var _p_fill = this.getProperty("fill");  //fillColor , fillAlpha
            var _p_border = this.getProperty("border");
            var _p_borderSize = this.getProperty("borderSize");
            var _p_displayMode = this.getProperty("displayMode");
            var _p_bubbleMin = this.getProperty("bubbleMin");
            var _p_bubbleMax = this.getProperty("bubbleMax");
            var _p_geoJsonFile = this.getProperty("geoJsonFile");
            var _p_fitRegion = this.getProperty("fitRegion");
            var _p_mapUrl = this.getProperty("mapUrl");
            var _p_selectionFill = this.getProperty("selectionFill");
            var _p_selectionBorder = this.getProperty("selectionBorder");
            var _p_selectionBorderSize = this.getProperty("selectionBorderSize");
			var _p_dataLabel = this.getProperty("dataLabel"); 
			var _p_hideCollision = $VISUTIL.isTrue(this.getProperty("hideCollision")); 
            var _p_labelFont = getFontStyle(this.getProperty("labelFont"));
			// var _p_zoomLevel = this.getProperty("zoomLevel");
			// var _p_centerPosition = this.getProperty("centerPosition"); 

			// if (_p_fitRegion !== "Data") { centerLatLng = [centerPosition.lat , centerPosition.lng]; }

            // 버블 최소 최대 사이즈 프로퍼티 지정
            var bMin = 5, bMax = 30;
            bMin = Number.isInteger(parseInt(_p_bubbleMin)) ? parseInt(_p_bubbleMin) : bMin;
            bMax = Number.isInteger(parseInt(_p_bubbleMax)) ? parseInt(_p_bubbleMax) : bMax;

            var divWidth = me.width, divHeight = me.height;
            // var marginWidth = 12, marginHeight = 12;
            // var svgWidth = me.width - marginWidth , svgHeight = me.height - marginHeight;
            // var svgId = "svg" + this.k;
            var divId = "div" + this.k;

            var labelAttrIndex = getAttrIndexByName(mstrmojo.desc("OffLineMap.17", "Display Label"));
            var latAttrIndex = getAttrIndexByName(mstrmojo.desc(7696, "Latitude"));
            var lngAttrIndex = getAttrIndexByName(mstrmojo.desc(7697, "Longitude"));
            var codeAttrIndex = getAttrIndexByName(mstrmojo.desc(518, 'Attribute'));

            rawData = this.dataInterface.getRawData(mstrmojo.models.template.DataInterface.ENUM_RAW_DATA_FORMAT.ROWS_ADV, {hasTitleName:true, hasSelection:true, hasThreshold:true});

            // var headerCnt = rawData[0].headers.length;
            // var metricCnt = rawData[0].values.length;

            // MSTR 데이터를 정제하여 지정
            rawData.forEach(function(c) {
                // var dHeaders = c.headers, dMetrics = c.values;
                var buffer = {};

                // process Header Data .
                // tmpData.lat = parseFloat(dHeaders[headerCnt-2].name);
                // tmpData.lon = parseFloat(dHeaders[headerCnt-1].name);
                // tmpData.lat = dHeaders[headerCnt - 2].name;
                // tmpData.lon = dHeaders[headerCnt - 1].name;
                buffer.lat = latAttrIndex == -1 ? undefined : c.headers[latAttrIndex].name;
                buffer.lng = lngAttrIndex == -1 ? undefined : c.headers[lngAttrIndex].name;
                buffer.code = codeAttrIndex == -1 ? undefined : c.headers[codeAttrIndex].name;
                buffer.label = codeAttrIndex == -1 ? undefined : c.headers[labelAttrIndex].name;

                buffer.node = [] ;
                for (var i = 0; i < rawData[0].headers.length; i++) {
                    buffer.node.push({nodeName:c.headers[i].tname, nodeValue:c.headers[i].name, attributeSelector:c.headers[i].attributeSelector});
                }
                buffer.colorInfo = c.colorInfo;

                // Process Metric Data
                buffer.metrics= [];

                for (var i = 0 ; i < rawData[0].values.length; i++) {
                    buffer.metrics.push ({
                        metricName: c.values[i].name, 
                        metricValue: c.values[i].v, 
                        metricRValue: c.values[i].rv,
                        metricColor: c.values[i].threshold ? c.values[i].threshold.fillColor : undefined
                    });
                }

                data.push(buffer);
            });

            var selectedItem = "";
            var valueExtent = d3.extent(rawData.map(function(d) {return d.values[0].rv}));
            var radius = d3.scaleLinear().domain(valueExtent).range([bMin,bMax]);
			var labelData = [];

            d3.select(this.domNode).select("div").remove();
            var container  = d3.select(this.domNode).select("div");
            // var contSvg = container.select(svgId) ;

            // 맵 위젯 설정
            if (container.empty()) {
                container = d3.select(this.domNode).append("div").attr("id", "div" + this.k);
                container
                    .style("width", divWidth +"px" )
                    .style("height", divHeight +"px" )
                    .style("position", "relative")
                    .style("background", "transparent")
                    .attr("class", "leaflet-container leaflet-touch leaflet-fade-anim leaflet-grab leaflet-touch-drag leaflet-touch-zoom");
                container
                    .append("div")
                    .attr("class", "leaflet-bottom leaflet-left");
            }

            // 한국 위치로 셋팅. 줌레벨을 8 로 기본 지정
            var mymap = L.map(divId).setView([36, 128], 8);

            // 맵 속성 설정
            /* L.tileLayer("/MicroStrategy/Map/Tiles/{z}/{x}/{y}.png", {
                  maxZoom : 15
                , minZoom : 5
                , errorTileUrl : "/MicroStrategy/Map/Tiles/errorTile.png"
                , attribution: "Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, " +
                                "<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>,"
                , id: "OpenStreetMap"
            }).addTo(mymap); */

            L.DomEvent.on(mymap, "click", function(e) {
                L.DomEvent.stopPropagation(e);
            });

            // 메트릭 색상 찾기. 메트릭 순서 대로 임계값이 있는 데이터를 리턴. 다른 메트릭으로 지정하려면 앞 순서의 메트릭 임계 값을 시각화 에서 지울것
            var getTC = function getTC(tcData) {
                // get first Threshold color Info.
                for (var i = 0; i < tcData.metrics.length; i++) {
                    if (tcData.metrics[i].metricColor && tcData.metrics[i].metricColor != "") {
                        return tcData.metrics[i].metricColor;
                    }
                }

                return fill.fillColor;
            };

            // Geo Json Map
            if (_p_displayMode === "region") {
                var dataMapCD = d3.map(data, function (d) {return d.lat});
                var dataMapNM = d3.map(data, function (d) {return d.lng});

                // Geo Json Loading
                d3.queue().defer(d3.json, libPath + "/lib/" + _p_geoJsonFile).await(makeGeoLayer);

                var geoJson;
                // var geoStyle = { color : "#ffffff" , "weight" : 1 , "opacity" : 0.65 , "fillColor" : "#1f77b4" , "fillOpacity" : 0.65 }

                function regionClick (e) {
                    var layer = e.target;
                    var feature = layer.feature;

                    if (feature.data.node[0]) {
                        if (selectedItem !== feature.data.node[0].attributeSelector) {
                            selectedItem = feature.data.node[0].attributeSelector;
                            me.applySelection(selectedItem); // for web
                        } else {  // Delete Selected Area
                            selectedItem = "" ;
                            me.clearSelections();
                            me.endSelections();
                        }
                    }

                    // geoJson.getLayers(); // 모든 layer 조회
                    // style의 color는 경계색, fillColor는 채우기 색
                    for (var i = 0; i < geoJson.getLayers().length; i++) {
                        var l = geoJson.getLayers()[i];
                        if (l == layer) {
                            layer.setStyle({fillOpacity: 0.8, color:"red", fillColor:"silver", weight:6});
                        } else {
                            geoJson.resetStyle(l);
                        }
                    }
                }

                function resetHighlight(e) { 
                    // geoJson.resetStyle(e.target); 
                }

                function highlightFeature(e) {
                    var layer = e.target;
                    // layer.setStyle({fillOpacity: 0.8, fillColor:"silver"});
                }

                function onEachFeature(feature , layer)  {
                    if (dataMapCD.get(feature.properties.CD)) { feature.data = dataMapCD.get(feature.properties.CD); }

                    /* 2018-09-20 mksong - KOR_NM 제거 */
                    /* if (dataMapNM.get(feature.properties.KOR_NM)) {
                        feature.data = dataMapNM.get(feature.properties.KOR_NM);
                    } */

                    layer.on({mouseup:regionClick, mouseover:highlightFeature, mouseout:resetHighlight});
                }

                function setToolTip(layer) {
                    // console.log (layer);
                    var PopupText = "" ;

                    if (layer.feature.data) {
                        // 팝업에 표시할 데이터 지정
                        for (var hi = 0; hi < layer.feature.data.node.length; hi++) {
                            PopupText += "<b>" + layer.feature.data.node[hi].nodeName + ": " + layer.feature.data.node[hi].nodeValue + "</b><br />";
                        }
                        for (var mi = 0; mi < layer.feature.data.metrics.length; mi++) {
                            PopupText += layer.feature.data.metrics[mi].metricName + " : " +  layer.feature.data.metrics[mi].metricValue  + "<br />";
                        }
                        return PopupText; // layer.feature.properties.KOR_NM ;
                    } else {
                        return "";
                    }
                }

                function makeGeoLayer(error, loadJsonFeature) {
                // function makeGeoLayer(loadJsonFeature) {
                    var geoJsonFeature = [];

                    for (var i = 0; i < loadJsonFeature.features.length; i++) {
                        if (dataMapCD.get(loadJsonFeature.features[i].properties.CD)) {
                            geoJsonFeature.push(loadJsonFeature.features[i]);
                            geoJsonFeature.data = dataMapCD.get(loadJsonFeature.features[i].properties.CD);
                        } // lookup from name

                        /* 2018-09-20 mksong - KOR_NM 제거 */
                        /* else if (dataMapNM.get(loadJsonFeature.features[i].properties.KOR_NM)) {
                            geoJsonFeature.push (loadJsonFeature.features[i]) ;
                            geoJsonFeature.data = dataMapNM.get(loadJsonFeature.features[i].properties.KOR_NM);
                        } */
                    }

                    geoJson = L.geoJson(
                                    geoJsonFeature, {
                                        style: function(feature) { return getGeoStyle( getMetricColor(dataMapCD.get(feature.properties.CD)) ) },
                                        className: "geoRegion", 
                                        onEachFeature: onEachFeature 
                                    }).addTo(mymap);

                    geoJson.bindTooltip(setToolTip);

					var featureLabel = [];
					geoJson.getLayers().forEach(function(d, i) {
						d.feature.data.LatLng = d.getBounds().getCenter();
						featureLabel.push(d.feature.data);
					});
					labelData = featureLabel;

                    // fit to current features ;
                    if (_p_fitRegion === "Data") {
                        mymap.fitBounds(geoJson.getBounds());
                    }
					
					updateMap();

                    /* 레이블 위치를 위해 임시로 허용
                    var layers = geoJson.getLayers();
                    layers.forEach( function(e) {
                        var labelPos = e.getCenter();
                        var labelText = "";

                        if (e.feature.data) {
                            // labelText = e.feature.properties.KOR_NM ;
                            // L.marker(labelPos, {icon:L.divIcon({className:"regionlabel", html:labelText})}).addTo(mymap);
                            labelText = 
                                "<div class='offlinemap-regionlabel-center'>" +
                                e.feature.properties.KOR_NM
                                "</div>";
                            // L.marker(labelPos, {icon:L.divIcon({html:labelText})}).addTo(mymap);
                            L.marker(labelPos, {icon:L.divIcon({className:"offlinemap-regionlabel", html:labelText})}).addTo(mymap);
                        }
                    } );
                    */

                    // 맵 위젯의 클릭 이벤트가 MSTR VI 로 전달 되지 않게 막기
                    L.DomEvent.disableClickPropagation(me.domNode);

                    me.raiseEvent({name:"renderFinished", id:me.k});
                }
            } else  // end of Region
            if (_p_displayMode !== "region") {
                // Bubble Map
                for (var i = 0; i < data.length; i++) {
                    var PopupText = "";
                    var Mradius = 10;       // 기본 마커 사이즈
                    var MetricColor = "";
                    MetricColor = getTC(data[i]);

                    // 표시 모드가 버블인 경우 지름을 메트릭 값으로 지정
                    if (_p_displayMode === "bubble") { Mradius = radius(data[i].metrics[0].metricRValue); }

                    // 팝업에 표시할 데이터 지정
                    for (var hi = 0; hi < data[i].node.length; hi++) { PopupText += "<b>" + data[i].node[hi].nodeName + ": " +data[i].node[hi].nodeValue + "</b><br />"; }
                    for (var mi = 0; mi < data[i].metrics.length; mi ++) { PopupText += data[i].metrics[mi].metricName + " : " +  data[i].metrics[mi].metricValue  + "<br />"; }

                    if (isNaN(data[i].lat) || isNaN(data[i].lng)) { continue; }

                    // 지도에 마커 표시
                    L.circleMarker([data[i].lat, data[i].lng], {
                        radius: Mradius,
                        color: "#000000",
                        width: "0.5",
                        fillColor: MetricColor,
                        fillOpacity: parseInt(_p_fill.fillAlpha) / 100,
                        className: "nodecircles",
                    }).addTo(mymap).bindPopup(PopupText).bindTooltip(data[i].node[0].nodeValue);
                }

                // 마커  선택 이벤트 추가 -> 선택시 다른 시각화 업데이트
                var nodeCircles = container.selectAll(".nodecircles").data(data);
                nodeCircles.on("click", function(d, i) {
                    selectedItem = d.node[0].attributeSelector;
                    me.applySelection(d.node[0].attributeSelector); // for web
                });

                // 선택 된것 지우기 이벤트
                container.on("click" , function(d, i) {
                    // console.log("container click");
                    // d3.event.stopPropagation();

                    if (!event.target.classList.contains("nodecircles")&& selectedItem  != "") {
                        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.selectionDataJSONString) { // mobile
                            var d = {};
                            d.messageType = "deselection";
                            window.webkit.messageHandlers.selectionDataJSONString.postMessage(d);
                        } else {
                            me.clearSelections();
                            me.endSelections();
                        }
                        selectedItem = "";
                    } else {
                        return true;
                    }
                });

				if (_p_fitRegion === "Data") { mymap.fitBounds( L.latLngBounds( getLatLngData(data) )); }
            } // end of marker
			mymap.on("moveend", updateMap);
			mymap.on("moveend", saveMapProperty);

            // 맵 위젯의 클릭 이벤트가 MSTR VI 로 전달 되지 않게 막기
            L.DomEvent.disableClickPropagation(me.domNode);

            /* 2018-11-16 mksong 수정 - PDF 내보내기 추가 */
            /*
            me.raiseEvent({
                  name: "renderFinished"
                , id: me.k
            });
            */
        }
    })
}());
//@ sourceURL=OffLineMap2.js

