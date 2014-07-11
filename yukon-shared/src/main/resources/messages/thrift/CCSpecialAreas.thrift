include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCSpecial {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     list<i32>                       _substationIds;
    3: required     bool                            _ovUvDisabledFlag;
    4: required     double                          _pfDisplayValue;
    5: required     double                          _estPfDisplayValue;
    6: required     bool                            _voltReductionControlValue;
}

struct CCSpecialAreas {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     list<CCSpecial>                 _ccSpecialAreas;
}
