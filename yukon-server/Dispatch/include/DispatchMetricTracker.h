#pragma once
#include "precompiled.h"
#include "YukonMetricTracker.h"
#include "worker_thread.h"

namespace Cti {

class DispatchMetricTracker: public YukonMetricTracker
{
public:
    DispatchMetricTracker();
    void DispatchMetricTrackerThread();

private:
    WorkerThread _metricTracker;
};
}
