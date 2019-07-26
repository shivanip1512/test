#include "precompiled.h"

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
#include "lmutility.h"
#include "database_writer.h"
#include "database_util.h"

using std::transform;
using std::string;
using std::endl;
using std::set;

extern ULONG _LM_DEBUG;
extern set<long> _CHANGED_PROGRAM_LIST;



/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramBase::CtiLMProgramBase() :
_paoid(0),
_paoType(0),
_disableflag(false),
_start_priority(0),
_stop_priority(0),
_constraintid(0),
_maxhoursdaily(0),
_maxhoursmonthly(0),
_maxhoursseasonal(0),
_maxhoursannually(0),
_minactivatetime(0),
_minrestarttime(0),
_maxdailyops(0),
_maxactivatetime(0),
_holidayscheduleid(0),
_seasonscheduleid(0),
_programstatuspointid(0),
_programstate(0),
_reductionanalogpointid(0),
_reductiontotal(0),
_manualcontrolreceivedflag(false),
_lastsentstate(-1),
_controlArea(NULL)
{
}

CtiLMProgramBase::CtiLMProgramBase(Cti::RowReader &rdr) :
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
    delete_container(_lmprogramcontrolwindows);
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
    return _paoType;
}

/*---------------------------------------------------------------------------
    getPAOTypeString

    Returns the pao type string of the substation
---------------------------------------------------------------------------*/
const string& CtiLMProgramBase::getPAOTypeString() const
{
    return _paoTypeString;
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

const std::vector<CtiLMProgramControlWindow*>& CtiLMProgramBase::getLMProgramControlWindows() const
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
    if( _programstate != progstate )
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
    if( _reductiontotal != reduction )
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
    if( _startedcontrolling != startcont )
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
    if( _lastcontrolsent != lastcontrol )
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
    if( _manualcontrolreceivedflag != manualreceived )
    {
        _manualcontrolreceivedflag = manualreceived;
        setDirty(true);
    }
    return *this;
}

CtiLMProgramBase& CtiLMProgramBase::setPaoTypeString( const std::string& typeString )
{
    _paoTypeString = typeString;
    return *this;
}

CtiLMProgramBase& CtiLMProgramBase::setLMProgramControlWindows( const std::vector<CtiLMProgramControlWindow*>& lmProgramControlWindows )
{
    delete_container(_lmprogramcontrolwindows);
    _lmprogramcontrolwindows.clear();
    for( int i=0; i<lmProgramControlWindows.size(); i++ )
    {
        _lmprogramcontrolwindows.push_back(((CtiLMProgramControlWindow*)lmProgramControlWindows[i])->replicate());
    }
    return *this;
}

CtiLMProgramBase& CtiLMProgramBase::setPaoType( const LONG paoType )
{
    _paoType = paoType;
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
        ( getSeasonScheduleId() > 0 && !CtiSeasonManager::getInstance().isInAnySeason(CtiDate(), getSeasonScheduleId())) )
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
    return(currentControlWindow != NULL);
}


/*---------------------------------------------------------------------------
    isTimedControlReady

    Returns true if this program is ready to either start or stop via
    timed control.
    It is up to subclasses to implement this.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isReadyForTimedControl(LONG secondsFromBeginningOfDay)
{
    CTILOG_WARN(dout, "Timed control is not implemented in this program type");
    return FALSE;
}

/*---------------------------------------------------------------------------
    handleTimedControl

    Performs an necessary timed control and returns TRUE if it did something.
    It is up to subclasses to implement this.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::handleTimedControl(CtiTime currentTime, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    CTILOG_WARN(dout, "Timed control is not implemented in this program type");
    return FALSE;
}

/*---------------------------------------------------------------------------
    isPastMinRestartTime

    Returns a BOOLean if the control area can be controlled more because the
    time since the last control is at least as long as the min response time.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isPastMinRestartTime(CtiTime currentTime)
{
    return TRUE;
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
        _paoType = right._paoType;
        _paoTypeString = right._paoTypeString;
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

        delete_container(_lmprogramcontrolwindows);
        _lmprogramcontrolwindows.clear();
        for( LONG i=0;i<right._lmprogramcontrolwindows.size();i++ )
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
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( !isDirty() )
    {
        return;
    }

    if( !_insertDynamicDataFlag )
    {
        static const std::string sql_update = "update dynamiclmprogram"
                                               " set "
                                                    "programstate = ?, "
                                                    "reductiontotal = ?, "
                                                    "startedcontrolling = ?, "
                                                    "lastcontrolsent = ?, "
                                                    "manualcontrolreceivedflag = ?, "
                                                    "timestamp = ?"
                                               " where "
                                                    "deviceid = ?";

        Cti::Database::DatabaseWriter   updater(conn, sql_update);

        updater
            << getProgramState()
            << getReductionTotal()
            << getStartedControlling()
            << getLastControlSent()
            << ( getManualControlReceivedFlag() ? std::string("Y") : std::string("N") )
            << currentDateTime
            << getPAOId();

        if( ! Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
        {
            return; // Error occurred!
        }
    }
    else
    {
        static const std::string sql_insert = "insert into dynamiclmprogram values (?, ?, ?, ?, ?, ?, ?)";

        CTILOG_INFO(dout, "Inserted program into DynamicLMProgram: " << getPAOName());

        Cti::Database::DatabaseWriter   inserter(conn, sql_insert);

        inserter
            << getPAOId()
            << getProgramState()
            << getReductionTotal()
            << getStartedControlling()
            << getLastControlSent()
            << ( getManualControlReceivedFlag() ? std::string("Y") : std::string("N") )
            << currentDateTime;

        if( ! Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
        {
            return; // Error occurred!
        }

        _insertDynamicDataFlag = false;
    }

    resetDirty(); // setDirty inserts into the changed group list and we do not want to do that here.
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
            getProgramState() == CtiLMProgramBase::ManualActiveState ||
            getProgramState() == CtiLMProgramBase::TimedActiveState )//controlling
        {
            if( _lastsentstate != STATEONE )
            {
                multiDispatchMsg->insert(CTIDBG_new CtiPointDataMsg(getProgramStatusPointId(),STATEONE,NormalQuality,StatusPointType));
                _lastsentstate = STATEONE;
            }
        }
        else if( _lastsentstate != STATEZERO )//not controlling
        {
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

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramBase::restore(Cti::RowReader &rdr)
{
    string tempBoolString;
    _insertDynamicDataFlag = FALSE;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paoTypeString;
    _paoType = resolvePAOType(_paocategory,_paoTypeString);
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

    if( !rdr["programstate"].isNull() )
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
        // lmprogramdirect->setProgramState cannot be called here as it has side effects
        // It cannot be called until the program is completely loaded.
        CtiLMProgramBase::setProgramState(InactiveState);
        setReductionTotal(0.0);
        setStartedControlling(gInvalidCtiTime);
        setLastControlSent(gInvalidCtiTime);
        setManualControlReceivedFlag(FALSE);

        _insertDynamicDataFlag = TRUE;
        setDirty(true);
    }

    setProgramStatusPointId(0);
    if( !rdr["pointid"].isNull() )
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

CtiLMProgramControlWindow* CtiLMProgramBase::getControlWindow(LONG secondsFromBeginningOfDay, CtiDate &defaultDate)
{
    //Control Windows can span midnight, in which case getAvailableStopTime will represent more than 24 hours worth of seconds

    // Mod secondsFromBeginningOfDay by 86400 because the control windows are cyclical.
    CtiTime currentTime = GetTimeFromOffsetAndDate(secondsFromBeginningOfDay % 86400, defaultDate);

    for( LONG i=0;i<_lmprogramcontrolwindows.size();i++ )
    {
        CtiLMProgramControlWindow* currentControlWindow = (CtiLMProgramControlWindow*)_lmprogramcontrolwindows[i];

        CtiTime controlWindowStartTime = currentControlWindow->getAvailableStartTime(defaultDate);
        CtiTime controlWindowStopTime = currentControlWindow->getAvailableStopTime(defaultDate);

        if(controlWindowStartTime <= controlWindowStopTime)     // sanity check... should always be true
        {
            if(controlWindowStartTime.date() == controlWindowStopTime.date())   // window entirely on same date
            {

// Times:                        controlWindowStartTime                      controlWindowStopTime
//          |--------------------|-------------------------------------------|-------------------------------|
// Window:                       XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

                if(controlWindowStartTime <= currentTime && currentTime <= controlWindowStopTime)
                {
                    return currentControlWindow;
                }
            }
            else    // window spans the midnight boundary between two days
            {
                CtiTime midnightToday = GetTimeFromOffsetAndDate(0, defaultDate);
                CtiTime midnightTomorrow = GetTimeFromOffsetAndDate(86400, defaultDate);
                CtiTime controlWindowStopTimeToday = controlWindowStopTime;
                controlWindowStopTimeToday.addDays(-1);

// Times:   midnightToday        controlWindowStopTimeToday                  controlWindowStartTime          midnightTomorrow
//          |--------------------|-------------------------------------------|-------------------------------|
// Window:  XXXXXXXXXXXXXXXXXXXXXX                                           XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

                if((midnightToday <= currentTime && currentTime <= controlWindowStopTimeToday) ||
                   (controlWindowStartTime <= currentTime && currentTime <= midnightTomorrow))
                {
                    return currentControlWindow;
                }
            }
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
    CtiTime currentTime(0,0,0);
    currentTime.addSeconds(secondsFromBeginningOfDay);

    if( _lmprogramcontrolwindows.size() == 0 )
    {
        return 0;
    }
    if( _lmprogramcontrolwindows.size() == 1 )  //only 1 control window, it must always be the next one
    {
        return(CtiLMProgramControlWindow*)  _lmprogramcontrolwindows[0];
    }
    CtiLMProgramControlWindow* cw = getControlWindow(secondsFromBeginningOfDay);
    if( cw != 0 )  // we are in a control window now, return the next one
    {
        return(CtiLMProgramControlWindow*)  _lmprogramcontrolwindows[cw->getWindowNumber()+1 % _lmprogramcontrolwindows.size()];
    }
    else
    {
        //not in a control window now, figure which is the next one
        for( LONG i=0;i<_lmprogramcontrolwindows.size();i++ )
        {
            cw = (CtiLMProgramControlWindow*)_lmprogramcontrolwindows[i];
            if( cw->getAvailableStartTime() > currentTime )
            {
                return cw;
            }
        }
        if( cw == 0 ) //all the control windows were earlier today, return the first one for tomorrow
        {
            return(CtiLMProgramControlWindow*) _lmprogramcontrolwindows[0];
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

// Sets the reason for the recent change, only used by LMProgramDirect
void CtiLMProgramBase::setChangeReason(const string& reason)
{
}

// Sets the last user, only used by LMProgramDirect
void CtiLMProgramBase::setLastUser(const string& user)
{
}

// Sets the origin of the change, only used by LMProgramDirect
void CtiLMProgramBase::setOrigin(const std::string& origin)
{
}

// Static Members
const string CtiLMProgramBase::AutomaticType = "Automatic";
const string CtiLMProgramBase::ManualOnlyType = "ManualOnly";
const string CtiLMProgramBase::TimedType = "Timed";

const int CtiLMProgramBase::InactiveState = STATEZERO;
const int CtiLMProgramBase::ActiveState = STATEONE;
const int CtiLMProgramBase::ManualActiveState = STATETWO;
const int CtiLMProgramBase::ScheduledState = STATETHREE;
const int CtiLMProgramBase::NotifiedState = STATEFOUR;
const int CtiLMProgramBase::FullyActiveState = STATEFIVE;
const int CtiLMProgramBase::StoppingState = STATESIX;
const int CtiLMProgramBase::GearChangeState = STATESEVEN;
const int CtiLMProgramBase::NonControllingState = STATEEIGHT;
const int CtiLMProgramBase::TimedActiveState = STATENINE;
