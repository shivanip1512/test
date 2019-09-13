package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoCategory;

public class DevicePointCriteria extends YukonObjectCriteriaHelper {

    public DevicePointCriteria() {
        super();
        addCriteria("category", PaoCategory.DEVICE.getDbString(), BooleanClause.Occur.SHOULD);
    }
}