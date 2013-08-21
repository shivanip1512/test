include "LMMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMCurtailmentAcknowledge {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _paoId;
    3: required     i32                             _curtailReferenceId;
    4: required     string                          _acknowledgeStatus;
    5: required     string                          _ipAddressOfAckUser;
    6: required     string                          _userIdName;
    7: required     string                          _nameOfAckPerson;
    8: required     string                          _curtailmentNotes;
}
