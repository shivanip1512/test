package com.cannontech.common.dr.setup;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public class LoadGroupFilter extends FilterBase {

    private List<PaoType> switchTypes = Lists.newArrayList();

    public List<PaoType> getSwitchTypes() {
        return switchTypes;
    }

    public void setSwitchTypes(List<PaoType> switchTypes) {
        this.switchTypes = switchTypes;
    }
}
