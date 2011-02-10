package com.cannontech.web.admin.energyCompany.service;

import java.util.Set;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDto;

public interface EnergyCompanyService {

    /**
     * Creates an energy company, if parentId is not null, will add this energy company
     * as a member of the parent. 
     * @param energyCompanyDto
     * @param user
     * @param parentId
     * @return
     * @throws Exception
     */
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user, 
                                                      Integer parentId) throws Exception;
    
    public void deleteEnergyCompany(int energyCompanyId);

    /**
     * Takes an energy company id and returns
     * a set of possible member candidates.
     * @param meAndMyParentsIds
     * @return Set<LiteStarsEnergyCompany> possible member candidates
     */
    public Set<LiteStarsEnergyCompany> getMemberCandidates(int ecId);

}