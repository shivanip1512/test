package com.cannontech.common.pao.service;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.database.data.point.PointBase;

public interface PaoPersistenceService {
    /**
     * Retrieve all of a PAO's information from the database.
     * 
     * @param paoIdentifier contains the paoId and type of the PAO to be retrieved.
     * @param klass The class of the object expected as a return value.
     * @return a PAO of type T completely populated with data.
     */
    <T extends CompleteYukonPao> T retreivePao(PaoIdentifier paoIdentifier, Class<T> klass);

    /**
     * Create a PAO of a specific type with default set of points.
     * 
     * @param pao The PAO to be inserted into the database.
     * @param paoType The PaoType of the object being inserted.
     */
    void createPaoWithDefaultPoints(CompleteYukonPao pao, PaoType paoType);

    /**
     * Create a PAO of a specific type using custom points. This method is generally used in template copying
     * of objects, and when there isn't a specific reason to use a list of custom points, createPao should be
     * used instead.
     */
    void createPaoWithCustomPoints(CompleteYukonPao pao, PaoType paoType, List<PointBase> points);

    /**
     * Update a PAO's information in the database. <b>This method should only ever be called after a retrieve
     * has been called first to populate the PAO with its current database incarnation.</b> Once modifications
     * to the retrieved version of the PAO are made, update should be called. If retrieve is not called first,
     * the PAOs information will possibly be overwritten with default values.
     * 
     * @param pao the PAO whose database information is to be updated.
     */
    void updatePao(CompleteYukonPao pao);

    /**
     * Delete a PAO from the database.
     * 
     * @param paoIdentifier the {@link PaoIdentifier} containing the paoId of the PAO to be deleted.
     */
    void deletePao(PaoIdentifier paoIdentifier);
}
