package com.custom.log.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogService {

	public Map<String, Object> addPortalLog(HttpServletRequest request, String screenId, String screenDetailId, String userAction, Map<String, Object> datilInfo);
	
}
