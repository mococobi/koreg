package com.mococo.microstrategy.sdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microstrategy.web.objects.EnumWebPromptType;
import com.microstrategy.web.objects.WebConstantPrompt;
import com.microstrategy.web.objects.WebDocumentInstance;
import com.microstrategy.web.objects.WebDocumentSource;
import com.microstrategy.web.objects.WebElement;
import com.microstrategy.web.objects.WebElements;
import com.microstrategy.web.objects.WebElementsPrompt;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.web.objects.WebPrompts;
import com.microstrategy.web.objects.WebReportInstance;
import com.microstrategy.web.objects.WebReportSource;
import com.microstrategy.webapi.EnumDSSXMLStatus;
import com.mococo.web.util.CustomProperties;

/**
 * command line : java -cp
 * .;C:/workspace/project/sdk/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/microstrategy-sdk/WEB-INF/lib/*
 * com.mocomsys.microstrategy.sdk.util.MstrReportExecutor
 *
 */
public class MstrReportExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MstrReportExecutor.class);

    public Map<String, Object> getConfig() {
        BufferedReader reader = null;
        Map<String, Object> config = null;

        try {
            InputStream inputStream = MstrReportExecutor.class.getResourceAsStream("ReportInfo.json");
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            config = new ObjectMapper().readValue(reader, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("!!! error", e);
            config = null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("!!! resource close error", e);
            }
        }

        return config;
    }

    public static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    /*
     * d-mf: 금월1일 d-bmf: 전월1일 d-bmd: 전월동일
     */
    public static String getDayString(String tag) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(new Date());
        Calendar targetCal = Calendar.getInstance();

        if (StringUtils.equals("mf", tag)) {
            targetCal.set(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), 1);
        } else if (StringUtils.equals("bmf", tag)) {
            targetCal.set(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH) - 1, 1);
        } else if (StringUtils.equals("bmd", tag)) {
            targetCal.setTime(currentCal.getTime());
            targetCal.add(Calendar.MONTH, -1);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateStr = format.format(targetCal.getTime());
        logger.debug("=> tag:[{}], Year:[{}]", tag, targetCal.get(Calendar.YEAR));

        return dateStr;
    }

    public static String getDayString(int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1 * interval);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateStr = format.format(cal.getTime());
        logger.debug("=> Year:[{}], Month:[{}]", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));

        return dateStr;
    }

    public static String getMonthString(int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1 * interval);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateStr = format.format(cal.getTime());
        logger.debug("=> Year:[{}], Month:[{}]", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));

        return dateStr;
    }

    public static String[] getTimeString(String str) {
        final Pattern pattern = Pattern.compile("(?<=\\{)(?:(?!\\}).)*?(?=\\})", Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(str);

        String interval = null;
        if (matcher.find()) {
            interval = matcher.group(0);
        }

        String[] result = null;
        if (StringUtils.isNotEmpty(interval)) {
            result = interval.split("-");
        }

        return result;
    }

    /*
     * d: 금일 d-mf: 금월1일 d-bmf: 전월1일 d-bmd: 전월동일 d-numeric: 금일 - numeric m: 금월
     * m-numeric: 금월 - numeric
     */
    public static String getConstantValue(String constantValue) {
        String[] tokens = getTimeString(constantValue);
        String result = constantValue;
        if (tokens != null) {
            if (tokens.length == 1) {
                if (StringUtils.equals(tokens[0], "d")) {
                    result = getDayString(0);
                } else if (StringUtils.equals(tokens[0], "m")) {
                    result = getMonthString(0);
                }
            } else if (tokens.length == 2) {
                if (StringUtils.equals(tokens[0], "d")) {
                    if (isNumeric(tokens[1])) {
                        result = getDayString(Integer.parseInt(tokens[1]));
                    } else {
                        result = getDayString(tokens[1]);
                    }
                } else if (StringUtils.equals(tokens[0], "m")) {
                    if (isNumeric(tokens[1])) {
                        result = getMonthString(Integer.parseInt(tokens[1]));
                    }
                }
            }
        }
        return result;
    }

    private void setPrompts(WebPrompts prompts, Map<String, String> param) {
        for (String objectId : param.keySet()) {
            for (Enumeration<?> e = prompts.elements(); e.hasMoreElements();) {
                WebPrompt prompt = (WebPrompt) e.nextElement();

                if (StringUtils.equals(objectId, prompt.getID())) {
                    switch (prompt.getPromptType()) {
                    case EnumWebPromptType.WebPromptTypeConstant:
                        WebConstantPrompt constantPrompt = (WebConstantPrompt) prompt;
                        String constantValue = param.get(objectId);

                        if (StringUtils.isNotEmpty(constantValue)) {
                            String answer = getConstantValue(constantValue);
                            constantPrompt.setAnswer(answer);
                        }

                        break;
                    case EnumWebPromptType.WebPromptTypeElements:
                        WebElementsPrompt elementsPrompt = (WebElementsPrompt) prompt;
                        String elementValue = param.get(objectId);

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

                                if (StringUtils.isNotEmpty(elementValue)
                                        && StringUtils.equals(elementValue, element.getID())) {
                                    answers.add(elementValue, element.getDisplayName());
                                    logger.debug("=> add this element: [{}]", elementValue);

                                    break;
                                }
                            }
                        } catch (WebObjectsException e3) {
                            logger.error("!!! error", e);
                        }
                    }

                    break;
                }
            }
        }
        logger.debug("=> [{}]", prompts.getAnswerXML());
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

    private static boolean hasFinishedExecution(int status) {
        Boolean finished = false;

        logger.debug("=> status:[{}]", status);

        switch (status) {
        case EnumDSSXMLStatus.DssXmlStatusResult:
        case EnumDSSXMLStatus.DssXmlStatusXMLResult:
        case EnumDSSXMLStatus.DssXmlStatusErrMsgXML:
            finished = true;
            break;
        default:
            finished = false;
            break;
        }

        return finished;
    }

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

    public void runReport(WebIServerSession session, String objectId, Map<String, String> param)
            throws WebObjectsException, InterruptedException {
        WebReportInstance report = getFinishedReportInstance(session, objectId, -1, -1);
        WebPrompts prompts = report.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            prompts.answerPrompts();
            logger.debug("prompts.getShortAnswerXML() :[{}]", prompts.getAnswerXML());
        }

        int i = 0;
        while (++i < 1000) {
            if (hasFinishedExecution(report.pollStatus())) {
                break;
            }
            Thread.sleep(200);
        }

        MstrReportUtil.closeReport(report);
    }

    public String getReportAnswerXML(WebIServerSession session, String objectId, Map<String, String> param)
            throws WebObjectsException, InterruptedException {
        String result = null;
        WebReportInstance report = getFinishedReportInstance(session, objectId, -1, -1);
        WebPrompts prompts = report.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            result = prompts.getAnswerXML();
        }

        return result;
    }

    public String getDocumentAnswerXML(WebIServerSession session, String objectId, Map<String, String> param)
            throws WebObjectsException, InterruptedException {
        String result = null;
        WebDocumentInstance document = getFinishedDocumentInstance(session, objectId, -1, -1);
        WebPrompts prompts = document.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            result = prompts.getShortAnswerXML();
        }

        return result;
    }

    public void runDocument(WebIServerSession session, String objectId, Map<String, String> param)
            throws WebObjectsException, InterruptedException {
        WebDocumentInstance document = getFinishedDocumentInstance(session, objectId, -1, -1);
        WebPrompts prompts = document.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            prompts.answerPrompts();
            logger.debug("prompts.getShortAnswerXML() :[{}]", prompts.getShortAnswerXML());
        }

        int i = 0;
        while (++i < 100) {
            if (hasFinishedExecution(document.pollStatus())) {
                break;
            }
            Thread.sleep(200);
        }

        MstrReportUtil.closeDocument(document);
    }

    public static void main(String[] args) {
        logger.debug("MstrReportExecutor start.");

        MstrReportExecutor executor = new MstrReportExecutor();
        Map<String, Object> config = executor.getConfig();
        String server = (String) config.get("server");
        String project = (String) config.get("project");
        int port = Integer.parseInt(CustomProperties.getProperty("mstr.server.port"));
        int locale = Integer.parseInt(CustomProperties.getProperty("mstr.session.locale"));
        String uid = (String) config.get("uid");
        String pwd = (String) config.get("pwd");

        logger.debug("config:[{}]", config);

        WebIServerSession session = null;
        try {
            List<Map<String, Object>> reportInfoList = (List<Map<String, Object>>) config.get("reportInfo");
            session = MstrUtil.connectSession(server, project, port, locale, uid, pwd);
            for (Map<String, Object> reportInfo : reportInfoList) {
                String objectId = (String) reportInfo.get("objectId");
                int type = (Integer) reportInfo.get("type");
                Map<String, String> prompts = (Map<String, String>) reportInfo.get("prompts");
                logger.debug("=> objectId:[{}]", objectId);
                logger.debug("=> type:[{}]", type);
                logger.debug("=> prompts:[{}]", prompts);

                try {
                    if (type == 3) {
                        // executor.runReport(session, objectId, prompts);
                        logger.debug("=> [{}]", executor.getReportAnswerXML(session, objectId, prompts));
                    } else if (type == 55) {
                        executor.runDocument(session, objectId, prompts);
                    }
                } catch (Exception e) {
                    logger.debug("!!! error", e);
                }
            }
        } catch (Exception e) {
            logger.error("!!! error", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        logger.debug("MstrReportExecutor end.");
    }
}
