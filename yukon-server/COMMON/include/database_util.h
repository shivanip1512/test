#pragma once

#include "logger.h"
#include "ctitime.h"
#include "database_writer.h"

namespace Cti {
namespace Database {

/**
 * Execute database command options
 */
struct CommandOptions
{
    bool _enableDebug;  // enable debug print

    CommandOptions() :
        _enableDebug(false)
    {}

    CommandOptions& enableDebug( bool enableDebug )
    {
        _enableDebug = enableDebug;
        return *this;
    }
};

/**
 * Execute a read or a write command
 *
 * @param command
 * @param file
 * @param line
 * @param enableDebug
 *
 * @return true if no execution error, false otherwise
 */
template <class T>
bool executeCommand( T& command, const char* file, const int line, const CommandOptions options = CommandOptions() )
{
    if( options._enableDebug )
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

/**
 * Execute database update options
 */
struct UpdateOptions : protected CommandOptions
{
    bool _rowsAffectedExpected; // report an error if no rows have been affected

    UpdateOptions() :
        _rowsAffectedExpected(true)
    {}

    UpdateOptions& enableDebug( bool enableDebug )
    {
        CommandOptions::enableDebug( enableDebug );
        return *this;
    }

    UpdateOptions& rowsAffectedExpected( bool rowsAffectedExpected )
    {
        _rowsAffectedExpected = rowsAffectedExpected;
        return *this;
    }

    const CommandOptions& getCommandOptions() const
    {
        return *this;
    }
};

/**
 * Execute a database update command
 *
 * @param updater
 * @param file
 * @param line
 * @param options
 *
 * @return true if no error and rows have been affected, false otherwise
 */
inline bool executeUpdater( DatabaseWriter& updater, const char* file, const int line, const UpdateOptions options = UpdateOptions() )
{
    if( ! executeCommand(updater, file, line, options.getCommandOptions() ))
    {
        return false;
    }

    if( ! updater.rowsAffected() )
    {
        if( options._rowsAffectedExpected )
        {
            std::string loggedSQLstring = updater.asString();
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " **** ERROR **** DB Update no rows affected : " << file << " (" << line << ")" << std::endl
                     << loggedSQLstring << std::endl;
            }
        }
        return false;
    }

    return true;
}

}
}
