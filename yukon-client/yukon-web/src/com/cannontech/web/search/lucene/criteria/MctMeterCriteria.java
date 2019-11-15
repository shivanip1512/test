package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class MctMeterCriteria extends YukonObjectCriteriaHelper {

    public MctMeterCriteria() {
        super();
        PaoType.getMctTypes().stream().forEach(mctType -> {
            addCriteria("type", mctType.getDbString(), BooleanClause.Occur.SHOULD);
        });
    }
}