include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMDynamicGroupData {
    1: required     i32                             _paoId;
    2: required     bool                            _disableFlag;
    3: required     i32                             _groupControlState;
    4: required     i32                             _currentHoursDaily;
    5: required     i32                             _currentHoursMonthly;
    6: required     i32                             _currentHoursSeasonal; 
    7: required     i32                             _currentHoursAnnually; 
    8: required     Types.Timestamp                 _lastControlSent;
    9: required     Types.Timestamp                 _controlStartTime;
   10: required     Types.Timestamp                 _controlCompleteTime;
   11: required     Types.Timestamp                 _nextControlTime;
   12: required     i32                             _internalState;
   13: required     i32                             _dailyOps;        
}
