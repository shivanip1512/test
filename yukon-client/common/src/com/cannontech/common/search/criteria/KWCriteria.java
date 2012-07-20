package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.UnitOfMeasure;

public class KWCriteria extends YukonObjectCriteriaHelper {
    
    public KWCriteria() {
        super();
        addCriteria("uomid", UnitOfMeasure.KW.getId(), BooleanClause.Occur.SHOULD);
    }

}