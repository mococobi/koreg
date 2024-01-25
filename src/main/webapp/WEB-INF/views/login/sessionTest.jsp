<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*" %>
<%@ page import="com.mococo.web.util.CustomProperties" %>
<%

%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>세션 테스트</title>
	

</head>
<body>
	<table border="1">
		<colgroup>
			<col width="20%">
			<col width="80%" style="max-width:500px;">
		</colgroup>
		<tbody>
			<tr>
				<td>mstrUserIdAttr</td>
				<td><%=session.getAttribute("mstrUserIdAttr") %></td>
			</tr>
			<tr>
				<td>mstrUserNameAttr</td>
				<td><%=session.getAttribute("mstrUserNameAttr") %></td>
			</tr>
			<tr>
				<td>mstr-user-vo</td>
				<td><%=session.getAttribute("mstr-user-vo") %></td>
			</tr>
			<tr>
				<td>portal-screen-id</td>
				<td><%=session.getAttribute("portal-screen-id") %></td>
			</tr>
			<tr>
				<td>mstrGroupIdMapAttr</td>
				<td><%=session.getAttribute("mstrGroupIdMapAttr") %></td>
			</tr>
			<tr>
				<td>PORAL_AUTH_LIST</td>
				<td><%=session.getAttribute("PORTAL_AUTH") %></td>
			</tr>
		</tbody>
	</table>

<script type="text/javascript">
	
</script>
</body>
</html>