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
		
		  .board_custom_div
		, .board_custom_div a
		, .board_custom_div input
		, .board_custom_div span
		, .board_custom_div select
		, .board_custom_div button {
			font-size: 1.5rem;
			font-family: 맑은 고딕;
		}
		
		.board_custom_div .h3 {
			font-size: 3rem;
			font-family: 맑은 고딕;
		}
		
		.board_custom_div .h6 {
			font-size: 2rem;
			font-family: 맑은 고딕;
		}
		
		table.dataTable td.focus {
	        outline: 1px solid #ac1212;
	        outline-offset: -3px;
	        background-color: #f8e6e6 !important;
	    }
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/adminDivStart.jsp" />
	
	<div class="container py-4 board_custom_div" style="max-width: 100%;">
		<p class="h3">게시판 관리</p>
		<p class="h6">게시판을 관리할 수 있습니다.</p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_board_modify" class="btn btn-secondary btn-sm" onclick="createBoard()">저장</button>
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
						<input type="text" id="board_id" class="form-control form-control-sm" title="게시판 ID" placeholder="게시판 ID를 입력하세요" disabled="disabled">
					</td>
				</tr>
				<tr>
					<td>
						<span>게시판 이름</span>
					</td>
					<td>
						<input type="text" id="board_nm" class="form-control form-control-sm" title="게시판 이름" placeholder="게시판 이름을 입력하세요">
					</td>
				</tr>
				<tr>
					<td>
						<span>게시판 설명</span>
					</td>
					<td>
						<input type="text" id="board_desc" class="form-control form-control-sm" title="게시판 설명" placeholder="게시판 설명을 입력하세요">
					</td>
				</tr>
				<tr>
					<td>
						<span>게시판 타입</span>
					</td>
					<td>
						<input name="board_type" type="radio" value="COMMON" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">일반</label>
             	 		<input name="board_type" type="radio" value="FAQ" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">FAQ</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시물 작성 권한</span>
					</td>
					<td>
						<button id="popup_board_create_auth" type="button" class="btn btn-primary">편집</button>
						<span id="board_create_auth"></span>
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
						<input name="post_file_yn" type="radio" value="Y" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">가능</label>
             	 		<input name="post_file_yn" type="radio" value="N" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">불가능</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시물 댓글 가능 여부</span>
					</td>
					<td>
						<input name="post_cmnt_yn" type="radio" value="Y" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">가능</label>
             	 		<input name="post_cmnt_yn" type="radio" value="N" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">불가능</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>게시물 팝업 가능 여부</span>
					</td>
					<td>
						<input name="post_popup_yn" type="radio" value="Y" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">가능</label>
             	 		<input name="post_popup_yn" type="radio" value="N" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">불가능</label>
					</td>
				</tr>
				<tr>
					<td>
						<span>삭제 여부</span>
					</td>
					<td>
						<input name="del_yn" type="radio" value="Y" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">Y</label>
             	 		<input name="del_yn" type="radio" value="N" class="form-check-input" required="">
             	 		<label class="form-check-label" for="credit">N</label>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/modal/editUserList.jsp" />
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	
	$(function() {
		if(boardId != null) {
			//수정
			fnBoardInit();
		} else {
			//신규
			$('#board_id').val('시스템 자동 채번');
			
			$("input:radio[name='board_type']:radio[value='COMMON']").prop('checked', true);
			$("input:radio[name='post_file_yn']:radio[value='N']").prop('checked', true);
			$("input:radio[name='post_cmnt_yn']:radio[value='N']").prop('checked', true);
			$("input:radio[name='post_popup_yn']:radio[value='N']").prop('checked', true);
			$("input:radio[name='del_yn']:radio[value='N']").prop('checked', true);
		}
		
		
		$('#popup_board_create_auth').on('click', function(e) {
			$('#editUserListModal').modal('show');
			
			setTimeout(() => {
				fnUserInit('modalUserListTable');
    		}, 100);
		});
		
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
		$('#board_id').val(boardData['BRD_ID']);
		$('#board_nm').val(boardData['BRD_NM']);
		$('#board_desc').val(boardData['BRD_DESC']);
		$('input:radio[name="board_type"]:radio[value="'+ boardData['BRD_TYPE'] +'"]').prop('checked', true);
		
		let boardCreateAuth = boardData['BRD_CRT_AUTH'];
		if(boardData['BRD_CRT_AUTH'] == null || boardData['BRD_CRT_AUTH'] == 'null') {
			boardCreateAuth = [];
		}
		$('#board_create_auth').text(getUserListSpanName(boardCreateAuth));
		modalUserList = boardCreateAuth;
		
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
	
	
	//입력 정보 확인 체크
	function checkPostInput() {
		let rtnCheck = true;
		
		if($('#board_id').val() == '') {
			alert('게시판 ID를 입력하세요');
			$('#board_id').focus();
			return false;
		}
		
		if($('#board_nm').val() == '') {
			alert('게시판 이름을 입력하세요');
			$('#board_nm').focus();
			return false;
		}
		
		return rtnCheck;
	}
	
	
	//게시판 등록
	function createBoard() {
		let checkVal = checkPostInput();
		
		if(checkVal) {
			let msg = '게시판을 등록하시겠습니까?';
			if (confirm(msg)) {
				let formData = new FormData();
				
				formData.append('BRD_ID', boardId);
				formData.append('BRD_NM', $('#board_nm').val());
				formData.append('BRD_DESC', $('#board_desc').val());
				formData.append('BRD_TYPE', $('input:radio[name="board_type"]:checked').val());
				
				formData.append('BRD_CRT_AUTH', modalUserList);
				
				formData.append('POST_FILE_YN', $('input:radio[name="post_file_yn"]:checked').val());
				formData.append('POST_CMNT_YN', $('input:radio[name="post_cmnt_yn"]:checked').val());
				formData.append('POST_POPUP_YN', $('input:radio[name="post_popup_yn"]:checked').val());
				formData.append('DEL_YN', $('input:radio[name="del_yn"]:checked').val());
				
				if(boardId != null) {
					//수정
					callAjaxForm('/admin/boardUpdate.json', formData, function(data) {
						alert('게시판이 수정되었습니다.');

						let pagePrams = [
							["boardId", data['BRD_ID']]
						];
						pageGoPost('_self', '${pageContext.request.contextPath}/app/admin/boardDetailView.do', pagePrams);
					});
				} else {
					//신규
					callAjaxForm('/admin/boardInsert.json', formData, function(data) {
						alert('게시판이 등록되었습니다.');

						let pagePrams = [
							["boardId", data['BRD_ID']]
						];
						pageGoPost('_self', '${pageContext.request.contextPath}/app/admin/boardDetailView.do', pagePrams);
					});
				}
		    }
		}
	}
	
	
</script>
</html>