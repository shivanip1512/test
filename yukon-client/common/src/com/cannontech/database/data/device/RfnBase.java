package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class RfnBase extends DeviceBase {
    
    private RfnAddress rfnAddress = null;
    
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
    public void delete() throws SQLException {
        getRfnAddress().delete();
        
        /** Clean up stars tables */
        PaoType paoType = PaoType.getForDbString(getPAOType());
        if (paoType.isRfn() && !paoType.isMeter()) {
            HardwareService hardwareService = YukonSpringHook.getBean("hardwareService", HardwareService.class);
            InventoryDao inventoryDao = YukonSpringHook.getBean("inventoryDao", InventoryDao.class);
            YukonEnergyCompanyService ecService = YukonSpringHook.getBean("yukonEnergyCompanyService", YukonEnergyCompanyService.class);
            
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
                YukonEnergyCompany ec = ecService.getEnergyCompanyByInventoryId(inventoryId);
                
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
    public void deletePartial() throws SQLException {
        super.deletePartial();
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getRfnAddress().retrieve();
    }
    
    @Override
    public void update() throws SQLException {
        super.update();
        
        /* Use the rfn device dao to do updating since depending on the address arguments
         * we will either be doing a delete, and insert or an update. */
        RfnDeviceDao rfnDeviceDao = YukonSpringHook.getBean("rfnDeviceDao", RfnDeviceDao.class);
        RfnDevice device = new RfnDevice(new PaoIdentifier(getPAObjectID(), PaoType.getForDbString(getPAOType())), 
                                      new RfnIdentifier(getRfnAddress().getSerialNumber(), getRfnAddress().getManufacturer(), getRfnAddress().getModel()));
        rfnDeviceDao.updateDevice(device);
    }
    
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