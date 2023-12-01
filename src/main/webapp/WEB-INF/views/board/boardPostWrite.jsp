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
	
	<div class="container py-4">
		<p class="h3">${postData['BRD_NM']}</p>
		<p class="h6">${postData['BRD_DESC']}</p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_post_modify" class="btn btn-secondary btn-sm" onclick="createBoardPost()" style="display: none;">저장</button>
			</div>
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm" onclick="moveCommunityPage(<%=boardId%>)">목록</button>
			</div>
	    </div>
	    
    	<table id="board_table" class="table table-sm table-bordered" style="width: 100%; border: 1px solid; border-collapse: collapse;">
			<colgroup>
				<col width="12.5%">
				<col width="12.5%">
				<col width="12.5%">
				<col width="12.5%">
				<col width="12.5%">
				<col width="12.5%">
				<col width="12.5%">
				<col width="12.5%">
			</colgroup>
			<tbody>
				<tr>
					<td>
						<span>제목</span>
					</td>
					<td colspan="7">
						<input type="text" id="post_title" class="form-control form-control-sm" title="제목" placeholder="제목을 입력하세요">
					</td>
				</tr>
				<tr>
					<td>
						<span>작성자</span>
					</td>
					<td>
						<span id="post_create_user_id"></span>
					</td>
					<td>
						<span>부서</span>
					</td>
					<td>
						<span id="post_create_user_dept_name"></span>
					</td>
					<td>
						<span>작성일자</span>
					</td>
					<td>
						<span id="post_create_date"></span>
					</td>
					<td>
						<span>조회</span>
					</td>
					<td>
						<span id="post_count"></span>
					</td>
				</tr>
				<c:if test="${postData['POST_POPUP_YN'] eq 'Y'}">
					<tr id="post_popup_yn_div">
						<td>
							<span>팝업여부</span>
						</td>
						<td class="text-center">
							<input type="checkbox" id="post_popup_yn">
						</td>
						<td>
							<span>팝업일자</span>
						</td>
						<td colspan="5">
							<span id="post_popup_dt"></span>
						</td>
					</tr>
				</c:if>
				<tr>
					<c:if test="${postData['POST_FIX_YN'] eq 'Y'}">
						<td  id="post_fix_yn_div">
							<span>상단 고정</span>
						</td>
						<td>
							<input type="checkbox" id="post_fix_yn" disabled>
						</td>
					</c:if>
					<c:if test="${postData['POST_SECRET_YN'] eq 'Y'}">
						<td id="post_secret_yn_div">
							<span>비밀글</span>
						</td>
						<td>
							<input type="checkbox" id="post_secret_yn" disabled>
						</td>
					</c:if>
					
				</tr>
					<c:if test="${postData['BRD_VIEW_AUTH'] eq 'Y'}">
						<td>
							<span>보기 권한</span>
						</td>
						<td colspan="7"></td>
					</c:if>
				<tr>
				</tr>
				<tr>
					<td>
						<span>내용</span>
					</td>
					<td colspan="7">
						<div id="post_content" style="min-height: 300px;"></div>
					</td>
				</tr>
				<c:if test="${postData['POST_FILE_YN'] eq 'Y'}">
					<tr id="post_file_yn_div">
						<td>
							<span>첨부 파일</span>
						</td>
						<td colspan="7">
							<div id="post_file" style=""></div>
						</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		
		<div class="row mb-1">
			<div class="col-1">
				<span>이전글</span>
			</div>
			<div class="col-1">
			</div>
	    </div>
	    <div class="row">
			<div class="col-1">
				<span>다음글</span>
			</div>
			<div class="col-1">
			</div>
	    </div>
	</div>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	
	$(function() {
		if(postId != null) {
			//수정
			fnBoardPostInit();
		} else {
			//신규
			
		}
	});
	
	
	//초기 함수
	function fnBoardPostInit() {
		let callParams = {
			  boardId : boardId
			, postId : postId
		};
		callAjaxPost('/board/boardPostDetail.json', callParams, function(data){
			let postData = data['data'];
			
			//displayCheck(postData);
			displayContents(postData);
		});
	}
	
	
	//내용 표시
	function displayContents(postData) {
		
		if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}') {
			$('#btn_post_modify').show();
			$('#btn_post_delete').show();
		}
		
		$('#post_title').val(postData['POST_TITLE']);
		
		$('#post_create_user_id').text(postData['CRT_USR_ID']);
		$('#post_create_user_dept_name').text(postData['CRT_USR_DEPT_NM']);
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_count').text(postData['POST_COUNT']);
		
		$('#post_content').html(postData['POST_CONTENT']);
		
		if(postData['POPUP_YN'] == 'Y') {
			$('#post_popup_yn').prop('checked', true);
			$('#post_popup_span').show();
		} else {
			$('#post_popup_yn').prop('checked', false);
			$('#post_popup_span').hide();
		}
		
		if(postData['POPUP_START_DT_TM'] || postData['POPUP_END_DT_TM']) {
			$('#post_popup_dt').text(postData['POPUP_START_DT_TM'] + ' ~ ' + postData['POPUP_END_DT_TM']);
		}
		
		if(postData['SECRET_YN'] == 'Y') {
			$('#post_secret_yn').prop('checked', true);
		} else {
			$('#post_secret_yn').prop('checked', false);
		}
		
		if(postData['FIX_YN'] == 'Y') {
			$('#post_fix_yn').prop('checked', true);
		} else {
			$('#post_fix_yn').prop('checked', false);
		}
		
		//TODO 첨부파일 리스트 표시
		if(postData['file']) {
			postData.forEach((attachFile, idx) => {
				
			});
		}
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
	
	
	//게시글 등록
	function createBoardPost() {
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