package com.mococo.microstrategy.sdk.util;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.EnumWebPromptType;
import com.microstrategy.web.objects.WebAttribute;
import com.microstrategy.web.objects.WebConstantPrompt;
import com.microstrategy.web.objects.WebDocumentInstance;
import com.microstrategy.web.objects.WebDocumentSource;
import com.microstrategy.web.objects.WebElement;
import com.microstrategy.web.objects.WebElements;
import com.microstrategy.web.objects.WebElementsPrompt;
import com.microstrategy.web.objects.WebExpression;
import com.microstrategy.web.objects.WebFilter;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebObjectsPrompt;
import com.microstrategy.web.objects.WebOperatorNode;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.web.objects.WebPrompts;
import com.microstrategy.web.objects.WebReportInstance;
import com.microstrategy.web.objects.WebReportManipulation;
import com.microstrategy.web.objects.WebReportSource;
import com.microstrategy.web.objects.WebReportValidationException;
import com.microstrategy.web.objects.WebTemplate;
import com.microstrategy.web.objects.WebTemplateMetrics;
import com.microstrategy.web.objects.WebWorkingSet;
import com.microstrategy.webapi.EnumDSSXMLAxisName;
import com.microstrategy.webapi.EnumDSSXMLDisplayMode;
import com.microstrategy.webapi.EnumDSSXMLExecutionFlags;
import com.microstrategy.webapi.EnumDSSXMLExpressionType;
import com.microstrategy.webapi.EnumDSSXMLFolderNames;
import com.microstrategy.webapi.EnumDSSXMLFunction;
import com.microstrategy.webapi.EnumDSSXMLObjectSubTypes;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLReportSaveAsFlags;
import com.microstrategy.webapi.EnumDSSXMLResultFlags;
import com.microstrategy.webapi.EnumDSSXMLStatus;
import com.mococo.web.util.PortalCodeUtil;

/**
 * MstrReportUtil
 * @author mococo
 *
 */
public class MstrReportUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(MstrReportUtil.class);
	
	
    /**
     * MstrReportUtil
     */
    public MstrReportUtil() {
    	logger.debug("MstrReportUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("MstrReportUtil");
    }
	
	
	/**
	 * closeReport
	 * @param reportInstance
	 */
    public static void closeReport(final WebReportInstance reportInstance) {
        try {
            if (reportInstance != null && reportInstance.getMessage() != null && reportInstance.getMessage(true) != null) {
                // reportInstance.getMessage().removeFromInbox();
            	final String rch1 = "N";
                if (PortalCodeUtil.CHECK_Y.equals(rch1)) {
                	logger.debug("closeReport");
                }
            }
        } catch (WebObjectsException e) {
        	logger.error("!!! error", e);
        }
    }
    
    
    /**
     * closeDocument
     * @param docuementInstance
     */
    public static void closeDocument(final WebDocumentInstance docuementInstance) {
        try {
            if (docuementInstance != null && docuementInstance.getMessage() != null && docuementInstance.getMessage(true) != null) {
                docuementInstance.getMessage().removeFromInbox();
            }
        } catch (WebObjectsException e) {
        	logger.error("!!! closeDocument Exception", e);
        }
    }
    
    
    private static boolean hasFinishedInformation(final int status) {
    	Boolean rtnCheck;
    	
        if (status == EnumDSSXMLStatus.DssXmlStatusResult 
    		|| status == EnumDSSXMLStatus.DssXmlStatusPromptXML
            || status == EnumDSSXMLStatus.DssXmlStatusInSQLEngine
            || status == EnumDSSXMLStatus.DssXmlStatusInQueryEngine
            || status == EnumDSSXMLStatus.DssXmlStatusErrMsgXML) {
        	rtnCheck = true;
        } else {
        	rtnCheck = false;
        }
        
        return rtnCheck;
    }
    
    
    /**
     * getFinishedReportInstance
     * @param session
     * @param objectId
     * @param executionFlags
     * @param resultFlags
     * @return
     */
    public static WebReportInstance getFinishedReportInstance(final WebIServerSession session, final String objectId, final int executionFlags, final int resultFlags) throws WebObjectsException, InterruptedException {

    	final WebObjectsFactory factory = session.getFactory();
    	final WebReportSource reportSource = factory.getReportSource();
        if (executionFlags != -1) {
            reportSource.setExecutionFlags(executionFlags);
        }
        final WebReportInstance reportInstance = reportSource.getNewInstance(objectId);
        reportInstance.setAsync(false);

        if (resultFlags != -1) {
            reportInstance.setResultFlags(resultFlags);
        }

        while (true) {
            if (hasFinishedInformation(reportInstance.pollStatus())) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        return reportInstance;
    }
    
    
    /**
     * getFinishedDocumentInstance
     */
    public static WebDocumentInstance getFinishedDocumentInstance(final WebIServerSession session, final String objectId, final int executionFlags, final int resultFlags) throws WebObjectsException, InterruptedException {

    	final WebObjectsFactory factory = session.getFactory();
        final WebDocumentSource documentSource = factory.getDocumentSource();
        if (executionFlags != -1) {
            documentSource.setExecutionFlags(executionFlags);
        }
        final WebDocumentInstance documentInstance = documentSource.getNewInstance(objectId);
        documentInstance.setAsync(false);

        while (true) {
            if (hasFinishedInformation(documentInstance.pollStatus())) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        return documentInstance;
    }
    
    
    /**
     * getReportAnswerXML
     */
    public static String getReportAnswerXML(final WebIServerSession session, final String objectId, final int type, final Map<String, List<String>> param) throws WebObjectsException, InterruptedException {
        String result = null;

        WebPrompts prompts = null;

        switch (type) {
        case EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition:
        	final WebReportInstance report = getFinishedReportInstance(session, objectId, -1, -1);
            report.setAsync(false);

            prompts = report.getPrompts();
            break;
        case EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition:
        	final WebDocumentInstance documentInstance = getFinishedDocumentInstance(session, objectId, -1, -1);
            documentInstance.setAsync(false);

            prompts = documentInstance.getPrompts();
            break;
        default:
        }

        if (prompts != null) {
            prompts.setClosed(false);
            setPrompts(prompts, param, session);
            result = prompts.getAnswerXML();
        }

        return result;
    }
    
    
    /**
     * date : 2022-04-25 선택된 개체 가져온 후, 리포트 저장 메소드
     */
    public static String ExecuteReport(final WebIServerSession session, final List<String> attribute, final List<String> metric,
    		final List<String> prompt, final int executionFlags, final int resultFlags, final String reportName)
            throws WebObjectsException, InterruptedException {
    	final WebObjectsFactory objectFactory = session.getFactory();
    	final WebReportSource reportSource = objectFactory.getReportSource();
        final WebObjectSource objectSource = objectFactory.getObjectSource();
        String saveObjId = "";

        reportSource.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionResolve);
        reportSource.setResultFlags(
                EnumDSSXMLResultFlags.DssXmlResultWorkingSet | EnumDSSXMLResultFlags.DssXmlResultViewReport
                        | EnumDSSXMLResultFlags.DssXmlResultStatusOnlyIfNotReady);
        // retrieve Blank Report for manipulation
        WebReportInstance reportInstance;
        try {
            // 빈리포트 개체 object_id로 얘는 id값을 고정으로 가져와야함
            reportInstance = reportSource.getNewInstance("05B202B9999F4C1AB960DA6208CADF3D");
            reportInstance.setAsync(false);
            int status = reportInstance.pollStatus();

            final WebTemplate template = reportInstance.getTemplate(); // 빈 리포트를 템플릿변수에 저장
            WebObjectInfo selectedAttrs = null;

            // get filter
            WebFilter rptFilter;
            if(status > -1) {
            	logger.debug("!!! rptFilter");
            }

            /* 선택한 애트리뷰트(선택관점) 가져옴 */
            for (int i = 0; i < attribute.size(); i++) {
                selectedAttrs = objectSource.getObject(attribute.get(i), EnumDSSXMLObjectTypes.DssXmlTypeAttribute);
                template.add(selectedAttrs, EnumDSSXMLAxisName.DssXmlAxisNameRows, i + 1);
                if (reportInstance != null && prompt.contains(attribute.get(i))) {
                	final WebWorkingSet wws = reportInstance.getWorkingSet();
                    if (wws != null) {
                        rptFilter = wws.getFilter();
                        final WebExpression filterExpression = rptFilter.getExpression();
                        // Create Elements Prompt for Year
                        /* 영상에서의 체크 박스 하는 부분 */
                        createElementsPrompt(session, filterExpression, selectedAttrs);
                    }
                }
            }

            /* 선택한 메트릭(선택지표) 가져옴 */
            WebObjectInfo selectedMetrics;
            WebTemplateMetrics wtm;
            for (int i = 0; i < metric.size(); i++) {
                selectedMetrics = objectSource.getObject(metric.get(i), EnumDSSXMLObjectTypes.DssXmlTypeMetric);

                template.addMetrics(EnumDSSXMLAxisName.DssXmlAxisNameColumns, i + 1);
                wtm = template.getTemplateMetrics();
                wtm.add(selectedMetrics);

            }

            // apply changes
            WebReportManipulation rptManip = reportInstance.getReportManipulator();
            WebReportInstance newInst;
            try {
                rptManip.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionResolve);
                rptManip.setResultFlags(1_268_388_256);
                newInst = rptManip.applyChanges();
                newInst.setAsync(false);
                status = newInst.pollStatus();

                // answer prompts
                // 리포트에서 프롬프트로 들어가는 부분
                // TODO : 현재는 마지막으로 선택된 애만 프롬프트로들어감
                if (status == EnumDSSXMLStatus.DssXmlStatusPromptXML) {
                	final WebPrompts wps = newInst.getPrompts();
                    final int logTmp1 = wps.size();
                    logger.debug("wps.size() : [{}]", logTmp1);

                    for (int i = 0; i < wps.size(); i++) {
                        switch (wps.get(i).getPromptType()) {
	                        case EnumWebPromptType.WebPromptTypeElements:
	                        	final WebElementsPrompt elementsPrompt = (WebElementsPrompt) wps.get(i);
	                            final WebElements elements = elementsPrompt.getAnswer();
	                            elements.clear();
	                            final WebAttribute promptAtt = (WebAttribute) selectedAttrs;
	                            final WebElements PromptElements = promptAtt.getElementSource().getElements();
	                            
		                        //elements.add(PromptElements.get(0).getID().toString()); 
	                            //elements.add(PromptElements.get(1).getID());
	                            
	                            int logTmp2 = wps.size();
	                            logger.debug("wps.size() : [{}]", logTmp1);
//	                            logger.debug("wps.get : [{}]", wps.get(i));
	                            
	                            final String logTmp4 = PromptElements.get(0).getID().replaceAll(PortalCodeUtil.logChange,"");
	                            logger.debug("PromptElements : [{}]", logTmp4);
	                            break;
	                        case EnumWebPromptType.WebPromptTypeObjects:
	                        	break;
	    	                default:
	    						break;
                        }
                    }
                    newInst.getPrompts().answerPrompts();
                    newInst.pollStatus();
                }

                newInst.setSaveAsDisplayMode(EnumDSSXMLDisplayMode.DssXmlDisplayModeGrid);
                newInst.setSaveAsFlags(EnumDSSXMLReportSaveAsFlags.DssXmlReportSaveAsFilterWithPrompts
                        | EnumDSSXMLReportSaveAsFlags.DssXmlReportSaveAs8iWarning
                        | EnumDSSXMLReportSaveAsFlags.DssXmlReportSaveAsEmbedded);

                // 리포트 저장 경로 : 공유리포트
                final String folderID = objectSource.getFolderID(EnumDSSXMLFolderNames.DssXmlFolderNamePublicReports);
                final WebFolder myReports = (WebFolder) objectSource.getObject(folderID,
                        EnumDSSXMLObjectTypes.DssXmlTypeFolder);
                // set report name to report name with current timestamp
                // 리포트 생성 부분 : 리포트명 + 현재시간

                // 리포트 이름.
//	            String datedReportName = reportName;
                final String datedReportName = reportName.concat(new Date().toString());

                // save report calling saveAs() method of WebReportInstance
                final WebObjectInfo woi = newInst.saveAs(myReports, datedReportName);

                // run report, this time return data
                /* 리포트 실행 부분 */
                rptManip = newInst.getReportManipulator();
                rptManip.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionFresh);
                rptManip.setResultFlags(EnumDSSXMLResultFlags.DssXmlResultGrid);
                // System.out.println("리포트 생성 완료 : " + woi.getID());
                saveObjId = woi.getID();
                /*
                 * try { newInst = rptManip.applyChanges(); newInst.pollStatus(); } catch
                 * (WebReportValidationException e) { e.printStackTrace(); }
                 */
            } catch (WebReportValidationException e) {
                logger.error("WebReportValidationException", e);
            }
        } catch (WebObjectsException e) {
        	logger.error("WebObjectsException", e);
        }
        return saveObjId;
    }
    
    
    private static void createElementsPrompt(final WebIServerSession session, final WebExpression expression,
    		final WebObjectInfo objectinfo) throws WebObjectsException {
    	final WebObjectsFactory objectFactory = session.getFactory();
    	final WebObjectSource objectSource = objectFactory.getObjectSource();
        // Retrieve a new Elements Prompt
        final WebElementsPrompt elementsPrompt = (WebElementsPrompt) objectSource.getNewObject(
                EnumDSSXMLObjectTypes.DssXmlTypePrompt, EnumDSSXMLObjectSubTypes.DssXmlSubTypePromptElements);
        // Set the Origin, the object that requires elements
        elementsPrompt.setOrigin((WebAttribute) objectinfo);
        // Set Other properties
        objectinfo.populate();

        final String name = objectinfo.getDisplayName();
        elementsPrompt.setMeaning("Please Select Elements for " + name);
        elementsPrompt.setName("Select Elements for " + name);

        // Add Prompt to filter expression.
        final WebOperatorNode weboperatornode = expression.createOperatorNode(
                EnumDSSXMLExpressionType.DssXmlExpressionGeneric, EnumDSSXMLFunction.DssXmlFunctionIn,
                expression.getRootNode());
        weboperatornode.getPromptInstances().newPromptInstance(elementsPrompt);
    }
    
    
    private static void setPrompts(final WebPrompts prompts, final Map<String, List<String>> param, final WebIServerSession session) {
        for (final Enumeration<?> e = prompts.elements(); e.hasMoreElements();) {
        	final WebPrompt prompt = (WebPrompt) e.nextElement();

            for (final String objectId : param.keySet()) {
            	final String logTmp1 = prompt.getID().replaceAll(PortalCodeUtil.logChange,"");
            	logger.debug("=> [{}]", logTmp1);

                if (!StringUtils.equals(objectId, prompt.getID())) {
                    continue;
                }

                switch (prompt.getPromptType()) {
	                case EnumWebPromptType.WebPromptTypeConstant:
	                	final WebConstantPrompt constantPrompt = (WebConstantPrompt) prompt;
	                	final List<String> list = param.get(objectId);
	                    final String constantValue = list.get(0);
	
	                    // TODO: 프롬프트값이 전달되지 않고 필수값이 아닌경우, 프롬프트 화면으로 이동하는 현상 대응
	                    if (StringUtils.isNotEmpty(constantValue)) {
	                    	final String answer = constantValue;
	                        constantPrompt.setAnswer(answer);
	                    }
	
	                    break;
	                case EnumWebPromptType.WebPromptTypeElements:
	                	final WebElementsPrompt elementsPrompt = (WebElementsPrompt) prompt;
	                    final List<String> elementValueList = param.get(objectId);
	
	                    try {
	                        elementsPrompt.populate();
	
	                        WebElements suggested = elementsPrompt.getSuggestedAnswers();
	
	                        if (suggested == null || suggested.size() == 0) {
	                            suggested = elementsPrompt.getOrigin().getElementSource().getElements();
	                        }
	
	                        final WebElements answers = elementsPrompt.getAnswer();
	                        answers.clear();
	                        for (final Enumeration<?> e2 = suggested.elements(); e2.hasMoreElements();) {
	                        	final WebElement element = (WebElement) e2.nextElement();
	                            
	                        	final String logTmp2 = elementValueList.toString().replaceAll(PortalCodeUtil.logChange,"");
	                            String logTmp3 = element.getID().replaceAll(PortalCodeUtil.logChange,"");
	                            logger.debug("=>elementsPrompt [{}], [{}]", logTmp1, logTmp2);
	
	                            for (final String elementValue : elementValueList) {
	                                if (StringUtils.isNotEmpty(elementValue) && StringUtils.equals(elementValue, element.getID())) {
	                                    answers.add(elementValue, element.getDisplayName());
	                                    String logTmp4 = elementValue.replaceAll(PortalCodeUtil.logChange,"");
	                                    logger.debug("=>add this element: [{}]", logTmp1);
	                                }
	                            }
	                        }
	                    } catch (WebObjectsException e3) {
	                    	logger.error("!!! error", e3);
	                    }
	
	                    break;
	                case EnumWebPromptType.WebPromptTypeObjects:
	                	final WebObjectsPrompt objectsPrompt = (WebObjectsPrompt) prompt;
	                    final List<String> answObjIdList = param.get(objectId);
	
	                    final WebFolder suggested = objectsPrompt.getSuggestedAnswers();
	                    final WebFolder answers = objectsPrompt.getAnswer();
	                    answers.clear();
	
	                    if (answObjIdList != null) {
	                        for (final String answerObjectId : answObjIdList) {
	                        	final WebObjectSource objectSource = session.getFactory().getObjectSource();
	                            
	                            for (final Enumeration<?> e2 = suggested.elements(); e2.hasMoreElements();) {
	                            	final WebObjectInfo info = (WebObjectInfo) e2.nextElement();
	                                
	                                if (StringUtils.equals(info.getID(), answerObjectId)) {
	                                    continue;
	                                }
	
	                                try {
	                                    answers.add(objectSource.getObject(answerObjectId, info.getType()));
	                                } catch (UnsupportedOperationException | IllegalArgumentException | WebObjectsException e1) {
	                                	logger.debug("!!! error", e1);
	                                }
	                                
	                                //PMD 관련 조건 로직 추가
	                                final String rch1 = "Y";
	                                if (PortalCodeUtil.CHECK_Y.equals(rch1)) {
	                                	break;
	                                }
	                                
	                            }
	                        }
	                    }
	
	                    break;
	                default:
						break;
                }

                //PMD 관련 조건 로직 추가
                final String rch1 = "Y";
                if (PortalCodeUtil.CHECK_Y.equals(rch1)) {
                	break;
                }
            }
        }
    }

}
