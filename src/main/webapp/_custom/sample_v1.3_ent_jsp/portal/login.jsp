<%@ page import="WiseAccess.*" %>
<%@ page import="java.util.Date" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="../common/common.jsp" %>
<%@ include file="../common/util.jsp" %>

<%
	// SSO 연동
	
	String sToken = getCookie(request, "ssotoken");
	
	if( sToken != null && sToken.length() > 0) 
	{
		SSO sso = new SSO(sApiKey);
		sso.setHostName(engineIP); // engine이 설치된 아이피 default 127.0.0.1
		sso.setPortNumber(enginePort); // engine 이 사용하고 있는 포트넘버 default 7000
		
		int nResult = sso.verifyToken( sToken, request.getRemoteAddr() );
		if( nResult >= 0 )
		{
			response.sendRedirect("./portal.jsp");
		}
	}
%>

<%
	// 로그인 처리
	
	String sUid	= request.getParameter("uid");
	String sPwd = request.getParameter("pwd");
	int nResult=-1;
	
	if( sUid != null && sPwd != null )
	{
		SSO sso = new SSO( sApiKey );
		sso.setHostName(engineIP); // engine이 설치된 아이피 default 127.0.0.1
		sso.setPortNumber(enginePort); // engine 이 사용하고 있는 포트넘버 default 7000
		sso.setCharacterSet("euc-kr");
		
		SsoAuthInfo authInfo = new SsoAuthInfo();
		authInfo = sso.authID( sUid, sPwd, true, request.getRemoteAddr()); // 사용자 아이디 및 비밀번호 정보로 인증
		nResult = sso.getLastError();
		
		// 로그인 성공
		if( nResult >= 0 )  // Logon Ok
		{		
			setCookie( response, "ssotoken", authInfo.getToken() );
						
			if( authInfo.getLastLogonTime() > 0 )
			{
				Date date = authInfo.getLastLogonTimeByDate();
				java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
				%>
				<script>
					alert("<%=authInfo.getUserName()+"("+sUid + ")"%>님이 인증되었습니다."
						+ "\n\n회원님의 가장 최근 로그온 시간은 <%=format.format(date)%> 입니다.");		
					location.href = "portal.jsp";
				</script>
				<%
			}
			else
			{
				%>
				<script>
					alert("<%=authInfo.getUserName()+"("+sUid + ")"%>님이 인증되었습니다."
						+ "\n\n회원님은 등록 후 처음 로그온 하셨습니다.");		
					location.href = "portal.jsp";
				</script>
				<%
			}	
		}
		
		// 로그인 실패
		else {
			switch(nResult)
			{
				case -1205:
					out.println("<script> alert('잘못된 인증토큰입니다.\\n\\nError Code("+nResult+")');</script>");
				break;
				
				case -2700:
					out.println("<script> alert('접속시간 제한정책에 의해 차단되었습니다.\\n\\nError Code("+nResult+")');</script>");
				break;

				case -2714:
					out.println("<script> alert('제한정책에 의해 차단되었습니다.\\n\\nError Code("+nResult+")');</script>");
				break;

				case -2902:
					out.println("<script> alert('세션만료 되었습니다.\\n\\nError Code("+nResult+")');</script>");
				break;

				case -2434:
					out.println("<script> alert('패스워드가 틀렸습니다.\\n\\nError Code("+nResult+")');</script>");
				break;

				case -3034:
					out.println("<script> alert('등록되지 않은 사용자입니다.\\n\\nError Code("+nResult+")');</script>");
				break;

				default:
					out.println("<script> alert('사용자 인증 오류입니다.\\n\\nError Code("+nResult+")');</script>");
				break;
			}
			setCookie( response, "ssotoken", "");
		}
	}	
%>

<html>
<head>
	<title>TouchEn wiseaccess [Raonsecure]</title>
	<meta http-equiv='Content-Type' content='text/html;' charset='utf-8'>
	<meta http-equiv='Pragma' content='No-Cache' charset="utf-8">
    <script src="../resources/js/jquery.js"></script>
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css">
</head>

<body class="default">
	<table class="default">
	
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
				<td><img src='../resources/img/common/img_members.gif' width="151px" height="116px"></td>
	        </tr>
       		<tr> 
				<td><img src='../resources/img/common/touchenlogo_2line.gif' class="touchenlogo2line"></td>
				<td> 
	           		<table class="logontab">
             		<tr>
             			<td width="8%"></td> 
               			<td valign="middle">
							<form id="logonform" method="POST" action="login.jsp">
               				<table class="center">
                   			<tr> 
                     			<td height="10" colspan="2"></td>
                   			</tr>
                   			<tr> 
	               				<td width="29%"> <font size="2"><b><font color="#333333">아이디  : </font></b></font></td>
    	                 		<td width="71%" > <input type="text" name="uid" size="10"> </td>
                   			</tr>
                   			<tr> 
                     			<td height="6" colspan="2"></td>
                   			</tr>
                   			<tr> 
                     			<td width="29%"> <font size="2"><b><font color="#333333">비밀번호  : </font></b></font></td>
                     			<td width="71%"> <input type="password" name="pwd" size="10" maxlength="8"> </td>
                   			</tr>
                   			<tr> 
                     			<td height="1" colspan="2"></td>
                   			</tr>
                			</table>
							</form> 						
               			</td>
             		</tr>
             		<tr>
             			<td height="28px" colspan="2" bgcolor="#ffffff">
             				<div id="txt001"></div>
						</td>
             		</tr>
           			</table>
         		</td>
			</tr>
        	<tr> 
          		<td width="397" height="25"></td>
          		<td width="372" height="25"> 
            		<table class=default>
              		<tr> 
                		<td></td>
              		</tr>
              		<tr> 
                		<td bgcolor="#eeeeee">
	                  		<table >
		                    <tr> 
		                      	<td width="20%" height="27">
		                      		<font size="2"><b>
		                        	<a href="javascript:logon();">
		                        	<img name="imgbtn" class="imgbtn" src="../resources/img/button/btn_logon_off.gif" width="58" >
		                        	</a></b></font>
		                        </td>                      
		                      	<td width="3%" height="27">
		                      		<img src="../resources/img/common/dot_trans.gif" width="5" height="24"> 
		                      	</td>
								<td width="77%" height="27">
		                      		<a href="index.jsp"> 
		                      		<img name="imgbtn" class="imgbtn" src="../resources/img/button/btn_cancle_off.gif" width="58">
		                      		</a>
		                      	</td>
		                    </tr>
	                  		</table>
                		</td>
              		</tr>
            		</table>
          		</td>
        	</tr>
      		</table>     		
    	</td>
	</tr>
</table>
</body>
</html>
<script>
function logon(){	
	var test = $("input[name='uid']").val();
	
	if($("input[name='uid']").val()==''){
		$('#txt001').text('텍스트를 정확히 입력하여 주십시오').show().fadeOut(1000);
	} else{
		$('#logonform').attr('action', 'login.jsp').submit();
	}
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
