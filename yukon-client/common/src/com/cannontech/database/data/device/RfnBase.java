package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public abstract class RfnBase extends DeviceBase {
    
    private RfnAddress rfnAddress = null;
    
    public RfnBase(PaoType paoType) {
        super(paoType);
    }
    
    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getRfnAddress().setDeviceID(deviceID);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        if(!getRfnAddress().isBlank()) {
            getRfnAddress().add();
        }
    }
    
    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        if(!getRfnAddress().isBlank()) {
            getRfnAddress().add();
        }
    }
    
    @Override
    public void deletePartial() throws SQLException {
        getRfnAddress().delete();
        super.deletePartial();
    }
    
    @Override
    public void delete() throws SQLException {
        getRfnAddress().delete();
        
        /** Clean up stars tables */
        PaoType paoType = getPaoType();
        if (paoType.isRfn() && !paoType.isMeter()) {
            HardwareService hardwareService = YukonSpringHook.getBean("hardwareService", HardwareService.class);
            InventoryDao inventoryDao = YukonSpringHook.getBean("inventoryDao", InventoryDao.class);
            EnergyCompanyDao ecDao = YukonSpringHook.getBean(EnergyCompanyDao.class);
            
            boolean skip = false;
            YukonInventory inventory = null;
            try {
                inventory = inventoryDao.getYukonInventoryForDeviceId(getPAObjectID());
            } catch (EmptyResultDataAccessException e) {
                /* When an rf device gets deleted from stars, the stars tables will have already been cleaned up. */
                skip = true;
            }
            
            if (!skip) {
                int inventoryId = inventory.getInventoryIdentifier().getInventoryId();
                YukonEnergyCompany ec = ecDao.getEnergyCompanyByInventoryId(inventoryId);
                
                try {
                    hardwareService.deleteHardware(ec.getEnergyCompanyUser(), true, inventoryId);
                } catch (Exception e) {
                    throw new SQLException("Could not delete stars tables for: " + this, e);
                }
            }
        }
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getRfnAddress().retrieve();
    }
    
    @Override
    public void update() throws SQLException {
        super.update();
        getRfnAddress().update();
    }
    
    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getRfnAddress().setDbConnection(conn);
    }

    public RfnAddress getRfnAddress() {
        if(rfnAddress == null) {
            rfnAddress = new RfnAddress();
        }
        return rfnAddress;
    }

    public void setRfnAddress(RfnAddress rfmAddress) {
        this.rfnAddress = rfmAddress;
    }
}