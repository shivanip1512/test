package com.cannontech.common.search.pao.db;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.google.common.collect.ImmutableList;

public class VoltageRegulatorGangFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        List<PaoType> regulatorTypes = ImmutableList.of(PaoType.GANG_OPERATED,
                                                        PaoType.LOAD_TAP_CHANGER);
                
        SqlStatementBuilder isVoltageRegulator = new SqlStatementBuilder();
        isVoltageRegulator.append("Type").in(regulatorTypes);
        
        return isVoltageRegulator;
    }
}