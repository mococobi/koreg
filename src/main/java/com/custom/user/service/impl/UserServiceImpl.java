package com.custom.user.service.impl;

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

import com.custom.log.service.LogService;
import com.custom.user.service.UserService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.HttpUtil;

/**
 * UserServiceImpl
 * @author mococo
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
	/**
	 * simpleBizDao
	 */
	/* default */ @Autowired /* default */ SimpleBizDao simpleBizDao;
    
    /**
     * logService
     */
	/* default */ @Autowired /* default */ LogService logService;
	
	
    /**
     * UserServiceImpl
     */
    public UserServiceImpl() {
    	logger.debug("UserServiceImpl");
    }
    
    
    //사용자 - 사용자 리스트 조회
    @Override
    public Map<String, Object> userList(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> params) throws SQLException {
    	final Map<String, Object> rtnMap = new ConcurrentHashMap<>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
    	final List<Map<String, Object>> rtnList = simpleBizDao.list("User.UserList", params);
        
        params.put("countCheck", true);
        final Map<String, Object> rtnListCnt = simpleBizDao.select("User.UserList", params);
    	 	
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
}