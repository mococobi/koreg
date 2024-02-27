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
    boolean isvi = StringUtils.equalsIgnoreCase("true", request.getParameter("isvi"));
    String title = StringUtils.defaultString(request.getParameter("title"));
    title = URLDecoder.decode(title, "UTF-8");
    
    if(request.getParameter("objectId") != null) {
    	objectId = StringUtils.defaultString(request.getParameter("objectId"));
    	type = StringUtils.defaultString(request.getParameter("type"));
    } else if(request.getAttribute("portalMainDashboardId") != null) {
    	objectId = StringUtils.defaultString((String)request.getAttribute("portalMainDashboardId"));
    	type = ((Integer)request.getAttribute("type")).toString();
    	isvi = (Boolean)request.getAttribute("isvi");
    } else {
    	type = "0";
    }
    
    String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
    pageContext.setAttribute("portalAppName", portalAppName);
    pageContext.setAttribute("title", title);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>${title}</title>
	
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
	
	<div class="top cont-wrap flex" style="height: calc(100vh - 163px);">
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
		
		$('#mstrReportTitle').text(getMstrTitleName('${title}'));
		fnReportInit();
	});
	
	
    //초기 실행 함수
	function fnReportInit() {
		$('#portal-loading').show();
		
		_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport', getMstrFormDefinition(objectId, type, isvi));
	}
	
	
	//팝업 콜백
    function popupCallback(promptId) {
        console.log('=> promptId', promptId);
    }
</script>
</body>
</html>