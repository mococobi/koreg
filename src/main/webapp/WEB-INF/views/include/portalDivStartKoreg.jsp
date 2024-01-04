<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
	List<String> PORAL_AUTH_LIST = (List<String>)session.getAttribute("PORTAL_AUTH");

	String portalAppName = (String)CustomProperties.getProperty("portal.application.file.name");
	pageContext.setAttribute("portalAppName", portalAppName);
%>
<!DOCTYPE html>
<html>
<body>
	<div class="wrap" style="background-color: rgb(231, 238, 249);"> <!-- 페이지 전체를 감싸는 wrap -->
		<!-- pop -->
		<div class="pop"> <!-- 팝업 영역 (배경) -->
			<div class="pop-inner"><!-- 팝업 내용이 들어가는 부분 -->
			</div>
	    </div>
	    <!-- //pop -->
	    
	    <!-- header -->
	    <jsp:include flush="true" page="/WEB-INF/views/include/portalTop_Center${portalAppName}.jsp" />
	   
		<!-- contents -->
        <div class="contents"> <!-- 본문 영역 -->
            <div class="bg sky"> <!-- 본문 배경색 -->
                <div class="inner flex">
                	<!-- Left Menu -->
                	<jsp:include flush="true" page="/WEB-INF/views/include/portalLeft${portalAppName}.jsp" />
                	
                    <div class="content flex2">
                        <div class="cont-area">
                            <div class="tab-btn-wrap flex">
                                <button onclick="popupMstrPage('SHARE_REPORT')" class="tab-btn">부서 공유 리포트</button>
                                <button onclick="popupMstrPage('MY_REPORT')" class="tab-btn">내 리포트</button>
                                <button onclick="popupMstrPage('NEW_REPORT')" class="tab-btn">비정형 분석</button>
                                <%--
                                <button onclick="popupMstrPage('NEW_DOSSIER')" class="tab-btn">새 대시보드</button>
                                --%>
                                <button onclick="moveCommunityPage(1)" class="tab-btn">공지사항</button>
                                <button onclick="moveCommunityPage(2)" class="tab-btn">FAQ</button>
                                <% if(PORAL_AUTH_LIST.contains("PORTAL_SYSTEM_ADMIN")) { %>
									<!-- 관리자 기능 -->
									<button onclick="moveAdminPage('BOARD_ADMIN')" class="tab-btn">관리자</button>
								<% } %>
                                
                            </div>
                            <div style="margin-bottom: 10px;">
                            	<h1 id="mstrReportTitle"></h1>
                            </div>
                            <div class="tab-cont-wrap">
                                <div class="cont-tab cont-tab1 on">
                                <!-- 메인 영역 -->
</body>
</html>