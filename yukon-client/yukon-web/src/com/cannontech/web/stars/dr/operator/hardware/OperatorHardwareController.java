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
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.SwitchAssignment;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
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
import com.cannontech.web.stars.dr.operator.hardware.model.InventoryCheckingAddDto;
import com.cannontech.web.stars.dr.operator.hardware.model.SerialNumber;
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
    private YukonEnergyCompanyService yukonEnergyCompanyService;
        
    /* HARDWARE LIST PAGE */
    @RequestMapping
    public String hardwareList(YukonUserContext userContext, ModelMap model, AccountInfoFragment fragment) throws ServletRequestBindingException {
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        SerialNumber serialNumber = new SerialNumber();
        model.addAttribute("serialNumber", serialNumber);
        
        setupHardwareListModelMap(fragment, model, energyCompany, userContext);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        return "operator/hardware/hardwareList.jsp";
    }
    
    @RequestMapping
    public String checkSerialNumber(@ModelAttribute("serialNumber") SerialNumber serialNumber, BindingResult bindingResult,
                                 ModelMap model, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment fragment,
                                 String hardwareClass) {
        
        LMHardwareClass lmHardwareClass = LMHardwareClass.valueOf(hardwareClass);
        serialNumberValidator.validate(serialNumber, bindingResult);
        
        if(bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            if(lmHardwareClass.equals(LMHardwareClass.SWITCH)) {
                model.addAttribute("showSwitchCheckingPopup", true);
            } else if(lmHardwareClass.equals(LMHardwareClass.THERMOSTAT)) {
                model.addAttribute("showThermostatCheckingPopup", true);
            } else {
                model.addAttribute("showGatewayCheckingPopup", true);
            }
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            return "redirect:hardwareList"; 
        }
        
        try {
            LiteStarsLMHardware possibleDuplicate = (LiteStarsLMHardware)starsSearchDao.searchLMHardwareBySerialNumber(serialNumber.getSerialNumber(), fragment.getEnergyCompanyId());

            InventoryCheckingAddDto inventoryCheckingAddDto = new InventoryCheckingAddDto(serialNumber.getSerialNumber());
            inventoryCheckingAddDto.setIsSwitch(lmHardwareClass.equals(LMHardwareClass.SWITCH));
            inventoryCheckingAddDto.setGateway(lmHardwareClass.equals(LMHardwareClass.GATEWAY));
            
            if(possibleDuplicate != null) {
                inventoryCheckingAddDto.setInventoryId(possibleDuplicate.getInventoryID());
                inventoryCheckingAddDto.setSerialNumber(serialNumber.getSerialNumber());
                
                String type = yukonListDao.getYukonListEntry(possibleDuplicate.getLmHardwareTypeID()).getEntryText();
                inventoryCheckingAddDto.setDeviceType(type);

                if(possibleDuplicate.getAccountID() > CtiUtilities.NONE_ZERO_ID) {
                    if(possibleDuplicate.getAccountID() == fragment.getAccountId()) {
                        /* Return to the list page with the 'Alread on this account' popup. */
                        model.addAttribute("sameAccountSerial", serialNumber.getSerialNumber());
                    } else {
                        /* Return to the list page with the confirm steal from account popup. */
                        model.addAttribute("confirmAccountSerial", serialNumber.getSerialNumber());
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
                    model.addAttribute("confirmWarehouseSerial", serialNumber.getSerialNumber());
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
                    model.addAttribute("confirmCreateSerial", serialNumber.getSerialNumber());
                } else {
                    /* Return to the list page with the SN unavailable popup. */
                    model.addAttribute("notFoundSerial", serialNumber.getSerialNumber());
                }
                
            }

            model.addAttribute("checkingAdd", inventoryCheckingAddDto);
            
        } catch (ObjectInOtherEnergyCompanyException e) {
            /* Return to the list page with the hardware found in another ec popup. */
            model.addAttribute("anotherECSerial", serialNumber.getSerialNumber());
            model.addAttribute("anotherEC", StringEscapeUtils.escapeHtml(e.getYukonEnergyCompany().getName()));
        }
        
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        setupHardwareListModelMap(fragment, model, energyCompany, userContext);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        return "operator/hardware/hardwareList.jsp";
    }
    
    /* HARDWARE CREATE PAGE*/
    @RequestMapping
    public String hardwareCreate(YukonUserContext context, ModelMap model, AccountInfoFragment fragment, String hardwareClass, String serialNumber) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, context.getYukonUser());
        
        LMHardwareClass lmHardwareClass = LMHardwareClass.valueOf(hardwareClass.toUpperCase());
        setupHardwareCreateModelMap(fragment, model, lmHardwareClass, context);
        
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setHardwareClass(lmHardwareClass);
        hardwareDto.setFieldInstallDate(new Date());
        
        if(StringUtils.isNotBlank(serialNumber)) {
            hardwareDto.setSerialNumber(serialNumber);
        }
        
        model.addAttribute("hardwareDto", hardwareDto);
        
        return "operator/hardware/hardware.jsp";
    }
    
    /* HARDWARE EDIT PAGE*/
    @RequestMapping
    public String hardwareEdit(HttpServletRequest request, ModelMap model, YukonUserContext userContext,  AccountInfoFragment fragment, int inventoryId) {
        /* Page Edit Mode */
        setPageMode(model, userContext);
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        
        /* Set two way device name when none has been chosen yet for two way switches */
        if(hardwareDto.getHardwareType().isSwitch() && hardwareDto.getHardwareType().isTwoWay() &&hardwareDto.getDeviceId() <= 0) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext); 
            hardwareDto.setTwoWayDeviceName(messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.noTwoWayDeviceName"));
        }
        
        model.addAttribute("hardwareDto", hardwareDto);
        model.addAttribute("displayName", hardwareDto.getDisplayName());
        
        setupHardwareEditModelMap(fragment, hardwareDto, model, userContext);
        
        return "operator/hardware/hardware.jsp";
    }
    
    @RequestMapping
    public String updateHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment fragment,
                                 int inventoryId) throws ServletRequestBindingException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Update */
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            return "redirect:hardwareList";
        }
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        
        boolean statusChange = false;
        
        /* Validate and Update*/
        try {
            hardwareEventLogService.hardwareUpdateAttemptedByOperator(context.getYukonUser(),
                                                                      hardwareDto.getSerialNumber());
            
            hardwareDtoValidator.validate(hardwareDto, bindingResult);
            
            if (!bindingResult.hasErrors()) {
                hardwareDto.setInventoryId(inventoryId);
                statusChange = hardwareUiService.updateHardware(context, hardwareDto);
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        }
        
        if (bindingResult.hasErrors()) {
            setupHardwareEditModelMap(fragment, hardwareDto, model, context);
            /* Return back to the jsp with the errors */
            setPageMode(model, context);
            model.addAttribute("displayName", hardwareDto.getDisplayName());
            
            /* Add errors to flash scope */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            if (hardwareDto.getHardwareType() == HardwareType.NON_YUKON_METER) {
                return "operator/hardware/meterProfile.jsp";
            } else {
                return "operator/hardware/hardware.jsp";
            }
        } 
        
        /* If the device status was changed, spawn an event for it */
        if (statusChange) {
            EventUtils.logSTARSEvent(context.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
        }
        
        /* Flash hardware updated */
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        return "redirect:hardwareList";
    }
    
    @RequestMapping
    public String createHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment fragment) throws ServletRequestBindingException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            return "redirect:hardwareList";
        }
        
        hardwareEventLogService.hardwareCreationAttemptedByOperator(context.getYukonUser(),
                                                                    fragment.getAccountNumber(),
                                                                    hardwareDto.getSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, context.getYukonUser());
        int inventoryId = -1;
        
        YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(hardwareDto.getHardwareTypeEntryId());
        hardwareDto.setHardwareType(HardwareType.valueOf(hardwareTypeEntry.getYukonDefID()));

        hardwareDtoValidator.validate(hardwareDto, bindingResult);

        if (!bindingResult.hasErrors()) {
            
            try {
                inventoryId = hardwareUiService.createHardware(hardwareDto, fragment.getAccountId(), context);
            } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
                bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
            } catch (ObjectInOtherEnergyCompanyException e) {
                bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
            }
            
            /* If the device status was set, spawn an event for it. */
            if (hardwareDto.getDeviceStatusEntryId() != null && hardwareDto.getDeviceStatusEntryId() != 0) {
                EventUtils.logSTARSEvent(context.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
            }

            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            return "redirect:hardwareList";
        } else {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            
            if (hardwareDto.getHardwareType() == HardwareType.NON_YUKON_METER) {
                model.addAttribute("mode", PageEditMode.CREATE);
                
                return "operator/hardware/meterProfile.jsp";
            } else {
                setupHardwareCreateModelMap(fragment, model, hardwareDto.getHardwareClass(), context);
                return "operator/hardware/hardware.jsp";
            }
        }
    }
    
    @RequestMapping
    public String deleteHardware(ModelMap model, YukonUserContext context, HttpServletRequest request, FlashScope flashScope, AccountInfoFragment fragment,
                                 int inventoryId, String deleteOption) throws Exception {
        HardwareDto hardwareToDelete = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        hardwareEventLogService.hardwareDeletionAttemptedByOperator(context.getYukonUser(), hardwareToDelete.getDisplayName());
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        /* Delete this hardware or just take it off the account and put in back in the warehouse */
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        hardwareService.deleteHardware(context, delete, inventoryId, fragment.getAccountId(), hardwareToDelete);
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        if(delete) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        } else {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareRemoved"));
        }
        return "redirect:hardwareList";
    }
    

    @RequestMapping
    public View createYukonDevice(ModelMap model, String deviceName, HttpServletResponse response, YukonUserContext context,
                                  int inventoryId) throws ServletRequestBindingException {
        
        hardwareEventLogService.twoWayHardwareCreationAttemptedByOperator(context.getYukonUser(), 
                                                                          deviceName);
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        int deviceId = -1;
        try {
            SimpleDevice yukonDevice = hardwareUiService.createTwoWayDevice(context, inventoryId, deviceName);
            deviceId = yukonDevice.getDeviceId();
        } catch (StarsTwoWayLcrYukonDeviceCreationException e) {
            model.addAttribute("errorOccurred", Boolean.TRUE);
            model.addAttribute("errorMsg", e.getMessage());
            return new JsonView();
        }
        model.addAttribute("errorOccurred", Boolean.FALSE);
        model.addAttribute("deviceId", deviceId);
        
        return new JsonView();
    }
    
    @RequestMapping
    public String serviceCompanyInfo(ModelMap model, YukonUserContext context, int serviceCompanyId) throws ServletRequestBindingException {
        ServiceCompanyDto serviceCompanyDto = serviceCompanyDao.getCompanyById(serviceCompanyId);
        LiteAddress serviceCompanyAddress = addressDao.getByAddressId(serviceCompanyDto.getAddress().getAddressID());
        Address address = Address.getDisplayableAddress(serviceCompanyAddress);
        
        model.addAttribute("companyName", serviceCompanyDto.getCompanyName());
        model.addAttribute("address", address);
        model.addAttribute("mainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());
        
        return "operator/hardware/serviceCompanyInfo.jsp";
    }
    
    @RequestMapping
    public String addDeviceToAccount(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, FlashScope flashScope, 
                                     boolean fromAccount, 
                                     int inventoryId) {
        
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.assigningHardwareAttemptedByOperator(context.getYukonUser(),
                                                                     fragment.getAccountNumber(),
                                                                     lmHardwareBase.getManufacturerSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
        
        hardwareUiService.addDeviceToAccount(liteInventoryBase, fragment.getAccountId(), fromAccount, energyCompany, context.getYukonUser());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:hardwareList";
    }
    
    @RequestMapping
    public String meterProfile(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, FlashScope flashScope, int inventoryId) {
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());

        /* Page Edit Mode */
        setPageMode(model, context);
        
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        
        model.addAttribute("hardwareDto", hardwareDto);
        model.addAttribute("displayName", hardwareDto.getDisplayName());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        return "operator/hardware/meterProfile.jsp";
    }
    
    @RequestMapping
    public String meterProfileCreate(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, FlashScope flashScope) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, context.getYukonUser());
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        
        model.addAttribute("mode", PageEditMode.CREATE);
        
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
        
        for(SwitchAssignment assignement : hardwareUiService.getSwitchAssignments(new ArrayList<Integer>(), fragment.getAccountId())) {
            hardwareDto.getSwitchAssignments().add(assignement);
        }

        model.addAttribute("hardwareDto", hardwareDto);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        return "operator/hardware/meterProfile.jsp";
    }
    
    @RequestMapping
    public String addMeter(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, FlashScope flashScope, int meterId) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meterId);
        hardwareEventLogService.hardwareMeterCreationAttemptedByOperator(context.getYukonUser(),
                                                                         liteYukonPAO.getPaoName());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        hardwareUiService.addYukonMeter(meterId, fragment.getAccountId(), context);
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:hardwareList";
    }

    @RequestMapping
    public String changeOut(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, 
                            FlashScope flashScope, int changeOutId, int oldInventoryId, boolean isMeter) {

        // Log change out attempt
        HardwareDto oldHardware = hardwareUiService.getHardwareDto(oldInventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        if(isMeter) {
            LiteYukonPAObject oldLiteYukonPAO = paoDao.getLiteYukonPAO(oldHardware.getDeviceId());
            LiteYukonPAObject newLiteYukonPAO = paoDao.getLiteYukonPAO(changeOutId);
            hardwareEventLogService.hardwareChangeOutForMeterAttemptedByOperator(context.getYukonUser(),
                                                                                 oldLiteYukonPAO.getPaoName(),
                                                                                 newLiteYukonPAO.getPaoName());
        } else {
            LMHardwareBase changeOutInventory = lmHardwareBaseDao.getById(changeOutId);
            hardwareEventLogService.hardwareChangeOutAttemptedByOperator(context.getYukonUser(), 
                                                                         oldHardware.getSerialNumber(), 
                                                                         changeOutInventory.getManufacturerSerialNumber());
        }

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        hardwareUiService.changeOutInventory(oldInventoryId, changeOutId, context, isMeter);
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
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
    
    /* Add common element to model for view/edit and create modes both need for the hardware page */
    /* Should only be called by setupHardwareCreateModelMap and setupHardwareEditModelMap methods */
    private void setupHardwareModelBasics(LMHardwareClass clazz, AccountInfoFragment fragment, ModelMap model, YukonUserContext context) {
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            /* Apparently 'getDefaultRouteID' is not a simple getter and relies heavily on deep magic. */
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteId());
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        LiteYukonPAObject[] routes = energyCompany.getAllRoutes();
        model.addAttribute("routes", routes);
        
        model.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyId()));
        
        /* Setup elements to hide/show based on device type */
        if (clazz.isGateway()) {
            /* Shows the MacAddress Field and Firmware Version */
            model.addAttribute("showMacAddress", true);
            model.addAttribute("showFirmwareVersion", true);
            model.addAttribute("showVoltage", false);
        }
        
        if (clazz.isThermostat()) {
            /* Shows install code */
            model.addAttribute("showInstallCode", true);
            model.addAttribute("showMacAddress", true);
            model.addAttribute("showVoltage", false);
        }
    }
    
    private void setupHardwareCreateModelMap(AccountInfoFragment fragment, ModelMap model, LMHardwareClass clazz, YukonUserContext context) {
        setupHardwareModelBasics(clazz, fragment, model, context);
        model.addAttribute("mode", PageEditMode.CREATE);
        
        List<DeviceTypeOpiton> deviceTypeOptions = Lists.newArrayList();
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        
        List<YukonListEntry> deviceTypeList = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE).getYukonListEntries();
        for(YukonListEntry deviceTypeEntry : deviceTypeList) {
            HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
            if(type.getLMHardwareClass() == clazz) {
                DeviceTypeOpiton option = new DeviceTypeOpiton();
                option.setDisplayName(deviceTypeEntry.getEntryText());
                option.setHardwareTypeEntryId(deviceTypeEntry.getEntryID());
                deviceTypeOptions.add(option);
            }
        }
        
        model.addAttribute("deviceTypes", deviceTypeOptions);
        
        if(clazz == LMHardwareClass.SWITCH) {
            model.addAttribute("displayTypeKey", ".switches.displayType");
        } else if (clazz == LMHardwareClass.THERMOSTAT) {
            model.addAttribute("displayTypeKey", ".thermostats.displayType");
            model.addAttribute("isThermostat", true);
        } else if (clazz == LMHardwareClass.GATEWAY) {
            model.addAttribute("displayTypeKey", ".gateways.displayType");
            model.addAttribute("isGateway", true);
        } else {
            model.addAttribute("displayTypeKey", ".meters.displayType");
        }
        
        if(clazz != LMHardwareClass.METER) {
            model.addAttribute("showSerialNumber", true);
            model.addAttribute("serialNumberEditable", true);
            
            if (clazz != LMHardwareClass.GATEWAY) {
                model.addAttribute("showRoute", true);    
            }
        }
    }
    
    private void setupHardwareEditModelMap(AccountInfoFragment fragment, HardwareDto dto, ModelMap model, YukonUserContext context) {
        setupHardwareModelBasics(dto.getHardwareType().getLMHardwareClass(), fragment, model, context);
        
        int inventoryId = dto.getInventoryId();
        model.addAttribute("inventoryId", inventoryId);
        
        /* Device Status History */
        List<EventInventory> deviceStatusHistory = EventInventory.retrieveEventInventories(inventoryId);
        model.addAttribute("deviceStatusHistory", deviceStatusHistory);
        
        /* Hardware History */
        model.addAttribute("hardwareHistory", hardwareUiService.getHardwareHistory(inventoryId));
        
        /* Warehouses */
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        List<Warehouse> warehouses = energyCompany.getWarehouses();
        model.addAttribute("warehouses", warehouses);
        
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        model.addAttribute("inventoryChecking", inventoryChecking);
        
        /* Setup elements to show or hide based on permissions and device type */
        HardwareType hardwareType = dto.getHardwareType();
        LMHardwareClass lmHardwareClass = hardwareType.getLMHardwareClass();

        /* For switches and tstats, if they have inventory checking turned off they can edit the serial number. */
        if (!inventoryChecking && lmHardwareClass != LMHardwareClass.METER) {
            model.addAttribute("serialNumberEditable", true);
        }
        
        /* For switches and tstats, show serial number instead of device name and show the route. */
        if (lmHardwareClass != LMHardwareClass.METER) {
            model.addAttribute("showSerialNumber", true);
            
            if (lmHardwareClass != LMHardwareClass.GATEWAY) {
                model.addAttribute("showRoute", true);    
            }
        }
        
        if (hardwareType.isSwitch() && hardwareType.isTwoWay()) {
            model.addAttribute("showTwoWay", true);
        }
    }
    
    private void setPageMode(ModelMap model, YukonUserContext context) {
     // pageEditMode
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        model.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
    }
    
    private void setupHardwareListModelMap(AccountInfoFragment fragment, ModelMap model, 
                                           YukonEnergyCompany energyCompany, 
                                           YukonUserContext context) {
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        ListMultimap<LMHardwareClass, HardwareDto> hardwareMap = hardwareUiService.getHardwareMapForAccount(fragment.getAccountId(), 
                                                                                                          fragment.getEnergyCompanyId());
        
        model.addAttribute("switches", hardwareMap.get(LMHardwareClass.SWITCH));
        model.addAttribute("meters", hardwareMap.get(LMHardwareClass.METER));
        model.addAttribute("thermostats", hardwareMap.get(LMHardwareClass.THERMOSTAT));
        model.addAttribute("gateways", hardwareMap.get(LMHardwareClass.GATEWAY));
        if(hardwareMap.get(LMHardwareClass.THERMOSTAT).size() > 1) {
            model.addAttribute("showSelectAll", Boolean.TRUE);
        }
        
        model.addAttribute("switchClass", LMHardwareClass.SWITCH);
        model.addAttribute("thermostatClass", LMHardwareClass.THERMOSTAT);
        model.addAttribute("meterClass", LMHardwareClass.METER);
        model.addAttribute("gatewayClass", LMHardwareClass.GATEWAY);
        
        String meterDesignation= rolePropertyDao.getPropertyStringValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, energyCompany.getEnergyCompanyUser());
        boolean starsMeters = meterDesignation.equalsIgnoreCase(StarsUtils.METER_BASE_DESIGNATION); 
        model.addAttribute("starsMeters", starsMeters);
        
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        model.addAttribute("inventoryChecking", inventoryChecking);
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
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }

}