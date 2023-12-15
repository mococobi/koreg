package com.custom.board.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.custom.board.service.BoardService;
import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;

@Service(value = "boardService")
public class BoardServiceImpl implements BoardService {

    final Logger LOGGER = LoggerFactory.getLogger(BoardServiceImpl.class);
    
    @Autowired
    SimpleBizDao simpleBizDao;
    
    @Autowired
    LogService logService;
 
    @Override
	public List<String> getSessionPortalAuthList(MultipartHttpServletRequest request) {
		List<String> rtnList = (List<String>) request.getSession().getAttribute("PORTAL_AUTH");
		return null;
	}
    
    @Override
    public Map<String, Object> boardList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	rtnMap = simpleBizDao.select("Board.boardList", params);
        
        rtnMap.put("data", rtnMap);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> boardPostList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
        List<Map<String, Object>> rtnList = simpleBizDao.list("Board.boardPostList", params);
        
        params.put("countCheck", true);
    	Map<String, Object> rtnListCnt = simpleBizDao.select("Board.boardPostList", params);
    	
    	if((Boolean)params.get("PORTAL_LOG") == true) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, params.get("boardId").toString(), "", "READ", params);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> boardPostDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	Map<String, Object> rtnPostMap = simpleBizDao.select("Board.boardPostDetail", params);
    	List<Map<String, Object>> rtnPostFileList = simpleBizDao.list("Board.boardPostFileList", params);
    	
    	if((Boolean)params.get("PORTAL_LOG") == true) {
    	//포탈 로그 기록(상세 조회)
    	logService.addPortalLog(request, params.get("boardId").toString(), params.get("postId").toString(), "DETAIL", params);
    	}
    	
        rtnMap.put("data", rtnPostMap);
        rtnMap.put("file", rtnPostFileList);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	if ( "Y".equalsIgnoreCase((String) params.get("POPUP_YN")) ) {
    		params.put("POPUP_START_DT_TM", ((String) params.get("POPUP_START_DT_TM") + " 00:00:00.000") );
    		params.put("POPUP_END_DT_TM", ((String) params.get("POPUP_END_DT_TM") + " 23:59:59.999") );
    	} else {
    		params.put("POPUP_START_DT_TM", null);
    		params.put("POPUP_END_DT_TM", null);
    	}
    	
    	
		//게시판 정보 입력
		int boardinsertCount = simpleBizDao.insert("Board.boardPostInsert", params);
	
		List<Map<String,Object>> lmRstUploadedFile = null;
	    //params = uploadFile(request, params);
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
	    
		rtnMap.put("POST_ID", params.get("insertKey"));
		rtnMap.put("INSERT_POST_CNT", boardinsertCount);
		rtnMap.put("INSERT_FILE_CNT", totalFileInsertCount);
		rtnMap.put("BRD_ID", params.get("BRD_ID"));
		rtnMap.put("params", params);
	    
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
    
    
    /**
     * 첨부파일 처리 - 등록
     * @param request
     * @param params
     * @return
     */
    private List<Map<String, Object>> uploadFile(MultipartHttpServletRequest request, Map<String, Object> params) {
    	int boardinsertFileCount = 0;
		long atchFileId = -1;
		
		
		List<Map<String,Object>> lmRstUploadedFile = new ArrayList<Map<String,Object>>();
		Map<String,Object> mRstUploadedFile = null;
		
		for (Iterator<String> i = request.getFileNames(); i.hasNext(); ) {
			String fileId = i.next();
			MultipartFile file = request.getFile(fileId);
			
			mRstUploadedFile = new HashMap<String, Object>();
			try {
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
				
				/*if(!orgFileName.equals("")) {
					
					Map<String, Object> mapFile = simpleBizDao.select("bulletin.select-max-index-AttachFileList", params);
					atchFileId = ((BigDecimal)mapFile.get("TOTAL")).longValue() + 1;
					mRstUploadedFile.put("atchFileId", atchFileId);
					mRstUploadedFile.put("fileSize", file.getSize());
					
					boardinsertFileCount += simpleBizDao.insert("bulletin.insert-AttachFileList", params);
				}*/
				
				lmRstUploadedFile.add(mRstUploadedFile);
			} catch (IllegalStateException | IOException e) {
				LOGGER.error("!!! error", e);
			}
		}
		
		return lmRstUploadedFile;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostUpdate(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception{
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	if ( "Y".equalsIgnoreCase((String) params.get("POPUP_YN")) ) {
    		params.put("POPUP_START_DT_TM", ((String) params.get("POPUP_START_DT_TM") + " 00:00:00.000") );
    		params.put("POPUP_END_DT_TM", ((String) params.get("POPUP_END_DT_TM") + " 23:59:59.999") );
    	} else {
    		params.put("POPUP_START_DT_TM", null);
    		params.put("POPUP_END_DT_TM", null);
    	}
    	
    	
		//게시판 정보 수정
		int boardUpdateCount = simpleBizDao.update("Board.boardPostUpdate", params);
		
		List<Map<String,Object>> lmRstUploadedFile = null;
	    //params = uploadFile(request, params);
		lmRstUploadedFile = uploadFile(request, params);
	    int totalFileUpdateCount = 0;
	    int fileUpdateCount = -1;
	    for (Map<String,Object> tmpFile : lmRstUploadedFile) {
	    	
	    	tmpFile.put("POST_ID", params.get("POST_ID"));
	    	tmpFile.put("BRD_ID", params.get("BRD_ID"));
	    	tmpFile.put("userId", HttpUtil.getLoginUserId(request));
	    	
	    	
	    	fileUpdateCount = simpleBizDao.update("Board.boardPostFileInsert", tmpFile);
	    	LOGGER.debug("file Update result : [{}], [{}]", fileUpdateCount, (String) tmpFile.get("orgFileName"));
	    	
	    	totalFileUpdateCount += fileUpdateCount;
	    }
	    LOGGER.debug("total file Update result : [{}]", fileUpdateCount);
	    
		rtnMap.put("POST_ID", params.get("POST_ID"));
		rtnMap.put("INSERT_POST_CNT", fileUpdateCount);
		rtnMap.put("INSERT_FILE_CNT", totalFileUpdateCount);
		rtnMap.put("BRD_ID", params.get("BRD_ID"));
		rtnMap.put("params", params);
	    
		Map<String, Object> logParams = new HashMap<String, Object>();
		
		logParams.put("BRD_ID", params.get("BRD_ID"));
		logParams.put("POST_ID", params.get("POST_ID"));
		logParams.put("POST_TITLE", params.get("POST_TITLE"));
		logParams.put("MOD_USR_ID", params.get("userId"));
		logParams.put("INSERT_FILE_CNT", totalFileUpdateCount);
		
		//포탈 로그 기록(게시물 + 파일 등록)
		logService.addPortalLog(request, params.get("BRD_ID").toString(), params.get("POST_ID").toString(), "UPDATE", logParams);
			
		return rtnMap;
    }
	
}
//private Map<String, Object> uploadFile(MultipartHttpServletRequest request, Map<String, Object> params) {
//int boardinsertFileCount = 0;
//long atchFileId = -1;
//List<Map<String,Object>> lmRstUploadedFile = new ArrayList<Map<String,Object>>();
//
//for (Iterator<String> i = request.getFileNames(); i.hasNext(); ) {
//	String fileId = i.next();
//	MultipartFile file = request.getFile(fileId);
//	
//	try {
//		String orgFileName = URLDecoder.decode(FilenameUtils.getBaseName(file.getOriginalFilename()), "utf-8");
//		
//		//파일 확인
//		if(!orgFileName.equals("")) {
//			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
//			String currentTime = timeFormat.format(new Date());
//			String newFileName = currentTime + HttpUtil.replaceFilePath(RandomStringUtils.randomAlphanumeric(32));
//			
//			params.put("newFileName", newFileName);
//			params.put("orgFileName", orgFileName);
//			params.put("orgFileType", FilenameUtils.getExtension(file.getOriginalFilename()));
//			
//			String filePath = CustomProperties.getProperty("attach.base.location");//\mococo\portal\
//			String uploadFilePath = filePath + (String)params.get("BRD_ID") + File.separator;
//			params.put("uploadFilePath", uploadFilePath);
//			
//			uploadFilePath += newFileName + '.' + FilenameUtils.getExtension(file.getOriginalFilename());
//			File uploadFile = new File(uploadFilePath);
//			file.transferTo(uploadFile);
//			
//			/*
//			SLBsUtil sUtil = new SLBsUtil();
//			int encrypted = sUtil.isEncryptFile(uploadFilePath);
//			
//			if(encrypted == 1) {
//				//암호화 파일 처리
//				File rtnDecDrmFile = DrmUtil.decDrm(uploadFile);
//				uploadFile.delete();
//				FileUtils.moveFile(rtnDecDrmFile, new File(uploadFilePath.replace("tmp_", "")));
//			} else if(encrypted == 0) {
//				//일반파일 처리
//				FileUtils.moveFile(uploadFile, new File(uploadFilePath.replace("tmp_", "")));
//			}
//			*/
//			
//		} else {
//			params.put("newFileName", "");
//			params.put("orgFileName", "");
//			params.put("orgFileType", "");
//		}
//		
//		if(!orgFileName.equals("")) {
//			
//			Map<String, Object> mapFile = simpleBizDao.select("bulletin.select-max-index-AttachFileList", params);
//			atchFileId = ((BigDecimal)mapFile.get("TOTAL")).longValue() + 1;
//			params.put("atchFileId", atchFileId);
//			params.put("fileSize", file.getSize());
//			
//			boardinsertFileCount += simpleBizDao.insert("bulletin.insert-AttachFileList", params);
//		}
//		
//	} catch (IllegalStateException | IOException e) {
//		LOGGER.error("!!! error", e);
//	}
//}
//
//return params;
//}
