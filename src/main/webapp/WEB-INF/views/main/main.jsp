<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>메인</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div style="text-align: center;">
		<!-- 포탈 메인 이미지 영역 -->
		<img alt="메인 이미지" src="${pageContext.request.contextPath}/image/main/mainImage.png?v=20231123001" style="width: 600px;">
	</div>
	<div style="min-height: 300px;">
		<!-- 공지사항 영역 -->
		<div style="float: left; width: 50%;">
			<p style="float: left;">공지사항</p>
			<p onclick="moveCommunityPage(1)" style="cursor: pointer; margin-left: 100px;">+</p>
			<div id="board_div_1"></div>
		</div>
		<!-- FAQ 영역 -->
		<div style="float: left; width: 50%;">
			<p style="float: left;">FAQ</p>
			<p onclick="moveCommunityPage(2)" style="cursor: pointer; margin-left: 100px;">+</p>
			<div id="board_div_2"></div>
		</div>
	</div>
	<div>
		<!-- 도움말 카드 영역 -->
		<div onclick="detailBoardPost(1,1)" style="float: left; width: 25%; cursor: pointer;">
			메뉴얼
		</div>
		<div onclick="detailBoardPost(1,1)" style="float: left; width: 25%; cursor: pointer;">
			통계 용어 사전
		</div>
		<div onclick="detailBoardPost(1,1)" style="float: left; width: 25%; cursor: pointer;">
			동영상 가이드
		</div>
		<div onclick="detailBoardPost(1,1)" style="float: left; width: 25%; cursor: pointer;">
			포탈 가이드
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
				let postHtml = $('<p>', {
					  text : post['POST_TITLE'] + '  ' + changeDisplayDate(post['CRT_DT_TM'], 'YYYY-MM-DD')
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
				
				$('#' + divId).append(postHtml);
			});
		});
	}
</script>
</body>
</html>