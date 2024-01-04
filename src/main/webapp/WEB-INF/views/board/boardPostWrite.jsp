<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String boardId = (String)request.getParameter("BRD_ID");
	String postId = (String)request.getParameter("POST_ID");
	
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");
	
	
	String attachBaseFileSize = (String)CustomProperties.getProperty("attach.base.file.size");
	String attachBaseFileExtension = (String)CustomProperties.getProperty("attach.base.file.extension");
	
	pageContext.setAttribute("attachBaseFileSize", attachBaseFileSize);
	pageContext.setAttribute("attachBaseFileExtension", attachBaseFileExtension);
	
	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>${postData['BRD_NM']} - 작성</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/daterangepicker.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/_custom/javascript/toastui-editor/toastui-editor.min.css" />
	
	<script src="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/daterangepicker.js"></script>
	<script src="${pageContext.request.contextPath}/_custom/javascript/daterangepicker/moment.min.js"></script>
	<script src="${pageContext.request.contextPath}/_custom/javascript/toastui-editor/toastui-editor-all.min.js"></script>
	
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
		<p class="h3">${postData['BRD_NM']}</p>
		<p class="h6">${postData['BRD_DESC']}</p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_post_modify" class="btn btn-secondary btn-sm" onclick="updateBoardPost()" style="display: none;">저장</button>
				<button id="btn_post_write" class="btn btn-secondary btn-sm" onclick='createBoardPost()'>작성</button>
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
					<c:if test="${postData['POST_POPUP_YN'] eq 'Y'}">
						<tr id="post_popup_yn_div">
							<td>
								<span>팝업여부</span>
							</td>
							<td class="text-center">
								<input type="checkbox" id="popup_yn" value="N" onclick="checkPopUp(this)">
							</td>
							<td>
								<span>팝업일자</span>
							</td>
							<td colspan="5">
								<span id="post_popup_dt"> 
									<img src="${pageContext.request.contextPath}/_custom/image/bootstrap-icons-1.11.2/calendar-fill.svg" id="datefilter" name="datefilter" style="width: 20px; pointer-events:none;" />
									<input type="text" name="startDateInput" id="startDateInput" value="" readonly style="width: 200px;" disabled /> 
									<input type="text" name="endDateInput" id="endDateInput" value="" readonly	style="width: 200px;" disabled />
								</span>
							</td>
						</tr>
					</c:if>
					<tr>
						<c:if test="${postData['POST_FIX_YN'] eq 'Y'}">
							<td id="post_fix_yn_div">
								<span>상단 고정</span>
							</td>
							<td>
								<input type="checkbox" id="post_fix_yn" disabled>
							</td>
						</c:if>
						<c:if test="${postData['POST_SECRET_YN'] eq 'Y'}">
							<td id="post_secret_yn_div">
								<span>비밀글</span>
							</td>
							<td>
								<input type="checkbox" id="post_secret_yn" disabled>
							</td>
						</c:if>
					</tr>
					<c:if test="${postData['BRD_VIEW_AUTH'] eq 'Y'}">
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
					<c:if test="${postData['POST_FILE_YN'] eq 'Y'}">
						<tr id="post_file_yn_div">
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
					</c:if>
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
				
			callAjaxPost('/board/boardPostDetail.json', callParams, function(data){
				let postData = data['data'];
				let postFile = data['file'];
				
				displayContents(postData, postFile);
			});
		} else {
			//신규
			$('#post_write_not').hide();
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
		$('#datefilter').daterangepicker({
				'locale': {
			         'format': 'YYYY-MM-DD hh:mm A'
			        , 'separator': ' ~ '
			        , 'applyLabel': '확인'
			        , 'cancelLabel': '지우기'
			        , 'fromLabel': 'From'
			        , 'toLabel': 'To'
			        , 'customRangeLabel': 'Custom'
			        , 'weekLabel': 'W'
			        , 'daysOfWeek': ['일', '월', '화', '수', '목', '금', '토']
					, 'monthNames': ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
				}
				, 'startDate': new Date()
				, 'endDate': new Date()
				, 'minDate': moment()
				, 'drops': 'auto'
			}
			, function (start, end, label) {
				$('#startDateInput').val(start.format('YYYY-MM-DD'));
				$('#endDateInput').val(end.format('YYYY-MM-DD'));
			}
		);
		
		
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
	
	
	//내용 표시
	function displayContents(postData, postFile) {
		
		if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}') {
			$('#btn_post_write').hide();
			$('#btn_post_modify').show();
			$('#btn_post_delete').show();
		}
		
		$('#post_title').val(postData['POST_TITLE']);
		$('#post_create_user_id').text(postData['CRT_USR_ID']);
		$('#post_create_user_dept_name').text(postData['CRT_USR_DEPT_NM']);
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_count').text(postData['POST_VIEW_COUNT']);
		editor.setHTML(postData['POST_CONTENT']);
		
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

		return rtnCheck;
	}
	
	
	//게시글 등록
	function createBoardPost() {
		let checkVal = checkPostInput();
		
		if(checkVal) {
			let msg = '게시글을 등록하시겠습니까?';
			if (confirm(msg)) {
				let formData = new FormData();
				
				formData.append('BRD_ID', boardId);
				formData.append('POST_TITLE', $('#post_title').val());
				formData.append('POST_CONTENT', editor.getHTML());
				formData.append('POPUP_YN', $('#popup_yn').val());
				formData.append('POPUP_START_DT_TM', $('#startDateInput').val());
				formData.append('POPUP_END_DT_TM', $('#endDateInput').val());
				//multiple 파일 갯수에 만큼 저장
				Object.values(filesObj).forEach((file, idx) => {
					formData.append('ATTACH_FILE_' + idx, file);
				});
				
				callAjaxForm('/board/boardPostInsert.json', formData, function(data) {
					alert('게시글이 등록되었습니다.');
					if (data['POST_ID']) {
						detailBoardPost(boardId, data.POST_ID);//POST_ID 받아오는 값
					} else {
						console.log('Undefined. post id [%s]', data.POST_ID);
					}
				});
		    }
		}
	}
	
		
	//게시글 수정
	function updateBoardPost() {
		let checkVal = checkPostInput();
		
		if(checkVal) {
			let msg = '게시글을 수정하시겠습니까?';
			if (confirm(msg)) {
				let formData = new FormData();
				
				formData.append('POST_ID', postId);
				formData.append('BRD_ID', boardId);
				formData.append('POST_TITLE', $('#post_title').val());
				formData.append('POST_CONTENT', editor.getHTML());
				formData.append('POPUP_YN', $('#popup_yn').val());
				formData.append('POPUP_START_DT_TM', $('#startDateInput').val());
				formData.append('POPUP_END_DT_TM', $('#endDateInput').val());
				//multiple 파일 갯수에 만큼 저장
				Object.values(filesObj).forEach((file, idx) => {
					formData.append('ATTACH_FILE_' + idx, file);
				});
				
				//삭제 첨부파일 추가
				deleteFileIds.forEach(id => {
					formData.append('deleteFileIds', id);
				});
				
				callAjaxForm('/board/boardPostUpdate.json', formData, function(data) {
					alert('게시글이 수정되었습니다.');
					if (data['POST_ID']) {
						detailBoardPost(boardId, data.POST_ID);//POST_ID 받아오는 값으로 변경
					} else {
						console.log('Undefined. post id [%s]', data.POST_ID);
					}
				});
		    }
		}
	}
</script>
</html>