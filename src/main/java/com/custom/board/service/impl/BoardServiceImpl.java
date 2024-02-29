package com.custom.board.service.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.custom.code.service.CodeService;
import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;
import com.mococo.web.util.PortalCodeUtil;

/**
 * BoardServiceImpl
 * @author mococo
 *
 */
@Service("boardService")
public class BoardServiceImpl implements BoardService {

	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(BoardServiceImpl.class);
    
	/**
	 * simpleBizDao
	 */
	/* default */ @Autowired /* default */ SimpleBizDao simpleBizDao;
    
	/**
	 * logService
	 */
	/* default */ @Autowired /* default */ LogService logService;
	
	/**
     * 코드
     */
    /* default */ @Autowired /* default */ CodeService codeService;
    
	
    /**
     * BoardServiceImpl
     */
    public BoardServiceImpl() {
    	logger.debug("BoardServiceImpl");
    }
    
    
    /**
     * 게시판 - 상세
     */
    @Override
    public Map<String, Object> boardDetail(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException{
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	final Map<String, Object> boardMap = simpleBizDao.select("Board.boardDetail", params);
        
    	if(boardMap != null) {
    		rtnMap.put(PortalCodeUtil.data, boardMap);
    		rtnMap.put(PortalCodeUtil.params, params);
    		
    		if(PortalCodeUtil.CHECK_Y.equals(boardMap.get("POST_TYPE_YN").toString()) 
    			&& params.get("CHECK_POST_TYPE") != null && (Boolean)params.get("CHECK_POST_TYPE")) 
    		{
    			params.put("customOrder1", 3);
    			params.put("customOrder2", "asc");
    			params.put("CD_TYPE_ENG_NM", "PORTAL_BRD_TYPE");
    			params.put("CHECK_DEL_YN", true);
    			
    			final Map<String, Object> postTypeCodeMap = codeService.codeList(request, response, params);
    			rtnMap.put("postTypeCode", postTypeCodeMap.get(PortalCodeUtil.data));
    		}
    	}
        
        return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 목록 조회
     */
    @Override
    public Map<String, Object> boardPostList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
        //게시물 - 카운트
        params.put("countCheck", true);
        final Map<String, Object> listCnt = simpleBizDao.select("Board.boardPostList", params);
        final int rtnListCnt = Integer.parseInt(String.valueOf(listCnt.get("COUNT")));
        
        //게시물 - 리스트
        final List<Map<String, Object>> rtnList = new ArrayList<>();
        if(rtnListCnt > 0) {
        	//일반 게시물
        	params.put("countCheck", false);
        	params.put("fixCheck", false);
        	final List<Map<String, Object>> rtnNormalList = simpleBizDao.list("Board.boardPostList", params);
        	
        	if(rtnNormalList.isEmpty()) {
        		//게시판
            	final Map<String, Object> rtnBoardMap = simpleBizDao.select("Board.boardDetail", params);
            	if(PortalCodeUtil.CHECK_Y.equals(rtnBoardMap.get("POST_FIX_YN").toString())) {
            		//고정 게시물
                	params.put("fixCheck", true);
                	params.remove("listViewCount");
                	final List<Map<String, Object>> rtnFixList = simpleBizDao.list("Board.boardPostList", params);
                	if(!rtnFixList.isEmpty()) {
                		rtnList.addAll(rtnFixList);
                	}
            	}
        	} else {
        		if(PortalCodeUtil.CHECK_Y.equals(rtnNormalList.get(0).get("POST_FIX_YN").toString())) {
            		//고정 게시물
                	params.put("fixCheck", true);
                	params.remove("listViewCount");
                	final List<Map<String, Object>> rtnFixList = simpleBizDao.list("Board.boardPostList", params);
                	if(!rtnFixList.isEmpty()) {
                		rtnList.addAll(rtnFixList);
                	}
            	}
        	}
        	
        	rtnList.addAll(rtnNormalList);
        }
    	 	
    	//첨부파일
    	if(!rtnList.isEmpty()
    		&& PortalCodeUtil.CHECK_Y.equals(rtnList.get(0).get("POST_FILE_YN").toString()) 
    		&& params.get(PortalCodeUtil.CHECK_POST_FILE) != null && (Boolean)params.get(PortalCodeUtil.CHECK_POST_FILE)
    	) {
			final List<Object> postIdList = new ArrayList<>();
			for (final Map<String, Object> postItem : rtnList) {
				postIdList.add(postItem.get(PortalCodeUtil.POST_ID));
			}
			
			params.put("postIdList", postIdList);
			if(!postIdList.isEmpty()) {
				final List<Map<String, Object>> postFileList = simpleBizDao.list("Board.boardPostFaqFileList", params);
				
    			for (final Map<String, Object> postItem : rtnList) {
    				final List<Map<String, Object>> postFile = new ArrayList<>();
    				for (final Map<String, Object> postFileItem : postFileList) {
    					if(postItem.get(PortalCodeUtil.POST_ID).toString().equals(postFileItem.get(PortalCodeUtil.POST_ID).toString())) {
    						postFile.add(postFileItem);
    					}
    				}
    				postItem.put("attachfiles", postFile);
    			}
			}
    	}

    	if(params.get(PortalCodeUtil.PORTAL_LOG) != null && (Boolean)params.get(PortalCodeUtil.PORTAL_LOG)) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, params.get(PortalCodeUtil.BRD_ID).toString(), "", "READ", params);
    	}
        
        rtnMap.put(PortalCodeUtil.data, rtnList);
        rtnMap.put("dataSize", rtnListCnt);
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 상세
     */
    @Override
    public Map<String, Object> boardPostDetail(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	if(params.get(PortalCodeUtil.PORTAL_LOG) != null && (Boolean)params.get(PortalCodeUtil.PORTAL_LOG)) {
	    	//포탈 로그 기록(상세 조회)
	    	logService.addPortalLog(request, params.get(PortalCodeUtil.BRD_ID).toString(), params.get(PortalCodeUtil.POST_ID).toString(), "DETAIL", params);
    	}
    	
    	//게시물 - 정보
    	final Map<String, Object> rtnPostMap = simpleBizDao.select("Board.boardPostDetail", params);
    	
    	//게시판 타입
    	if(PortalCodeUtil.CHECK_Y.equals(rtnPostMap.get("POST_TYPE_YN").toString()) 
			&& params.get("CHECK_POST_TYPE") != null && (Boolean)params.get("CHECK_POST_TYPE")) 
		{
			params.put("customOrder1", 3);
			params.put("customOrder2", "asc");
			params.put("CD_TYPE_ENG_NM", "PORTAL_BRD_TYPE");
			params.put("CHECK_DEL_YN", true);
			
			final Map<String, Object> postTypeCodeMap = codeService.codeList(request, response, params);
			rtnMap.put("postTypeCode", postTypeCodeMap.get(PortalCodeUtil.data));
		}
    	
    	//첨부 파일
    	if(!rtnPostMap.isEmpty()
    		&& PortalCodeUtil.CHECK_Y.equals(rtnPostMap.get("POST_FILE_YN").toString()) 
    		&& params.get(PortalCodeUtil.CHECK_POST_FILE) != null && (Boolean)params.get(PortalCodeUtil.CHECK_POST_FILE)
    	) {
			final List<Map<String, Object>> rtnPostFileList = simpleBizDao.list("Board.boardPostFileList", params);
    		rtnMap.put("file", rtnPostFileList);
    	}
    	
    	//이전글, 다음글
    	if(params.get("CHECK_POST_LOCATION") != null && (Boolean)params.get("CHECK_POST_LOCATION")) {
    		final List<Map<String, Object>> rtnLocList = simpleBizDao.list("Board.boardPostBeforeNext", params);
    		rtnMap.put("location", rtnLocList);
    	}
    	
        rtnMap.put(PortalCodeUtil.data, rtnPostMap);
        rtnMap.put("boardData", rtnPostMap);
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 추가
     */
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostInsert(final MultipartHttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException, IOException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	if (PortalCodeUtil.CHECK_Y.equals(params.get("POPUP_YN"))) {
    		params.put(PortalCodeUtil.POPUP_START_DT_TM, (String) params.get(PortalCodeUtil.POPUP_START_DT_TM) + " 00:00:00.000" );
    		params.put(PortalCodeUtil.POPUP_END_DT_TM, (String) params.get(PortalCodeUtil.POPUP_END_DT_TM) + " 23:59:59.999" );
    	} else {
    		params.put(PortalCodeUtil.POPUP_START_DT_TM, null);
    		params.put(PortalCodeUtil.POPUP_END_DT_TM, null);
    	}
    	
		//게시판 정보 입력
    	final int boardinsertCount = simpleBizDao.insert("Board.boardPostInsert", params);
		
		//첨부 파일 등록
		final List<Map<String,Object>> lmRstUploadedFile = uploadFile(request, params);
	    int totalFileInsCnt = 0;
	    int fileInsertCount = -1;
	    for (final Map<String,Object> tmpFile : lmRstUploadedFile) {
	    	tmpFile.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.insertKey));
	    	tmpFile.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
	    	tmpFile.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
	    	
	    	fileInsertCount = simpleBizDao.insert("Board.boardPostFileInsert", tmpFile);
	    	final String logTmp1 = tmpFile.get(PortalCodeUtil.orgFileName).toString().replaceAll("[\r\n]","");
	    	logger.debug("file Insert result : [{}], [{}]", fileInsertCount, logTmp1);
	    	
	    	totalFileInsCnt += fileInsertCount;
	    }
	    logger.debug("total file Insert result : [{}]", fileInsertCount);
	    
	    rtnMap.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
		rtnMap.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.insertKey));
		rtnMap.put("INSERT_POST_CNT", boardinsertCount);
		rtnMap.put(PortalCodeUtil.INSERT_FILE_CNT, totalFileInsCnt);
//		rtnMap.put(PortalCodeUtil.params, params);
	    
		final Map<String, Object> logParams = new ConcurrentHashMap<>();
		
		logParams.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
		logParams.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.insertKey));
		logParams.put(PortalCodeUtil.POST_TITLE, params.get(PortalCodeUtil.POST_TITLE));
		logParams.put("CRT_USR_ID", params.get(PortalCodeUtil.userId));
		logParams.put(PortalCodeUtil.INSERT_FILE_CNT, totalFileInsCnt);
		
		//포탈 로그 기록(게시물 + 파일 등록)
		logService.addPortalLog(request, params.get(PortalCodeUtil.BRD_ID).toString(), params.get(PortalCodeUtil.insertKey).toString(), "CREATE", logParams);
			
		return rtnMap;
    }
    
    
    /**
     * 첨부파일 처리
     * @param mRquest
     * @param params
     * @return
     * @throws IOException
     */
    private List<Map<String, Object>> uploadFile(final MultipartHttpServletRequest mRquest, final Map<String, Object> params) throws IOException {
    	final List<Map<String,Object>> lmRstUploadedFile = new ArrayList<>();
		
		final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_", Locale.KOREA);
		final String currentTime = timeFormat.format(new Date());
		
		for (final Iterator<String> i = mRquest.getFileNames(); i.hasNext(); ) {
			final String fileId = i.next();
			final MultipartFile file = mRquest.getFile(fileId);
			final Map<String,Object> mRstUploadedFile = new ConcurrentHashMap<>();
			
			if(fileId.indexOf("ATTACH_FILE") > -1) {
				mRstUploadedFile.put("fileType", "ATTACH_FILE");
			} else {
				mRstUploadedFile.put("fileType", "OTHER");
			}
			
			final String orgFileName = URLDecoder.decode(FilenameUtils.getBaseName(file.getOriginalFilename()), "utf-8");
			//파일 확인
			if(PortalCodeUtil.CHECK_EMPTY.equals(orgFileName)) {
				mRstUploadedFile.put("newFileName", "");
				mRstUploadedFile.put(PortalCodeUtil.orgFileName, "");
				mRstUploadedFile.put("orgFileType", "");
			} else {
				final String newFileName = currentTime + HttpUtil.replaceFilePath(RandomStringUtils.randomAlphanumeric(32))+ '.' + FilenameUtils.getExtension(file.getOriginalFilename());
				
				mRstUploadedFile.put("newFileName", newFileName);
				mRstUploadedFile.put(PortalCodeUtil.orgFileName, orgFileName);
				mRstUploadedFile.put("orgFileType", FilenameUtils.getExtension(file.getOriginalFilename()));
				mRstUploadedFile.put("fileSize", file.getSize());
				
				final String filePath = CustomProperties.getProperty("attach.base.location");
				final String uploadFilePath = filePath + (String)params.get(PortalCodeUtil.BRD_ID) + "/";
				mRstUploadedFile.put("uploadFilePath", uploadFilePath);
				
				final Path uploadFile = Paths.get(uploadFilePath + FilenameUtils.getName(newFileName));
				Files.createFile(uploadFile);
				
				file.transferTo(uploadFile);
				
				//DRM 복호화 처리 - 필요시
			}
			lmRstUploadedFile.add(mRstUploadedFile);
		}
		
		return lmRstUploadedFile;
    }
    
    
    /**
     * 게시판 - 게시물 수정
     */
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostUpdate(final MultipartHttpServletRequest mRequest, final HttpServletResponse response, final Map<String, Object> params) throws SQLException, IOException{
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(mRequest));
    	
    	if (PortalCodeUtil.CHECK_Y.equals(params.get("POPUP_YN"))) {
    		params.put(PortalCodeUtil.POPUP_START_DT_TM, (String) params.get(PortalCodeUtil.POPUP_START_DT_TM) + " 00:00:00.000" );
    		params.put(PortalCodeUtil.POPUP_END_DT_TM, (String) params.get(PortalCodeUtil.POPUP_END_DT_TM) + " 23:59:59.999" );
    	} else {
    		params.put(PortalCodeUtil.POPUP_START_DT_TM, null);
    		params.put(PortalCodeUtil.POPUP_END_DT_TM, null);
    	}
    	
		//게시판 정보 수정
		final int boardUpdateCount = simpleBizDao.update("Board.boardPostUpdate", params);
		
		//첨부 파일 등록
		final List<Map<String,Object>> lmRstUploadedFile = uploadFile(mRequest, params);
	    int totalFileUpCnt = 0;
	    for (final Map<String,Object> tmpFile : lmRstUploadedFile) {
	    	tmpFile.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.POST_ID));
	    	tmpFile.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
	    	tmpFile.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(mRequest));
	    	
	    	final int fileInsertCount = simpleBizDao.update("Board.boardPostFileInsert", tmpFile);
	    	totalFileUpCnt += fileInsertCount;
	    			
	    	final String logTmp1 = tmpFile.get(PortalCodeUtil.orgFileName).toString().replaceAll("[\r\n]","");
	    	logger.debug("file Update result : [{}], [{}]", fileInsertCount, logTmp1);
	    }
	    logger.debug("total file Update result : [{}]", totalFileUpCnt);
	    
	    //첨부 파일 삭제
	    int totalFileDelCnt = 0;
	    final String[] delete_file_ids = mRequest.getParameterValues("deleteFileIds");
	    List<String> deleteFileList = new ArrayList<>();
		if (delete_file_ids != null) {
			deleteFileList = Arrays.asList(delete_file_ids);
			params.put("deleteFileList", deleteFileList);
			totalFileDelCnt += simpleBizDao.update("Board.boardPostFileDelete", params);
		}
		logger.debug("total file Delete result : [{}]", totalFileDelCnt);
	    
		rtnMap.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
		rtnMap.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.POST_ID));
		rtnMap.put("INSERT_POST_CNT", boardUpdateCount);
		rtnMap.put(PortalCodeUtil.INSERT_FILE_CNT, totalFileUpCnt);
		rtnMap.put("DELETE_FILE_CNT", totalFileDelCnt);
		rtnMap.put(PortalCodeUtil.params, params);
	    
		final Map<String, Object> logParams = new ConcurrentHashMap<>();
		
		logParams.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
		logParams.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.POST_ID));
		logParams.put(PortalCodeUtil.POST_TITLE, params.get(PortalCodeUtil.POST_TITLE));
		logParams.put("MOD_USR_ID", params.get(PortalCodeUtil.userId));
		logParams.put(PortalCodeUtil.INSERT_FILE_CNT, totalFileUpCnt);
		logParams.put("DELETE_FILE_CNT", deleteFileList.size());
		
		//포탈 로그 기록(게시물 + 파일 등록)
		logService.addPortalLog(mRequest, params.get(PortalCodeUtil.BRD_ID).toString(), params.get(PortalCodeUtil.POST_ID).toString(), "UPDATE", logParams);
			
		return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 삭제
     */
    @Override
    public Map<String, Object> boardPostDelete(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	final int postDeleteCnt = simpleBizDao.update("Board.boardPostDelete", params);
    	
    	//포탈 로그 기록(삭제)
    	logService.addPortalLog(request, params.get(PortalCodeUtil.BRD_ID).toString(), params.get(PortalCodeUtil.POST_ID).toString(), "DELETE", params);
    	
        rtnMap.put(PortalCodeUtil.data, postDeleteCnt);
        rtnMap.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID));
        rtnMap.put(PortalCodeUtil.POST_ID, params.get(PortalCodeUtil.POST_ID));
        rtnMap.put("POST_DELETE_CNT", postDeleteCnt);
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
    
    
    /**
     * 게시판 - 게시물 - 파일 상세
     */
    @Override
    public Map<String, Object> boardPostFileDetail(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException{
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
        return simpleBizDao.select("Board.boardPostFileDetail", params);
    }


    /**
     * 팝업 - 게시물 목록 조회
     */
    @Override
    public Map<String, Object> boardPostPopupList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	final List<Map<String, Object>> rtnList = simpleBizDao.list("Board.boardPostPopupList", params);
    	
    	//첨부파일
    	if(params.get(PortalCodeUtil.CHECK_POST_FILE) != null && (Boolean)params.get(PortalCodeUtil.CHECK_POST_FILE)) {
    		final List<Map<String, Object>> rtnPostFileList = simpleBizDao.list("Board.boardPostFileList", params);
    		rtnMap.put("file", rtnPostFileList);
    	}
        
        rtnMap.put(PortalCodeUtil.data, rtnList);
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
    
    
}