package com.custom.code.service.impl;

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

import com.custom.code.service.CodeService;
import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.HttpUtil;

/**
 * CodeServiceImpl
 * @author mococo
 *
 */
@Service("codeService")
public class CodeServiceImpl implements CodeService {

	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(CodeServiceImpl.class);
    
	/**
	 * simpleBizDao
	 */
	/* default */ @Autowired /* default */ SimpleBizDao simpleBizDao;
    
    /**
     * logService
     */
	/* default */ @Autowired /* default */ LogService logService;
	
	
    /**
     * CodeServiceImpl
     */
    public CodeServiceImpl() {
    	logger.debug("CodeServiceImpl");
    }
    
    //코드 분류 - 리스트 조회
    @Override
    public Map<String, Object> codeTypeList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
    	final List<Map<String, Object>> rtnList = simpleBizDao.list("Code.codeTypeList", params);
        
        params.put("countCheck", true);
        final Map<String, Object> rtnListCnt = simpleBizDao.select("Code.codeTypeList", params);
    	 	
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
    //코드 - 리스트 조회
    @Override
    public Map<String, Object> codeList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
    	final List<Map<String, Object>> rtnList = simpleBizDao.list("Code.codeList", params);
        
		if(params.get("CHECK_LIST_COUNT") != null && (Boolean)params.get("CHECK_LIST_COUNT")) {
			params.put("countCheck", true);
			final Map<String, Object> rtnListCnt = simpleBizDao.select("Code.codeList", params);
			rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
    	}
    	 	
        rtnMap.put("data", rtnList);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
}