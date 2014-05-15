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
    connection = getNewConnection();
    if( connection != NULL )
    {
        connection->setAutoCommit(SA_AutoCommitOn);
    }
}

DatabaseConnection::~DatabaseConnection()
{
    releaseDBConnection(connection);
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** DB CONNECTION EXCEPTION " << (string)x.ErrText() << " Class "
             << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << endl;
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** DB CONNECTION EXCEPTION " << (string)x.ErrText() << " Class "
             << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << endl;
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** DB CONNECTION EXCEPTION " << (string)x.ErrText() << " Class "
             << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << endl;
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

typedef void (*ThrowDBExceptionFunc) (const SAException &);
typedef std::map<int, ThrowDBExceptionFunc> VendorErrorMap;

const VendorErrorMap sqlServerErrors = boost::assign::map_list_of
    ( 547, &throwForeignKeyViolationException)
    (2627, &throwPrimaryKeyViolationException);

const VendorErrorMap oracleErrors = boost::assign::map_list_of
    (2291, &throwForeignKeyViolationException)
    (   1, &throwPrimaryKeyViolationException);

} // anonymous namespace

void DatabaseConnection::resolveErrorCodeAndThrow(const SAConnection *conn, const SAException &x)
{
    if( ! conn )
    {
        std::string description = "Unexpected SAConnection is Null: " + x.ErrText();
        throw DBException(description);
    }

    boost::optional<ThrowDBExceptionFunc> throwFunc;

    switch( conn->Client() )
    {
        case SA_Oracle_Client:    throwFunc = Cti::mapFind(oracleErrors,    x.ErrNativeCode()); break;
        case SA_SQLServer_Client: throwFunc = Cti::mapFind(sqlServerErrors, x.ErrNativeCode()); break;
    }

    if( ! throwFunc )
    {
        std::string description = "Other Error: " + x.ErrText();
        throw DBException(description);
    }

    (*throwFunc)(x); // throw the exception found
}
