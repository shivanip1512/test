package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DeviceConfigurationCategory;
import com.cannontech.common.device.config.model.DeviceConfigurationCategoryItem;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.model.jaxb.InputBase;
import com.cannontech.common.device.config.model.jaxb.InputMap;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.jaxb.DeviceCategories;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.deviceConfiguration.model.CategoryDisplay;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean.RateBackingBean;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.ConfigurationDeviceTypesBackingBean;
import com.cannontech.web.deviceConfiguration.model.DisplayableConfigurationData;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean;
import com.cannontech.web.deviceConfiguration.model.RateInput;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean.CategorySelection;
import com.cannontech.web.deviceConfiguration.validation.CategoryEditValidator;
import com.cannontech.web.deviceConfiguration.validation.ConfigurationDeviceTypesValidator;
import com.cannontech.web.deviceConfiguration.validation.DeviceConfigurationValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Controller
@RequestMapping("/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationController extends DispatcherServlet {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationController.class);
    
    private final static String baseKey = "yukon.web.modules.tools.configs";
    
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private AuthDao authDao;
    @Autowired private ObjectFormattingService formattingService;
    @Autowired private DeviceConfigurationHelper deviceConfigurationHelper;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    // This will need to be updated if the schedules or rates/times can get to double digit values.
    private static final Pattern touPattern = Pattern.compile("^schedule[0-9](rate|time)[0-9]$");
    
    /**
     * A small class that wraps a JAXB category type to provide us the getValue() method
     * since JAXB only gives us value(), which can't be accessed in JSP-EL world.
     */
    public final static class DisplayableCategoryType {
        CategoryType categoryType;
        
        public DisplayableCategoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
        }

        public String getValue() {
            return categoryType.value();
        }
    }
    
    @RequestMapping
    public String home(ModelMap model, YukonUserContext context) {
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();

        List<DisplayableConfigurationData> displayables = new ArrayList<>();
        for (LightDeviceConfiguration config : configurations) {
            displayables.add(
                new DisplayableConfigurationData(
                    config, 
                    deviceConfigurationDao.getNumberOfDevicesForConfiguration(config.getConfigurationId())));
        }
        
        model.addAttribute("configurations", displayables);
        
        model.addAttribute("categories", deviceConfigurationDao.getAllDeviceConfigurationCategories());
        
        List<DisplayableCategoryType> categoryTypes = new ArrayList<>();
        for (CategoryType type : CategoryType.values()) {
            categoryTypes.add(new DisplayableCategoryType(type));
        }

        model.addAttribute("categoryTypes", categoryTypes);
        
        setupModelMap(model, PageEditMode.VIEW);
        return "home.jsp";
    }
    
    @RequestMapping
    public String selectTypes(ModelMap model) {
        setupModelMapForConfigurationSetup(model, null);
        return "create.jsp";
    }
    
    @RequestMapping
    public String modifyTypes(int configId, ModelMap model) {
        setupModelMapForConfigurationSetup(model, configId);
        return "create.jsp";
    }

    @RequestMapping
    public String createConfiguration(ModelMap model, 
                              FlashScope flashScope, 
                              @ModelAttribute ConfigurationDeviceTypesBackingBean configurationDeviceTypesBackingBean, 
                              BindingResult bindingResult) {
        ConfigurationDeviceTypesValidator validator = new ConfigurationDeviceTypesValidator();
        validator.validate(configurationDeviceTypesBackingBean, bindingResult);
        
        if (bindingResult.hasErrors()) {
            // User selected no device types. Try again!
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult, true);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            setupModelMapForConfigurationSetup(model, null);
            return "create.jsp";
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
        
        setupModelMapForConfiguration(model, PageEditMode.CREATE, deviceConfigurationBackingBean);
        return "configuration.jsp";
    }
    
    @RequestMapping
    public String editConfiguration(int configId, ModelMap model, FlashScope flashScope, YukonUserContext context) {
        return viewOrEditConfiguration(configId, model, flashScope, context, PageEditMode.EDIT);
    }
    
    private String viewOrEditConfiguration(int configId, 
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
        
        for (DeviceConfigurationCategory category : deviceConfiguration.getCategories()) {
            CategoryDisplay categoryDisplay = 
                new CategoryDisplay(
                    category.getCategoryType(), 
                    deviceConfigurationDao.categoriesExistForType(category.getCategoryType()));
            
            deviceConfigurationBackingBean.getCategorySelections().add(
                new CategorySelection(categoryDisplay, category.getCategoryName(), category.getCategoryId()));
        }
       
        setupModelMapForConfiguration(model, mode, deviceConfigurationBackingBean);
        return "configuration.jsp";
    }

    private void handleMissingCategories(FlashScope flashScope, YukonUserContext context,
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
                new YukonMessageSourceResolvable(baseKey + ".configuration.missingCategories", categories);
            
            flashScope.setError(flashMsg);
            
            YukonMessageSourceResolvable noneResolvable = new YukonMessageSourceResolvable(baseKey + ".noneSelected");
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
    
    @RequestMapping
    public String viewConfiguration(int configId, ModelMap model, FlashScope flashScope, YukonUserContext context) {
        return viewOrEditConfiguration(configId, model, flashScope, context, PageEditMode.VIEW);
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
        List<DeviceConfigurationCategory> categories = deviceConfiguration.getCategories();
        
        // Function to transform present types to strings.
        Function<DeviceConfigurationCategory, CategoryType> presentToType = 
            new Function<DeviceConfigurationCategory, CategoryType>() {
                public CategoryType apply(DeviceConfigurationCategory category) { 
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
    
    @RequestMapping
    public String deleteConfiguration(ModelMap model,
                                      FlashScope flashScope,
                                      int configurationId) {
        deviceConfigurationDao.deleteConfiguration(configurationId);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.deleteSuccess"));
        
        model.clear();
        return "redirect:home";
    }
    
    @RequestMapping
    public String saveConfiguration(ModelMap model, 
                                    FlashScope flashScope, 
                                    @ModelAttribute DeviceConfigurationBackingBean deviceConfigurationBackingBean, 
                                    BindingResult bindingResult) {
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
            
            setupModelMapForConfiguration(model, mode, deviceConfigurationBackingBean);
            return "configuration.jsp";
        }
        
        List<DeviceConfigurationCategory> categories = new ArrayList<>();
        for (CategorySelection categorySelection : deviceConfigurationBackingBean.getCategorySelections()) {
            DeviceConfigurationCategory category = 
                deviceConfigurationDao.getDeviceConfigurationCategory(categorySelection.getCategoryId());
            
            categories.add(category);
        }
        
        DeviceConfiguration deviceConfiguration = 
            new DeviceConfiguration(
                deviceConfigurationBackingBean.getConfigId(), 
                deviceConfigurationBackingBean.getConfigName(), 
                categories,
                supportedPaoTypes);
        
        deviceConfigurationDao.saveConfiguration(deviceConfiguration);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.saveSuccess"));

        model.clear();
        return "redirect:home";
    }
    
    @RequestMapping
    public String createCategory(ModelMap model, String categoryType, YukonUserContext context) {
        Category category = deviceConfigurationDao.getCategoryByType(CategoryType.fromValue(categoryType));
        
        CategoryEditBean categoryEditBean = new CategoryEditBean();
        categoryEditBean.setCategoryType(categoryType);

        for (InputBase inputBase : category.getIntegerOrFloatOrBoolean()) {
            if (inputBase.getClass() == InputMap.class) {
                // Create the backing bean to represent the collection of rates.
                RateBackingBean rateBackingBean = new RateBackingBean();
                
                List<InputMap.Entry> entries = ((InputMap)inputBase).getEntry();
                for (InputMap.Entry entry : entries) {
                    rateBackingBean.getRateInputs().put(entry.getField(), new RateInput());
                }
                
                categoryEditBean.getScheduleInputs().put(inputBase.getField(), rateBackingBean);
            }
            categoryEditBean.getCategoryInputs().put(inputBase.getField(), inputBase.getDefault());
        }
        
        setupModelMapForCategory(model, PageEditMode.CREATE, categoryEditBean, context);
        return "category.jsp";
    }
    
    @RequestMapping
    public String editCategory(ModelMap model, int categoryId, YukonUserContext context) {
        return navigateToViewOrEditCategory(model, categoryId, context, PageEditMode.EDIT);
    }
    
    @RequestMapping
    public String viewCategory(ModelMap model, int categoryId, YukonUserContext context) {
        return navigateToViewOrEditCategory(model, categoryId, context, PageEditMode.VIEW);
    }
    
    @RequestMapping
    public String deleteCategory(ModelMap model, 
                                 FlashScope flashScope, 
                                 int categoryId) {
        deviceConfigurationDao.deleteCategory(categoryId);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.deleteSuccess"));
        
        model.clear();
        return "redirect:home";
    }

    @RequestMapping
    public String saveCategory(ModelMap model, 
                               FlashScope flashScope, 
                               @ModelAttribute CategoryEditBean categoryEditBean, 
                               BindingResult bindingResult, 
                               YukonUserContext context) {
        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());
        
        CategoryTemplate categoryTemplate = 
                deviceConfigurationHelper.createTemplate(deviceConfigurationDao.getCategoryByType(type), context);

        CategoryEditValidator validator = new CategoryEditValidator(categoryTemplate);
        validator.validate(categoryEditBean, bindingResult);
        
        if (bindingResult.hasErrors()) {
            flashScope.setMessage(
                new YukonMessageSourceResolvable(baseKey + ".errorsExist"), 
                FlashScopeMessageType.ERROR);
            
            // Return to the edit view if the category has an Id, create otherwise.
            PageEditMode mode = categoryEditBean.getCategoryId() != null ? PageEditMode.EDIT : PageEditMode.CREATE;
            
            setupModelMapForCategory(model, mode, categoryEditBean, context);
            return "category.jsp";
        }
        
        deviceConfigurationDao.saveCategory(categoryEditBean.getModelObject());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.saveSuccess"));
        
        model.clear();
        return "redirect:home";
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
    
    /**
     * Converts the backing bean version of the supported pao types from the setup page into a set containing
     * only the PaoTypes the user selected.
     * @param supportedTypes The backing bean version of the map containing the pao type information the user specified.
     * @return a set of the keys from the supportedTypes map whose values were true. 
     */
    private Set<PaoType> getSupportedPaoTypes(Map<PaoType, Boolean> supportedTypes) {
        Set<PaoType> supportedPaoTypes = new HashSet<>();
        
        for (Entry<PaoType, Boolean> entry : supportedTypes.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == true) {
                supportedPaoTypes.add(entry.getKey());
            }
        }
        
        return Collections.unmodifiableSet(supportedPaoTypes);
    }
    
    /**
     * Converts the database representation of a category into the appropriate backing bean for the view to use.
     * @param category the database representation of the category.
     * @return the populated backing bean representing the data of the database instance of the category.
     */
    private CategoryEditBean createCategoryEditBean(DeviceConfigurationCategory category) {
        CategoryEditBean categoryEditBean = new CategoryEditBean();
        
        categoryEditBean.setCategoryId(category.getCategoryId());
        categoryEditBean.setCategoryName(category.getCategoryName());
        categoryEditBean.setCategoryType(category.getCategoryType());
        
        List<DeviceConfigurationCategoryItem> deviceConfigurationItems = category.getDeviceConfigurationItems();
        
        Map<String, String> categoryItems = new TreeMap<>();
        Map<String, String> scheduleItems = new TreeMap<>();
        
        for (DeviceConfigurationCategoryItem item : deviceConfigurationItems) {
            Matcher m = touPattern.matcher(item.getFieldName());
            if (m.matches()) {
                scheduleItems.put(item.getFieldName(), item.getValue());
            } else {
                categoryItems.put(item.getFieldName(), item.getValue());
            }
        }

        categoryEditBean.setCategoryInputs(categoryItems);
        categoryEditBean.setScheduleInputs(convertTouScheduleItems(scheduleItems));
        
        return categoryEditBean;
    }
    
    /**
     * Converts the schedule items stored in the database into renderable objects for the view. 
     * @param scheduleItems the map of schedule fields and their respective values.
     * @return a view-appropriate map containing the information from the scheduleItems map.
     */
    private Map<String, RateBackingBean> convertTouScheduleItems(Map<String, String> scheduleItems) {
        Map<String, RateBackingBean> scheduleInputs = new TreeMap<>();

        for (int schedule = 1; schedule <= 4; schedule++) {
            RateBackingBean backingBean = new RateBackingBean();
            
            Map<String, RateInput> rateInputs = new TreeMap<>();
            for (int field = 0; field <= (scheduleItems.size() / (4 * 2)); field++) {
                RateInput rateInput = new RateInput();
                
                String timeField = "schedule" + Integer.toString(schedule) + "time" + Integer.toString(field);
                String rateField = "schedule" + Integer.toString(schedule) + "rate" + Integer.toString(field);
                
                rateInput.setTime(scheduleItems.get(timeField));
                rateInput.setRate(scheduleItems.get(rateField));
            
                rateInputs.put("time" + Integer.toString(field), rateInput);
            }
            
            backingBean.setRateInputs(rateInputs);

            scheduleInputs.put("schedule" + Integer.toString(schedule), backingBean);
        }
        
        return scheduleInputs;
    }
    
    /**
     * Navigate the user to the view or edit rendering of the category.
     * @param model the model map
     * @param categoryId the category id of the category being viewed or edited
     * @param context the user context
     * @param mode the editing mode of the page.
     * @return the jsp of the view
     */
    private String navigateToViewOrEditCategory(ModelMap model, int categoryId, YukonUserContext context, PageEditMode mode) {
        DeviceConfigurationCategory category = deviceConfigurationDao.getDeviceConfigurationCategory(categoryId);
        
        CategoryEditBean categoryEditBean = createCategoryEditBean(category);

        setupModelMapForCategory(model, mode, categoryEditBean, context);
        
        return "category.jsp";
    }
    
    /**
     * Populates the model map for the view. Adds the role property for config editing and the page edit mode
     * to the model.
     * @param model the model map.
     * @param mode the page edit mode for the view to be rendered in.
     */
    private void setupModelMap(ModelMap model, PageEditMode mode) {
        model.addAttribute("mode", mode);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG.name());
    }
    
    /**
     * Sets up the model for rendering a category view.
     * @param model the model map
     * @param mode the mode the view will be rendered in.
     * @param categoryEditBean the backing bean containing the category data.
     * @param context the user context
     */
    private void setupModelMapForCategory(ModelMap model, 
                                          PageEditMode mode, 
                                          CategoryEditBean categoryEditBean, 
                                          YukonUserContext context) {
        // This category can be deleted if it exists in the database and has no configuration assignments.
        boolean isDeletable = categoryEditBean.getCategoryId() == null ? false : 
                deviceConfigurationDao.getNumberOfConfigurationsForCategory(categoryEditBean.getCategoryId()) == 0;
           
        model.addAttribute("isDeletable", isDeletable);
        
        // Add the bean itself.
        model.addAttribute("categoryEditBean", categoryEditBean);
        
        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());
        CategoryTemplate categoryTemplate = 
                deviceConfigurationHelper.createTemplate(deviceConfigurationDao.getCategoryByType(type), context);
        
        // Add the category template.
        model.addAttribute("categoryTemplate", categoryTemplate);
        
        setupModelMap(model, mode);
    }
    
    /**
     * Sets up the model map for rendering a configuration view.
     * @param model the model map
     * @param mode the mode the view will be rendered in
     * @param deviceConfigurationBackingBean the backing bean containing the configuration data.
     */
    private void setupModelMapForConfiguration(ModelMap model, 
                                               PageEditMode mode, 
                                               DeviceConfigurationBackingBean deviceConfigurationBackingBean) {
        Integer configId = deviceConfigurationBackingBean.getConfigId();
        boolean isDeletable = configId == null ? false : deviceConfigurationDao.isConfigurationDeletable(configId);
        model.addAttribute("isDeletable", isDeletable);
        
        // Add the backing bean.
        model.addAttribute("deviceConfigurationBackingBean", deviceConfigurationBackingBean);
        
        setupModelMap(model, mode);
    }
    
    /**
     * Adds the Device types that support device configuration to the model for the user to choose from.
     * @param model the model map
     */
    private void setupModelMapForConfigurationSetup(ModelMap model, Integer configId) {
        Set<PaoDefinition> configDefinitions = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION);
        
        ConfigurationDeviceTypesBackingBean backingBean = new ConfigurationDeviceTypesBackingBean(configDefinitions);
        
        if (configId != null) {
            model.addAttribute("configId", configId);
            
            // Populate the current types.
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            Set<PaoType> supportedDeviceTypes = configuration.getSupportedDeviceTypes();
            
            for (PaoType deviceType : supportedDeviceTypes) {
                backingBean.getSupportedTypes().put(deviceType, true);
            }
        }
        
        model.addAttribute("configurationDeviceTypesBackingBean", backingBean);

        // Add the possible types for the checkboxes.
        List<PaoType> rtus = new ArrayList<>();
        List<PaoType> meters = new ArrayList<>();
        List<PaoType> cbcs = new ArrayList<>();
        for (PaoDefinition definition : configDefinitions) {
            PaoType paoType = definition.getType();
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
        
        model.addAttribute("meters", meters);
        model.addAttribute("cbcs", cbcs);
        model.addAttribute("rtus", rtus);
    }
}
