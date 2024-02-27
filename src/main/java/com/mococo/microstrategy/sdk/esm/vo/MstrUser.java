package com.mococo.microstrategy.sdk.esm.vo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.microstrategy.webapi.EnumDSSXMLAuthModes;

/**
 * MstrUser
 * @author mococo
 *
 */
public class MstrUser {
	
	/**
	 * 사용자 ID
	 */
    private String userId;
    
    /**
     * 사용자 PWD
     */
    private String password;
    
    /**
     * 권한 모드
     */
    private int authMode;
    
    /**
     * MSTR 세션
     */
    private final Map<String, String> mstrSession = new ConcurrentHashMap<>();
    
    /**
     * 서버명
     */
    private String server;
    
    /**
     * 포트 번호
     */
    private int port;
    
    /**
     * 프로젝트명
     */
    private String project;
    
    /**
     * 사용자 IP
     */
    private String clientIp;
    
    
    /**
     * MstrUser
     * @param userId
     * @param password
     */
    public MstrUser(final String userId, final String password) {
        this.userId = userId;
        this.password = password;
        this.authMode = EnumDSSXMLAuthModes.DssXmlAuthStandard;
    }
    
    
    /**
     * MstrUser
     * @param userId
     */
    public MstrUser(final String userId) {
        this.userId = userId;
        this.authMode = EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn;
    }
    
    
    /**
     * getId
     * @return
     */
    public String getId() {
        return userId;
    }
    
    
    /**
     * setId
     * @param id
     */
    public void setId(final String userId) {
        this.userId = userId;
    }
    
    
    /**
     * getPassword
     * @return
     */
    public String getPassword() {
        return password;
    }
    
    
    /**
     * setPassword
     * @param password
     */
    public void setPassword(final String password) {
        this.password = password;
    }
    
    
    /**
     * getAuthMode
     * @return
     */
    public int getAuthMode() {
        return authMode;
    }
    
    
    /**
     * setAuthMode
     * @param authMode
     */
    public void setAuthMode(final int authMode) {
        this.authMode = authMode;
    }
    
    
    /**
     * addMstrSession
     * @param project
     * @param usrSmgr
     */
    public void addMstrSession(final String project, final String usrSmgr) {
        mstrSession.put(project, usrSmgr);
    }
    
    
    /**
     * removeMstrSession
     * @param project
     */
    public void removeMstrSession(final String project) {
        mstrSession.remove(project);
    }
    
    
    /**
     * getProjectSession
     * @param project
     * @return
     */
    public String getProjectSession(final String project) {
        return mstrSession.get(project);
    }
    
    
    /**
     * getMstrSession
     * @return
     */
    public Map<String, String> getMstrSession() {
        return mstrSession;
    }
    
    
    /**
     * setProjectSession
     * @param project
     * @param sessionState
     */
    public void setProjectSession(final String project, final String sessionState) {
        mstrSession.put(project, sessionState);
    }
    
    
    /**
     * getProjectSet
     * @return
     */
    public Set<String> getProjectSet() {
        return mstrSession.keySet();
    }
    
    
    /**
     * getServer
     * @return
     */
    public String getServer() {
        return server;
    }
    
    
    /**
     * setServer
     * @param server
     */
    public void setServer(final String server) {
        this.server = server;
    }
    
    
    /**
     * getPort
     * @return
     */
    public int getPort() {
        return port;
    }
    
    
    /**
     * 
     * @param port
     */
    public void setPort(final int port) {
        this.port = port;
    }
    
    
    /**
     * setPort
     * @return
     */
    public String getProject() {
        return project;
    }
    
    
    /**
     * setPort
     * @param project
     */
    public void setProject(final String project) {
        this.project = project;
    }
    
    
    /**
     * getClientIp
     * @return
     */
    public String getClientIp() {
        return clientIp;
    }
    
    
    /**
     * setClientIp
     * @param clientIp
     */
    public void setClientIp(final String clientIp) {
        this.clientIp = clientIp;
    }
    
    
    /**
     * toString
     */
    @Override
    public String toString() {
        return new StringBuffer()
    		.append("id:").append(userId)
    		.append(", password:").append(password)
    		.append(", authMode:").append(authMode)
    		.append(", mstrSession:").append(mstrSession)
    		.toString();
    }
}
