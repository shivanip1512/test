package com.cannontech.common.device.config.dao;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LightConfigurationCategoryFilter implements SqlFilter {
    private CategoryType categoryType;

    public LightConfigurationCategoryFilter(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("DCC.CategoryType").eq(categoryType.value());
        return retVal;
    }
}
