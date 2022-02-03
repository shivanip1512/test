#pragma once
#include "YukonMetricTracker.h"
#include "worker_thread.h"

namespace Cti {

class DispatchMetricTracker : public YukonMetricTracker 
{

public:
    DispatchMetricTracker();
 
    void submitRows(long rows);
	void submitRowsToTopic();
    void submitQueueSize(long queueSize);
    void submitQueueSizeToTopic();

    void DispatchMetricTrackerThread();
    
    void start();
    void interrupt();
    void terminateThread();

private:
    
    std::atomic<long> rowsSince5Mins;
    std::atomic<long> queueSizeSince5Mins;
    WorkerThread _metricTracker;
};
}
