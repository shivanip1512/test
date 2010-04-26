package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.PointUnits;

public class CCWattCriteria extends YukonObjectCriteriaHelper {
    private static final Integer[] UNITS = PointUnits.CAP_CONTROL_WATTS_UOMIDS;

    public CCWattCriteria() {
        super();
        //create all the rules for this criteria
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "StatusOutput", BooleanClause.Occur.MUST_NOT);
        addCriteria("pointtype", "CalcStatus", BooleanClause.Occur.MUST_NOT);
        for (int i = 0; i < UNITS.length; i++) {
            addCriteria("uomid", UNITS[i], BooleanClause.Occur.SHOULD);
        }
    }

}
