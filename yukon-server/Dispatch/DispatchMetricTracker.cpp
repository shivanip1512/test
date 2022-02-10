#include "precompiled.h"
#include "DispatchMetricTracker.h"
#include "YukonMetricTracker.h"
#include "amq_connection.h"
#include "amq_topics.h"
#include "worker_thread.h"
#include <chrono>
#include <atomic>

namespace Cti {


DispatchMetricTracker::DispatchMetricTracker() : YukonMetricTracker(),
                                                 _metricTracker(WorkerThread::Function([this] {DispatchMetricTrackerThread(); }).name("_metricTracker")),
                                                 rowsSince5Mins{0},
                                                 queueSizeSince5Mins{0}
{};

void DispatchMetricTracker::submitRows(long rows) 
{
    rowsSince5Mins += rows;
}

void DispatchMetricTracker::submitRowsToTopic()
{
    auto row5Min = rowsSince5Mins.exchange(0);
    const auto now = std::chrono::system_clock::now();

    sendYukonMetricMessage(RPH_INSERTS, row5Min, now);
}

void DispatchMetricTracker::submitQueueSize(long queueSize)
{
    queueSizeSince5Mins += queueSize;
}

void DispatchMetricTracker::submitQueueSizeToTopic()
{
    auto queueSize5Min = queueSizeSince5Mins.exchange(0);
    const auto now = std::chrono::system_clock::now();

    sendYukonMetricMessage(RPH_QUEUE_SIZE, queueSize5Min, now);
}

void DispatchMetricTracker::DispatchMetricTrackerThread()
{
    CTILOG_INFO(dout, "DispatchMetricTrackerThread - Started");

    try 
    {
        while (true)
        {
            Sleep(300000);
            submitRowsToTopic();
            submitQueueSizeToTopic();
        }
    }
    catch (const WorkerThread::Interrupted& ex)
    {
        CTILOG_ERROR(dout, "Interrupted, shutting downn DispatchMetricTrackerThread");
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "DispatchMetricTrackerThread Thread shutting down");
    
}

void DispatchMetricTracker::start()
{
    _metricTracker.start();
}

void DispatchMetricTracker::interrupt()
{
    _metricTracker.interrupt();
}

void DispatchMetricTracker::terminateThread()
{
    _metricTracker.terminateThread();
}

}

