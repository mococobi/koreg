package com.mococo.microstrategy.sdk.esm.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.microstrategy.webapi.EnumDSSXMLAuthModes;

public class MstrUser {
    private String id;
    private String password;
    private int authMode;
    private Map<String, String> mstrSession = new HashMap<String, String>();
    private String server;
    private int port;
    private String project;
    private String clientIp;
    
    public MstrUser(String id, String password) {
        this.id = id;
        this.password = password;
        this.authMode = EnumDSSXMLAuthModes.DssXmlAuthStandard;
    }

    public MstrUser(String id) {
        this.id = id;
        this.authMode = EnumDSSXMLAuthModes.DssXmlAuthSimpleSecurityPlugIn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAuthMode() {
        return authMode;
    }

    public void setAuthMode(int authMode) {
        this.authMode = authMode;
    }

    public void addMstrSession(String project, String usrSmgr) {
        mstrSession.put(project, usrSmgr);
    }

    public void removeMstrSession(String project) {
        mstrSession.remove(project);
    }

    public String getProjectSession(String project) {
        return mstrSession.get(project);
    }
    
    public Map<String, String> getMstrSession() {
        return mstrSession;
    }

    public void setProjectSession(String project, String sessionState) {
        mstrSession.put(project, sessionState);
    }

    public Set<String> getProjectSet() {
        return mstrSession.keySet();
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        return new StringBuffer()
    		.append("id:").append(id)
    		.append(", password:").append(password)
    		.append(", authMode:").append(authMode)
    		.append(", mstrSession:").append(mstrSession)
    		.toString();
    }
}
