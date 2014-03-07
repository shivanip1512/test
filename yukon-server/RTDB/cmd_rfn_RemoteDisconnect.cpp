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

const std::map<RfnRemoteDisconnectCommand::DisconnectMode, std::string>  disconnectModeResolver = boost::assign::map_list_of
    ( RfnRemoteDisconnectCommand::DisconnectMode_OnDemand,        "On Demand" )
    ( RfnRemoteDisconnectCommand::DisconnectMode_DemandThreshold, "Demand Threshold" )
    ( RfnRemoteDisconnectCommand::DisconnectMode_Cycling,         "Cycling" )
    ;

const std::map<unsigned char, RfnRemoteDisconnectCommand::DemandInterval> remoteDisconnectIntervalResolver = boost::assign::map_list_of
    (  5, RfnRemoteDisconnectCommand::DemandInterval_Five    )
    ( 10, RfnRemoteDisconnectCommand::DemandInterval_Ten     )
    ( 15, RfnRemoteDisconnectCommand::DemandInterval_Fifteen );

const std::map<unsigned char, RfnRemoteDisconnectCommand::Reconnect>  remoteDisconnectReconnectResolver = boost::assign::map_list_of
    ( 0, RfnRemoteDisconnectCommand::Reconnect_Arm       )
    ( 1, RfnRemoteDisconnectCommand::Reconnect_Immediate );

const std::map<unsigned char, RfnRemoteDisconnectCommand::DisconnectMode> disconnectModes = boost::assign::map_list_of
    ( 1, RfnRemoteDisconnectCommand::DisconnectMode_OnDemand        )
    ( 2, RfnRemoteDisconnectCommand::DisconnectMode_DemandThreshold )
    ( 3, RfnRemoteDisconnectCommand::DisconnectMode_Cycling         )
    ;

} // anonymous namespace

//-----------------------------------------------------------
// Remote Disconnect Command Constructor
//-----------------------------------------------------------
RfnRemoteDisconnectCommand::RfnRemoteDisconnectCommand( const Operation operation )
    :   _operation( operation )
{
    // Empty
}

unsigned char RfnRemoteDisconnectCommand::getCommandCode() const
{
    return CommandCode_Request;
}

unsigned char RfnRemoteDisconnectCommand::getOperation() const
{
    return _operation;
}

RfnCommandResult RfnRemoteDisconnectCommand::decodeResponseHeader( const CtiTime now, const RfnResponsePayload & response )
{
    RfnCommandResult result;

    // We need at least three bytes
    validate( Condition( response.size() >= 3, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the first 3 bytes
    validate( Condition( response[0] == CommandCode_Response, ErrorInvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == _operation, ErrorInvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    boost::optional<std::string> status = mapFind( remoteDisconnectStatusResolver, response[2] );

    // invalid status byte -- not found in map
    validate( Condition( status, ErrorInvalidData )
            << "Invalid Status (" << response[2] << ")" );

    validate( Condition( response[2] == 0, ErrorInvalidData ) // success
            << "Status: " << *status << " (" << response[2] << ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";

    return result;
}

RfnRemoteDisconnectCommand::TlvList RfnRemoteDisconnectCommand::getTlvsFromPayload( const RfnResponsePayload & response )
{
    validate( Condition( response.size() >= 5, ErrorInvalidData )
            << "Response too small (" << response.size() << " < 5)" );

    const RfnResponsePayload payload( response.begin() + 4, response.end() );

    return getTlvsFromBytes( payload );
}

RfnCommand::Bytes RfnRemoteDisconnectCommand::getCommandData()
{
    return getBytesFromTlvs( getTlvs() );
}

RfnRemoteDisconnectCommand::TlvList RfnRemoteDisconnectCommand::getTlvs()
{
    return TlvList();
}

//-----------------------------------------------------------
// Remote Disconnect Set-Configuration Functions
//-----------------------------------------------------------
RfnRemoteDisconnectSetConfigurationCommand::RfnRemoteDisconnectSetConfigurationCommand()
    :   RfnRemoteDisconnectCommand( Operation_SetConfiguration )
{
    // Empty
}

RfnCommandResult RfnRemoteDisconnectSetConfigurationCommand::decodeCommand( const CtiTime now, const RfnResponsePayload &response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    // We're a success, we should have a current disconnect mode.
    validate( Condition( response.size() >= 4, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    currentDisconnectMode = response[3];

    validate( Condition( currentDisconnectMode == getDisconnectMode(), ErrorInvalidData )
            << "Invalid current disconnect mode received (" << currentDisconnectMode 
            << " != " << getDisconnectMode() << ")" );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 0, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    return result;
}


RfnRemoteDisconnectCommand::TlvList RfnRemoteDisconnectSetConfigurationCommand::getTlvs()
{
    RfnCommand::Bytes data = getData();

    TypeLengthValue tlv( getDisconnectMode(), data );

    return boost::assign::list_of(tlv);
}

//-----------------------------------------------------------
// On-Demand Disconnect Set-Configuration Functions
//-----------------------------------------------------------
RfnOnDemandDisconnectSetConfigurationCommand::RfnOnDemandDisconnectSetConfigurationCommand( const Reconnect reconnect_param )
    :   reconnectParam( reconnect_param )
{
    // Empty
}

void RfnOnDemandDisconnectSetConfigurationCommand::invokeResultHandler( RfnCommand::ResultHandler &rh ) const
{
    rh.handleCommandResult( *this );
}

RfnRemoteDisconnectCommand::DisconnectMode RfnOnDemandDisconnectSetConfigurationCommand::getDisconnectMode()
{
    return DisconnectMode_OnDemand; 
}

RfnCommand::Bytes RfnOnDemandDisconnectSetConfigurationCommand::getData()
{
    RfnCommand::Bytes data;

    data.push_back( reconnectParam );

    return data;
}

//-----------------------------------------------------------
// Demand Threshold Disconnect Configuration Functions
//-----------------------------------------------------------
RfnThresholdDisconnectSetConfigurationCommand::RfnThresholdDisconnectSetConfigurationCommand( const Reconnect      reconnect_param,
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
    validate( Condition( demand_threshold >= 0.0, BADPARAM )
            << "Invalid Demand Threshold: (" << underVal
            << ") underflow (minimum 0.0)" );

    std::string overVal = CtiNumStr( demand_threshold, 1 );
    validate( Condition( demand_threshold <= 12.0, BADPARAM )
            << "Invalid Demand Threshold: (" << overVal
            << ") overflow (maximum 12.0)" );

    validate( Condition( connect_delay <= 30, BADPARAM )
            << "Invalid Connect Delay: (" << connect_delay
            << ") overflow (maximum 30)" );

    validate( Condition( max_disconnects <= 20, BADPARAM )
            << "Invalid Max Disconnects: (" << max_disconnects
            << ") overflow (maximum 20)" );
}

void RfnThresholdDisconnectSetConfigurationCommand::invokeResultHandler( RfnCommand::ResultHandler &rh ) const
{
    rh.handleCommandResult( *this );
}

RfnRemoteDisconnectCommand::DisconnectMode RfnThresholdDisconnectSetConfigurationCommand::getDisconnectMode()
{
    return DisconnectMode_DemandThreshold; 
}

RfnCommand::Bytes RfnThresholdDisconnectSetConfigurationCommand::getData()
{
    RfnCommand::Bytes data;

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
RfnCyclingDisconnectSetConfigurationCommand::RfnCyclingDisconnectSetConfigurationCommand( const unsigned disconnect_minutes,
                                                                                          const unsigned connect_minutes )
    :   disconnectMinutes( disconnect_minutes ),
        connectMinutes( connect_minutes )
{
    validate( Condition( disconnect_minutes <= 1440, BADPARAM )
            << "Invalid Disconnect Minutes: (" << disconnect_minutes
            << ") overflow (maximum 1440)" );

    validate( Condition( connect_minutes <= 1440, BADPARAM )
            << "Invalid Connect Minutes: (" << connect_minutes
            << ") overflow (maximum 1440)" );
}

void RfnCyclingDisconnectSetConfigurationCommand::invokeResultHandler( RfnCommand::ResultHandler &rh ) const
{
    rh.handleCommandResult( *this );
}

RfnRemoteDisconnectCommand::DisconnectMode RfnCyclingDisconnectSetConfigurationCommand::getDisconnectMode()
{
    return DisconnectMode_Cycling; 
}

RfnCommand::Bytes RfnCyclingDisconnectSetConfigurationCommand::getData()
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
    :   RfnRemoteDisconnectCommand( Operation_GetConfiguration ),
        _disconnectMode( DisconnectMode_OnDemand ),
        _reconnectParam( Reconnect_Arm )
{
    // Empty
}

void RfnRemoteDisconnectGetConfigurationCommand::invokeResultHandler( RfnCommand::ResultHandler &rh ) const
{
    rh.handleCommandResult( *this );
}

RfnCommandResult RfnRemoteDisconnectGetConfigurationCommand::decodeCommand( const CtiTime now, const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << " != 1)" );

    const TypeLengthValue & tlv = tlvs[0];

    boost::optional<DisconnectMode> disconnect_mode = mapFind( disconnectModes, tlv.type );

    validate( Condition( disconnect_mode, ErrorInvalidData )
            << "Invalid TLV type received in response (" << tlv.type << ")" );

    _disconnectMode = *disconnect_mode;

    result.description += "\nDisconnect mode: " + *mapFind( disconnectModeResolver, _disconnectMode );

    // Byte 0 in all three tlv types
    boost::optional<Reconnect> reconnect = mapFind( remoteDisconnectReconnectResolver, tlv.value[0] );

    // invalid reconnect byte -- not found in map
    validate( Condition( reconnect, ErrorInvalidData )
            << "Response reconnect param invalid (" << tlv.value[0] 
            << ") expecting 0 or 1" );

    _reconnectParam = *reconnect;

    std::string reconnectStr = _reconnectParam == Reconnect_Arm ? "Arm" : "Immediate";
    result.description += "\nReconnect param: " + reconnectStr + " reconnect";

    switch( tlv.type )
    {
        case DisconnectMode_DemandThreshold:
        {
            validate( Condition( tlv.value.size() == 5, ErrorInvalidData )
            << "Response TLV too small (" << tlv.value.size() << " != 5)");

            // Byte 1 - demand interval
            boost::optional<DemandInterval> interval = mapFind( remoteDisconnectIntervalResolver, tlv.value[1] );

            // invalid interval byte -- not found in map
            validate( Condition( interval, ErrorInvalidData )
                    << "Response demand interval invalid (" << tlv.value[1] 
                    << ") expecting 5, 10, or 15." );

            _demandInterval = *interval;

            result.description += "\nDisconnect demand interval: " + CtiNumStr( *_demandInterval ) + " minutes";

            // Byte 2 - demand threshold
            const unsigned hectoWattThreshold = tlv.value[2];

            validate( Condition( hectoWattThreshold <= 120, ErrorInvalidData )
                    << "Response hectoWatt threshold invalid (" << tlv.value[2] 
                    << ") expecting <= 120" );

            _demandThreshold = hectoWattThreshold / 10.0; // Convert to kW.

            result.description += "\nDisconnect demand threshold: " + CtiNumStr( *_demandThreshold, 1 ) + " kW";

            // Byte 3 - connect delay
            _connectDelay = tlv.value[3];

            validate( Condition( *_connectDelay <= 30, ErrorInvalidData )
                    << "Response connect delay invalid (" << tlv.value[3]
                    << ") expecting <= 30" );

            result.description += "\nConnect delay: " + CtiNumStr( *_connectDelay ) + " minutes";

            // Byte 4 - max disconnects
            _maxDisconnects = tlv.value[4];

            validate( Condition( *_maxDisconnects <= 30, ErrorInvalidData )
                    << "Response max disconnects invalid (" << tlv.value[4] 
                    << ") expecting <= 20" );

            std::string disconnectsStr = CtiNumStr( *_maxDisconnects );
            result.description += "\nMax disconnects: " + ( *_maxDisconnects ? disconnectsStr : "disable" );

            break;
        }
        case DisconnectMode_Cycling:
        {
            validate( Condition( tlv.value.size() == 5, ErrorInvalidData )
                    << "Response TLV too small (" << tlv.value.size() << " != 5)" );

            validate( Condition( getReconnectParam() == Reconnect_Immediate, ErrorInvalidData ) // must be 1 for cycling!
                    << "Response reconnect param invalid " << getReconnectParam()
                    << ") expecting 1)" );

            // Bytes 1-2 : disconnect minutes
            _disconnectMinutes = tlv.value[1] << 8 | tlv.value[2];

            validate( Condition( *_disconnectMinutes <= 1440, ErrorInvalidData )
                    << "Response disconnect minutes invalid (" << *_disconnectMinutes 
                    << ") expecting <= 1440" );

            result.description += "\nDisconnect minutes: " + CtiNumStr( *_disconnectMinutes );

            // Bytes 3-4 : connect minutes
            _connectMinutes = tlv.value[3] << 8 | tlv.value[4];

            validate( Condition( *_connectMinutes <= 1440, ErrorInvalidData )
                    << "Response connect minutes invalid (" << *_connectMinutes 
                    << ") expecting <= 1440" );

            result.description += "\nConnect minutes: " + CtiNumStr( *_connectMinutes );

            break;
        }
    }

    return result;
}

RfnRemoteDisconnectCommand::Reconnect RfnRemoteDisconnectGetConfigurationCommand::getReconnectParam() const
{
    return _reconnectParam;
}

RfnRemoteDisconnectCommand::DisconnectMode RfnRemoteDisconnectGetConfigurationCommand::getDisconnectMode() const
{
    return _disconnectMode;
}

boost::optional<RfnRemoteDisconnectCommand::DemandInterval> RfnRemoteDisconnectGetConfigurationCommand::getDemandInterval() const
{
    return _demandInterval;
}

boost::optional<double> RfnRemoteDisconnectGetConfigurationCommand::getDemandThreshold() const
{
    return _demandThreshold;
}

boost::optional<unsigned> RfnRemoteDisconnectGetConfigurationCommand::getConnectDelay() const
{
    return _connectDelay;
}

boost::optional<unsigned> RfnRemoteDisconnectGetConfigurationCommand::getMaxDisconnects() const
{
    return _maxDisconnects;
}

boost::optional<unsigned> RfnRemoteDisconnectGetConfigurationCommand::getDisconnectMinutes() const
{
    return _disconnectMinutes;
}

boost::optional<unsigned> RfnRemoteDisconnectGetConfigurationCommand::getConnectMinutes() const
{
    return _connectMinutes;
}

}
}
}
