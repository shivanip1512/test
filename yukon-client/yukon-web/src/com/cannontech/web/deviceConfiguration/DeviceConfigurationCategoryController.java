package com.cannontech.web.deviceConfiguration;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.model.jaxb.InputBase;
import com.cannontech.common.device.config.model.jaxb.InputMap;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.RateInput;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean.RateBackingBean;
import com.cannontech.web.deviceConfiguration.validation.CategoryEditValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/category/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationCategoryController {
	
	@Autowired DeviceConfigurationDao deviceConfigurationDao;
	@Autowired DeviceConfigurationHelper deviceConfigurationHelper;
	
	private final static String baseKey = "yukon.web.modules.tools.configs";
	
    // This will need to be updated if the schedules or rates/times can get to double digit values.
    private static final Pattern touPattern = Pattern.compile("^schedule[0-9](rate|time)[0-9]$");
	
	@RequestMapping
    public String create(ModelMap model, String categoryType, YukonUserContext context) {
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
        
        setupModelMap(model, PageEditMode.CREATE, categoryEditBean, context);
        return "category.jsp";
    }
    
    @RequestMapping
    public String edit(ModelMap model, int categoryId, YukonUserContext context) {
        return viewOrEdit(model, categoryId, context, PageEditMode.EDIT);
    }
    
    @RequestMapping
    public String view(ModelMap model, int categoryId, YukonUserContext context) {
        return viewOrEdit(model, categoryId, context, PageEditMode.VIEW);
    }
    
    @RequestMapping
    public String delete(ModelMap model, FlashScope flashScope, int categoryId) {
        deviceConfigurationDao.deleteCategory(categoryId);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.deleteSuccess"));
        
        model.clear();
        return "redirect:/deviceConfiguration/home";
    }

    @RequestMapping
    public String save(ModelMap model, 
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
            
            setupModelMap(model, mode, categoryEditBean, context);
            return "category.jsp";
        }
        
        int categoryId = deviceConfigurationDao.saveCategory(categoryEditBean.getModelObject());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".category.saveSuccess"));
        
        return viewOrEdit(model, categoryId, context, PageEditMode.VIEW);
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
        DeviceConfigCategory category = deviceConfigurationDao.getDeviceConfigCategory(categoryId);
        
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
                deviceConfigurationDao.getNumberOfConfigurationsForCategory(categoryEditBean.getCategoryId()) == 0;
           
        model.addAttribute("isDeletable", isDeletable);
        
        // Add the bean itself.
        model.addAttribute("categoryEditBean", categoryEditBean);
        
        CategoryType type = CategoryType.fromValue(categoryEditBean.getCategoryType());
        CategoryTemplate categoryTemplate = 
            deviceConfigurationHelper.createTemplate(deviceConfigurationDao.getCategoryByType(type), context);
        
        // Add the category template.
        model.addAttribute("categoryTemplate", categoryTemplate);
        
        model.addAttribute("mode", mode);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
    }
}
