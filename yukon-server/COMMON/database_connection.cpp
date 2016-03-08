#include "precompiled.h"

#include "database_connection.h"
#include "database_exceptions.h"
#include "logger.h"
#include "std_helper.h"

#include <SQLAPI.h>

#include <boost/assign/list_of.hpp>

using namespace Cti::Database;
using std::endl;
using std::string;


DatabaseConnection::DatabaseConnection()
{
    if( connection = getNewConnection() )
    {
        connection->setAutoCommit(SA_AutoCommitOn);
    }
}

DatabaseConnection::DatabaseConnection(QueryTimeout t)
    : DatabaseConnection()
{
    if( connection )
    {
        static const std::map<QueryTimeout, const char *> timeouts {
            { QueryTimeout::Fifteen_seconds, "15" } };

        if( auto timeoutString = mapFind(timeouts, t) )
        {
            connection->setOption("SQL_ATTR_QUERY_TIMEOUT") = *timeoutString;
        }
        else
        {
            CTILOG_ERROR(dout, "Unhandled QueryTimeout value " << static_cast<unsigned>(t) << ", cannot set query timeout");
        }
    }
}

DatabaseConnection::~DatabaseConnection()
{
    if( connection )
    {
        releaseDBConnection(connection);
    }
}

DatabaseConnection::operator SAConnection*()
{
    return connection;
}

bool DatabaseConnection::isValid() const
{
    return ( connection != NULL );
}

void DatabaseConnection::beginTransaction()
{
    try
    {
        connection->setAutoCommit(SA_AutoCommitOff);
    }
    catch(SAException &x)
    {
        CTILOG_EXCEPTION_ERROR(dout, x, "DB Connection begin transaction failed");
    }
}

bool DatabaseConnection::rollbackTransaction()
{
    try
    {
        connection->Rollback();
        connection->setAutoCommit(SA_AutoCommitOn);
        return true;
    }
    catch(SAException &x)
    {
        CTILOG_EXCEPTION_ERROR(dout, x, "DB Connection roll-back transaction failed");
    }
    return false;
}

bool DatabaseConnection::commitTransaction()
{
    try
    {
        connection->Commit();
        connection->setAutoCommit(SA_AutoCommitOn);
        return true;
    }
    catch(SAException &x)
    {
        CTILOG_EXCEPTION_ERROR(dout, x, "DB Connection commit transaction failed");
    }
    return false;
}

namespace {

void throwPrimaryKeyViolationException(const SAException &x)
{
    throw PrimaryKeyViolationException((std::string)x.ErrText());
}

void throwForeignKeyViolationException(const SAException &x)
{
    throw ForeignKeyViolationException((std::string)x.ErrText());
}

//  foreign key insert error:
//  oracle = ORA-02291
//  sqlsvr = 547 - The %ls statement conflicted with the %ls constraint "%.*ls". The conflict occurred in database "%.*ls", table "%.*ls"%ls%.*ls%ls.

//  unique key constraint error:
//  oracle = ORA-00001
//  sqlsvr = 2627 - Violation of %ls constraint '%.*ls'. Cannot insert duplicate key in object '%.*ls'.

typedef boost::function<void (const SAException &)> ThrowDatabaseExceptionFunc;
typedef std::map<int, ThrowDatabaseExceptionFunc> VendorErrorMap;

const VendorErrorMap sqlServerErrors = boost::assign::map_list_of
    ( 547, ThrowDatabaseExceptionFunc(throwForeignKeyViolationException))
    (2627, ThrowDatabaseExceptionFunc(throwPrimaryKeyViolationException));

const VendorErrorMap oracleErrors = boost::assign::map_list_of
    (2291, ThrowDatabaseExceptionFunc(throwForeignKeyViolationException))
    (   1, ThrowDatabaseExceptionFunc(throwPrimaryKeyViolationException));

} // anonymous namespace

void DatabaseConnection::throwDatabaseException(const SAConnection *conn, const SAException &x)
{
    if( ! conn )
    {
        std::string description = "Unexpected SAConnection is Null: " + x.ErrText();
        throw DatabaseException(description);
    }

    boost::optional<ThrowDatabaseExceptionFunc> throwFunc;

    switch( conn->Client() )
    {
        case SA_Oracle_Client:    throwFunc = Cti::mapFind(oracleErrors,    x.ErrNativeCode()); break;
        case SA_SQLServer_Client: throwFunc = Cti::mapFind(sqlServerErrors, x.ErrNativeCode()); break;
    }

    if( ! throwFunc )
    {
        std::string description = "Other Error: " + x.ErrText();
        throw DatabaseException(description);
    }

    (*throwFunc)(x); // throw the exception found
}
