package com.cannontech.web.stars.dr.operator.inventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable.DisplayType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.roles.yukon.EnergyCompanyRole.MeteringType;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.general.model.OperatorInventorySearchBy;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.Hardware;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
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
@CheckRole(YukonRole.INVENTORY)
@RequestMapping("/operator/inventory/*")
public class InventoryController {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareService hardwareService;
    @Autowired private RolePropertyDao rolePropertyDao; 
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao; 
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PaoDao paoDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareValidator hardwareValidator;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private HardwareModelHelper helper;
    
    /* Home - Landing Page */
    @RequestMapping
    public String home(ModelMap model, YukonUserContext context) {
        
        LiteYukonUser user = context.getYukonUser();
        rolePropertyDao.verifyRole(YukonRole.INVENTORY, user);
        
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany liteEc = starsDatabaseCache.getEnergyCompany(energyCompany);
        
        model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.home.fileUploadTitle");
        model.addAttribute("fileUploadTitle", title);
        
        MeteringType type = ecRolePropertyDao.getPropertyEnumValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, EnergyCompanyRole.MeteringType.class, energyCompany);
        model.addAttribute("showAddMeter", type == MeteringType.yukon);
        
        boolean showLinks = configurationSource.getBoolean("DIGI_ENABLED", false);
        model.addAttribute("showLinks", showLinks);
        
        boolean showSearch = rolePropertyDao.checkProperty(YukonRoleProperty.INVENTORY_SEARCH, user);
        model.addAttribute("showSearch", showSearch);
        
        boolean hasAddRange = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SN_ADD_RANGE, user);
        boolean hasCreate = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, user);
        boolean showActions = hasAddRange || hasCreate;
        model.addAttribute("showActions", showActions);
        
        SerialNumberValidation snv = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.SERIAL_NUMBER_VALIDATION, SerialNumberValidation.class, user);
        model.addAttribute("showAddByRange", snv == SerialNumberValidation.NUMERIC && hasAddRange);
        
        model.addAttribute("inventorySearch", new InventorySearch());
        
        List<YukonListEntry> yukonListEntries = liteEc.getYukonSelectionList(YukonSelectionListEnum.DEVICE_TYPE.getListName()).getYukonListEntries();
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
        
        return "operator/inventory/home.jsp";
    }
    
    /* Search Post */
    @RequestMapping
    public String search(HttpServletRequest request,
                         ModelMap model, 
                         YukonUserContext context, 
                         @ModelAttribute InventorySearch inventorySearch, 
                         Integer itemsPerPage, 
                         Integer page,
                         FlashScope flashScope) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.INVENTORY_SEARCH, context.getYukonUser());
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser()); //This may be wrong
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
            try {
                String phoneNo = ServletUtils.formatPhoneNumberForSearch(inventorySearch.getPhoneNumber());
                inventorySearch.setPhoneNumber(phoneNo);
            } catch (WebClientException e) {
                MessageSourceResolvable invalidPhoneNumberWarning = new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.invalidPhoneNumber");
                flashScope.setWarning(Collections.singletonList(invalidPhoneNumberWarning));
                hasWarnings = true;
            }
        }
                
        if(!hasWarnings){
            MeteringType value= ecRolePropertyDao.getPropertyEnumValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, EnergyCompanyRole.MeteringType.class,  ec);
            boolean starsMeters = value == MeteringType.stars;
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
            if(results.getHitCount() == 0){
                final MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
                List<Map<String, String>>  fieldList = buildNotFoundFieldList(inventorySearch, messageSourceAccessor);
                List<MessageSourceResolvable> messages = Lists.newArrayList();
                MessageSourceResolvable noResultsForFilter = new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.noResultsForFilter");
                messages.add(noResultsForFilter);
                for (Map<String, String> keyValue : fieldList){
                    String field = keyValue.keySet().iterator().next();
                    String fieldValue = keyValue.get(field);
                    MessageSourceResolvable noResultsForFilterItem = new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.noResultsForFilterItem", DisplayType.BULLETED , field, fieldValue);
                    messages.add(noResultsForFilterItem);
                }
                flashScope.setWarning(messages);
            }
            // Redirect to inventory page if only one result is found
            else if (results.getHitCount() == 1) {
                InventorySearchResult inventory = results.getResultList().get(0);
                
                if (inventory.getAccountId() > 0) {
                    model.addAttribute("inventoryId", inventory.getInventoryIdentifier().getInventoryId());
                    model.addAttribute("accountId", inventory.getAccountId());
                    return "redirect:/spring/stars/operator/hardware/view";
                }
                else {
                    model.addAttribute("inventoryId", inventory.getInventoryIdentifier().getInventoryId());
                    return "redirect:/spring/stars/operator/inventory/view";
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
    
    /**
     * Builds warning for search results that were not found
     *
     * @param inventorySearch
     * @return 
     */
    private List<Map<String, String>> buildNotFoundFieldList(InventorySearch inventorySearch, MessageSourceAccessor messageSourceAccessor) {
        List<Map<String, String>> notFoundList = new ArrayList<Map<String, String>>();
        buildNotFoundKeyValue(inventorySearch.getSerialNumber(), OperatorInventorySearchBy.serialNumber,  messageSourceAccessor, notFoundList);
        buildNotFoundKeyValue(inventorySearch.getMeterNumber(), OperatorInventorySearchBy.meterNumber,  messageSourceAccessor, notFoundList);
        buildNotFoundKeyValue(inventorySearch.getAccountNumber(), OperatorInventorySearchBy.accountNumber, messageSourceAccessor, notFoundList);
        buildNotFoundKeyValue(inventorySearch.getPhoneNumber(), OperatorInventorySearchBy.phoneNumber,  messageSourceAccessor, notFoundList);
        buildNotFoundKeyValue(inventorySearch.getLastName(), OperatorInventorySearchBy.lastName, messageSourceAccessor, notFoundList);
        buildNotFoundKeyValue(inventorySearch.getWorkOrderNumber(), OperatorInventorySearchBy.workOrderNumber, messageSourceAccessor, notFoundList);
        buildNotFoundKeyValue(inventorySearch.getAltTrackingNumber(), OperatorInventorySearchBy.altTrackingNumber, messageSourceAccessor, notFoundList);
        return notFoundList;
    }
    
    /**
     * Builds warning for search field that was not found
     *
     * @param value
     * @param searchBy
     * @param notFoundList
     */
    private void buildNotFoundKeyValue(String value, OperatorInventorySearchBy searchBy,  MessageSourceAccessor messageSourceAccessor, List<Map<String,String>> notFoundList) {
        if (StringUtils.isNotBlank(value)) {
            Map<String,String> keyValue = new HashMap<String,String>();
            keyValue.put(messageSourceAccessor.getMessage(searchBy.getFormatKey()), "'"+value+"'");
            notFoundList.add(keyValue);
        }
    }
    
    /* VIEW page for a particular hardware device */
    @RequestMapping
    public String view(ModelMap model, YukonUserContext context, int inventoryId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        int accountId = inventoryDao.getAccountIdForInventory(inventoryId);
        if (accountId > 0) {
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("accountId", accountId);
            return "redirect:/spring/stars/operator/hardware/view";
        }
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
    public String creationPage(ModelMap model, YukonUserContext context, int hardwareTypeId) {
        model.addAttribute("mode", PageEditMode.CREATE);
        rolePropertyDao.verifyProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, context.getYukonUser());
        
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
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        model.addAttribute("energyCompanyId", ec.getEnergyCompanyId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ec);
        
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
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
        }
        
        boolean showVoltage = !type.isZigbee() && !clazz.isGateway() && !clazz.isThermostat();
        model.addAttribute("showVoltage", showVoltage);
        
        /* Hide route for meters and zigbee devices */
        if (!clazz.isMeter() && !type.isZigbee()) {
            model.addAttribute("showRoute", true);
        }
        
        /* Show two way device row for non-zigbee two way lcr's */
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }
    }
    
    /* CREATE a hardware device */
    @RequestMapping
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
            int inventoryId = helper.create(user, null, hardware, result, request.getSession());
            
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
    @RequestMapping
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
    @RequestMapping
    public String delete(ModelMap model, YukonUserContext context, FlashScope flash, int inventoryId) 
    throws NotFoundException, PersistenceException, CommandCompletionException, SQLException, WebClientException {
        
        Hardware hardwareToDelete = hardwareUiService.getHardware(inventoryId);
        hardwareEventLogService.hardwareDeletionAttemptedByOperator(context.getYukonUser(), hardwareToDelete.getDisplayName());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        
        hardwareService.deleteHardware(context, true, inventoryId);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        return "redirect:home";
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
        model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
        
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
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
        }
        
        boolean showVoltage = !type.isZigbee() && !clazz.isGateway() && !clazz.isThermostat();
        model.addAttribute("showVoltage", showVoltage);

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