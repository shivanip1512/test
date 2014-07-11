include "LMMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMEnergyExchangeAccept {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _paoId;
    3: required     i32                             _offerId;
    4: required     i32                             _revisionNumber;
    5: required     string                          _acceptStatus;
    6: required     string                          _ipAddressOfAcceptUser;
    7: required     string                          _userIdName;    
    8: required     string                          _nameOfAcceptPerson;        
    9: required     string                          _energyExchangeNotes;           
   10: required     list<double>                    _amountsCommitted;
}
