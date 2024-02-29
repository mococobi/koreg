package com.custom.code.service;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CodeService
 * @author mococo
 *
 */
public interface CodeService {
	
	/**
	 * 코드 분류 - 리스트 조회
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> codeTypeList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws SQLException;
	
	/**
	 * 코드 - 리스트 조회
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> codeList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws SQLException;

}
