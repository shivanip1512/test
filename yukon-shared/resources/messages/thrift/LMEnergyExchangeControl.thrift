include "LMMessage.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMEnergyExchangeControl {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _command;
    3: required     i32                             _paoId;
    4: required     i32                             _offerId;
    5: required     Types.Timestamp                 _offerDate;
    6: required     Types.Timestamp                 _notificationDatetime;
    7: required     Types.Timestamp                 _expirationDatetime;
    8: required     string                          _additionalInfo;
    9: required     list<double>                    _amountsRequested;
   10: required     list<i32>                       _pricesOffered;
}
