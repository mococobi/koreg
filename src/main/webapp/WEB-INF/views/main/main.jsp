<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");

	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>메인화면</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		  #main_div
		, #main_div a
		, #main_div input
		, #main_div span
		, #main_div select
		, #main_div button {
			font-size: 1.5rem;
			font-family: 맑은 고딕;
		}
		
		#main_div .h3 {
			font-size: 3rem;
			font-family: 맑은 고딕;
		}
		
		#main_div .h6 {
			font-size: 2rem;
			font-family: 맑은 고딕;
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart${portalAppName}.jsp" />

<div>
	<div id="main_div" class="container py-1" style="max-width: 100%;">
		<!-- 이미지 영역 -->
		<div class="mb-4 bg-body-tertiary rounded-3">
			<img alt="메인 이미지" src="${pageContext.request.contextPath}/_custom/image/main/mainImage.png?v=20231123001" style="width: 100%; height: calc(33vh);">
		</div>

		<!-- 그리드 영역 -->
		<div class="row align-items-md-stretch" style="font-size: 2.0rem;">
			<!-- 공지사항 영역 -->
			<div class="col-md-6" style="padding: 0 10px 0 0;">
				<div class="h-100 p-4 bg-body-tertiary border rounded-3" style="min-height: calc(29.5vh);">
					<div style="display: flex;">
						<h3 style="font-size: 3rem;">공지사항</h3>
						<h3 onclick="moveCommunityPage(1)" title="더보기" style="margin-left: auto; cursor: pointer; font-size: 3rem;">+</h3>
					</div>
					<div class="table-responsive small">
						<table class="table table-striped table-sm">
							<colgroup>
								<col width="80%">
								<col width="20%">
							</colgroup>
							<thead>
								<tr>
									<th scope="col">제목</th>
									<th scope="col">등록일자</th>
								</tr>
							</thead>
							<tbody id="board_div_1">
							</tbody>
						</table>
					</div>
				</div>
			</div>
			
			<!-- FAQ 영역 -->
			<div class="col-md-6" style="padding: 0 0 0 10px;">
				<div class="h-100 p-4 bg-body-tertiary border rounded-3" style="min-height: calc(29.5vh);">
					<div style="display: flex;">
						<h3 style="font-size: 3rem;">FAQ</h3>
						<h3 onclick="moveCommunityPage(2)" title="더보기" style="margin-left: auto; cursor: pointer; font-size: 3rem;">+</h3>
					</div>
					<div class="table-responsive small">
						<table class="table table-striped table-sm">
							<colgroup>
								<col width="80%">
								<col width="20%">
							</colgroup>
							<thead>
							<tr>
									<th scope="col">제목</th>
									<th scope="col">등록일자</th>
								</tr>
							</thead>
							<tbody id="board_div_2">
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<!-- 도움말 카드 영역 -->
		<div class="container" id="hanging-icons" style="max-width: 100%; font-size: 2rem;">
			<div class="row g-4 py-4 row-cols-1 row-cols-lg-4">
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-book-fill" style="font-size: 3rem;"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis" style="font-size: 3rem;">메뉴얼</h3>
						<p style="font-size: 2rem;">포탈 메뉴얼을 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary" style="font-size: 2rem;"> 확인 </a>
					</div>
				</div>
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-box-seam-fill" style="font-size: 3rem;"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis" style="font-size: 3rem;">통계 용어 사전</h3>
						<p style="font-size: 2rem;">통계 용어 사전을 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary" style="font-size: 2rem;"> 확인 </a>
					</div>
				</div>
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-camera-reels-fill" style="font-size: 3rem;"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis" style="font-size: 3rem;">동영상 가이드</h3>
						<p style="font-size: 2rem;">동영상 가이드를 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary" style="font-size: 2rem;"> 확인 </a>
					</div>
				</div>
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-chat-square-text-fill" style="font-size: 3rem;"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis" style="font-size: 3rem;">포탈 가이드</h3>
						<p style="font-size: 2rem;">포탈 가이드를 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary" style="font-size: 2rem;"> 확인 </a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
	
<script type="text/javascript">
	$(function() {
		fnMainInit();
	});
	
	
	//초기 함수
	function fnMainInit() {
		getBoardList('1', 'board_div_1');
		getBoardList('2', 'board_div_2');
		getPopupList();
	}
	
	
	//게시물 목록
	function getBoardList(boardId, divId) {
		let callParams = {
			  BRD_ID : boardId
			, listViewCount : 5
			, start : 0
			, length : 5
		};
		callAjaxPost('/board/boardPostList.json', callParams, function(data) {
			let postData = data['data'];
			console.log(postData);
			postData.forEach((post, idx) => {
				
				let trHtml = $('<tr>');
				
				let tdTitleHtml = $('<td>', {
					  text : post['POST_TITLE']
					, title : post['POST_TITLE']
					, class : ''
					, style : 'cursor : pointer'
					, click : function(e) {
						let pagePrams = [
							  ['BRD_ID', boardId]
							, ['POST_ID', post['POST_ID']]
							
						];
						pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostDetailView.do', pagePrams);
					}
				});
				
				let tdCreateDateHtml = $('<td>', {
					  text : changeDisplayDate(post['CRT_DT_TM'], 'YYYY-MM-DD')
					, title : post['POST_TITLE']
					, class : ''
					, style : 'cursor : pointer'
					, click : function(e) {
						let pagePrams = [
							  ['BRD_ID', boardId]
							, ['POST_ID', post['POST_ID']]
							
						];
						pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostDetailView.do', pagePrams);
					}
				});
				
				$(trHtml).append(tdTitleHtml);
				$(trHtml).append(tdCreateDateHtml);
				
				
				$('#' + divId).append(trHtml);
			});
		});
	}
	
	
	function getPopupList() {
		let callParams = {};
		callAjaxPost('/board/boardPostPopupList.json', callParams, function(data) {
			let postData = data['data'];
			let postFile = data['file'];
			
			let w = 750;
			let h = 600;
			
			let xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
			xPos += window.screenLeft; // 듀얼 모니터일 때
			let yPos = (document.body.offsetHeight/2) - (h/2);
			
			$.each(postData, function(idx, item) {
				let isPopup = handleCookie.getCookie('PopUp'+item['POST_ID']);
				
				if (isPopup == 'false'){
					// 게시글에대한 쿠키가 있고 그 설정 값이 표시 안함일때
				}else{
					//1초 정도 딜레이 주는거
					setTimeout(function() {
						let popup = window.open('${pageContext.request.contextPath}/app/board/boardPostPopupView.do'+'?BRD_ID='+item['BRD_ID'] + '&POST_ID='+item['POST_ID'], idx, "width="+w+", height="+h+", left="+(xPos + (idx*30))+", top="+(yPos + (idx*30))+",resizable=yes");
						popup.focus();
					},1000);
				}
			});
		});
		
	}
	
	
	
</script>
</body>
</html>