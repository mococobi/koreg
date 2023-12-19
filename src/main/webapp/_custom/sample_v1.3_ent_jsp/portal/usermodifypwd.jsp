<%@ page import="WiseAccess.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="common.jsp" %>
<%@ include file="util.jsp" %>

 
<html>
<head>
	<meta content="text/html; charset=UTF-8" http-equiv="content-type">	
	<title>TouchEn wiseaccess [Raonsecure]</title>
	<link rel="stylesheet" href="../resources/css/common.css" type="text/css">
	<script src="../resources/js/jquery.js"></script>
</head> 

<%
String sToken=getCookie(request, "ssotoken");
int nResult=-1;
if( sToken != null )
{
	SSO sso = new SSO();
	sso.setHostName(engineIP);
	sso.setPortNumber(enginePort);
	
	nResult=sso.userModifyPwd( sToken, 
			request.getParameter("oldpwd"),
			request.getParameter("newpwd") );
}
%>

<% if( nResult >= 0 ){ //  ok %>
<body class="default">
	<table class="default">
	
	<tr>
		<td class="topline"> 
			<table>
	        <tr> 
	        	<td rowspan='2' class="toplinelogo"><img src='../../../img/common/raonlogo_1line.gif' class="raonlogo1line"> </td>
				<td width='0%' height='46'> <div align='right'> </div> </td>
				<td width='83%' height='46'> <div align='right'> </div> </td>
	        </tr>
	        <tr> 
				<td colspan='2'><img src='../../../img/common/dot_trans.gif' width='1' height='5'></td>
	        </tr>
			</table>
		</td>
	</tr>
  	
	
	<tr> 
		<td height='2' valign='middle' align='center'><img src='../../../img/common/dot_grayline.gif' width='100%' height='1'></td>
	</tr>
  	
 	<tr> 
	    <td valign='middle' align='center'> 
			<table class="maintable">
	      	<tr> 
		       	<td width="397"> &nbsp;</td>
		       	<td width="372"> &nbsp;</td>
		    </tr>
       		<tr> 
				<td valign='bottom'><img src='../../../img/common/raonlogo_2line.gif' class="raonlogo2line"></td>
				<td><img src='../../../img/button/img_userpass_on.gif'></td>
	        </tr>
        	<tr> 
				<td><img src='../../../img/common/touchenlogo_2line.gif' class="touchenlogo2line"></td>
          		<td> 
            		<table>
              		<tr>
                		<td><img src="../../../img/dot_trans.gif" width="6" height="1"></td> 
                <td> 
                  <p><font size="2" color="#000000" face="µ¸¿ò">
                  	비밀 번호가 변경되었습니다.</font></p>
                  <p><font size="2" color="#666666" face="µ¸¿ò">
                  	확인 버튼을 선택하시면 첫 화면으로 이동합니다.</font></p>
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
                		<td height="22" colspan="2"><img src="../../../img/common/dot_trans.gif" width="1" height="50"> </td>
              		</tr>
              		<tr> 
                		<td width="6"><img src="../../../img/common/dot_trans.gif" width="6" height="1"></td>
                		<td width="368"><a href="index.jsp"><img name="imgbtn" class="imgbtn" src="../../../img/button/img_btnok_off.gif" width="58" ></a></td>
					</tr>
            		</table>
				</td>
			</tr>
			</table>
    	</td>
  	</tr>

<tr> 
   	<td height='2' valign='middle' align='center'> 
      	<table class="underline">
        <tr> 
			<td><img src='../../../img/common/dot_trans.gif' width='1' height='20'></td>
          	<td> 
            	<div align='right'><a href='http://www.raonsecure.com' target='_blank'><img src='../../../img/common/raon_url.gif' width='138' height='20' border='0'></a></div>
          	</td>
        </tr> 
      	</table>
   	</td>
</tr>
</table>
</body>
</html>

<% } else { %>
	<script language=javascript>
		alert("비밀번호 변경 오류가 발생했습니다. \n\n [오류코드=<%=nResult%>]");
		history.back(-1);
	</script>	
<% } %>	