package com.mococo.microstrategy.sdk.prompt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.MstrCnst;
import com.mococo.microstrategy.sdk.prompt.vo.Report;
import com.mococo.microstrategy.sdk.util.MstrUtil;

public class ReportChargerTest {
	private static final Logger logger = LoggerFactory.getLogger(ReportChargerTest.class);
	
	@Test
	public void testChargeReport() throws Exception {
		Object[][] params = new Object[][] {
			{EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition, "256263D142248D56446F3A80AD100C06"}, // 주제영역 > 고객분석 > 고객 소득 분석 (프롬프트를 포함하지 않은 경우)
			{EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition, "C7589C1E4A318B7513C8BAA040B7752E"}, // MicroStrategy 플랫폼 기능 > 사용자정의 그룹 및 콘솔리데이션 > 프롬프트된 사용자정의 그룹별 수익 및 이익 마진
			{EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition, "303A10EA40B95D0A4F93D0A018485F75"}  // 개체 템플릿 > 리포트 > 성과 분석
		};

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
			
		    for (Object[] param : params) {
		    	Report report = ReportCharger.chargeObject(session, (Integer)param[0], (String)param[1]);
		    	
		    	String logTmp1 = report.toString().replaceAll("[\r\n]","");
		    	logger.debug("==> report: [{}]", logTmp1);
		    }
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}

}
