package com.mococo.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mococo.microstrategy.sdk.esm.vo.MstrUser;

/**
 * HttpUtil
 * @author mococo
 *
 */
public class HttpUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	
	/**
	 * equleChar
	 */
	private static final char EQULE_CHAR = '~';
	
	
    /**
     * HttpUtil
     */
    public HttpUtil() {
    	logger.debug("HttpUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("HttpUtil");
    }
    
    
	/**
	 * getCurrentRequest
	 * @return
	 */
    public static HttpServletRequest getCurrentRequest() {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
        	request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return request;
    }
    
    
    /**
     * 접속 클라이언트의 IP 확인
     * @param request
     * @return
     */
    public static String getClientIP(final HttpServletRequest request) {
	    String rtnIp = request.getHeader("X-Forwarded-For");

	    if (rtnIp == null) {
	    	rtnIp = request.getHeader("Proxy-Client-IP");
	    }
	    if (rtnIp == null) {
	    	rtnIp = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (rtnIp == null) {
	    	rtnIp = request.getHeader("HTTP_CLIENT_IP");
	    }
	    if (rtnIp == null) {
	    	rtnIp = request.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    if (rtnIp == null) {
	    	rtnIp = request.getRemoteAddr();
	    }
	    
//	    logger.info("> Result : IP Address : "+ rtnIp);
	    return rtnIp;
    }
    
    
    /**
     * isJsonRequest
     * @param request
     * @return
     */
    public static boolean isJsonRequest(final HttpServletRequest request) {
        boolean bResult;
        
        if(request.getContentType() == null) {
        	bResult = false;
        } else {
        	bResult = request.getContentType().matches("application/json.*|application/javascript.*")
//            	|| "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    			|| "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        	
        }
            
        return bResult;
    }
    
    
	/**
	 * 유저 ID 추출
	 * @param request
	 * @return
	 */
	public static String getLoginUserId(final HttpServletRequest request) {
		String resultUserId = null;
//		String resultUserVo = null;
		/*
        */

		/*
		if (request != null && request.getSession() != null) {
			HttpSession session = request.getSession();
			resultUserId = (String)session.getAttribute("mstrUserIdAttr"); 
		}
		*/
		
		final MstrUser mstrUser = (MstrUser) request.getSession().getAttribute("mstr-user-vo");
        if (mstrUser != null) {
        	resultUserId = mstrUser.getId();
        }
		
        /*
        if(resultUserId == null || resultUserVo == null) {
        	resultUserId = null;
        }
        */
		
		return resultUserId;
	}
	
	
	/**
	 * 취약성 점검 대비 XSSFilter
	 * @param sInValid
	 * @return
	 */
	public static String replaceFilePath(final String sInValid) {
		String sValid = sInValid;
		
		if(sValid == null || "".equals(sValid)) {
			sValid = "";
		} else {
			sValid = sValid.replaceAll("\r", "");
			sValid = sValid.replaceAll("\n", "");
			sValid = sValid.replaceAll("/", "");
			sValid = sValid.replaceAll("\\\\", "");
			sValid = sValid.replaceAll("\\.", "");
			sValid = sValid.replaceAll("&", "");
		}
		
		return sValid;
	}
	
	
	/**
	 * 목적 : 다운로드 파일 이름을 가지고 옴
	 * @param oriFileName
	 * @param request
	 * @return
	 */
	public static String getDownloadFileName(final String oriFileName, final HttpServletRequest request) {
		final String agent = request.getHeader("User-Agent").replaceAll("[\r\n]","");
		final String fileName = StringUtils.defaultString(oriFileName).replaceAll("[\r\n]","");
		
		logger.debug("=> agent:[{}], fileName:[{}]", agent, fileName);
		
		String result = null;
		
		try {
			if (agent.indexOf("MSIE") > -1 || agent.indexOf("Trident") > -1) {
				result = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
				logger.debug("=> MSIE:[{}]", result);
			} else
			if (agent.indexOf("Chrome") > -1) {
				final StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < fileName.length(); i++) {
					final char charAt = fileName.charAt(i);
					if (charAt > EQULE_CHAR) {
						buffer.append(URLEncoder.encode(Character.toString(charAt), "utf-8"));
					} else {
						buffer.append(charAt);
					}
				}
				result = buffer.toString();
				logger.debug("=> Chrome:[{}]", result);
			} else
			if (agent.indexOf("Firefox") > -1 || agent.indexOf("Opera") > -1) {
				result = "\"" + new String(fileName.getBytes("utf-8"), "8859_1") + "\"";
				
				final String logTmp1 = result.replaceAll("[\r\n]","");
				logger.debug("=> Firefox, Opera:[{}]", logTmp1);
			} else {
				result = fileName;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("!!! error", e);
		}
		
		return result;
	}
	
	
	/**
	 * checkNull
	 * @param checkValue
	 * @return
	 */
	public static String checkNull(final String checkValue) {
		return checkValue.replaceAll("[\0]", "");
	}
}
