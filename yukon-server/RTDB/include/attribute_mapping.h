#pragma once

#include "Attribute.h"
#include "pt_status.h"

#include "config_device.h"

#include "dev_base.h"

#include <boost/variant.hpp>

namespace Cti {
namespace Devices {

class AttributeMapping
{
public:

    using PointSource = std::function<CtiPointSPtr(const std::string&)>;

    AttributeMapping(PointSource pointSource);
    
    using AttributeList = std::vector<Attribute>;

    void refresh(Config::DeviceConfigSPtr deviceConfig, const AttributeList& mappableAttributes);

    using OptionalInt = boost::optional<int>;

    //  These throw YukonErrorException
    auto getControlOffset   (const Attribute attribute) -> int;
    auto getPointOffset     (const Attribute attribute) -> int;

    //  These do not throw
    auto findControlOffset(const Attribute attribute) -> OptionalInt;
    auto findPointOffset  (const Attribute attribute) -> OptionalInt;

private:

    template<typename Value>
    using Expected = boost::variant<std::string, Value>;

    using IntExpected         = Expected<int>;

    auto tryGetControlOffset(const Attribute attribute) -> IntExpected;
    auto tryGetPointOffset  (const Attribute attribute) -> IntExpected;

    std::map<Attribute, std::string> _overrides;

    size_t _lastConfigId = 0;

    PointSource _pointSource;
};

}
}
