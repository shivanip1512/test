#include "precompiled.h"

#include <algorithm>
#include <queue>

#include "dbaccess.h"

#include "lmprogramdirect.h"
#include "lmprogramdirectgear.h"
#include "lmgroupbase.h"
#include "lmgrouppoint.h"
#include "devicetypes.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"
#include "msg_signal.h"
#include "msg_pcrequest.h"
#include "msg_notif_lmcontrol.h"
#include "lmcontrolareatrigger.h"
#include "lmprogramthermostatgear.h"
#include "lmprogramcontrolwindow.h"
#include "lmconstraint.h"
#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"
#include "database_writer.h"
#include "database_util.h"
#include "smartgearbase.h"
#include "lmgroupdigisep.h"
#include "LMScheduledMessageHolder.h"
#include "random_generator.h"

using std::string;
using std::endl;
using std::vector;
using std::set;

extern ULONG _LM_DEBUG;
extern std::queue<CtiTableLMProgramHistory> _PROGRAM_HISTORY_QUEUE;
extern LMScheduledMessageHolder gScheduledStopMessages;

DEFINE_COLLECTABLE(CtiLMProgramDirect, CTILMPROGRAMDIRECT_ID)

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramDirect::CtiLMProgramDirect() :
_notify_active_time(gInvalidCtiTime),
_notify_inactive_time(gInvalidCtiTime),
_startedrampingout(gInvalidCtiTime),
_constraint_override(false),
_announced_program_constraint_violation(false),
_notify_active_offset(0),
_notify_inactive_offset(0),
_notify_when_scheduled(false),
_trigger_offset(0),
_trigger_restore_offset(0),
_currentgearnumber(0),
_lastgroupcontrolled(0),
_controlActivatedByStatusTrigger(false),
_curLogID(0),
_insertDynamicDataFlag(false),
_adjustment_notification_enabled(false),
_adjustment_notification_pending(false),
_hasBeatThePeakGear(false)
{
}

CtiLMProgramDirect::CtiLMProgramDirect(Cti::RowReader &rdr) :
_notify_active_time(gInvalidCtiTime),
_notify_inactive_time(gInvalidCtiTime),
_startedrampingout(gInvalidCtiTime),
_constraint_override(false),
_announced_program_constraint_violation(false),
_adjustment_notification_pending(false),
_hasBeatThePeakGear(false)
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
    delete_container(_lmprogramdirectgears);
    _lmprogramdirectgears.clear();
}

/*----------------------------------------------------------------------------
  getNotifyActiveOffset

  Returns the program active notification offset in seconds
  -1 indicates it isn't used
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getNotifyActiveOffset() const
{
    return _notify_active_offset;
}

/*----------------------------------------------------------------------------
  getNotifyInactiveOffset

  Returns the program inactive notification offset in seconds
  -1 indicates it isn't used
----------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::getNotifyInactiveOffset() const
{
    return _notify_inactive_offset;
}

/*----------------------------------------------------------------------------
getNotifySchedule

Returns the schedule notification enable
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::shouldNotifyWhenScheduled() const
{
    return _notify_when_scheduled;
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
    getDirectStartTime

    Returns the direct start time for a manual control of the direct program
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramDirect::getDirectStartTime() const
{
    return _directstarttime;
}

/*---------------------------------------------------------------------------
    getDirectStopTime

    Returns the direct stop time for a manual control of the direct program
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramDirect::getDirectStopTime() const
{
    return _directstoptime;
}

/*----------------------------------------------------------------------------
  getNotifyActiveTime

  Returns the program active notify time
----------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramDirect::getNotifyActiveTime() const
{
    return _notify_active_time;
}

// This is set when notification is needed and cleared when that notification goes out
bool CtiLMProgramDirect::isAdjustNotificationPending() const
{
    return _adjustment_notification_pending;
}

/*----------------------------------------------------------------------------
  getNotifyInActiveTime

  Returns the program inactive notify time
----------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramDirect::getNotifyInactiveTime() const
{
    return _notify_inactive_time;
}

/*----------------------------------------------------------------------------
  getStartedRampingOutTime

  Returns the time this progrma started to ramp out
----------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramDirect::getStartedRampingOutTime() const
{
    return _startedrampingout;
}

/*----------------------------------------------------------------------------
  getConstraintOverride

  Returns whether this program will evaluate group constraints when it goes
  active.
----------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::getConstraintOverride() const
{
    return _constraint_override;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info of the current control for the
    direct program.
---------------------------------------------------------------------------*/
const string& CtiLMProgramDirect::getAdditionalInfo() const
{

    return _additionalinfo;
}

/*----------------------------------------------------------------------------
  getIsRampingIn

  Returns whether the program is currently ramping in
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::getIsRampingIn()
{
    CtiLMProgramDirectGear* gear = getCurrentGearObject();
    if( gear == NULL )
    {
        CTILOG_ERROR(dout, "no current gear found!?");
    }

    if( gear->getRampInInterval() == 0 || gear->getRampInPercent() == 0 )
    {
        // The current gear doesn't ramp in, no way we can be ramping in
        return false;
    }

    // OK, the gear has ramp in set up, are any of our groups
    // actualy ramping in?
    CtiLMGroupVec& groups  = getLMProgramDirectGroups();
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr currentLMGroup  = *i;
        if( currentLMGroup->getIsRampingIn() )
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
    if( gear == NULL )
    {
        CTILOG_ERROR(dout, "no current gear found!?");
    }

    const string& stop_type = gear->getMethodStopType();
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
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr currentLMGroup  = *i;
        if( currentLMGroup->getIsRampingOut() )
        {
            return true;
        }
    }
    return false;
}

const vector<CtiLMProgramDirectGear*>& CtiLMProgramDirect::getLMProgramDirectGears() const
{
    return _lmprogramdirectgears;
}

void CtiLMProgramDirect::addGear(CtiLMProgramDirectGear* gear)
{
    if ( gear != NULL )
    {
        _lmprogramdirectgears.push_back(gear);

        if( ciStringEqual( gear->getControlMethod(), CtiLMProgramDirectGear::BeatThePeakMethod) )
        {
            _hasBeatThePeakGear = true;
        }
    }
    
}

/*---------------------------------------------------------------------------
    getLMProgramDirectGroups

    Returns the a vector of pointers to this programs groups
---------------------------------------------------------------------------*/
CtiLMGroupVec& CtiLMProgramDirect::getLMProgramDirectGroups()
{
    return _lmprogramdirectgroups;
}

const CtiLMGroupVec& CtiLMProgramDirect::getLMProgramDirectGroups() const
{
    return _lmprogramdirectgroups;
}

/*----------------------------------------------------------------------------
  getMasterPrograms

  Returns a set of pointers to the programs that this program is considered
  subordinate to.
----------------------------------------------------------------------------*/
set<CtiLMProgramDirectSPtr>& CtiLMProgramDirect::getMasterPrograms()
{
    return _master_programs;
}

const set<CtiLMProgramDirectSPtr>& CtiLMProgramDirect::getMasterPrograms() const
{
    return _master_programs;
}

/*----------------------------------------------------------------------------
  getSubordinateProgrmas

  Returns a set of pointers to this programs subordinate programs.
----------------------------------------------------------------------------*/
set<CtiLMProgramDirectSPtr>& CtiLMProgramDirect::getSubordinatePrograms()
{
    return _subordinate_programs;
}

const set<CtiLMProgramDirectSPtr>& CtiLMProgramDirect::getSubordinatePrograms() const
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
    if( _currentgearnumber != currentgear )
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
    if( _lastgroupcontrolled != lastcontrolled )
    {
        _lastgroupcontrolled = lastcontrolled;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setDirectStartTime

    Sets the direct start time for manual controls of the direct program
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStartTime(const CtiTime& start)
{
    if( _directstarttime != start )
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
CtiLMProgramDirect& CtiLMProgramDirect::setDirectStopTime(const CtiTime& stop)
{
    if( _directstoptime != stop )
    {
        _directstoptime = stop;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setAdditionalInfo

    Sets the additional info of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setAdditionalInfo(const string& additional)
{

    if( _additionalinfo != additional )
    {
        _additionalinfo = additional;
        setDirty(true);
    }

    return *this;
}

/*----------------------------------------------------------------------------
  setNotifyActiveTime

  Sets the program notify active time
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setNotifyActiveTime(const CtiTime& notify)
{
    if( _notify_active_time != notify )
    {
        _notify_active_time = notify;
        setDirty(true);
    }
    return *this;
}

// This is set when notification is needed and cleared when that notification goes out
void CtiLMProgramDirect::setAdjustNotificationPending(bool adjustNeedsToBeSent)
{
    // Note that unlike the times, this is not sent to the clients and dirty does not need to be set
    _adjustment_notification_pending = adjustNeedsToBeSent;
}

/*----------------------------------------------------------------------------
  setNotifyInactiveTime

  Sets the program notify active time
----------------------------------------------------------------------------*/
CtiLMProgramDirect& CtiLMProgramDirect::setNotifyInactiveTime(const CtiTime& notify)
{
    if( _notify_inactive_time != notify )
    {
        _notify_inactive_time = notify;
        setDirty(true);
    }
    return *this;
}

CtiLMProgramDirect& CtiLMProgramDirect::setStartedRampingOutTime(const CtiTime& startedrampingout)
{
    if( _startedrampingout != startedrampingout )
    {
        _startedrampingout = startedrampingout;
        setDirty(true);
    }
    return *this;
}

CtiLMProgramDirect& CtiLMProgramDirect::setConstraintOverride(BOOL override)
{
    if( _constraint_override != override )
    {
        _constraint_override = override;
        setDirty(true);
    }
    return *this;
}


CtiLMProgramDirect& CtiLMProgramDirect::setControlActivatedByStatusTrigger(BOOL flag)
{
    _controlActivatedByStatusTrigger = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the direct program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded)
{
    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.size() > 0 && _lmprogramdirectgroups.size() > 0 )
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.size() )
        {
            LONG previousGearNumber = _currentgearnumber;
            BOOL gearChangeBoolean = hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
            currentGearObject = getCurrentGearObject();

            if( gearChangeBoolean &&
                getProgramState() != CtiLMProgramBase::InactiveState )
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Gear Change, LM Program: " << getPAOName() << ", previous gear number: " << previousGearNumber << ", new gear number: " << _currentgearnumber);
                }

                expectedLoadReduced = updateProgramControlForAutomaticGearChange(currentTime, previousGearNumber, multiPilMsg, multiDispatchMsg);
            }
            else
            {
                LONG savedState = getProgramState();

                if( getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    getProgramState() != CtiLMProgramBase::ActiveState )
                {
                    // Let the world know we are starting up!
                    scheduleStartNotification(CtiTime());

                    setProgramState(CtiLMProgramBase::ActiveState);
                    setStartedControlling(CtiTime());

                    setDirectStartTime(CtiTime());
                    setDirectStopTime(gInvalidCtiTime);
                    {
                        string text("Automatic Start, LM Program: ");
                        text += getPAOName();
                        string additional("");
                        CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                        signal->setSOE(2);

                        multiDispatchMsg->insert(signal);
                        CTILOG_INFO(dout, text << ", " << additional);
                    }
                    // Dont let subordinate programs keep running
                    stopSubordinatePrograms(multiPilMsg, multiDispatchMsg, multiNotifMsg, secondsFromBeginningOfDay);
                    ResetGroups();
                }

                if( SmartGearBase *smartGearObject = dynamic_cast<SmartGearBase *>(currentGearObject) )
                {
                    for each( CtiLMGroupPtr currentLMGroup in _lmprogramdirectgroups )
                    {
                        LONG shedTime = getDirectStopTime().seconds() - CtiTime::now().seconds();

                        // .checkControl below can modify (shorten) the shed time
                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || (con_checker.checkControl(shedTime, true) && !hasGroupExceededMaxDailyOps(currentLMGroup))  )
                        {
                            if( smartGearObject->attemptControl(currentLMGroup, shedTime, expectedLoadReduced) )
                            {
                                setLastControlSent(CtiTime());
                            }
                        }
                        else
                        {
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }

                        if( getProgramState() == CtiLMProgramBase::InactiveState )
                        {
                            // Let the world know we are starting up!
                            scheduleStartNotification(CtiTime());
                        }

                    }

                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TimeRefreshMethod) )
                {
                    LONG refreshRate = currentGearObject->getMethodRate();
                    LONG shedTime = currentGearObject->getMethodPeriod();
                    LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                    string refreshCountDownType = currentGearObject->getMethodOptionType();

                    if( numberOfGroupsToTake == 0 )
                    {
                        numberOfGroupsToTake = _lmprogramdirectgroups.size();
                    }

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        if( savedState == CtiLMProgramBase::ActiveState )
                        {
                            CTILOG_INFO(dout, "Controlling additional time refresh groups, LM Program: " << getPAOName() << ", number of additional groups: " << numberOfGroupsToTake);
                        }
                        else
                        {
                            CTILOG_INFO(dout, "Controlling time refresh groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake);
                        }
                    }

                    int groups_taken = 0;
                    do
                    {
                        CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);

                        if( currentLMGroup.get() == NULL )   // No more groups to take, get outta here
                        {
                            CTILOG_INFO(dout, "Program: " << getPAOName() << " couldn't find any groups to take");
                            break;
                        }
                        else
                        {
                            // Only a time refresh dynamic shed time refresh type should have its shed times adjusted based on constraints
                            bool adjust_shed_time = (CtiLMProgramDirectGear::DynamicShedTimeMethodOptionType == refreshCountDownType ||
                                                     CtiLMProgramDirectGear::CountDownMethodOptionType == refreshCountDownType);

                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            int oldShedTime = shedTime;
                            if( getConstraintOverride() || con_checker.checkControl(shedTime, adjust_shed_time) )
                            {
                                if( refreshCountDownType == CtiLMProgramDirectGear::FixedCountMethodOptionType && shedTime != oldShedTime )
                                {
                                    shedTime = oldShedTime;
                                }

                                groups_taken++;
                                CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);


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
                                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                                {
                                    con_checker.dumpViolations();
                                }
                            }

                            //Set this group to refresh  (even if it violated constraints)
                            currentLMGroup->setNextRefreshTime(currentTime, refreshRate);
                        }
                    } while( groups_taken < numberOfGroupsToTake );

                    setPendingGroupsInactive();

                    if( getProgramState() == CtiLMProgramBase::ActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                        for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                        {
                            CtiLMGroupPtr currentLMGroup = *i;
                            if( currentLMGroup->getGroupControlState() != CtiLMGroupBase::ActiveState &&
                                //Added activepending state in case constraints keep group from activating right away
                                currentLMGroup->getGroupControlState() != CtiLMGroupBase::ActivePendingState &&
                                //This means at least 1 item was enabled, we can be fully active if all others are disabled
                                !currentLMGroup->getControlInhibit() && !currentLMGroup->getDisableFlag() )
                            {
                                setProgramState(CtiLMProgramBase::ActiveState);
                                break;
                            }
                        }
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::SmartCycleMethod) ||
                         ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TrueCycleMethod) ||
                         ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::MagnitudeCycleMethod) )
                {
                    LONG percent = currentGearObject->getMethodRate();
                    LONG period = currentGearObject->getMethodPeriod();
                    LONG cycleCount = currentGearObject->getMethodRateCount();
                    string cycleCountDownType = currentGearObject->getMethodOptionType();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        if( CtiLMProgramDirectGear::SmartCycleMethod == currentGearObject->getControlMethod() )
                        {
                            CTILOG_DEBUG(dout, "Smart Cycling all groups, LM Program: " << getPAOName());
                        }
                        else if( CtiLMProgramDirectGear::TrueCycleMethod == currentGearObject->getControlMethod() ||
                                 CtiLMProgramDirectGear::MagnitudeCycleMethod == currentGearObject->getControlMethod() )
                        {
                            CTILOG_DEBUG(dout, "True Cycling all groups, LM Program: " << getPAOName());

                        }
                    }

                    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        bool adjust_counts = false;

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

                            if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                            {
                                adjust_counts = true;
                            }
                            else if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )//can't really do anything for limited count down on start up
                            {
                            }//we have to send the default because it is programmed in the switch

                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, adjust_counts) )
                            {
                                CtiRequestMsg* requestMsg = NULL;
                                bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);

                                if( (ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                                     ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MagnitudeCycleMethod)) &&
                                    (isExpresscomGroup(currentLMGroup->getPAOType()) ||
                                     currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305) )
                                {
                                    requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                }
                                else
                                {
                                    requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                    if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                                        ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::MagnitudeCycleMethod) )
                                    {
                                        CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << ", Smart Cycling instead");
                                    }
                                }

                                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                                {
                                    if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                    {
                                        CTILOG_DEBUG(dout, *violations);
                                    }
                                }
                            }
                        }

                        if( getProgramState() == CtiLMProgramBase::InactiveState )
                        {
                            // Let the world know we are starting up!
                            scheduleStartNotification(CtiTime());
                        }

                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TargetCycleMethod) )
                {
                    LONG percent = currentGearObject->getMethodRate();
                    LONG period = currentGearObject->getMethodPeriod();
                    LONG cycleCount = currentGearObject->getMethodRateCount();
                    DOUBLE kw = currentGearObject->getKWReduction();
                    string cycleCountDownType = currentGearObject->getMethodOptionType();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_DEBUG(dout, "Target Cycling all groups, LM Program: " << getPAOName());
                    }

                    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        bool adjust_counts = false;

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

                            if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                            {
                                adjust_counts = true;
                            }
                            else if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )//can't really do anything for limited count down on start up
                            {
                            }//we have to send the default because it is programmed in the switch

                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, adjust_counts) )
                            {
                                CtiRequestMsg* requestMsg = NULL;
                                bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                                if( isExpresscomGroup(currentLMGroup->getPAOType()) ||
                                    currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305 )
                                {
                                    requestMsg = currentLMGroup->createTargetCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority, kw, getStartedControlling(), getAdditionalInfo());
                                }
                                else
                                {
                                    requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                    if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear ::TrueCycleMethod) ||
                                        ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MagnitudeCycleMethod) )
                                    CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Target Cycle a non-Expresscom group: " << currentLMGroup->getPAOName() << ", Smart Cycling instead");
                                }

                                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

                                if( currentGearObject->getPercentReduction() > 0.0 )
                                {
                                    expectedLoadReduced += kw;
                                }
                                else
                                {
                                    expectedLoadReduced += kw;
                                }
                            }
                            else
                            {
                                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                                {
                                    if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                    {
                                        CTILOG_DEBUG(dout, *violations);
                                    }
                                }
                            }
                        }

                        if( getProgramState() == CtiLMProgramBase::InactiveState )
                        {
                            // Let the world know we are starting up!
                            scheduleStartNotification(CtiTime());
                        }

                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MasterCycleMethod) )
                {
                    ResetGroups();
                    expectedLoadReduced = StartMasterCycle(currentTime, currentGearObject);
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::RotationMethod) )
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
                        CTILOG_DEBUG(dout, "Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake);
                    }

                    int groups_taken = 0;
                    do
                    {
                        CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);

                        if( currentLMGroup.get() == NULL )
                        {
                            CTILOG_INFO(dout, "Program: " << getPAOName() << " couldn't find any groups to take");
                            break;
                        }
                        else
                        {
                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                            {
                                groups_taken++;
                                CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);

                                setLastGroupControlled(currentLMGroup->getPAOId());
                                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                    } while( groups_taken < numberOfGroupsToTake );

                    setPendingGroupsInactive();

                    if( getProgramState() == CtiLMProgramBase::InactiveState )
                    {
                        // Let the world know we are starting up!
                        scheduleStartNotification(CtiTime());
                    }

                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::LatchingMethod) )
                {
                    LONG gearStartRawState = currentGearObject->getMethodRateCount();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_DEBUG(dout, "Controlling latch groups, LM Program: " << getPAOName());
                    }


                    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;

                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() &&
                            !hasGroupExceededMaxDailyOps(currentLMGroup) )
                        {
                            // TODO:  Why isn't startGroupControl called here?
                            if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                            {
                                multiPilMsg->insert( currentLMGroup->createLatchingRequestMsg( true, defaultLMStartPriority ) );
                            }
                            else
                            {
                                multiDispatchMsg->insert( currentLMGroup->createLatchingCommandMsg(gearStartRawState, defaultLMStartPriority) );
                            }


                            setLastControlSent(CtiTime());
                            setLastGroupControlled(currentLMGroup->getPAOId());
                            currentLMGroup->setLastControlSent(CtiTime());
                            currentLMGroup->setControlStartTime(CtiTime());
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

                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::ThermostatRampingMethod) )
                {
                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_DEBUG(dout, "Thermostat Set Point command all groups, LM Program: " << getPAOName());
                    }

                    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() &&
                            !hasGroupExceededMaxDailyOps(currentLMGroup) )
                        {
                            if(isExpresscomGroup(currentLMGroup->getPAOType()))
                            {
                                std::string logMessage;
                                const CtiLMProgramThermostatGear * thermostatGearObject = static_cast<const CtiLMProgramThermostatGear*>( currentGearObject );

                                // XXX thermostat constraints??

                                CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(*thermostatGearObject, defaultLMStartPriority, logMessage);
                                if ( requestMsg )   // valid request - signal the log message
                                {
                                    multiDispatchMsg->insert( CTIDBG_new CtiSignalMsg( SYS_PID_LOADMANAGEMENT,
                                                                                       0,
                                                                                       logMessage,
                                                                                       currentLMGroup->getPAOName() + " / (" + CtiNumStr( currentLMGroup->getPAOId() ) + "): Thermostat Set Point",
                                                                                       LoadMgmtLogType,
                                                                                       SignalEvent,
                                                                                       "(yukon system)",
                                                                                       0,
                                                                                       defaultLMStartPriority - 1 ) );
                                }
                                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                                CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName());
                            }
                        }
                    }
                    if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::SimpleThermostatRampingMethod) )
                {
                    bool didSendMessages = sendSimpleThermostatMessage(currentGearObject, currentTime, multiPilMsg, multiDispatchMsg, expectedLoadReduced, false);
                    if( didSendMessages && getProgramState() != CtiLMProgramBase::ManualActiveState )
                    {
                        setProgramState(CtiLMProgramBase::FullyActiveState);
                    }
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::NoControlMethod) )
                {
                    if( _LM_DEBUG & LM_DEBUG_EXTENDED )
                    {
                        CTILOG_DEBUG(dout, "NO control gear, LM Program: " << getPAOName());
                    }

                    setProgramState(CtiLMProgramBase::NonControllingState);
                }
                else
                {
                    CTILOG_INFO(dout, "Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method: " << currentGearObject->getControlMethod());
                }
            }
        }
        else
        {
            CTILOG_INFO(dout, "Invalid current gear number: " << _currentgearnumber);
        }
    }
    else if( _lmprogramdirectgears.size() <= 0 )
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " doesn't have any gears");
    }
    else
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " doesn't have any groups");
    }

    // Did we activate any groups??  If not set ourself inactive
    // This is kinda ugly, but if we didn't actually activate a group then we really aren't active.
    // The problem is that this function kinda assumes we can take load when that isn't necessarily
    // the case... if the caller would do some checking first then maybe we wouldn't have to do this...?
    bool found_active_group = false;
    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
    {
        CtiLMGroupPtr currentLMGroup  = *i;
        if( currentLMGroup->getGroupControlState() != CtiLMGroupBase::InactiveState )
        {
            found_active_group = true;
            break;
        }
    }

    if( getProgramState() == CtiLMProgramBase::NonControllingState )
    {
        setProgramState(CtiLMProgramBase::FullyActiveState);
    }
    else if( !found_active_group )
    {
        setProgramState(CtiLMGroupBase::InactiveState);
    }

    setReductionTotal(getReductionTotal() + expectedLoadReduced);
    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    manualReduceProgramLoad


---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::manualReduceProgramLoad(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    DOUBLE expectedLoadReduced = 0.0;

    if( _lmprogramdirectgears.size() > 0 && _lmprogramdirectgroups.size() > 0 )
    {
        CtiLMProgramDirectGear* currentGearObject = NULL;
        if( _currentgearnumber < _lmprogramdirectgears.size() )
        {
            currentGearObject = getCurrentGearObject();

            if( SmartGearBase *smartGearObject = dynamic_cast<SmartGearBase *>(currentGearObject) )
            {
                for each( CtiLMGroupPtr currentLMGroup in _lmprogramdirectgroups )
                {
                    LONG shedTime = getDirectStopTime().seconds() - CtiTime::now().seconds();

                    // .checkControl below can modify (shorten) the shed time
                    CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                    if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                    {
                        if( smartGearObject->attemptControl(currentLMGroup, shedTime, expectedLoadReduced) )
                        {
                            setLastControlSent(CtiTime());
                        }
                    }
                    else
                    {
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                            {
                                CTILOG_DEBUG(dout, *violations);
                            }
                        }
                    }
                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TimeRefreshMethod) )
            {
                bool do_ramp = (currentGearObject->getRampInPercent() > 0);
//                ResetGroups(); this also clears out next control times!
                ResetGroupsControlState();
                ResetGroupsInternalState();

                if( do_ramp )
                {
                    RampInGroups(currentTime);
                }
                else   //FIGURE OUT REGULAR TIME REFRESH
                {
                    LONG refreshRate = currentGearObject->getMethodRate();
                    LONG shedTime = currentGearObject->getMethodPeriod();

                    //take all groups in a manual control
                    string refreshCountDownType = currentGearObject->getMethodOptionType();
                    LONG maxRefreshShedTime = currentGearObject->getMethodPeriod();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_DEBUG(dout, "Controlling all time refresh groups, LM Program: " << getPAOName());
                    }
                    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                    {
                        CtiLMGroupPtr currentLMGroup  = *i;
                        if( !currentLMGroup->getDisableFlag() &&
                            !currentLMGroup->getControlInhibit() )
                        {
                            if( ciStringEqual(refreshCountDownType, CtiLMProgramDirectGear::DynamicShedTimeMethodOptionType) ||
                                ciStringEqual(refreshCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                            {
                                if( maxRefreshShedTime > 0 )   // Try to fit the shed time into the time the program will run
                                {
                                    // but respect the max shed time setting (methodoptionmax)
                                    ULONG tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();

                                    if( maxRefreshShedTime > 0 &&
                                        tempShedTime > maxRefreshShedTime )
                                    {
                                        tempShedTime = maxRefreshShedTime;
                                    }
                                    shedTime = tempShedTime;
                                }
                                else   // Adjust the shed time to fit the time the program will run
                                {
                                    // there is no max shed time but regardless don't let the shed time
                                    // extend past the beginning of tomorrow
                                    CtiTime tempDateTime;
                                    CtiDate tempDate = tempDateTime.date();
                                    CtiTime compareDateTime(tempDate,0,0,0);
                                    compareDateTime.addDays(1);
                                    ULONG tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();

                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempShedTime = compareDateTime.seconds() - CtiTime().seconds();
                                    }
                                    else
                                    {
                                        tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();
                                    }

                                    shedTime = tempShedTime;
                                }
                            }

                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            long oldShedTime = shedTime;
                            if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                            {
                                if( refreshCountDownType == CtiLMProgramDirectGear::FixedCountMethodOptionType && shedTime != oldShedTime )
                                {
                                    shedTime = oldShedTime;
                                }

                                CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);


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
                                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                                {
                                    if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                    {
                                        CTILOG_DEBUG(dout, *violations);
                                    }
                                }
                            }
                            //Set this group to refresh again (or not...)
                            currentLMGroup->setNextRefreshTime(currentTime, refreshRate);
                        }
                    }
                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::SmartCycleMethod) )
            {
                LONG percent = currentGearObject->getMethodRate();
                LONG period = currentGearObject->getMethodPeriod();
                LONG cycleCount = currentGearObject->getMethodRateCount();
                string cycleCountDownType = currentGearObject->getMethodOptionType();
                LONG maxCycleCount = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Smart Cycling all groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    bool adjust_counts = false;

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was different
                        cycleCount = currentGearObject->getMethodRateCount();

                        if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                        {
                            if( maxCycleCount > 0 || cycleCount == 0 )
                            {
                                if( period != 0 )
                                {
                                    ULONG tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
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
                                    CTILOG_INFO(dout, "Tried to divide by zero");
                                }
                            }
                            else
                            {
                                CtiTime tempDateTime;
                                CtiDate tempDate = tempDateTime.date();
                                CtiTime compareDateTime(tempDate,0,0,0);
                                compareDateTime.addDays(1);
                                LONG tempCycleCount = cycleCount;
                                if( period != 0 )
                                {
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempCycleCount = (compareDateTime.seconds() - CtiTime().seconds()) / period;
                                        if( ((compareDateTime.seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                    else
                                    {
                                        tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                }
                                else
                                {
                                    CTILOG_INFO(dout, "Tried to divide by zero");
                                }

                                if( tempCycleCount > 63 )//Versacom can't support counts higher than 63
                                {
                                    tempCycleCount = 63;
                                }
                                cycleCount = tempCycleCount;
                            }
                        }
                        else if( ciStringEqual(currentGearObject->getMethodOptionType(), CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )//can't really do anything for limited count down on start up
                        {
                        }//we have to send the default because it is programmed in the switch

                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, true) )
                        {
                            bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                            CtiRequestMsg* requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::MasterCycleMethod) )  //NOTE: add ramp in logic
            {
                ResetGroups();
                StartMasterCycle(CtiTime().seconds(), currentGearObject);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::RotationMethod) )
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
                    CTILOG_DEBUG(dout, "Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake);
                }

                int groups_taken = 0;
                do
                {
                    CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup.get() == NULL )   // No more groups to take, get outta here
                    {
                        CTILOG_INFO(dout, "Program: " << getPAOName() << " couldn't find any groups to take");
                        break;
                    }
                    else
                    {
                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                        {
                            groups_taken++;
                            CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);

                            setLastGroupControlled(currentLMGroup->getPAOId());
                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                } while( groups_taken < numberOfGroupsToTake );

                setPendingGroupsInactive();

                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::LatchingMethod) )
            {
                LONG gearStartRawState = currentGearObject->getMethodRateCount();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Controlling latch groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        // TODO: Why isn't startGroupControl called here?
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                        {
                            multiPilMsg->insert( currentLMGroup->createLatchingRequestMsg( true, defaultLMStartPriority ) );
                        }
                        else
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingCommandMsg(gearStartRawState, defaultLMStartPriority) );
                        }

                        setLastControlSent(CtiTime());
                        setLastGroupControlled(currentLMGroup->getPAOId());
                        currentLMGroup->setLastControlSent(CtiTime());
                        currentLMGroup->incrementDailyOps();
                        currentLMGroup->setControlStartTime(CtiTime());
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
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TrueCycleMethod) ||
                     ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::MagnitudeCycleMethod) )
            {
                LONG percent = currentGearObject->getMethodRate();
                LONG period = currentGearObject->getMethodPeriod();
                LONG cycleCount = currentGearObject->getMethodRateCount();
                string cycleCountDownType = currentGearObject->getMethodOptionType();
                LONG maxCycleCount = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "True Cycling all groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was different
                        cycleCount = currentGearObject->getMethodRateCount();

                        if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                        {
                            if( maxCycleCount > 0 || cycleCount == 0 )
                            {
                                if( period != 0 )
                                {
                                    LONG tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
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
                                    CTILOG_INFO(dout, "Tried to divide by zero");
                                }
                            }
                            else
                            {
                                CtiTime tempDateTime;
                                CtiDate tempDate = tempDateTime.date();
                                CtiTime compareDateTime(tempDate,0,0,0);
                                compareDateTime.addDays(1);
                                LONG tempCycleCount = cycleCount;
                                if( period != 0 )
                                {
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempCycleCount = (compareDateTime.seconds() - CtiTime().seconds()) / period;
                                        if( ((compareDateTime.seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                    else
                                    {
                                        tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                }
                                else
                                {
                                    CTILOG_INFO(dout, "Tried to divide by zero");
                                }

                                if( tempCycleCount > 63 )//Versacom can't support counts higher than 63
                                {
                                    tempCycleCount = 63;
                                }
                                cycleCount = tempCycleCount;
                            }
                        }
                        else if( ciStringEqual(currentGearObject->getMethodOptionType(), CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )//can't really do anything for limited count down on start up
                        {
                        }//we have to send the default because it is programmed in the switch

                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, true) )
                        {
                            CtiRequestMsg* requestMsg = NULL;
                            bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                            if( isExpresscomGroup(currentLMGroup->getPAOType()) || currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305 )
                            {
                                requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                            }
                            else
                            {
                                CtiLMProgramDirectGear* prevGearObject = 0;
                                requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                if( _currentgearnumber > 0 )
                                {
                                    prevGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];
                                }

                                CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName());
                            }

                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }

                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TargetCycleMethod) )
            {
                DOUBLE kw = currentGearObject->getKWReduction();
                LONG percent = currentGearObject->getMethodRate();
                LONG period = currentGearObject->getMethodPeriod();
                LONG cycleCount = currentGearObject->getMethodRateCount();
                string cycleCountDownType = currentGearObject->getMethodOptionType();
                LONG maxCycleCount = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "True Cycling all groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;

                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was different
                        cycleCount = currentGearObject->getMethodRateCount();

                        if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                        {
                            if( maxCycleCount > 0 || cycleCount == 0 )
                            {
                                if( period != 0 )
                                {
                                    LONG tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
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
                                    CTILOG_INFO(dout, "Tried to divide by zero");
                                }
                            }
                            else
                            {
                                CtiTime tempDateTime;
                                CtiDate tempDate = tempDateTime.date();
                                CtiTime compareDateTime(tempDate,0,0,0);
                                compareDateTime.addDays(1);
                                LONG tempCycleCount = cycleCount;
                                if( period != 0 )
                                {
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempCycleCount = (compareDateTime.seconds() - CtiTime().seconds()) / period;
                                        if( ((compareDateTime.seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                    else
                                    {
                                        tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                }
                                else
                                {
                                    CTILOG_INFO(dout, "Tried to divide by zero");
                                }

                                if( tempCycleCount > 63 )//Versacom can't support counts higher than 63
                                {
                                    tempCycleCount = 63;
                                }
                                cycleCount = tempCycleCount;
                            }
                        }
                        else if( ciStringEqual(currentGearObject->getMethodOptionType(), CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )//can't really do anything for limited count down on start up
                        {
                        }//we have to send the default because it is programmed in the switch

                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, 100, true) )
                        {
                            CtiRequestMsg* requestMsg = NULL;
                            bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                            if( isExpresscomGroup(currentLMGroup->getPAOType()) || currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305 )
                            {
                                requestMsg = currentLMGroup->createTargetCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority, kw, getStartedControlling(), getAdditionalInfo());
                            }
                            else
                            {
                                CtiLMProgramDirectGear* prevGearObject = 0;
                                requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                if( _currentgearnumber > 0 )
                                {
                                    prevGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];
                                }

                                CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Target Cycle a non-Expresscom group: " << currentLMGroup->getPAOName());
                            }

                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

                            if( currentGearObject->getPercentReduction() > 0.0 )
                            {
                                expectedLoadReduced += kw;
                            }
                            else
                            {
                                expectedLoadReduced += kw;
                            }
                        }
                        else
                        {
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }

                }
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod) )
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Thermostat Set Point command all groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        if( isExpresscomGroup(currentLMGroup->getPAOType()) )
                        {
                            std::string logMessage;
                            const CtiLMProgramThermostatGear * thermostatGearObject = static_cast<const CtiLMProgramThermostatGear*>( currentGearObject );

                            // thermo constraints?? XXX

                            CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(*thermostatGearObject, defaultLMStartPriority, logMessage);
                            if ( requestMsg )   // valid request - signal the log message
                            {
                                multiDispatchMsg->insert( CTIDBG_new CtiSignalMsg( SYS_PID_LOADMANAGEMENT,
                                                                                   0,
                                                                                   logMessage,
                                                                                   currentLMGroup->getPAOName() + " / (" + CtiNumStr( currentLMGroup->getPAOId() ) + "): Thermostat Set Point",
                                                                                   LoadMgmtLogType,
                                                                                   SignalEvent,
                                                                                   "(yukon system)",
                                                                                   0,
                                                                                   defaultLMStartPriority - 1 ) );
                            }
                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                            CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName());
                        }
                    }
                }
                if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::SimpleThermostatRampingMethod) )
            {
                const bool sentMessage = sendSimpleThermostatMessage(currentGearObject, currentTime, multiPilMsg, multiDispatchMsg, expectedLoadReduced, false);
                if( sentMessage && getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
            else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::NoControlMethod) )
            {
                if( _LM_DEBUG & LM_DEBUG_EXTENDED )
                {
                    CTILOG_DEBUG(dout, "NO control gear, LM Program: " << getPAOName());
                }

                setProgramState(CtiLMProgramBase::NonControllingState);
            }
            else
            {
                CTILOG_INFO(dout, "Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method");
            }
        }
        else
        {
            CTILOG_INFO(dout, "Invalid current gear number: " << _currentgearnumber);
        }
    }
    else if( _lmprogramdirectgears.size() <= 0 )
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " doesn't have any gears");
    }
    else
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " doesn't have any groups");
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

    if( ciStringEqual(currentGearObject->getGroupSelectionMethod(), CtiLMProgramDirectGear::LastControlledSelectionMethod) )
    {
        BOOL found = FALSE;
        for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
        {
            CtiLMGroupPtr currentLMGroup  = *i;
            if( getLastGroupControlled() == currentLMGroup->getPAOId() )
            {
                currentLMGroup = (++i == _lmprogramdirectgroups.end()) ?
                                 _lmprogramdirectgroups[0] : *i;

                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() &&
                    ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                      ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::RotationMethod) ) )
                {
                    found = TRUE;
                    returnGroup = currentLMGroup;
                    returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                }
                break;
            }
        }

        if( !found )   //Are any of the groups candidates?
        {
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( !currentLMGroup->getDisableFlag() &&
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
    else if( ciStringEqual(currentGearObject->getGroupSelectionMethod(), CtiLMProgramDirectGear::AlwaysFirstGroupSelectionMethod) )
    {
        if( !ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::RotationMethod) )
        {
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( !currentLMGroup->getDisableFlag() &&
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
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
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
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( getLastGroupControlled() == currentLMGroup->getPAOId() )
                    {
                        i++;
                        if( i == _lmprogramdirectgroups.end() )
                        {
                            currentLMGroup = *(_lmprogramdirectgroups.begin());
                        }
                        else
                        {
                            currentLMGroup = *i;
                        }

                        if( !currentLMGroup->getDisableFlag() &&
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
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    found = TRUE;
                    returnGroup = currentLMGroup;
                    returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
                }
            }

            if( !found )
            {
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( !currentLMGroup->getDisableFlag() &&
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
    else if( ciStringEqual(currentGearObject->getGroupSelectionMethod(), CtiLMProgramDirectGear::LeastControlTimeSelectionMethod) )
    {
        // progressive lookup first look at current hours daily then current hours monthly
        // then current hours seasonal but not annually
        for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
        {
            CtiLMGroupPtr currentLMGroup  = *i;
            if( !currentLMGroup->getDisableFlag() &&
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

        if( returnGroup.get() != NULL )
        {
            // Mark the group so that it doesn't get picked again!
            returnGroup->setGroupControlState(CtiLMGroupBase::ActivePendingState);
        }
    }

    return returnGroup;
}

/**
 * Find a group to ramp out.  Assumes that groups are either active or inactive.
 * make sure to clean any active/inactive pending before calling this.!
 */
CtiLMGroupPtr CtiLMProgramDirect::findGroupToRampOut(CtiLMProgramDirectGear* lm_gear)
{
    CtiLMGroupPtr lm_group;
    if( lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutFIFOStopType ||
        lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutFIFORestoreStopType )
    {
        //Find the group that started controlling first and isn't ramping out
        CtiLMGroupPtr temp_lm_group;
        CtiTime first_control_time;
        for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
        {
            temp_lm_group = *i;
            if( temp_lm_group->getIsRampingOut() )
            {
                // Yikes.  Here is the deal.  Sometimes when a program needs to ramp out some of the
                // groups haven't started controlling yet.  This is fifo, but we need to prefer currently
                if( lm_group.get() == 0 ||
                    // Prefer active groups over inactive
                    (temp_lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                     lm_group->getGroupControlState() != CtiLMGroupBase::ActiveState) ||
                    // Prefer earlier control time if both are active
                    (temp_lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                     lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                     temp_lm_group->getControlStartTime() < lm_group->getControlStartTime()) ||
                    // Prefer earler control time if both are inactive
                    (temp_lm_group->getGroupControlState() != CtiLMGroupBase::ActiveState &&
                     lm_group->getGroupControlState() != CtiLMGroupBase::ActiveState &&
                     temp_lm_group->getControlStartTime() < lm_group->getControlStartTime()) )
                {
                    lm_group = temp_lm_group;
                }
            }
        }
    }
    else if( lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutRandomStopType ||
             lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutRandomRestoreStopType )
    {
        CtiLMGroupPtr temp_lm_group;
        int num_groups = _lmprogramdirectgroups.size();
        int j = rand() % num_groups;
        int k = 0;
        do //Look for a group that is not ramping out starting at a random index
        {
            temp_lm_group = _lmprogramdirectgroups[(j+k)%num_groups];
        } while( !temp_lm_group->getIsRampingOut() && k++ <= num_groups );

        if( k > num_groups )
        {
            CTILOG_INFO(dout, "LMProgram: " << getPAOName() << " Couldn't find a group to ramp out, all the program's groups are already ramped out.");
        }
        else
        {
            lm_group = temp_lm_group;
        }
    }
    else
    {
        CTILOG_ERROR(dout, "LMProgram: " << getPAOName() << " has an invalid StopOrderType, it is: " << lm_gear->getMethodStopType());
    }

    if( lm_group.get() != 0 )
    {
        lm_group->setIsRampingOut(false);
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "LM Group: " << lm_group->getPAOName() << " has ramped out");
        }
    }
    else
    {
        CTILOG_WARN(dout, "LMProgram: " << getPAOName() << " couldn't find a group to ramp out");
    }
    return lm_group;
}
/*---------------------------------------------------------------------------
    Hasgearchanged

    Returns a boolean that represents if the current gear for the program
    has changed because of duration, priority, or trigger offset.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasGearChanged(LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, CtiTime currentTime, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded)
{
    BOOL returnBoolean = FALSE;

    //The below block sees if the program needs to shift to a higher gear
    if( _currentgearnumber < (_lmprogramdirectgears.size()-1) )
    {
        CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();
        CtiLMProgramDirectGear* prevGearObject = 0;

        if( _currentgearnumber > 0 )
        {
            prevGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];
        }

        if( ciStringEqual(currentGearObject->getChangeCondition(), CtiLMProgramDirectGear::NoneChangeCondition) )
        {
            //returnBoolean = FALSE;
        }
        else if( ciStringEqual(currentGearObject->getChangeCondition(), CtiLMProgramDirectGear::DurationChangeCondition) )
        {
            LONG secondsControlling = currentTime.seconds() - getStartedControlling().seconds();
            if( getProgramState() != CtiLMProgramBase::InactiveState &&
                secondsControlling >= currentGearObject->getChangeDuration() &&
                _currentgearnumber+1 < _lmprogramdirectgears.size() )
            {
                setCurrentGearNumber(getCurrentGearNumber() + 1);
                {
                    string text("Duration Gear Change Up Program: ");
                    text += getPAOName();
                    string additional("Previous Gear: ");
                    additional += currentGearObject->getGearName();
                    additional += " New Gear: ";
                    additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
                }

                if( isControlling() )
                {
                    setChangeReason("Duration Gear Change");
                    recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
                }
                hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
                returnBoolean = TRUE;
            }
        }
        else if( ciStringEqual(currentGearObject->getChangeCondition(),CtiLMProgramDirectGear::PriorityChangeCondition) )
        {
            if( currentPriority >= currentGearObject->getChangePriority() &&
                _currentgearnumber+1 < _lmprogramdirectgears.size() )
            {
                setCurrentGearNumber(getCurrentGearNumber() + 1);
                {
                    string text("Priority Gear Change Up Program: ");
                    text += getPAOName();
                    string additional("Previous Gear: ");
                    additional += currentGearObject->getGearName();
                    additional += " New Gear: ";
                    additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
                }

                if( isControlling() )
                {
                    setChangeReason("Priority Gear Change");
                    recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
                }
                hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
                returnBoolean = TRUE;
            }
        }
        else if( ciStringEqual(currentGearObject->getChangeCondition(),CtiLMProgramDirectGear::TriggerOffsetChangeCondition) )
        {
            if( currentGearObject->getChangeTriggerNumber() > 0 &&
                currentGearObject->getChangeTriggerNumber() <= controlAreaTriggers.size() )
            {
                CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[currentGearObject->getChangeTriggerNumber()-1];

                if( isTriggerCheckNeeded )
                {
                    if( (trigger->getPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ||
                         trigger->getProjectedPointValue() >= (trigger->getThreshold() + currentGearObject->getChangeTriggerOffset()) ) &&
                        _currentgearnumber+1 < _lmprogramdirectgears.size() )
                    {
                        setCurrentGearNumber(getCurrentGearNumber() + 1);
                        {
                            string text("Trigger Offset Gear Change Up Program: ");
                            text += getPAOName();
                            string additional("Previous Gear: ");
                            additional += currentGearObject->getGearName();
                            additional += " New Gear: ";
                            additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                            multiDispatchMsg->insert(signal);
                            CTILOG_INFO(dout, text << ", " << additional);
                        }

                        if( isControlling() )
                        {
                            setChangeReason("Trigger Offset Gear Change");
                            recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
                        }
                        hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
                        returnBoolean = TRUE;
                    }
                    else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MasterCycleMethod) &&
                             prevGearObject != 0 &&
                             (trigger->getPointValue() < (trigger->getThreshold() + prevGearObject->getChangeTriggerOffset()) ||
                              (trigger->getProjectedPointValue() < (trigger->getThreshold() + prevGearObject->getChangeTriggerOffset()) && trigger->getProjectedPointValue() > 0)) )
                    {
                        setCurrentGearNumber(getCurrentGearNumber() - 1);

                        string text("Trigger Offset Gear Change Down Program: ");
                        text += getPAOName();
                        string additional("Previous Gear: ");
                        additional += currentGearObject->getGearName();
                        additional += " New Gear: ";
                        additional += prevGearObject->getGearName();
                        CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                        multiDispatchMsg->insert(signal);
                        CTILOG_INFO(dout, text << ", " << additional);

                        if( isControlling() )
                        {
                            setChangeReason("Trigger Offset Gear Change");
                            recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
                        }
                        hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
                        returnBoolean = TRUE;
                    }
                }
            }
            else if( !getManualControlReceivedFlag() )
            {
                CTILOG_INFO(dout, "Invalid ChangeTriggerNumber: " << currentGearObject->getChangeTriggerNumber() << ", trigger numbers start at 1 in program: " << getPAOName());
            }
        }
        else
        {
            CTILOG_INFO(dout, "Invalid current gear change condition: " << currentGearObject->getChangeCondition());
        }
    }
    else if( _currentgearnumber == (_lmprogramdirectgears.size()-1) )
    {
        //Already at highest gear!!!
    }
    else
    {
        CTILOG_INFO(dout, "Invalid current gear number: " << _currentgearnumber);
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
            _currentgearnumber <= (_lmprogramdirectgears.size()-1) )
        {
            CtiLMProgramDirectGear* previousGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber-1];

            if( ciStringEqual(previousGearObject->getChangeCondition(),CtiLMProgramDirectGear::DurationChangeCondition) )
            {
                //doesn't make sense to shift down from a duration shift up
                //returnBoolean = FALSE;
            }
            else if( ciStringEqual(previousGearObject->getChangeCondition(),CtiLMProgramDirectGear::PriorityChangeCondition) )
            {
                if( currentPriority < previousGearObject->getChangePriority() &&
                    _currentgearnumber-1 >= 0 )
                {
                    {
                        string text("Priority Gear Change Down Program: ");
                        text += getPAOName();
                        string additional("Previous Gear: ");
                        additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                        additional += " New Gear: ";
                        additional += previousGearObject->getGearName();
                        CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                        multiDispatchMsg->insert(signal);
                        CTILOG_INFO(dout, text << ", " << additional);
                    }
                    setCurrentGearNumber(getCurrentGearNumber() - 1);

                    if( isControlling() )
                    {
                        setChangeReason("Priority Gear Change");
                        recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
                    }
                    hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
                    returnBoolean = TRUE;
                }
            }
            else if( ciStringEqual(previousGearObject->getChangeCondition(),CtiLMProgramDirectGear::TriggerOffsetChangeCondition) )
            {
                if( previousGearObject->getChangeTriggerNumber() > 0 &&
                    previousGearObject->getChangeTriggerNumber() <= controlAreaTriggers.size() )
                {
                    CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[previousGearObject->getChangeTriggerNumber()-1];
                    if( isTriggerCheckNeeded &&
                        trigger->getPointValue() < (trigger->getThreshold() + previousGearObject->getChangeTriggerOffset()) &&
                        trigger->getProjectedPointValue() < (trigger->getThreshold() + previousGearObject->getChangeTriggerOffset()) &&
                        _currentgearnumber-1 >= 0 )
                    {
                        {
                            string text("Trigger Offset Gear Change Down Program: ");
                            text += getPAOName();
                            string additional("Previous Gear: ");
                            additional += ((CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber])->getGearName();
                            additional += " New Gear: ";
                            additional += previousGearObject->getGearName();
                            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                            multiDispatchMsg->insert(signal);
                            CTILOG_INFO(dout, text << ", " << additional);
                        }
                        setCurrentGearNumber(getCurrentGearNumber() - 1);

                        if( isControlling() )
                        {
                            setChangeReason("Trigger Offset Gear Change");
                            recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
                        }
                        hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded);
                        returnBoolean = TRUE;
                    }
                }
                else if( !getManualControlReceivedFlag() )
                {
                    CTILOG_INFO(dout, "Invalid ChangeTriggerNumber: " << previousGearObject->getChangeTriggerNumber() << ", trigger numbers start at 1 in program: " << getPAOName());
                }
            }
            else if( ciStringEqual(previousGearObject->getChangeCondition(),CtiLMProgramDirectGear::NoneChangeCondition) )
            {
                //This will only happen on a manual gear change, how else would you get
                //to a higher gear if the previous change condition was none
                //returnBoolean = FALSE;
            }
            else
            {
                CTILOG_INFO(dout, "Invalid current gear change condition: " << previousGearObject->getChangeCondition());
            }
        }
        else if( _currentgearnumber == 0 )
        {
            //Already at lowest gear!!!
        }
        else
        {
            CTILOG_INFO(dout, "Invalid current gear number: " << _currentgearnumber);
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
BOOL CtiLMProgramDirect::maintainProgramControl(LONG currentPriority, vector<CtiLMControlAreaTrigger*>& controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isPastMinResponseTime, BOOL isTriggerCheckNeeded)
{
    BOOL returnBoolean = FALSE;
    LONG previousGearNumber = _currentgearnumber;
    if( !isWithinValidControlWindow(secondsFromBeginningOfDay) )
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "LM Program: " << getPAOName() << " is no longer in a valid control window, stopping program control");
        }
        setChangeReason("Control Window Stop");
        if( stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, currentTime) )
        {
            // Let the world know we just auto stopped?
            scheduleStopNotification(CtiTime());
        }
        returnBoolean = TRUE;
    }
    else
    {
        if( isPastMinResponseTime &&
            hasGearChanged(currentPriority, controlAreaTriggers, currentTime, multiDispatchMsg, isTriggerCheckNeeded) )
        {
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "Gear Change, LM Program: " << getPAOName() << ", previous gear number: " << previousGearNumber << ", new gear number: " << _currentgearnumber);
            }
            returnBoolean = ( 0.0 > updateProgramControlForAutomaticGearChange(currentTime, previousGearNumber, multiPilMsg, multiDispatchMsg));
        }
        else
        {
            if( refreshStandardProgramControl(currentTime, multiPilMsg, multiDispatchMsg) )
            {
                returnBoolean = TRUE;
            }
        }
    }


    return returnBoolean;
}

/*---------------------------------------------------------------------------
    updateProgramControlForAutomaticGearChange

    Handles the changing of gears within a running program by sending pil
    requests to change the type of shed or cycle depending on the original
    gear control method and the new gear method.

    Manual control does not use this but simply sends out the new commands
    as if a new start message was sent.
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramDirect::updateProgramControlForAutomaticGearChange(CtiTime currentTime, LONG previousGearNumber, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    DOUBLE expectedLoadReduced = 0.0;

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();
    CtiLMProgramDirectGear* previousGearObject = NULL;

    if( previousGearNumber < _lmprogramdirectgears.size() )
    {
        previousGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[previousGearNumber];
    }

    if( currentGearObject != NULL && previousGearObject != NULL && _lmprogramdirectgroups.size() > 0 )
    {
        if( SmartGearBase *smartGearObject = dynamic_cast<SmartGearBase *>(currentGearObject) )
        {
            LONG shedTime = getDirectStopTime().seconds() - CtiTime::now().seconds();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "Gear change for all groups, LM Program: " << getPAOName());
            }

            int numberOfActiveGroups = 0;
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                // .checkControl below can modify (shorten) the shed time
                CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                {
                    if( smartGearObject->attemptControl(currentLMGroup, shedTime, expectedLoadReduced) )
                    {
                        setLastControlSent(CtiTime());
                    }
                }
                else
                {
                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                        {
                            CTILOG_DEBUG(dout, *violations);
                        }
                    }
                }

                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    numberOfActiveGroups++;
                }
            }

            updateStandardControlActiveState(numberOfActiveGroups);
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TimeRefreshMethod) )
        {
            if( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::SmartCycleMethod) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                ciStringEqual(previousGearObject->getControlMethod(), CtiLMProgramDirectGear::MagnitudeCycleMethod) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::MasterCycleMethod) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::RotationMethod) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::ThermostatRampingMethod) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::LatchingMethod) ||
                ( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod) &&
                  getManualControlReceivedFlag() ) )
            {
                // Normally we would only take the commented out "numberOfGroupsToTake" but when we
                // switch gears to refresh from smart cycle or rotation or a manually controlled non control
                // there is the possibility that all groups need to be shed so that's what we'll do
                LONG refreshRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();

                string refreshCountDownType = currentGearObject->getMethodOptionType();
                LONG maxRefreshShedTime = currentGearObject->getMethodOptionMax();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Time Refreshing all previously controlled groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() &&
                        ( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState ||
                          ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::RotationMethod ) ) )
                    {
                        if( ciStringEqual(refreshCountDownType, CtiLMProgramDirectGear::DynamicShedTimeMethodOptionType) )
                        {
                            if( maxRefreshShedTime > 0 )
                            {
                                ULONG tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();

                                if( maxRefreshShedTime > 0 &&
                                    tempShedTime > maxRefreshShedTime )
                                {
                                    tempShedTime = maxRefreshShedTime;
                                }
                                shedTime = tempShedTime;
                            }
                            else
                            {
                                CtiTime tempDateTime;
                                CtiDate tempDate = tempDateTime.date();
                                CtiTime compareDateTime(tempDate,0,0,0);
                                compareDateTime.addDays(1);
                                ULONG tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();

                                if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                {
                                    tempShedTime = compareDateTime.seconds() - CtiTime().seconds();
                                }
                                else
                                {
                                    tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();
                                }

                                shedTime = tempShedTime;
                            }
                        }
                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        int oldShedTime = shedTime;
                        if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                        {
                            if( refreshCountDownType == CtiLMProgramDirectGear::FixedCountMethodOptionType && shedTime != oldShedTime )
                            {
                                shedTime = oldShedTime;
                            }
                            CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                            refreshGroupControl(currentLMGroup, requestMsg, multiPilMsg);

                            //Set this group to refresh again
                            currentLMGroup->setNextRefreshTime(CtiTime(), refreshRate);
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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                }
                if( getProgramState() == CtiLMProgramBase::ActiveState ||
                    getProgramState() == CtiLMProgramBase::NonControllingState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                    for( CtiLMGroupIter j = _lmprogramdirectgroups.begin(); j != _lmprogramdirectgroups.end(); j++ )
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
            else if( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::TimeRefreshMethod ) ||
                     ( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod ) &&
                       !getManualControlReceivedFlag() ) )
            {
                LONG refreshRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();
                LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();
                string refreshCountDownType = currentGearObject->getMethodOptionType();
                LONG maxRefreshShedTime = currentGearObject->getMethodPeriod();

                if( numberOfGroupsToTake == 0 )
                {
                    numberOfGroupsToTake = _lmprogramdirectgroups.size();
                }

                int groups_taken = 0;
                do
                {
                    CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup.get() == NULL )   // No more groups to take, get outta here
                    {
                        CTILOG_INFO(dout, "Program: " << getPAOName() << " couldn't find any groups to take");
                        break;
                    }
                    else
                    {
                        if( ciStringEqual(refreshCountDownType, CtiLMProgramDirectGear::DynamicShedTimeMethodOptionType) ||
                            ciStringEqual(refreshCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )
                        {
                            if( maxRefreshShedTime > 0 )
                            {
                                ULONG tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();

                                if( maxRefreshShedTime > 0 &&
                                    tempShedTime > maxRefreshShedTime )
                                {
                                    tempShedTime = maxRefreshShedTime;
                                }
                                shedTime = tempShedTime;
                            }
                            else
                            {
                                CtiTime tempDateTime;
                                CtiDate tempDate = tempDateTime.date();
                                CtiTime compareDateTime(tempDate,0,0,0);
                                compareDateTime.addDays(1);
                                ULONG tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();

                                if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                {
                                    tempShedTime = compareDateTime.seconds() - CtiTime().seconds();
                                }
                                else
                                {
                                    tempShedTime = getDirectStopTime().seconds() - CtiTime().seconds();
                                }

                                shedTime = tempShedTime;
                            }
                        }
                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        int oldShedTime = shedTime;
                        if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                        {
                            if( refreshCountDownType == CtiLMProgramDirectGear::FixedCountMethodOptionType && shedTime != oldShedTime )
                            {
                                shedTime = oldShedTime;
                            }
                            groups_taken++;
                            CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(refreshRate, shedTime, defaultLMStartPriority);
                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

                            //Set this group to refresh again
                            currentLMGroup->setNextRefreshTime(CtiTime(), refreshRate);
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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                } while( groups_taken < numberOfGroupsToTake );

                setPendingGroupsInactive();

                if( getProgramState() == CtiLMProgramBase::ActiveState ||
                    getProgramState() == CtiLMProgramBase::NonControllingState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);

                    for( CtiLMGroupIter j = _lmprogramdirectgroups.begin(); j != _lmprogramdirectgroups.end(); j++ )
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
                CTILOG_INFO(dout, "Program: " << getPAOName() << ", Gear#: " << previousGearObject->getGearNumber() << " doesn't have a valid control method");
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::SmartCycleMethod ) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            string cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "Smart Cycling all groups, LM Program: " << getPAOName());
            }
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    cycleCount = currentGearObject->getMethodRateCount();
                    if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::CountDownMethodOptionType ) )
                    {
                        // group constraints will adjust counts down...!?
                    }
                    else if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::LimitedCountDownMethodOptionType ) )//can't really do anything for limited count down on start up
                    {
                    }//we have to send the default because it is programmed in the switch

                    CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                    if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, true) )
                    {
                        bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                        CtiRequestMsg* requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                        startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                            {
                                CTILOG_DEBUG(dout, *violations);
                            }
                        }
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MasterCycleMethod ) )
        {
            expectedLoadReduced = 0.0;

            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
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
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::RotationMethod ) )
        {

            if( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::TimeRefreshMethod ) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod ) )
            {
                LONG sendRate = currentGearObject->getMethodRate();
                LONG shedTime = currentGearObject->getMethodPeriod();

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Controlling all rotation groups, LM Program: " << getPAOName());
                }
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                        {
                            CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);

                            setLastGroupControlled(currentLMGroup->getPAOId());
                            refreshGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                }
            }
            else if( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::SmartCycleMethod) ||
                     ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                     ciStringEqual(previousGearObject->getControlMethod(), CtiLMProgramDirectGear::MagnitudeCycleMethod) )   // Stop the groups from cycling, before we start doing rotation on them
            {
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    stopCycleGroup(currentTime, currentLMGroup,  multiPilMsg, currentGearObject->getMethodPeriod(), 0);
                }
            }

            // On a gear upshift from rotation to rotation we want to control some load immediately, however,
            // On a downshift, we want to do nothing but continue rotation, but with the new rotation settings.
            // Added for northern plains 11/05/04
            if( (ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::RotationMethod ) &&
                 getCurrentGearNumber() > previousGearNumber ) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::SmartCycleMethod ) ||
                ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod ) ||
                ciStringEqual(previousGearObject->getControlMethod(), CtiLMProgramDirectGear::MagnitudeCycleMethod) )
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
                    CTILOG_DEBUG(dout, "Controlling rotation groups, LM Program: " << getPAOName() << ", number of groups to control: " << numberOfGroupsToTake);
                }

                int groups_taken = 0;
                do
                {

                    CtiLMGroupPtr currentLMGroup = findGroupToTake(currentGearObject);
                    if( currentLMGroup.get() == NULL )   // No more groups to take, get outta here
                    {
                        CTILOG_INFO(dout, "Program: " << getPAOName() << " couldn't find any groups to take");
                        break;
                    }
                    else
                    {
                        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                        if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                        {
                            groups_taken++;
                            CtiRequestMsg* requestMsg = currentLMGroup->createRotationRequestMsg(sendRate, shedTime, defaultLMStartPriority);

                            setLastGroupControlled(currentLMGroup->getPAOId());
                            startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                } while( groups_taken < numberOfGroupsToTake );

                setPendingGroupsInactive();

                if( getProgramState() != CtiLMProgramBase::ManualActiveState )
                {
                    setProgramState(CtiLMProgramBase::FullyActiveState);
                }
            }
        } // End switching to rotation gear
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::LatchingMethod ) )
        {
            CTILOG_INFO(dout, "Gear Control Method: " << getPAOName() << " Gear#: " << currentGearObject->getGearNumber() << " control method can't support gear changes. ");
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                 ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MagnitudeCycleMethod) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            string cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "True Cycling all groups, LM Program: " << getPAOName());
            }
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    cycleCount = currentGearObject->getMethodRateCount();
                    if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::CountDownMethodOptionType ) )
                    {
                        // group constraints will adjust counts down...!?
                    }
                    else if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::LimitedCountDownMethodOptionType ) )//can't really do anything for limited count down on start up
                    {
                    }//we have to send the default because it is programmed in the switch

                    CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                    if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, true) )
                    {
                        CtiRequestMsg* requestMsg = NULL;
                        bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                        if( isExpresscomGroup(currentLMGroup->getPAOType()) || currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305 )
                        {
                            requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                        }
                        else
                        {
                            requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                            CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName());
                        }
                        startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                            {
                                CTILOG_DEBUG(dout, *violations);
                            }
                        }
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TargetCycleMethod) )
        {
            DOUBLE kw = currentGearObject->getKWReduction();
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            string cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "True Cycling all groups, LM Program: " << getPAOName());
            }
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    cycleCount = currentGearObject->getMethodRateCount();
                    if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear ::CountDownMethodOptionType) )
                    {
                        // group constraints will adjust counts down...!?
                    }
                    else if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )//can't really do anything for limited count down on start up
                    {
                    }//we have to send the default because it is programmed in the switch

                    CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                    if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, true) )
                    {
                        CtiRequestMsg* requestMsg = NULL;
                        bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                        if( isExpresscomGroup(currentLMGroup->getPAOType()) || currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305 )
                        {
                            requestMsg = currentLMGroup->createTargetCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority, kw, getStartedControlling(), getAdditionalInfo());
                        }
                        else
                        {
                            requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                            CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Target Cycle a non-Expresscom group: " << currentLMGroup->getPAOName());
                        }
                        startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

                        expectedLoadReduced += kw;
                    }
                    else
                    {
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                            {
                                CTILOG_DEBUG(dout, *violations);
                            }
                        }
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod) )
        {
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "Thermostat Set Point command all groups, LM Program: " << getPAOName());
            }
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( !currentLMGroup->getDisableFlag() &&
                    !currentLMGroup->getControlInhibit() )
                {
                    if( isExpresscomGroup(currentLMGroup->getPAOType()) )
                    {
                        std::string logMessage;
                        const CtiLMProgramThermostatGear * thermostatGearObject = static_cast<const CtiLMProgramThermostatGear*>( currentGearObject );

                        CtiRequestMsg* requestMsg = currentLMGroup->createSetPointRequestMsg(*thermostatGearObject, defaultLMStartPriority, logMessage);
                        if ( requestMsg )   // valid request - signal the log message
                        {
                            multiDispatchMsg->insert( CTIDBG_new CtiSignalMsg( SYS_PID_LOADMANAGEMENT,
                                                                               0,
                                                                               logMessage,
                                                                               currentLMGroup->getPAOName() + " / (" + CtiNumStr( currentLMGroup->getPAOId() ) + "): Thermostat Set Point",
                                                                               LoadMgmtLogType,
                                                                               SignalEvent,
                                                                               "(yukon system)",
                                                                               0,
                                                                               defaultLMStartPriority - 1 ) );
                        }
                        startGroupControl(currentLMGroup, requestMsg, multiPilMsg);

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
                        CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName());
                    }
                }
            }
            if( getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod ) )
        {
            if( _LM_DEBUG & LM_DEBUG_EXTENDED )
            {
                CTILOG_DEBUG(dout, "Entering NO control gear, LM Program: " << getPAOName());
            }

            string tempControlMethod = previousGearObject->getControlMethod();
            string tempMethodStopType = previousGearObject->getMethodStopType();

            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                CtiTime now;
                if( now.seconds() >
                    currentLMGroup->getControlStartTime().seconds() + getMinActivateTime() ||
                    getManualControlReceivedFlag() )
                {
                    if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::SmartCycleMethod ) ||
                        ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::TrueCycleMethod ) ||
                        ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::MagnitudeCycleMethod) )
                    {
                        if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RestoreStopType ) )
                        {
                            restoreGroup(currentTime, currentLMGroup, multiPilMsg, previousGearObject->getStopRepeatCount());
                        }
                        else if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::StopCycleStopType ) ||
                                 ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::TimeInStopType ) ||
                                 ciStringEqual(tempMethodStopType,"Time-In" ) )//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                        {
                            stopCycleGroup(currentTime, currentLMGroup, multiPilMsg, currentGearObject->getMethodPeriod(), previousGearObject->getStopRepeatCount());
                        }
                        else
                        {
                            CTILOG_INFO(dout, "Invalid current gear method stop type: " << tempMethodStopType);
                        }
                    }
                    else if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::TimeRefreshMethod ) ||
                             ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::MasterCycleMethod ) ||
                             ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::RotationMethod ) ||
                             ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::ThermostatRampingMethod ) )
                    {
                        if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RestoreStopType ) )
                        {
                            restoreGroup(currentTime, currentLMGroup, multiPilMsg, previousGearObject->getStopRepeatCount());
                        }
                        else if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::TimeInStopType ) || ciStringEqual(tempMethodStopType,"Time-In" ) )
                        {
                            //"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way

                            //I don't know if I should do anything unique here yet?
                            //multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), "control terminate"));
                            //setLastControlSent(CtiTime());
                            //currentLMGroup->setLastControlSent(CtiTime());
                            CtiTime timeToTimeIn = gInvalidCtiTime; //put in a bogus time stamp
                            if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::MasterCycleMethod ) )
                            {
                                timeToTimeIn = currentLMGroup->getLastControlSent();
                                LONG offTimeInSeconds = previousGearObject->getMethodPeriod() * (previousGearObject->getMethodRate() / 100.0);
                                timeToTimeIn.addMinutes(offTimeInSeconds/60);
                            }
                            else if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::ThermostatRampingMethod) )
                            {
                                timeToTimeIn = currentLMGroup->getLastControlSent();
                                const CtiLMProgramThermostatGear * thermostatGear = static_cast<const CtiLMProgramThermostatGear*>( previousGearObject );

                                int minutesToAdd = 0;
                                minutesToAdd += (thermostatGear->getRandom()/2+thermostatGear->getRandom()%2);
                                minutesToAdd += thermostatGear->getDelayTime();
                                minutesToAdd += thermostatGear->getPrecoolTime();
                                minutesToAdd += thermostatGear->getPrecoolHoldTime();
                                minutesToAdd += thermostatGear->getControlTime();
                                minutesToAdd += thermostatGear->getControlHoldTime();
                                minutesToAdd += thermostatGear->getRestoreTime();
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
                            CTILOG_INFO(dout, "Invalid current gear method stop type: " << tempMethodStopType);
                        }
                    }
                    else if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::LatchingMethod ) )
                    {
                        LONG gearStartRawState = currentGearObject->getMethodRateCount();
                        if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                        {
                            multiPilMsg->insert( currentLMGroup->createLatchingRequestMsg( false, defaultLMStartPriority ) );
                        }
                        else
                        {
                            multiDispatchMsg->insert( currentLMGroup->createLatchingCommandMsg(previousGearObject->getMethodRateCount()?0:1, defaultLMStartPriority) );
                        }

                        setLastControlSent(CtiTime());
                        currentLMGroup->setLastControlSent(CtiTime());
                    }
                    else if( ciStringEqual(previousGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod ) )
                    {
                        // Its not controlling so a stop method doesn't much matter, does it?
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Invalid current gear control method: " << tempControlMethod);
                    }

                    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                    currentLMGroup->setControlStartTime(gInvalidCtiTime);
                }
                else
                {
                    currentLMGroup->setGroupControlState(CtiLMGroupBase::InactivePendingState);
                    CTILOG_INFO(dout, "Group cannot be set inactive yet because of minimum active time Group: " << currentLMGroup->getPAOName());
                }
            }

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::NonControllingState);
        }
        else
        {
            CTILOG_INFO(dout, "Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method");
        }
    }
    else if( previousGearObject == NULL )
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " previousGearObject == NULL");
    }
    else if( currentGearObject == NULL )
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " currentGearObject == NULL");
    }
    else
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " doesn't have any groups");
    }

    return expectedLoadReduced;
}


/*
*/
BOOL CtiLMProgramDirect::stopOverControlledGroup(CtiLMProgramDirectGear* currentGearObject, CtiLMGroupPtr& currentLMGroup, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    {
        char tempchar[80];
        string text =  ("Stopping Control Group: ");
        text += currentLMGroup->getPAOName();
        string additional =  ("Reason: Exceeded Control Time Limit");
        additional += " PAO Id: ";
        _ltoa(currentLMGroup->getPAOId(),tempchar,10);
        additional += tempchar;

        multiDispatchMsg->insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
        CTILOG_INFO(dout, text << ", " << additional);
    }
    string tempMethodStopType = currentGearObject->getMethodStopType();
    if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RestoreStopType ) )
    {
        restoreGroup(currentTime, currentLMGroup, multiPilMsg, currentGearObject->getStopRepeatCount());
    }
    else if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::StopCycleStopType ) ||
             ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::TimeInStopType ) ||
             ciStringEqual(tempMethodStopType,"Time-In" ) )//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
    {
        stopCycleGroup(currentTime, currentLMGroup, multiPilMsg, currentGearObject->getMethodPeriod(), currentGearObject->getStopRepeatCount());
    }
    else
    {
        CTILOG_INFO(dout, "Invalid current gear method stop type: " << tempMethodStopType);
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
        CTILOG_DEBUG(dout, "sending notification of direct program start. Program: " << getPAOName());
    }

    // If the notify time is longer than say a month from now then we must never
    // be stopping
    CtiTime now;
    if( getNotifyActiveTime().seconds() > (now.seconds() + 60*60*24*30) )
    {
        return notifyGroups(CtiNotifLMControlMsg::STARTING_NEVER_STOP, multiNotifMsg);
    }
    else
    {
        return notifyGroups(CtiNotifLMControlMsg::STARTING, multiNotifMsg);
    }
}

bool CtiLMProgramDirect::notifyGroupsOfAdjustment(CtiMultiMsg* multiNotifMsg)
{
    if( _LM_DEBUG & LM_DEBUG_DIRECT_NOTIFY )
    {
        CTILOG_DEBUG(dout, "sending notification of direct program adjustment. Program: " << getPAOName());
    }

    setAdjustNotificationPending(false);
    return notifyGroups(CtiNotifLMControlMsg::UPDATING, multiNotifMsg);
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
        CTILOG_DEBUG(dout, "sending notification of direct program stop. Program: " << getPAOName());
    }

    return notifyGroups(CtiNotifLMControlMsg::FINISHING, multiNotifMsg);
}

/*----------------------------------------------------------------------------
notifyGroupsOfSchedule

Let the notification groups know when we have scheduled a start program
Returns true if a notifcation was sent.
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::notifyGroupsOfSchedule(const CtiTime &start, const CtiTime &stop)
{
    if ( shouldNotifyWhenScheduled() ) 
    {
        if ( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "sending notification of scheduled program start. Program: " << getPAOName());
        }

        auto multiNotifMsg = std::make_unique<CtiMultiMsg>();
        auto notif_msg = std::make_unique<CtiNotifLMControlMsg>(_notificationgroupids, CtiNotifLMControlMsg::SCHEDULING, getPAOId(), start, stop);
        multiNotifMsg->insert(notif_msg.release());

        CtiLoadManager::getInstance()->sendMessageToNotification(std::move(multiNotifMsg));

        return true;
    }
    else
    {
        return false;
    }
}

BOOL  CtiLMProgramDirect::wasControlActivatedByStatusTrigger()
{
    return _controlActivatedByStatusTrigger;
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
BOOL CtiLMProgramDirect::refreshStandardProgramControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    BOOL returnBoolean = FALSE;
    LONG numberOfActiveGroups = 0;
    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL && _lmprogramdirectgroups.size() > 0 )
    {
        if( SmartGearBase *smartGearObject = dynamic_cast<SmartGearBase *>(currentGearObject) )
        {
            for each( CtiLMGroupPtr currentLMGroup in _lmprogramdirectgroups )
            {
                if( currentLMGroup->readyToControlAt(currentTime) )
                {
                    LONG shedTime = getDirectStopTime().seconds() - CtiTime::now().seconds();

                    // .checkControl below can modify (shorten) the shed time
                    CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                    if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                    {
                        double expectedLoadReduced; // Apparently unused in refreshStandardProgramControl.
                        if( smartGearObject->attemptControl(currentLMGroup, shedTime, expectedLoadReduced) )
                        {
                            setLastControlSent(CtiTime());
                        }
                    }
                    else
                    {
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                            {
                                CTILOG_DEBUG(dout, *violations);
                            }
                        }
                    }
                }

                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    numberOfActiveGroups++;
                }
            }

            updateStandardControlActiveState(numberOfActiveGroups);
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TimeRefreshMethod) )
        {
            long refresh_rate = currentGearObject->getMethodRate();
            long shed_time = currentGearObject->getMethodPeriod();
            string refresh_count_down_type = currentGearObject->getMethodOptionType();
            long max_refresh_shed_time = currentGearObject->getMethodPeriod();

            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr lm_group  = *i;
                //Check active groups to see if they should stop being controlled

                /* Lets see if new constraint handling can replace stopovercontrolledgroup
                   It wasn't really causing a problem, but it doesn't recognize the rampxxx stop
                   types - and it is another control path... do we reallly need to send out restores?
                   :consider:
                   if(lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                   !getManualControlReceivedFlag() && !doesGroupHaveAmpleControlTime(lm_group,0))
                {
                    returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, lm_group, currentTime, multiPilMsg, multiDispatchMsg));
                }
                //Check to see if any groups are ready to be refreshed to ramped in
                else
                */
                if( lm_group->readyToControlAt(currentTime) &&
                    (!getIsRampingOut() || (getIsRampingOut() && lm_group->getIsRampingOut())) ) //if the program is ramping out, then only refresh if this group is stillr amping out
                {
                    // Reset shed time - it could have been modified by constraint process
                    shed_time = currentGearObject->getMethodPeriod();
                    if( refresh_count_down_type == CtiLMProgramDirectGear::CountDownMethodOptionType.c_str() )
                    {
                        if( getManualControlReceivedFlag() )
                        {
                            if( max_refresh_shed_time > 0 )   // Don't let this group control more than its max shed time
                            {
                                unsigned long calc_shed_time = getDirectStopTime().seconds() - CtiTime().seconds();
                                shed_time = ( max_refresh_shed_time > 0 ?
                                              std::min((unsigned)max_refresh_shed_time, (unsigned)calc_shed_time) :
                                              calc_shed_time );

                            }
                            else   // Don't let the shed time span tomorrow (why?)
                            {
                                CtiTime now;
                                CtiDate tempDate = now.date();
                                CtiTime tomorrow(tempDate,0,0,0);
                                tomorrow.addDays(1);
                                shed_time = std::min(tomorrow.seconds() - now.seconds(), getDirectStopTime().seconds() - now.seconds());
                            }
                        }

                        if( !getManualControlReceivedFlag() )
                        {
                            shed_time = calculateGroupControlTimeLeft(lm_group, shed_time);
                        }
                    } //end countdownmethod

                    CtiLMGroupConstraintChecker con_checker(*this, lm_group, currentTime);
                    int oldShedTime = shed_time;
                    if( getConstraintOverride() || con_checker.checkControl(shed_time, true) ) //adjust duration should follow fixed shed time?
                    {
                        if( refresh_count_down_type == CtiLMProgramDirectGear::FixedCountMethodOptionType && shed_time != oldShedTime )
                        {
                            shed_time = oldShedTime;
                        }

                        CtiRequestMsg* requestMsg = lm_group->createTimeRefreshRequestMsg( refresh_rate, shed_time, defaultLMRefreshPriority );

                        // If this group is inactive then we must be starting control...?
                        // This is important so that max control duration type constraints work -
                        // If this group is ramping in, lets start control,
                        // otherwise it is just a regular refresh
                        if( lm_group->getGroupControlState() == CtiLMGroupBase::InactiveState ||
                            lm_group->getIsRampingIn() )
                        {
                            startGroupControl( lm_group, requestMsg, multiPilMsg );
                        }
                        else
                        {
                            refreshGroupControl( lm_group, requestMsg, multiPilMsg );
                        }
                        returnBoolean = TRUE;
                    }
                    else
                    {
                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                            {
                                CTILOG_DEBUG(dout, *violations);
                            }
                        }
                    }
                    // Even if this group violated constraints we want to give it another
                    // chance later so set this group to refresh again
                    lm_group->setNextRefreshTime(currentTime, refresh_rate);

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_DEBUG(dout, "LMProgram: " << getPAOName() << ",  time refresh " << lm_group->getPAOName() << " next at: " << lm_group->getNextControlTime().asString());
                    }

                } //end refreshing or ramping in a group
                else if( lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                         lm_group->getNextControlTime() == gInvalidCtiTime ) // This group is active but has no next control time! we need to give it one.
                {
                    lm_group->setNextControlTime(CtiTime(CtiTime(lm_group->getLastControlSent().seconds() + refresh_rate)));
                    returnBoolean = TRUE;
                }
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::SmartCycleMethod ) ||
                 ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod ) ||
                 ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MagnitudeCycleMethod) )
        {
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            LONG cycleRefreshRate = currentGearObject->getCycleRefreshRate();
            string cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( cycleCount == 0 )
            {
                cycleCount = 8;//seems like a reasonable default
            }

            if( period != 0 )
            {
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    CtiTime periodEnd;
                    if( cycleRefreshRate == 0 )
                    {
                        periodEnd = currentLMGroup->getLastControlSent()+(period * cycleCount)+1;
                    }
                    else
                    {
                        periodEnd = currentLMGroup->getLastControlSent()+cycleRefreshRate;
                    }

                    if( currentTime >= periodEnd &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was lower
                        cycleCount = currentGearObject->getMethodRateCount();
                        // For limited count down we might change this!
                        bool adjust_counts = true;
                        if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::CountDownMethodOptionType) )   // We only want to reduce the count when we know when control is ending
                        {
                            // this is only true with manual and timed control
                            // xxx adjust for end of control window also!!! not just manual timed!
                            // note about above, group constraints should fix program ctrl window xxx
                            if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                getProgramState() == CtiLMProgramBase::TimedActiveState )
                            {
                                if( maxCycleCount > 0 || cycleCount == 0 )
                                {
                                    LONG tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
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
                                    CtiTime tempDateTime;
                                    CtiDate tempDate = tempDateTime.date();
                                    CtiTime compareDateTime(tempDate,0,0,0);
                                    compareDateTime.addDays(1);
                                    LONG tempCycleCount = cycleCount;
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempCycleCount = (compareDateTime.seconds() - CtiTime().seconds()) / period;
                                        if( ((compareDateTime.seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                    else
                                    {
                                        tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }

                                    if( tempCycleCount > 63 )//Versacom can't support counts higher than 63
                                    {
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
                                // group constraints will adjust counts down...!?
                            }
                        }
                        else if( ciStringEqual(cycleCountDownType,CtiLMProgramDirectGear::LimitedCountDownMethodOptionType ) )
                        {
                            adjust_counts = false;
                            LONG cycleLength = period * cycleCount;
                            LONG estimatedControlTimeInSeconds = cycleLength * (((double)percent)/100.0);
                            if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                getProgramState() == CtiLMProgramBase::TimedActiveState )
                            {
                                ULONG secondsAtLastControl = currentLMGroup->getLastControlSent().seconds();
                                if( (secondsAtLastControl + cycleLength) >= (getDirectStopTime().seconds()-120) )//if the last control sent is not within 2 minutes before the stop control time a refresh will be sent
                                {
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

                        if( cycleCount > 0 )
                        {
                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, percent, adjust_counts) )
                            {
                                CtiRequestMsg* requestMsg = NULL;
                                bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                                if( (ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                                     ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MagnitudeCycleMethod)) &&
                                    (isExpresscomGroup(currentLMGroup->getPAOType()) ||
                                     currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305) )
                                {
                                    requestMsg = currentLMGroup->createTrueCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                }
                                else
                                {
                                    requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                    if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::TrueCycleMethod) ||
                                        ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MagnitudeCycleMethod) )
                                    CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not True Cycle a non-Expresscom group: " << currentLMGroup->getPAOName());
                                }
                                refreshGroupControl(currentLMGroup, requestMsg, multiPilMsg);
                                returnBoolean = TRUE;
                            }
                            else
                            {
                                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                                {
                                    if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                    {
                                        CTILOG_DEBUG(dout, *violations);
                                    }
                                }
                            }
                        }
                        else
                        {
                            if( !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                  getProgramState() == CtiLMProgramBase::TimedActiveState) &&
                                !doesGroupHaveAmpleControlTime(currentLMGroup,0) &&
                                currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )//we need to restore the group in the way set in the gear because it went over max control time
                            {
                                // :consider: is this needed??
                                returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, currentTime, multiPilMsg, multiDispatchMsg));
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
                        // :consider: is this needed??
                        returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, currentTime, multiPilMsg, multiDispatchMsg));
                    }

                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                    {
                        numberOfActiveGroups++;
                    }
                }

                if( returnBoolean )
                    updateStandardControlActiveState(numberOfActiveGroups);
            }
            else
            {
                CTILOG_INFO(dout, "Tried to divide by zero");
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TargetCycleMethod) )
        {
            DOUBLE kw = currentGearObject->getKWReduction();
            LONG percent = currentGearObject->getMethodRate();
            LONG period = currentGearObject->getMethodPeriod();
            LONG cycleCount = currentGearObject->getMethodRateCount();
            LONG cycleRefreshRate = currentGearObject->getCycleRefreshRate();
            string cycleCountDownType = currentGearObject->getMethodOptionType();
            LONG maxCycleCount = currentGearObject->getMethodOptionMax();

            if( cycleCount == 0 )
            {
                cycleCount = 8;//seems like a reasonable default
            }

            if( period != 0 )
            {
                for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
                {
                    CtiLMGroupPtr currentLMGroup  = *i;
                    CtiTime periodEnd;
                    if( cycleRefreshRate == 0 )
                    {
                        periodEnd = currentLMGroup->getLastControlSent()+(period * cycleCount)+1;
                    }
                    else
                    {
                        periodEnd = currentLMGroup->getLastControlSent()+cycleRefreshRate;
                    }

                    if( currentTime >= periodEnd &&
                        !currentLMGroup->getDisableFlag() &&
                        !currentLMGroup->getControlInhibit() )
                    {
                        //reset the default for each group if the previous groups was lower
                        cycleCount = currentGearObject->getMethodRateCount();
                        // For limited count down we might change this!
                        bool adjust_counts = true;
                        if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::CountDownMethodOptionType) )   // We only want to reduce the count when we know when control is ending
                        {
                            // this is only true with manual and timed control
                            // xxx adjust for end of control window also!!! not just manual timed!
                            // note about above, group constraints should fix program ctrl window xxx
                            if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                getProgramState() == CtiLMProgramBase::TimedActiveState )
                            {
                                if( maxCycleCount > 0 || cycleCount == 0 )
                                {
                                    LONG tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                    if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
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
                                    CtiTime tempDateTime;
                                    CtiDate tempDate = tempDateTime.date();
                                    CtiTime compareDateTime(tempDate,0,0,0);
                                    compareDateTime.addDays(1);
                                    LONG tempCycleCount = cycleCount;
                                    if( getDirectStopTime().seconds() > compareDateTime.seconds() )
                                    {
                                        tempCycleCount = (compareDateTime.seconds() - CtiTime().seconds()) / period;
                                        if( ((compareDateTime.seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }
                                    else
                                    {
                                        tempCycleCount = (getDirectStopTime().seconds() - CtiTime().seconds()) / period;
                                        if( ((getDirectStopTime().seconds() - CtiTime().seconds()) % period) > 0 )
                                        {
                                            tempCycleCount++;
                                        }
                                    }

                                    if( tempCycleCount > 63 )//Versacom can't support counts higher than 63
                                    {
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
                                // group constraints will adjust counts down...!?
                            }
                        }
                        else if( ciStringEqual(cycleCountDownType, CtiLMProgramDirectGear::LimitedCountDownMethodOptionType) )
                        {
                            adjust_counts = false;
                            LONG cycleLength = period * cycleCount;
                            LONG estimatedControlTimeInSeconds = cycleLength * (((double)percent)/100.0);
                            if( getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                getProgramState() == CtiLMProgramBase::TimedActiveState )
                            {
                                ULONG secondsAtLastControl = currentLMGroup->getLastControlSent().seconds();
                                if( (secondsAtLastControl + cycleLength) >= (getDirectStopTime().seconds()-120) )//if the last control sent is not within 2 minutes before the stop control time a refresh will be sent
                                {
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

                        if( cycleCount > 0 )
                        {
                            CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                            if( getConstraintOverride() || con_checker.checkCycle(cycleCount, period, 100, adjust_counts) )
                            {
                                CtiRequestMsg* requestMsg = NULL;
                                bool no_ramp = (currentGearObject->getFrontRampOption() == CtiLMProgramDirectGear::NoRampRandomOptionType);
                                if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TargetCycleMethod) &&
                                    (isExpresscomGroup(currentLMGroup->getPAOType()) ||
                                     currentLMGroup->getPAOType() == TYPE_LMGROUP_SA305) )
                                {
                                    requestMsg = currentLMGroup->createTargetCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority, kw, getStartedControlling(), getAdditionalInfo());
                                }
                                else
                                {
                                    requestMsg = currentLMGroup->createSmartCycleRequestMsg(percent, period, cycleCount, no_ramp, defaultLMStartPriority);
                                    if( ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::TargetCycleMethod) )
                                    {
                                        CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Target Cycle a non-Expresscom group: " << currentLMGroup->getPAOName());
                                    }
                                }
                                refreshGroupControl(currentLMGroup, requestMsg, multiPilMsg);
                                returnBoolean = TRUE;
                            }
                            else
                            {
                                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                                {
                                    if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                    {
                                        CTILOG_DEBUG(dout, *violations);
                                    }
                                }
                            }
                        }
                        else
                        {
                            if( !(getProgramState() == CtiLMProgramBase::ManualActiveState ||
                                  getProgramState() == CtiLMProgramBase::TimedActiveState) &&
                                !doesGroupHaveAmpleControlTime(currentLMGroup,0) &&
                                currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )//we need to restore the group in the way set in the gear because it went over max control time
                            {
                                // :consider: is this needed??
                                returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, currentTime, multiPilMsg, multiDispatchMsg));
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
                        // :consider: is this needed??
                        returnBoolean = (returnBoolean || stopOverControlledGroup(currentGearObject, currentLMGroup, currentTime, multiPilMsg, multiDispatchMsg));
                    }

                    if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                    {
                        numberOfActiveGroups++;
                    }
                }

                if( returnBoolean )
                    updateStandardControlActiveState(numberOfActiveGroups);
            }
            else
            {
                CTILOG_INFO(dout, "Tried to divide by zero");
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::MasterCycleMethod) )
        {
            int percent = currentGearObject->getMethodRate();
            int period = currentGearObject->getMethodPeriod();
            long off_time = period * (percent / 100.0);

            //anything ready to control? move this out of here when possible - to a main control loop?
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr lm_group  = *i;
                LONG currentOffTime = lm_group->getControlCompleteTime().seconds() - lm_group->getLastControlSent().seconds();
                // For special group types we might need to give it a boost to achieve the correct control time
                if( lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState &&
                    lm_group->doesMasterCycleNeedToBeUpdated(currentTime, lm_group->getControlCompleteTime(), currentOffTime) )
                {
                    //if it is a emetcon switch 450 (7.5 min) is correct
                    //ripple switches have a predetermined shed time
                    multiPilMsg->insert( lm_group->createMasterCycleRequestMsg(450, period, defaultLMRefreshPriority) );
                    returnBoolean = TRUE;
                }
                else
                {
                    if( lm_group->readyToControlAt(currentTime) &&
                        (!getIsRampingOut() || (getIsRampingOut() && lm_group->getIsRampingOut())) ) //ready to control
                    {

                        CtiLMGroupConstraintChecker con_checker(*this, lm_group, currentTime);
                        if( getConstraintOverride() || con_checker.checkControl(off_time, true) )
                        {
                            CtiRequestMsg* req_msg = lm_group->createMasterCycleRequestMsg(off_time, period, defaultLMRefreshPriority);
                            refreshGroupControl(lm_group, req_msg, multiPilMsg);

                            //These two are a little different?  they correct?
                            setLastGroupControlled(lm_group->getPAOId());
                            lm_group->setControlCompleteTime(currentTime + off_time);

                            if( getProgramState() != CtiLMProgramBase::ManualActiveState &&
                                getProgramState() != CtiLMProgramBase::TimedActiveState )
                            {
                                setProgramState(CtiLMProgramBase::FullyActiveState);
                            }

                            returnBoolean = TRUE;
                        }
                        else
                        {
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                        // set up the next control time regardless whether constraints failed

                        lm_group->setNextControlTime(CtiTime(lm_group->getNextControlTime().seconds() + period));

                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CTILOG_DEBUG(dout, "LMProgram: " << getPAOName() << ",  master cycle controlling " << lm_group->getPAOName() << " next at: " << lm_group->getNextControlTime().asString());
                        }

                    }
                }
            }

        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::RotationMethod ) )
        {
            LONG sendRate = currentGearObject->getMethodRate();
            LONG shedTime = currentGearObject->getMethodPeriod();
            LONG numberOfGroupsToTake = currentGearObject->getMethodRateCount();

            if( numberOfGroupsToTake == 0 )
            {
                numberOfGroupsToTake = _lmprogramdirectgroups.size();
            }

            CtiTime sendRateEnd(0UL);

            // First we need to update the state of the currently active rotating groups
            for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
            {
                CtiLMGroupPtr currentLMGroup  = *i;
                if( currentLMGroup->getGroupControlState() == CtiLMGroupBase::ActiveState )
                {
                    if( currentTime >= currentLMGroup->getLastControlSent().seconds()+shedTime )
                    {
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        currentLMGroup->setControlCompleteTime(CtiTime());
                        returnBoolean = TRUE;
                    }
                }

                if( currentLMGroup->getLastControlSent()+sendRate > sendRateEnd )//sendRateEnd is set to the latest of the new group controls
                {
                    sendRateEnd = currentLMGroup->getLastControlSent()+sendRate;
                }
            }

            if( currentTime >= sendRateEnd )
            {
                // Now we need to take the next groups for the rotation method
                int groups_taken = 0;
                do
                {
                    CtiLMGroupPtr nextLMGroupToTake = findGroupToTake(currentGearObject);
                    if( nextLMGroupToTake.get() == NULL )   // No more groups to take, get outta here
                    {
                        CTILOG_INFO(dout, "Program: " << getPAOName() << " couldn't find any groups to take");
                        break;
                    }
                    else
                    {
                        CtiLMGroupConstraintChecker con_checker(*this, nextLMGroupToTake, currentTime);
                        if( getConstraintOverride() || con_checker.checkControl(shedTime, true) )
                        {
                            groups_taken++;
                            CtiRequestMsg* requestMsg = nextLMGroupToTake->createRotationRequestMsg(sendRate, shedTime, defaultLMRefreshPriority);

                            setLastGroupControlled(nextLMGroupToTake->getPAOId());
                            if( nextLMGroupToTake->getGroupControlState() == CtiLMGroupBase::InactiveState )
                            {
                                startGroupControl(nextLMGroupToTake, requestMsg, multiPilMsg);
                            }
                            else
                            {
                                refreshGroupControl(nextLMGroupToTake, requestMsg, multiPilMsg);
                            }
                            returnBoolean = TRUE;
                        }
                        else
                        {
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                                {
                                    CTILOG_DEBUG(dout, *violations);
                                }
                            }
                        }
                    }
                } while( groups_taken < numberOfGroupsToTake );

                setPendingGroupsInactive();
            }

            if( getProgramState() != CtiLMProgramBase::ManualActiveState &&
                getProgramState() != CtiLMProgramBase::TimedActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::LatchingMethod ) )
        {
            /*CTILOG_INFO(dout, "Gear Control Method: " << getPAOName() << " Gear#: " << currentGearObject->getGearNumber() << " control method isn't supported yet. ");*/
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::SimpleThermostatRampingMethod) )
        {
          /*double dummyVariable; BUMP_COMMAND_REMOVED
            bool didSendMessages = sendSimpleThermostatMessage(currentGearObject, currentTime, multiPilMsg, dummyVariable, true);
            if( didSendMessages && getProgramState() != CtiLMProgramBase::ManualActiveState )
            {
                setProgramState(CtiLMProgramBase::FullyActiveState);
            }*/
        }
        else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::ThermostatRampingMethod) ||
                 ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod) )
        {
            //we don't refresh set point commands or no control gears
        }
        else
        {
            CTILOG_INFO(dout, "Program: " << getPAOName() << ", Gear#: " << currentGearObject->getGearNumber() << " doesn't have a valid control method");
        }
    }
    else if( currentGearObject == NULL )
    {
        CTILOG_INFO(dout, "Program: " << getPAOName() << " currentGearObject == NULL");
    }
    else
    {
        /*CTILOG_INFO(dout, "Program: " << getPAOName() << " doesn't have any groups");*/
    }

    return returnBoolean;
}

void CtiLMProgramDirect::updateStandardControlActiveState(LONG numberOfActiveGroups)
{
    if ( getProgramState() != CtiLMProgramBase::ManualActiveState &&
         getProgramState() != CtiLMProgramBase::TimedActiveState )
    {
        if ( numberOfActiveGroups == _lmprogramdirectgroups.size() )
        {
            setProgramState(CtiLMProgramBase::FullyActiveState);
        }
        else if ( numberOfActiveGroups == 0 )
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

/*----------------------------------------------------------------------------
  stopSubordinatePrograms

  Stops controlling any subordinate programs that may be active.
  Returns true if any programs were found active and stopped.
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::stopSubordinatePrograms(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime)
{
    bool stopped_programs = false;
    std::set<CtiLMProgramDirectSPtr>& sub_set = getSubordinatePrograms();

    for( std::set<CtiLMProgramDirectSPtr>::iterator sub_iter = sub_set.begin();
       sub_iter != sub_set.end();
       sub_iter++ )
    {
        if( (*sub_iter)->getProgramState() != CtiLMProgramBase::InactiveState )
        {
            string text = "Stopping subordinate program: ";
            text += (*sub_iter)->getPAOName();
            string additional = "";
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
            signal->setSOE(2);
            multiDispatchMsg->insert(signal);
            CTILOG_INFO(dout,  text);

            if( (*sub_iter)->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, currentTime) )
            {
                (*sub_iter)->scheduleStopNotification(CtiTime());
                stopped_programs = true;
            }
        }
    }
    return stopped_programs;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by stopping all groups that are active.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime)
{
    BOOL returnBool = TRUE;
    bool is_ramping_out = false;

    CtiLMProgramDirectGear* currentGearObject = getCurrentGearObject();

    if( currentGearObject != NULL )
    {
        string tempControlMethod = currentGearObject->getControlMethod();
        string tempMethodStopType = currentGearObject->getMethodStopType();

        for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
        {
            CtiLMGroupPtr currentLMGroup  = *i;
            if( currentTime > currentLMGroup->getControlStartTime().seconds() + getMinActivateTime() ||
                getManualControlReceivedFlag() )
            {
                if( SmartGearBase *smartGearObject = dynamic_cast<SmartGearBase *>(currentGearObject) )
                {
                    CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
                    if( !(getConstraintOverride() || con_checker.checkRestore()) )
                    {
                        if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                        {
                            CTILOG_INFO(dout, *violations);
                        }
                        returnBool = FALSE; // At least one group did not restore, but others still could!
                    }
                    else
                    {
                        //We don't want to send a restore message to a group after it has already stopped on its own
                        if( currentLMGroup->doesStopRequireCommandAt( CtiTime::now() ) )
                        {
                            if( smartGearObject->stopControl(currentLMGroup))
                            {
                                setLastControlSent(CtiTime());
                            }
                        }
                        else
                        {
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CTILOG_DEBUG(dout, "Not sending Stop command. Group should have already stopped on its own. LM Group: " << currentLMGroup->getPAOName());
                            }
                        }
                    }
                }
                else
                if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::SmartCycleMethod ) ||
                    ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::TrueCycleMethod ) ||
                    ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::MagnitudeCycleMethod) ||
                    ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::TargetCycleMethod ) )
                {
                    if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RestoreStopType ) )
                    {
                        restoreGroup(currentTime, currentLMGroup, multiPilMsg, currentGearObject->getStopRepeatCount());
                    }
                    else if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::StopCycleStopType ) ||
                             ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::TimeInStopType ) ||
                             ciStringEqual(tempMethodStopType,"Time-In" ) )//"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way
                    {
                        stopCycleGroup(currentTime, currentLMGroup, multiPilMsg, currentGearObject->getMethodPeriod(), currentGearObject->getStopRepeatCount());
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Invalid current gear method stop type: " << tempMethodStopType);
                    }
                }
                else if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::TimeRefreshMethod) ||
                         ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::MasterCycleMethod) ||
                         ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::RotationMethod) ||
                         ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::ThermostatRampingMethod) ||
                         ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::SimpleThermostatRampingMethod) )
                {
                    if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RestoreStopType) ||
                        /* fugly.  Only manually active programs can ramp out.  If this program is actually controlling
                           automatically then we cannot current ramp out!  If that is the case and the program has a restore
                           ramp out type then just treat it as a restore. dang. The same thing for is done for time-in v */
                        ( getProgramState() != CtiLMProgramBase::ManualActiveState &&
                          (CtiLMProgramDirectGear::RampOutRandomRestoreStopType == tempMethodStopType ||
                           CtiLMProgramDirectGear::RampOutFIFORestoreStopType == tempMethodStopType))
                      )
                    {
                        restoreGroup(currentTime, currentLMGroup, multiPilMsg, currentGearObject->getStopRepeatCount());
                    }
                    else if( ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::TimeInStopType) ||
                             ciStringEqual(tempMethodStopType,"Time-In") ||
                             /* fugly.  Only manually active programs can ramp out.  If this program is actually controlling
                                automatically then we cannot current ramp out!
                                ramp out type then just treat it as a time-in. dang. The same thing for is done for restore ^ */
                             ( getProgramState() != CtiLMProgramBase::ManualActiveState &&
                               (CtiLMProgramDirectGear::RampOutRandomStopType == tempMethodStopType ||
                                CtiLMProgramDirectGear::RampOutFIFOStopType == tempMethodStopType))
                           )
                    {
                        //"Time-In" is a hack to account for older versions of the DB Editor putting it in the DB that way

                        //I don't know if I should do anything unique here yet?
                        //multiPilMsg->insert(new CtiRequestMsg(currentLMGroup->getPAOId(), "control terminate"));
                        //setLastControlSent(CtiTime());
                        //currentLMGroup->setLastControlSent(CtiTime());
                        CtiTime timeToTimeIn = gInvalidCtiTime;//put in a bogus time stamp
                        if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::MasterCycleMethod) )
                        {
                            timeToTimeIn = currentLMGroup->getLastControlSent();
                            LONG offTimeInSeconds = currentGearObject->getMethodPeriod() * (currentGearObject->getMethodRate() / 100.0);
                            timeToTimeIn.addMinutes(offTimeInSeconds/60);
                        }
                        else if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::ThermostatRampingMethod) ||
                                 ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::SimpleThermostatRampingMethod) )
                        {
                            timeToTimeIn = currentLMGroup->getLastControlSent();
                            const CtiLMProgramThermostatGear * thermostatGear = static_cast<const CtiLMProgramThermostatGear*>( currentGearObject );
                            int minutesToAdd = 0;
                            minutesToAdd += (thermostatGear->getRandom()/2+thermostatGear->getRandom()%2);
                            minutesToAdd += thermostatGear->getDelayTime();
                            minutesToAdd += thermostatGear->getPrecoolTime();
                            minutesToAdd += thermostatGear->getPrecoolHoldTime();
                            minutesToAdd += thermostatGear->getControlTime();
                            minutesToAdd += thermostatGear->getControlHoldTime();
                            minutesToAdd += thermostatGear->getRestoreTime();
                            timeToTimeIn.addMinutes(minutesToAdd);
                        }
                        else
                        {
                            timeToTimeIn = currentLMGroup->getLastControlSent();
                            timeToTimeIn.addMinutes(currentGearObject->getMethodPeriod()/60);
                        }
                        currentLMGroup->setControlCompleteTime(timeToTimeIn);
                    }
                    else if( !currentLMGroup->getIsRampingOut() &&
                             (ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RampOutRandomStopType ) ||
                              ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RampOutFIFOStopType ) ||
                              ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RampOutRandomRestoreStopType ) ||
                              ciStringEqual(tempMethodStopType,CtiLMProgramDirectGear::RampOutFIFORestoreStopType )) )
                    {
                        currentLMGroup->setIsRampingIn(false);
                        currentLMGroup->setIsRampingOut(true);
//NOTE: is this correct for the control complete time for the group?  does depend on type?
                        currentLMGroup->setControlCompleteTime( ( (CtiTime)(currentTime.seconds() + (unsigned long) floor( 100.0 / (double)currentGearObject->getRampOutPercent()) * currentGearObject->getRampOutInterval() + 1.0) ));
                        setStartedRampingOutTime(currentTime);
                        is_ramping_out = true;

                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CTILOG_DEBUG(dout, "LMGroup: " << currentLMGroup->getPAOName() << " waiting to ramp out.");
                        }
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Invalid current gear method stop type: " << tempMethodStopType);
                    }
                }
                else if( ciStringEqual(tempControlMethod,CtiLMProgramDirectGear::LatchingMethod ) )
                {
                    if( currentLMGroup->getPAOType() == TYPE_LMGROUP_POINT )
                    {
                        multiPilMsg->insert( currentLMGroup->createLatchingRequestMsg( false, defaultLMStartPriority ) );
                    }
                    else
                    {
                        multiDispatchMsg->insert( currentLMGroup->createLatchingCommandMsg(currentGearObject->getMethodRateCount()?0:1, defaultLMStartPriority) );
                    }

                    setLastControlSent(CtiTime());
                    currentLMGroup->setLastControlSent(CtiTime());
                }
                else if( ciStringEqual(currentGearObject->getControlMethod(),CtiLMProgramDirectGear::NoControlMethod ) )
                {
                    // Its not controlling so a stop method doesn't much matter, does it?
                }
                else
                {
                    CTILOG_INFO(dout, "Invalid current gear control method: " << tempControlMethod);
                }
            }
            else
            {
                currentLMGroup->setGroupControlState(CtiLMGroupBase::InactivePendingState);
                returnBool = FALSE;
                CTILOG_INFO(dout, "Group cannot be set inactive yet because of minimum active time Group: " << currentLMGroup->getPAOName());
            }
        }

        if( returnBool )
        {
            setDirectStopTime(CtiTime());
        }

        if( is_ramping_out )
        {
            // clear out any pending states
            ResetGroupsControlState();
        }

        if( !is_ramping_out && returnBool )
        {
            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            ResetGroups();
            setCurrentGearNumber(0);
            setStartedControlling(gInvalidCtiTime);
            setManualControlReceivedFlag(FALSE);
        }
    }
    else
    {
        returnBool = FALSE;
        CTILOG_INFO(dout, "Invalid current gear number: " << _currentgearnumber);
    }

    return returnBool;
}

/*----------------------------------------------------------------------------
  updateGroupsRampingOut

  Should only be called when the program is ramping out, checks to see if
  any groups should be done ramping out and sets them to not be ramping out.
  Retruns true if any groups were dirtied.
  Call getIsRampingOut after this if you want to know if any groups
  are still ramping out.
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::updateGroupsRampingOut(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiTime currentTime)
{
    bool ret_val = false;
    int num_groups = _lmprogramdirectgroups.size();
    CtiLMProgramDirectGear* lm_gear = getCurrentGearObject();
    long ramp_out_interval = lm_gear->getRampOutInterval();
    long ramp_out_percent = lm_gear->getRampOutPercent();

    if( ramp_out_interval == 0 || ramp_out_percent == 0 )
    {
        CTILOG_ERROR(dout, "LMProgram: " << getPAOName() << " Gear: " << getCurrentGearObject()->getGearName() << " has a stop type of 'Ramp Out', but is has 0 for a ramp out interval or ramp out percent.  Please change this to something reasonable.");
        return false;
    }

    int cur_interval = (currentTime.seconds() - getStartedRampingOutTime().seconds()) / getCurrentGearObject()->getRampOutInterval()+1;
    int should_be_ramped_out = std::min((int) floor((((double)ramp_out_percent/100.0 * (double)cur_interval) * (double)num_groups) + 0.5), (int) num_groups);
    int num_ramped_out = 0;

    //determine how many are ramped out now
    for( CtiLMGroupIter n = _lmprogramdirectgroups.begin(); n != _lmprogramdirectgroups.end(); n++ )
    {
        CtiLMGroupPtr lm_group  = *n;
        if( !lm_group->getIsRampingOut() )
        {
            num_ramped_out++;
        }
    }

    int num_to_ramp_out = should_be_ramped_out - num_ramped_out;

    for( int i = 0; i < num_to_ramp_out; i++ )
    {
        CtiLMGroupPtr lm_group = findGroupToRampOut(getCurrentGearObject());
        if( lm_group.get() != 0 )
        {
            ret_val = true;
        }

        // Possibly restore the group now that it has ramped out
        if( lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutRandomRestoreStopType ||
            lm_gear->getMethodStopType() == CtiLMProgramDirectGear::RampOutFIFORestoreStopType )
        {
            restoreGroup(currentTime, lm_group, multiPilMsg, lm_gear->getStopRepeatCount());
        }
    }

    return ret_val;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the direct program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    BOOL returnBoolean = FALSE;
    if( getControlArea() != NULL )
    {
        if( getProgramState() == CtiLMProgramBase::ManualActiveState && hasGearChanged(_lmprogramdirectgears[_currentgearnumber]->getChangePriority() - 1, getControlArea()->getLMControlAreaTriggers(), currentTime, multiDispatchMsg, true) )
        {
            // hasGearChanged here changed the gear for us, we want to do a full manual control now, not a refresh.
            setProgramState(CtiLMProgramBase::GearChangeState);
        }
        else if( getProgramState() ==  CtiLMProgramBase::GearChangeState )
        {
            //This is a bit ugly, but this is where we record a gear change when a manual gear change message came through.
            recordHistory(CtiTableLMProgramHistory::GearChange, CtiTime::now());
        }
    }

    if( getProgramState() == CtiLMProgramBase::ScheduledState || getProgramState() == CtiLMProgramBase::GearChangeState )
    {
        if( currentTime >= getDirectStartTime() )
        {

            CtiLMProgramConstraintChecker con_checker(*this, currentTime);
            // Currently program  constraints are already checked by the executor for handling manual control
            // as well sa the starttimedprogram function.  So when we get here the check should have already
            // been made.

            // Gear constraints are only not checked in the starttimed function now.
            if( getConstraintOverride() || con_checker.checkGroupConstraints(getCurrentGearNumber(), getDirectStartTime().seconds(), getDirectStopTime().seconds()) )
            {
                // are any of our master programs already running?  if so we can't start MASTERSLAVE - this is alreadya  constraint checked in executor
                returnBoolean = TRUE;

                string text("Manual Start, LM Program: ");
                text += getPAOName();
                string additional("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, text << ", " << additional);
                }

                // Dont let subordinate programs keep running
                stopSubordinatePrograms(multiPilMsg, multiDispatchMsg, multiNotifMsg, currentTime);
                // !CONSTRAINT - do it here or in manual?
                manualReduceProgramLoad(currentTime, multiPilMsg,multiDispatchMsg);
                setProgramState(CtiLMProgramBase::ManualActiveState);
            }
            else
            {
                if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                {
                    CTILOG_INFO(dout, *violations);
                }
                setProgramState(CtiLMProgramBase::InactiveState);
                setManualControlReceivedFlag(FALSE);
            }

        }
        if( currentTime >= getDirectStopTime().seconds() )
        {
            returnBoolean = TRUE;
            {
                string text =  ("Manual Stop, LM Program: ");
                text += getPAOName();
                string additional =  ("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
            }

            stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);

            setReductionTotal(0.0);
            setStartedControlling(gInvalidCtiTime);
            setDirectStopTime(CtiTime());
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ManualActiveState )
    {
        if( currentTime >= getDirectStopTime().seconds() && !getIsRampingOut() )
        {
            returnBoolean = TRUE;
            {
                string text =  ("Manual Stop, LM Program: ");
                text += getPAOName();
                string additional =  ("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
            }
            setChangeReason("Manual Stop Time Reached");
            stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);
            setReductionTotal(0.0);
            setStartedControlling(gInvalidCtiTime);
            setDirectStopTime(CtiTime());
        }
        else
        {
            if( refreshStandardProgramControl(currentTime, multiPilMsg, multiDispatchMsg) )
            {
                returnBoolean = TRUE;
            }
            if( getIsRampingOut() )
            {
                returnBoolean = updateGroupsRampingOut(multiPilMsg, multiDispatchMsg, currentTime);
                if( !getIsRampingOut() )  // no longer ramping out?
                {
                    string text =  ("Finshed Ramping Out, LM Program: ");
                    text += getPAOName();
                    string additional =  ("");
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(2);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
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
        if( currentTime >= getDirectStopTime() )
        {
            returnBoolean = TRUE;
            {
                string text =  ("Manual Stop, LM Program: ");
                text += getPAOName();
                string additional =  ("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
            } //NOTE: SHOULD WE BE SETTING THE STATE HERE?  COULD MESS UP RAMPOUT
            setChangeReason("Manual Stop Time Reached");
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);
            setManualControlReceivedFlag(FALSE);

            setReductionTotal(0.0);
            setProgramState(CtiLMProgramBase::InactiveState);
            ResetGroups();
            setStartedControlling(gInvalidCtiTime);
            setDirectStopTime(CtiTime());
        }
    }
    else if( getProgramState() == CtiLMProgramBase::StoppingState )
    {
        returnBoolean = TRUE;
        {
            string text =  ("Manual Stop, LM Program: ");
            text += getPAOName();
            string additional =  ("");
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(2);

            multiDispatchMsg->insert(signal);
            CTILOG_INFO(dout, text << ", " << additional);
        }
        stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);
        setReductionTotal(0.0);
        setStartedControlling(gInvalidCtiTime);
        setDirectStopTime(CtiTime());
    }
    else if( !getIsRampingOut() )
    {
        CTILOG_INFO(dout, "Invalid manual control state: " << getProgramState());
        setManualControlReceivedFlag(FALSE);
    }

    return returnBoolean;
}


/*---------------------------------------------------------------------------
    handleTimedControl

    Handles timed control
---------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::handleTimedControl(CtiTime currentTime, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
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

    if( was_ramping_out && timed_active ) // only do this if we are active, another program might be ramping out our groups!!!
    {
        ret_val = updateGroupsRampingOut(multiPilMsg, multiDispatchMsg, currentTime); // consider if any groups have ramped out
    }

    bool is_control_time = (currentTime < getDirectStopTime() && currentTime > getDirectStartTime());
    bool inactive = (getProgramState() == CtiLMProgramBase::InactiveState);
    bool disabled = getDisableFlag();
    bool is_ramping_out = getIsRampingOut();

    if( inactive )
    {
        if( is_control_time && !disabled ) //do we need to do a timed start?
        {
            // timed start
            return(ret_val || startTimedProgram(currentTime, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg));
        }
        else   // inactive and either not in a control window or disabled so nothin to do
        {
            return ret_val;
        }
    } // end timed start
    else if( timed_active ) //do we need to do a timed stop or just refresh?
    {
        if( (!is_control_time || disabled) )
        {
            if( !is_ramping_out && was_ramping_out )  //we just finished ramping out!
            {
                string text = "Finished ramping out, LM Program: ";
                text += getPAOName();
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.c_str(),"",GeneralLogType,SignalEvent);
                multiDispatchMsg->insert(signal);

                CTILOG_INFO(dout,  text);

                setProgramState(CtiLMProgramBase::InactiveState);
                ResetGroups();
                setManualControlReceivedFlag(FALSE);
                return true;
            } //end finshed ramping out
            else if( !is_ramping_out )
            {
                //timed stop
                string text = "Timed Stop, LM Program: ";
                text += getPAOName();
                string additional = "";
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.c_str(),additional.c_str(),GeneralLogType,SignalEvent);
                signal->setSOE(2);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);

                setChangeReason("Timed Stop");
                stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);

                setReductionTotal(0.0); //is this resetting dynamic info?
                setStartedControlling(gInvalidCtiTime);
                return true;
            } //end timed stop
        }
/*
  Lets say someone disabled this program and then enabled it again quick, while were are still
  in a control window, what do we do?
        else if(in_control_window && !disabled && is_ramping_out)  //ramping out, but we shouldn't be
        {
            return (ret_val || startTimedProgram(currentTime, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg));
        }
*/
        //refresh
        return(ret_val || refreshStandardProgramControl(currentTime, multiPilMsg, multiDispatchMsg));
        //end refresh
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid timed control state: " << getProgramState());
        return ret_val;
    }
}

/*----------------------------------------------------------------------------
  startTimedProgram

  Start a timed program, check constraints first though.
  Returns true if the timed program actually starts.
----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::startTimedProgram(CtiTime currentTime, long secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    CtiLMProgramConstraintChecker con_checker(*this, currentTime);

    CtiLMProgramControlWindow* controlWindow = getControlWindow(secondsFromBeginningOfDay);
    assert(controlWindow != NULL); //If we are not in a control window then we shouldn't be starting!

    CtiTime startTime = currentTime;
    CtiTime endTime = controlWindow->getAvailableStopTime(); // This is likely wrong, not changing during refactor.
    if( !getConstraintOverride() && !con_checker.checkAutomaticProgramConstraints(startTime.seconds(), endTime.seconds()) )
    {
        if( !_announced_program_constraint_violation )
        {
            string text = " LMProgram: ";
            text += getPAOName();
            text += ", a timed program, was scheduled to start but did not due to constraint violations";
            string additional = "";
            for each ( const std::string & violationMsg in con_checker.getResults() )
            {
                additional += violationMsg;
                additional += "\n";
            }
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
            signal->setSOE(2);
            multiDispatchMsg->insert(signal);

            CTILOG_INFO(dout,  text << endl << additional);

            _announced_program_constraint_violation = true;
        }
        return false;
    }
    else
    {
        string text = "Timed Start, LM Program: ";
        text += getPAOName();
        string additional = "";
        CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
        signal->setSOE(2);

        multiDispatchMsg->insert(signal);
        CTILOG_INFO(dout, text << ", " << additional);
        setChangeReason("Timed Start");
        manualReduceProgramLoad(currentTime, multiPilMsg,multiDispatchMsg);
        setProgramState(CtiLMProgramBase::TimedActiveState);

        setDirectStartTime(startTime);
        _announced_program_constraint_violation = false;

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

    if( getControlType() == CtiLMProgramBase::TimedType )
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
    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
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
BOOL CtiLMProgramDirect::isPastMinRestartTime(CtiTime currentTime)
{
    BOOL returnBoolean = TRUE;

    if( getPAOType() == TYPE_LMPROGRAM_DIRECT && getMinRestartTime() > 0 )
    {
        CtiLMGroupVec program_groups  = getLMProgramDirectGroups();
        for( CtiLMGroupIter i = program_groups.begin(); i != program_groups.end(); i++ )
        {
            CtiLMGroupPtr currentLMGroup = *i;
            if( currentLMGroup->getControlCompleteTime() + getMinRestartTime() > currentTime )
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

    Returns true if the group can control for at least estimatedControlTimeInSeconds.
    Returns false if the group only has estimatedControlTimeInSeconds-1 seconds left.
    Checks Max Activate Time, Max Hours Daily, Monthly, Seasonally, and Annually.
--------------------------------------------------------------------------*/
bool CtiLMProgramDirect::doesGroupHaveAmpleControlTime(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const
{
    // 0 is a special case that is passed in quite a bit. It really means: Do I have at least 1 second of control time remaining
    // This is necessary for the call to calculateGroupControlTimeLeft to work.
    if (estimatedControlTimeInSeconds == 0) { 
        estimatedControlTimeInSeconds = 1;
    }
    
    return calculateGroupControlTimeLeft(currentLMGroup, estimatedControlTimeInSeconds) == estimatedControlTimeInSeconds;
}

/*-----------------------------------------------------------------------------
  hasGroupExceededMaxDailyOps
-----------------------------------------------------------------------------*/
BOOL CtiLMProgramDirect::hasGroupExceededMaxDailyOps(CtiLMGroupPtr& lm_group) const
{
    return( getMaxDailyOps() > 0 && (lm_group->getDailyOps() >= getMaxDailyOps()));
}

/*-------------------------------------------------------------------------
    calculateGroupControlTimeLeft

    .
--------------------------------------------------------------------------*/
LONG CtiLMProgramDirect::calculateGroupControlTimeLeft(CtiLMGroupPtr& currentLMGroup, LONG estimatedControlTimeInSeconds) const
{
    LONG returnTimeLeft = estimatedControlTimeInSeconds;

    if( getMaxActivateTime() > 0 && (currentLMGroup->getCurrentControlDuration() + estimatedControlTimeInSeconds) > getMaxActivateTime() )
    {
        LONG tempTimeLeft = getMaxActivateTime() - currentLMGroup->getCurrentControlDuration();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursDaily() > 0 && (currentLMGroup->getCurrentHoursDaily() + estimatedControlTimeInSeconds) > getMaxHoursDaily() )
    {
        LONG tempTimeLeft = getMaxHoursDaily() - currentLMGroup->getCurrentHoursDaily();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursMonthly() > 0 && (currentLMGroup->getCurrentHoursMonthly() + estimatedControlTimeInSeconds) > getMaxHoursMonthly() )
    {
        LONG tempTimeLeft = getMaxHoursMonthly() - currentLMGroup->getCurrentHoursMonthly();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursSeasonal() > 0 && (currentLMGroup->getCurrentHoursSeasonal() + estimatedControlTimeInSeconds) > getMaxHoursSeasonal() )
    {
        LONG tempTimeLeft = getMaxHoursSeasonal() - currentLMGroup->getCurrentHoursSeasonal();
        if( tempTimeLeft < returnTimeLeft )
        {
            returnTimeLeft = tempTimeLeft;
        }
    }
    if( getMaxHoursAnnually() > 0 && (currentLMGroup->getCurrentHoursAnnually() + estimatedControlTimeInSeconds) > getMaxHoursAnnually() )
    {
        LONG tempTimeLeft = getMaxHoursAnnually() - currentLMGroup->getCurrentHoursAnnually();
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

    if( _currentgearnumber < _lmprogramdirectgears.size() )
    {
        returnGearObject = (CtiLMProgramDirectGear*)_lmprogramdirectgears[_currentgearnumber];
    }
    else
    {
        CTILOG_INFO(dout, "Invalid current gear number: " << _currentgearnumber);
    }

    return returnGearObject;
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
        _adjustment_notification_pending = right._adjustment_notification_pending;
        _trigger_offset = right._trigger_offset;
        _trigger_restore_offset = right._trigger_restore_offset;
        _startedrampingout = right._startedrampingout;
        _constraint_override = right._constraint_override;
        _announced_program_constraint_violation = right._announced_program_constraint_violation;
        _hasBeatThePeakGear = right._hasBeatThePeakGear;

        for each( CtiLMProgramDirectGear *gear in right._lmprogramdirectgears )
        {
            _lmprogramdirectgears.push_back( gear->replicate() );
        }

        _lmprogramdirectgroups = right._lmprogramdirectgroups;
        _master_programs = right._master_programs;
        _subordinate_programs = right._subordinate_programs;
        _controlActivatedByStatusTrigger = right._controlActivatedByStatusTrigger;
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
CtiLMProgramBaseSPtr CtiLMProgramDirect::replicate() const
{
    CtiLMProgramBaseSPtr retVal = CtiLMProgramBaseSPtr(CTIDBG_new CtiLMProgramDirect(*this));
    return retVal;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::restore(Cti::RowReader &rdr)
{
    CtiLMProgramBase::restore(rdr);

    string tempBoolString;
    _insertDynamicDataFlag = FALSE;
    long temp;

    rdr["heading"] >> _message_subject;
    rdr["messageheader"] >> _message_header;
    rdr["messagefooter"] >> _message_footer;
    rdr["triggeroffset"] >> _trigger_offset;
    rdr["restoreoffset"] >> _trigger_restore_offset;
    rdr["notifyactiveoffset"] >> _notify_active_offset;
    rdr["notifyinactiveoffset"] >> _notify_inactive_offset;
    rdr["notifyschedule"] >> temp;
    _notify_when_scheduled = (temp == 1);

    {
        // NotifyAdjust is represented by -1 = false, 1 = true in the database.
        int notifyAdjust;
        rdr["notifyadjust"] >> notifyAdjust;
        _adjustment_notification_enabled = (notifyAdjust == 1);
    }

    if( !rdr["currentgearnumber"].isNull() )
    {
        rdr["currentgearnumber"] >> _currentgearnumber;
        rdr["lastgroupcontrolled"] >> _lastgroupcontrolled;
        rdr["starttime"] >> _directstarttime;
        rdr["stoptime"] >> _directstoptime;
        rdr["timestamp"] >> _dynamictimestamp;
        rdr["notifyactivetime"] >> _notify_active_time;
        rdr["notifyinactivetime"] >> _notify_inactive_time;
        rdr["startedrampingout"] >> _startedrampingout;
        rdr["additionalinfo"] >> _additionalinfo;
        rdr["currentlogid"] >> _curLogID;
        rdr["constraintoverride"] >> tempBoolString;
        CtiToLower(tempBoolString);
        setConstraintOverride(tempBoolString=="y"?TRUE:FALSE);
        setControlActivatedByStatusTrigger(FALSE);
        _insertDynamicDataFlag = FALSE;
        setDirty(false);
    }
    else
    {
        setCurrentGearNumber(0);
        setLastGroupControlled(0);
        setDirectStartTime(gInvalidCtiTime);
        setDirectStopTime(gInvalidCtiTime);
        _dynamictimestamp = gInvalidCtiTime;
        _notify_active_time = gInvalidCtiTime;
        _notify_inactive_time = gInvalidCtiTime;
        _startedrampingout = gInvalidCtiTime;
        _constraint_override = false;
        setControlActivatedByStatusTrigger(FALSE);
        _insertDynamicDataFlag = TRUE;
        _curLogID = 0;
        setDirty(true);
    }
    //ok to announce timed program constraint violations once per database reload
    _announced_program_constraint_violation = false;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::dumpDynamicData()
{
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this direct program.
---------------------------------------------------------------------------*/
void CtiLMProgramDirect::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( !isDirty() )
    {
        return;
    }

    CtiLMProgramBase::dumpDynamicData(conn,currentDateTime);

    string additionalInfo = getAdditionalInfo();
    if( !additionalInfo.length() )
    {
        additionalInfo = "(none)";
    }

    if( !_insertDynamicDataFlag )
    {
        static const std::string sql_update = "update dynamiclmprogramdirect"
                                               " set "
                                                    "currentgearnumber = ?, "
                                                    "lastgroupcontrolled = ?, "
                                                    "starttime = ?, "
                                                    "stoptime = ?, "
                                                    "timestamp = ?, "
                                                    "notifyactivetime = ?, "
                                                    "startedrampingout = ?, "
                                                    "notifyinactivetime = ?, "
                                                    "constraintoverride = ?, "
                                                    "additionalinfo = ?, "
                                                    "currentlogid = ?"
                                               " where "
                                                    "deviceid = ?";

        Cti::Database::DatabaseWriter   updater(conn, sql_update);

        updater
            << getCurrentGearNumber()
            << getLastGroupControlled()
            << getDirectStartTime()
            << getDirectStopTime()
            << currentDateTime
            << getNotifyActiveTime()
            << getStartedRampingOutTime()
            << getNotifyInactiveTime()
            << ( getConstraintOverride() ? std::string("Y") : std::string("N") )
            << additionalInfo
            << getCurrentHistLogId()
            << getPAOId();

        if( ! Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
        {
            return; // Error occurred!
        }
    }
    else
    {
        CTILOG_INFO(dout, "Inserted program direct into DynamicLMProgramDirect: " << getPAOName());

        {
            static const std::string sql_insert = "insert into dynamiclmprogramdirect values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Cti::Database::DatabaseWriter   inserter(conn, sql_insert);

            inserter
                << getPAOId()
                << getCurrentGearNumber()
                << getLastGroupControlled()
                << getDirectStartTime()
                << getDirectStopTime()
                << currentDateTime
                << getNotifyActiveTime()
                << getStartedRampingOutTime()
                << getNotifyInactiveTime()
                << ( getConstraintOverride() ? std::string("Y") : std::string("N") )
                << additionalInfo
                << getCurrentHistLogId();

            if( ! Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
            {
                return; // Error occurred!
            }

            _insertDynamicDataFlag = false;
        }
    }

    _dynamictimestamp = currentDateTime;
    resetDirty(); // setDirty inserts into the changed group list and we do not want to do that here.
}

/*
 * estimateOffTime
 * Attets to estimate how much time the groups in this program will be off given a period of time
 * that the program would be active.
 */

ULONG CtiLMProgramDirect::estimateOffTime(ULONG proposed_gear, CtiTime start_time, CtiTime stop_time)
{
    vector<CtiLMProgramDirectGear*> lm_gears = getLMProgramDirectGears();
    CtiLMProgramDirectGear* cur_gear = (CtiLMProgramDirectGear*) lm_gears[proposed_gear];

    string method = cur_gear->getControlMethod();
    long control_time = stop_time.seconds() - start_time.seconds();

    SmartGearBase *smartGear = dynamic_cast<SmartGearBase *>(cur_gear);

    if( smartGear != NULL )
    {
        return smartGear->estimateOffTime(control_time);
    }
    else if( method == CtiLMProgramDirectGear::TimeRefreshMethod ||
             method == CtiLMProgramDirectGear::LatchingMethod ||
             method == CtiLMProgramDirectGear::SimpleThermostatRampingMethod )
    {
        return control_time;
    }
    else if( method == CtiLMProgramDirectGear::TrueCycleMethod ||
             method == CtiLMProgramDirectGear::MagnitudeCycleMethod ||
             method == CtiLMProgramDirectGear::SmartCycleMethod ||
             method == CtiLMProgramDirectGear::MasterCycleMethod ||
             method == CtiLMProgramDirectGear::TargetCycleMethod )
    {
        return control_time * (double) cur_gear->getMethodRate()/100.0;//       getMethodRateCount()
    }
    else if( method == CtiLMProgramDirectGear::RotationMethod )
    {

        LONG sendRate = cur_gear->getMethodRate();
        LONG shedTime = cur_gear->getMethodPeriod();
        LONG numberOfGroups = getLMProgramDirectGroups().size();
        LONG numberOfGroupsToTake = cur_gear->getMethodRateCount();

        if( numberOfGroupsToTake == 0 )
        {
            numberOfGroupsToTake = numberOfGroups;
        }
        LONG impliedPeriod = (numberOfGroups / numberOfGroupsToTake) * sendRate;
        DOUBLE impliedPercent = (DOUBLE) shedTime / (DOUBLE) impliedPeriod;

        return control_time * impliedPercent;
    }
    else if( method == CtiLMProgramDirectGear::ThermostatRampingMethod )
    {
        const CtiLMProgramThermostatGear * thermoGear = static_cast<const CtiLMProgramThermostatGear*>( cur_gear );
        return ( thermoGear->getPrecoolTime() + thermoGear->getPrecoolHoldTime() +
                 thermoGear->getControlTime() + thermoGear->getControlHoldTime() +
                 thermoGear->getRestoreTime() ) * 60;
    }
    else if( method == CtiLMProgramDirectGear::NoControlMethod )
    {
        return 0;
    }
    else
    {
        CTILOG_ERROR(dout, "invalid gear type: " << method);
    }
    return 0;
}

void CtiLMProgramDirect::startGroupControl(CtiLMGroupPtr& lm_group, CtiRequestMsg* req, CtiMultiMsg* multiPilMsg)
{
    if( req && multiPilMsg )
    {
        cancelScheduledStopsForGroup(lm_group);

        lm_group->setLastControlString(req->CommandString());
        lm_group->setLastControlSent(CtiTime());

        multiPilMsg->insert( req );

        if( lm_group->getGroupControlState() != CtiLMGroupBase::ActiveState )
        {
            lm_group->setControlStartTime(CtiTime());
            lm_group->incrementDailyOps();
            setLastControlSent(CtiTime()); //not sure about this - why only on starrt?
            lm_group->setIsRampingOut(false);     //?
        }

        lm_group->setIsRampingIn(false);     //?
        lm_group->setGroupControlState(CtiLMGroupBase::ActiveState);    // This should be sent from dispatch, no lies!
    }
    else
    {
        CTILOG_ERROR(dout, "One or both of req or multiPilMsg are null, LM program " << getPAOName() << " cannot send message for LM group " << lm_group->getPAOName());
    }

}


void CtiLMProgramDirect::refreshGroupControl(CtiLMGroupPtr& lm_group, CtiRequestMsg* req, CtiMultiMsg* multiPilMsg)
{
    lm_group->setLastControlString(req->CommandString());
    lm_group->setLastControlSent(CtiTime());

    multiPilMsg->insert( req );

    if( lm_group->getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        lm_group->setControlStartTime(CtiTime());
        lm_group->incrementDailyOps();
        setLastControlSent(CtiTime()); //not sure about this - why only on starrt?
        lm_group->setIsRampingOut(false);     //?
    }

    lm_group->setIsRampingIn(false);     //?
    lm_group->setGroupControlState(CtiLMGroupBase::ActiveState);    // This should be sent from dispatch, no lies!
}

// Schedule count copies of this message to be re-sent one per minute.
// Has a sanity check limit of 10 copies.
void CtiLMProgramDirect::scheduleMessageForResend(CtiTime currentTime, const CtiRequestMsg &message, short count, CtiLMGroupPtr &lm_group)
{
    // Ensure we dont double up on these somehow.
    cancelScheduledStopsForGroup(lm_group);

    if ( count > 0 && count < 10 ) 
    {

        string log = "Adding extra stop messages for group: " + lm_group->getPAOName() + ", string: " + message.CommandString();
        

        for ( int i = 0; i< count; i++) 
        {
            // each message is 1 minute after the previous stop, including the one sent that called this function
            currentTime.addMinutes(1);
            auto clonedMessage = std::unique_ptr<CtiRequestMsg>((CtiRequestMsg*)message.replicateMessage());
            static Cti::RandomGenerator<long> optionsFieldIdGenerator;
            clonedMessage->setOptionsField(optionsFieldIdGenerator());
            
            log += "\r\n    " + CtiNumStr(i+1) + ": Time " + currentTime.asString() + " Unique id " + CtiNumStr(clonedMessage->OptionsField()) + " Groupid " + CtiNumStr(message.DeviceId());

            gScheduledStopMessages.addMessage(currentTime, lm_group->getPAOId(), std::move(clonedMessage));
            
        }

        if ( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_INFO(dout, log);
        }
    }
    else if ( count >= 10 ) 
    {
        CTILOG_DEBUG(dout, "Resend count requested > 10, blocking for group id " << lm_group->getPAOId());
    }
}

// Delete all existing scheduled stop messages for this group
void CtiLMProgramDirect::cancelScheduledStopsForGroup(CtiLMGroupPtr &lm_group)
{
    auto count = gScheduledStopMessages.clearMessagesForGroup(lm_group->getPAOId());

    if ( count > 0 )
    {
        if ( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_INFO(dout, "Cancelled " << count << " future stop messages for group " << lm_group->getPAOName());
        }
    }
}

bool CtiLMProgramDirect::restoreGroup(CtiTime currentTime, CtiLMGroupPtr & lm_group, CtiMultiMsg * multiPilMsg, unsigned repeatCount)
{
    CtiLMGroupConstraintChecker con_checker(*this, lm_group, currentTime);
    if( !(getConstraintOverride() || con_checker.checkRestore()) )
    {
        if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
        {
            CTILOG_INFO(dout, *violations);
        }
        return false;
    }
    else
    {
        int priority = 11;
        string controlString = "control restore";
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Sending restore command to  LM Group: " << lm_group->getPAOName() << ", string: " << controlString << ", priority: " << priority);
        }

        CtiRequestMsg* requestMsg = lm_group->buildRequestMessage( controlString, priority );

        if ( repeatCount > 0 )
        {
            scheduleMessageForResend(currentTime, *requestMsg, repeatCount, lm_group);
        }
        lm_group->setLastControlString(requestMsg->CommandString());
        multiPilMsg->insert( requestMsg );
        setLastControlSent(CtiTime());
        lm_group->setLastControlSent(CtiTime());
        return true;
    }
}

/*----------------------------------------------------------------------------
  stopCycleGroup

  Sends messages to stop the current cycling gradually. Actual behavior depends on
  the individual group and it's protocols.
  ----------------------------------------------------------------------------*/
bool CtiLMProgramDirect::stopCycleGroup(CtiTime currentTime, CtiLMGroupPtr& lm_group, CtiMultiMsg* multiPilMsg, LONG period, unsigned repeatCount)
{
    CtiLMGroupConstraintChecker con_checker(*this, lm_group, currentTime);
    if( !(getConstraintOverride() || con_checker.checkTerminate()) )
    {
        if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
        {
            CTILOG_INFO(dout, *violations);
        }
        return false;
    }
    else
    {
        auto stopCycleMessage = lm_group->createStopCycleMsg(period, currentTime);

        if ( repeatCount > 0 )
        {
            scheduleMessageForResend(currentTime, *stopCycleMessage, repeatCount, lm_group);
        }
        multiPilMsg->insert(stopCycleMessage);
        setLastControlSent(currentTime);
        return true;
    }
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

    if( getProgramState() == CtiLMProgramBase::InactiveState )
    {
        return 0.0;
    }
    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        if( lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState )
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
    CtiTime reset_time(gInvalidCtiTime);
    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        lm_group->setNextControlTime(reset_time);
        lm_group->resetGroupControlState();
        lm_group->resetInternalState();
    }
}

void CtiLMProgramDirect::ResetGroupsControlState()
{
    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        lm_group->resetGroupControlState();
    }
}

void CtiLMProgramDirect::ResetGroupsInternalState()
{
    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        lm_group->resetInternalState();
    }
}

/*----------------------------------------------------------------------------
  RampInGroups

  Sets the next control time for all the groups in the program according to
  the ramp in settings of the program.
----------------------------------------------------------------------------*/
void CtiLMProgramDirect::RampInGroups(CtiTime currentTime, CtiLMProgramDirectGear* lm_gear)
{
    if( lm_gear == 0 )
    {
        lm_gear = getCurrentGearObject();
    }

    int num_groups = _lmprogramdirectgroups.size();
    long ramp_in_interval = lm_gear->getRampInInterval();
    long ramp_in_percent = lm_gear->getRampInPercent();
    int total_groups_taken = 0;
    int cur_interval = 1; //start at first interval so action starts right awayTue Jun 22 14:52:14 CST 2004

    while( total_groups_taken < num_groups )   //the number of groups that should be taken by the nth interval
    {
        int should_be_taken = floor((((double)ramp_in_percent/100.0 * (double)cur_interval) * (double)num_groups) + 0.5);
        int num_to_take = should_be_taken - total_groups_taken;
        CtiTime ctrl_time = CtiTime(currentTime + (cur_interval-1) * ramp_in_interval);

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "LMProgram: " << getPAOName() << ",  ramping in a total of " << num_to_take << " groups");
        }
        for( int j = 0; j < num_to_take; j++ )
        {
            CtiLMGroupPtr lm_group = findGroupToTake(lm_gear);
            if( lm_group.get() != NULL )
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "ramping in LMGroup: " << lm_group->getPAOName() << " at: " << ctrl_time.asString());
                }

                lm_group->setIsRampingIn(true);
                lm_group->setNextControlTime(ctrl_time);

                // Mark the last group ramped in in the first set of
                // ramped in groups as this programs last group controlled
                if( total_groups_taken  == 0 && j == (num_to_take-1) )
                {
                    setLastGroupControlled(lm_group->getPAOId());
                }
            }
            else
            {
                CTILOG_INFO(dout, "LMProgram: " << getPAOName() << " couldn't find a group to take when ramping in.");
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
double  CtiLMProgramDirect::StartMasterCycle(CtiTime currentTime, CtiLMProgramDirectGear* lm_gear)
{
    bool do_ramp = (lm_gear->getRampInPercent() > 0);
    int num_groups = _lmprogramdirectgroups.size();
    double expected_load_reduction = 0.0;

    // Let the world know we are starting
    scheduleStartNotification(CtiTime());

    if( do_ramp )
    {
        RampInGroups(currentTime);
    }
    else
    {
        //Regular master cycle, no ramping
        long period = lm_gear->getMethodPeriod();
        long send_rate = period / num_groups;
        int num_groups_to_take = 1;


        if( num_groups >= 8 ) //take 2 at a time - original code also seemed to care about an odd number of groups
        {
            //and would take only 1 if the next group to be taken was the last one, but
            //shouldn't this depend on the group selection method?? drop it for now
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "LMProgram: " << getPAOName() << ", has 8 or more groups, taking 2 at a time");
            }

            num_groups_to_take = 2;
            send_rate = period / (num_groups/2) + (num_groups%2);
        }
        int total_groups_taken = 0;
        int cur_period = 0;
        while( total_groups_taken < num_groups )
        {
            CtiTime ctrl_time = currentTime + (cur_period*send_rate);

/*          if(currentLMGroup->getPAOType() == TYPE_LMGROUP_SADIGITAL ||
               currentLMGroup->getPAOType() == TYPE_LMGROUP_GOLAY )
            {
                // NOTE: SPECIAL CASE - we are going to use the groups nominal time out instead of what is specified in the gear

            }
*/
            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "LMProgram: " << getPAOName() << ",  master cycle taking " << num_groups_to_take << " groups at: " << ctrl_time.asString());
            }

            for( int i = 0; i < std::min(num_groups_to_take, (num_groups-total_groups_taken)); i++ ) //if there is an odd number of groups, can't always take 2
            {
                CtiLMGroupPtr lm_group = findGroupToTake(getCurrentGearObject());//findNextGroupToTake();
                if( lm_group.get() != NULL )
                {
                    lm_group->setNextControlTime(ctrl_time);
                }
                else
                {
                    CTILOG_WARN(dout, "LMProgram: " << getPAOName() << " couldn't find a group to take, master cycle.");
                }
            }
            total_groups_taken += num_groups_to_take;
            cur_period++;
        }

        //figure out the amount of load reduction
        //and bump the daily ops for all the groups and set the control starttime
        for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
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

            lm_group->setControlStartTime(currentTime);
        }

    }
    return expected_load_reduction;
}

bool CtiLMProgramDirect::notifyGroups(int type, CtiMultiMsg* multiNotifMsg)
{
    CtiNotifLMControlMsg* notif_msg = CTIDBG_new CtiNotifLMControlMsg(_notificationgroupids, type, getPAOId(), getDirectStartTime(), getDirectStopTime());
    multiNotifMsg->insert(notif_msg);
    return true;
}

/**
 * Sets up the notification of groups given a start (active) and a stop (inactive) time
 * If this program isn't set to do notifications then this will do nothing.
 */
void CtiLMProgramDirect::scheduleNotification(const CtiTime& start_time, const CtiTime& stop_time)
{
    notifyGroupsOfSchedule(start_time, stop_time);
    scheduleStartNotification(start_time);
    scheduleStopNotification(stop_time);
}

/**
 * Sets up the notification of groups given a start (active) and a stop (inactive) time
 * If this program isn't set to do notifications then this will do nothing.
 * Only to be called for timed control. Timed control limits how
 * stop notifications work.
 */
void CtiLMProgramDirect::scheduleNotificationForTimedControl(const CtiTime&  start_time, const CtiTime& stop_time)
{
    scheduleStartNotification(start_time);
    scheduleStopNotificationForTimedControl(stop_time);
}

/**
 * Sets up the notification of groups give a start time
 * If this program isn't set to do start (active) notifications then this will do nothing
 */
void CtiLMProgramDirect::scheduleStartNotification(const CtiTime& start_time)
{
    // -1 indicates we shouldn't notify on active
    if( getNotifyActiveOffset() != -1 )
    {
        CtiTime notifyStartTime(start_time);
        notifyStartTime.addSeconds(-1*getNotifyActiveOffset());

        if( notifyStartTime != getNotifyActiveTime() )
        {
            setNotifyActiveTime(notifyStartTime);

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, getPAOName() << " going to notify of start @: " << notifyStartTime.asString());
            }
        }
    }
}

/**
 * If this program is set to do adjust notifications, schedule
 * an adjustment notification. Adjustment notifications will not
 * go out if the stop time is now or earlier as the stop
 * notification is supposed to this. Also the notify active time
 * must be < now as we should not send out an adjust before the
 * start.
 */
void CtiLMProgramDirect::requestAdjustNotification(const CtiTime& stop_time)
{
    if( _adjustment_notification_enabled && stop_time > CtiTime::now() && getNotifyActiveTime() < CtiTime::now() )
    {
        setAdjustNotificationPending(true);

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, getPAOName() << " going to notify of adjustment");
        }
    }
}

/**
 * Sets up the notification of groups give a stop time
 * If this program isn't set to do stop (inactive) notifications then this will do nothing
 */
void CtiLMProgramDirect::scheduleStopNotification(const CtiTime& stop_time)
{
    // -1 indicates we shouldn't notify on inactive
    if( getNotifyInactiveOffset() != -1 )
    {
        CtiTime notifyStopTime(stop_time);
        notifyStopTime.addSeconds(getNotifyInactiveOffset());

        if( notifyStopTime != getNotifyInactiveTime() )
        {
            setNotifyInactiveTime(notifyStopTime);

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, getPAOName() << " going to notify of stop @: " << notifyStopTime.asString());
            }
        }
    }
}

/**
 * Timed version of scheduleStopNotification(). Will not
 * schedule a message for after the timed stop. If a stop is set
 * to go after the expected stop, it will be scheduled for the
 * expected stop.
 *
 * Sets up the notification of groups give a stop time If this
 * program isn't set to do stop (inactive) notifications then
 * this will do nothing
 */
void CtiLMProgramDirect::scheduleStopNotificationForTimedControl(const CtiTime& stop_time)
{
    // -1 indicates we shouldn't notify on inactive
    if( getNotifyInactiveOffset() != -1 )
    {
        CtiTime notifyStopTime(stop_time);
        if( getNotifyInactiveOffset() < 0 )
        {
            notifyStopTime.addSeconds(getNotifyInactiveOffset());
        }

        if( notifyStopTime != getNotifyInactiveTime() )
        {
            setNotifyInactiveTime(notifyStopTime);

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, getPAOName() << " going to notify of stop @: " << notifyStopTime.asString());
            }
        }
    }
}


bool CtiLMProgramDirect::sendSimpleThermostatMessage(CtiLMProgramDirectGear* currentGearObject, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, double &expectedLoadReduced, bool isRefresh)
{
    bool retVal = false;
    const CtiLMProgramThermostatGear * thermostatGearObject = static_cast<const CtiLMProgramThermostatGear*>( currentGearObject );

    LONG maxTotalMinutes = thermostatGearObject->getDelayTime();

    LONG totalMinutes;
    if( getDirectStopTime() != gInvalidCtiTime )
        totalMinutes = (getDirectStopTime().seconds() - getDirectStartTime().seconds()) / 60;
    else
        totalMinutes = maxTotalMinutes;

    // We could check for a midnight boundary, but I think maxTotalMinutes is good enough.
    if( totalMinutes > maxTotalMinutes )
    {
        totalMinutes = maxTotalMinutes;
    }
    CtiTime endTime(getDirectStartTime().seconds() + totalMinutes*60);
    CtiTime now;

    for( CtiLMGroupIter i = _lmprogramdirectgroups.begin(); i != _lmprogramdirectgroups.end(); i++ )
    {
        CtiLMGroupPtr currentLMGroup  = *i;
        CtiLMGroupConstraintChecker con_checker(*this, currentLMGroup, currentTime);
        long controlTime = endTime.seconds() - now.seconds(); //Control time left from right now!

        if( (currentLMGroup->readyToControlAt(now) || !isRefresh) &&
            !currentLMGroup->getDisableFlag() &&
            !currentLMGroup->getControlInhibit() &&
            (getConstraintOverride() || con_checker.checkControl(controlTime, true))  )
        {
            // Ok, so controlTime is the time from now until when we have to stop controlling..
            // Then total control time is start time - (now+calculated control time);
            int totalControlMinutes = ((now.seconds() + controlTime) - getDirectStartTime().seconds())/60;
            int minutesFromBegin = (now.seconds() - getDirectStartTime().seconds())/60;
            if( !isRefresh && minutesFromBegin < 5 ) //A little sloppy here, why 5 minutes? It is assumed there is no resend faster than 5 minutes..
            {
                minutesFromBegin = 0;
            }
            if( isExpresscomGroup(currentLMGroup->getPAOType()) )
            {
                std::string logMessage;

                // XXX thermostat constraints??

                CtiRequestMsg* requestMsg = currentLMGroup->createSetPointSimpleMsg(*thermostatGearObject, totalControlMinutes, minutesFromBegin, defaultLMStartPriority, logMessage);
                if ( requestMsg )   // valid request - signal the log message
                {
                    multiDispatchMsg->insert( CTIDBG_new CtiSignalMsg( SYS_PID_LOADMANAGEMENT,
                                                                       0,
                                                                       logMessage,
                                                                       currentLMGroup->getPAOName() + " / (" + CtiNumStr( currentLMGroup->getPAOId() ) + "): Thermostat Simple Set Point",
                                                                       LoadMgmtLogType,
                                                                       SignalEvent,
                                                                       "(yukon system)",
                                                                       0,
                                                                       defaultLMStartPriority - 1 ) );
                }
                startGroupControl(currentLMGroup, requestMsg, multiPilMsg);
                currentLMGroup->setNextControlTime(gInvalidCtiTime);
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Thermostat Simple Set Point, LM Program: " << getPAOName());
                }

                retVal = true;

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
                CTILOG_INFO(dout, "Program: " << getPAOName() << ", can not Thermostat Set Point command a non-Expresscom group: " << currentLMGroup->getPAOName());
            }
        }
    }
    return retVal;
}

/**
 * recordHistory assumes that the current gear object is set for
 * the current or starting control.
 *
 * @param action LMHistoryActions
 * @param time CtiTime&
 *
 * @return bool
 */
bool CtiLMProgramDirect::recordHistory(CtiTableLMProgramHistory::LMHistoryActions action, CtiTime &time)
{
    bool retVal  = false;

    CtiLMProgramDirectGear* gear = getCurrentGearObject();

    if( action == CtiTableLMProgramHistory::Start )
    {
        setCurrentHistLogId(CtiTableLMProgramHistory::getNextProgramHistId()); // SynchronizedIdGen("LMProgramHistoryID", 1);
    }
    if( gear != NULL)
    {
        _PROGRAM_HISTORY_QUEUE.push(CtiTableLMProgramHistory(getCurrentHistLogId(), getPAOId(), gear->getUniqueID(), action, getPAOName(), getAndClearChangeReason(), getLastUser(), gear->getGearName(), time));
        retVal = true;
    }

    return retVal;
}

/*---------------------------------------------------------------------------
    setProgramState

    Sets the current state of the program. Also writes out historical program
    tables and requires the program to be properly loaded before it is called.
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramDirect::setProgramState(LONG newState)
{
    long currentState = getProgramState();

    if( currentState != newState &&
        newState     != CtiLMProgramBase::GearChangeState &&
        currentState != CtiLMProgramBase::GearChangeState )
    {
        if( isAControlState(newState) && !isAControlState(currentState) )
        {
            //It is a start
            recordHistory(CtiTableLMProgramHistory::Start, CtiTime::now());
        }
        else if( isAStopState(newState) && currentState != CtiLMProgramBase::ScheduledState )
        {
            // It is a stop.
            CtiTime recordTime = std::min(CtiTime::now(), getDirectStopTime());
            if( recordTime.is_special() || recordTime == gInvalidCtiTime )
            {
                recordTime = recordTime.now();
            }
            recordHistory(CtiTableLMProgramHistory::Stop, recordTime);
        }
    }

    Inherited::setProgramState(newState);
    return *this;
}

/**
 * Returns true if the given state is considered an active
 * (controlling) state. Uses states from lmprogrambase.h
 *
 * @param state LONG
 *
 * @return bool
 */
bool CtiLMProgramDirect::isAControlState(int state)
{
    bool retVal = false;
    if( state == ActiveState || state == ManualActiveState ||
        state == FullyActiveState || state == StoppingState ||
        state == NonControllingState || state == TimedActiveState )
    {
        retVal = true;
    }
    return retVal;
}

bool CtiLMProgramDirect::isAStopState(int state)
{
    bool retVal = false;
    if( state == InactiveState )
    {
        retVal = true;
    }
    return retVal;
}

bool CtiLMProgramDirect::isControlling()
{
    return isAControlState(getProgramState());
}

void CtiLMProgramDirect::setCurrentHistLogId(unsigned long logID)
{
    if( _curLogID != logID )
    {
        _curLogID = logID;
        setDirty(true);
    }
}

unsigned long CtiLMProgramDirect::getCurrentHistLogId()
{
    return _curLogID;
}

void CtiLMProgramDirect::setChangeReason(const string& reason)
{
    _change_reason = reason;
}

void CtiLMProgramDirect::setLastUser(const string& user)
{
    _last_user = user;
}

// Returns the last set change reason and clears out the reason.
// Each reason can only be used once!
string CtiLMProgramDirect::getAndClearChangeReason()
{
    string tempStr = _change_reason;
    _change_reason = "";
    return tempStr;
}

string CtiLMProgramDirect::getLastUser()
{
    return _last_user;
}

bool CtiLMProgramDirect::getHasBeatThePeakGear() const
{
    return _hasBeatThePeakGear;
}

void CtiLMProgramDirect::setHasBeatThePeakGear(bool hasBeatThePeakGear)
{
    _hasBeatThePeakGear = hasBeatThePeakGear;
}

void CtiLMProgramDirect::setPendingGroupsInactive()
{
    for each ( CtiLMGroupPtr group in _lmprogramdirectgroups )
    {
        if ( group->getGroupControlState() == CtiLMGroupBase::ActivePendingState )
        {
            group->setGroupControlState( CtiLMGroupBase::InactiveState );
        }
    }
}

// Static Members
int CtiLMProgramDirect::defaultLMStartPriority = 13;
int CtiLMProgramDirect::defaultLMRefreshPriority = 11;

int CtiLMProgramDirect::invalidNotifyOffset = -1;
