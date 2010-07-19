#include "yukon.h"

#include "tbl_signal.h"
#include "dbaccess.h"
#include "logger.h"
#include "database_writer.h"
#include "database_reader.h"

#define DEFAULT_ACTIONLENGTH        60
#define DEFAULT_DESCRIPTIONLENGTH   120
#define DEFAULT_USERLENGTH          64

CtiTableSignal::CtiTableSignal() :
    _logID(0),
    _pointID(0),
    _time((ULONG)0),
    _soe(0),
    _logType(InvalidLogType),
    _logPriority(0)
{
}

CtiTableSignal::CtiTableSignal(LONG            id,
                               const CtiTime  &tme,
                               INT             millis,
                               const string   &text,
                               const string   &addl,
                               INT             lp,
                               INT             lt,
                               INT             soe,
                               const string   &user,
                               const INT       lid) :
    _logID(lid),
    _pointID(id),
    _time(tme),
    _millis(millis),
    _soe(soe),
    _logType(lt),
    _logPriority(lp),
    _text(text),
    _additional(addl),
    _user(user)
{
    if(!_logID) _logID = SystemLogIdGen();
}

CtiTableSignal::CtiTableSignal(const CtiTableSignal& aRef)
{
    *this = aRef;
}

unsigned long CtiTableSignal::operator()(const CtiTableSignal& aRef) const
{
    return _logID;
}

BOOL CtiTableSignal::operator==(const CtiTableSignal& right) const
{
    return(_logID == right.getLogID());
}


CtiTableSignal::~CtiTableSignal()
{

}

bool CtiTableSignal::operator<(const CtiTableSignal& aRef) const
{
    return _logID < aRef.getLogID();
}

CtiTableSignal& CtiTableSignal::operator=(const CtiTableSignal& aRef)
{
    if(this != &aRef)
    {
        _logID        = aRef.getLogID();
        _pointID      = aRef.getPointID();
        _time         = aRef.getTime();
        _millis       = aRef.getMillis();
        _soe          = aRef.getSOE();
        _logType      = aRef.getLogType();
        _logPriority  = aRef.getPriority();
        _additional   = aRef.getAdditionalInfo();
        _text         = aRef.getText();
        _user         = aRef.getUser();
    }
    return *this;
}

void CtiTableSignal::DecodeDatabaseReader( Cti::RowReader& rdr )
{
    rdr["logid"]        >> _logID;
    rdr["pointid"]      >> _pointID;
    rdr["datetime"]     >> _time;
    rdr["soe_tag"]      >> _soe;
    rdr["type"]         >> _logType;
    rdr["priority"]     >> _logPriority;
    rdr["action"]       >> _additional;
    rdr["description"]  >> _text;
    rdr["username"]     >> _user;
    rdr["millis"]       >> _millis;
}

void CtiTableSignal::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    if(getAdditionalInfo().length() >= DEFAULT_ACTIONLENGTH)
    {
        string temp = getAdditionalInfo();
        temp.resize(DEFAULT_ACTIONLENGTH - 1);
        setAdditionalInfo(temp);
    }

    if(getText().length() >= DEFAULT_DESCRIPTIONLENGTH)
    {
        string temp = getText();
        temp.resize(DEFAULT_DESCRIPTIONLENGTH - 1);
        setText(temp);
    }

    if(getUser().length() >= DEFAULT_USERLENGTH)
    {
        string temp = getUser();
        temp.resize(DEFAULT_USERLENGTH - 1);
        setUser(temp);
    }

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
        << getLogID()
        << getPointID()
        << getTime()
        << getSOE()
        << getLogType()
        << getPriority()
        << getAdditionalInfo()
        << getText()
        << getUser()
        << getMillis();

    bool success = inserter.execute();

    if( !success )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << " **** SQL Insert Error." << endl;
        dout << inserter.asString() << endl;
    }
}

void CtiTableSignal::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    Insert(conn);
}

void CtiTableSignal::Restore()
{
    static const string sql =  "SELECT SL.logid, SL.pointid, SL.datetime, SL.soe_tag, SL.type, SL.priority, "
                                 "SL.action, SL.description, SL.username, SL.millis "
                               "FROM Systemlog SL "
                               "WHERE SL.logid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getLogID();

    reader.execute();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( true );
    }
}

void CtiTableSignal::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "pointid = ?, "
                                        "datetime = ?, "
                                        "soe_tag = ?, "
                                        "type = ?, "
                                        "priority = ?, "
                                        "action = ?, "
                                        "description = ?, "
                                        "username = ?, "
                                        "millis = ?"
                                   " where "
                                        "logid = ?";

    if(getAdditionalInfo().length() >= DEFAULT_ACTIONLENGTH)
    {
        getAdditionalInfo().resize(DEFAULT_ACTIONLENGTH - 1);
    }

    if(getText().length() >= DEFAULT_DESCRIPTIONLENGTH)
    {
        getText().resize(DEFAULT_DESCRIPTIONLENGTH - 1);
    }

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getPointID()
        << getTime()
        << getSOE()
        << getLogType()
        << getPriority()
        << getAdditionalInfo()
        << getText()
        << getUser()
        << getMillis()
        << getLogID();

    updater.execute();
}

void CtiTableSignal::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where logid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getLogID();

    deleter.execute();
}



string CtiTableSignal::getTableName() const
{
    return string("Systemlog");
}

LONG CtiTableSignal::getLogID() const
{
    return _logID;
}

LONG CtiTableSignal::getPointID() const
{
    return _pointID;
}

CtiTime CtiTableSignal::getTime() const
{
    return _time;
}

INT CtiTableSignal::getMillis() const
{
    return _millis;
}

INT CtiTableSignal::getPriority() const
{
    return _logPriority;
}

string CtiTableSignal::getText() const
{
    return _text;
}

string CtiTableSignal::getUser() const
{
    return _user;
}

INT CtiTableSignal::getSOE() const
{
    return _soe;
}

INT CtiTableSignal::getLogType() const
{
    return _logType;
}

string CtiTableSignal::getAdditionalInfo() const
{
    return _additional;
}

CtiTableSignal& CtiTableSignal::setLogID(LONG id)
{
    _logID = id;
    return *this;
}

CtiTableSignal& CtiTableSignal::setPointID(LONG id)
{
    _pointID = id;
    return *this;
}

CtiTableSignal& CtiTableSignal::setTime(const CtiTime rwt)
{
    _time = rwt;
    return *this;
}

CtiTableSignal& CtiTableSignal::setMillis(INT millis)
{
    if( millis > 999 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis = 0;
    }

    _millis = millis;

    return *this;
}

CtiTableSignal& CtiTableSignal::setPriority(INT cls)
{
    _logPriority = cls;
    return *this;
}

CtiTableSignal& CtiTableSignal::setText(const string &str)
{
    _text = str;
    return *this;
}

CtiTableSignal& CtiTableSignal::setUser(const string &str)
{
    _user = str;
    return *this;
}



CtiTableSignal& CtiTableSignal::setSOE(const INT &i)
{
    _soe = i;
    return *this;
}

CtiTableSignal& CtiTableSignal::setLogType(const INT &i)
{
    _logType = i;
    return *this;
}

CtiTableSignal& CtiTableSignal::setAdditionalInfo(const string &str)
{
    _additional = str;
    return *this;
}

void CtiTableSignal::dump() const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << endl;
    dout << " Log ID                        " << _logID << endl;
    dout << " Point ID                      " << _pointID << endl;
    dout << " Log time                      " << _time << ", " << _millis << "ms" << endl;
    dout << " SOE Tag                       " << _soe << endl;
    dout << " Log Type                      " << _logType << endl;
    dout << " Log Priority Level            " << _logPriority << endl;
    dout << " AdditionalInfo                " << _additional << endl;
    dout << " Text                          " << _text << endl;
    dout << " User Name                     " << _user << endl;
}

CtiTableSignal* CtiTableSignal::replicate() const
{
    return(CTIDBG_new CtiTableSignal(*this));
}
