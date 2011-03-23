package com.cannontech.common.pao.service.providers;

import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.impl.PaoCreationTypeProvider;

public abstract class  BaseCreationProvider<T extends PaoTemplatePart> implements PaoCreationTypeProvider<T> {

    @Override
    public int compareTo(PaoCreationTypeProvider<T> o) {
        return Float.compare(getOrder(), o.getOrder());
    }
    
}
