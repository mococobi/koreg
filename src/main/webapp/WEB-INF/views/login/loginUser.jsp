<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>로그인</title>
	
	<jsp:include flush="true" page="/WEB-INF/views/include/pageCss.jsp" />
	<jsp:include flush="true" page="/WEB-INF/views/include/pageJs.jsp" />
	
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/javascript/rsa/rsa.min.js"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/javascript/rsa/jsbn.min.js"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/javascript/rsa/prng4.min.js"></script>
	<script type="text/javascript" charset="UTF-8" src="${pageContext.request.contextPath}/javascript/rsa/rng.min.js"></script>
</head>
<body class="text-center">
    <!--  html 전체 영역을 지정하는 container -->
    <div id="container">
        <!--  login 폼 영역을 : loginBox -->
        <div id="loginBox">

            <!-- 로그인 페이지 타이틀 -->
            <div id="loginBoxTitle">로그인 화면</div>
            <!-- 아이디, 비번, 버튼 박스 -->
            <div id="inputBox">
                <div class="input-form-box">
                    <span>아이디 </span><input type="text" id="uid" name="uid" class="form-control">
                </div>
                <div class="input-form-box">
                    <span>비밀번호 </span><input type="password" id="upw" name="upw" class="form-control">
                </div>
                <div class="button-login-box">
                    <button id="submit_signin" type="button" class="btn btn-primary btn-xs" style="width: 100%">로그인</button>
                </div>
            </div>
            <div class="msgLogin"><c:out value="${errorMessage}"></c:out></div>
        </div>
    </div>

    <form id="frmSignIn"></form>
    <!-- Bootstrap Bundle with Popper -->

<script type="text/javascript">
//prevent mouse right click..
window.addEventListener('contextmenu', function(e) { e.preventDefault(); }, false); // Not compatible with IE < 9

let $frmSignIn;
// init
$(document).ready(function() {
    $('#submit_signin').off('click').on('click', signinInit);
});


function signinInit() {
    //alert("init SignIn...");
    $.ajax({
        type: "post",
        url: "${pageContext.request.contextPath}/app/login/createLoginKey.json",
        async: false,
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify({}),
        dataType: "json",
        success: function(data, text, request) {
            //console.log(data);
            if (data.RSAModulus && data.RSAExponent) {
                signinProc(data.RSAModulus, data.RSAExponent);
            } else {
                alert("처리중 오류가 발생하였습니다.")
            }
            
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
        }
    });
}


function signinProc(key1, key2) {
    //alert("proc SignIn...");
    
    const rsa = new RSAKey();
    rsa.setPublic(key1, key2);
    
    $frmSignIn = $("#frmSignIn");
    $frmSignIn.empty();
    $frmSignIn.attr("action", "${pageContext.request.contextPath}/app/login/loginUser.do");
    $("<input type='hidden'/>").attr('name', 'encAcntID').val(rsa.encrypt($("#uid").val())).appendTo($frmSignIn);
    $("<input type='hidden'/>").attr('name', 'encAcntPW').val(rsa.encrypt($("#upw").val())).appendTo($frmSignIn);
    $frmSignIn.attr("method", "post");
    $frmSignIn.attr("target", "_self").submit();
    $frmSignIn.empty().removeAttr("action","").removeAttr("target","").removeAttr("method","");
    
}

</script>
</body>
</html>