package com.custom.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.custom.user.service.UserService;
import com.mococo.web.util.ControllerUtil;

@Controller
@RequestMapping("/user/*")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    UserService userService;
    
    
    /**
     * 사용자 - 사용자 리스트 조회 - 그리드
     * @return
     */
    @RequestMapping(value = "/user/userListGrid.json", method = { RequestMethod.POST })
    public Map<String, Object> userListGrid(HttpServletRequest request, HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	LOGGER.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		Map<String, Object> rtnList = new HashMap<String, Object>();
    		rtnList = userService.userList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (Exception e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("userListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
}