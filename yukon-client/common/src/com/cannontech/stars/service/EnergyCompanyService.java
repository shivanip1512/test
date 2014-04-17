package com.cannontech.stars.service;

import java.sql.SQLException;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.ConfigurationException;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.model.EnergyCompanyDto;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.checker.UserChecker;

public interface EnergyCompanyService {
    /**
     * Create an energy company, if parentId is not null, will add this energy company
     * as a member of the parent.
     * 
     * @throws ConfigurationException - Throws a configuration error if the primary operator user group
     *         conflicts with the new EC Admin role group.
     * @throws SQLException
     * @throws Exception
     */
    LiteStarsEnergyCompany createEnergyCompany(EnergyCompanyDto energyCompanyDto, LiteYukonUser user, Integer parentId) 
    throws WebClientException, TransactionException, CommandExecutionException, ConfigurationException, SQLException;
    
    void deleteEnergyCompany(LiteYukonUser user, int energyCompanyId);

    /**
     * Get a set of possible member candidates for the energy company represented by the given id.
     * @param meAndMyParentsIds
     * @return Set<LiteStarsEnergyCompany> possible member candidates
     */
    Set<LiteStarsEnergyCompany> getMemberCandidates(int ecId);

    /**
     * Determine if the user has the appropriate privileges to edit an energy company.
     */
    boolean canEditEnergyCompany(LiteYukonUser user, int ecId);

    /**
     * Determine if the user is an operator of one of the energy company's parents.
     */
    boolean isParentOperator(int userId, int ecId);

    /**
     * Determine if the user has the appropriate privileges to manage member energy companies.
     */
    boolean canManageMembers(LiteYukonUser yukonUser);

    /** 
     * Returns true if the user has the appropriate privileges to create member energy companies.
     */
    boolean canCreateMembers(LiteYukonUser yukonUser);

    /** 
     * Determine if the user has the appropriate privileges to delete this energy company.
     */
    boolean canDeleteEnergyCompany(LiteYukonUser yukonUser, int ecId);

    /**
     *  Verify the user can view this energy company page. 
     *  @throws NotAuthorizedException
     */
    void verifyViewPageAccess(LiteYukonUser user, int ecId);
    
    /**
     *  Verify the user can edit this energy company page. 
     *  @throws NotAuthorizedException
     */
    void verifyEditPageAccess(LiteYukonUser user, int ecId);

    /**
     * Determine if the user is an operator of any energy company
     */
    boolean isOperator(LiteYukonUser user);
    
    /**
     * Determine if the user has the Residential Consumer role
     */
    boolean isResidentialUser(LiteYukonUser user);
    
    /**
     * Add a route to the supplied energy company.
     */
    void addRouteToEnergyCompany(int energyCompanyId, int routeId);
        
    /**
     * Remove a route from the given energy company
     */
    int removeRouteFromEnergyCompany(int energyCompanyId, int routeId);
 
    /**
     * Add a substation to the given energy company.
     */
    void addSubstationToEnergyCompany(int energyCompanyId, int substationId);
    
    /**
     * Remove a substation from an energy company, including removing the mapping entry,
     * setting all the relevant siteInfo substations to 0 and sending out a DB change message.
     */
    void removeSubstationFromEnergyCompany(int energyCompanyId, int substationId);
    
    /**
     * Create an energy company operator or super user checker.
     */
    UserChecker createEcOperatorOrSuperUserChecker();

    /**
     * Create an energy company operator or super user checker.
     */
    UserChecker createCanEditEnergyCompany();
    
    TimeZone getDefaultTimeZone(int ecId);
}
