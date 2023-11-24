<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mococo.microstrategy.sdk.esm.vo.MstrUser" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
// 세션 : 사용자 ID  
MstrUser mstrUser = (MstrUser) session.getAttribute("mstr-user-vo");
String sMstrUid = mstrUser.getId();
//System.out.println("mstrUid : " + sMstrUid);

String sDomainAddr = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
//System.out.println("DomainAddr : " + sDomainAddr);
%>
<c:set var="mstrUid" value="<%=sMstrUid%>"/>
<c:set var="domainAddr" value="<%=sDomainAddr%>"/>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login Bridge</title>
</head>
<body>
로그인 처리 중...

<script src="${pageContext.request.contextPath}/javascript/jquery-3.7.0.min.js"></script>
<script type="text/javascript">
//prevent mouse right click..
window.addEventListener('contextmenu', function(e) { e.preventDefault(); }, false); // Not compatible with IE < 9

//init
$(document).ready(function() {
    navigator.sendBeacon("${domainAddr}/MicroStrategy/plugins/esm/jsp/sso.jsp?mstrUserId=${mstrUid}");
    window.location.replace("${pageContext.request.contextPath}/app/main/category.do");
});
</script>
</body>
</html>