package com.cannontech.common.pao.service;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.point.PointBase;

public interface PaoCreationService {
    /**
     * Creates a Pao using the paoTemplate passed in.
     * 
     * This will call the CreationProviders that support the PaoType to create all of the required tables.
     */
    public PaoIdentifier createPao(PaoTemplate paoTemplate);
    
    public PaoIdentifier createTemplatePao(PaoTemplate paoTemplate, List<PointBase> points);
    
    public void updatePao(int paoId, PaoTemplate paoTemplate);
    
    public void deletePao(PaoIdentifier paoIdentifier);

}
