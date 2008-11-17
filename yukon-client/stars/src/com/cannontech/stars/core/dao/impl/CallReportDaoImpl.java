package com.cannontech.stars.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.core.dao.CallReportDao;
import com.cannontech.stars.core.dao.ECMappingDao;

public class CallReportDaoImpl implements CallReportDao, InitializingBean {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ECMappingDao ecMappingDao;
    private ChunkingSqlTemplate<Integer> chunkyJdbcTemplate;
    
    @Override
    @Transactional
    public void deleteByAccount(int accountId) {
        List<Integer> callReportIds = getByAccount(accountId);
        if(!callReportIds.isEmpty()) {
            ecMappingDao.deleteECToCallReportMapping(callReportIds);
            chunkyJdbcTemplate.update(new CallReportBaseDeleteSqlGenerator(), callReportIds);
        }
    }
    
    /**
     * Sql generator for deleting call reports from CallReportBase, useful for bulk deleteing
     * with chunking sql template.
     */
    private class CallReportBaseDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> callReportIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM CallReportBase WHERE CallId IN (", callReportIds, ")");
            return sql.toString();
        }
    }
    
    @Override
    public List<Integer> getByAccount(int accountId){
        String sql = "SELECT CallId FROM CallReportBase WHERE AccountId = ?";
        List<Integer> workOrderIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), accountId);
        return workOrderIds;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setECMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
    }
}
