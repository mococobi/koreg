package com.mococo.microstrategy.sdk.prompt;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mococo.microstrategy.sdk.prompt.config.ConfigManager;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

public class ConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);	

	/**
	 * 프롬프트 확장속성값 조회 기능 테스트
	 * 		- 어플리케이션 설정값으로 지정된 처리 방법으로 리소스에 접근 프롬프트의 확장속성을 조회  
	 */
	@Test
	public void testGetObjectConfig() {
		ConfigManager manager = ConfigManager.getInstance();
		
		// id는 프롬프트ID
		String[] ids = {null, "11", "111", "V111"};
		
		for (String id : ids) {
			ObjectConfig config = manager.getObjectConfig(null, id);
			logger.debug("==> id: [{}]", id);
			
			if (config != null && config.keySet() != null) {
				for (String key : config.keySet()) {
					String logTmp1 = key.replaceAll("[\r\n]","");
					String logTmp2 = config.get(key).toString().replaceAll("[\r\n]","");
					logger.debug("==> key: [{}], value: [{}]", logTmp1, logTmp2);
				}
			}
		}
	}

}
