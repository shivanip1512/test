#include "precompiled.h"
#include "database_reader.h"
#include "logger.h"
#include "CParms.h"

using namespace Cti::Database;
using Cti::RowReader;
using std::endl;
using std::string;

DatabaseReader::DatabaseReader(Cti::Database::DatabaseConnection &conn, const std::string &command) :
    _currentIndex(1),
    _isValid(false),
    _command(conn),
    _executeCalled(false)
{
    if( command != "" )
    {
        setCommandText(command);
    }
}

DatabaseReader::~DatabaseReader()
{
}

bool DatabaseReader::setCommandText(const std::string &command)
{
    _executeCalled = false;
    try
    {
        _command.setCommandText( assignSQLPlaceholders(command).c_str() );
        return true;
    }
    catch(SAException &x)
    {
        _isValid = false;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** DB EXCEPTION " << (string)x.ErrText() << " Class "
                 << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << " in query: " << endl;
            dout << asString() << " " << __FILE__ << " " << __LINE__ << endl;
        }
    }

    return false;
}

bool DatabaseReader::isValid()
{
    if( !_executeCalled )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** EXECUTE NOT CALLED BEFORE CHECKING VALIDITY!!!! " << endl;
    }
    return _isValid;
}


bool DatabaseReader::executeWithRetries()
{
    unsigned long waitTimer = gConfigParms.getValueAsULong("YUKON_RETRY_SQL_WAIT_MS", 500);
    unsigned long retries   = gConfigParms.getValueAsULong("YUKON_SQL_RETRY_COUNT", 3);

    while( retries > 0)
    {
        try
        {
            executeCommand();
            return true;
        }        
        catch(SAException &x)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ** Attempt failed for SQL query, due to native code Error: " << x.ErrNativeCode() <<". Retrying." << endl;
        }
        retries--;
        Sleep(waitTimer);
    };

    return execute(); 

}

void DatabaseReader::executeCommand()
{
    _executeCalled = true;
    try
    {
        _command.Execute();
        _isValid = true;
    }
    catch(SAException &x)
    {
        _isValid = false;
        throw x;
    }
}

bool DatabaseReader::execute()
{
    try
    {
        executeCommand();
        return true;
    }
    catch(SAException &x)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** DB EXCEPTION " << (string)x.ErrText() << " Class "
             << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << " in query: " << endl;
        dout << asString() << " " << __FILE__ << " " << __LINE__ << endl;
    }

    return false;
}

// Checks if the current index is null.
// For example: rdr["test"].isNull();
bool DatabaseReader::isNull()
{
    return _command[_currentIndex].isNull();
}

bool DatabaseReader::operator()()
{
    if( _isValid )
    {
        try
        {
            _currentIndex = 1;
            return _command.FetchNext();
        }
        catch(SAException &x)
        {
            _isValid = false;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** DB EXCEPTION " << (string)x.ErrText() << " Class "
                 << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << " in query: " << endl;
            dout << asString() << " " << __FILE__ << " " << __LINE__ << endl;
            }
        }
    }

    if( !_executeCalled )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** EXECUTE NOT CALLED BEFORE READING!!!! " << endl;
    }

    return false;
}

RowReader &DatabaseReader::operator[](const char *columnName)
{
    _currentIndex = _command[columnName].Pos();
    return *this;
}

RowReader &DatabaseReader::operator[](const std::string &columnName)
{
    _currentIndex = _command[columnName.c_str()].Pos();
    return *this;
}

RowReader &DatabaseReader::operator[](int columnNumber)
{
    _currentIndex = columnNumber + 1;
    return *this;
}

RowReader &DatabaseReader::operator>>(bool &operand)
{
    operand = _command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(short &operand)
{
    operand = _command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(unsigned short &operand)
{
    operand = _command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(long &operand)
{
    operand = _command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(INT &operand)
{
    operand = (long)_command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(UINT &operand)
{
    operand = (long)_command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(UCHAR &operand)
{
    operand = (long)_command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(unsigned long &operand)
{
    operand = _command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(__int64 &operand)
{
    operand = _command[_currentIndex++].asNumeric();
    return *this;
}

RowReader &DatabaseReader::operator>>(double &operand)
{
    operand = _command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(float &operand)
{
    operand = (double)_command[_currentIndex++];
    return *this;
}

RowReader &DatabaseReader::operator>>(CtiTime &operand)
{
    operand = CtiTime::CtiTime((struct tm*)&_command[_currentIndex++].asDateTime());
    return *this;
}

RowReader &DatabaseReader::operator>>(boost::posix_time::ptime &operand)
{
    operand = boost::posix_time::from_time_t(mktime((struct tm*)&_command[_currentIndex++].asDateTime()));
    return *this;
}

RowReader &DatabaseReader::operator>>(std::string &operand)
{
    operand = _command[_currentIndex++].asString();
    return *this;
}


RowReader &DatabaseReader::extractChars(char *destination, unsigned count)
    {
        SAString s = _command[_currentIndex++].asString();

        if( count )
        {
            const unsigned chars_to_extract = std::min<unsigned>(count - 1, s.GetLength());

            const char *source = s.GetBuffer(chars_to_extract);

            std::copy(source, source + chars_to_extract, destination);

            std::fill(destination + chars_to_extract, destination + count, 0);
        }

        return *this;
    }



RowReader &DatabaseReader::operator<<(const bool operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const short operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const unsigned short operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const long operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const INT operand)
{
    _command << (long)operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const UINT operand)
{
    _command << (unsigned long)operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const unsigned long operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const __int64 operand)
{
    _command << SANumeric(operand);
    return *this;
}

RowReader &DatabaseReader::operator<<(const double operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const float operand)
{
    _command << operand;
    return *this;
}

RowReader &DatabaseReader::operator<<(const CtiTime &operand)
{
    time_t time = operand.seconds();
    struct tm* tm = operand.localtime_r(&time);
    _command << *tm;
    return *this;
}

RowReader &DatabaseReader::operator<<(const boost::posix_time::ptime &operand)
{
    struct tm theTime = to_tm(operand);
    _command << theTime;
    return *this;
}

RowReader &DatabaseReader::operator<<(const std::string &operand)
{
    _command << operand.c_str();
    return *this;
}

RowReader &DatabaseReader::operator<<(const char *operand)
{
    _command << operand;
    return *this;
}

std::string DatabaseReader::asString()
{
    std::string sqlString = _command.CommandText() + " ";

    for( int i = 0; i < _command.ParamCount(); i++ )
    {
        if( _command.ParamByIndex(i).ParamType() != SA_dtCursor )
        {
            sqlString += " <" + CtiNumStr(i) + "> ";
            sqlString += _command.ParamByIndex(i).asString();
        }
    }

    return sqlString;
}
