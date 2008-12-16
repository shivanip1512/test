package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EnergyCompanyDao {

//  the following is a duplicate of StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID
    public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
    
    /**
     * Returns the lite energy company with the given energy company id.
     * @param energyCompanyID
     * @return LiteEnergyCompany
     */
    public LiteEnergyCompany getEnergyCompany(int energyCompanyID);

    /**
     * Return the energy company this user belongs to.
     * @param user
     * @return LiteEnergyCompany
     */
    public LiteEnergyCompany getEnergyCompany(LiteYukonUser user);

    /**
     * @param energyCompanyName
     * @return LiteEnergyCompany
     * @throws NotFoundException
     */
    public LiteEnergyCompany getEnergyCompanyByName(String energyCompanyName);
    
    /**
     * Returns all the LiteEnergyCompany's that have customerID_ in it.
     * 
     * @param customerID_ int
     * @return LiteEnergyCompany
     */
    public LiteEnergyCompany[] getEnergyCompaniesByCustomer(int customerID_);

    /**
     * Returns the energy companies lite user 
     * @param company
     * @return LiteYukonUser
     */
    public LiteYukonUser getEnergyCompanyUser(LiteEnergyCompany company);

    /**
     * Returns the energy companies lite user
     * @param energyCompanyID
     * @return LiteYukonUser
     */
    public LiteYukonUser getEnergyCompanyUser(int energyCompanyID);

    /**
     * Returns value of the energy company property
     * @param user, rolePropertyID
     * @return String
     */
    public String getEnergyCompanyProperty(LiteYukonUser user,
            int rolePropertyID);

    /**
     * Similar to a function in LiteStarsEnergyCompany that looks up a 
     * property for an energy company. This function simply determines
     * the energy company's user and delegates to getEnergyCompanyProperty(LiteYukonUser, int).
     * @param ec
     * @param rolePropertyID
     * @return String
     */
    public String getEnergyCompanyProperty(LiteEnergyCompany ec,
            int rolePropertyID);

    /**
     * Method to add a customer, energy company pair to EnergyCompanyCustomerList
     * @param customerId
     * @param ecId
     */
    void addEnergyCompanyCustomerListEntry(int customerId, int ecId);
}