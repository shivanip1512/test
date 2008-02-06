package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class StatusPointCriteria extends YukonObjectCriteriaHelper {
    
    public StatusPointCriteria() {
        super();
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST);
    }

}
