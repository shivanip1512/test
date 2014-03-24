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
bool IM_EX_CTIBASE executeCommand( T& command, const char* file, const int line, const LogDebug::Options logDebug );

/**
 * Overload of executeCommand() with default LogDebug
 */
template <class T>
bool IM_EX_CTIBASE executeCommand( T& command, const char* file, const int line );

/**
 * Execute a database update command
 * @return true if no error and rows have been affected, false otherwise
 */
bool IM_EX_CTIBASE executeUpdater( DatabaseWriter& updater, const char* file, const int line, const LogDebug::Options logDebug, const LogNoRowsAffected::Options logNoRowsAffected );

/**
 * Overload of executeUpdater() with default LogDebug and LogNoRowsAffected
 */
bool IM_EX_CTIBASE executeUpdater( DatabaseWriter& updater, const char* file, const int line );

/**
 * Overload of executeUpdater() with default LogNoRowsAffected
 */
bool IM_EX_CTIBASE executeUpdater( DatabaseWriter& updater, const char* file, const int line, const LogDebug::Options logDebug );

/**
 * Helper method to create a string of the format
 * "table.column in (x, y, z)" or
 * "table.column = x" or
 * "table.column in ()"
 */
std::string IM_EX_CTIBASE createIdSqlClause(const id_set &paoids, const std::string &table, const std::string &column);

} // namespace Database
} // namespace Cti
