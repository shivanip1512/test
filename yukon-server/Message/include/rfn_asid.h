#pragma once

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

struct IM_EX_MSG ApplicationServiceIdentifiers
{
    unsigned char value;

    ApplicationServiceIdentifiers() : value(0) {}

    ApplicationServiceIdentifiers(unsigned char value_) : value(value_) {}

    bool operator<(const ApplicationServiceIdentifiers &rhs) const
    {
        return value < rhs.value;
    }

    static const ApplicationServiceIdentifiers ChannelManager;
    static const ApplicationServiceIdentifiers EventManager;
    static const ApplicationServiceIdentifiers E2EDT;
    static const ApplicationServiceIdentifiers E2EAP_DNP3;
};

}
}
}
