package com.cannontech.web.picker;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;

public class CapControlSubstationBusPicker extends DatabasePaoPicker {
    
    @Override
    protected void updateFilters(List<SqlFilter> filters,
            List<PostProcessingFilter<UltraLightPao>> postFilters,
            String extraArgs, YukonUserContext userContext) {
        
        SqlFilter filter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                return new SqlStatementBuilder("Type").in(PaoType.getSubstationbustypes());
            }
        };
        
        filters.add(filter);
    }
    
}