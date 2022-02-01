#include "precompiled.h"
#include "YukonMetricTracker.h"
#include <json.hpp>
#include "amq_connection.h"
#include "amq_topics.h"

namespace Cti {

const std::string YukonMetricTracker::RPH_INSERTS = "RPH_INSERTS";
const std::string YukonMetricTracker::RPH_QUEUE_SIZE = "RPH_QUEUE_SIZE";

void YukonMetricTracker::sendYukonMetricMessage(const std::string pointInfo, int64_t value, std::string timeStamp)
{
    using json = nlohmann::json;
    using namespace Cti::Messaging;
    using Cti::Messaging::ActiveMQ::Topics::OutboundTopic;

    json j = {
        {"pointInfo", pointInfo},
        {"value", value},
        {"timeStamp", timeStamp}
    };

    std::string yukonMetricMessage = j.dump();
    ActiveMQConnectionManager::enqueueMessage(OutboundTopic::YukonMetricTopic, yukonMetricMessage);
    CTILOG_DEBUG(dout, "Message sent on the Yukon Metric topic is:" + yukonMetricMessage);
}

}