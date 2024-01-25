package com.custom.mstr.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

@Controller
@RequestMapping("/mstr/*")
public class MstrController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MstrController.class);

    
    @RequestMapping(value = {"/mstr/getReportInfo.json", "/mstr/getEisReportInfo.json"}, method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> getReportInfo(final HttpServletRequest request, @RequestBody final Map<String, Object> params) {
    	LOGGER.debug("=> param : [{}]", params);

        String objectId = (String) params.get("objectId");
        int type = (int) params.get("type");
        String uid = HttpUtil.getLoginUserId(request);

        Map<String, Object> rtnMap = ControllerUtil.getSuccessMap();

        WebIServerSession session = null;
        try {
            session = MstrUtil.connectTrustSession(
        		  CustomProperties.getProperty("mstr.server.name")
        		, params.get("projectNm") != null ? (String) params.get("projectNm") : CustomProperties.getProperty("mstr.default.project.name")
        		, Integer.parseInt(CustomProperties.getProperty("mstr.server.port"))
        		, Integer.parseInt(CustomProperties.getProperty("mstr.session.locale"))
                , uid
                , CustomProperties.getProperty("mstr.trust.token")
            );
            Report report = ReportCharger.chargeObject(session, type, objectId);

            LOGGER.debug("==> report: [{}]", report);

            rtnMap.put("report", report);
        } catch (WebObjectsException e) {
        	rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("getReportInfo WebObjectsException", e);
        } catch (Exception e) {
        	rtnMap = ControllerUtil.getFailMapMessage(e.getMessage());
			LOGGER.error("getReportInfo Exception", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        return rtnMap;
    }
    

    @SuppressWarnings("unchecked")
	@RequestMapping(value = {"/mstr/getAnswerXML.json", "/mstr/getEisAnswerXML.json"}, method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> getAnswerXML(final HttpServletRequest request, @RequestBody final Map<String, Object> params) {
    	LOGGER.debug("=> param : [{}]", params);

        String objectId = (String) params.get("objectId");
        int type = (int) params.get("type");
        String uid = HttpUtil.getLoginUserId(request);

        Map<String, List<String>> promptVal = (Map<String, List<String>>) params.get("promptVal");

        LOGGER.debug("=> promptVal : [{}]", promptVal);

        Map<String, Object> success = ControllerUtil.getSuccessMap();

        WebIServerSession session = null;
        try {
            session = MstrUtil.connectTrustSession(
        		  CustomProperties.getProperty("mstr.server.name")
        		, params.get("projectNm") != null ? (String) params.get("projectNm") : CustomProperties.getProperty("mstr.default.project.name")
        		, Integer.parseInt(CustomProperties.getProperty("mstr.server.port"))
        		, Integer.parseInt(CustomProperties.getProperty("mstr.session.locale"))
                , uid
                , CustomProperties.getProperty("mstr.trust.token")
            );
            String xml = MstrReportUtil.getReportAnswerXML(session, objectId, type, promptVal);
            success.put("xml", xml);
        } catch (WebObjectsException e) {
        	success = ControllerUtil.getFailMapMessage(e.getMessage());
        	LOGGER.error("!!! error", e);
        } catch (Exception e) {
        	success = ControllerUtil.getFailMapMessage(e.getMessage());
        	LOGGER.error("!!! error", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        return success;
    }
    

    // 포탈 메뉴 리스트 조회하는 json 메소드
    @RequestMapping(value = {"/mstr/getFolderList.json", "/mstr/getEisFolderList.json"}, method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> getFolderList(HttpServletRequest request, HttpServletResponse response, @RequestBody final Map<String, Object> params) {
    	LOGGER.debug("params : [{}]", params);
        Map<String, Object> success = ControllerUtil.getSuccessMap();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        WebIServerSession session = null;
        
        try {
            String server = MstrUtil.getLiveServer(CustomProperties.getProperty("mstr.server.name"));
            String project = params.get("projectNm") != null ? (String) params.get("projectNm") : CustomProperties.getProperty("mstr.default.project.name");
            int port = Integer.parseInt(CustomProperties.getProperty("mstr.server.port"));
            int locale = Integer.parseInt(CustomProperties.getProperty("mstr.session.locale"));
            String uid = HttpUtil.getLoginUserId(request);
            String folderId = (String) params.get("folderId");
            String trustToken = CustomProperties.getProperty("mstr.trust.token");

            session = MstrUtil.connectTrustSession(server, project, port, locale, uid, trustToken);
            list = MstrFolderBrowseUtil.getFolderTree(session, folderId, -1, Arrays.asList(
                    EnumDSSXMLObjectTypes.DssXmlTypeFolder, EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition,
                    EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition, EnumDSSXMLObjectTypes.DssXmlTypeShortcut));

            success.put("folder", list);
        } catch (Exception e) {
        	success = ControllerUtil.getFailMapMessage(e.getMessage());
        	LOGGER.error("!!! error", e);
        } finally {
            if (session != null) {
                try {
                    session.closeSession();
                } catch (WebObjectsException e) {
                	LOGGER.error("!!! error", e);
                }
            }
        }
        return success;
    }

}
