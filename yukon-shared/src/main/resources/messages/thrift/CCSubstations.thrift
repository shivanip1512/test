include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCSubstationItem {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     i32                             _parentId;
    3: required     bool                            _ovUvDisabledFlag;
    5: required     list<i32>                       _subBusIds;
    6: required     double                          _pfDisplayValue;
    7: required     double                          _estPfDisplayValue;
    8: required     bool                            _saEnabledFlag;
    9: required     i32                             _saEnabledId;
   10: required     bool                            _voltReductionFlag;
   11: required     bool                            _recentlyControlledFlag;
   12: required     bool                            _childVoltReductionFlag;
}

struct CCSubstations {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     i32                             _msgInfoBitMask;
    3: required     list<CCSubstationItem>          _ccSubstations;
}
