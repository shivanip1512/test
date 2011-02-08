package com.cannontech.web.admin.energyCompany.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDto;

public interface EnergyCompanyService {

    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user
            , boolean asMember, Integer parentId) throws Exception;
    
    public void deleteEnergyCompany(int energyCompanyId);
    
}