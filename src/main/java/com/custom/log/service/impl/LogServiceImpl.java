package com.custom.log.service.impl;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.HttpUtil;

/**
 * LogServiceImpl
 * @author mococo
 *
 */
@Service("logService")
public class LogServiceImpl implements LogService {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);
    
	/**
	 * simpleBizDao
	 */
	/* default */ @Autowired /* default */ SimpleBizDao simpleBizDao;
    
	
    /**
     * LogServiceImpl
     */
    public LogServiceImpl() {
    	logger.debug("LogServiceImpl");
    }
    
    
    @Override
    public Map<String, Object> addPortalLog(
    		  final HttpServletRequest request
    		, final String screenId
    		, final String screenDetailId
    		, final String userAction
    		, final Map<String, Object> datilInfo) throws SQLException {
    	final Map<String, Object> params = new ConcurrentHashMap<>();
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	//TODO 인사정보 연동 필요
    	params.put("userNm", "");
    	params.put("userDeptId", "");
    	params.put("userDeptNm", "");
    	params.put("userPosId", "");
    	params.put("userPosNm", "");
    	
    	params.put("userIp", HttpUtil.getClientIP(request));
    	params.put("screenId", screenId);
    	params.put("screenDetailId", screenDetailId);
    	params.put("userAction", userAction);
    	
    	final JSONObject detailObj = new JSONObject(datilInfo);
    	params.put("datilInfo", detailObj.toString());
    	
    	final int logCnt = simpleBizDao.insert("Log.addPortalLog", params);
        
        rtnMap.put("data", logCnt);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    @Override
    public Map<String, Object> addPortalLog(
    		  final MultipartHttpServletRequest request
    		, final String screenId
    		, final String screenDetailId
    		, final String userAction
    		, final Map<String, Object> datilInfo) throws SQLException {
    	
    		final Map<String, Object> params = new ConcurrentHashMap<>();
    		final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
	    	params.put("userId", HttpUtil.getLoginUserId(request));
	    	
	    	//TODO 인사정보 연동 필요
	    	params.put("userNm", "");
	    	params.put("userDeptId", "");
	    	params.put("userDeptNm", "");
	    	params.put("userPosId", "");
	    	params.put("userPosNm", "");
	    	
	    	params.put("userIp", HttpUtil.getClientIP(request));
	    	params.put("screenId", screenId);
	    	params.put("screenDetailId", screenDetailId);
	    	params.put("userAction", userAction);
	    	
	    	final JSONObject detailObj = new JSONObject(datilInfo);
	    	params.put("datilInfo", detailObj.toString());
	    	
	    	final int logCnt = simpleBizDao.insert("Log.addPortalLog", params);
	        
	        rtnMap.put("data", logCnt);
	        rtnMap.put("params", params);
	        
	        return rtnMap;
    }
}
