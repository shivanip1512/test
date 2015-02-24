package com.cannontech.web.stars.dr.operator.inventory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.HardwareModelHelper;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckRole({YukonRole.CONSUMER_INFO, YukonRole.INVENTORY})
@RequestMapping("/operator/inventory/*")
public class InventoryController {
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareModelHelper hardwareModelHelper;
    @Autowired private HardwareService hardwareService;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareValidator hardwareValidator;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao listDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping(value = "view", params = {"deviceId"})
    public String viewByDeviceId(ModelMap model, YukonUserContext context, int deviceId ,FlashScope flashScope) {
        model.addAttribute("mode", PageEditMode.VIEW);
        InventoryIdentifier inventory = null;
		try {
			inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
		} catch (EmptyResultDataAccessException e) {
			flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.error.notFound.inventoryId"));
			return "operator/inventory/inventory.jsp";
		}
		int inventoryId = inventory.getInventoryId();
        int accountId = inventoryDao.getAccountIdForInventory(inventoryId);
        if (accountId > 0) {
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("accountId", accountId);
            return "redirect:/stars/operator/hardware/view";
        }
        
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, context, hardware);
        
        return "operator/inventory/inventory.jsp";
    }
    
    @RequestMapping(value = "view", params = {"inventoryId"})
    public String viewByInventoryId(ModelMap model, YukonUserContext context, int inventoryId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        int accountId = inventoryDao.getAccountIdForInventory(inventoryId);
        if (accountId > 0) {
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("accountId", accountId);
            return "redirect:/stars/operator/hardware/view";
        }
        
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, context, hardware);
        
        return "operator/inventory/inventory.jsp";
    }
    
    @RequestMapping("edit")
    public String edit(ModelMap model, YukonUserContext context, int inventoryId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, context, hardware);
        return "operator/inventory/inventory.jsp";
    }
    
    @RequestMapping("creationPage")
    public String creationPage(ModelMap model, YukonUserContext context, int hardwareTypeId) {
        model.addAttribute("mode", PageEditMode.CREATE);
        rolePropertyDao.verifyProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, context.getYukonUser());
        
        Hardware hardware = new Hardware();
        hardware.setHardwareTypeEntryId(hardwareTypeId);
        hardware.setFieldInstallDate(new Date());
        YukonListEntry entry = listDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
        hardware.setHardwareType(type);
        hardware.setDisplayType(entry.getEntryText());
        model.addAttribute("hardware", hardware);
        
        setupCreationModel(model, context, type);
        
        return "operator/inventory/inventory.jsp";
    }
    
    private void setupCreationModel(ModelMap model, YukonUserContext context, HardwareType type) {
        LiteYukonUser user = context.getYukonUser();
        
        HardwareClass clazz = type.getHardwareClass();
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
            model.addAttribute("serialNumberEditable", true);
        }
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.INVENTORY_CREATE_HARDWARE.name());
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        model.addAttribute("energyCompanyId", energyCompany.getId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            int defaultRouteId = defaultRouteService.getDefaultRouteId(energyCompany);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute")
                    + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(energyCompany);
        model.addAttribute("routes", routes);
        
        List<Integer> energyCompanyIds = 
                Lists.transform(ecDao.getEnergyCompany(energyCompany.getId()).getAncestors(true), 
                                EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(energyCompanyIds));
        
        // Setup elements to hide/show based on device type/class
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
        
        // Hide route for meters, zigbee devices, and RF devices
        if (!clazz.isMeter() && !type.isZigbee() && !type.isRf() && !type.isEcobee()) {
            model.addAttribute("showRoute", true);
        }
        
        // Show two way device row for non-zigbee two way lcr's
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }
        
        model.addAttribute("showInstallNotes", false);
    }
    
    @RequestMapping("create")
    public String create(@ModelAttribute Hardware hardware, BindingResult result, ModelMap model,
            YukonUserContext context, HttpServletRequest request, String cancel, FlashScope flash)
            throws ServletRequestBindingException {
        if (cancel != null) {
            // Cancel Create
            return "redirect:home";
        }
        
        LiteYukonUser user = context.getYukonUser();
        hardwareModelHelper.creationAttempted(user, null, hardware,
            Sets.newHashSet(YukonRoleProperty.INVENTORY_CREATE_HARDWARE), result);
        
        if (!result.hasErrors()) {
            int inventoryId = hardwareModelHelper.create(user, hardware, result, request.getSession());
            
            if (result.hasErrors()) {
                return returnToCreateWithErrors(model, hardware, context, flash, result);
            }
            
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            model.addAttribute("inventoryId", inventoryId);
            return "redirect:view";
            
        }

        return returnToCreateWithErrors(model, hardware, context, flash, result);
    }

    private String returnToCreateWithErrors(ModelMap model, Hardware hardware, YukonUserContext context,
            FlashScope flash, BindingResult result) {
        model.addAttribute("mode", PageEditMode.CREATE);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        setupCreationModel(model, context, hardware.getHardwareType());
        return "operator/inventory/inventory.jsp";
    }
    
    @RequestMapping("update")
    public String update(@ModelAttribute Hardware hardware, BindingResult result, ModelMap model,
            YukonUserContext context, FlashScope flash, String cancel, int inventoryId) {
        if (cancel != null) {
            // Cancel Update
            model.addAttribute("inventoryId", inventoryId);
            model.clear();
            return "redirect:view";
        }
        
        LiteYukonUser user = context.getYukonUser();
        CustomerAccount custAccount = customerAccountDao.getById(hardware.getAccountId());
        hardwareEventLogService.hardwareUpdateAttempted(user, custAccount.getAccountNumber(),
            hardware.getSerialNumber(), EventSource.OPERATOR);

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user);
        
        // Validate and Update
        hardwareValidator.validate(hardware, result);
        if (result.hasErrors()) {
            return returnToEditWithErrors(context, model, flash, hardware, result);
        }
        
        // Update
        try {
            hardwareUiService.updateHardware(user, hardware);
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
            return returnToEditWithErrors(context, model, flash, hardware, result);
        }
        
        // Flash hardware updated
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        model.addAttribute("inventoryId", inventoryId);
        
        return "redirect:view";
    }

    private String returnToEditWithErrors(YukonUserContext context, ModelMap model, FlashScope flash,
            Hardware hardware, BindingResult result) {
        model.addAttribute("mode", PageEditMode.EDIT);
        // Return back to the jsp with the errors
        setupModel(model, context, hardware);
        model.addAttribute("displayName", hardware.getDisplayName());
        
        // Add errors to flash scope
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        
        return "operator/inventory/inventory.jsp";
    }
    
    @RequestMapping(value="update", params="cancel")
    public String update(ModelMap model, int inventoryId) {
        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }
    
    @RequestMapping("delete")
    public String delete(YukonUserContext context, FlashScope flash, int inventoryId) throws NotFoundException,
            PersistenceException, CommandCompletionException, SQLException {
        Hardware hardwareToDelete = hardwareUiService.getHardware(inventoryId);
        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareDeletionAttempted(user, hardwareToDelete.getDisplayName(), EventSource.OPERATOR);
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        
        hardwareService.deleteHardware(user, true, inventoryId);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        return "redirect:home";
    }
    
    public void setupModel(ModelMap model, YukonUserContext context, Hardware hardware) {
        boolean inventoryChecking = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.INVENTORY_CHECKING, hardware.getEnergyCompanyId());

        int inventoryId = hardware.getInventoryId();
        model.addAttribute("hardware", hardware);
        model.addAttribute("inventoryId", inventoryId);
        model.addAttribute("displayName", hardware.getDisplayName());
        
        HardwareType type = hardware.getHardwareType();
        HardwareClass clazz = type.getHardwareClass();
        model.addAttribute("displayTypeKey", ".displayType." + clazz);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES.name());
        
        // Hardware History
        model.addAttribute("hardwareHistory", hardwareUiService.getHardwareHistory(inventoryId));
        
        // Warehouses
        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(hardware.getEnergyCompanyId());
        EnergyCompany energyCompany = ecDao.getEnergyCompany(lsec.getEnergyCompanyId());
        
        model.addAttribute("energyCompanyId", lsec.getEnergyCompanyId());
        
        List<Warehouse> warehouses = lsec.getWarehouses();
        model.addAttribute("warehouses", warehouses);

        // For switches and tstats, if they have inventory checking turned off they can edit the serial number.
        if (!inventoryChecking && !clazz.isMeter()) {
            model.addAttribute("serialNumberEditable", true);
        }
        
        // For switches and tstats, show serial number instead of device name
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
        }
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            int defaultRouteId = defaultRouteService.getDefaultRouteId(energyCompany);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute")
                    + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(energyCompany);
        model.addAttribute("routes", routes);
        
        List<Integer> energyCompanyIds = 
                Lists.transform(energyCompany.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(energyCompanyIds));
        
        // Setup elements to hide/show based on device type/class
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

        // Hide route for meters and zigbee devices
        if (!clazz.isMeter() && !type.isZigbee() && !type.isRf() && type != HardwareType.LCR_3102 && !type.isEcobee()) {
            model.addAttribute("showRoute", true);
        }
        
        // Show two way device row for non-zigbee two way lcr's
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
    }
}
