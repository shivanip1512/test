#include "precompiled.h"

#include "behavior_rfnDataStreaming.h"

#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/counting_range.hpp>

namespace Cti {
namespace Behaviors {

RfnDataStreamingBehavior::RfnDataStreamingBehavior(const long paoId, const std::map<std::string, std::string>&& parameters) 
    :   DeviceBehavior { paoId, std::move(parameters) },
        enabled  { parseItem<bool>("enabled") },
        channels { parseChannels() }
{}


auto RfnDataStreamingBehavior::parseChannels() -> std::vector<Channel>
{
    return parseIndexedItems<Channel>("channels", 
                Param<std::string>{"attribute"}, 
                Param<uint8_t>    {"interval"});
}

}
}
