<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String boardId = (String)request.getParameter("BRD_ID");
	String postId = (String)request.getParameter("POST_ID");
	
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");
	
	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>${postData['data']['BRD_NM']} - 상세</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		#board_table th, #board_table td {
		    border: 1px solid;
		}
		
		  #boardPost_div
		, #boardPost_div a
		, #boardPost_div input
		, #boardPost_div span
		, #boardPost_div select
		, #boardPost_div button {
			font-size: 1.5rem;
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
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart${portalAppName}.jsp" />
	
	<div id="boardPost_div" class="container py-4" style="max-width: 100%;">
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
			<div id="post_befoe" class="col-11">
			</div>
	    </div>
	    <div class="row">
			<div class="col-1">
				<span>다음글</span>
			</div>
			<div id="post_next" class="col-11">
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
			  BRD_ID : boardId
			, POST_ID : postId
		};
		callAjaxPost('/board/boardPostDetail.json', callParams, function(data){
			let postData = data['data'];
			let postFile = data['file'];
			
			let postLocation = data['location'];
			
			displayContents(postData, postFile, postLocation);
		});
		
	}
	
	
	//내용 표시
	function displayContents(postData, postFile, postLocation) {
		
		if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}' || <%= PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN") == true %>) {
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
		
		//이전글,다음글
		postLocation.forEach((location, idx) => {
			if(location['POST_LOCATION'] == 'BEFORE') {
				let beforeHtml = $('<span>', {
					  text : location['POST_TITLE']
					, title : location['POST_TITLE']
					, style : 'cursor:pointer;'
					, click : function(e) {
						detailBoardPost(location['BRD_ID'], location['POST_ID']);
					}
				});
				
				$('#post_befoe').append(beforeHtml);
			} else if(location['POST_LOCATION'] == 'NEXT') {
				let nextHtml = $('<span>', {
					  text : location['POST_TITLE']
					, title : location['POST_TITLE']
					, style : 'cursor:pointer;'
					, click : function(e) {
						detailBoardPost(location['BRD_ID'], location['POST_ID']);
					}
				});
				
				$('#post_next').append(nextHtml);
			}
		});
		
		if($('#post_befoe').find('span').length == 0) {
			$('#post_befoe').parent().remove();
		}
		
		if($('#post_next').find('span').length == 0) {
			$('#post_next').parent().remove();
		}
	}
	
	
	//게시물 수정
	function modifyBoardPost() {
		let pagePrams = [
			  ['BRD_ID', boardId]
			, ['POST_ID', postId]
		];
		
		pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostWriteView.do', pagePrams);
	}
	
	
	//게시물 삭제
	function deleteBoardPost() {
		let pagePrams = [
			  ['BRD_ID', boardId]
			, ['POST_ID', postId]
		];
		
		let msg = '게시글을 삭제하시겠습니까?';
		if (confirm(msg)) {
			let callParams = {
				  BRD_ID : boardId
				, POST_ID : postId
			};
			callAjaxPost('/board/boardPostDelete.json', callParams, function(data) {
				alert('게시글이 삭제되었습니다.');
				moveCommunityPage(boardId);
			});
		}
	}
	
</script>
</html>