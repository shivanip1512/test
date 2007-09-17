/*---------------------------------------------------------------------------
        Filename:  lmprogram.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMProgramBase.
                        CtiLMProgramBase maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  2/12/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "mgr_holiday.h"
#include "mgr_season.h"
#include "utility.h"

extern ULONG _LM_DEBUG;
extern set<long> _CHANGED_PROGRAM_LIST;

using std::transform;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramBase::CtiLMProgramBase() :
_lastsentstate(-1), _controlArea(NULL)
{
}

CtiLMProgramBase::CtiLMProgramBase(RWDBReader& rdr) :
_lastsentstate(-1), _controlArea(NULL)
{
    restore(rdr);
}

CtiLMProgramBase::CtiLMProgramBase(const CtiLMProgramBase& lmprog) :
_lastsentstate(-1), _controlArea(NULL)
{
    operator=(lmprog);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramBase::~CtiLMProgramBase()
{
    delete_vector(_lmprogramcontrolwindows);
    _lmprogramcontrolwindows.clear();
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the program
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::getDisableFlag() const
{
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getStartPriority

    Returns the start priority of the program
---------------------------------------------------------------------------*/
int CtiLMProgramBase::getStartPriority() const
{
    return _start_priority;
}

/*---------------------------------------------------------------------------
    getStopPriority

    Returns the start priority of the program
---------------------------------------------------------------------------*/
int CtiLMProgramBase::getStopPriority() const
{
    return _stop_priority;
}

/*---------------------------------------------------------------------------
    getControlType

    Returns the control type of the program
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getControlType() const
{
    return _controltype;
}

/*---------------------------------------------------------------------------
    getConstraintID

    Returns the constraint id of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getConstraintID() const
{
    return _constraintid;
}

/*---------------------------------------------------------------------------
    getConstraintName

    Returns the constraint name of the program
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getConstraintName() const
{
    return _constraintname;
}

/*---------------------------------------------------------------------------
    getAvailableWeekDays

    Returns the available week days of the program
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getAvailableWeekDays() const
{
    return _availableweekdays;
}

/*---------------------------------------------------------------------------
    getMaxHoursDaily

    Returns the max hours daily of the program in seconds
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMaxHoursDaily() const
{
    return _maxhoursdaily;
}

/*---------------------------------------------------------------------------
    getMaxHoursMonthly

    Returns the max hours monthly of the program in seconds
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMaxHoursMonthly() const
{
    return _maxhoursmonthly;
}

/*---------------------------------------------------------------------------
    getMaxHoursSeasonal

    Returns the max hours seasonal of the program in seconds
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMaxHoursSeasonal() const
{
    return _maxhoursseasonal;
}

/*---------------------------------------------------------------------------
    getMaxHoursAnnually

    Returns the max hours annually of the program in seconds
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMaxHoursAnnually() const
{
    return _maxhoursannually;
}

/*---------------------------------------------------------------------------
    getMinActivateTime

    Returns the minimum activate time of the program in minutes
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMinActivateTime() const
{

    return _minactivatetime;
}

/*---------------------------------------------------------------------------
    getMinRestartTime

    Returns the minimum restart time of the program in minutes
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMinRestartTime() const
{
    return _minrestarttime;
}

/*---------------------------------------------------------------------------
  getMaxDailyOps

  Returns the maximum daily operations of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMaxDailyOps() const
{
    return _maxdailyops;
}

/*---------------------------------------------------------------------------
  getMaxActivateTime

  Returns the maximum activate time of the program in minutes
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getMaxActivateTime() const
{
    return _maxactivatetime;
}

/*---------------------------------------------------------------------------
    getHolidayScheduleId

    Returns the id of the holiday schedule used by the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getHolidayScheduleId() const
{
    return _holidayscheduleid;
}

/*---------------------------------------------------------------------------
    getSeasonScheduleId

    Returns the id of the season schedule used by the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getSeasonScheduleId() const
{
    return _seasonscheduleid;
}

/*---------------------------------------------------------------------------
    getProgramStatusPointId

    Returns the status point id for the program of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getProgramStatusPointId() const
{
    return _programstatuspointid;
}

/*---------------------------------------------------------------------------
    getProgramState

    Returns the state of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getProgramState() const
{
    return _programstate;
}

/*---------------------------------------------------------------------------
    getReductionAnalogPointId

    Returns the pointid of the analog that holds the current total reduction
    of load in the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getReductionAnalogPointId() const
{
    return _reductionanalogpointid;
}

/*---------------------------------------------------------------------------
    getReductionTotal

    Returns the current load reduced in KW of the program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramBase::getReductionTotal() const
{
    return _reductiontotal;
}

/*---------------------------------------------------------------------------
    getStartedControlling

    Returns the time that the program started controlling
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramBase::getStartedControlling() const
{
    return _startedcontrolling;
}

/*---------------------------------------------------------------------------
    getLastControlSent

    Returns the time of the last control sent in the program
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramBase::getLastControlSent() const
{
    return _lastcontrolsent;
}

/*---------------------------------------------------------------------------
    getLMProgramControlWindows

    Returns the list of control windows for this program
---------------------------------------------------------------------------*/
std::vector<CtiLMProgramControlWindow*>& CtiLMProgramBase::getLMProgramControlWindows()
{
    return _lmprogramcontrolwindows;
}

/*---------------------------------------------------------------------------
    getManualControlReceivedFlag

    Returns the manual control received flag of the program
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::getManualControlReceivedFlag() const
{
    return _manualcontrolreceivedflag;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOId(LONG id)
{
    _paoid = id;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOCategory(const string& category)
{
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOClass(const string& pclass)
{
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOName(const string& name)
{
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOType(LONG type)
{
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAODescription(const string& description)
{
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flaf of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setDisableFlag(BOOL disable)
{
    if( disable != _disableflag )
    {
        _disableflag = disable;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setStartPriority

    Sets the start priority of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStartPriority(int start_priority)
{
    _start_priority = start_priority;
    return *this;
}

/*---------------------------------------------------------------------------
    setStopPriority

    Sets the stop priority of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStopPriority(int stop_priority)
{
    _stop_priority = stop_priority;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlType

    Sets the controltype of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setControlType(const string& conttype)
{
    _controltype = conttype;
    return *this;
}

/*---------------------------------------------------------------------------
    setConstraintID

    Sets the constraint id of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setConstraintID(LONG constraint_id)
{
    _constraintid = constraint_id;
    return *this;
}

/*---------------------------------------------------------------------------
    setConstraintName

    Sets the constraint name of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setConstraintName(const string& constraint_name)
{
    _constraintname = constraint_name;
    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableWeekDays

    Sets the available week days of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setAvailableWeekDays(const string& availweekdays)
{
    _availableweekdays = availweekdays;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursDaily

    Sets the max hours daily of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursDaily(LONG daily)
{
    _maxhoursdaily = daily;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursMonthly

    Sets the max hours monthly of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursMonthly(LONG monthly)
{
    _maxhoursmonthly = monthly;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursSeasonal

    Sets the max hours seasonal of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursSeasonal(LONG seasonal)
{
    _maxhoursseasonal = seasonal;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursAnnually

    Sets the max hours annually of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursAnnually(LONG annually)
{
    _maxhoursannually = annually;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinActivateTime

    Sets the minimum activate time of the program in minutes
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMinActivateTime(LONG activate)
{
    _minactivatetime = activate;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinRestartTime

    Sets the minimum restart time of the program in minutes
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMinRestartTime(LONG restart)
{
    _minrestarttime = restart;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOps

    Sets the maximum daily ops for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxDailyOps(LONG max_daily_ops)
{
    _maxdailyops = max_daily_ops;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxActivateTime

    Set the maximum activate time of the program in minutes
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxActivateTime(LONG max_activate_time)
{
    _maxactivatetime = max_activate_time;
    return *this;
}

/*---------------------------------------------------------------------------
    setHolidayScheduleId

    Sets the id of the holiday schedule used by the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setHolidayScheduleId(LONG schdid)
{
    _holidayscheduleid = schdid;
    return *this;
}

/*---------------------------------------------------------------------------
    setSeasonScheduleId

    Sets the id of the season schedule used by the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setSeasonScheduleId(LONG schdid)
{
    _seasonscheduleid = schdid;
    return *this;
}

/*---------------------------------------------------------------------------
    setProgramStatusPointId

    Sets the status point id for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setProgramStatusPointId(LONG statuspointid)
{
    _programstatuspointid = statuspointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setProgramState

    Sets the current state of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setProgramState(LONG progstate)
{
    if(_programstate != progstate)
    {
        _programstate = progstate;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setReductionAnalogPointId

    Sets the pointid of the analog point that holds the current reduction
    total for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setReductionAnalogPointId(LONG reductionpointid)
{
    _reductionanalogpointid = reductionpointid;
    return *this;
}

/*---------------------------------------------------------------------------
    setReductionTotal

    Sets the current reduction total for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setReductionTotal(DOUBLE reduction)
{
    if(_reductiontotal != reduction)
    {
        _reductiontotal = reduction;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setStartedControlling

    Sets the time that the program started controlling
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStartedControlling(const CtiTime& startcont)
{
    if(_startedcontrolling != startcont)
    {
        _startedcontrolling = startcont;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setLastControlSent

    Sets the time of the last control sent in the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setLastControlSent(const CtiTime& lastcontrol)
{
    if(_lastcontrolsent != lastcontrol)
    {
        _lastcontrolsent = lastcontrol;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setManualControlReceivedFlag

    Sets the manual control received flag of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setManualControlReceivedFlag(BOOL manualreceived)
{
    if(_manualcontrolreceivedflag != manualreceived)
    {
        _manualcontrolreceivedflag = manualreceived;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    isAvailableToday

    Returns boolean if this program is available for control today based on
    available seasons and weekdays.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isAvailableToday()
{
    BOOL returnBool = TRUE;

   CtiTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    bool is_holiday = CtiHolidayManager::getInstance().isHoliday(getHolidayScheduleId());

    if( (is_holiday &&_availableweekdays[7] == 'E') || //exclude
        (_availableweekdays[start_tm.tm_wday] == 'N' && !(is_holiday && _availableweekdays[7] == 'F')) ||
        ( getSeasonScheduleId() > 0 && !CtiSeasonManager::getInstance().isInSeason(CtiDate(), getSeasonScheduleId())) )
    {
        returnBool = FALSE;
    }

    return returnBool;
}

/*---------------------------------------------------------------------------
    isWithinValidControlWindow()

    Returns TRUE if this program is currenly inside a control window or if
    there are no control windows.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isWithinValidControlWindow(LONG secondsFromBeginningOfDay)
{
    if( _lmprogramcontrolwindows.size() == 0 )
        return TRUE; // No control windows defined, so anytime is inside our control window

    // Try to find the control window we are in
    CtiLMProgramControlWindow* currentControlWindow = getControlWindow(secondsFromBeginningOfDay);
    return (currentControlWindow != NULL);
}


/*---------------------------------------------------------------------------
    isTimedControlReady

    Returns true if this program is ready to either start or stop via
    timed control.
    It is up to subclasses to implement this.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isReadyForTimedControl(LONG secondsFromBeginningOfDay)
{
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " << "Timed control is not implemented in this program type" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return FALSE;
}

/*---------------------------------------------------------------------------
    handleTimedControl

    Performs an necessary timed control and returns TRUE if it did something.
    It is up to subclasses to implement this.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::handleTimedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " << "Timed control is not implemented in this program type" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return FALSE;
}

/*---------------------------------------------------------------------------
    isPastMinRestartTime

    Returns a BOOLean if the control area can be controlled more because the
    time since the last control is at least as long as the min response time.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isPastMinRestartTime(ULONG secondsFrom1901)
{
    return TRUE;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramBase::restoreGuts(RWvistream& istrm)
{
    RWCollectable::restoreGuts( istrm );

    CtiTime tempTime1;
    CtiTime tempTime2;
    istrm >> _paoid
          >> _paocategory
          >> _paoclass
          >> _paoname
          >> _paotype
          >> _paodescription
          >> _disableflag
          >> _start_priority
          >> _stop_priority
          >> _controltype
          >> _availableweekdays
          >> _maxhoursdaily
          >> _maxhoursmonthly
          >> _maxhoursseasonal
          >> _maxhoursannually
          >> _minactivatetime
          >> _minrestarttime/*
                              >> _holidayscheduleid
                              >> _seasonscheduleid*/
          >> _programstatuspointid
          >> _programstate
          >> _reductionanalogpointid
          >> _reductiontotal
          >> tempTime1
          >> tempTime2
          >> _manualcontrolreceivedflag
          >> _lmprogramcontrolwindows;

    _startedcontrolling = CtiTime(tempTime1);
    _lastcontrolsent = CtiTime(tempTime2);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramBase::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
          << _paocategory
          << _paoclass
          << _paoname
          << _paotype
          << _paodescription
          << _disableflag
          << _start_priority
          << _stop_priority
          << _controltype
          << _availableweekdays
          << _maxhoursdaily
          << _maxhoursmonthly
          << _maxhoursseasonal
          << _maxhoursannually
          << _minactivatetime
          << _minrestarttime/*
                              << _holidayscheduleid
                              << _seasonscheduleid*/
          << _programstatuspointid
          << _programstate
          << _reductionanalogpointid
          << _reductiontotal
          << _startedcontrolling
          << _lastcontrolsent
          << _manualcontrolreceivedflag
          << _lmprogramcontrolwindows;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::operator=(const CtiLMProgramBase& right)
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
        _start_priority = right._start_priority;
        _stop_priority = right._stop_priority;
        _controltype = right._controltype;
        _availableweekdays = right._availableweekdays;
        _maxhoursdaily = right._maxhoursdaily;
        _maxhoursmonthly = right._maxhoursmonthly;
        _maxhoursseasonal = right._maxhoursseasonal;
        _maxhoursannually = right._maxhoursannually;
        _minactivatetime = right._minactivatetime;
        _minrestarttime = right._minrestarttime;
        _holidayscheduleid = right._holidayscheduleid;
        _seasonscheduleid = right._seasonscheduleid;
        _programstatuspointid = right._programstatuspointid;
        _programstate = right._programstate;
        _reductionanalogpointid = right._reductionanalogpointid;
        _reductiontotal = right._reductiontotal;
        _startedcontrolling = right._startedcontrolling;
        _lastcontrolsent = right._lastcontrolsent;
        _manualcontrolreceivedflag = right._manualcontrolreceivedflag;

        delete_vector(_lmprogramcontrolwindows);
        _lmprogramcontrolwindows.clear();
        for(LONG i=0;i<right._lmprogramcontrolwindows.size();i++)
        {
            _lmprogramcontrolwindows.push_back(((CtiLMProgramControlWindow*)right._lmprogramcontrolwindows[i])->replicate());
        }
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramBase::operator==(const CtiLMProgramBase& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramBase::operator!=(const CtiLMProgramBase& right) const
{
    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    if(!isDirty())
    {
        return;
    }
    {
        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable dynamicLMProgramTable = db.table( "dynamiclmprogram" );
            if( !_insertDynamicDataFlag )
            {
                RWDBUpdater updater = dynamicLMProgramTable.updater();

                updater << dynamicLMProgramTable["programstate"].assign( getProgramState() )
                << dynamicLMProgramTable["reductiontotal"].assign( getReductionTotal() )
                << dynamicLMProgramTable["startedcontrolling"].assign( toRWDBDT(getStartedControlling()) )
                << dynamicLMProgramTable["lastcontrolsent"].assign( toRWDBDT(getLastControlSent()) )
                << dynamicLMProgramTable["manualcontrolreceivedflag"].assign(( (getManualControlReceivedFlag() ? "Y":"N") ))
                << dynamicLMProgramTable["timestamp"].assign(toRWDBDT(currentDateTime));

                updater.where(dynamicLMProgramTable["deviceid"]==getPAOId());//will be paobjectid

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << updater.asString().data() << endl;
                }
                updater.execute( conn );
                setDirty(false);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Inserted program into DynamicLMProgram: " << getPAOName() << endl;
                }

                RWDBInserter inserter = dynamicLMProgramTable.inserter();

                inserter << getPAOId()
                << getProgramState()
                << getReductionTotal()
                << getStartedControlling()
                << getLastControlSent()
                << ( ( getManualControlReceivedFlag() ? "Y": "N" ) )
                 << currentDateTime;


                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );

                _insertDynamicDataFlag = FALSE;
                setDirty(false);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    createControlStatusPointUpdates

    Create new point data messages that track the state (controlling or not)
    of the control area.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::createControlStatusPointUpdates(CtiMultiMsg* multiDispatchMsg)
{
    if( getProgramStatusPointId() > 0 )
    {
        if( getProgramState() == CtiLMProgramBase::ActiveState ||
            getProgramState() == CtiLMProgramBase::FullyActiveState ||
            getProgramState() == CtiLMProgramBase::ManualActiveState )
        {//controlling
            if( _lastsentstate != STATEONE )
            {
                multiDispatchMsg->insert(CTIDBG_new CtiPointDataMsg(getProgramStatusPointId(),STATEONE,NormalQuality,StatusPointType));
                _lastsentstate = STATEONE;
            }
        }
        else if( _lastsentstate != STATEZERO )
        {//not controlling
            multiDispatchMsg->insert(CTIDBG_new CtiPointDataMsg(getProgramStatusPointId(),STATEZERO,NormalQuality,StatusPointType));
            _lastsentstate = STATEZERO;
        }
    }
}

/*---------------------------------------------------------------------------
    setControlArea

    Stores a pointer to a control area, no null checking.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::setControlArea(CtiLMControlArea *controlArea)
{
    _controlArea = controlArea;
}

/*---------------------------------------------------------------------------
    getControlArea

    Meant to be private! returns the stored bare control area pointer.
---------------------------------------------------------------------------*/
CtiLMControlArea* CtiLMProgramBase::getControlArea()
{
    return _controlArea;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramBase::restore(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    string tempBoolString;
    string tempTypeString;
    _insertDynamicDataFlag = FALSE;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> tempTypeString;
    _paotype = resolvePAOType(_paocategory,tempTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);

    rdr["controltype"] >> _controltype;
    rdr["constraintid"] >> _constraintid;
    rdr["constraintname"] >> _constraintname;
    rdr["availableweekdays"] >> _availableweekdays;
    rdr["maxhoursdaily"] >> _maxhoursdaily;
    rdr["maxhoursmonthly"] >> _maxhoursmonthly;
    rdr["maxhoursseasonal"] >> _maxhoursseasonal;
    rdr["maxhoursannually"] >> _maxhoursannually;
    rdr["minactivatetime"] >> _minactivatetime;
    rdr["minrestarttime"] >> _minrestarttime;
    rdr["maxdailyops"] >> _maxdailyops;
    rdr["maxactivatetime"] >> _maxactivatetime;
    rdr["holidayscheduleid"] >> _holidayscheduleid;
    rdr["seasonscheduleid"] >> _seasonscheduleid;

    rdr["programstate"] >> isNull;
    if( !isNull )
    {
        rdr["programstate"] >> _programstate;
        rdr["reductiontotal"] >> _reductiontotal;
        rdr["startedcontrolling"] >> _startedcontrolling;
        rdr["lastcontrolsent"] >> _lastcontrolsent;
        rdr["manualcontrolreceivedflag"] >> tempBoolString;
        transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        setManualControlReceivedFlag(tempBoolString=="y"?TRUE:FALSE);

        _insertDynamicDataFlag = FALSE;
        setDirty(false);
    }
    else
    {
        setProgramState(InactiveState);
        setReductionTotal(0.0);
        setStartedControlling(gInvalidCtiTime);
        setLastControlSent(gInvalidCtiTime);
        setManualControlReceivedFlag(FALSE);

        _insertDynamicDataFlag = TRUE;
        setDirty(true);
    }

    setProgramStatusPointId(0);
    rdr["pointid"] >> isNull;
    if( !isNull )
    {
        LONG tempPointId = 0;
        LONG tempPointOffset = 0;
        string tempPointType = "(none)";
        rdr["pointid"] >> tempPointId;
        rdr["pointoffset"] >> tempPointOffset;
        rdr["pointtype"] >> tempPointType;
        if( resolvePointType(tempPointType) == StatusPointType &&
            tempPointOffset == 1 )
        {
            setProgramStatusPointId(tempPointId);
        }
    }
}

CtiLMProgramControlWindow* CtiLMProgramBase::getControlWindow(LONG secondsFromBeginningOfDay)
{
    //Control Windows can span midnight, in which case getAvailableStopTime will represent more than 24 hours worth of seconds
    //So add 24 hours worth of seconds and do an additional test
    LONG secondsFromBeginningOfYesterday = secondsFromBeginningOfDay + 24 * 60 * 60;

    for(LONG i=0;i<_lmprogramcontrolwindows.size();i++)
    {
        CtiLMProgramControlWindow* currentControlWindow = (CtiLMProgramControlWindow*)_lmprogramcontrolwindows[i];

        if( currentControlWindow->getAvailableStartTime() <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= currentControlWindow->getAvailableStopTime() ||
            currentControlWindow->getAvailableStartTime() <= secondsFromBeginningOfYesterday && secondsFromBeginningOfYesterday <= currentControlWindow->getAvailableStopTime() )
        {
            return currentControlWindow;
        }
    }
    return 0;
}

/*----------------------------------------------------------------------------
  getNextControlWindow

  Returns the next control window, or 0 if there isn't one.
  Note that if there is only 1 control window, that window is always returned
----------------------------------------------------------------------------*/
CtiLMProgramControlWindow* CtiLMProgramBase::getNextControlWindow(LONG secondsFromBeginningOfDay)
{
    if(_lmprogramcontrolwindows.size() == 0)
    {
        return 0;
    }
    if(_lmprogramcontrolwindows.size() == 1)
    {  //only 1 control window, it must always be the next one
        return (CtiLMProgramControlWindow*)  _lmprogramcontrolwindows[0];
    }
    CtiLMProgramControlWindow* cw = getControlWindow(secondsFromBeginningOfDay);
    if(cw != 0)
    {  // we are in a control window now, return the next one
        return (CtiLMProgramControlWindow*)  _lmprogramcontrolwindows[cw->getWindowNumber()+1 % _lmprogramcontrolwindows.size()];
    }
    else
    {
        //not in a control window now, figure which is the next one
        for(LONG i=0;i<_lmprogramcontrolwindows.size();i++)
        {
            cw = (CtiLMProgramControlWindow*)_lmprogramcontrolwindows[i];
            if(cw->getAvailableStartTime() > secondsFromBeginningOfDay)
            {
                return cw;
            }
        }
        if(cw == 0)
        { //all the control windows were earlier today, return the first one for tomorrow
            return (CtiLMProgramControlWindow*) _lmprogramcontrolwindows[0];
        }
    }
    return 0;
}

/*-----------------------------------------------------------------------------
  setDirty

  Sets the dirty flag and notifies LM that this program should
  be sent to clients
-----------------------------------------------------------------------------*/
void CtiLMProgramBase::setDirty(BOOL b)
{
    _CHANGED_PROGRAM_LIST.insert(getPAOId());
    CtiMemDBObject::setDirty(b);
}


// Static Members
const string CtiLMProgramBase::AutomaticType = "Automatic";
const string CtiLMProgramBase::ManualOnlyType = "ManualOnly";
const string CtiLMProgramBase::TimedType = "Timed";

int CtiLMProgramBase::InactiveState = STATEZERO;
int CtiLMProgramBase::ActiveState = STATEONE;
int CtiLMProgramBase::ManualActiveState = STATETWO;
int CtiLMProgramBase::ScheduledState = STATETHREE;
int CtiLMProgramBase::NotifiedState = STATEFOUR;
int CtiLMProgramBase::FullyActiveState = STATEFIVE;
int CtiLMProgramBase::StoppingState = STATESIX;
int CtiLMProgramBase::AttemptingControlState = STATESEVEN;
int CtiLMProgramBase::NonControllingState = STATEEIGHT;
int CtiLMProgramBase::TimedActiveState = STATENINE;
