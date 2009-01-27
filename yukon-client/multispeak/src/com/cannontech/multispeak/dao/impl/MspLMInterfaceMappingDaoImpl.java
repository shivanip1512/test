package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.multispeak.dao.MspLMInterfaceMappingDao;
import com.cannontech.multispeak.db.MspLMInterfaceMapping;

public class MspLMInterfaceMappingDaoImpl implements MspLMInterfaceMappingDao {

	private String TABLENAME = "MspLMInterfaceMapping";
	private SimpleJdbcTemplate template;
    private NextValueHelper nextValueHelper;
    private PaoDao paoDao;

    private final ParameterizedRowMapper<MspLMInterfaceMapping> mspLMInterfaceMappingRowMapper = new ParameterizedRowMapper<MspLMInterfaceMapping>() {
        public MspLMInterfaceMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspLMInterfaceMapping(rs);
        };
    };

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(String strategyName, String substationName, int paobjectId) {
       String insertSql = "INSERT INTO " + TABLENAME + " (" + 
       					  " MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId)" + 
       					  " VALUES(?,?,?,?)";

        int result = template.update(insertSql.toString(),  
        		getNextMspLMInterfaceMappingId(), 
        		strategyName, substationName, paobjectId);
        return (result == 1);
    }
	
	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updatePaoIdForStrategyAndSubstation(String strategyName, String substationName, int paobjectId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE " + TABLENAME + " SET");
        sql.append("PaobjectId = ").appendArgument(paobjectId);
        sql.append("WHERE StrategyName = ").appendArgument(strategyName);
        sql.append("AND SubstationName = ").appendArgument(substationName);

        int result = template.update(sql.getSql(), sql.getArguments()); 
		return (result == 1);
	}
	
	@Override
	public MspLMInterfaceMapping getForId(int mspLMInterfaceMappingId) throws NotFoundException {
		try {
            String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                         " FROM " + TABLENAME +
                         " WHERE MspLMInterfaceMappingId = ? ";
			
            return template.queryForObject(sql, mspLMInterfaceMappingRowMapper, new Object[] { new Integer(mspLMInterfaceMappingId)});

		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NotFoundException("A MSP LM Interace with MspLmInterfaceMappingId " + mspLMInterfaceMappingId + " cannot be found.");
		}
	}

	@Override
	public MspLMInterfaceMapping getForStrategyAndSubstation(
			String strategyName, String substationName) throws NotFoundException {
		try {
            String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                         " FROM " + TABLENAME +
                         " WHERE StrategyName = ? " + 
                         " AND SubstationName = ? ";
			
            return template.queryForObject(sql, mspLMInterfaceMappingRowMapper, new Object[] { strategyName, substationName});            
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NotFoundException("A MSP LM Interace with StrategyName = " + strategyName + " and SubstationName = " + substationName + " cannot be found.");
		}
	}
	
	@Override
	public Integer findIdForStrategyAndSubstation(String strategyName, String substationName) {
		try {
            String sql = "SELECT MspLmInterfaceMappingId " +
                         " FROM " + TABLENAME +
                         " WHERE StrategyName = ? " + 
                         " AND SubstationName = ? ";
			
            return template.queryForInt(sql, new Object[] { strategyName, substationName});            
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<MspLMInterfaceMapping> getForStrategy(String strategyName) throws NotFoundException {
		try {
            String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                         " FROM " + TABLENAME +
                         " WHERE StrategyName = ? ";
			
            return template.query(sql, mspLMInterfaceMappingRowMapper, new Object[] { strategyName});            
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NotFoundException("No MSP LM Interace(s) with StrategyName = " + strategyName + " can be found.");
		}
	}
	
	@Override
	public List<MspLMInterfaceMapping> getAllMappings() {
        String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                     " FROM " + TABLENAME;
		
        return template.query(sql, mspLMInterfaceMappingRowMapper);         
	}
	
	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean remove(int mspLMInterfaceMappingId) {
		String sql = "DELETE FROM " + TABLENAME + 
					 " WHERE MspLmInterfaceMappingId = ?";
        int result = template.update(sql, mspLMInterfaceMappingId);
        return (result == 1);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean removeAllByStrategyName(String strategyName) {
		String sql = "DELETE FROM " + TABLENAME + 
					 " WHERE StrategyName = ?";
		int result = template.update(sql, strategyName);
		return (result == 1);
	}

    private MspLMInterfaceMapping createMspLMInterfaceMapping( ResultSet rset) throws SQLException {

        int mspLMInterfaceMappingID = rset.getInt(1);
        String strategyName = SqlUtils.convertDbValueToString(rset.getString(2).trim());
        String substationName = SqlUtils.convertDbValueToString(rset.getString(3).trim());
        int paobjectId = rset.getInt(4);
        String paoName = paoDao.getYukonPAOName(paobjectId);
        
        MspLMInterfaceMapping mspLMInterfaceMapping = new MspLMInterfaceMapping(mspLMInterfaceMappingID,
        																		strategyName,
        																		substationName,
        																		paobjectId,
        																		paoName);
        return mspLMInterfaceMapping;
    }
    
    private int getNextMspLMInterfaceMappingId() {
        return nextValueHelper.getNextValue("MSPLMINTERFACEMAPPING");
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate template) {
        this.template = template;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
