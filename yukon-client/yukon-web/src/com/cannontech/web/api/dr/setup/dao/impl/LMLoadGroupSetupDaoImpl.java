package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.List;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.web.api.dr.setup.model.LoadGroupFilteredResult;

public class LMLoadGroupSetupDaoImpl extends AbstractLMSetupDaoImpl <LoadGroupFilteredResult> {

    @Override
    public List<LoadGroupFilteredResult> getDetails(FilterCriteria<LMSetupFilter> criteria) {
        // TODO 
        return null;
    }

    @Override
    public Integer getTotalCount(FilterCriteria<LMSetupFilter> criteria) {
        // TODO 
        return null;
    }

    @Override
    public SqlStatementBuilder getFromAndWhereClause(LMSetupFilter filter) {
        // TODO 
        return null;
    }

    @Override
    public String getColumnNames() {
        // TODO Auto-generated method stub
        return null;
    }

}
