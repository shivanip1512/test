#include "YukonMetricTracker.h"

#include "amq_connection.h"
#include "amq_topics.h"

#include <json.hpp>

#include <format>

namespace Cti {

const std::string YukonMetricTracker::RPH_INSERTS = 
                                     "RPH_INSERTS";
const std::string YukonMetricTracker::RPH_INSERT_MILLIS_PER_ROW = 
                                     "RPH_INSERT_MILLIS_PER_ROW";
const std::string YukonMetricTracker::RPH_QUEUE_SIZE = 
                                     "RPH_QUEUE_SIZE";

namespace {

template<class T>
void sendImpl(const std::string pointInfo, T value, std::chrono::system_clock::time_point timestamp)
{
    using Cti::Messaging::ActiveMQ::Topics::OutboundTopic;

    const auto timestamp8601 = std::format("{:%FT%TZ}", timestamp);

    const nlohmann::json j = {
        { "pointInfo", pointInfo     },
        { "value",     value         },
        { "timestamp", timestamp8601 }};

    const std::string yukonMetricMessage = j.dump();

    Messaging::ActiveMQConnectionManager::enqueueMessage(
        OutboundTopic::YukonMetricTopic, 
        yukonMetricMessage);

    CTILOG_DEBUG(dout, "Message sent on the Yukon Metric topic is:" + yukonMetricMessage);
}

}

void YukonMetricTracker::sendIntegralMetric(const std::string pointInfo, int64_t value, std::chrono::system_clock::time_point timestamp)
{
    sendImpl(pointInfo, value, timestamp);
}

void YukonMetricTracker::sendFloatingMetric(const std::string pointInfo, double value, std::chrono::system_clock::time_point timestamp)
{
    sendImpl(pointInfo, value, timestamp);
}

}