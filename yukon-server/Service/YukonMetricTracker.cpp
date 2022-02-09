#include "YukonMetricTracker.h"
#include "amq_connection.h"
#include "amq_topics.h"
#include <json.hpp>
#include <format>

namespace Cti {

const std::string YukonMetricTracker::RPH_INSERTS = "RPH_INSERTS";
const std::string YukonMetricTracker::RPH_QUEUE_SIZE = "RPH_QUEUE_SIZE";

void YukonMetricTracker::sendYukonMetricMessage(const std::string pointInfo, int64_t value, std::chrono::system_clock::time_point timestamp)
{
    using json = nlohmann::json;
    using Cti::Messaging::ActiveMQConnectionManager;
    using Cti::Messaging::ActiveMQ::Topics::OutboundTopic;

    const auto timestamp8601 = std::format("{:%FT%TZ}", timestamp);

    json j = {
        {"pointInfo", pointInfo},
        {"value", value},
        {"timestamp", timestamp8601}
    };

    std::string yukonMetricMessage = j.dump();
    ActiveMQConnectionManager::enqueueMessage(OutboundTopic::YukonMetricTopic, yukonMetricMessage);
    CTILOG_DEBUG(dout, "Message sent on the Yukon Metric topic is:" + yukonMetricMessage);
}

}