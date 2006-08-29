package com.cannontech.common.search.criteria;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.search.PointDeviceCriteria;

public class PointDeviceCriteriaAdapter implements PointDeviceCriteria {
protected Map rulesMap = new HashMap();
    public PointDeviceCriteriaAdapter() {
        super();
    }


    public Map getRulesMap() {
        return rulesMap;
    }

}
