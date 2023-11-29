package com.custom.mstr.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.custom.mstr.service.DemoService;
import com.mococo.biz.common.dao.SimpleBizDao;

@Service(value = "demoService")
public class DemoServiceImpl implements DemoService {
    @Autowired
    SimpleBizDao simpleBizDao;

    @Override
    public List<Map<String, Object>> list(Map<String, Object> param) {
        return null;
    }

    @Override
    public List<Map<String, Object>> select(Map<String, Object> param) {
        return null;
    }

    /**
     * 스프링 프레임워크 환경 중 mybatis를 이용한 기본 DB 처리 구성 확인
     */
    @Override
    public int dummyInsert(Map<String, Object> param) {
        int result = simpleBizDao.insert("dummy-insert", param);
        return result;
    }

    /**
     * 오류 발생 시 트랜잭션 롤백 처리
     */
    @Override
    @Transactional
    public void errorInMultiInsert(List<Map<String, Object>> param) {
        if (param == null) {
            return;
        }
        ;

        for (Map<String, Object> map : param) {
            simpleBizDao.insert("dummy-insert", map);

            if (StringUtils.equals((String) map.get("dummy"), "오류데이터")) {
                throw new RuntimeException("!!! '오류데이터'가 포함되었습니다.");
            }
        }
    }

    @Autowired
    DriverManagerDataSource dataSource;

    /**
     * JdbcTemplate를 이용한 bulk-insert 시 트랜잭션 적용
     */
    @Override
    @Transactional
    public void dummyBatchUpdate(List<Map<String, Object>> param) {
        if (param == null) {
            return;
        }
        ;

        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.batchUpdate("INSERT INTO dbo.DUMMY (DUMMY) VALUES (?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement statement, int i) throws SQLException {
                String data = (String) param.get(i).get("dummy");
                statement.setString(1, data);

                if (StringUtils.equals(data, "오류데이터")) {
                    throw new RuntimeException("!!! '오류데이터'가 포함되었습니다.");
                }
            }

            @Override
            public int getBatchSize() {
                return param.size();
            }
        });
    }
}
