#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_taglog.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

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

CtiTableTagLog::~CtiTableTagLog()
{
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

    if( ! Cti::Database::executeCommand( inserter, __FILE__, __LINE__, Cti::Database::LogDebug( isDebugLudicrous() )))
    {
        return false;
    }

    setDirty(false);
    
    return true; // No error occured!
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

    if( ! Cti::Database::executeUpdater( updater, __FILE__, __LINE__ ))
    {
        return Insert(conn); // Try a vanilla insert if the update failed!
    }

    setDirty(false);
    
    return true; // Update was successful!
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

std::string CtiTableTagLog::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableTagLog";
    itemList.add("getLogId()")          << getLogId();
    itemList.add("getInstanceId()")     << getInstanceId();
    itemList.add("getPointId()")        << getPointId();
    itemList.add("getTagId()")          << getTagId();
    itemList.add("getUserName()")       << getUserName();
    itemList.add("getActionStr()")      << getActionStr();
    itemList.add("getDescriptionStr()") << getDescriptionStr();
    itemList.add("getTagTime()")        << getTagTime();
    itemList.add("getReferenceStr()")   << getReferenceStr();
    itemList.add("getTaggedForStr()")   << getTaggedForStr();

    return itemList.toString();
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
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

