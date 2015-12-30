package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DisplayableConfigurationCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.deviceConfiguration.model.DisplayableConfigurationData;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.ImmutableMap.Builder;

@Controller
@RequestMapping("/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationController {
    
	@Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private Map<ConfigurationSortBy, Comparator<DisplayableConfigurationData>> configurationSorters;
    private Map<CategorySortBy, Comparator<DisplayableConfigurationCategory>> categorySorters;
    
    @PostConstruct
    public void initialize() {
        Builder<ConfigurationSortBy, Comparator<DisplayableConfigurationData>> builder = ImmutableMap.builder();
        builder.put(ConfigurationSortBy.name, getConfigurationNameComparator());
        builder.put(ConfigurationSortBy.devices, getDevicesComparator());
        configurationSorters = builder.build();
        
        Builder<CategorySortBy, Comparator<DisplayableConfigurationCategory>> catBuilder = ImmutableMap.builder();
        catBuilder.put(CategorySortBy.name, getCategoryNameComparator());
        catBuilder.put(CategorySortBy.type, getTypeComparator());
        catBuilder.put(CategorySortBy.assignments, getAssignmentsComparator());
        categorySorters = catBuilder.build();
    }

	
    /**
     * A small class that wraps a JAXB category type to provide us the getValue() method
     * since JAXB only gives us value(), which can't be accessed in JSP-EL world.
     */
    public final static class DisplayableCategoryType implements DisplayableEnum {
        CategoryType categoryType;
        
        public DisplayableCategoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
        }

        public String getValue() {
            return categoryType.value();
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.configs.category." + categoryType.value() + ".title";
        }
    }
    
    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext context, @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting) {
        getConfigurations(model, sorting, context);
        //switch default sort to type for categories
        sorting = SortingParameters.of("type", Direction.asc);
        getCategories(model, sorting, context);
        
        model.addAttribute("mode", PageEditMode.VIEW);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
        
        return "home.jsp";
    }
    
    @RequestMapping("configTable")
    public String configTable(ModelMap model, YukonUserContext context, @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting) {
        getConfigurations(model, sorting, context);
        return "configTable.jsp";
    }
    
    @RequestMapping("categoryTable")
    public String categoryTable(ModelMap model, YukonUserContext context, @DefaultSort(dir=Direction.asc, sort="type") SortingParameters sorting) {
        getCategories(model, sorting, context);
        return "categoryTable.jsp";
    }
    
    private void getConfigurations(ModelMap model, @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, YukonUserContext userContext) {
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();

        List<DisplayableConfigurationData> displayables = new ArrayList<>();
        for (LightDeviceConfiguration config : configurations) {
            displayables.add(
                new DisplayableConfigurationData(
                    config, 
                    deviceConfigurationDao.getNumberOfDevicesForConfiguration(config.getConfigurationId())));
        }

        ConfigurationSortBy sortBy = ConfigurationSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<DisplayableConfigurationData>itemList = Lists.newArrayList(displayables);
        Comparator<DisplayableConfigurationData> comparator = configurationSorters.get(sortBy);
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        List<SortableColumn> columns = new ArrayList<SortableColumn>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (ConfigurationSortBy column : ConfigurationSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
        }
        model.addAttribute("columns", columns);
        
        model.addAttribute("configurations", itemList);
    }
    
    private void getCategories(ModelMap model, @DefaultSort(dir=Direction.asc, sort="type") SortingParameters sorting, YukonUserContext userContext) {
        List<DisplayableConfigurationCategory> categories = deviceConfigurationDao.getAllDeviceConfigurationCategories();
        
        List<DisplayableCategoryType> categoryTypes = new ArrayList<>();
        for (CategoryType type : CategoryType.values()) {
            categoryTypes.add(new DisplayableCategoryType(type));
        }
        
        CategorySortBy sortBy = CategorySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<DisplayableConfigurationCategory>itemList = Lists.newArrayList(categories);
        Comparator<DisplayableConfigurationCategory> comparator = categorySorters.get(sortBy);
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        List<SortableColumn> columns = new ArrayList<SortableColumn>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (CategorySortBy column : CategorySortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
        }
        model.addAttribute("catColumns", columns);        
        model.addAttribute("categoryTypes", categoryTypes);
        model.addAttribute("categories", itemList);

    }

    @RequestMapping("{id}")
    public @ResponseBody DeviceConfiguration deviceConfig(@PathVariable int id) {
        DeviceConfiguration deviceConfig = deviceConfigurationDao.getDeviceConfiguration(id);
        return deviceConfig;
    }
    
    public enum ConfigurationSortBy implements DisplayableEnum {
        
        name,
        devices;

        @Override
        public String getFormatKey() {
            return "yukon.common." + name();
        }
    }
    
    public enum CategorySortBy implements DisplayableEnum {
        
        name,
        type,
        assignments;

        @Override
        public String getFormatKey() {
            return "yukon.common." + name();
        }
    }
    
    private Comparator<DisplayableConfigurationData> getConfigurationNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DisplayableConfigurationData> nameOrdering = normalStringComparer
            .onResultOf(new Function<DisplayableConfigurationData, String>() {
                @Override
                public String apply(DisplayableConfigurationData from) {
                    return from.getName();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<DisplayableConfigurationData> getDevicesComparator() {
        Ordering<Integer> normalIntComparer = Ordering.natural();
        Ordering<DisplayableConfigurationData> deviceOrdering = normalIntComparer
            .onResultOf(new Function<DisplayableConfigurationData, Integer>() {
                @Override
                public Integer apply(DisplayableConfigurationData from) {
                    return from.getNumDevices();
                }
            });
        return deviceOrdering;
    }
    
    private Comparator<DisplayableConfigurationCategory> getCategoryNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DisplayableConfigurationCategory> nameOrdering = normalStringComparer
            .onResultOf(new Function<DisplayableConfigurationCategory, String>() {
                @Override
                public String apply(DisplayableConfigurationCategory from) {
                    return from.getCategoryName();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<DisplayableConfigurationCategory> getTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DisplayableConfigurationCategory> typeOrdering = normalStringComparer
            .onResultOf(new Function<DisplayableConfigurationCategory, String>() {
                @Override
                public String apply(DisplayableConfigurationCategory from) {
                    return from.getCategoryType();
                }
            });
        return typeOrdering;
    }
    
    private Comparator<DisplayableConfigurationCategory> getAssignmentsComparator() {
        Ordering<Integer> normalIntegerComparer = Ordering.natural();
        Ordering<DisplayableConfigurationCategory> assignmentsOrdering = normalIntegerComparer
            .onResultOf(new Function<DisplayableConfigurationCategory, Integer>() {
                @Override
                public Integer apply(DisplayableConfigurationCategory from) {
                    return from.getConfigNames().size();
                }
            });
        return assignmentsOrdering;
    }

}
