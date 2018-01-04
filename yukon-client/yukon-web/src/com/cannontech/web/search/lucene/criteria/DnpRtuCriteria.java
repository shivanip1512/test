package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class DnpRtuCriteria extends YukonObjectCriteriaHelper {

    public DnpRtuCriteria() {
        super();
        addCriteria("type", PaoType.RTU_DNP.getDbString(), BooleanClause.Occur.SHOULD);
    }
}
