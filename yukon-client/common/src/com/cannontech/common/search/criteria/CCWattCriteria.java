package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.collect.ImmutableSet;

public class CCWattCriteria extends YukonObjectCriteriaHelper {
    private static final ImmutableSet<UnitOfMeasure> WATTS = UnitOfMeasure.getCapControlWattsUom();

    public CCWattCriteria() {
        super();
        //create all the rules for this criteria
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "StatusOutput", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "CalcStatus", BooleanClause.Occur.MUST_NOT);
        for (UnitOfMeasure oum : WATTS) {
            addCriteria("uomid", oum.getId(), BooleanClause.Occur.SHOULD);
        }
    }

}
