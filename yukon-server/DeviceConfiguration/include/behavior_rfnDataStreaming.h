#pragma once

#include "yukon.h"

#include "behavior_device.h"
#include "PointAttribute.h"

#include <map>
#include <chrono>

namespace Cti {
namespace Behaviors {

struct IM_EX_CONFIG RfnDataStreamingBehavior : DeviceBehavior
{
    RfnDataStreamingBehavior(const std::map<std::string, std::string>&& parameters);

    bool enabled;
    struct ConfiguredChannel
    {
        PointAttribute attribute;
        std::chrono::minutes interval;
    };

    std::vector<ConfiguredChannel> configuredChannels;
};

}
}
