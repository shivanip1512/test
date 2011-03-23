package com.cannontech.common.pao.service.impl;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.PaoTemplatePart;

public interface PaoCreationTypeProvider<T extends PaoTemplatePart> extends Comparable<PaoCreationTypeProvider<T>> {
    
    public float getOrder();

    public boolean isTypeSupported(PaoType paoType);
    
    public Class<T> getRequiredFields();
    
    public void handleCreation(PaoIdentifier paoIdentifier, T fields);

}
