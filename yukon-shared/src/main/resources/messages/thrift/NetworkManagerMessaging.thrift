namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum NetworkManagerMessageLifetime {
    SESSION = 0,
    UNTIL_CANCEL,
    //...
}

struct NetworkManagerRequestHeader {
    1: required     string                          clientGuid;
    2: required     i64                             sessionId;
    3: required     i64                             messageId;
    4: optional     i64                             groupId;
    5: required     byte                            priority;
    6: optional     i64                             expiration; // timestamp in milliseconds since epoch 1970-01-01 UTC
    7: optional     NetworkManagerMessageLifetime   lifetime = NetworkManagerMessageLifetime.SESSION;
}

struct NetworkManagerRequestAck {
    1: required     NetworkManagerRequestHeader header;
}

enum NetworkManagerCancelType {
    MESSAGE_IDS = 0,
    GROUP_IDS,
    //...
}

struct NetworkManagerCancelRequest {
    1: required     string                      clientGuid;
    2: required     i64                         sessionId;
    3: required     NetworkManagerCancelType    type; 
    4: required     set<i64>                    ids;
}

struct NetworkManagerCancelRequestAck {
    1: required     NetworkManagerCancelRequest request;
}

enum NetworkManagerMessageCancelStatus {
    SUCCESS = 0,
    NOT_FOUND,
    //...
}

typedef map<i64, NetworkManagerMessageCancelStatus> MessageStatusPerId

struct NetworkManagerCancelResponse {
    1: required     string                clientGuid;
    2: required     i64                   sessionId;
    3: required     MessageStatusPerId    messageIds;
}
