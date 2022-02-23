#include "precompiled.h"

#include "DispatchMetricTracker.h"

#include "amq_connection.h"
#include "amq_topics.h"
#include "worker_thread.h"

#include <gsl/gsl_util>

using namespace std::chrono_literals;

namespace Cti {

DispatchMetricTracker::DispatchMetricTracker() : 
    YukonMetricTracker(),
    _metricTracker(WorkerThread::Function([this] {DispatchMetricTrackerThread(); }).name("_metricTracker")),
    _rphStatistics{ 0 },
    _queueSize    { 0 }
{
}

namespace {

struct RphStatistics 
{
    std::int32_t rows;
    std::int32_t millis;

    static RphStatistics from_int64_t(std::int64_t raw)
    {
        return {
            //  Top 32 bits
            gsl::narrow_cast<std::int32_t>(raw >> 32),
            //  Bottom 32 bits
            gsl::narrow_cast<std::int32_t>(raw) };
    }

    operator std::int64_t() const noexcept
    {
        return (static_cast<std::int64_t>(rows) << 32) | millis;
    }

    std::optional<double> getMillisPerRow() const
    {
        if( ! rows )
        {
            return std::nullopt;
        }
        return static_cast<double>(millis) / rows;
    }
};

}

void DispatchMetricTracker::submitRows(long rows, std::chrono::milliseconds duration) 
{
    _rphStatistics += RphStatistics { rows, gsl::narrow_cast<std::int32_t>(duration.count()) };
}

void DispatchMetricTracker::submitRowsToTopic()
{
    const auto stats = RphStatistics::from_int64_t(_rphStatistics.exchange(0));

    const auto now = std::chrono::system_clock::now();

    sendIntegralMetric(
        RPH_INSERTS, 
        stats.rows,
        now);

    if( const auto millisPerRow = stats.getMillisPerRow() )
    {
        sendFloatingMetric(
            RPH_INSERT_MILLIS_PER_ROW,
            *millisPerRow,
            now);
    }
}

void DispatchMetricTracker::submitQueueSize(long queueSize)
{
    _queueSize = queueSize;
}

void DispatchMetricTracker::submitQueueSizeToTopic()
{
    sendIntegralMetric(
        RPH_QUEUE_SIZE, 
        _queueSize.exchange(0),
        std::chrono::system_clock::now());
}

void DispatchMetricTracker::DispatchMetricTrackerThread()
{
    CTILOG_INFO(dout, "DispatchMetricTrackerThread - Started");

    try 
    {
        while (true)
        {
            std::this_thread::sleep_for(5min);
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

