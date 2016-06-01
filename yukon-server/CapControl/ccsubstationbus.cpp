#include "precompiled.h"

#include "ccsubstationbus.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "tbl_pt_alarm.h"
#include "MsgVerifyBanks.h"

using Cti::CapControl::PointResponse;
using Cti::CapControl::PointResponsePtr;
using Cti::CapControl::PointIdVector;
using Cti::CapControl::PointResponseKey;
using Cti::CapControl::ConvertIntToVerificationStrategy;
using Cti::CapControl::setVariableIfDifferent;
using Cti::CapControl::PointResponseDaoPtr;
using Cti::CapControl::EventLogEntry;
using Cti::CapControl::EventLogEntries;
using Cti::CapControl::Database::DatabaseDaoFactory;
using Cti::CapControl::deserializeFlag;
using Cti::CapControl::serializeFlag;

using std::endl;
using std::set;
using std::make_pair;
using std::string;
using std::map;

extern unsigned long _CC_DEBUG;
extern bool _IGNORE_NOT_NORMAL_FLAG;
extern unsigned long _SEND_TRIES;
extern bool _USE_FLIP_FLAG;
extern unsigned long _POINT_AGE;
extern unsigned long _SCAN_WAIT_EXPIRE;
extern bool _ALLOW_PARALLEL_TRUING;
extern bool _LOG_MAPID_INFO;
extern unsigned long _LIKEDAY_OVERRIDE_TIMEOUT;
extern bool _RATE_OF_CHANGE;
extern unsigned long _RATE_OF_CHANGE_DEPTH;
extern long _MAXOPS_ALARM_CATID;
extern bool _RETRY_ADJUST_LAST_OP_TIME;
extern double _IVVC_DEFAULT_DELTA;

DEFINE_COLLECTABLE( CtiCCSubstationBus, CTICCSUBSTATIONBUS_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstationBus::CtiCCSubstationBus( StrategyManager * strategyManager )
    :   Controllable( strategyManager ),
        _parentId( 0 ),
        _currentvarloadpointid( 0 ),
        _currentvarloadpointvalue( 0 ),
        _currentwattloadpointid( 0 ),
        _currentwattloadpointvalue( 0 ),
        _currentvoltloadpointid( 0 ),
        _currentvoltloadpointvalue( 0 ),
        _altDualSubId( 0 ),
        _altSubControlValue( 0 ),
        _switchOverPointId( 0 ),
        _switchOverStatus( false ),
        _primaryBusFlag( false ),
        _dualBusEnable( false ),
        _eventSeq( 0 ),
        _multiMonitorFlag( false ),
        _decimalplaces( 0 ),
        _newpointdatareceivedflag( false ),
        _busupdatedflag( false ),
        _estimatedvarloadpointid( 0 ),
        _estimatedvarloadpointvalue( 0 ),
        _dailyoperationsanalogpointid( 0 ),
        _powerfactorpointid( 0 ),
        _estimatedpowerfactorpointid( 0 ),
        _currentdailyoperations( 0 ),
        _peaktimeflag( true ),
        _recentlycontrolledflag( false ),
        _varvaluebeforecontrol( 0 ),
        _lastfeedercontrolledpaoid( 0 ),
        _lastfeedercontrolledposition( 0 ),
        _powerfactorvalue( -1 ),
        _kvarsolution( 0 ),
        _estimatedpowerfactorvalue( -1 ),
        _currentvarpointquality( NormalQuality),
        _currentwattpointquality( NormalQuality ),
        _currentvoltpointquality( NormalQuality ),
        _waivecontrolflag( false ),
        _currentVerificationCapBankId( -1 ),
        _currentVerificationFeederId( -1 ),
        _percentToClose( 0 ),
        _verificationFlag( false ),
        _performingVerificationFlag( false ),
        _verificationDoneFlag( false ),
        _overlappingSchedulesVerificationFlag( false ),
        _preOperationMonitorPointScanFlag( false ),
        _operationSentWaitFlag( false ),
        _postOperationMonitorPointScanFlag( false ),
        _reEnableBusFlag( false ),
        _waitForReCloseDelayFlag( false ),
        _waitToFinishRegularControlFlag( false ),
        _maxDailyOpsHitFlag( false ),
        _ovUvDisabledFlag( false ),
        _correctionNeededNoBankAvailFlag( false ),
        _likeDayControlFlag( false ),
        _voltReductionFlag( false ),
        _sendMoreTimeControlledCommandsFlag( false ),
        _voltReductionControlId( 0 ),
        _currentCapBankToVerifyAssumedOrigState( 0 ),
        _verificationStrategy( CtiPAOScheduleManager::Undefined ),
        _disableOvUvVerificationFlag( false ),
        _capBankToVerifyInactivityTime( -1 ),
        _targetvarvalue( 0 ),
        _displayOrder( 0 ),
        _altSubVoltVal( 0 ),
        _altSubVarVal( 0 ),
        _altSubWattVal( 0 ),
        _iVControlTot( 0 ),
        _iVCount( 0 ),
        _iWControlTot( 0 ),
        _iWCount( 0 ),
        _iVControl( 0 ),
        _iWControl( 0 ),
        _usePhaseData( false ),
        _phaseBid( 0 ),
        _phaseCid( 0 ),
        _totalizedControlFlag( false ),
        _phaseAvalue( 0 ),
        _phaseBvalue( 0 ),
        _phaseCvalue( 0 ),
        _phaseAvalueBeforeControl( 0 ),
        _phaseBvalueBeforeControl( 0 ),
        _phaseCvalueBeforeControl( 0 ),
        _commsStatePointId( 0 ),
        _disableBusPointId( 0 ),
        _solution( "IDLE" ),
        _parentName( "none" ),
        _lastcurrentvarpointupdatetime( gInvalidCtiTime ),
        _lastoperationtime( gInvalidCtiTime ),
        _lastVerificationCheck( gInvalidCtiTime ),
        _lastWattPointTime( gInvalidCtiTime ),
        _lastVoltPointTime( gInvalidCtiTime ),
        regression( _RATE_OF_CHANGE_DEPTH ),
        regressionA( _RATE_OF_CHANGE_DEPTH ),
        regressionB( _RATE_OF_CHANGE_DEPTH ),
        regressionC( _RATE_OF_CHANGE_DEPTH ),
        _insertDynamicDataFlag( true ),
        _dirty( false )
{
}

CtiCCSubstationBus::CtiCCSubstationBus( Cti::RowReader & rdr, StrategyManager * strategyManager )
    :   Controllable( rdr, strategyManager ),
        _parentId( 0 ),
        _currentvarloadpointid( 0 ),
        _currentvarloadpointvalue( 0 ),
        _currentwattloadpointid( 0 ),
        _currentwattloadpointvalue( 0 ),
        _currentvoltloadpointid( 0 ),
        _currentvoltloadpointvalue( 0 ),
        _altDualSubId( 0 ),
        _altSubControlValue( 0 ),
        _switchOverPointId( 0 ),
        _switchOverStatus( false ),
        _primaryBusFlag( false ),
        _dualBusEnable( false ),
        _eventSeq( 0 ),
        _multiMonitorFlag( false ),
        _decimalplaces( 0 ),
        _newpointdatareceivedflag( false ),
        _busupdatedflag( false ),
        _estimatedvarloadpointid( 0 ),
        _estimatedvarloadpointvalue( 0 ),
        _dailyoperationsanalogpointid( 0 ),
        _powerfactorpointid( 0 ),
        _estimatedpowerfactorpointid( 0 ),
        _currentdailyoperations( 0 ),
        _peaktimeflag( true ),
        _recentlycontrolledflag( false ),
        _varvaluebeforecontrol( 0 ),
        _lastfeedercontrolledpaoid( 0 ),
        _lastfeedercontrolledposition( 0 ),
        _powerfactorvalue( -1 ),
        _kvarsolution( 0 ),
        _estimatedpowerfactorvalue( -1 ),
        _currentvarpointquality( NormalQuality),
        _currentwattpointquality( NormalQuality ),
        _currentvoltpointquality( NormalQuality ),
        _waivecontrolflag( false ),
        _currentVerificationCapBankId( -1 ),
        _currentVerificationFeederId( -1 ),
        _percentToClose( 0 ),
        _verificationFlag( false ),
        _performingVerificationFlag( false ),
        _verificationDoneFlag( false ),
        _overlappingSchedulesVerificationFlag( false ),
        _preOperationMonitorPointScanFlag( false ),
        _operationSentWaitFlag( false ),
        _postOperationMonitorPointScanFlag( false ),
        _reEnableBusFlag( false ),
        _waitForReCloseDelayFlag( false ),
        _waitToFinishRegularControlFlag( false ),
        _maxDailyOpsHitFlag( false ),
        _ovUvDisabledFlag( false ),
        _correctionNeededNoBankAvailFlag( false ),
        _likeDayControlFlag( false ),
        _voltReductionFlag( false ),
        _sendMoreTimeControlledCommandsFlag( false ),
        _voltReductionControlId( 0 ),
        _currentCapBankToVerifyAssumedOrigState( 0 ),
        _verificationStrategy( CtiPAOScheduleManager::Undefined ),
        _disableOvUvVerificationFlag( false ),
        _capBankToVerifyInactivityTime( -1 ),
        _targetvarvalue( 0 ),
        _displayOrder( 0 ),
        _altSubVoltVal( 0 ),
        _altSubVarVal( 0 ),
        _altSubWattVal( 0 ),
        _iVControlTot( 0 ),
        _iVCount( 0 ),
        _iWControlTot( 0 ),
        _iWCount( 0 ),
        _iVControl( 0 ),
        _iWControl( 0 ),
        _usePhaseData( false ),
        _phaseBid( 0 ),
        _phaseCid( 0 ),
        _totalizedControlFlag( false ),
        _phaseAvalue( 0 ),
        _phaseBvalue( 0 ),
        _phaseCvalue( 0 ),
        _phaseAvalueBeforeControl( 0 ),
        _phaseBvalueBeforeControl( 0 ),
        _phaseCvalueBeforeControl( 0 ),
        _commsStatePointId( 0 ),
        _disableBusPointId( 0 ),
        _solution( "IDLE" ),
        _parentName( "none" ),
        _lastcurrentvarpointupdatetime( gInvalidCtiTime ),
        _lastoperationtime( gInvalidCtiTime ),
        _lastVerificationCheck( gInvalidCtiTime ),
        _lastWattPointTime( gInvalidCtiTime ),
        _lastVoltPointTime( gInvalidCtiTime ),
        regression( _RATE_OF_CHANGE_DEPTH ),
        regressionA( _RATE_OF_CHANGE_DEPTH ),
        regressionB( _RATE_OF_CHANGE_DEPTH ),
        regressionC( _RATE_OF_CHANGE_DEPTH ),
        _insertDynamicDataFlag( true ),
        _dirty( false )
{
    restore(rdr);

    if ( ! rdr[ "AdditionalFlags" ].isNull() ) 
    {
        setDynamicData( rdr );
    }

    if ( ! rdr[ "DECIMALPLACES" ].isNull() ) 
    {
        long decimalPlaces;

        rdr["DECIMALPLACES"] >> decimalPlaces;

        setDecimalPlaces( decimalPlaces );
    }
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationBus::~CtiCCSubstationBus()
{
    try
    {   delete_container(_ccfeeders);
        _ccfeeders.clear();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (AreaId) of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getParentId() const
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
long CtiCCSubstationBus::getDisplayOrder() const
{
    return _displayOrder;
}

/*---------------------------------------------------------------------------
    getIVControlTot

    Returns the Integrate Volt/Var Control total of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getIVControlTot() const
{
    return _iVControlTot;
}
/*---------------------------------------------------------------------------
    getIVCount

    Returns the Integrate Volt/Var Control count of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getIVCount() const
{
    return _iVCount;
}


/*---------------------------------------------------------------------------
    getIWControlTot

    Returns the Integrate Watt Control total of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getIWControlTot() const
{
    return _iWControlTot;
}

/*---------------------------------------------------------------------------
    getIWCount

    Returns the Integrate Watt Control count of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getIWCount() const
{
    return _iWCount;
}


/*---------------------------------------------------------------------------
    getIVControl

    Returns the Integrate Volt/Var Control of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getIVControl() const
{
    return _iVControl;
}
/*---------------------------------------------------------------------------
    getIWControl

    Returns the Integrate Watt Control of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getIWControl() const
{
    return _iWControl;
}

/*---------------------------------------------------------------------------
    get UsePhaseData flag

    Returns the usePhaseData flag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getUsePhaseData() const
{
    return _usePhaseData;
}

/*---------------------------------------------------------------------------
    getPhaseBid

    Returns the getPhaseB pointid of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getPhaseBId() const
{
    return _phaseBid;
}
/*---------------------------------------------------------------------------
    getPhaseCid

    Returns the getPhaseC pointid of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getPhaseCId() const
{
    return _phaseCid;
}

/*---------------------------------------------------------------------------
    getTotalizedControlFlag

    Returns the TotalizedControlFlag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getTotalizedControlFlag() const
{
    if (_dualBusEnable && _switchOverStatus)
    {
        return true;
    }
    return _totalizedControlFlag;
}

/*---------------------------------------------------------------------------
    getPhaseAValue

    Returns the getPhaseAValue VAR of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPhaseAValue() const
{
    return _phaseAvalue;
}

/*---------------------------------------------------------------------------
    getPhaseBValue

    Returns the getPhaseBValue VAR of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPhaseBValue() const
{
    return _phaseBvalue;
}

/*---------------------------------------------------------------------------
    getPhaseCValue

    Returns the getPhaseCValue VAR of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPhaseCValue() const
{
    return _phaseCvalue;
}
/*---------------------------------------------------------------------------
    getPhaseAValue

    Returns the getPhaseAValue VAR of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPhaseAValueBeforeControl() const
{
    return _phaseAvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getPhaseBValue

    Returns the getPhaseBValue VAR of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPhaseBValueBeforeControl() const
{
    return _phaseBvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getPhaseCValue

    Returns the getPhaseCValue VAR of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPhaseCValueBeforeControl() const
{
    return _phaseCvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentVarLoadPointId() const
{
    return _currentvarloadpointid;
}

/**
 * Returns var point id if totalized or the point ids to phase
 * A, B, and C if not.
 *
 * @return list<long>
 */
PointIdVector CtiCCSubstationBus::getCurrentVarLoadPoints() const
{
    PointIdVector points;
    int pointId = 0;

    if (getUsePhaseData())
    {
        pointId = getCurrentVarLoadPointId();
        points.push_back(pointId);

        pointId = getPhaseBId();
        points.push_back(pointId);

        pointId = getPhaseCId();
        points.push_back(pointId);
    }
    else
    {
        pointId = getCurrentVarLoadPointId();
        points.push_back(pointId);
    }

    return points;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getCurrentVarLoadPointValue() const
{
    if ( _dualBusEnable && _switchOverStatus &&
         getStrategy()->getUnitType() == ControlStrategy::Volts )
    {
        return _altSubVarVal;
    }
    if ( getPrimaryBusFlag() &&
         ( getStrategy()->getUnitType() == ControlStrategy::KVar ||
           getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ) )
    {
        return _altSubVarVal;
    }

    return _currentvarloadpointvalue;
}

double CtiCCSubstationBus::getRawCurrentVarLoadPointValue() const
{
    return _currentvarloadpointvalue;
}

double CtiCCSubstationBus::getTotalizedVarLoadPointValue() const
{
    if (getUsePhaseData())
    {
        return _phaseAvalue + _phaseBvalue + _phaseCvalue;
    }
    return getRawCurrentVarLoadPointValue();
}
/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentWattLoadPointId() const
{
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getCurrentWattLoadPointValue() const
{
    if ((_dualBusEnable && _switchOverStatus) || getPrimaryBusFlag())
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::KVar ||
             getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar )
        {
            return _altSubWattVal;
        }
    }

    return _currentwattloadpointvalue;
}

double CtiCCSubstationBus::getRawCurrentWattLoadPointValue() const
{
    return _currentwattloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointId

    Returns the current volt load point id of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentVoltLoadPointId() const
{
    return _currentvoltloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointValue

    Returns the current volt load point value of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getCurrentVoltLoadPointValue() const
{
    if (_dualBusEnable && _switchOverStatus)
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
        {
            return _altSubVoltVal;
        }
    }
    return _currentvoltloadpointvalue;
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
double CtiCCSubstationBus::getTargetVarValue() const
{
    return _targetvarvalue;
}


/*---------------------------------------------------------------------------
    getControlSendRetries

    Returns the ControlSendRetries of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getControlSendRetries() const
{
    if (_SEND_TRIES > 1 && _SEND_TRIES > getStrategy()->getControlSendRetries())
    {
        return _SEND_TRIES-1;
    }

    return getStrategy()->getControlSendRetries();
}


long CtiCCSubstationBus::getLastFeederControlledSendRetries() const
{
    long sendRetries = 0;

    try
    {
        if (getLastFeederControlledPosition() >= 0)
        {
            CtiCCFeederPtr feed = (CtiCCFeederPtr)_ccfeeders[getLastFeederControlledPosition()];

            if (feed->getPaoId() != getLastFeederControlledPAOId())
            {
                for(long i=0;i<_ccfeeders.size();i++)
                {
                    feed = (CtiCCFeeder*)_ccfeeders[i];
                    if (feed->getPaoId() == getLastFeederControlledPAOId())
                    {
                        //setLastFeederControlledPosition(i);
                        break;
                    }
                    feed = NULL;
                }

            }
            if (feed != NULL && feed->getStrategy()->getUnitType() != ControlStrategy::None)
            {
                sendRetries = feed->getStrategy()->getControlSendRetries();
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return sendRetries;
}

/*---------------------------------------------------------------------------
    getDecimalPlaces

    Returns the decimal places of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getDecimalPlaces() const
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
bool CtiCCSubstationBus::getNewPointDataReceivedFlag() const
{
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getBusUpdatedFlag

    Returns the substation updated flag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getBusUpdatedFlag() const
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
long CtiCCSubstationBus::getEstimatedVarLoadPointId() const
{
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getEstimatedVarLoadPointValue() const
{
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getDailyOperationsAnalogPointId() const
{
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getPowerFactorPointId

    Returns the power factor point id of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getPowerFactorPointId() const
{
    return _powerfactorpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorPointId

    Returns the estimated power factor point id of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getEstimatedPowerFactorPointId() const
{
    return _estimatedpowerfactorpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the flag that represents if the substation is in peak time
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getPeakTimeFlag() const
{
    return _peaktimeflag;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the substation has been recently controlled
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getRecentlyControlledFlag() const
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
double CtiCCSubstationBus::getVarValueBeforeControl() const
{
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPAOId

    Returns the PAO id of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getLastFeederControlledPAOId() const
{
    return _lastfeedercontrolledpaoid;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPosition

    Returns the position of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getLastFeederControlledPosition() const
{
    if (_lastfeedercontrolledposition >= 0 && _lastfeedercontrolledposition < _ccfeeders.size())
        return _lastfeedercontrolledposition;
    else
        return 0;

}

/*---------------------------------------------------------------------------
    getPowerFactorValue

    Returns the PowerFactorValue of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getPowerFactorValue() const
{
    return _powerfactorvalue;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorValue

    Returns the EstimatedPowerFactorValue of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getEstimatedPowerFactorValue() const
{
    return _estimatedpowerfactorvalue;
}

/*---------------------------------------------------------------------------
    getKVARSolution

    Returns the KVARSolution of the substation
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::getKVARSolution() const
{
    return _kvarsolution;
}

/*---------------------------------------------------------------------------
    getCurrentVarPointQuality

    Returns the CurrentVarPointQuality of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentVarPointQuality() const
{
    return _currentvarpointquality;
}
/*---------------------------------------------------------------------------
    getCurrentWattPointQuality

    Returns the CurrentWattPointQuality of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentWattPointQuality() const
{
    return _currentwattpointquality;
}
/*---------------------------------------------------------------------------
    getCurrentVoltPointQuality

    Returns the CurrentVoltPointQuality of the substation
---------------------------------------------------------------------------*/
long CtiCCSubstationBus::getCurrentVoltPointQuality() const
{
    return _currentvoltpointquality;
}

/*---------------------------------------------------------------------------
    getWaiveControlFlag

    Returns the WaiveControlFlag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getWaiveControlFlag() const
{
    return _waivecontrolflag;
}

/*---------------------------------------------------------------------------
    getVerificationFlag

    Returns the WaiveControlFlag of the substation
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::getVerificationFlag() const
{
    return _verificationFlag;
}

bool CtiCCSubstationBus::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

bool CtiCCSubstationBus::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

bool CtiCCSubstationBus::getOverlappingVerificationFlag() const
{
    return _overlappingSchedulesVerificationFlag;
}

bool CtiCCSubstationBus::getPreOperationMonitorPointScanFlag() const
{
    return _preOperationMonitorPointScanFlag;
}
bool CtiCCSubstationBus::getOperationSentWaitFlag() const
{
    return _postOperationMonitorPointScanFlag;
}
bool CtiCCSubstationBus::getPostOperationMonitorPointScanFlag() const
{
    return _postOperationMonitorPointScanFlag;
}

bool CtiCCSubstationBus::getWaitForReCloseDelayFlag() const
{
    return _waitForReCloseDelayFlag;
}

bool CtiCCSubstationBus::getWaitToFinishRegularControlFlag() const
{
    return _waitToFinishRegularControlFlag;
}
bool CtiCCSubstationBus::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}
bool CtiCCSubstationBus::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

bool CtiCCSubstationBus::getCorrectionNeededNoBankAvailFlag() const
{
    return _correctionNeededNoBankAvailFlag;
}
bool CtiCCSubstationBus::getLikeDayControlFlag() const
{
    return _likeDayControlFlag;
}
bool CtiCCSubstationBus::getVoltReductionFlag() const
{
    return _voltReductionFlag;
}
bool CtiCCSubstationBus::getSendMoreTimeControlledCommandsFlag() const
{
    return _sendMoreTimeControlledCommandsFlag;
}


long CtiCCSubstationBus::getVoltReductionControlId() const
{
    return _voltReductionControlId;
}
long CtiCCSubstationBus::getDisableBusPointId() const
{
    return _disableBusPointId;
}


long CtiCCSubstationBus::getCurrentVerificationFeederId() const
{
    return _currentVerificationFeederId;
}
long CtiCCSubstationBus::getCurrentVerificationCapBankId() const
{
    return _currentVerificationCapBankId;
}
long CtiCCSubstationBus::getCurrentVerificationCapBankOrigState() const
{
    return _currentCapBankToVerifyAssumedOrigState;
}
long CtiCCSubstationBus::getAltDualSubId() const
{
    return _altDualSubId;
}
double CtiCCSubstationBus::getAltSubControlValue() const
{
    return _altSubControlValue;
}

void CtiCCSubstationBus::getAllAltSubValues(double &volt, double &var, double &watt)
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
}
long CtiCCSubstationBus::getSwitchOverPointId() const
{
    return _switchOverPointId;
}
bool CtiCCSubstationBus::getSwitchOverStatus() const
{
    return _switchOverStatus;
}
bool CtiCCSubstationBus::getPrimaryBusFlag() const
{
    return _primaryBusFlag;
}
bool CtiCCSubstationBus::getDualBusEnable() const
{
    return _dualBusEnable;
}

long CtiCCSubstationBus::getEventSequence() const
{
    return _eventSeq;
}
bool CtiCCSubstationBus::getReEnableBusFlag() const
{
    return _reEnableBusFlag;
}

bool CtiCCSubstationBus::getMultiMonitorFlag() const
{
    return _multiMonitorFlag;
}

/*---------------------------------------------------------------------------

 Added for serialization

---------------------------------------------------------------------------*/

double CtiCCSubstationBus::getAltSubVoltVal() const
{
    return _altSubVoltVal;
}

double CtiCCSubstationBus::getAltSubVarVal() const
{
    return _altSubVarVal;
}

double CtiCCSubstationBus::getAltSubWattVal() const
{
    return _altSubWattVal;
}

double CtiCCSubstationBus::getCurrentvoltloadpointvalue() const
{
    return _currentvoltloadpointvalue;
}

double CtiCCSubstationBus::getCurrentvarloadpointvalue() const
{
    return _currentvarloadpointvalue;
}

double CtiCCSubstationBus::getCurrentwattloadpointvalue() const
{
    return _currentwattloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCCFeeders

    Returns the list of feeders in the substation
---------------------------------------------------------------------------*/
CtiFeeder_vec& CtiCCSubstationBus::getCCFeeders()
{
    return _ccfeeders;
}

const CtiFeeder_vec& CtiCCSubstationBus::getCCFeeders() const
{
    return _ccfeeders;
}


/*---------------------------------------------------------------------------
    getCCFeeders

    Returns the list of feeders in the substation
---------------------------------------------------------------------------*/
std::list<int> CtiCCSubstationBus::getCCFeederIds()
{
    std::list<int> ids;
    for each (const CtiCCFeederPtr f in _ccfeeders)
    {
        ids.push_back(f->getPaoId());
    }
    return ids;
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
    setParentId

    Sets the parentID (AreaID) of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setParentId(long parentId)
{
    _parentId = parentId;
}


/*---------------------------------------------------------------------------
    setParentControlUnits

    Sets the ParentControlUnits in the substation bus
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setParentControlUnits(const string& parentControlUnits)
{
    _dirty |= setVariableIfDifferent(_parentControlUnits, parentControlUnits);
}

/*---------------------------------------------------------------------------
    setParentName

    Sets the ParentName in the substation bus
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setParentName(const string& parentName)
{
    _dirty |= setVariableIfDifferent(_parentName, parentName);
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the DisplayOrder of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setDisplayOrder(long displayOrder)
{
    _displayOrder = displayOrder;
}
/*---------------------------------------------------------------------------
    setIVControlTot

    Sets the Integrated Volt or var Control Total of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setIVControlTot(double value)
{
    _dirty |= setVariableIfDifferent(_iVControlTot, value);
}
/*---------------------------------------------------------------------------
    setIVCoont

    Sets the Integrated Volt or var Control Count of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setIVCount(long value)
{
    _dirty |= setVariableIfDifferent(_iVCount, value);
}
/*---------------------------------------------------------------------------
    setIWControlTot

    Sets the Integrated Watt Control Total of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setIWControlTot(double value)
{
    _dirty |= setVariableIfDifferent(_iWControlTot, value);
}
/*---------------------------------------------------------------------------
    setIWCoont

    Sets the Integrated Watt Control Count of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setIWCount(long value)
{
    _iWCount = value;
}

/*---------------------------------------------------------------------------
    setIVControl

    Sets the Integrated Volt/Var Control of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setIVControl(double value)
{
    _iVControl = value;
}

/*---------------------------------------------------------------------------
    setIWControl

    Sets the Integrated Watt Control of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setIWControl(double value)
{
    _iWControl = value;
}
/*---------------------------------------------------------------------------
    setUsePhaseData flag

    Sets the UsePhaseData flag  of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setUsePhaseData(bool flag)
{
    _usePhaseData = flag;
}
/*---------------------------------------------------------------------------
    setPhaseBid

    Sets the PhaseB pointid  of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseBId(long pointid)
{
    _phaseBid = pointid;
}

/*---------------------------------------------------------------------------
    setPhaseCid

    Sets the PhaseC pointid  of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseCId(long pointid)
{
    _phaseCid = pointid;
}
/*---------------------------------------------------------------------------
    setTotalizedControlFlag

    Sets the TotalizedControlFlag  of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setTotalizedControlFlag(bool flag)
{
    _totalizedControlFlag = flag;
}


/*---------------------------------------------------------------------------
    setPhaseAValue

    Sets the setPhaseAValue Var of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseAValue(double value, CtiTime timestamp)
{
    _dirty |= setVariableIfDifferent(_phaseAvalue, value);

    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionA.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CTILOG_DEBUG(dout, "RATE OF CHANGE: Adding to regressionA  " << timestamp.seconds() << "  and " << value);
        }
    }
}

/*---------------------------------------------------------------------------
    setPhaseBValue

    Sets the setPhaseBValue Var of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseBValue(double value, CtiTime timestamp)
{
    _dirty |= setVariableIfDifferent(_phaseBvalue, value);

    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionB.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CTILOG_DEBUG(dout, "RATE OF CHANGE: Adding to regressionB  " << timestamp.seconds() << "  and " << value);
        }
    }
}



/*---------------------------------------------------------------------------
    setPhaseCValue

    Sets the setPhaseCValue Var of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseCValue(double value, CtiTime timestamp)
{
    _dirty |= setVariableIfDifferent(_phaseCvalue, value);

    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionC.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CTILOG_DEBUG(dout, "RATE OF CHANGE: Adding to regressionC  " << timestamp.seconds() << "  and " << value);
        }
    }
}

/*---------------------------------------------------------------------------
    setPhaseAValue

    Sets the setPhaseAValue Var of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseAValueBeforeControl(double value)
{
    _phaseAvalueBeforeControl = value;
}

/*---------------------------------------------------------------------------
    setPhaseBValue

    Sets the setPhaseBValue Var of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseBValueBeforeControl(double value)
{
    _phaseBvalueBeforeControl = value;
}



/*---------------------------------------------------------------------------
    setPhaseCValue

    Sets the setPhaseCValue Var of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPhaseCValueBeforeControl(double value)
{
    _phaseCvalueBeforeControl = value;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentVarLoadPointId(long currentvarid)
{
    _currentvarloadpointid = currentvarid;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentVarLoadPointValue(double value, CtiTime timestamp)
{
    _dirty |= setVariableIfDifferent(_currentvarloadpointvalue, value);

    if(_RATE_OF_CHANGE && !getRecentlyControlledFlag())
    {
        regression.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CTILOG_DEBUG(dout, "RATE OF CHANGE: Adding to regression  " << timestamp.seconds() << "  and " << value);
        }
    }
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentWattLoadPointId(long currentwattid)
{
    _currentwattloadpointid = currentwattid;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentWattLoadPointValue(double currentwattval)
{
    _dirty |= setVariableIfDifferent(_currentwattloadpointvalue, currentwattval);
}
/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointId

    Sets the current volt load point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentVoltLoadPointId(long currentvoltid)
{
    _currentvoltloadpointid = currentvoltid;
}

/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointValue

    Sets the current volt load point value of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentVoltLoadPointValue(double currentvoltval)
{
    _dirty |= setVariableIfDifferent(_currentvoltloadpointvalue, currentvoltval);
}


/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setMapLocationId(const string& maplocation)
{
    _maplocationid = maplocation;
}


/*---------------------------------------------------------------------------
    setSolution

    Sets the Solution of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setSolution(const string& text)
{
    _solution = text;
}
/*---------------------------------------------------------------------------
    setTargetVarValue

    Sets the target var value of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setTargetVarValue(double value)
{
    _targetvarvalue = value;
}

/*---------------------------------------------------------------------------
    setDecimalPlaces

    Sets the decimal places of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setDecimalPlaces(long places)
{
    _decimalplaces = places;
}

long CtiCCSubstationBus::getNextTODStartTime()
{
    long    retVal  = CtiTime().seconds() - CtiTime(0, 0, 0).seconds();
    CtiDate today   = CtiDate();

    // Need to convert our generic ControlStrategy smart pointer to a plain dumb TimeOfDayStrategy pointer...

    TimeOfDayStrategy * pStrategy = dynamic_cast<TimeOfDayStrategy *>( getStrategy().get() );

    if (pStrategy)
    {
        TimeOfDayStrategy::TimeOfDay timeOfDay = pStrategy->getNextTimeOfDay( retVal );

        retVal = timeOfDay.timeOffset;

        _percentToClose = (today.weekDay() > 0 && today.weekDay() < 6)
                            ? timeOfDay.weekdayPercent
                            : timeOfDay.weekendPercent;
    }

    if (_CC_DEBUG & CC_DEBUG_TIMEOFDAY )
    {
        CTILOG_DEBUG(dout,  "NextTODStartTime:  " <<CtiTime(CtiTime(0, 0, 0).seconds() + retVal) << " for SubBus: "<<getPaoName()
        << " scheduling " << _percentToClose <<"% of banks to Close.");
    }
    return retVal;
}
/*---------------------------------------------------------------------------
    figureNextCheckTime

    Figures out when the substation should be checked again according to the
    control interval.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::figureNextCheckTime()
{
    CtiTime currenttime = CtiTime();
    if (getLikeDayControlFlag())
    {
        _nextchecktime = CtiTime(currenttime.seconds() + 60);
    }
    else if ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
    {
        if (getSendMoreTimeControlledCommandsFlag())
        {
            long tempsum = (currenttime.seconds()-(currenttime.seconds()%getStrategy()->getControlInterval()))+getStrategy()->getControlInterval();
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
            CTILOG_DEBUG(dout, "TOD: NextCheckTime:  " <<CtiTime(_nextchecktime)<<" for SubBus: "<<getPaoName());
        }
    }
    else if( getStrategy()->getControlInterval() != 0 )
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::MultiVolt )
        {
            if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
            {
                for (long i = 0; i < _ccfeeders.size(); i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if (currentFeeder->getPostOperationMonitorPointScanFlag())
                    {
                        long tempsum = currenttime.seconds() + (_SCAN_WAIT_EXPIRE * 60);
                        _nextchecktime = CtiTime(CtiTime(tempsum));
                        break;
                    }
                    else
                    {
                        long tempsum = (currenttime.seconds()-(currenttime.seconds()%getStrategy()->getControlInterval()))+getStrategy()->getControlInterval();
                        _nextchecktime = CtiTime(CtiTime(tempsum));
                    }

                }
            }
            else
            {
                if (getPostOperationMonitorPointScanFlag() || getPreOperationMonitorPointScanFlag())
                {
                    long tempsum = currenttime.seconds() + (_SCAN_WAIT_EXPIRE * 60);
                    _nextchecktime = CtiTime(CtiTime(tempsum));
                }
                else
                {
                    long tempsum = (currenttime.seconds()-(currenttime.seconds()%getStrategy()->getControlInterval()))+getStrategy()->getControlInterval();
                    _nextchecktime = CtiTime(CtiTime(tempsum));
                }
            }

        }
        else
        {
            long tempsum = (currenttime.seconds()-(currenttime.seconds()%getStrategy()->getControlInterval()))+getStrategy()->getControlInterval();
            _nextchecktime = CtiTime(CtiTime(tempsum));
        }
        _dirty = true;
    }
    else
    {
        _nextchecktime = currenttime;
        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
        {
            for (long i = 0; i < _ccfeeders.size(); i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None)
                {
                    long tempsum1 = (currenttime.seconds() - ( currenttime.seconds()%currentFeeder->getStrategy()->getControlInterval() ) + currentFeeder->getStrategy()->getControlInterval() );
                    _nextchecktime = CtiTime(CtiTime(tempsum1));
                    break;
                }
            }

        }
    }
}

double CtiCCSubstationBus::getSetPoint()
{
    double lagLevel = (getPeakTimeFlag()?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
    double leadLevel = (getPeakTimeFlag()?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
    double setPoint = ((lagLevel + leadLevel)/2);

    if ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
         getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
    {
        setPoint = (getPeakTimeFlag()?getStrategy()->getPeakPFSetPoint():getStrategy()->getOffPeakPFSetPoint());
    }

    return setPoint;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setNewPointDataReceivedFlag(bool newpointdatareceived)
{
    _dirty |= setVariableIfDifferent(_newpointdatareceivedflag, newpointdatareceived);
}

/*---------------------------------------------------------------------------
    setBusUpdatedFlag

    Sets the substation updated flag of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setBusUpdatedFlag(bool busupdated)
{
    _busupdatedflag = busupdated;
}

/*---------------------------------------------------------------------------
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate)
{
    _dirty |= setVariableIfDifferent(_lastcurrentvarpointupdatetime, lastpointupdate);
}
/*---------------------------------------------------------------------------
    setLastWattPointTime

    Sets the last current Watt point update time of the subbus
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setLastWattPointTime(const CtiTime& lastpointupdate)
{
    _dirty |= setVariableIfDifferent(_lastWattPointTime, lastpointupdate);
}
/*---------------------------------------------------------------------------
    setLastVoltPointTime

    Sets the last current Volt point update time of the subbus
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setLastVoltPointTime(const CtiTime& lastpointupdate)
{
    _dirty |= setVariableIfDifferent(_lastVoltPointTime, lastpointupdate);
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setEstimatedVarLoadPointId(long estimatedvarid)
{
    _estimatedvarloadpointid = estimatedvarid;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setEstimatedVarLoadPointValue(double estimatedvarval)
{
    const bool varChanged = setVariableIfDifferent(_estimatedvarloadpointvalue, estimatedvarval);
    _busupdatedflag |= varChanged;
    _dirty |= varChanged;
}

/*---------------------------------------------------------------------------
    setDailyOperationsAnalogPointId

    Sets the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setDailyOperationsAnalogPointId(long opanalogpointid)
{
    _dailyoperationsanalogpointid = opanalogpointid;
}

/*---------------------------------------------------------------------------
    setPowerFactorPointId

    Sets the power factor point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPowerFactorPointId(long pfpointid)
{
    _powerfactorpointid = pfpointid;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorPointId

    Sets the estimated power factor point id of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setEstimatedPowerFactorPointId(long epfpointid)
{
    _estimatedpowerfactorpointid = epfpointid;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentDailyOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges)
{
    if( _currentdailyoperations != operations )
    {
        if( getDailyOperationsAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(getDailyOperationsAnalogPointId(),operations,NormalQuality,AnalogPointType));
        }
        _dirty = true;
    }
    _currentdailyoperations = operations;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentDailyOperations(long operations)
{
    _dirty |= setVariableIfDifferent(_currentdailyoperations, operations);
}

/*---------------------------------------------------------------------------
    setPeakTimeFlag

    Sets the flag if the substation is in peak time
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPeakTimeFlag(bool peaktime)
{
    _dirty |= setVariableIfDifferent(_peaktimeflag, peaktime);
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setRecentlyControlledFlag(bool recentlycontrolled)
{
    _dirty |= setVariableIfDifferent(_recentlycontrolledflag, recentlycontrolled);
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::checkAndUpdateRecentlyControlledFlag()
{
    int numberOfCapBanksPending = 0;
    bool pendingBanks = false;

    for(long i=0;i<_ccfeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
        CtiCCCapBank* currentCapBank = NULL;

        for(int j=0;j<ccCapBanks.size();j++)
        {
           currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
           if( (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
               currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ) &&
               !currentCapBank->getPerformingVerificationFlag() )
           {
               pendingBanks = true;
               currentFeeder->setRecentlyControlledFlag(true);
               setRecentlyControlledFlag(true);
               numberOfCapBanksPending += 1;
               j = ccCapBanks.size();
           }
        }
        if (!pendingBanks)
        {
            currentFeeder->setRecentlyControlledFlag(false);
            currentFeeder->setRetryIndex(0);
            pendingBanks = false;
        }
    }
    if (numberOfCapBanksPending == 0)
    {
        setRecentlyControlledFlag(false);
    }
}


/*---------------------------------------------------------------------------
    setLastOperationTime

    Sets the last operation time of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setLastOperationTime(const CtiTime& lastoperation)
{
    _dirty |= setVariableIfDifferent(_lastoperationtime, lastoperation);
}

void CtiCCSubstationBus::setLastVerificationCheck(const CtiTime& checkTime)
{
    _lastVerificationCheck = checkTime;
}

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setVarValueBeforeControl(double oldvarval, long originalParentId)
{
    _dirty |= setVariableIfDifferent(_varvaluebeforecontrol, oldvarval);

    setPhaseAValueBeforeControl(getPhaseAValue());
    setPhaseBValueBeforeControl(getPhaseBValue());
    setPhaseCValueBeforeControl(getPhaseCValue());
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPAOId

    Sets the pao id of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setLastFeederControlledPAOId(long lastfeederpao)
{
    _dirty |= setVariableIfDifferent(_lastfeedercontrolledpaoid, lastfeederpao);
}

void CtiCCSubstationBus::setLastFeederControlled(long lastfeederpao)
{
    setLastFeederControlledPAOId(lastfeederpao);
    setLastFeederControlledPosition(0);

    CtiFeeder_vec& ccFeeders = getCCFeeders();
    for(long j=0;j<ccFeeders.size();j++)
    {
        CtiCCFeederPtr tempFeeder = (CtiCCFeeder*)ccFeeders.at(j);
        if( tempFeeder->getPaoId() == lastfeederpao )
        {
            setLastFeederControlledPosition(j);
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPosition

    Sets the position of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setLastFeederControlledPosition(long lastfeederposition)
{
    _dirty |= setVariableIfDifferent(_lastfeedercontrolledposition, lastfeederposition);
}

/*---------------------------------------------------------------------------
    setPowerFactorValue

    Sets the PowerFactorValue in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setPowerFactorValue(double pfval)
{
    _dirty |= setVariableIfDifferent(_powerfactorvalue, pfval);
}


void CtiCCSubstationBus::figureAndSetPowerFactorByFeederValues( )
{
    if (_currentvarloadpointid == 0 || _currentwattloadpointid == 0)
    {
        //sum the var/watt values from the feeders and set as the pf for the substation.
        long varTotal = 0;
        long wattTotal = 0;
        long estVarTotal = 0;
        for (long i = 0; i < _ccfeeders.size(); i++)
        {
            varTotal += ((CtiCCFeeder*)_ccfeeders[i])->getCurrentVarLoadPointValue();
            wattTotal += ((CtiCCFeeder*)_ccfeeders[i])->getCurrentWattLoadPointValue();
            estVarTotal += ((CtiCCFeeder*)_ccfeeders[i])->getEstimatedVarLoadPointValue();
        }
        setPowerFactorValue(Cti::CapControl::calculatePowerFactor(varTotal, wattTotal));
        setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(estVarTotal, wattTotal));
        setBusUpdatedFlag(true);
    }
}
/*---------------------------------------------------------------------------
    setKVARSolution

    Sets the KVARSolution in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setKVARSolution(double solution)
{
    _dirty |= setVariableIfDifferent(_kvarsolution, solution);
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorValue

    Sets the EstimatedPowerFactorValue in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setEstimatedPowerFactorValue(double epfval)
{
    _dirty |= setVariableIfDifferent(_estimatedpowerfactorvalue, epfval);
}

/*---------------------------------------------------------------------------
    setCurrentVarPointQuality

    Sets the CurrentVarPointQuality in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentVarPointQuality(long cvpq)
{
    _dirty |= setVariableIfDifferent(_currentvarpointquality, cvpq);
}


/*---------------------------------------------------------------------------
    setCurrentWattPointQuality

    Sets the CurrentWattPointQuality in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentWattPointQuality(long cwpq)
{
    _dirty |= setVariableIfDifferent(_currentwattpointquality, cwpq);
}


/*---------------------------------------------------------------------------
    setCurrentVoltPointQuality

    Sets the CurrentVoltPointQuality in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setCurrentVoltPointQuality(long cvpq)
{
    _dirty |= setVariableIfDifferent(_currentvoltpointquality, cvpq);
}

/*---------------------------------------------------------------------------
    setWaiveControlFlag

    Sets the WaiveControlFlag in the substation
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::setWaiveControlFlag(bool waive)
{
    _dirty |= setVariableIfDifferent(_waivecontrolflag, waive);
}

void CtiCCSubstationBus::setAltDualSubId(long altDualSubId)
{
    _dirty |= setVariableIfDifferent(_altDualSubId, altDualSubId);
}
void CtiCCSubstationBus::setAltSubControlValue(double controlValue)
{
    _dirty |= setVariableIfDifferent(_altSubControlValue, controlValue);
}
void CtiCCSubstationBus::setAllAltSubValues(double volt, double var, double watt)
{
    _altSubVoltVal = volt;
    _altSubVarVal = var;
    _altSubWattVal = watt;
}


void CtiCCSubstationBus::setSwitchOverPointId(long pointId)
{
    _dirty |= setVariableIfDifferent(_switchOverPointId, pointId);
}
void CtiCCSubstationBus::setSwitchOverStatus(bool status)
{
    _dirty |= setVariableIfDifferent(_switchOverStatus, status);
}
void CtiCCSubstationBus::setPrimaryBusFlag(bool status)
{
    _dirty |= setVariableIfDifferent(_primaryBusFlag, status);
}
void CtiCCSubstationBus::setDualBusEnable(bool flag)
{
    _dirty |= setVariableIfDifferent(_dualBusEnable, flag);
}

void CtiCCSubstationBus::setEventSequence(long eventSeq)
{
    _dirty |= setVariableIfDifferent(_eventSeq, eventSeq);
}

void CtiCCSubstationBus::setReEnableBusFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_reEnableBusFlag, flag);
}

void CtiCCSubstationBus::setMultiMonitorFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_multiMonitorFlag, flag);
}

void CtiCCSubstationBus::reOrderFeederDisplayOrders()
{
    CtiFeeder_vec& feeders = getCCFeeders();
    CtiFeeder_SVector orderedFdrs;
    for (int i = 0; i < feeders.size(); i ++)
    {
        orderedFdrs.push_back((CtiCCFeeder*)feeders.at(i));
    }
    for (int i = 0; i < orderedFdrs.size(); i ++)
    {
        ((CtiCCFeeder*)orderedFdrs.at(i))->setDisplayOrder(i + 1);
    }
}
/*---------------------------------------------------------------------------
    figureEstimatedVarLoadPointValue

    Figures out the estimated var point value according to the states
    of the individual cap banks in each of the feeders
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::figureEstimatedVarLoadPointValue()
{
    if( getCurrentVarLoadPointId() > 0 )
    {
        double tempValue;
        if( getRecentlyControlledFlag() )
            tempValue = getVarValueBeforeControl();
        else
            tempValue = getCurrentVarLoadPointValue();

        for(long i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(long j=0;j<ccCapBanks.size();j++)
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
}

void CtiCCSubstationBus::checkForMaxDailyOpsHit()
{
    //have we went past the max daily ops
    if( getStrategy()->getMaxDailyOperation() > 0 &&
        (_currentdailyoperations == getStrategy()->getMaxDailyOperation()  ||
         (!getMaxDailyOpsHitFlag() && _currentdailyoperations > getStrategy()->getMaxDailyOperation()) ) )//only send once
    {
        string text = string("Substation Bus Exceeded Max Daily Operations");
        string additional = string("Substation Bus: ");
        additional += getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPaoDescription();
            additional += ")";
        }
        if (getDailyOperationsAnalogPointId() > 0 && !getMaxDailyOpsHitFlag())
        {
            CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                                TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
            pSig->setCondition(CtiTablePointAlarming::highReasonability);
            CtiCapController::getInstance()->sendMessageToDispatch(pSig, CALLSITE);
        }
        setSolution(text);
        setMaxDailyOpsHitFlag(true);

    }
}

bool CtiCCSubstationBus::maxOperationsHitDisableBus()
{
   CtiCCSubstationBusStore::getInstance()->UpdatePaoDisableFlagInDB(this, true);
   setBusUpdatedFlag(true);
   setSolution("  Sub Disabled. Automatic Control Inhibited.");
   string text = string("Substation Bus Disabled");
   string additional = string("Bus: ");
   additional += getPaoName();
   if (_LOG_MAPID_INFO)
   {
       additional += " MapID: ";
       additional += getMapLocationId();
       additional += " (";
       additional += getPaoDescription();
       additional += ")";
   }
   if (getDailyOperationsAnalogPointId() > 0)
   {
       CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                           TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
       pSig->setCondition(CtiTablePointAlarming::highReasonability);
       CtiCapController::getInstance()->sendMessageToDispatch(pSig, CALLSITE);
   }

   return false;
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool keepGoing = true;

    if (getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar)
    {
        keepGoing = false;
    }
    else
    {
        checkForMaxDailyOpsHit();
        if( getStrategy()->getMaxOperationDisableFlag() && getMaxDailyOpsHitFlag() )
        {
            if (getStrategy()->getEndDaySettings().compare("Trip") == 0)
            {
                bool flag = CtiCCSubstationBusStore::getInstance()->isAnyBankClosed(getPaoId(),Cti::CapControl::SubBus);
                if (flag == false)
                {
                    keepGoing = maxOperationsHitDisableBus();
                }
            }
            else if (getStrategy()->getEndDaySettings().compare("Close") == 0)
            {
                bool flag = CtiCCSubstationBusStore::getInstance()->isAnyBankOpen(getPaoId(),Cti::CapControl::SubBus);
                if (flag == false)
                {
                    keepGoing = maxOperationsHitDisableBus();
                }
            }
            else
            {
                keepGoing = maxOperationsHitDisableBus();
            }
        }
    }

    if( keepGoing )
    {
        for(long i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
            bool peakFlag = isPeakTime(currentDateTime);

            if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder &&
                 !(ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)")) &&
                 (currentFeeder->getStrategy()->getPeakStartTime() > 0 && currentFeeder->getStrategy()->getPeakStopTime() > 0))
            {
                currentFeeder->isPeakTime(currentDateTime);
            }
            else
            {
                currentFeeder->setPeakTimeFlag(isPeakTime(currentDateTime));
            }
        }
        if( currentDateTime.seconds() >= getLastOperationTime().seconds() + getStrategy()->getControlDelayTime() )
        {
            if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
            {
                for(long i=0;i<_ccfeeders.size();i++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                    if( !currentFeeder->getDisableFlag() && !currentFeeder->getRecentlyControlledFlag() &&
                        (getStrategy()->getControlInterval()!=0 ||
                         currentFeeder->getNewPointDataReceivedFlag() ||
                         currentFeeder->getStrategy()->getControlInterval() != 0) )
                    {
                        if( currentFeeder->checkForAndProvideNeededIndividualControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentFeeder->getPeakTimeFlag(), getDecimalPlaces(), getStrategy()->getControlUnits(), getMaxDailyOpsHitFlag()) )
                        {
                            setLastOperationTime(currentDateTime);
                            setRecentlyControlledFlag(true);
                            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        }
                        setBusUpdatedFlag(true);
                    }
                }
            }
            else if ( getStrategy()->getMethodType() != ControlStrategy::ManualOnly ) 
            {
                double lagLevel = (isPeakTime(currentDateTime)?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
                double leadLevel = (getPeakTimeFlag()?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
                double setPoint = (lagLevel + leadLevel)/2;

                setIWControl(getCurrentWattLoadPointValue());

                if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                {
                    setIVControl(getCurrentVoltLoadPointValue());
                }
                else
                {
                    setIVControl(getCurrentVarLoadPointValue());
                }

                if (getStrategy()->getIntegrateFlag() && getStrategy()->getIntegratePeriod() > 0)
                {
                    if (getIVCount() > 0)
                        setIVControl(getIVControlTot() / getIVCount());
                    if (getIWCount() > 0)
                        setIWControl(getIWControlTot() / getIWCount());

                    if ( _CC_DEBUG & CC_DEBUG_INTEGRATED )
                    {
                        CTILOG_DEBUG(dout, getPaoName() <<" USING INTEGRATED CONTROL - iVControl=iVControlTot/iVCount ( "<<
                                                getIVControl()<<" = "<< getIVControlTot() <<" / "<<getIVCount()<<" )"<< endl
                                        << getPaoName() <<" USING INTEGRATED CONTROL - iWControl=iWControlTot/iWCount ( "<<
                                                getIWControl()<<" = "<< getIWControlTot() <<" / "<<getIWCount()<<" )");
                    }
                    //resetting integration total...
                    if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                    {
                        setIVControlTot(getCurrentVoltLoadPointValue());
                    }
                    else
                    {
                        setIVControlTot(getCurrentVarLoadPointValue());
                    }
                    setIVCount(1);
                    setIWControlTot(getCurrentWattLoadPointValue());
                    setIWCount(1);
                }


                if ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                     getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                {
                    setPoint = (getPeakTimeFlag()?getStrategy()->getPeakPFSetPoint():getStrategy()->getOffPeakPFSetPoint());
                }

                setKVARSolution(calculateKVARSolution(getStrategy()->getControlUnits(),setPoint, getIVControl(), getIWControl()));
                setTargetVarValue( getKVARSolution() + getIVControl());


                bool arePointsNormalQuality = ( getCurrentVarPointQuality() == NormalQuality &&
                                     ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ? getCurrentWattPointQuality() == NormalQuality :
                                     ( getStrategy()->getUnitType() == ControlStrategy::Volts ? getCurrentVoltPointQuality() == NormalQuality : getCurrentVarPointQuality()  == NormalQuality) ) );

                if( !_IGNORE_NOT_NORMAL_FLAG || arePointsNormalQuality )
                {
                    if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                    {

                        if( (!_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality) &&
                             getIVControl() > lagLevel ||
                             getIVControl() < leadLevel )
                        {
                            if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
                            {
                                regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                CTILOG_INFO(dout, "Invalid control method: " << getStrategy()->getControlMethod() << ", in sub bus: " << getPaoName());
                            }
                        }
                        else
                        {
                            if (getCurrentVarPointQuality() != NormalQuality &&
                                ( getIVControl() > lagLevel ||
                                  getIVControl() < leadLevel) )
                            CTILOG_INFO(dout, "Control Inhibited - Not Normal Var Quality, in sub bus: " << getPaoName());
                            setSolution("Not Normal Quality.  Var Control Inhibited.");
                        }
                    }
                    else if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                    {
                        if( (!_IGNORE_NOT_NORMAL_FLAG || getCurrentVoltPointQuality() == NormalQuality) &&
                            (getCurrentVoltLoadPointValue() < lagLevel ||
                            getCurrentVoltLoadPointValue() > leadLevel) )
                        {
                            if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
                            {
                                if ((!_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality) &&
                                    getCurrentVarLoadPointId() > 0)
                                    regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                                else
                                {
                                    CTILOG_WARN(dout, "Substation Volt Control requires Var PointID.  Var PointID not found in sub bus: " << getPaoName());
                                }
                            }
                            else if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                CTILOG_INFO(dout, "Invalid control method: " << getStrategy()->getControlMethod() << ", in sub bus: " << getPaoName());
                            }
                        }
                        else
                        {
                            if (getCurrentVoltPointQuality() != NormalQuality &&
                                (getCurrentVoltLoadPointValue() < lagLevel ||
                                 getCurrentVoltLoadPointValue() > leadLevel))
                            CTILOG_INFO(dout, "Control Inhibited - Not Normal Volt Quality, in sub bus: " << getPaoName());
                            setSolution("Not Normal Quality.  Volt Control Inhibited.");
                        }
                    }
                    else if ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                              getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                    {
                        if (!_IGNORE_NOT_NORMAL_FLAG ||
                            (getCurrentVarPointQuality() == NormalQuality && getCurrentWattPointQuality() == NormalQuality) )
                        {
                            if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
                            {
                                regularSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                optimizedSubstationBusControl(lagLevel, leadLevel, currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                CTILOG_INFO(dout, "Invalid control method: " << getStrategy()->getControlMethod() << ", in sub bus: " << getPaoName());
                            }
                        }
                        else
                        {
                            {
                                Cti::StreamBuffer s;

                                s << "Control Inhibited on SubBus: "<<getPaoName()<< " by Abnormal Point Quality";

                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    Cti::FormattedTable table;

                                    table.setHorizontalBorders(Cti::FormattedTable::Borders_Outside_Bottom, 0);
                                    table.setVerticalBorders(Cti::FormattedTable::Borders_Inside);

                                    table.setCell(0, 0) << "Type";
                                    table.setCell(0, 1) << "ID";
                                    table.setCell(0, 2) << "Quality";

                                    table.setCell(1, 0) << "Var";
                                    table.setCell(1, 1) << getCurrentVarLoadPointId();
                                    table.setCell(1, 2) << getCurrentVarPointQuality();

                                    table.setCell(2, 0) << "Watt";
                                    table.setCell(2, 1) << getCurrentWattLoadPointId();
                                    table.setCell(2, 2) << getCurrentWattPointQuality();

                                    table.setCell(3, 0) << "Volt";
                                    table.setCell(3, 1) << getCurrentVoltLoadPointId();
                                    table.setCell(3, 2) << getCurrentVoltPointQuality();

                                    s << table;
                                }

                                CTILOG_INFO(dout, s);
                            }
                            setSolution("Not Normal Quality.  PF Control Inhibited.");
                        }
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Invalid control units: " << getStrategy()->getControlUnits() << ", in sub bus: " << getPaoName());
                    }
                }
                else
                {
                   {
                       Cti::StreamBuffer s;

                       s << "Control Inhibited on SubBus: "<<getPaoName()<< " by Abnormal Point Quality";

                       if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                       {
                           Cti::FormattedTable table;

                           table.setHorizontalBorders(Cti::FormattedTable::Borders_Outside_Bottom, 0);
                           table.setVerticalBorders(Cti::FormattedTable::Borders_Inside);

                           table.setCell(0, 0) << "Type";
                           table.setCell(0, 1) << "ID";
                           table.setCell(0, 2) << "Quality";

                           table.setCell(1, 0) << "Var";
                           table.setCell(1, 1) << getCurrentVarLoadPointId();
                           table.setCell(1, 2) << getCurrentVarPointQuality();

                           table.setCell(2, 0) << "Watt";
                           table.setCell(2, 1) << getCurrentWattLoadPointId();
                           table.setCell(2, 2) << getCurrentWattPointQuality();

                           table.setCell(3, 0) << "Volt";
                           table.setCell(3, 1) << getCurrentVoltLoadPointId();
                           table.setCell(3, 2) << getCurrentVoltPointQuality();

                           s << table;
                       }

                       CTILOG_INFO(dout, s);
                   }
                   setSolution("Not Normal Quality.  Control Inhibited.");
                }

                clearOutNewPointReceivedFlags();
            }
        }
        else if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Cannot control an additional bank because not past control delay time, in sub bus: " << getPaoName());
        }
    }
}

/*---------------------------------------------------------------------------
    calculateKVARSolution


---------------------------------------------------------------------------*/
double CtiCCSubstationBus::calculateKVARSolution(const string& controlUnits, double setPoint, double varValue, double wattValue)
{
    double returnKVARSolution = 0.0;
    if( ciStringEqual(controlUnits,ControlStrategy::KVarControlUnit) )
    {
        returnKVARSolution = setPoint - varValue;
    }
    else if( ciStringEqual(controlUnits,ControlStrategy::PFactorKWKVarControlUnit) ||
             ciStringEqual(controlUnits,ControlStrategy::PFactorKWKQControlUnit))
    {
        double targetKVAR = 0.0;
        if (setPoint != 0)
        {
            double targetKVA = wattValue / (setPoint/100.0);
            double NaNDefenseDouble = (targetKVA*targetKVA)-(wattValue*wattValue);
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
    else if( ciStringEqual(controlUnits,ControlStrategy::VoltsControlUnit) ||
             ciStringEqual(controlUnits,ControlStrategy::MultiVoltControlUnit)||
             ciStringEqual(controlUnits,ControlStrategy::MultiVoltVarControlUnit)||
             ciStringEqual(controlUnits,ControlStrategy::TimeOfDayControlUnit)||
             ciStringEqual(controlUnits,ControlStrategy::IntegratedVoltVarControlUnit) )
    {
        returnKVARSolution = 0;
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid control units: " << controlUnits);
    }

    return returnKVARSolution;
}

void CtiCCSubstationBus::figureAndSetTargetVarValue()
{
    setKVARSolution(calculateKVARSolution(getStrategy()->getControlUnits(),getSetPoint(), getCurrentVarLoadPointValue(), getCurrentWattLoadPointValue()));

    if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
    {
        setTargetVarValue( getKVARSolution() + getCurrentVoltLoadPointValue());
    }
    else
    {
        setTargetVarValue( getKVARSolution() + getCurrentVarLoadPointValue());
    }
}

/*---------------------------------------------------------------------------
    regularSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::regularSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;

    try
    {
        CtiCCFeeder* currentFeeder = NULL;
        long currentPosition = getLastFeederControlledPosition();
        long iterations = 0;
        if ( ( getStrategy()->getUnitType() == ControlStrategy::KVar && getCurrentVarLoadPointId() > 0 ) ||
             ( getStrategy()->getUnitType() == ControlStrategy::Volts && getCurrentVoltLoadPointId() > 0 ) )
        {
            if( !(getMaxDailyOpsHitFlag() && getStrategy()->getMaxOperationDisableFlag()) &&//end day on a trip.
                ( getStrategy()->getUnitType() == ControlStrategy::KVar &&
                lagLevel <  getIVControl() ) ||
                ( getStrategy()->getUnitType() == ControlStrategy::Volts &&
                lagLevel >  getIVControl() ) )
            {
                //if( _CC_DEBUG )
                if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                {
                    CTILOG_INFO(dout, "Attempting to Decrease Var level in substation bus: " << getPaoName());
                }
                else
                {
                    setKVARSolution(-1);
                    CTILOG_INFO(dout, "Attempting to Increase Volt level in substation bus: " << getPaoName());
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
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
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
                            Cti::StreamBuffer s;

                            s << "Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay.";

                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                Cti::FormattedList list;

                                list.add("Last Status Change Time") << capBank->getLastStatusChangeTime();
                                list.add("Reclose Delay")           << capBank->getRecloseDelay();
                                list.add("Current Date Time")       << currentDateTime;

                                s << list;
                            }
                            CTILOG_INFO(dout, s);
                            setSolution("Control Inhibited by Reclose Delay on Cap: "+capBank->getPaoName());
                        }
                        else
                        {
                            double controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                                        ? getCurrentVoltLoadPointValue()
                                                        : getIVControl();

                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue,  getCurrentVarLoadPointValue()) ;

                            request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  getCurrentVarLoadPointValue(),
                                                                              getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                        }
                    }
                }

                if( request == NULL )
                {
                    if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                    {
                        createCannotControlBankText("Decrease Var", "Close", ccEvents);
                    }
                    else
                    {
                        createCannotControlBankText("Increase Volt", "Close", ccEvents);
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);
                }
            }
            else if (( getStrategy()->getUnitType() == ControlStrategy::KVar &&
                      getIVControl() < leadLevel ) ||
                    ( getStrategy()->getUnitType() == ControlStrategy::Volts &&
                      getIVControl() > leadLevel )) // lead Level is greater than currentVarPointValue
            {
                if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                {
                    CTILOG_INFO(dout, "Attempting to Increase Var level in substation bus: " << getPaoName().c_str());
                }
                else
                {
                    setKVARSolution(1);
                    CTILOG_INFO(dout, "Attempting to Decrease Volt level in substation bus: " << getPaoName().c_str());
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
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                    }
                    iterations++;
                }
                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        double controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                                   ? getCurrentVoltLoadPointValue()
                                                   : getIVControl();

                        string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue()) ;
                        request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                    }
                }

                if( request == NULL )
                {
                    if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                    {
                        createCannotControlBankText("Increase Var", "Open", ccEvents);
                    }
                    else
                    {
                        createCannotControlBankText("Decrease Volt", "Open", ccEvents);
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);
                }
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                {
                    CTILOG_DEBUG(dout, "Max Daily Ops Hit. Control Inhibited on: " << getPaoName());
                }

            }
        }
        else if ( ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                    getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ ) &&
                    getCurrentVarLoadPointId() > 0 && getCurrentWattLoadPointId() > 0 )
        {
            if( getKVARSolution() < 0 && !(getMaxDailyOpsHitFlag() && getStrategy()->getMaxOperationDisableFlag()) ) //end day on a trip.
            {
                CTILOG_INFO(dout, "Attempting to Decrease Var level in substation bus: " << getPaoName().c_str());

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
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                    }
                    iterations++;
                }

                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        double adjustedBankKVARReduction = (lagLevel/100.0)*((double)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            if( capBank != NULL &&
                                capBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                            {
                                Cti::StreamBuffer s;

                                s << "Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay.";

                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    Cti::FormattedList list;

                                    list.add("Last Status Change Time") << capBank->getLastStatusChangeTime();
                                    list.add("Reclose Delay")           << capBank->getRecloseDelay();
                                    list.add("Current Date Time")       << currentDateTime;

                                    s << list;
                                }

                                CTILOG_INFO(dout, s);
                            }
                            else
                            {
                                string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close,  getIVControl(), getCurrentVarLoadPointValue());

                                request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  getCurrentVarLoadPointValue(),
                                                                                  getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                            }
                        }
                        else
                        {//cap bank too big
                            CTILOG_INFO(dout, "Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch");
                        }
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    createCannotControlBankText("Decrease Var", "Close", ccEvents);
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);
                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CTILOG_DEBUG(dout, "Attempting to Increase Var level in substation bus: " << getPaoName().c_str());
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
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                    }
                    iterations++;
                }

                if( capBank != NULL )
                {
                    currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                    if (!currentFeeder->getDisableFlag())
                    {
                        double adjustedBankKVARIncrease = -(leadLevel/100.0)*((double)capBank->getBankSize());
                        if( adjustedBankKVARIncrease <= getKVARSolution() )
                        {

                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open,  getIVControl(), getCurrentVarLoadPointValue());
                            request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text,
                                                                              getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                        }
                        else
                        {//cap bank too big
                            CTILOG_INFO(dout, "Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch");
                        }
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    createCannotControlBankText("Increase Var", "Open", ccEvents);
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);

                }
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                {
                    CTILOG_DEBUG(dout, "Max Daily Ops Hit. Control Inhibited on: " << getPaoName());
                }

            }
        }

        if( request != NULL )
        {
            pilMessages.push_back(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlled(currentFeeder->getPaoId());
            ((CtiCCFeeder*)_ccfeeders.at(currentPosition))->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl( getCurrentVarLoadPointValue(), currentFeeder->getOriginalParent().getOriginalParentId() );
            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(true);
        }
        //setNewPointDataReceivedFlag(false);
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(true);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
    optimizedSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::optimizedSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;
    CtiCCFeeder* lastFeederControlled = NULL;
    int positionLastFeederControlled = -1;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    try
    {
        typedef std::vector<CtiCCFeederPtr> SortedFeeders;

        SortedFeeders   varSortedFeeders;

        for each ( CtiCCFeederPtr feeder in _ccfeeders )
        {
            feeder->fillOutBusOptimizedInfo(getPeakTimeFlag());
            varSortedFeeders.push_back(feeder);
        }

        std::sort( varSortedFeeders.begin(), varSortedFeeders.end(), FeederVARComparison() );

        if ( ( getStrategy()->getUnitType() == ControlStrategy::KVar && getCurrentVarLoadPointId() > 0 ) ||
             ( getStrategy()->getUnitType() == ControlStrategy::Volts && getCurrentVoltLoadPointId() > 0 ) )
        {
            if ( !(getMaxDailyOpsHitFlag()  && getStrategy()->getMaxOperationDisableFlag()) &&//end day on a trip.
                 ( getStrategy()->getUnitType() == ControlStrategy::KVar &&
                 lagLevel <  getIVControl() ) ||
                 ( getStrategy()->getUnitType() == ControlStrategy::Volts &&
                 lagLevel >  getIVControl() ) )
            {
                //if( _CC_DEBUG )
                if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                {
                    CTILOG_INFO(dout, "Attempting to Decrease Var level in substation bus: " << getPaoName().c_str());
                }
                else
                {
                    CTILOG_INFO(dout, "Attempting to Increase Volt level in substation bus: " << getPaoName().c_str());
                }

                CtiCCCapBankPtr capBank = 0;
                SortedFeeders::size_type index = 0;
                for ( SortedFeeders::iterator itr = varSortedFeeders.begin(),
                                              end = varSortedFeeders.end();
                      itr != end; ++itr, ++index )
                {
                    CtiCCFeederPtr currentFeeder = *itr;

                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                            {
                                setKVARSolution(-1);
                            }
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found in feeder: " << currentFeeder->getPaoName());
                        }
                    }
                    if( capBank )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            if( capBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                            {
                                Cti::StreamBuffer s;

                                s << "Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay.";

                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    Cti::FormattedList list;

                                    list.add("Last Status Change Time") << capBank->getLastStatusChangeTime();
                                    list.add("Reclose Delay")           << capBank->getRecloseDelay();
                                    list.add("Current Date Time")       << currentDateTime;

                                    s << list;
                                }

                                CTILOG_INFO(dout, s);
                            }
                            else
                            {
                                double controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                                            ? getCurrentVoltLoadPointValue()
                                                            : getIVControl();

                                string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, currentFeeder->getCurrentVarLoadPointValue());
                                request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                  currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());
                                lastFeederControlled = currentFeeder;
                                positionLastFeederControlled = index;
                            }
                        }
                        break;
                    }
                }
                if( request == NULL )
                {
                    if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                    {
                        createCannotControlBankText("Decrease Var", "Close", ccEvents);
                    }
                    else
                    {
                        createCannotControlBankText("Increase Volt", "Close", ccEvents);
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);
                }
            }
            else //leadLevel greater than currentVarPointValue
            {
                //if( _CC_DEBUG )
                if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                {
                    CTILOG_INFO(dout, "Attempting to Increase Var level in substation bus: " << getPaoName().c_str());
                }
                else
                {
                    CTILOG_INFO(dout, "Attempting to Decrease Volt level in substation bus: " << getPaoName().c_str());
                }

                CtiCCCapBankPtr capBank = 0;
                SortedFeeders::size_type index = varSortedFeeders.size() - 1;
                for ( SortedFeeders::reverse_iterator itr = varSortedFeeders.rbegin(),
                                                      end = varSortedFeeders.rend();
                      itr != end; ++itr, --index )
                {
                    CtiCCFeederPtr currentFeeder = *itr;

                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                            {
                                setKVARSolution(1);
                            }
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found in feeder: " << currentFeeder->getPaoName());
                        }
                    }
                    if( capBank )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            double controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                                        ? getCurrentVoltLoadPointValue()
                                                        : getIVControl();

                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, currentFeeder->getCurrentVarLoadPointValue());
                            request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                              currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());
                            lastFeederControlled = currentFeeder;
                            positionLastFeederControlled = index;
                        }
                        break;
                    }
                }

                if( request == NULL )
                {
                    if ( getStrategy()->getUnitType() == ControlStrategy::KVar )
                    {
                        createCannotControlBankText("Increase Var", "Open", ccEvents);
                    }
                    else
                    {
                        createCannotControlBankText("Decrease Volt", "Open", ccEvents);
                    }
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);
                }
            }
        }
        else if ( ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                    getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ ) &&
                    getCurrentVarLoadPointId() > 0 && getCurrentWattLoadPointId() > 0 )
        {
            if( getKVARSolution() < 0 && !(getMaxDailyOpsHitFlag()  && getStrategy()->getMaxOperationDisableFlag() ))  //end day on a trip.
            {
                //if( _CC_DEBUG )
                {
                    CTILOG_DEBUG(dout, "Attempting to Decrease Var level in substation bus: " << getPaoName().c_str());
                }

                CtiCCCapBankPtr capBank = 0;
                SortedFeeders::size_type index = 0;
                for ( SortedFeeders::iterator itr = varSortedFeeders.begin(),
                                              end = varSortedFeeders.end();
                      itr != end; ++itr, ++index )
                {
                    CtiCCFeederPtr currentFeeder = *itr;

                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found or NOT NORMAL quality in feeder: " << currentFeeder->getPaoName());
                        }
                    }
                    if( capBank )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            lastFeederControlled = currentFeeder;
                            positionLastFeederControlled = index;
                            double adjustedBankKVARReduction = (lagLevel/100.0)*((double)capBank->getBankSize());
                            if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                            {
                                if( capBank->getRecloseDelay() > 0 &&
                                    currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                                {
                                    Cti::StreamBuffer s;

                                    s << "Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay.";

                                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                    {
                                        Cti::FormattedList list;

                                        list.add("Last Status Change Time") << capBank->getLastStatusChangeTime();
                                        list.add("Reclose Delay")           << capBank->getRecloseDelay();
                                        list.add("Current Date Time")       << currentDateTime;

                                        s << list;
                                    }

                                    CTILOG_INFO(dout, s);
                                }
                                else
                                {
                                    string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, getIVControl(), currentFeeder->getCurrentVarLoadPointValue());
                                    request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                      currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());
                                }
                            }
                            else
                            {//cap bank too big
                                CTILOG_INFO(dout, "Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch");
                            }
                        }
                        break;
                    }
                }

                if( ! capBank && request == NULL )
                {
                    createCannotControlBankText("Decrease Var", "Close", ccEvents);
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);

                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CTILOG_DEBUG(dout, "Attempting to Increase Var level in substation bus: " << getPaoName().c_str());
                }

                CtiCCCapBankPtr capBank = 0;
                SortedFeeders::size_type index = varSortedFeeders.size() - 1;
                for ( SortedFeeders::reverse_iterator itr = varSortedFeeders.rbegin(),
                                                      end = varSortedFeeders.rend();
                      itr != end; ++itr, --index )
                {
                    CtiCCFeederPtr currentFeeder = *itr;

                    if( !currentFeeder->getDisableFlag() &&
                        !currentFeeder->getWaiveControlFlag() &&
                        currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                    {
                        if ((!_IGNORE_NOT_NORMAL_FLAG || currentFeeder->getCurrentVarPointQuality() == NormalQuality) &&
                            currentFeeder->getCurrentVarLoadPointId() > 0)
                        {
                            capBank = currentFeeder->findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), getStrategy()->getUnitType() == ControlStrategy::KVar );
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Bus Optimized Volt Control requires Feeder Var PointID.  Var PointID not found or NOT NORMAL quality in feeder: " << currentFeeder->getPaoName());
                        }
                    }
                    if( capBank )
                    {
                        currentFeeder->checkMaxDailyOpCountExceeded(pointChanges);

                        if (!currentFeeder->getDisableFlag())
                        {
                            lastFeederControlled = currentFeeder;
                            positionLastFeederControlled = index;
                            double adjustedBankKVARIncrease = -(leadLevel/100.0)*((double)capBank->getBankSize());
                            if( adjustedBankKVARIncrease <= getKVARSolution() )
                            {
                                string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open,  getIVControl(), currentFeeder->getCurrentVarLoadPointValue());

                                request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                  currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());
                            }
                            else
                            {//cap bank too big
                                CTILOG_INFO(dout, "Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch");
                            }
                        }
                        break;
                    }
                }

                if( ! capBank && request == NULL  )
                {
                    createCannotControlBankText("Increase Var", "Open", ccEvents);
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);

                }
            }
        }
        else
        {
            CTILOG_INFO(dout, "Invalid control units: " << getStrategy()->getControlUnits() << " in sub bus: " << getPaoName());
        }

        if( request != NULL )
        {
            pilMessages.push_back(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlled(lastFeederControlled->getPaoId());
            lastFeederControlled->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl( getCurrentVarLoadPointValue(), lastFeederControlled->getOriginalParent().getOriginalParentId()  );
            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(true);
        }
        //setNewPointDataReceivedFlag(false);
        //for(int j=0;j<_ccfeeders.size();j++)
        //{
            //((CtiCCFeeder*)_ccfeeders[j])->setNewPointDataReceivedFlag(false);
        //}
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(true);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}



/*---------------------------------------------------------------------------
    figureCurrentSetPoint

    Returns the current set point depending on if it is peak or off peak
    time and sets the set point status
---------------------------------------------------------------------------*/
double CtiCCSubstationBus::figureCurrentSetPoint(const CtiTime& currentDateTime)
{
    double lagLevel = (isPeakTime(currentDateTime)?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
    double leadLevel = (getPeakTimeFlag()?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());

    return (lagLevel + leadLevel)/2;
}

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isPeakTime(const CtiTime& currentDateTime)
{
    setPeakTimeFlag( getStrategy()->isPeakTime( currentDateTime ) );

    return getPeakTimeFlag();
}

/*---------------------------------------------------------------------------
    isControlPoint

    Returns a boolean if it is the control point for the sub.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isControlPoint(long pointid)
{
    if ( getStrategy()->getMethodType() != ControlStrategy::IndividualFeeder )
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::Volts &&
             getCurrentVoltLoadPointId() == pointid )
        {
            return true;
        }
        else if ( getStrategy()->getUnitType() == ControlStrategy::KVar &&
                  getCurrentVarLoadPointId() == pointid )
        {
            return true;
        }
        else if ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar &&
                  ( getCurrentVarLoadPointId() == pointid || getCurrentWattLoadPointId() == pointid ) )
        {
            return true;
        }
    }
    return false;
}

void CtiCCSubstationBus::updateIntegrationVPoint(const CtiTime &currentDateTime)
{
    double controlVvalue = 0;
    if (getDisableFlag())
    {
        return;
    }
    if ( getStrategy()->getMethodType() != ControlStrategy::IndividualFeeder )
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
        {
            controlVvalue = getCurrentVoltLoadPointValue();
        }
        else if ( getStrategy()->getUnitType() == ControlStrategy::KVar ||
                  getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar )
        {
            controlVvalue = getCurrentVarLoadPointValue();
        }
        else
        {
            //integration not implemented.
            controlVvalue = 0;
        }

        if (getStrategy()->getControlInterval() > 0)
        {
            if (getNextCheckTime() - getStrategy()->getIntegratePeriod() <= currentDateTime)
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
    if( _CC_DEBUG & CC_DEBUG_INTEGRATED )
    {
        CTILOG_DEBUG(dout, getPaoName() <<": iVControlTot = " <<getIVControlTot() <<" iVCount = "<<getIVCount());
    }
}

void CtiCCSubstationBus::updateIntegrationWPoint(const CtiTime &currentDateTime)
{
    double controlWvalue = 0;
    if (getDisableFlag())
    {
        return;
    }

    if ( getStrategy()->getMethodType() != ControlStrategy::IndividualFeeder )
    {
        controlWvalue = getCurrentWattLoadPointValue();
        if (getStrategy()->getControlInterval() > 0)
        {
            if (getNextCheckTime() - getStrategy()->getIntegratePeriod() <= currentDateTime)
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
    if( _CC_DEBUG & CC_DEBUG_INTEGRATED )
    {
        CTILOG_DEBUG(dout, getPaoName() <<": iWControlTot = " <<getIWControlTot() <<" iWCount = "<<getIWCount());
    }

}

bool CtiCCSubstationBus::capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents)
{
     if( getPerformingVerificationFlag())
     {
         if ( getWaitForReCloseDelayFlag() ||
              (!capBankVerificationStatusUpdate(pointChanges, ccEvents)  &&
                getCurrentVerificationCapBankId() != -1)  )
         {
             return false;
         }
         return true;
     }
    bool returnBoolean = false;
    bool found = false;
    char tempchar[64] = "";
    string text = "";
    string additional = "";

    long minConfirmPercent = getStrategy()->getMinConfirmPercent();
    long maxConfirmTime = getStrategy()->getMaxConfirmTime();
    long sendRetries = getControlSendRetries();
    long failPercent = getStrategy()->getFailurePercent();

    try
    {
        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
             getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
        {
            long recentlyControlledFeeders = 0;
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getStrategy()->getMaxConfirmTime();
                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                    failPercent = currentFeeder->getStrategy()->getFailurePercent();
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
                            returnBoolean = currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                               failPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                                               currentFeeder->getPhaseBValueBeforeControl(),currentFeeder->getPhaseCValueBeforeControl(),
                                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                                               currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC() );

                        }
                        else
                        {
                            returnBoolean = currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, minConfirmPercent,failPercent,currentFeeder->getVarValueBeforeControl(),
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
                setWaitToFinishRegularControlFlag(false);
                setRecentlyControlledFlag(false);
            }
            figureEstimatedVarLoadPointValue();
        }
        else if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                  getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
        {
            CtiCCFeeder* currentFeeder = CtiCCSubstationBusStore::getInstance()->findFeederByPAObjectID(getLastFeederControlledPAOId());

            if( currentFeeder != NULL)
            {
                currentFeeder->setEventSequence(getEventSequence());

                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getStrategy()->getMaxConfirmTime();
                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                    failPercent = currentFeeder->getStrategy()->getFailurePercent();
                }
                if (getUsePhaseData() && !(getTotalizedControlFlag() || getPrimaryBusFlag()))
                {
                    returnBoolean = currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                       failPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                       getPhaseBValueBeforeControl(),getPhaseCValueBeforeControl(),
                                                       getPhaseAValue(), getPhaseBValue(), getPhaseCValue(), regressionA, regressionB, regressionC);

                }
                else
                {
                   if ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
                        currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag() )
                   {
                       returnBoolean = currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
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
                       returnBoolean = currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,getVarValueBeforeControl(),
                                                              getCurrentVarLoadPointValue(), getCurrentVarPointQuality(), getPhaseAValue(),
                                                              getPhaseBValue(), getPhaseCValue(), regression);
                   }
                }
                setWaitToFinishRegularControlFlag(false);
                setRecentlyControlledFlag(false);
                figureEstimatedVarLoadPointValue();
                found = true;
            }
            else
            {
                CTILOG_ERROR(dout, "Last Feeder controlled NOT FOUND");
                returnBoolean = false;
            }
        }
        else if ( getStrategy()->getMethodType() == ControlStrategy::ManualOnly )
        {
            long recentlyControlledFeeders = 0;
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                        maxConfirmTime = currentFeeder->getStrategy()->getMaxConfirmTime();
                        sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                        failPercent = currentFeeder->getStrategy()->getFailurePercent();
                    }
                    currentFeeder->setEventSequence(getEventSequence());
                    if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                    {
                        if( currentFeeder->isAlreadyControlled(minConfirmPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                           getPhaseBValueBeforeControl(), getPhaseCValueBeforeControl(),
                                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue(),
                                                           getVarValueBeforeControl(), getCurrentVarLoadPointValue(),
                                                           regression, regressionA, regressionB, regressionC,
                                                           getUsePhaseData(), (getTotalizedControlFlag() || getPrimaryBusFlag())  )
                            || currentFeeder->isPastMaxConfirmTime(CtiTime(),maxConfirmTime,sendRetries)
                          )
                        {
                            if (currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag())
                            {
                                returnBoolean = currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                                   failPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                                                   currentFeeder->getPhaseBValueBeforeControl(),currentFeeder->getPhaseCValueBeforeControl(),
                                                                   currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                                                   currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC() );

                            }
                            else
                            {
                                returnBoolean = currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,
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
                        if (getUsePhaseData() && !(getTotalizedControlFlag() || getPrimaryBusFlag()))
                        {
                            returnBoolean = currentFeeder->capBankControlPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent,
                                                               failPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                                               getPhaseBValueBeforeControl(),getPhaseCValueBeforeControl(),
                                                               getPhaseAValue(), getPhaseBValue(), getPhaseCValue(), regressionA, regressionB, regressionC);

                        }
                        else
                        {
                            returnBoolean = currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents,minConfirmPercent,failPercent,getVarValueBeforeControl(),
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
                        //setRecentlyControlledFlag(false);
                        figureEstimatedVarLoadPointValue();
                        CTILOG_INFO(dout, "Manual Only Sub Bus: " << getPaoName() << " does not have Var points to perform confirmation logic.");
                    }
                }
            }
            if( recentlyControlledFeeders == 0 )
            {
                setWaitToFinishRegularControlFlag(false);
                setRecentlyControlledFlag(false);
            }
            figureEstimatedVarLoadPointValue();
        }
        else
        {
            CTILOG_INFO(dout, "Invalid control method");
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    //setNewPointDataReceivedFlag(false);

    return returnBoolean;
}


bool CtiCCSubstationBus::capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents)
{
    bool returnBoolean = false;
    bool foundCap = false;
    bool foundFeeder = false;
    bool assumedWrongFlag = false;
    string text = "";
    string additional = "";
    double ratio = 0;
    double change = 0;
    double oldValue = 0;
    double newValue = 0;
    CtiTime timeNow;

    long minConfirmPercent = getStrategy()->getMinConfirmPercent();
    long maxConfirmTime = getStrategy()->getMaxConfirmTime();
    long sendRetries = getControlSendRetries();
    long failPercent = getStrategy()->getFailurePercent();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    bool vResult = false; //fail


    if ( ( getStrategy()->getMethodType() != ControlStrategy::IndividualFeeder &&
           getStrategy()->getMethodType() != ControlStrategy::BusOptimizedFeeder ) &&
           getUsePhaseData() && !(getTotalizedControlFlag() || getPrimaryBusFlag())  )
    {
        returnBoolean = capBankVerificationPerPhaseStatusUpdate(pointChanges, ccEvents);
    }
    else
    {

        for(long i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            if (currentFeeder->getPaoId() == getCurrentVerificationFeederId())
            {
                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                    maxConfirmTime = currentFeeder->getStrategy()->getMaxConfirmTime();
                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                    failPercent = currentFeeder->getStrategy()->getFailurePercent();
                }

                if( ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                      getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                      getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod ) &&
                      currentFeeder->getUsePhaseData() && !currentFeeder->getTotalizedControlFlag() )
                {
                    returnBoolean = currentFeeder->capBankVerificationPerPhaseStatusUpdate(pointChanges, ccEvents,
                                                                                           minConfirmPercent, failPercent);
                    foundCap = true;
                }
                else
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    CtiCCCapBank* currentCapBank = NULL;

                    for(int j=0;j<ccCapBanks.size();j++)
                    {
                       currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                       if (currentCapBank->getPaoId() == currentFeeder->getCurrentVerificationCapBankId())
                       {

                           if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                           {
                               if( !_IGNORE_NOT_NORMAL_FLAG ||
                                   getCurrentVarPointQuality() == NormalQuality )
                               {
                                   if( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                       getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                                       ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
                                       currentFeeder->getCurrentVarLoadPointId() > 0 ) )
                                   {
                                       newValue =  currentFeeder->getCurrentVarLoadPointValue();
                                       if( _RATE_OF_CHANGE && currentFeeder->getRegression().depthMet() )
                                       {
                                           oldValue =  currentFeeder->getRegression().regression(timeNow.seconds());
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
                                           oldValue =  getRegression().regression(timeNow.seconds());
                                       }
                                       else
                                       {
                                           oldValue =  getVarValueBeforeControl();
                                       }
                                   }
                                   change = newValue - oldValue;
                                   if( change < 0 )
                                   {
                                       CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                                       if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                                           currentCapBank->getVCtrlIndex() == 1)
                                       {
                                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                                           setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                                           currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);

                                           //return returnBoolean;
                                           change = 0 - change;
                                           assumedWrongFlag = true;
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
                                           additional += currentFeeder->getPaoName();

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
                                           additional += currentFeeder->getPaoName();

                                           currentCapBank->setControlStatusQuality(CC_Significant);
                                       }
                                       else
                                       {
                                           if (!assumedWrongFlag)
                                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                           else
                                               currentCapBank->setControlStatus(CtiCCCapBank::Close);

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPaoName();

                                           currentCapBank->setControlStatusQuality(CC_Normal);
                                           vResult = true;
                                       }
                                   }
                                   else
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPaoName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = true;
                                   }

                                   text = currentFeeder->createControlStatusUpdateText(currentCapBank->getControlStatusText(), newValue,ratio);

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));

                               }
                               else
                               {
                                   char tempchar[80];
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                   text = " Var: ";
                                   text += doubleToString(getCurrentVarLoadPointValue());
                                   text += "- Non Normal Var Quality = ";
                                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                                   text += tempchar;
                                   text += ", OpenQuestionable";
                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPaoName();

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
                                   if( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                       getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                                       ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
                                       currentFeeder->getCurrentVarLoadPointId() > 0 ))
                                   {
                                       newValue =  currentFeeder->getCurrentVarLoadPointValue();
                                       if( _RATE_OF_CHANGE && currentFeeder->getRegression().depthMet() )
                                       {
                                           oldValue =  currentFeeder->getRegression().regression(timeNow.seconds());
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
                                           oldValue =  regression.regression(timeNow.seconds());
                                       }
                                       else
                                       {
                                           oldValue =  getVarValueBeforeControl();
                                       }
                                   }
                                   change = oldValue - newValue;

                                   if( change < 0 )
                                   {
                                       CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                                       if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                                           currentCapBank->getVCtrlIndex() == 1)
                                       {
                                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                                           setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                                           //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                                           store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenPending, currentCapBank);
                                           //return returnBoolean;
                                           change = 0 - change;
                                           assumedWrongFlag = true;
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
                                           additional += currentFeeder->getPaoName();
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
                                           additional += currentFeeder->getPaoName();

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
                                               text += "% change), Close";
                                           else
                                               text += "% change), Open";

                                           additional = string("Feeder: ");
                                           additional += currentFeeder->getPaoName();

                                           currentCapBank->setControlStatusQuality(CC_Normal);
                                           vResult = true;
                                       }
                                   }
                                   else
                                   {
                                       if (!assumedWrongFlag)
                                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                       else
                                           currentCapBank->setControlStatus(CtiCCCapBank::Open);

                                       additional = string("Feeder: ");
                                       additional += currentFeeder->getPaoName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = true;
                                   }

                                   text = currentFeeder->createControlStatusUpdateText(currentCapBank->getControlStatusText(), newValue,ratio);

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));
                               }
                               else
                               {
                                   char tempchar[80];
                                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                   text = "Var: ";
                                   text += doubleToString(getCurrentVarLoadPointValue());
                                   text += "- Non Normal Var Quality = ";
                                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                                   text += tempchar;
                                   text += ", CloseQuestionable";
                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPaoName();

                                   currentCapBank->setBeforeVarsString(currentFeeder->createVarText(oldValue, 1.0));
                                   currentCapBank->setAfterVarsString(currentFeeder->createVarText(newValue, 1.0));
                                   currentCapBank->setPercentChangeString(currentFeeder->createVarText(ratio, 100.0));

                                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                               }
                           }
                           else
                           {
                               CTILOG_INFO(dout, "Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status");
                               if( currentCapBank->getPerformingVerificationFlag() )
                               {

                                   text += "Var: ";
                                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                                   text += " - control was not pending, " ;
                                   text += currentCapBank->getControlStatusText();
                                   currentCapBank->setControlStatusQuality(CC_Fail);
                               }
                           }
                           if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                               (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
                           {
                               currentCapBank->setRetryOpenFailedFlag(false);
                               currentCapBank->setRetryCloseFailedFlag(false);
                           }
                           setSolution(text);
                           if( currentCapBank->getStatusPointId() > 0 )
                           {
                               if (currentCapBank->getPorterRetFailFlag() && currentCapBank->getIgnoreFlag())
                               {
                                     currentCapBank->setVCtrlIndex(5);
                                     currentCapBank->setVerificationDoneFlag(true);
                                     currentCapBank->setIgnoreFlag(false);
                               }
                               else
                               {
                                   createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, currentFeeder, text, additional, true,
                                                              oldValue, newValue, change, getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                               }
                               currentCapBank->setPorterRetFailFlag(false);


                           }
                           else
                           {
                               CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                               << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                           }
                           currentCapBank->setControlRecentlySentFlag(false);
                           if (currentCapBank->updateVerificationState())
                           {
                               returnBoolean = true;
                               currentCapBank->setPerformingVerificationFlag(false);
                               currentFeeder->setPerformingVerificationFlag(false);
                               setBusUpdatedFlag(true);
                               return returnBoolean;
                           }
                           foundCap = true;
                           break;
                       }
                    }
                }
                foundFeeder = true;

                currentFeeder->setRetryIndex(0);
                break;
            }

        }
        if (foundCap == false)
        {
            CTILOG_ERROR(dout, "Last Verification Cap Bank controlled NOT FOUND");
            returnBoolean = true;
        }
        if (foundFeeder == false)
        {
            CTILOG_ERROR(dout, "Last Verification Feeder controlled NOT FOUND");
            returnBoolean = true;
        }


    }
    return returnBoolean;
}

void CtiCCSubstationBus::createStatusUpdateMessages(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiCCCapBankPtr capBank, CtiCCFeederPtr feeder,
                                                    string text, string additional, bool verifyFlag, double before, double after, double change,
                                                    double phaseA, double phaseB, double phaseC)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    string user = (verifyFlag ? "cap control verification" : "cap control");

    if( text.length() > 0 )
    {//if control failed or questionable, create event to be sent to dispatch
        long tempLong = capBank->getStatusPointId();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += feeder->getMapLocationId();
            additional += " (";
            additional += feeder->getPaoDescription();
            additional += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent, user));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
    }

    pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
    capBank->setLastStatusChangeTime(CtiTime());

    if( text.length() > 0 )
    {
        string stateInfo = capBank->getControlStatusQualityString();
        long stationId, areaId, spAreaId;
        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId,
                                            getPaoId(), feeder->getPaoId(), capBankStateUpdate,
                                            getEventSequence(), capBank->getControlStatus(), text,
                                            user, before, after, change,
                                            capBank->getIpAddress(), capBank->getActionId(), stateInfo,
                                            phaseA, phaseB, phaseC));
    }


}

bool CtiCCSubstationBus::capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents)
{
    bool returnBoolean = false;
    bool foundCap = false;
    bool foundFeeder = false;
    bool assumedWrongFlag = false;
    string text = "";
    string additional = "";
    double change = 0;
    double changeA = 0;
    double changeB = 0;
    double changeC = 0;
    double ratioA = 0;
    double ratioB = 0;
    double ratioC = 0;
    double varAValue = getPhaseAValue();
    double varBValue = getPhaseBValue();
    double varCValue = getPhaseCValue();

    double varValueAbc = getPhaseAValueBeforeControl();
    double varValueBbc = getPhaseBValueBeforeControl();
    double varValueCbc = getPhaseCValueBeforeControl();
    CtiTime timeNow;

    long minConfirmPercent = getStrategy()->getMinConfirmPercent();
    long maxConfirmTime = getStrategy()->getMaxConfirmTime();
    long sendRetries = getControlSendRetries();
    long failPercent = getStrategy()->getFailurePercent();

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool vResult = false; //fail

    for(long i=0;i<_ccfeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
        if (currentFeeder->getPaoId() == getCurrentVerificationFeederId())
        {
            foundFeeder = true;

            if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
            {
                minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                maxConfirmTime = currentFeeder->getStrategy()->getMaxConfirmTime();
                sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                failPercent = currentFeeder->getStrategy()->getFailurePercent();
            }

            if ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
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
                foundCap = true;

            }
            else
            {

                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                CtiCCCapBank* currentCapBank = NULL;

                for(int j=0;j<ccCapBanks.size();j++)
                {
                   currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                   if (currentCapBank->getPaoId() == getCurrentVerificationCapBankId())
                   {

                       if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                       {
                           if( !_IGNORE_NOT_NORMAL_FLAG ||
                               getCurrentVarPointQuality() == NormalQuality )
                           {
                               if( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                   getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                                   ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
                                     currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0 &&
                                     currentFeeder->getPhaseCId() > 0 ) )
                               {
                                   varAValue = currentFeeder->getPhaseAValue();
                                   varBValue = currentFeeder->getPhaseBValue();
                                   varCValue = currentFeeder->getPhaseCValue();

                                   if( _RATE_OF_CHANGE && currentFeeder->getRegressionA().depthMet() &&
                                       currentFeeder->getRegressionB().depthMet() &&
                                       currentFeeder->getRegressionC().depthMet() )
                                   {
                                       varValueAbc = currentFeeder->getRegressionA().regression( timeNow.seconds() );
                                       varValueBbc = currentFeeder->getRegressionB().regression( timeNow.seconds() );
                                       varValueCbc = currentFeeder->getRegressionC().regression( timeNow.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           Cti::FormattedList list;

                                           list << "Rate of Change";
                                           list.add("Phase A") << varValueAbc;
                                           list.add("Phase B") << varValueBbc;
                                           list.add("Phase C") << varValueCbc;

                                           CTILOG_DEBUG(dout, list);
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
                                       varValueAbc = getRegressionA().regression( timeNow.seconds() );
                                       varValueBbc = getRegressionB().regression( timeNow.seconds() );
                                       varValueCbc = getRegressionC().regression( timeNow.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           Cti::FormattedList list;

                                           list << "Rate of Change";
                                           list.add("Phase A") << varValueAbc;
                                           list.add("Phase B") << varValueBbc;
                                           list.add("Phase C") << varValueCbc;

                                           CTILOG_DEBUG(dout, list);
                                       }
                                   }
                               }
                               changeA = varAValue - varValueAbc;
                               changeB = varBValue - varValueBbc;
                               changeC = varCValue - varValueCbc;


                               if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                               {
                                   CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                                   if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                                       currentCapBank->getVCtrlIndex() == 1)
                                   {
                                       currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                                       setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                                       currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);

                                       changeA = 0 - changeA;
                                       changeB = 0 - changeB;
                                       changeC = 0 - changeC;

                                       assumedWrongFlag = true;
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
                                       additional += currentFeeder->getPaoName();

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
                                       additional += currentFeeder->getPaoName();

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
                                       additional += currentFeeder->getPaoName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = true;
                                   }
                               }
                               else
                               {
                                   if (!assumedWrongFlag)
                                       currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                   else
                                       currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPaoName();

                                   currentCapBank->setControlStatusQuality(CC_Normal);
                                   vResult = true;
                               }

                               currentCapBank->setPartialPhaseInfo(currentFeeder->getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failPercent));
                               text = currentFeeder->createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                              varBValue, varCValue, ratioA, ratioB, ratioC);

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));


                           }
                           else
                           {
                               char tempchar[80];
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                               text = "Var: ";
                               text += doubleToString(getCurrentVarLoadPointValue());
                               text += "- Non Normal Var Quality = ";
                               _ltoa(getCurrentVarPointQuality(),tempchar,10);
                               text += tempchar;
                               text += ", OpenQuestionable";
                               additional = string("Feeder: ");
                               additional += currentFeeder->getPaoName();

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
                               if( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                   getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                                  (getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
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
                                       varValueAbc = currentFeeder->getRegressionA().regression( timeNow.seconds() );
                                       varValueBbc = currentFeeder->getRegressionB().regression( timeNow.seconds() );
                                       varValueCbc = currentFeeder->getRegressionC().regression( timeNow.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           Cti::FormattedList list;

                                           list << "Rate of Change";
                                           list.add("Phase A") << varValueAbc;
                                           list.add("Phase B") << varValueBbc;
                                           list.add("Phase C") << varValueCbc;

                                           CTILOG_DEBUG(dout, list);
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
                                       varValueAbc = getRegressionA().regression( timeNow.seconds() );
                                       varValueBbc = getRegressionB().regression( timeNow.seconds() );
                                       varValueCbc = getRegressionC().regression( timeNow.seconds() );
                                       int size =  currentCapBank->getBankSize()/3;
                                       if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                                       {
                                           Cti::FormattedList list;

                                           list << "Rate of Change";
                                           list.add("Phase A") << varValueAbc;
                                           list.add("Phase B") << varValueBbc;
                                           list.add("Phase C") << varValueCbc;

                                           CTILOG_DEBUG(dout, list);
                                       }
                                   }
                               }
                               changeA = varValueAbc - varAValue;
                               changeB = varValueBbc - varBValue;
                               changeC = varValueCbc - varCValue;
                               if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                               {
                                   CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                                   if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                                       currentCapBank->getVCtrlIndex() == 1)
                                   {
                                       currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                                       setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                                       store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenPending, currentCapBank);

                                       changeA = 0 - changeA;
                                       changeB = 0 - changeB;
                                       changeC = 0 - changeC;

                                       assumedWrongFlag = true;
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
                                       additional += currentFeeder->getPaoName();

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
                                       additional += currentFeeder->getPaoName();

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
                                       additional += currentFeeder->getPaoName();

                                       currentCapBank->setControlStatusQuality(CC_Normal);
                                       vResult = true;
                                   }
                               }
                               else
                               {
                                   if (!assumedWrongFlag)
                                       currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                   else
                                       currentCapBank->setControlStatus(CtiCCCapBank::Open);

                                   additional = string("Feeder: ");
                                   additional += currentFeeder->getPaoName();

                                   currentCapBank->setControlStatusQuality(CC_Normal);
                                   vResult = true;
                               }
                               currentCapBank->setPartialPhaseInfo(currentFeeder->getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failPercent));
                               text = currentFeeder->createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                              varBValue, varCValue, ratioA, ratioB, ratioC);

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));


                           }
                           else
                           {
                               char tempchar[80];
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                               text = "Var: ";
                               text += doubleToString(getCurrentVarLoadPointValue());
                               text += "- Non Normal Var Quality = ";
                               _ltoa(getCurrentVarPointQuality(),tempchar,10);
                               text += tempchar;
                               text += ", CloseQuestionable";
                               additional = string("Feeder: ");
                               additional += currentFeeder->getPaoName();

                               currentCapBank->setBeforeVarsString(currentFeeder->createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                               currentCapBank->setAfterVarsString(currentFeeder->createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                               currentCapBank->setPercentChangeString(currentFeeder->createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

                               currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                           }
                       }
                       else
                       {
                           CTILOG_INFO(dout, "Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status");
                           if( currentCapBank->getPerformingVerificationFlag() )
                           {

                               text += "Var: ";
                               text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                               text += " - control was not pending, " ;
                               text += currentCapBank->getControlStatusText();
                               currentCapBank->setControlStatusQuality(CC_Fail);
                           }
                       }
                       if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                           (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
                       {
                           currentCapBank->setRetryOpenFailedFlag(false);
                           currentCapBank->setRetryCloseFailedFlag(false);
                       }
                       setSolution(text);
                       if( currentCapBank->getStatusPointId() > 0 )
                       {

                           if (currentCapBank->getPorterRetFailFlag() && currentCapBank->getIgnoreFlag())
                           {
                                 currentCapBank->setVCtrlIndex(5);
                                 currentCapBank->setVerificationDoneFlag(true);
                                 currentCapBank->setIgnoreFlag(false);
                           }
                           else
                           {
                               createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, currentFeeder, text, additional, true,
                                                          varValueAbc+varValueBbc+varValueCbc, varAValue+varBValue+varCValue, change,
                                                          getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                           }
                           currentCapBank->setPorterRetFailFlag(false);

                       }
                       else
                       {
                           CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                           << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                       }
                       currentCapBank->setControlRecentlySentFlag(false);
                       if (currentCapBank->updateVerificationState())
                       {
                           returnBoolean = true;
                           currentCapBank->setPerformingVerificationFlag(false);
                           currentFeeder->setPerformingVerificationFlag(false);
                           setBusUpdatedFlag(true);
                           return returnBoolean;
                       }
                       foundCap = true;
                       break;
                   }
                }
                foundFeeder = true;
                currentFeeder->setRetryIndex(0);
                break;
            }
        }

    }
    if (foundCap == false)
    {
        CTILOG_WARN(dout, "Last Verification Cap Bank controlled NOT FOUND");
        returnBoolean = true;
    }
    if (foundFeeder == false)
    {
        CTILOG_WARN(dout, "Last Verification Feeder controlled NOT FOUND");
        returnBoolean = true;
    }
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isVarCheckNeeded

    Returns a boolean if the control interval is up or if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isVarCheckNeeded(const CtiTime& currentDateTime)
{
    bool returnBoolean = false;

    try
    {

        if( getStrategy()->getControlInterval() > 0 || getLikeDayControlFlag() )
        {
            if ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
                (getSendMoreTimeControlledCommandsFlag() && !getRecentlyControlledFlag()) )
            {
                returnBoolean = true;
            }
            else
            {
                returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
            }
        }
        else
        {
            if ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
            {
                if( getSendMoreTimeControlledCommandsFlag() && !getRecentlyControlledFlag())
                {
                    returnBoolean = true;
                }
                else
                {
                    returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
            {
                if( _ccfeeders.size() > 0 )
                {
                    //confirm cap bank changes on just the feeder var value
                    if( getRecentlyControlledFlag() )
                    {
                        try
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                            if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                            {
                                if( currentFeeder->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = true;
                                }
                            }
                            else
                            {
                                for(long i=0;i<_ccfeeders.size();i++)
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                    if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                                    {
                                        if( currentFeeder->getNewPointDataReceivedFlag() )
                                        {
                                            returnBoolean = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }

                        if( !returnBoolean )
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                            if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                            {
                                if( currentFeeder->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = true;
                                }
                            }
                            else
                            {
                                for(long i=0;i<_ccfeeders.size();i++)
                                {
                                    currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                    if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                                    {
                                        if( currentFeeder->getNewPointDataReceivedFlag() )
                                        {
                                            returnBoolean = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else if (getVerificationFlag() && getPerformingVerificationFlag())
                    {
                        for(long i=0;i<_ccfeeders.size();i++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = true;
                            }
                            break;
                        }
                    }
                    //only try to control bus optimized if we have new var values
                    //for the bus and all feeders off the bus
                    else if( _newpointdatareceivedflag )
                    {
                        returnBoolean = true;
                        for(long i=0;i<_ccfeeders.size();i++)
                        {
                            if( !((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = false;
                                break;
                            }
                        }
                    }
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
            {
                if( _ccfeeders.size() > 0 )
                {
                    for(long i=0;i<_ccfeeders.size();i++)
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                        if( (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None  &&
                            currentFeeder->getStrategy()->getControlInterval() > 0) || currentFeeder->getLikeDayControlFlag() )
                        {
                            returnBoolean = (getNextCheckTime().seconds() <= currentDateTime.seconds());
                        }

                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = true;
                            break;
                        }
                    }
                }
                else if (getVerificationFlag() && getPerformingVerificationFlag())
                {
                    for(long i=0;i<_ccfeeders.size();i++)
                    {
                         CtiCCFeeder* currentFeeder =  (CtiCCFeeder*)_ccfeeders[i];

                        if( currentFeeder->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = true;
                        }
                        break;
                    }
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::ManualOnly )
            {
                if( _newpointdatareceivedflag )
                {
                    returnBoolean = true;
                }
                else
                {
                    for(long i=0;i<_ccfeeders.size();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = true;
                            break;
                        }
                    }
                }
            }
            else
            {
                CTILOG_INFO(dout, "Invalid Control Method");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }


    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isConfirmCheckNeeded

    Returns a boolean if the if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isConfirmCheckNeeded()
{
    bool returnBoolean = false;

    try
    {

        if( getRecentlyControlledFlag() )
        {
            if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
            {
                if( _ccfeeders.size() > 0 )
                {
                    //confirm cap bank changes on just the feeder var value
                    try
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                        if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                        {
                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = true;
                            }
                        }
                        else
                        {
                            for(long i=0;i<_ccfeeders.size();i++)
                            {
                                currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                                {
                                    if( currentFeeder->getNewPointDataReceivedFlag() )
                                    {
                                        returnBoolean = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }

                    if( !returnBoolean )
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(getLastFeederControlledPosition());

                        if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                        {
                            if( currentFeeder->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = true;
                            }
                        }
                        else
                        {
                            for(long i=0;i<_ccfeeders.size();i++)
                            {
                                currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                                if( currentFeeder->getPaoId() == getLastFeederControlledPAOId() )
                                {
                                    if( currentFeeder->getNewPointDataReceivedFlag() )
                                    {
                                        returnBoolean = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
            {
                if( _ccfeeders.size() > 0 )
                {
                    for(long i=0;i<_ccfeeders.size();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = true;
                            break;
                        }
                    }
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::ManualOnly )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                    if( !returnBoolean )
                    {
                        for(long i=0;i<_ccfeeders.size();i++)
                        {
                            if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = true;
                                break;
                            }
                        }
                    }
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
            {
                if( _ccfeeders.size() > 0 )
                {
                    returnBoolean = _newpointdatareceivedflag;
                }
            }
            else
            {
                CTILOG_INFO(dout, "Invalid Control Method");
            }
        }
        else if (getVerificationFlag())
        {
            if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                 getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
            {
                if( _ccfeeders.size() > 0 )
                {
                    for(long i=0;i<_ccfeeders.size();i++)
                    {
                        if( ((CtiCCFeeder*)_ccfeeders.at(i))->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = true;
                            break;
                        }
                    }
                }
            }
            else if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    clearOutNewPointReceivedFlags

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::clearOutNewPointReceivedFlags()
{
    setNewPointDataReceivedFlag(false);
    for(int i=0;i<_ccfeeders.size();i++)
    {
        ((CtiCCFeeder*)_ccfeeders.at(i))->setNewPointDataReceivedFlag(false);
    }
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isAlreadyControlled()
{

    if (getPerformingVerificationFlag())
    {
        return isVerificationAlreadyControlled();
    }
    bool returnBoolean = false;
    try
    {
        bool found = false;
        long minConfirmPercent = getStrategy()->getMinConfirmPercent();

        if( _IGNORE_NOT_NORMAL_FLAG && getCurrentVarPointQuality() != NormalQuality )
        {
            return returnBoolean;
        }

        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
             getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( !currentFeeder->getRecentlyControlledFlag() )
                {
                    continue;
                }
                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                }
                if( currentFeeder->isAlreadyControlled(minConfirmPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                               currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                               currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getRegression(),
                                               currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC() ,
                                               currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag() ))
                {
                    returnBoolean = true;
                    break;
                }
            }
        }
        else if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                  getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }

            CtiCCFeeder* currentFeeder = NULL;
            for(long i=0;i<=_ccfeeders.size();i++)
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

                if( currentFeeder->getPaoId() != getLastFeederControlledPAOId() )
                {
                    continue;
                }

                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                }

                returnBoolean = currentFeeder->isAlreadyControlled(minConfirmPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                           getPhaseBValueBeforeControl(), getPhaseCValueBeforeControl(),
                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue(),
                                           getVarValueBeforeControl(), getCurrentVarLoadPointValue(), regression, regressionA, regressionB, regressionC,
                                           getUsePhaseData(), (getTotalizedControlFlag() || getPrimaryBusFlag())  );

                break;
            }

        }
        else if ( getStrategy()->getMethodType() == ControlStrategy::ManualOnly )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( !currentFeeder->getRecentlyControlledFlag() )
                {
                    continue;
                }
                if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                { //use feeder values
                    if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                    }
                    if( currentFeeder->isAlreadyControlled(minConfirmPercent, currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                               currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                               currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                               currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(),
                                               currentFeeder->getRegression(), currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC(),
                                               currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag()  ))
                    {
                        returnBoolean = true;
                        break;
                    }
                }
                else
                { // use substation values.
                    if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                    {
                        minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                    }
                    if( currentFeeder->isAlreadyControlled(minConfirmPercent, getCurrentVarPointQuality(), getPhaseAValueBeforeControl(),
                                               getPhaseBValueBeforeControl(), getPhaseCValueBeforeControl(),
                                               getPhaseAValue(), getPhaseBValue(), getPhaseCValue(),
                                               getVarValueBeforeControl(), getCurrentVarLoadPointValue(), regression, regressionA, regressionB, regressionC,
                                               getUsePhaseData(), (getTotalizedControlFlag()||getPrimaryBusFlag())  ))
                    {
                        returnBoolean = true;
                        break;
                    }
                }
            }
        }
        else
        {
            CTILOG_INFO(dout, "Invalid control method");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return returnBoolean;

}


void CtiCCSubstationBus::getNextCapBankToVerify(EventLogEntries &ccEvents)
{
    _currentVerificationFeederId = -1;
    _currentVerificationCapBankId = -1;

    if (getOverlappingVerificationFlag())
    {
        setCapBanksToVerifyFlags(getVerificationStrategy(), ccEvents);
    }

    for(long i=0;i<_ccfeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

        if( currentFeeder->getVerificationFlag() && !currentFeeder->getVerificationDoneFlag() )
        {
            _currentVerificationFeederId = currentFeeder->getPaoId();

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(long j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getVerificationFlag() && !currentCapBank->getVerificationDoneFlag() )
                {
                    _currentVerificationCapBankId = currentCapBank->getPaoId();
                    return;
                }
            }
            currentFeeder->setVerificationDoneFlag(true);
            currentFeeder->setPerformingVerificationFlag(false);
            _currentVerificationFeederId = -1;
        }
    }
    setBusUpdatedFlag(true);
}

void CtiCCSubstationBus::setVerificationFlag(bool verificationFlag)
{
    _dirty |= setVariableIfDifferent(_verificationFlag, verificationFlag);
}

void CtiCCSubstationBus::setPerformingVerificationFlag(bool performingVerificationFlag)
{
    _dirty |= setVariableIfDifferent(_performingVerificationFlag, performingVerificationFlag);
}
void CtiCCSubstationBus::setVerificationDoneFlag(bool verificationDoneFlag)
{
    _dirty |= setVariableIfDifferent(_verificationDoneFlag, verificationDoneFlag);
}

void CtiCCSubstationBus::setOverlappingVerificationFlag(bool overlapFlag)
{
    _dirty |= setVariableIfDifferent(_overlappingSchedulesVerificationFlag, overlapFlag);
}

void CtiCCSubstationBus::setPreOperationMonitorPointScanFlag( bool flag)
{
    _dirty |= setVariableIfDifferent(_preOperationMonitorPointScanFlag, flag);
}

void CtiCCSubstationBus::setOperationSentWaitFlag( bool flag)
{
    _dirty |= setVariableIfDifferent(_operationSentWaitFlag, flag);
}

void CtiCCSubstationBus::setPostOperationMonitorPointScanFlag( bool flag)
{
    _dirty |= setVariableIfDifferent(_postOperationMonitorPointScanFlag, flag);
}

void CtiCCSubstationBus::setWaitForReCloseDelayFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_waitForReCloseDelayFlag, flag);
}

void CtiCCSubstationBus::setWaitToFinishRegularControlFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_waitToFinishRegularControlFlag, flag);
}
void CtiCCSubstationBus::setMaxDailyOpsHitFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_maxDailyOpsHitFlag, flag);
}

void CtiCCSubstationBus::setOvUvDisabledFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_ovUvDisabledFlag, flag);
}
void CtiCCSubstationBus::setCorrectionNeededNoBankAvailFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_correctionNeededNoBankAvailFlag, flag);
}

void CtiCCSubstationBus::setLikeDayControlFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_likeDayControlFlag, flag);
}

void CtiCCSubstationBus::setVoltReductionControlId(long pointid)
{
    _voltReductionControlId = pointid;
}

void CtiCCSubstationBus::setDisableBusPointId(long pointid)
{
    _disableBusPointId = pointid;
}

void CtiCCSubstationBus::setVoltReductionFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_voltReductionFlag, flag);
}

void CtiCCSubstationBus::setSendMoreTimeControlledCommandsFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_sendMoreTimeControlledCommandsFlag, flag);
}

void CtiCCSubstationBus::setCurrentVerificationFeederId(long feederId)
{
    _dirty |= setVariableIfDifferent(_currentVerificationFeederId, feederId);
}


void CtiCCSubstationBus::setCurrentVerificationCapBankId(long capBankId)
{
    _dirty |= setVariableIfDifferent(_currentVerificationCapBankId, capBankId);
}
void CtiCCSubstationBus::setCurrentVerificationCapBankState(long status)
{
    _dirty |= setVariableIfDifferent(_currentCapBankToVerifyAssumedOrigState, status);
}




bool CtiCCSubstationBus::sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = true;
    CtiRequestMsg* request = NULL;
    for (long i = 0; i < _ccfeeders.size(); i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*) _ccfeeders.at(i);
        if( currentFeeder->getPaoId() == _currentVerificationFeederId )
        {
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(long j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getPaoId() == _currentVerificationCapBankId )
                {
                    double controlValue;
                    double confirmValue;
                    double phaseAValue = currentFeeder->getPhaseAValue();
                    double phaseBValue = currentFeeder->getPhaseBValue();
                    double phaseCValue = currentFeeder->getPhaseCValue();

                    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                    {
                        controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                            ? currentFeeder->getCurrentVoltLoadPointValue()
                                            : currentFeeder->getCurrentVarLoadPointValue();

                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                    {
                        controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                            ? getCurrentVoltLoadPointValue()
                                            : currentFeeder->getCurrentVarLoadPointValue();

                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else
                    {
                        controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                            ? getCurrentVoltLoadPointValue()
                                            : getCurrentVarLoadPointValue();

                        confirmValue = getCurrentVarLoadPointValue();
                        phaseAValue = getPhaseAValue();
                        phaseBValue = getPhaseBValue();
                        phaseCValue = getPhaseCValue();

                    }


                    if (currentCapBank->getVCtrlIndex() == 1)
                    {
                        CTILOG_WARN(dout, "Adjusting VerificationControlIndex! setting vCtrlIdx = 2.");
                        currentCapBank->setVCtrlIndex(2);
                    }
                    else if (currentCapBank->getVCtrlIndex() == 2)
                    {
                        if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                        {
                            int control =  CtiCCCapBank::Open;
                            if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&_USE_FLIP_FLAG )
                            {
                                control = 4; //flip
                            }
                            currentFeeder->setEventSequence(getEventSequence());
                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), control, controlValue, confirmValue) ;
                            request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, confirmValue,
                                                                                          phaseAValue, phaseBValue,phaseCValue);

                        }
                        else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                        {
                            //check capbank reclose delay here...
                            if( currentCapBank->getRecloseDelay() > 0 &&
                                currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + currentCapBank->getRecloseDelay() )
                            {
                                Cti::StreamBuffer s;

                                s << "Can Not Close Cap Bank: " << currentCapBank->getPaoName() << " yet...because it has not passed its reclose delay.";

                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    Cti::FormattedList list;

                                    list.add("Last Status Change Time") << currentCapBank->getLastStatusChangeTime();
                                    list.add("Reclose Delay")           << currentCapBank->getRecloseDelay();
                                    list.add("Current Date Time")       << currentDateTime;

                                    s << list;
                                }

                                CTILOG_INFO(dout, s);

                                retVal = false;
                            }
                            else
                            {

                                int control =  CtiCCCapBank::Close;
                                if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
                                {
                                    control = 4; //flip
                                }
                                currentFeeder->setEventSequence(getEventSequence());
                                string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), control, controlValue, confirmValue) ;
                                request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, confirmValue,
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
                                Cti::StreamBuffer s;

                                s << "Can Not Close Cap Bank: " << currentCapBank->getPaoName() << " yet...because it has not passed its reclose delay.";

                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                    Cti::FormattedList list;

                                    list.add("Last Status Change Time") << currentCapBank->getLastStatusChangeTime();
                                    list.add("Reclose Delay")           << currentCapBank->getRecloseDelay();
                                    list.add("Current Date Time")       << currentDateTime;

                                    s << list;
                                }

                                CTILOG_INFO(dout, s);

                                retVal = false;
                            }
                            else
                            {
                                int control =  CtiCCCapBank::Close;
                                if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
                                {
                                    control = 4; //flip
                                }
                                currentFeeder->setEventSequence(getEventSequence());
                                string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), control, controlValue, confirmValue) ;
                                request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, confirmValue,
                                                                                               phaseAValue, phaseBValue,phaseCValue);

                            }
                        }
                        else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                        {
                            int control =  CtiCCCapBank::Open;
                            if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
                            {
                                control = 4; //flip
                            }
                            currentFeeder->setEventSequence(getEventSequence());
                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), control, controlValue, confirmValue) ;
                            request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, confirmValue,
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
                        setLastFeederControlled(currentFeeder->getPaoId());
                        currentFeeder->setLastCapBankControlledDeviceId( currentCapBank->getPaoId());
                        currentFeeder->setLastOperationTime(currentDateTime);
                       ((CtiCCFeeder*)_ccfeeders.at(i))->setLastOperationTime(currentDateTime);
                        setVarValueBeforeControl(getCurrentVarLoadPointValue(), currentFeeder->getOriginalParent().getOriginalParentId());
                        setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        figureEstimatedVarLoadPointValue();
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }

                        //setRecentlyControlledFlag(true);
                        setBusUpdatedFlag(true);
                        return retVal;
                    }

                }
            }

        }
    }

    return retVal;
}

void CtiCCSubstationBus::startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    //get CapBank to perform verification on...subbus stores, currentCapBankToVerifyId

    CtiRequestMsg* request = NULL;

    for (long i = 0; i < _ccfeeders.size(); i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*) _ccfeeders.at(i);

        if( currentFeeder->getPaoId() == _currentVerificationFeederId )
        {
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(long j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                if( currentCapBank->getPaoId() == _currentVerificationCapBankId )
                {

                    currentCapBank->initVerificationControlStatus();
                    setCurrentVerificationCapBankState(currentCapBank->getAssumedOrigVerificationState());
                    currentCapBank->setAssumedOrigVerificationState(getCurrentVerificationCapBankOrigState());
                    currentCapBank->setPreviousVerificationControlStatus(-1);
                    currentCapBank->setVCtrlIndex(1); //1st control sent
                    currentCapBank->setPerformingVerificationFlag(true);
                    currentFeeder->setPerformingVerificationFlag(true);
                    currentFeeder->setCurrentVerificationCapBankId(_currentVerificationCapBankId);

                    double controlValue;
                    double confirmValue;
                    double phaseAValue = currentFeeder->getPhaseAValue();
                    double phaseBValue = currentFeeder->getPhaseBValue();
                    double phaseCValue = currentFeeder->getPhaseCValue();


                    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                    {
                        controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                            ? currentFeeder->getCurrentVoltLoadPointValue()
                                            : currentFeeder->getCurrentVarLoadPointValue();

                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                    {
                        controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                            ? getCurrentVoltLoadPointValue()
                                            : currentFeeder->getCurrentVarLoadPointValue();

                        confirmValue = currentFeeder->getCurrentVarLoadPointValue();
                    }
                    else
                    {
                        controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                            ? getCurrentVoltLoadPointValue()
                                            : getCurrentVarLoadPointValue();

                        confirmValue = getCurrentVarLoadPointValue();
                        phaseAValue = getPhaseAValue();
                        phaseBValue = getPhaseBValue();
                        phaseCValue = getPhaseCValue();

                    }


                    if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                    {

                        //add capbank reclose delay check here...
                        int control =  CtiCCCapBank::Close;
                        if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
                        {
                            control = 4; //flip
                        }
                        currentFeeder->setEventSequence(getEventSequence());
                        string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), control, controlValue, confirmValue) ;
                        request = currentFeeder->createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, confirmValue,
                                                                                           phaseAValue, phaseBValue,phaseCValue);
                    }
                    else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                    {
                        int control =  CtiCCCapBank::Open;
                        if (findStringIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
                        {
                            control = 4; //flip
                        }
                        currentFeeder->setEventSequence(getEventSequence());
                        string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), control, controlValue, confirmValue) ;
                        request = currentFeeder->createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, confirmValue,
                                                                                           phaseAValue, phaseBValue,phaseCValue);
                    }


                    if( request != NULL )
                    {
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        setLastFeederControlled(currentFeeder->getPaoId());
                        currentFeeder->setLastCapBankControlledDeviceId( currentCapBank->getPaoId());
                        currentFeeder->setLastOperationTime(currentDateTime);
                        setVarValueBeforeControl(getCurrentVarLoadPointValue(), currentFeeder->getOriginalParent().getOriginalParentId());
                        setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        figureEstimatedVarLoadPointValue();
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                    }
                    //setNewPointDataReceivedFlag(false);
                    //regardless what happened the substation bus should be should be sent to the client
                    setBusUpdatedFlag(true);
                    return;
                }
            }
        }
    }
}
/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isVerificationAlreadyControlled()
{
    bool returnBoolean = false;
    bool foundCap = false;
    bool foundFeeder = false;
    long minConfirmPercent = getStrategy()->getMinConfirmPercent();

    if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
    {
        if( ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
              getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ) &&
              _ALLOW_PARALLEL_TRUING )
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            for(long i = 0; i < _ccfeeders.size(); i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( !currentFeeder->getPerformingVerificationFlag() )
                {
                    continue;
                }
                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                }
                foundFeeder = true;
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
                    returnBoolean = true;
                    break;
                }
            }
            if (foundFeeder == false)
            {
                CTILOG_WARN(dout, "Verification Feeder NOT FOUND");
                returnBoolean = true;
            }

        }
        else if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                  getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod ||
                  !_ALLOW_PARALLEL_TRUING)
        {
            if( minConfirmPercent <= 0 )
            {
                return returnBoolean;
            }
            long quality = getCurrentVarPointQuality();
            double oldCalcValue = getVarValueBeforeControl();
            double newCalcValue = getCurrentVarLoadPointValue();
            double beforeAValue = getPhaseAValueBeforeControl();
            double beforeBValue = getPhaseBValueBeforeControl();
            double beforeCValue = getPhaseCValueBeforeControl();
            double phaseAValue  = getPhaseAValue();
            double phaseBValue  = getPhaseBValue();
            double phaseCValue  = getPhaseCValue();
            bool usePhaseData   = getUsePhaseData();
            bool totalizedControlFlag = (getTotalizedControlFlag() || getPrimaryBusFlag());

            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

                if( currentFeeder->getPaoId() != getCurrentVerificationFeederId() )
                {
                    continue;
                }

                if (!ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))
                {
                    minConfirmPercent = currentFeeder->getStrategy()->getMinConfirmPercent();
                }
                if( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                    getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
                    ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
                      currentFeeder->getCurrentVarLoadPointId() > 0 && !currentFeeder->getUsePhaseData() ) ||
                    ( getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod &&
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
                    returnBoolean = true;
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

void CtiCCSubstationBus::analyzeVerificationByFeeder(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, CtiMultiMsg_vec& capMessages)
{
    bool verifyCapFound = false;



    for(long i=0;i<_ccfeeders.size();i++)
    {
        long minConfirmPercent =  getStrategy()->getMinConfirmPercent();
        long maxConfirmTime =  getStrategy()->getMaxConfirmTime();
        long sendRetries = getControlSendRetries();
        long failPercent = getStrategy()->getFailurePercent();

        CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);

        if (!ciStringEqual(currentCCFeeder->getStrategy()->getStrategyName(),"(none)"))
        {
            minConfirmPercent = currentCCFeeder->getStrategy()->getMinConfirmPercent();
            maxConfirmTime = currentCCFeeder->getStrategy()->getMaxConfirmTime();
            sendRetries = currentCCFeeder->getStrategy()->getControlSendRetries();
            failPercent = currentCCFeeder->getStrategy()->getFailurePercent();
        }

        if (currentCCFeeder->getPerformingVerificationFlag())
        {
            if ( currentCCFeeder->getLastVerificationMsgSentSuccessfulFlag())
            {
                bool alreadyControlled = currentCCFeeder->isVerificationAlreadyControlled(minConfirmPercent, currentCCFeeder->getCurrentVarPointQuality(),
                                                                       currentCCFeeder->getPhaseAValueBeforeControl(),
                                                                       currentCCFeeder->getPhaseBValueBeforeControl(),
                                                                       currentCCFeeder->getPhaseCValueBeforeControl(),
                                                                       currentCCFeeder->getPhaseAValue(),
                                                                       currentCCFeeder->getPhaseBValue(),
                                                                       currentCCFeeder->getPhaseCValue(),
                                                                       currentCCFeeder->getVarValueBeforeControl(),
                                                                       currentCCFeeder->getCurrentVarLoadPointValue(),
                                                                       currentCCFeeder->getUsePhaseData(),
                                                                       currentCCFeeder->getTotalizedControlFlag());

                if (alreadyControlled || currentCCFeeder->isPastMaxConfirmTime(currentDateTime,maxConfirmTime,sendRetries))
                {

                    if ( getControlSendRetries() > 0 && !alreadyControlled &&
                        currentDateTime.seconds() < currentCCFeeder->getLastOperationTime().seconds() + maxConfirmTime)
                    {
                        if(currentCCFeeder->checkForAndPerformVerificationSendRetry(currentDateTime, pointChanges, ccEvents, pilMessages, maxConfirmTime, sendRetries))
                        {
                            setLastOperationTime(currentDateTime);
                            setBusUpdatedFlag(true);
                        }
                        verifyCapFound = true;
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
                            setBusUpdatedFlag(true);
                        }
                        verifyCapFound = true;
                    }
                    else
                    {
                        if (currentCCFeeder->areThereMoreCapBanksToVerify())
                        {
                            if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                            {
                                    CTILOG_DEBUG(dout, "------ CAP BANK VERIFICATION LIST:  SUB-" << getPaoName()<< "( "<<getPaoId()<<" ) FEED-"<<currentCCFeeder->getPaoName()<<" CB-"<<currentCCFeeder->getCurrentVerificationCapBankId());
                            }

                            currentCCFeeder->setEventSequence(getEventSequence());
                            if (currentCCFeeder->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages))
                            {
                                setBusUpdatedFlag(true);
                            }
                            setPerformingVerificationFlag(true);
                            verifyCapFound = true;
                        }


                    }
                }
                else //WAIT
                {
                    verifyCapFound = true;
                }
            }
            else
            {
                if (currentCCFeeder->sendNextCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages))
                {
                     setBusUpdatedFlag(true);
                }
                verifyCapFound = true;

            }

        }
        else //if (!currentCCFeeder->getPerformingVerificationFlag())
        {
            if (currentCCFeeder->areThereMoreCapBanksToVerify())
            {
                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                {
                        CTILOG_DEBUG(dout, "------ CAP BANK VERIFICATION LIST:  SUB-" << getPaoName()<< "( "<<getPaoId()<<" )  FEED-"<<currentCCFeeder->getPaoName()<<" CB-"<<currentCCFeeder->getCurrentVerificationCapBankId());
                }

                currentCCFeeder->setEventSequence(getEventSequence());
                if (currentCCFeeder->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages))
                {
                    setBusUpdatedFlag(true);
                }
                setPerformingVerificationFlag(true);
                verifyCapFound = true;
            }
        }

    }
    if (!verifyCapFound)
    {
        setVerificationDoneFlag(true);
        setPerformingVerificationFlag(false);
        setBusUpdatedFlag(true);

        setVerificationFlag(false);
        capMessages.push_back(new VerifyBanks(getPaoId(),getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
        capMessages.push_back(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, getPaoId()));

        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
           CTILOG_DEBUG(dout, "DISABLED VERIFICATION ON: subBusID: "<<getPaoName() << "( "<<getPaoId()<<" ) ");
        }
    }
}


/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isPastMaxConfirmTime(const CtiTime& currentDateTime)
{
    bool returnBoolean = false;
    try
    {

        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder &&
             _ALLOW_PARALLEL_TRUING)
        {
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( (currentCCFeeder->getRecentlyControlledFlag()  || currentCCFeeder->getPerformingVerificationFlag()) &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) )
                {
                    returnBoolean = true;
                    break;
                }
            }
        }
        else
        {
            //Check feeder strategy's control send retry values
            //Use feeder's value if strategy control send retry is populated...
            long sendRetries = getControlSendRetries();

            if (getLastFeederControlledSendRetries() > 0)
                sendRetries = getLastFeederControlledSendRetries();

            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                if( (currentCCFeeder->getRecentlyControlledFlag() || currentCCFeeder->getPerformingVerificationFlag()) &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),sendRetries) )
                {
                    returnBoolean = true;
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    checkForAndPerformSendRetry

    Returns boolean if .
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::checkForAndPerformSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool returnBoolean = false;

    try
    {

        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
        {
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                currentCCFeeder->setEventSequence(getEventSequence());
                if( currentCCFeeder->getRecentlyControlledFlag() &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) &&
                    currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getStrategy()->getMaxConfirmTime()) )
                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = true;
                    break;
                }
                else if (getVerificationFlag() && getPerformingVerificationFlag() &&
                         currentCCFeeder->getPaoId() == getCurrentVerificationFeederId() &&
                         currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) &&
                         currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getStrategy()->getMaxConfirmTime()) )
                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = true;
                    break;
                }

            }
        }
        else
        {
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
                currentCCFeeder->setEventSequence(getEventSequence());
                if( currentCCFeeder->getPaoId() == getLastFeederControlledPAOId() &&
                    currentCCFeeder->getRecentlyControlledFlag() &&
                    currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) &&
                    currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getStrategy()->getMaxConfirmTime()) )
                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = true;
                    break;
                }
                else if (getVerificationFlag() && getPerformingVerificationFlag() &&
                         currentCCFeeder->getPaoId() == getCurrentVerificationFeederId() &&
                         currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) &&
                         currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getStrategy()->getMaxConfirmTime()) )

                {
                    if (_RETRY_ADJUST_LAST_OP_TIME)
                        setLastOperationTime(currentDateTime);
                    currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
                    returnBoolean = true;
                    break;
                }

            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return returnBoolean;
}


bool CtiCCSubstationBus::checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
   bool returnBoolean = false;

   for(long i=0;i<_ccfeeders.size();i++)
   {
       CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
       if (getVerificationFlag() && getPerformingVerificationFlag() &&
           currentCCFeeder->getPaoId() == getCurrentVerificationFeederId() &&
           currentCCFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) &&
           currentCCFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getStrategy()->getMaxConfirmTime()) )
       {
           if (_RETRY_ADJUST_LAST_OP_TIME)
           {
               currentCCFeeder->setLastOperationTime(currentDateTime);
               setLastOperationTime(currentDateTime);
           }
           currentCCFeeder->setRetryIndex(currentCCFeeder->getRetryIndex() + 1);
           returnBoolean = true;
           break;
       }

   }
   return returnBoolean;
}


/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
bool CtiCCSubstationBus::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if ( _dirty )
    {
        std::string flags( 20, 'N' );

        if( !_insertDynamicDataFlag )
        {
            flags[  0 ] = serializeFlag( _verificationFlag );
            flags[  1 ] = serializeFlag( _performingVerificationFlag );
            flags[  2 ] = serializeFlag( _verificationDoneFlag );
            flags[  3 ] = serializeFlag( _overlappingSchedulesVerificationFlag );
            flags[  4 ] = serializeFlag( _preOperationMonitorPointScanFlag );
            flags[  5 ] = serializeFlag( _operationSentWaitFlag );
            flags[  6 ] = serializeFlag( _postOperationMonitorPointScanFlag );
            flags[  7 ] = serializeFlag( _reEnableBusFlag );
            flags[  8 ] = serializeFlag( _waitForReCloseDelayFlag );
            flags[  9 ] = serializeFlag( _waitToFinishRegularControlFlag );
            flags[ 10 ] = serializeFlag( _maxDailyOpsHitFlag );
            flags[ 11 ] = serializeFlag( _ovUvDisabledFlag );
            flags[ 12 ] = serializeFlag( _correctionNeededNoBankAvailFlag );
            flags[ 13 ] = serializeFlag( _likeDayControlFlag );
            flags[ 14 ] = serializeFlag( _voltReductionFlag );
            flags[ 15 ] = serializeFlag( _sendMoreTimeControlledCommandsFlag );
            flags[ 16 ] = serializeFlag( _disableOvUvVerificationFlag );
            flags[ 17 ] = serializeFlag( _primaryBusFlag );

            static const string updateSql = "update dynamicccsubstationbus set "
                                            "currentvarpointvalue = ?, "
                                            "currentwattpointvalue = ?, "
                                            "nextchecktime = ?, "
                                            "newpointdatareceivedflag = ?, "
                                            "busupdatedflag = ?, "
                                            "lastcurrentvarupdatetime = ?, "
                                            "estimatedvarpointvalue = ?, "
                                            "currentdailyoperations = ?, "
                                            "peaktimeflag = ?, "
                                            "recentlycontrolledflag = ?, "
                                            "lastoperationtime = ?, "
                                            "varvaluebeforecontrol = ?, "
                                            "lastfeederpaoid = ?, "
                                            "lastfeederposition = ?, "
                                            "ctitimestamp = ?, "
                                            "powerfactorvalue = ?, "
                                            "kvarsolution = ?, "
                                            "estimatedpfvalue = ?, "
                                            "currentvarpointquality = ?, "
                                            "waivecontrolflag = ?, "
                                            "additionalflags = ?, "
                                            "currverifycbid = ?, "
                                            "currverifyfeederid = ?, "
                                            "currverifycborigstate = ?, "
                                            "verificationstrategy = ?, "
                                            "cbinactivitytime = ?, "
                                            "currentvoltpointvalue = ?, "
                                            "switchPointStatus = ?, "
                                            "altSubControlValue = ?, "
                                            "eventSeq = ?, "
                                            "currentwattpointquality = ?, "
                                            "currentvoltpointquality = ?, "
                                            "ivcontroltot = ?, "
                                            "ivcount = ?, "
                                            "iwcontroltot = ?, "
                                            "iwcount = ?, "
                                            "phaseavalue = ?, "
                                            "phasebvalue = ?, "
                                            "phasecvalue = ?, "
                                            "lastwattpointtime = ?, "
                                            "lastvoltpointtime = ?, "
                                            "phaseavaluebeforecontrol = ?, "
                                            "phasebvaluebeforecontrol = ?, "
                                            "phasecvaluebeforecontrol = ?"
                                            " where substationbusid = ?";

            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater << _currentvarloadpointvalue
                    << _currentwattloadpointvalue
                    << _nextchecktime
                    << (string)(_newpointdatareceivedflag?"Y":"N")
                    << (string)(_busupdatedflag?"Y":"N")
                    << _lastcurrentvarpointupdatetime
                    << _estimatedvarloadpointvalue
                    << _currentdailyoperations
                    << (string)(_peaktimeflag?"Y":"N")
                    << (string)(_recentlycontrolledflag?"Y":"N")
                    << _lastoperationtime
                    << _varvaluebeforecontrol
                    << _lastfeedercontrolledpaoid
                    << _lastfeedercontrolledposition
                    << currentDateTime
                    << _powerfactorvalue
                    << _kvarsolution
                    << _estimatedpowerfactorvalue
                    << _currentvarpointquality
                    << (string)(_waivecontrolflag?"Y":"N")
                    << flags
                    << _currentVerificationCapBankId
                    << _currentVerificationFeederId
                    << _currentCapBankToVerifyAssumedOrigState
                    << _verificationStrategy
                    << _capBankToVerifyInactivityTime
                    << _currentvoltloadpointvalue
                    << (string)(_switchOverStatus?"Y":"N")
                    << _altSubControlValue
                    << _eventSeq
                    << _currentwattpointquality
                    << _currentvoltpointquality
                    << _iVControlTot
                    << _iVCount
                    << _iWControlTot
                    << _iWCount
                    << _phaseAvalue
                    << _phaseBvalue
                    << _phaseCvalue
                    << _lastWattPointTime
                    << _lastVoltPointTime
                    << _phaseAvalueBeforeControl
                    << _phaseBvalueBeforeControl
                    << _phaseCvalueBeforeControl
                    << getPaoId();

            if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
            {
                _dirty = false; // No error occured!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted substation bus into DynamicCCSubstationBus: " << getPaoName());

            static const string insertSql = "insert into dynamicccsubstationbus values ( "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                            "?, ?, ?, ?, ? )";

            Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

            dbInserter << getPaoId()
            << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << _nextchecktime
            << (string)(_newpointdatareceivedflag?"Y":"N")
            << (string)(_busupdatedflag?"Y":"N")
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << (string)(_peaktimeflag?"Y":"N")
            << (string)(_recentlycontrolledflag?"Y":"N")
            << _lastoperationtime
            << _varvaluebeforecontrol
            << _lastfeedercontrolledpaoid
            << _lastfeedercontrolledposition
            << currentDateTime
            << _powerfactorvalue
            << _kvarsolution
            << _estimatedpowerfactorvalue
            << _currentvarpointquality
            << (string)(_waivecontrolflag?"Y":"N")
            << flags
            << _currentVerificationCapBankId
            << _currentVerificationFeederId
            << _currentCapBankToVerifyAssumedOrigState
            << _verificationStrategy
            << _capBankToVerifyInactivityTime
            << _currentvoltloadpointvalue
            << (string)(_switchOverStatus?"Y":"N")
            << _altSubControlValue
            << _eventSeq
            << _currentwattpointquality
            << _currentvoltpointquality
            << _iVControlTot
            << _iVCount
            << _iWControlTot
            << _iWCount
            << _phaseAvalue
            << _phaseBvalue
            << _phaseCvalue
            << _lastWattPointTime
            << _lastVoltPointTime
            << _phaseAvalueBeforeControl
            << _phaseBvalueBeforeControl
            << _phaseCvalueBeforeControl;

            if( Cti::Database::executeCommand( dbInserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
            {
                _insertDynamicDataFlag = false;
                _dirty = false; // No error occured!
            }
        }
    }

    getOperationStats().dumpDynamicData(conn, currentDateTime);
    for each (const map<long, CtiCCMonitorPointPtr>::value_type & entry in _monitorPoints )
    {
        CtiCCMonitorPointPtr monPoint = entry.second;
        monPoint->dumpDynamicData(conn,currentDateTime);
    }
    PointResponseDaoPtr pointResponseDao = DatabaseDaoFactory().getPointResponseDao();
    for each(const map<Cti::CapControl::PointResponseKey, PointResponsePtr>::value_type  entry in _pointResponses)
    {
        PointResponsePtr pResponse = entry.second;
        bool ret = pointResponseDao->save(conn,*pResponse);

        if( (ret == false) && (_CC_DEBUG & CC_DEBUG_DATABASE) )
        {
            CTILOG_DEBUG(dout, "Point Response save failed. ");
        }
    }

}

/*-------------------------------------------------------------------------
    convertKQToKVAR

    Converts KQ to KVAR, needs kw also
--------------------------------------------------------------------------*/
double CtiCCSubstationBus::convertKQToKVAR(double kq, double kw)
{
    double returnKVAR = 0.0;
    returnKVAR = ((2.0*kq)-kw)/SQRT3;
    return returnKVAR;
}

/*-------------------------------------------------------------------------
    convertKVARToKQ

    Converts KVAR to KQ, needs kw also
--------------------------------------------------------------------------*/
double CtiCCSubstationBus::convertKVARToKQ(double kvar, double kw)
{
    double returnKQ = 0.0;
    returnKQ = ((SQRT3*kvar)+kw)/2.0;
    return returnKQ;
}



bool CtiCCSubstationBus::isBusPerformingVerification()
{
    return _performingVerificationFlag;

}
bool CtiCCSubstationBus::isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime)
{
    bool foundFeeder = false;
    bool returnBoolean = false;


    if ( ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ||
           getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ) &&
           _ALLOW_PARALLEL_TRUING)
    {
         for(long i = 0; i < _ccfeeders.size(); i++)
         {
             CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
             //if( currentFeeder->getPAOId() == getCurrentVerificationFeederId() )
             if( currentFeeder->getPerformingVerificationFlag() )
             {
                 foundFeeder = true;
                 if( currentFeeder->isPastMaxConfirmTime(currentDateTime,getStrategy()->getMaxConfirmTime(),getControlSendRetries()) )
                 {
                     returnBoolean = true;
                     break;
                 }
             }
         }
         if (foundFeeder == false)
         {
             CTILOG_WARN(dout, "Verification Feeder NOT FOUND");
             returnBoolean = true;
         }

    }
    else if (isPastMaxConfirmTime(currentDateTime) )
        return true;
    else
        return false;

   return returnBoolean;
}

bool CtiCCSubstationBus::areThereMoreCapBanksToVerify(EventLogEntries &ccEvents)
{


    getNextCapBankToVerify(ccEvents);
    if (getCurrentVerificationCapBankId() != -1 )//&& !getDisableFlag())
    {
        setPerformingVerificationFlag(true);

        return true;
    }
    else
    {
        for(long i=0;i<_ccfeeders.size();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(i);
            //currentFeeder->setVerificationFlag(false);
            currentFeeder->setPerformingVerificationFlag(false);
            currentFeeder->setVerificationDoneFlag(true);

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            for(long j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                //currentCapBank->setVerificationFlag(false);
                currentCapBank->setPerformingVerificationFlag(false);
                currentCapBank->setVerificationDoneFlag(true);
            }
        }
        setPerformingVerificationFlag(false);
        setBusUpdatedFlag(true);
        return false;
    }
}



void CtiCCSubstationBus::setVerificationStrategy(CtiPAOScheduleManager::VerificationStrategy verificationStrategy)
{
    _dirty |= setVariableIfDifferent(_verificationStrategy, verificationStrategy);
}

CtiPAOScheduleManager::VerificationStrategy CtiCCSubstationBus::getVerificationStrategy(void) const
{
    return _verificationStrategy;
}

void CtiCCSubstationBus::setVerificationDisableOvUvFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_disableOvUvVerificationFlag, flag);
}

bool CtiCCSubstationBus::getVerificationDisableOvUvFlag(void) const
{
    return _disableOvUvVerificationFlag;
}


void CtiCCSubstationBus::setCapBankInactivityTime(long capBankToVerifyInactivityTime)
{
    _dirty |= setVariableIfDifferent(_capBankToVerifyInactivityTime, capBankToVerifyInactivityTime);
}

long CtiCCSubstationBus::getCapBankInactivityTime(void) const
{
    return _capBankToVerifyInactivityTime;
}


void CtiCCSubstationBus::setCapBanksToVerifyFlags(int verificationStrategy, EventLogEntries &ccEvents)
{
    long x, j;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    for (x = 0; x < _ccfeeders.size(); x++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
        if (!currentFeeder->getDisableFlag())
        {
            if (!getOverlappingVerificationFlag())
            {
                currentFeeder->setVerificationFlag(true);
            }
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)(ccCapBanks[j]);
                if (isBankSelectedByVerificationStrategy(verificationStrategy, currentCapBank))
                {
                    string textInfo = string("CapBank: ");
                    textInfo += currentCapBank->getPaoName();
                    CtiCCEventType_t eventAction = capControlEnableVerification;
                    if (!currentCapBank->getDisableFlag())
                    {
                        if (!getOverlappingVerificationFlag())
                        {
                            currentCapBank->setVerificationFlag(true);
                        }
                        else
                        {
                            if (!currentCapBank->getVerificationDoneFlag())
                            {
                                currentCapBank->setVerificationFlag(true);
                                currentFeeder->setVerificationFlag(true);
                            }
                            else
                            {
                                currentFeeder->setVerificationDoneFlag(true);
                            }
                        }

                        textInfo += " scheduled for verification.";
                    }
                    else
                    {

                        textInfo += " Disabled! Will not verify.";
                        eventAction = capControlDisableVerification;
                    }
                    long stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
                    ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getPaoId(), currentFeeder->getPaoId(), eventAction, getEventSequence(), currentCapBank->getControlStatus(), textInfo, "cap control verification"));
                }
            }
        }
    }
    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        Cti::StreamBuffer s;

        s << "CapBank LIST";

        for (x = 0; x < _ccfeeders.size(); x++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders.at(x);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(j=0;j<ccCapBanks.size();j++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[j]);
                if (currentCapBank->getVerificationFlag())
                {
                    s <<"  " << currentCapBank->getPaoId();
                }
            }
        }

        CTILOG_DEBUG(dout, s);
    }
    setBusUpdatedFlag(true);
}


bool CtiCCSubstationBus::isBankSelectedByVerificationStrategy(int verificationStrategy, CtiCCCapBankPtr currentCapBank)
{

    switch (verificationStrategy)
    {
        case CtiPAOScheduleManager::AllBanks:
        {
            return ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState);
        }
        case CtiPAOScheduleManager::FailedAndQuestionableBanks:
        {
            return (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) );
        }

        case CtiPAOScheduleManager::FailedBanks:
        {
           return (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            ( currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail ||
                              currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ) );

        }
        case CtiPAOScheduleManager::QuestionableBanks:
        {
            return (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            (currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                             currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) );
        }
        case CtiPAOScheduleManager::SelectedForVerificationBanks:
        {
            return currentCapBank->isSelectedForVerification();
        }
        case CtiPAOScheduleManager::BanksInactiveForXTime:
        {
            CtiTime currentTime = CtiTime();
            currentTime.now();
            return (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                            ( currentCapBank->getLastStatusChangeTime().seconds() <= ( currentTime.seconds() - getCapBankInactivityTime())) );
        }
        case CtiPAOScheduleManager::StandAloneBanks:
        {
            return ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::StandAloneState);
        }
        default:
        {
            return false;
        }
    }

}

void CtiCCSubstationBus::updatePointResponsePreOpValues(CtiCCCapBank* capBank)
{
    CTILOG_INFO(dout, "Updating POINT RESPONSE PREOPVALUES for CapBank: " <<capBank->getPaoName() << " Device ID: " << capBank->getPaoName() << " has " << capBank->getPointResponses().size() << " point responses");

    for (int i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        const CtiCCMonitorPoint & point = *_multipleMonitorPoints[i];

        try
        {
            if (capBank->updatePointResponsePreOpValue(point.getPointId(),point.getValue()))
            {
                CTILOG_INFO(dout, "Device Id: " << capBank->getPaoName() << " Point Id: " << point.getPointId( )<< " Value: " << point.getValue());
            }
        }
        catch (NotFoundException& e)
        {
            CTILOG_WARN(dout, "Error Updating PreOpValue for deltas. PointId not found: " << point.getPointId());
        }
    }
}

/**
 * This function will update the point response deltas but take
 * into account any missing updated values. pointIds is a list
 * of points we got an update for from a recent scan.
 *
 */
void CtiCCSubstationBus::updatePointResponseDeltas(std::set<long> pointIds)
{
    try
    {
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

        CtiCCFeederPtr feeder = store->findFeederByPAObjectID(getLastFeederControlledPAOId());
        if (feeder == NULL)
        {
            return;
        }

        CtiCCCapBankPtr capBank = store->findCapBankByPAObjectID(feeder->getLastCapBankControlledDeviceId());
        if (capBank == NULL)
        {
            return;
        }

        //This checks for the case that we did not get a response from the bank in question.
        if (pointIds.find(capBank->getPointIdByAttribute(PointAttribute::CbcVoltage)) == pointIds.end())
        {
            return;
        }

        CTILOG_INFO(dout, "Updating POINT RESPONSE DELTAS for CapBank: " << capBank->getPaoName());

        for each (const map<long, CtiCCMonitorPointPtr>::value_type & entry in _monitorPoints)
        {
            const CtiCCMonitorPoint & point = *entry.second;

            //This checks to make sure we got an update for the monitor point before updating the deltas.
            if (pointIds.find(point.getPointId()) != pointIds.end())
            {
                try
                {
                    capBank->updatePointResponseDelta(point);
                }
                catch (NotFoundException& e)
                {
                    CTILOG_WARN(dout, "Error Updating delta value. PointId not found: " << point.getPointId());
                }
            }
        }

        // Update the point response deltas in the DB immediately for IVVC.  See YUK-8929
        capBank->dumpDynamicPointResponseData();

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBus::updatePointResponseDeltas()
{
    try
    {
        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
        {
            for (long i = 0; i < _ccfeeders.size();i++)
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
            for (long i = 0; i < _ccfeeders.size();i++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];

                //check feeder to see if it all monitor points have received new pointdata
                if (currentFeeder->getPaoId() == getLastFeederControlledPAOId())
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                     for(long j=0;j<ccCapBanks.size();j++)
                     {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                        if (currentCapBank->getPaoId() == currentFeeder->getLastCapBankControlledDeviceId())
                        {
                            CTILOG_INFO(dout, "Updating POINT RESPONSE DELTAS for CapBank: " <<currentCapBank->getPaoName());
                            for (int k = 0; k < _multipleMonitorPoints.size(); k++)
                            {
                                const CtiCCMonitorPoint & point = *_multipleMonitorPoints[k];
                                try
                                {
                                    currentCapBank->updatePointResponseDelta(point);
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "Error Updating delta value. PointId not found: " << point.getPointId());
                                }
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}



bool CtiCCSubstationBus::areAllMonitorPointsNewEnough(const CtiTime& currentDateTime)
{
    bool retVal = false;
    if ( isScanFlagSet() && currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE *60) )  //T1 Expired.. Force Process
    {
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint & point = *_multipleMonitorPoints[i];
            if (point.getScanInProgress())
            {
                point.setScanInProgress(false);
            }
        }

        retVal = true;
    }
    else
    {
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint & point = *_multipleMonitorPoints[i];
            if (point.getTimeStamp().seconds() > (getLastOperationTime().seconds() - 30) &&
                point.getTimeStamp().seconds() + _POINT_AGE <= currentDateTime.seconds())
            {
                retVal = true;
                if (point.getScanInProgress())
                {
                    point.setScanInProgress(false);
                }
            }
            else
            {
                retVal = false;
                break;
            }
        }
        if (retVal == true)
        {
            bool scanInProgress = false;
            for (int i = 0; i < _multipleMonitorPoints.size(); i++)
            {
                const CtiCCMonitorPoint & point = *_multipleMonitorPoints[i];
                if (point.getScanInProgress())
                {
                    scanInProgress = true;

                }
            }
        }
    }
    return retVal;

}

unsigned long CtiCCSubstationBus::getMonitorPointScanTime()
{

    CtiTime timeNow;
    return timeNow.seconds();
}

bool CtiCCSubstationBus::isScanFlagSet()
{
    if (_preOperationMonitorPointScanFlag || _postOperationMonitorPointScanFlag)
    {
        return true;
    }
    return false;
}



bool CtiCCSubstationBus::scanAllMonitorPoints()
{
    bool retVal = false;

    for (int i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPoint & point = *_multipleMonitorPoints[i];
        if (point.isScannable() && !point.getScanInProgress())
        {
            for (long j = 0; j < _ccfeeders.size();j++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[j];
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k = 0; k < ccCapBanks.size(); k++ )
                {

                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if (currentCapBank->getPaoId() == point.getDeviceId())
                    {
                        CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );

                        if(pAltRate)
                        {
                            pAltRate->insert(-1);                       // token, not yet used.
                            pAltRate->insert( currentCapBank->getControlDeviceId() );       // Device to poke.

                            pAltRate->insert( -1 );                      // Seconds since midnight, or NOW if negative.

                            pAltRate->insert( 0 );                      // Duration of zero should cause 1 scan.

                            CtiCapController::getInstance()->sendMessageToDispatch(pAltRate, CALLSITE);

                            CTILOG_INFO(dout, "Requesting scans at the alternate scan rate for " << currentCapBank->getPaoName());

                            point.setScanInProgress(true);
                            retVal = true;
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

void CtiCCSubstationBus::analyzeMultiVoltBus1(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiCCMonitorPointPtr outOfRangeMonitorPoint;

    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
    {
        for (long i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (!currentFeeder->getRecentlyControlledFlag() && !currentFeeder->getPostOperationMonitorPointScanFlag())
            {
                if (!currentFeeder->areAllMonitorPointsInVoltageRange(outOfRangeMonitorPoint))
                {
                    if (currentFeeder->areAllMonitorPointsNewEnough(currentDateTime))
                    {
                        if (!currentFeeder->areAllMonitorPointsInVoltageRange(outOfRangeMonitorPoint))
                        {
                            if (currentFeeder->voltControlBankSelectProcess(*outOfRangeMonitorPoint, pointChanges, ccEvents, pilMessages))
                            {
                                currentFeeder->setOperationSentWaitFlag(true);
                                currentFeeder->setLastOperationTime(currentDateTime);
                                currentFeeder->setRecentlyControlledFlag(true);
                                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                                setRecentlyControlledFlag(true);
                                setBusUpdatedFlag(true);
                            }
                            else
                            {
                                CTILOG_INFO(dout, "No Bank Available for Control");

                                if (currentFeeder->getPreOperationMonitorPointScanFlag())
                                    currentFeeder->setPreOperationMonitorPointScanFlag(false);
                                if (currentFeeder->getOperationSentWaitFlag())
                                    currentFeeder->setOperationSentWaitFlag(false);
                                if (currentFeeder->getPostOperationMonitorPointScanFlag())
                                    currentFeeder->setPostOperationMonitorPointScanFlag(false);
                            }
                        }
                    }
                    else // !currentFeeder->isScanFlagSet()
                    {
                        if (!currentFeeder->isScanFlagSet())
                        {
                            if (currentFeeder->scanAllMonitorPoints())
                            {
                                currentFeeder->setPreOperationMonitorPointScanFlag(true);
                                currentFeeder->setLastOperationTime(currentDateTime);
                            }
                        }
                        //else if (timeElapsed...)
                            //
                    }
                }
                else if ( getStrategy()->getUnitType() == ControlStrategy::MultiVoltVar )  //check for VarImprovement flag?
                {
                    if (analyzeBusForVarImprovement(pointChanges, ccEvents, pilMessages))
                    {
                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                        {
                            CTILOG_DEBUG(dout, "MULTIVOLT: Var Improvement was attempted on SubBus: "<< getPaoName());
                        }
                    }

                }
            }
            else if(currentFeeder->getRecentlyControlledFlag())//recently controlled...
            {
                bool pastTime = currentFeeder->isPastMaxConfirmTime(currentDateTime, getStrategy()->getMaxConfirmTime(), getControlSendRetries());
                bool controled = currentFeeder->isAlreadyControlled(getStrategy()->getMinConfirmPercent(), currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValueBeforeControl(),
                                           currentFeeder->getPhaseBValueBeforeControl(), currentFeeder->getPhaseCValueBeforeControl(),
                                           currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(),
                                           currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(), currentFeeder->getRegression(),
                                           currentFeeder->getRegressionA(), currentFeeder->getRegressionB(), currentFeeder->getRegressionC(),
                                            currentFeeder->getUsePhaseData(), currentFeeder->getTotalizedControlFlag() );

                if ( pastTime || controled )
                {
                    if (!controled )
                    {
                        if (currentFeeder->attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, getStrategy()->getMaxConfirmTime()))
                        {
                            if (_RETRY_ADJUST_LAST_OP_TIME)
                                setLastOperationTime(currentDateTime);
                            currentFeeder->setRetryIndex(currentFeeder->getRetryIndex() + 1);
                        }
                        else if( currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, getStrategy()->getMinConfirmPercent(), getStrategy()->getFailurePercent(),
                                                              currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(),
                                                              currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                              currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(), currentFeeder->getRegression()) )
                        {
                            currentFeeder->scanAllMonitorPoints();
                            if (currentFeeder->getOperationSentWaitFlag())
                                currentFeeder->setOperationSentWaitFlag(false);
                            currentFeeder->setPostOperationMonitorPointScanFlag(true);
                            currentFeeder->setLastOperationTime(currentDateTime);
                            setBusUpdatedFlag(true);
                        }
                    }
                    else
                    {
                        currentFeeder->capBankControlStatusUpdate(pointChanges, ccEvents, getStrategy()->getMinConfirmPercent(), getStrategy()->getFailurePercent(),
                                                              currentFeeder->getVarValueBeforeControl(), currentFeeder->getCurrentVarLoadPointValue(),
                                                              currentFeeder->getCurrentVarPointQuality(), currentFeeder->getPhaseAValue(),
                                                              currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue(), currentFeeder->getRegression());
                        currentFeeder->scanAllMonitorPoints();
                        if (currentFeeder->getOperationSentWaitFlag())
                            currentFeeder->setOperationSentWaitFlag(false);
                        currentFeeder->setPostOperationMonitorPointScanFlag(true);
                        currentFeeder->setLastOperationTime(currentDateTime);
                    }
                    setBusUpdatedFlag(true);

                }
            }
            else // currentFeeder->getPostOperationMonitorPointScanFlag() == true
            {
                if ( currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE * 60) )
                {
                    currentFeeder->updatePointResponseDeltas();

                    currentFeeder->setPreOperationMonitorPointScanFlag(false);
                    currentFeeder->setOperationSentWaitFlag(false);
                    currentFeeder->setPostOperationMonitorPointScanFlag(false);
                    currentFeeder->setRecentlyControlledFlag(false);
                    setRecentlyControlledFlag(false);
                }
            }
        }
        for (int i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->getRecentlyControlledFlag())
            {
                setRecentlyControlledFlag(true);
                break;
            }
        }
        clearOutNewPointReceivedFlags();
        if( isVarCheckNeeded(currentDateTime) && getStrategy()->getControlInterval() > 0 )
        {
            figureNextCheckTime();
        }
        figureEstimatedVarLoadPointValue();
    }
}

void CtiCCSubstationBus::analyzeMultiVoltBus(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiCCMonitorPointPtr outOfRangeMonitorPoint;

    if ( getStrategy()->getMethodType() != ControlStrategy::IndividualFeeder )
    {
        if (!getRecentlyControlledFlag() && !getPostOperationMonitorPointScanFlag())
        {
            if (!areAllMonitorPointsInVoltageRange(outOfRangeMonitorPoint))
            {
                if (areAllMonitorPointsNewEnough(currentDateTime))
                {
                    if (!areAllMonitorPointsInVoltageRange(outOfRangeMonitorPoint))
                    {
                        if (voltControlBankSelectProcess(*outOfRangeMonitorPoint, pointChanges, ccEvents, pilMessages))
                        {
                            setOperationSentWaitFlag(true);
                            setLastOperationTime(currentDateTime);
                            setRecentlyControlledFlag(true);
                            //setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                            //setRecentlyControlledFlag(true);
                            setBusUpdatedFlag(true);
                        }
                        else
                        {
                            CTILOG_INFO(dout, "No Bank Available for Control");

                            if (getPreOperationMonitorPointScanFlag())
                                setPreOperationMonitorPointScanFlag(false);
                            if (getOperationSentWaitFlag())
                                setOperationSentWaitFlag(false);
                            if (getPostOperationMonitorPointScanFlag())
                                setPostOperationMonitorPointScanFlag(false);
                        }
                    }
                }
                else // !isScanFlagSet()
                {
                    if (!isScanFlagSet())
                    {
                        if (scanAllMonitorPoints())
                        {
                            setPreOperationMonitorPointScanFlag(true);
                            setLastOperationTime(currentDateTime);
                        }
                    }
                    //else if (timeElapsed...)
                        //
                }
            }
            else if ( getStrategy()->getUnitType() == ControlStrategy::MultiVoltVar )
            {
                if (analyzeBusForVarImprovement(pointChanges, ccEvents, pilMessages))
                {
                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                    {
                        CTILOG_DEBUG(dout, "MULTIVOLT: Var Improvement was attempted on SubBus: "<< getPaoName());
                    }
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
                    else
                    {
                        capBankControlStatusUpdate(pointChanges, ccEvents);
                        scanAllMonitorPoints();
                        if (getOperationSentWaitFlag())
                            setOperationSentWaitFlag(false);
                        setPostOperationMonitorPointScanFlag(true);
                        setLastOperationTime(currentDateTime);
                    }
                }
                else
                {
                    capBankControlStatusUpdate(pointChanges, ccEvents);
                    scanAllMonitorPoints();
                    if (getOperationSentWaitFlag())
                        setOperationSentWaitFlag(false);
                    setPostOperationMonitorPointScanFlag(true);
                    setLastOperationTime(currentDateTime);
                }
                setBusUpdatedFlag(true);

            }
        }
        else // getPostOperationMonitorPointScanFlag() == true
        {
            if ( currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE * 60) )
            {
                updatePointResponseDeltas();

                setPreOperationMonitorPointScanFlag(false);
                setOperationSentWaitFlag(false);
                setPostOperationMonitorPointScanFlag(false);
                setRecentlyControlledFlag(false);
            }
        }
        for (long i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->getRecentlyControlledFlag())
            {
                setRecentlyControlledFlag(true);
                break;
            }
        }
        clearOutNewPointReceivedFlags();
        if( isVarCheckNeeded(currentDateTime) && getStrategy()->getControlInterval() > 0 )
        {
            figureNextCheckTime();
        }
        figureEstimatedVarLoadPointValue();
    }
}

CtiCCCapBankPtr CtiCCSubstationBus::getMonitorPointParentBankAndFeeder( const CtiCCMonitorPoint & point,
                                                                        CtiCCFeederPtr          & feeder )
{
    for ( CtiCCFeederPtr currentFeeder : _ccfeeders )
    {
        if ( currentFeeder )
        {
            for ( CtiCCCapBankPtr currentBank : currentFeeder->getCCCapBanks() )
            {
                if ( currentBank && point.getDeviceId() == currentBank->getPaoId() )
                {
                    feeder = currentFeeder;
                    return currentBank;
                }
            }
        }
    }

    return nullptr;
}

bool CtiCCSubstationBus::analyzeBusForVarImprovement(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{

    bool retVal = false;
    CtiRequestMsg* request = NULL;
    CtiTime currentDateTime;

    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
    {
        for (long i = 0; i < _ccfeeders.size();i++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
            if (currentFeeder->getCurrentVarLoadPointId() > 0)
            {

                double lagLevel = (isPeakTime(currentDateTime)?currentFeeder->getStrategy()->getPeakVARLag():currentFeeder->getStrategy()->getOffPeakVARLag());
                double leadLevel = (getPeakTimeFlag()?currentFeeder->getStrategy()->getPeakVARLead():currentFeeder->getStrategy()->getOffPeakVARLead());
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    ( currentFeeder->getCurrentVarPointQuality() == NormalQuality &&
                      currentFeeder->getCurrentVoltPointQuality() == NormalQuality ) )
                {
                    if( currentFeeder->getCurrentVarLoadPointValue() > lagLevel )
                    {
                        //select bank to open...make sure volt points stay in range though.
                        std::vector <CtiCCMonitorPointPtr>& monPoints = currentFeeder->getMultipleMonitorPoints();
                        for (int j = 0; j < monPoints.size(); j++)
                        {
                            CtiCCMonitorPoint & pt = *monPoints[j];
                            CtiCCCapBankPtr bank = currentFeeder->getMonitorPointParentBank(pt);
                            if (bank->getControlStatus() == CtiCCCapBank::Open ||
                                bank->getControlStatus() == CtiCCCapBank::OpenQuestionable )
                            {
                                try
                                {
                                    PointResponse pResponse = bank->getPointResponse(pt);
                                    if ( (pt.getValue() + pResponse.getDelta() <= pt.getUpperBandwidth() &&
                                          pt.getValue() + pResponse.getDelta() >= pt.getLowerBandwidth() ) ||
                                          pt.getValue() + pResponse.getDelta() < pt.getUpperBandwidth() )
                                    {
                                        CTILOG_INFO(dout, "Attempting to Improve PF on Feeder: "<<getPaoName()<<" CapBank: "<<bank->getPaoName());

                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt.getDeviceId()<<"/"<<pt.getPointId()<<" Parent CapBank: "<<bank->getPaoName() <<" selected to Close");
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (currentFeeder->areOtherMonitorPointResponsesOk(pt.getPointId(), bank, CtiCCCapBank::Close))
                                        {
                                            double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                       ? currentFeeder->getCurrentVoltLoadPointValue()
                                                                       : currentFeeder->getCurrentVarLoadPointValue();

                                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, currentFeeder->getCurrentVarLoadPointValue());
                                            request = currentFeeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                              currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());

                                             currentFeeder->updatePointResponsePreOpValues(bank);
                                        }
                                    }
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< bank->getPaoName() <<  " pointId: " << pt.getPointId());
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
                        std::vector <CtiCCMonitorPointPtr>& monPoints = currentFeeder->getMultipleMonitorPoints();
                        for (int j = 0; j < monPoints.size(); j++)
                        {
                            CtiCCMonitorPoint & pt = *monPoints[j];
                            CtiCCCapBankPtr bank = currentFeeder->getMonitorPointParentBank(pt);
                            if (bank->getControlStatus() == CtiCCCapBank::Close ||
                                bank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                            {
                                try
                                {
                                    PointResponse pResponse = bank->getPointResponse(pt);
                                    if ( (pt.getValue() - pResponse.getDelta() <= pt.getUpperBandwidth() &&
                                          pt.getValue() - pResponse.getDelta() >= pt.getLowerBandwidth() ) ||
                                          pt.getValue() - pResponse.getDelta() > pt.getLowerBandwidth() )
                                    {
                                        CTILOG_INFO(dout, "Attempting to Improve PF on Feeder: "<<getPaoName()<<" CapBank: "<<bank->getPaoName());

                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt.getDeviceId()<<"/"<<pt.getPointId()<<" Parent CapBank: "<<bank->getPaoName() <<" selected to Open");
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (currentFeeder->areOtherMonitorPointResponsesOk(pt.getPointId(), bank, CtiCCCapBank::Open))
                                        {
                                            double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                       ? currentFeeder->getCurrentVoltLoadPointValue()
                                                                       : currentFeeder->getCurrentVarLoadPointValue();

                                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, currentFeeder->getCurrentVarLoadPointValue());
                                            request = currentFeeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, text, currentFeeder->getCurrentVarLoadPointValue(),
                                                                                              currentFeeder->getPhaseAValue(), currentFeeder->getPhaseBValue(), currentFeeder->getPhaseCValue());

                                             currentFeeder->updatePointResponsePreOpValues(bank);
                                        }
                                    }
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< bank->getPaoName() <<  " pointId: " << pt.getPointId());
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
                   CTILOG_DEBUG(dout, "MULTIVOLT: Var Information NOT available for Feeder: "<<currentFeeder->getPaoName());
               }
            }
        }
        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(true);
            //setLastCapBankControlledDeviceId( bestBank->getPAOId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }

            retVal = true;
        }

    }
    else
    {
        if (getCurrentVarLoadPointId() > 0)
        {

            CtiCCFeederPtr  parentFeeder = nullptr;

            double lagLevel = (isPeakTime(currentDateTime)?getStrategy()->getPeakVARLag():getStrategy()->getOffPeakVARLag());
            double leadLevel = (getPeakTimeFlag()?getStrategy()->getPeakVARLead():getStrategy()->getOffPeakVARLead());
            if( !_IGNORE_NOT_NORMAL_FLAG ||
                ( getCurrentVarPointQuality() == NormalQuality &&
                  getCurrentVoltPointQuality() == NormalQuality ) )
            {
                if( getCurrentVarLoadPointValue() > lagLevel )
                {
                    //select bank to open...make sure volt points stay in range though.
                    std::vector <CtiCCMonitorPointPtr>& monPoints = getMultipleMonitorPoints();
                    for (int i = 0; i < monPoints.size(); i++)
                    {
                        CtiCCMonitorPoint & pt = *monPoints[i];
                        CtiCCCapBankPtr bank = getMonitorPointParentBankAndFeeder(pt, parentFeeder);

                        if ( bank && parentFeeder )
                        {
                            if (bank->getControlStatus() == CtiCCCapBank::Open ||
                                bank->getControlStatus() == CtiCCCapBank::OpenQuestionable )
                            {
                                try
                                {
                                    PointResponse pResponse = bank->getPointResponse(pt);
                                    if ( (pt.getValue() + pResponse.getDelta() <= pt.getUpperBandwidth() &&
                                          pt.getValue() + pResponse.getDelta() >= pt.getLowerBandwidth() ) ||
                                          pt.getValue() + pResponse.getDelta() < pt.getUpperBandwidth() )
                                    {
                                        CTILOG_INFO(dout, "Attempting to Improve PF on Sub: "<<getPaoName()<<" CapBank: "<<bank->getPaoName());

                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt.getDeviceId()<<"/"<<pt.getPointId()<<" Parent CapBank: "<<bank->getPaoName() <<" selected to Close");
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (areOtherMonitorPointResponsesOk(pt.getPointId(), bank, CtiCCCapBank::Close))
                                        {
                                            double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                        ? getCurrentVoltLoadPointValue()
                                                                        : getCurrentVarLoadPointValue();

                                            string text = parentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                            request =  parentFeeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                            updatePointResponsePreOpValues(bank);
                                        }
                                    }
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< bank->getPaoName() <<  " pointId: " << pt.getPointId());
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
                    std::vector <CtiCCMonitorPointPtr>& monPoints = getMultipleMonitorPoints();
                    for (int i = 0; i < monPoints.size(); i++)
                    {
                        CtiCCMonitorPoint & pt = *monPoints[i];
                        CtiCCCapBankPtr bank = getMonitorPointParentBankAndFeeder(pt, parentFeeder);

                        if ( bank && parentFeeder)
                        {
                            if (bank->getControlStatus() == CtiCCCapBank::Close ||
                                bank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
                            {
                                try
                                {
                                    PointResponse pResponse = bank->getPointResponse(pt);

                                    if ( (pt.getValue() - pResponse.getDelta() <= pt.getUpperBandwidth() &&
                                          pt.getValue() - pResponse.getDelta() >= pt.getLowerBandwidth() ) ||
                                          pt.getValue() - pResponse.getDelta() > pt.getLowerBandwidth() )
                                    {
                                        CTILOG_INFO(dout, "Attempting to Improve PF on Sub: "<<getPaoName()<<" CapBank: "<<bank->getPaoName());

                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<pt.getDeviceId()<<"/"<<pt.getPointId()<<" Parent CapBank: "<<bank->getPaoName() <<" selected to Open");
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (areOtherMonitorPointResponsesOk(pt.getPointId(), bank, CtiCCCapBank::Open))
                                        {
                                            double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                        ? getCurrentVoltLoadPointValue()
                                                                        : getCurrentVarLoadPointValue();

                                            string text = parentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                            request = parentFeeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                            updatePointResponsePreOpValues(bank);
                                        }
                                    }
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< bank->getPaoName() <<  " pointId: " << pt.getPointId());
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
               CTILOG_DEBUG(dout, "MULTIVOLT: Var Information NOT available for Sub: "<<getPaoName());
           }
        }

        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(true);
            //setLastCapBankControlledDeviceId( bestBank->getPAOId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }

            retVal = true;
        }
    }
    return retVal;
}


bool CtiCCSubstationBus::voltControlBankSelectProcess(const CtiCCMonitorPoint & point, CtiMultiMsg_vec &pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec &pilMessages)
{
    bool retVal = false;

    CtiRequestMsg* request = NULL;

    CtiCCCapBank* bestBank = NULL;
    CtiCCFeederPtr  parentFeeder = nullptr;
   //Check for undervoltage condition first.
   try
   {
        if (point.getValue() < point.getLowerBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            double bestDelta = 0;

            //1.  First check this point's parent bank to see if we can close it.
            parentBank = getMonitorPointParentBankAndFeeder(point, parentFeeder);
            if ( parentBank && parentFeeder )
            {
                if (parentBank->getControlStatus() == CtiCCCapBank::Open ||
                    parentBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
                {
                    try
                    {
                        PointResponse pResponse = parentBank->getPointResponse(point);

                        if ( (point.getValue() + pResponse.getDelta() <= point.getUpperBandwidth() &&
                              point.getValue() + pResponse.getDelta() >= point.getLowerBandwidth() ) ||
                              point.getValue() + pResponse.getDelta() < point.getUpperBandwidth() )
                        {
                            CTILOG_INFO(dout, "Attempting to Increase Voltage on Feeder: "<<parentFeeder->getPaoName()<<" CapBank: "<<parentBank->getPaoName());
                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CTILOG_DEBUG(dout, "MULTIVOLT: Monitorpoint.bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" Parent CapBank: "<<parentBank->getPaoName() <<" selected to Close");
                            }
                            //Check other monitor point responses using this potential capbank
                            if (areOtherMonitorPointResponsesOk(point.getPointId(), parentBank, CtiCCCapBank::Close))
                            {
                                double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                            ? getCurrentVoltLoadPointValue()
                                                            : getCurrentVarLoadPointValue();

                                double monitorValue =  parentFeeder->getCurrentVarLoadPointValue();
                                double phaseAValue = parentFeeder->getPhaseAValue();
                                double phaseBValue = parentFeeder->getPhaseBValue();
                                double phaseCValue = parentFeeder->getPhaseCValue();
                                if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                                {
                                    monitorValue = getCurrentVarLoadPointValue();
                                    phaseAValue = getPhaseAValue();
                                    phaseBValue = getPhaseBValue();
                                    phaseCValue = getPhaseCValue();

                                }
                                string text = parentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, monitorValue);
                                request = parentFeeder->createDecreaseVarRequest(parentBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                            {
                                parentBank = NULL;
                            }
                        }
                        else
                        {
                            parentBank = NULL;
                        }
                    }
                    catch (NotFoundException& e)
                    {
                        CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< parentBank->getPaoName() <<  " pointId: " << point.getPointId());
                    }
                }
                else
                {
                    parentBank = NULL;
                }
            }

            //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {

                for (long h = 0; h < _ccfeeders.size();h++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[h];
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for (long i = 0; i < ccCapBanks.size(); i++)
                    {

                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*) ccCapBanks[i];
                        if (currentCapBank->getControlStatus() == CtiCCCapBank::Open || currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
                        {
                            if (point.getDeviceId() != currentCapBank->getPaoId())
                            {
                                try
                                {
                                    PointResponse pResponse = currentCapBank->getPointResponse(point);
                                    if ( (point.getValue() + pResponse.getDelta() <= point.getUpperBandwidth() &&
                                          point.getValue() + pResponse.getDelta() >= point.getLowerBandwidth() ) ||
                                          pResponse.getDelta() == 0 ||
                                          point.getValue() + pResponse.getDelta() < point.getUpperBandwidth() )
                                    {
                                        CTILOG_INFO(dout, "Attempting to Increase Voltage on Feeder: "<<currentFeeder->getPaoName()<<" CapBank: "<<currentCapBank->getPaoName());
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_DEBUG(dout, "MULTIVOLT: Monitorpoint.bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" Parent CapBank: "<<currentCapBank->getPaoName() <<" selected to Close");
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (areOtherMonitorPointResponsesOk(point.getPointId(), currentCapBank, CtiCCCapBank::Close))
                                        {
                                            double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                        ? getCurrentVoltLoadPointValue()
                                                                        : getCurrentVarLoadPointValue();

                                            double monitorValue =  currentFeeder->getCurrentVarLoadPointValue();
                                            double phaseAValue = currentFeeder->getPhaseAValue();
                                            double phaseBValue = currentFeeder->getPhaseBValue();
                                            double phaseCValue = currentFeeder->getPhaseCValue();
                                            if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                                            {
                                                monitorValue = getCurrentVarLoadPointValue();
                                                phaseAValue = getPhaseAValue();
                                                phaseBValue = getPhaseBValue();
                                                phaseCValue = getPhaseCValue();

                                            }
                                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, monitorValue);
                                            request = currentFeeder->createDecreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                            updatePointResponsePreOpValues(currentCapBank);
                                            bestBank = currentCapBank;
                                            parentFeeder = currentFeeder;
                                        }
                                    }
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< currentCapBank->getPaoName() <<  " pointId: " << point.getPointId());
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
                CTILOG_INFO(dout, "No Banks Available to Close on Sub: " <<getPaoName());
            }
        }
        else if (point.getValue() > point.getUpperBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            double bestDelta = 0;

            //1.  First check this point's parent bank to see if we can open it.
            parentBank = getMonitorPointParentBankAndFeeder(point, parentFeeder);
            if ( parentBank && parentFeeder )
            {
                if (parentBank->getControlStatus() == CtiCCCapBank::Close ||
                    parentBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                {
                    try
                    {
                        PointResponse pResponse = parentBank->getPointResponse(point);

                        if ( (point.getValue() - pResponse.getDelta() <= point.getUpperBandwidth() &&
                              point.getValue() - pResponse.getDelta() >= point.getLowerBandwidth() ) ||
                              //pRespone.getDelta() == 0 ||
                              point.getValue() - pResponse.getDelta() > point.getLowerBandwidth() )
                        {
                            CTILOG_INFO(dout, "Attempting to Decrease Voltage on Feeder: "<<parentFeeder->getPaoName()<<" CapBank: "<<parentBank->getPaoName());
                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CTILOG_DEBUG(dout, "MULTIVOLT: Monitorpoint.bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" Parent CapBank: "<<parentBank->getPaoName() <<" selected to Open");
                            }
                            //Check other monitor point responses using this potential capbank
                            if (areOtherMonitorPointResponsesOk(point.getPointId(), parentBank, CtiCCCapBank::Open))
                            {
                                double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                            ? getCurrentVoltLoadPointValue()
                                                            : getCurrentVarLoadPointValue();

                                double monitorValue =  parentFeeder->getCurrentVarLoadPointValue();
                                double phaseAValue = parentFeeder->getPhaseAValue();
                                double phaseBValue = parentFeeder->getPhaseBValue();
                                double phaseCValue = parentFeeder->getPhaseCValue();
                                if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                                {
                                    monitorValue = getCurrentVarLoadPointValue();
                                    phaseAValue = getPhaseAValue();
                                    phaseBValue = getPhaseBValue();
                                    phaseCValue = getPhaseCValue();

                                }
                                string text = parentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, monitorValue);
                                request = parentFeeder->createIncreaseVarRequest(parentBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                            {
                                parentBank = NULL;
                            }
                        }
                        else
                        {
                            parentBank = NULL;
                        }
                    }
                    catch (NotFoundException& e)
                    {
                        CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< parentBank->getPaoName() <<  " pointId: " << point.getPointId());
                    }
                }
                else
                {
                    parentBank = NULL;
                }
            }

           //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {

                for (long h = 0; h < _ccfeeders.size();h++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[h];
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for (long i = 0; i < ccCapBanks.size(); i++)
                    {

                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*) ccCapBanks[i];
                        if (currentCapBank->getControlStatus() == CtiCCCapBank::Close || currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                        {
                            if (point.getDeviceId() != currentCapBank->getPaoId())
                            {
                                try
                                {
                                    PointResponse pResponse = currentCapBank->getPointResponse(point);

                                    if ( (point.getValue() - pResponse.getDelta() <= point.getUpperBandwidth() &&
                                          point.getValue() - pResponse.getDelta() >= point.getLowerBandwidth() ) ||
                                          pResponse.getDelta() == 0 ||
                                          point.getValue() - pResponse.getDelta() > point.getUpperBandwidth() )
                                    {
                                        CTILOG_INFO(dout, "Attempting to Decrease Voltage on Feeder: "<<currentFeeder->getPaoName()<<" CapBank: "<<currentCapBank->getPaoName());
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_DEBUG(dout, "MULTIVOLT: Monitorpoint.bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" Parent CapBank: "<<currentCapBank->getPaoName() <<" selected to Open");
                                        }
                                        //Check other monitor point responses using this potential capbank
                                        if (areOtherMonitorPointResponsesOk(point.getPointId(), currentCapBank, CtiCCCapBank::Open))
                                        {
                                            double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                        ? getCurrentVoltLoadPointValue()
                                                                        : getCurrentVarLoadPointValue();

                                            double monitorValue =  currentFeeder->getCurrentVarLoadPointValue();
                                            double phaseAValue = currentFeeder->getPhaseAValue();
                                            double phaseBValue = currentFeeder->getPhaseBValue();
                                            double phaseCValue = currentFeeder->getPhaseCValue();
                                            if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                                            {
                                                monitorValue = getCurrentVarLoadPointValue();
                                                phaseAValue = getPhaseAValue();
                                                phaseBValue = getPhaseBValue();
                                                phaseCValue = getPhaseCValue();

                                            }
                                            string text = currentFeeder->createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, monitorValue);
                                            request = currentFeeder->createIncreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, monitorValue, phaseAValue, phaseBValue,phaseCValue);

                                            updatePointResponsePreOpValues(currentCapBank);
                                            bestBank = currentCapBank;
                                            parentFeeder = currentFeeder;
                                        }
                                    }
                                }
                                catch (NotFoundException& e)
                                {
                                    CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< currentCapBank->getPaoName() <<  " pointId: " << point.getPointId());
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
                CTILOG_INFO(dout, "No Banks Available to Open on Sub: "<<getPaoName());
            }
       }
       if ( request && bestBank && parentFeeder )
       {
            pilMessages.push_back(request);
           //setLastOperationTime(currentDateTime);
            setOperationSentWaitFlag(true);
            setLastFeederControlled( parentFeeder->getPaoId());
            parentFeeder->setLastCapBankControlledDeviceId( bestBank->getPaoId());
            parentFeeder->setRecentlyControlledFlag(true);
            parentFeeder->setVarValueBeforeControl(parentFeeder->getCurrentVarLoadPointValue());
            setVarValueBeforeControl(getCurrentVarLoadPointValue(), parentFeeder->getOriginalParent().getOriginalParentId());
            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setOperationSentWaitFlag(true);

            retVal = true;
       }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return retVal;

}

bool CtiCCSubstationBus::areOtherMonitorPointResponsesOk(long mPointID, CtiCCCapBank* potentialCap, int action)
{
    bool retVal = true;

    //action = 0 --> open
    //action = 1 --> close

    for (long i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        const CtiCCMonitorPoint & otherPoint = *_multipleMonitorPoints[i];
        if (otherPoint.getPointId() != mPointID)
        {
            for each (const PointResponse& pResponse in potentialCap->getPointResponses())
            {
                if (otherPoint.getPointId() == pResponse.getPointId())
                {
                    if (action) //CLOSE
                    {
                        if (pResponse.getDelta() != 0)
                        {
                            if (otherPoint.getValue() + pResponse.getDelta() > otherPoint.getUpperBandwidth() )
                            {
                                {
                                    CTILOG_INFO(dout, "OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: " << getPaoName() << " CapBank: " << potentialCap->getPaoName() << " otherPoint: " << otherPoint.getPointId() << " " << otherPoint.getDeviceId() << " Value: " << otherPoint.getValue() << " Delta: " << pResponse.getDelta() << " pResponse: " << pResponse.getPointId() << " " << pResponse.getDeviceId());                                }
                                retVal = false;
                                break;
                            }
                            else
                            {
                                retVal = true;
                            }
                        }
                        else
                        {
                            retVal = true;
                        }
                    }
                    else //OPEN
                    {
                        if (pResponse.getDelta() != 0)
                        {
                            if (otherPoint.getValue() - pResponse.getDelta() < otherPoint.getLowerBandwidth())
                            {
                                {
                                    CTILOG_INFO(dout, "OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: " << getPaoName() << " CapBank: " << potentialCap->getPaoName() << " otherPoint: " << otherPoint.getPointId() << " " << otherPoint.getDeviceId() << " Value: " << otherPoint.getValue() << " Delta: " << pResponse.getDelta() << " pResponse: " << pResponse.getPointId() << " " << pResponse.getDeviceId());                                }
                                retVal = false;
                                break;
                            }
                            else
                                retVal = true;
                        }
                        else
                        {
                            retVal = true;
                        }
                    }
                }
            }
            if (retVal == false)
            {
                break;
            }
        }
    }
    return retVal;
}

bool CtiCCSubstationBus::areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr & oorPoint)
{
    bool retVal = false;

    for (int i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
        if (point->getValue() >= point->getLowerBandwidth() &&
            point->getValue() <= point->getUpperBandwidth() )
        {
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                 CTILOG_DEBUG(dout, "MULTIVOLT: Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getDeviceId()<<" is inside limits.  Current value: "<<point->getValue());
            }
            retVal = true;
        }
        else
        {

            CTILOG_INFO(dout, "Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getDeviceId()<<" is outside limits.  Current value: "<<point->getValue());
            oorPoint = point;
            retVal = false;
            break;
        }
    }
    return retVal;
}


bool CtiCCSubstationBus::isMultiVoltBusAnalysisNeeded(const CtiTime& currentDateTime)
{
    bool retVal = false;
    if ( ( getStrategy()->getUnitType() == ControlStrategy::MultiVolt ||
           getStrategy()->getUnitType() == ControlStrategy::MultiVoltVar ) &&
           ! getVerificationFlag() )
    {
        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
             getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
        {
            for (long i = 0; i < _ccfeeders.size();i++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                if (currentFeeder->getNewPointDataReceivedFlag())
                {
                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                    {
                        CTILOG_DEBUG(dout, "MULTIVOLT ANALYSIS on Sub: "<<getPaoName());
                    }
                    retVal = true;
                    return retVal;
                }
                //if (currentFeeder->getPostOperationMonitorPointScanFlag() )
            }
        }

        if (_newpointdatareceivedflag )
        {
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                CTILOG_DEBUG(dout, "MULTIVOLT ANALYSIS on Sub: "<<getPaoName());
            }
            retVal = true;

        }
        else if( getStrategy()->getControlInterval() > 0 )
        {
            retVal = (getNextCheckTime().seconds() <= currentDateTime.seconds());

        }
    }

    return retVal;
}

bool CtiCCSubstationBus::isBusAnalysisNeeded(const CtiTime& currentDateTime)
{
    bool retVal = false;
    if (getStrategy()->getUnitType() != ControlStrategy::None)
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::MultiVolt ||
             getStrategy()->getUnitType() == ControlStrategy::MultiVoltVar )
        {
            if (_newpointdatareceivedflag || getVerificationFlag())
                retVal = true;

        }
        else if ( isVarCheckNeeded(currentDateTime) || isConfirmCheckNeeded() )
        {
                retVal = true;
        }
        else if (getVerificationFlag())
        {
            if (!isBusPerformingVerification())
                retVal = true;
            else if (isBusPerformingVerification() && isPastMaxConfirmTime(currentDateTime))
            {
                if (_lastVerificationCheck.seconds() + 30 < currentDateTime.seconds())
                    retVal = true;
            }
            else if (_lastVerificationCheck.seconds() + 30 < currentDateTime.seconds())
            {
                retVal = true;
            }
        }
    }

    return retVal;
}

int CtiCCSubstationBus::getNumOfBanksInState(set<int> setOfStates)
{
    int currentNum = 0;
    std::vector<CtiCCCapBankPtr> banks = getAllSwitchedCapBanks();
    for each (CtiCCCapBankPtr currentCapBank in banks)
    {
        currentNum += setOfStates.count(currentCapBank->getControlStatus());
    }
    return currentNum;
}

CtiCCCapBankPtr CtiCCSubstationBus::getPendingCapBank( )
{
    for each (CtiCCFeederPtr currentFeeder in _ccfeeders)
    {
         for each (CtiCCCapBankPtr currentBank in currentFeeder->getAllCapBanks( ))
         {
             if (currentBank->isPendingStatus())
             {
                 return currentBank;
             }
         }
    }
    return NULL;
}
std::vector<CtiCCCapBankPtr> CtiCCSubstationBus::getAllCapBanks( )
{
    std::vector<CtiCCCapBankPtr> banks;
    for each (CtiCCFeederPtr currentFeeder in _ccfeeders)
    {
        std::vector<CtiCCCapBankPtr> fdrbanks = currentFeeder->getAllCapBanks( );
        banks.insert(banks.begin(), fdrbanks.begin(), fdrbanks.end());
    }
    return banks;
}
std::vector<CtiCCCapBankPtr> CtiCCSubstationBus::getAllSwitchedCapBanks( )
{
    std::vector<CtiCCCapBankPtr> banks;
    for each (CtiCCFeederPtr currentFeeder in _ccfeeders)
    {
        std::vector<CtiCCCapBankPtr> fdrbanks = currentFeeder->getAllSwitchedCapBanks( );
        banks.insert(banks.begin(), fdrbanks.begin(), fdrbanks.end());
    }
    return banks;
}

void CtiCCSubstationBus::checkForAndProvideNeededTimeOfDayControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;

    if( !getDisableFlag() &&
        currentDateTime.seconds() >= getLastOperationTime().seconds() + getStrategy()->getControlDelayTime() )
    {
        try
        {
            int numOfBanks = getAllCapBanks().size();
            int currentNumClosed = getNumOfBanksInState(Cti::CapControl::ClosedStates);

            int targetNumClose = numOfBanks * (_percentToClose * 0.01);
            int targetState = CtiCCCapBank::Close; //close
            int targetNumInState = targetNumClose;
            int currentNumInState = currentNumClosed;
            if (targetNumClose < currentNumClosed)
            {
                targetState = CtiCCCapBank::Open; //open
                targetNumInState = numOfBanks  - targetNumClose;
                currentNumInState = numOfBanks - currentNumClosed;
            }
            CtiCCFeederPtr currentFeeder = NULL;
            CtiCCCapBankPtr capBank =  NULL;
            int loopCount = 0;

            if (_CC_DEBUG & CC_DEBUG_TIMEOFDAY )
            {
                CTILOG_DEBUG(dout, "SubBus: "<<getPaoName() << " percentClose: " << _percentToClose << "% targetClose: " << targetNumClose <<
                                   " currentClose: " << currentNumClosed << " targetOpen: " << (numOfBanks - targetNumClose)  <<" currentOpen: " << (numOfBanks - currentNumClosed));
            }
            setSendMoreTimeControlledCommandsFlag(false);

            long currentPosition = getLastFeederControlledPosition();
            while (currentNumInState < targetNumInState )
            {
                if( currentPosition >= _ccfeeders.size()-1 )
                    currentPosition = 0;
                else
                    currentPosition++;

                currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                if( !currentFeeder->getDisableFlag() &&
                    !currentFeeder->getWaiveControlFlag() &&
                    currentDateTime.seconds() >= currentFeeder->getLastOperationTime().seconds() + currentFeeder->getStrategy()->getControlDelayTime() )
                {
                    try
                    {
                        //CtiCCCapBank::Open = 0, CtiCCCapBank::Close = 1
                        //need to pass in -1 for close and 1 for open
                        int operation = (targetState == CtiCCCapBank::Close ? -1 : 1);
                        capBank = currentFeeder->findCapBankToChangeVars( operation, pointChanges);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                if (capBank != NULL)
                {
                    string text = "";

                    try
                    {
                        if (getStrategy()->getMaxConfirmTime() == 0)
                            request = currentFeeder->createForcedVarRequest(capBank, pointChanges, ccEvents, targetState, "TimeOfDay Control");
                        else
                        {
                            text = currentFeeder->createTextString(getStrategy()->getControlMethod(), targetState, getCurrentVarLoadPointValue(),  getCurrentVarLoadPointValue());

                            double varValue = getCurrentVarLoadPointValue();
                            double phaseAValue = getPhaseAValue();
                            double phaseBValue = getPhaseBValue();
                            double phaseCValue = getPhaseCValue();
                            if ( (currentFeeder->getCurrentVarLoadPointId() > 0 && !currentFeeder->getUsePhaseData() )  ||
                                 (currentFeeder->getCurrentVarLoadPointId() > 0 && currentFeeder->getPhaseBId() > 0
                                  && currentFeeder->getPhaseCId() > 0 && currentFeeder->getUsePhaseData() ) )
                            {
                                varValue = currentFeeder->getCurrentVarLoadPointValue();
                                phaseAValue = currentFeeder->getPhaseAValue();
                                phaseBValue = currentFeeder->getPhaseBValue();
                                phaseCValue = currentFeeder->getPhaseCValue();

                            }
                            if (targetState == CtiCCCapBank::Close)
                            {
                                request = currentFeeder->createDecreaseVarRequest(capBank, pointChanges, ccEvents, text,  varValue,
                                                                                  phaseAValue, phaseBValue, phaseCValue);
                            }
                            else
                            {
                                request = currentFeeder->createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, varValue,
                                                                                  phaseAValue, phaseBValue, phaseCValue);
                            }
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }

                    if( request != NULL )
                    {
                        try
                        {
                            if (getStrategy()->getMaxConfirmTime() == 0)
                                currentFeeder->createForcedVarConfirmation(capBank, pointChanges, ccEvents, "TimeOfDay Control");
                            else
                            {
                                currentFeeder->setRecentlyControlledFlag(true);
                                setRecentlyControlledFlag(true);
                                setVarValueBeforeControl(getCurrentVarLoadPointValue(), currentFeeder->getOriginalParent().getOriginalParentId());
                                if (currentNumInState + 1 < targetNumInState)
                                {
                                    setSendMoreTimeControlledCommandsFlag(true);
                                    currentNumInState = targetNumInState;
                                }
                            }
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }

                        try
                        {
                            pilMessages.push_back(request);
                            setLastOperationTime(currentDateTime);
                            setLastFeederControlled(currentFeeder->getPaoId());
                            currentFeeder->setLastOperationTime(currentDateTime);
                            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }
                    }
                    setBusUpdatedFlag(true);

                    currentNumInState += 1;
                }
                else
                {
                    if (loopCount > _ccfeeders.size() - 1)
                    {
                        string actionString = (targetState == CtiCCCapBank::Close ? " close " : " open ");
                        CTILOG_INFO(dout, "No more banks available to" << actionString << "on subBus: "<<getPaoName());
                        break;
                    }
                    else
                        loopCount += 1;
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
}

void CtiCCSubstationBus::checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    CtiRequestMsg* request = NULL;

    map <long, long> controlid_action_map;
    controlid_action_map.clear();

    if( !getDisableFlag() &&
        currentDateTime.seconds() >= getLastOperationTime().seconds() + getStrategy()->getControlDelayTime() )
    {
        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
        {
            for(long i=0;i<_ccfeeders.size();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if( !currentFeeder->getDisableFlag() )
                {
                    if (!currentFeeder->getLikeDayControlFlag() && !currentFeeder->getRecentlyControlledFlag())
                    {
                        if( currentFeeder->checkForAndProvideNeededIndividualControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentFeeder->getPeakTimeFlag(), getDecimalPlaces(), getStrategy()->getControlUnits(), getMaxDailyOpsHitFlag()) )
                        {
                            setLastOperationTime(currentDateTime);
                            setRecentlyControlledFlag(true);
                            setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                        }
                    }
                    else if( currentFeeder->checkForAndProvideNeededFallBackControl(currentDateTime,pointChanges,ccEvents,pilMessages))                    {
                        setLastOperationTime(currentDateTime);
                    }
                    setBusUpdatedFlag(true);
                }
            }
        }
        else
        {
            CtiTime lastSendTime = getLastOperationTime();
            if (getCurrentVarLoadPointId() &&
                getLastOperationTime() < getLastCurrentVarPointUpdateTime())
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
            CtiCCSubstationBusStore::getInstance()->reloadMapOfBanksToControlByLikeDay(getPaoId(), 0, &controlid_action_map,lastSendTime, fallBackConst);
            std::map <long, long>::iterator iter = controlid_action_map.begin();
            while (iter != controlid_action_map.end())
            {
                {
                    PointIdToCapBankMultiMap::iterator bankIter, end;
                    if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(iter->first, bankIter, end))
                    {
                        CtiCCCapBankPtr bank = bankIter->second;
                        CtiCCFeederPtr feed = CtiCCSubstationBusStore::getInstance()->findFeederByPAObjectID(bank->getParentId());
                        if (feed != NULL)
                        {
                            if (feed->getParentId() == getPaoId())
                            {
                                string text = "";
                                request = feed->createForcedVarRequest(bank, pointChanges, ccEvents, iter->second, "LikeDay Control");

                                if( request != NULL )
                                {
                                    feed->createForcedVarConfirmation(bank, pointChanges, ccEvents, "LikeDay Control");


                                    pilMessages.push_back(request);
                                    setLastOperationTime(currentDateTime);
                                    setLastFeederControlled(feed->getPaoId());
                                    feed->setLastOperationTime(currentDateTime);
                                    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                                }
                                setBusUpdatedFlag(true);
                            }
                        }
                    }
                }
                iter = controlid_action_map.erase(iter);
            }

        }

    }
}

void CtiCCSubstationBus::performDataOldAndFallBackNecessaryCheck()
{
    bool subBusFlag = false;
    CtiTime timeNow = CtiTime();

    if (!getDisableFlag())
    {

        //This could be moved to happen when the control strategy is changed instead of running every cycle.
        if ( getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
             getStrategy()->getMethodType() == ControlStrategy::ManualOnly ||
             getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
        {
            for (long i = 0; i < _ccfeeders.size();i++)
            {
                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
                if (currentFeeder->getLikeDayControlFlag() !=
                    currentFeeder->isDataOldAndFallBackNecessary(getStrategy()->getControlUnits()))
                {
                    setBusUpdatedFlag(true);
                }
                currentFeeder->setLikeDayControlFlag(false);
            }
        }

        if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
        {
            subBusFlag = performDataOldAndFallBackNecessaryCheckOnFeeders();
        }
        else
        {
            if (getStrategy()->getLikeDayFallBack())
            {
                if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                {
                    if ((getCurrentVarLoadPointId() && getCurrentVoltLoadPointId()) &&
                        (timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                         timeNow > getLastVoltPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT))
                    {
                        subBusFlag = true;
                    }
                }
                else if ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar )
                {
                    if ((getCurrentVarLoadPointId() && getCurrentWattLoadPointId()) &&
                        (timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                         timeNow > getLastWattPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT))
                    {
                        subBusFlag = true;
                    }
                }
                else
                {
                    if ( getCurrentVarLoadPointId() &&
                         timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT)
                    {
                        subBusFlag = true;
                    }
                }

                if ( getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                {
                    performDataOldAndFallBackNecessaryCheckOnFeeders();
                }
            }
        }
    }

    setLikeDayControlFlag(subBusFlag);
}

bool CtiCCSubstationBus::performDataOldAndFallBackNecessaryCheckOnFeeders()
{
    bool retFlag = false;

    for (long i = 0; i < _ccfeeders.size();i++)
    {
        CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)_ccfeeders[i];
        bool newFlag;
        if (currentFeeder->getLikeDayControlFlag() !=
            (newFlag = currentFeeder->isDataOldAndFallBackNecessary(getStrategy()->getControlUnits())))
        {
            currentFeeder->setLikeDayControlFlag(newFlag);
            setBusUpdatedFlag(true);
        }
        retFlag |= newFlag;
    }

    return retFlag;
}

void CtiCCSubstationBus::verifyControlledStatusFlags()
{

    for(long j=0;j<getCCFeeders().size();j++)
    {
        CtiCCFeeder* currentFeeder = getCCFeeders().at(j);
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(long k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                setRecentlyControlledFlag(true);
                currentFeeder->setRecentlyControlledFlag(true);
                setLastFeederControlled(currentFeeder->getPaoId());
                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                j = getCCFeeders().size();
                k = ccCapBanks.size();
            }
        }
    }
}

void CtiCCSubstationBus::addAllSubPointsToMsg(std::set<long>& pointAddMsg)
{

    if( getCurrentVarLoadPointId() > 0 )
    {
        pointAddMsg.insert(getCurrentVarLoadPointId());
    }
    if( getCurrentWattLoadPointId() > 0 )
    {
        pointAddMsg.insert(getCurrentWattLoadPointId());
    }
    if (getCurrentVoltLoadPointId() > 0)
    {
        pointAddMsg.insert(getCurrentVoltLoadPointId());
    }
    if (getEstimatedVarLoadPointId() > 0)
    {
        pointAddMsg.insert(getEstimatedVarLoadPointId());
    }
    if (getDailyOperationsAnalogPointId() > 0)
    {
        pointAddMsg.insert(getDailyOperationsAnalogPointId());
    }
    if (getPowerFactorPointId() > 0)
    {
        pointAddMsg.insert(getPowerFactorPointId());
    }
    if (getEstimatedPowerFactorPointId() > 0)
    {
        pointAddMsg.insert(getEstimatedPowerFactorPointId());
    }
    if (getSwitchOverPointId() > 0)
    {
        pointAddMsg.insert(getSwitchOverPointId());
    }
    if (getPhaseBId() > 0)
    {
        pointAddMsg.insert(getPhaseBId());
    }
    if (getPhaseCId() > 0)
    {
        pointAddMsg.insert(getPhaseCId());
    }
    if (getVoltReductionControlId() > 0)
    {
        pointAddMsg.insert(getVoltReductionControlId());
    }
    if (getDisableBusPointId() > 0)
    {
        pointAddMsg.insert(getDisableBusPointId());
    }
    if (getCommsStatePointId() > 0)
    {
        pointAddMsg.insert(getCommsStatePointId());
    }
    if (getOperationStats().getUserDefOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getUserDefOpSuccessPercentId());
    }
    if (getOperationStats().getDailyOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getDailyOpSuccessPercentId());
    }
    if (getOperationStats().getWeeklyOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getWeeklyOpSuccessPercentId());
    }
    if (getOperationStats().getMonthlyOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getMonthlyOpSuccessPercentId());
    }
}


int CtiCCSubstationBus::getAlterateBusIdForPrimary() const
{
    int paoId = getPaoId();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    PaoIdToPointIdMultiMap::iterator it;
    std::pair<PaoIdToPointIdMultiMap::iterator, PaoIdToPointIdMultiMap::iterator> ret;

    ret = store->getSubsWithAltSubID(paoId);
    for (it = ret.first; it != ret.second; it++)
    {
        CtiCCSubstationBusPtr altSub = store->findSubBusByPAObjectID((*it).second);
        if (altSub != NULL && altSub->getSwitchOverStatus())
        {
            return altSub->getPaoId();
        }
    }

    return paoId;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSubstationBus* CtiCCSubstationBus::replicate() const
{
    CtiCCSubstationBusPtr newBus = new CtiCCSubstationBus( *this );

    // The feeders are currently owned by both busses due to the shallow
    //  default copy semantics. We need to replicate the feeders in the new
    //  bus and overwrite the contents of the collection.

    std::transform( newBus->_ccfeeders.begin(), newBus->_ccfeeders.end(),
                    newBus->_ccfeeders.begin(),
                    [ & ]( auto feeder )
                    {
                        return feeder->replicate();
                    } ); 

    return newBus;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::restore( Cti::RowReader & rdr )
{
    std::string flag;

    rdr["CurrentVarLoadPointID"]  >> _currentvarloadpointid;

    if ( _currentvarloadpointid > 0 )
    {
        addPointId( _currentvarloadpointid );
    }

    rdr["CurrentWattLoadPointID"] >> _currentwattloadpointid;

    if ( _currentwattloadpointid > 0 )
    {
        addPointId( _currentwattloadpointid );
    }

    rdr["MapLocationID"]          >> _maplocationid;

    rdr["CurrentVoltLoadPointID"] >> _currentvoltloadpointid;

    if ( _currentvoltloadpointid > 0 )
    {
        addPointId( _currentvoltloadpointid );
    }

    rdr["AltSubID"]               >> _altDualSubId;

    rdr["SwitchPointID"]          >> _switchOverPointId;

    if ( _switchOverPointId > 0 )
    {
        addPointId( _switchOverPointId );
    }

    rdr["DualBusEnabled"]         >> flag;

    _dualBusEnable                = deserializeFlag( flag );

    rdr["MultiMonitorControl"]    >> flag;

    _multiMonitorFlag             = deserializeFlag( flag );

    rdr["usephasedata"]           >> flag;

    _usePhaseData                 = deserializeFlag( flag );

    rdr["phaseb"]                 >> _phaseBid;
    rdr["phasec"]                 >> _phaseCid;

    if ( _usePhaseData )
    {
        if ( _phaseBid > 0 )
        {
            addPointId( _phaseBid );
        }

        if ( _phaseCid > 0 )
        {
            addPointId( _phaseCid );
        }
    }

    rdr["ControlFlag"]            >> flag;

    _totalizedControlFlag         = deserializeFlag( flag );

    rdr["VoltReductionPointId"]   >> _voltReductionControlId;

    if ( _voltReductionControlId > 0 )
    {
        addPointId( _voltReductionControlId );
    }

    rdr["DisableBusPointId"]      >> _disableBusPointId;

    if ( _disableBusPointId > 0 )
    {
        addPointId( _disableBusPointId );
    }
}

void CtiCCSubstationBus::setDynamicData( Cti::RowReader & rdr )
{
    std::string flags;

    rdr["CurrentVarPointValue"]     >> _currentvarloadpointvalue;

    _altSubVarVal                   = _currentvarloadpointvalue;

    rdr["CurrentWattPointValue"]    >> _currentwattloadpointvalue;
    rdr["NextCheckTime"]            >> _nextchecktime;

    rdr["NewPointDataReceivedFlag"] >> flags;

    _newpointdatareceivedflag       = deserializeFlag( flags );

    rdr["BusUpdatedFlag"]           >> flags;

    _busupdatedflag                 = deserializeFlag( flags );

    rdr["LastCurrentVarUpdateTime"] >> _lastcurrentvarpointupdatetime;
    rdr["EstimatedVarPointValue"]   >> _estimatedvarloadpointvalue;
    rdr["CurrentDailyOperations"]   >> _currentdailyoperations;

    rdr["PeakTimeFlag"]             >> flags;

    _peaktimeflag                   = deserializeFlag( flags );

    rdr["RecentlyControlledFlag"]   >> flags;

    _recentlycontrolledflag         = deserializeFlag( flags );

    rdr["LastOperationTime"]        >> _lastoperationtime;
    rdr["VarValueBeforeControl"]    >> _varvaluebeforecontrol;
    rdr["LastFeederPAOid"]          >> _lastfeedercontrolledpaoid;
    rdr["LastFeederPosition"]       >> _lastfeedercontrolledposition;
    rdr["PowerFactorValue"]         >> _powerfactorvalue;
    rdr["KvarSolution"]             >> _kvarsolution;
    rdr["EstimatedPFValue"]         >> _estimatedpowerfactorvalue;
    rdr["CurrentVarPointQuality"]   >> _currentvarpointquality;

    rdr["WaiveControlFlag"]         >> flags;

    _waivecontrolflag               = deserializeFlag( flags );

    rdr["AdditionalFlags"]          >> flags;

    _verificationFlag                     = deserializeFlag( flags,  0 );
    _performingVerificationFlag           = deserializeFlag( flags,  1 );
    _verificationDoneFlag                 = deserializeFlag( flags,  2 );
    _overlappingSchedulesVerificationFlag = deserializeFlag( flags,  3 );
    _preOperationMonitorPointScanFlag     = deserializeFlag( flags,  4 );
    _operationSentWaitFlag                = deserializeFlag( flags,  5 );
    _postOperationMonitorPointScanFlag    = deserializeFlag( flags,  6 );
    _reEnableBusFlag                      = deserializeFlag( flags,  7 );
    _waitForReCloseDelayFlag              = deserializeFlag( flags,  8 );
    _waitToFinishRegularControlFlag       = deserializeFlag( flags,  9 );
    _maxDailyOpsHitFlag                   = deserializeFlag( flags, 10 );
    _ovUvDisabledFlag                     = deserializeFlag( flags, 11 );
    _correctionNeededNoBankAvailFlag      = deserializeFlag( flags, 12 );
    _likeDayControlFlag                   = deserializeFlag( flags, 13 );

    if ( _voltReductionControlId > 0 )
    {
        _voltReductionFlag                = deserializeFlag( flags, 14 );
    }

    _sendMoreTimeControlledCommandsFlag   = deserializeFlag( flags, 15 );
    _disableOvUvVerificationFlag          = deserializeFlag( flags, 16 );
    _primaryBusFlag                       = deserializeFlag( flags, 17 );

    rdr["CurrVerifyCBId"]           >> _currentVerificationCapBankId;
    rdr["CurrVerifyFeederId"]       >> _currentVerificationFeederId;
    rdr["CurrVerifyCBOrigState"]    >> _currentCapBankToVerifyAssumedOrigState;
        
    int verificationCode;
    rdr["VerificationStrategy"]     >> verificationCode;
    _verificationStrategy           = ConvertIntToVerificationStrategy( verificationCode );

    rdr["CbInactivityTime"]         >> _capBankToVerifyInactivityTime;
    rdr["CurrentVoltPointValue"]    >> _currentvoltloadpointvalue;

    rdr["SwitchPointStatus"]        >> flags;

    _switchOverStatus               = deserializeFlag( flags );

    rdr["AltSubControlValue"]       >> _altSubControlValue;
    rdr["EventSeq"]                 >> _eventSeq;
    rdr["CurrentWattPointQuality"]  >> _currentwattpointquality;

    _altSubWattVal                  = _currentwattloadpointvalue;

    rdr["CurrentVoltPointQuality"]  >> _currentvoltpointquality;

    _altSubVoltVal                  = _currentvoltloadpointvalue;

    rdr["iVControlTot"]             >> _iVControlTot;
    rdr["iVCount"]                  >> _iVCount;
    rdr["iWControlTot"]             >> _iWControlTot;
    rdr["iWCount"]                  >> _iWCount;

    rdr["phaseavalue"]              >> _phaseAvalue;
    rdr["phasebvalue"]              >> _phaseBvalue;
    rdr["phasecvalue"]              >> _phaseCvalue;

    rdr["LastWattPointTime"]        >> _lastWattPointTime;
    rdr["LastVoltPointTime"]        >> _lastVoltPointTime;

    rdr["PhaseAValueBeforeControl"] >> _phaseAvalueBeforeControl;
    rdr["PhaseBValueBeforeControl"] >> _phaseBvalueBeforeControl;
    rdr["PhaseCValueBeforeControl"] >> _phaseCvalueBeforeControl;

    _insertDynamicDataFlag = false;

    _dirty = false;
}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the string representation of a double
---------------------------------------------------------------------------*/
string CtiCCSubstationBus::doubleToString(double doubleVal)
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
        if (feeder->getPaoId() == feederId)
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
        case CapControlCommand::VERIFY_ALL_BANK:
        {
            text += " All";
            break;
        }
        case CapControlCommand::VERIFY_FAILED_QUESTIONABLE_BANK:
        {
            text += " Failed and Questionable";
            break;
        }
        case CapControlCommand::VERIFY_FAILED_BANK:
        {
            text += " Failed";
            break;
        }
        case CapControlCommand::VERIFY_QUESTIONABLE_BANK:
        {
            text += " Questionable";
            break;
        }
        case CapControlCommand::VERIFY_INACTIVE_BANKS:
        {
            break;
        }
        case CapControlCommand::VERIFY_SELECTED_BANK:
        {
            text += " Selected";
            break;
        }
        case CapControlCommand::VERIFY_STAND_ALONE_BANK:
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

void CtiCCSubstationBus::createCannotControlBankText(string text, string commandString, EventLogEntries &ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
    {
        Cti::StreamBuffer s;

        s << "Can Not "<<text<<" level for substation bus: " << getPaoName() << " any further.  All cap banks are already in the "<<commandString <<" state or Feeders Disabled";

        try
        {
            CtiCCCapBank* currentCapBank = NULL;
            CtiCCFeeder* currentFeeder = NULL;
            for(int i=0;i<_ccfeeders.size();i++)
            {
                currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                s << "Feeder: " << currentFeeder->getPaoName();

                Cti::FormattedList feederList;

                feederList.add("ControlDelay") << currentFeeder->getStrategy()->getControlDelayTime();
                feederList.add("DisableFlag")  << currentFeeder->getDisableFlag();

                CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)_ccfeeders[i])->getCCCapBanks();
                for(int j=0;j<ccCapBanks.size();j++)
                {
                    currentCapBank = (CtiCCCapBank*)ccCapBanks[j];

                    feederList.add("CapBank " + CtiNumStr(j + 1));

                    Cti::FormattedList capList;

                    capList.add("Name")                 << currentCapBank->getPaoName();
                    capList.add("ControlStatus")        << currentCapBank->getControlStatus();
                    capList.add("OperationalState")     << currentCapBank->getOperationalState();
                    capList.add("DisableFlag")          << currentCapBank->getDisableFlag();
                    capList.add("ControlInhibitFlag")   << currentCapBank->getControlInhibitFlag();

                    feederList << capList;
                }

                s << feederList;
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        CTILOG_DEBUG(dout, s);
    }
   if (!getCorrectionNeededNoBankAvailFlag())
    {
        setCorrectionNeededNoBankAvailFlag(true);
        string textInfo;
        textInfo = ("Sub: ");
        textInfo += getPaoName();
        textInfo += " Cannot ";
        textInfo += text;
        textInfo +=" Level.  No Cap Banks Available to " + commandString + ".";
        long stationId, areaId, spAreaId;
        store->getSubBusParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getPaoId(), 0, capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
  }


}

bool CtiCCSubstationBus::checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC)
{
    if(!_RATE_OF_CHANGE)
        return false;
    if( getUsePhaseData() && !(getTotalizedControlFlag() || getPrimaryBusFlag()))
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


long CtiCCSubstationBus::getCommsStatePointId() const
{
    return _commsStatePointId;
}

void CtiCCSubstationBus::setCommsStatePointId(long newId)
{
    _commsStatePointId = newId;
}

bool CtiCCSubstationBus::addMonitorPoint(long pointIdKey, CtiCCMonitorPointPtr monPoint)
{
    if (_monitorPoints.find(pointIdKey) == _monitorPoints.end())
    {
        _monitorPoints.insert(make_pair(pointIdKey, monPoint));
        return true;
    }

    _monitorPoints[pointIdKey] = monPoint;
    return false;
}

/*
    Inserts a new monitor point if it doesn't exist -- updates the guts of an existing one if it
        already exists.
*/
bool CtiCCSubstationBus::updateExistingMonitorPoint(CtiCCMonitorPointPtr monPoint)
{
    long pointIdKey = monPoint->getPointId();

    if (_monitorPoints.find(pointIdKey) == _monitorPoints.end())
    {
        _monitorPoints.insert(make_pair(pointIdKey, monPoint));
        return true;
    }

    _monitorPoints[pointIdKey]->updateNonDynamicData( *monPoint );
    return false;
}

void CtiCCSubstationBus::updatePointResponse(PointResponseKey key, PointResponsePtr  pResponse)
{
    PointResponsePtr pr = getPointResponse(key);
    if (pr)
    {
        _pointResponses.erase(key);
    }
    _pointResponses.insert(make_pair(key, pResponse));
}
const map <long, CtiCCMonitorPointPtr>& CtiCCSubstationBus::getAllMonitorPoints()
{
    return _monitorPoints;
}
std::vector <long> CtiCCSubstationBus::getAllMonitorPointIds()
{
    std::vector<long> pointIds;
    for each (const map<long, CtiCCMonitorPointPtr>::value_type & entry in _monitorPoints)
        pointIds.push_back(entry.first);
    return pointIds;
}

std::vector <CtiCCMonitorPointPtr> CtiCCSubstationBus::getAllCapBankMonitorPoints()
{
    std::vector<CtiCCMonitorPointPtr>   points;

    for ( auto & entry : _monitorPoints )
    {
        CtiCCFeederPtr  ignored;
        CtiCCMonitorPointPtr mPoint = entry.second;

        if ( CtiCCCapBankPtr bank = getMonitorPointParentBankAndFeeder( *mPoint, ignored ) )
        {
             points.push_back( mPoint );
        }
    }

    return points;
}

void CtiCCSubstationBus::removeAllMonitorPoints()
{
    _monitorPoints.clear();
    _pointResponses.clear();
}

CtiCCMonitorPointPtr CtiCCSubstationBus::getMonitorPoint(long pointIdKey)
{
    map <long, CtiCCMonitorPointPtr>::iterator iter = _monitorPoints.find(pointIdKey);
    return (iter == _monitorPoints.end() ? CtiCCMonitorPointPtr() : iter->second);
}

PointResponsePtr CtiCCSubstationBus::getPointResponse(PointResponseKey key)
{
    map <PointResponseKey, PointResponsePtr>::iterator iter = _pointResponses.find(key);
    return (iter == _pointResponses.end() ? PointResponsePtr() : iter->second);
}

void CtiCCSubstationBus::addDefaultPointResponses( std::set< std::pair<long, int> > &requiredPointResponses)
{
    for each (CtiCCMonitorPointPtr mPoint in getAllCapBankMonitorPoints() )
    {
        long mPointId = mPoint->getPointId();
        long bankId = mPoint->getDeviceId();

        for each(const map<long, CtiCCMonitorPointPtr>::value_type & response in _monitorPoints )
        {
            int responsePointId = response.second->getPointId();
            PointResponseKey prKey = PointResponseKey(bankId, responsePointId);
            PointResponsePtr pResponse = getPointResponse(prKey);
            if (pResponse == NULL)
            {
                pResponse = boost::shared_ptr<PointResponse>(new PointResponse(responsePointId, bankId, 0, _IVVC_DEFAULT_DELTA, false, getPaoId()));
                _pointResponses.insert(make_pair(prKey, pResponse));
                requiredPointResponses.insert(make_pair(responsePointId, bankId));
            }
        }
    }
}

std::vector<Cti::CapControl::PointResponse> CtiCCSubstationBus::getPointResponsesForDevice(const long deviceId)
{
    std::vector<Cti::CapControl::PointResponse>    results;

    for each ( std::map<PointResponseKey, PointResponsePtr>::value_type value in _pointResponses )
    {
        if ( value.first.deviceId == deviceId )
        {
            results.push_back( *(value.second) );
        }
    }

    return results;
}

