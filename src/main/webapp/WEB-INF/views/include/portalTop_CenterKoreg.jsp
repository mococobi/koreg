<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
	String mstrUserIdAttr = (String)session.getAttribute("mstrUserIdAttr");
	String mstrUserNameAttr = (String)session.getAttribute("mstrUserNameAttr");
	
	String mstrServerName = CustomProperties.getProperty("mstr.server.name");
	String mstrServerPort = CustomProperties.getProperty("mstr.server.port");
	String mstrDefaultProjectName = CustomProperties.getProperty("mstr.default.project.name");
	
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");
%>
<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
			#portal-loading {
				width: 100%;
				height: 100%;
				/*
				top: 0px;
				left: 0px;
				*/
				top: 100px;
    			left: 250px;
				position: fixed;
				display: block;
				/* opacity: 0.7; */
				background-color: #fff;
				z-index: 9000;
				text-align: center;
			}
			
			#loading-image {
				position: absolute;
				/*
				top: 40%;
				left: 46%;
				*/
				top: 31%;
    			left: 39%;
				z-index: 100;
			    width: 100px;
			    height: auto;
			}
		</style>
	</head>
<body>
	<header class="header"> <!-- 헤더 영역 -->
		<div class="logo">
			<a href="#">
				<img id="top_menu_logo" src="${pageContext.request.contextPath}/_custom/image/koreg/logo.png?v=20231218001" alt="신용보증재단">
			</a>
		</div>
		<div class="util flex">
			<p class="welcome-text"><span class="user-id">${mstrUserNameAttr}</span> &nbsp;님 환영합니다.</p>
			<a id="top_menu_logout" href="#">로그아웃</a>
		</div>
	</header>
	<!-- //header -->
</body>
<script type="text/javascript">
	var __mstrServerName = "<%=mstrServerName%>";
	var __mstrServerPort = "<%=mstrServerPort%>";
	var __mstrDefaultProjectName = "<%=mstrDefaultProjectName%>";
	
	$(function() {
		
		//메인화면 이동
		$("#top_menu_logo").click(function() {
			let pagePrams = [];
			pageGoPost('_self', '${pageContext.request.contextPath}/app/main/mainView.do', pagePrams);
		});
		
		//포탈 로그아웃
		$("#top_menu_logout").click(function() {
			let msg = '로그아웃 하시겠습니까?';
			if (confirm(msg)) {
				let pagePrams = [];
				pageGoPost('_self', '${pageContext.request.contextPath}/app/login/logoutUser.do', pagePrams);
		    }
		});
	});
</script>
</html>