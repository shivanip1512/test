package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidConfigurationRemovalException;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.ConfigurationCategory;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.deviceConfiguration.model.CategoryDisplay;
import com.cannontech.web.deviceConfiguration.model.ConfigCategories;
import com.cannontech.web.deviceConfiguration.model.ConfigCategories.CategorySelection;
import com.cannontech.web.deviceConfiguration.model.DeviceConfig;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigTypes;
import com.cannontech.web.deviceConfiguration.validation.DeviceConfigurationValidator;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Controller
@RequestMapping("/config/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationConfigController {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationConfigController.class);
    
    private final static String baseKey = "yukon.web.modules.tools.configs";

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigurationService deviceConfigurationService;
    @Autowired private ObjectFormattingService formattingService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    private final Comparator<PaoType> paoTypeAlphaComparator = new Comparator<PaoType>() {
        @Override
        public int compare(PaoType o1, PaoType o2) {
            return o1.getDbString().compareTo(o2.getDbString());
        }
    };
    
    @RequestMapping("view")
    public String view(int configId, ModelMap model, FlashScope flashScope, YukonUserContext context) {
        return viewOrEdit(configId, model, flashScope, context, PageEditMode.VIEW);
    }
    
    @RequestMapping("edit")
    public String edit(int configId, ModelMap model, FlashScope flashScope, YukonUserContext context) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_EDIT_CONFIG, context.getYukonUser());
        return viewOrEdit(configId, model, flashScope, context, PageEditMode.EDIT);
    }
    
    @RequestMapping("create")
    public String create(ModelMap model) {
        setupModelMap(model,
                      PageEditMode.CREATE,
                      null,  // no config id
                      new DeviceConfig(),
                      null,  // no device types backing bean
                      null); // no categories backing bean.
        
        return "configuration.jsp";
    }
    
    @RequestMapping("save")
    public String save(ModelMap model,
                       FlashScope flashScope,
                       @ModelAttribute DeviceConfig deviceConfig,
            BindingResult bindingResult) {
        DeviceConfigurationValidator validator = new DeviceConfigurationValidator();
        validator.validate(deviceConfig, bindingResult);
        
        if (bindingResult.hasErrors()) {
            flashScope.setMessage(new YukonMessageSourceResolvable(baseKey + ".errorsExist"), FlashScopeMessageType.ERROR);
            return showConfigurationPageForErrorMessage(model, deviceConfig);
            // We need to return to the edit view if this config has an Id, create otherwise.
        }

        String configName = deviceConfig.getConfigName();
        if (!(DeviceGroupUtil.isValidName(configName))) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.error.deviceGroupName.containsIllegalChars"));
            return showConfigurationPageForErrorMessage(model, deviceConfig);
        }

        try {
            int configId =
                deviceConfigurationService.saveConfigurationBase(deviceConfig.getConfigId(),
                                                                 deviceConfig.getConfigName(),
                                                                 deviceConfig.getDescription());
            
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.saveSuccess"));

            model.clear();
            return "redirect:view?configId=" + configId;
        } catch (DuplicateException de) {
            // The user specified a name that already exists.
            log.debug("Creation of a device configuration with an already existing name was attempted.");
            
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".config.nameExists"));
            if (deviceConfig.getConfigId() == null) {
                return "redirect:create";
            } else {
                return "redirect:edit?configId=" + deviceConfig.getConfigId();
            }
        } catch (DataIntegrityViolationException e) {
            // Some other error occurred.
            log.debug("Exception caught during the configuration save process.", e);
            
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".config.errorOccurred"));
            if (deviceConfig.getConfigId() == null) {
                return "redirect:create";
            } else {
                return "redirect:edit?configId=" + deviceConfig.getConfigId();
            }
        }
    }
    
    /**
     * Sets up the model map for rendering a configuration view.
     * @param model the model map
     * @param deviceConfig the backing bean containing the configuration data.
     */
    
    private String showConfigurationPageForErrorMessage(ModelMap model,
                                                        DeviceConfig deviceConfig) {
        PageEditMode mode =
            deviceConfig.getConfigId() != null ? PageEditMode.EDIT
                    : PageEditMode.CREATE;

        setupModelMap(model,
                      mode,
                      deviceConfig.getConfigId(),
                      deviceConfig,
                      null,
                      null);

        return "configuration.jsp";
    }

    @RequestMapping("swapCategory")
    public String swapCategory(ModelMap model,
                               FlashScope flashScope,
                               YukonUserContext context,
                               int configId,
                               int newCategoryId,
                               String categoryType) {
        CategoryType type = CategoryType.fromValue(categoryType);
        
        deviceConfigurationService.changeCategoryAssignment(configId, newCategoryId, type);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.swapSuccess"));
        
        return viewOrEdit(configId, model, flashScope, context, PageEditMode.VIEW);
    }
    
    @RequestMapping("processAddTypes")
    public String processAddTypes(ModelMap model, int configId) {
        model.addAttribute("configId", configId);
        
        setupModelMapForSetup(model, configId);
        return "setup.jsp";
    }
    
    @RequestMapping("addSupportedTypes")
    public String addSupportedTypes(ModelMap model,
                                    FlashScope flashScope,
                                    @ModelAttribute DeviceConfigTypes deviceConfigTypes) {
        
        Set<PaoType> supportedPaoTypes = 
                deviceConfigTypes.getSupportedTypes().entrySet().stream()
                    .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                    .map(Entry::getKey)
                    .collect(Collectors.toSet()); 
        
        int configId = deviceConfigTypes.getConfigId();
        
        SetView<PaoType> addTypes =
            Sets.difference(supportedPaoTypes, deviceConfigurationDao.getSupportedTypesForConfiguration(configId));
        
        if (!addTypes.isEmpty()) {
            boolean noTypesAdded = deviceConfigurationDao.getCategoryDifferenceForPaoTypesAdd(addTypes, configId).isEmpty();
            
            deviceConfigurationDao.addSupportedDeviceTypes(configId, addTypes);
            
            List<PaoType> addedTypes = new ArrayList<>(addTypes);
            Collections.sort(addedTypes, paoTypeAlphaComparator);
            
            List<String> dbTypes = Lists.transform(addedTypes, PaoType::getDbString);
            
            if (noTypesAdded) {
                String key = baseKey + ".config.addTypeSuccess";
                flashScope.setConfirm(new YukonMessageSourceResolvable(key, dbTypes));
            } else {
                String key = baseKey + ".config.addTypeWarning";
                flashScope.setWarning(new YukonMessageSourceResolvable(key, dbTypes));
            }
        }
        
        model.clear();
        return "redirect:view?configId=" + configId;
    }

    @RequestMapping("removeSupportedType")
    public String removeSupportedType(ModelMap model,
                                      FlashScope flashScope,
                                      int configId,
                                      PaoType paoType) {

        try {
            deviceConfigurationService.removeSupportedDeviceType(configId, paoType);
        } catch (InvalidConfigurationRemovalException e) {

            String key = baseKey + ".config.removeTypeFailed";
            flashScope.setError(new YukonMessageSourceResolvable(key, paoType.getDbString()));

            return "redirect:view?configId=" + configId;
        }

        String key = baseKey + ".config.removeTypeSuccess";
        flashScope.setConfirm(new YukonMessageSourceResolvable(key, paoType.getDbString()));
        
        model.clear();
        return "redirect:view?configId=" + configId;
    }
    
    @RequestMapping("delete")
    public String delete(ModelMap model, FlashScope flashScope, int configId) {

        try {
            deviceConfigurationService.deleteConfiguration(configId);
        } catch (InvalidConfigurationRemovalException e) {

            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".config.deleteFailed"));

            return "redirect:view?configId=" + configId;
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".config.deleteSuccess"));
        model.clear();

        return "redirect:/deviceConfiguration/home";
    }
    
    private String viewOrEdit(int configId,
                              ModelMap model,
                              FlashScope flashScope,
                              YukonUserContext context,
                              PageEditMode mode) {
        DeviceConfiguration config = deviceConfigurationDao.getDeviceConfiguration(configId);

        // Setup the DeviceConfig
        DeviceConfig deviceConfig = setupConfigBackingBean(configId, config);

        // Setup the DeviceConfigTypes
        DeviceConfigTypes deviceConfigTypes = setupTypesBackingBean(config);
        
        // Setup the ConfigCategories
        ConfigCategories configCategories =
            setupCategoriesBackingBean(flashScope, context, config);
        
        setupModelMap(model,
                      mode,
                      configId,
                      deviceConfig,
                      deviceConfigTypes,
                      configCategories);
        
        return "configuration.jsp";
    }
    
    private ConfigCategories setupCategoriesBackingBean(FlashScope flashScope,
                                                                          YukonUserContext context,
                                                                          DeviceConfiguration config) {
        ConfigCategories configCategories =
            new ConfigCategories();
        
        handleMissingCategories(flashScope, context, config, configCategories);

        List<CategorySelection> selections = configCategories.getCategorySelections();
        for (DeviceConfigCategory category : config.getCategories()) {
            CategoryDisplay categoryDisplay =
                new CategoryDisplay(
                    category.getCategoryType(),
                    deviceConfigurationDao.categoriesExistForType(category.getCategoryType()));

            Boolean othersExist = category.getCategoryId() != null ?
                    deviceConfigurationDao.otherCategoriesExistForType(category.getCategoryId()) : null;
            
            selections.add(
                new CategorySelection(
                    categoryDisplay,
                    category.getCategoryName(),
                    category.getCategoryId(),
                    category.getDescription(),
                    othersExist));
        }
        
        List<CategorySelection> orderedSelections =
            formattingService.sortDisplayableValues(selections, null, null, context);
        
        configCategories.setCategorySelections(orderedSelections);
        
        configCategories.setConfigId(config.getConfigurationId());

        return configCategories;
    }

    private DeviceConfigTypes setupTypesBackingBean(DeviceConfiguration config) {
        DeviceConfigTypes deviceConfigTypes =
            new DeviceConfigTypes();
        
        Set<PaoType> supportedTypes = config.getSupportedDeviceTypes();
        List<PaoType> sortedTypes = Lists.newArrayList(supportedTypes);
        Collections.sort(sortedTypes, paoTypeAlphaComparator);
        for (PaoType deviceType : sortedTypes) {
            deviceConfigTypes.getSupportedTypes().put(deviceType, true);
        }
        
        Map<String, Collection<PaoType>> types = 
                collectCategoryMap(paoDefinitionDao.getCategoryToPaoTypeMap().entries());
        
        deviceConfigTypes.setTypesByCategory(types);
        
        Set<PaoType> allTypes = getAllConfigurationPaoTypes();
        
        List<PaoType> availableTypes = new ArrayList<>(Sets.difference(allTypes, supportedTypes));
        Collections.sort(availableTypes, paoTypeAlphaComparator);
        
        deviceConfigTypes.setAvailableTypes(availableTypes);
        
        return deviceConfigTypes;
    }

    private Set<PaoType> getAllConfigurationPaoTypes() {
        return paoDefinitionDao.getCreatablePaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION).stream()
            .map(PaoDefinition::getType)
            .collect(Collectors.toSet());
    }
    
    private DeviceConfig setupConfigBackingBean(int configId, DeviceConfiguration deviceConfiguration) {

        DeviceConfig deviceConfig = new DeviceConfig();
        deviceConfig.setConfigId(configId);
        deviceConfig.setConfigName(deviceConfiguration.getName());
        deviceConfig.setDescription(deviceConfiguration.getDescription());
        
        return deviceConfig;
    }
    
    private Collection<ConfigurationCategory> validateCategories(DeviceConfiguration deviceConfiguration) {
        // Get the list of categories that apply for the supported device types.
        Map<String, ConfigurationCategory> categoriesForPaoTypes = 
                paoDefinitionDao.getCategoriesForPaoTypes(deviceConfiguration.getSupportedDeviceTypes()).stream()
                    .collect(Collectors.toMap(
                        c -> c.getType().value(), 
                        Function.identity()));
        
        // Get the list of categories that ARE present.
        List<String> categories = deviceConfiguration.getCategories().stream()
                .map(DeviceConfigCategory::getCategoryType)
                .collect(Collectors.toList());
        
        categoriesForPaoTypes.keySet().removeAll(categories);

        return categoriesForPaoTypes.values();
    }
    
    private void handleMissingCategories(FlashScope flashScope,
            YukonUserContext context,
            DeviceConfiguration deviceConfiguration,
            ConfigCategories configCategories) {
        Collection<ConfigurationCategory> missingCategories = validateCategories(deviceConfiguration);

        if (!missingCategories.isEmpty()) {
            List<String> categories = 
                    missingCategories.stream()
                        .filter(ConfigurationCategory::isRequired)
                        .map(c -> c.getType().value())
                        .map(type -> baseKey + ".category." + type + ".title")
                        .map(YukonMessageSourceResolvable::new)
                        .map(ymsr -> formattingService.formatObjectAsString(ymsr, context))
                        .collect(Collectors.toList());
            
            if (!categories.isEmpty()) {
                YukonMessageSourceResolvable flashMsg =
                        new YukonMessageSourceResolvable(baseKey + ".config.missingCategories", categories);

                flashScope.setError(flashMsg);
            }

            YukonMessageSourceResolvable noneResolvable =
                new YukonMessageSourceResolvable(baseKey + ".config.noneSelected");
            String noneSelected = formattingService.formatObjectAsString(noneResolvable, context);

            List<CategorySelection> unselected = 
                    missingCategories.stream()
                        .map(category -> 
                                new CategorySelection(
                                    new CategoryDisplay(
                                        category.getType().value(), 
                                        deviceConfigurationDao.categoriesExistForType(category.getType().value())), 
                                    noneSelected, 
                                    category.isOptional()))
                        .collect(Collectors.toList());

            configCategories.getCategorySelections().addAll(unselected);
        }
    }
        
    private void setupModelMapForSetup(ModelMap model, Integer configId) {
        Set<PaoDefinition> configDefinitions =
            paoDefinitionDao.getCreatablePaosThatSupportTag(PaoTag.DEVICE_CONFIGURATION);
        
        DeviceConfigTypes backingBean;
        if (configId != null) {
            // Populate the current types.
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            Set<PaoType> supportedDeviceTypes = configuration.getSupportedDeviceTypes();
            
            backingBean = DeviceConfigTypes.fromPaoTypes(configDefinitions, supportedDeviceTypes);
        } else {
            backingBean = DeviceConfigTypes.fromPaoDefinitions(configDefinitions);
        }
        
        backingBean.setConfigId(configId);
        
        setupModelMapForSetup(model, configId, backingBean);
    }
    
    /**
     * Adds the Device types that support device configuration to the model for the user to choose from.
     * @param model the model map
     */
    private void setupModelMapForSetup(ModelMap model,
                                       Integer configId,
                                       DeviceConfigTypes backingBean) {
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
        model.addAttribute("deviceConfigTypes", backingBean);
        
        // Add the possible types for the checkboxes.
        List<PaoType> rtus = new ArrayList<>();
        List<PaoType> meters = new ArrayList<>();
        List<PaoType> cbcs = new ArrayList<>();
        List<PaoType> regulators = new ArrayList<>();
        for (PaoType paoType : backingBean.getSupportedTypes().keySet()) {
            if (paoType.isRtu()) {
                rtus.add(paoType);
            } else if (paoType.isMeter()) {
                meters.add(paoType);
            } else if (paoType.isCbc()) {
                cbcs.add(paoType);
            } else if (paoType.isRegulator()) {
                regulators.add(paoType);
            } else {
                log.error("Invalid paoType " + paoType + " received for device configuration");
            }
        }
        
        Collections.sort(meters, paoTypeAlphaComparator);
        Collections.sort(cbcs, paoTypeAlphaComparator);
        Collections.sort(rtus, paoTypeAlphaComparator);
        Collections.sort(regulators, paoTypeAlphaComparator);
        
        model.addAttribute("meters", meters);
        model.addAttribute("cbcs", cbcs);
        model.addAttribute("rtus", rtus);
        model.addAttribute("regulators", regulators);
    }
    
    /**
     * Sets up the model map for rendering a configuration view.
     * @param model the model map
     * @param mode the mode the view will be rendered in
     * @param deviceConfig the backing bean containing the configuration data.
     */
    private void setupModelMap(ModelMap model,
                               PageEditMode mode,
                               Integer configId,
                               DeviceConfig deviceConfig,
                               DeviceConfigTypes deviceConfigTypes,
                               ConfigCategories configCategories) {
        boolean isDeletable = configId == null ? false : deviceConfigurationDao.isConfigurationDeletable(configId);
        model.addAttribute("isDeletable", isDeletable);

        model.addAttribute("deviceConfig", deviceConfig);
        model.addAttribute("deviceConfigTypes", deviceConfigTypes);
        model.addAttribute("configCategories", configCategories);

        model.addAttribute("mode", mode);
        
        boolean showTypesPopupOnLoad =
            mode == PageEditMode.VIEW &&
            deviceConfigTypes.isSupportedTypesEmpty();
        
        model.addAttribute("showTypesPopupOnLoad", showTypesPopupOnLoad);
        
        //  Split the category map into optional and required sets
        Map<Boolean, List<Entry<ConfigurationCategory, PaoType>>> partitioned = 
                paoDefinitionDao.getCategoryToPaoTypeMap().entries().stream()
                        .collect(Collectors.partitioningBy(e -> e.getKey().isRequired()));
        
        Map<String, Collection<PaoType>> requiredTypes = collectCategoryMap(partitioned.get(true));
        Map<String, Collection<PaoType>> optionalTypes = collectCategoryMap(partitioned.get(false));
        
        model.addAttribute("requiredCategoryToDeviceTypesMap", requiredTypes);
        model.addAttribute("optionalCategoryToDeviceTypesMap", optionalTypes);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
    }
    
    private Map<String, Collection<PaoType>> collectCategoryMap(Collection<Entry<ConfigurationCategory, PaoType>> categoryTypeLists) {
        return categoryTypeLists.stream()
                .collect(StreamUtils.toMultimap(
                        entry -> entry.getKey().getType().value(), 
                        Entry::getValue))
                .asMap();
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        EnumPropertyEditor.register(binder, PaoType.class);
    }
}
