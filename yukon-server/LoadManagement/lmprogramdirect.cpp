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
#include "lmprogramthermostatgear.h"
#include "lmprogramcontrolwindow.h"

extern ULONG _LM_DEBUG;

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

    _lmprogramdirectgears.clearAndDestroy();
    _lmprogramdirectgroups.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getCurrentGearNumber

    Returns the number of the current direct program gear starts at 0 and
    goes upto _lmprogramdirectgears.entries()-1
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getCurrentGearNumber() const
{

    return _currentgearnumber;
}

/*---------------------------------------------------------------------------
    getLastGroupControlled

    Returns the device id of the last lm group that was controlled in the
    program
---------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getLastGroupControlled() const
{

    return _lastgroupcontrolled;
}

/*---------------------------------------------------------------------------
    getDailyOps

    Returns the number of times this program has operated today
 ---------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getDailyOps() 
{
    // If we haven't written out dynamic data today
    RWDate today;
    if(today > _dynamictimestamp.rwdate())
    {
	resetDailyOps();
    }
    
    return _dailyops; //TODO: check if this is the value for today!
}

/*---------------------------------------------------------------------------
    getDirectStartTime

    Returns the direct start time for a manual control of the direct program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getDirectStartTime() const
{

    return _directstarttime;
}

/*---------------------------------------------------------------------------
    getDirectStopTime

    Returns the direct stop time for a manual control of the direct program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getDirectStopTime() const
{

    return _directstoptime;
}

/*---------------------------------------------------------------------------
    getLMProgramDirectGears

    Returns the pointer to a list of gears for this direct program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramDirect::getLMProgramDirectGears()
{

    return _lmprogramdirectgears;
}

/*---------------------------------------------------------------------------
    getLMProgramDirectGroups

    Returns the pointer to a list of groups for this direct program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramDirect::getLMProgramDirectGroups()
{

    return _lmprogramdirectgroups;
}

/*---------------------------------------------------------------------------
    setCurrentGearNumber

    Sets the number of the current direct program gear starts at 0 and
    goes upto _lmprogramdirectgears.entries()-1
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setCurrentGearNumber(LONG currentgear)
{

    _currentgearnumber = currentgear;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastGroupControlled

    Sets the device id of the last lm group that was controlled in the
    program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setLastGroupControlled(LONG lastcontrolled)
{

    _lastgroupcontrolled = lastcontrolled;

    return *this;
}

/*---------------------------------------------------------------------------
  incrementDailyOps

  Increments this programs daily operations count by 1
  ---------------------------------------------------------------------------*/  
CtiLMProgramDirect& CtiLMProgramDirect::incrementDailyOps()
{
    _dailyops++;
    return *this;
}

/*---------------------------------------------------------------------------
  resetDailyOps

  Resets this programs daily operations count to 0
  ---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::resetDailyOps()
{
    _dailyops = 0;
    return *this;
}

/*---------------------------------------------------------------------------
    setDirectStartTime

    Sets the direct start time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStartTime(const RWDBDateTime& start)
{

    _directstarttime = start;

    return *this;
}

/*---------------------------------------------------------------------------
    setDirectStopTime

    Sets the direct stop time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStopTime(const RWDBDateTime& stop)
{

    _directstoptime = stop;

    return *this;
}


/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the direct program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded)
{


    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.entries() > 0 && _lmprogramdirectgroups.entries() > 0 )
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.entries() )
        {
            LONG previousGearNumber = _currentgearnumber;
            BOOL gearChangeBoolean = hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded);
            currentGearObject = getCurrentGearObject();

            if( gearChangeBoolean &&
                getProgramState() != CtiLMProgramBase::InactiveState )
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Gear Change, LM Program: " << getPAOName() << ", previous gear number: " << previousGearNumber << ", new gear number: " << _currentgearnumber << endl;
                }
                expectedLoadReduced = updateProgramControlForGearChange(previousGearNumber, multiPilMsg, multiDispatchMsg);
            }
            else
            {
                LONG savedState = getProgramState();

                if( getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    getProgramState() != CtiLMProgramBase::ActiveState )
                {
                    setProgramState(CtiLMProgramBase::ActiveState);
                    setStartedControlling(RWDBDateTime());
		    incrementDailyOps();
                    setDirectStartTime(RWDBDateTime());
                    setDirectStopTime(RWDBDateTime(1990,1,1,0,0,0,0));
                    {
                        RWCString text = RWCString("Automatic Start, LM Program: ");
                        text += getPAOName();
                        RWCString additional = RWCString("");
                        CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                        signal->setSOE(2);

                        multiDispatchMsg->insert(signal);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << text << ", " << additional << endl;
                        }
                    }
                }

                if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
                {
                    LONG refreshRate = currentGearObject->getMethodRate();
                    LONG shedTime = currentGearObject->getMethodPeriod();
                    LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                    RWCString refreshCountDownType = currentGearObject->getMethodOptionType();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                    }

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        if( savedState == CtiLMProgramBase::ActiveState )
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

                    for(LONG i=0;i<numberOfGroupsToTake;i++)
                    {
                        CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup != NULL )
                        {
                            if( !refreshCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                            {
                                LONG estimatedControlTimeInSeconds = shedTime;
                                if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                                {
                                    shedTime = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                                }
                            }

                            CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
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
                        for(LONG j=0;j<_lmprogramdirectgroups.entries();j++)
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
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                         !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
                {
                    LONG percent = currentGearObject->getMethodRate();
                    LONG period = currentGearObject->getMethodPeriod();
                    LONG cycleCount = currentGearObject->getMethodRateCount();
                    RWCString cycleCountDownType = currentGearObject->getMethodOptionType();

                    if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) )
                    {
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Smart Cycling all groups, LM Program: " << getPAOName() << endl;
                        }
                    }
                    else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
                    {
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - True Cycling all groups, LM Program: " << getPAOName() << endl;
                        }
                    }

                    for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                    {
                        CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            //reset the default for each group if the previous groups was lower
                            cycleCount = currentGearObject->getMethodRateCount();
                            if( cycleCount == 0 )
                            {
                                cycleCount = 8;//seems like a reasonable default
                            }

                            if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                            {
                                LONG estimatedControlTimeInSeconds = (period * (((double)percent)/100.0)) * cycleCount;
                                if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                                {
                                    LONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                                    if( period != 0 && percent != 0 )
                                    {
                                        LONG tempCycleCount = ((LONG)(controlTimeLeft*(100.0/((double)percent)))) / period;
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
                            else if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::LimitedCountDownMethodOptionType,RWCString::ignoreCase) )
                            {//can't really do anything for limited count down on start up
                            }//we have to send the default because it is programmed in the switch

                            CtiRequestMsg* requestMsg = NULL;
                            if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) &&
                                currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                            {
                                requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                            }
                            else
                            {
                                requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                                if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << ", Smart Cycling instead in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
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
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) )
                {
                    LONG percent = currentGearObject->getMethodRate();
                    LONG period = currentGearObject->getMethodPeriod();

                    LONG offTime = period * (percent / 100.0);
                    LONG onTime = period - offTime;

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Master Station Cycling groups, LM Program: " << getPAOName() << endl;
                    }

                    int numberOfGroupsToTake = 1;
                    if( _lmprogramdirectgroups.entries() >= 8 )
                    {//take 2 at a time
                        numberOfGroupsToTake = 2;
                        if( (_lmprogramdirectgroups.entries() % 2) != 0 )//is there an odd number of groups
                        {//is this the last group in the group list, because if it is we only take 1 not 2
                            CtiLMGroupBase* testLMGroup = findGroupToTake(currentGearObject);
                            if( testLMGroup == _lmprogramdirectgroups[_lmprogramdirectgroups.entries()-1] )
                            {
                                numberOfGroupsToTake = 1;
                            }
                        }
                    }

                    for(int y=0;y<numberOfGroupsToTake;y++)
                    {
                        CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup != NULL )
                        {
                            if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                            {
                                offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                            }
                            CtiRequestMsg* requestMsg = currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMStartPriority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
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
                }
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
                {
                    LONG sendRate = currentGearObject->getMethodRate();
                    LONG shedTime = currentGearObject->getMethodPeriod();
                    LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                    }

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                    }
                    for(LONG i=0;i<numberOfGroupsToTake;i++)
                    {
                        CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup != NULL )
                        {
                            CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
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
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) )
                {
                    LONG gearStartRawState = currentGearObject->getMethodRateCount();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Controlling latch groups, LM Program: " << getPAOName() << endl;
                    }
                    for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
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
                            currentLMGroup->setControlStartTime(RWDBDateTime());
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
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
                {
                    CtiLMProgramThermoStatGear* thermostatGearObject = (CtiLMProgramThermoStatGear*)currentGearObject;
                    RWCString settings = thermostatGearObject->getSettings();
                    LONG minValue = thermostatGearObject->getMinValue();
                    LONG maxValue = thermostatGearObject->getMaxValue();
                    LONG valueB = thermostatGearObject->getValueB();
                    LONG valueD = thermostatGearObject->getValueD();
                    LONG valueF = thermostatGearObject->getValueF();
                    LONG random = thermostatGearObject->getRandom();
                    LONG valueTA = thermostatGearObject->getValueTa();
                    LONG valueTB = thermostatGearObject->getValueTb();
                    LONG valueTC = thermostatGearObject->getValueTc();
                    LONG valueTD = thermostatGearObject->getValueTd();
                    LONG valueTE = thermostatGearObject->getValueTe();
                    LONG valueTF = thermostatGearObject->getValueTf();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Thermostat Set Point command all groups, LM Program: " << getPAOName() << endl;
                    }
                    for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                    {
                        CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                            {
                                CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(settings, minValue, maxValue, valueB, valueD, valueF, random, valueTA, valueTB, valueTC, valueTD, valueTE, valueTF, defaultLMStartPriority);
                                currentLMGroup->setLastControlString(requestMsg->CommandString());
                                multiPilMsg->insert( requestMsg );
                                setLastControlSent(RWDBDateTime());
                                setLastGroupControlled(currentLMGroup->getPAOId());
                                currentLMGroup->setLastControlSent(RWDBDateTime());
                                currentLMGroup->setControlStartTime(RWDBDateTime());
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
                                dout << RWTime() << " - Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
                {
                    if( _LM_DEBUG & LM_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - NO control gear, LM Program: " << getPAOName() << endl;
                    }

                    setProgramState(CtiLMProgramBase::NonControllingState);
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


    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.entries() > 0 && _lmprogramdirectgroups.entries() > 0 )
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.entries() )
        {
            currentGearObject = getCurrentGearObject();

            if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
            {
                LONG refreshRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                //LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                //take all groups in a manual control
                RWCString refreshCountDownType = currentGearObject->getMethodOptionType();
                LONG maxRefreshShedTime = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling all time refresh groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        if( !refreshCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( maxRefreshShedTime > 0 )
                            {
                                ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();

                                if( maxRefreshShedTime > 0 &&
                                    tempShedTime > maxRefreshShedTime )
                                {
                                    tempShedTime = maxRefreshShedTime;
                                }
                                shedTime = tempShedTime;
                            }
                            else
                            {
                                RWDBDateTime tempDateTime;
                                RWDBDateTime compareDateTime(tempDateTime.year(),tempDateTime.month(),tempDateTime.dayOfMonth(),0,0,0,0);
                                compareDateTime.addDays(1);
                                ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();

                                if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                {
                                    tempShedTime = compareDateTime.seconds() - RWDBDateTime().seconds();
                                }
                                else
                                {
                                    tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();
                                }

                                shedTime = tempShedTime;
                            }
                        }
                        CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setControlStartTime(RWDBDateTime());
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
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) )
            {
                LONG percent = currentGearObject->getMethodRate();
                LONG period = currentGearObject->getMethodPeriod();
                LONG cycleCount = currentGearObject->getMethodRateCount();
                RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
                LONG maxCycleCount = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Smart Cycling all groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was different
                        cycleCount = currentGearObject->getMethodRateCount();

                        if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( maxCycleCount > 0 || cycleCount == 0 )
                            {
                                if( period != 0 )
                                {
                                    ULONG tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                    {
                                        tempCycleCount++;
                                    }

                                    if( maxCycleCount > 0 &&
                                        tempCycleCount > maxCycleCount )
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
                                LONG tempCycleCount = cycleCount;
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
                        else if( !currentGearObject->getMethodOptionType().compareTo(CtiLMProgramDirectGear::LimitedCountDownMethodOptionType,RWCString::ignoreCase) )
                        {//can't really do anything for limited count down on start up
                        }//we have to send the default because it is programmed in the switch

                        CtiRequestMsg* requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setControlStartTime(RWDBDateTime());
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
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) )
            {
                LONG percent = currentGearObject->getMethodRate();
                LONG period = currentGearObject->getMethodPeriod();

                LONG offTime = period * (percent / 100.0);
                LONG onTime = period - offTime;

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Master Station Cycling groups, LM Program: " << getPAOName() << endl;
                }

                int numberOfGroupsToTake = 1;
                if( _lmprogramdirectgroups.entries() >= 8 )
                {//take 2 at a time
                    numberOfGroupsToTake = 2;
                    if( (_lmprogramdirectgroups.entries() % 2) != 0 )//is there an odd number of groups
                    {//is this the last group in the group list, because if it is we only take 1 not 2
                        CtiLMGroupBase* testLMGroup = findGroupToTake(currentGearObject);
                        if( testLMGroup == _lmprogramdirectgroups[_lmprogramdirectgroups.entries()-1] )
                        {
                            numberOfGroupsToTake = 1;
                        }
                    }
                }

                for(int y=0;y<numberOfGroupsToTake;y++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        //don't check if it has ample control time because it's manual
                        /*if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                        {
                            offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                        }*/
                        CtiRequestMsg* requestMsg = currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setControlStartTime(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
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
            }
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
            {
                LONG sendRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                }

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                }
                for(LONG i=0;i<numberOfGroupsToTake;i++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setControlStartTime(RWDBDateTime());
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
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) )
            {
                LONG gearStartRawState = currentGearObject->getMethodRateCount();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling latch groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
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
                        currentLMGroup->setControlStartTime(RWDBDateTime());
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
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
            {
                LONG percent = currentGearObject->getMethodRate();
                LONG period = currentGearObject->getMethodPeriod();
                LONG cycleCount = currentGearObject->getMethodRateCount();
                RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
                LONG maxCycleCount = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - True Cycling all groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was different
                        cycleCount = currentGearObject->getMethodRateCount();

                        if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( maxCycleCount > 0 || cycleCount == 0 )
                            {
                                if( period != 0 )
                                {
                                    LONG tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                    {
                                        tempCycleCount++;
                                    }

                                    if( maxCycleCount > 0 &&
                                        tempCycleCount > maxCycleCount )
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
                                LONG tempCycleCount = cycleCount;
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
                        else if( !currentGearObject->getMethodOptionType().compareTo(CtiLMProgramDirectGear::LimitedCountDownMethodOptionType,RWCString::ignoreCase) )
                        {//can't really do anything for limited count down on start up
                        }//we have to send the default because it is programmed in the switch

                        CtiRequestMsg* requestMsg = NULL;
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                        {
                            requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                        }
                        else
                        {
                            requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << " : " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setControlStartTime(RWDBDateTime());
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
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
            {
                CtiLMProgramThermoStatGear* thermostatGearObject = (CtiLMProgramThermoStatGear*)currentGearObject;
                RWCString settings = thermostatGearObject->getSettings();
                LONG minValue = thermostatGearObject->getMinValue();
                LONG maxValue = thermostatGearObject->getMaxValue();
                LONG valueB = thermostatGearObject->getValueB();
                LONG valueD = thermostatGearObject->getValueD();
                LONG valueF = thermostatGearObject->getValueF();
                LONG random = thermostatGearObject->getRandom();
                LONG valueTA = thermostatGearObject->getValueTa();
                LONG valueTB = thermostatGearObject->getValueTb();
                LONG valueTC = thermostatGearObject->getValueTc();
                LONG valueTD = thermostatGearObject->getValueTd();
                LONG valueTE = thermostatGearObject->getValueTe();
                LONG valueTF = thermostatGearObject->getValueTf();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Thermostat Set Point command all groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                        {
                            CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(settings, minValue, maxValue, valueB, valueD, valueF, random, valueTA, valueTB, valueTC, valueTD, valueTE, valueTF, defaultLMStartPriority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
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
                            dout << RWTime() << " - Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                }
                if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
            {
                if( _LM_DEBUG & LM_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - NO control gear, LM Program: " << getPAOName() << endl;
                }

                setProgramState(CtiLMProgramBase::NonControllingState);
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


    CtiLMGroupBase* returnGroup = NULL;

    if( !currentGearObject->getGroupSelectionMethod().compareTo(CtiLMProgramDirectGear::LastControlledSelectionMethod,RWCString::ignoreCase) )
    {
        BOOL found = FALSE;
        for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( getLastGroupControlled() == currentLMGroup->getPAOId() )
            {
                if( i < (_lmprogramdirectgroups.entries()-1) )
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i+1];
                    if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                          !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ) )
                    {
                        found = TRUE;
                        returnGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i+1];
                        returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                    }
                }
                else if( i == (_lmprogramdirectgroups.entries()-1) )
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[0];
                    if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                          !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ) )
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
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                    !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() &&
                    currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState )
                {
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                    returnGroup = currentLMGroup;
                    break;
                }
            }
        }
    }
    else if( !currentGearObject->getGroupSelectionMethod().compareTo(CtiLMProgramDirectGear::AlwaysFirstGroupSelectionMethod,RWCString::ignoreCase) )
    {
        if( currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
        {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                    !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() &&
                    currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState )
                {
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                    returnGroup = currentLMGroup;
                    break;
                }
            }
        }
        else
        {
            BOOL atLeastOneActive = FALSE;
            for(LONG x=0;x<_lmprogramdirectgroups.entries();x++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[x];
                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    atLeastOneActive = TRUE;
                    break;
                }
            }

            BOOL found = FALSE;
            if( atLeastOneActive )//is already active so take last group plus one
            {
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( getLastGroupControlled() == currentLMGroup->getPAOId() )
                    {
                        if( i < (_lmprogramdirectgroups.entries()-1) )
                        {
                            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i+1];
                            if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                                !currentLMGroup->getDisableFlag() &&
                                !currentLMGroup->getControlInhibit() )
                            {
                                found = TRUE;
                                returnGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i+1];
                                returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                            }
                        }
                        else if( i == (_lmprogramdirectgroups.entries()-1) )
                        {
                            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[0];
                            if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                                !currentLMGroup->getDisableFlag() &&
                                !currentLMGroup->getControlInhibit() )
                            {
                                found = TRUE;
                                returnGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[0];
                                returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                            }
                        }
                        break;
                    }
                }
            }
            else//program inactive so pick the first group
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[0];
                if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                    !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    found = TRUE;
                    returnGroup = currentLMGroup;
                    returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                }
            }

            if( !found )
            {
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState )
                    {
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                        returnGroup = currentLMGroup;
                        break;
                    }
                }
            }
        }
    }
    else if( !currentGearObject->getGroupSelectionMethod().compareTo(CtiLMProgramDirectGear::LeastControlTimeSelectionMethod,RWCString::ignoreCase) )
    {
        // progressive lookup first look at current hours daily then current hours monthly
        // then current hours seasonal but not annually
        for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                !currentLMGroup->getDisableFlag() &&
                !currentLMGroup->getControlInhibit() &&
                currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState )
            {
                if( returnGroup == NULL )
                {
                    returnGroup = currentLMGroup;
                }
                else if( currentLMGroup->getHoursDailyPointId() > 0 && returnGroup->getHoursDailyPointId() > 0 )
                {
                    if( currentLMGroup->getCurrentHoursDaily() < returnGroup->getCurrentHoursDaily() )
                    {
                        returnGroup = currentLMGroup;
                    }
                    //if the group's daily control is greater than the current lowest then we don't check higher levels
                    else if( currentLMGroup->getCurrentHoursDaily() == returnGroup->getCurrentHoursDaily() )
                    {
                        if( currentLMGroup->getHoursMonthlyPointId() > 0 && returnGroup->getHoursMonthlyPointId() > 0 )
                        {
                            if( currentLMGroup->getCurrentHoursMonthly() < returnGroup->getCurrentHoursMonthly() )
                            {
                                returnGroup = currentLMGroup;
                            }
                            //if the group's monthly control is greater than the current lowest then we don't check higher levels
                            else if( currentLMGroup->getCurrentHoursMonthly() == returnGroup->getCurrentHoursMonthly() )
                            {
                                if( currentLMGroup->getHoursSeasonalPointId() > 0 && returnGroup->getHoursSeasonalPointId() > 0 )
                                {
                                    if( currentLMGroup->getCurrentHoursSeasonal() < returnGroup->getCurrentHoursSeasonal() )
                                    {
                                        returnGroup = currentLMGroup;
                                    }
                                }
                            }
                        }
                        else if( currentLMGroup->getHoursSeasonalPointId() > 0 && returnGroup->getHoursSeasonalPointId() > 0 )
                        {
                            if( currentLMGroup->getCurrentHoursSeasonal() < returnGroup->getCurrentHoursSeasonal() )
                            {
                                returnGroup = currentLMGroup;
                            }
                        }
                    }
                }
                else if( currentLMGroup->getHoursMonthlyPointId() > 0 && returnGroup->getHoursMonthlyPointId() > 0 )
                {
                    if( currentLMGroup->getCurrentHoursMonthly() < returnGroup->getCurrentHoursMonthly() )
                    {
                        returnGroup = currentLMGroup;
                    }
                    //if the group's monthly control is greater than the current lowest then we don't check higher levels
                    else if( currentLMGroup->getCurrentHoursMonthly() == returnGroup->getCurrentHoursMonthly() )
                    {
                        if( currentLMGroup->getHoursSeasonalPointId() > 0 && returnGroup->getHoursSeasonalPointId() > 0 )
                        {
                            if( currentLMGroup->getCurrentHoursSeasonal() < returnGroup->getCurrentHoursSeasonal() )
                            {
                                returnGroup = currentLMGroup;
                            }
                        }
                    }
                }
                else if( currentLMGroup->getHoursSeasonalPointId() > 0 && returnGroup->getHoursSeasonalPointId() > 0 )
                {
                    if( currentLMGroup->getCurrentHoursSeasonal() < returnGroup->getCurrentHoursSeasonal() )
                    {
                        returnGroup = currentLMGroup;
                    }
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
BOOL CtiLMProgramDirect::hasGearChanged(LONG currentPriority, RWOrdered controlAreaTriggers, ULONG secondsFrom1901, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded)
{
    BOOL returnBoolean = FALSE;

    //The below block sees if the program needs to shift to a higher gear
    if( _currentgearnumber < (_lmprogramdirectgears.entries()-1) )
    {
        CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

        if( !currentGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::NoneChangeCondition,RWCString::ignoreCase) )
        {
            //returnBoolean = FALSE;
        }
        else if( !currentGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::DurationChangeCondition,RWCString::ignoreCase) )
        {
            LONG secondsControlling = secondsFrom1901 - getStartedControlling().seconds();
            if( secondsControlling >= currentGearObject->getChangeDuration() &&
                _currentgearnumber+1 < _lmprogramdirectgears.entries() )
            {
                _currentgearnumber++;
                {
                    RWCString text = RWCString("Duration Gear Change Up Program: ");
                    text += getPAOName();
                    RWCString additional = RWCString("Previous Gear: ");
                    additional += currentGearObject->getGearName();
                    additional += " New Gear: ";
                    additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                    multiDispatchMsg->insert(signal);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                }
                hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded);
                returnBoolean = TRUE;
            }
        }
        else if( !currentGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::PriorityChangeCondition,RWCString::ignoreCase) )
        {
            if( currentPriority >= currentGearObject->getChangePriority() &&
                _currentgearnumber+1 < _lmprogramdirectgears.entries() )
            {
                _currentgearnumber++;
                {
                    RWCString text = RWCString("Priority Gear Change Up Program: ");
                    text += getPAOName();
                    RWCString additional = RWCString("Previous Gear: ");
                    additional += currentGearObject->getGearName();
                    additional += " New Gear: ";
                    additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                    multiDispatchMsg->insert(signal);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                }
                hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded);
                returnBoolean = TRUE;
            }
        }
        else if( !currentGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::TriggerOffsetChangeCondition,RWCString::ignoreCase) )
        {
            if( currentGearObject->getChangeTriggerNumber() > 0 &&
                currentGearObject->getChangeTriggerNumber() <= controlAreaTriggers.entries() )
            {
                CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[currentGearObject->getChangeTriggerNumber()-1];
                if( isTriggerCheckNeeded &&
                    ( trigger->getPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ||
                      trigger->getProjectedPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ) &&
                    _currentgearnumber+1 < _lmprogramdirectgears.entries() )
                {
                    _currentgearnumber++;
                    {
                        RWCString text = RWCString("Trigger Offset Gear Change Up Program: ");
                        text += getPAOName();
                        RWCString additional = RWCString("Previous Gear: ");
                        additional += currentGearObject->getGearName();
                        additional += " New Gear: ";
                        additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                        CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                        multiDispatchMsg->insert(signal);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << text << ", " << additional << endl;
                        }
                    }
                    hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded);
                    returnBoolean = TRUE;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Invalid ChangeTriggerNumber: " << currentGearObject->getChangeTriggerNumber() << ", trigger numbers start at 1 in program: " << getPAOName() << endl;
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

            if( !previousGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::DurationChangeCondition,RWCString::ignoreCase) )
            {
                //doesn't make sense to shift down from a duration shift up
                //returnBoolean = FALSE;
            }
            else if( !previousGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::PriorityChangeCondition,RWCString::ignoreCase) )
            {
                if( currentPriority < previousGearObject->getChangePriority() &&
                    _currentgearnumber-1 >= 0 )
                {
                    {
                        RWCString text = RWCString("Priority Gear Change Down Program: ");
                        text += getPAOName();
                        RWCString additional = RWCString("Previous Gear: ");
                        additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                        additional += " New Gear: ";
                        additional += previousGearObject->getGearName();
                        CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                        multiDispatchMsg->insert(signal);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << text << ", " << additional << endl;
                        }
                    }
                    _currentgearnumber--;
                    hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded);
                    returnBoolean = TRUE;
                }
            }
            else if( !previousGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::TriggerOffsetChangeCondition,RWCString::ignoreCase) )
            {
                if( previousGearObject->getChangeTriggerNumber() > 0 &&
                    previousGearObject->getChangeTriggerNumber() <= controlAreaTriggers.entries() )
                {
                    CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[previousGearObject->getChangeTriggerNumber()-1];
                    if( isTriggerCheckNeeded && 
                        trigger->getPointValue() < (trigger->getThreshold() + previousGearObject->getChangeTriggerOffset()) &&
                        trigger->getProjectedPointValue() < (trigger->getThreshold() + previousGearObject->getChangeTriggerOffset()) &&
                        _currentgearnumber-1 >= 0 )
                    {
                        {
                            RWCString text = RWCString("Trigger Offset Gear Change Down Program: ");
                            text += getPAOName();
                            RWCString additional = RWCString("Previous Gear: ");
                            additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                            additional += " New Gear: ";
                            additional += previousGearObject->getGearName();
                            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                            multiDispatchMsg->insert(signal);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - " << text << ", " << additional << endl;
                            }
                        }
                        _currentgearnumber--;
                        hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded);
                        returnBoolean = TRUE;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Invalid ChangeTriggerNumber: " << previousGearObject->getChangeTriggerNumber() << ", trigger numbers start at 1 in program: " << getPAOName() << endl;
                }
            }
            else if( !previousGearObject->getChangeCondition().compareTo(CtiLMProgramDirectGear::NoneChangeCondition,RWCString::ignoreCase) )
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
BOOL CtiLMProgramDirect::maintainProgramControl(LONG currentPriority, RWOrdered& controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isPastMinResponseTime, BOOL isTriggerCheckNeeded)
{


    BOOL returnBoolean = FALSE;
    LONG previousGearNumber = _currentgearnumber;

    if( isWithinValidControlWindow(secondsFromBeginningOfDay) )
    {
        if( isPastMinResponseTime &&
            hasGearChanged(currentPriority, controlAreaTriggers, secondsFrom1901, multiDispatchMsg, isTriggerCheckNeeded) )
        {
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Gear Change, LM Program: " << getPAOName() << ", previous gear number: " << previousGearNumber << ", new gear number: " << _currentgearnumber << endl;
            }
            returnBoolean = ( 0.0 > updateProgramControlForGearChange(previousGearNumber, multiPilMsg, multiDispatchMsg));
        }
        else
        {
            if( refreshStandardProgramControl(secondsFrom1901, multiPilMsg, multiDispatchMsg) )
            {
                returnBoolean = TRUE;
            }
        }
    }
    else
    {
        stopProgramControl(multiPilMsg, multiDispatchMsg, secondsFrom1901);
        returnBoolean = TRUE;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    updateProgramControlForGearChange

    Handles the changing of gears within a running program by sending pil
    requests to change the type of shed or cycle depending on the original
    gear control method and the new gear method.
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::updateProgramControlForGearChange(LONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{


    DOUBLE expectedLoadReduced = 0.0;

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();
    CtiLMProgramDirectGear* previousGearObject = NULL;

    if( previousGearNumber < _lmprogramdirectgears.entries() )
    {
        previousGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[previousGearNumber];
    }

    if( currentGearObject != NULL && previousGearObject != NULL && _lmprogramdirectgroups.entries() > 0 )
    {
        if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
        {
            if( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) ||
                ( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) &&
                  getManualControlReceivedFlag() ) )
            {
                // Normally we would only take the commented out "numberOfGroupsToTake" but when we
                // switch gears to refresh from smart cycle or rotation or a manually controlled non control
                // there is the possibility that all groups need to be shed so that's what we'll do
                LONG refreshRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                //LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                RWCString refreshCountDownType = currentGearObject->getMethodOptionType();
                LONG maxRefreshShedTime = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Time Refreshing all previously controlled groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState ||
                          !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ) )
                    {
                        if( !refreshCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( maxRefreshShedTime > 0 )
                            {
                                ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();

                                if( maxRefreshShedTime > 0 &&
                                    tempShedTime > maxRefreshShedTime )
                                {
                                    tempShedTime = maxRefreshShedTime;
                                }
                                shedTime = tempShedTime;
                            }
                            else
                            {
                                RWDBDateTime tempDateTime;
                                RWDBDateTime compareDateTime(tempDateTime.year(),tempDateTime.month(),tempDateTime.dayOfMonth(),0,0,0,0);
                                compareDateTime.addDays(1);
                                ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();

                                if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                {
                                    tempShedTime = compareDateTime.seconds() - RWDBDateTime().seconds();
                                }
                                else
                                {
                                    tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();
                                }

                                shedTime = tempShedTime;
                            }
                        }
                        CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
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
                if( getProgramState() == CtiLMProgramBase::ActiveState ||
                    getProgramState() == CtiLMProgramBase::NonControllingState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                    for(LONG j=0;j<_lmprogramdirectgroups.entries();j++)
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
            else if( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) ||
                     ( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) &&
                       !getManualControlReceivedFlag() ) )
            {
                LONG refreshRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                RWCString refreshCountDownType = currentGearObject->getMethodOptionType();
                LONG maxRefreshShedTime = currentGearObject->getMethodOptionMax();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                }

                for(LONG i=0;i<numberOfGroupsToTake;i++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        if( !refreshCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( maxRefreshShedTime > 0 )
                            {
                                ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();

                                if( maxRefreshShedTime > 0 &&
                                    tempShedTime > maxRefreshShedTime )
                                {
                                    tempShedTime = maxRefreshShedTime;
                                }
                                shedTime = tempShedTime;
                            }
                            else
                            {
                                RWDBDateTime tempDateTime;
                                RWDBDateTime compareDateTime(tempDateTime.year(),tempDateTime.month(),tempDateTime.dayOfMonth(),0,0,0,0);
                                compareDateTime.addDays(1);
                                ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();

                                if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                {
                                    tempShedTime = compareDateTime.seconds() - RWDBDateTime().seconds();
                                }
                                else
                                {
                                    tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();
                                }

                                shedTime = tempShedTime;
                            }
                        }
                        CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
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

                if( getProgramState() == CtiLMProgramBase::ActiveState ||
                    getProgramState() == CtiLMProgramBase::NonControllingState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                    for(LONG j=0;j<_lmprogramdirectgroups.entries();j++)
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
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Program: " << getPAOName() << ", Gear#: " << previousGearObject->getGearNumber() << " doesn't have a valid control method in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Smart Cycling all groups, LM Program: " << getPAOName() << endl;
            }
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    cycleCount = currentGearObject->getMethodRateCount();
                    if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                    {
                        LONG estimatedControlTimeInSeconds = (period * (((double)percent)/100.0)) * cycleCount;
                        if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                        {
                            LONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                            if( period != 0 && percent != 0 )
                            {
                                LONG tempCycleCount = ((LONG)(controlTimeLeft*(100.0/((double)percent)))) / period;
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
                    else if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::LimitedCountDownMethodOptionType,RWCString::ignoreCase) )
                    {//can't really do anything for limited count down on start up
                    }//we have to send the default because it is programmed in the switch
    
                    CtiRequestMsg* requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                    currentLMGroup->setLastControlString(requestMsg->CommandString());
                    multiPilMsg->insert( requestMsg );
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
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();

            LONG offTime = period * (percent / 100.0);
            LONG onTime = period - offTime;

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Master Station Cycling groups, LM Program: " << getPAOName() << endl;
            }

            int numberOfGroupsToTake = 1;
            if( _lmprogramdirectgroups.entries() >= 8 )
            {//take 2 at a time
                numberOfGroupsToTake = 2;
                if( (_lmprogramdirectgroups.entries() % 2) != 0 )//is there an odd number of groups
                {//is this the last group in the group list, because if it is we only take 1 not 2
                    CtiLMGroupBase* testLMGroup = findGroupToTake(currentGearObject);
                    if( testLMGroup == _lmprogramdirectgroups[_lmprogramdirectgroups.entries()-1] )
                    {
                        numberOfGroupsToTake = 1;
                    }
                }
            }

            for(int y=0;y<numberOfGroupsToTake;y++)
            {
                CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                if( currentLMGroup != NULL )
                {
                    if( !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                    {
                        offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                    }
                    CtiRequestMsg* requestMsg = currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMStartPriority);
                    currentLMGroup->setLastControlString(requestMsg->CommandString());
                    multiPilMsg->insert( requestMsg );
                    setLastControlSent(RWDBDateTime());
                    setLastGroupControlled(currentLMGroup->getPAOId());
                    currentLMGroup->setLastControlSent(RWDBDateTime());
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                    for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
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
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
        {
            if( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
            {
                LONG sendRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling all rotation groups, LM Program: " << getPAOName() << endl;
                }
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
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
            else if( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                     !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
            {
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                    int priority = 11;
                    RWCString controlString = "control terminate";
                    if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                    {
                        controlString = "control xcom terminate";
                    }
                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Sending terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                    }
                    multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority));
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                }

                LONG sendRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.entries();
                }

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                }
                for(LONG j=0;j<numberOfGroupsToTake;j++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
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
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Gear Control Method: " << getPAOName() << " Gear#: " << currentGearObject->getGearNumber() << " control method can't support gear changes.  In: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - True Cycling all groups, LM Program: " << getPAOName() << endl;
            }
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    cycleCount = currentGearObject->getMethodRateCount();
                    if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                    {
                        LONG estimatedControlTimeInSeconds = (period * (((double)percent)/100.0)) * cycleCount;
                        if( !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                        {
                            LONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                            if( period != 0 && percent != 0 )
                            {
                                LONG tempCycleCount = ((LONG)(controlTimeLeft*(100.0/((double)percent)))) / period;
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
                    else if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::LimitedCountDownMethodOptionType,RWCString::ignoreCase) )
                    {//can't really do anything for limited count down on start up
                    }//we have to send the default because it is programmed in the switch
    
                    CtiRequestMsg* requestMsg = NULL;
                    if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                    {
                        requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                    }
                    else
                    {
                        requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << " : " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    currentLMGroup->setLastControlString(requestMsg->CommandString());
                    multiPilMsg->insert( requestMsg );
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
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
        {
            CtiLMProgramThermoStatGear* thermostatGearObject = (CtiLMProgramThermoStatGear*)currentGearObject;
            RWCString settings = thermostatGearObject->getSettings();
            LONG minValue = thermostatGearObject->getMinValue();
            LONG maxValue = thermostatGearObject->getMaxValue();
            LONG valueB = thermostatGearObject->getValueB();
            LONG valueD = thermostatGearObject->getValueD();
            LONG valueF = thermostatGearObject->getValueF();
            LONG random = thermostatGearObject->getRandom();
            LONG valueTA = thermostatGearObject->getValueTa();
            LONG valueTB = thermostatGearObject->getValueTb();
            LONG valueTC = thermostatGearObject->getValueTc();
            LONG valueTD = thermostatGearObject->getValueTd();
            LONG valueTE = thermostatGearObject->getValueTe();
            LONG valueTF = thermostatGearObject->getValueTf();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Thermostat Set Point command all groups, LM Program: " << getPAOName() << endl;
            }
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                    {
                        CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(settings, minValue, maxValue, valueB, valueD, valueF, random, valueTA, valueTB, valueTC, valueTD, valueTE, valueTF, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
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
                        dout << RWTime() << " - Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
        {
            if( _LM_DEBUG & LM_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Entering NO control gear, LM Program: " << getPAOName() << endl;
            }

            RWCString tempControlMethod = previousGearObject->getControlMethod();
            RWCString tempMethodStopType = previousGearObject->getMethodStopType();

            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( RWDBDateTime().seconds() > currentLMGroup->getControlStartTime().seconds() + getMinActivateTime() ||
                    getManualControlReceivedFlag() )
                {
                    if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                        !tempControlMethod.compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
                    {
                        if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RestoreStopType,RWCString::ignoreCase) )
                        {
                            int priority = 11;
                            RWCString controlString = "control restore";
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                            }
                            CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            RWDBDateTime nowPlusRandom = RWDBDateTime();
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EMETCON )
                            {
                                nowPlusRandom.addMinutes(2);
                            }
                            else
                            {
                                nowPlusRandom.addMinutes(1);
                            }
                            currentLMGroup->setControlCompleteTime(nowPlusRandom);
                        }
                        else if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::StopCycleStopType,RWCString::ignoreCase) ||
                                 !tempMethodStopType.compareTo(CtiLMProgramDirectGear::TimeInStopType,RWCString::ignoreCase) ||
                                 !tempMethodStopType.compareTo("Time-In",RWCString::ignoreCase) )
                        {//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                            int priority = 11;
                            RWCString controlString = "control terminate";
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                            {
                                controlString = "control xcom terminate";
                            }
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Sending terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                            }
                            CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            RWDBDateTime nowPlusRandom = RWDBDateTime();
                            nowPlusRandom.addMinutes(previousGearObject->getMethodPeriod()/60/2);
                            currentLMGroup->setControlCompleteTime(nowPlusRandom);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    else if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) ||
                             !tempControlMethod.compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) ||
                             !tempControlMethod.compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ||
                             !tempControlMethod.compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
                    {
                        if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RestoreStopType,RWCString::ignoreCase) )
                        {
                            int priority = 11;
                            RWCString controlString = "control restore";
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                            }
                            CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            RWDBDateTime nowPlusRandom = RWDBDateTime();
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EMETCON )
                            {
                                nowPlusRandom.addMinutes(2);
                            }
                            else
                            {
                                nowPlusRandom.addMinutes(1);
                            }
                            currentLMGroup->setControlCompleteTime(nowPlusRandom);
                        }
                        else if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::TimeInStopType,RWCString::ignoreCase) || !tempMethodStopType.compareTo("Time-In",RWCString::ignoreCase) )
                        {
                            //"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                            //CtiLockGuard<CtiLogger> logger_guard(dout);
                            //dout << RWTime() << " - Stopping control on LM Group: " << currentLMGroup->getPAOName() << " with Stop Type of (Time In) in: " << __FILE__ << " at:" << __LINE__ << endl;
                            //I don't know if I should do anything unique here yet?
                            //multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), "control terminate"));
                            //setLastControlSent(RWDBDateTime());
                            //currentLMGroup->setLastControlSent(RWDBDateTime());
                            RWDBDateTime timeToTimeIn = RWDBDateTime(1990,1,1,0,0,0,0);//put in a bogus time stamp
                            if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) )
                            {
                                timeToTimeIn = currentLMGroup->getLastControlSent();
                                LONG offTimeInSeconds = previousGearObject->getMethodPeriod() * (previousGearObject->getMethodRate() / 100.0);
                                timeToTimeIn.addMinutes(offTimeInSeconds/60);
                            }
                            else if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
                            {
                                timeToTimeIn = currentLMGroup->getLastControlSent();
                                CtiLMProgramThermoStatGear* thermostatGear = (CtiLMProgramThermoStatGear*)previousGearObject;
                                int minutesToAdd = 0;
                                minutesToAdd += (thermostatGear->getRandom()/2+thermostatGear->getRandom()%2);
                                minutesToAdd += thermostatGear->getValueTa();
                                minutesToAdd += thermostatGear->getValueTb();
                                minutesToAdd += thermostatGear->getValueTc();
                                minutesToAdd += thermostatGear->getValueTd();
                                minutesToAdd += thermostatGear->getValueTe();
                                minutesToAdd += thermostatGear->getValueTf();
                                timeToTimeIn.addMinutes(minutesToAdd);
                            }
                            else
                            {
                                timeToTimeIn = currentLMGroup->getLastControlSent();
                                timeToTimeIn.addMinutes(previousGearObject->getMethodPeriod()/60);
                            }
                            currentLMGroup->setControlCompleteTime(timeToTimeIn);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    else if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) )
                    {
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( (((CtiLMGroupPoint*)currentLMGroup)->getStartControlRawState()?0:1), defaultLMRefreshPriority ) );
                        }
                        else
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( (previousGearObject->getMethodRateCount()?0:1), defaultLMRefreshPriority ) );
                        }
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                    }
                    else if( !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
                    {
                        // Its not controlling so a stop method doesn't much matter, does it?
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Invalid current gear control method: " << tempControlMethod << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
    
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                    currentLMGroup->setControlStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
                }
                else
                {
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactivePendingState);
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Group cannot be set inactive yet because of minimum active time Group: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
    
            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::NonControllingState);
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


/*
*/
BOOL CtiLMProgramDirect::stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupBase* currentLMGroup, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    {
        char tempchar[80];
        RWCString text = RWCString("Stopping Control Group: ");
        text += currentLMGroup->getPAOName();
        RWCString additional = RWCString("Reason: Exceeded Control Time Limit");
        additional += " PAO Id: ";
        _ltoa(currentLMGroup->getPAOId(),tempchar,10);
        additional += tempchar;

        multiDispatchMsg->insert(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << text << ", " << additional << endl;
        }
    }
    RWCString tempMethodStopType = currentGearObject->getMethodStopType();
    if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RestoreStopType,RWCString::ignoreCase) )
    {
        int priority = 11;
        RWCString controlString = "control restore";
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
        }
        CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
        currentLMGroup->setLastControlString(requestMsg->CommandString());
        multiPilMsg->insert( requestMsg );
        setLastControlSent(RWDBDateTime());
        currentLMGroup->setLastControlSent(RWDBDateTime());
    }
    else if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::StopCycleStopType,RWCString::ignoreCase) ||
             !tempMethodStopType.compareTo(CtiLMProgramDirectGear::TimeInStopType,RWCString::ignoreCase) ||
             !tempMethodStopType.compareTo("Time-In",RWCString::ignoreCase) )
    {//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
        int priority = 11;
        RWCString controlString = "control terminate";
        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
        {
            controlString = "control xcom terminate";
        }
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Sending terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
        }
        CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
        currentLMGroup->setLastControlString(requestMsg->CommandString());
        multiPilMsg->insert( requestMsg );
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
    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);

    return TRUE;
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
    BOOL returnBoolean = FALSE;
    LONG numberOfActiveGroups = 0;
    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL && _lmprogramdirectgroups.entries() > 0 )
    {
        if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
        {
            LONG refreshRate = currentGearObject->getMethodRate();
            LONG shedTime = currentGearObject->getMethodPeriod();
            RWCString refreshCountDownType = currentGearObject->getMethodOptionType();
            LONG maxRefreshShedTime = currentGearObject->getMethodOptionMax();

            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                    !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                        !getManualControlReceivedFlag() &&
                        !doesGroupHaveAmpleControlTime(currentLMGroup,0) )
                    {
                        returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, secondsFrom1901, multiPilMsg, multiDispatchMsg));
                    }
                    else if( secondsFrom1901 >= currentLMGroup->getLastControlSent().seconds()+refreshRate )
                    {
                        if( !refreshCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( getManualControlReceivedFlag() )
                            {
                                if( maxRefreshShedTime > 0 )
                                {
                                    ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();
    
                                    if( maxRefreshShedTime > 0 &&
                                        tempShedTime > maxRefreshShedTime )
                                    {
                                        tempShedTime = maxRefreshShedTime;
                                    }
                                    shedTime = tempShedTime;
                                }
                                else
                                {
                                    RWDBDateTime tempDateTime;
                                    RWDBDateTime compareDateTime(tempDateTime.year(),tempDateTime.month(),tempDateTime.dayOfMonth(),0,0,0,0);
                                    compareDateTime.addDays(1);
                                    ULONG tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();
    
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempShedTime = compareDateTime.seconds() - RWDBDateTime().seconds();
                                    }
                                    else
                                    {
                                        tempShedTime = getDirectStopTime().seconds() - RWDBDateTime().seconds();
                                    }
    
                                    shedTime = tempShedTime;
                                }
                            }

                            LONG estimatedControlTimeInSeconds = shedTime;
                            if( !getManualControlReceivedFlag() &&
                                !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                            {
                                shedTime = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                            }
                        }
                        CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMRefreshPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        //This setLastControlSent() is just to be used for new control sent
                        //setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                        returnBoolean = TRUE;
                    }
                }
                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    numberOfActiveGroups++;
                }
            }

            if( returnBoolean &&
                getProgramState() != CtiLMProgramBase::ManualActiveState &&
		getProgramState() != CtiLMProgramBase::TimedActiveState )
            {
                if( numberOfActiveGroups == _lmprogramdirectgroups.entries() )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
                else if( numberOfActiveGroups == 0 )
                {
                    setProgramState(CtiLMProgramBase::InactiveState);
                }
                else
                {
                    setProgramState(CtiLMProgramBase::ActiveState);
                }
            }
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                 !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            LONG cycleRefreshRate = currentGearObject->getCycleRefreshRate();
            RWCString cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( cycleCount == 0 )
            {
                cycleCount = 8;//seems like a reasonable default
            }

            if( period != 0 )
            {
                for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
                {
                    CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

                    ULONG periodEndInSecondsFrom1901 = 0;
                    if( cycleRefreshRate == 0 )
                    {
                        periodEndInSecondsFrom1901 = currentLMGroup->getLastControlSent().seconds()+(period * cycleCount)+1;
                    }
                    else
                    {
                        periodEndInSecondsFrom1901 = currentLMGroup->getLastControlSent().seconds()+cycleRefreshRate;
                    }

                    if( secondsFrom1901 >= periodEndInSecondsFrom1901 &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was lower
                        cycleCount = currentGearObject->getMethodRateCount();
                        if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::CountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            if( getManualControlReceivedFlag() )
                            {
                                if( maxCycleCount > 0 || cycleCount == 0 )
                                {
                                    LONG tempCycleCount = (getDirectStopTime().seconds() - RWDBDateTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - RWDBDateTime().seconds()) % period) > 0 )
                                    {
                                        tempCycleCount++;
                                    }

                                    if( maxCycleCount > 0 &&
                                        tempCycleCount > maxCycleCount )
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
                                    LONG tempCycleCount = cycleCount;
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

                            LONG estimatedControlTimeInSeconds = (period * (((double)percent)/100.0)) * cycleCount;
                            if( !getManualControlReceivedFlag() &&
                                !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                            {
                                LONG controlTimeLeft = calculateGroupControlTimeLeft(currentLMGroup,estimatedControlTimeInSeconds);
                                if( period != 0 && percent != 0 )
                                {
                                    LONG tempCycleCount = ((LONG)(controlTimeLeft*(100.0/((double)percent)))) / period;
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
                        else if( !cycleCountDownType.compareTo(CtiLMProgramDirectGear::LimitedCountDownMethodOptionType,RWCString::ignoreCase) )
                        {
                            LONG cycleLength = period * cycleCount;
                            LONG estimatedControlTimeInSeconds = cycleLength * (((double)percent)/100.0);
                            if( getManualControlReceivedFlag() )
                            {
                                ULONG secondsAtLastControl = currentLMGroup->getLastControlSent().seconds();
                                if( (secondsAtLastControl + cycleLength) >= (getDirectStopTime().seconds()-120) )
                                {//if the last control sent is not within 2 minutes before the stop control time a refresh will be sent
                                    cycleCount = 0;
                                }
                            }

                            if( !getManualControlReceivedFlag() &&
                                !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                            {
                                cycleCount = 0;
                            }
                        }
                        else if( !getManualControlReceivedFlag() &&
                                 !doesGroupHaveAmpleControlTime(currentLMGroup,0) )
                        {
                            cycleCount = 0;
                        }

                        if( cycleCount > 0 )
                        {
                            CtiRequestMsg* requestMsg = NULL;
                            if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) &&
                                currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                            {
                                requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                            }
                            else
                            {
                                requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
                                if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << " : " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            multiPilMsg->insert( requestMsg );
                            //This setLastControlSent() is just to be used for new control sent
                            //setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            if( !getManualControlReceivedFlag() &&
                                !doesGroupHaveAmpleControlTime(currentLMGroup,0) &&
                                currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                            {//we need to restore the group in the way set in the gear because it went over max control time
                                returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, secondsFrom1901, multiPilMsg, multiDispatchMsg));
                            }
                        }
                    }

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                        !getManualControlReceivedFlag() &&
                        !doesGroupHaveAmpleControlTime(currentLMGroup,0) )
                    {
                        returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, secondsFrom1901, multiPilMsg, multiDispatchMsg));
                    }

                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                    {
                        numberOfActiveGroups++;
                    }
                }

                if( returnBoolean &&
                    getProgramState() != CtiLMProgramBase::ManualActiveState &&
		    getProgramState() != CtiLMProgramBase::TimedActiveState )
                {
                    if( numberOfActiveGroups == _lmprogramdirectgroups.entries() )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                    else if( numberOfActiveGroups == 0 )
                    {
                        setProgramState(CtiLMProgramBase::InactiveState);
                    }
                    else
                    {
                        setProgramState(CtiLMProgramBase::ActiveState);
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();

            LONG offTime = period * (percent / 100.0);
            LONG onTime = period - offTime;
            LONG sendRate = period / _lmprogramdirectgroups.entries();
            if( _lmprogramdirectgroups.entries() >= 8 )
            {//take two at a time
                sendRate = period / ((_lmprogramdirectgroups.entries()/2)+(_lmprogramdirectgroups.entries()%2));
            }

            ULONG sendRateEndFrom1901 = 0;

            //this loop turns groups inactive when there shed is concluded and refreshes emetcon and ripple groups
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
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
                    else if( currentLMGroup->getLastControlSent().seconds()+offTime <= secondsFrom1901 )
                    {//groups not currently shed need to be set back to inactive state
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        currentLMGroup->setControlCompleteTime(RWDBDateTime());
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Master Cycle group timing-in, LM Group: " << currentLMGroup->getPAOName() << endl;
                        }
                        if( !currentGearObject->getMethodStopType().compareTo(CtiLMProgramDirectGear::RestoreStopType,RWCString::ignoreCase) )
                        {
                            int priority = 11;
                            RWCString controlString = "control restore";
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                            }
                            multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority));
                        }
                        returnBoolean = TRUE;
                    }
                }

                if( currentLMGroup->getLastControlSent().seconds()+sendRate > sendRateEndFrom1901 )
                {//sendRateEndFrom1901 is set to the latest of the new group controls
                    sendRateEndFrom1901 = currentLMGroup->getLastControlSent().seconds()+sendRate;
                }
            }
            //this loop turns groups inactive when there shed is concluded and refreshes emetcon and ripple groups

            //this takes the next iteration of groups to be master cycled
            if( secondsFrom1901 >= sendRateEndFrom1901 )
            {
                int numberOfGroupsToTake = 1;
                if( _lmprogramdirectgroups.entries() >= 8 )
                {//take 2 at a time
                    numberOfGroupsToTake = 2;
                    if( (_lmprogramdirectgroups.entries() % 2) != 0 )//is there an odd number of groups
                    {//is this the last group in the group list, because if it is we only take 1 not 2
                        CtiLMGroupBase* testLMGroup = findGroupToTake(currentGearObject);
                        if( testLMGroup == _lmprogramdirectgroups[_lmprogramdirectgroups.entries()-1] )
                        {
                            numberOfGroupsToTake = 1;
                        }
                    }
                }

                for(int y=0;y<numberOfGroupsToTake;y++)
                {
                    CtiLMGroupBase* currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup != NULL )
                    {
                        if( !getManualControlReceivedFlag() &&
                            !doesGroupHaveAmpleControlTime(currentLMGroup,offTime) )
                        {
                            offTime = calculateGroupControlTimeLeft(currentLMGroup,offTime);
                        }
                        CtiRequestMsg* requestMsg = currentLMGroup->createMasterCycleRequestMsg(offTime, period, defaultLMRefreshPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        //This setLastControlSent() is just to be used for new control sent
                        //setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);

                        if( getProgramState() != CtiLMProgramBase::ManualActiveState &&
			    getProgramState() != CtiLMProgramBase::TimedActiveState )
                        {
                            setProgramState(CtiLMProgramBase::FullyActiveState);
                        }
                        returnBoolean = TRUE;
                    }
                    else
                    {
                        //This setLastControlSent() is just to be used for new control sent
                        //setLastControlSent(RWDBDateTime());
                        /*{
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }*/
                    }
                }
            }
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
        {
            LONG sendRate = currentGearObject->getMethodRate();
            LONG shedTime = currentGearObject->getMethodPeriod();
            LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

            if( numberOfGroupsToTake == 0 )
            {
                numberOfGroupsToTake = _lmprogramdirectgroups.entries();
            }

            ULONG sendRateEndFrom1901 = 0;

            // First we need to update the state of the currently active rotating groups
            for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
            {
                CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    if( secondsFrom1901 >= currentLMGroup->getLastControlSent().seconds()+shedTime )
                    {
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        currentLMGroup->setControlCompleteTime(RWDBDateTime());
                        returnBoolean = TRUE;
                    }
                } 

                if( currentLMGroup->getLastControlSent().seconds()+sendRate > sendRateEndFrom1901 )
                {//sendRateEndFrom1901 is set to the latest of the new group controls
                    sendRateEndFrom1901 = currentLMGroup->getLastControlSent().seconds()+sendRate;
                }
            }

            if( secondsFrom1901 >= sendRateEndFrom1901 )
            {
                // Now we need to take the next groups for the rotation method
                for(LONG j=0;j<numberOfGroupsToTake;j++)
                {
                    CtiLMGroupBase* nextLMGroupToTake = findGroupToTake(currentGearObject);
                    if( nextLMGroupToTake != NULL )
                    {
                        CtiRequestMsg* requestMsg = nextLMGroupToTake->createRotationRequestMsg(sendRate, shedTime, defaultLMRefreshPriority);
                        nextLMGroupToTake->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        //This setLastControlSent() is just to be used for new control sent
                        //setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(nextLMGroupToTake->getPAOId());
                        nextLMGroupToTake->setLastControlSent(RWDBDateTime());
                        if( nextLMGroupToTake->getGroupControlState() == CtiLMGroupBase::InactiveState )
                        {
                            nextLMGroupToTake->setControlStartTime(RWDBDateTime());
                        }
                        nextLMGroupToTake->setGroupControlState(CtiLMGroupBase::ActiveState);
                        returnBoolean = TRUE;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Program: " << getPAOName() << " couldn't find any groups to take in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }

            if( getProgramState() != CtiLMProgramBase::ManualActiveState &&
		getProgramState() != CtiLMProgramBase::TimedActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) )
        {
            /*CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Gear Control Method: " << getPAOName() << " Gear#: " << currentGearObject->getGearNumber() << " control method isn't supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;*/
        }
        else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) ||
                 !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
        {
            //we don't refresh set point commands or no control gears
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method in: " << __FILE__ << " at: " << __LINE__ << endl;
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
BOOL CtiLMProgramDirect::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901)
{
    BOOL returnBool = TRUE;

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL )
    {
        RWCString tempControlMethod = currentGearObject->getControlMethod();
        RWCString tempMethodStopType = currentGearObject->getMethodStopType();
        for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];
            if( secondsFrom1901 > currentLMGroup->getControlStartTime().seconds() + getMinActivateTime() ||
                getManualControlReceivedFlag() )
            {
                if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                    !tempControlMethod.compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
                {
                    if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RestoreStopType,RWCString::ignoreCase) )
                    {
                        int priority = 11;
                        RWCString controlString = "control restore";
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                        }
                        CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        RWDBDateTime nowPlusRandom = RWDBDateTime();
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EMETCON )
                        {
                            nowPlusRandom.addMinutes(2);
                        }
                        else
                        {
                            nowPlusRandom.addMinutes(1);
                        }
                        currentLMGroup->setControlCompleteTime(nowPlusRandom);
                    }
                    else if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::StopCycleStopType,RWCString::ignoreCase) ||
                             !tempMethodStopType.compareTo(CtiLMProgramDirectGear::TimeInStopType,RWCString::ignoreCase) ||
                             !tempMethodStopType.compareTo("Time-In",RWCString::ignoreCase) )
                    {//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                        int priority = 11;
                        RWCString controlString = "control terminate";
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                        {
                            controlString = "control xcom terminate";
                        }
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Sending terminate command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                        }
                        CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        RWDBDateTime nowPlusRandom = RWDBDateTime();
                        nowPlusRandom.addMinutes(currentGearObject->getMethodPeriod()/60/2);
                        currentLMGroup->setControlCompleteTime(nowPlusRandom);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) ||
                         !tempControlMethod.compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) ||
                         !tempControlMethod.compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ||
                         !tempControlMethod.compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
                {
                    if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RestoreStopType,RWCString::ignoreCase) )
                    {
                        int priority = 11;
                        RWCString controlString = "control restore";
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                        }
                        CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        multiPilMsg->insert( requestMsg );
                        setLastControlSent(RWDBDateTime());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
                        RWDBDateTime nowPlusRandom = RWDBDateTime();
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EMETCON )
                        {
                            nowPlusRandom.addMinutes(2);
                        }
                        else
                        {
                            nowPlusRandom.addMinutes(1);
                        }
                        currentLMGroup->setControlCompleteTime(nowPlusRandom);
                    }
                    else if( !tempMethodStopType.compareTo(CtiLMProgramDirectGear::TimeInStopType,RWCString::ignoreCase) || !tempMethodStopType.compareTo("Time-In",RWCString::ignoreCase) )
                    {
                        //"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                        //CtiLockGuard<CtiLogger> logger_guard(dout);
                        //dout << RWTime() << " - Stopping control on LM Group: " << currentLMGroup->getPAOName() << " with Stop Type of (Time In) in: " << __FILE__ << " at:" << __LINE__ << endl;
                        //I don't know if I should do anything unique here yet?
                        //multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), "control terminate"));
                        //setLastControlSent(RWDBDateTime());
                        //currentLMGroup->setLastControlSent(RWDBDateTime());
                        RWDBDateTime timeToTimeIn = RWDBDateTime(1990,1,1,0,0,0,0);//put in a bogus time stamp
                        if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) )
                        {
                            timeToTimeIn = currentLMGroup->getLastControlSent();
                            LONG offTimeInSeconds = currentGearObject->getMethodPeriod() * (currentGearObject->getMethodRate() / 100.0);
                            timeToTimeIn.addMinutes(offTimeInSeconds/60);
                        }
                        else if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::ThermostatSetbackMethod,RWCString::ignoreCase) )
                        {
                            timeToTimeIn = currentLMGroup->getLastControlSent();
                            CtiLMProgramThermoStatGear* thermostatGear = (CtiLMProgramThermoStatGear*)currentGearObject;
                            int minutesToAdd = 0;
                            minutesToAdd += (thermostatGear->getRandom()/2+thermostatGear->getRandom()%2);
                            minutesToAdd += thermostatGear->getValueTa();
                            minutesToAdd += thermostatGear->getValueTb();
                            minutesToAdd += thermostatGear->getValueTc();
                            minutesToAdd += thermostatGear->getValueTd();
                            minutesToAdd += thermostatGear->getValueTe();
                            minutesToAdd += thermostatGear->getValueTf();
                            timeToTimeIn.addMinutes(minutesToAdd);
                        }
                        else
                        {
                            timeToTimeIn = currentLMGroup->getLastControlSent();
                            timeToTimeIn.addMinutes(currentGearObject->getMethodPeriod()/60);
                        }
                        currentLMGroup->setControlCompleteTime(timeToTimeIn);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Invalid current gear method stop type: " << tempMethodStopType << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else if( !tempControlMethod.compareTo(CtiLMProgramDirectGear::LatchingMethod,RWCString::ignoreCase) )
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
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::NoControlMethod,RWCString::ignoreCase) )
                {
                    // Its not controlling so a stop method doesn't much matter, does it?
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Invalid current gear control method: " << tempControlMethod << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                currentLMGroup->setControlStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
            }
            else
            {
                currentLMGroup->setGroupControlState(CtiLMGroupBase::InactivePendingState);
                returnBool = FALSE;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Group cannot be set inactive yet because of minimum active time Group: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }

        if( returnBool )
        {
            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            setCurrentGearNumber(0);
            setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime());
        }
    }
    else
    {
        returnBool = FALSE;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid current gear number: " << _currentgearnumber << " in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBool;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the direct program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
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
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime());
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ManualActiveState )
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
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime());
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
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
            setDirectStopTime(RWDBDateTime());
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
        }
        stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
        setManualControlReceivedFlag(FALSE);

        setReductionTotal(0.0);
        setProgramState(CtiLMProgramBase::InactiveState);
        setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
        setDirectStopTime(RWDBDateTime());
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
    handleTimedControl

    Handles timed control
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::handleTimedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
	getProgramState() == CtiLMProgramBase::ScheduledState )
    {
	// don't do any timed control while this program is manually active
	return FALSE;
    }
    
    // Have we entered or left a control window?
    bool isReady = isReadyForTimedControl(secondsFromBeginningOfDay);
    
    if( getProgramState() == CtiLMProgramBase::InactiveState )
    {
	if(isReady)
	{
	    string text = "Timed Start, LM Program: ";
	    text += getPAOName();
	    string additional = "";
	    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
	    signal->setSOE(2);

	    multiDispatchMsg->insert(signal);
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " - " << text << ", " << additional << endl;
        }
	manualReduceProgramLoad(multiPilMsg,multiDispatchMsg);
	setProgramState(CtiLMProgramBase::TimedActiveState);
	
	CtiLMProgramControlWindow* controlWindow = getControlWindow(secondsFromBeginningOfDay);
	assert(controlWindow != NULL); //If we are not in a control window then we shouldn't be starting!
	RWDBDateTime startTime(RWTime((unsigned long)secondsFrom1901));
	RWDBDateTime endTime(RWTime((unsigned long) secondsFrom1901 + (controlWindow->getAvailableStopTime() - controlWindow->getAvailableStartTime())));

	incrementDailyOps();
        setDirectStartTime(startTime);
	setDirectStopTime(endTime);
	return TRUE;
	}
	else
	{
	    return FALSE;
	}
	
    }
    else if( getProgramState() == CtiLMProgramBase::TimedActiveState )
    {
	if(isReady)
	{
	    string text = "Timed Stop, LM Program: ";
	    text += getPAOName();
	    string additional = "";
	    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
	    signal->setSOE(2);

	    multiDispatchMsg->insert(signal);
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " - " << text << ", " << additional << endl;
        }
	setProgramState(CtiLMProgramBase::StoppingState); //are we settings thse to make stopprogramcontrol work correctly?
	//otherwise i don't see why
	stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);

	setReductionTotal(0.0); //is this resetting dynamic info?
	setProgramState(CtiLMProgramBase::InactiveState);
	setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
	return TRUE;
	}
	else
	{
	    return refreshStandardProgramControl(secondsFrom1901, multiPilMsg, multiDispatchMsg);
	}
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " **Checkpoint** " << "Invalid timed control state: " << getProgramState()
	         << " " << __FILE__ << "(" << __LINE__ << ")" << endl;
	}
	return FALSE;
    }
}

/*---------------------------------------------------------------------------
    isTimedControlReady

    Returns true if some action due to timed control should be taken.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::isReadyForTimedControl(LONG secondsFromBeginningOfDay)
{   
    // If the program IS in a control window and NOT started, then we are ready
    // to start the program
    // If the program IS NOT in a control window and IS started, then we are ready
    // to stop the program
        
    if(getControlType() == CtiLMProgramBase::TimedType && !getDisableFlag())
    {
	CtiLMProgramControlWindow* controlWindow = getControlWindow(secondsFromBeginningOfDay);
	if(controlWindow != NULL)
	{
   	    return (getProgramState() == CtiLMProgramBase::InactiveState);
	}
	else
	{
	    return (getProgramState() == CtiLMProgramBase::TimedActiveState);

	}
    }
    return FALSE; 
}

/*---------------------------------------------------------------------------
    hasControlHoursAvailable()

    Returns boolean if groups in this program are below the max hours
    daily/monthly/seasonal/annually.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasControlHoursAvailable() const
{


    BOOL returnBoolean = FALSE;

    if( _lmprogramdirectgroups.entries() > 0 )
    {
        for(LONG i=0;i<_lmprogramdirectgroups.entries();i++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)_lmprogramdirectgroups[i];

            // As soon as we find a group with hours available we know that the entire program
            // has at least some hours available
            if( doesGroupHaveAmpleControlTime(currentLMGroup,0) &&
                currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState &&
                !currentLMGroup->getDisableFlag() &&
                !currentLMGroup->getControlInhibit() )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPastMinRestartTime

    Returns a BOOLean if the control area can be controlled more because the
    time since the last control is at least as long as the min response time.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::isPastMinRestartTime(ULONG secondsFrom1901)
{
    BOOL returnBoolean = TRUE;

    if( getPAOType() == TYPE_LMPROGRAM_DIRECT && getMinRestartTime() > 0 )
    {
        RWOrdered& programGroups = getLMProgramDirectGroups();

        for(LONG j=0;j<programGroups.entries();j++)
        {
            CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)programGroups[j];
            if( currentLMGroup->getControlCompleteTime().seconds() + getMinRestartTime() > secondsFrom1901 )
            {
                returnBoolean = FALSE;
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
BOOL CtiLMProgramDirect::doesGroupHaveAmpleControlTime(CtiLMGroupBase* currentLMGroup, LONG estimatedControlTimeInSeconds) const
{
    return !( (getMaxHoursDaily() > 0 && (currentLMGroup->getCurrentHoursDaily() + estimatedControlTimeInSeconds) > (getMaxHoursDaily()*3600)) ||
              (getMaxHoursMonthly() > 0 && (currentLMGroup->getCurrentHoursMonthly() + estimatedControlTimeInSeconds) > (getMaxHoursMonthly()*3600)) ||
              (getMaxHoursSeasonal() > 0 && (currentLMGroup->getCurrentHoursSeasonal() + estimatedControlTimeInSeconds) > (getMaxHoursSeasonal()*3600)) ||
              (getMaxHoursAnnually() > 0 && (currentLMGroup->getCurrentHoursAnnually() + estimatedControlTimeInSeconds) > (getMaxHoursAnnually()*3600)) );
}

/*-------------------------------------------------------------------------
    calculateGroupControlTimeLeft

    .
--------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::calculateGroupControlTimeLeft(CtiLMGroupBase* currentLMGroup, LONG estimatedControlTimeInSeconds) const
{
    LONG returnTimeLeft = estimatedControlTimeInSeconds;

    if( getMaxHoursDaily() > 0 && (currentLMGroup->getCurrentHoursDaily() + estimatedControlTimeInSeconds) > (getMaxHoursDaily()*3600) )
    {
        LONG tempTimeLeft = (getMaxHoursDaily()*3600) - currentLMGroup->getCurrentHoursDaily();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursMonthly() > 0 && (currentLMGroup->getCurrentHoursMonthly() + estimatedControlTimeInSeconds) > (getMaxHoursMonthly()*3600) )
    {
        LONG tempTimeLeft = (getMaxHoursMonthly()*3600) - currentLMGroup->getCurrentHoursMonthly();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursSeasonal() > 0 && (currentLMGroup->getCurrentHoursSeasonal() + estimatedControlTimeInSeconds) > (getMaxHoursSeasonal()*3600) )
    {
        LONG tempTimeLeft = (getMaxHoursSeasonal()*3600) - currentLMGroup->getCurrentHoursSeasonal();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursAnnually() > 0 && (currentLMGroup->getCurrentHoursAnnually() + estimatedControlTimeInSeconds) > (getMaxHoursAnnually()*3600) )
    {
        LONG tempTimeLeft = (getMaxHoursAnnually()*3600) - currentLMGroup->getCurrentHoursAnnually();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }

    if( returnTimeLeft < 0 )
    {
        returnTimeLeft = 0;
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


    if( this != &right )
    {
        CtiLMProgramBase::operator=(right);
        _currentgearnumber = right._currentgearnumber;
        _lastgroupcontrolled = right._lastgroupcontrolled;
        _directstarttime = right._directstarttime;
        _directstoptime = right._directstoptime;

        _lmprogramdirectgears.clearAndDestroy();
        for(LONG i=0;i<right._lmprogramdirectgears.entries();i++)
        {
            _lmprogramdirectgears.insert(((CtiLMProgramDirectGear*)right._lmprogramdirectgears[i])->replicate());
        }

        _lmprogramdirectgroups.clearAndDestroy();
        for(LONG j=0;j<right._lmprogramdirectgroups.entries();j++)
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

    return CtiLMProgramBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramDirect::operator!=(const CtiLMProgramDirect& right) const
{

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
    CtiLMProgramBase::restore(rdr);

    RWDBNullIndicator isNull;
    _insertDynamicDataFlag = FALSE;

    rdr["currentgearnumber"] >> isNull;
    if( !isNull )
    {
        rdr["currentgearnumber"] >> _currentgearnumber;
        rdr["lastgroupcontrolled"] >> _lastgroupcontrolled;
	rdr["dailyops"] >> _dailyops;
        rdr["starttime"] >> _directstarttime;
        rdr["stoptime"] >> _directstoptime;
	rdr["timestamp"] >> _dynamictimestamp;

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        setCurrentGearNumber(0);
        setLastGroupControlled(0);
	resetDailyOps();
        setDirectStartTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setDirectStopTime(RWDBDateTime(1990,1,1,0,0,0,0));
	_dynamictimestamp = RWDBDateTime(1990,1,1,0,0,0,0);
        _insertDynamicDataFlag = TRUE;
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this direct program.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
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
                << dynamicLMProgramDirectTable["timestamp"].assign((RWDBDateTime)currentDateTime)
		<< dynamicLMProgramDirectTable["dailyops"].assign(getDailyOps());
                updater.where(dynamicLMProgramDirectTable["deviceid"]==getPAOId());//will be paobjectid

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }

                updater.execute( conn );
		_dynamictimestamp = currentDateTime;
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
                << currentDateTime
		<< getDailyOps();
		
                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );
		_dynamictimestamp = currentDateTime;
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

/*
 * estimateOffTime
 * Attempts to estimate how much time the groups in this program will be off given a period of time
 * that the program would be active.  
 */

ULONG CtiLMProgramDirect::estimateOffTime(ULONG proposed_gear, ULONG start, ULONG stop) 
{
    RWOrdered lm_gears = getLMProgramDirectGears();
    CtiLMProgramDirectGear* cur_gear = (CtiLMProgramDirectGear*) lm_gears[proposed_gear];
    
    string method = cur_gear->getControlMethod();
    long control_time = start - stop;

    if(method == CtiLMProgramDirectGear::TimeRefreshMethod.data())
    {
	return control_time;
    }
    else if(method == CtiLMProgramDirectGear::MasterCycleMethod.data())
    {
	return control_time * (double) cur_gear->getMethodRate()/100.0;
    }
    else if(method == CtiLMProgramDirectGear::TrueCycleMethod.data() ||
	    method == CtiLMProgramDirectGear::SmartCycleMethod.data())
    {
	return control_time * (double) cur_gear->getMethodRate()/100.0;//	getMethodRateCount()
    }
    else if(method == CtiLMProgramDirectGear::RotationMethod.data())
    {
	long send_rate = cur_gear->getMethodRate();
	long shed_time = cur_gear->getMethodPeriod();
	long number_of_groups = getLMProgramDirectGroups().entries();
	long number_of_groups_to_take = cur_gear->getMethodRateCount();

	long implied_period = (number_of_groups / number_of_groups_to_take) * send_rate;
	double implied_percent = (double) shed_time / (double) implied_period;

	return control_time * implied_percent;
    }
    else
    {
    {
	CtiLockGuard<CtiLogger> dout_guard(dout);
	dout << RWTime() << " **Checkpoint** " << "invalid gear type" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    }

       
    return 0;
}

// Static Members
int CtiLMProgramDirect::defaultLMStartPriority = 13;
int CtiLMProgramDirect::defaultLMRefreshPriority = 11;

