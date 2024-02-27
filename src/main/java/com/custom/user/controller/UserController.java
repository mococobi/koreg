package com.custom.user.controller;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.custom.user.service.UserService;
import com.mococo.web.util.ControllerUtil;

/**
 * UserController
 * @author mococo
 *
 */
@Controller
@RequestMapping("/user/*")
public class UserController {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    /**
     * userService
     */
    /* default */ @Autowired /* default */ UserService userService;
    
    
    /**
	 * UserController
	 */
    public UserController() {
    	logger.debug("UserController");
    }
    
    
    /**
     * 사용자 - 사용자 리스트 조회 - 그리드
     * @return
     */
    @PostMapping("/user/userListGrid.json")
    public Map<String, Object> userListGrid(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
//    	LOGGER.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = userService.userList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("userListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
}