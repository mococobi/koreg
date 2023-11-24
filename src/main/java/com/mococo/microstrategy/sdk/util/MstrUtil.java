package com.mococo.microstrategy.sdk.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

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
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.web.util.SpringUtil;

public class MstrUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MstrUtil.class);

    private MstrUtil() {
    }

    public static String getLiveServer() throws WebObjectsException {
        return getLiveServer(null);
    }

    public static String getLiveServer(String defaultServer) throws WebObjectsException {
        WebClusterAdmin admin = WebObjectsFactory.getInstance().getClusterAdmin();
        admin.refreshAllClusters();
        String serverName = defaultServer;
        Enumeration<WebCluster> clusters = admin.getClusters().elements();
        if (clusters.hasMoreElements()) {
            WebCluster cluster = clusters.nextElement();
            serverName = cluster.get(0).getNodeName();

        }

        return serverName;
    }

    public static WebIServerSession connectSession(String server, int port, String project, String uid, String pwd,
            int authMode, int localeNum, String trustToken, String clientId) throws WebObjectsException {
        WebIServerSession session = null;

        String serverName = getLiveServer(server);
        LOGGER.debug("server [{}]", server);

        if(StringUtils.isEmpty(serverName)) {
            throw new RuntimeException(SpringUtil.getMessage("login.error.no.server"));
        }
        
        if(StringUtils.isEmpty(uid)) {
        	throw new RuntimeException(SpringUtil.getMessage("login.error.no.id"));
        }
        
        if(authMode == EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn && StringUtils.isEmpty(trustToken)) {
        	throw new RuntimeException(SpringUtil.getMessage("login.error.no.token"));
        }

        session = WebObjectsFactory.getInstance().getIServerSession();
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
        }

        Locale locale = LocaleInfo.getInstance(localeNum).getLocale();
        session.setDisplayLocale(locale);
        session.setLocale(locale);
        session.setApplicationType(EnumDSSXMLApplicationType.DssXmlApplicationDSSWeb);
        // session.setApplicationType(com.microstrategy.webapi.EnumDSSXMLApplicationType.DssXmlApplicationCustomApp);
        if (StringUtils.isNotEmpty(clientId)) {
            session.setClientID(clientId);
        }
        session.getSessionID();

        return session;
    }

    public static WebIServerSession connectSession(String server, String project, String uid, String pwd) throws WebObjectsException {
        return connectSession(server, 0, project, uid, pwd, EnumDSSXMLAuthModes.DssXmlAuthStandard, 1042, null, null);
    }

    public static WebIServerSession connectSession(String server, String project, int port, String uid, String pwd) throws WebObjectsException {
        return connectSession(server, port, project, uid, pwd, EnumDSSXMLAuthModes.DssXmlAuthStandard, 1042, null,
                null);
    }

    public static WebIServerSession connectSession(String server, String project, int port, String uid, String pwd, String clientId) throws WebObjectsException {
        return connectSession(server, port, project, uid, pwd, EnumDSSXMLAuthModes.DssXmlAuthStandard, 1042, null, clientId);
    }

    public static WebIServerSession connectTrustSession(String server, String project, String uid, String trustToken) throws WebObjectsException {
        return connectSession(server, 0, project, uid, null, EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn, 1042,
                trustToken, null);
    }

    public static WebIServerSession reconnectSession(String sessionState) throws WebObjectsException {
        if (StringUtils.isEmpty(sessionState)) {
            return null;
        }

        WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();

        session.restoreState(sessionState);
        if (!session.isAlive()) {
            session.reconnect();
        }

        return session;
    }

    public static void closeISession(WebIServerSession wss) throws SdkRuntimeException {
        try {
            if (wss != null)
                wss.closeSession();
        } catch (WebObjectsException e) {
            if (e.getErrorCode() != -2147205069) {
                LOGGER.error("error !!!", e);
            }
        } catch (Exception e) {
            LOGGER.error("error !!!", e);
            throw new SdkRuntimeException(e);
        }
    }

    public static void closeISession(String sessionState) {
        try {
            WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();
            session.restoreState(sessionState);

            if (session.isAlive()) {
                session.closeSession();
            }
        } catch (WebObjectsException e) {
        	LOGGER.error("!!! error", e);
        }
    }

    public static String logWebElements(WebElements webElements) {
        StringBuilder builder = new StringBuilder();
        Enumeration<WebElement> e = webElements.elements();

        builder.append("{");
        while (e.hasMoreElements()) {
            WebElement webElement = e.nextElement();
            builder.append("id:").append(webElement.getID()).append(", elementId:").append(webElement.getElementID())
                    .append(", displayName:").append(webElement.getDisplayName());
        }
        builder.append("}");

        return builder.toString();

    }

    public static Map<String, Object> getLongDesc(WebObjectInfo object, WebIServerSession session) {
        WebObjectSource source = session.getFactory().getObjectSource();
        source.setFlags(source.getFlags() | EnumDSSXMLObjectFlags.DssXmlObjectComments);

        Map<String, Object> configInfo = null;
        if (object != null) {
            try {
                WebObjectInfo info = source.getObject(object.getID(), object.getType(), true);

                if (info.getComments() != null && info.getComments().length > 0) {
                    String json = info.getComments()[0];

                    if (StringUtils.isNotEmpty(json)) {
                        configInfo = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
                        });
                    }
                }
            } catch (WebObjectsException | IllegalArgumentException e) {
            	LOGGER.error("!!! error", e);
            } catch (IOException e) {
            	LOGGER.error("!!! json [{}] parsing error", e);
            }

        }

        return configInfo;
    }
    
    
	/**
	 * 세션 상태 추출
	 * @param session
	 * @return
	 */
    public static Map<String, String> getSessionStateMap(HttpSession session) {
		return (Map<String, String>)session.getAttribute("mstrSessionStateMap"); 
	}
	
	
	/**
	 * MSTR 유저 세션 초기화
	 * @param session
	 * @param userId
	 * @return
	 */
	public static Boolean cleanOtherUserMstrSession(HttpSession session, String userId) {
		Boolean cleanCheck = true;
		
		Map<String, String> sessionStateMap = getSessionStateMap(session);
		
		LOGGER.debug("=> sessionStateMap:[{}]", sessionStateMap);
		
		if (sessionStateMap != null) {
			for (String project : sessionStateMap.keySet()) {
				String sessionState = sessionStateMap.get(project);
				
	            final WebIServerSession isession = WebObjectsFactory.getInstance().getIServerSession();

	            try {
	            	isession.restoreState(sessionState);
	            	
					if (!isession.isAlive()) { 
					    isession.reconnect();
					}

					// LOGGER.debug("=> userId:[{}], session userId:[{}]", userId, isession.getUserInfo().getAbbreviation());
					if (!StringUtils.equalsIgnoreCase(userId, isession.getUserInfo().getAbbreviation())) {
						MstrUtil.closeISession(sessionState);			
						LOGGER.debug("=> close session:[{}]", project);
					}
				} catch (WebObjectsException e) {
					LOGGER.error("!!! error", e);
					cleanCheck = false;
					return cleanCheck;
				}
				
			}
		}
		return cleanCheck;
	}
	
	
	/**
	 * MSTR 세션 초기화
	 * @param session
	 */
	public static void cleanMstrSession(HttpSession session) {
		Map<String, String> sessionStateMap = getSessionStateMap(session);
		
		LOGGER.debug("=> sessionStateMap:[{}]", sessionStateMap);
		
		if (sessionStateMap != null) {
			for (String project : sessionStateMap.keySet()) {
				LOGGER.debug("=> close session:[{}]", project);
				String sessionState = sessionStateMap.get(project);
				
				MstrUtil.closeISession(sessionState);			
			}
		}		
	}

}