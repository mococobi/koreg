/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 문자열 암복호화 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * EncryptUtil
 * @author mococo
 *
 */
public class EncryptUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(EncryptUtil.class);
	
	/**
	 * encToken
	 */
	private static byte[] encToken = PortalCodeUtil.encToken.getBytes();
	
	/**
	 * GCM_IV_LENGTH
	 */
	public static final int GCM_IV_LENGTH = 16;
	
	/**
	 * GCM_TAG_LENGTH
	 */
    public static final int GCM_TAG_LENGTH = 128;
    
    
    /**
     * EncryptUtil
     */
    public EncryptUtil() {
    	logger.debug("EncryptUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("EncryptUtil");
    }
	
    
    /**
     * 암호화
     * @param plaintext
     * @return
     */
	public static String encrypt(final String plaintext) {
		String rtnText = "";
		
		try {
			final byte[] gcmIv = Arrays.copyOfRange(encToken, 0, GCM_IV_LENGTH);
			final SecretKeySpec keySpec = new SecretKeySpec(encToken, "AES");
			final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, gcmIv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
			final byte[] cipherText = cipher.doFinal(plaintext.getBytes());
			rtnText = new String(Base64.getEncoder().encode(concatenate(gcmIv, cipherText)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
			| 	IllegalBlockSizeException | BadPaddingException e) {
			logger.error("!!! ", e);
		}
		
		return rtnText;
	}
	
	
	/**
	 * 복호화
	 * @param str
	 * @return
	 */
	public static String decrypt(final String str) {
		String rtnText = "";
		
		try {
			final byte[] ciphertext = Base64.getDecoder().decode(str.getBytes());
			final byte[] gcmIv = Arrays.copyOfRange(ciphertext, 0, GCM_IV_LENGTH);
			final byte[] cipherText = Arrays.copyOfRange(ciphertext, GCM_IV_LENGTH, ciphertext.length);
			final SecretKeySpec keySpec = new SecretKeySpec(encToken, "AES");
			final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, gcmIv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
			rtnText = new String(cipher.doFinal(cipherText));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
			| 	IllegalBlockSizeException | BadPaddingException e) {
			logger.error("!!! ", e);
		}
		
		return rtnText;
	}
	
	
	private static byte[] concatenate(final byte[] firstArray, final byte[] secondArray) {
		final byte[] result = Arrays.copyOf(firstArray, firstArray.length + secondArray.length);
		System.arraycopy(secondArray, 0, result, firstArray.length, secondArray.length);
		return result;
	}
}
