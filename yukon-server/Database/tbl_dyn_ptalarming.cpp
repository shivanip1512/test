

#include "yukon.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_ptalarming.h"
#include "database_reader.h"
#include "database_writer.h"

#define DEFAULT_ACTIONLENGTH        60
#define DEFAULT_DESCRIPTIONLENGTH   120
#define DEFAULT_USERLENGTH          64


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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

int CtiTableDynamicPointAlarming::operator==(const CtiTableDynamicPointAlarming &right) const
{
    return( getPointID() == right.getPointID() && getAlarmCondition() == right.getAlarmCondition() );
}

string CtiTableDynamicPointAlarming::getTableName()
{
    return string("DynamicPointAlarming");
}


bool CtiTableDynamicPointAlarming::Insert(Cti::Database::DatabaseConnection &conn)
{
    if(getAction().empty())
    {
        setAction("(none)");
    }
    if(getDescription().empty())
    {
        setDescription("(none)");
    }
    if(getUser().empty())
    {
        setUser("(none)");
    }

    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

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

    if(isDebugLudicrous())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }

    bool success = inserter.execute();

    if( ! success )     // Error occured!
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

bool CtiTableDynamicPointAlarming::Update(Cti::Database::DatabaseConnection &conn)
{
    if(getAction().empty())
    {
        setAction("(none)");
    }
    if(getDescription().empty())
    {
        setDescription("(none)");
    }
    if(getUser().empty())
    {
        setUser("(none)");
    }

    if(getAction().length() >= DEFAULT_ACTIONLENGTH)
    {
        string temp = getAction();
        temp.resize(DEFAULT_ACTIONLENGTH - 1);
        setAction(temp);
    }

    if(getDescription().length() >= DEFAULT_DESCRIPTIONLENGTH)
    {
        string temp = getDescription();
        temp.resize(DEFAULT_DESCRIPTIONLENGTH - 1);
        setDescription(temp);
    }

    if(getUser().length() >= DEFAULT_USERLENGTH)
    {
        string temp = getUser();
        temp.resize(DEFAULT_USERLENGTH - 1);
        setUser(temp);
    }

    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "categoryid = ?, "
                                        "alarmtime = ?, "
                                        "action = ?, "
                                        "description = ?, "
                                        "tags = ?, "
                                        "logid = ?, "
                                        "soe_tag = ?, "
                                        "type = ?, "
                                        "username = ?"
                                    " where "
                                        "pointid = ? and "
                                        "alarmcondition = ?";

    Cti::Database::DatabaseWriter   updater(conn, sql);

    updater
        << getCategoryID()
        << getAlarmTime()
        << getAction().c_str()
        << getDescription().c_str()
        << getTags()
        << getLogID()
        << getSOE()
        << getLogType()
        << getUser().c_str()
        << getPointID()
        << getAlarmCondition();

    bool success      = updater.execute();
    long rowsAffected = updater.rowsAffected();

    if(isDebugLudicrous())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl;
    }

    if( ! success )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl;
    }

    if( success && rowsAffected > 0)
    {
        setDirty(false);
    }
    else
    {
        success = Insert(conn);        // Try a vanilla insert if the update failed!
    }

    return success;
}

bool CtiTableDynamicPointAlarming::Delete(long pointid, int alarm_condition)
{
    static const std::string sql = "delete from " + getTableName() + " where pointid = ? and alarmcondition = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter 
        << pointid
        << alarm_condition;

    if(isDebugLudicrous())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << CtiTime() << deleter.asString() << endl;
    }

    return deleter.execute();
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

CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmTime(const CtiTime &rwt)
{
    _alarmTime = CtiTime( rwt );
    return *this;
}

CtiTime CtiTableDynamicPointAlarming::getAlarmDBTime() const
{
    return _alarmTime;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAlarmDBTime(const CtiTime &rwt)
{
    _alarmTime = rwt;
    return *this;
}

string CtiTableDynamicPointAlarming::getAction() const
{
    return _action;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setAction(const string &str)
{
    _action = str;
    return *this;
}

string CtiTableDynamicPointAlarming::getDescription() const
{
    return _description;
}
CtiTableDynamicPointAlarming& CtiTableDynamicPointAlarming::setDescription(const string &str)
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
        dout << "getAlarmDBTime()    " << getAlarmDBTime() << endl;
        dout << "getAction()         " << getAction() << endl;
        dout << "getDescription()    " << getDescription() << endl;
        dout << "getTags()            0x" << CtiNumStr(getTags()).xhex().zpad(8).toString() << endl;
        dout << "getLogID()          " << getLogID() << endl;
        dout << "getSOE()            " << getSOE() << endl;
        dout << "getLogType()        " << getLogType() << endl;
        dout << "getUser()           " << getUser() << endl;

    }
}
