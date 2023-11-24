package com.custom.main.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.main.service.MainService;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.esm.vo.MstrUser;
import com.mococo.microstrategy.sdk.util.MstrFolderBrowseUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;

@Service(value = "mainService")
public class MainServiceImpl implements MainService {

    final Logger LOGGER = LoggerFactory.getLogger(MainServiceImpl.class);
    
    @Override
    public List<Map<String, Object>> getCorpFolder(MstrUser pMstrUser) {
        
        List<Map<String, Object>> lmResult = new ArrayList<Map<String, Object>>();
        WebIServerSession webIServiceSession = null;
        try {
            String server = MstrUtil.getLiveServer(pMstrUser.getServer());
            String project = pMstrUser.getProject();
            String uid = pMstrUser.getId();
            String clientIp = pMstrUser.getClientIp();
            String folderId = CustomProperties.getProperty("mstr.menu.folder.id");
            String trustToken = CustomProperties.getProperty("mstr.trust.token");

//            webIServiceSession = MstrUtil.connectTrustSession(server, project, uid, trustToken, clientIp);
            lmResult = MstrFolderBrowseUtil.getFolderTree(webIServiceSession, folderId, 3, 
                    Arrays.asList(
                            EnumDSSXMLObjectTypes.DssXmlTypeFolder, 
                            /*EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition,*/
                            /*EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition,*/
                            EnumDSSXMLObjectTypes.DssXmlTypeShortcut
                    )
            );
            
        } catch (Exception e) {
            LOGGER.error("!!! error", e);
            throw new RuntimeException();
        } finally {
            if (webIServiceSession != null) {
                try {
                    webIServiceSession.closeSession();
                } catch (WebObjectsException e) {
                    LOGGER.error("!!! error", e);
                }
            }
        }
        
        return lmResult;
    }
    
    @Override
    public List<Map<String, Object>> getEquipmentProductFolder(MstrUser pMstrUser, String pFolderObjectId) {
        
        List<Map<String, Object>> lmResult = new ArrayList<Map<String, Object>>();
        
        WebIServerSession webIServiceSession = null;
        try {
            String server = MstrUtil.getLiveServer(pMstrUser.getServer());
            String project = pMstrUser.getProject();
            String uid = pMstrUser.getId();
            String clientIp = pMstrUser.getClientIp();
            String trustToken = CustomProperties.getProperty("mstr.trust.token");
            
//            webIServiceSession = MstrUtil.connectTrustSession(server, project, uid, trustToken, clientIp);
            lmResult = MstrFolderBrowseUtil.getFolderTree(webIServiceSession, pFolderObjectId, -1, 
                    Arrays.asList(
                            EnumDSSXMLObjectTypes.DssXmlTypeFolder, 
                            EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition,
                            EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition,
                            EnumDSSXMLObjectTypes.DssXmlTypeShortcut
                    )
            );
            
        } catch (Exception e) {
            LOGGER.error("!!! error", e);
            throw new RuntimeException();
        } finally {
            if (webIServiceSession != null) {
                try {
                    webIServiceSession.closeSession();
                } catch (WebObjectsException e) {
                    LOGGER.error("!!! error", e);
                }
            }
        }
        
        return lmResult;
    }

    @Override
    public List<Map<String, Object>> getEnergySavingFolder(MstrUser pMstrUser, String pFolderObjectId) {
        
        List<Map<String, Object>> lmResult = new ArrayList<Map<String, Object>>();
        
        WebIServerSession webIServiceSession = null;
        try {
            String server = MstrUtil.getLiveServer(pMstrUser.getServer());
            String project = pMstrUser.getProject();
            String uid = pMstrUser.getId();
            String clientIp = pMstrUser.getClientIp();
            String trustToken = CustomProperties.getProperty("mstr.trust.token");
            
//            webIServiceSession = MstrUtil.connectTrustSession(server, project, uid, trustToken, clientIp);
            lmResult = MstrFolderBrowseUtil.getFolderTree(webIServiceSession, pFolderObjectId, -1, 
                    Arrays.asList(
                            EnumDSSXMLObjectTypes.DssXmlTypeFolder, 
                            EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition,
                            EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition,
                            EnumDSSXMLObjectTypes.DssXmlTypeShortcut
                    )
            );
            
        } catch (Exception e) {
            LOGGER.error("!!! error", e);
            throw new RuntimeException();
        } finally {
            if (webIServiceSession != null) {
                try {
                    webIServiceSession.closeSession();
                } catch (WebObjectsException e) {
                    LOGGER.error("!!! error", e);
                }
            }
        }
        
        return lmResult;
    }
}
