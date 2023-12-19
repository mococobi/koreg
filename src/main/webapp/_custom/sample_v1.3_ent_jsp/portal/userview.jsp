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


<% if(successView == true) { %>
<body class="default">
<table class="default">
<tr>
	<td class="topline"> 
		<table>
        <tr> 
			<td width='0%' height='46'> <div align='right'></div> </td>
			<td width='100%' height='46'> <div align='right'><font size='2'> <a href='./portal.jsp'><b>[Home]</b></a><b> 
             		| <a href='../portal/logoff.jsp'>로그아웃</a>&nbsp;&nbsp;&nbsp;<img src='../resources/img/common/dot_trans.gif ' width='1' height='51'></b></font></div>
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
    <td valign="top" align="center"> 
      <table width="753" border="0" cellspacing="0" cellpadding="0" align="left">
        <tr> 
          <td valign="bottom"><img src="../resources/img/common/dot_trans.gif" width="1" height="80"></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td valign="bottom"><img src="../resources/img/common/touchenlogo_2line.gif" width="263" height="65"></td>
          <td><img src="../resources/img/common/img_userview.gif" width="235" height="46"></td>
        </tr>
        <tr> 
          <td valign="top"> <br>
            <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td><font color="#FFFFFF">...</font></td>
                <td colspan="3"><img src="../resources/img/common/img_userview_bar_top.gif" width="154" height="17"></td>
              </tr>
              <tr> 
                <td height="20">&nbsp;</td>
                <td height="20">&nbsp;</td>
                <td height="20"><a href="#go01"><img name="imgbtn" border="0" src="../resources/img/button/img_userinfo_off.gif" width="117" height="31"></a></td>
                <td height="20">&nbsp;</td>
              </tr>
              <tr> 
                <td height="29"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="30"></font></td>
                <td height="29">&nbsp;</td>
                <td height="29" valign="bottom"><a href="#go02"><img name="imgbtn" border="0" src="../resources/img/button/img_userpass_off.gif" width="117" height="27"></a></td>
                <td height="29">&nbsp;</td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
                <td colspan="3"><img src="../resources/img/common/img_userview_bar_button.gif" width="154" height="10"></td>
              </tr>
            </table>
          </td>
          <td valign="top"> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td height="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="20"></font></td>
              </tr>
              <tr> 
                <td height="8"><font size="2" color="#003399"><b><img src="../resources/img/menu/box.gif" width="16" height="16"> 
                  사용자 정보</b></font></td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td height="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="10"></font></td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#CCCCCC">
              <tr> 
                <td width="26%" height="25" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                아이디</font></td>
                <td width="74%" height="25" bgcolor="#FFFFFF"> 
                  <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td><font size="2" face="돋움"><%=authInfo.getUserId()%></font></td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="26%" height="25" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                이름</font></td>
                <td width="74%" height="25" bgcolor="#FFFFFF"> 
                  <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td><font size="2" face="돋움"><%=authInfo.getUserName()%></font></td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="26%" height="25" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                주민등록번호</font></td>
                <td width="74%" height="25" bgcolor="#FFFFFF"> 
                  <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td><font size="2" face="돋움"><%=authInfo.getRrn()%></font></td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="25"></font></td>
        </tr>
        <tr> 
          <td><img src="../resources/img/common/dot_trans.gif" width="1" height="1"></td>
          <td><img src="../resources/img/common/dot_redline.gif" width="100%" height="1"></td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td height="2"><font size="2"><a name="go01"></a></font></td>
              </tr>
              <tr> 
                <td height="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="70"></font></td>
              </tr>
              <tr> 
                <td><font size="2" color="#003399"><b><font size="2" color="#003399"><b><img src="../resources/img/menu/co_arrow_box_game.gif" width="12" height="11"> 
                  </b></font><font color="#003399">비밀번호 변경</font></b></font></td>
              </tr>
              <tr> 
                <td height="7"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="10"></font></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top"> 
            <p><font size="2" face="돋움" color="#666666">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;기존비밀번호 
              입력 후 새로운 비밀번호를 두 번 입력해 주십시오.</font></p>
            <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#CCCCCC">
            <form name="usermodifypwd" method="post">
              <tr> 
                <td width="26%" height="30" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                기존 비밀번호</font></td>
                <td width="74%" height="30" bgcolor="#FFFFFF"> 
                  <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td> 
                        <input type="password" name="oldpwd" maxlength="63" size="24">
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="26%" height="30" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                새 비밀번호</font></td>
                <td width="74%" height="30" bgcolor="#FFFFFF"> 
                  <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td> 
                        <input type="password" name="newpwd" maxlength="63" size="24">
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="26%" height="30" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                새 비밀번호 확인</font></td>
                <td width="74%" height="30" bgcolor="#FFFFFF"> 
                  <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td> 
                        <input type="password" name="newpwd2" maxlength="63" size="24">
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </form>  
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td height="2" colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="5"></font></td>
              </tr>
              <tr> 
                <td width="86%"> 
                  <div align="right"><a href="javascript:usermodifypwd()"><img name="imgbtn" border="0" src="../resources/img/button/btn_re_off.gif" width="70" height="30"></a></div>
                </td>
                <td width="14%"> 
                  <div align="right"><a href="#"><img name="imgbtn" border="0" src="../resources/img/button/btn_top_off.gif" width="70" height="30"></a></div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top"> 
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="25"></font></td>
              </tr>
              <tr> 
                <td colspan="2"><img src="../resources/img/common/dot_grayline.gif" width="100%" height="1"></td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2"><a name="go02"></a></font></td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="100"></font></td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2" color="#003399"><b><font size="2" color="#003399"><b><img src="../resources/img/menu/co_arrow_box_game.gif" width="12" height="11"> 
                  </b></font>개인정보 변경</b></font></td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="10"></font></td>
              </tr>
              <tr> 
                <td colspan="2"><font face="돋움" size="2" color="#666666">
                회원가입 시 등록하신 개인 정보의 변경을 원하시면 항목별로 변경할 내용을 재입력하신 후
                다음 수정 버튼을 눌러주시기 바랍니다.<br></font></td>
              </tr>
                            <tr> 
                <td colspan="2"> 
                  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#CCCCCC">
                  <form name="usermodify" method="post">                    
                    <tr> 
                      <td width="26%" height="30" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                      e-mail</font></td>
                      <td width="74%" height="30" bgcolor="#FFFFFF"> 
                        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td> 
                              <input type="text" name="email" value="<%=authInfo.getEmailAddress()%>" maxlength="127" size="40">
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr> 
                      <td width="26%" height="30" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                      전화번호</font></td>
                      <td width="74%" height="30" bgcolor="#FFFFFF"> 
                        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td> 
                              <input type="text" name="phone" value="<%=parser.search("PHONE")%>" maxlength="13" size="24">
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr> 
                      <td width="26%" height="30" bgcolor="#eeeeee"><font size="2" face="돋움">&nbsp;&nbsp;
                      주소</font></td>
                      <td width="74%" height="30" bgcolor="#FFFFFF"> 
                        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td> 
                              <input type="text" name="address" value="<%=parser.search("ADDRESS")%>" maxlength="40" size="40">
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </form>
                  </table>
                </td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="5"></font></td>
              </tr>
              <tr> 
                <td width="93%"> 
                  <div align="right"><a href="javascript:usermodify()"><img name="imgbtn" border="0" src="../resources/img/button/btn_re_off.gif" width="70" height="30"></a></div>
                </td>
                <td width="7%"> 
                  <div align="right"><a href="#"><img name="imgbtn" border="0" src="../resources/img/button/btn_top_off.gif" width="70" height="30"></a></div>
                </td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="25"></font></td>
              </tr>
              <tr> 
                <td colspan="2"><img src="../resources/img/common/dot_grayline.gif" width="100%" height="1"></td>
              </tr>
              <tr>
                <td colspan="2"><font size="2"><a name="go03"></a></font></td>
              </tr>
              <tr> 
                <td colspan="2"><font size="2"><img src="../resources/img/common/dot_trans.gif" width="1" height="100"></font></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top"><img src="../resources/img/common/dot_trans.gif" width="1" height="70"></td>
        </tr>
      </table>
      <div align="left"></div>
    </td>
  </tr>
</table>
</body>
<% } %>

</html>

<script>
function usermodifypwd()	// by leejh
{ 	
 	if(document.usermodifypwd.oldpwd.value  == ""){
 		alert("\n 기존 비밀번호를 입력하세요.");
 		document.usermodifypwd.oldpwd.focus();
 		return;
	}
	if(document.usermodifypwd.newpwd.value  == ""){
 		alert("\n 새 비밀번호를 입력하세요.");
 		document.usermodifypwd.newpwd.focus();
 		return;
	}
	if(document.usermodifypwd.newpwd.value != document.usermodifypwd.newpwd2.value){
 		alert("\n 같은 비밀번호를 입력하세요.");
 		document.usermodifypwd.newpwd.focus();
 		return;
	}
	 
	document.usermodifypwd.action="usermodifypwd.jsp";
	document.usermodifypwd.submit();
}

function usermodify()	// by leejh
{ 	 
	document.usermodify.action="usermodify.jsp";
	document.usermodify.submit();
}

function userdisable()	// by leejh
{ 	 
	if(document.userdisable.oldpwd.value  == ""){
 		alert("\n 기존 비밀번호를 입력하세요.");
 		document.userdisable.oldpwd.focus();
 		return;
	}
	document.userdisable.action="userdisable.jsp";
	document.userdisable.submit();
}

$("img[name='imgbtn']").hover(
	  function () {
		  $(this).attr("src", $(this).attr("src").replace("_off.gif", "_on.gif"));			  
	  },
	  function () {
		  $(this).attr("src", $(this).attr("src").replace("_on.gif", "_off.gif"));
	  }
);
</script>	