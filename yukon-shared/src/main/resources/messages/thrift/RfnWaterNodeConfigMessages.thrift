include "Types.thrift"
include "RfnE2eData.thrift"
include "NetworkManagerMessaging.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated


struct RfnSetChannelConfigRequest {
    1: required     RfnE2eData.RfnIdentifier    rfnIdentifier;
    2: required     i32                         reportingInterval; 
    3: required     i32                         recordingInterval;
    4: optional     NetworkManagerMessaging.NetworkManagerRequestHeader header;
}

enum SetChannelConfigReplyType {
    SUCCESS = 0,
    INVALID_DEVICE,    
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    //...
}

struct RfnSetChannelConfigReply {
    1: required     SetChannelConfigReplyType   reply;
    2: required     RfnE2eData.RfnIdentifier    rfnIdentifier;
}

struct RfnGetChannelConfigRequest {
    1: required     RfnE2eData.RfnIdentifier    rfnIdentifier;
}

struct ChannelInfo {
    1: required     string      UOM;
    2: required     set<string> uomModifier;
    3: required     i16         channelNum;
    4: required     bool        enabled;
}

enum GetChannelConfigReplyType {
    SUCCESS = 0,
    INVALID_DEVICE,    
    NO_NODE,
    FAILURE,
   //...
}

struct RfnGetChannelConfigReply {
    1: required     Types.Timestamp             timestamp;
    2: required     set<ChannelInfo>            channelInfo;
    3: required     RfnE2eData.RfnIdentifier    rfnIdentifier;
    4: required     i32                         recordingInterval;
    5: required     i32                         reportingInterval;
    6: required     GetChannelConfigReplyType   reply;
}

