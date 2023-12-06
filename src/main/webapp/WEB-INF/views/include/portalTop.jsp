<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<% 
	String mstrUserIdAttr = (String)session.getAttribute("mstrUserIdAttr");
	String mstrUserNameAttr = (String)session.getAttribute("mstrUserNameAttr");
	
	String mstrServerName = CustomProperties.getProperty("mstr.server.name");
	String mstrServerPort = CustomProperties.getProperty("mstr.server.port");
	String mstrDefaultProjectName = CustomProperties.getProperty("mstr.default.project.name");
%>
<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
			.form-control-dark {
			  border-color: var(--bs-gray);
			}
			.form-control-dark:focus {
			  border-color: #fff;
			  box-shadow: 0 0 0 .25rem rgba(255, 255, 255, .25);
			}
			
			.text-small {
			  font-size: 85%;
			}
			
			.dropdown-toggle:not(:focus) {
			  outline: 0;
			}
		</style>
	</head>
<body>
	<nav id="portal-top-nav-div" class="navbar navbar-expand-lg navbar-dark bg-primary">
		<div class="container-fluid">
			<a class="navbar-brand" href="#">
				<img id="top_menu_logo" alt="로고 이미지" src="${pageContext.request.contextPath}/image/logo/logo_hdr.png?v=20231123001" style="width: 100%; height: 30px;" class="d-inline-block align-top">
			</a>
			<button class="navbar-toggler" type="button" data-bs-toggle="collapse"
				data-bs-target="#navbarSupportedContent2"
				aria-controls="navbarSupportedContent2" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="navbarSupportedContent2">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">
					<!--  
					<li class="nav-item">
						<a class="nav-link active" aria-current="page" href="#">Home</a>
					</li>
					<li class="nav-item">
						<a class="nav-link" href="#">Link</a>
					</li>
					-->
					<li class="nav-item">
						<a onclick="popupMstrPage('MY_REPORT')" class="nav-link" aria-current="page" href="#">내 리포트</a>
					</li>
					<li class="nav-item">
						<a onclick="popupMstrPage('SHARE_REPORT')" class="nav-link" aria-current="page" href="#">공유 리포트</a>
					</li>
					<li class="nav-item">
						<a onclick="popupMstrPage('NEW_REPORT')" class="nav-link" aria-current="page" href="#">비정형 분석</a>
					</li>
					<li class="nav-item">
						<a onclick="popupMstrPage('NEW_DOSSIER')" class="nav-link" aria-current="page" href="#">새 대시보드</a>
					</li>
					<li class="nav-item">
						<a onclick="moveCommunityPage(1)" class="nav-link" aria-current="page" href="#">공지사항</a>
					</li>
					<li class="nav-item">
						<a onclick="moveCommunityPage(2)" class="nav-link" aria-current="page" href="#">FAQ</a>
					</li>
					<li class="nav-item">
						<a onclick="moveCommunityPage(3)" class="nav-link" aria-current="page" href="#">자료실</a>
					</li>
				</ul>
				<div class="flex-shrink-0 dropdown text-end">
					<a href="#" class="d-block link-body-emphasis text-decoration-none dropdown-toggle-x" data-bs-toggle="dropdown" aria-expanded="false">
						<svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="white" class="bi bi-person-circle" viewBox="0 0 16 16">
						  <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
						  <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1"/>
						</svg>
					</a>
					<!-- 
					<ul class="dropdown-menu text-small">
						<li><a class="dropdown-item" href="#">New project...</a></li>
						<li><a class="dropdown-item" href="#">Settings</a></li>
						<li><a class="dropdown-item" href="#">Profile</a></li>
						<li><hr class="dropdown-divider"></li>
						<li><a class="dropdown-item" href="#">Sign out</a></li>
					</ul>
					-->
				</div>
				<ul class="nav">
		        	<li class="nav-item">
		        		<span class="nav-link link-body-emphasis px-2" style="color: white !important;">${mstrUserNameAttr}(${mstrUserIdAttr})님 환영합니다</span>
		        	</li>
		      	</ul>
				<div class="d-flex" role="search">
					<button id="top_menu_logout" class="btn">
						<svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="white" class="bi bi-box-arrow-right" viewBox="0 0 16 16">
						  <path fill-rule="evenodd" d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0z"/>
						  <path fill-rule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3z"/>
						</svg>
					</button>
				</div>
			</div>
		</div>
	</nav>
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