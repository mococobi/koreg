(function () { 
    if (!mstrmojo.plugins.OffLineMap) {
        mstrmojo.plugins.OffLineMap = {};
    }

    mstrmojo.requiresCls(
        "mstrmojo.CustomVisBase",
        "mstrmojo.models.template.DataInterface",
        "mstrmojo.vi.models.editors.CustomVisEditorModel"
    );

	
	var $VISUTIL = mstrmojo.VisUtility ; 

	var $pluginName = "OffLineMap"  ; 
	var isApp = window.webkit ? true  : false ; 
	var libPath = ((mstrApp.getPluginsRoot && mstrApp.getPluginsRoot()) || "../plugins/") + $pluginName  ;  
	var d3Path = libPath + "/lib/d3.v4.min.js" ;  
	var leafletPath = libPath + "/lib/leaflet.js" ;  
	var d3LibFile = isApp ? "//d3js.org/d3.v4.min.js" : d3Path ; 
	var topoLibFile = isApp ? "//d3js.org/topojson.v1.min.js" : libPath + "/lib/topojson.v1.min.js"; 
	function isTrue(value) {
			return value === 'true' || value === true ? true : false;
	}; 

    mstrmojo.plugins.OffLineMap.OffLineMap = mstrmojo.declare(
        mstrmojo.CustomVisBase,
        null,
        {
            scriptClass: "mstrmojo.plugins.OffLineMap.OffLineMap",
            cssClass: "OffLinemap",
            errorMessage: "Either there is not enough data to display the visualization or the visualization configuration is incomplete.",
            errorDetails: "This visualization requires one or more attributes and one metric.",
            externalLibraries: [{url:leafletPath}, {url:d3Path} ],
            useRichTooltip: true,
            supportNEE: true,
			reuseDOMNode: false, 

			getFontStyle: function getFontStyle(styleName) {
				var fontStyle = {}; 
				var fontProps = this.getProperty(styleName) ;
				fontStyle.fontFamily = fontProps.fontFamily;
				fontStyle.fontColor = fontProps.fontColor ; 
				fontStyle.fontSize = fontProps.fontSize ; 
                fontStyle.fontStyle = isTrue(fontProps.fontItalic) ? 'italic' : 'normal';
                fontStyle.fontWeight = isTrue(fontProps.fontWeight) ? 'bold' : 'normal';
                fontStyle.textDecoration = "";
                if (isTrue(fontProps.fontUnderline)) {
                    fontStyle.textDecoration += ' underline';
                }

                if (isTrue(fontProps.fontLineThrough)) {
                    fontStyle.textDecoration += ' line-through';
                }

                if (fontStyle.textDecoration === "") {
                    fontStyle.textDecoration = "none";
                }                
				return fontStyle ; 
			} , 

            plot:function(){

            this.addUseAsFilterMenuItem();
            this.addThresholdMenuItem();  // Threshold 
            var DIModel =  new mstrmojo.models.template.DataInterface(this.model.data) ; 
			var rawData , treeData; 
			var data= [];   
			var bMin = 5 , bMax = 30 ; 
			var centerLatLng = [36.0 , 128.0] ;
			var lineType = mstrmojo.vi.models.editors.CustomVisEditorModel.ENUM_LINE_STYLE ; 	
			this.setDefaultPropertyValues (  
			 {	 
                fillColor : {fillColor:"#1F77B4" , fillAlpha : 65} , 
                displaymode : "bubble"  ,
                bubblemin : 5 , 
                bubblemax : 30 , 
                geoJsonFile : "Geo_Sido.json" , 
				fitRegion : "Data" , 
				labelfont: { fontSize: '10pt', fontFamily: 'Malgun Gothic',  fontWeight : 'false' ,  fontColor: '#202020' }   ,
				dataLabel : {N:'false', V:'false' }  , 
				hideCollision : 'true' , 
				zoomLevel : 8 , 
				centerPosition : {lat : centerLatLng[0] , lng : centerLatLng[1]}  , 
				borderColor : {lineColor: "#ddd" , lineStyle : lineType.THIN }  , 
				
				// modify mapurl (this version is not image map)
				//mapurl : "http://localhost:8080/Map/Tiles/" // Default 
				mapurl : "" // Default 
				 
				 
			 });

            var fillColor = this.getProperty("fillColor") ;//fillColor , fillAlpha  
            var displaymode =this.getProperty("displaymode") ; 
            var bubblemin    =this.getProperty("bubblemin") ;  
            var bubblemax   =this.getProperty("bubblemax") ; 
            var geoJsonFile = this.getProperty("geoJsonFile") ;  
			var fitRegion = this.getProperty("fitRegion") ; 
			var dataLabel = this.getProperty("dataLabel") ; 
			var fontLabel = this.getFontStyle("labelfont");  
			var zoomLevel = this.getProperty("zoomLevel") ;
			var mapurl = this.getProperty("mapurl") ;
			var hideCollision = isTrue (this.getProperty("hideCollision")) ; 
			var centerPosition = this.getProperty("centerPosition") ; 
			var borderColor =  this.getProperty("borderColor").lineColor ; 
			var borderStyle = {} ;
			switch (+this.getProperty("borderColor").lineStyle) {
				case lineType.NONE : 				
					borderStyle = {width : 0 ,dasharray  : 0  };  break ;				
				case lineType.THIN : 
					borderStyle = {width : 1 ,dasharray  : 0  };  break ;				
				case lineType.DASHED : 
					borderStyle = {width : 1 ,dasharray  : 3  };  break ;				
				case lineType.DOTTED : 
					borderStyle = {width : 1 ,dasharray  : 1  };  break ;				
				case lineType.THICK : 
					borderStyle = {width : 2 ,dasharray  : 0  };  break ;				
			}
			if ( fitRegion !== "Data" ) {
				centerLatLng = [centerPosition.lat , centerPosition.lng] ;
			}
		   
			// 버블 최소 최대 사이즈 프로퍼티 지정 

			Number.isInteger = Number.isInteger || function(value) {
				return typeof value === "number" &&
					   isFinite(value) &&
					   Math.floor(value) === value;
			};
		
			bMin =   Number.isInteger(parseInt(bubblemin)) ? parseInt(bubblemin) : bMin ; 
			bMax =   Number.isInteger(parseInt(bubblemax)) ? parseInt(bubblemax) : bMax ;  
			if (displaymode !== "bubble") { 
				bMin = 10 ;  bMax = 10  ;
			}
			//bMin =    parseInt(bubblemin) ; 
			//bMax =    parseInt(bubblemax) ; 
    	    var me = this ; 
            var divWidth = me.width , divHeight = me.height ; 
            var marginWidth = 12 , marginHeight = 12 ; 
            var svgWidth = me.width -marginWidth , svgHeight = me.height -marginHeight; 
        	var svgId = "svg" + this.k ; 
			var divId = "div" + this.k ;


			d3.select(this.domNode).select('div').remove(); 
			var container  = d3.select(this.domNode).select("div");
			//var contSvg = container.select(svgId) ;
			// 맵 위젯 설정 
			if (container.empty()) {
                container = d3.select (this.domNode).append("div").attr("id","div"+this.k) ; 
				container.style("width", divWidth +"px" ).style("height",divHeight +"px" ).style("position" , "relative")
				.attr("class" , "leaflet-container leaflet-touch leaflet-fade-anim leaflet-grab leaflet-touch-drag leaflet-touch-zoom"); 
				container.append("div").attr("class", "leaflet-bottom leaflet-left"); 
			 }			
			 var mymap = L.map(divId , {attributionControl : false } ).setView(centerLatLng, zoomLevel );  
			
			// remove (this map is not image map)
			/* 	 mymap.setMaxBounds ([
				 [31.8, 122.03 ], 
				 [39.43 , 132.132]
			 ]) */
			 //Bounds -- titleLayer for Korea  122.505275323983,32.4064048439238,131.460186479752,39.2081754029502 
			// 맵 속성 설정
			// var maptileurl = mapurl + "{z}/{x}/{y}.png";  
			// Naver Test //https://map.pstatic.net/nrb/styles/basic/1625727569/15/27950/12697.png?mt=bg.ol.ts.lko
			//maptileurl = "https://map.pstatic.net/nrb/styles/basic/1625727569/" + "{z}/{x}/{y}.png?mt=bg.ol.ts.lko" ; 
			// maptileurl = "https://xdworld.vworld.kr/2d/Base/service/" + "{z}/{x}/{y}.png" ; 
			//https://xdworld.vworld.kr/2d/Base/service/7/111/48.png
/* 			var tileLayer = L.tileLayer(
				 // 'http://localhost:8080/Map/Tiles/{z}/{x}/{y}.png', 
				 maptileurl , 
				{
				maxZoom: 20, minZoom:2, 
				errorTileUrl : "http://localhost:8080/Map/Tiles/errorTile.png" , 
				attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
					'<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' , 
				updateWhenIdle : false ,  
				renderer : L.svg({padding:0.0 , tolerance:0}) , 
				opacity : 1 , 
				id: 'OpenStreetMap'
			}).addTo(mymap)
			;   */
			






			rawData =   this.dataInterface.getRawData(mstrmojo.models.template.DataInterface.ENUM_RAW_DATA_FORMAT.ROWS_ADV,
                                    { hasTitleName: true  , hasSelection:true , hasThreshold: true}); 
            var headerCnt = rawData[0].headers.length  ; 
            var metricCnt = rawData[0].values.length  ; 


			// Get Lat and Long attribute 
		//	var nameAttribute = this.zonesModel.getDropZoneObjectsByIndex(0)[0] && this.zonesModel.getDropZoneObjectsByIndex(0)[0].id
		//	var latAttribute = this.zonesModel.getDropZoneObjectsByIndex(1)[0] && this.zonesModel.getDropZoneObjectsByIndex(1)[0].id ; // Get Latitude 
		//	var longAttribute = this.zonesModel.getDropZoneObjectsByIndex(2)[0] && this.zonesModel.getDropZoneObjectsByIndex(2)[0].id ; // Get Longitude 

			
            // MSTR 데이터를 정제하여 지정
            rawData.forEach(function(c) {
	        	var dHeaders = c.headers , dMetrics = c.values ;         	
	        	var tmpData = {} ; 
				// process Header Data . 
				if ( displaymode != "region" ) { // Bubble and points 
					tmpData.lat = dHeaders[headerCnt-2].name ; 
					tmpData.lon = dHeaders[headerCnt-1].name  ;
					tmpData.LatLng =  new L.LatLng(tmpData.lat , tmpData.lon) ;
				}        	
	        	tmpData.node = [] ;
	        	for (i=0 ; i<headerCnt  ; i++)
	        	{
	        		tmpData.node.push ({nodeName:dHeaders[i].tname , nodeValue:dHeaders[i].name , attributeSelector : dHeaders[i].attributeSelector}) ;
	        	}
	        	tmpData.colorInfo = c.colorInfo ; 
				// Process Metric Data 
				tmpData.metrics= [] ; 
	        	for (i=0 ; i<metricCnt ; i++)
	        	{
	        		tmpData.metrics.push ({metricName:dMetrics[i].name , metricValue:dMetrics[i].v , metricRValue:dMetrics[i].rv , 
	        			metricColor : (dMetrics[i].threshold) ? dMetrics[i].threshold.fillColor : undefined }) ;
				}

	        	data.push (tmpData) ;
		        });

            var selectedItem =""; 

            var ValueExtent = d3.extent( rawData.map( function(d) {  return d.values[0].rv} ) ) ; 
            var radius = d3.scaleLinear().domain(ValueExtent).range([bMin,bMax]);
			var labelData = [] ; 
		 
			// PDF Export finished event 			
			function renderEvent(msgcall) {
				console.log("called from " + msgcall + " , and tile is still loading ? " + tileLayer.isLoading() ) ; 
				
				me.raiseEvent({
				name: 'renderFinished',
				id: me.k
					}) ;
				}
			// PDF Export finished 
/* 			tileLayer.on("load", function(){
				renderEvent("tile loaded event");
				}) ;	 */

			L.DomEvent.on( mymap, "click", function(e){
					L.DomEvent.stopPropagation(e);
			});

	// 메트릭 색상 찾기. 메트릭 순서 대로 임계값이 있는 데이터를 리턴. 다른 메트릭으로 지정하려면 앞 순서의 메트릭 임계 값을 시각화 에서 지울것 
		var getTC = function getTC (tcData) {
			// get first Threshold color Info. 
				for ( var ti=0 ; ti<tcData.metrics.length ; ti ++ )
				{
					if (tcData.metrics[ti].metricColor && tcData.metrics[ti].metricColor !="" ){
						return tcData.metrics[ti].metricColor ; 
					}
				}
			return fillColor.fillColor  ;
		} ; 
		// Geo Json Map 
		if (displaymode === "region") {
				drawGeoJsonMap() ; 
		}  // end of Region 
		else if (displaymode !== "region") {
				drawMarkerMap () ;
				updateMap () ;  
			} // end of marker  visualization 
		// Redraw Label Data 
		mymap.on("moveend",updateMap) ;
		mymap.on("moveend",saveMapProperty) ;

		function  saveMapProperty() {
			try { 			// Save Map Properties for Center and ZoomLevel ..
				me.setProperty("centerPosition", {lat:mymap.getCenter().lat , lng : mymap.getCenter().lng },{suppressData: true }) ;  
				// [ parseFloat(mymap.getCenter().lat) ,parseFloat(mymap.getCenter().lng)] 
				me.setProperty("zoomLevel",mymap.getZoom(),{suppressData: true}) ;  
				}
		catch (e) {
			console.log("Many interaction could cause invalid state error") ;
		}
		}
		function updateMap() {				
				addLabelText(labelData) ; 
// 				saveMapProperty () ; 
		}

		function drawMarkerMap() {
				// Bubble Map 
				for (i=0 ; i < data.length ; i++ ) {
					var PopupText = "" ; 
					var Mradius =  radius(data[i].metrics[0].metricRValue)   ; 
					var MetricColor = "" 
					MetricColor = getTC(data[i]) ; 
					// 표시 모드가 버블인 경우 지름을 메트릭 값으로 지정
					// if (displaymode === "bubble") { 
					// 	Mradius  = radius(data[i].metrics[0].metricRValue)  
					// } 
					// 팝업에 표시할 데이터 지정
					for (hi =0 ; hi < data[i].node.length ; hi ++ ) {
							PopupText += "<b>" + data[i].node[hi].nodeName + ": " +data[i].node[hi].nodeValue + "</b><br />" ;  
						}
					for (mi =0 ; mi < data[i].metrics.length ; mi ++ ) {
							PopupText += data[i].metrics[mi].metricName + " : " +  data[i].metrics[mi].metricValue  + "<br />" ;  
						}
					// 지도에 마커 표시 
					// .style("border" ,borderStyle); 
					var circleMarker =L.circleMarker([data[i].lat, data[i].lon],  
							{
							radius: Mradius , 
							// color: '#ddd',
							color : borderColor , 
							stroke : true , 
							dashArray :borderStyle.dasharray ,
							weight : borderStyle.width , 
							fillColor:MetricColor ,
							fillOpacity:  parseInt(fillColor.fillAlpha) / 100   , 
							className : "nodecircles"
							}
							).addTo(mymap)
						.bindPopup(PopupText)
						// .bindTooltip(data[i].node[0].nodeValue , {permanent:true  , className : svgId+"label",offset:[0,0],opacity:1 })
						;				   					
				}

				// 마커  선택 이벤트 추가 -> 선택시 다른 시각화 업데이트  
				var nodecircles = container.selectAll(".nodecircles").data(data); 
				nodecircles.on("click", function(d,i){ 
					selectedItem  = d.node[0].attributeSelector; 
					me.applySelection(d.node[0].attributeSelector); //for web
				} );

				// 선택 된것 지우기 이벤트 
				container.on("click" , function(d,i){ 
					// console.log("container click") ;
					// d3.event.stopPropagation(); // ? 
							// if (!event.target.classList.contains('nodecircles')&& selectedItem  !="") {
								if (event.srcElement.classList && !event.srcElement.classList.contains('nodecircles')&& selectedItem  !="") {	// for ie10 
								if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.selectionDataJSONString) {  // mobile
									var d = {}
									d.messageType = "deselection";
									window.webkit.messageHandlers.selectionDataJSONString.postMessage(d);
								} else {
									me.clearSelections();
									me.endSelections();
								}
								selectedItem  = ""; 
							} else {
								return true;
							}
				}); 
				// Center or not .. 
				if ( fitRegion === "Data") {
					var myBounds =	new  L.LatLngBounds (data.map (function(e) { return [+e.lat,+e.lon]})) ; 
					mymap.fitBounds (myBounds) ;
				}
/* 				var textLabelData = data.filter(function(d,i,origin){
				return  ( mymap.getBounds().contains(d.LatLng ));  
				})
 */				labelData =  data   ; 
		}
		function drawGeoJsonMap() {
				var dataMap= []  ; 
				for ( i=0 ; i< headerCnt ; i++) {
					dataMap[i] =  d3.map(data, function (d) { return d.node[i].nodeValue}) ;  
				}
				var geoJsonFilePath = libPath + "/lib/" +  geoJsonFile ;  
				d3.queue().defer(d3.json , geoJsonFilePath)
				.await(makeGeoLayer); 
				var geoJson ; 
				function geoStyle (feature) {
					var mcolor = fillColor.fillColor ; 
					for (i=0 ; i<dataMap.length ; i++) {
						for (j=0 ; j<propertiesNames.length ; j++){ 
							var featureProperty= feature.properties[propertiesNames[j]] ;
							if (dataMap[i].get(featureProperty)) {
								mcolor = getTC( dataMap[i].get(featureProperty)) ;
								break; 
							}
						}
					} 
					return  { color : borderColor  , "weight" : 1 , "opacity" : 0.65 , "fillColor" : mcolor , "fillOpacity" : parseInt(fillColor.fillAlpha) / 100  
						, dashArray : borderStyle.dasharray  , weight :  borderStyle.width }  ; 
				}

				function regionClick (e) {
					var layer = e.target; 
					var feature = layer.feature ; 
					if (feature.data.node[0]) {
						if ( selectedItem !== feature.data.node[0].attributeSelector ) {
							selectedItem  = feature.data.node[0].attributeSelector; 
							me.applySelection(selectedItem); //for web
						}
						else {  // Delete Selected Area
							selectedItem = "" ; 
							me.clearSelections();
							me.endSelections();		                		              
						}
					}
				}

				function resetHighlight(e) {
				geoJson.resetStyle(e.target);
				}

				function highlightFeature(e) {
					var layer = e.target;
					layer.setStyle({				        
						fillOpacity:0.8  
					});
				}

				function onEachFeature(feature , layer)  { 					
					for (i=0 ; i<dataMap.length ; i++) {
						for (j=0 ; j<propertiesNames.length ; j++){ 
								var featureProperty= feature.properties[propertiesNames[j]] ;
								if (dataMap[i].get(featureProperty)) {
									feature.data = dataMap[i].get(featureProperty); 
									break  ; 
								}
						}
					}		
					layer.on({mouseup:regionClick , mouseover:highlightFeature , mouseout :  resetHighlight}); 
				}

				function setToolTip (layer) {
					//console.log (layer); 
					var PopupText = "" ; 
					if ( layer.feature.data  ) { 

					// 팝업에 표시할 데이터 지정
					for (hi =0 ; hi < layer.feature.data.node.length ; hi ++ ) {
							PopupText += "<b>" + layer.feature.data.node[hi].nodeName + ": " + layer.feature.data.node[hi].nodeValue + "</b><br />" ;  
						}
					for (mi =0 ; mi < layer.feature.data.metrics.length ; mi ++ ) {
							PopupText += layer.feature.data.metrics[mi].metricName + " : " +  layer.feature.data.metrics[mi].metricValue  + "<br />" ;  
						}
						return PopupText ; //   layer.feature.properties.KOR_NM ; 
					}
					else 
						{return "" } ; 
				}

				var propertiesNames = [] ;				
				
				function makeGeoLayer (error , loadJsonFeature) { 
					var geoJsonFeature = [] ;  

					for ( var key in loadJsonFeature.features[0].properties) 
					{
						propertiesNames.push(key) ;
					}

					for (l = 0 ; l <   loadJsonFeature.features.length ; l ++ ) {
						for (i=0 ; i<dataMap.length ; i++) {
							for (j=0 ; j<propertiesNames.length ; j++){
								if (dataMap[i].get(loadJsonFeature.features[l].properties[propertiesNames[j]])) { 
									geoJsonFeature.push (loadJsonFeature.features[l]) ; 
									geoJsonFeature.data = dataMap[i].get(loadJsonFeature.features[l].properties[propertiesNames[j]]); 
								} 					
							}
							}
					}

					geoJson  = L.geoJson ( geoJsonFeature , 
						{ 
							style : geoStyle  , 
							className : "geoRegion"  , 
							onEachFeature : onEachFeature  
						}).addTo(mymap) ; 
					geoJson.bindTooltip(setToolTip)  ;  
					// Label Data
					var featureLabel = [] 
					geoJson.getLayers().forEach(function(d,i){
						d.feature.data.LatLng = d.getBounds().getCenter()  ; 
						featureLabel.push(d.feature.data ) ;
					})
					labelData =  featureLabel  ;
					// fit to current features ;  
					if (fitRegion === "Data") {
						mymap.fitBounds(geoJson.getBounds()) ; 
					} 
					updateMap () ; 
				}
		}

		function addLabelText (targetLabelData ) {

			if (dataLabel.N != "true" &&  dataLabel.V != "true" ) {
				return ; // don't draw any text. 
			}
			// Add d3 Text test 
			var mapsvg = d3.select ("#" +divId).select("svg") ;						
			var mapG = mapsvg.select("#label"+divId ) ;
			if (mapG.empty()) {
				mapG = mapsvg.append("g").attr("id","label"+divId ) ; 
			}
			textLabelData = targetLabelData ; 
/* 			textLabelData = targetLabelData.filter(function(d,i,origin){
				return  ( mymap.getBounds().contains(d.LatLng));  
			}) */
 
			// Sort by Distance 
/* 			var centerLatLng = mymap.getCenter() ;
			textLabelData.sort(function (a,b) {
				var da = mymap.distance ( a.LatLng ,centerLatLng )	 ;
				var db = mymap.distance ( b.LatLng ,centerLatLng )	 ;
				return  db - da  ; 
			}) ; 
 */
			mapG.selectAll("text").remove() ; 
			var textLabel = mapG.selectAll("text")
				.data(textLabelData) 
				.enter()
				.append("text")
				.style("font-size", fontLabel.fontSize )
				.style("font-family" , fontLabel.fontFamily) 
				.attr("fill", fontLabel.fontColor)
				.attr("visibility" , "hidden")
				.attr("id", function (d,i){
					return "label_" + i ; 
				})
				// .style("display","none")			
				; 
			var valueMargin =0 ; 
			if (dataLabel.N == "true" ) {
				textLabel.append("tspan")
				.style("text-anchor","middle")
					.attr("y" , 0 ).attr("x",0)
					.text(function(d) {
						 return 	d.node[0].nodeValue ;  
						 }) ; 		
				valueMargin = parseInt(fontLabel.fontSize) + 4  ;
			}
			if (dataLabel.V == "true" ) {
				textLabel.append("tspan")
				.style("text-anchor","middle")
						 .attr("y" , valueMargin).attr("x",0)
						 .text(function(d) {
							  return 	d.metrics[0].metricValue  ;  
							  }) ; 
			}
			textLabel.attr ("transform" , function (d) {
				return "translate("  + 
					mymap.latLngToLayerPoint(d.LatLng).x  + "," + 
					(mymap.latLngToLayerPoint(d.LatLng).y + valueMargin + radius(d.metrics[0].metricRValue))  
				+")"
			}) ; 

			if (hideCollision) {
				var labelNodes = textLabel.nodes() ;
				var labelLength = labelNodes.length ;

				var labelRect = {} , labelMatrix  = {} , labelDisplayList = {} , labelID , matrixSize  = 10 ; 
				// Create label Rect Matrix 
				for (var l = 0 ; l < labelLength; l ++ ) {
					var o =  labelNodes[l].getBoundingClientRect() ;
					labelID = "label_"  + l ; 
					labelRect[labelID] = o ; 
					// Create Label Matrix 
					CreateLabelMatrix(labelID, o ) ; 
					labelDisplayList[labelID]  = true ; 
				}			

				// Detect Collision
 				for (var l = 0 ; l < labelLength; l ++ ) {
					labelID = "label_"  + l ; 
					var o =  labelRect[labelID] ;
					if (!labelDisplayList.hasOwnProperty(labelID)) { // 지워진 노드는 비교하지 않음
						continue  ; 
					}
					var g = cellStartEnd(o) ; 
					for (i=g.rowStart; i<g.rowEnd ; i++ ) {
						for (j =g.colStart ; j< g.colEnd ; j++) {
							if (labelMatrix[i] && labelMatrix[i][j]) {
								coNodes = labelMatrix[i][j] ; 
								for (node in coNodes) {
									if (labelID == node) { continue ; } 
									if (coNodes.hasOwnProperty(node) && labelDisplayList.hasOwnProperty(node) ) { // 중첩 노드인 경우 디스플레이 리스트에서 삭제 ,  지워진 노드와는 비교하지 않음
										// console.log(node ) ; 
										// delete Node 
										delete labelDisplayList[node] ;  
										coNodes[node]  = false ; 
									}
								}
							}
						}
					}
				}
				console.log(labelDisplayList) ;

				var Nodeselector = ""  , labelDisplayListLength = 0 ; 
				for (node in labelDisplayList) {
					Nodeselector += (labelDisplayListLength == 0 ? "" : "," ) +  "#" + node ;
					labelDisplayListLength += 1  ;
				}
				mapG.selectAll(Nodeselector ).attr("visibility","visible").attr("class","OffLineMapLabel")

				function CreateLabelMatrix(lableID, o) {
					var g = cellStartEnd(o) ; 
					for (i=g.rowStart; i<g.rowEnd ; i++ ) {
						for (j =g.colStart ; j< g.colEnd ; j++) {
							if (!labelMatrix[i] ){
								labelMatrix[i] = {} ; 
							}
							if (!labelMatrix[i][j]) {
								labelMatrix[i][j] = {} ; 
							}
								labelMatrix[i][j][labelID] = true ; 
						}
					}
				}


				function cellStartEnd (n) {
					var g= {}  ;
					g.rowStart =  Math.floor(n.top / matrixSize  ) ;
					g.rowEnd =  Math.floor(n.bottom  / matrixSize  ) ;
					g.colStart =  Math.floor(n.left  / matrixSize  ) ;
					g.colEnd  =  Math.floor(n.right   / matrixSize  ) ;
					return g ; 
				}


/* 				// Show and Remove Way 
				while(labelLength--) {
					// for (var j=labelLength -1 ; j>=0 ;j--) {
					for (var j=0 ; j<labelLength ;j++) {						
						if ( checkCollision	(labelNodes[labelLength], labelNodes[j] )) 
						{
							d3.select(labelNodes[labelLength]).remove() ;
							   // delete text node ; 
							labelNodes.splice(labelLength,1);
							 break ; 
						}; 							
					}				 
				} */
				
				// Show one by one 				
/* 				labelNodes[0].style.display = "block" ;
				var visibleNodes = [] , nodesVisible = true ; 
				visibleNodes.push(labelNodes[0]) ; 				
				for ( var j = 1 ; j < labelLength ; j ++ ) {
					labelNodes[j].style.display = "block" ;
					for ( k = 0 ; k < visibleNodes.length ; k ++ )  {
						if (checkCollision(labelNodes[j] , visibleNodes[k])) { // find collision
							nodesVisible = false ; 
							labelNodes[j].style.display = "none" ;
							break ; 
						}
					} 					
					visibleNodes.push(labelNodes[j]) ;					
				}
				 */
			}  // end of Label collision ..  
			else {
				textLabel.attr("visibility","visible").attr("class","OffLineMapLabel");
			}

				function checkCollision(a,b) {
					var isCollision = false  ; 
					var o = a.getBoundingClientRect() ; 
					var t = b.getBoundingClientRect() ;  
					// collision test ; 
					//	if (o.x + o.width  >= t.x   && o.x <= t.x+t.width  && o.y + o.height >= t.y  && o.y <= t.y + t.height ) {
					if (o.right  >= t.left  && o.left <= t.right   && o.bottom >= t.top  && o.top <= t.bottom ) {
						isCollision =true ;  
					} 
					return isCollision ;  
				} 
	} // End of drawLabel .. 


    	// 맵 위젯의 클릭 이벤트가 MSTR VI 로 전달 되지 않게 막기 
		 L.DomEvent.disableClickPropagation ( me.domNode ) ; 

}})}());
//@ sourceURL=OffLineMap.js

