package com.mococo.web.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebObjectsException;

public class EncryptUtilTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtilTest.class);

	
	@Test
	public void testEncrypt() throws WebObjectsException {
		String testVal = "";
		
		String testEnc = EncryptUtil.encrypt(testVal);
		LOGGER.debug("String encrypt : [{}]", testEnc);
		
		String testDec = EncryptUtil.decrypt(testEnc);
		LOGGER.debug("String decrypt : [{}]", testDec);
	}
	
	
	@Test
	public void testDecrypt() throws WebObjectsException {
		String testVal = "";
		
		String testDec = EncryptUtil.decrypt(testVal);
		LOGGER.debug("String decrypt : [{}]", testDec);
	}
	
}
