<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<% 
	String mstrUserIdAttr = (String)session.getAttribute("mstrUserIdAttr");
	String mstrUserNameAttr = (String)session.getAttribute("mstrUserNameAttr");
%>
<!DOCTYPE html>
<html>
<body>
	<table style="width: 100%">
		<colgroup>
			<col width="20%">
			<col width="80%">
		</colgroup>
		<tbody>
			<tr>
				<td>
					<img id="top_menu_logo" alt="로고 이미지" src="${pageContext.request.contextPath}/image/logo/logo_hdr.png?v=20231123001" style="background: linear-gradient(125deg, #1575c7 0%, #1677c9 43%, #22a3f4 100%); box-shadow: 0.5rem 0.5rem 0.45rem rgba(8,132,229,.25);">
				</td>
				<td>
					<div style="float: right;">
						<div>
							<span>${mstrUserNameAttr}(${mstrUserIdAttr})님 환영합니다</span>
							<button id="top_menu_logout">로그아웃</button>
						</div>
						<div>
							<button>내 리포트</button>
							<button>비정형 분석</button>
							<button onclick="moveCommunityPage(1)">공지사항</button>
							<button onclick="moveCommunityPage(2)">FAQ</button>
							<button onclick="moveCommunityPage(3)">자료실</button>
						</div>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</body>
<script type="text/javascript">
	$(function() {
		
		//메인화면 이동
		$("#top_menu_logo").click(function() {
			let pagePrams = [];
			pageGoPost('_self', '${pageContext.request.contextPath}/app/main/mainView.do', pagePrams);
		});
		
		//포탈 로그아웃
		$("#top_menu_logout").click(function() {
			let msg = '로그아웃 하시겠습니까?';
			if (confirm(msg)) {
				let pagePrams = [];
				pageGoPost('_self', '${pageContext.request.contextPath}/app/login/logoutUser.do', pagePrams);
		    }
		});
	});
</script>
</html>