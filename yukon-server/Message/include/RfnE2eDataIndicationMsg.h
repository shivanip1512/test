#pragma once

#include "rfn_identifier.h"
#include "RfnE2eMsg.h"

#include <boost/optional.hpp>

#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct /*IM_EX_MSG*/ E2eDataIndicationMsg : E2eMsg  //  no methods, does not need to be exported
{
    Protocol protocol;
    unsigned char applicationServiceId;
    RfnIdentifier rfnIdentifier;
    char priority;
    boost::optional<std::string> security;
    std::vector<unsigned char> payload;
};

}
}
}
