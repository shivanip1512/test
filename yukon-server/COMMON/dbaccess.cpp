#include "precompiled.h"

#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"

#include "guard.h"
#include "critical_section.h"

#include <boost/algorithm/string/predicate.hpp>

#include <SQLAPI.h>

namespace Cti {
namespace Database {

namespace {

std::string dbType;
std::string dbServer;
std::string dbUser;
std::string dbPassword;

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

    if( placeholder_chars > 99 )
    {
        //  each placeholder after 99 will require 3 chars
        placeholder_chars *= 3;
        placeholder_chars -= 99 + 9;
    }
    else if( placeholder_chars > 9 )
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

            //  This only writes out 3 digits, so we can only support up to 999 placeholders.
            if( offset > 99 )
            {
                result.push_back('0' + offset / 100);
                result.push_back('0' + offset / 10 % 10);
            }
            else if( offset > 9 )
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

namespace {

// Attempts to create a connection to the database, returns NULL if not successful.
SAConnection* createDBConnection()
{
    CtiLockGuard<CtiCriticalSection> guard(DbMutex);

    if( dbType == "none" )
    {
        return nullptr;
    }

    try
    {
        auto connection = std::make_unique<SAConnection>();

        std::string server = dbServer;

        if( boost::starts_with(dbType, "oracle") )
        {
            connection->setClient(SA_Oracle_Client);
        }
        else
        {
            connection->setClient(SA_SQLServer_Client);
            if( server.find('@', 0) == std::string::npos )
            {
                //  The connection string is in the format servername@databasename
                //    No database name specified, so make sure the server string is interpreted as a server name.
                server += "@";
            }
        }

        connection->Connect(server.c_str(), dbUser.c_str(), dbPassword.c_str());

        return connection.release();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Database connection unsuccessful");
    }

    return nullptr;
}

}

DLLEXPORT std::function<SAConnection*(void)> gDatabaseConnectionFactory = createDBConnection;

}
}
