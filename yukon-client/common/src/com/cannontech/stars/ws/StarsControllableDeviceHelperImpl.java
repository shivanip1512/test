package com.cannontech.stars.ws;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.field.processor.impl.LatitudeLongitudeBulkFieldProcessor;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationException;
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
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.ecobee.EcobeeBuilder;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceNotFoundOnAccountException;
import com.cannontech.stars.dr.hardware.exception.StarsInvalidDeviceTypeException;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.honeywell.HoneywellBuilder;
import com.cannontech.stars.dr.itron.ItronBuilder;
import com.cannontech.stars.dr.nest.NestBuilder;
import com.cannontech.stars.dr.route.exception.StarsRouteNotFoundException;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.util.Validator;
import com.google.common.collect.Lists;

public class StarsControllableDeviceHelperImpl implements StarsControllableDeviceHelper {
    private static final Logger log = YukonLogManager.getLogger(StarsControllableDeviceHelperImpl.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private EcobeeBuilder ecobeeBuilder;
    @Autowired private HoneywellBuilder honeywellBuilder;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsDatabaseCache starsCache;
    @Autowired private StarsInventoryBaseService starsInventoryBaseService;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private NestBuilder nestBuilder;
    @Autowired private LatitudeLongitudeBulkFieldProcessor latitudeLongitudeBulkFieldProcessor;
    @Autowired private ItronBuilder itronBuilder;
    @Autowired private HardwareUiService hardwareUiService;

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
       
        YukonListEntry deviceType = getDeviceType(dto, yec);
        SerialNumberValidation serialNumberValidation = ecSettingDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION,
                                                                             SerialNumberValidation.class,
                                                                             yec.getEnergyCompanyId());
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

    private CustomerAccount getCustomerAccount(LmDeviceDto dto, EnergyCompany energyCompany) {
        CustomerAccount custAcct = null;
        try {
            custAcct = customerAccountDao.getByAccountNumber(getAccountNumber(dto), energyCompany.getDescendants(true));
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
    private YukonListEntry getDeviceType(LmDeviceDto dto, YukonEnergyCompany yec) {
        String deviceType = getDeviceTypeString(dto);
        try {
            YukonListEntry entry = YukonListEntryHelper.getEntryForEntryText(deviceType,
                                                                             YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                             yec);

            return entry;

        } catch (NotFoundException e) {
            throw new StarsInvalidDeviceTypeException(deviceType, yec.getName());
        }
    }

    @Override
    @Transactional
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser user) {
        
        //Get energyCompany for the user
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        
        HardwareType hardwareType = getHardwareType(dto, energyCompany);
        
        // Get Inventory, if exists on account
        LiteInventoryBase  liteInv = getInventoryOnAccount(dto, energyCompany);
        // Inventory already exists on the account
        if (liteInv != null) {
            throw new StarsDeviceAlreadyExistsException(getAccountNumber(dto), getSerialNumber(dto, energyCompany), energyCompany.getName());
        }
                
        // add device to account
        liteInv = internalAddDeviceToAccount(dto, energyCompany, user, hardwareType);

        return liteInv;
    }

    // Gets Inventory if exists on the account
    private LiteInventoryBase getInventoryOnAccount(LmDeviceDto dto, EnergyCompany energyCompany) {
        
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
    private LiteInventoryBase getInventory(LmDeviceDto dto, YukonEnergyCompany yec) {
        
        LiteInventoryBase lib = null;
        try {
            YukonListEntry deviceType = getDeviceType(dto, yec);
            HardwareType type = HardwareType.valueOf(deviceType.getYukonDefID());
            
            boolean lmHardware = type.isLmHardware();
            if (lmHardware) {
                LiteLmHardwareBase lmhw =
                    starsSearchDao.searchLmHardwareBySerialNumber(getSerialNumber(dto, yec), yec);
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
    private LiteInventoryBase buildLiteInventoryBase(LmDeviceDto dto, YukonEnergyCompany yec) {
        
        LiteInventoryBase lib = null;

        YukonListEntry deviceType = getDeviceType(dto, yec);
        HardwareType type = HardwareType.valueOf(deviceType.getYukonDefID());
        boolean lmHardware = type.isLmHardware();
        
        if (lmHardware) {
            LiteLmHardwareBase lmhw = new LiteLmHardwareBase();
            lmhw.setCategoryID(selectionListService.getListEntry(yec, type.getInventoryCategory().getDefinitionId()).getEntryID());
            lmhw.setLmHardwareTypeID(deviceType.getEntryID());
            lmhw.setManufacturerSerialNumber(getSerialNumber(dto, yec));
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
                                                         EnergyCompany energyCompany, 
                                                         LiteYukonUser user,
                                                         HardwareType ht) {

        boolean isNewDevice = false;
        LiteInventoryBase lib = getInventory(dto, energyCompany);
        
        // See if Inventory exists
        if (lib != null) {
            // Inventory associated to an account, error out
            if (lib.getAccountID() > 0) {
                throw new StarsDeviceAlreadyAssignedException(getAccountNumber(dto), getSerialNumber(dto, energyCompany), energyCompany.getName());
            }
            // existing inventory, reset some fields
            // currentStateId should be retained
            lib.setRemoveDate(0);
            lib.setInstallDate(new Date().getTime());
        }
        else {
            // Inventory doesn't exist, create a new one
            lib = buildLiteInventoryBase(dto, energyCompany);
            isNewDevice = true;
        }

        // By this point, we should have an existing or new Inventory to add
        // to account
        if (lib == null) {
            throw new StarsInvalidDeviceTypeException(getDeviceTypeString(dto), energyCompany.getName());
        }
        // set common fields on the Inventory
        setInventoryValues(dto, lib, energyCompany);

        LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(energyCompany);
        
        // call service to add device on the customer account
        try {
            lib = starsInventoryBaseService.addDeviceToAccount(lib, lsec, user, true);
        } catch (ItronCommunicationException e) {
            throw new StarsClientRequestException("There was a communication error trying to connect with Itron.");
        }

        if (isNewDevice) {
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
                    lib.setDeviceID(device.getPaoIdentifier().getPaoId());
                    dbChangeManager.processDbChange(lib.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                    DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);
                } catch (BadTemplateDeviceCreationException e) {
                    throw new StarsInvalidArgumentException(e.getMessage(), e);
                }
            } else if (ht.isNest()) {
                nestBuilder.createDevice(lib.getInventoryID(), dto.getSerialNumber(), ht);
            } else if (ht.isEcobee()) {
                try {
                    ecobeeBuilder.createDevice(lib.getInventoryID(), dto.getSerialNumber(), ht);
                } catch (DeviceCreationException e) {
                    throw new StarsClientRequestException("Failed to register ecobee device with ecobee server.", e);
                }
            } else if (ht.isHoneywell()) {
                try {
                    String macAddress = dto.getMacAddress();
                    Integer deviceVendorUserId = dto.getDeviceVendorUserId();
                    if (StringUtils.isBlank(macAddress) || !Validator.isMacAddress(macAddress)) {
                        throw new StarsInvalidArgumentException("Valid MAC Address is required");
                    }
                    if (deviceVendorUserId == null) {
                        throw new StarsInvalidArgumentException("Valid UserId is required");
                    }
                    honeywellBuilder.createDevice(lib.getInventoryID(), dto.getSerialNumber(), ht, macAddress,
                        dto.getDeviceVendorUserId());
                } catch (DeviceCreationException e) {
                    throw new StarsClientRequestException("Failed to register honeywell wifi device with honeywell server.", e);
                }
            } else if (ht.isItron()) {
                String macAddress = dto.getMacAddress();
                if (StringUtils.isBlank(macAddress) || !Validator.isMacAddress(macAddress, true)) {
                    throw new StarsInvalidArgumentException("Valid MAC Address is required");
                }
                Hardware hardware = hardwareUiService.getHardware(lib.getInventoryID());
                hardware.setMacAddress(macAddress);
                try {
                    itronBuilder.createDevice(hardware);
                    lib.setDeviceID(hardware.getDeviceId());
                } catch (ItronCommunicationException e) {
                    throw new StarsClientRequestException("There was a communication error trying to connect with Itron.");
                }
            }
        }

        setLocationForHardware(ht, dto, lib);

        return lib;
    }

    private void setInventoryValues(LmDeviceDto dto, LiteInventoryBase lib, EnergyCompany energyCompany) {
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
        CustomerAccount custAcct = getCustomerAccount(dto, energyCompany);
        lib.setAccountID(custAcct.getAccountId());
        
        // Update Route, if specified
        String inventoryRoute = dto.getInventoryRoute();
        if (StringUtils.isNotEmpty(inventoryRoute)) {
            
            // Search all of the energy companies routes for a name match
            List<LiteYukonPAObject> allRoutes = ecDao.getAllRoutes(energyCompany);
            Optional<LiteYukonPAObject> route = allRoutes.stream()
                                                   .filter(tempRoute -> tempRoute.getPaoName().equalsIgnoreCase(inventoryRoute))
                                                   .findFirst();
            
            LiteYukonPAObject defaultRoute = defaultRouteService.getDefaultRoute(energyCompany);
            
            if (route.isPresent()) {
                ((LiteLmHardwareBase)lib).setRouteID(route.get().getLiteID());
            } else if (defaultRoute != null && inventoryRoute.equalsIgnoreCase("Default - " + defaultRoute.getPaoName())) {
                // If we did not find a match, we check for our special case which is assigning to the EC default route
                // so we must remove the route assignment.
                ((LiteLmHardwareBase)lib).setRouteID(0);
            } else {
                throw new StarsRouteNotFoundException(inventoryRoute, energyCompany.getName());
            }
        }

        // Derive and update Installation Company id, if specified
        String serviceCompany = dto.getServiceCompanyName();
        if (serviceCompany != null) {
            if (StringUtils.isBlank(serviceCompany)) {
                lib.setInstallationCompanyID(0);
            } else {
                List<Integer> parentIds = Lists.transform(energyCompany.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
                List<ServiceCompanyDto> companies = serviceCompanyDao.getAllServiceCompaniesForEnergyCompanies(new HashSet<>(parentIds));
                for (ServiceCompanyDto entry : companies) {
                    if (entry.getCompanyName().equalsIgnoreCase(serviceCompany)) {
                        lib.setInstallationCompanyID(entry.getCompanyId());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser ecOperator) {

        //Get energyCompany for the user
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(ecOperator);
        HardwareType hardwareType = getHardwareType(dto, energyCompany);
 
        LiteInventoryBase lib = null;
        
        // Get Inventory if exists on account
        lib = getInventoryOnAccount(dto, energyCompany);
        // Inventory exists on the account
        if (lib != null) {
            // existing inventory, reset some fields
            lib.setRemoveDate(0);

            // set common fields on the Inventory
            setInventoryValues(dto, lib, energyCompany);

            LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(energyCompany);
            // call service to update device on the customer account
            lib = starsInventoryBaseService.updateDeviceOnAccount(lib, lsec, ecOperator, dto);

            setLocationForHardware(hardwareType, dto, lib);

        } else {
            // add device to account
            lib = internalAddDeviceToAccount(dto, energyCompany, ecOperator, hardwareType);
        }
        return lib;
    };

    @Override
    public void removeDeviceFromAccount(LmDeviceDto dto, LiteYukonUser ecOperator) {
        
        LiteInventoryBase liteInv = null;
        
        //Get energyCompany for the user
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(ecOperator);
        
        // Get Inventory if exists on account
        liteInv = getInventoryOnAccount(dto, energyCompany);
        // Error, if Inventory not found on the account
        if (liteInv == null) {
            throw new StarsDeviceNotFoundOnAccountException(getAccountNumber(dto), getSerialNumber(dto, energyCompany), energyCompany.getName());
        }

        // Remove date defaults to current date, if not specified
        Date removeDate = dto.getFieldRemoveDate();
        if (removeDate == null) {
            removeDate = new Date();
        }
        liteInv.setRemoveDate(removeDate.getTime());

        LiteStarsEnergyCompany lsec = starsCache.getEnergyCompany(energyCompany);
        // Remove device from the account
        starsInventoryBaseService.removeDeviceFromAccount(liteInv, lsec, ecOperator);
    }

    @Override
    public boolean isOperationAllowedForDevice(LmDeviceDto dto, LiteYukonUser user) {
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        HardwareType hardwareType = getHardwareType(dto, energyCompany);
        return isOperationAllowedForHardware(hardwareType);
    }

    /* Method to set or update the latitude and longitude for the hardware */
    private void setLocationForHardware(HardwareType hardwareType, LmDeviceDto dto, LiteInventoryBase lib) {

        if (hardwareType.isTwoWay() && lib.getDeviceID() > 0) {
            if (dto.getLatitude() != null && dto.getLongitude() != null) {
                saveLocation(dto, lib);
            } else if (dto.getLatitude() == null && dto.getLongitude() == null) {
                paoLocationDao.delete(lib.getDeviceID());
            }
        } else {
            log.warn("Location data is not supported by " + dto.getDeviceType()
            + " device type. No location data will be set for serial number " + dto.getSerialNumber());
        }
    }

    /* Method to save the location for the hardware */
    private void saveLocation(LmDeviceDto deviceDto, LiteInventoryBase lib) {
        DisplayablePao displayablePaoHardware =
                paoLoadingService.getDisplayablePao(paoDao.getYukonPao(lib.getDeviceID()));

        try {
            latitudeLongitudeBulkFieldProcessor.locationValidation(displayablePaoHardware.getPaoIdentifier(),
                deviceDto.getLatitude(), deviceDto.getLongitude());
            PaoLocation newLocation = new PaoLocation(displayablePaoHardware.getPaoIdentifier(),
                deviceDto.getLatitude(), deviceDto.getLongitude());
            paoLocationDao.save(newLocation);
        } catch (ProcessingException e) {
            throw e;
        }
    }

    private boolean isOperationAllowedForHardware(HardwareType hardwareType) {
        if (hardwareType.isNest()) {
            return false;
        }
        return true;
    }

    // Get the hardware type for the device and energy company.
    private HardwareType getHardwareType(LmDeviceDto device, EnergyCompany energyCompany ) {
        YukonListEntry deviceType = getDeviceType(device, energyCompany);
        HardwareType ht = HardwareType.valueOf(deviceType.getYukonDefID());
        return ht;
    }
}