package com.mococo.microstrategy.sdk.prompt;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.web.objects.WebReportInstance;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.MstrCnst;
import com.mococo.microstrategy.sdk.prompt.vo.Prompt;
import com.mococo.microstrategy.sdk.util.MstrReportUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;

public class PromptChargerTest {
	private static final Logger logger = LoggerFactory.getLogger(PromptChargerTest.class);

	@Test
	public void testChargedCustomPrompt() throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
		Prompt prompt = PromptCharger.getChargedPrompt(null, "V111");
//		logger.debug("==> prompt: [{}]", prompt);
	}
	
	@Test
	public void testChargedMstrPrompt() {
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
			
			String[] objectIds = {
					// "256263D142248D56446F3A80AD100C06", // 주제영역 > 고객분석 > 고객 소득 분석 (프롬프트를 포함하지 않은 경우)
					"1288E3B944BFDFA145D55181DC7CD49F" // , // MicroStrategy 플랫폼 기능 > 사용자정의 그룹 및 콘솔리데이션 > 프롬프트된 사용자정의 그룹별 수익 및 이익 마진
					// "303A10EA40B95D0A4F93D0A018485F75"  // 개체 템플릿 > 리포트 > 성과 분석
			};
			
			for (String objectId : objectIds) {
				 WebReportInstance reportInstance = MstrReportUtil.getFinishedReportInstance(session, objectId, -1, -1);
				 
				 String logTmp1 = reportInstance.toString().replaceAll("[\r\n]","");
				 logger.debug("==> reportInstance:[{}]", logTmp1);
				 for (Enumeration<WebPrompt> e = reportInstance.getPrompts().elements(); e.hasMoreElements(); ) {
					 WebPrompt webPrompt = e.nextElement();
					 String logTmp2 = webPrompt.toString().replaceAll("[\r\n]","");
					 logger.debug("=> webPrompt:[{}]", logTmp2);
					 
					 Prompt prompt = PromptCharger.getChargedPrompt(session, webPrompt);
					 String logTmp3 = prompt.toString().replaceAll("[\r\n]","");
					 logger.debug("=> prompt:[{}]", logTmp3);
				 }
			}
		} catch (WebObjectsException e) {
			logger.error("!!! error", e);
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}
	
	@Test
	public void testChargedMstrPrompt2() {
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

			List<String> promptIdList = Arrays.asList(new String[] {
				"78EB1EE14770755DA97A7891280DF4DD", // 프폼프트 > 시간 제한
				"47943D504389AFD2007557A2D1CAAB52" 
			});
			
			for (String promptId : promptIdList) {
		    	WebPrompt webPrompt = (WebPrompt)session.getFactory().getObjectSource().getObject(promptId, EnumDSSXMLObjectTypes.DssXmlTypePrompt, true);
				Prompt prompt = PromptCharger.getChargedPrompt(session, webPrompt);
			}
		} catch (WebObjectsException e) {
			logger.error("!!! error", e);
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}

}
