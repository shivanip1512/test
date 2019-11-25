package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

public class AnalogAndCalculatedPointCriteria extends YukonObjectCriteriaHelper {
    
    public AnalogAndCalculatedPointCriteria() {
        super();
        addCriteria("pointtype", "Analog", BooleanClause.Occur.SHOULD);
        addCriteria("pointtype", "CalcAnalog", BooleanClause.Occur.SHOULD);
    }
}
