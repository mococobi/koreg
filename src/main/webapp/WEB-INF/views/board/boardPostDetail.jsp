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
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div class="container py-4">
		<p class="h3">${postData['data']['BRD_NM']}</p>
		<p class="h6">${postData['data']['BRD_DESC']}</p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_post_modify" class="btn btn-secondary btn-sm" onclick="modifyBoardPost()" style="display: none;">수정</button>
				<button id="btn_post_delete" class="btn btn-secondary btn-sm" onclick="deleteBoardPost()" style="display: none;">삭제</button>
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
					<tr id="post_popup_yn_div">
						<td>
							<span>팝업여부</span>
						</td>
						<td class="text-center">
							<input type="checkbox" id="post_popup_yn" disabled>
						</td>
						<td>
							<span>팝업일자</span>
						</td>
						<td colspan="5">
							<span id="post_popup_dt"></span>
						</td>
					</tr>
				<tr>
					<c:if test="${postData['data']['POST_FIX_YN'] eq 'Y'}">
						<td  id="post_fix_yn_div">
							<span>상단 고정</span>
						</td>
						<td>
							<input type="checkbox" id="post_fix_yn" disabled>
						</td>
					</c:if>
					<c:if test="${postData['data']['POST_SECRET_YN'] eq 'Y'}">
						<td id="post_secret_yn_div">
							<span>비밀글</span>
						</td>
						<td>
							<input type="checkbox" id="post_secret_yn" disabled>
						</td>
					</c:if>
				</tr>
					<c:if test="${postData['data']['BRD_VIEW_AUTH'] eq 'Y'}">
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
				<c:if test="${postData['data']['POST_FILE_YN'] eq 'Y'}">
					<tr id="post_file_yn_div">
						<td>
							<span>첨부 파일</span>
						</td>
						<td colspan="7">
							<div id="post_file" class="list-group">
							</div>								
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
	let searchKey = '';
	let searchVal = '';
	
	$(function() {
		fnBoardPostInit();
	});
	
	
	//초기 함수
	function fnBoardPostInit() {
		let callParams = {
			  boardId : boardId
			, postId : postId
		};
		callAjaxPost('/board/boardPostDetail.json', callParams, function(data){
			let postData = data['data'];
			let postFile = data['file'];
			
			displayContents(postData, postFile);
		});
		
	}
	
	
	//내용 표시
	function displayContents(postData, postFile) {
		
		if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}') {
			$('#btn_post_modify').show();
			$('#btn_post_delete').show();
		}
		
		$('#post_title').text(postData['POST_TITLE']);
		
		$('#post_create_user_id').text(postData['CRT_USR_ID']);
		$('#post_create_user_dept_name').text(postData['CRT_USR_DEPT_NM']);
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_count').text(postData['POST_VIEW_COUNT']);
		
		$('#post_content').html(postData['POST_CONTENT']);
		
		if(postData['POPUP_YN'] == 'Y') {
			$('#post_popup_yn').prop('checked', true);
			$('#post_popup_span').show();
		} else {
			$('#post_popup_yn').prop('checked', false);
			$('#post_popup_span').hide();
		}
		
		if(postData['POPUP_START_DT_TM'] || postData['POPUP_END_DT_TM']) {
			$('#post_popup_dt').text(changeDisplayDate(postData['POPUP_START_DT_TM'], 'YYYY-MM-DD') + ' ~ ' + changeDisplayDate(postData['POPUP_END_DT_TM']), 'YYYY-MM-DD');
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
		
		//첨부파일
		if(postFile) {
			postFile.forEach((attachFile, idx) => {
				let fileHtml = $('<a>', {
					  class : 'list-group-item list-group-item-action list-group-item-secondary'
					, style : 'cursor:pointer;'
					, text : attachFile['ORG_FILE_NM'] + '.' + attachFile['FILE_EXT'] + '\t' + formatFileSize(attachFile['FILE_SIZE'])
					, title : attachFile['ORG_FILE_NM'] + '.' + attachFile['FILE_EXT']
					, click : function(e) {
						let fileData = {
							  BRD_ID : boardId
							, POST_ID : attachFile['POST_ID']
							, FILE_ID : attachFile['FILE_ID']
						};
						downloadAttachFile(fileData);
					}
				});
				
				$('#post_file').append(fileHtml);
			});
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