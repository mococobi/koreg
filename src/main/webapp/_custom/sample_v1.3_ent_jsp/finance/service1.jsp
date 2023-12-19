<%@ page import="WiseAccess.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>

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
	// EAM 체크
	if(successSSO == true) {
	
		// EAM 권한 있음
		String perm = sso.getRolePermission("/service1", sToken, request.getRemoteAddr());
		if(perm != null && perm.equals("1")) {
			successEAM = true;
		}
		
		// EAM 권한 없음
		else {
			%>
			<script>
				alert("사용자가 EAM 권한이 없습니다.\n");
				history.go("-1");
			</script>
			<%	
		}
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

<% if(successView == true) { %>
<body class="default">
<table class="default">
<tr>
	<td class="topline"> 
		<table>
        <tr> 
			<td width='0%' height='46'> <div align='right'></div> </td>
			<td width='100%' height='46'> <div align='right'><font size='2'> <a href='../portal/portal.jsp'><b>[Home]</b></a><b> 
             		| <a href='../portal/logoff.jsp'>로그아웃</a></b></font></div>
         	</td>
       	</tr>
       	<tr> 
			<td colspan='2'><img src='../resourcess/img/common/dot_trans.gif' width='1' height='5'></td>
        </tr>
		</table>
	</td>
</tr>

<tr> 
	<td height='2' valign='middle' align='center'><img src='../resourcess/img/common/dot_grayline.gif' width='100%' height='1'></td>
</tr>

<tr> 
 	<td valign="top" align="left"> 
    	<table class="svcmaintable">
        <tr> 
			<td valign="bottom"><img src="../resources/img/common/dot_trans.gif" width="1" height="80"></td>
			<td>&nbsp;</td>
        </tr>
        <tr> 
			<td valign="bottom"><img src="../resources/img/common/touchenlogo_2line.gif" width="263" height="65"></td>
			<td><img src="../resources/img/common/img_svc1_interbank.gif" width="360" height="45"></td>
        </tr>
        <tr> 
			<td>&nbsp;</td>
			<td> 
				<table class="default">
				<tr> 
					<td height="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="10"></font></td>
				</tr>
				<tr> 
					<td><font size="2"><b><font face="굴림" color="#000000">
					<%= sName + "[" + sUid + "]" %></font></b><font face="굴림" color="#003399"> 
					<font color="#666666">님의 맞춤서비스 화면입니다.<br>
                	 					다음 계좌번호를 선택한 후, 아래 메뉴에서 원하는 서비스를<br>
										선택하여 주십시오.</font></font></font></td>
				</tr>
				</table>
			</td>
        </tr>
        <tr> 
			<td>&nbsp;</td>
			<td><img src="../resources/img/common/dot_trans.gif" width="1" height="20"></td>
        </tr>
        <tr> 
			<td height="11">&nbsp;</td>
			<td height="11"> 
				<table>
				<tr> 
					<td width="114"><img name="imgbtn" src="../resources/img/menu/img_deposit_on.gif" width="114" height="30"></td>
					<td width="117"><a href="#"><img name="imgbtn" border="0" src="../resources/img/menu/img_deposit_off.gif" width="117" height="30"></a></td>
					<td width="99"><a href="#"><img name="imgbtn" border="0" src="../resources/img/menu/img_deposit_off.gif" width="123" height="30"></a></td>
					<td width="183"><div align="right"><font face="굴림" size="2">총계좌수 : 1 개</font></div></td>
             	</tr>
            	</table>
          	</td>
        </tr>
        <tr> 
          	<td height="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="1"></td>
         	<td height="2"><img src="../resources/img/common/dot_grayline.gif" width="100%" height="1"></td>
        </tr>
        <tr> 
          	<td>&nbsp;</td>
          	<td>&nbsp;</td>
        </tr>
        <tr> 
          	<td>&nbsp;</td>
          	<td valign="top"> 
            	<table class="svcinner">
              	<tr bgcolor="#eeeeee"> 
                	<td height="38" width="28"> 
                  		<div align="center"><font face="굴림" size="2">선택</font></div>
                	</td>
                	<td height="38" width="112"> 
                  		<div align="center"><font face="굴림" size="2">계좌번호</font></div>
                	</td>
                	<td height="38" width="80"> 
                  		<div align="center"><font face="굴림" size="2">현재잔액</font></div>
                	</td>
                	<td height="38" width="72"> 
                  		<div align="center"><font face="굴림" size="2">최근거래일<br>/만기일</font></div>
                	</td>
                	<td height="38" width="52"> 
                  		<div align="center"><font face="굴림" size="2">개설점</font></div>
                	</td>
                	<td height="38" width="68"> 
                	  	<div align="center"><font face="굴림" size="2">신규일</font></div>
                	</td>
                	<td height="38" width="79"> 
                  		<div align="center"><font face="굴림" size="2">이체가능여부</font></div>
                	</td>
              	</tr>
              	<tr bgcolor="#FFFFFF"> 
                	<td height="25" width="28"> 
                  		<div align="center"> <input type="radio" name="radiobutton" value="radiobutton"> </div>
                	</td>
                	<td height="25" width="112"> 
                  		<div align="center"><font face="굴림" size="2">065-19-21464-2</font></div>
                	</td>
                	<td height="25" width="80"> 
	                  	<div align="right"><font face="굴림" size="2">5,312,760원&nbsp;&nbsp;</font></div>
    	            </td>
        	        <td height="25" width="72"> 
            	      <div align="center"><font face="굴림" size="2">2001/08/10</font></div>
                	</td>
                	<td height="25" width="52"> 
                  		<div align="center"><font face="굴림" size="2">퇴계로</font></div>
                	</td>
                	<td height="25" width="68"> 
                  		<div align="center"><font face="굴림" size="2">1994/09/13</font></div>
                	</td>
                	<td height="25" width="79"> 
                  		<div align="center"><font face="굴림" size="2">이체가능</font></div>
                	</td>
              	</tr>
            	</table>
          	</td>
        </tr>
        <tr> 
			<td>&nbsp;</td>
          	<td valign="top"><img src="../resources/img/common/dot_trans.gif" width="1" height="70"></td>
        </tr>
        <tr> 
			<td>&nbsp;</td>
          	<td valign="top"> 
				<table class="default">
				<tr> 
					<td width="25%"><a href="#"><img name="imgbtn" class="imgbtn" src="../resources/img/button/img_accountcheck_off.gif" width="106" ></a></td>
					<td width="25%"><a href="#"><img name="imgbtn" class="imgbtn" src="../resources/img/button/img_transfer_off.gif" width="106" ></a></td>
					<td width="25%"><a href="#"><img name="imgbtn" class="imgbtn" src="../resources/img/button/img_clientsvc_off.gif" width="106" ></a></td>
					<td width="25%"><a href="#"><img name="imgbtn" class="imgbtn" src="../resources/img/button/img_infocenter_off.gif" width="106" ></a></td>
				</tr>
				</table>
			</td>
		</tr>
		</table>
		<div align="left"></div>
    </td>
</tr>
</table>
</body>

<% } %>

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
</html>