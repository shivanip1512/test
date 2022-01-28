#include "include\YukonMetricTracker.h"
#include "include\YukonMetricTracker.h"
#include "precompiled.h"
#include "YukonMetricTracker.h"
#include "../../../../environment/libraries/nlohmann/3.1.2/json.hpp"
#include "amq_connection.h"
#include "amq_topics.h"

namespace Cti {

YukonMetricTracker::YukonMetricTracker() {}

YukonMetricTracker::YukonMetricTracker(static const std::string pointInfo, int64_t value, long long timeStamp) {}

void YukonMetricTracker::sendYukonMetricMessage(static const std::string pointInfo, int64_t value, long long timeStamp)
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