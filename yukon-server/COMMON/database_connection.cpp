#include "precompiled.h"
#include "database_connection.h"
#include "logger.h"

#include <SQLAPI.h>

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

void DatabaseConnection::beginTransaction()
{
    try
    {
        if( connection->AutoCommit() != SA_AutoCommitOff )
        {
            connection->setAutoCommit(SA_AutoCommitOff);
        }
    }
    catch(SAException &x)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** DB CONNECTION EXCEPTION " << (string)x.ErrText() << " Class "
                 << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << endl;
        }
    }
}

bool DatabaseConnection::commitTransaction()
{
    bool commitSuccess = false;

    try
    {
        connection->Commit();
        commitSuccess = true;
    }
    catch(SAException &x)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** DB CONNECTION EXCEPTION " << (string)x.ErrText() << " Class "
                 << x.ErrClass() << " Pos " << x.ErrPos() << " nativeCode " << x.ErrNativeCode() << endl;
        }
    }

    return commitSuccess;
}
