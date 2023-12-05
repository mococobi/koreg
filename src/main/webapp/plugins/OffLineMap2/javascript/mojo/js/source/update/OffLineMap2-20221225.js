(function () {
    if (!mstrmojo.plugins.OffLineMap2) {
        mstrmojo.plugins.OffLineMap2 = {};
    }

    /* 2018-08-20 mksong - IE 함수지원 문제 수정 */
    Number.isInteger = Number.isInteger || function(value) {
        return typeof value === "number" &&
               isFinite(value) &&
               Math.floor(value) === value;
    };

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

    function isTrue(value) { return value === "true" || value === true ? true : false; };

    mstrmojo.plugins.OffLineMap2.OffLineMap2 = mstrmojo.declare(mstrmojo.CustomVisBase, null, {
        scriptClass: "mstrmojo.plugins.OffLineMap2.OffLineMap2",
        cssClass: "offLinemap2",
        errorMessage: "Either there is not enough data to display the visualization or the visualization configuration is incomplete.",
        errorDetails: "This visualization requires one or more attributes and one metric.",
        externalLibraries: [{url:leafletPath}, {url:d3Path}],
        useRichTooltip: true,
        supportNEE: true,
        reuseDOMNode: false,
        getFontStyle: function getFontStyle(styleName) {
            var fontStyle = {}; 
            var fontProps = this.getProperty(styleName);

            fontStyle.fontFamily = fontProps.fontFamily;
            fontStyle.fontColor = fontProps.fontColor ; 
            fontStyle.fontSize = fontProps.fontSize ; 
            fontStyle.fontStyle = isTrue(fontProps.fontItalic) ? "italic" : "normal";
            fontStyle.fontWeight = isTrue(fontProps.fontWeight) ? "bold" : "normal";
            fontStyle.textDecoration = "";
            if (isTrue(fontProps.fontUnderline)) { fontStyle.textDecoration += " underline"; }
            if (isTrue(fontProps.fontLineThrough)) { fontStyle.textDecoration += " line-through"; }
            if (fontStyle.textDecoration === "") { fontStyle.textDecoration = "none"; }

            return fontStyle ; 
        },
        getMetricColor: function (data) {
            // get first Threshold color Info.
            for (var i = 0; i < data.metrics.length; i++) {
                if (data.metrics[i].metricColor && data.metrics[i].metricColor != "") {
                    return data.metrics[i].metricColor;
                }
            }
        },
        getGeoStyle: function (fill, overrideColor, border, borderSize) { // FILLGROUP {fillColor,fillAlpha}, LINEGROUP {lineColor,lineStyle}
            var style = {};

            style.fillColor = overrideColor !== undefined ? overrideColor : fill.fillColor; 
            style.fillOpacity = isNaN(fill.fillAlpha) ? 1 : parseInt(fill.fillAlpha) / 100;
            style.color = border.lineColor;
            style.weight = isNaN(borderSize) ? 1 : parseInt(borderSize);

            var lineType = mstrmojo.vi.models.editors.CustomVisEditorModel.ENUM_LINE_STYLE;
            switch (border.lineStyle) {
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
        },
        plot: function() {
            function updateMap() { addLabelText(labelData); }

            function addLabelText(labelData) {
                function getLabelAttrIndex() {
                    var zoneId = undefined;
                    for (var i = 0; i < me.zonesModel.getCustomDropZones().length; i++) {
                        if (me.zonesModel.getCustomDropZones()[i].name == mstrmojo.desc("OffLineMap.17", "Display Label")) {
                            zoneId = me.zonesModel.getDropZoneObjectsByIndex(i)[0].id; // 데이터 레이블 애트리뷰트는 1개로 제한되어 [0]으로 조회
                            break;
                        }

                    }

                    if (zoneId == undefined) { return {}; }

                    var zone = DIModel.getUnitById(zoneId);
                    console.log("=> zone", zone);
                    console.log("=> ", zone.depth - 1, zone.unit.n);
                    return zone.depth - 1;
                }

                var labelAttrIndex = getLabelAttrIndex();

                if (!isTrue(dataLabel.N) && !isTrue(dataLabel.V)) { return; }

                var mapsvg = d3.select("#" + divId).select("svg");
                var mapG = mapsvg.select("#label" + divId);
                if (mapG.empty()) { mapG = mapsvg.append("g").attr("id", "label" + divId); }
                mapG.selectAll("text").remove();
                var textLabel = mapG.selectAll("text")
                    .data(labelData)
                    .enter()
                    .append("text")
                    .style("font-size", fontLabel.fontSize)
                    .style("font-family", fontLabel.fontFamily)
                    .attr("fill", fontLabel.fontColor)
                    .attr("visibility", "hidden")
                    .attr("id", function(d, i) { return "label_" + i; });

                var valueMargin = 0;
                if (isTrue(dataLabel.N)) {
                    textLabel.append("tspan")
                        .style("text-anchor", "middle")
                        .attr("y", 0).attr("x", 0)
                        //.text(function(d) { return d.node[0].nodeValue; });
                        //.text(function(d) { return d.node[d.node.length - 1].nodeValue; });
                        .text(function(d) { return d.node[labelAttrIndex].nodeValue; });
                    valueMargin = parseInt(fontLabel.fontSize) + 4;
                }
                if (isTrue(dataLabel.V)) {
                    textLabel.append("tspan")
                        .style("text-anchor", "middle")
                        .attr("y", valueMargin).attr("x", 0)
                        .text(function(d) { return d.metrics[0].metricValue; });
                    valueMargin = parseInt(fontLabel.fontSize) + 4;
                }
                textLabel.attr("transform", function(d) {
                    return "translate(" +
                        mymap.latLngToLayerPoint(d.LatLng).x + "," +
                        // (mymap.latLngToLayerPoint(d.LatLng).y + valueMargin + radius(d.metrics[0].metricRValue)) +
                        (mymap.latLngToLayerPoint(d.LatLng).y) +
                        ")";
                });
                
                if (hideCollision) {
                    var labelNodes = textLabel.nodes();
                    var labelLength = labelNodes.length;

                    var labelRect = {}, labelMatrix = {}, labelDisplayList = {}, labelID, matrixSize = 10;
                    // Create label Rect Matrix
                    for (var l = 0; l < labelLength; l++) {
                        var o = labelNodes[l].getBoundingClientRect();
                        labelID = "label_" + l;
                        labelRect[labelID] = o;
                        // Create Label Matrix
                        CreateLabelMatrix(labelID, o);
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
                                if (labelMatrix[i] && labelMatrix[i][j]) {
                                    coNodes = labelMatrix[i][j];
                                    for (node in coNodes) {
                                        if (labelID == node) { continue; }
                                        if (coNodes.hasOwnProperty(node) && labelDisplayList.hasOwnProperty(node)) { // 중첩 노드인 경우 디스플레이 리스트에서 삭제 ,  지워진 노드와는 비교하지 않음
                                            delete labelDisplayList[node];
                                            coNodes[node] = false;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    var nodesSelector = "", labelDisplayListLength = 0;
                    for (node in labelDisplayList) {
                        nodesSelector += (labelDisplayListLength == 0 ? "" : ",") + "#" + node;
                        labelDisplayListLength += 1;
                    }
                    mapG.selectAll(nodesSelector).attr("visibility", "visible").attr("class", "OffLineMapLabel");

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
                } else {
                    textLabel.attr("visibility", "visible").attr("class", "OffLineMapLabel");
                }
            }
            
            this.addUseAsFilterMenuItem();
            this.addThresholdMenuItem();  // Threshold
            var DIModel = new mstrmojo.models.template.DataInterface(this.model.data);
            var rawData, treeData;
            var data = [];
            var bMin = 5, bMax = 30;
            var centerLatLng = [36.0, 128.0];
			var lineType = mstrmojo.vi.models.editors.CustomVisEditorModel.ENUM_LINE_STYLE; 	
            this.setDefaultPropertyValues ({
                fillColor: {fillColor:"#1F77B4", fillAlpha:65},
                borderColor: {lineColor:"#1F77B4", lineStyle:1},
                displaymode: "region",
                bubblemin: 5,
                bubblemax: 30,
                geoJsonFile: "Geo_Sido.json",
                fitRegion: "Data", 
				labelfont: {fontSize: "10pt", fontFamily: "Malgun Gothic", fontWeight: "false", fontColor: "#202020"},
				dataLabel: {N: true, V: true},
				hideCollision: "false",
				zoomLevel: 8,
				centerPosition: {lat:centerLatLng[0], lng:centerLatLng[1]},
				borderColor: {lineColor:"#6C6C6C" , lineStyle:lineType.THIN},
                borderSize: 2
            });

            var fillColor = this.getProperty("fillColor");  //fillColor , fillAlpha
            var displaymode = this.getProperty("displaymode");
            var bubblemin = this.getProperty("bubblemin");
            var bubblemax = this.getProperty("bubblemax");
            var geoJsonFile = this.getProperty("geoJsonFile");
            var fitRegion = this.getProperty("fitRegion");
			var dataLabel = this.getProperty("dataLabel"); 
			var fontLabel = this.getFontStyle("labelfont");  
			var zoomLevel = this.getProperty("zoomLevel");
			var hideCollision = isTrue (this.getProperty("hideCollision")); 
			var centerPosition = this.getProperty("centerPosition"); 
            /*
			var borderColor = this.getProperty("borderColor"); 
            var borderStyle = {} ;
			switch (this.getProperty("borderColor").lineStyle) {
				case lineType.NONE : 				
					borderStyle = {width: 0, dasharray: 0}; break;				
				case lineType.DASHED : 
					borderStyle = {width: 1, dasharray: 3}; break;				
				case lineType.DOTTED : 
					borderStyle = {width: 1, dasharray: 1}; break;				
                case lineType.THIN : 
					borderStyle = {width: 1, dasharray: 0}; break;				
				case lineType.THICK : 
					borderStyle = {width: 2, dasharray: 0}; break;				
			}
            */

			if (fitRegion !== "Data") { centerLatLng = [centerPosition.lat , centerPosition.lng]; }

            // 버블 최소 최대 사이즈 프로퍼티 지정
            bMin = Number.isInteger(parseInt(bubblemin)) ? parseInt(bubblemin) : bMin;
            bMax = Number.isInteger(parseInt(bubblemax)) ? parseInt(bubblemax) : bMax;

            var me = this;
            var me2 = this;
            var divWidth = me.width, divHeight = me.height;
            var marginWidth = 12, marginHeight = 12;
            var svgWidth = me.width - marginWidth , svgHeight = me.height - marginHeight;
            var svgId = "svg" + this.k;
            var divId = "div" + this.k;

            rawData = this.dataInterface.getRawData(mstrmojo.models.template.DataInterface.ENUM_RAW_DATA_FORMAT.ROWS_ADV, {hasTitleName: true, hasSelection: true, hasThreshold: true});

            var headerCnt = rawData[0].headers.length;
            var metricCnt = rawData[0].values.length;

            // MSTR 데이터를 정제하여 지정
            rawData.forEach(function(c) {
                var dHeaders = c.headers, dMetrics = c.values;
                var tmpData = {};

                // process Header Data .
                // tmpData.lat = parseFloat(dHeaders[headerCnt-2].name);
                // tmpData.lon = parseFloat(dHeaders[headerCnt-1].name);
                tmpData.lat = dHeaders[headerCnt - 2].name;
                tmpData.lon = dHeaders[headerCnt - 1].name;
                tmpData.node = [] ;
                for (var i = 0; i < headerCnt; i++) {
                    tmpData.node.push({nodeName:dHeaders[i].tname, nodeValue:dHeaders[i].name, attributeSelector: dHeaders[i].attributeSelector});
                }
                tmpData.colorInfo = c.colorInfo;

                // Process Metric Data
                tmpData.metrics= [];

                for (var i = 0 ; i < metricCnt; i++) {
                    tmpData.metrics.push ({
                        metricName: dMetrics[i].name, 
                        metricValue: dMetrics[i].v, 
                        metricRValue: dMetrics[i].rv,
                        metricColor: (dMetrics[i].threshold) ? dMetrics[i].threshold.fillColor : undefined
                    });
                }

                data.push (tmpData) ;
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
                container = d3.select (this.domNode).append("div").attr("id", "div" + this.k);
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
                console.log(e.event);
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

                return fillColor.fillColor;
            };

            // Geo Json Map
            if (displaymode === "region") {
                var dataMapCD = d3.map(data, function (d) {return d.lat});
                var dataMapNM = d3.map(data, function (d) {return d.lon});

                // Geo Json Loading
                d3.queue().defer(d3.json, libPath + "/lib/" + geoJsonFile).await(makeGeoLayer);
                /* fetch(libPath + "/lib/" + geoJsonFile)
                    .then((response) => response.json())
                    .then((data) => makeGeoLayer(data)); */

                var geoJson;
                // var geoStyle = { color : "#ffffff" , "weight" : 1 , "opacity" : 0.65 , "fillColor" : "#1f77b4" , "fillOpacity" : 0.65 }

                /*
                function getFillColor(data, defaultColor) {
                    for (var i = 0;  data !== undefined && i < data.metrics.length; i++) {
                        if (data.metrics[i].metricColor && data.metrics[i].metricColor != "") {
                            return data.metrics[i].metricColor;
                        }
                    }
    
                    return defaultColor;
                }
                */
    
                function geoStyle(feature) {
                    // console.log( feature.properties.CD + feature.properties.KOR_NM) ;
                    /*
                    var mcolor = fillColor.fillColor ;
                    if (dataMapCD.get(feature.properties.CD)) {
                        mcolor = getTC( dataMapCD.get(feature.properties.CD)) ;
                    }
                    */
                    /*
                    var style = {};

                    var fill  = me.getProperty("fill");
                    style.fillColor = getFillColor(dataMapCD.get(feature.properties.CD), fill.fillColor);
                    style.fillOpacity = isNaN(fill.fillAlpha) ? 1 : parseInt(fill.fillAlpha) / 100;

                    var border = me.getProperty("border");
                    style.color = border.lineColor;

                    var n = me.getProperty("borderSize");
                    style.weight = isNaN(n) ? 1 : parseInt(n);

                    switch (border.lineStyle) {
                        case lineType.NONE:
                            style.weight =0; break;
                        case lineType.DASHED: 
                            style.dashArray = "5,5"; break;
                        case lineType.DOTTED: 
                            style.dashArray = "3,3"; break;
                        case lineType.THIN: 
                        case lineType.THICK: 
                    }

                    Object.assign(style, {"opacity":0.60});
                    */

                    // return  style;
                    return me.getGeoStyle(
                        me.getProperty("fill"),
                        me.getMetricColor(dataMapCD.get(feature.properties.CD)),
                        me.getProperty("border"),
                        me.getProperty("borderSize")
                    )
                }

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

                    geoJson = L.geoJson(geoJsonFeature, {style:geoStyle, className: "geoRegion", onEachFeature: onEachFeature }).addTo(mymap);

                    geoJson.bindTooltip(setToolTip);

					var featureLabel = [];
					geoJson.getLayers().forEach(function(d, i) {
						d.feature.data.LatLng = d.getBounds().getCenter();
						featureLabel.push(d.feature.data);
					});
					labelData = featureLabel;

                    // fit to current features ;
                    if (fitRegion === "Data") {
                        mymap.fitBounds(geoJson.getBounds());
                    }
					
					updateMap();

                    /* var layers = geoJson.getLayers();
                    layers.forEach ( function (e) {
                        var labelPos = e.getCenter();
                        var labelText = "";

                        if(e.feature.data) {
                            labelText = e.feature.properties.KOR_NM ;
                            L.marker (labelPos , {icon:L.divIcon({className : "regionlabel" , html:labelText})}).addTo(mymap);
                        }
                    }); */

                    // 맵 위젯의 클릭 이벤트가 MSTR VI 로 전달 되지 않게 막기
                    L.DomEvent.disableClickPropagation(me.domNode);

                    me.raiseEvent({name:"renderFinished", id:me.k});
                }
            } else  // end of Region
            if (displaymode !== "region") {
                // Bubble Map
                for (var i = 0; i < data.length; i++) {
                    var PopupText = "";
                    var Mradius = 10;       // 기본 마커 사이즈
                    var MetricColor = "";
                    MetricColor = getTC(data[i]);

                    // 표시 모드가 버블인 경우 지름을 메트릭 값으로 지정
                    if (displaymode === "bubble") { Mradius = radius(data[i].metrics[0].metricRValue); }

                    // 팝업에 표시할 데이터 지정
                    for (var hi = 0; hi < data[i].node.length; hi++) { PopupText += "<b>" + data[i].node[hi].nodeName + ": " +data[i].node[hi].nodeValue + "</b><br />"; }
                    for (var mi = 0; mi < data[i].metrics.length; mi ++) { PopupText += data[i].metrics[mi].metricName + " : " +  data[i].metrics[mi].metricValue  + "<br />"; }

                    if (isNaN(data[i].lat) || isNaN(data[i].lon)) { continue; }

                    // 지도에 마커 표시
                    L.circleMarker([data[i].lat, data[i].lon], {
                        radius: Mradius,
                        color: "#000000",
                        width: "0.5",
                        fillColor: MetricColor,
                        fillOpacity: parseInt(fillColor.fillAlpha) / 100,
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

				// Center or not .. 
				if (fitRegion === "Data") {
					var myBounds = L.latLngBounds(data.map(function(e) { return [parseFloat(e.lat), parseFloat(e.lon)]})) ; 
					mymap.fitBounds(myBounds) ;
				}
            } // end of marker
			mymap.on("moveend", updateMap);
			mymap.on("moveend", saveMapProperty);

			function saveMapProperty() {
				try {
					me.setProperty("centerPosition", {lat:mymap.getCenter().lat, lng:mymap.getCenter().lng}, {suppressData:true});
					me.setProperty("zoomLevel", mymap.getZoom(), {suppressData:true});
				} catch (e) {
					console.log("Many interaction could cause invalid state error") ;
				}
			}

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

