/*---------------------------------------------------------------------------
        Filename:  lmprogram.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMProgramBase.
                        CtiLMProgramBase maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  2/12/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "device.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "mgr_holiday.h"
#include "mgr_season.h"

extern ULONG _LM_DEBUG;

LONG CtiLMProgramBase::numberOfReferences = 0;
/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramBase::CtiLMProgramBase()
{
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Default Constructor, Number of CtiLMProgramBase increased to: " << numberOfReferences << endl;
    }*/
}

CtiLMProgramBase::CtiLMProgramBase(RWDBReader& rdr)
{
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Restore Constructor, Number of CtiLMProgramBase increased to: " << numberOfReferences << endl;
    }*/
    restore(rdr);
}

CtiLMProgramBase::CtiLMProgramBase(const CtiLMProgramBase& lmprog)
{
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Copy Constructor, Number of CtiLMProgramBase increased to: " << numberOfReferences << endl;
    }*/
    operator=(lmprog);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramBase::~CtiLMProgramBase()
{

    _lmprogramcontrolwindows.clearAndDestroy();
    /*numberOfReferences--;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Destructor, Number of CtiLMProgramBase decreased to: " << numberOfReferences << endl;
    }*/
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
const RWCString& CtiLMProgramBase::getPAOCategory() const
{

    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getPAOClass() const
{

    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getPAOName() const
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
const RWCString& CtiLMProgramBase::getPAODescription() const
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
    getUserOrder

    Returns the user order of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getUserOrder() const
{

    return _userorder;
}

/*---------------------------------------------------------------------------
    getStopOrder

    Returns the stop order of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getStopOrder() const
{

    return _stoporder;
}

/*---------------------------------------------------------------------------
    getDefaultPriority

    Returns the default priority of the program
---------------------------------------------------------------------------*/
LONG CtiLMProgramBase::getDefaultPriority() const
{

    return _defaultpriority;
}

/*---------------------------------------------------------------------------
    getControlType

    Returns the control type of the program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getControlType() const
{

    return _controltype;
}

/*---------------------------------------------------------------------------
    getAvailableSeasons

    Returns the available seasons of the program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getAvailableSeasons() const
{

    return _availableseasons;
}

/*---------------------------------------------------------------------------
    getAvailableWeekDays

    Returns the available week days of the program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getAvailableWeekDays() const
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
const RWDBDateTime& CtiLMProgramBase::getStartedControlling() const
{

    return _startedcontrolling;
}

/*---------------------------------------------------------------------------
    getLastControlSent

    Returns the time of the last control sent in the program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramBase::getLastControlSent() const
{

    return _lastcontrolsent;
}

/*---------------------------------------------------------------------------
    getLMProgramControlWindows

    Returns the list of control windows for this program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramBase::getLMProgramControlWindows()
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
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOCategory(const RWCString& category)
{

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOClass(const RWCString& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOName(const RWCString& name)
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
CtiLMProgramBase& CtiLMProgramBase::setPAODescription(const RWCString& description)
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

    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setUserOrder

    Sets the user order of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setUserOrder(LONG userorder)
{

    _userorder = userorder;

    return *this;
}

/*---------------------------------------------------------------------------
    setStopOrder

    Sets the stop order of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStopOrder(LONG stoporder)
{

    _stoporder = stoporder;

    return *this;
}

/*---------------------------------------------------------------------------
    setDefaultPriority

    Sets the default priority of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setDefaultPriority(LONG defpriority)
{

    _defaultpriority = defpriority;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlType

    Sets the controltype of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setControlType(const RWCString& conttype)
{

    _controltype = conttype;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableSeasons

    Sets the available seasons of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setAvailableSeasons(const RWCString& availseasons)
{

    _availableseasons = availseasons;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableWeekDays

    Sets the available week days of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setAvailableWeekDays(const RWCString& availweekdays)
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

    _programstate = progstate;

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

    _reductiontotal = reduction;

    return *this;
}

/*---------------------------------------------------------------------------
    setStartedControlling

    Sets the time that the program started controlling
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStartedControlling(const RWDBDateTime& startcont)
{

    _startedcontrolling = startcont;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastControlSent

    Sets the time of the last control sent in the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setLastControlSent(const RWDBDateTime& lastcontrol)
{

    _lastcontrolsent = lastcontrol;

    return *this;
}

/*---------------------------------------------------------------------------
    setManualControlReceivedFlag

    Sets the manual control received flag of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setManualControlReceivedFlag(BOOL manualreceived)
{

    _manualcontrolreceivedflag = manualreceived;

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

    RWTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    long currentSeason = CtiSeasonManager::getInstance().getCurrentSeason(RWDate(), getSeasonScheduleId());
    if( _availableweekdays(start_tm.tm_wday) == 'N' ||
        ( _availableweekdays(7) == 'N' && CtiHolidayManager::getInstance().isHoliday(getHolidayScheduleId()) ) ||
        _availableseasons(currentSeason) == 'N' )
    {
        returnBool = FALSE;
    }

    return returnBool;
}

/*---------------------------------------------------------------------------
    isWithinValidControlWindow()

    Returns boolean if this program is in a valid control window.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isWithinValidControlWindow(LONG secondsFromBeginningOfDay)
{
    BOOL returnBoolean = FALSE;
    if( _lmprogramcontrolwindows.entries() > 0 )
    {
        for(LONG i=0;i<_lmprogramcontrolwindows.entries();i++)
        {
            CtiLMProgramControlWindow* currentControlWindow = (CtiLMProgramControlWindow*)_lmprogramcontrolwindows[i];
            if( currentControlWindow->getAvailableStartTime() <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= currentControlWindow->getAvailableStopTime() )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }
    else
    {
        returnBoolean = TRUE;
    }

    return returnBoolean;
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

    RWTime tempTime1;
    RWTime tempTime2;
    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _userorder
    >> _stoporder
    >> _defaultpriority
    >> _controltype
    >> _availableseasons
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

    _startedcontrolling = RWDBDateTime(tempTime1);
    _lastcontrolsent = RWDBDateTime(tempTime2);
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
    << _userorder
    << _stoporder
    << _defaultpriority
    << _controltype
    << _availableseasons
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
    << _startedcontrolling.rwtime()
    << _lastcontrolsent.rwtime()
    << _manualcontrolreceivedflag
    << _lmprogramcontrolwindows;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::operator=(const CtiLMProgramBase& right)
{


    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _userorder = right._userorder;
        _stoporder = right._stoporder;
        _defaultpriority = right._defaultpriority;
        _controltype = right._controltype;
        _availableseasons = right._availableseasons;
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

        _lmprogramcontrolwindows.clearAndDestroy();
        for(LONG i=0;i<right._lmprogramcontrolwindows.entries();i++)
        {
            _lmprogramcontrolwindows.insert(((CtiLMProgramControlWindow*)right._lmprogramcontrolwindows[i])->replicate());
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

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
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
                << dynamicLMProgramTable["startedcontrolling"].assign( getStartedControlling() )
                << dynamicLMProgramTable["lastcontrolsent"].assign( getLastControlSent() )
                << dynamicLMProgramTable["manualcontrolreceivedflag"].assign(RWCString( (getManualControlReceivedFlag() ? 'Y':'N') ))
                << dynamicLMProgramTable["timestamp"].assign((RWDBDateTime)currentDateTime);

                updater.where(dynamicLMProgramTable["deviceid"]==getPAOId());//will be paobjectid

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }
                updater.execute( conn );
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Inserted program into DynamicLMProgram: " << getPAOName() << endl;
                }

                RWDBInserter inserter = dynamicLMProgramTable.inserter();

                inserter << getPAOId()
                << getProgramState()
                << getReductionTotal()
                << getStartedControlling()
                << getLastControlSent()
                << RWCString( ( getManualControlReceivedFlag() ? 'Y': 'N' ) )
                << currentDateTime;

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );

                _insertDynamicDataFlag = FALSE;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
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
            multiDispatchMsg->insert(new CtiPointDataMsg(getProgramStatusPointId(),STATEONE,NormalQuality,StatusPointType));
        }
        else
        {//not controlling
            multiDispatchMsg->insert(new CtiPointDataMsg(getProgramStatusPointId(),STATEZERO,NormalQuality,StatusPointType));
        }
    }
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramBase::restore(RWDBReader& rdr)
{


    RWDBNullIndicator isNull;
    RWCString tempBoolString;
    RWCString tempTypeString;
    _insertDynamicDataFlag = FALSE;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> tempTypeString;
    _paotype = resolvePAOType(_paocategory,tempTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    /*rdr["userorder"] >> _userorder;
    rdr["stoporder"] >> _stoporder;
    rdr["defaultpriority"] >> _defaultpriority;*/
    _userorder = 0;
    _stoporder = 0;
    _defaultpriority = 0;

    rdr["controltype"] >> _controltype;
    rdr["availableseasons"] >> _availableseasons;
    rdr["availableweekdays"] >> _availableweekdays;
    rdr["maxhoursdaily"] >> _maxhoursdaily;
    rdr["maxhoursmonthly"] >> _maxhoursmonthly;
    rdr["maxhoursseasonal"] >> _maxhoursseasonal;
    rdr["maxhoursannually"] >> _maxhoursannually;
    rdr["minactivatetime"] >> _minactivatetime;
    rdr["minrestarttime"] >> _minrestarttime;
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
        tempBoolString.toLower();
        setManualControlReceivedFlag(tempBoolString=="y"?TRUE:FALSE);

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        setProgramState(InactiveState);
        setReductionTotal(0.0);
        setStartedControlling(RWDBDateTime(1990,1,1,0,0,0,0));
        setLastControlSent(RWDBDateTime(1990,1,1,0,0,0,0));
        setManualControlReceivedFlag(FALSE);

        _insertDynamicDataFlag = TRUE;
    }

    setProgramStatusPointId(0);
    rdr["pointid"] >> isNull;
    if( !isNull )
    {
        LONG tempPointId = 0;
        LONG tempPointOffset = 0;
        RWCString tempPointType = "(none)";
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

// Static Members
const RWCString CtiLMProgramBase::AutomaticType = "Automatic";
const RWCString CtiLMProgramBase::ManualOnlyType = "ManualOnly";

int CtiLMProgramBase::InactiveState = STATEZERO;
int CtiLMProgramBase::ActiveState = STATEONE;
int CtiLMProgramBase::ManualActiveState = STATETWO;
int CtiLMProgramBase::ScheduledState = STATETHREE;
int CtiLMProgramBase::NotifiedState = STATEFOUR;
int CtiLMProgramBase::FullyActiveState = STATEFIVE;
int CtiLMProgramBase::StoppingState = STATESIX;
int CtiLMProgramBase::AttemptingControlState = STATESEVEN;
int CtiLMProgramBase::NonControllingState = STATEEIGHT;

