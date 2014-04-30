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

    validate( Condition( response.size() >= 3, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the Response code

    validate( Condition( response[0] == CommandCode_Response, ErrorInvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    // Validate the Operation code

    validate( Condition( response[1] == _operation, ErrorInvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    // Validate Status code

    boost::optional<std::string> status = mapFind( statusResolver, response[2] );

    validate( Condition( status, ErrorInvalidData )
            << "Invalid Status (" << response[2] << ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";

    // Determine the Supported/Unsupported status

    _isSupported = ( response[2] != AlarmStatus_Unsupported );

    return result;
}


////


RfnSetTemperatureAlarmConfigurationCommand::RfnSetTemperatureAlarmConfigurationCommand( const AlarmConfiguration & configuration )
    :   RfnTemperatureAlarmCommand( Operation_SetConfiguration, configuration )
{
    validate( Condition( configuration.alarmRepeatInterval == 15, BADPARAM )
            << "Invalid Repeat Interval: (" << configuration.alarmRepeatInterval << ") (fixed at: 15)" );

    validate( Condition( configuration.alarmRepeatCount == 3, BADPARAM )
            << "Invalid Repeat Count: (" << configuration.alarmRepeatCount << ") (fixed at: 3)" );
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

    // We need 4 bytes

    validate( Condition( response.size() == 4, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate TLV count is 0

    validate( Condition( response[3] == 0, ErrorInvalidData )
            << "Invalid TLV count (" << response[3] << ")" );

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

    validate( Condition( response.size() == 13, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate TLV count is 1

    validate( Condition( response[3] == 1, ErrorInvalidData )
            << "Invalid TLV count (" << response[3] << ")" );

    // Decode the TLV

    const std::vector<TypeLengthValue> tlvs = getTlvsFromBytes( Bytes( response.begin() + 3 , response.end() ));

    validate( Condition( tlvs.size() == 1, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const TypeLengthValue &tlv = tlvs[0];

    // Validate TLV type

    validate( Condition( tlv.type == TlvType_TemperatureAlarmConfiguration, ErrorInvalidData )
            << "Invalid TLV type (" << CtiNumStr(tlv.type).xhex(2) << ")" );

    // Validate the Alarm State

    boost::optional<std::string> enabledState = mapFind( alarmStateResolver, tlv.value[0] );

    validate( Condition( enabledState, ErrorInvalidData )
            << "Invalid Alarm Enabled State ( " << tlv.value[0] << ")" );

    _configuration.alarmEnabled = ( tlv.value[0] == AlarmState_AlarmEnabled );

    result.description += "\nState: Alarm " + *enabledState + " (" + CtiNumStr(tlv.value[0]) + ")";

    // Decode the rest of the data

    boost::int16_t highThreshold = ( tlv.value[1] << 8 ) | tlv.value[2];

    _configuration.alarmHighTempThreshold = highThreshold;  // sign extend as necessary

    result.description += "\nHigh Temperature Threshold: " + CtiNumStr(_configuration.alarmHighTempThreshold) + " degree(s) ("
                       + CtiNumStr(highThreshold).xhex(4) + ")";

    // Ignore bytes 3 and 4 (low temp threshold)

    _configuration.alarmRepeatInterval = tlv.value[5];

    result.description += "\nAlarm Repeat Interval: " + CtiNumStr(_configuration.alarmRepeatInterval) + " minute(s)";

    _configuration.alarmRepeatCount = tlv.value[6];

    result.description += "\nAlarm Repeat Count: " + CtiNumStr(_configuration.alarmRepeatCount) + " count(s)";

    return result;
}


}
}
}

