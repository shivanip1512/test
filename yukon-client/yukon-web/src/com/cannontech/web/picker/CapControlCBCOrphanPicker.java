package com.cannontech.web.picker;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;

public class CapControlCBCOrphanPicker extends DatabasePaoPicker {
    
    private String selectedCBCId;
    
    @Override
    protected void updateFilters(List<SqlFilter> filters,
            List<PostProcessingFilter<UltraLightPao>> postFilters,
            String extraArgs, YukonUserContext userContext) {
        
        selectedCBCId = extraArgs;
        
        SqlFilter filter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                return new SqlStatementBuilder("Type").in(PaoType.getCbcTypes());
            }
        };
        
        SqlFilter orphanFilter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                
                SqlStatementBuilder controlDeviceInClause = new SqlStatementBuilder();
                controlDeviceInClause.append("SELECT DISTINCT ControlDeviceId FROM CapBank");
                
                SqlStatementBuilder orphanClause = new SqlStatementBuilder();
                orphanClause.append("( PAObjectId").notIn(controlDeviceInClause);
                orphanClause.append("  OR PAObjectId").eq(selectedCBCId);
                orphanClause.append(")");
                return orphanClause;
                
            }
        };
        
        filters.add(filter);
        filters.add(orphanFilter);
    }
    
}