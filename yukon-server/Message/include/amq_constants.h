#pragma once

#include <string.h>

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

namespace Broker {
    const std::string defaultURI      = "tcp://localhost:61616";

    // producerWindowSize sets the size in Bytes of messages that a producer can send before it is blocked
    // to await a ProducerAck from the broker that frees enough memory to allow another message to be sent.
    const std::string flowControlURI  = defaultURI + "?connection.producerWindowSize=1048576";
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
