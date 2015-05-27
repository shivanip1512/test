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


RfnTemperatureAlarmCommand::RfnTemperatureAlarmCommand( const Operation operation )
    :   _operation( operation )
{
    // empty
}


void RfnTemperatureAlarmCommand::invokeResultHandler( ResultHandler & rh ) const
{
    rh.handleCommandResult( *this );
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
    return _commandStatus != CommandStatus::Unsupported;
}


auto RfnTemperatureAlarmCommand::commandStatus() const -> CommandStatus
{
    return _commandStatus;
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

    using CommandStatusDescription = std::pair<CommandStatus, std::string>;

    static const std::map<unsigned char, CommandStatusDescription>  statusResolver = {
            { 0x00, { CommandStatus::Success,     "Success" } },
            { 0x01, { CommandStatus::Failure,     "Failure" } },
            { 0x02, { CommandStatus::Unsupported, "Unsupported" } } };

    auto statusDescription = mapFind( statusResolver, response[2] );

    validate( Condition( !! statusDescription, ClientErrors::InvalidData )
            << "Invalid Status (" << response[2] << ")" );

    _commandStatus = statusDescription->first;

    result.description += "Status: " + statusDescription->second + " (" + CtiNumStr(response[2]) + ")";

    return result;
}


auto RfnTemperatureAlarmCommand::decodeAlarmConfigTlv(const TypeLengthValue &tlv) -> TemperatureConfig
{
    TemperatureConfig config;

    // Validate TLV type

    validate( Condition( tlv.type == TlvType_TemperatureAlarmConfiguration, ClientErrors::InvalidData )
            << "Invalid TLV type (" << CtiNumStr(tlv.type).xhex(2) << ")" );

    // Validate the Alarm State

    static const std::map<unsigned char, std::string> AlarmStateResolver = {
            { 0x00, "Disabled" },
            { 0x01, "Enabled"  } };

    boost::optional<std::string> enabledState = mapFind( AlarmStateResolver, tlv.value[0] );

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
    :   RfnTemperatureAlarmCommand( Operation_SetConfiguration ),
        _configuration( configuration )
{
    enum ConfigurationLimits
    {
        Limit_HighTempThresholdMinimum  = -40,
        Limit_HighTempThresholdMaximum  = 185,
        Limit_RepeatIntervalMinimum     = 0,
        Limit_RepeatIntervalMaximum     = 255,
        Limit_RepeatCountMinimum        = 0,
        Limit_RepeatCountMaximum        = 255
    };

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


auto RfnSetTemperatureAlarmConfigurationCommand::getAlarmConfiguration() const -> boost::optional<AlarmConfiguration>
{
    if( commandStatus() == CommandStatus::Success )
    {
        return _configuration;
    }

    return boost::none;
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


auto RfnGetTemperatureAlarmConfigurationCommand::getAlarmConfiguration() const -> boost::optional<AlarmConfiguration>
{
    return _configuration;
}


RfnCommandResult RfnGetTemperatureAlarmConfigurationCommand::decodeCommand( const CtiTime now,
                                                                            const RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    if ( ! isSupported() )  // bail out if device returns 'Unsupported'
    {
        return result;
    }

    //  if no TLVs, just return immediately
    if( response[3] == 0 )
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

    if( commandStatus() == CommandStatus::Success )
    {
        _configuration = config.value;
    }

    result.description += "\n" + config.description;

    return result;
}


}
}
}

