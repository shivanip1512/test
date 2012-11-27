package com.cannontech.web.stars.dr.operator.hardware;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.roles.yukon.EnergyCompanyRole.MeteringType;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.event.EventInventory;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.digi.model.ZigbeeDeviceDto;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.HardwareModelHelper;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.InventoryCheckingAddDto;
import com.cannontech.web.stars.dr.operator.hardware.model.SerialNumber;
import com.cannontech.web.stars.dr.operator.hardware.service.ZigbeeDeviceService;
import com.cannontech.web.stars.dr.operator.hardware.validator.SerialNumberValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.util.SessionUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RequestMapping("/operator/hardware/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class OperatorHardwareController {
    
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private AddressDao addressDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;
    @Autowired private SerialNumberValidator serialNumberValidator;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ContactDao contactDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private HardwareService hardwareService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private ZigbeeDeviceService zigbeeDeviceService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private HardwareModelHelper helper;
    @Autowired private OperatorAccountService operatorAccountService;

    /* HARDWARE LIST PAGE */
    @RequestMapping
    public String list(YukonUserContext userContext, ModelMap model, AccountInfoFragment fragment) 
    throws ServletRequestBindingException {
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        model.addAttribute("serialNumberSwitch", new SerialNumber());
        model.addAttribute("serialNumberThermostat", new SerialNumber());
        model.addAttribute("serialNumberGateway", new SerialNumber());
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        setupListModel(fragment, model, energyCompany, userContext);
        return "operator/hardware/hardwareList.jsp";
    }

    @RequestMapping
    public String checkSerialNumberSwitch(@ModelAttribute("serialNumberSwitch") SerialNumber serialNumber, BindingResult bindingResult,
                                          ModelMap model, 
                                          YukonUserContext context,
                                          HttpServletRequest request,
                                          FlashScope flashScope,
                                          AccountInfoFragment fragment,
                                          int hardwareTypeId) {
        model.addAttribute("serialNumberThermostat", new SerialNumber());
        model.addAttribute("serialNumberGateway", new SerialNumber());
        return checkSerialNumber(serialNumber, bindingResult, model, context, request, 
                                 flashScope, fragment, hardwareTypeId);
    }
    
    @RequestMapping
    public String checkSerialNumberThermostat(@ModelAttribute("serialNumberThermostat") SerialNumber serialNumber, BindingResult bindingResult,
                                          ModelMap model, 
                                          YukonUserContext context,
                                          HttpServletRequest request,
                                          FlashScope flashScope,
                                          AccountInfoFragment fragment,
                                          int hardwareTypeId) {
        model.addAttribute("serialNumberSwitch", new SerialNumber());
        model.addAttribute("serialNumberGateway", new SerialNumber());
        return checkSerialNumber(serialNumber, bindingResult, model, context, request, 
                                 flashScope, fragment, hardwareTypeId);
    }
    
    @RequestMapping
    public String checkSerialNumberGateway(@ModelAttribute("serialNumberGateway") SerialNumber serialNumber, BindingResult bindingResult,
                                          ModelMap model, 
                                          YukonUserContext context,
                                          HttpServletRequest request,
                                          FlashScope flashScope,
                                          AccountInfoFragment fragment,
                                          int hardwareTypeId) {
        model.addAttribute("serialNumberSwitch", new SerialNumber());
        model.addAttribute("serialNumberThermostat", new SerialNumber());
        return checkSerialNumber(serialNumber, bindingResult, model, context, request, 
                                 flashScope, fragment, hardwareTypeId);
    }
    
    private String checkSerialNumber(SerialNumber serialNumber, BindingResult bindingResult,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment fragment,
                                 int hardwareTypeId) {
        
        serialNumberValidator.validate(serialNumber, bindingResult);
        YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            if (type.getHardwareClass().equals(HardwareClass.SWITCH)) {
                model.addAttribute("showSwitchCheckingPopup", true);
            } else if (type.getHardwareClass().equals(HardwareClass.THERMOSTAT)) {
                model.addAttribute("showThermostatCheckingPopup", true);
            } else {
                model.addAttribute("showGatewayCheckingPopup", true);
            }
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
            setupListModel(fragment, model, energyCompany, context);
            return "operator/hardware/hardwareList.jsp"; 
        }
        
        try {
            LiteLmHardwareBase possibleDuplicate = starsSearchDao.searchLmHardwareBySerialNumber(serialNumber.getSerialNumber(), fragment.getEnergyCompanyId());

            InventoryCheckingAddDto inventoryCheckingAddDto = new InventoryCheckingAddDto(serialNumber.getSerialNumber());
            inventoryCheckingAddDto.setHardwareTypeId(hardwareTypeId);
            inventoryCheckingAddDto.setHardwareType(type);
            
            if (possibleDuplicate != null) {
                inventoryCheckingAddDto.setInventoryId(possibleDuplicate.getInventoryID());
                inventoryCheckingAddDto.setSerialNumber(serialNumber.getSerialNumber());
                
                inventoryCheckingAddDto.setDeviceType(yukonListDao.getYukonListEntry(possibleDuplicate.getLmHardwareTypeID()).getEntryText());

                if (possibleDuplicate.getAccountID() > CtiUtilities.NONE_ZERO_ID) {
                    if (possibleDuplicate.getAccountID() == fragment.getAccountId()) {
                        /* Return to the list page with the 'Alread on this account' popup. */
                        model.addAttribute("sameAccountSerial", serialNumber.getSerialNumber());
                    } else {
                        /* Return to the list page with the confirm steal from account popup. */
                        model.addAttribute("confirmAccountSerial", serialNumber.getSerialNumber());
                        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
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
                    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
                    String defaultWarehouse = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.serialNumber.defaultWarehouse");
                    inventoryCheckingAddDto.setWarehouse(warehouse == null ? defaultWarehouse : warehouse.getWarehouseName());
                }
                
            } else {
                
                boolean invCheckingCreate = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING_CREATE, context.getYukonUser());
                if (invCheckingCreate) {
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
        
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        setupListModel(fragment, model, energyCompany, context);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        return "operator/hardware/hardwareList.jsp";
    }
    
    /* HARDWARE CREATE PAGE*/
    @RequestMapping
    public String createPage(YukonUserContext context, 
                             ModelMap model, 
                             AccountInfoFragment fragment, 
                             int hardwareTypeId, 
                             String serialNumber) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, context.getYukonUser());
        
        Hardware hardware = new Hardware();
        hardware.setAccountId(fragment.getAccountId());
        hardware.setHardwareTypeEntryId(hardwareTypeId);
        hardware.setFieldInstallDate(new Date());
        YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
        hardware.setHardwareType(type);
        hardware.setDisplayType(entry.getEntryText());

        setupCreateModel(fragment, model, type, context);
        
        
        if (StringUtils.isNotBlank(serialNumber)) {
            hardware.setSerialNumber(serialNumber);
        }
        
        model.addAttribute("hardware", hardware);
        
        return "operator/hardware/hardware.jsp";
    }
    
    /* HARDWARE VIEW PAGE*/
    @RequestMapping
    public String view(ModelMap model, 
                       YukonUserContext context, 
                       AccountInfoFragment fragment, 
                       int inventoryId) {
               
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        
        model.addAttribute("hardware", hardware);
        
        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("mode", PageEditMode.VIEW);
        
        setupHardwareViewEditModel(fragment, hardware, model, context);
        
        if(hardware.getHardwareType() == HardwareType.NON_YUKON_METER){
            return "redirect:/stars/operator/hardware/mp/view";
        }
        return "operator/hardware/hardware.jsp";
    }
    
    /* HARDWARE EDIT PAGE*/
    @RequestMapping
    public String edit(HttpServletRequest request, ModelMap model, YukonUserContext context, AccountInfoFragment fragment, int inventoryId) {
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        
        model.addAttribute("hardware", hardware);
        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("mode", PageEditMode.EDIT);
        
        setupHardwareViewEditModel(fragment, hardware, model, context);
        
        return "operator/hardware/hardware.jsp";
    }
    
    @RequestMapping(value="update", params="cancel")
    public String cancel(ModelMap model, AccountInfoFragment fragment, int inventoryId) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }
    
    @RequestMapping(value="update", params="save")
    public String update(@ModelAttribute Hardware hardware, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash,
                                 int inventoryId) throws ServletRequestBindingException {
        AccountInfoFragment fragment = operatorAccountService.getAccountInfoFragment(hardware.getAccountId());
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        LiteYukonUser user = context.getYukonUser();
        
        helper.updateAttempted(hardware, user, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, result);
        
        if (result.hasErrors()) {
            return returnToEditWithErrors(context, model, fragment, flash, hardware, result);
        }
        
        boolean statusChange = false;
        
        try {
            statusChange = hardwareUiService.updateHardware(user, hardware);
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (Lcr3102YukonDeviceCreationException e) {
            switch (e.getType()) {
            case UNKNOWN:
                result.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unknown");
                break;
            case REQUIRED:
                result.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.required");
                break;
            case UNAVAILABLE:
                result.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unavailable");
                break;
            case NON_NUMERIC:
                result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.nonNumericSerialNumber");
                break;
            }
        }
        if (result.hasErrors()) {
            return returnToEditWithErrors(context, model, fragment, flash, hardware, result);
        }
        
        /* If the device status was changed, spawn an event for it */
        if (statusChange) {
            int userId = SessionUtil.getParentLoginUserId(request.getSession(), user.getUserID());
            EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inventoryId);
        }
        
        /* Flash hardware updated */
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("inventoryId", inventoryId);
        
        return "redirect:view";
    }
    
    private String returnToEditWithErrors(YukonUserContext context, 
                                          ModelMap model, 
                                          AccountInfoFragment fragment, 
                                          FlashScope flash, 
                                          Hardware hardware, 
                                          BindingResult result) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        /* Return back to the jsp with the errors */
        setupHardwareViewEditModel(fragment, hardware, model, context);
        model.addAttribute("displayName", hardware.getDisplayName());
        
        /* Add errors to flash scope */
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        
        return "operator/hardware/hardware.jsp";
    }
    
    @RequestMapping
    public String create(@ModelAttribute("hardware") Hardware hardware, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash) throws ServletRequestBindingException {
        
        AccountInfoFragment fragment = operatorAccountService.getAccountInfoFragment(hardware.getAccountId());
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        int accountId = fragment.getAccountId();
        
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            model.clear();
            model.addAttribute("accountId", accountId);
            return "redirect:list";
        }
        
        LiteYukonUser user = context.getYukonUser();
        Set<YukonRoleProperty> verifyProperties = Sets.newHashSet(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                                                  YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE);
        helper.creationAttempted(user, fragment.getAccountNumber(), hardware, verifyProperties, result);

        if (!result.hasErrors()) {
            int inventoryId = helper.create(user, hardware, result, request.getSession());
            
            if (result.hasErrors()) {
                return returnToCreateWithErrors(model, hardware, context, flash, fragment, result);
            }
            
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            model.addAttribute("inventoryId", inventoryId);
            return "redirect:view";
            
        } else {
            return returnToCreateWithErrors(model, hardware, context, flash, fragment, result);
        }
    }
    
    private String returnToCreateWithErrors(ModelMap model, 
                                            Hardware hardware, 
                                            YukonUserContext context, 
                                            FlashScope flash, 
                                            AccountInfoFragment fragment, 
                                            BindingResult result) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        setupCreateModel(fragment, model, hardware.getHardwareType(), context);
        return "operator/hardware/hardware.jsp";
    }
    
    @RequestMapping
    public String delete(ModelMap model, 
                         YukonUserContext context, 
                         HttpServletRequest request, 
                         FlashScope flashScope, 
                         AccountInfoFragment fragment,
                         int inventoryId, 
                         String deleteOption) 
    throws NotAuthorizedException, NotFoundException, CommandCompletionException, SQLException, PersistenceException, WebClientException {
        
        Hardware hardwareToDelete = hardwareUiService.getHardware(inventoryId);
        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareDeletionAttemptedByOperator(user, hardwareToDelete.getDisplayName());
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        
        /* Delete this hardware or just take it off the account and put in back in the warehouse */
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        hardwareService.deleteHardware(user, delete, inventoryId);
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        if (delete) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        } else {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareRemoved"));
        }
        return "redirect:list";
    }
    
    @RequestMapping
    public String serviceCompanyInfo(ModelMap model, 
                                     YukonUserContext context, 
                                     int serviceCompanyId) 
    throws ServletRequestBindingException {
        
        ServiceCompanyDto serviceCompanyDto = serviceCompanyDao.getCompanyById(serviceCompanyId);
        LiteAddress serviceCompanyAddress = addressDao.getByAddressId(serviceCompanyDto.getAddress().getAddressID());
        Address address = Address.getDisplayableAddress(serviceCompanyAddress);
        
        model.addAttribute("companyName", serviceCompanyDto.getCompanyName());
        model.addAttribute("address", address);
        model.addAttribute("mainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());
        
        return "operator/hardware/serviceCompanyInfo.jsp";
    }
    
    @RequestMapping
    public String addDeviceToAccount(ModelMap model, 
                                     YukonUserContext context, 
                                     AccountInfoFragment fragment, 
                                     FlashScope flashScope, 
                                     boolean fromAccount, 
                                     int inventoryId) {
        
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.assigningHardwareAttemptedByOperator(context.getYukonUser(),
                                                                     fragment.getAccountNumber(),
                                                                     lmHardwareBase.getManufacturerSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        LiteInventoryBase liteInventoryBase = inventoryBaseDao.getByInventoryId(inventoryId);
        
        hardwareUiService.addDeviceToAccount(liteInventoryBase, fragment.getAccountId(), fromAccount, energyCompany, context.getYukonUser());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:list";
    }
    
    @RequestMapping
    public String addMeter(ModelMap model, 
                           YukonUserContext context, 
                           AccountInfoFragment fragment, 
                           FlashScope flashScope, 
                           int meterId) {
        
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meterId);
        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareMeterCreationAttemptedByOperator(user, liteYukonPAO.getPaoName());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        try {
            hardwareUiService.addYukonMeter(meterId, fragment.getAccountId(), user);
        } catch (ObjectInOtherEnergyCompanyException e) {/* Ignore */}
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:list";
    }

    @RequestMapping
    public String changeOut(ModelMap model, 
                            YukonUserContext context, 
                            AccountInfoFragment fragment, 
                            FlashScope flashScope, 
                            int newInventoryId, 
                            int oldInventoryId, 
                            boolean isMeter,
                            String redirect) {

        // Log change out attempt
        Hardware oldInventory = hardwareUiService.getHardware(oldInventoryId);
        LiteYukonUser user = context.getYukonUser();
        
        InventoryIdentifier newInventoryIdentifier = inventoryDao.getYukonInventory(newInventoryId);
        
        if (isMeter) {
            LiteYukonPAObject oldLiteYukonPAO = paoDao.getLiteYukonPAO(oldInventory.getDeviceId());
            LiteYukonPAObject newLiteYukonPAO = paoDao.getLiteYukonPAO(newInventoryId);
            hardwareEventLogService.hardwareChangeOutForMeterAttemptedByOperator(user,
                                                                                 oldLiteYukonPAO.getPaoName(),
                                                                                 newLiteYukonPAO.getPaoName());
        } else {
            String newInventorySN = lmHardwareBaseDao.getSerialNumberForInventoryId(newInventoryId);
            String oldInventorySN = oldInventory.getSerialNumber();
            hardwareEventLogService.hardwareChangeOutAttemptedByOperator(user, oldInventorySN, newInventorySN);
        }

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        hardwareUiService.changeOutInventory(oldInventoryId, newInventoryId, user, isMeter);
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        HardwareType type = newInventoryIdentifier.getHardwareType();
        String changeOutMessage;
        FlashScopeMessageType messageType;
        if (type.isGateway()) {
            changeOutMessage = "yukon.web.modules.operator.hardware.hardwareChangeOut.gateway";
            messageType = FlashScopeMessageType.WARNING;
        } else if (type.isThermostat() && type.isZigbee()) {
            changeOutMessage = "yukon.web.modules.operator.hardware.hardwareChangeOut.zigbeeDevice";
            messageType = FlashScopeMessageType.WARNING;
        } else {
            changeOutMessage = "yukon.web.modules.operator.hardware.hardwareChangeOut";
            messageType = FlashScopeMessageType.CONFIRM;
        }

        MessageSourceResolvable message = new YukonMessageSourceResolvable(changeOutMessage);
        flashScope.setMessage(message, messageType);
        
        if (redirect.equalsIgnoreCase("list")) {
            return "redirect:list";
        } else {
            model.addAttribute("inventoryId", newInventoryId);
            return "redirect:view";
        }
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
    
    /* Add common Attributes to model for view/edit and create modes both need for the hardware page */
    /* Should only be called by setupHardwareCreateModelMap and setupHardwareViewEditModelMap methods */
    private void setupCommonModelAttributes(HardwareType type, 
                                            AccountInfoFragment fragment, 
                                            ModelMap model, 
                                            YukonUserContext context) {
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING.name());
        
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        model.addAttribute("none", accessor.getMessage("yukon.web.defaults.none"));
        
        String defaultRoute;
        try {
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteId());
            defaultRoute = accessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = accessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        List<LiteYukonPAObject> routes = energyCompany.getAllRoutes();
        model.addAttribute("routes", routes);
        
        model.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyId()));
        
        /* Setup elements to hide/show based on device type/class */
        HardwareClass clazz = type.getHardwareClass();
        model.addAttribute("displayTypeKey", ".displayType." + clazz);

        if (type.isZigbee()) {
            model.addAttribute("showMacAddress", true);
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
        }
        
        boolean showVoltage = !type.isZigbee() && !clazz.isGateway() && !clazz.isThermostat();
        model.addAttribute("showVoltage", showVoltage);
        
        /* Hide route for meters and zigbee devices */
        if (!clazz.isMeter() && !type.isZigbee() && !type.isRf() && type != HardwareType.LCR_3102) {
            model.addAttribute("showRoute", true);
        }
        
        /* Show two way device row for non-zigbee two way lcr's */
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }
        
        model.addAttribute("showInstallNotes", true);
        
    }
    
    private void setupCreateModel(AccountInfoFragment fragment, 
                                  ModelMap model, 
                                  HardwareType type, 
                                  YukonUserContext context) {
        
        HardwareClass clazz = type.getHardwareClass();
        setupCommonModelAttributes(type, fragment, model, context);
        
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
            model.addAttribute("serialNumberEditable", true);
        }
    
    }
    
    private void setupHardwareViewEditModel(AccountInfoFragment fragment, 
                                    Hardware hardware, 
                                    ModelMap model, 
                                    YukonUserContext context) {
        
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        boolean tstatAccess = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT, context.getYukonUser());
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        model.addAttribute("allowAccountEditing", allowAccountEditing);
        model.addAttribute("inventoryChecking", inventoryChecking);
        
        HardwareType type = hardware.getHardwareType();
        HardwareClass clazz = type.getHardwareClass();
        
        setupCommonModelAttributes(type, fragment, model, context);
        
        int accountId = fragment.getAccountId();
        int inventoryId = hardware.getInventoryId();
        model.addAttribute("inventoryId", inventoryId);
        
        /*  Add Pao Specifics */
        Integer deviceId = hardware.getDeviceId();
        if (deviceId != null && deviceId > 0 && !type.isZigbee()) {  // points for ZigBee device have their own special box
            if (type.isRf()) { // Only showing points list for rf devices for now
                List<LitePoint> points = pointDao.getLitePointsByPaObjectId(deviceId);
                if (!points.isEmpty()) {
                    ListMultimap<PointType, LitePoint> pointsMap = ArrayListMultimap.create();
                    for (LitePoint point : points) {
                        pointsMap.put(point.getPointTypeEnum(), point);
                    }
                    for (PointType pointTypeKey : pointsMap.keySet()) {
                        Collections.sort(pointsMap.get(pointTypeKey), new Comparator<LitePoint>() {
                            @Override
                            public int compare(LitePoint o1, LitePoint o2) {
                                return o1.getPointName().compareTo(o2.getPointName());
                            }
                        });
                    }
                    model.addAttribute("showPoints", true);
                    model.addAttribute("points", pointsMap.asMap());
                }
            }
        }

        /* Device Status History */
        List<EventInventory> deviceStatusHistory = EventInventory.retrieveEventInventories(inventoryId);
        model.addAttribute("deviceStatusHistory", deviceStatusHistory);
        
        /* Hardware History */
        model.addAttribute("hardwareHistory", hardwareUiService.getHardwareHistory(inventoryId));
        
        /* Warehouses */
        LiteStarsEnergyCompany ec = starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        List<Warehouse> warehouses = ec.getWarehouses();
        model.addAttribute("warehouses", warehouses);

        /* For switches and tstats, if they have inventory checking turned off they can edit the serial number. */
        if (!inventoryChecking && !clazz.isMeter()) {
            /* Rf devices can not have their serial number edited here...yet. */
            if (!type.isRf()) {
                model.addAttribute("serialNumberEditable", true);
            }
        }
        
        /* For switches and tstats, show serial number instead of device name */
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
        }
        
        /* If ZigBee device, show Commissioned/Decommissioned State */
        if (type.isZigbee()) {
            model.addAttribute("showZigbeeState", true);
        }
        
        /* Actions to show */
        switch (clazz) {
        
        case SWITCH:
            model.addAttribute("showSwitchAndTstatConfigAction", true);
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showSwitchChangeoutAction", true);
                }
            }
            break;

        case THERMOSTAT:
            model.addAttribute("showSwitchAndTstatConfigAction", true);
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showTstatChangeoutAction", true);
                }
            }
            
            if (tstatAccess) {
                if (type.isSupportsSchedules()) {
                    model.addAttribute("showScheduleActions", true);
                }
                if (type.isSupportsManualAdjustment()) {
                    model.addAttribute("showManualAction", true);
                }
                model.addAttribute("showThermostatHistoryAction", true);
            }
            break;
            
        case METER:
            model.addAttribute("showDeviceName", true);
            if (type == HardwareType.YUKON_METER) {
                model.addAttribute("showMeterConfigAction", true);
                if (rolePropertyDao.checkRole(YukonRole.METERING, context.getYukonUser())) {
                    model.addAttribute("showMeterDetailAction", true);
                }
            }
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showMeterChangeoutAction", true);
                }
            }
            break;
            
        case GATEWAY:
            model.addAttribute("showCommissionActions", true);
            model.addAttribute("showTextMessageAction", true);
            model.addAttribute("showAssignedDevices", true);
            
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showGatewayChangeOutAction", true);
                }
            }
            
            List<Pair<InventoryIdentifier, ZigbeeDeviceDto>> zigbeeDevices = zigbeeDeviceService.buildZigbeeDeviceDtoList(accountId);
            List<ZigbeeDeviceDto> assignedDevices = Lists.newArrayList();
            List<ZigbeeDeviceDto> availableDevices = Lists.newArrayList();
            for (Pair<InventoryIdentifier, ZigbeeDeviceDto> pair : zigbeeDevices) {
                if (pair.first == null) {
                    availableDevices.add(pair.second);
                } else if (pair.first != null && pair.first.getInventoryId() == inventoryId) {
                    assignedDevices.add(pair.second);
                }
            }
            model.addAttribute("availableDevices", availableDevices);
            model.addAttribute("assignedDevices", assignedDevices);
            
            break;
        }
        
        if (type.isZigbee() && !type.isGateway()) {
            InventoryIdentifier gateway = gatewayDeviceDao.findGatewayByDeviceMapping(deviceId);
            if (gateway == null) {
                model.addAttribute("showDisabledCommissionActions", true);
                model.addAttribute("showDisabledRefresh", true);
            } else {
                model.addAttribute("showCommissionActions", true);
                model.addAttribute("gatewayInventoryId", gateway.getInventoryId());
            }

            model.addAttribute("showGateway", true);
            List<DigiGateway> gateways = gatewayDeviceDao.getGatewaysForAccount(accountId);
            model.addAttribute("gateways", gateways);
            
        }
        
    }
    
    private void setupListModel(AccountInfoFragment fragment, 
                                ModelMap model, 
                                YukonEnergyCompany ec, 
                                YukonUserContext context) {
        LiteStarsEnergyCompany liteEnergyCompany = starsDatabaseCache.getEnergyCompany(ec);
        
        /* Add device types for dropdown menus */
        ListMultimap<String, DeviceTypeOption> deviceTypeMap = ArrayListMultimap.create();
        List<YukonListEntry> deviceTypeList = liteEnergyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE).getYukonListEntries();
        
        for (YukonListEntry deviceTypeEntry : deviceTypeList) {
            HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
            DeviceTypeOption option = new DeviceTypeOption();
            option.setDisplayName(deviceTypeEntry.getEntryText());
            option.setHardwareTypeEntryId(deviceTypeEntry.getEntryID());
            deviceTypeMap.put(type.getHardwareClass().name(), option);
        }
        model.addAttribute("deviceTypeMap", deviceTypeMap.asMap());
        
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        ListMultimap<HardwareClass, Hardware> hardwareMap = hardwareUiService.getHardwareMapForAccount(fragment.getAccountId());
        
        model.addAttribute("switches", hardwareMap.get(HardwareClass.SWITCH));
        model.addAttribute("meters", hardwareMap.get(HardwareClass.METER));
        model.addAttribute("thermostats", hardwareMap.get(HardwareClass.THERMOSTAT));
        model.addAttribute("gateways", hardwareMap.get(HardwareClass.GATEWAY));
        Iterable<Hardware> schedualableThermostats = Iterables.filter(hardwareMap.get(HardwareClass.THERMOSTAT), new Predicate<Hardware>() {
            @Override
            public boolean apply(Hardware dto) {
                return dto.getHardwareType().isSupportsSchedules();
            }
        });
        if (Lists.newArrayList(schedualableThermostats).size() > 1) {
            model.addAttribute("showSelectAll", Boolean.TRUE);
        }
        
        model.addAttribute("switchClass", HardwareClass.SWITCH);
        model.addAttribute("thermostatClass", HardwareClass.THERMOSTAT);
        model.addAttribute("meterClass", HardwareClass.METER);
        model.addAttribute("gatewayClass", HardwareClass.GATEWAY);
        
        MeteringType meterDesignation= ecRolePropertyDao.getPropertyEnumValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, EnergyCompanyRole.MeteringType.class,  ec);
        boolean starsMeters = meterDesignation == MeteringType.stars; 
        model.addAttribute("starsMeters", starsMeters);
        
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        model.addAttribute("inventoryChecking", inventoryChecking);
    }
    
    /* DEVICE TYPE SELECT OPTIONS WRAPPER */
    public static class DeviceTypeOption {
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
    
}