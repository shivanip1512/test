package com.cannontech.common.dr.setup;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public class LoadGroupFilter {

    private String name;
    private List<PaoType> switchTypes = Lists.newArrayList()    ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PaoType> getSwitchTypes() {
        return switchTypes;
    }

    public void setSwitchTypes(List<PaoType> switchTypes) {
        this.switchTypes = switchTypes;
    }
}
