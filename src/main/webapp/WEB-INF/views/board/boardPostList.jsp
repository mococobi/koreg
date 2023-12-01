<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String boardId = (String)request.getParameter("boardId");
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
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivStart.jsp" />
	
	<div class="container py-4">
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
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm" onclick="writeBoardPost()">글쓰기</button>
			</div>
	    </div>
		<div id="boardTable_div">
			<table id="boardTable" class="table hover table-striped table-bordered dataTablesCommonStyle">
				<colgroup>
					<col width="5%">
					<col >
					<col width="10%">
					<col width="10%">
				</colgroup>
				<thead>
	    			<tr>
	     				<th>NO</th>
	      				<th>제목</th>
	      				<th>작성일자</th>
	      				<th>작성자</th>
	    			</tr>
	  			</thead>
			</table>
		</div>
	</div>
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let searchKey = '';
	let searchVal = '';
	
	$(function() {
		fnBoardInit();
		
		$('#searchVal').keypress(function(e){
			if(e.keyCode && e.keyCode == 13){
				searchBoardPostList();
			}
		});
	});
	
	
	//게시물 목록
	function fnBoardInit() {
		let listViewCount = 10;
		$('#boardTable').DataTable({
			  lengthChange : false
			, searching : false
			, serverSide : true
			, processing : true
			, ordering : true
			, pageLength : listViewCount
			, pagingType : 'full_numbers'
			, ajax : {
				  url : '${pageContext.request.contextPath}/app/board/boardPostListGrid.json'
				, type : 'POST'
				, data : function(data) {
					data['boardId'] = boardId;
					data['listViewCount'] = listViewCount;
					data['searchKey'] = searchKey;
					data['searchVal'] = searchVal;
				}
				, dataSrc : function (data) {
					if (data['errorCode'] != 'success') {
						alert(data['errorMessage']);
					} else {
				        data['recordsTotal'] = data['dataSize'];
				        data['recordsFiltered'] = data['dataSize'];
				        
						return data['data'];
					}
			    } 
				, error: function (jqXHR, textStatus, errorThrown) {
					if(xhr['status'] == 404) {
						alert('지정되지 않은 URL입니다.');
					} else {
						alert('에러 처리 필요');
					}
			    }
			}
			, columns : [
				{
					  data : 'POST_ID'
					, className : 'textCenter'
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return rtnData;
					}
	            }
				, {
					  data : 'POST_TITLE'
					, className : ''
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return '<a onclick="detailBoardPost('+ row['BRD_ID'] +', '+ row['POST_ID'] +')" class="not-a-text" title="'+ rtnData +'">' + rtnData + '</a>';
					}
	            }
				, {
					  data : 'CRT_DT_TM'
					, className : 'textCenter'
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return changeDisplayDate(rtnData, 'YYYY-MM-DD');
					}
	            }
				, {
					  data : 'CRT_USR_ID'
					, className : 'textCenter'
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return rtnData;
					}
	            }
	        ]
		});
	}
	
	
	//게시물 검색
	function searchBoardPostList() {
		searchKey = $('#searchKey option:selected').val();
		searchVal = $('#searchVal').val();
		
		$('#boardTable').DataTable().ajax.reload();
	}
	
	
	//게시물 작성
	function writeBoardPost() {
		let pagePrams = [
			["boardId", boardId]
		];
		pageGoPost('_self', '${pageContext.request.contextPath}/app/board/boardPostWriteView.do', pagePrams);
	}
	
	
</script>
</html>