#include "precompiled.h"

#include "behavior_rfnDataStreaming.h"

#include <boost/range/adaptor/transformed.hpp>
#include <boost/algorithm/string/join.hpp>

namespace Cti {
namespace Behaviors {

RfnDataStreamingBehavior::RfnDataStreamingBehavior(const long paoId, const std::map<std::string, std::string>&& parameters)
    :   DeviceBehavior{ paoId, std::move(parameters) },
        enabled{ true },  //  Behaviors stored in the DB are enabled by definition
        channels{ parseBehaviorChannels() }
{}


RfnDataStreamingBehavior::RfnDataStreamingBehavior(const long paoId, const std::map<std::string, std::string>&& parameters, behavior_report_tag)
    :   DeviceBehavior{ paoId, std::move(parameters) },
        enabled{ parseItem<bool>("enabled") },  //  The device's BehaviorReportedValue state might be disabled, though
        channels{ parseReportedChannels() }
{}


auto RfnDataStreamingBehavior::parseBehaviorChannels() -> std::vector<Channel>
{
    return parseIndexedItems<Channel>("channels",
                Param<std::string>{"attribute"},
                Param<uint8_t>    {"interval"});
                //  Behavior channels are enabled by definition
}

auto RfnDataStreamingBehavior::parseReportedChannels() -> std::vector<Channel>
{
    return parseIndexedItems<Channel>("channels",
                Param<std::string>{"attribute"},
                Param<uint8_t>    {"interval"},
                Param<bool>       {"enabled"});  //  Device's BehaviorReportedValue channels can be disabled
}


IM_EX_CONFIG bool operator<(const RfnDataStreamingBehavior::Channel& lhs, const RfnDataStreamingBehavior::Channel& rhs)
{
    return std::tie(lhs.attribute, lhs.interval) 
         < std::tie(rhs.attribute, rhs.interval);
}

IM_EX_CONFIG std::ostream& operator<<(std::ostream& os, const RfnDataStreamingBehavior::ChannelList& channels)
{
    using boost::adaptors::transformed;

    static const auto channelToString = 
        [](const RfnDataStreamingBehavior::Channel &channel) -> std::string
        {
            return channel.attribute.getName() + "@" + std::to_string(channel.interval.count()) + "min";
        };

    return os << "[" << boost::algorithm::join(channels | transformed(channelToString), ",") << "]";
}

}
}
