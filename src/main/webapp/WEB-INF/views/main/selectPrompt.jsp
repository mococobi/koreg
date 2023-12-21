<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>프롬프트 선택</title>
	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/style/bootstrap-5.3.2-dist/css/bootstrap.min.css?v=20231006001" >
<%-- 	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" /> --%>
</head>
<body>
	<div class="d-flex align-items-center justify-content-center vh-100">
		<div class="text-center">
            <h1 class="display-3 fw-bold">프롬프트 선택</h1>
            <p class="fs-3">
            <%--
            <span class="text-danger">Opps!</span>
            --%> 
            프롬프트를 선택하고 리포트를 조회하세요.
            </p>
            <%--
            <p class="lead">The page you’re looking for doesn’t exist.</p>
            --%>
        </div>
    </div>
</body>
</html>