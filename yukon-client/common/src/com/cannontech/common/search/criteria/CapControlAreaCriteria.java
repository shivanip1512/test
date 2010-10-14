package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.CapControlType;

public class CapControlAreaCriteria extends YukonObjectCriteriaHelper {

    public CapControlAreaCriteria() {
        super();
        
        //create all the rules for this criteria
        addCriteria("type", CapControlType.AREA.getDbValue(), BooleanClause.Occur.SHOULD);
        addCriteria("type", CapControlType.SPECIAL_AREA.getDbValue(), BooleanClause.Occur.SHOULD);
    }

}