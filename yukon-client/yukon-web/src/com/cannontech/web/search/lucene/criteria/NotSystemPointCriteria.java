package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.PointType;

public class NotSystemPointCriteria extends YukonObjectCriteriaHelper {
    
    public NotSystemPointCriteria() {
        super();
        for (PointType type : PointType.values()) {
            if (type != PointType.System) {
                addCriteria("pointtype", type.getPointTypeString(), BooleanClause.Occur.SHOULD);
            }
        }
    }
    
}