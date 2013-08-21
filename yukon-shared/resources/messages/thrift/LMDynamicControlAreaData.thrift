include "LMDynamicTriggerData.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMDynamicControlAreaData {
    1: required     i32                             _paoId;
    2: required     i32                             _disableFlag;
    3: required     Types.Timestamp                 _nextCheckTime;       
    4: required     i32                             _controlAreaState;
    5: required     i32                             _currentPriority;
    6: required     i32                             _currentDailyStartTime;
    7: required     i32                             _currentDailyStopTime;
    8: required     list<LMDynamicTriggerData.LMDynamicTriggerData> _triggers;
}
