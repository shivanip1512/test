#include "precompiled.h"

#include "behavior_rfnDataStreaming.h"


namespace Cti {
namespace Behaviors {

RfnDataStreamingBehavior::RfnDataStreamingBehavior(const long paoId, const std::map<std::string, std::string>&& parameters) 
    :   DeviceBehavior { paoId, std::move(parameters) },
        enabled  { parseItem<bool>("enabled") },
        channels { parseChannels() }
{}


auto RfnDataStreamingBehavior::parseChannels() -> std::vector<Channel>
{
    const auto channels = getIndexedItemDescriptor("channels");

    std::vector<Channel> parsedChannels;

    for( size_t i = 0; i < channels.itemCount; ++i )
    {
        parsedChannels.emplace_back(
            parseItem<std::string>(channels, i, "attribute"), 
            parseItem<uint8_t>    (channels, i, "interval"));
    }

    return parsedChannels;
}

}
}
