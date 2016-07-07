#pragma once

#include "yukon.h"
#include "dllbase.h"

#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/counting_range.hpp>

#include <map>

namespace Cti {
namespace Behaviors {

class IM_EX_CONFIG DeviceBehavior
{
public:
    using ItemMap = std::map<std::string, std::string>;
    
    struct behavior_report_tag {};

protected:
    DeviceBehavior(const long paoId, const std::map<std::string, std::string>&& parameters);

    template<typename T>
    T parseItem(const std::string& itemName);

    struct IndexedItemDescriptor 
    {
        std::string itemName;
        size_t itemCount;
    };

    template<typename T>
    struct Param
    {
        const char* name;

        Param(const char* behaviorValueName) : name(behaviorValueName) {}
    };

    template<typename T, class ...Types>
    std::vector<T> parseIndexedItems(const std::string& itemName, Types... items)  //  This should technically be Param<Types>... items, but that caused an ICE in VS2015u2.  Maybe try again in u3?
    {
        const auto itemCount = parseItem<unsigned long>(itemName);

        return 
            boost::copy_range<std::vector<T>>(
                boost::counting_range<size_t>(0, itemCount)
                    | boost::adaptors::transformed([this, itemName, &items...](size_t idx) {
                            return T { parseIndexedItem(itemName, idx, items)... }; }));
    }

private:

    std::string get(const std::string& itemName);

    std::string makeIndexedName(const std::string& baseName, const size_t index, const std::string& subitemName);

    template<typename T>
    T parseIndexedItem(const std::string& base, const size_t idx, Param<T> value)
    {
        return parseItem<T>(makeIndexedName(base, idx, value.name));
    }

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
