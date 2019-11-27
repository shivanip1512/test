package com.cannontech.web.search.lucene.criteria;

import com.cannontech.database.data.point.PointType;

public class AnalogPointCriteria extends YukonObjectCriteriaHelper {
    
    public AnalogPointCriteria() {
        super();
        typeShouldOccur(PointType.Analog);
    }
    
}