package com.cannontech.stars.dr.hardware.service;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;


public interface DeviceActivationService {
    
    public boolean isValidActivation(String accountNumber, String serialNumber);
    
    public boolean activate(LiteStarsEnergyCompany energyCompany, String accountNumber, String serialNumber);
    
}
