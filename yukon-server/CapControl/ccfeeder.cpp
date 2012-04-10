#include "precompiled.h"

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccid.h"
#include "cccapbank.h"
#include "ccfeeder.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "ccmonitorpoint.h"
#include "mgr_holiday.h"
#include "resolvers.h"
#include "numstr.h"
#include "utility.h"
#include "msg_lmcontrolhistory.h"
#include "tbl_pt_alarm.h"
#include "ccutil.h"
#include "ccsubstationbus.h"
#include "database_reader.h"
#include "database_writer.h"
#include "Exceptions.h"
#include "PointResponse.h"
#include "PointResponseManager.h"

using Cti::CapControl::PointResponse;
using Cti::CapControl::PointResponseManager;
using Cti::CapControl::createPorterRequestMsg;
using std::endl;

extern unsigned long _CC_DEBUG;
extern bool _IGNORE_NOT_NORMAL_FLAG;
extern unsigned long _SEND_TRIES;
extern bool _USE_FLIP_FLAG;
extern unsigned long _POINT_AGE;
extern unsigned long _SCAN_WAIT_EXPIRE;
extern bool _RETRY_FAILED_BANKS;
extern unsigned long _MAX_KVAR;
extern unsigned long _MAX_KVAR_TIMEOUT;
extern bool _LOG_MAPID_INFO;
extern unsigned long _LIKEDAY_OVERRIDE_TIMEOUT;
extern bool _RATE_OF_CHANGE;
extern unsigned long _RATE_OF_CHANGE_DEPTH;
extern long _MAXOPS_ALARM_CATID;
extern bool _RETRY_ADJUST_LAST_OP_TIME;
extern unsigned long _REFUSAL_TIMEOUT;
extern bool _USE_PHASE_INDICATORS;

RWDEFINE_COLLECTABLE( CtiCCFeeder, CTICCFEEDER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCFeeder::CtiCCFeeder()
    : Controllable(0),
      _parentId(0),
      _multiMonitorFlag(false),
      _currentvarloadpointid(0),
      _currentvarloadpointvalue(0),
      _currentwattloadpointid(0),
      _currentwattloadpointvalue(0),
      _currentvoltloadpointid(0),
      _currentvoltloadpointvalue(0),
      _displayorder(0),
      _newpointdatareceivedflag(false),
      _estimatedvarloadpointid(0),
      _estimatedvarloadpointvalue(0),
      _dailyoperationsanalogpointid(0),
      _powerfactorpointid(0),
      _estimatedpowerfactorpointid(0),
      _currentdailyoperations(0),
      _recentlycontrolledflag(false),
      _varvaluebeforecontrol(0),
      _lastcapbankcontrolleddeviceid(0),
      _busoptimizedvarcategory(0),
      _busoptimizedvaroffset(0),
      _powerfactorvalue(0),
      _kvarsolution(0),
      _estimatedpowerfactorvalue(0),
      _currentvarpointquality(0),
      _currentwattpointquality(0),
      _currentvoltpointquality(0),
      _waivecontrolflag(false),
      _decimalPlaces(0),
      _peakTimeFlag(false),
      _verificationFlag(false),
      _performingVerificationFlag(false),
      _verificationDoneFlag(false),
      _preOperationMonitorPointScanFlag(false),
      _operationSentWaitFlag(false),
      _postOperationMonitorPointScanFlag(false),
      _waitForReCloseDelayFlag(false),
      _maxDailyOpsHitFlag(false),
      _ovUvDisabledFlag(false),
      _correctionNeededNoBankAvailFlag(false),
      _lastVerificationMsgSentSuccessful(false),
      _eventSeq(0),
      _currentVerificationCapBankId(0),
      _currentCapBankToVerifyAssumedOrigState(0),
      _targetvarvalue(0),
      _iVControlTot(0),
      _iVCount(0),
      _iWControlTot(0),
      _iWCount(0),
      _iVControl(0),
      _iWControl(0),
      _usePhaseData(0),
      _phaseBid(0),
      _phaseCid(0),
      _totalizedControlFlag(false),
      _phaseAvalue(0),
      _phaseBvalue(0),
      _phaseCvalue(0),
      _phaseAvalueBeforeControl(0),
      _phaseBvalueBeforeControl(0),
      _phaseCvalueBeforeControl(0),
      _retryIndex(0),
      _insertDynamicDataFlag(true),
      _dirty(true)
{
    _porterRetFailFlag = false;
    regression = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionA = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionB = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionC = CtiRegression(_RATE_OF_CHANGE_DEPTH);
}

CtiCCFeeder::CtiCCFeeder(StrategyManager * strategyManager)
    : Controllable(strategyManager),
      _parentId(0),
      _multiMonitorFlag(false),
      _currentvarloadpointid(0),
      _currentvarloadpointvalue(0),
      _currentwattloadpointid(0),
      _currentwattloadpointvalue(0),
      _currentvoltloadpointid(0),
      _currentvoltloadpointvalue(0),
      _displayorder(0),
      _newpointdatareceivedflag(false),
      _estimatedvarloadpointid(0),
      _estimatedvarloadpointvalue(0),
      _dailyoperationsanalogpointid(0),
      _powerfactorpointid(0),
      _estimatedpowerfactorpointid(0),
      _currentdailyoperations(0),
      _recentlycontrolledflag(false),
      _varvaluebeforecontrol(0),
      _lastcapbankcontrolleddeviceid(0),
      _busoptimizedvarcategory(0),
      _busoptimizedvaroffset(0),
      _powerfactorvalue(0),
      _kvarsolution(0),
      _estimatedpowerfactorvalue(0),
      _currentvarpointquality(0),
      _currentwattpointquality(0),
      _currentvoltpointquality(0),
      _waivecontrolflag(false),
      _decimalPlaces(0),
      _peakTimeFlag(false),
      _verificationFlag(false),
      _performingVerificationFlag(false),
      _verificationDoneFlag(false),
      _preOperationMonitorPointScanFlag(false),
      _operationSentWaitFlag(false),
      _postOperationMonitorPointScanFlag(false),
      _waitForReCloseDelayFlag(false),
      _maxDailyOpsHitFlag(false),
      _ovUvDisabledFlag(false),
      _correctionNeededNoBankAvailFlag(false),
      _lastVerificationMsgSentSuccessful(false),
      _eventSeq(0),
      _currentVerificationCapBankId(0),
      _currentCapBankToVerifyAssumedOrigState(0),
      _targetvarvalue(0),
      _iVControlTot(0),
      _iVCount(0),
      _iWControlTot(0),
      _iWCount(0),
      _iVControl(0),
      _iWControl(0),
      _usePhaseData(0),
      _phaseBid(0),
      _phaseCid(0),
      _totalizedControlFlag(false),
      _phaseAvalue(0),
      _phaseBvalue(0),
      _phaseCvalue(0),
      _phaseAvalueBeforeControl(0),
      _phaseBvalueBeforeControl(0),
      _phaseCvalueBeforeControl(0),
      _retryIndex(0),
      _insertDynamicDataFlag(true),
      _dirty(true)
{
    _porterRetFailFlag = false;
    regression = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionA = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionB = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionC = CtiRegression(_RATE_OF_CHANGE_DEPTH);
}

CtiCCFeeder::CtiCCFeeder(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : Controllable(rdr, strategyManager)
{
    restore(rdr);

    _operationStats.setPAOId(getPaoId());
    _confirmationStats.setPAOId(getPaoId());
    _originalParent.setPAOId(getPaoId());
    regression = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionA = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionB = CtiRegression(_RATE_OF_CHANGE_DEPTH);
    regressionC = CtiRegression(_RATE_OF_CHANGE_DEPTH);
}

CtiCCFeeder::CtiCCFeeder(const CtiCCFeeder& feeder)
    : Controllable(feeder)
{
    operator=(feeder);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCFeeder::~CtiCCFeeder()
{
    try
    {
        delete_container(_cccapbanks);
        _cccapbanks.clear();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

CtiCCOperationStats& CtiCCFeeder::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats& CtiCCFeeder::getConfirmationStats()
{
    return _confirmationStats;
}

CtiCCOriginalParent& CtiCCFeeder::getOriginalParent()
{
    return _originalParent;
}

/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (subBusId) of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getParentId() const
{
    return _parentId;
}


/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentVarLoadPointId() const
{
    return _currentvarloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getCurrentVarLoadPointValue() const
{
    return _currentvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentWattLoadPointId() const
{
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getCurrentWattLoadPointValue() const
{
    return _currentwattloadpointvalue;
}
/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointId

    Returns the current volt load point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentVoltLoadPointId() const
{
    return _currentvoltloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointValue

    Returns the current volt load point value of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getCurrentVoltLoadPointValue() const
{
    return _currentvoltloadpointvalue;
}


/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getMapLocationId() const
{
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the display order of the feeder
---------------------------------------------------------------------------*/
float CtiCCFeeder::getDisplayOrder() const
{
    return _displayorder;
}



/*---------------------------------------------------------------------------
    getIVControlTot

    Returns the Integrate Volt/Var Control total of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getIVControlTot() const
{
    return _iVControlTot;
}
/*---------------------------------------------------------------------------
    getIVCount

    Returns the Integrate Volt/Var Control count of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getIVCount() const
{
    return _iVCount;
}


/*---------------------------------------------------------------------------
    getIWControlTot

    Returns the Integrate Watt Control total of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getIWControlTot() const
{
    return _iWControlTot;
}

/*---------------------------------------------------------------------------
    getIWCount

    Returns the Integrate Watt Control count of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getIWCount() const
{
    return _iWCount;
}

/*---------------------------------------------------------------------------
    getIVControl

    Returns the Integrate Volt/Var Control of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getIVControl() const
{
    return _iVControl;
}
/*---------------------------------------------------------------------------
    getIWControl

    Returns the Integrate Watt Control of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getIWControl() const
{
    return _iWControl;
}
/*---------------------------------------------------------------------------
    get UsePhaseData flag

    Returns the usePhaseData flag of the feeder
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getUsePhaseData() const
{
    return _usePhaseData;
}

/*---------------------------------------------------------------------------
    getPhaseBid

    Returns the getPhaseB pointid of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getPhaseBId() const
{
    return _phaseBid;
}
/*---------------------------------------------------------------------------
    getPhaseCid

    Returns the getPhaseC pointid of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getPhaseCId() const
{
    return _phaseCid;
}

/*---------------------------------------------------------------------------
    getTotalizedControlFlag

    Returns the getPhaseC pointid of the feeder
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getTotalizedControlFlag() const
{
    return _totalizedControlFlag;
}


/*---------------------------------------------------------------------------
    getPhaseAValue

    Returns the PhaseAValue VAr of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPhaseAValue() const
{
    return _phaseAvalue;
}
/*---------------------------------------------------------------------------
    getPhaseBValue

    Returns the PhaseBValue VAr of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPhaseBValue() const
{
    return _phaseBvalue;
}
/*---------------------------------------------------------------------------
    getPhaseCValue

    Returns the PhaseCValue VAr of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPhaseCValue() const
{
    return _phaseCvalue;
}
/*---------------------------------------------------------------------------
    getPhaseAValue

    Returns the PhaseAValue VAr of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPhaseAValueBeforeControl() const
{
    return _phaseAvalueBeforeControl;
}
/*---------------------------------------------------------------------------
    getPhaseBValue

    Returns the PhaseBValue VAr of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPhaseBValueBeforeControl() const
{
    return _phaseBvalueBeforeControl;
}
/*---------------------------------------------------------------------------
    getPhaseCValue

    Returns the PhaseCValue VAr of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPhaseCValueBeforeControl() const
{
    return _phaseCvalueBeforeControl;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the feeder
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getNewPointDataReceivedFlag() const
{
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getLastCurrentVarPointUpdateTime

    Returns the last current var point update time of the feeder
---------------------------------------------------------------------------*/
const CtiTime& CtiCCFeeder::getLastCurrentVarPointUpdateTime() const
{

    return _lastcurrentvarpointupdatetime;
}

/*---------------------------------------------------------------------------
    getLastWattPointTime

    Returns the last current watt point update time of the feeder
---------------------------------------------------------------------------*/
const CtiTime& CtiCCFeeder::getLastWattPointTime() const
{
    return _lastWattPointTime;
}

/*---------------------------------------------------------------------------
    getLastVoltPointTime

    Returns the last current var point update time of the feeder
---------------------------------------------------------------------------*/
const CtiTime& CtiCCFeeder::getLastVoltPointTime() const
{
    return _lastVoltPointTime;
}

long CtiCCFeeder::getRetryIndex() const
{
    return _retryIndex;
}


/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointId

    Returns the estimated var load point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getEstimatedVarLoadPointId() const
{
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getEstimatedVarLoadPointValue() const
{
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getDailyOperationsAnalogPointId() const
{
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getPowerFactorPointId

    Returns the power factor point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getPowerFactorPointId() const
{
    return _powerfactorpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorPointId

    Returns the estimated power factor point id of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getEstimatedPowerFactorPointId() const
{
    return _estimatedpowerfactorpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the feeder has been recently controlled
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getRecentlyControlledFlag() const
{
    return _recentlycontrolledflag;
}

/*---------------------------------------------------------------------------
    getLastOperationTime

    Returns the last operation time of the feeder
---------------------------------------------------------------------------*/
const CtiTime& CtiCCFeeder::getLastOperationTime() const
{
    return _lastoperationtime;
}

/*---------------------------------------------------------------------------
    getVarValueBeforeControl

    Returns the var value before control of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getVarValueBeforeControl() const
{
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastCapBankControlledDeviceId

    Returns the device id of the last cap bank controlled of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getLastCapBankControlledDeviceId() const
{
    return _lastcapbankcontrolleddeviceid;
}

CtiCCCapBankPtr CtiCCFeeder::getLastCapBankControlledDevice()
{
    CtiCCCapBankPtr bank = CtiCCSubstationBusStore::getInstance()->findCapBankByPAObjectID(_lastcapbankcontrolleddeviceid);

    return bank;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarCategory

    Returns the bus optimized var category of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getBusOptimizedVarCategory() const
{
    return _busoptimizedvarcategory;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarOffset

    Returns the bus optimized var offset of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getBusOptimizedVarOffset() const
{
    return _busoptimizedvaroffset;
}

/*---------------------------------------------------------------------------
    getPowerFactorValue

    Returns the power factor value of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getPowerFactorValue() const
{
    return _powerfactorvalue;
}

/*---------------------------------------------------------------------------
    getKVARSolution

    Returns the kvar solution of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getKVARSolution() const
{
    return _kvarsolution;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorValue

    Returns the estimated power factor value of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getEstimatedPowerFactorValue() const
{
    return _estimatedpowerfactorvalue;
}

/*---------------------------------------------------------------------------
    getCurrentVarPointQuality

    Returns the CurrentVarPointQuality of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentVarPointQuality() const
{
    return _currentvarpointquality;
}

/*---------------------------------------------------------------------------
    getCurrentWattPointQuality

    Returns the CurrentWattPointQuality of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentWattPointQuality() const
{
    return _currentwattpointquality;
}

/*---------------------------------------------------------------------------
    getCurrentVoltPointQuality

    Returns the CurrentVoltPointQuality of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getCurrentVoltPointQuality() const
{
    return _currentvoltpointquality;
}

/*---------------------------------------------------------------------------
    getWaiveControlFlag

    Returns the WaiveControlFlag of the feeder
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getWaiveControlFlag() const
{
    return _waivecontrolflag;
}

/*---------------------------------------------------------------------------
    getParentControlUnits

    Returns the ParentControlUnits of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getParentControlUnits() const
{
    return _parentControlUnits;
}

/*---------------------------------------------------------------------------
    getParentName

    Returns the ParentName of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getParentName() const
{
    return _parentName;
}


/*---------------------------------------------------------------------------
    getDecimalPlaces

    Returns the DecimalPlaces of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getDecimalPlaces() const
{
    return _decimalPlaces;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the PeakTimeFlag of the feeder
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getPeakTimeFlag() const
{
    return _peakTimeFlag;
}

bool CtiCCFeeder::getPorterRetFailFlag() const
{
    return _porterRetFailFlag;
}

long CtiCCFeeder::getEventSequence() const
{
    return _eventSeq;
}


bool CtiCCFeeder::getMultiMonitorFlag() const
{
    return _multiMonitorFlag;
}

/*---------------------------------------------------------------------------
    getCCCapBanks

    Returns the list of cap banks in the feeder
---------------------------------------------------------------------------*/
CtiCCCapBank_SVector& CtiCCFeeder::getCCCapBanks()
{
    return _cccapbanks;
}

std::list<int> CtiCCFeeder::getAllCapBankIds()
{
    std::list<int> ids;
    for each (const CtiCCCapBankPtr c in _cccapbanks)
    {
        ids.push_back(c->getPaoId());
    }
    return ids;
}

std::vector<CtiCCCapBankPtr> CtiCCFeeder::getAllSwitchedCapBanks( )
{
    std::vector<CtiCCCapBankPtr> banks;
    for each (const CtiCCCapBankPtr c in _cccapbanks)
    {
        if( ciStringEqual(c->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) )
        {
            banks.push_back(c);
        }
    }
    return banks;
}

std::vector<CtiCCCapBankPtr> CtiCCFeeder::getAllCapBanks( )
{
    std::vector<CtiCCCapBankPtr> banks;
    banks.assign(_cccapbanks.begin(), _cccapbanks.end());
    return banks;
}


const CtiRegression& CtiCCFeeder::getRegression()
{
    return regression;
}
const CtiRegression& CtiCCFeeder::getRegressionA()
{
    return regressionA;
}
const CtiRegression& CtiCCFeeder::getRegressionB()
{
    return regressionB;
}
const CtiRegression& CtiCCFeeder::getRegressionC()
{
    return regressionC;
}

/*---------------------------------------------------------------------------
    setParentId

    Sets the parentID (subBusID) of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setParentId(long parentId)
{
    _parentId = parentId;
    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarLoadPointId(long currentvarid)
{
    _currentvarloadpointid = currentvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarLoadPointValue(double currentvarval, CtiTime timestamp)
{
    if( _currentvarloadpointvalue != currentvarval )
    {
        _dirty = true;
    }
    _currentvarloadpointvalue = currentvarval;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() ){
        regression.appendWithoutFill(std::make_pair((double)timestamp.seconds(),currentvarval));
        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " RATE OF CHANGE: Adding to regression  " << timestamp.seconds() << "  and " << currentvarval << endl;
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattLoadPointId(long currentwattid)
{
    _currentwattloadpointid = currentwattid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattLoadPointValue(double currentwattval)
{
    if( _currentwattloadpointvalue != currentwattval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentwattloadpointvalue = currentwattval;
    return *this;
}
/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointId

    Sets the current volt load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVoltLoadPointId(long currentvoltid)
{
    _currentvoltloadpointid = currentvoltid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVoltLoadPointValue(double currentvoltval)
{
    if( _currentvoltloadpointvalue != currentvoltval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentvoltloadpointvalue = currentvoltval;
    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setMapLocationId(const string& maplocation)
{
    _maplocationid = maplocation;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the display order of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDisplayOrder(float order)
{
    _displayorder = order;
    return *this;
}


/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setNewPointDataReceivedFlag(bool newpointdatareceived)
{
    if( _newpointdatareceivedflag != newpointdatareceived )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _newpointdatareceivedflag = newpointdatareceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate)
{
    if( _lastcurrentvarpointupdatetime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _lastcurrentvarpointupdatetime = lastpointupdate;
    return *this;
}
/*---------------------------------------------------------------------------
    setLastWattPointTime

    Sets the last current Watt point update time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastWattPointTime(const CtiTime& lastpointupdate)
{
    if( _lastWattPointTime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _lastWattPointTime = lastpointupdate;
    return *this;
}
/*---------------------------------------------------------------------------
    setLastVoltPointTime

    Sets the last current Volt point update time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastVoltPointTime(const CtiTime& lastpointupdate)
{
    if( _lastVoltPointTime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _lastVoltPointTime = lastpointupdate;
    return *this;
}


/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedVarLoadPointId(long estimatedvarid)
{
    _estimatedvarloadpointid = estimatedvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedVarLoadPointValue(double estimatedvarval)
{
    if( _estimatedvarloadpointvalue != estimatedvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _estimatedvarloadpointvalue = estimatedvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setDailyOperationsAnalogPointId

    Sets the daily operations analog point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDailyOperationsAnalogPointId(long opspointid)
{
    _dailyoperationsanalogpointid = opspointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorPointId

    Sets the power factor point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPowerFactorPointId(long pfpointid)
{
    _powerfactorpointid = pfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorPointId

    Sets the estimated power factor point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedPowerFactorPointId(long epfpointid)
{
    _estimatedpowerfactorpointid = epfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentDailyOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges)
{
    if( _currentdailyoperations != operations )
    {
        if( getDailyOperationsAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(getDailyOperationsAnalogPointId(),operations,NormalQuality,AnalogPointType));
        }
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentdailyoperations = operations;

    return *this;
}
/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentDailyOperations(long operations)
{
    if( _currentdailyoperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentdailyoperations = operations;

    return *this;
}


/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setRecentlyControlledFlag(bool recentlycontrolled)
{
    if( _recentlycontrolledflag != recentlycontrolled )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _recentlycontrolledflag = recentlycontrolled;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastOperationTime

    Sets the last operation time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastOperationTime(const CtiTime& lastoperation)
{
    if( _lastoperationtime != lastoperation )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _lastoperationtime = lastoperation;
    return *this;
}

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setVarValueBeforeControl(double oldvarval)
{
    if( _varvaluebeforecontrol != oldvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _varvaluebeforecontrol = oldvarval;

    setPhaseAValueBeforeControl(getPhaseAValue());
    setPhaseBValueBeforeControl(getPhaseBValue());
    setPhaseCValueBeforeControl(getPhaseCValue());

    return *this;
}

/*---------------------------------------------------------------------------
    setLastCapBankControlledDeviceId

    Sets the device id of the last cap bank controlled in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastCapBankControlledDeviceId(long lastcapbank)
{
    if( _lastcapbankcontrolleddeviceid != lastcapbank )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _lastcapbankcontrolleddeviceid = lastcapbank;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorValue

    Sets the PowerFactorValue in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPowerFactorValue(double pfval)
{
    if( _powerfactorvalue != pfval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _powerfactorvalue = pfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setKVARSolution

    Sets the KVARSolution in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setKVARSolution(double solution)
{
    if( _kvarsolution != solution )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _kvarsolution = solution;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorValue

    Sets the EstimatedPowerFactorValue in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedPowerFactorValue(double epfval)
{
    if( _estimatedpowerfactorvalue != epfval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _estimatedpowerfactorvalue = epfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarPointQuality

    Sets the CurrentVarPointQuality in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarPointQuality(long cvpq)
{
    if( _currentvarpointquality != cvpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentvarpointquality = cvpq;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattPointQuality

    Sets the CurrentWattPointQuality in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattPointQuality(long cwpq)
{
    if( _currentwattpointquality != cwpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentwattpointquality = cwpq;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVoltPointQuality

    Sets the CurrentVoltPointQuality in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVoltPointQuality(long cvpq)
{
    if( _currentvoltpointquality != cvpq )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentvoltpointquality = cvpq;
    return *this;
}

/*---------------------------------------------------------------------------
    setWaiveControlFlag

    Sets the WaiveControlFlag in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setWaiveControlFlag(bool waive)
{
    if( _waivecontrolflag != waive )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _waivecontrolflag = waive;
    return *this;
}

/*---------------------------------------------------------------------------
    setParentControlUnits

    Sets the ParentControlUnits in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setParentControlUnits(const string& parentControlUnits)
{
    if (_parentControlUnits != parentControlUnits)
    {
        _dirty = true;
    }
    _parentControlUnits = parentControlUnits;
    return *this;
}

/*---------------------------------------------------------------------------
    setParentName

    Sets the ParentName in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setParentName(const string& parentName)
{
    if (_parentName != parentName)
    {
        _dirty = true;
    }
    _parentName = parentName;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentDecimalPlaces

    Sets the ParentDecimalPlaces in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDecimalPlaces(long decimalPlaces)
{
    if (_decimalPlaces != decimalPlaces)
    {
        _dirty = true;
    }
    _decimalPlaces = decimalPlaces;
    return *this;
}

/*---------------------------------------------------------------------------
    setParentPeakTimeFlag

    Sets the ParentPeakTimeFlag in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakTimeFlag(bool peakTimeFlag)
{
    if (_peakTimeFlag != peakTimeFlag)
    {
        _dirty = true;
    }
    _peakTimeFlag = peakTimeFlag;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setPorterRetFailFlag(bool flag)
{
    if (_porterRetFailFlag != flag)
    {
        _dirty = true;
    }
    _porterRetFailFlag = flag;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setEventSequence(long eventSeq)
{
    if (_eventSeq != eventSeq)
    {
        _dirty = true;
    }
    _eventSeq = eventSeq;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setMultiMonitorFlag(bool flag)
{
    if (_multiMonitorFlag != flag)
    {
        _dirty = true;
    }
    _multiMonitorFlag = flag;
    return *this;
}


CtiCCCapBank* CtiCCFeeder::findCapBankToChangeVars(double kvarSolution,  CtiMultiMsg_vec& pointChanges, double leadLevel, double lagLevel, double currentVarValue,
                                                   bool checkLimits)
{
    CtiCCCapBankPtr returnCapBank = NULL;
    CtiTime currentTime = CtiTime();
    BankOperation solution;
    bool endDayFlag = false;
    std::vector<CtiCCCapBankPtr> banks;

    if (kvarSolution == 0.0)
    {
        return NULL;
    }
    else if (kvarSolution < 0.0)
    {
        solution = Close;
        // Sort according to CloseOrder.
        CtiCCCapBank_SCloseVector closeCaps;
        for (int i = 0; i < _cccapbanks.size(); i++)
        {
            closeCaps.insert(_cccapbanks[i]);
        }
        banks = closeCaps.get_container();
    }
    else
    {
        solution = Open;
        // Sort according to TripOrder..
        CtiCCCapBank_STripVector tripCaps;
        for (int i = 0; i < _cccapbanks.size(); i++)
        {
            tripCaps.insert(_cccapbanks[i]);
        }
        banks = tripCaps.get_container();
    }

    for (int i = 0; i < banks.size(); i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)banks[i];

        if (currentCapBank->getIgnoreFlag() &&
            currentTime >= CtiTime(currentCapBank->getLastStatusChangeTime().seconds() + (_REFUSAL_TIMEOUT * 60)))
        {
            currentCapBank->setIgnoreFlag(false);
        }

        long controlStatus = currentCapBank->getControlStatus();
        bool correctControlStatus;

        if (solution == Close)
        {
            correctControlStatus = (controlStatus == CtiCCCapBank::Open || controlStatus == CtiCCCapBank::OpenQuestionable || controlStatus == CtiCCCapBank::OpenPending);
        }
        else
        {
            correctControlStatus = (controlStatus == CtiCCCapBank::Close || controlStatus == CtiCCCapBank::CloseQuestionable || controlStatus == CtiCCCapBank::ClosePending);
        }

        if (!currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
            ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
            correctControlStatus && !currentCapBank->getIgnoreFlag())
        {
            //Before anything, make sure this bank will not push past a threshold.
            if (checkLimits && !((leadLevel == 0) && (lagLevel == 0) && (currentVarValue == 0)))
            {
                int bankSize = currentCapBank->getBankSize();
                if (solution == Close)
                {
                    int newValue = currentVarValue - bankSize;
                    if ((newValue <= leadLevel))
                    {
                        //This would cause another operation, so try a different bank.
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " CapBank skipped because it was too large for the lead/lag range. Name: " << currentCapBank->getPaoName() << " Id: " << currentCapBank->getPaoId() << " size: " << currentCapBank->getBankSize() << endl;
                        }
                        break;
                    }
                }
                else
                {
                    int newValue = currentVarValue + bankSize;
                    if ((newValue >= lagLevel))
                    {
                        //This would cause another operation, so try a different bank.
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " CapBank skipped because it was too large for the lead/lag range. Name: " << currentCapBank->getPaoName() << " Id: " << currentCapBank->getPaoId() << " size: " << currentCapBank->getBankSize() << endl;
                        }
                        break;
                    }
                }
            }

            // Check max daily ops.
            if (currentCapBank->getMaxDailyOps() > 0 &&
                !currentCapBank->getMaxDailyOpsHitFlag() &&
                currentCapBank->getCurrentDailyOperations() >= currentCapBank->getMaxDailyOps())
            {
                currentCapBank->setMaxDailyOpsHitFlag(true);
                string text = string("CapBank Exceeded Max Daily Operations");
                string additional = string("CapBank: ");
                additional += getPaoName();
                if (_LOG_MAPID_INFO)
                {
                    additional += " MapID: ";
                    additional += currentCapBank->getMapLocationId();
                    additional += " (";
                    additional += currentCapBank->getPaoDescription();
                    additional += ")";
                }

                if (currentCapBank->getOperationAnalogPointId() > 0)
                {
                    CtiSignalMsg* pSig = new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                        TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, currentCapBank->getCurrentDailyOperations() );
                    pSig->setCondition(CtiTablePointAlarming::highReasonability);
                    pointChanges.push_back(pSig);
                }

                // We should disable bank if the flag says so
                if (currentCapBank->getMaxOpsDisableFlag())
                {
                    CtiCCSubstationBusStore::getInstance()->UpdatePaoDisableFlagInDB(currentCapBank, true);

                    text = string("CapBank Disabled");
                    additional = string("CapBank: ");
                    additional += getPaoName();
                    if (_LOG_MAPID_INFO)
                    {
                        additional += " MapID: ";
                        additional += currentCapBank->getMapLocationId();
                        additional += " (";
                        additional += currentCapBank->getPaoDescription();
                        additional += ")";
                    }
                    if (currentCapBank->getOperationAnalogPointId() > 0)
                    {
                        CtiSignalMsg* pSig = new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                              TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, currentCapBank->getCurrentDailyOperations() );
                        pSig->setCondition(CtiTablePointAlarming::highReasonability);
                        pointChanges.push_back(pSig);
                    }

                    if (!getStrategy()->getEndDaySettings().compare("Trip") && (solution == Open))
                    {
                        // We need this to return this bank (since we disabled it).
                        endDayFlag = true;
                    }
                    else if (!getStrategy()->getEndDaySettings().compare("Close") && (solution == Close))
                    {
                        // We need this to return this bank (since we disabled it).
                        endDayFlag = true;
                    }
                    else
                    {
                        // Skip this the break statement, lets try the next bank since we disabled this one.
                        continue;
                    }
                }
            }

            if (!currentCapBank->getDisableFlag() || endDayFlag)
            {
                returnCapBank = currentCapBank;
            }

            break;
        }
    }

    if (returnCapBank == NULL && _RETRY_FAILED_BANKS)
    {
        for (int i = 0; i < banks.size(); i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)banks[i];

            if (!currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                ciStringEqual(currentCapBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail ||
                 currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail))
            {
                if( solution == Close && !currentCapBank->getRetryCloseFailedFlag() )
                {
                    currentCapBank->setRetryCloseFailedFlag(true);
                    return currentCapBank;
                }
                if( solution == Open && !currentCapBank->getRetryOpenFailedFlag() )
                {
                    currentCapBank->setRetryOpenFailedFlag(true);
                    return currentCapBank;
                }
            }
        }
    }

    return returnCapBank;
}

bool CtiCCFeeder::checkForMaxKvar( long bankId, long bankSize )
{
    //check to make sure we will not over run the max kvar cparm.
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    return store->addKVAROperation(bankId, bankSize);
}

bool CtiCCFeeder::removeMaxKvar( long bankId )
{
    //check to make sure we will not over run the max kvar cparm.
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    return store->removeKVAROperation(bankId);
}

/*---------------------------------------------------------------------------
    createIncreaseVarRequest

    Creates a CtiRequestMsg to open the next cap bank to increase the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createIncreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                     string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank == NULL )
    {
        return reqMsg;
    }

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". "  << endl;
        return reqMsg;
    }

    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::OpenPending);

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(_currentdailyoperations+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
    setRecentlyControlledFlag(true);

    setVarValueBeforeControl(kvarBefore);

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");


    if( capBank->getStatusPointId() > 0 )
    {
        string additional;
        additional = ("Sub: ");
        additional += getParentName();
        additional += " /Feeder: ";
        additional += getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPaoDescription();
            additional += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPaoName()));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();
        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", kvarBefore, kvarBefore, 0,
                                                capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" << endl;
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
    }

    if  (capBank->getTwoWayPoints() != NULL)
    {
        if (capBank->getPointIdByAttribute(PointAttribute::CapacitorBankState) > 0)
        {
            CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(),
                                                                               capBank->getPointIdByAttribute(PointAttribute::CapacitorBankState),
                                                                               capBank->getControlStatus(),
                                                                               CtiTime(), -1, 100 );
            hist->setMessagePriority( hist->getMessagePriority() + 2 );
            pointChanges.push_back( hist );
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);


        }
    }

    reqMsg = createPorterRequestMsg(capBank->getControlDeviceId(),"control open");
    reqMsg->setSOE(4);

    return reqMsg;
}

CtiRequestMsg* CtiCCFeeder::createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                                 string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue )
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank == NULL )
        return reqMsg;

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". "  << endl;
        return reqMsg;
    }

    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ***VERIFICATION INFO***  CBid: "<<capBank->getPaoId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Open Sent Now " << endl;
    }
    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::OpenPending);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(_currentdailyoperations+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
    //setRecentlyControlledFlag(true);
    setVarValueBeforeControl(getCurrentVarLoadPointValue());

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional("Sub: ");
        additional += getParentName();
        additional += " /Feeder: ";
        additional += getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPaoDescription();
            additional += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control verification"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control verification", kvarBefore, kvarBefore, 0,
                                                capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" << endl;
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control verification"));
    }

    if  (stringContainsIgnoreCase(capBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
    {
        reqMsg = createPorterRequestMsg(capBank->getControlDeviceId(),"control flip");
    }
    else
    {
        reqMsg = createPorterRequestMsg(capBank->getControlDeviceId(),"control open");
    }
    reqMsg->setSOE(4);

    return reqMsg;
}

CtiRequestMsg* CtiCCFeeder::createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                                 string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue )
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank == NULL )
        return reqMsg;

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". "  << endl;
        return reqMsg;
    }

    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ***VERIFICATION INFO***  CBid: "<<capBank->getPaoId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Close Sent Now " << endl;
    }
    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::ClosePending);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(_currentdailyoperations+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);

    setVarValueBeforeControl(getCurrentVarLoadPointValue());

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional("Sub: ");
        additional += getParentName();
        additional += " /Feeder: ";
        additional += getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPaoDescription();
            additional += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control verification"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control verification", kvarBefore, kvarBefore, 0,
                                                capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" << endl;
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control verification"));
    }

    if  (stringContainsIgnoreCase(capBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
    {
        reqMsg = createPorterRequestMsg(capBank->getControlDeviceId(),"control flip");
    }
    else
    {
        reqMsg = createPorterRequestMsg(capBank->getControlDeviceId(),"control close");
    }
    reqMsg->setSOE(4);

    return reqMsg;
}


/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/


CtiRequestMsg* CtiCCFeeder::createDecreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                     string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank == NULL )
    {
        return reqMsg;
    }

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". "  << endl;
        return reqMsg;
    }

    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::ClosePending);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(_currentdailyoperations+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
    setRecentlyControlledFlag(true);
    //setVarValueBeforeControl(getCurrentVarLoadPointValue());
    setVarValueBeforeControl(kvarBefore);

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional;
        additional = ("Sub: ");
        additional += getParentName();
        additional += " /Feeder: ";
        additional += getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPaoDescription();
            additional += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPaoName()));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", kvarBefore, kvarBefore, 0,
                                                capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" << endl;
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
    }

    if  (capBank->getTwoWayPoints() != NULL)
    {
        if (capBank->getPointIdByAttribute(PointAttribute::CapacitorBankState) > 0)
        {
            CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(),
                                                                               capBank->getPointIdByAttribute(PointAttribute::CapacitorBankState),
                                                                               capBank->getControlStatus(),
                                                                               CtiTime(), -1, 100 );
            hist->setMessagePriority( hist->getMessagePriority() + 2 );
            pointChanges.push_back( hist );
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);


        }
    }

    reqMsg = createPorterRequestMsg( capBank->getControlDeviceId(),"control close" );
    reqMsg->setSOE(4);

    return reqMsg;
}

/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createForcedVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, int action, string typeOfControl)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiRequestMsg* reqMsg = NULL;
    if( capBank == NULL )
        return reqMsg;

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". "  << endl;
        return reqMsg;
    }

    setLastCapBankControlledDeviceId(capBank->getPaoId());
    string textInfo = "";
    if (action == CtiCCCapBank::Close ||
        action == CtiCCCapBank::ClosePending ||
        action == CtiCCCapBank::CloseQuestionable ||
        action == CtiCCCapBank::CloseFail )
    {
        store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::Close,
                                                                       capBank, true);
        textInfo += "Close Sent, ";
        textInfo += typeOfControl;
    }
    else
    {
        store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::Open,
                                                                       capBank, true);
        textInfo += "Open Sent, ";
        textInfo += typeOfControl;
    }
    capBank->setControlStatusQuality(CC_AbnormalQuality);
    setCurrentDailyOperationsAndSendMsg(_currentdailyoperations+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);


    capBank->setBeforeVarsString(textInfo);
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional;
        additional = ("Sub: ");
        additional += getParentName();
        additional += " /Feeder: ";
        additional += getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPaoDescription();
            additional += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPaoName()));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0, capBank->getIpAddress(), capBank->getActionId(), stateInfo));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" << endl;
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
    }

    if  (capBank->getTwoWayPoints() != NULL)
    {
        if (capBank->getPointIdByAttribute(PointAttribute::CapacitorBankState) > 0)
        {
            CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(),
                                                                               capBank->getPointIdByAttribute(PointAttribute::CapacitorBankState),
                                                                               capBank->getControlStatus(),
                                                                               CtiTime(), -1, 100 );
            hist->setMessagePriority( hist->getMessagePriority() + 2 );
            pointChanges.push_back( hist );
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
        }
    }
    if (capBank->getControlStatus() == CtiCCCapBank::Close )
    {
        reqMsg = createPorterRequestMsg( capBank->getControlDeviceId(),"control close" );
    }
    else
        reqMsg = createPorterRequestMsg( capBank->getControlDeviceId(),"control open" );

    reqMsg->setSOE(4);

    return reqMsg;
}

void CtiCCFeeder::createForcedVarConfirmation(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string typeOfControl)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    string textInfo = "Var: ";
    textInfo += typeOfControl;
    textInfo += ", ";
    textInfo += capBank->getControlStatusText();

    capBank->setControlRecentlySentFlag(true);
    long stationId, areaId, spAreaId;
    store->getFeederParentInfo(this, spAreaId, areaId, stationId);
    string stateInfo = capBank->getControlStatusQualityString();
    ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capBankStateUpdate, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0, capBank->getIpAddress(), capBank->getActionId(), stateInfo));

}


/*---------------------------------------------------------------------------
    figureEstimatedVarLoadPointValue

    Figures out the estimated var point value according to the states
    of the individual cap banks in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::figureEstimatedVarLoadPointValue()
{
    if( getCurrentVarLoadPointId() > 0 )
    {
        double tempValue;
        if( getRecentlyControlledFlag() || getPerformingVerificationFlag())
            tempValue = getVarValueBeforeControl();
        else
            tempValue = getCurrentVarLoadPointValue();

        for(long i=0;i<_cccapbanks.size();i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            if( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
            {
                tempValue = tempValue + currentCapBank->getBankSize();
            }
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
    figureCurrentSetPoint

    Returns the current set point depending on if it is peak or off peak
    time and sets the set point status
---------------------------------------------------------------------------*/
/*double CtiCCFeeder::figureCurrentSetPoint(const CtiTime& currentDateTime)
{
    return (isPeakTime(currentDateTime)?_peaksetpoint:_offpeaksetpoint);
}   */

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isPeakTime(const CtiTime& currentDateTime)
{
    unsigned secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
    if( isPeakDay() && getStrategy()->getPeakStartTime() <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= getStrategy()->getPeakStopTime() )
    {
        setPeakTimeFlag(true);
    }
    else
    {
        setPeakTimeFlag(false);
    }
    return _peakTimeFlag;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isPeakDay()
{
    //-------------------------------------
    //Need to check if it is a holiday today
    //also, but we must wait until there is
    //a dll with a function to do this
    //-------------------------------------
    CtiTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    if( getStrategy()->getDaysOfWeek()[start_tm.tm_wday] == 'Y' &&
        ( getStrategy()->getDaysOfWeek()[7] == 'Y' ||
          !CtiHolidayManager::getInstance().isHoliday(CtiDate()) ) )
        return true;
    else
        return false;
}

bool CtiCCFeeder::isControlPoint(long pointid)
{
    bool retVal = false;

    if (ciStringEqual(getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod) )
    {
        if (ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::VoltsControlUnit)  &&
            getCurrentVoltLoadPointId() == pointid )
            retVal = true;
        else if (ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit)  &&
            getCurrentVarLoadPointId() == pointid)
            retVal = true;
        else if (ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit) &&
                 (getCurrentVarLoadPointId() == pointid || getCurrentWattLoadPointId() == pointid) )
            retVal = true;
        else
            retVal = false;
    }
    return retVal;
}

void CtiCCFeeder::updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime)
{
    double controlVvalue = 0;

    if( getDisableFlag() )
    {
        return;
    }

    if (ciStringEqual(getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod) )
    {
        if (ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::VoltsControlUnit) )
            controlVvalue = getCurrentVoltLoadPointValue();
        else if (ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit)||
                 ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit))
            controlVvalue = getCurrentVarLoadPointValue();
        else
        {
            //integration not implemented.
            controlVvalue = 0;
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " **DEBUG** integration not implemented for this controlUnit method " << endl;
            }
        }

        if (getStrategy()->getControlInterval() > 0)
        {
            if (nextCheckTime - getStrategy()->getIntegratePeriod() <= currentDateTime)
            {
                if (nextCheckTime > currentDateTime)
                {


                    if (getIVCount() == 0)
                    {
                        setIVControlTot( controlVvalue );
                    }
                    else
                        setIVControlTot( getIVControlTot() + controlVvalue );

                    setIVCount( getIVCount() + 1 );
                }

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " " << getPaoName() <<": iVControlTot = " <<getIVControlTot() <<" iVCount = "<<getIVCount()<< endl;
    }
}

void CtiCCFeeder::updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime)
{
    double controlWvalue = 0;

    if( getDisableFlag() )
    {
        return;
    }

    if (ciStringEqual(getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod) )
    {
        controlWvalue = getCurrentWattLoadPointValue();
        if (getStrategy()->getControlInterval() > 0)
        {
            if (nextCheckTime - getStrategy()->getIntegratePeriod() <= currentDateTime)
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " " << getPaoName() <<":  iWControlTot = " <<getIWControlTot() <<" iWCount = "<<getIWCount()<< endl;
    }
}

void CtiCCFeeder::orderBanksOnFeeder()
{
    CtiCCCapBank_SVector displayCaps;
    CtiCCCapBank_SCloseVector closeCaps;
    CtiCCCapBank_STripVector tripCaps;
    int i=0;
    CtiCCCapBank* currentCapBank = NULL;
    for (i = 0; i < _cccapbanks.size(); i++)
    {
        displayCaps.insert(_cccapbanks[i]);
        closeCaps.insert(_cccapbanks[i]);
        tripCaps.insert(_cccapbanks[i]);
    }

    for(i=0;i<displayCaps.size();i++)
    {
        currentCapBank = (CtiCCCapBank*)displayCaps[i];
        currentCapBank->setControlOrder(i + 1);
    }

    for(i=0;i<closeCaps.size();i++)
    {
        currentCapBank = (CtiCCCapBank*)closeCaps[i];
        currentCapBank->setCloseOrder(i + 1);
    }

    for(i=0;i<tripCaps.size();i++)
    {
        currentCapBank = (CtiCCCapBank*)tripCaps[i];
        currentCapBank->setTripOrder(i + 1);
    }

}

void CtiCCFeeder::figureAndSetTargetVarValue(const string& controlMethod, const string& controlUnits, bool peakTimeFlag)
{

    if( ciStringEqual(controlMethod, ControlStrategy::IndividualFeederControlMethod ))
    {
        string feederControlUnits = controlUnits;
        if (!ciStringEqual(getStrategy()->getStrategyName(),"(none)"))
        {
            feederControlUnits = getStrategy()->getControlUnits();
        }
        if (ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKVarControlUnit) ||
            ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKQControlUnit ))
        {
            double setpoint = (peakTimeFlag?getStrategy()->getPeakPFSetPoint():getStrategy()->getOffPeakPFSetPoint());
            setKVARSolution(CtiCCSubstationBus::calculateKVARSolution(feederControlUnits, setpoint, getCurrentVarLoadPointValue(), getCurrentWattLoadPointValue()));
            setTargetVarValue( getKVARSolution() + getCurrentVarLoadPointValue());
        }
        else
        {

            double lagLevel = (peakTimeFlag?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
            double leadLevel = (peakTimeFlag?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
            double setpoint = (lagLevel + leadLevel)/2;
            setKVARSolution(CtiCCSubstationBus::calculateKVARSolution(feederControlUnits, setpoint, getCurrentVarLoadPointValue(), getCurrentWattLoadPointValue()));
            if( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) )
            {
                setTargetVarValue( getKVARSolution() + getCurrentVoltLoadPointValue());
            }
            else
            {
                setTargetVarValue( getKVARSolution() + getCurrentVarLoadPointValue());
            }
        }
    }
    else
    {
        setTargetVarValue(0);
    }
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededIndividualControl


---------------------------------------------------------------------------*/
bool CtiCCFeeder::checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, bool peakTimeFlag, long decimalPlaces, const string& controlUnits, bool dailyMaxOpsHitFlag)
{
    bool returnBoolean = false;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    double lagLevel = (peakTimeFlag?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
    double leadLevel = (peakTimeFlag?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
    double setpoint = (lagLevel + leadLevel)/2;
    bool maxOpsDisableFlag = getStrategy()->getMaxOperationDisableFlag();
    string feederControlUnits = controlUnits;
    if (!ciStringEqual(getStrategy()->getStrategyName(),"(none)"))
    {
        feederControlUnits = getStrategy()->getControlUnits();
        maxOpsDisableFlag = getStrategy()->getMaxOperationDisableFlag();
    }
    if( ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKVarControlUnit) ||
       ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKQControlUnit) )
        setpoint = (peakTimeFlag?getStrategy()->getPeakPFSetPoint():getStrategy()->getOffPeakPFSetPoint());

    //Integration Control Point setting...
    setIWControl(getCurrentWattLoadPointValue());
    if (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit))
        setIVControl(getCurrentVoltLoadPointValue());
    else
        setIVControl(getCurrentVarLoadPointValue());
    if (getStrategy()->getIntegrateFlag() && getStrategy()->getIntegratePeriod() > 0)
    {
        if (getIVCount() > 0)
            setIVControl(getIVControlTot() / getIVCount());
        if (getIWCount() > 0)
            setIWControl(getIWControlTot() / getIWCount());

        if( _CC_DEBUG & CC_DEBUG_INTEGRATED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " " << getPaoName() <<"  USING INTEGRATED CONTROL - iVControl=iVControlTot/iVCount ( "<<
                    getIVControl()<<" = "<< getIVControlTot() <<" / "<<getIVCount()<<" )"<< endl;
            dout << CtiTime()  << " " << getPaoName() <<" USING INTEGRATED CONTROL - iWControl=iWControlTot/iWCount ( "<<
                    getIWControl()<<" = "<< getIWControlTot() <<" / "<<getIWCount()<<" )"<< endl;
        }
     //resetting integration total...
        if (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit))
            setIVControlTot(getCurrentVoltLoadPointValue());
        else
            setIVControlTot(getCurrentVarLoadPointValue());
        setIVCount(1);
        setIWControlTot(getCurrentWattLoadPointValue());
        setIWCount(1);
    }

    setKVARSolution(CtiCCSubstationBus::calculateKVARSolution(feederControlUnits,setpoint,getIVControl(),getIWControl()));
    setTargetVarValue( getKVARSolution() + getIVControl());


    //if current var load is outside of range defined by the set point plus/minus the bandwidths
    CtiRequestMsg* request = NULL;

    //checks max daily op count, feeder disable if maxOperationDisableFlag set.
    checkMaxDailyOpCountExceeded(pointChanges);

    bool arePointsNormalQuality = ( getCurrentVarPointQuality() == NormalQuality &&
                         ( ciStringEqual(feederControlUnits,ControlStrategy::PFactorKWKVarControlUnit) ? getCurrentWattPointQuality() == NormalQuality :
                         ( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) ? getCurrentVoltPointQuality() == NormalQuality : getCurrentVarPointQuality()  == NormalQuality) ) );

    if( !getDisableFlag() &&
        !getWaiveControlFlag() &&
        ( !_IGNORE_NOT_NORMAL_FLAG || arePointsNormalQuality ) &&
        ( currentDateTime.seconds() >= getLastOperationTime().seconds() + getStrategy()->getControlDelayTime() ) )
    {
        if( (ciStringEqual(feederControlUnits, ControlStrategy::KVarControlUnit) &&
             getCurrentVarLoadPointId() > 0) ||
            (ciStringEqual(feederControlUnits, ControlStrategy::VoltsControlUnit) &&
             getCurrentVarLoadPointId() > 0 && getCurrentVoltLoadPointId() > 0 ) )
        {
            if( (ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) &&
                (getIVControl() > lagLevel || getIVControl() < leadLevel )) ||
                (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) &&
                (getIVControl() < lagLevel || getIVControl() > leadLevel) ) )
            {

                try
                {
                    if( ( ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) &&
                          lagLevel < getIVControl() ) ||
                        ( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) &&
                          lagLevel > getIVControl() ) )
                    {
                        //if( _CC_DEBUG )
                        if( ciStringEqual(feederControlUnits, ControlStrategy::KVarControlUnit) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Attempting to Decrease Var level in feeder: " << getPaoName().data() << endl;
                        }
                        else
                        {
                            setKVARSolution(-1);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Attempting to Increase Volt level in feeder: " << getPaoName().data() << endl;
                            }
                        }

                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(),!(ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit)));

                        if( capBank != NULL &&
                            capBank->getRecloseDelay() > 0 &&
                            currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay." << endl;
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime() << endl;
                                dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                dout << " Current Date Time:       " << currentDateTime << endl;
                            }
                        }
                        else
                        {
                            //double controlValue = (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                            string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Close, getIVControl(), getCurrentVarLoadPointValue());
                            request = createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                            if( request == NULL )
                            {
                                if(  ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) )
                                    createCannotControlBankText("Decrease Var", "Close", ccEvents);
                                else
                                    createCannotControlBankText("Increase Volt", "Close", ccEvents);
                            }
                            else
                            {
                                setCorrectionNeededNoBankAvailFlag(false);

                            }
                        }
                    }
                    else if (( ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) &&
                          getIVControl() < leadLevel ) ||
                        ( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) &&
                          getIVControl() > leadLevel ) )
                    {
                        //if( _CC_DEBUG )
                        if( ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Attempting to Increase Var level in feeder: " << getPaoName().data() << endl;
                        }
                        else
                        {
                            setKVARSolution(1);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Attempting to Decrease Volt level in feeder: " << getPaoName().data() << endl;
                            }
                        }

                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), !(ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit)));

                        //double controlValue = (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Open, getIVControl(), getCurrentVarLoadPointValue());
                        request = createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                        if( request == NULL )
                        {
                            if(  ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) )
                                    createCannotControlBankText("Increase Var", "Open", ccEvents);
                                else
                                    createCannotControlBankText("Decrease Volt", "Open", ccEvents);
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
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Max Daily Ops Hit. Control Inhibited on: " << getPaoName() << endl;
                        }
                    }

                    if( request != NULL )
                    {
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                        returnBoolean = true;
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
        }
        else if( (ciStringEqual(feederControlUnits,ControlStrategy::PFactorKWKVarControlUnit) ||
                 ciStringEqual(feederControlUnits,ControlStrategy::PFactorKWKQControlUnit) )
                 && getCurrentVarLoadPointId() > 0 && getCurrentWattLoadPointId() > 0 )
        {
            if( getKVARSolution() < 0 &&
                !(dailyMaxOpsHitFlag && maxOpsDisableFlag) &&
                !(getMaxDailyOpsHitFlag() && maxOpsDisableFlag) )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Var level in feeder: " << getPaoName() << endl;
                }

                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit));
                if( capBank != NULL )
                {
                    if( capBank->getRecloseDelay() > 0 &&
                        currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay." << endl;
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime() << endl;
                            dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                            dout << " Current Date Time:       " << currentDateTime << endl;
                        }
                    }
                    else
                    {
                        double adjustedBankKVARReduction = (lagLevel/100.0)*((double)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Close, getIVControl(), getCurrentVarLoadPointValue());
                            request = createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch" << endl;
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
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Var level in feeder: " << getPaoName() << endl;
                }

                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit));
                if( capBank != NULL )
                {
                    double adjustedBankKVARIncrease = -(leadLevel/100.0) * capBank->getBankSize();
                    if( adjustedBankKVARIncrease <= getKVARSolution() )
                    {
                        string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Open, getIVControl(), getCurrentVarLoadPointValue());
                        request = createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                    }
                    else
                    {//cap bank too big
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch" << endl;
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
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Max Daily Ops Hit. Control Inhibited on: " << getPaoName() << endl;
                }

            }
            if( request != NULL )
            {
                pilMessages.push_back(request);
                setLastOperationTime(currentDateTime);
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }
                returnBoolean = true;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid control units: " << feederControlUnits << ", in feeder: " << getPaoName() << endl;
        }
    }
    else
    {
        if ( _IGNORE_NOT_NORMAL_FLAG && !arePointsNormalQuality  )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Control Inhibited on Feeder: "<<getPaoName()<< " by Abnormal Point Quality " <<endl;
            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                dout << " Var PointId: " <<getCurrentVarLoadPointId()  <<" (" << getCurrentVarPointQuality()
                    <<")  Watt PointId: "<<getCurrentWattLoadPointId() <<" (" << getCurrentWattPointQuality()
                    <<")  Volt PointId: "<<getCurrentVoltLoadPointId() <<" (" << getCurrentVoltPointQuality() <<")"<< endl;
            }
        }
    }

    return returnBoolean;
}

bool CtiCCFeeder::capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, long minConfirmPercent, long failurePercent,
                                             double varValueBeforeControl, double currentVarLoadPointValue, long currentVarPointQuality,
                                             double varAValue, double varBValue, double varCValue, const CtiRegression& reg)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool found = false;
    double change = 0;
    double ratio = 0;
    double varValueBC = varValueBeforeControl;

    string text = "";
    string additional = "Sub: ";
              additional += getParentName();
              additional += " /Feeder: ";
              additional += getPaoName();;
              if (_LOG_MAPID_INFO)
              {
                  additional += " MapID: ";
                  additional += getMapLocationId();
                  additional += " (";
                  additional += getPaoDescription();
                  additional += ")";
              }


    for(long i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() )
        {
            returnBoolean = true;
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                removeMaxKvar(currentCapBank->getPaoId());
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && reg.depthMet() )
                    {
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueBC = reg.regression( CtiTime().seconds() );

                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Rate of Change Value: " << varValueBC << endl;
                        }

                        // is estimated within Percent of currentVar?
                        change = currentVarLoadPointValue - varValueBC;
                    }
                    else
                    {
                        change = currentVarLoadPointValue - varValueBC;
                    }
                    if( _RATE_OF_CHANGE && !reg.depthMet() )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Rate of Change Depth not met: " << reg.getCurDepth() << " / " << reg.getRegDepth() << endl;
                    }

                    if( change < 0 )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<": Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }

                    ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                            {
                                currentCapBank->setControlStatusQuality(CC_Fail);
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: OpenFail: "<<currentCapBank->getPaoName() << endl;
                            }
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlStatusQuality(CC_Significant);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: OpenQuestionable: "<<currentCapBank->getPaoName() << endl;
                            }
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: Open: "<<currentCapBank->getPaoName() << endl;
                            }
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                        {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: Open: "<<currentCapBank->getPaoName() << endl;
                        }
                    }
                    text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), currentVarLoadPointValue,ratio);

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));

                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(currentVarLoadPointValue, getDecimalPlaces()).toString();
                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += ", OpenQuestionable";

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                removeMaxKvar(currentCapBank->getPaoId());
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && reg.depthMet() )
                    {
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueBC = reg.regression( CtiTime().seconds() );

                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Rate of Change Value: " << varValueBC << endl;
                        }
                        // is estimated within Percent of currentVar?
                        change = varValueBC - currentVarLoadPointValue;
                    }
                    else
                    {
                        change = varValueBC - currentVarLoadPointValue;
                    }
                    if( _RATE_OF_CHANGE && !reg.depthMet() )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Rate of Change Depth not met: " << reg.getCurDepth() << " / " << reg.getRegDepth() << endl;
                    }

                    if( change < 0 )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }

                    ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            //currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                currentCapBank->setControlStatusQuality(CC_Fail);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: CloseFail: "<<currentCapBank->getPaoName() << endl;
                            }
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlStatusQuality(CC_Significant);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: CloseQuestionable: "<<currentCapBank->getPaoName() << endl;
                            }
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: Close: "<<currentCapBank->getPaoName() << endl;
                            }
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                        {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CapBank Control Status: Close: "<<currentCapBank->getPaoName() << endl;
                        }
                    }

                    text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), currentVarLoadPointValue, ratio);

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(currentVarLoadPointValue, getDecimalPlaces()).toString();
                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += ", CloseQuestionable";

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                }

            }
            else
            {
                returnBoolean = false;
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                }
                text += "Var: ";
                text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                text += " - control was not pending, " ;
                text += currentCapBank->getControlStatusText();
                currentCapBank->setControlStatusQuality(CC_Fail);
            }

            if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
            {
                currentCapBank->setRetryOpenFailedFlag(false);
                currentCapBank->setRetryCloseFailedFlag(false);
            }

            if( currentCapBank->getStatusPointId() > 0 )
            {
                CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                if( sub != NULL )
                {
                   sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, false,
                                                                  varValueBC, currentVarLoadPointValue, change,  varAValue, varBValue, varCValue);
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
            }

            found = true;
            currentCapBank->setControlRecentlySentFlag(false);
            currentCapBank->setIgnoreFlag(false);
            break;
        }
    }

    if (found == false)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        returnBoolean = false;
    }

    setRetryIndex(0);
    setRecentlyControlledFlag(false);

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
bool CtiCCFeeder::capBankControlPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, long minConfirmPercent,
                                                     long failurePercent, long currentVarPointQuality, double varAValueBeforeControl,
                                                     double varBValueBeforeControl, double varCValueBeforeControl,
                                                     double varAValue, double varBValue, double varCValue,
                                                     const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool found = false;
    double change = 0;
    double changeA = 0;
    double changeB = 0;
    double changeC = 0;
    double ratioA = 0;
    double ratioB = 0;
    double ratioC = 0;
    double varValueAbc = varAValueBeforeControl;
    double varValueBbc = varBValueBeforeControl;
    double varValueCbc = varCValueBeforeControl;

    string text = "";
    string additional = "Sub: ";
           additional += getParentName();
           additional += " /Feeder: ";
           additional += getPaoName();;
           if (_LOG_MAPID_INFO)
           {
               additional += " MapID: ";
               additional += getMapLocationId();
               additional += " (";
               additional += getPaoDescription();
               additional += ")";
           }

    for(long i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() )
        {
            returnBoolean = true;
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && regressionA.depthMet() && regressionB.depthMet() && regressionC.depthMet() )
                    {
                        CtiTime timeNow;
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueAbc = regA.regression( timeNow.seconds() );
                        varValueBbc = regB.regression( timeNow.seconds() );
                        varValueCbc = regC.regression( timeNow.seconds() );

                        int size =  currentCapBank->getBankSize()/3;
                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << timeNow << " - Rate of Change  Phase A: " << varValueAbc << endl;
                            dout << timeNow << "                   Phase B: " << varValueBbc << endl;
                            dout << timeNow << "                   Phase C: " << varValueCbc << endl;
                        }
                    }
                    changeA = varAValue - varValueAbc;
                    changeB = varBValue - varValueBbc;
                    changeC = varCValue - varValueCbc;

                    if( _RATE_OF_CHANGE && (!regA.depthMet() || !regB.depthMet() || !regC.depthMet()) )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        CtiTime timeNow;
                        dout << timeNow << " - Rate of Change Depth not met. Phase A: " << regA.getCurDepth() << " / " << regA.getRegDepth() << endl;
                        dout << timeNow << "                                 Phase B: " << regB.getCurDepth() << " / " << regB.getRegDepth() << endl;
                        dout << timeNow << "                                 Phase C: " << regC.getCurDepth() << " / " << regC.getRegDepth() << endl;
                    }

                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? changeA:"<<changeA<<" changeB:"<<changeB<<" changeC:"
                            <<changeC<<" in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);
                    if( !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                    {
                        if( shouldCapBankBeFailed(ratioA, ratioB, ratioC, failurePercent) &&
                            failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                currentCapBank->setControlStatusQuality(CC_Fail);
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failurePercent))
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
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                    }

                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failurePercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                          varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc, varValueBbc, varValueCbc,1.0));
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(varAValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varBValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varCValue, getDecimalPlaces()).toString();

                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += ", OpenQuestionable";
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && regressionA.depthMet() && regressionB.depthMet() && regressionC.depthMet() )
                    {
                        CtiTime timeNow;
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueAbc = regA.regression( timeNow.seconds() );
                        varValueBbc = regB.regression( timeNow.seconds() );
                        varValueCbc = regC.regression( timeNow.seconds() );

                        int size =  currentCapBank->getBankSize()/3;

                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << timeNow << " - Rate of Change  Phase A: " << varValueAbc << endl;
                            dout << timeNow << "                   Phase B: " << varValueBbc << endl;
                            dout << timeNow << "                   Phase C: " << varValueCbc << endl;
                        }
                    }
                    changeA = (varValueAbc) - varAValue;
                    changeB = (varValueBbc) - varBValue;
                    changeC = (varValueCbc) - varCValue;

                    if( _RATE_OF_CHANGE && (!regA.depthMet() || !regB.depthMet() || !regC.depthMet()) )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        CtiTime timeNow;
                        dout << timeNow << " - Rate of Change Depth not met. Phase A: " << regA.getCurDepth() << " / " << regA.getRegDepth() << endl;
                        dout << timeNow << "                                 Phase B: " << regB.getCurDepth() << " / " << regB.getRegDepth() << endl;
                        dout << timeNow << "                                 Phase C: " << regC.getCurDepth() << " / " << regC.getRegDepth() << endl;
                    }

                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);

                    if(  !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent)   )
                    {
                        if(  shouldCapBankBeFailed(ratioA, ratioB, ratioC, failurePercent) &&
                             failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail,
                                                                       currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                currentCapBank->setControlStatusQuality(CC_Fail);
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failurePercent))
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
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                    }
                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failurePercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                          varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc, varValueBbc, varValueCbc,1.0) );
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(varAValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varBValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varCValue, getDecimalPlaces()).toString();
                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += ", CloseQuestionable";
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                }

            }
            else
            {
                returnBoolean = false;
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                }
                text += "Var: ";
                text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                text += " - control was not pending, " ;
                text += currentCapBank->getControlStatusText();

            }

            if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
            {
                currentCapBank->setRetryOpenFailedFlag(false);
                currentCapBank->setRetryCloseFailedFlag(false);
            }


            if( currentCapBank->getStatusPointId() > 0 )
            {
                CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                if( sub != NULL )
                {
                   sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, false,
                                                   varValueAbc+varValueBbc+varValueCbc, varAValue+varBValue+varCValue, changeA+changeB+changeC,
                                                   varAValue, varBValue, varCValue);
                }

            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
            }
            found = true;

            currentCapBank->setIgnoreFlag(false);
            currentCapBank->setControlRecentlySentFlag(false);
            break;
        }
    }
    if (found == false)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
    }

    setRecentlyControlledFlag(false);
    setRetryIndex(0);

    return returnBoolean;
}

bool CtiCCFeeder::capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, long minConfirmPercent, long failurePercent,
                                                  double varAValue, double varBValue, double varCValue)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool foundCap = false;
    double change = 0;
    double ratio = 0;
    string text = "";
    string additional = "";
    bool assumedWrongFlag = false;
    bool vResult = false; //fail

    CtiCCCapBank* currentCapBank = NULL;

    if (getUsePhaseData() && !getTotalizedControlFlag())
    {
        returnBoolean = capBankVerificationPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent, failurePercent);
    }
    else
    {
        for(int j=0;j<_cccapbanks.size();j++)
        {
           currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
           if (currentCapBank->getPaoId() == getCurrentVerificationCapBankId())
           {

               if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
               {
                   if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
                   {
                        if( _RATE_OF_CHANGE && regression.depthMet() )
                        {
                            //This will only be called if we intend to do rate of change and the regression depth is met.
                            double varValueReg = regression.regression( CtiTime().seconds() );

                            if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Rate of Change Value: " << varValueReg << endl;
                            }
                            // is estimated within Percent of currentVar?
                            change = getCurrentVarLoadPointValue() - varValueReg;
                        }
                        else
                        {
                           change = getCurrentVarLoadPointValue() - getVarValueBeforeControl();
                        }
                        if( _RATE_OF_CHANGE && !regression.depthMet() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Rate of Change Depth not met: " << regression.getCurDepth() << " / " << regression.getRegDepth() << endl;
                        }
                        if( change < 0 )
                        {
                            {
                               CtiLockGuard<CtiLogger> logger_guard(dout);
                               dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                               currentCapBank->getVCtrlIndex() == 1)
                            {
                               currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                               setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                               currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);

                               assumedWrongFlag = true;
                               change = 0 - change;
                            }
                        }

                        ratio = change/currentCapBank->getBankSize();
                        if( ratio < minConfirmPercent*.01 )
                        {
                           if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                           {
                               if (!assumedWrongFlag)
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                               else
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);

                               additional = string("Feeder: ");
                               additional += getPaoName();
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
                               additional += getPaoName();
                               currentCapBank->setControlStatusQuality(CC_Significant);
                           }
                           else
                           {
                               if (!assumedWrongFlag)
                                   currentCapBank->setControlStatus(CtiCCCapBank::Open);
                               else
                                   currentCapBank->setControlStatus(CtiCCCapBank::Close);

                               additional = string("Feeder: ");
                               additional += getPaoName();
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
                           additional += getPaoName();
                           currentCapBank->setControlStatusQuality(CC_Normal);
                           vResult = true;
                        }
                        text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), getCurrentVarLoadPointValue(),ratio);

                        currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                        currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                        currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));

                   }
                   else
                   {
                       char tempchar[80];
                       currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                       text = "Var: ";
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += "- Non Normal Var Quality = ";
                       _ltoa(getCurrentVarPointQuality(),tempchar,10);
                       text += tempchar;
                       text += ", OpenQuestionable";
                       additional = string("Feeder: ");
                       additional += getPaoName();

                       currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                       currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                       currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                       currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                   }
               }
               else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
               {
                   if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
                   {
                        if( _RATE_OF_CHANGE && regression.depthMet() )
                        {
                            //This will only be called if we intend to do rate of change and the regression depth is met.
                            double varValueReg = regression.regression( CtiTime().seconds() );
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Rate of Change Value: " << varValueReg << endl;
                            }
                            // is estimated within Percent of currentVar?
                            change = varValueReg - getCurrentVarLoadPointValue();
                        }
                        else
                        {
                            change = getVarValueBeforeControl() - getCurrentVarLoadPointValue();
                        }
                        if( _RATE_OF_CHANGE && !regression.depthMet() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Rate of Change Depth not met: " << regression.getCurDepth() << " / " << regression.getRegDepth() << endl;
                        }
                        if( change < 0 )
                        {
                            {
                               CtiLockGuard<CtiLogger> logger_guard(dout);
                               dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                               currentCapBank->getVCtrlIndex() == 1)
                            {
                               currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                               setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                               assumedWrongFlag = true;
                               change = 0 - change;
                            }
                        }
                        ratio = change/currentCapBank->getBankSize();
                        if( ratio < minConfirmPercent*.01 )
                        {
                           if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                           {
                               if (!assumedWrongFlag)
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                               else
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);

                               additional = string("Feeder: ");
                               additional += getPaoName();
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
                               additional += getPaoName();
                               currentCapBank->setControlStatusQuality(CC_Significant);
                           }
                           else
                           {
                               if (!assumedWrongFlag)
                                   currentCapBank->setControlStatus(CtiCCCapBank::Close);
                               else
                                   currentCapBank->setControlStatus(CtiCCCapBank::Open);

                               additional = string("Feeder: ");
                               additional += getPaoName();
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
                           additional += getPaoName();
                           currentCapBank->setControlStatusQuality(CC_Normal);
                           vResult = true;
                        }
                        text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), getCurrentVarLoadPointValue(),ratio);

                        currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                        currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                        currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                   }
                   else
                   {
                       char tempchar[80];
                       currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                       text = "Var: ";
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += "- Non Normal Var Quality = ";
                       _ltoa(getCurrentVarPointQuality(),tempchar,10);
                       text += tempchar;
                       text += ", CloseQuestionable";
                       additional = string("Feeder: ");
                       additional += getPaoName();


                       currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                       currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                       currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));

                       currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                   }
               }
               else
               {
                   {
                       CtiLockGuard<CtiLogger> logger_guard(dout);
                       dout << CtiTime() << " - Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                   }
                   if( currentCapBank->getPerformingVerificationFlag() )
                   {
                       text += "Var: ";
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += " - control was not pending, " ;
                       text += currentCapBank->getControlStatusText();
                   }

               }
               if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                    (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
                {
                    currentCapBank->setRetryOpenFailedFlag(false);
                    currentCapBank->setRetryCloseFailedFlag(false);
                }


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
                       CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                       if( sub != NULL )
                       {
                          sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, true,
                                                          getVarValueBeforeControl(), getCurrentVarLoadPointValue(), change,
                                                          varAValue, varBValue, varCValue);
                       }

                   }
                   currentCapBank->setPorterRetFailFlag(false);
               }
               else
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                   << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
               }

               if (currentCapBank->updateVerificationState())
               {
                   returnBoolean = true;
                   currentCapBank->setPerformingVerificationFlag(false);
                   return returnBoolean;
               }

               currentCapBank->setIgnoreFlag(false);
               currentCapBank->setControlRecentlySentFlag(false);
               foundCap = true;
               break;
           }
        }

        if (foundCap == false)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Last Verification Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
            returnBoolean = true;
        }
    }

    setRetryIndex(0);
    return returnBoolean;
}

bool CtiCCFeeder::capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, long minConfirmPercent, long failPercent)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool foundCap = false;
    double change = 0;
    double ratio = 0;
    string text = "";
    string additional = "";
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

    bool assumedWrongFlag = false;


    bool vResult = false; //fail

    CtiCCCapBank* currentCapBank = NULL;

    for(int j=0;j<_cccapbanks.size();j++)
    {
       currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
       if (currentCapBank->getPaoId() == getCurrentVerificationCapBankId())
       {

           if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
           {
               if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
               {
                    if( _RATE_OF_CHANGE && regressionA.depthMet() &&
                        regressionB.depthMet() && regressionC.depthMet() )
                    {

                        CtiTime timeNow;
                        varValueAbc = getRegressionA().regression( timeNow.seconds() );
                        varValueBbc = getRegressionB().regression( timeNow.seconds() );
                        varValueCbc = getRegressionC().regression( timeNow.seconds() );
                        int size =  currentCapBank->getBankSize()/3;
                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << timeNow << " - Rate of Change  Phase A: " << varValueAbc << endl;
                            dout << timeNow << "                   Phase B: " << varValueBbc << endl;
                            dout << timeNow << "                   Phase C: " << varValueCbc << endl;
                        }
                    }
                    changeA = varAValue - varValueAbc;
                    changeB = varBValue - varValueBbc;
                    changeC = varCValue - varValueCbc;


                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                           currentCapBank->getVCtrlIndex() == 1)
                        {
                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                           setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                           currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);

                           assumedWrongFlag = true;
                           changeA = 0 - changeA;
                           changeB = 0 - changeB;
                           changeC = 0 - changeC;
                        }
                    }

                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);
                    if( !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent)  )
                    {
                        if( shouldCapBankBeFailed(ratioA, ratioB, ratioC, failPercent) &&
                             failPercent != 0 && minConfirmPercent != 0 )
                        {
                           if (!assumedWrongFlag)
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                           else
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);

                           additional = string("Feeder: ");
                           additional += getPaoName();
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
                           additional += getPaoName();
                           if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failPercent))
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
                           additional += getPaoName();
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
                       additional += getPaoName();
                       currentCapBank->setControlStatusQuality(CC_Normal);
                       vResult = true;
                    }
                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failPercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                          varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));


               }
               else
               {
                   char tempchar[80];
                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                   text = "Var: ";
                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                   text += "- Non Normal Var Quality = ";
                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                   text += tempchar;
                   text += ", OpenQuestionable";
                   additional = string("Feeder: ");
                   additional += getPaoName();

                   currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                   currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                   currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));
                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
               }
           }
           else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
           {
               if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
               {
                    if( _RATE_OF_CHANGE && regressionA.depthMet() &&
                        regressionB.depthMet() && regressionC.depthMet() )
                    {

                        CtiTime timeNow;
                        varValueAbc = getRegressionA().regression( timeNow.seconds() );
                        varValueBbc = getRegressionB().regression( timeNow.seconds() );
                        varValueCbc = getRegressionC().regression( timeNow.seconds() );
                        int size =  currentCapBank->getBankSize()/3;
                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << timeNow << " - Rate of Change  Phase A: " << varValueAbc << endl;
                            dout << timeNow << "                   Phase B: " << varValueBbc << endl;
                            dout << timeNow << "                   Phase C: " << varValueCbc << endl;
                        }
                    }
                    changeA = varValueAbc - varAValue;
                    changeB = varValueBbc - varBValue;
                    changeC = varValueCbc - varCValue;

                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - "<< currentCapBank->getPaoName() <<":Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                           currentCapBank->getVCtrlIndex() == 1)
                        {
                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                           setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                           currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                           assumedWrongFlag = true;
                           changeA = 0 - changeA;
                           changeB = 0 - changeB;
                           changeC = 0 - changeC;

                        }
                    }
                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);
                    if( !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent)  )
                    {

                        if( shouldCapBankBeFailed(ratioA, ratioB, ratioC, failPercent) &&
                             failPercent != 0 && minConfirmPercent != 0 )
                        {
                           if (!assumedWrongFlag)
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                           else
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);

                           additional = string("Feeder: ");
                           additional += getPaoName();
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
                           additional += getPaoName();
                           if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failPercent))
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
                           additional += getPaoName();
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
                       additional += getPaoName();
                       currentCapBank->setControlStatusQuality(CC_Normal);
                       vResult = true;
                    }
                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failPercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                   varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

               }
               else
               {
                   char tempchar[80];
                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = "Var: ";
                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                   text += "- Non Normal Var Quality = ";
                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                   text += tempchar;
                   text += ", CloseQuestionable";
                   additional = string("Feeder: ");
                   additional += getPaoName();

                   currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                   currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                   currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
               }
           }
           else
           {
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
               }
               text += "Var: ";
               text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
               text += " - control was not pending, " ;
               text += currentCapBank->getControlStatusText();

           }
           if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
               (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
           {
               currentCapBank->setRetryOpenFailedFlag(false);
               currentCapBank->setRetryCloseFailedFlag(false);
           }


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
                   CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                   if( sub != NULL )
                   {
                      sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, true,
                                                      varValueAbc+varValueBbc+varValueCbc, varAValue+varBValue+varCValue, changeA+changeB+changeC,
                                                      varAValue, varBValue, varCValue);
                   }
               }
               currentCapBank->setPorterRetFailFlag(false);
           }
           else
           {
               CtiLockGuard<CtiLogger> logger_guard(dout);
               dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
               << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
           }

           if (currentCapBank->updateVerificationState())
           {
               returnBoolean = true;
               currentCapBank->setPerformingVerificationFlag(false);
               return returnBoolean;
           }

           currentCapBank->setIgnoreFlag(false);
           currentCapBank->setControlRecentlySentFlag(false);
           foundCap = true;
           break;
       }
    }

    if (foundCap == false)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Verification Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        returnBoolean = true;
    }

    setRetryIndex(0);
    return returnBoolean;
}


bool CtiCCFeeder::startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    //get CapBank to perform verification on...subbus stores, currentCapBankToVerifyId

    CtiRequestMsg* request = NULL;
    bool retVal = true;

    for(long j=0;j<_cccapbanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
        if( currentCapBank->getPaoId() == _currentVerificationCapBankId )
        {
            currentCapBank->initVerificationControlStatus();
            currentCapBank->setAssumedOrigVerificationState(getCurrentVerificationCapBankOrigState());
            currentCapBank->setPreviousVerificationControlStatus(-1);
            currentCapBank->setVCtrlIndex(1); //1st control sent
            setPerformingVerificationFlag(true);

            setCurrentVerificationCapBankState(currentCapBank->getAssumedOrigVerificationState());
            currentCapBank->setPerformingVerificationFlag(true);
            if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
            {
                request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Close);
            }
            else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
            {
                request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Open);
            }
            if( request != NULL )
            {
                pilMessages.push_back(request);
                setLastOperationTime(currentDateTime);

                setLastVerificationMsgSentSuccessfulFlag(true);
                setLastCapBankControlledDeviceId( currentCapBank->getPaoId());
                setLastOperationTime(currentDateTime);
               //((CtiCCFeeder*)_ccfeeders[currentPosition])->setLastOperationTime(currentDateTime);
                setVarValueBeforeControl(getCurrentVarLoadPointValue());
                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                figureEstimatedVarLoadPointValue();
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }

            }
            else
            {
                retVal = false;
                setLastVerificationMsgSentSuccessfulFlag(false);
            }

            return retVal;
        }
    }
    return retVal;
}

CtiRequestMsg*  CtiCCFeeder::createCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                      CtiMultiMsg_vec& pilMessages, CtiCCCapBank* currentCapBank, int control)
{

    CtiRequestMsg* request = NULL;
    if( control == CtiCCCapBank::Close && currentCapBank->getRecloseDelay() > 0 &&
        currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + currentCapBank->getRecloseDelay() )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can Not Close Cap Bank: " << currentCapBank->getPaoName() << " yet...because it has not passed its reclose delay." << endl;
        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
            dout << " Last Status Change Time: " << currentCapBank->getLastStatusChangeTime() << endl;
            dout << " Reclose Delay:           " << currentCapBank->getRecloseDelay() << endl;
            dout << " Current Date Time:       " << currentDateTime << endl;
        }

        setWaitForReCloseDelayFlag(true);
    }
    else
    {
        //check capbank reclose delay here...
        double controlValue = (ciStringEqual(getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
        string text = createTextString(getStrategy()->getControlMethod(), control, controlValue, getCurrentVarLoadPointValue()) ;
        bool flipFlag = _USE_FLIP_FLAG && stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701");
        
        if( control == CtiCCCapBank::Open)
        {
            control = (flipFlag ? 4 : CtiCCCapBank::Open);
            request = createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(),
                                                       getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
        }
        else
        {
            control = (flipFlag ? 4 : CtiCCCapBank::Close);
            request = createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(),
                                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
        }
    }
    return request;
}




bool CtiCCFeeder::sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = false;
    CtiRequestMsg* request = NULL;
    for(long j=0;j<_cccapbanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
        if( currentCapBank->getPaoId() == _currentVerificationCapBankId )
        {
            if (currentCapBank->getVCtrlIndex() == 1 || currentCapBank->getVCtrlIndex() == 3)
            {
                if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                {
                    request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Close);
                }
                else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                {
                    request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Open);
                }
            }
            else if (currentCapBank->getVCtrlIndex() == 2)
            {
                //(getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open) ||
                //(getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank, getCurrentVerificationCapBankOrigState());
            }
            else if (currentCapBank->getVCtrlIndex() == 5 || currentCapBank->getVCtrlIndex() == 0)
            {
                request = NULL;
                currentCapBank->setVCtrlIndex(5);
                setLastVerificationMsgSentSuccessfulFlag(true);
                return true;
            }

            if( request != NULL )
            {
                retVal = true;
                pilMessages.push_back(request);
                setLastCapBankControlledDeviceId( currentCapBank->getPaoId());
                setLastOperationTime(currentDateTime);
                setVarValueBeforeControl(getCurrentVarLoadPointValue());
                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                setLastVerificationMsgSentSuccessfulFlag(true);
                setWaitForReCloseDelayFlag(false);
                figureEstimatedVarLoadPointValue();
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }

                return retVal;
            }
            else
            {
                setLastVerificationMsgSentSuccessfulFlag(false);
            }

        }
    }




    return retVal;
}



bool CtiCCFeeder::areThereMoreCapBanksToVerify()
{

    getNextCapBankToVerify();
    if (getCurrentVerificationCapBankId() != -1 )//&& !getDisableFlag())
    {
        setPerformingVerificationFlag(true);

        return true;
    }
    else
    {
        CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
        for(long j=0;j<ccCapBanks.size();j++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
            //currentCapBank->setVerificationFlag(false);
            currentCapBank->setPerformingVerificationFlag(false);
            currentCapBank->setVerificationDoneFlag(true);
        }

        setPerformingVerificationFlag(false);
        setVerificationDoneFlag(true);
        //setBusUpdatedFlag(true);
        return false;
    }
}


CtiCCFeeder& CtiCCFeeder::getNextCapBankToVerify()
{
    _currentVerificationCapBankId = -1;

    CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
    for(long j=0;j<ccCapBanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
        if( currentCapBank->getVerificationFlag() && !currentCapBank->getVerificationDoneFlag() )
        {
            _currentVerificationCapBankId = currentCapBank->getPaoId();
            return *this;
        }
    }
    setVerificationDoneFlag(true);
    return *this;
}



/*---------------------------------------------------------------------------
    fillOutBusOptimizedInfo

    Sets _busoptimizedvarcategory with one of three different integers
    0 (below current set point minus bandwidth),
    1 (within bandwidth around current set point), or
    2 (above current set point plus bandwidth)

    Also sets the _busoptimizedvaroffset within the current
    _busoptimizedvarcategory
---------------------------------------------------------------------------*/
void CtiCCFeeder::fillOutBusOptimizedInfo(bool peakTimeFlag)
{
    setPeakTimeFlag(peakTimeFlag);
    double lagLevel = (peakTimeFlag?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
    double leadLevel = (peakTimeFlag?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
    double setpoint = (lagLevel + leadLevel)/2;

    //if current var load is less than the set point minus the bandwidth
    if( getCurrentVarLoadPointValue() < leadLevel )
    {
        _busoptimizedvarcategory = 0;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - leadLevel;
    }
    //if current var load is within the range defined by the set point plus/minus the bandwidth
    else if( (getCurrentVarLoadPointValue() > leadLevel) &&
             (getCurrentVarLoadPointValue() < lagLevel) )
    {
        _busoptimizedvarcategory = 1;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - setpoint;
    }
    //if current var load is more than the set point plus the bandwidth
    else if( getCurrentVarLoadPointValue() > lagLevel )
    {
        _busoptimizedvarcategory = 2;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - lagLevel;
    }
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time

---------------------------------------------------------------------------*/
bool CtiCCFeeder::isAlreadyControlled(long minConfirmPercent, long currentVarPointQuality,
                                         double varAValueBeforeControl, double varBValueBeforeControl,
                                         double varCValueBeforeControl, double varAValue, double varBValue,
                                         double varCValue, double varValueBeforeControl, double currentVarLoadPointValue,
                                         const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC,
                                         bool usePhaseData, bool useTotalizedControl)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    bool returnBoolean = false;
    bool found = false;

    double timeSeconds = CtiTime().seconds();
    if( _IGNORE_NOT_NORMAL_FLAG && currentVarPointQuality != NormalQuality )
    {
        return false;
    }
    if( minConfirmPercent <= 0 )
    {
        return false;
    }

    double oldVarValue = varValueBeforeControl;
    double newVarValue = currentVarLoadPointValue;
    long bankId = getLastCapBankControlledDeviceId();

    CtiCCCapBankPtr currentCapBank = store->getCapBankByPaoId(bankId);

    if (currentCapBank != NULL)
    {
        found = true;
    }
    else
    {
        // Check all other banks on this feeder for a pending state...

        // Not adding return statements to keep the functionality the same.
        // In the error case of multiple pending banks, the last one in the list will be determining the return variable.
        for (int i=0; i < _cccapbanks.size(); ++i)
        {
            CtiCCCapBank* bank = (CtiCCCapBank*)_cccapbanks[i];
            if (bank->getControlStatus() == CtiCCCapBank::OpenPending ||
                bank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                {
                    bankId = currentCapBank->getPaoId();
                    setLastCapBankControlledDeviceId(bankId);
                    currentCapBank = bank;
                    found = true;
                }
            }
        }
    }

    if (found == true)
    {
        if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
            currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
        {
            if (usePhaseData && !useTotalizedControl)
            {
                double ratioA;
                double ratioB;
                double ratioC;
                int banksize = currentCapBank->getBankSize() / 3;

                if( checkForRateOfChange(reg,regA,regB,regC) )
                {
                    ratioA = fabs((varAValue - (regA.regression(timeSeconds))) / banksize);
                    ratioB = fabs((varBValue - (regB.regression(timeSeconds))) / banksize);
                    ratioC = fabs((varCValue - (regC.regression(timeSeconds))) / banksize);
                }
                else
                {
                    ratioA = fabs((varAValue - varAValueBeforeControl) / banksize);
                    ratioB = fabs((varBValue - varBValueBeforeControl) / banksize);
                    ratioC = fabs((varCValue - varCValueBeforeControl) / banksize);
                }

                if( areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                {
                    return true;
                }
            }
            else
            {
                double change;
                double ratio;
                int banksize = currentCapBank->getBankSize();

                if( checkForRateOfChange(reg,regA,regB,regC) )
                {
                    if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " Regression Value: " << reg.regression(timeSeconds) << endl;
                    }
                    change = fabs(reg.regression(timeSeconds) - newVarValue);
                }
                else
                {
                    change = fabs(newVarValue - oldVarValue);
                }

                ratio = change/banksize;
                if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " Change Value: " << change << " Ratio Value: " << ratio << " ?>= Min Confirm: " << minConfirmPercent << endl;
                }
                if( ratio >= minConfirmPercent*.01 )
                {
                    return true;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Last Cap Bank: " << bankId << " controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
            return true;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        return true;
    }

    return false;
}

/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isPastMaxConfirmTime(const CtiTime& currentDateTime, long maxConfirmTime, long feederRetries)
{
    bool returnBoolean = false;

    if (getStrategy()->getUnitType() != ControlStrategy::None && getStrategy()->getControlSendRetries() > feederRetries)
    {
        feederRetries = getStrategy()->getControlSendRetries();
    }

    if( ((getLastOperationTime().seconds() + ((maxConfirmTime/_SEND_TRIES) * (_retryIndex + 1))) <= currentDateTime.seconds()) ||
        ((getLastOperationTime().seconds() + ((maxConfirmTime/(feederRetries+1)) * (_retryIndex + 1))) <= currentDateTime.seconds()) )
    {
        returnBoolean = true;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isVerificationAlreadyControlled(long minConfirmPercent, long quality, double varAValueBeforeControl,
                             double varBValueBeforeControl, double varCValueBeforeControl,
                             double varAValue, double varBValue, double varCValue,
                             double oldVarValue, double newVarValue,  bool usePhaseData, bool useTotalizedControl)
{
    bool returnBoolean = false;
    bool found = false;

    if (_porterRetFailFlag == true)
    {
        _porterRetFailFlag = false;
        return true;
    }
    else if( !_IGNORE_NOT_NORMAL_FLAG || quality == NormalQuality )
    {
        if( minConfirmPercent > 0 )
        {
            CtiCCCapBankPtr currentCapBank = getLastCapBankControlledDevice();
            if( currentCapBank != NULL &&
                currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() &&
                currentCapBank->getPerformingVerificationFlag() &&
                currentCapBank->getVCtrlIndex() > 0)
                {
                    double change;
                    if( currentCapBank->isPendingStatus())
                    {
                        if ( usePhaseData && !useTotalizedControl )
                        {
                            double ratioA;
                            double ratioB;
                            double ratioC;
                            int banksize = currentCapBank->getBankSize() / 3;

                            ratioA = fabs((varAValue - varAValueBeforeControl) / banksize);
                            ratioB = fabs((varBValue - varBValueBeforeControl) / banksize);
                            ratioC = fabs((varCValue - varCValueBeforeControl) / banksize);

                            if( areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                        }
                        else
                        {
                            change = fabs(newVarValue - oldVarValue);
                            double ratio = fabs(change/currentCapBank->getBankSize());
                            if( ratio >= minConfirmPercent*.01 )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                        }
                        found = true;
                    }
                    else if (currentCapBank->getPorterRetFailFlag())
                    {
                        returnBoolean = true;
                        found = true;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Last Cap Bank: "<<getLastCapBankControlledDeviceId()<<" controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                        returnBoolean = false;
                    }

                }


            // Check all other banks on this feeder for a pending state...
            if (!found)
            {
                for(long i=0;i<_cccapbanks.size();i++)
                {
                    currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->isPendingStatus() && currentCapBank->getVCtrlIndex() > 0)
                    {
                        if ( usePhaseData && !useTotalizedControl )
                        {
                            double ratioA;
                            double ratioB;
                            double ratioC;
                            int banksize = currentCapBank->getBankSize() / 3;

                            ratioA = fabs((varAValue - varAValueBeforeControl) / banksize);
                            ratioB = fabs((varBValue - varBValueBeforeControl) / banksize);
                            ratioC = fabs((varCValue - varCValueBeforeControl) / banksize);

                            if( areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                            found = true;

                        }
                        else
                        {
                            double change = newVarValue - oldVarValue;
                            double ratio = fabs(change/currentCapBank->getBankSize());
                            if( ratio >= minConfirmPercent*.01 )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                        }
                        found = true;
                    }
                    else if (currentCapBank->getPorterRetFailFlag())
                    {
                        returnBoolean = true;
                        found = true;
                    }
                }
            }
            if (found == false)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = true;
            }
        }
    }

    return returnBoolean;
}


/*---------------------------------------------------------------------------
    attemptToResendControl

    Returns a .
---------------------------------------------------------------------------*/
bool CtiCCFeeder::attemptToResendControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, long maxConfirmTime)
{
    bool returnBoolean = false;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    for(long i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() )
        {
            if (!(_USE_FLIP_FLAG && stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && getVerificationFlag()) )
            {

                if( currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + maxConfirmTime )
                {
                    if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                    {
                        figureEstimatedVarLoadPointValue();
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            string text = string("Resending Open");
                            string additional;
                            additional = string("Sub: ");
                            additional += getParentName();
                            additional += " /Feeder: ";
                            additional += getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += getMapLocationId();
                                    additional += " (";
                                    additional += getPaoDescription();
                                    additional += ")";
                                }
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);

                            long stationId, areaId, spAreaId;
                            store->getFeederParentInfo(this, spAreaId, areaId, stationId);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandRetrySent, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control"));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                            << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                        }

                        CtiRequestMsg* reqMsg = createPorterRequestMsg(currentCapBank->getControlDeviceId(),"control open");
                        pilMessages.push_back(reqMsg);
                        if (_RETRY_ADJUST_LAST_OP_TIME)
                        {
                            setLastOperationTime(currentDateTime);
                            currentCapBank->setLastStatusChangeTime(currentDateTime);
                        }
                        returnBoolean = true;
                    }
                    else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        figureEstimatedVarLoadPointValue();
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            string text = string("Resending Close");
                            string additional;
                            additional = string("Sub: ");
                            additional += getParentName();
                            additional += " /Feeder: ";
                            additional += getPaoName();
                            if (_LOG_MAPID_INFO)
                            {
                                additional += " MapID: ";
                                additional += getMapLocationId();
                                additional += " (";
                                additional += getPaoDescription();
                                additional += ")";
                            }
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);

                            long stationId, areaId, spAreaId;
                            store->getFeederParentInfo(this, spAreaId, areaId, stationId);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandRetrySent, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control"));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                            << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                        }

                        CtiRequestMsg* reqMsg = createPorterRequestMsg(currentCapBank->getControlDeviceId(),"control close");
                        pilMessages.push_back(reqMsg);
                        if (_RETRY_ADJUST_LAST_OP_TIME)
                        {
                            setLastOperationTime(currentDateTime);
                            currentCapBank->setLastStatusChangeTime(currentDateTime);
                        }
                        returnBoolean = true;
                    }
                    else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cannot Resend Control for Cap Bank: "<< currentCapBank->getPaoName() <<", Not Pending in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }
                else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Cannot Resend Control for Cap Bank: "<< currentCapBank->getPaoName() <<", Past Confirm Time in: " << __FILE__ << " at: " << __LINE__ << endl;
                }
                break;
            }
        }
    }

    return returnBoolean;
}

bool CtiCCFeeder::checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, long maxConfirmTime, long sendRetries)
{
   bool returnBoolean = false;
   if (getVerificationFlag() && getPerformingVerificationFlag() &&
       isPastMaxConfirmTime(currentDateTime,maxConfirmTime,sendRetries) &&
       attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, maxConfirmTime) )
   {
       if (_RETRY_ADJUST_LAST_OP_TIME)
           setLastOperationTime(currentDateTime);
       setRetryIndex(getRetryIndex() + 1);
       returnBoolean = true;
   }
   return returnBoolean;
}


CtiCCFeeder& CtiCCFeeder::setVerificationFlag(bool verificationFlag)
{
    if( verificationFlag )
    {
        setVerificationDoneFlag(!verificationFlag);
    }
    if (_verificationFlag != verificationFlag)
        _dirty = true;
    _verificationFlag = verificationFlag;

    return *this;
}
CtiCCFeeder& CtiCCFeeder::setPerformingVerificationFlag(bool performingVerificationFlag)
{
    if (_performingVerificationFlag != performingVerificationFlag)
        _dirty = true;
    _performingVerificationFlag = performingVerificationFlag;

    return *this;
}
CtiCCFeeder& CtiCCFeeder::setVerificationDoneFlag(bool verificationDoneFlag)
{
    if (_verificationDoneFlag != verificationDoneFlag)
        _dirty = true;
    _verificationDoneFlag = verificationDoneFlag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setPreOperationMonitorPointScanFlag( bool flag)
{
    if (_preOperationMonitorPointScanFlag != flag)
        _dirty = true;
    _preOperationMonitorPointScanFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setOperationSentWaitFlag( bool flag)
{
    if (_operationSentWaitFlag != flag)
        _dirty = true;
    _operationSentWaitFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setPostOperationMonitorPointScanFlag( bool flag)
{
    if (_postOperationMonitorPointScanFlag != flag)
        _dirty = true;
    _postOperationMonitorPointScanFlag = flag;

    return *this;
}



CtiCCFeeder& CtiCCFeeder::setWaitForReCloseDelayFlag(bool flag)
{
    if (_waitForReCloseDelayFlag != flag)
        _dirty = true;
    _waitForReCloseDelayFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setMaxDailyOpsHitFlag(bool flag)
{
    if (_maxDailyOpsHitFlag != flag)
        _dirty = true;
    _maxDailyOpsHitFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setOvUvDisabledFlag(bool flag)
{
    if (_ovUvDisabledFlag != flag)
        _dirty = true;
    _ovUvDisabledFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setCorrectionNeededNoBankAvailFlag(bool flag)
{
    if (_correctionNeededNoBankAvailFlag != flag)
        _dirty = true;
    _correctionNeededNoBankAvailFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setLikeDayControlFlag(bool flag)
{
    if (_likeDayControlFlag != flag)
        _dirty = true;
    _likeDayControlFlag = flag;

    return *this;
}
CtiCCFeeder& CtiCCFeeder::setLastVerificationMsgSentSuccessfulFlag(bool flag)
{
    if (_lastVerificationMsgSentSuccessful != flag)
        _dirty = true;
    _lastVerificationMsgSentSuccessful = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setCurrentVerificationCapBankId(long capBankId)
{
    if( _currentVerificationCapBankId != capBankId )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }

    _currentVerificationCapBankId = capBankId;

    return *this;
}
CtiCCFeeder& CtiCCFeeder::setCurrentVerificationCapBankState(long status)
{
    if( _currentCapBankToVerifyAssumedOrigState != status )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }

    _currentCapBankToVerifyAssumedOrigState = status;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setTargetVarValue(double value)
{
    if( _targetvarvalue != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }

    _targetvarvalue = value;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setSolution(const string &text)
{
    if( _solution != text )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }

    _solution = text;

    return *this;
}

/*---------------------------------------------------------------------------
    setIVControlTot

    Sets the Integrated Volt or var Control Total of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIVControlTot(double value)
{
    if( _iVControlTot != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _iVControlTot = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVCoont

    Sets the Integrated Volt or var Control Count of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIVCount(long value)
{
    if( _iVCount != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _iVCount = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIWControlTot

    Sets the Integrated Watt Control Total of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIWControlTot(double value)
{
    if( _iWControlTot != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _iWControlTot = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIWCoont

    Sets the Integrated Watt Control Count of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIWCount(long value)
{
    if( _iWCount != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _iWCount = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVControl

    Sets the Integrated Volt/Var Control of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIVControl(double value)
{

    if( _iVControl != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _iVControl = value;
    return *this;
}/*---------------------------------------------------------------------------
    setIWControl

    Sets the Integrated Watt Control  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIWControl(double value)
{
    if( _iWControl != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _iWControl = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setUsePhaseData flag

    Sets the UsePhaseData flag  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setUsePhaseData(bool flag)
{
    _usePhaseData = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setPhaseBid

    Sets the PhaseB pointid  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseBId(long pointid)
{
    _phaseBid = pointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPhaseCid

    Sets the PhaseC pointid  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseCId(long pointid)
{
    _phaseCid = pointid;
    return *this;
}


/*---------------------------------------------------------------------------
    setTotalizedControlFlag

    Sets the TotalizedControlFlag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setTotalizedControlFlag(bool flag)
{
    _totalizedControlFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setPhaseAValue

    Sets the PhaseAValue VAr  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseAValue(double value, CtiTime timestamp)
{
    if( _phaseAvalue != value )
    {
        _dirty = true;
    }
    _phaseAvalue = value;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionA.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
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

    Sets the PhaseBValue VAr  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseBValue(double value, CtiTime timestamp)
{
    if( _phaseBvalue != value )
    {
        _dirty = true;
    }
    _phaseBvalue = value;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionB.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
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

    Sets the PhaseCValue VAr  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseCValue(double value, CtiTime timestamp)
{
    if( _phaseCvalue != value )
    {
        _dirty = true;
    }
    _phaseCvalue = value;
    if( _RATE_OF_CHANGE && !getRecentlyControlledFlag() )
    {
        regressionC.appendWithoutFill(std::make_pair((double)timestamp.seconds(),value));
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

    Sets the PhaseAValue VAr  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseAValueBeforeControl(double value)
{
    _phaseAvalueBeforeControl = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setPhaseBValue

    Sets the PhaseBValue VAr  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseBValueBeforeControl(double value)
{
    _phaseBvalueBeforeControl = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setPhaseCValue

    Sets the PhaseCValue VAr  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPhaseCValueBeforeControl(double value)
{
    _phaseCvalueBeforeControl = value;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setRetryIndex(long value)
{
    if (_retryIndex != value)
    {
        _dirty = true;
    }
    _retryIndex = value;
    return *this;
}


bool CtiCCFeeder::getVerificationFlag() const
{
    return _verificationFlag;
}

bool CtiCCFeeder::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

bool CtiCCFeeder::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

bool CtiCCFeeder::getPreOperationMonitorPointScanFlag() const
{
    return _preOperationMonitorPointScanFlag;
}
bool CtiCCFeeder::getOperationSentWaitFlag() const
{
    return _operationSentWaitFlag;
}
bool CtiCCFeeder::getPostOperationMonitorPointScanFlag() const
{
    return _postOperationMonitorPointScanFlag;
}

bool CtiCCFeeder::getWaitForReCloseDelayFlag() const
{
    return _waitForReCloseDelayFlag;
}
bool CtiCCFeeder::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}

bool CtiCCFeeder::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

bool CtiCCFeeder::getCorrectionNeededNoBankAvailFlag() const
{
    return _correctionNeededNoBankAvailFlag;

}
bool CtiCCFeeder::getLikeDayControlFlag() const
{
    return _likeDayControlFlag;
}

bool CtiCCFeeder::getLastVerificationMsgSentSuccessfulFlag() const
{
    return _lastVerificationMsgSentSuccessful;
}



long CtiCCFeeder::getCurrentVerificationCapBankId() const
{
    return _currentVerificationCapBankId;
}
long CtiCCFeeder::getCurrentVerificationCapBankOrigState() const
{
    return _currentCapBankToVerifyAssumedOrigState;

}
double CtiCCFeeder::getTargetVarValue() const
{
    return _targetvarvalue;

}
const string& CtiCCFeeder::getSolution() const
{
    return _solution;

}

bool CtiCCFeeder::voltControlBankSelectProcess(CtiCCMonitorPointPtr point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = false;
    CtiCCCapBank* bestBank = NULL;

    CtiRequestMsg* request = NULL;
   //Check for undervoltage condition first.
   try
   {
        if (point->getValue() < point->getLowerBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            double bestDelta = 0;

            //1.  First check this point's parent bank to see if we can close it.
            parentBank = getMonitorPointParentBank(point);
            if (parentBank != NULL)
            {
                if (parentBank->getControlStatus() == CtiCCCapBank::Open ||
                    parentBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
                {
                    try
                    {
                        PointResponse pResponse = parentBank->getPointResponse(point);

                        if ( (point->getValue() + pResponse.getDelta() <= point->getUpperBandwidth() &&
                              point->getValue() + pResponse.getDelta() >= point->getLowerBandwidth() ) ||
                              point->getValue() + pResponse.getDelta() < point->getUpperBandwidth() )
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " Attempting to Increase Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<parentBank->getPaoName() << endl;
                            }

                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getDeviceId()<<"/"<<point->getPointId()<<" Parent CapBank: "<<parentBank->getPaoName() <<" selected to Close" << endl;
                            }

                            //Check other monitor point responses using this potential capbank
                            if (areOtherMonitorPointResponsesOk(point->getPointId(), parentBank, CtiCCCapBank::Close))
                            {
                                double controlValue = (!ciStringEqual(getStrategy()->getControlUnits(),ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                request = createDecreaseVarRequest(parentBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

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
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " PointResponse not found. CapBank: "<< parentBank->getPaoName() <<  " pointId: " << point->getPointId() << endl;
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
                for (long i = 0; i < _cccapbanks.size(); i++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                        currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
                    {

                        if (point->getDeviceId() != currentCapBank->getPaoId())
                        {
                            try
                            {
                                PointResponse pResponse = currentCapBank->getPointResponse(point);

                                if ( (point->getValue() + pResponse.getDelta() <= point->getUpperBandwidth() &&
                                      point->getValue() + pResponse.getDelta() >= point->getLowerBandwidth() ) ||
                                      pResponse.getDelta() == 0 ||
                                      point->getValue() + pResponse.getDelta() < point->getUpperBandwidth() )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " Attempting to Increase Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<currentCapBank->getPaoName() << endl;
                                    }

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getDeviceId()<<"/"<<point->getPointId()<<" CapBank: "<<currentCapBank->getPaoName() <<" selected to Close" << endl;
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if (areOtherMonitorPointResponsesOk(point->getPointId(), currentCapBank, CtiCCCapBank::Close))
                                    {
                                        double controlValue = (!ciStringEqual(getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                        string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                        request = createDecreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                        updatePointResponsePreOpValues(currentCapBank);
                                        bestBank = currentCapBank;
                                    }
                                }
                            }
                            catch (NotFoundException& e)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " PointResponse not found. CapBank: "<< currentCapBank->getPaoName() <<  " pointId: " << point->getPointId() << endl;
                            }
                        }
                        if (request != NULL)
                        {
                            bestBank = currentCapBank;
                            break;
                        }
                    }
                }
            }
            if (request == NULL)
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " No Banks Available to Close on Feeder: "<<getPaoName() << endl;
                }
            }
        }
        else if (point->getValue() > point->getUpperBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            double bestDelta = 0;

            //1.  First check this point's parent bank to see if we can open it.
            parentBank = getMonitorPointParentBank(point);
            if (parentBank != NULL)
            {
                if (parentBank->getControlStatus() == CtiCCCapBank::Close ||
                    parentBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                {
                    try
                    {
                        PointResponse pResponse = parentBank->getPointResponse(point);

                        if ( (point->getValue() - pResponse.getDelta() <= point->getUpperBandwidth() &&
                              point->getValue() - pResponse.getDelta() >= point->getLowerBandwidth() ) ||
                              //pRespone.getDelta() == 0 ||
                              point->getValue() - pResponse.getDelta() > point->getLowerBandwidth() )
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " Attempting to Decrease Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<parentBank->getPaoName() << endl;
                            }

                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getDeviceId()<<"/"<<point->getPointId()<<" Parent CapBank: "<<parentBank->getPaoName() <<" selected to Open" << endl;
                            }
                            //Check other monitor point responses using this potential capbank
                            if (areOtherMonitorPointResponsesOk(point->getPointId(), parentBank, CtiCCCapBank::Open))
                            {
                                double controlValue = (!ciStringEqual(getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                request = createIncreaseVarRequest(parentBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                                parentBank = NULL;
                        }
                        else
                            parentBank = NULL;
                    }
                    catch (NotFoundException& e)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " PointResponse not found. CapBank: "<< parentBank->getPaoName() <<  " pointId: " << point->getPointId() << endl;
                    }
                }
                else
                    parentBank = NULL;
            }

            //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {
                for (long i = 0; i < _cccapbanks.size(); i++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                        currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                    {

                        if (point->getDeviceId() != currentCapBank->getPaoId())
                        {
                            try
                            {
                                PointResponse pResponse = currentCapBank->getPointResponse(point);

                                if ( (point->getValue() - pResponse.getDelta() <= point->getUpperBandwidth() &&
                                      point->getValue() - pResponse.getDelta() >= point->getLowerBandwidth() ) ||
                                      pResponse.getDelta() == 0 ||
                                      point->getValue() - pResponse.getDelta() > point->getLowerBandwidth() )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " Attempting to Decrease Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<currentCapBank->getPaoName() << endl;
                                    }

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getDeviceId()<<"/"<<point->getPointId()<<" CapBank: "<<currentCapBank->getPaoName() <<" selected to Open" << endl;
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if (areOtherMonitorPointResponsesOk(point->getPointId(), currentCapBank, CtiCCCapBank::Open))
                                    {
                                        double controlValue = (!ciStringEqual(getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                        string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                        request = createIncreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                        updatePointResponsePreOpValues(currentCapBank);
                                        bestBank = currentCapBank;
                                    }
                                }
                            }
                            catch (NotFoundException& e)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " PointResponse not found. CapBank: "<< currentCapBank->getPaoName() <<  " pointId: " << point->getPointId() << endl;
                            }
                        }
                        if (request != NULL)
                        {
                            bestBank = currentCapBank;
                            break;
                        }
                    }
                }
            }
            //3.  If there are no banks avail which put UpperBW > mp->value > LowerBW...just settle for bestFit..
            if (request == NULL)
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " MULTIVOLT: No Banks Available to Open on Feeder: "<<getPaoName() << endl;
                }
            }
       }


        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(true);
            setLastCapBankControlledDeviceId( bestBank->getPaoId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }

            retVal = true;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return retVal;

}

bool CtiCCFeeder::areOtherMonitorPointResponsesOk(long mPointID, CtiCCCapBank* potentialCap, int action)
{
    bool retVal = true;

    //action = 0 --> open
    //action = 1 --> close

    for (long i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPointPtr otherPoint = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
        if (otherPoint->getPointId() != mPointID)
        {
            for each (const PointResponse& pResponse in potentialCap->getPointResponses())
            {
                if (otherPoint->getPointId() == pResponse.getPointId())
                {
                    if (action) //CLOSE
                    {
                        if (pResponse.getDelta() != 0)
                        {
                            if (otherPoint->getValue() + pResponse.getDelta() > otherPoint->getUpperBandwidth() )
                                //||otherPoint->getValue() + pResponse->getDelta() < otherPoint->getLowerBandwidth())
                            {

                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: "<<getPaoName()<<" CapBank: "<<potentialCap->getPaoName() << endl;
                                    dout << CtiTime() << " MULTIVOLT: otherPoint: "<<otherPoint->getPointId()<<" "<<otherPoint->getDeviceId()<<" Value: "<<otherPoint->getValue()<<" Delta: "<<pResponse.getDelta()<<" pResponse: "<<pResponse.getPointId()<<" "<<pResponse.getDeviceId() << endl;
                                }
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
                    else // OPEN
                    {
                        if (pResponse.getDelta() != 0)
                        {
                            if (//otherPoint->getValue() - pResponse->getDelta() > otherPoint->getUpperBandwidth() ||
                                otherPoint->getValue() - pResponse.getDelta() < otherPoint->getLowerBandwidth())
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: "<<getPaoName()<<" CapBank: "<<potentialCap->getPaoName() << endl;
                                    dout << CtiTime() << " MULTIVOLT: otherPoint: "<<otherPoint->getPointId()<<" "<<otherPoint->getDeviceId()<<" Value: "<<otherPoint->getValue()<<" Delta: "<<pResponse.getDelta()<<" pResponse: "<<pResponse.getPointId()<<" "<<pResponse.getDeviceId() << endl;
                                }
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

bool CtiCCFeeder::areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr oorPoint)
{
    bool retVal = false;
    try
    {
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
            if (point->getValue() >= point->getLowerBandwidth() &&
                point->getValue() <= point->getUpperBandwidth() )
            {
                if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " MULTIVOLT: Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getDeviceId()<<" is inside limits.  Current value: "<<point->getValue() << endl;
                }
                retVal = true;
            }
            else
            {

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " ** WARNING ** Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getDeviceId()<<" is OUTSIDE limits.  Current value: "<<point->getValue() << endl;
                }
                *oorPoint = *point;
                retVal = false;
                break;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return retVal;
}

void CtiCCFeeder::updatePointResponsePreOpValues(CtiCCCapBank* capBank)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Updating POINT RESPONSE PREOPVALUES for CapBank: " <<capBank->getPaoName() << endl;
        dout << CtiTime() << " Device ID: " << capBank->getPaoName() << " has " << capBank->getPointResponses().size() << " point responses" << endl;
    }

    for (int i = 0; i < _multipleMonitorPoints.size(); i++)
    {
        CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];

        try
        {
            if (capBank->updatePointResponsePreOpValue(point->getPointId(),point->getValue()))
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " Device ID: " << capBank->getPaoName() << " Point ID: " << point->getPointId( )<< " Value: " << point->getValue() << endl;
            }
        }
        catch (NotFoundException& e)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Error Updating PreOpValue for deltas. PointId not found: " << point->getPointId() << endl;
        }
    }
}


void CtiCCFeeder::updatePointResponseDeltas()
{

    try
    {

        for(long i=0;i<_cccapbanks.size();i++)
        {
           CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

           if (currentCapBank->getPaoId() == getLastCapBankControlledDeviceId())
           {
               if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " MULTIVOLT: Updating POINT RESPONSE DELTAS for CapBank: " <<currentCapBank->getPaoName() << endl;
               }
               for (int j = 0; j < _multipleMonitorPoints.size(); j++)
               {
                    CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[j];
                    try
                    {
                       currentCapBank->updatePointResponseDelta(point);
                    }
                    catch (NotFoundException& e)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " Error Updating delta value. PointId not found: " << point->getPointId() << endl;
                    }
               }
               break;
           }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

bool CtiCCFeeder::areAllMonitorPointsNewEnough(const CtiTime& currentDateTime)
{
    bool retVal = false;

    try
    {
        //if ( isScanFlagSet() && currentDateTime >= getMonitorPointScanTime() - (_SCAN_WAIT_EXPIRE) )  //T1 Expired.. Force Process
        if ( isScanFlagSet() && currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE *60) )  //T1 Expired.. Force Process
        {
            for (int i = 0; i < _multipleMonitorPoints.size(); i++)
            {
                CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
                if (point->getScanInProgress())
                {
                    point->setScanInProgress(false);
                }
            }
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " ALL MONITOR POINTS ARE NEW ENOUGH on Feeder: " <<getPaoName() << endl;
            }
            retVal = true;
        }
        else
        {
            for (int i = 0; i < _multipleMonitorPoints.size(); i++)
            {
                CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
                if (point->getTimeStamp().seconds() > (getLastOperationTime().seconds() - 30) &&
                    point->getTimeStamp().seconds() + _POINT_AGE <= currentDateTime.seconds())
                {
                    retVal = true;
                    if (point->getScanInProgress())
                    {
                        point->setScanInProgress(false);
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
                if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " ALL MONITOR POINTS ARE NEW ENOUGH on Feeder: " <<getPaoName() << endl;
                }
                bool scanInProgress = false;
                for (int i = 0; i < _multipleMonitorPoints.size(); i++)
                {
                    CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
                    if (point->getScanInProgress())
                    {
                        scanInProgress = true;

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
    return retVal;
}

unsigned long CtiCCFeeder::getMonitorPointScanTime()
{

    return CtiTime().seconds();

}

bool CtiCCFeeder::isScanFlagSet()
{
    return (_preOperationMonitorPointScanFlag || _postOperationMonitorPointScanFlag);
}

bool CtiCCFeeder::scanAllMonitorPoints()
{
    bool retVal = false;
    try
    {

        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPointPtr point = (CtiCCMonitorPointPtr)_multipleMonitorPoints[i];
            if (point->isScannable() && !point->getScanInProgress())
            {
                for (long j = 0; j < _cccapbanks.size();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
                    if (currentCapBank->getPaoId() == point->getDeviceId())
                    {
                        CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );

                        if(pAltRate)
                        {
                            pAltRate->insert(-1);                       // token, not yet used.
                            pAltRate->insert( currentCapBank->getControlDeviceId() );       // Device to poke.

                            pAltRate->insert( -1 );                      // Seconds since midnight, or NOW if negative.

                            pAltRate->insert( 0 );                      // Duration of zero should cause 1 scan.

                            CtiCapController::getInstance()->sendMessageToDispatch(pAltRate);
                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " MULTIVOLT: Requesting scans at the alternate scan rate for " << currentCapBank->getPaoName() << endl;
                            }
                            //CtiCapController::getInstance()->sendMessageToDispatch(createPorterRequestMsg(currentCapBank->getControlDeviceId(), "scan general"));
                            point->setScanInProgress(true);
                            retVal = true;
                        }
                        break;
                    }
                }
            }
            {
                if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " MULTIVOLT: monPoint: "<<point->getPointId()<< " Scannable? " << point->isScannable() << "ScanInProgress? "<<point->getScanInProgress() << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    //set MonitorPointScanTime
    return retVal;
}

CtiCCFeeder& CtiCCFeeder::addAllFeederPointsToMsg(std::set<long>& pointAddMsg)
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

    return *this;
}




/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCFeeder::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( _dirty )
    {
        if( !_insertDynamicDataFlag )
        {
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_verificationFlag?'Y':'N');
            addFlags[1] = (_performingVerificationFlag?'Y':'N');
            addFlags[2] = (_verificationDoneFlag?'Y':'N');
            addFlags[3] = (_preOperationMonitorPointScanFlag?'Y':'N');
            addFlags[4] = (_operationSentWaitFlag?'Y':'N');
            addFlags[5] = (_postOperationMonitorPointScanFlag?'Y':'N');
            addFlags[6] = (_waitForReCloseDelayFlag?'Y':'N');
            addFlags[7] = (_peakTimeFlag?'Y':'N');
            addFlags[8] = (_maxDailyOpsHitFlag?'Y':'N');
            addFlags[9] = (_ovUvDisabledFlag?'Y':'N');
            addFlags[10] = (_correctionNeededNoBankAvailFlag?'Y':'N');
            addFlags[11] = (_likeDayControlFlag?'Y':'N');
            addFlags[12] = (_lastVerificationMsgSentSuccessful?'Y':'N');

            _additionalFlags = char2string(*addFlags);
            _additionalFlags.append(char2string(*(addFlags+1)));
            _additionalFlags.append(char2string(*(addFlags+2)));
            _additionalFlags.append(char2string(*(addFlags+3)));
            _additionalFlags.append(char2string(*(addFlags+4)));
            _additionalFlags.append(char2string(*(addFlags+5)));
            _additionalFlags.append(char2string(*(addFlags+6)));
            _additionalFlags.append(char2string(*(addFlags+7)));
            _additionalFlags.append(char2string(*(addFlags+8)));
            _additionalFlags.append(char2string(*(addFlags+9)));
            _additionalFlags.append(char2string(*(addFlags+10)));
            _additionalFlags.append(char2string(*(addFlags+11)));
            _additionalFlags.append(char2string(*(addFlags+12)));
            _additionalFlags.append("NNNNNNN");

            static const string updateSql = "update dynamicccfeeder set "
                                            "currentvarpointvalue = ?, currentwattpointvalue = ?, "
                                            "newpointdatareceivedflag = ?, lastcurrentvarupdatetime = ?, "
                                            "estimatedvarpointvalue = ?, currentdailyoperations = ?, "
                                            "recentlycontrolledflag = ?, lastoperationtime = ?, "
                                            "varvaluebeforecontrol = ?, lastcapbankdeviceid = ?, "
                                            "busoptimizedvarcategory = ?, busoptimizedvaroffset = ?, "
                                            "ctitimestamp = ?, powerfactorvalue = ?, kvarsolution = ?, "
                                            "estimatedpfvalue = ?, currentvarpointquality = ?, waivecontrolflag = ?, "
                                            "additionalflags = ?, currentvoltpointvalue = ?, eventseq = ?, "
                                            "currverifycbid = ?, currverifycborigstate = ?, currentwattpointquality = ?, "
                                            "currentvoltpointquality = ?, ivcontroltot = ?, ivcount = ?, "
                                            "iwcontroltot = ?, iwcount = ?, phaseavalue = ?, phasebvalue = ?, "
                                            "phasecvalue = ?, lastwattpointtime = ?, lastvoltpointtime = ?, "
                                            "retryindex = ?, phaseavaluebeforecontrol = ?, "
                                            "phasebvaluebeforecontrol = ?, phasecvaluebeforecontrol = ?"
                                            " where feederid = ?";

            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << (string)(_newpointdatareceivedflag?"Y":"N")
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << (string)(_recentlycontrolledflag?"Y":"N")
            << _lastoperationtime
            << _varvaluebeforecontrol
            << _lastcapbankcontrolleddeviceid
            << _busoptimizedvarcategory
            << _busoptimizedvaroffset
            << currentDateTime
            << _powerfactorvalue
            << _kvarsolution
            << _estimatedpowerfactorvalue
            << _currentvarpointquality
            << (string)(_waivecontrolflag?"Y":"N")
            << _additionalFlags
            << _currentvoltloadpointvalue
            << _eventSeq
            << _currentVerificationCapBankId
            << _currentCapBankToVerifyAssumedOrigState
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
            << _retryIndex
            << _phaseAvalueBeforeControl
            << _phaseBvalueBeforeControl
            << _phaseCvalueBeforeControl
            << getPaoId();

            if(updater.execute())    // No error occured!
            {
                _dirty = false;
            }
            else
            {
                _dirty = true;
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
                dout << CtiTime() << " - Inserted Feeder into DynamicCCFeeder: " << getPaoName() << endl;
            }
            static const string inserterSql = "insert into dynamicccfeeder values ( "
                                              "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                              "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                              "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                              "?, ?, ?, ?, ?, ?, ?, ?, ?)";
            static const string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            Cti::Database::DatabaseWriter dbInserter(conn, inserterSql);

            dbInserter << getPaoId()
            << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << (string)(_newpointdatareceivedflag?"Y":"N")
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << (string)(_recentlycontrolledflag?"Y":"N")
            << _lastoperationtime
            << _varvaluebeforecontrol
            << _lastcapbankcontrolleddeviceid
            << _busoptimizedvarcategory
            << _busoptimizedvaroffset
            << currentDateTime
            << _powerfactorvalue
            << _kvarsolution
            << _estimatedpowerfactorvalue
            << _currentvarpointquality
            << (string)(_waivecontrolflag?"Y":"N")
            << addFlags
            << _currentvoltloadpointvalue
            << _eventSeq
            << _currentVerificationCapBankId
            << _currentCapBankToVerifyAssumedOrigState
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
            << _retryIndex
            << _phaseAvalueBeforeControl
            << _phaseBvalueBeforeControl
            << _phaseCvalueBeforeControl;


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
                _insertDynamicDataFlag = false;
                _dirty = false;
            }
            else
            {
                _dirty = true;
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
        getOriginalParent().dumpDynamicData(conn, currentDateTime);
        getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCFeeder::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);

    double temppowerfactorvalue = _powerfactorvalue;
    double tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue;

    if( _powerfactorvalue > 1 )
    {
        temppowerfactorvalue = _powerfactorvalue - 2;
    }
    if( _estimatedpowerfactorvalue > 1 )
    {
        tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue - 2;
    }

    // make strategy values returned through getPeak...()/getOffPeak...() good for IVVC too.
    getStrategy()->setPeakTimeFlag(_peakTimeFlag);

    ostrm   << _parentId
            << getStrategy()->getMaxDailyOperation()
            << getStrategy()->getMaxOperationDisableFlag()
            << _currentvarloadpointid
            << _currentvarloadpointvalue
            << _currentwattloadpointid
            << _currentwattloadpointvalue
            << _maplocationid
            << _displayorder
            << _newpointdatareceivedflag
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointid
            << _estimatedvarloadpointvalue
            << _dailyoperationsanalogpointid
            << _powerfactorpointid
            << _estimatedpowerfactorpointid
            << _currentdailyoperations
            << (_recentlycontrolledflag || _performingVerificationFlag)
            << _lastoperationtime
            << _varvaluebeforecontrol
            << temppowerfactorvalue
            << tempestimatedpowerfactorvalue
            << _currentvarpointquality
            << _waivecontrolflag
            << getStrategy()->getControlUnits()
            << _decimalPlaces
            << _peakTimeFlag
            << getStrategy()->getPeakLag()
            << getStrategy()->getOffPeakLag()
            << getStrategy()->getPeakLead()
            << getStrategy()->getOffPeakLead()
            << _currentvoltloadpointid
            << _currentvoltloadpointvalue
            << _currentwattpointquality
            << _currentvoltpointquality
            << _targetvarvalue
            << _solution
            << _ovUvDisabledFlag
            << getStrategy()->getPeakPFSetPoint()
            << getStrategy()->getOffPeakPFSetPoint()
            << getStrategy()->getControlMethod()
            << _phaseAvalue
            << _phaseBvalue
            << _phaseCvalue
            << _likeDayControlFlag
            << _usePhaseData
            << _originalParent.getOriginalParentId();


    ostrm << _cccapbanks.size();
    for(long i=0;i<_cccapbanks.size();i++)
    {
        ostrm << (CtiCCCapBank*)_cccapbanks[i];
    }
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::operator=(const CtiCCFeeder& right)
{
    Controllable::operator=(right);

    if( this != &right )
    {
        _parentId = right._parentId;

        _currentvarloadpointid = right._currentvarloadpointid;
        _currentvarloadpointvalue = right._currentvarloadpointvalue;
        _currentwattloadpointid = right._currentwattloadpointid;
        _currentwattloadpointvalue = right._currentwattloadpointvalue;
        _currentvoltloadpointid = right._currentvoltloadpointid;
        _currentvoltloadpointvalue = right._currentvoltloadpointvalue;
        _maplocationid = right._maplocationid;
        _displayorder = right._displayorder;

        _newpointdatareceivedflag = right._newpointdatareceivedflag;
        _lastcurrentvarpointupdatetime = right._lastcurrentvarpointupdatetime;
        _estimatedvarloadpointid = right._estimatedvarloadpointid;
        _estimatedvarloadpointvalue = right._estimatedvarloadpointvalue;
        _dailyoperationsanalogpointid = right._dailyoperationsanalogpointid;
        _powerfactorpointid = right._powerfactorpointid;
        _estimatedpowerfactorpointid = right._estimatedpowerfactorpointid;
        _currentdailyoperations = right._currentdailyoperations;
        _recentlycontrolledflag = right._recentlycontrolledflag;
        _lastoperationtime = right._lastoperationtime;
        _varvaluebeforecontrol = right._varvaluebeforecontrol;
        _lastcapbankcontrolleddeviceid = right._lastcapbankcontrolleddeviceid;
        _powerfactorvalue = right._powerfactorvalue;
        _kvarsolution = right._kvarsolution;
        _estimatedpowerfactorvalue = right._estimatedpowerfactorvalue;
        _currentvarpointquality = right._currentvarpointquality;
        _currentwattpointquality = right._currentwattpointquality;
        _currentvoltpointquality = right._currentvoltpointquality;
        _waivecontrolflag = right._waivecontrolflag;
        _additionalFlags = right._additionalFlags;
        _parentControlUnits = right._parentControlUnits;
        _parentName = right._parentName;
        _decimalPlaces = right._decimalPlaces;
        _peakTimeFlag = right._peakTimeFlag;
        _eventSeq = right._eventSeq;
        _multiMonitorFlag = right._multiMonitorFlag;

        _verificationFlag = right._verificationFlag;
        _performingVerificationFlag = right._performingVerificationFlag;
        _verificationDoneFlag = right._verificationDoneFlag;
        _porterRetFailFlag = right._porterRetFailFlag;
        _currentVerificationCapBankId = right._currentVerificationCapBankId;
        _currentCapBankToVerifyAssumedOrigState = right._currentCapBankToVerifyAssumedOrigState;

        _preOperationMonitorPointScanFlag = right._preOperationMonitorPointScanFlag;
        _operationSentWaitFlag = right._operationSentWaitFlag;
        _postOperationMonitorPointScanFlag = right._postOperationMonitorPointScanFlag;
        _waitForReCloseDelayFlag = right._waitForReCloseDelayFlag;
        _maxDailyOpsHitFlag = right._maxDailyOpsHitFlag;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _correctionNeededNoBankAvailFlag = right._correctionNeededNoBankAvailFlag;
        _likeDayControlFlag = right._likeDayControlFlag;
        _lastVerificationMsgSentSuccessful = right._lastVerificationMsgSentSuccessful;

        _targetvarvalue = right._targetvarvalue;
        _solution = right._solution;
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
         _retryIndex = right._retryIndex;
        regression = right.regression;
        regressionA = right.regressionA;
        regressionB = right.regressionB;
        regressionC = right.regressionC;
        delete_container(_cccapbanks);
        _cccapbanks.clear();
        for(long i=0;i<right._cccapbanks.size();i++)
        {
            _cccapbanks.insert(((CtiCCCapBank*)right._cccapbanks[i])->replicate());
        }

        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;
        _originalParent = right._originalParent;
        _insertDynamicDataFlag = right._insertDynamicDataFlag;
        _dirty = right._dirty;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCFeeder* CtiCCFeeder::replicate() const
{
    return(new CtiCCFeeder(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/

void CtiCCFeeder::restore(Cti::RowReader& rdr)
{
    CtiTime currentDateTime;
    CtiTime dynamicTimeStamp;
    string tempBoolString;

    rdr["currentvarloadpointid"] >> _currentvarloadpointid;
    rdr["currentwattloadpointid"] >> _currentwattloadpointid;
    rdr["maplocationid"] >> _maplocationid;

    //rdr["displayorder"] >> _displayorder;
    rdr["currentvoltloadpointid"] >> _currentvoltloadpointid;
    rdr["multiMonitorControl"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _multiMonitorFlag = (tempBoolString=="y");
    rdr["usephasedata"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _usePhaseData = (tempBoolString=="y");
    rdr["phaseb"] >> _phaseBid;
    rdr["phasec"] >> _phaseCid;
    rdr["controlflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _totalizedControlFlag = (tempBoolString=="y");

    setDecimalPlaces(0);

    _displayorder = 0;
    _estimatedvarloadpointid = 0;
    _dailyoperationsanalogpointid = 0;
    _powerfactorpointid = 0;
    _estimatedpowerfactorpointid = 0;

    setNewPointDataReceivedFlag(false);
    setLastCurrentVarPointUpdateTime(gInvalidCtiTime);
    setEstimatedVarLoadPointValue(0.0);
    _currentdailyoperations = 0;
    setRecentlyControlledFlag(false);
    setLastOperationTime(gInvalidCtiTime);
    setVarValueBeforeControl(0.0);
    setLastCapBankControlledDeviceId(0);
    _busoptimizedvarcategory = 1;
    _busoptimizedvaroffset = 0.0;
    _powerfactorvalue = -1000000.0;
    _kvarsolution = 0.0;
    _estimatedpowerfactorvalue = -1000000.0;
    _currentvarpointquality = NormalQuality;
    _waivecontrolflag = false;
    setVerificationFlag(false);
    _additionalFlags = string("NNNNNNNNNNNNNNNNNNNN");
    setPerformingVerificationFlag(false);
    setVerificationDoneFlag(false);
    setPreOperationMonitorPointScanFlag(false);
    setOperationSentWaitFlag(false);
    setPostOperationMonitorPointScanFlag(false);
    setWaitForReCloseDelayFlag(false);
    setMaxDailyOpsHitFlag(false);
    setOvUvDisabledFlag(false);
    setCorrectionNeededNoBankAvailFlag(false);
    setLikeDayControlFlag(false);
    setPeakTimeFlag(false);
    setEventSequence(0);
    setCurrentVerificationCapBankId(-1);
    setCurrentVerificationCapBankState(0);
    _currentwattpointquality = NormalQuality;
    _currentvoltpointquality = NormalQuality;

    setTargetVarValue(0);
    setSolution("IDLE");

    _insertDynamicDataFlag = true;
    _dirty = true;


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
    }

    _currentvarloadpointvalue = 0;
    _currentwattloadpointvalue = 0;
    _currentvoltloadpointvalue = 0;

    setIVControlTot(0);
    setIVCount(0);
    setIWControlTot(0);
    setIWCount(0);
    setIVControl(0);
    setIWControl(0);

    setPhaseAValue(0, currentDateTime);
    setPhaseBValue(0, currentDateTime);
    setPhaseCValue(0, currentDateTime);
    setPhaseAValueBeforeControl(0);
    setPhaseBValueBeforeControl(0);
    setPhaseCValueBeforeControl(0);

    setLastWattPointTime(gInvalidCtiTime);
    setLastVoltPointTime(gInvalidCtiTime);
    setRetryIndex(0);
    _originalParent.setPAOId(getPaoId());
}

void CtiCCFeeder::setDynamicData(Cti::RowReader& rdr)
{

    CtiTime dynamicTimeStamp;
    string tempBoolString;
    rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
    rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
    rdr["newpointdatareceivedflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _newpointdatareceivedflag = (tempBoolString=="y");
    rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
    rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
    rdr["currentdailyoperations"] >> _currentdailyoperations;
    rdr["recentlycontrolledflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _recentlycontrolledflag = (tempBoolString=="y");
    rdr["lastoperationtime"] >> _lastoperationtime;
    rdr["varvaluebeforecontrol"] >> _varvaluebeforecontrol;
    rdr["lastcapbankdeviceid"] >> _lastcapbankcontrolleddeviceid;
    rdr["busoptimizedvarcategory"] >> _busoptimizedvarcategory;
    rdr["busoptimizedvaroffset"] >> _busoptimizedvaroffset;
    rdr["ctitimestamp"] >> dynamicTimeStamp;
    rdr["powerfactorvalue"] >> _powerfactorvalue;
    rdr["kvarsolution"] >> _kvarsolution;
    rdr["estimatedpfvalue"] >> _estimatedpowerfactorvalue;
    rdr["currentvarpointquality"] >> _currentvarpointquality;
    rdr["waivecontrolflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _waivecontrolflag = (tempBoolString=="y");
    rdr["additionalflags"] >> _additionalFlags;
    rdr["currentvoltpointvalue"] >> _currentvoltloadpointvalue;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
    _verificationFlag = (_additionalFlags[0]=='y');
    _performingVerificationFlag = (_additionalFlags[1]=='y');
    _verificationDoneFlag = (_additionalFlags[2]=='y');
    _preOperationMonitorPointScanFlag = (_additionalFlags[3]=='y');
    _operationSentWaitFlag = (_additionalFlags[4]=='y');
    _postOperationMonitorPointScanFlag = (_additionalFlags[5]=='y');
    _waitForReCloseDelayFlag = (_additionalFlags[6]=='y');
    _peakTimeFlag = (_additionalFlags[7]=='y');
    _maxDailyOpsHitFlag = (_additionalFlags[8]=='y');
    _ovUvDisabledFlag = (_additionalFlags[9]=='y');
    _correctionNeededNoBankAvailFlag = (_additionalFlags[10]=='y');
    _likeDayControlFlag = (_additionalFlags[11]=='y');
    _lastVerificationMsgSentSuccessful = (_additionalFlags[12]=='y');
    rdr["eventSeq"] >> _eventSeq;
    rdr["currverifycbid"] >> _currentVerificationCapBankId;
    rdr["currverifycborigstate"] >> _currentCapBankToVerifyAssumedOrigState;
    rdr["currentwattpointquality"] >> _currentwattpointquality;
    rdr["currentvoltpointquality"] >> _currentvoltpointquality;

    rdr["ivcontroltot"] >> _iVControlTot;
    rdr["ivcount"] >> _iVCount;
    rdr["iwcontroltot"] >> _iWControlTot;
    rdr["iwcount"] >> _iWCount;

    rdr["phaseavalue"] >> _phaseAvalue;
    rdr["phasebvalue"] >> _phaseBvalue;
    rdr["phasecvalue"] >> _phaseCvalue;

    rdr["lastwattpointtime"] >> _lastWattPointTime;
    rdr["lastvoltpointtime"] >> _lastVoltPointTime;
    rdr["retryindex"] >> _retryIndex;

    rdr["phaseavaluebeforecontrol"] >> _phaseAvalueBeforeControl;
    rdr["phasebvaluebeforecontrol"] >> _phaseBvalueBeforeControl;
    rdr["phasecvaluebeforecontrol"] >> _phaseCvalueBeforeControl;

    _originalParent.restore(rdr);

    _insertDynamicDataFlag = false;
    _dirty = false;

}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the string representation of a double
---------------------------------------------------------------------------*/
string CtiCCFeeder::doubleToString(double doubleVal, long decimalPlaces)
{
    char tempchar[80] = "";
    string retString = string("");
    _snprintf(tempchar,80,"%.*f",decimalPlaces, doubleVal);
    retString += tempchar;

    return retString;
}

void CtiCCFeeder::deleteCCCapBank(long capBankId)
{
    CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
    CtiCCCapBank_SVector::iterator itr = ccCapBanks.begin();
    while(itr != ccCapBanks.end())
    {
        CtiCCCapBank *capBank = *itr;
        if (capBank->getPaoId() == capBankId)
        {
            itr = getCCCapBanks().erase(itr);
            break;
        }else
            ++itr;

    }
    return;
}

bool CtiCCFeeder::checkMaxDailyOpCountExceeded(CtiMultiMsg_vec& pointChanges)
{
    bool retVal = false;
    if( getStrategy()->getMaxDailyOperation() > 0 &&
        ( _currentdailyoperations == getStrategy()->getMaxDailyOperation()  ||
         (!getMaxDailyOpsHitFlag() && _currentdailyoperations > getStrategy()->getMaxDailyOperation()) ) )//only send once
    {

        string text = ("Feeder Exceeded Max Daily Operations");
        string additional = ("Feeder: ");
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
            pointChanges.push_back(pSig);

        }
        setMaxDailyOpsHitFlag(true);

        retVal = true;
    }

    if( getStrategy()->getMaxOperationDisableFlag() && getMaxDailyOpsHitFlag() )
    {
        bool endOfDayOverride = false;

        if (getStrategy()->getEndDaySettings().compare("Trip") == 0)
        {
            endOfDayOverride = CtiCCSubstationBusStore::getInstance()->isAnyBankClosed(getPaoId(),Cti::CapControl::Feeder);
        }
        else if (getStrategy()->getEndDaySettings().compare("Close") == 0)
        {
            endOfDayOverride = CtiCCSubstationBusStore::getInstance()->isAnyBankOpen(getPaoId(),Cti::CapControl::Feeder);
        }

        if (endOfDayOverride == false)
        {
            string text = string("Feeder Disabled");
            string additional = string("Feeder: ");
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
                pointChanges.push_back(pSig);
            }
            CtiCCSubstationBusStore::getInstance()->UpdatePaoDisableFlagInDB(this, true);
            retVal = false;
        }
    }

    return retVal;
}

string CtiCCFeeder::createPhaseVarText(double aValue,double bValue, double cValue, float multiplier)
{
    string text = ("");
    text += CtiNumStr(aValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(bValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(cValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr((aValue+bValue+cValue)*multiplier, 2).toString();
    return text;
}
string CtiCCFeeder::createPhaseRatioText(double aValue,double bValue, double cValue, float multiplier)
{
    string text = ("");
    text += CtiNumStr(aValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(bValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(cValue*multiplier, 2).toString();
    text += " : ";
    double totalRatio = (aValue+bValue+cValue) / 3;
    text += CtiNumStr(totalRatio*multiplier, 2).toString();
    return text;
}

string CtiCCFeeder::createVarText(double aValue,float multiplier)
{
    string text = ("");
    text += CtiNumStr(aValue*multiplier, 2).toString();
    return text;
}

string CtiCCFeeder::createPhaseControlStatusUpdateText(string capControlStatus, double varAValue,double varBValue, double varCValue,
                                                  double ratioA, double ratioB, double ratioC)
{
    string text = ("");

    text = string("Var: ");
    text += CtiNumStr(varAValue, 2).toString();
    text += "/";
    text += CtiNumStr(varBValue, 2).toString();
    text += "/";
    text += CtiNumStr(varCValue, 2).toString();
    text += "  ( ";
    text += CtiNumStr(ratioA*100.0, 2).toString();
    text += "% / ";
    text += CtiNumStr(ratioB*100.0, 2).toString();
    text += "% / ";
    text += CtiNumStr(ratioC*100.0, 2).toString();

    text += "% change), ";
    text += capControlStatus;

    return text;
}

string CtiCCFeeder::createControlStatusUpdateText(string capControlStatus, double varAValue, double ratioA)
{
    string text = ("");
    text = string("Var: ");
    text += CtiNumStr(varAValue, 2).toString();
    text += "  ( ";
    text += CtiNumStr(ratioA*100.0, 2).toString();
    text += "% change), ";
    text += capControlStatus;

    return text;
}
string CtiCCFeeder::createTextString(const string& controlMethod, int control, double controlValue, double monitorValue)//, long decimalPlaces)
{
    string text = ("");
    switch (control)
    {
        case 0:
            {
                text += "Open Sent, ";
            }
            break;
        case 1:
            {
                text += "Close Sent, ";
            }
            break;
        case 2:
            {
                text += "Resending Open, ";
            }
            break;
        case 3:
            {
                text += "Resending Close, ";
            }
            break;
        case 4:
            {
                text += "Flip sent, ";
            }
        default:
            break;

    }
    if (ciStringEqual(getParentControlUnits(),ControlStrategy::VoltsControlUnit))
    {
        if (ciStringEqual(controlMethod,ControlStrategy::SubstationBusControlMethod))
            text += "SubBus-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::BusOptimizedFeederControlMethod))
            text += "BusOp-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::IndividualFeederControlMethod))
            text += "IndvFdr-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::ManualOnlyControlMethod))
            text += "Manual-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
            text += "TimeOfDay-Volt: ";
        else
            text += "No Method Defined? Volt: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    else if (ciStringEqual(getParentControlUnits(), ControlStrategy::KVarControlUnit) ||
             ciStringEqual(getParentControlUnits(), ControlStrategy::PFactorKWKVarControlUnit) ||
             ciStringEqual(getParentControlUnits(), ControlStrategy::PFactorKWKQControlUnit))
    {
        if (ciStringEqual(controlMethod,ControlStrategy::SubstationBusControlMethod))
        {
            text += "SubBus-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            CtiCCSubstationBusPtr bus = CtiCCSubstationBusStore::getInstance()->findSubBusByPAObjectID(getParentId());
            if (bus != NULL)
            {
                if (bus->getUsePhaseData())
                {
                    text += doubleToString(bus->getPhaseAValue(), getDecimalPlaces());
                    text += " : ";
                    text += doubleToString(bus->getPhaseBValue(), getDecimalPlaces());
                    text += " : ";
                    text += doubleToString(bus->getPhaseCValue(), getDecimalPlaces());
                }
                else
                    text += doubleToString(monitorValue, getDecimalPlaces());
            }
            else
                text += doubleToString(monitorValue, getDecimalPlaces());
        }

        else if (ciStringEqual(controlMethod,ControlStrategy::BusOptimizedFeederControlMethod))
        {
            text += "BusOp-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            if (getUsePhaseData())
            {
                text += doubleToString(getPhaseAValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseBValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseCValue(), getDecimalPlaces());
            }
            else
                text += doubleToString(monitorValue, getDecimalPlaces());

        }
        else if (ciStringEqual(controlMethod,ControlStrategy::IndividualFeederControlMethod))
        {
            text += "IndvFdr-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            if (getUsePhaseData())
            {
                text += doubleToString(getPhaseAValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseBValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseCValue(), getDecimalPlaces());
            }
            else
                text += doubleToString(monitorValue, getDecimalPlaces());
        }
        else if (ciStringEqual(controlMethod,ControlStrategy::ManualOnlyControlMethod))
        {
            text += "Manual-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
        else if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
        {
            text += "TimeOfDay-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
        else
        {
            text += "No Method Defined? Var:";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
    }
    else if (ciStringEqual(getParentControlUnits(), ControlStrategy::MultiVoltControlUnit))
    {
        if (ciStringEqual(controlMethod, ControlStrategy::SubstationBusControlMethod))
            text += "SubBus-MultiVolt: ";
        else if (ciStringEqual(controlMethod, ControlStrategy::BusOptimizedFeederControlMethod))
            text += "BusOp-MultiVolt: ";
        else if (ciStringEqual(controlMethod, ControlStrategy::IndividualFeederControlMethod))
            text += "IndvFdr-MultiVolt: ";
        else if (ciStringEqual(controlMethod, ControlStrategy::ManualOnlyControlMethod))
            text += "Manual-MultiVolt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
            text += "TimeOfDay-MultiVolt: ";
        else
            text += "No Method Defined? MultiVolt: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    if (ciStringEqual(getParentControlUnits(),ControlStrategy::IntegratedVoltVarControlUnit))
    {
        text += "IVVC: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    else
    {
        if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
        {
            text += "TimeOfDay-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
    }
    return text;

}


CtiCCCapBank* CtiCCFeeder::getMonitorPointParentBank(CtiCCMonitorPointPtr point)
{

    for (long i = 0; i < _cccapbanks.size(); i++)
    {
        CtiCCCapBankPtr cap = (CtiCCCapBankPtr)_cccapbanks[i];
        if (point->getDeviceId() == cap->getPaoId())
        {
            return cap;
        }
    }
    return NULL;
}

bool CtiCCFeeder::checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = false;
    CtiRequestMsg* request = NULL;

    map <long, long> controlid_action_map;
    controlid_action_map.clear();

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
    CtiCCSubstationBusStore::getInstance()->reloadMapOfBanksToControlByLikeDay(0, getPaoId(), &controlid_action_map,lastSendTime, fallBackConst);
    std::map <long, long>::iterator iter = controlid_action_map.begin();
    while (iter != controlid_action_map.end())
    {
        {
            int capCount = 0;
            long ptId = iter->first;

            PointIdToCapBankMultiMap::iterator bankIter, end;
            if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(iter->first, bankIter, end))
            {
                CtiCCCapBankPtr bank = bankIter->second;
                if (bank->getParentId() == getPaoId())
                {
                    request = createForcedVarRequest(bank, pointChanges, ccEvents, iter->second, "LikeDay Control");

                    if( request != NULL )
                    {
                        createForcedVarConfirmation(bank, pointChanges, ccEvents, "LikeDay Control");

                        retVal = true;
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                    }
                }
            }
        }
        iter = controlid_action_map.erase(iter);
    }

    return retVal;
}

bool CtiCCFeeder::isDataOldAndFallBackNecessary(string controlUnits)
{
    bool retVal = false;
    CtiTime timeNow = CtiTime();
    string feederControlUnits = controlUnits;

    if (!ciStringEqual(getStrategy()->getStrategyName(),"(none)"))
    {
        feederControlUnits = getStrategy()->getControlUnits();
    }

    if (!getDisableFlag())
    {
        if (getStrategy()->getLikeDayFallBack())
        {
            if ( ciStringEqual(feederControlUnits, ControlStrategy::VoltsControlUnit))
            {
                 if ((getCurrentVoltLoadPointId() && getCurrentVarLoadPointId()) &&
                     (timeNow > getLastVoltPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                      timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT))
                {
                     retVal = true;
                }
            }
            else if ( ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKVarControlUnit))
            {
                 if ((getCurrentWattLoadPointId() && getCurrentVarLoadPointId()) &&
                     (timeNow > getLastWattPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                      timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT))
                {
                     retVal = true;
                }
            }
            else
            {
                if (getCurrentVarLoadPointId() &&
                    timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT)
                {
                    retVal = true;
                }
            }
        }
    }

    return retVal;
}

bool CtiCCFeeder::checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC)
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

bool CtiCCFeeder::areAllPhasesSuccess(double ratioA, double ratioB, double ratioC, double confirmPercent)
{
    return ( isResponseSuccess(ratioA, confirmPercent) &&
             isResponseSuccess(ratioB, confirmPercent) &&
             isResponseSuccess(ratioC, confirmPercent) );
}
bool CtiCCFeeder::areAllPhasesQuestionable(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    return (isResponseQuestionable(ratioA, confirmPercent, failPercent) &&
            isResponseQuestionable(ratioB, confirmPercent, failPercent) &&
            isResponseQuestionable(ratioC, confirmPercent, failPercent) );

}
bool CtiCCFeeder::isResponseQuestionable(double ratio, double confirmPercent, double failPercent)
{
    return ( ! ( isResponseSuccess(ratio, confirmPercent) || isResponseFail(ratio, failPercent) ) );

}
bool CtiCCFeeder::isResponseFail(double ratio,double failPercent)
{
    return (ratio < failPercent * .01) ;
}
bool CtiCCFeeder::isResponseSuccess(double ratio,double confirmPercent)
{
    return (ratio >= confirmPercent * .01) ;

}
bool CtiCCFeeder::shouldCapBankBeFailed(double ratioA, double ratioB, double ratioC, double failPercent)
{
    bool retVal = false;
    long numOfFails = 0;
    retVal = isAnyPhaseFail(ratioA, ratioB, ratioC, failPercent, numOfFails);
    if( !_USE_PHASE_INDICATORS && numOfFails != 3)
    {
        //if _USE_PHASE_INDICATORS == false, all phases must be failed
        retVal = false;
    }
    return retVal;
}


bool CtiCCFeeder::isAnyPhaseFail(double ratioA, double ratioB, double ratioC, double failPercent, long &numFailedPhases)
{
    numFailedPhases = 0;

    if (isResponseFail(ratioA, failPercent))
    {
        numFailedPhases += 1;
    }
    if (isResponseFail(ratioB, failPercent))
    {
        numFailedPhases += 1;
    }
    if (isResponseFail(ratioC, failPercent))
    {
        numFailedPhases += 1;
    }

    return numFailedPhases != 0;
}

string CtiCCFeeder::getFailedPhasesString(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    string retStr = "";
    long numOfFail = 0;
    if( isAnyPhaseFail(ratioA, ratioB, ratioC, failPercent, numOfFail) )
    {
        if( numOfFail == 3)
        {
            retStr = "ABC";
        }
        else if( numOfFail == 2 )
        {
            if( isResponseFail(ratioA,failPercent) )
            {
                retStr += "A";
            }
            if( isResponseFail(ratioB,failPercent) )
            {
                retStr += "B";
            }
            if( isResponseFail(ratioC,failPercent) )
            {
                retStr += "C";
            }
        }
        else if( numOfFail == 1 )
        {
            if( isResponseSuccess(ratioA, confirmPercent) ||
                isResponseSuccess(ratioB, confirmPercent) ||
                isResponseSuccess(ratioC, confirmPercent) )
            {
                if( isResponseFail(ratioA,failPercent) )
                {
                    retStr += "A";
                }
                else if( isResponseFail(ratioB,failPercent) )
                {
                    retStr += "B";
                }
                else
                {
                    retStr += "C";
                }
            }
            else
            {
                if(isResponseQuestionable(ratioA, confirmPercent, failPercent))
                {
                    retStr += "A";
                }
                if(isResponseQuestionable(ratioB, confirmPercent, failPercent))
                {
                    retStr += "B";
                }
                if(isResponseQuestionable(ratioC, confirmPercent, failPercent))
                {
                    retStr += "C";
                }
            }
        }
    }
    return retStr;
}
string CtiCCFeeder::getQuestionablePhasesString(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    string retStr = "";
    if( isResponseQuestionable(ratioA, confirmPercent, failPercent)  )
    {
        retStr += "A";
    }
    if( isResponseQuestionable(ratioB, confirmPercent, failPercent)  )
    {
        retStr += "B";
    }
    if( isResponseQuestionable(ratioC, confirmPercent, failPercent)  )
    {
        retStr += "C";
    }
    return retStr;
}


string CtiCCFeeder::getPhaseIndicatorString(long capState, double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    string retStr = "(none)";
    if( _USE_PHASE_INDICATORS )
    {
        if (capState == CtiCCCapBank::OpenQuestionable ||
            capState == CtiCCCapBank::CloseQuestionable )
        {
            retStr = getQuestionablePhasesString(ratioA, ratioB, ratioC, confirmPercent, failPercent);
        }
        else if (capState == CtiCCCapBank::OpenFail ||
                 capState == CtiCCCapBank::CloseFail )
        {
            retStr = getFailedPhasesString(ratioA, ratioB, ratioC, confirmPercent, failPercent);
        }
        else
        {
             retStr = "(none)";
        }
    }

    return retStr;
}

void CtiCCFeeder::createCannotControlBankText(string text, string commandString, CtiMultiMsg_vec& ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can Not "<< text << " level for feeder: " << getPaoName()
        << " any further.  All cap banks are already in the "<< commandString <<" state in: " << __FILE__ << " at: " << __LINE__ << endl;

        CtiCCCapBank* currentCapBank = NULL;
        for(int i=0;i<_cccapbanks.size();i++)
        {
            currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            dout << "CapBank: " << currentCapBank->getPaoName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"true":"false") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"true":"false") << endl;
        }
    }
    if (!getCorrectionNeededNoBankAvailFlag())
    {
        setCorrectionNeededNoBankAvailFlag(true);
        string textInfo;
        textInfo = ("Feeder: ");
        textInfo += getPaoName();
        textInfo += " Cannot ";
        textInfo += text;
        textInfo +=" Level.  No Cap Banks Available to " + commandString + ".";

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
    }

}


void CtiCCFeeder::resetVerificationFlags()
{
    setVerificationFlag(false);
    setPerformingVerificationFlag(false);
    setVerificationDoneFlag(false);


    for(long k=0;k<_cccapbanks.size();k++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[k];

        currentCapBank->setVerificationFlag(false);
        currentCapBank->setPerformingVerificationFlag(false);
        currentCapBank->setVerificationDoneFlag(false);
        //wouldn't hurt to set this.
        currentCapBank->setVCtrlIndex(0);
        currentCapBank->setSelectedForVerificationFlag(false);
    }
}

