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
    FILETIME creationTime;
    FILETIME exitTime;
    FILETIME kernelTime;
    FILETIME userTime;
    FILETIME currentTime;
};

IM_EX_CTIBASE int getProcessTimes(processTimes_t &times);

IM_EX_CTIBASE double pdhGetCpuTotal(void);

} // namespace Cti
