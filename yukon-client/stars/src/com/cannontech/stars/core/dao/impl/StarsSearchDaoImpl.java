package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.web.bean.InventoryBean;

public class StarsSearchDaoImpl implements StarsSearchDao {
    private static final ParameterizedRowMapper<LiteInventoryBase> inventoryRowMapper =
        new SmartLiteInventoryBaseRowMapper();
	private SimpleJdbcTemplate jdbcTemplate;
	private EnergyCompanyService energyCompanyService;
	private StarsInventoryBaseDao starsInventoryBaseDao;

	@Override
    public LiteInventoryBase searchLMHardwareBySerialNumber(String serialNumber, LiteStarsEnergyCompany energyCompany) throws ObjectInOtherEnergyCompanyException {
	    return searchLMHardwareBySerialNumber(serialNumber, energyCompany.getEnergyCompanyId());
	}

	@Override
	public LiteInventoryBase searchLMHardwareBySerialNumber(String serialNumber, int energyCompanyId) throws ObjectInOtherEnergyCompanyException {
		
		SqlStatementBuilder idSql = new SqlStatementBuilder();
		idSql.append("SELECT inventoryId");
		idSql.append("FROM LMHardwareBase");
		idSql.append("WHERE UPPER(ManufacturerSerialNumber) = UPPER(?)");
		
		List<Integer> hardwareIds = 
			jdbcTemplate.query(idSql.toString(), new IntegerRowMapper(), serialNumber);
		
		List<LiteStarsLMHardware> liteHardwareList = new ArrayList<LiteStarsLMHardware>();
		if(hardwareIds.size() > 0) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
			sql.append("SELECT ib.*, lhb.*, etim.EnergyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
			sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
			sql.append("WHERE lhb.InventoryId = ib.InventoryId");
			sql.append("AND etim.InventoryId = ib.InventoryId");
			sql.append("AND yle.EntryId = ib.CategoryId");
			sql.append("AND ib.InventoryId IN (").appendArgumentList(hardwareIds).append(")");
	
			liteHardwareList = jdbcTemplate.query(
					sql.getSql(), 
					new LiteStarsLMHardwareRowMapper(),
					sql.getArguments());
		}
		
		if(liteHardwareList.size() == 0) {
			return null;
		}
		
		LiteStarsLMHardware lmHardwareInOtherEnergyCompany = null;
		for (LiteStarsLMHardware liteStarsLMHardware : liteHardwareList) {
			if(energyCompanyId == liteStarsLMHardware.getEnergyCompanyId()) {
				//found _a_ match on same energy company, let's use that one.
				return liteStarsLMHardware;
			} else {	//found a match on different energy company
				lmHardwareInOtherEnergyCompany = liteStarsLMHardware;
			}
		}
		
		// If we get to here, we didn't find the serial number for the energyCompany provided.  Throw exception.
		YukonEnergyCompany otherEnergyCompany = energyCompanyService.getEnergyCompanyByInventoryId(lmHardwareInOtherEnergyCompany.getInventoryID());
		throw new ObjectInOtherEnergyCompanyException( lmHardwareInOtherEnergyCompany, otherEnergyCompany);
	}

	@Override
	public List<LiteInventoryBase> searchLMHardwareBySerialNumber(String serialNumber,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyId());
		}

		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
		sql.append("WHERE lhb.InventoryId = ib.InventoryId");
		sql.append("AND etim.inventoryId = lhb.InventoryId");
		sql.append("AND yle.EntryId = ib.CategoryId");
		sql.append("AND UPPER(lhb.ManufacturerSerialNumber) = UPPER(?)");
		sql.append("AND etim.energyCompanyId IN (");
		sql.append(ecIdList);
		sql.append(")");
		
		
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
		if(!inventoryBase.getEnergyCompanyId().equals(energyCompany.getEnergyCompanyId())) {
			YukonEnergyCompany yukonEnergyCompany = energyCompanyService.getEnergyCompanyByInventoryId(inventoryBase.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( inventoryBase, yukonEnergyCompany);
		}
		
		return inventoryBase;
	}
	

	@Override
	public List<LiteInventoryBase> searchInventoryByDeviceName(String deviceName,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyId());
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
		sql.append("AND etim.energyCompanyId IN (");
		sql.append(ecIdList);
		sql.append(")");
		sql.append("AND UPPER(ypo.paoname) LIKE UPPER(?)");
		
		List<LiteInventoryBase> inventoryList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				deviceName + "%");
		
		return inventoryList;
	}

	@Override
	public LiteInventoryBase getByDeviceId(int deviceId, LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {

		//Throws NotFoundException
		LiteInventoryBase inventoryBase = starsInventoryBaseDao.getByDeviceId(deviceId);
		
		return verifyInventoryInEnergyCompany(inventoryBase,energyCompany);
	}

	@Override
	public LiteInventoryBase getById(int inventoryId, LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException {

		LiteInventoryBase inventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
		return verifyInventoryInEnergyCompany(inventoryBase,energyCompany);
	}
	
	private LiteInventoryBase verifyInventoryInEnergyCompany(LiteInventoryBase invBase, LiteStarsEnergyCompany energyCompany) throws ObjectInOtherEnergyCompanyException {

		if(!invBase.getEnergyCompanyId().equals(energyCompany.getEnergyCompanyId())) {
			YukonEnergyCompany yukonEnergyCompany = energyCompanyService.getEnergyCompanyByInventoryId(invBase.getInventoryID());
			throw new ObjectInOtherEnergyCompanyException( invBase, yukonEnergyCompany);
		}
		
		return invBase;
	}
	
	@Override
	public List<LiteInventoryBase> searchInventoryByAltTrackNumber(
			String altTrackNumber,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyId());
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
		sql.append("AND etim.energyCompanyId IN (");
		sql.append(ecIdList);
		sql.append(")");
		
		List<LiteInventoryBase> inventoryBaseList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				altTrackNumber);
		
		return inventoryBaseList;
	}

	@Override
	public List<LiteInventoryBase> searchInventoryByInstallationCompany(
			int installationCompanyId,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyId());
		}
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("WHERE ib.InventoryId >= 0");
		sql.append("AND ib.InstallationCompanyId = ?");
		sql.append("AND etim.energyCompanyId IN (");
		sql.append(ecIdList);
		sql.append(")");
		
		List<LiteInventoryBase> inventoryBaseList = jdbcTemplate.query(
				sql.toString(), 
				inventoryRowMapper,
				installationCompanyId);
		
		return inventoryBaseList;
	}

	@Override
	public List<LiteStarsLMHardware> searchLMHardwareByRoute(int routeId,
			List<LiteStarsEnergyCompany> energyCompanyList) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyId());
		}

		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.inventoryId = lhb.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("AND lhb.RouteId = ?");
		sql.append("AND etim.EnergyCompanyId IN (");
		sql.append(ecIdList);
		sql.append(")");
		
		
		List<LiteStarsLMHardware> hardwareList = jdbcTemplate.query(
				sql.toString(), 
				new LiteStarsLMHardwareRowMapper(),
				routeId);
		
		return hardwareList;
	}

	@Override
	public List<LiteStarsLMHardware> searchLMHardwareBySerialNumberRange(
			long startSerialNumber, long endSerialNumber,
			int deviceTypeDefinitionId,
			List<LiteStarsEnergyCompany> energyCompanyList) throws PersistenceException {


		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		List<Integer> ecIdList = new ArrayList<Integer>();
		for(LiteStarsEnergyCompany energyCompany : energyCompanyList) {
			ecIdList.add(energyCompany.getEnergyCompanyId());
		}

		sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
		sql.append("JOIN ECToInventoryMapping etim ON etim.inventoryId = lhb.InventoryId");
		sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
		sql.append("JOIN YukonListEntry yle2 ON yle2.EntryId = lhb.LMHardwareTypeId");
		sql.append("AND yle2.YukonDefinitionId = ?");
		sql.append("AND CAST(lhb.ManufacturerSerialNumber AS NUMERIC) >= ?");
		sql.append("AND CAST(lhb.ManufacturerSerialNumber AS NUMERIC) <= ?");
		sql.append("AND etim.EnergyCompanyId IN (");
		sql.append(ecIdList);
		sql.append(")");
		
		List<LiteStarsLMHardware> hardwareList = null;
        try {
            hardwareList = jdbcTemplate.query(
                                      sql.toString(), 
                                      new LiteStarsLMHardwareRowMapper(),
                                      deviceTypeDefinitionId,
                                      startSerialNumber,
                                      endSerialNumber);
        // Oracle seems to throw SQLSyntaxErrorException whereas MS-SQL throws SQLException, 
        // that is the root of discrepancy in the Spring SQL error exception translator to throw
        // BadSqlGrammarException vs. DataIntegrityViolationException
        } catch (BadSqlGrammarException e){
            throw new PersistenceException(InventoryBean.INVENTORY_SQL_ERROR_FUNCTION, e);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException(InventoryBean.INVENTORY_SQL_ERROR_FUNCTION, e);
        }		
		
		return hardwareList;
		
	}

	// DI Setters
	@Autowired
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
	@Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
	
    @Autowired   
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

}