#include "precompiled.h"

#include "module_util.h"
#include "win_helper.h"
#include "logger.h"
#include "dllbase.h"
#include "guard.h"

using std::endl;

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
 * Create an exclusive event with default arguments.
 *
 * @return the event handle,
 *         NULL if another event with the same name already exist
 *
 * @throw  std::runtime_error if CreateEvent fails
 */
HANDLE createExclusiveEvent(const char *eventName)
{
    return createExclusiveEvent(true,  // manual reset by default
                                false, // initial state is false
                                eventName);
}

/**
 * Create an exclusive event.
 *
 * @return the event handle,
 *         NULL if another event with the same name already exist
 *
 * @throw  std::runtime_error if CreateEvent fails
 */
HANDLE createExclusiveEvent(bool manualReset,
                            bool initialState,
                            const char *eventName)
{
    const HANDLE hExclusion = CreateEvent(NULL, manualReset, initialState, eventName);

    if( hExclusion == NULL )
    {
        const DWORD error = GetLastError();
        std::ostringstream msg;
        msg <<"failed to create event \""<< eventName <<"\" due to error "<< error <<" / "<< getSystemErrorMessage(error);
        throw std::runtime_error(msg.str());
    }

    if( GetLastError() == ERROR_ALREADY_EXISTS )
    {
        CloseHandle(hExclusion);
        return NULL;
    }

    return hExclusion;
}

} // namespace Cti
