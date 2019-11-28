package com.cannontech.web.search.lucene.criteria;

import com.cannontech.database.data.point.PointType;

public class AnalogAndCalculatedPointCriteria extends YukonObjectCriteriaHelper {
    
    public AnalogAndCalculatedPointCriteria() {
        super();
        typeShouldOccur(PointType.Analog);
        typeShouldOccur(PointType.CalcAnalog);
    }
}
