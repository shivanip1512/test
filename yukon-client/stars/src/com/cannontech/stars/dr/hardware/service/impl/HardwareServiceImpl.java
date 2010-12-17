package com.cannontech.stars.dr.hardware.service.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.user.YukonUserContext;

public class HardwareServiceImpl implements HardwareService {

    private ApplianceDao applianceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private EnrollmentHelperService enrollmentHelperService;
    private CustomerAccountDao customerAccountDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private HardwareEventLogService hardwareEventLogService;
    private LMHardwareEventDao lmHardwareEventDao;
    private InventoryBaseDao inventoryBaseDao;

    
    @Override
    @Transactional
    public void deleteHardware(YukonUserContext userContext, boolean delete, int inventoryId, 
                               int accountId, LiteStarsEnergyCompany energyCompany) throws Exception {
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        boolean deleteMCT = false;
        
        /* Unenroll the hardware */
        LMHardwareBase lmHardwareBase = null;
        try {
            lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
            enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());
            enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
            
            enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, energyCompany.getUser());
            
        } catch (NotFoundException ignore) {
            /* Ignore this if we are not an LMHardwareBase such as mct's */
            deleteMCT = true;
        }
        
        if (delete) {
            /* Delete this hardware from the database */
            /*TODO handle this with new code, not with this util. */
            InventoryManagerUtil.deleteInventory( liteInventoryBase, energyCompany, deleteMCT);

            // Log hardware deletion
            hardwareEventLogService.hardwareDeleted(userContext.getYukonUser(), liteInventoryBase.getDeviceLabel());

        } else {
            /* Just remove it from the account and put it back in general inventory */
            Date removeDate = new Date();
            int hwEventEntryId = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
            int uninstallActionId = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
            
            /* Add an uninstall event for this hardware */
            LiteLMHardwareEvent liteLMHardwareEvent = new LiteLMHardwareEvent();
            liteLMHardwareEvent.setInventoryID(inventoryId);
            liteLMHardwareEvent.setActionID(uninstallActionId);
            liteLMHardwareEvent.setEventTypeID(hwEventEntryId);
            liteLMHardwareEvent.setEventDateTime(removeDate.getTime());
            
            liteLMHardwareEvent.setNotes( "Removed from account #" + customerAccount.getAccountNumber() );
            
            lmHardwareEventDao.add(liteLMHardwareEvent, energyCompany.getEnergyCompanyID());
            
            if (liteInventoryBase instanceof LiteStarsLMHardware) {
                applianceDao.deleteAppliancesByAccountIdAndInventoryId(accountId, inventoryId);
            }
            
            /* Removes any entries found in the InventoryBase */
            InventoryBase inventoryBase =  inventoryBaseDao.getById(inventoryId);
            inventoryBase.setAccountId(CtiUtilities.NONE_ZERO_ID);
            inventoryBase.setRemoveDate(new Timestamp(removeDate.getTime()));
            inventoryBase.setDeviceLabel("");
            inventoryBaseDao.update(inventoryBase);
            
            // Log hardware deletion
            hardwareEventLogService.hardwareRemoved(userContext.getYukonUser(), liteInventoryBase.getDeviceLabel(), customerAccount.getAccountNumber());
        }
    }
    
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
		this.applianceDao = applianceDao;
	}
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setEnrollmentHelperService(
			EnrollmentHelperService enrollmentHelperService) {
		this.enrollmentHelperService = enrollmentHelperService;
	}
    
    @Autowired
    public void setHardwareEventLogService(
			HardwareEventLogService hardwareEventLogService) {
		this.hardwareEventLogService = hardwareEventLogService;
	}
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
		this.inventoryBaseDao = inventoryBaseDao;
	}
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
		this.lmHardwareBaseDao = lmHardwareBaseDao;
	}
    
    @Autowired
    public void setLmHardwareEventDao(LMHardwareEventDao lmHardwareEventDao) {
		this.lmHardwareEventDao = lmHardwareEventDao;
	}
    
    @Autowired    
    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
}
