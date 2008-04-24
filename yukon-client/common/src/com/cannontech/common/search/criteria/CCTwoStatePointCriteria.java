package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class CCTwoStatePointCriteria extends YukonObjectCriteriaHelper {
    
    public CCTwoStatePointCriteria() {
        super();
        //create all the rules for this criteria
        //point for the dual bus should be
        //a. two-state state groups 
        //b. can't have name BANK STATUS
        
        //a
        addCriteria("stategroupid", 1, BooleanClause.Occur.SHOULD);
        addCriteria("stategroupid", 4, BooleanClause.Occur.SHOULD);
        addCriteria("stategroupid", 5, BooleanClause.Occur.SHOULD);
        addCriteria("stategroupid", -8, BooleanClause.Occur.SHOULD);
        //b
        addCriteria("pointName", "BANK STATUS", BooleanClause.Occur.MUST_NOT);
    }
}
