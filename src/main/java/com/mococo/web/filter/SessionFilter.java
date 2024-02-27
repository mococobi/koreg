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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
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

/**
 * SessionFilter
 * @author mococo
 *
 */
public class SessionFilter implements Filter {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(SessionFilter.class);
	
	/**
	 * ENTRYPOINTS
	 */
	private final Set<String> ENTRYPOINTS = new HashSet<>();
	
	
    /**
     * SessionFilter
     */
    public SessionFilter() {
    	logger.debug("SessionFilter");
    }
    
	
	@Override
	public void destroy() {
		//
	}
	
	
	private boolean isEntryPoint(final String uri) {
		Boolean rtnCheck = false;
		
		if (StringUtils.isNotEmpty(uri)) {
			for (final String entryPoint : ENTRYPOINTS) {
//				logger.debug("entryPoint : [{}]", entryPoint);
//				logger.debug("uri.matche(entryPoint) : [{}]", uri.matches(entryPoint));
				if (uri.matches(entryPoint)) {
					rtnCheck = true;
					logger.debug("=> is entry point: [{}]", uri);
				}
			}
		}

		return rtnCheck;
	}
	
	
	@Override
	public void doFilter(final ServletRequest arg0, final ServletResponse arg1, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) arg0;
		final HttpServletResponse response = (HttpServletResponse) arg1;

		final String uri = request.getRequestURI();
//		logger.debug("=> request.getRequestURI(): [{}]", uri);

		if (!isEntryPoint(uri)) {
			final String currentSessionId = HttpUtil.getLoginUserId(request);
			if (StringUtils.isEmpty(currentSessionId)) {
				if(uri.indexOf("Eis") > -1) {
					final String fowardUrl = CustomProperties.getProperty("eis.login.page");
					response.sendRedirect(request.getContextPath() + fowardUrl);
					logger.debug("=> 세션 없는 요청 URI [{}] foward : [{}]", uri, fowardUrl);
				} else {
					final String fowardUrl = CustomProperties.getProperty("portal.login.page");
					response.sendRedirect(request.getContextPath() + fowardUrl);
					logger.debug("=> 세션 없는 요청 URI [{}] foward : [{}]", uri, fowardUrl);
				}
				
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	
	/**
	 * 패턴 검색
	 * @param filterConfig
	 * @param parameterName
	 * @return
	 */
	private boolean contains(final FilterConfig filterConfig, final String parameterName) {
		Boolean rtnCheck = false;
		
		if (filterConfig == null) {
			rtnCheck = false;
		} else {
			final Enumeration<String> paramNames = filterConfig.getInitParameterNames();

			while (paramNames.hasMoreElements()) {
				if (StringUtils.equals(paramNames.nextElement(), parameterName)) {
					rtnCheck = true;
				}
			}
		}

		return rtnCheck;
	}
	
	
	/**
	 * 통과 URL 설정
	 * @param entryPoints
	 */
	public void setEntryPoints(final String entryPoints) {
		final String values[] = entryPoints.split(";");
		for (final String value : values) {
			this.ENTRYPOINTS.add(value.trim());
		}
	}
	
	
	/**
	 * 초기 함수
	 */
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		if (contains(filterConfig, "entryPoints")) {
			setEntryPoints(filterConfig.getInitParameter("entryPoints"));
		}
	}

}
