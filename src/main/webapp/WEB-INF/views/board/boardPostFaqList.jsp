<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
	<title>${postData['BRD_NM']}</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
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
		
		.accordion-button::after {
		    flex-shrink: 0;
		    width: var(--bs-accordion-btn-icon-width);
		    height: var(--bs-accordion-btn-icon-width);
		    margin-left: 10px;
		    content: "";
		    background-image: var(--bs-accordion-btn-icon);
		    background-repeat: no-repeat;
		    background-size: var(--bs-accordion-btn-icon-width);
		    transition: var(--bs-accordion-btn-icon-transition);
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart${portalAppName}.jsp" />
	<% if(boardId.equals("2")) {
		%>
	<div id="boardPost_div" class="container py-4" style="max-width: 100%;">
		<p class="h3">${postData['BRD_NM']}</p>
		<p class="h6">${postData['BRD_DESC']}</p>
		<div class="row mb-3">
			<div class="col-md-1">
				<select id="searchKey" class="form-select form-select-sm">
					<option value="POST_TITLE">제목</option>
					<option value="CRT_USR_ID">작성자</option>
				</select>
			</div>
			<div class="col-md-4">
				<input id="searchVal" class="form-control form-control-sm" type="search" placeholder="Search" aria-label="Search">
			</div>
			<div class="col-md-1">
				<button class="btn btn-primary btn-sm" onclick="searchBoardPostList()">조회</button>
			</div>
			<% if(
				(boardId.equals("2") && PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN"))
				|| !boardId.equals("2")) { %>
				<!-- 관리자 기능 -->
				<div class="col text-end" style="margin-top:-10px; margin-bottom:10px;">
					<button id="btn_post_write" class="btn btn-secondary btn-sm" onclick="writeBoardPost()">글쓰기</button>
				</div>
			<% } %>
		</div>
			<table id="boardPostTable" class="table hover table-striped table-bordered dataTablesCommonStyle" style="width:100%;">
		  		<tbody id="board_div_2">
		  		</tbody>
			</table>
	</div>
	<% } %>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	let searchKey = '';
	let searchVal = '';
	$(function() {
		fnBoardInit('2');
		
		$('#searchVal').keypress(function(e){
			if(e.keyCode && e.keyCode == 13){
				searchBoardPostList();
			}
		});
		
	});
	
	
	//게시물 목록
	function fnBoardInit(boardId) {
		let callParams = {
			BRD_ID : boardId
		};
		callAjaxPost('/board/boardPostFaqList.json', callParams, function(data) {
			let postData = data['data'];

			let accordionHTML = '<div class="accordion" id="accordionExample">';
			
			postData.forEach((post, idx) => {
				let divHtml = $('<div>');
			    accordionHTML +=
						'<div class="accordion-item">'
					+		'<h2 class="accordion-header">'
					+			'<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse' + post['POST_ID'] + '" aria-expanded="false" aria-controls="collapse"' + post['POST_ID'] +'style="margin-right:0;">'
					+				'<strong>' + post['POST_TITLE'] + '</strong>'
					+				'<input type="button" id="btn_post_detail" class="btn btn-secondary btn-sm" value="상세" onclick="detailBoardPost(' + post['POST_ID'] + ')" style="width:5%; height:50%; margin-left:auto;">'
					+			'</button>'
					+		'</h2>'
					+		'<div id="collapse' + post['POST_ID'] + '" class="accordion collapse" data-bs-parent="#accordionExample">'
					+			'<div class="accordion-body">'
					+				'<span>' + post['POST_CONTENT'] + '</span>'
					+				'<div class="list-group" id="post_file' + post['POST_ID'] + '">'
					+				'</div>'
					+			'</div>'
					+		'</div>'
					+	'</div>';
			});
			
			// 첨부 파일들 렌더링
			let aTempFileInfoTag = [];
			if(postData) {
				postData.forEach((data, idx) => {
					let tmpAttachFileInfo = data;
					//console.log("tmpAttachFileInfo ["+tmpAttachFileInfo+"]");
					$.each(tmpAttachFileInfo.attachfiles, function(attachFileIdx, attachFileInfo) {
						let fileHtml = $('<a></a>', {
							  class : 'list-group-item list-group-item-action list-group-item-secondary'
							, style : 'cursor:pointer;'
							, text : attachFileInfo['ORG_FILE_NM'] + '.' + attachFileInfo['FILE_EXT'] + '\t' + formatFileSize(attachFileInfo['FILE_SIZE'])
							, title : attachFileInfo['ORG_FILE_NM'] + '.' + attachFileInfo['FILE_EXT']
							, click : function(e) {
								let fileData = {
									  BRD_ID : boardId
									, POST_ID : attachFileInfo['POST_ID']
									, FILE_ID : attachFileInfo['FILE_ID']
								};
								downloadAttachFile(fileData);
							}
						});
						//console.log(attachFileInfo['ORG_FILE_NM']);
						
						fileHtml.attr('id', attachFileInfo['POST_ID']);
						
						aTempFileInfoTag.push(fileHtml);
						//$('#post_file').append(fileHtml);
					});
					
				});
			}
			
			accordionHTML += '</div>';
			$('#board_div_2').append(accordionHTML);
			
			$.each(aTempFileInfoTag, function(idx, item) {
				//console.log(item);
				//console.log($(item).attr('id'));
				//$('#post_file').append(item);
				$('div#collapse'+$(item).attr('id')).find('div#post_file'+$(item).attr('id')).append(item);
			});
		});
	}
	
	
	//게시물 검색
	function searchBoardPostList() {
		searchKey = $('#searchKey option:selected').val();
		searchVal = $('#searchVal').val();
		
		var dataTable = $('#boardPostTable').DataTable();
		dataTable.ajax.reload();
	}
	
	
	//게시물 작성
	function writeBoardPost() {
		let pagePrams = [
			['BRD_ID', boardId]
		];
		pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostWriteView.do', pagePrams);
	}
	
	
	//게시물 수정
	function detailBoardPost(postId) {
		let pagePrams = [
			  ['BRD_ID', boardId]
			, ['POST_ID', postId]
		];
		
		pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostDetailView.do', pagePrams);
	}
	
	//게시물 삭제
	function deleteBoardPost(postId) {
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