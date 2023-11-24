/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : DB 연결 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SecureBasicDataSource extends DriverManagerDataSource {
	
	
	/**
	 * <pre>
	 * 목적 : DB 사용자 ID 설정
	 * 매개변수 : 
	 * 	String username
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
	}
	
	
	/**
	 * <pre>
	 * 목적 : DB 사용자 패스워드 설정
	 * 매개변수 : 
	 * 	String password
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	@Override
	public void setPassword(String password) {
		super.setPassword(EncryptUtil.decrypt(password));
	}

}
