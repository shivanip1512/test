/*---------------------------------------------------------------------------
        Filename:  strategy.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCStrategy.
                        CtiCCStrategy maintains the state and handles
                        the persistence of strategies for Cap Control.

        Initial Date:  8/18/2000

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#include "dbaccess.h"
#include "strategy.h"
#include "ccid.h"
#include "capbank.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "controller.h"

extern BOOL _CAP_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCStrategy, CTICCSTRATEGY_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCStrategy::CtiCCStrategy()
{
}

CtiCCStrategy::CtiCCStrategy(RWDBReader& rdr)
{
    restore(rdr);
}

CtiCCStrategy::CtiCCStrategy(const CtiCCStrategy& strat)
{
    operator=( strat );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCStrategy::~CtiCCStrategy()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _capbanks.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    Id

    Returns the unique id of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::Id() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _id;
}

/*---------------------------------------------------------------------------
    Name

    Returns the name of the strategy
---------------------------------------------------------------------------*/
const RWCString& CtiCCStrategy::Name() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _name;
}

/*---------------------------------------------------------------------------
    District

    Returns the district of the strategy
---------------------------------------------------------------------------*/
const RWCString& CtiCCStrategy::District() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _district;
}

/*---------------------------------------------------------------------------
    ActualVarPointId

    Returns the actual var point id of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::ActualVarPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _actualid;
}

/*---------------------------------------------------------------------------
    ActualVarPointValue

    Returns the actual var point value of the strategy
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::ActualVarPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _actualval;
}

/*---------------------------------------------------------------------------
    MaxDailyOperation

    Returns the max daily operations of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::MaxDailyOperation() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _max;
}

/*---------------------------------------------------------------------------
    PeakSetPoint

    Returns the peak set point value of the strategy
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::PeakSetPoint() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peak;
}

/*---------------------------------------------------------------------------
    OffPeakSetPoint

    Returns the off peak set point value of the strategy
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::OffPeakSetPoint() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _offpeak;
}

/*---------------------------------------------------------------------------
    PeakStartTime

    Returns the peak start time of the strategy
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCStrategy::PeakStartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _start;
}

/*---------------------------------------------------------------------------
    PeakStopTime

    Returns the peak start time of the strategy
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCStrategy::PeakStopTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _stop;
}

/*---------------------------------------------------------------------------
    CalculatedVarPointId

    Returns the calculated var point id of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::CalculatedVarPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _calculatedid;
}

/*---------------------------------------------------------------------------
    CalculatedVarPointValue

    Returns the calculated var point value of the strategy
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::CalculatedVarPointValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _calculatedval;
}

/*---------------------------------------------------------------------------
    Bandwidth

    Returns the bandwidth value of the strategy
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::Bandwidth() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _bandwidth;
}

/*---------------------------------------------------------------------------
    ControlInterval

    Returns the control interval of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::ControlInterval() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _interval;
}

/*---------------------------------------------------------------------------
    MinResponseTime

    Returns the min response time of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::MinResponseTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _response;
}

/*---------------------------------------------------------------------------
    MinConfirmPercent

    Returns the min confirm percent of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::MinConfirmPercent() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _confirm;
}

/*---------------------------------------------------------------------------
    FailurePercent

    Returns the min confirm percent of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::FailurePercent() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _failure;
}

/*---------------------------------------------------------------------------
    CapBankList

    Returns the cap bank list of the strategy
---------------------------------------------------------------------------*/
RWOrdered& CtiCCStrategy::CapBankList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _capbanks;
}

/*---------------------------------------------------------------------------
    NextCheckTime

    Returns the next check time of the strategy
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCStrategy::NextCheckTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _nextcheck;
}

/*---------------------------------------------------------------------------
    NewPointDataReceived

    Returns the boolean if new point data has been sent by dispatch
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::NewPointDataReceived() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _newpointdatareceived;
}

/*---------------------------------------------------------------------------
    Status

    Returns the status of the strategy
---------------------------------------------------------------------------*/
const RWCString& CtiCCStrategy::Status() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _status;
}

/*---------------------------------------------------------------------------
    Operations

    Returns the number of operations for the current day of the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::Operations() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _operations;
}

/*---------------------------------------------------------------------------
    LastOperation

    Returns the time of the last cap bank operation the strategy
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCStrategy::LastOperation() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastoperation;
}

/*---------------------------------------------------------------------------
    LastCapBankControlled

    Returns the device id of the last cap bank controlled in the strategy
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::LastCapBankControlled() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastcapbank;
}

/*---------------------------------------------------------------------------
    DaysOfWeek

    Returns the days of the week that the strategy recognizes peak changes
---------------------------------------------------------------------------*/
const RWCString& CtiCCStrategy::DaysOfWeek() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _daysofweek;
}

/*---------------------------------------------------------------------------
    PeakOrOffPeak

    Returns if the strategy is in peak or off peak time
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::PeakOrOffPeak() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _peakoroffpeak;
}

/*---------------------------------------------------------------------------
    RecentlyControlled

    Returns the BOOLean if this strategy has controlled a cap bank recently
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::RecentlyControlled() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _recentlycontrolled;
}

/*---------------------------------------------------------------------------
    CalculatedValueBeforeControl

    Returns the calculated value of the strategy before the last control of
    a cap bank
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::CalculatedValueBeforeControl() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _calculatedvaluebeforecontrol;
}

/*---------------------------------------------------------------------------
    StrategyUpdated

    Returns the BOOLean if this strategy has been updated recently
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::StrategyUpdated() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _strategyupdated;
}

/*---------------------------------------------------------------------------
    LastPointUpdate

    Returns the time of the timestamp of the last current var point data msg
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCStrategy::LastPointUpdate() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastpointupdate;
}

/*---------------------------------------------------------------------------
    DecimalPlaces

    Returns if the number of digits to be displayed after the decimal point
---------------------------------------------------------------------------*/
ULONG CtiCCStrategy::DecimalPlaces() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _decimalplaces;
}

/*---------------------------------------------------------------------------
    StatusesReceivedFlag

    Returns a boolean if all the cap bank statuses have been received from
    Dispatch
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::StatusesReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statusesreceivedflag;
}

/*---------------------------------------------------------------------------
    setId

    Sets the id of the strategy - use with caution
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _id = id;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setName

    Sets the name of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _name = name;

    return *this;
}

/*---------------------------------------------------------------------------
    setDistrict

    Sets the district of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setDistrict(const RWCString& district)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _district = district;

    return *this;
}

/*---------------------------------------------------------------------------
    setActualVarPointId

    Sets the actual var point id of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setActualVarPointId(ULONG actualid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _actualid = actualid;

    return *this;
}

/*---------------------------------------------------------------------------
    setActualVarPointValue

    Sets the actual var point value of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setActualVarPointValue(DOUBLE actualval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _actualval = actualval;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the max daily operations of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setMaxDailyOperation(ULONG max)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _max = max;

    return *this;
}

/*---------------------------------------------------------------------------
    setPeakSetPoint

    Sets the peak set point value of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setPeakSetPoint(DOUBLE peak)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peak = peak;

    return *this;
}

/*---------------------------------------------------------------------------
    setOffPeakSetPoint

    Sets the off peak set point value of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setOffPeakSetPoint(DOUBLE offpeak)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _offpeak = offpeak;

    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStartTime

    Sets the peak start time of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setPeakStartTime(const RWDBDateTime& start)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _start = start;

    return *this;
}

/*---------------------------------------------------------------------------
    setPeakStopTime

    Sets the peak stop time of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setPeakStopTime(const RWDBDateTime& stop)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _stop = stop;

    return *this;
}

/*---------------------------------------------------------------------------
    setCalculatedVarPointId

    Sets the calculated var point id of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setCalculatedVarPointId(ULONG calculatedid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _calculatedid = calculatedid;

    return *this;
}

/*---------------------------------------------------------------------------
    setCalculatedVarPointValue

    Sets the calculated var point value of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setCalculatedVarPointValue(DOUBLE calculatedval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _calculatedval = calculatedval;

    return *this;
}

/*---------------------------------------------------------------------------
    setBandwidth

    Sets the bandwidth value of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setBandwidth(DOUBLE bandwidth)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _bandwidth = bandwidth;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInterval

    Sets the control interval of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setControlInterval(ULONG interval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _interval = interval;

    return *this;
}

/*---------------------------------------------------------------------------
    setMinResponseTime

    Sets the min response time of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setMinResponseTime(ULONG response)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _response = response;

    return *this;
}

/*---------------------------------------------------------------------------
    setMinConfirmPercent

    Sets the min confirm percent of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setMinConfirmPercent(ULONG confirm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _confirm = confirm;

    return *this;
}

/*---------------------------------------------------------------------------
    setFailurePercent

    Sets the failure percent of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setFailurePercent(ULONG failure)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _failure = failure;

    return *this;
}

/*---------------------------------------------------------------------------
    figureNextCheckTime

    Figures out when the strategy should be checked again according to the
    control interval.
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::figureNextCheckTime()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    RWDBDateTime currenttime = RWDBDateTime();
    if( _interval != 0 )
    {
        ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%_interval))+_interval;
        _nextcheck = RWDBDateTime(RWTime(tempsum));
    }
    else
    {
        _nextcheck = currenttime;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceived

    Sets the BOOLean if we have received new point data from dispatch.
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setNewPointDataReceived(BOOL received)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _newpointdatareceived = received;

    return *this;
}

/*---------------------------------------------------------------------------
    insertCapBank

    inserts a new cap bank device into the strategy's bank list
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::insertCapBank(CtiCapBank* capbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _capbanks.insert(capbank);

    return *this;
}

/*---------------------------------------------------------------------------
    setStatus

    Sets the status of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setStatus(const RWCString& status)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _status = status;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperations

    Sets the operations for the current day of the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setOperations(ULONG operations)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _operations = operations;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastOperation

    Sets the time that the last cap bank operation occured in the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setLastOperation(const RWDBDateTime& lastoperation)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastoperation = lastoperation;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastCapBankControlled

    Sets the device id of the last cap bank controlled for the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setLastCapBankControlled(ULONG lastcapbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastcapbank = lastcapbank;

    return *this;
}

/*---------------------------------------------------------------------------
    setDaysOfWeek

    Sets the days of the week that the strategy recognizes peak changes
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setDaysOfWeek(const RWCString& days)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _daysofweek = days;

    return *this;
}

/*---------------------------------------------------------------------------
    setPeakOrOffPeak

    Sets if the strategy is in peak or off peak time
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setPeakOrOffPeak(ULONG peakoroff)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _peakoroffpeak = peakoroff;

    return *this;
}

/*---------------------------------------------------------------------------
    setRecentlyControlled

    Sets the BOOLean if this strategy has just been controlled
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setRecentlyControlled(BOOL recentcontrol)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _recentlycontrolled = recentcontrol;

    return *this;
}

/*---------------------------------------------------------------------------
    setCalculatedValueBeforeControl

    Sets the calculated value of the strategy at the time of a cap bank
    control
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setCalculatedValueBeforeControl(DOUBLE oldcalcval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _calculatedvaluebeforecontrol = oldcalcval;

    return *this;
}

/*---------------------------------------------------------------------------
    setStrategyUpdated

    Sets the BOOLean when this strategy is updated
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setStrategyUpdated(BOOL updated)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _strategyupdated = updated;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastPointUpdate

    Sets the time that the last cap bank operation occured in the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setLastPointUpdate(const RWDBDateTime& lastpointupdate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastpointupdate = lastpointupdate;

    return *this;
}

/*---------------------------------------------------------------------------
    setDecimalPlaces

    Sets the number of digits to be displayed after the decimal point
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setDecimalPlaces(ULONG decimalplaces)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _decimalplaces = decimalplaces;

    return *this;
}

/*---------------------------------------------------------------------------
    setStatusesReceivedFlag

    Sets a boolean if all the cap bank statuses have been received from
    Dispatch
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::setStatusesReceivedFlag(BOOL statusesreceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statusesreceivedflag = statusesreceived;

    return *this;
}

/*---------------------------------------------------------------------------
    createIncreaseVarRequest

    Creates a CtiRequestMsg to open the next cap bank to increase the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCStrategy::createIncreaseVarRequest(RWOrdered& pointChanges)
{
    long devID = 0;
    for(int i=_capbanks.entries()-1;i>=0;i--)
    {
        CtiCapBank* current = (CtiCapBank*)_capbanks[i];
        if( !current->DisableFlag() && current->OperationalState() == CtiCapBank::Switched &&
            ( current->ControlStatus() == CtiCapBank::Close ||
              current->ControlStatus() == CtiCapBank::CloseQuestionable ||
              current->ControlStatus() == CtiCapBank::ClosePending ) )
        {
            devID = current->ControlDeviceId();
            setLastCapBankControlled(current->Id());
            current->setControlStatus(CtiCapBank::OpenPending);
            figureActualVarPointValue();
            _operations++;
            if( MaxDailyOperation() &&
                _operations == MaxDailyOperation() + 1 )
            {//only send once
                RWCString text = RWCString("Strategy Exceeded Max Daily Operations");
                RWCString additional = RWCString("Strat: ");
                additional += Name();
                CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,GeneralLogType,SignalEvent));
            }
            current->setOperations(current->Operations() + 1);
            setRecentlyControlled(TRUE);
            setCalculatedValueBeforeControl(CalculatedVarPointValue());
            if( current->StatusPointId() > 0 )
            {
                char tempchar[64] = "";
                RWCString text = RWCString("Open sent, Var Load = ");
                sprintf(tempchar,"%.*f",DecimalPlaces(),CalculatedVarPointValue());
                text += tempchar;
                RWCString additional = RWCString("Strat: ");
                additional += Name();
                pointChanges.insert(new CtiSignalMsg(current->StatusPointId(),0,text,additional,GeneralLogType,SignalEvent));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                pointChanges.insert(new CtiPointDataMsg(current->StatusPointId(),current->ControlStatus(),NormalQuality,StatusPointType));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Cap Bank: " << current->Name()
                << " DeviceID: " << current->Id() << " doesn't have a status point!" << endl;
            }

            if( current->OperationAnalogPointId() > 0 )
            {
                pointChanges.insert(new CtiPointDataMsg(current->OperationAnalogPointId(),current->Operations(),NormalQuality,AnalogPointType));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
            }
            break;
        }
    }
    if( devID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg( devID,"control open" );
        reqMsg->setSOE(4);
        return reqMsg;
    }
    else
        return NULL;
}

/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCStrategy::createDecreaseVarRequest(RWOrdered& pointChanges)
{
    long devID = 0;
    for(int i=0;i<_capbanks.entries();i++)
    {
        CtiCapBank* current = (CtiCapBank*)_capbanks[i];
        if( !current->DisableFlag() && current->OperationalState() == CtiCapBank::Switched &&
            ( current->ControlStatus() == CtiCapBank::Open ||
              current->ControlStatus() == CtiCapBank::OpenQuestionable ||
              current->ControlStatus() == CtiCapBank::OpenPending ) )
        {
            devID = current->ControlDeviceId();
            setLastCapBankControlled(current->Id());
            current->setControlStatus(CtiCapBank::ClosePending);
            figureActualVarPointValue();
            _operations++;
            if( MaxDailyOperation() > 0 &&
                _operations == MaxDailyOperation() + 1 )
            {//only send once
                RWCString text = RWCString("Strategy Exceeded Max Daily Operations");
                RWCString additional = RWCString("Strat: ");
                additional += Name();
                CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,GeneralLogType,SignalEvent));
            }
            current->setOperations(current->Operations() + 1);
            setRecentlyControlled(TRUE);
            setCalculatedValueBeforeControl(CalculatedVarPointValue());
            if( current->StatusPointId() > 0 )
            {
                char tempchar[64] = "";
                RWCString text = RWCString("Close sent, Var Load = ");
                sprintf(tempchar,"%.*f",DecimalPlaces(),CalculatedVarPointValue());
                text += tempchar;
                RWCString additional = RWCString("Strat: ");
                additional += Name();
                pointChanges.insert(new CtiSignalMsg(current->StatusPointId(),0,text,additional,GeneralLogType,SignalEvent));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                pointChanges.insert(new CtiPointDataMsg(current->StatusPointId(),current->ControlStatus(),NormalQuality,StatusPointType));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Cap Bank: " << current->Name()
                << " DeviceID: " << current->Id() << " doesn't have a status point!" << endl;
            }

            if( current->OperationAnalogPointId() > 0 )
            {
                pointChanges.insert(new CtiPointDataMsg(current->OperationAnalogPointId(),current->Operations(),NormalQuality,AnalogPointType));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
            }
            break;
        }
    }
    if( devID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg( devID,"control close" );
        reqMsg->setSOE(4);
        return reqMsg;
    }
    else
        return NULL;
}

/*---------------------------------------------------------------------------
    figureActualVarPointValue

    Figures out the initial actual var point value according to the states
    of the individual cap banks in the strategy
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::figureActualVarPointValue()
{
    DOUBLE tempValue;
    if( RecentlyControlled() )
        tempValue = CalculatedValueBeforeControl();
    else
        tempValue = CalculatedVarPointValue();

    for(UINT i=0;i<_capbanks.entries();i++)
    {
        CtiCapBank* current = (CtiCapBank*)_capbanks[i];
        if( current->ControlStatus() == CtiCapBank::Close ||
            current->ControlStatus() == CtiCapBank::CloseQuestionable )
        {
            tempValue = tempValue + current->BankSize();
        }
    }

    setActualVarPointValue(tempValue);

    return *this;
}

/*---------------------------------------------------------------------------
    figureCurrentSetPoint

    Returns the current set point depending on if it is peak or off peak
    time and sets the set point status
---------------------------------------------------------------------------*/
DOUBLE CtiCCStrategy::figureCurrentSetPoint(unsigned nowInSeconds)
{
    unsigned startInSeconds = (PeakStartTime().hour() * 3600) + (PeakStartTime().minute() * 60) + PeakStartTime().second();
    unsigned stopInSeconds = (PeakStopTime().hour() * 3600) + (PeakStopTime().minute() * 60) + PeakStopTime().second();

    if( isPeakDay() && startInSeconds <= nowInSeconds && nowInSeconds <= stopInSeconds )
    {
        setPeakOrOffPeak(CtiCCStrategy::PeakState);
        return _peak;
    }
    else
    {
        setPeakOrOffPeak(CtiCCStrategy::OffPeakState);
        return _offpeak;
    }
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a BOOLean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::capBankControlStatusUpdate(RWOrdered& pointChanges)
{
    BOOL returnValue = TRUE;
    DOUBLE oldCalcValue = CalculatedValueBeforeControl();
    DOUBLE newCalcValue = CalculatedVarPointValue();
    char tempchar[64] = "";
    RWCString text = "";
    RWCString additional = "";

    for(UINT i=0;i<_capbanks.entries();i++)
    {
        CtiCapBank* current = (CtiCapBank*)_capbanks[i];
        if( current->Id() == LastCapBankControlled() )
        {
            if( current->ControlStatus() == CtiCapBank::OpenPending )
            {
                DOUBLE change = newCalcValue - oldCalcValue;
                if( change < 0 )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Var change in wrong direction? strategy.cpp::capBankControlStatusUpdate(RWOrdered& pointChanges)" << endl;
                }
                DOUBLE ratio = change/current->BankSize();
                if( ratio < MinConfirmPercent()*.01 )
                {
                    if( ratio < FailurePercent()*.01 && FailurePercent() != 0 && MinConfirmPercent() != 0 )
                    {
                        current->setControlStatus(CtiCapBank::OpenFail);
                        text = RWCString("Var Change = ");
                        text += doubleToString(ratio*100.0);
                        text += "%, OpenFail";
                        additional = RWCString("Strat: ");
                        additional = Name();
                    }
                    else if( MinConfirmPercent() != 0 )
                    {
                        current->setControlStatus(CtiCapBank::OpenQuestionable);
                        text = RWCString("Var Change = ");
                        text += doubleToString(ratio*100.0);
                        text += "%, OpenQuestionable";
                        additional = RWCString("Strat: ");
                        additional = Name();
                    }
                    else
                    {
                        current->setControlStatus(CtiCapBank::Open);
                    }
                }
                else
                {
                    current->setControlStatus(CtiCapBank::Open);
                }
            }
            else if( current->ControlStatus() == CtiCapBank::ClosePending )
            {
                DOUBLE change = oldCalcValue - newCalcValue;
                if( change < 0 )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Var change in wrong direction? strategy.cpp::capBankControlStatusUpdate(RWOrdered& pointChanges)" << endl;
                }
                DOUBLE ratio = change/current->BankSize();
                if( ratio < MinConfirmPercent()*.01 )
                {
                    if( ratio < FailurePercent()*.01 && FailurePercent() != 0 && MinConfirmPercent() != 0 )
                    {
                        current->setControlStatus(CtiCapBank::CloseFail);
                        text = RWCString("Var Change = ");
                        text += doubleToString(ratio*100.0);
                        text += "%, CloseFail";
                        additional = RWCString("Strat: ");
                        additional = Name();
                    }
                    else if( MinConfirmPercent() != 0 )
                    {
                        current->setControlStatus(CtiCapBank::CloseQuestionable);
                        text = RWCString("Var Change = ");
                        text += doubleToString(ratio*100.0);
                        text += "%, CloseQuestionable";
                        additional = RWCString("Strat: ");
                        additional = Name();
                    }
                    else
                    {
                        current->setControlStatus(CtiCapBank::Close);
                    }
                }
                else
                {
                    current->setControlStatus(CtiCapBank::Close);
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Last Cap Bank controlled not in pending status - strategy.cpp::capBankControlStatusUpdate(RWOrdered& pointChanges)" << endl;
                returnValue = FALSE;
            }

            if( current->StatusPointId() > 0 )
            {
                if( text.length() > 0 )
                {//if control failed or questionable, create event to be sent to dispatch
                    long tempLong = current->StatusPointId();
                    pointChanges.insert(new CtiSignalMsg(tempLong,0,text,additional,GeneralLogType,SignalEvent));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                }
                pointChanges.insert(new CtiPointDataMsg(current->StatusPointId(),current->ControlStatus(),NormalQuality,StatusPointType));
                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Cap Bank: " << current->Name()
                << " DeviceID: " << current->Id() << " doesn't have a status point!" << endl;
            }
            break;
        }
    }

    setRecentlyControlled(FALSE);

    return returnValue;
}

/*---------------------------------------------------------------------------
    isPeakDay

    Returns a BOOLean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::isPeakDay()
{
    /*-------------------------------------
    Need to check if it is a holiday today
    also, but we must wait until there is
    a dll with a function to do this
    -------------------------------------*/
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    RWDate today;
    ULONG weekday = today.weekDay();
    if( _daysofweek(weekday-1) == 'Y' )
        return TRUE;
    else
        return FALSE;
}

/*---------------------------------------------------------------------------
    areAllCapBankStatusesReceived

    Returns a BOOLean if all the cap banks in the strategy have updated statuses
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::areAllCapBankStatusesReceived()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = TRUE;

    if( !_statusesreceivedflag )
    {
        if( _capbanks.entries() > 0 )
        {
            for(ULONG i=0;i<_capbanks.entries();i++)
            {
                if( !((CtiCapBank*)_capbanks[i])->StatusReceivedFlag() )
                {
                    returnBoolean = FALSE;
                    break;
                }
            }
            _statusesreceivedflag = TRUE;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a BOOLean if the last cap bank controlled expected var changes
    are reflected in the current var level before the min response time
---------------------------------------------------------------------------*/
BOOL CtiCCStrategy::isAlreadyControlled()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnValue = FALSE;

    if( MinConfirmPercent() == 0 )
    {
        returnValue = FALSE;
    }
    else
    {
        DOUBLE oldCalcValue = CalculatedValueBeforeControl();
        DOUBLE newCalcValue = CalculatedVarPointValue();
        for(UINT i=0;i<_capbanks.entries();i++)
        {
            CtiCapBank* current = (CtiCapBank*)_capbanks[i];
            if( current->Id() == LastCapBankControlled() )
            {
                if( current->ControlStatus() == CtiCapBank::OpenPending )
                {
                    DOUBLE change = newCalcValue - oldCalcValue;
                    DOUBLE ratio = change/current->BankSize();
                    if( ratio >= MinConfirmPercent()*.01 )
                    {
                        returnValue = TRUE;
                    }
                    else
                    {
                        returnValue = FALSE;
                    }
                }
                else if( current->ControlStatus() == CtiCapBank::ClosePending )
                {
                    DOUBLE change = oldCalcValue - newCalcValue;
                    DOUBLE ratio = change/current->BankSize();
                    if( ratio >= MinConfirmPercent()*.01 )
                    {
                        returnValue = TRUE;
                    }
                    else
                    {
                        returnValue = FALSE;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Last Cap Bank controlled not in pending status - strategy.cpp::capBankControlStatusUpdate(RWOrdered& pointChanges)" << endl;
                    returnValue = FALSE;
                }
                break;
            }
        }
    }

    return returnValue;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiCCStrategy::dumpDynamicData()
{
    RWDBDateTime currentTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable dynamicCapControlStrategy = getDatabase().table( "dynamiccapcontrolstrategy" );
    RWDBUpdater updater = dynamicCapControlStrategy.updater();

    updater << dynamicCapControlStrategy["newpointdatareceived"].assign(RWCString( ( NewPointDataReceived() ? 'Y': 'N' ) ))
    << dynamicCapControlStrategy["strategyupdated"].assign(RWCString( ( StrategyUpdated() ? 'Y': 'N' ) ))
    << dynamicCapControlStrategy["actualvarpointvalue"].assign(ActualVarPointValue())
    << dynamicCapControlStrategy["nextchecktime"].assign((RWDBDateTime)NextCheckTime())
    << dynamicCapControlStrategy["calcvarpointvalue"].assign(CalculatedVarPointValue())
    << dynamicCapControlStrategy["operations"].assign(Operations())
    << dynamicCapControlStrategy["lastoperation"].assign((RWDBDateTime)LastOperation())
    << dynamicCapControlStrategy["lastcapbankcontrolled"].assign(LastCapBankControlled())
    << dynamicCapControlStrategy["peakoroffpeak"].assign(RWCString( ( PeakOrOffPeak() ? 'Y': 'N' ) ))
    << dynamicCapControlStrategy["recentlycontrolled"].assign(RWCString( ( RecentlyControlled() ? 'Y': 'N' ) ))
    << dynamicCapControlStrategy["calcvaluebeforecontrol"].assign(CalculatedValueBeforeControl())
    << dynamicCapControlStrategy["timestamp"].assign((RWDBDateTime)currentTime)
    << dynamicCapControlStrategy["lastpointupdate"].assign((RWDBDateTime)LastPointUpdate());

    updater.where(dynamicCapControlStrategy["CapStrategyID"]==Id());

    updater.execute( conn );
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCStrategy::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::restoreGuts( istrm );

    istrm >> _id
    >> _name
    >> _district
    >> _actualid
    >> _actualval
    >> _max
    >> _peak
    >> _offpeak
    >> _start.rwtime()
    >> _stop.rwtime()
    >> _calculatedid
    >> _calculatedval
    >> _bandwidth
    >> _interval
    >> _response
    >> _confirm
    >> _failure
    >> _status
    >> _operations
    >> _lastoperation.rwtime()
    >> _lastcapbank
    >> _daysofweek
    >> _peakoroffpeak
    >> _recentlycontrolled
    >> _calculatedvaluebeforecontrol
    >> _lastpointupdate.rwtime()
    >> _decimalplaces
    >> _capbanks;

}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCStrategy::saveGuts(RWvostream& ostrm ) const
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::saveGuts( ostrm );

    ostrm << _id
    << _name
    << _district
    << _actualid
    << _actualval
    << _max
    << _peak
    << _offpeak
    << _start.rwtime()
    << _stop.rwtime()
    << _calculatedid
    << _calculatedval
    << _bandwidth
    << _interval
    << _response
    << _confirm
    << _failure
    << _status
    << _operations
    << _lastoperation.rwtime()
    << _lastcapbank
    << _daysofweek
    << _peakoroffpeak
    << _recentlycontrolled
    << _calculatedvaluebeforecontrol
    << _lastpointupdate.rwtime()
    << _decimalplaces
    << _capbanks;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCStrategy& CtiCCStrategy::operator=(const CtiCCStrategy& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _id = right._id;
        _name = right._name;
        _district = right._district;
        _actualid = right._actualid;
        _actualval = right._actualval;
        _max = right._max;
        _peak = right._peak;
        _offpeak = right._offpeak;
        _start = right._start;
        _stop = right._stop;
        _calculatedid = right._calculatedid;
        _calculatedval = right._calculatedval;
        _bandwidth = right._bandwidth;
        _interval = right._interval;
        _response = right._response;
        _confirm = right._confirm;
        _failure = right._failure;
        _nextcheck = right._nextcheck;
        _newpointdatareceived = right._newpointdatareceived;
        _status = right._status;
        _operations = right._operations;
        _lastoperation = right._lastoperation;
        _lastcapbank = right._lastcapbank;
        _daysofweek = right._daysofweek;
        _peakoroffpeak = right._peakoroffpeak;
        _recentlycontrolled = right._recentlycontrolled;
        _calculatedvaluebeforecontrol = right._calculatedvaluebeforecontrol;
        _strategyupdated = right._strategyupdated;
        _lastpointupdate = right._lastpointupdate;
        _decimalplaces = right._decimalplaces;
        _statusesreceivedflag = right._statusesreceivedflag;

        _capbanks.clearAndDestroy();
        for(UINT i=0;i<right._capbanks.entries();i++)
        {
            _capbanks.insert(((CtiCapBank*)right._capbanks[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCStrategy::operator==(const CtiCCStrategy& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return Id() == right.Id();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCStrategy::operator!=(const CtiCCStrategy& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return Id() != right.Id();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCStrategy* CtiCCStrategy::replicate() const
{
    return(new CtiCCStrategy(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCStrategy::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    // cout << "starting impl restore..." << endl;

    RWDBSchema schema = rdr.table().schema();

    for( UINT i = 0; i < schema.entries(); i++ )
    {
        RWCString col = schema[i].qualifiedName();
        col.toLower();

        RWDBNullIndicator isNull;

        //   cout << "col is:  " << col << endl;
        if( col == "capstrategyid" )
        {
            ULONG id;
            rdr[col] >> id;

            setId(id);
        }
        else if( col == "strategyname" )
        {
            RWCString name;
            rdr[col] >> name;

            setName(name);
        }
        else if( col == "districtname" )
        {
            RWCString district;
            rdr[col] >> district;

            setDistrict(district);
        }
        else if( col == "actualvarpointid" )
        {
            ULONG actualid;
            rdr[col] >> actualid;

            setActualVarPointId(actualid);
        }
        else if( col == "maxdailyoperation" )
        {
            ULONG max;
            rdr[col] >> max;

            setMaxDailyOperation(max);
        }
        else if( col == "peaksetpoint" )
        {
            DOUBLE peak;
            rdr[col] >> peak;

            setPeakSetPoint(peak);
        }
        else if( col == "offpeaksetpoint" )
        {
            DOUBLE offpeak;
            rdr[col] >> offpeak;

            setOffPeakSetPoint(offpeak);
        }
        else if( col == "peakstarttime" )
        {
            RWDBDateTime start;
            rdr[col] >> start;

            setPeakStartTime(start);
        }
        else if( col == "peakstoptime" )
        {
            RWDBDateTime stop;
            rdr[col] >> stop;

            setPeakStopTime(stop);
        }
        else if( col == "calculatedvarloadpointid" )
        {
            ULONG calculatedid;
            rdr[col] >> calculatedid;

            setCalculatedVarPointId(calculatedid);
        }
        else if( col == "bandwidth" )
        {
            DOUBLE bandwidth;
            rdr[col] >> bandwidth;

            setBandwidth(bandwidth);
        }
        else if( col == "controlinterval" )
        {
            ULONG interval;
            rdr[col] >> interval;

            setControlInterval(interval);
        }
        else if( col == "minresponsetime" )
        {
            ULONG response;
            rdr[col] >> response;

            setMinResponseTime(response);
        }
        else if( col == "minconfirmpercent" )
        {
            ULONG confirm;
            rdr[col] >> confirm;

            setMinConfirmPercent(confirm);
        }
        else if( col == "failurepercent" )
        {
            ULONG failure;
            rdr[col] >> failure;

            setFailurePercent(failure);
        }
        else if( col == "status" )
        {
            RWCString status;
            rdr[col] >> status;

            setStatus(status);
        }
        else if( col == "daysofweek" )
        {
            RWCString days;
            rdr[col] >> days;

            setDaysOfWeek(days);
        }
        else if( col == "decimalplaces" )
        {
            ULONG decimalplaces;
            rdr[col] >> decimalplaces;

            setDecimalPlaces(decimalplaces);
        }
    }

    //initialize data members not in database
    setNewPointDataReceived(FALSE);
    setStrategyUpdated(FALSE);
    figureNextCheckTime();
    setActualVarPointValue(0.0);
    setCalculatedVarPointValue(0.0);
    setOperations(0);
    setLastOperation(RWDBDateTime(1990,1,1,0,0,0,0));
    setLastCapBankControlled(0);
    setPeakOrOffPeak(CtiCCStrategy::PeakState);
    setRecentlyControlled(FALSE);
    setCalculatedValueBeforeControl(0.0);
    setLastPointUpdate(RWDBDateTime(1990,1,1,0,0,0,0));
    setStatusesReceivedFlag(FALSE);
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCStrategy::restoreDynamicData(RWDBReader& rdr)
{
    RWDBDateTime currentDateTime = RWDBDateTime();
    RWDBDateTime dynamicTimeStamp;
    RWCString tempStr;

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBSchema schema = rdr.table().schema();

    for( UINT i = 0; i < schema.entries(); i++ )
    {
        RWCString col = schema[i].qualifiedName();
        col.toLower();

        RWDBNullIndicator isNull;

        if( col == "newpointdatareceived" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setNewPointDataReceived(tempStr=="y"?TRUE:FALSE);
        }
        else if( col == "strategyupdated" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setStrategyUpdated(tempStr=="y"?TRUE:FALSE);
        }
        else if( col == "actualvarpointvalue" )
        {
            DOUBLE actual;
            rdr[col] >> actual;

            setActualVarPointValue(actual);
        }
        else if( col == "nextchecktime" )
        {
            RWDBDateTime next;
            rdr[col] >> next;

            _nextcheck = next;
        }
        else if( col == "calcvarpointvalue" )
        {
            DOUBLE calcval;
            rdr[col] >> calcval;

            setCalculatedVarPointValue(calcval);
        }
        else if( col == "operations" )
        {
            ULONG ops;
            rdr[col] >> ops;

            setOperations(ops);
        }
        else if( col == "lastoperation" )
        {
            RWDBDateTime lastop;
            rdr[col] >> lastop;

            setLastOperation(lastop);
        }
        else if( col == "lastcapbankcontrolled" )
        {
            ULONG lastcapcontrolled;
            rdr[col] >> lastcapcontrolled;

            setLastCapBankControlled(lastcapcontrolled);
        }
        else if( col == "peakoroffpeak" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setPeakOrOffPeak(tempStr=="y"?TRUE:FALSE);
        }
        else if( col == "recentlycontrolled" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setRecentlyControlled(tempStr=="y"?TRUE:FALSE);
        }
        else if( col == "calcvaluebeforecontrol" )
        {
            DOUBLE calcbefore;
            rdr[col] >> calcbefore;

            setCalculatedValueBeforeControl(calcbefore);
        }
        else if( col == "timestamp" )
        {
            rdr[col] >> dynamicTimeStamp;
        }
        else if( col == "lastpointupdate" )
        {
            RWDBDateTime lastupdate;
            rdr[col] >> lastupdate;

            setLastPointUpdate(lastupdate);
        }
    }

    //if dynamic timestamp from yesterday, zero out operation count
    if( dynamicTimeStamp.rwdate() < currentDateTime.rwdate() ||
        currentDateTime.hour() == 0 )
    {
        if( currentDateTime.hour() == 0 &&
            dynamicTimeStamp.rwdate() >= (currentDateTime.rwdate()-1) )
        {
            char tempchar[64] = "";
            RWCString text = RWCString("Daily Operations were ");
            _ltoa(Operations(),tempchar,10);
            text += tempchar;
            RWCString additional = RWCString("Strat: ");
            additional += Name();
            CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
        }
        setOperations(0);
    }

    //if not recently controlled , reinit
    if( !RecentlyControlled() || dynamicTimeStamp.seconds() < (currentDateTime.seconds() - 900) )
    {
        setNewPointDataReceived(FALSE);
        setStrategyUpdated(FALSE);
        figureNextCheckTime();
        setActualVarPointValue(0.0);
        setCalculatedVarPointValue(0.0);
        setLastOperation(RWDBDateTime(1990,1,1,0,0,0,0));
        setLastCapBankControlled(0);
        setPeakOrOffPeak(CtiCCStrategy::PeakState);
        setCalculatedValueBeforeControl(0.0);
    }
    else//recently controlled at last dynamic write
    {
        if( MinResponseTime() > 0 )
        {
            //if dynamic info more than 10% older than the minimum response time, reinit
            if( dynamicTimeStamp.seconds() < (currentDateTime.seconds() - (MinResponseTime() * 1.1)) )
            {
                setNewPointDataReceived(FALSE);
                setStrategyUpdated(FALSE);
                figureNextCheckTime();
                setActualVarPointValue(0.0);
                setCalculatedVarPointValue(0.0);
                setLastOperation(RWDBDateTime(1990,1,1,0,0,0,0));
                setLastCapBankControlled(0);
                setPeakOrOffPeak(CtiCCStrategy::PeakState);
                setRecentlyControlled(FALSE);
                setCalculatedValueBeforeControl(0.0);
                setLastPointUpdate(RWDBDateTime(1990,1,1,0,0,0,0));
            }
        }
        else
        {
            //if dynamic info more than 15 minutes old, reinit
            if( dynamicTimeStamp.seconds() < (currentDateTime.seconds() - 900) )
            {
                setNewPointDataReceived(FALSE);
                setStrategyUpdated(FALSE);
                figureNextCheckTime();
                setActualVarPointValue(0.0);
                setCalculatedVarPointValue(0.0);
                setLastOperation(RWDBDateTime(1990,1,1,0,0,0,0));
                setLastCapBankControlled(0);
                setPeakOrOffPeak(CtiCCStrategy::PeakState);
                setRecentlyControlled(FALSE);
                setCalculatedValueBeforeControl(0.0);
                setLastPointUpdate(RWDBDateTime(1990,1,1,0,0,0,0));
            }
            else if( ControlInterval() > 0 && currentDateTime > NextCheckTime() )
            {
                figureNextCheckTime();
            }
        }
    }
}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the RWCString representation of a double
---------------------------------------------------------------------------*/
RWCString CtiCCStrategy::doubleToString(DOUBLE doubleVal)
{
    char tempchar[64] = "";
    RWCString retString = RWCString("");
    sprintf(tempchar,"%d",(int)(doubleVal+0.5));
    retString += tempchar;

    return retString;
}

/* Public Static members */
const RWCString CtiCCStrategy::Enabled = "Enabled";
const RWCString CtiCCStrategy::Disabled = "Disabled";

int CtiCCStrategy::PeakState = 0;
int CtiCCStrategy::OffPeakState = 1;
