#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_TemperatureAlarm.h"
#include "boost_test_helpers.h"


using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnTemperatureAlarmCommand;
using Cti::Devices::Commands::RfnSetTemperatureAlarmConfigurationCommand;
using Cti::Devices::Commands::RfnGetTemperatureAlarmConfigurationCommand;


// --- defined in RTDB\test_main.cpp -- so BOOST_CHECK_EQUAL_*() works for RfnCommand::CommandException
namespace boost         {
namespace test_tools    {
    bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs );
}
}

namespace std   {
    ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );
}
// ---


BOOST_AUTO_TEST_SUITE( test_cmd_rfn_TemperatureAlarm )


const CtiTime execute_time( CtiDate( 25, 3, 2014 ) , 15 );


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__GetApplicationServiceIdentifier )
{
    struct test : RfnGetTemperatureAlarmConfigurationCommand
    {
        using RfnTemperatureAlarmCommand::getApplicationServiceId;
    }
    cmd;

    BOOST_CHECK_EQUAL(
            static_cast<unsigned>(cmd.getApplicationServiceId()),
            0x06);
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration )
{
    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration =
    {
        true,       // alarmEnabled
        15,         // alarmRepeatInterval
        3,          // alarmRepeatCount
        50          // alarmHighTempThreshold
    };

    RfnSetTemperatureAlarmConfigurationCommand  command( configuration );

    // execute
    {
        const std::vector< unsigned char > exp {
                0x88, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
    // decode -- success response
    {
        const std::vector< unsigned char > response {
                0x89, 0x00, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );

        BOOST_CHECK_EQUAL( true, command.isSupported() );
    }
    // decode -- full description response
    {
        const std::vector< unsigned char > response {
            0x89, 0x00, 0x00, 0x01, 0x01, 0x07, 0x01, 0x00, 0x2d, 0x00, 0x23, 0x05, 0x03 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                           "Status: Success (0)"
                           "\nState: Alarm Enabled (1)"
                           "\nHigh Temperature Threshold: 45 degrees (0x002d)"
                           "\nLow Temperature Threshold: 35 degrees (0x0023)"
                           "\nAlarm Repeat Interval: 5 minutes"
                           "\nAlarm Repeat Count: 3 counts" );

        BOOST_CHECK_EQUAL( true, command.isSupported() );
    }
    // decode -- failure response
    {
        const std::vector< unsigned char > response {
                0x89, 0x00, 0x01, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Failure (1)" );

        BOOST_CHECK_EQUAL( true, command.isSupported() );
    }
    // decode -- unsupported response
    {
        const std::vector< unsigned char > response {
                0x89, 0x00, 0x02, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Unsupported (2)" );

        BOOST_CHECK_EQUAL( false, command.isSupported() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration_construction_exceptions_repeat_interval )
{
    // Currently there is no way to test underflow as we are data type limited to unsigned with a valid minimum of 0

    const std::vector< unsigned > inputs {
            300 };

    const std::vector< RfnCommand::CommandException >   expected {
            RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Repeat Interval: (300) overflow (maximum: 255)" ) };

    std::vector< RfnCommand::CommandException > actual;

    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration =
    {
        true,       // alarmEnabled
        0,          // alarmRepeatInterval
        3,          // alarmRepeatCount
        50          // alarmHighTempThreshold
    };

    for ( const auto input : inputs )
    {
        try
        {
            configuration.alarmRepeatInterval = input;

            RfnSetTemperatureAlarmConfigurationCommand  command( configuration );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_RANGES( actual, expected );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration_construction_exceptions_repeat_count )
{
    // Currently there is no way to test underflow as we are data type limited to unsigned with a valid minimum of 0

    const std::vector< unsigned > inputs {
            400 };

    const std::vector< RfnCommand::CommandException >   expected {
            RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Repeat Count: (400) overflow (maximum: 255)" ) };

    std::vector< RfnCommand::CommandException > actual;

    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration =
    {
        true,       // alarmEnabled
        15,         // alarmRepeatInterval
        0,          // alarmRepeatCount
        50          // alarmHighTempThreshold
    };

    for ( const auto input : inputs )
    {
        try
        {
            configuration.alarmRepeatCount = input;

            RfnSetTemperatureAlarmConfigurationCommand  command( configuration );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_RANGES( actual, expected );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration_construction_exceptions_threshold )
{
    const std::vector< int > inputs {
            -50,
            200 };

    const std::vector< RfnCommand::CommandException >   expected {
            RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid High Temperature Threshold: (-50) underflow (minimum: -40)" ),
            RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid High Temperature Threshold: (200) overflow (maximum: 185)" ) };

    std::vector< RfnCommand::CommandException > actual;

    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration =
    {
        true,       // alarmEnabled
        15,         // alarmRepeatInterval
        3,          // alarmRepeatCount
        0           // alarmHighTempThreshold
    };

    for ( const auto input : inputs )
    {
        try
        {
            configuration.alarmHighTempThreshold = input;

            RfnSetTemperatureAlarmConfigurationCommand  command( configuration );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_RANGES( actual, expected );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration_decode_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
            { 0x8a, 0x00, 0x00, 0x00 },
            { 0x89, 0x01, 0x00, 0x00 },
            { 0x89, 0x00, 0x03, 0x00 },
            { 0x89, 0x00, 0x00, 0x01 },
            { 0x89, 0x00, 0x00, 0x01, 0x00, 0x00 } };

    const std::vector< RfnCommand::CommandException >   expected {
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x8a)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x01)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (3)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Incomplete data for TLV" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type (0x00)" ) };

    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration =
    {
        true,       // alarmEnabled
        15,         // alarmRepeatInterval
        3,          // alarmRepeatCount
        50          // alarmHighTempThreshold
    };

    RfnSetTemperatureAlarmConfigurationCommand  command( configuration );

    std::vector< RfnCommand::CommandException > actual;

    for ( const auto & response : responses )
    {
        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_RANGES( actual, expected );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__GetConfiguration )
{
    RfnGetTemperatureAlarmConfigurationCommand  command;

    // execute
    {
        const std::vector< unsigned char > exp {
                0x88, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
    // decode -- success response
    {
        const std::vector< unsigned char > response {
                0x89, 0x01, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nState: Alarm Enabled (1)"
                                            "\nHigh Temperature Threshold: 50 degrees (0x0032)"
                                            "\nLow Temperature Threshold: 40 degrees (0x0028)"
                                            "\nAlarm Repeat Interval: 15 minutes"
                                            "\nAlarm Repeat Count: 3 counts" );

        BOOST_CHECK_EQUAL( true, command.isSupported() );

        RfnTemperatureAlarmCommand::AlarmConfiguration configuration = command.getAlarmConfiguration();

        BOOST_CHECK_EQUAL( true, configuration.alarmEnabled );
        BOOST_CHECK_EQUAL(   50, configuration.alarmHighTempThreshold );
        BOOST_CHECK_EQUAL(   15, configuration.alarmRepeatInterval );
        BOOST_CHECK_EQUAL(    3, configuration.alarmRepeatCount );
    }
    // decode -- success response with negative threshold
    {
        const std::vector< unsigned char > response {
                0x89, 0x01, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0xff, 0xf0, 0xff, 0xe6, 0x0f, 0x03 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nState: Alarm Enabled (1)"
                                            "\nHigh Temperature Threshold: -16 degrees (0xfff0)"
                                            "\nLow Temperature Threshold: -26 degrees (0xffe6)"
                                            "\nAlarm Repeat Interval: 15 minutes"
                                            "\nAlarm Repeat Count: 3 counts" );

        BOOST_CHECK_EQUAL( true, command.isSupported() );

        RfnTemperatureAlarmCommand::AlarmConfiguration configuration = command.getAlarmConfiguration();

        BOOST_CHECK_EQUAL( true, configuration.alarmEnabled );
        BOOST_CHECK_EQUAL(  -16, configuration.alarmHighTempThreshold );
        BOOST_CHECK_EQUAL(   15, configuration.alarmRepeatInterval );
        BOOST_CHECK_EQUAL(    3, configuration.alarmRepeatCount );
    }
    // decode -- success response with singular degree, minute, count
    {
        const std::vector< unsigned char > response {
                0x89, 0x01, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x01, 0xff, 0xf7, 0x01, 0x01 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nState: Alarm Enabled (1)"
                                            "\nHigh Temperature Threshold: 1 degree (0x0001)"
                                            "\nLow Temperature Threshold: -9 degrees (0xfff7)"
                                            "\nAlarm Repeat Interval: 1 minute"
                                            "\nAlarm Repeat Count: 1 count" );

        BOOST_CHECK_EQUAL( true, command.isSupported() );

        RfnTemperatureAlarmCommand::AlarmConfiguration configuration = command.getAlarmConfiguration();

        BOOST_CHECK_EQUAL( true, configuration.alarmEnabled );
        BOOST_CHECK_EQUAL(    1, configuration.alarmHighTempThreshold );
        BOOST_CHECK_EQUAL(    1, configuration.alarmRepeatInterval );
        BOOST_CHECK_EQUAL(    1, configuration.alarmRepeatCount );
    }
    // decode -- unsupported response -- with TLV
    {
        const std::vector< unsigned char > response {
                0x89, 0x01, 0x02, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Unsupported (2)" );

        BOOST_CHECK_EQUAL( false, command.isSupported() );
    }
    // decode -- unsupported response -- without TLV
    {
        const std::vector< unsigned char > response {
                0x89, 0x01, 0x02, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Unsupported (2)" );

        BOOST_CHECK_EQUAL( false, command.isSupported() );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__GetConfiguration_decode_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
            { 0x8a, 0x01, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 },
            { 0x89, 0x00, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 },
            { 0x89, 0x01, 0x03, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 },
            { 0x89, 0x01, 0x01, 0x00 },
            { 0x89, 0x01, 0x00, 0x02,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03 },
            { 0x89, 0x01, 0x00, 0x01,
                    0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f } };

    const std::vector< RfnCommand::CommandException >   expected {
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x8a)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x00)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (3)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (4)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (2)" ),
            RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (12)" ) };

    RfnGetTemperatureAlarmConfigurationCommand  command;

    std::vector< RfnCommand::CommandException > actual;

    for ( const auto  & response : responses )
    {
        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_RANGES( actual, expected );
}


BOOST_AUTO_TEST_SUITE_END()

