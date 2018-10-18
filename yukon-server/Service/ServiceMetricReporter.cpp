#include "precompiled.h"

#include "ServiceMetricReporter.h"
#include "module_util.h"
#include "win_helper.h"
#include "msg_pdata.h"
#include "connection.h"

#include <boost/range/numeric.hpp>
#include <boost/range/algorithm/max_element.hpp>

namespace Cti::ServiceMetrics {

constexpr auto SYSTEM_DEVICE = 0;

MetricReporter::MetricReporter(CpuPointOffsets cpuPoint, MemoryPointOffsets memoryPoint, std::string processName, std::string applicationName)
    :   _cpuPointId      { GetPIDFromDeviceAndOffset(SYSTEM_DEVICE, static_cast<int>(cpuPoint)) }
    ,   _memoryPointId   { GetPIDFromDeviceAndOffset(SYSTEM_DEVICE, static_cast<int>(memoryPoint)) }
    ,   _processName     { processName     }
    ,   _applicationName { applicationName }
{
    std::string processorCountString(getenv("NUMBER_OF_PROCESSORS"));
    _processorCount = std::stoi(processorCountString);

    _previous = metrics.getProcessTimes();
}

/** Calculate CPU Load based on processTimes().  Result is in percent. */
double MetricReporter::getCpuLoad(const processTimes_t current)
{
    const auto elapsedUserTime   = current.userTime   - _previous.userTime;
    const auto elapsedKernelTime = current.kernelTime - _previous.kernelTime;
    const double elapsedCpuTime = (elapsedUserTime + elapsedKernelTime).count();

    const double elapsedTime = (current.currentTime - _previous.currentTime).count();

    auto cpuLoad = elapsedCpuTime / elapsedTime;

    _previous = current;

    return cpuLoad / _processorCount * 100;  // Handle multiple cores & Dont forget this is in percent.
}

void MetricReporter::reportCheck(const compileinfo_t& info, CtiConnection& connection)
{
    reportCheckImpl(info, 
        [&connection](std::unique_ptr<CtiPointDataMsg> msg) { 
            connection.WriteConnQue(msg.release(), CALLSITE); 
        });
}

void MetricReporter::reportCheck(const compileinfo_t& info, CtiConnection::Que_t& queue)
{
    reportCheckImpl(info, 
        [&queue](std::unique_ptr<CtiPointDataMsg> msg) {
            queue.putQueue(msg.release());
        });
}

template <typename MessageConsumer>
void MetricReporter::reportCheckImpl(const compileinfo_t& info, MessageConsumer messageConsumer)
{
    const CtiTime Now;

    if( Now > _nextPointDataTime )
    {
        _nextPointDataTime = nextScheduledTimeAlignedOnRate(Now, 60);    // Wait another 60 seconds

        const auto processTimes = metrics.getProcessTimes();
        const auto memoryUse = metrics.getPrivateBytes();
        const auto cpuLoad = getCpuLoad(processTimes);

        constexpr auto megabytes = [](const long long bytes) {
            return bytes / 1024.0 / 1024.0;
        };

        _cpuLoads.emplace_back(cpuLoad);
        _memoryUsage.emplace_back(memoryUse);

        if( _cpuPointId )
        {
            messageConsumer(createUpdate(_cpuPointId, "CPU Usage", cpuLoad));
        }
        if( _memoryPointId )
        {
            messageConsumer(createUpdate(_memoryPointId, "Memory Usage", megabytes(memoryUse)));
        }

        if( Now > _nextLogTime )
        {
            using std::chrono::duration_cast;
            using std::chrono::milliseconds;

            _nextLogTime = nextScheduledTimeAlignedOnRate(Now, 900);

            FormattedList l;
            
            l.add("Metric use period") << Now.seconds() - _reportStart.seconds() << " seconds (since " << _reportStart << ")";

            const auto cpuAverage = boost::accumulate(_cpuLoads, 0.0) / _cpuLoads.size();
            const auto cpuMaximum = *boost::max_element(_cpuLoads);

            _cpuLoads.clear();

            l.add("CPU use (latest)")  << CtiNumStr(cpuLoad, 3) << "%";
            l.add("CPU use (average)") << CtiNumStr(cpuAverage, 3) << "%";
            l.add("CPU use (maximum)") << CtiNumStr(cpuMaximum, 3) << "%";

            const auto memoryAverage = boost::accumulate(_memoryUsage, 0.0) / _memoryUsage.size();
            const auto memoryMaximum = *boost::max_element(_memoryUsage);

            _memoryUsage.clear();

            l.add("Memory use (latest)")  << CtiNumStr(megabytes(memoryUse),     1) << " MB (" << commaFormatted(memoryUse)     << ")";
            l.add("Memory use (average)") << CtiNumStr(megabytes(memoryAverage), 1) << " MB (" << commaFormatted(memoryAverage) << ")";
            l.add("Memory use (maximum)") << CtiNumStr(megabytes(memoryMaximum), 1) << " MB (" << commaFormatted(memoryMaximum) << ")";

            FormattedTable timesTable;

            timesTable.setCell(0, 0) << "Elapsed";
            timesTable.setCell(0, 1, FormattedTable::Middle_Right) << duration_cast<milliseconds>(processTimes.currentTime - processTimes.creationTime);

            timesTable.setCell(1, 0) << "Kernel";
            timesTable.setCell(1, 1, FormattedTable::Middle_Right) << duration_cast<milliseconds>(processTimes.kernelTime);

            timesTable.setCell(2, 0) << "User";
            timesTable.setCell(2, 1, FormattedTable::Middle_Right) << duration_cast<milliseconds>(processTimes.userTime);

            l.add("Process times") << timesTable.toString().substr(1);  //  trim the implicit newline

            l.add("Total processor time") << CtiNumStr(metrics.getCpuTotal(), 3) << "%";

            CTILOG_INFO(dout, info.project << " metrics:" << l);

            _reportStart = Now;
        }
    }
}

std::unique_ptr<CtiPointDataMsg> MetricReporter::createUpdate(int pointId, std::string pointName, double value) const
{
    auto data = 
        std::make_unique<CtiPointDataMsg>(
            pointId,
            value,
            NormalQuality, 
            AnalogPointType, 
            _processName + " " + pointName);

    data->setSource(_applicationName);

    return data;
}

}