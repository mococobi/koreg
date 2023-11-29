<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.microstrategy.web.objects.WebIServerSession"%>
<%@ page import="com.microstrategy.web.objects.WebObjectsFactory"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ page import="com.mococo.microstrategy.sdk.util.MstrUtil"%>
<%
	String objectId = StringUtils.defaultString(request.getParameter("objectId"));
    String type = StringUtils.defaultString(request.getParameter("type"));
    boolean isvi = StringUtils.equalsIgnoreCase("y", request.getParameter("isvi"));
	
	System.out.println("objectId : " + objectId);
	System.out.println("type : " + type);
	System.out.println("isvi : " + isvi);
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>메인</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div>
		<iframe name="mstrReport" id="mstrReport" style="width: 100%; border: 1px solid silver; margin: 0px;" marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto"></iframe>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
	
<script type="text/javascript">
	$(function() {
		fnReportMainInit();
	});
	
	
	//초기 함수
	function fnReportMainInit() {
	
	}
	
</script>
</body>
</html>