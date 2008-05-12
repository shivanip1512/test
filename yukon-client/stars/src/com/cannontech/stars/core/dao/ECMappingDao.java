package com.cannontech.stars.core.dao;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;

/**
 * Data access interface used to get energy company
 */
public interface ECMappingDao {

    /**
     * Method to get an energy company by account
     * @param account - Account to get energy company for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getCustomerAccountEC(CustomerAccount account);

    /**
     * Method to get an energy company by accountId
     * @param accountId - Id of account to get energy company for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getCustomerAccountEC(int accountId);

    /**
     * Method to get the energy company for a given inventory id
     * @param inventoryId - Id of inventory to get ec for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getInventoryEC(int inventoryId);

    /**
     * Method to get the energy company for a given contact id
     * @param contactId - Id of contact to get ec for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getContactEC(int contactId);
    
    

}