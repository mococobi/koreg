package com.custom.board.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
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
    public Map<String, Object> boardList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	rtnMap = simpleBizDao.select("Board.boardList", params);
        
        rtnMap.put("data", rtnMap);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> boardPostList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
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
    public Map<String, Object> boardPostDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	Map<String, Object> rtnPostMap = simpleBizDao.select("Board.boardPostDetail", params);
    	List<Map<String, Object>> rtnPostFileList = simpleBizDao.list("Board.boardPostFileList", params);
    	
    	//포탈 로그 기록(상세 조회)
    	logService.addPortalLog(request, params.get("boardId").toString(), params.get("postId").toString(), "DETAIL", params);
        
        rtnMap.put("data", rtnPostMap);
        rtnMap.put("file", rtnPostFileList);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
		//게시판 정보 입력
		int boardinsertCount = simpleBizDao.insert("Board.boardPostInsert", params);
		
		params = uploadFile(request, params);
		
		params.put("POST_ID", params.get("insertKey"));
		rtnMap.put("INSERT_POST_CNT", boardinsertCount);
		rtnMap.put("INSERT_FILE_CNT", params.get("boardinsertFileCount"));
		rtnMap.put("BRD_ID", params.get("BRD_ID"));
		rtnMap.put("params", params);
		
		return rtnMap;
    }
    
    
    /**
     * 첨부파일 처리 - 등록
     * @param request
     * @param params
     * @return
     */
    private Map<String, Object> uploadFile(MultipartHttpServletRequest request, Map<String, Object> params) {
    	int boardinsertFileCount = 0;
		long atchFileId = -1;
    	
		for (Iterator<String> i = request.getFileNames(); i.hasNext(); ) {
			String fileId = i.next();
			MultipartFile file = request.getFile(fileId);
			
			try {
				String orgFileName = URLDecoder.decode(FilenameUtils.getBaseName(file.getOriginalFilename()), "utf-8");
				
				//파일 확인
				if(!orgFileName.equals("")) {
					SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
					String currentTime = timeFormat.format(new Date());
					String newFileName = currentTime + HttpUtil.replaceFilePath(RandomStringUtils.randomAlphanumeric(32));
					
					params.put("newFileName", newFileName);
					params.put("orgFileName", orgFileName);
					params.put("orgFileType", FilenameUtils.getExtension(file.getOriginalFilename()));
					
					String filePath = CustomProperties.getProperty("attach.base.location");
					String uploadFilePath = filePath + (String)params.get("BRD_ID") + File.separator;
					params.put("uploadFilePath", uploadFilePath);
					
					uploadFilePath += "tmp_" + newFileName + '.' + FilenameUtils.getExtension(file.getOriginalFilename());
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
					params.put("newFileName", "");
					params.put("orgFileName", "");
					params.put("orgFileType", "");
				}
				
				if(!orgFileName.equals("")) {
					
					Map<String, Object> mapFile = simpleBizDao.select("bulletin.select-max-index-AttachFileList", params);
					atchFileId = ((BigDecimal)mapFile.get("TOTAL")).longValue() + 1;
					params.put("atchFileId", atchFileId);
					params.put("fileSize", file.getSize());
					
					boardinsertFileCount += simpleBizDao.insert("bulletin.insert-AttachFileList", params);
				}
				
			} catch (IllegalStateException | IOException e) {
				LOGGER.error("!!! error", e);
			}
		}
		
		return params;
    }
}
