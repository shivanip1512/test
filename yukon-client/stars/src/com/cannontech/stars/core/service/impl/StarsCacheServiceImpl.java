package com.cannontech.stars.core.service.impl;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.service.StarsCacheService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.event.model.LMCustomerEventBase;
import com.cannontech.stars.dr.hardware.model.InventoryBase;

public class StarsCacheServiceImpl implements StarsCacheService {

    public void updateInventoryCache(LiteStarsEnergyCompany energyCompany, InventoryBase inventoryBase, 
            CustomerAccount customerAccount, LMCustomerEventBase eventBase) {
        
        LiteInventoryBase liteInventoryBase = energyCompany.getInventory(inventoryBase.getInventoryId(), false);
        updateInventoryModel(inventoryBase, liteInventoryBase);
        
        if (customerAccount.getAccountId() != 0) {
            updateInventoryCustomerAccount(energyCompany, customerAccount);
        }
    }
    
    private void updateInventoryCustomerAccount(final LiteStarsEnergyCompany energyCompany, final CustomerAccount customerAccount) {
        final LiteStarsCustAccountInformation accountInformation = energyCompany.getCustAccountInformation(customerAccount.getAccountId(), false);
        if (accountInformation != null) energyCompany.deleteCustAccountInformation(accountInformation);
        energyCompany.deleteStarsCustAccountInformation(customerAccount.getAccountId());
    }
    
    private void updateInventoryModel(final InventoryBase inventoryBase, final LiteInventoryBase liteInventoryBase) {
        liteInventoryBase.setAccountID(inventoryBase.getAccountId());
        liteInventoryBase.setAlternateTrackingNumber(inventoryBase.getAlternateTrackingNumber());
        liteInventoryBase.setCategoryID(inventoryBase.getCategoryId());
        liteInventoryBase.setCurrentStateID(inventoryBase.getCurrentStateId());
        liteInventoryBase.setDeviceID(inventoryBase.getDeviceId());
        liteInventoryBase.setDeviceLabel(inventoryBase.getDeviceLabel());
        liteInventoryBase.setInstallationCompanyID(inventoryBase.getInstallationCompanyId());
        liteInventoryBase.setInstallDate(inventoryBase.getInstallDate().getTime());
        liteInventoryBase.setInventoryID(inventoryBase.getInventoryId());
        liteInventoryBase.setNotes(inventoryBase.getNotes());
        liteInventoryBase.setReceiveDate(inventoryBase.getReceiveDate().getTime());
        liteInventoryBase.setRemoveDate(inventoryBase.getRemoveDate().getTime());
        liteInventoryBase.setVoltageID(inventoryBase.getVoltageId());
    }

}
