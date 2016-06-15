#include "precompiled.h"

#include "database_exceptions.h"
#include "database_util.h"
#include "database_reader.h"
#include "logger.h"

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
        CTILOG_DEBUG(dout, "DB command:\n" << command.asString());
    }

    if( ! command.execute() )
    {
        CTILOG_ERROR(dout, "DB command:\n" << command.asString());
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
            CTILOG_ERROR(dout, "DB update no rows affected:\n" << updater.asString());
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
 * Execute a database write command with exceptions, rethrow the 
 *      caught exception. 
 */
void executeWriter( DatabaseWriter &writer, const char* file, const int line, const LogDebug::Options logDebug )
{
    if( logDebug == LogDebug::Enable )
    {
        CTILOG_DEBUG(dout, "DB command:\n" << writer.asString());
    }

    try
    {
        writer.executeWithDatabaseException();
    }
    catch( DatabaseException& x )
    {
        CTILOG_EXCEPTION_ERROR(dout, x, "DB Writer execute failed for SQL query: " << writer.asString());
        throw;
    }
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

    Cti::StreamBuffer sql;

    sql << table << "." << column;

    //  special single id case
    if( paoids.size() == 1 )
    {
        sql << " = " << *paoids.begin();
    }
    else
    {
        sql << " IN (";
        sql << Cti::join(paoids, ",");
        sql << ")";
    }

    return sql.extractToString();
}

std::string createIdSqlClause(const long id, const std::string &table, const std::string &column)
{
    Cti::StreamBuffer sql;

    sql << table << "." << column << " = " << id;

    return sql.extractToString();
}


std::string createPlaceholderList(const size_t count)
{
    if( count == 0 )
    {
        return "()";
    }

    std::string in_list(count * 2 + 1, '?');  //  N '?' + N-1 ',' + '(' + ')' = N * 2 + 1

    *in_list.begin() = '(';
    *in_list.rbegin() = ')';

    for( auto idx = 1; idx < count; ++idx )
    {
        in_list[idx * 2] = ',';  //  replace index 2, 4, 6, etc:  "(?,?,?,?..."
    }

    return in_list;
}


std::string createIdInClause(const std::string &table, const std::string &column, size_t count)
{
    if( count == 1 )
    {
        return createIdEqualClause(table, column);
    }

    Cti::StreamBuffer sql;

    sql << table << "." << column << " IN " << createPlaceholderList(count);

    return sql.extractToString();
}


std::string createIdEqualClause(const std::string &table, const std::string &column)
{
    Cti::StreamBuffer sql;

    sql << table << "." << column << " = ?";

    return sql.extractToString();
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
void executeUpsert(DatabaseConnection &conn,
                   const boost::function<void (DatabaseWriter &)> &initInserter,
                   const boost::function<void (DatabaseWriter &)> &initUpdater,
                   const TryInsertFirst::Options tryInsertFirst,
                   const char* file, const int line, const LogDebug::Options logDebug )
{
    if( tryInsertFirst == TryInsertFirst::Enable )
    {
        try
        {
            DatabaseWriter inserter(conn);
            initInserter( inserter );

            executeWriter(inserter, file, line, logDebug);
        }
        catch( PrimaryKeyViolationException& /*ex*/ )
        {
            if( logDebug == LogDebug::Enable )
            {
                CTILOG_WARN(dout, "DB Insert has fail for primary key violation, will try Update");
            }

            // if the error is a primary violation the row could already be there, retry with update first
            executeUpsert(conn, initInserter, initUpdater, TryInsertFirst::Disable, file, line, logDebug);
        }
    }
    else
    {
        DatabaseWriter updater(conn);
        initUpdater( updater );

        executeWriter(updater, file, line, logDebug);

        if( ! updater.rowsAffected() )
        {
            if( logDebug == LogDebug::Enable )
            {
                CTILOG_WARN(dout, "DB Update no rows affected, will try Insert");
            }

            DatabaseWriter inserter(conn);
            initInserter( inserter );

            executeWriter(inserter, file, line, logDebug);
        }
    }
}

} // Database
} // Cti
