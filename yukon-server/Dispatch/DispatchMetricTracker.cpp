#include "precompiled.h"
#include "DispatchMetricTracker.h"

namespace Cti {

BOOL bGCtrlC;

DispatchMetricTracker::DispatchMetricTracker() :
    _metricTracker(WorkerThread::Function([this] { DispatchMetricTrackerThread(); }).name("_metricTracker")) {}

void DispatchMetricTracker::DispatchMetricTrackerThread()
{
    bGCtrlC = TRUE;
    _metricTracker.start();

    for (; bGCtrlC;) {
        //submitRowstoTopic();
        //submitQueuSizeToTopic();
        Sleep(300000);
    }
}
}

