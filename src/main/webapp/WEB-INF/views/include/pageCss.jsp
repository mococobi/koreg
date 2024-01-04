<%@ page language="java" contentType="thtml; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
    pageContext.setAttribute("portalAppName", portalAppName);
%>

<!-- Bootstrap Css -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/style/bootstrap-5.3.2-dist/css/bootstrap.min.css?v=20231006001" >
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/font/bootstrap-icons.min.css?v=20231006001">

<!-- Jquery Css -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/jquery-ui/themes/start/jquery-ui.css?v=20231006001" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/jquery-easyui-1.10.18/themes/default/easyui.css?v=20231006001" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/jquery-easyui-1.10.18/themes/icon.css?v=20231006001" />

<!-- dataTables -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/DataTables-1.13.8/css/dataTables.bootstrap5.min.css?v=20231006001"></script>

<!-- Air Datepicker -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/air-datepicker-3/dist/air-datepicker.css?v=20231006001"></script>

<!-- Portal Css -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/style/gcgf/layout.css?v=20240104001" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/style/gcgf/style${portalAppName}.css?v=20240104001" />

<!-- Application Css -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/style/common.css?v=20231228001" />

<!-- 파비콘 -->
<link rel="shortcut icon" href="${pageContext.request.contextPath}/_custom/image/favicon/favicon.ico?v=20231006001">