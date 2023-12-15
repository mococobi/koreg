package com.custom.board.controller;

import java.net.FileNameMap;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.custom.admin.service.AdminService;
import com.custom.board.service.BoardService;
import com.microstrategy.web.app.tags.Log;
import com.mococo.web.util.ControllerUtil;

@Controller
@RequestMapping("/board/*")
public class BoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardController.class);
    
    @Autowired
    BoardService boardService;
    @Autowired
    AdminService adminService;
    
    /**
     * 게시판 - 게시물 조회 화면 이동
     * @return
     */
    @RequestMapping(value = "/board/boardPostListView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardPostListView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("board/boardPostList");
        
        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
        try {
    		Map<String, Object> boardMap = boardService.boardList(request, response, params);
    		view.addObject("postData", boardMap);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			view.addObject("data", rtnMap);
			LOGGER.error("boardPostListView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 게시물 리스트 조회
     * @return
     */
    @RequestMapping(value = "/board/boardPostList.json", method = { RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> boardPostList(HttpServletRequest request, HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 리스트 조회 - 그리드
     * @return
     */
    @RequestMapping(value = "/board/boardPostListGrid.json", method = { RequestMethod.POST })
    public Map<String, Object> boardPostListGrid(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", true);
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 작성 화면 이동
     * @return
     */
    @RequestMapping(value = "/board/boardPostWriteView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardPostWriteView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("board/boardPostWrite");
        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
        
        try {
    		Map<String, Object> boardMap = boardService.boardList(request, response, params);
    		view.addObject("postData", boardMap);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			view.addObject("data", rtnMap);
			LOGGER.error("boardPostWriteView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 게시물 상세 화면 이동
     * @return
     */
    @RequestMapping(value = "/board/boardPostDetailView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardPostDetailView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);//값을 불러오는게 아닌 화면 이동에선 로그처리 필요X
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("board/boardPostDetail");
        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
        
        try {
    		//Map<String, Object> boardMap = boardService.boardList(request, response, params);
        	Map<String, Object> boardMap = boardService.boardPostDetail(request, response, params);
    		view.addObject("postData", boardMap);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			view.addObject("data", rtnMap);
			LOGGER.error("boardPostWriteView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 게시물 상세 조회
     * @return
     */
    @RequestMapping(value = "/board/boardPostDetail.json", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> boardPostDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put("PORTAL_LOG", true);
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostDetail(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostDetail Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 등록
     * @param params
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/board/boardPostInsert.json", method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletRequest hrequest, HttpServletResponse response, @RequestParam Map<String, Object> params) throws Exception {
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(hrequest);
	    	
	    	LOGGER.debug("AUTHLIST : [{}]", portalAuthList);
	    	
	    	if(params.get("BRD_ID").equals("1")) {
		    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
		    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
		    		return rtnMap;
	    		}
	    	}
    	}    	
    	
		try {
			Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostInsert(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostDetail Exception", e);
		}
		
		return rtnMap;
	}
    
    
    @RequestMapping(value = "/board/boardPostUpdate.json", method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> boardPostUpdate(MultipartHttpServletRequest request, HttpServletRequest hrequest, HttpServletResponse response, @RequestParam Map<String, Object> params) throws Exception {
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	/*
    	{
    		//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(hrequest);
	    	
	    	LOGGER.debug("AUTHLIST : [{}]", portalAuthList);
	    	
	    	if(params.get("BRD_ID").equals("1")) {
		    	if(!portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
		    		rtnMap = ControllerUtil.getFailMap("portal.admin.auth.error");
		    		return rtnMap;
	    		}
	    	}
    	}
    	*/
    	
		try {
			Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostUpdate(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostDetail Exception", e);
		}
		
		return rtnMap;
	}
    
}
