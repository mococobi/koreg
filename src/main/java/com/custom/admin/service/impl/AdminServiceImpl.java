package com.custom.admin.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.FileUtil;
import com.mococo.web.util.HttpUtil;
import com.mococo.web.util.PortalCodeUtil;

/**
 * AdminServiceImpl
 * @author mococo
 *
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	
	/**
	 * simpleBizDao
	 */
	/* default */ @Autowired /* default */ SimpleBizDao simpleBizDao;
    
    /**
     * logService
     */
	/* default */ @Autowired /* default */ LogService logService;
	
	
    /**
     * AdminServiceImpl
     */
    public AdminServiceImpl() {
    	logger.debug("AdminServiceImpl");
    }
	
	
    @SuppressWarnings("unchecked")
	@Override
    public List<String> getSessionPortalAuthList(final HttpServletRequest request) {
    	return (List<String>) request.getSession().getAttribute("PORTAL_AUTH");
    }
    
    
    @Override
    public List<Map<String, Object>> adminAuthList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) {
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
        return simpleBizDao.list("Admin.adminAuthList", params);
    }
    
    
    @Override
    public Map<String, Object> boardList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	params.put(PortalCodeUtil.countCheck, false);
    	final List<Map<String, Object>> rtnList = simpleBizDao.list("Admin.boardList", params);
        
        params.put(PortalCodeUtil.countCheck, true);
        final Map<String, Object> rtnListCnt = simpleBizDao.select("Admin.boardList", params);
    	
    	if((Boolean)params.get("PORTAL_LOG")) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, PortalCodeUtil.BOARD_ADMIN, "", "READ", params);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> boardDetail(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	final Map<String, Object> rtnPostMap = simpleBizDao.select("Admin.boardDetail", params);
    	
    	//포탈 로그 기록(상세 조회)
    	logService.addPortalLog(request, PortalCodeUtil.BOARD_ADMIN, params.get(PortalCodeUtil.BRD_ID).toString(), "DETAIL", params);
        
        rtnMap.put("data", rtnPostMap);
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardInsert(final MultipartHttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
		//게시판 정보 입력
    	final int boardinsertCount = simpleBizDao.insert("Admin.boardInsert", params);
    	FileUtil.folderCheckAndCreate(CustomProperties.getProperty("attach.base.location") + params.get("insertKey").toString());
    	
		//포탈 로그 기록(생성)
    	logService.addPortalLog(request, PortalCodeUtil.BOARD_ADMIN, params.get("insertKey").toString(), "CREATE", params);
		
		rtnMap.put("INSERT_BOARD_CNT", boardinsertCount);
		rtnMap.put(PortalCodeUtil.BRD_ID, params.get("insertKey"));
		rtnMap.put(PortalCodeUtil.params, params);
		
		return rtnMap;
    }
    
    
    @Override
    @Transactional("transactionManager")
    public Map<String, Object> boardUpdate(final MultipartHttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
		//게시판 정보 입력
    	final int boardupdateCount = simpleBizDao.update("Admin.boardUpdate", params);
		
		//포탈 로그 기록(수정)
    	logService.addPortalLog(request, PortalCodeUtil.BOARD_ADMIN, params.get(PortalCodeUtil.BRD_ID).toString(), "MODIFY", params);
		
		rtnMap.put("UPDATE_BOARD_CNT", boardupdateCount);
		rtnMap.put(PortalCodeUtil.BRD_ID, params.get(PortalCodeUtil.BRD_ID).toString());
		rtnMap.put(PortalCodeUtil.params, params);
		
		return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> logList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put(PortalCodeUtil.userId, HttpUtil.getLoginUserId(request));
    	
    	params.put(PortalCodeUtil.countCheck, false);
    	final List<Map<String, Object>> rtnList = simpleBizDao.list("Admin.logList", params);
        
        params.put(PortalCodeUtil.countCheck, true);
        final Map<String, Object> rtnListCnt = simpleBizDao.select("Admin.logList", params);
    	
    	if((Boolean)params.get("PORTAL_LOG")) {
    		//포탈 로그 기록(조회)
    		logService.addPortalLog(request, "LOG_ADMIN", "", "READ", params);
    	}
        
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put(PortalCodeUtil.params, params);
        
        return rtnMap;
    }
}
