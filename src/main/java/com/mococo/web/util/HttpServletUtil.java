package com.mococo.web.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * HttpServletUtil
 * @author mococo
 *
 */
public class HttpServletUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(HttpServletUtil.class);
	
	
    /**
     * HttpServletUtil
     */
    public HttpServletUtil() {
    	logger.debug("HttpServletUtil");
    }
    
	
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("HttpServletUtil");
    }
	
	
	/**
	 * HttpServletUtil
	 * @param request
	 * @param response
	 * @return
	 */
    public static boolean checkSession(final HttpServletRequest request, final HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {

    	final HttpSession session = request.getSession(false);
        final Object mstrUser = session.getAttribute("mstr-user-vo");
        
        if (mstrUser == null) {
            response.sendRedirect("/MicroStrategy/plugins/esm/jsp/sso.jsp?mstrUserId=demo");
        }
//		session.getAttributeNames().asIterator()
//        .forEachRemaining(name -> logger.info("session name={}, value={}"
//        , name, session.getAttribute(name)));
        return true;

    }
}
