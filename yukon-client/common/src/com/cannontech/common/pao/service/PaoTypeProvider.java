package com.cannontech.common.pao.service;

import com.cannontech.common.pao.PaoIdentifier;

public interface PaoTypeProvider<T extends PaoTemplatePart> {
    
    /**
     * Returns the table that the provider's functionality supports.
     * @return {@link PaoProviderTable} the name of the supported table.
     */
	public PaoProviderTable getSupportedTable();
	
	/**
	 * Returns the fields object that the provider's functionality requires.
	 * @return Class - the class of the PaoTemplatePart fields object.
	 */
    public Class<T> getRequiredFields();
    
    /**
     * Performs the SQL call to insert Pao data into the table the provider supports. 
     * This function should only ever be called by the 
     * {@link PaoCreationService}.<code>callProviderCreation()</code> method.
     * @param paoIdentifier containing the PaoID of the pao being created.
     * @param fields containing the data to be inserted to the supported table.
     */
    public void handleCreation(PaoIdentifier paoIdentifier, T fields);
    
    /**
     * Performs the SQL call to update Pao data in the table the provider supports. 
     * This function should only ever be called by the 
     * {@link PaoCreationService}.<code>callProviderUpdate()</code> method.
     * @param paoIdentifier containing the PaoID of the pao being updated.
     * @param fields containing the data to be updated in the supported table.
     */
    public void handleUpdate(PaoIdentifier paoIdentifier, T fields);
    
    /**
     * Performs the SQL call to insert Pao data into the table the provider supports. 
     * This function should only ever be called by the 
     * {@link PaoCreationService}.<code>deletePao()</code> method.
     * @param paoIdentifier containing the PaoID of the pao being deleted.
     */
    public void handleDeletion(PaoIdentifier paoIdentifier);
}
