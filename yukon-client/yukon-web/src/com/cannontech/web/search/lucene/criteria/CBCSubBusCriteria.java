package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.CapControlType;

public class CBCSubBusCriteria extends YukonObjectCriteriaHelper{
    
    public CBCSubBusCriteria() {
        super();
        addCriteria("type", CapControlType.SUBBUS.getDbValue(), BooleanClause.Occur.SHOULD);
    }
    
}