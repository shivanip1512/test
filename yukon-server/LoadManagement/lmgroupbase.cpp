/*---------------------------------------------------------------------------
        Filename:  lmgroupbase.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMGroupBase.
                        CtiLMGroupBase maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  2/9/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupbase.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern BOOL _LM_DEBUG;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupBase::CtiLMGroupBase()
{
}

CtiLMGroupBase::CtiLMGroupBase(RWDBReader& rdr)
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
ULONG CtiLMGroupBase::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupBase::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the group
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getGroupOrder

    Returns the order of the group in a program
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getGroupOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _grouporder;
}

/*---------------------------------------------------------------------------
    getKWCapacity

    Returns the kw capacity of the group
---------------------------------------------------------------------------*/
DOUBLE CtiLMGroupBase::getKWCapacity() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _kwcapacity;
}

/*---------------------------------------------------------------------------
    getChildOrder

    Returns the order of the group macro children in a program
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getChildOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _childorder;
}

/*---------------------------------------------------------------------------
    getAlarmInhibit

    Returns the alarm inhibit flag of the group
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::getAlarmInhibit() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _alarminhibit;
}

/*---------------------------------------------------------------------------
    getControlInhibit

    Returns the control inhibit flag of the group
---------------------------------------------------------------------------*/
BOOL CtiLMGroupBase::getControlInhibit() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlinhibit;
}

/*---------------------------------------------------------------------------
    getGroupControlState

    Returns the control state of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getGroupControlState() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _groupcontrolstate;
}

/*---------------------------------------------------------------------------
    getCurrentHoursDaily

    Returns the current hours for this day for the group in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getCurrentHoursDaily() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currenthoursdaily;
}

/*---------------------------------------------------------------------------
    getCurrentHoursMonthly

    Returns the current hours for this month for the group in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getCurrentHoursMonthly() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currenthoursmonthly;
}

/*---------------------------------------------------------------------------
    getCurrentHoursSeasonal

    Returns the current hours for this season for the group in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getCurrentHoursSeasonal() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currenthoursseasonal;
}

/*---------------------------------------------------------------------------
    getCurrentHoursAnnually

    Returns the current hours for this year for the group in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMGroupBase::getCurrentHoursAnnually() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currenthoursannually;
}

/*---------------------------------------------------------------------------
    getLastControlSent

    Returns the time of the last control sent for the program
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMGroupBase::getLastControlSent() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lastcontrolsent;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOId(ULONG id)
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
CtiLMGroupBase& CtiLMGroupBase::setPAOCategory(const RWCString& category)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAOType(ULONG type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flaf of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setGroupOrder

    Sets the order of the group in a program
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setGroupOrder(ULONG order)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _grouporder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setKWCapacity

    Sets the kw capacity of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setKWCapacity(DOUBLE kwcap)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _kwcapacity = kwcap;
    return *this;
}

/*---------------------------------------------------------------------------
    setChildOrder

    Sets the order of the group macro children in a program
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setChildOrder(ULONG order)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _childorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setAlarmInhibit

    Sets the alarm inhibit flag of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setAlarmInhibit(BOOL alarm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _alarminhibit = alarm;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlInhibit

    Sets the control inhibit flag of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setControlInhibit(BOOL control)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlinhibit = control;
    return *this;
}

/*---------------------------------------------------------------------------
    setGroupControlState

    Sets the control state of the group
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setGroupControlState(ULONG controlstate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _groupcontrolstate = controlstate;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursDaily

    Sets the current hours for this day for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursDaily(ULONG daily)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currenthoursdaily = daily;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursMonthly

    Sets the current hours for this month for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursMonthly(ULONG monthly)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currenthoursmonthly = monthly;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursSeasonal

    Sets the current hours for this season for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursSeasonal(ULONG seasonal)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currenthoursseasonal = seasonal;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentHoursAnnually

    Sets the current hours for this year for the group in seconds
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setCurrentHoursAnnually(ULONG annually)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currenthoursannually = annually;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastControlSent

    Sets the time of the last control sent for the program
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::setLastControlSent(const RWDBDateTime& controlsent)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _lastcontrolsent = controlsent;
    return *this;
}


/*-------------------------------------------------------------------------
    createLatchingRequestMsg

    .
--------------------------------------------------------------------------*/
CtiCommandMsg* CtiLMGroupBase::createLatchingRequestMsg(ULONG rawState, int priority) const
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

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending latch command, LM Group: " << getPAOName() << endl;
    }
    return returnCommandMsg;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupBase::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::restoreGuts( istrm );

    RWTime tempTime;
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
    >> tempTime;

    _lastcontrolsent = RWDBDateTime(tempTime);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupBase::saveGuts(RWvostream& ostrm ) const
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
    << _lastcontrolsent.rwtime();

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupBase::operator=(const CtiLMGroupBase& right)
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
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupBase::operator==(const CtiLMGroupBase& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupBase::operator!=(const CtiLMGroupBase& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return !operator==(right);
}

/*---------------------------------------------------------------------------
    convertSecondsToEvenTimeString

    
---------------------------------------------------------------------------*/
RWCString CtiLMGroupBase::convertSecondsToEvenTimeString(ULONG shedTime) const
{
    char tempchar[64];
    RWCString retStr;

    if( (shedTime % 3600) == 0 )
    {
        ULONG hourShedTime = shedTime/3600;
        _ultoa(hourShedTime,tempchar,10);
        retStr += tempchar;
        retStr += "h";
    }
    else if( (shedTime % 60) == 0 )
    {
        ULONG minuteShedTime = shedTime/60;
        _ultoa(minuteShedTime,tempchar,10);
        retStr += tempchar;
        retStr += "m";
    }
    else
    {
        _ultoa(shedTime,tempchar,10);
        retStr += tempchar;
        retStr += "s";
    }

    return retStr;
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
    rdr["alarminhibit"] >> tempBoolString;
    tempBoolString.toLower();
    setAlarmInhibit(tempBoolString=="y"?TRUE:FALSE);
    rdr["controlinhibit"] >> tempBoolString;
    tempBoolString.toLower();
    setControlInhibit(tempBoolString=="y"?TRUE:FALSE);
    rdr["grouporder"] >> _grouporder;
    rdr["kwcapacity"] >> _kwcapacity;

    rdr["childorder"] >> isNull;
    if( !isNull )
    {
        rdr["childorder"] >> _childorder;
    }
    else
    {
        _childorder = 0;
    }

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

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        setGroupControlState(InactiveState);
        setCurrentHoursDaily(0);
        setCurrentHoursMonthly(0);
        setCurrentHoursSeasonal(0);
        setCurrentHoursAnnually(0);
        setLastControlSent(RWDBDateTime(1990,1,1,0,0,0,0));

        _insertDynamicDataFlag = TRUE;
    }

    /*if( getPAOId() == 4181 || getPAOId() == 4195 || getPAOId() == 4154 )
    {//debug for MVEA only !!!
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << __FILE__ << "(" << __LINE__ << ")" << endl;
        dout << "isNull-" << isNull << endl;
        dout << "PAOId-" << getPAOId() << "  GroupControlState-" << getGroupControlState() << endl;
        dout << "CurrentHoursDaily-" << getCurrentHoursDaily() << "  CurrentHoursMonthly-" << getCurrentHoursMonthly() << endl;
        dout << "CurrentHoursSeasonal-" << getCurrentHoursSeasonal() << "  CurrentHoursAnnually-" << getCurrentHoursAnnually() << endl;
        dout << "LastControlSent-" << getLastControlSent().rwtime() << "  _insertDynamicDataFlag-" << _insertDynamicDataFlag << endl;
    }*/
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this group.
---------------------------------------------------------------------------*/
void CtiLMGroupBase::dumpDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBDateTime currentDateTime = RWDBDateTime();

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
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
                << dynamicLMGroupTable["timestamp"].assign( currentDateTime );

                updater.where(dynamicLMGroupTable["deviceid"]==getPAOId());

                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }*/
                /*if( getPAOId() == 4181 || getPAOId() == 4195 || getPAOId() == 4154 )
                {//debug for MVEA only !!!
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << __FILE__ << "(" << __LINE__ << ")" << endl;
                    dout << "PAOId-" << getPAOId() << "  GroupControlState-" << getGroupControlState() << endl;
                    dout << "CurrentHoursDaily-" << getCurrentHoursDaily() << "  CurrentHoursMonthly-" << getCurrentHoursMonthly() << endl;
                    dout << "CurrentHoursSeasonal-" << getCurrentHoursSeasonal() << "  CurrentHoursAnnually-" << getCurrentHoursAnnually() << endl;
                    dout << "LastControlSent-" << getLastControlSent().rwtime() << "  currentDateTime-" << currentDateTime.rwtime() << endl;
                    dout << "_insertDynamicDataFlag-" << _insertDynamicDataFlag << endl;
                    dout << RWTime() << " - " << updater.asString().data() << endl;
                }*/
                updater.execute( conn );
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
                << currentDateTime;

                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }*/

                _insertDynamicDataFlag = FALSE;
                /*if( getPAOId() == 4181 || getPAOId() == 4195 || getPAOId() == 4154 )
                {//debug for MVEA only !!!
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << __FILE__ << "(" << __LINE__ << ")" << endl;
                    dout << "PAOId-" << getPAOId() << "  GroupControlState-" << getGroupControlState() << endl;
                    dout << "CurrentHoursDaily-" << getCurrentHoursDaily() << "  CurrentHoursMonthly-" << getCurrentHoursMonthly() << endl;
                    dout << "CurrentHoursSeasonal-" << getCurrentHoursSeasonal() << "  CurrentHoursAnnually-" << getCurrentHoursAnnually() << endl;
                    dout << "LastControlSent-" << getLastControlSent().rwtime() << "  currentDateTime-" << currentDateTime.rwtime() << endl;
                    dout << "_insertDynamicDataFlag-" << _insertDynamicDataFlag << endl;
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }*/

                inserter.execute( conn );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

// Static Members
int CtiLMGroupBase::InactiveState = STATEZERO;
int CtiLMGroupBase::ActiveState = STATEONE;
int CtiLMGroupBase::InactivePendingState = STATETWO;
int CtiLMGroupBase::ActivePendingState = STATETHREE;

