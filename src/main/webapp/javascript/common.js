/**
 * 
 */
function wait() {
	$.blockUI({ 
        theme: true, 
        // title: "알림", 
        message: "<p><b>잠시 기다려 주세요...<b></p>" 
    }); 	
}


function unwait() {
	$.unblockUI();
}


//커뮤니티 - 리스트 화면 이동
function moveCommunityPage(moveBoardId) {
	let pagePrams = [
		["boardId", moveBoardId]
	];
	pageGoPost('_self', __contextPath + '/app/board/boardPostListView.do', pagePrams);
}


//커뮤니티 - 게시물 상세 화면 이동
function detailBoardPost(moveBoardId, movePostId) {
	let pagePrams = [
		  ["boardId", moveBoardId]
		, ["postId", movePostId]
		
	];
	pageGoPost('_self', __contextPath + '/app/board/boardPostDetailView.do', pagePrams);
}


//POST 페이지 이동
function pageGoPost(target, url, params) {
	var insdoc = "";
    
	for (var i = 0; i < params.length; i++) {
		insdoc+= "<input type='hidden' name='"+ XSSCheck(params[i][0]) +"' value='"+ XSSCheck(params[i][1], 0) +"'>";
	}
    
	var goform = $("<form>", {
		  method: "post"
		, action: url
		, target: target
		, html: insdoc
	}).appendTo("body");
    
	goform.submit();
}


//XSS 방지 코드
function XSSCheck(str, level) {
	//숫자형 패스
	if(typeof str == 'number') {
		return str;
	}
	
    if (level == undefined || level == 0) {
        str = str.replace(/\<|\>|\"|\'|\%|\;|\(|\)|\&|\+|\-/g,"");
    } else if (level != undefined && level == 1) {
        str = str.replace(/\</g, "&lt;");
        str = str.replace(/\>/g, "&gt;");
    }
    return str;
}


//ajax POST 요청
function callAjaxPost(url, params, callFunction) {
	$.ajax({
		  type : 'post'
		, url : __contextPath + '/app' + url
		, data : JSON.stringify(params)
   		, dataType : "json"
   		, async : true
    	, contentType : "application/json;charset=UTF-8"
		, success : function(data, text, request) {
			//작업이 성공적으로 발생했을 경우
			if(data['errorCode'] == 'success') {
				callFunction(data);
			} else {
				alert(data['errorMessage']);
			}
		}
		, error : function(jqXHR, textStatus, errorThrown) { 
            if(jqXHR['status'] == 404) {
				alert('지정되지 않은 URL입니다.');
			} else {
				alert('에러 처리 필요');
			}
		}
	});
}

//ajax form 데이터 요청
function callAjaxForm(url, params, callFunction) {
	$.ajax({
		  type : 'post'
		, url : __contextPath + '/app' + url
		, data : params
    	, dataType : 'json'
    	, enctype : "multipart/form-data"
   		, processData : false
    	, contentType : false
		, success : function(data, text, request) {
			//작업이 성공적으로 발생했을 경우
			if(data['errorCode'] == 'success') {
				callFunction(data);
			} else {
				alert(data['errorMessage']);
			}
		}
		, error : function(jqXHR, textStatus, errorThrown) { 
            if(jqXHR['status'] == 404) {
				alert('지정되지 않은 URL입니다.');
			} else {
				alert('에러 처리 필요');
			}
		}
	});
}


function changeDisplayDate(orgDate, changeType) {
  let changeDate = new Date(orgDate);

  const year = changeDate.getFullYear(); // 2023
  const month = (changeDate.getMonth() + 1).toString().padStart(2, '0'); // 06
  const day = changeDate.getDate().toString().padStart(2, '0'); // 18

  const dateString = year + '-' + month + '-' + day; // 2023-06-18

  return dateString;
}