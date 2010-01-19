package com.cannontech.stars.dr.deviceReconfig.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonListEntryRowMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
import com.cannontech.stars.dr.deviceReconfig.dao.DeviceReconfigDao;
import com.cannontech.stars.dr.deviceReconfig.model.DeviceReconfigDeviceType;
import com.google.common.collect.Lists;

public class DeviceReconfigDaoImpl implements DeviceReconfigDao {
	
	private YukonJdbcTemplate yukonJdbcTemplate;

	public List<DeviceReconfigDeviceType> getDeviceTypes(int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT yle.* FROM YukonSelectionList ysl");
		sql.append("JOIN ectogenericmapping ecgm ON (ecgm.ItemId = ysl.ListId AND ecgm.MappingCategory = 'YukonSelectionList')");
		sql.append("JOIN YukonListEntry yle ON (yle.ListId = ysl.ListId)");
		sql.append("WHERE ListName = 'DeviceType'");
		sql.append("AND ecgm.EnergyCompanyId").eq(energyCompanyId);
		sql.append("ORDER BY yle.EntryText");
		
		List<YukonListEntry> yukonListEntries = yukonJdbcTemplate.query(sql, new YukonListEntryRowMapper());
		List<DeviceReconfigDeviceType> deviceTypes = Lists.newArrayListWithCapacity(yukonListEntries.size());
		for (YukonListEntry yukonListEntry : yukonListEntries) {
			
			DeviceReconfigDeviceType deviceReconfigDeviceType = new DeviceReconfigDeviceType(yukonListEntry.getEntryText(), yukonListEntry);
			deviceTypes.add(deviceReconfigDeviceType);
		}
		
		return deviceTypes;
	}
	
	@Override
	public List<LiteStarsLMHardware> getLmHardwareByLmGroupPaoIds(List<Integer> lmGroupPaoIds, int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("JOIN InventoryBase ib ON (lmhcg.InventoryId = ib.InventoryId)");
		sql.append("JOIN LMHardwareBase lhb ON (lhb.InventoryId = ib.InventoryId)");
		sql.append("JOIN ECToInventoryMapping etim ON (etim.inventoryId = lhb.InventoryId)");
		sql.append("JOIN YukonListEntry yle ON (yle.EntryId = ib.CategoryId)");
		sql.append("WHERE etim.EnergyCompanyId").eq(energyCompanyId);
		sql.append("AND lmhcg.LmGroupId IN (").appendList(lmGroupPaoIds).append(")");
		
		return yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
	}
	
	@Override
	public int getLmHardwareCountByLmGroupPaoIds(List<Integer> lmGroupPaoIds, int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT COUNT(DISTINCT lmhcg.InventoryId)");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("JOIN ECToInventoryMapping etim ON (etim.inventoryId = lmhcg.InventoryId)");
		sql.append("WHERE etim.EnergyCompanyId").eq(energyCompanyId);
		sql.append("AND lmhcg.LmGroupId IN (").appendList(lmGroupPaoIds).append(")");
		
		return yukonJdbcTemplate.queryForInt(sql);
	}
	
	@Override
	public List<Integer> getLmHardwareIdsByLmGroupPaoIds(List<Integer> lmGroupPaoIds, int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT lmhcg.InventoryId");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("JOIN ECToInventoryMapping etim ON (etim.inventoryId = lmhcg.InventoryId)");
		sql.append("WHERE etim.EnergyCompanyId").eq(energyCompanyId);
		sql.append("AND lmhcg.LmGroupId IN (").appendList(lmGroupPaoIds).append(")");
		
		return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
	}
	
	@Override
	public List<LiteStarsLMHardware> getLmHardwareByProgramPaoIds(List<Integer> programPaoIds, int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("JOIN LMProgramWebPublishing pwp ON (lmhcg.ProgramId = pwp.programId)");
		sql.append("JOIN InventoryBase ib ON (lmhcg.InventoryId = ib.InventoryId)");
		sql.append("JOIN LMHardwareBase lhb ON (lhb.InventoryId = ib.InventoryId)");
		sql.append("JOIN ECToInventoryMapping etim ON (etim.inventoryId = lhb.InventoryId)");
		sql.append("JOIN YukonListEntry yle ON (yle.EntryId = ib.CategoryId)");
		sql.append("WHERE etim.EnergyCompanyId").eq(energyCompanyId);
		sql.append("AND pwp.DeviceId IN (").appendList(programPaoIds).append(")");
		
		return yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
	}
	
	@Override
	public int getLmHardwareCountByProgramPaoIds(List<Integer> programPaoIds, int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT COUNT(DISTINCT lmhcg.InventoryId)");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("JOIN LMProgramWebPublishing pwp ON (lmhcg.ProgramId = pwp.programId)");
		sql.append("JOIN ECToInventoryMapping etim ON (etim.inventoryId = lmhcg.InventoryId)");
		sql.append("WHERE etim.EnergyCompanyId").eq(energyCompanyId);
		sql.append("AND pwp.DeviceId IN (").appendList(programPaoIds).append(")");
		
		return yukonJdbcTemplate.queryForInt(sql);
	}
	
	@Override
	public List<Integer> getLmHardwareIdsByProgramPaoIds(List<Integer> programPaoIds, int energyCompanyId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT lmhcg.InventoryId");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("JOIN LMProgramWebPublishing pwp ON (lmhcg.ProgramId = pwp.programId)");
		sql.append("JOIN ECToInventoryMapping etim ON (etim.inventoryId = lmhcg.InventoryId)");
		sql.append("WHERE etim.EnergyCompanyId").eq(energyCompanyId);
		sql.append("AND pwp.DeviceId IN (").appendList(programPaoIds).append(")");
		
		return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
	}
	
	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}
