package com.mococo.biz.common.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mococo.biz.common.dao.SimpleBizDao;

/**
 * 기본 쿼리 처리
 * @author mococo
 *
 */
@Repository("simpleBizDao")
public class SimpleBizDaoImpl extends SqlSessionDaoSupport implements SimpleBizDao {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(SimpleBizDaoImpl.class);
	
	
    /**
	 * UserController
	 */
    public SimpleBizDaoImpl() {
    	super();
    	logger.debug("SimpleBizDaoImpl");
    }
    
    
	/**
	 * setSqlSessionFactory
	 */
    @Override
	@Autowired
    @Qualifier("sqlSessionFactory")
    public void setSqlSessionFactory(final SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    
    /**
     * list
     */
    @Override
    public List<Map<String, Object>> list(final String mapperId, final Map<String, Object> param) {
        return getSqlSession().<Map<String, Object>>selectList(mapperId, param);
    }
    
    
    /**
     * list
     */
    @Override
    public List<Map<String, Object>> list(final String mapperId, final String param) {
        return getSqlSession().<Map<String, Object>>selectList(mapperId, param);
    }
    
    
    /**
     * select
     */
    @Override
    public Map<String, Object> select(final String mapperId, final Map<String, Object> param) {
        return (Map<String, Object>) getSqlSession().selectOne(mapperId, param);
    }
    
    
    /**
     * select
     */
    @Override
    public Map<String, Object> select(final String mapperId, final String param) {
        return (Map<String, Object>) getSqlSession().selectOne(mapperId, param);
    }
    
    
    /**
     * newSeq
     */
    @Override
    public String newSeq(final String mapperId) {
        return (String) getSqlSession().selectOne(mapperId);
    }
    
    
    /**
     * insert
     */
    @Override
    public int insert(final String mapperId, final Map<String, Object> param) {
        return getSqlSession().insert(mapperId, param);
    }
    
    /**
     * update
     */
    @Override
    public int update(final String mapperId, final Map<String, Object> param) {
        return getSqlSession().update(mapperId, param);
    }
    
    /**
     * delete
     */
    @Override
    public int delete(final String mapperId, final Map<String, Object> param) {
        return getSqlSession().delete(mapperId, param);
    }
}
