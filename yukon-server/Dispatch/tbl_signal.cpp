#include "precompiled.h"

#include "tbl_signal.h"
#include "dbaccess.h"
#include "logger.h"
#include "database_writer.h"
#include "database_reader.h"
#include "database_util.h"

#define DEFAULT_ACTIONLENGTH        60
#define DEFAULT_DESCRIPTIONLENGTH   120
#define DEFAULT_USERLENGTH          64

using std::endl;

CtiTableSignal::CtiTableSignal() :
    _logID(0),
    _pointID(0),
    _time((ULONG)0),
    _soe(0),
    _logType(InvalidLogType),
    _logPriority(0)
{
}

CtiTableSignal::CtiTableSignal(LONG                 id,
                               const CtiTime       &tme,
                               INT                  millis,
                               const std::string   &text,
                               const std::string   &addl,
                               INT                  lp,
                               INT                  lt,
                               INT                  soe,
                               const std::string   &user,
                               const INT            lid) :
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

void CtiTableSignal::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    if(getAdditionalInfo().length() >= DEFAULT_ACTIONLENGTH)
    {
        std::string temp = getAdditionalInfo();
        temp.resize(DEFAULT_ACTIONLENGTH - 1);
        setAdditionalInfo(temp);
    }

    if(getText().length() >= DEFAULT_DESCRIPTIONLENGTH)
    {
        std::string temp = getText();
        temp.resize(DEFAULT_DESCRIPTIONLENGTH - 1);
        setText(temp);
    }

    if(getUser().length() >= DEFAULT_USERLENGTH)
    {
        std::string temp = getUser();
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

    Cti::Database::executeCommand( inserter, __FILE__, __LINE__ );
}

void CtiTableSignal::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    Insert(conn);
}

std::string CtiTableSignal::getTableName() const
{
    return std::string("Systemlog");
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

std::string CtiTableSignal::getText() const
{
    return _text;
}

std::string CtiTableSignal::getUser() const
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

std::string CtiTableSignal::getAdditionalInfo() const
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

CtiTableSignal& CtiTableSignal::setMillis(INT millis)
{
    if( millis > 999 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" > 999 - returning % 1000");

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" < 0 - returning 0");

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

CtiTableSignal& CtiTableSignal::setText(const std::string &str)
{
    _text = str;
    return *this;
}

CtiTableSignal& CtiTableSignal::setUser(const std::string &str)
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

CtiTableSignal& CtiTableSignal::setAdditionalInfo(const std::string &str)
{
    _additional = str;
    return *this;
}

void CtiTableSignal::dump() const
{
    Cti::FormattedList itemList;

    itemList.add("Log ID")             << _logID;
    itemList.add("Point ID")           << _pointID;
    itemList.add("Log time")           << _time <<", "<< _millis <<"ms";
    itemList.add("SOE Tag")            << _soe;
    itemList.add("Log Type")           << _logType;
    itemList.add("Log Priority Level") << _logPriority;
    itemList.add("AdditionalInfo")     << _additional;
    itemList.add("Text")               << _text;
    itemList.add("User Name")          << _user;

    CTILOG_INFO(dout, itemList);
}

CtiTableSignal* CtiTableSignal::replicate() const
{
    return(CTIDBG_new CtiTableSignal(*this));
}
