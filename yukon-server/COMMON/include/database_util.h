#pragma once

#include "logger.h"
#include "ctitime.h"
#include "database_writer.h"

namespace Cti {
namespace Database {

namespace Detail {

/**
 * Commands Optional Parameters
 */
enum CommandParamEnum
{
    ShowDebug,
    RowsAffectedExpected
};

/**
 * Command flag default template implementation using Disable / Enable options
 */
template<CommandParamEnum>
struct CommandParam
{
    enum Options {
        Disable,
        Enable
    };

    CommandParam() : _option(Disable) // initializes to disable by default
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

    /// casting to Option enum
    operator Options() const
    {
        return _option;
    }

private:
    Options _option;
};

} // namespace Detail

typedef Detail::CommandParam <Detail::ShowDebug>            ShowDebug;
typedef Detail::CommandParam <Detail::RowsAffectedExpected> RowsAffectedExpected;

/**
 * Execute a read or a write command
 *
 * @return true if no error, false otherwise
 */
template <class T>
bool executeCommand( T& command, const char* file, const int line, const ShowDebug::Options showDebug = ShowDebug::Disable )
{
    if( showDebug == ShowDebug::Enable )
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
 * Execute a database update command
 *
 * @return true if no error and rows have been affected, false otherwise
 */
inline bool executeUpdater( DatabaseWriter& updater, const char* file, const int line, const ShowDebug::Options showDebug = ShowDebug::Disable, const RowsAffectedExpected::Options rowsAffectedExpected = RowsAffectedExpected::Enable )
{
    if( ! executeCommand( updater, file, line, showDebug ))
    {
        return false;
    }

    if( ! updater.rowsAffected() )
    {
        if( rowsAffectedExpected == RowsAffectedExpected::Enable )
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

} // namespace Database
} // namespace Cti
