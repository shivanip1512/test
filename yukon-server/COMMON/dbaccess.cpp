#include "yukon.h"

#include <set>
#include "ctidbgmem.h"      // defines CTIDBG_new for memory tracking!
#include "types.h"
#include "dlldefs.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"
#include "cparms.h"
#include "ctistring.h"
#include <boost/foreach.hpp>

#include <SQLAPI.h>
#include "database_writer.h"

using namespace std;

typedef set< long > ignoreCol;
static ignoreCol dbIgnores;     // Vendor Error numbers that should be ignored for Oracle, or SQLServer.

struct DBConnectionHolder
{
    SAConnection *connection;
    bool inUse;

    DBConnectionHolder():
        connection(NULL),
        inUse(false)
    {
    }
};

// Bookkeeping information about a database connection
struct NewDBInfoStruct
{
    string serverName;
    string user;
    string password;
    string dll; // currently ora15d/ora12d/msq15d/msq12d...

    std::list<DBConnectionHolder> connectionList;
    
};

static NewDBInfoStruct NewDBInfo;

// This lock serializes access to the database,connection
static RWRecursiveLock<RWMutexLock> DbMutex;

/**
 For a given dbID, set the necessary information to make a connection
 to a database.  After a call to setDatabaseParams with a given dbID
 a call to getDatabase or getConnection will cause a connection to be made.
**/
DLLEXPORT
void setDatabaseParams(const string& dll, const string& name,
                       const string& user, const string& password )
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    NewDBInfo.password = password;
    NewDBInfo.user = user;
    NewDBInfo.serverName = name;
    NewDBInfo.dll = dll;
}

/* 
    assignSQLPlaceholders()
 
    This function replaces the '?' placeholder characters in a SQL query with the ':#' sequence
        that SQLAPI++ expects.
 
    Example:
 
    replaces
 
        "update tableX set lastfreezetime = ?, prevfreezetime = ? where deviceid = ?"
 
    with
 
        "update tableX set lastfreezetime = :1, prevfreezetime = :2 where deviceid = :3"
*/
DLLEXPORT
std::string assignSQLPlaceholders(const std::string &sql)
{
    unsigned            offset = 1;
    std::ostringstream  output;

    for each (char c in sql)
    {
        if ( c == '?' )
        {
            output << ':' << offset++;
        }
        else
        {
            output << c;
        }
    }

    return output.str();
}

// Attempts to create a connection to the database, returns NULL if not successful.
SAConnection* createDBConnection()
{
    SAConnection* connection = new SAConnection();

    try
    {
        CtiString tempStr = NewDBInfo.dll;
        if(tempStr.contains("ora"))
        {
            //connection->setOption("UseAPI") = "OCI7";
            connection->setClient(SA_Oracle_Client);
            tempStr = NewDBInfo.serverName;
        }
        else
        {
            connection->setClient(SA_SQLServer_Client);
            tempStr = NewDBInfo.serverName;
            tempStr += "@";
        }

        connection->Connect(tempStr.c_str(), NewDBInfo.user.c_str(), NewDBInfo.password.c_str());
        return connection;
    }
    catch(...)
    {
        delete connection;
    }

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Database connection unsuccessful " << endl;
    }

    return NULL;
}

DLLEXPORT
SAConnection* getNewConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    BOOST_FOREACH(DBConnectionHolder &connHolder, NewDBInfo.connectionList)
    {
        if(!connHolder.inUse)
        {
            // If !inUse, this is ours. This block must return.
            connHolder.inUse = true;
            if(connHolder.connection != NULL && connHolder.connection->isAlive())
            {
                return connHolder.connection;
            }
            else 
            {
                delete connHolder.connection;
                connHolder.connection = createDBConnection();
                return connHolder.connection;
            }
        }
    }

    // We need a new connection, all of them were in use!

    DBConnectionHolder connHolder;
    connHolder.inUse = true;
    connHolder.connection = createDBConnection();

    if(connHolder.connection != NULL)
    {
        NewDBInfo.connectionList.push_back(connHolder);
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Database connection " << NewDBInfo.connectionList.size() << " created " << endl;
        }
    }

    return connHolder.connection;   
}

DLLEXPORT
void releaseDBConnection(SAConnection *connection)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    BOOST_FOREACH(DBConnectionHolder &connHolder, NewDBInfo.connectionList)
    {
        if(connHolder.connection == connection)
        {
            connHolder.inUse = false;
            return;
        }
    }
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << " **** CHECKPOINT **** Attempted to release a connection that could not be found " << __FILE__ << " " << __LINE__ << endl;
    }
}

DLLEXPORT
bool executeUpdater( Cti::Database::DatabaseWriter &updater )
{
    return updater.execute() && (updater.rowsAffected() > 0);
}

