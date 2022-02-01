#include "precompiled.h"
#include "DispatchMetricTracker.h"
#include "YukonMetricTracker.h"
#include "amq_connection.h"
#include "amq_topics.h"
#include "rph_archiver.h"
#include "ctivangogh.h"
#include "worker_thread.h"
#include <chrono>
#include <format>

namespace Cti {

BOOL bGCtrlC;

DispatchMetricTracker::DispatchMetricTracker() : YukonMetricTracker(),
    _metricTracker(WorkerThread::Function([this] {DispatchMetricTrackerThread();}).name("_metricTracker"))
{}

void DispatchMetricTracker::submitRows(long rows) 
{
    rowsSince5mins += rows;
}

void DispatchMetricTracker::submitRowsToTopic()
{
    auto row5Min = rowsSince5mins.exchange(0);
    const auto now = std::chrono::system_clock::now();
    const auto time_string = std::format("{:%FT%TZ}", now);

    YukonMetricTracker::sendYukonMetricMessage(YukonMetricTracker::RPH_INSERTS, row5Min, time_string);
}

void DispatchMetricTracker::DispatchMetricTrackerThread()
{
     bGCtrlC = TRUE;
    _metricTracker.start();

    for (;bGCtrlC;) 
    {
        submitRowsToTopic();
        Sleep(300000);
    }
}

}

