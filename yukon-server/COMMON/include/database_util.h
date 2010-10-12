#pragma once

#include "logger.h"
#include "ctitime.h"

namespace Cti {
namespace Database {

/**
 * This is to execute a database command whether write or read.
 *
 * Note that it will not print out any expection information to
 * the querys, only return false. Error handling is the
 * responsibility of the calling code.
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

    //False to squelch printing insert errors. The calling code is responsible for displaying them.
    if (databaseCommand.execute(false))
    {
        //No Error occurred
        return true;
    }
    else
    {
        return false;
    }
}

}
}
