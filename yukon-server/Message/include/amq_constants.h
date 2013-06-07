#pragma once

#include <string.h>

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

namespace Broker {
    const std::string defaultURI     = "tcp://localhost:61616";

    // Use failover with a maximum attempt of 120 with 30 sec of maximum delay in between.
    // This should approximate 1 hour. We should reconnect with the delay 1,2,4,8,16,30,30 ...
    const std::string startupReconnectURI =
                                        "failover:(" + defaultURI + ")"
                                        "?useExponentialBackOff=true"
                                        "&initialReconnectDelay=1000"
                                        "&maxReconnectDelay=30000"
                                        "&startupMaxReconnectAttempts=120"
                                        "&maxReconnectAttempts=1";
}

namespace MessageType {
    const std::string prefix          = "com.cooper.eas.yukon.";

    const std::string clientInit      = prefix + "clientinit";
    const std::string serverResp      = prefix + "serverresp";
}

namespace Queue {
    const std::string prefix          = "com.cooper.eas.yukon.";

    const std::string dispatch        = prefix + "dispatch";
    const std::string pil             = prefix + "pil";
    const std::string notification    = prefix + "notification";
    const std::string macs            = prefix + "macs";
    const std::string capcontrol      = prefix + "capcontrol";
    const std::string loadmanagement  = prefix + "loadmanagement";
}


}
}
}
