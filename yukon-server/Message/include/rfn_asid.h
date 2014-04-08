#pragma once

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

struct IM_EX_MSG ApplicationServiceIdentifiers
{
    const unsigned char value;

    static const ApplicationServiceIdentifiers ChannelManager;
    static const ApplicationServiceIdentifiers EventManager;
    static const ApplicationServiceIdentifiers HubMeterCommandSet;
    static const ApplicationServiceIdentifiers E2EDT;
    static const ApplicationServiceIdentifiers E2EAP_DNP3;

private:

    ApplicationServiceIdentifiers(unsigned char value);
};

}
}
}
