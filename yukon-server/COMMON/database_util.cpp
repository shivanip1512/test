#include "precompiled.h"

#include "database_util.h"
#include "database_reader.h"

namespace Cti {
namespace Database {

/**
 * Execute a read or a write command
 * @return true if no error, false otherwise
 */
template <class T>
bool executeCommand( T& command, const char* file, const int line, const LogDebug::Options logDebug )
{
    if( logDebug == LogDebug::Enable )
    {
        std::string loggedSQLstring = command.asString();
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " **** DEBUG **** DB command : " << file << " (" << line << ")" << std::endl
                 << loggedSQLstring << std::endl;
        }
    }

    if( ! command.execute() )
    {
        std::string loggedSQLstring = command.asString();
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " **** ERROR **** DB command : " << file << " (" << line << ")" << std::endl
                 << loggedSQLstring << std::endl;
        }
        return false;
    }

    return true;
}

template bool DLLEXPORT executeCommand<DatabaseWriter>( DatabaseWriter &command, const char* file, const int line, const LogDebug::Options logDebug);
template bool DLLEXPORT executeCommand<DatabaseReader>( DatabaseReader &command, const char* file, const int line, const LogDebug::Options logDebug);

/**
 * Overload of executeCommand() with default LogDebug
 */
template <class T>
bool executeCommand( T& command, const char* file, const int line )
{
    return executeCommand( command, file, line, LogDebug::Disable );
}

template bool DLLEXPORT executeCommand<DatabaseWriter>( DatabaseWriter &command, const char* file, const int line);
template bool DLLEXPORT executeCommand<DatabaseReader>( DatabaseReader &command, const char* file, const int line);

/**
 * Execute a database update command
 * @return true if no error and rows have been affected, false otherwise
 */
bool executeUpdater( DatabaseWriter& updater, const char* file, const int line, const LogDebug::Options logDebug, const LogNoRowsAffected::Options logNoRowsAffected )
{
    if( ! executeCommand( updater, file, line, logDebug ))
    {
        return false;
    }

    if( ! updater.rowsAffected() )
    {
        if( logNoRowsAffected == LogNoRowsAffected::Enable )
        {
            std::string loggedSQLstring = updater.asString();
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " **** ERROR **** DB update no rows affected : " << file << " (" << line << ")" << std::endl
                     << loggedSQLstring << std::endl;
            }
        }
        return false;
    }

    return true;
}

/**
 * Overload of executeUpdater() with default LogDebug and LogNoRowsAffected
 */
bool executeUpdater( DatabaseWriter& updater, const char* file, const int line )
{
    return executeUpdater( updater, file, line, LogDebug::Disable, LogNoRowsAffected::Enable  );
}

/**
 * Overload of executeUpdater() with default LogNoRowsAffected
 */
bool executeUpdater( DatabaseWriter& updater, const char* file, const int line, const LogDebug::Options logDebug )
{
    return executeUpdater( updater, file, line, logDebug, LogNoRowsAffected::Enable );
}


/**
 * Helper method to create a string of the format
 * "table.column in (x, y, z)" or
 * "table.column = x" or
 * "table.column in ()"
 */
std::string createIdSqlClause(const id_set &paoids, const std::string &table, const std::string &column)
{
    if( paoids.empty() )
    {
        return std::string();
    }

    std::ostringstream sql;

    sql << table << "." << column;

    //  special single id case
    if( paoids.size() == 1 )
    {
        sql << " = " << *paoids.begin();
    }
    else
    {
        sql << " IN (";

        std::copy(paoids.begin(), paoids.end(), csv_output_iterator<long, std::ostringstream>(sql));

        sql << ")";
    }

    return sql.str();
}

}
}
