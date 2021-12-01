package com.cannontech.common.search.pao.db;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.google.common.collect.ImmutableList;

public class ExpresscomLoadGroupFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        List<PaoType> expresscomGroupTypes = ImmutableList.of(PaoType.LM_GROUP_EXPRESSCOMM, PaoType.LM_GROUP_RFN_EXPRESSCOMM);
                
        SqlStatementBuilder isExpresscomGroup = new SqlStatementBuilder();
        isExpresscomGroup.append("Type").in(expresscomGroupTypes);
        
        return isExpresscomGroup;
    }
}