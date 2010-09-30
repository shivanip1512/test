package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.CapControlType;

public class CapControlAreaCriteria extends YukonObjectCriteriaHelper {

    public CapControlAreaCriteria() {
        super();
        
        //create all the rules for this criteria
        addCriteria("type", (String)CapControlType.AREA.getDisplayValue(), BooleanClause.Occur.SHOULD);
        addCriteria("type", (String)CapControlType.SPECIAL_AREA.getDisplayValue(), BooleanClause.Occur.SHOULD);
    }

}