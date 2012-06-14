package com.cannontech.common.search.criteria;

import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableList;

public class WaterUsageCriteria extends YukonObjectCriteriaHelper {

    private final static List<String> types = ImmutableList.of(PaoType.RFWMETER.getDbString());

    public WaterUsageCriteria() {
        for (String type : types) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }
}
