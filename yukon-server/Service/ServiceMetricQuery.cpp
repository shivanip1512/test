#include "precompiled.h"

#include "ServiceMetricQuery.h"
#include "logger.h"
#include "win_helper.h"

#include "psapi.h"

namespace Cti {

ServiceMetricQuery::ServiceMetricQuery()
{
    PdhOpenQuery(NULL, NULL, &_query);

    PdhAddCounter(_query, TEXT("\\Processor(_Total)\\% Processor Time"), NULL, &_cpuTotal);

    PdhCollectQueryData(_query);
}

double ServiceMetricQuery::getCpuTotal()
{
    PDH_FMT_COUNTERVALUE counterVal;

    PdhCollectQueryData(_query);
    PdhGetFormattedCounterValue(_cpuTotal, PDH_FMT_DOUBLE, NULL, &counterVal);

    return counterVal.doubleValue;
}

long long ServiceMetricQuery::getPrivateBytes()
{
    PROCESS_MEMORY_COUNTERS_EX memoryCounters;

    if( !GetProcessMemoryInfo(GetCurrentProcess(), reinterpret_cast<PPROCESS_MEMORY_COUNTERS>(&memoryCounters), sizeof(memoryCounters)) )
    {
        const DWORD error = GetLastError();

        CTILOG_ERROR(dout, "GetProcessMemoryInfo failed with error code " << error << " / " << getSystemErrorMessage(error));

        return -1;
    }

    return memoryCounters.PrivateUsage;
}

std::chrono::system_clock::duration toDuration(const FILETIME filetime)
{
    ULARGE_INTEGER ularge;
    ularge.LowPart = filetime.dwLowDateTime;
    ularge.HighPart = filetime.dwHighDateTime;
    return std::chrono::system_clock::duration(ularge.QuadPart);  //  FILETIME uses 100 ns increments, same as system_clock::duration
}

std::chrono::system_clock::time_point toTimePoint(const FILETIME filetime)
{
    return std::chrono::system_clock::time_point(toDuration(filetime));
}

processTimes_t ServiceMetricQuery::getProcessTimes()
{
    processTimes_t longTimes;
    FILETIME creationTime, exitTime, kernelTime, userTime, currentTime;

    if( ! GetProcessTimes(GetCurrentProcess(), &creationTime, &exitTime, &kernelTime, &userTime) )
    {
        const DWORD error = GetLastError();

        CTILOG_ERROR(dout, "GetProcessTimes failed with error code " << error << " / " << getSystemErrorMessage(error));

        return longTimes;
    }

    GetSystemTimeAsFileTime(&currentTime);

    longTimes.creationTime = toTimePoint(creationTime);
    longTimes.kernelTime   = toDuration(kernelTime);
    longTimes.userTime     = toDuration(userTime);
    longTimes.currentTime  = toTimePoint(currentTime);

    return longTimes;
}

}