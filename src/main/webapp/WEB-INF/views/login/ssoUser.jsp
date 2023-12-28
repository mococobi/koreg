<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.mococo.web.util.HttpUtil"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ page import="WiseAccess.*"%>
<%
	String ssotoken = StringUtils.defaultString(request.getParameter("ssotoken"));
	System.out.println("ssotoken : " + ssotoken);
	
	int nRet = -1;
	String sIp = HttpUtil.getClientIP(request);
	/*
		에러코드(nResult값)
		0 : 정상
		-721 : 인증서DN 값이 null인 경우
		-722 : 인증서DN 값이 null은 아니지만 empty string인 경우
		-3034 : 인증서DN값이 SSO DB에 저장되어 있지 않은 경우
		-2580 : 계정이 잠긴 사용자 입니다.
	*/
	
	String sApiKey = (String)CustomProperties.getProperty("portal.sso.token.api.key");
	SSO sso = new SSO(sApiKey);
	
	nRet = sso.verifyToken(ssotoken, sIp);
	String sabun = "";
	
	if(nRet >= 0) {
		sabun = sso.getValueUserID();	//SSO 토큰 생성한 사번 값
		System.out.println("sabun : " + sabun);
	} else {
		System.out.println("SSO인증에 실패하였습니다.");
	}
	
	pageContext.setAttribute("sabun", sabun);

%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>포탈 SSO 로그인</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		#portal-loading {
			width: 100%;
			height: 100%;
			top: 172px;
   			left: 250px;
			position: fixed;
			display: block;
			background-color: #fff;
			z-index: 9000;
			text-align: center;
		}
		
		#loading-image {
			position: absolute;
			top: 25%;
   			left: 33%;
			z-index: 100;
		    width: 100px;
		    height: auto;
		}
	</style>
</head>
<body>
	<div id="portal-loading">
		<img id="loading-image" src="${pageContext.request.contextPath}/_custom/image/main/loading.gif" alt="Loading..." />
	</div>

    <form id="frmSignIn"></form>

<script type="text/javascript">

	$(document).ready(function() {
	    fnSsoLogin();
	});
	
	
	function fnSsoLogin() {
		if('${sabun}' != '') {
			$frmSignIn = $('#frmSignIn');
		    $frmSignIn.empty();
		    $frmSignIn.attr('action', '${pageContext.request.contextPath}/app/login/loginTrust.do');
		    $('<input type="hidden"/>').attr('name', 'userId').val('${sabun}').appendTo($frmSignIn);
		    $('<input type="hidden"/>').attr('name', 'screenId').val('PORTAL').appendTo($frmSignIn);
		    $frmSignIn.attr('method', 'post');
		    $frmSignIn.attr('target', '_self').submit();
		    $frmSignIn.empty().removeAttr('action','').removeAttr('target','').removeAttr('method','');
		} else {
			pageGoPost('_self', '${pageContext.request.contextPath}/app/error/errorAuth', []);
		}    
	}

</script>
</body>
</html>