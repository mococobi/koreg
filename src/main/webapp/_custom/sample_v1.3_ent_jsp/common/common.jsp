<%
	// 브라우저 캐싱 방지
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	
	String context = request.getContextPath();
	String engineIP   = "10.0.4.122";
	int	   enginePort = 7000;
	
	// ssoapi 와 ssoengine 간의 인증을 위한 키 정보
	String sApiKey="368B184727E89AB69FAF";
%>