package com.cannontech.common.search.criteria;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.point.PointUnits;

public class CCVoltCriteria extends PointDeviceCriteriaAdapter {
    private static final Integer[] UNITS = PointUnits.CAP_CONTROL_VOLTS_UOMIDS;

    public CCVoltCriteria() {
        super();
        //create all the rules for this criteria
        List c = new ArrayList<String>();
        for (int i = 0; i < UNITS.length; i++) {
            c.add( Integer.toString( UNITS [i]) );
        }
        CriteriaRule r1 = new CriteriaRule (c, BooleanClause.Occur.SHOULD);
        rulesMap .put("uomid", r1);
    }


}
