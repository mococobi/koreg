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
                return [{
                    name: mstrmojo.desc("OffLineMap.1", "Configuration").replace(/^\[+|\]+$/g, ""), 
                    value: [{
                        style: $WT.EDITORGROUP,
                        items: [{
                            style: $WT.TWOCOLUMN,
                            items: [{
                                style: $WT.LABEL,
                                name: "text", 
                                width: "35%",
                                labelText:  mstrmojo.desc("OffLineMap.2", "Color").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.FILLGROUP,
                                width: "65%",
                                propertyName: "fillColor",
                                items: [{
                                    childName: "fillAlpha",
                                    disabled: false 
                                }]
                            }]
                        }, { // end of color  
                            style: $WT.TWOCOLUMN,
                            items: [{
                                style: $WT.LABEL,
                                name: "text",
                                width: "25%",
                                labelText: mstrmojo.desc("OffLineMap.3", "Border").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.LINEGROUP,
                                width: "75%",
                                propertyName: "borderColor"
                            }]
                        }, { // border Color 
                            style: $WT.TWOCOLUMN,
                            items: [{
                                style: $WT.LABEL,
                                name: "text", 
                                width: "35%",
                                labelText: mstrmojo.desc("OffLineMap.4", "Display").replace(/^\[+|\]+$/g, "")  
                            }, {
                                style: $WT.PULLDOWN,
                                width: "65%",
                                propertyName: "displaymode",
                                items: [
                                    {name: mstrmojo.desc("OffLineMap.5", "Bubble").replace(/^\[+|\]+$/g, ""), value: "bubble"}, 
                                    {name: mstrmojo.desc("OffLineMap.6", "Circle").replace(/^\[+|\]+$/g, ""), value: "circle"}, 
                                    {name: mstrmojo.desc("OffLineMap.7", "Region").replace(/^\[+|\]+$/g, ""), value: "region"} 
                                ]   
                            }]
                        }, { // end of displaymode 
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
                                propertyName: "bubblemin",
                                disabled: this.getHost().getProperty("displaymode") === "region"
                            }]      
                        }, {
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
                                propertyName: "bubblemax", 
                                disabled: this.getHost().getProperty("displaymode") === "region"
                            }]      
                        }, {
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
                                disabled: this.getHost().getProperty("displaymode") !== "region", 
                                items: [
                                    {name: mstrmojo.desc("OffLineMap.11", "World").replace(/^\[+|\]+$/g, ""), value: "countries.geo.json"}, 
                                    {name: mstrmojo.desc("OffLineMap.12", "State").replace(/^\[+|\]+$/g, ""), value: "Geo_Sido.json"},
                                    {name: mstrmojo.desc("OffLineMap.13", "City").replace(/^\[+|\]+$/g, ""), value: "Geo_Sigungu.json"}, 
                                    {name: mstrmojo.desc("OffLineMap.22", "Town").replace(/^\[+|\]+$/g, ""), value: "Geo_BubjeongDong.json"}
                                ]  
                            }]      
                        }, {
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
                        }
                            /*							
                            {
                                style: $WT.LABEL,
                                name: "text",
                                width: "50%",
                                labelText: "MapTile:" 
                            }, 
                            {
                                style: $WT.TEXTBOX,
                                propertyName: "mapurl",
                                value: ""                                
                            },                           
                            */
                        ]
                    }, {
                        style: $WT.EDITORGROUP,
                        items: [{
                            style: $WT.LABEL,
                            name: "text",
                            width: "100%",
                            labelText: mstrmojo.desc("OffLineMap.17", "Data Label").replace(/^\[+|\]+$/g, "")  
                        }, {
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
                        }, {
                            style: $WT.CHECKBOXANDLABEL,
                            propertyName: "hideCollision",
                            labelText: mstrmojo.desc("OffLineMap.20", "Hide Overlap Label").replace(/^\[+|\]+$/g, "")  
	                    }]
                    }, { 
                        style: $WT.EDITORGROUP,
                        items: [{
                            style: $WT.LABEL,
                            name: "text",
                            width: "100%",
                            labelText: mstrmojo.desc("OffLineMap.21", "Lable Format").replace(/^\[+|\]+$/g, "")  
                        }, {
                            style: $WT.CHARACTERGROUP,
                            propertyName: "labelfont",
                            items: [{
                                childName: "fontSize"
                            }],
                            disabled: this.getHost().getProperty("dataLabel") ? (this.getHost().getProperty("dataLabel").N === "false" && this.getHost().getProperty("dataLabel").V === "false") : false 
                            // disabled: (this.getHost().getProperty("dataLabel")) 
                        }]
                    }]
                }]; // end of configuration 
            }
        }
    )
}());
//@ sourceURL=OffLineMap2EditorModel.js