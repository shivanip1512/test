package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.jaxb.DeviceCategories;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.deviceConfiguration.model.CategoryDisplay;
import com.cannontech.web.deviceConfiguration.model.ConfigurationDeviceTypesBackingBean;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean.CategorySelection;
import com.cannontech.web.deviceConfiguration.validation.ConfigurationDeviceTypesValidator;
import com.cannontech.web.deviceConfiguration.validation.DeviceConfigurationValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Controller
@RequestMapping("/config/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationConfigController {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationConfigController.class);
    
    private final static String baseKey = "yukon.web.modules.tools.configs";

    @Autowired private DeviceConfigurationHelper deviceConfigurationHelper;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private ObjectFormattingService formattingService;
    
    @RequestMapping
    public String view(int configId, ModelMap model, FlashScope flashScope, YukonUserContext context) {
        return viewOrEdit(configId, model, flashScope, context, PageEditMode.VIEW);
    }
    
    @RequestMapping
    public String edit(int configId, ModelMap model, FlashScope flashScope, YukonUserContext context) {
        return viewOrEdit(configId, model, flashScope, context, PageEditMode.EDIT);
    }
    
    @RequestMapping
    public String create(ModelMap model, 
                         FlashScope flashScope, 
                         @ModelAttribute ConfigurationDeviceTypesBackingBean configurationDeviceTypesBackingBean, 
                         BindingResult bindingResult) {
        ConfigurationDeviceTypesValidator validator = new ConfigurationDeviceTypesValidator();
        validator.validate(configurationDeviceTypesBackingBean, bindingResult);
        
        if (bindingResult.hasErrors()) {
            // User selected no device types. Try again!
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult, true);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            // We don't have a configId yet.
            setupModelMapForSetup(model, null, configurationDeviceTypesBackingBean);
            return "setup.jsp";
        }
        
        DeviceConfigurationBackingBean deviceConfigurationBackingBean = new DeviceConfigurationBackingBean();
        deviceConfigurationBackingBean.setSupportedTypes(configurationDeviceTypesBackingBean.getSupportedTypes());
        
        for (Entry<PaoType, Boolean> entry : deviceConfigurationBackingBean.getSupportedTypes().entrySet()) {
            if (entry.getValue() != null && entry.getValue() == true) {
                for (com.cannontech.common.pao.definition.model.jaxb.DeviceCategories.Category category : 
                        paoDefinitionDao.getCategoriesForPaoType(entry.getKey())) {
                    String typeStr = category.getType().value();
                    
                    CategoryDisplay categoryDisplay = 
                        new CategoryDisplay(typeStr, deviceConfigurationDao.categoriesExistForType(typeStr));
                    
                    List<CategorySelection> selections = deviceConfigurationBackingBean.getCategorySelections();
                    if (!categoryTypeIncluded(typeStr, selections)) {
                        selections.add(new CategorySelection(categoryDisplay));
                    }
                }
            }
        }
        
        setupModelMap(model, PageEditMode.CREATE, deviceConfigurationBackingBean);
        
        return "configuration.jsp";
    }
    
    @RequestMapping
    public String save(ModelMap model, 
                       FlashScope flashScope, 
                       @ModelAttribute DeviceConfigurationBackingBean deviceConfigurationBackingBean, 
                       BindingResult bindingResult,
                       YukonUserContext context) {
        Set<CategoryType> categoriesForPaoTypes = new HashSet<>();
        
        Map<PaoType, Boolean> supportedTypes = deviceConfigurationBackingBean.getSupportedTypes();
        
        Set<PaoType> supportedPaoTypes = getSupportedPaoTypes(supportedTypes);
        for (com.cannontech.common.pao.definition.model.jaxb.DeviceCategories.Category category : 
             paoDefinitionDao.getCategoriesForPaoTypes(supportedPaoTypes)) {
            categoriesForPaoTypes.add(CategoryType.fromValue(category.getType().value()));
        }
        
        DeviceConfigurationValidator validator = new DeviceConfigurationValidator(categoriesForPaoTypes);
        validator.validate(deviceConfigurationBackingBean, bindingResult);
        
        if (bindingResult.hasErrors()) {
            flashScope.setMessage(
                new YukonMessageSourceResolvable(baseKey + ".errorsExist"), 
                FlashScopeMessageType.ERROR);
            
            // We need to return to the edit view if this config has an Id, create otherwise.
            PageEditMode mode = 
                deviceConfigurationBackingBean.getConfigId() != null ? PageEditMode.EDIT : PageEditMode.CREATE;
            
            setupModelMap(model, mode, deviceConfigurationBackingBean);
            return "configuration.jsp";
        }
        
        List<DeviceConfigCategory> categories = new ArrayList<>();
        for (CategorySelection categorySelection : deviceConfigurationBackingBean.getCategorySelections()) {
            DeviceConfigCategory category = 
                deviceConfigurationDao.getDeviceConfigCategory(categorySelection.getCategoryId());
            
            categories.add(category);
        }
        
        DeviceConfiguration deviceConfiguration = 
            new DeviceConfiguration(
                deviceConfigurationBackingBean.getConfigId(), 
                deviceConfigurationBackingBean.getConfigName(), 
                categories,
                supportedPaoTypes);
        
        int configId = deviceConfigurationDao.saveConfiguration(deviceConfiguration);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.saveSuccess"));

        return viewOrEdit(configId, model, flashScope, context, PageEditMode.VIEW);
    }
    
    @RequestMapping
    public String delete(ModelMap model, FlashScope flashScope, int configId) {
        deviceConfigurationDao.deleteConfiguration(configId);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.deleteSuccess"));
        
        model.clear();
        return "redirect:/deviceConfiguration/home";
    }
    
    @RequestMapping
    public String selectTypes(ModelMap model) {
        setupModelMapForSetup(model, null);
        return "setup.jsp";
    }
    
    @RequestMapping
    public String modifyTypes(int configId, ModelMap model) {
        setupModelMapForSetup(model, configId);
        return "setup.jsp";
    }
    
    private String viewOrEdit(int configId, 
                              ModelMap model, 
                              FlashScope flashScope, 
                              YukonUserContext context, 
                              PageEditMode mode) {
        DeviceConfiguration deviceConfiguration = deviceConfigurationDao.getDeviceConfiguration(configId);

        DeviceConfigurationBackingBean deviceConfigurationBackingBean = new DeviceConfigurationBackingBean();
        deviceConfigurationBackingBean.setConfigId(configId);
        deviceConfigurationBackingBean.setConfigName(deviceConfiguration.getName());

        Set<PaoType> supportedTypes = deviceConfiguration.getSupportedDeviceTypes();
        for (PaoType deviceType : supportedTypes) {
            deviceConfigurationBackingBean.getSupportedTypes().put(deviceType, true);
        }

        handleMissingCategories(flashScope, context, deviceConfiguration, deviceConfigurationBackingBean);

        for (DeviceConfigCategory category : deviceConfiguration.getCategories()) {
            CategoryDisplay categoryDisplay = 
                new CategoryDisplay(
                    category.getCategoryType(), 
                    deviceConfigurationDao.categoriesExistForType(category.getCategoryType()));

            deviceConfigurationBackingBean.getCategorySelections().add(
                new CategorySelection(categoryDisplay, category.getCategoryName(), category.getCategoryId()));
        }

        setupModelMap(model, mode, deviceConfigurationBackingBean);
        return "configuration.jsp";
    }
    
    private Set<CategoryType> validateCategories(DeviceConfiguration deviceConfiguration) {
        // Get the list of categories that SHOULD be present.
        Set<DeviceCategories.Category> categoriesForPaoTypes =
            paoDefinitionDao.getCategoriesForPaoTypes(deviceConfiguration.getSupportedDeviceTypes());
        
        // Function to transform expected types to strings.
        Function<DeviceCategories.Category, CategoryType> expectedToType = 
            new Function<DeviceCategories.Category, CategoryType>() {
                public CategoryType apply(DeviceCategories.Category category) { 
                    return CategoryType.fromValue(category.getType().value()); 
                }
            };
        
        // Get the list of categories that ARE present.
        List<DeviceConfigCategory> categories = deviceConfiguration.getCategories();
        
        // Function to transform present types to strings.
        Function<DeviceConfigCategory, CategoryType> presentToType = 
            new Function<DeviceConfigCategory, CategoryType>() {
                public CategoryType apply(DeviceConfigCategory category) { 
                    return CategoryType.fromValue(category.getCategoryType()); 
                }
            };
        
        Set<CategoryType> expectedTypes = 
                new HashSet<>(Lists.transform(new ArrayList<>(categoriesForPaoTypes), expectedToType));
        Set<CategoryType> presentTypes = new HashSet<>(Lists.transform(categories, presentToType));
            
        if (expectedTypes.equals(presentTypes)) {
            return new HashSet<>();
        }
        
        SetView<CategoryType> setDifference = Sets.difference(expectedTypes, presentTypes);
        
        Set<CategoryType> difference = new HashSet<>();
        return setDifference.copyInto(difference);
    }
    
    private void handleMissingCategories(FlashScope flashScope, 
            YukonUserContext context,
            DeviceConfiguration deviceConfiguration,
            DeviceConfigurationBackingBean deviceConfigurationBackingBean) {
        Set<CategoryType> missingCategories = validateCategories(deviceConfiguration);

        if (!missingCategories.isEmpty()) {
            List<String> categories = new ArrayList<>();

            for (CategoryType type : missingCategories) {
                YukonMessageSourceResolvable ymsr = 
                    new YukonMessageSourceResolvable(baseKey + ".category." + type.value() + ".title");
                categories.add(formattingService.formatObjectAsString(ymsr, context));
            }

            YukonMessageSourceResolvable flashMsg = 
                new YukonMessageSourceResolvable(baseKey + ".config.missingCategories", categories);

            flashScope.setError(flashMsg);

            YukonMessageSourceResolvable noneResolvable = new YukonMessageSourceResolvable(baseKey + ".config.noneSelected");
            String noneSelected = formattingService.formatObjectAsString(noneResolvable, context);

            for (CategoryType categoryType : missingCategories) {
                CategoryDisplay categoryDisplay = 
                    new CategoryDisplay(
                        categoryType.value(),
                        deviceConfigurationDao.categoriesExistForType(categoryType.value()));

                deviceConfigurationBackingBean.getCategorySelections().add(
                    new CategorySelection(categoryDisplay, noneSelected, null));
            }
        }
    }
    
    /**
     * Converts the backing bean version of the supported pao types from the setup page into a set containing
     * only the PaoTypes the user selected.
     * @param supportedTypes The backing bean version of the map containing the pao type information the user specified.
     * @return a set of the keys from the supportedTypes map whose values were true. 
     */
    private Set<PaoType> getSupportedPaoTypes(Map<PaoType, Boolean> supportedTypes) {
        Builder<PaoType> builder = new Builder<>();
        
        for (Entry<PaoType, Boolean> entry : supportedTypes.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == true) {
                builder.add(entry.getKey());
            }
        }
        
        return builder.build();
    }
    
    /**
     * Determines whether or not a categoryType is already present in a list of selections (used to prevent
     * duplication of categories when a user creates a configuration with device types that share category types.)
     * @param categoryType the category type being checked.
     * @param selections the list of current selections being checked again.
     * @return true if the list contains the categoryType already, false otherwise.
     */
    private boolean categoryTypeIncluded(String categoryType, List<CategorySelection> selections) {
        for (CategorySelection categorySelection : selections) {
            if (categoryType.equals(categorySelection.getCategoryDisplay().getCategoryType())) {
                return true;
            }
        }
        
        return false;
    }
    
    private void setupModelMapForSetup(ModelMap model, Integer configId) {
        Set<PaoDefinition> configDefinitions = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION);
        
        ConfigurationDeviceTypesBackingBean backingBean;
        if (configId != null) {
            // Populate the current types.
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            Set<PaoType> supportedDeviceTypes = configuration.getSupportedDeviceTypes();
            
            backingBean = ConfigurationDeviceTypesBackingBean.fromPaoTypes(configDefinitions, supportedDeviceTypes);
        } else {
            backingBean = ConfigurationDeviceTypesBackingBean.fromPaoDefinitions(configDefinitions);
        }
        
        setupModelMapForSetup(model, configId, backingBean);
    }
    
    /**
     * Adds the Device types that support device configuration to the model for the user to choose from.
     * @param model the model map
     */
    private void setupModelMapForSetup(ModelMap model, 
                                       Integer configId, 
                                       ConfigurationDeviceTypesBackingBean backingBean) {
        if (configId != null) {
            model.addAttribute("configId", configId);
        }
        
        // If this came from a use submission the map may have nulls. Make those false instead.
        for (Entry<PaoType, Boolean> entry : backingBean.getSupportedTypes().entrySet()) {
            if (entry.getValue() == null) {
                entry.setValue(false);
            }
        }
        
        // Add the bean to the model.
        model.addAttribute("configurationDeviceTypesBackingBean", backingBean);
        
        // Add the possible types for the checkboxes.
        List<PaoType> rtus = new ArrayList<>();
        List<PaoType> meters = new ArrayList<>();
        List<PaoType> cbcs = new ArrayList<>();
        for (PaoType paoType : backingBean.getSupportedTypes().keySet()) {
            if (paoType.isRtu()) {
                rtus.add(paoType);
            } else if (paoType.isMeter()) {
                meters.add(paoType);
            } else if (paoType.isCbc()) {
                cbcs.add(paoType);
            } else {
                log.error("Invalid paoType " + paoType + " received for device configuration");
            }
        }
        
        Comparator<PaoType> paoTypeAlphaComparator = new Comparator<PaoType>() {
            @Override
            public int compare(PaoType o1, PaoType o2) {
                return o1.getDbString().compareTo(o2.getDbString());
            }
        };
        
        Collections.sort(meters, paoTypeAlphaComparator);
        Collections.sort(cbcs, paoTypeAlphaComparator);
        Collections.sort(rtus, paoTypeAlphaComparator);
        
        model.addAttribute("meters", meters);
        model.addAttribute("cbcs", cbcs);
        model.addAttribute("rtus", rtus);
    }
    
    /**
     * Sets up the model map for rendering a configuration view.
     * @param model the model map
     * @param mode the mode the view will be rendered in
     * @param deviceConfigurationBackingBean the backing bean containing the configuration data.
     */
    private void setupModelMap(ModelMap model, 
                              PageEditMode mode, 
                              DeviceConfigurationBackingBean deviceConfigurationBackingBean) {
        Integer configId = deviceConfigurationBackingBean.getConfigId();
        boolean isDeletable = configId == null ? false : deviceConfigurationDao.isConfigurationDeletable(configId);
        model.addAttribute("isDeletable", isDeletable);

        // Add the backing bean.
        model.addAttribute("deviceConfigurationBackingBean", deviceConfigurationBackingBean);

        model.addAttribute("mode", mode);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
    }
}
