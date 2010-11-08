#pragma once

#include "logger.h"
#include "ctitime.h"

namespace Cti {
namespace Database {

/**
 * This is to execute a database command whether write or read.
 *
 *
 * @param databaseCommand
 * @param printDebug
 *
 * @return bool
 */
template<class T>
bool executeDbCommand(T& databaseCommand, bool printDebug = false)
{
    if (printDebug)
    {
        string loggedSQLstring = databaseCommand.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
    }

    return databaseCommand.execute();
}

}
}
