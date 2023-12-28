<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");

	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<body style="background-color: rgb(234 240 251);">
	<div id="top_div">
		<jsp:include flush="true" page="/WEB-INF/views/include/portalTop_Center${portalAppName}.jsp" />
	</div>
	<div class="row" style="width: 100%;">
		<div class="col-md-2">
			<jsp:include flush="true" page="/WEB-INF/views/include/adminLeft.jsp" />
		</div>
		<div id="frame_div" class="col-md-10">
		
</body>
</html>