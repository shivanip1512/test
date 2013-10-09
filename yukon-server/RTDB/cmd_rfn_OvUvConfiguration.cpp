#include "precompiled.h"

#include "cmd_rfn_OvUvConfiguration.h"
#include "numstr.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>


namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   {

const std::map<unsigned char, std::string>  responseResolver = boost::assign::map_list_of
    ( 0x01, "Configuration Success" )
    ( 0x02, "Configuration Failure" );


void validateCondition( const bool condition,
                        const int error_code,
                        const std::string & error_message )
{
    if ( ! condition )
    {
        throw RfnCommand::CommandException( error_code, error_message );
    }
}


}


RfnOvUvConfigurationCommand::RfnOvUvConfigurationCommand( const Operation operationCode )
    :   _operationCode( operationCode )
{
    // empty
}


unsigned char RfnOvUvConfigurationCommand::getCommandCode() const
{
    return 0x00;
}


unsigned char RfnOvUvConfigurationCommand::getOperation() const
{
    return _operationCode;
}


RfnCommand::Bytes RfnOvUvConfigurationCommand::getCommandHeader()
{
    return Bytes( 1, getOperation() );
}


// Event manager message
unsigned char RfnOvUvConfigurationCommand::getApplicationServiceId() const
{
    return 0x06;
}


RfnCommandResult RfnOvUvConfigurationCommand::decodeCommand( const CtiTime now,
                                                             const RfnResponsePayload & response )
{
    RfnCommandResult    result;

    // We need 2 bytes

    validateCondition( response.size() == 2,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    // Validate the bytes

    validateCondition( response[0] == Operation_OvUvConfigurationResponse,
                       ErrorInvalidData, "Invalid Response Code (" + CtiNumStr(response[0]).xhex(2) + ")" );

    // validate response

    boost::optional<std::string> status = Cti::mapFind( responseResolver, response[1] );

    // invalid status byte -- not found in map

    validateCondition( status,
                       ErrorInvalidData, "Invalid Status (" + CtiNumStr(response[1]) + ")" );

    validateCondition( response[1] == 0x01,    // success
                       ErrorInvalidData, "Status: " + *status + " (" + CtiNumStr(response[1]) + ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[1]) + ")";

    return result;
}


////////////////////////
///


RfnSetOvUvAlarmProcessingStateCommand::RfnSetOvUvAlarmProcessingStateCommand( const AlarmStates alarmState )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvAlarmProcessingState ),
        _state( alarmState )
{
    // empty
}


RfnCommand::Bytes RfnSetOvUvAlarmProcessingStateCommand::getCommandData()
{
    return boost::assign::list_of( _state == EnableOvUv ? 0x01 : 0x00 );
}


////////////////////////
///


RfnSetOvUvNewAlarmReportIntervalCommand::RfnSetOvUvNewAlarmReportIntervalCommand( const unsigned interval_minutes )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvNewAlarmReportingInterval ),
        _reportingInterval( interval_minutes )
{
    validateCondition( interval_minutes >= 2,
                       BADPARAM,
                       "Invalid Reporting Interval: (" + CtiNumStr(interval_minutes) + ") underflow (minimum: 2)" );

    validateCondition( interval_minutes <= 30,
                       BADPARAM,
                       "Invalid Reporting Interval: (" + CtiNumStr(interval_minutes) + ") overflow (maximum: 30)" );
}


RfnCommand::Bytes RfnSetOvUvNewAlarmReportIntervalCommand::getCommandData()
{
    return boost::assign::list_of( _reportingInterval );
}


////////////////////////
///


RfnSetOvUvAlarmRepeatIntervalCommand::RfnSetOvUvAlarmRepeatIntervalCommand( const unsigned interval_minutes )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvAlarmRepeatInterval ),
        _repeatInterval( interval_minutes )
{
    validateCondition( interval_minutes >= 60,
                       BADPARAM,
                       "Invalid Repeat Interval: (" + CtiNumStr(interval_minutes) + ") underflow (minimum: 60)" );

    validateCondition( interval_minutes <= 240,
                       BADPARAM,
                       "Invalid Repeat Interval: (" + CtiNumStr(interval_minutes) + ") overflow (maximum: 240)" );
}


RfnCommand::Bytes RfnSetOvUvAlarmRepeatIntervalCommand::getCommandData()
{
    return boost::assign::list_of( _repeatInterval );
}


////////////////////////
///


RfnSetOvUvAlarmRepeatCountCommand::RfnSetOvUvAlarmRepeatCountCommand( const unsigned repeat_count )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvAlarmRepeatCount ),
        _repeatCount( repeat_count )
{
    validateCondition( repeat_count >= 1,
                       BADPARAM,
                       "Invalid Repeat Count: (" + CtiNumStr(repeat_count) + ") underflow (minimum: 1)" );

    validateCondition( repeat_count <= 3,
                       BADPARAM,
                       "Invalid Repeat Count: (" + CtiNumStr(repeat_count) + ") overflow (maximum: 3)" );
}


RfnCommand::Bytes RfnSetOvUvAlarmRepeatCountCommand::getCommandData()
{
    return boost::assign::list_of( _repeatCount );
}


////////////////////////
///


RfnSetOvUvSetThresholdCommand::RfnSetOvUvSetThresholdCommand( const MeterID meter_id,
                                                              const EventID event_id,
                                                              const double threshold_volts )
    :   RfnOvUvConfigurationCommand( Operation_SetSetThreshold ),
        _meterID( meter_id ),
        _eventID( event_id ),
        _thresholdValue( threshold_volts * 1000 )
{
    validateCondition( meter_id != Unspecified,
                       BADPARAM,
                       "Invalid Meter ID: Unspecified (" + CtiNumStr(meter_id) + ")" );
}


RfnCommand::Bytes RfnSetOvUvSetThresholdCommand::getCommandData()
{
    Bytes   bytes;

    bytes.push_back( _meterID );

    // eventID
    bytes.push_back( _eventID >> 8 );
    bytes.push_back( _eventID );

    // Threshold in millivolts
    bytes.push_back( _thresholdValue >> 24  );
    bytes.push_back( _thresholdValue >> 16  );
    bytes.push_back( _thresholdValue >> 8   );
    bytes.push_back( _thresholdValue        );

    // UoM
    bytes.push_back( Uom_Volts );

    // UoM modifier 1 == 0x8000
    bytes.push_back( 0x80 );
    bytes.push_back( 0x00 );

    // UoM modifier 2 ==> 0x07 << 6 == 0x01c0   millivolts
    bytes.push_back( 0x01 );
    bytes.push_back( 0xc0 );

    return bytes;
}


////////////////////////
///


RfnGetOvUvAlarmConfigurationCommand::RfnGetOvUvAlarmConfigurationCommand( const MeterID   meter_id,
                                                                          const EventID   event_id )
    :   RfnOvUvConfigurationCommand( Operation_GetOvUvAlarmConfigurationInfo ),
        _meterID( meter_id ),
        _eventID( event_id )
{
    validateCondition( meter_id != Unspecified,
                       BADPARAM,
                       "Invalid Meter ID: Unspecified (" + CtiNumStr(meter_id) + ")" );
}


void RfnGetOvUvAlarmConfigurationCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


RfnCommand::Bytes RfnGetOvUvAlarmConfigurationCommand::getCommandData()
{
    Bytes   bytes;

    bytes.push_back( _meterID );

    // eventID
    bytes.push_back( _eventID >> 8 );
    bytes.push_back( _eventID );

    return bytes;
}


RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration  RfnGetOvUvAlarmConfigurationCommand::getAlarmConfiguration() const
{
    return _alarmConfig;
}


namespace   {

const std::map<unsigned char, std::string>  meterIdResolver = boost::assign::map_list_of
    ( 0x02, "L&G Focus AL" )
    ( 0x03, "L&G Focus AX" )
    ( 0x04, "Centron C2SX" );

const std::map<unsigned, std::string>  eventIdResolver = boost::assign::map_list_of
    ( 2022, "Over Voltage" )
    ( 2023, "Under Voltage" );

const std::map<unsigned char, std::string>  ovuvStateResolver = boost::assign::map_list_of
    ( 0x00, "OV/UV Disabled" )
    ( 0x01, "OV/UV Enabled" );

}


RfnCommandResult RfnGetOvUvAlarmConfigurationCommand::decodeCommand( const CtiTime now,
                                                                     const RfnResponsePayload & response )
{
    RfnCommandResult    result;

///    We need 18 bytes
///        1   -- opCode
///        1   -- Meter ID
///        2   -- Event ID
///        1   -- OV/UV Enable/Disable
///        1   -- New Alarm Reporting Interval (minutes)
///        1   -- Alarm Repeat Interval (minutes)
///        1   -- SET Alarm Repeat Count
///        1   -- CLEAR Alarm Repeat Count
///        4   -- Threshold Value
///        1   -- Unit of Measurement
///        2   -- UoM modifier 1
///        2   -- UoM modifier 2

    validateCondition( response.size() == 18,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    // Validate the bytes

    validateCondition( response[0] == Operation_GetOvUvAlarmConfigurationInfoResponse,
                       ErrorInvalidData, "Invalid Response Code (" + CtiNumStr(response[0]).xhex(2) + ")" );

    // UoM is always in volts for Ov/Uv (0x10)

    validateCondition( response[13] == Uom_Volts,
                       ErrorInvalidData, "Invalid UoM Code (" + CtiNumStr(response[13]).xhex(2) + ")" );

    // UoM modifier 1 is always 0x8000 for Ov/Uv

    const unsigned uom_modifier1 = (response[14] << 8) + response[15];

    validateCondition( uom_modifier1 == 0x8000,
                       ErrorInvalidData, "Invalid UoM Modifier 1 Code (" + CtiNumStr(uom_modifier1).xhex(4) + ")" );

    // UoM modifier 2 is always 0x01c0 for Ov/Uv

    const unsigned uom_modifier2 = (response[16] << 8) + response[17];

    validateCondition( uom_modifier2 == 0x01c0,
                       ErrorInvalidData, "Invalid UoM Modifier 2 Code (" + CtiNumStr(uom_modifier2).xhex(4) + ")" );

    // Parse and report the rest

    boost::optional<std::string> meterID = Cti::mapFind( meterIdResolver, response[1] );

    validateCondition( meterID,
                       ErrorInvalidData, "Invalid Meter ID (" + CtiNumStr(response[1]) + ")" );

    validateCondition( response[1] == _meterID,
                       ErrorInvalidData, "Meter ID mismatch (" + CtiNumStr(response[1]) + ") expected (" + CtiNumStr(_meterID) + ")" );

    result.description += "Meter ID: " + *meterID + " (" + CtiNumStr(response[1]) + ")";

    const unsigned eventIDvalue = (response[2] << 8) + response[3];

    boost::optional<std::string> eventID = Cti::mapFind( eventIdResolver, eventIDvalue );

    validateCondition( eventID,
                       ErrorInvalidData, "Invalid Event ID (" + CtiNumStr(eventIDvalue) + ")" );

    validateCondition( eventIDvalue == _eventID,
                       ErrorInvalidData, "Event ID mismatch (" + CtiNumStr(eventIDvalue) + ") expected (" + CtiNumStr(_eventID) + ")" );

    result.description += "\nEvent ID: " + *eventID + " (" + CtiNumStr(eventIDvalue) + ")";

    boost::optional<std::string> ovuvState = Cti::mapFind( ovuvStateResolver, response[4] );

    validateCondition( ovuvState,
                       ErrorInvalidData, "Invalid OV/UV State (" + CtiNumStr(response[4]) + ")" );

    result.description += "\nOV/UV State: " + *ovuvState + " (" + CtiNumStr(response[4]) + ")";

    result.description += "\nNew Alarm Reporting Interval: " + CtiNumStr(response[5]) + " minutes";
    result.description += "\nAlarm Repeat Interval: " + CtiNumStr(response[6]) + " minutes";
    result.description += "\nSET Alarm Repeat Count: " + CtiNumStr(response[7]) + " count(s)";
    result.description += "\nCLEAR Alarm Repeat Count: " + CtiNumStr(response[8]) + " count(s)";

    _alarmConfig.ovuvEnabled                = response[4];
    _alarmConfig.ovuvAlarmReportingInterval = response[5];
    _alarmConfig.ovuvAlarmRepeatInterval    = response[6];
    _alarmConfig.ovuvAlarmRepeatCount       = response[7];

    const unsigned thresholdValue = (response[9] << 24) + (response[10] << 16) + (response[11] << 8) + response[12];

    const double threshold = thresholdValue / 1000.0;

    result.description += "\nSet Threshold Value: " + CtiNumStr(threshold) + " volts (" + CtiNumStr(thresholdValue).xhex(8) + ")";

    ( _eventID == OverVoltage ? _alarmConfig.ovThreshold : _alarmConfig.uvThreshold ) = threshold;

    result.description += "\nUnit of Measure: Volts (" + CtiNumStr(response[13]).xhex(2) + ")";
    result.description += "\nUoM modifier 1: " + CtiNumStr(uom_modifier1).xhex(4);
    result.description += "\nUoM modifier 2: " + CtiNumStr(uom_modifier2).xhex(4);

    return result;
}


}
}
}

