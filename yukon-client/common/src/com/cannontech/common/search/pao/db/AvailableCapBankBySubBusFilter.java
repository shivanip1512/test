package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;


public class AvailableCapBankBySubBusFilter implements SqlFilter {

    private Integer subBusId = null;
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();
        
        SqlStatementBuilder bySubBus = new SqlStatementBuilder();
        bySubBus.append("PAObjectId IN (SELECT DeviceId");
        bySubBus.append("               FROM CCFeederBankList");
        bySubBus.append("               WHERE FeederId IN (SELECT FeederId");
        bySubBus.append("               FROM CCFeederSubAssignment");
        bySubBus.append("               WHERE SubStationBusId").eq(subBusId);
        bySubBus.append("               ))");
        retVal.add(bySubBus);
        
        return retVal;
    }
    
    public void setSubBusId(int subBusId) {
        this.subBusId = subBusId;
    }
}
