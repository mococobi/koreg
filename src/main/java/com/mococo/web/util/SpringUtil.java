package com.mococo.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * SpringUtil
 * @author mococo
 *
 */
@Component
public class SpringUtil implements ApplicationContextAware {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpringUtil.class);
	
	/**
	 * context
	 */
    private static ApplicationContext context;
    
    
    /**
     * SpringUtil
     */
    public SpringUtil() {
    	logger.debug("SpringUtil");
    }
    
    
    /**
     * getBean
     * @param beanName
     * @return
     */
    public static Object getBean(final String beanName) {
        return context.getBean(beanName);
    }
    
    
    /**
     * getCurrentRequest
     * @return
     */
    public static HttpServletRequest getCurrentRequest() {
        HttpServletRequest request = null;
        
        if (RequestContextHolder.getRequestAttributes() != null) {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        
        return request;
    }
    
    
    /**
     * getCurrentLocale
     * @return
     */
    public static Locale getCurrentLocale() {
    	final SessionLocaleResolver resolver = (SessionLocaleResolver) getBean("localeResolver");
        final HttpServletRequest request = getCurrentRequest();

        Locale locale;
        if (request != null) {
            locale = resolver.resolveLocale(request);
        } else {
            locale = Locale.getDefault();
        }

        return locale;
    }
    
    
    /**
     * getMessage
     * @param msgId
     * @return
     */
    public static String getMessage(final String msgId) {
        return getMessage(msgId, null, getCurrentLocale());
    }
    
    
    /**
     * getMessage
     * @param msgId
     * @param args
     * @return
     */
    public static String getMessage(final String msgId, final Object... args) {
        return getMessage(msgId, args, getCurrentLocale());
    }
    
    
    /**
     * getMessage
     * @param msgId
     * @param args
     * @param locale
     * @return
     */
    public static String getMessage(final String msgId, final Object[] args, final Locale locale) {
    	final MessageSource messageSource = (MessageSource) context.getBean("messageSource");
        return messageSource.getMessage(msgId, args, locale);
    }
    
    
    /**
     * getApplicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }
    
    
    /**
     * setApplicationContext
     */
    @Override
    public void setApplicationContext(final ApplicationContext appContxt) {
        context = appContxt;
    }
}
