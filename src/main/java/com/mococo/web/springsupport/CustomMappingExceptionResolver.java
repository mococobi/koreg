package com.mococo.web.springsupport;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mococo.biz.exception.BizException;
import com.mococo.web.util.HttpUtil;

/**
 * CustomMappingExceptionResolver
 * @author mococo
 *
 */
public class CustomMappingExceptionResolver extends SimpleMappingExceptionResolver implements MessageSourceAware {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(CustomMappingExceptionResolver.class);
    
    /**
     * ERR_ATT_CODE
     */
    private static final String ERR_ATT_CODE = "code"; // 모델에 포함될 오류코드 애트리뷰트 명
    
    /**
     * ERR_ATT_MSG
     */
    private static final String ERR_ATT_MSG = "message"; // 모델에 포함될 오류메세지 애트리뷰트 명
    
    /**
     * messageSource
     */
    private MessageSource messageSource;
    
    /**
     * defaultCode
     */
    private String defaultCode;
    
    
    /**
     * CustomMappingExceptionResolver
     */
    public CustomMappingExceptionResolver() {
    	super();
    	logger.debug("CustomMappingExceptionResolver");
    }
    
    
    /**
     * setMessageSource
     */
    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    
    public void setDefaultCode(final String defaultCode) {
        this.defaultCode = defaultCode;
    }
    
    
    public String getDefaultCode() {
        return this.defaultCode;
    }
    
    
    private String getMessage(final String code, final Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }
    
    
    private String getCode(final Exception excp) {
        String result = this.getDefaultCode();

        if (excp instanceof BizException) {
            final String code = ((BizException) excp).getCode();
            if (StringUtils.isNotEmpty(code)) {
                result = code;
            }
        }

        return result;
    }
    
    
    /**
     * json type으로 반환해야 할 경우 ModelAndView를 생성하여 처리, json이 아닌경우는 Super에서 처리
     */
    @Override
    protected ModelAndView doResolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception excp) {
    	ModelAndView rtnView;
    	
        logger.debug("=> 예외 발생: [{}]", excp);
        
        if (!HttpUtil.isJsonRequest(request)) {
            return super.doResolveException(request, response, handler, excp);
        }

        if (HttpUtil.isJsonRequest(request)) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			final ModelAndView mnv = new ModelAndView(new MappingJackson2JsonView());
			final String code = getCode(excp);
			final String msg = getMessage(code, request.getLocale());
			
			mnv.addObject(ERR_ATT_CODE, code);
			mnv.addObject(ERR_ATT_MSG, msg);
			
			rtnView = mnv;
        } else {
        	rtnView = super.doResolveException(request, response, handler, excp);
        }
        
        return rtnView;
    }
}
