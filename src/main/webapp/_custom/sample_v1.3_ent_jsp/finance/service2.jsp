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
		String perm = sso.getRolePermission("/service2", sToken, request.getRemoteAddr());
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
             		| <a href='../portal/logoff.jsp'>로그아웃</a>&nbsp;&nbsp;&nbsp;<img src='img/dot_trans.gif' width='1' height='51'></b></font></div>
         	</td>
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
 	<td valign="top" align="left"> 
    	<table class="svcmaintable">
        <tr> 
			<td valign="bottom"><img src="../resources/img/common/dot_trans.gif" width="1" height="80"></td>
			<td>&nbsp;</td>
        </tr>
        <tr> 
			<td valign="bottom"><img src="../resources/img/common/touchenlogo_2line.gif" width="263" height="65"></td>
			<td><img src="../resources/img/common/img_svc2_creditcard.gif" width="360" height="45"></td>
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
					<%= sName + "[" + sUid + "]"  %></font></b><font face="굴림" color="#003399"> 
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
			<td height="64">&nbsp;</td>
			<td colspan="2" height="64"> 
				<table class="svcinner2">
				<tr> 
					<td width="110"><img src="../resources/img/menu/img_meminfo.gif" width="114" height="30"></td>
					<td width="403">&nbsp; </td>
				</tr>
				</table>
			</td>
        </tr>
        <tr> 
         	<td height="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="1"></td>
         	<td height="2"><img src="../resources/img/common/dot_grayline.gif" width="100%" height="1"></td>
        </tr>
        <tr> 
			<td height="10">&nbsp;</td>
			<td colspan="2" height="10">&nbsp;</td>
        </tr>
        <tr> 
			<td>&nbsp;</td>
			<td valign="top" colspan="2"> 
				<table class="svcinner">
				<tr bgcolor="#eeeeee"> 
					<td height="28" width="73"> 
						<div align="center"><font face="굴림" size="2">성명</font></div>
					</td>
					<td height="28" width="89"> 
						<div align="center"><font face="굴림" size="2">날짜</font></div>
					</td>
					<td height="28" width="139"> 
						<div align="center"><font face="굴림" size="2">카드번호</font></div>
					</td>
					<td height="28" width="97"> 
						<div align="center"><font face="굴림" size="2">결제계좌은행</font></div>
					</td>
					<td height="28" width="99"> 
						<div align="center"><font face="굴림" size="2">결제계좌번호</font></div>
					</td>
				</tr>
				<tr bgcolor="#FFFFFF"> 
					<td height="25" width="73"> 
						<div align="center"><font face="굴림" size="2"><%= sUid %></font></div>
                	</td>
                	<td height="25" width="89"> 
                  		<div align="center"><font face="굴림" size="2">2001/08/02</font></div>
                	</td>
               		<td height="25" width="139"> 
 	                 	<div align="center"><font face="굴림" size="2">4404 4500 1234 5678 </font></div>
                	</td>
                	<td height="25" width="97"> 
                  		<div align="center"><font face="굴림" size="2">외환은행</font></div>
                	</td>
                	<td height="25" width="99"> 
                  		<div align="center"><font face="굴림" size="2">971018****</font></div>
                	</td>
				</tr>
				</table>
			</td>
        </tr>
        <tr> 
          	<td height="21">&nbsp;</td>
          	<td valign="top" height="21" colspan="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="1"></td>
        </tr>
        <tr> 
          	<td>&nbsp;</td>
          	<td valign="top" colspan="2"> 
            	<table class="svcinner">
              	<tr bgcolor="#F3F3E9"> 
                	<td rowspan="2" width="170"> 
                  		<div align="center"><font face="굴림" size="2">결제정보</font></div>
                	</td>
                	<td width="174"> 
                  		<div align="right"><font face="굴림" size="2">결제일</font></div>
                	</td>
               		<td width="159"> 
                  		<div align="right"><font face="굴림" size="2">2001년 09월05일</font></div>
                	</td>
              	</tr>
              	<tr bgcolor="#FFFFFF"> 
                	<td width="174" bgcolor="#F3F3E9"> 
                  		<div align="right"><font face="굴림" size="2">결제대상금액</font></div>
                	</td>
                	<td bgcolor="#F3F3E9" width="159"> 
                  		<div align="right"><font face="굴림" size="2">미청구 상태</font></div>
                	</td>
              	</tr>
              	<tr> 
                	<td bgcolor="#FFFFFF" rowspan="2"> 
                  		<div align="center"><font face="굴림" size="2">사용한도</font></div>
                	</td>
                	<td bgcolor="#FFFFFF" width="174"> 
                  		<div align="right"><font face="굴림" size="2">총사용한도</font></div>
                	</td>
                	<td bgcolor="#FFFFFF" width="159"> 
                  		<div align="right"><font face="굴림" size="2">5,890,000원</font></div>
                	</td>
              	</tr>
              	<tr> 
                	<td bgcolor="#FFFFFF" width="174"> 
                  		<div align="right"><font face="굴림" size="2">사용금액</font></div>
                	</td>
                	<td bgcolor="#FFFFFF" width="159"> 
                  		<div align="right"><font face="굴림" size="2">7000,000원</font></div>
                	</td>
              	</tr>
              	<tr bgcolor="#F3F3E9"> 
                	<td width="170" rowspan="2"> 
                  		<div align="center"><font face="굴림" size="2">사용금액</font></div>
                	</td>
                	<td width="174"> 
                  		<div align="right"><font face="굴림" size="2">총사용금액</font></div>
	                </td>
                	<td width="159"> 
                  		<div align="right"><font face="굴림" size="2">0원</font></div>
                	</td>
				</tr>
              	<tr> 
                	<td width="174" bgcolor="#F3F3E9"> 
                  		<div align="right"><font face="굴림" size="2">현금서비스</font></div>
                	</td>
                	<td width="159" bgcolor="#F3F3E9"> 
                  		<div align="right"><font face="굴림" size="2">0원</font></div>
                	</td>
              	</tr>
              	<tr bgcolor="#FFFFFF"> 
                	<td width="170" rowspan="2"> 
                  		<div align="center"><font face="굴림" size="2">사용가능잔액</font></div>
                	</td>
                	<td width="174"> 
                  		<div align="right"><font face="굴림" size="2">총사용가능잔액</font></div>
                	</td>
                	<td width="159"> 
                  		<div align="right"><font face="굴림" size="2">5,006,500원</font></div>
                	</td>
              	</tr>
              	<tr> 
                	<td width="174" bgcolor="#FFFFFF"> 
                  		<div align="right"><font face="굴림" size="2">현금서비스</font></div>
                	</td>
                	<td width="159" bgcolor="#FFFFFF"> 
                  		<div align="right"><font face="굴림" size="2">700,000¿ø</font></div>
                	</td>
              	</tr>
            	</table>
          	</td>
        </tr>
        <tr> 
          	
          	<td valign="top" height="2"> <img src="../resources/img/common/dot_trans.gif " width="1" height="1"> </td>
          	<td valign="middle" height="2" rowspan="2"> 
            	<div align="right"><font size="2" color="#666666" face="굴림">* 현금서비스는 
             		 총액에 포함되어 있습니다.</font></div>
          	</td>
        </tr>
      	</table>
      	<div align="left"></div>
    </td>
</tr>
</table>
</body>
<% } %>	

</html>