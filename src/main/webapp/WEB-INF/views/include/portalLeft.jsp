<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<% 
	String mstrUserIdAttr = (String)session.getAttribute("mstrUserIdAttr");
	String mstrUserNameAttr = (String)session.getAttribute("mstrUserNameAttr");
%>
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

			.mstrType-55::before {
			    width: 1.25em;
			    line-height: 0;
			    content: url(${pageContext.request.contextPath}/image/bootstrap-icons-1.11.2/file-earmark-bar-graph.svg);
			    transition: transform .35s ease;
			    transform-origin: 0.5em 50%;
			    margin-right: 5px;
			}
			
			.mstrType-3::before {
			    width: 1.25em;
			    line-height: 0;
			    content: url(${pageContext.request.contextPath}/image/bootstrap-icons-1.11.2/grid-3x3.svg);
			    transition: transform .35s ease;
			    transform-origin: 0.5em 50%;
			    margin-right: 5px;
			}
			
		</style>
	</head>
<body>
<%-- 
	<div id="left_menu_tree" style="height: calc(93vh); overflow: auto;">
		<!-- 
		<ul>
			<li>상담/접수/실행 분석</li>
			<li>
				<ol>
					<li>고객 속성별</li>
					<li>상품별</li>
					<li>업체수</li>
				</ol>
			</li>
		</ul>
		-->
	</div>
--%>
<div class="flex-shrink-0 p-3" style="width: 280px;">
	<!-- 
	<a href="/"
		class="d-flex align-items-center pb-3 mb-3 link-body-emphasis text-decoration-none border-bottom">
		<svg class="bi pe-none me-2" width="30" height="24">
			<use xlink:href="#bootstrap" /></svg> <span class="fs-5 fw-semibold">Collapsible</span>
	</a>
	 -->
	
	<!--
	<ul class="list-unstyled ps-0">
		<li class="mb-1">
			<button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed" data-bs-toggle="collapse" data-bs-target="#home-collapse" aria-expanded="true">Home</button>
			<div class="collapse show" id="home-collapse">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li class="mb-1">
						<button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed" data-bs-toggle="collapse" data-bs-target="#dashboard-collapse" aria-expanded="false">Dashboard</button>
						<div class="collapse" id="dashboard-collapse">
							<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
								<li>
									<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Overview</a>
								</li>
								<li>
									<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Weekly</a>
								</li>
								<li>
									<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Monthly</a>
								</li>
								<li>
									<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Annually</a>
								</li>
							</ul>
						</div>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Updates</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Reports</a>
					</li>
				</ul>
			</div>
		</li>
		<li class="mb-1">
			<button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed" data-bs-toggle="collapse" data-bs-target="#dashboard-collapse" aria-expanded="false">Dashboard</button>
			<div class="collapse" id="dashboard-collapse">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Overview</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Weekly</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Monthly</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Annually</a>
					</li>
				</ul>
			</div>
		</li>
		<li class="mb-1">
			<button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed" data-bs-toggle="collapse" data-bs-target="#orders-collapse" aria-expanded="false">Orders</button>
			<div class="collapse" id="orders-collapse">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">New</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Processed</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Shipped</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Returned</a>
					</li>
				</ul>
			</div>
		</li>
		<li class="border-top my-3"></li>
		<li class="mb-1">
			<button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed" data-bs-toggle="collapse" data-bs-target="#account-collapse" aria-expanded="false">Account</button>
			<div class="collapse" id="account-collapse">
				<ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">New...</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Profile</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Settings</a>
					</li>
					<li>
						<a href="#" class="link-body-emphasis d-inline-flex text-decoration-none rounded">Sign out</a>
					</li>
				</ul>
			</div>
		</li>
	</ul>
	-->
	
	<ul id="left_menu_tree" class="list-unstyled ps-0" style="height: calc(87vh); overflow: auto;">
		
	</ul>
	
</div>
</body>
<script type="text/javascript">

	$(function() {
		let callParams = {
			folderId : '032A5E114A59D28267BDD8B6D9E58B22'
		};
		callAjaxPost('/mstr/getFolderList.json', callParams, function(data){
			let menuReport = data['folder'];
			let drawHtml = drawMenuReport(menuReport, $('<ul>', {class : 'list-unstyled ps-0'}));
			$('#left_menu_tree').append(drawHtml);
		});
	});
	
	//메뉴 리포트 동적 생성
	function drawMenuReport(menuReport, rtnHtml) {
		
		menuReport.forEach((menu, idx) => {
			/*
			let menuHtml = $('<li>', {
				  text : menu['name']
				, title : menu['name']
				, class : 'mb-1'
				, click : function(e) {
					let pagePrams = [
						  ["objectId", menu['id']]
						, ["type", menu['type']]
						, ["subType", menu['subType']]
					  	, ["isvi", menu['isVI']]
					];
					
					pageGoPost('_self', '${pageContext.request.contextPath}/app/main/reportMainView.do', pagePrams);
				}
			});
			$(rtnHtml).append(menuHtml);
			
			if(menu['child']) {
				let childHtml = drawMenuReport(menu['child'], $('<ul>'));
				let liHtml = $('<li>');
				$(liHtml).append(childHtml);
				$(rtnHtml).append(liHtml);
			}
			*/
			
			if(menu['type'] == 8) {
				let buttonHtml = $('<button>', {
					  text : menu['name']
					, id : menu['id'] + '-btn'
					, title : menu['name']
					, class : 'btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed'
					, 'data-bs-toggle' : 'collapse'
					, 'data-bs-target' : '#' + menu['id'] + '-collapse'
					, 'aria-expanded' : 'true'
				});
				
				let menuHtml = $('<li>', { class : 'mb-1'});
				
				$(menuHtml).append(buttonHtml);
				$(rtnHtml).append(menuHtml);
			} else {
				let menuHtml = $('<a>', {
					  text : menu['name']
					, title : menu['name']
					, href : '#'
					, class : 'link-body-emphasis d-inline-flex text-decoration-none rounded mstrType-' + menu['type']
					, click : function(e) {
						let pagePrams = [
							  ["objectId", menu['id']]
							, ["type", menu['type']]
							, ["subType", menu['subType']]
						  	, ["isvi", menu['isVI']]
						];
						
						pageGoPost('_self', '${pageContext.request.contextPath}/app/main/reportMainView.do', pagePrams);
					}
				});
				let liHtml = $('<li>');
				
				$(liHtml).append(menuHtml);
				$(rtnHtml).find('ul').append(liHtml);
			}
			
			if(menu['child']) {
				let divHtml = $('<div>', {
					  text : ''
					, id : menu['id'] + '-collapse'
					, class : 'collapse show'
				});
				
				let ulHtml = $('<ul>', {
					class : 'btn-toggle-nav list-unstyled fw-normal pb-1 small'
				});
				
				$(divHtml).append(ulHtml);
				
				let childHtml = drawMenuReport(menu['child'], $(divHtml));
				$(rtnHtml).find('#' + menu['id'] + '-btn').after(childHtml);
			}
			
		});
		
		
		return rtnHtml;
	}
	
</script>
</html>