<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>메인화면</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		.wrapper {
			font-family: "Malgun Gothic";
			font-size: 12px;
			padding: 10px;
			margin: 0px;
		}
		
		.wrapper {
			width: 100%;
			height: 100%;
		}
		
		#mainReport {
			height: 100%;
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div class="wrapper">
		<div class="report-wrapper">
			<iframe name="mainReport" id="mainReport" src=""
				style="width: 100%; border: 1px solid silver; margin: 0px;"
				marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto"></iframe>
		</div>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
	
<script type="text/javascript">
	var objectId = "<%= objectId %>";
	var type = <%= type %>;
	var isvi = <%= isvi %>;
	

	$(function() {
		$('#mainReport').on("load", function() {
			$('#portal-loading').hide();
		});
		
		fnMainInit();
	});
	
	
	//초기 함수
	function fnMainInit() {
		$('#portal-loading').show();
		
		$(window).resize(function() {
			let height	= $(window).height();
			let $report = $('.report-wrapper');
			
			$report.height( height - $report.offset().top - 10 );
		});
		
		$(window).resize();
		
		//메인 대시보드 실행
		let dossierUrl =  'evt=3140&src=mstrWeb.3140&' + '&Server='+ __mstrServerName +'&Port='+ __mstrServerPort +'&Project='+ __mstrDefaultProjectName +'&share=1&hiddensections=path,dockTop&';
		dossierUrl += 'documentID=' + objectId;
		$('#mainReport').attr('src', '${pageContext.request.contextPath}/servlet/mstrWeb?' + dossierUrl);
	}
	

</script>
</body>
</html>