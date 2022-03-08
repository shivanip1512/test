package com.cannontech.common.search.pao.db;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.google.common.collect.ImmutableList;

public class VersacomLoadGroupFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        List<PaoType> versacomGroupTypes = ImmutableList.of(PaoType.LM_GROUP_VERSACOM);
                
        SqlStatementBuilder isVersacomGroup = new SqlStatementBuilder();
        isVersacomGroup.append("Type").in(versacomGroupTypes);
        
        return isVersacomGroup;
    }
}