<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<% 
	String portalIframePageMoveYn = CustomProperties.getProperty("portal.iframe.page.move.yn");
	String mstrMenuFolderId = CustomProperties.getProperty("mstr.menu.folder.id");
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
			    content: url(${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/file-earmark-bar-graph.svg);
			    transition: transform .35s ease;
			    transform-origin: 0.5em 50%;
			    margin-right: 5px;
			}
			
			.mstrType-3::before {
			    width: 1.25em;
			    line-height: 0;
			    content: url(${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/grid-3x3.svg);
			    transition: transform .35s ease;
			    transform-origin: 0.5em 50%;
			    margin-right: 5px;
			}
			
		</style>
	</head>
<body>
<div class="flex-shrink-0 p-3" style="">
	<ul id="left_menu_tree" class="list-unstyled ps-0" style="height: calc(75vh); overflow: auto;">
	</ul>
</div>
</body>
<script type="text/javascript">
	var portalIframePageMoveYn = "<%=portalIframePageMoveYn%>"
	
	
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
		
		let callParams = {
			folderId : "<%=mstrMenuFolderId%>"
		};
		callAjaxPost('/mstr/getFolderList.json', callParams, function(data) {
			let menuReport = data['folder'];
			let drawHtml = drawMenuReport(menuReport, $('<ul>', {class : 'list-unstyled ps-0'}));
			$('#left_menu_tree').append(drawHtml);
		});
	}
	
	
	//메뉴 리포트 동적 생성
	function drawMenuReport(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
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
						
						if(portalIframePageMoveYn == 'Y') {
							pageGoPost('_self', '${pageContext.request.contextPath}/app/main/reportMainView.do', pagePrams);
						} else {
							if($('#mstrReport').length == 0) {
								pageGoPost('_self', '${pageContext.request.contextPath}/app/main/reportMainView.do', pagePrams);
							} else {
								objectId = menu['id'];
								type = menu['type'];
								isvi = menu['isVI'];
								$('.prompt-wrapper').parent().show();
								getPromptInfo();
							}
						}
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