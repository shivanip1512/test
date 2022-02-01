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
	
    void DispatchMetricTrackerThread();

private:
    WorkerThread _metricTracker;
    std::atomic<long> rowsSince5mins;

};
}
