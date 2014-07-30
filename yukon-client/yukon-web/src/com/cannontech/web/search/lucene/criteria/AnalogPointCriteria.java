package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

public class AnalogPointCriteria extends YukonObjectCriteriaHelper {
    
    public AnalogPointCriteria() {
        super();
        addCriteria("pointtype", "Analog", BooleanClause.Occur.SHOULD);
    }
    
}