package com.custom.user.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
	
	/**
	 * 사용자 - 사용자 리스트 조회
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> userList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
}
