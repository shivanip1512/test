package com.cannontech.stars.dr.account.dao.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.dao.CallReportRowAndFieldMapper;
import com.cannontech.stars.dr.account.model.CallReport;

public class CallReportDaoImpl implements CallReportDao, InitializingBean {

	private static final RowAndFieldMapper<CallReport> rowAndFieldMapper;
	private YukonJdbcTemplate yukonJdbcTemplate;
	private NextValueHelper nextValueHelper;
	private SimpleTableAccessTemplate<CallReport> template;
	
    private ECMappingDao ecMappingDao;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    
    static {
        rowAndFieldMapper = new CallReportRowAndFieldMapper();
    }
    
    @Override
    public CallReport getCallReportByCallId(int callId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT crb.*");
    	sql.append("FROM CallReportBase crb");
    	sql.append("WHERE crb.CallId").eq(callId);
    	
    	return yukonJdbcTemplate.queryForObject(sql, rowAndFieldMapper);
    }
    
    @Override
    public List<CallReport> getAllCallReportByAccountId(int accountId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT crb.*");
    	sql.append("FROM CallReportBase crb");
    	sql.append("WHERE crb.AccountId").eq(accountId);
    	sql.append("ORDER BY crb.DateTaken DESC");
    	
    	return yukonJdbcTemplate.query(sql, rowAndFieldMapper);
    }
    
    @Override
    @Transactional
    public void insert(CallReport callReport, int energyCompanyId) {
    	
        template.insert(callReport);
        
        ecMappingDao.addECToCallReportMapping(energyCompanyId, callReport.getCallId());
    }

    @Override
    @Transactional
    public void update(CallReport callReport, int energyCompanyId) {
    	
    	if (callReport.getCallId() <= 0) {
    		throw new IllegalArgumentException("callId must be set");
    	}
    	
    	template.update(callReport);
    }
    
    @Override
    @Transactional
    public void delete(int callId) {
    	
    	List<Integer> callIds = Collections.singletonList(callId);
    	
        ecMappingDao.deleteECToCallReportMapping(callIds);
        chunkyJdbcTemplate.update(new CallReportBaseDeleteSqlGenerator(), callIds);
    }
    
    @Override
    @Transactional
    public void deleteAllCallsByAccount(int accountId) {
        List<Integer> callReportIds = getAllCallIdsByAccount(accountId);
        if(!callReportIds.isEmpty()) {
            ecMappingDao.deleteECToCallReportMapping(callReportIds);
            chunkyJdbcTemplate.update(new CallReportBaseDeleteSqlGenerator(), callReportIds);
        }
    }
    
    /**
     * Sql generator for deleting call reports from CallReportBase, useful for bulk deleteing
     * with chunking sql template.
     */
    private class CallReportBaseDeleteSqlGenerator implements SqlFragmentGenerator<Integer> {

        @Override
        public SqlFragmentSource generate(List<Integer> callReportIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM CallReportBase WHERE CallId IN (", callReportIds, ")");
            return sql;
        }
    }
    
    @Override
    public List<Integer> getAllCallIdsByAccount(int accountId){
        String sql = "SELECT CallId FROM CallReportBase WHERE AccountId = ?";
        List<Integer> workOrderIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper(), accountId);
        return workOrderIds;
    }
    
    @Override
    public Integer findCallIdByCallNumber(String callNumber, int energyCompanyId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT crb.CallId");
    	sql.append("FROM CallReportBase crb");
    	sql.append("JOIN ECToCallReportMapping ecmap ON (crb.CallId = ecmap.CallReportId)");
    	sql.append("WHERE ecmap.EnergyCompanyId").eq(energyCompanyId);
    	sql.append("AND crb.CallNumber").eq(callNumber);
    	
    	try {
    		return yukonJdbcTemplate.queryForInt(sql);
    	} catch (EmptyResultDataAccessException e) {
    		return null;
    	}
	}

    @Override
    public void afterPropertiesSet() throws Exception {
    	
    	template = new SimpleTableAccessTemplate<CallReport>(yukonJdbcTemplate, nextValueHelper);
    	template.setTableName("CallReportBase");
    	template.setPrimaryKeyField("CallId");
    	template.setFieldMapper(rowAndFieldMapper); 
    	
    	chunkyJdbcTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setECMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
}
