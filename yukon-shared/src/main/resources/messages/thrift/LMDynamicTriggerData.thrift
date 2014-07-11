include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMDynamicTriggerData {
    1: required     i32                             _paoId;
    2: required     i32                             _triggerNumber;
    3: required     double                          _pointValue;
    4: required     Types.Timestamp                 _lastPointValueTimestamp;
    5: required     i32                             _normalState;
    6: required     double                          _threshold;
    7: required     double                          _peakPointValue;
    8: required     Types.Timestamp                 _lastPeakPointValueTimestamp;
    9: required     double                          _projectedPointValue;
}
