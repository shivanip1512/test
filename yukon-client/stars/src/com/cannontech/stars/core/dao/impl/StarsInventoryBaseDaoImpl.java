package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.event.dao.EventInventoryDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.LMConfigurationBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.util.StarsUtils;

public class StarsInventoryBaseDaoImpl implements StarsInventoryBaseDao {
    private static final ParameterizedRowMapper<LiteInventoryBase> smartInventoryRowMapper = new SmartLiteInventoryBaseRowMapper();

    private SimpleJdbcTemplate simpleJdbcTemplate;

    private static final String selectInventorySql;
    private static final String insertInventorySql;
    private static final String updateInventorySql;
    private static final String insertECToInventorySql;
    private static final String insertLmHardwareSql;
    private static final String updateLmHardwareSql;
    private static final String removeInventoryFromAccountSql;

    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private LMHardwareEventDao hardwareEventDao;
    private LMConfigurationBaseDao lmConfigurationBaseDao;
    private EventInventoryDao eventInventoryDao;
    private ECMappingDao ecMappingDao;

    static {
        selectInventorySql = "SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId " + "FROM InventoryBase ib " + "LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId " + "LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId " + "JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId " + "JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId ";

        insertInventorySql = "INSERT INTO InventoryBase (AccountID,InstallationCompanyID,CategoryID,ReceiveDate," + "InstallDate,RemoveDate,AlternateTrackingNumber,VoltageID,Notes,DeviceID," + "DeviceLabel,CurrentStateID,InventoryID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        updateInventorySql = "UPDATE InventoryBase SET AccountID = ?, InstallationCompanyID = ?, CategoryID = ?, ReceiveDate = ?, " + "InstallDate = ?, RemoveDate = ?, AlternateTrackingNumber = ?, VoltageID = ?, Notes = ?, " + "DeviceID = ?, DeviceLabel = ?, CurrentStateID = ? WHERE InventoryID = ?";

        insertECToInventorySql = "INSERT INTO ECToInventoryMapping (EnergyCompanyID,InventoryID) VALUES (?,?)";

        insertLmHardwareSql = "INSERT INTO LMHardwareBase (ManufacturerSerialNumber,LMHardwareTypeID," + "RouteID,ConfigurationID,InventoryID) VALUES (?,?,?,?,?)";

        updateLmHardwareSql = "UPDATE LMHardwareBase SET ManufacturerSerialNumber = ?, LMHardwareTypeID = ?, RouteID = ?, " + "ConfigurationID = ? WHERE InventoryID = ?";

        removeInventoryFromAccountSql = "UPDATE InventoryBase SET AccountID = 0, DeviceLabel = '', RemoveDate = ?  WHERE InventoryID = ?";
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
        if (liteInventoryList.size() == 0) {
            return null;
        }

        LiteInventoryBase inventoryBase = liteInventoryList.get(0);

        return inventoryBase;
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

    /**
     * Saves a Inventory hardware device to the inventory and customer account.
     * Handles both insert/update records.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public LiteInventoryBase saveInventoryBase(LiteInventoryBase liteInv,
            int energyCompanyId) {

        boolean insert = false;
        if (liteInv.getInventoryID() <= 0) {
            liteInv.setInventoryID(getNextInventoryID());
            insert = true;
        }
        // Insert into InventoryBase
        Object[] invParams = new Object[] { liteInv.getAccountID(),
                liteInv.getInstallationCompanyID(), liteInv.getCategoryID(),
                StarsUtils.translateTstamp(liteInv.getReceiveDate()),
                StarsUtils.translateTstamp(liteInv.getInstallDate()),
                StarsUtils.translateTstamp(liteInv.getRemoveDate()),
                liteInv.getAlternateTrackingNumber(), liteInv.getVoltageID(),
                liteInv.getNotes(), liteInv.getDeviceID(),
                liteInv.getDeviceLabel(), liteInv.getCurrentStateID(),
                liteInv.getInventoryID() };

        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { energyCompanyId,
                liteInv.getInventoryID() };

        if (insert) {
            simpleJdbcTemplate.update(insertInventorySql, invParams);
            simpleJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
        } else {
            simpleJdbcTemplate.update(updateInventorySql, invParams);
        }

        return liteInv;
    }

    /**
     * Saves a LM hardware device to the inventory and customer account. Handles
     * both insert/update records.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public LiteStarsLMHardware saveLmHardware(LiteStarsLMHardware lmHw,
            int energyCompanyId) {

        boolean insert = false;
        if (lmHw.getInventoryID() <= 0) {
            lmHw.setInventoryID(getNextInventoryID());
            insert = true;
        }
        // Insert into InventoryBase
        Object[] invParams = new Object[] { lmHw.getAccountID(),
                lmHw.getInstallationCompanyID(), lmHw.getCategoryID(),
                StarsUtils.translateTstamp(lmHw.getReceiveDate()),
                StarsUtils.translateTstamp(lmHw.getInstallDate()),
                StarsUtils.translateTstamp(lmHw.getRemoveDate()),
                lmHw.getAlternateTrackingNumber(), lmHw.getVoltageID(),
                lmHw.getNotes(), lmHw.getDeviceID(), lmHw.getDeviceLabel(),
                lmHw.getCurrentStateID(), lmHw.getInventoryID() };

        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { energyCompanyId,
                lmHw.getInventoryID() };

        // Insert into LMHardwareBase
        Object[] lmHwParams = new Object[] {
                lmHw.getManufacturerSerialNumber(), lmHw.getLmHardwareTypeID(),
                lmHw.getRouteID(), lmHw.getConfigurationID(),
                lmHw.getInventoryID() };

        if (insert) {
            simpleJdbcTemplate.update(insertInventorySql, invParams);
            simpleJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
            simpleJdbcTemplate.update(insertLmHardwareSql, lmHwParams);
        } else {
            simpleJdbcTemplate.update(updateInventorySql, invParams);
            simpleJdbcTemplate.update(updateLmHardwareSql, lmHwParams);
        }

        return lmHw;
    }

    /**
     * Removes a hardware device from the account.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeInventoryFromAccount(LiteInventoryBase liteInv) {

        // Update InventoryBase
        Object[] removeInvParams = new Object[] {
                StarsUtils.translateTstamp(liteInv.getRemoveDate()),
                liteInv.getInventoryID() };

        simpleJdbcTemplate.update(removeInventoryFromAccountSql,
                                  removeInvParams);

    }

    /**
     * Deletes a hardware device from the inventory. Deletes only the LM
     * Hardware types for now.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteInventoryBase(int inventoryId) {

        // retrieve the Inventory
        LiteInventoryBase liteInv = getById(inventoryId);

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
            lmConfigurationBaseDao.delete(((LiteStarsLMHardware) liteInv).getConfigurationID());
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
        return new Integer(YukonSpringHook.getNextValueHelper()
                                          .getNextValue("InventoryBase"));
    }
    
    //Spring IOC
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    //Spring IOC
    public void setLmHardwareConfigurationDao(
            LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

    //Spring IOC    
    public void setThermostatScheduleDao(ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    //Spring IOC
    public void setHardwareEventDao(LMHardwareEventDao hardwareEventDao) {
		this.hardwareEventDao = hardwareEventDao;
	}

    //Spring IOC
    public void setLmConfigurationBaseDao(
			LMConfigurationBaseDao lmConfigurationBaseDao) {
		this.lmConfigurationBaseDao = lmConfigurationBaseDao;
	}
    
    //Spring IOC    
    public void setEventInventoryDao(EventInventoryDao eventInventoryDao) {
        this.eventInventoryDao = eventInventoryDao;
    }

    //Spring IOC    
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    
}
