/*---------------------------------------------------------------------------
        Filename:  cctwowaycbcpoints.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCTwoWayPoints.
                        CtiCCTwoWayPoints maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "cctwowaycbcpoints.h"
#include "cccapbank.h"
#include "ccid.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "msg_ptreg.h"
#include "database_writer.h"

extern ULONG _CC_DEBUG;

//RWDEFINE_COLLECTABLE( CtiCCTwoWayPoints, CtiCCTwoWayPoints_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints(LONG paoid)
{
    _paoid = paoid;
    _capacitorBankStateId = 0;
    _capacitorBankState = 0;
    _reCloseBlockedId = 0;
    _reCloseBlocked = 0;
    _controlModeId = 0;
    _controlMode = 0;
    _autoVoltControlId = 0;
    _autoVoltControl = 0;
    _lastControlLocalId = 0;
    _lastControlLocal = 0;
    _lastControlRemoteId = 0;
    _lastControlRemote = 0;
    _lastControlOvUvId = 0;
    _lastControlOvUv = 0;
    _lastControlNeutralFaultId = 0;
    _lastControlNeutralFault = 0;
    _lastControlScheduledId = 0;
    _lastControlScheduled = 0;
    _lastControlDigitalId = 0;
    _lastControlDigital = 0;
    _lastControlAnalogId = 0;
    _lastControlAnalog = 0;
    _lastControlTemperatureId = 0;
    _lastControlTemperature = 0;
    _ovConditionId = 0;
    _ovCondition = 0;
    _uvConditionId = 0;
    _uvCondition = 0;
    _opFailedNeutralCurrentId = 0;
    _opFailedNeutralCurrent = 0;
    _neutralCurrentFaultId = 0;
    _neutralCurrentFault = 0;
    _badRelayId = 0;
    _badRelay = 0;
    _dailyMaxOpsId = 0;
    _dailyMaxOps = 0;
    _voltageDeltaAbnormalId = 0;
    _voltageDeltaAbnormal = 0;
    _tempAlarmId = 0;
    _tempAlarm = 0;
    _DSTActiveId = 0;
    _DSTActive = 0;
    _neutralLockoutId = 0;
    _neutralLockout = 0;
    _ignoredIndicatorId = 0;
    _ignoredIndicator = 0;

    //analog inputs
    _voltageControlId = 0;
    _voltageControl = 0;
    _voltageId = 0;
    _voltage = 0;
    _highVoltageId = 0;
    _highVoltage = 0;
    _lowVoltageId = 0;
    _lowVoltage = 0;
    _deltaVoltageId = 0;
    _deltaVoltage = 0;
    _analogInput1Id = 0;
    _analogInput1 = 0;
    _rssiId = 0;
    _rssi = 0;
    _ignoredReasonId = 0;
    _ignoredReason = 0;
    _temperatureId = 0;
    _temperature = 0;

    //analog outputs
    _ovSetPointId = 0;
    _ovSetPoint = 0;
    _uvSetPointId = 0;
    _uvSetPoint = 0;
    _ovuvTrackTimeId = 0;
    _ovuvTrackTime = 0;
    _neutralCurrentSensorId = 0;
    _neutralCurrentSensor = 0;
    _neutralCurrentAlarmSetPointId = 0;
    _neutralCurrentAlarmSetPoint = 0;
    _timeTempSeasonOneId = 0;
    _timeTempSeasonOne = 0;
    _timeTempSeasonTwoId = 0;
    _timeTempSeasonTwo = 0;
    _varControlId = 0;
    _varControl = 0;
    _udpIpAddressId = 0;
    _udpIpAddress = 0;
    _udpPortNumberId = 0;
    _udpPortNumber = 0;

    _totalOpCountId = 0;
    _totalOpCount = 0;
    _ovCountId = 0;
    _ovCount = 0;
    _uvCountId = 0;
    _uvCount = 0;

    _ovuvCountResetDate = gInvalidCtiTime;
    _lastOvUvDateTime = gInvalidCtiTime;

    _lastControlReason = 0;

    _insertDynamicDataFlag = TRUE;
    _dirty = TRUE;

    return;
}

CtiCCTwoWayPoints::CtiCCTwoWayPoints(Cti::RowReader& rdr)
{
    //restore(rdr);
}

CtiCCTwoWayPoints::CtiCCTwoWayPoints(const CtiCCTwoWayPoints& twoWayPt)
{
    operator=(twoWayPt);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::~CtiCCTwoWayPoints()
{

}

LONG CtiCCTwoWayPoints::getPAOId() const
{
    return _paoid;
}
LONG CtiCCTwoWayPoints::getCapacitorBankStateId() const
{
    return _capacitorBankStateId;
}
LONG CtiCCTwoWayPoints::getCapacitorBankState() const
{
    return _capacitorBankState;
}
LONG CtiCCTwoWayPoints::getReCloseBlockedId() const
{
    return _reCloseBlockedId;
}
LONG CtiCCTwoWayPoints::getReCloseBlocked() const
{
    return _reCloseBlocked;
}
LONG CtiCCTwoWayPoints::getControlModeId() const
{
    return _controlModeId;
}
LONG CtiCCTwoWayPoints::getControlMode() const
{
    return _controlMode;
}
LONG CtiCCTwoWayPoints::getAutoVoltControlId() const
{
    return _autoVoltControlId;
}
LONG CtiCCTwoWayPoints::getAutoVoltControl() const
{
    return _autoVoltControl;
}
LONG CtiCCTwoWayPoints::getLastControlLocalId() const
{
    return _lastControlLocalId;
}
LONG CtiCCTwoWayPoints::getLastControlLocal() const
{
    return _lastControlLocal;
}
LONG CtiCCTwoWayPoints::getLastControlRemoteId() const
{
    return _lastControlRemoteId;
}
LONG CtiCCTwoWayPoints::getLastControlRemote() const
{
    return _lastControlRemote;
}
LONG CtiCCTwoWayPoints::getLastControlOvUvId() const
{
    return _lastControlOvUvId;
}
LONG CtiCCTwoWayPoints::getLastControlOvUv() const
{
    return _lastControlOvUv;
}
LONG CtiCCTwoWayPoints::getLastControlNeutralFaultId() const
{
    return _lastControlNeutralFaultId;
}
LONG CtiCCTwoWayPoints::getLastControlNeutralFault() const
{
    return _lastControlNeutralFault;
}
LONG CtiCCTwoWayPoints::getLastControlScheduledId() const
{
    return _lastControlScheduledId;
}
LONG CtiCCTwoWayPoints::getLastControlScheduled() const
{
    return _lastControlScheduled;
}
LONG CtiCCTwoWayPoints::getLastControlDigitalId() const
{
    return _lastControlDigitalId;
}
LONG CtiCCTwoWayPoints::getLastControlDigital() const
{
    return _lastControlDigital;
}
LONG CtiCCTwoWayPoints::getLastControlAnalogId() const
{
    return _lastControlAnalogId;
}
LONG CtiCCTwoWayPoints::getLastControlAnalog() const
{
    return _lastControlAnalog;
}
LONG CtiCCTwoWayPoints::getLastControlTemperatureId() const
{
    return _lastControlTemperatureId;
}
LONG CtiCCTwoWayPoints::getLastControlTemperature() const
{
    return _lastControlTemperature;
}
LONG CtiCCTwoWayPoints::getOvConditionId() const
{
    return _ovConditionId;
}
LONG CtiCCTwoWayPoints::getOvCondition() const
{
    return _ovCondition;
}
LONG CtiCCTwoWayPoints::getUvConditionId() const
{
    return _uvConditionId;
}
LONG CtiCCTwoWayPoints::getUvCondition() const
{
    return _uvCondition;
}
LONG CtiCCTwoWayPoints::getOpFailedNeutralCurrentId() const
{
    return _opFailedNeutralCurrentId;
}
LONG CtiCCTwoWayPoints::getOpFailedNeutralCurrent() const
{
    return _opFailedNeutralCurrent;
}
LONG CtiCCTwoWayPoints::getNeutralCurrentFaultId() const
{
    return _neutralCurrentFaultId;
}
LONG CtiCCTwoWayPoints::getNeutralCurrentFault() const
{
    return _neutralCurrentFault;
}
LONG CtiCCTwoWayPoints::getBadRelayId() const
{
    return _badRelayId;
}
LONG CtiCCTwoWayPoints::getBadRelay() const
{
    return _badRelay;
}
LONG CtiCCTwoWayPoints::getDailyMaxOpsId() const
{
    return _dailyMaxOpsId;
}
LONG CtiCCTwoWayPoints::getDailyMaxOps() const
{
    return _dailyMaxOps;
}
LONG CtiCCTwoWayPoints::getVoltageDeltaAbnormalId() const
{
    return _voltageDeltaAbnormalId;
}
LONG CtiCCTwoWayPoints::getVoltageDeltaAbnormal() const
{
    return _voltageDeltaAbnormal;
}
LONG CtiCCTwoWayPoints::getTempAlarmId() const
{
    return _tempAlarmId;
}
LONG CtiCCTwoWayPoints::getTempAlarm() const
{
    return _tempAlarm;
}
LONG CtiCCTwoWayPoints::getDSTActiveId() const
{
    return _DSTActiveId;
}
LONG CtiCCTwoWayPoints::getDSTActive() const
{
    return _DSTActive;
}
LONG CtiCCTwoWayPoints::getNeutralLockoutId() const
{
    return _neutralLockoutId;
}
LONG CtiCCTwoWayPoints::getNeutralLockout() const
{
    return _neutralLockout;
}
LONG CtiCCTwoWayPoints::getIgnoredIndicatorId() const
{
    return _ignoredIndicatorId;
}
LONG CtiCCTwoWayPoints::getIgnoredIndicator() const
{
    return _ignoredIndicator;
}
LONG CtiCCTwoWayPoints::getRSSIId() const
{
    return _rssiId;
}
LONG CtiCCTwoWayPoints::getRSSI() const
{
    return _rssi;
}

LONG CtiCCTwoWayPoints::getIgnoredReasonId() const
{
    return _ignoredReasonId;
}
LONG CtiCCTwoWayPoints::getIgnoredReason() const
{
    return _ignoredReason;
}
long CtiCCTwoWayPoints::getVoltageControlId() const
{
    return _voltageControlId;
}
long CtiCCTwoWayPoints::getVoltageControl() const
{
    return _voltageControl;
}
LONG CtiCCTwoWayPoints::getUvSetPointId() const
{
    return _uvSetPointId;
}
LONG CtiCCTwoWayPoints::getUvSetPoint() const
{
    return _uvSetPoint;
}
LONG CtiCCTwoWayPoints::getOvSetPointId() const
{
    return _ovSetPointId;
}
LONG CtiCCTwoWayPoints::getOvSetPoint() const
{
    return _ovSetPoint;
}
LONG CtiCCTwoWayPoints::getOVUVTrackTimeId() const
{
    return _ovuvTrackTimeId;
}
LONG CtiCCTwoWayPoints::getOVUVTrackTime() const
{
    return _ovuvTrackTime;
}
LONG CtiCCTwoWayPoints::getNeutralCurrentSensorId() const
{
    return _neutralCurrentSensorId;
}
LONG CtiCCTwoWayPoints::getNeutralCurrentSensor() const
{
    return _neutralCurrentSensor;
}
LONG CtiCCTwoWayPoints::getNeutralCurrentAlarmSetPointId() const
{
    return _neutralCurrentAlarmSetPointId;
}
LONG CtiCCTwoWayPoints::getNeutralCurrentAlarmSetPoint() const
{
    return _neutralCurrentAlarmSetPoint;
}
long CtiCCTwoWayPoints::getTimeTempSeasonOneId() const
{
    return _timeTempSeasonOneId;
}
long CtiCCTwoWayPoints::getTimeTempSeasonOne() const
{
    return _timeTempSeasonOne;
}
long CtiCCTwoWayPoints::getTimeTempSeasonTwoId() const
{
    return _timeTempSeasonTwoId;
}
long CtiCCTwoWayPoints::getTimeTempSeasonTwo() const
{
    return _timeTempSeasonTwo;
}
long CtiCCTwoWayPoints::getVarControlId() const
{
    return _varControlId;
}
long CtiCCTwoWayPoints::getVarControl() const
{
    return _varControl;
}
LONG CtiCCTwoWayPoints::getUDPIpAddressId() const
{
    return _udpIpAddressId;
}
ULONG CtiCCTwoWayPoints::getUDPIpAddress() const
{
    return _udpIpAddress;
}
LONG CtiCCTwoWayPoints::getUDPPortNumberId() const
{
    return _udpPortNumberId;
}
LONG CtiCCTwoWayPoints::getUDPPortNumber() const
{
    return _udpPortNumber;
}
LONG CtiCCTwoWayPoints::getVoltageId() const
{
    return _voltageId;
}
LONG CtiCCTwoWayPoints::getVoltage() const
{
    return _voltage;
}
LONG CtiCCTwoWayPoints::getHighVoltageId() const
{
    return _highVoltageId;
}
LONG CtiCCTwoWayPoints::getHighVoltage() const
{
    return _highVoltage;
}
LONG CtiCCTwoWayPoints::getLowVoltageId() const
{
    return _lowVoltageId;
}
LONG CtiCCTwoWayPoints::getLowVoltage() const
{
    return _lowVoltage;
}
LONG CtiCCTwoWayPoints::getDeltaVoltageId() const
{
    return _deltaVoltageId;
}
LONG CtiCCTwoWayPoints::getDeltaVoltage() const
{
    return _deltaVoltage;
}
LONG CtiCCTwoWayPoints::getAnalogInput1Id() const
{
    return _analogInput1Id;
}
LONG CtiCCTwoWayPoints::getAnalogInput1() const
{
    return _analogInput1;
}
LONG CtiCCTwoWayPoints::getTemperatureId() const
{
    return _temperatureId;
}
LONG CtiCCTwoWayPoints::getTemperature() const
{
    return _temperature;
}
LONG CtiCCTwoWayPoints::getTotalOpCountId() const
{
    return _totalOpCountId;
}
LONG CtiCCTwoWayPoints::getTotalOpCount() const
{
    return _totalOpCount;
}
LONG CtiCCTwoWayPoints::getOvCountId() const
{
    return _ovCountId;
}
LONG CtiCCTwoWayPoints::getOvCount() const
{
    return _ovCount;
}
LONG CtiCCTwoWayPoints::getUvCountId() const
{
    return _uvCountId;
}
LONG CtiCCTwoWayPoints::getUvCount() const
{
    return _uvCount;
}

const CtiTime& CtiCCTwoWayPoints::getOvUvCountResetDate() const
{
    return _ovuvCountResetDate;
}
const CtiTime& CtiCCTwoWayPoints::getLastOvUvDateTime() const
{
    return _lastOvUvDateTime;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setPAOId(LONG paoId)
{
    _paoid = paoId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setCapacitorBankStateId(LONG pointId)
{
    if (pointId != _capacitorBankStateId)
    {
        _dirty = TRUE;
    }
    _capacitorBankStateId = pointId;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setCapacitorBankState(LONG value)
{
    if (value != _capacitorBankState)
    {
        _dirty = TRUE;
    }
    _capacitorBankState = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setReCloseBlockedId(LONG pointId)
{
    if (pointId != _reCloseBlockedId)
    {
        _dirty = TRUE;
    }
    _reCloseBlockedId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setReCloseBlocked(LONG value)
{
    if (value != _reCloseBlocked)
    {
        _dirty = TRUE;
    }
    _reCloseBlocked = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setControlModeId(LONG pointId)
{
    if (pointId != _controlModeId)
    {
        _dirty = TRUE;
    }
    _controlModeId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setControlMode(LONG value)
{
    if (value != _controlMode)
    {
        _dirty = TRUE;
    }
    _controlMode = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setAutoVoltControlId(LONG pointId)
{
    if (pointId != _autoVoltControlId)
    {
        _dirty = TRUE;
    }
    _autoVoltControlId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setAutoVoltControl(LONG value)
{
    if (value != _autoVoltControl)
    {
        _dirty = TRUE;
    }
    _autoVoltControl = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlLocalId(LONG pointId)
{
    if (pointId != _lastControlLocalId)
    {
        _dirty = TRUE;
    }
    _lastControlLocalId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlLocal(LONG value)
{
    if (value != _lastControlLocal)
    {
        _dirty = TRUE;
    }
    _lastControlLocal = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlRemoteId(LONG pointId)
{
    if (pointId != _lastControlRemoteId)
    {
        _dirty = TRUE;
    }
    _lastControlRemoteId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlRemote(LONG value)
{
    if (value != _lastControlRemote)
    {
        _dirty = TRUE;
    }
    _lastControlRemote = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlOvUvId(LONG pointId)
{
    if (pointId != _lastControlOvUvId)
    {
        _dirty = TRUE;
    }
    _lastControlOvUvId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlOvUv(LONG value)
{
    if (value != _lastControlOvUv)
    {
        _dirty = TRUE;
    }
    _lastControlOvUv = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlNeutralFaultId(LONG pointId)
{
    if (pointId != _lastControlNeutralFaultId)
    {
        _dirty = TRUE;
    }
    _lastControlNeutralFaultId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlNeutralFault(LONG value)
{
    if (value != _lastControlNeutralFault)
    {
        _dirty = TRUE;
    }
    _lastControlNeutralFault = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlScheduledId(LONG pointId)
{
    if (pointId != _lastControlScheduledId)
    {
        _dirty = TRUE;
    }
    _lastControlScheduledId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlScheduled(LONG value)
{
    if (value != _lastControlScheduled)
    {
        _dirty = TRUE;
    }
    _lastControlScheduled = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlDigitalId(LONG pointId)
{
    if (pointId != _lastControlDigitalId)
    {
        _dirty = TRUE;
    }
    _lastControlDigitalId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlDigital(LONG value)
{
    if (value != _lastControlDigital)
    {
        _dirty = TRUE;
    }
    _lastControlDigital = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlAnalogId(LONG pointId)
{
    if (pointId != _lastControlAnalogId)
    {
        _dirty = TRUE;
    }
    _lastControlAnalogId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlAnalog(LONG value)
{
    if (value != _lastControlAnalog)
    {
        _dirty = TRUE;
    }
    _lastControlAnalog = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlTemperatureId(LONG pointId)
{
    if (pointId != _lastControlTemperatureId)
    {
        _dirty = TRUE;
    }
    _lastControlTemperatureId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlTemperature(LONG value)
{
    if (value != _lastControlTemperature)
    {
        _dirty = TRUE;
    }
    _lastControlTemperature = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvConditionId(LONG pointId)
{
    if (pointId != _ovConditionId)
    {
        _dirty = TRUE;
    }
    _ovConditionId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvCondition(LONG value)
{
    if (value != _ovCondition)
    {
        _dirty = TRUE;
    }
    _ovCondition = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUvConditionId(LONG pointId)
{
    if (pointId != _uvConditionId)
    {
        _dirty = TRUE;
    }
    _uvConditionId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUvCondition(LONG value)
{
    if (value != _uvCondition)
    {
        _dirty = TRUE;
    }
    _uvCondition = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOpFailedNeutralCurrentId(LONG pointId)
{
    if (pointId != _opFailedNeutralCurrentId)
    {
        _dirty = TRUE;
    }
    _opFailedNeutralCurrentId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOpFailedNeutralCurrent(LONG value)
{
    if (value != _opFailedNeutralCurrent)
    {
        _dirty = TRUE;
    }
    _opFailedNeutralCurrent = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralCurrentFaultId(LONG pointId)
{
    if (pointId != _neutralCurrentFaultId)
    {
        _dirty = TRUE;
    }
    _neutralCurrentFaultId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralCurrentFault(LONG value)
{
    if (value != _neutralCurrentFault)
    {
        _dirty = TRUE;
    }
    _neutralCurrentFault = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setBadRelayId(LONG pointId)
{
    if (pointId != _badRelayId)
    {
        _dirty = TRUE;
    }
    _badRelayId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setBadRelay(LONG value)
{
    if (value != _badRelay)
    {
        _dirty = TRUE;
    }
    _badRelay = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setDailyMaxOpsId(LONG pointId)
{
    if (pointId != _dailyMaxOpsId)
    {
        _dirty = TRUE;
    }
    _dailyMaxOpsId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setDailyMaxOps(LONG value)
{
    if (value != _dailyMaxOps)
    {
        _dirty = TRUE;
    }
    _dailyMaxOps = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setVoltageDeltaAbnormalId(LONG pointId)
{
    if (pointId != _voltageDeltaAbnormalId)
    {
        _dirty = TRUE;
    }
    _voltageDeltaAbnormalId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setVoltageDeltaAbnormal(LONG value)
{
    if (value != _voltageDeltaAbnormal)
    {
        _dirty = TRUE;
    }
    _voltageDeltaAbnormal = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setTempAlarmId(LONG pointId)
{
    if (pointId != _tempAlarmId)
    {
        _dirty = TRUE;
    }
    _tempAlarmId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setTempAlarm(LONG value)
{
    if (value != _tempAlarm)
    {
        _dirty = TRUE;
    }
    _tempAlarm = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setDSTActiveId(LONG pointId)
{
    if (pointId != _DSTActiveId)
    {
        _dirty = TRUE;
    }
    _DSTActiveId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setDSTActive(LONG value)
{
    if (value != _DSTActive)
    {
        _dirty = TRUE;
    }
    _DSTActive = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralLockoutId(LONG pointId)
{
    if (pointId != _neutralLockoutId)
    {
        _dirty = TRUE;
    }
    _neutralLockoutId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralLockout(LONG value)
{
    if (value != _neutralLockout)
    {
        _dirty = TRUE;
    }
    _neutralLockout = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setRSSIId(LONG pointId)
{
    if (pointId != _rssiId)
    {
        _dirty = TRUE;
    }
    _rssiId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setRSSI(LONG value)
{
    if (value != _rssi)
    {
        _dirty = TRUE;
    }
    _rssi = value;
    return *this;

}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setIgnoredIndicatorId(LONG pointId)
{
    if (pointId != _ignoredIndicatorId)
    {
        _dirty = TRUE;
    }
    _ignoredIndicatorId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setIgnoredIndicator(LONG value)
{
    if (value != _ignoredIndicator)
    {
        _dirty = TRUE;
    }
    _ignoredIndicator = value;
    return *this;

}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setIgnoredReasonId(LONG pointId)
{
    if (pointId != _ignoredReasonId)
    {
        _dirty = TRUE;
    }
    _ignoredReasonId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setIgnoredReason(LONG value)
{
    if (value != _ignoredReason)
    {
        _dirty = TRUE;
    }
    _ignoredReason = value;
    return *this;

}

void CtiCCTwoWayPoints::setVoltageControlId(long value)
{
    if (value != _voltageControlId)
    {
        _dirty = TRUE;
    }
    _voltageControlId = value;
}

void CtiCCTwoWayPoints::setVoltageControl(long value)
{
    if (value != _voltageControl)
    {
        _dirty = TRUE;
    }
    _voltageControl = value;
}


CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUvSetPointId(LONG pointId)
{
    if (pointId != _uvSetPointId)
    {
        _dirty = TRUE;
    }
    _uvSetPointId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUvSetPoint(LONG value)
{
    if (value != _uvSetPoint)
    {
        _dirty = TRUE;
    }
    _uvSetPoint = value;
    return *this;

}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvSetPointId(LONG pointId)
{
    if (pointId != _ovSetPointId)
    {
        _dirty = TRUE;
    }
    _ovSetPointId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvSetPoint(LONG value)
{
    if (value != _ovSetPoint)
    {
        _dirty = TRUE;
    }
    _ovSetPoint = value;
    return *this;

}


CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOVUVTrackTimeId(LONG pointId)
{
    if (pointId != _ovuvTrackTimeId)
    {
        _dirty = TRUE;
    }
    _ovuvTrackTimeId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOVUVTrackTime(LONG value)
{
    if (value != _ovuvTrackTime)
    {
        _dirty = TRUE;
    }
    _ovuvTrackTime = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralCurrentSensorId(LONG pointId)
{
    if (pointId != _neutralCurrentSensorId)
    {
        _dirty = TRUE;
    }
    _neutralCurrentSensorId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralCurrentSensor(LONG value)
{
    if (value != _neutralCurrentSensor)
    {
        _dirty = TRUE;
    }
    _neutralCurrentSensor = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralCurrentAlarmSetPointId(LONG pointId)
{
    if (pointId != _neutralCurrentAlarmSetPointId)
    {
        _dirty = TRUE;
    }
    _neutralCurrentAlarmSetPointId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setNeutralCurrentAlarmSetPoint(LONG value)
{
    if (value != _neutralCurrentAlarmSetPoint)
    {
        _dirty = TRUE;
    }
    _neutralCurrentAlarmSetPoint = value;
    return *this;
}

void CtiCCTwoWayPoints::setTimeTempSeasonOneId(long value)
{
    if (value != _timeTempSeasonOneId)
    {
        _dirty = TRUE;
    }
    _timeTempSeasonOneId = value;
}

void CtiCCTwoWayPoints::setTimeTempSeasonOne(long value)
{
    if (value != _timeTempSeasonOne)
    {
        _dirty = TRUE;
    }
    _timeTempSeasonOne = value;
}

void CtiCCTwoWayPoints::setTimeTempSeasonTwoId(long value)
{
    if (value != _timeTempSeasonTwoId)
    {
        _dirty = TRUE;
    }
    _timeTempSeasonTwoId = value;
}

void CtiCCTwoWayPoints::setTimeTempSeasonTwo(long value)
{
    if (value != _timeTempSeasonTwo)
    {
        _dirty = TRUE;
    }
    _timeTempSeasonTwo = value;
}

void CtiCCTwoWayPoints::setVarControlId(long value)
{
    if (value != _varControlId )
    {
        _dirty = TRUE;
    }
    _varControlId = value;
}

void CtiCCTwoWayPoints::setVarControl(long value)
{
    if (value != _varControl)
    {
        _dirty = TRUE;
    }
    _varControl = value;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUDPIpAddressId(LONG pointId)
{
    if (pointId != _udpIpAddressId)
    {
        _dirty = TRUE;
    }
    _udpIpAddressId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUDPIpAddress(ULONG value)
{
    if (value != _udpIpAddress)
    {
        _dirty = TRUE;
    }
    _udpIpAddress = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUDPPortNumberId(LONG pointId)
{
    if (pointId != _udpPortNumberId)
    {
        _dirty = TRUE;
    }
    _udpPortNumberId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUDPPortNumber(LONG value)
{
    if (value != _udpPortNumber)
    {
        _dirty = TRUE;
    }
    _udpPortNumber = value;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setVoltageId(LONG pointId)
{
    if (pointId != _voltageId)
    {
        _dirty = TRUE;
    }
    _voltageId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setVoltage(LONG value)
{
    if (value != _voltage)
    {
        _dirty = TRUE;
    }
    _voltage = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setHighVoltageId(LONG pointId)
{
    if (pointId != _highVoltageId)
    {
        _dirty = TRUE;
    }
    _highVoltageId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setHighVoltage(LONG value)
{
    if (value != _highVoltage)
    {
        _dirty = TRUE;
    }
    _highVoltage = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLowVoltageId(LONG pointId)
{
    if (pointId != _lowVoltageId)
    {
        _dirty = TRUE;
    }
    _lowVoltageId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLowVoltage(LONG value)
{
    if (value != _lowVoltage)
    {
        _dirty = TRUE;
    }
    _lowVoltage = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setDeltaVoltageId(LONG pointId)
{
    if (pointId != _deltaVoltageId)
    {
        _dirty = TRUE;
    }
    _deltaVoltageId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setDeltaVoltage(LONG value)
{
    if (value != _deltaVoltage)
    {
        _dirty = TRUE;
    }
    _deltaVoltage = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setAnalogInput1Id(LONG pointId)
{
    if (pointId != _analogInput1Id)
    {
        _dirty = TRUE;
    }
    _analogInput1Id = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setAnalogInput1(LONG value)
{
    if (value != _analogInput1)
    {
        _dirty = TRUE;
    }
    _analogInput1 = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setTemperatureId(LONG pointId)
{
    if (pointId != _temperatureId)
    {
        _dirty = TRUE;
    }
    _temperatureId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setTemperature(LONG value)
{
    if (value != _temperature)
    {
        _dirty = TRUE;
    }
    _temperature = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setTotalOpCountId(LONG pointId)
{
    if (pointId != _totalOpCountId)
    {
        _dirty = TRUE;
    }
    _totalOpCountId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setTotalOpCount(LONG value)
{
    if (value != _totalOpCount)
    {
        _dirty = TRUE;
    }
    _totalOpCount = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvCountId(LONG pointId)
{
    if (pointId != _ovCountId)
    {
        _dirty = TRUE;
    }
    _ovCountId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvCount(LONG value)
{
    if (value != _ovCount)
    {
        _dirty = TRUE;
    }
    _ovCount = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUvCountId(LONG pointId)
{
    if (pointId != _uvCountId)
    {
        _dirty = TRUE;
    }
    _uvCountId = pointId;
    return *this;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setUvCount(LONG value)
{
    if (value != _uvCount)
    {
        _dirty = TRUE;
    }
    _uvCount = value;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setOvUvCountResetDate(const CtiTime eventTime)
{
    if (eventTime != _ovuvCountResetDate)
    {
        _dirty = TRUE;
    }
    _ovuvCountResetDate = eventTime;
    return *this;
}
CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastOvUvDateTime(const CtiTime eventTime)
{
    if (eventTime != _uvCount)
    {
        _dirty = TRUE;
    }
    _lastOvUvDateTime = eventTime;
    return *this;
}



BOOL CtiCCTwoWayPoints::isDirty()
{
    return _dirty;
}
void CtiCCTwoWayPoints::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
        if( !_insertDynamicDataFlag )
        {
            static const string updateSql = "update dynamiccctwowaycbc set "
                                            "recloseblocked = ?, "
                                            "controlmode = ?, "
                                            "autovoltcontrol = ?, "
                                            "lastcontrol = ?, "
                                            "condition = ?, "
                                            "opfailedneutralcurrent = ?, "
                                            "neutralcurrentfault = ?, "
                                            "badrelay = ?, "
                                            "dailymaxops = ?, "
                                            "voltagedeltaabnormal = ?, "
                                            "tempalarm = ?, "
                                            "dstactive = ?, "
                                            "neutrallockout = ?, "
                                            "ignoredindicator = ?, "
                                            "voltage = ?, "
                                            "highvoltage = ?, "
                                            "lowvoltage = ?, "
                                            "deltavoltage = ?, "
                                            "analoginputone = ?, "
                                            "temp = ?, "
                                            "rssi = ?, "
                                            "ignoredreason = ?, "
                                            "totalopcount = ?, "
                                            "uvopcount = ?, "
                                            "ovopcount = ?, "
                                            "ovuvcountresetdate = ?, "
                                            "uvsetpoint = ?, "
                                            "ovsetpoint = ?, "
                                            "ovuvtracktime = ?, "
                                            "lastovuvdatetime = ?, "
                                            "neutralcurrentsensor = ?, "
                                            "neutralcurrentalarmsetpoint = ?, "
                                            "ipaddress = ?, "
                                            "udpport = ?"
                                            " where deviceid = ?";

            _lastControlReason = ( ( _lastControlRemote & 0x01)            ||
                                ((_lastControlLocal & 0x01 )        << 1 ) ||
                                ((_lastControlOvUv & 0x01 )         << 2 ) ||
                                ((_lastControlNeutralFault & 0x01)  << 3 ) ||
                                ((_lastControlScheduled & 0x01)     << 4 ) ||
                                ((_lastControlDigital & 0x01)       << 5 ) ||
                                ((_lastControlAnalog & 0x01 )       << 6 ) ||
                                ((_lastControlTemperature & 0x01 )  << 7 ) );
            INT condition = 0;
            if (_uvCondition)
                condition = 1;
            else if (_ovCondition)
                condition = 2;
            else
                condition = 0;

            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater 
                << (string)(_reCloseBlocked?"Y":"N")
                << (string)(_controlMode?"Y":"N")
                << (string)(_autoVoltControl?"Y":"N") 
                << _lastControlReason 
                << condition 
                << (string)(_opFailedNeutralCurrent?"Y":"N") 
                << (string)(_neutralCurrentFault?"Y":"N")
                << (string)(_badRelay?"Y":"N")
                << (string)(_dailyMaxOps?"Y":"N")
                << (string)(_voltageDeltaAbnormal?"Y":"N")
                << (string)(_tempAlarm?"Y":"N") 
                << (string)(_DSTActive?"Y":"N")
                << (string)(_neutralLockout?"Y":"N") 
                << (string)(_ignoredIndicator?"Y":"N") 
                << _voltage 
                << _highVoltage 
                << _lowVoltage 
                << _deltaVoltage 
                << _analogInput1 
                << _temperature 
                << _rssi 
                << _ignoredReason 
                << _totalOpCount 
                << _uvCount 
                << _ovCount
                << _ovuvCountResetDate
                << _uvSetPoint 
                << _ovSetPoint 
                << _ovuvTrackTime 
                << _lastOvUvDateTime
                << _neutralCurrentSensor 
                << _neutralCurrentAlarmSetPoint 
                << _udpIpAddress 
                << _udpPortNumber 
                << _paoid;

            if(updater.execute())    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = updater.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted TwoWay CBC data into DynamicCCTwoWayCBC: " << getPAOId() << endl;
            }
            static const string insertSql = "insert into dynamiccctwowaycbc values ( "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ?)";

            Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

            INT lastControl = ( ( _lastControlLocal & 0x01)||
                                (_lastControlRemote & 0x02 ) ||
                                (_lastControlOvUv & 0x04 ) ||
                                (_lastControlNeutralFault & 0x08 ) ||
                                (_lastControlScheduled & 0x10 ) ||
                                (_lastControlDigital & 0x20 ) ||
                                (_lastControlAnalog & 0x40 ) ||
                                (_lastControlTemperature & 0x80 ) );
            INT condition = 0;
            if (_uvCondition)
                condition = 1;
            else if (_ovCondition)
                condition = 2;
            else
                condition = 0;


            dbInserter << _paoid
                     << ((string)(_reCloseBlocked?"Y":"N"))
                     << ((string)(_controlMode?"Y":"N"))
                     << ((string)(_autoVoltControl?"Y":"N"))
                     << lastControl
                     << condition
                     << (string)(_opFailedNeutralCurrent?"Y":"N")
                     << (string)(_neutralCurrentFault?"Y":"N")
                     << (string)(_badRelay?"Y":"N")
                     << (string)(_dailyMaxOps?"Y":"N")
                     << (string)(_voltageDeltaAbnormal?"Y":"N")
                     << (string)(_tempAlarm?"Y":"N")
                     << (string)(_DSTActive?"Y":"N")
                     << (string)(_neutralLockout?"Y":"N")
                     << (string)(_ignoredIndicator?"Y":"N")
                     << _voltage
                     << _highVoltage
                     << _lowVoltage
                     << _deltaVoltage
                     << _analogInput1
                     << _temperature
                     << _rssi
                     << _ignoredReason
                     << _totalOpCount
                     << _uvCount
                     << _ovCount
                     << _ovuvCountResetDate
                     << _uvSetPoint
                     << _ovSetPoint
                     << _ovuvTrackTime
                     << _lastOvUvDateTime
                     << _neutralCurrentSensor
                     << _neutralCurrentAlarmSetPoint
                     << _udpIpAddress
                     << _udpPortNumber;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = dbInserter.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            if(dbInserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = dbInserter.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
    }

}

BOOL CtiCCTwoWayPoints::setTwoWayPointId(int pointtype, int offset, LONG pointId)
{
    BOOL retVal = FALSE;

    switch (pointtype)
    {
        //case StatusPointType:
        case 0:
        {
            switch (offset)
            {
                case 1:
                {
                    setCapacitorBankStateId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 2:
                {
                    setReCloseBlockedId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 3:
                {
                    setControlModeId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 4:
                {
                    setAutoVoltControlId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 5:
                {
                    setLastControlLocalId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 6:
                {
                     setLastControlRemoteId(pointId);
                     retVal = TRUE;
                    break;
                }
                case 7:
                {
                    setLastControlOvUvId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 8:
                {
                    setLastControlNeutralFaultId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 9:
                {
                    setLastControlScheduledId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 10:
                {
                    setLastControlDigitalId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 11:
                {
                    setLastControlAnalogId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 12:
                {
                    setLastControlTemperatureId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 13:
                {
                    setOvConditionId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 14:
                {
                    setUvConditionId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 15:
                {
                    setOpFailedNeutralCurrentId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 16:
                {
                    setNeutralCurrentFaultId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 24:
                {
                    setBadRelayId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 25:
                {
                    setDailyMaxOpsId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 26:
                {
                    setVoltageDeltaAbnormalId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 27:
                {
                    setTempAlarmId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 28:
                {
                    setDSTActiveId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 29:
                {
                    setNeutralLockoutId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 34:
                {
                    setIgnoredIndicatorId(pointId);
                    retVal = TRUE;
                    break;
                }

                default:
                    break;

            }
            break;
        }
        //case AnalogPointType:
        case 1:
        {
            switch (offset)
            {
                 case 5:
                 {
                     setVoltageId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 6:
                 {
                      setHighVoltageId(pointId);
                      retVal = TRUE;
                     break;
                 }
                 case 7:
                 {
                     setLowVoltageId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 8:
                 {
                     setDeltaVoltageId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 9:
                 {
                     setAnalogInput1Id(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 10:
                 {
                     setTemperatureId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 13:
                 {
                     setRSSIId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 14:
                 {
                     setIgnoredReasonId(pointId);
                     retVal = TRUE;
                     break;
                 }

                 //dnp analog output points have offsets starting with 10000
                 case 10001:
                 {
                     //Voltage Control Flags
                     setVoltageControlId(pointId);
                     retVal = true;
                     break;
                 }
                 case 10002:
                 {
                     setUvSetPointId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 10003:
                 {
                     setOvSetPointId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 10004:
                 {
                     setOVUVTrackTimeId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 10010:
                 {
                     setNeutralCurrentSensorId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 10011:
                 {
                     setNeutralCurrentAlarmSetPointId(pointId);
                     retVal = TRUE;
                     break;
                 }
                 case 10026:
                 {
                     setTimeTempSeasonOneId(pointId);
                     retVal = true;
                     break;
                 }
                 case 10042:
                 {
                     setTimeTempSeasonTwoId(pointId);
                     retVal = true;
                     break;
                 }
                 case 10068:
                 {
                     setVarControlId(pointId);
                     break;
                 }
                 case 20001:
                 {
                     setUDPIpAddressId(pointId);
                     retVal = TRUE;
                     break;
                 }

                 case 20002:
                 {
                     setUDPPortNumberId(pointId);
                     retVal = TRUE;
                     break;
                 }

                 default:
                    break;
            }
            break;
        }
        //case PulseAccumulatorPointType:
        case 2:
        {
            switch (offset)
            {
                case 1:
                {
                    setTotalOpCountId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 2:
                {
                    setUvCountId(pointId);
                    retVal = TRUE;
                    break;
                }
                case 3:
                {
                    setOvCountId(pointId);
                    retVal = TRUE;
                    break;
                }
                default:
                    break;
            }
            break;
        }
        default:
            break;

    }
    return retVal;

}

BOOL CtiCCTwoWayPoints::setTwoWayStatusPointValue(LONG pointID, LONG value)
{
    BOOL retVal = FALSE;
    if( getCapacitorBankStateId() == pointID )
    {
        setCapacitorBankState(value);
        retVal = TRUE;
    }
    else if ( getReCloseBlockedId() == pointID )
    {
        setReCloseBlocked(value);
        retVal = TRUE;
    }
    else if ( getControlModeId() == pointID )
    {
        setControlMode(value);
        retVal = TRUE;
    }
    else if ( getAutoVoltControlId() == pointID )
    {
        setAutoVoltControl(value);
        retVal = TRUE;
    }
    else if ( getLastControlLocalId() == pointID )
    {
        setLastControlLocal(value);
        retVal = TRUE;
    }
    else if ( getLastControlRemoteId() == pointID )
    {
        setLastControlRemote(value);
        retVal = TRUE;
    }
    else if ( getLastControlOvUvId() == pointID )
    {
        setLastControlOvUv(value);
        retVal = TRUE;
    }
    else if ( getLastControlNeutralFaultId() == pointID )
    {
        setLastControlNeutralFault(value);
        retVal = TRUE;
    }
    else if ( getLastControlScheduledId() == pointID )
    {
        setLastControlScheduled(value);
        retVal = TRUE;
    }
    else if ( getLastControlDigitalId() == pointID )
    {
        setLastControlDigital(value);
        retVal = TRUE;
    }
    else if ( getLastControlAnalogId() == pointID )
    {
        setLastControlAnalog(value);
        retVal = TRUE;
    }
    else if ( getLastControlTemperatureId() == pointID )
    {
        setLastControlTemperature(value);
        retVal = TRUE;
    }
    else if ( getOvConditionId() == pointID )
    {
        setOvCondition(value);
        retVal = TRUE;
    }
    else if ( getUvConditionId() == pointID )
    {
        setUvCondition(value);
        retVal = TRUE;
    }
    else if ( getOpFailedNeutralCurrentId() == pointID )
    {
        setOpFailedNeutralCurrent(value);
        retVal = TRUE;
    }
    else if ( getNeutralCurrentFaultId() == pointID )
    {
        setNeutralCurrentFault(value);
        retVal = TRUE;
    }
    else if ( getBadRelayId() == pointID )
    {
        setBadRelay(value);
        retVal = TRUE;
    }
    else if ( getDailyMaxOpsId() == pointID )
    {
        setDailyMaxOps(value);
        retVal = TRUE;
    }
    else if ( getVoltageDeltaAbnormalId() == pointID )
    {
        setVoltageDeltaAbnormal(value);
        retVal = TRUE;
    }
    else if ( getTempAlarmId() == pointID )
    {
        setTempAlarm(value);
        retVal = TRUE;
    }
    else if ( getDSTActiveId() == pointID )
    {
        setDSTActive(value);
        retVal = TRUE;
    }
    else if ( getNeutralLockoutId() == pointID )
    {
        setNeutralLockout(value);
        retVal = TRUE;
    }
    else if ( getIgnoredIndicatorId() == pointID )
    {
        setIgnoredIndicator(value);
        retVal = TRUE;
    }

    return retVal;

}
BOOL CtiCCTwoWayPoints::setTwoWayAnalogPointValue(LONG pointID, LONG value)
{

    BOOL retVal = FALSE;
    if( getVoltageId() == pointID )
    {
        setVoltage(value);
        retVal = TRUE;
    }
    else if ( getHighVoltageId() == pointID )
    {
        setHighVoltage(value);
        retVal = TRUE;
    }
    else if ( getLowVoltageId() == pointID )
    {
        setLowVoltage(value);
        retVal = TRUE;
    }
    else if ( getDeltaVoltageId() == pointID )
    {
        setDeltaVoltage(value);
        retVal = TRUE;
    }
    else if ( getAnalogInput1Id() == pointID )
    {
        setAnalogInput1(value);
        retVal = TRUE;
    }
    else if ( getTemperatureId() == pointID )
    {
        setTemperature(value);
        retVal = TRUE;
    }
    else if ( getRSSIId() == pointID )
    {
        setRSSI(value);
        retVal = TRUE;
    }
    else if ( getIgnoredReasonId() == pointID )
    {
        setIgnoredReason(value);
        retVal = TRUE;
    }
    else if (getVoltageControlId() == pointID)
    {
        setVoltageControl(value);
    }
    else if ( getUvSetPointId() == pointID )
    {
        setUvSetPoint(value);
        retVal = TRUE;
    }
    else if ( getOvSetPointId() == pointID )
    {
        setOvSetPoint(value);
        retVal = TRUE;
    }
    else if ( getOVUVTrackTimeId() == pointID )
    {
        setOVUVTrackTime(value);
        retVal = TRUE;
    }
    else if ( getNeutralCurrentSensorId() == pointID )
    {
        setNeutralCurrentSensor(value);
        retVal = TRUE;
    }
    else if ( getNeutralCurrentAlarmSetPointId() == pointID )
    {
        setNeutralCurrentAlarmSetPoint(value);
        retVal = TRUE;
    }
    else if (getTimeTempSeasonOneId() == pointID)
    {
        setTimeTempSeasonOne(value);
    }
    else if (getTimeTempSeasonTwoId() == pointID)
    {
        setTimeTempSeasonTwo(value);
    }
    else if (getVarControlId() == pointID)
    {
        setVoltageControl(value);
    }
    else if ( getUDPIpAddressId() == pointID )
    {
        setUDPIpAddress(value);
        retVal = TRUE;
    }
    else if ( getUDPPortNumberId() == pointID )
    {
        setUDPPortNumber(value);
        retVal = TRUE;
    }

    return retVal;
}
BOOL CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue(LONG pointID, LONG value)
{

    BOOL retVal = FALSE;
    if( getTotalOpCountId() == pointID )
    {
        setTotalOpCount(value);
        retVal = TRUE;
    }
    else if ( getOvCountId() == pointID )
    {
        setOvCount(value);
        retVal = TRUE;
    }
    else if ( getUvCountId() == pointID )
    {
        setUvCount(value);
        retVal = TRUE;
    }

    return retVal;
}



CtiCCTwoWayPoints& CtiCCTwoWayPoints::addAllCBCPointsToRegMsg(std::set<long>& pointList)
{

    if( getCapacitorBankStateId()  > 0 )
    {
        pointList.insert(getCapacitorBankStateId());
    }
    if ( getReCloseBlockedId()  > 0 )
    {
        pointList.insert(getReCloseBlockedId());
    }
    if ( getControlModeId()  > 0 )
    {
        pointList.insert(getControlModeId());
    }
    if ( getAutoVoltControlId()  > 0 )
    {
        pointList.insert(getAutoVoltControlId());
    }
    if ( getLastControlLocalId()  > 0 )
    {
        pointList.insert(getLastControlLocalId());
    }
    if ( getLastControlRemoteId()  > 0 )
    {
        pointList.insert(getLastControlRemoteId());
    }
    if ( getLastControlOvUvId() > 0 )
    {
        pointList.insert(getLastControlOvUvId());
    }
    if ( getLastControlNeutralFaultId()  > 0 )
    {
        pointList.insert(getLastControlNeutralFaultId());
    }
    if ( getLastControlScheduledId()  > 0 )
    {
        pointList.insert(getLastControlScheduledId());
    }
    if ( getLastControlDigitalId()  > 0 )
    {
        pointList.insert(getLastControlDigitalId());
    }
    if ( getLastControlAnalogId()  > 0 )
    {
        pointList.insert(getLastControlAnalogId());
    }
    if ( getLastControlTemperatureId()  > 0 )
    {
        pointList.insert(getLastControlTemperatureId());
    }
    if ( getOvConditionId()  > 0 )
    {
        pointList.insert(getOvConditionId());
    }
    if ( getUvConditionId()  > 0 )
    {
        pointList.insert(getUvConditionId());
    }
    if ( getOpFailedNeutralCurrentId()  > 0 )
    {
        pointList.insert(getOpFailedNeutralCurrentId());
    }
    if ( getNeutralCurrentFaultId()  > 0 )
    {
        pointList.insert(getNeutralCurrentFaultId());
    }
    if ( getBadRelayId()  > 0 )
    {
        pointList.insert(getBadRelayId());
    }
    if ( getDailyMaxOpsId()  > 0 )
    {
        pointList.insert(getDailyMaxOpsId());
    }
    if ( getVoltageDeltaAbnormalId()  > 0 )
    {
        pointList.insert(getVoltageDeltaAbnormalId());
    }
    if ( getTempAlarmId()  > 0 )
    {
        pointList.insert(getTempAlarmId());
    }
    if ( getDSTActiveId()  > 0 )
    {
        pointList.insert(getDSTActiveId());
    }
    if ( getNeutralLockoutId() > 0 )
    {
        pointList.insert(getNeutralLockoutId());
    }
    if ( getIgnoredIndicatorId()  > 0 )
    {
        pointList.insert(getIgnoredIndicatorId());
    }
    if ( getRSSIId()  > 0 )
    {
        pointList.insert(getRSSIId());
    }
    if ( getIgnoredReasonId()  > 0 )
    {
        pointList.insert(getIgnoredReasonId());
    }
    if ( getUvSetPointId()  > 0 )
    {
        pointList.insert(getUvSetPointId());
    }
    if ( getOvSetPointId() > 0 )
    {
        pointList.insert(getOvSetPointId());
    }
    if ( getOVUVTrackTimeId() > 0 )
    {
        pointList.insert(getOVUVTrackTimeId());
    }
    if ( getNeutralCurrentSensorId() > 0 )
    {
        pointList.insert(getNeutralCurrentSensorId());
    }
    if ( getNeutralCurrentAlarmSetPointId() > 0 )
    {
        pointList.insert(getNeutralCurrentAlarmSetPointId());
    }
    if ( getUDPIpAddressId() > 0 )
    {
        pointList.insert(getUDPIpAddressId());
    }
    if ( getUDPPortNumberId() > 0 )
    {
        pointList.insert(getUDPPortNumberId());
    }
    if ( getVoltageId()  > 0 )
    {
        pointList.insert(getVoltageId());
    }
    if ( getHighVoltageId()  > 0 )
    {
        pointList.insert(getHighVoltageId());
    }
    if ( getLowVoltageId()  > 0 )
    {
        pointList.insert(getLowVoltageId());
    }
    if ( getDeltaVoltageId()  > 0 )
    {
        pointList.insert(getDeltaVoltageId());
    }
    if ( getAnalogInput1Id()  > 0 )
    {
        pointList.insert(getAnalogInput1Id());
    }
    if ( getTemperatureId() > 0 )
    {
        pointList.insert(getTemperatureId());
    }
    if ( getTotalOpCountId()  > 0 )
    {
        pointList.insert(getTotalOpCountId());
    }
    if ( getOvCountId()  > 0 )
    {
        pointList.insert(getOvCountId());
    }
    if ( getUvCountId() > 0 )
    {
        pointList.insert(getUvCountId());
    }

    return *this;
}


void CtiCCTwoWayPoints::setDynamicData(Cti::RowReader& rdr)
{
    INT lastControl;
    INT condition = 0;
    string tempBoolString;

    //rdr["deviceid"] >> _paoid;
    rdr["recloseblocked"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setReCloseBlocked(tempBoolString=="y"?TRUE:FALSE);
    rdr["controlmode"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setControlMode(tempBoolString=="y"?TRUE:FALSE);
    rdr["autovoltcontrol"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setAutoVoltControl(tempBoolString=="y"?TRUE:FALSE);
    rdr["lastcontrol"] >> lastControl;
    rdr["condition"] >> condition;
    rdr["opfailedneutralcurrent"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setOpFailedNeutralCurrent(tempBoolString=="y"?TRUE:FALSE);
    rdr["neutralcurrentfault"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setNeutralCurrentFault(tempBoolString=="y"?TRUE:FALSE);
    rdr["badrelay"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setBadRelay(tempBoolString=="y"?TRUE:FALSE);
    rdr["dailymaxops"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDailyMaxOps(tempBoolString=="y"?TRUE:FALSE);
    rdr["voltagedeltaabnormal"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setVoltageDeltaAbnormal(tempBoolString=="y"?TRUE:FALSE);
    rdr["tempalarm"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setTempAlarm(tempBoolString=="y"?TRUE:FALSE);
    rdr["dstactive"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDSTActive(tempBoolString=="y"?TRUE:FALSE);
    rdr["neutrallockout"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setNeutralLockout(tempBoolString=="y"?TRUE:FALSE);
    rdr["ignoredindicator"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setIgnoredIndicator(tempBoolString=="y"?TRUE:FALSE);
    rdr["voltage"] >> _voltage;
    rdr["highvoltage"] >> _highVoltage;
    rdr["lowvoltage"] >> _lowVoltage;
    rdr["deltavoltage"] >> _deltaVoltage;
    rdr["analoginputone"] >> _analogInput1;
    rdr["temp"] >> _temperature;
    rdr["rssi"] >> _rssi;
    rdr["ignoredreason"] >> _ignoredReason;
    rdr["totalopcount"] >> _totalOpCount;
    rdr["uvopcount"] >> _uvCount;
    rdr["ovopcount"] >> _ovCount;
    rdr["ovuvcountresetdate"] >> _ovuvCountResetDate; //toADD
    rdr["uvsetpoint"] >> _uvSetPoint;
    rdr["ovsetpoint"] >> _ovSetPoint;
    rdr["ovuvtracktime"] >> _ovuvTrackTime;
    rdr["lastovuvdatetime"] >> _lastOvUvDateTime; //toAdd
    rdr["neutralcurrentsensor"] >> _neutralCurrentSensor;
    rdr["neutralcurrentalarmsetpoint"] >> _neutralCurrentAlarmSetPoint;
    rdr["ipaddress"]  >> _udpIpAddress;
    rdr["udpport"] >>  _udpPortNumber;

    _lastControlLocal = lastControl & 0x01;
    _lastControlRemote = lastControl & 0x02;
    _lastControlOvUv = lastControl & 0x04;
    _lastControlNeutralFault = lastControl & 0x08;
    _lastControlScheduled = lastControl & 0x10;
    _lastControlDigital = lastControl & 0x20;
    _lastControlAnalog = lastControl & 0x40;
    _lastControlTemperature = lastControl & 0x80;

    _uvCondition = condition & 0x01;
    _ovCondition = condition & 0x02;

    _insertDynamicDataFlag = FALSE;
    _dirty = false;


}
/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints* CtiCCTwoWayPoints::replicate() const
{
    return(new CtiCCTwoWayPoints(*this));
}


CtiCCTwoWayPoints& CtiCCTwoWayPoints::operator=(const CtiCCTwoWayPoints& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _capacitorBankStateId = right._capacitorBankStateId;
        _capacitorBankState = right._capacitorBankState;
        _reCloseBlockedId = right._reCloseBlockedId;
        _reCloseBlocked = right._reCloseBlocked;
        _controlModeId = right._controlModeId;
        _controlMode = right._controlMode;
        _autoVoltControlId = right._autoVoltControlId;
        _autoVoltControl = right._autoVoltControl;
        _lastControlLocalId = right._lastControlLocalId;
        _lastControlLocal = right._lastControlLocal;
        _lastControlRemoteId = right._lastControlRemoteId;
        _lastControlRemote = right._lastControlRemote;
        _lastControlOvUvId = right._lastControlOvUvId;
        _lastControlOvUv = right._lastControlOvUv;
        _lastControlNeutralFaultId = right._lastControlNeutralFaultId;
        _lastControlNeutralFault = right._lastControlNeutralFault;
        _lastControlScheduledId = right._lastControlScheduledId;
        _lastControlScheduled = right._lastControlScheduled;
        _lastControlDigitalId = right._lastControlDigitalId;
        _lastControlDigital = right._lastControlDigital;
        _lastControlAnalogId = right._lastControlAnalogId;
        _lastControlAnalog = right._lastControlAnalog;
        _lastControlTemperatureId = right._lastControlTemperatureId;
        _lastControlTemperature = right._lastControlTemperature;
        _ovConditionId = right._ovConditionId;
        _ovCondition = right._ovCondition;
        _uvConditionId = right._uvConditionId;
        _uvCondition = right._uvCondition;
        _opFailedNeutralCurrentId = right._opFailedNeutralCurrentId;
        _opFailedNeutralCurrent = right._opFailedNeutralCurrent;
        _neutralCurrentFaultId = right._neutralCurrentFaultId;
        _neutralCurrentFault = right._neutralCurrentFault;
        _badRelayId = right._badRelayId;
        _badRelay = right._badRelay;
        _dailyMaxOpsId = right._dailyMaxOpsId;
        _dailyMaxOps = right._dailyMaxOps;
        _voltageDeltaAbnormalId = right._voltageDeltaAbnormalId;
        _voltageDeltaAbnormal = right._voltageDeltaAbnormal;
        _tempAlarmId = right._tempAlarmId;
        _tempAlarm = right._tempAlarm;
        _DSTActiveId = right._DSTActiveId;
        _DSTActive = right._DSTActive;
        _neutralLockoutId = right._neutralLockoutId;
        _neutralLockout = right._neutralLockout;
        _ignoredIndicatorId = right._ignoredIndicatorId;
        _ignoredIndicator = right._ignoredIndicator;

        _voltageControlId = right._voltageControlId;
        _voltageControl = right._voltageControl;
        _voltageId = right._voltageId;
        _voltage = right._voltage;
        _highVoltageId = right._highVoltageId;
        _highVoltage = right._highVoltage;
        _lowVoltageId = right._lowVoltageId;
        _lowVoltage = right._lowVoltage;
        _deltaVoltageId = right._deltaVoltageId;
        _deltaVoltage = right._deltaVoltage;
        _analogInput1Id = right._analogInput1Id;
        _analogInput1 = right._analogInput1;
        _rssiId = right._rssiId;
        _rssi = right._rssi;
        _ignoredReasonId = right._ignoredReasonId;
        _ignoredReason = right._ignoredReason;

        _temperatureId = right._temperatureId;
        _temperature = right._temperature;

        _ovSetPointId = right._ovSetPointId;
        _ovSetPoint = right._ovSetPoint;
        _uvSetPointId = right._uvSetPointId;
        _uvSetPoint = right._uvSetPoint;
        _ovuvTrackTimeId = right._ovuvTrackTimeId;
        _ovuvTrackTime =right._ovuvTrackTime;
        _neutralCurrentSensorId = right._neutralCurrentSensorId;
        _neutralCurrentSensor = right._neutralCurrentSensor;
        _neutralCurrentAlarmSetPointId = right._neutralCurrentAlarmSetPointId;
        _neutralCurrentAlarmSetPoint = right. _neutralCurrentAlarmSetPoint;
        _timeTempSeasonOneId = right._timeTempSeasonOneId;
        _timeTempSeasonOne = right._timeTempSeasonOne;
        _timeTempSeasonTwoId = right._timeTempSeasonTwoId;
        _timeTempSeasonTwo = right._timeTempSeasonTwo;
        _varControlId = right._varControlId;
        _varControl = right._varControl;
        _udpIpAddressId = right._udpIpAddressId;
        _udpIpAddress = right._udpIpAddress;
        _udpPortNumberId = right._udpPortNumberId;
        _udpPortNumber = right._udpPortNumber;

        _totalOpCountId = right._totalOpCountId;
        _totalOpCount = right._totalOpCount;
        _ovCountId = right._ovCountId;
        _ovCount = right._ovCount;
        _uvCountId = right._uvCountId;
        _uvCount = right._uvCount;

        _lastControlReason = right._lastControlReason;


        _insertDynamicDataFlag = right._insertDynamicDataFlag;
        _dirty = right._dirty;
    }
    return *this;
}

int CtiCCTwoWayPoints::operator==(const CtiCCTwoWayPoints& right) const
{
    return getPAOId() == right.getPAOId();
}
int CtiCCTwoWayPoints::operator!=(const CtiCCTwoWayPoints& right) const
{
    return getPAOId() != right.getPAOId();
}


string CtiCCTwoWayPoints::getLastControlText() const
{
    string retVal = "Uninitialized";

    if (_lastControlLocal > 0 )
        retVal = "Local";
    else if (_lastControlRemote > 0 )
        retVal = "Remote";
    else if (_lastControlOvUv > 0 )
        retVal = "OvUv";
    else if (_lastControlNeutralFault > 0 )
        retVal = "NeutralFault";
    else if (_lastControlScheduled > 0 )
        retVal = "Schedule";
    else if (_lastControlDigital > 0 )
        retVal = "Digital";
    else if (_lastControlAnalog > 0 )
        retVal = "Analog";
    else if (_lastControlTemperature > 0 )
        retVal = "Temp";

    return retVal;
}


LONG CtiCCTwoWayPoints::getLastControl() const
{
    LONG retVal = 0;

    if (_lastControlRemote > 0 )
        retVal = CC_Remote;
    else if (_lastControlLocal > 0 )
        retVal = CC_Local;
    else if (_lastControlOvUv > 0 )
        retVal = CC_OvUv;
    else if (_lastControlNeutralFault > 0 )
        retVal = CC_NeutralFault;
    else if (_lastControlScheduled > 0 )
        retVal = CC_Scheduled;
    else if (_lastControlDigital > 0 )
        retVal = CC_Digital;
    else if (_lastControlAnalog > 0 )
        retVal = CC_Analog;
    else if (_lastControlTemperature > 0 )
        retVal = CC_Temperature;

    return retVal;
}

INT CtiCCTwoWayPoints::getLastControlReason() const
{
    return _lastControlReason;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlReason()
{

    if (_lastControlRemote > 0 )
        _lastControlReason = CC_Remote;
    else if (_lastControlLocal > 0 )
        _lastControlReason = CC_Local;
    else if (_lastControlOvUv > 0 )
        _lastControlReason = CC_OvUv;
    else if (_lastControlNeutralFault > 0 )
        _lastControlReason = CC_NeutralFault;
    else if (_lastControlScheduled > 0 )
        _lastControlReason = CC_Scheduled;
    else if (_lastControlDigital > 0 )
        _lastControlReason = CC_Digital;
    else if (_lastControlAnalog > 0 )
        _lastControlReason = CC_Analog;
    else if (_lastControlTemperature > 0 )
        _lastControlReason = CC_Temperature;

    return *this;
}


BOOL CtiCCTwoWayPoints::isLastControlReasonUpdated(LONG pointID,LONG reason )
{
    BOOL retVal = FALSE;
    switch (reason)
    {
        case CC_Remote:
        {
            if (pointID == getLastControlRemoteId() && getLastControlRemote() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Local:
        {
            if (pointID == getLastControlLocalId() && getLastControlLocal() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_OvUv:
        {
            if (pointID == getLastControlOvUvId() && getLastControlOvUv() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_NeutralFault:
        {
            if (pointID == getLastControlNeutralFaultId() && getLastControlNeutralFault() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Scheduled:
        {
            if (pointID == getLastControlScheduledId() && getLastControlScheduled() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Digital:
        {
            if (pointID == getLastControlDigitalId() && getLastControlDigital() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Analog:
        {
            if (pointID == getLastControlAnalogId() && getLastControlAnalog() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Temperature:
        {
            if (pointID == getLastControlTemperatureId() && getLastControlTemperature() == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        default:
            break;
    }

    return retVal;
}

