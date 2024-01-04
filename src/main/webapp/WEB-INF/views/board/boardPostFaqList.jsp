<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String boardId = (String)request.getParameter("BRD_ID");
	
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
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart${portalAppName}.jsp" />
	<% if(boardId.equals("2")) {
		%>
	<div id="boardPost_div" class="container py-4" style="max-width: 100%;">
		<p class="h3">${postData['BRD_NM']}</p>
		<p class="h6">${postData['BRD_DESC']}</p>
	<% if(
		(boardId.equals("2") && PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN"))
		|| !boardId.equals("2")) { %>
		<!-- 관리자 기능 -->
		<div class="col text-end" style="margin-top:-10px; margin-bottom:10px;">
			<button id="btn_post_write" class="btn btn-secondary btn-sm" onclick="writeBoardPost()">글쓰기</button>
		</div>
	<% } %>
		<div id="boardPostTable_div">
			<table id="boardPostTable" class="table hover table-striped table-bordered dataTablesCommonStyle" style="width:100%;">
				<colgroup>
					<col >
				</colgroup>
				<thead>
    				<tr>
	      				<th scope="col">제목</th>
	    			</tr>
	  			</thead>
	  			<tbody id="board_div_2">
	  			</tbody>
			</table>
		</div>
	</div>
	<% } %>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let searchKey = '';
	let searchVal = '';
	
	$(function() {
		fnBoardInit('2', 'board_div_2');
		
		$('#searchVal').keypress(function(e){
			if(e.keyCode && e.keyCode == 13){
				searchBoardPostList();
			}
		});
		
	});
	
	
	//게시물 목록
	function fnBoardInit(boardId, divId) {
		let callParams = {
			BRD_ID : boardId
		};
		callAjaxPost('/board/boardPostList.json', callParams, function(data) {
			let postData = data['data'];
			
			postData.forEach((post, idx) => {
				
				let rtnData = '-';
				let trHtml = $('<tr>');
				
				let tdTitleHtml = $('<td>', {
					  text : post['POST_TITLE']
					, title : post['POST_TITLE']
					, style : 'cursor : pointer'
					, render : function (data, type, row) {
						let rtnData = '-';
						if (data) {
							 rtnData = 
								 '<div class="accordion" id="accordionExample">'
								+	'<div class="accordion-item">'
								+		'<h2 class="accordion-header">'
								+			'<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#accordion_' + row.POST_ID + '" aria-expanded="false" aria-controls="accordion_' + row.POST_ID + '">'
								+				XSSCheck(data, 0)
								+			'</button>'
								+		'</h2>'
								+		'<div id="accordion_' + row.POST_ID + '" class="collapse" data-bs-parent="#accordionExample">'
								+			'<div class="accordion-body">'
								+				'<strong>' + row.POST_CONTENT + '</strong>'
								+			'</div>'
								+		'</div>'
								+	'</div>'
								+'</div>';
				        }
							return rtnData
					}
				});
				
				$(trHtml).append(tdTitleHtml);
				
				$('#' + divId).append(trHtml);
			});
		});
	}
			
					 /*  data : 'POST_TITLE'
					, className : ''
					, render : function (data, type, row) {
						let rtnData = '-';
						if (data) {
							 rtnData = 
								 '<div class="accordion" id="accordionExample">'
								+	'<div class="accordion-item">'
								+		'<h2 class="accordion-header">'
								+			'<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#accordion_' + row.POST_ID + '" aria-expanded="false" aria-controls="accordion_' + row.POST_ID + '">'
								+				XSSCheck(data, 0)
								+			'</button>'
								+		'</h2>'
								+		'<div id="accordion_' + row.POST_ID + '" class="collapse" data-bs-parent="#accordionExample">'
								+			'<div class="accordion-body">'
								+				'<strong>' + row.POST_CONTENT + '</strong>'
								+			'</div>'
								+		'</div>'
								+	'</div>'
								+'</div>';
				                
				        }
							return rtnData
						}*/
	
	
	function toggleContent(element) {
	    const content = $(element).next('.post-content');

	    content.toggleClass('d-none');
	}
	
	
	//게시물 검색
	function searchBoardPostList() {
		searchKey = $('#searchKey option:selected').val();
		searchVal = $('#searchVal').val();
		
		//$('#boardPostTable').DataTable().ajax.reload();
	}
	
	
	//게시물 작성
	function writeBoardPost() {
		let pagePrams = [
			['BRD_ID', boardId]
		];
		pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostWriteView.do', pagePrams);
	}
	
	
</script>
</html>