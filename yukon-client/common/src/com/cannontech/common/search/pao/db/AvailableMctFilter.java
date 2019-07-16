package com.cannontech.common.search.pao.db;

import java.util.Set;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AvailableMctFilter implements SqlFilter {
    
    private Set<Integer> energyCompanyIds;
    private PaoDefinitionDao paoDefinitionDao;
    
    public AvailableMctFilter(Set<Integer> energyCompanyIds, PaoDefinitionDao paoDefinitionDao) {
        this.energyCompanyIds = energyCompanyIds;
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        
    	Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.STARS_ACCOUNT_ATTACHABLE_METER);
        paoTypes.addAll(PaoType.getRfMeterTypes());

        SqlStatementBuilder limiter1 = new SqlStatementBuilder();
        limiter1.append("paobjectId IN (");
        limiter1.append("  SELECT paobjectId");
        limiter1.append("  FROM YukonPAObject ypo");
        limiter1.append("  WHERE ypo.type ").in(paoTypes);
        limiter1.append("    AND ypo.paobjectId NOT IN (SELECT deviceId FROM inventoryBase ib WHERE ib.DeviceId = ypo.PAObjectId) )");

        SqlStatementBuilder limiter2 = new SqlStatementBuilder();
        limiter2.append("paobjectId IN (");
        limiter2.append("  SELECT deviceId");
        limiter2.append("  FROM inventoryBase ib ");
        limiter2.append("    JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
        limiter2.append("    JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        limiter2.append("    JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        limiter2.append("  WHERE etim.EnergyCompanyId ").in(energyCompanyIds);
        limiter2.append("    AND ib.accountId = 0 ");
        limiter2.append("    AND yle.YukonDefinitionId = ").append(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_YUKON_METER).append(")");
        
        SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();
        retVal.add(limiter1);
        retVal.add(limiter2);

        return retVal;
    }
    
}