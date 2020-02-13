#include "precompiled.h"

#include "cmd_rfn_OvUvConfiguration.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>


namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   {

const std::map<unsigned char, std::string>  responseResolver = boost::assign::map_list_of
    ( 0x01, "Configuration Success" )
    ( 0x02, "Configuration Failure" );

template <typename F>
bool isInteger( F val )
{
    return floor(val) == val;
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
auto RfnOvUvConfigurationCommand::getApplicationServiceId() const -> ASID
{
    return ASID::EventManager;
}


RfnCommandResult RfnOvUvConfigurationCommand::decodeCommand( const CtiTime now,
                                                             const RfnResponsePayload & response )
{
    // We need 2 bytes

    validate( Condition( response.size() == 2, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the bytes

    validate( Condition( response[0] == Operation_OvUvConfigurationResponse, ClientErrors::InvalidData )
            << "Invalid Response Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    // validate response

    boost::optional<std::string> status = Cti::mapFind( responseResolver, response[1] );

    // invalid status byte -- not found in map

    validate( Condition( !! status, ClientErrors::InvalidData )
            << "Invalid Status (" << response[1] << ")" );

    validate( Condition( response[1] == 0x01, ClientErrors::InvalidData ) // success
            << "Status: " << *status << " (" << response[1] << ")" );

    return "Status: " + *status + " (" + std::to_string(response[1]) + ")";
}

std::string RfnOvUvConfigurationCommand::getCommandName() const
{
    return "OV/UV Configuration Request";
}


////////////////////////
///


RfnSetOvUvAlarmProcessingStateCommand::RfnSetOvUvAlarmProcessingStateCommand( const AlarmStates alarmState )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvAlarmProcessingState ),
        alarmState( alarmState )
{
    // empty
}


RfnCommand::Bytes RfnSetOvUvAlarmProcessingStateCommand::getCommandData()
{
    return boost::assign::list_of( alarmState == EnableOvUv ? 0x01 : 0x00 );
}


////////////////////////
///


RfnSetOvUvNewAlarmReportIntervalCommand::RfnSetOvUvNewAlarmReportIntervalCommand( const unsigned interval_minutes )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvNewAlarmReportingInterval ),
        reportingInterval( interval_minutes )
{
    validate( Condition( interval_minutes >= 2, ClientErrors::BadParameter )
            << "Invalid Reporting Interval: (" << interval_minutes << ") underflow (minimum: 2)" );

    validate( Condition( interval_minutes <= 30, ClientErrors::BadParameter )
            << "Invalid Reporting Interval: (" << interval_minutes << ") overflow (maximum: 30)" );
}


RfnCommand::Bytes RfnSetOvUvNewAlarmReportIntervalCommand::getCommandData()
{
    return boost::assign::list_of( reportingInterval );
}


////////////////////////
///


RfnSetOvUvAlarmRepeatIntervalCommand::RfnSetOvUvAlarmRepeatIntervalCommand( const unsigned interval_minutes )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvAlarmRepeatInterval ),
        repeatInterval( interval_minutes )
{
    validate( Condition( interval_minutes >= 60, ClientErrors::BadParameter )
            << "Invalid Repeat Interval: (" << interval_minutes << ") underflow (minimum: 60)" );

    validate( Condition( interval_minutes <= 240, ClientErrors::BadParameter )
            << "Invalid Repeat Interval: (" << interval_minutes << ") overflow (maximum: 240)" );
}


RfnCommand::Bytes RfnSetOvUvAlarmRepeatIntervalCommand::getCommandData()
{
    return boost::assign::list_of( repeatInterval );
}


////////////////////////
///


RfnSetOvUvAlarmRepeatCountCommand::RfnSetOvUvAlarmRepeatCountCommand( const unsigned repeat_count )
    :   RfnOvUvConfigurationCommand( Operation_SetOvUvAlarmRepeatCount ),
        repeatCount( repeat_count )
{
    validate( Condition( repeat_count >= 1, ClientErrors::BadParameter )
            << "Invalid Repeat Count: (" << repeat_count << ") underflow (minimum: 1)" );

    validate( Condition( repeat_count <= 3, ClientErrors::BadParameter )
            << "Invalid Repeat Count: (" << repeat_count << ") overflow (maximum: 3)" );
}


RfnCommand::Bytes RfnSetOvUvAlarmRepeatCountCommand::getCommandData()
{
    return boost::assign::list_of( repeatCount );
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
    validate( Condition( meter_id != Unspecified, ClientErrors::BadParameter )
            << "Invalid Meter ID: Unspecified (" << meter_id << ")" );

    validate( Condition( isInteger( threshold_volts * 1000.0 ), ClientErrors::BadParameter )
            << "Invalid threshold " << threshold_volts << ": Precision should not exceed 0.001" );
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


RfnSetOvUvSetOverVoltageThresholdCommand::RfnSetOvUvSetOverVoltageThresholdCommand( const MeterID meter_id,
                                                                                    const double threshold_volts )
    :   RfnSetOvUvSetThresholdCommand(meter_id, OverVoltage, threshold_volts),
        ovThresholdValue(threshold_volts)
{
}

RfnSetOvUvSetUnderVoltageThresholdCommand::RfnSetOvUvSetUnderVoltageThresholdCommand( const MeterID meter_id,
                                                                                    const double threshold_volts )
    :   RfnSetOvUvSetThresholdCommand(meter_id, UnderVoltage, threshold_volts),
        uvThresholdValue(threshold_volts)
{
}

////////////////////////
///


RfnGetOvUvAlarmConfigurationCommand::RfnGetOvUvAlarmConfigurationCommand( const MeterID   meter_id,
                                                                          const EventID   event_id )
    :   RfnOvUvConfigurationCommand( Operation_GetOvUvAlarmConfigurationInfo ),
        _meterID( meter_id ),
        _eventID( event_id )
{
    validate( Condition( meter_id != Unspecified, ClientErrors::BadParameter )
            << "Invalid Meter ID: Unspecified (" << meter_id << ")" );
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

std::string RfnGetOvUvAlarmConfigurationCommand::getCommandName() const
{
    return "Get Ov/Uv Alarm Configuration Request";
}


namespace   {

const std::map<unsigned char, std::string>  meterIdResolver = boost::assign::map_list_of
    ( RfnOvUvConfigurationCommand::LGFocusAL,   "L&G Focus AL" )
    ( RfnOvUvConfigurationCommand::LGFocusAX,   "L&G Focus AX" )
    ( RfnOvUvConfigurationCommand::CentronC1SX, "Centron C1SX" )
    ( RfnOvUvConfigurationCommand::CentronC2SX, "Centron C2SX" );

const std::map<unsigned, std::string>  eventIdResolver = boost::assign::map_list_of
    ( RfnOvUvConfigurationCommand::OverVoltage,  "Over Voltage" )
    ( RfnOvUvConfigurationCommand::UnderVoltage, "Under Voltage" );

const std::map<unsigned char, std::string>  ovuvStateResolver = boost::assign::map_list_of
    ( 0x00, "OV/UV Disabled" )
    ( 0x01, "OV/UV Enabled" );

const std::map<unsigned char, std::string> severityResolver = boost::assign::map_list_of
    ( 0x01, "Not Reported" )
    ( 0x02, "Not Alarmed" )
    ( 0x03, "Warning" )
    ( 0x04, "Minor" )
    ( 0x05, "Major" )
    ( 0x06, "Critical" );
}


RfnCommandResult RfnGetOvUvAlarmConfigurationCommand::decodeCommand( const CtiTime now,
                                                                     const RfnResponsePayload & response )
{
///    We need 19 bytes
///        1   -- opCode
///        1   -- Meter ID
///        2   -- Event ID
///        1   -- OV/UV Enable/Disable
///        1   -- New Alarm Reporting Interval (minutes)
///        1   -- Alarm Repeat Interval (minutes)
///        1   -- SET Alarm Repeat Count
///        1   -- CLEAR Alarm Repeat Count
///        1   -- Severity
///        4   -- Threshold Value
///        1   -- Unit of Measurement
///        2   -- UoM modifier 1
///        2   -- UoM modifier 2

    validate( Condition( response.size() == 19, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the bytes

    validate( Condition( response[0] == Operation_GetOvUvAlarmConfigurationInfoResponse, ClientErrors::InvalidData )
            << "Invalid Response Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    // UoM is always in volts for Ov/Uv (0x10)

    validate( Condition( response[14] == Uom_Volts, ClientErrors::InvalidData )
            << "Invalid UoM Code (" << CtiNumStr(response[14]).xhex(2) << ")" );

    // UoM modifier 1 is always 0x8000 for Ov/Uv

    const unsigned uom_modifier1 = (response[15] << 8) + response[16];

    validate( Condition( uom_modifier1 == 0x8000, ClientErrors::InvalidData )
            << "Invalid UoM Modifier 1 Code (" << CtiNumStr(uom_modifier1).xhex(4) << ")" );

    // UoM modifier 2 is always 0x01c0 for Ov/Uv

    const unsigned uom_modifier2 = (response[17] << 8) + response[18];

    validate( Condition( uom_modifier2 == 0x01c0, ClientErrors::InvalidData )
            << "Invalid UoM Modifier 2 Code (" << CtiNumStr(uom_modifier2).xhex(4) << ")" );

    // Parse and report the rest

    boost::optional<std::string> meterID = Cti::mapFind( meterIdResolver, response[1] );

    validate( Condition( !! meterID, ClientErrors::InvalidData )
            << "Invalid Meter ID (" << response[1] << ")" );

    validate( Condition( response[1] == _meterID, ClientErrors::InvalidData )
            << "Meter ID mismatch (" << response[1] << ") expected (" << _meterID << ")" );

    std::string description = "Meter ID: " + *meterID + " (" + CtiNumStr(response[1]) + ")";

    const unsigned eventIDvalue = (response[2] << 8) + response[3];

    boost::optional<std::string> eventID = Cti::mapFind( eventIdResolver, eventIDvalue );

    validate( Condition( !! eventID, ClientErrors::InvalidData )
            << "Invalid Event ID (" << eventIDvalue << ")" );

    validate( Condition( eventIDvalue == _eventID, ClientErrors::InvalidData )
            << "Event ID mismatch (" << eventIDvalue << ") expected (" << _eventID << ")" );

    description += "\nEvent ID: " + *eventID + " (" + CtiNumStr(eventIDvalue) + ")";

    boost::optional<std::string> ovuvState = Cti::mapFind( ovuvStateResolver, response[4] );

    validate( Condition( !! ovuvState, ClientErrors::InvalidData )
            << "Invalid OV/UV State (" << response[4] << ")" );

    description += "\nOV/UV State: " + *ovuvState + " (" + CtiNumStr(response[4]) + ")";

    description += "\nNew Alarm Reporting Interval: " + CtiNumStr(response[5]) + " minutes";
    description += "\nAlarm Repeat Interval: " + CtiNumStr(response[6]) + " minutes";
    description += "\nSET Alarm Repeat Count: " + CtiNumStr(response[7]) + " count(s)";
    description += "\nCLEAR Alarm Repeat Count: " + CtiNumStr(response[8]) + " count(s)";

    boost::optional<std::string> severity = Cti::mapFind( severityResolver, response[9] );

    validate( Condition( !! severity, ClientErrors::InvalidData )
            << "Invalid severity (" << response[9] << ")" );

    description += "\nSeverity: " + *severity + " (" + CtiNumStr(response[9]) + ")";

    _alarmConfig.ovuvEnabled                = response[4];
    _alarmConfig.ovuvAlarmReportingInterval = response[5];
    _alarmConfig.ovuvAlarmRepeatInterval    = response[6];
    _alarmConfig.ovuvAlarmRepeatCount       = response[7];

    const unsigned thresholdValue = (response[10] << 24) + (response[11] << 16) + (response[12] << 8) + response[13];

    const double threshold = thresholdValue / 1000.0;

    description += "\nSet Threshold Value: " + CtiNumStr(threshold) + " volts (" + CtiNumStr(thresholdValue).xhex(8) + ")";

    ( _eventID == OverVoltage ? _alarmConfig.ovThreshold : _alarmConfig.uvThreshold ) = threshold;

    description += "\nUnit of Measure: Volts (" + CtiNumStr(response[14]).xhex(2) + ")";
    description += "\nUoM modifier 1: " + CtiNumStr(uom_modifier1).xhex(4);
    description += "\nUoM modifier 2: " + CtiNumStr(uom_modifier2).xhex(4);

    return description;
}


}
}
}

