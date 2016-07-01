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
    RfnDataStreamingBehavior(const long paoId, const std::map<std::string, std::string>&& parameters);

    const bool enabled;

    struct Channel
    {
        Channel(std::string name, uint8_t intervalMinutes)
            :   attribute{Attribute::Lookup(name)},
                interval{intervalMinutes}
        {}

        Attribute attribute;
        std::chrono::minutes interval;
    };

    const std::vector<Channel> channels;

private:

    std::vector<Channel> parseChannels();
};

}
}
