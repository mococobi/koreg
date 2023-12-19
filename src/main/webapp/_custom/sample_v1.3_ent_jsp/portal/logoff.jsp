<%@ page import="WiseAccess.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="../common/common.jsp" %>
<%@ include file="../common/util.jsp" %>

 
<html>
<head>
	<meta content="text/html; charset=UTF-8" http-equiv="content-type">	
	<title>TouchEn wiseaccess [Raonsecure]</title>
	<link rel="stylesheet" href="../resources/css/common.css" type="text/css">
	<script src="../resources/js/jquery.js"></script>
</head> 


<%
	String sToken=getCookie(request, "ssotoken");

	if( sToken != null ) 
	{
		SSO sso = new SSO();	
		sso.setHostName(engineIP);
		sso.setPortNumber(enginePort);
		
		// SSO 세션 종료
		int nResult = sso.authOut( sToken );
		
		// 쿠키 제거
		setCookie( response, "ssotoken", "" );
	}
	else {
		%>
		<script>
			alert("로그오프 오류가 발생했습니다.\n");
			location.href = "./login.jsp";
		</script>
		<%	
	}
%>

<% if( sToken != null ){ // logoff ok %>

<body class="default">
	<table class="default">
	
	<tr>
		<td class="topline"> 
			<table>
	        <tr> 
				<td width='0%' height='46'> <div align='right'> </div> </td>
				<td width='83%' height='46'> <div align='right'> </div> </td>
	        </tr>
	        <tr> 
				<td colspan='2'><img src='../resources/img/common/dot_trans.gif' width='1' height='5'></td>
	        </tr>
			</table>
		</td>
	</tr>
  	
	
	<tr> 
		<td height='2' valign='middle' align='center'><img src='../resources/img/common/dot_grayline.gif' width='100%' height='1'></td>
	</tr>
  	
 	<tr> 
	    <td valign='middle' align='center'> 
			<table class="maintable">
	      	<tr> 
		       	<td width="397"> &nbsp;</td>
		       	<td width="372"> &nbsp;</td>
		    </tr>
       		<tr> 
				<td><img src='../resources/img/common/img_logoff.gif' width="374px" height="116px"></td>
	        </tr>
        	<tr> 
				<td><img src='../resources/img/common/touchenlogo_2line.gif' class="touchenlogo2line"></td>
          		<td> 
            		<table>
              		<tr>
                		<td><img src="../resources/img/common/dot_trans.gif" width="6" height="1"></td> 
                		<td> 
                  			<p><font size="2" color="#000000" face="굴림">회원님께서 로그온 한 모든 서비스의
                  														    세션 정보가 안전하게 정리되었습니다. </font></p>
                  			<p><font size="2" color="#666666" face="굴림">이제 안심하시고 다른 사이트로
                  														     이동하셔도 됩니다.</font></p>
          				</td>
              		</tr>
            		</table>
          		</td>
        	</tr>
        	<tr> 
				<td>&nbsp;</td>
				<td> 
            		<table>
              		<tr> 
                		<td height="22" colspan="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="50"> </td>
              		</tr>
              		<tr> 
                		<td width="6"><img src="../resources/img/common/dot_trans.gif" width="6" height="1"></td>
                		<td width="368"><a href="login.jsp"><img name="imgbtn" class="imgbtn" src="../resources/img/button/img_btnok_off.gif" width="58" ></a></td>
					</tr>
            		</table>
				</td>
			</tr>
			</table>
    	</td>
  	</tr>
  
	</table>
</body>
<% } %>
</html>

<script>
$("img[name='imgbtn']").hover(
	  function () {
		  $(this).attr("src", $(this).attr("src").replace("_off.gif", "_on.gif"));			  
	  },
	  function () {
		  $(this).attr("src", $(this).attr("src").replace("_on.gif", "_off.gif"));
	  }
);
</script>