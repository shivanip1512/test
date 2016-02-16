#include "precompiled.h"

#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"

#include "guard.h"
#include "critical_section.h"

#include <boost/algorithm/string/predicate.hpp>
#include <boost/foreach.hpp>

#include <SQLAPI.h>

namespace {

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

std::string dbType;
std::string dbServer;
std::string dbUser;
std::string dbPassword;

std::vector<DBConnectionHolder> connectionList;

// This lock serializes access to the database,connection
CtiCriticalSection DbMutex;

}

/**
 For a given dbID, set the necessary information to make a connection
 to a database.  After a call to setDatabaseParams with a given dbID
 a call to getDatabase or getConnection will cause a connection to be made.
**/
DLLEXPORT
void setDatabaseParams(const std::string& type, const std::string& name,
                       const std::string& user, const std::string& password )
{
    CtiLockGuard<CtiCriticalSection> guard( DbMutex);

    dbType     = type;
    dbServer   = name;
    dbUser     = user;
    dbPassword = password;
}

/**
 * Replaces the '?' placeholder characters in a SQL query with
 *       the ':#' sequence that SQLAPI++ expects.
 *
 * For example,
 *
 *      "update tableX set lastfreezetime = ?, prevfreezetime =
 *      ? where deviceid = ?"
 *
 * will be changed to
 *
 *      "update tableX set lastfreezetime = :1, prevfreezetime =
 *      :2 where deviceid = :3"
 *
 * @param sql the SQL with "?" placeholders
 *
 * @return the SQL with ":#" placeholders
 */
DLLEXPORT
std::string assignSQLPlaceholders(const std::string &sql)
{
    unsigned    offset = 1;
    std::string result;

    unsigned placeholder_chars = std::count(sql.begin(), sql.end(), '?');

    if( placeholder_chars > 9 )
    {
        //  each placeholder after 9 will require 2 chars
        placeholder_chars *= 2;
        placeholder_chars -= 9;
    }

    result.reserve(sql.size() + placeholder_chars);

    std::string::const_iterator sql_itr = sql.begin();

    while( sql_itr != sql.end() )
    {
        //  try to find a placeholder
        std::string::const_iterator placeholder = find(sql_itr, sql.end(), '?');

        result.append(sql_itr, placeholder);

        sql_itr = placeholder;

        //  did we find a placeholder?
        if( placeholder != sql.end() )
        {
            result.push_back(':');

            //  This only writes out 2 digits, so we can only support up to 99 placeholders.
            if( offset > 9 )
            {
                result.push_back('0' + offset / 10);
            }

            result.push_back('0' + offset % 10);

            offset++;

            //  Move past the placeholder character.
            sql_itr++;
        }
    }

    return result;
}

// Attempts to create a connection to the database, returns NULL if not successful.
SAConnection* createDBConnection()
{
    if( dbType == "none" )
    {
        return NULL;
    }

    SAConnection* connection = new SAConnection();

    try
    {
        std::string server = dbServer;

        if( boost::starts_with(dbType, "oracle") )
        {
            connection->setClient(SA_Oracle_Client);
        }
        else
        {
            connection->setClient(SA_SQLServer_Client);
            server += "@";
        }

        connection->Connect(server.c_str(), dbUser.c_str(), dbPassword.c_str());
        return connection;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Database connection unsuccessful");
        delete connection;
    }

    return NULL;
}

DLLEXPORT
SAConnection* getNewConnection()
{
    CtiLockGuard<CtiCriticalSection> guard( DbMutex);

    BOOST_FOREACH(DBConnectionHolder &connHolder, connectionList)
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
        connectionList.push_back(connHolder);
        CTILOG_INFO(dout, "Database connection " << connectionList.size() << " created ");
    }

    return connHolder.connection;
}

DLLEXPORT
void releaseDBConnection(SAConnection *connection)
{
    CtiLockGuard<CtiCriticalSection> guard( DbMutex);

    BOOST_FOREACH(DBConnectionHolder &connHolder, connectionList)
    {
        if(connHolder.connection == connection)
        {
            connHolder.inUse = false;
            delete connHolder.connection;
            connHolder.connection = 0;
            return;
        }
    }
    CTILOG_ERROR(dout, "Attempted to release a connection that could not be found ");
}
