package com.cannontech.common.search.pao.db;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.google.common.collect.Lists;

public class VoltageRegulatorFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();
        
        List<String> regulatorTypes = Lists.newArrayList();
        regulatorTypes.add(PaoType.PHASE_OPERATED.getDbString());
        regulatorTypes.add(PaoType.GANG_OPERATED.getDbString());
        regulatorTypes.add(PaoType.LOAD_TAP_CHANGER.getDbString());
        
        SqlStatementBuilder isVoltageRegulator = new SqlStatementBuilder();
        isVoltageRegulator.append("Type").in(regulatorTypes);
        retVal.add(isVoltageRegulator);
        
        return retVal;
    }
}