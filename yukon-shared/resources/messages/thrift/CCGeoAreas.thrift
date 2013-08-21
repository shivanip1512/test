include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCArea {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     bool                            _ovUvDisabledFlag;
    3: required     list<i32>                       _substationIds;
    4: required     double                          _pfDisplayValue;
    5: required     double                          _estPfDisplayValue;
    6: required     bool                            _voltReductionControlValue;
    7: required     bool                            _childVoltReductionFlag;
}

struct CCGeoAreas {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     i32                             _msgInfoBitMask; 
    3: required     list<CCArea>                    _ccGeoAreas;
}
