package com.cannontech.stars.ws;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceNotFoundOnAccountException;
import com.cannontech.stars.dr.hardware.exception.StarsInvalidDeviceTypeException;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsInvalidArgumentException;

public class StarsControllableDeviceHelperImpl implements StarsControllableDeviceHelper {

    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private CustomerAccountDao customerAccountDao;    
    @Autowired private StarsInventoryBaseService starsInventoryBaseService;
    @Autowired private StarsDatabaseCache cache;
    @Autowired private YukonEnergyCompanyService yecService;

    private String getAccountNumber(LmDeviceDto dto) {
        String acctNum = dto.getAccountNumber();
        if (StringUtils.isBlank(acctNum)) {
            throw new StarsInvalidArgumentException("Account Number is required");
        }
        return acctNum;
    }

    private String getSerialNumber(LmDeviceDto dto) {
        String serialNum = dto.getSerialNumber();
        if (StringUtils.isBlank(serialNum)) {
            throw new StarsInvalidArgumentException("Serial Number is required");
        } 
        return serialNum;
    }

    private String getDeviceType(LmDeviceDto deviceInfo) {
        String deviceType = deviceInfo.getDeviceType();
        if (StringUtils.isBlank(deviceType)) {
            throw new StarsInvalidArgumentException("Device type is required");
        }
        return deviceType;
    }

    private CustomerAccount getCustomerAccount(LmDeviceDto dto, LiteStarsEnergyCompany lsec) {
        CustomerAccount custAcct = null;
        try {
            custAcct = customerAccountDao.getByAccountNumber(getAccountNumber(dto), lsec.getLiteID());
        } catch (NotFoundException e) {
            // convert to a better, Account not found exception
            throw new StarsAccountNotFoundException(getAccountNumber(dto), lsec.getName(), e);
        }
        return custAcct;
    }

    /**
     * Returns the YukonListEntry id value for deviceInfo.deviceType
     * Performs initial validation on deviceType as defined by getDeviceType(dto).
     * @param dto
     * @param lsec
     * @return deviceTypeId - yukonListEntry id value.
     * @throws StarsInvalidDeviceTypeException if yukonListEntry is not found for deviceInfo.deviceType 
     */
    private int getDeviceTypeId(LmDeviceDto dto, LiteStarsEnergyCompany lsec) {
    	String deviceType = getDeviceType(dto);
    	try {
    		int deviceTypeId = YukonListEntryHelper.getEntryIdForEntryText(deviceType, 
    				YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
    				lsec);
    		
    		return deviceTypeId;
    		
    	} catch (NotFoundException e) {
            throw new StarsInvalidDeviceTypeException(deviceType, lsec.getName());
        }
    }

    @Override
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser user, HardwareType type) {

        LiteInventoryBase liteInv = null;
        
        //Get energyCompany for the user
        YukonEnergyCompany yec = yecService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lsec = cache.getEnergyCompany(yec);
        
        // Get Inventory, if exists on account
        liteInv = getInventoryOnAccount(dto, lsec);
        // Inventory already exists on the account
        if (liteInv != null) {
            throw new StarsDeviceAlreadyExistsException(getAccountNumber(dto), getSerialNumber(dto), yec.getName());
        }
        // add device to account
        liteInv = internalAddDeviceToAccount(dto, lsec, user, type);
        return liteInv;
    }

    // Gets Inventory if exists on the account
    private LiteInventoryBase getInventoryOnAccount(LmDeviceDto dto, LiteStarsEnergyCompany lsec) {
        
        LiteInventoryBase lib = null;

        CustomerAccount custAcct = getCustomerAccount(dto, lsec);
        LiteInventoryBase existing = getInventory(dto, lsec);
        if (existing != null && existing.getAccountID() == custAcct.getAccountId()) {
            lib = existing;
        }

        return lib;

    }

    // Gets Inventory if exists for the energyCompany
    // Handles only LMHardware devices for now, will need to support other
    // device types later.
    private LiteInventoryBase getInventory(LmDeviceDto dto, LiteStarsEnergyCompany lsec) {
        
        LiteInventoryBase lib = null;
        try {
            int deviceTypeId = getDeviceTypeId(dto, lsec);
            int categoryID = InventoryUtils.getInventoryCategoryID(deviceTypeId, lsec);
            
            boolean lmHardware = InventoryUtils.isLMHardware(categoryID);
            if (lmHardware) {
                LiteLmHardwareBase lmhw = (LiteLmHardwareBase) starsSearchDao.searchLmHardwareBySerialNumber(getSerialNumber(dto), lsec);
                lib = lmhw;
            }
        } catch (ObjectInOtherEnergyCompanyException e) {
            throw new RuntimeException(e);
        }

        return lib;
    }

    // Creates new Inventory based on the deviceType
    // Handles only LMHardware devices for now, will need to support other
    // device types later.
    private LiteInventoryBase buildLiteInventoryBase(LmDeviceDto dto, LiteStarsEnergyCompany lsec) {
        
        LiteInventoryBase lib = null;

        int deviceTypeId = getDeviceTypeId(dto, lsec);
        int categoryID = InventoryUtils.getInventoryCategoryID(deviceTypeId, lsec);
        boolean lmHardware = InventoryUtils.isLMHardware(categoryID);
        
        if (lmHardware) {
            LiteLmHardwareBase lmhw = new LiteLmHardwareBase();
            lmhw.setCategoryID(categoryID);
            lmhw.setLmHardwareTypeID(deviceTypeId);
            lmhw.setManufacturerSerialNumber(getSerialNumber(dto));
            lmhw.setInstallDate(new Date().getTime());
            // force not null
            lmhw.setAlternateTrackingNumber("");
            lmhw.setNotes("");
            lmhw.setDeviceLabel("");

            lib = lmhw;
        }

        return lib;
    }

    private LiteInventoryBase internalAddDeviceToAccount(LmDeviceDto dto, 
                                                         LiteStarsEnergyCompany lsec, 
                                                         LiteYukonUser user, 
                                                         HardwareType type) {

        LiteInventoryBase lib = null;
        // See if Inventory exists
        lib = getInventory(dto, lsec);

        if (lib != null) {
            // Inventory associated to an account, error out
            if (lib.getAccountID() > 0) {
                throw new StarsDeviceAlreadyAssignedException(getAccountNumber(dto), getSerialNumber(dto), lsec.getName());
            }
            // existing inventory, reset some fields
            // currentStateId should be retained
            lib.setRemoveDate(0);
            lib.setInstallDate(new Date().getTime());
        }// Inventory doesn't exist, create a new one
        else {
            lib = buildLiteInventoryBase(dto, lsec);
        }

        // By this point, we should have an existing or new Inventory to add
        // to account
        if (lib == null) {
            throw new StarsInvalidDeviceTypeException(getDeviceType(dto), lsec.getName());
        }
        // set common fields on the Inventory
        setInventoryValues(dto, lib, lsec);

        // call service to add device on the customer account
        lib = starsInventoryBaseService.addDeviceToAccount(lib, lsec, user, true);
        return lib;
    }

    private void setInventoryValues(LmDeviceDto dto, LiteInventoryBase lib, LiteStarsEnergyCompany lsec) {
        // update install date, if specified
        Date installDate = dto.getFieldInstallDate();
        if (installDate != null) {
            lib.setInstallDate(installDate.getTime());
        }

        // update Device label, if specified
        String deviceLabel = dto.getDeviceLabel();
        if (deviceLabel != null) {
            lib.setDeviceLabel(deviceLabel);
        }

        // Derive Account Id
        CustomerAccount custAcct = getCustomerAccount(dto, lsec);
        lib.setAccountID(custAcct.getAccountId());

        // Derive and update Installation Company id, if specified
        String serviceCompany = dto.getServiceCompanyName();
        if (serviceCompany != null) {
            if (StringUtils.isBlank(serviceCompany)) {
                lib.setInstallationCompanyID(0);
            } else {
                List<LiteServiceCompany> companies = lsec.getAllServiceCompanies();
                for (LiteServiceCompany entry : companies) {
                    if (entry.getCompanyName().equalsIgnoreCase(serviceCompany)) {
                        lib.setInstallationCompanyID(entry.getCompanyID());
                        break;
                    }
                }

            }
        }
    }

    @Override
    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser user, HardwareType type) {

        LiteInventoryBase lib = null;
        
        //Get energyCompany for the user
        LiteStarsEnergyCompany energyCompany = cache.getEnergyCompanyByUser(user);
        
        // Get Inventory if exists on account
        lib = getInventoryOnAccount(dto, energyCompany);
        // Inventory exists on the account
        if (lib != null) {
            // existing inventory, reset some fields
            lib.setRemoveDate(0);

            // set common fields on the Inventory
            setInventoryValues(dto, lib, energyCompany);

            // call service to update device on the customer account
            lib = starsInventoryBaseService.updateDeviceOnAccount(lib,
                                                                      energyCompany,
                                                                      user);
        } else {
            // add device to account
            lib = internalAddDeviceToAccount(dto, energyCompany, user, type);
        }
        return lib;
    }

    @Override
    public void removeDeviceFromAccount(LmDeviceDto deviceInfo,
            LiteYukonUser user) {

        LiteInventoryBase liteInv = null;
        
        //Get energyCompany for the user
        YukonEnergyCompany yec = yecService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lsec = cache.getEnergyCompany(yec);

        // Get Inventory if exists on account
        liteInv = getInventoryOnAccount(deviceInfo, lsec);
        // Error, if Inventory not found on the account
        if (liteInv == null) {
            throw new StarsDeviceNotFoundOnAccountException(getAccountNumber(deviceInfo), getSerialNumber(deviceInfo), lsec.getName());
        }

        // Remove date defaults to current date, if not specified
        Date removeDate = deviceInfo.getFieldRemoveDate();
        if (removeDate == null) {
            removeDate = new Date();
        }
        liteInv.setRemoveDate(removeDate.getTime());

        // Remove device from the account
        starsInventoryBaseService.removeDeviceFromAccount(liteInv, lsec, user);
    }

}