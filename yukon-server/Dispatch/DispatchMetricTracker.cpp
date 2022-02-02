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

DispatchMetricTracker::DispatchMetricTracker() : YukonMetricTracker(), _metricTracker(WorkerThread::Function([this] {DispatchMetricTrackerThread(); }).name("_metricTracker")){};

void DispatchMetricTracker::submitRows(long rows) 
{
    rowsSince5mins += rows;
}

void DispatchMetricTracker::submitRowsToTopic()
{
    auto row5Min = rowsSince5mins.exchange(0);
    const auto now = std::chrono::system_clock::now();
    //Adding a function to CtiTime instead..
    const std::string time_string = "12345"; //to be updated
    //not using this as will need C++20
    //const auto time_string = std::format("{:%FT%TZ}", now);
    //auto now = std::chrono::system_clock::now();
    //auto itt = std::chrono::system_clock::to_time_t(now);
    //std::ostringstream ss;
    //ss << std::put_time(gmtime(&itt), "%FT%TZ");

    sendYukonMetricMessage(RPH_INSERTS, 123, "567");
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

