/*---------------------------------------------------------------------------
        Filename:  ccfeeder.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCFeeder.
                        CtiCCFeeder maintains the state and handles
                        the persistence of feeders for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

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
#include "ccmonitorpoint.h"
#include "mgr_holiday.h"
#include "resolvers.h"
#include "numstr.h"
#include "utility.h"
#include "msg_lmcontrolhistory.h"

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;
extern ULONG _POINT_AGE;
extern ULONG _SCAN_WAIT_EXPIRE;
extern BOOL _RETRY_FAILED_BANKS;
extern BOOL _END_DAY_ON_TRIP;
extern BOOL _LOG_MAPID_INFO;

RWDEFINE_COLLECTABLE( CtiCCFeeder, CTICCFEEDER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCFeeder::CtiCCFeeder()
{
    _porterRetFailFlag = false;
}

CtiCCFeeder::CtiCCFeeder(RWDBReader& rdr)
{
    restore(rdr);
}

CtiCCFeeder::CtiCCFeeder(const CtiCCFeeder& feeder)
{
    operator=(feeder);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCFeeder::~CtiCCFeeder()
{
    _pointIds.clear();
    try
    {
        delete_vector(_cccapbanks);
        _cccapbanks.clear();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getDisableFlag() const
{
    return _disableflag;
}


/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (subBusId) of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getParentId() const
{
    return _parentId;
}

/*---------------------------------------------------------------------------
    getStrategyId

    Returns the strategyID of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getStrategyId() const
{
    return _strategyId;
}
/*-------------------------------------------------------------------------
    getStrategyName

    Returns the strategyName of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getStrategyName() const
{
    return _strategyName;
}
/*---------------------------------------------------------------------------
    getControlMethod

    Returns the controlMethod of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getControlMethod() const
{
    return _controlmethod;
}
/*---------------------------------------------------------------------------
    getMaxDailyOperation

    Returns the MaxDailyOperation limit of the feeder
---------------------------------------------------------------------------*/

LONG CtiCCFeeder::getMaxDailyOperation() const
{
    return _maxdailyoperation;
}
/*---------------------------------------------------------------------------
    getMaxOperationDisableFlag

    Returns the maxOperationDisableFlag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getMaxOperationDisableFlag() const
{
    return _maxoperationdisableflag;
}

    
/*---------------------------------------------------------------------------
    getPeakLag

    Returns the peak lag level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPeakLag() const
{
    return _peaklag;
}

/*---------------------------------------------------------------------------
    getOffPeakLag

    Returns the off peak lag level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getOffPeakLag() const
{
    return _offpklag;
}
/*---------------------------------------------------------------------------
    getPeakLead

    Returns the peak lead level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPeakLead() const
{
    return _peaklead;
}

/*---------------------------------------------------------------------------
    getOffPeakLead

    Returns the off peak lead level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getOffPeakLead() const
{
    return _offpklead;
}

/*---------------------------------------------------------------------------
    getPeakLag

    Returns the peak lag level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPeakVARLag() const
{
    return _peakVARlag;
}

/*---------------------------------------------------------------------------
    getOffPeakLag

    Returns the off peak lag level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getOffPeakVARLag() const
{
    return _offpkVARlag;
}
/*---------------------------------------------------------------------------
    getPeakVARLead

    Returns the peak lead level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPeakVARLead() const
{
    return _peakVARlead;
}

/*---------------------------------------------------------------------------
    getOffPeakVARLead

    Returns the off peak lead level of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getOffPeakVARLead() const
{
    return _offpkVARlead;
}

/*---------------------------------------------------------------------------
    getPeakPFSetPoint

    Returns the peak target power factor of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPeakPFSetPoint() const
{
    return _peakpfsetpoint;
}

/*---------------------------------------------------------------------------
    getOffPeakPFSetPoint

    Returns the off peak target power factor of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getOffPeakPFSetPoint() const
{
    return _offpkpfsetpoint;
}



/*---------------------------------------------------------------------------
    getPeakStartTime

    Returns the PeakStartTime of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getPeakStartTime() const
{
    return _peakstarttime;

}
/*---------------------------------------------------------------------------
    getPeakStopTime

    Returns the PeakStopTime of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getPeakStopTime() const
{
    return _peakstoptime;
}
    
/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentVarLoadPointId() const
{
    return _currentvarloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getCurrentVarLoadPointValue() const
{
    return _currentvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentWattLoadPointId() const
{
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getCurrentWattLoadPointValue() const
{
    return _currentwattloadpointvalue;
}
/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointId

    Returns the current volt load point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentVoltLoadPointId() const
{
    return _currentvoltloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVoltLoadPointValue

    Returns the current volt load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getCurrentVoltLoadPointValue() const
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
    getControlInterval

    Returns the ControlInterval of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getControlInterval() const
{
    return _controlinterval;
}
/*---------------------------------------------------------------------------
    getMaxConfirmTime

    Returns the MaxConfirmTime of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getMaxConfirmTime() const
{
    return _maxconfirmtime;
}
/*---------------------------------------------------------------------------
    getMinConfirmPercent

    Returns the MinConfirmPercent of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getMinConfirmPercent() const
{
    return _minconfirmpercent;
}
/*---------------------------------------------------------------------------
    getFailurePercent

    Returns the FailurePercent of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getFailurePercent() const
{
    return _failurepercent;
}

/*---------------------------------------------------------------------------
    getDaysOfWeek

    Returns the DaysOfWeek of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getDaysOfWeek() const
{
    return _daysofweek;
}
/*---------------------------------------------------------------------------
    getControlUnits

    Returns the ControlUnits of the feeder
---------------------------------------------------------------------------*/
const string& CtiCCFeeder::getControlUnits() const
{
    return _controlunits;
}
/*---------------------------------------------------------------------------
    getControlDelayTime

    Returns the ControlDelayTime of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getControlDelayTime() const
{
    return _controldelaytime;
}
/*---------------------------------------------------------------------------
    getControlSendRetries

    Returns the # of ControlSendRetries of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getControlSendRetries() const
{
    return _controlsendretries;
}
    
/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the display order of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getDisplayOrder() const
{
    return _displayorder;
}

/*---------------------------------------------------------------------------
    getIntegrateFlag

    Returns the IntegrateFlag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getIntegrateFlag() const
{
    return _integrateflag;
}
/*---------------------------------------------------------------------------
    getIntegratePeriod

    Returns the IntegratePeriod of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getIntegratePeriod() const
{
    return _integrateperiod;
}
/*---------------------------------------------------------------------------
    getIVControlTot

    Returns the Integrate Volt/Var Control total of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getIVControlTot() const
{
    return _iVControlTot;
}
/*---------------------------------------------------------------------------
    getIVCount

    Returns the Integrate Volt/Var Control count of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getIVCount() const
{
    return _iVCount;
}


/*---------------------------------------------------------------------------
    getIWControlTot

    Returns the Integrate Watt Control total of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getIWControlTot() const
{
    return _iWControlTot;
}

/*---------------------------------------------------------------------------
    getIWCount

    Returns the Integrate Watt Control count of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getIWCount() const
{
    return _iWCount;
}

/*---------------------------------------------------------------------------
    getIVControl

    Returns the Integrate Volt/Var Control of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getIVControl() const
{
    return _iVControl;
}
/*---------------------------------------------------------------------------
    getIWControl

    Returns the Integrate Watt Control of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getIWControl() const
{
    return _iWControl;
}
/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getNewPointDataReceivedFlag() const
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
    getEstimatedVarLoadPointId

    Returns the estimated var load point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getEstimatedVarLoadPointId() const
{
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getEstimatedVarLoadPointValue() const
{
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getDailyOperationsAnalogPointId() const
{
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getPowerFactorPointId

    Returns the power factor point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getPowerFactorPointId() const
{
    return _powerfactorpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorPointId

    Returns the estimated power factor point id of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getEstimatedPowerFactorPointId() const
{
    return _estimatedpowerfactorpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the feeder has been recently controlled
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getRecentlyControlledFlag() const
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
DOUBLE CtiCCFeeder::getVarValueBeforeControl() const
{
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastCapBankControlledDeviceId

    Returns the device id of the last cap bank controlled of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getLastCapBankControlledDeviceId() const
{
    return _lastcapbankcontrolleddeviceid;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarCategory

    Returns the bus optimized var category of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getBusOptimizedVarCategory() const
{
    return _busoptimizedvarcategory;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarOffset

    Returns the bus optimized var offset of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getBusOptimizedVarOffset() const
{
    return _busoptimizedvaroffset;
}

/*---------------------------------------------------------------------------
    getPowerFactorValue

    Returns the power factor value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPowerFactorValue() const
{
    return _powerfactorvalue;
}

/*---------------------------------------------------------------------------
    getKVARSolution

    Returns the kvar solution of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getKVARSolution() const
{
    return _kvarsolution;
}

/*---------------------------------------------------------------------------
    getEstimatedPowerFactorValue

    Returns the estimated power factor value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getEstimatedPowerFactorValue() const
{
    return _estimatedpowerfactorvalue;
}

/*---------------------------------------------------------------------------
    getCurrentVarPointQuality

    Returns the CurrentVarPointQuality of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentVarPointQuality() const
{
    return _currentvarpointquality;
}

/*---------------------------------------------------------------------------
    getCurrentWattPointQuality

    Returns the CurrentWattPointQuality of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentWattPointQuality() const
{
    return _currentwattpointquality;
}

/*---------------------------------------------------------------------------
    getCurrentVoltPointQuality

    Returns the CurrentVoltPointQuality of the feeder
---------------------------------------------------------------------------*/
LONG CtiCCFeeder::getCurrentVoltPointQuality() const
{
    return _currentvoltpointquality;
}

/*---------------------------------------------------------------------------
    getWaiveControlFlag

    Returns the WaiveControlFlag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getWaiveControlFlag() const
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
LONG CtiCCFeeder::getDecimalPlaces() const
{
    return _decimalPlaces;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the PeakTimeFlag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getPeakTimeFlag() const
{
    return _peakTimeFlag;
}

BOOL CtiCCFeeder::getPorterRetFailFlag() const
{
    return _porterRetFailFlag;
}

LONG CtiCCFeeder::getEventSequence() const
{
    return _eventSeq;
}


BOOL CtiCCFeeder::getMultiMonitorFlag() const
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


/*---------------------------------------------------------------------------
    setPAOId

    Sets the pao id of the feeder - use with caution
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOCategory(const string& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOClass(const string& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOName(const string& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOType(const string& _type)
{
    _paotype = _type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAODescription(const string& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDisableFlag(BOOL disable)
{
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setParentId

    Sets the parentID (subBusID) of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setParentId(LONG parentId)
{
    _parentId = parentId;
    return *this;
}
/*---------------------------------------------------------------------------
    setStrategyId

    Sets the StrategyId of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setStrategyId(LONG strategyId)
{
    _strategyId = strategyId;
    return *this;           
}
/*---------------------------------------------------------------------------
    setStrategyName

    Sets the StrategyName of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setStrategyName(const string& strategyName)
{
    _strategyName = strategyName;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlMethod

    Sets the ControlMethod of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlMethod(const string& method)
{
    _controlmethod = method;
    return *this;
}
/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the MaxDailyOperation of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setMaxDailyOperation(LONG max)
{
    _maxdailyoperation = max;
    return *this;
}
/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the MaxOperationDisableFlag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setMaxOperationDisableFlag(BOOL maxopdisable)
{
    _maxoperationdisableflag = maxopdisable;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlInterval

    Sets the ControlInterval of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlInterval(LONG interval)
{
    _controlinterval = interval;
    return *this;
}
/*---------------------------------------------------------------------------
    setMaxConfirmTime

    Sets the MaxConfirmTime of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setMaxConfirmTime(LONG confirm)
{
    _maxconfirmtime = confirm;
    return *this;
}
/*---------------------------------------------------------------------------
    setMinConfirmPercent

    Sets the MinConfirmPercent of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setMinConfirmPercent(LONG confirm)
{
    _minconfirmpercent = confirm;
    return *this;
}
/*---------------------------------------------------------------------------
    setFailurePercent

    Sets the FailurePercent of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setFailurePercent(LONG failure)
{
    _failurepercent = failure;
    return *this;
}
/*---------------------------------------------------------------------------
    setDaysOfWeek

    Sets the DaysOfWeek of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDaysOfWeek(const string& days)
{
    _daysofweek = days;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlUnits

    Sets the ControlUnits of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlUnits(const string& contunit)
{
    _controlunits = contunit;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlDelayTime

    Sets the ControlDelayTime of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlDelayTime(LONG delay)
{
    _controldelaytime = delay;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlSendRetries

    Sets the ControlSendRetries of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlSendRetries(LONG retries)
{
    _controlsendretries = retries;
    return *this;
}
    
/*---------------------------------------------------------------------------
    setPeakLag

    Sets the peak lag level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakLag(DOUBLE peak)
{
    _peaklag = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakLag
    
    Sets the off peak lag level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setOffPeakLag(DOUBLE offpeak)
{
    _offpklag = offpeak;
    return *this;
}
/*---------------------------------------------------------------------------
    setPeakLead

    Sets the peak lead level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakLead(DOUBLE peak)
{
    _peaklead = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakLead

    Sets the off peak lead level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setOffPeakLead(DOUBLE offpeak)
{
    _offpklead = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakVARLag

    Sets the peak lag level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakVARLag(DOUBLE peak)
{
    _peakVARlag = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakVARLag
    
    Sets the off peak lag level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setOffPeakVARLag(DOUBLE offpeak)
{
    _offpkVARlag = offpeak;
    return *this;
}
/*---------------------------------------------------------------------------
    setPeakVARLead

    Sets the peak lead level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakVARLead(DOUBLE peak)
{
    _peakVARlead = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakVARLead

    Sets the off peak lead level of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setOffPeakVARLead(DOUBLE offpeak)
{
    _offpkVARlead = offpeak;
    return *this;
}



/*---------------------------------------------------------------------------
    setPeakPFSetPoint

    Sets the peak target power factor of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakPFSetPoint(DOUBLE peak)
{
    _peakpfsetpoint = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakPFSetPoint

    Sets the off peak target power factor of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setOffPeakPFSetPoint(DOUBLE offpeak)
{
    _offpkpfsetpoint = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStartTime

    Sets the PeakStartTime of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakStartTime(LONG starttime)
{
    _peakstarttime = starttime;
    return *this;
}
/*---------------------------------------------------------------------------
    setPeakStopTime

    Sets the PeakStopTime of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakStopTime(LONG stoptime)
{
    _peakstoptime = stoptime;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarLoadPointId(LONG currentvarid)
{
    _currentvarloadpointid = currentvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarLoadPointValue(DOUBLE currentvarval)
{
    if( _currentvarloadpointvalue != currentvarval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvarloadpointvalue = currentvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattLoadPointId(LONG currentwattid)
{
    _currentwattloadpointid = currentwattid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattLoadPointValue(DOUBLE currentwattval)
{
    if( _currentwattloadpointvalue != currentwattval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentwattloadpointvalue = currentwattval;
    return *this;
}
/*---------------------------------------------------------------------------
    setCurrentVoltLoadPointId

    Sets the current volt load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVoltLoadPointId(LONG currentvoltid)
{
    _currentvoltloadpointid = currentvoltid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVoltLoadPointValue(DOUBLE currentvoltval)
{
    if( _currentvoltloadpointvalue != currentvoltval )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
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
CtiCCFeeder& CtiCCFeeder::setDisplayOrder(LONG order)
{
    _displayorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setNewPointDataReceivedFlag(BOOL newpointdatareceived)
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
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate)
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
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedVarLoadPointId(LONG estimatedvarid)
{
    _estimatedvarloadpointid = estimatedvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedVarLoadPointValue(DOUBLE estimatedvarval)
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

    Sets the daily operations analog point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDailyOperationsAnalogPointId(LONG opspointid)
{
    _dailyoperationsanalogpointid = opspointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorPointId

    Sets the power factor point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPowerFactorPointId(LONG pfpointid)
{
    _powerfactorpointid = pfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedPowerFactorPointId

    Sets the estimated power factor point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedPowerFactorPointId(LONG epfpointid)
{
    _estimatedpowerfactorpointid = epfpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentDailyOperations(LONG operations)
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
    setRecentlyControlledFlag

    Sets the recently controlled flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setRecentlyControlledFlag(BOOL recentlycontrolled)
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
    setLastOperationTime

    Sets the last operation time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastOperationTime(const CtiTime& lastoperation)
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

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setVarValueBeforeControl(DOUBLE oldvarval)
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
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCapBankControlledDeviceId

    Sets the device id of the last cap bank controlled in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastCapBankControlledDeviceId(LONG lastcapbank)
{
    if( _lastcapbankcontrolleddeviceid != lastcapbank )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _lastcapbankcontrolleddeviceid = lastcapbank;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorValue

    Sets the PowerFactorValue in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPowerFactorValue(DOUBLE pfval)
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

    Sets the KVARSolution in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setKVARSolution(DOUBLE solution)
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

    Sets the EstimatedPowerFactorValue in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedPowerFactorValue(DOUBLE epfval)
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

    Sets the CurrentVarPointQuality in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarPointQuality(LONG cvpq)
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

    Sets the CurrentWattPointQuality in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattPointQuality(LONG cwpq)
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

    Sets the CurrentVoltPointQuality in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVoltPointQuality(LONG cvpq)
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

    Sets the WaiveControlFlag in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setWaiveControlFlag(BOOL waive)
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

/*---------------------------------------------------------------------------
    setParentControlUnits

    Sets the ParentControlUnits in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setParentControlUnits(const string& parentControlUnits)
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

    Sets the ParentName in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setParentName(const string& parentName)
{
    if (_parentName != parentName)
    {
        _dirty = TRUE;
    }
    _parentName = parentName;
    return *this;
}


/*---------------------------------------------------------------------------
    setParentDecimalPlaces

    Sets the ParentDecimalPlaces in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDecimalPlaces(LONG decimalPlaces)
{
    if (_decimalPlaces != decimalPlaces)
    {
        _dirty = TRUE;
    }
    _decimalPlaces = decimalPlaces;
    return *this;
}

/*---------------------------------------------------------------------------
    setParentPeakTimeFlag

    Sets the ParentPeakTimeFlag in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakTimeFlag(BOOL peakTimeFlag)
{
    if (_peakTimeFlag != peakTimeFlag)
    {
        _dirty = TRUE;
    }
    _peakTimeFlag = peakTimeFlag;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setPorterRetFailFlag(BOOL flag)
{
    if (_porterRetFailFlag != flag)
    {
        _dirty = TRUE;
    }
    _porterRetFailFlag = flag;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setEventSequence(LONG eventSeq)
{
    if (_eventSeq != eventSeq)
    {
        _dirty = TRUE;
    }
    _eventSeq = eventSeq;
    return *this;
}

CtiCCFeeder& CtiCCFeeder::setMultiMonitorFlag(BOOL flag)
{
    if (_multiMonitorFlag != flag)
    {
        _dirty = TRUE;
    }
    _multiMonitorFlag = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    findCapBankToDecreaseVars

    .
---------------------------------------------------------------------------*/
CtiCCCapBank* CtiCCFeeder::findCapBankToChangeVars(DOUBLE kvarSolution)
{
    CtiCCCapBank* returnCapBank = NULL;

    if( kvarSolution < 0.0 )
    {
        for(int i=0;i<_cccapbanks.size();i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

            if( !currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                ( currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ) )
            {
                //have we went past the max daily ops
                if( currentCapBank->getMaxDailyOps() > 0 &&
                    currentCapBank->getCurrentDailyOperations() == currentCapBank->getMaxDailyOps() )//only send once
                {
                    currentCapBank->setMaxDailyOpsHitFlag(TRUE);
                    string text = string("CapBank Exceeded Max Daily Operations");
                    string additional = string("CapBank: ");
                    additional += getPAOName();
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentCapBank->getMapLocationId();
                        additional += " (";
                        additional += currentCapBank->getPAODescription();
                        additional += ")";
                    }
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),5,text,additional,CapControlLogType,SignalAlarm0, "cap control"));

                    //we should disable feeder if the flag says so
                    if( currentCapBank->getMaxOpsDisableFlag() )
                    {
                        currentCapBank->setDisableFlag(TRUE);
                   //     setBusUpdatedFlag(TRUE);
                        string text = string("CapBank Disabled");
                        string additional = string("CapBank: ");
                        additional += getPAOName();
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentCapBank->getMapLocationId();
                            additional += " (";
                            additional += currentCapBank->getPAODescription();
                            additional += ")";
                        }
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),0,text,additional,CapControlLogType,SignalAlarm0, "cap control"));
                    }  
                }
                

                if( !currentCapBank->getDisableFlag() )
                    returnCapBank = currentCapBank;
                break;
            }
        }
        if (returnCapBank == NULL && _RETRY_FAILED_BANKS) 
        {
            for(int i=0;i<_cccapbanks.size();i++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

                if( !currentCapBank->getRetryCloseFailedFlag() && !currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                    !stringCompareIgnoreCase(currentCapBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                    (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail || 
                     currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail) )
                {
                    returnCapBank = currentCapBank;
                    currentCapBank->setRetryCloseFailedFlag(TRUE);
                    break;
                }
            }
        } 

    }
    else if( kvarSolution > 0.0 )
    {
        for(int i=_cccapbanks.size()-1;i>=0;i--)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            if( !currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
                ( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ) )
            {
                //have we went past the max daily ops
                if( currentCapBank->getMaxDailyOps() > 0 &&
                    (currentCapBank->getCurrentDailyOperations() == currentCapBank->getMaxDailyOps() && !_END_DAY_ON_TRIP) ||
                     (currentCapBank->getCurrentDailyOperations() == currentCapBank->getMaxDailyOps() + 1 && _END_DAY_ON_TRIP))//only send once
                {
                    string text("CapBank Exceeded Max Daily Operations");
                    string additional("CapBank: ");
                    additional += getPAOName();
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentCapBank->getMapLocationId();
                        additional += " (";
                        additional += currentCapBank->getPAODescription();
                        additional += ")";
                    }
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),5,text,additional,CapControlLogType,SignalAlarm0, "cap control"));

                    //we should disable feeder if the flag says so
                    if( currentCapBank->getMaxOpsDisableFlag() )
                    {
                        currentCapBank->setDisableFlag(TRUE);
                   //     setBusUpdatedFlag(TRUE);
                        string text = string("CapBank Disabled");
                        string additional = string("CapBank: ");
                        additional += getPAOName();
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentCapBank->getMapLocationId();
                            additional += " (";
                            additional += currentCapBank->getPAODescription();
                            additional += ")";
                        }
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),0,text,additional,CapControlLogType,SignalAlarm0, "cap control"));
                    } 
                }
                
                if( !currentCapBank->getDisableFlag() ||
                    (currentCapBank->getDisableFlag() && currentCapBank->getMaxDailyOpsHitFlag() && _END_DAY_ON_TRIP))
                    returnCapBank = currentCapBank;
                break;
            }
        }
        if (returnCapBank == NULL && _RETRY_FAILED_BANKS) 
        {
            for(int i=_cccapbanks.size()-1;i>=0;i--)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

                if( !currentCapBank->getRetryOpenFailedFlag() && !currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                    !stringCompareIgnoreCase(currentCapBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                    (currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail) )
                {
                    returnCapBank = currentCapBank;
                    currentCapBank->setRetryOpenFailedFlag(TRUE);
                    break;
                }
            }
        }  

    }
    
    return returnCapBank;
}

/*---------------------------------------------------------------------------
    createIncreaseVarRequest

    Creates a CtiRequestMsg to open the next cap bank to increase the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createIncreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        setLastCapBankControlledDeviceId(capBank->getPAOId());
        capBank->setControlStatus(CtiCCCapBank::OpenPending);
        figureEstimatedVarLoadPointValue();
        _currentdailyoperations++;
        capBank->setTotalOperations(capBank->getTotalOperations() + 1);
        capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
        setRecentlyControlledFlag(TRUE);
        //setVarValueBeforeControl(getCurrentVarLoadPointValue());
        setVarValueBeforeControl(kvarBefore);
        if( capBank->getStatusPointId() > 0 )
        {
            string additional;
            additional = ("Sub: ");
            additional += getParentName();
            additional += " /Feeder: ";
            additional += getPAOName();
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
            pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPAOName()));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(CtiTime());

            //setEventSequence(getEventSequence() + 1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), getParentId(), getPAOId(), capBankStateUpdate, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", kvarBefore, kvarBefore, 0, capBank->getIpAddress()));
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        }

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

            //setEventSequence(getEventSequence() + 1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), getParentId(), getPAOId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
        }
        if  (capBank->getTwoWayPoints() != NULL) 
        {
            if (capBank->getTwoWayPoints()->getCapacitorBankStateId() > 0) 
            { 
                CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(), capBank->getTwoWayPoints()->getCapacitorBankStateId(), capBank->getControlStatus(), CtiTime(), -1, 100 );
                hist->setMessagePriority( hist->getMessagePriority() + 2 );
                pointChanges.push_back( hist );
                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
            }
        }

        reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control open");
        reqMsg->setSOE(4);
    }

    return reqMsg;
}

CtiRequestMsg* CtiCCFeeder::createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore )
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ***VERIFICATION INFO***  CBid: "<<capBank->getPAOId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Open Sent Now " << endl;
        }

        setLastCapBankControlledDeviceId(capBank->getPAOId());
        capBank->setControlStatus(CtiCCCapBank::OpenPending);
        figureEstimatedVarLoadPointValue();
        _currentdailyoperations++;
        capBank->setTotalOperations(capBank->getTotalOperations() + 1);
        capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
        //setRecentlyControlledFlag(TRUE);
        setVarValueBeforeControl(getCurrentVarLoadPointValue());
        if( capBank->getStatusPointId() > 0 )
        {
            string additional("Sub: ");
            additional += getParentName();
            additional += " /Feeder: ";
            additional += getPAOName();
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control verification"));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
            pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(CtiTime());

            //setEventSequence(getEventSequence() + 1); // should be sub's event sequence for verification...
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), getParentId(), getPAOId(), capControlVerificationCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control verification", kvarBefore, kvarBefore, 0, capBank->getIpAddress()));
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        } 

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

            //setEventSequence(getEventSequence() + 1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), getParentId(), getPAOId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control verification"));
        }  

        if  (stringContainsIgnoreCase(capBank->getControlDeviceType(),"CBC 701") &&
             _USE_FLIP_FLAG == TRUE)
            reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control flip");
        else
            reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control open");
        reqMsg->setSOE(4);
    }

    return reqMsg;
}

CtiRequestMsg* CtiCCFeeder::createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore )  
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ***VERIFICATION INFO***  CBid: "<<capBank->getPAOId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Close Sent Now " << endl;
        }

        setLastCapBankControlledDeviceId(capBank->getPAOId());
        capBank->setControlStatus(CtiCCCapBank::ClosePending);
        figureEstimatedVarLoadPointValue();
        _currentdailyoperations++;
        capBank->setTotalOperations(capBank->getTotalOperations() + 1);
        capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
        //setRecentlyControlledFlag(TRUE);
        setVarValueBeforeControl(getCurrentVarLoadPointValue());
        if( capBank->getStatusPointId() > 0 )
        {
            string additional("Sub: ");
            additional += getParentName();
            additional += " /Feeder: ";
            additional += getPAOName();
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control verification"));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
            pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(CtiTime());

            //setEventSequence(getEventSequence() + 1);     // should be sub's sequence for verification
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), getParentId(), getPAOId(), capControlVerificationCommandSent, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control verification", kvarBefore, kvarBefore, 0, capBank->getIpAddress()));
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        } 

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

            //setEventSequence(getEventSequence() + 1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), getParentId(), getPAOId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control verification"));
        }  

        if  (stringContainsIgnoreCase(capBank->getControlDeviceType(),"CBC 701") &&
             _USE_FLIP_FLAG == TRUE)
            reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control flip");
        else
            reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control close");
        reqMsg->setSOE(4);
    }

    return reqMsg;
}


/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createDecreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        setLastCapBankControlledDeviceId(capBank->getPAOId());
        capBank->setControlStatus(CtiCCCapBank::ClosePending);
        figureEstimatedVarLoadPointValue();
        _currentdailyoperations++;
        capBank->setTotalOperations(capBank->getTotalOperations() + 1);
        capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
        setRecentlyControlledFlag(TRUE);
        //setVarValueBeforeControl(getCurrentVarLoadPointValue());
        setVarValueBeforeControl(kvarBefore);
        if( capBank->getStatusPointId() > 0 )
        {
            string additional;
            additional = ("Sub: ");
            additional += getParentName();
            additional += " /Feeder: ";
            additional += getPAOName();
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
            pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPAOName()));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(CtiTime());

            //setEventSequence(getEventSequence() + 1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getStatusPointId(), getParentId(), getPAOId(), capBankStateUpdate, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", kvarBefore, kvarBefore, 0, capBank->getIpAddress()));
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        }

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

            //setEventSequence(getEventSequence() + 1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, capBank->getOperationAnalogPointId(), getParentId(), getPAOId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
        }

        if  (capBank->getTwoWayPoints() != NULL) 
        {
            if (capBank->getTwoWayPoints()->getCapacitorBankStateId() > 0) 
            { 
                CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(), capBank->getTwoWayPoints()->getCapacitorBankStateId(), capBank->getControlStatus(), CtiTime(), -1, 100 );
                hist->setMessagePriority( hist->getMessagePriority() + 2 );
                pointChanges.push_back( hist );
                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
            
            
            }
        }



        reqMsg = new CtiRequestMsg( capBank->getControlDeviceId(),"control close" );
        reqMsg->setSOE(4);
    }

    return reqMsg;
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
        DOUBLE tempValue;
        if( getRecentlyControlledFlag() || getPerformingVerificationFlag())
            tempValue = getVarValueBeforeControl();
        else
            tempValue = getCurrentVarLoadPointValue();

        for(LONG i=0;i<_cccapbanks.size();i++)
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
/*DOUBLE CtiCCFeeder::figureCurrentSetPoint(const CtiTime& currentDateTime)
{
    return (isPeakTime(currentDateTime)?_peaksetpoint:_offpeaksetpoint);
}   */

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isPeakTime(const CtiTime& currentDateTime)
{
    unsigned secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
    if( isPeakDay() && getPeakStartTime() <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= getPeakStopTime() )
    {
        setPeakTimeFlag(TRUE);
    }
    else
    {
        setPeakTimeFlag(FALSE);
    }
    return _peakTimeFlag;
}  

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isPeakDay()
{
    //-------------------------------------
    //Need to check if it is a holiday today
    //also, but we must wait until there is
    //a dll with a function to do this
    //-------------------------------------
    CtiTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    if( _daysofweek[start_tm.tm_wday] == 'Y' &&
        ( _daysofweek[7] == 'Y' ||
          !CtiHolidayManager::getInstance().isHoliday(CtiDate()) ) )
        return TRUE;
    else
        return FALSE;
}

BOOL CtiCCFeeder::isControlPoint(LONG pointid)
{
    BOOL retVal = FALSE;

    if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
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

void CtiCCFeeder::updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime)
{
    DOUBLE controlVvalue = 0;

    if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " **DEBUG** integration not implemented for this controlUnit method " << endl;
            }
        }

        if (getControlInterval() > 0) 
        {
            if (nextCheckTime - getIntegratePeriod() <= currentDateTime) 
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
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " iVControlTot = " <<getIVControlTot() <<" iVCount = "<<getIVCount()<< endl;
    }
}

void CtiCCFeeder::updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime)
{
    DOUBLE controlWvalue = 0;

    if (!stringCompareIgnoreCase(getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
    {
        controlWvalue = getCurrentWattLoadPointValue();
        if (getControlInterval() > 0) 
        {
            if (nextCheckTime - getIntegratePeriod() <= currentDateTime) 
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
    checkForAndProvideNeededIndividualControl


---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, BOOL peakTimeFlag, LONG decimalPlaces, const string& controlUnits, BOOL dailyMaxOpsHitFlag)
{
    BOOL returnBoolean = FALSE;

    //setPeakTimeFlag(peakTimeFlag);
    //isPeakTime(currentDateTime);   //already set prior to this call..
    DOUBLE lagLevel = (peakTimeFlag?getPeakLag():getOffPeakLag());
    DOUBLE leadLevel = (peakTimeFlag?getPeakLead():getOffPeakLead());
    DOUBLE setpoint = (lagLevel + leadLevel)/2;
    string feederControlUnits = controlUnits;
    //DON'T ADD !... Supposed to be !=none
    if (stringCompareIgnoreCase(_controlunits,"(none)"))
    {
        feederControlUnits = _controlunits;
    }
    if( !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
       !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::PF_BY_KQControlUnits) )
        setpoint = (peakTimeFlag?getPeakPFSetPoint():getOffPeakPFSetPoint());

    //Integration Control Point setting...
    setIWControl(getCurrentWattLoadPointValue());
    if (!stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::VoltControlUnits))
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
        if (!stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::VoltControlUnits))
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
    checkMaxDailyOpCountExceeded();

    if( !getDisableFlag() &&
        !getWaiveControlFlag() &&
        ( !_IGNORE_NOT_NORMAL_FLAG || ( getCurrentVarPointQuality() == NormalQuality &&
            getCurrentWattPointQuality() == NormalQuality && getCurrentVoltPointQuality() == NormalQuality ) ) &&
        ( currentDateTime.seconds() >= getLastOperationTime().seconds() + getControlDelayTime() ) )
    {
        if( !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::KVARControlUnits) ||
            !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::VoltControlUnits) ) 
        {
            if( (!stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::KVARControlUnits) &&
                (getIVControl() > lagLevel || getIVControl() < leadLevel )) ||
                (!stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::VoltControlUnits) &&  
                (getIVControl() < lagLevel || getIVControl() > leadLevel) ) ) 
            {
        
                try
                {   
                    if( !dailyMaxOpsHitFlag && 
                        !getMaxDailyOpsHitFlag() &&
                        ( !stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::KVARControlUnits) &&
                          lagLevel < getIVControl() ) ||
                        ( !stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::VoltControlUnits) &&
                          lagLevel > getIVControl() ) )
                    {                    
                        //if( _CC_DEBUG )
                        if( !stringCompareIgnoreCase(feederControlUnits, CtiCCSubstationBus::KVARControlUnits) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Attempting to Decrease Var level in feeder: " << getPAOName().data() << endl;
                        }
                        else
                        {
                            setKVARSolution(-1);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Attempting to Increase Volt level in feeder: " << getPAOName().data() << endl;
                            }
                        }
    
                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());

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
                            //DOUBLE controlValue = (!stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                            string text = createTextString(CtiCCSubstationBus::IndividualFeederControlMethod, CtiCCCapBank::Close, getIVControl(), getCurrentVarLoadPointValue());
                            request = createDecreaseVarRequest(capBank , pointChanges, ccEvents, text, getIVControl());

                            if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Can Not Decrease Var level for feeder: " << getPAOName()
                                << " any further.  All cap banks are already in the Close state in: " << __FILE__ << " at: " << __LINE__ << endl;

                                CtiCCCapBank* currentCapBank = NULL;
                                for(int i=0;i<_cccapbanks.size();i++)
                                {
                                    currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                                    dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                                }
                            }
                        }
                    }
                    else
                    {
                        //if( _CC_DEBUG )
                        if( !stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::KVARControlUnits) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Attempting to Increase Var level in feeder: " << getPAOName().data() << endl;
                        }
                        else
                        {
                            setKVARSolution(1);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Attempting to Decrease Volt level in feeder: " << getPAOName().data() << endl;
                            }
                        }

                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());

                        //DOUBLE controlValue = (!stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        string text = createTextString(CtiCCSubstationBus::IndividualFeederControlMethod, CtiCCCapBank::Open, getIVControl(), getCurrentVarLoadPointValue());
                        request = createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getIVControl());
    
                        if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Can Not Increase Var level for feeder: " << getPAOName()
                            << " any further.  All cap banks are already in the Open state in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                            CtiCCCapBank* currentCapBank = NULL;
                            for(int i=0;i<_cccapbanks.size();i++)
                            {
                                currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
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
                        returnBoolean = TRUE;
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
        }
        else if( !stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
                 !stringCompareIgnoreCase(feederControlUnits,CtiCCSubstationBus::PF_BY_KQControlUnits) )
        {
            if( getKVARSolution() < 0 &&
                !dailyMaxOpsHitFlag && 
                !getMaxDailyOpsHitFlag() )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Decrease Var level in feeder: " << getPAOName() << endl;
                }
    
                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());
                if( capBank != NULL )
                {
                    if( capBank->getRecloseDelay() > 0 &&
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
                        DOUBLE adjustedBankKVARReduction = (lagLevel/100.0)*((DOUBLE)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            string text = createTextString(CtiCCSubstationBus::IndividualFeederControlMethod, CtiCCCapBank::Close, getIVControl(), getCurrentVarLoadPointValue());
                            request = createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, getIVControl());
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                        }
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Can Not Decrease Var level for feeder: " << getPAOName()
                    << " any further.  All cap banks are already in the Close state in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int j=0;j<_cccapbanks.size();j++)
                        {
                            currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
                            dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Attempting to Increase Var level in feeder: " << getPAOName() << endl;
                }
    
                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());
                if( capBank != NULL )
                {
                    DOUBLE adjustedBankKVARIncrease = -(leadLevel/100.0)*((DOUBLE)capBank->getBankSize());
                    if( adjustedBankKVARIncrease <= getKVARSolution() )
                    {
                        string text = createTextString(CtiCCSubstationBus::IndividualFeederControlMethod, CtiCCCapBank::Open, getIVControl(), getCurrentVarLoadPointValue());
                        request = createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getIVControl());
                    }
                    else
                    {//cap bank too big
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Open state in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int j=0;j<_cccapbanks.size();j++)
                        {
                            currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
                            dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
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
                returnBoolean = TRUE;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid control units: " << feederControlUnits << ", in feeder: " << getPAOName() << endl;
        }
    }
    
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality)
{
    BOOL returnBoolean = TRUE;
    BOOL found = FALSE;
    char tempchar[64] = "";
    DOUBLE change = 0;
    string text = "";
    string additional = "Sub: ";
              additional += getParentName();
              additional += " /Feeder: ";
              additional += getPAOName();;
              if (_LOG_MAPID_INFO) 
              {
                  additional += " MapID: ";
                  additional += getMapLocationId();
                  additional += " (";
                  additional += getPAODescription();
                  additional += ")";
              }

    for(LONG i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
        {
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    currentVarPointQuality == NormalQuality )
                {
                    change = currentVarLoadPointValue - varValueBeforeControl;
                    if( change < 0 )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    DOUBLE ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            text = string("Var: ");
                            text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                            text += " ( ";
                            text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                            text += "% change), OpenFail";
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            text = string("Var: ");
                            text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                            text += " ( ";
                            text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                            text += "% change), OpenQuestionable";
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            text = string("Var: ");
                            text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                            text += " ( ";
                            text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                            text += "% change), Open";
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        text = string("Var: ");
                        text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                        text += " ( ";
                        text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                        text += "% change), Open";
                    }
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                    text = string("Non Normal Var Quality = ");
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += " Var: ";
                    text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                    text += ", OpenQuestionable";
                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    currentVarPointQuality == NormalQuality )
                {
                    change = varValueBeforeControl - currentVarLoadPointValue;
                    if( change < 0 )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    DOUBLE ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                            text = string("Var: ");
                            text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                            text += " ( ";
                            text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                            text += "% change), CloseFail";
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            text = string("Var: ");
                            text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                            text += " ( ";
                            text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                            text += "% change), CloseQuestionable";
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            text = string("Var: ");
                            text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                            text += " ( ";
                            text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                            text += "% change), Closed";
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        text = string("Var: ");
                        text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                        text += " ( ";
                        text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                        text += "% change), Closed";
                    }
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = string("Non Normal Var Quality = ");
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += " Var: ";
                    text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                    text += ", CloseQuestionable";
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = FALSE;
            }

            if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  && 
                (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail)) 
            {
                currentCapBank->setRetryOpenFailedFlag(FALSE);
                currentCapBank->setRetryCloseFailedFlag(FALSE);
            }


            if( currentCapBank->getStatusPointId() > 0 )
            {
                if( text.length() > 0 )
                {//if control failed or questionable, create event to be sent to dispatch
                    long tempLong = currentCapBank->getStatusPointId();
                    pointChanges.push_back(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                }
                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, currentCapBank->getPAOName()));
                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                currentCapBank->setLastStatusChangeTime(CtiTime());

                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), getParentId(), getPAOId(), capBankStateUpdate, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control", varValueBeforeControl, currentVarLoadPointValue, change, currentCapBank->getIpAddress()));
                
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
            }
            found = TRUE;
            break;
        }
    }
    if (found == FALSE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        returnBoolean = TRUE;
    }

    setRecentlyControlledFlag(FALSE);

    return returnBoolean;
}


BOOL CtiCCFeeder::capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality)
{
    BOOL returnBoolean = FALSE;
    BOOL foundCap = FALSE;
    char tempchar[64] = "";
    DOUBLE change = 0;
    string text = "";
    string additional = "";
    BOOL assumedWrongFlag = FALSE;

    BOOL vResult = FALSE; //fail

    CtiCCCapBank* currentCapBank = NULL;

    for(int j=0;j<_cccapbanks.size();j++)
    {
       currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
       if (currentCapBank->getPAOId() == getCurrentVerificationCapBankId())
       {   

           if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
           {
               if( !_IGNORE_NOT_NORMAL_FLAG ||
                   getCurrentVarPointQuality() == NormalQuality )
               {    
                   change = getCurrentVarLoadPointValue() - getVarValueBeforeControl();

                   if( change < 0 )
                   {
                       {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                       }
                       if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE &&
                           currentCapBank->getVCtrlIndex() == 1)            
                       {
                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                           setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                           currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                           //return returnBoolean;
                           assumedWrongFlag = TRUE;
                           change = 0 - change;
                       }
                   }
                   DOUBLE ratio = change/currentCapBank->getBankSize();
                   if( ratio < minConfirmPercent*.01 )
                   {
                       if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);

                           text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), OpenFail";
                           else
                               text += "% change), CloseFail";
                           additional = string("Feeder: ");
                           additional += getPAOName();
                       }
                       else if( minConfirmPercent != 0 )
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                           text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), OpenQuestionable";
                           else
                               text += "% change), CloseQuestionable";
                           additional = string("Feeder: ");
                           additional += getPAOName();
                       }
                       else
                       {    
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);
                           
                           text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), Open";
                           else
                               text += "% change), Close";
                           additional = string("Feeder: ");
                           additional += getPAOName();
                           vResult = TRUE;
                       }
                   }        
                   else
                   {
                       if (!assumedWrongFlag)
                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                       else
                           currentCapBank->setControlStatus(CtiCCCapBank::Close);

                       text = string("Var: ");
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += " ( ";
                       text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                       if (!assumedWrongFlag)
                           text += "% change), Open";
                       else
                           text += "% change), Close";
                       additional = string("Feeder: ");
                       additional += getPAOName();
                       vResult = TRUE;
                   }
               }
               else
               {
                   char tempchar[80];
                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                   text = string("Non Normal Var Quality = ");
                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                   text += tempchar;
                   text += " Var: ";
                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                   text += ", OpenQuestionable";
                   additional = string("Feeder: ");
                   additional += getPAOName();
               }
           }
           else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
           {
               if( !_IGNORE_NOT_NORMAL_FLAG ||
                   getCurrentVarPointQuality() == NormalQuality )
               {
                   change = getVarValueBeforeControl() - getCurrentVarLoadPointValue();
                   if( change < 0 )
                   {
                       {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                       }
                       if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG == TRUE &&
                           currentCapBank->getVCtrlIndex() == 1)            
                       {
                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                           setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                           currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                           //return returnBoolean;
                           assumedWrongFlag = TRUE;
                           change = 0 - change;
                       }
                   }
                   DOUBLE ratio = change/currentCapBank->getBankSize();
                   if( ratio < minConfirmPercent*.01 )
                   {
                       if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);

                           text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), CloseFail";
                           else
                               text += "% change), OpenFail";

                           additional = string("Feeder: ");
                           additional += getPAOName();
                       }
                       else if( minConfirmPercent != 0 )
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);

                           text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), CloseQuestionable";
                           else
                               text += "% change), OpenQuestionable";

                           additional = string("Feeder: ");
                           additional += getPAOName();
                       }
                       else
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);

                           text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), Closed";
                           else
                               text += "% change), Open";
                           additional = string("Feeder: ");
                           additional += getPAOName();
                           vResult = TRUE;
                       }
                   }
                   else
                   {
                       if (!assumedWrongFlag)
                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                       else
                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                       text = string("Var: ");
                           text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                           text += " ( ";
                           text += CtiNumStr(ratio*100.0,getDecimalPlaces()).toString();
                           if (!assumedWrongFlag)
                               text += "% change), Closed";
                           else
                               text += "% change), Open";
                           additional = string("Feeder: ");
                           additional += getPAOName();
                       vResult = TRUE;
                   }
               }
               else
               {
                   char tempchar[80];
                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                   text = string("Non Normal Var Quality = ");
                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                   text += tempchar;
                   text += " Var: ";
                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                   text += ", CloseQuestionable";
                   additional = string("Feeder: ");
                   additional += getPAOName();
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
               returnBoolean = FALSE;
              // break;
           }
           if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  && 
                (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail)) 
            {
                currentCapBank->setRetryOpenFailedFlag(FALSE);
                currentCapBank->setRetryCloseFailedFlag(FALSE);
            }


           if( currentCapBank->getStatusPointId() > 0 )
           {
               if( text.length() > 0 )
               {//if control failed or questionable, create event to be sent to dispatch
                   long tempLong = currentCapBank->getStatusPointId();
                   if (_LOG_MAPID_INFO) 
                   {
                       additional += " MapID: ";
                       additional += getMapLocationId();
                       additional += " (";
                       additional += getPAODescription();
                       additional += ")";
                   }
                   pointChanges.push_back(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent, "cap control verification"));
                   ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
               }
               pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
               ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
               currentCapBank->setLastStatusChangeTime(CtiTime()); 

               //setEventSequence(currentFeeder->getEventSequence() + 1);
               ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), getParentId(), getPAOId(), capBankStateUpdate, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control verification", varValueBeforeControl, currentVarLoadPointValue, change));
               //setEventSequence(0);
           }
           else
           {
               CtiLockGuard<CtiLogger> logger_guard(dout);
               dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
               << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
           }

           if (currentCapBank->updateVerificationState())
           {
               returnBoolean = TRUE;
               currentCapBank->setPerformingVerificationFlag(FALSE);
               //setBusUpdatedFlag(TRUE);
               return returnBoolean;
           }
           foundCap = TRUE;
           break;
       }
    }
    
    if (foundCap == FALSE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Last Verification Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
        returnBoolean = TRUE;
    }
    return returnBoolean;
}

CtiCCFeeder& CtiCCFeeder::startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    //get CapBank to perform verification on...subbus stores, currentCapBankToVerifyId

    CtiRequestMsg* request = NULL;
    BOOL retVal = TRUE;

    for(LONG j=0;j<_cccapbanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
        if( currentCapBank->getPAOId() == _currentVerificationCapBankId )
        {
        
            currentCapBank->initVerificationControlStatus();
            setCurrentVerificationCapBankState(currentCapBank->getAssumedOrigVerificationState());
            currentCapBank->setAssumedOrigVerificationState(getCurrentVerificationCapBankOrigState());
            currentCapBank->setPreviousVerificationControlStatus(-1);
            currentCapBank->setVCtrlIndex(1); //1st control sent
            currentCapBank->setPerformingVerificationFlag(TRUE);
            setPerformingVerificationFlag(TRUE);


            if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
            {

                /*if( currentCapBank->getRecloseDelay() > 0 &&
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
                else  */
                {
                    //add capbank reclose delay check here...
                    DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                    int control =  CtiCCCapBank::Close;
                    if  (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&
                        _USE_FLIP_FLAG == TRUE )
                    {
                        control = 4; //flip
                    }
                string text = createTextString(getControlMethod(), control, controlValue, getCurrentVarLoadPointValue());
                request = createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
                }
            }
            else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
            {
                DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                int control =  CtiCCCapBank::Open;
                if  (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&
                    _USE_FLIP_FLAG == TRUE )
                {
                    control = 4; //flip
                }
                string text = createTextString(getControlMethod(), control, controlValue, getCurrentVarLoadPointValue());
                request = createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
            }


            if( request != NULL )
            {
                pilMessages.push_back(request);
                setLastOperationTime(currentDateTime);
                //setLastFeederControlledPAOId(currentFeeder->getPAOId());
                //setLastFeederControlledPosition(i);
                setLastCapBankControlledDeviceId( currentCapBank->getPAOId());
                setLastOperationTime(currentDateTime);
               //((CtiCCFeeder*)_ccfeeders[currentPosition])->setLastOperationTime(currentDateTime);
                setVarValueBeforeControl(getCurrentVarLoadPointValue());
                setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                figureEstimatedVarLoadPointValue();
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }
                //setEventSequence(getEventSequence() + 2);
                //setRecentlyControlledFlag(TRUE);
            }
            //setNewPointDataReceivedFlag(FALSE);
            //regardless what happened the substation bus should be should be sent to the client

            //setBusUpdatedFlag(TRUE);
            return *this;
        }
    }
    return *this;
}


BOOL CtiCCFeeder::sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{
    BOOL retVal = TRUE;
    CtiRequestMsg* request = NULL;
    for(LONG j=0;j<_cccapbanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
        if( currentCapBank->getPAOId() == _currentVerificationCapBankId )
        {
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
                    DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                    int control =  CtiCCCapBank::Open;
                    if  (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&
                        _USE_FLIP_FLAG == TRUE )
                    {
                        control = 4; //flip
                    }
                    //setEventSequence(getEventSequence());
                    string text = createTextString(getControlMethod(), control, controlValue, getCurrentVarLoadPointValue()) ;
                    request = createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
                }
                else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                {   
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
                        //check capbank reclose delay here...
                        DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        int control =  CtiCCCapBank::Close;
                        if  (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&
                            _USE_FLIP_FLAG == TRUE )
                        {
                            control = 4; //flip
                        }
                        //currentFeeder->setEventSequence(getEventSequence());
                        string text = createTextString(getControlMethod(), control, controlValue, getCurrentVarLoadPointValue()) ;
                        request = createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
                    }
                }

            }
            else if (currentCapBank->getVCtrlIndex() == 3)
            {
                if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                {
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
                         //check capbank reclose delay here...
                        DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        int control =  CtiCCCapBank::Close;
                        if  (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&
                            _USE_FLIP_FLAG == TRUE )
                        {
                            control = 4; //flip
                        }
                        string text = createTextString(getControlMethod(), control, controlValue, getCurrentVarLoadPointValue()) ;
                        request = createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
                    }
                }
                else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                {
                    DOUBLE controlValue = (!stringCompareIgnoreCase(_controlunits, CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                    int control =  CtiCCCapBank::Open;
                    if  (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") &&
                        _USE_FLIP_FLAG == TRUE )
                    {
                        control = 4; //flip
                    }
                    string text = createTextString(getControlMethod(), control, controlValue, getCurrentVarLoadPointValue()) ;
                    request = createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
                }

            }
            else if (currentCapBank->getVCtrlIndex() == 5)
            {
                request = NULL;
            }
            if( request != NULL )
            {
                pilMessages.push_back(request);
                setLastCapBankControlledDeviceId( currentCapBank->getPAOId());
                setLastOperationTime(currentDateTime);
               //((CtiCCFeeder*)_ccfeeders[i])->setLastOperationTime(currentDateTime);
                setVarValueBeforeControl(getCurrentVarLoadPointValue());
                setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                figureEstimatedVarLoadPointValue();
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }

                return retVal;
            }

        }
    }
    
    
    

    return retVal;
}


                    
BOOL CtiCCFeeder::areThereMoreCapBanksToVerify()
{

    getNextCapBankToVerify();
    if (getCurrentVerificationCapBankId() != -1 )//&& !getDisableFlag())
    {
        setPerformingVerificationFlag(TRUE);

        return TRUE;
    }
    else
    {
        CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
        for(LONG j=0;j<ccCapBanks.size();j++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
            //currentCapBank->setVerificationFlag(FALSE);
            currentCapBank->setPerformingVerificationFlag(FALSE);
            currentCapBank->setVerificationDoneFlag(TRUE);
        }
        
        setPerformingVerificationFlag(FALSE);
        setVerificationDoneFlag(TRUE);
        //setBusUpdatedFlag(TRUE);
        return FALSE;
    }
}


CtiCCFeeder& CtiCCFeeder::getNextCapBankToVerify()
{
    _currentVerificationCapBankId = -1;
    
    CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
    for(LONG j=0;j<ccCapBanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
        if( currentCapBank->getVerificationFlag() && !currentCapBank->getVerificationDoneFlag() )
        {  
            _currentVerificationCapBankId = currentCapBank->getPAOId();
            return *this;
        }
    }
    setVerificationDoneFlag(TRUE);
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
void CtiCCFeeder::fillOutBusOptimizedInfo(BOOL peakTimeFlag)
{
    setPeakTimeFlag(peakTimeFlag);
    DOUBLE lagLevel = (peakTimeFlag?getPeakLag():getOffPeakLag());
    DOUBLE leadLevel = (peakTimeFlag?getPeakLead():getOffPeakLead());
    DOUBLE setpoint = (lagLevel + leadLevel)/2;

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
BOOL CtiCCFeeder::isAlreadyControlled(LONG minConfirmPercent)
{
    BOOL returnBoolean = FALSE;
    BOOL found = FALSE;

    if( !_IGNORE_NOT_NORMAL_FLAG ||
        getCurrentVarPointQuality() == NormalQuality )
    {
        if( minConfirmPercent > 0 )
        {
            DOUBLE oldVarValue = getVarValueBeforeControl();
            DOUBLE newVarValue = getCurrentVarLoadPointValue();
            for(LONG i=0;i<_cccapbanks.size();i++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
                {
                    if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending || 
                        currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        DOUBLE change = newVarValue - oldVarValue;
                        DOUBLE ratio = fabs(change/currentCapBank->getBankSize());
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                        found = TRUE;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Last Cap Bank: "<<getLastCapBankControlledDeviceId()<<" controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                        returnBoolean = FALSE;
                    }
                    break;
                }
            }

            // Check all other banks on this feeder for a pending state...
            if (found == FALSE)
            {
                for(LONG i=0;i<_cccapbanks.size();i++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending || 
                        currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        DOUBLE change = newVarValue - oldVarValue;
                        DOUBLE ratio = fabs(change/currentCapBank->getBankSize());
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                        found = TRUE;
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
                            setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                    }
                }
            }
            if (found == FALSE)
            {    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = TRUE;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isPastMaxConfirmTime(const CtiTime& currentDateTime, LONG maxConfirmTime, LONG feederRetries)
{
    BOOL returnBoolean = FALSE;

    if (getStrategyId() > 0 && getControlSendRetries() > feederRetries)
    {
        feederRetries = getControlSendRetries();
    }

    if( ((getLastOperationTime().seconds() + (maxConfirmTime/_SEND_TRIES)) <= currentDateTime.seconds()) ||
        ((getLastOperationTime().seconds() + (maxConfirmTime/(feederRetries+1))) <= currentDateTime.seconds()) )
    {
        returnBoolean = TRUE;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isVerificationAlreadyControlled(LONG minConfirmPercent)
{
    BOOL returnBoolean = FALSE;
    BOOL found = FALSE;

    if (_porterRetFailFlag == TRUE)
    {
        _porterRetFailFlag = FALSE;
        return TRUE;
    }
    else if( !_IGNORE_NOT_NORMAL_FLAG ||
        getCurrentVarPointQuality() == NormalQuality )
    {
        if( minConfirmPercent > 0 )
        {
            DOUBLE oldVarValue = getVarValueBeforeControl();
            DOUBLE newVarValue = getCurrentVarLoadPointValue();
            for(LONG i=0;i<_cccapbanks.size();i++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
                {
                    if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                    {
                        DOUBLE change = newVarValue - oldVarValue;
                        DOUBLE ratio = fabs(change/currentCapBank->getBankSize());
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                        found = TRUE;
                    }
                    else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        DOUBLE change = oldVarValue - newVarValue;
                        DOUBLE ratio = fabs(change/currentCapBank->getBankSize());
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                        found = TRUE;
                    }

                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Last Cap Bank: "<<getLastCapBankControlledDeviceId()<<" controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                        returnBoolean = FALSE;
                    }
                    break;
                }
            }

            // Check all other banks on this feeder for a pending state...
            if (found == FALSE)
            {
                for(LONG i=0;i<_cccapbanks.size();i++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending || 
                        currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        DOUBLE change = newVarValue - oldVarValue;
                        DOUBLE ratio = fabs(change/currentCapBank->getBankSize());
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                        found = TRUE;
                    }
                }
            }
            if (found == FALSE)
            {    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Last Cap Bank controlled NOT FOUND: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = TRUE;
            }
        }
    }

    return returnBoolean;
}


/*---------------------------------------------------------------------------
    attemptToResendControl

    Returns a .
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::attemptToResendControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, LONG maxConfirmTime)
{
    BOOL returnBoolean = FALSE;

    for(LONG i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
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
                        additional += getPAOName();
                            if (_LOG_MAPID_INFO) 
                            {
                                additional += " MapID: ";
                                additional += getMapLocationId();
                                additional += " (";
                                additional += getPAODescription();
                                additional += ")";
                            }
                        pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);

                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), getParentId(), getPAOId(), capBankStateUpdate, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control"));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                            << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }

                        CtiRequestMsg* reqMsg = new CtiRequestMsg(currentCapBank->getControlDeviceId(),"control open");
                        pilMessages.push_back(reqMsg);
                        setLastOperationTime(currentDateTime);
                        returnBoolean = TRUE;
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
                            additional += getPAOName();
                            if (_LOG_MAPID_INFO) 
                            {
                                additional += " MapID: ";
                                additional += getMapLocationId();
                                additional += " (";
                                additional += getPAODescription();
                                additional += ")";
                            }
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);

                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), getParentId(), getPAOId(), capBankStateUpdate, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control"));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                            << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }

                        CtiRequestMsg* reqMsg = new CtiRequestMsg(currentCapBank->getControlDeviceId(),"control close");
                        pilMessages.push_back(reqMsg);
                        setLastOperationTime(currentDateTime);
                        returnBoolean = TRUE;
                    }
                    else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cannot Resend Control for Cap Bank: "<< currentCapBank->getPAOName() <<", Not Pending in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }
                else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Cannot Resend Control for Cap Bank: "<< currentCapBank->getPAOName() <<", Past Confirm Time in: " << __FILE__ << " at: " << __LINE__ << endl;
                }
                break;
            }
        }
    }

    return returnBoolean;
}

BOOL CtiCCFeeder::checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, LONG maxConfirmTime, LONG sendRetries)
{
   BOOL returnBoolean = FALSE;
   if (getVerificationFlag() && getPerformingVerificationFlag() &&
       //currentCCFeeder->getCurrentVerificationCapBankId() == getCurrentVerificationFeederId() && 
       isPastMaxConfirmTime(currentDateTime,maxConfirmTime,sendRetries) &&
       attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, maxConfirmTime) )
   {
       setLastOperationTime(currentDateTime);
       returnBoolean = TRUE;
   }
   return returnBoolean;
}


CtiCCFeeder& CtiCCFeeder::setVerificationFlag(BOOL verificationFlag)
{
    if (_verificationFlag != verificationFlag)
        _dirty = TRUE;
    _verificationFlag = verificationFlag;

    return *this;
}
CtiCCFeeder& CtiCCFeeder::setPerformingVerificationFlag(BOOL performingVerificationFlag)
{
    if (_performingVerificationFlag != performingVerificationFlag)
        _dirty = TRUE;
    _performingVerificationFlag = performingVerificationFlag;

    return *this;
}
CtiCCFeeder& CtiCCFeeder::setVerificationDoneFlag(BOOL verificationDoneFlag)
{
    if (_verificationDoneFlag != verificationDoneFlag)
        _dirty = TRUE;
    _verificationDoneFlag = verificationDoneFlag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setPreOperationMonitorPointScanFlag( BOOL flag)
{
    if (_preOperationMonitorPointScanFlag != flag)
        _dirty = TRUE;
    _preOperationMonitorPointScanFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setOperationSentWaitFlag( BOOL flag)
{
    if (_operationSentWaitFlag != flag)
        _dirty = TRUE;
    _operationSentWaitFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setPostOperationMonitorPointScanFlag( BOOL flag)
{
    if (_postOperationMonitorPointScanFlag != flag)
        _dirty = TRUE;
    _postOperationMonitorPointScanFlag = flag;

    return *this;
}



CtiCCFeeder& CtiCCFeeder::setWaitForReCloseDelayFlag(BOOL flag)
{
    if (_waitForReCloseDelayFlag != flag)
        _dirty = TRUE;
    _waitForReCloseDelayFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setMaxDailyOpsHitFlag(BOOL flag)
{
    if (_maxDailyOpsHitFlag != flag)
        _dirty = TRUE;
    _maxDailyOpsHitFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setOvUvDisabledFlag(BOOL flag)
{
    if (_ovUvDisabledFlag != flag)
        _dirty = TRUE;
    _ovUvDisabledFlag = flag;

    return *this;
}

CtiCCFeeder& CtiCCFeeder::setCurrentVerificationCapBankId(LONG capBankId)
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
CtiCCFeeder& CtiCCFeeder::setCurrentVerificationCapBankState(LONG status)
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

CtiCCFeeder& CtiCCFeeder::setTargetVarValue(DOUBLE value)
{
    if( _targetvarvalue != value )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
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
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    _solution = text;

    return *this;
}

/*---------------------------------------------------------------------------
    setIntegrateFlag

    Sets the IntegrateFlag of the substation
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIntegrateFlag(BOOL flag)
{
    _integrateflag = flag;
    return *this;
}
/*---------------------------------------------------------------------------
    setIntegratePeriod
    
    Sets the IntegratePeriod of the substation
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIntegratePeriod(LONG period)
{
    _integrateperiod = period;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVControlTot 
        
    Sets the Integrated Volt or var Control Total of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIVControlTot(DOUBLE value)
{
    _iVControlTot = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVCoont 
        
    Sets the Integrated Volt or var Control Count of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIVCount(LONG value)
{
    _iVCount = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIWControlTot 
        
    Sets the Integrated Watt Control Total of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIWControlTot(DOUBLE value)
{
    _iWControlTot = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIWCoont 
        
    Sets the Integrated Watt Control Count of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIWCount(LONG value)
{
    _iWCount = value;
    return *this;
}
/*---------------------------------------------------------------------------
    setIVControl 
        
    Sets the Integrated Volt/Var Control of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIVControl(DOUBLE value)
{
    _iVControl = value;
    return *this;
}/*---------------------------------------------------------------------------
    setIWControl 
        
    Sets the Integrated Watt Control  of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setIWControl(DOUBLE value)
{
    _iWControl = value;
    return *this;
}

BOOL CtiCCFeeder::getVerificationFlag() const
{
    return _verificationFlag;
}

BOOL CtiCCFeeder::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

BOOL CtiCCFeeder::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

BOOL CtiCCFeeder::getPreOperationMonitorPointScanFlag() const
{
    return _preOperationMonitorPointScanFlag;
}
BOOL CtiCCFeeder::getOperationSentWaitFlag() const
{
    return _operationSentWaitFlag;
}
BOOL CtiCCFeeder::getPostOperationMonitorPointScanFlag() const
{
    return _postOperationMonitorPointScanFlag;
}                                             

BOOL CtiCCFeeder::getWaitForReCloseDelayFlag() const
{
    return _waitForReCloseDelayFlag;
}
BOOL CtiCCFeeder::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}

BOOL CtiCCFeeder::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

LONG CtiCCFeeder::getCurrentVerificationCapBankId() const
{
    return _currentVerificationCapBankId;
}
LONG CtiCCFeeder::getCurrentVerificationCapBankOrigState() const
{
    return _currentCapBankToVerifyAssumedOrigState;

}
DOUBLE CtiCCFeeder::getTargetVarValue() const
{
    return _targetvarvalue;

}
const string& CtiCCFeeder::getSolution() const
{
    return _solution;

}

BOOL CtiCCFeeder::voltControlBankSelectProcess(CtiCCMonitorPoint* point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    BOOL retVal = FALSE;
    CtiCCCapBank* bestBank = NULL;

    CtiRequestMsg* request = NULL;
   //Check for undervoltage condition first.
   try
   {
        if (point->getValue() < point->getLowerBandwidth()) 
        {
            CtiCCCapBank* parentBank = NULL;
            DOUBLE bestDelta = 0;

            //1.  First check this point's parent bank to see if we can close it.
            parentBank = getMonitorPointParentBank(point);
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
                                string text = createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                request = createDecreaseVarRequest(parentBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());

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
                for (LONG i = 0; i < _cccapbanks.size(); i++)
                {                                              
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::Open || 
                        currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)
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
                                        dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getBankId()<<"/"<<point->getPointId()<<" CapBank: "<<currentCapBank->getPAOName() <<" selected to Close" << endl;
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if (areOtherMonitorPointResponsesOk(point->getPointId(), currentCapBank, CtiCCCapBank::Close))
                                    {
                                        DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                        string text = createTextString(getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                        request = createDecreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());

                                        updatePointResponsePreOpValues(currentCapBank);
                                        bestBank = currentCapBank;
                                    }
                                }
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
                    dout << CtiTime() << " No Banks Available to Close on Feeder: "<<getPAOName() << endl;
                }
            }
        }
        else if (point->getValue() > point->getUpperBandwidth())
        {
            CtiCCCapBank* parentBank = NULL;
            DOUBLE bestDelta = 0;

            //1.  First check this point's parent bank to see if we can open it.
            parentBank = getMonitorPointParentBank(point);
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
                                DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                string text = createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                request = createIncreaseVarRequest(parentBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());

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
                for (LONG i = 0; i < _cccapbanks.size(); i++)
                {                                              
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::Close || 
                        currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)
                    {

                        if (point->getBankId() != currentCapBank->getPAOId()) 
                        {
                            CtiCCPointResponse* pResponse =  currentCapBank->getPointResponse(point);
                            if (pResponse != NULL) 
                            {
                           
                                if ( (point->getValue() - pResponse->getDelta() <= point->getUpperBandwidth() && 
                                      point->getValue() - pResponse->getDelta() >= point->getLowerBandwidth() ) ||
                                      pResponse->getDelta() == 0 ||
                                      point->getValue() - pResponse->getDelta() > point->getLowerBandwidth() ) 
                                {
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " Attempting to Decrease Voltage on Feeder: "<<getPAOName()<<" CapBank: "<<currentCapBank->getPAOName() << endl;
                                    }

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " MULTIVOLT: MonitorPoint->bankID/pointID: "<<point->getBankId()<<"/"<<point->getPointId()<<" CapBank: "<<currentCapBank->getPAOName() <<" selected to Open" << endl;
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if (areOtherMonitorPointResponsesOk(point->getPointId(), currentCapBank, CtiCCCapBank::Open))
                                    {
                                        DOUBLE controlValue = (stringCompareIgnoreCase(getControlUnits(), CtiCCSubstationBus::VoltControlUnits) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                                        string text = createTextString(getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                        request = createIncreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue());
                           
                                        updatePointResponsePreOpValues(currentCapBank);
                                        bestBank = currentCapBank;
                                    }
                                }
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
                    dout << CtiTime() << " MULTIVOLT: No Banks Available to Open on Feeder: "<<getPAOName() << endl;
                }
            }
       }


        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(TRUE);
            setLastCapBankControlledDeviceId( bestBank->getPAOId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
      
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

BOOL CtiCCFeeder::areOtherMonitorPointResponsesOk(LONG mPointID, CtiCCCapBank* potentialCap, int action)
{
    BOOL retVal = TRUE;

    //action = 0 --> open
    //action = 1 --> close
    try
    {
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
                                        dout << CtiTime() << " MULTIVOLT: otherPoint: "<<otherPoint->getPointId()<<" "<<otherPoint->getBankId()<<" Value: "<<otherPoint->getValue()<<" Delta: "<<pResponse->getDelta()<<" pResponse: "<<pResponse->getPointId()<<" "<<pResponse->getBankId() << endl;
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
                                        dout << CtiTime() << " MULTIVOLT: otherPoint: "<<otherPoint->getPointId()<<" "<<otherPoint->getBankId()<<" Value: "<<otherPoint->getValue()<<" Delta: "<<pResponse->getDelta()<<" pResponse: "<<pResponse->getPointId()<<" "<<pResponse->getBankId() << endl;
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
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return retVal;
}

BOOL CtiCCFeeder::areAllMonitorPointsInVoltageRange(CtiCCMonitorPoint* oorPoint)
{
    BOOL retVal = FALSE;
    try
    {
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
                    dout << CtiTime() << " ** WARNING ** Monitor Point: "<<point->getPointId()<<" on CapBank: "<<point->getBankId()<<" is OUTSIDE limits.  Current value: "<<point->getValue() << endl;
                }
                *oorPoint = *point;
                retVal = FALSE;
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

    try
    {
        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " MULTIVOLT: Updating POINT RESPONSE PREOPVALUES for CapBank: " <<capBank->getPAOName() << endl;
            dout << CtiTime() << " MULTIVOLT: Bank ID: " <<capBank->getPAOName()<<" has "<<capBank->getPointResponse().size()<<" point responses"<< endl;
        }
        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];

            for (LONG j=0; j<capBank->getPointResponse().size(); j++)
            {
                CtiCCPointResponse* pResponse = (CtiCCPointResponse*)capBank->getPointResponse()[j];

                if (point->getPointId() == pResponse->getPointId())
                {
                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " MULTIVOLT: Bank ID: " <<capBank->getPAOName()<<" Point ID: "<<pResponse->getPointId()<<" Value: "<<point->getValue() << endl;
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


void CtiCCFeeder::updatePointResponseDeltas()
{

    try
    {

        for(LONG i=0;i<_cccapbanks.size();i++)
        {
           CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

           if (currentCapBank->getPAOId() == getLastCapBankControlledDeviceId())
           {   
               if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " MULTIVOLT: Updating POINT RESPONSE DELTAS for CapBank: " <<currentCapBank->getPAOName() << endl;
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
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

BOOL CtiCCFeeder::areAllMonitorPointsNewEnough(const CtiTime& currentDateTime)
{
    BOOL retVal = FALSE;

    try
    {
        //if ( isScanFlagSet() && currentDateTime >= getMonitorPointScanTime() - (_SCAN_WAIT_EXPIRE) )  //T1 Expired.. Force Process
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
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " ALL MONITOR POINTS ARE NEW ENOUGH on Feeder: " <<getPAOName() << endl;
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
                if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " ALL MONITOR POINTS ARE NEW ENOUGH on Feeder: " <<getPAOName() << endl;
                }
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
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return retVal;
}

ULONG CtiCCFeeder::getMonitorPointScanTime()
{

    CtiTime time;
    time.now();


    return time.seconds();

}

BOOL CtiCCFeeder::isScanFlagSet()
{
    if (_preOperationMonitorPointScanFlag || _postOperationMonitorPointScanFlag)
    {
        return TRUE;
    }
    return FALSE;
}

BOOL CtiCCFeeder::scanAllMonitorPoints()
{
    BOOL retVal = FALSE;
    try
    {

        for (int i = 0; i < _multipleMonitorPoints.size(); i++)
        {
            CtiCCMonitorPoint* point = (CtiCCMonitorPoint*)_multipleMonitorPoints[i];
            if (point->isScannable() && !point->getScanInProgress())
            {
                for (LONG j = 0; j < _cccapbanks.size();j++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
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
                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " MULTIVOLT: Requesting scans at the alternate scan rate for " << currentCapBank->getPAOName() << endl;
                            }
                            //CtiCapController::getInstance()->sendMessageToDispatch(new CtiRequestMsg(currentCapBank->getControlDeviceId(), "scan general"));
                            point->setScanInProgress(TRUE);
                            retVal = TRUE;
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

CtiCCFeeder& CtiCCFeeder::addAllFeederPointsToMsg(CtiCommandMsg *pointAddMsg)
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
 
    return *this;
}




/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCFeeder::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCFeeder::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {

        RWDBTable dynamicCCFeederTable = getDatabase().table( "dynamicccfeeder" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCFeederTable.updater();

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["currentvarpointvalue"].assign( _currentvarloadpointvalue )
            << dynamicCCFeederTable["currentwattpointvalue"].assign( _currentwattloadpointvalue )
            << dynamicCCFeederTable["newpointdatareceivedflag"].assign( _newpointdatareceivedflag?"Y":"N" )
            << dynamicCCFeederTable["lastcurrentvarupdatetime"].assign( toRWDBDT((CtiTime)_lastcurrentvarpointupdatetime) );

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

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["estimatedvarpointvalue"].assign( _estimatedvarloadpointvalue )
            << dynamicCCFeederTable["currentdailyoperations"].assign( _currentdailyoperations )                  
            << dynamicCCFeederTable["recentlycontrolledflag"].assign( _recentlycontrolledflag?"Y":"N" )
            << dynamicCCFeederTable["lastoperationtime"].assign( toRWDBDT((CtiTime)_lastoperationtime) );

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

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["varvaluebeforecontrol"].assign( _varvaluebeforecontrol )
            << dynamicCCFeederTable["lastcapbankdeviceid"].assign( _lastcapbankcontrolleddeviceid )
            << dynamicCCFeederTable["busoptimizedvarcategory"].assign( _busoptimizedvarcategory )
            << dynamicCCFeederTable["busoptimizedvaroffset"].assign( _busoptimizedvaroffset );

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
            addFlags[3] = (_preOperationMonitorPointScanFlag?'Y':'N');
            addFlags[4] = (_operationSentWaitFlag?'Y':'N');
            addFlags[5] = (_postOperationMonitorPointScanFlag?'Y':'N');
            addFlags[6] = (_waitForReCloseDelayFlag?'Y':'N');
            addFlags[7] = (_peakTimeFlag?'Y':'N');
            addFlags[8] = (_maxDailyOpsHitFlag?'Y':'N');
            addFlags[9] = (_ovUvDisabledFlag?'Y':'N');
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
            _additionalFlags.append("NNNNNNNNNN");

            updater.clear();

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["ctitimestamp"].assign(toRWDBDT(currentDateTime))
            << dynamicCCFeederTable["powerfactorvalue"].assign( _powerfactorvalue )
            << dynamicCCFeederTable["kvarsolution"].assign( _kvarsolution )
            << dynamicCCFeederTable["estimatedpfvalue"].assign( _estimatedpowerfactorvalue )
            << dynamicCCFeederTable["currentvarpointquality"].assign( _currentvarpointquality )
            << dynamicCCFeederTable["waivecontrolflag"].assign( (_waivecontrolflag?"Y":"N")) 
            << dynamicCCFeederTable["additionalflags"].assign( string2RWCString(_additionalFlags) )
            << dynamicCCFeederTable["currentvoltpointvalue"].assign( _currentvoltloadpointvalue )
            << dynamicCCFeederTable["eventseq"].assign( _eventSeq )
            << dynamicCCFeederTable["currverifycbid"].assign(_currentVerificationCapBankId)
            << dynamicCCFeederTable["currverifycborigstate"].assign(_currentCapBankToVerifyAssumedOrigState)
            << dynamicCCFeederTable["currentwattpointquality"].assign(_currentwattpointquality)
            << dynamicCCFeederTable["currentvoltpointquality"].assign(_currentvoltpointquality)
            << dynamicCCFeederTable["ivcontroltot"].assign(_iVControlTot)
            << dynamicCCFeederTable["ivcount"].assign(_iVCount)
            << dynamicCCFeederTable["iwcontroltot"].assign(_iWControlTot)
            << dynamicCCFeederTable["iwcount"].assign(_iWCount);

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
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted Feeder into DynamicCCFeeder: " << getPAOName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            RWDBInserter inserter = dynamicCCFeederTable.inserter();

            inserter << _paoid
            << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << (_newpointdatareceivedflag?"Y":"N")
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << (_recentlycontrolledflag?"Y":"N")
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
            << (_waivecontrolflag?"Y":"N")
            << string2RWCString(addFlags)
            << _currentvoltloadpointvalue
            << _eventSeq
            << _currentVerificationCapBankId
            << _currentCapBankToVerifyAssumedOrigState
            << _currentwattpointquality
            << _currentvoltpointquality
            << _iVControlTot
            << _iVCount
            <<  _iWControlTot
            <<  _iWCount;


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
    }
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCFeeder::restoreGuts(RWvistream& istrm)
{
    CtiTime tempTime1;
    CtiTime tempTime2;
    LONG numberOfCapBanks;
    CtiCCCapBank* currentCapBank = NULL;

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
    >> _displayorder
    >> _newpointdatareceivedflag
    >> tempTime1
    >> _estimatedvarloadpointid
    >> _estimatedvarloadpointvalue
    >> _dailyoperationsanalogpointid
    >> _powerfactorpointid
    >> _estimatedpowerfactorpointid
    >> _currentdailyoperations
    >> _recentlycontrolledflag
    >> tempTime2
    >> _varvaluebeforecontrol
    >> _powerfactorvalue
    >> _estimatedpowerfactorvalue
    >> _currentvarpointquality
    >> _waivecontrolflag
    >> _controlunits
    >> _decimalPlaces
    >> _peakTimeFlag
    >> _peaklag
    >> _offpklag
    >> _peaklead
    >> _offpklead
    >> _currentvoltloadpointid
    >> _currentvoltloadpointvalue
    >> _currentwattpointquality
    >> _currentvoltpointquality
    >> _targetvarvalue
    >> _solution
    >> _ovUvDisabledFlag;

   
    istrm >> numberOfCapBanks;
    for(LONG i=0;i<numberOfCapBanks;i++)
    {
        istrm >> currentCapBank;
        _cccapbanks.insert(currentCapBank);
    }

    _lastcurrentvarpointupdatetime = CtiTime(tempTime1);
    _lastoperationtime = CtiTime(tempTime2);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCFeeder::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    DOUBLE temppowerfactorvalue = _powerfactorvalue;
    DOUBLE tempestimatedpowerfactorvalue = _estimatedpowerfactorvalue;

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
    << _recentlycontrolledflag
    << _lastoperationtime
    << _varvaluebeforecontrol
    << temppowerfactorvalue
    << tempestimatedpowerfactorvalue
    << _currentvarpointquality
    << _waivecontrolflag
    << _controlunits
    << _decimalPlaces
    << _peakTimeFlag
    << _peaklag
    << _offpklag
    << _peaklead
    << _offpklead
    << _currentvoltloadpointid
    << _currentvoltloadpointvalue
    << _currentwattpointquality
    << _currentvoltpointquality
    << _targetvarvalue
    << _solution
    << _ovUvDisabledFlag;

    ostrm << _cccapbanks.size();
    for(LONG i=0;i<_cccapbanks.size();i++)
    {
        ostrm << (CtiCCCapBank*)_cccapbanks[i];
    }
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::operator=(const CtiCCFeeder& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _parentId = right._parentId;
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
        _currentwattloadpointvalue = right._currentwattloadpointvalue;
        _currentvoltloadpointid = right._currentvoltloadpointid;
        _currentvoltloadpointvalue = right._currentvoltloadpointvalue;
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

        _preOperationMonitorPointScanFlag = right._preOperationMonitorPointScanFlag;  
        _operationSentWaitFlag = right._operationSentWaitFlag;
        _postOperationMonitorPointScanFlag = right._postOperationMonitorPointScanFlag;
        _waitForReCloseDelayFlag = right._waitForReCloseDelayFlag;
        _maxDailyOpsHitFlag = right._maxDailyOpsHitFlag;
        _ovUvDisabledFlag = right._ovUvDisabledFlag;

        _targetvarvalue = right._targetvarvalue;
        _solution = right._solution;
        _integrateflag = right._integrateflag;
        _integrateperiod = right._integrateperiod;
        _iVControlTot = right._iVControlTot;
        _iVCount = right._iVCount;
        _iWControlTot = right._iWControlTot;
        _iWCount = right._iWCount;
        _iVControl = right._iVControl;
        _iWControl = right._iWControl;

        _cccapbanks.clear();
        for(LONG i=0;i<right._cccapbanks.size();i++)
        {
            _cccapbanks.insert(((CtiCCCapBank*)right._cccapbanks[i])->replicate());
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCFeeder::operator==(const CtiCCFeeder& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCFeeder::operator!=(const CtiCCFeeder& right) const
{
    return getPAOId() != right.getPAOId();
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

    Restores given a RWDBReader
---------------------------------------------------------------------------*/

void CtiCCFeeder::restore(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    CtiTime currentDateTime();
    CtiTime dynamicTimeStamp;
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
 
    //rdr["displayorder"] >> _displayorder;
    rdr["strategyid"] >> _strategyId;
    rdr["currentvoltloadpointid"] >> _currentvoltloadpointid;
    rdr["multiMonitorControl"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _multiMonitorFlag = (tempBoolString=="y"?TRUE:FALSE);


    //initialize strategy members
    setStrategyName("default");
    setControlMethod("SubstationBus");
    setMaxDailyOperation(0);
    setMaxOperationDisableFlag(FALSE);
    setPeakLag(0);
    setOffPeakLag(0);
    setPeakLead(0);
    setOffPeakLead(0);
    setPeakVARLag(300);
    setOffPeakVARLag(300);
    setPeakVARLead(-250);
    setOffPeakVARLead(-251);
    setPeakStartTime(0);
    setPeakStopTime(0);
    setControlInterval(0);
    setMaxConfirmTime(0);
    setMinConfirmPercent(0);
    setFailurePercent(0);
    setDaysOfWeek("NNNNNNNN");
    setControlUnits("KVAR");
    setControlDelayTime(0);
    setControlSendRetries(0);


    _displayorder = 0;
    _estimatedvarloadpointid = 0;
    _dailyoperationsanalogpointid = 0;
    _powerfactorpointid = 0;
    _estimatedpowerfactorpointid = 0;

    
    setNewPointDataReceivedFlag(FALSE);
    setLastCurrentVarPointUpdateTime(gInvalidCtiTime);
    setEstimatedVarLoadPointValue(0.0);
    setCurrentDailyOperations(0);
    setRecentlyControlledFlag(FALSE);
    setLastOperationTime(gInvalidCtiTime);
    setVarValueBeforeControl(0.0);
    setLastCapBankControlledDeviceId(0);
    _busoptimizedvarcategory = 1;
    _busoptimizedvaroffset = 0.0;
    _powerfactorvalue = -1000000.0;
    _kvarsolution = 0.0;
    _estimatedpowerfactorvalue = -1000000.0;
    _currentvarpointquality = NormalQuality;
    _waivecontrolflag = FALSE;
    setVerificationFlag(FALSE);
    _additionalFlags = string("NNNNNNNNNNNNNNNNNNNN");
    setPerformingVerificationFlag(FALSE);
    setVerificationDoneFlag(FALSE);
    setPreOperationMonitorPointScanFlag(FALSE);
    setOperationSentWaitFlag(FALSE);
    setPostOperationMonitorPointScanFlag(FALSE);
    setWaitForReCloseDelayFlag(FALSE);
    setMaxDailyOpsHitFlag(FALSE);
    setOvUvDisabledFlag(FALSE);
    setPeakTimeFlag(FALSE);
    setEventSequence(0);
    setCurrentVerificationCapBankId(-1);
    setCurrentVerificationCapBankState(0);
    _currentwattpointquality = NormalQuality;
    _currentvoltpointquality = NormalQuality;

    setTargetVarValue(0);
    setSolution("IDLE");

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

}

void CtiCCFeeder::setStrategyValues(CtiCCStrategyPtr strategy)
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
}
void CtiCCFeeder::setDynamicData(RWDBReader& rdr)
{

    CtiTime dynamicTimeStamp;
    string tempBoolString;
    rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
    rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
    rdr["newpointdatareceivedflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _newpointdatareceivedflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
    rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
    rdr["currentdailyoperations"] >> _currentdailyoperations;
    rdr["recentlycontrolledflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _recentlycontrolledflag = (tempBoolString=="y"?TRUE:FALSE);
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
    _waivecontrolflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["additionalflags"] >> _additionalFlags;
    rdr["currentvoltpointvalue"] >> _currentvoltloadpointvalue;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
    _verificationFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);
    _performingVerificationFlag = (_additionalFlags[1]=='y'?TRUE:FALSE);
    _verificationDoneFlag = (_additionalFlags[2]=='y'?TRUE:FALSE);
    _preOperationMonitorPointScanFlag = (_additionalFlags[3]=='y'?TRUE:FALSE);
    _operationSentWaitFlag = (_additionalFlags[4]=='y'?TRUE:FALSE);
    _postOperationMonitorPointScanFlag = (_additionalFlags[5]=='y'?TRUE:FALSE);
    _waitForReCloseDelayFlag = (_additionalFlags[6]=='y'?TRUE:FALSE);
    _peakTimeFlag = (_additionalFlags[7]=='y'?TRUE:FALSE);
    _maxDailyOpsHitFlag = (_additionalFlags[8]=='y'?TRUE:FALSE);
    _ovUvDisabledFlag = (_additionalFlags[9]=='y'?TRUE:FALSE);
    rdr["eventSeq"] >> _eventSeq;
    rdr["currverifycbid"] >> _currentVerificationCapBankId;
    rdr["currverifycborigstate"] >> _currentCapBankToVerifyAssumedOrigState;
    rdr["currentwattpointquality"] >> _currentwattpointquality;
    rdr["currentvoltpointquality"] >> _currentvoltpointquality;

    rdr["ivcontroltot"] >> _iVControlTot;
    rdr["ivcount"] >> _iVCount;
    rdr["iwcontroltot"] >> _iWControlTot;
    rdr["iwcount"] >> _iWCount;

    
    _insertDynamicDataFlag = FALSE;
    _dirty = false;

}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the string representation of a double
---------------------------------------------------------------------------*/
string CtiCCFeeder::doubleToString(DOUBLE doubleVal, LONG decimalPlaces)
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
        if (capBank->getPAOId() == capBankId)
        {
            //delete capBank;
            itr = getCCCapBanks().erase(itr);
            break;
        }else
            ++itr;

    }
    return;
}

BOOL CtiCCFeeder::checkMaxDailyOpCountExceeded()
{
    BOOL retVal = FALSE;
    if( getMaxDailyOperation() > 0 &&
        _currentdailyoperations == getMaxDailyOperation() )//only send once
    {

        setMaxDailyOpsHitFlag(TRUE);

        string text = ("Feeder Exceeded Max Daily Operations");
        string additional = ("Feeder: ");
        additional += getPAOName();
        if (_LOG_MAPID_INFO) 
        {
            additional += " MapID: ";
            additional += getMapLocationId();
            additional += " (";
            additional += getPAODescription();
            additional += ")";
        }
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalAlarm0, "cap control"));

        //we should disable feeder if the flag says so
       /* if( getMaxOperationDisableFlag() )
        {
            setDisableFlag(TRUE);
       //     setBusUpdatedFlag(TRUE);
            string text = string("Feeder Disabled");
            string additional = string("Feeder: ");
            additional += getPAOName();
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0, "cap control"));

            //keepGoing = FALSE;
            // feeder disable flag is already set, so it will return false.
        } */
        retVal = TRUE;
    }

    if( getMaxOperationDisableFlag() && getMaxDailyOpsHitFlag() )
    {
        if ( !_END_DAY_ON_TRIP ) 
        {
            setDisableFlag(TRUE);
            string text = string("Feeder Disabled");
            string additional = string("Feeder: ");
            additional += getPAOName();
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += getMapLocationId();
                additional += " (";
                additional += getPAODescription();
                additional += ")";
            }
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0, "cap control"));

            retVal = FALSE;
        }
        else
        {
            bool closeFoundFlag = false;
            for (LONG j=0; j<getCCCapBanks().size(); j++) 
            {
                CtiCCCapBank* currentCap = (CtiCCCapBank*)getCCCapBanks()[j];
                if (currentCap->getControlStatus() == CtiCCCapBank::Close ||
                    currentCap->getControlStatus() == CtiCCCapBank::CloseQuestionable ) 
                {
                    retVal = TRUE;
                    closeFoundFlag = true;
                    break;
                }                          
            }
            

            if (!closeFoundFlag) 
            {   
                setDisableFlag(TRUE);
                string text = string("Feeder Disabled");
                string additional = string("Feeder: ");
                additional += getPAOName();
                if (_LOG_MAPID_INFO) 
                {
                    additional += " MapID: ";
                    additional += getMapLocationId();
                    additional += " (";
                    additional += getPAODescription();
                    additional += ")";
                }
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0, "cap control"));

                retVal = FALSE;
            }

        }
    }

    return retVal;
}

string CtiCCFeeder::createTextString(const string& controlMethod, int control, DOUBLE controlValue, DOUBLE monitorValue)//, LONG decimalPlaces)
{
    string text = ("");
    switch (control)
    {
        case 0:
            {
                text += "Open sent, ";
            }
            break;
        case 1:
            {
                text += "Close sent, ";
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
    if (!stringCompareIgnoreCase(getParentControlUnits(),CtiCCSubstationBus::VoltControlUnits))
    {
        if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::SubstationBusControlMethod))
            text += "SubBus-Volt: ";
        else if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod))
            text += "BusOp-Volt: ";
        else if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::IndividualFeederControlMethod))
            text += "IndvFdr-Volt: ";
        else if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::ManualOnlyControlMethod))
            text += "Manual-Volt: ";
        else
            text += "No Method Defined? Volt: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    else if (!stringCompareIgnoreCase(getParentControlUnits(), CtiCCSubstationBus::KVARControlUnits) ||
             !stringCompareIgnoreCase(getParentControlUnits(), CtiCCSubstationBus::PF_BY_KVARControlUnits) ||
             !stringCompareIgnoreCase(getParentControlUnits(), CtiCCSubstationBus::PF_BY_KQControlUnits))
    {
        if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::SubstationBusControlMethod))
            text += "SubBus-Var: ";
        else if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::BusOptimizedFeederControlMethod))
            text += "BusOp-Var: ";
        else if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::IndividualFeederControlMethod))
            text += "IndvFdr-Var: ";
        else if (!stringCompareIgnoreCase(controlMethod,CtiCCSubstationBus::ManualOnlyControlMethod))
            text += "Manual-Var: ";
        else
            text += "No Method Defined? Var:";

        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    if (!stringCompareIgnoreCase(getParentControlUnits(), CtiCCSubstationBus::MultiVoltControlUnits))
    {
        if (!stringCompareIgnoreCase(controlMethod, CtiCCSubstationBus::SubstationBusControlMethod))
            text += "SubBus-MultiVolt: ";
        else if (!stringCompareIgnoreCase(controlMethod, CtiCCSubstationBus::BusOptimizedFeederControlMethod))
            text += "BusOp-MultiVolt: ";
        else if (!stringCompareIgnoreCase(controlMethod, CtiCCSubstationBus::IndividualFeederControlMethod))
            text += "IndvFdr-MultiVolt: ";
        else if (!stringCompareIgnoreCase(controlMethod, CtiCCSubstationBus::ManualOnlyControlMethod))
            text += "Manual-MultiVolt: ";
        else
            text += "No Method Defined? MultiVolt: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    else
    {
    }
    return text; 

}


CtiCCCapBank* CtiCCFeeder::getMonitorPointParentBank(CtiCCMonitorPoint* point)
{

    for (long i = 0; i < _cccapbanks.size(); i++) 
    {                            
        CtiCCCapBankPtr cap = (CtiCCCapBankPtr)_cccapbanks[i];
        if (point->getBankId() == cap->getPAOId()) 
        {
            return cap;
        }
    }
    return NULL;
}
