package com.cannontech.web.stars.dr.operator.hardware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.LMHardwareClass;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.stars.event.EventInventory;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.model.InventoryCheckingAddDto;
import com.cannontech.web.stars.dr.operator.hardware.model.SerialNumber;
import com.cannontech.web.stars.dr.operator.hardware.model.SwitchAssignment;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareUiService;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareDtoValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.SerialNumberValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

@RequestMapping("/operator/hardware/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class OperatorHardwareController {
    public StarsDatabaseCache starsDatabaseCache;
    
    private HardwareEventLogService hardwareEventLogService;
    private HardwareUiService hardwareUiService;
    private EnergyCompanyDao energyCompanyDao;
    private EnergyCompanyService energyCompanyService;
    private PaoDao paoDao;
    private ServiceCompanyDao serviceCompanyDao;
    private AddressDao addressDao;
    private RolePropertyDao rolePropertyDao;
    private HardwareDtoValidator hardwareDtoValidator;
    private SerialNumberValidator serialNumberValidator;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private StarsSearchDao starsSearchDao;
    private YukonListDao yukonListDao;
    private WarehouseDao warehouseDao;
    private CustomerAccountDao customerAccountDao;
    private ContactDao contactDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private HardwareService hardwareService;
    
    /* HARDWARE LIST PAGE*/
    @RequestMapping
    public String hardwareList(YukonUserContext userContext, ModelMap modelMap, AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        SerialNumber serialNumber = new SerialNumber();
        modelMap.addAttribute("serialNumber", serialNumber);
        
        setupHardwareListModelMap(accountInfoFragment, modelMap, energyCompany, userContext);
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        return "operator/hardware/hardwareList.jsp";
    }
    
    @RequestMapping
    public String checkSerialNumber(@ModelAttribute("serialNumber") SerialNumber serialNumber, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment,
                                 String hardwareClass) {
        
        LMHardwareClass lmHardwareClass = LMHardwareClass.valueOf(hardwareClass);
        serialNumberValidator.validate(serialNumber, bindingResult);
        
        if(bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            if(lmHardwareClass.equals(LMHardwareClass.SWITCH)) {
                modelMap.addAttribute("showSwitchCheckingPopup", true);
            } else {
                modelMap.addAttribute("showThermostatCheckingPopup", true);
            }
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            return "redirect:hardwareList"; 
        }
        
        try {
            LiteStarsLMHardware possibleDuplicate = (LiteStarsLMHardware)starsSearchDao.searchLMHardwareBySerialNumber(serialNumber.getSerialNumber(), accountInfoFragment.getEnergyCompanyId());

            InventoryCheckingAddDto inventoryCheckingAddDto = new InventoryCheckingAddDto(serialNumber.getSerialNumber());
            inventoryCheckingAddDto.setIsSwitch(lmHardwareClass.equals(LMHardwareClass.SWITCH));
            
            
            if(possibleDuplicate != null) {
                inventoryCheckingAddDto.setInventoryId(possibleDuplicate.getInventoryID());
                inventoryCheckingAddDto.setSerialNumber(serialNumber.getSerialNumber());
                
                String type = yukonListDao.getYukonListEntry(possibleDuplicate.getLmHardwareTypeID()).getEntryText();
                inventoryCheckingAddDto.setDeviceType(type);

                if(possibleDuplicate.getAccountID() > CtiUtilities.NONE_ZERO_ID) {
                    if(possibleDuplicate.getAccountID() == accountInfoFragment.getAccountId()) {
                        /* Return to the list page with the 'Alread on this account' popup. */
                        modelMap.addAttribute("sameAccountSerial", serialNumber.getSerialNumber());
                    } else {
                        /* Return to the list page with the confirm steal from account popup. */
                        modelMap.addAttribute("confirmAccountSerial", serialNumber.getSerialNumber());
                        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                        String none = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.noneSelectOption");
                        CustomerAccount account = customerAccountDao.getById(possibleDuplicate.getAccountID());
                        inventoryCheckingAddDto.setAccountNumber(account.getAccountNumber());
                        
                        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(possibleDuplicate.getAccountID());
                        
                        inventoryCheckingAddDto.setName(primaryContact.getContFirstName() + primaryContact.getContFirstName() == null ? none : primaryContact.getContFirstName() + " " + primaryContact.getContLastName());
                        
                        LiteAddress address = addressDao.getByAddressId(primaryContact.getAddressID());
                        inventoryCheckingAddDto.setAddress(Address.getDisplayableAddress(address));
                    }
                } else {
                    /* Return to the list page with the confirm add from warehouse popup. */
                    modelMap.addAttribute("confirmWarehouseSerial", serialNumber.getSerialNumber());
                    inventoryCheckingAddDto.setAltTrackingNumber(possibleDuplicate.getAlternateTrackingNumber());
                    
                    Warehouse warehouse = warehouseDao.findWarehouseForInventoryId(possibleDuplicate.getInventoryID());
                    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                    String defaultWarehouse = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.serialNumber.defaultWarehouse");
                    inventoryCheckingAddDto.setWarehouse(warehouse == null ? defaultWarehouse : warehouse.getWarehouseName());
                }
                
            } else {
                
                boolean invCheckingCreate = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING_CREATE, userContext.getYukonUser());
                if(invCheckingCreate) {
                    /* Return to the list page with the confirm creation popup. */
                    modelMap.addAttribute("confirmCreateSerial", serialNumber.getSerialNumber());
                } else {
                    /* Return to the list page with the SN unavailable popup. */
                    modelMap.addAttribute("notFoundSerial", serialNumber.getSerialNumber());
                }
                
            }

            modelMap.addAttribute("checkingAdd", inventoryCheckingAddDto);
            
        } catch (ObjectInOtherEnergyCompanyException e) {
            /* Return to the list page with the hardware found in another ec popup. */
            modelMap.addAttribute("anotherECSerial", serialNumber.getSerialNumber());
            modelMap.addAttribute("anotherEC", StringEscapeUtils.escapeHtml(e.getYukonEnergyCompany().getName()));
        }
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        setupHardwareListModelMap(accountInfoFragment, modelMap, energyCompany, userContext);
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/hardware/hardwareList.jsp";
    }
    
    /* HARDWARE CREATE PAGE*/
    @RequestMapping
    public String hardwareCreate(YukonUserContext userContext, ModelMap modelMap, AccountInfoFragment accountInfoFragment, String hardwareClass, String serialNumber) throws ServletRequestBindingException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, userContext.getYukonUser());
        
        LMHardwareClass lmHardwareClass = LMHardwareClass.valueOf(hardwareClass.toUpperCase());
        setupHardwareCreateModelMap(modelMap, lmHardwareClass, userContext);
        
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setHardwareClass(lmHardwareClass);
        hardwareDto.setFieldInstallDate(new Date());
        
        if(StringUtils.isNotBlank(serialNumber)) {
            hardwareDto.setSerialNumber(serialNumber);
        }
        
        modelMap.addAttribute("hardwareDto", hardwareDto);
        
        setupHardwareEditModelMap(accountInfoFragment, null, modelMap, userContext, false);
        
        return "operator/hardware/hardware.jsp";
    }
    
    /* HARDWARE EDIT PAGE*/
    @RequestMapping
    public String hardwareEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext,  AccountInfoFragment accountInfoFragment, int inventoryId) {
        /* Page Edit Mode */
        setPageMode(modelMap, userContext);
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, accountInfoFragment.getEnergyCompanyId(), accountInfoFragment.getAccountId());
        
        /* Set two way device name when none is choosen yet for two way switches */
        if(hardwareDto.getHardwareType().isSwitch() && hardwareDto.getHardwareType().isTwoWay() &&hardwareDto.getDeviceId() <= 0) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext); 
            hardwareDto.setTwoWayDeviceName(messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.noTwoWayDeviceName"));
        }
        
        modelMap.addAttribute("hardwareDto", hardwareDto);
        modelMap.addAttribute("displayName", hardwareDto.getDisplayName());
        
        setupHardwareShowHideElements(hardwareDto, modelMap, userContext);
        
        setupHardwareEditModelMap(accountInfoFragment, inventoryId, modelMap, userContext, true);
        
        return "operator/hardware/hardware.jsp";
    }
    
    @RequestMapping
    public String updateHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment,
                                 int inventoryId) throws ServletRequestBindingException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Update */
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            return "redirect:hardwareList";
        }
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        
        boolean statusChange = false;
        
        /* Validate and Update*/
        try {
            hardwareEventLogService.hardwareUpdateAttemptedByOperator(userContext.getYukonUser(),
                                                                      hardwareDto.getSerialNumber());
            
            hardwareDtoValidator.validate(hardwareDto, bindingResult);
            
            if(!bindingResult.hasErrors()) {
                
                hardwareDto.setInventoryId(inventoryId);
                statusChange = hardwareUiService.updateHardware(userContext, hardwareDto);
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        }
        
        setupHardwareEditModelMap(accountInfoFragment, inventoryId, modelMap, userContext, true);
        
        if (bindingResult.hasErrors()) {
            /* Return back to the jsp with the errors */
            setPageMode(modelMap, userContext);
            modelMap.addAttribute("displayName", hardwareDto.getDisplayName());
            
            /* Add errors to flash scope */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            if(hardwareDto.getHardwareType() == HardwareType.NON_YUKON_METER) {
                return "operator/hardware/meterProfile.jsp";
            } else {
                setupHardwareShowHideElements(hardwareDto, modelMap, userContext);
                return "operator/hardware/hardware.jsp";
            }
        } 
        
        /* If the device status was changed, spawn an event for it */
        if(statusChange){
            EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
        }
        
        /* Flash hardware updated */
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        
        return "redirect:hardwareList";
    }
    
    @RequestMapping
    public String createHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            return "redirect:hardwareList";
        }
        
        hardwareEventLogService.hardwareCreationAttemptedByOperator(userContext.getYukonUser(),
                                                                    accountInfoFragment.getAccountNumber(),
                                                                    hardwareDto.getSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, userContext.getYukonUser());
        int inventoryId = -1;
        
        try {
            hardwareDtoValidator.validate(hardwareDto, bindingResult);
        
            if(!bindingResult.hasErrors()) {
                inventoryId = hardwareUiService.createHardware(hardwareDto, accountInfoFragment.getAccountId(), userContext);
                /* If the device status was set, spawn an event for it. */
                if(hardwareDto.getDeviceStatusEntryId() != null && hardwareDto.getDeviceStatusEntryId() != 0) {
                    EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
                }
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        }
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            if(hardwareDto.getHardwareType() == HardwareType.NON_YUKON_METER) {
                modelMap.addAttribute("mode", PageEditMode.CREATE);
                AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
                return "operator/hardware/meterProfile.jsp";
            } else {
                setupHardwareCreateModelMap(modelMap, hardwareDto.getHardwareClass(), userContext);
                setupHardwareEditModelMap(accountInfoFragment, null, modelMap, userContext, false);
                return "operator/hardware/hardware.jsp";
            }
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
        setupHardwareEditModelMap(accountInfoFragment, inventoryId, modelMap, userContext, true);

        return "redirect:hardwareList";
    }
    
    @RequestMapping
    public String deleteHardware(ModelMap modelMap, YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope, AccountInfoFragment accountInfoFragment,
                                 int inventoryId, String deleteOption) throws Exception {
        HardwareDto hardwareToDelete = hardwareUiService.getHardwareDto(inventoryId, accountInfoFragment.getEnergyCompanyId(), accountInfoFragment.getAccountId());
        hardwareEventLogService.hardwareDeletionAttemptedByOperator(userContext.getYukonUser(), hardwareToDelete.getDisplayName());
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        /* Delete this hardware or just take it off the account and put in back in the warehouse */
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        YukonEnergyCompany yukonEnergyCompany = energyCompanyService.getEnergyCompanyByAccountId(accountInfoFragment.getAccountId());
        hardwareService.deleteHardware(userContext, delete, inventoryId, accountInfoFragment.getAccountId(), yukonEnergyCompany);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        if(delete) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        } else {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareRemoved"));
        }
        return "redirect:hardwareList";
    }
    

    @RequestMapping
    public View createYukonDevice(ModelMap modelMap, String deviceName, HttpServletResponse response, YukonUserContext userContext,
                                  int inventoryId) throws ServletRequestBindingException {
        
        hardwareEventLogService.twoWayHardwareCreationAttemptedByOperator(userContext.getYukonUser(), 
                                                                          deviceName);
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        int deviceId = -1;
        try {
            SimpleDevice yukonDevice = hardwareUiService.createTwoWayDevice(userContext, inventoryId, deviceName);
            deviceId = yukonDevice.getDeviceId();
        } catch (StarsTwoWayLcrYukonDeviceCreationException e) {
            modelMap.addAttribute("errorOccurred", Boolean.TRUE);
            modelMap.addAttribute("errorMsg", e.getMessage());
            return new JsonView();
        }
        modelMap.addAttribute("errorOccurred", Boolean.FALSE);
        modelMap.addAttribute("deviceId", deviceId);
        
        return new JsonView();
    }
    
    @RequestMapping
    public String serviceCompanyInfo(ModelMap modelMap, YukonUserContext userContext, int serviceCompanyId) throws ServletRequestBindingException {
        ServiceCompanyDto serviceCompanyDto = serviceCompanyDao.getCompanyById(serviceCompanyId);
        LiteAddress serviceCompanyAddress = addressDao.getByAddressId(serviceCompanyDto.getAddressId());
        Address address = Address.getDisplayableAddress(serviceCompanyAddress);
        
        modelMap.addAttribute("companyName", serviceCompanyDto.getCompanyName());
        modelMap.addAttribute("address", address);
        modelMap.addAttribute("mainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());
        
        return "operator/hardware/serviceCompanyInfo.jsp";
    }
    
    @RequestMapping
    public String addDeviceToAccount(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment, FlashScope flashScope, 
                                     boolean fromAccount, 
                                     int inventoryId) {
        
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.assigningHardwareAttemptedByOperator(userContext.getYukonUser(),
                                                                     accountInfoFragment.getAccountNumber(),
                                                                     lmHardwareBase.getManufacturerSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
        
        hardwareUiService.addDeviceToAccount(liteInventoryBase, accountInfoFragment.getAccountId(), fromAccount, energyCompany, userContext.getYukonUser());
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:hardwareList";
    }
    
    @RequestMapping
    public String meterProfile(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment, FlashScope flashScope, int inventoryId) {
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());

        /* Page Edit Mode */
        setPageMode(modelMap, userContext);
        
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, accountInfoFragment.getEnergyCompanyId(), accountInfoFragment.getAccountId());
        
        modelMap.addAttribute("hardwareDto", hardwareDto);
        modelMap.addAttribute("displayName", hardwareDto.getDisplayName());
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        return "operator/hardware/meterProfile.jsp";
    }
    
    @RequestMapping
    public String meterProfileCreate(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, userContext.getYukonUser());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        modelMap.addAttribute("displayName", "Create Meter");
        
        modelMap.addAttribute("mode", PageEditMode.CREATE);
        
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setHardwareType(HardwareType.NON_YUKON_METER);
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(HardwareType.NON_YUKON_METER.getDefinitionId());
        if(typeEntry.getEntryID() == 0) {
            /* This happens when using stars to handle meters (role property z_meter_mct_base_desig) but this energy company does
             * not have the 'MCT' device type (which is the non yukon meter device type) added to it's device type list in the 
             * config energy company section yet.  This is a setup issue and we will blow up here for now.  */
            throw new NoNonYukonMeterDeviceTypeException("There is no meter type in the device types list for this energy company.");
        }
        hardwareDto.setHardwareTypeEntryId(typeEntry.getEntryID());
        hardwareDto.setFieldInstallDate(new Date());
        
        for(SwitchAssignment assignement : hardwareUiService.getSwitchAssignments(new ArrayList<Integer>(), accountInfoFragment.getAccountId())) {
            hardwareDto.getSwitchAssignments().add(assignement);
        }

        modelMap.addAttribute("hardwareDto", hardwareDto);
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        return "operator/hardware/meterProfile.jsp";
    }
    
    @RequestMapping
    public String addMeter(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment, FlashScope flashScope, int meterId) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meterId);
        hardwareEventLogService.hardwareMeterCreationAttemptedByOperator(userContext.getYukonUser(),
                                                                         liteYukonPAO.getPaoName());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        hardwareUiService.addYukonMeter(meterId, accountInfoFragment.getAccountId(), userContext);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:hardwareList";
    }

    @RequestMapping
    public String changeOut(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment, 
                            FlashScope flashScope, int changeOutId, int oldInventoryId, boolean isMeter) {

        // Log change out attempt
        HardwareDto oldHardware = hardwareUiService.getHardwareDto(oldInventoryId, accountInfoFragment.getEnergyCompanyId(), accountInfoFragment.getAccountId());
        if(isMeter) {
            LiteYukonPAObject oldLiteYukonPAO = paoDao.getLiteYukonPAO(oldHardware.getDeviceId());
            LiteYukonPAObject newLiteYukonPAO = paoDao.getLiteYukonPAO(changeOutId);
            hardwareEventLogService.hardwareChangeOutForMeterAttemptedByOperator(userContext.getYukonUser(),
                                                                                 oldLiteYukonPAO.getPaoName(),
                                                                                 newLiteYukonPAO.getPaoName());
        } else {
            LMHardwareBase changeOutInventory = lmHardwareBaseDao.getById(changeOutId);
            hardwareEventLogService.hardwareChangeOutAttemptedByOperator(userContext.getYukonUser(), 
                                                                         oldHardware.getSerialNumber(), 
                                                                         changeOutInventory.getManufacturerSerialNumber());
        }

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        hardwareUiService.changeOutInventory(oldInventoryId, changeOutId, userContext, isMeter);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareChangeOut"));
        
        return "redirect:hardwareList";
    }
    
    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
    }

    /* HELPERS */
    
    private void setupHardwareCreateModelMap(ModelMap modelMap, LMHardwareClass lmHardwareClass, YukonUserContext userContext) {
        modelMap.addAttribute("displayName", "Create Hardware");
        
        modelMap.addAttribute("mode", PageEditMode.CREATE);
        
        List<DeviceTypeOpiton> deviceTypeOptions = Lists.newArrayList();
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        
        List<YukonListEntry> deviceTypeList = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE).getYukonListEntries();
        for(YukonListEntry deviceTypeEntry : deviceTypeList) {
            HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
            if(type.getLMHardwareClass() == lmHardwareClass) {
                DeviceTypeOpiton option = new DeviceTypeOpiton();
                option.setDisplayName(deviceTypeEntry.getEntryText());
                option.setHardwareTypeEntryId(deviceTypeEntry.getEntryID());
                deviceTypeOptions.add(option);
            }
        }
        
        modelMap.addAttribute("deviceTypes", deviceTypeOptions);
        
        if(lmHardwareClass == LMHardwareClass.SWITCH) {
            modelMap.addAttribute("displayTypeKey", ".switches.displayType");
        } else if (lmHardwareClass == LMHardwareClass.THERMOSTAT) {
            modelMap.addAttribute("displayTypeKey", ".thermostats.displayType");
        } else {
            modelMap.addAttribute("displayTypeKey", ".meters.displayType");
        }
        
        if(lmHardwareClass != LMHardwareClass.METER) {
            modelMap.addAttribute("showSerialNumber", true);
            modelMap.addAttribute("serialNumberEditable", true);
            modelMap.addAttribute("showRoute", true);
        }
    }
    
    private void setupHardwareShowHideElements(HardwareDto hardwareDto, ModelMap modelMap, YukonUserContext userContext) {
        boolean inventoryChecking = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, userContext.getYukonUser());
        
        /* For switches and tstats, if they have inventory checking turned off they can edit the serial number. */
        HardwareType hardwareType = hardwareDto.getHardwareType(); 
        if(!inventoryChecking && hardwareType.getLMHardwareClass() != LMHardwareClass.METER) {
            modelMap.addAttribute("serialNumberEditable", true);
        }
        
        /* For switches and tstats, show serial number instead of device name and show the route. */
        if(hardwareType.getLMHardwareClass() != LMHardwareClass.METER) {
            modelMap.addAttribute("showRoute", true);
            modelMap.addAttribute("showSerialNumber", true);
        }
        
        if(hardwareType.isSwitch() && hardwareType.isTwoWay()) {
            modelMap.addAttribute("showTwoWay", true);
        }
    }
    
    private void setPageMode(ModelMap modelMap, YukonUserContext userContext) {
     // pageEditMode
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
    }
    
    private void setupHardwareListModelMap(AccountInfoFragment accountInfoFragment, ModelMap modelMap, 
                                           LiteStarsEnergyCompany energyCompany, 
                                           YukonUserContext userContext) {
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        ListMultimap<LMHardwareClass, HardwareDto> hardwareMap = hardwareUiService.getHardwareMapForAccount(accountInfoFragment.getAccountId(), 
                                                                                                          accountInfoFragment.getEnergyCompanyId());
        
        modelMap.addAttribute("switches", hardwareMap.get(LMHardwareClass.SWITCH));
        modelMap.addAttribute("meters", hardwareMap.get(LMHardwareClass.METER));
        modelMap.addAttribute("thermostats", hardwareMap.get(LMHardwareClass.THERMOSTAT));
        if(hardwareMap.get(LMHardwareClass.THERMOSTAT).size() > 1) {
            modelMap.addAttribute("showSelectAll", Boolean.TRUE);
        }
        
        modelMap.addAttribute("switchClass", LMHardwareClass.SWITCH);
        modelMap.addAttribute("thermostatClass", LMHardwareClass.THERMOSTAT);
        modelMap.addAttribute("meterClass", LMHardwareClass.METER);
        
        String meterDesignation= rolePropertyDao.getPropertyStringValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, energyCompany.getUser());
        boolean starsMeters = meterDesignation.equalsIgnoreCase(StarsUtils.METER_BASE_DESIGNATION); 
        modelMap.addAttribute("starsMeters", starsMeters);
        
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, userContext.getYukonUser());
        modelMap.addAttribute("inventoryChecking", inventoryChecking);
    }
    
    private void setupHardwareEditModelMap(AccountInfoFragment accountInfoFragment, Integer inventoryId, ModelMap modelMap, 
                                           YukonUserContext userContext, 
                                           boolean editing){
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        String defaultRoute;
        try {
            /* Apperently 'getDefaultRouteID' is not a simple getter and relies heavily on deep magic. */
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteId());
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        modelMap.addAttribute("defaultRoute", defaultRoute);
        
        LiteYukonPAObject[] routes = energyCompany.getAllRoutes();
        modelMap.addAttribute("routes", routes);
        
        
        modelMap.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyId()));
        
        
        if(editing){
            if(inventoryId != null) {
                modelMap.addAttribute("inventoryId", inventoryId);
                
                /* Device Status History */
                List<EventInventory> deviceStatusHistory = EventInventory.retrieveEventInventories(inventoryId);
                modelMap.addAttribute("deviceStatusHistory", deviceStatusHistory);
                
                /* Hardware History */
                modelMap.addAttribute("hardwareHistory", hardwareUiService.getHardwareHistory(inventoryId));
            }
        
            List<Warehouse> warehouses = energyCompany.getWarehouses();
            modelMap.addAttribute("warehouses", warehouses);
            boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, userContext.getYukonUser());
            modelMap.addAttribute("inventoryChecking", inventoryChecking);
        }
    }
    
    /* DEVICE TYPE SELECT OPTIONS WRAPPER */
    public static class DeviceTypeOpiton {
        private int hardwareTypeEntryId;
        private String displayName;
        
        public int getHardwareTypeEntryId() {
            return hardwareTypeEntryId;
        }
        public void setHardwareTypeEntryId(int hardwareTypeEntryId) {
            this.hardwareTypeEntryId = hardwareTypeEntryId;
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }

    @Autowired
    public void setHardwareUiService(HardwareUiService hardwareUiService) {
        this.hardwareUiService = hardwareUiService;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setServiceCompanyDao(ServiceCompanyDao serviceCompanyDao) {
        this.serviceCompanyDao = serviceCompanyDao;
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setHardwareDtoValidator(HardwareDtoValidator hardwareDtoValidator) {
        this.hardwareDtoValidator = hardwareDtoValidator;
    }
    
    @Autowired
    public void setSerialNumberValidator(SerialNumberValidator serialNumberValidator) {
        this.serialNumberValidator = serialNumberValidator;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
    @Autowired
    public void setHardwareService(HardwareService hardwareService) {
		this.hardwareService = hardwareService;
	}
}