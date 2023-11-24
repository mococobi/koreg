/*
    WebPromptTypeUnsupported = 0;
    WebPromptTypeConstant = 1;
    WebPromptTypeElements = 2;
    WebPromptTypeExpression = 3;
    WebPromptTypeObjects = 4;
    WebPromptTypeDimty = 5; 

	결정된 UI 유형에 따른 프롬프트 랜더링 
 */
var _defaultElemRenderer = {
    "label": function($wrapper, data) {
		$wrapper.append("<span class='elem-label'>&bull;&nbsp;" + data["title"] + "</span>");
    }		
};

var __popupHandler = {
	"load": undefined,	
	"select": undefined
}

var promptRenderer = {
	"value-default": {
		"label": _defaultElemRenderer["label"],
		"body": function($wrapper, data) {
			var $input = $("<input ui-type='value-default' type='text' prompt-id='" + data["id"] + "' prompt-tp='" + data["type"] + "' value='" + (data["defaultAnswer"] ? data["defaultAnswer"] : "") + "'/>");
			$wrapper.append($input);
		},
		"selected": function($elem) {
			return [$elem.val()];
		}
	},
	"list-default": {
		"label": _defaultElemRenderer["label"],
		"body": function($wrapper, data) {
			var $select = $("<select ui-type='list-default' prompt-id='" + data["id"] + "' prompt-tp='" + data["type"] + "'></select>");
			$wrapper.append($select);
			if (data["suggestedAnswers"]) {
				var selectedId = undefined;
				
				$.each(data["suggestedAnswers"], function(i, v) {
					var elementId = v["id"];
					if (data["defaultAnswers"]) {
						$.each(data["defaultAnswers"], function(i2, v2) {
							if (elementId == v2["id"]) {
								selectedId = elementId;
								return false;
							}
						});
					}
					
					$select.append("<option value='" + v["id"] + "'>" + v["displayName"] + "</option>");
				});
				
				if (selectedId) { $select.val(selectedId); }
			}
		},
		"selected": function($elem) {
			var selected = [];
			$(" option:selected", $elem).each(function(i, v) {
				selected.push( $(v).val() );
			})
			return selected;
		}
	},
	"multiSelect-default": {
		"label": _defaultElemRenderer["label"],
		"body": function($wrapper, data) {
			var $select = $("<select ui-type='multiSelect-default' prompt-id='" + data["id"] + "' prompt-tp='" + data["type"] + "' multiple='multiple'></select>");
			$wrapper.append($select);
			if (data["suggestedAnswers"]) {
				var selectedId = undefined;
				
				$.each(data["suggestedAnswers"], function(i, v) {
					var elementId = v["id"];
					var selected = false;
					if (data["defaultAnswers"]) {
						$.each(data["defaultAnswers"], function(i2, v2) {
							if (elementId == v2["id"]) {
								selected = true;
								return false;
							}
						});
					}
					
					$select.append("<option value='" + v["id"] + "' " + (selected ? "selected='selected'" : "") + ">" + v["displayName"] + "</option>");
				});
				$select.multiselect();
			}
		},
		"selected": function($elem) {
			var selected = [];
			$(" option:selected", $elem).each(function(i, v) {
				selected.push( $(v).val() );
			})
			return selected;
		}
	},
	"date": {
		"label": _defaultElemRenderer["label"],
		"body": function($wrapper, data) {
			var $day = $("<input ui-type='date' prompt-id='" + data["id"] + "' prompt-tp='" + data["type"] + "'/>");
			$wrapper.append($day);
			$day.datebox();
		},
		"selected": function($elem) {
			return [$.datepicker.formatDate("yymmdd", $elem.datebox("getDate"))];
		}
	},
	"multiselect-yyyymm": {
		"label": _defaultElemRenderer["label"],
		"body": function($wrapper, data) {
			var $select = $("<select ui-type='list-default' prompt-id='" + data["id"] + "' prompt-tp='" + data["type"] + "' multiple='multiple'></select>");
			$wrapper.append($select);
			
			var to = 2020; from = to - 10;
			
			for (var i = to; i > from; i--) {
				$select.append("<option value='" + String(i) + "'>" + String(i) + "</option>");
			}
			
			$select.val(String(to));
			$select.multiselect();
		},
		"selected": function($elem) {
			var selected = [];
			$(" option:selected", $elem).each(function(i, v) {
				selected.push( $(v).val() );
			})
			return selected;
		}
	},
	"popup": {
		"label": _defaultElemRenderer["label"],
		"body": function($wrapper, data) {
			function loadCallback() {
				return data["suggestedAnswers"];
			}
			
			function selectCallback(selected) {
				var $elem = $("[prompt-id='" + data["id"] + "']");
				
				if (selected) {
					var ids = [];
					var names = [];
					$.each(selected, function(i, v) {
						ids.push(v.id);
						names.push(v.name);
					});
					
					$elem.val(ids.join(";"));
					$elem.next().val(names.join(","));
				} else {
					$elem.val("");
					$elem.next().val("");
				}
			}
			
			function popupOpen() {
				var popup = window.open(__contextPath + "/_custom/jsp/popup.jsp", "_popup", "width=460,height=570");
				__popupHandler.load = loadCallback;
				__popupHandler.select = selectCallback;
			}
			
			var $popup = $("<span class='popElem'><input type='hidden' ui-type='popup' prompt-id='" + data["id"] + "' prompt-tp='" + data["type"] + "'/><input readonly='readonly' type='text' value=''/><button type='button' class='popup'>선택</button></span>");
			$wrapper.append($popup);
			$("button", $popup).click(popupOpen);
		},
		"selected": function($elem) {
			var selected = [];
			
			var val = $elem.val();
			if (val) {
				selected = val.split(";");
			}
			
			return selected;
		}
	}
};