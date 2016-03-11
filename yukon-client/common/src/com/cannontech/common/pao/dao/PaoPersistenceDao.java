package com.cannontech.common.pao.dao;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteYukonPao;

public interface PaoPersistenceDao {
    /**
     * Retrieve all of a PAO's information from the database.
     * 
     * @param paoIdentifier contains the paoId and type of the PAO to be retrieved.
     * @param klass The class of the object expected as a return value.
     * @return a PAO of type T completely populated with data.
     */
    <T extends CompleteYukonPao> T retreivePao(YukonPao pao, Class<T> klass);

    /**
     * Build the correct CompleteYukonPao for the given pao.  This method will find the correct subclass
     * of {@link CompleteYukonPao} for the type of the given PAO and return a instance of that class
     * populated from the database.
     */
    CompleteYukonPao retreivePao(YukonPao pao);

    /**
     * Create a PAO of a specific type. <b>This method should only be called from within the
     * PaoPersistenceService (which handles the DbChangeMessaging and Point creation automatically.)</b> If
     * you're calling this method from anywhere else, you're doing it wrong!
     * 
     * @param pao The PAO to be inserted into the database.
     * @param paoType The PaoType of the object being inserted.
     * @param points The points the PAO will be created with.
     */
    void createPao(CompleteYukonPao pao, PaoType paoType);

    /**
     * Update a PAO's information in the database. <b>This method should only be called from within the
     * PaoPersistenceService (which handles the DbChangeMessaging automatically.)</b> If you're calling this
     * method from anywhere else, you're doing it wrong!
     * 
     * @param pao the PAO whose database information is to be updated.
     */
    void updatePao(CompleteYukonPao pao);

    /**
     * Delete a PAO from the database. <b>This method should only be called from within the
     * PaoPersistenceService (which handles the DbChangeMessaging and Point deletion automatically.)</b> If
     * you're calling this method from anywhere else, you're doing it wrong!
     * 
     * @param paoIdentifier the {@link PaoIdentifier} containing the paoId of the PAO to be deleted.
     */
    void deletePao(YukonPao pao);

    /**
     * Returns true if pao is supported by PaoPersistence table mappings
     */
    public boolean supports(YukonPao pao);
}
