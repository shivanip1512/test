/*---------------------------------------------------------------------------
        Filename:  lmgroupbase.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMGroupBase.
                        CtiLMGroupBase maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  2/9/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmgroupbase.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"
#include "numstr.h"

#define GROUP_RAMPING_IN 0x00000001
#define GROUP_RAMPING_OUT 0x00000002

extern ULONG _LM_DEBUG;
#ifdef _DEBUG_MEMORY    
LONG CtiLMGroupBase::numberOfReferences = 0;
#endif
/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupBase::CtiLMGroupBase()
    : _next_control_time(gInvalidRWDBDateTime),
      _controlstarttime(gInvalidRWDBDateTime),
      _controlcompletetime(gInvalidRWDBDateTime),
      _daily_ops(0),      
      _insertDynamicDataFlag(TRUE)
{
#ifdef _DEBUG_MEMORY    
        numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Constructor, Number of CtiLMGroupBase increased to: " << numberOfReferences << endl;
    }
#endif   
    resetInternalState();
}

CtiLMGroupBase::CtiLMGroupBase(RWDBReader& rdr)
    : _next_control_time(gInvalidRWDBDateTime),
      _controlstarttime(gInvalidRWDBDateTime),
      _controlcompletetime(gInvalidRWDBDateTime),
      _daily_ops(0),
      _insertDynamicDataFlag(TRUE)
{
#ifdef _DEBUG_MEMORY    
        numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Constructor(dbreader), Number of CtiLMGroupBaes increased to: " << numberOfReferences << endl;
    }
#endif    
    resetInternalState();
    restore(rdr);
}

CtiLMGroupBase::CtiLMGroupBase(const CtiLMGroupBase& groupbase)
{
#ifdef _DEBUG_MEMORY    
    numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Copy Constructor, Number of CtiLMGroupBaseincreased to: " << numberOfReferences << endl;
    }
#endif    
    operator=(groupbase);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupBase::~CtiLMGroupBase()
{
#ifdef _DEBUG_MEMORY    
    numberOfReferences--;
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << "Destructor, Number of CtiLMGroupBase decreased to: " << numberOfReferences << endl;
}
#endif
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
const RWCString& CtiLMGroupBase::getPAOCategory() const
{

    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAOName() const
{

    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
LONG CtiLMGroupBase::getPAOType() const
{

    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAODescription() const
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
const RWDBDateTime& CtiLMGroupBase::getLastControlSent() const
{

    return _lastcontrolsent;
}

/*---------------------------------------------------------------------------
    getControlStartTime

    Returns the time when the current group control started
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMGroupBase::getControlStartTime() const
{
    return _controlstarttime;
}

/*---------------------------------------------------------------------------
    getControlCompleteTime

    Returns the time when the current group control is scheduled to complete
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMGroupBase::getControlCompleteTime() const
{
    return _controlcompletetime;
}

/*----------------------------------------------------------------------------
  getNextControlTime

  Returns the time this group will be controlled next
----------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMGroupBase::getNextControlTime() const
{
    return _next_control_time;
}

/*-----------------------------------------------------------------------------
  getDynamicTimestamp

  Returns the dynamic info timestamp
-----------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMGroupBase::getDynamicTimestamp() const
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
    return (getGroupControlState() == ActiveState ?
	    RWTime::now().seconds() - getControlStartTime().seconds() : 0);
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
const RWCString& CtiLMGroupBase::getLastControlString() const
{

    return _lastcontrolstring;
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
CtiLMGroupBase& CtiLMGroupBase::setPAOCategory(const RWCString& category)
{

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOClass(const RWCString& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOName(const RWCString& name)
{

    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOType(LONG type)
{

    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAODescription(const RWCString& description)
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

    _disableflag = disable;
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
    if(_groupcontrolstate != controlstate)
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
    if(_currenthoursdaily != daily)
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
    if(_currenthoursmonthly != monthly)
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
    if(_currenthoursseasonal != seasonal)
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
    if(_currenthoursannually != annually)
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
CtiLMGroupBase& CtiLMGroupBase::setLastControlSent(const RWDBDateTime& controlsent)
{
    if(_lastcontrolsent != controlsent)
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
CtiLMGroupBase& CtiLMGroupBase::setControlStartTime(const RWDBDateTime& start)
{
    if(_controlstarttime != start)
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
CtiLMGroupBase& CtiLMGroupBase::setControlCompleteTime(const RWDBDateTime& complete)
{
    _controlcompletetime = complete;
    return *this;
}


/*---------------------------------------------------------------------------
    setNextControlTime

    Sets the time when the current group will be controlled next
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setNextControlTime(const RWDBDateTime& controltime)
{
    if(_next_control_time != controltime)
    {
        _next_control_time = controltime;
        setDirty(true);
    }
    return *this;
}

/*-----------------------------------------------------------------------------
  setDynamicTimestamp

  Sets the dynamic info timestamp
-----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setDynamicTimestamp(const RWDBDateTime& timestamp)
{
    if(_dynamic_timestamp != timestamp)
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
    _internalState = state;
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
CtiLMGroupBase& CtiLMGroupBase::setLastControlString(const RWCString& controlstr)
{

    _lastcontrolstring = controlstr;
    return *this;
}

/*-------------------------------------------------------------------------
    createLatchingRequestMsg

    .
--------------------------------------------------------------------------*/
CtiCommandMsg* CtiLMGroupBase::createLatchingRequestMsg(LONG rawState, int priority) const
{
    CtiCommandMsg* returnCommandMsg = new CtiCommandMsg();
    returnCommandMsg->setOperation(CtiCommandMsg::ControlRequest);

    RWTValOrderedVector<RWInteger> opArgList;
    opArgList.insert(RWInteger(-1));
    opArgList.insert(RWInteger(getPAOId()));
    opArgList.insert(RWInteger(1));//this is control offset 1
    opArgList.insert(RWInteger(rawState));
    opArgList.insert(RWInteger(1));//this simulates a boolean to use the third integer as a control offset rather than a point id

    returnCommandMsg->setOpArgList(opArgList);

    returnCommandMsg->setMessagePriority(priority);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending base latch command, LM Group: " << getPAOName() << ", raw state: " << rawState << ", priority: " << priority << endl;
    }
    return returnCommandMsg;
}

/*-------------------------------------------------------------------------
    createTrueCycleRequestMsg

    .
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupBase::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not True Cycle a non-Expresscom Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createTrueCycleRequestMsg

    .
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupBase::createSetPointRequestMsg(RWCString settings, LONG minValue, LONG maxValue,
                                                        LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                        LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                        LONG valueTE, LONG valueTF, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not create a Set Point command to a non-Expresscom Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupBase::restoreGuts(RWvistream& istrm)
{
    RWCollectable::restoreGuts( istrm );

    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;
    RWTime tempTime4;
    
    istrm >> _paoid
          >> _paocategory
          >> _paoclass
          >> _paoname
          >> _paotype
          >> _paodescription
          >> _disableflag
          >> _grouporder
          >> _kwcapacity
          >> _childorder
          >> _alarminhibit
          >> _controlinhibit
          >> _groupcontrolstate
          >> _currenthoursdaily
          >> _currenthoursmonthly
          >> _currenthoursseasonal
          >> _currenthoursannually
          >> tempTime1
          >> tempTime2
          >> tempTime3
          >> tempTime4
          >> _internalState
	  >> _daily_ops;

    _lastcontrolsent = RWDBDateTime(tempTime1);
    _controlstarttime = RWDBDateTime(tempTime2);
    _controlcompletetime = RWDBDateTime(tempTime3);
    _next_control_time = RWDBDateTime(tempTime4);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupBase::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
          << _paocategory
          << _paoclass
          << _paoname
          << _paotype
          << _paodescription
          << _disableflag
          << _grouporder
          << _kwcapacity
          << _childorder
          << _alarminhibit
          << _controlinhibit
          << _groupcontrolstate
          << _currenthoursdaily
          << _currenthoursmonthly
          << _currenthoursseasonal
          << _currenthoursannually
          << _lastcontrolsent.rwtime()
          << _controlstarttime.rwtime()
          << _controlcompletetime.rwtime()
          << _next_control_time.rwtime()
          << _internalState
	  << _daily_ops;

    return;
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
        _paotype = right._paotype;
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
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupBase::operator==(const CtiLMGroupBase& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupBase::operator!=(const CtiLMGroupBase& right) const
{

    return !operator==(right);
}

/*----------------------------------------------------------------------------
  operator<

  This is defined to indicate this groups order inside a program
----------------------------------------------------------------------------*/  
bool CtiLMGroupBase::operator<(const CtiLMGroupBase& right) const
{
    return (getGroupOrder() < right.getGroupOrder() ||
            (getGroupOrder() == right.getGroupOrder() && getChildOrder() < right.getChildOrder()));
}

/*---------------------------------------------------------------------------
    buildShedString

    Builds a shed time string given a period of time in seconds.
---------------------------------------------------------------------------*/
RWCString CtiLMGroupBase::buildShedString(LONG shedTime) const
{
    char tempchar[64];
    RWCString retStr;

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
RWCString CtiLMGroupBase::buildPeriodString(LONG periodTime) const
{
    return CtiNumStr(periodTime/(LONG)60);
}

/*---------------------------------------------------------------------------
    convertSecondsToEvenTimeString

    doesMasterCycleNeedToBeUpdated
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime)
{
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - PAOId: " << getPAOId() << " Group Type: " << getPAOType() << " does not need to be Master Cycle refreshed." << endl;
    }*/
    return FALSE;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupBase::restore(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;
    RWCString tempTypeString;
    _insertDynamicDataFlag = FALSE;

    rdr["groupid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> tempTypeString;
    _paotype = resolvePAOType(_paocategory,tempTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["alarminhibit"] >> tempBoolString;
    tempBoolString.toLower();
    setAlarmInhibit(tempBoolString=="y"?TRUE:FALSE);
    rdr["controlinhibit"] >> tempBoolString;
    tempBoolString.toLower();
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
        setLastControlSent(gInvalidRWDBDateTime);
        setControlStartTime(gInvalidRWDBDateTime);
        setControlCompleteTime(gInvalidRWDBDateTime);
        setNextControlTime(gInvalidRWDBDateTime);
	resetDailyOps(0);
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
        RWCString tempPointType = "(none)";
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
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this group.
---------------------------------------------------------------------------*/
void CtiLMGroupBase::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    if(!isDirty())
    {
        return;
    }
    
    {
        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable dynamicLMGroupTable = db.table( "dynamiclmgroup" );
            if( !_insertDynamicDataFlag )
            {
                RWDBUpdater updater = dynamicLMGroupTable.updater();

                updater << dynamicLMGroupTable["groupcontrolstate"].assign( getGroupControlState() )
                        << dynamicLMGroupTable["currenthoursdaily"].assign( getCurrentHoursDaily() )
                        << dynamicLMGroupTable["currenthoursmonthly"].assign( getCurrentHoursMonthly() )
                        << dynamicLMGroupTable["currenthoursseasonal"].assign( getCurrentHoursSeasonal() )
                        << dynamicLMGroupTable["currenthoursannually"].assign( getCurrentHoursAnnually() )
                        << dynamicLMGroupTable["lastcontrolsent"].assign( getLastControlSent() )
                        << dynamicLMGroupTable["timestamp"].assign( currentDateTime )
                        << dynamicLMGroupTable["controlstarttime"].assign( getControlStartTime() )
                        << dynamicLMGroupTable["controlcompletetime"].assign( getControlCompleteTime() )
                        << dynamicLMGroupTable["nextcontroltime"].assign( getNextControlTime() )
                        << dynamicLMGroupTable["internalstate"].assign( _internalState )
			<< dynamicLMGroupTable["dailyops"].assign( _daily_ops );
                

                updater.where(dynamicLMGroupTable["deviceid"]==getPAOId());

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }

                updater.execute( conn );
		_dynamic_timestamp = currentDateTime;
                setDirty(false);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Inserted group into DynamicLMGroup: " << getPAOName() << endl;
                }

                RWDBInserter inserter = dynamicLMGroupTable.inserter();

                inserter << getPAOId()
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
			 << _daily_ops;;

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }

                _insertDynamicDataFlag = FALSE;

                inserter.execute( conn );
		_dynamic_timestamp = currentDateTime;
                setDirty(false);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*----------------------------------------------------------------------------
  resetInternalState
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::resetInternalState()
{
    if(_internalState != 0x00000000)
    {
        _internalState = 0x00000000;
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setIsRampingIn
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setIsRampingIn(bool in)
{
    if(getIsRampingIn() != in)
    {
        _internalState = (in ?
                          _internalState | GROUP_RAMPING_IN :
                          _internalState & ~GROUP_RAMPING_IN);
        setDirty(true);
    }
    return *this;
}

/*----------------------------------------------------------------------------
  setIsRampingOut
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setIsRampingOut(bool out)
{
    if(getIsRampingOut() != out)
    {
        _internalState = (out ?
                          _internalState | GROUP_RAMPING_OUT :
                          _internalState & ~GROUP_RAMPING_OUT);
        setDirty(true);
    }
    return *this;
}

/*-----------------------------------------------------------------------------
  updateDailyOps

  Resets the daily ops if necessary based on the dynamic timestamp
-----------------------------------------------------------------------------*/
void CtiLMGroupBase::updateDailyOps()
{
    // If we haven't written out dynamic data today
    RWDate today;
    if(today > _dynamic_timestamp.rwdate())
    {
        resetDailyOps();
    }
}

// Static Members
int CtiLMGroupBase::InactiveState = STATEZERO;
int CtiLMGroupBase::ActiveState = STATEONE;
int CtiLMGroupBase::InactivePendingState = STATETWO;
int CtiLMGroupBase::ActivePendingState = STATETHREE;

