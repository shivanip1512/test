package com.cannontech.common.events.service.mappers;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LiteYukonUserToNameMapper implements ObjectMapper<LiteYukonUser, String> {

    @Override
    public String map(LiteYukonUser from) throws ObjectMappingException {
        return from.getUsername();
    }

}
