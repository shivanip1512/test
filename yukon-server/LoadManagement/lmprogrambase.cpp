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

extern BOOL _LM_DEBUG;

ULONG CtiLMProgramBase::numberOfReferences = 0;
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
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
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
ULONG CtiLMProgramBase::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the program
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getUserOrder

    Returns the user order of the program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getUserOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _userorder;
}

/*---------------------------------------------------------------------------
    getStopOrder

    Returns the stop order of the program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getStopOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _stoporder;
}

/*---------------------------------------------------------------------------
    getDefaultPriority

    Returns the default priority of the program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getDefaultPriority() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _defaultpriority;
}

/*---------------------------------------------------------------------------
    getControlType

    Returns the control type of the program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getControlType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controltype;
}

/*---------------------------------------------------------------------------
    getAvailableSeasons

    Returns the available seasons of the program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getAvailableSeasons() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _availableseasons;
}

/*---------------------------------------------------------------------------
    getAvailableWeekDays

    Returns the available week days of the program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramBase::getAvailableWeekDays() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _availableweekdays;
}

/*---------------------------------------------------------------------------
    getMaxHoursDaily

    Returns the max hours daily of the program in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getMaxHoursDaily() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maxhoursdaily;
}

/*---------------------------------------------------------------------------
    getMaxHoursMonthly

    Returns the max hours monthly of the program in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getMaxHoursMonthly() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maxhoursmonthly;
}

/*---------------------------------------------------------------------------
    getMaxHoursSeasonal

    Returns the max hours seasonal of the program in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getMaxHoursSeasonal() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maxhoursseasonal;
}

/*---------------------------------------------------------------------------
    getMaxHoursAnnually

    Returns the max hours annually of the program in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getMaxHoursAnnually() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maxhoursannually;
}

/*---------------------------------------------------------------------------
    getMinActivateTime

    Returns the minimum activate time of the program in minutes
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getMinActivateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _minactivatetime;
}

/*---------------------------------------------------------------------------
    getMinRestartTime

    Returns the minimum restart time of the program in minutes
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getMinRestartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _minrestarttime;
}

/*---------------------------------------------------------------------------
    getProgramStatusPointId

    Returns the status point id for the program of the program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getProgramStatusPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _programstatuspointid;
}

/*---------------------------------------------------------------------------
    getProgramState

    Returns the state of the program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getProgramState() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _programstate;
}

/*---------------------------------------------------------------------------
    getReductionAnalogPointId

    Returns the pointid of the analog that holds the current total reduction
    of load in the program
---------------------------------------------------------------------------*/
ULONG CtiLMProgramBase::getReductionAnalogPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _reductionanalogpointid;
}

/*---------------------------------------------------------------------------
    getReductionTotal

    Returns the current load reduced in KW of the program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramBase::getReductionTotal() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _reductiontotal;
}

/*---------------------------------------------------------------------------
    getStartedControlling

    Returns the time that the program started controlling
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramBase::getStartedControlling() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _startedcontrolling;
}

/*---------------------------------------------------------------------------
    getLastControlSent

    Returns the time of the last control sent in the program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramBase::getLastControlSent() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastcontrolsent;
}

/*---------------------------------------------------------------------------
    getLMProgramControlWindows

    Returns the list of control windows for this program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramBase::getLMProgramControlWindows()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lmprogramcontrolwindows;
}

/*---------------------------------------------------------------------------
    getManualControlReceivedFlag

    Returns the manual control received flag of the program
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::getManualControlReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _manualcontrolreceivedflag;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAOType(ULONG type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flaf of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setUserOrder

    Sets the user order of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setUserOrder(ULONG userorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _userorder = userorder;

    return *this;
}

/*---------------------------------------------------------------------------
    setStopOrder

    Sets the stop order of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStopOrder(ULONG stoporder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _stoporder = stoporder;

    return *this;
}

/*---------------------------------------------------------------------------
    setDefaultPriority

    Sets the default priority of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setDefaultPriority(ULONG defpriority)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _defaultpriority = defpriority;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlType

    Sets the controltype of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setControlType(const RWCString& conttype)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controltype = conttype;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableSeasons

    Sets the available seasons of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setAvailableSeasons(const RWCString& availseasons)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _availableseasons = availseasons;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableWeekDays

    Sets the available week days of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setAvailableWeekDays(const RWCString& availweekdays)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _availableweekdays = availweekdays;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursDaily

    Sets the max hours daily of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursDaily(ULONG daily)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maxhoursdaily = daily;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursMonthly

    Sets the max hours monthly of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursMonthly(ULONG monthly)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maxhoursmonthly = monthly;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursSeasonal

    Sets the max hours seasonal of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursSeasonal(ULONG seasonal)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maxhoursseasonal = seasonal;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxHoursAnnually

    Sets the max hours annually of the program in seconds
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMaxHoursAnnually(ULONG annually)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maxhoursannually = annually;

    return *this;
}

/*---------------------------------------------------------------------------
    setMinActivateTime

    Sets the minimum activate time of the program in minutes
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMinActivateTime(ULONG activate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _minactivatetime = activate;

    return *this;
}

/*---------------------------------------------------------------------------
    setMinRestartTime

    Sets the minimum restart time of the program in minutes
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setMinRestartTime(ULONG restart)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _minrestarttime = restart;

    return *this;
}

/*---------------------------------------------------------------------------
    setProgramStatusPointId

    Sets the status point id for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setProgramStatusPointId(ULONG statuspointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _programstatuspointid = statuspointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setProgramState

    Sets the current state of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setProgramState(ULONG progstate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _programstate = progstate;

    return *this;
}

/*---------------------------------------------------------------------------
    setReductionAnalogPointId

    Sets the pointid of the analog point that holds the current reduction
    total for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setReductionAnalogPointId(ULONG reductionpointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _reductionanalogpointid = reductionpointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setReductionTotal

    Sets the current reduction total for the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setReductionTotal(DOUBLE reduction)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _reductiontotal = reduction;

    return *this;
}

/*---------------------------------------------------------------------------
    setStartedControlling

    Sets the time that the program started controlling
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setStartedControlling(const RWDBDateTime& startcont)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _startedcontrolling = startcont;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastControlSent

    Sets the time of the last control sent in the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setLastControlSent(const RWDBDateTime& lastcontrol)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastcontrolsent = lastcontrol;

    return *this;
}

/*---------------------------------------------------------------------------
    setManualControlReceivedFlag

    Sets the manual control received flag of the program
---------------------------------------------------------------------------*/
CtiLMProgramBase& CtiLMProgramBase::setManualControlReceivedFlag(BOOL manualreceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    RWTime now;
    struct tm start_tm;

    now.extract(&start_tm);

    if( _availableweekdays(start_tm.tm_wday) == 'Y' &&
        ( _availableweekdays(7) == 'Y' ||
          !CtiHolidayManager::getInstance().isHoliday() ) )
        return TRUE;
    else
        return FALSE;
}

/*---------------------------------------------------------------------------
    isWithinValidControlWindow()

    Returns boolean if this program is in a valid control window.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramBase::isWithinValidControlWindow(ULONG secondsFromBeginningOfDay)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL returnBoolean = FALSE;
    if( _lmprogramcontrolwindows.entries() > 0 )
    {
        for(ULONG i=0;i<_lmprogramcontrolwindows.entries();i++)
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

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramBase::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    >> _minrestarttime
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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    << _minrestarttime
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
        _programstatuspointid = right._programstatuspointid;
        _programstate = right._programstate;
        _reductionanalogpointid = right._reductionanalogpointid;
        _reductiontotal = right._reductiontotal;
        _startedcontrolling = right._startedcontrolling;
        _lastcontrolsent = right._lastcontrolsent;
        _manualcontrolreceivedflag = right._manualcontrolreceivedflag;

        _lmprogramcontrolwindows.clearAndDestroy();
        for(UINT i=0;i<right._lmprogramcontrolwindows.entries();i++)
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramBase::operator!=(const CtiLMProgramBase& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMProgramBase::dumpDynamicData()
{
    RWDBDateTime currentDateTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
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

                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }*/
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
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramBase::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBNullIndicator isNull;
    RWDBDateTime dynamicTimeStamp;
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
    rdr["userorder"] >> _userorder;
    rdr["stoporder"] >> _stoporder;
    rdr["defaultpriority"] >> _defaultpriority;
    rdr["controltype"] >> _controltype;
    rdr["availableseasons"] >> _availableseasons;
    rdr["availableweekdays"] >> _availableweekdays;
    rdr["maxhoursdaily"] >> _maxhoursdaily;
    rdr["maxhoursmonthly"] >> _maxhoursmonthly;
    rdr["maxhoursseasonal"] >> _maxhoursseasonal;
    rdr["maxhoursannually"] >> _maxhoursannually;
    rdr["minactivatetime"] >> _minactivatetime;
    rdr["minrestarttime"] >> _minrestarttime;

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
        rdr["timestamp"] >> dynamicTimeStamp;

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

