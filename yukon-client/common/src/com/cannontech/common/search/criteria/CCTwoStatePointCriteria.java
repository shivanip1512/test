package com.cannontech.common.search.criteria;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

public class CCTwoStatePointCriteria extends PointDeviceCriteriaAdapter {
    
    public CCTwoStatePointCriteria() {
        super();
        //create all the rules for this criteria
        //point for the dual bus should be
        //a. two state
        //b. not a status point or CALCULATED_STATUS_POINT
        //c. can't have name BANK STATUS
        List c1 = new ArrayList<String>();
        //a
        c1.add("1");
        CriteriaRule r1= new CriteriaRule (c1, BooleanClause.Occur.SHOULD);
        rulesMap.put("stategroupid", r1);
        //b
        List c2 = new ArrayList<String>();
        c2.add("CalcStatus");
        CriteriaRule r2 = new CriteriaRule (c2, BooleanClause.Occur.MUST_NOT);
        rulesMap.put("pointtype", r2);
        //c
        List c3 = new ArrayList<String>();
        c3.add ("BANK STATUS");
        CriteriaRule r3 = new  CriteriaRule (c3, BooleanClause.Occur.MUST_NOT);
        rulesMap.put("point", r3);
    }


    

}
