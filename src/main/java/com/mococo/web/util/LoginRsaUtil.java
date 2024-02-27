package com.mococo.web.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.login.controller.LoginController;

/**
 * LoginRsaUtil
 * @author mococo
 *
 */
public class LoginRsaUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	/**
	 * 개인키 session key
	 */
    public static final String rsaWebKey = "_RSA_SF_KEY_";
    
    /**
     * rsa transformation
     */
    public static final String rsaInstance = "RSA";
    
    
    /**
     * LoginRsaUtil
     */
    public LoginRsaUtil() {
    	logger.debug("LoginRsaUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("LoginRsaUtil");
    }
	
	
    /**
     * 로그인 RSA 복호화
     * @param privateKey
     * @param securedValue
     * @return
     */
    public static String decryptRsa(final PrivateKey privateKey, final String securedValue) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
    	final Cipher cipher = Cipher.getInstance(rsaInstance);
    	final byte[] encryptedBytes = hexToByteArray(securedValue);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, "utf-8");
    }
    
    
    /**
     * 16진 문자열을 byte 배열로 변환
     * @param hex
     * @return
     */
    private static byte[] hexToByteArray(final String hex) {
    	byte[] rtnByte;  
    	
        if (hex == null || hex.length() % 2 != 0) { 
        	rtnByte = new byte[] {};
        } else {
        	rtnByte = new byte[hex.length() / 2];
            for (int i = 0; i < hex.length(); i += 2) {
            	final byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            	rtnByte[(int) Math.floor(i / 2)] = value;
            }
        }
        
        return rtnByte;
    }
}
