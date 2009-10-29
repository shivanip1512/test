package com.cannontech.core.service.impl;

import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;

/**
 * This interface is implemented by an object that can load objects associated with PAOs in a certain manner.
 * 
 * For a given type of object (0), it is assumed that several different loaders will be involved
 * and that their coordination will be managed by the PaoLoadingService.
 *
 * @param <O> The type of object to be loaded.
 */
public interface PaoLoader<O> {
	/**
	 * This method will return as many of the O objects as possible for the given 
	 * identifiers. It is expected that the number of keys in the result could
	 * be less than the number of identifiers.
	 * 
	 * Implementers of this method are expected to copy the identifier reference
	 * exactly into the key of the map.
	 * 
	 * @param <T> The type of the identifier
	 * @param identifiers
	 * @return
	 */
	public Map<PaoIdentifier,O> getForPaos(Iterable<PaoIdentifier> identifiers);

}
