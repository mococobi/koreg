<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
	
	String boardId = (String)request.getParameter("BRD_ID");
	String postId = (String)request.getParameter("POST_ID");
	
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");
	Boolean portalAdminAuth = PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>팝업</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		#board_table th, #board_table td {
		    border: 1px solid;
		}
		
		html, body {
			font-size: 1rem;
		}
		
		  #boardPost_div
		, #boardPost_div a
		, #boardPost_div input
		, #boardPost_div span
		, #boardPost_div select
		, #boardPost_div button {
			font-size: 1rem;
			font-family: 맑은 고딕;
		}
		
		#boardPost_div .h3 {
			font-size: 3rem;
			font-family: 맑은 고딕;
		}
		
		#boardPost_div .h6 {
			font-size: 2rem;
			font-family: 맑은 고딕;
		}

	</style>
</head>
<body>
	<div id="boardPost_div">
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
				<tr id="post_type_yn" style="display: none;">
					<td>
						<span>분류</span>
					</td>
					<td colspan="7">
						<span id="post_type"></span>
					</td>
				</tr>
				<tr id="post_popup_yn" style="display: none;">
					<td>
						<span>팝업여부</span>
					</td>
					<td class="text-center">
						<input type="checkbox" id="popup_yn" disabled>
					</td>
					<td>
						<span>팝업일자</span>
					</td>
					<td colspan="5">
						<span id="popup_dt"></span>
					</td>
				</tr>
				<tr id="post_fix_yn" style="display: none;">
					<td>
						<span>고정여부</span>
					</td>
					<td class="text-center">
						<input type="checkbox" id="fix_yn" disabled>
					</td>
				</tr>
				<tr>
					<td>
						<span>내용</span>
					</td>
					<td colspan="7">
						<div id="post_content" style="min-height: 300px;"></div>
					</td>
				</tr>
				<tr id="post_file_yn" style="display: none;">
					<td>
						<span>첨부 파일</span>
					</td>
					<td colspan="7">
						<div id="post_file" class="list-group">
						</div>								
					</td>
				</tr>
			</tbody>
		</table>
		
		<input type="checkbox" id="hidePopupCheckbox"> 일주일 동안 보지 않기
	</div>
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
			
			displayBoardPostTag(postData, postFile);
			displayContents(postData, postFile);
		});
		
	}
	
	
	//태그 표시
	function displayBoardPostTag(postData, postFile) {
		document.title = postData['BRD_NM'] + ' - 상세';
		
		if(postData['POST_TYPE_YN'] == 'Y') {
			$('#post_type_yn').show();
		}
		
		if(postData['POST_POPUP_YN'] == 'Y') {
			$('#post_popup_yn').show();
		}
		
		if(postData['POST_FIX_YN'] == 'Y') {
			$('#post_fix_yn').show();
		}
		
		if(postData['POST_FILE_YN'] == 'Y') {
			$('#post_file_yn').show();
		}
	}
	
	
	//내용 표시
	function displayContents(postData, postFile) {
		$('#post_title').text(postData['POST_TITLE']);
		$('#post_create_user_id').text(postData['CRT_USR_NM']);
		$('#post_create_user_dept_name').text(postData['CRT_USR_DEPT_NM']);
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_count').text(postData['POST_VIEW_COUNT']);
		
		if(postData['POST_TYPE_YN'] == 'Y') {
			$('#post_type').text(postData['POST_TYPE']);
		}
		
		if(postData['POST_POPUP_YN'] == 'Y') {
			if(postData['POPUP_YN'] == 'Y') {
				$('#popup_yn').prop('checked', true);
			} else {
				$('#popup_yn').prop('checked', false);
			}
			
			if(postData['POPUP_START_DT_TM'] || postData['POPUP_END_DT_TM']) {
				$('#popup_dt').text(changeDisplayDate(postData['POPUP_START_DT_TM'], 'YYYY-MM-DD') + ' ~ ' + changeDisplayDate(postData['POPUP_END_DT_TM'], 'YYYY-MM-DD'));
			}
		}
		
		if(postData['POST_FIX_YN'] == 'Y') {
			if(postData['FIX_YN'] == 'Y') {
				$('#fix_yn').prop('checked', true);
			} else {
				$('#fix_yn').prop('checked', false);
			}
		}
		
		$('#post_content').html(postData['POST_CONTENT']);
		
		if(postData['POST_FILE_YN'] == 'Y') {
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
	}
	
	//팝업 쿠키 세팅
	function popUpCookie(ckId, ckVal, exp) {
		handleCookie.setCookie('PopUp'+ckId, !ckVal, 7);
	}
	
</script>
</html>