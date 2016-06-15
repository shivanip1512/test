#pragma once

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
    LogNoRowsAffected,
    TryInsertFirst
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
typedef Detail::CommandParam <Detail::TryInsertFirst>    TryInsertFirst;

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
 * Execute a database insert command
 * @throws DatabaseException
 */
IM_EX_CTIBASE void executeWriter( DatabaseWriter &writer, const char* file, const int line, const LogDebug::Options logDebug );

/**
 * Helper method to create a string of the format
 * "table.column in (x, y, z)" or
 * "table.column = x" or
 * "table.column in ()"
 */
std::string IM_EX_CTIBASE createIdSqlClause(const id_set &ids, const std::string &table, const std::string &column);
std::string IM_EX_CTIBASE createIdSqlClause(const long id, const std::string &table, const std::string &column);

/**
 * Helper method to create a string of the format
 * "table.column in (?, ?, ?)" or
 * "table.column = ?" or
 * "table.column in ()"
 */
std::string IM_EX_CTIBASE createIdInClause(const std::string &table, const std::string &column, size_t count);

/**
* Helper method to create a string of the format
* "table.column = ?"
*/
std::string IM_EX_CTIBASE createIdEqualClause(const std::string &table, const std::string &column);

/**
 * Execute an Upsert operation
 * @throws DatabaseException
 */
IM_EX_CTIBASE void executeUpsert(
        DatabaseConnection &conn,
        const boost::function<void (DatabaseWriter &)> &initInserter,
        const boost::function<void (DatabaseWriter &)> &initUpdater,
        const TryInsertFirst::Options tryInsertFirst,
        const char* file, const int line, const LogDebug::Options logDebug );

} // namespace Database
} // namespace Cti
