#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_taglog
*
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/



#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_taglog.h"


int CtiTableTagLog::_maxInstanceId = 0;
int CtiTableTagLog::_nextLogId = 0;

CtiTableTagLog::CtiTableTagLog() :
_logId(getNextLogId()),       // no two tags share the same one
_instanceId(0),               //
_pointId(0),                  //
_tagid(0),                    // refers to id in tag table
_userName("(none)"),          // VC(60)
_actionStr("(none)"),         // VC(20)
_descriptionStr("(none)"),    // VC(120)
_referenceStr("(none)"),      // job id, etc, user field
_taggedForStr("(none)")       // user field
{
}

CtiTableTagLog::CtiTableTagLog(const CtiTableTagLog& aRef) :
_logId(0),                    // no two tags share the same one
_instanceId(0),               //
_pointId(0),                  //
_tagid(0),                    // refers to id in tag table
_userName("(none)"),          // VC(60)
_actionStr("(none)"),         // VC(20)
_descriptionStr("(none)"),    // VC(120)
_referenceStr("(none)"),      // job id, etc, user field
_taggedForStr("(none)")      // user field
{
    *this = aRef;
}

CtiTableTagLog::~CtiTableTagLog()
{
}

CtiTableTagLog& CtiTableTagLog::operator=(const CtiTableTagLog& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        setLogId( aRef.getLogId() );
        setInstanceId( aRef.getInstanceId() );
        setPointId( aRef.getPointId() );
        setTagId( aRef.getTagId() );
        setUserName( aRef.getUserName() );
        setActionStr( aRef.getActionStr() );
        setDescriptionStr( aRef.getDescriptionStr() );
        setTagTime( aRef.getTagTime() );
        setReferenceStr( aRef.getReferenceStr() );
        setTaggedForStr( aRef.getTaggedForStr() );
    }
    return *this;
}

int CtiTableTagLog::operator==(const CtiTableTagLog &right) const
{
    return( getLogId() == right.getLogId() );
}

bool CtiTableTagLog::operator<(const CtiTableTagLog& aRef) const
{
    return(getLogId() < aRef.getLogId());
}


RWCString CtiTableTagLog::getTableName()
{
    return RWCString("TagLog");
}


RWDBStatus CtiTableTagLog::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTableTagLog::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Update(conn);
}

RWDBStatus CtiTableTagLog::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    if(getUserName().isNull())
    {
        setUserName("(none)");
    }
    if(getActionStr().isNull())
    {
        setActionStr("(none)");
    }
    if(getDescriptionStr().isNull())
    {
        setDescriptionStr("(none)");
    }
    if(getReferenceStr().isNull())
    {
        setReferenceStr("(none)");
    }
    if(getTaggedForStr().isNull())
    {
        setTaggedForStr("(none)");
    }

    inserter << getLogId()
        << getInstanceId()
        << getPointId()
        << getTagId()
        << getUserName()
        << getActionStr()
        << getDescriptionStr()
        << getTagTime()
        << getReferenceStr()
        << getTaggedForStr();

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

RWDBStatus CtiTableTagLog::Update(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["logid"] == getLogId() );

    if(getUserName().isNull())
    {
        setUserName("(none)");
    }
    if(getActionStr().isNull())
    {
        setActionStr("(none)");
    }
    if(getDescriptionStr().isNull())
    {
        setDescriptionStr("(none)");
    }
    if(getReferenceStr().isNull())
    {
        setReferenceStr("(none)");
    }
    if(getTaggedForStr().isNull())
    {
        setTaggedForStr("(none)");
    }

    updater <<
    table["logid"].assign(getLogId()) <<
    table["instanceid"].assign(getInstanceId()) <<
    table["pointid"].assign(getPointId()) <<
    table["tagid"].assign(getTagId()) <<
    table["username"].assign(getUserName()) <<
    table["action"].assign(getActionStr()) <<
    table["description"].assign(getDescriptionStr()) <<
    table["tagtime"].assign(getTagTime()) <<
    table["refstr"].assign(getReferenceStr()) <<
    table["forstr"].assign(getTaggedForStr());

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

RWDBStatus CtiTableTagLog::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
        table["logid"] <<
        table["instanceid"] <<
        table["pointid"] <<
        table["pointid"] <<
        table["tagid"] <<
        table["username"] <<
        table["action"] <<
        table["description"] <<
        table["tagtime"] <<
        table["refstr"] <<
        table["forstr"];

    selector.where( table["logid"] == getLogId() );

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
RWDBStatus CtiTableTagLog::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["logid"] == getLogId() );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}

RWDBStatus CtiTableTagLog::Delete(int log)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["logid"] == log );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}


void CtiTableTagLog::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTableTagLog::getTableName());

    selector <<
    keyTable["logid"] <<
    keyTable["instanceid"] <<
    keyTable["pointid"] <<
    keyTable["tagid"] <<
    keyTable["username"] <<
    keyTable["action"] <<
    keyTable["description"] <<
    keyTable["tagtime"] <<
    keyTable["refstr"] <<
    keyTable["forstr"];

    selector.from(keyTable);
}
void CtiTableTagLog::DecodeDatabaseReader(RWDBReader& rdr)
{
    rdr["logid"]        >> _logId;
    rdr["instanceid"]   >> _instanceId;
    rdr["pointid"]      >> _pointId;
    rdr["tagid"]        >> _tagid;
    rdr["username"]     >> _userName;
    rdr["action"]       >> _actionStr;
    rdr["description"]  >> _descriptionStr;
    rdr["tagtime"]      >> _tagtime;
    rdr["refstr"]       >> _referenceStr;
    rdr["forstr"]       >> _taggedForStr;

    resetDirty(FALSE);
}

int CtiTableTagLog::getLogId() const        // no two tags share the same one
{
    return _logId;
}

int CtiTableTagLog::getPointId() const           //
{
    return _pointId;
}
int CtiTableTagLog::getTagId() const             // refers to id in tag table
{
    return _tagid;
}

RWCString CtiTableTagLog::getUserName() const          // VC(60)  Console user name
{
    return _userName;
}
RWCString CtiTableTagLog::getActionStr() const         // VC(20)
{
    return _actionStr;
}
RWCString CtiTableTagLog::getDescriptionStr() const    // VC(120)
{
    return _descriptionStr;
}

RWDBDateTime CtiTableTagLog::getTagTime() const        // when was tag created
{
    return _tagtime;
}
RWCString CtiTableTagLog::getReferenceStr() const      // job id, etc, user field
{
    return _referenceStr;
}
RWCString CtiTableTagLog::getTaggedForStr() const
{
    return _taggedForStr;
}

CtiTableTagLog& CtiTableTagLog::setLogId(int id)        // no two tags share the same one
{
    _logId = id;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setPointId(int id)           //
{
    _pointId = id;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setTagId(int id)             // refers to id in tag table
{
    _tagid = id;
    return *this;
}

CtiTableTagLog& CtiTableTagLog::setUserName(const RWCString& str)          // VC(60)  Console user name
{
    _userName = str;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setActionStr(const RWCString& str)         // VC(20)
{
#if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " Action String is " << str << endl;
    }
#endif
    _actionStr = str;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setDescriptionStr(const RWCString& str)    // VC(120)
{
    _descriptionStr = str;
    return *this;
}

CtiTableTagLog& CtiTableTagLog::setTagTime(const RWDBDateTime &dbdt)        // when was tag created
{
    _tagtime = dbdt;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setReferenceStr(const RWCString& str)      // job id, etc, user field
{
    _referenceStr = str;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setTaggedForStr(const RWCString& str)
{
    _taggedForStr = str;
    return *this;
}

void CtiTableTagLog::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getLogId()           "  << getLogId() << endl;
        dout << "getInstanceId()      "  << getInstanceId() << endl;
        dout << "getPointId()         "  << getPointId() << endl;
        dout << "getTagId()           "  << getTagId() << endl;

        dout << "getUserName()        "  << getUserName() << endl;
        dout << "getActionStr()       "  << getActionStr() << endl;
        dout << "getDescriptionStr()  "  << getDescriptionStr() << endl;

        dout << "getTagTime()         "  << getTagTime().rwtime() << endl;
        dout << "getReferenceStr()    "  << getReferenceStr() << endl;
        dout << "getTaggedForStr()    "  << getTaggedForStr() << endl;
    }
}

int CtiTableTagLog::getLastMaxInstanceId()
{
    return _maxInstanceId;
}

// This function will return the largest instanceid in the TagLog table.  This number will double as the instanceID in the DynamicTag table.
int CtiTableTagLog::getMaxInstanceId()
{
    if(_maxInstanceId <= 0)
    {
        try
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable table = getDatabase().table( getTableName() );
            RWDBSelector selector = getDatabase().selector();

            selector << rwdbName("maxid",rwdbMax(table["instanceid"]));

            RWDBReader rdr = selector.reader(conn);

            if(rdr.status().errorCode() == RWDBStatus::ok)
            {
                if( rdr() )
                {
                    rdr["maxid"] >> _maxInstanceId;
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return ++_maxInstanceId;
}

int CtiTableTagLog::getLastNextLogId()
{
    return _nextLogId;
}

// This function will return the largest logid in the TagLog table.
int CtiTableTagLog::getNextLogId()
{
    if(_nextLogId <= 0)
    {
        try
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable table = getDatabase().table( getTableName() );
            RWDBSelector selector = getDatabase().selector();

            selector << rwdbName("maxid",rwdbMax(table["logid"]));

            RWDBReader rdr = selector.reader(conn);

            if(rdr.status().errorCode() == RWDBStatus::ok)
            {
                if( rdr() )
                {
                    rdr["maxid"] >> _nextLogId;
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return ++_nextLogId;
}


int CtiTableTagLog::getInstanceId() const                   // Matches the dynamictag entry.  Follows the life cycle of the tag.
{
    return _instanceId;
}

CtiTableTagLog& CtiTableTagLog::setInstanceId(int id)       // Matches the dynamictag entry.  Follows the life cycle of the tag.
{
    _instanceId = id;
    return *this;
}

