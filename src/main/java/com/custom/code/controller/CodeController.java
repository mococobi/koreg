package com.custom.code.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.custom.code.service.CodeService;
import com.mococo.web.util.ControllerUtil;

/**
 * 코드 Controller
 * @author mococo
 *
 */
@Controller
@RequestMapping("/code/*")
public class CodeController {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(CodeController.class);
    
    /**
     * 코드
     */
    /* default */ @Autowired /* default */ CodeService codeService;
    
    
    /**
     * BoardServiceImpl
     */
    public CodeController() {
    	logger.debug("CodeController");
    }
    
    
    /**
     * 코드 분류 - 리스트 조회 - 그리드
     * @return
     */
    @PostMapping("/code/codeTypeList.json")
    @ResponseBody
    public Map<String, Object> codeTypeList(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = codeService.codeTypeList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("userListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 코드 분류 - 리스트 조회 - 그리드
     * @return
     */
    @PostMapping("/code/codeTypeListGrid.json")
    public Map<String, Object> codeTypeListGrid(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = codeService.codeTypeList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("userListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 코드 - 리스트 조회
     * @return
     */
    @PostMapping("/code/codeList.json")
    @ResponseBody
    public Map<String, Object> boardPostList(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	params.put("CHECK_LIST_COUNT", true);
    	logger.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = codeService.codeList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("userListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
    /**
     * 코드 - 리스트 조회 - 그리드
     * @return
     */
    @PostMapping("/code/codeListGrid.json")
    public Map<String, Object> codeListGrid(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
    	params.put("PORTAL_LOG", false);
    	params.put("CHECK_LIST_COUNT", true);
    	logger.debug("params : [{}]", params);
    	Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();
    	
    	try {
    		final Map<String, Object> rtnList = codeService.codeList(request, response, params);
    		rtnMap.putAll(rtnList);
		} catch (BadSqlGrammarException | SQLException e) {
			rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			logger.error("userListGrid Exception", e);
		}
    	
    	return rtnMap;
    }
    
    
}