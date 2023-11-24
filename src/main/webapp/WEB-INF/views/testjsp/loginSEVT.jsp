<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SEVT - Login Page</title>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/rsa/rsa.js"></script>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/rsa/jsbn.js"></script>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/rsa/prng4.js"></script>
<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/_custom/javascript/rsa/rng.js"></script>
</head>
<body oncontextmenu="return false;">
    <div class="login_wrap">
        <input id="userId" name="userId" type="text" onkeypress="if(event.keyCode == 13) {alert('엔터입력.. 로그인 함수 호출..');}">
        <input id="userPwd" name="userPwd" type="password" onkeypress="if(event.keyCode == 13) {alert('엔터입력.. 로그인 함수 호출..');}">
        <button id="loginProc">로그인</button>
    </div>
<script type="text/javascript">
function init() {
	//
}

function loginProc() {
	//
}
</script>
</body>
</html>