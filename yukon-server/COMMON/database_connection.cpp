#include "precompiled.h"
#include "database_connection.h"
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

//  foreign key insert error:
//  oracle = ORA-02291
//  sqlsvr = 547 - The %ls statement conflicted with the %ls constraint "%.*ls". The conflict occurred in database "%.*ls", table "%.*ls"%ls%.*ls%ls.

//  unique key constraint error:
//  oracle = ORA-00001
//  sqlsvr = 2627 - Violation of %ls constraint '%.*ls'. Cannot insert duplicate key in object '%.*ls'.

typedef std::map<int, const ErrorCodes *> VendorErrorMap;

const VendorErrorMap sqlServerErrors = boost::assign::map_list_of
    ( 547, &ErrorCodes::ErrorCode_ForeignKeyViolated)
    (2627, &ErrorCodes::ErrorCode_PrimaryKeyViolated);

const VendorErrorMap oracleErrors = boost::assign::map_list_of
    (2291, &ErrorCodes::ErrorCode_ForeignKeyViolated)
    (   1, &ErrorCodes::ErrorCode_PrimaryKeyViolated);

const ErrorCodes *DatabaseConnection::resolveErrorCode(const SAConnection *conn, const SAException &x)
{
    boost::optional<const ErrorCodes *> ec;

    if( conn )
    {
        switch( conn->Client() )
        {
            case SA_Oracle_Client:       ec = Cti::mapFind(oracleErrors,    x.ErrNativeCode());  break;
            case SA_SQLServer_Client:    ec = Cti::mapFind(sqlServerErrors, x.ErrNativeCode());  break;
        }
    }

    if( ! ec || ! *ec )
    {
        return &ErrorCodes::ErrorCode_Other;
    }

    return *ec;
}

ErrorCodes::ErrorCodes(const std::string name_) : name(name_) {}

const ErrorCodes ErrorCodes::ErrorCode_ForeignKeyViolated("Foreign key violated");
const ErrorCodes ErrorCodes::ErrorCode_PrimaryKeyViolated("Primary key violated");
const ErrorCodes ErrorCodes::ErrorCode_Other             ("Other");

