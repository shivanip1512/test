package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;

public class StarsInventoryBaseDaoImpl implements StarsInventoryBaseDao {
    private static final ParameterizedRowMapper<LiteInventoryBase> smartInventoryRowMapper = 
        new SmartLiteInventoryBaseRowMapper();
    private static final String selectInventorySql;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        selectInventorySql = "SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId " +
                             "FROM InventoryBase ib " +
                             "LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId " +
                             "LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId " +
                             "JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId " +
                             "JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId "; 
    }
    
    @Override
	@Transactional(readOnly = true)
	public LiteInventoryBase getById(final int inventoryId) {
		SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
		sqlBuilder.append(selectInventorySql);
		sqlBuilder.append("WHERE ib.InventoryId = ?");

		String sql = sqlBuilder.toString(); 
		List<LiteInventoryBase> liteInventoryList = simpleJdbcTemplate.query(sql, 
		                                                                     smartInventoryRowMapper,
		                                                                     inventoryId);
		if(liteInventoryList.size() == 0) {
			return null;
		}
		
		LiteInventoryBase inventoryBase = liteInventoryList.get(0);
		
		return inventoryBase;
	}

    @Override
    @Transactional(readOnly = true)
    public List<LiteInventoryBase> getByIds(final Set<Integer> inventoryIds) {
        final List<LiteInventoryBase> resultList = new ArrayList<LiteInventoryBase>(inventoryIds.size());

        for (final Integer inventoryId : inventoryIds) {
            resultList.add(getById(inventoryId));
        }
        
        return resultList;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteInventoryBase> getAllByEnergyCompanyId(final int energyCompanyId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append(selectInventorySql);
        sqlBuilder.append(" WHERE etim.EnergyCompanyID = ?");
        
        String sql = sqlBuilder.toString();
        List<LiteInventoryBase> list = simpleJdbcTemplate.query(sql, 
                                                                smartInventoryRowMapper,
                                                                energyCompanyId);
        return list;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
