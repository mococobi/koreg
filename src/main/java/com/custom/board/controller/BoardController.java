package com.custom.board.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
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
import com.custom.board.service.BoardService;
import com.custom.log.service.LogService;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;

@Controller
@RequestMapping("/board/*")
public class BoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardController.class);
    
    private enum Status {OK, FILE_NOT_FOUND, EXCEPTION};
    
    @Autowired
    BoardService boardService;
    
    @Autowired
    AdminService adminService;
    
    @Autowired
    LogService logService;
    
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
    		Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
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
    		Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
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
    	params.put("CHECK_POST_FILE", true);
    	params.put("CHECK_POST_LOCATION", true);
    	
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
     * 게시판 - 게시물 등록
     * @param params
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/board/boardPostInsert.json", method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletRequest hrequest, HttpServletResponse response, @RequestParam Map<String, Object> params) {
//    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	{
    		//기본 권한 설정
    		Boolean boardPostInsertCheck = true;
	    	if(params.get("BRD_ID").equals("1")) {
	    		boardPostInsertCheck = false;
	    	} else {
	    		boardPostInsertCheck = true;
	    	}
	    	
	    	//관리자 권한 체크
	    	List<String> portalAuthList = adminService.getSessionPortalAuthList(hrequest);
	    	if(portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		boardPostInsertCheck = true;
	    	}
	    	
	    	//최종 권한 판단
	    	if(!boardPostInsertCheck) {
	    		rtnMap = ControllerUtil.getFailMap("portal.board.insert.auth.error");
	    		return rtnMap;
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
    
    
    /**
     * 게시판 - 게시물 리스트 수정
     * @param request
     * @param hrequest
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/board/boardPostUpdate.json", method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> boardPostUpdate(MultipartHttpServletRequest request, HttpServletRequest hrequest, HttpServletResponse response, @RequestParam Map<String, Object> params) {
//    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
		try {
			{
	    		//기본 권한 설정
	    		Boolean boardPostInsertCheck = false;
	    		
	    		//작성자 권한 체크
	    		Map<String, Object> boardPostCheckData = (Map<String, Object>)boardService.boardPostDetail(hrequest, response, params).get("data");
	    		if(boardPostCheckData.get("CRT_USR_ID").toString().equals(HttpUtil.getLoginUserId(request))) {
	    			boardPostInsertCheck = true;
	    		}
	    		
		    	//관리자 권한 체크
		    	List<String> portalAuthList = adminService.getSessionPortalAuthList(hrequest);
		    	if(portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
		    		boardPostInsertCheck = true;
		    	}
		    	
		    	//최종 권한 판단
		    	if(!boardPostInsertCheck) {
		    		rtnMap = ControllerUtil.getFailMap("portal.board.update.auth.error");
		    		return rtnMap;
		    	}
	    	}
			
			Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostUpdate(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostDetail Exception", e);
		}
		
		return rtnMap;
	}
    
    
	/**
	 * 게시판 - 게시물 삭제
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
    @RequestMapping(value = "/board/boardPostDelete.json", method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> boardPostDelete(HttpServletRequest request, HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
		try {
	    	{
	    		//기본 권한 설정
	    		Boolean boardPostInsertCheck = false;
		    	
	    		//작성자 권한 체크
	    		Map<String, Object> boardPostCheckData = (Map<String, Object>)boardService.boardPostDetail(request, response, params).get("data");
	    		if(boardPostCheckData.get("CRT_USR_ID").toString().equals(HttpUtil.getLoginUserId(request))) {
	    			boardPostInsertCheck = true;
	    		}
	    		
		    	//관리자 권한 체크
		    	List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
		    	if(portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
		    		boardPostInsertCheck = true;
		    	}
		    	
		    	//최종 권한 판단
		    	if(!boardPostInsertCheck) {
		    		rtnMap = ControllerUtil.getFailMap("portal.board.delete.auth.error");
		    		return rtnMap;
		    	}
	    	}
			
			Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostDelete(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostDetail Exception", e);
		}
		
		return rtnMap;
	}
    
    
	/**
	 * 첨부파일 다운로드
	 * @param request
	 * @param response
	 * @param params
	 * @throws IOException
	 */
	@RequestMapping(value = "/board/downloadAttachFile.do", method = {RequestMethod.GET, RequestMethod.POST})
	public void download(final HttpServletRequest request, final HttpServletResponse response, @RequestParam Map<String, Object> params) throws IOException {
		Status state = Status.OK;
		String errorMsg = "";

		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;
		ServletOutputStream servletOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		File rtnEncDrmFile = null;
		
		try {
			String boardId = HttpUtil.replaceFilePath(request.getParameter("BRD_ID"));
			String postId = HttpUtil.replaceFilePath(request.getParameter("POST_ID"));
//			String fileId = HttpUtil.replaceFilePath(request.getParameter("FILE_ID"));
			
			Map<String, Object> fileMap = boardService.boardPostFileDetail(request, response, params);
			
			String serverFileName = fileMap.get("SRV_FILE_NM").toString();
			String orgFileName = fileMap.get("ORG_FILE_NM").toString();
			String fileExtension = "." + fileMap.get("FILE_EXT").toString();
			
			String downFilePath = CustomProperties.getProperty("attach.base.location") + boardId + "/";
			String downFileName = HttpUtil.getDownloadFileName(orgFileName, request) + fileExtension;
			
			//DRM 영역
			/*
			SLDsFile sFile = new SLDsFile();
			SLBsUtil sUtil = new SLBsUtil();
			
			int isSupportFile = sFile.DSIsSupportFile(downFilePath + downFileName);
			int isEncrypted = sUtil.isEncryptFile(downFilePath + downFileName);
			
			System.out.println("지원가능 여부 : " + isSupportFile);	//0 : 지원 X, 	1 : 지원되는
			System.out.println("암호화 여부 : " + isEncrypted);		//0 : 일반 파일, 	1 : 암호화파일
			
			rtnEncDrmFile = DrmUtil.encDrm(downFilePath, downFileName);
			*/
			
			rtnEncDrmFile = new File(downFilePath + serverFileName);
			fileInputStream = new FileInputStream(rtnEncDrmFile);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			
			response.reset();
			
			/* 전송방식이 'file'일 경우, browser 별 설정도 추가하여야 한다 */
		    response.setHeader("Content-type", "application/octet-stream");
		    response.setHeader("Content-Disposition", "attachment; filename=" + downFileName);
		    response.setHeader("Content-Length", Long.toString(rtnEncDrmFile.length()) );

		    response.setHeader("Content-Transfer-Encoding", "binary");
		    response.setHeader("Pragma", "no-cache");
		    response.setHeader("Cache-Control", "private");
		    response.setHeader("Expires", "0");

			int nReadSize = 0;
			final int BUFFER_CAPACITY = 1024;
			
			byte[] buf = null;
			buf = new byte[BUFFER_CAPACITY];
			
			servletOutputStream = response.getOutputStream();
			bufferedOutputStream = new BufferedOutputStream(servletOutputStream);

			nReadSize = bufferedInputStream.read(buf);
			while (nReadSize != -1) {
				bufferedOutputStream.write(buf, 0, nReadSize);
				nReadSize = bufferedInputStream.read(buf);				
			}
			
			bufferedOutputStream.flush();
			servletOutputStream.flush();
			
			/*
			//포탈 이력
			if(bulletinId.equals("99999")) {
				params.put("historyAction", "download_bdp");
			} else {
				params.put("historyAction", "download1");
			}
			bulletinService.insertProtalHistory(params);
			*/
			
			//포탈 로그 기록(게시물 + 파일 등록)
			logService.addPortalLog(request, boardId, postId, "DOWNLOAD", params);
			
		} catch (FileNotFoundException e) {
			LOGGER.error("error", e);
			
			errorMsg = e.getMessage();
			state = Status.FILE_NOT_FOUND;
		} catch (Exception e) {
			LOGGER.error("error", e);
			
			errorMsg = e.getMessage();
			state = Status.EXCEPTION;
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					LOGGER.error("!!! error", e);
				}
			}
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					LOGGER.error("!!! error", e);
				}
			}
			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					LOGGER.error("!!! error", e);
				}
			}
			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					LOGGER.error("!!! error", e);
				}
			}
			if (servletOutputStream != null) {
				try {
					servletOutputStream.close();
				} catch (IOException e) {
					LOGGER.error("!!! error", e);
				}
			}
			
			/*
			if(rtnEncDrmFile != null) {
				boolean isDeleteFile = rtnEncDrmFile.delete();
				LOGGER.debug("DRM Finish. temp isDeleteFile [{}]", isDeleteFile);
			}
			*/
		}
		
		if (state != Status.OK) {
			LOGGER.debug("!!! 오류가 발생하였습니다.");
			
			if(response != null) {
				response.reset();
				response.setHeader("Content-type", "text/html;charset=UTF-8");
				response.setHeader("Content-Transfer-Encoding", "chunked");
				
				final StringBuffer BUF = new StringBuffer(100);
				BUF.append("<script type='text/javascript'>alert('" + errorMsg + "')</script>");
				
				final PrintWriter WRITER = response.getWriter();
				if(WRITER != null) {
					WRITER.print(BUF.toString());
				}
			}
		}
	}

	/**
     * 팝업 - 게시물 리스트 조회
     * @return
     */
    @RequestMapping(value = "/board/boardPostPopupList.json", method = {RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> boardPostListPopup(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", true);
    	params.put("CHECK_POST_FILE", true);
    	
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = boardService.boardPostPopupList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("boardPostList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 팝업 - 게시물 상세 화면 이동
     * @return
     */
    @RequestMapping(value = "/board/boardPostPopupView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardPostPopupView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);//값을 불러오는게 아닌 화면 이동에선 로그처리 필요X
    	params.put("CHECK_POST_LOCATION", false);//값을 불러오는게 아닌 화면 이동에선 로그처리 필요X
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("board/boardPostPopup");
        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
        
        try {
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
     * 팝업 - 팝업 게시글 조회
     * @return
     */
    @RequestMapping(value = "/board/boardPostPopupDetail.json", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> boardPostPopupDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put("PORTAL_LOG", true);
    	params.put("CHECK_POST_FILE", true);
    	params.put("CHECK_POST_LOCATION", false);
    	
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
     * FAQ - 게시물 조회 화면 이동
     * @return
     */
    @RequestMapping(value = "/board/boardPostFaqListView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView boardPostFaqListView(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        ModelAndView view = new ModelAndView("board/boardPostFaqList");
        
        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
        try {
    		Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		view.addObject("postData", boardMap);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			view.addObject("data", rtnMap);
			LOGGER.error("boardPostListView Exception", e);
		}
        
        return view;
    }
    
}