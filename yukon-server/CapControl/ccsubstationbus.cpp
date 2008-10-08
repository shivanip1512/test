/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSubstationBus.
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccsubstationbus.h"
#include "ccid.h"
#include "cccapbank.h"
#include "ccfeeder.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "mgr_holiday.h"
#include "mgr_paosched.h"
#include "utility.h"
#include "tbl_pt_alarm.h"

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;
extern ULONG _POINT_AGE;
extern ULONG _SCAN_WAIT_EXPIRE;
extern BOOL _ALLOW_PARALLEL_TRUING;
extern BOOL _LOG_MAPID_INFO;
extern BOOL _END_DAY_ON_TRIP;
extern ULONG _LIKEDAY_OVERRIDE_TIMEOUT;
extern bool _RATE_OF_CHANGE;
extern unsigned long _RATE_OF_CHANGE_DEPTH;
extern BOOL _TIME_OF_DAY_VAR_CONF;
extern LONG _MAXOPS_ALARM_CATID;
extern BOOL _RETRY_ADJUST_LAST_OP_TIME;


RWDEFINE_COLLECTABLE( CtiCCSubstationBus, CTICCSUBSTATIONBUS_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstationBus::CtiCCSubstationBus()
{
    regression = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionA = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionB = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionC = CtiRegression(_RATE_OF_CHANGE_DEPTH);
}

CtiCCSubstationBus::CtiCCSubstationBus(RWDBReader& rdr)
{
    restore(rdr);
    _operationStats.setPAOId(_paoid);
    _confirmationStats.setPAOId(_paoid);

    regression = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionA = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionB = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionC = CtiRegression(_RATE_OF_CHANGE_DEPTH);
}

CtiCCSubstationBus::CtiCCSubstationBus(const CtiCCSubstationBus& sub)
{
    operator=(sub);

}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationBus::~CtiCCSubstationBus()
{
    _pointIds.clear();
    try
    {   delete_container(_ccfeeders);
        _ccfeeders.clear();

        if (!_todControls.empty())
        {
            delete_container(_todControls);
            _todControls.clear();
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

CtiCCOperationStats& CtiCCSubstationBus::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats& CtiCCSubstationBus::getConfirmationStats()
{
    return _confirmationStats;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getDisableFlag() const
{
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (AreaId) of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getParentId() const
{
    return _parentId;
}

/*---------------------------------------------------------------------------
    getParentControlUnits

    Returns the ParentControlUnits of the substation bus
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getParentControlUnits() const
{
    return _parentControlUnits;
}

/*---------------------------------------------------------------------------
    getParentName

    Returns the ParentName of the substation bus
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getParentName() const
{
    return _parentName;
}


/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the DisplayOrder of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getDisplayOrder() const
{
    return _displayOrder;
}
/*---------------------------------------------------------------------------
    getIntegrateFlag

    Returns the IntegrateFlag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getIntegrateFlag() const
{
    return _integrateflag;
}
/*---------------------------------------------------------------------------
    getIntegratePeriod

    Returns the IntegratePeriod of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getIntegratePeriod() const
{
    return _integrateperiod;
}

/*---------------------------------------------------------------------------
    getLikeDayFallBack

    Returns the LikeDayFallBack of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getLikeDayFallBack() const
{
    return _likedayfallback;
}

/*---------------------------------------------------------------------------
    getIVControlTot

    Returns the Integrate Volt/Var Control total of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getIVControlTot() const
{
    return _iVControlTot;
}
/*---------------------------------------------------------------------------
    getIVCount

    Returns the Integrate Volt/Var Control count of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getIVCount() const
{
    return _iVCount;
}


/*---------------------------------------------------------------------------
    getIWControlTot

    Returns the Integrate Watt Control total of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getIWControlTot() const
{
    return _iWControlTot;
}

/*---------------------------------------------------------------------------
    getIWCount

    Returns the Integrate Watt Control count of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getIWCount() const
{
    return _iWCount;
}


/*---------------------------------------------------------------------------
    getIVControl

    Returns the Integrate Volt/Var Control of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getIVControl() const
{
    return _iVControl;
}
/*---------------------------------------------------------------------------
    getIWControl

    Returns the Integrate Watt Control of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getIWControl() const
{
    return _iWControl;
}

/*---------------------------------------------------------------------------
    get UsePhaseData flag

    Returns the usePhaseData flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getUsePhaseData() const
{
    return _usePhaseData;
}

/*---------------------------------------------------------------------------
    getPhaseBid

    Returns the getPhaseB pointid of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPhaseBId() const
{
    return _phaseBid;
}
/*---------------------------------------------------------------------------
    getPhaseCid

    Returns the getPhaseC pointid of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPhaseCId() const
{
    return _phaseCid;
}

/*---------------------------------------------------------------------------
    getTotalizedControlFlag

    Returns the TotalizedControlFlag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getTotalizedControlFlag() const
{
    return _totalizedControlFlag;
}

/*---------------------------------------------------------------------------
    getPhaseAValue

    Returns the getPhaseAValue VAR of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPhaseAValue() const
{
    return _phaseAvalue;
}

/*---------------------------------------------------------------------------
    getPhaseBValue

    Returns the getPhaseBValue VAR of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPhaseBValue() const
{
    return _phaseBvalue;
}

/*---------------------------------------------------------------------------
    getPhaseCValue

    Returns the getPhaseCValue VAR of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPhaseCValue() const
{
    return _phaseCvalue;
}
/*---------------------------------------------------------------------------
    getPhaseAValue

    Returns the getPhaseAValue VAR of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPhaseAValueBeforeControl() const
{
    return _phaseAvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getPhaseBValue

    Returns the getPhaseBValue VAR of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPhaseBValueBeforeControl() const
{
    return _phaseBvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getPhaseCValue

    Returns the getPhaseCValue VAR of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPhaseCValueBeforeControl() const
{
    return _phaseCvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getStrategyId

    Returns the StrategyId of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getStrategyId() const
{
    return _strategyId;
}

/*---------------------------------------------------------------------------
    getStrategyName

    Returns the StrategyName of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getStrategyName() const
{
    return _strategyName;
}
/*---------------------------------------------------------------------------
    getControlMethod

    Returns the control method of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getControlMethod() const
{
    return _controlmethod;
}

/*---------------------------------------------------------------------------
    getMaxDailyOperation

    Returns the max daily operations of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getMaxDailyOperation() const
{
    return _maxdailyoperation;
}

/*---------------------------------------------------------------------------
    getMaxOperationDisableFlag

    Returns the max operations disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getMaxOperationDisableFlag() const
{
    return _maxoperationdisableflag;
}

/*---------------------------------------------------------------------------
    getPeakLag

    Returns the peak lag level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakLag() const
{
    return _peaklag;
}

/*---------------------------------------------------------------------------
    getOffPeakLag

    Returns the off peak lag level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakLag() const
{
    return _offpklag;
}
/*---------------------------------------------------------------------------
    getPeakLead

    Returns the peak lead level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakLead() const
{
    return _peaklead;
}

/*---------------------------------------------------------------------------
    getOffPeakLead

    Returns the off peak lead level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakLead() const
{
    return _offpklead;
}


/*---------------------------------------------------------------------------
    getPeakVARLag

    Returns the peak VAR lag level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakVARLag() const
{
    return _peakVARlag;
}

/*---------------------------------------------------------------------------
    getOffPeakVARLag

    Returns the off peak VAR lag level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakVARLag() const
{
    return _offpkVARlag;
}
/*---------------------------------------------------------------------------
    getPeakVARLead

    Returns the peak VAR lead level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakVARLead() const
{
    return _peakVARlead;
}

/*---------------------------------------------------------------------------
    getOffPeakVARLead

    Returns the off peak VAR lead level of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakVARLead() const
{
    return _offpkVARlead;
}


/*---------------------------------------------------------------------------
    getOffPeakPFSetPoint

    Returns the off peak target Power Factor of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakPFSetPoint() const
{
    return _offpkpfsetpoint;
}


/*---------------------------------------------------------------------------
    getPeakPFSetPoint

    Returns the peak Target Power Factor of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakPFSetPoint() const
{
    return _peakpfsetpoint;
}


/*---------------------------------------------------------------------------
    getPeakStartTime

    Returns the peak start time of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPeakStartTime() const
{
    return _peakstarttime;
}

/*---------------------------------------------------------------------------
    getPeakStopTime

    Returns the peak stop time of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPeakStopTime() const
{
    return _peakstoptime;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVarLoadPointId() const
{
    return _currentvarloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentVarLoadPointValue() const
{
    if (_dualBusEnable && _switchOverStatus && !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
    {
        return _altSubControlValue;
    }
    return _currentvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentWattLoadPointId() const
{
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentWattLoadPointValue() const
{
    return _currentwattloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointId

    Returns the current volt load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVoltLoadPointId() const
{
    return _currentvoltloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointValue

    Returns the current volt load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentVoltLoadPointValue() const
{
    if (_dualBusEnable && _switchOverStatus && !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) )
    {
        return _altSubControlValue;
    }
    return _currentvoltloadpointvalue;
}

/*---------------------------------------------------------------------------
    getControlInterval

    Returns the control interval of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getControlInterval() const
{
    return _controlinterval;
}

/*---------------------------------------------------------------------------
    getMaxConfirmTime

    Returns the maximum confirm time of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getMaxConfirmTime() const
{
    return _maxconfirmtime;
}

/*---------------------------------------------------------------------------
    getMinConfirmPercent

    Returns the minimum confirm percent of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getMinConfirmPercent() const
{
    return _minconfirmpercent;
}

/*---------------------------------------------------------------------------
    getFailurePercent

    Returns the failure percent of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getFailurePercent() const
{
    return _failurepercent;
}

/*---------------------------------------------------------------------------
    getDaysOfWeek

    Returns the days of the week of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getDaysOfWeek() const
{
    return _daysofweek;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getMapLocationId() const
{
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getControlUnits

    Returns the control unit of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getControlUnits() const
{
    return _controlunits;
}


/*---------------------------------------------------------------------------
    getSolution

    Returns the solution of the substation
---------------------------------------------------------------------------*/
const string& CtiCCSubstationBus::getSolution() const
{
    return _solution;
}

/*---------------------------------------------------------------------------
    getTargetVarValue

    Returns the target var value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getTargetVarValue() const
{
    return _targetvarvalue;
}

/*---------------------------------------------------------------------------
    getControlDelayTime

    Returns the ControlDelayTime of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getControlDelayTime() const
{
    return _controldelaytime;
}

/*---------------------------------------------------------------------------
    getControlSendRetries

    Returns the ControlSendRetries of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getControlSendRetries() const
{
    if (_SEND_TRIES > 1 && _SEND_TRIES > _controlsendretries)
    {
        return _SEND_TRIES-1;
    }

    return _controlsendretries;
}

LONG CtiCCSubstationBus::getLastFeederControlledSendRetries() const
{
    LONG sendRetries = 0;

    try
    {
        if (getLastFeederControlledPosition() > 0)
        {
            CtiCCFeederPtr feed = (CtiCCFeederPtr)_ccfeeders[getLastFeederControlledPosition()];

            if (feed->getPAOId() != getLastFeederControlledPAOId())
            {
                for(LONG i=0;i<_ccfeeders.size();i++)
                {
                    feed = (CtiCCFeeder*)_ccfeeders[i];
                    if (feed->getPAOId() == getLastFeederControlledPAOId())
                    {
                        //setLastFeederControlledPosition(i);
                        break;
                    }
                    feed = NULL;
                }

            }
            if (feed != NULL && feed->getStrategyId() > 0)
            {
                sendRetries = feed->getControlSendRetries();
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return sendRetries;
}

/*---------------------------------------------------------------------------
    getDecimalPlaces

    Returns the decimal places of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getDecimalPlaces() const
{
    return _decimalplaces;
}

/*---------------------------------------------------------------------------
    getNextCheckTime

    Returns the next check time of the substation
---------------------------------------------------------------------------*/
const CtiTime& CtiCCSubstationBus::getNextCheckTime() const
{
    return _nextchecktime;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getNewPointDataReceivedFlag() const
{
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getBusUpdatedFlag

    Returns the substation updated flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getBusUpdatedFlag() const
{
    return _busupdatedflag;
}

/*---------------------------------------------------------------------------
    getLastCurrentVarPointUpdateTime

    Returns the last current var point update time of the substation
---------------------------------------------------------------------------*/
const CtiTime& CtiCCSubstationBus::getLastCurrentVarPointUpdateTime() const
{
    return _lastcurrentvarpointupdatetime;
}

/*---------------------------------------------------------------------------
    getLastWattPointTime

    Returns the last current watt point update time of the substation
---------------------------------------------------------------------------*/
const CtiTime& CtiCCSubstationBus::getLastWattPointTime() const
{
    return _lastWattPointTime;
}

/*---------------------------------------------------------------------------
    getLastVoltPointTime

    Returns the last current var point update time of the substation
---------------------------------------------------------------------------*/
const CtiTime& CtiCCSubstationBus::getLastVoltPointTime() const
{
    return _lastVoltPointTime;
}


/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointId

    Returns the estimated var load point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getEstimatedVarLoadPointId() const
{
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getEstimatedVarLoadPointValue() const
{
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getDailyOperationsAnalogPointId() const
{
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getPowerFactorPointId

    Returns the power factor point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getPowerFactorPointId() const
{
    return _powerfactorpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorPointId

    Returns the estimated power factor point id of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getEstimatedPowerFactorPointId() const
{
    return _estimatedpowerfactorpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the flag that represents if the substation is in peak time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getPeakTimeFlag() const
{
    return _peaktimeflag;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the substation has been recently controlled
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getRecentlyControlledFlag() const
{
    return _recentlycontrolledflag;
}

/*---------------------------------------------------------------------------
    getLastOperationTime

    Returns the last operation time of the substation
---------------------------------------------------------------------------*/
const CtiTime& CtiCCSubstationBus::getLastOperationTime() const
{
    return _lastoperationtime;
}

/*---------------------------------------------------------------------------
    getVarValueBeforeControl

    Returns the var value before control of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getVarValueBeforeControl() const
{
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPAOId

    Returns the PAO id of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getLastFeederControlledPAOId() const
{
    return _lastfeedercontrolledpaoid;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPosition

    Returns the position of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getLastFeederControlledPosition() const
{
    return _lastfeedercontrolledposition;
}

/*---------------------------------------------------------------------------
    getPowerFactorValue

    Returns the PowerFactorValue of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPowerFactorValue() const
{
    return _powerfactorvalue;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorValue

    Returns the EstimatedPowerFactorValue of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getEstimatedPowerFactorValue() const
{
    return _estimatedpowerfactorvalue;
}

/*---------------------------------------------------------------------------
    getKVARSolution

    Returns the KVARSolution of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getKVARSolution() const
{
    return _kvarsolution;
}

/*---------------------------------------------------------------------------
    getCurrentVarPointQuality

    Returns the CurrentVarPointQuality of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVarPointQuality() const
{
    return _currentvarpointquality;
}
/*---------------------------------------------------------------------------
    getCurrentWattPointQuality

    Returns the CurrentWattPointQuality of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentWattPointQuality() const
{
    return _currentwattpointquality;
}
/*---------------------------------------------------------------------------
    getCurrentVoltPointQuality

    Returns the CurrentVoltPointQuality of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getCurrentVoltPointQuality() const
{
    return _currentvoltpointquality;
}

/*---------------------------------------------------------------------------
    getWaiveControlFlag

    Returns the WaiveControlFlag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getWaiveControlFlag() const
{
    return _waivecontrolflag;
}

/*---------------------------------------------------------------------------
    getVerificationFlag

    Returns the WaiveControlFlag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getVerificationFlag() const
{
    return _verificationFlag;
}

BOOL CtiCCSubstationBus::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

BOOL CtiCCSubstationBus::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

BOOL CtiCCSubstationBus::getOverlappingVerificationFlag() const
{
    return _overlappingSchedulesVerificationFlag;
}

BOOL CtiCCSubstationBus::getPreOperationMonitorPointScanFlag() const
{
    return _preOperationMonitorPointScanFlag;
}
BOOL CtiCCSubstationBus::getOperationSentWaitFlag() const
{
    return _postOperationMonitorPointScanFlag;
}
BOOL CtiCCSubstationBus::getPostOperationMonitorPointScanFlag() const
{
    return _postOperationMonitorPointScanFlag;
}

BOOL CtiCCSubstationBus::getWaitForReCloseDelayFlag() const
{
    return _waitForReCloseDelayFlag;
}

BOOL CtiCCSubstationBus::getWaitToFinishRegularControlFlag() const
{
    return _waitToFinishRegularControlFlag;
}
BOOL CtiCCSubstationBus::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}
BOOL CtiCCSubstationBus::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

BOOL CtiCCSubstationBus::getCorrectionNeededNoBankAvailFlag() const
{
    return _correctionNeededNoBankAvailFlag;
}
BOOL CtiCCSubstationBus::getLikeDayControlFlag() const
{
    return _likeDayControlFlag;
}
BOOL CtiCCSubstationBus::getVoltReductionFlag() const
{
    return _voltReductionFlag;
}
BOOL CtiCCSubstationBus::getSendMoreTimeControlledCommandsFlag() const
{
    return _sendMoreTimeControlledCommandsFlag;
}


LONG CtiCCSubstationBus::getVoltReductionControlId() const
{
    return _voltReductionControlId;
}


LONG CtiCCSubstationBus::getCurrentVerificationFeederId() const
{
    return _currentVerificationFeederId;
}
LONG CtiCCSubstationBus::getCurrentVerificationCapBankId() const
{
    return _currentVerificationCapBankId;
}
LONG CtiCCSubstationBus::getCurrentVerificationCapBankOrigState() const
{
    return _currentCapBankToVerifyAssumedOrigState;
}
LONG CtiCCSubstationBus::getAltDualSubId() const
{
    return _altDualSubId;
}
DOUBLE CtiCCSubstationBus::getAltSubControlValue() const
{
    return _altSubControlValue;
}

void CtiCCSubstationBus::getAllAltSubValues(DOUBLE &volt, DOUBLE &var, DOUBLE &watt)
{
    if (_dualBusEnable && _switchOverStatus)
    {
        volt = _altSubVoltVal;
        var  = _altSubVarVal;
        watt = _altSubWattVal;
    }
    else
    {
        volt = _currentvoltloadpointvalue;
        var  = _currentvarloadpointvalue;
        watt = _currentwattloadpointvalue;
    }
    return;
}
LONG CtiCCSubstationBus::getSwitchOverPointId() const
{
    return _switchOverPointId;
}
BOOL CtiCCSubstationBus::getSwitchOverStatus() const
{
    return _switchOverStatus;
}
BOOL CtiCCSubstationBus::getDualBusEnable() const
{
    return _dualBusEnable;
}

LONG CtiCCSubstationBus::getEventSequence() const
{
    return _eventSeq;
}
BOOL CtiCCSubstationBus::getReEnableBusFlag() const
{
    return _reEnableBusFlag;
}

BOOL CtiCCSubstationBus::getMultiMonitorFlag() const
{
    return _multiMonitorFlag;
}


/*---------------------------------------------------------------------------
    getCCFeeders

    Returns the list of feeders in the substation
---------------------------------------------------------------------------*/
CtiFeeder_vec& CtiCCSubstationBus::getCCFeeders()
{
    return _ccfeeders;
}

/*---------------------------------------------------------------------------
    getCCFeeders

    Returns the list of feeders in the substation
---------------------------------------------------------------------------*/
CtiTODC_SVector& CtiCCSubstationBus::getTODControls()
{
    return _todControls;
}

const CtiRegression& CtiCCSubstationBus::getRegression()
{
    return regression;
}
const CtiRegression& CtiCCSubstationBus::getRegressionA()
{
    return regressionA;
}
const CtiRegression& CtiCCSubstationBus::getRegressionB()
{
    return regressionB;
}
const CtiRegression& CtiCCSubstationBus::getRegressionC()
{
    return regressionC;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOCategory(const string& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOClass(const string& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOName(const string& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOType(const string& _type)
{
    _paotype = _type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAODescription(const string& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDisableFlag(BOOL disable)
{
    _disableflag = disable;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentId

    Sets the parentID (AreaID) of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setParentId(LONG parentId)
{
    _parentId = parentId;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentControlUnits

    Sets the ParentControlUnits in the substation bus
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setParentControlUnits(const string& parentControlUnits)
{
    if (_parentControlUnits != parentControlUnits)
    {
        _dirty = TRUE;
    }
    _parentControlUnits = parentControlUnits;
    return *this;
}

/*---------------------------------------------------------------------------
    setParentName

    Sets the ParentName in the substation bus
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setParentName(const string& parentName)
{
    if (_parentName != parentName)
    {
        _dirty = TRUE;
    }
    _parentName = parentName;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the DisplayOrder of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDisplayOrder(LONG displayOrder)
{
    _displayOrder = displayOrder;
    return *this;
}
/*---------------------------------------------------------------------------
    setIntegrateFlag

    Sets the IntegrateFlag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIntegrateFlag(BOOL flag)
{
    _integrateflag = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setIntegratePeriod

    Sets the IntegratePeriod of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIntegratePeriod(LONG period)
{
    _integrateperiod = period;
    return *this;
}
/*---------------------------------------------------------------------------
    LikeDayFallBack

    Sets the LikeDayFallBack of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLikeDayFallBack(BOOL flag)
{
    _likedayfallback = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVControlTot

    Sets the Integrated Volt or var Control Total of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIVControlTot(DOUBLE value)
{
    if ( _iVControlTot != value)
    {
        _dirty = TRUE;
    }

    _iVControlTot = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVCoont

    Sets the Integrated Volt or var Control Count of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIVCount(LONG value)
{
    if (_iVCount != value)
    {
        _dirty = TRUE;
    }
    _iVCount = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIWControlTot

    Sets the Integrated Watt Control Total of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIWControlTot(DOUBLE value)
{
    if (_iWControlTot != value)
    {
        _dirty = TRUE;
    }

    _iWControlTot = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIWCoont

    Sets the Integrated Watt Control Count of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIWCount(LONG value)
{
    _iWCount = value;
    return *this;
}

/*---------------------------------------------------------------------------
    setIVControl

    Sets the Integrated Volt/Var Control of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIVControl(DOUBLE value)
{
    _iVControl = value;
    return *this;
}

/*---------------------------------------------------------------------------
    setIWControl

    Sets the Integrated Watt Control of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setIWControl(DOUBLE value)
{
    _iWControl = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setUsePhaseData flag

    Sets the UsePhaseData flag  of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setUsePhaseData(BOOL flag)
{
    _usePhaseData = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setPhaseBid

    Sets the PhaseB pointid  of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseBId(LONG pointid)
{
    _phaseBid = pointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPhaseCid

    Sets the PhaseC pointid  of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseCId(LONG pointid)
{
    _phaseCid = pointid;
    return *this;
}
/*---------------------------------------------------------------------------
    setTotalizedControlFlag

    Sets the TotalizedControlFlag  of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setTotalizedControlFlag(BOOL flag)
{
    _totalizedControlFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setPhaseAValue

    Sets the setPhaseAValue Var of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseAValue(DOUBLE value, CtiTime timestamp)
{
    if (_phaseAvalue != value)
    {
        _dirty = TRUE;
    }
    _phaseAvalue = value;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionA.appendWithoutFill(std::make_pair(timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " RATE OF CHANGE: Adding to regressionA  " << timestamp.seconds() << "  and " << value << endl;
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setPhaseBValue

    Sets the setPhaseBValue Var of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseBValue(DOUBLE value, CtiTime timestamp)
{
    if (_phaseBvalue != value)
    {
        _dirty = TRUE;
    }
    _phaseBvalue = value;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionB.appendWithoutFill(std::make_pair(timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " RATE OF CHANGE: Adding to regressionB  " << timestamp.seconds() << "  and " << value << endl;
        }
    }
    return *this;
}



/*---------------------------------------------------------------------------
    setPhaseCValue

    Sets the setPhaseCValue Var of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseCValue(DOUBLE value, CtiTime timestamp)
{
    if (_phaseCvalue != value)
    {
        _dirty = TRUE;
    }
    _phaseCvalue = value;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionC.appendWithoutFill(std::make_pair(timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " RATE OF CHANGE: Adding to regressionC  " << timestamp.seconds() << "  and " << value << endl;
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setPhaseAValue

    Sets the setPhaseAValue Var of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseAValueBeforeControl(DOUBLE value)
{
    _phaseAvalueBeforeControl = value;
    return *this;
}

/*---------------------------------------------------------------------------
    setPhaseBValue

    Sets the setPhaseBValue Var of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseBValueBeforeControl(DOUBLE value)
{
    _phaseBvalueBeforeControl = value;
    return *this;
}



/*---------------------------------------------------------------------------
    setPhaseCValue

    Sets the setPhaseCValue Var of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPhaseCValueBeforeControl(DOUBLE value)
{
    _phaseCvalueBeforeControl = value;
    return *this;
}



/*---------------------------------------------------------------------------
    setStrategyId

    Sets the strategy id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setStrategyId(LONG strategyId)
{
    _strategyId = strategyId;
    return *this;
}

/*---------------------------------------------------------------------------
    setStrategyName

    Sets the strategy name of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setStrategyName(const string& strategyName)
{
    _strategyName = strategyName;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlMethod

    Sets the control method of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlMethod(const string& method)
{
    _controlmethod = method;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the max daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxDailyOperation(LONG max)
{
    _maxdailyoperation = max;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the max operations disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxOperationDisableFlag(BOOL maxopdisable)
{
    _maxoperationdisableflag = maxopdisable;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakLag

    Sets the peak lag level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakLag(DOUBLE peak)
{
    _peaklag = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakLag

    Sets the off peak lag level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakLag(DOUBLE offpeak)
{
    _offpklag = offpeak;
    return *this;
}
/*---------------------------------------------------------------------------
    setPeakLead

    Sets the peak lead level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakLead(DOUBLE peak)
{
    _peaklead = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakLead

    Sets the off peak lead level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakLead(DOUBLE offpeak)
{
    _offpklead = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakVARLag

    Sets the peak VAR lag level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakVARLag(DOUBLE peak)
{
    _peakVARlag = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakVARLag

    Sets the off peak VAR lag level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakVARLag(DOUBLE offpeak)
{
    _offpkVARlag = offpeak;
    return *this;
}
/*---------------------------------------------------------------------------
    setPeakVARLead

    Sets the peak VAR lead level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakVARLead(DOUBLE peak)
{
    _peakVARlead = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakVARLead

    Sets the off peak VAR lead level of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakVARLead(DOUBLE offpeak)
{
    _offpkVARlead = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakPFSetPoint

    Sets the peak target Power Factor of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakPFSetPoint(DOUBLE peak)
{
    _peakpfsetpoint = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakPFSetPoint

    Sets the off peak target Power Factor of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakPFSetPoint(DOUBLE offpeak)
{
    _offpkpfsetpoint = offpeak;
    return *this;
}


/*---------------------------------------------------------------------------
    setPeakStartTime

    Sets the peak start time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakStartTime(LONG starttime)
{
    _peakstarttime = starttime;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStopTime

    Sets the peak stop time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakStopTime(LONG stoptime)
{
    _peakstoptime = stoptime;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarLoadPointId(LONG currentvarid)
{
    _currentvarloadpointid = currentvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarLoadPointValue(DOUBLE value, CtiTime timestamp)
{
    if( _currentvarloadpointvalue != value )
    {
        _dirty = TRUE;
    }
    _currentvarloadpointvalue = value;
    if(_RATE_OF_CHANGE && !getRecentlyControlledFlag())
    {
        regression.appendWithoutFill(std::make_pair(timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " RATE OF CHANGE: Adding to regression  " << timestamp.seconds() << "  and " << value << endl;
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattLoadPointId(LONG currentwattid)
{
    _currentwattloadpointid = currentwattid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattLoadPointValue(DOUBLE currentwattval)
{
    if( _currentwattloadpointvalue != currentwattval )
    {
        _dirty = TRUE;
    }
    _currentwattloadpointvalue = currentwattval;
    return *this;
}
/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointId

    Sets the current volt load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVoltLoadPointId(LONG currentvoltid)
{
    _currentvoltloadpointid = currentvoltid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointValue

    Sets the current volt load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVoltLoadPointValue(DOUBLE currentvoltval)
{
    if( _currentvoltloadpointvalue != currentvoltval )
    {
        _dirty = TRUE;
    }
    _currentvoltloadpointvalue = currentvoltval;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlInterval

    Sets the control interval of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlInterval(LONG interval)
{
    _controlinterval = interval;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxConfirmTime

    Sets the MaxConfirmTime of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxConfirmTime(LONG confirm)
{
    _maxconfirmtime = confirm;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinConfirmPercent

    Sets the min confirm percent of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMinConfirmPercent(LONG confirm)
{
    _minconfirmpercent = confirm;
    return *this;
}

/*---------------------------------------------------------------------------
    setFailurePercent

    Sets the failure percent of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setFailurePercent(LONG failure)
{
    _failurepercent = failure;
    return *this;
}

/*---------------------------------------------------------------------------
    setDaysOfWeek

    Sets the days of the week of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDaysOfWeek(const string& days)
{
    _daysofweek = days;
    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMapLocationId(const string& maplocation)
{
    _maplocationid = maplocation;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlUnits

    Sets the ControlUnits of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlUnits(const string& units)
{
    _controlunits = units;
    return *this;
}


/*---------------------------------------------------------------------------
    setSolution

    Sets the Solution of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setSolution(const string& text)
{
    _solution = text;
    return *this;
}
/*---------------------------------------------------------------------------
    setTargetVarValue

    Sets the target var value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setTargetVarValue(DOUBLE value)
{
    _targetvarvalue = value;
    return *this;
}


/*---------------------------------------------------------------------------
    setControlDelayTime

    Sets the ControlDelayTime of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlDelayTime(LONG delay)
{
    _controldelaytime = delay;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlSendRetries

    Sets the ControlSendRetries of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlSendRetries(LONG retries)
{
    _controlsendretries = retries;
    return *this;
}

/*---------------------------------------------------------------------------
    setDecimalPlaces

    Sets the decimal places of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDecimalPlaces(LONG places)
{
    _decimalplaces = places;
    return *this;
}

LONG CtiCCSubstationBus::getNextTODStartTime()
{
    LONG retVal =  CtiTime().seconds() - CtiTime(0, 0, 0).seconds();
    retVal += 1; //add 1 sec for delay.
    BOOL found = FALSE;

    for (LONG i = 0; i < _todControls.size(); i++)
    {
        if (((CtiTimeOfDayController*)_todControls[i])->_secsFromMidnight >= retVal)
        {
            found = TRUE;
            retVal = ((CtiTimeOfDayController*)_todControls[i])->_secsFromMidnight;
            _percentToClose = ((CtiTimeOfDayController*)_todControls[i])->_percentToClose;
            break;
        }
    }
    if (!found)
    {
        retVal = ((CtiTimeOfDayController*)_todControls[0])->_secsFromMidnight;
        _percentToClose = ((CtiTimeOfDayController*)_todControls[0])->_percentToClose;
    }
    if (_CC_DEBUG & CC_DEBUG_TIMEOFDAY )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " - NextTODStartTime:  " <<CtiTime(CtiTime(0, 0, 0).seconds() + retVal) << " for SubBus: "<<getPAOName()
        << " scheduling " << _percentToClose <<"% of banks to Close."<< endl;
    }
    return retVal;
}
/*---------------------------------------------------------------------------
    figureNextCheckTime

    Figures out when the substation should be checked again according to the
    control interval.
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::figureNextCheckTime()
{
    CtiTime currenttime = CtiTime();
    if (getLikeDayControlFlag())
    {
        _nextchecktime = CtiTime(currenttime.seconds() + 60);
    }
    else if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod))
    {
        if (getSendMoreTimeControlledCommandsFlag())
        {
            LONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_controlinterval))+_controlinterval;
            _nextchecktime = CtiTime(CtiTime(tempsum));
        }
        else
        {
            _nextchecktime = CtiTime(CtiTime(0, 0, 0).seconds() + getNextTODStartTime());
            if (_nextchecktime == CtiTime(0,0,0) && _nextchecktime.seconds() <= currenttime.seconds())
            {
                _nextchecktime = _nextchecktime.addDays(1, _nextchecktime.isDST());
            }
        }

        if (_CC_DEBUG & CC_DEBUG_TIMEOFDAY )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - TOD: NextCheckTime:  " <<CtiTime(_nextchecktime)<<" for SubBus: "<<getPAOName() << endl;
        }
    }
    else if( _controlinterval != 0 )
    {
        if (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::MultiVoltControlUnits))
        {
            if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod))
            {
                for (LONG i = 0; i < _ccfeeders.size(); i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if (currentFeeder->getPostOperationMonitorPointScanFlag())
                    {
                        LONG tempsum = currenttime.seconds() + (_SCAN_WAIT_EXPIRE * 60);
                        _nextchecktime = CtiTime(CtiTime(tempsum));
                        break;
                    }
                    else
                    {
                        LONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_controlinterval))+_controlinterval;
                        _nextchecktime = CtiTime(CtiTime(tempsum));
                    }

                }
            }
            else
            {
                if (getPostOperationMonitorPointScanFlag() || getPreOperationMonitorPointScanFlag())
                {
                    LONG tempsum = currenttime.seconds() + (_SCAN_WAIT_EXPIRE * 60);
                    _nextchecktime = CtiTime(CtiTime(tempsum));
                }
                else
                {
                    LONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_controlinterval))+_controlinterval;
                    _nextchecktime = CtiTime(CtiTime(tempsum));
                }
            }

        }
        else
        {
            LONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_controlinterval))+_controlinterval;
            _nextchecktime = CtiTime(CtiTime(tempsum));
        }
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    else
    {
        _nextchecktime = currenttime;
        if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod))
        {
            for (LONG i = 0; i < _ccfeeders.size(); i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if (currentFeeder->getStrategyId() > 0)
                {
                    LONG tempsum1 = (currenttime.seconds() - ( currenttime.seconds()%currentFeeder->getControlInterval() ) + currentFeeder->getControlInterval() );
                    _nextchecktime = CtiTime(CtiTime(tempsum1));
                    break;
                }
            }

        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setNewPointDataReceivedFlag(BOOL newpointdatareceived)
{
    if( _newpointdatareceivedflag != newpointdatareceived )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _newpointdatareceivedflag = newpointdatareceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setBusUpdatedFlag

    Sets the substation updated flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setBusUpdatedFlag(BOOL busupdated)
{
    /*if( _busupdatedflag != busupdated )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        _dirty = TRUE;
    }*/
    _busupdatedflag = busupdated;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate)
{
    if( _lastcurrentvarpointupdatetime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastcurrentvarpointupdatetime = lastpointupdate;
    return *this;
}
/*---------------------------------------------------------------------------
    setLastWattPointTime

    Sets the last current Watt point update time of the subbus
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastWattPointTime(const CtiTime& lastpointupdate)
{
    if( _lastWattPointTime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastWattPointTime = lastpointupdate;
    return *this;
}
/*---------------------------------------------------------------------------
    setLastVoltPointTime

    Sets the last current Volt point update time of the subbus
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastVoltPointTime(const CtiTime& lastpointupdate)
{
    if( _lastVoltPointTime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastVoltPointTime = lastpointupdate;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedVarLoadPointId(LONG estimatedvarid)
{
    _estimatedvarloadpointid = estimatedvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedVarLoadPointValue(DOUBLE estimatedvarval)
{
    if( _estimatedvarloadpointvalue != estimatedvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _estimatedvarloadpointvalue = estimatedvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setDailyOperationsAnalogPointId

    Sets the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDailyOperationsAnalogPointId(LONG opanalogpointid)
{
    _dailyoperationsanalogpointid = opanalogpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorPointId

    Sets the power factor point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPowerFactorPointId(LONG pfpointid)
{
    _powerfactorpointid = pfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorPointId

    Sets the estimated power factor point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedPowerFactorPointId(LONG epfpointid)
{
    _estimatedpowerfactorpointid = epfpointid;
    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentDailyOperationsAndSendMsg(LONG operations, CtiMultiMsg_vec& pointChanges)
{
    if( _currentdailyoperations != operations )
    {
        if( getDailyOperationsAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(getDailyOperationsAnalogPointId(),operations,NormalQuality,AnalogPointType));
        }
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentdailyoperations = operations;

    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentDailyOperations(LONG operations)
{
    if( _currentdailyoperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentdailyoperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakTimeFlag

    Sets the flag if the substation is in peak time
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakTimeFlag(LONG peaktime)
{
    if( _peaktimeflag != peaktime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _peaktimeflag = peaktime;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setRecentlyControlledFlag(BOOL recentlycontrolled)
{
    if( _recentlycontrolledflag != recentlycontrolled )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _recentlycontrolledflag = recentlycontrolled;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::checkAndUpdateRecentlyControlledFlag()
{
    int numberOfCapBanksPending = 0;
    BOOL pendingBanks = FALSE;

    for(LONG i=0;i<_ccfeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
        CtiCCCapBank* currentCapBank = NULL;

        for(int j=0;j<ccCapBanks.size();j++)
        {
           currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
           if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
               currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
           {
               pendingBanks = TRUE;
               currentFeeder->setRecentlyControlledFlag(TRUE);
               setRecentlyControlledFlag(TRUE);
               numberOfCapBanksPending += 1;
               j = ccCapBanks.size();
           }
        }
        if (!pendingBanks)
        {
            currentFeeder->setRecentlyControlledFlag(FALSE);
            currentFeeder->setRetryIndex(0);
            pendingBanks = FALSE;
        }
    }
    if (numberOfCapBanksPending == 0)
    {
        setRecentlyControlledFlag(FALSE);
    }

    return *this;
}


/*---------------------------------------------------------------------------
    setLastOperationTime

    Sets the last operation time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastOperationTime(const CtiTime& lastoperation)
{
    if( _lastoperationtime != lastoperation )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastoperationtime = lastoperation;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setLastVerificationCheck(const CtiTime& checkTime)
{
    _lastVerificationCheck = checkTime;
    return *this;
}

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setVarValueBeforeControl(DOUBLE oldvarval)
{
    if( _varvaluebeforecontrol != oldvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _varvaluebeforecontrol = oldvarval;

    setPhaseAValueBeforeControl(getPhaseAValue());
    setPhaseBValueBeforeControl(getPhaseBValue());
    setPhaseCValueBeforeControl(getPhaseCValue());

    return *this;
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPAOId

    Sets the pao id of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastFeederControlledPAOId(LONG lastfeederpao)
{
    if( _lastfeedercontrolledpaoid != lastfeederpao )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastfeedercontrolledpaoid = lastfeederpao;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPosition

    Sets the position of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastFeederControlledPosition(LONG lastfeederposition)
{
    if( _lastfeedercontrolledposition != lastfeederposition )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastfeedercontrolledposition = lastfeederposition;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorValue

    Sets the PowerFactorValue in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPowerFactorValue(DOUBLE pfval)
{
    if( _powerfactorvalue != pfval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _powerfactorvalue = pfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setKVARSolution

    Sets the KVARSolution in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setKVARSolution(DOUBLE solution)
{
    if( _kvarsolution != solution )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _kvarsolution = solution;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorValue

    Sets the EstimatedPowerFactorValue in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedPowerFactorValue(DOUBLE epfval)
{
    if( _estimatedpowerfactorvalue != epfval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _estimatedpowerfactorvalue = epfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarPointQuality

    Sets the CurrentVarPointQuality in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarPointQuality(LONG cvpq)
{
    if( _currentvarpointquality != cvpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvarpointquality = cvpq;
    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentWattPointQuality

    Sets the CurrentWattPointQuality in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattPointQuality(LONG cwpq)
{
    if( _currentwattpointquality != cwpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentwattpointquality = cwpq;
    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentVoltPointQuality

    Sets the CurrentVoltPointQuality in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVoltPointQuality(LONG cvpq)
{
    if( _currentvoltpointquality != cvpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvoltpointquality = cvpq;
    return *this;
}

/*---------------------------------------------------------------------------
    setWaiveControlFlag

    Sets the WaiveControlFlag in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setWaiveControlFlag(BOOL waive)
{
    if( _waivecontrolflag != waive )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _waivecontrolflag = waive;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setAltDualSubId(LONG altDualSubId)
{
    if (_altDualSubId != altDualSubId)
    {
        _dirty = TRUE;
    }
    _altDualSubId = altDualSubId;
    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setAltSubControlValue(DOUBLE controlValue)
{
    if (_altSubControlValue != controlValue)
    {
        _dirty = TRUE;
    }
    _altSubControlValue = controlValue;
    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setAllAltSubValues(DOUBLE volt, DOUBLE var, DOUBLE watt)
{
    _altSubVoltVal = volt;
    _altSubVarVal = var;
    _altSubWattVal = watt;

    return *this;
}


CtiCCSubstationBus& CtiCCSubstationBus::setSwitchOverPointId(LONG pointId)
{
    if (_switchOverPointId != pointId)
    {
        _dirty = TRUE;
    }
    _switchOverPointId = pointId;
    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setSwitchOverStatus(BOOL status)
{
    if (_switchOverStatus != status)
    {
        _dirty = TRUE;
    }
    _switchOverStatus = status;
    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setDualBusEnable(BOOL flag)
{
    if (_dualBusEnable != flag)
    {
        _dirty = TRUE;
    }
    _dualBusEnable = flag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setEventSequence(LONG eventSeq)
{
    if (_eventSeq != eventSeq)
    {
        _dirty = TRUE;
    }
    _eventSeq = eventSeq;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setReEnableBusFlag(BOOL flag)
{
    if (_reEnableBusFlag != flag)
    {
        _dirty = TRUE;
    }
    _reEnableBusFlag = flag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setMultiMonitorFlag(BOOL flag)
{
    if (_multiMonitorFlag != flag)
    {
        _dirty = TRUE;
    }
    _multiMonitorFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    figureEstimatedVarLoadPointValue

    Figures out the estimated var point value according to the states
    of the individual cap banks in each of the feeders
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::figureEstimatedVarLoadPointValue()
{
    if( getCurrentVarLoadPointId() > 0 )
    {
        DOUBLE tempValue;
        if( getRecentlyControlledFlag() )
            tempValue = getVarValueBeforeControl();
        else
            tempValue = getCurrentVarLoadPointValue();

        for(LONG i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                {
                    tempValue = tempValue + currentCapBank->getBankSize();
                }
            }
            //if( currentFeeder->getRecentlyControlledFlag() )
            //{
            currentFeeder->figureEstimatedVarLoadPointValue();
            //}
        }

        setEstimatedVarLoadPointValue(tempValue);
    }
    else
    {
        setEstimatedVarLoadPointValue(0.0);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededControl


---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    BOOL keepGoing = TRUE;

    //have we went past the max daily ops
    if( getMaxDailyOperation() > 0 &&
        (_currentdailyoperations == getMaxDailyOperation()  ||
         (!getMaxDailyOpsHitFlag() && _currentdailyoperations > getMaxDailyOperation()) ) )//only send once
    {
        string text = string("Substation Bus Exceeded Max Daily Operations");
        string additional = string("Substation Bus: ");
        additional += getPAOName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPAODescription();
            additional += ")";
        }
        if (getDailyOperationsAnalogPointId() > 0 && !getMaxDailyOpsHitFlag())
        {
            CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                                TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
            pSig->setCondition(CtiTablePointAlarming::highReasonability);
            CtiCapController::getInstance()->sendMessageToDispatch(pSig);
        }
        setSolution(text);
        setMaxDailyOpsHitFlag(TRUE);

    }
    if( getMaxOperationDisableFlag() && getMaxDailyOpsHitFlag() )
    {
        if ( !_END_DAY_ON_TRIP )
        {
            setDisableFlag(TRUE);
            setBusUpdatedFlag(TRUE);
            CtiCCSubstationBusStore::getInstance()->UpdateBusDisableFlagInDB(this);
            setSolution("  Sub Disabled. Automatic Control Inhibited.");
            string text = string("Substation Bus Disabled");
            string additional = string("Bus: ");
            additional += getPAOName();
            if (_LOG_MAPID_INFO)
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            if (getDailyOperationsAnalogPointId() > 0)
            {
                CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                                    TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
                pSig->setCondition(CtiTablePointAlarming::highReasonability);
                CtiCapController::getInstance()->sendMessageToDispatch(pSig);
            }
            keepGoing = FALSE;
        }
        else
        {
            bool closeFoundFlag = false;
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if (!currentFeeder->getDisableFlag())
                {
                    for (LONG j=0; j<currentFeeder->getCCCapBanks().size(); j++)
                    {
                        CtiCCCapBank* currentCap = (CtiCCCapBank*)currentFeeder->getCCCapBanks()[j];
                        if (currentCap->getControlStatus() == CtiCCCapBank::Close ||
                            currentCap->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                        {
                            keepGoing = TRUE;
                            closeFoundFlag = true;
                            break;
                        }
                    }
                    if (closeFoundFlag)
                    {
                        break;
                    }
                }
            }
            if (!closeFoundFlag)
            {
                setDisableFlag(TRUE);
                setBusUpdatedFlag(TRUE);
                setSolution("  Sub Disabled. Automatic Control Inhibited.");
                CtiCCSubstationBusStore::getInstance()->UpdateBusDisableFlagInDB(this);
                string text = string("Substation Bus Disabled");
                string additional = string("Bus: ");
                additional += getPAOName();
                if (_LOG_MAPID_INFO)
                {
                    additional += " MapID: ";
                    additional += getMapLocationId();
                    additional += " (";
                    additional += getPAODescription();
                    additional += ")";
                }
                if (getDailyOperationsAnalogPointId() > 0)
                {
                    CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                                        TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
                    pSig->setCondition(CtiTablePointAlarming::highReasonability);
                    CtiCapController::getInstance()->sendMessageToDispatch(pSig);
                }

                keepGoing = FALSE;
            }

        }
    }

    if( keepGoing )
    {
        for(LONG i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
            BOOL peakFlag = isPeakTime(currentDateTime);

            if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) &&
                 stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)") &&
                (currentFeeder->getPeakStartTime() > 0 && currentFeeder->getPeakStopTime() > 0))
            {
                currentFeeder->isPeakTime(currentDateTime);
            }
            else
            {
                currentFeeder->setPeakTimeFlag(isPeakTime(currentDateTime));
            }
        }
        if( currentDateTime.seconds() >= getLastOperationTime().seconds() + getControlDelayTime() )
        {
            if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
            {
                for(LONG i=0;i<_ccfeeders.size();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( !currentFeeder->getDisableFlag() && !currentFeeder->getRecentlyControlledFlag() &&
                        (getControlInterval()!=0 ||
                         currentFeeder->getNewPointDataReceivedFlag() ||
                         currentFeeder->getControlInterval() != 0) )
                    {
                        if( currentFeeder->checkForAndProvideNeededIndividualControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentFeeder->getPeakTimeFlag(), getDecimalPlaces(), getControlUnits(), getMaxDailyOpsHitFlag()) )
                        {
                            setLastOperationTime(currentDateTime);
                            setRecentlyControlledFlag(TRUE);
                            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        }
                        setBusUpdatedFlag(TRUE);
                    }
                }
            }
            else if( stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::ManualOnlyControlMethod) )//intentionally left the ! off
            {
                DOUBLE lagLevel = (isPeakTime(currentDateTime)?_peaklag:_offpklag);
                DOUBLE leadLevel = (getPeakTimeFlag()?_peaklead:_offpklead);
                DOUBLE setPoint = (lagLevel + leadLevel)/2;

                setIWControl(getCurrentWattLoadPointValue());

                if (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits))
                    setIVControl(getCurrentVoltLoadPointValue());
                else
                    setIVControl(getCurrentVarLoadPointValue());


                if (getIntegrateFlag() && getIntegratePeriod() > 0)
                {
                    if (getIVCount() > 0)
                        setIVControl(getIVControlTot() / getIVCount());
                    if (getIWCount() > 0)
                        setIWControl(getIWControlTot() / getIWCount());
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - USING INTEGRATED CONTROL - iVControl=iVControlTot/iVCount ( "<<
                                getIVControl()<<" = "<< getIVControlTot() <<" / "<<getIVCount()<<" )"<< endl;
                        dout << CtiTime() << " - USING INTEGRATED CONTROL - iWControl=iWControlTot/iWCount ( "<<
                                getIWControl()<<" = "<< getIWControlTot() <<" / "<<getIWCount()<<" )"<< endl;
                    }
                    //resetting integration total...
                    if (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits))
                        setIVControlTot(getCurrentVoltLoadPointValue());
                    else
                        setIVControlTot(getCurrentVarLoadPointValue());
                    setIVCount(1);
                    setIWControlTot(getCurrentWattLoadPointValue());
                    setIWCount(1);
                }


                if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
                   !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KQControlUnits) )
                    setPoint = (getPeakTimeFlag()?getPeakPFSetPoint():getOffPeakPFSetPoint());
                setKVARSolution(calculateKVARSolution(_controlunits,setPoint, getIVControl(), getIWControl()));
                setTargetVarValue( getKVARSolution() + getIVControl());


                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    ( getCurrentVarPointQuality() == NormalQuality ||
                      getCurrentWattPointQuality() == NormalQuality ||
                      getCurrentVoltPointQuality() == NormalQuality ) )
                {
                    if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                    {

                        if( (!_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality) &&
                             getIVControl() > lagLevel ||
                             getIVControl() < leadLevel )
                        {
                            if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) )
                            {
                                regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Invalid control method: " << _controlmethod << ", in sub bus: " << getPAOName() << endl;
                            }
                        }
                        else
                        {
                            if (getCurrentVarPointQuality() != NormalQuality &&
                                ( getIVControl() > lagLevel ||
                                  getIVControl() < leadLevel) )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Control Inhibited - Not Normal Var Quality, in sub bus: " << getPAOName() << endl;
                            }
                            setSolution("Not Normal Quality.  Var Control Inhibited.");
                        }
                    }
                    else if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) )
                    {
                        if( (!_IGNORE_NOT_NORMAL_FLAG || getCurrentVoltPointQuality() == NormalQuality) &&
                            (getCurrentVoltLoadPointValue() < lagLevel ||
                            getCurrentVoltLoadPointValue() > leadLevel) )
                        {
                            if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) )
                            {
                                if ((!_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality) &&
                                    getCurrentVarLoadPointId() > 0)
                                    regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Substation Volt Control requires Var PointID.  Var PointID not found in sub bus: " << getPAOName() << endl;
                                }
                            }
                            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Invalid control method: " << _controlmethod << ", in sub bus: " << getPAOName() << endl;
                            }
                        }
                        else
                        {
                            if (getCurrentVoltPointQuality() != NormalQuality &&
                                (getCurrentVoltLoadPointValue() < lagLevel ||
                                 getCurrentVoltLoadPointValue() > leadLevel))
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Control Inhibited - Not Normal Volt Quality, in sub bus: " << getPAOName() << endl;
                            }
                            setSolution("Not Normal Quality.  Volt Control Inhibited.");
                        }
                    }
                    else if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
                             !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KQControlUnits) )
                    {
                        if (!_IGNORE_NOT_NORMAL_FLAG ||
                            (getCurrentVarPointQuality() == NormalQuality && getCurrentWattPointQuality() == NormalQuality) )
                        {
                            if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) )
                            {
                                regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Invalid control method: " << _controlmethod << ", in sub bus: " << getPAOName() << endl;
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Not Normal Quality, in sub bus: " << getPAOName() << endl;
                            }
                            setSolution("Not Normal Quality.  PF Control Inhibited.");
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Invalid control units: " << _controlunits << ", in sub bus: " << getPAOName() << endl;
                    }
                }
                else
                {
                   {
                       CtiLockGuard<CtiLogger> logger_guard(dout);
                       dout << CtiTime() << " - Not Normal Quality, in sub bus: " << getPAOName() << endl;
                   }
                   setSolution("Not Normal Quality.  Control Inhibited.");
                }

                clearOutNewPointReceivedFlags();
            }
        }
        else if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cannot control an additional bank because not past control delay time, in sub bus: " << getPAOName() << endl;
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    calculateKVARSolution


---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::calculateKVARSolution(const string& controlUnits, DOUBLE setPoint, DOUBLE varValue, DOUBLE wattValue)
{
    DOUBLE returnKVARSolution = 0.0;
    if( !stringCompareIgnoreCase(controlUnits,CtiCCSubstationBus::KVARControlUnits) )
    {
        returnKVARSolution = setPoint - varValue;
    }
    else if( !stringCompareIgnoreCase(controlUnits,CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
             !stringCompareIgnoreCase(controlUnits,CtiCCSubstationBus::PF_BY_KQControlUnits))
    {
        DOUBLE targetKVAR = 0.0;
        if (setPoint != 0)
        {
            DOUBLE targetKVA = wattValue / (setPoint/100.0);
            DOUBLE NaNDefenseDouble = (targetKVA*targetKVA)-(wattValue*wattValue);
            if( NaNDefenseDouble > 0.0 )
            {
                targetKVAR = sqrt(NaNDefenseDouble);
                if (setPoint < 0)
                {
                    targetKVAR = 0 - targetKVAR;
                }
            }
        }

        returnKVARSolution = targetKVAR - varValue;

    }
    else if( !stringCompareIgnoreCase(controlUnits,CtiCCSubstationBus::VoltControlUnits) )
    {
        returnKVARSolution = 0;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Invalid control units: " << controlUnits << ", in: " __FILE__ << " at: " << __LINE__ << endl;
    }

    return returnKVARSolution;
}

/*---------------------------------------------------------------------------
    regularSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::regularSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    try
    {
        CtiCCFeeder* currentFeeder = NULL;
        LONG currentPosition = getLastFeederControlledPosition();
        LONG iterations = 0;
        if( ( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) && getCurrentVarLoadPointId() > 0 ) ||
            ( !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) && getCurrentVoltLoadPointId() > 0 ) )
        {
            if( !(getMaxDailyOpsHitFlag() && getMaxOperationDisableFlag()) &&//end day on a trip.
                ( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) &&
                lagLevel <  getIVControl() ) ||
                ( !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) &&
                lagLevel >  getIVControl() ) )
            {
                //if( _CC_DEBUG )
                if(  !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName() << endl;
                }
                else
                {
                    setKVARSolution(-1);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Attempting to Increase Volt level in substation bus: " << getPAOName() << endl;
                    }
                }

                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.size() )
                {
                    if( currentPosition >= _ccfeeders.size()-1 )
                    {
                        currentPosition = 0;
                    }
                    else
                    {
                        currentPosition++;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                    }
                    iterations++;
                }
                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        if( capBank != NULL &&
                            capBank->getRecloseDelay() > 0 &&
                            currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime() << endl;
                                dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                dout << " Current Date Time:       " << currentDateTime << endl;
                            }
                            setSolution("Control Inhibited by Reclose Delay on Cap: "+capBank->getPAOName());
                        }
                        else
                        {
                            DOUBLE controlValue = ( !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getIVControl());
                            string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue,  getCurrentVarLoadPointValue()) ;

                            request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  getCurrentVarLoadPointValue(),
                                                                              getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                        }
                    }
                }

                if( request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {

                       CtiLockGuard<CtiLogger> logger_guard(dout);
                       dout << CtiTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                       << " any further.  All cap banks are already in the Close state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                       try
                       {
                           CtiCCCapBank* currentCapBank = NULL;
                           CtiCCFeeder* currentFeeder = NULL;
                           for(int i=0;i<_ccfeeders.size();i++)
                           {
                               {
                                   currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                                   dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                               }
                               CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                               for(int j=0;j<ccCapBanks.size();j++)
                               {
                                   currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                   dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                               }
                           }
                       }
                       catch(...)
                       {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                       }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        if(  !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                            textInfo += " Cannot Decrease Var Level.  No Cap Banks Available to Close.";
                        else
                            textInfo += " Cannot Increase Volt Level.  No Cap Banks Available to Close.";

                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
            else if (( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) &&
                      getIVControl() < leadLevel ) ||
                    ( !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) &&
                      getIVControl() > leadLevel )) // lead Level is greater than currentVarPointValue
            {
                if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().c_str() << endl;
                }
                else
                {
                    setKVARSolution(1);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Attempting to Decrease Volt level in substation bus: " << getPAOName().c_str() << endl;
                    }
                }

                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.size() )
                {
                    if( currentPosition <= 0 )
                    {
                        currentPosition = _ccfeeders.size()-1;
                    }
                    else
                    {
                        currentPosition--;
                    }

                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                    }
                    iterations++;
                }
                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getIVControl());
                        string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue()) ;
                        request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                    }
                }

                if( request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {

                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        if(  !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                            textInfo += " Cannot Increase Var Level.  No Cap Banks Available to Open.";
                        else
                            textInfo += " Cannot Decrease Volt Level.  No Cap Banks Available to Open.";

                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Max Daily Ops Hit. Control Inhibited on: " << getPAOName() << endl;
                }

            }
        }
        else if( (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
                 !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KQControlUnits) ) &&
                 getCurrentVarLoadPointId() > 0 && getCurrentWattLoadPointId() > 0 )
        {
            if( getKVARSolution() < 0 && !(getMaxDailyOpsHitFlag() && getMaxOperationDisableFlag()) ) //end day on a trip.
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().c_str() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.size() )
                {
                    if( currentPosition >= _ccfeeders.size()-1 )
                    {
                        currentPosition = 0;
                    }
                    else
                    {
                        currentPosition++;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                    }
                    iterations++;
                }

                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        DOUBLE adjustedBankKVARReduction = (lagLevel/100.0)*((DOUBLE)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            if( capBank != NULL &&
                                capBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime() << endl;
                                    dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                    dout << " Current Date Time:       " << currentDateTime << endl;
                                }
                            }
                            else
                            {
                                string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close,  getIVControl(), getCurrentVarLoadPointValue());

                                request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  getCurrentVarLoadPointValue(),
                                                                                  getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                            }
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                        }
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {

                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Close state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;
                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        textInfo += " Cannot Decrease Var Level.  No Cap Banks Available to Close.";
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);
                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().c_str() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                while( capBank == NULL &&
                       iterations < _ccfeeders.size() )
                {
                    if( currentPosition <= 0 )
                    {
                        currentPosition = _ccfeeders.size()-1;
                    }
                    else
                    {
                        currentPosition--;
                    }
                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                    }
                    iterations++;
                }

                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        DOUBLE adjustedBankKVARIncrease = -(leadLevel/100.0)*((DOUBLE)capBank->getBankSize());
                        if( adjustedBankKVARIncrease <= getKVARSolution() )
                        {

                            string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open,  getIVControl(), getCurrentVarLoadPointValue());
                            request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text,
                                                                              getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                        }
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        textInfo += " Cannot Increase Var Level.  No Cap Banks Available to Open.";
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Max Daily Ops Hit. Control Inhibited on: " << getPAOName() << endl;
                }

            }
        }

        if( request != NULL )
        {
            pilMessages.push_back(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlledPAOId(currentFeeder->getPAOId());
            setLastFeederControlledPosition(currentPosition);
            ((CtiCCFeeder*)_ccfeeders.at(currentPosition))->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl( getCurrentVarLoadPointValue() );
            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(TRUE);
        }
        //setNewPointDataReceivedFlag(FALSE);
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(TRUE);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    optimizedSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::optimizedSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;
    CtiCCFeeder* lastFeederControlled = NULL;
    int positionLastFeederControlled = -1;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    try
    {
        RWTPtrSortedVector<CtiCCFeeder, FeederVARComparison<CtiCCFeeder> > varSortedFeeders;
        //DOUBLE setpoint = figureCurrentSetPoint(currentDateTime);

        for(int i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            currentFeeder->fillOutBusOptimizedInfo(getPeakTimeFlag());
            varSortedFeeders.insert(currentFeeder);
        }

        if( ( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) && getCurrentVarLoadPointId() > 0) ||
            ( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) && getCurrentVoltLoadPointId() > 0 ) )
        {
            if( !(getMaxDailyOpsHitFlag()  && getMaxOperationDisableFlag()) &&//end day on a trip.
                ( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) &&
                lagLevel <  getIVControl() ) ||
                ( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) &&
                lagLevel >  getIVControl() ) )
            {
                //if( _CC_DEBUG )
                if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().c_str() << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Volt level in substation bus: " << getPAOName().c_str() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                for(int j=0;j<varSortedFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            if (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits))
                            {
                                setKVARSolution(-1);
                            }
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found in feeder: " << currentFeeder->getPAOName() << endl;
                        }
                    }
                    if( capBank != NULL )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            if( capBank != NULL &&
                                capBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime() << endl;
                                    dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                    dout << " Current Date Time:       " << currentDateTime << endl;
                                }
                            }
                            else
                            {
                                DOUBLE controlValue = ( !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getIVControl());
                                string text =  ((CtiCCFeeder*)varSortedFeeders[j])->createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue());
                                request = ((CtiCCFeeder*)varSortedFeeders[j])->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue(),
                                                                                                        ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseAValue(), ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseBValue(),
                                                                                                        ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseCValue());
                                lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                                positionLastFeederControlled = j;
                            }
                        }
                        break;
                    }
                }
                if( request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Close state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        if(  !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                            textInfo += " Cannot Decrease Var Level.  No Cap Banks Available to Close.";
                        else
                            textInfo += " Cannot Increase Volt Level.  No Cap Banks Available to Close.";

                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
            else //leadLevel greater than currentVarPointValue
            {
                //if( _CC_DEBUG )
                if( !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().c_str() << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Volt level in substation bus: " << getPAOName().c_str() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                for(int j=varSortedFeeders.entries()-1;j>=0;j--)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            if (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits))
                            {
                                setKVARSolution(1);
                            }
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found in feeder: " << currentFeeder->getPAOName() << endl;
                        }
                    }
                    if( capBank != NULL )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            DOUBLE controlValue = ( !stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() :  getIVControl());
                            string text =  ((CtiCCFeeder*)varSortedFeeders[j])->createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue());
                            request = ((CtiCCFeeder*)varSortedFeeders[j])->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue(),
                                                                                                    ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseAValue(), ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseBValue(),
                                                                                                    ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseCValue());
                            lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                            positionLastFeederControlled = j;
                        }
                        break;
                    }
                }

                if( request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {

                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        if(  !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::KVARControlUnits) )
                            textInfo += " Cannot Increase Var Level.  No Cap Banks Available to Open.";
                        else
                            textInfo += " Cannot Decrease Volt Level.  No Cap Banks Available to Open.";

                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
        }
        else if( (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
                 !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::PF_BY_KQControlUnits)) &&
                 getCurrentVarLoadPointId() > 0 && getCurrentWattLoadPointId() > 0 )
        {
            if( getKVARSolution() < 0 && !(getMaxDailyOpsHitFlag()  && getMaxOperationDisableFlag() ))  //end day on a trip.
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().c_str() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                for(int j=0;j<varSortedFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found or NOT NORMAL quality in feeder: " << currentFeeder->getPAOName() << endl;
                        }
                    }
                    if( capBank != NULL )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                            positionLastFeederControlled = j;
                            DOUBLE adjustedBankKVARReduction = (lagLevel/100.0)*((DOUBLE)capBank->getBankSize());
                            if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                            {
                                if( capBank != NULL &&
                                    capBank->getRecloseDelay() > 0 &&
                                    currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                    {
                                        dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime() << endl;
                                        dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                        dout << " Current Date Time:       " << currentDateTime << endl;
                                    }
                                }
                                else
                                {
                                    string text =  ((CtiCCFeeder*)varSortedFeeders[j])->createTextString(getControlMethod(), CtiCCCapBank::Close, getIVControl(), ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue());
                                    request = ((CtiCCFeeder*)varSortedFeeders[j])->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue(),
                                                                                                            ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseAValue(), ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseBValue(),
                                                                                                            ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseCValue());
                                }
                            }
                            else
                            {//cap bank too big
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                            }
                        }
                        break;
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {

                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Decrease Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Close stateor Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        textInfo += " Cannot Decrease Var Level.  No Cap Banks Available to Close.";

                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().c_str() << endl;
                }

                CtiCCCapBank* capBank = NULL;
                for(int j=varSortedFeeders.entries()-1;j>=0;j--)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)varSortedFeeders[j];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found or NOT NORMAL quality in feeder: " << currentFeeder->getPAOName() << endl;
                        }
                    }
                    if( capBank != NULL )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                            positionLastFeederControlled = j;
                            DOUBLE adjustedBankKVARIncrease = -(leadLevel/100.0)*((DOUBLE)capBank->getBankSize());
                            if( adjustedBankKVARIncrease <= getKVARSolution() )
                            {
                                string text =  ((CtiCCFeeder*)varSortedFeeders[j])->createTextString(getControlMethod(), CtiCCCapBank::Open,  getIVControl(), ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue());

                                request = ((CtiCCFeeder*)varSortedFeeders[j])->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, ((CtiCCFeeder*)varSortedFeeders[j])->getCurrentVarLoadPointValue(),
                                                                                                        ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseAValue(), ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseBValue(),
                                                                                                    ((CtiCCFeeder*)varSortedFeeders[j])->getPhaseCValue());
                            }
                            else
                            {//cap bank too big
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                            }
                        }
                        break;
                    }
                }

                if( capBank == NULL && request == NULL  )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {

                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                        << " any further.  All cap banks are already in the Open state or Feeders Disabled in: " << __FILE__ << " at: " << __LINE__ << endl;

                        try
                        {
                            CtiCCCapBank* currentCapBank = NULL;
                            CtiCCFeeder* currentFeeder = NULL;
                            for(int i=0;i<_ccfeeders.size();i++)
                            {
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                                    dout << "Feeder: " << currentFeeder->getPAOName() << " ControlDelay: " << currentFeeder->getControlDelayTime() << " DisableFlag: " << (currentFeeder->getDisableFlag()?"TRUE":"FALSE") << endl;
                                }
                                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                                for(int j=0;j<ccCapBanks.size();j++)
                                {
                                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (!getCorrectionNeededNoBankAvailFlag())
                    {
                        setCorrectionNeededNoBankAvailFlag(TRUE);
                        string textInfo;
                        textInfo = ("Sub: ");
                        textInfo += getPAOName();
                        textInfo += " Cannot Increase Var Level.  No Cap Banks Available to Open.";
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPAOId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(FALSE);

                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid control units: " << _controlunits << " in sub bus: " << getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        if( request != NULL )
        {
            pilMessages.push_back(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlledPAOId(lastFeederControlled->getPAOId());
            setLastFeederControlledPosition(positionLastFeederControlled);
            lastFeederControlled->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl( getCurrentVarLoadPointValue() );
            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(TRUE);
        }
        //setNewPointDataReceivedFlag(FALSE);
        //for(int j=0;j<_ccfeeders.size();j++)
        //{
            //((CtiCCFeeder*)_ccfeeders[j])->setNewPointDataReceivedFlag(FALSE);
        //}
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(TRUE);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}



/*---------------------------------------------------------------------------
    figureCurrentSetPoint

    Returns the current set point depending on if it is peak or off peak
    time and sets the set point status
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::figureCurrentSetPoint(const CtiTime& currentDateTime)
{
    DOUBLE lagLevel = (isPeakTime(currentDateTime)?_peaklag:_offpklag);
    DOUBLE leadLevel = (getPeakTimeFlag()?_peaklead:_offpklead);

    return (lagLevel + leadLevel)/2;
}

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPeakTime(const CtiTime& currentDateTime)
{
    unsigned secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
    if( isPeakDay(currentDateTime) && getPeakStartTime() <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= getPeakStopTime() )
    {
        setPeakTimeFlag(TRUE);
    }
    else
    {
        setPeakTimeFlag(FALSE);
    }
    return _peaktimeflag;
}

/*---------------------------------------------------------------------------
    isControlPoint

    Returns a boolean if it is the control point for the sub.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isControlPoint(LONG pointid)
{
    BOOL retVal = FALSE;

    if (stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        if (!stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits)  &&
            getCurrentVoltLoadPointId() == pointid )
            retVal = TRUE;
        else if (!stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::KVARControlUnits)  &&
            getCurrentVarLoadPointId() == pointid)
            retVal = TRUE;
        else if (!stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::PF_BY_KVARControlUnits) &&
                 (getCurrentVarLoadPointId() == pointid || getCurrentWattLoadPointId() == pointid) )
            retVal = TRUE;
        else
            retVal = FALSE;
    }
    return retVal;
}

void CtiCCSubstationBus::updateIntegrationVPoint(const CtiTime &currentDateTime)
{
    DOUBLE controlVvalue = 0;

    if (stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        if (!stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) )
            controlVvalue = getCurrentVoltLoadPointValue();
        else if (!stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::KVARControlUnits)||
                 !stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::PF_BY_KVARControlUnits))
            controlVvalue = getCurrentVarLoadPointValue();
        else
        {
            //integration not implemented.
            controlVvalue = 0;
        }

        if (getControlInterval() > 0)
        {
            if (getNextCheckTime() - getIntegratePeriod() <= currentDateTime)
            {

                if (getIVCount() == 0)
                {
                    setIVControlTot( controlVvalue );
                }
                else
                    setIVControlTot( getIVControlTot() + controlVvalue );

                setIVCount( getIVCount() + 1 );
            }
            else
            {
                setIVControlTot( controlVvalue );
                setIVCount( 1 );
            }
        }
        else
        {
            setIVControlTot( controlVvalue );
            setIVCount( 1 );
        }
    }
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " iVControlTot = " <<getIVControlTot() <<" iVCount = "<<getIVCount()<< endl;
    }
}

void CtiCCSubstationBus::updateIntegrationWPoint(const CtiTime &currentDateTime)
{
    DOUBLE controlWvalue = 0;

    if (stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        controlWvalue = getCurrentWattLoadPointValue();
        if (getControlInterval() > 0)
        {
            if (getNextCheckTime() - getIntegratePeriod() <= currentDateTime)
            {

                if (getIWCount() == 0)
                {
                    setIWControlTot( controlWvalue );
                }
                else
                    setIWControlTot( getIWControlTot() + controlWvalue );

                setIWCount( getIWCount() + 1 );
            }
            else
            {
                setIWControlTot( controlWvalue );
                setIWCount( 1 );
            }
        }
        else
        {
            setIWControlTot( controlWvalue );
            setIWCount( 1 );
        }
    }
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " iWControlTot = " <<getIWControlTot() <<" iWCount = "<<getIWCount()<< endl;
    }

}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    BOOL returnBoolean = TRUE;
    BOOL found = FALSE;
    char tempchar[64] = "";
    string text = "";
    string additional = "";

    LONG minConfirmPercent = getMinConfirmPercent();
    LONG maxConfirmTime = getMaxConfirmTime();
    LONG sendRetries = getControlSendRetries();
    LONG failPercent = getFailurePercent();

    try
    {
        if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) ||
            !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
        {
            LONG recentlyControlledFeeders = 0;
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getMaxConfirmTime();
                    sendRetries = currentFeeder->getControlSendRetries();
                    failPercent = currentFeeder->getFailurePercent();
                }

                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    if( currentFeeder->isAlreadyControlled(minConfirmPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                               currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                               currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getRegression(),
                                               currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC(),
                                               currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag() )
                        || (currentFeeder->getLastOperationTime().seconds() + maxConfirmTime <= CtiTime().seconds() ) )
                    {

                        if (currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag())
                        {
                            currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                               failPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                                               currentFeeder->getPhaseBValueBeforeControl(),currentFeeder->getPhaseCValueBeforeControl(),
                                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                                               currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC() );

                        }
                        else
                        {
                            currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, minConfirmPercent,failPercent,currentFeeder->getVarValueBeforeControl(),
                                                                      currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                                      currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                                                      currentFeeder->getRegression());
                        }

                    }
                    if( currentFeeder->getRecentlyControlledFlag() )
                    {
                        recentlyControlledFeeders++;
                    }
                }
            }
            if( recentlyControlledFeeders == 0 )
            {
                setWaitToFinishRegularControlFlag(FALSE);
                setRecentlyControlledFlag(FALSE);
            }
            figureEstimatedVarLoadPointValue();
        }
        else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) ||
                 !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod))
        {
            LONG currentFeedPos = 0;
            if (getLastFeederControlledPosition() >= 0)
                currentFeedPos = getLastFeederControlledPosition();

            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(currentFeedPos);

            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
            {
                currentFeeder->setEventSequence(getEventSequence());

                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getMaxConfirmTime();
                    sendRetries = currentFeeder->getControlSendRetries();
                    failPercent = currentFeeder->getFailurePercent();
                }
                if (getUsePhaseData() && !getTotalizedControlFlag())
                {
                    currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                       failPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                       getPhaseBValueBeforeControl(),getPhaseCValueBeforeControl(),
                                                       getPhaseAValue(), getPhaseBValue(), getPhaseCValue(), regressionA, regressionB, regressionC);

                }
                else
                {
                   if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                        currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag() )
                   {
                       currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                                         failPercent,  currentFeeder->getCurrentVarPointQuality(),
                                                                         currentFeeder->getPhaseAValueBeforeControl(),
                                                                         currentFeeder->getPhaseBValueBeforeControl(),
                                                                         currentFeeder->getPhaseCValueBeforeControl(),
                                                                         currentFeeder->getPhaseAValue(),
                                                                         currentFeeder->getPhaseBValue(),
                                                                         currentFeeder->getPhaseCValue(),
                                                                         currentFeeder->getRegressionA(),
                                                                         currentFeeder->getRegressionB(),
                                                                         currentFeeder->getRegressionC());
                   }
                   else
                   {
                       currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,getVarValueBeforeControl(),
                                                              getCurrentVarLoadPointValue(), getCurrentVarPointQuality(), getPhaseAValue(),
                                                              getPhaseBValue(), getPhaseCValue(), regression);
                   }
                }
                setRecentlyControlledFlag(FALSE);
                figureEstimatedVarLoadPointValue();
                found = TRUE;
            }
            else
            {
                for(LONG i=0;i<_ccfeeders.size();i++)
                {
                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                    if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getMinConfirmPercent();
                        maxConfirmTime = currentFeeder->getMaxConfirmTime();
                        sendRetries = currentFeeder->getControlSendRetries();
                        failPercent = currentFeeder->getFailurePercent();
                    }

                    if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                    {
                        currentFeeder->setEventSequence(getEventSequence());
                        if (getUsePhaseData() && !getTotalizedControlFlag())
                        {
                            currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                               failPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                               getPhaseBValueBeforeControl(),getPhaseCValueBeforeControl(),
                                                               getPhaseAValue(), getPhaseBValue(), getPhaseCValue(), regressionA, regressionB, regressionC);

                        }
                        else
                        {
                            currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,
                                                                      getVarValueBeforeControl(),getCurrentVarLoadPointValue(),
                                                                      getCurrentVarPointQuality(), getPhaseAValue(),
                                                                      getPhaseBValue(), getPhaseCValue(), regression);
                        }
                        setRecentlyControlledFlag(FALSE);
                        figureEstimatedVarLoadPointValue();
                        found = TRUE;
                        break;
                    }
                }
                if (found == FALSE)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Last Feeder controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
                    returnBoolean = TRUE;
                }
            }
        }
        else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::ManualOnlyControlMethod) )
        {
            LONG recentlyControlledFeeders = 0;
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getMinConfirmPercent();
                        maxConfirmTime = currentFeeder->getMaxConfirmTime();
                        sendRetries = currentFeeder->getControlSendRetries();
                        failPercent = currentFeeder->getFailurePercent();
                    }
                    currentFeeder->setEventSequence(getEventSequence());
                    if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                    {
                        if( currentFeeder->isAlreadyControlled(minConfirmPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                           getPhaseBValueBeforeControl(), getPhaseCValueBeforeControl(),
                                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue(),
                                                           getVarValueBeforeControl(), getCurrentVarLoadPointValue(),
                                                           regression, regressionA, regressionB, regressionC,
                                                           getUsePhaseData(), getTotalizedControlFlag()  )
                            || currentFeeder->isPastMaxConfirmTime(CtiTime(),maxConfirmTime,sendRetries)
                          )
                        {
                            if (currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag())
                            {
                                currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                                   failPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                                                   currentFeeder->getPhaseBValueBeforeControl(),currentFeeder->getPhaseCValueBeforeControl(),
                                                                   currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                                                   currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC() );

                            }
                            else
                            {
                                currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,
                                                                          currentFeeder->getVarValueBeforeControl(),currentFeeder->getCurrentVarLoadPointValue(),
                                                                          currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                                          currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(), currentFeeder->getRegression());
                            }
                        }
                        if( currentFeeder->getRecentlyControlledFlag() )
                        {
                            recentlyControlledFeeders++;
                        }
                    }
                    else if( getCurrentVarLoadPointId() > 0 )
                    {
                        if (getUsePhaseData() && !getTotalizedControlFlag())
                        {
                            currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                               failPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                               getPhaseBValueBeforeControl(),getPhaseCValueBeforeControl(),
                                                               getPhaseAValue(), getPhaseBValue(), getPhaseCValue(), regressionA, regressionB, regressionC);

                        }
                        else
                        {
                            currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,getVarValueBeforeControl(),
                                                                      getCurrentVarLoadPointValue(), getCurrentVarPointQuality(), getPhaseAValue(),
                                                                      getPhaseBValue(), getPhaseCValue(), regression);
                        }
                        figureEstimatedVarLoadPointValue();
                    }
                    else
                    {
                        currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, 0,0,getVarValueBeforeControl(),
                                                                  getCurrentVarLoadPointValue(), getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                                  currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(), currentFeeder->getRegression());
                        //setRecentlyControlledFlag(FALSE);
                        figureEstimatedVarLoadPointValue();
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Manual Only Sub Bus: " << getPAOName() << " does not have Var points to perform confirmation logic." << endl;
                        }
                    }
                }
            }
            if( recentlyControlledFeeders == 0 )
            {
                setRecentlyControlledFlag(FALSE);
            }
            figureEstimatedVarLoadPointValue();
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    //setNewPointDataReceivedFlag(FALSE);

    return returnBoolean;
}


BOOL CtiCCSubstationBus::capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    BOOL returnBoolean = FALSE;
    BOOL foundCap = FALSE;
    BOOL foundFeeder = FALSE;
    BOOL assumedWrongFlag = FALSE;
    char tempchar[64] = "";
    string text = "";
    string additional = "";
    DOUBLE ratio = 0;
    DOUBLE change = 0;
    DOUBLE oldValue = 0;
    DOUBLE newValue = 0;
    CtiTime time;

    LONG minConfirmPercent = getMinConfirmPercent();
    LONG maxConfirmTime = getMaxConfirmTime();
    LONG sendRetries = getControlSendRetries();
    LONG failPercent = getFailurePercent();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    BOOL vResult = FALSE; //fail


    if ((stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) &&
         stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) ) &&
        getUsePhaseData() && !getTotalizedControlFlag()  ) 
    {
        returnBoolean = capBankVerificationPerPhaseStatusUpdate(pointChanges, ccEvents);
    }
    else
    {

        for(LONG i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            if (currentFeeder->getPAOId() == getCurrentVerificationFeederId())
            {
                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getMaxConfirmTime();
                    sendRetries = currentFeeder->getControlSendRetries();
                    failPercent = currentFeeder->getFailurePercent();
                }

                if( ((!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) ||
                     !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
                     !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod)) &&
                     currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag() ) )
                {
                    returnBoolean = currentFeeder->capBankVerificationPerPhaseStatusUpdate(pointChanges, ccEvents,
                                                                                           minConfirmPercent, failPercent);
                    foundCap = TRUE;
                }
                else
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    CtiCCCapBank* currentCapBank = NULL;

                    for(int j=0;j<ccCapBanks.size();j++)
                    {
                       currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                       if (currentCapBank->getPAOId() == getCurrentVerificationCapBankId())
                       {

                           if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                           {
                               if( !_IGNORE_NOT_NORMAL_FLAG ||
                                   getCurrentVarPointQuality() == NormalQuality )
                               {
                                   if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                       !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
                                       (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                                       currentFeeder->getCurrentVarLoadPointId() > 0 ) )
                                   {
                                       newValue =  currentFeeder->getCurrentVarLoadPointValue();
                                       if( _RATE_OF_CHANGE && currentFeeder->getRegression().depthMet() )
                                       {
                                           oldValue =  currentFeeder->getRegression().regression(time.seconds());
                                       }
                                       else
                                       {
                                           oldValue =  currentFeeder->getVarValueBeforeControl();
                                       }
                                   }
                                   else
                                   {
                                       newValue =  getCurrentVarLoadPointValue();
                                       if( _RATE_OF_CHANGE && regression.depthMet() )
                                       {
                                           oldValue =  getRegression().regression(time.seconds());
                                       }
                                       else
                                       {
                                           oldValue =  getVarValueBeforeControl();
                                       }
                                   }
                                   change = newValue - oldValue;
                                   if( change < 0 )
                                   {
                                       {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           dout << CtiTime() << " - "<< currentCapBank->getPAOName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                                       }
                                       if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE &&
                                           currentCapBank->getVCtrlIndex() == 1)
                                       {
                                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                                           setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                                           store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, FALSE);


                                           //return returnBoolean;
                                           change = 0 - change;
                                           assumedWrongFlag = TRUE;
                                       }
                                   }
                                   ratio = change/currentCapBank->getBankSize();
                                   if( ratio < minConfirmPercent*.01 )
                                   {
                                       if( ratio < failPercent*.01 && failPercent != 0 && minConfirmPercent != 0 )
                                       {
                                           if (!assumedWrongFlag)
                                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                                           else
                                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPAOName();

                                           if (currentCapBank->getControlStatusQuality() != CC_CommFail) 
                                               currentCapBank->setControlStatusQuality(CC_Fail);
                                       }
                                       else if( minConfirmPercent != 0 )
                                       {
                                           if (!assumedWrongFlag)
                                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                           else
                                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPAOName();

                                           currentCapBank->setControlStatusQuality(CC_Significant);
                                       }
                                       else
                                       {
                                           if (!assumedWrongFlag)
                                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                           else
                                               currentCapBank->setControlStatus(CtiCCCapBank::Close);

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPAOName();

                                           currentCapBank->setControlStatusQuality(CC_Normal);
                                           vResult = TRUE;
                                       }
                                   }
                                   else
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = TRUE;
                                   }

                                   text = currentFeeder->createControlStatusUpdateText(currentCapBank->getControlStatus(), newValue,ratio);

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));

                               }
                               else
                               {
                                   char tempchar[80];
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                   text = string("Non Normal Var Quality = ");
                                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                                   text += tempchar;
                                   text += " Var: ";
                                   text += doubleToString(getCurrentVarLoadPointValue());
                                   text += ", OpenQuestionable";
                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPAOName();

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));

                                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                               }
                           }
                           else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                           {
                               if( !_IGNORE_NOT_NORMAL_FLAG ||
                                   getCurrentVarPointQuality() == NormalQuality )
                               {
                                   if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                       !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
                                       (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                                       currentFeeder->getCurrentVarLoadPointId() > 0 ))
                                   {
                                       newValue =  currentFeeder->getCurrentVarLoadPointValue();
                                       if( _RATE_OF_CHANGE && currentFeeder->getRegression().depthMet() )
                                       {
                                           oldValue =  currentFeeder->getRegression().regression(time.seconds());
                                       }
                                       else
                                       {
                                           oldValue =  currentFeeder->getVarValueBeforeControl();
                                       }
                                   }
                                   else
                                   {
                                       newValue =  getCurrentVarLoadPointValue();
                                       if( _RATE_OF_CHANGE && regression.depthMet() )
                                       {
                                           oldValue =  regression.regression(time.seconds());
                                       }
                                       else
                                       {
                                           oldValue =  getVarValueBeforeControl();
                                       }
                                   }
                                   change = oldValue - newValue;

                                   if( change < 0 )
                                   {
                                       {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           dout << CtiTime() << " - "<< currentCapBank->getPAOName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                                       }
                                       if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE &&
                                           currentCapBank->getVCtrlIndex() == 1)
                                       {
                                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                                           setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                                           //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                                           store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenPending, currentCapBank);
                                           //return returnBoolean;
                                           change = 0 - change;
                                           assumedWrongFlag = TRUE;
                                       }
                                   }
                                   ratio = change/currentCapBank->getBankSize();
                                   if( ratio < minConfirmPercent*.01 )
                                   {
                                       if( ratio < failPercent*.01 && failPercent != 0 && minConfirmPercent != 0 )
                                       {
                                           if (!assumedWrongFlag)
                                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                                           else
                                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPAOName();
                                           if (currentCapBank->getControlStatusQuality() != CC_CommFail) 
                                               currentCapBank->setControlStatusQuality(CC_Fail);
                                       }
                                       else if( minConfirmPercent != 0 )
                                       {
                                           if (!assumedWrongFlag)
                                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                           else
                                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPAOName();

                                           currentCapBank->setControlStatusQuality(CC_Significant);
                                       }
                                       else
                                       {
                                           if (!assumedWrongFlag)
                                               currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                           else
                                               currentCapBank->setControlStatus(CtiCCCapBank::Open);

                                           text = string("Var: ");
                                           text += doubleToString(newValue);
                                           text += " ( ";
                                           text += doubleToString(ratio*100.0);
                                           if (!assumedWrongFlag)
                                               text += "% change), Closed";
                                           else
                                               text += "% change), Open";

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPAOName();

                                           currentCapBank->setControlStatusQuality(CC_Normal);
                                           vResult = TRUE;
                                       }
                                   }
                                   else
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::Open);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = TRUE;
                                   }

                                   text = currentFeeder->createControlStatusUpdateText(currentCapBank->getControlStatus(), newValue,ratio);

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));
                               }
                               else
                               {
                                   char tempchar[80];
                                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                   text = string("Non Normal Var Quality = ");
                                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                                   text += tempchar;
                                   text += " Var: ";
                                   text += doubleToString(getCurrentVarLoadPointValue());
                                   text += ", CloseQuestionable";
                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPAOName();

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));

                                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                               }
                           }
                           else
                           {
                               {
                                   CtiLockGuard<CtiLogger> logger_guard(dout);
                                   dout << CtiTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                               }
                               if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail)
                               {
                                   text = "OpenFail";
                               }
                               else if (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail)
                               {
                                   text = "CloseFail";
                               }
                               currentCapBank->setControlStatusQuality(CC_Fail);
                               returnBoolean = FALSE;
                              // break;
                           }
                           if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                               (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
                           {
                               currentCapBank->setRetryOpenFailedFlag(FALSE);
                               currentCapBank->setRetryCloseFailedFlag(FALSE);
                           }
                           setSolution(text);
                           if( currentCapBank->getStatusPointId() > 0 )
                           {
                               if (currentCapBank->getPorterRetFailFlag() && currentCapBank->getIgnoreFlag())
                               {
                                     currentCapBank->setVCtrlIndex(5);
                                     currentCapBank->setVerificationDoneFlag(TRUE);
                                     currentCapBank->setIgnoreFlag(FALSE);
                               }
                               else
                               {
                                   if( text.length() > 0 )
                                   {//if control failed or questionable, create event to be sent to dispatch
                                       long tempLong = currentCapBank->getStatusPointId();
                                       if (_LOG_MAPID_INFO)
                                       {
                                           additional += " MapID: ";
                                           additional += currentFeeder->getMapLocationId();
                                           additional += " (";
                                           additional += currentFeeder->getPAODescription();
                                           additional += ")";
                                       }
                                       pointChanges.push_back(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent, "cap control verification"));
                                       ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                   }
                                   pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                                   ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                   currentCapBank->setLastStatusChangeTime(CtiTime());

                                   INT actionId = CCEventActionIdGen(currentCapBank->getStatusPointId());
                                   string stateInfo = currentCapBank->getControlStatusQualityString();
                                   LONG stationId, areaId, spAreaId;
                                   store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                   ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId,
                                                                           getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate,
                                                                           getEventSequence(), currentCapBank->getControlStatus(), text,
                                                                           "cap control verification", oldValue, newValue, change,
                                                                           currentCapBank->getIpAddress(), actionId, stateInfo,
                                                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue()));
                               }
                               currentCapBank->setPorterRetFailFlag(FALSE);


                           }
                           else
                           {
                               CtiLockGuard<CtiLogger> logger_guard(dout);
                               dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                               << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                           }
                           currentCapBank->setControlRecentlySentFlag(FALSE);
                           if (currentCapBank->updateVerificationState())
                           {
                               returnBoolean = TRUE;
                               currentCapBank->setPerformingVerificationFlag(FALSE);
                               currentFeeder->setPerformingVerificationFlag(FALSE);
                               setBusUpdatedFlag(TRUE);
                               return returnBoolean;
                           }
                           foundCap = TRUE;
                           break;
                       }
                    }
                }
                foundFeeder = TRUE;

                currentFeeder->setRetryIndex(0);
                break;
            }

        }
        if (foundCap == FALSE)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Last Verification Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
            returnBoolean = TRUE;
        }
        if (foundFeeder == FALSE)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Last Verification Feeder controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
            returnBoolean = TRUE;
        }


    }
    return returnBoolean;
}


BOOL CtiCCSubstationBus::capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    BOOL returnBoolean = FALSE;
    BOOL foundCap = FALSE;
    BOOL foundFeeder = FALSE;
    BOOL assumedWrongFlag = FALSE;
    char tempchar[64] = "";
    string text = "";
    string additional = "";
    DOUBLE change = 0;
    DOUBLE changeA = 0;
    DOUBLE changeB = 0;
    DOUBLE changeC = 0;
    DOUBLE ratioA = 0;
    DOUBLE ratioB = 0;
    DOUBLE ratioC = 0;
    double varAValue = getPhaseAValue();
    double varBValue = getPhaseBValue();
    double varCValue = getPhaseCValue();

    double varValueAbc = getPhaseAValueBeforeControl();
    double varValueBbc = getPhaseBValueBeforeControl();
    double varValueCbc = getPhaseCValueBeforeControl();
    CtiTime time;

    LONG minConfirmPercent = getMinConfirmPercent();
    LONG maxConfirmTime = getMaxConfirmTime();
    LONG sendRetries = getControlSendRetries();
    LONG failPercent = getFailurePercent();

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    BOOL vResult = FALSE; //fail

    for(LONG i=0;i<_ccfeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
        if (currentFeeder->getPAOId() == getCurrentVerificationFeederId())
        {
            foundFeeder = TRUE;

            if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
            {
                minConfirmPercent = currentFeeder->getMinConfirmPercent();
                maxConfirmTime = currentFeeder->getMaxConfirmTime();
                sendRetries = currentFeeder->getControlSendRetries();
                failPercent = currentFeeder->getFailurePercent();
            }

            if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
               currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0 &&
               currentFeeder->getPhaseCId() > 0 && currentFeeder->getTotalizedControlFlag())
            {
                returnBoolean = currentFeeder->capBankVerificationStatusUpdate(pointChanges,
                                                                               ccEvents,
                                                                               minConfirmPercent,
                                                                               failPercent,
                                                                               currentFeeder->getPhaseAValue(),
                                                                               currentFeeder->getPhaseBValue(),
                                                                               currentFeeder->getPhaseCValue() );
                foundCap = TRUE;

            }
            else
            {

                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                CtiCCCapBank* currentCapBank = NULL;

                for(int j=0;j<ccCapBanks.size();j++)
                {
                   currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                   if (currentCapBank->getPAOId() == getCurrentVerificationCapBankId())
                   {

                       if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                       {
                           if( !_IGNORE_NOT_NORMAL_FLAG ||
                               getCurrentVarPointQuality() == NormalQuality )
                           {
                               if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                   !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
                                  (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                                    currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0 &&
                                    currentFeeder->getPhaseCId() > 0 )  )
                               {
                                   varAValue = currentFeeder->getPhaseAValue();
                                   varBValue = currentFeeder->getPhaseBValue();
                                   varCValue = currentFeeder->getPhaseCValue();

                                   if( _RATE_OF_CHANGE && currentFeeder->getRegressionA().depthMet() &&
                                       currentFeeder->getRegressionB().depthMet() &&
                                       currentFeeder->getRegressionC().depthMet() )
                                   {
                                       varValueAbc = currentFeeder->getRegressionA().regression( time.seconds() );
                                       varValueBbc = currentFeeder->getRegressionB().regression( time.seconds() );
                                       varValueCbc = currentFeeder->getRegressionC().regression( time.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           CtiTime time;
                                           dout << time << " - Rate of Change  Phase A: " << varValueAbc << endl;
                                           dout << time << "                   Phase B: " << varValueBbc << endl;
                                           dout << time << "                   Phase C: " << varValueCbc << endl;
                                       }
                                   }
                                   else
                                   {
                                       varValueAbc = currentFeeder->getPhaseAValueBeforeControl();
                                       varValueBbc = currentFeeder->getPhaseBValueBeforeControl();
                                       varValueCbc = currentFeeder->getPhaseCValueBeforeControl();
                                   }
                               }
                               else
                               {
                                   if( _RATE_OF_CHANGE && regressionA.depthMet() &&
                                       regressionB.depthMet() && regressionC.depthMet() )
                                   {
                                       varValueAbc = getRegressionA().regression( time.seconds() );
                                       varValueBbc = getRegressionB().regression( time.seconds() );
                                       varValueCbc = getRegressionC().regression( time.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           CtiTime time;
                                           dout << time << " - Rate of Change  Phase A: " << varValueAbc << endl;
                                           dout << time << "                   Phase B: " << varValueBbc << endl;
                                           dout << time << "                   Phase C: " << varValueCbc << endl;
                                       }
                                   }
                               }
                               changeA = varAValue - varValueAbc;
                               changeB = varBValue - varValueBbc;
                               changeC = varCValue - varValueCbc;


                               if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                               {
                                   {
                                       CtiLockGuard<CtiLogger> logger_guard(dout);
                                       dout << CtiTime() << " - "<< currentCapBank->getPAOName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                                   }
                                   if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE &&
                                       currentCapBank->getVCtrlIndex() == 1)
                                   {
                                       currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                                       setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                                       store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                           currentCapBank, FALSE);


                                       changeA = 0 - changeA;
                                       changeB = 0 - changeB;
                                       changeC = 0 - changeC;

                                       assumedWrongFlag = TRUE;
                                   }
                               }
                               ratioA = changeA/(currentCapBank->getBankSize() /3);
                               ratioB = changeB/(currentCapBank->getBankSize() /3);
                               ratioC = changeC/(currentCapBank->getBankSize() /3);
                               if( ratioA < minConfirmPercent*.01 ||
                                   ratioB < minConfirmPercent*.01 ||
                                   ratioC < minConfirmPercent*.01  )
                               {
                                   if( (ratioA < failPercent*.01 && ratioB < failPercent*.01 && ratioC < failPercent*.01) &&
                                        failPercent != 0 && minConfirmPercent != 0 )
                                   {
                                       if (!assumedWrongFlag)
                                           store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                                       else
                                           store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                           currentCapBank->setControlStatusQuality(CC_Fail);
                                   }
                                   else if( minConfirmPercent != 0 )
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       if ((ratioA < minConfirmPercent*.01 && ratioA >= failPercent*.01) &&
                                           (ratioB < minConfirmPercent*.01 && ratioB >= failPercent*.01) &&
                                           (ratioC < minConfirmPercent*.01 && ratioC >= failPercent*.01))
                                       {
                                           currentCapBank->setControlStatusQuality(CC_Significant);
                                       }
                                       else
                                       {
                                           currentCapBank->setControlStatusQuality(CC_Partial);
                                       }
                                   }
                                   else
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::Close);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = TRUE;
                                   }
                               }
                               else
                               {
                                   if (!assumedWrongFlag)
                                       currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                   else
                                       currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPAOName();

                                   currentCapBank->setControlStatusQuality(CC_Normal);
                                   vResult = TRUE;
                               }

                               text = currentFeeder->createPhaseControlStatusUpdateText(currentCapBank->getControlStatus(), varAValue,
                                                              varBValue, varCValue, ratioA, ratioB, ratioC);

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));


                           }
                           else
                           {
                               char tempchar[80];
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                               text = string("Non Normal Var Quality = ");
                               _ltoa(getCurrentVarPointQuality(),tempchar,10);
                               text += tempchar;
                               text += " Var: ";
                               text += doubleToString(getCurrentVarLoadPointValue());
                               text += ", OpenQuestionable";
                               additional = string("Feeder: ");
                               additional += currentFeeder->getPAOName();

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

                               currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                           }
                       }
                       else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                       {
                           if( !_IGNORE_NOT_NORMAL_FLAG ||
                               getCurrentVarPointQuality() == NormalQuality )
                           {
                               if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                   !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
                                  (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                                    currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0 &&
                                    currentFeeder->getPhaseCId() > 0 )  )
                               {
                                   varAValue = currentFeeder->getPhaseAValue();
                                   varBValue = currentFeeder->getPhaseBValue();
                                   varCValue = currentFeeder->getPhaseCValue();

                                   if( _RATE_OF_CHANGE && currentFeeder->getRegressionA().depthMet() &&
                                       currentFeeder->getRegressionB().depthMet() &&
                                       currentFeeder->getRegressionC().depthMet() )
                                   {
                                       varValueAbc = currentFeeder->getRegressionA().regression( time.seconds() );
                                       varValueBbc = currentFeeder->getRegressionB().regression( time.seconds() );
                                       varValueCbc = currentFeeder->getRegressionC().regression( time.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           CtiTime time;
                                           dout << time << " - Rate of Change  Phase A: " << varValueAbc << endl;
                                           dout << time << "                   Phase B: " << varValueBbc << endl;
                                           dout << time << "                   Phase C: " << varValueCbc << endl;
                                       }
                                   }
                                   else
                                   {
                                       varValueAbc = currentFeeder->getPhaseAValueBeforeControl();
                                       varValueBbc = currentFeeder->getPhaseBValueBeforeControl();
                                       varValueCbc = currentFeeder->getPhaseCValueBeforeControl();
                                   }
                               }
                               else
                               {
                                   if( _RATE_OF_CHANGE && regressionA.depthMet() &&
                                       regressionB.depthMet() && regressionC.depthMet() )
                                   {
                                       varValueAbc = getRegressionA().regression( time.seconds() );
                                       varValueBbc = getRegressionB().regression( time.seconds() );
                                       varValueCbc = getRegressionC().regression( time.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           CtiTime time;
                                           dout << time << " - Rate of Change  Phase A: " << varValueAbc << endl;
                                           dout << time << "                   Phase B: " << varValueBbc << endl;
                                           dout << time << "                   Phase C: " << varValueCbc << endl;
                                       }
                                   }
                               }
                               changeA = varValueAbc - varAValue;
                               changeB = varValueBbc - varBValue;
                               changeC = varValueCbc - varCValue;
                               if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                               {
                                   {
                                       CtiLockGuard<CtiLogger> logger_guard(dout);
                                       dout << CtiTime() << " - "<< currentCapBank->getPAOName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                                   }
                                   if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE &&
                                       currentCapBank->getVCtrlIndex() == 1)
                                   {
                                       currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                                       setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                                       store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenPending, currentCapBank);

                                       changeA = 0 - changeA;
                                       changeB = 0 - changeB;
                                       changeC = 0 - changeC;

                                       assumedWrongFlag = TRUE;
                                   }
                               }
                               ratioA = changeA/(currentCapBank->getBankSize() /3);
                               ratioB = changeB/(currentCapBank->getBankSize() /3);
                               ratioC = changeC/(currentCapBank->getBankSize() /3);
                               if( ratioA < minConfirmPercent*.01 ||
                                   ratioB < minConfirmPercent*.01 ||
                                   ratioC < minConfirmPercent*.01  )
                               {

                                   if( (ratioA < failPercent*.01 && ratioB < failPercent*.01 && ratioC < failPercent*.01) &&
                                        failPercent != 0 && minConfirmPercent != 0 )
                                   {
                                       if (!assumedWrongFlag)
                                           store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                                       else
                                           store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                           currentCapBank->setControlStatusQuality(CC_Fail);
                                   }
                                   else if( minConfirmPercent != 0 )
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       if ((ratioA < minConfirmPercent*.01 && ratioA >= failPercent*.01) &&
                                           (ratioB < minConfirmPercent*.01 && ratioB >= failPercent*.01) &&
                                           (ratioC < minConfirmPercent*.01 && ratioC >= failPercent*.01))
                                       {
                                           currentCapBank->setControlStatusQuality(CC_Significant);
                                       }
                                       else
                                       {
                                           currentCapBank->setControlStatusQuality(CC_Partial);
                                       }
                                   }
                                   else
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::Open);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPAOName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = TRUE;
                                   }
                               }
                               else
                               {
                                   if (!assumedWrongFlag)
                                       currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                   else
                                       currentCapBank->setControlStatus(CtiCCCapBank::Open);

                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPAOName();

                                   currentCapBank->setControlStatusQuality(CC_Normal);
                                   vResult = TRUE;
                               }

                               text = currentFeeder->createPhaseControlStatusUpdateText(currentCapBank->getControlStatus(), varAValue,
                                                              varBValue, varCValue, ratioA, ratioB, ratioC);

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));


                           }
                           else
                           {
                               char tempchar[80];
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                               text = string("Non Normal Var Quality = ");
                               _ltoa(getCurrentVarPointQuality(),tempchar,10);
                               text += tempchar;
                               text += " Var: ";
                               text += doubleToString(getCurrentVarLoadPointValue());
                               text += ", CloseQuestionable";
                               additional = string("Feeder: ");
                               additional += currentFeeder->getPAOName();

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

                               currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                           }
                       }
                       else
                       {
                           {
                               CtiLockGuard<CtiLogger> logger_guard(dout);
                               dout << CtiTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                           }
                           if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail)
                           {
                               text = "OpenFail";
                           }
                           else if (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail)
                           {
                               text = "CloseFail";
                           }
                           currentCapBank->setControlStatusQuality(CC_Fail);
                           returnBoolean = FALSE;
                          // break;
                       }
                       if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                           (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
                       {
                           currentCapBank->setRetryOpenFailedFlag(FALSE);
                           currentCapBank->setRetryCloseFailedFlag(FALSE);
                       }
                       setSolution(text);
                       if( currentCapBank->getStatusPointId() > 0 )
                       {

                           if (currentCapBank->getPorterRetFailFlag() && currentCapBank->getIgnoreFlag())
                           {
                                 currentCapBank->setVCtrlIndex(5);
                                 currentCapBank->setVerificationDoneFlag(TRUE);
                                 currentCapBank->setIgnoreFlag(FALSE);
                           }
                           else
                           {
                               if( text.length() > 0 )
                               {//if control failed or questionable, create event to be sent to dispatch
                                   long tempLong = currentCapBank->getStatusPointId();
                                   if (_LOG_MAPID_INFO)
                                   {
                                       additional += " MapID: ";
                                       additional += currentFeeder->getMapLocationId();
                                       additional += " (";
                                       additional += currentFeeder->getPAODescription();
                                       additional += ")";
                                   }
                                   pointChanges.push_back(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent, "cap control verification"));
                                   ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                               }
                               pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                               ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                               currentCapBank->setLastStatusChangeTime(CtiTime());

                               INT actionId = CCEventActionIdGen(currentCapBank->getStatusPointId());
                               string stateInfo = currentCapBank->getControlStatusQualityString();
                               LONG stationId, areaId, spAreaId;
                               store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                               ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId,
                                                                       getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate,
                                                                       getEventSequence(), currentCapBank->getControlStatus(), text,
                                                                       "cap control verification",
                                                                       varAValue+varBValue+varCValue,
                                                                       varValueAbc+varValueBbc+varValueCbc,
                                                                       changeA+changeB+changeC,
                                                                       currentCapBank->getIpAddress(),
                                                                        actionId, stateInfo,
                                                                       getPhaseAValue(), getPhaseBValue(), getPhaseCValue()));
                           }
                           currentCapBank->setPorterRetFailFlag(FALSE);

                       }
                       else
                       {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                           << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                       }
                       currentCapBank->setControlRecentlySentFlag(FALSE);
                       if (currentCapBank->updateVerificationState())
                       {
                           returnBoolean = TRUE;
                           currentCapBank->setPerformingVerificationFlag(FALSE);
                           currentFeeder->setPerformingVerificationFlag(FALSE);
                           setBusUpdatedFlag(TRUE);
                           return returnBoolean;
                       }
                       foundCap = TRUE;
                       break;
                   }
                }
                foundFeeder = TRUE;
                currentFeeder->setRetryIndex(0);
                break;
            }
        }

    }
    if (foundCap == FALSE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Verification Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        returnBoolean = TRUE;
    }
    if (foundFeeder == FALSE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Verification Feeder controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        returnBoolean = TRUE;
    }
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isVarCheckNeeded

    Returns a boolean if the control interval is up or if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isVarCheckNeeded(const CtiTime& currentDateTime)
{
    BOOL returnBoolean = FALSE;

    try
    {

        if( getControlInterval() > 0 || getLikeDayControlFlag() )
        {
            if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                (getSendMoreTimeControlledCommandsFlag() && !getRecentlyControlledFlag()) )
            {
                returnBoolean = TRUE;
            }
            else
            {
                returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
            }
        }
        else
        {
            if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod))
            {
                if( getSendMoreTimeControlledCommandsFlag() && !getRecentlyControlledFlag())
                {
                    returnBoolean = TRUE;
                }
                else
                {
                    returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    //confirm cap bank changes on just the feeder var value
                    if( getRecentlyControlledFlag() )
                    {
                        try
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                            {
                                if( currentFeeder->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = TRUE;
                                }
                            }
                            else
                            {
                                for(LONG i=0;i<_ccfeeders.size();i++)
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                    if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                    {
                                        if( currentFeeder->getNewPointDataReceivedFlag() )
                                        {
                                            returnBoolean = TRUE;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }

                        if( !returnBoolean )
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                            {
                                if( currentFeeder->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = TRUE;
                                }
                            }
                            else
                            {
                                for(LONG i=0;i<_ccfeeders.size();i++)
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                    if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                    {
                                        if( currentFeeder->getNewPointDataReceivedFlag() )
                                        {
                                            returnBoolean = TRUE;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else if (getVerificationFlag() && getPerformingVerificationFlag())
                    {
                        for(LONG i=0;i<_ccfeeders.size();i++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                            }
                            break;
                        }
                    }
                    //only try to control bus optimized if we have new var values
                    //for the bus and all feeders off the bus
                    else if( _newpointdatareceivedflag )
                    {
                        returnBoolean = TRUE;
                        for(LONG i=0;i<_ccfeeders.size();i++)
                        {
                            if( !((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = FALSE;
                                break;
                            }
                        }
                    }
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    for(LONG i=0;i<_ccfeeders.size();i++)
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                        if( (currentFeeder->getStrategyId() > 0  &&
                            currentFeeder->getControlInterval() > 0) || currentFeeder->getLikeDayControlFlag() )
                        {
                            returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
                        }

                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                }
                else if (getVerificationFlag() && getPerformingVerificationFlag())
                {
                    for(LONG i=0;i<_ccfeeders.size();i++)
                    {
                         CtiCCFeeder* currentFeeder =  (CtiCCFeeder*)_ccfeeders[i];

                        if( currentFeeder->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                        }
                        break;
                    }
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::ManualOnlyControlMethod) )
            {
                if( _newpointdatareceivedflag )
                {
                    returnBoolean = TRUE;
                }
                else
                {
                    for(LONG i=0;i<_ccfeeders.size();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Invalid Control Method in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }


    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isConfirmCheckNeeded

    Returns a boolean if the if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isConfirmCheckNeeded()
{
    BOOL returnBoolean = FALSE;

    try
    {

        if( getRecentlyControlledFlag() )
        {
            if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    //confirm cap bank changes on just the feeder var value
                    try
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                        if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                        {
                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                            }
                        }
                        else
                        {
                            for(LONG i=0;i<_ccfeeders.size();i++)
                            {
                                currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                {
                                    if( currentFeeder->getNewPointDataReceivedFlag() )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                    if( !returnBoolean )
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                        if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                        {
                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                            }
                        }
                        else
                        {
                            for(LONG i=0;i<_ccfeeders.size();i++)
                            {
                                currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
                                {
                                    if( currentFeeder->getNewPointDataReceivedFlag() )
                                    {
                                        returnBoolean = TRUE;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    for(LONG i=0;i<_ccfeeders.size();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::ManualOnlyControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                    if( !returnBoolean )
                    {
                        for(LONG i=0;i<_ccfeeders.size();i++)
                        {
                            if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                                break;
                            }
                        }
                    }
                }
            }
            else if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Invalid Control Method in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
        else if (getVerificationFlag())
        {
            if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod)||
                !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    for(LONG i=0;i<_ccfeeders.size();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = TRUE;
                            break;
                        }
                    }
                }
            }
            else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPeakDay(const CtiTime& currentDateTime)
{
    //-------------------------------------
    //Need to check if it is a holiday today
    //also, but we must wait until there is
    //a dll with a function to do this
    //-------------------------------------
    CtiTime now;
    struct tm start_tm;

    currentDateTime.extract(&start_tm);

    if( _daysofweek[start_tm.tm_wday] == 'Y' &&
        ( _daysofweek[7] == 'Y' ||
          !CtiHolidayManager::getInstance().isHoliday(CtiDate()) ) )
        return TRUE;
    else
        return FALSE;
}

/*---------------------------------------------------------------------------
    clearOutNewPointReceivedFlags

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::clearOutNewPointReceivedFlags()
{
    setNewPointDataReceivedFlag(FALSE);
    for(int i=0;i<_ccfeeders.size();i++)
    {
        ((CtiCCFeeder*)_ccfeeders.at(i))->setNewPointDataReceivedFlag(FALSE);
    }
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isAlreadyControlled()
{

    BOOL returnBoolean = FALSE;
    try
    {
        BOOL found = FALSE;
        LONG minConfirmPercent = getMinConfirmPercent();

        if( _IGNORE_NOT_NORMAL_FLAG && getCurrentVarPointQuality() != NormalQuality )
        {
            return returnBoolean;
        }

        if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) ||
            !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( !currentFeeder->getRecentlyControlledFlag() )
                {
                    continue;
                }
                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                }
                if( currentFeeder->isAlreadyControlled(minConfirmPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                               currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                               currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getRegression(),
                                               currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC() ,
                                               currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag() ))
                {
                    returnBoolean = TRUE;
                    break;
                }
            }
        }
        else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::SubstationBusControlMethod) ||
                 !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }

            CtiCCFeeder* currentFeeder = NULL;
            for(LONG i=0;i<=_ccfeeders.size();i++)
            {
                if (i == 0)
                {
                    if (getLastFeederControlledPosition() >= 0)
                        currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());
                    else
                        currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                }
                else
                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i - 1);

                if( currentFeeder->getPAOId() != getLastFeederControlledPAOId() )
                {
                    continue;
                }

                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                }

                returnBoolean = currentFeeder->isAlreadyControlled(minConfirmPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                           getPhaseBValueBeforeControl(), getPhaseCValueBeforeControl(),
                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue(),
                                           getVarValueBeforeControl(), getCurrentVarLoadPointValue(), regression, regressionA, regressionB, regressionC,
                                           getUsePhaseData(), getTotalizedControlFlag()  );

                break;
            }

        }
        else if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::ManualOnlyControlMethod) )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    continue;
                }
                if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                { //use feeder values
                    if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getMinConfirmPercent();
                    }
                    if( currentFeeder->isAlreadyControlled(minConfirmPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                               currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                               currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(),
                                               currentFeeder->getRegression(), currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC(),
                                               currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag()  ))
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
                else
                { // use substation values.
                    if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getMinConfirmPercent();
                    }
                    if( currentFeeder->isAlreadyControlled(minConfirmPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                               getPhaseBValueBeforeControl(), getPhaseCValueBeforeControl(),
                                               getPhaseAValue(), getPhaseBValue(), getPhaseCValue(),
                                               getVarValueBeforeControl(), getCurrentVarLoadPointValue(), regression, regressionA, regressionB, regressionC,
                                               getUsePhaseData(), getTotalizedControlFlag()  ))
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return returnBoolean;

}


CtiCCSubstationBus& CtiCCSubstationBus::getNextCapBankToVerify(CtiMultiMsg_vec& ccEvents)
{
    _currentVerificationFeederId = -1;
    _currentVerificationCapBankId = -1;

    //_currentCapBankToVerifyId = (LONG) _verificationCapBankIds.back();
    //_verificationCapBankIds.pop_back();

    if (getOverlappingVerificationFlag())
    {
        setCapBanksToVerifyFlags(getVerificationStrategy(), ccEvents);
    }

    for(LONG i=0;i<_ccfeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

        if( currentFeeder->getVerificationFlag() && !currentFeeder->getVerificationDoneFlag() )
        {
            _currentVerificationFeederId = currentFeeder->getPAOId();

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getVerificationFlag() && !currentCapBank->getVerificationDoneFlag() )
                {
                    _currentVerificationCapBankId = currentCapBank->getPAOId();
                    return *this;
                }
            }
            currentFeeder->setVerificationDoneFlag(TRUE);
            _currentVerificationFeederId = -1;
        }
    }
    setBusUpdatedFlag(TRUE);

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setVerificationFlag(BOOL verificationFlag)
{

    if( _verificationFlag != verificationFlag )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _verificationFlag = verificationFlag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setPerformingVerificationFlag(BOOL performingVerificationFlag)
{
    if( _performingVerificationFlag != performingVerificationFlag )
    {
        _dirty = TRUE;
    }

    _performingVerificationFlag = performingVerificationFlag;

    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setVerificationDoneFlag(BOOL verificationDoneFlag)
{
    if( _verificationDoneFlag != verificationDoneFlag )
    {
        _dirty = TRUE;
    }

    _verificationDoneFlag = verificationDoneFlag;

    return *this;
}


CtiCCSubstationBus& CtiCCSubstationBus::setOverlappingVerificationFlag(BOOL overlapFlag)
{
    if( _overlappingSchedulesVerificationFlag != overlapFlag )
    {
        _dirty = TRUE;
    }

    _overlappingSchedulesVerificationFlag = overlapFlag;

    return *this;
}


CtiCCSubstationBus& CtiCCSubstationBus::setPreOperationMonitorPointScanFlag( BOOL flag)
{
    if( _preOperationMonitorPointScanFlag != flag )
    {
        _dirty = TRUE;
    }

    _preOperationMonitorPointScanFlag = flag;

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setOperationSentWaitFlag( BOOL flag)
{
    if( _operationSentWaitFlag != flag )
    {
        _dirty = TRUE;
    }

    _operationSentWaitFlag = flag;

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setPostOperationMonitorPointScanFlag( BOOL flag)
{
    if( _postOperationMonitorPointScanFlag != flag )
    {
        _dirty = TRUE;
    }

    _postOperationMonitorPointScanFlag = flag;

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setWaitForReCloseDelayFlag(BOOL flag)
{
    if (_waitForReCloseDelayFlag != flag)
    {
        _dirty = TRUE;
    }
    _waitForReCloseDelayFlag = flag;

    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setWaitToFinishRegularControlFlag(BOOL flag)
{
    if (_waitToFinishRegularControlFlag != flag)
    {
        _dirty = TRUE;
    }
    _waitToFinishRegularControlFlag = flag;
    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setMaxDailyOpsHitFlag(BOOL flag)
{
    if (_maxDailyOpsHitFlag != flag)
    {
        _dirty = TRUE;
    }
    _maxDailyOpsHitFlag = flag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setOvUvDisabledFlag(BOOL flag)
{
    if (_ovUvDisabledFlag != flag)
    {
        _dirty = TRUE;
    }
    _ovUvDisabledFlag = flag;
    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setCorrectionNeededNoBankAvailFlag(BOOL flag)
{
    if (_correctionNeededNoBankAvailFlag != flag)
    {
        _dirty = TRUE;
    }
    _correctionNeededNoBankAvailFlag = flag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setLikeDayControlFlag(BOOL flag)
{
    if (_likeDayControlFlag != flag)
    {
        _dirty = TRUE;
    }
    _likeDayControlFlag = flag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setVoltReductionControlId(LONG pointid)
{
    _voltReductionControlId = pointid;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setVoltReductionFlag(BOOL flag)
{
    if (_voltReductionFlag != flag)
    {
        _dirty = TRUE;
    }
    _voltReductionFlag = flag;
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::setSendMoreTimeControlledCommandsFlag(BOOL flag)
{
    if (_sendMoreTimeControlledCommandsFlag != flag)
    {
        _dirty = TRUE;
    }
    _sendMoreTimeControlledCommandsFlag = flag;
    return *this;
}



CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVerificationFeederId(LONG feederId)
{
    if( _currentVerificationFeederId != feederId )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _currentVerificationFeederId = feederId;

    return *this;
}


CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVerificationCapBankId(LONG capBankId)
{
    if( _currentVerificationCapBankId != capBankId )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _currentVerificationCapBankId = capBankId;

    return *this;
}
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVerificationCapBankState(LONG status)
{
    if( _currentCapBankToVerifyAssumedOrigState != status )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _currentCapBankToVerifyAssumedOrigState = status;

    return *this;
}




BOOL CtiCCSubstationBus::sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    BOOL retVal = TRUE;
    CtiRequestMsg* request = NULL;
    for (LONG i = 0; i < _ccfeeders.size(); i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*) _ccfeeders.at(i);
        if( currentFeeder->getPAOId() == _currentVerificationFeederId )
        {
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getPAOId() == _currentVerificationCapBankId )
                {
                    DOUBLE controlValue;
                    DOUBLE confirmValue;
                    DOUBLE phaseAValue = currentFeeder->getPhaseAValue();
                    DOUBLE phaseBValue = currentFeeder->getPhaseBValue();
                    DOUBLE phaseCValue = currentFeeder->getPhaseCValue();

                    if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
                    {
                        controlValue = (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) ? currentFeeder->getCurrentVoltLoadPointValue() : currentFeeder->getCurrentVarLoadPointValue());
                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else if ( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod))
                    {
                        controlValue = (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : currentFeeder->getCurrentVarLoadPointValue());
                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else
                    {
                        controlValue = (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        confirmValue = getCurrentVarLoadPointValue();
                        phaseAValue = getPhaseAValue();
                        phaseBValue = getPhaseBValue();
                        phaseCValue = getPhaseCValue();

                    }


                    if (currentCapBank->getVCtrlIndex() == 1)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ***WARNING*** Adjusting VerificationControlIndex! setting vCtrlIdx = 2. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        currentCapBank->setVCtrlIndex(2);
                    }
                    else if (currentCapBank->getVCtrlIndex() == 2)
                    {
                        if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                        {
                            int control =  CtiCCCapBank::Open;
                            if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&_USE_FLIP_FLAG == TRUE )
                            {
                                control = 4; //flip
                            }
                            currentFeeder->setEventSequence(getEventSequence());
                            string text = currentFeeder->createTextString(getControlMethod(), control, controlValue, confirmValue) ;
                            request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, confirmValue,
                                                                                          phaseAValue, phaseBValue,phaseCValue);

                        }
                        else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                        {
                            //check capbank reclose delay here...
                            if( currentCapBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + currentCapBank->getRecloseDelay() )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Can Not Close Cap Bank: " << currentCapBank->getPAOName() << " yet...because it has not passed its reclose delay." << endl;
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    dout << " Last Status Change Time: " << currentCapBank->getLastStatusChangeTime() << endl;
                                    dout << " Reclose Delay:           " << currentCapBank->getRecloseDelay() << endl;
                                    dout << " Current Date Time:       " << currentDateTime << endl;
                                }
                                retVal = FALSE;
                            }
                            else
                            {

                                int control =  CtiCCCapBank::Close;
                                if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE )
                                {
                                    control = 4; //flip
                                }
                                currentFeeder->setEventSequence(getEventSequence());
                                string text = currentFeeder->createTextString(getControlMethod(), control, controlValue, confirmValue) ;
                                request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, confirmValue,
                                                                                              phaseAValue, phaseBValue,phaseCValue);

                            }
                        }

                    }
                    else if (currentCapBank->getVCtrlIndex() == 3)
                    {
                        if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                        {
                            //check capbank reclose delay here...
                            if( currentCapBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + currentCapBank->getRecloseDelay() )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Can Not Close Cap Bank: " << currentCapBank->getPAOName() << " yet...because it has not passed its reclose delay." << endl;
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    dout << " Last Status Change Time: " << currentCapBank->getLastStatusChangeTime() << endl;
                                    dout << " Reclose Delay:           " << currentCapBank->getRecloseDelay() << endl;
                                    dout << " Current Date Time:       " << currentDateTime << endl;
                                }
                                retVal = FALSE;
                            }
                            else
                            {
                                int control =  CtiCCCapBank::Close;
                                if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE )
                                {
                                    control = 4; //flip
                                }
                                currentFeeder->setEventSequence(getEventSequence());
                                string text = currentFeeder->createTextString(getControlMethod(), control, controlValue, confirmValue) ;
                                request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, confirmValue,
                                                                                               phaseAValue, phaseBValue,phaseCValue);

                            }
                        }
                        else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                        {
                            int control =  CtiCCCapBank::Open;
                            if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE )
                            {
                                control = 4; //flip
                            }
                            currentFeeder->setEventSequence(getEventSequence());
                            string text = currentFeeder->createTextString(getControlMethod(), control, controlValue, confirmValue) ;
                            request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, confirmValue,
                                                                                           phaseAValue, phaseBValue,phaseCValue);

                        }

                    }
                    else if (currentCapBank->getVCtrlIndex() == 5)
                    {
                        request = NULL;
                    }
                    if( request != NULL )
                    {
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        setLastFeederControlledPAOId(currentFeeder->getPAOId());
                        setLastFeederControlledPosition(i);
                        currentFeeder->setLastCapBankControlledDeviceId( currentCapBank->getPAOId());
                        currentFeeder->setLastOperationTime(currentDateTime);
                       ((CtiCCFeeder*)_ccfeeders.at(i))->setLastOperationTime(currentDateTime);
                        setVarValueBeforeControl(getCurrentVarLoadPointValue());
                        setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        figureEstimatedVarLoadPointValue();
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }

                        //setRecentlyControlledFlag(TRUE);
                        setBusUpdatedFlag(TRUE);
                        return retVal;
                    }

                }
            }

        }
    }

    return retVal;
}

CtiCCSubstationBus& CtiCCSubstationBus::startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    //get CapBank to perform verification on...subbus stores, currentCapBankToVerifyId

    CtiRequestMsg* request = NULL;

    for (LONG i = 0; i < _ccfeeders.size(); i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*) _ccfeeders.at(i);

        if( currentFeeder->getPAOId() == _currentVerificationFeederId )
        {
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getPAOId() == _currentVerificationCapBankId )
                {

                    currentCapBank->initVerificationControlStatus();
                    setCurrentVerificationCapBankState(currentCapBank->getAssumedOrigVerificationState());
                    currentCapBank->setAssumedOrigVerificationState(getCurrentVerificationCapBankOrigState());
                    currentCapBank->setPreviousVerificationControlStatus(-1);
                    currentCapBank->setVCtrlIndex(1); //1st control sent
                    currentCapBank->setPerformingVerificationFlag(TRUE);
                    currentFeeder->setPerformingVerificationFlag(TRUE);
                    currentFeeder->setCurrentVerificationCapBankId(_currentVerificationCapBankId);

                    DOUBLE controlValue;
                    DOUBLE confirmValue;
                    DOUBLE phaseAValue = currentFeeder->getPhaseAValue();
                    DOUBLE phaseBValue = currentFeeder->getPhaseBValue();
                    DOUBLE phaseCValue = currentFeeder->getPhaseCValue();


                    if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
                    {
                        controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? currentFeeder->getCurrentVoltLoadPointValue() : currentFeeder->getCurrentVarLoadPointValue());
                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else if ( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod))
                    {
                        controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : currentFeeder->getCurrentVarLoadPointValue());
                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else
                    {
                        controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        confirmValue = getCurrentVarLoadPointValue();
                        phaseAValue = getPhaseAValue();
                        phaseBValue = getPhaseBValue();
                        phaseCValue = getPhaseCValue();

                    }


                    if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                    {

                        //add capbank reclose delay check here...
                        int control =  CtiCCCapBank::Close;
                        if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE )
                        {
                            control = 4; //flip
                        }
                        currentFeeder->setEventSequence(getEventSequence());
                        string text = currentFeeder->createTextString(getControlMethod(), control, controlValue, confirmValue) ;
                        request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, confirmValue,
                                                                                           phaseAValue, phaseBValue,phaseCValue);
                    }
                    else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                    {
                        int control =  CtiCCCapBank::Open;
                        if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE )
                        {
                            control = 4; //flip
                        }
                        currentFeeder->setEventSequence(getEventSequence());
                        string text = currentFeeder->createTextString(getControlMethod(), control, controlValue, confirmValue) ;
                        request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, confirmValue,
                                                                                           phaseAValue, phaseBValue,phaseCValue);
                    }


                    if( request != NULL )
                    {
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        setLastFeederControlledPAOId(currentFeeder->getPAOId());
                        setLastFeederControlledPosition(i);
                        currentFeeder->setLastCapBankControlledDeviceId( currentCapBank->getPAOId());
                        currentFeeder->setLastOperationTime(currentDateTime);
                        setVarValueBeforeControl(getCurrentVarLoadPointValue());
                        setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        figureEstimatedVarLoadPointValue();
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                    }
                    //setNewPointDataReceivedFlag(FALSE);
                    //regardless what happened the substation bus should be should be sent to the client
                    setBusUpdatedFlag(TRUE);
                    return *this;
                }
            }
        }
    }


    return *this;
}
/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isVerificationAlreadyControlled()
{
    BOOL returnBoolean = FALSE;
    BOOL foundCap = FALSE;
    BOOL foundFeeder = FALSE;
    LONG minConfirmPercent = getMinConfirmPercent();

    if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
    {
        if( (!stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) ||
            !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod)) &&
            _ALLOW_PARALLEL_TRUING )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            for(LONG i = 0; i < _ccfeeders.size(); i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( !currentFeeder->getPerformingVerificationFlag() )
                {
                    continue;
                }
                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                }
                foundFeeder = TRUE;
                if( currentFeeder->isVerificationAlreadyControlled(minConfirmPercent,
                                                                   currentFeeder->getCurrentVarPointQuality(),
                                                                   currentFeeder->getPhaseAValueBeforeControl(),currentFeeder->getPhaseBValueBeforeControl(),
                                                                   currentFeeder->getPhaseCValueBeforeControl(),currentFeeder->getPhaseAValue(),
                                                                   currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                                                   currentFeeder->getVarValueBeforeControl(),
                                                                   currentFeeder->getCurrentVarLoadPointValue(),
                                                                   currentFeeder->getUsePhaseData(),
                                                                   currentFeeder->getTotalizedControlFlag() )  )
                {
                    returnBoolean = TRUE;
                    break;
                }
            }
            if (foundFeeder == FALSE)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Verification Feeder NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = TRUE;
            }

        }
        else if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::SubstationBusControlMethod) ||
                 !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) ||
                 !_ALLOW_PARALLEL_TRUING)
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            long quality = getCurrentVarPointQuality();
            double oldCalcValue = getVarValueBeforeControl();
            DOUBLE newCalcValue = getCurrentVarLoadPointValue();
            DOUBLE beforeAValue = getPhaseAValueBeforeControl();
            DOUBLE beforeBValue = getPhaseBValueBeforeControl();
            DOUBLE beforeCValue = getPhaseCValueBeforeControl();
            DOUBLE phaseAValue  = getPhaseAValue();
            DOUBLE phaseBValue  = getPhaseBValue();
            DOUBLE phaseCValue  = getPhaseCValue();
            BOOL usePhaseData   = getUsePhaseData();
            BOOL totalizedControlFlag = getTotalizedControlFlag();

            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                if( currentFeeder->getPAOId() != getCurrentVerificationFeederId() )
                {
                    continue;
                }

                if (stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getMinConfirmPercent();
                }
                if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) ||
                    !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
                    ( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                      currentFeeder->getCurrentVarLoadPointId() > 0 && !currentFeeder->getUsePhaseData() ) ||
                    ( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod) &&
                      currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0 &&
                      currentFeeder->getPhaseCId() > 0 && currentFeeder->getUsePhaseData() ) )
                {

                    quality = currentFeeder->getCurrentVarPointQuality();
                    oldCalcValue = currentFeeder->getVarValueBeforeControl();
                    newCalcValue = currentFeeder->getCurrentVarLoadPointValue();
                    beforeAValue = currentFeeder->getPhaseAValueBeforeControl();
                    beforeBValue = currentFeeder->getPhaseBValueBeforeControl();
                    beforeCValue = currentFeeder->getPhaseCValueBeforeControl();
                    phaseAValue = currentFeeder->getPhaseAValue();
                    phaseBValue = currentFeeder->getPhaseBValue();
                    phaseCValue = currentFeeder->getPhaseCValue();
                    usePhaseData = currentFeeder->getUsePhaseData();
                    totalizedControlFlag = currentFeeder->getTotalizedControlFlag();

                }


                if( currentFeeder->isVerificationAlreadyControlled(minConfirmPercent,quality,
                                                                   beforeAValue,
                                                                   beforeBValue,
                                                                   beforeCValue,
                                                                   phaseAValue,
                                                                   phaseBValue,
                                                                   phaseCValue,
                                                                   oldCalcValue,
                                                                   newCalcValue,
                                                                   usePhaseData,
                                                                   totalizedControlFlag) )
                {
                    returnBoolean = TRUE;
                    break;
                }
                
            }
        }
        else
        {
            returnBoolean = isAlreadyControlled();
        }
    }
    return returnBoolean;
}

CtiCCSubstationBus& CtiCCSubstationBus::analyzeVerificationByFeeder(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, CtiMultiMsg_vec& capMessages)
{
    BOOL verifyCapFound = FALSE;



    for(LONG i=0;i<_ccfeeders.size();i++)
    {
        LONG minConfirmPercent =  getMinConfirmPercent();
        LONG maxConfirmTime =  getMaxConfirmTime();
        LONG sendRetries = getControlSendRetries();
        LONG failPercent = getFailurePercent();

        CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

        if (stringCompareIgnoreCase(currentCCFeeder->getStrategyName(),"(none)"))
        {
            minConfirmPercent = currentCCFeeder->getMinConfirmPercent();
            maxConfirmTime = currentCCFeeder->getMaxConfirmTime();
            sendRetries = currentCCFeeder->getControlSendRetries();
            failPercent = currentCCFeeder->getFailurePercent();
        }

        if (currentCCFeeder->getPerformingVerificationFlag())
        {
            if (currentCCFeeder->isVerificationAlreadyControlled(minConfirmPercent, currentCCFeeder->getCurrentVarPointQuality(),
                                                                   currentCCFeeder->getPhaseAValueBeforeControl(),
                                                                   currentCCFeeder->getPhaseBValueBeforeControl(),
                                                                   currentCCFeeder->getPhaseCValueBeforeControl(),
                                                                   currentCCFeeder->getPhaseAValue(),
                                                                   currentCCFeeder->getPhaseBValue(),
                                                                   currentCCFeeder->getPhaseCValue(),
                                                                   currentCCFeeder->getVarValueBeforeControl(),
                                                                   currentCCFeeder->getCurrentVarLoadPointValue(),
                                                                   currentCCFeeder->getUsePhaseData(),
                                                                   currentCCFeeder->getTotalizedControlFlag())
                 || currentCCFeeder->isPastMaxConfirmTime(currentDateTime,maxConfirmTime,sendRetries))
            {

                if ( getControlSendRetries() > 0 &&
                     !currentCCFeeder->isVerificationAlreadyControlled(minConfirmPercent,
                                                                       currentCCFeeder->getCurrentVarPointQuality(),
                                                                       currentCCFeeder->getPhaseAValueBeforeControl(),
                                                                       currentCCFeeder->getPhaseBValueBeforeControl(),
                                                                       currentCCFeeder->getPhaseCValueBeforeControl(),
                                                                       currentCCFeeder->getPhaseAValue(),
                                                                       currentCCFeeder->getPhaseBValue(),
                                                                       currentCCFeeder->getPhaseCValue(),
                                                                       currentCCFeeder->getVarValueBeforeControl(),
                                                                       currentCCFeeder->getCurrentVarLoadPointValue(),
                                                                       currentCCFeeder->getUsePhaseData(),
                                                                       currentCCFeeder->getTotalizedControlFlag()) &&
                    currentDateTime.seconds() < currentCCFeeder->getLastOperationTime().seconds() + maxConfirmTime)
                {
                    if(currentCCFeeder->checkForAndPerformVerificationSendRetry(currentDateTime, pointChanges, ccEvents, pilMessages, maxConfirmTime, sendRetries))
                    {
                        setLastOperationTime(currentDateTime);
                        setBusUpdatedFlag(TRUE);
                    }
                    verifyCapFound = TRUE;
                }
                else if (currentCCFeeder->getWaitForReCloseDelayFlag() ||
                         (!currentCCFeeder->capBankVerificationStatusUpdate(pointChanges, ccEvents, minConfirmPercent, failPercent,
                                                                            currentCCFeeder->getPhaseAValue(),
                                                                            currentCCFeeder->getPhaseBValue(),
                                                                            currentCCFeeder->getPhaseCValue()) &&
                         currentCCFeeder->getCurrentVerificationCapBankId() != -1) )
                {
                    if (currentCCFeeder->sendNextCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages))
                    {
                        setBusUpdatedFlag(TRUE);
                        currentCCFeeder->setWaitForReCloseDelayFlag(FALSE);
                    }
                    else
                        currentCCFeeder->setWaitForReCloseDelayFlag(TRUE);

                    verifyCapFound = TRUE;

                }
                else
                {
                    if (currentCCFeeder->areThereMoreCapBanksToVerify())
                    {
                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                        {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << getPAOName()<< "( "<<getPAOId()<<" ) FEED-"<<currentCCFeeder->getPAOName()<<" CB-"<<currentCCFeeder->getCurrentVerificationCapBankId() << endl;
                        }

                        currentCCFeeder->setEventSequence(getEventSequence());
                        currentCCFeeder->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                        verifyCapFound = TRUE;
                    }


                    setBusUpdatedFlag(TRUE);
                    setPerformingVerificationFlag(TRUE);
                }
            }
            else //WAIT
            {
                verifyCapFound = TRUE;
            }
        }
        else //if (!currentCCFeeder->getPerformingVerificationFlag())
        {
            if (currentCCFeeder->areThereMoreCapBanksToVerify())
            {
                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << getPAOName()<< "( "<<getPAOId()<<" )  FEED-"<<currentCCFeeder->getPAOName()<<" CB-"<<currentCCFeeder->getCurrentVerificationCapBankId() << endl;
                }

                currentCCFeeder->setEventSequence(getEventSequence());
                currentCCFeeder->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                setPerformingVerificationFlag(TRUE);
                setBusUpdatedFlag(TRUE);
                verifyCapFound = TRUE;
            }
        }

    }
    if (!verifyCapFound)
    {
        setVerificationDoneFlag(TRUE);
        setPerformingVerificationFlag(FALSE);
        setBusUpdatedFlag(TRUE);

        setVerificationFlag(FALSE);
        capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, getPAOId(),0, -1));
        capMessages.push_back(new CtiCCCommand(CtiCCCommand::ENABLE_SUBSTATION_BUS, getPAOId()));

        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
           CtiLockGuard<CtiLogger> logger_guard(dout);
           dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<getPAOName() << "( "<<getPAOId()<<" ) "<< endl;
        }
    }

    return *this;
}


/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPastMaxConfirmTime(const CtiTime& currentDateTime)
{
    BOOL returnBoolean = FALSE;
    try
    {

        if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) &&
            _ALLOW_PARALLEL_TRUING)
        {
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( (currentCCFeeder->getRecentlyControlledFlag()  || currentCCFeeder->getPerformingVerificationFlag()) &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) )
                {
                    returnBoolean = TRUE;
                    break;
                }
            }
        }
        else
        {
            //Check feeder strategy's control send retry values
            //Use feeder's value if strategy control send retry is populated...
            LONG sendRetries = getControlSendRetries();

            if (getLastFeederControlledSendRetries() > 0)
                sendRetries = getLastFeederControlledSendRetries();

            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( (currentCCFeeder->getRecentlyControlledFlag() || currentCCFeeder->getPerformingVerificationFlag()) &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),sendRetries) )
                {
                    returnBoolean = TRUE;
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    checkForAndPerformSendRetry

    Returns boolean if .
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::checkForAndPerformSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    BOOL returnBoolean = FALSE;

    try
    {

        if( !stringCompareIgnoreCase(_controlmethod, CtiCCSubstationBus::IndividualFeederControlMethod) )
        {
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                currentCCFeeder->setEventSequence(getEventSequence());
                if( currentCCFeeder->getRecentlyControlledFlag() &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
                    currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getMaxConfirmTime()) )
                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = TRUE;
                    break;
                }
                else if (getVerificationFlag() && getPerformingVerificationFlag() &&
                         currentCCFeeder->getPAOId() == getCurrentVerificationFeederId() &&
                         currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
                         currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getMaxConfirmTime()) )
                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = TRUE;
                    break;
                }

            }
        }
        else
        {
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                currentCCFeeder->setEventSequence(getEventSequence());
                if( currentCCFeeder->getPAOId() == getLastFeederControlledPAOId() &&
                    currentCCFeeder->getRecentlyControlledFlag() &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
                    currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getMaxConfirmTime()) )
                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = TRUE;
                    break;
                }
                else if (getVerificationFlag() && getPerformingVerificationFlag() &&
                         currentCCFeeder->getPAOId() == getCurrentVerificationFeederId() &&
                         currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
                         currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getMaxConfirmTime()) )

                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = TRUE;
                    break;
                }

            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return returnBoolean;
}


BOOL CtiCCSubstationBus::checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
   BOOL returnBoolean = FALSE;

   for(LONG i=0;i<_ccfeeders.size();i++)
   {
       CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
       if (getVerificationFlag() && getPerformingVerificationFlag() &&
           currentCCFeeder->getPAOId() == getCurrentVerificationFeederId() &&
           currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) &&
           currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getMaxConfirmTime()) )
       {
           if (_RETRY_ADJUST_LAST_OP_TIME)
           {
               currentCCFeeder->setLastOperationTime(currentDateTime);
               setLastOperationTime(currentDateTime);
           }
           currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
           returnBoolean = TRUE;
           break;
       }

   }
   return returnBoolean;
}


/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {
        RWDBTable dynamicCCSubstationBusTable = getDatabase().table( "dynamicccsubstationbus" );

        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCSubstationBusTable.updater();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["currentvarpointvalue"].assign( _currentvarloadpointvalue )
            << dynamicCCSubstationBusTable["currentwattpointvalue"].assign( _currentwattloadpointvalue )
            << dynamicCCSubstationBusTable["nextchecktime"].assign( toRWDBDT(_nextchecktime) )
            << dynamicCCSubstationBusTable["newpointdatareceivedflag"].assign( ((_newpointdatareceivedflag?"Y":"N")) )
            << dynamicCCSubstationBusTable["busupdatedflag"].assign( ((_busupdatedflag?"Y":"N")) );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }

            updater.clear();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["lastcurrentvarupdatetime"].assign( toRWDBDT(_lastcurrentvarpointupdatetime) )
            << dynamicCCSubstationBusTable["estimatedvarpointvalue"].assign( _estimatedvarloadpointvalue )
            << dynamicCCSubstationBusTable["currentdailyoperations"].assign( _currentdailyoperations )
            << dynamicCCSubstationBusTable["peaktimeflag"].assign( ((_peaktimeflag?"Y":"N")) );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << updater.asString().c_str() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }

            updater.clear();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["recentlycontrolledflag"].assign( ((_recentlycontrolledflag?"Y":"N")) )
            << dynamicCCSubstationBusTable["lastoperationtime"].assign( toRWDBDT(_lastoperationtime ))
            << dynamicCCSubstationBusTable["varvaluebeforecontrol"].assign( _varvaluebeforecontrol )
            << dynamicCCSubstationBusTable["lastfeederpaoid"].assign( _lastfeedercontrolledpaoid );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << updater.asString().c_str() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_verificationFlag?'Y':'N');
            addFlags[1] = (_performingVerificationFlag?'Y':'N');
            addFlags[2] = (_verificationDoneFlag?'Y':'N');
            addFlags[3] = (_overlappingSchedulesVerificationFlag?'Y':'N');
            addFlags[4] = (_preOperationMonitorPointScanFlag?'Y':'N');
            addFlags[5] = (_operationSentWaitFlag?'Y':'N');
            addFlags[6] = (_postOperationMonitorPointScanFlag?'Y':'N');
            addFlags[7] = (_reEnableBusFlag?'Y':'N');
            addFlags[8] = (_waitForReCloseDelayFlag?'Y':'N');
            addFlags[9] = (_waitToFinishRegularControlFlag?'Y':'N');
            addFlags[10] = (_maxDailyOpsHitFlag?'Y':'N');
            addFlags[11] = (_ovUvDisabledFlag?'Y':'N');
            addFlags[12] = (_correctionNeededNoBankAvailFlag?'Y':'N');
            addFlags[13] = (_likeDayControlFlag?'Y':'N');
            addFlags[14] = (_voltReductionFlag?'Y':'N');
            addFlags[15] = (_sendMoreTimeControlledCommandsFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags) + char2string(*(addFlags+1)) + char2string(*(addFlags+2))+
                                         char2string(*(addFlags+3)) + char2string(*(addFlags+4)) +  char2string(*(addFlags+5)) +
                                         char2string(*(addFlags+6)) + char2string(*(addFlags+7)) + char2string(*(addFlags+8)) +
                                         char2string(*(addFlags+9)) + char2string(*(addFlags+10)) + char2string(*(addFlags+11)) +
                                         char2string(*(addFlags+12)) +char2string(*(addFlags+13)) +char2string(*(addFlags+14))
                                        +char2string(*(addFlags+15)));
            _additionalFlags.append("NNNN");

            updater.clear();

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==_paoid);

            updater << dynamicCCSubstationBusTable["lastfeederposition"].assign( _lastfeedercontrolledposition )
            << dynamicCCSubstationBusTable["ctitimestamp"].assign(toRWDBDT(currentDateTime))
            << dynamicCCSubstationBusTable["powerfactorvalue"].assign( _powerfactorvalue )
            << dynamicCCSubstationBusTable["kvarsolution"].assign( _kvarsolution )
            << dynamicCCSubstationBusTable["estimatedpfvalue"].assign( _estimatedpowerfactorvalue )
            << dynamicCCSubstationBusTable["currentvarpointquality"].assign( _currentvarpointquality )
            << dynamicCCSubstationBusTable["waivecontrolflag"].assign( (_waivecontrolflag?"Y":"N"))
            << dynamicCCSubstationBusTable["additionalflags"].assign( string2RWCString(_additionalFlags) )
            << dynamicCCSubstationBusTable["currverifycbid"].assign( _currentVerificationCapBankId )
            << dynamicCCSubstationBusTable["currverifyfeederid"].assign( _currentVerificationFeederId )
            << dynamicCCSubstationBusTable["currverifycborigstate"].assign( _currentCapBankToVerifyAssumedOrigState )
            << dynamicCCSubstationBusTable["verificationstrategy"].assign( _verificationStrategy )
            << dynamicCCSubstationBusTable["cbinactivitytime"].assign( _capBankToVerifyInactivityTime )
            << dynamicCCSubstationBusTable["currentvoltpointvalue"].assign( _currentvoltloadpointvalue )
            << dynamicCCSubstationBusTable["switchPointStatus"].assign( _switchOverStatus )
            << dynamicCCSubstationBusTable["altSubControlValue"].assign( _altSubControlValue )
            << dynamicCCSubstationBusTable["eventSeq"].assign( _eventSeq )
            << dynamicCCSubstationBusTable["currentwattpointquality"].assign( _currentwattpointquality )
            << dynamicCCSubstationBusTable["currentvoltpointquality"].assign( _currentvoltpointquality )
            << dynamicCCSubstationBusTable["ivcontroltot"].assign( _iVControlTot )
            << dynamicCCSubstationBusTable["ivcount"].assign( _iVCount )
            << dynamicCCSubstationBusTable["iwcontroltot"].assign( _iWControlTot )
            << dynamicCCSubstationBusTable["iwcount"].assign( _iWCount )
            << dynamicCCSubstationBusTable["phaseavalue"].assign( _phaseAvalue )
            << dynamicCCSubstationBusTable["phasebvalue"].assign( _phaseBvalue )
            << dynamicCCSubstationBusTable["phasecvalue"].assign( _phaseCvalue )
            << dynamicCCSubstationBusTable["lastwattpointtime"].assign( toRWDBDT((CtiTime)_lastWattPointTime) )
            << dynamicCCSubstationBusTable["lastvoltpointtime"].assign( toRWDBDT((CtiTime)_lastVoltPointTime) );

            ;

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << updater.asString().c_str() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted substation bus into DynamicCCSubstationBus: " << getPAOName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            RWDBInserter inserter = dynamicCCSubstationBusTable.inserter();
            //TS FLAG
            inserter << _paoid
            << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << _nextchecktime
            << ((_newpointdatareceivedflag?"Y":"N"))
            << ((_busupdatedflag?"Y":"N"))
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << ((_peaktimeflag?"Y":"N"))
            << ((_recentlycontrolledflag?"Y":"N"))
            << _lastoperationtime
            << _varvaluebeforecontrol
            << _lastfeedercontrolledpaoid
            << _lastfeedercontrolledposition
            << currentDateTime
            << _powerfactorvalue
            << _kvarsolution
            << _estimatedpowerfactorvalue
            << _currentvarpointquality
            << string((_waivecontrolflag?"Y":"N"))
            << string2RWCString(addFlags)
            << _currentVerificationCapBankId
            << _currentVerificationFeederId
            << _currentCapBankToVerifyAssumedOrigState
            << _verificationStrategy
            << _capBankToVerifyInactivityTime
            << _currentvoltloadpointvalue
            << _switchOverStatus
            << _altSubControlValue
            << _eventSeq
            << _currentwattpointquality
            << _currentvoltpointquality
            << _iVControlTot
            << _iVCount
            <<  _iWControlTot
            <<  _iWCount
            << _phaseAvalue
            << _phaseBvalue
            << _phaseCvalue
            << _lastWattPointTime
            << _lastVoltPointTime;


            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << inserter.asString().data() << endl;
            }

            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }

        if (getOperationStats().isDirty())
            getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}

/*-------------------------------------------------------------------------
    calculatePowerFactor


--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::calculatePowerFactor(DOUBLE kvar, DOUBLE kw)
{
    DOUBLE newPowerFactorValue = 1.0;
    DOUBLE kva = 0.0;

    kva = sqrt((kw*kw)+(kvar*kvar));

    if( kva != 0.0 )
    {
        if( kw < 0 )
        {
            kw = -kw;
        }
        newPowerFactorValue = kw / kva;
        //check if this is leading
        if( kvar < 0.0 && newPowerFactorValue != 1.0 )
        {
            newPowerFactorValue = 2.0-newPowerFactorValue;
        }
    }

    return newPowerFactorValue;
}

/*-------------------------------------------------------------------------
    convertKQToKVAR

    Converts KQ to KVAR, needs kw also
--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::convertKQToKVAR(DOUBLE kq, DOUBLE kw)
{
    DOUBLE returnKVAR = 0.0;
    returnKVAR = ((2.0*kq)-kw)/SQRT3;
    return returnKVAR;
}

/*-------------------------------------------------------------------------
    convertKVARToKQ

    Converts KVAR to KQ, needs kw also
--------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::convertKVARToKQ(DOUBLE kvar, DOUBLE kw)
{
    DOUBLE returnKQ = 0.0;
    returnKQ = ((SQRT3*kvar)+kw)/2.0;
    return returnKQ;
}




/////////////////////////////////////////////////////////////////////////////
// LOTS OF WORK TO DO HERE!!!!
/////////////////////////////////////////////////////////////////////////////

BOOL CtiCCSubstationBus::isBusPerformingVerification()
{
    return _performingVerificationFlag;

}
BOOL CtiCCSubstationBus::isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime)
{
    BOOL foundFeeder = FALSE;
    BOOL returnBoolean = FALSE;


    if( (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) ||
        !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) ) &&
        _ALLOW_PARALLEL_TRUING)
    {
         for(LONG i = 0; i < _ccfeeders.size(); i++)
         {
             CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
             //if( currentFeeder->getPAOId() == getCurrentVerificationFeederId() )
             if( currentFeeder->getPerformingVerificationFlag() )
             {
                 foundFeeder = TRUE;
                 if( currentFeeder->isPastMaxConfirmTime(currentDateTime,getMaxConfirmTime(),getControlSendRetries()) )
                 {
                     returnBoolean = TRUE;
                     break;
                 }
             }
         }
         if (foundFeeder == FALSE)
         {
             CtiLockGuard<CtiLogger> logger_guard(dout);
             dout << CtiTime() << " - Verification Feeder NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
             returnBoolean = TRUE;
         }

    }
    else if (isPastMaxConfirmTime(currentDateTime) )
        return TRUE;
    else
        return FALSE;

   return returnBoolean;
}

BOOL CtiCCSubstationBus::areThereMoreCapBanksToVerify(CtiMultiMsg_vec& ccEvents)
{


    getNextCapBankToVerify(ccEvents);
    if (getCurrentVerificationCapBankId() != -1 )//&& !getDisableFlag())
    {
        setPerformingVerificationFlag(TRUE);

        return TRUE;
    }
    else
    {
        for(LONG i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            //currentFeeder->setVerificationFlag(FALSE);
            currentFeeder->setPerformingVerificationFlag(FALSE);
            currentFeeder->setVerificationDoneFlag(TRUE);

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(LONG j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                //currentCapBank->setVerificationFlag(FALSE);
                currentCapBank->setPerformingVerificationFlag(FALSE);
                currentCapBank->setVerificationDoneFlag(TRUE);
            }
        }
        setPerformingVerificationFlag(FALSE);
        setBusUpdatedFlag(TRUE);
        return FALSE;
    }
}



void CtiCCSubstationBus::setVerificationStrategy(int verificationStrategy)
{
    if( _verificationStrategy != verificationStrategy )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _verificationStrategy = verificationStrategy;
}

int CtiCCSubstationBus::getVerificationStrategy(void) const
{
    return _verificationStrategy;
}


void CtiCCSubstationBus::setCapBankInactivityTime(LONG capBankToVerifyInactivityTime)
{
    if( _capBankToVerifyInactivityTime != capBankToVerifyInactivityTime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _capBankToVerifyInactivityTime = capBankToVerifyInactivityTime;
}

LONG CtiCCSubstationBus::getCapBankInactivityTime(void) const
{
    return _capBankToVerifyInactivityTime;
}


CtiCCSubstationBus& CtiCCSubstationBus::setCapBanksToVerifyFlags(int verificationStrategy, CtiMultiMsg_vec& ccEvents)
{
    LONG x, j;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    switch (verificationStrategy)
    {
        //case ALLBANKS:
        case CtiPAOScheduleManager::AllBanks:
        {
            for (x = 0; x < _ccfeeders.size(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
                if (!currentFeeder->getDisableFlag())
                {
                    if (!getOverlappingVerificationFlag())
                    {
                        currentFeeder->setVerificationFlag(TRUE);
                        currentFeeder->setVerificationDoneFlag(FALSE);
                    }
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                        {
                            if (!currentCapBank->getDisableFlag())
                            {
                                if (!getOverlappingVerificationFlag())
                                {
                                    currentCapBank->setVerificationFlag(TRUE);
                                    currentCapBank->setVerificationDoneFlag(FALSE);
                                    currentCapBank->setVCtrlIndex(0);
                                }
                                else
                                {
                                    if (!currentCapBank->getVerificationDoneFlag())
                                    {
                                        currentCapBank->setVerificationFlag(TRUE);
                                        currentCapBank->setVerificationDoneFlag(FALSE);
                                        currentCapBank->setVCtrlIndex(0);
                                        currentFeeder->setVerificationFlag(TRUE);
                                        currentFeeder->setVerificationDoneFlag(FALSE);
                                    }
                                    else
                                    {
                                        currentFeeder->setVerificationDoneFlag(TRUE);
                                    }
                                }
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " scheduled for verification.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlEnableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                            else
                            {
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " Disabled! Will not verify.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlDisableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                        }
                    }
                }
            }
            break;
        }
        case CtiPAOScheduleManager::FailedAndQuestionableBanks:
        {
            for (x = 0; x < _ccfeeders.size(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
                if (!currentFeeder->getDisableFlag())
                {
                    if (!getOverlappingVerificationFlag())
                    {
                        currentFeeder->setVerificationFlag(TRUE);
                        currentFeeder->setVerificationDoneFlag(FALSE);
                    }
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) )
                        {
                            if (!currentCapBank->getDisableFlag())
                            {
                                if (!getOverlappingVerificationFlag())
                                {
                                    currentCapBank->setVerificationFlag(TRUE);
                                    currentCapBank->setVerificationDoneFlag(FALSE);
                                    currentCapBank->setVCtrlIndex(0);
                                }
                                else
                                {
                                    if (!currentCapBank->getVerificationDoneFlag())
                                    {
                                        currentCapBank->setVerificationFlag(TRUE);
                                        currentCapBank->setVerificationDoneFlag(FALSE);
                                        currentCapBank->setVCtrlIndex(0);
                                        currentFeeder->setVerificationFlag(TRUE);
                                        currentFeeder->setVerificationDoneFlag(FALSE);
                                    }
                                    else
                                    {
                                        currentFeeder->setVerificationDoneFlag(TRUE);
                                    }
                                }
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " scheduled for verification.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlEnableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                            else
                            {
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " Disabled! Will not verify.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlDisableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                        }
                    }
                }
            }
            break;
        }

        case CtiPAOScheduleManager::FailedBanks:
        {
            for (x = 0; x < _ccfeeders.size(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);

                if (!currentFeeder->getDisableFlag())
                {
                    if (!getOverlappingVerificationFlag())
                    {
                        currentFeeder->setVerificationFlag(TRUE);
                        currentFeeder->setVerificationDoneFlag(FALSE);
                    }
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            ( currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail ||
                              currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ) )
                        {
                            if (!currentCapBank->getDisableFlag())
                            {
                                if (!getOverlappingVerificationFlag())
                                {
                                    currentCapBank->setVerificationFlag(TRUE);
                                    currentCapBank->setVerificationDoneFlag(FALSE);
                                    currentCapBank->setVCtrlIndex(0);
                                }
                                else
                                {
                                    if (!currentCapBank->getVerificationDoneFlag())
                                    {
                                        currentCapBank->setVerificationFlag(TRUE);
                                        currentCapBank->setVerificationDoneFlag(FALSE);
                                        currentCapBank->setVCtrlIndex(0);
                                        currentFeeder->setVerificationFlag(TRUE);
                                        currentFeeder->setVerificationDoneFlag(FALSE);
                                    }
                                    else
                                    {
                                        currentFeeder->setVerificationDoneFlag(TRUE);
                                    }
                                }

                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " scheduled for verification.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlEnableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                            else
                            {
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " Disabled! Will not verify.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlDisableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                        }
                    }
                }
            }
            break;
        }
        case CtiPAOScheduleManager::QuestionableBanks:
        {
            for (x = 0; x < _ccfeeders.size(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
                if (!currentFeeder->getDisableFlag())
                {
                    if (!getOverlappingVerificationFlag())
                    {
                        currentFeeder->setVerificationFlag(TRUE);
                        currentFeeder->setVerificationDoneFlag(FALSE);
                    }
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            (currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) )
                        {
                            if (!currentCapBank->getDisableFlag())
                            {
                                if (!getOverlappingVerificationFlag())
                                {
                                    currentCapBank->setVerificationFlag(TRUE);
                                    currentCapBank->setVerificationDoneFlag(FALSE);
                                    currentCapBank->setVCtrlIndex(0);
                                }
                                else
                                {
                                    if (!currentCapBank->getVerificationDoneFlag())
                                    {
                                        currentCapBank->setVerificationFlag(TRUE);
                                        currentCapBank->setVerificationDoneFlag(FALSE);
                                        currentCapBank->setVCtrlIndex(0);
                                        currentFeeder->setVerificationFlag(TRUE);
                                        currentFeeder->setVerificationDoneFlag(FALSE);
                                    }
                                    else
                                    {
                                        currentFeeder->setVerificationDoneFlag(TRUE);
                                    }
                                }

                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " scheduled for verification.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlEnableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                            else
                            {
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " Disabled! Will not verify.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlDisableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                        }
                    }
                }
            }
            break;
        }
        case CtiPAOScheduleManager::SelectedForVerificationBanks:
        {
            break;
        }
        case CtiPAOScheduleManager::BanksInactiveForXTime:
        {
            CtiTime currentTime = CtiTime();
            currentTime.now();
            for (x = 0; x < _ccfeeders.size(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
                if (!currentFeeder->getDisableFlag())
                {
                    if (!getOverlappingVerificationFlag())
                    {
                        currentFeeder->setVerificationFlag(TRUE);
                        currentFeeder->setVerificationDoneFlag(FALSE);
                    }
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            ( currentCapBank->getLastStatusChangeTime().seconds() <= ( currentTime.seconds() - getCapBankInactivityTime())) )
                        {
                            if (!currentCapBank->getDisableFlag())
                            {
                                if (!getOverlappingVerificationFlag())
                                {
                                    currentCapBank->setVerificationFlag(TRUE);
                                    currentCapBank->setVerificationDoneFlag(FALSE);
                                    currentCapBank->setVCtrlIndex(0);
                                }
                                else
                                {
                                    if (!currentCapBank->getVerificationDoneFlag())
                                    {
                                        currentCapBank->setVerificationFlag(TRUE);
                                        currentCapBank->setVerificationDoneFlag(FALSE);
                                        currentCapBank->setVCtrlIndex(0);
                                        currentFeeder->setVerificationFlag(TRUE);
                                        currentFeeder->setVerificationDoneFlag(FALSE);
                                    }
                                    else
                                    {
                                        currentFeeder->setVerificationDoneFlag(TRUE);
                                    }
                                }

                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " scheduled for verification.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlEnableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                            else
                            {
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " Disabled! Will not verify.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlDisableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                        }
                    }
                }
            }

            break;
        }
        case CtiPAOScheduleManager::StandAloneBanks:
        {
            for (x = 0; x < _ccfeeders.size(); x++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[x];
                if (!currentFeeder->getDisableFlag())
                {
                    if (!getOverlappingVerificationFlag())
                    {
                        currentFeeder->setVerificationFlag(TRUE);
                        currentFeeder->setVerificationDoneFlag(FALSE);
                    }
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::StandAloneState) )
                        {
                            if (!currentCapBank->getDisableFlag() )
                            {
                                if (!getOverlappingVerificationFlag())
                                {
                                    currentCapBank->setVerificationFlag(TRUE);
                                    currentCapBank->setVerificationDoneFlag(FALSE);
                                    currentCapBank->setVCtrlIndex(0);
                                }
                                else
                                {
                                    if (!currentCapBank->getVerificationDoneFlag())
                                    {
                                        currentCapBank->setVerificationFlag(TRUE);
                                        currentCapBank->setVerificationDoneFlag(FALSE);
                                        currentCapBank->setVCtrlIndex(0);
                                        currentFeeder->setVerificationFlag(TRUE);
                                        currentFeeder->setVerificationDoneFlag(FALSE);
                                    }
                                    else
                                    {
                                        currentFeeder->setVerificationDoneFlag(TRUE);
                                    }
                                }

                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " scheduled for verification.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlEnableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                            else
                            {
                                string textInfo;
                                textInfo += string("CapBank: ");
                                textInfo += currentCapBank->getPAOName();
                                textInfo += " Disabled! Will not verify.";

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPAOId(), currentFeeder->getPAOId(), capControlDisableVerification, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                            }
                        }
                    }
                }
            }
            break;
        }
        default:
            break;
    }
    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout <<" CapBank LIST:  ";
        }
        for (x = 0; x < _ccfeeders.size(); x++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                if (currentCapBank->getVerificationFlag())
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout <<"  " << currentCapBank->getPAOId();
                    }
                }

            }
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout <<endl;
        }
    }


    setBusUpdatedFlag(TRUE);

    return *this;
}




void CtiCCSubstationBus::updatePointResponsePreOpValues(CtiCCCapBank* capBank)
{
    try
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Updating POINT RESPONSE PREOPVALUES for CapBank: " <<capBank->getPAOName() << endl;
            dout << CtiTime() << " Bank ID: " <<capBank->getPAOName()<<" has "<<capBank->getPointResponse().size()<<" point responses"<< endl;
        }
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];

            for (LONG j=0; j<capBank->getPointResponse().size(); j++)
            {
                CtiCCPointResponse* pResponse = (CtiCCPointResponse*)capBank->getPointResponse()[j];

                if (point->getPointId() == pResponse->getPointId())
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " Bank ID: " <<capBank->getPAOName()<<" Point ID: "<<pResponse->getPointId()<<" Value: "<<point->getValue() << endl;
                    }

                    pResponse->setPreOpValue(point->getValue());
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCCSubstationBus::updatePointResponseDeltas()
{
    try
    {
        if( !stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
        {
            for (LONG i = 0; i < _ccfeeders.size();i++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                //check feeder to see if it all monitor points have received new pointdata
                {
                    currentFeeder->updatePointResponseDeltas();
                }
            }
        }
        else
        {
            for (LONG i = 0; i < _ccfeeders.size();i++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];

                //check feeder to see if it all monitor points have received new pointdata
                if (currentFeeder->getPAOId() == getLastFeederControlledPAOId())
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                     for(LONG j=0;j<ccCapBanks.size();j++)
                     {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                        if (currentCapBank->getPAOId() == currentFeeder->getLastCapBankControlledDeviceId())
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " Updating POINT RESPONSE DELTAS for CapBank: " <<currentCapBank->getPAOName() << endl;
                            }
                            for (int j = 0; j < _multipleMonitorPoints.size(); j++)
                            {
                                CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[j];
                                currentCapBank->updatePointResponseDeltas(point);
                            }
                            break;
                        }
                    }

                }

            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}



BOOL CtiCCSubstationBus::areAllMonitorPointsNewEnough(const CtiTime& currentDateTime)
{
        BOOL retVal = FALSE;
    if ( isScanFlagSet() && currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE *60) )  //T1 Expired.. Force Process
    {
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];
            if (point->getScanInProgress())
            {
                point->setScanInProgress(FALSE);
            }
        }

        retVal = TRUE;
    }
    else
    {
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];
            if (point->getTimeStamp().seconds() > (getLastOperationTime().seconds() - 30) &&
                point->getTimeStamp().seconds() + _POINT_AGE <= currentDateTime.seconds())
            {
                retVal = TRUE;
                if (point->getScanInProgress())
                {
                    point->setScanInProgress(FALSE);
                }
            }
            else
            {
                retVal = FALSE;
                break;
            }
        }
        if (retVal == TRUE)
        {
            BOOL scanInProgress = FALSE;
            for (i = 0; i < _multipleMonitorPoints.size(); i++)
            {
                CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];
                if (point->getScanInProgress())
                {
                    scanInProgress = TRUE;

                }
            }
        }
    }
    return retVal;

}

ULONG CtiCCSubstationBus::getMonitorPointScanTime()
{

    CtiTime time;
    time.now();

    return time.seconds();

}

BOOL CtiCCSubstationBus::isScanFlagSet()
{
    if (_preOperationMonitorPointScanFlag || _postOperationMonitorPointScanFlag)
    {
        return TRUE;
    }
    return FALSE;
}



BOOL CtiCCSubstationBus::scanAllMonitorPoints()
{
    BOOL retVal = FALSE;

    for (int i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];
        if (point->isScannable() && !point->getScanInProgress())
        {
            for (LONG j = 0; j < _ccfeeders.size();j++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[j];
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k = 0; k < ccCapBanks.size(); k++ )
                {

                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if (currentCapBank->getPAOId() == point->getBankId())
                    {
                        CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );

                        if(pAltRate)
                        {
                            pAltRate->insert(-1);                       // token, not yet used.
                            pAltRate->insert( currentCapBank->getControlDeviceId() );       // Device to poke.

                            pAltRate->insert( -1 );                      // Seconds since midnight, or NOW if negative.

                            pAltRate->insert( 0 );                      // Duration of zero should cause 1 scan.

                            CtiCapController::getInstance()->sendMessageToDispatch(pAltRate);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Requesting scans at the alternate scan rate for " << currentCapBank->getPAOName() << endl;
                            }

                            point->setScanInProgress(TRUE);
                            retVal = TRUE;
                        }
                        j = _ccfeeders.size();
                        break;
                    }
                }
            }
        }
    }

    //set MonitorPointScanTime
    return retVal;
}

void CtiCCSubstationBus::analyzeMultiVoltBus1(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiCCMonitorPoint outOfRangeMonitorPoint;

    if ( !stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        for (LONG i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (!currentFeeder->getRecentlyControlledFlag() && !currentFeeder->getPostOperationMonitorPointScanFlag())
            {
                if (!currentFeeder->areAllMonitorPointsInVoltageRange(&outOfRangeMonitorPoint))
                {
                    if (currentFeeder->areAllMonitorPointsNewEnough(currentDateTime))
                    {
                        if (!currentFeeder->areAllMonitorPointsInVoltageRange(&outOfRangeMonitorPoint))
                        {
                            if (currentFeeder->voltControlBankSelectProcess(&outOfRangeMonitorPoint, pointChanges, ccEvents, pilMessages))
                            {
                                currentFeeder->setOperationSentWaitFlag(TRUE);
                                currentFeeder->setLastOperationTime(currentDateTime);
                                currentFeeder->setRecentlyControlledFlag(TRUE);
                                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                                setRecentlyControlledFlag(TRUE);
                                setBusUpdatedFlag(TRUE);
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " No Bank Available for Control"<< endl;
                                }
                                if (currentFeeder->getPreOperationMonitorPointScanFlag())
                                    currentFeeder->setPreOperationMonitorPointScanFlag(FALSE);
                                if (currentFeeder->getOperationSentWaitFlag())
                                    currentFeeder->setOperationSentWaitFlag(FALSE);
                                if (currentFeeder->getPostOperationMonitorPointScanFlag())
                                    currentFeeder->setPostOperationMonitorPointScanFlag(FALSE);
                            }
                        }
                    }
                    else // !currentFeeder->isScanFlagSet()
                    {
                        if (!currentFeeder->isScanFlagSet())
                        {
                            if (currentFeeder->scanAllMonitorPoints())
                            {
                                currentFeeder->setPreOperationMonitorPointScanFlag(TRUE);
                                currentFeeder->setLastOperationTime(currentDateTime);
                            }
                        }
                        //else if (timeElapsed...)
                            //
                    }
                }
                else if (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::MultiVoltVarControlUnits))  //check for VarImprovement flag?
                {
                    if (analyzeBusForVarImprovement(&outOfRangeMonitorPoint, pointChanges, ccEvents, pilMessages))
                    {

                    }

                }
            }
            else if(currentFeeder->getRecentlyControlledFlag())//recently controlled...
            {
                bool pastTime = currentFeeder->isPastMaxConfirmTime(currentDateTime, getMaxConfirmTime(), getControlSendRetries());
                bool controled = currentFeeder->isAlreadyControlled(getMinConfirmPercent(), currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                           currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                           currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                           currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getRegression(),
                                           currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC(),
                                            currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag() );

                if ( pastTime || controled )
                {
                    if (!controled )
                    {
                        if (currentFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getMaxConfirmTime()))
                        {
                            if (_RETRY_ADJUST_LAST_OP_TIME)
                                setLastOperationTime(currentDateTime);
                            currentFeeder->setRetryIndex(currentFeeder->getRetryIndex() + 1);
                        }
                        else if( currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, getMinConfirmPercent(), getFailurePercent(),
                                                              currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(),
                                                              currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                              currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(), currentFeeder->getRegression()) )
                        {
                            currentFeeder->scanAllMonitorPoints();
                            if (currentFeeder->getOperationSentWaitFlag())
                                currentFeeder->setOperationSentWaitFlag(FALSE);
                            currentFeeder->setPostOperationMonitorPointScanFlag(TRUE);
                            currentFeeder->setLastOperationTime(currentDateTime);
                            setBusUpdatedFlag(TRUE);
                        }
                    }
                    else
                    {
                        currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, getMinConfirmPercent(), getFailurePercent(),
                                                              currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(),
                                                              currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                              currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(), currentFeeder->getRegression());
                        currentFeeder->scanAllMonitorPoints();
                        if (currentFeeder->getOperationSentWaitFlag())
                            currentFeeder->setOperationSentWaitFlag(FALSE);
                        currentFeeder->setPostOperationMonitorPointScanFlag(TRUE);
                        currentFeeder->setLastOperationTime(currentDateTime);
                    }
                    setBusUpdatedFlag(TRUE);

                }
            }
            else // currentFeeder->getPostOperationMonitorPointScanFlag() == TRUE
            {
                if ( currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE * 60) )
                {
                    currentFeeder->updatePointResponseDeltas();

                    currentFeeder->setPreOperationMonitorPointScanFlag(FALSE);
                    currentFeeder->setOperationSentWaitFlag(FALSE);
                    currentFeeder->setPostOperationMonitorPointScanFlag(FALSE);
                    currentFeeder->setRecentlyControlledFlag(FALSE);
                    setRecentlyControlledFlag(FALSE);
                }
            }
        }
        for ( i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->getRecentlyControlledFlag())
            {
                setRecentlyControlledFlag(TRUE);
                break;
            }
        }
        clearOutNewPointReceivedFlags();
        if( isVarCheckNeeded(currentDateTime) && getControlInterval() > 0 )
        {
            figureNextCheckTime();
        }
        figureEstimatedVarLoadPointValue();
    }
}

void CtiCCSubstationBus::analyzeMultiVoltBus(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiCCMonitorPoint outOfRangeMonitorPoint;

    if ( stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        if (!getRecentlyControlledFlag() && !getPostOperationMonitorPointScanFlag())
        {
            if (!areAllMonitorPointsInVoltageRange(&outOfRangeMonitorPoint))
            {
                if (areAllMonitorPointsNewEnough(currentDateTime))
                {
                    if (!areAllMonitorPointsInVoltageRange(&outOfRangeMonitorPoint))
                    {
                        if (voltControlBankSelectProcess(&outOfRangeMonitorPoint, pointChanges, ccEvents, pilMessages))
                        {
                            setOperationSentWaitFlag(TRUE);
                            setLastOperationTime(currentDateTime);
                            setRecentlyControlledFlag(TRUE);
                            //setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                            //setRecentlyControlledFlag(TRUE);
                            setBusUpdatedFlag(TRUE);
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " No Bank Available for Control"<< endl;
                            }
                            if (getPreOperationMonitorPointScanFlag())
                                setPreOperationMonitorPointScanFlag(FALSE);
                            if (getOperationSentWaitFlag())
                                setOperationSentWaitFlag(FALSE);
                            if (getPostOperationMonitorPointScanFlag())
                                setPostOperationMonitorPointScanFlag(FALSE);
                        }
                    }
                }
                else // !isScanFlagSet()
                {
                    if (!isScanFlagSet())
                    {
                        if (scanAllMonitorPoints())
                        {
                            setPreOperationMonitorPointScanFlag(TRUE);
                            setLastOperationTime(currentDateTime);
                        }
                    }
                    //else if (timeElapsed...)
                        //
                }
            }
            else if (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::MultiVoltVarControlUnits))
            {
                if (analyzeBusForVarImprovement(&outOfRangeMonitorPoint, pointChanges, ccEvents, pilMessages))
                {

                }

            }

        }
        else if(getRecentlyControlledFlag())//recently controlled...
        {
            if (isPastMaxConfirmTime(currentDateTime) || isAlreadyControlled())
            {
                if (!isAlreadyControlled() )
                {
                    if (checkForAndPerformSendRetry(currentDateTime, pointChanges, ccEvents, pilMessages))
                    {
                        setLastOperationTime(currentDateTime);
                    }
                    else if (capBankControlStatusUpdate(pointChanges, ccEvents))
                    {
                        scanAllMonitorPoints();
                        if (getOperationSentWaitFlag())
                            setOperationSentWaitFlag(FALSE);
                        setPostOperationMonitorPointScanFlag(TRUE);
                        setLastOperationTime(currentDateTime);
                    }
                }
                else
                {
                    capBankControlStatusUpdate(pointChanges, ccEvents);
                    scanAllMonitorPoints();
                    if (getOperationSentWaitFlag())
                        setOperationSentWaitFlag(FALSE);
                    setPostOperationMonitorPointScanFlag(TRUE);
                    setLastOperationTime(currentDateTime);
                }
                setBusUpdatedFlag(TRUE);

            }
        }
        else // getPostOperationMonitorPointScanFlag() == TRUE
        {
            if ( currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE * 60) )
            {
                updatePointResponseDeltas();

                setPreOperationMonitorPointScanFlag(FALSE);
                setOperationSentWaitFlag(FALSE);
                setPostOperationMonitorPointScanFlag(FALSE);
                setRecentlyControlledFlag(FALSE);
            }
        }
        for (LONG i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->getRecentlyControlledFlag())
            {
                setRecentlyControlledFlag(TRUE);
                break;
            }
        }
        clearOutNewPointReceivedFlags();
        if( isVarCheckNeeded(currentDateTime) && getControlInterval() > 0 )
        {
            figureNextCheckTime();
        }
        figureEstimatedVarLoadPointValue();
    }
}


CtiCCCapBank* CtiCCSubstationBus::getMonitorPointParentBankAndFeeder(CtiCCMonitorPoint* point, CtiCCFeeder* feed)
{

    for (LONG h = 0; h < _ccfeeders.size();h++)
    {
        CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[h];
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for (LONG i = 0; i < ccCapBanks.size(); i++)
        {
            CtiCCCapBankPtr cap = (CtiCCCapBankPtr)ccCapBanks[i];
            if (point->getBankId() == cap->getPAOId())
            {
                feed = currentFeeder;
                return cap;
            }
        }
    }
    return NULL;
}

BOOL CtiCCSubstationBus::analyzeBusForVarImprovement(CtiCCMonitorPoint* point, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{

    BOOL retVal = FALSE;
    CtiRequestMsg* request = NULL;
    CtiTime currentDateTime;

    if ( !stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        for (LONG i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->getCurrentVarLoadPointId() > 0)
            {

                DOUBLE lagLevel = (isPeakTime(currentDateTime)?currentFeeder->getPeakVARLag():currentFeeder->getOffPeakVARLag());
                DOUBLE leadLevel = (getPeakTimeFlag()?currentFeeder->getPeakVARLead():currentFeeder->getOffPeakVARLead());
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    ( currentFeeder->getCurrentVarPointQuality() == NormalQuality &&
                      currentFeeder->getCurrentVoltPointQuality() == NormalQuality ) )
                {
                    if( currentFeeder->getCurrentVarLoadPointValue() > lagLevel )
                    {
                        //select bank to open...make sure volt points stay in range though.
                        vector <CtiCCMonitorPointPtr>& monPoints = currentFeeder->getMultipleMonitorPoints();
                        for (int i = 0; i < monPoints.size(); i++)
                        {
                            CtiCCMonitorPointPtr pt = monPoints[i];
                            CtiCCCapBankPtr bank = currentFeeder->getMonitorPointParentBank(pt);
                            if (bank->getControlStatus() == CtiCCCapBank::Open ||
                                bank->getControlStatus() == CtiCCCapBank::OpenQuestionable )
                            {
                                CtiCCPointResponse* pResponse =  bank->getPointResponse(pt);
                                if (pResponse != NULL)
                                {
                                    if ( (pt->getValue() + pResponse->getDelta() <= pt->getUpperBandwidth() &&
                                          pt->getValue() + pResponse->getDelta() >= pt->getLowerBandwidth() ) ||
                                          pt->getValue() + pResponse->getDelta() < pt->getUpperBandwidth() )
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " Attempting to Improve PF on Feeder: "<<getPAOName()<<" CapBank: "<<bank->getPAOName() << endl;
                                        }

                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt->getBankId()<<"/"<<pt->getPointId()<<" Parent CapBank: "<<bank->getPAOName() <<" selected to Close" << endl;
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (currentFeeder->areOtherMonitorPointResponsesOk(pt->getPointId(), bank, CtiCCCapBank::Close))
                                        {
                                            DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) ? currentFeeder->getCurrentVoltLoadPointValue() : currentFeeder->getCurrentVarLoadPointValue());
                                            string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, currentFeeder->getCurrentVarLoadPointValue());
                                            request = currentFeeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                              currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());

                                             currentFeeder->updatePointResponsePreOpValues(bank);
                                        }
                                    }
                                }

                            }
                            if (request != NULL)
                            {
                                break;
                            }

                        }

                    }
                    else if ( currentFeeder->getCurrentVarLoadPointValue() < leadLevel )
                    {
                        //select bank to open...make sure volt points stay in range though.
                        vector <CtiCCMonitorPointPtr>& monPoints = currentFeeder->getMultipleMonitorPoints();
                        for (int i = 0; i < monPoints.size(); i++)
                        {
                            CtiCCMonitorPointPtr pt = monPoints[i];
                            CtiCCCapBankPtr bank = currentFeeder->getMonitorPointParentBank(pt);
                            if (bank->getControlStatus() == CtiCCCapBank::Close ||
                                bank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                            {
                                CtiCCPointResponse* pResponse =  bank->getPointResponse(pt);
                                if (pResponse != NULL)
                                {
                                    if ( (pt->getValue() - pResponse->getDelta() <= pt->getUpperBandwidth() &&
                                          pt->getValue() - pResponse->getDelta() >= pt->getLowerBandwidth() ) ||
                                          pt->getValue() - pResponse->getDelta() > pt->getLowerBandwidth() )
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " Attempting to Improve PF on Feeder: "<<getPAOName()<<" CapBank: "<<bank->getPAOName() << endl;
                                        }

                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt->getBankId()<<"/"<<pt->getPointId()<<" Parent CapBank: "<<bank->getPAOName() <<" selected to Open" << endl;
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (currentFeeder->areOtherMonitorPointResponsesOk(pt->getPointId(), bank, CtiCCCapBank::Open))
                                        {
                                            DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) ? currentFeeder->getCurrentVoltLoadPointValue() : currentFeeder->getCurrentVarLoadPointValue());
                                            string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, currentFeeder->getCurrentVarLoadPointValue());
                                            request = currentFeeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                              currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());

                                             currentFeeder->updatePointResponsePreOpValues(bank);
                                        }
                                    }
                                }

                            }
                            if (request != NULL)
                            {
                                break;
                            }

                        }

                    }
                    else
                    {
                        //nothing.  var load within limits...
                    }
                }
                if (request != NULL)
                {
                    break;
                }
            }
            else
            {
               if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " MULTIVOLT: Var Information NOT available for Feeder: "<<currentFeeder->getPAOName() << endl;
               }
            }
        }
        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(TRUE);
            //setLastCapBankControlledDeviceId( bestBank->getPAOId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }

            retVal = TRUE;
        }

    }
    else
    {
        if (getCurrentVarLoadPointId() > 0)
        {

            CtiCCFeeder* parentFeeder = NULL;
            DOUBLE lagLevel = (isPeakTime(currentDateTime)?getPeakVARLag():getOffPeakVARLag());
            DOUBLE leadLevel = (getPeakTimeFlag()?getPeakVARLead():getOffPeakVARLead());
            if( !_IGNORE_NOT_NORMAL_FLAG ||
                ( getCurrentVarPointQuality() == NormalQuality &&
                  getCurrentVoltPointQuality() == NormalQuality ) )
            {
                if( getCurrentVarLoadPointValue() > lagLevel )
                {
                    //select bank to open...make sure volt points stay in range though.
                    vector <CtiCCMonitorPointPtr>& monPoints = getMultipleMonitorPoints();
                    for (int i = 0; i < monPoints.size(); i++)
                    {
                        CtiCCMonitorPointPtr pt = monPoints[i];
                        CtiCCCapBankPtr bank = getMonitorPointParentBankAndFeeder(pt, parentFeeder);

                        if (bank->getControlStatus() == CtiCCCapBank::Open ||
                            bank->getControlStatus() == CtiCCCapBank::OpenQuestionable )
                        {
                            CtiCCPointResponse* pResponse =  bank->getPointResponse(pt);
                            if (pResponse != NULL)
                            {
                                if ( (pt->getValue() + pResponse->getDelta() <= pt->getUpperBandwidth() &&
                                      pt->getValue() + pResponse->getDelta() >= pt->getLowerBandwidth() ) ||
                                      pt->getValue() + pResponse->getDelta() < pt->getUpperBandwidth() )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " Attempting to Improve PF on Sub: "<<getPAOName()<<" CapBank: "<<bank->getPAOName() << endl;
                                    }

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt->getBankId()<<"/"<<pt->getPointId()<<" Parent CapBank: "<<bank->getPAOName() <<" selected to Close" << endl;
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if (areOtherMonitorPointResponsesOk(pt->getPointId(), bank, CtiCCCapBank::Close))
                                    {
                                        DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                        string text = parentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                        request =  parentFeeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                        updatePointResponsePreOpValues(bank);
                                    }
                                }
                            }

                        }
                        if (request != NULL)
                        {
                            break;
                        }
                    }
                }
                else if ( getCurrentVarLoadPointValue() < leadLevel )
                {
                    //select bank to open...make sure volt points stay in range though.
                    vector <CtiCCMonitorPointPtr>& monPoints = getMultipleMonitorPoints();
                    for (int i = 0; i < monPoints.size(); i++)
                    {
                        CtiCCMonitorPointPtr pt = monPoints[i];
                        CtiCCCapBankPtr bank = getMonitorPointParentBankAndFeeder(pt, parentFeeder);

                        if (bank->getControlStatus() == CtiCCCapBank::Close ||
                            bank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                        {
                            CtiCCPointResponse* pResponse =  bank->getPointResponse(pt);
                            if (pResponse != NULL)
                            {
                                if ( (pt->getValue() - pResponse->getDelta() <= pt->getUpperBandwidth() &&
                                      pt->getValue() - pResponse->getDelta() >= pt->getLowerBandwidth() ) ||
                                      pt->getValue() - pResponse->getDelta() > pt->getLowerBandwidth() )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " Attempting to Improve PF on Sub: "<<getPAOName()<<" CapBank: "<<bank->getPAOName() << endl;
                                    }

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt->getBankId()<<"/"<<pt->getPointId()<<" Parent CapBank: "<<bank->getPAOName() <<" selected to Open" << endl;
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if (areOtherMonitorPointResponsesOk(pt->getPointId(), bank, CtiCCCapBank::Open))
                                    {
                                        DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                        string text = parentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                        request = parentFeeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                        updatePointResponsePreOpValues(bank);
                                    }
                                }
                            }

                        }
                        if (request != NULL)
                        {
                            break;
                        }

                    }

                }
                else
                {
                    //nothing.  var load within limits...
                }
            }
        }
        else
        {
           if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
           {
               CtiLockGuard<CtiLogger> logger_guard(dout);
               dout << CtiTime() << " MULTIVOLT: Var Information NOT available for Sub: "<<getPAOName() << endl;
           }
        }

        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(TRUE);
            //setLastCapBankControlledDeviceId( bestBank->getPAOId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }

            retVal = TRUE;
        }
    }
    return FALSE;
}


BOOL CtiCCSubstationBus::voltControlBankSelectProcess(CtiCCMonitorPoint* point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec &pilMessages)
{
    BOOL retVal = FALSE;

    CtiRequestMsg* request = NULL;

    CtiCCCapBank* bestBank = NULL;
    CtiCCFeeder* parentFeeder = NULL;
   //Check for undervoltage condition first.
   try
   {
        if (point->getValue() < point->getLowerBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            DOUBLE bestDelta = 0;

            //1.  First check this point's parent bank to see if we can close it.
            parentBank = getMonitorPointParentBankAndFeeder(point, parentFeeder);
            if (parentBank != NULL)
            {
                if (parentBank->getControlStatus() == CtiCCCapBank::Open ||
                    parentBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
                {
                    CtiCCPointResponse* pResponse =  parentBank->getPointResponse(point);
                    if (pResponse != NULL)
                    {

                        if ( (point->getValue() + pResponse->getDelta() <= point->getUpperBandwidth() &&
                              point->getValue() + pResponse->getDelta() >= point->getLowerBandwidth() ) ||
                              //pRespone->getDelta() == 0 ||
                              point->getValue() + pResponse->getDelta() < point->getUpperBandwidth() )
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " Attempting to Increase Voltage on Feeder: "<<getPAOName()<<" CapBank: "<<parentBank->getPAOName() << endl;
                            }
                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getBankId()<<"/"<<point->getPointId()<<" Parent CapBank: "<<parentBank->getPAOName() <<" selected to Close" << endl;
                            }
                            //Check other monitor point responses using this potential capbank
                            if (areOtherMonitorPointResponsesOk(point->getPointId(), parentBank, CtiCCCapBank::Close))
                            {
                                DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                DOUBLE monitorValue =  parentFeeder->getCurrentVarLoadPointValue();
                                DOUBLE phaseAValue = parentFeeder->getPhaseAValue();
                                DOUBLE phaseBValue = parentFeeder->getPhaseBValue();
                                DOUBLE phaseCValue = parentFeeder->getPhaseCValue();
                                if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod))
                                {
                                    monitorValue = getCurrentVarLoadPointValue();
                                    phaseAValue = getPhaseAValue();
                                    phaseBValue = getPhaseBValue();
                                    phaseCValue = getPhaseCValue();

                                }
                                string text = parentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, monitorValue);
                                request = parentFeeder->createDecreaseVarRequest(parentBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                                parentBank = NULL;
                        }
                        else
                            parentBank = NULL;
                    }
                }
                else
                    parentBank = NULL;
            }

            //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {

                for (LONG h = 0; h < _ccfeeders.size();h++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[h];
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for (LONG i = 0; i < ccCapBanks.size(); i++)
                    {

                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*) ccCapBanks[i];
                        if (currentCapBank->getControlStatus() == CtiCCCapBank::Open || currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
                        {
                            if (point->getBankId() != currentCapBank->getPAOId())
                            {
                                CtiCCPointResponse* pResponse =  currentCapBank->getPointResponse(point);
                                if (pResponse != NULL)
                                {

                                    if ( (point->getValue() + pResponse->getDelta() <= point->getUpperBandwidth() &&
                                          point->getValue() + pResponse->getDelta() >= point->getLowerBandwidth() ) ||
                                          pResponse->getDelta() == 0 ||
                                          point->getValue() + pResponse->getDelta() < point->getUpperBandwidth() )
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " Attempting to Increase Voltage on Feeder: "<<getPAOName()<<" CapBank: "<<currentCapBank->getPAOName() << endl;
                                        }
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getBankId()<<"/"<<point->getPointId()<<" Parent CapBank: "<<currentCapBank->getPAOName() <<" selected to Close" << endl;
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (areOtherMonitorPointResponsesOk(point->getPointId(), currentCapBank, CtiCCCapBank::Close))
                                        {
                                            DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                            DOUBLE monitorValue =  parentFeeder->getCurrentVarLoadPointValue();
                                            DOUBLE phaseAValue = parentFeeder->getPhaseAValue();
                                            DOUBLE phaseBValue = parentFeeder->getPhaseBValue();
                                            DOUBLE phaseCValue = parentFeeder->getPhaseCValue();
                                            if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod))
                                            {
                                                monitorValue = getCurrentVarLoadPointValue();
                                                phaseAValue = getPhaseAValue();
                                                phaseBValue = getPhaseBValue();
                                                phaseCValue = getPhaseCValue();

                                            }
                                            string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, monitorValue);
                                            request = currentFeeder->createDecreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                            updatePointResponsePreOpValues(currentCapBank);
                                            bestBank = currentCapBank;
                                            parentFeeder = currentFeeder;
                                        }
                                    }
                                }
                            }

                        }
                        if (request != NULL)
                        {
                            bestBank = currentCapBank;
                            parentFeeder = currentFeeder;
                            break;
                        }
                    }
                    if (request != NULL)
                    {
                        //bestBank = currentCapBank;
                        parentFeeder = currentFeeder;
                        break;
                    }
                }
            }

            //3.  If there are no banks avail which put LowerBW > mp->value > LowerBW...just settle for bestFit..
            if (request == NULL)
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " No Banks Available to Close on Sub: " <<getPAOName()<< endl;
                }
            }
        }
        else if (point->getValue() > point->getUpperBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            DOUBLE bestDelta = 0;

            //1.  First check this point's parent bank to see if we can open it.
            parentBank = getMonitorPointParentBankAndFeeder(point, parentFeeder);
            if (parentBank != NULL)
            {
                if (parentBank->getControlStatus() == CtiCCCapBank::Close ||
                    parentBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                {
                    CtiCCPointResponse* pResponse =  parentBank->getPointResponse(point);
                    if (pResponse != NULL)
                    {

                        if ( (point->getValue() - pResponse->getDelta() <= point->getUpperBandwidth() &&
                              point->getValue() - pResponse->getDelta() >= point->getLowerBandwidth() ) ||
                              //pRespone->getDelta() == 0 ||
                              point->getValue() - pResponse->getDelta() > point->getLowerBandwidth() )
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " Attempting to Decrease Voltage on Feeder: "<<getPAOName()<<" CapBank: "<<parentBank->getPAOName() << endl;
                            }
                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getBankId()<<"/"<<point->getPointId()<<" Parent CapBank: "<<parentBank->getPAOName() <<" selected to Open" << endl;
                            }
                            //Check other monitor point responses using this potential capbank
                            if (areOtherMonitorPointResponsesOk(point->getPointId(), parentBank, CtiCCCapBank::Open))
                            {
                                DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                DOUBLE monitorValue =  parentFeeder->getCurrentVarLoadPointValue();
                                DOUBLE phaseAValue = parentFeeder->getPhaseAValue();
                                DOUBLE phaseBValue = parentFeeder->getPhaseBValue();
                                DOUBLE phaseCValue = parentFeeder->getPhaseCValue();
                                if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod))
                                {
                                    monitorValue = getCurrentVarLoadPointValue();
                                    phaseAValue = getPhaseAValue();
                                    phaseBValue = getPhaseBValue();
                                    phaseCValue = getPhaseCValue();

                                }
                                string text = parentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, monitorValue);
                                request = parentFeeder->createIncreaseVarRequest(parentBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                                parentBank = NULL;
                        }
                        else
                            parentBank = NULL;
                    }
                }
                else
                    parentBank = NULL;
            }

           //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {

                for (LONG h = 0; h < _ccfeeders.size();h++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[h];
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for (LONG i = 0; i < ccCapBanks.size(); i++)
                    {

                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*) ccCapBanks[i];
                        if (currentCapBank->getControlStatus() == CtiCCCapBank::Close || currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                        {
                            if (point->getBankId() != currentCapBank->getPAOId())
                            {
                                CtiCCPointResponse* pResponse =  currentCapBank->getPointResponse(point);
                                if (pResponse != NULL)
                                {

                                    if ( (point->getValue() - pResponse->getDelta() <= point->getUpperBandwidth() &&
                                          point->getValue() - pResponse->getDelta() >= point->getLowerBandwidth() ) ||
                                          pResponse->getDelta() == 0 ||
                                          point->getValue() - pResponse->getDelta() > point->getUpperBandwidth() )
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " Attempting to Decrease Voltage on Feeder: "<<getPAOName()<<" CapBank: "<<currentCapBank->getPAOName() << endl;
                                        }
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getBankId()<<"/"<<point->getPointId()<<" Parent CapBank: "<<currentCapBank->getPAOName() <<" selected to Open" << endl;
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (areOtherMonitorPointResponsesOk(point->getPointId(), currentCapBank, CtiCCCapBank::Open))
                                        {
                                            DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                            DOUBLE monitorValue =  currentFeeder->getCurrentVarLoadPointValue();
                                            DOUBLE phaseAValue = currentFeeder->getPhaseAValue();
                                            DOUBLE phaseBValue = currentFeeder->getPhaseBValue();
                                            DOUBLE phaseCValue = currentFeeder->getPhaseCValue();
                                            if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod))
                                            {
                                                monitorValue = getCurrentVarLoadPointValue();
                                                phaseAValue = getPhaseAValue();
                                                phaseBValue = getPhaseBValue();
                                                phaseCValue = getPhaseCValue();

                                            }
                                            string text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, monitorValue);
                                            request = currentFeeder->createIncreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                            updatePointResponsePreOpValues(currentCapBank);
                                            bestBank = currentCapBank;
                                            parentFeeder = currentFeeder;
                                        }
                                    }
                                }
                            }

                        }
                        if (request != NULL)
                        {
                            bestBank = currentCapBank;
                            parentFeeder = currentFeeder;
                            break;
                        }
                    }
                    if (request != NULL)
                    {
                        //bestBank = currentCapBank;
                        parentFeeder = currentFeeder;
                        break;
                    }
                }
            }

            //3.  If there are no banks avail which put UpperBW > mp->value > LowerBW...just settle for bestFit..
            if (request == NULL)
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " No Banks Available to Open on Sub: "<<getPAOName() << endl;
                }
            }
       }
       if( request != NULL )
       {
            pilMessages.push_back(request);
           //setLastOperationTime(currentDateTime);
            setOperationSentWaitFlag(TRUE);
            setLastFeederControlledPAOId( parentFeeder->getPAOId());
            parentFeeder->setLastCapBankControlledDeviceId( bestBank->getPAOId());
            parentFeeder->setRecentlyControlledFlag(TRUE);
            parentFeeder->setVarValueBeforeControl(parentFeeder->getCurrentVarLoadPointValue());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setOperationSentWaitFlag(TRUE);

            retVal = TRUE;
       }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return retVal;

}

BOOL CtiCCSubstationBus::areOtherMonitorPointResponsesOk(LONG mPointID, CtiCCCapBank* potentialCap, int action)
{
    BOOL retVal = TRUE;

    //action = 0 --> open
    //action = 1 --> close

    for (LONG i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPointPtr otherPoint = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
        if (otherPoint->getPointId() != mPointID)
        {
            for(LONG j=0;j<potentialCap->getPointResponse().size();j++)
            {
                CtiCCPointResponse* pResponse = (CtiCCPointResponse*)potentialCap->getPointResponse()[j];

                if (otherPoint->getPointId() == pResponse->getPointId())
                {
                    if (action) //CLOSE
                    {
                        if (pResponse->getDelta() != 0)
                        {
                            if (otherPoint->getValue() + pResponse->getDelta() > otherPoint->getUpperBandwidth() )
                                //||otherPoint->getValue() + pResponse->getDelta() < otherPoint->getLowerBandwidth())
                            {

                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: "<<getPAOName()<<" CapBank: "<<potentialCap->getPAOName() << endl;
                                    dout << CtiTime() << " otherPoint: "<<otherPoint->getPointId()<<" "<<otherPoint->getBankId()<<" Value: "<<otherPoint->getValue()<<" Delta: "<<pResponse->getDelta()<<" pResponse: "<<pResponse->getPointId()<<" "<<pResponse->getBankId() << endl;
                                }
                                retVal = FALSE;
                                break;
                            }
                            else
                                retVal = TRUE;
                        }
                        else
                        {
                            retVal = TRUE;
                        }
                    }
                    else // OPEN
                    {
                        if (pResponse->getDelta() != 0)
                        {
                            if (//otherPoint->getValue() - pResponse->getDelta() > otherPoint->getUpperBandwidth() ||
                                otherPoint->getValue() - pResponse->getDelta() < otherPoint->getLowerBandwidth())
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: "<<getPAOName()<<" CapBank: "<<potentialCap->getPAOName() << endl;
                                    dout << CtiTime() << " otherPoint: "<<otherPoint->getPointId()<<" "<<otherPoint->getBankId()<<" Value: "<<otherPoint->getValue()<<" Delta: "<<pResponse->getDelta()<<" pResponse: "<<pResponse->getPointId()<<" "<<pResponse->getBankId() << endl;
                                }
                                retVal = FALSE;
                                break;
                            }
                            else
                                retVal = TRUE;
                        }
                        else
                        {
                            retVal = TRUE;
                        }

                    }
                }
            }
            if (retVal == FALSE)
            {
                break;
            }

        }

    }
    return retVal;
}

BOOL CtiCCSubstationBus::areAllMonitorPointsInVoltageRange(CtiCCMonitorPoint* oorPoint)
{
    BOOL retVal = FALSE;

    for (int i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];
        if (point->getValue() >= point->getLowerBandwidth() &&
            point->getValue() <= point->getUpperBandwidth() )
        {
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                 CtiLockGuard<CtiLogger> logger_guard(dout);
                 dout << CtiTime() << " MULTIVOLT: Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getBankId()<<" is inside limits.  Current value: "<<point->getValue() << endl;
            }
            retVal = TRUE;
        }
        else
        {

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getBankId()<<" is outside limits.  Current value: "<<point->getValue() << endl;
            }
            *oorPoint = *point;
            retVal = FALSE;
            break;
        }
    }
    return retVal;
}


BOOL CtiCCSubstationBus::isMultiVoltBusAnalysisNeeded(const CtiTime& currentDateTime)
{
    BOOL retVal = FALSE;
    if (getStrategyId() > 0 &&
       (!stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::MultiVoltControlUnits) ||
        !stringCompareIgnoreCase(_controlunits,CtiCCSubstationBus::MultiVoltVarControlUnits) ) &&
        !getVerificationFlag() )
    {
        if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) ||
            !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
        {
            for (LONG i = 0; i < _ccfeeders.size();i++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                if (currentFeeder->getNewPointDataReceivedFlag())
                {
                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " MULTIVOLT ANALYSIS on Sub: "<<getPAOName() << endl;
                    }
                    retVal = TRUE;
                    return retVal;
                }
                //if (currentFeeder->getPostOperationMonitorPointScanFlag() )
            }
        }

        if (_newpointdatareceivedflag )
        {
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " MULTIVOLT ANALYSIS on Sub: "<<getPAOName() << endl;
            }
            retVal = TRUE;

        }
        else if( getControlInterval() > 0 )
        {
            retVal = (getNextCheckTime().seconds() <= currentDateTime.seconds());

        }
    }

    return retVal;
}

BOOL CtiCCSubstationBus::isBusAnalysisNeeded(const CtiTime& currentDateTime)
{
    BOOL retVal = FALSE;
    if (getStrategyId() > 0)
    {
        if (!stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::MultiVoltControlUnits) ||
            !stringCompareIgnoreCase(getControlUnits(),CtiCCSubstationBus::MultiVoltVarControlUnits) )
        {
            if (_newpointdatareceivedflag || getVerificationFlag())
                retVal = TRUE;

        }
        else if ( isVarCheckNeeded(currentDateTime) || isConfirmCheckNeeded() )
        {
                retVal = TRUE;
        }
        else if (getVerificationFlag())
        {
            if (!isBusPerformingVerification())
                retVal = TRUE;
            else if (isBusPerformingVerification() && isPastMaxConfirmTime(currentDateTime))
            {
                if (_lastVerificationCheck.seconds() + 30 < currentDateTime.seconds())
                    retVal = TRUE;
            }
            else if (_lastVerificationCheck.seconds() + 30 < currentDateTime.seconds())
            {
                retVal = TRUE;
            }
        }
    }

    return retVal;
}

CtiCCSubstationBus& CtiCCSubstationBus::checkForAndProvideNeededTimeOfDayControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;

    map <long, long> controlid_action_map;
    controlid_action_map.clear();

    if( !getDisableFlag() &&
        currentDateTime.seconds() >= getLastOperationTime().seconds() + getControlDelayTime() )
    {
        try
        {

            LONG currentNumClosed = 0;
            LONG numOfBanks = 0;
            try
            {
                /*if (!getSendMoreTimeControlledCommandsFlag())
                {
                    for(LONG i=0;i<_ccfeeders.size();i++)
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                        if (!currentFeeder->getDisableFlag())
                        {
                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                            for(LONG j=0;j<ccCapBanks.size();j++)
                            {
                                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                                if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                {
                                    numOfBanks += 1;
                                    if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                                    {
                                        currentNumClosed += 1;
                                    }
                                }
                            }
                    }
                } */

                for(LONG i=0;i<_ccfeeders.size();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    for(LONG j=0;j<ccCapBanks.size();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                        {
                            numOfBanks += 1;
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                            {
                                currentNumClosed += 1;
                            }
                        }
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            int targetNumClose = numOfBanks * (_percentToClose * 0.01);
            int targetNumOpen =  numOfBanks - targetNumClose;
            int currentNumOpen = numOfBanks - currentNumClosed;
            CtiCCFeeder* currentFeeder = NULL;
            CtiCCCapBank* capBank =  NULL;
            int loopCount = 0;
            if (_CC_DEBUG & CC_DEBUG_TIMEOFDAY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " - SubBus: "<<getPAOName() << " percentClose: " << _percentToClose << "% targetClose: " << targetNumClose <<
                       " currentClose: " << currentNumClosed << " targetOpen: " << targetNumOpen  <<" currentOpen: " << currentNumOpen << endl;
            }
            if (targetNumClose >= currentNumClosed)
            {

                setSendMoreTimeControlledCommandsFlag(FALSE);
                LONG currentPosition = getLastFeederControlledPosition();
                while (currentNumClosed < targetNumClose )
                {
                    if( currentPosition >= _ccfeeders.size()-1 )
                        currentPosition = 0;
                    else
                        currentPosition++;

                    currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                    {
                        try
                        {
                            capBank = currentFeeder->findCapBankToChangeVars(-1, pointChanges);  //close 
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    if (capBank != NULL)
                    {
                        string text = "";

                        try
                        {
                            if (!_TIME_OF_DAY_VAR_CONF)
                                request = currentFeeder->createForcedVarRequest(capBank, pointChanges, ccEvents, CtiCCCapBank::Close, "TimeOfDay Control");
                            else
                            {
                                text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Close, getCurrentVarLoadPointValue(),  getCurrentVarLoadPointValue());
                                if ( (currentFeeder->getCurrentVarLoadPointId() > 0 && !currentFeeder->getUsePhaseData() )  ||
                                     (currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0
                                      && currentFeeder->getPhaseCId() > 0 && currentFeeder->getUsePhaseData() ) )
                                {
                                    request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  currentFeeder->getCurrentVarLoadPointValue(),
                                                                                      currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());
                                }
                                else
                                {
                                    //assume substation points...
                                    request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  getCurrentVarLoadPointValue(),
                                                                                      getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                                }

                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }


                        if( request != NULL )
                        {
                            try
                            {
                                if (!_TIME_OF_DAY_VAR_CONF)
                                    currentFeeder->createForcedVarConfirmation(capBank, pointChanges, ccEvents, "TimeOfDay Control");
                                else
                                {
                                    currentFeeder->setRecentlyControlledFlag(TRUE);
                                    setRecentlyControlledFlag(TRUE);
                                    setVarValueBeforeControl(getCurrentVarLoadPointValue());
                                    if (currentNumClosed + 1 < targetNumClose)
                                    {
                                        setSendMoreTimeControlledCommandsFlag(TRUE);
                                        currentNumClosed = targetNumClose;
                                    }
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }

                            try
                            {

                                pilMessages.push_back(request);
                                setLastOperationTime(currentDateTime);
                                setLastFeederControlledPAOId(currentFeeder->getPAOId());
                                currentFeeder->setLastOperationTime(currentDateTime);
                                setLastFeederControlledPosition(currentPosition);
                                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        setBusUpdatedFlag(TRUE);

                        currentNumClosed += 1;
                    }
                    else
                    {
                        if (loopCount > _ccfeeders.size() - 1)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " No more banks available to close on subBus: "<<getPAOName()<< endl;

                            break;
                        }
                        else
                            loopCount += 1;
                    }
                }
            }
            else
            {
                if (targetNumOpen >= currentNumOpen)
                {

                    setSendMoreTimeControlledCommandsFlag(FALSE);
                    LONG currentPosition = getLastFeederControlledPosition();
                    while (currentNumOpen < targetNumOpen)
                    {
                        if( currentPosition >= _ccfeeders.size()-1 )
                            currentPosition = 0;
                        else
                            currentPosition++;

                        currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                        if( !currentFeeder->getDisableFlag() &&
                            !currentFeeder->getWaiveControlFlag() &&
                            currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getControlDelayTime() )
                        {

                            try
                            {
                                capBank = currentFeeder->findCapBankToChangeVars(1, pointChanges);  //open 
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        if (capBank != NULL)
                        {
                            string text = "";
                            try
                            {

                                if (!_TIME_OF_DAY_VAR_CONF)
                                    request = currentFeeder->createForcedVarRequest(capBank, pointChanges, ccEvents, CtiCCCapBank::Open, "TimeOfDay Control");
                                else
                                {
                                    text = currentFeeder->createTextString(getControlMethod(), CtiCCCapBank::Open, getCurrentVarLoadPointValue(),  getCurrentVarLoadPointValue());
                                    if ( (currentFeeder->getCurrentVarLoadPointId() > 0 && !currentFeeder->getUsePhaseData() )  ||
                                         (currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0
                                          && currentFeeder->getPhaseCId() > 0 && currentFeeder->getUsePhaseData() ) )
                                    {
                                        request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text,  currentFeeder->getCurrentVarLoadPointValue(),
                                                                                          currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());
                                    }
                                    else
                                    {
                                        request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text,  getCurrentVarLoadPointValue(),
                                                                                          getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                                    }
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }


                            if( request != NULL )
                            {
                                try
                                {
                                    if (!_TIME_OF_DAY_VAR_CONF)
                                        currentFeeder->createForcedVarConfirmation(capBank, pointChanges, ccEvents, "TimeOfDay Control");
                                    else
                                    {
                                        currentFeeder->setRecentlyControlledFlag(TRUE);
                                        setRecentlyControlledFlag(TRUE);
                                        setVarValueBeforeControl(getCurrentVarLoadPointValue());
                                        if (currentNumOpen + 1 < targetNumOpen)
                                        {
                                            setSendMoreTimeControlledCommandsFlag(TRUE);
                                            currentNumOpen = targetNumOpen;
                                        }
                                    }
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                                try
                                {
                                    pilMessages.push_back(request);
                                    setLastOperationTime(currentDateTime);
                                    setLastFeederControlledPAOId(currentFeeder->getPAOId());
                                    setLastFeederControlledPosition(currentPosition);
                                    currentFeeder->setLastOperationTime(currentDateTime);
                                    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            setBusUpdatedFlag(TRUE);

                            currentNumOpen += 1;
                        }
                        else
                        {
                            if (loopCount > _ccfeeders.size() -1 )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " No more banks available to open on subBus: "<<getPAOName()<< endl;

                                break;
                            }
                            else
                                loopCount += 1;
                        }
                    }
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;

    map <long, long> controlid_action_map;
    controlid_action_map.clear();

    if( !getDisableFlag() &&
        currentDateTime.seconds() >= getLastOperationTime().seconds() + getControlDelayTime() )
    {
        if( !stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::IndividualFeederControlMethod) )
        {
            for(LONG i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if( !currentFeeder->getDisableFlag() )
                {
                    if( currentFeeder->checkForAndProvideNeededFallBackControl(currentDateTime,pointChanges,ccEvents,pilMessages))                    {
                        setLastOperationTime(currentDateTime);
                    }
                    setBusUpdatedFlag(TRUE);
                }
            }
        }
        else
        {
            CtiTime lastSendTime = getLastOperationTime();
            if (getLastOperationTime() < getLastCurrentVarPointUpdateTime())
            {
                lastSendTime = getLastCurrentVarPointUpdateTime();
                setLastOperationTime(currentDateTime);
            }
            int fallBackConst = 86400;
            //if (currentDateTime.tm_wday)
            {
                struct tm *ctm= new struct tm();
                currentDateTime.extract(ctm);
                if (ctm->tm_wday == 0 || ctm->tm_wday == 6)
                {
                    fallBackConst = 604800;
                }
                else if (ctm->tm_wday == 1)
                {
                    fallBackConst = 259200;
                }
                else
                    fallBackConst = 86400;
                delete ctm;

            }
            CtiCCSubstationBusStore::getInstance()->reloadMapOfBanksToControlByLikeDay(getPAOId(), 0, &controlid_action_map,lastSendTime, fallBackConst);
            std::map <long, long>::iterator iter = controlid_action_map.begin();
            while (iter != controlid_action_map.end())
            {
                {
                    int capCount = 0;
                    CtiCCCapBankPtr bank = CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(iter->first, capCount)->second;;
                    if (bank != NULL)
                    {
                        CtiCCFeederPtr feed = CtiCCSubstationBusStore::getInstance()->findFeederByPAObjectID(bank->getParentId());
                        if (feed != NULL)
                        {
                            if (feed->getParentId() == getPAOId())
                            {
                                string text = "";
                                request = feed->createForcedVarRequest(bank, pointChanges, ccEvents, iter->second, "LikeDay Control");

                                if( request != NULL )
                                {
                                    feed->createForcedVarConfirmation(bank, pointChanges, ccEvents, "LikeDay Control");


                                    pilMessages.push_back(request);
                                    setLastOperationTime(currentDateTime);
                                    setLastFeederControlledPAOId(feed->getPAOId());
                                    //setLastFeederControlledPosition(currentPosition);
                                    feed->setLastOperationTime(currentDateTime);
                                    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                                }
                                setBusUpdatedFlag(TRUE);
                            }
                        }
                    }
                }
                iter = controlid_action_map.erase(iter);
            }

        }

    }
    return *this;
}

BOOL CtiCCSubstationBus::isDataOldAndFallBackNecessary()
{
    BOOL retVal = FALSE;
    CtiTime timeNow = CtiTime();


    if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        for (LONG i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->isDataOldAndFallBackNecessary(getControlUnits()))
            {
                currentFeeder->setLikeDayControlFlag(TRUE);
                retVal = TRUE;

            }
            else
                currentFeeder->setLikeDayControlFlag(FALSE);
        }

    }
    else if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
    {
        if (getLikeDayControlFlag())
        {
            if (getNewPointDataReceivedFlag())
            {
                setLikeDayControlFlag(FALSE);
                for (LONG i = 0; i < _ccfeeders.size();i++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                    currentFeeder->setLikeDayControlFlag(FALSE);
                }
            }
            else
            {
                for (LONG i = 0; i < _ccfeeders.size();i++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                    if (currentFeeder->getNewPointDataReceivedFlag())
                    {
                        setLikeDayControlFlag(FALSE);
                        break;
                    }
                }
                if (!getLikeDayControlFlag())
                {
                    for (LONG i = 0; i < _ccfeeders.size();i++)
                    {
                        CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                        currentFeeder->setLikeDayControlFlag(FALSE);

                    }
                }

            }
        }
        else
        {

            if (getLikeDayFallBack() )
            {
                 if ( !stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) )
                 {
                     if (timeNow > getLastVoltPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                     {
                          retVal = TRUE;
                     }
                     else
                     {

                         for (LONG i = 0; i < _ccfeeders.size();i++)
                         {
                             CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                             if (timeNow > currentFeeder->getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                             {
                                 retVal = TRUE;
                                 break;
                             }
                         }
                     }
                 }
                 else if ( !stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::PF_BY_KVARControlUnits) )
                 {
                     if (timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                         timeNow > getLastWattPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT)
                     {
                          retVal = TRUE;
                     }
                     else
                     {
                         for (LONG i = 0; i < _ccfeeders.size();i++)
                         {
                             CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                             if (timeNow > currentFeeder->getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                             {
                                 retVal = TRUE;
                                 break;
                             }
                         }
                     }

                 }
                 else //if( !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::KVARControlUnits) )
                 {
                     if (timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                     {
                         retVal = TRUE;
                     }
                     else
                     {
                         for (LONG i = 0; i < _ccfeeders.size();i++)
                         {
                             CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                             if (timeNow > currentFeeder->getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                             {
                                 retVal = TRUE;
                                 break;
                             }
                         }
                     }
                 }
            }
        }
    }
    else
    {

        if (getLikeDayControlFlag())
        {
            if (getNewPointDataReceivedFlag())
            {
                setLikeDayControlFlag(FALSE);
                for (LONG i = 0; i < _ccfeeders.size();i++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                    currentFeeder->setLikeDayControlFlag(FALSE);
                }
            }
        }
        else
        {
            if (getLikeDayFallBack())
            {
                if ( !stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) )
                {
                    if (timeNow > getLastVoltPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                        timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                    {
                         retVal = TRUE;
                    }
                }
                else if ( !stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::PF_BY_KVARControlUnits) )
                {
                     if (timeNow > getLastWattPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                        timeNow > getLastVoltPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                        timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                    {
                         retVal = TRUE;
                    }
                }
                else //if( !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::KVARControlUnits) )
                {
                    if (timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT )
                    {
                        retVal = TRUE;
                    }
                }
            }
        }
    }

    return retVal;
}


CtiCCSubstationBus& CtiCCSubstationBus::verifyControlledStatusFlags()
{

    for(LONG j=0;j<getCCFeeders().size();j++)
    {
        CtiCCFeeder* currentFeeder = getCCFeeders().at(j);
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(LONG k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                setRecentlyControlledFlag(TRUE);
                currentFeeder->setRecentlyControlledFlag(TRUE);
                setLastFeederControlledPAOId(currentFeeder->getPAOId());
                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                j = getCCFeeders().size();
                k = ccCapBanks.size();
            }
        }
    }
    return *this;
}

CtiCCSubstationBus& CtiCCSubstationBus::addAllSubPointsToMsg(CtiCommandMsg *pointAddMsg)
{

    if( getCurrentVarLoadPointId() > 0 )
    {
        pointAddMsg->insert(getCurrentVarLoadPointId());
    }
    if( getCurrentWattLoadPointId() > 0 )
    {
        pointAddMsg->insert(getCurrentWattLoadPointId());
    }
    if (getCurrentVoltLoadPointId() > 0)
    {
        pointAddMsg->insert(getCurrentVoltLoadPointId());
    }
    if (getEstimatedVarLoadPointId() > 0)
    {
        pointAddMsg->insert(getEstimatedVarLoadPointId());
    }
    if (getDailyOperationsAnalogPointId() > 0)
    {
        pointAddMsg->insert(getDailyOperationsAnalogPointId());
    }
    if (getPowerFactorPointId() > 0)
    {
        pointAddMsg->insert(getPowerFactorPointId());
    }
    if (getEstimatedPowerFactorPointId() > 0)
    {
        pointAddMsg->insert(getEstimatedPowerFactorPointId());
    }
    if (getSwitchOverPointId() > 0)
    {
        pointAddMsg->insert(getSwitchOverPointId());
    }

    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCSubstationBus::restoreGuts(RWvistream& istrm)
{

    CtiTime tempTime2;
    CtiTime tempTime3;
    string tempString;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _parentId
    >> _maxdailyoperation
    >> _maxoperationdisableflag
    >> _currentvarloadpointid
    >> _currentvarloadpointvalue
    >> _currentwattloadpointid
    >> _currentwattloadpointvalue
    >> _maplocationid
    >> _controlunits
    >> _decimalplaces
    >> _newpointdatareceivedflag
    >> _busupdatedflag
    >> tempTime2
    >> _estimatedvarloadpointid
    >> _estimatedvarloadpointvalue
    >> _dailyoperationsanalogpointid
    >> _powerfactorpointid
    >> _estimatedpowerfactorpointid
    >> _currentdailyoperations
    >> _peaktimeflag
    >> _recentlycontrolledflag
    >> tempTime3
    >> _varvaluebeforecontrol
    >> _powerfactorvalue
    >> _estimatedpowerfactorvalue
    >> _currentvarpointquality
    >> _waivecontrolflag
    >> _peaklag
    >> _offpklag
    >> _peaklead
    >> _offpklead
    >> _currentvoltloadpointid
    >> _currentvoltloadpointvalue
    >> _verificationFlag
    >> _switchOverStatus
    >> _currentwattpointquality
    >> _currentvoltpointquality
    >> _targetvarvalue
    >> _solution
    >> _ovUvDisabledFlag
    >> _peakpfsetpoint
    >> _offpkpfsetpoint
    >> _controlmethod
    >> _phaseAvalue
    >> _phaseBvalue
    >> _phaseCvalue
    >> _likeDayControlFlag
    >> _displayOrder
    >> _voltReductionFlag
    >> _ccfeeders;

    _lastcurrentvarpointupdatetime = CtiTime(tempTime2);
    _lastoperationtime = CtiTime(tempTime3);

    if (_switchOverStatus && _dualBusEnable)
    {
        setAllAltSubValues(_currentvoltloadpointvalue, _currentvarloadpointvalue, _currentwattloadpointvalue);
    }
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    DOUBLE tempVar;
    DOUBLE tempVolt;
    DOUBLE tempWatt;

    DOUBLE temppowerfactorvalue = _powerfactorvalue;
    DOUBLE tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue;
    if (_dualBusEnable && _switchOverStatus)
    {
        tempVolt = _altSubVoltVal;
        tempVar  = _altSubVarVal;
        tempWatt = _altSubWattVal;
    }
    else
    {
        tempVolt = _currentvoltloadpointvalue;
        tempVar  = _currentvarloadpointvalue;
        tempWatt = _currentwattloadpointvalue;
    }
    if( _powerfactorvalue > 1 )
    {
        temppowerfactorvalue = _powerfactorvalue - 2;
    }
    if( _estimatedpowerfactorvalue > 1 )
    {
        tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue - 2;
    }

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _parentId
    << _maxdailyoperation
    << _maxoperationdisableflag
    << _currentvarloadpointid
    << tempVar
    << _currentwattloadpointid
    << tempWatt
    << _maplocationid
    << _controlunits
    << _decimalplaces
    << _newpointdatareceivedflag
    << _busupdatedflag
    << _lastcurrentvarpointupdatetime
    << _estimatedvarloadpointid
    << _estimatedvarloadpointvalue
    << _dailyoperationsanalogpointid
    << _powerfactorpointid
    << _estimatedpowerfactorpointid
    << _currentdailyoperations
    << _peaktimeflag
    << _recentlycontrolledflag
    << _lastoperationtime
    << _varvaluebeforecontrol
    << temppowerfactorvalue
    << tempestimatedpowerfactorvalue
    << _currentvarpointquality
    << _waivecontrolflag
    << _peaklag
    << _offpklag
    << _peaklead
    << _offpklead
    << _currentvoltloadpointid
    << tempVolt
    << _verificationFlag
    << _switchOverStatus
    << _currentwattpointquality
    << _currentvoltpointquality
    << _targetvarvalue
    << _solution
    << _ovUvDisabledFlag
    << _peakpfsetpoint
    << _offpkpfsetpoint
    << _controlmethod
    << _phaseAvalue
    << _phaseBvalue
    << _phaseCvalue
    << _likeDayControlFlag
    << _displayOrder
    << _voltReductionFlag
    << _ccfeeders;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::operator=(const CtiCCSubstationBus& right)
{
    if( this != &right )
    {
        _paoid = right.getPAOId();
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _parentId = right._parentId;
        _parentName = right._parentName;
        _strategyId = right._strategyId;
        _strategyName = right._strategyName;
        _controlmethod = right._controlmethod;
        _maxdailyoperation = right._maxdailyoperation;
        _maxoperationdisableflag = right._maxoperationdisableflag;
        _peakstarttime = right._peakstarttime;
        _peakstoptime = right._peakstoptime;
        _currentvarloadpointid = right._currentvarloadpointid;
        _currentvarloadpointvalue = right._currentvarloadpointvalue;
        _currentwattloadpointid = right._currentwattloadpointid;
        _currentvoltloadpointvalue = right._currentwattloadpointvalue;
        _currentvoltloadpointid = right._currentvoltloadpointid;
        _currentwattloadpointvalue = right._currentvoltloadpointvalue;
        _controlinterval = right._controlinterval;
        _maxconfirmtime = right._maxconfirmtime;
        _minconfirmpercent = right._minconfirmpercent;
        _failurepercent = right._failurepercent;
        _daysofweek = right._daysofweek;
        _maplocationid = right._maplocationid;
        _controlunits = right._controlunits;
        _controldelaytime = right._controldelaytime;
        _controlsendretries = right._controlsendretries;
        _peaklag = right._peaklag;
        _offpklag = right._offpklag;
        _peaklead = right._peaklead;
        _offpklead = right._offpklead;
        _peakVARlag = right._peakVARlag;
        _offpkVARlag = right._offpkVARlag;
        _peakVARlead = right._peakVARlead;
        _offpkVARlead = right._offpkVARlead;
        _peakpfsetpoint = right._peakpfsetpoint;
        _offpkpfsetpoint = right._offpkpfsetpoint;
        _decimalplaces = right._decimalplaces;
        _nextchecktime = right._nextchecktime;
        _newpointdatareceivedflag = right._newpointdatareceivedflag;
        _busupdatedflag = right._busupdatedflag;
        _lastcurrentvarpointupdatetime = right._lastcurrentvarpointupdatetime;
        _estimatedvarloadpointid = right._estimatedvarloadpointid;
        _estimatedvarloadpointvalue = right._estimatedvarloadpointvalue;
        _dailyoperationsanalogpointid = right._dailyoperationsanalogpointid;
        _powerfactorpointid = right._powerfactorpointid;
        _estimatedpowerfactorpointid = right._estimatedpowerfactorpointid;
        _currentdailyoperations = right._currentdailyoperations;
        _peaktimeflag = right._peaktimeflag;
        _recentlycontrolledflag = right._recentlycontrolledflag;
        _lastoperationtime = right._lastoperationtime;
        _varvaluebeforecontrol = right._varvaluebeforecontrol;
        _lastfeedercontrolledpaoid = right._lastfeedercontrolledpaoid;
        _lastfeedercontrolledposition = right._lastfeedercontrolledposition;
        _powerfactorvalue = right._powerfactorvalue;
        _kvarsolution = right._kvarsolution;
        _estimatedpowerfactorvalue = right._estimatedpowerfactorvalue;
        _currentvarpointquality = right._currentvarpointquality;
        _currentwattpointquality = right._currentwattpointquality;
        _currentvoltpointquality = right._currentvoltpointquality;
        _waivecontrolflag = right._waivecontrolflag;
        _additionalFlags = right._additionalFlags;
        _currentVerificationCapBankId = right._currentVerificationCapBankId;
        _currentVerificationFeederId = right._currentVerificationFeederId;
        _verificationStrategy = right._verificationStrategy;
        _capBankToVerifyInactivityTime = right._capBankToVerifyInactivityTime;
        _verificationFlag = right._verificationFlag;
        delete_container(_ccfeeders);
        _performingVerificationFlag = right._performingVerificationFlag;
        _verificationDoneFlag = right._verificationDoneFlag;
        _overlappingSchedulesVerificationFlag = right._overlappingSchedulesVerificationFlag;
        _preOperationMonitorPointScanFlag = right._preOperationMonitorPointScanFlag;
        _operationSentWaitFlag = right._operationSentWaitFlag;
        _postOperationMonitorPointScanFlag = right._postOperationMonitorPointScanFlag;
        _reEnableBusFlag = right._reEnableBusFlag;
        _waitForReCloseDelayFlag = right._waitForReCloseDelayFlag;
        _waitToFinishRegularControlFlag = right._waitToFinishRegularControlFlag;
        _maxDailyOpsHitFlag = right._maxDailyOpsHitFlag;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _correctionNeededNoBankAvailFlag = right._correctionNeededNoBankAvailFlag;
        _likeDayControlFlag = right._likeDayControlFlag;
        _voltReductionFlag = right._voltReductionFlag;
        _voltReductionControlId = right._voltReductionControlId;
        _sendMoreTimeControlledCommandsFlag = right._sendMoreTimeControlledCommandsFlag;

        _altDualSubId = right._altDualSubId;
        _switchOverPointId = right._switchOverPointId;
        _dualBusEnable = right._dualBusEnable;
        _switchOverStatus = right._switchOverStatus;
        _altSubControlValue = right._altSubControlValue;
        _eventSeq = right._eventSeq;
        _multiMonitorFlag = right._multiMonitorFlag;

        _altSubVoltVal = right._altSubVoltVal;
        _altSubVarVal  = right._altSubVarVal;
        _altSubWattVal = right._altSubWattVal;
        _lastVerificationCheck = right._lastVerificationCheck;

        _solution = right._solution;
        _targetvarvalue = right._targetvarvalue;
        _integrateflag = right._integrateflag;
        _integrateperiod = right._integrateperiod;
        _likedayfallback = right._likedayfallback;
        _iVControlTot = right._iVControlTot;
        _iVCount = right._iVCount;
        _iWControlTot = right._iWControlTot;
        _iWCount = right._iWCount;

        _iVControl = right._iVControl;
        _iWControl = right._iWControl;

        _usePhaseData = right._usePhaseData;
        _phaseBid = right._phaseBid;
        _phaseCid = right._phaseCid;
        _totalizedControlFlag = right._totalizedControlFlag;
        _phaseAvalue = right._phaseAvalue;
        _phaseBvalue = right._phaseBvalue;
        _phaseCvalue = right._phaseCvalue;
        _phaseAvalueBeforeControl = right._phaseAvalueBeforeControl;
        _phaseBvalueBeforeControl = right._phaseBvalueBeforeControl;
        _phaseCvalueBeforeControl = right._phaseCvalueBeforeControl;

        _lastWattPointTime = right._lastWattPointTime;
        _lastVoltPointTime = right._lastVoltPointTime;

        _displayOrder = right._displayOrder;

        _ccfeeders.clear();
        LONG i=0;
        for(i=0;i<right._ccfeeders.size();i++)
        {
            _ccfeeders.push_back(((CtiCCFeeder*)right._ccfeeders.at(i))->replicate());
        }

        delete_container(_todControls);
        _todControls.clear();
        for(i=0;i<right._todControls.size();i++)
        {
            CtiTimeOfDayController* tmp = new CtiTimeOfDayController;
            tmp->_percentToClose = ((CtiTimeOfDayController*)right._todControls[i])->_percentToClose;
            tmp->_secsFromMidnight = ((CtiTimeOfDayController*)right._todControls[i])->_secsFromMidnight;
            _todControls.push_back(tmp);
        }
        _percentToClose = right._percentToClose;

        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;

        regression = right.regression;
        regressionA = right.regressionA;
        regressionB = right.regressionB;
        regressionC = right.regressionC;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCSubstationBus::operator==(const CtiCCSubstationBus& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCSubstationBus::operator!=(const CtiCCSubstationBus& right) const
{
    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSubstationBus* CtiCCSubstationBus::replicate() const
{
    return(new CtiCCSubstationBus(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::restore(RWDBReader& rdr)
{
    string tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _disableflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["currentvarloadpointid"] >> _currentvarloadpointid;
    rdr["currentwattloadpointid"] >> _currentwattloadpointid;
    rdr["maplocationid"] >> _maplocationid;
    rdr["currentvoltloadpointid"] >> _currentvoltloadpointid;
    rdr["AltSubID"] >> _altDualSubId;
    rdr["SwitchPointID"] >> _switchOverPointId;
    rdr["DualBusEnabled"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _dualBusEnable = (tempBoolString=="y"?TRUE:FALSE);
    rdr["multiMonitorControl"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _multiMonitorFlag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["usephasedata"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _usePhaseData = (tempBoolString=="y"?TRUE:FALSE);
    rdr["phaseb"] >> _phaseBid;
    rdr["phasec"] >> _phaseCid;
    rdr["controlflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _totalizedControlFlag = (tempBoolString=="y"?TRUE:FALSE);

    rdr["voltreductionpointid"] >> _voltReductionControlId;

    _parentId =  0;
    _decimalplaces = 2;
    _estimatedvarloadpointid = 0;
    _dailyoperationsanalogpointid = 0;
    _powerfactorpointid = 0;
    _estimatedpowerfactorpointid = 0;
    _strategyId = 0;

    //initialize strategy members
    setStrategyName("(none)");
    setControlMethod("SubstationBus");
    setMaxDailyOperation(0);
    setMaxOperationDisableFlag(FALSE);
    setPeakLag(0);
    setOffPeakLag(0);
    setPeakLead(0);
    setOffPeakLead(0);
    setPeakVARLag(0);
    setOffPeakVARLag(0);
    setPeakVARLead(0);
    setOffPeakVARLead(0);
    setPeakStartTime(0);
    setPeakStopTime(0);
    setControlInterval(0);
    setMaxConfirmTime(0);
    setMinConfirmPercent(0);
    setFailurePercent(0);
    setDaysOfWeek("NYYYYYNN");
    setControlUnits("KVAR");
    setControlDelayTime(0);
    setControlSendRetries(0);
    setDecimalPlaces(0);

    setSendMoreTimeControlledCommandsFlag(FALSE);
    setLikeDayControlFlag(FALSE);
    _todControls.clear();
    _percentToClose = 0;

    figureNextCheckTime();
    setNewPointDataReceivedFlag(FALSE);
    setBusUpdatedFlag(FALSE);
    setLastCurrentVarPointUpdateTime(gInvalidCtiTime);
    setEstimatedVarLoadPointValue(0.0);
    setCurrentDailyOperations(0);
    setPeakTimeFlag(TRUE);
    setRecentlyControlledFlag(FALSE);
    setLastOperationTime(gInvalidCtiTime);
    setVarValueBeforeControl(0.0);
    setLastFeederControlledPAOId(0);
    setLastFeederControlledPosition(-1);
    setPowerFactorValue(-1000000.0);
    setKVARSolution(0.0);
    setEstimatedPowerFactorValue(-1000000.0);
    setCurrentVarPointQuality(NormalQuality);
    setCurrentVoltPointQuality(NormalQuality);
    setCurrentWattPointQuality(NormalQuality);
    setWaiveControlFlag(FALSE);

    _additionalFlags = string("NNNNNNNNNNNNNNNNNNNN");
    setVerificationFlag(FALSE);
    setPerformingVerificationFlag(FALSE);
    setVerificationDoneFlag(FALSE);
    setOverlappingVerificationFlag(FALSE);
    setPreOperationMonitorPointScanFlag(FALSE);
    setOperationSentWaitFlag(FALSE);
    setPostOperationMonitorPointScanFlag(FALSE);
    setReEnableBusFlag(FALSE);
    setWaitForReCloseDelayFlag(FALSE);
    setWaitToFinishRegularControlFlag(FALSE);
    setMaxDailyOpsHitFlag(FALSE);
    setOvUvDisabledFlag(FALSE);
    setCorrectionNeededNoBankAvailFlag(FALSE);
    setCurrentVerificationCapBankId(-1);
    setCurrentVerificationFeederId(-1);
    setCurrentVerificationCapBankState(0);
    setVerificationStrategy(-1);
    setCapBankInactivityTime(-1);

    setSwitchOverStatus(FALSE);
    setEventSequence(0);

    setTargetVarValue(0);
    setSolution("IDLE");
    _lastVerificationCheck = gInvalidCtiTime;

    _insertDynamicDataFlag = TRUE;

    if( _currentvarloadpointid <= 0 )
    {
        _currentvarloadpointvalue = 0;
    }
    if( _currentwattloadpointid <= 0 )
    {
        _currentwattloadpointvalue = 0;
    }
    if( _currentvoltloadpointid <= 0 )
    {
        _currentvoltloadpointvalue = 0;
        setAltSubControlValue(0);
    }
    _currentvarloadpointvalue = 0;
    _currentwattloadpointvalue = 0;
    _currentvoltloadpointvalue = 0;
    _altSubControlValue = 0;

    if ( _switchOverPointId <= 0 )
    {
        _switchOverPointId = 0;
    }
    _altSubVoltVal = 0;
    _altSubVarVal = 0;
    _altSubWattVal = 0;

    if (_voltReductionControlId <= 0 )
    {
        setVoltReductionFlag(FALSE);
    }

    setIVControlTot(0);
    setIVCount(0);
    setIWControlTot(0);
    setIWCount(0);
    setIVControl(0);
    setIWControl(0);

    CtiTime time;
    setPhaseAValue(0,time);
    setPhaseBValue(0,time);
    setPhaseCValue(0,time);
    setPhaseAValueBeforeControl(0);
    setPhaseBValueBeforeControl(0);
    setPhaseCValueBeforeControl(0);

    setLastWattPointTime(gInvalidCtiTime);
    setLastVoltPointTime(gInvalidCtiTime);

    setParentName("none");
}

void CtiCCSubstationBus::setStrategyValues(CtiCCStrategyPtr strategy)
{
    string tempBoolString;

    _strategyName = strategy->getStrategyName();
    _controlmethod = strategy->getControlMethod();
    _maxdailyoperation = strategy->getMaxDailyOperation();
    _maxoperationdisableflag = strategy->getMaxOperationDisableFlag();
    _peaklag = strategy->getPeakLag();
    _offpklag = strategy->getOffPeakLag();
    _peaklead = strategy->getPeakLead();
    _offpklead = strategy->getOffPeakLead();
    _peakVARlag = strategy->getPeakVARLag();
    _offpkVARlag = strategy->getOffPeakVARLag();
    _peakVARlead = strategy->getPeakVARLead();
    _offpkVARlead = strategy->getOffPeakVARLead();
    _peakpfsetpoint = strategy->getPeakPFSetPoint();
    _offpkpfsetpoint = strategy->getOffPeakPFSetPoint();
    _peakstarttime = strategy->getPeakStartTime();
    _peakstoptime = strategy->getPeakStopTime();
    _controlinterval = strategy->getControlInterval();
    _maxconfirmtime = strategy->getMaxConfirmTime();
    _minconfirmpercent = strategy->getMinConfirmPercent();
    _failurepercent = strategy->getFailurePercent();
    _daysofweek = strategy->getDaysOfWeek();
    _controlunits = strategy->getControlUnits();
    _controldelaytime = strategy->getControlDelayTime();
    _controlsendretries = strategy->getControlSendRetries();
    _integrateflag = strategy->getIntegrateFlag();
    _integrateperiod = strategy->getIntegratePeriod();
    _likedayfallback = strategy->getLikeDayFallBack();

    setTODControls(strategy);

}

void CtiCCSubstationBus::setTODControls(CtiCCStrategyPtr strategy)
{
    CtiTODC_SVector tmpVec = strategy->getTimeOfDayControllers();
    _todControls.clear();
    for(LONG i=0;i<tmpVec.size();i++)
    {
        CtiTimeOfDayController* tmp = new CtiTimeOfDayController;
        tmp->_percentToClose = ((CtiTimeOfDayController*)tmpVec[i])->_percentToClose;
        tmp->_secsFromMidnight = ((CtiTimeOfDayController*)tmpVec[i])->_secsFromMidnight;
        _todControls.push_back(tmp);
    }
}


void CtiCCSubstationBus::setDynamicData(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    CtiTime dynamicTimeStamp;
    string tempBoolString;


    /*rdr["currentvarpointvalue"] >> isNull;
    if( !isNull )
    { */
        rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
        rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
        rdr["nextchecktime"] >> _nextchecktime;
        rdr["newpointdatareceivedflag"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        _newpointdatareceivedflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["busupdatedflag"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        _busupdatedflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
        rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
        rdr["currentdailyoperations"] >> _currentdailyoperations;
        rdr["peaktimeflag"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        _peaktimeflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["recentlycontrolledflag"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        _recentlycontrolledflag = (tempBoolString=="y"?TRUE:FALSE);
        rdr["lastoperationtime"] >> _lastoperationtime;
        rdr["varvaluebeforecontrol"] >> _varvaluebeforecontrol;
        rdr["lastfeederpaoid"] >> _lastfeedercontrolledpaoid;
        rdr["lastfeederposition"] >> _lastfeedercontrolledposition;
        rdr["ctitimestamp"] >> dynamicTimeStamp;
        rdr["powerfactorvalue"] >> _powerfactorvalue;
        rdr["kvarsolution"] >> _kvarsolution;
        rdr["estimatedpfvalue"] >> _estimatedpowerfactorvalue;
        rdr["currentvarpointquality"] >> _currentvarpointquality;
        rdr["waivecontrolflag"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        _waivecontrolflag = (tempBoolString=="y"?TRUE:FALSE);

        rdr["additionalflags"] >> _additionalFlags;
        std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
        _verificationFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);
        _performingVerificationFlag = (_additionalFlags[1]=='y'?TRUE:FALSE);
        _verificationDoneFlag = (_additionalFlags[2]=='y'?TRUE:FALSE);
        _overlappingSchedulesVerificationFlag = (_additionalFlags[3]=='y'?TRUE:FALSE);
        _preOperationMonitorPointScanFlag = (_additionalFlags[4]=='y'?TRUE:FALSE);
        _operationSentWaitFlag = (_additionalFlags[5]=='y'?TRUE:FALSE);
        _postOperationMonitorPointScanFlag = (_additionalFlags[6]=='y'?TRUE:FALSE);
        _reEnableBusFlag = (_additionalFlags[7]=='y'?TRUE:FALSE);
        _waitForReCloseDelayFlag = (_additionalFlags[8]=='y'?TRUE:FALSE);
        _waitToFinishRegularControlFlag = (_additionalFlags[9]=='y'?TRUE:FALSE);
        _maxDailyOpsHitFlag = (_additionalFlags[10]=='y'?TRUE:FALSE);
        _ovUvDisabledFlag = (_additionalFlags[11]=='y'?TRUE:FALSE);
        _correctionNeededNoBankAvailFlag = (_additionalFlags[12]=='y'?TRUE:FALSE);
        _likeDayControlFlag = (_additionalFlags[13]=='y'?TRUE:FALSE);
        _voltReductionFlag = (_additionalFlags[14]=='y'?TRUE:FALSE);
        _sendMoreTimeControlledCommandsFlag  = (_additionalFlags[15]=='y'?TRUE:FALSE);

        if (!_TIME_OF_DAY_VAR_CONF)
        {
            setSendMoreTimeControlledCommandsFlag(FALSE);
        }
        if (!stringCompareIgnoreCase(_controlmethod,CtiCCSubstationBus::TimeOfDayMethod)||
            _likeDayControlFlag)
            figureNextCheckTime();
        if (_voltReductionControlId <= 0 )
        {
            setVoltReductionFlag(FALSE);
        }

        rdr["currverifycbid"] >> _currentVerificationCapBankId;
        rdr["currverifyfeederid"] >> _currentVerificationFeederId;
        rdr["currverifycborigstate"] >> _currentCapBankToVerifyAssumedOrigState;
        rdr["verificationstrategy"] >> _verificationStrategy;
        rdr["cbinactivitytime"] >> _capBankToVerifyInactivityTime;
        rdr["currentvoltpointvalue"] >> _currentvoltloadpointvalue;

        rdr["switchPointStatus"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        _switchOverStatus = (tempBoolString=="y"?TRUE:FALSE);
        rdr["altSubControlValue"] >> _altSubControlValue;
        rdr["eventSeq"] >> _eventSeq;
        rdr["currentwattpointquality"] >> _currentwattpointquality;
        rdr["currentvoltpointquality"] >> _currentvoltpointquality;

        _altSubVoltVal = _currentvoltloadpointvalue;
        _altSubVarVal = _currentvarloadpointvalue;
        _altSubWattVal = _currentwattloadpointvalue;

        rdr["ivcontroltot"] >> _iVControlTot;
        rdr["ivcount"] >> _iVCount;
        rdr["iwcontroltot"] >> _iWControlTot;
        rdr["iwcount"] >> _iWCount;

        rdr["phaseavalue"] >> _phaseAvalue;
        rdr["phasebvalue"] >> _phaseBvalue;
        rdr["phasecvalue"] >> _phaseCvalue;

        rdr["lastwattpointtime"] >> _lastWattPointTime;
        rdr["lastvoltpointtime"] >> _lastVoltPointTime;

        _insertDynamicDataFlag = FALSE;

        _dirty = false;
    //}
}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the string representation of a double
---------------------------------------------------------------------------*/
string CtiCCSubstationBus::doubleToString(DOUBLE doubleVal)
{
    char tempchar[80] = "";
    string retString = string("");
    _snprintf(tempchar,80,"%d",(int)(doubleVal+0.5));
    retString += tempchar;

    return retString;
}

void CtiCCSubstationBus::deleteCCFeeder(long feederId)
{
    CtiFeeder_vec& ccFeeders = getCCFeeders();
    CtiFeeder_vec::iterator itr = ccFeeders.begin();
    while(itr != ccFeeders.end())
    {
        CtiCCFeeder *feeder = *itr;
        if (feeder->getPAOId() == feederId)
        {
            itr = getCCFeeders().erase(itr);
            break;
        }else
            ++itr;
    }

    return;
}

string CtiCCSubstationBus::getVerificationString()
{
    string text = string("Verify");
    switch (getVerificationStrategy())
    {
        case CtiPAOScheduleManager::AllBanks:
        {
            text += " All";
            break;
        }
        case CtiPAOScheduleManager::FailedAndQuestionableBanks:
        {
            text += " Failed and Questionable";
            break;
        }
        case CtiPAOScheduleManager::FailedBanks:
        {
            text += " Failed";
            break;
        }
        case CtiPAOScheduleManager::QuestionableBanks:
        {
            text += " Questionable";
            break;
        }
        case CtiPAOScheduleManager::SelectedForVerificationBanks:
        {
            break;
        }
        case CtiPAOScheduleManager::BanksInactiveForXTime:
        {
            break;
        }
        case CtiPAOScheduleManager::StandAloneBanks:
        {
            text += " Standalone";
            break;
        }
    default:
        break;

    }
    text += " Banks";
    return text;
}

bool CtiCCSubstationBus::checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC)
{
    if(!_RATE_OF_CHANGE)
        return false;
    if( getUsePhaseData() && !getTotalizedControlFlag() )
    {
        if( regA.depthMet() && regB.depthMet() && regC.depthMet() )
            return true;
    }
    else
    {
        return reg.depthMet();
    }
    return false;
}

/* Public Static members */
const string CtiCCSubstationBus::IndividualFeederControlMethod   = "IndividualFeeder";
const string CtiCCSubstationBus::SubstationBusControlMethod      = "SubstationBus";
const string CtiCCSubstationBus::BusOptimizedFeederControlMethod = "BusOptimizedFeeder";
const string CtiCCSubstationBus::ManualOnlyControlMethod         = "ManualOnly";
const string CtiCCSubstationBus::TimeOfDayMethod                 = "TimeOfDay";

const string CtiCCSubstationBus::KVARControlUnits         = "KVAR";
const string CtiCCSubstationBus::VoltControlUnits         = "Volts";
const string CtiCCSubstationBus::MultiVoltControlUnits    = "Multi Volt";
const string CtiCCSubstationBus::MultiVoltVarControlUnits = "Multi Volt/VAR";
const string CtiCCSubstationBus::PF_BY_KVARControlUnits   = "P-Factor KW/KVar";
const string CtiCCSubstationBus::PF_BY_KQControlUnits     = "P-Factor KW/KQ";

