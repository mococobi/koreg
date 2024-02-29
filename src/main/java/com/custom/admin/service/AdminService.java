package com.custom.admin.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * AdminService
 * @author mococo
 *
 */
public interface AdminService {
	
	/**
	 * getSessionPortalAuthList
	 * @param request
	 * @return
	 */
	List<String> getSessionPortalAuthList(HttpServletRequest request);
	
	/**
	 * adminAuthList
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> adminAuthList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);
	
	/**
	 * boardList
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> boardList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws SQLException;
	
	/**
	 * boardDetail
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> boardDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws SQLException;
	
	/**
	 * boardInsert
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> boardInsert(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws SQLException;
	
	/**
	 * boardInsert
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> boardUpdate(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws SQLException;
	
	/**
	 * logList
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> logList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws SQLException;
	
	/**
	 * codeInsert
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> codeInsert(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws SQLException;
	
	/**
	 * codeUpdate
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> codeUpdate(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws SQLException;
}
