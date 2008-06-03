/**
 * 
 */
package com.cannontech.common.bulk.mapper;

import com.cannontech.common.util.ObjectMapper;

public final class PassThroughMapper<I> implements ObjectMapper<I, I> {
    public I map(I from) throws ObjectMappingException {

        return from;

    }
}