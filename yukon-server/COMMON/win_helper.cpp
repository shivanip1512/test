#include "precompiled.h"

#include "win_helper.h"
#include "logger.h"

#include <psapi.h>

namespace Cti {

long long getPrivateBytes()
{
    PROCESS_MEMORY_COUNTERS_EX memoryCounters;

    if( ! GetProcessMemoryInfo(GetCurrentProcess(), reinterpret_cast<PPROCESS_MEMORY_COUNTERS>(&memoryCounters), sizeof(memoryCounters)) )
    {
        const DWORD error = GetLastError();

        CTILOG_ERROR(dout, "GetProcessMemoryInfo failed with error code " << error << " / " << getSystemErrorMessage(error));

        return -1;
    }

    return memoryCounters.PrivateUsage;
}

}

