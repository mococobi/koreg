<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="com.microstrategy.web.objects.WebIServerSession"%>
<%@ page import="com.microstrategy.web.objects.WebObjectsFactory"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ page import="com.mococo.microstrategy.sdk.util.MstrUtil"%>
<%
	String objectId = "";
    String type = "";
    boolean isvi = StringUtils.equalsIgnoreCase("y", request.getParameter("isvi"));
    String title = StringUtils.defaultString(request.getParameter("title"));
    title = URLDecoder.decode(title, "UTF-8");
    
    if(request.getParameter("objectId") != null) {
    	objectId = StringUtils.defaultString(request.getParameter("objectId"));
    	type = StringUtils.defaultString(request.getParameter("type"));
    } else if(request.getAttribute("portalMainDashboardId") != null) {
    	objectId = StringUtils.defaultString((String)request.getAttribute("portalMainDashboardId"));
    	type = ((Integer)request.getAttribute("type")).toString();
    	isvi = (Boolean)request.getAttribute("isvi");
    }
    
    String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
    pageContext.setAttribute("portalAppName", portalAppName);
    pageContext.setAttribute("title", title);
%>
<!DOCTYPE html>
<html>
<head>
	<title>${title}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/jquery-multiselect/jquery.multiselect.css?v=20240115001" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-multiselect/jquery.multiselect.js?v=20240115001"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/prompt-renderer.js?v=20240115001"></script>
	
	<style type="text/css">
		#mstrReport {
			height: 100%;
		}
		
		.run-setting-box .ms-options-wrap li {
			margin-right: 0px !important;
		    padding-right: 0px !important;
		    content: none !important;
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart${portalAppName}.jsp" />
	
	<div class="run-box flex" style="display: flow-root;">
		<!-- 프롬프트 영역 -->
	    <ul class="run-setting-box flex" style="float: left; padding-left: 20px;"></ul>
    	<button id="run" class="btn-run blue" style="float: right; margin-right: 20px;">실행</button>
	</div>
	
	<div class="top cont-wrap flex" style="height: calc(100vh - 203px);">
	    <iframe name="mstrReport" id="mstrReport" src=""
				style="width: 100%; border: 1px solid silver; margin: 0px; background: #fff; border-radius: 8px; border: 1px solid #c8d8ec;"
				marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto">
		</iframe>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
	
<script type="text/javascript">
	var objectId = "<%= objectId %>";
	var type = <%= type %>;
	var isvi = <%= isvi %>;
	var reportInfo = undefined;
	
	
	$(function() {
		//메인 대시보드 프롬프트 화면 숨김 처리
		if('${portalMainDashboardId}' != '') {
			$('.prompt-wrapper').parent().hide();
		} else {
			$('.prompt-wrapper').parent().show();
		}
	
		$('#mstrReport').on("load", function() {
			$('#portal-loading').hide();
		});
		
		$('#run').on('click', function() {
			getAnswerXML();
		})
		
		fnReportInit();
	});
	
	
    //초기 실행 함수
	function fnReportInit() {
		getPromptInfo();
	}
	
	
	//리포트정보 및 프롬프트정보의 조회
	function getPromptInfo() {
		$('#portal-loading').show();
		$('.run-setting-box').html('');
		
		let callParams = {
			  objectId : objectId
			, type : type
		};
		callAjaxPost('/mstr/getReportInfo.json', callParams, function(data) {
			if (data) {
    			reportInfo = data.report;
    			document.title = reportInfo['title'];
	    		renderPrompt();
	    		setTimeout(() => {
	    			$('#run').trigger('click');
	    		}, 100);
    		} else {
    		    alert('리포트정보를 가져올 수 없습니다.');
    		    $('#portal-loading').hide();
    		}
		});
	}
	
	
	//프롬프트정보를 이용한 랜더링
	function renderPrompt() {
		
		if (reportInfo == undefined || reportInfo.promptList == undefined) {
			//자동실행
// 			$('#run').trigger('click');
        	return; 
        }
        	
        $.each(reportInfo.promptList, function(i, v) {
        	var uiType = undefined;
        	
        	if (v['exUiType']) {
        		uiType = v['exUiType'];
        	} else {
	            switch (v['type']) { // 프롬프트유형을 이용한 UI유형결정
		            case 1:
		            	uiType = 'value-default';
		            	break;
		            case 2:	  
		            case 4:
		            	uiType = 'list-default';
		            	if ((v['max'] && Number(v['max']) > 1) || !v['max']) {
		            	    uiType = 'multiSelect-default';
		            	}
		                break;
		            default:
	            } 
        	}
        	
        	if (promptRenderer[uiType]) {
        		var $wrapper = $('<li>', {class : 'flex'});
        		$('.run-setting-box').append($wrapper);
        		promptRenderer[uiType]['label']($wrapper, v);
        		promptRenderer[uiType]['body']($wrapper, v);
        	}
        });
        
        if(reportInfo.promptList.length == 0) {
			//자동실행
// 			$('#run').trigger('click');
        } else {
//         	$('#mstrReport').attr('src', '${pageContext.request.contextPath}/app/main/selectPrompt.do');
        }
	}
	
	
	//정합성 체크
	function validationPrompt() {
		var elemVal = {};
		$('[prompt-id]').each(function(i, v) {
			var $elem = $(v);
			elemVal[$elem.attr('prompt-id')] = promptRenderer[$elem.attr('ui-type')]['validation']($elem);
		});
		
		return elemVal;
	}
	
	
	//프롬프트 값 세팅
	function getPromptVal() {
		var elemVal = {};
		$('[prompt-id]').each(function(i, v) {
			var $elem = $(v);
			elemVal[$elem.attr('prompt-id')] = promptRenderer[$elem.attr('ui-type')]['selected']($elem);
		});
		
		return elemVal;
	}
	
	
	//리포트 실행
	function getAnswerXML() {
		$('#portal-loading').show();
		
		//현재 선택된 프롬프트값들을 파라미터로 전달하여 XML형태로 반환 받음.
		let promptVal = getPromptVal();
		
		if(typeof reportInfo['promptList'] != 'undefined' && reportInfo['promptList'].length == 0) {
			//프롬프트 없을 경우
			let inputs = getMstrFormDefinition(type);
			_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport', inputs);
		} else {
			//프롬프트 있을 경우
			
			//정합성 체크
			let promptCheck = validationPrompt();
			for (const [key, value] of Object.entries(promptCheck)) {
				if(value) {
					alert(value);
					$('#portal-loading').hide();
					return false;
				}
			}
			
			$.ajax({
		    	  type: 'post'
		    	, url: '${pageContext.request.contextPath}/app/mstr/getAnswerXML.json'
		    	, async: true
		    	, contentType: 'application/json;charset=utf-8'
		    	, data : JSON.stringify({
	    			  objectId: objectId
	    			, type: type
	    			, promptVal: promptVal
	    		})
	    		, dataType: 'json'
	    		, success: function(data, text, request) {
		    		// 리포트 실행 시 파라미터로 전달될 promptsAnswerXML의 값을 서버로부터 조회 성공
		    		let inputs = getMstrFormDefinition(type);
		    		$.extend(inputs, {promptsAnswerXML : data['xml']});
		    		_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport', inputs);
		    	}
		    	, error : function(jqXHR, textStatus, errorThrown) {
		    		$('#portal-loading').hide();
		    		errorProcess(jqXHR, textStatus, errorThrown);
		    	}
		    });
		}
	}
	
	
	//팝업 콜백
    function popupCallback(promptId) {
        console.log('=> promptId', promptId);
    }
</script>
</body>
</html>