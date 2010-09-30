package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class CBCControlPointCriteria extends YukonObjectCriteriaHelper {

    public CBCControlPointCriteria() {
        super();
        
        //Should be a cbc device
        CbcCriteria.addCbcDeviceCriteria(this);

        //point should only be a status point
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST);
        addCriteria("pointoffset", "1", BooleanClause.Occur.MUST);
    }

}
