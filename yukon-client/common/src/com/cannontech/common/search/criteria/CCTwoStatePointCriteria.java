package com.cannontech.common.search.criteria;

import javax.annotation.PostConstruct;

import org.apache.lucene.search.BooleanClause;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteStateGroup;

public class CCTwoStatePointCriteria extends YukonObjectCriteriaHelper {

    @Autowired private StateDao stateDao;

    @PostConstruct
    public void init() {
        //create all the rules for this criteria
        //point for the dual bus should be
        //a. two-state state groups 
        //b. can't have name BANK STATUS
        
        //a
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        for (LiteStateGroup group : allStateGroups) {
            if (group.getStatesList().size() == 2) {
                addCriteria("stategroupid", group.getStateGroupID(), BooleanClause.Occur.SHOULD);
            }
        }

        //b
        addCriteria("pointName", "BANK STATUS", BooleanClause.Occur.MUST_NOT);
    }
}
