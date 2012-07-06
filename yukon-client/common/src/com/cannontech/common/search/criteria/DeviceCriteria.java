package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class DeviceCriteria extends YukonObjectCriteriaHelper {

    public DeviceCriteria() {
        int i = 0;
        addCriteria("isDevice", "true", BooleanClause.Occur.SHOULD);
        // devices with paoid >= 1 (prevents picker from displaying system devices)
        addCriteria("paoid", "1", null, true, false, BooleanClause.Occur.MUST);
    }

}
