#include "precompiled.h"

#include "module_util.h"
#include "win_helper.h"
#include "logger.h"
#include "dllbase.h"
#include "guard.h"

using std::endl;
using std::cerr;

namespace Cti {

/**
 *  Used by the logging to identify each module at startup
 */
void identifyProject(const compileinfo_t &info)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(isDebugLudicrous() && info.date)      // DEBUGLEVEL added 012903 CGP
        {
            dout << CtiTime() << " " << info.project << " [Version " << info.version << "]" /* << endl */ << " [Version Details: " << info.details << ", " << info.date << "]" << endl;
        }
        else
        {
            dout << CtiTime() << " " << info.project << " [Version " << info.version << "]" /* << endl */ << " [Version Details: " << info.details << "]" << endl;
        }
    }

    return;
}

/**
 *  Attempt to set the title of the console window
 *  If it returns false, the program is not running in a window
 */
bool setConsoleTitle(const compileinfo_t &info)
{
    std::ostringstream s;

    s << info.project << " - YUKON " << info.version;

    return SetConsoleTitle( s.str().c_str() );
}

/**
 * Overload using default parameters.
 * Try to create and exclusive event. Exit if another instance is running.
 */
HANDLE createExclusiveEvent(const compileinfo_t &info,
                            const char *eventName)
{
    return createExclusiveEvent(info.project,
                                true,  // manual reset by default
                                false, // initial state is false
                                eventName);
}

/**
 * Try to create and exclusive event. Exit if another instance is running.
 */
HANDLE createExclusiveEvent(const char *moduleName,
                            bool  manualReset,
                            bool  initialState,
                            const char *eventName)
{
    HANDLE hExclusion = CreateEvent(NULL, manualReset, initialState, eventName);

    if( hExclusion == NULL )
    {
        const DWORD error = GetLastError();
        cerr << moduleName <<" failed to create event \""<< eventName <<"\""<< endl;
        cerr <<"caused by error "<< error <<" / "<< getSystemErrorMessage(error) << endl;
        exit(-1);
    }

    if( GetLastError() == ERROR_ALREADY_EXISTS )
    {
        CloseHandle(hExclusion);
        hExclusion = NULL;
        cerr << moduleName <<" is already running on this machine, exiting."<< endl;
        exit(-1);
    }

    return hExclusion;
}

} // namespace Cti
