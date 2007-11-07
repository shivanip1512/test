package com.cannontech.common.util;

import com.cannontech.common.bulk.mapper.IgnoreMappingException;
import com.cannontech.common.bulk.mapper.ObjectMappingException;

public interface ObjectMapper<J, K> {

    /**
     * Method to map an object of type J to an object of type K
     * @param from - Object to map
     * @return - Mapped object
     * @throws ObjectMappingException if there was a problem mappint the object
     * @throws IgnoreMappingException if the mapping was ignored
     */
    public K map(J from) throws ObjectMappingException, IgnoreMappingException;
}
