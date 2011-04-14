package com.cannontech.web.stars.dr.operator.inventoryOperations;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
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
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterRuleType;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventoryOperations.service.InventoryOperationsFilterService;
import com.cannontech.web.stars.dr.operator.inventoryOperations.service.impl.InvalidSerialNumberRangeDataException;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
@RequestMapping(value = "/operator/inventory/inventoryOperations/*")
public class InventoryFilterController {
    
    private ApplianceCategoryDao applianceCategoryDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private ECMappingDao ecMappingDao;
    private InventoryOperationsFilterService inventoryOperationsFilterService;
    private MemoryCollectionProducer memoryCollectionProducer;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private FilterModelValidator filterModelValidator;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    /* Setup Filter Rules */
    @RequestMapping(value = "setupFilterRules")
    public String setupFilterRules(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext, String filterButton) 
            throws ServletRequestBindingException, CollectionCreationException {
        
        FilterModel filterModel = new FilterModel();
        setupFilterSelectionModelMap(modelMap, userContext);
        modelMap.addAttribute("filterModel", filterModel);
        return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
    }
    
    /* Add Filter Rule */
    @RequestMapping(value = "applyFilter", method=RequestMethod.POST, params="addButton")
    public String addFilterRow(@ModelAttribute("filterModel") FilterModel filterModel, 
                               HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext, String ruleType) {

        /* Add a row and return to filter setup page. */
        FilterRuleType filterRuleType = FilterRuleType.valueOf(ruleType);
        RuleModel newRule = new RuleModel(filterRuleType);
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        YukonSelectionList devTypeList = energyCompany.getYukonSelectionList(YukonSelectionListEnum.DEVICE_TYPE.getListName());
        newRule.setDeviceType(devTypeList.getYukonListEntries().get(0).getEntryID());
        
        filterModel.getFilterRules().add(newRule);

        setupFilterSelectionModelMap(modelMap, userContext);
        return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
    }
    
    /* Cancel */
    @RequestMapping(value = "applyFilter", method=RequestMethod.POST, params="cancelButton")
    public String cancel(@ModelAttribute("filterModel") FilterModel filterModel) {
        return "redirect:home";
    }
    
    /* Apply Filter */
    @RequestMapping(value = "applyFilter", method=RequestMethod.POST, params="apply")
    public String applyFilter(@ModelAttribute("filterModel") FilterModel filterModel, BindingResult bindingResult, FlashScope flashScope,
                              HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ParseException {
        
        filterModelValidator.validate(filterModel, bindingResult);
        if(bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupFilterSelectionModelMap(modelMap, userContext);
            return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
        }
        
        Set<InventoryIdentifier> inventory = null;
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        try {
            inventory = inventoryOperationsFilterService.getInventory(filterModel.getFilterMode(), 
                                filterModel.getFilterRules(), energyCompany.getDefaultDateTimeZone(), userContext);
        } catch (InvalidSerialNumberRangeDataException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.filterSelection.error.invalidSerialNumbers"));
            setupFilterSelectionModelMap(modelMap, userContext);
            return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
        }
        
        if(inventory.isEmpty()) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.filterSelection.error.noInventory"));
            setupFilterSelectionModelMap(modelMap, userContext);
            return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
        }
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String filterDescription = messageSourceAccessor.getMessage("yukon.common.collection.inventory.filterBased");
        
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(inventory.iterator(), filterDescription);
        modelMap.addAttribute("inventoryCollection", temporaryCollection);
        modelMap.addAllAttributes(temporaryCollection.getCollectionParameters());
        return "redirect:inventoryActions";
    }
    
    /* Remove Rule - This guy as no 'params' in the @RequestMapping because the form had to be submitted via javascript for removes. */
    @RequestMapping(value = "applyFilter", method=RequestMethod.POST)
    public String remove(@ModelAttribute("filterModel") FilterModel filterModel, BindingResult bindingResult,
                              ModelMap modelMap, YukonUserContext userContext, int removeRule) {
        filterModel.getFilterRules().remove(removeRule);
        setupFilterSelectionModelMap(modelMap, userContext);
        return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
    }
    
    public void setupFilterSelectionModelMap(ModelMap modelMap, YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        modelMap.addAttribute("energyCompanyId", yukonEnergyCompany.getEnergyCompanyId());
    }
    
    /* Global Model Attributes */
    @ModelAttribute(value="ruleTypes")
    public List<FilterRuleType> getRuleTypes(YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        
        List<FilterRuleType> ruleTypes = Lists.newArrayList(FilterRuleType.values());
        SerialNumberValidation value = energyCompanyRolePropertyDao.getPropertyEnumValue(
            YukonRoleProperty.SERIAL_NUMBER_VALIDATION, SerialNumberValidation.class, yukonEnergyCompany);
        
        if (value == SerialNumberValidation.ALPHANUMERIC) {
            ruleTypes.remove(FilterRuleType.SERIAL_NUMBER_RANGE);
        }
        
        return ruleTypes;
    }
    
    @ModelAttribute(value="filterModes")
    public List<FilterMode> getFilterModes() {
        List<FilterMode> filterModes = Arrays.asList(FilterMode.values());
        return filterModes;
    }
    
    @ModelAttribute(value="applianceTypes")
    public List<ApplianceCategory> getApplianceTypes(YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        List<ApplianceCategory> applianceCategories = applianceCategoryDao.getApplianceCategoriesByECId(energyCompany.getEnergyCompanyId());
        return applianceCategories;
    }

    @ModelAttribute(value="customerTypes")
    public List<YukonListEntry> getCustomerTypes(YukonUserContext userContext) {

        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        YukonSelectionList ciCustTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE, true, true);
        List<YukonListEntry> customerTypes = Lists.newArrayListWithCapacity(ciCustTypes.getYukonListEntries().size() + 1);
        
        // Add residential customer type
        YukonListEntry residentialCustomerType = createResidentialYukonListEntry(userContext);
        customerTypes.add(residentialCustomerType);

        // Add commercial customer types 
        customerTypes.addAll(ciCustTypes.getYukonListEntries());
        
        return customerTypes;
    }

    /**
     * @param userContext
     * @return
     */
    @ModelAttribute(value="energyCompanies")
    public List<YukonEnergyCompany> getEnergyCompanies(YukonUserContext userContext) {
        List<YukonEnergyCompany> energyCompanies = Lists.newArrayList();
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        Set<YukonEnergyCompany> childEnergyCompanies = ecMappingDao.getChildEnergyCompanies(yukonEnergyCompany.getEnergyCompanyId());
        energyCompanies.addAll(childEnergyCompanies);
        
        return energyCompanies;
    }
    
    @ModelAttribute(value="serviceCompanies")
    public List<LiteServiceCompany> getServiceCompanies(YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        List<LiteServiceCompany> serviceCompanies = energyCompany.getAllServiceCompaniesUpDown();
        
        return serviceCompanies;
    }

    @ModelAttribute(value="warehouses")
    public List<Warehouse> getWarehouses(YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        List<Warehouse> warehouses = energyCompany.getAllWarehousesDownward();
        
        return warehouses;
    }
    
    /* Init Binder */
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "filterRules.fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "filterRules.programSignupDate", dateValidationType.getPropertyEditor());

        binder.registerCustomEditor(LocalDate.class, "filterRules.deviceStateDateFrom", 
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));
        binder.registerCustomEditor(LocalDate.class, "filterRules.deviceStateDateTo", 
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));

        
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver("yukon.web.modules.operator.filterSelection.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }

    /**
     * This method creates a residential mock yukonListEntry which can be used with the CICustomerType
     * selection list to create a drop down that has all possible customer types.
     */
    private YukonListEntry createResidentialYukonListEntry(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String residentialCustomerTypeStr = messageSourceAccessor.getMessage("yukon.web.modules.operator.filterSelection.filterEntry.residential");
            
        YukonListEntry residentialCustomerType = new YukonListEntry();
        residentialCustomerType.setEntryID(RuleModel.RESIDENTIAL_ENTRY_ID);
        residentialCustomerType.setEntryText(residentialCustomerTypeStr);
        return residentialCustomerType;
    }
    
    /* DI Setters */
    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
    
    @Autowired
    public void setInventoryOperationsFilterService(InventoryOperationsFilterService inventoryOperationsFilterService) {
        this.inventoryOperationsFilterService = inventoryOperationsFilterService;
    }
    
    @Autowired
    public void setMemoryCollectionProducer(MemoryCollectionProducer memoryCollectionProducer) {
        this.memoryCollectionProducer = memoryCollectionProducer;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setFilterModelValidator(FilterModelValidator filterModelValidator) {
        this.filterModelValidator = filterModelValidator;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
}