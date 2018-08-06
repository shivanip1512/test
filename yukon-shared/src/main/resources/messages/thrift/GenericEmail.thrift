include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct GenericEmail {
    1: required     Message.Message                 _baseMessage;
    2: optional     string                          _to;
    3: required     string                          _from;
    4: required     string                          _subject;
    5: required     string                          _body;
    6: optional     string                          _bcc;
}
