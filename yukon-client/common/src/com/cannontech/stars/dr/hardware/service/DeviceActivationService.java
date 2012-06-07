package com.cannontech.stars.dr.hardware.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;


public interface DeviceActivationService {
    
    public boolean isValidActivation(String accountNumber, String serialNumber, 
    		LiteYukonUser user);
    
    public boolean activate(LiteStarsEnergyCompany energyCompany, String accountNumber, 
    		String serialNumber, LiteYukonUser user);
    
}
