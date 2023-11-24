package com.mococo.microstrategy.sdk.prompt;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

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
	public void testChargedCustomPrompt() throws Exception {
		Prompt prompt = PromptCharger.getChargedPrompt(null, "V111");
		logger.debug("==> prompt: [{}]", prompt);
	}
	
	@Test
	public void testChargedMstrPrompt() {
		WebIServerSession session = null;
		
		try {
			session = MstrUtil.connectSession(MstrCnst.SERVER, MstrCnst.PROJECT, MstrCnst.USERID, MstrCnst.PWD);
		
			String[] objectIds = {
					// "256263D142248D56446F3A80AD100C06", // 주제영역 > 고객분석 > 고객 소득 분석 (프롬프트를 포함하지 않은 경우)
					"C7589C1E4A318B7513C8BAA040B7752E" // , // MicroStrategy 플랫폼 기능 > 사용자정의 그룹 및 콘솔리데이션 > 프롬프트된 사용자정의 그룹별 수익 및 이익 마진
					// "303A10EA40B95D0A4F93D0A018485F75"  // 개체 템플릿 > 리포트 > 성과 분석
			};
			
			for (String objectId : objectIds) {
				 WebReportInstance reportInstance = MstrReportUtil.getFinishedReportInstance(session, objectId, -1, -1);
				 
				 logger.debug("==> reportInstance:[{}]", reportInstance);
				 for (Enumeration<WebPrompt> e = reportInstance.getPrompts().elements(); e.hasMoreElements(); ) {
					 WebPrompt webPrompt = e.nextElement();
					 
					 logger.debug("=> webPrompt:[{}]", webPrompt);
					 Prompt prompt = PromptCharger.getChargedPrompt(session, webPrompt);
					 logger.debug("=> prompt:[{}]", prompt);
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
			session = MstrUtil.connectSession(MstrCnst.SERVER, MstrCnst.PROJECT, MstrCnst.USERID, MstrCnst.PWD);

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
