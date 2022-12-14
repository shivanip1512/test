
#include "precompiled.h"
#include "database_writer.h"
#include "ctidate.h"
#include "logger.h"
#include "boost_time.h"

using namespace Cti::Database;
using Cti::RowWriter;
using std::endl;
using std::string;

DatabaseWriter::DatabaseWriter(Cti::Database::DatabaseConnection &conn, const std::string &command) :
    _command(conn)
{
    if( command != "" )
    {
        setCommandText(command);
    }
}

DatabaseWriter::~DatabaseWriter()
{
}

RowWriter &DatabaseWriter::setCommandText(const std::string &command)
{
    _command.setCommandText( assignSQLPlaceholders(command).c_str() );

    return *this;
}

bool DatabaseWriter::execute()
{
    bool retVal = true;
    try
    {
        _command.Execute();
    }
    catch(SAException &x)
    {
        retVal = false;

        CTILOG_EXCEPTION_ERROR(dout, x, "DB Writer execute failed for SQL query: "<< asString());
    }
    return retVal;
}

void DatabaseWriter::executeWithDatabaseException()
{
    try
    {
        _command.Execute();
    }
    catch(SAException &x)
    {
        DatabaseConnection::throwDatabaseException(_command.Connection(), x);
    }
}

void DatabaseWriter::reset()
{
    try
    {
        _command << SAPos(1);
    }
    catch(SAException &x)
    {
        DatabaseConnection::throwDatabaseException(_command.Connection(), x);
    }
}

RowWriter &DatabaseWriter::operator<<(const SpecialValues operand)
{
    _command << SANull();
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const bool operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const short operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const unsigned short operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const long operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const INT operand)
{
    _command << (long)operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const UINT operand)
{
    _command << (unsigned long)operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const unsigned long operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const long long operand)
{
    _command << SANumeric(operand);
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const double operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const float operand)
{
    _command << operand;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const CtiTime &operand)
{
    time_t time = operand.seconds();
    struct tm* tm = operand.localtime_r(&time);
    _command << *tm;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const boost::posix_time::ptime &operand)
{
    time_t time = ptime_to_utc_seconds(operand);
    struct tm* tm = CtiTime::localtime_r(&time);
    _command << *tm;
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const std::string &operand)
{
    _command << operand.c_str();
    return *this;
}

RowWriter &DatabaseWriter::operator<<(const char *operand)
{
    _command << operand;
    return *this;
}

std::string DatabaseWriter::asString()
{
    std::string sqlString = _command.CommandText() + " ";
    for( int i = 0; i < _command.ParamCount(); i++ )
    {
        if( _command.ParamByIndex(i).ParamType() != SA_dtCursor )
        {
            sqlString += " <" + CtiNumStr(i + 1) + "> ";
            sqlString += _command.ParamByIndex(i).asString();
        }
    }

    return sqlString;
}

long DatabaseWriter::rowsAffected()
{
    return _command.RowsAffected();
}

