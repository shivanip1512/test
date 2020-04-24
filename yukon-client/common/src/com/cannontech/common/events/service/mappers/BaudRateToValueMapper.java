package com.cannontech.common.events.service.mappers;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.util.ObjectMapper;

public class BaudRateToValueMapper implements ObjectMapper<BaudRate, String> {

    @Override
    public String map(BaudRate from) throws ObjectMappingException {
        return from.getBaudRateValue().toString();
    }
}