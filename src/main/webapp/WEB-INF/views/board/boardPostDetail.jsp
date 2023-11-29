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
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div>
		<h3>${postData['BRD_NM']}</h3>
		<span>${postData['BRD_DESC']}</span>
	</div>
	<div>
		<button onclick="moveCommunityPage(<%=boardId%>)">목록</button>
		<button id="btn_post_modify" onclick="modifyBoardPost()" style="display: none;">수정</button>
		<button id="btn_post_delete" onclick="deleteBoardPost()" style="display: none;">삭제</button>
	</div>
	<table id="board_table" style="width: 100%; border: 1px solid; border-collapse: collapse;">
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
					<span id="post_title"></span>
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
					<td>
						<input type="checkbox" id="post_popup_yn" disabled>
					</td>
					<td>
						<span>팝업일자</span>
					</td>
					<td colspan="5">
						<span id="post_popup_start_dt"></span>
						<span id="post_popup_span" style="display: none;"> ~ </span>
						<span id="post_popup_end_dt"></span>
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
				<%-- 
				<td>
					<span>보기 권한</span>
				</td>
				<td colspan="3"></td>
				--%>
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
						<div id="post_file" style="min-height: 100px;"></div>
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	<div>
		<div>
			<span>이전글</span>
		</div>
		<div>
			<span>다음글</span>
		</div>
	</div>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>

<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	let searchKey = '';
	let searchVal = '';
	
	$(function() {
		fnBoardInit();
	});
	
	
	//초기 함수
	function fnBoardInit() {
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
	
	
	//기능 숨김 및 표시 처리
	function displayCheck(postData) {
		//팝업 여부
		if(postData['POST_POPUP_YN'] == 'Y') {
			$('#post_popup_yn_div').show();
		} else {
			$('#post_popup_yn_div').hide();
		}
		
		//비밀글 여부
		if(postData['POST_SECRET_YN'] == 'Y') {
			$('#post_secret_yn_div').show();
		} else {
			$('#post_secret_yn_div').hide();
		}
		
		//상단 고정 여부
		if(postData['POST_FIX_YN'] == 'Y') {
			$('#post_fix_yn_div').show();
		} else {
			$('#post_fix_yn_div').hide();
		}
		
		//첨부 파일 여부
		if(postData['POST_FILE_YN'] == 'Y') {
			$('#post_file_yn_div').show();
		} else {
			$('#post_file_yn_div').hide();
		}
	}
	
	
	//내용 표시
	function displayContents(postData) {
		
		if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}') {
			$('#btn_post_modify').show();
			$('#btn_post_delete').show();
		}
		
		
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_create_user_id').text(postData['CRT_USR_ID']);
		$('#post_title').text(postData['POST_TITLE']);
		$('#post_content').html(postData['POST_CONTENT']);
		
		if(postData['POPUP_YN'] == 'Y') {
			$('#post_popup_yn').prop('checked', true);
			$('#post_popup_span').show();
		} else {
			$('#post_popup_yn').prop('checked', false);
			$('#post_popup_span').hide();
		}
		
		if(postData['POPUP_START_DT_TM'] || postData['POPUP_END_DT_TM']) {
			$('#post_popup_start_dt').text(postData['POPUP_START_DT_TM']);
			$('#post_popup_end_dt').text(postData['POPUP_END_DT_TM']);
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
			
		}
	}
	
	
	//게시물 수정
	function modifyBoardPost() {
		let pagePrams = [
			  ["boardId", boardId]
			, ["postId", postId]
		];
		
		pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostWriteView.do', pagePrams);
	}
	
	
	//게시물 삭제
	function deleteBoardPost() {
		let pagePrams = [
			  ["boardId", boardId]
			, ["postId", postId]
		];
		
		let msg = '게시글을 삭제하시겠습니까?';
		if (confirm(msg)) {
			
		}
	}
	
</script>
</html>