package com.mococo.microstrategy.sdk.esm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.utils.MSTRUncheckedException;
import com.microstrategy.utils.StringUtils;
import com.microstrategy.utils.serialization.EnumWebPersistableState;
import com.microstrategy.web.app.AbstractExternalSecurity;
import com.microstrategy.web.beans.RequestKeys;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.platform.ContainerServices;
import com.mococo.microstrategy.sdk.esm.vo.MstrUser;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.PortalCodeUtil;

/**
 * MSTR ESM
 * @author mococo
 *
 */
public class CustomExternalSecurity extends AbstractExternalSecurity {
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(CustomExternalSecurity.class);
    
    
    /**
     * CustomExternalSecurity
     */
    public CustomExternalSecurity() {
    	super();
    	
    	final Boolean logCheck = false;
    	if(logCheck) {
    		logger.debug("CustomExternalSecurity");
    	}
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("CustomExternalSecurity");
    }
	
	
    /**
     * MSTR 인증 필요 시 이 클래스에서 최초로 호출되는 메서드로 인증가능한지 여부를 고려하여 앞으로 호출될 메서드를 결정.
     */
    @Override
    public int handlesAuthenticationRequest(final RequestKeys reqKeys, final ContainerServices cntSvcs, final int reason) {
    	int rtnPageNum = 3;
    	
    	if(rtnPageNum == PortalCodeUtil.NUMBER_3) {
//    		logger.debug("=> reqKeys:[{}]", reqKeys);
    		logger.debug("=> reason:[{}]", reason);
    	}
    	
    	final MstrUser mstrUser = getMstrUser(cntSvcs);
//    	logger.debug("=> mstrUser:[{}]", mstrUser);

        if (mstrUser == null) {
        	rtnPageNum = USE_CUSTOM_LOGIN_PAGE; // 오류 페이지로 이동
        } else {
        	final String server = getServer(reqKeys);
            final String project = getProject(reqKeys);
            final int port = getPort(reqKeys);

            final String logTmp1 = server.replaceAll(PortalCodeUtil.logChange,"");
            final String logTmp2 = project.replaceAll("[\r\n]","");
            logger.debug("=> server:[{}], project[{}]", logTmp1, logTmp2);

            // 서버가 지정되어 있지 않다면 오류 페이지(USE_CUSTOM_LOGIN_PAGE)로 이동
            if (StringUtils.isEmpty(server)) {
            	rtnPageNum = USE_CUSTOM_LOGIN_PAGE;
            } else {
            	 boolean connected = reconnectISession(cntSvcs, project, mstrUser.getId());

                 if (!connected) {
                     connected = connectISession(cntSvcs, server, project, port, mstrUser.getId());
                 }

                 if (connected) {
                 	// 성공 시 세션 생성 단계로 이동
                	 rtnPageNum = COLLECT_SESSION_NOW;
                 } else {
                 	 // 재접속/접속이 성공하지 않았다면 로그인 페이지로 이동
                	 rtnPageNum = USE_CUSTOM_LOGIN_PAGE;
                 }
            }
        }

        return rtnPageNum;
    }
    
    
    /**
     * getCustomLoginURL
     */
    @Override
    public String getCustomLoginURL(final String originalURL, final String desiredServer, final int desiredPort, final String desiredProject) {
        return getEsmFailUrl();
    }
    
    
    /**
     * getWebIServerSession
     */
    @Override
    public WebIServerSession getWebIServerSession(final RequestKeys reqKeys, final ContainerServices cntSvcs) {
        // handlesAuthenticationRequest에서 이미 생성된 세션을 재활용
    	final String project = getProject(reqKeys);

        try {
        	final String sessionState = getSessionState(cntSvcs, project);
            final WebIServerSession session = MstrUtil.reconnectSession(sessionState);
            final String newSessionState = session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO);

            if (StringUtils.isEqual(sessionState, newSessionState)) {
                setSessionState(cntSvcs, project, newSessionState);
            }

            return session;
        } catch (WebObjectsException e) {
            logger.error("!!! error", e);

            setSessionState(cntSvcs, project, null);
            throw new MSTRUncheckedException(e);
        }
    }
    
    
    // 세션의 생성
    private boolean connectISession(final ContainerServices cntSvcs, final String server, final String project, final int port, final String mstrUserId) {
        Boolean rtnCheck;
    	try {
        	final MstrUser user = getMstrUser(cntSvcs);

        	final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", server);
			connData.put("project", project);
			connData.put("port", port);
			connData.put("authMode", user.getAuthMode());
			connData.put("localeNum", getLocale());
			connData.put("uid", mstrUserId);
			connData.put("pwd", user.getPassword());
			connData.put("trustToken", getTrustToken());
			connData.put("clientId", getClientID(cntSvcs));
			final WebIServerSession session = MstrUtil.connectTrustSession(connData);

            setSessionState(cntSvcs, project, session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO));
            rtnCheck = true;
        } catch (WebObjectsException e) {
        	rtnCheck = false;
            logger.error("!!! error", e);
        }

        return rtnCheck;
    }
    
    
    // 접속 서버,프로젝트,포트,트러스트토큰 조회
    @Override
    protected String getServer(final RequestKeys reqKeys) {
        String server = super.getServer(reqKeys);

        if (StringUtils.isEmpty(server)) {
            server = CustomProperties.getProperty("mstr.server.name");
        }
        return server;
    }
    
    
    @Override
    protected int getPort(final RequestKeys reqKeys) {
        int port = super.getPort(reqKeys);
        if (port == 0) {
        	port = Integer.parseInt(CustomProperties.getProperty("mstr.server.port"));
        }

        return port;
    }
    
    
    @Override
    protected String getProject(final RequestKeys reqKeys) {
        String project = super.getProject(reqKeys);

        if (StringUtils.isEmpty(project)) {
            project = CustomProperties.getProperty("mstr.default.project.name");
        }

        return project;
    }
    
    
    private String getTrustToken() {
        return CustomProperties.getProperty("mstr.trust.token");
    }
    
    
    private String getEsmFailUrl() {
        return CustomProperties.getProperty("mstr.esm.fail.url");
    }
    
    
    private int getLocale() {
    	final String sLocale = CustomProperties.getProperty("mstr.session.locale");
        return Integer.parseInt(sLocale);
    }
    
    
    /**
     * getClientID
     * @param cntSvcs
     * @return
     */
    protected String getClientID(final ContainerServices cntSvcs) {
        return cntSvcs.getRemoteAddress();
    }
    
    
    private MstrUser getMstrUser(final ContainerServices cntSvcs) {
        return (MstrUser) cntSvcs.getSessionAttribute("mstr-user-vo");
    }
    
    
    // 각 프로젝트의 세션상태를 포함한 맵에서 프로젝트에 해당하는 세션상태를 조회
    private String getSessionState(final ContainerServices cntSvcs, final String project) {
    	final MstrUser mstrUser = getMstrUser(cntSvcs);
        return mstrUser == null ? null : mstrUser.getProjectSession(project);
    }
    
    
    // 각 프로젝트의 세션상태를 포함한 맵에 프로젝트의 세션상태를 지정
    private void setSessionState(final ContainerServices cntSvcs, final String project, final String sessionState) {
    	final MstrUser mstrUser = getMstrUser(cntSvcs);
        if (mstrUser != null) {
            mstrUser.setProjectSession(StringUtils.isEmpty(project) ? "-" : project, sessionState);
        }
    }
    
    
    // 관리되고 있던 모든 세션의 종료
    private void closeAllISession(final ContainerServices cntSvcs) {
    	final MstrUser mstrUser = getMstrUser(cntSvcs);

        if (mstrUser != null) {
            for (final String project : mstrUser.getProjectSet()) {
            	final String sessionState = mstrUser.getProjectSession(project);

                MstrUtil.closeISession(sessionState);
                mstrUser.removeMstrSession(project);
            }
        }
    }
    
    
    // 세션의 재활용을 위해 과거에 생성되고 이용되고 있던 모든 프로젝트의 세션 중 조회
    private boolean reconnectISession(final ContainerServices cntSvcs, final String project, final String mstrUserId) {
    	Boolean rtnCheck = false;
    	if(rtnCheck) {
    		logger.debug("reconnectISession");
    	}
    	
        final String sessionState = getSessionState(cntSvcs, project);

        // 파라미터로 전달된 프로젝트에 해당하는 세션상태가 있다면, 재활용 시도
        if (StringUtils.isNotEmpty(sessionState)) {
            final WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();
            session.restoreState(sessionState);

            // 사용자가 변경되었다면 기존 세션상태 모두 삭제
            if (StringUtils.isEqual(mstrUserId, session.getLogin())) {
            	
                // 프로젝트와 세션의 프로젝트가 같지 않을 경우 (접근될 수 없는 부분으로 다음 로그는 부적절 상태)
                if (StringUtils.isEqual(project, session.getProjectName())) {
                	
                    try {
                        if (!session.isAlive()) {
                            session.setTrustToken(getTrustToken());
                            session.reconnect();
                            setSessionState(cntSvcs, project, session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO));
                        }
                        
                    	rtnCheck = true;
                    } catch (WebObjectsException e) {
                        logger.error("!!! error", e);
                        rtnCheck = false;
                    }
                    
                } else {
                	final String logTmp1 = project.replaceAll("[\r\n]","");
                	final String logTmp2 = session.getProjectName().replaceAll("[\r\n]","");
                    logger.debug("=> invalide project, project session: [{}] [{}]", logTmp1, logTmp2);
                    rtnCheck = false;
                }
                
            } else {
                closeAllISession(cntSvcs);
                rtnCheck = false;
            }
        } else {
        	rtnCheck = false;
        }

        return rtnCheck;
    }
    
}
