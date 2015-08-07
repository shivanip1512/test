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

int getProcessTimes(processTimes_t &times)
{
    if(!GetProcessTimes(GetCurrentProcess(),
        &times.creationTime, &times.exitTime, &times.kernelTime, &times.userTime))
    {
        const DWORD error = GetLastError();

        CTILOG_ERROR(dout, "GetProcessTimes failed with error code " << error << " / " << getSystemErrorMessage(error));

        return -1;
    }

    GetSystemTimeAsFileTime(&times.currentTime);

    return 0;
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

