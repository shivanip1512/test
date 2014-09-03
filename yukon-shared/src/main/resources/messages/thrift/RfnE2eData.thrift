namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct RfnIdentifier {
    1: required string sensorManufacturer;
    2: required string sensorModel;
    3: required string sensorSerialNumber;
}

enum RfnE2eProtocol {
    APPLICATION = 0x00,
    NETWORK = 0x01,
    LINK = 0x02,
    //...
}

struct RfnE2eDataRequest {
    1: required     RfnE2eProtocol e2eProtocol;
    2: required     byte           applicationServiceId;
    3: required     RfnIdentifier  rfnIdentifier;
    4: required     byte           priority;
    5: optional     string         security;
    6: required     binary         payload;
}

struct RfnE2eDataIndication {
    1: required     RfnE2eProtocol e2eProtocol;
    2: required     byte           applicationServiceId;
    3: required     RfnIdentifier  rfnIdentifier;
    4: required     byte           priority;
    5: optional     string         security;
    6: required     binary         payload;
}

enum RfnE2eDataReplyType {
    OK = 0x00,
    DESTINATION_DEVICE_ADDRESS_UNKNOWN     = 0x01,
    DESTINATION_NETWORK_UNAVAILABLE        = 0x02,
    PMTU_LENGTH_EXCEEDED                   = 0x03,
    E2E_PROTOCOL_TYPE_NOT_SUPPORTED        = 0x04,
    NETWORK_SERVER_IDENTIFIER_INVALID      = 0x05,
    APPLICATION_SERVICE_IDENTIFIER_INVALID = 0x06,
    NETWORK_LOAD_CONTROL                   = 0x07,
    NETWORK_SERVICE_FAILURE                = 0x08,
    //...
}

struct RfnE2eDataConfirm {
    1: required     RfnE2eProtocol        e2eProtocol;
    2: required     byte                  applicationServiceId;
    3: required     RfnIdentifier         rfnIdentifier;
    4: required     RfnE2eDataReplyType   replyType;
}
