package com.custom.admin.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.custom.admin.service.AdminService;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.PortalCodeUtil;

/**
 * AdminController
 * @author mococo
 *
 */
@Controller
@RequestMapping("/admin/*")
public class AdminController {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    /**
     * adminService
     */
    /* default */ @Autowired /* default */ AdminService adminService;
    
    
    /**
     * AdminController
     */
    public AdminController() {
    	logger.debug("AdminController");
    }
    
    
    /**
     * 포탈 관리자 권한 체크
     * @param request
     * @return
     */
	private Boolean checkPortalAdmin(final HttpServletRequest request) {
    	Boolean rtnCheck = false;
    	
    	//포탈 관리자 권한 체크
		final List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
    	if(portalAuthList.contains(PortalCodeUtil.ptlSysAdmin)) {
    		rtnCheck = true;
    	}
    	
		return rtnCheck;
    }
    
    
    /**
     * 관리자 화면 이동
     * @return
     */
    @PostMapping("/admin/adminPage.do")
    public ModelAndView adminPage(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	
    	final String movePageName = params.get("page").toString();
    	final ModelAndView view = new ModelAndView("admin/boardList");
    	
    	//관리자 권한 체크
    	if(checkPortalAdmin(request)) {
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
    	} else {
    		view.setViewName(PortalCodeUtil.errAuth);
    	}
    	
        return view;
    }
    
    
    /**
     * 게시판 - 리스트 조회 - 그리드
     * @return
     */
    @PostMapping("/admin/boardListGrid.json")
    public Map<String, Object> boardListGrid(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	//관리자 권한 체크
    	if(checkPortalAdmin(request)) {
    		try {
        		final Map<String, Object> rtnList = adminService.boardList(request, response, params);
        		rtnMap.putAll(rtnList);
    		} catch (BadSqlGrammarException | SQLException e) {
    			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
    			logger.error("boardListGrid Exception", e);
    		}
    	} else {
    		rtnMap = ControllerUtil.getFailMap(PortalCodeUtil.portalError01);
    	}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 작성 화면 이동
     * @return
     */
    @GetMapping("/admin/boardWriteView.do")
    public ModelAndView boardWriteViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("admin/boardWrite");
        
    	//관리자 권한 체크
		if(!checkPortalAdmin(request)) {
			view.setViewName(PortalCodeUtil.errAuth);
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 작성 화면 이동
     * @return
     */
    @PostMapping("/admin/boardWriteView.do")
    public ModelAndView boardWriteViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("admin/boardWrite");
        
    	//관리자 권한 체크
		if(!checkPortalAdmin(request)) {
			view.setViewName("error/auth");
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 상세 화면 이동
     * @return
     */
    @GetMapping("/admin/boardDetailView.do")
    public ModelAndView boardDetailViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("admin/boardDetail");
        
    	//관리자 권한 체크
		if(!checkPortalAdmin(request)) {
			view.setViewName("error/auth");
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 상세 화면 이동
     * @return
     */
    @PostMapping("/admin/boardDetailView.do")
    public ModelAndView boardDetailViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        final ModelAndView view = new ModelAndView("admin/boardDetail");
        
    	//관리자 권한 체크
		if(!checkPortalAdmin(request)) {
			view.setViewName("error/auth");
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 상세 조회
     * @return
     */
    @PostMapping("/admin/boardDetail.json")
    @ResponseBody
    public Map<String, Object> boardDetail(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	//관리자 권한 체크
		if(checkPortalAdmin(request)) {
			try {
				final Map<String, Object> rtnList = adminService.boardDetail(request, response, params);
	    		rtnMap.putAll(rtnList);
			} catch (BadSqlGrammarException | SQLException e) {
				rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
				logger.error("boardDetail Exception", e);
			}
		} else {
			rtnMap = ControllerUtil.getFailMap(PortalCodeUtil.portalError01);
		}
		
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 등록
     * @param params
     * @param request
     * @return
     */
    @PostMapping("/admin/boardInsert.json")
	@ResponseBody
	public Map<String, Object> boardInsert(final MultipartHttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
		
    	//관리자 권한 체크
		if(checkPortalAdmin(request)) {
			try {
				final Map<String, Object> rtnList = adminService.boardInsert(request, response, params);
	    		rtnMap.putAll(rtnList);
			} catch (BadSqlGrammarException | SQLException e) {
				rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
				logger.error("boardInsert Exception", e);
			}
		} else {
			rtnMap = ControllerUtil.getFailMap(PortalCodeUtil.portalError01);
		}
		
		return rtnMap;
	}
    
    
    /**
     * 게시판 - 수정
     * @param params
     * @param request
     * @return
     */
    @PostMapping("/admin/boardUpdate.json")
	@ResponseBody
	public Map<String, Object> boardUpdate(final MultipartHttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
		
    	//관리자 권한 체크
		if(checkPortalAdmin(request)) {
			try {
				final Map<String, Object> rtnList = adminService.boardUpdate(request, response, params);
	    		rtnMap.putAll(rtnList);
			} catch (BadSqlGrammarException | SQLException e) {
				rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
				logger.error("boardUpdate Exception", e);
			}
		} else {
			rtnMap = ControllerUtil.getFailMap(PortalCodeUtil.portalError01);
		}
		
		return rtnMap;
	}
    
    
    /**
     * 로그 - 리스트 조회 - 그리드
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/admin/logListGrid.json")
    public Map<String, Object> logListGrid(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	//관리자 권한 체크
		if(checkPortalAdmin(request)) {
			try {
				final Map<String, Object> rtnList = adminService.logList(request, response, params);
	    		rtnMap.putAll(rtnList);
			} catch (BadSqlGrammarException | SQLException e) {
				rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
				logger.error("logListGrid Exception", e);
			}
		} else {
			rtnMap = ControllerUtil.getFailMap(PortalCodeUtil.portalError01);
		}
    	
    	return rtnMap;
    }
}
