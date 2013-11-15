package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;

public class CbcCriteria extends YukonObjectCriteriaHelper {
    
    public CbcCriteria() {
        super();
        addCbcDeviceCriteria(this);
    }

    public static void addCbcDeviceCriteria(YukonObjectCriteriaHelper criteria) {
        //point should only belong to cap bank controller
        for (int i = 0; i < PAOGroups.STRING_CBC_DNP.length; i++) {
            String type = PAOGroups.STRING_CBC_DNP[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_7010.length; i++) {
            String type = PAOGroups.STRING_CBC_7010[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_7020.length; i++) {
            String type = PAOGroups.STRING_CBC_7020[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
        for (int i = 0; i < PAOGroups.STRING_CBC_EXPRESSCOM.length; i++) {
            String type = PAOGroups.STRING_CBC_EXPRESSCOM[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
        
        for (int i = 0; i < PAOGroups.STRING_CAP_BANK_CONTROLLER.length; i++) {
            String type = PAOGroups.STRING_CAP_BANK_CONTROLLER[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
        
        for (int i = 0; i < PAOGroups.STRING_CBC_FP_2800.length; i++) {
            String type = PAOGroups.STRING_CBC_FP_2800[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
        
        for (int i = 0; i < PAOGroups.STRING_RTU_X.length; i++) {
            String type = PAOGroups.STRING_RTU_X[i];
            criteria.addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }
}
