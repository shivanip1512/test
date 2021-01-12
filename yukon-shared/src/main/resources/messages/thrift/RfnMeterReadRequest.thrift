include "NetworkManagerMessaging.thrift"
include "RfnAddressing.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct RfnMeterReadRequest {
    1: required     RfnAddressing.RfnIdentifier  rfnIdentifier;
}

enum RfnMeterReadingReplyType {
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT // Yukon specific timeout
}

struct RfnMeterReadReply {
    1: required     RfnMeterReadingReplyType replyType;
}

enum RfnMeterReadingDataReplyType {
    OK,
    FAILURE,
    NETWORK_TIMEOUT,
    TIMEOUT // Yukon specific timeout
}

enum ChannelDataStatus {
    OK,
    TIMEOUT,
    FAILURE,
    LONG
}

struct ChannelData {
    1: required     i32                   channelNumber;
    2: required     ChannelDataStatus     status;
    3: required     string                unitOfMeasure;
    4: required     set<string>           unitOfMeasureModifiers;
    5: required     double                value;
}

struct DatedChannelData {
    1: required     ChannelData           channelData;
    2: required     Types.Timestamp       timeStamp;
    3: required     ChannelData           baseChannelData;
}

struct RfnMeterReadingData {
    1: required     list<ChannelData>     channelDataList;
    2: required     list<DatedChannelData> datedChannelDataList;
    3: required     RfnAddressing.RfnIdentifier rfnIdentifier;
    4: required     Types.Timestamp       timeStamp;
    5: required     i32                   recordInterval;
}

struct RfnMeterReadDataReply {
    1: required     RfnMeterReadingDataReplyType replyType;
    2: required     RfnMeterReadingData   data;
}