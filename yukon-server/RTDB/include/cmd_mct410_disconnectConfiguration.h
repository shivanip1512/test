#pragma once

#include "cmd_mct410.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct410DisconnectConfigurationCommand : public Mct410Command
{
public:

    enum DisconnectMode
    {
        OnDemand,
        DemandThreshold,
        Cycling
    };

    enum ReconnectButtonOptions
    {
        ButtonRequired,
        ButtonNotRequired,
    };

    // Write constructor
    Mct410DisconnectConfigurationCommand(const DisconnectMode mode, const unsigned disconnectAddress, const float disconnectDemandThreshold,
                            const unsigned connectDelay, const unsigned disconnectMinutes, const unsigned connectMinutes,
                            ReconnectButtonOptions reconnectButtonRequired, const long demandInterval);

    // Read constructor
    Mct410DisconnectConfigurationCommand();

    enum
    {
        Read_Disconnect  = 0x1fe,
        Write_Disconnect = 0x1fe,
    };

    emetcon_request_ptr executeCommand(const CtiTime now) override;
    request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points) override;
    request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description) override;

    boost::optional<float> getDisconnectDemandThreshold() const;
    boost::optional<DisconnectMode> getDisconnectMode() const;

private:

    DisconnectMode _disconnectMode;
    unsigned _disconnectAddress;
    float _disconnectDemandThreshold;
    unsigned _connectDelay;
    unsigned _disconnectMinutes;
    unsigned _connectMinutes;
    ReconnectButtonOptions _reconnectButtonRequired;
    long _demandInterval;

    boost::optional<float> _returnedDisconnectDemandThreshold;
    boost::optional<DisconnectMode> _returnedDisconnectMode;

    void invokeResultHandler(ResultHandler &rh) const override;

protected:

    std::vector<unsigned char> assemblePayload();

    using state_t = emetcon_request_ptr(Mct410DisconnectConfigurationCommand::*)(void);

    state_t _executionState;

    emetcon_request_ptr read();
    emetcon_request_ptr write();
    emetcon_request_ptr done();

    emetcon_request_ptr doCommand();
};

}
}
}

