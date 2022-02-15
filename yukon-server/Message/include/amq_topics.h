#pragma once

#include "dlldefs.h"
#include <string>

namespace Cti::Messaging::ActiveMQ::Topics {

class IM_EX_MSG OutboundTopic
{
public:
    std::string name;

    static const OutboundTopic YukonMetricTopic;

};

}
