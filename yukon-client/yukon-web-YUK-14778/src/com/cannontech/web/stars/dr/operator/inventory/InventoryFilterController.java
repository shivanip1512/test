package com.cannontech.web.stars.dr.operator.inventory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterRuleType;
import com.cannontech.web.stars.dr.operator.inventory.model.RuleModel;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsFilterService;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.InvalidSerialNumberRangeDataException;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckRole(YukonRole.INVENTORY)
@RequestMapping(value="/operator/inventory/*")
public class InventoryFilterController {
    
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EnergyCompanyService ecService;
    @Autowired private FilterModelValidator filterValidator;
    @Autowired private InventoryActionsFilterService filterService;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private SelectionListService selectionListService;
    
    private static final String path = "operator/inventory/";
    private static final String key = "yukon.web.modules.operator.inventory.filter.";
    
    /* Setup Filter Rules */
    @RequestMapping(value="setupFilterRules")
    public String setupFilterRules(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        
        FilterModel filter = new FilterModel();
        model.addAttribute("energyCompanyId", ecDao.getEnergyCompanyByOperator(user).getId());
        model.addAttribute("filterModel", filter);
        
        return path + "setupFilterRules.jsp";
    }
    
    /* Add Filter Rule */
    @RequestMapping(value="applyFilter", method=RequestMethod.POST, params="addButton")
    public String addFilterRow(@ModelAttribute("filterModel") FilterModel filter, BindingResult result, 
            FlashScope flash, ModelMap model, LiteYukonUser user, FilterRuleType ruleType) {
        
        boolean hasErrors = validate(result, filter, model, flash, user);
        if (hasErrors) {
            return path + "setupFilterRules.jsp";
        }
        
        /* Add a row and return to filter setup page. */
        RuleModel newRule = new RuleModel(ruleType);
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lsec = starsDbCache.getEnergyCompany(ec.getId());
        String typesListName = YukonSelectionListEnum.DEVICE_TYPE.getListName();
        YukonSelectionList devTypeList = selectionListService.getSelectionList(lsec, typesListName);
        newRule.setDeviceType(devTypeList.getYukonListEntries().get(0).getEntryID());
        
        filter.getFilterRules().add(newRule);
        model.addAttribute("energyCompanyId", ecDao.getEnergyCompanyByOperator(user).getId());
        
        return path + "setupFilterRules.jsp";
    }
    
    /* Cancel */
    @RequestMapping(value="applyFilter", method=RequestMethod.POST, params="cancelButton")
    public String cancel() {
        return "redirect:home";
    }
    
    /* Apply Filter */
    @RequestMapping(value="applyFilter", method=RequestMethod.POST, params="apply")
    public String applyFilter(@ModelAttribute("filterModel") FilterModel filter, BindingResult result, FlashScope flash,
                              ModelMap model, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        boolean hasErrors = validate(result, filter, model, flash, user);
        if (hasErrors) {
            return path + "setupFilterRules.jsp";
        }
        
        Set<InventoryIdentifier> inventory = null;
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        try {
            TimeZone ecTimeZone = ecService.getDefaultTimeZone(ec.getId());
            DateTimeZone ecDateTimeZone = DateTimeZone.forTimeZone(ecTimeZone);
            inventory = filterService.getInventory(filter, ecDateTimeZone, user);
        } catch (InvalidSerialNumberRangeDataException e) {
            flash.setError(new YukonMessageSourceResolvable(key + "error.invalidSerialNumbers"));
            model.addAttribute("energyCompanyId", ecDao.getEnergyCompanyByOperator(user).getId());
            
            return path + "setupFilterRules.jsp";
        }
        
        if (inventory.isEmpty()) {
            flash.setError(new YukonMessageSourceResolvable(key + "error.noInventory"));
            model.addAttribute("energyCompanyId", ecDao.getEnergyCompanyByOperator(user).getId());
            
            return path + "setupFilterRules.jsp";
        }
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String description = accessor.getMessage("yukon.common.collection.inventory.filterBased");
        
        InventoryCollection collection = collectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        return "redirect:inventoryActions";
    }
    
    private boolean validate(BindingResult result, FilterModel filter, ModelMap model, FlashScope flash, 
            LiteYukonUser user) {
        
        filterValidator.validate(filter, result);
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("energyCompanyId", ecDao.getEnergyCompanyByOperator(user).getId());
        }
        return result.hasErrors();
    }
    
    /* Remove Rule - This guy as no 'params' in the @RequestMapping because the 
     * form had to be submitted via javascript for removes. */
    @RequestMapping(value="applyFilter", method=RequestMethod.POST)
    public String remove(@ModelAttribute("filterModel") FilterModel filter, BindingResult result,
                              ModelMap model, LiteYukonUser user, int removeRule) {
        
        filter.getFilterRules().remove(removeRule);
        model.addAttribute("energyCompanyId", ecDao.getEnergyCompanyByOperator(user).getId());
        
        return path + "setupFilterRules.jsp";
    }
    
    /* Global Model Attributes */
    @ModelAttribute(value="ruleTypes")
    public List<FilterRuleType> getRuleTypes(LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        
        List<FilterRuleType> ruleTypes = Lists.newArrayList(FilterRuleType.values());
        SerialNumberValidation value = ecSettingDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION,
                SerialNumberValidation.class, ec.getId());
        if (value == SerialNumberValidation.ALPHANUMERIC) {
            ruleTypes.remove(FilterRuleType.SERIAL_NUMBER_RANGE);
        }
        
        // Only show the warehouse option if multiple warehouses are enabled and more than one exists 
        // OR mutliple warehouses are disabled and thus every inventory not assigned to an account is 
        // considered in the warehouse.
        List<Warehouse> warehouses = getAvailableWarehouses(user);
        boolean multiple = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, user);
        if (warehouses.isEmpty() && multiple) {
            ruleTypes.remove(FilterRuleType.WAREHOUSE);
        }
        
        return ruleTypes;
    }
    
    @ModelAttribute(value="filterModes")
    public List<FilterMode> getFilterModes() {
        return Arrays.asList(FilterMode.values());
    }
    
    @ModelAttribute(value="applianceTypes")
    public List<ApplianceCategory> getApplianceTypes(LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        List<ApplianceCategory> applianceCategories = applianceCategoryDao.getApplianceCategoriesByEcId(ec.getId());
        
        return applianceCategories;
    }
    
    @ModelAttribute(value="residentialModelEntryId")
    public int getResidentialModelCustomerTypeId(){
        return RuleModel.RESIDENTIAL_MODEL_ENTRY_ID;
    }
    
    @ModelAttribute(value="ciCustomerTypes")
    public List<YukonListEntry> getCiCustomerTypes(LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        YukonSelectionList customerTypes = 
                selectionListService.getSelectionList(ec, YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE);
        
        return customerTypes.getYukonListEntries();
    }
    
    @ModelAttribute(value="energyCompanies")
    public List<EnergyCompany> getEnergyCompanies(LiteYukonUser user) {
        
        List<EnergyCompany> energyCompanies = Lists.newArrayList();
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        energyCompanies.addAll(energyCompany.getDescendants(true));
        
        return energyCompanies;
    }
    
    @ModelAttribute(value="serviceCompanies")
    public List<ServiceCompanyDto> getServiceCompanies(LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lsec = starsDbCache.getEnergyCompany(ec);
        
        // Get all the parent and child energy company ids of this energy company
        List<LiteStarsEnergyCompany> memberEnergyCompanies = ECUtils.getAllDescendants(lsec);
        memberEnergyCompanies.addAll(ECUtils.getAllAscendants(lsec));
        
        Set<Integer> usableEnergyCompanyIds = 
            Sets.newHashSet(Iterables.transform(memberEnergyCompanies, new Function<LiteStarsEnergyCompany, Integer>() {
                @Override
                public Integer apply(LiteStarsEnergyCompany energyCompany) {
                    return energyCompany.getEnergyCompanyId();
                }
            }));
        usableEnergyCompanyIds.add(ec.getId());
        
        return serviceCompanyDao.getAllServiceCompaniesForEnergyCompanies(usableEnergyCompanyIds);
    }
    
    @ModelAttribute(value="warehouses")
    public List<Warehouse> getWarehouses(LiteYukonUser user) {
        return getAvailableWarehouses(user);
    }
    
    private List<Warehouse> getAvailableWarehouses(LiteYukonUser user) {
        
        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany energyCompany = starsDbCache.getEnergyCompany(yukonEnergyCompany);
        List<Warehouse> warehouses = energyCompany.getAllWarehousesDownward();
        
        return warehouses;
    }
    
    /* Init Binder */
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "filterRules.fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "filterRules.programSignupDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "filterRules.deviceStateDateFrom", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "filterRules.deviceStateDateTo", dateValidationType.getPropertyEditor());
        
        datePropertyEditorFactory.setupLocalDatePropertyEditor(binder, userContext, BlankMode.CURRENT);
        
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(key);
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
}