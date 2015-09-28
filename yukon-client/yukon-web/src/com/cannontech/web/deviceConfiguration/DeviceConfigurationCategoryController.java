package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDouble;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.deviceConfiguration.enumeration.Read.ReadType;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean.RateBackingBean;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.ChannelField;
import com.cannontech.web.deviceConfiguration.model.ChannelInput;
import com.cannontech.web.deviceConfiguration.model.Field;
import com.cannontech.web.deviceConfiguration.model.RateInput;
import com.cannontech.web.deviceConfiguration.model.RateMapField;
import com.cannontech.web.deviceConfiguration.model.RateMapField.DisplayableRate;
import com.cannontech.web.deviceConfiguration.validation.CategoryEditValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableSet;

@Controller
@RequestMapping("/category/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationCategoryController {

    Set<PaoType> voltageDataStreamingTypes = ImmutableSet.of(PaoType.RFN410CL, PaoType.RFN420CL, PaoType.RFN410FX,
        PaoType.RFN410FD, PaoType.RFN420FD);
    
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationCategoryController.class);

    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private DeviceConfigurationHelper deviceConfigHelper;
    @Autowired private DeviceConfigurationService deviceConfigService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;

    private final static String baseKey = "yukon.web.modules.tools.configs";

    // This will need to be updated if the schedules or rates/times can get to double digit values.
    private static final Pattern touPattern = Pattern.compile("^schedule[0-9](rate|time)[0-9]$");
    private static final Pattern channelPattern = Pattern.compile("^enabledChannels\\.\\d+\\.(attribute|read)$");

    @RequestMapping("create")
    public String create(ModelMap model, String categoryType, YukonUserContext userContext) {

        CategoryEditBean categoryEditBean = createDefaultCategoryEditBean(categoryType, null, userContext);
        setupModelMap(model, PageEditMode.CREATE, categoryEditBean, null, userContext);
        return "category.jsp";
    }

    @RequestMapping("createInPlace")
    public String createInPlace(ModelMap model, String categoryType, int configId, YukonUserContext userContext) {
        CategoryEditBean categoryEditBean = createDefaultCategoryEditBean(categoryType, configId, userContext);

        return categoryPopup(categoryEditBean, model, configId, userContext);
    }

    private CategoryEditBean createDefaultCategoryEditBean(String categoryType, Integer configId, YukonUserContext userContext) {

        Category category = deviceConfigDao.getCategoryByType(CategoryType.fromValue(categoryType));
        CategoryTemplate categoryTemplate = deviceConfigHelper.createDefaultTemplate(category, configId, userContext);

        CategoryEditBean categoryEditBean = new CategoryEditBean();
        categoryEditBean.setCategoryType(categoryType);

        List<Field<?>> fields = categoryTemplate.getFields();
        for (Field<?> field : fields) {
            if (field.getClass() == RateMapField.class) {
                RateMapField rateMapField = (RateMapField) field;

                // Create the backing bean to represent the collection of rates.
                RateBackingBean rateBackingBean = new RateBackingBean();

                List<DisplayableRate> entries = rateMapField.getInputTypes();
                for (DisplayableRate entry : entries) {
                    rateBackingBean.getRateInputs().put(entry.getField(), new RateInput());
                }
                categoryEditBean.getScheduleInputs().put(field.getFieldName(), rateBackingBean);
            } else if (field.getClass() == ChannelField.class) {
                List<ChannelInput> channelInputs = new ArrayList<>();
                ChannelField channelField = (ChannelField) field;

                for (BuiltInAttribute channel : channelField.getChannelTypes()) {
                    channelInputs.add(new ChannelInput(channel, ReadType.DISABLED));
                }
                channelInputs = CtiUtilities.smartTranslatedSort(channelInputs, deviceConfigHelper.toAttribute(userContext));
                categoryEditBean.setChannelInputs(channelInputs);
            } else {
                categoryEditBean.getCategoryInputs().put(field.getFieldName(), field.getDefault());
            }
        }

        return categoryEditBean;
    }

    @RequestMapping("editInPlace")
    public String editInPlace(ModelMap model, int categoryId, int configId, YukonUserContext userContext) {

        DeviceConfigCategory category = deviceConfigDao.getDeviceConfigCategory(categoryId);
        CategoryEditBean categoryEditBean = createCategoryEditBean(category, configId, userContext);

        return categoryPopup(categoryEditBean, model, configId, userContext);
    }

    @RequestMapping("quick-view")
    public String quickView(ModelMap model, int categoryId, int configId, YukonUserContext userContext) {

        DeviceConfigCategory category = deviceConfigDao.getDeviceConfigCategory(categoryId);
        CategoryEditBean categoryEditBean = createCategoryEditBean(category, configId, userContext);

        categoryPopup(categoryEditBean, model, configId, userContext);
        return "category.quick.view.jsp";
    }

    @RequestMapping("edit")
    @CheckRoleProperty(YukonRoleProperty.ADMIN_EDIT_CONFIG)
    public String edit(ModelMap model, int categoryId, YukonUserContext userCOntext) {
        return viewOrEdit(model, categoryId, userCOntext, PageEditMode.EDIT);
    }

    @RequestMapping("view")
    public String view(ModelMap model, int categoryId, YukonUserContext userContext) {
        return viewOrEdit(model, categoryId, userContext, PageEditMode.VIEW);
    }

    @RequestMapping("delete")
    public String delete(ModelMap model, FlashScope flashScope, int categoryId) {
        deviceConfigService.deleteCategory(categoryId);

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.deleteSuccess"));

        model.clear();
        return "redirect:/deviceConfiguration/home";
    }

    @RequestMapping("save")
    public String save(ModelMap model, 
                       FlashScope flashScope, 
                       @ModelAttribute CategoryEditBean categoryEditBean, 
                       BindingResult bindingResult, 
                       YukonUserContext userContext) {
        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());

        CategoryTemplate categoryTemplate = deviceConfigHelper.createTemplate(
                deviceConfigDao.getCategoryByType(type), categoryEditBean.getCategoryInputs(), null, userContext);

        // pre-patch categoryEditBean, to remove or add inputs validators
        prePatchCategoryEditBean(categoryEditBean, categoryTemplate);

        CategoryEditValidator validator = new CategoryEditValidator(categoryTemplate);
        validator.validate(categoryEditBean, bindingResult);

        if (bindingResult.hasErrors()) {
            flashScope.setMessage(
                new YukonMessageSourceResolvable(baseKey + ".errorsExist"), 
                FlashScopeMessageType.ERROR);

            // Return to the edit view if the category has an Id, create otherwise.
            PageEditMode mode = categoryEditBean.getCategoryId() != null ? PageEditMode.EDIT : PageEditMode.CREATE;

            setupModelMap(model, mode, categoryEditBean, null, userContext);
            return "category.jsp";
        }

        DeviceConfigCategory configCategory = categoryEditBean.getModelObject();
        try {
            int categoryId = deviceConfigService.saveCategory(configCategory);

            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.saveSuccess"));

            model.clear();
            return "redirect:view?categoryId=" + categoryId;
        } catch (DuplicateException de) {
            log.debug("Creation of a device configuration category with an already existing name was attempted.");

            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".category.nameExists"));

            if (configCategory.getCategoryId() == null) {
                return "redirect:create?categoryType=" + configCategory.getCategoryType();
            } else {
                return "redirect:edit?categoryId=" + configCategory.getCategoryId();
            }
        }
    }

    @RequestMapping("saveInPlace")
    public String saveInPlace(HttpServletResponse resp,
                              ModelMap model, 
                              FlashScope flash, 
                              @ModelAttribute CategoryEditBean category,
                              BindingResult result, 
                              int configId,
                              YukonUserContext userContext) {

        CategoryType type = CategoryType.fromValue(category.getCategoryType());

        CategoryTemplate categoryTemplate = deviceConfigHelper.createTemplate(
                deviceConfigDao.getCategoryByType(type), category.getCategoryInputs(), null, userContext);

        // pre-patch categoryEditBean, to remove or add inputs validators
        prePatchCategoryEditBean(category, categoryTemplate);

        CategoryEditValidator validator = new CategoryEditValidator(categoryTemplate);
        validator.validate(category, result);

        if (result.hasErrors()) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".errorsExist"));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return categoryPopup(category, model, configId, userContext);
        }

        try {
            deviceConfigService.saveCategory(category.getModelObject());
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.saveSuccess"));
        } catch (DuplicateException de) {
            log.debug("An attempt to save a category with a duplicate name has occurred.");
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".category.nameExists"));
            resp.setStatus(HttpStatus.CONFLICT.value());
            return categoryPopup(category, model, configId, userContext);
        }

        model.clear();
        return null;
    }

    /**
     * Patch categoryEditBean category inputs, before validation or before
     * rendering. This method removes deleted entries and adds default value 
     * from the template (if itexists) to newly added entries.
     *
     */
    private void prePatchCategoryEditBean(CategoryEditBean categoryEditBean, CategoryTemplate categoryTemplate) {

        Set<String> fieldsExpected = new HashSet<>();

        List<Field<?>> fields = categoryTemplate.getFields();

        for (Field<?> field : fields) {
            if (field.getClass() != RateMapField.class && field.getClass() != ChannelField.class) {
                if (!categoryEditBean.getCategoryInputs().containsKey(field.getFieldName())) {
                    categoryEditBean.getCategoryInputs().put(field.getFieldName(), field.getDefault());
                }
                fieldsExpected.add(field.getFieldName());
            }
        }
        categoryEditBean.getCategoryInputs().keySet().retainAll(fieldsExpected);
    }

    private String categoryPopup(CategoryEditBean category, ModelMap model, int configId, YukonUserContext userContext) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        // Return to the edit view if the category has an Id, create otherwise.
        PageEditMode mode = PageEditMode.CREATE;
        if (category.getCategoryId() != null) {
            mode = PageEditMode.EDIT;
            model.addAttribute("popupTitle", accessor.getMessage(baseKey + ".category.EDIT.pageName"));
        } else {
            model.addAttribute("popupTitle", accessor.getMessage(baseKey + ".category.CREATE.pageName"));
        }
        model.addAttribute("configId", configId);
        setupModelMap(model, mode, category, configId,  userContext);

        return "ajaxCategory.jsp";
    }

    /**
     * Navigate the user to the view or edit rendering of the category.
     * @param model the model map
     * @param categoryId the category id of the category being viewed or edited
     * @param userContext the user context
     * @param mode the editing mode of the page.
     * @return the jsp of the view
     */
    private String viewOrEdit(ModelMap model, int categoryId, YukonUserContext userContext, PageEditMode mode) {
        DeviceConfigCategory category = deviceConfigDao.getDeviceConfigCategory(categoryId);

        CategoryEditBean categoryEditBean = createCategoryEditBean(category, null, userContext);
        setupModelMap(model, mode, categoryEditBean, null, userContext);

        return "category.jsp";
    }

    /**
     * Converts the database representation of a category into the appropriate backing bean for the view to use.
     * @param category the database representation of the category.
     * @param configId the config to base channel items around
     * @return the populated backing bean representing the data of the database instance of the category.
     */
    private CategoryEditBean createCategoryEditBean(DeviceConfigCategory category, Integer configId, YukonUserContext userContext) {
        CategoryEditBean categoryEditBean = new CategoryEditBean();

        categoryEditBean.setCategoryId(category.getCategoryId());
        categoryEditBean.setCategoryName(category.getCategoryName());
        categoryEditBean.setCategoryType(category.getCategoryType());
        categoryEditBean.setDescription(category.getDescription());

        List<String> configNames = deviceConfigDao.getConfigurationNamesForCategory(category.getCategoryId());
        categoryEditBean.setAssignments(configNames);

        List<DeviceConfigCategoryItem> deviceConfigurationItems = category.getDeviceConfigurationItems();

        Map<String, String> categoryItems = new TreeMap<>();
        Map<String, String> scheduleItems = new TreeMap<>();
        Map<String, String> channelItems = new TreeMap<>();

        for (DeviceConfigCategoryItem item : deviceConfigurationItems) {
            Matcher touMatcher = touPattern.matcher(item.getFieldName());
            Matcher channelMatcher = channelPattern.matcher(item.getFieldName());
            if (touMatcher.matches()) {
                scheduleItems.put(item.getFieldName(), item.getValue());
            } else if (channelMatcher.matches()) {
                channelItems.put(item.getFieldName(), item.getValue());
            } else {
                categoryItems.put(item.getFieldName(), item.getValue());
            }
        }

        categoryEditBean.setCategoryInputs(categoryItems);
        categoryEditBean.setScheduleInputs(convertTouScheduleItems(scheduleItems));
        categoryEditBean.setChannelInputs(convertChannelConfigItems(channelItems, configId, userContext ));

        return categoryEditBean;
    }

    /**
     * Converts the schedule items stored in the database into renderable objects for the view. 
     * @param scheduleItems the map of schedule fields and their respective values.
     * @return a view-appropriate map containing the information from the scheduleItems map.
     */
    private SortedMap<String, RateBackingBean> convertTouScheduleItems(Map<String, String> scheduleItems) {
        SortedMap<String, RateBackingBean> scheduleInputs = new TreeMap<>();

        for (int schedule = 1; schedule <= 4; schedule++) {
            RateBackingBean backingBean = new RateBackingBean();

            SortedMap<String, RateInput> rateInputs = new TreeMap<>();

            /*
             * There are four schedules, and each entry for each schedule has a rate and a time in the map. 
             * As a result, if we divide the map size by eight we'll have the number of fields present.
             * i.e. if the size of the map is 8, there is only one entry for each schedule:
             *
             *      schedule1time0, schedule1rate0
             *      schedule2time0, schedule2rate0
             *      schedule3time0, schedule3rate0
             *      schedule4time0, schedule4rate0
             */

            int numFields = scheduleItems.size() / 8;

            for (int field = 0; field <= numFields; field++) {
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
     * Converts database representation of channel config entries into a list of ChannelInputs.
     * Note: only selected entries are stored in the database. Non-selected entries are determined
     * based on the configId (if known) or all possible metric attributes.
     * 
     * @param channelItems database entries
     * @param configId used to determine metric attributes. If null, all metric attributes are used.
     * @return
     */
    private List<ChannelInput> convertChannelConfigItems(Map<String, String> channelItems, Integer configId, YukonUserContext userContext) {

        List<ChannelInput> channelInputs = new ArrayList<>();
        Map<BuiltInAttribute, ReadType> attributeAssignment = new LinkedHashMap<>();

        for (BuiltInAttribute attribute : deviceConfigHelper.getAttributeListForConfigId(configId)) {
            attributeAssignment.put(attribute, ReadType.DISABLED);
        }

        //Each channel input has 2 database entries : .read and .channel
        for (int field = 0; field < channelItems.size() / 2; field++) {
            String attributeField = "enabledChannels." + Integer.toString(field) + ".attribute";
            String readField = "enabledChannels." + Integer.toString(field) + ".read";
            BuiltInAttribute attribute =  BuiltInAttribute.valueOf(channelItems.get(attributeField));
            ReadType readType = ReadType.valueOf(channelItems.get(readField));

            attributeAssignment.put(attribute, readType);
        }

        for (Entry<BuiltInAttribute, ReadType> entry : attributeAssignment.entrySet()) {
            channelInputs.add(new ChannelInput(entry.getKey(), entry.getValue()));
        }

        channelInputs = CtiUtilities.smartTranslatedSort(channelInputs, deviceConfigHelper.toAttribute(userContext));

        return channelInputs;
    }

    /**
     * Sets up the model for rendering a category view.
     * @param model the model map
     * @param mode the mode the view will be rendered in.
     * @param categoryEditBean the backing bean containing the category data.
     * @param userContext the user context
     */
    private void setupModelMap(ModelMap model, 
            PageEditMode mode,
            CategoryEditBean categoryEditBean,
            Integer configId,
            YukonUserContext userContext) {
        // This category can be deleted if it exists in the database and has no configuration assignments.
        boolean isDeletable = categoryEditBean.getCategoryId() == null ? false : 
            deviceConfigDao.getConfigurationNamesForCategory(categoryEditBean.getCategoryId()).size() == 0;

        model.addAttribute("isDeletable", isDeletable);

        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());
        
        if (configId != null && type == CategoryType.RFN_VOLTAGE) {
            DeviceConfiguration config = deviceConfigurationDao.getDeviceConfiguration(configId);
            // Returns true if the two specified collections have no elements in common.
            boolean isAssignedToConfig =
                !Collections.disjoint(config.getSupportedDeviceTypes(), voltageDataStreamingTypes);
            model.addAttribute("enableVoltageDataStreamingOptions", isAssignedToConfig);
            System.out.println("------------------enableDataStreamingOptions=" + isAssignedToConfig);
        }
        if (type == CategoryType.RFN_CHANNEL_CONFIGURATION) {
            boolean isNMCompatible = configurationSource.getDouble(MasterConfigDouble.NM_COMPATIBILITY) == 7.0;
            model.addAttribute("isNMCompatible", isNMCompatible);
            System.out.println("------------------isNMCompatible="+isNMCompatible);
        }
        
        CategoryTemplate categoryTemplate = deviceConfigHelper.createTemplate(
                deviceConfigDao.getCategoryByType(type), categoryEditBean.getCategoryInputs(), configId, userContext);

        // pre-patch categoryEditBean, to provide default values and indexed values, before rendering
        prePatchCategoryEditBean(categoryEditBean, categoryTemplate);

        model.addAttribute("categoryEditBean", categoryEditBean);
        model.addAttribute("categoryTemplate", categoryTemplate);

        String templateCategoryType = categoryTemplate.getCategoryType();

        boolean isDisplayItemsCategory =
                templateCategoryType.equals(CategoryType.CENTRON_410_DISPLAY.value()) ||
                templateCategoryType.equals(CategoryType.CENTRON_420_DISPLAY_ITEMS.value()) ||
                templateCategoryType.equals(CategoryType.FOCUS_AL_DISPLAY.value());

        model.addAttribute("isDisplayItemsCategory", isDisplayItemsCategory);

        model.addAttribute("mode", mode);
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
        
    }
    
}
