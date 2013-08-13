package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class AnalogPointCriteria extends YukonObjectCriteriaHelper {
    
    public AnalogPointCriteria() {
        super();
        addCriteria("pointtype", "Analog", BooleanClause.Occur.SHOULD);
    }
}
