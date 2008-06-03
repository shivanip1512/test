package com.cannontech.web.bulk.model.collection;

import com.cannontech.web.bulk.model.DeviceCollectionProducer;

public abstract class DeviceCollectionProducerBase implements DeviceCollectionProducer {

    public String getParameterName(String shortName) {
        return getSupportedType() + "." + shortName;
    }

}
