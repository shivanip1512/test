package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class CCTwoStatePointCriteria extends YukonObjectCriteriaHelper {
    
    public CCTwoStatePointCriteria() {
        super();
        //create all the rules for this criteria
        //point for the dual bus should be
        //a. two state
        //b. not a status point or CALCULATED_STATUS_POINT
        //c. can't have name BANK STATUS
        //a
        addCriteria("stategroupid", 1, BooleanClause.Occur.SHOULD);
        //b
        addCriteria("pointtype", "CalcStatus", BooleanClause.Occur.MUST_NOT);
        //c
        addCriteria("pointName", "BANK STATUS", BooleanClause.Occur.MUST_NOT);
    }


    

}
