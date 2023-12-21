package com.custom.board.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface BoardService {
	
	/**
	 * 게시판 상세
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	/**
	 * 게시판 - 게시물 목록 조회
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardPostList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	/**
	 * 게시판 - 게시물 상세
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardPostDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	/**
	 * 게시판 - 게시물 추가
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardPostInsert(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws Exception;
	
	/**
	 * 게시판 - 게시물 수정
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardPostUpdate(MultipartHttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) throws Exception;
	
	/**
	 * 게시판 - 게시물 삭제
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardPostDelete(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
	/**
	 * 게시판 - 게시물 - 파일 상세
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> boardPostFileDetail(HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Exception;
	
}
