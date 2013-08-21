#pragma once

#include "rfn_identifier.h"

#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct IM_EX_MSG RfnE2eDataRequestMsg
{
    std::string applicationServiceId;
    Devices::RfnIdentifier rfnIdentifier;
    int priority;
    std::string security;
    std::vector<unsigned char> payload;
};

}
}
}
