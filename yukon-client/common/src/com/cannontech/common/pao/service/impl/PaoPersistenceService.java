package com.cannontech.common.pao.service.impl;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.pojo.CompleteYukonPaObject;
import com.cannontech.database.data.point.PointBase;

public interface PaoPersistenceService {
    
    /**
     * This method is used to retrieve all of a PAO's information from the database.
     * @param paoIdentifier contains the paoId and type of the PAO to be retrieved.
     * @param klass The class of the object expected as a return value.
     * @return a PAO of type T completely populated with data.
     */
    public <T extends CompleteYukonPaObject> T retreivePao(PaoIdentifier paoIdentifier, Class<T> klass);
    
    /**
     * This method is used to create a PAO of a specific type using default points for that type.
     * @param pao The PAO to be inserted into the database.
     * @param paoType The PaoType of the object being inserted.
     */
    public void createPao(CompleteYukonPaObject pao, PaoType paoType);
    
    /**
     * This method is used to create a PAO of a specific type using custom points. This method is 
     * generally used in template copying of objects, and when there isn't a specific reason to 
     * use a list of custom points, createPao should be used instead.
     * @param pao
     * @param paoType
     * @param points
     */
    public void createPaoWithCustomPoints(CompleteYukonPaObject pao, PaoType paoType, List<PointBase> points);
    
    /**
     * This method is used to update a PAO's information in the database. <b>This method should only 
     * ever be called after a retrieve has been called first to populate the PAO with its current 
     * database incarnation.</b> Once modifications to the retrieved version of the PAO are made, 
     * update should be called. If retrieve is not called first, the PAOs information will possibly
     * be overwritten with default values.
     * @param pao the PAO whose database information is to be updated.
     */
    public void updatePao(CompleteYukonPaObject pao);
    
    /**
     * This method is used to delete a PAO from the database.
     * @param paoIdentifier the {@link PaoIdentifier} containing the paoId of the PAO to be deleted.
     */
    public void deletePao(PaoIdentifier paoIdentifier);
}
