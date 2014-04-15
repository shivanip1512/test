package com.cannontech.web.stars.dr.operator.inventory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.general.model.OperatorInventorySearchBy;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckRole({YukonRole.CONSUMER_INFO, YukonRole.INVENTORY})
@RequestMapping("/operator/inventory/*")
public class AssetDashboardController {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareService hardwareService;
    @Autowired private RolePropertyDao rpDao; 
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private PaoDao paoDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private EnergyCompanyDao ecService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareValidator hardwareValidator;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private HardwareModelHelper helper;
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private EnergyCompanySettingDao ecSettingsDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private DefaultRouteService defaultRouteService;

    /* Home - Landing Page */
    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        boolean ecOperator = ecService.isEnergyCompanyOperator(context.getYukonUser());
        
        if (ecOperator) {
            EnergyCompany energyCompany = ecService.getEnergyCompanyByOperator(user);
            
            int ecId = energyCompany.getId();
            model.addAttribute("energyCompanyId", ecId);
            
            MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(context);
            String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.home.fileUploadTitle");
            model.addAttribute("fileUploadTitle", title);
            
            MeteringType type = ecSettingsDao.getEnum(EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION, MeteringType.class, ecId);
            model.addAttribute("showAddMeter", type == MeteringType.yukon);
            
            boolean showLinks = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DIGI_ENABLED);
            model.addAttribute("showLinks", showLinks);
            
            boolean showSearch = rpDao.checkProperty(YukonRoleProperty.INVENTORY_SEARCH, user);
            model.addAttribute("showSearch", showSearch);
            
            boolean showAccountSearch = rpDao.checkProperty(YukonRoleProperty.OPERATOR_ACCOUNT_SEARCH, user);
            model.addAttribute("showAccountSearch", showAccountSearch);
            
            /** Hardware Creation */
            boolean hasAddHardwareByRange = rpDao.checkProperty(YukonRoleProperty.SN_ADD_RANGE, user);
            boolean hasCreateHardware = rpDao.checkProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, user);

            boolean showHardwareCreate = hasAddHardwareByRange || hasCreateHardware;
            model.addAttribute("showHardwareCreate", showHardwareCreate);
            
            /** Account Creation */
            boolean hasCreateAccount = rpDao.checkProperty(YukonRoleProperty.OPERATOR_NEW_ACCOUNT_WIZARD, user);
            boolean showAccountCreate = hasCreateAccount && ecOperator;
            model.addAttribute("showAccountCreate", showAccountCreate);
            
            /** Actions Dropdown Button */
            if(showHardwareCreate || showAccountCreate) {
                model.addAttribute("showActions", true);
            }
            
            SerialNumberValidation snv = ecSettingsDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION,
                                                                         SerialNumberValidation.class,
                                                                         ecId);
            model.addAttribute("showAddByRange", snv == SerialNumberValidation.NUMERIC && hasAddHardwareByRange);
            
            model.addAttribute("inventorySearch", new InventorySearch());
            
            List<YukonListEntry> yukonListEntries = 
                selectionListService.getSelectionList(energyCompany, 
                                              YukonSelectionListEnum.DEVICE_TYPE.getListName()).getYukonListEntries();
            Iterable<YukonListEntry> addHardwareTypes = Iterables.filter(yukonListEntries, new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry input) {
                    HardwareType type = HardwareType.valueOf(input.getYukonDefID());
                    return type != HardwareType.YUKON_METER && type != HardwareType.NON_YUKON_METER;
                }
            });
            model.addAttribute("addHardwareTypes", addHardwareTypes.iterator());
            
            Iterable<YukonListEntry> addHardwareByRangeTypes = Iterables.filter(yukonListEntries, new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry input) {
                    HardwareType type = HardwareType.valueOf(input.getYukonDefID()); 
                    return type.isSupportsAddByRange();
                }
            });
            model.addAttribute("addHardwareByRangeTypes", addHardwareByRangeTypes.iterator());
        }

        return "operator/inventory/home.jsp";
    }
    
    /* Search Post */
    @RequestMapping("search")
    public String search(HttpServletRequest request,
                         ModelMap model, 
                         YukonUserContext context, 
                         @ModelAttribute InventorySearch inventorySearch, 
                         Integer itemsPerPage, 
                         Integer page,
                         FlashScope flashScope) {
        rpDao.verifyProperty(YukonRoleProperty.INVENTORY_SEARCH, context.getYukonUser());
        YukonEnergyCompany ec = ecService.getEnergyCompanyByOperator(context.getYukonUser()); //This may be wrong
        LiteStarsEnergyCompany liteEc = starsDatabaseCache.getEnergyCompany(ec);
        
        String searchByStr = ServletRequestUtils.getStringParameter(request, "searchBy", null);
        
        if (StringUtils.isNotBlank(searchByStr)) {
            //search initiated from Operations.jsp (Search for existing hardware)
            OperatorInventorySearchBy searchBy = OperatorInventorySearchBy.valueOf(searchByStr);
            String searchValue = ServletRequestUtils.getStringParameter(request, "searchValue", null);
            updateInventorySearch(inventorySearch, searchBy, searchValue);
        }
        
        boolean hasWarnings = false;
        
        if (StringUtils.isNotBlank(inventorySearch.getPhoneNumber())) {
            if (phoneNumberFormattingService.isHasInvalidCharacters(inventorySearch.getPhoneNumber())) {
                MessageSourceResolvable invalidPhoneNumberWarning =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.invalidPhoneNumber");
                flashScope.setWarning(Collections.singletonList(invalidPhoneNumberWarning));
                hasWarnings = true;
            }
        }
                
        if(!hasWarnings){
            MeteringType value = ecSettingsDao.getEnum(EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION, MeteringType.class, ec.getEnergyCompanyId());

            boolean starsMeters = value == MeteringType.stars;
            model.addAttribute("starsMeters", starsMeters);
            
            // PAGING
            if (page == null) {
                page = 1;
            }
            itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
            int startIndex = (page - 1) * itemsPerPage;
            
            List<EnergyCompany> descendantEcs = 
                    ecService.getEnergyCompany(liteEc.getEnergyCompanyId()).getDescendants(true);
            List<Integer> ecDescendantIds = Lists.transform(descendantEcs, EnergyCompanyDao.TO_ID_FUNCTION);
            SearchResults<InventorySearchResult> results = 
                    inventoryDao.search(inventorySearch, ecDescendantIds, startIndex, itemsPerPage, starsMeters);

            // Redirect to inventory page if only one result is found
           if (results.getHitCount() == 1) {
                InventorySearchResult inventory = results.getResultList().get(0);
                
                if (inventory.getAccountId() > 0) {
                    model.addAttribute("inventoryId", inventory.getInventoryIdentifier().getInventoryId());
                    model.addAttribute("accountId", inventory.getAccountId());
                    return "redirect:/stars/operator/hardware/view";
                }
                else {
                    model.addAttribute("inventoryId", inventory.getInventoryIdentifier().getInventoryId());
                    return "redirect:/stars/operator/inventory/view";
                }
            }
            model.addAttribute("results", results);
        }
        
        model.addAttribute("showAccountNumber", StringUtils.isNotBlank(inventorySearch.getAccountNumber()));
        model.addAttribute("showPhoneNumber", StringUtils.isNotBlank(inventorySearch.getPhoneNumber()));
        model.addAttribute("showLastName", StringUtils.isNotBlank(inventorySearch.getLastName()));
        model.addAttribute("showWordOrder", StringUtils.isNotBlank(inventorySearch.getWorkOrderNumber()));
        model.addAttribute("showAltTrackingNumber", StringUtils.isNotBlank(inventorySearch.getAltTrackingNumber()));
        model.addAttribute("showEc", liteEc.hasChildEnergyCompanies());
        model.addAttribute("hasWarnings", hasWarnings);
        
        return "operator/inventory/inventoryList.jsp";
    }
    
    /**
     * Updates inventory search with the search value
     *
     * @param inventorySearch
     * @param searchBy
     * @param searchValue
     */
    private void updateInventorySearch(InventorySearch inventorySearch, OperatorInventorySearchBy searchBy, String searchValue){
        switch(searchBy){
        case serialNumber:
            inventorySearch.setSerialNumber(searchValue);
            break;
        case meterNumber:
            inventorySearch.setMeterNumber(searchValue);
            break;
        case accountNumber:
            inventorySearch.setAccountNumber(searchValue);
            break;
        case phoneNumber:
            inventorySearch.setPhoneNumber(searchValue);
            break;
        case lastName:
            inventorySearch.setLastName(searchValue);
            break;
        case workOrderNumber:
            inventorySearch.setWorkOrderNumber(searchValue);
            break;
        case altTrackingNumber:
            inventorySearch.setAltTrackingNumber(searchValue);
            break;
        }
    }
    
    /* VIEW page for a particular hardware device */
    @RequestMapping("view")
    public String view(ModelMap model, YukonUserContext context, int inventoryId) {
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
    
    /* EDIT page for a particular hardware device */
    @RequestMapping("edit")
    public String edit(ModelMap model, YukonUserContext context, int inventoryId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        setupModel(model, context, hardware);
        return "operator/inventory/inventory.jsp";
    }
    
    /* CREATION page for a hardware device */
    @RequestMapping("creationPage")
    public String creationPage(ModelMap model, YukonUserContext context, int hardwareTypeId) {
        model.addAttribute("mode", PageEditMode.CREATE);
        rpDao.verifyProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, context.getYukonUser());
        
        Hardware hardware = new Hardware();
        hardware.setHardwareTypeEntryId(hardwareTypeId);
        hardware.setFieldInstallDate(new Date());
        YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeId);
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
        EnergyCompany energyCompany = ecService.getEnergyCompanyByOperator(user);
        model.addAttribute("energyCompanyId", energyCompany.getId());
        
        MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            int defaultRouteId = defaultRouteService.getDefaultRouteId(energyCompany);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        List<LiteYukonPAObject> routes = ecService.getAllRoutes(energyCompany);
        model.addAttribute("routes", routes);
        
        List<Integer> energyCompanyIds = 
                Lists.transform(ecService.getEnergyCompany(energyCompany.getId()).getParents(true), 
                                EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(energyCompanyIds));
        
        /* Setup elements to hide/show based on device type/class */
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
        
        /* Hide route for meters, zigbee devices, and RF devices */
        if (!clazz.isMeter() && !type.isZigbee() && !type.isRf()) {
            model.addAttribute("showRoute", true);
        }
        
        /* Show two way device row for non-zigbee two way lcr's */
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }
        
        model.addAttribute("showInstallNotes", false);
    }
    
    /* CREATE a hardware device */
    @RequestMapping("create")
    public String create(@ModelAttribute Hardware hardware, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 String cancel,
                                 FlashScope flash) throws ServletRequestBindingException {
        
        if (cancel != null) { /* Cancel Create */
            return "redirect:home";
        }
        
        LiteYukonUser user = context.getYukonUser();
        helper.creationAttempted(user, null, hardware, Sets.newHashSet(YukonRoleProperty.INVENTORY_CREATE_HARDWARE), result);
        
        if (!result.hasErrors()) {
            int inventoryId = helper.create(user, hardware, result, request.getSession());
            
            if (result.hasErrors()) {
                return returnToCreateWithErrors(model, hardware, context, flash, result);
            }
            
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            model.addAttribute("inventoryId", inventoryId);
            return "redirect:view";
            
        } else {
            return returnToCreateWithErrors(model, hardware, context, flash, result);
        }
    }
    
    private String returnToCreateWithErrors(ModelMap model, Hardware hardware, YukonUserContext context, FlashScope flash, BindingResult result) {
        model.addAttribute("mode", PageEditMode.CREATE);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setMessage(messages, FlashScopeMessageType.ERROR);
        setupCreationModel(model, context, hardware.getHardwareType());
        return "operator/inventory/inventory.jsp";
    }
    
    /* UPDATE a particular hardware device */
    @RequestMapping("update")
    public String update(@ModelAttribute Hardware hardware, BindingResult result,
                         ModelMap model, 
                         YukonUserContext context,
                         HttpServletRequest request,
                         FlashScope flash,
                         String cancel,
                         int inventoryId) {
        
        if (cancel != null) { /* Cancel Update */
            model.addAttribute("inventoryId", inventoryId);
            model.clear();
            return "redirect:view";
        }
        
        LiteYukonUser user = context.getYukonUser();
        CustomerAccount custAccount = customerAccountDao.getById(hardware.getAccountId());
        hardwareEventLogService.hardwareUpdateAttempted(user, custAccount.getAccountNumber(), hardware.getSerialNumber(), EventSource.OPERATOR);

        rpDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user);
        
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
    
    /* DELETE */
    @RequestMapping("delete")
    public String delete(ModelMap model, YukonUserContext context, FlashScope flash, int inventoryId) 
    throws NotFoundException, PersistenceException, CommandCompletionException, SQLException {
        
        Hardware hardwareToDelete = hardwareUiService.getHardware(inventoryId);
        LiteYukonUser user = context.getYukonUser();
        hardwareEventLogService.hardwareDeletionAttempted(user, hardwareToDelete.getDisplayName(), EventSource.OPERATOR);
        rpDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        
        hardwareService.deleteHardware(user, true, inventoryId);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        return "redirect:home";
    }
    
    public void setupModel(ModelMap model, YukonUserContext context, Hardware hardware) {
        boolean inventoryChecking = rpDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, context.getYukonUser());
        
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
        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(hardware.getEnergyCompanyId());
        EnergyCompany energyCompany = ecService.getEnergyCompany(lsec.getEnergyCompanyId());
        
        model.addAttribute("energyCompanyId", lsec.getEnergyCompanyId());
        
        List<Warehouse> warehouses = lsec.getWarehouses();
        model.addAttribute("warehouses", warehouses);

        /* For switches and tstats, if they have inventory checking turned off they can edit the serial number. */
        if (!inventoryChecking && !clazz.isMeter()) {
            model.addAttribute("serialNumberEditable", true);
        }
        
        /* For switches and tstats, show serial number instead of device name */
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
        }
        
        MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            int defaultRouteId = defaultRouteService.getDefaultRouteId(energyCompany);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        
        List<LiteYukonPAObject> routes = ecService.getAllRoutes(energyCompany);
        model.addAttribute("routes", routes);
        
        List<Integer> energyCompanyIds = 
                Lists.transform(energyCompany.getParents(true), EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(energyCompanyIds));
        
        /* Setup elements to hide/show based on device type/class */
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