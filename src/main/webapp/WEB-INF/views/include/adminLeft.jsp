<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
			body {
			  min-height: 100vh;
			  min-height: -webkit-fill-available;
			}
			
			html {
			  height: -webkit-fill-available;
			}
			
			main {
			  height: 100vh;
			  height: -webkit-fill-available;
			  max-height: 100vh;
			  overflow-x: auto;
			  overflow-y: hidden;
			}
			
			.dropdown-toggle { outline: 0; }
			
			.btn-toggle {
			  padding: .25rem .5rem;
			  font-weight: 600;
			  color: var(--bs-emphasis-color);
			  background-color: transparent;
			}
			.btn-toggle:hover,
			.btn-toggle:focus {
			  color: rgba(var(--bs-emphasis-color-rgb), .85);
			  background-color: var(--bs-tertiary-bg);
			}
			
			.btn-toggle::before {
			  width: 1.25em;
			  line-height: 0;
			  content: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='rgba%280,0,0,.5%29' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M5 14l6-6-6-6'/%3e%3c/svg%3e");
			  transition: transform .35s ease;
			  transform-origin: .5em 50%;
			}
			
			[data-bs-theme="dark"] .btn-toggle::before {
			  content: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='rgba%28255,255,255,.5%29' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M5 14l6-6-6-6'/%3e%3c/svg%3e");
			}
			
			.btn-toggle[aria-expanded="true"] {
			  color: rgba(var(--bs-emphasis-color-rgb), .85);
			}
			.btn-toggle[aria-expanded="true"]::before {
			  transform: rotate(90deg);
			}
			
			.btn-toggle-nav a {
			  padding: .1875rem .5rem;
			  margin-top: .125rem;
			  margin-left: 1.25rem;
			}
			.btn-toggle-nav a:hover,
			.btn-toggle-nav a:focus {
			  background-color: var(--bs-tertiary-bg);
			}
			
			.scrollarea {
			  overflow-y: auto;
			}
			
			  #left_menu_tree
			, #left_menu_tree a
			, #left_menu_tree input
			, #left_menu_tree span
			, #left_menu_tree select
			, #left_menu_tree button {
				font-size: 1.5rem;
			}
			
		</style>
	</head>
<body>
<div class="flex-shrink-0 p-3" style="">
	<ul id="left_menu_tree" class="list-unstyled ps-0" style="height: calc(75vh); overflow: auto;">
		<li class="mb-1">
			<button
				title="포탈 관리"
				class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed"
				data-bs-toggle="collapse"
				data-bs-target="#portalManage-collapse"
				aria-expanded="true">포탈 관리</button>
			<div id="portalManage-collapse" class="collapse show">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li>
						<a title="게시판 관리" onclick="moveAdminPage('BOARD_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>게시판 관리
						</a>
					</li>
					<!-- 
					<li>
						<a title="코드 분류 관리" onclick="moveAdminPage('CODE_TYPE_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>코드 분류 관리
						</a>
					</li>
					-->
					<li>
						<a title="코드 관리" onclick="moveAdminPage('CODE_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>코드 관리
						</a>
					</li>
				</ul>
			</div>
		</li>
		<%--
		<li class="mb-1">
			<button
				title="MSTR"
				class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed"
				data-bs-toggle="collapse"
				data-bs-target="#mstrManage-collapse"
				aria-expanded="true">MSTR</button>
			<div id="mstrManage-collapse" class="collapse show">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li>
						<a title="사용자 확인" onclick="moveAdminPage('LOGIN_LOG_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>사용자 확인
						</a>
					</li>
					<li>
						<a title="보고서 확인" onclick="moveAdminPage('BOARD_LOG_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>보고서 확인
						</a>
					</li>
				</ul>
			</div>
		</li>
		--%>
		<li class="mb-1">
			<button
				title="로그"
				class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed"
				data-bs-toggle="collapse"
				data-bs-target="#portalLog-collapse"
				aria-expanded="true">로그</button>
			<div id="portalLog-collapse" class="collapse show">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li>
						<a title="포탈 로그 확인(로그인)" onclick="moveAdminPage('LOGIN_LOG_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>포탈 로그 확인(로그인)
						</a>
					</li>
					<li>
						<a title="포탈 로그 확인(게시판)" onclick="moveAdminPage('BOARD_LOG_ADMIN')" href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">
							<i class="bi bi-tools" style="margin-right: 15px;"></i>포탈 로그 확인(게시판)
						</a>
					</li>
				</ul>
			</div>
		</li>
	</ul>
</div>
</body>
<script type="text/javascript">
	
	$(function() {
		initLeftMenu();
	});
	
	
	//초기함수
	function initLeftMenu() {
		$(window).resize(function() {
			let height	= $(window).height();
			$('#left_menu_tree').height(height - $('#portal-top-nav-div').height() - 70);
		});
		
		$(window).resize();
	}
	
</script>
</html>