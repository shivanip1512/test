package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public class LMHardwareConfigurationDaoImpl implements LMHardwareConfigurationDao {
    
    private static Logger log = YukonLogManager.getLogger(LMHardwareConfigurationDaoImpl.class);
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate; 
    @Autowired private StarsApplianceDao starsApplianceDao;
    @Autowired private StaticLoadGroupMappingDao staticLoadGroupMappingDao;
    
    @Override    
    public LMHardwareConfiguration getStaticLoadGroupMapping(
            LiteAccountInfo liteAcct, LiteLmHardwareBase lmHw,
            LiteStarsEnergyCompany energyCompany) {
        
        if (!VersionTools.staticLoadGroupMappingExists()) {
            return null;
        }
        
        LMHardwareConfiguration lmHwConfig = null;
        StarsStaticLoadGroupMapping criteria = new StarsStaticLoadGroupMapping();
        criteria.setSwitchTypeID(lmHw.getLmHardwareTypeID());
        log.debug("criteria, switchTypeID=[" + criteria.getSwitchTypeID() + "]");

        // get the recently added unassigned appliance on the account
        LiteStarsAppliance unassignedAppliance = null;
        if (liteAcct.getCustomerAccount() != null) { 
            // Must already have at least the base objects loaded
            List<LiteStarsAppliance> unassignedAppliances = starsApplianceDao.getUnassignedAppliances(liteAcct.getAccountID(),
                                                                                                      energyCompany.getEnergyCompanyId());
            if (unassignedAppliances.size() > 0) {
                unassignedAppliance = unassignedAppliances.get(0);
                criteria.setApplianceCategoryID(
                             unassignedAppliance.getApplianceCategory().getApplianceCategoryId());
                log.debug("criteria, applianceCategoryID=[" + criteria.getApplianceCategoryID() + "]");               
            }
        }
        if (unassignedAppliance == null) {
            return null;
        }
        // get zipCode
        String zip = energyCompany.getAddress(liteAcct.getAccountSite()
                                                      .getStreetAddressID())
                                  .getZipCode();
        if (!StringUtils.isBlank(zip)) {
            zip = StringUtils.substring(zip.trim(), 0, 5);
            criteria.setZipCode(zip);
            log.debug("criteria, zipCode=[" + criteria.getZipCode() + "]");     
        }

        // get ConsumptionType
        LiteCustomer cust = liteAcct.getCustomer();
        int consumptionType = -1;
        if (cust instanceof LiteCICustomer && cust.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
            consumptionType = ((LiteCICustomer) cust).getCICustType();
        }
        criteria.setConsumptionTypeID(consumptionType);
        log.debug("criteria, consumptionTypeID=[" + criteria.getConsumptionTypeID() + "]");        

        StarsStaticLoadGroupMapping loadGroup = staticLoadGroupMappingDao.getStaticLoadGroupMapping(criteria);

        if (loadGroup == null) {
            CTILogger.error("A static mapping could not be determined for serial number=[" + lmHw.getManufacturerSerialNumber() + "], applianceCategoryID=[" + unassignedAppliance.getApplianceCategory().getApplianceCategoryId() + "], ZipCode=[" + zip + "], ConsumptionTypeID=[" + consumptionType + "],SwitchTypeID=[" + lmHw.getLmHardwareTypeID() + "]");
        } else {
            lmHwConfig = new LMHardwareConfiguration();
            lmHwConfig.setInventoryId(lmHw.getInventoryID());
            lmHwConfig.setApplianceId(unassignedAppliance.getApplianceID());
            lmHwConfig.setAddressingGroupId(loadGroup.getLoadGroupID());
        }
        return lmHwConfig;
    }    
    
    @Override
    public void add(LMHardwareConfiguration config) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink insert = sql.insertInto("LMHardwareConfiguration");
        insert.addValue("InventoryId", config.getInventoryId());
        insert.addValue("ApplianceId", config.getApplianceId());
        insert.addValue("AddressingGroupId", config.getAddressingGroupId());
        insert.addValue("LoadNumber", config.getLoadNumber());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void update(LMHardwareConfiguration config) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink insert = sql.update("LMHardwareConfiguration");
        insert.addValue("InventoryId", config.getInventoryId());
        insert.addValue("ApplianceId", config.getApplianceId());
        insert.addValue("AddressingGroupId", config.getAddressingGroupId());
        insert.addValue("LoadNumber", config.getLoadNumber());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void delete(LMHardwareConfiguration lmConfiguration) {
        delete(lmConfiguration.getInventoryId());
    }
    
    @Override
    public void delete(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareConfiguration WHERE InventoryID").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteForAppliance(int applianceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareConfiguration WHERE ApplianceId").eq(applianceId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteForAppliances(Collection<Integer> applianceIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareConfiguration WHERE ApplianceId").in(applianceIds);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void delete(Collection<Integer> inventoryIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareConfiguration WHERE InventoryId").in(inventoryIds);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public LMHardwareConfiguration getForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryId, ApplianceId, AdderssingGroupId, LoadNumber");
        sql.append("FROM LMHardwareConfiguration");
        sql.append("WHERE InventoryID").eq(inventoryId);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<LMHardwareConfiguration>() {
                @Override
                public LMHardwareConfiguration mapRow(YukonResultSet rs) throws SQLException {
                    LMHardwareConfiguration config = new LMHardwareConfiguration();
                    config.setInventoryId(rs.getInt("inventoryId"));
                    config.setApplianceId(rs.getInt("ApplianceId"));
                    config.setAddressingGroupId(rs.getInt("AdderssingGroupId"));
                    config.setLoadNumber(rs.getInt("LoadNumber"));
                    return config;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No LMHardwareConfiguration found for inventory with id: " + inventoryId);
        }
    }

}