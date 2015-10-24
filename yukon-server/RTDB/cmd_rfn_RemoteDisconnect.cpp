#include "precompiled.h"

#include "cmd_rfn_RemoteDisconnect.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/assign/list_of.hpp>

namespace Cti        {
namespace Devices    {
namespace Commands   {

namespace   { // anonymous namespace

const std::map<unsigned char, std::string>  remoteDisconnectStatusResolver = boost::assign::map_list_of
    ( 0, "Success" )
    ( 1, "Failure" );

typedef RfnRemoteDisconnectConfigurationCommand BaseCmd;
typedef BaseCmd::DemandInterval DemandInterval;
typedef BaseCmd::DisconnectMode DisconnectMode;
typedef BaseCmd::Reconnect      Reconnect;

const std::map<DisconnectMode, std::string>  disconnectModeResolver = boost::assign::map_list_of
    ( BaseCmd::DisconnectMode_OnDemand,        "On Demand" )
    ( BaseCmd::DisconnectMode_DemandThreshold, "Demand Threshold" )
    ( BaseCmd::DisconnectMode_Cycling,         "Cycling" )
    ;

const std::map<unsigned char, DemandInterval> remoteDisconnectIntervalResolver = boost::assign::map_list_of
    (  5, BaseCmd::DemandInterval_Five    )
    ( 10, BaseCmd::DemandInterval_Ten     )
    ( 15, BaseCmd::DemandInterval_Fifteen );

const std::map<unsigned char, Reconnect>  remoteDisconnectReconnectResolver = boost::assign::map_list_of
    ( 0, BaseCmd::Reconnect_Arm       )
    ( 1, BaseCmd::Reconnect_Immediate );

const std::map<unsigned char, DisconnectMode> disconnectModes = boost::assign::map_list_of
    ( 1, BaseCmd::DisconnectMode_OnDemand        )
    ( 2, BaseCmd::DisconnectMode_DemandThreshold )
    ( 3, BaseCmd::DisconnectMode_Cycling         )
    ;

} // anonymous namespace

//-----------------------------------------------------------
// Remote Disconnect Command Constructor
//-----------------------------------------------------------
RfnRemoteDisconnectConfigurationCommand::RfnRemoteDisconnectConfigurationCommand( const Operation operation )
    :   _operation( operation )
{
    // Empty
}

unsigned char RfnRemoteDisconnectConfigurationCommand::getCommandCode() const
{
    return CommandCode_Request;
}

unsigned char RfnRemoteDisconnectConfigurationCommand::getOperation() const
{
    return _operation;
}

RfnCommandResult RfnRemoteDisconnectConfigurationCommand::decodeResponseHeader( const CtiTime now, const RfnResponsePayload & response )
{
    RfnCommandResult result;

    // We need at least three bytes
    validate( Condition( response.size() >= 3, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the first 3 bytes
    validate( Condition( response[0] == CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == _operation, ClientErrors::InvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    boost::optional<std::string> status = mapFind( remoteDisconnectStatusResolver, response[2] );

    // invalid status byte -- not found in map
    validate( Condition( !! status, ClientErrors::InvalidData )
            << "Invalid Status (" << response[2] << ")" );

    validate( Condition( response[2] == 0, ClientErrors::InvalidData ) // success
            << "Status: " << *status << " (" << response[2] << ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";

    return result;
}

BaseCmd::TlvList RfnRemoteDisconnectConfigurationCommand::getTlvsFromPayload( const RfnResponsePayload & response )
{
    validate( Condition( response.size() >= 5, ClientErrors::InvalidData )
            << "Response too small (" << response.size() << " < 5)" );

    const RfnResponsePayload payload( response.begin() + 4, response.end() );

    return getTlvsFromBytes( payload );
}

RfnCommand::Bytes RfnRemoteDisconnectConfigurationCommand::getCommandData()
{
    return getBytesFromTlvs( getTlvs() );
}

BaseCmd::TlvList RfnRemoteDisconnectConfigurationCommand::getTlvs()
{
    return TlvList();
}

std::string RfnRemoteDisconnectConfigurationCommand::decodeDisconnectConfigTlv( TypeLengthValue tlv )
{
    std::ostringstream description;

    _disconnectMode = mapFind( disconnectModes, tlv.type );

    validate( Condition( !! _disconnectMode, ClientErrors::InvalidData )
            << "Invalid TLV type received in response (" << tlv.type << ")" );

    description << "\nDisconnect mode: " << *mapFind( disconnectModeResolver, *_disconnectMode );

    // Byte 0 in all three tlv types
    boost::optional<Reconnect> reconnect = mapFind( remoteDisconnectReconnectResolver, tlv.value[0] );

    // invalid reconnect byte -- not found in map
    validate( Condition( !! reconnect, ClientErrors::InvalidData )
            << "Response reconnect param invalid (" << tlv.value[0]
            << ") expecting 0 or 1" );

    _reconnectParam = *reconnect;

    std::string reconnectStr = _reconnectParam == Reconnect_Arm ? "Arm" : "Immediate";
    description << "\nReconnect param: " << reconnectStr << " reconnect";

    switch( tlv.type )
    {
        case DisconnectMode_DemandThreshold:
        {
            validate( Condition( tlv.value.size() == 5, ClientErrors::InvalidData )
            << "Response TLV too small (" << tlv.value.size() << " != 5)");

            // Byte 1 - demand interval
            boost::optional<DemandInterval> interval = mapFind( remoteDisconnectIntervalResolver, tlv.value[1] );

            _demandInterval = *interval;

            description << "\nDisconnect demand interval: " << CtiNumStr( tlv.value[1] ) << " minutes";

            if( ! interval )
            {
                // invalid interval - not found in map, but we store it anyway
                description << ", invalid (expecting 5, 10, or 15)";
            }

            // Byte 2 - demand threshold
            const unsigned hectoWattThreshold = tlv.value[2];

            _demandThreshold = hectoWattThreshold / 10.0; // Convert to kW.

            description << "\nDisconnect demand threshold: " << CtiNumStr( *_demandThreshold, 1 ) << " kW";

            if( hectoWattThreshold > 120 )
            {
                description << ", invalid (expecting <= 120)";
            }

            // Byte 3 - connect delay
            _connectDelay = tlv.value[3];

            description << "\nConnect delay: " << CtiNumStr( *_connectDelay ) << " minutes";

            if( *_connectDelay > 30 )
            {
                description << ", invalid (expecting <= 30)";
            }

            // Byte 4 - max disconnects
            _maxDisconnects = tlv.value[4];

            const std::string disconnectsStr = CtiNumStr( *_maxDisconnects );
            description << "\nMax disconnects: " << ( *_maxDisconnects ? disconnectsStr : "disable" );

            if( *_maxDisconnects > 20 )
            {
                description << ", invalid (expecting <= 20)";
            }

            break;
        }
        case DisconnectMode_Cycling:
        {
            validate( Condition( tlv.value.size() == 5, ClientErrors::InvalidData )
                    << "Response TLV too small (" << tlv.value.size() << " != 5)" );

            const boost::optional<Reconnect> reconnectParam = getReconnectParam();

            validate( Condition( !! reconnectParam, ClientErrors::DataMissing ) // must be 1 for cycling!
                    << "Response reconnect param missing, expecting 1)" );

            validate( Condition( reconnectParam == Reconnect_Immediate, ClientErrors::InvalidData ) // must be 1 for cycling!
                    << "Response reconnect param invalid " << *reconnectParam
                    << ") expecting 1)" );

            // Bytes 1-2 : disconnect minutes
            _disconnectMinutes = tlv.value[1] << 8 | tlv.value[2];

            description << "\nDisconnect minutes: " << CtiNumStr( *_disconnectMinutes );

            if( *_disconnectMinutes < 5 )
            {
                description << ", invalid (expecting >= 5)";
            }
            else if( *_disconnectMinutes > 1440 )
            {
                description << ", invalid (expecting <= 1440)";
            }

            // Bytes 3-4 : connect minutes
            _connectMinutes = tlv.value[3] << 8 | tlv.value[4];

            description << "\nConnect minutes: " << CtiNumStr( *_connectMinutes );

            if( *_connectMinutes < 5 )
            {
                description << ", invalid (expecting >= 5)";
            }
            else if( *_connectMinutes > 1440 )
            {
                description << ", invalid (expecting <= 1440)";
            }

            break;
        }
    }

    return description.str();
}


boost::optional<Reconnect>      RfnRemoteDisconnectConfigurationCommand::getReconnectParam() const  {  return _reconnectParam;  }
boost::optional<DisconnectMode> RfnRemoteDisconnectConfigurationCommand::getDisconnectMode() const  {  return _disconnectMode;  }

boost::optional<unsigned> RfnRemoteDisconnectConfigurationCommand::getDemandInterval()    const  {  return _demandInterval;     }
boost::optional<double>   RfnRemoteDisconnectConfigurationCommand::getDemandThreshold()   const  {  return _demandThreshold;    }
boost::optional<unsigned> RfnRemoteDisconnectConfigurationCommand::getConnectDelay()      const  {  return _connectDelay;       }
boost::optional<unsigned> RfnRemoteDisconnectConfigurationCommand::getMaxDisconnects()    const  {  return _maxDisconnects;     }
boost::optional<unsigned> RfnRemoteDisconnectConfigurationCommand::getDisconnectMinutes() const  {  return _disconnectMinutes;  }
boost::optional<unsigned> RfnRemoteDisconnectConfigurationCommand::getConnectMinutes()    const  {  return _connectMinutes;     }

//-----------------------------------------------------------
// Remote Disconnect Set-Configuration Functions
//-----------------------------------------------------------
RfnRemoteDisconnectSetConfigurationCommand::RfnRemoteDisconnectSetConfigurationCommand()
    :   RfnRemoteDisconnectConfigurationCommand( Operation_SetConfiguration )
{
    // Empty
}

RfnCommandResult RfnRemoteDisconnectSetConfigurationCommand::decodeCommand( const CtiTime now, const RfnResponsePayload &response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    // We're a success, we should have a current disconnect mode and TLV.
    validate( Condition( response.size() >= 4, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    currentDisconnectMode = response[3];

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    result.description += decodeDisconnectConfigTlv( tlvs[0] );

    return result;
}


BaseCmd::TlvList RfnRemoteDisconnectSetConfigurationCommand::getTlvs()
{
    RfnCommand::Bytes data = getData();

    TypeLengthValue tlv( getConfigurationDisconnectMode(), data );

    return boost::assign::list_of(tlv);
}

//-----------------------------------------------------------
// On-Demand Disconnect Set-Configuration Functions
//-----------------------------------------------------------
RfnRemoteDisconnectSetOnDemandConfigurationCommand::RfnRemoteDisconnectSetOnDemandConfigurationCommand( const Reconnect reconnect_param )
    :   reconnectParam( reconnect_param )
{
    // Empty
}

DisconnectMode RfnRemoteDisconnectSetOnDemandConfigurationCommand::getConfigurationDisconnectMode() const
{
    return DisconnectMode_OnDemand;
}

RfnCommand::Bytes RfnRemoteDisconnectSetOnDemandConfigurationCommand::getData()
{
    RfnCommand::Bytes data;

    data.push_back( reconnectParam );

    return data;
}

//-----------------------------------------------------------
// Demand Threshold Disconnect Configuration Functions
//-----------------------------------------------------------
RfnRemoteDisconnectSetThresholdConfigurationCommand::RfnRemoteDisconnectSetThresholdConfigurationCommand( const Reconnect      reconnect_param,
                                                                                                          const DemandInterval demand_interval,
                                                                                                          const double         demand_threshold,
                                                                                                          const unsigned       connect_delay,
                                                                                                          const unsigned       max_disconnects )
    :   reconnectParam( reconnect_param ),
        demandInterval( demand_interval ),
        demandThreshold( demand_threshold ),
        connectDelay( connect_delay ),
        maxDisconnects( max_disconnects )
{
    std::string underVal = CtiNumStr( demand_threshold, 1 );
    validate( Condition( demand_threshold >= 0.5, ClientErrors::BadParameter )
            << "Invalid Demand Threshold: (" << underVal
            << ") underflow (minimum 0.5)" );

    std::string overVal = CtiNumStr( demand_threshold, 1 );
    validate( Condition( demand_threshold <= 12.0, ClientErrors::BadParameter )
            << "Invalid Demand Threshold: (" << overVal
            << ") overflow (maximum 12.0)" );

    validate( Condition( connect_delay <= 30, ClientErrors::BadParameter )
            << "Invalid Connect Delay: (" << connect_delay
            << ") overflow (maximum 30)" );

    validate( Condition( max_disconnects <= 20, ClientErrors::BadParameter )
            << "Invalid Max Disconnects: (" << max_disconnects
            << ") overflow (maximum 20)" );
}

DisconnectMode RfnRemoteDisconnectSetThresholdConfigurationCommand::getConfigurationDisconnectMode() const
{
    return DisconnectMode_DemandThreshold;
}

RfnCommand::Bytes RfnRemoteDisconnectSetThresholdConfigurationCommand::getData()
{
    RfnCommand::Bytes data;

    // This calculation should always yield a whole number because of the
    // restriction of one digit after the decimal place. If that isn't the
    // case, this calculation ends up rounding the number down to the
    // nearest whole (i.e. 10 * 7.29 cast to unsigned char would yield 72).
    const unsigned char hw_threshold = demandThreshold * 10;

    data.push_back( reconnectParam );
    data.push_back( demandInterval );
    data.push_back( hw_threshold );
    data.push_back( connectDelay );
    data.push_back( maxDisconnects );

    return data;
}

//-----------------------------------------------------------
// Cycling Disconnect Set-Configuration Functions
//-----------------------------------------------------------
RfnRemoteDisconnectSetCyclingConfigurationCommand::RfnRemoteDisconnectSetCyclingConfigurationCommand( const unsigned disconnect_minutes,
                                                                                                      const unsigned connect_minutes )
    :   disconnectMinutes( disconnect_minutes ),
        connectMinutes( connect_minutes )
{
    validate( Condition( disconnect_minutes >= 5, ClientErrors::BadParameter )
            << "Invalid Disconnect Minutes: (" << disconnect_minutes
            << ") underflow (minimum 5)" );

    validate( Condition( disconnect_minutes <= 1440, ClientErrors::BadParameter )
            << "Invalid Disconnect Minutes: (" << disconnect_minutes
            << ") overflow (maximum 1440)" );

    validate( Condition( connect_minutes >= 5, ClientErrors::BadParameter )
            << "Invalid Connect Minutes: (" << connect_minutes
            << ") underflow (minimum 5)" );

    validate( Condition( connect_minutes <= 1440, ClientErrors::BadParameter )
            << "Invalid Connect Minutes: (" << connect_minutes
            << ") overflow (maximum 1440)" );
}

DisconnectMode RfnRemoteDisconnectSetCyclingConfigurationCommand::getConfigurationDisconnectMode() const
{
    return DisconnectMode_Cycling;
}

RfnCommand::Bytes RfnRemoteDisconnectSetCyclingConfigurationCommand::getData()
{
    RfnCommand::Bytes data;

    data.push_back( 0x01 ); // Reconnect Param forced to 1 per ICD
    data.push_back( disconnectMinutes >> 8 );
    data.push_back( disconnectMinutes );
    data.push_back( connectMinutes >> 8 );
    data.push_back( connectMinutes );

    return data;
}

//-----------------------------------------------------------
// Remote Disconnect Get-Configuration Functions
//-----------------------------------------------------------
RfnRemoteDisconnectGetConfigurationCommand::RfnRemoteDisconnectGetConfigurationCommand()
    :   RfnRemoteDisconnectConfigurationCommand( Operation_GetConfiguration )
{
    // Empty
}

RfnCommandResult RfnRemoteDisconnectGetConfigurationCommand::decodeCommand( const CtiTime now, const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << " != 1)" );

    result.description += decodeDisconnectConfigTlv( tlvs[0] );

    return result;
}

}
}
}
