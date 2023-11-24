
<%
%><%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ page
	import="com.mococo.web.util.CustomProperties"%>
<%
	String folderId = CustomProperties.getProperty("mstr.menu.folder.id");
%><!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">
#reportMenu {
	width: 300px;
}

#reportMenu .menu-node .menu-node {
	padding-left: 20px;
}

#reportMenu div[type='3'] span, #reportMenu div[type='55'] span {
	cursor: pointer;
}
</style>
<script type="text/javascript" charset="UTF-8"
	src="${pageContext.request.contextPath}/plugins/main/javascript/jquery-1.8.3.min.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="${pageContext.request.contextPath}/plugins/main/javascript/tree.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="${pageContext.request.contextPath}/plugins/main/javascript/common-form.js?v=2020032501"></script>
<script type="text/javascript">
	/*
	http://localhost:8080/MicroStrategy/plugins/esm/jsp/sso.jsp?mstrUserId=demo
	http://localhost:8080/MicroStrategy/plugins/main/jsp/main.jsp
	*/
	function renderMenu(folder) {
		navigateDepthFirst(
			{"id":"root", "child":folder},
			function (ref, stack) { // javascript 객체를 트리 탐색 시 각 노드에 대해 호출되는 콜백함수
				var parentId = undefined;
				if (stack && stack.length > 0) { parentId = stack[stack.length - 1]["id"]; }
				
				var $parent = $("#reportMenu [report-id='" + parentId + "']");
				if ($parent.get(0) != undefined) {
					var $child = $("<div class='menu-node' report-id='" + ref["id"] + "' type='" + ref["type"] + "' is-vi='" + ref["isVI"] + "'><span>" + ref["name"] + "</span></div>");
					
					$("span", $child).on("click", function() { 
						var $parent = $(this).parent();
						var type = $parent.attr("type");
						
						if (type == "3" || type == "55") {
							alert("id:[" + $parent.attr("report-id") + "], type:[" + type + "], isVI:[" + $parent.attr("is-vi") + "]");
							execReport($parent.attr("report-id"), type, $parent.attr("is-vi"));
						}
					});
					
					$parent.append($child);
				} 
			},
			"child"
		);		
	}
	
	function loadMenu() {
		// json으로 메뉴정보를 조회
		var option = {
			url: "${pageContext.request.contextPath}/app/getFolderList.json",			
			type: "post",
			data: JSON.stringify({folderId: "<%= folderId %>"}),
			contentType: "application/json;charset=utf-8",
			dataType: "json",
			success: function(result) {
				// 서버로부터 응답을 받는 시점에서 호출되는 콜백함수
				if (result && result["errorCode"] == "success") {
					renderMenu(result["folder"]);
				} else {
					alert("메뉴정보 조회 중 오류가 발생하였습니다.");
				}
			},
			error: function() { 
				alert("메뉴정보 조회 중 오류가 발생하였습니다.");
			},
			async: true
		};				
		
		$.ajax(option);
	}
	
	function execReport(objId, objTp, isvi) {
		var callurl = "${pageContext.request.contextPath}/plugins/main/jsp/report.jsp";
		var params = {
				  objectId: objId
				, type: objTp
				, isvi: isvi };
		_submit(callurl, "popReportView", params);
	}
	
	$(function() {
		loadMenu();
		
		$("#smartFactoryMain").click(function() {
			alert("스마트팩토리 시연-샘플");
			_submit("main3.jsp", "mainSmartFactory");
		});
	});
</script>
</head>
<body>
	<button id="smartFactoryMain">스마트팩토리-메인</button>
	<div id="reportMenu">
		<div report-id="root"></div>
	</div>
</body>
</html>