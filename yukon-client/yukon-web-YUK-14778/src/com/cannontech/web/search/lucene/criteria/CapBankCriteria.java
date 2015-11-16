package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.CapControlType;

public class CapBankCriteria extends YukonObjectCriteriaHelper {
    
    public CapBankCriteria() {
        super();
        
        addCriteria("type", CapControlType.CAPBANK.getDbValue(), BooleanClause.Occur.MUST);
    }
    
}