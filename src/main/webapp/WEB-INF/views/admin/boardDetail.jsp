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
	<title>${postData['BRD_NM']}</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		#board_table th, #board_table td {
		    border: 1px solid;
		}
		.themed-grid-col {
		  padding-top: .75rem;
		  padding-bottom: .75rem;
/* 		  background-color: rgba(112.520718, 44.062154, 249.437846, .15); */
		  border: 1px solid rgba(112.520718, 44.062154, 249.437846, .3);
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/adminDivStart.jsp" />
	
	<div class="container py-4">
		<p class="h3">게시판 관리</p>
		<p class="h6">게시판을 관리할 수 있습니다.</p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_board_modify" class="btn btn-secondary btn-sm" onclick="modifyBoard()">수정</button>
			</div>
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm" onclick="moveAdminPage('BOARD_ADMIN')">목록</button>
			</div>
	    </div>
	    
    	<table id="board_table" class="table table-sm table-bordered" style="width: 100%; border: 1px solid; border-collapse: collapse;">
			<colgroup>
				<col width="20%">
				<col width="">
			</colgroup>
			<tbody>
				<tr>
					<td>
						<span>게시판 ID</span>
					</td>
					<td>
						<span id="board_id"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시판 이름</span>
					</td>
					<td>
						<span id="board_nm"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시판 설명</span>
					</td>
					<td>
						<span id="board_desc"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시판 타입</span>
					</td>
					<td>
						<input name="board_type" type="radio" value="COMMON" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">일반</label>
             	 		<input name="board_type" type="radio" value="FAQ" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">FAQ</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>생성일시</span>
					</td>
					<td>
						<span id="board_create_date"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>생성자 ID</span>
					</td>
					<td>
						<span id="board_create_user_id"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>수정일시</span>
					</td>
					<td>
						<span id="board_modify_date"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>수정자 ID</span>
					</td>
					<td>
						<span id="board_modify_user_id"></span>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시물 파일 첨부 여부</span>
					</td>
					<td>
						<input name="post_file_yn" type="radio" value="Y" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">가능</label>
             	 		<input name="post_file_yn" type="radio" value="N" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">불가능</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시물 댓글 가능 여부</span>
					</td>
					<td>
						<input name="post_cmnt_yn" type="radio" value="Y" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">가능</label>
             	 		<input name="post_cmnt_yn" type="radio" value="N" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">불가능</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시물 팝업 가능 여부</span>
					</td>
					<td>
						<input name="post_popup_yn" type="radio" value="Y" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">가능</label>
             	 		<input name="post_popup_yn" type="radio" value="N" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">불가능</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>삭제 여부</span>
					</td>
					<td>
						<input name="del_yn" type="radio" value="Y" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">Y</label>
             	 		<input name="del_yn" type="radio" value="N" class="form-check-input" required="" onclick="return false;">
             	 		<label class="form-check-label" for="credit">N</label>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>

<script type="text/javascript">
	var boardId = <%=boardId%>;
	
	$(function() {
		fnBoardInit();
	});
	
	
	//초기 함수
	function fnBoardInit() {
		let callParams = {
			  boardId : boardId
		};
		callAjaxPost('/admin/boardDetail.json', callParams, function(data){
			let postData = data['data'];
			displayContents(postData);
		});
		
	}
	
	
	//내용 표시
	function displayContents(boardData) {
		$('#board_id').text(boardData['BRD_ID']);
		$('#board_nm').text(boardData['BRD_NM']);
		$('#board_desc').text(boardData['BRD_DESC']);
		$('input:radio[name="board_type"]:radio[value="'+ boardData['BRD_TYPE'] +'"]').prop('checked', true);
		
		$('#board_create_date').text(changeDisplayDate(boardData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#board_create_user_id').text(boardData['CRT_USR_ID']);
		$('#board_modify_date').text(changeDisplayDate(boardData['MOD_DT_TM'], 'YYYY-MM-DD'));
		$('#board_modify_user_id').text(boardData['MOD_USR_ID']);
		
		if(boardData['POST_FILE_YN'] == 'Y') {
			$('input:radio[name="post_file_yn"]:radio[value="Y"]').prop('checked', true);
		} else {
			$('input:radio[name="post_file_yn"]:radio[value="N"]').prop('checked', true);
		}
		
		if(boardData['POST_CMNT_YN'] == 'Y') {
			$('input:radio[name="post_cmnt_yn"]:radio[value="Y"]').prop('checked', true);
		} else {
			$('input:radio[name="post_cmnt_yn"]:radio[value="N"]').prop('checked', true);
		}
		
		if(boardData['POST_POPUP_YN'] == 'Y') {
			$('input:radio[name="post_popup_yn"]:radio[value="Y"]').prop('checked', true);
		} else {
			$('input:radio[name="post_popup_yn"]:radio[value="N"]').prop('checked', true);
		}
		
		if(boardData['DEL_YN'] == 'Y') {
			$('input:radio[name="del_yn"]:radio[value="Y"]').prop('checked', true);
		} else {
			$('input:radio[name="del_yn"]:radio[value="N"]').prop('checked', true);
		}
		
	}
	
	
	//게시판 수정
	function modifyBoard() {
		let pagePrams = [
			["boardId", boardId]
		];
		
		pageGoPost('_self', '${pageContext.request.contextPath}/app/admin/boardWriteView.do', pagePrams);
	}
	
	
</script>
</html>