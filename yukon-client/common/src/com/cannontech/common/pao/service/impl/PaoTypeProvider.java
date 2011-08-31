package com.cannontech.common.pao.service.impl;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.PaoTemplatePart;

public interface PaoTypeProvider<T extends PaoTemplatePart> {
    
	public PaoProviderTableEnum getSupportedTable();
	
    public Class<T> getRequiredFields();
    
    public void handleCreation(PaoIdentifier paoIdentifier, T fields);
    
    public void handleUpdate(PaoIdentifier paoIdentifier, T fields);
    
    public void handleDeletion(PaoIdentifier paoIdentifier);

}
