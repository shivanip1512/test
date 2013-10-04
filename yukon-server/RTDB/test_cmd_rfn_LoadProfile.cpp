#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_LoadProfile.h"
#include "boost_test_helpers.h"


using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnVoltageProfileGetConfigurationCommand;
using Cti::Devices::Commands::RfnVoltageProfileSetConfigurationCommand;
using Cti::Devices::Commands::RfnLoadProfileRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileGetRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileSetRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileReadPointsCommand;

using boost::assign::list_of;
using boost::assign::pair_list_of;


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



BOOST_AUTO_TEST_SUITE( test_cmd_rfn_LoadProfile )


const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_SetConfiguration )
{
    RfnVoltageProfileSetConfigurationCommand  command( 255, 34 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x00 )( 0x01 )( 0x01 )( 0x02 )( 0x11 )( 0x22 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 ) ( 0x00 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_SetConfiguration_constructor_exceptions )
{
    const std::vector< std::pair< unsigned, unsigned > > inputs = pair_list_of
        (    0,  15 )       // demand interval of 0 seconds
        ( 3900,  15 )       // demand interval > 255 * 15 == 3825
        (  301,  15 )       // demand interval not divisible by 15
        (  300,   0 )       // load profile interval of 0 minutes
        (  300, 300 );      // load profile interval > 255

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( BADPARAM, "Invalid Voltage Demand Interval: (0) underflow (minimum: 15)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Voltage Demand Interval: (3900) overflow (maximum: 3825)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Voltage Demand Interval: (301) not divisible by 15" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Load Profile Demand Interval: (0) underflow (minimum: 1)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Load Profile Demand Interval: (300) overflow (maximum: 255)" ) );

    std::vector< RfnCommand::CommandException > actual;

    for each ( const std::pair< unsigned, unsigned > & input in inputs )
    {
        try
        {
            RfnVoltageProfileSetConfigurationCommand command( input.first, input.second );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_SetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x6f )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x69 )( 0x01 )( 0x00 )( 0x00 ) )
        ( list_of( 0x69 )( 0x00 )( 0x02 )( 0x00 ) )
        ( list_of( 0x69 )( 0x00 )( 0x00 )( 0x01 ) )
        ( list_of( 0x69 )( 0x00 )( 0x00 ) )
        ( list_of( 0x69 )( 0x00 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x69 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x6f)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (5)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (8)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnVoltageProfileSetConfigurationCommand command( 300, 15 );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetConfiguration )
{
    RfnVoltageProfileGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x01 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x02 )( 0x04 )( 0x06 );

        BOOST_CHECK_EQUAL( 0, command.getDemandIntervalSeconds() );
        BOOST_CHECK_EQUAL( 0, command.getLoadProfileIntervalMinutes() );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nVoltage Demand interval: 60 seconds"
                                 "\nLoad Profile Demand interval: 6 minutes" );

        BOOST_CHECK_EQUAL( 60, command.getDemandIntervalSeconds() );
        BOOST_CHECK_EQUAL(  6, command.getLoadProfileIntervalMinutes() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x01 )( 0x01 )( 0x01 )( 0x01 )( 0x02 )( 0x04 )( 0x06 );

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x69 )( 0x01 )( 0x00 )( 0x02 )( 0x01 )( 0x02 )( 0x04 )( 0x06 ) )
        ( list_of( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x02 )( 0x02 )( 0x04 )( 0x06 ) )
        ( list_of( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x01 )( 0x04 )( 0x06 ) )
        ( list_of( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x02 )( 0x04 ) )
        ( list_of( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x02 )( 0x04 )( 0x06 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV type (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV length (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (7)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (9)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnVoltageProfileGetConfigurationCommand  command;

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_DisableLoadProfileRecording )
{
    RfnLoadProfileSetRecordingCommand  command( RfnLoadProfileRecordingCommand::DisableRecording );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x02 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x02 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x02 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_DisableLoadProfileRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x69 )( 0x02 )( 0x00 )( 0x01 ) )
        ( list_of( 0x69 )( 0x02 )( 0x00 ) )
        ( list_of( 0x69 )( 0x02 )( 0x00 )( 0x01 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (5)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileSetRecordingCommand  command( RfnLoadProfileRecordingCommand::DisableRecording );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_EnableLoadProfileRecording )
{
    RfnLoadProfileSetRecordingCommand  command( RfnLoadProfileRecordingCommand::EnableRecording );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x03 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x03 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x03 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_EnableLoadProfileRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x69 )( 0x03 )( 0x00 )( 0x01 ) )
        ( list_of( 0x69 )( 0x03 )( 0x00 ) )
        ( list_of( 0x69 )( 0x03 )( 0x00 )( 0x01 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (5)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileSetRecordingCommand  command( RfnLoadProfileRecordingCommand::EnableRecording );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfileRecording )
{
    RfnLoadProfileGetRecordingCommand  command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x04 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response -- disabled
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                              "\nCurrent State: Disabled (0)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::DisableRecording, command.getRecordingOption() );
    }

    // decode -- success response -- enabled
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 )( 0x01 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                              "\nCurrent State: Enabled (1)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::EnableRecording, command.getRecordingOption() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x04 )( 0x01 )( 0x01 )( 0x02 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfileRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = list_of
        ( list_of( 0x69 )( 0x04 )( 0x00 )( 0x02 )( 0x02 )( 0x01 )( 0x00 ) )
        ( list_of( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x01 )( 0x01 )( 0x00 ) )
        ( list_of( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x02 )( 0x00 ) )
        ( list_of( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 )( 0x03 ) )
        ( list_of( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 ) )
        ( list_of( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 )( 0x00 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV type (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV length (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid State (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (6)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (8)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileGetRecordingCommand  command;

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfilePoints_invalid_dates )
{
    //  begin == end, before today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin( 7,  7, 2013);
        const CtiDate end  ( 7,  7, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, BADPARAM);
            BOOST_CHECK_EQUAL(ex.what(), "End date must be before begin date (begin = 07/07/2013, end = 07/07/2013)");
        }
    }

    //  begin == end, after today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin(11, 8, 2013);
        const CtiDate end  (11, 8, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, BADPARAM);
            BOOST_CHECK_EQUAL(ex.what(), "End date must be before begin date (begin = 08/11/2013, end = 08/11/2013)");
        }
    }

    //  begin > end, before today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin( 9,  8, 2013);
        const CtiDate end  ( 7,  7, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, BADPARAM);
            BOOST_CHECK_EQUAL(ex.what(), "End date must be before begin date (begin = 08/09/2013, end = 07/07/2013)");
        }
    }

    //  begin > end, after today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin(15,  8, 2013);
        const CtiDate end  (12,  8, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, BADPARAM);
            BOOST_CHECK_EQUAL(ex.what(), "End date must be before begin date (begin = 08/15/2013, end = 08/12/2013)");
        }
    }

    //  end after today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin(12,  8, 2013);
        const CtiDate end  (15,  8, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, BADPARAM);
            BOOST_CHECK_EQUAL(ex.what(), "End date must be before today (end = 08/12/2013, now = 08/10/2013)");
        }
    }
}

void printData( std::vector< unsigned char > data )
{
    printf("data :\n");

    for each ( unsigned char val in data  )
    {
        printf("( 0x%02x )\n", val);
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfilePoints )
{
    using Cti::Devices::Commands::RfnCommand;

    Cti::Test::set_to_central_timezone();

    //  begin == end, before today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin( 7, 7, 2013);
        const CtiDate end  ( 7, 8, 2013);

        RfnLoadProfileReadPointsCommand command(now, begin, end);

        // execute
        {
            const std::vector< unsigned char > exp = boost::assign::list_of
                    ( 0x68 )( 0x05 )( 0x01 )
                    ( 0x04 )
                    ( 0x08 ) // 8 bytes
                    ( 0x51 )( 0xd8 )( 0xf5 )( 0xd0 )  // start timestamp
                    ( 0x52 )( 0x01 )( 0xd4 )( 0x50 ); // end timestamp

            RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

            BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                           exp.begin() , exp.end() );
    }

        // decode
        {
           const std::vector< unsigned char > response = boost::assign::list_of
                   ( 0x69 )( 0x05 )( 0x00 )( 0x01 )
                   ( 0x03 )
                   ( 0x43 ) // tlv size = 67-byte
                   // report header
                   ( 0x00 ) // channel number
                   ( 0x10 ) // uom
                   ( 0x80 )( 0x00 ) // uom modifier 1
                   ( 0x00 )( 0x00 ) // uom modifier 1
                   ( 0x45 ) // profile interval
                   ( 0x04 ) // Number of profile point records
                   // record 1
                   ( 0x51 )( 0xd8 )( 0xf5 )( 0xd0 )
                   ( 0x00 ) // 8-bit delta
                   ( 0x01 )
                   ( 0x11 )( 0x00 )
                   // record 2
                   ( 0x51 )( 0xd8 )( 0xf5 )( 0xd1 )
                   ( 0x01 ) // 16-bit delta
                   ( 0x02 )
                   ( 0x11 )( 0x12 )( 0x00 )
                   ( 0x21 )( 0x22 )( 0x01 )
                   // record 3
                   ( 0x51 )( 0xd8 )( 0xf5 )( 0xd2 )
                   ( 0x02 ) // 32-bit absolute
                   ( 0x03 )
                   ( 0x11 )( 0x12 )( 0x13 )( 0x14 )( 0x00 )
                   ( 0x21 )( 0x22 )( 0x23 )( 0x24 )( 0x01 )
                   ( 0x31 )( 0x32 )( 0x33 )( 0x34 )( 0x02 )
                   // record 4
                   ( 0x51 )( 0xd8 )( 0xf5 )( 0xd3 )
                   ( 0x03 ) // 16-bit absolute
                   ( 0x04 )
                   ( 0x11 )( 0x12 )( 0x00 )
                   ( 0x21 )( 0x22 )( 0x01 )
                   ( 0x31 )( 0x32 )( 0x02 )
                   ( 0x41 )( 0x42 )( 0x03 );

            RfnCommandResult rcv = command.decodeCommand( execute_time, response );

            BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );

            BOOST_REQUIRE_EQUAL( rcv.points.size(), 10 );

            const unsigned expected_offset = 214;

            // record 1

            BOOST_CHECK_EQUAL( rcv.points[0].value,     0x11 );
            BOOST_CHECK_EQUAL( rcv.points[0].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[0].quality,   NormalQuality );
            BOOST_CHECK_EQUAL( rcv.points[0].offset,    expected_offset );

            // record 2

            BOOST_CHECK_EQUAL( rcv.points[1].value,     0x1112 );
            BOOST_CHECK_EQUAL( rcv.points[1].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[1].quality,   NormalQuality );
            BOOST_CHECK_EQUAL( rcv.points[1].offset,    expected_offset );

            BOOST_CHECK_EQUAL( rcv.points[2].value,     0x2122 );
            BOOST_CHECK_EQUAL( rcv.points[2].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[2].quality,   InvalidQuality );
            BOOST_CHECK_EQUAL( rcv.points[2].offset,    expected_offset );

            // record 3

            BOOST_CHECK_EQUAL( rcv.points[3].value,     0x11121314 );
            BOOST_CHECK_EQUAL( rcv.points[3].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[3].quality,   NormalQuality );
            BOOST_CHECK_EQUAL( rcv.points[3].offset,    expected_offset );

            BOOST_CHECK_EQUAL( rcv.points[4].value,     0x21222324 );
            BOOST_CHECK_EQUAL( rcv.points[4].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[4].quality,   InvalidQuality );
            BOOST_CHECK_EQUAL( rcv.points[4].offset,    expected_offset );

            BOOST_CHECK_EQUAL( rcv.points[5].value,     0x31323334 );
            BOOST_CHECK_EQUAL( rcv.points[5].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[5].quality,   InvalidQuality );
            BOOST_CHECK_EQUAL( rcv.points[5].offset,    expected_offset );

            // record 4

            BOOST_CHECK_EQUAL( rcv.points[6].value,     0x1112 );
            BOOST_CHECK_EQUAL( rcv.points[6].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[6].quality,   NormalQuality );
            BOOST_CHECK_EQUAL( rcv.points[6].offset,    expected_offset );

            BOOST_CHECK_EQUAL( rcv.points[7].value,     0x2122 );
            BOOST_CHECK_EQUAL( rcv.points[7].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[7].quality,   InvalidQuality );
            BOOST_CHECK_EQUAL( rcv.points[7].offset,    expected_offset );

            BOOST_CHECK_EQUAL( rcv.points[8].value,     0x3132 );
            BOOST_CHECK_EQUAL( rcv.points[8].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[8].quality,   InvalidQuality );
            BOOST_CHECK_EQUAL( rcv.points[8].offset,    expected_offset );

            BOOST_CHECK_EQUAL( rcv.points[9].value,     0x4142 );
            BOOST_CHECK_EQUAL( rcv.points[9].type,      AnalogPointType );
            BOOST_CHECK_EQUAL( rcv.points[9].quality,   InvalidQuality );
            BOOST_CHECK_EQUAL( rcv.points[9].offset,    expected_offset );
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()

