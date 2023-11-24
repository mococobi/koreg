package com.mococo.microstrategy.sdk.esm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class CustomExternalSecurity extends AbstractExternalSecurity {
    private static final Logger logger = LoggerFactory.getLogger(CustomExternalSecurity.class);

    // MSTR 인증 필요 시 이 클래스에서 최초로 호출되는 메서드로 인증가능한지 여부를 고려하여 앞으로 호출될 메서드를 결정.
    @Override
    public int handlesAuthenticationRequest(final RequestKeys reqKeys, final ContainerServices cntSvcs,
            final int reason) {
        logger.debug("=> reqKeys:[{}]", reqKeys);
        logger.debug("=> reason:[{}]", reason);

        MstrUser mstrUser = getMstrUser(cntSvcs);

        logger.debug("=> mstrUser:[{}]", mstrUser);

        if (mstrUser == null) {
            return USE_CUSTOM_LOGIN_PAGE; // 오류 페이지로 이동
        }

        String server = getServer(reqKeys);
        String project = getProject(reqKeys);
        int port = getPort(reqKeys);

        logger.debug("=> server:[{}], project[{}]", server, project);

        // 서버가 지정되어 있지 않다면 오류 페이지(USE_CUSTOM_LOGIN_PAGE)로 이동
        if (StringUtils.isEmpty(server)) {
            return USE_CUSTOM_LOGIN_PAGE;
        }

        boolean connected = reconnectISession(cntSvcs, project, mstrUser.getId());

        if (!connected) {
            connected = connectISession(cntSvcs, server, project, port, mstrUser.getId());
        }

        if (!connected) {
            // 재접속/접속이 성공하지 않았다면 로그인 페이지로 이동
            return USE_CUSTOM_LOGIN_PAGE;
        } else {
            // 성공 시 세션 생성 단계로 이동
            return COLLECT_SESSION_NOW;
        }
    }

    @Override
    public String getCustomLoginURL(final String originalURL, final String desiredServer, final int desiredPort,
            final String desiredProject) {
        return getEsmFailUrl();
    }

    @Override
    public WebIServerSession getWebIServerSession(final RequestKeys reqKeys, final ContainerServices cntSvcs) {
        // handlesAuthenticationRequest에서 이미 생성된 세션을 재활용
        String project = getProject(reqKeys);

        try {
            String sessionState = getSessionState(cntSvcs, project);

            WebIServerSession session = MstrUtil.reconnectSession(sessionState);

            String newSessionState = session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO);

            if (StringUtils.isEqual(sessionState, newSessionState)) {
                setSessionState(cntSvcs, project, newSessionState);
            }

            return session;
        } catch (WebObjectsException e) {
            logger.error("!!! error", e);

            setSessionState(cntSvcs, project, null);
            throw new RuntimeException(e);
        }
    }

    // 세션의 생성
    private boolean connectISession(ContainerServices cntSvcs, String server, String project, int port,
            String mstrUserId) {
        try {
            MstrUser user = getMstrUser(cntSvcs);

            WebIServerSession session = MstrUtil.connectSession(server, port, project, mstrUserId, user.getPassword(),
                    user.getAuthMode(), getLocale(), getTrustToken(), getClientID(cntSvcs));

            setSessionState(cntSvcs, project, session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO));
        } catch (Exception e) {
            logger.error("!!! error", e);

            return false;
        }

        return true;
    }

    // 접속 서버,프로젝트,포트,트러스트토큰 조회
    @Override
    protected String getServer(RequestKeys reqKeys) {
        String server = super.getServer(reqKeys);

        if (StringUtils.isEmpty(server)) {
            server = CustomProperties.getProperty("mstr.server.name");
        }
        return server;
    }

    @Override
    protected int getPort(RequestKeys reqKeys) {
        int port = super.getPort(reqKeys);
        if (port == 0) {
            try {
                port = Integer.parseInt(CustomProperties.getProperty("mstr.server.port"));
            } catch (Exception e) {
                port = 0;
            }
        }

        return port;
    }

    @Override
    protected String getProject(RequestKeys reqKeys) {
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
        String sLocale = CustomProperties.getProperty("mstr.session.locale");
        int locale = -1;

        try {
            locale = Integer.parseInt(sLocale);
        } catch (Exception e) {
            locale = 1033;
        }

        return locale;
    }

    protected String getClientID(ContainerServices cntSvcs) {
        return cntSvcs.getRemoteAddress();
    }

    private MstrUser getMstrUser(ContainerServices cntSvcs) {
        return (MstrUser) cntSvcs.getSessionAttribute("mstr-user-vo");
    }

    // 각 프로젝트의 세션상태를 포함한 맵에서 프로젝트에 해당하는 세션상태를 조회
    private String getSessionState(ContainerServices cntSvcs, String project) {
        MstrUser mstrUser = getMstrUser(cntSvcs);
        return mstrUser == null ? null : mstrUser.getProjectSession(project);
    }

    // 각 프로젝트의 세션상태를 포함한 맵에 프로젝트의 세션상태를 지정
    private void setSessionState(ContainerServices cntSvcs, String project, String sessionState) {
        MstrUser mstrUser = getMstrUser(cntSvcs);
        if (mstrUser != null) {
            mstrUser.setProjectSession(StringUtils.isEmpty(project) ? "-" : project, sessionState);
        }
    }

    // 관리되고 있던 모든 세션의 종료
    private void closeAllISession(ContainerServices cntSvcs) {
        MstrUser mstrUser = getMstrUser(cntSvcs);

        if (mstrUser != null) {
            for (String project : mstrUser.getProjectSet()) {
                String sessionState = mstrUser.getProjectSession(project);

                MstrUtil.closeISession(sessionState);
                mstrUser.removeMstrSession(project);
            }
        }
    }

    // 세션의 재활용을 위해 과거에 생성되고 이용되고 있던 모든 프로젝트의 세션 중 조회
    private boolean reconnectISession(ContainerServices cntSvcs, String project, String mstrUserId) {
        String sessionState = getSessionState(cntSvcs, project);

        // 파라미터로 전달된 프로젝트에 해당하는 세션상태가 있다면, 재활용 시도
        if (StringUtils.isNotEmpty(sessionState)) {
            final WebIServerSession session = WebObjectsFactory.getInstance().getIServerSession();
            session.restoreState(sessionState);

            // 사용자가 변경되었다면 기존 세션상태 모두 삭제
            if (!StringUtils.isEqual(mstrUserId, session.getLogin())) {
                closeAllISession(cntSvcs);
                return false;
            }

            // 프로젝트와 세션의 프로젝트가 같지 않을 경우 (접근될 수 없는 부분으로 다음 로그는 부적절 상태)
            if (!StringUtils.isEqual(project, session.getProjectName())) {
                logger.debug("=> invalide project, project session: [{}] [{}]", project, session.getProjectName());
                return false;
            }

            try {
                if (!session.isAlive()) {
                    session.setTrustToken(getTrustToken());
                    session.reconnect();
                    setSessionState(cntSvcs, project, session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO));
                }
            } catch (WebObjectsException e) {
                logger.error("!!! error", e);

                return false;
            }

            return true;
        }

        return false;

    }
}
