package com.cannontech.common.pao.dao;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteYukonPao;

public interface PaoPersistenceDao {
    
    /**
     * This method is used to retrieve all of a PAO's information from the database.
     * @param paoIdentifier contains the paoId and type of the PAO to be retrieved.
     * @param klass The class of the object expected as a return value.
     * @return a PAO of type T completely populated with data.
     */
    public <T extends CompleteYukonPao> T retreivePao(PaoIdentifier paoIdentifier, Class<T> klass);
    
    /**
     * This method is used to create a PAO of a specific type. <b>This method should only be called
     * from within the PaoPersistenceService (which handles the DbChangeMessaging and Point 
     * creation automatically.)</b> If you're calling this method from anywhere else, you're doing
     * it wrong!
     * @param pao The PAO to be inserted into the database.
     * @param paoType The PaoType of the object being inserted.
     * @param points The points the PAO will be created with.
     */
    public void createPao(CompleteYukonPao pao, PaoType paoType);
    
    /**
     * This method is used to update a PAO's information in the database. <b>This method should 
     * only be called from within the PaoPersistenceService (which handles the DbChangeMessaging
     * automatically.)</b> If you're calling this method from anywhere else, you're doing
     * it wrong!
     * @param pao the PAO whose database information is to be updated.
     */
    public void updatePao(CompleteYukonPao pao);
    
    /**
     * This method is used to delete a PAO from the database. <b>This method should only be called
     * from within the PaoPersistenceService (which handles the DbChangeMessaging and Point 
     * deletion automatically.)</b> If you're calling this method from anywhere else, you're doing
     * it wrong!
     * @param paoIdentifier the {@link PaoIdentifier} containing the paoId of the PAO to be deleted.
     */
    public void deletePao(PaoIdentifier paoIdentifier);
}
