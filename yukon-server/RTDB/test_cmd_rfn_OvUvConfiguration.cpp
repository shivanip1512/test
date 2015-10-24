#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_OvUvConfiguration.h"
#include "boost_test_helpers.h"


using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnOvUvConfigurationCommand;
using Cti::Devices::Commands::RfnSetOvUvAlarmProcessingStateCommand;
using Cti::Devices::Commands::RfnSetOvUvNewAlarmReportIntervalCommand;
using Cti::Devices::Commands::RfnSetOvUvAlarmRepeatIntervalCommand;
using Cti::Devices::Commands::RfnSetOvUvAlarmRepeatCountCommand;
using Cti::Devices::Commands::RfnSetOvUvSetOverVoltageThresholdCommand;
using Cti::Devices::Commands::RfnSetOvUvSetUnderVoltageThresholdCommand;
using Cti::Devices::Commands::RfnGetOvUvAlarmConfigurationCommand;


using boost::assign::list_of;


// --- defined in RTDB\test_main.cpp -- so BOOST_CHECK_EQUAL_COLLECTIONS() works for RfnCommand::CommandException
namespace boost         {
namespace test_tools    {
    bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs );
}
}

namespace std   {
    ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );
}
// ---


BOOST_AUTO_TEST_SUITE( test_cmd_rfn_OvUvConfiguration )


const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Enable_OvUv )
{
    RfnSetOvUvAlarmProcessingStateCommand  command( RfnSetOvUvAlarmProcessingStateCommand::EnableOvUv );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x24 )( 0x01 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x29 )( 0x01 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Configuration Success (1)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x29 )( 0x02 );

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Configuration Failure (2)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Disable_OvUv )
{
    RfnSetOvUvAlarmProcessingStateCommand  command( RfnSetOvUvAlarmProcessingStateCommand::DisableOvUv );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x24 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_New_Alarm_Report_Interval )
{
    RfnSetOvUvNewAlarmReportIntervalCommand  command( 20 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x26 )( 0x14 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_New_Alarm_Report_Interval_constructor_exceptions )
{
    const std::vector< unsigned > inputs = boost::assign::list_of
        (  1 )
        ( 35 );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Reporting Interval: (1) underflow (minimum: 2)" ) )
        ( RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Reporting Interval: (35) overflow (maximum: 30)" ) );

    std::vector< RfnCommand::CommandException > actual;

    for each ( const unsigned input in inputs )
    {
        try
        {
            RfnSetOvUvNewAlarmReportIntervalCommand command( input );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Alarm_Repeat_Interval )
{
    RfnSetOvUvAlarmRepeatIntervalCommand  command( 120 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x27 )( 0x78 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Alarm_Repeat_Interval_constructor_exceptions )
{
    const std::vector< unsigned > inputs = boost::assign::list_of
        (  30 )
        ( 350 );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Repeat Interval: (30) underflow (minimum: 60)" ) )
        ( RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Repeat Interval: (350) overflow (maximum: 240)" ) );

    std::vector< RfnCommand::CommandException > actual;

    for each ( const unsigned input in inputs )
    {
        try
        {
            RfnSetOvUvAlarmRepeatIntervalCommand command( input );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Alarm_Repeat_Count )
{
    RfnSetOvUvAlarmRepeatCountCommand  command( 2 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x28 )( 0x02 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Alarm_Repeat_Count_constructor_exceptions )
{
    const std::vector< unsigned > inputs = boost::assign::list_of
        ( 0 )
        ( 5 );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Repeat Count: (0) underflow (minimum: 1)" ) )
        ( RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Repeat Count: (5) overflow (maximum: 3)" ) );

    std::vector< RfnCommand::CommandException > actual;

    for each ( const unsigned input in inputs )
    {
        try
        {
            RfnSetOvUvAlarmRepeatCountCommand command( input );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_Centron_UnderVoltage )
{
    RfnSetOvUvSetUnderVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::CentronC2SX, 119.3);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x04 )
            ( 0x07 )( 0xe7 )
            ( 0x00 )( 0x01 )( 0xd2 )( 0x04 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_Centron_OverVoltage )
{
    RfnSetOvUvSetOverVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::CentronC2SX, 124.8);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x04 )
            ( 0x07 )( 0xe6 )
            ( 0x00 )( 0x01 )( 0xe7 )( 0x80 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_FocusAX_UnderVoltage )
{
    RfnSetOvUvSetUnderVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::LGFocusAX, 119.3);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x03 )
            ( 0x07 )( 0xe7 )
            ( 0x00 )( 0x01 )( 0xd2 )( 0x04 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_FocusAX_OverVoltage )
{
    RfnSetOvUvSetOverVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::LGFocusAX, 124.8);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x03 )
            ( 0x07 )( 0xe6 )
            ( 0x00 )( 0x01 )( 0xe7 )( 0x80 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_FocusAL_UnderVoltage )
{
    RfnSetOvUvSetUnderVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::LGFocusAL, 119.3);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x02 )
            ( 0x07 )( 0xe7 )
            ( 0x00 )( 0x01 )( 0xd2 )( 0x04 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_FocusAL_OverVoltage )
{
    RfnSetOvUvSetOverVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::LGFocusAL, 124.8);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x02 )
            ( 0x07 )( 0xe6 )
            ( 0x00 )( 0x01 )( 0xe7 )( 0x80 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Set_Threshold_CentronC1SX_OverVoltage )
{
    RfnSetOvUvSetOverVoltageThresholdCommand  command(RfnOvUvConfigurationCommand::CentronC1SX, 124.8);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x25 )
            ( 0x06 )
            ( 0x07 )( 0xe6 )
            ( 0x00 )( 0x01 )( 0xe7 )( 0x80 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Get_Config_FocusAL_OverVoltage )
{
    RfnGetOvUvAlarmConfigurationCommand  command(RfnOvUvConfigurationCommand::LGFocusAL,
                                                 RfnOvUvConfigurationCommand::OverVoltage);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x34 )
            ( 0x02 )
            ( 0x07 )( 0xe6 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x35 )
            ( 0x02 )
            ( 0x07 )( 0xe6 )
            ( 0x00 )
            ( 0x0f )
            ( 0xb4 )
            ( 0x02 )
            ( 0x01 )
            ( 0x04 )
            ( 0x00 )( 0x01 )( 0xd2 )( 0x04 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Meter ID: L&G Focus AL (2)"
                                            "\nEvent ID: Over Voltage (2022)"
                                            "\nOV/UV State: OV/UV Disabled (0)"
                                            "\nNew Alarm Reporting Interval: 15 minutes"
                                            "\nAlarm Repeat Interval: 180 minutes"
                                            "\nSET Alarm Repeat Count: 2 count(s)"
                                            "\nCLEAR Alarm Repeat Count: 1 count(s)"
                                            "\nSeverity: Minor (4)"
                                            "\nSet Threshold Value: 119.300 volts (0x0001d204)"
                                            "\nUnit of Measure: Volts (0x10)"
                                            "\nUoM modifier 1: 0x8000"
                                            "\nUoM modifier 2: 0x01c0" );
    }

    RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration alarmConfig = command.getAlarmConfiguration();

    BOOST_CHECK_EQUAL( alarmConfig.ovuvEnabled, false );

    BOOST_CHECK_EQUAL( alarmConfig.ovuvAlarmReportingInterval,  15 );
    BOOST_CHECK_EQUAL( alarmConfig.ovuvAlarmRepeatInterval,     180 );
    BOOST_CHECK_EQUAL( alarmConfig.ovuvAlarmRepeatCount,        2 );

    BOOST_CHECK( ! alarmConfig.uvThreshold );

    BOOST_CHECK_CLOSE( *alarmConfig.ovThreshold, 119.3, 1e-4 );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_OvUvConfiguration_Get_Config_CentronC1SX_UnderVoltage )
{
    RfnGetOvUvAlarmConfigurationCommand  command(RfnOvUvConfigurationCommand::CentronC1SX,
                                                 RfnOvUvConfigurationCommand::UnderVoltage);

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x34 )
            ( 0x06 )
            ( 0x07 )( 0xe7 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x35 )
            ( 0x06 )
            ( 0x07 )( 0xe7 )
            ( 0x00 )
            ( 0x0f )
            ( 0xb4 )
            ( 0x02 )
            ( 0x01 )
            ( 0x04 )
            ( 0x00 )( 0x01 )( 0xd2 )( 0x04 )
            ( 0x10 )
            ( 0x80 )( 0x00 )
            ( 0x01 )( 0xc0 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Meter ID: Centron C1SX (6)"
                                            "\nEvent ID: Under Voltage (2023)"
                                            "\nOV/UV State: OV/UV Disabled (0)"
                                            "\nNew Alarm Reporting Interval: 15 minutes"
                                            "\nAlarm Repeat Interval: 180 minutes"
                                            "\nSET Alarm Repeat Count: 2 count(s)"
                                            "\nCLEAR Alarm Repeat Count: 1 count(s)"
                                            "\nSeverity: Minor (4)"
                                            "\nSet Threshold Value: 119.300 volts (0x0001d204)"
                                            "\nUnit of Measure: Volts (0x10)"
                                            "\nUoM modifier 1: 0x8000"
                                            "\nUoM modifier 2: 0x01c0" );
    }

    RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration alarmConfig = command.getAlarmConfiguration();

    BOOST_CHECK_EQUAL( alarmConfig.ovuvEnabled, false );

    BOOST_CHECK_EQUAL( alarmConfig.ovuvAlarmReportingInterval,  15 );
    BOOST_CHECK_EQUAL( alarmConfig.ovuvAlarmRepeatInterval,     180 );
    BOOST_CHECK_EQUAL( alarmConfig.ovuvAlarmRepeatCount,        2 );

    BOOST_CHECK( ! alarmConfig.ovThreshold );

    BOOST_CHECK_CLOSE( *alarmConfig.uvThreshold, 119.3, 1e-4 );
}


BOOST_AUTO_TEST_SUITE_END()

