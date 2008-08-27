package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareMapper;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;

public class StarsSearchDaoImpl implements StarsSearchDao {
    private static final ParameterizedRowMapper<LiteInventoryBase> inventoryRowMapper =
        new SmartLiteInventoryBaseRowMapper();
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
	public LiteInventoryBase searchLMHardwareBySerialNumber(
			String serialNumber,
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, etim.EnergyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
		sql.append("WHERE lhb.InventoryId = ib.InventoryId");
		sql.append("AND etim.InventoryId = ib.InventoryId");
		sql.append("AND yle.EntryId = ib.CategoryId");
		sql.append("AND UPPER(lhb.ManufacturerSerialNumber) = UPPER(?)");

		List<LiteStarsLMHardware> liteHardwareList = jdbcTemplate.query(
				sql.toString(), 
				new LiteStarsLMHardwareMapper(),
				serialNumber);
		
		if(liteHardwareList.size() == 0) {
			return null;
		}
		
		LiteStarsLMHardware liteHardware = liteHardwareList.get(0);
		if(!energyCompany.getEnergyCompanyID().equals(liteHardware.getEnergyCompanyId())) {
			LiteStarsEnergyCompany inventoryEC = ecMappingDao.getInventoryEC(liteHardware.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( liteHardware, inventoryEC);
		}
		
		return liteHardware;
	}
	

	@Override
	public List<LiteInventoryBase> searchLMHardwareBySerialNumber(String serialNumber,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}

		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
		sql.append("WHERE lhb.InventoryId = ib.InventoryId");
		sql.append("AND etim.inventoryId = lhb.InventoryId");
		sql.append("AND yle.EntryId = ib.CategoryId");
		sql.append("AND UPPER(lhb.ManufacturerSerialNumber) = UPPER(?)");
		sql.append("AND etim.energyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");
		
		
		List<LiteInventoryBase> hardwareList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				serialNumber);
		
		return hardwareList;
	}

	@Override
	public LiteInventoryBase searchForDevice(int categoryID, String deviceName,
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, mhb.*, ypo.paoname, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
		sql.append("WHERE ib.DeviceID > 0");
		sql.append("AND ib.CategoryID = ?");
		sql.append("AND UPPER(ypo.paoname) LIKE UPPER(?)");
		
		List<LiteInventoryBase> inventoryBaseList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				categoryID,
				deviceName + "%");
		
		
		
		if(inventoryBaseList.size() == 0) {
			return null;
		}
		
		LiteInventoryBase inventoryBase = inventoryBaseList.get(0);
		if(energyCompany.getEnergyCompanyID() != inventoryBase.getEnergyCompanyId()) {
			LiteStarsEnergyCompany inventoryEC = ecMappingDao.getInventoryEC(inventoryBase.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( inventoryBase, inventoryEC);
		}
		
		return inventoryBase;
	}
	

	@Override
	public List<LiteInventoryBase> searchInventoryByDeviceName(String deviceName,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, mhb.*, ypo.paoname, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
		sql.append("WHERE ib.DeviceID > 0");
		sql.append("AND etim.energyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");
		sql.append("AND UPPER(ypo.paoname) LIKE UPPER(?)");
		
		List<LiteInventoryBase> inventoryList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				deviceName + "%");
		
		return inventoryList;
	}

	@Override
	public LiteInventoryBase getDevice(int deviceID, LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {

		LiteInventoryBase inventoryBase = starsInventoryBaseDao.getById(deviceID);
		
		if(inventoryBase == null) {
			return inventoryBase;
		}
		
		if(energyCompany.getEnergyCompanyID() != inventoryBase.getEnergyCompanyId()) {
			LiteStarsEnergyCompany inventoryEC = ecMappingDao.getInventoryEC(inventoryBase.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( inventoryBase, inventoryEC);
		}
		
		return inventoryBase;
	}

	@Override
	public List<LiteInventoryBase> searchInventoryByAltTrackNumber(
			String altTrackNumber,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyID());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("WHERE ib.InventoryId >= 0");
		sql.append("AND UPPER(ib.AlternateTrackingNumber) = UPPER(?)");
		sql.append("AND etim.energyCompanyId IN (" + StringUtils.join(ecIdList, ",") + ")");
		
		List<LiteInventoryBase> inventoryBaseList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				altTrackNumber);
		
		return inventoryBaseList;
	}

}
