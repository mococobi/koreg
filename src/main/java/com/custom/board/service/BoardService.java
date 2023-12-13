package com.custom.board.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface BoardService {

	public Map<String, Object> boardList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	public Map<String, Object> boardPostList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	public Map<String, Object> boardPostDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws Exception;
}
