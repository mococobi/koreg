<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String boardId = (String) request.getParameter("boardId");
String postId = (String) request.getParameter("postId");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판 - 글쓰기</title>

<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
<script type="text/javascript"
	src="https://cdn.jsdelivr.net/jquery/latest/jquery.min.js"></script>
<script type="text/javascript"
	src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
<script type="text/javascript"
	src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
<script
	src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<link rel="stylesheet"
	href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
</head>
<body>
	<jsp:include flush="true"
		page="/WEB-INF/views/include/portalDivStart.jsp" />

	<div class="container py-4">
		<p class="h3">${postData['BRD_NM']}</p>
		<p class="h6">${postData['BRD_DESC']}</p>
		<div class="row mb-3">
			<div class="col">
				<button id="btn_post_modify" class="btn btn-secondary btn-sm"
					onclick="createBoardPost()" style="display: none;">저장</button>
			</div>
			<div class="col text-end">
				<button class="btn btn-secondary btn-sm"
					onclick="moveCommunityPage(<%=boardId%>)">목록</button>
			</div>
		</div>
		<form name="form" method="post" enctype="multipart/form-data">
			<table id="board_table" class="table table-sm table-bordered"
				style="width: 100%; border: 1px solid; border-collapse: collapse;">
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
						<td><span>제목</span></td>
						<td colspan="7"><input type="text" id="post_title"
							class="form-control form-control-sm" title="제목"
							placeholder="제목을 입력하세요"></td>
					</tr>
					<c:if test="${postData['POST_ID'] != 'NULL'}">
					<tr>
						<td><span>작성자</span></td>
						<td><span id="post_create_user_id"></span></td>
						<td><span>부서</span></td>
						<td><span id="post_create_user_dept_name"></span></td>
						<td><span>작성일자</span></td>
						<td><span id="post_create_date"></span></td>
						<td><span>조회</span></td>
						<td><span id="post_count"></span></td>
					</tr>
					</c:if>
					<c:if test="${postData['POST_POPUP_YN'] eq 'Y'}">
						<tr id="post_popup_yn_div">
							<td><span>팝업여부</span></td>
							<td class="text-center"><input type="checkbox" id="popup_yn" value="N" onclick="checkPopUp(this)"></td>
							<td><span>팝업일자</span></td>
							<td colspan="5"><span id="post_popup_dt"> 
							<img src="../../image/bootstrap-icons-1.11.2/calendar-fill.svg"	id="datefilter" name="datefilter" style="width: 20px;" /> 
							<input type="text" name="startDateInput" id="startDateInput" value="" readonly style="width: 200px;" disabled /> 
							<input type="text" name="endDateInput" id="endDateInput" value="" readonly	style="width: 200px;" disabled />
							</span></td>
						</tr>
					</c:if>
					<tr>
						<c:if test="${postData['POST_FIX_YN'] eq 'Y'}">
							<td id="post_fix_yn_div"><span>상단 고정</span></td>
							<td><input type="checkbox" id="post_fix_yn" disabled>
							</td>
						</c:if>
						<c:if test="${postData['POST_SECRET_YN'] eq 'Y'}">
							<td id="post_secret_yn_div"><span>비밀글</span></td>
							<td><input type="checkbox" id="post_secret_yn" disabled>
							</td>
						</c:if>
					</tr>
					<c:if test="${postData['BRD_VIEW_AUTH'] eq 'Y'}">
						<td><span>보기 권한</span></td>
						<td colspan="7"></td>
					</c:if>
					<tr>
					</tr>
					<tr>
						<td><span>내용</span></td>
						<td colspan="7">
							<div id="post_content" style="min-height: 300px;"></div>
						</td>
					</tr>
					<c:if test="${postData['POST_FILE_YN'] eq 'Y'}">
						<tr id="post_file_yn_div">
							<td><span>첨부 파일</span></td>
							<td colspan="7">
								<div style="">
									<input id="post_file" class="form-control" type="file" multiple
									accept="text/plain,
							                application/vnd.ms-excel,
							                application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,
							                text/html,
							                .pdf,
							                image/*">
								</div>
								<div>
								<output id="output" style="display:block; white-space:pre-wrap;"></output>
								</div>
							</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</form>
		<button type="button" onclick='createBoardPost()'>작성</button>
	</div>

	<jsp:include flush="true"
		page="/WEB-INF/views/include/portalDivEnd.jsp" />
</body>
<script type="text/javascript">
	let boardId = <%=boardId%>;
	let postId = <%=postId%>;
	
	$(function() {
		if(postId != null) {
			//수정
			fnBoardPostInit();
		} else {
			//신규
			
		}
	});
	
	
	//초기 함수
	function fnBoardPostInit() {
		let callParams = {
			  boardId : boardId
			, postId : postId
		};
		callAjaxPost('/board/boardPostDetail.json', callParams, function(data){
			let postData = data['data'];
			
			//displayCheck(postData);
			displayContents(postData);
		});
	}
	
	
	//내용 표시
	function displayContents(postData) {
		
		if(postData['CRT_USR_ID'] == '${mstrUserIdAttr}') {
			$('#btn_post_modify').show();
			$('#btn_post_delete').show();
		}
		
		$('#post_title').val(postData['POST_TITLE']);
		
		$('#post_create_user_id').text(postData['CRT_USR_ID']);
		$('#post_create_user_dept_name').text(postData['CRT_USR_DEPT_NM']);
		$('#post_create_date').text(changeDisplayDate(postData['CRT_DT_TM'], 'YYYY-MM-DD'));
		$('#post_count').text(postData['POST_VIEW_COUNT']);
		
		$('#post_content').html(postData['POST_CONTENT']);
		
		if(postData['POST_POPUP_YN'] == 'Y') {
			$('#post_popup_yn_div').prop('checked', true);
			$('#popup_yn').show();
		} else {
			$('#post_popup_yn_div').prop('checked', false);
			$('#popup_yn').hide();
		}
		
		if(postData['POPUP_START_DT_TM'] || postData['POPUP_END_DT_TM']) {
			$('#post_popup_dt').text(changeDisplayDate(postData['POPUP_START_DT_TM'], 'YYYY-MM-DD') + ' ~ ' + changeDisplayDate(postData['POPUP_END_DT_TM']), 'YYYY-MM-DD');
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
		
		//TODO 첨부파일 리스트 표시
		if(postData['file']) {
			postData.forEach((attachFile, idx) => {
				
			});
		}
	}
	
	
	//첨부파일 이름 출력
	const input = document.getElementById('post_file')
	const output = document.getElementById('output')

	document.getElementById('post_file').addEventListener('input', (event) => {
	const files = event.target.files
	output.textContent = Array.from(files).map(file => '파일명 : ' + file.name + '   [크기 : ' + formatFileSize(file.size) + ']').join('\n')
	});
	
	function formatFileSize(size) {
		  if (size > 9999) {
		    const fileSizeInKB = size / 1024;
		    return Math.round(fileSizeInKB.toFixed(3)) + ' MB';
		  } else if(size > 999){
			  const fileSizeInKB = size / 1024;
			    return Math.round(fileSizeInKB.toFixed(2)) + ' KB';
		  } else {
		    return (size).toLocaleString() + ' Bytes';
		  }
		}
	
	
	//체크박스 달력 설정
	function checkPopUp(checkbox) {
	    let datefilter_elem = document.querySelector('#datefilter');
	    let startDateInput_elem = document.querySelector('#startDateInput');
	    let endDateInput_elem = document.querySelector('#endDateInput');

	    if (checkbox.checked) {
	    	checkbox.value = 'Y';
	        datefilter_elem.src = '../../image/bootstrap-icons-1.11.2/calendar-plus.svg';
	        datefilter_elem.disabled = false;
	        startDateInput_elem.disabled = false;
	        endDateInput_elem.disabled = false;
	        //활성화
	        datefilter_elem.style.pointerEvents = 'auto';
	        
	        
	    } else {
	    	checkbox.value = 'N';
	        datefilter_elem.src = '../../image/bootstrap-icons-1.11.2/calendar-fill.svg';
	        datefilter_elem.disabled = true;
	        startDateInput_elem.disabled = true;
	        endDateInput_elem.disabled = true;
	        //다시 비활성화
	        datefilter_elem.style.pointerEvents = 'none';
	        
	    }
	}


	//달력 초기 설정
	document.addEventListener("DOMContentLoaded", function () {
	    let datefilter_elem = document.getElementById('datefilter');
	    //ponterEvents는 css이벤트, style적용 비활성화
	    datefilter_elem.style.pointerEvents = 'none';
	});
	
	
	//팝업일자 설정
	$('#datefilter').daterangepicker({
	    'locale': {
	         'format': 'YYYY-MM-DD hh:mm A'
	        ,'separator': ' ~ '
	        ,'applyLabel': '확인'
	        ,'cancelLabel': '지우기'
	        ,'fromLabel': 'From'
	        ,'toLabel': 'To'
	        ,'customRangeLabel': 'Custom'
	        ,'weekLabel': 'W'
	        ,'daysOfWeek': ['일', '월', '화', '수', '목', '금', '토']
			,'monthNames': ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
		}
		,'startDate': new Date()
		,'endDate': new Date()
		,'minDate': moment()
		,'drops': 'auto'
	}, function (start, end, label) {
		$('#startDateInput').val(start.format('YYYY-MM-DD'));
	    $('#endDateInput').val(end.format('YYYY-MM-DD'));
	    console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
	});
	//달력 지우기
	$('#datefilter').on('cancel.daterangepicker', function(ev, picker) {  
	    $('#startDateInput').val('');
	    $('#endDateInput').val('');
	    console.log('Date range selection canceled');
	});

	
     let editor = new toastui.Editor({
         el: document.querySelector('#post_content'), // 에디터를 적용할 요소 (컨테이너)
         height: '400px',                        // 에디터 영역의 높이 값 (OOOpx || auto)
         initialEditType: 'wysiwyg',            // 최초로 보여줄 에디터 타입 (markdown || wysiwyg)
         initialValue: '',     					// 내용의 초기 값으로, 반드시 마크다운 문자열 형태여야 함
         previewStyle: 'vertical'                // 마크다운 프리뷰 스타일 (tab || vertical)
     });

	
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
		
		// 첨부 파일 유효성(용량) 체크
		let invalidFiles=[];
		
		$.each($('#post_file')[0].files, function(idx, item) {
			console.log(idx + ":: " + item + ".");
			if (item.size > 10000) {
				invalidFiles.push(item.name);
			}
		});
		
		if (invalidFiles.length > 0) {
			alert("다음 파일의 첨부 허용된 용량을 초과하였습니다.\n확인 후 다시 첨부해 주세요.\n" + invalidFiles.join("\n")) ;
			return false;
		}
		
		// 첨부 파일 유효성(확장자) 체크
		let invalidExtensions=[];
		let allowedExtensions = ['jpg', 'jpeg', 'png', 'gif', 'txt', 'pdf'];
		
		$.each($('#post_file')[0].files, function(idx, item) {
			
			let fileExtension = item.name.split('.').pop().toLowerCase();
			
			if (!allowedExtensions.includes(fileExtension)) {
				invalidExtensions.push(item.name);
			}
		});
		
		if (invalidExtensions.length > 0) {
			alert("다음 형식의 파일은 첨부할 수 없습니다.\n확인 후 다시 첨부해 주세요.\n" + invalidExtensions.join("\n")) ;
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
				for(i=0; i<$('#post_file')[0].files.length; i++){
					formData.append('FILE_'+i, $('#post_file')[0].files[i]);
				}
				
				
				callAjaxForm('/board/boardPostInsert.json', formData, function(data) {
					console.log(data);
					alert('게시글이 등록되었습니다.');
					//moveCommunityPage(boardId);
					if (data.POST_ID) {
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