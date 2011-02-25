package com.cannontech.stars.dr.hardware.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
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
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class HardwareServiceImpl implements HardwareService {

    private ApplianceDao applianceDao;
    private CustomerAccountDao customerAccountDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    private EnrollmentHelperService enrollmentHelperService;
    private HardwareEventLogService hardwareEventLogService;
    private InventoryBaseDao inventoryBaseDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private LMHardwareEventDao lmHardwareEventDao;
    private StarsDatabaseCache starsDatabaseCache;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private OptOutService optOutService;
    private OptOutEventDao optOutEventDao;
    
    @Override
    @Transactional
    public void deleteHardware(YukonUserContext userContext, boolean delete, int inventoryId, int accountId) 
    throws Exception {
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByAccountId(accountId);
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        boolean deleteMCT = false;
        
        //Cancel all optouts for that hardware
        
        List<OptOutEventDto> optOutEvents = optOutEventDao.getCurrentOptOuts(accountId, inventoryId);
        
        List<Integer> optOutEventIdList = Lists.newArrayListWithCapacity(optOutEvents.size());
        
        for(OptOutEventDto event : optOutEvents){
            optOutEventIdList.add(event.getEventId());
        }
        
        optOutService.cancelOptOut(optOutEventIdList, userContext.getYukonUser());
        
        /* Unenroll the hardware */
        LMHardwareBase lmHardwareBase = null;
        try {
            lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
            enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());
            enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
            
            enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, userContext.getYukonUser());
            
        } catch (NotFoundException ignore) {
            /* Ignore this if we are not an LMHardwareBase such as mct's */
            deleteMCT = true;
        }
        
        if (delete) {
            /* Delete this hardware from the database */
            /*TODO handle this with new code, not with this util. */
            InventoryManagerUtil.deleteInventory( liteInventoryBase, yukonEnergyCompany, deleteMCT);

            // Log hardware deletion
            hardwareEventLogService.hardwareDeleted(userContext.getYukonUser(), liteInventoryBase.getDeviceLabel());

        } else {
            /* Just remove it from the account and put it back in general inventory */
            Date removeDate = new Date();
            LiteStarsEnergyCompany liteStarsEnergyCompany = 
                starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
            int hwEventEntryId = liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
            int uninstallActionId = liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
            
            /* Add an uninstall event for this hardware */
            LiteLMHardwareEvent liteLMHardwareEvent = new LiteLMHardwareEvent();
            liteLMHardwareEvent.setInventoryID(inventoryId);
            liteLMHardwareEvent.setActionID(uninstallActionId);
            liteLMHardwareEvent.setEventTypeID(hwEventEntryId);
            liteLMHardwareEvent.setEventDateTime(removeDate.getTime());
            
            liteLMHardwareEvent.setNotes( "Removed from account #" + customerAccount.getAccountNumber() );
            
            lmHardwareEventDao.add(liteLMHardwareEvent, yukonEnergyCompany.getEnergyCompanyId());
            
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
    
    // DI Setters
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
		this.applianceDao = applianceDao;
	}
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
    @Autowired
    public void setEnrollmentHelperService(EnrollmentHelperService enrollmentHelperService) {
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
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired    
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }
}
