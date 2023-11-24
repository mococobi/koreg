package com.mococo.microstrategy.sdk.prompt;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mococo.microstrategy.sdk.util.MstrReportExecutor;

public class MstrReportExecutorTest {
	private static final Logger logger = LoggerFactory.getLogger(MstrReportExecutorTest.class);

	@Test
	public void testGetConstantValue() {
		String[] tokens = {"{d}", "{d-10}", "{d-mf}", "{d-bmf}", "{d-bmd}", "hello", "{m}", "{m-1}"};
		for (String token : tokens) {
			logger.debug("token:[{}], value:[{}]", token, MstrReportExecutor.getConstantValue(token));
		}
	}

}
