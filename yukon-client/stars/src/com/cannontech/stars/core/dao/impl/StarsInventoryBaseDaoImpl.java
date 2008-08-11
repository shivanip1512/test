package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.LiteInventoryBaseMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;

public class StarsInventoryBaseDaoImpl implements StarsInventoryBaseDao {
    
    private SimpleJdbcTemplate jdbcTemplate = null;
    private ECMappingDao ecMappingDao = null;
    
    
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
    
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
    
    @Override
	@Transactional(readOnly = true)
	public LiteInventoryBase getById(final int inventoryId) {

		LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, mhb.*");
		sql.append("FROM InventoryBase ib");
		sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
		sql.append("WHERE ib.InventoryId = ?");

		LiteInventoryBase liteInventory = jdbcTemplate.queryForObject(
				sql.toString(), 
				new LiteInventoryBaseMapper(energyCompany),
				inventoryId);

		return liteInventory;
	}

    @Override
    public List<LiteInventoryBase> getByIds(final Set<Integer> inventoryIds) {
        final List<LiteInventoryBase> resultList = new ArrayList<LiteInventoryBase>(inventoryIds.size());

        for (final Integer inventoryId : inventoryIds) {
            resultList.add(getById(inventoryId));
        }
        
        return resultList;
    }
    
}
