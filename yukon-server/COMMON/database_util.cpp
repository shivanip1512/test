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
 * Execute a database insert command
 * @return normalized error code, or null if no error
 */
const ErrorCodes *executeInserter( DatabaseWriter &inserter, const char* file, const int line, const LogDebug::Options logDebug )
{
    if( logDebug == LogDebug::Enable )
    {
        std::string loggedSQLstring = inserter.asString();
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " **** DEBUG **** DB command : " << file << " (" << line << ")" << std::endl
                 << loggedSQLstring << std::endl;
        }
    }

    const ErrorCodes *ec = inserter.executeReturningErrorCode();

    if( ec )
    {
        std::string loggedSQLstring = inserter.asString();
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " **** ERROR **** DB command : " << file << " (" << line << ")" << std::endl
                 << loggedSQLstring << std::endl;
        }
    }

    return ec;
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

/**
 * Execute an Upsert operation
 *
 * If try Insert first:
 * Insert -> if fail for PK retry Upsert with update first
 *
 * If try Update first:
 * Update -> if no error but no rows are affected try an insert
 *
 */
const ErrorCodes *executeUpsert(
        DatabaseConnection &conn,
        const InitWriterFunc& initInserter, const InitWriterFunc& initUpdater,
        const char* file, const int line, const TryInsertFirst::Options tryInsertFirst, const LogDebug::Options logDebug)
{
    if( tryInsertFirst == TryInsertFirst::Enable )
    {
        DatabaseWriter inserter(conn);
        initInserter( inserter );

        const ErrorCodes* ec = executeInserter(inserter, file, line, logDebug);

        if( ec == &ErrorCodes::ErrorCode_PrimaryKeyViolated )
        {
            if( logDebug == LogDebug::Enable )
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " DB Insert has fail for primary key violation, will try Update : " << file << " (" << line << ")" << std::endl;
            }

            // if the error is a primary violation the row could already be there,
            // retry with update first
            return executeUpsert(conn, initInserter, initUpdater, file, line, TryInsertFirst::Disable, logDebug);
        }

        return ec;
    }
    else
    {
        DatabaseWriter updater(conn);
        initUpdater( updater );

        if( ! executeCommand(updater, file, line, logDebug) )
        {
            return &ErrorCodes::ErrorCode_Other;
        }

        if( ! updater.rowsAffected() )
        {
            if( logDebug == LogDebug::Enable )
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " DB Update no rows affected will try Insert : " << file << " (" << line << ")" << std::endl;
            }

            DatabaseWriter inserter(conn);
            initInserter( inserter );

            return executeInserter(inserter, file, line, logDebug);
        }

        return 0;
    }
}

} // Database
} // Cti
