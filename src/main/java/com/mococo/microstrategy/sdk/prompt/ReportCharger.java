package com.mococo.microstrategy.sdk.prompt;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebDocumentInstance;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.web.objects.WebPrompts;
import com.microstrategy.web.objects.WebReportInstance;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLStatus;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.config.ConfigManager;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;
import com.mococo.microstrategy.sdk.prompt.vo.Prompt;
import com.mococo.microstrategy.sdk.prompt.vo.Report;
import com.mococo.microstrategy.sdk.util.MstrReportUtil;

public class ReportCharger {
    private static final Logger logger = LoggerFactory.getLogger(ReportCharger.class);

    /*
     * private static void chargePrompts(Report report, List<Map<String, Object>>
     * promptIdList) throws Exception { List<Prompt> promptList =
     * report.getPromptList();
     * 
     * if (promptIdList == null || promptIdList.size() == 0) { return; }
     * 
     * Prompt promptArray[] = new Prompt[promptIdList.size()];
     * 
     * for (Map<String, Object> promptId : promptIdList) { String project =
     * (String)promptId.get("project"); String objectId =
     * (String)promptId.get("objectId"); Prompt prompt =
     * PromptCharger.getChargedPrompt(project, objectId);
     * promptArray[prompt.getPin()] = prompt; } for (Prompt prompt : promptArray) {
     * promptList.add(prompt); } }
     */

    private static void chargePrompts(Report report, List<Map<String, Object>> customPromptList) throws Exception {
        List<Prompt> promptList = report.getPromptList();

        if (customPromptList == null || customPromptList.size() == 0) {
            return;
        }

        Prompt promptArray[] = new Prompt[customPromptList.size()];

        for (Map<String, Object> customPrompt : customPromptList) {
            Prompt prompt = PromptCharger.getChargedPrompt(new ObjectConfig(customPrompt));

            promptArray[prompt.getPin()] = prompt;
        }
        for (Prompt prompt : promptArray) {
            promptList.add(prompt);
        }
    }

    private static void chargePrompts(WebIServerSession session, Report report, WebPrompts prompts)
            throws IndexOutOfBoundsException, Exception {
        List<Prompt> promptList = report.getPromptList();
        System.out.println("mksong : " + promptList.size());
        
        if (promptList == null) {
            return;
        }

        /* 2024-01-04 mksong 수정 - 프롬프트 순서 체크 */
        for(int i=0; i<prompts.size(); i++) {
        	WebPrompt prompt = prompts.get(i);
            logger.debug("=> id: [{}]", prompt.getID());
            
            promptList.add(PromptCharger.getChargedPrompt(session, prompt));
        }
        
        /*
        for (Enumeration<?> elem = prompts.elements(); elem.hasMoreElements();) {
            WebPrompt prompt = (WebPrompt) elem.nextElement();
            logger.debug("=> id: [{}]", prompt.getID());
            
            promptList.add(PromptCharger.getChargedPrompt(session, prompt));
        }
        */
    }

    private static void chargeReport(WebIServerSession session, Report report, String objectId) {
        WebReportInstance reportInstance = null;

        try {
            reportInstance = MstrReportUtil.getFinishedReportInstance(session, objectId, -1, -1);

            if (reportInstance.getStatus() == EnumDSSXMLStatus.DssXmlStatusPromptXML) {
                chargePrompts(session, report, reportInstance.getPrompts());
            }
        } catch (Exception e) {
//            throw new SdkRuntimeException("!!! error on charge report prompts.", e);
        	throw new SdkRuntimeException(e.getMessage());
        } finally {
            MstrReportUtil.closeReport(reportInstance);
        }
        logger.debug("=> reportInstance: [{}]", reportInstance);
    }

    private static void chargeDocument(WebIServerSession session, Report report, String objectId) {
        WebDocumentInstance docuementInstance = null;

        try {
            docuementInstance = MstrReportUtil.getFinishedDocumentInstance(session, objectId, -1, -1);

            if (docuementInstance.getStatus() == EnumDSSXMLStatus.DssXmlStatusPromptXML) {
                chargePrompts(session, report, docuementInstance.getPrompts());
            }
        } catch (Exception e) {
            throw new SdkRuntimeException("!!! error on charge document prompts.");
        } finally {
            MstrReportUtil.closeDocument(docuementInstance);
        }
    }

    public static Report chargeObject(WebIServerSession session, int type, String id) throws Exception {
        WebObjectInfo info = session.getFactory().getObjectSource().getObject(id, type, true);
        Report report = new Report(info.getID(), info.getType(), info.getDisplayName());

        switch (type) {
        case EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition:
            chargeReport(session, report, id);
            break;
        case EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition:
            chargeDocument(session, report, id);
            break;
        default:
        }

        /*
         * 예시: { ... "customPromptList": [{ "pin": 0, "objectId":
         * "customPromptReportSelector", "exTitle": "기간별 매출현황", "exUiType":
         * "reportList", "exExtUiType": { "dataSource": [ {"reportId":"",
         * "reportType":3, "displayName":"일별 매출현황"}, {"reportId":"", "reportType":55,
         * "displayName":"일별 매출현황"}, {"reportId":"", "reportType":55,
         * "displayName":"분기별 매출현황"} ] } }] ... }
         */

        // 리포트 가상 프롬프트에 대한 처리
        ObjectConfig config = ConfigManager.getInstance().getObjectConfig(info, session);
        if (config != null) {
            chargePrompts(report, config.<List<Map<String, Object>>>get("customPromptList"));
        }

        return report;
    }

}
