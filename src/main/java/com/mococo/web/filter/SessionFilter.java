/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 세션 필터 구현
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;

public class SessionFilter implements Filter {
	private static final Logger LOGGER = LogManager.getLogger(SessionFilter.class);
	
	private final Set<String> ENTRYPOINTS = new HashSet<String>();
	
	@Override
	public void destroy() {
		//
	}
	
	
	private final boolean isEntryPoint(String uri) {
		if (StringUtils.isNotEmpty(uri)) {
			for (String entryPoint : ENTRYPOINTS) {
//				LOGGER.debug("entryPoint : [{}]", entryPoint);
//				LOGGER.debug("uri.matche(entryPoint) : [{}]", uri.matches(entryPoint));
				if (uri.matches(entryPoint)) {
					LOGGER.debug("=> is entry point: [{}]", uri);
					return true;
				}
			}
		}

		return false;
	}
	
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) arg0;
		final HttpServletResponse response = (HttpServletResponse) arg1;

		String uri = request.getRequestURI();
//		LOGGER.debug("=> request.getRequestURI(): [{}]", uri);

		if (!isEntryPoint(uri)) {
			String currentSessionId = HttpUtil.getLoginUserId(request);
			if (StringUtils.isEmpty(currentSessionId)) {
				
				/*
				if (HttpUtil.isJsonRequest(request)) {
					LOGGER.debug("=> reponse json");

//					Gson gson = new Gson();
					response.setContentType("application/json");
					response.setCharacterEncoding("utf-8");
					
					PrintWriter out = response.getWriter();
//					out.print(gson.toJson(ControllerUtil.getFailMap("nosession")));
					out.flush();
				} else {
					
				}
				*/
				
				LOGGER.debug("=> 세션 없는 요청 URI [{}] foward : [{}]", uri, CustomProperties.getProperty("portal.login.page"));
				RequestDispatcher dispatcher = request.getSession().getServletContext().getRequestDispatcher(CustomProperties.getProperty("portal.login.page"));
				dispatcher.forward(request, response);
				
				return;
			}
		}
		
		/*
		if(uri.indexOf("servlet/mstrWebAdmin") > -1) {
			if(request.getParameter("evt") == null) {
				String sessionId = (String)CookieManager.getCookieValue(SECode.USER_ID, request);
				if(sessionId == null) {
					sessionId = (String) request.getSession().getAttribute("mstrUserIdAttr");
				}
				String sessionIp = HttpUtil.getClientIPReal(request);
				String sessionTime = timeFormat.format(new Date());
				
				String logText = sessionId + "|" + sessionIp + "|" +sessionTime + "|IDS|BI|mstrWebAdmin";
				LOGGER_B02.info("응용프로그램 관리자 접근기록 [{}]", logText);
				LogUtil.writeSystemLog("B02", logText);
			}
		}
		*/
		
		chain.doFilter(request, response);
	}
	
	
	/**
	 * <pre>
	 * 목적 : 패턴 검색
	 * 매개변수 : 
	 * 	FilterConfig filterConfig
	 * 	String parameterName
	 * 반환값 : java.lang.Boolean
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	private boolean contains(FilterConfig filterConfig, String parameterName) {
		if (filterConfig == null) {
			return false;
		}

		Enumeration<String> paramNames = filterConfig.getInitParameterNames();

		while (paramNames.hasMoreElements()) {
			if (StringUtils.equals(paramNames.nextElement(), parameterName)) {
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * <pre>
	 * 목적 : 통과 URL 설정
	 * 매개변수 : 
	 * 	String entryPoints
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public void setEntryPoints(String entryPoints) {
		String values[] = entryPoints.split(";");
		for (String value : values) {
			this.ENTRYPOINTS.add(value.trim());
		}
	}
	
	
	/**
	 * <pre>
	 * 목적 : 시작 함수
	 * 매개변수 : 
	 * 	FilterConfig filterConfig
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (contains(filterConfig, "entryPoints")) {
			setEntryPoints(filterConfig.getInitParameter("entryPoints"));
		}
	}

}
