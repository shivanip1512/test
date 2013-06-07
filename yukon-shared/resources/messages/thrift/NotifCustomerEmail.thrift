include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifCustomerEmail {
    1: required     Message.Message                 _baseMessage;
    2: required     string                          _to;
    3: required     i32                             _customerId;
    4: required     string                          _subject;
    5: required     string                          _body;
    6: required     string                          _toCc;
    7: required     string                          _toBcc;
}
