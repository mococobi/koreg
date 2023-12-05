(function () { 
    if (!mstrmojo.plugins.OffLineMap2) {
        mstrmojo.plugins.OffLineMap2 = {};
    }

    mstrmojo.requiresCls(
        "mstrmojo.vi.models.editors.CustomVisEditorModel",
        "mstrmojo.plugins.OffLineMap2.OffLineMap2",         
        "mstrmojo.array"
    );
    var $WT = mstrmojo.vi.models.editors.CustomVisEditorModel.WIDGET_TYPE ; 
    mstrmojo.plugins.OffLineMap2.OffLineMap2EditorModel = mstrmojo.declare(
        mstrmojo.vi.models.editors.CustomVisEditorModel,
        null,
        {
            scriptClass: "mstrmojo.plugins.OffLineMap2.OffLineMap2EditorModel",
            cssClass: "offlinemap2editormodel",
            getCustomProperty: function getCustomProperty() {
                return [{ // 설정
                    name: mstrmojo.desc("OffLineMap.1", "Configuration").replace(/^\[+|\]+$/g, ""), 
                    value: [{
                        style: $WT.EDITORGROUP,
                        items: [{ // 색상
                            style: $WT.TWOCOLUMN,
                            items: [{ 
                                style: $WT.LABEL,
                                name: "text", 
                                width: "35%",
                                labelText:  mstrmojo.desc("OffLineMap.2", "Color").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.FILLGROUP,
                                width: "65%",
                                propertyName: "fill"
                            }]
                        }, { // 테두리
                            style: $WT.TWOCOLUMN,
                            items: [{ 
                                style: $WT.LABEL,
                                name: "text",
                                width: "25%",
                                labelText: mstrmojo.desc("OffLineMap.3", "Border").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.LINEGROUP,
                                width: "75%",
                                propertyName: "border"
                            }]
                        }, { // 테두리 굵기
                            style: $WT.TWOCOLUMN, 
                            width: "50%", 
                            items: [{ 
                                style: $WT.LABEL,
                                name: "text",
                                width: "70%",
                                labelText: mstrmojo.desc("OffLineMap.23", "Border Size").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.TEXTBOX,
                                width: "30%",
                                propertyName: "borderSize",
                                disabled: this.getHost().getProperty("displayMode") !== "region"
                            }]      
                        }, { // 표시방식
                            style: $WT.TWOCOLUMN,
                            items: [{
                                style: $WT.LABEL,
                                name: "text", 
                                width: "35%",
                                labelText: mstrmojo.desc("OffLineMap.4", "Display").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.PULLDOWN,
                                width: "65%",
                                propertyName: "displayMode",
                                items: [
                                    {name: mstrmojo.desc("OffLineMap.5", "Bubble").replace(/^\[+|\]+$/g, ""), value: "bubble"}, 
                                    {name: mstrmojo.desc("OffLineMap.6", "Circle").replace(/^\[+|\]+$/g, ""), value: "circle"}, 
                                    {name: mstrmojo.desc("OffLineMap.7", "Region").replace(/^\[+|\]+$/g, ""), value: "region"} 
                                ]   
                            }]
                        }, { // 버블 최소 사이즈
                            style: $WT.TWOCOLUMN, 
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "70%",
                                labelText: mstrmojo.desc("OffLineMap.8", "Minimum Size").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.TEXTBOX,
                                width: "30%",
                                propertyName: "bubbleMin",
                                disabled: this.getHost().getProperty("displayMode") !== "bubble"
                            }]      
                        }, { // 버블 최대 사이즈
                            style: $WT.TWOCOLUMN,
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "70%",
                                labelText: mstrmojo.desc("OffLineMap.9", "Maximum Size").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.TEXTBOX,
                                width: "30%",
                                propertyName: "bubbleMax", 
                                disabled: this.getHost().getProperty("displayMode") !== "bubble"
                            }]      
                        }, { // 영역 지정
                            style: $WT.TWOCOLUMN,
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "50%",
                                labelText: mstrmojo.desc("OffLineMap.10", "Select Region").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.PULLDOWN,
                                width: "50%",
                                propertyName: "geoJsonFile", 
                                disabled: this.getHost().getProperty("displayMode") !== "region", 
                                items: [
                                    {name: mstrmojo.desc("OffLineMap.12", "State").replace(/^\[+|\]+$/g, ""), value: "Geo_Sido.json"},
                                    {name: mstrmojo.desc("OffLineMap.13", "City").replace(/^\[+|\]+$/g, ""), value: "Geo_Sigungu.json"}, 
                                    {name: mstrmojo.desc("OffLineMap.22", "Town1").replace(/^\[+|\]+$/g, ""), value: "Geo_BubjeongDong.json"},
                                    {name: mstrmojo.desc("OffLineMap.26", "Town2").replace(/^\[+|\]+$/g, ""), value: "Geo_HangJeongDong.json"}
                                ]  
                            }]      
                        }, { // 확대 축소
                            style: $WT.TWOCOLUMN,
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "50%",
                                labelText: mstrmojo.desc("OffLineMap.14", "Zoom").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.PULLDOWN,
                                width: "50%",
                                propertyName: "fitRegion", 
                                // disabled: this.getHost().getProperty("displaymode") !== "region", 
                                items: [
                                    {name: mstrmojo.desc("OffLineMap.15", "Static").replace(/^\[+|\]+$/g, ""), value:"All"}, 
                                    {name: mstrmojo.desc("OffLineMap.16", "Dynamic").replace(/^\[+|\]+$/g, ""), value:"Data"}  
                                ]  
                            }]      
                        }, { // 지도 URL
                            style: $WT.TWOCOLUMN,
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "50%",
                                labelText: mstrmojo.desc("OffLineMap.25", "Map Image URL").replace(/^\[+|\]+$/g, "")
                            }, {
                                style: $WT.TEXTBOX,
                                propertyName: "mapUrl",
                                value: ""                                
                            }]
                        }, { // 지도 투명도
                            style: $WT.TWOCOLUMN, 
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "70%",
                                labelText: mstrmojo.desc("OffLineMap.27", "Map Opacity").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.TEXTBOX,
                                width: "30%",
                                propertyName: "mapOpacity",
                                disabled: !this.getHost().getProperty("mapUrl")
                            }]      
                        }]
                    }, { // 선택영역
                        style: $WT.EDITORGROUP,
                        items: [{
                            style: $WT.LABEL,
                            name: "text",
                            width: "100%",
                            labelText: mstrmojo.desc("OffLineMap.24", "Selected Area").replace(/^\[+|\]+$/g, "")  
                        }, { // 선택영역 > 색상
                            style: $WT.TWOCOLUMN,
                            items: [{
                                style: $WT.LABEL,
                                name: "text", 
                                width: "35%",
                                labelText:  mstrmojo.desc("OffLineMap.2", "Color").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.FILLGROUP,
                                width: "65%",
                                propertyName: "selectionFill",
                                items: [
                                    {childName:"fillColor", disabled: this.getHost().getProperty("displayMode") !== "region"},
                                    {childName:"fillAlpha", disabled: this.getHost().getProperty("displayMode") !== "region"}
                                ]
                            }]
                        }, { // 선택영역 > 테두리
                            style: $WT.TWOCOLUMN,
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "25%",
                                labelText: mstrmojo.desc("OffLineMap.3", "Border").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.LINEGROUP,
                                width: "75%",
                                propertyName: "selectionBorder",
                                items: [
                                    {childName:"lineColor", disabled: this.getHost().getProperty("displayMode") !== "region"},
                                    {childName:"lineStyle", disabled: this.getHost().getProperty("displayMode") !== "region"}
                                ]
                            }]
                        }, { // 선택영역 > 테두리 굵기
                            style: $WT.TWOCOLUMN, 
                            width: "50%",
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "70%",
                                labelText: mstrmojo.desc("OffLineMap.23", "Border Size").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.TEXTBOX,
                                width: "30%",
                                propertyName: "selectionBorderSize",
                                disabled: this.getHost().getProperty("displayMode") !== "region"
                            }]      
                        }]
                    }, { // 데이터 레이블 
                        style: $WT.EDITORGROUP,
                        items: [{
                            style: $WT.LABEL,
                            name: "text",
                            width: "100%",
                            labelText: mstrmojo.desc("OffLineMap.17", "Data Label").replace(/^\[+|\]+$/g, "")  
                        }, { // 데이터 레이블 > 레이블
                            style: $WT.BUTTONBAR,
                            propertyName: "dataLabel",
                            items: [{
                                labelText: mstrmojo.desc("OffLineMap.18", "Label").replace(/^\[+|\]+$/g, ""),
                                propertyName: "N"
                            }, {
                                labelText: mstrmojo.desc("OffLineMap.19", "Value").replace(/^\[+|\]+$/g, ""),
                                propertyName: "V"
                            }],
                            multiSelect:true  
                        }, { // 데이터 레이블 > 겹치는 레이블을 숨김
                            style: $WT.CHECKBOXANDLABEL,
                            propertyName: "hideCollision",
                            labelText: mstrmojo.desc("OffLineMap.20", "Hide Overlap Label").replace(/^\[+|\]+$/g, "")  
	                    }]
                    }, { // 텍스트 포멧
                        style: $WT.EDITORGROUP,
                        items: [{
                            style: $WT.LABEL,
                            name: "text",
                            width: "100%",
                            labelText: mstrmojo.desc("OffLineMap.21", "Label Format").replace(/^\[+|\]+$/g, "")  
                        }, { // 텍스트 포멧 > 폰트
                            style: $WT.CHARACTERGROUP,
                            propertyName: "labelFont" /*,
                            items: [{
                                childName: "fontSize"
                            }] */,
                            disabled: this.getHost().getProperty("dataLabel") ? (this.getHost().getProperty("dataLabel").N === "false" && this.getHost().getProperty("dataLabel").V === "false") : false 
                            // disabled: (this.getHost().getProperty("dataLabel")) 
                        }]
                    }]
                }];
            }
        }
    )
}());
//@ sourceURL=OffLineMap2EditorModel.js