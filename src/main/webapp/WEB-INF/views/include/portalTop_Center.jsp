<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
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
			
			#portal-loading {
				width: 100%;
				height: 100%;
				/*
				top: 0px;
				left: 0px;
				*/
				top: 15%;
    			left: 15%;
				position: fixed;
				display: block;
				/* opacity: 0.7; */
				background-color: #fff;
				z-index: 9999;
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
			}
		</style>
	</head>
<body>
	<div id="portal-top-nav-div">
		<div class="container-test" style="background-color: rgb(26 48 86);">
			<header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-1 mb-0 border-bottom">
				<div class="col-md-3 mb-2 mb-md-0">
				</div>
				<ul class="nav col-12 col-md-auto mb-2 justify-content-center mb-md-0">
					<li>
						<a class="navbar-brand" href="#">
							<img id="top_menu_logo" alt="로고 이미지" src="${pageContext.request.contextPath}/_custom/image/logo/logo.png?v=20231123001" style="width: 100%; height: 40px; background-color: white;" class="d-inline-block align-top">
						</a>
					</li>
				</ul>
				<div class="col-md-3 text-end">
					 <ul class="nav col-12 col-md-auto mb-2 justify-content-center mb-md-0">
				        <li>
				        	<button class="btn" style="cursor: default;">
								<i class="bi bi-person-circle" style="color: white; font-size: 1.2rem;"></i>
							</button>
						</li>
				        <li>
				        	<span class="nav-link link-body-emphasis px-2" style="color: white !important;">${mstrUserNameAttr}(${mstrUserIdAttr})님 환영합니다</span>
				        </li>
				        <li>
							<button id="top_menu_logout" class="btn" title="로그아웃">
								<i class="bi bi-box-arrow-right" style="color: white; font-size: 1.2rem;"></i>
							</button>
						</li>
				      </ul>
				</div>
			</header>
		</div>
		<div class="container" style="">
			<header class="d-flex justify-content-center py-3">
				<ul class="nav nav-pills">
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="popupMstrPage('MY_REPORT')" type="button" class="btn btn-outline-primary">내 리포트</button>
					</li>
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="popupMstrPage('SHARE_REPORT')" type="button" class="btn btn-outline-primary">부서 공유 리포트</button>
					</li>
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="popupMstrPage('NEW_REPORT')" type="button" class="btn btn-outline-primary">비정형 분석</button>
					</li>
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="popupMstrPage('NEW_DOSSIER')" type="button" class="btn btn-outline-primary">새 대시보드</button>
					</li>
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="moveCommunityPage(1)" type="button" class="btn btn-outline-primary">공지사항</button>
					</li>
					
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="moveCommunityPage(2)" type="button" class="btn btn-outline-primary">FAQ</button>
					</li>
					<%-- 
					<li class="nav-item" style="margin-right: 10px;">
						<button onclick="moveCommunityPage(3)" type="button" class="btn btn-outline-primary">자료실</button>
					</li>
					--%>
					<% if(PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN")) { %>
						<!-- 관리자 기능 -->
						<li class="nav-item" style="margin-right: 10px;">
							<button onclick="moveAdminPage('BOARD_ADMIN')" type="button" class="btn btn-outline-primary">관리자</button>
						</li>
					<% } %>
				</ul>
			</header>
		</div>
	</div>
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