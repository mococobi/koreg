package com.custom.board.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.owasp.encoder.Encode;
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
import com.custom.board.service.BoardService;
import com.custom.log.service.LogService;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;
import com.mococo.web.util.PortalCodeUtil;

/**
 * 게시판 Controller
 * @author mococo
 *
 */
@Controller
@RequestMapping("/board/*")
public class BoardController {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
    
    /**
     * 상태
     * @author mococo
     *
     */
    private enum Status {FILE_OK, FILE_NOT_FOUND, EXCEPTION}
    
    /**
     * 게시판
     */
    /* default */ @Autowired /* default */ BoardService boardService;
    
    /**
     * 관리자
     */
    /* default */ @Autowired /* default */ AdminService adminService;
    
    /**
     * 로그
     */
    /* default */ @Autowired /* default */ LogService logService;
    
    
    /**
     * BoardServiceImpl
     */
    public BoardController() {
    	logger.debug("BoardController");
    }
    
    
    /**
     * 게시판 - 게시물 조회 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @GetMapping("/board/boardPostListView.do")
    public ModelAndView boardPostListViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("board/boardPostList");
        
        try {
        	final Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		view.addObject(PortalCodeUtil.boardData, boardMap);
		} catch (BadSqlGrammarException | SQLException e) {
			logger.error("boardPostListView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 게시물 조회 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostListView.do")
    public ModelAndView boardPostListViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("board/boardPostList");
        
        try {
    		final Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		view.addObject(PortalCodeUtil.boardData, boardMap);
		} catch (BadSqlGrammarException | SQLException e) {
			logger.error("boardPostListView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * 게시판 - 조회
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardInfo.json")
    @ResponseBody
    public Map<String, Object> boardInfo(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, false);
    	
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		rtnMap.putAll(boardMap);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 리스트 조회
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostList.json")
    @ResponseBody
    public Map<String, Object> boardPostList(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, false);
    	
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = boardService.boardPostList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 리스트 조회 - 그리드
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostListGrid.json")
    public Map<String, Object> boardPostListGrid(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, true);
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = boardService.boardPostList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * FAQ - 게시물 조회 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @GetMapping("/board/boardPostFaqListView.do")
    public ModelAndView boardPostFaqListViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("board/boardPostFaqList");
        
        try {
        	final Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		view.addObject(PortalCodeUtil.boardData, boardMap);
		} catch (BadSqlGrammarException | SQLException e) {
			logger.error("boardPostFaqListView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * FAQ - 게시물 조회 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostFaqListView.do")
    public ModelAndView boardPostFaqListViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView("board/boardPostFaqList");
        
        try {
        	final Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		view.addObject(PortalCodeUtil.boardData, boardMap);
		} catch (BadSqlGrammarException | SQLException e) {
			logger.error("boardPostFaqListView Exception", e);
		}
        
        return view;
    }
    
    
    /**
     * FAQ - 게시물 리스트 조회
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostFaqList.json")
    @ResponseBody
    public Map<String, Object> boardPostFaqList(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, true);
    	params.put(PortalCodeUtil.CHECK_POST_FILE, true);
    	
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = boardService.boardPostList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostFaqList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 작성 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @GetMapping("/board/boardPostWriteView.do")
    public ModelAndView boardPostWriteViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
        return new ModelAndView("board/boardPostWrite");
    }
    
    
    /**
     * 게시판 - 게시물 작성 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostWriteView.do")
    public ModelAndView boardPostWriteViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
        return new ModelAndView("board/boardPostWrite");
    }
    
    
    /**
     * 게시판 - 게시물 상세 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @GetMapping("/board/boardPostDetailView.do")
    public ModelAndView boardPostDetailViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
        return new ModelAndView("board/boardPostDetail");
    }
    
    
    /**
     * 게시판 - 게시물 상세 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostDetailView.do")
    public ModelAndView boardPostDetailViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
        return new ModelAndView("board/boardPostDetail");
    }
    
    
    /**
     * 게시판 - 게시물 상세 조회
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostDetail.json")
    @ResponseBody
    public Map<String, Object> boardPostDetail(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, true);
    	params.put("CHECK_POST_FILE", true);
    	params.put(PortalCodeUtil.CHECK_POST_LOC, true);
    	
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = boardService.boardPostDetail(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostDetail Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 등록
     * @param request
     * @param hrequest
     * @param response
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/board/boardPostInsert.json")
	public Map<String, Object> boardPostInsert(final MultipartHttpServletRequest request, final HttpServletRequest hrequest, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
		try {
    		//사용자 개인 권한 설정
    		Boolean boardPostInsert = false;
    		final Map<String, Object> boardMap = boardService.boardDetail(request, response, params);
    		final Map<String, Object> boardMapData = (Map<String, Object>) boardMap.get(PortalCodeUtil.data);
    		final String boardCreateAuth = boardMapData.get("BRD_CRT_AUTH").toString();
    		final String userId = "\"AUTH_ID\":\"" + HttpUtil.getLoginUserId(request) + "\"";
    		if(boardCreateAuth.indexOf(userId) > -1) {
    			boardPostInsert = true;
    		}
    		
    		//전체 유저 체크
    		final String allUserId = "\"AUTH_ID\":\"" + "ALL_USER" + "\"";
    		if(boardCreateAuth.indexOf(allUserId) > -1) {
    			boardPostInsert = true;
    		}
    		
	    	//관리자 권한 체크
    		final List<String> portalAuthList = adminService.getSessionPortalAuthList(hrequest);
	    	if(portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		boardPostInsert = true;
	    	}
	    	
	    	//최종 권한 판단
	    	if(boardPostInsert) {
	    		final Map<String, Object> rtnList = boardService.boardPostInsert(request, response, params);
	    		rtnMap.putAll(rtnList);
	    	} else {
	    		rtnMap = ControllerUtil.getFailMap("portal.board.insert.auth.error");
	    	}
		} catch (BadSqlGrammarException | SQLException | IOException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostInsert Exception", e);
		}
		
		return rtnMap;
	}
    
    
    /**
     * 게시판 - 게시물 수정
     * @param request
     * @param hrequest
     * @param response
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/board/boardPostUpdate.json")
	public Map<String, Object> boardPostUpdate(final MultipartHttpServletRequest mRequest, final HttpServletRequest hRequest, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
		try {
    		//기본 권한 설정
    		Boolean boardPostInsert = false;
    		
    		//작성자 권한 체크
    		final Map<String, Object> boardPostData = (Map<String, Object>)boardService.boardPostDetail(hRequest, response, params).get(PortalCodeUtil.data);
    		if(boardPostData.get("CRT_USR_ID").toString().equals(HttpUtil.getLoginUserId(mRequest))) {
    			boardPostInsert = true;
    		}
    		
	    	//관리자 권한 체크
    		final List<String> portalAuthList = adminService.getSessionPortalAuthList(hRequest);
	    	if(portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		boardPostInsert = true;
	    	}
	    	
	    	//최종 권한 판단
	    	if(boardPostInsert) {
	    		final Map<String, Object> rtnList = boardService.boardPostUpdate(mRequest, response, params);
	    		rtnMap.putAll(rtnList);
	    	} else {
	    		rtnMap = ControllerUtil.getFailMap("portal.board.update.auth.error");
	    	}
		} catch (BadSqlGrammarException | SQLException | IOException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostUpdate Exception", e);
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
    @SuppressWarnings("unchecked")
    @PostMapping("/board/boardPostDelete.json")
	public Map<String, Object> boardPostDelete(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
		try {
    		//기본 권한 설정
    		Boolean boardPostInsert = false;
	    	
    		//작성자 권한 체크
    		final Map<String, Object> boardPostData = (Map<String, Object>)boardService.boardPostDetail(request, response, params).get(PortalCodeUtil.data);
    		if(boardPostData.get("CRT_USR_ID").toString().equals(HttpUtil.getLoginUserId(request))) {
    			boardPostInsert = true;
    		}
    		
	    	//관리자 권한 체크
    		final List<String> portalAuthList = adminService.getSessionPortalAuthList(request);
	    	if(portalAuthList.contains("PORTAL_SYSTEM_ADMIN")) {
	    		boardPostInsert = true;
	    	}
	    	
	    	//최종 권한 판단
	    	if(boardPostInsert) {
	    		final Map<String, Object> rtnList = boardService.boardPostDelete(request, response, params);
	    		rtnMap.putAll(rtnList);
	    	} else {
	    		rtnMap = ControllerUtil.getFailMap("portal.board.delete.auth.error");
	    	}
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostDelete Exception", e);
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
    @PostMapping("/board/downloadAttachFile.do")
	public void download(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
		Status state = Status.FILE_OK;
		String errorMsg = "";
		
		final String boardId = params.get("BRD_ID").toString();
		final String postId = params.get("POST_ID").toString();
		
		Map<String, Object> fileMap = null;
		
		try {
			fileMap = boardService.boardPostFileDetail(request, response, params);
		}  catch (BadSqlGrammarException | SQLException e) {
			logger.error("download Exception", e);
			errorMsg = e.getMessage();
			state = Status.EXCEPTION;
		}
		
		if (state == Status.FILE_OK) {
			final String serverFileName = fileMap.get("SRV_FILE_NM").toString();
			final String orgFileName = fileMap.get("ORG_FILE_NM").toString();
			final String fileExtension = "." + fileMap.get("FILE_EXT").toString();
			
			final String downFilePath = CustomProperties.getProperty("attach.base.location") + fileMap.get("BRD_ID").toString() + "/";
			final String downFileName = HttpUtil.getDownloadFileName(orgFileName, request) + fileExtension;
			
			final File rtnEncFile = new File(downFilePath, FilenameUtils.getName(serverFileName));
			
			try (InputStream fileInputStream = Files.newInputStream(Paths.get(downFilePath + FilenameUtils.getName(serverFileName)));
				BufferedInputStream buffInStream = new BufferedInputStream(fileInputStream);){
				/* 전송방식이 'file'일 경우, browser 별 설정도 추가하여야 한다 */
				response.reset();
				response.setHeader("Content-type", "application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=" + downFileName);
				response.setHeader("Content-Length", Long.toString(rtnEncFile.length()));
				
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "private");
				response.setHeader("Expires", "0");
				
				try (ServletOutputStream svlOutStream = response.getOutputStream();
					BufferedOutputStream buffOutStream = new BufferedOutputStream(svlOutStream);){
					final byte[] buf = new byte[1024];
					int nReadSize = buffInStream.read(buf);
					while (nReadSize != -1) {
						buffOutStream.write(buf, 0, nReadSize);
						nReadSize = buffInStream.read(buf);				
					}
					
					buffOutStream.flush();
					svlOutStream.flush();
					
					//포탈 로그 기록(게시물 + 파일 등록)
					logService.addPortalLog(request, boardId, postId, "DOWNLOAD", params);
				}  catch (IOException | BadSqlGrammarException | SQLException e) {
					logger.error("downloadAttachFile Exception", e);
					errorMsg = e.getMessage();
					state = Status.EXCEPTION;
				}
				
			} catch (IOException e) {
				logger.error("download Exception", e);
				errorMsg = e.getMessage();
				state = Status.EXCEPTION;
			}
		}
		
		checkErrorMsg(response, state, errorMsg);
	}
    
    
    /**
     * 에러 메시지 출력
     * @param response
     * @param state
     * @param errorMsg
     */
    private void checkErrorMsg(final HttpServletResponse response, final Status state, final String errorMsg) {
    	if (state != Status.FILE_OK) {
			final String logTmp1 = state.toString().replaceAll("[\r\n]","");
			logger.debug("!!! download [{}]", logTmp1);
			
			if(response != null) {
				response.reset();
				response.setHeader("Content-type", "text/html;charset=UTF-8");
				response.setHeader("Content-Transfer-Encoding", "chunked");
				
				final StringBuffer BUF = new StringBuffer(300);
				BUF.append(errorMsg);
				
				try (PrintWriter WRITER = response.getWriter();) {
					if(WRITER != null) {
						WRITER.print("<script type='text/javascript'>alert('" + Encode.forHtml(BUF.toString()) + "')</script>");
					}
				} catch (IOException e) {
					logger.error("WRITER Exception", e);
				} 
			}
		}
    }
	
	
	/**
	 * 팝업 - 게시물 리스트 조회
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
    @PostMapping("/board/boardPostPopupList.json")
    @ResponseBody
    public Map<String, Object> boardPostListPopup(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, true);
    	
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = boardService.boardPostPopupList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostPopupList Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 팝업 - 게시물 상세 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @GetMapping("/board/boardPostPopupView.do")
    public ModelAndView boardPostPopupViewGet(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
        return new ModelAndView("board/boardPostPopup");
    }
    
    
    /**
     * 팝업 - 게시물 상세 화면 이동
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostPopupView.do")
    public ModelAndView boardPostPopupViewPost(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
        return new ModelAndView("board/boardPostPopup");
    }
    
    
    /**
     * 팝업 - 팝업 게시글 조회
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/board/boardPostPopupDetail.json")
    @ResponseBody
    public Map<String, Object> boardPostPopupDetail(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put(PortalCodeUtil.PORTAL_LOG, true);
    	params.put("CHECK_POST_FILE", true);
    	params.put(PortalCodeUtil.CHECK_POST_LOC, false);
    	
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = boardService.boardPostDetail(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("boardPostPopupDetail Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
}