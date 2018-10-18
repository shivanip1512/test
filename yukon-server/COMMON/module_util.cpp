#include "precompiled.h"

#include "module_util.h"
#include "win_helper.h"
#include "logger.h"
#include "dllbase.h"
#include "guard.h"

#include "dbaccess.h"

using std::endl;

namespace Cti {

void identifyProject(const compileinfo_t &info)
{
    if( isDebugLudicrous() && info.date )
    {
        CTILOG_INFO(dout, info.project << " [Version " << info.version << "]" << " [Version Details: " << info.details << ", " << info.date << "]");
    }
    else
    {
        CTILOG_INFO(dout, info.project << " [Version " << info.version << "]" << " [Version Details: " << info.details << "]");
    }
}

//  Log the executable name and set the database application name.
void identifyExecutable(const compileinfo_t &info)
{
    identifyProject(info);

    Database::setApplicationName(info.project);
}

//  Just log the DLL name.
void identifyLibrary(const compileinfo_t &info)
{
    identifyProject(info);
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

/* Called when we get an SEH exception.  Generates a minidump. */

IM_EX_CTIBASE LONG WINAPI MinidumpExceptionFilter( const Cti::compileinfo_t &info, const LPEXCEPTION_POINTERS &pExceptionPtrs )
{
    return CreateMiniDumpExceptionHandler( info, pExceptionPtrs );
}



} // namespace Cti
