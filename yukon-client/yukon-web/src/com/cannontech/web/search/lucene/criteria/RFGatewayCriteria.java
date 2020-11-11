package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class RFGatewayCriteria extends YukonObjectCriteriaHelper {

    public RFGatewayCriteria() {
        super();
        PaoType.getRfGatewayTypes().stream().forEach(rfGatewayType -> {
            if (rfGatewayType == PaoType.RFN_GATEWAY) {
                addCriteria("type", rfGatewayType.getDbString(), BooleanClause.Occur.SHOULD);
            }
        });
    }
}