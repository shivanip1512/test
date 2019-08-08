include "LMMessage.thrift"
include "Types.thrift"
include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMControlAreaTrigger {
    1: required     i32                             _paoId;
    2: required     i32                             _triggerNumber;
    3: required     string                          _triggerType;
    4: required     i32                             _pointId;
    5: required     double                          _pointValue;
    6: required     Types.Timestamp                 _lastPointValueTimestamp;
    7: required     i32                             _normalState;
    8: required     double                          _threshold;
    9: required     string                          _projectionType;
   10: required     i32                             _projectionPoints;
   11: required     i32                             _projectAheadDuration;
   12: required     i32                             _thresholdKickPercent;
   13: required     double                          _minRestoreOffset;
   14: required     i32                             _peakPointId;
   15: required     double                          _peakPointValue;
   16: required     Types.Timestamp                 _lastPeakPointValueTimestamp;
   17: required     double                          _projectedPointValue;
}

struct LMProgramControlWindow {
    1: required     i32                             _paoId;
    2: required     i32                             _windowNumber
    3: required     i32                             _availableStartTime;
    4: required     i32                             _availableStopTime;
}

struct LMProgramBase {
    1: required     i32                             _paoId;
    2: required     string                          _paoCategory;
    3: required     string                          _paoClass;
    4: required     string                          _paoName;
    5: required     string                          _paoTypeString;
    6: required     string                          _paoDescription;
    7: required     bool                            _disableFlag;
    8: required     i32                             _startPriority;
    9: required     i32                             _stopPriority;
   10: required     string                          _controlType;
   11: required     string                          _availableWeekdays;
   12: required     i32                             _maxHoursDaily;
   13: required     i32                             _maxHoursMonthly;
   14: required     i32                             _maxHoursSeasonal;
   15: required     i32                             _maxHoursAnnually;
   16: required     i32                             _minActivateTime;
   17: required     i32                             _minRestartTime;
   18: required     i32                             _programStatusPointId;
   19: required     i32                             _programState;
   20: required     i32                             _reductionAnalogPointId;
   21: required     double                          _reductionTotal;
   22: required     Types.Timestamp                 _startedControlling;
   23: required     Types.Timestamp                 _lastControlSent;
   24: required     bool                            _manualControlReceivedFlag;
   25: required     list<LMProgramControlWindow>    _lmProgramControlWindows;
}

struct LMCICustomerBase
{
    1: required     i32                             _customerId;
    2: required     string                          _companyName;
    3: required     double                          _customerDemandLevel;
    4: required     double                          _curtailAmount;
    5: required     string                          _curtailmentAgreement;
    6: required     string                          _timeZone;
    7: required     i32                             _customerOrder;
}
    
struct LMCurtailCustomer
{
    1: required     LMCICustomerBase                _baseMessage;
    2: required     bool                            _requireAck;
    3: required     i32                             _curtailReferenceId;
    4: required     string                          _acknowledgeStatus;
    5: required     Types.Timestamp                 _ackDatetime;
    6: required     string                          _ipAddressOfAckUser;
    7: required     string                          _userIdName;
    8: required     string                          _nameOfAckPerson;
    9: required     string                          _curtailmentNotes;
   10: required     bool                            _ackLateFlag;
}

struct LMProgramCurtailment
{
    1: required     LMProgramBase                   _baseMessage
    2: required     i32                             _minNotifyTime;
    3: required     string                          _heading;
    4: required     string                          _messageHeader;
    5: required     string                          _messageFooter;
    6: required     i32                             _acktimeLimit;
    7: required     string                          _canceledMsg;
    8: required     string                          _stoppedEarlyMsg;
    9: required     i32                             _curtailReferenceId;
   10: required     Types.Timestamp                 _actionDateTime;
   11: required     Types.Timestamp                 _notificationDateTime;
   12: required     Types.Timestamp                 _curtailmentStartTime;
   13: required     Types.Timestamp                 _curtailmentStopTime;
   14: required     string                          _runStatus;
   15: required     string                          _additionalInfo;
   16: required     list<LMCurtailCustomer>         _lmProgramCurtailmentCustomers;
}

struct LMProgramDirectGear
{
    1: required     i32                             _programPaoId;
    2: required     string                          _gearName;
    3: required     i32                             _gearNumber;
    4: required     string                          _controlMethod;
    5: required     i32                             _methodRate;
    6: required     i32                             _methodPeriod;
    7: required     i32                             _methodRateCount;
    8: required     i32                             _cyclereFreshRate;
    9: required     string                          _methodStopType;
   10: required     string                          _changeCondition;
   11: required     i32                             _changeDuration;
   12: required     i32                             _changePriority;
   13: required     i32                             _changeTriggerNumber;
   15: required     double                          _changeTriggerOffset;
   16: required     i32                             _percentReduction;
   17: required     string                          _groupSelectionMethod;
   18: required     string                          _methodOptionType;
   19: required     i32                             _methodOptionMax;
   20: required     i32                             _rampInInterval;
   21: required     i32                             _rampInPercent;
   22: required     i32                             _rampOutInterval;
   23: required     i32                             _rampOutPercent;
   24: required     double                          _kwReduction;
}

struct LMGroupBase
{
    1: required     i32                             _paoId;
    2: required     string                          _paoCategory;
    3: required     string                          _paoClass;
    4: required     string                          _paoName;
    5: required     string                          _paoTypeString;
    6: required     string                          _paoDescription;
    7: required     bool                            _disableFlag;
    8: required     i32                             _groupOrder;
    9: required     double                          _kwCapacity;
   10: required     i32                             _childOrder;
   11: required     bool                            _alarmInhibit;
   12: required     bool                            _controlInhibit;
   13: required     i32                             _groupControlState;
   14: required     i32                             _currentHoursDaily;
   15: required     i32                             _currentHoursMonthly;
   16: required     i32                             _currentHoursSeasonal;
   17: required     i32                             _currentHoursAnnually;
   18: required     Types.Timestamp                 _lastControlSent;
   19: required     Types.Timestamp                 _controlStartTime;
   21: required     Types.Timestamp                 _controlCompleteTime;
   23: required     Types.Timestamp                 _nextControlTime;
   24: required     i32                             _internalState;
   25: required     i32                             _dailyOps;
   26: required     Types.Timestamp                 _lastStopTimeSent;
}

struct LMGroupDigiSEP
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupEcobee
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupHoneywell
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupNest
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupItron
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupMeterDisconnect
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupEmetcon
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupExpresscom
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupGolay
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupMacro
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupMCT
{
    1: required     LMGroupEmetcon                  _baseMessage;
}

struct LMGroupPoint
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupRipple
{
    1: required     LMGroupBase                     _baseMessage;
    2: required     i32                             _shedTime;
}

struct LMGroupSA105
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupSA205
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupSA305
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupSADigital
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMGroupVersacom
{
    1: required     LMGroupBase                     _baseMessage;
}

struct LMProgramDirect
{
    1: required     LMProgramBase                   _baseMessage;
    2: required     i32                             _currentGearNumber;
    3: required     i32                             _lastGroupControlled;
    4: required     Types.Timestamp                 _directStartTime;
    5: required     Types.Timestamp                 _directstopTime;
    6: required     Types.Timestamp                 _notifyActiveTime;
    7: required     Types.Timestamp                 _notifyInactiveTime;
    8: required     Types.Timestamp                 _startedRampingOut;
    9: required     i32                             _triggerOffset;
   10: required     i32                             _triggerRestoreOffset;
   11: required     bool                            _constraintOverride;
   12: required     list<LMProgramDirectGear>       _lmProgramDirectGears;
   13: required     list<Message.GenericMessage>    _lmProgramDirectGroups; // is actually a list of LMGroupBase
   14: required     list<Message.GenericMessage>    _activeMasters;         // is actually a list of LMProgramDirect
   15: required     list<Message.GenericMessage>    _activeSubordinates;    // is actually a list of LMProgramDirect
   16: required     string                          _originSource;
}

struct LMEnergyExchangeHourlyOffer
{
    1: required     i32                             _offerId;
    2: required     i32                             _revisionNumber;
    3: required     i32                             _hour;
    4: required     double                          _price;
    5: required     double                          _amountRequested;
}

struct LMEnergyExchangeOfferRevision
{
    1: required     i32                             _offerId;
    2: required     i32                             _revisionNumber;
    3: required     Types.Timestamp                 _actionDatetime;
    4: required     Types.Timestamp                 _notificationDatetime;
    5: required     Types.Timestamp                 _offerexpirationDatetime;
    6: required     string                          _additionalInfo;
    7: required     list<LMEnergyExchangeHourlyOffer> _lmEnergyExchangeHourlyOffers;
}

struct LMEnergyExchangeOffer
{
    1: required     i32                             _paoId;
    2: required     i32                             _offerId;
    3: required     string                          _runStatus;
    4: required     Types.Timestamp                 _offerDate;
    5: required     list<LMEnergyExchangeOfferRevision> _lmEnergyExchangeOfferRevisions;
}

struct LMEnergyExchangeHourlyCustomer
{
    1: required     i32                             _customerId;
    2: required     i32                             _offerId;
    3: required     i32                             _revisionNumber;
    4: required     i32                             _hour;
    5: required     double                          _amountCommitted;
}

struct LMEnergyExchangeCustomerReply
{
    1: required     i32                             _customerId;
    2: required     i32                             _offerId;
    3: required     string                          _acceptStatus;
    4: required     Types.Timestamp                 _acceptDatetime;
    5: required     i32                             _revisionNumber;
    6: required     string                          _ipAddressOfAcceptUser;
    7: required     string                          _userIdName;
    8: required     string                          _nameOfAcceptPerson;
    9: required     string                          _energyExchangeNotes;
   10: required     list<LMEnergyExchangeHourlyCustomer> _lmEnergyExchangeHourlyCustomers;
}

struct LMEnergyExchangeCustomer
{
    1: required     LMCICustomerBase                _baseMessage;
    2: required     list<LMEnergyExchangeCustomerReply> _lmEnergyExchangeCustomerReplies;
}

struct LMProgramEnergyExchange
{
    1: required     LMProgramBase                   _baseMessage;
    2: required     i32                             _minNotifyTime;
    3: required     string                          _heading;
    4: required     string                          _messageHeader;
    5: required     string                          _messageFooter;
    6: required     string                          _canceledMsg;
    7: required     string                          _stoppedEarlyMsg;
    8: required     list<LMEnergyExchangeOffer>     _lmEnergyExchangeOffers;
    9: required     list<LMEnergyExchangeCustomer>  _lmEnergyExchangeCustomers;
}

struct LMControlAreaItem {
    1: required     i32                             _paoId;
    2: required     string                          _paoCategory;
    3: required     string                          _paoClass;
    4: required     string                          _paoName;
    5: required     string                          _paoTypeString;
    6: required     string                          _paoDescription;
    7: required     bool                            _disableFlag;
    8: required     string                          _defOperationalState;
    9: required     i32                             _controlInterval;
   10: required     i32                             _minResponseTime;
   11: required     i32                             _defDailyStartTime;
   12: required     i32                             _defDailyStopTime;
   13: required     bool                            _requireAllTriggersActiveFlag;
   14: required     Types.Timestamp                 _nextCheckTime;
   15: required     bool                            _newPointDataReceivedFlag;
   16: required     bool                            _updatedFlag;
   17: required     i32                             _controlAreaStatusPointId;
   18: required     i32                             _controlAreaState;
   19: required     i32                             _currentPriority;
   20: required     i32                             _currentDailyStartTime;
   21: required     i32                             _currentDailyStopTime;
   22: required     list<LMControlAreaTrigger>      _lmControlAreaTriggers;
   23: required     list<Message.GenericMessage>    _lmPrograms; // is actually a list of LMProgramBase
}

struct LMControlAreas {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _msgInfoBitMask;
    3: required     list<LMControlAreaItem>         _controlAreas;
}
