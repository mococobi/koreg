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
	
		<style type="text/css">
		html, body {
		  height: 100%;
		}
		
		.form-signin {
		  max-width: 330px;
		  padding: 1rem;
		}
		
		.form-signin .form-floating:focus-within {
		  z-index: 2;
		}
		
		.form-signin input[type="email"] {
		  margin-bottom: -1px;
		  border-bottom-right-radius: 0;
		  border-bottom-left-radius: 0;
		}
		
		.form-signin input[type="password"] {
		  margin-bottom: 10px;
		  border-top-left-radius: 0;
		  border-top-right-radius: 0;
		}
	</style>
</head>
<body class="d-flex align-items-center py-4 bg-body-tertiary">
	<main class="form-signin w-100 m-auto">
		<div id="container">
			<%--
			<img class="mb-4" src="${pageContext.request.contextPath}/image/logo/logo_hdr.png?v=20231123001" height="57" style="width: 100%">
			--%>
			<h1 class="h3 mb-3 fw-normal">EIS 로그인</h1>
	
			<div class="form-floating">
				<input id="uid" name="uid" type="text" class="form-control" placeholder="name@example.com" onkeyup="enterkey()">
				<label for="floatingInput">사번</label>
			</div>
			<div class="form-floating">
				<input id="upw" name="upw" type="password" class="form-control" placeholder="Password" onkeyup="enterkey()">
				<label for="floatingPassword">Password</label>
			</div>
			<!--
			<div class="form-check text-start my-3">
				<input class="form-check-input" type="checkbox" value="remember-me" id="flexCheckDefault"> 
				<label class="form-check-label" for="flexCheckDefault"> Remember me </label>
			</div>
			-->
			<button id="submit_signin"  class="btn btn-primary w-100 py-2">로그인</button>
			<p class="mt-5 mb-3 text-body-secondary">
				<c:out value="${errorMessage}"></c:out>
			</p>
		</div>
	</main>

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


//엔터키 이벤트
function enterkey() {
	if (window.event.keyCode == 13) {
		signinInit();
    }
}


function signinInit() {
    //alert("init SignIn...");
    $.ajax({
          type: 'post'
        , url: '${pageContext.request.contextPath}/app/login/createLoginKey.json'
        , async: false
        , contentType: 'application/json;charset=utf-8'
        , data: JSON.stringify({})
        , dataType: 'json'
        , success: function(data, text, request) {
            //console.log(data);
            if (data.RSAModulus && data.RSAExponent) {
                signinProc(data.RSAModulus, data.RSAExponent);
            } else {
                alert('처리중 오류가 발생하였습니다.')
            }
            
        }
        , error: function(jqXHR, textStatus, errorThrown) {
        	errorProcess(jqXHR, textStatus, errorThrown);
        }
    });
}


function signinProc(key1, key2) {
    //alert('proc SignIn...');
    
    const rsa = new RSAKey();
    rsa.setPublic(key1, key2);
    
    $frmSignIn = $('#frmSignIn');
    $frmSignIn.empty();
    $frmSignIn.attr('action', '${pageContext.request.contextPath}/app/login/loginUserEis.do');
    $('<input type="hidden"/>').attr('name', 'encAcntID').val(rsa.encrypt($('#uid').val())).appendTo($frmSignIn);
    $('<input type="hidden"/>').attr('name', 'encAcntPW').val(rsa.encrypt($('#upw').val())).appendTo($frmSignIn);
    $('<input type="hidden"/>').attr('name', 'screenId').val('EIS').appendTo($frmSignIn);
    $frmSignIn.attr('method', 'post');
    $frmSignIn.attr('target', '_self').submit();
    $frmSignIn.empty().removeAttr('action','').removeAttr('target','').removeAttr('method','');
    
}

</script>
</body>
</html>