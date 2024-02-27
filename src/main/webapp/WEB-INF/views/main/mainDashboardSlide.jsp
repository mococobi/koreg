<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ page import="java.util.*" %>
<%
	String userName = "";
	userName = StringUtils.defaultString((String)session.getAttribute("mstrUserNameAttr"), "-");
	pageContext.setAttribute("userName", userName);
	
	/* 2020-12-01 소스코드취약점점검대응 - Cross-Site Scripting */
	String server = CustomProperties.getProperty("mstr.server.name");
	String project = CustomProperties.getProperty("mstr.default.project.name");
	String port = CustomProperties.getProperty("mstr.server.port");
	
	pageContext.setAttribute("server", server);
	pageContext.setAttribute("project", project);
	pageContext.setAttribute("port", port);
%>
<!DOCTYPE html>
<html>
<head>
   	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EIS</title>
    <jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
    <!--  
    <script src="${pageContext.request.contextPath}/_custom/js/full-screen-helper.js?v=20201116001" type="text/javascript"></script>
    -->
    
    <style type="text/css">
        .divWaitCurtain {
            position: absolute;
		    top: 0;
		    left: 0;
		    background: #fcfcfc;
		    width: 100%;
		    height: 100%;
		    opacity: 1.0;
        }
        
        .lodingImg {
            background: #eee url(../style/mstr/images/loader_round.gif) no-repeat scroll 50% 60%;
        }
        
        :not(:root):-webkit-full-screen::backdrop {
	        position: fixed;
	        top: 0px;
	        right: 0px;
	        bottom: 0px;
	        left: 0px;
	        background: #fff !important;
	    }
    </style>
    <script type="text/javascript">
		
	    var server = '${server}';
        var project = '${project}';
        var port = '${port}';
	    
	    var frameSubjectHeight = 5 + 40;
	    var slideDashboard = [];
	    
	    var slideIdx = 0;
	    
	    var replaceUrl = '${pageContext.request.contextPath}/servlet/mstrWeb?evt=3140&src=mstrWeb.3140&share=1&Server='+server+'&Project='+project+'&Port='+port+'&hiddenSections=header,dockTop&documentID=';
	    //var replaceUrl = '${pageContext.request.contextPath}/servlet/mstrWeb?evt=3140&src=mstrWeb.3140&share=1&Server='+server+'&Project='+project+'&Port='+port+'&documentID=';
	    var customInterval1 = null;
	    var customInterval2 = null;
	    var customInterval3 = null;
	    
	    var loadCheck = 0;
	    var zidx = 100;
	    
	    var timeOutSec = 60;
	    
	    
	    //초기 함수
        function fnInit(data) {
            let listData = data['folder'];
            let $tmpHtml = '';
            /*
            for(let i=0; i<listData.length; i++) {
                slideDashboard.push({
                	  'dossierId' : listData[i]['id']
                	, 'subject' : listData[i]['name']
                });
                $tmpHtml += '<a href="#" title="'+ listData[i]['name'] +'" style="margin-left: 5px;" id="move_'+i+'" name="move_Btn" onclick="moveUrlBtn(\'' + i +'\');">'+ (i+1) +'</a>';
            }
            */
            
//             let defaultListData = null;
//             let userListDataAll = null;
//             let userListData = null;
//             let listViewData = null;
            let userName = '${userName}'.replace(')', '').split('(');
            userName = userName[1] + '(' + userName[0] + ')';
            
            /*
            for(var i=0; i<listData.length; i++) {
            	if(listData[i]['name'] == '01.Default') {
            		defaultListData = listData[i];
            	} else if(listData[i]['name'] == '02.User') {
            		userListDataAll = listData[i]['child'];
            	}
            }
            
            for(var j=0; j<userListDataAll.length; j++) {
            	if(userListDataAll[j]['name'] == userName) {
            		userListData = userListDataAll[j];
            		break;
            	}
            }
            
            if(userListData == null) {
            	listViewData = defaultListData;
            } else {
            	listViewData = userListData;
            }
            
            if(listViewData['description'] != '') {
            	if(listViewData['description'].indexOf("{") > -1 && listViewData['description'].indexOf("}") > -1) {
            		var jsonData = JSON.parse(listViewData['description']);
            		timeOutSec = parseInt(jsonData['slideTime']);
            	}
            }
            */
            
            for(let k=0; k<listData.length; k++) {
                slideDashboard.push({
                	  'dossierId' : listData[k]['id']
                	, 'subject' : listData[k]['name']
                });
                $tmpHtml += '<a href="#" title="'+ listData[k]['name'] +'" style="margin-left: 5px;" id="move_'+k+'" name="move_Btn" onclick="moveUrlBtn(\'' + k +'\');">'+ (k+1) +'</a>';
            }
            $tmpHtml += '<span id="changeTimerStop" class="stop"></span>';
            $('#moveBtnDiv').html($tmpHtml);
            
            $('#changeTimerStop').on('click', function() {
                if($('#changeTimerStop').attr('class') == 'stop') {
                    $('#changeTimerStop').removeClass('stop');
                    $('#changeTimerStop').addClass('play');
                
                    timerStopCheck = true;
                    timerStopIdx = parseInt($('#currentTime').text().split(':')[0]) * 60 + parseInt($('#currentTime').text().split(':')[1]);
                    timerStopIdx2 = timerStopIdx;
                    timerStopText = $('#currentTime').text();
                } else {
                    $('#changeTimerStop').removeClass('play');
                    $('#changeTimerStop').addClass('stop');
                    
                    timerStopCheck = false;
                    countDown('currentTime', timeOutFunction, 0, timerStopIdx2, true);
                }
            });
            
            
            $('#currentSubject').text(slideDashboard[slideIdx]['subject']);
            $('#currentDash1').text(slideIdx + 1);
            $('#currentDash2').text(slideDashboard.length);
            $('#currentTime').text('페이지 이동중...');
            
            loadIframe(0, 1);
            
            $(window).resize(function (){
                for(var i=0; i<slideDashboard.length; i++) {
                     $('iframe[name=slideReportFrame_'+slideDashboard[i]['dossierId']+']').height(window.innerHeight - frameSubjectHeight);
                }
            });
            
            if(slideDashboard.length >= 1) {
                $('iframe[name=slideReportFrame_'+slideDashboard[0]['dossierId']+']').on('load', function(e) {
                    customInterval1 = setInterval(function() {
                        if($('iframe[name=slideReportFrame_'+slideDashboard[0]['dossierId']+']').contents().find('.mstrmojo-Editor.mstrWaitBox.modal').css('display') == 'none') {
                            checkIframeLoad(0);
                        }
                    }, 1000);
                });
            }
          
        }
	    
	    
	    //Iframe 세팅
	    function loadIframe(startPage, endPage) {
	    	var $displayArea = $('#displayArea');
	    	var iframeHeight = (window.innerHeight - frameSubjectHeight) + 'px';
	    	
	    	for(var i=startPage; i<endPage; i++) {
	    		if($('iframe[name=slideReportFrame_'+slideDashboard[i]['dossierId']+']').length == 0) {
	    			zidx -= 1;
	    			var url = replaceUrl + slideDashboard[i]['dossierId'];
	                var $tempHtml = '';
	                $tempHtml += '<iframe name="slideReportFrame_' + slideDashboard[i]['dossierId'] + '" frameborder="0" width="100%" height="'+iframeHeight+'" scrolling="yes" style="position: absolute; z-index: '+ zidx +';"></iframe>';
	                $displayArea.append($tempHtml);
	                
	                timeoutFrame(i, url);
	    		}
	    	}
	    }
	    
	    
	    function timeoutFrame(i, url) {
	    	setTimeout(function() { 
	    		  $('iframe[name=slideReportFrame_'+slideDashboard[i]['dossierId']+']').get(0).contentWindow.location.replace(url);
            }, 1000);
	    }
	    
	    
	    var iframeDarwCheck = true;
	    //Iframe Load 체크
	    function checkIframeLoad(iCheck) {
	    	if(iCheck == 0) {
                clearInterval(customInterval1);
            }
            
	    	$('html').animate({scrollTop:0}, 0);
	    	
	    	loadCheck +=1;
	    	console.log('loadCheck : ' + loadCheck);
	    	
	    	if(0 == iCheck && iframeDarwCheck) {
	    		setTimeout(function() { 
	                moveUrl('main');
	            }, 1000);
	    	}
    		
	    }
	    
	    
	    //슬라이드 이동
	    function moveUrl() {
	    	$('#currentSubject').text(slideDashboard[slideIdx]['subject']);
	    	$('#currentDash1').text(slideIdx + 1);
	    	
	    	for(var idx=0; idx<$('[name=move_Btn]').length; idx++) {
	    		if(idx == slideIdx) {
	    			$('[name=move_Btn]').eq(idx).addClass('active');
	    		} else {
	    			$('[name=move_Btn]').eq(idx).removeClass('active');
	    		}
	    	}
	    	
	    	var tmpLoadCheck =false;
	    	if($('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').length == 0) {
	    		tmpLoadCheck = true;
	    		loadIframe(slideIdx, slideIdx+1);
	    	}
	    	
	    	var tmpZidx = 50;
            for(var i=0; i<slideDashboard.length; i++) {
            	tmpZidx--;
            	if(i != slideIdx) {
            	    $('iframe[name=slideReportFrame_'+slideDashboard[i]['dossierId']+']').css('z-index', tmpZidx);
            	} else {
            		$('iframe[name=slideReportFrame_'+slideDashboard[i]['dossierId']+']').css('z-index', 100);
            	}
            }
            
            //다른 페이지 불러오기
            if((slideIdx+1) == slideDashboard.length) {
                iframeDarwCheck = false;
            } else {
                loadIframe(slideIdx+1, slideIdx+2);
            }
            
            if(tmpLoadCheck) {
            	 $('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').on('load', function(e) {
            		 customInterval2 = setInterval(function() {
                         if($('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').contents().find('.mstrmojo-Editor.mstrWaitBox.modal').css('display') == 'none'
                         || $('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').contents().find('.mstrmojo-Editor.mstrmojo-alert.modal').length == 1		 
                         ) {
                        	 clearInterval(customInterval2);
                        	 
                        	 $('#mstrWeb_waitCurtain').hide();
                        	 if(!timerStopCheck) {
                                 countDown('currentTime', timeOutFunction, 0, timeOutSec);
                             }
                         }
                     }, 500);
                 });
            } else {
            	if($('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').contents().find('.mstrmojo-Editor.mstrWaitBox.modal').length == 1) {
            		$('#mstrWeb_waitCurtain').hide();
            		if(!timerStopCheck) {
            		    countDown('currentTime', timeOutFunction, 0, timeOutSec);
            		}
                } else {
                	customInterval3 = setInterval(function() {
                		if($('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').contents().find('.mstrmojo-Editor.mstrWaitBox.modal').css('display') == 'none'
               		    || $('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').contents().find('.mstrmojo-Editor.mstrmojo-alert.modal').length == 1		
                		) {
                			clearInterval(customInterval3);
                            $('#mstrWeb_waitCurtain').hide();
                            if(!timerStopCheck) {
                                countDown('currentTime', timeOutFunction, 0, timeOutSec);
                            }
                        }
                    }, 500);
                }
            	
            }
            
	    }

	    
	    var timerEndCheck = false;
	    var timerEndIdx = -1;
	    
	    var timerStopCheck = false;
        var timerStopIdx = -1;
        var timerStopIdx2 = -1;
        var timerStopText = '';
	    
	    //버튼 클릭 이동
	    function moveUrlBtn(idx) {
	    	$('#mstrWeb_waitCurtain').show();
	    	timerEndCheck = true;
	    	timerEndIdx = idx;
	    	
	    	//timerStopCheck = false;
	    	//timerStopIdx = -1;
	    	
	    	/**/
	    	if(timerStopCheck) {
	    		timerEndCheck = false;
	    		timeOutFunction(timerEndIdx);
	            timerEndIdx = -1;
	    	}
	    	
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
	    
	    
	    //타임 아웃 로직
	    function timeOutFunction(moveIdx, stopCheck) {
	    	if((slideIdx+1) == slideDashboard.length) {
                slideIdx = 0;
            } else {
                slideIdx++;
            }
	    	
	    	if(typeof moveIdx != 'undefined') {
	    		slideIdx = parseInt(moveIdx);
	    		
	    		if($('iframe[name=slideReportFrame_'+slideDashboard[slideIdx]['dossierId']+']').length == 0) {
	    			setTimeout(function() { 
	                    moveUrl();
	                }, 7000);
	    		} else {
	    			setTimeout(function() { 
                        moveUrl();
                    }, 1000);
	    		}
	    	} else {
	    		moveUrl();
	    	}
	    	
            
	    };
	    
	    
	    //풀스크린 변경
	    function changeFullScreen() {
	    	/*
	    	var docElm = document.documentElement;
	    	
	    	if(docElm.requestFullscreen) {
	    		docElm.requestFullscreen();
	    	} else if(docElm.mozRequestFullScreen) {
                docElm.mozRequestFullScreen();
            } else if(docElm.webkitRequestFullScreen) {
                docElm.webkitRequestFullScreen();
            }
	    	*/
	    	
	    	/*
	    	if (FullScreenHelper.state()) {
	    		FullScreenHelper.exit();
	    		$('#fullScreenBtn').text('전체화면 켜기');
	        } else {
	        	FullScreenHelper.request(document);
	        	$('#fullScreenBtn').text('전체화면 끄기')
	        }
	    	*/
	    }
	    
	    
	    //세션 유지를 위한 콜 함수
	    function timeOutFunctionSession() {
	    	$.ajax({
	    	      url : '${pageContext.request.contextPath}/plugins/main/jsp/Empty.jsp'
	    	    , data : {}
	    	    , processData : false
	    	    , type : 'POST'
	    	    , success: function (data, text, request) {
	    	    	countDown('sessionCurrentTime', timeOutFunctionSession, 10, 0);
	    	    }
	    	    , error: function (jqXHR, textStatus, errorThrown) {
	    	        if (jqXHR && jqXHR.responseJSON && jqXHR.responseJSON.errorMessage) {
	    	            alert(jqXHR.responseJSON.errorMessage);
	    	        } else {
	    	            alert("처리 중 오류가 발생하였습니다.");
	    	        } 
	    	    }
	    	});
	    }
	    
	    $(document).ready(function() {
	    	
	    });
	    
	    /* **************************************************************************
	     * 초기함수 시작
	    ************************************************************************** */
	    
	    $(function() {
	    	
            let callParams = {
       			folderId : 'FA7EF09E40511428EEBC1C9E5D739D42'
       		};
       		callAjaxPost('/mstr/getFolderList.json', callParams, function(data) {
       			fnInit(data);
       		});
            
//             countDown('sessionCurrentTime', timeOutFunctionSession, 10, 0);
            
            $('#closeBtn').click(function() {
            	window.close();
            });
	    	
            
	    });
    
    </script>
</head>
<body style="overflow-x: hidden; overflow-y: hidden;">
    <div id="displayArea">
        <div style="width: 100%; height: 40px;">
	        <div class="title_wrap" style="float: left; margin: 0px; padding: 10px;">
	            <h1 id="currentSubject">보고서 제목</h1>
	        </div>
	        <div class="title_wrap" style="float: left; margin: 0px; padding: 6px;">
	           <!-- 
	            <h3> 대시보드 상황</h3>
	            <h3 style="padding-left: 5px;">:</h3>
	            <h3 id="currentDash1" style="padding-left: 5px;"></h3>
	            <h3 style="padding-left: 5px;">/</h3> 
	            <h3 id="currentDash2" style="padding-left: 5px;"></h3>
	           -->
	            <h3 style="padding-left: 20px;">남은 시간</h3>
	            <h3 style="padding-left: 5px;">:</h3>
	            <h3 id="currentTime" style="padding-left: 5px;"></h3>
	            <!-- 
	            <button id="changeTimerStop" style="margin-left: 10px;">멈춤</button>
	            -->
	        </div>
	        <!-- -->
	        <div class="paging ds_nav" style="float: left; margin: 0px; padding: 6px;">
                <span id="changeTimerStop" class="stop">멈춤</span>
	        </div>
	        
	        <div style="float: right; margin: 0px; padding: 6px; padding-right: 20px;">
               <button id="closeBtn">닫기</button>
            </div>
            <!-- 
	        <div id="moveBtnDiv" class="title_wrap" style="float: right; margin: 0px; padding: 6px; padding-right: 20px;">
	        </div>
	         -->
	        <div id="moveBtnDiv" class="paging ds_nav" style="float: right; margin: 0px; padding-right: 50px; padding-top: 5px;">
	            <!-- 
		        <span class="first"></span>
				<span class="prev"></span>
				<a class="active">1</a>
				<a>2</a>
				<a>3</a>
				<a>4</a>
				<a>5</a>
				<span class="next"></span>
				<span class="last"></span>
				<span class="stop"></span>
				<span class="play"></span>
				 -->
			</div>
	        
	        <div class="title_wrap" style="float: right; margin: 0px; padding: 6px; display: none;">
                <h3 style="padding-left: 20px;">세션 호출 시간</h3>
                <h3 style="padding-left: 5px;">:</h3>
                <h3 id="sessionCurrentTime" style="padding-left: 5px; margin-right: 10px;"></h3>
            </div>
	        
	        <!-- 
	        <button id="fullScreenBtn" onclick="changeFullScreen();" style="float: right; margin-top: 8px; margin-right: 20px;">전체화면 전환</button>
	        -->
        </div>
	</div>
	
	<div id="mstrWeb_waitCurtain" class="divWaitCurtain" style="z-index:9998;">
	   <div id="loadingImg" style="text-align: center; margin-top: 20%;">
	       <img alt="" src="${pageContext.request.contextPath}/_custom/image/main/loading.gif">
	   </div>
	</div>
</body>
</html>