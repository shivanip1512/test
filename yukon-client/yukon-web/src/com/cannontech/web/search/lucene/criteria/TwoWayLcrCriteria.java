package com.cannontech.web.search.lucene.criteria;

import com.cannontech.common.pao.PaoType;

public class TwoWayLcrCriteria extends YukonObjectCriteriaHelper {
    
    public TwoWayLcrCriteria() {
        PaoType.getTwoWayLcrTypes().forEach(this::typeShouldOccur);
    }
}