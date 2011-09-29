package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.PaoProviderTableEnum;

public interface PaoCreationTableParserService {

    /**
     * Returns the table names required for the creation of a pao of the given type
     * @param paoType the PaoType of the Pao being created.
     * @return a list of PaoProviderTableEnums representing all the tables required for
     * creating/deleting the Pao of the given type.
     */
	public List<PaoProviderTableEnum> parseTableNames(PaoType paoType);
}
