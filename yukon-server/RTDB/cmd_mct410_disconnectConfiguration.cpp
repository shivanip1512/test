#include "precompiled.h"

#include "numstr.h"

#include "cmd_mct410_disconnectConfiguration.h"
#include "cmd_rfn_helper.h"
#include "dev_mct410.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct410DisconnectConfigurationCommand::Mct410DisconnectConfigurationCommand(const DisconnectMode mode, const unsigned disconnectAddress,
                                                 const float disconnectDemandThreshold, const unsigned connectDelay,
                                                 const unsigned disconnectMinutes, const unsigned connectMinutes, ReconnectButtonOptions reconnectButtonRequired,
                                                 const long demandInterval) :
    _disconnectMode(mode),
    _disconnectAddress(disconnectAddress),
    _disconnectDemandThreshold(disconnectDemandThreshold),
    _connectDelay(connectDelay),
    _disconnectMinutes(disconnectMinutes),
    _connectMinutes(connectMinutes),
    _reconnectButtonRequired(reconnectButtonRequired),
    _demandInterval(demandInterval),
    _executionState(&Mct410DisconnectConfigurationCommand::write)
{
    //  Clear out the irrelevant/overriding parameters.
    //  Load limit takes precedence, then cycling, then on-demand.
    //  So if the mode is on-demand, clear both cycling and load-limit.
    //  If the mode is cycling, clear load-limit.
    switch( _disconnectMode )
    {
        case OnDemand:
        {
            _connectMinutes    = 0;
            _disconnectMinutes = 0;
        }  //  fall through
        case Cycling:
        {
            _disconnectDemandThreshold = 0.0;
        }
    }

    validate(Condition(_disconnectAddress < (1 << 22), ClientErrors::BadParameter)
             << "Invalid disconnect address (" << _disconnectAddress << "), must be 0-4194303");

    validate(Condition(_disconnectDemandThreshold >= 0.0 && _disconnectDemandThreshold <= 400.0, ClientErrors::BadParameter)
             << "Invalid disconnect demand threshold (" << _disconnectDemandThreshold << "), must be 0.0-400.0");

    validate(Condition(_connectDelay <= 10, ClientErrors::BadParameter)
             << "Invalid connect delay (" << _connectDelay << "), must be 0-10");

    if( _disconnectMode == Cycling )
    {
        validate(Condition(_disconnectMinutes >= 5 && _disconnectMinutes <= 60, ClientErrors::BadParameter)
                 << "Invalid number of disconnect minutes (" << _disconnectMinutes << "), must be 5-60");

        validate(Condition(_connectMinutes >= 5 && _connectMinutes <= 60, ClientErrors::BadParameter)
                 << "Invalid number of connect minutes (" << _connectMinutes << "), must be 5-60");
    }

    validate(Condition(_demandInterval > 0, ClientErrors::BadParameter)
             << "Invalid demand interval (" << _demandInterval << "), must be a positive integer");
}

Mct410DisconnectConfigurationCommand::Mct410DisconnectConfigurationCommand() :
    // Default values here, we're a read anyway, so these values will be ignored.
    _disconnectAddress(0),
    _disconnectDemandThreshold(0),
    _connectDelay(5),
    _disconnectMinutes(5),
    _connectMinutes(5),
    _reconnectButtonRequired(ButtonRequired),
    _demandInterval(300),
    _executionState(&Mct410DisconnectConfigurationCommand::read)
{
}

DlcCommand::emetcon_request_ptr Mct410DisconnectConfigurationCommand::executeCommand(const CtiTime now)
{
    return doCommand();
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::decodeCommand(const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points)
{
    //  just did the read
    if( _executionState == &Mct410DisconnectConfigurationCommand::done )
    {
        if( ! payload || payload->size() < 11 )
        {
            throw CommandException(ClientErrors::InvalidData, "Payload too small");
        }

        std::ostringstream desc;

        desc << "\nConfig data received:" << std::hex << std::setfill('0');
        for each(const unsigned char byte in *payload)
        {
            desc << " " << std::setw(2) << (unsigned)byte;
        }
        description += desc.str();

        //  the rest of the disconnect configuration elements
        const unsigned dynamicDemand = (*payload)[5] << 8 | (*payload)[6];

        _returnedDisconnectDemandThreshold = dynamicDemand & 0x0fff;

        switch( dynamicDemand & 0x3000 )
        {
            case 0x3000:  *_returnedDisconnectDemandThreshold /= 10.0;
            case 0x2000:  *_returnedDisconnectDemandThreshold /= 10.0;
            case 0x1000:  *_returnedDisconnectDemandThreshold /= 10.0;
            case 0x0000:  *_returnedDisconnectDemandThreshold /= 10.0;
        }

        // adjust for the demand interval
        *_returnedDisconnectDemandThreshold *= (3600 / _demandInterval);

        const unsigned returnedDisconnectMinutes = (*payload)[9];
        const unsigned returnedConnectMinutes    = (*payload)[10];

        if( *_returnedDisconnectDemandThreshold > 0.0 )
        {
            _returnedDisconnectMode = DemandThreshold;
        }
        else if( returnedDisconnectMinutes || returnedConnectMinutes )
        {
            _returnedDisconnectMode = Cycling;
        }
        else
        {
            _returnedDisconnectMode = OnDemand;
        }
    }

    return doCommand();
}

void Mct410DisconnectConfigurationCommand::invokeResultHandler(ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}

boost::optional<float> Mct410DisconnectConfigurationCommand::getDisconnectDemandThreshold() const
{
    return _returnedDisconnectDemandThreshold;
}

boost::optional<Mct410DisconnectConfigurationCommand::DisconnectMode> Mct410DisconnectConfigurationCommand::getDisconnectMode() const
{
    return _returnedDisconnectMode;
}

DlcCommand::request_ptr Mct410DisconnectConfigurationCommand::error(const CtiTime now, const YukonError_t error_code, std::string &description)
{
    throw CommandException(error_code, GetErrorString(error_code));
}

DlcCommand::emetcon_request_ptr Mct410DisconnectConfigurationCommand::doCommand()
{
    //  call the current state's member function
    return (this->*_executionState)();
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

    if( _reconnectButtonRequired == ButtonNotRequired )
    {
        // Enable bit 2.
        configuration |= 0x04;
    }

    payload.push_back(configuration & 0xff);

    // Byte 9 - Max Use (currently intentionally omitted)

    // Bytes 10-14 - Unused

    return payload;
}

DlcCommand::emetcon_request_ptr Mct410DisconnectConfigurationCommand::write()
{
    _executionState = &Mct410DisconnectConfigurationCommand::read;

    return std::make_unique<write_request_t>(Write_Disconnect, assemblePayload());
}

DlcCommand::emetcon_request_ptr Mct410DisconnectConfigurationCommand::read()
{
    _executionState = &Mct410DisconnectConfigurationCommand::done;

    return std::make_unique<read_request_t>(Read_Disconnect, 13);
}

DlcCommand::emetcon_request_ptr Mct410DisconnectConfigurationCommand::done()
{
    return nullptr;
}

}
}
}
