package com.mococo.web.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebObjectsException;

public class EncryptUtilTest {
	private static final Logger logger = LoggerFactory.getLogger(EncryptUtilTest.class);

	
	@Test
	public void testEncrypt() throws WebObjectsException {
		final String testVal = "1";
		
		final String testEnc = EncryptUtil.encrypt(testVal).replaceAll("[\r\n]","");
		logger.debug("String encrypt : [{}]", testEnc);
		
		final String testDec = EncryptUtil.decrypt(testEnc).replaceAll("[\r\n]","");
		logger.debug("String decrypt : [{}]", testDec);
	}
	
	
	@Test
	public void testDecrypt() throws WebObjectsException {
		final String testVal = "bW9jb2NvcG9ydGFsMjAyM+Wk9cbgd2r7RT2vlwpWbaCK";
		
		final String testDec = EncryptUtil.decrypt(testVal).replaceAll("[\r\n]","");
		logger.debug("String decrypt : [{}]", testDec);
		
	}
	
}
