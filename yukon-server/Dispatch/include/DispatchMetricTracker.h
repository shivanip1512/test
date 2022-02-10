#pragma once
#include "YukonMetricTracker.h"
#include "worker_thread.h"

namespace Cti {

class DispatchMetricTracker : public YukonMetricTracker 
{

public:

    DispatchMetricTracker();
 
    void submitRows(long rows);
    void submitQueueSize(long queueSize);

    void start();
    void interrupt();
    void terminateThread();

private:

    void DispatchMetricTrackerThread();
    void submitRowsToTopic();
    void submitQueueSizeToTopic();

    std::atomic<long> rowsSince5Mins;
    std::atomic<long> queueSizeSince5Mins;
    WorkerThread _metricTracker;
};
}
