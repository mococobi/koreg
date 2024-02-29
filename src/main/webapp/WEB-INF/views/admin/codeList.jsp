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
		
		
	  	  .board_custom_div
		, .board_custom_div a
		, .board_custom_div input
		, .board_custom_div span
		, .board_custom_div select
		, .board_custom_div button {
			font-size: 1.5rem;
			font-family: 맑은 고딕;
		}
		
		.board_custom_div .h3 {
			font-size: 3rem;
			font-family: 맑은 고딕;
		}
		
		.board_custom_div .h6 {
			font-size: 2rem;
			font-family: 맑은 고딕;
		}
	</style>
</head>
<body>
	<jsp:include flush="true" page="/WEB-INF/views/include/adminDivStart.jsp" />
	
	<div id="board_div" class="container py-4" style="max-width: 100%;">
		<p class="h3">코드 관리</p>
		<p class="h6">코드를 관리할 수 있습니다.</p>
		<div class="row mb-3">
			<div class="col-md-2">
				<select id="searchKey" class="form-select form-select-sm">
					<option value="CD_TYPE_ENG_NM">코드 분류 영문명</option>
					<option value="CD_ENG_NM">코드 영문명</option>
      				<option value="CD_KOR_NM">코드 한글명</option>
      				<option value="CD_ORD">코드 순서</option>
      				<option value="DEL_YN">삭제 여부</option>
				</select>
			</div>
			<div class="col-md-4">
				<input id="searchVal" class="form-control form-control-sm" type="search" placeholder="Search" aria-label="Search">
			</div>
			<div class="col-md-1">
				<button class="btn btn-primary btn-sm" onclick="searchBoardList()">조회</button>
			</div>
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm" onclick="writeCode()">글쓰기</button>
			</div>
		</div>
		<div id="boardTable_div">
			<table id="boardTable" class="table hover table-striped table-bordered dataTablesCommonStyle" style="width: 100%">
				<colgroup>
					<col width="20%">
					<col width="20%">
					<col >
					<col width="10%">
					<col width="10%">
					<col width="10%">
					<col width="10%">
				</colgroup>
				<thead>
    				<tr>
	     				<th>코드 분류 영문명</th>
	     				<th>코드 영문명</th>
	      				<th>코드 한글명</th>
	      				<th>코드 순서</th>
	      				<th>삭제 여부</th>
	      				<th>작성일자</th>
	      				<th>작성자</th>
	    			</tr>
	  			</thead>
			</table>
		</div>
	</div>
	
	<div class="modal fade board_custom_div" id="logListModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title fs-5" id="exampleModalLabel">코드 정보</h3>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					
			    	<table id="board_table" class="table table-sm table-bordered" style="width: 100%; border: 1px solid; border-collapse: collapse;">
						<colgroup>
							<col width="20%">
							<col width="">
						</colgroup>
						<tbody>
							<tr>
								<td>
									<span>코드 분류 영문명</span>
								</td>
								<td>
									<input type="text" id="cd_type_eng_nm" class="form-control form-control-sm" title="코드 분류 영문명" placeholder="코드 분류 영문명을 입력하세요">
								</td>
							</tr>
							<tr>
								<td>
									<span>코드 영문명</span>
								</td>
								<td>
									<input type="text" id="cd_eng_nm" class="form-control form-control-sm" title="코드 영문명" placeholder="코드 영문명을 입력하세요">
								</td>
							</tr>
							<tr>
								<td>
									<span>코드 한글명</span>
								</td>
								<td>
									<input type="text" id="cd_kor_nm" class="form-control form-control-sm" title="코드 한글명" placeholder="코드 한글명을 입력하세요">
								</td>
							</tr>
							<tr>
								<td>
									<span>코드 설명</span>
								</td>
								<td>
									<input type="text" id="cd_desc" class="form-control form-control-sm" title="코드 설명" placeholder="코드 설명을 입력하세요">
								</td>
							</tr>
							<tr>
								<td>
									<span>코드 순서</span>
								</td>
								<td>
									<input type="text" id="cd_ord" class="form-control form-control-sm" title="코드 순서" placeholder="코드 순서를 입력하세요">
								</td>
							</tr>
							<tr>
								<td>
									<span>생성일시</span>
								</td>
								<td>
									<span id="board_create_date"></span>
								</td>
							</tr>
							<tr>
								<td>
									<span>생성자 ID</span>
								</td>
								<td>
									<span id="board_create_user_id"></span>
								</td>
							</tr>
							<tr>
								<td>
									<span>수정일시</span>
								</td>
								<td>
									<span id="board_modify_date"></span>
								</td>
							</tr>
							<tr>
								<td>
									<span>수정자 ID</span>
								</td>
								<td>
									<span id="board_modify_user_id"></span>
								</td>
							</tr>
							<tr>
								<td>
									<span>삭제 여부</span>
								</td>
								<td>
									<input name="del_yn" type="radio" value="Y" class="form-check-input" required="">
			             	 		<span class="form-check-label" for="credit">Y</span>
			             	 		<input name="del_yn" type="radio" value="N" class="form-check-input" required="">
			             	 		<span class="form-check-label" for="credit">N</span>
								</td>
							</tr>
						</tbody>
					</table>
					
					
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" onclick="createCode()">저장</button>
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let searchKey = '';
	let searchVal = '';
	let listData;
	
	$(function() {
		fnBoardInit();
		
		$('#searchVal').keypress(function(e){
			if(e.keyCode && e.keyCode == 13){
				searchBoardList();
			}
		});
	});
	
	
	//게시판 검색
	function searchBoardList() {
		searchKey = $('#searchKey option:selected').val();
		searchVal = $('#searchVal').val();
		
		$('#boardTable').DataTable().ajax.reload();
	}
	
	
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
			, order : [[ 1, 'asc' ]]
			, ajax : {
				  url : '${pageContext.request.contextPath}/app/code/codeListGrid.json'
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
				        
				        listData = data['data'];
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
					  data : 'CD_TYPE_ENG_NM'
					, className : 'textCenter'
					, render : function (data, type, row, colInfo) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return '<a onclick="detailCode(' + colInfo['row'] + ')" class="not-a-text" title="'+ rtnData +'">' + rtnData + '</a>';
					}
	            }
				, {
					  data : 'CD_ENG_NM'
					, className : ''
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return rtnData;
					}
	            }
				, {
					  data : 'CD_KOR_NM'
					, className : ''
					, render : function (data, type, row) {
						let rtnData = '-';
						if(data) {
							rtnData = XSSCheck(data, 0);
						}
						return rtnData;
					}
	            }
				, {
					  data : 'CD_ORD'
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
					  data : 'DEL_YN'
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
	function writeCode() {
		let pagePrams = [];
		pageGoPost('_self', '${pageContext.request.contextPath}/app/admin/codeWriteView.do', pagePrams);
	}
	
	
	//관리자 - 게시판 상세 화면 이동
	function detailCode(moveBoardId) {
		let selectedData = listData[moveBoardId];
		
		$('#cd_type_eng_nm').val(selectedData['CD_TYPE_ENG_NM']);
		$('#cd_eng_nm').val(selectedData['CD_ENG_NM']);
		$('#cd_kor_nm').val(selectedData['CD_KOR_NM']);
		$('#cd_desc').val(selectedData['CD_DESC']);
		$('#cd_ord').val(selectedData['CD_ORD']);
		
		$('#board_create_date').text(changeDisplayDate(selectedData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#board_create_user_id').text(selectedData['CRT_USR_ID']);
		$('#board_modify_date').text(changeDisplayDate(selectedData['MOD_DT_TM'], 'YYYY-MM-DD'));
		$('#board_modify_user_id').text(selectedData['MOD_USR_ID']);
		
		if(selectedData['DEL_YN'] == 'Y') {
			$('input:radio[name="del_yn"]:radio[value="Y"]').prop('checked', true);
		} else {
			$('input:radio[name="del_yn"]:radio[value="N"]').prop('checked', true);
		}
		
		$('#logListModal').modal('show');
	}
	
	
	//글쓰기
	function writeCode() {
		$('#cd_type_eng_nm').val('');
		$('#cd_eng_nm').val('');
		$('#cd_kor_nm').val('');
		$('#cd_desc').val('');
		$('#cd_ord').val('');
		
		$('#board_create_date').text('');
		$('#board_create_user_id').text('');
		$('#board_modify_date').text('');
		$('#board_modify_user_id').text('');
		
		$('input:radio[name="del_yn"]:radio[value="N"]').prop('checked', true);
		
		$('#logListModal').modal('show');
	}
	
	
	//입력 정보 확인 체크
	function checkPostInput() {
		let rtnCheck = true;
		
		if($('#cd_type_eng_nm').val() == '') {
			alert('코드 분류 영문명을 입력하세요');
			$('#cd_type_eng_nm').focus();
			return false;
		}
		
		if($('#cd_eng_nm').val() == '') {
			alert('코드 영문명을 입력하세요');
			$('#cd_eng_nm').focus();
			return false;
		}
		
		return rtnCheck;
	}
	
	
	//코드 등록
	function createCode() {
		let checkVal = checkPostInput();
		
		let changeNm = '';
		let changeQuery = '';
		if($('#board_create_user_id').text() == '') {
			changeNm = '등록';
			changeQuery = 'codeInsert';
		} else {
			changeNm = '수정';
			changeQuery = 'codeUpdate';
		}
		
		if(checkVal) {
			let msg = '코드를 ' + changeNm + '하시겠습니까?';
			if (confirm(msg)) {
				let formData = new FormData();
				
				formData.append('CD_TYPE_ENG_NM', $('#cd_type_eng_nm').val());
				formData.append('CD_ENG_NM', $('#cd_eng_nm').val());
				formData.append('CD_KOR_NM', $('#cd_kor_nm').val());
				formData.append('CD_DESC', $('#cd_desc').val());
				formData.append('CD_ORD', $('#cd_ord').val());
				
				formData.append('DEL_YN', $('input:radio[name="del_yn"]:checked').val());
				
				callAjaxForm('/admin/' + changeQuery + '.json', formData, function(data) {
					alert('코드가 ' + changeNm + '되었습니다.');
					$('#logListModal').modal('hide');
				});
		    }
		}
		
	}
	
</script>
</html>