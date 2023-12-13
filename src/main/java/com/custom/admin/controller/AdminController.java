package com.custom.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.custom.admin.service.AdminService;
import com.mococo.web.util.ControllerUtil;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    AdminService adminService;
    
    
    /**
     * 어드민 화면 이동
     * @return
     */
    @RequestMapping(value = "/admin/adminPage.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView adminPage(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
    	
    	String movePageName = params.get("page").toString();
    	ModelAndView view = new ModelAndView("admin/boardList");
    	
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		view.setViewName("error/auth");
	    		return view;
	    	}
    	}
    	
    	switch (movePageName) {
			case "BOARD_ADMIN":
				view.setViewName("admin/boardList");
				break;
			case "LOGIN_LOG_ADMIN":
				view.setViewName("admin/logList");
				view.addObject("LOG_TYPE", "LOGIN");
				view.addObject("LOG_TYPE_NM", "로그인");
				break;
			case "BOARD_LOG_ADMIN":
				view.setViewName("admin/logList");
				view.addObject("LOG_TYPE", "BOARD");
				view.addObject("LOG_TYPE_NM", "게시판");
				break;
			default:
				break;
		}
    	
        return view;
    }
    
    
    /**
     * 게시판 - 리스트 조회 - 그리드
     * @return
     */
    @RequestMapping(value = "/admin/boardListGrid.json", method = { RequestMethod.POST })
    public Map<String, Object> boardListGrid(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
	    		return rtnMap;
	    	}
    	}
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = adminService.boardList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 작성 화면 이동
     * @return
     */
    @RequestMapping(value = "/admin/boardWriteView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardWriteView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("admin/boardWrite");
        
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		view.setViewName("error/auth");
	    		return view;
	    	}
    	}
        
        return view;
    }
    
    
    /**
     * 게시판 - 상세 화면 이동
     * @return
     */
    @RequestMapping(value = "/admin/boardDetailView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardDetailView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("admin/boardDetail");
        
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		view.setViewName("error/auth");
	    		return view;
	    	}
    	}
        
        return view;
    }
    
    
    /**
     * 게시판 - 상세 조회
     * @return
     */
    @RequestMapping(value = "/admin/boardDetail.json", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> boardDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
	    		return rtnMap;
	    	}
    	}
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = adminService.boardDetail(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardDetail Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 등록
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/boardInsert.json", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> boardInsert(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
		
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
	    		return rtnMap;
	    	}
    	}
    	
		try {
			Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = adminService.boardInsert(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardInsert Exception", e);
		}
		
		return rtnMap;
	}
    
    
    /**
     * 게시판 - 수정
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/boardUpdate.json", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> boardUpdate(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
		
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
	    		return rtnMap;
	    	}
    	}
    	
		try {
			Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = adminService.boardUpdate(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardUpdate Exception", e);
		}
		
		return rtnMap;
	}
    
    
    /**
     * 로그 - 리스트 조회 - 그리드
     * @return
     */
    @RequestMapping(value = "/admin/logListGrid.json", method = { RequestMethod.POST })
    public Map<String, Object> logListGrid(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
	    		return rtnMap;
	    	}
    	}
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = adminService.logList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("logListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
}
