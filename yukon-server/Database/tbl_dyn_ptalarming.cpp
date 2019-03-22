#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_ptalarming.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

#include <boost/algorithm/string/erase.hpp>

#define DEFAULT_ACTIONLENGTH        60
#define DEFAULT_DESCRIPTIONLENGTH   120
#define DEFAULT_USERLENGTH          64

using std::string;
using std::endl;

CtiTableDynamicPointAlarming::CtiTableDynamicPointAlarming() :
    _tags(0),
    _user("(none)"),
    _action("(none)"),
    _description("(none)"),
    _pointID(0),
    _alarmCondition(0),
    _categoryID(0),
    _logID(0),
    _soe(0),
    _logType(0)
{
}

CtiTableDynamicPointAlarming::~CtiTableDynamicPointAlarming()
{
}

int CtiTableDynamicPointAlarming::operator==(const CtiTableDynamicPointAlarming &right) const
{
    return( getPointID() == right.getPointID() && getAlarmCondition() == right.getAlarmCondition() );
}

string CtiTableDynamicPointAlarming::getTableName()
{
    return string("DynamicPointAlarming");
}

bool CtiTableDynamicPointAlarming::Delete(long pointid, int alarm_condition)
{
    static const std::string sql = "delete from " + getTableName() + " where pointid = ? and alarmcondition = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter
        << pointid
        << alarm_condition;

    return Cti::Database::executeCommand( deleter, CALLSITE, Cti::Database::LogDebug( isDebugLudicrous() ));
}

string CtiTableDynamicPointAlarming::getSQLCoreStatement() const
{
    static const string sql = "SELECT D.pointid, D.alarmcondition, D.categoryid, D.alarmtime, D.action, D.description, "
                                 "D.tags, D.logid, D.soe_tag, D.type, D.username "
                              "FROM DynamicPointAlarming D";

    return sql;
}

void CtiTableDynamicPointAlarming::DecodeDatabaseReader(Cti::RowReader& rdr)
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

CtiTime CtiTableDynamicPointAlarming::getAlarmTime() const
{
    return _alarmTime;
}

CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmTime(const CtiTime &newTime)
{
    _alarmTime = newTime;
    return *this;
}

CtiTime CtiTableDynamicPointAlarming::getAlarmDBTime() const
{
    return _alarmTime;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmDBTime(const CtiTime &newTime)
{
    _alarmTime = newTime;
    return *this;
}

string CtiTableDynamicPointAlarming::getAction() const
{
    return _action;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAction(const string &str)
{
    _action = formatStringInput( str, DEFAULT_ACTIONLENGTH );
    return *this;
}

string CtiTableDynamicPointAlarming::getDescription() const
{
    return _description;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setDescription(const string &str)
{
    _description = formatStringInput( str, DEFAULT_DESCRIPTIONLENGTH );
    return *this;
}

UINT CtiTableDynamicPointAlarming::getTags() const
{
    return _tags;
}
UINT CtiTableDynamicPointAlarming::setTags(UINT tags)
{
    setDirty(TRUE);
    _tags |= tags;
    return _tags;
}
UINT CtiTableDynamicPointAlarming::resetTags(UINT mask)
{
    setDirty(TRUE);
    _tags &= ~mask;
    return _tags;
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

string CtiTableDynamicPointAlarming::getUser() const
{
    return _user;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setUser(const string &str)
{
    _user = formatStringInput( str, DEFAULT_USERLENGTH );
    return *this;
}


std::string CtiTableDynamicPointAlarming::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableDynamicPointAlarming";
    itemList.add("getPointID()")        << getPointID();
    itemList.add("getAlarmCondition()") << getAlarmCondition();
    itemList.add("getCategoryID()")     << getCategoryID();
    itemList.add("getAlarmDBTime()")    << getAlarmDBTime();
    itemList.add("getAction()")         << getAction();
    itemList.add("getDescription()")    << getDescription();
    itemList.add("getTags()")           << CtiNumStr(getTags()).xhex().zpad(8);
    itemList.add("getLogID()")          << getLogID();
    itemList.add("getSOE()")            << getSOE();
    itemList.add("getLogType()")        << getLogType();
    itemList.add("getUser()")           << getUser();

    return itemList.toString();
}

void CtiTableDynamicPointAlarming::fillRowWriter(Cti::RowWriter& writer) const
{
    writer
        << getPointID()
        << getAlarmCondition()
        << getCategoryID()
        << getAlarmDBTime()
        << getAction()
        << getDescription()
        << getTags()
        << getLogID()
        << getSOE()
        << getLogType()
        << getUser();
}

std::array<Cti::Database::ColumnDefinition, 11> CtiTableDynamicPointAlarming::getTempTableSchema()
{
    return
    {
        Cti::Database::ColumnDefinition
            { "PointID",        "numeric",      "NUMBER"        },
            { "AlarmCondition", "numeric",      "NUMBER"        },
            { "CategoryID",     "numeric",      "NUMBER"        },
            { "AlarmTime",      "datetime",     "DATE"          },
            { "Action",         "varchar(60)",  "VARCHAR2(60)"  },
            { "Description",    "varchar(120)", "VARCHAR2(120)" },
            { "Tags",           "numeric",      "NUMBER"        },
            { "LogID",          "numeric",      "NUMBER"        },
            { "SOE_TAG",        "numeric",      "NUMBER"        },
            { "Type",           "numeric",      "NUMBER"        },
            { "UserName",       "varchar(64)",  "VARCHAR2(64)"  }
    };
}

std::string CtiTableDynamicPointAlarming::formatStringInput( const std::string & input, const std::size_t maxLength )
{
    if ( input.empty() )
    {
        return "(none)";
    }
    if ( input.length() > maxLength )
    {
        return boost::erase_tail_copy( input, -maxLength );
    }

    return input;
}

