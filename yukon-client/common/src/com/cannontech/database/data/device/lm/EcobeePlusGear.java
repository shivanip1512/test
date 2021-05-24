package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeePlusGear extends WifiThermostatSetpointGear {
    private static final long serialVersionUID = 1L;
    
    public EcobeePlusGear() {
        setControlMethod(GearControlMethod.EcobeePlus);
    }
    
 
    @Override
    public boolean useCustomDbRetrieve() {
        return true;
    }
    
    
    
  
}
