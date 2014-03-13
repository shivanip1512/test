package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.commands.dao.model.CommandRequestUnsupported;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionResultDaoImpl implements CommandRequestExecutionResultDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private final static RowAndFieldMapper<CommandRequestExecutionResult> rowAndFieldMapper;
    private SimpleTableAccessTemplate<CommandRequestExecutionResult> template;
    private final static FieldMapper<CommandRequestUnsupported> unsupportedRowMapper;
    private SimpleTableAccessTemplate<CommandRequestUnsupported> unsupportedTemplate;

    private final static String selectResultIdsById;
    private final static String selectCountById;
    private final static String selectSuccessCountById;
    private final static String selectFailCountById;

    static {
		selectResultIdsById = "SELECT CRER.CommandRequestExecResultId FROM CommandRequestExecResult CRER WHERE CommandRequestExecId = ? ORDER BY CompleteTime";
		
		selectCountById = "SELECT COUNT(CRER.CommandRequestExecResultId) AS CrerCount FROM CommandRequestExecResult CRER WHERE CRER.CommandRequestExecId = ?";
		selectSuccessCountById = selectCountById + " AND CRER.ErrorCode = 0";
		selectFailCountById = selectCountById + " AND CRER.ErrorCode > 0";
		
		rowAndFieldMapper = new CommandRequestExecutionResultsRowAndFieldMapper();
		
		unsupportedRowMapper = new FieldMapper<CommandRequestUnsupported>() {
            @Override
            public Number getPrimaryKey(CommandRequestUnsupported commandUnsupported) {
                return commandUnsupported.getId();
            }

            @Override
            public void setPrimaryKey(CommandRequestUnsupported commandUnsupported, int id) {
                commandUnsupported.setId(id);
            }

            @Override
            public void extractValues(MapSqlParameterSource parameterHolder, CommandRequestUnsupported commandUnsupported) {
                parameterHolder.addValue("CommandRequestExecId", commandUnsupported.getCommandRequestExecId());
                parameterHolder.addValue("DeviceId", commandUnsupported.getDeviceId());
            }
        };
    }

    @Override
    public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult) {
    	template.save(commandRequestExecutionResult);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType) {
		
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM CommandRequestExecResult");
        sql.append("WHERE CommandRequestExecId").eq(commandRequestExecutionId);
        
        // filter
        sql.append("AND", reportFilterType.getConditionSqlFragmentSource());
        sql.append("ORDER BY CompleteTime");
        
        List<CommandRequestExecutionResult> list = 
            jdbcTemplate.query(sql.getSql(), rowAndFieldMapper, sql.getArguments());
        return list;
	}

    @Override
    public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId) {
    	
    	ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
	        @Override
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
	        	return rs.getInt("CommandRequestExecResultId");
	        }
	    };
	    
    	List<Integer> resultIds = 
    	    jdbcTemplate.query(selectResultIdsById, mapper, commandRequestExecutionId);
    	return resultIds;
    }
    
    @Override
    public int getCountByExecutionId(int commandRequestExecutionId) {
    	return jdbcTemplate.queryForInt(selectCountById, commandRequestExecutionId);
    }
    
    @Override
    public int getSucessCountByExecutionId(int commandRequestExecutionId) {
    	return jdbcTemplate.queryForInt(selectSuccessCountById, commandRequestExecutionId);
    }
    
	@Override
    public int getFailCountByExecutionId(int commandRequestExecutionId) {
		return jdbcTemplate.queryForInt(selectFailCountById, commandRequestExecutionId);
	}
	
	@Override
    public List<PaoIdentifier> getDeviceIdsByExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = getBaseDeviceSqlForExecutionId(commandRequestExecutionId);
		return jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);
	}

	@Override
    public List<PaoIdentifier> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = getBaseDeviceSqlForExecutionId(commandRequestExecutionId);
	    sql.append("  AND CRER.ErrorCode = 0");
		return jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);
	}

	@Override
    public List<PaoIdentifier> getFailDeviceIdsByExecutionId(int commandRequestExecutionId) {
	    SqlStatementBuilder sql = getBaseDeviceSqlForExecutionId(commandRequestExecutionId);
	    sql.append("  AND CRER.ErrorCode > 0");
		return jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);
	}

    @Override
    public List<PaoIdentifier> getUnsupportedDeviceIdsByExecutionId(int commandRequestExecutionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT YPO.PAObjectID, YPO.Type");
        sql.append("FROM CommandRequestUnsupported CRER");
        sql.append("  JOIN YukonPAObject YPO on CRER.DeviceId = YPO.PAObjectID");
        sql.append("WHERE CommandRequestExecId").eq(commandRequestExecutionId);
        return jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);
    }

    private SqlStatementBuilder getBaseDeviceSqlForExecutionId(int commandRequestExecutionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT YPO.PAObjectID, YPO.Type");
        sql.append("FROM CommandRequestExecResult CRER");
        sql.append("  JOIN YukonPAObject YPO on CRER.DeviceId = YPO.PAObjectID");
        sql.append("WHERE CommandRequestExecId").eq(commandRequestExecutionId);
        return sql;
    }

    @Override
    public void saveUnsupported(CommandRequestUnsupported unsupportedCmd) {
        unsupportedTemplate.save(unsupportedCmd);
    }

    @Override
    public int getUnsupportedCountByExecutionId(int commandRequestExecutionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM CommandRequestUnsupported");
        sql.append("WHERE CommandRequestExecId").eq(commandRequestExecutionId);
        
        return jdbcTemplate.queryForInt(sql);
    }

    @PostConstruct
    public void postConstruct() {
        template = new SimpleTableAccessTemplate<>(jdbcTemplate, nextValueHelper);
        template.setTableName("CommandRequestExecResult");
        template.setPrimaryKeyField("CommandRequestExecResultId");
        template.setFieldMapper(rowAndFieldMapper); 
        
        unsupportedTemplate = new SimpleTableAccessTemplate<>(jdbcTemplate, nextValueHelper);
        unsupportedTemplate.setTableName("CommandRequestUnsupported");
        unsupportedTemplate.setPrimaryKeyField("CommandRequestUnsupportedId");
        unsupportedTemplate.setFieldMapper(unsupportedRowMapper);
    }
}
