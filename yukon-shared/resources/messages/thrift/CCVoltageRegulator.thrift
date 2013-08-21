include "CCMessage.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCVoltageRegulatorItem {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     i32                             _parentId;
    3: required     i32                             _lastTapOperation;
    4: required     Types.Timestamp                 _lastTapOperationTime;
    5: required     i32                             _regulatorType;
    6: required     bool                            _recentTapOperation;
    7: required     i32                             _lastOperatingMode;
    8: required     i32                             _lastCommandedOperatingMode;
}

struct CCVoltageRegulator {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     list<CCVoltageRegulatorItem>    _regulators;
}
