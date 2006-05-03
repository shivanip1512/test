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
#include "yukon.h"

#include <windows.h>
#include <set>
#include "ctidbgmem.h"      // defines CTIDBG_new for memory tracking!
#include "types.h"
#include "dlldefs.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"

using namespace std;

DLLEXPORT CtiSemaphore  gDBAccessSema(gMaxDBConnectionCount, gMaxDBConnectionCount);

typedef set< long > ignoreCol;
static ignoreCol dbIgnores;     // Vendor Error numbers that should be ignored for Oracle, or SQLServer.

// Bookkeeping information about a database connection
struct DBInfo
{
    string dll;
    string name;
    string user;
    string password;
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
                    dout << CtiTime() << " **** ANARCHY **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            // delete db_info[i]->db;
            // db_info[i]->db = 0;
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
                       const string& dbDll, const string& dbName,
                       const string& dbUser, const string& dbPassword )
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
                dout << CtiTime() << " Invalidating all pending connections to " << info->name << " as " << info->user << " with " << info->dll << endl;
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
                    dout << CtiTime() << " **** EXCEPTION Invalidating database.  Nonfatal. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            *db = RWDBManager::database( info->dll.c_str(),info->name.c_str(), info->user.c_str(), info->password.c_str(), "" );

            if( db->isValid() )
            {
                db->defaultConnections( gMaxDBConnectionCount );
                reconnect = false;

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() <<    " Connected to "    << info->name <<
                " as "              << info->user <<
                " with "            << info->dll << endl;
            }
            else
            {
                Sleep(10000);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Failed to connect to database" << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Creating new database connection.  Nonfatal if non-repetitive. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
RWDBReader ExecuteQuery(RWDBConnection& conn, const string& query)
{
    RWDBResult results = conn.executeSql(query.c_str());

    if(!results.isValid())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** RWDB Error #" << results.status().errorCode() << " in query:" << endl;
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
    ignoreCol::iterator itr;

    if( dbIgnores.end() != (itr = dbIgnores.find(aStatus.vendorError1())) )
    {
        // These errors have been specified as ignores.
        return;
    }

    switch(aStatus.vendorError1())
    {
    case 547:           // This is a foreign key violation for sqlserver.
    case 2627:          // This is a primary key violation for sqlserver.
    case 5701:          // This is a changed database context for sqlserver.
        return;
        break;

    default:
        {
            CtiTime Now;
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

/*
 *  rowsAffected can be used to detect an Update of a non-existent row... It will be zero if no update occurred.
 */
DLLEXPORT
RWDBStatus::ErrorCode ExecuteUpdater(RWDBConnection& conn, RWDBUpdater &updater, const char *file, int line, long *rowsAffected)
{
    RWDBResult myResult = updater.execute( conn );
    RWDBStatus stat = myResult.status();
    RWDBStatus::ErrorCode ec = stat.errorCode();

    if(rowsAffected)                                        // Ha.  The calls to table and rowCount ALTER stat!!!!
    {                                                       // This may make the usage of the return VERY suspect!
        RWDBTable myTable = myResult.table();
        *rowsAffected = myResult.rowCount();
    }

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** Checkpoint **** ";
        if(file != 0) dout << file << " (" << line << ")";
        dout << endl << updater.asString() << endl;
    }

    if( ec != RWDBStatus::ok )
    {
        CtiTime Now;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Error Code = " << stat.errorCode() << ". ";
        if(file != 0) dout << file << " (" << line << ")";
        dout << endl << endl << updater.asString() << endl << endl;


        if(stat.vendorError1() != 0)
        {
            dout << Now << " Thread id:        " << rwThreadId() << endl
            << Now << " Error code:       " << (int) stat.errorCode() << endl
            << Now << " Error message     " << stat.message() << endl
            << Now << " Is terminal:      " << (stat.isTerminal() ? "Yes" : "No") << endl
            << Now << " Vendor error 1:   " << stat.vendorError1() << endl
            << Now << " Vendor error 2:   " << stat.vendorError2() << endl
            << Now << " Vendor message 1: " << stat.vendorMessage1() << endl
            << Now << " Vendor message 2: " << stat.vendorMessage2() << endl << endl;
            if(rowsAffected) dout << Now << " Rows Updated      " << *rowsAffected << endl;
        }
    }


    return ec;
}

DLLEXPORT
RWDBStatus ExecuteInserter(RWDBConnection& conn, RWDBInserter &inserter, const char *file, int line)
{
    RWDBStatus stat = inserter.execute( conn ).status();

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << CtiTime() << " **** Checkpoint **** ";
        if(file != 0) dout << file << " (" << line << ")";
        dout << endl << inserter.asString() << endl;
    }

    if( stat.errorCode() != RWDBStatus::ok )
    {
        CtiTime Now;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << Now << " Error Code = " << stat.errorCode() << ". ";

        if(file != 0) dout << file << " (" << line << ")";
        dout << endl << endl << inserter.asString() << endl << endl;

        if(stat.vendorError1() != 0)
        {
            dout << Now << " Thread id:        " << rwThreadId() << endl
            << Now << " Error code:       " << (int) stat.errorCode() << endl
            << Now << " Error message     " << stat.message() << endl
            << Now << " Is terminal:      " << (stat.isTerminal() ? "Yes" : "No") << endl
            << Now << " Vendor error 1:   " << stat.vendorError1() << endl
            << Now << " Vendor error 2:   " << stat.vendorError2() << endl
            << Now << " Vendor message 1: " << stat.vendorMessage1() << endl
            << Now << " Vendor message 2: " << stat.vendorMessage2() << endl << endl;
        }
    }

    return stat;
}


DLLEXPORT int addDBIgnore(long ignoreError)
{
    dbIgnores.insert(ignoreError);
    return (int)(dbIgnores.size());
}
DLLEXPORT void resetDBIgnore()
{
    dbIgnores.clear();
    return;
}

