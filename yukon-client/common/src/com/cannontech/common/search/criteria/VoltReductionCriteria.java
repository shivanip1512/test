package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class VoltReductionCriteria extends YukonObjectCriteriaHelper {
    
    public VoltReductionCriteria() {
        super();
        addCriteria("pointtype", "Status", BooleanClause.Occur.SHOULD);
        addCriteria("pointtype", "CalcStatus", BooleanClause.Occur.SHOULD);
    }

}
