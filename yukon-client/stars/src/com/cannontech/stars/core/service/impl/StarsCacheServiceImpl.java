package com.cannontech.stars.core.service.impl;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
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
        
        updateInventoryHistoryCache(energyCompany, liteInventoryBase, eventBase);
        
        if (customerAccount.getAccountId() != 0) {
            updateInventoryCustomerAccount(energyCompany, customerAccount);
        }
    }
    
    private void updateInventoryCustomerAccount(final LiteStarsEnergyCompany energyCompany, final CustomerAccount customerAccount) {
        final LiteStarsCustAccountInformation accountInformation = energyCompany.getCustAccountInformation(customerAccount.getAccountId(), false);
        energyCompany.deleteCustAccountInformation(accountInformation);
        energyCompany.deleteStarsCustAccountInformation(customerAccount.getAccountId());
    }

    @SuppressWarnings("unchecked")
    private void updateInventoryHistoryCache(final LiteStarsEnergyCompany energyCompany, final LiteInventoryBase liteInventoryBase, final LMCustomerEventBase eventBase) {
        LiteLMCustomerEvent event = createLiteLMHardwareEvent(eventBase); 
        liteInventoryBase.getInventoryHistory().add(event);
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
    
    private LiteLMCustomerEvent createLiteLMHardwareEvent(final LMCustomerEventBase eventBase) {
        final LiteLMCustomerEvent liteEvent = new LiteLMCustomerEvent();
        liteEvent.setActionID(eventBase.getActionId());
        liteEvent.setEventDateTime(eventBase.getEventDateTime().getTime());
        liteEvent.setEventID(eventBase.getEventId());
        liteEvent.setEventTypeID(eventBase.getEventTypeId());
        liteEvent.setNotes(eventBase.getNotes());
        return liteEvent;
    }

}
