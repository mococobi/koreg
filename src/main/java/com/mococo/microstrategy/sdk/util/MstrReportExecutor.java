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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
import com.mococo.web.util.PortalCodeUtil;


/**
 * MstrReportExecutor
 * @author mococo
 *
 */
public class MstrReportExecutor {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(MstrReportExecutor.class);
	
	
    /**
     * MstrReportExecutor
     */
    public MstrReportExecutor() {
    	logger.debug("MstrReportExecutor");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("MstrReportExecutor");
    }
	
	
	/**
	 * getConfig
	 * @return
	 */
    public Map<String, Object> getConfig() {
        Map<String, Object> config = null;

        try (
    		InputStream inputStream = MstrReportExecutor.class.getResourceAsStream("ReportInfo.json");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        ) {
        	config = new ObjectMapper().readValue(reader, new TypeReference<>() {});		
        } catch (IOException e) {
        	logger.error("!!! error", e);
        }

        return config;
    }
    
    
    /**
     * isNumeric
     * @param s
     * @return
     */
    public static boolean isNumeric(final String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }
    
    
    /**
     * d-mf: 금월1일 d-bmf: 전월1일 d-bmd: 전월동일
     * @param tag
     * @return
     */
    public static String getDayString(final String tag) {
    	final Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(new Date());
        final Calendar targetCal = Calendar.getInstance();

        if (StringUtils.equals("mf", tag)) {
            targetCal.set(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), 1);
        } else if (StringUtils.equals("bmf", tag)) {
            targetCal.set(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH) - 1, 1);
        } else if (StringUtils.equals("bmd", tag)) {
            targetCal.setTime(currentCal.getTime());
            targetCal.add(Calendar.MONTH, -1);
        }

        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        final String dateStr = format.format(targetCal.getTime());
        
        final String logTmp1 = tag.replaceAll(PortalCodeUtil.logChange,"");
        final int logTmp2 = targetCal.get(Calendar.YEAR);
        logger.debug("=> tag:[{}], Year:[{}]", logTmp1, logTmp2);

        return dateStr;
    }
    
    
    /**
     * getDayString
     * @param interval
     * @return
     */
    public static String getDayString(final int interval) {
    	final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1 * interval);

        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        final String dateStr = format.format(cal.getTime());
        
        final int logTmp1 = cal.get(Calendar.YEAR);
        final int logTmp2 = cal.get(Calendar.MONTH);
        logger.debug("=> Year:[{}], Month:[{}]", logTmp1, logTmp2);

        return dateStr;
    }
    
    
    /**
     * getMonthString
     * @param interval
     * @return
     */
    public static String getMonthString(final int interval) {
    	final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1 * interval);

        final SimpleDateFormat format = new SimpleDateFormat("yyyyMM", Locale.KOREA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        final String dateStr = format.format(cal.getTime());
        
        final int logTmp1 = cal.get(Calendar.YEAR);
        final int logTmp2 = cal.get(Calendar.MONTH);
        logger.debug("=> Year:[{}], Month:[{}]", logTmp1, logTmp2);

        return dateStr;
    }
    
    
    /**
     * getTimeString
     * @param str
     * @return
     */
    public static String[] getTimeString(final String str) {
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
    
    
    /**
     * d: 금일 d-mf: 금월1일 d-bmf: 전월1일 d-bmd: 전월동일 d-numeric: 금일 - numeric m: 금월
     * m-numeric: 금월 - numeric
     * @param constantValue
     * @return
     */
    public static String getConstantValue(final String constantValue) {
    	final String[] tokens = getTimeString(constantValue);
        String result = constantValue;
        if (tokens != null) {
            if (tokens.length == PortalCodeUtil.NUMBER_1) {
                if (StringUtils.equals(tokens[0], "d")) {
                    result = getDayString(0);
                } else if (StringUtils.equals(tokens[0], "m")) {
                    result = getMonthString(0);
                }
            } else if (tokens.length == PortalCodeUtil.NUMBER_2) {
                if (StringUtils.equals(tokens[0], "d")) {
                    if (isNumeric(tokens[1])) {
                        result = getDayString(Integer.parseInt(tokens[1]));
                    } else {
                        result = getDayString(tokens[1]);
                    }
                } else if (StringUtils.equals(tokens[0], "m") && isNumeric(tokens[1])) {
                    result = getMonthString(Integer.parseInt(tokens[1]));
                }
            }
        }
        return result;
    }
    
    
    private void setPrompts(final WebPrompts prompts, final Map<String, String> param) {
        for (final String objectId : param.keySet()) {
            for (final Enumeration<?> e = prompts.elements(); e.hasMoreElements();) {
            	final WebPrompt prompt = (WebPrompt) e.nextElement();

                if (StringUtils.equals(objectId, prompt.getID())) {
                    switch (prompt.getPromptType()) {
	                    case EnumWebPromptType.WebPromptTypeConstant:
	                    	final WebConstantPrompt constantPrompt = (WebConstantPrompt) prompt;
	                    	final String constantValue = param.get(objectId);
	
	                        if (StringUtils.isNotEmpty(constantValue)) {
	                        	final String answer = getConstantValue(constantValue);
	                            constantPrompt.setAnswer(answer);
	                        }
	
	                        break;
	                    case EnumWebPromptType.WebPromptTypeElements:
	                    	final WebElementsPrompt elementsPrompt = (WebElementsPrompt) prompt;
	                        final String elementValue = param.get(objectId);
	
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
	
	                                if (StringUtils.isNotEmpty(elementValue) && StringUtils.equals(elementValue, element.getID())) {
	                                    answers.add(elementValue, element.getDisplayName());
	                                    
	                                    final String logTmp1 = elementValue.replaceAll(PortalCodeUtil.logChange,"");
	                                    logger.debug("=> add this element: [{}]", logTmp1);
	
	                                    break;
	                                }
	                            }
	                        } catch (WebObjectsException e3) {
	                        	logger.error("!!! setPrompts WebObjectsException", e3);
	                        }
	                        
	                        break;
		                default:
		                	break;
                    }

                    break;
                }
            }
        }
        
//        logger.debug("=> [{}]", prompts.getAnswerXML());
    }
    
    
    private static boolean hasFinishedInformation(final int status) {
    	Boolean rtnCheck;
        if (status == EnumDSSXMLStatus.DssXmlStatusResult || status == EnumDSSXMLStatus.DssXmlStatusPromptXML
                || status == EnumDSSXMLStatus.DssXmlStatusInSQLEngine
                || status == EnumDSSXMLStatus.DssXmlStatusInQueryEngine
                || status == EnumDSSXMLStatus.DssXmlStatusErrMsgXML) {
        	rtnCheck = true;
        } else {
        	rtnCheck = false;
        }
        
        return rtnCheck;
    }
    
    
    private static boolean hasFinishedExecution(final int status) {
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
     * @param session
     * @param objectId
     * @param executionFlags
     * @param resultFlags
     * @return
     */
    public static WebDocumentInstance getFinishedDocumentInstance(final WebIServerSession session, final String objectId, final int executionFlags, int resultFlags) throws WebObjectsException, InterruptedException {
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
     * runReport
     * @param session
     * @param objectId
     * @param param
     * @throws WebObjectsException
     * @throws InterruptedException
     */
    public void runReport(final WebIServerSession session, final String objectId, final Map<String, String> param) throws WebObjectsException, InterruptedException {
    	final WebReportInstance report = getFinishedReportInstance(session, objectId, -1, -1);
        final WebPrompts prompts = report.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            prompts.answerPrompts();
            
//            logger.debug("prompts.getShortAnswerXML() :[{}]", prompts.getAnswerXML());
        }

        int whilI = 0;
        while (++whilI < 1000) {
            if (hasFinishedExecution(report.pollStatus())) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }

        MstrReportUtil.closeReport(report);
    }
    
    
    /**
     * getReportAnswerXML
     * @param session
     * @param objectId
     * @param param
     * @return
     */
    public String getReportAnswerXML(final WebIServerSession session, final String objectId, final Map<String, String> param) throws WebObjectsException, InterruptedException {
        String result = null;
        final WebReportInstance report = getFinishedReportInstance(session, objectId, -1, -1);
        final WebPrompts prompts = report.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            result = prompts.getAnswerXML();
        }

        return result;
    }
    
    
    /**
     * getDocumentAnswerXML
     * @param session
     * @param objectId
     * @param param
     * @return
     */
    public String getDocumentAnswerXML(final WebIServerSession session, final String objectId, final Map<String, String> param) throws WebObjectsException, InterruptedException {
        String result = null;
        final WebDocumentInstance document = getFinishedDocumentInstance(session, objectId, -1, -1);
        final WebPrompts prompts = document.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            result = prompts.getShortAnswerXML();
        }

        return result;
    }
    
    
    /**
     * runDocument
     * @param session
     * @param objectId
     * @param param
     * @throws WebObjectsException
     * @throws InterruptedException
     */
    public void runDocument(final WebIServerSession session, final String objectId, final Map<String, String> param) throws WebObjectsException, InterruptedException {
    	final WebDocumentInstance document = getFinishedDocumentInstance(session, objectId, -1, -1);
    	final WebPrompts prompts = document.getPrompts();

        if (prompts != null) {
            setPrompts(prompts, param);
            prompts.answerPrompts();
            
            final String logTmp1 = prompts.getShortAnswerXML().replaceAll(PortalCodeUtil.logChange,"");
            logger.debug("prompts.getShortAnswerXML() :[{}]", logTmp1);
        }

        int whilI = 0;
        while (++whilI < 100) {
            if (hasFinishedExecution(document.pollStatus())) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }

        MstrReportUtil.closeDocument(document);
    }
    
    
    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
    	logger.debug("MstrReportExecutor start.");

    	final MstrReportExecutor executor = new MstrReportExecutor();
        final Map<String, Object> config = executor.getConfig();
        final String logTmp1 = config.toString().replaceAll(PortalCodeUtil.logChange,"");
        logger.debug("config:[{}]", logTmp1);

        WebIServerSession session = null;
        try {
        	final List<Map<String, Object>> reportInfoList = (List<Map<String, Object>>) config.get("reportInfo");
            
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", config.get("server"));
			connData.put("project", config.get("project"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", config.get("uid"));
			connData.put("pwd", config.get("pwd"));
			session = MstrUtil.connectStandardSession(connData);
            
            for (final Map<String, Object> reportInfo : reportInfoList) {
            	final String objectId = (String) reportInfo.get("objectId");
            	final int type = (Integer) reportInfo.get("type");
                final Map<String, String> prompts = (Map<String, String>) reportInfo.get("prompts");
                
                final String logTmp2 = objectId.replaceAll(PortalCodeUtil.logChange,"");
                logger.debug("=> objectId:[{}]", logTmp1);
                logger.debug("=> type:[{}]", type);
                final String logTmp3 = prompts.toString().replaceAll(PortalCodeUtil.logChange,"");
                logger.debug("=> prompts:[{}]", logTmp2);

                try {
                    if (type == PortalCodeUtil.NUMBER_3) {
                        // executor.runReport(session, objectId, prompts);
                    	String logTmp4 = executor.getReportAnswerXML(session, objectId, prompts);
                    	logger.debug("=> [{}]", logTmp3);
                    } else if (type == PortalCodeUtil.NUMBER_55) {
                        executor.runDocument(session, objectId, prompts);
                    }
                } catch (WebObjectsException | InterruptedException e) {
                	logger.debug("!!! error", e);
                }
            }
        } catch (WebObjectsException e) {
        	logger.error("!!! error", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        logger.debug("MstrReportExecutor end.");
    }
}
