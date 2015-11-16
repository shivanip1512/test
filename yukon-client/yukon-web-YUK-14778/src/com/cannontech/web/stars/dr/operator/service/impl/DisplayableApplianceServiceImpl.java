package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.web.stars.dr.operator.model.DisplayableApplianceListEntry;
import com.cannontech.web.stars.dr.operator.service.DisplayableApplianceService;
import com.google.common.collect.Lists;

public class DisplayableApplianceServiceImpl implements DisplayableApplianceService {
    
    @Autowired private ApplianceDao applianceDao;
    @Autowired private ProgramDao programDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    
    @Override
    public List<DisplayableApplianceListEntry> getDisplayableApplianceListEntries(int accountId) {
        List<DisplayableApplianceListEntry> results = Lists.newArrayList();
        
        List<Appliance> appliances = applianceDao.getByAccountId(accountId);
        for (Appliance appliance : appliances) {
            LiteInventoryBase inventoryBase = inventoryBaseDao.getByInventoryId(appliance.getInventoryId());
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
 
}