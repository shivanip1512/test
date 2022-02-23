#pragma once

#include "YukonMetricTracker.h"
#include "worker_thread.h"

#include <chrono>

namespace Cti {

class DispatchMetricTracker : public YukonMetricTracker 
{
public:

    DispatchMetricTracker();
 
    void submitRows(long rows, std::chrono::milliseconds duration);
    void submitQueueSize(long queueSize);

    void start();
    void interrupt();
    void terminateThread();

private:

    void DispatchMetricTrackerThread();

    void submitRowsToTopic();
    void submitQueueSizeToTopic();

    std::atomic<std::int64_t> _rphStatistics;
    std::atomic<long> _queueSize;

    WorkerThread _metricTracker;
};

}
