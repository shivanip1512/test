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
    RfnDataStreamingBehavior(const long paoId, const std::map<std::string, std::string>&& parameters, behavior_report_tag);

    const bool enabled;

    struct Channel
    {
        //  Constructor for use with Behavior, which contains only enabled channels by definition
        Channel(std::string attributeName, uint8_t intervalMinutes)
            :   attribute{ Attribute::Lookup(attributeName) },
                interval{ intervalMinutes }
        {
            if( ! intervalMinutes )
            {
                throw std::invalid_argument("RfnDataStreamingBehavior::Channel::interval set from Behavior must be greater than 0");
            }
        }

        //  Constructor for use with BehaviorReport, which will contain disabled channels
        Channel(std::string attributeName, uint8_t intervalMinutes, bool isEnabled)
            :   attribute{ Attribute::Lookup(attributeName) },
                interval{ isEnabled ? intervalMinutes : 0 }
        {}

        Attribute attribute;
        std::chrono::minutes interval;
    };

    using ChannelList = std::vector<Channel>;

    ChannelList channels;

private:

    ChannelList parseBehaviorChannels();
    ChannelList parseReportedChannels();
};

IM_EX_CONFIG bool operator<(const RfnDataStreamingBehavior::Channel& lhs, const RfnDataStreamingBehavior::Channel& rhs);
IM_EX_CONFIG std::ostream& operator<<(std::ostream& os, const RfnDataStreamingBehavior::ChannelList& channels);

}
}
