package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.PointUnits;

public class KWCriteria extends PointDeviceCriteriaHelper {
    private static final Integer[] UNITS =  {PointUnits.UOMID_KW};

    public KWCriteria() {
        super();
        //create all the rules for this criteria
        for (int i = 0; i < UNITS.length; i++) {
            addCriteria("uomid", i, BooleanClause.Occur.SHOULD);
        }
    }

}
