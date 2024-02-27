package com.mococo.microstrategy.sdk.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microstrategy.utils.localization.LocaleInfo;
import com.microstrategy.web.objects.WebCluster;
import com.microstrategy.web.objects.WebClusterAdmin;
import com.microstrategy.web.objects.WebElement;
import com.microstrategy.web.objects.WebElements;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.webapi.EnumDSSXMLApplicationType;
import com.microstrategy.webapi.EnumDSSXMLAuthModes;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.mococo.biz.exception.BizException;
import com.mococo.microstrategy.sdk.esm.vo.MstrUser;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.web.util.SpringUtil;

/**
 * MstrUtil
 * @author mococo
 *
 */
public class MstrUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(MstrUtil.class);
	
	/**
	 * authMode
	 */
	private static final String authMode = "authMode";
	
    /**
     * MstrUtil
     */
    public MstrUtil() {
    	logger.debug("MstrUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("MstrUtil");
    }
    
    
	/**
	 * getLiveServer
	 * @return
	 * @throws WebObjectsException
	 */
    public static String getLiveServer() throws WebObjectsException {
        return getLiveServer(null);
    }
    
    
    /**
     * getLiveServer
     * @param defaultServer
     * @return
     * @throws WebObjectsException
     */
    public static String getLiveServer(final String defaultServer) throws WebObjectsException {
        final WebClusterAdmin admin = WebObjectsFactory.getInstance().getClusterAdmin();
        admin.refreshAllClusters();
        String serverName = defaultServer;
        final Enumeration<WebCluster> clusters = admin.getClusters().elements();
        if (clusters.hasMoreElements()) {
        	final WebCluster cluster = clusters.nextElement();
            
            if(cluster.size() > 0) {
            	serverName = cluster.get(0).getNodeName();
            }

        }

        return serverName;
    }
    
    
    /**
     * connectSession
     */
    private static WebIServerSession connectSession(final Map<String, Object> connData) throws WebObjectsException {
    	final String server = (String) connData.get("server");
    	final String project = (String) connData.get("project");
    	final int port = (int) connData.get("port");
    	final int authMode = (int) connData.get(MstrUtil.authMode);
    	final int localeNum = (int) connData.get("localeNum");
    	final String uid = (String) connData.get("uid");
    	final String pwd = (String) connData.get("pwd");
    	final String trustToken = (String) connData.get("trustToken");
    	final String clientId = (String) connData.get("clientId");
    	
        final String serverName = getLiveServer(server);
        final String logTmp1 = server.replaceAll("[\r\n]","");
        logger.debug("server [{}]", logTmp1);

        if(StringUtils.isEmpty(serverName)) {
//            throw new RuntimeException(SpringUtil.getMessage("login.error.no.server"));
            throw new BizException(SpringUtil.getMessage("login.error.no.server"));
        }
        
        if(StringUtils.isEmpty(uid)) {
//        	throw new RuntimeException(SpringUtil.getMessage("login.error.no.id"));
        	throw new BizException(SpringUtil.getMessage("login.error.no.id"));
        }
        
        if(authMode == EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn && StringUtils.isEmpty(trustToken)) {
//        	throw new RuntimeException(SpringUtil.getMessage("login.error.no.token"));
        	throw new BizException(SpringUtil.getMessage("login.error.no.token"));
        }

        final WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();
        session.setServerName(serverName);
        session.setServerPort(port);
        if (StringUtils.isNotEmpty(project)) {
            session.setProjectName(project);
        }
        session.setLogin(uid);
        session.setAuthMode(authMode);

        switch (authMode) {
	        case EnumDSSXMLAuthModes.DssXmlAuthStandard:
	        case EnumDSSXMLAuthModes.DssXmlAuthLDAP:
	            session.setPassword(pwd);
	            break;
	        case EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn:
	            session.setTrustToken(trustToken);
	            break;
	        default:
	        	break;
        }

        final Locale locale = LocaleInfo.getInstance(localeNum).getLocale();
        session.setDisplayLocale(locale);
        session.setLocale(locale);
        session.setApplicationType(EnumDSSXMLApplicationType.DssXmlApplicationDSSWeb);
        //EnumDSSXMLApplicationType.DssXmlApplicationCustomApp
        if (StringUtils.isNotEmpty(clientId)) {
            session.setClientID(clientId);
        }
        session.getSessionID();

        return session;
    }
    
    
    /**
     * connectStandardSession
     * @param connData
     * @return
     * @throws WebObjectsException
     */
    public static WebIServerSession connectStandardSession(final Map<String, Object> connData) throws WebObjectsException {
    	if(connData.get(authMode) == null) {
    		connData.put(authMode, EnumDSSXMLAuthModes.DssXmlAuthStandard);
    	} else {
    		connData.put(authMode, connData.get(authMode));
    	}
    	
        return connectSession(connData);
    }
    
    
    /**
     * connectTrustSession
     * @param connData
     * @return
     * @throws WebObjectsException
     */
    public static WebIServerSession connectTrustSession(final Map<String, Object> connData) throws WebObjectsException {
    	if(connData.get(authMode) == null) {
    		connData.put(authMode, EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn);
    	} else {
    		connData.put(authMode, connData.get(authMode));
    	}
    	
        return connectSession(connData);
    }
    
    
    /**
     * reconnectSession
     * @param sessionState
     * @return
     * @throws WebObjectsException
     */
    public static WebIServerSession reconnectSession(final String sessionState) throws WebObjectsException {
    	WebIServerSession rtnSession = null; 
    			
        if (!StringUtils.isEmpty(sessionState)) {
        	rtnSession = WebObjectsFactory.getInstance().getIServerSession();

        	rtnSession.restoreState(sessionState);
            if (!rtnSession.isAlive()) {
            	rtnSession.reconnect();
            }
        }
        
        return rtnSession;
    }
    
    
    /**
     * logWebElements
     * @param webElements
     * @return
     */
    public static String logWebElements(final WebElements webElements) {
    	final StringBuilder builder = new StringBuilder(200);
    	final Enumeration<WebElement> enumWeb = webElements.elements();
        
        builder.append('{');
        while (enumWeb.hasMoreElements()) {
        	final WebElement webElement = enumWeb.nextElement();
            builder.append("id:").append(webElement.getID())
            	.append(", elementId:").append(webElement.getElementID())
            	.append(", displayName:").append(webElement.getDisplayName());
        }
        builder.append('}');

        return builder.toString();

    }
    
    
    /**
     * getLongDesc
     * @param object
     * @param session
     * @return
     */
    public static Map<String, Object> getLongDesc(final WebIServerSession session, final WebObjectInfo object) {
    	final WebObjectSource source = session.getFactory().getObjectSource();
        source.setFlags(source.getFlags() | EnumDSSXMLObjectFlags.DssXmlObjectComments);

        Map<String, Object> configInfo = null;
        if (object != null) {
            try {
            	final WebObjectInfo info = source.getObject(object.getID(), object.getType(), true);

                if (info.getComments() != null && info.getComments().length > 0) {
                	final String json = info.getComments()[0];

                    if (StringUtils.isNotEmpty(json)) {
                        configInfo = new ObjectMapper().readValue(json, new TypeReference<>() {});
                    }
                }
            } catch (WebObjectsException | IllegalArgumentException e) {
            	logger.error("!!! error", e);
            } catch (IOException e) {
            	logger.error("!!! json [{}] parsing error", e);
            }

        }

        return configInfo;
    }
    
    
	/**
	 * 세션 상태 추출
	 * @param session
	 * @return
	 */
    public static Map<String, String> getSessionStateMap(final HttpSession session) {
    	Map<String, String> rtnMap = new ConcurrentHashMap<>();
    	
    	final MstrUser mstrUserVo = (MstrUser)session.getAttribute("mstr-user-vo");
    	if(mstrUserVo != null) {
    		rtnMap = mstrUserVo.getMstrSession();
    	}
    	
		return rtnMap; 
	}
    
    
    /**
     * closeISession
     * @param wss
     * @throws SdkRuntimeException
     */
    public static void closeISession(final WebIServerSession wss) {
        try {
            if (wss != null) {
            	wss.closeSession();
            }
        } catch (WebObjectsException e) {
            if (e.getErrorCode() != -2_147_205_069) {
            	logger.error("error !!!", e);
            }
        }
    }
    
    
    /**
     * closeISession
     * @param sessionState
     */
    public static void closeISession(final String sessionState) {
        try {
        	final WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();
            session.restoreState(sessionState);

            if (session.isAlive()) {
                session.closeSession();
            }
        } catch (WebObjectsException e) {
        	logger.error("!!! error", e);
        }
    }
    
    
	/**
	 * MSTR 유저 세션 초기화
	 * @param session
	 * @param userId
	 * @return
	 */
	public static Boolean cleanOtherUserMstrSession(final HttpSession httpSession, final String userId) {
		Boolean cleanCheck = true;
		
		final Map<String, String> sessionStateMap = getSessionStateMap(httpSession);
//		logger.debug("=> sessionStateMap:[{}]", sessionStateMap);
		
		if (sessionStateMap != null) {
			for (final String project : sessionStateMap.keySet()) {
				final String sessionState = sessionStateMap.get(project);
	            final WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();

	            try {
	            	session.restoreState(sessionState);
	            	
					if (!session.isAlive()) { 
						session.reconnect();
					}

					if (!StringUtils.equalsIgnoreCase(userId, session.getUserInfo().getAbbreviation())) {
						closeISession(sessionState);
						final String logTmp1 = project.replaceAll("[\r\n]","");
						logger.debug("=> close session:[{}]", logTmp1);
					}
				} catch (WebObjectsException e) {
					logger.error("!!! error", e);
					cleanCheck = false;
				}
			}
		}
		
		return cleanCheck;
	}
	
	
	/**
	 * MSTR 세션 초기화
	 * @param session
	 */
	public static void cleanMstrSession(final HttpSession session) {
		final Map<String, String> sessionStateMap = getSessionStateMap(session);
//		logger.debug("=> sessionStateMap:[{}]", sessionStateMap);
		
		if (sessionStateMap != null) {
			for (final String project : sessionStateMap.keySet()) {
				
				final String logTmp1 = project.replaceAll("[\r\n]","");
				logger.debug("=> close session:[{}]", logTmp1);
				
				final String sessionState = sessionStateMap.get(project);
				closeISession(sessionState);
			}
		}		
	}

}