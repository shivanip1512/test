package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.multispeak.dao.MspLmInterfaceMappingDao;
import com.cannontech.multispeak.db.MspLmMapping;
import com.cannontech.yukon.IDatabaseCache;

public class MspLMInterfaceMappingDaoImpl implements MspLmInterfaceMappingDao {

    private final String TABLENAME = "MspLMInterfaceMapping";
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private final RowMapper<MspLmMapping> mspLMInterfaceMappingRowMapper = new RowMapper<MspLmMapping>() {
        @Override
        public MspLmMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspLMInterfaceMapping(rs);
        };
    };

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(String strategyName, String substationName, int paobjectId) {
        String insertSql = "INSERT INTO " + TABLENAME + " (" +
                           " MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId)" +
                           " VALUES(?,?,?,?)";

        int result = jdbcTemplate.update(insertSql.toString(),
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

        int result = jdbcTemplate.update(sql.getSql(), sql.getArguments());
        return (result == 1);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean updateMappingById(int mappingId, String strategyName, String substationName, int paobjectId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE " + TABLENAME);
        sql.set("PaobjectId", paobjectId, "StrategyName", strategyName, "SubstationName", substationName);
        sql.append("WHERE MspLMInterfaceMappingId").eq(mappingId);
        
        int result = jdbcTemplate.update(sql.getSql(), sql.getArguments());
        return (result == 1);
    }

    @Override
    public MspLmMapping getForId(int mspLMInterfaceMappingId) throws NotFoundException {
        try {
            String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                         " FROM " + TABLENAME +
                         " WHERE MspLMInterfaceMappingId = ? ";

            return jdbcTemplate.queryForObject(sql, mspLMInterfaceMappingRowMapper, new Object[] { new Integer(mspLMInterfaceMappingId) });

        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP LM Interace with MspLmInterfaceMappingId " + mspLMInterfaceMappingId + " cannot be found.");
        }
    }

    @Override
    public MspLmMapping getForStrategyAndSubstation(String strategyName, String substationName) throws NotFoundException {
        try {
            String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                         " FROM " + TABLENAME +
                         " WHERE StrategyName = ? " +
                         " AND SubstationName = ? ";

            return jdbcTemplate.queryForObject(sql, mspLMInterfaceMappingRowMapper, new Object[] { strategyName, substationName });
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP LM Interace with StrategyName = " + strategyName + " and SubstationName = " + substationName + " cannot be found.");
        }
    }

    @Override
    public Integer findIdForStrategyAndSubstation(String strategyName, String substationName) {
        try {
            final SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT MspLmInterfaceMappingId");
            sql.append("FROM " + TABLENAME);
            sql.append("WHERE StrategyName").eq(strategyName);
            sql.append("  AND SubstationName").eq(substationName);

            return jdbcTemplate.queryForInt(sql);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<MspLmMapping> getAllMappings() {
        String sql = "SELECT MspLmInterfaceMappingId, StrategyName, SubstationName, PaobjectId " +
                     " FROM " + TABLENAME;

        return jdbcTemplate.query(sql, mspLMInterfaceMappingRowMapper);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(int mspLMInterfaceMappingId) {
        String sql = "DELETE FROM " + TABLENAME +
                     " WHERE MspLmInterfaceMappingId = ?";
        int result = jdbcTemplate.update(sql, mspLMInterfaceMappingId);
        return (result == 1);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean removeAllByStrategyName(String strategyName) {
        String sql = "DELETE FROM " + TABLENAME +
                     " WHERE StrategyName = ?";
        int result = jdbcTemplate.update(sql, strategyName);
        return (result == 1);
    }

    private MspLmMapping createMspLMInterfaceMapping(ResultSet rset) throws SQLException {

        int mspLMInterfaceMappingID = rset.getInt(1);
        String strategyName = SqlUtils.convertDbValueToString(rset.getString(2).trim());
        String substationName = SqlUtils.convertDbValueToString(rset.getString(3).trim());
        int paobjectId = rset.getInt(4);
        String paoName = databaseCache.getAllPaosMap().get(paobjectId).getPaoName();

        MspLmMapping mspLMInterfaceMapping = new MspLmMapping(mspLMInterfaceMappingID,
                                                              strategyName,
                                                              substationName,
                                                              paobjectId,
                                                              paoName);
        return mspLMInterfaceMapping;
    }

    private int getNextMspLMInterfaceMappingId() {
        return nextValueHelper.getNextValue("MSPLMINTERFACEMAPPING");
    }
}