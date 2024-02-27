/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : DB 연결 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;

/**
 * SecureBasicDataSource
 * @author mococo
 *
 */
public class SecureBasicDataSource extends DriverManagerDataSource {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(SecureBasicDataSource.class);
    
    
    /**
     * SecureBasicDataSource
     */
	public SecureBasicDataSource() {
		super();
		logger.debug("SecureBasicDataSource");
	}
    
	/**
	 * DB 사용자 ID 설정
	 */
	@Nullable
	@Override
	public void setUsername(final String username) {
		super.setUsername(username);
	}
	
	
	/**
	 * DB 사용자 패스워드 설정
	 */
	@Override
	public void setPassword(final String password) {
		super.setPassword(EncryptUtil.decrypt(password));
	}

}
