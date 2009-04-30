package com.cannontech.stars.core.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.event.dao.EventInventoryDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.LMConfigurationBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.util.ServerUtils;

public class StarsInventoryBaseDaoImpl implements StarsInventoryBaseDao, InitializingBean {
    private static final ParameterizedRowMapper<LiteInventoryBase> smartInventoryRowMapper = new SmartLiteInventoryBaseRowMapper();

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private SimpleTableAccessTemplate<LiteInventoryBase> liteInventoryTemplate;

    private static final String selectInventorySql;
    private static final String insertECToInventorySql;
    private static final String insertLmHardwareSql;
    private static final String updateLmHardwareSql;
    private static final String removeInventoryFromAccountSql;

    private NextValueHelper nextValueHelper;    
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private LMHardwareEventDao hardwareEventDao;
    private LMConfigurationBaseDao lmConfigurationBaseDao;
    private EventInventoryDao eventInventoryDao;
    private ECMappingDao ecMappingDao;

    static {
        selectInventorySql = "SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId " 
            + "FROM InventoryBase ib " 
            + "LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId " 
            + "LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId " 
            + "JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId " 
            + "JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId ";

        insertECToInventorySql = "INSERT INTO ECToInventoryMapping (EnergyCompanyID,InventoryID) VALUES (?,?)";

        insertLmHardwareSql = "INSERT INTO LMHardwareBase (ManufacturerSerialNumber,LMHardwareTypeID," 
            + "RouteID,ConfigurationID,InventoryID) VALUES (?,?,?,?,?)";

        updateLmHardwareSql = "UPDATE LMHardwareBase SET ManufacturerSerialNumber = ?, LMHardwareTypeID = ?, RouteID = ?, " 
            + "ConfigurationID = ? WHERE InventoryID = ?";

        removeInventoryFromAccountSql = "UPDATE InventoryBase SET AccountID = 0, DeviceLabel = '', RemoveDate = ?  WHERE InventoryID = ?";
    }

    @Override
    @Transactional(readOnly = true)
    public LiteInventoryBase getByInventoryId(final int inventoryId) throws NotFoundException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectInventorySql);
        sql.append("WHERE ib.InventoryId = ").appendArgument(inventoryId);
        LiteInventoryBase liteInv = null;
        try {
            liteInv = simpleJdbcTemplate.queryForObject(sql.getSql(),
                                                        smartInventoryRowMapper,
                                                        sql.getArguments());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("LiteInventoryBase not found by Inventory Id: " + inventoryId);
        }

        return liteInv;
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public LiteInventoryBase getByDeviceId(final int deviceId) throws NotFoundException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append(selectInventorySql);
    	sql.append("WHERE ib.DeviceId = ").appendArgument(deviceId);
    	
        List<LiteInventoryBase> liteInventoryList = simpleJdbcTemplate.query(sql.getSql(),
                smartInventoryRowMapper,
                sql.getArguments());
        
        if (liteInventoryList.size() == 0) {
            throw new NotFoundException("LiteInventoryBase not found by Device Id: " + deviceId);
        }
        
        return ServerUtils.returnFirstRow(liteInventoryList, "multiple device ids found for " + deviceId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteInventoryBase> getByIds(
            final Collection<Integer> inventoryIds) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append(selectInventorySql);
        sqlBuilder.append("WHERE ib.InventoryId IN (");
        sqlBuilder.append(inventoryIds);
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();

        List<LiteInventoryBase> list = simpleJdbcTemplate.query(sql,
                                                                smartInventoryRowMapper);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, LiteInventoryBase> getByIdsMap(
            Collection<Integer> inventoryIds) {
        List<LiteInventoryBase> list = getByIds(inventoryIds);

        Map<Integer, LiteInventoryBase> map = new HashMap<Integer, LiteInventoryBase>(list.size());

        for (final LiteInventoryBase value : list) {
            Integer key = value.getInventoryID();
            map.put(key, value);
        }

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiteInventoryBase> getAllByEnergyCompanyList(
            List<LiteStarsEnergyCompany> energyCompanyList) {

        List<Integer> ecIdList = new ArrayList<Integer>();
        for (LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            ecIdList.add(energyCompany.getEnergyCompanyID());
        }

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append(selectInventorySql);
        sqlBuilder.append(" WHERE etim.EnergyCompanyID IN (");
        sqlBuilder.appendList(ecIdList);
        sqlBuilder.append(")");

        String sql = sqlBuilder.toString();
        List<LiteInventoryBase> list = simpleJdbcTemplate.query(sql,
                                                                smartInventoryRowMapper);
        return list;
    }

    @Override
    public List<LiteStarsLMHardware> getAllLMHardware(
            List<LiteStarsEnergyCompany> energyCompanyList) {

        List<Integer> ecIdList = new ArrayList<Integer>();
        for (LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            ecIdList.add(energyCompany.getEnergyCompanyID());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId IN (");
        sql.append(ecIdList);
        sql.append(")");

        List<LiteStarsLMHardware> liteHardwareList = simpleJdbcTemplate.query(sql.toString(),
                                                                              new LiteStarsLMHardwareRowMapper());

        return liteHardwareList;
    }

    @Override
    public List<LiteStarsLMHardware> getAllLMHardwareWithoutLoadGroups(
            List<LiteStarsEnergyCompany> energyCompanyList) {

        List<Integer> ecIdList = new ArrayList<Integer>();
        for (LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            ecIdList.add(energyCompany.getEnergyCompanyID());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId, lhc.*");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("JOIN LMHardwareConfiguration lhc ON lhc.InventoryId = ib.InventoryId");
        sql.append("WHERE etim.EnergyCompanyId IN (");
        sql.append(ecIdList);
        sql.append(")");
        sql.append("AND lhc.AddressingGroupId = 0");

        List<LiteStarsLMHardware> liteHardwareList = simpleJdbcTemplate.query(sql.toString(),
                                                                              new LiteStarsLMHardwareRowMapper());

        return liteHardwareList;
    }

    @Override
    public List<LiteInventoryBase> getAllMCTsWithAccount(
            List<LiteStarsEnergyCompany> energyCompanyList) {

        List<Integer> ecIdList = new ArrayList<Integer>();
        for (LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            ecIdList.add(energyCompany.getEnergyCompanyID());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId IN (");
        sql.append(ecIdList);
        sql.append(")");
        sql.append("AND ib.AccountId > 0");
        sql.append("AND yle.YukonDefinitionId = ?");

        List<LiteInventoryBase> liteHardwareList = simpleJdbcTemplate.query(sql.toString(),
                                                                            smartInventoryRowMapper,
                                                                            YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);

        return liteHardwareList;

    }

    @Override
    public List<LiteInventoryBase> getAllMCTsWithoutAccount(
            List<LiteStarsEnergyCompany> energyCompanyList) {

        List<Integer> ecIdList = new ArrayList<Integer>();
        for (LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            ecIdList.add(energyCompany.getEnergyCompanyID());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId IN (");
        sql.append(ecIdList);
        sql.append(")");
        sql.append("AND ib.AccountId = 0");
        sql.append("AND yle.YukonDefinitionId = ?");

        List<LiteInventoryBase> liteHardwareList = simpleJdbcTemplate.query(sql.toString(),
                                                                            smartInventoryRowMapper,
                                                                            YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);

        return liteHardwareList;

    }

    @Override
    @Transactional
    public LiteInventoryBase saveInventoryBase(LiteInventoryBase liteInv,
            int energyCompanyId) {

        boolean insert = false;
        if (liteInv.getInventoryID() <= 0) {
            liteInv.setInventoryID(getNextInventoryID());
            insert = true;
        }

        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { energyCompanyId,
                liteInv.getInventoryID() };

        if (insert) {
            //Insert into InventoryBase            
            liteInventoryTemplate.insert(liteInv);
            simpleJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
        } else {
            //Update InventoryBase            
            liteInventoryTemplate.update(liteInv);
        }

        return liteInv;
    }

    @Override
    @Transactional
    public LiteStarsLMHardware saveLmHardware(LiteStarsLMHardware lmHw,
            int energyCompanyId) {

        boolean insert = false;
        if (lmHw.getInventoryID() <= 0) {
            lmHw.setInventoryID(getNextInventoryID());
            insert = true;
        }
        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { energyCompanyId,
                lmHw.getInventoryID() };

        // Insert into LMHardwareBase
        Object[] lmHwParams = new Object[] {
                lmHw.getManufacturerSerialNumber(), lmHw.getLmHardwareTypeID(),
                lmHw.getRouteID(), lmHw.getConfigurationID(),
                lmHw.getInventoryID() };

        if (insert) {
            //Insert into InventoryBase
            liteInventoryTemplate.insert(lmHw);
            simpleJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
            simpleJdbcTemplate.update(insertLmHardwareSql, lmHwParams);
        } else {
            //Update InventoryBase            
            liteInventoryTemplate.update(lmHw);
            simpleJdbcTemplate.update(updateLmHardwareSql, lmHwParams);
        }

        return lmHw;
    }

    @Override
    @Transactional
    public void removeInventoryFromAccount(LiteInventoryBase liteInv) {

        // Update InventoryBase
        Object[] removeInvParams = new Object[] {
                new Timestamp(liteInv.getRemoveDate()),
                liteInv.getInventoryID() };

        simpleJdbcTemplate.update(removeInventoryFromAccountSql,
                                  removeInvParams);

    }

    @Override
    @Transactional
    public void deleteInventoryBase(int inventoryId) {

        // retrieve the Inventory
        LiteInventoryBase liteInv = getByInventoryId(inventoryId);

        // delete the lmHardware info
        deleteLMHardwareInfo(liteInv);

        // now delete the Inventory base info
        eventInventoryDao.deleteInventoryEvents(inventoryId);
        ecMappingDao.deleteECToInventoryMapping(inventoryId);
        deleteInventoryToWarehouseMapping(inventoryId);
        internalDeleteInventoryBase(inventoryId);
    }

    private void deleteLMHardwareInfo(LiteInventoryBase liteInv) {
        int inventoryId = liteInv.getInventoryID();

        lmHardwareConfigurationDao.delete(inventoryId);
        thermostatScheduleDao.deleteScheduleForInventory(inventoryId);
        thermostatScheduleDao.deleteManualEvents(inventoryId);
        if (liteInv instanceof LiteStarsLMHardware) {
            LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;
            if (lmHw.getConfigurationID() > 0) {
                lmConfigurationBaseDao.delete(lmHw.getConfigurationID());
            }
        }
        deleteHardwareToMeterMapping(inventoryId);
        hardwareEventDao.deleteAllLMHardwareEvents(inventoryId);
        deleteLmHardware(inventoryId);
    }

    private void deleteHardwareToMeterMapping(int inventoryId) {
        String deleteSql = "DELETE FROM LMHardwareToMeterMapping WHERE LMHardwareInventoryId = ?";
        simpleJdbcTemplate.update(deleteSql, inventoryId);
    }

    private void deleteLmHardware(int inventoryId) {
        String deleteSql = "DELETE FROM LMHardwareBase WHERE InventoryID = ?";
        simpleJdbcTemplate.update(deleteSql, inventoryId);
    }

    private void deleteInventoryToWarehouseMapping(int inventoryId) {
        String deleteSql = "DELETE FROM InventoryToWarehouseMapping WHERE InventoryID = ?";
        simpleJdbcTemplate.update(deleteSql, inventoryId);
    }

    private void internalDeleteInventoryBase(int inventoryId) {
        String deleteSql = "DELETE FROM InventoryBase WHERE InventoryID = ?";
        simpleJdbcTemplate.update(deleteSql, inventoryId);
    }

    public final Integer getNextInventoryID() {
        return new Integer(nextValueHelper.getNextValue("InventoryBase"));
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setLmHardwareConfigurationDao(
            LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

    @Autowired
    public void setThermostatScheduleDao(ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Autowired
    public void setHardwareEventDao(LMHardwareEventDao hardwareEventDao) {
		this.hardwareEventDao = hardwareEventDao;
	}

    @Autowired
    public void setLmConfigurationBaseDao(
			LMConfigurationBaseDao lmConfigurationBaseDao) {
		this.lmConfigurationBaseDao = lmConfigurationBaseDao;
	}
    
    @Autowired
    public void setEventInventoryDao(EventInventoryDao eventInventoryDao) {
        this.eventInventoryDao = eventInventoryDao;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        liteInventoryTemplate = new SimpleTableAccessTemplate<LiteInventoryBase>(simpleJdbcTemplate, nextValueHelper);
        liteInventoryTemplate.withTableName("InventoryBase");
        liteInventoryTemplate.withPrimaryKeyField("InventoryID");
        liteInventoryTemplate.withFieldMapper(inventoryFieldMapper);
    }
    
    private FieldMapper<LiteInventoryBase> inventoryFieldMapper = new FieldMapper<LiteInventoryBase>() {

        @Override
        public void extractValues(MapSqlParameterSource p, LiteInventoryBase o) {
            p.addValue("AccountID", o.getAccountID());
            p.addValue("InstallationCompanyID", o.getInstallationCompanyID());
            p.addValue("CategoryID", o.getCategoryID());
            p.addValue("ReceiveDate", new Timestamp(o.getReceiveDate()));
            p.addValue("InstallDate", new Timestamp(o.getInstallDate()));
            p.addValue("RemoveDate", new Timestamp(o.getRemoveDate()));
            p.addValue("AlternateTrackingNumber", o.getAlternateTrackingNumber());
            p.addValue("VoltageID", o.getVoltageID());
            p.addValue("Notes", o.getNotes());
            p.addValue("DeviceID", o.getDeviceID());
            p.addValue("DeviceLabel", o.getDeviceLabel());
            p.addValue("CurrentStateID", o.getCurrentStateID());
        }

        @Override
        public Number getPrimaryKey(LiteInventoryBase object) {
            return object.getInventoryID();
        }

        @Override
        public void setPrimaryKey(LiteInventoryBase object, int value) {
            object.setInventoryID(value);
            
        }
    };    
    
}
