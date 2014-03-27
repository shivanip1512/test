#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_TemperatureAlarm.h"
#include "boost_test_helpers.h"


using boost::assign::list_of;

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnTemperatureAlarmCommand;
using Cti::Devices::Commands::RfnSetTemperatureAlarmConfigurationCommand;
using Cti::Devices::Commands::RfnGetTemperatureAlarmConfigurationCommand;


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


BOOST_AUTO_TEST_SUITE( test_cmd_rfn_TemperatureAlarm )


const CtiTime execute_time( CtiDate( 25, 3, 2014 ) , 15 );


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration )
{
    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration = 
    {
        true,       // alarmEnabled
        15,         // alarmRepeatInterval
        2,          // alarmRepeatCount
        50          // alarmHighTempThreshold
    };

    RfnSetTemperatureAlarmConfigurationCommand  command( configuration );

    // execute
    {
        const std::vector< unsigned char > exp = list_of
            ( 0x88 )( 0x00 )( 0x01 )
                ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x02 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
    // decode -- success response
    {
        const std::vector< unsigned char > response = list_of
            ( 0x89 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }
    // decode -- failure response
    {
        const std::vector< unsigned char > response = list_of
            ( 0x89 )( 0x00 )( 0x01 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Failure (1)" );
    }
    // decode -- unsupported response
    {
        const std::vector< unsigned char > response = list_of
            ( 0x89 )( 0x00 )( 0x02 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Unsupported (2)" );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration_construction_exceptions )
{

        // Input validation?

    BOOST_CHECK( true );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__SetConfiguration_decode_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x8a )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x89 )( 0x01 )( 0x00 )( 0x00 ) )
        ( list_of( 0x89 )( 0x00 )( 0x03 )( 0x00 ) )
        ( list_of( 0x89 )( 0x00 )( 0x00 )( 0x01 ) )
        ( list_of( 0x89 )( 0x00 )( 0x00 )( 0x01 )( 0x00 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x8a)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (6)" ) );

    RfnTemperatureAlarmCommand::AlarmConfiguration  configuration = 
    {
        true,       // alarmEnabled
        15,         // alarmRepeatInterval
        2,          // alarmRepeatCount
        50          // alarmHighTempThreshold
    };

    RfnSetTemperatureAlarmConfigurationCommand  command( configuration );

    std::vector< RfnCommand::CommandException > actual;

    for each ( const RfnCommand::RfnResponsePayload & response in responses )
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

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__GetConfiguration )
{
    RfnGetTemperatureAlarmConfigurationCommand  command;

    // execute
    {
        const std::vector< unsigned char > exp = list_of
            ( 0x88 )( 0x01 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
    // decode -- success response
    {
        const std::vector< unsigned char > response = list_of
            ( 0x89 )( 0x01 )( 0x00 )( 0x01 )
                ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x02 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nState: Alarm Enabled (1)"
                                            "\nHigh Temperature Threshold: 50 degree(s) (0x0032)"
                                            "\nAlarm Repeat Interval: 15 minute(s)"
                                            "\nAlarm Repeat Count: 2 count(s)" );

        RfnTemperatureAlarmCommand::AlarmConfiguration configuration = command.getAlarmConfiguration();

        BOOST_CHECK_EQUAL( true, configuration.alarmEnabled );
        BOOST_CHECK_EQUAL(   50, configuration.alarmHighTempThreshold );
        BOOST_CHECK_EQUAL(   15, configuration.alarmRepeatInterval );
        BOOST_CHECK_EQUAL(    2, configuration.alarmRepeatCount );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_TemperatureAlarm__GetConfiguration_decode_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x8a )( 0x01 )( 0x00 )( 0x01 )
                    ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x02 ) )
        ( list_of( 0x89 )( 0x00 )( 0x00 )( 0x01 )
                    ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x02 ) )
        ( list_of( 0x89 )( 0x01 )( 0x03 )( 0x01 )
                    ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x02 ) )
        ( list_of( 0x89 )( 0x01 )( 0x00 )( 0x02 )
                    ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x02 ) )
        ( list_of( 0x89 )( 0x01 )( 0x00 )( 0x01 )
                    ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x8a)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x00)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (12)" ) );

    RfnGetTemperatureAlarmConfigurationCommand  command;

    std::vector< RfnCommand::CommandException > actual;

    for each ( const RfnCommand::RfnResponsePayload & response in responses )
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

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}


BOOST_AUTO_TEST_SUITE_END()

