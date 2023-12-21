<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.microstrategy.web.objects.WebIServerSession"%>
<%@ page import="com.microstrategy.web.objects.WebObjectsFactory"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ page import="com.mococo.microstrategy.sdk.util.MstrUtil"%>
<%
	String objectId = "";
    String type = "";
    boolean isvi = StringUtils.equalsIgnoreCase("y", request.getParameter("isvi"));
    
    if(request.getParameter("objectId") != null) {
    	objectId = StringUtils.defaultString(request.getParameter("objectId"));
    	type = StringUtils.defaultString(request.getParameter("type"));
    } else if(request.getAttribute("portalMainDashboardId") != null) {
    	objectId = StringUtils.defaultString((String)request.getAttribute("portalMainDashboardId"));
    	type = ((Integer)request.getAttribute("type")).toString();
    	isvi = (Boolean)request.getAttribute("isvi");
    }
%>
<!DOCTYPE html>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/jquery-multiselect/jquery.multiselect.css?v=20231006001" />
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-multiselect/jquery.multiselect.js?v=20231128001"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/prompt-renderer.js?v=20231128001"></script>
	
	<style type="text/css">
		#mstrReport {
			height: 100%;
		}
	</style>
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
		
		fnReportInit();
	});
	
	
    function popupCallback(promptId) {
        console.log('=> promptId', promptId);
    }
    
    
    //초기 실행 함수
	function fnReportInit() {
		$(window).resize(function() {
			var height	= $(window).height();
			var $report = $('.report-wrapper');
			
// 			$report.height( height - $report.offset().top - 18);
		});
		
// 		$(window).resize();
		
		getPromptInfo();
		$('#run').click(getAnswerXML);
	}
	
	
	//리포트정보 및 프롬프트정보의 조회
	function getPromptInfo() {
		$('#portal-loading').show();
		$('.run-setting-box').html('');
		
		let callParams = {
			  objectId : objectId
			, type : type
		};
		callAjaxPost('/mstr/getReportInfo.json', callParams, function(data){
			if (data) {
    			reportInfo = data.report;
	    		renderPrompt();
    		} else {
    		    alert('리포트정보를 가져올 수 없습니다.');
    		    $('#portal-loading').show();
    		}
			
			$(window).resize(); // 프롬프트 랜더링이 종료되고 iframe 높이 조정
		});
	}
	
	
	//프롬프트정보를 이용한 랜더링
	function renderPrompt() {
		
		if (reportInfo == undefined || reportInfo.promptList == undefined) {
			//자동실행
			$('#run').trigger('click');
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
			$('#run').trigger('click');
        } else {
        	$('#mstrReport').attr('src', '${pageContext.request.contextPath}/app/main/selectPrompt.do');
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
		
		//정합성 체크
		let promptCheck = validationPrompt();
		for (const [key, value] of Object.entries(promptCheck)) {
			if(value) {
				alert(value);
				$('#portal-loading').hide();
				return false;
			}
		}
		
		//현재 선택된 프롬프트값들을 파라미터로 전달하여 XML형태로 반환 받음.
		let promptVal = getPromptVal();
		
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
</script>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div class="run-box flex">
		<!-- 프롬프트 영역 -->
	    <ul class="run-setting-box flex"></ul>
	    <button id="run" class="btn-run blue">실행</button>
	</div>
	
	<div class="top cont-wrap flex" style="height: calc(100vh - 275px);">
	    <iframe name="mstrReport" id="mstrReport" src=""
				style="width: 100%; border: 1px solid silver; margin: 0px;"
				marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto">
		</iframe>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
</html>