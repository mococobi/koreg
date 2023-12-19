/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 사용자 배치 SQL 연결
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.batch.JdbcTemplate;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.batch.properties.BatchProperties;

@Repository(value = "userJdbcTemplate")
public class UserJdbcTemplate {
	
	@Autowired 
	JdbcTemplate jdbcTemplate;
	
	
	/**
	 * <pre>
	 * 목적 : EIAM 부서 정보를 가지고 옴
	 * 매개변수 : 없음
	 * 반환값 : java.util.List : List&lt;Map&lt;String, Object&gt;&gt;
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public List<Map<String, Object>> selectEiamDepartment() {
		return jdbcTemplate.queryForList(BatchProperties.getProperty("select.eaim.department"));
	}
	
	
	/**
	 * <pre>
	 * 목적 : EIAM 사용자 정보를 가지고 옴
	 * 매개변수 : 없음
	 * 반환값 : java.util.List : List&lt;Map&lt;String, Object&gt;&gt;
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public List<Map<String, Object>> selectEiamUser() {
		return jdbcTemplate.queryForList(BatchProperties.getProperty("select.eaim.user"));
	}
}
