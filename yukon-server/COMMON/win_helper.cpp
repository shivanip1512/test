#include "precompiled.h"

#include "win_helper.h"
#include "logger.h"

#include <psapi.h>
#include <pdh.h>

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

/** Convert a FILETIME to a ULARGE_INTEGER */
ULONGLONG fileTime2LongLong(FILETIME &filetime)
{
    ULARGE_INTEGER ularge;
    ularge.LowPart = filetime.dwLowDateTime;
    ularge.HighPart = filetime.dwHighDateTime;
    return ularge.QuadPart;
}

/** Read in ProcessTimes and make them into ULLONGLONG_INTEGERS */
struct processTimes_t getProcessTimes()
{
    Cti::processTimes_t longTimes = {0, 0, 0, 0, 0};
    FILETIME creationTime;
    FILETIME exitTime;
    FILETIME kernelTime;
    FILETIME userTime;
    FILETIME currentTime;

    if(!GetProcessTimes(GetCurrentProcess(),
        &creationTime, &exitTime, &kernelTime, &userTime))
    {
        const DWORD error = GetLastError();

        CTILOG_ERROR(dout, "GetProcessTimes failed with error code " << error << " / " << getSystemErrorMessage(error));

        return longTimes;
    }

    GetSystemTimeAsFileTime(&currentTime);

    longTimes.creationTime = fileTime2LongLong(creationTime);
    longTimes.kernelTime = fileTime2LongLong(kernelTime);
    longTimes.userTime = fileTime2LongLong(userTime);
    longTimes.currentTime = fileTime2LongLong(currentTime);

    return longTimes;
}

static PDH_HQUERY cpuQuery;
static PDH_HCOUNTER cpuTotal;

void pdhInit() {
    PdhOpenQuery(NULL, NULL, &cpuQuery);
    PdhAddCounter(cpuQuery, TEXT("\\Processor(_Total)\\% Processor Time"), NULL, &cpuTotal);
    PdhCollectQueryData(cpuQuery);
}

double pdhGetCpuTotal() {
    PDH_FMT_COUNTERVALUE counterVal;

    if(cpuQuery == 0)
    {
        pdhInit();
    }

    PdhCollectQueryData(cpuQuery);
    PdhGetFormattedCounterValue(cpuTotal, PDH_FMT_DOUBLE, NULL, &counterVal);

    return counterVal.doubleValue;
}

}

