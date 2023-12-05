(function () { 
    if (!mstrmojo.plugins.OffLineMap2) {
        mstrmojo.plugins.OffLineMap2 = {};
    }

    mstrmojo.requiresCls(
        "mstrmojo.vi.models.CustomVisDropZones",
        "mstrmojo.array"
    );

    mstrmojo.plugins.OffLineMap2.OffLineMap2DropZones = mstrmojo.declare(
        mstrmojo.vi.models.CustomVisDropZones,
        null,
        {
            scriptClass: "mstrmojo.plugins.OffLineMap2.OffLineMap2DropZones",
            cssClass: "offlinemap2dropzones",
            getCustomDropZones: function getCustomDropZones() {
                return [{ 
                    name: mstrmojo.desc(518, 'Attribute'), 
                    title:mstrmojo.desc(13828, 'Drag attributes here'), 
                    allowObjectType:1
                    }, { 
                    //name: "displyLabel", 
                    name: mstrmojo.desc("OffLineMap.17", "Display Label"),
                    title:mstrmojo.desc(13828, 'Drag attributes here'), 
                    allowObjectType:1
                    },{ 
                    name: mstrmojo.desc(7696, 'Latitude'), 
                    title: mstrmojo.desc(13828, 'Drag attributes here'), 
                    maxCapacity: 1,
                    allowObjectType:1
                    }, { 
                    name: mstrmojo.desc(7697, 'Longitude'), 
                    title:mstrmojo.desc(13828, 'Drag attributes here'), 
                    maxCapacity: 1,
                    allowObjectType:1
                    }, { 
                    name: mstrmojo.desc(517, 'Metric'), 
                    title: mstrmojo.desc(13827, 'Drag metric here'), 
                    allowObjectType:2
                    }];
            },
            shouldAllowObjectsInDropZone: function shouldAllowObjectsInDropZone(zone, dragObjects, idx, edge, context) {
            },
            getActionsForObjectsDropped: function getActionsForObjectsDropped(zone, droppedObjects, idx, replaceObject, extras) {
            },
            getActionsForObjectsRemoved: function getActionsForObjectsRemoved(zone, objects) { 
            },
            getDropZoneContextMenuItems: function getDropZoneContextMenuItems(cfg, zone, object, el) {
            }
        }
    )
}());//@ sourceURL=OffLineMap2DropZones.js