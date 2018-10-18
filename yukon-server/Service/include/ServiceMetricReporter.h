#pragma once

#include "dlldefs.h"
#include "connection.h"
#include "ServiceMetricQuery.h"

#include <string>

namespace Cti::ServiceMetrics {

enum class CpuPointOffsets;
enum class MemoryPointOffsets;

class IM_EX_SERVICE MetricReporter
{
public:
    MetricReporter(CpuPointOffsets cpuPoint, MemoryPointOffsets memoryPoint, std::string processName, std::string applicationName);

    void reportCheck(const compileinfo_t& info, CtiConnection& connection);
    void reportCheck(const compileinfo_t& info, CtiConnection::Que_t& queue);

private:
    template <typename MessageConsumer>
    void reportCheckImpl(const compileinfo_t& info, MessageConsumer m);

    std::unique_ptr<CtiPointDataMsg> createUpdate(int pointId, std::string pointName, double value) const;

    ServiceMetricQuery metrics;

    const int _cpuPointId;
    const int _memoryPointId;
    const std::string _processName;
    const std::string _applicationName;

    double getCpuLoad(const processTimes_t newTimes);

    processTimes_t _previous;
    int _processorCount;
        
    CtiTime _nextPointDataTime;
    CtiTime _nextLogTime;
    CtiTime _reportStart;

    std::vector<double> _cpuLoads;
    std::vector<long long> _memoryUsage;
};

enum class CpuPointOffsets
{
    Porter = 1007,
    Dispatch = 1008,
    Scanner = 1009,
    Calc = 1010,
    CapControl = 1011,
    FDR = 1012,
    Macs = 1013,
    //     NotificationServer = 1014,        // Not used in the C++ code, only in Java
    //     ServiceManager = 1015,            // Not used in the C++ code, only in Java
    //     WebServer = 1016,                 // Not used in the C++ code, only in Java
    LoadManager = 1028,
};

enum class MemoryPointOffsets
{
    Porter = 1017,
    Dispatch = 1018,
    Scanner = 1019,
    Calc = 1020,
    CapControl = 1021,
    FDR = 1022,
    Macs = 1023,
    //     NotificationServer = 1024,     // Not used in the C++ code, only in Java
    //     ServiceManager = 1025,         // Not used in the C++ code, only in Java
    //     WebServer = 1026,              // Not used in the C++ code, only in Java
    LoadManager = 1029,
};

}