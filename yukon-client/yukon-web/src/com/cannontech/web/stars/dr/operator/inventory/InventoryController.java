package com.cannontech.web.stars.dr.operator.inventory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckRole({YukonRole.CONSUMER_INFO, YukonRole.INVENTORY})
@RequestMapping("/operator/inventory/*")
public class InventoryController {

    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareModelHelper hardwareModelHelper;
    @Autowired private HardwareService hardwareService;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareValidator hardwareValidator;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private YukonListDao listDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PointDao pointDao;

    private static final String key = "yukon.web.modules.operator.hardware.";

    @RequestMapping(value = "view", params = {"deviceId"})
    public String viewByDeviceId(ModelMap model, YukonUserContext userContext, int deviceId, FlashScope flash) {

        model.addAttribute("mode", PageEditMode.VIEW);
        InventoryIdentifier inventory = null;
        try {
            inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        } catch (EmptyResultDataAccessException e) {
            flash.setError(new YukonMessageSourceResolvable(key + "error.notFound.inventoryId"));
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
        setupModel(model, userContext, hardware);

        return "operator/inventory/inventory.jsp";
    }

    @RequestMapping(value = "view", params = {"inventoryId"})
    public String viewByInventoryId(ModelMap model, YukonUserContext userContext, int inventoryId) {

        model.addAttribute("mode", PageEditMode.VIEW);
        int accountId = inventoryDao.getAccountIdForInventory(inventoryId);
        if (accountId > 0) {
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("accountId", accountId);
            return "redirect:/stars/operator/hardware/view";
        }
        InventoryIdentifier inventory = inventoryDao.getYukonInventory(inventoryId);
        model.addAttribute("canEnableDisable", inventory.getHardwareType().isSupportServiceInServiceOut());
        model.addAttribute("canSendShed", inventory.getHardwareType().isSupportsIndividualDeviceShed());

        boolean isAllowDRControl =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_DR_CONTROL, userContext.getYukonUser());
        model.addAttribute("isAllowDRControl", isAllowDRControl);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);

        if (hardware.getHardwareType().isRf()) {
            int deviceId = inventoryDao.getDeviceId(inventoryId);
            model.addAttribute("deviceId", deviceId);
            model.addAttribute("showNetworkInfo", true);
            model.addAttribute("showMapNetwork", true);
        } else if (hardware.getHardwareType().isTwoWayPlcLcr()) {
            int deviceId = inventoryDao.getDeviceId(inventoryId);
            model.addAttribute("deviceId", deviceId);
            model.addAttribute("showMapNetwork", true);
        }
        setupModel(model, userContext, hardware);

        return "operator/inventory/inventory.jsp";
    }

    @RequestMapping("edit")
    public String edit(ModelMap model, YukonUserContext userContext, int inventoryId) {

        model.addAttribute("mode", PageEditMode.EDIT);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, userContext, hardware);

        return "operator/inventory/inventory.jsp";
    }

    @RequestMapping(value="creationPage", method=RequestMethod.POST)
    public String creationPage(ModelMap model, YukonUserContext userContext, int hardwareTypeId) {

        model.addAttribute("mode", PageEditMode.CREATE);
        LiteYukonUser user = userContext.getYukonUser();
        rolePropertyDao.verifyProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, user);

        Hardware hardware = new Hardware();
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        hardware.setEnergyCompanyId(energyCompany.getId());
        hardware.setHardwareTypeEntryId(hardwareTypeId);
        hardware.setFieldInstallDate(new Date());
        YukonListEntry entry = listDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
        hardware.setHardwareType(type);
        hardware.setDisplayType(entry.getEntryText());
        model.addAttribute("hardware", hardware);

        setupCreationModel(model, userContext, type);

        return "operator/inventory/inventory.jsp";
    }

    private void setupCreationModel(ModelMap model, YukonUserContext userContext, HardwareType type) {

        LiteYukonUser user = userContext.getYukonUser();

        HardwareClass clazz = type.getHardwareClass();
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
            model.addAttribute("serialNumberEditable", true);
        }

        model.addAttribute("editingRoleProperty", YukonRoleProperty.INVENTORY_CREATE_HARDWARE.name());
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        int ecId = ec.getId();
        model.addAttribute("energyCompanyId", ecId);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        String defaultRoute;
        int defaultRouteId = 0;
        try {
            defaultRouteId = defaultRouteService.getDefaultRouteId(ec);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = accessor.getMessage("yukon.common.route.default", defaultRoute);
        } catch(NotFoundException e) {
            defaultRouteId = 0;
            defaultRoute = accessor.getMessage("yukon.common.route.default.none");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        model.addAttribute("defaultRouteId", defaultRouteId);
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(ec);
        model.addAttribute("routes", routes);

        List<Integer> ecIds = Lists.transform(ec.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(ecIds));

        // Setup elements to hide/show based on device type/class
        model.addAttribute("displayTypeKey", ".displayType." + clazz);

        model.addAttribute("showMacAddress", type.isSupportsMacAddress());
        if (type.isZigbee()) {
            model.addAttribute("macAddressEditable", true);
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
        } else if (type.isHoneywell()) {
            model.addAttribute("showDeviceVendorUserId", true);
            model.addAttribute("macAddressEditable", true);
            model.addAttribute("deviceVendorUserIdEditable", true);
        } else if (type.isItron()) {
            model.addAttribute("macAddressEditable", true);
        }

        boolean showVoltage = !type.isZigbee() 
                && !clazz.isGateway() 
                && !clazz.isThermostat();
        model.addAttribute("showVoltage", showVoltage);

        if (type.showRoute()) {
            model.addAttribute("showRoute", true);
        }

        // Show two way device row for non-zigbee two way lcr's
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }

        model.addAttribute("showInstallNotes", false);
    }

    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@ModelAttribute Hardware hardware, BindingResult result, ModelMap model,
            YukonUserContext userContext, HttpSession session, String cancel, FlashScope flash) {

        if (cancel != null) {
            // Cancel Create
            return "redirect:home";
        }

        LiteYukonUser user = userContext.getYukonUser();
        hardwareModelHelper.creationAttempted(user, null, hardware,
                Sets.newHashSet(YukonRoleProperty.INVENTORY_CREATE_HARDWARE), result);

        if (!result.hasErrors()) {
            try {
                int inventoryId = hardwareModelHelper.create(user, hardware, result, session);
                model.addAttribute("inventoryId", inventoryId);
            } catch (RuntimeException e) {
                flash.setError(new YukonMessageSourceResolvable(key + "error.createDeviceFailed"));
                return returnToCreateWithErrors(model, hardware, userContext, flash, result);
            }

            if (result.hasErrors()) {
                return returnToCreateWithErrors(model, hardware, userContext, flash, result);
            }

            flash.setConfirm(new YukonMessageSourceResolvable(key + "hardwareCreated"));
            return "redirect:view";

        }

        return returnToCreateWithErrors(model, hardware, userContext, flash, result);
    }

    private String returnToCreateWithErrors(ModelMap model, Hardware hardware, YukonUserContext userContext,
            FlashScope flash, BindingResult result) {

        model.addAttribute("mode", PageEditMode.CREATE);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        if (!messages.isEmpty()) {
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
        }
        setupCreationModel(model, userContext, hardware.getHardwareType());

        return "operator/inventory/inventory.jsp";
    }

    @RequestMapping("update")
    public String update(@ModelAttribute Hardware hardware, BindingResult result, ModelMap model,
            YukonUserContext userContext, FlashScope flash, String cancel, int inventoryId) {

        if (cancel != null) {
            // Cancel Update
            model.addAttribute("inventoryId", inventoryId);
            model.clear();
            return "redirect:view";
        }

        LiteYukonUser user = userContext.getYukonUser();
        CustomerAccount custAccount = customerAccountDao.getById(hardware.getAccountId());
        hardwareEventLogService.hardwareUpdateAttempted(user, custAccount.getAccountNumber(),
                hardware.getSerialNumber(), EventSource.OPERATOR);

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user);

        // Validate and Update
        hardwareValidator.validate(hardware, result);
        if (result.hasErrors()) {
            return returnToEditWithErrors(userContext, model, flash, hardware, result);
        }

        // Update
        try {
            hardwareUiService.updateHardware(user, hardware);
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            result.rejectValue("serialNumber", key + "error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            result.rejectValue("serialNumber", key + "error.unavailable");
        } catch (Lcr3102YukonDeviceCreationException e) {
            switch (e.getType()) {
            case UNKNOWN:
                result.rejectValue("twoWayDeviceName", key + "error.unknown");
                break;
            case REQUIRED:
                result.rejectValue("twoWayDeviceName", key + "error.required");
                break;
            case UNAVAILABLE:
                result.rejectValue("twoWayDeviceName", key + "error.unavailable");
                break;
            case NON_NUMERIC:
                result.rejectValue("serialNumber", key + "error.nonNumericSerialNumber");
                break;
            }
        } 
        if (result.hasErrors()) {
            return returnToEditWithErrors(userContext, model, flash, hardware, result);
        }

        // Flash hardware updated
        flash.setConfirm(new YukonMessageSourceResolvable(key + "hardwareUpdated"));
        model.addAttribute("inventoryId", inventoryId);

        return "redirect:view";
    }

    private String returnToEditWithErrors(YukonUserContext userContext, ModelMap model, FlashScope flash,
            Hardware hardware, BindingResult result) {

        model.addAttribute("mode", PageEditMode.EDIT);
        // Return back to the jsp with the errors
        setupModel(model, userContext, hardware);
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
    public String delete(LiteYukonUser user, FlashScope flash, int inventoryId) 
            throws CommandCompletionException, SQLException {

        Hardware toDelete = hardwareUiService.getHardware(inventoryId);
        hardwareEventLogService.hardwareDeletionAttempted(user, toDelete.getDisplayName(), EventSource.OPERATOR);
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);

        hardwareService.deleteHardware(user, true, inventoryId);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "hardwareDeleted"));

        return "redirect:home";
    }

    public void setupModel(ModelMap model, YukonUserContext userContext, Hardware hardware) {

        int ecId = hardware.getEnergyCompanyId();
        boolean inventoryChecking = ecSettingDao.getBoolean(EnergyCompanySettingType.INVENTORY_CHECKING, ecId);

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
        LiteStarsEnergyCompany lsec = starsDbCache.getEnergyCompany(ecId);
        EnergyCompany ec = ecDao.getEnergyCompany(ecId);

        model.addAttribute("energyCompanyId", ecId);

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

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        String defaultRoute;
        int defaultRouteId = 0;
        try {
            defaultRouteId = defaultRouteService.getDefaultRouteId(ec);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = accessor.getMessage("yukon.common.route.default", defaultRoute);
        } catch(NotFoundException e) {
            defaultRouteId = 0;
            defaultRoute = accessor.getMessage("yukon.common.route.default.none");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        model.addAttribute("defaultRouteId", defaultRouteId);

        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(ec);
        model.addAttribute("routes", routes);

        List<Integer> ecIds = Lists.transform(ec.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(ecIds));

        // Setup elements to hide/show based on device type/class
        model.addAttribute("displayTypeKey", ".displayType." + clazz);

        model.addAttribute("showMacAddress", type.isSupportsMacAddress());
        if (type.isZigbee()) {
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
        } else if (type.isHoneywell()) {
            model.addAttribute("showDeviceVendorUserId", true);
        }

        boolean showVoltage = !type.isZigbee() 
                && !clazz.isGateway() 
                && !clazz.isThermostat();
        model.addAttribute("showVoltage", showVoltage);

        if (type.showRoute()) {
            model.addAttribute("showRoute", true);
        }

        // Show two way device row for non-zigbee two way lcr's
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }

        // Add Pao Specifics
        Integer deviceId = hardware.getDeviceId();
        if (deviceId != null && deviceId > 0 && !type.isZigbee()) { // points for ZigBee device have their own
            // special box
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateType dateTYpe = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateTYpe.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateTYpe.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateTYpe.getPropertyEditor());
    }

}