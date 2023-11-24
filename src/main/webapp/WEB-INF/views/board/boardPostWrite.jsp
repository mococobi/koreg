<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String boardId = (String)request.getParameter("boardId");
	String postId = (String)request.getParameter("postId");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>게시판 - 글쓰기</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div id="boardTable_div">
		<p>${postData['BRD_NM']}</p>
		<p>${postData['BRD_DESC']}</p>
	</div>
	<button onclick="createPost()">작성</button>
	<div>
		<p>제목</p><input type="text" id="post_title" title="제목">
	</div>
	<div>
		<p>내용</p><input type="text" id="post_content" title="내용">
	</div>
	<c:if test="${postData['POST_POPUP_YN'] eq 'Y'}">
		<div>
			<p>팝업 여부</p>
			<input type="checkbox" id="post_popup_yn">
			<input type="text" id="post_popup_start_dt">
			<input type="text" id="post_popup_end_dt">
		</div>
	</c:if>
	<c:if test="${postData['POST_SECRET_YN'] eq 'Y'}">
		<div>
			<p>비밀글 여부</p>
			<input type="checkbox" id="post_secret_yn">
		</div>
	</c:if>
	<c:if test="${postData['POST_FIX_YN'] eq 'Y'}">
		<div>
			<p>상단 고정 여부</p>
			<input type="checkbox" id="post_fix_yn">
		</div>
	</c:if>
	<c:if test="${postData['POST_FILE_YN'] eq 'Y'}">
		<div>
			<p>첨부파일</p>
		</div>
	</c:if>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	
	$(function() {
		fnBoardInit();
	});
	
	
	//초기 함수
	function fnBoardInit() {
		
	}
	
	
	//입력 정보 확인 체크
	function checkPostInput() {
		let rtnCheck = true;
		
		if($('#post_title').val() == '') {
			alert('제목을 입력하세요');
			$('#post_title').focus();
			return false;
		}
		
		if($('#post_content').val() == '') {
			alert('내용을 입력하세요');
			$('#post_content').focus();
			return false;
		}
		
		return rtnCheck;
	}
	
	
	//게시글 작성
	function createPost() {
		let checkVal = checkPostInput();
		
		if(checkVal) {
			let msg = '게시글을 등록하시겠습니까?';
			if (confirm(msg)) {
				let formData = new FormData();
				
				formData.append('BRD_ID', boardId);
				formData.append('POST_TITLE', $('#post_title').val());
				formData.append('POST_CONTENT', $('#post_content').val());
				
				callAjaxForm('/board/boardPostInsert.json', formData, function(data) {
					alert('게시글이 등록되었습니다.');
					moveCommunityPage(boardId);
				});
		    }
		}
	}
	
	
</script>
</html>