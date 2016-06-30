#pragma once

#include "yukon.h"
#include "dllbase.h"

#include <map>

namespace Cti {
namespace Behaviors {

struct DeviceBehavior
{
    enum BehaviorTypes
    {
        RfnDataStreaming
    };

    BehaviorTypes type;
    using ItemMap = std::map<std::string, std::string>;
    ItemMap parameters;
    using IndexedItems = std::vector<ItemMap>;
    using IndexedItemMap = std::map<std::string, IndexedItems>;
    IndexedItemMap indexedParameters;
};

}
}
