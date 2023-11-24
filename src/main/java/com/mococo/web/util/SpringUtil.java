package com.mococo.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Component
public class SpringUtil implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringUtil.class);

    private static ApplicationContext context;

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static HttpServletRequest getCurrentRequest() {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return request;
    }

    public static Locale getCurrentLocale() {
        SessionLocaleResolver resolver = (SessionLocaleResolver) getBean("localeResolver");

        Locale locale = null;

        HttpServletRequest request = getCurrentRequest();

        if (request != null) {
            locale = resolver.resolveLocale(request);
        } else {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public static String getMessage(String id) {
        return getMessage(id, null, getCurrentLocale());
    }

    public static String getMessage(String id, Object[] args) {
        return getMessage(id, args, getCurrentLocale());
    }

    public static String getMessage(String id, Object[] args, Locale locale) {
        MessageSource messageSource = (MessageSource) context.getBean("messageSource");
        return messageSource.getMessage(id, args, locale);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
