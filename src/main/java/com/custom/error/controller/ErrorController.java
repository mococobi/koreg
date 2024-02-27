package com.custom.error.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * ErrorController
 * @author mococo
 *
 */
@Controller
@RequestMapping("/error/*")
public class ErrorController {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);
    
    
    /**
     * ErrorController
     */
    public ErrorController() {
    	logger.debug("ErrorController");
    }
    
    
    /**
     * 에러 코드 처리(404, 405, 500 ...)
     * @return
     */
    @GetMapping("/error/error{error_code}")
    public ModelAndView errorProcess(final HttpServletRequest request, @PathVariable final String error_code) {
		final ModelAndView view = new ModelAndView("error/500");
    	
    	switch (error_code) {
			case "404":
				view.setViewName("error/404");
				break;
			case "405":
				view.setViewName("error/405");
				break;
			case "500":
				view.setViewName("error/500");
				break;
			default:
				break;
		}
    	
    	final Map<String, Object> map = getErrorMsg(request);
        final Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
        Entry<String, Object> entry;
        
        while(iterator.hasNext()) {
            entry = iterator.next();
            view.addObject(entry.getKey(), entry.getValue());
            
            if (logger.isDebugEnabled()) {
            	logger.debug("key : [{}], value : [{}]", entry.getKey(), entry.getValue());
            }
        }
    	
        return view;
    }
    
    
    /**
     * getErrorMsg
     * @param request
     * @return
     */
    private Map<String, Object> getErrorMsg(final HttpServletRequest request) {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	//예외
    	if(request.getAttribute("javax.servlet.error.exception") != null) {
    		rtnMap.put("EXCEPTION", request.getAttribute("javax.servlet.error.exception"));
    	} else {
    		rtnMap.put("EXCEPTION", "");
    	}
    	
    	//예외 타입
    	if(request.getAttribute("javax.servlet.error.exception_type") != null) {
    		rtnMap.put("EXCEPTION_TYPE", request.getAttribute("javax.servlet.error.exception_type"));
    	} else {
    		rtnMap.put("EXCEPTION_TYPE", "");
    	}
    	
    	//오류 메시지
    	if(request.getAttribute("javax.servlet.error.message") != null) {
    		rtnMap.put("EXCEPTION_MESSAGE", request.getAttribute("javax.servlet.error.message"));
    	} else {
    		rtnMap.put("EXCEPTION_MESSAGE", "");
    	}
    	
    	//클라이언트 요청 URI
    	if(request.getAttribute("javax.servlet.error.request_uri") != null) {
    		rtnMap.put("REQUEST_URI", request.getAttribute("javax.servlet.error.request_uri"));
    	} else {
    		rtnMap.put("REQUEST_URI", "");
    	}
    	
    	//오류가 발생한 서블릿 이름
    	if(request.getAttribute("javax.servlet.error.servlet_name") != null) {
    		rtnMap.put("SERVLET_NAME", request.getAttribute("javax.servlet.error.servlet_name"));
    	} else {
    		rtnMap.put("SERVLET_NAME", "");
    	}
    	
    	//HTTP 상태 코드
    	if(request.getAttribute("javax.servlet.error.status_code") != null) {
    		rtnMap.put("STATUS_CODE", request.getAttribute("javax.servlet.error.status_code"));
    	} else {
    		rtnMap.put("STATUS_CODE", "");
    	}
    	
    	return rtnMap;
    }
    
    
    /**
     * 권한 에러 처리 - SSO 에러
     * @return
     */
    @GetMapping("/error/errorSsoAuth")
    public ModelAndView errorSsoAuth(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/authSso");
    }
    
    
    /**
     * 권한 에러 처리 - SSO 에러
     * @return
     */
    @PostMapping("/error/errorSsoAuth")
    public ModelAndView errorSsoAuthPost(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/authSso");
    }
    
    
    /**
     * 권한 에러 처리 - 어드민 접근
     * @return
     */
    @GetMapping("/error/errorAuth")
    public ModelAndView errorAuth(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/auth");
    }
    
    
    /**
     * 권한 에러 처리 - 어드민 접근
     * @return
     */
    @PostMapping("/error/errorAuth")
    public ModelAndView errorAuthPost(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/auth");
    }
    
    
    /**
     * 세션 만료 페이지 - 포탈
     * @return
     */
    @GetMapping("/error/noSessionView.do")
    public ModelAndView noSessionViewGet(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/noSession");
    }
    
    
    /**
     * 세션 만료 페이지 - 포탈
     * @return
     */
    @PostMapping("/error/noSessionView.do")
    public ModelAndView noSessionViewPost(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/noSession");
    }
    
    
    /**
     * 세션 만료 페이지 - EIS
     * @return
     */
    @GetMapping("/error/noSessionEisView.do")
    public ModelAndView noSessionEisViewGet(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/noSessionEis");
    }
    
    
    /**
     * 세션 만료 페이지 - EIS
     * @return
     */
    @PostMapping("/error/noSessionEisView.do")
    public ModelAndView noSessionEisViewPost(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        return new ModelAndView("error/noSessionEis");
    }
}
