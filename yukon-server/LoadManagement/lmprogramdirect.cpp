/*---------------------------------------------------------------------------
        Filename:  lmprogramdirect.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMProgramDirect.
                        CtiLMProgramDirect maintains the state and handles
                        the persistence of direct programs in Load
                        Management.

        Initial Date:  2/9/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmprogramdirect.h"
#include "lmprogramdirectgear.h"
#include "lmgroupbase.h"
#include "lmgrouppoint.h"
#include "devicetypes.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "desolvers.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"
#include "msg_pcrequest.h"
#include "lmcontrolareatrigger.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramDirect, CTILMPROGRAMDIRECT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramDirect::CtiLMProgramDirect()
{
}

CtiLMProgramDirect::CtiLMProgramDirect(RWDBReader& rdr)
{
    restore(rdr);
}

CtiLMProgramDirect::CtiLMProgramDirect(const CtiLMProgramDirect& directprog)
{
    operator=(directprog);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramDirect::~CtiLMProgramDirect()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    _lmprogramdirectgears.clearAndDestroy();
    _lmprogramdirectgroups.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getCurrentGearNumber

    Returns the number of the current direct program gear starts at 0 and
    goes upto _lmprogramdirectgears.entries()-1
---------------------------------------------------------------------------*/
ULONG CtiLMProgramDirect::getCurrentGearNumber() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentgearnumber;
}

/*---------------------------------------------------------------------------
    getLastGroupControlled

    Returns the device id of the last lm group that was controlled in the
    program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramDirect::getLastGroupControlled() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastgroupcontrolled;
}

/*---------------------------------------------------------------------------
    getDirectStartTime

    Returns the direct start time for a manual control of the direct program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getDirectStartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _directstarttime;
}

/*---------------------------------------------------------------------------
    getDirectStopTime

    Returns the direct stop time for a manual control of the direct program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getDirectStopTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _directstoptime;
}

/*---------------------------------------------------------------------------
    getLMProgramDirectGears

    Returns the pointer to a list of gears for this direct program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramDirect::getLMProgramDirectGears()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lmprogramdirectgears;
}

/*---------------------------------------------------------------------------
    getLMProgramDirectGroups

    Returns the pointer to a list of groups for this direct program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramDirect::getLMProgramDirectGroups()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lmprogramdirectgroups;
}

/*---------------------------------------------------------------------------
    setCurrentGearNumber

    Sets the number of the current direct program gear starts at 0 and
    goes upto _lmprogramdirectgears.entries()-1
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setCurrentGearNumber(ULONG currentgear)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentgearnumber = currentgear;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastGroupControlled

    Sets the device id of the last lm group that was controlled in the
    program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setLastGroupControlled(ULONG lastcontrolled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastgroupcontrolled = lastcontrolled;

    return *this;
}

/*---------------------------------------------------------------------------
    setDirectStartTime

    Sets the direct start time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStartTime(const RWDBDateTime& start)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _directstarttime = start;

    return *this;
}

/*---------------------------------------------------------------------------
    setDirectStopTime

    Sets the direct stop time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStopTime(const RWDBDateTime& stop)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _directstoptime = stop;

    return *this;
}


/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the direct program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::reduceProgramLoad(DOUBLE loadReductionNeeded, ULONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.entries() > 0 && _lmprogramdirectgroups.entries() > 0 )
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.entries() )
        {
            ULONG previousGearNumber = _currentgearnumber;
            BOOL gearChangeBoolean = hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901);
            currentGearObject = getCurrentGearObject();

            if( gearChangeBoolean &&
                getProgramState() != CtiLMProgramBase::InactiveState )
            {
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Gear Change, LM Program: " << getPAOName() << ", previous gear number: " << previousGearNumber << ", new gear number: " << _currentgearnumber << endl;
                }
                expectedLoadReduced = updateProgramControlForGearChange(previousGearNumber, multiPilMsg, multiDispatchMsg);
            }
            else
            {

                if( getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    getProgramState() != CtiLMProgramBase::ActiveState )
                {
                    setProgramState(CtiLMProgramBase::ActiveState);
                    setDirectStartTime(RWDBDateTime());
                    setDirectStopTime(RWDBDateTime(1990,1,1,0,0,0,0));
                    {
                        RWCString text = RWCString("Automatic Start, LM Program: ");
                        text += getPAOName();
                        RWCString additional = RWCString("");
                        CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                        signal->setSOE(2);

                        multiDispatchMsg->insert(signal);
                        if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << text << ", " << additional << endl;
                        }
                    }
                }

                if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::TimeRefreshMethod )
                {
                    ULONG refreshRate = currentGearObject->getMethodRate();
                    ULONG shedTime = currentGearObject->getMethodPeriod();
                    ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                    }

                    if( _LM_DEBUG )
                    {
                        if( getProgramState() == CtiLMProgramBase::ActiveState )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Controlling additional time refresh groups, LM Program: " << getPAOName() << ", number of additional groups: " << numberOfGroupsToTake << endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Controlling time refresh groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                        }
                    }

                    for(ULONG i=0;i<numberOfGroupsToTake;i++)
                    {
                        CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup != NULL )
                        {
                            multiPilMsg->insert( currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority) );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            if( currentGearObject->getPercentReduction() > 0.0 )
                            {
                                expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                            }
                            else
                            {
                                expectedLoadReduced += currentLMGroup->getKWCapacity();
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }

                    if( getProgramState() == CtiLMProgramBase::ActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                        for(ULONG j=0;j<_lmprogramdirectgroups.entries();j++)
                        {
                            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[j];
                            if( currentLMGroup->getGroupControlState() != CtiLMGroupBase::ActiveState )
                            {
                                setProgramState(CtiLMProgramBase::ActiveState);
                                break;
                            }
                        }
                    }
                }
                else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::SmartCycleMethod )
                {
                    ULONG percent = currentGearObject->getMethodRate();
                    ULONG period = currentGearObject->getMethodPeriod();
                    ULONG cycleCount = currentGearObject->getMethodRateCount();
                    RWCString cycleCountDownType = currentGearObject->getMethodOptionType();

                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Smart Cycling all groups, LM Program: " << getPAOName() << endl;
                    }
                    for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                    {
                        CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            //reset the default for each group if the previous groups was lower
                            cycleCount = currentGearObject->getMethodRateCount();
                            if( cycleCountDownType == CtiLMProgramDirectGear::CountDownMethodOptionType )
                            {
                                ULONG estimatedControlTimeInSeconds = period * cycleCount;
                                if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                                {
                                    ULONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                                    if( period != 0 )
                                    {
                                        ULONG tempCycleCount = controlTimeLeft / period;
                                        if( (controlTimeLeft % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                        if( tempCycleCount < cycleCount )
                                        {
                                            cycleCount = tempCycleCount;
                                        }
                                    }
                                    else
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
                                    }
                                }
                            }
                            else if( cycleCountDownType == CtiLMProgramDirectGear::LimitedCountDownMethodOptionType )
                            {//can't really do anything for limited count down on start up
                            }//we have to send the default because it is programmed in the switch

                            multiPilMsg->insert( currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority) );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            if( currentGearObject->getPercentReduction() > 0.0 )
                            {
                                expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                            }
                            else
                            {
                                expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                            }
                        }
                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::MasterCycleMethod )
                {
                    ULONG percent = currentGearObject->getMethodRate();
                    ULONG period = currentGearObject->getMethodPeriod();

                    ULONG offTime = period * (percent / 100.0);
                    ULONG onTime = period - offTime;

                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Master Station Cycling groups, LM Program: " << getPAOName() << endl;
                    }
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                        {
                            offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                        }
                        multiPilMsg->insert( currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMStartPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                        {
                            CtiLMGroupBase* currentLMGroup2 = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                            if( currentGearObject->getPercentReduction() > 0.0 )
                            {
                                expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup2->getKWCapacity();
                            }
                            else
                            {
                                expectedLoadReduced += currentLMGroup2->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                            }
                        }

                        if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                        {
                            setProgramState(CtiLMProgramBase::FullyActiveState);
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::RotationMethod )
                {
                    ULONG sendRate = currentGearObject->getMethodRate();
                    ULONG shedTime = currentGearObject->getMethodPeriod();
                    ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                    }

                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                    }
                    for(ULONG i=0;i<numberOfGroupsToTake;i++)
                    {
                        CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup != NULL )
                        {
                            multiPilMsg->insert( currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority) );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            if( currentGearObject->getPercentReduction() > 0.0 )
                            {
                                expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                            }
                            else
                            {
                                expectedLoadReduced += currentLMGroup->getKWCapacity();
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }

                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::LatchingMethod )
                {
                    ULONG gearStartRawState = currentGearObject->getMethodRateCount();

                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Controlling latch groups, LM Program: " << getPAOName() << endl;
                    }
                    for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                    {
                        CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                            {
                                multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( ((CtiLMGroupPoint*)currentLMGroup)->getStartControlRawState(), defaultLMStartPriority ) );
                            }
                            else
                            {
                                multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg(gearStartRawState, defaultLMStartPriority) );
                            }
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            if( currentGearObject->getPercentReduction() > 0.0 )
                            {
                                expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                            }
                            else
                            {
                                expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                            }
                        }
                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method: " << currentGearObject->getControlMethod() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else if( _lmprogramdirectgears.entries() <= 0 )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " doesn't have any gears in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " doesn't have any groups in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    setReductionTotal(getReductionTotal() + expectedLoadReduced);
    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    manualReduceProgramLoad


---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::manualReduceProgramLoad(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.entries() > 0 && _lmprogramdirectgroups.entries() > 0 )
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.entries() )
        {
            currentGearObject = getCurrentGearObject();

            if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::TimeRefreshMethod )
            {
                ULONG refreshRate = currentGearObject->getMethodRate();
                ULONG shedTime = currentGearObject->getMethodPeriod();
                //ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                //take all groups in a manual control

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling all time refresh groups, LM Program: " << getPAOName() << endl;
                }
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        multiPilMsg->insert( currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup->getKWCapacity();
                        }
                    }
                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::SmartCycleMethod )
            {
                ULONG percent = currentGearObject->getMethodRate();
                ULONG period = currentGearObject->getMethodPeriod();
                ULONG cycleCount = currentGearObject->getMethodRateCount();
                RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
                ULONG maxCycleCount = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Smart Cycling all groups, LM Program: " << getPAOName() << endl;
                }
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was different
                        cycleCount = currentGearObject->getMethodRateCount();
                        if( cycleCountDownType == CtiLMProgramDirectGear::CountDownMethodOptionType )
                        {
                            if( maxCycleCount > 0 )
                            {
                                if( period != 0 )
                                {
                                    ULONG tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                    {
                                        tempCycleCount++;
                                    }

                                    if( tempCycleCount > maxCycleCount )
                                    {
                                        cycleCount = maxCycleCount;
                                    }
                                    else
                                    {
                                        cycleCount = tempCycleCount;
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                RWDBDateTime tempDateTime;
                                RWDBDateTime compareDateTime(tempDateTime.year(),tempDateTime.month(),tempDateTime.dayOfMonth(),0,0,0,0);
                                compareDateTime.addDays(1);
                                ULONG tempCycleCount = cycleCount;
                                if( period != 0 )
                                {
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempCycleCount = (compareDateTime.seconds() - RWDBDateTime().seconds()) / period;
                                        if( ((compareDateTime.seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                    else
                                    {
                                        tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }

                                if( tempCycleCount > 63 )
                                {//Versacom can't support counts higher than 63
                                    tempCycleCount = 63;
                                }
                                cycleCount = tempCycleCount;
                            }
                        }
                        else if( currentGearObject->getMethodOptionType() == CtiLMProgramDirectGear::LimitedCountDownMethodOptionType )
                        {//can't really do anything for limited count down on start up
                        }//we have to send the default because it is programmed in the switch

                        multiPilMsg->insert( currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                        }
                    }
                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::MasterCycleMethod )
            {
                ULONG percent = currentGearObject->getMethodRate();
                ULONG period = currentGearObject->getMethodPeriod();

                ULONG offTime = period * (percent / 100.0);
                ULONG onTime = period - offTime;

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Master Station Cycling groups, LM Program: " << getPAOName() << endl;
                }
                CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                if( currentLMGroup != NULL )
                {
                    if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                    {
                        offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                    }
                    multiPilMsg->insert( currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMStartPriority) );
                    setLastControlSent(RWDBDateTime());
                    setLastGroupControlled(currentLMGroup->getPAOId());
                    currentLMGroup->setLastControlSent(RWDBDateTime());
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                    for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                    {
                        CtiLMGroupBase* currentLMGroup2 = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup2->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup2->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                        }
                    }
                    setProgramState(CtiLMProgramBase::ManualActiveState);
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
            else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::RotationMethod )
            {
                ULONG sendRate = currentGearObject->getMethodRate();
                ULONG shedTime = currentGearObject->getMethodPeriod();
                ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                }

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                }
                for(ULONG i=0;i<numberOfGroupsToTake;i++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        multiPilMsg->insert( currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup->getKWCapacity();
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::LatchingMethod )
            {
                ULONG gearStartRawState = currentGearObject->getMethodRateCount();

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling latch groups, LM Program: " << getPAOName() << endl;
                }
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( ((CtiLMGroupPoint*)currentLMGroup)->getStartControlRawState(), defaultLMStartPriority ) );
                        }
                        else
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg(gearStartRawState, defaultLMStartPriority) );
                        }
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                        }
                    }
                }
                if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else if( _lmprogramdirectgears.entries() <= 0 )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " doesn't have any gears in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " doesn't have any groups in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    setReductionTotal(getReductionTotal() + expectedLoadReduced);
    return expectedLoadReduced;
}

/*-------------------------------------------------------------------------
    findGroupToTake

    Finds the next group to be controlled according to the group selection
    method.
--------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMProgramDirect::findGroupToTake(CtiLMProgramDirectGear* currentGearObject)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiLMGroupBase* returnGroup = NULL;

    if( currentGearObject->getGroupSelectionMethod() == CtiLMProgramDirectGear::LastControlledSelectionMethod )
    {
        BOOL found = FALSE;
        for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( getLastGroupControlled() == currentLMGroup->getPAOId() )
            {
                if( i < (_lmprogramdirectgroups.entries()-1) )
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i+1];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                          getCurrentGearObject()->getControlMethod() == CtiLMProgramDirectGear::RotationMethod ) )
                    {
                        found = TRUE;
                        returnGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i+1];
                        returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                    }
                }
                else if( i == (_lmprogramdirectgroups.entries()-1) )
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[0];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                          getCurrentGearObject()->getControlMethod() == CtiLMProgramDirectGear::RotationMethod ) )
                    {
                        found = TRUE;
                        returnGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[0];
                        returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                    }
                }
                break;
            }
        }

        if( !found )
        {
            for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() &&
                    ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                      getCurrentGearObject()->getControlMethod() == CtiLMProgramDirectGear::RotationMethod ) )
                {
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                    returnGroup = currentLMGroup;
                    break;
                }
            }
        }
    }
    else if( currentGearObject->getGroupSelectionMethod() == CtiLMProgramDirectGear::AlwaysFirstGroupSelectionMethod )
    {
        for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( !currentLMGroup->getDisableFlag() &&
                !currentLMGroup->getControlInhibit() &&
                ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                  getCurrentGearObject()->getControlMethod() == CtiLMProgramDirectGear::RotationMethod ) )
            {
                currentLMGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                returnGroup = currentLMGroup;
                break;
            }
        }
    }
    else if( currentGearObject->getGroupSelectionMethod() == CtiLMProgramDirectGear::LeastControlTimeSelectionMethod )
    {
        // progressive lookup first look at current hours daily then current hours monthly
        // then current hours seasonal but not annually
        for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( !currentLMGroup->getDisableFlag() &&
                !currentLMGroup->getControlInhibit() &&
                currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState )
            {
                if( returnGroup == NULL )
                {
                    returnGroup = currentLMGroup;
                }
                else if( returnGroup->getCurrentHoursDaily() > currentLMGroup->getCurrentHoursDaily() )
                {
                    returnGroup = currentLMGroup;
                }
                else if( returnGroup->getCurrentHoursDaily() == currentLMGroup->getCurrentHoursDaily() &&
                         returnGroup->getCurrentHoursMonthly() > currentLMGroup->getCurrentHoursMonthly() )
                {
                    returnGroup = currentLMGroup;
                }
                else if( returnGroup->getCurrentHoursMonthly() == currentLMGroup->getCurrentHoursMonthly() &&
                         returnGroup->getCurrentHoursSeasonal() > currentLMGroup->getCurrentHoursSeasonal() )
                {
                    returnGroup = currentLMGroup;
                }
            }
        }
    }

    return returnGroup;
}

/*---------------------------------------------------------------------------
    hasGearChanged

    Returns a boolean that represents if the current gear for the program
    has changed because of duration, priority, or trigger offset.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasGearChanged(ULONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL returnBoolean = FALSE;

    //The below block sees if the program needs to shift to a higher gear
    if( _currentgearnumber < (_lmprogramdirectgears.entries()-1) )
    {
        CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

        if( currentGearObject->getChangeCondition() == CtiLMProgramDirectGear::NoneChangeCondition )
        {
            //returnBoolean = FALSE;
        }
        else if( currentGearObject->getChangeCondition() == CtiLMProgramDirectGear::DurationChangeCondition )
        {
            ULONG secondsControlling = secondsFrom1901 - getStartedControlling().seconds();
            if( secondsControlling >= currentGearObject->getChangeDuration() &&
                _currentgearnumber+1 < _lmprogramdirectgears.entries() )
            {
                _currentgearnumber++;
                hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901);
                returnBoolean = TRUE;
            }
        }
        else if( currentGearObject->getChangeCondition() == CtiLMProgramDirectGear::PriorityChangeCondition )
        {
            if( currentPriority >= currentGearObject->getChangePriority() &&
                _currentgearnumber+1 < _lmprogramdirectgears.entries() )
            {
                _currentgearnumber++;
                hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901);
                returnBoolean = TRUE;
            }
        }
        else if( currentGearObject->getChangeCondition() == CtiLMProgramDirectGear::TriggerOffsetChangeCondition )
        {
            if( currentGearObject->getChangeTriggerNumber() > 0 &&
                currentGearObject->getChangeTriggerNumber() <= controlAreaTriggers.entries() )
            {
                CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[currentGearObject->getChangeTriggerNumber()-1];
                if( ( trigger->getPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ||
                      trigger->getProjectedPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ) &&
                    _currentgearnumber+1 < _lmprogramdirectgears.entries() )
                {
                    _currentgearnumber++;
                    hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901);
                    returnBoolean = TRUE;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid current gear change condition: " << currentGearObject->getChangeCondition() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else if( _currentgearnumber == (_lmprogramdirectgears.entries()-1) )
    {
        //Already at highest gear!!!
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    //The above block sees if the program needs to shift to a higher gear
    //The above block sees if the program needs to shift to a higher gear

    //*******************************************************************
    //*******************************************************************

    //The below block sees if the program needs to shift to a lower gear
    //The below block sees if the program needs to shift to a lower gear
    if( !returnBoolean )
    {
        if( _currentgearnumber > 0 &&
            _currentgearnumber <= (_lmprogramdirectgears.entries()-1) )
        {
            CtiLMProgramDirectGear* previousGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];

            if( previousGearObject->getChangeCondition() == CtiLMProgramDirectGear::DurationChangeCondition )
            {
                //doesn't make sense to shift down from a duration shift up
                //returnBoolean = FALSE;
            }
            else if( previousGearObject->getChangeCondition() == CtiLMProgramDirectGear::PriorityChangeCondition )
            {
                if( currentPriority < previousGearObject->getChangePriority() &&
                    _currentgearnumber-1 >= 0 )
                {
                    _currentgearnumber--;
                    hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901);
                    returnBoolean = TRUE;
                }
            }
            else if( previousGearObject->getChangeCondition() == CtiLMProgramDirectGear::TriggerOffsetChangeCondition )
            {
                if( previousGearObject->getChangeTriggerNumber() > 0 &&
                    previousGearObject->getChangeTriggerNumber() <= controlAreaTriggers.entries() )
                {
                    CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[previousGearObject->getChangeTriggerNumber()-1];
                    if( trigger->getPointValue() < (trigger->getThreshold() + previousGearObject->getChangeTriggerOffset()) &&
                        trigger->getProjectedPointValue() < (trigger->getThreshold() + previousGearObject->getChangeTriggerOffset()) &&
                        _currentgearnumber-1 >= 0 )
                    {
                        _currentgearnumber--;
                        hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901);
                        returnBoolean = TRUE;
                    }
                }
            }
            else if( previousGearObject->getChangeCondition() == CtiLMProgramDirectGear::NoneChangeCondition )
            {
                //This will only happen on a manual gear change, how else would you get
                //to a higher gear if the previous change condition was none
                //returnBoolean = FALSE;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Invalid current gear change condition: " << previousGearObject->getChangeCondition() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else if( _currentgearnumber == 0 )
        {
            //Already at lowest gear!!!
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    //The above block sees if the program needs to shift to a lower gear

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    maintainProgramControl

    Maintains control on the program by going through all groups that are
    active and sending refresh pil requests if needed.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::maintainProgramControl(ULONG currentPriority, RWOrdered& controlAreaTriggers, ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL returnBoolean = FALSE;
    ULONG previousGearNumber = _currentgearnumber;
    if( hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901) )
    {
        if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Gear Change, LM Program: " << getPAOName() << ", previous gear number: " << previousGearNumber << ", new gear number: " << _currentgearnumber << endl;
        }
        updateProgramControlForGearChange(previousGearNumber, multiPilMsg, multiDispatchMsg);
    }
    else
    {
        if( refreshStandardProgramControl(secondsFrom1901, multiPilMsg, multiDispatchMsg) )
        {
            returnBoolean = TRUE;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    updateProgramControlForGearChange

    Handles the changing of gears within a running program by sending pil
    requests to change the type of shed or cycle depending on the original
    gear control method and the new gear method.
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::updateProgramControlForGearChange(ULONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    DOUBLE expectedLoadReduced = 0.0;

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();
    CtiLMProgramDirectGear* previousGearObject = NULL;

    if( previousGearNumber < _lmprogramdirectgears.entries() )
    {
        previousGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[previousGearNumber];
    }

    if( currentGearObject != NULL && previousGearObject != NULL && _lmprogramdirectgroups.entries() > 0 )
    {
        if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::TimeRefreshMethod )
        {
            // Normally we would only take the commented out "numberOfGroupsToTake" but when we
            // switch gears to refresh from smart cycle or rotation there is the possibility
            // that all groups need to be shed so that's what we'll do
            ULONG refreshRate = currentGearObject->getMethodRate();
            ULONG shedTime = currentGearObject->getMethodPeriod();
            //ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Controlling all time refresh groups, LM Program: " << getPAOName() << endl;
            }
            for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    multiPilMsg->insert( currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority) );
                    setLastControlSent(RWDBDateTime());
                    setLastGroupControlled(currentLMGroup->getPAOId());
                    currentLMGroup->setLastControlSent(RWDBDateTime());
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                    if( currentGearObject->getPercentReduction() > 0.0 )
                    {
                        expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                    }
                    else
                    {
                        expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::SmartCycleMethod )
        {
            ULONG percent = currentGearObject->getMethodRate();
            ULONG period = currentGearObject->getMethodPeriod();
            ULONG cycleCount = currentGearObject->getMethodRateCount();
            RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
            ULONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Smart Cycling all groups, LM Program: " << getPAOName() << endl;
            }
            for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    cycleCount = currentGearObject->getMethodRateCount();
                    if( cycleCountDownType == CtiLMProgramDirectGear::CountDownMethodOptionType )
                    {
                        ULONG estimatedControlTimeInSeconds = period * cycleCount;
                        if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                        {
                            ULONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                            if( period != 0 )
                            {
                                ULONG tempCycleCount = controlTimeLeft / period;
                                if( (controlTimeLeft % period) > 0 )
                                {
                                    tempCycleCount++;
                                }
                                if( tempCycleCount < cycleCount )
                                {
                                    cycleCount = tempCycleCount;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                    else if( cycleCountDownType == CtiLMProgramDirectGear::LimitedCountDownMethodOptionType )
                    {//can't really do anything for limited count down on start up
                    }//we have to send the default because it is programmed in the switch
    
                    multiPilMsg->insert( currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority) );
                    setLastControlSent(RWDBDateTime());
                    setLastGroupControlled(currentLMGroup->getPAOId());
                    currentLMGroup->setLastControlSent(RWDBDateTime());
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                    if( currentGearObject->getPercentReduction() > 0.0 )
                    {
                        expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                    }
                    else
                    {
                        expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::MasterCycleMethod )
        {
            ULONG percent = currentGearObject->getMethodRate();
            ULONG period = currentGearObject->getMethodPeriod();

            ULONG offTime = period * (percent / 100.0);
            ULONG onTime = period - offTime;

            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Master Station Cycling groups, LM Program: " << getPAOName() << endl;
            }
            CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
            if( currentLMGroup != NULL )
            {
                if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                {
                    offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                }
                multiPilMsg->insert( currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMStartPriority) );
                setLastControlSent(RWDBDateTime());
                setLastGroupControlled(currentLMGroup->getPAOId());
                currentLMGroup->setLastControlSent(RWDBDateTime());
                currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup2 = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( currentGearObject->getPercentReduction() > 0.0 )
                    {
                        expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup2->getKWCapacity();
                    }
                    else
                    {
                        expectedLoadReduced += currentLMGroup2->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                    }
                }
    
                if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::RotationMethod )
        {
            if( previousGearObject->getControlMethod() == CtiLMProgramDirectGear::TimeRefreshMethod )
            {
                ULONG sendRate = currentGearObject->getMethodRate();
                ULONG shedTime = currentGearObject->getMethodPeriod();

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling all rotation groups, LM Program: " << getPAOName() << endl;
                }
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        multiPilMsg->insert( currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                        }
                    }
                }
            }
            else if( previousGearObject->getControlMethod() == CtiLMProgramDirectGear::SmartCycleMethod )
            {
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    RWCString controlString = "control cycle terminate";
                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Sending cycle terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                    }
                    multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                }

                ULONG sendRate = currentGearObject->getMethodRate();
                ULONG shedTime = currentGearObject->getMethodPeriod();
                ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                }

                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                }
                for(ULONG j=0;j<numberOfGroupsToTake;j++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        multiPilMsg->insert( currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        if( currentGearObject->getPercentReduction() > 0.0 )
                        {
                            expectedLoadReduced += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                        }
                        else
                        {
                            expectedLoadReduced += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                        }

                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::LatchingMethod )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Gear Control Method: " << getPAOName() << " Gear#: " << currentGearObject->getGearNumber() << " control method can't support gear changes.  In: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else if( previousGearObject == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " previousGearObject == NULL in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    else if( currentGearObject == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " currentGearObject == NULL in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " doesn't have any groups in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    refreshStandardProgramControl

    In Time Refresh gears this sends out additional shed messages at a given
    refresh rate. In Smart Cycle gears this sends out additional shed
    messages at a given refresh rate. In Rotation gears this finds the next
    groups that should be controlled according to the group selection method
    and send sheds to those groups, it also updates the group control state
    of the groups that have been rotated through and are now inactive.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::refreshStandardProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL returnBoolean = FALSE;
    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL && _lmprogramdirectgroups.entries() > 0 )
    {
        if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::TimeRefreshMethod )
        {
            ULONG refreshRate = currentGearObject->getMethodRate();
            ULONG shedTime = currentGearObject->getMethodPeriod();

            for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                RWDBDateTime previousLastControlSent = currentLMGroup->getLastControlSent();
                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                    !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    if( secondsFrom1901 >= currentLMGroup->getLastControlSent().seconds()+refreshRate )
                    {
                        multiPilMsg->insert( currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMRefreshPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        returnBoolean = TRUE;
                    }
                }
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::SmartCycleMethod )
        {
            ULONG percent = currentGearObject->getMethodRate();
            ULONG period = currentGearObject->getMethodPeriod();
            ULONG cycleCount = currentGearObject->getMethodRateCount();
            ULONG cycleRefreshRate = currentGearObject->getCycleRefreshRate();
            RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
            ULONG maxCycleCount = currentGearObject->getMethodOptionMax();

            ULONG periodEndInSecondsFrom1901 = 0;
            if( cycleRefreshRate == 0 )
            {
                periodEndInSecondsFrom1901 = getLastControlSent().seconds()+(period * cycleCount)+1;
            }
            else
            {
                periodEndInSecondsFrom1901 = getLastControlSent().seconds()+cycleRefreshRate;
            }

            if( period != 0 )
            {
                if( secondsFrom1901 >= periodEndInSecondsFrom1901 )
                {
                    for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                    {
                        CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            //reset the default for each group if the previous groups was lower
                            cycleCount = currentGearObject->getMethodRateCount();
                            if( cycleCountDownType == CtiLMProgramDirectGear::CountDownMethodOptionType )
                            {
                                if( getManualControlReceivedFlag() )
                                {
                                    if( maxCycleCount > 0 )
                                    {
                                        ULONG tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }

                                        if( tempCycleCount > maxCycleCount )
                                        {
                                            cycleCount = maxCycleCount;
                                        }
                                        else
                                        {
                                            cycleCount = tempCycleCount;
                                        }
                                    }
                                    else
                                    {
                                        RWDBDateTime tempDateTime;
                                        RWDBDateTime compareDateTime(tempDateTime.year(),tempDateTime.month(),tempDateTime.dayOfMonth(),0,0,0,0);
                                        compareDateTime.addDays(1);
                                        ULONG tempCycleCount = cycleCount;
                                        if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                        {
                                            tempCycleCount = (compareDateTime.seconds() - RWDBDateTime().seconds()) / period;
                                            if( ((compareDateTime.seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                            {
                                                tempCycleCount++;
                                            }
                                        }
                                        else
                                        {
                                            tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                            if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                            {
                                                tempCycleCount++;
                                            }
                                        }

                                        if( tempCycleCount > 63 )
                                        {//Versacom can't support counts higher than 63
                                            tempCycleCount = 63;
                                        }
                                        cycleCount = tempCycleCount;
                                    }
                                }

                                ULONG estimatedControlTimeInSeconds = period * cycleCount;
                                if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                                {
                                    ULONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                                    ULONG tempCycleCount = controlTimeLeft / period;
                                    if( (controlTimeLeft % period) > 0 )
                                    {
                                        tempCycleCount++;
                                    }
                                    if( tempCycleCount < cycleCount )
                                    {
                                        cycleCount = tempCycleCount;
                                    }
                                }
                            }
                            else if( cycleCountDownType == CtiLMProgramDirectGear::LimitedCountDownMethodOptionType )
                            {
                                ULONG estimatedControlTimeInSeconds = period * cycleCount;
                                if( getManualControlReceivedFlag() )
                                {
                                    unsigned long secondsSince1901 = RWDBDateTime().seconds();
                                    if( (secondsSince1901 + estimatedControlTimeInSeconds) > getDirectStopTime().seconds())
                                    {
                                        cycleCount = 0;
                                    }
                                }

                                if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                                {
                                    cycleCount = 0;
                                }

                            }

                            if( cycleCount > 0 )
                            {
                                multiPilMsg->insert( currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMRefreshPriority) );
                                setLastControlSent(RWDBDateTime());
                                setLastGroupControlled(currentLMGroup->getPAOId());
                                currentLMGroup->setLastControlSent(RWDBDateTime());
                                currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                                returnBoolean = TRUE;
                            }
                            else
                            {
                                if( !doesGroupHaveAmpleControlTime(currentLMGroup,0) )
                                {//we need to restore the group in the way set in the gear because it went over max control time
                                    RWCString tempMethodStopType = currentGearObject->getMethodStopType();
                                    if( tempMethodStopType == CtiLMProgramDirectGear::RestoreStopType )
                                    {
                                        RWCString controlString = "control restore";
                                        if( _LM_DEBUG )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                                        }
                                        multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                                        setLastControlSent(RWDBDateTime());
                                        currentLMGroup->setLastControlSent(RWDBDateTime());
                                    }
                                    else if( tempMethodStopType == CtiLMProgramDirectGear::StopCycleStopType ||
                                             tempMethodStopType == CtiLMProgramDirectGear::TimeInStopType ||
                                             tempMethodStopType == "Time-In" )
                                    {//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                                        RWCString controlString = "control cycle terminate";
                                        if( _LM_DEBUG )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - Sending terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                                        }
                                        multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                                        setLastControlSent(RWDBDateTime());
                                        currentLMGroup->setLastControlSent(RWDBDateTime());
                                    }
                                    else
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                                    }

                                    DOUBLE restoredGroupLoad = 0.0;
                                    if( currentGearObject->getPercentReduction() > 0.0 )
                                    {
                                        restoredGroupLoad += (currentGearObject->getPercentReduction() / 100.0) * currentLMGroup->getKWCapacity();
                                    }
                                    else
                                    {
                                        restoredGroupLoad += currentLMGroup->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                                    }
                                    setReductionTotal(getReductionTotal()-restoredGroupLoad);
                                    returnBoolean = TRUE;
                                }
                            }
                        }
                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::MasterCycleMethod )
        {
            ULONG percent = currentGearObject->getMethodRate();
            ULONG period = currentGearObject->getMethodPeriod();

            ULONG offTime = period * (percent / 100.0);
            ULONG onTime = period - offTime;
            ULONG sendRate = onTime;
            if( _lmprogramdirectgroups.entries()-1 > 0 )
            {
                ULONG sendRate = onTime / (_lmprogramdirectgroups.entries()-1);
            }

            ULONG sendRateEndFrom1901 = getLastControlSent().seconds()+sendRate;

            for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    if( currentLMGroup->doesMasterCycleNeedToBeUpdated(secondsFrom1901, (currentLMGroup->getLastControlSent().seconds()+offTime), offTime) )
                    {
                        //if it is a emetcon switch 450 (7.5 min) is correct
                        //ripple switches have a predetermined shed time
                        multiPilMsg->insert( currentLMGroup->createMasterCycleRequestMsg(450, period, defaultLMRefreshPriority) );
                        returnBoolean = TRUE;
                    }
                    else if( currentLMGroup->getLastControlSent().seconds()+offTime >= secondsFrom1901 )
                    {//groups not currently shed need to be set back to inactive state
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        if( currentGearObject->getMethodStopType() == CtiLMProgramDirectGear::RestoreStopType )
                        {
                            RWCString controlString = "control restore";
                            if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                            }
                            multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                        }
                        returnBoolean = TRUE;
                    }
                }
            }

            if( secondsFrom1901 >= sendRateEndFrom1901 )
            {
                CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                if( currentLMGroup != NULL )
                {
                    if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                    {
                        offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                    }
                    multiPilMsg->insert( currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMRefreshPriority) );
                    setLastControlSent(RWDBDateTime());
                    setLastGroupControlled(currentLMGroup->getPAOId());
                    currentLMGroup->setLastControlSent(RWDBDateTime());
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
        
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                    returnBoolean = TRUE;
                }
                else
                {
                    setLastControlSent(RWDBDateTime());
                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }*/
                }
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::RotationMethod )
        {
            ULONG sendRate = currentGearObject->getMethodRate();
            ULONG shedTime = currentGearObject->getMethodPeriod();
            ULONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

            if( numberOfGroupsToTake == 0 )
            {
                numberOfGroupsToTake = _lmprogramdirectgroups.entries();
            }

            if( secondsFrom1901 >= getLastControlSent().second()+sendRate )
            {
                // First we need to update the state of the currently active rotating groups
                for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                    {
                        if( secondsFrom1901 >= currentLMGroup->getLastControlSent().seconds()+shedTime )
                        {
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        }
                    }
                }

                // Now we need to take the next groups for the rotation method
                for(ULONG j=0;j<numberOfGroupsToTake;j++)
                {
                    CtiLMGroupBase* nextLMGroupToTake = findGroupToTake(currentGearObject);
                    if( nextLMGroupToTake != NULL )
                    {
                        multiPilMsg->insert( nextLMGroupToTake->createRotationRequestMsg(sendRate, shedTime, defaultLMRefreshPriority) );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(nextLMGroupToTake->getPAOId());
                        nextLMGroupToTake->setLastControlSent(RWDBDateTime());
                        nextLMGroupToTake->setGroupControlState(CtiLMGroupBase::ActiveState);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                returnBoolean = TRUE;
            }

            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( currentGearObject->getControlMethod() == CtiLMProgramDirectGear::LatchingMethod )
        {
            /*CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Gear Control Method: " << getPAOName() << " Gear#: " << currentGearObject->getGearNumber() << " control method isn't supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;*/
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else if( currentGearObject == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " currentGearObject == NULL in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    else
    {
        /*CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Program: " << getPAOName() << " doesn't have any groups in: " << __FILE__ << " at:" << __LINE__ << endl;*/
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by stopping all groups that are active.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL )
    {
        RWCString tempControlMethod = currentGearObject->getControlMethod();
        RWCString tempMethodStopType = currentGearObject->getMethodStopType();
        for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
            {
                if( tempControlMethod == CtiLMProgramDirectGear::SmartCycleMethod )
                {
                    if( tempMethodStopType == CtiLMProgramDirectGear::RestoreStopType )
                    {
                        RWCString controlString = "control restore";
                        if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                        }
                        multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                    }
                    else if( tempMethodStopType == CtiLMProgramDirectGear::StopCycleStopType ||
                             tempMethodStopType == CtiLMProgramDirectGear::TimeInStopType ||
                             tempMethodStopType == "Time-In" )
                    {//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                        RWCString controlString = "control cycle terminate";
                        if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Sending terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                        }
                        multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else if( tempControlMethod == CtiLMProgramDirectGear::TimeRefreshMethod ||
                         tempControlMethod == CtiLMProgramDirectGear::MasterCycleMethod ||
                         tempControlMethod == CtiLMProgramDirectGear::RotationMethod )
                {
                    if( tempMethodStopType == CtiLMProgramDirectGear::RestoreStopType )
                    {
                        RWCString controlString = "control restore";
                        if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << endl;
                        }
                        multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString));
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                    }
                    else if( tempMethodStopType == CtiLMProgramDirectGear::TimeInStopType || tempMethodStopType == "Time-In" )
                    {//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                        //CtiLockGuard<CtiLogger> logger_guard(dout);
                        //dout << RWTime() << " - Stopping control on LM Group: " << currentLMGroup->getPAOName() << " with Stop Type of (Time In) in: " << __FILE__ << " at:" << __LINE__ << endl;
                        //I don't know if I should do anything unique here yet?
                        //multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), "control cycle terminate"));
                        //setLastControlSent(RWDBDateTime());
                        //currentLMGroup->setLastControlSent(RWDBDateTime());
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else if( tempControlMethod == CtiLMProgramDirectGear::LatchingMethod )
                {
                    if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                    {
                        multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( (((CtiLMGroupPoint*)currentLMGroup)->getStartControlRawState()?0:1), defaultLMRefreshPriority ) );
                    }
                    else
                    {
                        multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( (currentGearObject->getMethodRateCount()?0:1), defaultLMRefreshPriority ) );
                    }
                    setLastControlSent(RWDBDateTime());
                    currentLMGroup->setLastControlSent(RWDBDateTime());
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Invalid current gear control method: " << tempControlMethod << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
            }
        }
        setReductionTotal(0.0);
        setProgramState(CtiLMProgramBase::InactiveState);
        setCurrentGearNumber(0);
        //setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setDirectStopTime(RWDBDateTime(/*1990,1,1,0,0,0,0*/));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the direct program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( getProgramState() == CtiLMProgramBase::ScheduledState )
    {
        if( secondsFrom1901 >= getDirectStartTime().seconds() )
        {
            returnBoolean = TRUE;
            {
                RWCString text = RWCString("Manual Start, LM Program: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            manualReduceProgramLoad(multiPilMsg,multiDispatchMsg);
            setProgramState(CtiLMProgramBase::ManualActiveState);
        }
        if( secondsFrom1901 >= getDirectStopTime().seconds() )
        {
            returnBoolean = TRUE;
            {
                RWCString text = RWCString("Manual Stop, LM Program: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            //setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime(/*1990,1,1,0,0,0,0*/));
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ManualActiveState )
    {
        if( secondsFrom1901 >= getDirectStopTime().seconds() )
        {
            returnBoolean = TRUE;
            {
                RWCString text = RWCString("Manually Stop, LM Program: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            //setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime(/*1990,1,1,0,0,0,0*/));
        }
        else
        {
            if( refreshStandardProgramControl(secondsFrom1901, multiPilMsg, multiDispatchMsg) )
            {
                returnBoolean = TRUE;
            }
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ActiveState ||
             getProgramState() == CtiLMProgramBase::FullyActiveState )
    {
        if( secondsFrom1901 >= getDirectStopTime().seconds() )
        {
            returnBoolean = TRUE;
            {
                RWCString text = RWCString("Manual Stop, LM Program: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            //setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime(/*1990,1,1,0,0,0,0*/));
        }
    }
    else if( getProgramState() == CtiLMProgramBase::StoppingState )
    {
        returnBoolean = TRUE;
        {
            RWCString text = RWCString("Manual Stop, LM Program: ");
            text += getPAOName();
            RWCString additional = RWCString("");
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(2);

            multiDispatchMsg->insert(signal);
            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
        }
        stopProgramControl(multiPilMsg,multiDispatchMsg);
        setManualControlReceivedFlag(FALSE);

        setReductionTotal(0.0);
        setProgramState(CtiLMProgramBase::InactiveState);
        //setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setDirectStopTime(RWDBDateTime(/*1990,1,1,0,0,0,0*/));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid manual control state: " << getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        setManualControlReceivedFlag(FALSE);
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    hasControlHoursAvailable()

    Returns boolean if groups in this program are below the max hours
    daily/monthly/seasonal/annually.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasControlHoursAvailable() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL returnBoolean = FALSE;

    if( _lmprogramdirectgroups.entries() > 0 )
    {
        for(ULONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

            // As soon as we find a group with hours available we know that the entire program
            // has at least some hours available
            if( !doesGroupHaveAmpleControlTime(currentLMGroup,0) )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*-------------------------------------------------------------------------
    doesGroupHaveAmpleControlTime

    .
--------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::doesGroupHaveAmpleControlTime(CtiLMGroupBase* currentLMGroup, ULONG estimatedControlTimeInSeconds) const
{
    return ( getMaxHoursDaily() > 0 && (currentLMGroup->getCurrentHoursDaily() + estimatedControlTimeInSeconds) > getMaxHoursDaily() ||
             getMaxHoursMonthly() > 0 && (currentLMGroup->getCurrentHoursMonthly() + estimatedControlTimeInSeconds) > getMaxHoursMonthly() ||
             getMaxHoursSeasonal() > 0 && (currentLMGroup->getCurrentHoursSeasonal() + estimatedControlTimeInSeconds) > getMaxHoursSeasonal() ||
             getMaxHoursAnnually() > 0 && (currentLMGroup->getCurrentHoursAnnually() + estimatedControlTimeInSeconds) > getMaxHoursAnnually() );
}

/*-------------------------------------------------------------------------
    calculateGroupControlTimeLeft

    .
--------------------------------------------------------------------------*/
ULONG CtiLMProgramDirect::calculateGroupControlTimeLeft(CtiLMGroupBase* currentLMGroup, ULONG estimatedControlTimeInSeconds) const
{
    ULONG returnTimeLeft = estimatedControlTimeInSeconds;

    if( getMaxHoursDaily() > 0 && (currentLMGroup->getCurrentHoursDaily() + estimatedControlTimeInSeconds) > getMaxHoursDaily() )
    {
        ULONG tempTimeLeft = getMaxHoursDaily() - currentLMGroup->getCurrentHoursDaily();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursMonthly() > 0 && (currentLMGroup->getCurrentHoursMonthly() + estimatedControlTimeInSeconds) > getMaxHoursMonthly() )
    {
        ULONG tempTimeLeft = getMaxHoursMonthly() - currentLMGroup->getCurrentHoursMonthly();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursSeasonal() > 0 && (currentLMGroup->getCurrentHoursSeasonal() + estimatedControlTimeInSeconds) > getMaxHoursSeasonal() )
    {
        ULONG tempTimeLeft = getMaxHoursSeasonal() - currentLMGroup->getCurrentHoursSeasonal();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursAnnually() > 0 && (currentLMGroup->getCurrentHoursAnnually() + estimatedControlTimeInSeconds) > getMaxHoursAnnually() )
    {
        ULONG tempTimeLeft = getMaxHoursAnnually() - currentLMGroup->getCurrentHoursAnnually();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    return returnTimeLeft;
}

/*-------------------------------------------------------------------------
    getCurrentGearObject

    Returns a pointer to the gear at the current gear number.  Returns NULL
    if there is an invalid current gear number.
--------------------------------------------------------------------------*/
CtiLMProgramDirectGear* CtiLMProgramDirect::getCurrentGearObject()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiLMProgramDirectGear* returnGearObject = NULL;

    if( _currentgearnumber < _lmprogramdirectgears.entries() )
    {
        returnGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber];
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }

    return returnGearObject;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramDirect::restoreGuts(RWvistream& istrm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMProgramBase::restoreGuts( istrm );

    RWTime tempTime1;
    RWTime tempTime2;
    istrm >> _currentgearnumber
    >> _lastgroupcontrolled
    >> tempTime1
    >> tempTime2
    >> _lmprogramdirectgears
    >> _lmprogramdirectgroups;


    _directstarttime = RWDBDateTime(tempTime1);
    _directstoptime = RWDBDateTime(tempTime2);
    if( _currentgearnumber > 0 )
    {
        _currentgearnumber = _currentgearnumber - 1;
    }
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::saveGuts(RWvostream& ostrm ) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMProgramBase::saveGuts( ostrm );

    ostrm << (_currentgearnumber+1)
    << _lastgroupcontrolled
    << _directstarttime.rwtime()
    << _directstoptime.rwtime()
    << _lmprogramdirectgears
    << _lmprogramdirectgroups;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::operator=(const CtiLMProgramDirect& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        CtiLMProgramBase::operator=(right);
        _currentgearnumber = right._currentgearnumber;
        _lastgroupcontrolled = right._lastgroupcontrolled;
        _directstarttime = right._directstarttime;
        _directstoptime = right._directstoptime;

        _lmprogramdirectgears.clearAndDestroy();
        for(UINT i=0;i<right._lmprogramdirectgears.entries();i++)
        {
            _lmprogramdirectgears.insert(((CtiLMProgramDirectGear*)right._lmprogramdirectgears[i])->replicate());
        }

        _lmprogramdirectgroups.clearAndDestroy();
        for(UINT j=0;j<right._lmprogramdirectgroups.entries();j++)
        {
            _lmprogramdirectgroups.insert(((CtiLMGroupBase*)right._lmprogramdirectgroups[j])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramDirect::operator==(const CtiLMProgramDirect& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return CtiLMProgramBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramDirect::operator!=(const CtiLMProgramDirect& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return CtiLMProgramBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramBase* CtiLMProgramDirect::replicate() const
{
    return(new CtiLMProgramDirect(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMProgramBase::restore(rdr);

    /*setCurrentGearNumber(0);
    setLastGroupControlled(0);
    setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
    setDirectStopTime(RWDBDateTime(1990,1,1,0,0,0,0));*/
}

/*---------------------------------------------------------------------------
    restoreDirectSpecificDatabaseEntries

    Restores the database entries for a direct program that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::restoreDirectSpecificDatabaseEntries(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    RWDBNullIndicator isNull;
    RWDBDateTime dynamicTimeStamp;
    _insertDynamicDataFlag = FALSE;

    rdr["currentgearnumber"] >> isNull;
    if( !isNull )
    {
        rdr["currentgearnumber"] >> _currentgearnumber;
        rdr["lastgroupcontrolled"] >> _lastgroupcontrolled;
        rdr["starttime"] >> _directstarttime;
        rdr["stoptime"] >> _directstoptime;
        rdr["timestamp"] >> dynamicTimeStamp;

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        setCurrentGearNumber(0);
        setLastGroupControlled(0);
        setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setDirectStopTime(RWDBDateTime(1990,1,1,0,0,0,0));

        _insertDynamicDataFlag = TRUE;
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this direct program.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::dumpDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    RWDBDateTime currentDateTime = RWDBDateTime();

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBTable dynamicLMProgramDirectTable = getDatabase().table( "dynamiclmprogramdirect" );
            if( !_insertDynamicDataFlag )
            {
                RWDBUpdater updater = dynamicLMProgramDirectTable.updater();

                updater << dynamicLMProgramDirectTable["currentgearnumber"].assign( getCurrentGearNumber() )
                << dynamicLMProgramDirectTable["lastgroupcontrolled"].assign(getLastGroupControlled())
                << dynamicLMProgramDirectTable["starttime"].assign(getDirectStartTime())
                << dynamicLMProgramDirectTable["stoptime"].assign(getDirectStopTime())
                << dynamicLMProgramDirectTable["timestamp"].assign((RWDBDateTime)currentDateTime);

                updater.where(dynamicLMProgramDirectTable["deviceid"]==getPAOId());//will be paobjectid

                updater.execute( conn );
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Inserted program direct into DynamicLMProgramDirect: " << getPAOName() << endl;
                }

                RWDBInserter inserter = dynamicLMProgramDirectTable.inserter();

                inserter << getPAOId()
                << getCurrentGearNumber()
                << getLastGroupControlled()
                << getDirectStartTime()
                << getDirectStopTime()
                << currentDateTime;

                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }*/

                inserter.execute( conn );

                _insertDynamicDataFlag = FALSE;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid database connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

// Static Members
int CtiLMProgramDirect::defaultLMStartPriority = 13;
int CtiLMProgramDirect::defaultLMRefreshPriority = 11;

