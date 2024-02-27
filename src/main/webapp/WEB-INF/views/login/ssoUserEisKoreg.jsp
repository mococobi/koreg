<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.mococo.web.util.HttpUtil"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ page import="SafeIdentity.*" %>
<%
	response.setHeader("Cache-Control", "no-cache");

	String sToken = StringUtils.defaultString(request.getParameter("npstoken"));
// 	System.out.println("ssotoken : " + ssotoken);
	
	int nResult = -1;
// 	String sToken = "";
	String CIP = request.getRemoteAddr();
	
	//------------------------------------------------------------------
	Cookie[] cookies = request.getCookies();
	if (cookies != null) 
	{
		System.out.println("cookies.length[" + cookies.length + "]");
		for (int i=0; i < cookies.length; i++) {
		
			String name = cookies[i].getName();
			String sDomain = cookies[i].getDomain();		
			
			if(name != null && name.equals("npstoken")) {
				System.out.println("* cookiename =" + cookies[i].getName() );
				System.out.println("* cookievalue=" + cookies[i].getValue());
				System.out.println("* cookies[i].getValue()=" + cookies[i].getValue());
				sToken = cookies[i].getValue();
			}
		}
	}
	//------------------------------------------------------------------
	
	SSO sso = new SSO();
	String userID = "";
	
	if(sToken != "")
	{	
		nResult = sso.verifyToken(sToken, CIP) ;
		if(nResult < 0)
		{
			System.out.println("인증 오류입니다. 에러코드 : " + nResult);
			// 페이지 접근을 제한하는 코드 삽입.
		}else{
			userID = sso.getValueUserID();
			// 사용자 정보 조회
			System.out.println("sso token에서 검증한 사용자 정보입니다. <br><br>");
			System.out.println("UserID : " + userID);	
		}
	}
	
	pageContext.setAttribute("npstoken", sToken);
	pageContext.setAttribute("sabun", userID);

%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>EIS SSO 로그인</title>
	
	<!-- Bootstrap Css -->
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/style/bootstrap-5.3.2-dist/css/bootstrap.min.css?v=20240115001" >
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/font/bootstrap-icons.min.css?v=20240115001">

	<!-- Bootstrap JS -->
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/style/bootstrap-5.3.2-dist/js/bootstrap.bundle.min.js?v=20240115001"></script>

	<!-- Jquery JS -->
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-3.7.0.min.js?v=20240115001"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-ui/jquery-ui-1.9.2.min.js?v=20240115001"></script>
	
	<!-- 공통 CSS -->
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/common.js?v=20240115001"></script>

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
		
		const agent = window.navigator.userAgent.toLowerCase();
		let browserName;
		let newWindowCheck = false;
		
		switch (true) {
			case agent.indexOf("edge") > -1: 
				browserName = "MS Edge"; // MS 엣지
				break;
			case agent.indexOf("edg/") > -1: 
				browserName = "Edge (chromium based)"; // 크롬 기반 엣지
				break;
			case agent.indexOf("opr") > -1 && !!window.opr: 
				browserName = "Opera"; // 오페라
				break;
			case agent.indexOf("chrome") > -1 && !!window.chrome: 
				browserName = "Chrome"; // 크롬
				break;
			case agent.indexOf("trident") > -1: 
				browserName = "MS IE"; // 익스플로러
				
				//Edge 브라우저로 열기
				newWindowCheck = true;
				
				let moveUrl = location.origin + location.pathname + '?npstoken=${npstoken}';
				window.open("microsoft-edge:"+ moveUrl);

// 				top.window.open('about:blank','_self').close();
// 				top.window.opener = self;
// 				top.self.close();

				//테스트 필요
// 				window.open('','_self').close();
				
				break;
			case agent.indexOf("firefox") > -1: 
				browserName = "Mozilla Firefox"; // 파이어 폭스
				break;
			case agent.indexOf("safari") > -1: 
				browserName = "Safari"; // 사파리
				break;
			default: 
				browserName = "other"; // 기타
		}
		
		if(!newWindowCheck) {
			if('${sabun}' != '') {
				$frmSignIn = $('#frmSignIn');
			    $frmSignIn.empty();
			    $frmSignIn.attr('action', '${pageContext.request.contextPath}/app/login/loginTrust.do');
			    $('<input type="hidden"/>').attr('name', 'userId').val('${sabun}').appendTo($frmSignIn);
			    $('<input type="hidden"/>').attr('name', 'screenId').val('EIS').appendTo($frmSignIn);
			    $frmSignIn.attr('method', 'post');
			    $frmSignIn.attr('target', '_self').submit();
			    $frmSignIn.empty().removeAttr('action','').removeAttr('target','').removeAttr('method','');
			} else {
				pageGoPost('_self', '${pageContext.request.contextPath}/app/error/errorSsoAuth', []);
			}
		}
		
	}

</script>
</body>
</html>