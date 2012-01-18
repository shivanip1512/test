package com.cannontech.stars.ws.helperImpl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceNotFoundOnAccountException;
import com.cannontech.stars.dr.hardware.exception.StarsInvalidDeviceTypeException;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.helper.StarsControllableDeviceHelper;

public class StarsControllableDeviceHelperImpl implements
        StarsControllableDeviceHelper {

    private StarsSearchDao starsSearchDao;
    private CustomerAccountDao customerAccountDao;    
    private StarsInventoryBaseService starsInventoryBaseService;
    private StarsDatabaseCache starsDatabaseCache;

    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    @Autowired
    public void setStarsInventoryBaseService(
            StarsInventoryBaseService starsInventoryBaseService) {
        this.starsInventoryBaseService = starsInventoryBaseService;
    }
    
    @Autowired    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    private String getAccountNumber(StarsControllableDeviceDTO deviceInfo) {
        String acctNum = deviceInfo.getAccountNumber();
        if (StringUtils.isBlank(acctNum)) {
            throw new StarsInvalidArgumentException("Account Number is required");
        }
        return acctNum;
    }

    private String getSerialNumber(StarsControllableDeviceDTO deviceInfo) {
        String serialNum = deviceInfo.getSerialNumber();
        if (StringUtils.isBlank(serialNum)) {
            throw new StarsInvalidArgumentException("Serial Number is required");
        } 
        // Serial number validation that it should be numeric, upto 18-digits removed for now.
//        else if (!InventoryUtils.isValidHardwareSerialNumber(serialNum)) {
//            throw new StarsInvalidArgumentException("Invalid Serial Number [" + serialNum + "], should be numeric upto 18-digits");        
//        }
        return serialNum;
    }

    private String getDeviceType(StarsControllableDeviceDTO deviceInfo) {
        String deviceType = deviceInfo.getDeviceType();
        if (StringUtils.isBlank(deviceType)) {
            throw new StarsInvalidArgumentException("Device type is required");
        }
        return deviceType;
    }

    private CustomerAccount getCustomerAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        CustomerAccount custAcct = null;
        try {
            custAcct = customerAccountDao.getByAccountNumber(getAccountNumber(deviceInfo),
                                                             energyCompany.getLiteID());
        } catch (NotFoundException e) {
            // convert to a better, Account not found exception
            throw new StarsAccountNotFoundException(getAccountNumber(deviceInfo),
                                                    energyCompany.getName(),
                                                    e);
        }
        return custAcct;
    }

    /**
     * Returns the YukonListEntry id value for deviceInfo.deviceType
     * Performs initial validation on deviceType as defined by getDeviceType(deviceInfo).
     * @param deviceInfo
     * @param energyCompany
     * @return deviceTypeId - yukonListEntry id value.
     * @throws StarsInvalidDeviceTypeException if yukonListEntry is not found for deviceInfo.deviceType 
     */
    private int getDeviceTypeId(StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
    	String deviceType = getDeviceType(deviceInfo);
    	try {
    		int deviceTypeId = YukonListEntryHelper.getEntryIdForEntryText(deviceType, 
    				YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
    				energyCompany);
    		return deviceTypeId;	
    	} catch (NotFoundException e) {
            throw new StarsInvalidDeviceTypeException(deviceType,
                                                      energyCompany.getName());
        }
    }

    @Override
    public LiteInventoryBase addDeviceToAccount(StarsControllableDeviceDTO deviceInfo,
             LiteYukonUser user) {

        LiteInventoryBase liteInv = null;
        
        //Get energyCompany for the user
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        
        // Get Inventory, if exists on account
        liteInv = getInventoryOnAccount(deviceInfo, energyCompany);
        // Inventory already exists on the account
        if (liteInv != null) {
            throw new StarsDeviceAlreadyExistsException(getAccountNumber(deviceInfo),
                                                        getSerialNumber(deviceInfo),
                                                        energyCompany.getName());
        }
        // add device to account
        liteInv = internalAddDeviceToAccount(deviceInfo,
                                             energyCompany,
                                             user);
        return liteInv;
    }

    // Gets Inventory if exists on the account
    private LiteInventoryBase getInventoryOnAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInvAcct = null;

        CustomerAccount custAcct = getCustomerAccount(deviceInfo, energyCompany);
        LiteInventoryBase liteInv = getInventory(deviceInfo, energyCompany);
        if (liteInv != null && liteInv.getAccountID() == custAcct.getAccountId()) {
            liteInvAcct = liteInv;
        }

        return liteInvAcct;

    }

    // Gets Inventory if exists for the energyCompany
    // Handles only LMHardware devices for now, will need to support other
    // device types later.
    private LiteInventoryBase getInventory(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInv = null;

        try {
            int deviceTypeId = getDeviceTypeId(deviceInfo, energyCompany);
            int categoryID = InventoryUtils.getInventoryCategoryID(deviceTypeId,
                                                                   energyCompany);
            boolean lmHardware = InventoryUtils.isLMHardware(categoryID);
            if (lmHardware) {
                LiteStarsLMHardware lmhw = (LiteStarsLMHardware) starsSearchDao.searchLMHardwareBySerialNumber(getSerialNumber(deviceInfo),
                                                                                                               energyCompany);
                liteInv = lmhw;
            }
        } catch (ObjectInOtherEnergyCompanyException e) {
            throw new RuntimeException(e);
        }

        return liteInv;
    }

    // Creates new Inventory based on the deviceType
    // Handles only LMHardware devices for now, will need to support other
    // device types later.
    private LiteInventoryBase createNewInventory(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInv = null;

        int deviceTypeId = getDeviceTypeId(deviceInfo, energyCompany);
        int categoryID = InventoryUtils.getInventoryCategoryID(deviceTypeId,
                                                               energyCompany);
        boolean lmHardware = InventoryUtils.isLMHardware(categoryID);
        if (lmHardware) {
            LiteStarsLMHardware lmhw = new LiteStarsLMHardware();
            lmhw.setCategoryID(categoryID);
            lmhw.setLmHardwareTypeID(deviceTypeId);
            lmhw.setManufacturerSerialNumber(getSerialNumber(deviceInfo));
            lmhw.setInstallDate(new Date().getTime());
            // force not null
            lmhw.setAlternateTrackingNumber("");
            lmhw.setNotes("");
            lmhw.setDeviceLabel("");

            liteInv = lmhw;
        }

        return liteInv;
    }

    private LiteInventoryBase internalAddDeviceToAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        LiteInventoryBase liteInv = null;
        // See if Inventory exists
        liteInv = getInventory(deviceInfo, energyCompany);

        if (liteInv != null) {
            // Inventory associated to an account, error out
            if (liteInv.getAccountID() > 0) {
                throw new StarsDeviceAlreadyAssignedException(getAccountNumber(deviceInfo),
                                                              getSerialNumber(deviceInfo),
                                                              energyCompany.getName());
            }
            // existing inventory, reset some fields
            // currentStateId should be retained
            liteInv.setRemoveDate(0);
            liteInv.setInstallDate(new Date().getTime());
        }// Inventory doesn't exist, create a new one
        else {
            liteInv = createNewInventory(deviceInfo, energyCompany);
        }

        // By this point, we should have an existing or new Inventory to add
        // to account
        if (liteInv == null) {
            throw new StarsInvalidDeviceTypeException(getDeviceType(deviceInfo),
                                                      energyCompany.getName());
        }
        // set common fields on the Inventory
        setInventoryValues(deviceInfo, liteInv, energyCompany);

        // call service to add device on the customer account
        liteInv = starsInventoryBaseService.addDeviceToAccount(liteInv,
                                                               energyCompany,
                                                               user, true);
        return liteInv;
    }

    private void setInventoryValues(StarsControllableDeviceDTO deviceInfo,
            LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
        // update install date, if specified
        Date installDate = deviceInfo.getFieldInstallDate();
        if (installDate != null) {
            liteInv.setInstallDate(installDate.getTime());
        }

        // update Device label, if specified
        String deviceLabel = deviceInfo.getDeviceLabel();
        if (deviceLabel != null) {
            liteInv.setDeviceLabel(deviceLabel);
        }

        // Derive Account Id
        CustomerAccount custAcct = getCustomerAccount(deviceInfo, energyCompany);
        liteInv.setAccountID(custAcct.getAccountId());

        // Derive and update Installation Company id, if specified
        String serviceCompany = deviceInfo.getServiceCompanyName();
        if (serviceCompany != null) {
            if (StringUtils.isBlank(serviceCompany)) {
                liteInv.setInstallationCompanyID(0);
            } else {
                List<LiteServiceCompany> companies = energyCompany.getAllServiceCompanies();
                for (LiteServiceCompany entry : companies) {
                    if (entry.getCompanyName().equalsIgnoreCase(serviceCompany)) {
                        liteInv.setInstallationCompanyID(entry.getCompanyID());
                        break;
                    }
                }

            }
        }
    }

    @Override
    public LiteInventoryBase updateDeviceOnAccount(
            StarsControllableDeviceDTO deviceInfo, LiteYukonUser user) {

        LiteInventoryBase liteInv = null;
        
        //Get energyCompany for the user
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        
        // Get Inventory if exists on account
        liteInv = getInventoryOnAccount(deviceInfo, energyCompany);
        // Inventory exists on the account
        if (liteInv != null) {
            // existing inventory, reset some fields
            liteInv.setRemoveDate(0);

            // set common fields on the Inventory
            setInventoryValues(deviceInfo, liteInv, energyCompany);

            // call service to update device on the customer account
            liteInv = starsInventoryBaseService.updateDeviceOnAccount(liteInv,
                                                                      energyCompany,
                                                                      user);
        } else {
            // add device to account
            liteInv = internalAddDeviceToAccount(deviceInfo,
                                                 energyCompany,
                                                 user);
        }
        return liteInv;
    }

    @Override
    public void removeDeviceFromAccount(StarsControllableDeviceDTO deviceInfo,
            LiteYukonUser user) {

        LiteInventoryBase liteInv = null;
        
        //Get energyCompany for the user
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);

        // Get Inventory if exists on account
        liteInv = getInventoryOnAccount(deviceInfo, energyCompany);
        // Error, if Inventory not found on the account
        if (liteInv == null) {
            throw new StarsDeviceNotFoundOnAccountException(getAccountNumber(deviceInfo),
                                                            getSerialNumber(deviceInfo),
                                                            energyCompany.getName());
        }

        // Remove date defaults to current date, if not specified
        Date removeDate = deviceInfo.getFieldRemoveDate();
        if (removeDate == null) {
            removeDate = new Date();
        }
        liteInv.setRemoveDate(removeDate.getTime());

        // Remove device from the account
        starsInventoryBaseService.removeDeviceFromAccount(liteInv,
                                                          false,
                                                          energyCompany,
                                                          user);
    }

}
