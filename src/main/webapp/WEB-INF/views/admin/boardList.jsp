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
	<title>관리자 페이지</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<style type="text/css">
		  #board_div
		, #board_div a
		, #board_div input
		, #board_div span
		, #board_div select
		, #board_div button {
			font-size: 1.5rem;
			font-family: 맑은 고딕;
		}
		
		#board_div .h3 {
			font-size: 3rem;
			font-family: 맑은 고딕;
		}
		
		#board_div .h6 {
			font-size: 2rem;
			font-family: 맑은 고딕;
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/adminDivStart.jsp" />
	
	<div id="board_div" class="container py-4" style="max-width: 100%;">
		<p class="h3">게시판 관리</p>
		<p class="h6">게시판을 관리할 수 있습니다.</p>
		<div class="row mb-3">
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm" onclick="writeBoard()">글쓰기</button>
			</div>
		</div>
		<div id="boardTable_div">
			<table id="boardTable" class="table hover table-striped table-bordered dataTablesCommonStyle" style="width: 100%">
				<colgroup>
					<col width="10%">
					<col >
					<col width="10%">
					<col width="10%">
				</colgroup>
				<thead>
    				<tr>
	     				<th>게시판 ID</th>
	      				<th>게시판 이름</th>
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
	let searchKey = '';
	let searchVal = '';
	
	$(function() {
		fnBoardInit();
	});
	
	
	//게시판 목록
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
			, order : [[ 0, 'desc' ]]
			, ajax : {
				  url : '${pageContext.request.contextPath}/app/admin/boardListGrid.json'
				, type : 'POST'
				, data : function(data) {
					data['listViewCount'] = listViewCount;
					data['searchKey'] = searchKey;
					data['searchVal'] = searchVal;
					
					data['customOrder1'] = $('#boardTable').DataTable().order()[0][0];
					data['customOrder2'] = $('#boardTable').DataTable().order()[0][1];
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
				, error : function (jqXHR, textStatus, errorThrown) {
					errorProcess(jqXHR, textStatus, errorThrown);
			    }
			}
			, language : commonDatatableLanguage()
			, columns : [
				{
					  data : 'BRD_ID'
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
					  data : 'BRD_NM'
					, className : ''
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return '<a onclick="detailBoard('+ row['BRD_ID'] +')" class="not-a-text" title="'+ rtnData +'">' + rtnData + '</a>';
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
	
	
	//게시판 작성
	function writeBoard() {
		let pagePrams = [];
		pageGoPost('_self', '${pageContext.request.contextPath}/app/admin/boardWriteView.do', pagePrams);
	}
	
	
	//관리자 - 게시판 상세 화면 이동
	function detailBoard(moveBoardId) {
		let pagePrams = [
			["boardId", moveBoardId]
		];
		pageGoPost('_self', '${pageContext.request.contextPath}/app/admin/boardDetailView.do', pagePrams);
	}
	
	
</script>
</html>