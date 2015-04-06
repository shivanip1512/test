package com.cannontech.stars.ws;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.ecobee.EcobeeBuilder;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceNotFoundOnAccountException;
import com.cannontech.stars.dr.hardware.exception.StarsInvalidDeviceTypeException;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.yukon.IDatabaseCache;

public class StarsControllableDeviceHelperImpl implements StarsControllableDeviceHelper {

    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private CustomerAccountDao customerAccountDao;    
    @Autowired private StarsInventoryBaseService starsInventoryBaseService;
    @Autowired private StarsDatabaseCache starsCache;
    @Autowired private IDatabaseCache cache;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private SelectionListService selectionListService;
    @Autowired private EcobeeBuilder ecobeeBuilder;
    @Autowired private EnergyCompanySettingDao ecSettingDao;

    private String getAccountNumber(LmDeviceDto dto) {
        String acctNum = dto.getAccountNumber();
        if (StringUtils.isBlank(acctNum)) {
            throw new StarsInvalidArgumentException("Account Number is required");
        }
        return acctNum;
    }

    private String getSerialNumber(LmDeviceDto dto, YukonEnergyCompany yec) {
        String serialNum = dto.getSerialNumber();
        if (StringUtils.isBlank(serialNum)) {
            throw new StarsInvalidArgumentException("Serial Number is required");
        }
        LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(yec);
        YukonListEntry deviceType = getDeviceType(dto, lsec);
        SerialNumberValidation serialNumberValidation = ecSettingDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION,
                                                                             SerialNumberValidation.class,
                                                                             lsec.getEnergyCompanyId());
        /* Check if the current energy company setting is numeric) */
        if (serialNumberValidation == SerialNumberValidation.NUMERIC) {
            /* Check if the serial number entered is numeric */
            if (!StringUtils.isNumeric(serialNum)) {
                throw new StarsInvalidArgumentException("Serial Number must be Numeric");
                
            }
        }/* Check if the current energy company setting is alphanumeric */ 
        else if (serialNumberValidation == SerialNumberValidation.ALPHANUMERIC) {
            /* Check if the serial number entered is alphanumeric */
            if (!StringUtils.isAlphanumeric(serialNum)) {
                throw new StarsInvalidArgumentException("Serial Number must be Alphanumeric");
            }
        }
        /*
         * Implementing serial Number Validation based on the type of protocol
         * supported by device
         */
        HardwareType hardwareType = HardwareType.valueOf(deviceType.getYukonDefID());
        if (!hardwareType.getHardwareConfigType().isSerialNumberValid(serialNum)) {
            throw new StarsInvalidArgumentException("Serial Number is not valid");
        }

        return serialNum;
    }

    private String getDeviceTypeString(LmDeviceDto dto) {
        String deviceType = dto.getDeviceType();
        if (StringUtils.isBlank(deviceType)) {
            throw new StarsInvalidArgumentException("Device type is required");
        }
        return deviceType;
    }

    private CustomerAccount getCustomerAccount(LmDeviceDto dto, YukonEnergyCompany energyCompany) {
        CustomerAccount custAcct = null;
        try {
            custAcct = customerAccountDao.getByAccountNumber(getAccountNumber(dto), energyCompany.getEnergyCompanyId());
        } catch (NotFoundException e) {
            // convert to a better, Account not found exception
            throw new StarsAccountNotFoundException(getAccountNumber(dto), energyCompany.getName(), e);
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
    private YukonListEntry getDeviceType(LmDeviceDto dto, LiteStarsEnergyCompany lsec) {
    	String deviceType = getDeviceTypeString(dto);
    	try {
    		YukonListEntry entry = YukonListEntryHelper.getEntryForEntryText(deviceType, 
    				YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
    				lsec);
    		
    		return entry;
    		
    	} catch (NotFoundException e) {
            throw new StarsInvalidDeviceTypeException(deviceType, lsec.getName());
        }
    }

    @Override
    @Transactional
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser user) {
        
        //Get energyCompany for the user
        YukonEnergyCompany yec = ecDao.getEnergyCompanyByOperator(user);
        return addDeviceToAccount(dto, user, yec);
        
    }
    
    @Override
    @Transactional
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser user, YukonEnergyCompany yec) {

        LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(yec);
        
        // Get Inventory, if exists on account
        LiteInventoryBase  liteInv = getInventoryOnAccount(dto, lsec);
        // Inventory already exists on the account
        if (liteInv != null) {
            throw new StarsDeviceAlreadyExistsException(getAccountNumber(dto), getSerialNumber(dto, yec), yec.getName());
        }
                
        // add device to account
        liteInv = internalAddDeviceToAccount(dto, lsec, user);

        return liteInv;
    }

    // Gets Inventory if exists on the account
    private LiteInventoryBase getInventoryOnAccount(LmDeviceDto dto, LiteStarsEnergyCompany energyCompany) {
        
        LiteInventoryBase lib = null;

        CustomerAccount custAcct = getCustomerAccount(dto, energyCompany);
        LiteInventoryBase existing = getInventory(dto, energyCompany);
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
            YukonListEntry deviceType = getDeviceType(dto, lsec);
            HardwareType type = HardwareType.valueOf(deviceType.getYukonDefID());
            
            boolean lmHardware = type.isLmHardware();
            if (lmHardware) {
                LiteLmHardwareBase lmhw =
                    starsSearchDao.searchLmHardwareBySerialNumber(getSerialNumber(dto, lsec), lsec);
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

        YukonListEntry deviceType = getDeviceType(dto, lsec);
        HardwareType type = HardwareType.valueOf(deviceType.getYukonDefID());
        boolean lmHardware = type.isLmHardware();
        
        if (lmHardware) {
            LiteLmHardwareBase lmhw = new LiteLmHardwareBase();
            lmhw.setCategoryID(selectionListService.getListEntry(lsec, type.getInventoryCategory().getDefinitionId()).getEntryID());
            lmhw.setLmHardwareTypeID(deviceType.getEntryID());
            lmhw.setManufacturerSerialNumber(getSerialNumber(dto, lsec));
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
                                                         LiteYukonUser user) {

        boolean isNewDevice = false;
        LiteInventoryBase lib = getInventory(dto, lsec);
        
        // See if Inventory exists
        if (lib != null) {
            // Inventory associated to an account, error out
            if (lib.getAccountID() > 0) {
                throw new StarsDeviceAlreadyAssignedException(getAccountNumber(dto), getSerialNumber(dto, lsec), lsec.getName());
            }
            // existing inventory, reset some fields
            // currentStateId should be retained
            lib.setRemoveDate(0);
            lib.setInstallDate(new Date().getTime());
        }
        else {
            // Inventory doesn't exist, create a new one
            lib = buildLiteInventoryBase(dto, lsec);
            isNewDevice = true;
        }

        // By this point, we should have an existing or new Inventory to add
        // to account
        if (lib == null) {
            throw new StarsInvalidDeviceTypeException(getDeviceTypeString(dto), lsec.getName());
        }
        // set common fields on the Inventory
        setInventoryValues(dto, lib, lsec);

        // call service to add device on the customer account
        lib = starsInventoryBaseService.addDeviceToAccount(lib, lsec, user, true);
        if (isNewDevice) {
            YukonListEntry deviceType = getDeviceType(dto, lsec);
            HardwareType ht = HardwareType.valueOf(deviceType.getYukonDefID());
            if (ht.isRf()) {
                try {
                    // add rf device
                    RfnManufacturerModel mm = RfnManufacturerModel.getForType(ht.getForHardwareType()).get(0);
                    String manufacturer = mm.getManufacturer().trim();
                    String model = mm.getModel().trim();
                    String templatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, 
                            "*RfnTemplate_");
                    String templateName = templatePrefix + manufacturer + "_" + model;
                    String deviceName = dto.getSerialNumber();
                    YukonDevice newDevice = deviceCreationService.createDeviceByTemplate(templateName, deviceName, true);
                    RfnIdentifier rfn = new RfnIdentifier(dto.getSerialNumber(), manufacturer, model);
                    RfnDevice device = new RfnDevice(deviceName, newDevice.getPaoIdentifier(), rfn);
                    rfnDeviceDao.updateDevice(device);
                    inventoryBaseDao.updateInventoryBaseDeviceId(lib.getInventoryID(), device.getPaoIdentifier().getPaoId());
                    dbChangeManager.processDbChange(lib.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                    DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);
                } catch (BadTemplateDeviceCreationException e) {
                    throw new StarsInvalidArgumentException(e.getMessage(), e);
                }
            } else if (ht.isEcobee()) {
                try {
                    ecobeeBuilder.createDevice(lib.getInventoryID(), dto.getSerialNumber(), ht);
                } catch (DeviceCreationException e) {
                    throw new StarsClientRequestException("Failed to register ecobee device with ecobee server.", e);
                }
            }
        }
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
        if (StringUtils.isNotEmpty(deviceLabel)) {
            lib.setDeviceLabel(deviceLabel);
        }else{
            lib.setDeviceLabel(dto.getSerialNumber()); 
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
    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser user) {

        //Get energyCompany for the user
        YukonEnergyCompany yec = ecDao.getEnergyCompanyByOperator(user);
        return updateDeviceOnAccount(dto, user, yec);
        
    }
    
    @Override
    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser user, YukonEnergyCompany yec) {
        LiteInventoryBase lib = null;
        
        LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(yec);
        
        // Get Inventory if exists on account
        lib = getInventoryOnAccount(dto, lsec);
        // Inventory exists on the account
        if (lib != null) {
            // existing inventory, reset some fields
            lib.setRemoveDate(0);

            // set common fields on the Inventory
            setInventoryValues(dto, lib, lsec);

            // call service to update device on the customer account
            lib = starsInventoryBaseService.updateDeviceOnAccount(lib, lsec, user);
        } else {
            // add device to account
            lib = internalAddDeviceToAccount(dto, lsec, user);
        }
        return lib;
    };

    @Override
    public void removeDeviceFromAccount(LmDeviceDto dto, LiteYukonUser user) {
        
        //Get energyCompany for the user
        YukonEnergyCompany yec = ecDao.getEnergyCompanyByOperator(user);
        removeDeviceFromAccount(dto, user, yec);
        
    }
    
    @Override
    public void removeDeviceFromAccount(LmDeviceDto dto, LiteYukonUser user, YukonEnergyCompany yec) {
        
        LiteInventoryBase liteInv = null;
        
        LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(yec);
        
        // Get Inventory if exists on account
        liteInv = getInventoryOnAccount(dto, lsec);
        // Error, if Inventory not found on the account
        if (liteInv == null) {
            throw new StarsDeviceNotFoundOnAccountException(getAccountNumber(dto), getSerialNumber(dto, lsec), lsec.getName());
        }

        // Remove date defaults to current date, if not specified
        Date removeDate = dto.getFieldRemoveDate();
        if (removeDate == null) {
            removeDate = new Date();
        }
        liteInv.setRemoveDate(removeDate.getTime());

        // Remove device from the account
        starsInventoryBaseService.removeDeviceFromAccount(liteInv, lsec, user);
    }

}