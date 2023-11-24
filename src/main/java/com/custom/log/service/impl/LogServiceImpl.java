package com.custom.log.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.custom.log.service.LogService;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.web.util.HttpUtil;

@Service(value = "logService")
public class LogServiceImpl implements LogService {

    final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);
    
    @Autowired
    SimpleBizDao simpleBizDao;
    
    @Override
    public Map<String, Object> addPortalLog(HttpServletRequest request, String screenId, String screenDetailId, String userAction, Map<String, Object> datilInfo) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	
    	Map<String, Object> rtnMap = new HashMap<String, Object>();
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
    	
    	JSONObject detailObj = new JSONObject(datilInfo);
    	params.put("datilInfo", detailObj.toString());
    	
    	int logCnt = simpleBizDao.insert("Log.addPortalLog", params);
        
        rtnMap.put("data", logCnt);
        rtnMap.put("params", params);
        
        return rtnMap;
    }
    
}
