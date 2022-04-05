package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;
import com.cannontech.common.pao.PaoClass;

public class DeviceCriteria extends YukonObjectCriteriaHelper {
    
    public DeviceCriteria() {
        addCriteria("isDevice", "true", BooleanClause.Occur.SHOULD);
        addCriteria("paoclass", PaoClass.GROUP.getDbString(), BooleanClause.Occur.MUST_NOT);
        // devices with paoid >= 1 (prevents picker from displaying system devices)
        addCriteria("paoid", "1", null, true, false, BooleanClause.Occur.MUST);
    }
    
}