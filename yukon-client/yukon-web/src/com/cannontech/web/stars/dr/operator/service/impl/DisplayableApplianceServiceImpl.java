package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.web.stars.dr.operator.model.DisplayableApplianceListEntry;
import com.cannontech.web.stars.dr.operator.service.DisplayableApplianceService;
import com.google.common.collect.Lists;

public class DisplayableApplianceServiceImpl implements DisplayableApplianceService {
    private ApplianceDao applianceDao;
    private InventoryBaseDao inventoryBaseDao;
    private ProgramDao programDao;
    
    @Override
    public List<DisplayableApplianceListEntry> getDisplayableApplianceListEntries(int accountId) {
        List<DisplayableApplianceListEntry> results = Lists.newArrayList();
        
        List<Appliance> appliances = applianceDao.getByAccountId(accountId);
        for (Appliance appliance : appliances) {
            InventoryBase inventoryBase = inventoryBaseDao.getById(appliance.getInventoryId());
            Program starsProgram = programDao.getByProgramId(appliance.getProgramId());
            
            DisplayableApplianceListEntry displayableApplianceListEntry = 
                new DisplayableApplianceListEntry(appliance.getApplianceId(), 
                                                  appliance.getApplianceCategory().getDisplayName(), 
                                                  inventoryBase.getDeviceLabel(),
                                                  starsProgram.getProgramName());

            results.add(displayableApplianceListEntry);
        }
        
        return results;
    }
 
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
}