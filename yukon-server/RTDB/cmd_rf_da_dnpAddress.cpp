#include "precompiled.h"

#include "cmd_rf_da_dnpAddress.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>


namespace Cti {
namespace Devices {
namespace Commands {


using Messaging::Rfn::ApplicationServiceIdentifiers;

// Defaults to Advanced Metrology, which operates via Channel Manager
ApplicationServiceIdentifiers RfDaReadDnpSlaveAddressCommand::getApplicationServiceId() const
{
    return ApplicationServiceIdentifiers::HubMeterCommandSet;
}

RfnCommand::Bytes RfDaReadDnpSlaveAddressCommand::getCommandHeader()
{
    return Bytes( 1, getCommandCode() );
}

unsigned char RfDaReadDnpSlaveAddressCommand::getCommandCode() const
{
    return CommandCode_Request;
}

//  unused
unsigned char RfDaReadDnpSlaveAddressCommand::getOperation() const
{
    return 0x00;
}

RfnCommand::Bytes RfDaReadDnpSlaveAddressCommand::getCommandData()
{
    return Bytes();
}

unsigned short RfDaReadDnpSlaveAddressCommand::getDnpSlaveAddress() const
{
    return _dnp3SlaveAddress;
}


void RfDaReadDnpSlaveAddressCommand::invokeResultHandler( ResultHandler & rh ) const
{
    rh.handleCommandResult( *this );
}


RfnCommandResult RfDaReadDnpSlaveAddressCommand::decodeCommand( const CtiTime now,
                                                                 const RfnResponsePayload & response )
{
    // We need 3 bytes

    validate( Condition( response.size() == 3, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate response code

    validate( Condition( response[0] == CommandCode_Response, ErrorInvalidData )
            << "Invalid response code (" << (unsigned)response[0] << ")" );

    _dnp3SlaveAddress = (response[1] << 8) | response[2];

    return "Outstation DNP3 address: " + CtiNumStr(_dnp3SlaveAddress);
}


}
}
}

