#include "precompiled.h"

#include "cmd_rfn_DemandInterval.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

using namespace std::chrono_literals;

namespace Cti::Devices::Commands {

//  unused
unsigned char RfnDemandIntervalCommand::getOperation()   const {  return {};  }
unsigned char RfnDemandIntervalCommand::getCommandCode() const {  return {};  }


RfnDemandIntervalSetConfigurationCommand::RfnDemandIntervalSetConfigurationCommand( std::chrono::minutes interval )
    : demandInterval { interval }
{
    validate( Condition( demandInterval > 0min, ClientErrors::BadParameter ) 
        << "Invalid interval, must be greater than 0 (" << demandInterval << ")");

    validate( Condition( demandInterval < 61min, ClientErrors::BadParameter)
        << "Invalid interval, must be less than 61 (" << demandInterval << ")");
}


auto RfnDemandIntervalSetConfigurationCommand::getCommandHeader() -> Bytes
{
    return { static_cast<uint8_t>(Command::Request) };
}


auto RfnDemandIntervalSetConfigurationCommand::getCommandData() -> Bytes
{
    return { static_cast<uint8_t>( demandInterval.count() ) };
}


RfnCommandResult RfnDemandIntervalSetConfigurationCommand::decodeCommand( const CtiTime now, const RfnResponsePayload & response )
{
    // We need at least 2 bytes

    validate( Condition( response.size() >= 2, ClientErrors::DataMissing )
            << "Invalid Response length (" << response.size() << ")" );

    validate( Condition( response[0] == Command::Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == 0, ClientErrors::Abnormal )
            << "Set command failed with error code " << std::to_string(response[1]) );

    return { (StreamBuffer() << "Successfully set demand interval to " << demandInterval).extractToString() };
}


std::string RfnDemandIntervalSetConfigurationCommand::getCommandName()
{
    return "Set Demand Interval Request";
}


auto RfnDemandIntervalGetConfigurationCommand::getCommandHeader() -> Bytes
{
    return { static_cast<uint8_t>(Command::Request) };
}


auto RfnDemandIntervalGetConfigurationCommand::getCommandData() -> Bytes
{
    return {};
}


RfnCommandResult RfnDemandIntervalGetConfigurationCommand::decodeCommand( const CtiTime now, const RfnResponsePayload & response )
{
    // We need at least 2 bytes

    validate( Condition( response.size() >= 2, ClientErrors::DataMissing )
            << "Invalid Response length (" << response.size() << ")" );

    validate( Condition( response[0] == Command::Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    _interval = std::chrono::minutes( response[1] );
    
    validate( Condition( _interval > 0min, ClientErrors::BadParameter )
            << "Invalid demand interval " << _interval );

    validate( Condition( _interval < 61min, ClientErrors::BadParameter )
            << "Invalid demand interval " << _interval);

    return { (StreamBuffer() << "Demand interval: " << _interval).extractToString() };
}


std::chrono::minutes RfnDemandIntervalGetConfigurationCommand::getDemandInterval() const
{
    return _interval;
}


std::string RfnDemandIntervalGetConfigurationCommand::getCommandName()
{
    return "Get Demand Interval Request";
}


}