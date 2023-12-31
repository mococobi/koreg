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
		.wrapper {
			font-family: "Malgun Gothic";
			font-size: 12px;
			padding: 10px;
			margin: 0px;
		}
		
		button#run {
			float: right;
			margin-bottom: 10px;
			padding: 5px 10px;
			background-color: #0078D4;
			border: 0;
			color: #ffffff;
		}
		
		.wrapper {
			width: 100%;
			height: 100%;
		}
		
		.top-wrapper {
			display: flex;
		}
		
		.prompt-wrapper {
			width: calc(100% - 100px);
		}
		
		.run-wrapper {
			width: 100px;
		}
		
		.elem-wrapper {
			display: inline-flex;
			align-items: center;
			margin: 0px 7px;
			height: 30px;
		}
		
		.elem-wrapper .elem-label {
			padding: 0 10px 0 0;
		}
		
		.elem-wrapper select {
			width: 120px;
		}
		
		.elem-wrapper input[type=text] {
			height: 30px;
			width: 150px;
		}
		
		.ms-options-wrap button {
			width: 150px !important;
		}
		
		#mstrReport {
			height: 100%;
		}
	</style>
<script type="text/javascript">
	var objectId = "";
	var type = 0;
	var isvi = true;
	var reportInfo = undefined;
	
	
	$(function() {
		//메인 대시보드 프롬프트 화면 숨김 처리
		if('${portalMainDashboardId}' != '') {
			$('.prompt-wrapper').parent().hide();
		}
		
		init();
	});
	
	
    function popupCallback(promptId) {
        console.log('=> promptId', promptId);
    }
    
    
	function init() {
		$(window).resize(function() {
			var height	= $(window).height();
			
			//var $detail = $('#divReportDetail');
			var $report = $('.report-wrapper');
			
			//console.log($detail.offset(), $report.offset(), $detail.height());
			console.log(height, $report.offset());
			
			$report.height( height - $report.offset().top - 10 );
		});
		
		$(window).resize();
		
// 		getPromptInfo();
		$('#run').click(getAnswerXML);
	}
	
	
	//리포트정보 및 프롬프트정보의 조회
	function getPromptInfo() {
		$('.prompt-wrapper').html('');
		
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
        		var $wrapper = $('<span class="elem-wrapper"></span>'); 
        		$('.prompt-wrapper').append($wrapper);
        		promptRenderer[uiType]['label']($wrapper, v);
        		promptRenderer[uiType]['body']($wrapper, v);
        	}
        });
        
        if(reportInfo.promptList.length == 0) {
			//자동실행
			$('#run').trigger('click');
        }
	}
	
	
	function getPromptVal() {
		var elemVal = {};
		$('[prompt-id]').each(function(i, v) {
			var $elem = $(v);
			elemVal[$elem.attr('prompt-id')] = promptRenderer[$elem.attr('ui-type')]['selected']($elem);
		});
		
		return elemVal;
	}
	
	
	function getAnswerXML() {

	    $.ajax({
	    	  type: 'post'
	    	, url: '${pageContext.request.contextPath}/app/mstr/getAnswerXML.json'
	    	, async: true
	    	, contentType: 'application/json;charset=utf-8'
	    	, data : JSON.stringify({
    			objectId: objectId, 
    			type: type,
    			promptVal: getPromptVal() // 현재 선택된 프롬프트값들을 파라미터로 전달하여 XML형태로 반환 받음.
    		})
    		, dataType: 'json'
    		, success: function(data, text, request) {
	    		// 리포트 실행 시 파라미터로 전달될 promptsAnswerXML의 값을 서버로부터 조회 성공
	    		var inputs = let inputs = getMstrFormDefinition(type);
	    		$.extend(inputs, {promptsAnswerXML: data['xml']});
	    		_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport', inputs);
	    	}
	    	, error : function(jqXHR, textStatus, errorThrown) {
	    		errorProcess(jqXHR, textStatus, errorThrown);
	    	}
	    });	
	}
</script>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/eisDivStart.jsp" />
	
	<div class="wrapper">
		<div class="top-wrapper">
			<div class="prompt-wrapper"></div>
			<div class="run-wrapper">
				<button type="button" id="run">리포트 조회</button>
			</div>
		</div>
		<div class="report-wrapper">
			<iframe name="mstrReport" id="mstrReport" src=""
				style="width: 100%; border: 1px solid silver; margin: 0px;"
				marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto"></iframe>
		</div>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/eisDivEnd.jsp" />
</body>
</html>