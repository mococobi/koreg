package com.custom.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.custom.board.service.BoardService;
import com.mococo.web.util.CustomProperties;

/**
 * MainController
 * @author mococo
 *
 */
@Controller
@RequestMapping("/main/*")
public class MainController {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    /**
     * boardService
     */
	/* default */ @Autowired /* default */ BoardService boardService;
    
    
    /**
     * MainController
     */
    public MainController() {
    	logger.debug("MainController");
    }
    
    
    /**
     * 메인 화면 이동
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/main/mainView.do")
    public ModelAndView mainViewGet(HttpServletRequest request, HttpServletResponse response) {
    	final ModelAndView view = new ModelAndView("error/auth");
    	
    	switch (CustomProperties.getProperty("portal.main.display")) {
			case "MSTR_DASHBOARD":
				view.setViewName("main/mainDashboard");
				view.addObject("portalMainDashboardId", CustomProperties.getProperty("portal.main.dashboard.id"));
				view.addObject("type", CustomProperties.getProperty("portal.main.dashboard.type"));
				view.addObject("isvi", true);
				break;
			case "MAIN":
				view.setViewName("main/main");
				break;
			default:
				break;
		}
        
        return view;
    }
    
    
    /**
     * 메인 화면 이동
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/main/mainView.do")
    public ModelAndView mainViewPost(HttpServletRequest request, HttpServletResponse response) {
    	final ModelAndView view = new ModelAndView("error/auth");
    	
    	switch (CustomProperties.getProperty("portal.main.display")) {
			case "MSTR_DASHBOARD":
				view.setViewName("main/mainDashboard");
				view.addObject("portalMainDashboardId", CustomProperties.getProperty("portal.main.dashboard.id"));
				view.addObject("type", CustomProperties.getProperty("portal.main.dashboard.type"));
				view.addObject("isvi", true);
				break;
			case "MAIN":
				view.setViewName("main/main");
				break;
			default:
				break;
		}
        
        return view;
    }
    
    
    /**
     * 리포트 메인 화면 이동
     * @return
     */
    @GetMapping("/main/reportMainView.do")
    public ModelAndView reportMainViewGet() {
    	final ModelAndView view = new ModelAndView("main/reportMain");
    	
    	switch (CustomProperties.getProperty("portal.application.file.name")) {
			case "Gcgf":
				view.setViewName("main/reportMainGcgf");
				break;
			case "Koreg":
				view.setViewName("main/reportMainKoreg");
				break;
			default:
				break;
		}
        
        return view;
    }
    
    
    /**
     * 리포트 메인 화면 이동
     * @return
     */
    @PostMapping("/main/reportMainView.do")
    public ModelAndView reportMainViewPost() {
    	final ModelAndView view = new ModelAndView("main/reportMain");
    	
    	switch (CustomProperties.getProperty("portal.application.file.name")) {
			case "Gcgf":
				view.setViewName("main/reportMainGcgf");
				break;
			case "Koreg":
				view.setViewName("main/reportMainKoreg");
				break;
			default:
				break;
		}
        
        return view;
    }
    
    
    /**
     * EIS 메인 화면 이동
     * @return
     */
    @GetMapping("/main/mainEisView.do")
    public ModelAndView mainEisViewGet() {
        return new ModelAndView("main/mainEis");
    }
    
    
    /**
     * EIS 메인 화면 이동
     * @return
     */
    @PostMapping("/main/mainEisView.do")
    public ModelAndView mainEisViewPost() {
        return new ModelAndView("main/mainEis");
    }
    
    
    /**
     * 프롬프트 선택 화면
     * @return
     */
    @PostMapping("/main/selectPrompt.do")
    public ModelAndView selectPrompt() {
        return new ModelAndView("main/selectPrompt");
    }
    
    
}
