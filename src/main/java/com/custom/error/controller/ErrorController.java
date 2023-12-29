package com.custom.error.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error/*")
public class ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
    
    
    /**
     * 404 에러 처리
     * @return
     */
    @RequestMapping(value = "/error/error404", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView error404(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("error/404");
        
        return view;
    }
    
    
    /**
     * 500 에러 처리
     * @return
     */
    @RequestMapping(value = "/error/error500", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView error500(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("error/500");
        
        return view;
    }
    
    
    /**
     * 권한 에러 처리 - 어드민 접근
     * @return
     */
    @RequestMapping(value = "/error/errorAuth", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView errorAuth(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("error/auth");
        
        return view;
    }
    
    
    /**
     * 세션 만료 페이지
     * @return
     */
    @RequestMapping(value = "/error/noSessionView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView noSessionView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("error/noSession");
        
        return view;
    }
}
