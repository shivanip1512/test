package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

public class CBCDeviceCriteria extends YukonObjectCriteriaHelper {
    private static final String[] TYPES =  {"CCAREA" };//just area's for now.

    public CBCDeviceCriteria() {
        super();
        //create all the rules for this criteria
        for (int i = 0; i < TYPES.length; i++) {
            addCriteria("type", TYPES[i], BooleanClause.Occur.SHOULD);
        }
    }

}