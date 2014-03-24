package com.cannontech.stars.dr;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class YukonEnergyCompanyMockFactory {
    public static YukonEnergyCompany getYukonEC1() {
        return new YukonEnergyCompany() {
            
            @Override
            public String getName() {
                return "test energy company";
            }
            
            @Override
            public LiteYukonUser getEnergyCompanyUser() {
                return null;
            }
            
            @Override
            public int getEnergyCompanyId() {
                return 1;
            }
        };
    }
    
}

