package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

public class DevicePointCriteria extends YukonObjectCriteriaHelper {

    public DevicePointCriteria() {
        super();
        addCriteria("category", "DEVICE", BooleanClause.Occur.SHOULD);
    }
}