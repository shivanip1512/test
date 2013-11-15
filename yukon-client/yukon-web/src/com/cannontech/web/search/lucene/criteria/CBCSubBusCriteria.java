package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

public class CBCSubBusCriteria extends YukonObjectCriteriaHelper{
    private static final String[] TYPES =  {"CCSUBBUS"};//just busses

    public CBCSubBusCriteria() {
        super();
        //create all the rules for this criteria
        for (int i = 0; i < TYPES.length; i++) {
            addCriteria("type", TYPES[i], BooleanClause.Occur.SHOULD);
        }
    }
}
