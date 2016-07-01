#pragma once

#include "yukon.h"
#include "dllbase.h"

#include <map>

namespace Cti {
namespace Behaviors {

class IM_EX_CONFIG DeviceBehavior
{
public:
    using ItemMap = std::map<std::string, std::string>;

protected:
    DeviceBehavior(const long paoId, const std::map<std::string, std::string>&& parameters);

    template <typename T>
    T parseItem(const std::string& itemName);

    struct IndexedItemDescriptor 
    {
        std::string itemName;
        size_t itemCount;
    };

    IndexedItemDescriptor getIndexedItemDescriptor(const std::string &itemName);

    template <typename T>
    T parseItem(const IndexedItemDescriptor &descriptor, const size_t index, const std::string& subitemName);

private:

    std::string get(const std::string& itemName);

    const ItemMap _parameters;
    const long _paoId;
};

struct BehaviorItemNotFoundException : std::exception
{
    std::string err;

    BehaviorItemNotFoundException(long paoId, std::string type)
        :   err { "Behavior item \"" + type + "\" not found for paoId " + std::to_string(paoId) }
    {}

    const char* what() const override 
    {
        return err.c_str();
    }
};

}
}
