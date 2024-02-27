package com.mococo.microstrategy.sdk.prompt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.EnumWebPromptType;
import com.microstrategy.web.objects.WebConstantPrompt;
import com.microstrategy.web.objects.WebElementsPrompt;
import com.microstrategy.web.objects.WebExpressionPrompt;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebObjectsPrompt;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.web.objects.WebPrompts;
import com.microstrategy.web.objects.WebReportInstance;
import com.microstrategy.web.objects.WebReportSource;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.MstrCnst;
import com.mococo.microstrategy.sdk.prompt.dao.PromptDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrConstantPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrElementsPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrExpressionPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrObjectsPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.EncryptUtil;

public class PromptDaoTest {
	private static final Logger logger = LoggerFactory.getLogger(PromptDaoTest.class);

	public static void doElement(WebPrompt webPrompt) throws WebObjectsException {
	}
	
	public static void doExpression(PromptDao prompt) throws Exception {
        List<PromptElement> topElementList = prompt.getSuggestedAnswers();
        
        String logTmp1 = topElementList.toString().replaceAll("[\r\n]","");
        logger.debug("topElementList: [{}]", logTmp1);
        
        List<PromptElement> childElementList = prompt.getSuggestedAnswers(0, "8D679D4A11D3E4981000E787EC6DE8A4:20101");
        String logTmp2 = childElementList.toString().replaceAll("[\r\n]","");
        logger.debug("childElementList: [{}]", logTmp2);
	}
	
	@Test
	public void testMstrPromptDao() throws WebObjectsException {
		WebIServerSession session = null; 
		List<String> objectIdList = Arrays.asList(new String[] {"5604502D4327EE263BFACB93606BC929", "7378908B47B87E0D8405DFBA7F1E4F08", "1288E3B944BFDFA145D55181DC7CD49F"});
		
		try {
		    WebObjectsFactory factory = WebObjectsFactory.getInstance();
		    session = factory.getIServerSession();
		    
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", MstrCnst.SERVER);
			connData.put("project", MstrCnst.PROJECT);
			connData.put("port", MstrCnst.PORT);
			connData.put("localeNum", MstrCnst.LOCALE);
			connData.put("uid", MstrCnst.USERID);
			connData.put("pwd", MstrCnst.PWD);
			session = MstrUtil.connectStandardSession(connData);
		    
		    WebReportSource reportSource = session.getFactory().getReportSource(); 
		    for (String reportId : objectIdList) {
		    	try {
				    WebReportInstance reportInstance = reportSource.getNewInstance(reportId);
			        WebPrompts webPrompts = reportInstance.getPrompts();	
			        
			        for (int i = 0; i < webPrompts.size(); i++) {
			        	WebPrompt webPrompt = webPrompts.get(i);
			        	PromptDao promptDao = null;
			        	
			        	String logTmp1 = webPrompt.getName().replaceAll("[\r\n]","");
			        	logger.debug("==> name: [{}]", logTmp1);
			            switch (webPrompt.getPromptType()) {
			            case EnumWebPromptType.WebPromptTypeConstant:
			            	logger.debug("WebPromptTypeConstant");
			            	promptDao = new MstrConstantPromptApiDao((WebConstantPrompt)webPrompt);
			            	break;
			            case EnumWebPromptType.WebPromptTypeElements:
			            	logger.debug("WebPromptTypeElements");
			            	promptDao = new MstrElementsPromptApiDao((WebElementsPrompt)webPrompt);
			            	break;
			            case EnumWebPromptType.WebPromptTypeObjects:
			            	logger.debug("WebPromptTypeObjects");
			            	promptDao = new MstrObjectsPromptApiDao((WebObjectsPrompt)webPrompt);
			            	break;
			            case EnumWebPromptType.WebPromptTypeExpression:
			            	logger.debug("WebPromptTypeExpression");
			            	promptDao = new MstrExpressionPromptApiDao(session, (WebExpressionPrompt)webPrompt);
			            	break;
			            case EnumWebPromptType.WebPromptTypeDimty:
			            	logger.debug("WebPromptTypeDimty");
//			            	throw new RuntimeException("prompt type dimty.");
			            case EnumWebPromptType.WebPromptTypeUnsupported:
			            	logger.debug("WebPromptTypeUnsupported");
//			            	throw new RuntimeException("unsuppoert prompt type.");
			            }
			            
//			            logger.debug("dao class name: [{}]", promptDao.getClass().getName().toString().replaceAll("[\r\n]",""));
//			            logger.debug("defaultAnswer: [{}]", promptDao.getDefaultAnswer());
//			            logger.debug("defaultAnswers: [{}]", promptDao.getDefaultAnswers());
//			            logger.debug("suggestedAnswers: [{}]", promptDao.getSuggestedAnswers());
			        }
		    	} catch (Exception e) {
		    		logger.error("testMstrPromptDao Exception", e);
		    	}
		    }
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}

	/**
	 * '시간', '시간(단일계층)' 프롬프트에 대한 프롬프트 DAO 테스트
	 * 		'시간' 프롬프트인 경우 '월' 애트리뷰트의 각 엘리먼트에 대하여, 하위 애트리뷰트인 '날짜' 애트리뷰트에 필터 생성 적용 후 필터된 결과 출력
	 * 		'시간(계층)' 프롬프트인 경우 '연도' 애트리뷰트의 각 엘리먼트에 대하여, 하위 애트리뷰트인 '분기' 애트리뷰트에 필터 생성 적용 후 필터된 결과 출력 
	 * @throws Exception
	 */
	@Test
	public void testExpressionPromptDaoTest() throws Exception {
		WebIServerSession session = null;
		
		
		
		try {
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", MstrCnst.SERVER);
			connData.put("project", MstrCnst.PROJECT);
			connData.put("port", MstrCnst.PORT);
			connData.put("localeNum", MstrCnst.LOCALE);
			connData.put("uid", MstrCnst.USERID);
			connData.put("pwd", MstrCnst.PWD);
			session = MstrUtil.connectStandardSession(connData);
		    
		    // 78EB1EE14770755DA97A7891280DF4DD 인 경우 Entry Point가 다수이며, '년 개월' 애트리뷰트로 조회된다. -> Entry Point가 1만 필요
		    // 47943D504389AFD2007557A2D1CAAB52 인 경우 Entry Point가 '연도' 하나이므로, '연도' 애트리뷰트 조회  
		    // List<String> objectIdList = Arrays.asList(new String[] {"78EB1EE14770755DA97A7891280DF4DD", "47943D504389AFD2007557A2D1CAAB52"});
		    List<String> objectIdList = Arrays.asList(new String[] {"78EB1EE14770755DA97A7891280DF4DD"});
		    for (String objectId : objectIdList) {
		    	WebObjectInfo webObjectInfo = session.getFactory().getObjectSource().getObject(objectId, EnumDSSXMLObjectTypes.DssXmlTypePrompt, true);
		    	
		    	PromptDao promptDao = new MstrExpressionPromptApiDao(session, (WebExpressionPrompt)webObjectInfo);
		    	for (PromptElement element : promptDao.getSuggestedAnswers()) {
		    		String logTmp1 = element.toString().replaceAll("[\r\n]","");
		    		logger.debug("==> element: [{}]", logTmp1);
		    		
		    		PromptDao promptDao2 = new MstrExpressionPromptApiDao(session, (WebExpressionPrompt)webObjectInfo);
		    		
		    		for (PromptElement element2 : promptDao2.getSuggestedAnswers(0, element.getId())) {
		    			String logTmp2 = element2.toString().replaceAll("[\r\n]","");
		    			logger.debug("==> child element: [{}]", logTmp2);
		    		}
		    	}
		    }
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}
	
}
