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
#include "resolvers.h"

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;

RWDEFINE_COLLECTABLE( CtiCCFeeder, CTICCFEEDER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCFeeder::CtiCCFeeder()
{
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
    //_cccapbanks.clearAndDestroy();
    try
    {
        _cccapbanks.clearAndDestroy();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
const RWCString& CtiCCFeeder::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAODescription() const
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
const RWCString& CtiCCFeeder::getStrategyName() const
{
    return _strategyName;
}
/*---------------------------------------------------------------------------
    getControlMethod

    Returns the controlMethod of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getControlMethod() const
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
const RWCString& CtiCCFeeder::getMapLocationId() const
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
const RWCString& CtiCCFeeder::getDaysOfWeek() const
{
    return _daysofweek;
}
/*---------------------------------------------------------------------------
    getControlUnits

    Returns the ControlUnits of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getControlUnits() const
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
const RWDBDateTime& CtiCCFeeder::getLastCurrentVarPointUpdateTime() const
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
const RWDBDateTime& CtiCCFeeder::getLastOperationTime() const
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
const RWCString& CtiCCFeeder::getParentControlUnits() const
{
    return _parentControlUnits;
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
    
/*---------------------------------------------------------------------------
    getCCCapBanks

    Returns the list of cap banks in the feeder
---------------------------------------------------------------------------*/
RWSortedVector& CtiCCFeeder::getCCCapBanks()
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
CtiCCFeeder& CtiCCFeeder::setPAOCategory(const RWCString& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOClass(const RWCString& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOName(const RWCString& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOType(const RWCString& type)
{
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAODescription(const RWCString& description)
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
CtiCCFeeder& CtiCCFeeder::setStrategyName(const RWCString& strategyName)
{
    _strategyName = strategyName;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlMethod

    Sets the ControlMethod of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlMethod(const RWCString& method)
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
CtiCCFeeder& CtiCCFeeder::setDaysOfWeek(const RWCString& days)
{
    _daysofweek = days;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlUnits

    Sets the ControlUnits of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setControlUnits(const RWCString& contunit)
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiCCFeeder& CtiCCFeeder::setMapLocationId(const RWCString& maplocation)
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiCCFeeder& CtiCCFeeder::setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate)
{
    if( _lastcurrentvarpointupdatetime != lastpointupdate )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiCCFeeder& CtiCCFeeder::setLastOperationTime(const RWDBDateTime& lastoperation)
{
    if( _lastoperationtime != lastoperation )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentvarpointquality = cvpq;
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
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiCCFeeder& CtiCCFeeder::setParentControlUnits(RWCString parentControlUnits)
{
    if (_parentControlUnits != parentControlUnits)
    {
        _dirty = TRUE;
    }
    _parentControlUnits = parentControlUnits;
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


/*---------------------------------------------------------------------------
    findCapBankToDecreaseVars

    .
---------------------------------------------------------------------------*/
CtiCCCapBank* CtiCCFeeder::findCapBankToChangeVars(DOUBLE kvarSolution)
{
    CtiCCCapBank* returnCapBank = NULL;

    if( kvarSolution < 0.0 )
    {
        for(int i=0;i<_cccapbanks.entries();i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

            if( !currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                !currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase) &&
                ( currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ) )
            {
                //have we went past the max daily ops
                if( currentCapBank->getMaxDailyOps() > 0 &&
                    currentCapBank->getCurrentDailyOperations() == currentCapBank->getMaxDailyOps() )//only send once
                {
                    RWCString text = RWCString("CapBank Exceeded Max Daily Operations");
                    RWCString additional = RWCString("CapBank: ");
                    additional += getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalAlarm0));

                    //we should disable feeder if the flag says so
                    if( currentCapBank->getMaxOpsDisableFlag() )
                    {
                        currentCapBank->setDisableFlag(TRUE);
                   //     setBusUpdatedFlag(TRUE);
                        RWCString text = RWCString("CapBank Disabled");
                        RWCString additional = RWCString("CapBank: ");
                        additional += getPAOName();
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0));

                        //keepGoing = FALSE;
                        // feeder disable flag is already set, so it will return false.
                    }
                }

                if( !currentCapBank->getDisableFlag() )
                    returnCapBank = currentCapBank;
                break;
            }
        }
    }
    else if( kvarSolution > 0.0 )
    {
        for(int i=_cccapbanks.entries()-1;i>=0;i--)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            if( !currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                !currentCapBank->getOperationalState().compareTo(CtiCCCapBank::SwitchedOperationalState,RWCString::ignoreCase) &&
                ( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ) )
            {
                //have we went past the max daily ops
                if( currentCapBank->getMaxDailyOps() > 0 &&
                    currentCapBank->getCurrentDailyOperations() == currentCapBank->getMaxDailyOps() )//only send once
                {
                    RWCString text = RWCString("CapBank Exceeded Max Daily Operations");
                    RWCString additional = RWCString("CapBank: ");
                    additional += getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalAlarm0));

                    //we should disable feeder if the flag says so
                    if( currentCapBank->getMaxOpsDisableFlag() )
                    {
                        currentCapBank->setDisableFlag(TRUE);
                   //     setBusUpdatedFlag(TRUE);
                        RWCString text = RWCString("CapBank Disabled");
                        RWCString additional = RWCString("CapBank: ");
                        additional += getPAOName();
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0));

                        //keepGoing = FALSE;
                        // feeder disable flag is already set, so it will return false.
                    }
                }

                if( !currentCapBank->getDisableFlag() )
                    returnCapBank = currentCapBank;
                break;
            }
        }
    }
    else
    {
    }

    return returnCapBank;
}

/*---------------------------------------------------------------------------
    createIncreaseVarRequest

    Creates a CtiRequestMsg to open the next cap bank to increase the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createIncreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, LONG decimalPlaces)
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
        setVarValueBeforeControl(getCurrentVarLoadPointValue());
        if( capBank->getStatusPointId() > 0 )
        {
            char tempchar[80] = "";
            RWCString text = RWCString("Open sent, Var Load = ");
            _snprintf(tempchar,80,"%.*f",decimalPlaces,currentVarLoadPointValue);
            text += tempchar;
            RWCString additional = RWCString("Feeder: ");
            additional += getPAOName();
            pointChanges.insert(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
            pointChanges.insert(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(RWDBDateTime());
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        }

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.insert(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
        }

        reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control open");
        reqMsg->setSOE(4);
    }

    return reqMsg;
}

CtiRequestMsg* CtiCCFeeder::createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, LONG decimalPlaces)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ***VERIFICATION INFO***  CBid: "<<capBank->getPAOId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Open Sent Now " << endl;
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
            char tempchar[80] = "";
            RWCString text = RWCString("Open sent, Var Load = ");
            _snprintf(tempchar,80,"%.*f",decimalPlaces,currentVarLoadPointValue);
            text += tempchar;
            RWCString additional = RWCString("Feeder: ");
            additional += getPAOName();
            pointChanges.insert(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
            pointChanges.insert(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(RWDBDateTime());
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        } 

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.insert(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
        }  

        if  (capBank->getControllerType().contains("CBC 70",RWCString::ignoreCase) &&
             _USE_FLIP_FLAG == TRUE)
            reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control flip");
        else
            reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control open");
        reqMsg->setSOE(4);
    }

    return reqMsg;
}

CtiRequestMsg* CtiCCFeeder::createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, LONG decimalPlaces)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ***VERIFICATION INFO***  CBid: "<<capBank->getPAOId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Close Sent Now " << endl;
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
            char tempchar[80] = "";
            RWCString text = RWCString("Open sent, Var Load = ");
            _snprintf(tempchar,80,"%.*f",decimalPlaces,currentVarLoadPointValue);
            text += tempchar;
            RWCString additional = RWCString("Feeder: ");
            additional += getPAOName();
            pointChanges.insert(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
            pointChanges.insert(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(RWDBDateTime());
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        } 

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.insert(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
        }  

        if  (capBank->getControllerType().contains("CBC 70",RWCString::ignoreCase) &&
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
CtiRequestMsg* CtiCCFeeder::createDecreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, LONG decimalPlaces)
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
        setVarValueBeforeControl(getCurrentVarLoadPointValue());
        if( capBank->getStatusPointId() > 0 )
        {
            char tempchar[80] = "";
            RWCString text = RWCString("Close sent, Var Load = ");
            _snprintf(tempchar,80,"%.*f",decimalPlaces,currentVarLoadPointValue);
            text += tempchar;
            RWCString additional = RWCString("Feeder: ");
            additional += getPAOName();
            pointChanges.insert(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
            pointChanges.insert(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            capBank->setLastStatusChangeTime(RWDBDateTime());
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName()
            << " DeviceID: " << capBank->getPAOId() << " doesn't have a status point!" << endl;
        }

        if( capBank->getOperationAnalogPointId() > 0 )
        {
            pointChanges.insert(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
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

        for(LONG i=0;i<_cccapbanks.entries();i++)
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
/*DOUBLE CtiCCFeeder::figureCurrentSetPoint(const RWDBDateTime& currentDateTime)
{
    return (isPeakTime(currentDateTime)?_peaksetpoint:_offpeaksetpoint);
}   */

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
/*BOOL CtiCCFeeder::isPeakTime(const RWDBDateTime& currentDateTime)
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
    return _peaktimeflag;
} */  

/*---------------------------------------------------------------------------
    checkForAndProvideNeededIndividualControl


---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::checkForAndProvideNeededIndividualControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages, BOOL peakTimeFlag, LONG decimalPlaces, const RWCString& controlUnits)
{
    BOOL returnBoolean = FALSE;

    setPeakTimeFlag(peakTimeFlag);
    DOUBLE lagLevel = (peakTimeFlag?getPeakLag():getOffPeakLag());
    DOUBLE leadLevel = (peakTimeFlag?getPeakLead():getOffPeakLead());
    DOUBLE setpoint = (lagLevel + leadLevel)/2;
    setKVARSolution(CtiCCSubstationBus::calculateKVARSolution(controlUnits,setpoint,getCurrentVarLoadPointValue(),getCurrentWattLoadPointValue()));

    //if current var load is outside of range defined by the set point plus/minus the bandwidths
    CtiRequestMsg* request = NULL;


    //have we went past the max daily ops
    if( getMaxDailyOperation() > 0 &&
        _currentdailyoperations == getMaxDailyOperation() )//only send once
    {
        RWCString text = RWCString("Feeder Exceeded Max Daily Operations");
        RWCString additional = RWCString("Feeder: ");
        additional += getPAOName();
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalAlarm0));

        //we should disable feeder if the flag says so
        if( getMaxOperationDisableFlag() )
        {
            setDisableFlag(TRUE);
       //     setBusUpdatedFlag(TRUE);
            RWCString text = RWCString("Feeder Disabled");
            RWCString additional = RWCString("Feeder: ");
            additional += getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalAlarm0));

            //keepGoing = FALSE;
            // feeder disable flag is already set, so it will return false.
        }
    }



    if( !getDisableFlag() &&
        !getWaiveControlFlag() &&
        ( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality ) &&
        ( currentDateTime.seconds() >= getLastOperationTime().seconds() + getControlDelayTime() ) )
    {
        if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) ||
            !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) ) 
        {
            if( (!_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) &&
                (getCurrentVarLoadPointValue() > lagLevel || getCurrentVarLoadPointValue() < leadLevel )) ||
                (!_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) &&  
                (getCurrentVoltLoadPointValue() < lagLevel || getCurrentVoltLoadPointValue() < leadLevel) ) ) 
            {
        
                try
                {
                    if( ( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) &&
                          lagLevel < getCurrentVarLoadPointValue() ) ||
                        ( !_controlunits.compareTo(CtiCCSubstationBus::VoltControlUnits,RWCString::ignoreCase) &&
                          lagLevel > getCurrentVoltLoadPointValue() ) )
                    {                    
                        //if( _CC_DEBUG )
                        if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Attempting to Decrease Var level in feeder: " << getPAOName().data() << endl;
                        }
                        else
                        {
                            setKVARSolution(-1);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Attempting to Increase Volt level in feeder: " << getPAOName().data() << endl;
                            }
                        }
    
                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());

                        if( capBank != NULL &&
                            capBank->getRecloseDelay() > 0 &&
                            currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime().rwtime() << endl;
                                dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                                dout << " Current Date Time:       " << currentDateTime.rwtime() << endl;
                            }
                        }
                        else
                        {
                            request = createDecreaseVarRequest(capBank , pointChanges, getCurrentVarLoadPointValue(), decimalPlaces);

                            if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Can Not Decrease Var level for feeder: " << getPAOName()
                                << " any further.  All cap banks are already in the Close state in: " << __FILE__ << " at: " << __LINE__ << endl;

                                CtiCCCapBank* currentCapBank = NULL;
                                for(int i=0;i<_cccapbanks.entries();i++)
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
                        if( !_controlunits.compareTo(CtiCCSubstationBus::KVARControlUnits,RWCString::ignoreCase) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Attempting to Increase Var level in feeder: " << getPAOName().data() << endl;
                        }
                        else
                        {
                            setKVARSolution(1);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Attempting to Decrease Volt level in feeder: " << getPAOName().data() << endl;
                            }
                        }

                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());
                        request = createIncreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), decimalPlaces);
    
                        if( request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Can Not Increase Var level for feeder: " << getPAOName()
                            << " any further.  All cap banks are already in the Open state in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                            CtiCCCapBank* currentCapBank = NULL;
                            for(int i=0;i<_cccapbanks.entries();i++)
                            {
                                currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                                dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                            }
                        }
                    }
    
                    if( request != NULL )
                    {
                        pilMessages.insert(request);
                        setLastOperationTime(currentDateTime);
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                        returnBoolean = TRUE;
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
        }
        else if( !controlUnits.compareTo(CtiCCSubstationBus::PF_BY_KVARControlUnits,RWCString::ignoreCase) ||
                 !controlUnits.compareTo(CtiCCSubstationBus::PF_BY_KQControlUnits,RWCString::ignoreCase) )
        {
            if( getKVARSolution() < 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Decrease Var level in feeder: " << getPAOName() << endl;
                }
    
                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());
                if( capBank != NULL )
                {
                    if( capBank->getRecloseDelay() > 0 &&
                        currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Can Not Close Cap Bank: " << capBank->getPAOName() << " because it has not passed its reclose delay." << endl;
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            dout << " Last Status Change Time: " << capBank->getLastStatusChangeTime().rwtime() << endl;
                            dout << " Reclose Delay:           " << capBank->getRecloseDelay() << endl;
                            dout << " Current Date Time:       " << currentDateTime.rwtime() << endl;
                        }
                    }
                    else
                    {
                        DOUBLE adjustedBankKVARReduction = (lagLevel/100.0)*((DOUBLE)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            request = createDecreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), decimalPlaces);
                        }
                        else
                        {//cap bank too big
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                        }
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Decrease Var level for feeder: " << getPAOName()
                    << " any further.  All cap banks are already in the Close state in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int j=0;j<_cccapbanks.entries();j++)
                        {
                            currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
                            dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Attempting to Increase Var level in feeder: " << getPAOName() << endl;
                }
    
                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution());
                if( capBank != NULL )
                {
                    DOUBLE adjustedBankKVARIncrease = (leadLevel/100.0)*((DOUBLE)capBank->getBankSize());
                    if( adjustedBankKVARIncrease <= getKVARSolution() )
                    {
                        request = createIncreaseVarRequest(capBank, pointChanges, getCurrentVarLoadPointValue(), decimalPlaces);
                    }
                    else
                    {//cap bank too big
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << capBank->getPAOName() << ", KVAR size too large to switch" << endl;
                    }
                }
    
                if( capBank == NULL && request == NULL && (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                    << " any further.  All cap banks are already in the Open state in: " << __FILE__ << " at: " << __LINE__ << endl;
    
                    try
                    {
                        CtiCCCapBank* currentCapBank = NULL;
                        for(int j=0;j<_cccapbanks.entries();j++)
                        {
                            currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
                            dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            if( request != NULL )
            {
                pilMessages.insert(request);
                setLastOperationTime(currentDateTime);
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }
                returnBoolean = TRUE;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid control units: " << controlUnits << ", in feeder: " << getPAOName() << endl;
        }
    }
    
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::capBankControlStatusUpdate(RWOrdered& pointChanges, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality)
{
    BOOL returnBoolean = TRUE;
    char tempchar[64] = "";
    RWCString text = "";
    RWCString additional = "";

    for(LONG i=0;i<_cccapbanks.entries();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
        {
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    currentVarPointQuality == NormalQuality )
                {
                    DOUBLE change = currentVarLoadPointValue - varValueBeforeControl;
                    if( change < 0 )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    DOUBLE ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            text = RWCString("Var Change = ");
                            text += doubleToString(ratio*100.0);
                            text += "%, OpenFail";
                            additional = RWCString("Feeder: ");
                            additional = getPAOName();
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            text = RWCString("Var Change = ");
                            text += doubleToString(ratio*100.0);
                            text += "%, OpenQuestionable";
                            additional = RWCString("Feeder: ");
                            additional = getPAOName();
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                    }
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                    text = RWCString("Non Normal Var Quality = ");
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += "%, OpenQuestionable";
                    additional = RWCString("Feeder: ");
                    additional = getPAOName();
                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG ||
                    currentVarPointQuality == NormalQuality )
                {
                    DOUBLE change = varValueBeforeControl - currentVarLoadPointValue;
                    if( change < 0 )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Var change in wrong direction? in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    DOUBLE ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                            text = RWCString("Var Change = ");
                            text += doubleToString(ratio*100.0);
                            text += "%, CloseFail";
                            additional = RWCString("Feeder: ");
                            additional = getPAOName();
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            text = RWCString("Var Change = ");
                            text += doubleToString(ratio*100.0);
                            text += "%, CloseQuestionable";
                            additional = RWCString("Feeder: ");
                            additional = getPAOName();
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                    }
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = RWCString("Non Normal Var Quality = ");
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += "%, CloseQuestionable";
                    additional = RWCString("Feeder: ");
                    additional = getPAOName();
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = FALSE;
            }

            if( currentCapBank->getStatusPointId() > 0 )
            {
                if( text.length() > 0 )
                {//if control failed or questionable, create event to be sent to dispatch
                    long tempLong = currentCapBank->getStatusPointId();
                    pointChanges.insert(new CtiSignalMsg(tempLong,0,text,additional,CapControlLogType,SignalEvent));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                }
                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
            }
            break;
        }
    }

    setRecentlyControlledFlag(FALSE);

    return returnBoolean;
}


BOOL CtiCCFeeder::capBankVerificationStatusUpdate(RWOrdered& pointChanges, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality)
{
    return TRUE;
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

    if( !_IGNORE_NOT_NORMAL_FLAG ||
        getCurrentVarPointQuality() == NormalQuality )
    {
        if( minConfirmPercent > 0 )
        {
            DOUBLE oldVarValue = getVarValueBeforeControl();
            DOUBLE newVarValue = getCurrentVarLoadPointValue();
            for(LONG i=0;i<_cccapbanks.entries();i++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
                {
                    if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending || 
                        currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        DOUBLE change = newVarValue - oldVarValue;
                        DOUBLE ratio = abs(change/currentCapBank->getBankSize());
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                    }
                    /*else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        DOUBLE change = oldVarValue - newVarValue;
                        DOUBLE ratio = change/currentCapBank->getBankSize();
                        if( ratio >= minConfirmPercent*.01 )
                        {
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            returnBoolean = FALSE;
                        }
                    }   */
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Last Cap Bank: "<<getLastCapBankControlledDeviceId()<<" controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                        returnBoolean = FALSE;
                    }
                    break;
                }
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isPastMaxConfirmTime(const RWDBDateTime& currentDateTime, LONG maxConfirmTime, LONG feederRetries)
{
    BOOL returnBoolean = FALSE;

    if( ((getLastOperationTime().seconds() + (maxConfirmTime/_SEND_TRIES)) <= currentDateTime.seconds()) ||
        ((getLastOperationTime().seconds() + (maxConfirmTime/(feederRetries+1))) <= currentDateTime.seconds()) )
    {
        returnBoolean = TRUE;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    attemptToResendControl

    Returns a .
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::attemptToResendControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages, LONG maxConfirmTime)
{
    BOOL returnBoolean = FALSE;

    for(LONG i=0;i<_cccapbanks.entries();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() &&
            !(_USE_FLIP_FLAG && currentCapBank->getControlDeviceType().contains("CBC 70", RWCString::ignoreCase)) )
        {
            if( currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + maxConfirmTime )
            {
                if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                {
                    figureEstimatedVarLoadPointValue();
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        char tempchar[80] = "";
                        RWCString text = RWCString("Resending Open");
                        RWCString additional = RWCString("Feeder: ");
                        additional += getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                        << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }

                    CtiRequestMsg* reqMsg = new CtiRequestMsg(currentCapBank->getControlDeviceId(),"control open");
                    pilMessages.insert(reqMsg);
                    setLastOperationTime(currentDateTime);
                    returnBoolean = TRUE;
                }
                else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    figureEstimatedVarLoadPointValue();
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        char tempchar[80] = "";
                        RWCString text = RWCString("Resending Close");
                        RWCString additional = RWCString("Feeder: ");
                        additional += getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                        << " DeviceID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }

                    CtiRequestMsg* reqMsg = new CtiRequestMsg(currentCapBank->getControlDeviceId(),"control close");
                    pilMessages.insert(reqMsg);
                    setLastOperationTime(currentDateTime);
                    returnBoolean = TRUE;
                }
                else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Cannot Resend Control for Cap Bank: "<< currentCapBank->getPAOName() <<", Not Pending in: " << __FILE__ << " at: " << __LINE__ << endl;
                }
            }
            else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Cannot Resend Control for Cap Bank: "<< currentCapBank->getPAOName() <<", Past Confirm Time in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            break;
        }
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


BOOL CtiCCFeeder::isFeederPerformingVerification()
{
    return TRUE;
}
BOOL CtiCCFeeder::isVerificationAlreadyControlled(LONG minConfirmPercent)
{
    return isAlreadyControlled(minConfirmPercent);
}

//BOOL isVerificationAlreadyControlled(LONG minConfirmPercent); 





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

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCFeeder::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    {

        RWDBTable dynamicCCFeederTable = getDatabase().table( "dynamicccfeeder" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCFeederTable.updater();

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["currentvarpointvalue"].assign( _currentvarloadpointvalue )
            << dynamicCCFeederTable["currentwattpointvalue"].assign( _currentwattloadpointvalue )
            << dynamicCCFeederTable["newpointdatareceivedflag"].assign( RWCString((_newpointdatareceivedflag?'Y':'N')) )
            << dynamicCCFeederTable["lastcurrentvarupdatetime"].assign( (RWDBDateTime)_lastcurrentvarpointupdatetime );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
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
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }

            updater.clear();

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["estimatedvarpointvalue"].assign( _estimatedvarloadpointvalue )
            << dynamicCCFeederTable["currentdailyoperations"].assign( _currentdailyoperations )
            << dynamicCCFeederTable["recentlycontrolledflag"].assign( RWCString((_recentlycontrolledflag?'Y':'N')) )
            << dynamicCCFeederTable["lastoperationtime"].assign( (RWDBDateTime)_lastoperationtime );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
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
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << RWTime() << " - " << updater.asString().data() << endl;
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
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_verificationFlag?'Y':'N');
            addFlags[1] = (_performingVerificationFlag?'Y':'N');
            addFlags[2] = (_verificationDoneFlag?'Y':'N');
            _additionalFlags = RWCString(RWCString(*addFlags) + RWCString(*(addFlags+1)) + RWCString(*(addFlags+2)) + RWCString(*(addFlags + 3), 17));

            updater.clear();

            updater.where(dynamicCCFeederTable["feederid"]==_paoid);

            updater << dynamicCCFeederTable["ctitimestamp"].assign((RWDBDateTime)currentDateTime)
            << dynamicCCFeederTable["powerfactorvalue"].assign( _powerfactorvalue )
            << dynamicCCFeederTable["kvarsolution"].assign( _kvarsolution )
            << dynamicCCFeederTable["estimatedpfvalue"].assign( _estimatedpowerfactorvalue )
            << dynamicCCFeederTable["currentvarpointquality"].assign( _currentvarpointquality )
            << dynamicCCFeederTable["waivecontrolflag"].assign( RWCString((_waivecontrolflag?'Y':'N')) )
            << dynamicCCFeederTable["additionalflags"].assign( _additionalFlags )
            << dynamicCCFeederTable["currentvoltpointvalue"].assign( _currentvoltloadpointvalue );


            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
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
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }

            updater.clear();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted Feeder into DynamicCCFeeder: " << getPAOName() << endl;
            }
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            
            RWDBInserter inserter = dynamicCCFeederTable.inserter();

            inserter << _paoid
            << _currentvarloadpointvalue
            << _currentwattloadpointvalue
            << RWCString((_newpointdatareceivedflag?'Y':'N'))
            << _lastcurrentvarpointupdatetime
            << _estimatedvarloadpointvalue
            << _currentdailyoperations
            << RWCString((_recentlycontrolledflag?'Y':'N'))
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
            << RWCString((_waivecontrolflag?'Y':'N'))
            << RWCString(*addFlags, 20)
            << _currentvoltloadpointvalue;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
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
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    RWTime tempTime1;
    RWTime tempTime2;
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
    >> _currentvoltloadpointvalue;
   
    istrm >> numberOfCapBanks;
    for(LONG i=0;i<numberOfCapBanks;i++)
    {
        istrm >> currentCapBank;
        _cccapbanks.insert(currentCapBank);
    }

    _lastcurrentvarpointupdatetime = RWDBDateTime(tempTime1);
    _lastoperationtime = RWDBDateTime(tempTime2);
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
    << _lastcurrentvarpointupdatetime.rwtime()
    << _estimatedvarloadpointid
    << _estimatedvarloadpointvalue
    << _dailyoperationsanalogpointid
    << _powerfactorpointid
    << _estimatedpowerfactorpointid
    << _currentdailyoperations
    << _recentlycontrolledflag
    << _lastoperationtime.rwtime()
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
    << _currentvoltloadpointvalue;

    ostrm << _cccapbanks.entries();
    for(LONG i=0;i<_cccapbanks.entries();i++)
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
        _waivecontrolflag = right._waivecontrolflag;
        _additionalFlags = right._additionalFlags;
        
        _parentControlUnits = right._parentControlUnits;
        _decimalPlaces = right._decimalPlaces;
        _peakTimeFlag = right._peakTimeFlag;

        _cccapbanks.clearAndDestroy();
        for(LONG i=0;i<right._cccapbanks.entries();i++)
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
    RWDBDateTime currentDateTime = RWDBDateTime();
    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    _disableflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["currentvarloadpointid"] >> _currentvarloadpointid;
    rdr["currentwattloadpointid"] >> _currentwattloadpointid;
    rdr["maplocationid"] >> _maplocationid;
    //rdr["displayorder"] >> _displayorder;
    rdr["strategyid"] >> _strategyId;
    rdr["currentvoltloadpointid"] >> _currentvoltloadpointid;


    //initialize strategy members
    setStrategyName("default");
    setControlMethod("SubstationBus");
    setMaxDailyOperation(0);
    setMaxOperationDisableFlag(FALSE);
    setPeakLag(0);
    setOffPeakLag(0);
    setPeakLead(0);
    setOffPeakLead(0);
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
    setLastCurrentVarPointUpdateTime(gInvalidRWDBDateTime);
    setEstimatedVarLoadPointValue(0.0);
    setCurrentDailyOperations(0);
    setRecentlyControlledFlag(FALSE);
    setLastOperationTime(gInvalidRWDBDateTime);
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
    _additionalFlags = RWCString("NNNNNNNNNNNNNNNNNNNN");
    setPerformingVerificationFlag(FALSE);
    setVerificationDoneFlag(FALSE);

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

}

void CtiCCFeeder::setStrategyValues(CtiCCStrategyPtr strategy)
{
    RWCString tempBoolString;

    _strategyName = strategy->getStrategyName();
    _controlmethod = strategy->getControlMethod();           
    _maxdailyoperation = strategy->getMaxDailyOperation();
    _maxoperationdisableflag = strategy->getMaxOperationDisableFlag();
    _peaklag = strategy->getPeakLag();
    _offpklag = strategy->getOffPeakLag();       
    _peaklead = strategy->getPeakLead();
    _offpklead = strategy->getOffPeakLead();       
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
}
void CtiCCFeeder::setDynamicData(RWDBReader& rdr)
{

    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;
    rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
    rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
    rdr["newpointdatareceivedflag"] >> tempBoolString;
    tempBoolString.toLower();
    _newpointdatareceivedflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
    rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
    rdr["currentdailyoperations"] >> _currentdailyoperations;
    rdr["recentlycontrolledflag"] >> tempBoolString;
    tempBoolString.toLower();
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
    tempBoolString.toLower();
    _waivecontrolflag = (tempBoolString=="y"?TRUE:FALSE);
    rdr["additionalflags"] >> _additionalFlags;
    rdr["currentvoltpointvalue"] >> _currentvoltloadpointvalue;
    _additionalFlags.toLower();
    _verificationFlag = (_additionalFlags.data()[0]=='y'?TRUE:FALSE);
    _performingVerificationFlag = (_additionalFlags.data()[1]=='y'?TRUE:FALSE);
    _verificationDoneFlag = (_additionalFlags.data()[2]=='y'?TRUE:FALSE);


    _insertDynamicDataFlag = FALSE;
    _dirty = false;

}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the RWCString representation of a double
---------------------------------------------------------------------------*/
RWCString CtiCCFeeder::doubleToString(DOUBLE doubleVal)
{
    char tempchar[80] = "";
    RWCString retString = RWCString("");
    _snprintf(tempchar,80,"%d",(int)(doubleVal+0.5));
    retString += tempchar;

    return retString;
}

void CtiCCFeeder::deleteCCCapBank(long capBankId)
{
    RWOrdered& ccCapBanks = getCCCapBanks();
    for (LONG j = 0; j < ccCapBanks.entries(); j++)
    {
        CtiCCCapBank *capBank = (CtiCCCapBank*)ccCapBanks[j];
        if (capBank->getPAOId() == capBankId)
        {
            getCCCapBanks().removeAt(j);
            break;
        }

    }
    return;
}

