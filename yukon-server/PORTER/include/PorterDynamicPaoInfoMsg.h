#pragma once

#include "ctitime.h"
#include "tbl_dyn_paoinfo.h"

#include <boost/optional.hpp>
#include <boost/variant/variant.hpp>

#include <string>
#include <set>
#include <map>

namespace Cti {
namespace Messaging {
namespace Porter {

enum class DynamicPaoInfoKeys
{
    RfnVoltageProfileEnabledUntil,
    RfnVoltageProfileInterval  //  data streaming interval
};

struct PorterDynamicPaoInfoRequestMsg
{
    long deviceId;

    std::set<DynamicPaoInfoKeys> paoInfoKeys;
};

struct PorterDynamicPaoInfoResponseMsg
{
    using AllowedTypes = boost::variant<long long, CtiTime, std::string>;

    long deviceId;

    std::map<DynamicPaoInfoKeys, AllowedTypes> result;
};

}
}
}
