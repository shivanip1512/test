#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_pttag.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;

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

bool CtiTableDynamicTag::Insert(Cti::Database::DatabaseConnection &conn)
{
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

    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
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

bool CtiTableDynamicTag::Update(Cti::Database::DatabaseConnection &conn)
{
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

    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "pointid = ?, "
                                        "tagid = ?, "
                                        "username = ?, "
                                        "action = ?, "
                                        "description = ?, "
                                        "tagtime = ?, "
                                        "refstr = ?, "
                                        "forstr = ?"
                                    " where "
                                        "instanceid = ?";

    using namespace Cti::Database;

    DatabaseWriter updater(conn, sql);

    updater
        << getPointId()
        << getTagId()
        << getUserName()
        << getActionStr()
        << getDescriptionStr()
        << getTagTime()
        << getReferenceStr()
        << getTaggedForStr()
        << getInstanceId();

    if( ! executeUpdater( updater, __FILE__, __LINE__ , LogDebug(isDebugLudicrous()), LogNoRowsAffected::Disable ))
    {
        return Insert(conn); // Try a vanilla insert if the update failed!
    }

    setDirty(false);
    return true;
}

bool CtiTableDynamicTag::Delete()
{
    return Delete( getInstanceId() );
}

bool CtiTableDynamicTag::Delete(int instance)
{
    static const std::string sql = "delete from " + getTableName() + " where instanceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << instance;

    return Cti::Database::executeCommand( deleter, __FILE__, __LINE__, Cti::Database::LogDebug( isDebugLudicrous() ));
}

string CtiTableDynamicTag::getSQLCoreStatement()
{
    static const string sql =  "SELECT DYT.instanceid, DYT.pointid, DYT.tagid, DYT.username, DYT.action, DYT.description, "
                                  "DYT.tagtime, DYT.refstr, DYT.forstr "
                               "FROM DynamicTags DYT";

    return sql;
}

void CtiTableDynamicTag::DecodeDatabaseReader(Cti::RowReader& rdr)
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

std::string CtiTableDynamicTag::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableDynamicTag";
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
