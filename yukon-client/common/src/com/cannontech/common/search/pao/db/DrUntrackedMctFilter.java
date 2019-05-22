package com.cannontech.common.search.pao.db;

import java.util.Set;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class DrUntrackedMctFilter implements SqlFilter {
    
    private PaoDefinitionDao paoDefinitionDao;
    
    public DrUntrackedMctFilter(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.STARS_ACCOUNT_ATTACHABLE_METER);
        paoTypes.addAll(PaoType.getRfMeterTypes());

        SqlStatementBuilder drTrackableMeter = new SqlStatementBuilder();
        drTrackableMeter.append("paobjectId IN (");
        drTrackableMeter.append("  SELECT paobjectId");
        drTrackableMeter.append("  FROM YukonPAObject ypo");
        drTrackableMeter.append("  WHERE ypo.type ").in(paoTypes);
        drTrackableMeter.append("    AND ypo.paobjectId NOT IN (SELECT deviceId FROM inventoryBase ib WHERE ib.DeviceId = ypo.PAObjectId) )");
        
        return drTrackableMeter;
    }
    
}