package com.mococo.microstrategy.sdk.prompt;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebDocumentInstance;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectsException;
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

/**
 * ReportCharger
 * @author mococo
 *
 */
public class ReportCharger {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(ReportCharger.class);

    
    /**
     * ReportCharger
     */
    public ReportCharger() {
    	logger.debug("ReportCharger");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("ReportCharger");
    }
	
    
    private static void chargePrompts(final Report report, final List<Map<String, Object>> customPromptList) throws ClassNotFoundException, InstantiationException, IllegalAccessException,  InvocationTargetException, WebObjectsException  {
    	final List<Prompt> promptList;
    	promptList = report.getPromptList();

    	if (customPromptList == null || customPromptList.isEmpty()) {
            return;
        }

        final Prompt promptArray[] = new Prompt[customPromptList.size()];
        for (final Map<String, Object> customPrompt : customPromptList) {
        	final Prompt prompt = PromptCharger.getChargedPrompt(makeObjConfig(customPrompt));
            promptArray[prompt.getPin()] = prompt;
        }
        
        /*
        for (final Prompt prompt : promptArray) {
            promptList.add(prompt);
        }
        */
        
        final List<Prompt> addPrompt = Arrays.asList(promptArray);
        promptList.addAll(addPrompt);
    }
    
    
    private static ObjectConfig makeObjConfig(final Map<String, Object> customPrompt) {
    	return new ObjectConfig(customPrompt);
    }
    
    
    private static void chargePrompts(final WebIServerSession session, final Report report, final WebPrompts prompts) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
    	final List<Prompt> promptList = report.getPromptList();
        
        if (promptList == null) {
            return;
        }

        /* 2024-01-04 mksong 수정 - 프롬프트 순서 체크 */
        for(int i=0; i<prompts.size(); i++) {
        	final WebPrompt prompt = prompts.get(i);
        	final String logTmp1 = prompt.getID().replaceAll("[\r\n]","");
            logger.debug("=> prompt.getID() : [{}]", logTmp1);
            
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
    
    
    private static void chargeReport(final WebIServerSession session, final Report report, final String objectId) {
        WebReportInstance reportInstance = null;

        try {
            reportInstance = MstrReportUtil.getFinishedReportInstance(session, objectId, -1, -1);

            if (reportInstance.getStatus() == EnumDSSXMLStatus.DssXmlStatusPromptXML) {
                chargePrompts(session, report, reportInstance.getPrompts());
            }
        } catch (WebObjectsException | InterruptedException | IndexOutOfBoundsException | ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
        	throw new SdkRuntimeException(e);
        } finally {
            MstrReportUtil.closeReport(reportInstance);
        }
        
//        logger.debug("=> reportInstance: [{}]", reportInstance);
    }
    
    
    private static void chargeDocument(final WebIServerSession session, final Report report, final String objectId) {
        WebDocumentInstance docuementInstance = null;

        try {
            docuementInstance = MstrReportUtil.getFinishedDocumentInstance(session, objectId, -1, -1);

            if (docuementInstance.getStatus() == EnumDSSXMLStatus.DssXmlStatusPromptXML) {
                chargePrompts(session, report, docuementInstance.getPrompts());
            }
        } catch (WebObjectsException | InterruptedException | IndexOutOfBoundsException | ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new SdkRuntimeException(e);
        } finally {
            MstrReportUtil.closeDocument(docuementInstance);
        }
    }
    
    
    /**
     * chargeObject
     * @param session
     * @param type
     * @param id
     * @return
     */
    public static Report chargeObject(final WebIServerSession session, final int type, final String objId) throws WebObjectsException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	final WebObjectInfo info = session.getFactory().getObjectSource().getObject(objId, type, true);
    	final Report report = new Report(info.getID(), info.getType(), info.getDisplayName());

        switch (type) {
	        case EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition:
	            chargeReport(session, report, objId);
	            break;
	        case EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition:
	            chargeDocument(session, report, objId);
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
        final ObjectConfig config = ConfigManager.getInstance().getObjectConfig(info, session);
        if (config != null) {
            chargePrompts(report, config.<List<Map<String, Object>>>get("customPromptList"));
        }

        return report;
    }

}
