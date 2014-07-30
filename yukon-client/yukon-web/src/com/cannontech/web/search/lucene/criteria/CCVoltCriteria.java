package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.UnitOfMeasure;

public class CCVoltCriteria extends YukonObjectCriteriaHelper {
    
    public CCVoltCriteria() {
        super();
        //create all the rules for this criteria
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "StatusOutput", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "CalcStatus", BooleanClause.Occur.MUST_NOT);
        
        for (UnitOfMeasure oum : UnitOfMeasure.getCapControlVoltsUom()) {
            addCriteria("uomid", oum.getId(), BooleanClause.Occur.SHOULD);
        }
    }
    
}