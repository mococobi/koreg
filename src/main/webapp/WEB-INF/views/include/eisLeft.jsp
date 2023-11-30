<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<% 
	String mstrUserIdAttr = (String)session.getAttribute("mstrUserIdAttr");
	String mstrUserNameAttr = (String)session.getAttribute("mstrUserNameAttr");
%>
<!DOCTYPE html>
<html>
<body>
	<div id="left_menu_tree" style="height: calc(90vh); overflow: auto;">
		<%-- 
		<ul>
			<li>상담/접수/실행 분석</li>
			<li>
				<ol>
					<li>고객 속성별</li>
					<li>상품별</li>
					<li>업체수</li>
				</ol>
			</li>
		</ul>
		--%>
	</div>
</body>
<script type="text/javascript">

	$(function() {
		let callParams = {
			folderId : '032A5E114A59D28267BDD8B6D9E58B22'
		};
		callAjaxPost('/mstr/getFolderList.json', callParams, function(data){
			let menuReport = data['folder'];
			let drawHtml = drawMenuReport(menuReport, $('<ul>'));
			$('#left_menu_tree').append(drawHtml);
		});
	});
	
	//메뉴 리포트 동적 생성
	function drawMenuReport(menuReport, rtnHtml) {
		
		menuReport.forEach((menu, idx) => {
			let menuHtml = $('<li>', {
				  text : menu['name']
				, title : menu['name']
				, class : ''
				, style : ''
				, click : function(e) {
					let pagePrams = [
						  ["objectId", menu['id']]
						, ["type", menu['type']]
						, ["subType", menu['subType']]
					  	, ["isvi", menu['isVI']]
					];
					
					pageGoPost('_self', '${pageContext.request.contextPath}/app/main/mainEisView.do', pagePrams);
				}
			});
			$(rtnHtml).append(menuHtml);
			
			if(menu['child']) {
				let childHtml = drawMenuReport(menu['child'], $('<ul>'));
				let liHtml = $('<li>');
				$(liHtml).append(childHtml);
				$(rtnHtml).append(liHtml);
			}
			
		});
		
		return rtnHtml;
	}
	
</script>
</html>