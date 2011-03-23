package com.cannontech.common.pao.service;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ClassToInstanceMap;

public class PaoTemplate {
    private PaoType paoType;
    private ClassToInstanceMap<PaoTemplatePart> paoFields;
    
    public PaoTemplate(PaoType paoType, ClassToInstanceMap<PaoTemplatePart> paoFields) {
        this.paoType = paoType;
        this.paoFields = paoFields;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
    
    public ClassToInstanceMap<PaoTemplatePart> getPaoFields() {
        return paoFields;
    }
    
}
