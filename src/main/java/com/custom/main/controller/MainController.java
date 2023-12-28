package com.custom.main.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.custom.board.service.BoardService;
import com.custom.main.service.MainService;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.CustomProperties;

@Controller
@RequestMapping("/main/*")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    
    @Autowired
    MainService mainService;
    
    @Autowired
    BoardService boardService;
    
    /**
     * 메인 화면 이동
     * @return
     */
    @RequestMapping(value = "/main/mainView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView mainView(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView view = new ModelAndView("error/auth");
    	
    	switch (CustomProperties.getProperty("portal.main.display")) {
			case "MSTR_DASHBOARD":
				view.setViewName("main/mainDashboard");
				view.addObject("portalMainDashboardId", CustomProperties.getProperty("portal.main.dashboard.id"));
				view.addObject("type", CustomProperties.getProperty("portal.main.dashboard.type"));
				view.addObject("isvi", true);
				break;
			case "MAIN":
				view.setViewName("main/main");
				Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
		        try {
		        	Map<String, Object> params = new HashMap<String, Object>();
		        	params.put("customOrder1", "BRD_ID");
		        	
//		    		Map<String, Object> boardMap = boardService.boardList(request, response, params);
//		    		view.addObject("communityData", boardMap);
				} catch (Exception e) {
					rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
					view.addObject("data", rtnMap);
					LOGGER.error("mainView Exception", e);
				}
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
    @RequestMapping(value = "/main/reportMainView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView reportMainView() {
        ModelAndView view = new ModelAndView("main/reportMain" + CustomProperties.getProperty("portal.application.file.name"));
        
        return view;
    }
    
    
    /**
     * EIS 메인 화면 이동
     * @return
     */
    @RequestMapping(value = "/main/mainEisView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView mainEisView() {
        ModelAndView view = new ModelAndView("main/mainEis");
        
        return view;
    }
    
    
    /**
     * 프롬프트 선택 화면
     * @return
     */
    @RequestMapping(value = "/main/selectPrompt.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView selectPrompt() {
        ModelAndView view = new ModelAndView("main/selectPrompt");
        
        return view;
    }
    
    
}
