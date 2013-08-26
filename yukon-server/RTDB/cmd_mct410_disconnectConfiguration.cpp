#include "precompiled.h"

#include "cmd_mct410_disconnectConfiguration.h"
#include "dev_mct410.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct410DisconnectConfigurationCommand::Mct410DisconnectConfigurationCommand(const unsigned disconnectAddress, const float disconnectDemandThreshold, const unsigned connectDelay,
                                                 const unsigned disconnectMinutes, const unsigned connectMinutes, ReconnectButtonState reconnectButtonState,
                                                 const long demandInterval) :
    _disconnectAddress(disconnectAddress),
    _disconnectDemandThreshold(disconnectDemandThreshold),
    _connectDelay(connectDelay),
    _disconnectMinutes(disconnectMinutes),
    _connectMinutes(connectMinutes),
    _reconnectButtonState(reconnectButtonState),
    _demandInterval(demandInterval),
    _executionState(&Mct410DisconnectConfigurationCommand::write)
{
    if( _disconnectAddress >> 22 )
    {
        throw CommandException(BADPARAM, "Invalid disconnect address (" + CtiNumStr(_disconnectAddress) + "), must be 0-4194303");
    }
    if( _disconnectDemandThreshold < 0.0 || _disconnectDemandThreshold > 400.0 )
    {
        throw CommandException(BADPARAM, "Invalid disconnect demand threshold (" + CtiNumStr(_disconnectDemandThreshold, 1) + "), must be 0.0-400.0");
    }
    if( _connectDelay > 10 )
    {
        throw CommandException(BADPARAM, "Invalid connect delay (" + CtiNumStr(_connectDelay) + "), must be 0-10");
    }
    if( _disconnectMinutes < 5 || _disconnectMinutes > 60 )
    {
        throw CommandException(BADPARAM, "Invalid number of disconnect minutes (" + CtiNumStr(_disconnectMinutes) + "), must be 5-60");
    }
    if( _connectMinutes < 5 || _connectMinutes > 60 )
    {
        throw CommandException(BADPARAM, "Invalid number of connect minutes (" + CtiNumStr(_connectMinutes) + "), must be 5-60");
    }
    if( _demandInterval <= 0 )
    {
        throw CommandException(BADPARAM, "Invalid demand interval (" + CtiNumStr(_demandInterval) + "), must be a positive integer");
    }
}

Mct410DisconnectConfigurationCommand::Mct410DisconnectConfigurationCommand() :
    // Default values here, we're a read anyway, so these values will be ignored.
    _disconnectAddress(0),
    _disconnectDemandThreshold(0),
    _connectDelay(5),
    _disconnectMinutes(5),
    _connectMinutes(5),
    _reconnectButtonState(Enabled),
    _demandInterval(300),
    _executionState(&Mct410DisconnectConfigurationCommand::read)
{
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::executeCommand(const CtiTime now)
{
    return doCommand();
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::decodeCommand(const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points)
{
    //  just did the read
    if( _executionState == &Mct410DisconnectConfigurationCommand::done )
    {
        if( payload )
        {
            //  the rest of the disconnect configuration elements
            unsigned dynamicDemand = (*payload)[5] << 8 | (*payload)[6];

            _returnedDisconnectDemandThreshold = dynamicDemand & 0x0fff;

            switch( dynamicDemand & 0x3000 )
            {
                case 0x3000:  *_returnedDisconnectDemandThreshold /= 10.0;
                case 0x2000:  *_returnedDisconnectDemandThreshold /= 10.0;
                case 0x1000:  *_returnedDisconnectDemandThreshold /= 10.0;
                case 0x0000:  *_returnedDisconnectDemandThreshold /= 10.0;
            }
        }
    }

    return doCommand();
}

boost::optional<float> Mct410DisconnectConfigurationCommand::getDisconnectDemandThreshold() const
{
    return _disconnectDemandThreshold;
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::error(const CtiTime now, const int error_code, std::string &description)
{
    throw CommandException(error_code, GetErrorString(error_code));
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::doCommand()
{
    //  call the current state's member function
    return _executionState(this);
}

std::vector<unsigned char> Mct410DisconnectConfigurationCommand::assemblePayload()
{
    std::vector<unsigned char> payload;

    // Bytes 0-2 - Disconnect Address
    payload.push_back((_disconnectAddress >> 16) & 0xff);
    payload.push_back((_disconnectAddress >>  8) & 0xff);
    payload.push_back( _disconnectAddress        & 0xff);

    // Bytes 3-4 - Disconnect Demand Threshold
    _disconnectDemandThreshold *= 1000.0; // Convert the kW value to Watts
    _disconnectDemandThreshold /= (3600 / _demandInterval);

    const int dynamicDemandThreshold = Mct410Device::Utility::makeDynamicDemand(_disconnectDemandThreshold);

    payload.push_back((dynamicDemandThreshold >>  8) & 0xff);
    payload.push_back( dynamicDemandThreshold        & 0xff);

    // Byte 5 - Disconnect Load Limit Connect Delay
    payload.push_back(_connectDelay & 0xff);

    // Byte 6 - Disconnect Minutes
    payload.push_back(_disconnectMinutes & 0xff);

    // Byte 7 - Connect Minutes
    payload.push_back(_connectMinutes & 0xff);

    // Byte 8 - Configuration Byte - force rev E disconnect true.
    unsigned char configuration = 0x40;

    if( _reconnectButtonState == Disabled )
    {
        // Enable bit 2.
        configuration |= 0x04;
    }

    payload.push_back(configuration & 0xff);

    // Byte 9 - Max Use (currently intentionally omitted)

    // Bytes 10-14 - Unused

    return payload;
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::write()
{
    _executionState = &Mct410DisconnectConfigurationCommand::read;

    return request_ptr(new write_request_t(Write_Disconnect, assemblePayload()));
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::read()
{
    _executionState = &Mct410DisconnectConfigurationCommand::done;

    return request_ptr(new read_request_t(Read_Disconnect, 13));
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::done()
{
    return request_ptr();
}

}
}
}
