package com.mococo.microstrategy.sdk.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

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

public class MstrReportUtil {
    private static final Logger logger = LoggerFactory.getLogger(MstrReportUtil.class);

    public static void closeReport(WebReportInstance reportInstance) {
        try {
            if (reportInstance != null && reportInstance.getMessage() != null
                    && reportInstance.getMessage(true) != null) {
                // reportInstance.getMessage().removeFromInbox();
            }
        } catch (Exception e) {
            logger.error("!!! error", e);
        }
    }

    public static void closeDocument(WebDocumentInstance docuementInstance) {
        try {
            if (docuementInstance != null && docuementInstance.getMessage() != null
                    && docuementInstance.getMessage(true) != null) {
                docuementInstance.getMessage().removeFromInbox();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean hasFinishedInformation(int status) {
        if (status == EnumDSSXMLStatus.DssXmlStatusResult || status == EnumDSSXMLStatus.DssXmlStatusPromptXML
                || status == EnumDSSXMLStatus.DssXmlStatusInSQLEngine
                || status == EnumDSSXMLStatus.DssXmlStatusInQueryEngine
                || status == EnumDSSXMLStatus.DssXmlStatusErrMsgXML) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Example: WebReportInstance reportInstance = getFinishedReportInstance(
     * session, objectId, EnumDSSXMLExecutionFlags.DssXmlExecutionNoAction,
     * EnumDSSXMLResultFlags.DssXmlResultDtlsFilterExpr); 사용용도: 일반 리포스 실행 -
     * executionFlags = -1, resultFlags = -1 로 지정 리포트의 필터객체를 조회할때 사용 -
     * EnumDSSXMLExecutionFlags.DssXmlExecutionNoAction,
     * EnumDSSXMLResultFlags.DssXmlResultDtlsFilterExpr 로 지정 리포트 익스포트 실행 -
     * EnumDSSXMLResultFlags.DssXmlResultGrid |
     * EnumDSSXMLResultFlags.DssXmlResultBandingStyle
     * 
     * @param session
     * @param objectId
     * @param executionFlags
     * @param resultFlags
     * @return
     * @throws WebObjectsException
     * @throws InterruptedException
     */
    public static WebReportInstance getFinishedReportInstance(WebIServerSession session, String objectId,
            int executionFlags, int resultFlags) throws WebObjectsException, InterruptedException {

        WebObjectsFactory factory = session.getFactory();
        WebReportSource reportSource = factory.getReportSource();
        if (executionFlags != -1) {
            reportSource.setExecutionFlags(executionFlags);
        }
        WebReportInstance reportInstance = reportSource.getNewInstance(objectId);
        reportInstance.setAsync(false);

        if (resultFlags != -1) {
            reportInstance.setResultFlags(resultFlags);
        }

        while (true) {
            if (hasFinishedInformation(reportInstance.pollStatus())) {
                break;
            }
            Thread.sleep(200);
        }
        return reportInstance;
    }

    public static WebDocumentInstance getFinishedDocumentInstance(WebIServerSession session, String objectId,
            int executionFlags, int resultFlags) throws WebObjectsException, InterruptedException {

        WebObjectsFactory factory = session.getFactory();
        WebDocumentSource documentSource = factory.getDocumentSource();
        if (executionFlags != -1) {
            documentSource.setExecutionFlags(executionFlags);
        }
        WebDocumentInstance documentInstance = documentSource.getNewInstance(objectId);
        documentInstance.setAsync(false);

        while (true) {
            if (hasFinishedInformation(documentInstance.pollStatus())) {
                break;
            }
            Thread.sleep(200);
        }
        return documentInstance;
    }

    public static String getReportAnswerXML(WebIServerSession session, String objectId, int type,
            Map<String, List<String>> param) throws WebObjectsException, InterruptedException {
        String result = null;

        WebPrompts prompts = null;

        switch (type) {
        case EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition:
            WebReportInstance report = getFinishedReportInstance(session, objectId, -1, -1);
            report.setAsync(false);

            prompts = report.getPrompts();
            break;
        case EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition:
            WebDocumentInstance documentInstance = getFinishedDocumentInstance(session, objectId, -1, -1);
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

    /*
     * date : 2022-04-25 선택된 개체 가져온 후, 리포트 저장 메소드
     */
    public static String ExecuteReport(WebIServerSession session, ArrayList<String> attribute, ArrayList<String> metric,
            ArrayList<String> prompt, int executionFlags, int resultFlags, String reportName)
            throws WebObjectsException, InterruptedException {
        WebObjectsFactory objectFactory = session.getFactory();
        WebReportSource reportSource = objectFactory.getReportSource();
        WebObjectSource objectSource = objectFactory.getObjectSource();
        String saveObjId = "";

        reportSource.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionResolve);
        reportSource.setResultFlags(
                EnumDSSXMLResultFlags.DssXmlResultWorkingSet | EnumDSSXMLResultFlags.DssXmlResultViewReport
                        | EnumDSSXMLResultFlags.DssXmlResultStatusOnlyIfNotReady);
        // retrieve Blank Report for manipulation
        WebReportInstance reportInstance = null;
        try {
            // 빈리포트 개체 object_id로 얘는 id값을 고정으로 가져와야함
            reportInstance = reportSource.getNewInstance("05B202B9999F4C1AB960DA6208CADF3D");
            reportInstance.setAsync(false);
            int status = reportInstance.pollStatus();

            WebTemplate template = reportInstance.getTemplate(); // 빈 리포트를 템플릿변수에 저장
            WebObjectInfo selectedAttrs = null;

            // get filter
            WebFilter rptFilter = null;
            logger.error("!!! rptFilter");

            /* 선택한 애트리뷰트(선택관점) 가져옴 */
            for (int i = 0; i < attribute.size(); i++) {
                selectedAttrs = objectSource.getObject(attribute.get(i), EnumDSSXMLObjectTypes.DssXmlTypeAttribute);
                template.add(selectedAttrs, EnumDSSXMLAxisName.DssXmlAxisNameRows, i + 1);
                if (reportInstance != null && prompt.contains(attribute.get(i))) {
                    WebWorkingSet wws = reportInstance.getWorkingSet();
                    if (wws != null) {
                        rptFilter = wws.getFilter();
                        WebExpression filterExpression = rptFilter.getExpression();
                        // Create Elements Prompt for Year
                        /* 영상에서의 체크 박스 하는 부분 */
                        createElementsPrompt(session, filterExpression, selectedAttrs);

                    }

                }

            }

            /* 선택한 메트릭(선택지표) 가져옴 */
            WebObjectInfo selectedMetrics = null;
            WebTemplateMetrics wtm = null;
            for (int i = 0; i < metric.size(); i++) {
                selectedMetrics = objectSource.getObject(metric.get(i), EnumDSSXMLObjectTypes.DssXmlTypeMetric);

                template.addMetrics(EnumDSSXMLAxisName.DssXmlAxisNameColumns, i + 1);
                wtm = template.getTemplateMetrics();
                wtm.add(selectedMetrics);

            }

            // apply changes
            WebReportManipulation rptManip = reportInstance.getReportManipulator();
            WebReportInstance newInst = null;
            try {
                rptManip.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionResolve);
                rptManip.setResultFlags(1268388256);
                newInst = rptManip.applyChanges();
                newInst.setAsync(false);
                status = newInst.pollStatus();

                // answer prompts
                // 리포트에서 프롬프트로 들어가는 부분
                // TODO : 현재는 마지막으로 선택된 애만 프롬프트로들어감
                if (status == EnumDSSXMLStatus.DssXmlStatusPromptXML) {
                    WebPrompts wps = newInst.getPrompts();
                    System.out.println("wps.size() : " + wps.size());

                    for (int i = 0; i < wps.size(); i++) {
                        switch (wps.get(i).getPromptType()) {
                        case EnumWebPromptType.WebPromptTypeElements:
                            WebElementsPrompt elementsPrompt = (WebElementsPrompt) wps.get(i);
                            WebElements elements = elementsPrompt.getAnswer();
                            elements.clear();
                            WebAttribute promptAtt = (WebAttribute) selectedAttrs;
                            WebElements PromptElements = promptAtt.getElementSource().getElements();
                            System.out.println("wps.size() : " + wps.size());
                            System.out.println("wps.get : " + wps.get(i));
//	                            elements.add(PromptElements.get(0).getID().toString()); 
                            // elements.add(PromptElements.get(1).getID());
                            System.out.println("PromptElements : " + PromptElements.get(0).getID());
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
                String folderID = objectSource.getFolderID(EnumDSSXMLFolderNames.DssXmlFolderNamePublicReports);
                WebFolder myReports = (WebFolder) objectSource.getObject(folderID,
                        EnumDSSXMLObjectTypes.DssXmlTypeFolder);
                // set report name to report name with current timestamp
                // 리포트 생성 부분 : 리포트명 + 현재시간

                // TODO : 리포트 이름.
//	            String datedReportName = reportName;
                String datedReportName = reportName.concat((new Date()).toString());

                // save report calling saveAs() method of WebReportInstance
                WebObjectInfo woi = newInst.saveAs(myReports, datedReportName);

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
                e.printStackTrace();
            }
        } catch (WebObjectsException e) {
            e.printStackTrace();
        }
        return saveObjId;
    }

    private static void createElementsPrompt(WebIServerSession session, WebExpression expression,
            WebObjectInfo objectinfo) throws WebObjectsException {
        WebObjectsFactory objectFactory = session.getFactory();
        WebObjectSource objectSource = objectFactory.getObjectSource();
        // Retrieve a new Elements Prompt
        WebElementsPrompt elementsPrompt = (WebElementsPrompt) objectSource.getNewObject(
                EnumDSSXMLObjectTypes.DssXmlTypePrompt, EnumDSSXMLObjectSubTypes.DssXmlSubTypePromptElements);
        // Set the Origin, the object that requires elements
        elementsPrompt.setOrigin((WebAttribute) objectinfo);
        // Set Other properties
        objectinfo.populate();

        String name = objectinfo.getDisplayName();
        elementsPrompt.setMeaning("Please Select Elements for " + name);
        elementsPrompt.setName("Select Elements for " + name);

        // Add Prompt to filter expression.
        WebOperatorNode weboperatornode = expression.createOperatorNode(
                EnumDSSXMLExpressionType.DssXmlExpressionGeneric, EnumDSSXMLFunction.DssXmlFunctionIn,
                expression.getRootNode());
        weboperatornode.getPromptInstances().newPromptInstance(elementsPrompt);
    }

    private static void setPrompts(WebPrompts prompts, Map<String, List<String>> param, WebIServerSession session) {
        for (Enumeration<?> e = prompts.elements(); e.hasMoreElements();) {
            WebPrompt prompt = (WebPrompt) e.nextElement();

            for (String objectId : param.keySet()) {
                logger.debug("=> [{}]", prompt.getID());

                if (!StringUtils.equals(objectId, prompt.getID())) {
                    continue;
                }

                switch (prompt.getPromptType()) {
                case EnumWebPromptType.WebPromptTypeConstant:
                    WebConstantPrompt constantPrompt = (WebConstantPrompt) prompt;
                    List<String> list = param.get(objectId);
                    String constantValue = list.get(0);

                    // TODO: 프롬프트값이 전달되지 않고 필수값이 아닌경우, 프롬프트 화면으로 이동하는 현상 대응
                    if (StringUtils.isNotEmpty(constantValue)) {
                        String answer = constantValue;
                        constantPrompt.setAnswer(answer);
                    }

                    break;
                case EnumWebPromptType.WebPromptTypeElements:
                    WebElementsPrompt elementsPrompt = (WebElementsPrompt) prompt;
                    List<String> elementValueList = param.get(objectId);

                    try {
                        elementsPrompt.populate();

                        WebElements suggested = elementsPrompt.getSuggestedAnswers();

                        if (suggested == null || suggested.size() == 0) {
                            suggested = elementsPrompt.getOrigin().getElementSource().getElements();
                        }

                        WebElements answers = elementsPrompt.getAnswer();
                        answers.clear();
                        for (Enumeration<?> e2 = suggested.elements(); e2.hasMoreElements();) {
                            WebElement element = (WebElement) e2.nextElement();
                            logger.debug("=> [{}], [{}]", elementValueList, element.getID());

                            for (String elementValue : elementValueList) {
                                if (StringUtils.isNotEmpty(elementValue)
                                        && StringUtils.equals(elementValue, element.getID())) {
                                    answers.add(elementValue, element.getDisplayName());
                                    logger.debug("=> add this element: [{}]", elementValue);
                                }
                            }
                        }
                    } catch (WebObjectsException e3) {
                        logger.error("!!! error", e);
                    }

                    break;
                case EnumWebPromptType.WebPromptTypeObjects:
                    WebObjectsPrompt objectsPrompt = (WebObjectsPrompt) prompt;
                    List<String> answerObjectIdList = param.get(objectId);

                    WebFolder suggested = objectsPrompt.getSuggestedAnswers();
                    WebFolder answers = objectsPrompt.getAnswer();
                    answers.clear();

                    if (answerObjectIdList != null) {
                        for (String answerObjectId : answerObjectIdList) {
                            WebObjectSource objectSource = session.getFactory().getObjectSource();

                            for (Enumeration<?> e2 = suggested.elements(); e2.hasMoreElements();) {
                                WebObjectInfo info = (WebObjectInfo) e2.nextElement();

                                if (StringUtils.equals(info.getID(), answerObjectId)) {
                                    continue;
                                }

                                try {
                                    answers.add(objectSource.getObject(answerObjectId, info.getType()));
                                } catch (UnsupportedOperationException | IllegalArgumentException
                                        | WebObjectsException e1) {
                                    logger.debug("!!! error", e1);
                                }

                                break;
                            }
                        }
                    }

                    break;
                }

                break;
            }
        }
    }

}
