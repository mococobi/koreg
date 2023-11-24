package com.mococo.biz.common.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mococo.biz.common.dao.SimpleBizDao;

@Repository(value = "simpleBizDao")
public class SimpleBizDaoImpl extends SqlSessionDaoSupport implements SimpleBizDao {

    @Autowired
    @Qualifier("sqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public List<Map<String, Object>> list(String mapperId, Map<String, Object> param) {
        return getSqlSession().<Map<String, Object>>selectList(mapperId, param);
    }

    @Override
    public List<Map<String, Object>> list(String mapperId, String param) {
        return getSqlSession().<Map<String, Object>>selectList(mapperId, param);
    }

    @Override
    public Map<String, Object> select(String mapperId, Map<String, Object> param) {
        return (Map<String, Object>) getSqlSession().selectOne(mapperId, param);
    }

    @Override
    public Map<String, Object> select(String mapperId, String param) {
        return (Map<String, Object>) getSqlSession().selectOne(mapperId, param);
    }

    @Override
    public String newSeq(String mapperId) {
        return (String) getSqlSession().selectOne(mapperId);
    }

    @Override
    public int insert(String mapperId, Map<String, Object> param) {
        return getSqlSession().insert(mapperId, param);
    }

    @Override
    public int update(String mapperId, Map<String, Object> param) {
        return getSqlSession().update(mapperId, param);
    }

    @Override
    public int delete(String mapperId, Map<String, Object> param) {
        return getSqlSession().delete(mapperId, param);
    }
}
