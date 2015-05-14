#include "precompiled.h"

#include "cmd_rfn_TemperatureAlarm.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>
#include <boost/cstdint.hpp>

namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   {

const std::map<unsigned char, std::string>  statusResolver = boost::assign::map_list_of
    ( 0x00, "Success" )
    ( 0x01, "Failure" )
    ( 0x02, "Unsupported" );


const std::map<unsigned char, std::string>  alarmStateResolver = boost::assign::map_list_of
    ( 0x00, "Disabled" )
    ( 0x01, "Enabled" );

}


////


RfnTemperatureAlarmCommand::RfnTemperatureAlarmCommand( const Operation operation )
    :   _operation( operation ),
        _isSupported( false )
{
    // empty
}


RfnTemperatureAlarmCommand::RfnTemperatureAlarmCommand( const Operation operation, const AlarmConfiguration & configuration )
    :   _operation( operation ),
        _isSupported( false ),
        _configuration( configuration )
{
    // empty
}


// Event manager message
auto RfnTemperatureAlarmCommand::getApplicationServiceId() const -> ASID
{
    return ASID::EventManager;
}


unsigned char RfnTemperatureAlarmCommand::getCommandCode() const
{
    return CommandCode_Request;
}


unsigned char RfnTemperatureAlarmCommand::getOperation() const
{
    return _operation;
}


bool RfnTemperatureAlarmCommand::isSupported() const
{
    return _isSupported;
}


RfnCommand::Bytes RfnTemperatureAlarmCommand::getCommandData()
{
    return getBytesFromTlvs( std::vector<TypeLengthValue>() );
}


RfnCommandResult RfnTemperatureAlarmCommand::decodeResponseHeader( const CtiTime now,
                                                                    const RfnResponsePayload & response )
{
    RfnCommandResult  result;

    // We need at least 3 bytes

    validate( Condition( response.size() >= 3, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the Response code

    validate( Condition( response[0] == CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    // Validate the Operation code

    validate( Condition( response[1] == _operation, ClientErrors::InvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    // Validate Status code

    boost::optional<std::string> status = mapFind( statusResolver, response[2] );

    validate( Condition( !! status, ClientErrors::InvalidData )
            << "Invalid Status (" << response[2] << ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";

    // Determine the Supported/Unsupported status

    _isSupported = ( response[2] != AlarmStatus_Unsupported );

    return result;
}


auto RfnTemperatureAlarmCommand::decodeAlarmConfigTlv(const TypeLengthValue &tlv) -> TemperatureConfig
{
    TemperatureConfig config;

    // Validate TLV type

    validate( Condition( tlv.type == TlvType_TemperatureAlarmConfiguration, ClientErrors::InvalidData )
            << "Invalid TLV type (" << CtiNumStr(tlv.type).xhex(2) << ")" );

    // Validate the Alarm State

    boost::optional<std::string> enabledState = mapFind( alarmStateResolver, tlv.value[0] );

    validate( Condition( !! enabledState, ClientErrors::InvalidData )
            << "Invalid Alarm Enabled State ( " << tlv.value[0] << ")" );

    config.value.alarmEnabled = ( tlv.value[0] == AlarmState_AlarmEnabled );

    config.description += "State: Alarm " + *enabledState + " (" + CtiNumStr(tlv.value[0]) + ")";

    // Decode the rest of the data

    boost::int16_t highThreshold = ( tlv.value[1] << 8 ) | tlv.value[2];

    config.value.alarmHighTempThreshold = highThreshold;  // sign extend as necessary

    config.description += "\nHigh Temperature Threshold: " + CtiNumStr(config.value.alarmHighTempThreshold)
            + (config.value.alarmHighTempThreshold == 1 ? " degree (" : " degrees (") + CtiNumStr(highThreshold).xhex(4) + ")";

    boost::int16_t lowThreshold = ( tlv.value[3] << 8 ) | tlv.value[4];

    int lowTempThreshold = lowThreshold;    // sign extend as necessary

    config.description += "\nLow Temperature Threshold: " + CtiNumStr(lowTempThreshold) +
            + (lowTempThreshold == 1 ? " degree (" : " degrees (") + CtiNumStr(lowThreshold).xhex(4) + ")";

    config.value.alarmRepeatInterval = tlv.value[5];

    config.description += "\nAlarm Repeat Interval: " + CtiNumStr(config.value.alarmRepeatInterval)
            + (config.value.alarmRepeatInterval == 1 ? " minute" : " minutes");

    config.value.alarmRepeatCount = tlv.value[6];

    config.description += "\nAlarm Repeat Count: " + CtiNumStr(config.value.alarmRepeatCount)
            + (config.value.alarmRepeatCount == 1 ? " count" : " counts");

    return config;
}


////


RfnSetTemperatureAlarmConfigurationCommand::RfnSetTemperatureAlarmConfigurationCommand( const AlarmConfiguration & configuration )
    :   RfnTemperatureAlarmCommand( Operation_SetConfiguration, configuration )
{
    validate( Condition( configuration.alarmHighTempThreshold >= Limit_HighTempThresholdMinimum, ClientErrors::BadParameter )
            << "Invalid High Temperature Threshold: (" << configuration.alarmHighTempThreshold
            << ") underflow (minimum: "<< Limit_HighTempThresholdMinimum << ")" );

    validate( Condition( configuration.alarmHighTempThreshold <= Limit_HighTempThresholdMaximum, ClientErrors::BadParameter )
            << "Invalid High Temperature Threshold: (" << configuration.alarmHighTempThreshold
            << ") overflow (maximum: " << Limit_HighTempThresholdMaximum << ")" );

    validate( Condition( configuration.alarmRepeatInterval >= Limit_RepeatIntervalMinimum, ClientErrors::BadParameter )
            << "Invalid Repeat Interval: (" << configuration.alarmRepeatInterval
            << ") underflow (minimum: "<< Limit_RepeatIntervalMinimum << ")" );

    validate( Condition( configuration.alarmRepeatInterval <= Limit_RepeatIntervalMaximum, ClientErrors::BadParameter )
            << "Invalid Repeat Interval: (" << configuration.alarmRepeatInterval
            << ") overflow (maximum: " << Limit_RepeatIntervalMaximum << ")" );

    validate( Condition( configuration.alarmRepeatCount >= Limit_RepeatCountMinimum, ClientErrors::BadParameter )
            << "Invalid Repeat Count: (" << configuration.alarmRepeatCount
            << ") underflow (minimum: "<< Limit_RepeatCountMinimum << ")" );

    validate( Condition( configuration.alarmRepeatCount <= Limit_RepeatCountMaximum, ClientErrors::BadParameter )
            << "Invalid Repeat Count: (" << configuration.alarmRepeatCount
            << ") overflow (maximum: " << Limit_RepeatCountMaximum << ")" );
}


void RfnSetTemperatureAlarmConfigurationCommand::invokeResultHandler( ResultHandler & rh ) const
{
    rh.handleCommandResult( *this );
}


RfnCommand::Bytes RfnSetTemperatureAlarmConfigurationCommand::getCommandData()
{
    TypeLengthValue     tlv( TlvType_TemperatureAlarmConfiguration );

    // Enabled state
    tlv.value.push_back( _configuration.alarmEnabled ? AlarmState_AlarmEnabled : AlarmState_AlarmDisabled );

    // High Temperature Alarm Threshold (degrees C)
    tlv.value.push_back( _configuration.alarmHighTempThreshold >> 8 );
    tlv.value.push_back( _configuration.alarmHighTempThreshold );

    // Low Temperature Alarm Threshold (degrees C) - always 10 degrees below the high threshold
    const int alarmLowThreshold = _configuration.alarmHighTempThreshold - 10;

    tlv.value.push_back( alarmLowThreshold >> 8 );
    tlv.value.push_back( alarmLowThreshold );

    // Repeat Interval (minutes)
    tlv.value.push_back( _configuration.alarmRepeatInterval );

    // Repeat Count (counts)
    tlv.value.push_back( _configuration.alarmRepeatCount );

    return getBytesFromTlvs( boost::assign::list_of(tlv) );
}


RfnCommandResult RfnSetTemperatureAlarmConfigurationCommand::decodeCommand( const CtiTime now,
                                                                            const RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    // We need at least 4 bytes

    validate( Condition( response.size() >= 4, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    //  if no TLVs, just return immediately
    if( response[3] == 0 )
    {
        return result;
    }

    const auto tlvs = getTlvsFromBytes( Bytes( response.begin() + 3 , response.end() ));

    //  otherwise, require just one
    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const auto config = decodeAlarmConfigTlv(tlvs[0]);

    _configuration = config.value;

    result.description += "\n" + config.description;

    return result;
}


////


RfnGetTemperatureAlarmConfigurationCommand::RfnGetTemperatureAlarmConfigurationCommand()
    :   RfnTemperatureAlarmCommand( Operation_GetConfiguration )
{
    // empty
}


RfnTemperatureAlarmCommand::AlarmConfiguration RfnGetTemperatureAlarmConfigurationCommand::getAlarmConfiguration() const
{
    return _configuration;
}


void RfnGetTemperatureAlarmConfigurationCommand::invokeResultHandler( ResultHandler & rh ) const
{
    rh.handleCommandResult( *this );
}


RfnCommandResult RfnGetTemperatureAlarmConfigurationCommand::decodeCommand( const CtiTime now,
                                                                            const RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    if ( ! isSupported() )  // bail out if device returns 'Unsupported'
    {
        return result;
    }

    // We need 13 bytes

    validate( Condition( response.size() == 13, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate TLV count is 1

    validate( Condition( response[3] == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << response[3] << ")" );

    // Decode the TLV

    const auto tlvs = getTlvsFromBytes( Bytes( response.begin() + 3 , response.end() ));

    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const auto config = decodeAlarmConfigTlv(tlvs[0]);

    _configuration = config.value;

    result.description += "\n" + config.description;

    return result;
}


}
}
}

