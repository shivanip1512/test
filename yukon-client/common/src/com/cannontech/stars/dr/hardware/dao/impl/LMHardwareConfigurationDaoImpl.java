package com.cannontech.stars.dr.hardware.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public class LMHardwareConfigurationDaoImpl implements LMHardwareConfigurationDao {
    
    private static Logger log = YukonLogManager.getLogger(LMHardwareConfigurationDaoImpl.class);    
    private SimpleJdbcTemplate simpleJdbcTemplate; 
    private static final String insertLmHwConfigSql;
    private static final String updateLmHwConfigSql;

    StarsApplianceDao starsApplianceDao;
    StaticLoadGroupMappingDao staticLoadGroupMappingDao;
    
    static {
        insertLmHwConfigSql = "INSERT INTO LMHardwareConfiguration (AddressingGroupID,LoadNumber,InventoryID,ApplianceID) VALUES (?,?,?,?)";

        updateLmHwConfigSql = "UPDATE LMHardwareConfiguration SET AddressingGroupID = ?,LoadNumber=? WHERE InventoryID = ? and ApplianceID=?";
    }
    
    @Override    
    public LMHardwareConfiguration getStaticLoadGroupMapping(
            LiteStarsCustAccountInformation liteAcct, LiteStarsLMHardware lmHw,
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
    @Transactional
    public void add(LMHardwareConfiguration lmHwConfig) {
        // Insert into LMHardwareConfiguration
        simpleJdbcTemplate.update(insertLmHwConfigSql,
                                  lmHwConfig.getAddressingGroupId(),
                                  lmHwConfig.getLoadNumber(),
                                  lmHwConfig.getInventoryId(),
                                  lmHwConfig.getApplianceId());
    }

    @Override
    @Transactional
    public void update(LMHardwareConfiguration lmHwConfig) {
        // Update LMHardwareConfiguration
        simpleJdbcTemplate.update(updateLmHwConfigSql,
                                  lmHwConfig.getAddressingGroupId(),
                                  lmHwConfig.getLoadNumber(),
                                  lmHwConfig.getInventoryId(),
                                  lmHwConfig.getApplianceId());
    }
    
    @Override
    @Transactional    
    public void delete(LMHardwareConfiguration lmConfiguration) {
        delete(lmConfiguration.getInventoryId());
    }
    
    @Override
    @Transactional    
    public void delete(int invetoryId) {
        String sql = "DELETE FROM LMHardwareConfiguration WHERE InventoryID = ?";
        simpleJdbcTemplate.update(sql, invetoryId);
    }
    
    @Override
    @Transactional    
    public void deleteForAppliance(int applianceId) {
        String sql = "DELETE FROM LMHardwareConfiguration WHERE ApplianceId = ?";
        simpleJdbcTemplate.update(sql, applianceId);
    }
    
    @Override
    @Transactional
    public void deleteForAppliances(List<Integer> applianceIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareConfiguration WHERE ApplianceId IN (", applianceIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }

    @Override
    @Transactional
    public void delete(List<Integer> inventoryIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareConfiguration WHERE InventoryId IN (", inventoryIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired    
    public void setStarsApplianceDao(StarsApplianceDao starsApplianceDao) {
        this.starsApplianceDao = starsApplianceDao;
    }

    @Autowired    
    public void setStaticLoadGroupMappingDao(
            StaticLoadGroupMappingDao staticLoadGroupMappingDao) {
        this.staticLoadGroupMappingDao = staticLoadGroupMappingDao;
    }

}
