package com.custom.admin.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface AdminService {
	
	public List<String> getSessionPortalAuthList(HttpServletRequest request);
	
	public List<Map<String, Object>> adminAuthList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);
	
	public Map<String, Object> boardList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);
	
	public Map<String, Object> boardDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);
	
	public Map<String, Object> boardInsert(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params);
	
	public Map<String, Object> boardUpdate(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params);
	
	public Map<String, Object> logList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);
}
