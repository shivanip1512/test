package com.cannontech.web.stars.dr.operator.inventory;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.Hardware;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareValidator;

@Controller
@CheckRole(YukonRole.INVENTORY)
@RequestMapping("/operator/inventory/*")
public class InventoryController {
    
    private @Autowired HardwareUiService hardwareUiService;
    private @Autowired RolePropertyDao rolePropertyDao; 
    private @Autowired StarsDatabaseCache starsDatabaseCache;
    private @Autowired YukonUserContextMessageSourceResolver messageSourceResolver;
    private @Autowired PaoDao paoDao;
    private @Autowired EnergyCompanyDao energyCompanyDao;
    private @Autowired YukonEnergyCompanyService yukonEnergyCompanyService;
    private @Autowired ConfigurationSource configurationSource;
    private @Autowired InventoryDao inventoryDao;
    private @Autowired HardwareEventLogService hardwareEventLogService;
    private @Autowired HardwareValidator hardwareValidator;
    
    /* Home - Landing Page */
    @RequestMapping
    public String home(ModelMap model, YukonUserContext context) {
        
        LiteYukonUser user = context.getYukonUser();
        rolePropertyDao.verifyRole(YukonRole.INVENTORY, user);
        
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.home.fileUploadTitle");
        model.addAttribute("fileUploadTitle", title);
        
        boolean showLinks = configurationSource.getBoolean("DIGI_ENABLED", false);
        model.addAttribute("showLinks", showLinks);
        
        boolean showActions = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SN_ADD_RANGE, user)
            || rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SN_DELETE_RANGE, user)
            || rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, user);
        model.addAttribute("showActions", showActions);
        
        model.addAttribute("inventorySearch", new InventorySearch());
        
        return "operator/inventory/home.jsp";
    }

    /* Search Post */
    @RequestMapping
    public String search(ModelMap model, YukonUserContext context, 
                         @ModelAttribute InventorySearch inventorySearch, 
                         Integer itemsPerPage, 
                         Integer page) {
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser()); //This may be wrong
        LiteStarsEnergyCompany liteEc = starsDatabaseCache.getEnergyCompany(ec);

        String meterDesignation= rolePropertyDao.getPropertyStringValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, ec.getEnergyCompanyUser());
        boolean starsMeters = meterDesignation.equalsIgnoreCase(StarsUtils.METER_BASE_DESIGNATION);
        model.addAttribute("starsMeters", starsMeters);
        
        // PAGING
        if (page == null) page = 1;
        if (itemsPerPage == null) itemsPerPage = 25;
        int startIndex = (page - 1) * itemsPerPage;
        
        SearchResult<InventorySearchResult> results = inventoryDao.search(inventorySearch, 
                                                                          liteEc.getAllEnergyCompaniesDownward(), 
                                                                          startIndex, 
                                                                          itemsPerPage,
                                                                          starsMeters);
        model.addAttribute("results", results);
        
        model.addAttribute("showAccountNumber", StringUtils.isNotBlank(inventorySearch.getAccountNumber()));
        model.addAttribute("showPhoneNumber", StringUtils.isNotBlank(inventorySearch.getPhoneNumber()));
        model.addAttribute("showLastName", StringUtils.isNotBlank(inventorySearch.getLastName()));
        model.addAttribute("showWordOrder", StringUtils.isNotBlank(inventorySearch.getWorkOrderNumber()));
        model.addAttribute("showAltTrackingNumber", StringUtils.isNotBlank(inventorySearch.getAltTrackingNumber()));
        model.addAttribute("showEc", liteEc.hasChildEnergyCompanies());
        
        return "operator/inventory/inventoryList.jsp";
    }
    
    /* VIEW page for a particular hardware device */
    @RequestMapping
    public String view(ModelMap model, YukonUserContext context, int inventoryId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, context, hardware);
        return "operator/inventory/inventory.jsp";
    }
    
    /* EDIT page for a particular hardware device */
    @RequestMapping
    public String edit(ModelMap model, YukonUserContext context, int inventoryId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, context, hardware);
        return "operator/inventory/inventory.jsp";
    }
    
    /* CREATION page for a hardware device */
    @RequestMapping
    public String creationPage(ModelMap model) {
        //TODO
        return "operator/inventory/inventory.jsp";
    }
    
    /* CREATE a hardware device */
    @RequestMapping
    public String create(@ModelAttribute Hardware hardware, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash) throws ServletRequestBindingException {
        //TODO
        return "operator/inventory/inventory.jsp";
    }
    
    /* UPDATE a particular hardware device */
    @RequestMapping(value="update", params="save")
    public String update(@ModelAttribute Hardware hardware, BindingResult result,
                         ModelMap model, 
                         YukonUserContext context,
                         HttpServletRequest request,
                         FlashScope flash,
                         int inventoryId) {
        
        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareUpdateAttemptedByOperator(user, hardware.getSerialNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user);
        
        /* Validate and Update*/
        hardwareValidator.validate(hardware, result);
        if (result.hasErrors()) {
            return returnToEditWithErrors(context, model, flash, hardware, result);
        }
        
        /* Update */
        try {
            hardwareUiService.updateHardware(user, hardware);
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
            return returnToEditWithErrors(context, model, flash, hardware, result);
        }
        
        /* Flash hardware updated */
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        model.addAttribute("inventoryId", inventoryId);
        
        return "redirect:view";
    }
    
    private String returnToEditWithErrors(YukonUserContext context, 
                                          ModelMap model, 
                                          FlashScope flash, 
                                          Hardware hardware, 
                                          BindingResult result) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        /* Return back to the jsp with the errors */
        setupModel(model, context, hardware);
        model.addAttribute("displayName", hardware.getDisplayName());
        
        /* Add errors to flash scope */
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        
        return "operator/inventory/inventory.jsp";
    }
    
    /* CANCEL updating a particular hardware device */
    @RequestMapping(value="update", params="cancel")
    public String update(ModelMap model, int inventoryId) {
        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }
    
    public void setupModel(ModelMap model, YukonUserContext context, Hardware hardware) {
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        
        int inventoryId = hardware.getInventoryId();
        model.addAttribute("hardware", hardware);
        model.addAttribute("inventoryId", inventoryId);
        model.addAttribute("displayName", hardware.getDisplayName());
        
        HardwareType type = hardware.getHardwareType();
        HardwareClass clazz = type.getHardwareClass();
        model.addAttribute("displayTypeKey", ".displayType." + clazz);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES.name());
        
        /* Hardware History */
        model.addAttribute("hardwareHistory", hardwareUiService.getHardwareHistory(inventoryId));
        
        /* Warehouses */
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(hardware.getEnergyCompanyId());
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
        model.addAttribute("displayTypeKey", ".displayType." + clazz);

        if (type.isZigbee()) {
            model.addAttribute("showMacAddress", true);
            model.addAttribute("showVoltage", false);
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
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
    
    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
        
    }
    
}