<%@ page import="WiseAccess.*" %>
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
	SSO sso = new SSO(sApiKey);
	sso.setHostName(engineIP);
	sso.setPortNumber(enginePort);
	
	boolean successSSO	= false;
	boolean successEAM	= false;
	boolean successView	= false;
	
	String sUid			= null;
	String sName		= null;
%>

<%
	// SSO 체크
	String sToken = getCookie(request, "ssotoken");
	int rst = -1;
	
	// SSO 성공
	if( sToken != null && sToken.length() > 0) {
		rst = sso.verifyToken( sToken, request.getRemoteAddr() );
		if( rst >= 0 ) {
			sUid = sso.getValueUserID();
			successSSO = true;
		}
	}
	
	// SSO 실패
	if(rst < 0) {
		switch(rst)
		{
			case -1205:
				out.println("<script> alert('잘못된 인증토큰입니다.\\n\\nError Code("+rst+")');</script>");
			break;
			
			case -2700:
				out.println("<script> alert('접속시간 제한정책에 의해 차단되었습니다.\\n\\nError Code("+rst+")');</script>");
			break;

			case -2714:
				out.println("<script> alert('제한정책에 의해 차단되었습니다.\\n\\nError Code("+rst+")');</script>");
			break;

			case -2902:
				out.println("<script> alert('세션만료 되었습니다.\\n\\nError Code("+rst+")');</script>");
			break;

			case -2434:
				out.println("<script> alert('패스워드가 틀렸습니다.\\n\\nError Code("+rst+")');</script>");
			break;

			case -3034:
				out.println("<script> alert('등록되지 않은 사용자입니다.\\n\\nError Code("+rst+")');</script>");
			break;

			default:
				out.println("<script> alert('사용자 인증 오류입니다.\\n\\nError Code("+rst+")');</script>");
			break;
		}
		setCookie( response, "ssotoken", "");
	}
%>

<%
	if(successSSO == true) {
		successEAM = true;
	}
%>

<%
	// 사용자 정보 조회
	SsoAuthInfo authInfo = null;
	SsoParser parser = null;
	int nResult = -1;
	
	if(successEAM == true) {
		authInfo=sso.userView(sToken, request.getRemoteAddr());
		nResult=sso.getLastError();

		// 사용자 정보조회 성공
		if( nResult >=0 ) {
			sName = authInfo.getUserName();
			parser = new SsoParser(authInfo.getProfile()); // profile info
			successView = true;
		}
		
		// 사용자 정보조회 실패
		else {
			%>
			<script>
				alert("사용자가 인증되지 않았거나 사용자 조회 오류입니다." + "\n\n [오류코드=<%=nResult%>]");
			</script>
			<%
		}
	}
%>



<body class="default">
<table class="default">
	
<% 
	if( successSSO == false ){ // not yet logon 
%>
<tr>
	<td class="topline"> 
		<table>
        <tr> 
			<td colspan='2'><img src='../resources/img/common/img_selfregist.gif' width='1' height='5'></td>
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
			<td>Version 1.3 Enterprise Demo</td>
        </tr>
        <tr> 
			<td><img src='../resources/img/common/touchenlogo_2line.gif' class="touchenlogo2line"></td>
			<td><img src='../resources/img/common/img_welcome.gif' width='374' height='34'></td>
        </tr>
        <tr> 
			<td><img src='../resources/img/common/img_selfregist.gif' width='1' height='80'></td>
			<td><img src='../resources/img/common/img_vline.gif' width='23' height='114'></td>
        </tr>
        <tr> 
			<td>&nbsp;</td>
			<td>
				<table>
              	<tr> 
                	<td><a href='./login.jsp' ><img name="imgbtn" class="imgbtn" src='../resources/img/button/img_btnlogon_off.gif' width='83'></a></td>
              	</tr>
            	</table>
          	</td>
        </tr>
             <tr> 
        <td width='397'>&nbsp;</td>
          <td width='372'>
          <font size='2'>
          <font face='돋움' color='#333333'>
          <br>

          </font></font>
          </td>
        </tr>        
      	</table>
    </td>
</tr>
	

<%
} else { //already logon 
%>

<tr>
	<td class="topline"> 
		<table>
        <tr>
			<td width='0%' height='46'> <div align='right'></div> </td>
			<td width='100%' height='46'> <div align='right'><font size='2'><b> 
             		<a href='userview.jsp'>[회원정보]</a>              
             		| <a href='../finance/service1.jsp'>[인터넷뱅킹 서비스]</a>
             		| <a href='../finance/service2.jsp'>[카드 서비스]</a>
             		| <a href='logoff.jsp'>[로그아웃]</a> </b></font></div>
         	</td>
       	</tr>
       	<tr> 
			<td colspan='2'><img src='../resources/img/common/img_selfregist.gif' width='1' height='5'></td>
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
				<td>Version 1.3 Enterprise Demo</td>
	        </tr>
	        <tr> 
				<td><img src='../resources/img/common/touchenlogo_2line.gif' class="touchenlogo2line"></td>
	         	<td width='372'><font size='2'><b><font face='굴림' color='#000000'>&nbsp;
	         				<%= sName + "[" + sUid + "]" %></font></b><font face='굴림' color='#333333'>님 환영합니다.</font></font></td>
	       	</tr>
	       	<tr> 
				<td><img src='../resources/img/common/img_selfregist.gif' width='1' height='80'></td>
	        </tr>
      		</table>
    	</td>   	
 	</tr>
 	
<%
}
%>

</table>
</body>
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
