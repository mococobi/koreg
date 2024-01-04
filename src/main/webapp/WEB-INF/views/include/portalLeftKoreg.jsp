<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<% 
	String portalIframePageMoveYn = CustomProperties.getProperty("portal.iframe.page.move.yn");
	String mstrMenuFolderId = CustomProperties.getProperty("mstr.menu.folder.id");
	
	pageContext.setAttribute("portalIframePageMoveYn", portalIframePageMoveYn);
	pageContext.setAttribute("mstrMenuFolderId", mstrMenuFolderId);
%>
<!DOCTYPE html>
<html>
<head>
</head>
<body>
	<nav class="lnb" style="position: relative; overflow: auto; height: calc(100vh - 122px);">
	</nav>
	<!-- //Left Menu -->
</body>
<script type="text/javascript">
	
	$(function() {
		initLeftMenu();
		
        /* 컨텐츠 탭 */
        /*
        $('.tab-btn-wrap button').each(function(index, item) {
            $(item).on('click', function(){
                $('.tab-btn-wrap button').removeClass('active');
                $(this).addClass('active');

                var idx = index
                var showCont = $('.cont-tab')[idx];
                $('.cont-tab').removeClass('on');
                $(showCont).addClass('on');
            });
        });
        */
        
        /* box1 탭 */
        $('.box-btn-wrap button').each(function(index, item) {
            $(item).on('click', function(){
                $('.box-btn-wrap button').removeClass('active');
                $(this).addClass('active');

                var idx = index
                var showCont = $('.box-tab')[idx];
                $('.box-tab').removeClass('on');
                $(showCont).addClass('on');
            });
        });
	});
	
	
	//초기함수
	function initLeftMenu() {
		$(window).resize(function() {
			let height	= $(window).height();
// 			$('.lnb').height(height - $('.header').height() - $('.tab-btn-wrap.flex').height() - 100);
		});
		
		$(window).resize();
		
		let callParams = {
			folderId : '${mstrMenuFolderId}'
		};
		callAjaxPost('/mstr/getFolderList.json', callParams, function(data) {
			let drawHtml = drawMenuParentReport(data['folder'], $('<ul>', {class : 'dep1-ul'}));
			$('.lnb').append(drawHtml);
			
			
	        /* 메뉴 */
	        $('.dep2').on('mouseenter', function(e) {
	        	$('.dep3-wrap').css('position', 'absolute');
	        	$('.dep3-wrap').removeClass('on');
	        	
	            $(this).find('.dep3-wrap').addClass('on');
	            $(this).find('.dep3-wrap').css('position', 'fixed');
	            $(this).find('.dep3-wrap').css('top', $(this).offset().top);
	            $(this).find('.dep3-wrap').css('left', $(this).offset().left + 176);
	            
	            $(this).find('.dep3-wrap').width($(this).find('.dep3-ul').width());
	            $(this).find('.dep3-wrap').height($(this).find('.dep3-ul').height());
	        });
	        
	        /*
	        $('.dep2').on('mouseleave', function(e){
	            $(this).find('.dep3-wrap').removeClass('on');
	        });
	        */
	        
	        $('.dep3-wrap').on('mouseleave', function(e) {
	            $(this).css('position', 'absolute');
	            $(this).removeClass('on');
	        });
	        
	        $('#mstrReport').on('mouseenter', function(e) {
	        	$('.dep3-wrap').css('position', 'absolute');
	        	$('.dep3-wrap').removeClass('on');
	        });
		});
	}
	
	
	//메뉴 리포트 동적 생성
	function drawMenuParentReport(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
			
			let depLiHtml = $('<li>', {class : 'dep1'});
			let aHtml = $('<a>', {
				  href : 'javascript:void(0)'
				, title : getMstrTitleName(menu['name'])
				, text : getMstrTitleName(menu['name'])
				, click : function(e) {
					clickReportObj(menu);
				}
			});
			depLiHtml.append(aHtml);
			
			if(menu['child']) {
				let childHtml = drawMenuChildDep2Report(menu['child'], $('<ul>', {class : 'dep2-ul'}));
				depLiHtml.append(childHtml);
			}
			
			$(rtnHtml).append(depLiHtml);
		});
		
		return rtnHtml;
	}
	
	
	//2레벨 태그 생성
	function drawMenuChildDep2Report(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
			let depLiHtml = $('<li>', {class : 'dep2'});
			let aHtml = $('<a>', {
				  href : '#'
				, title : getMstrTitleName(menu['name'])
				, text : getMstrTitleName(menu['name'])
				, click : function(e) {
					clickReportObj(menu);
				}
			});
			depLiHtml.append(aHtml);
			
			if(menu['child']) {
				let divHtml = $('<div>', {
					  class : 'dep3-wrap'
					, style : 'z-index: 9001;'
				});
				let childHtml = drawMenuChildDep3Report(menu['child'], $('<ul>', {class : 'dep3-ul'}));
				divHtml.append(childHtml);
				depLiHtml.append(divHtml);
			}
			
			$(rtnHtml).append(depLiHtml);
		});
		
		return rtnHtml;
	}
	
	
	//3레벨 태그 생성
	function drawMenuChildDep3Report(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
			let depLiHtml = $('<li>', {class : 'dep3'});
			let aHtml = $('<a>', {
				  href : '#'
				, title : getMstrTitleName(menu['name'])
				, text : getMstrTitleName(menu['name'])
				, click : function(e) {
					clickReportObj(menu);
				}
			});
			depLiHtml.append(aHtml);
			$(rtnHtml).append(depLiHtml);
		});
		
		return rtnHtml;
	}
	
	
	//리포트 클릭
	function clickReportObj(menu) {
		if(menu['type'] == 8) {
			//폴더
		} else {
			let pagePrams = [
				  ["objectId", menu['id']]
				, ["type", menu['type']]
				, ["subType", menu['subType']]
			  	, ["isvi", menu['isVI']]
				, ["title", encodeURI(menu['name'])]
			];
			
			if('${portalIframePageMoveYn}' == 'Y') {
				pageGoPost('_self', '${pageContext.request.contextPath}/app/main/reportMainView.do', pagePrams, 1);
			} else {
				if($('#mstrReport').length == 0) {
					pageGoPost('_self', '${pageContext.request.contextPath}/app/main/reportMainView.do', pagePrams, 1);
				} else {
					objectId = menu['id'];
					type = menu['type'];
					isvi = menu['isVI'];
					
					$('#mstrReportTitle').text(getMstrTitleName(menu['name']));
					fnReportInit();
				}
			}
		}
	}
	
</script>
</html>