package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class SignalTransmitterCriteria extends YukonObjectCriteriaHelper {

    public SignalTransmitterCriteria() {
        super();
        PaoType.getTransmitterTypes().stream().forEach(transmitterType -> {
            if (!transmitterType.isCcu() && transmitterType != PaoType.ROUTE_CCU) {
                addCriteria("type", transmitterType.getDbString(), BooleanClause.Occur.SHOULD);
            }
        });
    }
}
