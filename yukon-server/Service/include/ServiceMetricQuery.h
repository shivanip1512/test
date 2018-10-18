#pragma once

#include "pdh.h"

#include <chrono>

namespace Cti {

struct processTimes_t {
    //  system_clock::duration uses 100 ns increments

    std::chrono::system_clock::time_point creationTime;
    std::chrono::system_clock::time_point currentTime;
    std::chrono::system_clock::time_point exitTime;
    std::chrono::system_clock::duration kernelTime;
    std::chrono::system_clock::duration userTime;
};

class ServiceMetricQuery
{
public:
    ServiceMetricQuery();

    double getCpuTotal(void);

    long long getPrivateBytes();

    processTimes_t getProcessTimes(void);

private:
    PDH_HQUERY   _query;
    PDH_HCOUNTER _cpuTotal;
};

}