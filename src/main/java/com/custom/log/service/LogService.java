package com.custom.log.service;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * LogService
 * @author mococo
 *
 */
public interface LogService {
	
	/**
	 * addPortalLog
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> addPortalLog(HttpServletRequest request, String screenId, String screenDetailId, String userAction, Map<String, Object> datilInfo) throws SQLException;
	
	/**
	 * addPortalLog
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> addPortalLog(MultipartHttpServletRequest request, String screenId, String screenDetailId, String userAction, Map<String, Object> datilInfo) throws SQLException;
	
}
