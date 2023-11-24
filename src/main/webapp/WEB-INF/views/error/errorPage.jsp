<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String sDomainAddr = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
//System.out.println("DomainAddr : " + sDomainAddr);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error</title>
</head>
<body>
커스텀 에러 페이지
<a href="<%=sDomainAddr%>/SmartFactory">로그인 화면</a>
</body>
</html>