package com.cannontech.web.deviceConfiguration;

import java.util.List;
import java.util.Map;
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
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.model.jaxb.InputBase;
import com.cannontech.common.device.config.model.jaxb.InputMap;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean.RateBackingBean;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.RateInput;
import com.cannontech.web.deviceConfiguration.validation.CategoryEditValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/category/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationCategoryController {
    
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationCategoryController.class);
    
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private DeviceConfigurationHelper deviceConfigHelper;
    @Autowired private DeviceConfigurationService deviceConfigService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private final static String baseKey = "yukon.web.modules.tools.configs";
    
    // This will need to be updated if the schedules or rates/times can get to double digit values.
    private static final Pattern touPattern = Pattern.compile("^schedule[0-9](rate|time)[0-9]$");
    
    @RequestMapping("create")
    public String create(ModelMap model, String categoryType, YukonUserContext context) {
        Category category = deviceConfigDao.getCategoryByType(CategoryType.fromValue(categoryType));
        
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
        
        setupModelMap(model, PageEditMode.CREATE, categoryEditBean, context);
        return "category.jsp";
    }
    
    @RequestMapping("createInPlace")
    public String createInPlace(ModelMap model, String categoryType, int configId, YukonUserContext context) {
        
        Category category = deviceConfigDao.getCategoryByType(CategoryType.fromValue(categoryType));
        
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
        
        return categoryPopup(categoryEditBean, model, configId, context);
    }
    
    @RequestMapping("editInPlace")
    public String editInPlace(ModelMap model, int categoryId, int configId, YukonUserContext context) {
        
        DeviceConfigCategory category = deviceConfigDao.getDeviceConfigCategory(categoryId);
        CategoryEditBean categoryEditBean = createCategoryEditBean(category);
        
        return categoryPopup(categoryEditBean, model, configId, context);
    }
    
    @RequestMapping("edit")
    public String edit(ModelMap model, int categoryId, YukonUserContext context) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_EDIT_CONFIG, context.getYukonUser());
        return viewOrEdit(model, categoryId, context, PageEditMode.EDIT);
    }
    
    @RequestMapping("view")
    public String view(ModelMap model, int categoryId, YukonUserContext context) {
        return viewOrEdit(model, categoryId, context, PageEditMode.VIEW);
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
                       YukonUserContext context) {
        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());
        
        CategoryTemplate categoryTemplate = 
                deviceConfigHelper.createTemplate(deviceConfigDao.getCategoryByType(type), context);

        CategoryEditValidator validator = new CategoryEditValidator(categoryTemplate);
        validator.validate(categoryEditBean, bindingResult);
        
        if (bindingResult.hasErrors()) {
            flashScope.setMessage(
                new YukonMessageSourceResolvable(baseKey + ".errorsExist"), 
                FlashScopeMessageType.ERROR);
            
            // Return to the edit view if the category has an Id, create otherwise.
            PageEditMode mode = categoryEditBean.getCategoryId() != null ? PageEditMode.EDIT : PageEditMode.CREATE;
            
            setupModelMap(model, mode, categoryEditBean, context);
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
                              YukonUserContext context) {
        
        CategoryType type = CategoryType.fromValue(category.getCategoryType());
        
        Category categoryByType = deviceConfigDao.getCategoryByType(type);
        CategoryTemplate categoryTemplate = deviceConfigHelper.createTemplate(categoryByType, context);

        CategoryEditValidator validator = new CategoryEditValidator(categoryTemplate);
        validator.validate(category, result);
        
        if (result.hasErrors()) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".errorsExist"));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return categoryPopup(category, model, configId, context);
        }
        
        try {
            deviceConfigService.saveCategory(category.getModelObject());
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.saveSuccess"));
        } catch (DuplicateException de) {
            log.debug("An attempt to save a category with a duplicate name has occurred.");
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".category.nameExists"));
            resp.setStatus(HttpStatus.CONFLICT.value());
            return categoryPopup(category, model, configId, context);
        }
        
        model.clear();
        return null;
    }
    
    /** 
     * Return the category popup view. 
     */
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
        setupModelMap(model, mode, category, userContext);
        
        return "ajaxCategory.jsp";
    }
    
    /**
     * Navigate the user to the view or edit rendering of the category.
     * @param model the model map
     * @param categoryId the category id of the category being viewed or edited
     * @param context the user context
     * @param mode the editing mode of the page.
     * @return the jsp of the view
     */
    private String viewOrEdit(ModelMap model, int categoryId, YukonUserContext context, PageEditMode mode) {
        DeviceConfigCategory category = deviceConfigDao.getDeviceConfigCategory(categoryId);
        
        CategoryEditBean categoryEditBean = createCategoryEditBean(category);

        setupModelMap(model, mode, categoryEditBean, context);
        
        return "category.jsp";
    }
    
    /**
     * Converts the database representation of a category into the appropriate backing bean for the view to use.
     * @param category the database representation of the category.
     * @return the populated backing bean representing the data of the database instance of the category.
     */
    private CategoryEditBean createCategoryEditBean(DeviceConfigCategory category) {
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
        
        for (DeviceConfigCategoryItem item : deviceConfigurationItems) {
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
     * Sets up the model for rendering a category view.
     * @param model the model map
     * @param mode the mode the view will be rendered in.
     * @param categoryEditBean the backing bean containing the category data.
     * @param context the user context
     */
    public void setupModelMap(ModelMap model, 
                              PageEditMode mode, 
                              CategoryEditBean categoryEditBean, 
                              YukonUserContext context) {
        // This category can be deleted if it exists in the database and has no configuration assignments.
        boolean isDeletable = categoryEditBean.getCategoryId() == null ? false : 
                deviceConfigDao.getConfigurationNamesForCategory(categoryEditBean.getCategoryId()).size() == 0;
           
        model.addAttribute("isDeletable", isDeletable);
        
        model.addAttribute("categoryEditBean", categoryEditBean);
        
        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());
        CategoryTemplate categoryTemplate = 
            deviceConfigHelper.createTemplate(deviceConfigDao.getCategoryByType(type), context);
        
        model.addAttribute("categoryTemplate", categoryTemplate);
        
        String templateCategoryType = categoryTemplate.getCategoryType();

        boolean isDisplayItemsCategory =
                        templateCategoryType.equals(CategoryType.CENTRON_DISPLAY_ITEMS.value()) ||
                        templateCategoryType.equals(CategoryType.FOCUS_AL_DISPLAY.value());
        
        model.addAttribute("isDisplayItemsCategory", isDisplayItemsCategory);
        
        boolean isTouCategory = 
                        templateCategoryType.equals(CategoryType.MCT_440_TOU.value()) || 
                        templateCategoryType.equals(CategoryType.TOU.value());
        
        model.addAttribute("isTouCategory", isTouCategory);
        
        model.addAttribute("mode", mode);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
    }
}
