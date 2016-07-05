#include "precompiled.h"

#include "behavior_device.h"
#include "std_helper.h"

namespace Cti {
namespace Behaviors {

DeviceBehavior::DeviceBehavior(const long paoId, const std::map<std::string, std::string>&& parameters)
    :   _paoId(paoId),
        _parameters(std::move(parameters))
{
}

std::string DeviceBehavior::get(const std::string& itemName)
{
    if( auto item = Cti::mapFind(_parameters, itemName) )
    {
        return *item;
    }
    throw BehaviorItemNotFoundException(_paoId, itemName);
}

template <> IM_EX_CONFIG
std::string DeviceBehavior::parseItem(const std::string& itemName)
{
    return get(itemName);
}

template <> IM_EX_CONFIG
unsigned long DeviceBehavior::parseItem(const std::string& itemName)
{
    return std::stoul(get(itemName));
}

template <> IM_EX_CONFIG
uint8_t DeviceBehavior::parseItem(const std::string& itemName)
{
    auto asUnsignedLong = parseItem<unsigned long>(itemName);

    if( asUnsignedLong <= std::numeric_limits<uint8_t>::max() )
    {
        return static_cast<uint8_t>(asUnsignedLong);
    }
    throw std::out_of_range(std::to_string(asUnsignedLong));
}

template <> IM_EX_CONFIG
bool DeviceBehavior::parseItem(const std::string& itemName)
{
    auto item = get(itemName);

    if( item == "true" )
    {
        return true;
    }
    if( item == "false" )
    {
        return false;
    }
    throw std::invalid_argument(item);
}

std::string DeviceBehavior::makeIndexedName(const std::string& baseName, const size_t index, const std::string& subitemName)
{
    return baseName + "." + std::to_string(index) + "." + subitemName;
}

}
}
