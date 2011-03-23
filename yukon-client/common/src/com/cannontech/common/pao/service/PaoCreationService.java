package com.cannontech.common.pao.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.google.common.collect.ClassToInstanceMap;

public interface PaoCreationService {
    /**
     * Creates a Pao using the paoTemplate passed in.
     * 
     * This will call the CreationProviders that support the PaoType to create all of the required tables.
     */
    public PaoIdentifier createPao(PaoTemplate paoTemplate);
    
    /**
     * Creates a map with a default entry of NullFields. PaoTemplate objects that use NullFields don't
     * have to add it manually.
     * 
     * @return
     */
    public ClassToInstanceMap<PaoTemplatePart> createFieldMap();

}
