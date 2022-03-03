package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class RFGatewayLegacyCriteria extends YukonObjectCriteriaHelper {

    public RFGatewayLegacyCriteria() {
        super();
        addCriteria("type", PaoType.RFN_GATEWAY.getDbString(), BooleanClause.Occur.SHOULD);
    }
}