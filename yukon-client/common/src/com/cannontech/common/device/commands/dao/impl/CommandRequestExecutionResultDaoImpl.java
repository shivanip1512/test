package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionResultDaoImpl implements CommandRequestExecutionResultDao, InitializingBean {

	private static final RowAndFieldMapper<CommandRequestExecutionResult> rowAndFieldMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CommandRequestExecutionResult> template;
    
    private static final String selectResultIdsById;
    
    private static final String selectCountById;
    private static final String selectSuccessCountById;
    private static final String selectFailCountById;
    
    static {
    	
		selectResultIdsById = "SELECT CRER.CommandRequestExecResultId FROM CommandRequestExecResult CRER WHERE CommandRequestExecId = ? ORDER BY CompleteTime";
		
		selectCountById = "SELECT COUNT(CRER.CommandRequestExecResultId) AS CrerCount FROM CommandRequestExecResult CRER WHERE CRER.CommandRequestExecId = ?";
		selectSuccessCountById = selectCountById + " AND CRER.ErrorCode = 0";
		selectFailCountById = selectCountById + " AND CRER.ErrorCode > 0";
		
		rowAndFieldMapper = new CommandRequestExecutionResultsRowAndFieldMapper();
    }
    
    // SAVE OR UPDATE
    public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult) {
    	template.save(commandRequestExecutionResult);
    }
    
    // GET RESULTS BY CRE ID
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType) {
		    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM CommandRequestExecResult");
        sql.append("WHERE CommandRequestExecId = ").appendArgument(commandRequestExecutionId);
        
        // filter
        sql.append("AND", reportFilterType.getConditionSqlFragmentSource());
        
        sql.append("ORDER BY CompleteTime");
        
        List<CommandRequestExecutionResult> list = 
            yukonJdbcTemplate.query(sql.getSql(), rowAndFieldMapper, sql.getArguments());
        return list;
	}
    
    // GET RESULT IDS BY CRE ID
    public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId) {
    	
    	ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
	        public Integer mapRow(ResultSet rs, int num) throws SQLException{
	        	return rs.getInt("CommandRequestExecResultId");
	        }
	    };
	    
    	List<Integer> resultIds = 
    	    yukonJdbcTemplate.query(selectResultIdsById, mapper, commandRequestExecutionId);
    	return resultIds;
    }
    
    // GET RESULT COUNT BY CRE ID
    public int getCountByExecutionId(int commandRequestExecutionId) {
    	return yukonJdbcTemplate.queryForInt(selectCountById, commandRequestExecutionId);
    }
    
    public int getSucessCountByExecutionId(int commandRequestExecutionId) {
    	return yukonJdbcTemplate.queryForInt(selectSuccessCountById, commandRequestExecutionId);
    }
    
	public int getFailCountByExecutionId(int commandRequestExecutionId) {
		return yukonJdbcTemplate.queryForInt(selectFailCountById, commandRequestExecutionId);
	}
	
	private SqlStatementBuilder getBaseDeviceSqlForExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT DISTINCT YPO.PAObjectID, YPO.Type");
	    sql.append("FROM CommandRequestExecResult CRER");
	    sql.append("  JOIN YukonPAObject YPO on CRER.DeviceId = YPO.PAObjectID");
	    sql.append("WHERE CommandRequestExecId").eq(commandRequestExecutionId);
	    return sql;
	}

	public List<PaoIdentifier> getDeviceIdsByExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = getBaseDeviceSqlForExecutionId(commandRequestExecutionId);
		return yukonJdbcTemplate.query(sql, new YukonPaoRowMapper());
	}

	public List<PaoIdentifier> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = getBaseDeviceSqlForExecutionId(commandRequestExecutionId);
	    sql.append("  AND CRER.ErrorCode = 0");
		return yukonJdbcTemplate.query(sql, new YukonPaoRowMapper());
	}
	public List<PaoIdentifier> getFailDeviceIdsByExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = getBaseDeviceSqlForExecutionId(commandRequestExecutionId);
	    sql.append("  AND CRER.ErrorCode > 0");
		return yukonJdbcTemplate.query(sql, new YukonPaoRowMapper());
	}
    
    
    public void afterPropertiesSet() throws Exception {
    	template = new SimpleTableAccessTemplate<CommandRequestExecutionResult>(yukonJdbcTemplate,
    	                                                                        nextValueHelper);
    	template.setTableName("CommandRequestExecResult");
    	template.setPrimaryKeyField("CommandRequestExecResultId");
    	template.setFieldMapper(rowAndFieldMapper); 
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}

}
