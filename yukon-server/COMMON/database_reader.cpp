#include "precompiled.h"
#include "database_reader.h"
#include "logger.h"
#include "CParms.h"
#include "database_exceptions.h"

#include <boost/date_time/posix_time/conversion.hpp>

using namespace Cti::Database;
using Cti::RowReader;
using namespace std::string_literals;
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
    catch( const SAException& x )
    {
        _isValid = false;

        CTILOG_EXCEPTION_ERROR(dout, x, "DB Reader setCommandText failed for SQL query: "<< asString());
    }

    return false;
}

bool DatabaseReader::isValid()
{
    if( !_executeCalled )
    {
        CTILOG_ERROR(dout, "EXECUTE NOT CALLED BEFORE CHECKING VALIDITY!!!! ");
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
            executeWithDatabaseException();
            return true;
        }
        catch( const DatabaseException& x )
        {
            retries--;

            if( retries )
            {
                CTILOG_EXCEPTION_WARN(dout, x, "Attempt failed for SQL query: "<< asString() <<". Retrying..");
            }
            else
            {
                CTILOG_EXCEPTION_ERROR(dout, x, "Attempt failed for SQL query: "<< asString());
            }
        }

        Sleep(waitTimer);
    };

    return execute();

}

void DatabaseReader::executeWithDatabaseException()
{
    _executeCalled = true;
    try
    {
        _command.Execute();
        _isValid = true;
    }
    catch( const SAException& x )
    {
        _isValid = false;
        DatabaseConnection::throwDatabaseException(_command.Connection(), x);
    }
}

bool DatabaseReader::execute()
{
    try
    {
        executeWithDatabaseException();
        return true;
    }
    catch( const DatabaseException& x )
    {
        CTILOG_EXCEPTION_ERROR(dout, x, "DB Reader execute command failed for SQL query: "<< asString());
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
        catch( const SAException& x )
        {
            _isValid = false;

            CTILOG_EXCEPTION_ERROR(dout, x, "DB Reader fetch next failed for SQL query: "<< asString());
        }
    }

    if( !_executeCalled )
    {
        CTILOG_ERROR(dout, "EXECUTE NOT CALLED BEFORE READING!!!! ");
    }

    return false;
}

RowReader &DatabaseReader::operator[](const char *columnName)
{
    try
    {
        _currentIndex = _command[columnName].Pos();
    }
    catch( const SAException& x )
    {
        std::string error_text = "Column "s + columnName + " is not present in the result set";

        CTILOG_EXCEPTION_ERROR(dout, x, error_text);

        throw DatabaseException(error_text);
    }

    return *this;
}

RowReader &DatabaseReader::operator[](const std::string &columnName)
{
    return this->operator[](columnName.c_str());
}

RowReader &DatabaseReader::operator[](int columnNumber)
{
    _currentIndex = columnNumber + 1;
    return *this;
}

DatabaseReader::operator bool()
{
    return _command[_currentIndex];
}

DatabaseReader::operator short()
{
    return _command[_currentIndex];
}

DatabaseReader::operator unsigned short()
{
    return _command[_currentIndex];
}

DatabaseReader::operator long()
{
    return _command[_currentIndex];
}

DatabaseReader::operator int()
{
    return (long)_command[_currentIndex];
}

DatabaseReader::operator unsigned()
{
    return (long)_command[_currentIndex];
}

DatabaseReader::operator unsigned char()
{
    return (long)_command[_currentIndex];
}

DatabaseReader::operator unsigned long()
{
    return _command[_currentIndex];
}

DatabaseReader::operator long long()
{
    return _command[_currentIndex].asNumeric();
}

DatabaseReader::operator double()
{
    return _command[_currentIndex];
}

DatabaseReader::operator float()
{
    return (double)_command[_currentIndex];
}

DatabaseReader::operator CtiTime()
{
    tm t = _command[_currentIndex].asDateTime();

    return CtiTime { &t };
}

DatabaseReader::operator boost::posix_time::ptime()
{
    tm t = _command[_currentIndex].asDateTime();

    return boost::posix_time::from_time_t(mktime(&t));
}

DatabaseReader::operator std::string()
{
    return std::string{_command[_currentIndex].asString()};
}

DatabaseReader::operator Bytes()
{
    auto saBytes = _command[_currentIndex].asBytes();
    const auto length = saBytes.GetBinaryLength();
    const auto buf = saBytes.GetBinaryBuffer(length);
    const unsigned char* begin = static_cast<unsigned char*>(buf);
    const unsigned char* end = begin + length;

    return { begin, end };
}

void DatabaseReader::incrementColumnIndex()
{
    ++_currentIndex;
}

void DatabaseReader::setPrefetch()
{
    _command.setOption("PreFetchRows") = "100";
}

RowReader &DatabaseReader::extractChars(char *destination, unsigned count)
{
    SAString s = _command[_currentIndex++].asString();

    auto dest = stdext::make_checked_array_iterator(destination, count);

    if( count )
    {
        const unsigned chars_to_extract = std::min<unsigned>(count - 1, s.GetLength());

        const char *source = s.GetBuffer(chars_to_extract);

        std::copy(source, source + chars_to_extract, dest);

        std::fill(dest + chars_to_extract, dest + count, 0);
    }

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

RowReader &DatabaseReader::operator<<(const long long operand)
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
