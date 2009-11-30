package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;

public class LtcTypeCriteria extends YukonObjectCriteriaHelper {

    public LtcTypeCriteria() {
        super();
        addCriteria("type", PAOGroups.STRING_LTC[0], BooleanClause.Occur.MUST);
    }

}