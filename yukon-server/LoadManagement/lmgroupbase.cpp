#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupbase.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "numstr.h"
#include "ctidate.h"
#include "database_writer.h"
#include "database_util.h"

#include <algorithm>


#define GROUP_RAMPING_IN 0x00000001
#define GROUP_RAMPING_OUT 0x00000002

using std::transform;
using std::vector;
using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;
extern std::set<long> _CHANGED_GROUP_LIST;

std::size_t calculateMemoryConsumption( const CtiLMGroupBase * g )
{
    return  g->getFixedSize()
        +   g->getVariableSize();
}

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupBase::CtiLMGroupBase()
: _next_control_time(gInvalidCtiTime),
_controlstarttime(gInvalidCtiTime),
_controlcompletetime(gInvalidCtiTime),
_daily_ops(0),
_lastStopTimeSent(gInvalidCtiTime),
_groupcontrolstate(InactiveState),
_insertDynamicDataFlag(TRUE),
_internalState(0)
{
}

CtiLMGroupBase::CtiLMGroupBase(Cti::RowReader &rdr)
: _next_control_time(gInvalidCtiTime),
_controlstarttime(gInvalidCtiTime),
_controlcompletetime(gInvalidCtiTime),
_daily_ops(0),
_lastStopTimeSent(gInvalidCtiTime),
_groupcontrolstate(InactiveState),
_insertDynamicDataFlag(TRUE),
_internalState( 0 )
{
    restore(rdr);
}

CtiLMGroupBase::CtiLMGroupBase(const CtiLMGroupBase& groupbase)
{
    operator=(groupbase);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupBase::~CtiLMGroupBase()
{
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getPAOId() const
{

    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const string& CtiLMGroupBase::getPAOCategory() const
{

    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const string& CtiLMGroupBase::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const string& CtiLMGroupBase::getPAOName() const
{

    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getPAOType() const
{

    return _paoType;
}

/*---------------------------------------------------------------------------
    getPAOTypeString

    Returns the pao type string of the substation
---------------------------------------------------------------------------*/
const string& CtiLMGroupBase::getPAOTypeString() const
{

    return _paoTypeString;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const string& CtiLMGroupBase::getPAODescription() const
{

    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the group
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::getDisableFlag() const
{

    return _disableflag;
}

/*---------------------------------------------------------------------------
    getGroupOrder

    Returns the order of the group in a program
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getGroupOrder() const
{

    return _grouporder;
}

/*---------------------------------------------------------------------------
    getKWCapacity

    Returns the kw capacity of the group
---------------------------------------------------------------------------*/
DOUBLE CtiLMGroupBase::getKWCapacity() const
{

    return _kwcapacity;
}

/*---------------------------------------------------------------------------
    getChildOrder

    Returns the order of the group macro children in a program
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getChildOrder() const
{

    return _childorder;
}

/*---------------------------------------------------------------------------
    getAlarmInhibit

    Returns the alarm inhibit flag of the group
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::getAlarmInhibit() const
{

    return _alarminhibit;
}

/*---------------------------------------------------------------------------
    getControlInhibit

    Returns the control inhibit flag of the group
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::getControlInhibit() const
{

    return _controlinhibit;
}

/*---------------------------------------------------------------------------
    getGroupControlState

    Returns the control state of the group
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getGroupControlState() const
{

    return _groupcontrolstate;
}

/*---------------------------------------------------------------------------
    getCurrentHoursDaily

    Returns the current hours for this day for the group in seconds
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getCurrentHoursDaily() const
{

    return _currenthoursdaily;
}

/*---------------------------------------------------------------------------
    getCurrentHoursMonthly

    Returns the current hours for this month for the group in seconds
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getCurrentHoursMonthly() const
{

    return _currenthoursmonthly;
}

/*---------------------------------------------------------------------------
    getCurrentHoursSeasonal

    Returns the current hours for this season for the group in seconds
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getCurrentHoursSeasonal() const
{

    return _currenthoursseasonal;
}

/*---------------------------------------------------------------------------
    getCurrentHoursAnnually

    Returns the current hours for this year for the group in seconds
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getCurrentHoursAnnually() const
{

    return _currenthoursannually;
}

/*---------------------------------------------------------------------------
    getLastControlSent

    Returns the time of the last control sent for the group
---------------------------------------------------------------------------*/
const CtiTime& CtiLMGroupBase::getLastControlSent() const
{

    return _lastcontrolsent;
}

/*---------------------------------------------------------------------------
    getControlStartTime

    Returns the time when the current group control started
---------------------------------------------------------------------------*/
const CtiTime& CtiLMGroupBase::getControlStartTime() const
{
    return _controlstarttime;
}

/*---------------------------------------------------------------------------
    getControlCompleteTime

    Returns the time when the current group control is scheduled to complete
---------------------------------------------------------------------------*/
const CtiTime& CtiLMGroupBase::getControlCompleteTime() const
{
    return _controlcompletetime;
}

/*----------------------------------------------------------------------------
  getNextControlTime

  Returns the time this group will be controlled next
----------------------------------------------------------------------------*/
const CtiTime& CtiLMGroupBase::getNextControlTime() const
{
    return _next_control_time;
}

/*----------------------------------------------------------------------------
  getLastStopTimeSent

  Returns the time this group was most recently told to stop
  Currently only maintained by Digi SEP group
----------------------------------------------------------------------------*/
const CtiTime& CtiLMGroupBase::getLastStopTimeSent() const
{
    return _lastStopTimeSent;
}

/*-----------------------------------------------------------------------------
  getDynamicTimestamp

  Returns the dynamic info timestamp
-----------------------------------------------------------------------------*/
const CtiTime& CtiLMGroupBase::getDynamicTimestamp() const
{
    return _dynamic_timestamp;
}

/*---------------------------------------------------------------------------
    getDailyOps

    Returns the number of times this program has operated today
 ---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getDailyOps()
{
    updateDailyOps();
    return _daily_ops; //TODO: check if this is the value for today!
}

/*----------------------------------------------------------------------------
  getIsRampingIn

  Returns whether or not the group is currently ramping in
----------------------------------------------------------------------------*/
bool CtiLMGroupBase::getIsRampingIn() const
{
    return _internalState & GROUP_RAMPING_IN;
}

/*----------------------------------------------------------------------------
  getIsRampinOut

  Returns whether or not he group is currently ramping out
----------------------------------------------------------------------------*/
bool CtiLMGroupBase::getIsRampingOut() const
{
    return _internalState & GROUP_RAMPING_OUT;
}

/*----------------------------------------------------------------------------
  getCurrentControlDuration

  Returns the amount of time in seconds that we have been controlling.
  If the group is inactive this will be zero.
-----------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getCurrentControlDuration() const
{
    return(getGroupControlState() == ActiveState ?
           CtiTime::now().seconds() - getControlStartTime().seconds() : 0);
}

/*---------------------------------------------------------------------------
    getHoursDailyPointId

    Returns the point id of the daily control hours point for the group
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getHoursDailyPointId() const
{

    return _hoursdailypointid;
}

/*---------------------------------------------------------------------------
    getHoursMonthlyPointId

    Returns the point id of the monthly control hours point for the group
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getHoursMonthlyPointId() const
{

    return _hoursmonthlypointid;
}

/*---------------------------------------------------------------------------
    getHoursSeasonalPointId

    Returns the point id of the seasonal control hours point for the group
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getHoursSeasonalPointId() const
{

    return _hoursseasonalpointid;
}

/*---------------------------------------------------------------------------
    getHoursAnnuallyPointId

    Returns the point id of the annual control hours point for the group
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getHoursAnnuallyPointId() const
{

    return _hoursannuallypointid;
}

/*---------------------------------------------------------------------------
    getControlStatusPointId

    Returns the point id of the pseudo control point for this group
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getControlStatusPointId() const
{
    return _controlstatuspointid;
}

/*---------------------------------------------------------------------------
    getLastControlString

    Returns the last control string of the substation
---------------------------------------------------------------------------*/
const string& CtiLMGroupBase::getLastControlString() const
{

    return _lastcontrolstring;
}

/*---------------------------------------------------------------------------
    Returns whether the group is set to resend a control at currentTime
---------------------------------------------------------------------------*/
bool CtiLMGroupBase::readyToControlAt(CtiTime &currentTime) const
{
    return ( (_next_control_time != gInvalidCtiTime) &&  (currentTime >= _next_control_time) );
}

/*---------------------------------------------------------------------------
    Returns the internal state
---------------------------------------------------------------------------*/
unsigned CtiLMGroupBase::getInternalState() const
{
    return _internalState;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOCategory(const string& category)
{

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOClass(const string& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOName(const string& name)
{

    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAODescription(const string& description)
{

    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flaf of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setDisableFlag(BOOL disable)
{
    if( disable != _disableflag )
    {
        _disableflag = disable;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setGroupOrder

    Sets the order of the group in a program
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setGroupOrder(LONG order)
{

    _grouporder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setKWCapacity

    Sets the kw capacity of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setKWCapacity(DOUBLE kwcap)
{

    _kwcapacity = kwcap;
    return *this;
}

/*---------------------------------------------------------------------------
    setChildOrder

    Sets the order of the group macro children in a program
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setChildOrder(LONG order)
{

    _childorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setAlarmInhibit

    Sets the alarm inhibit flag of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setAlarmInhibit(BOOL alarm)
{

    _alarminhibit = alarm;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlInhibit

    Sets the control inhibit flag of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setControlInhibit(BOOL control)
{

    _controlinhibit = control;
    return *this;
}

/*---------------------------------------------------------------------------
    setGroupControlState

    Sets the control state of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setGroupControlState(LONG controlstate)
{
    if( _groupcontrolstate != controlstate )
    {
        _groupcontrolstate = controlstate;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursDaily

    Sets the current hours for this day for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursDaily(LONG daily)
{
    if( _currenthoursdaily != daily )
    {
        _currenthoursdaily = daily;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursMonthly

    Sets the current hours for this month for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursMonthly(LONG monthly)
{
    if( _currenthoursmonthly != monthly )
    {
        _currenthoursmonthly = monthly;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursSeasonal

    Sets the current hours for this season for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursSeasonal(LONG seasonal)
{
    if( _currenthoursseasonal != seasonal )
    {
        _currenthoursseasonal = seasonal;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursAnnually

    Sets the current hours for this year for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursAnnually(LONG annually)
{
    if( _currenthoursannually != annually )
    {
        _currenthoursannually = annually;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setLastControlSent

    Sets the time of the last control sent for the program
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setLastControlSent(const CtiTime& controlsent)
{
    if( _lastcontrolsent != controlsent )
    {
        _lastcontrolsent = controlsent;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setControlStartTime

    Sets the time when the current group control started
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setControlStartTime(const CtiTime& start)
{
    if( _controlstarttime != start )
    {
        _controlstarttime = start;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setControlCompleteTime

    Sets the time when the current group control is scheduled to complete
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setControlCompleteTime(const CtiTime& complete)
{
    if( _controlcompletetime != complete )
    {
        _controlcompletetime = complete;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setNextRefreshTime

    Sets the time when the current group will be controlled next due to refresh.
    If the refreshRate is 0, the group will receive no futher refreshes.
---------------------------------------------------------------------------*/
void CtiLMGroupBase::setNextRefreshTime( const CtiTime currentTime, long refreshRate )
{
    setNextControlTime( refreshRate > 0 ? currentTime + refreshRate : gEndOfCtiTime );
}

/*---------------------------------------------------------------------------
    setNextControlTime

    Sets the time when the current group will be controlled next
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setNextControlTime(const CtiTime& controltime)
{
    if( _next_control_time != controltime )
    {
        _next_control_time = controltime;
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setLastStopTimeSent

  Sets the time that the group will stop at
  Currently only maintained by Digi SEP group
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setLastStopTimeSent(const CtiTime& lastStopTimeSent)
{
    if( _lastStopTimeSent != lastStopTimeSent )
    {
        _lastStopTimeSent = lastStopTimeSent;
        setDirty(true);
    }
    return *this;
}

/*-----------------------------------------------------------------------------
  setDynamicTimestamp

  Sets the dynamic info timestamp
-----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setDynamicTimestamp(const CtiTime& timestamp)
{
    if( _dynamic_timestamp != timestamp )
    {
        _dynamic_timestamp = timestamp;
        setDirty(true);
    }
    return *this;
}

/*-----------------------------------------------------------------------------
  incrementDailyOps
-----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::incrementDailyOps()
{
    updateDailyOps();
    _daily_ops++;
    setDirty(true);
    return *this;
}

/*-----------------------------------------------------------------------------
  resetDailyOps
  This only exists so the controlareastore can set the dailyops when
  it reads it from the database.  usually you won't call this directly.
-----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::resetDailyOps(int ops)
{
    _daily_ops = ops;
    setDirty(true);
    return *this;
}

void CtiLMGroupBase::setInternalState(unsigned state)
{
    if( _internalState != state )
    {
        _internalState = state;
        setDirty(true);
    }
}

/*---------------------------------------------------------------------------
    setHoursDailyPointId

    Sets the point id of the point that tracks daily control hours for the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setHoursDailyPointId(LONG dailyid)
{

    _hoursdailypointid = dailyid;
    return *this;
}

/*---------------------------------------------------------------------------
    setHoursMonthlyPointId

    Sets the point id of the point that tracks monthly control hours for the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setHoursMonthlyPointId(LONG monthlyid)
{

    _hoursmonthlypointid = monthlyid;
    return *this;
}

/*---------------------------------------------------------------------------
    setHoursSeasonalPointId

    Sets the point id of the point that tracks seasonal control hours for the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setHoursSeasonalPointId(LONG seasonalid)
{

    _hoursseasonalpointid = seasonalid;
    return *this;
}

/*---------------------------------------------------------------------------
    setHoursAnnuallyPointId

    Sets the point id of the point that tracks annually control hours for the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setHoursAnnuallyPointId(LONG annuallyid)
{

    _hoursannuallypointid = annuallyid;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatusPointId

    Sets the point id of the pseudo control point for this group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setControlStatusPointId(LONG cntid)
{
    _controlstatuspointid = cntid;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastControlString

    Sets the last control string of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setLastControlString(const string& controlstr)
{

    _lastcontrolstring = controlstr;
    return *this;
}

/*-------------------------------------------------------------------------
    createLatchingRequestMsg

    .
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupBase::createLatchingRequestMsg(bool do_shed, int priority) const
{
    string control_str;
    if( do_shed )  //shed
    {
        control_str = "control shed";
    }
    else  //restore
    {
        control_str = "control restore";
    }

    CtiRequestMsg* req_msg = buildRequestMessage( control_str, priority );

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        Cti::FormattedList list;

        list << "Sending latch request";
        list.add("LM Group")        << getPAOName();
        list.add("control string")  << control_str;
        list.add("priority")        << priority;

        CTILOG_DEBUG(dout, list);
    }
    return req_msg;
}

/*-------------------------------------------------------------------------
    createLatchingCommandMsg

    .
--------------------------------------------------------------------------*/
CtiCommandMsg* CtiLMGroupBase::createLatchingCommandMsg(LONG rawState, int priority) const
{
    CtiCommandMsg* returnCommandMsg = new CtiCommandMsg();
    returnCommandMsg->setOperation(CtiCommandMsg::ControlRequest);

    std::vector<int> opArgList;
    opArgList.push_back(-1);
    opArgList.push_back(getPAOId());
    opArgList.push_back(1);//this is control offset 1
    opArgList.push_back(rawState);
    opArgList.push_back(1);//this simulates a boolean to use the third integer as a control offset rather than a point id

    returnCommandMsg->setOpArgList(opArgList);

    returnCommandMsg->setMessagePriority(priority);

    if( _LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending base latch command, LM Group: " << getPAOName() << ", raw state: " << rawState << ", priority: " << priority);
    }
    return returnCommandMsg;
}

/*-------------------------------------------------------------------------
    createTrueCycleRequestMsg

    .
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupBase::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO(dout, "Can not True Cycle a non-Expresscom Load Management Group,");
    return NULL;
}

/*-------------------------------------------------------------------------
    createTargetCycleRequestMsg

    .
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupBase::createTargetCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority, DOUBLE kwh, CtiTime originalTime, const string& additionalInfo) const
{
    CTILOG_INFO(dout, "Can not Target Cycle a non-Expresscom Load Management Group,");
    return NULL;
}

/*-------------------------------------------------------------------------
    createTrueCycleRequestMsg

    .
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupBase::createSetPointRequestMsg(const CtiLMProgramThermostatGear & gear, int priority, std::string & logMessage) const
{
    CTILOG_INFO(dout, "Can not create a Set Point command to a non-Expresscom Load Management Group,");
    return NULL;
}

// CreateSetPointSimpleMessage
// Only in expresscom.
CtiRequestMsg* CtiLMGroupBase::createSetPointSimpleMsg(const CtiLMProgramThermostatGear & gear, LONG totalTime, LONG minutesFromBegin, int priority, std::string & logMessage) const
{
    CTILOG_INFO(dout, "Can not create a Set Point command to a non-Expresscom Load Management Group,");
    return NULL;
}

// Sends a message that tells device to finish out it's current cycle then end. A "soft" stop.
// In some protocols this may do different things, this is primarily for Expresscom.
CtiRequestMsg* CtiLMGroupBase::createStopCycleMsg(LONG period, CtiTime &currentTime)
{
    const int priority = 11;
    string controlString = "control terminate";

    CTILOG_INFO(dout, "Sending terminate to LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);

    setLastControlString(controlString);
    setLastControlSent(currentTime);

    return buildRequestMessage( controlString, priority );
}

CtiRequestMsg * CtiLMGroupBase::buildRequestMessage( const std::string & request, const int priority ) const
{
    return new CtiRequestMsg( getPAOId(),
                              request,
                              0,
                              0,
                              0,
                              MacroOffset::none,
                              0,
                              0,
                              priority );
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::operator=(const CtiLMGroupBase& right)
{
    CtiMemDBObject::operator=(right);

    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paoType = right._paoType;
        _paoTypeString = right._paoTypeString;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _grouporder = right._grouporder;
        _kwcapacity = right._kwcapacity;
        _childorder = right._childorder;
        _alarminhibit = right._alarminhibit;
        _controlinhibit = right._controlinhibit;
        _groupcontrolstate = right._groupcontrolstate;
        _currenthoursdaily = right._currenthoursdaily;
        _currenthoursmonthly = right._currenthoursmonthly;
        _currenthoursseasonal = right._currenthoursseasonal;
        _currenthoursannually = right._currenthoursannually;
        _lastcontrolsent = right._lastcontrolsent;
        _controlstarttime = right._controlstarttime;
        _controlcompletetime = right._controlcompletetime;
        _next_control_time = right._next_control_time;
        _hoursdailypointid = right._hoursdailypointid;
        _hoursmonthlypointid = right._hoursmonthlypointid;
        _hoursseasonalpointid = right._hoursseasonalpointid;
        _hoursannuallypointid = right._hoursannuallypointid;
        _controlstatuspointid = right._controlstatuspointid;
        _lastcontrolstring = right._lastcontrolstring;
        _internalState = right._internalState;
        _daily_ops = right._daily_ops;
        _lastStopTimeSent = right._lastStopTimeSent;
    }

    return *this;
}

/*----------------------------------------------------------------------------
  operator<

  This is defined to indicate this groups order inside a program
----------------------------------------------------------------------------*/
bool CtiLMGroupBase::operator<(const CtiLMGroupBase& right) const
{
    return(getGroupOrder() < right.getGroupOrder() ||
           (getGroupOrder() == right.getGroupOrder() && getChildOrder() < right.getChildOrder()));
}

/*---------------------------------------------------------------------------
    buildShedString

    Builds a shed time string given a period of time in seconds.
---------------------------------------------------------------------------*/
string CtiLMGroupBase::buildShedString(LONG shedTime) const
{
    char tempchar[64];
    string retStr;

    if( (shedTime % 3600) == 0 )
    {
        LONG hourShedTime = shedTime/3600;
        _ltoa(hourShedTime,tempchar,10);
        retStr += tempchar;
        retStr += "h";
    }
    else if( (shedTime % 60) == 0 )
    {
        LONG minuteShedTime = shedTime/60;
        _ltoa(minuteShedTime,tempchar,10);
        retStr += tempchar;
        retStr += "m";
    }
    else
    {
        _ltoa(shedTime,tempchar,10);
        retStr += tempchar;
        retStr += "s";
    }

    return retStr;
}

/*---------------------------------------------------------------------------
    buildPeriodString

    Builds a period time string given a period of time in seconds.
    Period strings are always in terms of minutes!
    i.e. 61 seconds is 1m
---------------------------------------------------------------------------*/
string CtiLMGroupBase::buildPeriodString(LONG periodTime) const
{
    return CtiNumStr(periodTime/(LONG)60);
}

/*---------------------------------------------------------------------------
    convertSecondsToEvenTimeString

    doesMasterCycleNeedToBeUpdated
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime)
{
    /*{
        CTILOG_INFO(dout, "PAOId: " << getPAOId() << " Group Type: " << getPAOType() << " does not need to be Master Cycle refreshed.");
    }*/
    return FALSE;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMGroupBase::restore(Cti::RowReader &rdr)
{
    CtiTime dynamicTimeStamp;
    string tempBoolString;
    _insertDynamicDataFlag = FALSE;

    rdr["groupid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paoTypeString;

    _paoType = resolvePAOType(_paocategory,_paoTypeString);

    rdr["description"] >> _paodescription;

    rdr["disableflag"] >> tempBoolString;

    transform (tempBoolString.begin(),tempBoolString.end(), tempBoolString.begin(), tolower);

    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);

    rdr["alarminhibit"] >> tempBoolString;

    transform (tempBoolString.begin(),tempBoolString.end(), tempBoolString.begin(), tolower);

    setAlarmInhibit(tempBoolString=="y"?TRUE:FALSE);

    rdr["controlinhibit"] >> tempBoolString;

    transform (tempBoolString.begin(),tempBoolString.end(), tempBoolString.begin(), tolower);

    setControlInhibit(tempBoolString=="y"?TRUE:FALSE);
//    rdr["grouporder"] >> _grouporder;
    rdr["kwcapacity"] >> _kwcapacity;

    /*  rdr["childorder"] >> isNull;
    if( !isNull )
    {
        rdr["childorder"] >> _childorder;
    }
    else
    {
        _childorder = 0;
    }
    */
//    rdr["programid"] >> _lmprogramid;
#ifdef _SHOULD_WE_LOAD_THIS_HERE_
    rdr["groupcontrolstate"] >> isNull;
    if( !isNull )
    {
        rdr["groupcontrolstate"] >> _groupcontrolstate;
        rdr["currenthoursdaily"] >> _currenthoursdaily;
        rdr["currenthoursmonthly"] >> _currenthoursmonthly;
        rdr["currenthoursseasonal"] >> _currenthoursseasonal;
        rdr["currenthoursannually"] >> _currenthoursannually;
        rdr["lastcontrolsent"] >> _lastcontrolsent;
        rdr["timestamp"] >> dynamicTimeStamp;
        rdr["controlstarttime"] >> _controlstarttime;
        rdr["controlcompletetime"] >> _controlcompletetime;
        rdr["nextcontroltime"] >> _next_control_time;
        rdr["internalstate"] >> _internalState;
        rdr["laststoptimesent"] >> _lastStopTimeSent;

        _insertDynamicDataFlag = FALSE;
        setDirty(false);
    }
    else
#endif
    {
        setGroupControlState(InactiveState);
        setCurrentHoursDaily(0);
        setCurrentHoursMonthly(0);
        setCurrentHoursSeasonal(0);
        setCurrentHoursAnnually(0);
        setLastControlSent(gInvalidCtiTime);
        setControlStartTime(gInvalidCtiTime);
        setControlCompleteTime(gInvalidCtiTime);
        setNextControlTime(gInvalidCtiTime);
        resetDailyOps(0);
        setLastStopTimeSent(gInvalidCtiTime);
        _internalState = 0;

        _insertDynamicDataFlag = TRUE;
        setDirty(true);
    }

    setHoursDailyPointId(0);
    setHoursMonthlyPointId(0);
    setHoursSeasonalPointId(0);
    setHoursAnnuallyPointId(0);
    setLastControlString("");
/* NOTE: moved to reset() loop
    rdr["pointid"] >> isNull;
    if( !isNull )
    {
        LONG tempPointId = -1000;
        LONG tempPointOffset = -1000;
        string tempPointType = "(none)";
        rdr["pointid"] >> tempPointId;
        rdr["pointoffset"] >> tempPointOffset;
        rdr["pointtype"] >> tempPointType;

        if( resolvePointType(tempPointType) == AnalogPointType )
        {
            if( tempPointOffset == DAILYCONTROLHISTOFFSET )
            {
                _hoursdailypointid = tempPointId;
            }
            else if( tempPointOffset == MONTHLYCONTROLHISTOFFSET )
            {
                _hoursmonthlypointid = tempPointId;
            }
            else if( tempPointOffset == SEASONALCONTROLHISTOFFSET )
            {
                _hoursseasonalpointid = tempPointId;
            }
            else if( tempPointOffset == ANNUALCONTROLHISTOFFSET )
            {
                _hoursannuallypointid = tempPointId;
            }
        }
    }
*/
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMGroupBase::dumpDynamicData()
{
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this group.
---------------------------------------------------------------------------*/
void CtiLMGroupBase::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( !isDirty() )
    {
        return;
    }

    if( !_insertDynamicDataFlag )
    {
        static const std::string sql_update = "update dynamiclmgroup"
                                               " set "
                                                    "groupcontrolstate = ?, "
                                                    "currenthoursdaily = ?, "
                                                    "currenthoursmonthly = ?, "
                                                    "currenthoursseasonal = ?, "
                                                    "currenthoursannually = ?, "
                                                    "lastcontrolsent = ?, "
                                                    "timestamp = ?, "
                                                    "controlstarttime = ?, "
                                                    "controlcompletetime = ?, "
                                                    "nextcontroltime = ?, "
                                                    "internalstate = ?, "
                                                    "dailyops = ?, "
                                                    "laststoptimesent = ?"
                                               " where "
                                                    "deviceid = ?";

        Cti::Database::DatabaseWriter   updater(conn, sql_update);

        updater
            << getGroupControlState()
            << getCurrentHoursDaily()
            << getCurrentHoursMonthly()
            << getCurrentHoursSeasonal()
            << getCurrentHoursAnnually()
            << getLastControlSent()
            << currentDateTime
            << getControlStartTime()
            << getControlCompleteTime()
            << getNextControlTime()
            << _internalState
            << getDailyOps()
            << getLastStopTimeSent()
            << getPAOId();

        if( ! Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
        {
            return;
        }
    }
    else
    {
        static const std::string sql_insert = "insert into dynamiclmgroup values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        CTILOG_INFO(dout, "Inserted group into DynamicLMGroup: " << getPAOName());

        Cti::Database::DatabaseWriter   inserter(conn, sql_insert);

        inserter
            << getPAOId()
            << getGroupControlState()
            << getCurrentHoursDaily()
            << getCurrentHoursMonthly()
            << getCurrentHoursSeasonal()
            << getCurrentHoursAnnually()
            << getLastControlSent()
            << currentDateTime
            << getControlStartTime()
            << getControlCompleteTime()
            << getNextControlTime()
            << _internalState
            << getDailyOps()
            << getLastStopTimeSent();

        if( ! Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
        {
            return;
        }

        _insertDynamicDataFlag = FALSE;
    }

    _dynamic_timestamp = currentDateTime;
    resetDirty(); // setDirty inserts into the changed group list and we do not want to do that here.
}

/*----------------------------------------------------------------------------
  resetInternalState
  Reset internal state, that is, ramp in, ramp out currently
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::resetInternalState()
{
    if( _internalState != 0x00000000 )
    {
        _internalState = 0x00000000;
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  resetGroupControlState
  Will reset a pending state to inactive.
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::resetGroupControlState()
{
    int state = getGroupControlState();
    if( InactivePendingState == state || ActivePendingState == state )
    {
        setGroupControlState(InactiveState);
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setIsRampingIn
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setIsRampingIn(bool in)
{
    if( getIsRampingIn() != in )
    {
        if( in )
        {
            // We can't be ramping out and ramping in at the same time!
            if( _LM_DEBUG & LM_DEBUG_STANDARD && getIsRampingOut() )
            {
                CTILOG_DEBUG(dout, "Load Group: " << getPAOName() << " was ramping out and is now ramping in.");
            }
            setIsRampingOut(false);

            _internalState |= GROUP_RAMPING_IN;
        }
        else
        {
            _internalState &= ~GROUP_RAMPING_IN;
        }
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setIsRampingOut
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setIsRampingOut(bool out)
{
    if( getIsRampingOut() != out )
    {
        if( out )
        {
            // We can't be ramping out and ramping in at the same time!
            if( _LM_DEBUG & LM_DEBUG_STANDARD && getIsRampingIn() )
            {
                CTILOG_DEBUG(dout, "Load Group: " << getPAOName() << " was ramping in and is now ramping out.");
            }
            setIsRampingIn(false);

            _internalState |= GROUP_RAMPING_OUT;
        }
        else
        {
            _internalState &= ~GROUP_RAMPING_OUT;
        }

        setDirty(true);
    }
    return *this;
}

/*-----------------------------------------------------------------------------
  setDirty

  Sets the dirty flag and notifies LM that this group should be
  sent to clients
-----------------------------------------------------------------------------*/
void CtiLMGroupBase::setDirty(BOOL b)
{
    _CHANGED_GROUP_LIST.insert(getPAOId());
    CtiMemDBObject::setDirty(b);
}

/*-----------------------------------------------------------------------------
  updateDailyOps

  Resets the daily ops if necessary based on the dynamic timestamp
-----------------------------------------------------------------------------*/
void CtiLMGroupBase::updateDailyOps()
{
    // If we haven't written out dynamic data today
    CtiTime today;
    if( today.day() > _dynamic_timestamp.day() )
    {
        resetDailyOps();
    }
}

/*-----------------------------------------------------------------------------
  doesStopRequireCommandAt

  Checks whether a group stop at this point would require a command message,
  since the group may have already been told to stop at this time.

  Currently only maintained by Digi SEP Group
-----------------------------------------------------------------------------*/
bool CtiLMGroupBase::doesStopRequireCommandAt(const CtiTime &currentTime) const
{
    return true;
}

std::size_t CtiLMGroupBase::getVariableSize() const
{
    return  dynamic_sizeof( _paocategory )
        +   dynamic_sizeof( _paoclass )
        +   dynamic_sizeof( _paoname )
        +   dynamic_sizeof( _paoTypeString )
        +   dynamic_sizeof( _paodescription )
        +   dynamic_sizeof( _lastcontrolstring );
}

// Static Members
int CtiLMGroupBase::InactiveState = STATEZERO;
int CtiLMGroupBase::ActiveState = STATEONE;
int CtiLMGroupBase::InactivePendingState = STATETWO;
int CtiLMGroupBase::ActivePendingState = STATETHREE;
