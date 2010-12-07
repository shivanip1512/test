#include "yukon.h"

#include "tbl_commerrhist.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

//CtiTableCommErrorHistory::CtiTableCommErrorHistory() {}

CtiTableCommErrorHistory::CtiTableCommErrorHistory(LONG paoid, const CtiTime& datetime,
                                                   LONG soe, LONG type, LONG number,
                                                   const string& cmd,
                                                   const string& out,
                                                   const string& in, LONG ceid) :
_commErrorID(ceid), _paoID(paoid), _dateTime(datetime), _soeTag(soe), _errorType(type),
_errorNumber(number), _command(cmd), _outMessage(out), _inMessage(in)
{
}

CtiTableCommErrorHistory::CtiTableCommErrorHistory(const CtiTableCommErrorHistory& aRef)
{
    *this = aRef;
}

CtiTableCommErrorHistory::~CtiTableCommErrorHistory()
{
}

bool CtiTableCommErrorHistory::operator<(const CtiTableCommErrorHistory& aRef) const
{
    return(getCommErrorID() < aRef.getCommErrorID());
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::operator=(const CtiTableCommErrorHistory &aRef)
{
    if(this != &aRef)
    {
        _commErrorID   = aRef.getCommErrorID();
        _paoID         = aRef.getPAOID();
        _dateTime      = aRef.getDateTime();
        _soeTag        = aRef.getSoeTag();
        _errorType     = aRef.getErrorType();
        _errorNumber   = aRef.getErrorNumber();
        _command       = aRef.getCommand();
        _outMessage    = aRef.getOutMessage();
        _inMessage     = aRef.getInMessage();
    }
    return *this;
}

LONG CtiTableCommErrorHistory::getCommErrorID() const
{

    return _commErrorID;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setCommErrorID( const LONG ce )
{

    _commErrorID = ce;
    return *this;
}

LONG CtiTableCommErrorHistory::getPAOID() const
{

    return _paoID;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setPAOID( const LONG pao )
{

    _paoID = pao;
    return *this;
}

const CtiTime& CtiTableCommErrorHistory::getDateTime() const
{

    return _dateTime;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setDateTime( const CtiTime& dt )
{

    _dateTime = dt;
    return *this;
}

LONG CtiTableCommErrorHistory::getSoeTag() const
{

    return _soeTag;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setSoeTag( const LONG st )
{

    _soeTag = st;
    return *this;
}

LONG CtiTableCommErrorHistory::getErrorType() const
{

    return _errorType;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setErrorType( const LONG et )
{

    _errorType = et;
    return *this;
}

LONG CtiTableCommErrorHistory::getErrorNumber() const
{

    return _errorNumber;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setErrorNumber( const LONG en )
{

    _errorNumber = en;
    return *this;
}

const string& CtiTableCommErrorHistory::getCommand() const
{
    return _command;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setCommand( const string &str )
{

    _command = str;
    return *this;
}

const string& CtiTableCommErrorHistory::getOutMessage() const
{
    return _outMessage;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setOutMessage( const string &str )
{
    _outMessage = str;
    return *this;
}

const string& CtiTableCommErrorHistory::getInMessage() const
{
    return _inMessage;
}

CtiTableCommErrorHistory& CtiTableCommErrorHistory::setInMessage( const string &str )
{

    _inMessage = str;
    return *this;
}

string CtiTableCommErrorHistory::getTableName()
{
    return "CommErrorHistory";
}

void CtiTableCommErrorHistory::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"]          >> _paoID;
    rdr["commerrorid"]         >> _commErrorID;
    rdr["datetime"]            >> _dateTime;
    rdr["soe_tag"]             >> _soeTag;
    rdr["errortype"]           >> _errorType;
    rdr["errornumber"]         >> _errorNumber;
    rdr["command"]             >> _command;
    rdr["outmessage"]          >> _outMessage;
    rdr["inmessage"]           >> _inMessage;
}

bool CtiTableCommErrorHistory::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    return Insert(conn);
}

bool CtiTableCommErrorHistory::Insert(Cti::Database::DatabaseConnection &conn)
{
    if(getCommand().length() > MAX_COMMAND_LENGTH)
    {
        _command.resize(MAX_COMMAND_LENGTH-1);
    }

    if(getOutMessage().length() > MAX_OUTMESS_LENGTH)
    {
        _outMessage.resize(MAX_OUTMESS_LENGTH - 1);
    }

    if(getInMessage().length() > MAX_INMESS_LENGTH)
    {
        _inMessage.resize(MAX_INMESS_LENGTH - 1);
    }

    if(getCommand().empty()) setCommand("none");
    if(getOutMessage().empty()) setOutMessage("none");
    if(getInMessage().empty()) setInMessage("none");

    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter 
        << getCommErrorID()
        << getPAOID()
        << getDateTime()
        << getSoeTag()
        << getErrorType()
        << getErrorNumber()
        << getCommand()
        << getOutMessage()
        << getInMessage();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Unable to insert Comm Error History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << CtiTime() << inserter.asString() << endl;
    }

    return success;
}

bool CtiTableCommErrorHistory::Prune(CtiDate &earliestDate)
{
    static const std::string sql = "delete from " + getTableName() + " where datetime < ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << CtiTime(earliestDate);

    return deleter.execute();
}
