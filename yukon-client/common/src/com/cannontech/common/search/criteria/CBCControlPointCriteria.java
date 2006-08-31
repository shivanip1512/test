package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;

public class CBCControlPointCriteria extends PointDeviceCriteriaHelper {

    public CBCControlPointCriteria() {
        super();
        //point should only belong to cap bank controller
        for (int i = 0; i < PAOGroups.STRING_CBC_7010.length; i++) {
            String type = PAOGroups.STRING_CBC_7010[i];
            addCriteria("paotype", type, BooleanClause.Occur.SHOULD);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_7020.length; i++) {
            String type = PAOGroups.STRING_CBC_7020[i];
            addCriteria("paotype", type, BooleanClause.Occur.SHOULD);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_EXPRESSCOM.length; i++) {
            String type = PAOGroups.STRING_CBC_EXPRESSCOM[i];
            addCriteria("paotype", type, BooleanClause.Occur.SHOULD);
        }
        //point should only be a status point
        addCriteria("pointtype", "Status", BooleanClause.Occur.MUST);
    }

}
