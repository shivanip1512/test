package com.cannontech.common.bulk.mapper;

import com.cannontech.common.util.ObjectMapper;

public final class StringToIntegerMapper implements ObjectMapper<String, Integer> {
    public Integer map(String idStr) throws ObjectMappingException {

        try {
            int id = Integer.parseInt(idStr.trim());
            return id;
        } catch (NumberFormatException e) {
            throw new ObjectMappingException("Could not interpret as integer: " + idStr, e);
        }

    }
}