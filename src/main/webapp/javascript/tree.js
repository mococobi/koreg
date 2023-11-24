/**
 * 
 */

function getSibling(me, parent, parChildAttrName) {
    var sibling;
    var childAttrName = (parChildAttrName ? parChildAttrName : "child");
    
    for (var i = 0; parent && parent[childAttrName] && i < parent[childAttrName].length; i++) {
        if (parent[childAttrName][i] == me) {
            // 마지막 노드 이전일 경우만 sibling이 존재함
            if (i < parent[childAttrName].length - 1) { sibling = parent[childAttrName][i + 1]; }
            break;
        }   
    }
    
    return sibling;
}

function navigateDepthFirst(data, handler, parChildAttrName) {
    var childAttrName = (parChildAttrName ? parChildAttrName : "child");
    var done = [];
    var stack = [];
    var ref = data;
    
    while (ref) {
        if ($.inArray(ref, done) == -1) { handler(ref, stack); }
        
        if ($.inArray(ref, done) == -1 && ref[childAttrName] && ref[childAttrName].length > 0) {
            stack.push(ref);
            done.push(ref);
            ref = ref[childAttrName][0];
        } else {
            // var parent = stack.peek();
            var parent = stack[stack.length - 1];
            var sibling = getSibling(ref, parent);
            
            if (sibling) {
                ref = sibling;
            } else {
                ref = stack.pop();
            }
        }
    }
}    

function searchDepthFirst(data, handler, parChildAttrName) {
    var childAttrName = (parChildAttrName ? parChildAttrName : "child");
    var done = [];
    var stack = [];
    var ref = data;
    while (ref) {
        if (handler(ref)) { return ref; }   
    
        if ($.inArray(ref, done) == -1 && ref[childAttrName] && ref[childAttrName].length > 0) {
            stack.push(ref);
            done.push(ref);
            ref = ref[childAttrName][0];
        } else {
            // var parent = stack.peek();
        	var parent = stack[stack.length - 1];
            var sibling = getSibling(ref, parent, childAttrName);
            
            if (sibling) {
                ref = sibling;
            } else {
                ref = stack.pop();
            }
        }
    }
    return;
}

function arrayToTree(data, _option, rootkey) {
	var option = $.extend({parentKey:"PARENT_ID", key:"ID", parent:"parent", child:"child"}, _option);
	
	var clone = $.extend(true, [], data); // deep cloning
	var tree = undefined;
	var keyMap = {};
	
	while (clone.length > 0) {
		var beforeCount = clone.length;
		
		$.each(clone, function(index, row) {
			if ( // root노드가 아니면서 parentKey가 비어있거나, key값이 비어있을 경우
				(!row[option.parentKey] && row[option.key] != rootkey) || !row[option.key]
			) {
				return true; // continue;
			}
			
			var rowKey = row[option.key];
			var parentKey = row[option.parentKey];
			
			if (rowKey == rootkey || keyMap[parentKey]) {
				if (rowKey == rootkey) {
					tree = row;
				} else {
					var parent = keyMap[parentKey];
					
					if (!parent[option.child]) {
						parent[option.child] = [];
					}
					parent[option.child].push(row);
					row[option.parent] = parent;
				}
				
				keyMap[row[option.key]] = row;
				clone.splice(index, 1);
				
				return false;
			}  
		});
		
		if (beforeCount == clone.length) {
			alert("infinite loop. please check data.");
			break;
		}
	}
	
	return tree;
}