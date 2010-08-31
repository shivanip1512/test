#pragma once

#include "logger.h"
#include "ctitime.h"

namespace Cti {
namespace Database {

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

    if (databaseCommand.execute())
    {
        //No Error occurred
        return true;
    }
    else
    {
        string loggedSQLstring = databaseCommand.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Database error occured" << endl;
            dout << "  " << loggedSQLstring << endl;
        }
        return false;
    }
}

}
}
