package com.custom.board.service.impl;

import java.io.File;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.custom.board.service.BoardService;
import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.FileUtil;
import com.mococo.web.util.HttpUtil;

@Service(value = "boardService")
public class BoardServiceImpl implements BoardService {

    final Logger LOGGER = LoggerFactory.getLogger(BoardServiceImpl.class);
    
    @Autowired
    SimpleBizDao simpleBizDao;
    
    @Autowired
    LogService logService;
    
    
    //게시판 상세
    @Override
    public Map<String, Object> boardDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	rtnMap = simpleBizDao.select("Board.boardDetail", params);
        
        rtnMap.put("data", rtnMap);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    //게시판 - 게시물 목록 조회
    @Override
    public Map<String, Object> boardPostList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	//게시판
    	Map<String, Object> rtnBoardMap = simpleBizDao.select("Board.boardDetail", params);
    	
    	//게시물 - 리스트
    	params.put("countCheck", false);
        List<Map<String, Object>> rtnList = simpleBizDao.list("Board.boardPostList", params);
        
        //게시물 - 카운트
        params.put("countCheck", true);
    	Map<String, Object> rtnListCnt = simpleBizDao.select("Board.boardPostList", params);
    	 	
    	//첨부파일
    	if(rtnBoardMap.get("POST_FILE_YN").toString().equals("Y")) {
    		if (params.get("CHECK_POST_FILE") != null && (Boolean) params.get("CHECK_POST_FILE") == true) {
    			List<Object> postIdList = new ArrayList<Object>();
    			for (Map<String, Object> postItem : rtnList) {
    				postIdList.add(postItem.get("POST_ID"));
    			}
    			
    			params.put("postIdList", postIdList);
    			List<Map<String, Object>> postFileList = simpleBizDao.list("Board.boardPostFaqFileList", params);
    			
    			for (Map<String, Object> postItem : rtnList) {
    				List<Map<String, Object>> postFile = new ArrayList<Map<String,Object>>();
    				
    				for (Map<String, Object> postFileItem : postFileList) {
    					if(postItem.get("POST_ID").toString().equals(postFileItem.get("POST_ID").toString())) {
    						postFile.add(postFileItem);
    					}
    				}
    				postItem.put("attachfiles", postFile);
    			}
    		}
    	}

    	if(params.get("PORTAL_LOG") != null && (Boolean)params.get("PORTAL_LOG") == true) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, params.get("BRD_ID").toString(), "", "READ", params);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    //게시판 - 게시물 상세
    @Override
    public Map<String, Object> boardPostDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	if(params.get("PORTAL_LOG") != null && (Boolean)params.get("PORTAL_LOG") == true) {
	    	//포탈 로그 기록(상세 조회)
	    	logService.addPortalLog(request, params.get("BRD_ID").toString(), params.get("POST_ID").toString(), "DETAIL", params);
    	}
    	
    	//게시판
    	Map<String, Object> rtnBoardMap = simpleBizDao.select("Board.boardDetail", params);
    	
    	//게시물
    	Map<String, Object> rtnPostMap = simpleBizDao.select("Board.boardPostDetail", params);
    	
    	//첨부파일
    	if(rtnBoardMap.get("POST_FILE_YN").toString().equals("Y")) {
    		if(params.get("CHECK_POST_FILE") != null && (Boolean)params.get("CHECK_POST_FILE") == true) {
    			List<Map<String, Object>> rtnPostFileList = simpleBizDao.list("Board.boardPostFileList", params);
        		rtnMap.put("file", rtnPostFileList);
    		}
    	}
    	
    	//이전글, 다음글
    	if(params.get("CHECK_POST_LOCATION") != null && (Boolean)params.get("CHECK_POST_LOCATION") == true) {
    		List<Map<String, Object>> rtnPostLocationList = simpleBizDao.list("Board.boardPostBeforeNext", params);
    		rtnMap.put("location", rtnPostLocationList);
    	}
    	
        rtnMap.put("data", rtnPostMap);
        rtnMap.put("boardData", rtnBoardMap);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    //게시판 - 게시물 추가
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	if ("Y".equalsIgnoreCase((String) params.get("POPUP_YN")) ) {
    		params.put("POPUP_START_DT_TM", ((String) params.get("POPUP_START_DT_TM") + " 00:00:00.000") );
    		params.put("POPUP_END_DT_TM", ((String) params.get("POPUP_END_DT_TM") + " 23:59:59.999") );
    	} else {
    		params.put("POPUP_START_DT_TM", null);
    		params.put("POPUP_END_DT_TM", null);
    	}
    	
		//게시판 정보 입력
		int boardinsertCount = simpleBizDao.insert("Board.boardPostInsert", params);
		
		//첨부 파일 등록
		List<Map<String,Object>> lmRstUploadedFile = null;
		lmRstUploadedFile = uploadFile(request, params);
	    int totalFileInsertCount = 0;
	    int fileInsertCount = -1;
	    for (Map<String,Object> tmpFile : lmRstUploadedFile) {
	    	tmpFile.put("POST_ID", params.get("insertKey"));
	    	tmpFile.put("BRD_ID", params.get("BRD_ID"));
	    	tmpFile.put("userId", HttpUtil.getLoginUserId(request));
	    	
	    	fileInsertCount = simpleBizDao.insert("Board.boardPostFileInsert", tmpFile);
	    	LOGGER.debug("file Insert result : [{}], [{}]", fileInsertCount, (String) tmpFile.get("orgFileName"));
	    	
	    	totalFileInsertCount += fileInsertCount;
	    }
	    LOGGER.debug("total file Insert result : [{}]", fileInsertCount);
	    
	    rtnMap.put("BRD_ID", params.get("BRD_ID"));
		rtnMap.put("POST_ID", params.get("insertKey"));
		rtnMap.put("INSERT_POST_CNT", boardinsertCount);
		rtnMap.put("INSERT_FILE_CNT", totalFileInsertCount);
//		rtnMap.put("params", params);
	    
		Map<String, Object> logParams = new HashMap<String, Object>();
		
		logParams.put("BRD_ID", params.get("BRD_ID"));
		logParams.put("POST_ID", params.get("insertKey"));
		logParams.put("POST_TITLE", params.get("POST_TITLE"));
		logParams.put("CRT_USR_ID", params.get("userId"));
		logParams.put("INSERT_FILE_CNT", totalFileInsertCount);
		
		//포탈 로그 기록(게시물 + 파일 등록)
		logService.addPortalLog(request, params.get("BRD_ID").toString(), params.get("insertKey").toString(), "CREATE", logParams);
			
		return rtnMap;
    }
    
    
    //첨부파일 처리
    private List<Map<String, Object>> uploadFile(MultipartHttpServletRequest request, Map<String, Object> params) throws Exception {
		long atchFileId = -1;
		
		List<Map<String,Object>> lmRstUploadedFile = new ArrayList<Map<String,Object>>();
		Map<String,Object> mRstUploadedFile = null;
		
		for (Iterator<String> i = request.getFileNames(); i.hasNext(); ) {
			String fileId = i.next();
			MultipartFile file = request.getFile(fileId);
			
			mRstUploadedFile = new HashMap<String, Object>();
			if(fileId.indexOf("ATTACH_FILE") > -1) {
				mRstUploadedFile.put("fileType", "ATTACH_FILE");
			} else {
				mRstUploadedFile.put("fileType", "OTHER");
			}
			
			String orgFileName = URLDecoder.decode(FilenameUtils.getBaseName(file.getOriginalFilename()), "utf-8");
			
			//파일 확인
			if(!orgFileName.equals("")) {
				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
				String currentTime = timeFormat.format(new Date());
				String newFileName = currentTime + HttpUtil.replaceFilePath(RandomStringUtils.randomAlphanumeric(32))+ '.' + FilenameUtils.getExtension(file.getOriginalFilename());
				
				mRstUploadedFile.put("newFileName", newFileName);
				mRstUploadedFile.put("orgFileName", orgFileName);
				mRstUploadedFile.put("orgFileType", FilenameUtils.getExtension(file.getOriginalFilename()));
				mRstUploadedFile.put("atchFileId", atchFileId);
				mRstUploadedFile.put("fileSize", file.getSize());
				
				String filePath = CustomProperties.getProperty("attach.base.location");//\mococo\portal\
				String uploadFilePath = filePath + (String)params.get("BRD_ID") + "/";
				FileUtil.folderCheckAndCreate(uploadFilePath);
				
				mRstUploadedFile.put("uploadFilePath", uploadFilePath);
				
				uploadFilePath += newFileName;
				File uploadFile = new File(uploadFilePath);
				file.transferTo(uploadFile);
				
				/*
				SLBsUtil sUtil = new SLBsUtil();
				int encrypted = sUtil.isEncryptFile(uploadFilePath);
				
				if(encrypted == 1) {
					//암호화 파일 처리
					File rtnDecDrmFile = DrmUtil.decDrm(uploadFile);
					uploadFile.delete();
					FileUtils.moveFile(rtnDecDrmFile, new File(uploadFilePath.replace("tmp_", "")));
				} else if(encrypted == 0) {
					//일반파일 처리
					FileUtils.moveFile(uploadFile, new File(uploadFilePath.replace("tmp_", "")));
				}
				*/
				
			} else {
				mRstUploadedFile.put("newFileName", "");
				mRstUploadedFile.put("orgFileName", "");
				mRstUploadedFile.put("orgFileType", "");
			}
			
			lmRstUploadedFile.add(mRstUploadedFile);
		}
		
		return lmRstUploadedFile;
    }
    
    
    //게시판 - 게시물 수정
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostUpdate(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	if ("Y".equalsIgnoreCase((String) params.get("POPUP_YN")) ) {
    		params.put("POPUP_START_DT_TM", ((String) params.get("POPUP_START_DT_TM") + " 00:00:00.000") );
    		params.put("POPUP_END_DT_TM", ((String) params.get("POPUP_END_DT_TM") + " 23:59:59.999") );
    	} else {
    		params.put("POPUP_START_DT_TM", null);
    		params.put("POPUP_END_DT_TM", null);
    	}
    	
		//게시판 정보 수정
		int boardUpdateCount = simpleBizDao.update("Board.boardPostUpdate", params);
		
		//첨부 파일 등록
		List<Map<String,Object>> lmRstUploadedFile = null;
		lmRstUploadedFile = uploadFile(request, params);
	    int totalFileUpdateCount = 0;
	    int fileInsertCount = -1;
	    for (Map<String,Object> tmpFile : lmRstUploadedFile) {
	    	tmpFile.put("POST_ID", params.get("POST_ID"));
	    	tmpFile.put("BRD_ID", params.get("BRD_ID"));
	    	tmpFile.put("userId", HttpUtil.getLoginUserId(request));
	    	
	    	totalFileUpdateCount += simpleBizDao.update("Board.boardPostFileInsert", tmpFile);
	    	LOGGER.debug("file Update result : [{}], [{}]", fileInsertCount, (String) tmpFile.get("orgFileName"));
	    }
	    LOGGER.debug("total file Update result : [{}]", totalFileUpdateCount);
	    
	    //첨부 파일 삭제
	    int totalFileDeleteCount = 0;
	    String[] delete_file_ids = request.getParameterValues("deleteFileIds");
	    List<String> deleteFileList = new ArrayList<String>();
		if (delete_file_ids != null) {
			for (int i = 0; i < delete_file_ids.length; i++) {
				deleteFileList.add(delete_file_ids[i]);
	        }
			
			params.put("deleteFileList", deleteFileList);
			totalFileDeleteCount += simpleBizDao.update("Board.boardPostFileDelete", params);
		}
		LOGGER.debug("total file Delete result : [{}]", totalFileDeleteCount);
	    
		rtnMap.put("BRD_ID", params.get("BRD_ID"));
		rtnMap.put("POST_ID", params.get("POST_ID"));
		rtnMap.put("INSERT_POST_CNT", boardUpdateCount);
		rtnMap.put("INSERT_FILE_CNT", totalFileUpdateCount);
		rtnMap.put("DELETE_FILE_CNT", totalFileDeleteCount);
		rtnMap.put("params", params);
	    
		Map<String, Object> logParams = new HashMap<String, Object>();
		
		logParams.put("BRD_ID", params.get("BRD_ID"));
		logParams.put("POST_ID", params.get("POST_ID"));
		logParams.put("POST_TITLE", params.get("POST_TITLE"));
		logParams.put("MOD_USR_ID", params.get("userId"));
		logParams.put("INSERT_FILE_CNT", totalFileUpdateCount);
		logParams.put("DELETE_FILE_CNT", deleteFileList.size());
		
		//포탈 로그 기록(게시물 + 파일 등록)
		logService.addPortalLog(request, params.get("BRD_ID").toString(), params.get("POST_ID").toString(), "UPDATE", logParams);
			
		return rtnMap;
    }
    
    
    //게시판 - 게시물 삭제
    @Override
    public Map<String, Object> boardPostDelete(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	int postDeleteCnt = simpleBizDao.update("Board.boardPostDelete", params);
    	
    	//포탈 로그 기록(삭제)
    	logService.addPortalLog(request, params.get("BRD_ID").toString(), params.get("POST_ID").toString(), "DELETE", params);
    	
        rtnMap.put("data", postDeleteCnt);
        rtnMap.put("BRD_ID", params.get("BRD_ID"));
        rtnMap.put("POST_ID", params.get("POST_ID"));
        rtnMap.put("POST_DELETE_CNT", postDeleteCnt);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    //게시판 - 게시물 - 파일 상세
    @Override
    public Map<String, Object> boardPostFileDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	rtnMap = simpleBizDao.select("Board.boardPostFileDetail", params);
        
        return rtnMap;
    }


    //팝업 - 게시물 목록 조회
    @Override
    public Map<String, Object> boardPostPopupList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
        List<Map<String, Object>> rtnList = simpleBizDao.list("Board.boardPostPopupList", params);
    	
    	//첨부파일
    	if(params.get("CHECK_POST_FILE") != null && (Boolean)params.get("CHECK_POST_FILE") == true) {
    		List<Map<String, Object>> rtnPostFileList = simpleBizDao.list("Board.boardPostFileList", params);
    		rtnMap.put("file", rtnPostFileList);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
}