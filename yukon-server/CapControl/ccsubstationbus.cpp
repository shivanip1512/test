/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSubstationBus.
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#include <rw/tpsrtvec.h>
#include "dbaccess.h"
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

extern BOOL _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCSubstationBus, CTICCSUBSTATIONBUS_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstationBus::CtiCCSubstationBus()
{
}

CtiCCSubstationBus::CtiCCSubstationBus(RWDBReader& rdr)
{
    restore(rdr);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _ccfeeders.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getControlMethod

    Returns the control method of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getControlMethod() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlmethod;
}

/*---------------------------------------------------------------------------
    getMaxDailyOperation

    Returns the max daily operations of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getMaxDailyOperation() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maxdailyoperation;
}

/*---------------------------------------------------------------------------
    getMaxOperationDisableFlag

    Returns the max operations disable flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getMaxOperationDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maxoperationdisableflag;
}

/*---------------------------------------------------------------------------
    getPeakSetPoint

    Returns the peak set point of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getPeakSetPoint() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peaksetpoint;
}

/*---------------------------------------------------------------------------
    getOffPeakSetPoint

    Returns the off peak set point of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getOffPeakSetPoint() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _offpeaksetpoint;
}

/*---------------------------------------------------------------------------
    getPeakStartTime

    Returns the peak start time of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getPeakStartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peakstarttime;
}

/*---------------------------------------------------------------------------
    getPeakStopTime

    Returns the peak stop time of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getPeakStopTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peakstoptime;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getCurrentVarLoadPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentvarloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentVarLoadPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getCurrentWattLoadPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getCurrentWattLoadPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentwattloadpointvalue;
}

/*---------------------------------------------------------------------------
    getBandwidth

    Returns the bandwidth of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getBandwidth() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _bandwidth;
}

/*---------------------------------------------------------------------------
    getControlInterval

    Returns the control interval of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getControlInterval() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlinterval;
}

/*---------------------------------------------------------------------------
    getMinResponseTime

    Returns the minimum response time of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getMinResponseTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _minresponsetime;
}

/*---------------------------------------------------------------------------
    getMinConfirmPercent

    Returns the minimum confirm percent of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getMinConfirmPercent() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _minconfirmpercent;
}

/*---------------------------------------------------------------------------
    getFailurePercent

    Returns the failure percent of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getFailurePercent() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _failurepercent;
}

/*---------------------------------------------------------------------------
    getDaysOfWeek

    Returns the days of the week of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiCCSubstationBus::getDaysOfWeek() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _daysofweek;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getMapLocationId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getDecimalPlaces

    Returns the decimal places of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getDecimalPlaces() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _decimalplaces;
}

/*---------------------------------------------------------------------------
    getNextCheckTime

    Returns the next check time of the substation
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCSubstationBus::getNextCheckTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _nextchecktime;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getNewPointDataReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getBusUpdatedFlag

    Returns the substation updated flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getBusUpdatedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _busupdatedflag;
}

/*---------------------------------------------------------------------------
    getLastCurrentVarPointUpdateTime

    Returns the last current var point update time of the substation
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCSubstationBus::getLastCurrentVarPointUpdateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastcurrentvarpointupdatetime;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointId

    Returns the estimated var load point id of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getEstimatedVarLoadPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getEstimatedVarLoadPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getStatusesReceivedFlag

    Returns the statuses received flag of the substation
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getStatusesReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statusesreceivedflag;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getDailyOperationsAnalogPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getCurrentDailyOperations() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the flag that represents if the substation is in peak time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getPeakTimeFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peaktimeflag;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the substation has been recently controlled
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::getRecentlyControlledFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _recentlycontrolledflag;
}

/*---------------------------------------------------------------------------
    getLastOperationTime

    Returns the last operation time of the substation
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCSubstationBus::getLastOperationTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastoperationtime;
}

/*---------------------------------------------------------------------------
    getVarValueBeforeControl

    Returns the var value before control of the substation
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::getVarValueBeforeControl() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPAOId

    Returns the PAO id of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
ULONG CtiCCSubstationBus::getLastFeederControlledPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastfeedercontrolledpaoid;
}

/*---------------------------------------------------------------------------
    getLastFeederControlledPosition

    Returns the position of the last feeder controlled of the substation
---------------------------------------------------------------------------*/
LONG CtiCCSubstationBus::getLastFeederControlledPosition() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastfeedercontrolledposition;
}

/*---------------------------------------------------------------------------
    getCCFeeders

    Returns the list of feeders in the substation
---------------------------------------------------------------------------*/
RWOrdered& CtiCCSubstationBus::getCCFeeders()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _ccfeeders;
}


/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOCategory(const RWCString& category)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAOType(const RWCString& type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlMethod

    Sets the control method of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlMethod(const RWCString& method)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlmethod = method;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the max daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxDailyOperation(ULONG max)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maxdailyoperation = max;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the max operations disable flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMaxOperationDisableFlag(BOOL maxopdisable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maxoperationdisableflag = maxopdisable;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakSetPoint

    Sets the peak set point of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakSetPoint(DOUBLE peak)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peaksetpoint = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakSetPoint

    Sets the off peak set point of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setOffPeakSetPoint(DOUBLE offpeak)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _offpeaksetpoint = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStartTime

    Sets the peak start time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakStartTime(ULONG starttime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peakstarttime = starttime;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStopTime

    Sets the peak stop time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakStopTime(ULONG stoptime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peakstoptime = stoptime;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarLoadPointId(ULONG currentvarid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentvarloadpointid = currentvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentVarLoadPointValue(DOUBLE currentvarval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentvarloadpointvalue = currentvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattLoadPointId(ULONG currentwattid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentwattloadpointid = currentwattid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentWattLoadPointValue(DOUBLE currentwattval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentwattloadpointvalue = currentwattval;
    return *this;
}

/*---------------------------------------------------------------------------
    setBandwidth

    Sets the bandwidth of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setBandwidth(ULONG bandwidth)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _bandwidth = bandwidth;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlInterval

    Sets the control interval of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setControlInterval(ULONG interval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlinterval = interval;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinResponseTime

    Sets the minimum response time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMinResponseTime(ULONG response)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _minresponsetime = response;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinConfirmPercent

    Sets the min confirm percent of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMinConfirmPercent(ULONG confirm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _minconfirmpercent = confirm;
    return *this;
}

/*---------------------------------------------------------------------------
    setFailurePercent

    Sets the failure percent of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setFailurePercent(ULONG failure)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _failurepercent = failure;
    return *this;
}

/*---------------------------------------------------------------------------
    setDaysOfWeek

    Sets the days of the week of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDaysOfWeek(const RWCString& days)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _daysofweek = days;
    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setMapLocationId(ULONG maplocation)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maplocationid = maplocation;
    return *this;
}

/*---------------------------------------------------------------------------
    setDecimalPlaces

    Sets the decimal places of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDecimalPlaces(ULONG places)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _decimalplaces = places;
    return *this;
}

/*---------------------------------------------------------------------------
    figureNextCheckTime

    Figures out when the substation should be checked again according to the
    control interval.
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::figureNextCheckTime()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    RWDBDateTime currenttime = RWDBDateTime();
    if( _controlinterval != 0 )
    {
        ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_controlinterval))+_controlinterval;
        _nextchecktime = RWDBDateTime(RWTime(tempsum));
    }
    else
    {
        _nextchecktime = currenttime;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setNewPointDataReceivedFlag(BOOL newpointdatareceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _newpointdatareceivedflag = newpointdatareceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setBusUpdatedFlag

    Sets the substation updated flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setBusUpdatedFlag(BOOL busupdated)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _busupdatedflag = busupdated;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastcurrentvarpointupdatetime = lastpointupdate;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedVarLoadPointId(ULONG estimatedvarid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _estimatedvarloadpointid = estimatedvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setEstimatedVarLoadPointValue(DOUBLE estimatedvarval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _estimatedvarloadpointvalue = estimatedvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setStatusesReceivedFlag

    Sets the statuses received flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setStatusesReceivedFlag(BOOL statusesreceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statusesreceivedflag = statusesreceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setDailyOperationsAnalogPointId

    Sets the daily operations analog point id of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setDailyOperationsAnalogPointId(ULONG opanalogpointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _dailyoperationsanalogpointid = opanalogpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setCurrentDailyOperations(ULONG operations)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentdailyoperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakTimeFlag

    Sets the flag if the substation is in peak time
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setPeakTimeFlag(ULONG peaktime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peaktimeflag = peaktime;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setRecentlyControlledFlag(BOOL recentlycontrolled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _recentlycontrolledflag = recentlycontrolled;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastOperationTime

    Sets the last operation time of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastOperationTime(const RWDBDateTime& lastoperation)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastoperationtime = lastoperation;
    return *this;
}

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setVarValueBeforeControl(DOUBLE oldvarval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _varvaluebeforecontrol = oldvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPAOId

    Sets the pao id of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastFeederControlledPAOId(ULONG lastfeederpao)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastfeedercontrolledpaoid = lastfeederpao;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastFeederControlledPosition

    Sets the position of the last feeder controlled in the substation
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::setLastFeederControlledPosition(LONG lastfeederposition)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastfeedercontrolledposition = lastfeederposition;
    return *this;
}


/*---------------------------------------------------------------------------
    figureEstimatedVarLoadPointValue

    Figures out the estimated var point value according to the states
    of the individual cap banks in each of the feeders
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::figureEstimatedVarLoadPointValue()
{
    DOUBLE tempValue;
    if( getRecentlyControlledFlag() )
        tempValue = getVarValueBeforeControl();
    else
        tempValue = getCurrentVarLoadPointValue();

    for(UINT i=0;i<_ccfeeders.entries();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
        RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

        for(UINT j=0;j<ccCapBanks.entries();j++)
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

    return *this;
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededControl


---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::checkForAndProvideNeededControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    BOOL keepGoing = TRUE;

    //have we went past the max daily ops
    if( getMaxDailyOperation() > 0 &&
        _currentdailyoperations == getMaxDailyOperation() )//only send once
    {
        RWCString text = RWCString("Substation Bus Exceeded Max Daily Operations");
        RWCString additional = RWCString("Substation Bus: ");
        additional += getPAOName();
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,GeneralLogType,SignalEvent));

        //we should disable bus if the flag says so
        if( getMaxOperationDisableFlag() )
        {
            setDisableFlag(TRUE);
            setBusUpdatedFlag(TRUE);
            //store->UpdateSubstation(currentSubstationBus);
            RWCString text = RWCString("Substation Bus Disabled");
            RWCString additional = RWCString("Bus: ");
            additional += getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
            keepGoing = FALSE;
        }
    }

    if( keepGoing )
    {
        if( _controlmethod == CtiCCSubstationBus::IndividualFeederControlMethod )
        {
            for(ULONG i=0;i<_ccfeeders.entries();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if( !currentFeeder->getRecentlyControlledFlag() &&
                    (getControlInterval()!=0 ||
                     currentFeeder->getNewPointDataReceivedFlag()) )
                {
                    figureCurrentSetPoint(currentDateTime);//this is to set the Peak Time Flag
                    if( currentFeeder->checkForAndProvideNeededIndividualControl(currentDateTime, pointChanges, pilMessages, getPeakTimeFlag(), getDecimalPlaces()) )
                    {
                        setLastOperationTime(currentDateTime);
                        setBusUpdatedFlag(TRUE);
                        setRecentlyControlledFlag(TRUE);
                        setCurrentDailyOperations(getCurrentDailyOperations() + 1);
                    }
                    //currentFeeder->setNewPointDataReceivedFlag(FALSE);
                }
            }
            //setNewPointDataReceivedFlag(FALSE);
        }
        else if( _controlmethod == CtiCCSubstationBus::SubstationBusControlMethod )
        {
            DOUBLE setpoint = figureCurrentSetPoint(currentDateTime);
            if( (setpoint - getBandwidth() > getCurrentVarLoadPointValue()) ||
                (setpoint + getBandwidth() < getCurrentVarLoadPointValue()) )
            {
                regularSubstationBusControl(setpoint, currentDateTime, pointChanges, pilMessages);
            }
            clearOutNewPointReceivedFlags();
        }
        else if( _controlmethod == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
        {
            DOUBLE setpoint = figureCurrentSetPoint(currentDateTime);
            if( (setpoint - getBandwidth() > getCurrentVarLoadPointValue()) ||
                (setpoint + getBandwidth() < getCurrentVarLoadPointValue()) )
            {
                optimizedSubstationBusControl(setpoint, currentDateTime, pointChanges, pilMessages);
            }
            clearOutNewPointReceivedFlags();
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Attempting to Reduce Var level in substation: " << getPAOName().data() << endl;
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    regularSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::regularSubstationBusControl(DOUBLE setpoint, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    CtiRequestMsg* request = NULL;
    try
    {
        CtiCCFeeder* currentFeeder = NULL;
        LONG currentPosition = getLastFeederControlledPosition();
        ULONG iterations = 0;
        if( setpoint < getCurrentVarLoadPointValue() )
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().data() << endl;
            }

            while( request == NULL &&
                   iterations < _ccfeeders.entries() )
            {
                if( currentPosition >= _ccfeeders.entries()-1 )
                {
                    currentPosition = 0;
                }
                else
                {
                    currentPosition++;
                }
                currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                request = currentFeeder->createDecreaseVarRequest(pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                iterations++;
            }

            if( request == NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Can Not Reduce Var level for substation bus: " << getPAOName()
                << " any further.  All cap banks are already in the Close state" << endl;
            }
        }
        else
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().data() << endl;
            }

            while( request == NULL &&
                   iterations < _ccfeeders.entries() )
            {
                if( currentPosition <= 0 )
                {
                    currentPosition = _ccfeeders.entries()-1;
                }
                else
                {
                    currentPosition--;
                }
                currentFeeder = (CtiCCFeeder*)_ccfeeders[currentPosition];
                request = currentFeeder->createIncreaseVarRequest(pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                iterations++;
            }

            if( request == NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                << " any further.  All cap banks are already in the Open state" << endl;
            }
        }
        if( request != NULL )
        {
            pilMessages.insert(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlledPAOId(currentFeeder->getPAOId());
            setLastFeederControlledPosition(currentPosition);
            ((CtiCCFeeder*)_ccfeeders[currentPosition])->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            setCurrentDailyOperations(getCurrentDailyOperations() + 1);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    optimizedSubstationBusControl


---------------------------------------------------------------------------*/
void CtiCCSubstationBus::optimizedSubstationBusControl(DOUBLE setpoint, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages)
{
    CtiRequestMsg* request = NULL;
    CtiCCFeeder* lastFeederControlled = NULL;
    int positionLastFeederControlled = -1;
    try
    {
        RWTPtrSortedVector<CtiCCFeeder, FeederVARComparison<CtiCCFeeder> > varSortedFeeders;
        DOUBLE setpoint = figureCurrentSetPoint(currentDateTime);

        for(int i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
            currentFeeder->fillOutBusOptimizedInfo(getPeakTimeFlag());
            varSortedFeeders.insert(currentFeeder);
        }

        if( setpoint < getCurrentVarLoadPointValue() )
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Attempting to Decrease Var level in substation bus: " << getPAOName().data() << endl;
            }

            for(int j=0;j<varSortedFeeders.entries();j++)
            {
                request = ((CtiCCFeeder*)varSortedFeeders[j])->createDecreaseVarRequest(pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                if( request != NULL )
                {
                    lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                    positionLastFeederControlled = j;
                    break;
                }
            }

            if( request == NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Can Not Reduce Var level for substation bus: " << getPAOName()
                << " any further.  All cap banks are already in the Close state" << endl;
            }
        }
        else
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Attempting to Increase Var level in substation bus: " << getPAOName().data() << endl;
            }

            long devID = 0;
            for(int j=varSortedFeeders.entries()-1;j>=0;j--)
            {
                request = ((CtiCCFeeder*)varSortedFeeders[j])->createIncreaseVarRequest(pointChanges, getCurrentVarLoadPointValue(), getDecimalPlaces());
                if( request != NULL )
                {
                    lastFeederControlled = (CtiCCFeeder*)varSortedFeeders[j];
                    positionLastFeederControlled = j;
                    break;
                }
            }

            if( request == NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Can Not Increase Var level for substation bus: " << getPAOName()
                << " any further.  All cap banks are already in the Open state" << endl;
            }
        }
        if( request != NULL )
        {
            pilMessages.insert(request);
            setLastOperationTime(currentDateTime);
            setLastFeederControlledPAOId(lastFeederControlled->getPAOId());
            setLastFeederControlledPosition(positionLastFeederControlled);
            lastFeederControlled->setLastOperationTime(currentDateTime);
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            setCurrentDailyOperations(getCurrentDailyOperations() + 1);
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.insert(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            setRecentlyControlledFlag(TRUE);
        }
        //setNewPointDataReceivedFlag(FALSE);
        //for(int j=0;j<_ccfeeders.entries();j++)
        //{
            //((CtiCCFeeder*)_ccfeeders[j])->setNewPointDataReceivedFlag(FALSE);
        //}
        //regardless what happened the substation bus should be should be sent to the client
        setBusUpdatedFlag(TRUE);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    figureCurrentSetPoint

    Returns the current set point depending on if it is peak or off peak
    time and sets the set point status
---------------------------------------------------------------------------*/
DOUBLE CtiCCSubstationBus::figureCurrentSetPoint(const RWDBDateTime& currentDateTime)
{
    unsigned nowInSeconds = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
    if( isPeakDay() && getPeakStartTime() <= nowInSeconds && nowInSeconds <= getPeakStopTime() )
    {
        setPeakTimeFlag(TRUE);
        return _peaksetpoint;
    }
    else
    {
        setPeakTimeFlag(FALSE);
        return _offpeaksetpoint;
    }
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::capBankControlStatusUpdate(RWOrdered& pointChanges)
{
    BOOL returnBoolean = TRUE;
    char tempchar[64] = "";
    RWCString text = "";
    RWCString additional = "";

    if( _controlmethod == CtiCCSubstationBus::IndividualFeederControlMethod ||
        _controlmethod == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
    {
        ULONG recentlyControlledFeeders = 0;
        for(UINT i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

            if( currentFeeder->getRecentlyControlledFlag() )
            {
                if( currentFeeder->isAlreadyControlled(getMinConfirmPercent()) ||
                    currentFeeder->isPastResponseTime(RWDBDateTime(),getMinResponseTime()) )
                {
                    currentFeeder->capBankControlStatusUpdate(pointChanges,getMinConfirmPercent(),getFailurePercent(),currentFeeder->getVarValueBeforeControl(),currentFeeder->getCurrentVarLoadPointValue());
                    //currentFeeder->setNewPointDataReceivedFlag(FALSE);
                }
                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    recentlyControlledFeeders++;
                }
            }
        }
        if( recentlyControlledFeeders == 0 )
        {
            setRecentlyControlledFlag(FALSE);
        }
        figureEstimatedVarLoadPointValue();
    }
    else if( _controlmethod == CtiCCSubstationBus::SubstationBusControlMethod )
    {
        for(UINT i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];

            if( currentFeeder->getPAOId() == getLastFeederControlledPAOId() )
            {
                currentFeeder->capBankControlStatusUpdate(pointChanges,getMinConfirmPercent(),getFailurePercent(),getVarValueBeforeControl(),getCurrentVarLoadPointValue());
                setRecentlyControlledFlag(FALSE);
                figureEstimatedVarLoadPointValue();
                break;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
    }

    //setNewPointDataReceivedFlag(FALSE);

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isVarCheckNeeded

    Returns a boolean if the control interval is up or if new point data has
    been received for all the points associated with the bus.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isVarCheckNeeded(const RWDBDateTime& currentDateTime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( getControlInterval() > 0 )
    {
        returnBoolean = (getNextCheckTime() <= currentDateTime);
    }
    else
    {
        if( _controlmethod == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
        {
            if( _ccfeeders.entries() > 0 )
            {
                //confirm cap bank changes on just the feeder var value
                if( getRecentlyControlledFlag() )
                {
                    try
                    {
                        if( ((CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()])->getPAOId() == getLastFeederControlledPAOId() )
                        {
                            if( ((CtiCCFeeder*)_ccfeeders[getLastFeederControlledPosition()])->getNewPointDataReceivedFlag() )
                            {
                                returnBoolean = TRUE;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                    if( !returnBoolean )
                    {
                        for(ULONG i=0;i<_ccfeeders.entries();i++)
                        {
                            if( ((CtiCCFeeder*)_ccfeeders[i])->getPAOId() == getLastFeederControlledPAOId() )
                            {
                                if( ((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                                {
                                    returnBoolean = TRUE;
                                    break;
                                }
                            }
                        }
                    }
                }
                //only try to control bus optimized if we have new var values
                //for the bus and all feeders off the bus
                else if( _newpointdatareceivedflag )
                {
                    returnBoolean = TRUE;
                    for(ULONG i=0;i<_ccfeeders.entries();i++)
                    {
                        if( !((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                        {
                            returnBoolean = FALSE;
                            break;
                        }
                    }
                }
            }
        }
        else if( _controlmethod == CtiCCSubstationBus::IndividualFeederControlMethod )
        {
            if( _ccfeeders.entries() > 0 )
            {
                for(ULONG i=0;i<_ccfeeders.entries();i++)
                {
                    if( ((CtiCCFeeder*)_ccfeeders[i])->getNewPointDataReceivedFlag() )
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
            }
        }
        else if( _controlmethod == CtiCCSubstationBus::SubstationBusControlMethod )
        {
            if( _ccfeeders.entries() > 0 )
            {
                returnBoolean = _newpointdatareceivedflag;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid Control Method in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPeakDay()
{
    //-------------------------------------
    //Need to check if it is a holiday today
    //also, but we must wait until there is
    //a dll with a function to do this
    //-------------------------------------
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    RWTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    if( _daysofweek(start_tm.tm_wday) == 'Y' &&
        ( _daysofweek(7) == 'Y' ||
          !CtiHolidayManager::getInstance().isHoliday() ) )
        return TRUE;
    else
        return FALSE;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::clearOutNewPointReceivedFlags()
{
    setNewPointDataReceivedFlag(FALSE);
    for(int i=0;i<_ccfeeders.entries();i++)
    {
        ((CtiCCFeeder*)_ccfeeders[i])->setNewPointDataReceivedFlag(FALSE);
    }
}

/*---------------------------------------------------------------------------
    areAllCapBankStatusesReceived

    Returns a boolean if all the cap banks in each feeder have updated statuses
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::areAllCapBankStatusesReceived()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL tempBoolean = TRUE;

    if( !_statusesreceivedflag )
    {
        if( _ccfeeders.entries() > 0 )
        {
            for(ULONG i=0;i<_ccfeeders.entries();i++)
            {
                if( !((CtiCCFeeder*)_ccfeeders[i])->areAllCapBankStatusesReceived() )
                {
                    tempBoolean = FALSE;
                    break;
                }
            }
        }
    }

    _statusesreceivedflag = tempBoolean;
    return _statusesreceivedflag;
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the min response time
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isAlreadyControlled()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( _controlmethod == CtiCCSubstationBus::IndividualFeederControlMethod ||
        _controlmethod == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
    {
        if( getMinConfirmPercent() > 0 )
        {
            for(ULONG i=0;i<_ccfeeders.entries();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    if( currentFeeder->isAlreadyControlled(getMinConfirmPercent()) )
                    {
                        returnBoolean = TRUE;
                        break;
                    }
                }
            }
        }
    }
    else if( _controlmethod == CtiCCSubstationBus::SubstationBusControlMethod )
    {
        if( getMinConfirmPercent() > 0 )
        {
            DOUBLE oldCalcValue = getVarValueBeforeControl();
            DOUBLE newCalcValue = getCurrentVarLoadPointValue();
            for(UINT i=0;i<_ccfeeders.entries();i++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)_ccfeeders[i];
                if( getLastFeederControlledPAOId() == currentFeeder->getPAOId() )
                {
                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                    for(UINT j=0;j<ccCapBanks.entries();j++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
                        if( currentCapBank->getPAOId() == currentFeeder->getLastCapBankControlledDeviceId() )
                        {
                            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                            {
                                DOUBLE change = newCalcValue - oldCalcValue;
                                DOUBLE ratio = change/currentCapBank->getBankSize();
                                if( ratio >= getMinConfirmPercent()*.01 )
                                {
                                    returnBoolean = TRUE;
                                }
                                else
                                {
                                    returnBoolean = FALSE;
                                }
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                DOUBLE change = oldCalcValue - newCalcValue;
                                DOUBLE ratio = change/currentCapBank->getBankSize();
                                if( ratio >= getMinConfirmPercent()*.01 )
                                {
                                    returnBoolean = TRUE;
                                }
                                else
                                {
                                    returnBoolean = FALSE;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                                returnBoolean = FALSE;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPastResponseTime

    Returns a boolean if the last control is past the minimum response time.
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBus::isPastResponseTime(const RWDBDateTime& currentDateTime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( _controlmethod == CtiCCSubstationBus::IndividualFeederControlMethod )
    {
        for(UINT i=0;i<_ccfeeders.entries();i++)
        {
            CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)_ccfeeders[i];
            if( currentCCFeeder->getRecentlyControlledFlag() &&
                currentCCFeeder->isPastResponseTime(currentDateTime,getMinResponseTime()) )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }
    else
    {
        if( ((getLastOperationTime().seconds() + getMinResponseTime()) <= currentDateTime.seconds()) )
        {
            returnBoolean = TRUE;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::dumpDynamicData()
{
    RWDBDateTime currentDateTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable dynamicCCSubstationBusTable = getDatabase().table( "dynamicccsubstationbus" );

        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCSubstationBusTable.updater();

            updater << dynamicCCSubstationBusTable["currentvarpointvalue"].assign( getCurrentVarLoadPointValue() )
            << dynamicCCSubstationBusTable["currentwattpointvalue"].assign( getCurrentWattLoadPointValue() )
            << dynamicCCSubstationBusTable["nextchecktime"].assign( (RWDBDateTime)getNextCheckTime() )
            << dynamicCCSubstationBusTable["newpointdatareceivedflag"].assign( RWCString((getNewPointDataReceivedFlag()?'Y':'N')) )
            << dynamicCCSubstationBusTable["busupdatedflag"].assign( RWCString((getBusUpdatedFlag()?'Y':'N')) )
            << dynamicCCSubstationBusTable["lastcurrentvarupdatetime"].assign( (RWDBDateTime)getLastCurrentVarPointUpdateTime() )
            << dynamicCCSubstationBusTable["estimatedvarpointvalue"].assign( getEstimatedVarLoadPointValue() )
            << dynamicCCSubstationBusTable["currentdailyoperations"].assign( getCurrentDailyOperations() )
            << dynamicCCSubstationBusTable["peaktimeflag"].assign( RWCString((getPeakTimeFlag()?'Y':'N')) )
            << dynamicCCSubstationBusTable["recentlycontrolledflag"].assign( RWCString((getRecentlyControlledFlag()?'Y':'N')) )
            << dynamicCCSubstationBusTable["lastoperationtime"].assign( (RWDBDateTime)getLastOperationTime() )
            << dynamicCCSubstationBusTable["varvaluebeforecontrol"].assign( getVarValueBeforeControl() )
            << dynamicCCSubstationBusTable["lastfeederpaoid"].assign( getLastFeederControlledPAOId() )
            << dynamicCCSubstationBusTable["lastfeederposition"].assign( getLastFeederControlledPosition() )
            << dynamicCCSubstationBusTable["ctitimestamp"].assign((RWDBDateTime)currentDateTime);

            updater.where(dynamicCCSubstationBusTable["substationbusid"]==getPAOId());

            updater.execute( conn );
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted substation bus into DynamicCCSubstationBus: " << getPAOName() << endl;
            }

            RWDBInserter inserter = dynamicCCSubstationBusTable.inserter();

            inserter << getPAOId()
            << getCurrentVarLoadPointValue()
            << getCurrentWattLoadPointValue()
            << getNextCheckTime()
            << getNewPointDataReceivedFlag()
            << getBusUpdatedFlag()
            << getLastCurrentVarPointUpdateTime()
            << getEstimatedVarLoadPointValue()
            << getCurrentDailyOperations()
            << getPeakTimeFlag()
            << getRecentlyControlledFlag()
            << getLastOperationTime()
            << getVarValueBeforeControl()
            << getLastFeederControlledPAOId()
            << getLastFeederControlledPosition()
            << currentDateTime;

            /*if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }*/

            inserter.execute( conn );

            _insertDynamicDataFlag = FALSE;
        }
    }
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCSubstationBus::restoreGuts(RWvistream& istrm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _controlmethod
    >> _maxdailyoperation
    >> _maxoperationdisableflag
    >> _peaksetpoint
    >> _offpeaksetpoint
    >> _peakstarttime
    >> _peakstoptime
    >> _currentvarloadpointid
    >> _currentvarloadpointvalue
    >> _currentwattloadpointid
    >> _currentwattloadpointvalue
    >> _bandwidth
    >> _controlinterval
    >> _minresponsetime
    >> _minconfirmpercent
    >> _failurepercent
    >> _daysofweek
    >> _maplocationid
    >> _decimalplaces
    >> tempTime1
    >> _newpointdatareceivedflag
    >> _busupdatedflag
    >> tempTime2
    >> _estimatedvarloadpointid
    >> _estimatedvarloadpointvalue
    >> _statusesreceivedflag
    >> _dailyoperationsanalogpointid
    >> _currentdailyoperations
    >> _peaktimeflag
    >> _recentlycontrolledflag
    >> tempTime3
    >> _varvaluebeforecontrol
    >> _lastfeedercontrolledpaoid
    >> _lastfeedercontrolledposition
    >> _ccfeeders;

    _nextchecktime = RWDBDateTime(tempTime1);
    _lastcurrentvarpointupdatetime = RWDBDateTime(tempTime2);
    _lastoperationtime = RWDBDateTime(tempTime3);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSubstationBus::saveGuts(RWvostream& ostrm ) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _controlmethod
    << _maxdailyoperation
    << _maxoperationdisableflag
    << _peaksetpoint
    << _offpeaksetpoint
    << _peakstarttime
    << _peakstoptime
    << _currentvarloadpointid
    << _currentvarloadpointvalue
    << _currentwattloadpointid
    << _currentwattloadpointvalue
    << _bandwidth
    << _controlinterval
    << _minresponsetime
    << _minconfirmpercent
    << _failurepercent
    << _daysofweek
    << _maplocationid
    << _decimalplaces
    << _nextchecktime.rwtime()
    << _newpointdatareceivedflag
    << _busupdatedflag
    << _lastcurrentvarpointupdatetime.rwtime()
    << _estimatedvarloadpointid
    << _estimatedvarloadpointvalue
    << _statusesreceivedflag
    << _dailyoperationsanalogpointid
    << _currentdailyoperations
    << _peaktimeflag
    << _recentlycontrolledflag
    << _lastoperationtime.rwtime()
    << _varvaluebeforecontrol
    << _lastfeedercontrolledpaoid
    << _lastfeedercontrolledposition
    << _ccfeeders;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationBus& CtiCCSubstationBus::operator=(const CtiCCSubstationBus& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _controlmethod = right._controlmethod;
        _maxdailyoperation = right._maxdailyoperation;
        _maxoperationdisableflag = right._maxoperationdisableflag;
        _peaksetpoint = right._peaksetpoint;
        _offpeaksetpoint = right._offpeaksetpoint;
        _peakstarttime = right._peakstarttime;
        _peakstoptime = right._peakstoptime;
        _currentvarloadpointid = right._currentvarloadpointid;
        _currentvarloadpointvalue = right._currentvarloadpointvalue;
        _currentwattloadpointid = right._currentwattloadpointid;
        _currentwattloadpointvalue = right._currentwattloadpointvalue;
        _bandwidth = right._bandwidth;
        _controlinterval = right._controlinterval;
        _minresponsetime = right._minresponsetime;
        _minconfirmpercent = right._minconfirmpercent;
        _failurepercent = right._failurepercent;
        _daysofweek = right._daysofweek;
        _maplocationid = right._maplocationid;
        _decimalplaces = right._decimalplaces;
        _nextchecktime = right._nextchecktime;
        _newpointdatareceivedflag = right._newpointdatareceivedflag;
        _busupdatedflag = right._busupdatedflag;
        _lastcurrentvarpointupdatetime = right._lastcurrentvarpointupdatetime;
        _estimatedvarloadpointid = right._estimatedvarloadpointid;
        _estimatedvarloadpointvalue = right._estimatedvarloadpointvalue;
        _statusesreceivedflag = right._statusesreceivedflag;
        _dailyoperationsanalogpointid = right._dailyoperationsanalogpointid;
        _currentdailyoperations = right._currentdailyoperations;
        _peaktimeflag = right._peaktimeflag;
        _recentlycontrolledflag = right._recentlycontrolledflag;
        _lastoperationtime = right._lastoperationtime;
        _varvaluebeforecontrol = right._varvaluebeforecontrol;
        _lastfeedercontrolledpaoid = right._lastfeedercontrolledpaoid;
        _lastfeedercontrolledposition = right._lastfeedercontrolledposition;

        _ccfeeders.clearAndDestroy();
        for(UINT i=0;i<right._ccfeeders.entries();i++)
        {
            _ccfeeders.insert(((CtiCCFeeder*)right._ccfeeders[i])->replicate());
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCSubstationBus::operator==(const CtiCCSubstationBus& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCSubstationBus::operator!=(const CtiCCSubstationBus& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["controlmethod"] >> _controlmethod;
    rdr["maxdailyoperation"] >> _maxdailyoperation;
    rdr["maxoperationdisableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setMaxOperationDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["peaksetpoint"] >> _peaksetpoint;
    rdr["offpeaksetpoint"] >> _offpeaksetpoint;
    rdr["peakstarttime"] >> _peakstarttime;
    rdr["peakstoptime"] >> _peakstoptime;
    rdr["currentvarloadpointid"] >> _currentvarloadpointid;
    rdr["currentwattloadpointid"] >> _currentwattloadpointid;
    rdr["bandwidth"] >> _bandwidth;
    rdr["controlinterval"] >> _controlinterval;
    rdr["minresponsetime"] >> _minresponsetime;
    rdr["minconfirmpercent"] >> _minconfirmpercent;
    rdr["failurepercent"] >> _failurepercent;
    rdr["daysofweek"] >> _daysofweek;
    rdr["maplocationid"] >> _maplocationid;
    rdr["decimalplaces"] >> _decimalplaces;

    setEstimatedVarLoadPointId(0);
    setStatusesReceivedFlag(FALSE);
    setDailyOperationsAnalogPointId(0);

    rdr["currentvarpointvalue"] >> isNull;
    if( !isNull )
    {
        rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
        rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
        rdr["nextchecktime"] >> _nextchecktime;
        rdr["newpointdatareceivedflag"] >> tempBoolString;
        tempBoolString.toLower();
        setNewPointDataReceivedFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["busupdatedflag"] >> tempBoolString;
        tempBoolString.toLower();
        setBusUpdatedFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
        rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
        rdr["currentdailyoperations"] >> _currentdailyoperations;
        rdr["peaktimeflag"] >> tempBoolString;
        tempBoolString.toLower();
        setPeakTimeFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["recentlycontrolledflag"] >> tempBoolString;
        tempBoolString.toLower();
        setRecentlyControlledFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["lastoperationtime"] >> _lastoperationtime;
        rdr["varvaluebeforecontrol"] >> _varvaluebeforecontrol;
        rdr["lastfeederpaoid"] >> _lastfeedercontrolledpaoid;
        rdr["lastfeederposition"] >> _lastfeedercontrolledposition;
        rdr["ctitimestamp"] >> dynamicTimeStamp;

        //if dynamic timestamp from yesterday, zero out operation count
        if( dynamicTimeStamp.rwdate() < currentDateTime.rwdate() ||
            currentDateTime.hour() == 0 )
        {
            if( currentDateTime.hour() == 0 &&
                dynamicTimeStamp.rwdate() >= (currentDateTime.rwdate()-1) )
            {
                char tempchar[64] = "";
                RWCString text = RWCString("Daily Operations were ");
                _ltoa(getCurrentDailyOperations(),tempchar,10);
                text += tempchar;
                RWCString additional = RWCString("Sub Bus: ");
                additional += getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
            }
            setCurrentDailyOperations(0);
        }
        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        //initialize dynamic data members
        setCurrentVarLoadPointValue(0.0);
        setCurrentWattLoadPointValue(0.0);
        figureNextCheckTime();
        setNewPointDataReceivedFlag(FALSE);
        setBusUpdatedFlag(FALSE);
        setLastCurrentVarPointUpdateTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setEstimatedVarLoadPointValue(0.0);
        setCurrentDailyOperations(0);
        setPeakTimeFlag(TRUE);
        setRecentlyControlledFlag(FALSE);
        setLastOperationTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setVarValueBeforeControl(0.0);
        setLastFeederControlledPAOId(0);
        setLastFeederControlledPosition(-1);

        _insertDynamicDataFlag = TRUE;
    }

    rdr["pointid"] >> isNull;
    if( !isNull )
    {
        LONG tempPointId = -1000;
        LONG tempPointOffset = -1000;
        RWCString tempPointType = "(none)";
        rdr["pointid"] >> tempPointId;
        rdr["pointoffset"] >> tempPointOffset;
        rdr["pointtype"] >> tempPointType;
        if( resolvePointType(tempPointType) == AnalogPointType )
        {
            if( tempPointOffset==1 )
            {//estimated vars point
                setEstimatedVarLoadPointId(tempPointId);
            }
            else if( tempPointOffset==2 )
            {//daily operations point
                setDailyOperationsAnalogPointId(tempPointId);
            }
            else
            {//undefined bus point
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Undefined Substation Bus point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Undefined Substation Bus point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the RWCString representation of a double
---------------------------------------------------------------------------*/
RWCString CtiCCSubstationBus::doubleToString(DOUBLE doubleVal)
{
    char tempchar[80] = "";
    RWCString retString = RWCString("");
    _snprintf(tempchar,80,"%d",(int)(doubleVal+0.5));
    retString += tempchar;

    return retString;
}

/* Public Static members */
const RWCString CtiCCSubstationBus::IndividualFeederControlMethod   = "IndividualFeeder";
const RWCString CtiCCSubstationBus::SubstationBusControlMethod      = "SubstationBus";
const RWCString CtiCCSubstationBus::BusOptimizedFeederControlMethod = "BusOptimizedFeeder";

