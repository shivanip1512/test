package com.cannontech.web.admin.energyCompany.service;

import java.util.Set;

import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.checker.UserChecker;
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
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user, Integer parentId) 
    throws WebClientException, TransactionException, CommandExecutionException;
    
    public void deleteEnergyCompany(LiteYukonUser user, int energyCompanyId);

    /**
     * Takes an energy company id and returns
     * a set of possible member candidates.
     * @param meAndMyParentsIds
     * @return Set<LiteStarsEnergyCompany> possible member candidates
     */
    public Set<LiteStarsEnergyCompany> getMemberCandidates(int ecId);

    /**
     * Returns true is the user has the appropriate privaledges to edit an energy company.
     */
    public boolean canEditEnergyCompany(LiteYukonUser user, int ecId);

    /**
     * Returns true if the user is an operator of one of the energy companies parents.
     */
    public boolean isParentOperator(int userId, int ecId);

    /**
     * Returns true if the user has the appropriate privaledges to manage member energy companies.
     */
    public boolean canManageMembers(LiteYukonUser yukonUser);

    /** 
     * Returns true if the user has the appropriate privaledges to create member energy companies.
     */
    public boolean canCreateMembers(LiteYukonUser yukonUser);

    /** 
     * Returns true if the user has the appropriate privaledges to delete this energy company.
     */
    public boolean canDeleteEnergyCompany(LiteYukonUser yukonUser, int ecId);

    /**
     *  Verify the user can view this energy company page. 
     *  @throws NotAuthorizedException
     */
    public void verifyViewPageAccess(LiteYukonUser user, int ecId);
    
    /**
     *  Verify the user can edit this energy company page. 
     *  @throws NotAuthorizedException
     */
    public void verifyEditPageAccess(LiteYukonUser user, int ecId);

    /**
     * Returns true is the user is an operator of any energy company
     */
    public boolean isOperator(LiteYukonUser user);

    public UserChecker createEcOperatorChecker();
    
    /**
     * This method adds a route to the supplied energy company.
     */
    public void addRouteToEnergyCompany(int energyCompanyId, int routeId);
        
    /**
     * This method removes a route from the given energy company
     */
    public int removeRouteFromEnergyCompany(int energyCompanyId, int routeId);
 
    /**
     * This method adds a substation to the given energy company.
     */
    public void addSubstationToEnergyCompany(int energyCompanyId, int substationId);
    
    /**
     * This method removes a substation from an energy company.  It handles removing the mapping entry,
     * setting all the relevant siteInfo substations to 0, and also sends out a db change message.
     * @return returns 1 if a substation was deleted.
     */
    public int removeSubstationFromEnergyCompany(int energyCompanyId, int substationId);    
}