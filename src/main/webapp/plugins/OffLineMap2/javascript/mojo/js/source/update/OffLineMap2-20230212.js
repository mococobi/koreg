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
                // mapUrl: "",
                selectionFill: {fillColor:"#1F77B4", fillAlpha:65},
                selectionBorder: {lineColor:"#1F77B4", lineStyle:1},
                selectionBorderSize: 2, 
                dataLabel: {N:true, V:true},
                hideCollision: "false",
                labelFont: {fontSize:"10pt", fontFamily:"Malgun Gothic", fontWeight:"false", fontColor:"#202020"},
                zoomLevel: 8 //,
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
            // 메트릭 색상 찾기. 메트릭 순서 대로 임계값이 있는 데이터를 리턴. 다른 메트릭으로 지정하려면 앞 순서의 메트릭 임계 값을 시각화 에서 지울것
            function getMetricColor(data, defaultColor) { // get first Threshold color Info.
                for (var i = 0; i < data.metrics.length; i++) {
                    if (data.metrics[i].metricColor && data.metrics[i].metricColor != "") {
                        return data.metrics[i].metricColor;
                    }
                }

                return defaultColor;
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
                /*
                for (var i = 0; header && i < header.length; i++) {
                    if (header[i].attributeSelector.tid == zoneId) { return i; }
                }

                return -1;
                */
            }
            function updateMap(evt) { addLabelText(); }
            function addLabelText() {
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
                    .data(featureLabel)
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

                if ($VISUTIL.isTrue(_p_dataLabel.N)) {
                    textLabel.append("tspan")
                        .style("text-anchor", "middle")
                        .attr("y", 0).attr("x", 0)
                        .text(function(d) { return d.node[labelAttrIndex].nodeValue; });
                }
                if ($VISUTIL.isTrue(_p_dataLabel.V)) {
                    textLabel.append("tspan")
                        .style("text-anchor", "middle")
                        .attr("y", parseInt(_p_labelFont.fontSize) + 4).attr("x", 0) // 첫줄의 높이만큼 y 이동
                        .text(function(d) { return d.metrics[0].metricValue; });
                    // valueMargin = parseInt(_p_labelFont.fontSize) + 4;
                }

                textLabel.attr("transform", function(d) {
                    var margin = 0;

                    if (_p_displayMode == "bubble") {
                        margin += 3;
                        margin += parseInt(_p_labelFont.fontSize) + 4;
                        margin += radius(d.metrics[0].metricRValue);
                    } else
                    if (_p_displayMode == "circle") {
                        margin += 3;
                        margin += parseInt(_p_labelFont.fontSize) + 4;
                        margin += 10;
                    }

                    return "translate(" +
                        mymap.latLngToLayerPoint(d.LatLng).x + "," +
                        // (mymap.latLngToLayerPoint(d.LatLng).y + valueMargin + radius(d.metrics[0].metricRValue)) +
                        (mymap.latLngToLayerPoint(d.LatLng).y + margin) +
                        ")";
                });
                
                if (_p_hideCollision) {
                    function CreateLabelMatrix(labelId, o) {
                        var g = cellStartEnd(o);

                        for (var i = g.rowStart; i < g.rowEnd; i++) {
                            for (var j = g.colStart; j < g.colEnd; j++) {
                                if (!labelMatrix[i]) { labelMatrix[i] = {}; }
                                if (!labelMatrix[i][j]) { labelMatrix[i][j] = {}; }
                                labelMatrix[i][j][labelId] = true;
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

                    var labelRect = {}, labelMatrix = {}, labelDisplayList = {}, matrixSize = 10;
                    // Create label Rect Matrix
                    for (var l = 0; l < labelLength; l++) {
                        var o = labelNodes[l].getBoundingClientRect();
                        var labelId = "label_" + l;
                        labelRect[labelId] = o;
                        CreateLabelMatrix(labelId, o); // Create Label Matrix
                        labelDisplayList[labelId] = true;
                    }

                    // Detect Collision
                    for (var l = 0; l < labelLength; l++) {
                        var labelId = "label_" + l;
                        var o = labelRect[labelId];

                        if (!labelDisplayList.hasOwnProperty(labelId)) { continue; } // 지워진 노드는 비교하지 않음}

                        var g = cellStartEnd(o);
                        for (var i = g.rowStart; i < g.rowEnd; i++) {
                            for (var j = g.colStart; j < g.colEnd; j++) {
                                if (!labelMatrix[i] || !labelMatrix[i][j]) { continue; }
                                // if (labelMatrix[i] && labelMatrix[i][j]) {

                                coNodes = labelMatrix[i][j];
                                for (node in coNodes) {
                                    if (labelId == node) { continue; }
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
            function refineData(rawData) {
                var buffer = [];
                (rawData || []).forEach(function(c) {
                    var row = {};
    
                    // tmpData.lon = dHeaders[headerCnt - 1].name;
                    row.lat = latAttrIndex == -1 ? undefined : c.headers[latAttrIndex].name;
                    row.lng = lngAttrIndex == -1 ? undefined : c.headers[lngAttrIndex].name;
                    row.code = codeAttrIndex == -1 ? undefined : c.headers[codeAttrIndex].name;
                    row.label = labelAttrIndex == -1 ? undefined : c.headers[labelAttrIndex].name;
                    if (row.lat && row.lng) {
                        row.LatLng = new L.LatLng(row.lat, row.lng) ;
                    }
    
                    row.node = [] ;
                    for (var i = 0; i < rawData[0].headers.length; i++) {
                        row.node.push({nodeName:c.headers[i].tname, nodeValue:c.headers[i].name, attributeSelector:c.headers[i].attributeSelector});
                    }
                    row.colorInfo = c.colorInfo;
    
                    // Process Metric Data
                    row.metrics= [];
    
                    for (var i = 0 ; i < rawData[0].values.length; i++) {
                        row.metrics.push ({
                            metricName: c.values[i].name, 
                            metricValue: c.values[i].v, 
                            metricRValue: c.values[i].rv,
                            metricColor: c.values[i].threshold ? c.values[i].threshold.fillColor : undefined
                        });
                    }
    
                    buffer.push(row);
                });

                return buffer;
            }
            function renderGeoJson() {
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

                    // geoJson.getLayers(); -> 모든 layer 조회
                    // style의 color는 경계색, fillColor는 채우기 색
                    for (var i = 0; i < geoJson.getLayers().length; i++) {
                        var l = geoJson.getLayers()[i];
                        if (l == layer) {
                            if (l.feature.selected) {
                                geoJson.resetStyle(l);    
                                l.feature.selected = false;
                            } else {
                                l.setStyle({
                                    fillOpacity: 0.8, 
                                    color: _p_selectionBorder.lineColor, 
                                    fillColor: _p_selectionFill.fillColor, 
                                    weight: _p_selectionBorderSize
                                });
                                l.feature.selected = true;
                            }
                        } else {
                            geoJson.resetStyle(l);
                            l.feature.selected = false;
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
                    if (codeMap && codeMap.get(feature.properties.CD)) { feature.data = codeMap.get(feature.properties.CD); }

                    layer.on({mouseup:regionClick, mouseover:highlightFeature, mouseout:resetHighlight});
                }
                function setToolTip(layer) {
                    var popupText = "" ;

                    if (layer.feature.data) {
                        // 팝업에 표시할 데이터 지정
                        for (var hi = 0; hi < layer.feature.data.node.length; hi++) {
                            popupText += "<b>" + layer.feature.data.node[hi].nodeName + ": " + layer.feature.data.node[hi].nodeValue + "</b><br />";
                        }
                        for (var mi = 0; mi < layer.feature.data.metrics.length; mi++) {
                            popupText += layer.feature.data.metrics[mi].metricName + " : " +  layer.feature.data.metrics[mi].metricValue  + "<br />";
                        }
                        return popupText;
                    } else {
                        return "";
                    }
                }
                function makeGeoLayer(error, loadJsonFeature) {
                    var geoJsonFeature = [];

                    for (var i = 0; i < loadJsonFeature.features.length; i++) {
                        if (codeMap && codeMap.get(loadJsonFeature.features[i].properties.CD)) {
                            geoJsonFeature.push(loadJsonFeature.features[i]);
                            geoJsonFeature.data = codeMap.get(loadJsonFeature.features[i].properties.CD);
                        } // lookup from name
                    }

                    geoJson = L.geoJson(
                                    geoJsonFeature, {
                                        style: function(feature) { 
                                            return getGeoStyle( getMetricColor(codeMap ? codeMap.get(feature.properties.CD) : undefined )  )
                                        },
                                        className: "geoRegion", 
                                        onEachFeature: onEachFeature 
                                    }).addTo(mymap);

                    geoJson.bindTooltip(setToolTip);

					featureLabel = [];
					geoJson.getLayers().forEach(function(d, i) {
						d.feature.data.LatLng = d.getBounds().getCenter();
						featureLabel.push(d.feature.data);
					});

                    if (_p_fitRegion === "Data") { // fit to current features ;
                        mymap.fitBounds(geoJson.getBounds());
                    }
					
					updateMap();

                    // 맵 위젯의 클릭 이벤트가 MSTR VI 로 전달 되지 않게 막기
                    L.DomEvent.disableClickPropagation(me.domNode);

                    me.raiseEvent({name:"renderFinished", id:me.k});
                }

                var codeMap = {};
                if (codeAttrIndex != -1) { codeMap = d3.map(data, function (d) {return d.code}); }
                var geoJson = undefined;

                // Geo Json Loading
                d3.queue().defer(d3.json, libPath + "/lib/" + _p_geoJsonFile).await(makeGeoLayer);
            }
            function renderMarker() {
                // Bubble Map
                for (var i = 0; i < data.length; i++) {
                    var popupText = "";
                    var markerRadius = 10;       // 기본 마커 사이즈
                    var metricColor = getMetricColor(data[i], _p_fill.fillColor)

                    // 표시 모드가 버블인 경우 지름을 메트릭 값으로 지정
                    if (_p_displayMode === "bubble") { markerRadius = radius(data[i].metrics[0].metricRValue); }

                    // 팝업에 표시할 데이터 지정
                    for (var j = 0; j < data[i].node.length; j++) { popupText += "<b>" + data[i].node[j].nodeName + ": " +data[i].node[j].nodeValue + "</b><br />"; }
                    for (var j = 0; j < data[i].metrics.length; j++) { popupText += data[i].metrics[j].metricName + " : " +  data[i].metrics[j].metricValue  + "<br />"; }

                    if (isNaN(data[i].lat) || isNaN(data[i].lng)) { continue; }

                    // 지도에 마커 표시
                    L.circleMarker([data[i].lat, data[i].lng], {
                        radius: markerRadius,
                        color: _p_border.lineColor,
                        width: "0.5",
                        fillColor: metricColor,
                        fillOpacity: parseInt(_p_fill.fillAlpha) / 100,
                        className: "nodecircles",
                    //}).addTo(mymap).bindPopup(popupText).bindTooltip(data[i].node[0].nodeValue);
                    }).addTo(mymap).bindTooltip(popupText);
                }

                // 마커  선택 이벤트 추가 -> 선택시 다른 시각화 업데이트
                container.selectAll(".nodecircles").data(data)
                    .on("click", function(d, i) {
                        selectedItem = d.node[0].attributeSelector;
                        me.applySelection(d.node[0].attributeSelector);
                    });

                // 선택 된것 지우기 이벤트
                container.on("click" , function(d, i) {
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

                featureLabel = data;

                updateMap();
            }

            this.addUseAsFilterMenuItem();
            this.addThresholdMenuItem();  // Threshold

            var DIModel = new mstrmojo.models.template.DataInterface(this.model.data);
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
            var _p_zoomLevel = this.getProperty("zoomLevel");

            // 버블 최소 최대 사이즈 프로퍼티 지정
            var bubbleMin = Number.isInteger(parseInt(_p_bubbleMin)) ? parseInt(_p_bubbleMin) : 5;
            var bubbleMax = Number.isInteger(parseInt(_p_bubbleMax)) ? parseInt(_p_bubbleMax) : 30;

            var divId = "div" + this.k;

            var rawData = this.dataInterface.getRawData(
                mstrmojo.models.template.DataInterface.ENUM_RAW_DATA_FORMAT.ROWS_ADV, {hasTitleName:true, hasSelection:true, hasThreshold:true}
            );

            var labelAttrIndex = getAttrIndexByName(mstrmojo.desc("OffLineMap.17", "Display Label"));
            var latAttrIndex = getAttrIndexByName(mstrmojo.desc(7696, "Latitude"));
            var lngAttrIndex = getAttrIndexByName(mstrmojo.desc(7697, "Longitude"));
            var codeAttrIndex = getAttrIndexByName(mstrmojo.desc(518, 'Attribute'));

            // 원시데이터 정제
            var data = refineData(rawData);

            var selectedItem = undefined;
            var valueExtent = d3.extent(rawData.map(function(d) {return d.values[0].rv})); // 첫번째 매트릭 기준 최대 최소 수치 산출
            var radius = d3.scaleLinear().domain(valueExtent).range([bubbleMin, bubbleMax]); // bubbleMin, bubbleMax 스케일로 수치로 변환하기 위한 함수 생성
            var featureLabel = undefined;

            d3.select(this.domNode).select("div").remove();
            var container = d3.select(this.domNode).select("div");

            // 맵 위젯 설정
            if (container.empty()) {
                container = d3.select(this.domNode).append("div").attr("id", "div" + this.k);
                container
                    .style("width", me.width +"px" )
                    .style("height", me.height +"px" )
                    .style("position", "relative")
                    .style("background", "transparent")
                    .attr("class", "leaflet-container leaflet-touch leaflet-fade-anim leaflet-grab leaflet-touch-drag leaflet-touch-zoom");
                container
                    .append("div")
                    .attr("class", "leaflet-bottom leaflet-left");
            }

            // 한국 위치로 셋팅. 줌레벨을 8 로 기본 지정
            var mymap = L.map(divId).setView([36, 128], _p_zoomLevel);

            // 맵 속성 설정
            if (_p_mapUrl) {
                L.tileLayer(_p_mapUrl + "/Tiles/{z}/{x}/{y}.png", { // L.tileLayer("/MicroStrategy/Map/Tiles/{z}/{x}/{y}.png", {
                    maxZoom: 15,
                    minZoom: 5,
                    errorTileUrl: "/MicroStrategy/Map/Tiles/errorTile.png",
                    attribution: "Map data &copy; <a href='https://www.openstreetmap.org/'>OpenStreetMap</a> contributors, " +
                                 "<a href='https://creativecommons.org/licenses/by-sa/2.0/'>CC-BY-SA</a>",
                    id: "OpenStreetMap"
                }).addTo(mymap);
            }

            L.DomEvent.on(mymap, "click", function(e) {
                L.DomEvent.stopPropagation(e);
            });

            // Geo Json Map
            if (_p_displayMode === "region") {
                renderGeoJson();
            } else  // end of Region
            if (_p_displayMode !== "region") {
                renderMarker();
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

