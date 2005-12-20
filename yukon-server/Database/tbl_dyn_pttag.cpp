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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_pttag.h"
#include "rwutil.h"

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


string CtiTableDynamicTag::getTableName()
{
    return string("DynamicTags");
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
    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    if(getUserName().empty())
    {
        setUserName("(none)");
    }
    if(getActionStr().empty())
    {
        setActionStr("(none)");
    }
    if(getDescriptionStr().empty())
    {
        setDescriptionStr("(none)");
    }
    if(getReferenceStr().empty())
    {
        setReferenceStr("(none)");
    }
    if(getTaggedForStr().empty())
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
        dout << endl << CtiTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl << endl;
    }

    ExecuteInserter(conn,inserter,__FILE__,__LINE__);

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
    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["instanceid"] == getInstanceId() );

    if(getUserName().empty())
    {
        setUserName("(none)");
    }
    if(getActionStr().empty())
    {
        setActionStr("(none)");
    }
    if(getDescriptionStr().empty())
    {
        setDescriptionStr("(none)");
    }
    if(getReferenceStr().empty())
    {
        setReferenceStr("(none)");
    }
    if(getTaggedForStr().empty())
    {
        setTaggedForStr("(none)");
    }

    updater <<
    table["instanceid"].assign(getInstanceId()) <<
    table["pointid"].assign(getPointId()) <<
    table["tagid"].assign(getTagId()) <<
    table["username"].assign(getUserName().c_str()) <<
    table["action"].assign(getActionStr().c_str()) <<
    table["description"].assign(getDescriptionStr().c_str()) <<
    table["tagtime"].assign(toRWDBDT(getTagTime())) <<
    table["refstr"].assign(getReferenceStr().c_str()) <<
    table["forstr"].assign(getTaggedForStr().c_str());

    long rowsAffected;
    RWDBStatus stat = ExecuteUpdater(conn,updater,__FILE__,__LINE__&rowsAffected);

    if( stat.errorCode() == RWDBStatus::ok && rowsAffected > 0)
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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["instanceid"] == getInstanceId() );

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}

RWDBStatus CtiTableDynamicTag::Delete(int instance)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["instanceid"] == instance );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << deleter.asString() << endl << endl;
    }

    return deleter.execute( conn ).status();
}


void CtiTableDynamicTag::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTableDynamicTag::getTableName().c_str());

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

string CtiTableDynamicTag::getUserName() const          // VC(60)  Console user name
{
    return _userName;
}
string CtiTableDynamicTag::getActionStr() const         // VC(20)
{
    return _actionStr;
}
string CtiTableDynamicTag::getDescriptionStr() const    // VC(120)
{
    return _descriptionStr;
}

CtiTime CtiTableDynamicTag::getTagTime() const        // when was tag created
{
    return _tagtime;
}
string CtiTableDynamicTag::getReferenceStr() const      // job id, etc, user field
{
    return _referenceStr;
}
string CtiTableDynamicTag::getTaggedForStr() const
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

CtiTableDynamicTag& CtiTableDynamicTag::setUserName(const string& str)          // VC(60)  Console user name
{
    _userName = str;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setActionStr(const string& str)         // VC(20)
{
    _actionStr = str;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setDescriptionStr(const string& str)    // VC(120)
{
    _descriptionStr = str;
    return *this;
}

CtiTableDynamicTag& CtiTableDynamicTag::setTagTime(const CtiTime &dbdt)        // when was tag created
{
    _tagtime = dbdt;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setReferenceStr(const string& str)      // job id, etc, user field
{
    _referenceStr = str;
    return *this;
}
CtiTableDynamicTag& CtiTableDynamicTag::setTaggedForStr(const string& str)
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

        dout << "getTagTime()         "  << getTagTime() << endl;
        dout << "getReferenceStr()    "  << getReferenceStr() << endl;
        dout << "getTaggedForStr()    "  << getTaggedForStr() << endl;
    }
}
