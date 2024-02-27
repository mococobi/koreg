package com.custom.mstr.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.prompt.ReportCharger;
import com.mococo.microstrategy.sdk.prompt.vo.Report;
import com.mococo.microstrategy.sdk.util.MstrFolderBrowseUtil;
import com.mococo.microstrategy.sdk.util.MstrReportUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;
import com.mococo.web.util.PortalCodeUtil;

/**
 * MstrController
 * @author mococo
 *
 */
@Controller
@RequestMapping("/mstr/*")
public class MstrController {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(MstrController.class);

    
	/**
	 * MstrController
	 */
    public MstrController() {
    	logger.debug("MstrController");
    }
	
	
	/**
	 * 리포트 정보
	 * @param request
	 * @param params
	 * @return
	 */
    @PostMapping({"/mstr/getReportInfo.json", "/mstr/getEisReportInfo.json"})
    @ResponseBody
    public Map<String, Object> getReportInfo(final HttpServletRequest request, @RequestBody final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));

        final String objectId = (String) params.get("objectId");
        final int type = (int) params.get("type");
        final String uid = HttpUtil.getLoginUserId(request);

        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();

        WebIServerSession session = null;
        try {
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", CustomProperties.getProperty("mstr.server.name"));
			connData.put("project", params.get(PortalCodeUtil.projectNm) != null ? (String) params.get(PortalCodeUtil.projectNm) : CustomProperties.getProperty("mstr.default.project.name"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", uid);
			connData.put("trustToken", CustomProperties.getProperty("mstr.trust.token"));
			session = MstrUtil.connectTrustSession(connData);
            
            final Report report = ReportCharger.chargeObject(session, type, objectId);
//            logger.debug("==> report: [{}]", report);
            
            rtnMap.put("report", report);
        } catch (WebObjectsException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        	rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
        	logger.error("getReportInfo Exception", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        return rtnMap;
    }
    
    
    /**
     * 프롬프트 값 변환
     * @param request
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    @PostMapping({"/mstr/getAnswerXML.json", "/mstr/getEisAnswerXML.json"})
    @ResponseBody
    public Map<String, Object> getAnswerXML(final HttpServletRequest request, @RequestBody final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));

    	final String objectId = (String) params.get("objectId");
    	final int type = (int) params.get("type");
    	final String uid = HttpUtil.getLoginUserId(request);

        final Map<String, List<String>> promptVal = (Map<String, List<String>>) params.get("promptVal");

//        logger.debug("=> promptVal : [{}]", promptVal);

        Map<String, Object> success = ControllerUtil.getSuccessMap();

        WebIServerSession session = null;
        try {
        	final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", CustomProperties.getProperty("mstr.server.name"));
			connData.put("project", params.get(PortalCodeUtil.projectNm) != null ? (String) params.get(PortalCodeUtil.projectNm) : CustomProperties.getProperty("mstr.default.project.name"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", uid);
			connData.put("trustToken", CustomProperties.getProperty("mstr.trust.token"));
			session = MstrUtil.connectTrustSession(connData);
            
            final String xml = MstrReportUtil.getReportAnswerXML(session, objectId, type, promptVal);
            success.put("xml", xml);
        } catch (WebObjectsException | InterruptedException e) {
        	success = ControllerUtil.getFailMapMessage(e.getMessage());
        	logger.error("!!!", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        return success;
    }
    

    /**
     * 포탈 메뉴 리스트 조회하는 json 메소드
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping({"/mstr/getFolderList.json", "/mstr/getEisFolderList.json"})
    @ResponseBody
    public Map<String, Object> getFolderList(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
        Map<String, Object> success = ControllerUtil.getSuccessMap();

        WebIServerSession session = null;
        
        try {
        	final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", CustomProperties.getProperty("mstr.server.name"));
			connData.put("project", params.get(PortalCodeUtil.projectNm) != null ? (String) params.get(PortalCodeUtil.projectNm) : CustomProperties.getProperty("mstr.default.project.name"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", HttpUtil.getLoginUserId(request));
			connData.put("trustToken", CustomProperties.getProperty("mstr.trust.token"));
			session = MstrUtil.connectTrustSession(connData);
            
			final String folderId = (String) params.get("folderId");
            final List<Map<String, Object>> list = MstrFolderBrowseUtil.getFolderTree(session, folderId, -1, Arrays.asList(
                    EnumDSSXMLObjectTypes.DssXmlTypeFolder, EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition,
                    EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition, EnumDSSXMLObjectTypes.DssXmlTypeShortcut));

            success.put("folder", list);
        } catch (WebObjectsException e) {
        	success = ControllerUtil.getFailMapMessage(e.getMessage());
        	logger.error("!!! error", e);
        } finally {
            if (session != null) {
                try {
                    session.closeSession();
                } catch (WebObjectsException e) {
                	logger.error("!!! error", e);
                }
            }
        }
        return success;
    }

}
