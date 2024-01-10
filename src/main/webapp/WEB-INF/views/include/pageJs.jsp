<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!-- Bootstrap JS -->
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/style/bootstrap-5.3.2-dist/js/bootstrap.bundle.min.js?v=20231006001"></script>

<!-- Jquery JS -->
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-3.7.0.min.js?v=20231006001"></script>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-ui/jquery-ui-1.9.2.min.js?v=20231006001"></script>

<!-- dataTables -->
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/DataTables-1.13.8/js/jquery.dataTables.min.js?v=20231006001"></script>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/DataTables-1.13.8/js/dataTables.bootstrap5.min.js?v=20231006001"></script>

<!-- Air Datepicker -->
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/air-datepicker-3/dist/air-datepicker.js?v=20231006001"></script>

<!-- Daterangepicker -->
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/daterangepicker.js?v=20231006001"></script>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/moment.min.js?v=20231006001"></script>

<!-- 공통 CSS -->
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/common.js?v=20240110001"></script>

<script type="text/javascript">
	var __contextPath = '${pageContext.request.contextPath}';
	var __portalAppName = '${portalAppName}';
</script>