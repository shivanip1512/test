package com.cannontech.cbc.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public interface CapControlCreationService {
	
	/**
	 * Creates a non-cbc cap control object with the given name and type
	 * @param paoType the type of the capcontrol object
	 * @param name the name of the capcontrol object
	 * @return the PaoIdentifier representing the newly created object.
	 */
	public PaoIdentifier createCapControlObject(PaoType paoType, String name, boolean disabled);
	
	/**
	 * Creates a new CBC object with the specified information
	 * @param paoType the type of CBC
	 * @param name the name of the CBC
	 * @param disabled whether or not the new CBC will be disabled
	 * @param portId for the comm channel the CBC will be operating on
	 * @return the PaoIdentifier representing the newly created CBC.
	 */
	public PaoIdentifier createCbc(PaoType paoType, String name, boolean disabled, int portId);
}
