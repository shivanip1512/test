package com.cannontech.common.bulk.model;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.collect.Lists;

public class DevicePointValuesHolder {
    private PaoIdentifier paoIdentifier;
    private DisplayablePao displayablePao;
    private List<PointValueHolder> pointValues = Lists.newArrayList();
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public DisplayablePao getDisplayablePao() {
        return displayablePao;
    }

    public void setDisplayablePao(DisplayablePao displayablePao) {
        this.displayablePao = displayablePao;
    }

    public List<PointValueHolder> getPointValues() {
        return pointValues;
    }
    
    public void setPointValues(List<PointValueHolder> pointValues) {
        this.pointValues = pointValues;
    }
}
