package com.custom.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.custom.admin.service.AdminService;
import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.HttpUtil;

@Service(value = "adminService")
public class AdminServiceImpl implements AdminService {

    final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);
    
    @Autowired
    SimpleBizDao simpleBizDao;
    
    @Autowired
    LogService logService;
    
    
    @SuppressWarnings("unchecked")
	@Override
    public List<String> getSessionPortalAuthList(HttpServletRequest request) {
    	List<String> rtnList = (List<String>) request.getSession().getAttribute("PORTAL_AUTH");
    	return rtnList;
    }
    
    
    @Override
    public List<Map<String, Object>> adminAuthList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	List<Map<String, Object>> rtnList = simpleBizDao.list("Admin.adminAuthList", params);
        
        return rtnList;
    }
    
    
    @Override
    public Map<String, Object> boardList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
        List<Map<String, Object>> rtnList = simpleBizDao.list("Admin.boardList", params);
        
        params.put("countCheck", true);
    	Map<String, Object> rtnListCnt = simpleBizDao.select("Admin.boardList", params);
    	
    	if((Boolean)params.get("PORTAL_LOG") == true) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, "BOARD_ADMIN", "", "READ", params);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> boardDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	Map<String, Object> rtnPostMap = simpleBizDao.select("Admin.boardDetail", params);
    	
    	//포탈 로그 기록(상세 조회)
    	logService.addPortalLog(request, "BOARD_ADMIN", params.get("boardId").toString(), "DETAIL", params);
        
        rtnMap.put("data", rtnPostMap);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardInsert(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
		//게시판 정보 입력
		int boardinsertCount = simpleBizDao.insert("Admin.boardInsert", params);
		
		//포탈 로그 기록(생성)
    	logService.addPortalLog(request, "BOARD_ADMIN", params.get("insertKey").toString(), "CREATE", params);
		
		rtnMap.put("INSERT_BOARD_CNT", boardinsertCount);
		rtnMap.put("BRD_ID", params.get("insertKey"));
		rtnMap.put("params", params);
		
		return rtnMap;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardUpdate(MultipartHttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	
    	System.out.println("\n\n\nmksong 테스트 : " + params.get("BRD_CRT_AUTH"));
    	
		//게시판 정보 입력
		int boardupdateCount = simpleBizDao.update("Admin.boardUpdate", params);
		
		//포탈 로그 기록(수정)
    	logService.addPortalLog(request, "BOARD_ADMIN", params.get("BRD_ID").toString(), "MODIFY", params);
		
		rtnMap.put("UPDATE_BOARD_CNT", boardupdateCount);
		rtnMap.put("BRD_ID", params.get("BRD_ID").toString());
		rtnMap.put("params", params);
		
		return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> logList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
        List<Map<String, Object>> rtnList = simpleBizDao.list("Admin.logList", params);
        
        params.put("countCheck", true);
    	Map<String, Object> rtnListCnt = simpleBizDao.select("Admin.logList", params);
    	
    	if((Boolean)params.get("PORTAL_LOG") == true) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, "LOG_ADMIN", "", "READ", params);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
}
