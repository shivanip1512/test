/*---------------------------------------------------------------------------
        Filename:  ccfeeder.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCFeeder.
                        CtiCCFeeder maintains the state and handles
                        the persistence of feeders for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

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

extern BOOL _CC_DEBUG;

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _cccapbanks.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the feeder
---------------------------------------------------------------------------*/
const RWCString& CtiCCFeeder::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getPeakSetPoint

    Returns the peak set point of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPeakSetPoint() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peaksetpoint;
}

/*---------------------------------------------------------------------------
    getOffPeakSetPoint

    Returns the off peak set point of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getOffPeakSetPoint() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _offpeaksetpoint;
}

/*---------------------------------------------------------------------------
    getUpperBandwidth

    Returns the Upper bandwidth of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getUpperBandwidth() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _upperbandwidth;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointId

    Returns the current var load point id of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getCurrentVarLoadPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentvarloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentVarLoadPointValue

    Returns the current var load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getCurrentVarLoadPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointId

    Returns the current watt load point id of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getCurrentWattLoadPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentwattloadpointid;
}

/*---------------------------------------------------------------------------
    getCurrentWattLoadPointValue

    Returns the current watt load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getCurrentWattLoadPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentwattloadpointvalue;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getMapLocationId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getLowerBandwidth

    Returns the lower bandwidth of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getLowerBandwidth() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lowerbandwidth;
}

/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the display order of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getDisplayOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _displayorder;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getNewPointDataReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getLastCurrentVarPointUpdateTime

    Returns the last current var point update time of the feeder
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCFeeder::getLastCurrentVarPointUpdateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastcurrentvarpointupdatetime;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointId

    Returns the estimated var load point id of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getEstimatedVarLoadPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _estimatedvarloadpointid;
}

/*---------------------------------------------------------------------------
    getEstimatedVarLoadPointValue

    Returns the estimated var load point value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getEstimatedVarLoadPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _estimatedvarloadpointvalue;
}

/*---------------------------------------------------------------------------
    getStatusesReceivedFlag

    Returns the statuses received flag of the feeder
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getStatusesReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statusesreceivedflag;
}

/*---------------------------------------------------------------------------
    getDailyOperationsAnalogPointId

    Returns the daily operations point id of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getDailyOperationsAnalogPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _dailyoperationsanalogpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getCurrentDailyOperations() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getRecentlyControlledFlag

    Returns the flag if the feeder has been recently controlled
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::getRecentlyControlledFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _recentlycontrolledflag;
}

/*---------------------------------------------------------------------------
    getLastOperationTime

    Returns the last operation time of the feeder
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCFeeder::getLastOperationTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastoperationtime;
}

/*---------------------------------------------------------------------------
    getVarValueBeforeControl

    Returns the var value before control of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getVarValueBeforeControl() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _varvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    getLastCapBankControlledDeviceId

    Returns the device id of the last cap bank controlled of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getLastCapBankControlledDeviceId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastcapbankcontrolleddeviceid;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarCategory

    Returns the bus optimized var category of the feeder
---------------------------------------------------------------------------*/
ULONG CtiCCFeeder::getBusOptimizedVarCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _busoptimizedvarcategory;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarOffset

    Returns the bus optimized var offset of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getBusOptimizedVarOffset() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _busoptimizedvaroffset;
}

/*---------------------------------------------------------------------------
    getPowerFactorValue

    Returns the power factor value of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getPowerFactorValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _powerfactorvalue;
}

/*---------------------------------------------------------------------------
    getKVARSolution

    Returns the kvar solution of the feeder
---------------------------------------------------------------------------*/
DOUBLE CtiCCFeeder::getKVARSolution() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _kvarsolution;
}

/*---------------------------------------------------------------------------
    getCCCapBanks

    Returns the list of cap banks in the feeder
---------------------------------------------------------------------------*/
RWSortedVector& CtiCCFeeder::getCCCapBanks()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _cccapbanks;
}


/*---------------------------------------------------------------------------
    setPAOId

    Sets the pao id of the feeder - use with caution
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAOType(const RWCString& type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakSetPoint

    Sets the peak set point of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPeakSetPoint(DOUBLE peak)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peaksetpoint = peak;
    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakSetPoint

    Sets the off peak set point of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setOffPeakSetPoint(DOUBLE offpeak)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _offpeaksetpoint = offpeak;
    return *this;
}

/*---------------------------------------------------------------------------
    setUpperBandwidth

    Sets the Upper bandwidth of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setUpperBandwidth(DOUBLE bandwidth)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _upperbandwidth = bandwidth;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointId

    Sets the current var load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarLoadPointId(ULONG currentvarid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentvarloadpointid = currentvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentVarLoadPointValue

    Sets the current var load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentVarLoadPointValue(DOUBLE currentvarval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentvarloadpointvalue = currentvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointId

    Sets the current watt load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattLoadPointId(ULONG currentwattid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentwattloadpointid = currentwattid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentWattLoadPointValue

    Sets the current watt load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentWattLoadPointValue(DOUBLE currentwattval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentwattloadpointvalue = currentwattval;
    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setMapLocationId(ULONG maplocation)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maplocationid = maplocation;
    return *this;
}

/*---------------------------------------------------------------------------
    setLowerBandwidth

    Sets the lower bandwidth of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLowerBandwidth(DOUBLE bandwidth)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lowerbandwidth = bandwidth;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the display order of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDisplayOrder(ULONG order)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _displayorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setNewPointDataReceivedFlag(BOOL newpointdatareceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _newpointdatareceivedflag = newpointdatareceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCurrentVarPointUpdateTime

    Sets the last current var point update time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastcurrentvarpointupdatetime = lastpointupdate;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointId

    Sets the estimated var load point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedVarLoadPointId(ULONG estimatedvarid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _estimatedvarloadpointid = estimatedvarid;
    return *this;
}

/*---------------------------------------------------------------------------
    setEstimatedVarLoadPointValue

    Sets the estimated var load point value of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setEstimatedVarLoadPointValue(DOUBLE estimatedvarval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _estimatedvarloadpointvalue = estimatedvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setStatusesReceivedFlag

    Sets the statuses received flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setStatusesReceivedFlag(BOOL statusesreceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statusesreceivedflag = statusesreceived;
    return *this;
}

/*---------------------------------------------------------------------------
    setDailyOperationsAnalogPointId

    Sets the daily operations analog point id of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setDailyOperationsAnalogPointId(ULONG opspointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _dailyoperationsanalogpointid = opspointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setCurrentDailyOperations(ULONG operations)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentdailyoperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlledFlag

    Sets the recently controlled flag of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setRecentlyControlledFlag(BOOL recentlycontrolled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _recentlycontrolledflag = recentlycontrolled;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastOperationTime

    Sets the last operation time of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastOperationTime(const RWDBDateTime& lastoperation)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastoperationtime = lastoperation;
    return *this;
}

/*---------------------------------------------------------------------------
    setVarValueBeforeControl

    Sets the var value before control of the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setVarValueBeforeControl(DOUBLE oldvarval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _varvaluebeforecontrol = oldvarval;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastCapBankControlledDeviceId

    Sets the device id of the last cap bank controlled in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setLastCapBankControlledDeviceId(ULONG lastcapbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastcapbankcontrolleddeviceid = lastcapbank;
    return *this;
}

/*---------------------------------------------------------------------------
    setPowerFactorValue

    Sets the PowerFactorValue in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setPowerFactorValue(DOUBLE pfval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _powerfactorvalue = pfval;
    return *this;
}

/*---------------------------------------------------------------------------
    setKVARSolution

    Sets the KVARSolution in the feeder
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::setKVARSolution(DOUBLE solution)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _kvarsolution = solution;
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
                currentCapBank->getOperationalState() == CtiCCCapBank::SwitchedOperationalState &&
                ( currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ) )
            {
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
                currentCapBank->getOperationalState() == CtiCCCapBank::SwitchedOperationalState &&
                ( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                  currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ) )
            {
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
CtiRequestMsg* CtiCCFeeder::createIncreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, ULONG decimalPlaces)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        setLastCapBankControlledDeviceId(capBank->getPAOId());
        capBank->setControlStatus(CtiCCCapBank::OpenPending);
        figureEstimatedVarLoadPointValue();
        _currentdailyoperations++;
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
            pointChanges.insert(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,GeneralLogType,SignalEvent));
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
            pointChanges.insert(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getCurrentDailyOperations(),NormalQuality,AnalogPointType));
            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
        }

        reqMsg = new CtiRequestMsg(capBank->getControlDeviceId(),"control open");
        reqMsg->setSOE(4);
    }

    return reqMsg;
}

/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createDecreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, ULONG decimalPlaces)
{
    CtiRequestMsg* reqMsg = NULL;
    if( capBank != NULL )
    {
        setLastCapBankControlledDeviceId(capBank->getPAOId());
        capBank->setControlStatus(CtiCCCapBank::ClosePending);
        figureEstimatedVarLoadPointValue();
        _currentdailyoperations++;
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
            pointChanges.insert(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,GeneralLogType,SignalEvent));
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
            pointChanges.insert(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getCurrentDailyOperations(),NormalQuality,AnalogPointType));
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
    DOUBLE tempValue;
    if( getRecentlyControlledFlag() )
        tempValue = getVarValueBeforeControl();
    else
        tempValue = getCurrentVarLoadPointValue();

    for(UINT i=0;i<_cccapbanks.entries();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
            currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
        {
            tempValue = tempValue + currentCapBank->getBankSize();
        }
    }

    setEstimatedVarLoadPointValue(tempValue);

    return *this;
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededIndividualControl


---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::checkForAndProvideNeededIndividualControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages, BOOL peakTimeFlag, ULONG decimalPlaces, const RWCString& controlUnits)
{
    BOOL returnBoolean = FALSE;
    DOUBLE setpoint = (peakTimeFlag?getPeakSetPoint():getOffPeakSetPoint());
    setKVARSolution(CtiCCSubstationBus::calculateKVARSolution(controlUnits,setpoint,getCurrentVarLoadPointValue(),getCurrentWattLoadPointValue()));

    //if current var load is outside of range defined by the set point plus/minus the bandwidths
    CtiRequestMsg* request = NULL;
    if( controlUnits == CtiCCSubstationBus::KVARControlUnits )
    {
        if( (getCurrentVarLoadPointValue() < (setpoint - getUpperBandwidth())) ||
            (getCurrentVarLoadPointValue() > (setpoint + getLowerBandwidth())) )
        {
            try
            {
                if( setpoint < getCurrentVarLoadPointValue() )
                {
                    //if( _CC_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Attempting to Reduce Var level in feeder: " << getPAOName().data() << endl;
                    }

                    request = createDecreaseVarRequest(findCapBankToChangeVars(getKVARSolution()) , pointChanges, getCurrentVarLoadPointValue(), decimalPlaces);

                    if( request == NULL && _CC_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Can Not Reduce Var level for feeder: " << getPAOName()
                        << " any further.  All cap banks are already in the Close state in: " << __FILE__ << " at: " << __LINE__ << endl;

                        CtiCCCapBank* currentCapBank = NULL;
                        for(int i=0;i<_cccapbanks.entries();i++)
                        {
                            currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                            dout << "CapBank: " << currentCapBank->getPAOName() << " ControlStatus: " << currentCapBank->getControlStatus() << " OperationalState: " << currentCapBank->getOperationalState() << " DisableFlag: " << (currentCapBank->getDisableFlag()?"TRUE":"FALSE") << " ControlInhibitFlag: " << (currentCapBank->getControlInhibitFlag()?"TRUE":"FALSE") << endl;
                        }
                    }
                }
                else
                {
                    //if( _CC_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Attempting to Increase Var level in feeder: " << getPAOName().data() << endl;
                    }

                    request = createIncreaseVarRequest(findCapBankToChangeVars(getKVARSolution()), pointChanges, getCurrentVarLoadPointValue(), decimalPlaces);

                    if( request == NULL && _CC_DEBUG )
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
                }

                //regardless what happened the substation bus should be should be sent to the client
                returnBoolean = TRUE;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
    }
    else if( controlUnits == CtiCCSubstationBus::PF_BY_KVARControlUnits ||
             controlUnits == CtiCCSubstationBus::PF_BY_KQControlUnits )
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
                DOUBLE adjustedBankKVARReduction = (getLowerBandwidth()/100.0)*((DOUBLE)capBank->getBankSize());
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

            if( capBank == NULL && request == NULL && _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Can Not Reduce Var level for feeder: " << getPAOName()
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
                DOUBLE adjustedBankKVARIncrease = (getUpperBandwidth()/100.0)*((DOUBLE)capBank->getBankSize());
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

            if( capBank == NULL && request == NULL && _CC_DEBUG )
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
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid control untis: " << controlUnits << ", in feeder: " << getPAOName() << endl;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    figureKVARSolution

    
---------------------------------------------------------------------------*/
void CtiCCFeeder::figureKVARSolution(const RWCString& controlUnits, DOUBLE setPoint)
{
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::capBankControlStatusUpdate(RWOrdered& pointChanges, ULONG minConfirmPercent, ULONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue)
{
    BOOL returnBoolean = TRUE;
    char tempchar[64] = "";
    RWCString text = "";
    RWCString additional = "";

    for(UINT i=0;i<_cccapbanks.entries();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
        {
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
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
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Last Cap Bank controlled not in pending status in: " << __FILE__ << " at: " << __LINE__ << endl;
                returnBoolean = FALSE;
            }

            if( currentCapBank->getStatusPointId() > 0 )
            {
                if( text.length() > 0 )
                {//if control failed or questionable, create event to be sent to dispatch
                    long tempLong = currentCapBank->getStatusPointId();
                    pointChanges.insert(new CtiSignalMsg(tempLong,0,text,additional,GeneralLogType,SignalEvent));
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

/*---------------------------------------------------------------------------
    areAllCapBankStatusesReceived

    Returns a boolean if all the cap banks in the feeder have updated statuses
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::areAllCapBankStatusesReceived()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL tempBoolean = TRUE;

    if( !_statusesreceivedflag )
    {
        if( _cccapbanks.entries() > 0 )
        {
            for(ULONG i=0;i<_cccapbanks.entries();i++)
            {
                if( !((CtiCCCapBank*)_cccapbanks[i])->getStatusReceivedFlag() )
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
    RWRecursiveLock<RWMutexLock>::LockGuard guard(_mutex);

    DOUBLE setpoint = (peakTimeFlag?getPeakSetPoint():getOffPeakSetPoint());

    //if current var load is less than the set point minus the bandwidth
    if( getCurrentVarLoadPointValue() < (setpoint - getLowerBandwidth()) )
    {
        _busoptimizedvarcategory = 0;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - (setpoint - getLowerBandwidth());
    }
    //if current var load is within the range defined by the set point plus/minus the bandwidth
    else if( (getCurrentVarLoadPointValue() > (setpoint - getLowerBandwidth())) ||
             (getCurrentVarLoadPointValue() < (setpoint + getUpperBandwidth())) )
    {
        _busoptimizedvarcategory = 1;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - setpoint;
    }
    //if current var load is more than the set point plus the bandwidth
    else if( getCurrentVarLoadPointValue() > (setpoint + getUpperBandwidth()) )
    {
        _busoptimizedvarcategory = 2;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - (setpoint + getUpperBandwidth());
    }
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the min response time
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isAlreadyControlled(ULONG minConfirmPercent)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( minConfirmPercent > 0 )
    {
        DOUBLE oldVarValue = getVarValueBeforeControl();
        DOUBLE newVarValue = getCurrentVarLoadPointValue();
        for(UINT i=0;i<_cccapbanks.entries();i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            if( currentCapBank->getPAOId() == getLastCapBankControlledDeviceId() )
            {
                if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                {
                    DOUBLE change = newVarValue - oldVarValue;
                    DOUBLE ratio = change/currentCapBank->getBankSize();
                    if( ratio >= minConfirmPercent*.01 )
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

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPastResponseTime

    Returns a boolean if the last control is past the minimum response time.
---------------------------------------------------------------------------*/
BOOL CtiCCFeeder::isPastResponseTime(const RWDBDateTime& currentDateTime, ULONG minResponseTime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( ((getLastOperationTime().seconds() + minResponseTime) <= currentDateTime.seconds()) )
    {
        returnBoolean = TRUE;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCFeeder::dumpDynamicData()
{
    RWDBDateTime currentDateTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable dynamicCCFeederTable = getDatabase().table( "dynamicccfeeder" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCFeederTable.updater();

            updater << dynamicCCFeederTable["currentvarpointvalue"].assign( getCurrentVarLoadPointValue() )
            << dynamicCCFeederTable["currentwattpointvalue"].assign( getCurrentWattLoadPointValue() )
            << dynamicCCFeederTable["newpointdatareceivedflag"].assign( RWCString((getNewPointDataReceivedFlag()?'Y':'N')) )
            << dynamicCCFeederTable["lastcurrentvarupdatetime"].assign( (RWDBDateTime)getLastCurrentVarPointUpdateTime() )
            << dynamicCCFeederTable["estimatedvarpointvalue"].assign( getEstimatedVarLoadPointValue() )
            << dynamicCCFeederTable["currentdailyoperations"].assign( getCurrentDailyOperations() )
            << dynamicCCFeederTable["recentlycontrolledflag"].assign( RWCString((getRecentlyControlledFlag()?'Y':'N')) )
            << dynamicCCFeederTable["lastoperationtime"].assign( (RWDBDateTime)getLastOperationTime() )
            << dynamicCCFeederTable["varvaluebeforecontrol"].assign( getVarValueBeforeControl() )
            << dynamicCCFeederTable["lastcapbankdeviceid"].assign( getLastCapBankControlledDeviceId() )
            << dynamicCCFeederTable["busoptimizedvarcategory"].assign( getBusOptimizedVarCategory() )
            << dynamicCCFeederTable["busoptimizedvaroffset"].assign( getBusOptimizedVarOffset() )
            << dynamicCCFeederTable["ctitimestamp"].assign((RWDBDateTime)currentDateTime);

            updater.where(dynamicCCFeederTable["feederid"]==getPAOId());

            updater.execute( conn );
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted Feeder into DynamicCCFeeder: " << getPAOName() << endl;
            }

            RWDBInserter inserter = dynamicCCFeederTable.inserter();

            inserter << getPAOId()
            << getCurrentVarLoadPointValue()
            << getCurrentWattLoadPointValue()
            << getNewPointDataReceivedFlag()
            << getLastCurrentVarPointUpdateTime()
            << getEstimatedVarLoadPointValue()
            << getCurrentDailyOperations()
            << getRecentlyControlledFlag()
            << getLastOperationTime()
            << getVarValueBeforeControl()
            << getLastCapBankControlledDeviceId()
            << getBusOptimizedVarCategory()
            << getBusOptimizedVarOffset()
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
void CtiCCFeeder::restoreGuts(RWvistream& istrm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWTime tempTime1;
    RWTime tempTime2;
    ULONG numberOfCapBanks;
    CtiCCCapBank* currentCapBank = NULL;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _peaksetpoint
    >> _offpeaksetpoint
    >> _upperbandwidth
    >> _currentvarloadpointid
    >> _currentvarloadpointvalue
    >> _currentwattloadpointid
    >> _currentwattloadpointvalue
    >> _maplocationid
    >> _lowerbandwidth
    >> _displayorder
    >> _newpointdatareceivedflag
    >> tempTime1
    >> _estimatedvarloadpointid
    >> _estimatedvarloadpointvalue
    >> _statusesreceivedflag
    >> _dailyoperationsanalogpointid
    >> _currentdailyoperations
    >> _recentlycontrolledflag
    >> tempTime2
    >> _varvaluebeforecontrol
    >> _lastcapbankcontrolleddeviceid
    >> _powerfactorvalue
    >> _kvarsolution;
    istrm >> numberOfCapBanks;
    for(UINT i=0;i<numberOfCapBanks;i++)
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _peaksetpoint
    << _offpeaksetpoint
    << _upperbandwidth
    << _currentvarloadpointid
    << _currentvarloadpointvalue
    << _currentwattloadpointid
    << _currentwattloadpointvalue
    << _maplocationid
    << _lowerbandwidth
    << _displayorder
    << _newpointdatareceivedflag
    << _lastcurrentvarpointupdatetime.rwtime()
    << _estimatedvarloadpointid
    << _estimatedvarloadpointvalue
    << _statusesreceivedflag
    << _dailyoperationsanalogpointid
    << _currentdailyoperations
    << _recentlycontrolledflag
    << _lastoperationtime.rwtime()
    << _varvaluebeforecontrol
    << _lastcapbankcontrolleddeviceid
    << _powerfactorvalue
    << _kvarsolution;
    ostrm << _cccapbanks.entries();
    for(UINT i=0;i<_cccapbanks.entries();i++)
    {
        ostrm << (CtiCCCapBank*)_cccapbanks[i];
    }
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCFeeder& CtiCCFeeder::operator=(const CtiCCFeeder& right)
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
        _peaksetpoint = right._peaksetpoint;
        _offpeaksetpoint = right._offpeaksetpoint;
        _upperbandwidth = right._upperbandwidth;
        _currentvarloadpointid = right._currentvarloadpointid;
        _currentvarloadpointvalue = right._currentvarloadpointvalue;
        _currentwattloadpointid = right._currentwattloadpointid;
        _currentwattloadpointvalue = right._currentwattloadpointvalue;
        _maplocationid = right._maplocationid;
        _lowerbandwidth = right._lowerbandwidth;
        _displayorder = right._displayorder;
        _newpointdatareceivedflag = right._newpointdatareceivedflag;
        _lastcurrentvarpointupdatetime = right._lastcurrentvarpointupdatetime;
        _estimatedvarloadpointid = right._estimatedvarloadpointid;
        _estimatedvarloadpointvalue = right._estimatedvarloadpointvalue;
        _statusesreceivedflag = right._statusesreceivedflag;
        _dailyoperationsanalogpointid = right._dailyoperationsanalogpointid;
        _currentdailyoperations = right._currentdailyoperations;
        _recentlycontrolledflag = right._recentlycontrolledflag;
        _lastoperationtime = right._lastoperationtime;
        _varvaluebeforecontrol = right._varvaluebeforecontrol;
        _lastcapbankcontrolleddeviceid = right._lastcapbankcontrolleddeviceid;
        _powerfactorvalue = right._powerfactorvalue;
        _kvarsolution = right._kvarsolution;

        _cccapbanks.clearAndDestroy();
        for(UINT i=0;i<right._cccapbanks.entries();i++)
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCFeeder::operator!=(const CtiCCFeeder& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    rdr["peaksetpoint"] >> _peaksetpoint;
    rdr["offpeaksetpoint"] >> _offpeaksetpoint;
    rdr["upperbandwidth"] >> _upperbandwidth;
    rdr["currentvarloadpointid"] >> _currentvarloadpointid;
    rdr["currentwattloadpointid"] >> _currentwattloadpointid;
    rdr["maplocationid"] >> _maplocationid;
    rdr["lowerbandwidth"] >> _lowerbandwidth;
    rdr["displayorder"] >> _displayorder;

    setEstimatedVarLoadPointId(0);
    setStatusesReceivedFlag(FALSE);
    setDailyOperationsAnalogPointId(0);

    rdr["currentvarpointvalue"] >> isNull;
    if( !isNull )
    {
        rdr["currentvarpointvalue"] >> _currentvarloadpointvalue;
        rdr["currentwattpointvalue"] >> _currentwattloadpointvalue;
        rdr["newpointdatareceivedflag"] >> tempBoolString;
        tempBoolString.toLower();
        setNewPointDataReceivedFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["lastcurrentvarupdatetime"] >> _lastcurrentvarpointupdatetime;
        rdr["estimatedvarpointvalue"] >> _estimatedvarloadpointvalue;
        rdr["currentdailyoperations"] >> _currentdailyoperations;
        rdr["recentlycontrolledflag"] >> tempBoolString;
        tempBoolString.toLower();
        setRecentlyControlledFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["lastoperationtime"] >> _lastoperationtime;
        rdr["varvaluebeforecontrol"] >> _varvaluebeforecontrol;
        rdr["lastcapbankdeviceid"] >> _lastcapbankcontrolleddeviceid;
        rdr["busoptimizedvarcategory"] >> _busoptimizedvarcategory;
        rdr["busoptimizedvaroffset"] >> _busoptimizedvaroffset;
		//HACK 
        //rdr["powerfactorvalue"] >> _powerfactorvalue;
        //rdr["kvarsolution"] >> _kvarsolution;
        setPowerFactorValue(-1000000.0);
        setKVARSolution(0.0);
		//HACK 
        rdr["ctitimestamp"] >> dynamicTimeStamp;

        //if dynamic timestamp from yesterday, zero out operation count
        if( dynamicTimeStamp.rwdate() < currentDateTime.rwdate() ||
            currentDateTime.hour() == 0 )
        {
            setCurrentDailyOperations(0);
        }
        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        //initialize dynamic data members
        setCurrentVarLoadPointValue(0.0);
        setCurrentWattLoadPointValue(0.0);
        setNewPointDataReceivedFlag(FALSE);
        setLastCurrentVarPointUpdateTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setEstimatedVarLoadPointValue(0.0);
        setCurrentDailyOperations(0);
        setRecentlyControlledFlag(FALSE);
        setLastOperationTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setVarValueBeforeControl(0.0);
        setLastCapBankControlledDeviceId(0);
        _busoptimizedvarcategory = 1;
        _busoptimizedvaroffset = 0.0;
        _powerfactorvalue = -1000000.0;
        _kvarsolution = 0.0;

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
            {//undefined feeder point
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Undefined Feeder point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Undefined Feeder point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
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

