package com.mococo.web.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mococo.microstrategy.sdk.esm.vo.MstrUser;

public class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    
    
    public static HttpServletRequest getCurrentRequest() {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return request;
    }
    
    
    /**
     * 접속 클라이언트의 IP 확인
     * 
     * @param request
     * @return
     */
    public static String getClientIP(final HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");

	    if (ip == null) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_CLIENT_IP");
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    if (ip == null) {
	        ip = request.getRemoteAddr();
	    }
	    
//	    LOGGER.info("> Result : IP Address : "+ ip);
	    return ip;
    }
    
    
    /**
     * 파라메터로 전달받은 파일의 스트림을 HttpServletResponse에 write
     * 
     * @param fileName
     * @param response
     * @return
     */
    public boolean writeResponseFileStream(String fileName, HttpServletResponse response) {
        boolean success = true;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        ServletOutputStream servletOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        // jquery-filedownload를 지원하기 위해 쿠키설정
        Cookie cookie = new Cookie("fileDownload", "true");
        cookie.setSecure(false);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        try {
            final File file = new File(fileName);

            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            response.reset();
            /* 전송방식이 'file'일 경우, browser 별 설정도 추가하여야 한다 */
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + FilenameUtils.getName(fileName));
            response.setHeader("Content-Length", Long.toString(file.length()));

            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "private");
            response.setHeader("Expires", "0");

            response.addCookie(cookie);

            byte[] buf = new byte[bufferedInputStream.available() + 1];

            servletOutputStream = response.getOutputStream();
            bufferedOutputStream = new BufferedOutputStream(servletOutputStream);

            int nReadSize = bufferedInputStream.read(buf);
            while (nReadSize != -1) {
                bufferedOutputStream.write(buf, 0, nReadSize);
                nReadSize = bufferedInputStream.read(buf);
            }

            bufferedOutputStream.flush();
            servletOutputStream.flush();
        } catch (FileNotFoundException e) {
        	LOGGER.error("!!! error", e);
            success = false;
        } catch (Exception e) {
        	LOGGER.error("!!! error", e);
            success = false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                	LOGGER.error("!!! error", e);
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (Exception e) {
                	LOGGER.error("!!! error", e);
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e) {
                	LOGGER.error("!!! error", e);
                }
            }
            if (servletOutputStream != null) {
                try {
                    servletOutputStream.close();
                } catch (Exception e) {
                	LOGGER.error("!!! error", e);
                }
            }
        }

        return success;
    }
    
    
    public static boolean isJsonRequest(HttpServletRequest request) {
        boolean bResult = false;
        try {
            bResult = request.getContentType().matches("application/json.*|application/javascript.*")
            || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        } catch (Exception e) {
            bResult = false;
            e.printStackTrace();
        }
        return bResult;
    }
    
    
	/**
	 * 유저 ID 추출
	 * @param request
	 * @return
	 */
	public static String getLoginUserId(HttpServletRequest request) {
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
		
		MstrUser mstrUser = (MstrUser) request.getSession().getAttribute("mstr-user-vo");
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
	public static String replaceFilePath(String sInValid) {
		String sValid = sInValid;
		
		if(sValid == null || sValid.equals("")) {
			return "";
		}
		
		sValid = sValid.replaceAll("\r", "");
		sValid = sValid.replaceAll("\n", "");
		sValid = sValid.replaceAll("/", "");
		sValid = sValid.replaceAll("\\\\", "");
		sValid = sValid.replaceAll("\\.", "");
		sValid = sValid.replaceAll("&", "");
		
		return sValid;
		
	}
	
	
	/**
	 * <pre>
	 * 목적 : 다운로드 파일 이름을 가지고 옴
	 * 매개변수 : 
	 * 	String oriFileName
	 * 	HttpServletRequest request
	 * 반환값 : java.lang.String
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static String getDownloadFileName(String oriFileName, HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		
		LOGGER.debug("=> agent:[{}], fileName:[{}]", agent, oriFileName);
		
		/* 2019-11-01, 소스코드취약점점검대응(D020) */
		String fileName = StringUtils.defaultString(oriFileName).replaceAll("\r", "").replaceAll("\n", "");
		
		String result = null;
		
		try {
			if (agent.indexOf("MSIE") > -1 || agent.indexOf("Trident") > -1) {
				result = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
				LOGGER.debug("=> MSIE:[{}]", result);
			} else
			if (agent.indexOf("Chrome") > -1) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < fileName.length(); i++) {
					char c = fileName.charAt(i);
					if (c > '~') {
						buffer.append(URLEncoder.encode(Character.toString(c), "utf-8"));
					} else {
						buffer.append(c);
					}
				}
				result = buffer.toString();
				LOGGER.debug("=> Chrome:[{}]", result);
			} else
			if (agent.indexOf("Firefox") > -1 || agent.indexOf("Opera") > -1) {
				result = "\"" + new String(fileName.getBytes("utf-8"), "8859_1") + "\"";
				LOGGER.debug("=> Firefox, Opera:[{}]", result);
			} else {
				result = fileName;
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("!!! error", e);
		}
		
		return result;
	}
}
