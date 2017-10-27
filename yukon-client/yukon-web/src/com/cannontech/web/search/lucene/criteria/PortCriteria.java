package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class PortCriteria extends YukonObjectCriteriaHelper {

    public PortCriteria() {
        super();
        PaoType.getPortTypes().stream().forEach(portType -> {
            addCriteria("type", portType.getDbString(), BooleanClause.Occur.SHOULD);
        });
    }
}
