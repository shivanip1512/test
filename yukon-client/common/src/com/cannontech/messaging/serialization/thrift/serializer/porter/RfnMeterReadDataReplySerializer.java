package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadDataReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;
import com.cannontech.messaging.serialization.thrift.util.ThriftEnumHelper;

public class RfnMeterReadDataReplySerializer extends RfnSerializationHelper implements ThriftByteDeserializer<RfnMeterReadDataReply> {

    private static final ThriftEnumHelper<
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingDataReplyType, 
                    com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType> replyTypeHelper = 
            ThriftEnumHelper.of(
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingDataReplyType.class, 
                    com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType.class);
    
    private static final ThriftEnumHelper<
                    com.cannontech.messaging.serialization.thrift.generated.ChannelDataStatus, 
                    com.cannontech.amr.rfn.message.read.ChannelDataStatus> statusHelper = 
            ThriftEnumHelper.of(
                    com.cannontech.messaging.serialization.thrift.generated.ChannelDataStatus.class, 
                    com.cannontech.amr.rfn.message.read.ChannelDataStatus.class);

    @Override
    public RfnMeterReadDataReply fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadDataReply();
        
        deserialize(msgBytes, entity);
        
        var msg = new RfnMeterReadDataReply();
        
        msg.setData(convert(entity.getData()));
        msg.setReplyType(replyTypeHelper.toJava(entity.getReplyType()));

        return msg;
    }

    private static RfnMeterReadingData convert(com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingData tdata) {
        var data = new RfnMeterReadingData();
        
        data.setChannelDataList(convertList(tdata.getChannelDataList(), RfnMeterReadDataReplySerializer::channelDataConverter));
        data.setDatedChannelDataList(convertList(tdata.getDatedChannelDataList(), RfnMeterReadDataReplySerializer::datedChannelDataConverter));
        data.setRecordInterval(tdata.getRecordInterval());
        data.setRfnIdentifier(convert(tdata.getRfnIdentifier()));
        data.setTimeStamp(tdata.getTimeStamp());
        
        return data;
    }

    private static DatedChannelData datedChannelDataConverter(com.cannontech.messaging.serialization.thrift.generated.DatedChannelData tdata) {
        var data = new DatedChannelData();

        if (tdata.isSetBaseChannelData()) {
            var tBaseData = tdata.getBaseChannelData();
            data.setBaseChannelData(channelDataConverter(tBaseData));
        }

        data.setTimeStamp(tdata.getTimeStamp());

        translateChannelData(tdata.getChannelData(), data);
        
        return data;
    }

    private static void translateChannelData(com.cannontech.messaging.serialization.thrift.generated.ChannelData tdata, ChannelData data) {
        data.setChannelNumber(tdata.getChannelNumber());
        data.setStatus(statusHelper.toJava(tdata.getStatus()));
        data.setUnitOfMeasure(translateUom(tdata.getUnitOfMeasure()));
        data.setUnitOfMeasureModifiers(tdata.getUnitOfMeasureModifiers());
        data.setValue(tdata.getValue());
    }

    private static String translateUom(String unitOfMeasure) {
        //  Remap the degree character, since it is outside of the ASCII range and doesn't map 
        //    between C++ and Java without explicit encoding 
        if (unitOfMeasure.equals("deg C")) {
            final char degree = 0xB0;
            return Character.toString(degree) + "C";
        }
        return unitOfMeasure;
    }

    private static ChannelData channelDataConverter(com.cannontech.messaging.serialization.thrift.generated.ChannelData tdata) {
        var data = new ChannelData();

        translateChannelData(tdata, data);
        
        return data;
    }
}
