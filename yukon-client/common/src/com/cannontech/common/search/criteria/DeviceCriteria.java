package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class DeviceCriteria extends YukonObjectCriteriaHelper {

    public DeviceCriteria() {
        addCriteria("isDevice", "true", BooleanClause.Occur.SHOULD);
    }

}
