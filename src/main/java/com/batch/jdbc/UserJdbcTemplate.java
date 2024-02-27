package com.batch.jdbc;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.batch.properties.BatchProperties;

/**
 * 사용자 배치 userJdbcTemplate
 * @author mococo
 *
 */
@Repository("userJdbcTemplate")
public class UserJdbcTemplate {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(UserJdbcTemplate.class);
	
	/**
	 * jdbcTemplate
	 */
    /* default */ @Autowired /* default */ JdbcTemplate jdbcTemplate;
	
	
    /**
     * UserJdbcTemplate
     */
    public UserJdbcTemplate() {
    	logger.debug("UserJdbcTemplate");
    }
    
	
	/**
	 * 부서 정보
	 * @return
	 */
	public List<Map<String, Object>> selectEiamDepartment() {
		//SpotBugs 버그 하드코딩만 됨
		//return jdbcTemplate.queryForList(select 1 form dual);
		return jdbcTemplate.queryForList(BatchProperties.getProperty("Batch.eiamDepartmentList"));
	}
	
	
	/**
	 * 사용자 정보
	 * @return
	 */
	public List<Map<String, Object>> selectEiamUser() {
		return jdbcTemplate.queryForList(BatchProperties.getProperty("Batch.eiamUserList"));
	}
}
