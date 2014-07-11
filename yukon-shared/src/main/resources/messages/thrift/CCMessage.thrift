include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCMessage {
    1: required     Message.Message                 _baseMessage;
}

struct CCPao {
    1: required     i32                             _paoId;                  
    2: required     string                          _paoCategory;
    3: required     string                          _paoClass;
    4: required     string                          _paoName;
    5: required     string                          _paoType;
    6: required     string                          _paoDescription;
    7: required     bool                            _disableFlag;
}
