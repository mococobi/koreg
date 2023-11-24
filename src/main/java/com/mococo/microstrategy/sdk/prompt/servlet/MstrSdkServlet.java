package com.mococo.microstrategy.sdk.prompt.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.cache.CacheManager;
import com.mococo.microstrategy.sdk.prompt.cache.CacheManager.CacheObjectType;
import com.mococo.microstrategy.sdk.prompt.config.MstrSessionProvider;
import com.mococo.microstrategy.sdk.prompt.prop.PropManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

/**
 * Servlet implementation class MstrSdkServlet
 */
@WebServlet("*.mstr")

public class MstrSdkServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MstrSdkServlet.class);
    private static MstrSessionProvider mstrSessionProvider = null;

    @Override
    public void init() throws ServletException {
        super.init();

        Map<String, String> providerConfig = PropManager.<String>getProp("mstrSessionProvider");
        String providerName = providerConfig.get("className");
        try {
            Class<?> clazz = MstrSdkServlet.class.getClassLoader().loadClass(providerName);
            mstrSessionProvider = (MstrSessionProvider) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("!!! error load Mstr Session Provider", e);
        }
    }

    private static Set<Class> getClasses(String packageName) {
        Set<Class> classes = new HashSet<Class>();
        String packageNameSlash = "./" + packageName.replace(".", "/");
        URL directoryURL = Thread.currentThread().getContextClassLoader().getResource(packageNameSlash);
        if (directoryURL == null) {
            System.err.println("Could not retrive URL resource : " + packageNameSlash);
            return null;
        }

        String directoryString = directoryURL.getFile();
        if (directoryString == null) {
            System.err.println("Could not find directory for URL resource : " + packageNameSlash);
            return null;
        }

        File directory = new File(directoryString);
        if (directory.exists()) {
            String[] files = directory.list();
            for (String fileName : files) {

                if (fileName.endsWith(".class")) {
                    fileName = fileName.substring(0, fileName.length() - 6); // remove .class
                    try {
                        Class c = Class.forName(packageName + "." + fileName);
                        if (!Modifier.isAbstract(c.getModifiers())) // add a class which is not abstract
                            classes.add(c);
                    } catch (ClassNotFoundException e) {
                        System.err.println(packageName + "." + fileName + " does not appear to be a valid class");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println(packageName + " does not appear to exist as a valid package on the file system.");
        }

        return classes;
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MstrSdkServlet() {
        super();
    }

    private RequestHandler<?> getHandler(String serviceId) {
        RequestHandler<?> handler = null;
        String handlerClassName = "";
        logger.debug(" ******************** getHandler()");

        try {
            Map<String, String> requestHandlerConfig = PropManager.<String>getProp("requestHandler");
            Set<Class> classs = getClasses("com.mococo.microstrategy.sdk.prompt.servlet.impl");
            for (Class<?> clazz : classs) {
//    	        	 
                if (clazz.isAnnotationPresent(ServiceId.class)) {
                    ServiceId serviceId2 = clazz.getAnnotation(ServiceId.class);
                    if (serviceId.equals(serviceId2.id())) {
                        handlerClassName = clazz.getName();
                    }
//    	                  strFruitName = strFruitName + serviceId.value();
//    	                  System.out.println(strFruitName);
                } else {
                    Method[] methodList = clazz.getDeclaredMethods();
                    for (Method method : methodList) {
                        Annotation[] annotations = method.getDeclaredAnnotations();
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof ServiceId) {
                                ServiceId serviceId2 = (ServiceId) annotation;

                                logger.debug(" ******************** serviceId2()");
                                logger.debug(method.getName());
                                logger.debug(serviceId2.id());
                                if (serviceId.equals(serviceId2.id())) {
                                    handlerClassName = method.getName();
                                    logger.debug(handlerClassName);

                                }
                            }
                        }
                    }
                }

//    	        	
            }

//    			String handlerClassName = (String)requestHandlerConfig.get(serviceId);

            handler = CacheManager.<RequestHandler<?>>getCache(
                    CacheManager.getCacheItemId(CacheObjectType.REQUEST_HANDLER, handlerClassName));
            if (handler == null) {
                Class<?> clazz = MstrSdkServlet.class.getClassLoader().loadClass(handlerClassName);
                handler = (RequestHandler<?>) clazz.newInstance();
                CacheManager.<RequestHandler<?>>setCache(
                        CacheManager.getCacheItemId(CacheObjectType.REQUEST_HANDLER, handlerClassName), handler);
            }
        } catch (Exception e) {
            logger.error("!!! RequestHandler instantiate error.", e);
        }

        return handler;
    }

    private String getSuccessResponseString(Object object)
            throws JsonGenerationException, JsonMappingException, IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("isOk", true);
        result.put("data", object);
        return new ObjectMapper().writeValueAsString(result);
    }

    private String getFailResponseString(Exception e)
            throws JsonGenerationException, JsonMappingException, IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("isOk", false);
        result.put("message", e.getMessage());
        return new ObjectMapper().writeValueAsString(result);
    }

    private void writeResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder builder = new StringBuilder();

        String serviceId = request.getServletPath();
        serviceId = serviceId.replace("/", "");
        serviceId = serviceId.replace(".mstr", "");

        logger.debug("*************************");
        logger.debug(request.getServletPath());

        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            Map<String, Object> param = null;
            RequestHandler<?> handler = null;
//			String serviceId = null;
            if (StringUtils.isNotEmpty(builder.toString())) {
                param = new ObjectMapper().readValue(builder.toString(), new TypeReference<Map<String, Object>>() {
                });
                logger.debug("==> param: [{}]", param);
//				serviceId = (String)param.get("serviceId");

            }
            handler = getHandler(serviceId);
            if (handler != null) {
                writeResponse(response, getSuccessResponseString(handler.GetResponse(mstrSessionProvider, param)));
            } else {
                writeResponse(response, getFailResponseString(
                        new SdkRuntimeException("request handler not found, serviceId: [" + serviceId + "]")));
            }
        } catch (Exception e) {
            logger.error("!!! error ", e);
            writeResponse(response, getFailResponseString(e));
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
