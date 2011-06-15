package com.cannontech.web.stars.dr.operator.hardware;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
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
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
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
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.SwitchAssignment;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.thirdparty.digi.model.ZigbeeDeviceDto;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.InventoryCheckingAddDto;
import com.cannontech.web.stars.dr.operator.hardware.model.SerialNumber;
import com.cannontech.web.stars.dr.operator.hardware.service.ZigbeeDeviceService;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareDtoValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.SerialNumberValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
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
    private GatewayDeviceDao gatewayDeviceDao;
    
    private ZigbeeDeviceService zigbeeDeviceService;
    private InventoryDao inventoryDao;

    /* HARDWARE LIST PAGE */
    @RequestMapping
    public String list(YukonUserContext userContext, 
                       ModelMap model, 
                       AccountInfoFragment fragment) 
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
            LiteStarsLMHardware possibleDuplicate = (LiteStarsLMHardware) starsSearchDao.searchLMHardwareBySerialNumber(serialNumber.getSerialNumber(), fragment.getEnergyCompanyId());

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
        
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setHardwareTypeEntryId(hardwareTypeId);
        hardwareDto.setFieldInstallDate(new Date());
        YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
        hardwareDto.setHardwareType(type);
        hardwareDto.setDisplayType(entry.getEntryText());

        setupCreateModel(fragment, model, type, context);
        
        
        if (StringUtils.isNotBlank(serialNumber)) {
            hardwareDto.setSerialNumber(serialNumber);
        }
        
        model.addAttribute("hardwareDto", hardwareDto);
        
        return "operator/hardware/hardware.jsp";
    }
    
    /* HARDWARE VIEW PAGE*/
    @RequestMapping
    public String view(ModelMap model, 
                       YukonUserContext context, 
                       AccountInfoFragment fragment, 
                       int inventoryId) {
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        
        model.addAttribute("hardwareDto", hardwareDto);
        
        model.addAttribute("displayName", hardwareDto.getDisplayName());
        model.addAttribute("mode", PageEditMode.VIEW);
        
        setupHardwareViewEditModel(fragment, hardwareDto, model, context);
        
        return "operator/hardware/hardware.jsp";
    }
    
    /* HARDWARE EDIT PAGE*/
    @RequestMapping
    public String edit(HttpServletRequest request, 
                       ModelMap model, 
                       YukonUserContext context, 
                       AccountInfoFragment fragment, 
                       int inventoryId) {
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        
        model.addAttribute("hardwareDto", hardwareDto);
        model.addAttribute("displayName", hardwareDto.getDisplayName());
        model.addAttribute("mode", PageEditMode.EDIT);
        
        setupHardwareViewEditModel(fragment, hardwareDto, model, context);
        
        return "operator/hardware/hardware.jsp";
    }
    
    @RequestMapping(value="update", params="cancel")
    public String cancel(ModelMap model, AccountInfoFragment fragment, int inventoryId) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }
    
    @RequestMapping(value="update", params="save")
    public String update(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash,
                                 AccountInfoFragment fragment,
                                 int inventoryId) throws ServletRequestBindingException {

        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareUpdateAttemptedByOperator(user, hardwareDto.getSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        
        hardwareDto.setInventoryId(inventoryId);
        
        /* Validate and Update*/
        hardwareDtoValidator.validate(hardwareDto, result);
        if (result.hasErrors()) {
            return returnToEditWithErrors(context, model, fragment, flash, hardwareDto, result);
        }
        
        boolean statusChange = false;
        
        try {
            statusChange = hardwareUiService.updateHardware(user, hardwareDto);
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (StarsTwoWayLcrYukonDeviceCreationException e) {
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
            return returnToEditWithErrors(context, model, fragment, flash, hardwareDto, result);
        }
        
        /* If the device status was changed, spawn an event for it */
        if (statusChange) {
            EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
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
                                          HardwareDto dto, 
                                          BindingResult result) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        /* Return back to the jsp with the errors */
        setupHardwareViewEditModel(fragment, dto, model, context);
        model.addAttribute("displayName", dto.getDisplayName());
        
        /* Add errors to flash scope */
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        
        if (dto.getHardwareType() == HardwareType.NON_YUKON_METER) {
            return "operator/hardware/meterProfile.jsp";
        } else {
            return "operator/hardware/hardware.jsp";
        }
    }
    
    @RequestMapping
    public String create(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash,
                                 AccountInfoFragment fragment) throws ServletRequestBindingException {
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            int accountId = fragment.getAccountId();
            model.clear();
            model.addAttribute("accountId", accountId);
            return "redirect:list";
        }
        
        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareCreationAttemptedByOperator(user,
                                                                    fragment.getAccountNumber(),
                                                                    hardwareDto.getSerialNumber());
        
        if (hardwareDto.isCreatingNewTwoWayDevice()) {
            hardwareEventLogService.twoWayHardwareCreationAttemptedByOperator(user, hardwareDto.getTwoWayDeviceName());
        }
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, user);

        hardwareDtoValidator.validate(hardwareDto, result);

        if (!result.hasErrors()) {
            int inventoryId = -1;
            
            try {
                inventoryId = hardwareUiService.createHardware(hardwareDto, fragment.getAccountId(), user);

                /* If the device status was set, spawn an event for it. */
                /* This is within the try block because it relies on inventoryId being set */
                if (hardwareDto.getDeviceStatusEntryId() != null && hardwareDto.getDeviceStatusEntryId() != 0) {
                    EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
                }
            } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
                result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
            } catch (ObjectInOtherEnergyCompanyException e) {
                result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
            } catch (StarsTwoWayLcrYukonDeviceCreationException e) {
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
                return returnToCreateWithErrors(model, hardwareDto, context, flash, fragment, result);
            }
            
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            model.addAttribute("inventoryId", inventoryId);
            return "redirect:view";
            
        } else {
            return returnToCreateWithErrors(model, hardwareDto, context, flash, fragment, result);
        }
    }
    
    private String returnToCreateWithErrors(ModelMap model, 
                                            HardwareDto dto, 
                                            YukonUserContext context, 
                                            FlashScope flash, 
                                            AccountInfoFragment fragment, 
                                            BindingResult result) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        if (dto.getHardwareType() == HardwareType.NON_YUKON_METER) {
            return "operator/hardware/meterProfile.jsp";
        } else {
            setupCreateModel(fragment, model, dto.getHardwareType(), context);
            return "operator/hardware/hardware.jsp";
        }
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
        
        HardwareDto hardwareToDelete = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        hardwareEventLogService.hardwareDeletionAttemptedByOperator(context.getYukonUser(), hardwareToDelete.getDisplayName());
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        /* Delete this hardware or just take it off the account and put in back in the warehouse */
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        hardwareService.deleteHardware(context, delete, inventoryId, fragment.getAccountId());
        
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
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
        
        hardwareUiService.addDeviceToAccount(liteInventoryBase, fragment.getAccountId(), fromAccount, energyCompany, context.getYukonUser());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));
        
        return "redirect:list";
    }
    
    @RequestMapping
    public String viewMeterProfile(ModelMap model, AccountInfoFragment fragment, int inventoryId) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        return meterProfilePage(model, fragment, inventoryId);
    }
    
    @RequestMapping
    public String editMeterProfile(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, int inventoryId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        model.addAttribute("mode", PageEditMode.EDIT);
        return meterProfilePage(model, fragment, inventoryId);
    }
    
    private String meterProfilePage(ModelMap model, AccountInfoFragment fragment, int inventoryId) {
        
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
        
        model.addAttribute("hardwareDto", hardwareDto);
        model.addAttribute("displayName", hardwareDto.getDisplayName());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        return "operator/hardware/meterProfile.jsp";
    }
    
    @RequestMapping
    public String meterProfileCreate(ModelMap model, 
                                     YukonUserContext context, 
                                     AccountInfoFragment fragment, 
                                     FlashScope flashScope) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, context.getYukonUser());
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        
        model.addAttribute("mode", PageEditMode.CREATE);
        
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setHardwareType(HardwareType.NON_YUKON_METER);
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(HardwareType.NON_YUKON_METER.getDefinitionId());
        if (typeEntry.getEntryID() == 0) {
            /* This happens when using stars to handle meters (role property z_meter_mct_base_desig) but this energy company does
             * not have the 'MCT' device type (which is the non yukon meter device type) added to it's device type list in the 
             * config energy company section yet.  This is a setup issue and we will blow up here for now.  */
            throw new NoNonYukonMeterDeviceTypeException("There is no meter type in the device types list for this energy company.");
        }
        hardwareDto.setHardwareTypeEntryId(typeEntry.getEntryID());
        hardwareDto.setFieldInstallDate(new Date());
        
        for (SwitchAssignment assignement : hardwareUiService.getSwitchAssignments(new ArrayList<Integer>(), fragment.getAccountId())) {
            hardwareDto.getSwitchAssignments().add(assignement);
        }

        model.addAttribute("hardwareDto", hardwareDto);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        return "operator/hardware/meterProfile.jsp";
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
        hardwareUiService.addYukonMeter(meterId, fragment.getAccountId(), user);
        
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
        HardwareDto oldInventory = hardwareUiService.getHardwareDto(oldInventoryId, fragment.getEnergyCompanyId(), fragment.getAccountId());
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
    public void initBinder(WebDataBinder binder, YukonUserContext context) {
        
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
        
    }

    /* HELPERS */
    
    /* Add common element to model for view/edit and create modes both need for the hardware page */
    /* Should only be called by setupHardwareCreateModelMap and setupHardwareViewEditModelMap methods */
    private void setupCommonModelAttributes(HardwareType type, 
                                            AccountInfoFragment fragment, 
                                            ModelMap model, 
                                            YukonUserContext context) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteId());
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        List<LiteYukonPAObject> routes = energyCompany.getAllRoutes();
        model.addAttribute("routes", routes);
        
        model.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyId()));
        
        /* Setup elements to hide/show based on device type/class */
        HardwareClass clazz = type.getHardwareClass();
        switch (clazz) {
        case SWITCH:
            model.addAttribute("displayTypeKey", ".switches.displayType");
            break;
            
        case THERMOSTAT:
            model.addAttribute("displayTypeKey", ".thermostats.displayType");
            if (type.isZigbee()) {
                model.addAttribute("showInstallCode", true);
            }
            break;

        case METER:
            model.addAttribute("displayTypeKey", ".meters.displayType");
            break;
            
        case GATEWAY:
            model.addAttribute("displayTypeKey", ".gateways.displayType");
            model.addAttribute("showFirmwareVersion", true);
            break;
        }

        if (type.isGateway() || (type.isThermostat() && type.isZigbee())) {
            model.addAttribute("showMacAddress", true);
            model.addAttribute("showVoltage", false);
        }
        
        /* Hide route for meters and zigbee devices */
        if (!clazz.isMeter() && !type.isZigbee()) {
            model.addAttribute("showRoute", true);
        }
        
        /* Show two way device row for non-zigbee two way lcr's */
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }
        
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
                                    HardwareDto dto, 
                                    ModelMap model, 
                                    YukonUserContext context) {
        
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        boolean tstatAccess = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT, context.getYukonUser());
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        model.addAttribute("allowAccountEditing", allowAccountEditing);
        model.addAttribute("inventoryChecking", inventoryChecking);
        
        HardwareType type = dto.getHardwareType();
        HardwareClass clazz = type.getHardwareClass();
        
        setupCommonModelAttributes(type, fragment, model, context);
        
        int accountId = fragment.getAccountId();
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

        /* For switches and tstats, if they have inventory checking turned off they can edit the serial number. */
        if (!inventoryChecking && !clazz.isMeter()) {
            model.addAttribute("serialNumberEditable", true);
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
            }
            
            if (type.isZigbee()) {
                InventoryIdentifier gateway = gatewayDeviceDao.findGatewayByDeviceMapping(dto.getDeviceId());
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
            break;
            
        case METER:
            model.addAttribute("showDeviceName", true);
            if (type == HardwareType.YUKON_METER) {
                model.addAttribute("showMeterConfigAction", true);
            }
            if (rolePropertyDao.checkRole(YukonRole.METERING, context.getYukonUser())) {
                model.addAttribute("showMeterDetailAction", true);
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
        
    }
    
    private void setupListModel(AccountInfoFragment fragment, 
                                ModelMap model, 
                                YukonEnergyCompany energyCompany, 
                                YukonUserContext context) {
        LiteStarsEnergyCompany liteEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompany);
        
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
        ListMultimap<HardwareClass, HardwareDto> hardwareMap = hardwareUiService.getHardwareMapForAccount(fragment.getAccountId(), 
                                                                                                          fragment.getEnergyCompanyId());
        
        model.addAttribute("switches", hardwareMap.get(HardwareClass.SWITCH));
        model.addAttribute("meters", hardwareMap.get(HardwareClass.METER));
        model.addAttribute("thermostats", hardwareMap.get(HardwareClass.THERMOSTAT));
        model.addAttribute("gateways", hardwareMap.get(HardwareClass.GATEWAY));
        Iterable<HardwareDto> schedualableThermostats = Iterables.filter(hardwareMap.get(HardwareClass.THERMOSTAT), new Predicate<HardwareDto>() {
            @Override
            public boolean apply(HardwareDto dto) {
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
        
        String meterDesignation= rolePropertyDao.getPropertyStringValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, energyCompany.getEnergyCompanyUser());
        boolean starsMeters = meterDesignation.equalsIgnoreCase(StarsUtils.METER_BASE_DESIGNATION); 
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
    
    @ModelAttribute("none")
    public String getNone(YukonUserContext context) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        return messageSourceAccessor.getMessage("yukon.web.defaults.none");
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

    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setZigbeeDeviceService(ZigbeeDeviceService zigbeeDeviceService) {
        this.zigbeeDeviceService = zigbeeDeviceService;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
}