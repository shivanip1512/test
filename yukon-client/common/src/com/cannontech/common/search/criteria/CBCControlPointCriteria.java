package com.cannontech.common.search.criteria;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;

public class CBCControlPointCriteria extends PointDeviceCriteriaAdapter{

    public CBCControlPointCriteria() {
        super();
        //point should only belong to cap bank controller
        List <String> c = new ArrayList<String>();
        for (int i = 0; i < PAOGroups.STRING_CBC_7010.length; i++) {
            String type = PAOGroups.STRING_CBC_7010[i];
            c.add(type);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_7020.length; i++) {
            String type = PAOGroups.STRING_CBC_7020[i];
            c.add(type);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_EXPRESSCOM.length; i++) {
            String type = PAOGroups.STRING_CBC_EXPRESSCOM[i];
            c.add(type);
        }
        CriteriaRule r1 = new CriteriaRule (c, BooleanClause.Occur.SHOULD);
        rulesMap.put("paotype", r1);
        //point should only be a status point
        List <String> c2 = new ArrayList<String>();
        c2.add("Status");
        CriteriaRule r2 = new CriteriaRule (c2, BooleanClause.Occur.MUST);
        rulesMap.put("pointtype", r2);
    }

}
