package com.cannontech.messaging.serialization.thrift.serializer;

import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.CANCELED;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.CONFIRMING;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.FAILED;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.IDLE;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.INITIATING;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.MISMATCHED;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.UPLOADING;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source.PORTER;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source.SM_CONFIG_FAILURE;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source.SM_STATUS_ARCHIVE;
import static com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source.WS_COLLECTION_ACTION;

import java.util.Map;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest.Source;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;

public class MeterProgramStatusArchiveRequestSerializer extends SimpleThriftSerializer implements 
    ThriftByteDeserializer<MeterProgramStatusArchiveRequest>, 
    ThriftByteSerializer<MeterProgramStatusArchiveRequest> {

    private static final BiMap<Source, com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source> sourceMapping = 
            EnumBiMap.create(Map.of(Source.PORTER, PORTER,
                                    Source.SM_CONFIG_FAILURE, SM_CONFIG_FAILURE,
                                    Source.SM_STATUS_ARCHIVE, SM_STATUS_ARCHIVE,
                                    Source.WS_COLLECTION_ACTION, WS_COLLECTION_ACTION));

    private static final BiMap<ProgrammingStatus, com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus> statusMapping = 
            EnumBiMap.create(Map.of(ProgrammingStatus.CANCELED, CANCELED,
                                    ProgrammingStatus.CONFIRMING, CONFIRMING,
                                    ProgrammingStatus.FAILED, FAILED,
                                    ProgrammingStatus.IDLE, IDLE,
                                    ProgrammingStatus.INITIATING, INITIATING,
                                    ProgrammingStatus.MISMATCHED, MISMATCHED,
                                    ProgrammingStatus.UPLOADING, UPLOADING));

    @Override
    public byte[] toBytes(MeterProgramStatusArchiveRequest requestPayload) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.meterProgramming.MeterProgramStatusArchiveRequest();
        
        entity.setConfigurationId(requestPayload.getConfigurationId());
        entity.setError(requestPayload.getError().getCode());
        
        var rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier();
        rfnIdentifier.setSensorManufacturer(requestPayload.getRfnIdentifier().getSensorManufacturer());
        rfnIdentifier.setSensorModel(requestPayload.getRfnIdentifier().getSensorModel());
        rfnIdentifier.setSensorSerialNumber(requestPayload.getRfnIdentifier().getSensorSerialNumber());
        entity.setRfnIdentifier(rfnIdentifier);

        entity.setSource(sourceMapping.get(requestPayload.getSource()));
        entity.setStatus(statusMapping.get(requestPayload.getStatus()));
        entity.setTimeStamp(requestPayload.getTimeStamp());
        
        return serialize(entity);
    }

    @Override
    public MeterProgramStatusArchiveRequest fromBytes(byte[] msgBytes) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.meterProgramming.MeterProgramStatusArchiveRequest();

        deserialize(msgBytes, entity);
        
        var message = new MeterProgramStatusArchiveRequest();
        
        message.setConfigurationId(entity.getConfigurationId());
        message.setError(DeviceError.getErrorByCode(entity.getError()));
        
        var rfnIdentifier = new RfnIdentifier(entity.getRfnIdentifier().getSensorSerialNumber(),
                                              entity.getRfnIdentifier().getSensorManufacturer(),
                                              entity.getRfnIdentifier().getSensorModel());
        message.setRfnIdentifier(rfnIdentifier);

        message.setSource(sourceMapping.inverse().get(entity.getSource()));
        message.setStatus(statusMapping.inverse().get(entity.getStatus()));
        message.setTimeStamp(entity.getTimeStamp());

        return message;
    }

}
