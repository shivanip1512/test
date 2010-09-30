package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.CapControlType;

public class CapBankCriteria extends YukonObjectCriteriaHelper {

    public CapBankCriteria() {
        super();
        
        //create all the rules for this criteria
        addCriteria("type", (String)CapControlType.CAPBANK.getDisplayValue(), BooleanClause.Occur.MUST);
    }

}