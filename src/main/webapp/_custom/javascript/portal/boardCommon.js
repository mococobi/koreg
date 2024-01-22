//게시물 검색
function searchBoardPostList() {
	searchKey = $('#searchKey option:selected').val();
	searchVal = $('#searchVal').val();
	
	if($('#boardPostTable').length > 0) {
		$('#boardPostTable').DataTable().ajax.reload();
	} else {
		fnBoardInit(boardId);
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


//게시물 작성 - 페이지 이동
function writeBoardPost() {
	let pagePrams = [
		['BRD_ID', boardId]
	];
	pageGoPost('_self', __contextPath + '/app/board/boardPostWriteView.do', pagePrams);
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
			formData.append('POPUP_YN', $('#popup_yn').is(":checked")?"Y":"N");
			formData.append('POPUP_START_DT_TM', $('#startDateInput').val());
			formData.append('POPUP_END_DT_TM', $('#endDateInput').val());
			
			//multiple 파일 갯수에 만큼 저장
			Object.values(filesObj).forEach((file, idx) => {
				formData.append('ATTACH_FILE_' + idx, file);
			});
			
			callAjaxForm('/board/boardPostInsert.json', formData, function(data) {
				alert('게시글이 등록되었습니다.');
				detailBoardPost(boardId, data['POST_ID']);//POST_ID 받아오는 값
			});
	    }
	}
}


//게시물 수정 - 페이지 이동
function modifyBoardPost() {
	let pagePrams = [
		  ['BRD_ID', boardId]
		, ['POST_ID', postId]
	];
	
	pageGoPost('_self', __contextPath + '/app/board/boardPostWriteView.do', pagePrams);
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
			formData.append('POPUP_YN', $('#popup_yn').is(":checked")?"Y":"N");
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
				detailBoardPost(boardId, data['POST_ID']);//POST_ID 받아오는 값
			});
	    }
	}
}


//게시물 삭제
function deleteBoardPost() {
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


//게시글 - 상세
function callBoardPostDetail() {
	let callParams = {
		  BRD_ID : boardId
		, POST_ID : postId
	};
		
	callAjaxPost('/board/boardPostDetail.json', callParams, function(data) {
		let postData = data['data'];
		let postFile = data['file'];
		let postLocation = data['location'];
		
		displayBoardPostContents(postData, postFile, postLocation);
	});
}