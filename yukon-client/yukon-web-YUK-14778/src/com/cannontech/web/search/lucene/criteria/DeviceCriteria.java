package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

public class DeviceCriteria extends YukonObjectCriteriaHelper {
    
    public DeviceCriteria() {
        addCriteria("isDevice", "true", BooleanClause.Occur.SHOULD);
        // devices with paoid >= 1 (prevents picker from displaying system devices)
        addCriteria("paoid", "1", null, true, false, BooleanClause.Occur.MUST);
    }
    
}