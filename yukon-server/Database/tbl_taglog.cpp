#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_taglog.h"
#include "database_reader.h"
#include "database_writer.h"

using std::string;
using std::endl;

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


string CtiTableTagLog::getTableName()
{
    return string("TagLog");
}

bool CtiTableTagLog::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
        << getLogId()
        << getInstanceId()
        << getPointId()
        << getTagId()
        << getUserName()
        << getActionStr()
        << getDescriptionStr()
        << getTagTime()
        << getReferenceStr()
        << getTaggedForStr();

    if(isDebugLudicrous())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }

    bool success = inserter.execute();

    if( ! success )    // Error occured!
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }
    else
    {
        resetDirty(FALSE);
    }

    return success;
}

bool CtiTableTagLog::Update(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "instanceid = ?, "
                                        "pointid = ?, "
                                        "tagid = ?, "
                                        "username = ?, "
                                        "action = ?, "
                                        "description = ?, "
                                        "tagtime = ?, "
                                        "refstr = ?, "
                                        "forstr = ?"
                                   " where "
                                        "logid = ?";

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

    Cti::Database::DatabaseWriter   updater(conn, sql);

    updater
        << getInstanceId()
        << getPointId()
        << getTagId()
        << getUserName()
        << getActionStr()
        << getDescriptionStr()
        << getTagTime()
        << getReferenceStr()
        << getTaggedForStr()
        << getLogId();

    bool success = executeUpdater(updater);

    if( success )
    {
        setDirty(false);
    }
    else
    {
        success = Insert(conn);        // Try a vanilla insert if the update failed!
    }

    return success;
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

string CtiTableTagLog::getUserName() const          // VC(60)  Console user name
{
    return _userName;
}
string CtiTableTagLog::getActionStr() const         // VC(20)
{
    return _actionStr;
}
string CtiTableTagLog::getDescriptionStr() const    // VC(120)
{
    return _descriptionStr;
}

CtiTime CtiTableTagLog::getTagTime() const        // when was tag created
{
    return _tagtime;
}
string CtiTableTagLog::getReferenceStr() const      // job id, etc, user field
{
    return _referenceStr;
}
string CtiTableTagLog::getTaggedForStr() const
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

CtiTableTagLog& CtiTableTagLog::setUserName(const string& str)          // VC(60)  Console user name
{
    _userName = str;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setActionStr(const string& str)         // VC(20)
{
#if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " Action String is " << str << endl;
    }
#endif
    _actionStr = str;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setDescriptionStr(const string& str)    // VC(120)
{
    _descriptionStr = str;
    return *this;
}

CtiTableTagLog& CtiTableTagLog::setTagTime(const CtiTime &dbdt)        // when was tag created
{
    _tagtime = dbdt;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setReferenceStr(const string& str)      // job id, etc, user field
{
    _referenceStr = str;
    return *this;
}
CtiTableTagLog& CtiTableTagLog::setTaggedForStr(const string& str)
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

        dout << "getTagTime()         "  << getTagTime() << endl;
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
            static const string sql = "SELECT MAX (TLG.instanceid) as maxid "
                                      "FROM TagLog TLG";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);
            rdr.execute();

            if( rdr() )
            {
                rdr["maxid"] >> _maxInstanceId;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            static const string sql = "SELECT maxid = MAX (TLG.logid) "
                                      "FROM TagLog TLG";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);
            rdr.execute();

            if( rdr() )
            {
                rdr["maxid"] >> _nextLogId;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

