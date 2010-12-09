package com.cannontech.web.stars.dr.operator.inventoryOperations;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterRuleType;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventoryOperations.service.InventoryOperationsFilterService;

@Controller
@CheckRole(YukonRole.INVENTORY)
@RequestMapping(value = "/operator/inventory/inventoryOperations/*")
public class InventoryFilterController {
    
    private InventoryOperationsFilterService inventoryOperationsFilterService;
    private MemoryCollectionProducer memoryCollectionProducer;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonListDao yukonListDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private FilterModelValidator filterModelValidator;
    
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
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        YukonSelectionList devTypeList = energyCompany.getYukonSelectionList(YukonSelectionListEnum.DEVICE_TYPE.getListName());
        newRule.setDeviceType(devTypeList.getYukonListEntries().get(0));
        
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
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        Set<InventoryIdentifier> inventory = inventoryOperationsFilterService.getInventory(filterModel.getFilterMode(), 
                                                                                           filterModel.getFilterRules(), energyCompany.getDefaultDateTimeZone(), userContext);
        
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
    
    /* Remove Rule - This guy as no 'params' in the method signature because the form had to be submitted via javascript for removes. */
    @RequestMapping(value = "applyFilter", method=RequestMethod.POST)
    public String remove(@ModelAttribute("filterModel") FilterModel filterModel, BindingResult bindingResult,
                              ModelMap modelMap, YukonUserContext userContext, int removeRule) {
        filterModel.getFilterRules().remove(removeRule);
        setupFilterSelectionModelMap(modelMap, userContext);
        return "operator/inventory/inventoryOperations/setupFilterRules.jsp";
    }
    
    public void setupFilterSelectionModelMap(ModelMap modelMap, YukonUserContext userContext) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        modelMap.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyID());
    }
    
    @ModelAttribute(value="ruleTypes")
    public List<FilterRuleType> getRuleTypes() {
        return Arrays.asList(FilterRuleType.values());
    }
    
    @ModelAttribute(value="filterModes")
    public List<FilterMode> getFilterModes() {
        List<FilterMode> filterModes = Arrays.asList(FilterMode.values());
        return filterModes;
    }
    
    /* Init Binder */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "filterRules.fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "filterRules.programSignupDate", dateValidationType.getPropertyEditor());
        
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver("yukon.web.modules.operator.filterSelection.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
        
        binder.registerCustomEditor(YukonListEntry.class, "filterRules.deviceType", new PropertyEditorSupport() {
            @Override
            public void setAsText(String deviceTypeEntryId) throws IllegalArgumentException {
                YukonListEntry entry = yukonListDao.getYukonListEntry(Integer.parseInt(deviceTypeEntryId));
                setValue(entry);
            }
            @Override
            public String getAsText() {
                YukonListEntry entry = (YukonListEntry) getValue();
                return Integer.toString((entry.getEntryID()));
            }
        });
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
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setFilterModelValidator(FilterModelValidator filterModelValidator) {
        this.filterModelValidator = filterModelValidator;
    }
    
}