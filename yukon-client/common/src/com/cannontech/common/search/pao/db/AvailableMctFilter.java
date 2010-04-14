package com.cannontech.common.search.pao.db;

import java.util.Set;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AvailableMctFilter implements SqlFilter {
    
    private Set<Integer> energyCompanyIds;
    
    public AvailableMctFilter(Set<Integer> energyCompanyIds) {
        this.energyCompanyIds = energyCompanyIds;
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder limiter1 = new SqlStatementBuilder();
        limiter1.append("paobjectId IN (");
        limiter1.append("   SELECT paobjectId FROM YukonPAObject ypo where ypo.type like 'MCT%' ");
        limiter1.append("   AND ypo.paobjectId NOT IN (SELECT deviceId FROM inventoryBase ib WHERE ib.DeviceId = ypo.PAObjectId)");
        limiter1.append("               )");

        SqlStatementBuilder limiter2 = new SqlStatementBuilder();
        limiter2.append("paobjectId IN (");
        limiter2.append("   SELECT deviceId FROM inventoryBase ib ");
        limiter2.append("   JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
        limiter2.append("   JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        limiter2.append("   JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        limiter2.append("   WHERE etim.EnergyCompanyId ").in(energyCompanyIds);
        limiter2.append("   AND ib.accountId = 0 ");
        limiter2.append("   AND yle.YukonDefinitionId = ").append(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).append(")");
        
        SqlFragmentCollection retVal = new SqlFragmentCollection("OR");
        retVal.add(limiter1);
        retVal.add(limiter2);

        return retVal;
    }
    
}