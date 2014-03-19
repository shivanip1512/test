#pragma once

#include "logger.h"
#include "ctitime.h"
#include "database_writer.h"

namespace Cti {
namespace Database {

namespace Detail {

/**
 * Commands parameters
 */
enum CommandParamEnum
{
    LogDebug,
    LogNoRowsAffected
};

/**
 * Commands parameter default template implementation using Disable/Enable options
 */
template<CommandParamEnum>
struct CommandParam
{
    enum Options {
        Disable,
        Enable
    };

    CommandParam() : _option(Disable) // initialize to Disable by default
    {}

    template <typename T>
    CommandParam( T value )
    {
        *this = value;
    }

    /// overload assignment operator for an option
    CommandParam& operator=( const Options option )
    {
        _option = option;
    }

    /// overload assignment operator for a bool
    CommandParam& operator=( const bool isEnable )
    {
        _option = isEnable ? Enable : Disable;
        return *this;
    }

    /// casting to Options enum
    operator Options() const
    {
        return _option;
    }

private:
    Options _option;
};

} // namespace Detail

typedef Detail::CommandParam <Detail::LogDebug>          LogDebug;
typedef Detail::CommandParam <Detail::LogNoRowsAffected> LogNoRowsAffected;

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

/**
 * Overload of executeCommand() with default LogDebug
 */
template <class T>
bool executeCommand( T& command, const char* file, const int line )
{
    return executeCommand( command, file, line, LogDebug::Disable );
}

/**
 * Execute a database update command
 * @return true if no error and rows have been affected, false otherwise
 */
inline bool executeUpdater( DatabaseWriter& updater, const char* file, const int line, const LogDebug::Options logDebug, const LogNoRowsAffected::Options logNoRowsAffected )
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
inline bool executeUpdater( DatabaseWriter& updater, const char* file, const int line )
{
    return executeUpdater( updater, file, line, LogDebug::Disable, LogNoRowsAffected::Enable  );
}

/**
 * Overload of executeUpdater() with default LogNoRowsAffected
 */
inline bool executeUpdater( DatabaseWriter& updater, const char* file, const int line, const LogDebug::Options logDebug )
{
    return executeUpdater( updater, file, line, logDebug, LogNoRowsAffected::Enable );
}

std::string IM_EX_CTIBASE createIdSqlClause(const id_set &paoids, const std::string &table, const std::string &column);

} // namespace Database
} // namespace Cti
