#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_LoadProfile.h"


using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnVoltageProfileConfigurationCommand;
using Cti::Devices::Commands::RfnLoadProfileRecordingCommand;

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


struct test_ConfigResultHandler : RfnVoltageProfileConfigurationCommand::ResultHandler
{
    void handleResult(const RfnVoltageProfileConfigurationCommand &cmd)
    {
        //  This is temporarily marked BOOST_FAIL - the unit test should eventually exercise this.
        BOOST_FAIL("Should not reach this code!");
    }
};


struct test_RecordingResultHandler : RfnLoadProfileRecordingCommand::ResultHandler
{
    void handleResult(const RfnLoadProfileRecordingCommand &cmd)
    {
        //  This is temporarily marked BOOST_FAIL - the unit test should eventually exercise this.
        BOOST_FAIL("Should not reach this code!");
    }
};


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_SetConfiguration )
{
    test_ConfigResultHandler rh;

    RfnVoltageProfileConfigurationCommand  command( rh, 255, 34 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x00 )( 0x01 )( 0x01 )( 0x02 )( 0x11 )( 0x22 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 ) ( 0x00 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
            test_ConfigResultHandler rh;

            RfnVoltageProfileConfigurationCommand  command( rh, input.first, input.second );
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
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
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

    test_ConfigResultHandler rh;

    RfnVoltageProfileConfigurationCommand  command( rh, 300, 15 );

    for each ( const RfnCommand::RfnResponse & response in responses )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    test_ConfigResultHandler rh;

    RfnVoltageProfileConfigurationCommand  command( rh );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x01 )( 0x00 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x02 )( 0x04 )( 0x06 );

        BOOST_CHECK_EQUAL( 0, command.getDemandIntervalSeconds() );
        BOOST_CHECK_EQUAL( 0, command.getLoadProfileIntervalMinutes() );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

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

        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
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

    test_ConfigResultHandler rh;

    RfnVoltageProfileConfigurationCommand  command( rh );

    for each ( const RfnCommand::RfnResponse & response in responses )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    test_RecordingResultHandler rh;

    RfnLoadProfileRecordingCommand  command( rh, RfnLoadProfileRecordingCommand::DisableRecording );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x02 )( 0x00 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x02 )( 0x00 )( 0x00 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x02 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
        ( list_of( 0x69 )( 0x02 )( 0x00 )( 0x01 ) )
        ( list_of( 0x69 )( 0x02 )( 0x00 ) )
        ( list_of( 0x69 )( 0x02 )( 0x00 )( 0x01 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (5)" ) );

    std::vector< RfnCommand::CommandException > actual;

    test_RecordingResultHandler rh;

    RfnLoadProfileRecordingCommand  command( rh, RfnLoadProfileRecordingCommand::DisableRecording );

    for each ( const RfnCommand::RfnResponse & response in responses )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    test_RecordingResultHandler rh;

    RfnLoadProfileRecordingCommand  command( rh, RfnLoadProfileRecordingCommand::EnableRecording );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x03 )( 0x00 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x03 )( 0x00 )( 0x00 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x03 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
        ( list_of( 0x69 )( 0x03 )( 0x00 )( 0x01 ) )
        ( list_of( 0x69 )( 0x03 )( 0x00 ) )
        ( list_of( 0x69 )( 0x03 )( 0x00 )( 0x01 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response length (5)" ) );

    std::vector< RfnCommand::CommandException > actual;

    test_RecordingResultHandler rh;

    RfnLoadProfileRecordingCommand  command( rh, RfnLoadProfileRecordingCommand::EnableRecording );

    for each ( const RfnCommand::RfnResponse & response in responses )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    test_RecordingResultHandler rh;

    RfnLoadProfileRecordingCommand  command( rh );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x68 )( 0x04 )( 0x00 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response -- disabled
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 )( 0x00 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" 
                                              "\nCurrent State: Disabled (0)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::DisableRecording, command.getRecordingOption() );
    }

    // decode -- success response -- enabled
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x04 )( 0x00 )( 0x01 )( 0x02 )( 0x01 )( 0x01 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" 
                                              "\nCurrent State: Enabled (1)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::EnableRecording, command.getRecordingOption() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x69 )( 0x04 )( 0x01 )( 0x01 )( 0x02 )( 0x01 )( 0x00 );

        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
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

    test_RecordingResultHandler rh;

    RfnLoadProfileRecordingCommand  command( rh );

    for each ( const RfnCommand::RfnResponse & response in responses )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, response );
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

