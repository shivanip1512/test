/*-----------------------------------------------------------------------------
    Filename:
         dbaccess.cpp

    Programmer:
         Aaron Lauinger

    Description:
         Provides access to rw database and connection objects.

    Initial Date:  1/11/99
    07/07/1999    CGP   Carved this module down to a more CGP managable unit.

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )


#include <windows.h>
#include "ctidbgmem.h"      // defines CTIDBG_new for memory tracking!
#include "types.h"
#include "dlldefs.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"



DLLEXPORT CtiSemaphore  gDBAccessSema(gMaxDBConnectionCount, gMaxDBConnectionCount);

// Bookkeeping information about a database connection
struct DBInfo
{
    RWCString dll;
    RWCString name;
    RWCString user;
    RWCString password;
    RWDBDatabase*  db;
    bool reconnect;
    void (*error_handler) (const RWDBStatus&);
};

// Each RWDBDatabase requires its own error handler
void dbErrorHandler(const RWDBStatus& aStatus, DBInfo* dbInfo);
void dbErrorHandler1( const RWDBStatus& aStatus);
void dbErrorHandler2( const RWDBStatus& aStatus);

// This lock serializes access to the database,connection
static RWRecursiveLock<RWMutexLock> DbMutex;

// Keep information about a max of 2 databases
static const int MAXDBINFO = 2;
static DBInfo* db_info[MAXDBINFO] = { NULL, NULL};


DLLEXPORT void testAnarchyOnDB()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    for(int i = 0; i < MAXDBINFO; i++)
    {
        if(db_info[i] != NULL)
        {
            if(db_info[i]->db != NULL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** ANARCHY **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                delete db_info[i]->db;
                db_info[i]->db = (RWDBDatabase*)1;                 // Thats a nice pointer!
                db_info[i]->reconnect = true;
            }
        }
    }
}

DLLEXPORT void cleanupDB()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    for(int i = 0; i < MAXDBINFO; i++)
    {
        if(db_info[i] != NULL)
        {
            delete db_info[i];
            db_info[i] = NULL;
        }
    }
}

/**
 For a given dbID, set the necessary information to make a connection
 to a database.  After a call to setDatabaseParams with a given dbID
 a call to getDatabase or getConnection will cause a connection to be made.
**/
DLLEXPORT
void setDatabaseParams(unsigned dbID,
                       const RWCString& dbDll, const RWCString& dbName,
                       const RWCString& dbUser, const RWCString& dbPassword )
{
    assert( dbID == 0 || dbID == 1 );

    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    DBInfo* info;
    if( (info = db_info[dbID]) == NULL )
    {
        db_info[dbID] = info = CTIDBG_new DBInfo;
        info->db = NULL;
    }

    info->dll = dbDll;
    info->name = dbName;
    info->user = dbUser;
    info->password = dbPassword;

    if(info->db != NULL)
    {
        RWDBConnection conn = info->db->connection();
        conn.close();

        delete info->db;
    }

    info->db = NULL;
    info->reconnect = true;

    switch(dbID)
    {
    case 0:
        info->error_handler = dbErrorHandler1;
        break;
    case 1:
        info->error_handler = dbErrorHandler2;
        break;
    default: //oh crap
        assert( 0 );
    }
}

DLLEXPORT
RWDBDatabase getDatabase()
{
    return getDatabase(0);
}

/**
 Returns a RWDBDatabase for the given dbID
 setDatabaseParams must have been called before the first call
 to getDatabase for a given dbID.
**/
DLLEXPORT
RWDBDatabase getDatabase(unsigned dbID)
{
    assert( dbID == 0 || dbID == 1 );

    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    DBInfo* info = db_info[dbID];

    assert( info != NULL );

    RWDBDatabase*& db = info->db;
    bool& reconnect = info->reconnect;

    if( reconnect == true )
    {
        if(db != NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Invalidating all pending connections to " << info->name << " as " << info->user << " with " << info->dll << endl;
            }
            try
            {
                RWDBConnection conn = db->connection();
                conn.close();
                delete db;
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** EXCEPTION Invalidating database.  Nonfatal. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        db = NULL;
    }

    if(db == NULL)
    {
        db = CTIDBG_new RWDBDatabase;
    }

    if( reconnect == true )
    {
        try
        {
            RWDBManager::setErrorHandler(info->error_handler);
            *db = RWDBManager::database( info->dll,info->name, info->user, info->password, "" );

            if( db->isValid() )
            {
                db->defaultConnections( gMaxDBConnectionCount );
                reconnect = false;

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() <<    " Connected to "    << info->name <<
                " as "              << info->user <<
                " with "            << info->dll << endl;
            }
            else
            {
                Sleep(10000);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Failed to connect to database" << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION Creating new database connection.  Nonfatal if non-repetitive. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return *db;
}

DLLEXPORT
RWDBConnection getConnection()
{
    return getConnection(0);
}

DLLEXPORT
RWDBConnection getConnection(unsigned dbID)
{
    RWDBConnection conn;

    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);
    conn = getDatabase(dbID).connection();
    return conn;
}

DLLEXPORT
RWDBReader ExecuteQuery(RWDBConnection& conn, const RWCString& query)
{
    RWDBResult results = conn.executeSql(query);

    if(!results.isValid())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** ERROR **** RWDB Error #" << results.status().errorCode() << " in query:" << endl;
            dout << query << endl << endl;
        }
    }

    RWDBTable resTable = results.table();

    return resTable.reader();
}

/**
 Database error handler.
 Sets the reconnect flag for the database that caused the error.
 Reconnection will occur on the next call to getDatabase(..)
**/
void
dbErrorHandler (const RWDBStatus& aStatus, DBInfo* dbInfo)
{
    switch(aStatus.vendorError1())
    {
    case 547:           // This is a foreign key violation for sqlserver.
    case 2627:          // This is a primary key violation for sqlserver.
    case 5701:          // This is a changed database context for sqlserver.
        return;
        break;

    default:
        {
            RWTime Now;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            // Print out the error.
            dout << Now << " Thread id:        " << rwThreadId() << endl
            << Now << " Error code:       " << (int) aStatus.errorCode() << endl
            << Now << " Error message     " << aStatus.message() << endl
            << Now << " Is terminal:      " << (aStatus.isTerminal() ? "Yes" : "No") << endl
            << Now << " Vendor error 1:   " << aStatus.vendorError1() << endl
            << Now << " Vendor error 2:   " << aStatus.vendorError2() << endl
            << Now << " Vendor message 1: " << aStatus.vendorMessage1() << endl
            << Now << " Vendor message 2: " << aStatus.vendorMessage2() << endl << endl;
        }
    }

    // Force a reconnect
    dbInfo->reconnect = true;
}

void
dbErrorHandler1 (const RWDBStatus& aStatus)
{
    dbErrorHandler(aStatus, db_info[0]);
}

void
dbErrorHandler2 (const RWDBStatus& aStatus)
{
    dbErrorHandler(aStatus, db_info[1]);
}
