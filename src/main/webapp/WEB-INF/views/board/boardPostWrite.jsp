<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);

	String boardId = (String)request.getParameter("BRD_ID");
	String postId = (String)request.getParameter("POST_ID");
	
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");
	Boolean portalAdminAuth = PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN");
	
	String attachBaseFileSize = (String)CustomProperties.getProperty("attach.base.file.size");
	String attachBaseFileExtension = (String)CustomProperties.getProperty("attach.base.file.extension");
	
	pageContext.setAttribute("attachBaseFileSize", attachBaseFileSize);
	pageContext.setAttribute("attachBaseFileExtension", attachBaseFileExtension);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>작성</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/daterangepicker.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/_custom/javascript/toastui-editor/toastui-editor.min.css" />
	
	<script type="text/javascript" charset="UTF-8"  src="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/daterangepicker.js"></script>
	<script type="text/javascript" charset="UTF-8"  src="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/moment.min.js"></script>
	<script type="text/javascript" charset="UTF-8"  src="${pageContext.request.contextPath}/_custom/javascript/toastui-editor/toastui-editor-all.min.js"></script>
	
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
		<p id="brd_nm" class="h3"></p>
		<p id="brd_desc" class="h6"></p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_post_create" class="btn btn-secondary btn-sm" onclick="changeBoardPost('CREATE')" style="display: none;">작성</button>
				<button id="btn_post_modify" class="btn btn-secondary btn-sm" onclick="changeBoardPost('UPDATE')" style="display: none;">저장</button>
			</div>
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm" onclick="moveCommunityPage(<%=boardId%>)">목록</button>
			</div>
		</div>
		<form name="form" method="post" enctype="multipart/form-data">
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
							<input type="text" id="post_title" class="form-control form-control-sm" title="제목" placeholder="제목을 입력하세요"></td>
					</tr>
					<tr id="post_write_not">
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
							<select id="post_type" class="form-select form-select-sm" style="width: 29%;">
								<option>메뉴얼</option>
								<option>용어사전</option>
								<option>동영상교육</option>
								<option>지점안내</option>
				            </select>
						</td>
					</tr>
					<tr id="post_popup_yn" style="display: none;">
						<td>
							<span>팝업여부</span>
						</td>
						<td class="text-center">
							<input type="checkbox" id="popup_yn" value="" onclick="checkPopUp(this)">
						</td>
						<td>
							<span>팝업일자</span>
						</td>
						<td colspan="5">
							<span id="post_popup_dt"> 
								<img src="${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/calendar-fill.svg" id="datefilter" name="datefilter" style="width: 20px; pointer-events:none;" />
								<input type="text" name="startDateInput" id="startDateInput" value="" readonly style="width: 200px;" readonly /> 
								<input type="text" name="endDateInput" id="endDateInput" value="" readonly	style="width: 200px;" readonly />
							</span>
						</td>
					</tr>
					<tr id="post_fix_yn" style="display: none;">
						<td id="post_fix_yn_div">
							<span>고정여부</span>
						</td>
						<td class="text-center">
							<input type="checkbox" id="fix_yn">
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
							<div>
								<input id="post_file" class="form-control" type="file" multiple>
							</div>
							<div id="post_file_output" class="list-group"></div>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>

	<jsp:include flush="true" page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	
	let editor;
	let filesObj = {};
	let fileObjIndex = 0;
	let deleteFileIds = [];
	
	$(function() {
		fnBoardPostInit();
		
		if(postId != null) {
			//수정
			
			let callParams = {
				  BRD_ID : boardId
				, POST_ID : postId
			};
			callAjaxPost('/board/boardPostDetail.json', callParams, function(data) {
				let postData = data['data'];
				let postFile = data['file'];
				let postLocation = data['location'];
				
				if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}' || <%=portalAdminAuth%>) {
					$('#btn_post_modify').show();
				}
				
				if(postData['BRD_NM'] == '') {
					alert('선택한 게시물이 존재하지 않습니다.');
					
					let pagePrams = [];
					pageGoPost('_self', '${pageContext.request.contextPath}/app/main/mainView.do', pagePrams);
				} else {
					displayBoardPostTag(postData, postFile, postLocation);
					displayBoardPostContents(postData, postFile, postLocation);
				}
			});
			
		} else {
			//신규
			$('#post_write_not').hide();
			
			let callParams = {
				  BRD_ID : boardId
			};
			callAjaxPost('/board/boardInfo.json', callParams, function(data) {
				let postData = data['data'];
				let postFile = data['file'];
				let postLocation = data['location'];
				
				if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}' 
					|| <%=portalAdminAuth%>
					|| postData['BRD_CRT_AUTH'].indexOf('"AUTH_ID":"ALL_USER"') > -1
					|| postData['BRD_CRT_AUTH'].indexOf('"AUTH_ID":"${mstrUserIdAttr}"') > -1
				) {
					$('#btn_post_create').show();
				}
				
				if(postData['BRD_NM'] == '') {
					alert('선택한 게시물이 존재하지 않습니다.');
					
					let pagePrams = [];
					pageGoPost('_self', '${pageContext.request.contextPath}/app/main/mainView.do', pagePrams);
				} else {
					displayBoardPostTag(postData, postFile, postLocation);
					displayBoardPostContents(postData, postFile, postLocation);
				}
			});
		}
	});
	
	
	//초기 함수
	function fnBoardPostInit() {
		
		//에디터 설정
		editor = new toastui.Editor({
			  el: document.querySelector('#post_content') // 에디터를 적용할 요소 (컨테이너)
			, height: '400px' // 에디터 영역의 높이 값 (OOOpx || auto)
			, initialEditType: 'wysiwyg' // 최초로 보여줄 에디터 타입 (markdown || wysiwyg)
			, initialValue: '' // 내용의 초기 값으로, 반드시 마크다운 문자열 형태여야 함
			, previewStyle: 'vertical' // 마크다운 프리뷰 스타일 (tab || vertical)
		});
		
		//달력 지우기
		$('#datefilter').on('cancel.daterangepicker', function(ev, picker) {
			$('#startDateInput').val('');
			$('#endDateInput').val('');
		});
		
		//달력 설정
		$('#datefilter').daterangepicker(
			{
				'locale': {
			         'format' : 'YYYY-MM-DD hh:mm A'
			        , 'separator' : ' ~ '
			        , 'applyLabel' : '확인'
			        , 'cancelLabel' : '지우기'
			        , 'fromLabel' : 'From'
			        , 'toLabel' : 'To'
			        , 'customRangeLabel' : 'Custom'
			        , 'weekLabel' : 'W'
			        , 'daysOfWeek' : ['일', '월', '화', '수', '목', '금', '토']
					, 'monthNames' : ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
				}
				, 'startDate' : new Date()
				, 'endDate' : new Date()
				, 'minDate' : moment()
				, 'drops' : 'auto'
			}
			, function (start, end, label) {
				$('#startDateInput').val(start.format('YYYY-MM-DD'));
				$('#endDateInput').val(end.format('YYYY-MM-DD'));
			}
		);
		
	}
	
	
	//태그 표시
	function displayBoardPostTag(postData, postFile, postLocation) {
		document.title = postData['BRD_NM'] + ' - 상세';
		
		$('#brd_nm').text(postData['BRD_NM']);
		$('#brd_desc').text(postData['BRD_DESC']);
		
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
			
			//파일 변경
			$('#post_file').change(function(e) {
				let files = e.target.files;
				
				for (let i=0; i < files.length; i++) {
					let appendCheck = true;
					let fileExtension = getExtensionOfFilename(files[i]['name']);
					if(files[i]['size'] > ${attachBaseFileSize}) {
						appendCheck = false;
						alert(files[i]['name'] + '의 크기가 제한에 초과되었습니다.\n제한 용량 : ' + formatFileSize(${attachBaseFileSize}) + '\n현재 용량 : ' + formatFileSize(files[i]['size']));
					} else if('${attachBaseFileExtension}'.indexOf(fileExtension.toLocaleUpperCase()) == -1) {
						appendCheck = false;
						alert(files[i]['name'] + '의 확장자가 지원되지 않습니다.\n현재 확장자 : ' + fileExtension);
					}
					
					if(appendCheck) {
						filesObj[fileObjIndex++] = files[i];
						
						let strongHtml = $('<span>', {
							  class : 'text-gray-dark'
							, text : files[i]['name'] + '\t' + formatFileSize(files[i]['size'])
							, title : files[i]['name']
						});
						
						let aHtml = $('<a>', {
							  class : 'text-gray-dark'
							, text : '삭제'
							, title : '삭제'
							, style : 'cursor:pointer;'
							, 'data-index' : fileObjIndex - 1
							, click : function(e) {
								delete filesObj[$(this).attr('data-index')];
								$(this).parent().parent().parent().remove();
							}
						});
						
						let divHtml1 = $('<div>', {
							class : 'd-flex justify-content-between'
						}).append(strongHtml).append(aHtml);
						
						let divHtml2 = $('<div>', {
							class : 'pb-3 mb-0 small lh-sm border-bottom w-100'
						}).append(divHtml1);
						
						let divHtml3 = $('<div>', {
							class : 'd-flex text-body-secondary pt-3'
						}).append(divHtml2);
						
						$('#post_file_output').append(divHtml3);
					}
				}
				
				$('#post_file').val('');
			});
		}
	}
	
	
	//내용 표시
	function displayBoardPostContents(postData, postFile) {
		$('#post_title').val(postData['POST_TITLE']);
		$('#post_create_user_id').text(postData['CRT_USR_ID']);
		$('#post_create_user_dept_name').text(postData['CRT_USR_DEPT_NM']);
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_count').text(postData['POST_VIEW_COUNT']);
		
		if(postData['POST_TYPE_YN'] == 'Y') {
			if(postData['POST_TYPE']) {
				$("#post_type").val(postData['POST_TYPE']).prop("selected", true);
			}
		}
		
		if(postData['POST_POPUP_YN'] == 'Y') {
			if(postData['POPUP_YN'] == 'Y') {
				$('#popup_yn').prop('checked', true);
				let datefilter_elem = $('#datefilter');
				datefilter_elem.attr('src', '${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/calendar-plus.svg');
				datefilter_elem.css('pointer-events', 'auto');
			} else {
				$('#popup_yn').prop('checked', false);
			}
			
			if(postData['POPUP_START_DT_TM'] || postData['POPUP_END_DT_TM']) {
				$('#startDateInput').val(changeDisplayDate(postData['POPUP_START_DT_TM'], 'YYYY-MM-DD'));
				$('#endDateInput').val(changeDisplayDate(postData['POPUP_END_DT_TM'], 'YYYY-MM-DD'));
			}
		}
		
		if(postData['POST_FIX_YN'] == 'Y') {
			if(postData['FIX_YN'] == 'Y') {
				$('#fix_yn').prop('checked', true);
			} else {
				$('#fix_yn').prop('checked', false);
			}
		}
		
		editor.setHTML(postData['POST_CONTENT']);
		
		if(postData['POST_FILE_YN'] == 'Y') {
			//첨부파일 리스트 표시
			if(postFile) {
				postFile.forEach((attachFile, idx) => {
					let strongHtml = $('<span>', {
						  class : 'text-gray-dark'
						, text : attachFile['ORG_FILE_NM'] + '.' + attachFile['FILE_EXT'] + '\t' + formatFileSize(attachFile['FILE_SIZE'])
						, title : attachFile['ORG_FILE_NM'] + '.' + attachFile['FILE_EXT']
					});
					
					let aHtml = $('<a>', {
						  class : 'text-gray-dark'
						, text : '삭제'
						, title : '삭제'
						, style : 'cursor:pointer;'
						, click : function(e) {
							deleteFileIds.push(attachFile['FILE_ID']);
							$(this).parent().parent().parent().remove();
						}
					});
					
					let divHtml1 = $('<div>', {
						class : 'd-flex justify-content-between'
					}).append(strongHtml).append(aHtml);
					
					let divHtml2 = $('<div>', {
						class : 'pb-3 mb-0 small lh-sm border-bottom w-100'
					}).append(divHtml1);
					
					let divHtml3 = $('<div>', {
						class : 'd-flex text-body-secondary pt-3'
					}).append(divHtml2);
					
					$('#post_file_output').append(divHtml3);
				});
			}
		}
	}
	
	
	//체크박스 달력 설정
	function checkPopUp(checkbox) {
	    let datefilter_elem = $('#datefilter');
	    let startDateInput_elem = $('#startDateInput');
	    let endDateInput_elem = $('#endDateInput');

	    if (checkbox.checked) {
			checkbox.value = 'Y';
			datefilter_elem.attr('src', '${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/calendar-plus.svg');
			datefilter_elem.prop('disabled', false);
			startDateInput_elem.prop('disabled', false);
			endDateInput_elem.prop('disabled', false);
	        //활성화
			datefilter_elem.css('pointer-events', 'auto');
	    } else {
			checkbox.value = 'N';
			datefilter_elem.attr('src', '${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/calendar-fill.svg');
			datefilter_elem.prop('disabled', true);
			startDateInput_elem.prop('disabled', true);
			endDateInput_elem.prop('disabled', true);
	        //다시 비활성화
			datefilter_elem.css('pointer-events', 'none');
	    }
	}
	
	
	//입력 정보 확인 체크
	function checkPostInput() {
		let rtnCheck = true;
		
		if($('#post_title').val() == '') {
			alert('제목을 입력하세요');
			$('#post_title').focus();
			return false;
		}
		
		if(editor.getHTML() == '') {
			alert('내용을 입력하세요');
			editor.getHTML().focus();
			return false;
		}
		
		if($('#popup_yn').val() == 'Y' && $('#startDateInput').val() == '') {
			alert('팝업일자를 선택하세요');
			return false;
		}

		return rtnCheck;
	}
	
	
	//게시글 등록 및 수정
	function changeBoardPost(changeType) {
		let checkVal = checkPostInput();
		
		let changeNm = '';
		let changeQuery = '';
		if(changeType == 'CREATE') {
			changeNm = '등록';
			changeQuery = 'boardPostInsert';
		} else if(changeType == 'UPDATE') {
			changeNm = '수정';
			changeQuery = 'boardPostUpdate';
		}
		
		if(checkVal) {
			let msg = '게시글을 '+ changeNm +'하시겠습니까?';
			if (confirm(msg)) {
				let formData = new FormData();
				
				formData.append('POST_ID', postId);
				formData.append('BRD_ID', boardId);
				formData.append('POST_TYPE', $('#post_type').val());
				formData.append('POST_TITLE', $('#post_title').val());
				formData.append('POST_CONTENT', editor.getHTML());
				formData.append('POPUP_YN', $('#popup_yn').is(':checked') ? 'Y' : 'N');
				formData.append('FIX_YN', $('#fix_yn').is(':checked') ? 'Y' : 'N');
				formData.append('POPUP_START_DT_TM', $('#startDateInput').val());
				formData.append('POPUP_END_DT_TM', $('#endDateInput').val());
				
				//multiple 파일 갯수에 만큼 저장
				Object.values(filesObj).forEach((file, idx) => {
					formData.append('ATTACH_FILE_' + idx, file);
				});
				
				if(changeType == 'UPDATE') {
					//삭제 첨부파일 추가
					deleteFileIds.forEach(id => {
						formData.append('deleteFileIds', id);
					});
				}
				
				callAjaxForm('/board/'+changeQuery+'.json', formData, function(data) {
					alert('게시글이 '+ changeNm +'되었습니다.');
					detailBoardPost(boardId, data['POST_ID']);//POST_ID 받아오는 값
				});
		    }
		}
	}
</script>
</html>