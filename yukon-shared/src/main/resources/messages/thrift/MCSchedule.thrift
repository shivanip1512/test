include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct MCSchedule {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _scheduleId;
    3: required     string                          _scheduleName;
    4: required     string                          _categoryName;
    5: required     string                          _scheduleType;
    6: required     i32                             _holidayScheduleId;
    7: required     string                          _commandFile;
    8: required     string                          _currentState;
    9: required     string                          _startPolicy;
   10: required     string                          _stopPolicy;
   11: required     Types.Timestamp                 _lastRunTime;
   12: required     string                          _lastRunStatus;
   13: required     i32                             _startDay;
   14: required     i32                             _startMonth;
   15: required     i32                             _startYear;
   16: required     string                          _startTime;
   17: required     string                          _stopTime;
   18: required     string                          _validWeekDays;
   19: required     i32                             _duration;
   20: required     Types.Timestamp                 _manualStartTime;
   21: required     Types.Timestamp                 _manualStopTime;
   22: required     i32                             _targetPaoId;
   23: required     string                          _startCommand;
   24: required     string                          _stopCommand;
   25: required     i32                             _repeatInterval;
   26: required     Types.Timestamp                 _currentStartTime;
   27: required     Types.Timestamp                 _currentStopTime;
   28: required     i32                             _templateType;
}
