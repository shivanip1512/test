package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.UnitOfMeasure;


public class CCVarCriteria extends YukonObjectCriteriaHelper {
    
    public CCVarCriteria() {
        super();
        //create all the rules for this criteria
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "StatusOutput", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "CalcStatus", BooleanClause.Occur.MUST_NOT);
        
        for (UnitOfMeasure oum : UnitOfMeasure.getCapControlVarUom()) {
            addCriteria("uomid", oum.getId(), BooleanClause.Occur.SHOULD);
        }
    }
    
}