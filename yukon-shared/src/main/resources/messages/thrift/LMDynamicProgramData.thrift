include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMDynamicProgramData {
    1: required     i32                             _paoId;
    2: required     bool                            _disableFlag;
    3: required     i32                             _currentGearNumber;
    4: required     i32                             _lastGroupControlled;
    5: required     i32                             _programState;
    6: required     double                          _reductionTotal;
    7: required     Types.Timestamp                 _directStartTime;
    8: required     Types.Timestamp                 _directStopTime;
    9: required     Types.Timestamp                 _notifyActiveTime;
   10: required     Types.Timestamp                 _notifyInactiveTime;
   11: required     Types.Timestamp                 _startedRampingOutTime;
   12: required     string                          _originSource;
}
