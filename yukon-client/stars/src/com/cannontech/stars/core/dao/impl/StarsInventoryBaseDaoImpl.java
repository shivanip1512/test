package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
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
    public List<LiteInventoryBase> getAllByEnergyCompanyList(List<LiteStarsEnergyCompany> energyCompanyList) {
        
    	List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
    	
    	SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append(selectInventorySql);
        sqlBuilder.append(" WHERE etim.EnergyCompanyID IN (" + StringUtils.join(ecIdList, ",") + ")");
        
        String sql = sqlBuilder.toString();
        List<LiteInventoryBase> list = simpleJdbcTemplate.query(sql, smartInventoryRowMapper);
        return list;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

	@Override
	public List<LiteStarsLMHardware> getAllLMHardware(List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("WHERE etim.EnergyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");

		List<LiteStarsLMHardware> liteHardwareList = simpleJdbcTemplate.query(
				sql.toString(), 
				new LiteStarsLMHardwareRowMapper());
		
		return liteHardwareList;
	}
	
	@Override
	public List<LiteStarsLMHardware> getAllLMHardwareWithoutLoadGroups(
			List<LiteStarsEnergyCompany> energyCompanyList) {
		
		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId, lhc.*");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("JOIN LMHardwareConfiguration lhc ON lhc.InventoryId = ib.InventoryId");
		sql.append("WHERE etim.EnergyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");
		sql.append("AND lhc.InventoryId = ib.InventoryId");
		sql.append("AND lhc.AddressingGroupId = 0");
		
		List<LiteStarsLMHardware> liteHardwareList = simpleJdbcTemplate.query(
				sql.toString(), 
				new LiteStarsLMHardwareRowMapper());
		
		return liteHardwareList;
	}

	@Override
	public List<LiteInventoryBase> getAllMCTsWithAccount(
			List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("WHERE etim.EnergyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");
		sql.append("AND ib.AccountId > 0");
		sql.append("AND yle.YukonDefinitionId = ?");

		List<LiteInventoryBase> liteHardwareList = simpleJdbcTemplate.query(
				sql.toString(), 
				smartInventoryRowMapper,
				YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
		
		return liteHardwareList;
		
	}
	
	@Override
	public List<LiteInventoryBase> getAllMCTsWithoutAccount(
			List<LiteStarsEnergyCompany> energyCompanyList) {
		
		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("WHERE etim.EnergyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");
		sql.append("AND ib.AccountId = 0");
		sql.append("AND yle.YukonDefinitionId = ?");
		
		List<LiteInventoryBase> liteHardwareList = simpleJdbcTemplate.query(
				sql.toString(), 
				smartInventoryRowMapper,
				YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
		
		return liteHardwareList;
		
	}
    
}
