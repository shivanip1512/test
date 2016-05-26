package com.cannontech.web.search.lucene.criteria;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.search.BooleanClause;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteStateGroup;

public class CCTwoStatePointCriteria extends YukonObjectCriteriaHelper {
    
    @Autowired private StateGroupDao stateGroupDao;
    
    @PostConstruct
    public void init() {
        // Only groups with two states 
        List<LiteStateGroup> allStateGroups = stateGroupDao.getAllStateGroups();
        for (LiteStateGroup group : allStateGroups) {
            if (group.getStatesList().size() == 2) {
                addCriteria("stategroupid", group.getStateGroupID(), BooleanClause.Occur.SHOULD);
            }
        }
        // Any point except BANK STATUS
        addCriteria("pointName", "BANK STATUS", BooleanClause.Occur.MUST_NOT);
    }
    
}