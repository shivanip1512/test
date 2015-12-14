#pragma once

#include <string.h>

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

namespace Broker {
    // Listener URI
    const std::string defaultURI = "tcp://localhost:61616";

    // Client URI
    const std::string flowControlURI = defaultURI;
}

namespace MessageType {
    const std::string prefix          = "com.eaton.eas.yukon.";

    const std::string clientInit      = prefix + "clientinit";
    const std::string serverResp      = prefix + "serverresp";
    const std::string clientAck       = prefix + "clientack";
}

namespace Queue {
    const std::string prefix          = "com.eaton.eas.yukon.";

    const std::string dispatch        = prefix + "dispatch";
    const std::string porter          = prefix + "porter";
    const std::string notification    = prefix + "notification";
    const std::string macs            = prefix + "macs";
    const std::string capcontrol      = prefix + "capcontrol";
    const std::string loadmanagement  = prefix + "loadmanagement";
}


}
}
}
