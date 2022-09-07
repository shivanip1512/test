package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;

public class RouteCriteria extends YukonObjectCriteriaHelper {

    public RouteCriteria() {
        super();
        addCriteria("category", PaoCategory.ROUTE.getDbString(), BooleanClause.Occur.SHOULD);
        addCriteria("type", PaoType.ROUTE_MACRO.getDbString(), BooleanClause.Occur.MUST_NOT);
    }
}