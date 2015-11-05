#pragma once

#include <windows.h>
#include <string>
#include "pdh.h"

#include "numstr.h"

namespace Cti {

//-----------------------------------------------------------------------------
//  Get the error message from the
//-----------------------------------------------------------------------------
inline std::string getSystemErrorMessage(const DWORD errorCode)
{
    LPTSTR errorMessage = NULL;

    const DWORD len = ::FormatMessage(
            FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
            NULL,
            errorCode,
            MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
            (LPTSTR)&errorMessage,
            0,
            NULL);

    if( ! len )
    {
        return std::string("Unknown error code: ") + CtiNumStr(errorCode).xhex().zpad(8);
    }

    std::string result(errorMessage, errorMessage + len);
    ::LocalFree(errorMessage);
    return result;
}

IM_EX_CTIBASE long long getPrivateBytes();

struct processTimes_t {
    ULONGLONG creationTime;
    ULONGLONG exitTime;
    ULONGLONG kernelTime;
    ULONGLONG userTime;
    ULONGLONG currentTime;
};

IM_EX_CTIBASE processTimes_t getProcessTimes(void);

IM_EX_CTIBASE double pdhGetCpuTotal(void);

} // namespace Cti
