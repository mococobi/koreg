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
	<title>포탈 세션 만료</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	
	<style type="text/css">
		html, body {
		  height: 100%;
		  font-size: 1rem;
		}
	</style>
</head>
<body>
	<div class="d-flex align-items-center justify-content-center vh-100">
		<div class="text-center">
            <h1 class="display-1 fw-bold">세션 만료</h1>
            <p class="fs-3">
            <%--
            <span class="text-danger">Opps!</span>
            --%> 
            포탈 세션이 만료되었습니다.
            </p>
            <%--
            <p class="lead">The page you’re looking for doesn’t exist.</p>
            --%>
            <a href="${pageContext.request.contextPath}/app/login/loginUserView.do" class="btn btn-primary">
            	<i class="bi bi-house-door-fill" style="margin-right: 10px;"></i>로그인 화면
            </a>
        </div>
    </div>
</body>
</html>