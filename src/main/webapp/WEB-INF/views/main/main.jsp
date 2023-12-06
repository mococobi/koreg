<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
		.feature-icon {
		  width: 4rem;
		  height: 4rem;
		  border-radius: .75rem;
		}
		
		.icon-square {
		  width: 3rem;
		  height: 3rem;
		  border-radius: .75rem;
		}
		
		.text-shadow-1 { text-shadow: 0 .125rem .25rem rgba(0, 0, 0, .25); }
		.text-shadow-2 { text-shadow: 0 .25rem .5rem rgba(0, 0, 0, .25); }
		.text-shadow-3 { text-shadow: 0 .5rem 1.5rem rgba(0, 0, 0, .25); }
		
		.card-cover {
		  background-repeat: no-repeat;
		  background-position: center center;
		  background-size: cover;
		}
		
		.feature-icon-small {
		  width: 3rem;
		  height: 3rem;
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />

<div>
	<div class="container py-1">
		<!-- 이미지 영역 -->
		<div class="mb-4 bg-body-tertiary rounded-3">
			<img alt="메인 이미지" src="${pageContext.request.contextPath}/image/main/mainImage.png?v=20231123001" style="width: 100%; height: calc(24vh);">
		</div>

		<!-- 그리드 영역 -->
		<div class="row align-items-md-stretch">
			<!-- 공지사항 영역 -->
			<div class="col-md-6">
				<div class="h-100 p-4 bg-body-tertiary border rounded-3">
					<div style="display: flex;">
						<h3>공지사항</h3>
						<h3 onclick="moveCommunityPage(1)" title="더보기" style="margin-left: auto; cursor: pointer;">+</h3>
					</div>
					<div class="table-responsive small">
						<table class="table table-striped table-sm" style="min-height: calc(22.5vh);">
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
			<div class="col-md-6">
				<div class="h-100 p-4 bg-body-tertiary border rounded-3">
					<div style="display: flex;">
						<h3>FAQ</h3>
						<h3 onclick="moveCommunityPage(2)" title="더보기" style="margin-left: auto; cursor: pointer;">+</h3>
					</div>
					<div class="table-responsive small">
						<table class="table table-striped table-sm" style="min-height: calc(22.5vh);">
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
		<div class="container" id="hanging-icons">
			<div class="row g-4 py-4 row-cols-1 row-cols-lg-4">
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-book-fill"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis">메뉴얼</h3>
						<p>포탈 메뉴얼을 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary"> 확인 </a>
					</div>
				</div>
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-box-seam-fill"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis">통계 용어 사전</h3>
						<p>통계 용어 사전을 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary"> 확인 </a>
					</div>
				</div>
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-camera-reels-fill"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis">동영상 가이드</h3>
						<p>동영상 가이드를 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary"> 확인 </a>
					</div>
				</div>
				<div class="col d-flex align-items-start">
					<div class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
						<i class="bi bi-chat-square-text-fill"></i>
					</div>
					<div>
						<h3 class="text-body-emphasis">포탈 가이드</h3>
						<p>포탈 가이드를 확인합니다.</p>
						<a href="#" onclick="detailBoardPost(1,1)" class="btn btn-primary"> 확인 </a>
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
	}
	
	
	//게시물 목록
	function getBoardList(boardId, divId) {
		let callParams = {
			  boardId : boardId
			, listViewCount : 5
			, start : 0
			, length : 5
		};
		callAjaxPost('/board/boardPostList.json', callParams, function(data) {
			let postData = data['data'];
			
			postData.forEach((post, idx) => {
				
				let trHtml = $('<tr>');
				
				let tdTitleHtml = $('<td>', {
					  text : post['POST_TITLE']
					, title : post['POST_TITLE']
					, class : ''
					, style : 'cursor : pointer'
					, click : function(e) {
						let pagePrams = [
							  ['boardId', boardId]
							, ['postId', post['POST_ID']]
							
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
							  ['boardId', boardId]
							, ['postId', post['POST_ID']]
							
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
</script>
</body>
</html>