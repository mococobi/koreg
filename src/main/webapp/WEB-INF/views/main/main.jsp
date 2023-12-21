<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>메인화면</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />

	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
	
<script type="text/javascript">
	$(function() {
		fnMainInit();
	});
	
	
	//초기 함수
	function fnMainInit() {
		getBoardList('1', 'board_div_1');
		getBoardList('2', 'board_div_2');
	}
	
	
	//게시물 목록
	function getBoardList(boardId, divId) {
		let callParams = {
			  BRD_ID : boardId
			, listViewCount : 5
			, start : 0
			, length : 5
		};
		callAjaxPost('/board/boardPostList.json', callParams, function(data) {
			let postData = data['data'];
			
			postData.forEach((post, idx) => {
				
				let trHtml = $('<tr>');
				
				let tdTitleHtml = $('<td>', {
					  text : post['POST_TITLE']
					, title : post['POST_TITLE']
					, class : ''
					, style : 'cursor : pointer'
					, click : function(e) {
						let pagePrams = [
							  ['BRD_ID', boardId]
							, ['POST_ID', post['POST_ID']]
							
						];
						pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostDetailView.do', pagePrams);
					}
				});
				
				let tdCreateDateHtml = $('<td>', {
					  text : changeDisplayDate(post['CRT_DT_TM'], 'YYYY-MM-DD')
					, title : post['POST_TITLE']
					, class : ''
					, style : 'cursor : pointer'
					, click : function(e) {
						let pagePrams = [
							  ['BRD_ID', boardId]
							, ['POST_ID', post['POST_ID']]
							
						];
						pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostDetailView.do', pagePrams);
					}
				});
				
				$(trHtml).append(tdTitleHtml);
				$(trHtml).append(tdCreateDateHtml);
				
				
				$('#' + divId).append(trHtml);
			});
		});
	}
</script>
</body>
</html>