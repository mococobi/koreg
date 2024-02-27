<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<body>
	<div class="modal fade board_custom_div" id="editUserListModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title fs-5" id="exampleModalLabel">유저 편집</h3>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div>
						<div style="width: 45%; float: left;">
							<div>
								<input type="checkbox" class="modal_auth_group" data-idx="ALL_USER"><span>전체 사용자</span>
							</div>
							<div class="container py-4" style="max-width: 100%;">
								<div class="row mb-3">
									<div class="col-md-2">
										<select id="searchKey" class="form-select form-select-sm">
											<option value="USER_ID">사번</option>
											<option value="USER_NAME">이름</option>
										</select>
									</div>
									<div class="col">
										<input id="searchVal" class="form-control form-control-sm" type="search" placeholder="Search" aria-label="Search">
									</div>
									<div class="col-md-2">
										<button class="btn btn-primary btn-sm" onclick="searchBoardPostList()">조회</button>
									</div>
								</div>
							</div>
							<div style="min-height: 410px;">
								<table id="modalUserListTable" class="table hover table-striped table-bordered dataTablesCommonStyle" style="width: 100%">
									<colgroup>
										<col width="50%">
										<col >
									</colgroup>
									<thead>
					    				<tr>
						     				<th>사번</th>
						      				<th>이름</th>
						    			</tr>
						  			</thead>
								</table>
							</div>
						</div>
						<div style="width: 10%; float: left; display: grid;">
							<button id="modal_user_add" type="button" class="btn btn-primary" style="margin-top: 150px;width: 50px;margin-left: 30px;">추가</button>
							<button id="modal_user_remove" type="button" class="btn btn-primary" style="margin-top: 10px;width: 50px;margin-left: 30px;">제거</button>
						</div>
						<div style="width: 45%; float: left;">
							<table class="table hover table-striped table-bordered dataTablesCommonStyle">
								<colgroup>
									<col width="10%">
									<col width="40%">
									<col >
								</colgroup>
								<thead>
				    				<tr>
				    					<th>선택</th>
					     				<th>사번</th>
					      				<th>이름</th>
					    			</tr>
					  			</thead>
					  			<tbody id="modalUserListSelectTableTbody">
					  			</tbody>
							</table>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="modalUserListApply()">확인</button>
					<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	let searchKey = '';
	let searchVal = '';
	
	$(function() {
		
		$('#searchVal').keypress(function(e){
			if(e.keyCode && e.keyCode == 13){
				searchBoardPostList();
			}
		});
		
		let row;
		//Row 클릭 이벤트
		$('#modalUserListTable').on('click', 'tr', function () {
			row = $('#modalUserListTable').DataTable().row($(this)).data();
			console.log(row);
		});

		
		//유저 추가 버튼 클릭
		$('#modal_user_add').on('click', function () {
			//체크 박스 확인
			for(i=0; i<$('.modal_auth_group').length; i++) {
				if($('.modal_auth_group').eq(i).is(':checked') && $('#modalUserListSelectTableTbody').find('[data-idx='+ $('.modal_auth_group').eq(i).attr('data-idx') +']').length == 0) {
					addUserListHtml($('.modal_auth_group').eq(i).attr('data-idx'), $('.modal_auth_group').eq(i).next().text());
				}
			}
			
			//그리드 확인
			if($('#modalUserListSelectTableTbody').find('[data-idx='+ row['ABBREVIATION'] +']').length == 0) {
				addUserListHtml(row['ABBREVIATION'], row['OBJECT_NAME']);
			}
		});
		
		
		//유저 삭제 버튼 클릭
		$('#modal_user_remove').on('click', function () {
			for(i=$('#modalUserListSelectTableTbody').find('tr').length-1; i>=0; i--) {
				if($('#modalUserListSelectTableTbody').find('tr').eq(i).find('input[type="checkbox"]').is(':checked')) {
					$('#modalUserListSelectTableTbody').find('tr').eq(i).remove();
				}
			}
		});
		
	});
	
	
	//유저 리스트 이름 추출
	function getUserListSpanName(userList) {
		let rtnUserNm = '';
		
		if(userList.length > 0) {
			JSON.parse(userList).forEach((authMap, idx) => {
				if(rtnUserNm == '') {
					rtnUserNm += authMap['AUTH_NAME'];
				} else {
					rtnUserNm += ', ' +authMap['AUTH_NAME'];
				}
			});
		}
		
		return rtnUserNm;
	}
	
	
	//왼쪽 - 유저 검색
	function searchBoardPostList() {
		searchKey = $('#searchKey option:selected').val();
		searchVal = $('#searchVal').val();
		
		$('#' + 'modalUserListTable').DataTable().ajax.reload();
	}
	
	
	//왼쪽 - 유저 리스트 초기화
	function fnUserInit(modalTableId) {
		let listViewCount = 10;
		
		if($('#' + modalTableId).find('tbody').length == 0) {
			$('#' + modalTableId).DataTable({
				  lengthChange : false
				, keys: true
				, searching : false
				, serverSide : true
				, processing : true
				, ordering : true
				, pageLength : listViewCount
				, pagingType : 'full_numbers'
				, order : [[ 1, 'desc' ]]
				, ajax : {
					  url : '${pageContext.request.contextPath}/app/user/userListGrid.json'
					, type : 'POST'
					, data : function(data) {
						data['listViewCount'] = listViewCount;
						data['searchKey'] = searchKey;
						data['searchVal'] = searchVal;
						
						data['customOrder1'] = $('#' + modalTableId).DataTable().order()[0][0];
						data['customOrder2'] = $('#' + modalTableId).DataTable().order()[0][1];
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
						errorProcess(jqXHR, textStatus, errorThrown);
				    }
				}
				, language : commonDatatableLanguage()
				, columns : [
					{
						  data : 'ABBREVIATION'
						, render : function (data, type, row) {
							let rtnData = '-';
							if(data) {
								rtnData = XSSCheck(data, 0);
							}
							
							return rtnData;
						}
		            }
					, {
						  data : 'OBJECT_NAME'
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
		} else {
			$('#' + modalTableId).DataTable().ajax.reload();
		}
		
		$('#modalUserListSelectTableTbody').html('');
		JSON.parse(modalUserList).forEach((user, idx) => {
			addUserListHtml(user['AUTH_ID'], user['AUTH_NAME']);
		});
		
	}
	
	
	//오른쪽 - 선택 유저 리스트 HTML 생성
	function addUserListHtml(userId, userName) {
		let checkBoxInputHtml = $('<input>', {
			  type : 'checkbox'
			, checked : false
		});
		
		let checkBoxHtml = $('<td>', {
			class : 'textCenter'
		}).append(checkBoxInputHtml);
		
		let userIdHtml = $('<td>', {
			text : userId
		});
		
		let userNameHtml = $('<td>', {
			text : userName
		});
		
		let trHtml = $('<tr>', {
			  'data-idx' : userId
			, 'data-name' : userName
		}).append(checkBoxHtml).append(userIdHtml).append(userNameHtml);
		
		$('#modalUserListSelectTableTbody').append(trHtml);
	}
	
	
	//유저 리스트 확인 버튼 - 적용
	let modalUserList = JSON.stringify([]);
	function modalUserListApply() {
		let authList = [];
		for(i=0; i<$('#modalUserListSelectTableTbody tr').length; i++) {
			let authId = $('#modalUserListSelectTableTbody tr').eq(i).attr('data-idx');
			let authName = $('#modalUserListSelectTableTbody tr').eq(i).attr('data-name');
			
			let authMap = {};
			authMap.AUTH_ID = authId;
			authMap.AUTH_NAME = authName;
			
			authList.push(authMap);
		}
		
		modalUserList = JSON.stringify(authList);
		$('#board_create_auth').text(getUserListSpanName(modalUserList));
		$('#editUserListModal').modal('hide');
	}
	
</script>
</html>