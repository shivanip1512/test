package com.cannontech.stars.core.dao;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;

/**
 * Data access interface used to get energy company
 */
public interface ECMappingDao {

    public LiteStarsEnergyCompany getCustomerAccountEC(CustomerAccount account);

    /**
     * Method to get the energy company for a given inventory id
     * @param inventoryId - Id of inventory to get ec for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getInventoryEC(int inventoryId);

}