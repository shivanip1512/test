package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.device.config.dao.UltraLightConfigurationCategoryFilter;
import com.cannontech.common.device.config.dao.UltraLightConfigurationCategoryRowMapper;
import com.cannontech.common.device.config.model.UltraLightConfigurationCategory;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ConfigurationCategoryPicker extends DatabasePicker<UltraLightConfigurationCategory> {

    private final static String[] searchColumnNames = new String[] {
        "DCC.Name"
    };
    
    private final static List<OutputColumn> outputColumns;
    
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.configurationCategory.";

        columns.add(new OutputColumn("name", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("categoryType", titleKeyPrefix + "categoryType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public ConfigurationCategoryPicker() {
        super(new UltraLightConfigurationCategoryRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(
            List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightConfigurationCategory>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        CategoryType categoryType = CategoryType.fromValue(extraArgs);
        sqlFilters.add(new UltraLightConfigurationCategoryFilter(categoryType));
    }

    @Override
    public String getIdFieldName() {
        return "categoryId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "DCC.DeviceConfigCategoryId";
    }
}
