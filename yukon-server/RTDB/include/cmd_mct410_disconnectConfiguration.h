#pragma once

#include "cmd_mct410.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct410DisconnectConfigurationCommand : public Mct410Command
{
public:

    enum ReconnectButtonState
    {
        Enabled,
        Disabled,
    };

    // Write constructor
    Mct410DisconnectConfigurationCommand(const unsigned disconnectAddress, const float disconnectDemandThreshold, const unsigned connectDelay, 
                            const unsigned disconnectMinutes, const unsigned connectMinutes, ReconnectButtonState reconnectButtonState,
                            const long demandInterval);

    // Read constructor
    Mct410DisconnectConfigurationCommand();

    enum
    {
        Read_Disconnect  = 0x1fe,
        Write_Disconnect = 0x1fe,
    };

    virtual request_ptr executeCommand(const CtiTime now);
    virtual request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error         (const CtiTime now, const int error_code, std::string &description);

private:

    unsigned _disconnectAddress;
    float _disconnectDemandThreshold;
    unsigned _connectDelay;
    unsigned _disconnectMinutes;
    unsigned _connectMinutes;
    ReconnectButtonState _reconnectButtonState;
    long _demandInterval;

protected:

    std::vector<unsigned char> assemblePayload();

    typedef boost::function<request_ptr (Mct410DisconnectConfigurationCommand *)> state_t;

    state_t _executionState;

    request_ptr read();
    request_ptr write();
    request_ptr done();

    request_ptr doCommand();
};

}
}
}

