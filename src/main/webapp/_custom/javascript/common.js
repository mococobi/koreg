/**
 * 
 */
function wait() {
	$.blockUI({ 
        theme: true, 
        // title: "알림", 
        message: '<p><b>잠시 기다려 주세요...<b></p>' 
    }); 	
}


function unwait() {
	$.unblockUI();
}


//MSTR 새탭 열기
function popupMstrPage(popupName) {
	let eventParam = '';
	
	switch (popupName) {
		case 'MY_REPORT' :
			eventParam = 'evt=3003&src=mstrWeb.3003';
			break;
		case 'SHARE_REPORT' :
			eventParam = 'evt=3002&src=mstrWeb.3002';
			break;
		case 'NEW_REPORT' :
			eventParam = 'evt=3011&src=mstrWeb.3011';
			break;
		case 'NEW_DOSSIER' :
			eventParam = 'evt=3187&src=mstrWeb.3187';
			break;
		default :
			break;
	}
	
	let mstrUlr = '/servlet/mstrWeb?&Server='+ __mstrServerName +'&Port='+ __mstrServerPort +'&Project='+ __mstrDefaultProjectName +'&';
	let newTab = window.open(__contextPath + mstrUlr + eventParam, popupName);
}


//커뮤니티 - 리스트 화면 이동
function moveCommunityPage(moveBoardId) {
	let pagePrams = [
		['BRD_ID', moveBoardId]
	];
	if(moveBoardId=="1"){
		pageGoPost('_self', __contextPath + '/app/board/boardPostListView.do', pagePrams);
	}else{
		pageGoPost('_self', __contextPath + '/app/board/boardPostFaqListView.do', pagePrams);
	}
}


//커뮤니티 - 게시물 상세 화면 이동
function detailBoardPost(moveBoardId, movePostId) {
	let pagePrams = [
		  ['BRD_ID', moveBoardId]
		, ['POST_ID', movePostId]
		
	];
	pageGoPost('_self', __contextPath + '/app/board/boardPostDetailView.do', pagePrams);
}


//관리자 화면 이동
function moveAdminPage(moveAdminPage) {
	let pagePrams = [
		['page', moveAdminPage]
	];
	pageGoPost('_self', __contextPath + '/app/admin/adminPage.do', pagePrams);
}


//POST 페이지 이동
function pageGoPost(target, url, params, level) {
	let insdoc = '';
    
	for (let i = 0; i < params.length; i++) {
		insdoc += "<input type='hidden' name='"+ XSSCheck(params[i][0]) +"' value='"+ XSSCheck(params[i][1], level) +"'>";
	}
    
	let goform = $('<form>', {
		  method: 'post'
		, action: url
		, target: target
		, html: insdoc
	}).appendTo('body');
    
	goform.submit();
}


//XSS 방지 코드
function XSSCheck(str, level) {
	//숫자형 패스
	if(typeof str == 'number' ||typeof str == 'boolean') {
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
   		, dataType : 'json'
   		, async : true
    	, contentType : "application/json;charset=UTF-8"
		, success : function(data, text, request) {
			//작업이 성공적으로 발생했을 경우
			if(data['errorCode'] == 'success') {
				callFunction(data);
			} else {
				alert(data['errorMessage']);
				$('#portal-loading').hide();
			}
		}
		, error : function(jqXHR, textStatus, errorThrown) { 
           errorProcess(jqXHR, textStatus, errorThrown);
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
    	, enctype : 'multipart/form-data'
   		, processData : false
    	, contentType : false
		, success : function(data, text, request) {
			//작업이 성공적으로 발생했을 경우
			if(data['errorCode'] == 'success') {
				callFunction(data);
			} else {
				alert(data['errorMessage']);
				$('#portal-loading').hide();
			}
		}
		, error : function(jqXHR, textStatus, errorThrown) {
            errorProcess(jqXHR, textStatus, errorThrown);
		}
	});
}


//파라메터에 포함된 정보로 ajax 호출
function _submit(action, target, inputs) {
    var $form = $('[name="__temp_form"]');
    if ($form.length != 0) {
		$form.remove(); 
	}
    
    $form = $("<form id='__temp_form' name='__temp_form' action='" + action + "' target='" + target + "' method='post'></form>"); 
    $("body").append($form);

    $.each(inputs || [], function(i, v) {
        // $form.append("<input type='hidden' name='" + i + "' value='" + v + "'/>");
        var $input = $("<input type='hidden' name='" + i + "' value=''/>");
        $input.val(v);
        $form.append($input);
    });
    
    $form.submit();
}


//공통 에러 처리
function errorProcess(jqXHR, textStatus, errorThrown) {
	if(jqXHR['status'] == 404) {
		alert('지정되지 않은 URL입니다.');
	} else {
		alert('에러 처리 필요');
	}
	
	$('#portal-loading').hide();
}


//파일 다운로드
function downloadAttachFile(fileData) {
	_submit(__contextPath + '/app/board/downloadAttachFile.do', 'downloadTarget', fileData);
}

//날짜 타입 표시
function changeDisplayDate(orgDate, changeType) {
	
	if(orgDate == null) {
		return '';
	}
	
	let changeDate = new Date(orgDate);

	let year = changeDate.getFullYear(); // 2023
	let month = (changeDate.getMonth() + 1).toString().padStart(2, '0');
	let day = changeDate.getDate().toString().padStart(2, '0');
	
	let hours = changeDate.getHours().toString().padStart(2, '0');
	let mins = changeDate.getMinutes().toString().padStart(2, '0');
    let sec = changeDate.getSeconds().toString().padStart(2, '0');
	
	switch(changeType) {
		case "YYYY-MM-DD":
			dateString = year + '-' + month + '-' + day;
			break;
		case "YYYY-MM-DD HH:mm:ss":
			dateString = year + '-' + month + '-' + day;
			dateString += ' ' + hours + ':' + mins + ':' + sec;
			break;
		default :
			break;
	}
	
	return dateString;
}


//스트링 날짜 타입 변환
function changeStringToDate(date_str) {
    let yyyyMMdd = String(date_str);
    let sYear = yyyyMMdd.substring(0,4);
    if(sYear == '') {
		sYear = '2000';
	}
    
    let sMonth = yyyyMMdd.substring(4,6);
    if(sMonth == '') {
		sMonth = '02';
	}
	
    let sDate = yyyyMMdd.substring(6,8);
    if(sDate == '') {
		sDate = '01';
	}

    return new Date(Number(sYear), Number(sMonth)-1, Number(sDate));
}


//Datatable 기본 텍스트 설정
function commonDatatableLanguage() {
	let language = {
		'decimal' : ''
		, 'emptyTable' : '데이터가 없습니다.'
		, 'info' : '_START_ - _END_ (총 _TOTAL_개)'
		, 'infoEmpty' : '0명'
		, 'infoFiltered' : '(전체 _MAX_ 개 중 검색결과)'
		, 'infoPostFix' : ''
		, 'thousands' : ','
		, 'lengthMenu' : '_MENU_ 개씩 보기'
		, 'loadingRecords' : '로딩중...'
		, 'processing' : '처리중...'
		, 'search' : '검색 : '
		, 'zeroRecords' : '검색된 데이터가 없습니다.'
		, 'paginate' : {
			  'first' : '<<'
			, 'last' : '>>'
			, 'next' : '>'
			, 'previous' : '<'
		}
		, 'aria' : {
			  'sortAscending' : ' :  오름차순 정렬'
			, 'sortDescending' : ' :  내림차순 정렬'
		}
	}
	
	return language;
}

//Air Datepicker 기본 텍스트 설정
function commonAirDatepickerLanguage() {
	let language = {
		  days: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일']
		, daysShort: ['일', '월', '화', '수', '목', '금', '토']
		, daysMin: ['일', '월', '화', '수', '목', '금', '토']
		, months: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
		, monthsShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
		, today: '오늘'
		, clear: '초기화'
		, dateFormat: 'yyyy-MM-dd'
		, timeFormat: 'hh:mm aa'
		, firstDay: 0
	}
	
	return language;
}


//jQuery MultiSelect 기본 텍스트 설정
function commonMultiSelectLanguage() {
	let language = {
          placeholder: '선택하세요'
        , search : '검색어를 입력하세요'
        , searchNoResult : '검색 결과 없음'
        , selectedOptions : '개 선택'
        , selectAll : '전체 선택'
        , unselectAll : '전체 선택 해제'
	}
	
	return language;
}


//MSTR Form 데이터 정의
function getMstrFormDefinition(type) {
	let rtnInput;
	
	let formDefs = {
		common : {
			  server: __mstrServerName
			, port: __mstrServerPort
			, project: __mstrDefaultProjectName
			, hiddenSections: 'path,header,footer,dockLeft'
			, promptAnswerMode: '2'
		}
		, report : {
			  evt: '4001'
			, src: 'mstrWeb.4001'
			, reportID: objectId
		}
		, document : {
	          evt: '2048001'
	        , src: 'mstrWeb.2048001'
	        , documentID: objectId
	        , share: '1'
	    	, hiddenSections: 'dockTop,path,header,footer'
		}
		, Gcgf_document : {
	          evt: '2048001'
	        , src: 'mstrWeb.2048001'
	        , documentID: objectId
	        , share: '1'
	    	, hiddenSections: 'header,footer'
		}
		, Koreg_document : {
	          evt: '2048001'
	        , src: 'mstrWeb.2048001'
	        , documentID: objectId
	        , share: '1'
	    	, hiddenSections: 'dockTop,path,header,footer'
		}
		, dossier: {
	          evt: '3140'
	        , src: 'mstrWeb.3140'
	        , documentID: objectId
	        , share: '1'
	    	, hiddenSections: 'dockTop,path,header,footer'
		}
		, Gcgf_dossier : {
	          evt: '3140'
	        , src: 'mstrWeb.3140'
	        , documentID: objectId
	    	, hiddenSections: 'path,header,footer'
		}
		, Gcgf_dossier_main : {
	          evt: '3140'
	        , src: 'mstrWeb.3140'
	        , documentID: objectId
	        , share: '1'
	    	, hiddenSections: 'path,header,footer'
		}
		, Koreg_dossier: {
	          evt: '3140'
	        , src: 'mstrWeb.3140'
	        , documentID: objectId
	        , share: '1'
	    	, hiddenSections: 'path,header,footer'
		}
	}
	
	rtnInput = $.extend({}, formDefs['common']);
	
	switch (type) {
		case 3:
			$.extend(rtnInput, formDefs['report']);
			break;
		case 55:
	        if (isvi == true) {
	        	$.extend(rtnInput, formDefs[__portalAppName + '_dossier']);
	        } else {
	        	$.extend(rtnInput, formDefs[__portalAppName + '_document']);
	        }
			break;
		case 551:
	        if (isvi == true) {
	        	$.extend(rtnInput, formDefs[__portalAppName + '_dossier_main']);
	        } else {
	        	$.extend(rtnInput, formDefs[__portalAppName + '_document']);
	        }
			break;
		default:
			$.extend(rtnInput, formDefs['dossier']);
			break;
	}
	
	return rtnInput;
}


//사이즈 포맷 변경
function formatFileSize(filesize) {
	let text = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
    let e = Math.floor(Math.log(filesize) / Math.log(1024));
    return (filesize / Math.pow(1024, e)).toFixed(2) + " " + text[e];
}


//확장자 추출
function getExtensionOfFilename(filename) {
	let _fileLen = filename.length;     /**      * lastIndexOf('.')      * 뒤에서부터 '.'의 위치를 찾기위한 함수     * 검색 문자의 위치를 반환한다.     * 파일 이름에 '.'이 포함되는 경우가 있기 때문에 lastIndexOf() 사용     */    
	let _lastDot = filename.lastIndexOf('.');     // 확장자 명만 추출한 후 소문자로 변경    
	let _fileExt = filename.substring(_lastDot, _fileLen).toLowerCase();     
	return _fileExt;
}


//팝업 쿠키
let handleCookie = {
	setCookie: function (name, val, exp) {
		let date = new Date();
					      
		// 만료 시간 구하기(exp를 ms단위로 변경)
		date.setDate(date.getDate() + exp);
		date.setHours(23,59,59);
		
		// 실제로 쿠키 작성하기
		document.cookie = name + '=' + val + ';expires=' + date.toUTCString() + ';path='+__contextPath+'/app/main/';
	},
	// 쿠키 읽어오기(정규식 이용해서 가져오기)
	getCookie: function (name) {
		let value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
		return value ? value[2] : null;
	}
};


//MSTR 타이틀 이름 앞에 .자르고 표시
function getMstrTitleName(title) {
	let titleSplit = title.split('.');
	let rtnTitle = '';
	
	if(titleSplit.length < 2) {
		rtnTitle = title;
	} else {
		titleSplit.shift();
		rtnTitle = titleSplit.join('');
	}
	
	return rtnTitle;
}
