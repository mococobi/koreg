<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String boardId = (String)request.getParameter("BRD_ID");
	String postId = (String)request.getParameter("POST_ID");
	
	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>공지사항</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		#board_table th, #board_table td {
		    border: 1px solid;
		}
		
		html, body {
			font-size: 1rem;
		}

	</style>
</head>
<body>
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
		
		<input type="checkbox" id="hidePopupCheckbox"> 일주일 동안 보지 않기
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	
	$(function() {
        fnBoardPostInit();
        
        $('#hidePopupCheckbox').change(function(){
        	//console.log($(this).is(':checked'));
        	popUpCookie(postId, $(this).is(':checked'));
        });
    });
	
	
	//초기 함수
	function fnBoardPostInit() {
		// 게시글 조회
		let callParams = {
			  BRD_ID : boardId
			, POST_ID : postId
		};
		callAjaxPost('/board/boardPostPopupDetail.json', callParams, function(data){
			let postData = data['data'];
			let postFile = data['file'];
			
			document.title = '공지사항 - ' + postData['POST_TITLE'];
			
			displayContents(postData, postFile);
		});
		
	}
	
	
	//내용 표시
	function displayContents(postData, postFile) {
		
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
			$('#post_popup_dt').text(changeDisplayDate(postData['POPUP_START_DT_TM'], 'YYYY-MM-DD') + ' ~ ' + changeDisplayDate(postData['POPUP_END_DT_TM'], 'YYYY-MM-DD'));
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
	
	//팝업 쿠키 세팅
	function popUpCookie(ckId, ckVal, exp) {
		handleCookie.setCookie('PopUp'+ckId, !ckVal, 7);
	}
	
</script>
</html>