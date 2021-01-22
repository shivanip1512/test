package com.cannontech.messaging.serialization.thrift.serializer;

import java.util.List;
import java.util.function.Function;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.google.common.collect.Lists;

public class RfnSerializationHelper extends SimpleThriftSerializer {

    protected static com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier 
            convert(RfnIdentifier msg) {
        var thr = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier();
        
        thr.setSensorManufacturer(
                msg.getSensorManufacturer());
        thr.setSensorModel(
                msg.getSensorModel());
        thr.setSensorSerialNumber(
                msg.getSensorSerialNumber());
        
        return thr;
    }

    protected static RfnIdentifier convert(com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier msg) {
        return new RfnIdentifier(
                msg.getSensorSerialNumber(),
                msg.getSensorManufacturer(),
                msg.getSensorModel());
    }
    
    protected static <D1, D2> List<D2> convertList(List<D1> inputList, Function<D1, D2> itemConverter) {
        return Lists.transform(inputList, itemConverter::apply);
    }
}
