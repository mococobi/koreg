<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="com.microstrategy.web.objects.WebIServerSession"%>
<%@ page import="com.microstrategy.web.objects.WebObjectsFactory"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<%@ page import="com.mococo.microstrategy.sdk.util.MstrUtil"%>
<%
	String objectId = "";
    String type = "";
    boolean isvi = StringUtils.equalsIgnoreCase("true", request.getParameter("isvi"));
    
    if(request.getParameter("objectId") != null) {
    	objectId = StringUtils.defaultString(request.getParameter("objectId"));
    	type = StringUtils.defaultString(request.getParameter("type"));
    } else if(request.getAttribute("portalMainDashboardId") != null) {
    	objectId = StringUtils.defaultString((String)request.getAttribute("portalMainDashboardId"));
    	type = ((Integer)request.getAttribute("type")).toString();
    	isvi = (Boolean)request.getAttribute("isvi");
    }
    
    String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
    pageContext.setAttribute("portalAppName", portalAppName);
    
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");

	String mstrServerName = CustomProperties.getProperty("mstr.server.name");
	String mstrServerPort = CustomProperties.getProperty("mstr.server.port");
	String mstrDefaultProjectName = CustomProperties.getProperty("mstr.default.project.name");
	
	String portalIframePageMoveYn = CustomProperties.getProperty("portal.iframe.page.move.yn");
	pageContext.setAttribute("portalIframePageMoveYn", portalIframePageMoveYn);
	
	String mstrMenuFolderId = CustomProperties.getProperty("eis.menu.folder.id");
	pageContext.setAttribute("mstrMenuFolderId", mstrMenuFolderId);
%>
<!DOCTYPE html>
<html>
<head>
	<title>EIS</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/_custom/javascript/jquery-multiselect/jquery.multiselect.css?v=20240115001" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/jquery-multiselect/jquery.multiselect.js?v=20240115001"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/prompt-renderer.js?v=20240115001"></script>
	
	<style type="text/css">
		#mstrReport {
			height: 100%;
		}
		
		.run-setting-box .ms-options-wrap li {
			margin-right: 0px !important;
		    padding-right: 0px !important;
		    content: none !important;
		}
		
		#portal-loading {
			width: 100%;
			height: 100%;
			/*
			top: 0px;
			left: 0px;
			*/
			top: 59px;
   			left: 250px;
			position: fixed;
			display: block;
			/* opacity: 0.7; */
			background-color: #fff;
			z-index: 9000;
			text-align: center;
		}
		
		#loading-image {
			position: absolute;
			top: 31%;
   			left: 39%;
			z-index: 100;
		    width: 100px;
		    height: auto;
		}
		
		.mstrReport {
			width: 100%;
			border: 1px solid silver;
			margin: 0px;
			background: #fff;
			border-radius: 8px;
			border: 1px solid #c8d8ec;
		}
	</style>
</head>
<body>
	<div class="wrap" style="background-color: rgb(231, 238, 249);"> <!-- 페이지 전체를 감싸는 wrap -->
		<!-- pop -->
		<div class="pop"> <!-- 팝업 영역 (배경) -->
			<div class="pop-inner"><!-- 팝업 내용이 들어가는 부분 -->
			</div>
	    </div>
	    <!-- //pop -->
	    
		<!-- contents -->
        <div class="contents"> <!-- 본문 영역 -->
            <div class="bg sky"> <!-- 본문 배경색 -->
                <div class="inner flex">
                	<!-- Left Menu -->
                	<nav class="lnb" style="position: relative; overflow: auto; height: calc(100vh - 45px);"></nav>
                	
                    <div class="content flex2">
                        <div class="cont-area">
                        	<div style="margin-bottom: 10px; display: flex;">
                            	<h1 id="mstrReportTitle" style="margin-left: 10px;">EIS 리포트</h1>
                            	<h3 id="currentTime" style="margin-left: 10px; margin-top: 5px;"></h3>
								<h3 id="timer" style="margin-left: 10px; margin-top: 5px;">00:00</h3>
								<h2 id="timer1" style="margin-left: 10px; margin-top: 5px;" onclick="startTimer()"><i class="bi bi-play-circle"></i></h2>
								<h2 id="timer2" style="margin-left: 10px; margin-top: 5px; display: none;" onclick="pauseTimer()"><i class="bi bi-pause-circle"></i></h2>
								<h2 id="timer3" style="margin-left: 10px; margin-top: 5px; display: none;" onclick="resumeTimer()"><i class="bi bi-play-circle"></i></h2>
								<h2 id="timer4" style="margin-left: 10px; margin-top: 5px; display: none;" onclick="resetTimer()">초기화</h2>
                            </div>
                            <div class="tab-cont-wrap">
                                <div class="cont-tab cont-tab1 on">
                                	<!-- 메인 영역 -->
                                	<div id="run-box-div">
                                		<%--
										<div class="run-box flex" style="display: flow-root;">
											<!-- 프롬프트 영역 -->
										    <ul class="run-setting-box flex" style="float: left; padding-left: 20px;"></ul>
									    	<button id="run" class="btn-run blue" style="float: right; margin-right: 20px;">실행</button>
										</div>
										--%>
                                	</div>
									<div id="mstrReport-div" class="top cont-wrap flex" style="height: calc(100vh - 165px);">
										<%--
									    <iframe id="mstrReport" name="mstrReport" class="mstrReport" src="" marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto">
										</iframe>
										--%>
									</div>
								</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- contents -->
		<div id="portal-loading" style="display: none;">
			<img id="loading-image" src="${pageContext.request.contextPath}/_custom/image/main/loading.gif" alt="Loading..." />
		</div>
	</div>

<script type="text/javascript">
	let objectId = "";
	let type = 0;
	let isvi = true;
	let reportInfo = undefined;
	
	let __mstrServerName = "<%=mstrServerName%>";
	let __mstrServerPort = "<%=mstrServerPort%>";
	let __mstrDefaultProjectName = "<%=mstrDefaultProjectName%>";
	
	let leftMenuList = [];	//슬라이드 목록
	let leftMenuIdx = 0;	//실행 리포트 인덱스
	
	let timer;
	let startTime;
	let elapsedTime = 0;
	let isTimerRunning = false;
	const timerDuration = 30; // 타이머 지속 시간(초)
	
	$(function() {
		/* box1 탭 */
        $('.box-btn-wrap button').each(function(index, item) {
            $(item).on('click', function(){
                $('.box-btn-wrap button').removeClass('active');
                $(this).addClass('active');

                var idx = index
                var showCont = $('.box-tab')[idx];
                $('.box-tab').removeClass('on');
                $(showCont).addClass('on');
            });
        });
		
        initLeftMenu();
        
        setInterval(function() {
			console.log('MSTR 세션 유지용 Dossier 호출');
        	connMstr();
       	}, 60 * 1000 * 5);
	});
	
	
	//초기함수
	function initLeftMenu() {
		$(window).resize(function() {
			let height	= $(window).height();
// 			$('.lnb').height(height - $('.header').height() - $('.tab-btn-wrap.flex').height() - 100);
		});
		
		$(window).resize();
		
		let callParams = {
			folderId : '${mstrMenuFolderId}'
		};
		callAjaxPost('/mstr/getEisFolderList.json', callParams, function(data) {
			leftMenuList = data['folder'];
			let drawHtml = drawMenuParentReport(data['folder'], $('<ul>', {class : 'dep1-ul'}));
			$('.lnb').append(drawHtml);
			
			//초기 실행
			if(objectId == '') {
				$('.dep3').find('a').eq(0).click();
			}
			
	        /* 메뉴 */
	        $('.dep2').on('mouseenter', function(e) {
	        	$('.dep3-wrap').css('position', 'absolute');
	        	$('.dep3-wrap').removeClass('on');
	        	
	            $(this).find('.dep3-wrap').addClass('on');
	            $(this).find('.dep3-wrap').css('position', 'fixed');
	            $(this).find('.dep3-wrap').css('top', $(this).offset().top);
	            $(this).find('.dep3-wrap').css('left', $(this).offset().left + 176);
	            
	            $(this).find('.dep3-wrap').width($(this).find('.dep3-ul').width());
	            $(this).find('.dep3-wrap').height($(this).find('.dep3-ul').height());
	        });
	        
	        /*
	        $('.dep2').on('mouseleave', function(e){
	            $(this).find('.dep3-wrap').removeClass('on');
	        });
	        */
	        
	        $('.dep3-wrap').on('mouseleave', function(e) {
	            $(this).css('position', 'absolute');
	            $(this).removeClass('on');
	        });
	        
			drawTabFrame();
		});
	}
	
	
	//프레임 그리기
	function drawTabFrame(){
		leftMenuList.forEach((menu, idx) => {
			let ulHtml = $('<ul>', {
				  class : 'flex run-setting-box run-setting-box_' + idx
				, style : 'float: left; padding-left: 20px;'
			});
			
			let btnHtml = $('<button>', {
				  id : 'run_' + idx
				, class : 'btn-run blue'
				, style : 'float: right; margin-right: 20px;'
				, text : '실행'
			});
			
			let divHtml = $('<div>', {
				  id : 'run-box-div_' + idx
				, class : 'run-box flex'
				, style : 'display: flow-root; display: none;'
			}).append(ulHtml).append(btnHtml);
			
			$('#run-box-div').append(divHtml);
			
			let iframeHtml = $('<iframe>', {
				  id : 'mstrReport_' + idx
				, name : 'mstrReport_' + idx
				, class : 'mstrReport'
				, marginWidth : 0 
				, marginHeight : 0 
				, frameBorder : 0 
				, scrolling : 'auto'
				, style : 'display: none;'
			});
			
			$('#mstrReport-div').append(iframeHtml);
			
			$('#mstrReport_' + idx).on("load", function() {
				$('#portal-loading').hide();
				
				if($(this).contents().get(0).location.href.indexOf('servlet/mstrWeb')> -1) {
					resetTimer();
					if($('#timer3').css('display') != 'block') {
						startTimer();
					}
				}
			});
	        
			
			$('#mstrReport_' + idx).on('mouseenter', function(e) {
	        	$('.dep3-wrap').css('position', 'absolute');
	        	$('.dep3-wrap').removeClass('on');
	        });
			
			
			$('#run_' + idx).on('click', function() {
				getAnswerXML();
			});
			
		});
		
		$('#run-box-div_0').css('display', 'flow-root');
		$('#mstrReport_0').show();
		$('#' + leftMenuList[leftMenuIdx]['id']).click();
	}
	
	
	//메뉴 리포트 동적 생성
	function drawMenuParentReport(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
			
			let depLiHtml = $('<li>', {class : 'dep1'});
			let aHtml = $('<a>', {
				  href : 'javascript:void(0)'
				, id : menu['id']
				, title : getMstrTitleName(menu['name'])
				, text : getMstrTitleName(menu['name'])
				, click : function(e) {
					leftMenuIdx = idx;
					clickReportObj(menu);
				}
			});
			depLiHtml.append(aHtml);
			
			if(menu['child']) {
				let childHtml = drawMenuChildDep2Report(menu['child'], $('<ul>', {class : 'dep2-ul'}));
				depLiHtml.append(childHtml);
			}
			
			$(rtnHtml).append(depLiHtml);
		});
		
		return rtnHtml;
	}
	
	
	//2레벨 태그 생성
	function drawMenuChildDep2Report(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
			let depLiHtml = $('<li>', {class : 'dep2'});
			let aHtml = $('<a>', {
				  href : '#'
				, title : getMstrTitleName(menu['name'])
				, text : getMstrTitleName(menu['name'])
				, click : function(e) {
					clickReportObj(menu);
				}
			});
			depLiHtml.append(aHtml);
			
			if(menu['child']) {
				let divHtml = $('<div>', {
					  class : 'dep3-wrap'
					, style : 'z-index: 9001;'
				});
				let childHtml = drawMenuChildDep3Report(menu['child'], $('<ul>', {class : 'dep3-ul'}));
				divHtml.append(childHtml);
				depLiHtml.append(divHtml);
			}
			
			$(rtnHtml).append(depLiHtml);
		});
		
		return rtnHtml;
	}
	
	
	//3레벨 태그 생성
	function drawMenuChildDep3Report(menuReport, rtnHtml) {
		menuReport.forEach((menu, idx) => {
			let depLiHtml = $('<li>', {class : 'dep3'});
			let aHtml = $('<a>', {
				  href : '#'
				, title : getMstrTitleName(menu['name'])
				, text : getMstrTitleName(menu['name'])
				, click : function(e) {
					clickReportObj(menu);
				}
			});
			depLiHtml.append(aHtml);
			$(rtnHtml).append(depLiHtml);
		});
		
		return rtnHtml;
	}
	
	
	//리포트 클릭
	function clickReportObj(menu) {
		if(menu['type'] == 8) {
			//폴더
		} else {
			let pagePrams = [
				  ["objectId", menu['id']]
				, ["type", menu['type']]
				, ["subType", menu['subType']]
			  	, ["isvi", menu['isVI']]
				, ["title", encodeURI(menu['name'])]
			];
			
			objectId = menu['id'];
			type = menu['type'];
			isvi = menu['isVI'];
			
			$('#mstrReportTitle').text(getMstrTitleName(menu['name']));
			fnReportInit();
		}
	}
	
	
    //초기 실행 함수
	function fnReportInit() {
		showAndHideTab();
    	
    	if($('#mstrReport_' + leftMenuIdx).contents().get(0).location.href.indexOf('servlet/mstrWeb')> -1) {
    		resetTimer();
    		if($('#timer3').css('display') != 'block') {
				startTimer();
			}
		} else {
			getPromptInfo();
		}
    	
	}
	
	
	//리포트정보 및 프롬프트정보의 조회
	function getPromptInfo() {
		$('#portal-loading').show();
// 		$('.run-setting-box').html('');
		
		let callParams = {
			  objectId : objectId
			, type : type
		};
		callAjaxPost('/mstr/getEisReportInfo.json', callParams, function(data) {
			if (data) {
    			reportInfo = data.report;
    			document.title = reportInfo['title'];
	    		renderPrompt();
	    		setTimeout(() => {
	    			$('#run_' + leftMenuIdx).trigger('click');
	    		}, 100);
    		} else {
    		    alert('리포트정보를 가져올 수 없습니다.');
    		    $('#portal-loading').hide();
    		}
		});
	}
	
	
	//프롬프트정보를 이용한 랜더링
	function renderPrompt() {
		
		if (reportInfo == undefined || reportInfo.promptList == undefined) {
			//자동실행
// 			$('#run').trigger('click');
        	return; 
        }
        	
        $.each(reportInfo.promptList, function(i, v) {
        	var uiType = undefined;
        	
        	if (v['exUiType']) {
        		uiType = v['exUiType'];
        	} else {
	            switch (v['type']) { // 프롬프트유형을 이용한 UI유형결정
		            case 1:
		            	uiType = 'value-default';
		            	break;
		            case 2:	  
		            case 4:
		            	uiType = 'list-default';
		            	if ((v['max'] && Number(v['max']) > 1) || !v['max']) {
		            	    uiType = 'multiSelect-default';
		            	}
		                break;
		            default:
	            } 
        	}
        	
        	if (promptRenderer[uiType]) {
        		var $wrapper = $('<li>', {class : 'flex'});
//         		$('.run-setting-box').append($wrapper);
				$('.run-setting-box_' + leftMenuIdx).append($wrapper);
        		promptRenderer[uiType]['label']($wrapper, v);
        		promptRenderer[uiType]['body']($wrapper, v);
        	}
        });
        
        if(reportInfo.promptList.length == 0) {
			//자동실행
// 			$('#run').trigger('click');
        } else {
//         	$('#mstrReport').attr('src', '${pageContext.request.contextPath}/app/main/selectPrompt.do');
        }
	}
	
	
	//정합성 체크
	function validationPrompt() {
		var elemVal = {};
		$('[prompt-id]').each(function(i, v) {
			var $elem = $(v);
			elemVal[$elem.attr('prompt-id')] = promptRenderer[$elem.attr('ui-type')]['validation']($elem);
		});
		
		return elemVal;
	}
	
	
	//프롬프트 값 세팅
	function getPromptVal() {
		var elemVal = {};
		$('[prompt-id]').each(function(i, v) {
			var $elem = $(v);
			elemVal[$elem.attr('prompt-id')] = promptRenderer[$elem.attr('ui-type')]['selected']($elem);
		});
		
		return elemVal;
	}
	
	
	//리포트 실행
	function getAnswerXML() {
		$('#portal-loading').show();
		
		//현재 선택된 프롬프트값들을 파라미터로 전달하여 XML형태로 반환 받음.
		let promptVal = getPromptVal();
		
		if(typeof reportInfo['promptList'] != 'undefined' && reportInfo['promptList'].length == 0) {
			//프롬프트 없을 경우
			let inputs = getMstrFormDefinition(objectId, type, isvi);
			_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport_' + leftMenuIdx, inputs);
		} else {
			//프롬프트 있을 경우
			
			//정합성 체크
			let promptCheck = validationPrompt();
			for (const [key, value] of Object.entries(promptCheck)) {
				if(value) {
					alert(value);
					$('#portal-loading').hide();
					return false;
				}
			}
			
			$.ajax({
		    	  type: 'post'
		    	, url: '${pageContext.request.contextPath}/app/mstr/getEisAnswerXML.json'
		    	, async: true
		    	, contentType: 'application/json;charset=utf-8'
		    	, data : JSON.stringify({
	    			  objectId: objectId
	    			, type: type
	    			, promptVal: promptVal
	    		})
	    		, dataType: 'json'
	    		, success: function(data, text, request) {
		    		// 리포트 실행 시 파라미터로 전달될 promptsAnswerXML의 값을 서버로부터 조회 성공
		    		let inputs = getMstrFormDefinition(objectId, type, isvi);
		    		$.extend(inputs, {promptsAnswerXML : data['xml']});
		    		_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport_' + leftMenuIdx, inputs);
		    	}
		    	, error : function(jqXHR, textStatus, errorThrown) {
		    		$('#portal-loading').hide();
		    		errorProcess(jqXHR, textStatus, errorThrown);
		    	}
		    });
		}
	}
	
	
	//팝업 콜백
    function popupCallback(promptId) {
        console.log('=> promptId', promptId);
    }
	
	
    function startTimer() {
		$('#timer1').hide();
		$('#timer2').show();
    	
        if (!isTimerRunning) {
            isTimerRunning = true;
            startTime = Date.now() - elapsedTime;
            timer = setInterval(updateTime, 1000);
        }
    }
    
    
    function updateTime() {
        const elapsedTimeInSeconds = Math.floor((Date.now() - startTime) / 1000);
        const remainingTime = timerDuration - elapsedTimeInSeconds;
        if (remainingTime <= 0) {
            clearInterval(timer);
            isTimerRunning = false;
            document.getElementById('timer').innerText = '00:00';
            timerComplete();
        } else {
            const hours = Math.floor(remainingTime / 3600);
            const minutes = Math.floor((remainingTime % 3600) / 60);
            const seconds = remainingTime % 60;
            //document.getElementById('timer').innerText = formatTime(hours) + ':' + formatTime(minutes) + ':' + formatTime(seconds);
            document.getElementById('timer').innerText = formatTime(minutes) + ':' + formatTime(seconds);
        }
    }
    
    
    function formatTime(time) {
        return time < 10 ? '0' + time : time;
    }
    
    
    function pauseTimer() {
    	$('#timer2').hide();
    	$('#timer3').show();
    	
        if (isTimerRunning) {
            clearInterval(timer);
            isTimerRunning = false;
            elapsedTime = Date.now() - startTime;
        }
    }
    
    
    function resumeTimer() {
    	$('#timer2').show();
    	$('#timer3').hide();
    	
        if (!isTimerRunning) {
            isTimerRunning = true;
            startTime = Date.now() - elapsedTime;
            timer = setInterval(updateTime, 1000);
        }
    }
    
    
    function resetTimer() {
        clearInterval(timer);
        isTimerRunning = false;
        elapsedTime = 0;
        document.getElementById('timer').innerText = '00:00';
    }
    
    
    function timerComplete() {
        // 타이머가 완료되었을 때 실행할 동작을 여기에 추가하세요.
       	// alert('타이머가 완료되었습니다!');
        
        $('#timer1').show();
        $('#timer2').hide();
    	$('#timer3').hide();
    	
    	showAndHideTab();
        
        leftMenuIdx++;
        if(leftMenuIdx >= leftMenuList.length) {
			leftMenuIdx = 0;
		}
        $('#' + leftMenuList[leftMenuIdx]['id']).click();
        
    }
    
    
    //MSTR 세션 호출
    function connMstr() {
    	$.ajax({
	  		  type : 'post'
	  		, url : __contextPath + '/servlet/mstrWeb?evt=3187&src=mstrWeb.3187'
	   		, async : true
	  		, success : function(data, text, request) {
	  			//작업이 성공적으로 발생했을 경우
	  		}
	  		, error : function(jqXHR, textStatus, errorThrown) {
	             errorProcess(jqXHR, textStatus, errorThrown);
	  		}
	  	});
  	}
    
    
    //영역 숨김 및 표시
    function showAndHideTab() {
		//프롬프트 영역
    	$('div[class="run-box flex"]').each(function() {
			$(this).hide();
        });
    	$('#run-box-div_' + leftMenuIdx).css('display', 'flow-root');
    	
    	//iframe 영역
        $('iframe[class="mstrReport"]').each(function() {
			$(this).hide();
        });
        $('#mstrReport_' + leftMenuIdx).show();
	}
	
	
</script>
</body>
</html>