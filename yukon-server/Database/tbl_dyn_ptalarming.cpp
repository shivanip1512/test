#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_ptalarming
*
* Date:   8/7/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_ptalarming.h"

CtiTableDynamicPointAlarming::CtiTableDynamicPointAlarming() :
_tags(0),
_user("(none)"),
_action("(none)"),
_description("(none)")
{
}

CtiTableDynamicPointAlarming::CtiTableDynamicPointAlarming(const CtiTableDynamicPointAlarming& aRef) :
_tags(0),
_user("(none)"),
_action("(none)"),
_description("(none)")
{
    *this = aRef;
}

CtiTableDynamicPointAlarming::~CtiTableDynamicPointAlarming()
{
}

CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::operator=(const CtiTableDynamicPointAlarming& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

int CtiTableDynamicPointAlarming::operator==(const CtiTableDynamicPointAlarming &right) const
{
    return( getPointID() == right.getPointID() && getAlarmCondition() == right.getAlarmCondition() );
}

RWCString CtiTableDynamicPointAlarming::getTableName()
{
    return RWCString("DynamicPointAlarming");
}


RWDBStatus CtiTableDynamicPointAlarming::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTableDynamicPointAlarming::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Update(conn);
}

RWDBStatus CtiTableDynamicPointAlarming::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();


    if(getAction().isNull())
    {
        setAction("(none)");
    }
    if(getDescription().isNull())
    {
        setDescription("(none)");
    }
    if(getUser().isNull())
    {
        setUser("(none)");
    }

    inserter <<
    getPointID() <<
    getAlarmCondition() <<
    getCategoryID() <<
    getAlarmDBTime() <<
    getAction() <<
    getDescription() <<
    getTags() <<
    getLogID() <<
    getSOE() <<
    getLogType() <<
    getUser();

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl << endl;
    }

    inserter.execute( conn );

    if(inserter.status().errorCode() != RWDBStatus::ok)    // No error occured!
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
        }
    }
    else
    {
        resetDirty(FALSE);
    }

    return inserter.status();
}

RWDBStatus CtiTableDynamicPointAlarming::Update(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["pointid"] == getPointID() && table["alarmcondition"] == getAlarmCondition() );

    if(getAction().isNull())
    {
        setAction("(none)");
    }
    if(getDescription().isNull())
    {
        setDescription("(none)");
    }
    if(getUser().isNull())
    {
        setUser("(none)");
    }

    updater <<
    table["categoryid"].assign(getCategoryID()) <<
    table["alarmtime"].assign(getAlarmTime()) <<
    table["action"].assign(getAction()) <<
    table["description"].assign(getDescription()) <<
    table["tags"].assign(getTags()) <<
    table["logid"].assign(getLogID());
    table["soe_tag"].assign(getSOE());
    table["type"].assign(getLogType());
    table["username"].assign(getUser());

    RWDBResult myResult = updater.execute( conn );
    RWDBStatus stat = myResult.status();
    RWDBStatus::ErrorCode ec = stat.errorCode();

    RWDBTable myTable = myResult.table();
    long rowsAffected = myResult.rowCount();

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl << endl;
    }

    if( ec == RWDBStatus::ok && rowsAffected > 0)
    {
        setDirty(false);
    }
    else
    {
        stat = Insert(conn);        // Try a vanilla insert if the update failed!
    }

    return stat;
}

RWDBStatus CtiTableDynamicPointAlarming::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["pointid"] <<
    table["alarmcondition"] <<
    table["categoryid"] <<
    table["alarmtime"] <<
    table["action"] <<
    table["description"] <<
    table["tags"]  <<
    table["logid"] <<
    table["soe_tag"] <<
    table["type"] <<
    table["username"];

    selector.where( table["pointid"] == getPointID() && table["alarmcondition"] == getAlarmCondition() );

    RWDBReader reader = selector.reader( conn );

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are sirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( TRUE );
    }

    return reader.status();
}
RWDBStatus CtiTableDynamicPointAlarming::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == getPointID() && table["alarmcondition"] == getAlarmCondition() );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}

RWDBStatus CtiTableDynamicPointAlarming::Delete(long pointid, int alarm_condition)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == pointid && table["alarmcondition"] == alarm_condition );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}


void CtiTableDynamicPointAlarming::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTableDynamicPointAlarming::getTableName());

    selector <<
    keyTable["pointid"] <<
    keyTable["alarmcondition"] <<
    keyTable["categoryid"] <<
    keyTable["alarmtime"] <<
    keyTable["action"] <<
    keyTable["description"] <<
    keyTable["tags"]  <<
    keyTable["logid"] <<
    keyTable["soe_tag"] <<
    keyTable["type"] <<
    keyTable["username"];

    selector.from(keyTable);
}
void CtiTableDynamicPointAlarming::DecodeDatabaseReader(RWDBReader& rdr)
{
    rdr["pointid"] >> _pointID;
    rdr["alarmcondition"] >> _alarmCondition;
    rdr["categoryid"] >> _categoryID;
    rdr["alarmtime"] >> _alarmTime;
    rdr["action"] >> _action;
    rdr["description"] >> _description;
    rdr["tags"] >> _tags;
    rdr["logid"] >> _logID;
    rdr["soe_tag"] >> _soe;
    rdr["type"] >> _logType;
    rdr["username"] >> _user;

    resetDirty(FALSE);
}

LONG CtiTableDynamicPointAlarming::getPointID() const
{
    return _pointID;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setPointID(LONG pointID)
{
    _pointID = pointID;
    return *this;
}

UINT CtiTableDynamicPointAlarming::getAlarmCondition() const        // Same as Priority in the systemlog.priority column
{
    return _alarmCondition;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmCondition(INT cnd)
{
    _alarmCondition = cnd;
    return *this;
}

UINT CtiTableDynamicPointAlarming::getCategoryID() const        // Same as Priority in the systemlog.priority column
{
    return _categoryID;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setCategoryID(UINT cid)
{
    _categoryID = cid;
    return *this;
}

RWTime CtiTableDynamicPointAlarming::getAlarmTime() const
{
    return _alarmTime.rwtime();
}

CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmTime(const RWTime &rwt)
{
    _alarmTime = RWDBDateTime( rwt );
    return *this;
}

RWDBDateTime CtiTableDynamicPointAlarming::getAlarmDBTime() const
{
    return _alarmTime;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmDBTime(const RWDBDateTime &rwt)
{
    _alarmTime = rwt;
    return *this;
}

RWCString CtiTableDynamicPointAlarming::getAction() const
{
    return _action;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAction(const RWCString &str)
{
    _action = str;
    return *this;
}

RWCString CtiTableDynamicPointAlarming::getDescription() const
{
    return _description;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setDescription(const RWCString &str)
{
    _description = str;
    return *this;
}

UINT CtiTableDynamicPointAlarming::getTags() const
{
    return _tags;
}
UINT CtiTableDynamicPointAlarming::setTags(UINT tags)
{
    setDirty(TRUE);
    _tags |= (tags & MASK_ANY_ALARM);
    return _tags;
}
UINT CtiTableDynamicPointAlarming::resetTags(UINT mask)
{
    setDirty(TRUE);
    _tags &= ~mask;
    return (_tags & MASK_ANY_ALARM);
}

LONG CtiTableDynamicPointAlarming::getLogID() const
{
    return _logID;
}
CtiTableDynamicPointAlarming&   CtiTableDynamicPointAlarming::setLogID(UINT id)
{
    _logID = id;
    return *this;
}

INT CtiTableDynamicPointAlarming::getSOE() const
{
    return _soe;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setSOE(const INT &i)
{
    _soe = i;
    return *this;
}

INT CtiTableDynamicPointAlarming::getLogType() const
{
    return _logType;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setLogType(const INT &i)
{
    _logType = i;
    return *this;
}

RWCString CtiTableDynamicPointAlarming::getUser() const
{
    return _user;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setUser(const RWCString &str)
{
    _user = str;
    return *this;
}


void CtiTableDynamicPointAlarming::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "getPointID()        " << getPointID() << endl;
        dout << "getAlarmCondition() " << getAlarmCondition() << endl;
        dout << "getCategoryID()     " << getCategoryID() << endl;
        dout << "getAlarmDBTime()    " << getAlarmDBTime().rwtime() << endl;
        dout << "getAction()         " << getAction() << endl;
        dout << "getDescription()    " << getDescription() << endl;
        dout << "getTags()            0x" << CtiNumStr(getTags()).xhex().zpad(8) << endl;
        dout << "getLogID()          " << getLogID() << endl;
        dout << "getSOE()            " << getSOE() << endl;
        dout << "getLogType()        " << getLogType() << endl;
        dout << "getUser()           " << getUser() << endl;

    }
}
