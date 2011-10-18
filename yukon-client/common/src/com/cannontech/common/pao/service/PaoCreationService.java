package com.cannontech.common.pao.service;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.point.PointBase;

public interface PaoCreationService {
    
    /**
     * Creates a Pao with default points using the paoTemplate passed in.
     * This will call the CreationProviders that support the PaoType to create all of the required tables.
     * @param paoTemplate the template containing the fields objects of the data to be created.
     * @return {@link PaoIdentifier} which contains the type and PaoId of the newly created Pao.
     * @throws IllegalArgumentException if the PaoTemplate is missing fields objects required 
     * for creation.
     */
    public PaoIdentifier createPao(PaoTemplate paoTemplate);
    
    /**
     * Creates a new pao with the specified information and the specific points provided rather 
     * than the default points as in the createPao function.
     * @param paoTemplate the template containing the fields objects of the data to be created.
     * @param points list of specific points to be added to the newly created pao.
     * @return {@link PaoIdentifier} which contains the type and PaoId of the newly created Pao.
     * @throws IllegalArgumentException if the PaoTemplate is missing fields objects required 
     * for creation.
     */
    public PaoIdentifier createPaoWithCustomPoints(PaoTemplate paoTemplate, List<PointBase> points);
    
    /**
     * Updates pao's information in the database. This will only update the information 
     * provided in the fields objects of the PaoTemplate.
     * @param paoId the id of the pao being updated.
     * @param paoTemplate the template containing the fields objects of the data to be updated.
     */
    public void updatePao(int paoId, PaoTemplate paoTemplate);
    
    /**
     * Deletes the pao with a specified PaoId from the database and all points associated
     * with the pao.
     * @param paoIdentifier containing the PaoId of the Pao to be deleted.
     * @throws IllegalArgumentException i
     */
    public void deletePao(PaoIdentifier paoIdentifier);

}
