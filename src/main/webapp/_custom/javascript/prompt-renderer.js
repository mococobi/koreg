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
    'label' : function($wrapper, data) {
		let lableHtml = $('<label>', {
			text : data['title']
		});
		$wrapper.append(lableHtml);
    }		
};


var __popupHandler = {
	  'load' : undefined
	, 'select' : undefined
}

//exUiType 판단
var promptRenderer = {
	'value-default': {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			var $input = $('<input class="run-setting-box_input" ui-type="value-default" type="text" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" value="' + (data['defaultAnswer'] ? data['defaultAnswer'] : '') + '"/>');
			$wrapper.append($input);
		}
		, 'selected': function($elem) {
			return [$elem.val()];
		}
		, 'validation' : function($elem) {
			return null;
		}
	}
	, 'list-default' : {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			var $select = $('<select class="run-setting-box_select form-select" ui-type="list-default" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '"></select>');
			$wrapper.append($select);
			if (data['suggestedAnswers']) {
				var selectedId = undefined;
				
				$.each(data['suggestedAnswers'], function(i, v) {
					var elementId = v['id'];
					if (data['defaultAnswers']) {
						$.each(data['defaultAnswers'], function(i2, v2) {
							if (elementId == v2['id']) {
								selectedId = elementId;
								return false;
							}
						});
					}
					
					$select.append('<option value="' + v['id'] + '">' + v['displayName'] + '</option>');
				});
				
				if (selectedId) { $select.val(selectedId); }
			}
		}
		, 'selected': function($elem) {
			var selected = [];
			$('option:selected', $elem).each(function(i, v) {
				selected.push( $(v).val() );
			})
			return selected;
		}
		, 'validation' : function($elem) {
			return null;
		}
	}
	, 'multiSelect-default': {
		  'label': _defaultElemRenderer['label']
		, 'body': function($wrapper, data) {
			var $select = $('<select class="run-setting-box_select form-select" ui-type="multiSelect-default" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" '
			+ 'prompt-title="' + data['title'] + '" prompt-min="' + data['min'] + '" prompt-max="' + data['max'] + '" prompt-required="' + data['required'] + '" '
			+ 'multiple="multiple"></select>');
			$wrapper.append($select);
			if (data['suggestedAnswers']) {
				var selectedId = undefined;
				
				$.each(data['suggestedAnswers'], function(i, v) {
					var elementId = v['id'];
					var selected = false;
					if (data['defaultAnswers']) {
						$.each(data['defaultAnswers'], function(i2, v2) {
							if (elementId == v2['id']) {
								selected = true;
								return false;
							}
						});
					}
					$select.append('<option value="' + v['id'] + '" ' + (selected ? ' selected="selected"' : '') + '>' + v['displayName'] + '</option>');
				});
				
				$select.multiselect({
					  columns : 1
//					, search : true
					, texts : commonMultiSelectLanguage()
					, selectAll : true
					, maxHeight : 300
					, maxWidth : 800
					, maxPlaceholderOpts : 3
					, checkboxAutoFit : true
				});
			}
		}
		, 'selected': function($elem) {
			var selected = [];
			$('option:selected', $elem).each(function(i, v) {
				selected.push($(v).val());
			})
			return selected;
		}
		, 'validation' : function($elem) {
			var selected = [];
			$('option:selected', $elem).each(function(i, v) {
				selected.push($(v).val());
			})
			
			//프롬프트 길이
			let promptLength = selected.length;
			
			//필수 값 체크
			if($elem.attr('prompt-required') == 'true') {
				if(promptLength == 0) {
					return '[' + $elem.attr('prompt-title') + ']를 선택하세요';
				}
			}
			
			//최소 값 체크
			let promptMinLength = $elem.attr('prompt-min');
			if(promptMinLength) {
				if(promptLength < promptMinLength) {
					return '[' + $elem.attr('prompt-title') + ']의 최소 선택은 ' + promptMinLength + '입니다';
				}
			}
			
			//최대 값 체크
			let promptMaxLength = $elem.attr('prompt-max');
			if(promptMaxLength) {
				if(promptLength > promptMaxLength) {
					return '[' + $elem.attr('prompt-title') + ']의 최대 선택은 ' + promptMaxLength + '입니다';
				}
			}
			
			return null;
		}
	}
	, 'date': {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			var $day = $('<input class="run-setting-box_input" class="form-control" ui-type="date" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" '
			+ 'prompt-title="' + data['title'] + '" prompt-min="' + data['min'] + '" prompt-max="' + data['max'] + '" prompt-required="' + data['required'] + '" '
			+ 'prompt-promptType="' + data['promptType'] + '" prompt-promptSubType="' + data['promptSubType'] + '"'
			+ 'style="cursor:pointer;"/>');
			$wrapper.append($day);
			
			//전일자 기본
			let currentDate = new Date();
			currentDate.setDate(currentDate.getDate() - 1);
			
		    let selectDate = new AirDatepicker('input[prompt-id="'+ data['id'] +'"]', {
				  locale : commonAirDatepickerLanguage()
				, navTitles: {
			          days: '<strong>yyyy</strong> <i>MMMM</i>'
			        , months: '<strong>yyyy</strong>'
			    }
				, selectedDates : [currentDate]
//				, buttons: ['today', 'clear']
			    , autoClose : true
			    , minDate : (data['promptSubType'] == 2565 && data['min']) ? new Date(data['min']) : false
				, maxDate : (data['promptSubType'] == 2565 && data['max']) ? new Date(data['max']) : new Date()
			});
			
			//기본 값 설정
			if(data['defaultAnswer']) {
				if(data['promptSubType'] == 2565) {
					//날짜 프롬프트일 경우
					selectDate.selectDate(new Date(data['defaultAnswer']));
				} else {
					selectDate.selectDate(new Date(changeStringToDate(data['defaultAnswer'])));
				}
			}
			
			//엔터키 이벤트
			$day.on('keyup', function(e) {
				if(e.keyCode==13) {
					$('#run').trigger('click');
				}
			});
		}
		, 'selected' : function($elem) {
			let promptVal = '';
			
			if($elem.attr('prompt-promptSubType') == 2565) {
				//날짜 프롬프트일 경우
				promptVal = $elem.val();
			} else {
				if($elem.val().indexOf('-') > -1) {
					promptVal = $.datepicker.formatDate('yymmdd', new Date($elem.val()));
				} else {
					promptVal = $elem.val();
				}
			}
			
			return [promptVal];
		}
		, 'validation' : function($elem) {
			
			//필수 값 체크
			if($elem.attr('prompt-required') == 'true') {
				if($elem.val() == '') {
					return '[' + $elem.attr('prompt-title') + ']를 입력하세요';
				}
			}
			
			//프롬프트 값 확인
			let promptVal = '';
			if($elem.val().indexOf('-') > -1) {
				promptVal = $.datepicker.formatDate('yymmdd', new Date($elem.val()));
			} else {
				promptVal = $elem.val();
			}
			
			//프롬프트 길이
			let promptLength = promptVal.length;
			
			//최소 값 체크
			let promptMinLength = $elem.attr('prompt-min');
			if(promptMinLength) {
				if($elem.attr('prompt-promptSubType') == 2565) {
					//날짜 프롬프트일 경우
					if(new Date($elem.val()) < new Date(promptMinLength)) {
						return '[' + $elem.attr('prompt-title') + ']의 최소 날짜는 ' + promptMinLength + '입니다';
					}
				} else {
					if(promptLength < promptMinLength) {
						return '[' + $elem.attr('prompt-title') + ']의 최소 길이는 ' + promptMinLength + '입니다';
					}
				}
			}
			
			//최대 값 체크
			let promptMaxLength = $elem.attr('prompt-max');
			if(promptMaxLength) {
				if($elem.attr('prompt-promptSubType') == 2565) {
					//날짜 프롬프트일 경우
					if(new Date($elem.val()) > new Date(promptMaxLength)) {
						return '[' + $elem.attr('prompt-title') + ']의 최대 날짜는 ' + promptMaxLength + '입니다';
					}
				} else {
					if(promptLength > promptMaxLength) {
						return '[' + $elem.attr('prompt-title') + ']의 최대 길이는 ' + promptMaxLength + '입니다';
					}
				}
			}
			
			return null;
		}
	}
	, 'month': {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			var $month = $('<input class="run-setting-box_input" class="form-control" ui-type="month" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" '
			+ 'prompt-title="' + data['title'] + '" prompt-min="' + data['min'] + '" prompt-max="' + data['max'] + '" prompt-required="' + data['required'] + '" '
			+ 'style="cursor:pointer;"/>');
			$wrapper.append($month);
		    let selectDate = new AirDatepicker('input[prompt-id="'+ data['id'] +'"]', {
				  locale : commonAirDatepickerLanguage()
				, navTitles: {
			          days: '<strong>yyyy</strong> <i>MMMM</i>'
			        , months: '<strong>yyyy</strong>'
			    }
				, selectedDates : [new Date()]
//				, buttons: ['today', 'clear']
			    , autoClose : true
				, maxDate : new Date()
				, view :  'months' 
				, minView : 'months' 
			    , dateFormat : 'yyyy-MM'
			});
			
			//기본 값 설정
			if(data['defaultAnswer']) {
				selectDate.setViewDate(new Date(changeStringToDate(data['defaultAnswer'])));
				selectDate.selectDate(new Date(changeStringToDate(data['defaultAnswer'])));
			}
			
			//엔터키 이벤트
			$month.on('keyup', function(e) {
				if(e.keyCode==13) {
					$('#run').trigger('click');
				}
			});
		}
		, 'selected' : function($elem) {
			let promptVal = '';
			if($elem.val().indexOf('-') > -1) {
				promptVal = $.datepicker.formatDate('yymm', new Date($elem.val()));
			} else {
				promptVal = $elem.val();
			}
			
			return [promptVal];
		}
		, 'validation' : function($elem) {
			
			//필수 값 체크
			if($elem.attr('prompt-required') == 'true') {
				if($elem.val() == '') {
					return '[' + $elem.attr('prompt-title') + ']를 입력하세요';
				}
			}
			
			//프롬프트 값 확인
			let promptVal = '';
			if($elem.val().indexOf('-') > -1) {
				promptVal = $.datepicker.formatDate('yymm', new Date($elem.val()));
			} else {
				promptVal = $elem.val();
			}
			
			//프롬프트 길이
			let promptLength = promptVal.length;
			
			//최소 값 체크
			let promptMinLength = $elem.attr('prompt-min');
			if(promptMinLength) {
				if(promptLength < promptMinLength) {
					return '[' + $elem.attr('prompt-title') + ']의 최소 길이는 ' + promptMinLength + '입니다';
				}
			}
			
			//최대 값 체크
			let promptMaxLength = $elem.attr('prompt-max');
			if(promptMaxLength) {
				if(promptLength > promptMaxLength) {
					return '[' + $elem.attr('prompt-title') + ']의 최대 길이는 ' + promptMaxLength + '입니다';
				}
			}
			
			return null;
		}
	}
	, 'year' : {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			var $year = $('<input class="run-setting-box_input" class="form-control" ui-type="year" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" '
			+ 'prompt-title="' + data['title'] + '" prompt-min="' + data['min'] + '" prompt-max="' + data['max'] + '" prompt-required="' + data['required'] + '" '
			+ 'style="cursor:pointer;"/>');
			$wrapper.append($year);
			
			let selectDate = new AirDatepicker('input[prompt-id="'+ data['id'] +'"]', {
				locale : commonAirDatepickerLanguage()
				, navTitles : {
			          days: '<strong>yyyy</strong> <i>MMMM</i>'
			        , months: '<strong>yyyy</strong>'
			    }
				, selectedDates : [new Date()]
			    , autoClose : true
				, view : 'years'
				, minView : 'years'
				, dateFormat : 'yyyy'
//				, minDate : new Date()
				, maxDate : new Date()
			});
			
			//기본 값 설정
			if(data['defaultAnswer']) {
				selectDate.selectDate(new Date(changeStringToDate(data['defaultAnswer'])));
			}
			
			//엔터키 이벤트
			$year.on('keyup', function(e) {
				if(e.keyCode==13) {
					$('#run').trigger('click');
				}
			});
		}
		, 'selected': function($elem) {
			var selected = [];
			selected.push($elem.val());
			return selected;
		}
		, 'validation' : function($elem) {
			
			//필수 값 체크
			if($elem.attr('prompt-required') == 'true') {
				if($elem.val() == '') {
					return '[' + $elem.attr('prompt-title') + ']를 입력하세요';
				}
			}
			
			//프롬프트 값 확인
			let promptVal = '';
			if($elem.val().indexOf('-') > -1) {
				promptVal = $.datepicker.formatDate('yy', new Date($elem.val()));
			} else {
				promptVal = $elem.val();
			}
			
			//프롬프트 길이
			let promptLength = promptVal.length;
			
			//최소 값 체크
			let promptMinLength = $elem.attr('prompt-min');
			if(promptMinLength) {
				if(promptLength < promptMinLength) {
					return '[' + $elem.attr('prompt-title') + ']의 최소 길이는 ' + promptMinLength + '입니다';
				}
			}
			
			//최대 값 체크
			let promptMaxLength = $elem.attr('prompt-max');
			if(promptMaxLength) {
				if(promptLength > promptMaxLength) {
					return '[' + $elem.attr('prompt-title') + ']의 최대 길이는 ' + promptMaxLength + '입니다';
				}
			}
			
			return null;
		}
	}
	, 'checkbox': {
		  'label': _defaultElemRenderer['label']
		, 'body': function($wrapper, data) {
			var $checkbox = $('<input type="checkbox" class="form-check-input" ui-type="checkbox" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" '
			+ 'prompt-title="' + data['title'] + '" prompt-min="' + data['min'] + '" prompt-max="' + data['max'] + '" prompt-required="' + data['required'] + '" '
			+ 'value="'+ data['exAction']['value'] +'" value-unchecked="'+ data['exAction']['value-unchecked'] +'"'
			+ 'style="cursor:pointer; font-size: 1rem;"/>');
			$wrapper.append($checkbox);
			
			//기본 값 설정
			if(data['defaultAnswer']) {
				if($checkbox.val() == data['defaultAnswer']) {
					$checkbox.attr('checked', true);
				}
			}
		}
		, 'selected': function($elem) {
			var selected = [];
			
			if($elem.is(":checked")) {
				selected.push($elem.val());
			} else {
				selected.push($elem.attr('value-unchecked'));
			}
			
			return selected;
		}
		, 'validation' : function($elem) {
			
			//필수 값 체크
			if($elem.attr('prompt-required') == 'true') {
				if($elem.val() == '') {
					return '[' + $elem.attr('prompt-title') + ']를 입력하세요';
				}
			}
			
			return null;
		}
	}
	, 'multiselect-yyyymm' : {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			var $select = $('<select ui-type="list-default" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '" multiple="multiple"></select>');
			$wrapper.append($select);
			
			var to = 2020; from = to - 10;
			for (var i = to; i > from; i--) {
				$select.append('<option value="' + String(i) + '">' + String(i) + '</option>');
			}
			
			$select.val(String(to));
			$select.multiselect();
		}
		, 'selected' : function($elem) {
			var selected = [];
			$('option:selected', $elem).each(function(i, v) {
				selected.push( $(v).val() );
			})
			return selected;
		}
		, 'validation' : function($elem) {
			return null;
		}
	}
	, 'popup': {
		  'label' : _defaultElemRenderer['label']
		, 'body' : function($wrapper, data) {
			function loadCallback() {
				return data['suggestedAnswers'];
			}
			
			function selectCallback(selected) {
				var $elem = $('[prompt-id="' + data['id'] + '"]');
				
				if (selected) {
					var ids = [];
					var names = [];
					$.each(selected, function(i, v) {
						ids.push(v.id);
						names.push(v.name);
					});
					
					$elem.val(ids.join(';'));
					$elem.next().val(names.join(','));
				} else {
					$elem.val('');
					$elem.next().val('');
				}
			}
			
			function popupOpen() {
				var popup = window.open(__contextPath + '/_custom/jsp/popup.jsp', '_popup', 'width=460,height=570');
				__popupHandler.load = loadCallback;
				__popupHandler.select = selectCallback;
			}
			
			var $popup = $('<span class="popElem"><input type="hidden" ui-type="popup" prompt-id="' + data['id'] + '" prompt-tp="' + data['type'] + '"/><input readonly="readonly" type="text" value=""/><button type="button" class="popup">선택</button></span>');
			$wrapper.append($popup);
			$('button', $popup).click(popupOpen);
		}
		, 'selected': function($elem) {
			var selected = [];
			
			var val = $elem.val();
			if (val) {
				selected = val.split(';');
			}
			
			return selected;
		}
	}
};