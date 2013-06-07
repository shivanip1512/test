include "CCMessage.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCCapBank {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     i32                             _parentId;
    3: required     i32                             _maxDailyOps;
    4: required     bool                            _maxOpsDisableFlag;
    5: required     bool                            _alarmInhibitFlag;
    6: required     bool                            _controlInhibitFlag;
    7: required     string                          _operationalState;
    8: required     string                          _controllerType;
    9: required     i32                             _controlDeviceId;
   10: required     i32                             _bankSize;
   11: required     string                          _typeOfSwitch;
   12: required     string                          _switchManufacture;
   13: required     string                          _mapLocationId;
   14: required     i32                             _recloseDelay;
   15: required     double                          _controlOrder;
   16: required     i32                             _statusPointId;
   17: required     i32                             _controlStatus;
   18: required     i32                             _operationAnalogPointId;
   19: required     i32                             _totalOperations;
   20: required     Types.Timestamp                 _lastStatusChangeTime;
   21: required     i32                             _tagsControlStatus;
   22: required     i32                             _originalParentId;
   23: required     i32                             _currentDailyOperations;
   24: required     bool                            _ignoreFlag;
   25: required     i32                             _ignoreReason;
   26: required     bool                            _ovUvDisabledFlag;
   27: required     double                          _tripOrder;
   28: required     double                          _closeOrder;
   29: required     string                          _controlDeviceType;
   30: required     string                          _sBeforeVars;
   31: required     string                          _sAfterVars;
   32: required     string                          _sPercentChange;
   33: required     bool                            _maxDailyOpsHitFlag;
   34: required     bool                            _ovUvSituationFlag;
   35: required     i32                             _controlStatusQuality;
   36: required     bool                            _localControlFlag;
   37: required     string                          _partialPhaseInfo;
}

struct CCFeeder {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     i32                             _parentId;
    3: required     i32                             _strategy_maxDailyOperation;
    4: required     bool                            _strategy_maxOperationDisableFlag;
    5: required     i32                             _currentVarLoadPointId;
    6: required     double                          _currentVarLoadPointValue;
    7: required     i32                             _currentWattLoadPointId;
    8: required     double                          _currentWattLoadPointValue;
    9: required     string                          _mapLocationId;
   10: required     double                          _displayOrder;
   11: required     bool                            _newPointDataReceivedFlag;
   12: required     Types.Timestamp                 _lastCurrentVarPointUpdateTime;
   13: required     i32                             _estimatedVarLoadPointId;
   14: required     double                          _estimatedVarLoadPointValue;
   15: required     i32                             _dailyOperationsAnalogPointId;
   16: required     i32                             _powerFactorPointId;
   17: required     i32                             _estimatedPowerFactorPointId;
   18: required     i32                             _currentDailyOperations;
   19: required     bool                            _recentlyControlledFlag_or_performingVerificationFlag;
   20: required     i32                             _lastOperationTime;
   21: required     double                          _varValueBeforeControl;
   22: required     double                          _powerFactorValue;
   23: required     double                          _estimatedPowerFactorValue;
   24: required     i32                             _currentVarPointQuality;
   25: required     bool                            _waiveControlFlag;
   26: required     string                          _strategy_controlUnits;
   27: required     i32                             _decimalPlaces;
   28: required     bool                            _peakTimeFlag;
   29: required     double                          _strategy_peakLag;
   30: required     double                          _strategy_offPeakLag;
   31: required     double                          _strategy_PeakLead;
   32: required     double                          _strategy_OffPeakLead;
   33: required     i32                             _currentVoltLoadPointId;
   34: required     double                          _currentVoltLoadPointValue;
   35: required     i32                             _currentWattPointQuality;
   36: required     i32                             _currentVoltPointQuality;
   37: required     double                          _targetVarValue;
   38: required     string                          _solution;
   39: required     bool                            _ovUvDisabledFlag;
   40: required     double                          _strategy_peakPFSetPoint;
   41: required     double                          _strategy_offPeakPFSetPoint;
   42: required     string                          _strategy_controlMethod;
   43: required     double                          _phaseAValue;
   44: required     double                          _phaseBValue;
   45: required     double                          _phaseCValue;
   46: required     bool                            _likeDayControlFlag;
   47: required     bool                            _usePhaseData;
   48: required     i32                             _originalParentId;
   49: required     list<CCCapBank>                 _ccCapbanks;
}

struct CCSubstationBusItem {
    1: required     CCMessage.CCPao                 _baseMessage;
    2: required     i32                             _parentId;
    3: required     i32                             _strategy_maxDailyOperation;
    4: required     bool                            _strategy_maxOperationDisableFlag;
    5: required     i32                             _currentVarLoadPointId;
    6: required     double                          _varValue;
    7: required     i32                             _currentWattLoadPointId;
    8: required     double                          _wattValue;
    9: required     string                          _mapLocationId;
   10: required     string                          _strategy_controlUnits;
   11: required     i32                             _decimalPlaces;
   12: required     bool                            _newPointDataReceivedFlag;
   13: required     bool                            _busUpdatedflag;
   14: required     i32                             _lastCurrentVarPointUpdateTime;
   15: required     i32                             _estimatedVarLoadPointId;
   16: required     i32                             _estimatedVarLoadPointValue;
   17: required     i32                             _dailyOperationsAnalogPointId;
   18: required     i32                             _powerFactorPointId;
   19: required     i32                             _estimatedPowerFactorPointId;
   20: required     i32                             _currentDailyOperations;
   21: required     bool                            _peakTimeFlag;
   22: required     bool                            _recentlyControlledFlag;
   23: required     i32                             _lastOperationTime;
   24: required     double                          _varValueBeforeControl;
   25: required     double                          _powerFactorValue;
   26: required     double                          _estimatedPowerFactorValue;
   27: required     i32                             _currentVarPointQuality;
   28: required     bool                            _waiveControlFlag;
   29: required     double                          _strategy_peakLag;
   30: required     double                          _strategy_offPeakLag;
   31: required     double                          _strategy_peakLead;
   32: required     double                          _strategy_offPeakLead;
   33: required     i32                             _currentVoltLoadPointId;
   34: required     double                          _voltValue;
   35: required     bool                            _verificationFlag;
   36: required     bool                            _switchOverStatus;
   37: required     i32                             _currentWattPointQuality;
   38: required     i32                             _currentVoltPointQuality;
   39: required     double                          _targetVarValue;
   40: required     string                          _solution;
   41: required     bool                            _ovUvDisabledFlag;
   42: required     double                          _strategy_peakPFSetPoint;
   43: required     double                          _strategy_offPeakPFSetPoint;
   44: required     string                          _strategy_controlMethod;
   45: required     double                          _phaseAValue;
   46: required     double                          _phaseBValue;
   47: required     double                          _phaseCValue;
   48: required     bool                            _likeDayControlFlag;
   49: required     i32                             _displayOrder;
   50: required     bool                            _voltReductionFlag;
   51: required     bool                            _usePhaseData;
   52: required     bool                            _primaryBusFlag;
   53: required     i32                             _altSubId;
   54: required     bool                            _dualBusEnabled;
   55: required     i32                             _strategyId;
   56: required     list<CCFeeder>                  _ccFeeders;
}

struct CCSubstationBus {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     i32                             _msgInfoBitMask;
    3: required     list<CCSubstationBusItem>       _ccSubstationBuses;
}
