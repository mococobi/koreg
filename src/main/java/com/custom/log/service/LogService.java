package com.custom.log.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface LogService {

	public Map<String, Object> addPortalLog(HttpServletRequest request, String screenId, String screenDetailId, String userAction, Map<String, Object> datilInfo) throws Exception;

	public Map<String, Object> addPortalLog(MultipartHttpServletRequest request, String screenId, String screenDetailId, String userAction, Map<String, Object> datilInfo) throws Exception;
	
}
