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
                            	<h3 id="changeTimerStop" class="stop" style="margin-left: 10px; margin-top: 5px;">정지</h3>
                            </div>
                            <div class="tab-cont-wrap">
                                <div class="cont-tab cont-tab1 on">
                                	<!-- 메인 영역 -->
	
									<div class="run-box flex" style="display: flow-root;">
										<!-- 프롬프트 영역 -->
									    <ul class="run-setting-box flex" style="float: left; padding-left: 20px;"></ul>
								    	<button id="run" class="btn-run blue" style="float: right; margin-right: 20px;">실행</button>
									</div>
									
									<div class="top cont-wrap flex" style="height: calc(100vh - 165px);">
									    <iframe name="mstrReport" id="mstrReport" src=""
												style="width: 100%; border: 1px solid silver; margin: 0px; background: #fff; border-radius: 8px; border: 1px solid #c8d8ec;"
												marginWidth=0 marginHeight=0 frameBorder=0 scrolling="auto">
										</iframe>
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
		<iframe id="downloadTarget" name="downloadTarget" style="display:none;"></iframe>
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
	let timerStopIdx = -1;
	let timerStopIdx2 = -1;
	let timerEndCheck = false;
	let timerStopCheck = false;
	let timerEndIdx = -1;
	
	let reportSlideTimeOut;
	let reportSlideCheck = true;
	
	$(function() {
		$('#run').on('click', function() {
			getAnswerXML();
		});
		
		
		$('#mstrReport').on("load", function() {
			/*
			countDown('currentTime', function() {
				leftMenuIdx++;
				
				if(leftMenuIdx >= leftMenuList.length) {
					leftMenuIdx = 0;
				}
				
				$('#' + leftMenuList[leftMenuIdx]['id']).click();
			}, 0, 10);
			*/
			
			//도씨에 슬라이드
			clearTimeout(reportSlideTimeOut);
			if(reportSlideCheck) {
				reportSlideTimeOut = setTimeout(function () {
					leftMenuIdx++;
					
					if(leftMenuIdx >= leftMenuList.length) {
						leftMenuIdx = 0;
					}
					
					$('#' + leftMenuList[leftMenuIdx]['id']).click();
				}, 5000);
			}
			
			$('#portal-loading').hide();
		});
		
		
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
		
		
        $('#changeTimerStop').on('click', function() {
        	/*
            if($('#changeTimerStop').attr('class') == 'stop') {
                $('#changeTimerStop').removeClass('stop');
                $('#changeTimerStop').addClass('play');
            
                timerStopCheck = true;
                timerStopIdx = parseInt($('#currentTime').text().split(':')[0]) * 60 + parseInt($('#currentTime').text().split(':')[1]);
                timerStopIdx2 = timerStopIdx;
                timerStopText = $('#currentTime').text();
            } else if($('#changeTimerStop').attr('class') == 'play') {
                $('#changeTimerStop').removeClass('play');
                $('#changeTimerStop').addClass('stop');
                
                timerStopCheck = false;
                
                countDown('currentTime', function() {
    				leftMenuIdx++;
    				
    				if(leftMenuIdx >= leftMenuList.length) {
    					leftMenuIdx = 0;
    				}
    				
    				$('#' + leftMenuList[leftMenuIdx]['id']).click();
    			}, 0, timerStopIdx2, true);
            }
        	*/
        	
        	
        	if($('#changeTimerStop').attr('class') == 'stop') {
                $('#changeTimerStop').removeClass('stop');
                $('#changeTimerStop').addClass('play');
                $('#changeTimerStop').text('시작');
                reportSlideCheck = false;
                
              	clearTimeout(reportSlideTimeOut);
            } else if($('#changeTimerStop').attr('class') == 'play') {
                $('#changeTimerStop').removeClass('play');
                $('#changeTimerStop').addClass('stop');
                $('#changeTimerStop').text('정지');
                reportSlideCheck = true;
                
                reportSlideTimeOut = setTimeout(function () {
					leftMenuIdx++;
					
					if(leftMenuIdx >= leftMenuList.length) {
						leftMenuIdx = 0;
					}
					
					$('#' + leftMenuList[leftMenuIdx]['id']).click();
				}, 5000);
            }
        });
		
        
        initLeftMenu();
// 		fnReportInit();
	});
	
	
	function timeOutFunction(moveIdx, stopCheck) {
		
	}
	
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
	        
	        $('#mstrReport').on('mouseenter', function(e) {
	        	$('.dep3-wrap').css('position', 'absolute');
	        	$('.dep3-wrap').removeClass('on');
	        });
	        
	        $('#' + leftMenuList[leftMenuIdx]['id']).click();
		});
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
		getPromptInfo();
	}
	
	
	//리포트정보 및 프롬프트정보의 조회
	function getPromptInfo() {
		$('#portal-loading').show();
		$('.run-setting-box').html('');
		
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
	    			$('#run').trigger('click');
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
        		$('.run-setting-box').append($wrapper);
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
			_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport', inputs);
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
		    		_submit('${pageContext.request.contextPath}/servlet/mstrWeb', 'mstrReport', inputs);
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
	
	
    //타이머
    function countDown(showTextId, timeOutFunctionCustom, minutes, seconds, stopCheck) {
    	var element, endTime, hours, mins, msLeft, time;
    	
    	function twoDigits(n) {
    		return (n <= 9 ? "0" + n : n);
    	}
    	
    	function updateTimer() {
    		msLeft = endTime - (+new Date);
    		
    		if(timerEndCheck && showTextId == 'currentTime') {
    			msLeft = 0;
    			timerEndCheck = false;
    		}
    		
    		if(timerStopCheck && showTextId == 'currentTime') {
    			msLeft = 0;
                timerEndCheck = false;
    		}
    		
    		if(msLeft < 1000) {
    			//시간 종료
    			if(!timerStopCheck && showTextId == 'currentTime') {
    			    $('#' + showTextId).text('페이지 이동중...');
    			}
    			if(timerEndIdx > -1) {
    				timeOutFunctionCustom(timerEndIdx, stopCheck);
    				timerEndIdx = -1;
    			} else if(timerStopIdx > -1) {
    				$('#' + showTextId).text(timerStopText);
    				timerStopIdx = -1;
    			} else {
    				timeOutFunctionCustom();
    			}
    			
    		} else {
    			time = new Date(msLeft);
    			hours = time.getUTCHours();
    			mins = time.getUTCMinutes();
    			
    			//화면 표시
    			var tempElemnet = (hours ? hours + ':' + twoDigits(mins) : mins) + ':' + twoDigits(time.getUTCSeconds());
    			$('#' + showTextId).text(tempElemnet);
    			console.log('tempElemnet : ' + tempElemnet);
    			
            	setTimeout(updateTimer, time.getUTCSeconds() + 500);
    		}
    	}
    	
    	endTime = (+new Date) + 1000 * (60 * minutes + seconds) + 500;
    	updateTimer();
    }
</script>
</body>
</html>