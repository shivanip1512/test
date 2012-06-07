package com.cannontech.stars.core.service.impl;

import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.StarsCacheService;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.event.model.LMCustomerEventBase;
import com.cannontech.stars.dr.hardware.model.InventoryBase;

public class StarsCacheServiceImpl implements StarsCacheService {
	
	private StarsInventoryBaseDao starsInventoryBaseDao;
	
	public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}

    public void updateInventoryCache(LiteStarsEnergyCompany energyCompany, InventoryBase inventoryBase, 
            CustomerAccount customerAccount, LMCustomerEventBase eventBase) {
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryBase.getInventoryId());
        updateInventoryModel(inventoryBase, liteInventoryBase);
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
