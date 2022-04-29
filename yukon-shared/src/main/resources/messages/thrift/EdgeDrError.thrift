namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum EdgeDrErrorType {
    TIMEOUT = 0x01,
    //...
}

struct EdgeDrError {
    1: required EdgeDrErrorType errorType;
    2: required string errorMessage;
}