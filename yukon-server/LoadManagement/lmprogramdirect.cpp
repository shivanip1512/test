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
#include "yukon.h"

#include <algorithm>

#include "dbaccess.h"
#include "rwutil.h"
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
#include "msg_email.h"
#include "msg_notif_lmcontrol.h"
#include "lmcontrolareatrigger.h"
#include "lmprogramthermostatgear.h"
#include "lmprogramcontrolwindow.h"
#include "lmconstraint.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramDirect, CTILMPROGRAMDIRECT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramDirect::CtiLMProgramDirect() :
    _notify_active_time(gInvalidRWDBDateTime),
    _notify_inactive_time(gInvalidRWDBDateTime),    
    _startedrampingout(gInvalidRWDBDateTime),
    _announced_constraint_violation(false)

{
}

CtiLMProgramDirect::CtiLMProgramDirect(RWDBReader& rdr) :
    _notify_active_time(gInvalidRWDBDateTime),
    _notify_inactive_time(gInvalidRWDBDateTime),    
    _startedrampingout(gInvalidRWDBDateTime),
        _announced_constraint_violation(false)
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
//    _lmprogramdirectgroups.clearAndDestroy();
}

/*----------------------------------------------------------------------------
  getNotifyActiveOffset

  Returns the program active notification offset in seconds
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getNotifyActiveOffset() const
{
    return _notify_active_offset;
}

/*----------------------------------------------------------------------------
  getNotifyInactiveOffset

  Returns the program inactive notification offset in seconds
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getNotifyInactiveOffset() const
{
    return _notify_inactive_offset;
}

/*----------------------------------------------------------------------------
  getMessageSubject
  Returns the subject of the notification messages sent by this program
----------------------------------------------------------------------------*/
const string& CtiLMProgramDirect::getMessageSubject() const
{
    return _message_subject;
}

/*----------------------------------------------------------------------------
  getMessageHeader
  Returns the header of the notification messages sent by this program
----------------------------------------------------------------------------*/
const string& CtiLMProgramDirect::getMessageHeader() const
{
    return _message_header;
}

/*----------------------------------------------------------------------------
  getMessageFooter
  Returns the footer of the notification messages sent by this program
  ----------------------------------------------------------------------------*/
const string& CtiLMProgramDirect::getMessageFooter() const
{
    return _message_footer;
}

/*-----------------------------------------------------------------------------
  getTriggerOffset
  Returns the trigger offset to use in determining this programs
  trigger threshold offset
-----------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getTriggerOffset() const
{
    return _trigger_offset;
}

/*-----------------------------------------------------------------------------
  getTriggerRestoreOffset
  Returns the trigger offset to use in determining this programs
  trigger threshold offset
-----------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getTriggerRestoreOffset() const
{
    return _trigger_restore_offset;
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
/*LONG CtiLMProgramDirect::getDailyOps() 
{
    // If we haven't written out dynamic data today
    RWDate today;
    if(today > _dynamictimestamp.rwdate())
    {
        resetDailyOps();
    }
    
    return _dailyops; //TODO: check if this is the value for today!
}*/

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

/*----------------------------------------------------------------------------
  getNotifyActiveTime

  Returns the program active notify time
----------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getNotifyActiveTime() const
{
    return _notify_active_time;
}

/*----------------------------------------------------------------------------
  getNotifyInactiveTime

  Returns the program inactive notify time
----------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getNotifyInactiveTime() const
{
    return _notify_inactive_time;
}

/*----------------------------------------------------------------------------
  getStartedRampingOutTime

  Returns the time this progrma started to ramp out
----------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramDirect::getStartedRampingOutTime() const
{
    return _startedrampingout;
}

/*----------------------------------------------------------------------------
  getIsRampingIn

  Returns whether the program is currently ramping in
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::getIsRampingIn() 
{
    CtiLMProgramDirectGear* gear = getCurrentGearObject();
    if(gear == NULL)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "CtiLMProgramDirect::getIsRampingIn() - no current gear found!?" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    if(gear->getRampInInterval() == 0 || gear->getRampInPercent() == 0)
    {
	// The current gear doesn't ramp in, no way we can be ramping in
	return false;
    }

    // OK, the gear has ramp in set up, are any of our groups
    // actualy ramping in?
    CtiLMGroupVec& groups  = getLMProgramDirectGroups();
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr currentLMGroup  = *i;
        if(currentLMGroup->getIsRampingIn())
        {
            return true;
        }
    }
    return false;
}

/*----------------------------------------------------------------------------
  getIsRampingOut

  Returns whether the program is currently ramping out
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::getIsRampingOut() 
{
    CtiLMProgramDirectGear* gear = getCurrentGearObject();
    if(gear == NULL)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "CtiLMProgramDirect::getIsRampingOut() - no current gear found!?" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    const RWCString& stop_type = gear->getMethodStopType();
    if( !(stop_type == CtiLMProgramDirectGear::RampOutRandomStopType ||
          stop_type == CtiLMProgramDirectGear::RampOutFIFOStopType ||
          stop_type == CtiLMProgramDirectGear::RampOutRandomRestoreStopType ||
          stop_type == CtiLMProgramDirectGear::RampOutFIFORestoreStopType) )
    {
	// The current gear doesn't ramp out, no way we can be ramping out
	return false;
    }

    // OK, the gear has ramp out set up, are any of our groups
    // actualy ramping out?
    CtiLMGroupVec& groups  = getLMProgramDirectGroups();
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr currentLMGroup  = *i;
        if(currentLMGroup->getIsRampingOut())
        {
            return true;
        }
    }
    return false;
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

    Returns the a vector of pointers to this programs groups
---------------------------------------------------------------------------*/
CtiLMGroupVec& CtiLMProgramDirect::getLMProgramDirectGroups()
{
    return _lmprogramdirectgroups;
}

/*----------------------------------------------------------------------------
  getMasterPrograms

  Returns a set of pointers to the programs that this program is considered
  subordinate to.
----------------------------------------------------------------------------*/
set<CtiLMProgramDirect*>& CtiLMProgramDirect::getMasterPrograms() 
{
    return _master_programs;
}

/*----------------------------------------------------------------------------
  getSubordinateProgrmas

  Returns a set of pointers to this programs subordinate programs.
----------------------------------------------------------------------------*/
set<CtiLMProgramDirect*>& CtiLMProgramDirect::getSubordinatePrograms() 
{
    return _subordinate_programs;
}

/*----------------------------------------------------------------------------
  getNotificationGroups

  Returns a vector of notification group ids
----------------------------------------------------------------------------*/
vector<int>& CtiLMProgramDirect::getNotificationGroupIDs() 
{
    return _notificationgroupids;
}

/*----------------------------------------------------------------------------
  setMessageSubject
  Sets the subject for notifications that are sent out
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setMessageSubject(const string& subject)
{
    _message_subject = subject;
    return *this;
}

/*----------------------------------------------------------------------------
  setMessageHeader
  Sets the header for the notifications that are sent out
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setMessageHeader(const string& header)
{
    _message_header = header;
    return *this;
}

/*----------------------------------------------------------------------------
  setMessageFooter
  Sets the footer for the notifications that are sent out
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setMessageFooter(const string& footer)
{
    _message_footer = footer;
    return *this;
}

/*-----------------------------------------------------------------------------
  setTriggerOffset
  Sets the trigger offset to use in determining this programs trigger
  threshold offset
-----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setTriggerOffset(LONG trigger_offset)
{
    _trigger_offset = trigger_offset;
    return *this;
}

/*-----------------------------------------------------------------------------
  setTriggerRestoreOffset
  Sets the trigger offset to use in determining this programs trigger
  threshold restore offset
-----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setTriggerRestoreOffset(LONG restore_offset)
{
    _trigger_restore_offset = restore_offset;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentGearNumber

    Sets the number of the current direct program gear starts at 0 and
    goes upto _lmprogramdirectgears.entries()-1
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setCurrentGearNumber(LONG currentgear)
{
    if(_currentgearnumber != currentgear)
    {
        _currentgearnumber = currentgear;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setLastGroupControlled

    Sets the device id of the last lm group that was controlled in the
    program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setLastGroupControlled(LONG lastcontrolled)
{
    if(_lastgroupcontrolled != lastcontrolled)
    {
        _lastgroupcontrolled = lastcontrolled;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
  incrementDailyOps

  Increments this programs daily operations count by 1
  ---------------------------------------------------------------------------*/  
/*CtiLMProgramDirect& CtiLMProgramDirect::incrementDailyOps()
{
    _dailyops++;
    setDirty(true);
    return *this;
}
*/
/*---------------------------------------------------------------------------
  resetDailyOps

  Resets this programs daily operations count to 0
  ---------------------------------------------------------------------------*/
/*CtiLMProgramDirect& CtiLMProgramDirect::resetDailyOps()
{
    _dailyops = 0;
    return *this;
}
*/
/*---------------------------------------------------------------------------
    setDirectStartTime

    Sets the direct start time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStartTime(const RWDBDateTime& start)
{
    if(_directstarttime != start)
    {
        _directstarttime = start;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setDirectStopTime

    Sets the direct stop time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStopTime(const RWDBDateTime& stop)
{
    if(_directstoptime != stop)
    {
        _directstoptime = stop;
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setNotifyActiveTime

  Sets the program notify active time
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setNotifyActiveTime(const RWDBDateTime& notify)
{
    if(_notify_active_time != notify)
    {
        _notify_active_time = notify;
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setNotifyInactiveTime

  Sets the program inactive notify time
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setNotifyInactiveTime(const RWDBDateTime& notify)
{
    if(_notify_inactive_time != notify)
    {
        _notify_inactive_time = notify;
        setDirty(true);
    }
    return *this;
}

CtiLMProgramDirect& CtiLMProgramDirect::setStartedRampingOutTime(const RWDBDateTime& startedrampingout)
{
    if(_startedrampingout != startedrampingout)
    {
        _startedrampingout = startedrampingout;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the direct program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded)
{
    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.entries() > 0 && _lmprogramdirectgroups.size() > 0 )
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
//                    incrementDailyOps();
                    setDirectStartTime(RWDBDateTime());
                    setDirectStopTime(gInvalidRWDBDateTime);
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
                    // Dont let subordinate programs keep running
                    stopSubordinatePrograms(multiPilMsg, multiDispatchMsg, secondsFromBeginningOfDay);
                }

                if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
                {
                    LONG refreshRate = currentGearObject->getMethodRate();
                    LONG shedTime = currentGearObject->getMethodPeriod();
                    LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                    RWCString refreshCountDownType = currentGearObject->getMethodOptionType();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.size();
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
                        CtiLMGroupPtr& currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup.get() != NULL )
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
			    currentLMGroup->incrementDailyOps();
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            //Set this group to refresh 
                            currentLMGroup->setNextControlTime(RWDBDateTime(RWTime(secondsFrom1901+refreshRate)));
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
                        for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                        {
                            CtiLMGroupPtr currentLMGroup = *i;
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

                    
                    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;

                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() &&
			    !hasGroupExceededMaxDailyOps(currentLMGroup) )
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
			    currentLMGroup->incrementDailyOps();
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
                    ResetGroups();
                    expectedLoadReduced = StartMasterCycle(secondsFrom1901, currentGearObject);
                }
                else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
                {
                    LONG sendRate = currentGearObject->getMethodRate();
                    LONG shedTime = currentGearObject->getMethodPeriod();
                    LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.size();
                    }

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                    }
                    for(LONG i=0;i<numberOfGroupsToTake;i++)
                    {
                        CtiLMGroupPtr& currentLMGroup = findGroupToTake(currentGearObject);
                        if( currentLMGroup.get() != NULL )
                        {
                            CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
			    currentLMGroup->incrementDailyOps();
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

                    
                    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() &&
			    !hasGroupExceededMaxDailyOps(currentLMGroup) )
                        {
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                            {
                                multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( ((CtiLMGroupPoint*)currentLMGroup.get())->getStartControlRawState(), defaultLMStartPriority ) );
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

                    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() &&
			    !hasGroupExceededMaxDailyOps(currentLMGroup) )
                        {
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                            {
                                CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(settings, minValue, maxValue, valueB, valueD, valueF, random, valueTA, valueTB, valueTC, valueTD, valueTE, valueTF, defaultLMStartPriority);
                                currentLMGroup->setLastControlString(requestMsg->CommandString());
				currentLMGroup->incrementDailyOps();
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

    // Did we activate any groups??  If not set ourself inactive
    // This is kinda ugly, but if we didn't actually activate a group then we really aren't active.
    // The problem is that this function kinda assumes we can take load when that isn't necessarily
    // the case... if the caller would do some checking first then maybe we wouldn't have to do this...?
    bool found_active_group = false;
    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
	CtiLMGroupPtr currentLMGroup  = *i;
	if(currentLMGroup->getGroupControlState() != CtiLMGroupBase::InactiveState)
	{
	    found_active_group = true;
	    break;
	}
    }

    if(!found_active_group)
    {
	setProgramState(CtiLMGroupBase::InactiveState);
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

    if( _lmprogramdirectgears.entries() > 0 && _lmprogramdirectgroups.size() > 0)
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.entries() )
        {
            currentGearObject = getCurrentGearObject();

            if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
            {
                bool do_ramp = (currentGearObject->getRampInPercent() > 0);
                unsigned long secondsFrom1901 = RWDBDateTime().seconds();
                ResetGroups();
                if(do_ramp)
                {
                    RampInGroups(secondsFrom1901);
                }
                else
                {   //FIGURE OUT REGULAR TIME REFRESH
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
                    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )//&&
//			    !hasGroupExceededMaxDailyOps(currentLMGroup) )
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
			    currentLMGroup->incrementDailyOps();
                            multiPilMsg->insert( requestMsg );
                            setLastControlSent(RWDBDateTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setControlStartTime(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            //Set this group to refresh again
                            currentLMGroup->setNextControlTime(RWDBDateTime(RWTime(secondsFrom1901+refreshRate)));
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )//&&
//			!hasGroupExceededMaxDailyOps(currentLMGroup) )
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
			currentLMGroup->incrementDailyOps();
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
            {  //NOTE: add ramp in logic
                ResetGroups();
                StartMasterCycle(RWDBDateTime().seconds(), currentGearObject);
            }
            else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) )
            {
                LONG sendRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.size();
                }

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                }
                for(LONG i=0;i<numberOfGroupsToTake;i++)
                {
                    CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup.get() != NULL )
                    {
                        CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
			currentLMGroup->incrementDailyOps();
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )//&&
//			!hasGroupExceededMaxDailyOps(currentLMGroup) )
                    {
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( ((CtiLMGroupPoint*)currentLMGroup.get())->getStartControlRawState(), defaultLMStartPriority ) );
                        }
                        else
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg(gearStartRawState, defaultLMStartPriority) );
                        }
                        setLastControlSent(RWDBDateTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(RWDBDateTime());
			currentLMGroup->incrementDailyOps();
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )//&&
//			!hasGroupExceededMaxDailyOps(currentLMGroup) )
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
			    CtiLMProgramDirectGear* prevGearObject = 0;
                            requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, defaultLMStartPriority);
			    if(_currentgearnumber > 0)
			    {
				prevGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];
			    }
         
			{
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << " : " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        currentLMGroup->setLastControlString(requestMsg->CommandString());
			currentLMGroup->incrementDailyOps();
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )//&&
//			!hasGroupExceededMaxDailyOps(currentLMGroup) )
                    {
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_EXPRESSCOM )
                        {
                            CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(settings, minValue, maxValue, valueB, valueD, valueF, random, valueTA, valueTB, valueTC, valueTD, valueTE, valueTF, defaultLMStartPriority);
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
			    currentLMGroup->incrementDailyOps();
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
CtiLMGroupPtr CtiLMProgramDirect::findGroupToTake(CtiLMProgramDirectGear* currentGearObject)
{
    CtiLMGroupPtr returnGroup;

    if( !currentGearObject->getGroupSelectionMethod().compareTo(CtiLMProgramDirectGear::LastControlledSelectionMethod,RWCString::ignoreCase) )
    {
        BOOL found = FALSE;
        for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
        {
            CtiLMGroupPtr currentLMGroup  = *i;
            if( getLastGroupControlled() == currentLMGroup->getPAOId() )
            {
                currentLMGroup = (++i == _lmprogramdirectgroups.end()) ?
                _lmprogramdirectgroups[0] : *i;
             
                if( (getManualControlReceivedFlag() || (doesGroupHaveAmpleControlTime(currentLMGroup,1) && !hasGroupExceededMaxDailyOps(currentLMGroup))) &&
                    !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() &&
                    ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                      !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) ) )
                {
                    found = TRUE;
                    returnGroup = currentLMGroup;
                    returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                }
                break;
            }
        }

        if( !found )
        {   //Are any of the groups candidates?
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( (getManualControlReceivedFlag() || (doesGroupHaveAmpleControlTime(currentLMGroup,1) && !hasGroupExceededMaxDailyOps(currentLMGroup))) &&
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
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( (getManualControlReceivedFlag() || (doesGroupHaveAmpleControlTime(currentLMGroup,1) && !hasGroupExceededMaxDailyOps(currentLMGroup))) &&
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
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    atLeastOneActive = TRUE;
                    break;
                }
            }

            BOOL found = FALSE;
            if( atLeastOneActive )//is already active so take last group plus one
            {
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( getLastGroupControlled() == currentLMGroup->getPAOId() )
                    {
                        i++;
                        CtiLMGroupPtr currentLMGroup;
                        if( i == _lmprogramdirectgroups.end() )
                        {
                            currentLMGroup = *(_lmprogramdirectgroups.begin());
                        }
                        else
                        {
                            currentLMGroup = *i;
                        }

                        if( (getManualControlReceivedFlag() || (doesGroupHaveAmpleControlTime(currentLMGroup,1) && !hasGroupExceededMaxDailyOps(currentLMGroup))) &&
                            !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            found = TRUE;
                            returnGroup = currentLMGroup;
                            returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                        }

                        break;
                    }
                }
            }
            else//program inactive so pick the first group
            {
                CtiLMGroupPtr currentLMGroup = _lmprogramdirectgroups[0];
                if( (getManualControlReceivedFlag() || (doesGroupHaveAmpleControlTime(currentLMGroup,1) && !hasGroupExceededMaxDailyOps(currentLMGroup))) &&
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
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
        for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
        {
            CtiLMGroupPtr currentLMGroup  = *i;        
            if( (getManualControlReceivedFlag() || doesGroupHaveAmpleControlTime(currentLMGroup,1) ) &&
                !currentLMGroup->getDisableFlag() &&
                !currentLMGroup->getControlInhibit() &&
                currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState )
            {
                if( returnGroup.get() == NULL )
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

        if(returnGroup.get() != NULL)
        {
            // Mark the group so that it doesn't get picked again!
            returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
        }
    }

    return returnGroup;
}

CtiLMGroupPtr CtiLMProgramDirect::findGroupToRampOut(CtiLMProgramDirectGear* lm_gear)
{
    CtiLMGroupPtr lm_group;
    if(lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutFIFOStopType ||
       lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutFIFORestoreStopType)
    {
        //Find the group that started controlling first and isn't ramping out
        CtiLMGroupPtr temp_lm_group;
        RWDBDateTime first_control_time;
        for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
        {
            CtiLMGroupPtr temp_lm_group = *i;                
            if(temp_lm_group->getIsRampingOut() &&
               (lm_group.get() == 0 ||
               temp_lm_group->getControlStartTime().seconds() < lm_group->getControlStartTime().seconds()))
            {
                lm_group = temp_lm_group;
            }
        }
    }
    else if(lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutRandomStopType ||
            lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutRandomRestoreStopType)
    {
        CtiLMGroupPtr temp_lm_group;
        int num_groups = _lmprogramdirectgroups.size();
        int j = rand() % num_groups;
        int k = 0;
        do { //Look for a group that is not ramping out starting at a random index
            temp_lm_group = _lmprogramdirectgroups[(j+k)%num_groups];
        } while(!temp_lm_group->getIsRampingOut() && k++ <= num_groups);

        if(k > num_groups)
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " LMProgram: " << getPAOName() << " Couldn't find a group to ramp out, all the program's groups are already ramped out." << endl;
        }
        else
        {
            lm_group = temp_lm_group;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  "LMProgram: " << getPAOName() << " has an invalid StopOrderType, it is: " << lm_gear->getMethodStopType() << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    if(lm_group.get() != 0)
    {
        lm_group->setIsRampingOut(false);
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " LM Group: " << lm_group->getPAOName() << " has ramped out" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  " LMProgram: " << getPAOName() << " couldn't find a group to ramp out" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return lm_group;
}
/*---------------------------------------------------------------------------
    Hasgearchanged

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
        CtiLMProgramDirectGear* prevGearObject = 0;

        if(_currentgearnumber > 0)
        {
            prevGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];
        }

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

                if( isTriggerCheckNeeded )

                    if((trigger->getPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ||
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
                    else if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::MasterCycleMethod,RWCString::ignoreCase) &&
                             prevGearObject != 0 &&
                             (trigger->getPointValue() < (trigger->getThreshold() + prevGearObject->getChangeTriggerOffset()) ||
                              (trigger->getProjectedPointValue() < (trigger->getThreshold() + prevGearObject->getChangeTriggerOffset()) && trigger->getProjectedPointValue() > 0)))
                    {
                        _currentgearnumber--;

                        RWCString text = RWCString("Trigger Offset Gear Change Down Program: ");
                        text += getPAOName();
                        RWCString additional = RWCString("Previous Gear: ");
                        additional += currentGearObject->getGearName();
                        additional += " New Gear: ";
                        additional += prevGearObject->getGearName();
                        CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                        multiDispatchMsg->insert(signal);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << text << ", " << additional << endl;
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
    if( !isWithinValidControlWindow(secondsFromBeginningOfDay) )
    {
	if( _LM_DEBUG & LM_DEBUG_STANDARD )
	{
	    CtiLockGuard<CtiLogger> dout_guard(dout);
	    dout << RWTime() << " - LM Program: " << getPAOName() << " is no longer in a valid control window, stopping program control" << endl;
	}
        stopProgramControl(multiPilMsg, multiDispatchMsg, secondsFrom1901);
        returnBoolean = TRUE;	
    }
/*    else if(!hasExceededMaxActivateTime(secondsFrom1901))
    {
	if( _LM_DEBUG & LM_DEBUG_STANDARD )
	{
	    CtiLockGuard<CtiLogger> dout_guard(dout);
	    dout << RWTime() << " - LM Program: " << getPAOName() << " has exceeded its maximum activate time of " << getMaxActivateTime() << " seconds, stopping program control" << endl;
	}	
	stopProgramControl(multiPilMsg, multiDispatchMsg, secondsFrom1901);
	returnBoolean = TRUE;
	}*/
    else
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

    if( currentGearObject != NULL && previousGearObject != NULL && _lmprogramdirectgroups.size() > 0 )
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
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
                        //Set this group to refresh again
                        currentLMGroup->setNextControlTime(RWDBDateTime(RWTime(RWDBDateTime().seconds()+refreshRate)));
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
                    for(CtiLMGroupIter j = _lmprogramdirectgroups.begin(); j != _lmprogramdirectgroups.end(); j++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *j;
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
                    numberOfGroupsToTake = _lmprogramdirectgroups.size();
                }

                for(LONG i=0;i<numberOfGroupsToTake;i++)
                {
                    CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup.get() != NULL )
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
                        //Set this group to refresh again
                        currentLMGroup->setNextControlTime(RWDBDateTime(RWTime(RWDBDateTime().seconds()+refreshRate)));
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

                    for(CtiLMGroupIter j = _lmprogramdirectgroups.begin(); j != _lmprogramdirectgroups.end(); j++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *j;
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
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
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
            //ResetGroups();  
            //expectedLoadReduced = StartMasterCycle(R3WDBDateTime().seconds(), currentGearObject);
            expectedLoadReduced = 0.0;

            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr lm_group = *i;
                if( currentGearObject->getPercentReduction() > 0.0 )
                {
                    expectedLoadReduced  += (currentGearObject->getPercentReduction() / 100.0) * lm_group->getKWCapacity();
                }
                else
                {
                    expectedLoadReduced += lm_group->getKWCapacity() * (currentGearObject->getMethodRate() / 100.0);
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
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
            {   // Stop the groups from cycling, before we start doing rotation on them
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;                
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
            }
            
            // On a gear upshift from rotation to rotation we want to control some load immediately, however,
            // On a downshift, we want to do nothing but continue rotation, but with the new rotation settings.
            // Added for northern plains 11/05/04
            if( (!previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::RotationMethod,RWCString::ignoreCase) &&
                 getCurrentGearNumber() > previousGearNumber ) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::SmartCycleMethod,RWCString::ignoreCase) ||
                !previousGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TrueCycleMethod,RWCString::ignoreCase) )
            {
                // Get standard rotation going
                LONG sendRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.size();
                }

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake << endl;
                }
                for(LONG j=0;j<numberOfGroupsToTake;j++)
                {
                    CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup.get() != NULL )
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
        } // End switching to rotation gear
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
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
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
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;            
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

            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                RWDBDateTime now;
                if( now.seconds() >
                    currentLMGroup->getControlStartTime().seconds() + getMinActivateTime() ||
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
                            RWDBDateTime timeToTimeIn = gInvalidRWDBDateTime; //put in a bogus time stamp
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
                            multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( (((CtiLMGroupPoint*)currentLMGroup.get())->getStartControlRawState()?0:1), defaultLMRefreshPriority ) );
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
                    currentLMGroup->setControlStartTime(gInvalidRWDBDateTime);
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
BOOL CtiLMProgramDirect::stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupPtr& currentLMGroup, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
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

/*----------------------------------------------------------------------------
  notifyGroupsOfStart

  Let the notification groups know when we are going to start the program
  Returns true if a notifcation was sent.
----------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::notifyGroupsOfStart(CtiMultiMsg* multiNotifMsg)
{
    if( _LM_DEBUG & LM_DEBUG_DIRECT_NOTIFY )
    {
	CtiLockGuard<CtiLogger> logger_guard(dout);
	dout << RWTime() << " sending notification of direct program start. Program: " << getPAOName() << endl;
    }

    // If the notify time is longer than say a month from now then we must never
    // be stopping
    RWDBDateTime now;
    if(getNotifyActiveTime().seconds() > (now.seconds() + 60*60*24*30))
    {
	return notifyGroups(CtiNotifLMControlMsg::STARTING_NEVER_STOP, multiNotifMsg);	
    }
    else
    {
	return notifyGroups(CtiNotifLMControlMsg::STARTING, multiNotifMsg);
    }
}

/*----------------------------------------------------------------------------
  notifyGroupsOfStop

  Let the notification groups know when we are going to stop the program
  Returns true if a notifcation was sent.
----------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::notifyGroupsOfStop(CtiMultiMsg* multiNotifMsg)
{
    if( _LM_DEBUG & LM_DEBUG_DIRECT_NOTIFY )
    {
	CtiLockGuard<CtiLogger> logger_guard(dout);
	dout << RWTime() << " sending notification of direct program stop. Program: " << getPAOName() << endl;
    }

    return notifyGroups(CtiNotifLMControlMsg::FINISHING, multiNotifMsg);		
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

    if( currentGearObject != NULL && _lmprogramdirectgroups.size() > 0 )
    {
        
        if( !currentGearObject->getControlMethod().compareTo(CtiLMProgramDirectGear::TimeRefreshMethod,RWCString::ignoreCase) )
        {
            long refresh_rate = currentGearObject->getMethodRate();
            long shed_time = currentGearObject->getMethodPeriod();
            string refresh_count_down_type = currentGearObject->getMethodOptionType();
            long max_refresh_shed_time = currentGearObject->getMethodOptionMax();

            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr lm_group  = *i;            
                //Check active groups to see if they should stop being controlled
                if(lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                   !getManualControlReceivedFlag() && !doesGroupHaveAmpleControlTime(lm_group,0))
                {   
                    returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, lm_group, secondsFrom1901, multiPilMsg, multiDispatchMsg));
                }
                //Check to see if any groups are ready to be refreshed to ramped in
                else if( lm_group->getNextControlTime() > gInvalidRWDBDateTime &&
                         lm_group->getNextControlTime().seconds() <= secondsFrom1901 &&
                         (!getIsRampingOut() || (getIsRampingOut() && lm_group->getIsRampingOut()))) //if the program is ramping out, then only refresh if this group is stillr amping out
                {
                    if(refresh_count_down_type == CtiLMProgramDirectGear::CountDownMethodOptionType.data())
                    {
                        if( getManualControlReceivedFlag() )
                        {
                            if(max_refresh_shed_time > 0)
                            {   // Don't let this group control more than its max shed time
                                unsigned long calc_shed_time = getDirectStopTime().seconds() - RWDBDateTime().seconds();
                                shed_time = ( max_refresh_shed_time > 0 ?
                                              min((unsigned)max_refresh_shed_time, (unsigned)calc_shed_time) :
                                              calc_shed_time );
                                         
                            }
                            else
                            {   // Don't let the shed time span tomorrow (why?)
                                RWDBDateTime now;
                                RWDBDateTime tomorrow(now.year(), now.month(), now.dayOfMonth(),0,0,0,0);
                                tomorrow.addDays(1);
                                shed_time = min(tomorrow.seconds() - now.seconds(), getDirectStopTime().seconds() - now.seconds());
                            }
                        }

                        long estimated_control_time = shed_time;
                        if(!getManualControlReceivedFlag() && !doesGroupHaveAmpleControlTime(lm_group, estimated_control_time))
                        {
                            shed_time = calculateGroupControlTimeLeft(lm_group, estimated_control_time);
                        }
                    } //end countdownmethod
                    
                    CtiRequestMsg* requestMsg = lm_group->createTimeRefreshRequestMsg(refresh_rate, shed_time, defaultLMRefreshPriority);
                    lm_group->setLastControlString(requestMsg->CommandString());
                    multiPilMsg->insert( requestMsg );
                    //This setLastControlSent() is just to be used for new control sent
                    //setLastControlSent(RWDBDateTime());
                    //setLastGroupControlled(lm_group->getPAOId()); 
                    lm_group->setLastControlSent(RWDBDateTime());
                    lm_group->setIsRampingIn(false);
                    lm_group->setGroupControlState(CtiLMGroupBase::ActiveState);
                    //Set this group to refresh again
                    lm_group->setNextControlTime(RWDBDateTime(RWTime(secondsFrom1901+refresh_rate)));

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )                 
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << RWTime() << "LMProgram: " << getPAOName() << ",  time refresh " << lm_group->getPAOName() << " next at: " << lm_group->getNextControlTime().asString() << endl;
                    }
                    returnBoolean = TRUE;
                } //end refreshing or ramping in a group
                else if( lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
/*                         lm_group->getNextControlTime().seconds() < RWDBDateTime(1991,1,1,0,0,0,0).seconds()) */
                         lm_group->getNextControlTime() == gInvalidRWDBDateTime)
                { // This group is active but has no next control time! we need to give it one.
                    lm_group->setNextControlTime(RWDBDateTime(RWTime(lm_group->getLastControlSent().seconds() + refresh_rate)));
                    returnBoolean = TRUE;
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
                for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
                {
                    CtiLMGroupPtr currentLMGroup  = *i;                
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
                        {   // We only want to reduce the count when we know when control is ending
                            // this is only true with manual and timed control
                            if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                getProgramState() == CtiLMProgramBase::TimedActiveState )
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
                            if( !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                  getProgramState() == CtiLMProgramBase::TimedActiveState) &&
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
                            if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                getProgramState() == CtiLMProgramBase::TimedActiveState )                           
                            {
                                ULONG secondsAtLastControl = currentLMGroup->getLastControlSent().seconds();
                                if( (secondsAtLastControl + cycleLength) >= (getDirectStopTime().seconds()-120) )
                                {//if the last control sent is not within 2 minutes before the stop control time a refresh will be sent
                                    cycleCount = 0;
                                }
                            }

                            if( !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                  getProgramState() == CtiLMProgramBase::TimedActiveState) &&                       
                                !doesGroupHaveAmpleControlTime(currentLMGroup,estimatedControlTimeInSeconds) )
                            {
                                cycleCount = 0;
                            }
                        }
                        else if( !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                   getProgramState() == CtiLMProgramBase::TimedActiveState) &&                                                   
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
                            //setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(RWDBDateTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            if( !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                  getProgramState() == CtiLMProgramBase::TimedActiveState) &&                                               
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
                         !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                           getProgramState() == CtiLMProgramBase::TimedActiveState) &&                                                                  
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
                    if( numberOfActiveGroups == _lmprogramdirectgroups.size() )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                    else if( numberOfActiveGroups == 0 )
                    {
                        setProgramState(CtiLMProgramBase::InactiveState);
                        ResetGroups();
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
            int percent = currentGearObject->getMethodRate();
            int period = currentGearObject->getMethodPeriod();
            int off_time = period * (percent / 100.0);
            
            //anything ready to control? move this out of here when possible - to a main control loop?
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr lm_group  = *i;            
                LONG currentOffTime = lm_group->getControlCompleteTime().seconds() - lm_group->getLastControlSent().seconds();
                // For special group types we might need to give it a boost to achieve the correct control time
                if( lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                    lm_group->doesMasterCycleNeedToBeUpdated(secondsFrom1901, lm_group->getControlCompleteTime().seconds(), currentOffTime))
//                    if(lm_group->doesMasterCycleNeedToBeUpdated(secondsFrom1901, (lm_group->getLastControlSent().seconds()+off_time), off_time))
                {
                    //if it is a emetcon switch 450 (7.5 min) is correct
                    //ripple switches have a predetermined shed time
                    multiPilMsg->insert( lm_group->createMasterCycleRequestMsg(450, period, defaultLMRefreshPriority) );
                    returnBoolean = TRUE;
                }
                else
                {
                    if(lm_group->getNextControlTime().seconds() > gInvalidRWDBDateTimeSeconds &&
                       lm_group->getNextControlTime().seconds() < secondsFrom1901 &&
                       (!getIsRampingOut() || (getIsRampingOut() && lm_group->getIsRampingOut())))
                    { //ready to control
                        CtiRequestMsg* req_msg = lm_group->createMasterCycleRequestMsg(off_time, period, defaultLMRefreshPriority);
                        multiPilMsg->insert(req_msg);

                        lm_group->setLastControlString(req_msg->CommandString());
                        lm_group->setIsRampingIn(false);
                        lm_group->setGroupControlState(CtiLMGroupBase::ActiveState);
                    
                        setLastGroupControlled(lm_group->getPAOId());

                        RWDBDateTime now;
                        lm_group->setLastControlSent(now);
                        lm_group->setControlCompleteTime(now.addSeconds(off_time));
                        
                        if( getProgramState() != CtiLMProgramBase::ManualActiveState &&
                            getProgramState() != CtiLMProgramBase::TimedActiveState )
                        {
                            setProgramState(CtiLMProgramBase::FullyActiveState);
                        }

                        lm_group->setNextControlTime(RWDBDateTime(RWTime(lm_group->getNextControlTime().seconds() + period)));

                        if( _LM_DEBUG & LM_DEBUG_STANDARD )                     
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << RWTime() << "LMProgram: " << getPAOName() << ",  master cycle controlling " << lm_group->getPAOName() << " next at: " << lm_group->getNextControlTime().asString() << endl;
                        }

                        returnBoolean = TRUE;
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
                numberOfGroupsToTake = _lmprogramdirectgroups.size();
            }

            ULONG sendRateEndFrom1901 = 0;

            // First we need to update the state of the currently active rotating groups
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;
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
                    CtiLMGroupPtr nextLMGroupToTake = findGroupToTake(currentGearObject);
                    if( nextLMGroupToTake.get() != NULL )
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

/*----------------------------------------------------------------------------
  stopSubordinatePrograms

  Stops controlling any subordinate programs that may be active.
  Returns true if any programs were found active and stopped.
----------------------------------------------------------------------------*/  
bool CtiLMProgramDirect::stopSubordinatePrograms(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901)
{
    bool stopped_programs = false;
    set<CtiLMProgramDirect*>& sub_set = getSubordinatePrograms();

    for(set<CtiLMProgramDirect*>::iterator sub_iter = sub_set.begin();
        sub_iter != sub_set.end();
        sub_iter++)
    {
        if((*sub_iter)->getProgramState() != CtiLMProgramBase::InactiveState)
        {
            string text = "Stopping subordinate program: ";
            text += (*sub_iter)->getPAOName();
            string additional = "";
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
            signal->setSOE(2);
            multiDispatchMsg->insert(signal);
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " - " <<  text << endl;
        }
        stopped_programs = (*sub_iter)->stopProgramControl(multiPilMsg, multiDispatchMsg, secondsFrom1901) || stopped_programs;
        }
    }
    return stopped_programs;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by stopping all groups that are active.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901)
{
    BOOL returnBool = TRUE;
    bool is_ramping_out = false;

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL )
    {
        RWCString tempControlMethod = currentGearObject->getControlMethod();
        RWCString tempMethodStopType = currentGearObject->getMethodStopType();
        for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
        {
            CtiLMGroupPtr currentLMGroup  = *i;        
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
                        currentLMGroup->setNextControlTime(gInvalidRWDBDateTime);
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
                        RWDBDateTime timeToTimeIn = gInvalidRWDBDateTime;//put in a bogus time stamp
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
                    else if(  !currentLMGroup->getIsRampingOut() &&
                             (!tempMethodStopType.compareTo(CtiLMProgramDirectGear::RampOutRandomStopType,RWCString::ignoreCase) ||
                              !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RampOutFIFOStopType,RWCString::ignoreCase) ||
                              !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RampOutRandomRestoreStopType,RWCString::ignoreCase) ||
                              !tempMethodStopType.compareTo(CtiLMProgramDirectGear::RampOutFIFORestoreStopType,RWCString::ignoreCase)))
                   {
                       currentLMGroup->setIsRampingIn(false);
                       currentLMGroup->setIsRampingOut(true);
                       RWDBDateTime now = RWDBDateTime(RWTime(secondsFrom1901));
//NOTE: is this correct for the control complete time for the group?  does depend on type?
                       currentLMGroup->setControlCompleteTime( RWDBDateTime(RWTime(now.seconds() + (unsigned long) floor( 100.0 / (double)currentGearObject->getRampOutPercent()) * currentGearObject->getRampOutInterval() + 1.0)));
                       setStartedRampingOutTime(now);
                       is_ramping_out = true;

                       if( _LM_DEBUG & LM_DEBUG_STANDARD )
                       {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << RWTime() << " LMGroup: " << currentLMGroup->getPAOName() << " waiting to ramp out." << endl;
                       }
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
                        multiDispatchMsg->insert( currentLMGroup->createLatchingRequestMsg( (((CtiLMGroupPoint*)currentLMGroup.get())->getStartControlRawState()?0:1), defaultLMRefreshPriority ) );
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
            }
            else
            {
                currentLMGroup->setGroupControlState(CtiLMGroupBase::InactivePendingState);
                returnBool = FALSE;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Group cannot be set inactive yet because of minimum active time Group: " << currentLMGroup->getPAOName() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }

        if(returnBool)
        {
            setDirectStopTime(RWDBDateTime());
        }
        if( !is_ramping_out && returnBool )
        {
            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            ResetGroups();
            for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
            {
                CtiLMGroupPtr currentLMGroup  = *i;            
//Why set the state??  shouldn't we let dispatch update this via status point?
//                lm_group->setGroupControlState(CtiLMGroupBase::InactiveState);
            }
            setCurrentGearNumber(0);
            setStartedControlling(gInvalidRWDBDateTime);
            setManualControlReceivedFlag(FALSE);
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

/*----------------------------------------------------------------------------
  refreshRampOutProgramControl
  Only for master cycle ramp out right now
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::refreshRampOutProgramControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
//     we need to keep controlling any groups currently active until it is time for that
//      program to ramp out.  At that point we need to send a restore to it.

//          How often do we refresh a groups control?  Different types of groups can handle different shed times....
//                           for now just do one
    bool ret_val = false;
    
    RWDBDateTime now = RWDBDateTime(RWTime(secondsFrom1901));
    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;    
        if( lm_group->getIsRampingOut() )
        {
            if( now.seconds() >= lm_group->getControlCompleteTime().seconds())
            {
            int priority = 11;
            string ctrl_str = "control restore";
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Sending restore command (ramp out), LM Group: " << lm_group->getPAOName() << ", string: " << ctrl_str << ", priority: " << priority << endl;
            }
            CtiRequestMsg* requestMsg = new CtiRequestMsg(lm_group->getPAOId(), ctrl_str.data(),0,0,0,0,0,0,priority);
            lm_group->setLastControlString(requestMsg->CommandString());
            lm_group->setLastControlSent(now);
            lm_group->setControlCompleteTime(now);
            lm_group->setIsRampingOut(false);
            multiPilMsg->insert(requestMsg);

            ret_val = true;
            }
/*          else if(
            { Refresh groups that are ramping out?
            How often? how to know when?
            not so difficult with time refresh , use resend rate
            but with master cycle?
            }*/
       }
    }
    return ret_val;
}

/*----------------------------------------------------------------------------
  updateGroupsRampingOut

  Should only be called when the program is ramping out, checks to see if
  any groups should be done ramping out and sets them to not be ramping out.
  Retruns true if any groups were dirtied.
  Call getIsRampingOut after this if you want to know if any groups
  are still ramping out.
----------------------------------------------------------------------------*/  
bool CtiLMProgramDirect::updateGroupsRampingOut(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901)
{
    bool ret_val = false;
    int num_groups = _lmprogramdirectgroups.size();
    /* int total_ramp_time = (100.0 / (double) getCurrentGearObject()->getRampOutPercent()) * getCurrentGearObject()->getRampOutInterval();
    innt started_ramping_time = getControlCompleteTime().seconds() - total_ramp_time;
    int num_intervals = (secondsFrom1901 - started_ramping_time) / getCurrentGearObject()->getRampOutInterval();
    */
    CtiLMProgramDirectGear* lm_gear = getCurrentGearObject();
    long ramp_out_interval = lm_gear->getRampOutInterval();
    long ramp_out_percent = lm_gear->getRampOutPercent();

    if(ramp_out_interval == 0 || ramp_out_percent == 0)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "LMProgram: " << getPAOName() << " Gear: " << getCurrentGearObject()->getGearName() << " has a stop type of 'Ramp Out', but is has 0 for a ramp out interval or ramp out percent.  Please change this to something reasonable.  Cowardly doing nothing."  << __FILE__ << "(" << __LINE__ << ")" << endl;
        return false;
    }

    int cur_interval = (secondsFrom1901 - getStartedRampingOutTime().seconds()) / getCurrentGearObject()->getRampOutInterval()+1;
    int should_be_ramped_out = min((int) floor((((double)ramp_out_percent/100.0 * (double)cur_interval) * (double)num_groups) + 0.5), (int) num_groups);
    int num_ramped_out = 0;
    
    //determine how many are ramped out now
    for(CtiLMGroupIter n = _lmprogramdirectgroups.begin(); n != _lmprogramdirectgroups.end(); n++)
    {
        CtiLMGroupPtr lm_group  = *n;
        if(!lm_group->getIsRampingOut())
        {
            num_ramped_out++;
        }
    }
        
    int num_to_ramp_out = should_be_ramped_out - num_ramped_out;

    for(int i = 0; i < num_to_ramp_out; i++)
    {
        CtiLMGroupPtr lm_group = findGroupToRampOut(getCurrentGearObject());
        if(lm_group.get() != 0)
        {
            ret_val = true;
        }
                
        // Possibly restore the group now that it has ramped out
        if(lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutRandomRestoreStopType ||
           lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutFIFORestoreStopType)
        {
            int priority = 11;
            string ctrl_str = "control restore";
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Sending restore command, LM Group: " << lm_group->getPAOName() << ", string: " << ctrl_str << ", priority: " << priority << endl;
            }
            CtiRequestMsg* requestMsg = new CtiRequestMsg(lm_group->getPAOId(), ctrl_str.data(),0,0,0,0,0,0,priority);
            lm_group->setLastControlString(requestMsg->CommandString());
            multiPilMsg->insert( requestMsg );
            setLastControlSent(RWDBDateTime());
            lm_group->setLastControlSent(RWDBDateTime());
            RWDBDateTime nowPlusRandom = RWDBDateTime();
            if( lm_group->getPAOType() == TYPE_LMGROUP_EMETCON )
            {
                nowPlusRandom.addMinutes(2);
            }
            else
            {
                nowPlusRandom.addMinutes(1);
            }
            lm_group->setControlCompleteTime(nowPlusRandom);
        }
    }

    return ret_val;
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
            // are any of our master programs already running?  if so we can't start MASTERSLAVE - this is alreadya  constraint checked in executor
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

            // Dont let subordinate programs keep running
            stopSubordinatePrograms(multiPilMsg, multiDispatchMsg, secondsFrom1901);
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
            //          setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
//            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            //        setProgramState(CtiLMProgramBase::InactiveState);
            setStartedControlling(gInvalidRWDBDateTime);
            setDirectStopTime(RWDBDateTime());
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ManualActiveState )
    {
        if( secondsFrom1901 >= getDirectStopTime().seconds() && !getIsRampingOut() )
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
//            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
//            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
//            setProgramState(CtiLMProgramBase::InactiveState); //stopprogramcountrol should do this
            setStartedControlling(gInvalidRWDBDateTime);
            setDirectStopTime(RWDBDateTime());
        }
        else
        {
            if( refreshStandardProgramControl(secondsFrom1901, multiPilMsg, multiDispatchMsg) )
            {
                returnBoolean = TRUE;
            }
            if( getIsRampingOut()) 
            {   
                returnBoolean = updateGroupsRampingOut(multiPilMsg, multiDispatchMsg, secondsFrom1901);
                if(!getIsRampingOut())  // no longer ramping out?
                {
                    RWCString text = RWCString("Finshed Ramping Out, LM Program: ");
                    text += getPAOName();
                    RWCString additional = RWCString("");
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(2);

                    multiDispatchMsg->insert(signal);
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
//NOTE more ?
                    setProgramState(CtiLMProgramBase::InactiveState);
                    ResetGroups();
                    setManualControlReceivedFlag(FALSE);
                }
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
            } //NOTE: SHOULD WE BE SETTING THE STATE HERE?  COULD MESS UP RAMPOUT
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            ResetGroups();
            setStartedControlling(gInvalidRWDBDateTime);
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
//        setManualControlReceivedFlag(FALSE);

        setReductionTotal(0.0);
//        setProgramState(CtiLMProgramBase::InactiveState);
        setStartedControlling(gInvalidRWDBDateTime);
        setDirectStopTime(RWDBDateTime());
    }
    else if(!getIsRampingOut())
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
    if( (getDisableFlag() && CtiLMProgramBase::InactiveState) ||
        getProgramState() == CtiLMProgramBase::ManualActiveState ||
        getProgramState() == CtiLMProgramBase::ScheduledState )
    {
        // don't do any timed control while this program is manually active
        // or we are disabled and inactive
        return false;
    }

    bool ret_val = false;
    bool was_ramping_out = getIsRampingOut();
    bool timed_active = (getProgramState() == CtiLMProgramBase::TimedActiveState);
    
    if(was_ramping_out && timed_active) // only do this if we are active, another program might be ramping out our groups!!!
    {
        ret_val = updateGroupsRampingOut(multiPilMsg, multiDispatchMsg, secondsFrom1901); // consider if any groups have ramped out
    }
    
    bool in_control_window = (getControlWindow(secondsFromBeginningOfDay) != NULL);
    bool inactive = (getProgramState() == CtiLMProgramBase::InactiveState);
    bool disabled = getDisableFlag();
    bool is_ramping_out = getIsRampingOut();

    if(inactive)
    {
        if(in_control_window && !disabled) //do we need to do a timed start?
        {
            // timed start
            return (ret_val || startTimedProgram(secondsFrom1901, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg));
        }
        else
        {   // inactive and either not in a control window or disabled so nothin to do
            return ret_val;
        }
    } // end timed start
    else if(timed_active) //do we need to do a timed stop or just refresh?
    {
        if( (!in_control_window || disabled))
        {
            if(!is_ramping_out && was_ramping_out)  //we just finished ramping out!
            {
                string text = "Finished ramping out, LM Program: "; 
                text += getPAOName();
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),"",GeneralLogType,SignalEvent);
                multiDispatchMsg->insert(signal);
                
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << RWTime() << " - " <<  text << endl;
            }

                setProgramState(CtiLMProgramBase::InactiveState);
                ResetGroups();
                setManualControlReceivedFlag(FALSE);
                return true;
            } //end finshed ramping out
            else if(!is_ramping_out )
            {
                //timed stop
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

            stopProgramControl(multiPilMsg,multiDispatchMsg, secondsFrom1901);

            setReductionTotal(0.0); //is this resetting dynamic info?
            setStartedControlling(gInvalidRWDBDateTime);
            return true;
            } //end timed stop
        }
/*
  Lets say someone disabled this program and then enabled it again quick, while were are still
  in a control window, what do we do?
        else if(in_control_window && !disabled && is_ramping_out)  //ramping out, but we shouldn't be
        {
            return (ret_val || startTimedProgram(secondsFrom1901, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg));
        }
*/
        //refresh
        return (ret_val || refreshStandardProgramControl(secondsFrom1901, multiPilMsg, multiDispatchMsg));
        //end refresh
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "Invalid timed control state: " << getProgramState()
             << " " << __FILE__ << "(" << __LINE__ << ")" << endl;
        return ret_val;
    }
}

/*----------------------------------------------------------------------------
  startTimedProgram

  Start a timed program, check constraints first though.
  Returns true if the timed program actually starts.
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::startTimedProgram(unsigned long secondsFrom1901, long secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
            CtiLMConstraintChecker con_checker;
        
            CtiLMProgramControlWindow* controlWindow = getControlWindow(secondsFromBeginningOfDay);
            assert(controlWindow != NULL); //If we are not in a control window then we shouldn't be starting!
            RWDBDateTime startTime(RWTime((unsigned long)secondsFrom1901));
//            RWDBDateTime endTime(RWTime((unsigned long) secondsFrom1901 + (controlWindow->getAvailableStopTime() - controlWindow->getAvailableStartTime())));
            RWDBDateTime endTime(RWTime( ((unsigned long) secondsFrom1901 - (unsigned long) secondsFromBeginningOfDay)) + controlWindow->getAvailableStopTime());
            vector<string> cons_results;
            if(!con_checker.checkConstraints(*this, getCurrentGearNumber(), secondsFrom1901, startTime.seconds(), endTime.seconds(), cons_results))
            {
                if(!_announced_constraint_violation)
                {
                    string text = " LMProgram: ";
                    text += getPAOName();
                    text += ", a timed program, was scheduled to start but did not due to constraint violations";
                    string additional = "";
                    for(vector<string>::iterator iter = cons_results.begin(); iter != cons_results.end(); iter++)
                    {
                        additional += *iter;
                        additional += "\n";
                    }
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
                    signal->setSOE(2);
                    multiDispatchMsg->insert(signal);

                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << RWTime() << " - " <<  text << endl << additional << endl;
                }
                
                    _announced_constraint_violation = true;
                }
                return false;
            }
            else
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

//            incrementDailyOps();
            setDirectStartTime(startTime);
            setDirectStopTime(endTime);
            _announced_constraint_violation = false;
            return true;
        }
}
/*---------------------------------------------------------------------------
    isTimedControlReady

    Returns true if some action due to timed control should be taken.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::isReadyForTimedControl(LONG secondsFromBeginningOfDay)
{   
    bool ret_val = true;

    if(getControlType() == CtiLMProgramBase::TimedType)
    {
        bool in_cw = (getControlWindow(secondsFromBeginningOfDay) != NULL);
        bool timed_active = (getProgramState() == CtiLMProgramBase::TimedActiveState);
        bool inactive = (getProgramState() == CtiLMProgramBase::InactiveState);
        bool disabled = getDisableFlag();
        bool is_ramping_out = getIsRampingOut();
        
        return
            (in_cw && inactive && !disabled) ||     // In window, inactive, should start
            (!in_cw && timed_active && !disabled) || // Not in window but timed active, should stop
            (timed_active && disabled /*&& !is_ramping_out*/);               // Timed active and disabled, should stop
    }
    return false;
}

/*---------------------------------------------------------------------------
    hasControlHoursAvailable()

    Returns boolean if groups in this program are below the max hours
    daily/monthly/seasonal/annually.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasControlHoursAvailable() 
{
    BOOL returnBoolean = FALSE;
    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
        CtiLMGroupPtr currentLMGroup  = *i;    
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
        CtiLMGroupVec program_groups  = getLMProgramDirectGroups();
        for(CtiLMGroupIter i = program_groups.begin(); i != program_groups.end(); i++)
        {
            CtiLMGroupPtr currentLMGroup = *i;
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
BOOL CtiLMProgramDirect::doesGroupHaveAmpleControlTime(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const
{
    return !( (getMaxActivateTime() > 0 && (currentLMGroup->getCurrentControlDuration() + estimatedControlTimeInSeconds > getMaxActivateTime())) ||
	      (getMaxHoursDaily() > 0 && (currentLMGroup->getCurrentHoursDaily() + estimatedControlTimeInSeconds) > getMaxHoursDaily()) ||
              (getMaxHoursMonthly() > 0 && (currentLMGroup->getCurrentHoursMonthly() + estimatedControlTimeInSeconds) > getMaxHoursMonthly()*3600) ||
              (getMaxHoursSeasonal() > 0 && (currentLMGroup->getCurrentHoursSeasonal() + estimatedControlTimeInSeconds) > getMaxHoursSeasonal()*3600) ||
              (getMaxHoursAnnually() > 0 && (currentLMGroup->getCurrentHoursAnnually() + estimatedControlTimeInSeconds) > getMaxHoursAnnually()*3600) );
}

/*-----------------------------------------------------------------------------
  hasGroupExceededMaxDailyOps
-----------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasGroupExceededMaxDailyOps(CtiLMGroupPtr& lm_group) const
{
    return ( getMaxDailyOps() > 0 && (lm_group->getDailyOps() >= getMaxDailyOps()));
}

/*-------------------------------------------------------------------------
    calculateGroupControlTimeLeft

    .
--------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::calculateGroupControlTimeLeft(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const
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

    RWOrdered* rw_groups = new RWOrdered();
    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        rw_groups->insert(lm_group.get());
    }
    
    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;
    RWTime tempTime4;
    RWTime tempTime5;
    
    istrm >> _currentgearnumber
          >> _lastgroupcontrolled
          >> tempTime1
          >> tempTime2
          >> tempTime3
          >> tempTime4
	  >> tempTime5
	  >> _trigger_offset
	  >> _trigger_restore_offset
          >> _lmprogramdirectgears
          >> rw_groups;


    _directstarttime = RWDBDateTime(tempTime1);
    _directstoptime = RWDBDateTime(tempTime2);
    _notify_active_time = RWDBDateTime(tempTime3);    
    _notify_inactive_time = RWDBDateTime(tempTime4);
    _startedrampingout = RWDBDateTime(tempTime5);
    if( _currentgearnumber > 0 )
    {
        _currentgearnumber = _currentgearnumber - 1;
    }
    delete rw_groups;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::saveGuts(RWvostream& ostrm ) const
{
    CtiLMProgramBase::saveGuts( ostrm );

    // Only send active master/subordinate programs
    vector<CtiLMProgramDirect*> active_masters(_master_programs.size());
    for(set<CtiLMProgramDirect*>::const_iterator m_iter = _master_programs.begin();
        m_iter != _master_programs.end();
        m_iter++)
    {
        if((*m_iter)->getProgramState() != CtiLMProgramBase::InactiveState)
        {
            active_masters.push_back(*m_iter);
        }
    }
    
    vector<CtiLMProgramDirect*> active_subordinates(_subordinate_programs.size());
    for(set<CtiLMProgramDirect*>::const_iterator s_iter = _subordinate_programs.begin();
        s_iter != _subordinate_programs.end();
        s_iter++)
    {
        if((*s_iter)->getProgramState() != CtiLMProgramBase::InactiveState)
        {
            active_subordinates.push_back(*s_iter);
        }
    }
    
    ostrm << (_currentgearnumber+1)
          << _lastgroupcontrolled
          << _directstarttime.rwtime()
          << _directstoptime.rwtime()
          << _notify_active_time.rwtime()
          << _notify_inactive_time.rwtime()	
          << _startedrampingout.rwtime()
	  << _trigger_offset
	  << _trigger_restore_offset
          << _lmprogramdirectgears;

    // send groups, using a rwordered seems to be trouble
    ostrm << _lmprogramdirectgroups.size();
    for(CtiLMGroupConstIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        ostrm << lm_group.get();
    }    

    // send all the active master programs
    ostrm << active_masters.size();
    for(vector<CtiLMProgramDirect*>::const_iterator m2_iter = active_masters.begin();
        m2_iter != active_masters.end();
        m2_iter++)
    {
        ostrm << *m2_iter;
    }

    // send all the active subordinate programs
    ostrm << active_subordinates.size();
    for(vector<CtiLMProgramDirect*>::const_iterator s2_iter = active_subordinates.begin();
        s2_iter != active_subordinates.end();
        s2_iter++)
    {
        ostrm << *s2_iter;
    }
    
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
        _notify_active_time = right._notify_active_time;
        _notify_inactive_time = right._notify_inactive_time;	
        _trigger_offset = right._trigger_offset;
	_trigger_restore_offset = right._trigger_restore_offset;
        _startedrampingout = right._startedrampingout;
        _announced_constraint_violation = right._announced_constraint_violation;

         for(LONG i=0;i<right._lmprogramdirectgears.entries();i++)
         {
                _lmprogramdirectgears.insert(((CtiLMProgramDirectGear*)right._lmprogramdirectgears[i])->replicate());
//             _lmprogramdirectgears.insert(right._lmprogramdirectgears[i]);
         }

         _lmprogramdirectgroups = right._lmprogramdirectgroups;
         _master_programs = right._master_programs;
         _subordinate_programs = right._subordinate_programs;
         
        
//        _lmprogramdirectgears.clearAndDestroy();
//         for(LONG i=0;i<right._lmprogramdirectgears.entries();i++)
//         {
//             _lmprogramdirectgears.insert(((CtiLMProgramDirectGear*)right._lmprogramdirectgears[i])->replicate());
//         }

// //        _lmprogramdirectgroups.clearAndDestroy();
//         for(LONG j=0;j<right._lmprogramdirectgroups.entries();j++)
//         {
//             _lmprogramdirectgroups.insert(((CtiLMGroupBase*)right._lmprogramdirectgroups[j])->replicate());
//         }
//     }
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
        rdr["heading"] >> _message_subject;
        rdr["messageheader"] >> _message_header;
        rdr["messagefooter"] >> _message_footer;
        rdr["triggeroffset"] >> _trigger_offset;
	rdr["restoreoffset"] >> _trigger_restore_offset;
	rdr["notifyactiveoffset"] >> _notify_active_offset;
        rdr["notifyinactiveoffset"] >> _notify_inactive_offset;
        rdr["currentgearnumber"] >> _currentgearnumber;
        rdr["lastgroupcontrolled"] >> _lastgroupcontrolled;
        rdr["starttime"] >> _directstarttime;
        rdr["stoptime"] >> _directstoptime;
        rdr["timestamp"] >> _dynamictimestamp;
//        rdr["dailyops"] >> _dailyops;
        rdr["notifyactivetime"] >> _notify_active_time;
        rdr["notifyinactivetime"] >> _notify_inactive_time;	
        rdr["startedrampingout"] >> _startedrampingout;
        
        _insertDynamicDataFlag = FALSE;
        setDirty(false);
    }
    else
    {
        _notify_active_offset = 0;
        _notify_inactive_offset = 0;	
        setCurrentGearNumber(0);
        setLastGroupControlled(0);
//        resetDailyOps();
        setDirectStartTime(gInvalidRWDBDateTime);
        setDirectStopTime(gInvalidRWDBDateTime);
        setTriggerOffset(0);
	setTriggerRestoreOffset(0);
        _dynamictimestamp = gInvalidRWDBDateTime;
        _notify_active_time = gInvalidRWDBDateTime;
        _notify_inactive_time = gInvalidRWDBDateTime;	
        _startedrampingout = gInvalidRWDBDateTime;
        _insertDynamicDataFlag = TRUE;
        setDirty(true);
    }
    //ok to announce timed program constraint violations once per database reload
    _announced_constraint_violation = false;
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
    if(!isDirty())
    {
        return;
    }
    
    CtiLMProgramBase::dumpDynamicData(conn,currentDateTime);
    
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
//                        << dynamicLMProgramDirectTable["dailyops"].assign(getDailyOps())
                        << dynamicLMProgramDirectTable["notifyactivetime"].assign(getNotifyActiveTime())
                        << dynamicLMProgramDirectTable["startedrampingout"].assign(getStartedRampingOutTime())
                        << dynamicLMProgramDirectTable["notifyinactivetime"].assign(getNotifyInactiveTime());		
                
                updater.where(dynamicLMProgramDirectTable["deviceid"]==getPAOId());//will be paobjectid

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }

                updater.execute( conn );
                _dynamictimestamp = currentDateTime;
                setDirty(false);
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
//                         << getDailyOps()
                         << getNotifyActiveTime()
                         << getStartedRampingOutTime()
			 << getNotifyInactiveTime();
                
                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );
                _dynamictimestamp = currentDateTime;
                _insertDynamicDataFlag = FALSE;
                setDirty(false);
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
    long control_time = stop - start;

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
        return control_time * (double) cur_gear->getMethodRate()/100.0;//       getMethodRateCount()
    }
    else if(method == CtiLMProgramDirectGear::RotationMethod.data())
    {
        long send_rate = cur_gear->getMethodRate();
        long shed_time = cur_gear->getMethodPeriod();
        long number_of_groups = getLMProgramDirectGroups().size();
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

/*----------------------------------------------------------------------------
  getCurrentLoadReduction

  Returns how much load this program currently represents.
  Add up all the kwcapacites * group reduction %
  ----------------------------------------------------------------------------*/
double CtiLMProgramDirect::getCurrentLoadReduction()
{
    double total_load_reduction = 0.0;
    CtiLMProgramDirectGear* lm_gear = getCurrentGearObject();

    if(getProgramState() == CtiLMProgramBase::InactiveState)
    {
        return 0.0;
    }
    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
	if(lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState)
	{
	    total_load_reduction += (lm_gear->getPercentReduction() * lm_group->getKWCapacity()) / 100.0;
	}
    }
    return total_load_reduction;
}

/*----------------------------------------------------------------------------
  ResetGroups
  
----------------------------------------------------------------------------*/  
void CtiLMProgramDirect::ResetGroups()
{
    RWDBDateTime reset_time(gInvalidRWDBDateTime);
    for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        lm_group->setNextControlTime(reset_time);
    }
}

/*----------------------------------------------------------------------------
  RampInGroups

  Sets the next control time for all the groups in the program according to
  the ramp in settings of the program.
----------------------------------------------------------------------------*/  
void CtiLMProgramDirect::RampInGroups(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear)
{
    if(lm_gear == 0)
    {
        lm_gear = getCurrentGearObject();
    }
    
    int num_groups = _lmprogramdirectgroups.size();
    long ramp_in_interval = lm_gear->getRampInInterval();
    long ramp_in_percent = lm_gear->getRampInPercent();
    int total_groups_taken = 0;
    int cur_interval = 1; //start at first interval so action starts right awayTue Jun 22 14:52:14 CST 2004
    
    while(total_groups_taken < num_groups)
    {   //the number of groups that should be taken by the nth interval
        int should_be_taken = floor((((double)ramp_in_percent/100.0 * (double)cur_interval) * (double)num_groups) + 0.5);
        int num_to_take = should_be_taken - total_groups_taken;
        RWDBDateTime ctrl_time = RWDBDateTime(RWTime(secondsFrom1901 + (cur_interval-1) * ramp_in_interval));
        
        if( _LM_DEBUG & LM_DEBUG_STANDARD )                     
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << "LMProgram: " << getPAOName() << ",  ramping in a total of " << num_to_take << " groups"; 
        }
                for(int j = 0; j < num_to_take; j++)
        {
//          CtiLMGroupBase* lm_group = findNextGroupToTake();
            CtiLMGroupPtr lm_group = findGroupToTake(lm_gear);
            if(lm_group.get() != NULL)
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )                     
                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << RWTime() << "ramping in LMGroup: " << lm_group->getPAOName() << " at: " << ctrl_time.asString() << endl;
                }

                lm_group->setIsRampingIn(true);
                lm_group->setNextControlTime(ctrl_time);
            }
            else
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << RWTime() << " **Checkpoint** " <<  "LMProgram: " << getPAOName() << " couldn't find a group to take when ramping in." << __FILE__ << "(" << __LINE__ << ")" << endl;
            }
    
        }
        total_groups_taken += num_to_take;
        cur_interval++;
    }
}

/*----------------------------------------------------------------------------
  StartMasterCycle

  Sets the next control time for all the groups in this program
  and return the expected load reduction.
----------------------------------------------------------------------------*/
double  CtiLMProgramDirect::StartMasterCycle(ULONG secondsFrom1901, CtiLMProgramDirectGear* lm_gear)
{
    bool do_ramp = (lm_gear->getRampInPercent() > 0);
    int num_groups = _lmprogramdirectgroups.size();
    double expected_load_reduction = 0.0;
    
    if(do_ramp)
    {   
        RampInGroups(secondsFrom1901);
    }
    else
    {
        //Regular master cycle, no ramping
        long period = lm_gear->getMethodPeriod();
        long send_rate = period / num_groups;
        int num_groups_to_take = 1;


        if(num_groups >= 8)
        { //take 2 at a time - original code also seemed to care about an odd number of groups
            //and would take only 1 if the next group to be taken was the last one, but
            //shouldn't this depend on the group selection method?? drop it for now
            if( _LM_DEBUG & LM_DEBUG_STANDARD )                 
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << RWTime() << "LMProgram: " << getPAOName() << ",  has more than 8 groups, taking 2 at a time" << endl;
            }

            num_groups_to_take = 2;
            send_rate = period / (num_groups/2) + (num_groups%2);
        }
        int total_groups_taken = 0;
        int cur_period = 0;
        while(total_groups_taken < num_groups)
        {
            RWDBDateTime ctrl_time = RWDBDateTime(RWTime(secondsFrom1901 +(cur_period*send_rate)));

/*          if(currentLMGroup->getPAOType() == TYPE_LMGROUP_SADIGITAL ||
               currentLMGroup->getPAOType() == TYPE_LMGROUP_GOLAY )
            {
                // NOTE: SPECIAL CASE - we are going to use the groups nominal time out instead of what is specified in the gear
                
            }
*/    
            if( _LM_DEBUG & LM_DEBUG_STANDARD )                 
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << RWTime() << "LMProgram: " << getPAOName() << ",  master cycle taking " << num_groups_to_take << " groups at: " << ctrl_time.asString() << endl;
            }
                        
            for(int i = 0; i < min(num_groups_to_take, (num_groups-total_groups_taken)); i++) //if there is an odd number of groups, can't always take 2
            {
                CtiLMGroupPtr lm_group = findGroupToTake(getCurrentGearObject());//findNextGroupToTake();
                if(lm_group.get() != NULL)
                {
                    lm_group->setNextControlTime(ctrl_time);
                }
                else
                {   
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << RWTime() << " **Checkpoint** " <<  "LMProgram: " << getPAOName() << " couldn't find a group to take, master cycle." << __FILE__ << "(" << __LINE__ << ")" << endl;
                }
            }
            total_groups_taken += num_groups_to_take;
            cur_period++;
        }

	//figure out the amount of load reduction
	//and bump the daily ops for all the groups and set the control starttime
        for(CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++)
        {
            CtiLMGroupPtr lm_group  = *i;        
            if( lm_gear->getPercentReduction() > 0.0 )
            {
                expected_load_reduction += (lm_gear->getPercentReduction() / 100.0) * lm_group->getKWCapacity();
            }
            else
            {
                expected_load_reduction += lm_group->getKWCapacity() * (lm_gear->getMethodRate() / 100.0);
            }

	    lm_group->incrementDailyOps();
	    lm_group->setControlStartTime(RWDBDateTime(RWTime(secondsFrom1901)));
        }

    }
    return expected_load_reduction;
}

bool CtiLMProgramDirect::notifyGroups(int type, CtiMultiMsg* multiNotifMsg)
{
    vector<int> notif_groups;// = _notificationgroupids;
//    std::copy(_notificationgroupids.begin(), _notificationgroupids.end(), notif_groups.begin());
    CtiNotifLMControlMsg* notif_msg = new CtiNotifLMControlMsg(_notificationgroupids, type, getPAOId(), getDirectStartTime().rwtime(), getDirectStopTime().rwtime());
    multiNotifMsg->insert(notif_msg);
    return true;
}


// Static Members
int CtiLMProgramDirect::defaultLMStartPriority = 13;
int CtiLMProgramDirect::defaultLMRefreshPriority = 11;
