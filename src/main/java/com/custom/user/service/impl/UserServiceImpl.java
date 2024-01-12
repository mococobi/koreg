package com.custom.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    SimpleBizDao simpleBizDao;
    
    @Autowired
    LogService logService;
    
    
    //사용자 - 사용자 리스트 조회
    @Override
    public Map<String, Object> userList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception {
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
    	
    	params.put("userId", HttpUtil.getLoginUserId(request));
    	
    	params.put("countCheck", false);
        List<Map<String, Object>> rtnList = simpleBizDao.list("User.UserList", params);
        
        params.put("countCheck", true);
    	Map<String, Object> rtnListCnt = simpleBizDao.select("User.UserList", params);
    	 	
        rtnMap.put("data", rtnList);
        rtnMap.put("dataSize", rtnListCnt.get("COUNT"));
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
    
}