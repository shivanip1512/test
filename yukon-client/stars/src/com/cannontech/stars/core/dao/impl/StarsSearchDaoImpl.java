package com.cannontech.stars.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.LiteInventoryBaseMapper;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;

public class StarsSearchDaoImpl implements StarsSearchDao {

	private SimpleJdbcTemplate jdbcTemplate;
	private ECMappingDao ecMappingDao;
	private StarsInventoryBaseDao starsInventoryBaseDao;

	public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
	
	public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}

	@Override
	public LiteStarsLMHardware getLMHardwareBySerialNumber(
			String serialNumber,
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, etim.EnergyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
		sql.append("WHERE lhb.InventoryId = ib.InventoryId");
		sql.append("AND etim.InventoryId = ib.InventoryId");
		sql.append("AND yle.EntryId = ib.CategoryId");
		sql.append("AND lhb.ManufacturerSerialNumber = ?");

		LiteStarsLMHardware liteHardware = jdbcTemplate.queryForObject(
				sql.toString(), 
				new LiteStarsLMHardwareMapper(),
				serialNumber);
		
		if(!energyCompany.getEnergyCompanyID().equals(liteHardware.getEnergyCompanyId())) {
			LiteStarsEnergyCompany inventoryEC = ecMappingDao.getInventoryEC(liteHardware.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( liteHardware, inventoryEC);
		}
		
		return liteHardware;
	}
	

	@Override
	public List<LiteStarsLMHardware> getLMHardwareBySerialNumber(String serialNumber,
			List<LiteStarsEnergyCompany> energyCompany) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId");
		sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim");
		sql.append("WHERE lhb.InventoryId = ib.InventoryId");
		sql.append("AND etim.inventoryId = lhb.InventoryId");
		sql.append("AND lhb.ManufacturerSerialNumber = ?");
		sql.append("AND etim.energyCompanyId IN (?)");
		
		List<LiteStarsLMHardware> hardwareList = jdbcTemplate.query(
				sql.toString(), 
				new LiteStarsLMHardwareMapper(),
				serialNumber);
		
		return hardwareList;
	}

	@Override
	public LiteInventoryBase searchForDevice(int categoryID, String deviceName,
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, ypo.paoname, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib, LMHardwareBase lhb, YukonPAObject ypo, ECToInventoryMapping etim, YukonListEntry yle");
		sql.append("WHERE lhb.InventoryId = ib.InventoryId");
		sql.append("AND ypo.PaobjectId = ib.DeviceId");
		sql.append("AND etim.InventoryId = ib.InventoryId");
		sql.append("AND yle.EntryId = ib.CategoryId");
		sql.append("AND ib.DeviceID > 0");
		sql.append("AND ib.CategoryID = ?");
		sql.append("AND ypo.paoname LIKE ?");
		
		LiteInventoryBase inventoryBase = jdbcTemplate.queryForObject(
				sql.toString(), 
				new LiteInventoryBaseMapper(),
				categoryID,
				deviceName + "%");
		
		if(energyCompany.getEnergyCompanyID() != inventoryBase.getEnergyCompanyId()) {
			LiteStarsEnergyCompany inventoryEC = ecMappingDao.getInventoryEC(inventoryBase.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( inventoryBase, inventoryEC);
		}
		
		return inventoryBase;
	}

	@Override
	public LiteInventoryBase getDevice(int deviceID, LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {

		LiteInventoryBase inventoryBase = starsInventoryBaseDao.getById(deviceID);
		
		if(energyCompany.getEnergyCompanyID() != inventoryBase.getEnergyCompanyId()) {
			LiteStarsEnergyCompany inventoryEC = ecMappingDao.getInventoryEC(inventoryBase.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( inventoryBase, inventoryEC);
		}
		
		return inventoryBase;
	}

}
