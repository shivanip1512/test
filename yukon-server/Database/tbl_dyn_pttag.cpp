#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_pttag
*
* Date:   12/22/2003
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
#include "tbl_dyn_pttag.h"

CtiTableDynamicTag::CtiTableDynamicTag() :
_instanceId(0),               // no two tags share the same one
_pointId(0),                  //
_tagid(0),                    // refers to id in tag table
_userName("(none)"),          // VC(60)
_actionStr("(none)"),         // VC(20)
_descriptionStr("(none)"),    // VC(120)
_referenceStr("(none)"),      // job id, etc, user field
_taggedForStr("(none)")       // user field
{
}

CtiTableDynamicTag::CtiTableDynamicTag(const CtiTableDynamicTag& aRef) :
_instanceId(0),               // no two tags share the same one
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

CtiTableDynamicTag::~CtiTableDynamicTag()
{
}

CtiTableDynamicTag& CtiTableDynamicTag::operator=(const CtiTableDynamicTag& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        setInstanceId(aRef.getInstanceId());
        setPointId(aRef.getPointId());
        setTagId(aRef.getTagId());
        setUserName(aRef.getUserName());
        setActionStr(aRef.getActionStr());
        setDescriptionStr(aRef.getDescriptionStr());
        setTagTime(aRef.getTagTime());
        setReferenceStr(aRef.getReferenceStr());
        setTaggedForStr(aRef.getTaggedForStr());
    }
    return *this;
}

int CtiTableDynamicTag::operator==(const CtiTableDynamicTag &right) const
{
    return( getInstanceId() == right.getInstanceId() );
}

bool CtiTableDynamicTag::operator<(const CtiTableDynamicTag& aRef) const
{
    return ( getInstanceId() < aRef.getInstanceId() );
}


RWCString CtiTableDynamicTag::getTableName()
{
    return RWCString("DynamicTags");
}


RWDBStatus CtiTableDynamicTag::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTableDynamicTag::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Update(conn);
}

RWDBStatus CtiTableDynamicTag::Insert(RWDBConnection &conn)
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

    inserter << getInstanceId()
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

    if(inserter.status().errorCode() != RWDBStatus::ok)    // error occured!
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

RWDBStatus CtiTableDynamicTag::Update(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["instanceid"] == getInstanceId() );

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

RWDBStatus CtiTableDynamicTag::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
        table["instanceid"] <<
        table["pointid"] <<
        table["tagid"] <<
        table["username"] <<
        table["action"] <<
        table["description"] <<
        table["tagtime"] <<
        table["refstr"] <<
        table["forstr"];

    selector.where( table["instanceid"] == getInstanceId() );

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
RWDBStatus CtiTableDynamicTag::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["instanceid"] == getInstanceId() );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}

RWDBStatus CtiTableDynamicTag::Delete(int instance)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["instanceid"] == instance );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}


void CtiTableDynamicTag::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTableDynamicTag::getTableName());

    selector <<
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
void CtiTableDynamicTag::DecodeDatabaseReader(RWDBReader& rdr)
{
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

int CtiTableDynamicTag::getInstanceId() const        // no two tags share the same one
{
    return _instanceId;
}

int CtiTableDynamicTag::getPointId() const           //
{
    return _pointId;
}
int CtiTableDynamicTag::getTagId() const             // refers to id in tag table
{
    return _tagid;
}

RWCString CtiTableDynamicTag::getUserName() const          // VC(60)  Console user name
{
    return _userName;
}
RWCString CtiTableDynamicTag::getActionStr() const         // VC(20)
{
    return _actionStr;
}
RWCString CtiTableDynamicTag::getDescriptionStr() const    // VC(120)
{
    return _descriptionStr;
}

RWDBDateTime CtiTableDynamicTag::getTagTime() const        // when was tag created
{
    return _tagtime;
}
RWCString CtiTableDynamicTag::getReferenceStr() const      // job id, etc, user field
{
    return _referenceStr;
}
RWCString CtiTableDynamicTag::getTaggedForStr() const
{
    return _taggedForStr;
}

CtiTableDynamicTag& CtiTableDynamicTag::setInstanceId(int id)        // no two tags share the same one
{
    _instanceId = id;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setPointId(int id)           //
{
    _pointId = id;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setTagId(int id)             // refers to id in tag table
{
    _tagid = id;
    return *this;
}

CtiTableDynamicTag& CtiTableDynamicTag::setUserName(const RWCString& str)          // VC(60)  Console user name
{
    _userName = str;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setActionStr(const RWCString& str)         // VC(20)
{
    _actionStr = str;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setDescriptionStr(const RWCString& str)    // VC(120)
{
    _descriptionStr = str;
    return *this;
}

CtiTableDynamicTag& CtiTableDynamicTag::setTagTime(const RWDBDateTime &dbdt)        // when was tag created
{
    _tagtime = dbdt;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setReferenceStr(const RWCString& str)      // job id, etc, user field
{
    _referenceStr = str;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setTaggedForStr(const RWCString& str)
{
    _taggedForStr = str;
    return *this;
}

void CtiTableDynamicTag::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

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
