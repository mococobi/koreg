<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");

	String mstrServerName = CustomProperties.getProperty("mstr.server.name");
	String mstrServerPort = CustomProperties.getProperty("mstr.server.port");
	String mstrDefaultProjectName = CustomProperties.getProperty("mstr.default.project.name");

	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<head>
	<style type="text/css">
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
                	<jsp:include flush="true" page="/WEB-INF/views/include/eisLeft.jsp" />
                	
                	<div id="changeMenuDiv" style="position: fixed; left: 10.5%; z-index: 9999">
                		<button class="btn btn-secondary btn-sm" style="font-size: 1.5rem;" onclick="changeLeftMenu()"><i class="bi bi-list"></i></button>
                	</div>
                	
                    <div class="content flex2">
                        <div class="cont-area">
                        
                        	<div style="margin-bottom: 10px;">
                            	<h1 id="mstrReportTitle" style="margin-left: 10px;">EIS 리포트</h1>
                            </div>
                        
                            <div class="tab-cont-wrap">
                                <div class="cont-tab cont-tab1 on">
                                <!-- 메인 영역 -->
</body>
<script type="text/javascript">
	var __mstrServerName = "<%=mstrServerName%>";
	var __mstrServerPort = "<%=mstrServerPort%>";
	var __mstrDefaultProjectName = "<%=mstrDefaultProjectName%>";
	
	//메뉴 숨김 및 표시
	function changeLeftMenu() {
		let displayCheck = $('.lnb').css('display');
		if(displayCheck == 'block') {
			$('.lnb').hide("slide", {direction: "left"}, 500);
			$('#changeMenuDiv').css('left', '0px');
		} else {
			$('.lnb').show("slide", {direction: "left"}, 500);
			$('#changeMenuDiv').css('left', '10.5%');
		}
	}
	
</script>
</html>