package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;

public interface LMHardwareConfigurationDao {
    
    public void add(LMHardwareConfiguration lmHardwareConfiguration);
    
    public void delete(LMHardwareConfiguration lmHardwareConfiguration);
    
    public void update(LMHardwareConfiguration lmHardwareConfiguration);

    public void delete(int invetoryId);

    public void delete(List<Integer> inventoryIds);

    public void deleteForAppliance(int applianceId);

    public void deleteForAppliances(List<Integer> applianceIds);
    
    public LMHardwareConfiguration getStaticLoadGroupMapping(
            LiteStarsCustAccountInformation liteAcct, LiteStarsLMHardware lmHw,
            LiteStarsEnergyCompany energyCompany);    

}
