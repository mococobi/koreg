/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 문자열 암복호화 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptUtil {
	private static final Logger LOGGER = LogManager.getLogger(EncryptUtil.class);
	private static String iv;
	private static Key keySpec;
	
	static {
		try {
			String key = CustomProperties.getProperty("enc.token");
			iv = key.substring(0, 16);
			
			byte[] b = key.getBytes("UTF-8");
			int len = b.length;
			
			byte[] keyBytes = new byte[32];
			if(len > keyBytes.length) {
				len = keyBytes.length;
			}
			
			System.arraycopy(b, 0, keyBytes, 0, len);
			keySpec = new SecretKeySpec(keyBytes, "AES");
		} catch (IOException e) {
			LOGGER.error("!!! error", e);
		}
	}
	
	
	/**
	 * <pre>
	 * 목적 : 문자열 암호화
	 * 매개변수 : 
	 * 	String token
	 * 반환값 : java.lang.String
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static String encrypt(String token) {
		String result = null;
		
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
			byte[] encrypted = c.doFinal(token.getBytes("UTF-8"));
			result = new String(Base64.getEncoder().encodeToString(encrypted));
		} catch (IOException e) {
			LOGGER.error("!!! error", e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("!!! error", e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("!!! error", e);
		} catch (InvalidKeyException e) {
			LOGGER.error("!!! error", e);
		} catch (InvalidAlgorithmParameterException e) {
			LOGGER.error("!!! error", e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("!!! error", e);
		} catch (BadPaddingException e) {
			LOGGER.error("!!! error", e);
		}
		
		return result;
	}
	
	
	/**
	 * <pre>
	 * 목적 : 문자열 복호화
	 * 매개변수 : 
	 * 	String token
	 * 반환값 : java.lang.String
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static String decrypt(String token) { 
		String result = null;
		
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
			byte[] byteStr = Base64.getDecoder().decode(token.getBytes());
			result = new String(c.doFinal(byteStr), "UTF-8");
		} catch (IOException e) {
			LOGGER.error("!!! error", e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("!!! error", e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("!!! error", e);
		} catch (InvalidKeyException e) {
			LOGGER.error("!!! error", e);
		} catch (InvalidAlgorithmParameterException e) {
			LOGGER.error("!!! error", e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("!!! error", e);
		} catch (BadPaddingException e) {
			LOGGER.error("!!! error", e);
		}
		
		return result;
	}
}
