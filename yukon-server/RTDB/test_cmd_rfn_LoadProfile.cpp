#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_LoadProfile.h"
#include "boost_test_helpers.h"

#include <boost/optional/optional_io.hpp>

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnVoltageProfileGetConfigurationCommand;
using Cti::Devices::Commands::RfnVoltageProfileSetConfigurationCommand;
using Cti::Devices::Commands::RfnLoadProfileRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileGetRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileSetTemporaryRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileSetPermanentRecordingCommand;
using Cti::Devices::Commands::RfnLoadProfileReadPointsCommand;


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
    RfnVoltageProfileSetConfigurationCommand  command( 60, 34 );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x68, 0x00, 0x01, 0x01, 0x00, 0x02, 0x04, 0x22 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x69, 0x00, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x00, 0x01, 0x00 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_SetConfiguration_constructor_exceptions )
{
    const std::vector< std::pair< unsigned, unsigned > > inputs {
        {   0,  15 },      // voltage averaging interval of 0 seconds
        {  64,  15 },      // voltage averaging interval not modulo 15 and not one of the allowed values
        { 300,   0 },      // load profile interval of 0 minutes
        { 300, 300 } };    // load profile interval > 255

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Voltage Averaging Interval: (0) invalid setting" ) },
        { RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Voltage Averaging Interval: (64) invalid setting" ) },
        { RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Load Profile Demand Interval: (0) underflow (minimum: 1)" ) },
        { RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Load Profile Demand Interval: (300) overflow (maximum: 255)" ) } };

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
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        { 0x6f,  0x00,  0x00,  0x00 },
        { 0x69,  0x01,  0x00,  0x00 },
        { 0x69,  0x00,  0x02,  0x00 },
        { 0x69,  0x00,  0x00,  0x01,  0x00,  0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x6f)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x01)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (2)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (1)" ) } };

    std::vector< RfnCommand::CommandException > actual;

    RfnVoltageProfileSetConfigurationCommand command( 60, 15 );

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
        const std::vector< unsigned char > exp {
            0x68, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x69, 0x01, 0x00, 0x01, 0x01, 0x00, 0x02, 0x04, 0x06 };


        BOOST_CHECK( ! command.getVoltageAveragingInterval() );
        BOOST_CHECK( ! command.getLoadProfileInterval() );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nVoltage Averaging interval: 60 seconds"
                                 "\nLoad Profile Demand interval: 6 minutes" );

        BOOST_CHECK_EQUAL(   60, *command.getVoltageAveragingInterval() );
        BOOST_CHECK_EQUAL(    6, *command.getLoadProfileInterval() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x01, 0x01, 0x01, 0x01, 0x00, 0x02, 0x04, 0x06 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        {0x69,  0x00,  0x00,  0x01,  0x01,  0x02,  0x04,  0x06 },
        {0x69,  0x01,  0x00,  0x02,  0x00,  0x00,  0x04,  0x00 },
        {0x69,  0x01,  0x00,  0x01,  0x04,  0x00,  0x00 },
        {0x69,  0x01,  0x00,  0x01,  0x01,  0x00,  0x01,  0x04 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x00)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (2)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type (4 != 1)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV length (1)" ) } };

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_DisableLoadProfileTemporaryRecording )
{
    RfnLoadProfileSetTemporaryRecordingCommand  command( RfnLoadProfileRecordingCommand::DisableRecording );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x68, 0x02, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x69, 0x02, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x02, 0x01, 0x00 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_DisableLoadProfileTemporaryRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        { 0x69, 0x03, 0x00, 0x00 },
        { 0x69, 0x02, 0x00, 0x01, 0x01, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x03)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (1)" ) } };

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileSetTemporaryRecordingCommand  command( RfnLoadProfileRecordingCommand::DisableRecording );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_EnableTemporaryLoadProfileRecording )
{
    RfnLoadProfileSetTemporaryRecordingCommand  command( RfnLoadProfileRecordingCommand::EnableRecording );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x68, 0x03, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x69, 0x03, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x03, 0x01, 0x00 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_EnableTemporaryLoadProfileRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        { 0x69, 0x02, 0x00, 0x00 },
        { 0x69, 0x03, 0x00, 0x01, 0x01, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x02)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (1)" ) } };

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileSetTemporaryRecordingCommand  command( RfnLoadProfileRecordingCommand::EnableRecording );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_DisableLoadProfilePermanentRecording )
{
    RfnLoadProfileSetPermanentRecordingCommand  command( RfnLoadProfileRecordingCommand::DisableRecording );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x68, 0x02, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x69, 0x02, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x02, 0x01, 0x00 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_DisableLoadProfilePermanentRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        { 0x69, 0x03, 0x00, 0x00 },
        { 0x69, 0x02, 0x00, 0x01, 0x01, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x03)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (1)" ) } };

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileSetPermanentRecordingCommand  command( RfnLoadProfileRecordingCommand::DisableRecording );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_EnablePermanentLoadProfileRecording )
{
    RfnLoadProfileSetPermanentRecordingCommand  command( RfnLoadProfileRecordingCommand::EnableRecording );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x68, 0x06, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x69, 0x06, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x06, 0x01, 0x00 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_EnablePermanentLoadProfileRecording_decoding_exceptions )
{
    const auto tz_override = Cti::Test::set_to_central_timezone();

    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        { 0x69, 0x02, 0x00, 0x00 },
        { 0x69, 0x06, 0x00, 0x01, 0x01, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x02)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (1)" ) } };

    std::vector< RfnCommand::CommandException > actual;

    RfnLoadProfileSetPermanentRecordingCommand  command( RfnLoadProfileRecordingCommand::EnableRecording );

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
    const auto tz_override = Cti::Test::set_to_central_timezone();

    RfnLoadProfileGetRecordingCommand  command;

    // execute
    {
        const std::vector< unsigned char > exp {
            0x68, 0x04, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response -- disabled -- old firmware style
    {
        const std::vector< unsigned char > response {
            0x69, 0x04, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nCurrent State: Disabled (0)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::DisableRecording, command.getRecordingOption() );

        BOOST_CHECK( ! command.getEndTime() );

        BOOST_CHECK_EQUAL( false, command.isPermanentEnabled() );
        BOOST_CHECK_EQUAL( false, command.isTemporaryEnabled() );
    }

    // decode -- success response -- enabled -- old firmware style
    {
        const std::vector< unsigned char > response {
            0x69, 0x04, 0x00, 0x01, 0x02, 0x00, 0x01, 0x01 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nCurrent State: Enabled (1)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::EnableRecording, command.getRecordingOption() );

        BOOST_CHECK( ! command.getEndTime() );

        BOOST_CHECK_EQUAL( false, command.isPermanentEnabled() );
        BOOST_CHECK_EQUAL( true,  command.isTemporaryEnabled() );
    }

    // decode -- success response -- enabled -- new firmware - permanent
    {
        const std::vector< unsigned char > response {
            0x69, 0x04, 0x00, 0x01, 0x05, 0x00, 0x00 };

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nCurrent State: Enabled (Permanent)" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::EnableRecording, command.getRecordingOption() );

        BOOST_CHECK( ! command.getEndTime() );

        BOOST_CHECK_EQUAL( true,  command.isPermanentEnabled() );
        BOOST_CHECK_EQUAL( false, command.isTemporaryEnabled() );
    }

    // decode -- success response -- enabled -- new firmware - temporary
    {
        const std::vector< unsigned char > response {
            0x69, 0x04, 0x00, 0x01, 0x06, 0x00, 0x04, 0x56, 0x28, 0x22, 0x16 };

        // 0x56282216 == 1445470742 == Wed, 21 Oct 2015 23:39:02 GMT

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)"
                                            "\nCurrent State: Enabled (Temporary)"
                                            "\nEnd Time: 10/21/2015 18:39:02" );

        BOOST_CHECK_EQUAL( RfnLoadProfileRecordingCommand::EnableRecording, command.getRecordingOption() );

        BOOST_CHECK_EQUAL( CtiTime( 1445470742 ), command.getEndTime() );

        BOOST_CHECK_EQUAL( false, command.isPermanentEnabled() );
        BOOST_CHECK_EQUAL( true,  command.isTemporaryEnabled() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x69, 0x04, 0x01, 0x01, 0x02, 0x00, 0x01, 0x00 };

        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfileRecording_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses {
        { 0x69, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00 },
        { 0x69, 0x04, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00 },
        { 0x69, 0x04, 0x00, 0x01, 0x01, 0x00, 0x01, 0x00 },
        { 0x69, 0x04, 0x00, 0x01, 0x02, 0x00, 0x02, 0x00, 0x00 },
        { 0x69, 0x04, 0x00, 0x01, 0x02, 0x00, 0x01, 0x03 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x02)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (2)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type (1)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV length (2)" ) },
        { RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid State (3)" ) } };


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
        const CtiDate end  ( 6,  7, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.what(), "End time must be after begin time (begin = 07/07/2013 00:00:00, end = 07/06/2013 00:00:00)");
        }
    }

    //  begin == end, after today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin(11, 8, 2013);
        const CtiDate end  (10, 8, 2013);

        try
        {
            RfnLoadProfileReadPointsCommand command(now, begin, end);

            BOOST_FAIL("Should have thrown");
        }
        catch ( const RfnCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.what(), "End time must be after begin time (begin = 08/11/2013 00:00:00, end = 08/10/2013 00:00:00)");
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
            BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.what(), "End time must be after begin time (begin = 08/09/2013 00:00:00, end = 07/07/2013 00:00:00)");
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
            BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.what(), "End time must be after begin time (begin = 08/15/2013 00:00:00, end = 08/12/2013 00:00:00)");
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
            BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.what(), "End time must be before now (end = 08/15/2013 00:00:00, now = 08/10/2013 08:23:00)");
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfilePoints )
{
    using Cti::Devices::Commands::RfnCommand;

    const auto tz_override = Cti::Test::set_to_central_timezone();

    //  begin == end, before today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin( 7, 7, 2013);
        const CtiDate end  ( 6, 8, 2013);

        RfnLoadProfileReadPointsCommand command(now, begin, end);

        // execute
        {
            const std::vector< unsigned char > exp {
                    0x68, 0x05, 0x01,
                    0x04,
                    0x00, 0x08, // 8 bytes
                    0x51, 0xd8, 0xf5, 0xd0,    // start timestamp
                    0x52, 0x00, 0x82, 0xd0 };  // end timestamp

            RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

            BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                           exp.begin() , exp.end() );
        }

        // decode
        {
            const unsigned interval_minutes = 0x12;

            const std::vector< unsigned char > response {
                   0x69, 0x05, 0x00, 0x01,
                   0x03,
                   0x00, 0x43, // tlv size = 67-byte
                   // report header
                   0x00, // channel number
                   0x10, // uom
                   0x80, 0x00, // uom modifier 1
                   0x00, 0x00, // uom modifier 2
                   interval_minutes,
                   0x04, // Number of profile point records
                   // record 1
                   0x51, 0xd8, 0xf5, 0xd0,
                   0x00, // 8-bit delta
                   0x01,
                   0x11, 0x00,
                   // record 2
                   0x51, 0xd8, 0xf5, 0xd1,
                   0x01, // 16-bit delta
                   0x02,
                   0x11, 0x12, 0x00,
                   0x21, 0x22, 0x01,
                   // record 3
                   0x51, 0xd8, 0xf5, 0xd2,
                   0x02, // 32-bit absolute
                   0x03,
                   0x11, 0x12, 0x13, 0x14, 0x00,
                   0x21, 0x22, 0x23, 0x24, 0x01,
                   0x31, 0x32, 0x33, 0x34, 0x02,
                   // record 4
                   0x51, 0xd8, 0xf5, 0xd3,
                   0x03, // 16-bit absolute
                   0x04,
                   0x11, 0x12, 0x00,
                   0x21, 0x22, 0x01,
                   0x31, 0x32, 0x02,
                   0x41, 0x42, 0x03 };

            RfnCommandResult rcv = command.decodeCommand( execute_time, response );

            BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );

            BOOST_REQUIRE_EQUAL( rcv.points.size(), 10 );

            const unsigned expected_offset  = 214;
            const unsigned interval_seconds = interval_minutes * 60;

            // record 1
            {
                const unsigned timestamp = 0x51d8f5d0;

                BOOST_CHECK_EQUAL( rcv.points[0].value,             0x11 );
                BOOST_CHECK_EQUAL( rcv.points[0].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[0].quality,           NormalQuality );
                BOOST_CHECK_EQUAL( rcv.points[0].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[0].time.seconds(),    timestamp + interval_seconds*0 );
            }

            // record 2
            {
                const unsigned timestamp = 0x51d8f5d1;

                BOOST_CHECK_EQUAL( rcv.points[1].value,             0x1112 );
                BOOST_CHECK_EQUAL( rcv.points[1].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[1].quality,           NormalQuality );
                BOOST_CHECK_EQUAL( rcv.points[1].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[1].time.seconds(),    timestamp + interval_seconds*0 );

                BOOST_CHECK_EQUAL( rcv.points[2].value,             0x2122 );
                BOOST_CHECK_EQUAL( rcv.points[2].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[2].quality,           InvalidQuality );
                BOOST_CHECK_EQUAL( rcv.points[2].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[2].time.seconds(),    timestamp + interval_seconds*1 );
            }

            // record 3
            {
                const unsigned timestamp = 0x51d8f5d2;

                BOOST_CHECK_EQUAL( rcv.points[3].value,             0x11121314 );
                BOOST_CHECK_EQUAL( rcv.points[3].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[3].quality,           NormalQuality );
                BOOST_CHECK_EQUAL( rcv.points[3].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[3].time.seconds(),    timestamp + interval_seconds*0 );

                BOOST_CHECK_EQUAL( rcv.points[4].value,             0x21222324 );
                BOOST_CHECK_EQUAL( rcv.points[4].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[4].quality,           InvalidQuality );
                BOOST_CHECK_EQUAL( rcv.points[4].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[4].time.seconds(),    timestamp + interval_seconds*1 );

                BOOST_CHECK_EQUAL( rcv.points[5].value,             0x31323334 );
                BOOST_CHECK_EQUAL( rcv.points[5].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[5].quality,           InvalidQuality );
                BOOST_CHECK_EQUAL( rcv.points[5].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[5].time.seconds(),    timestamp + interval_seconds*2 );
            }

            // record 4
            {
                const unsigned timestamp = 0x51d8f5d3;

                BOOST_CHECK_EQUAL( rcv.points[6].value,             0x1112 );
                BOOST_CHECK_EQUAL( rcv.points[6].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[6].quality,           NormalQuality );
                BOOST_CHECK_EQUAL( rcv.points[6].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[6].time.seconds(),    timestamp + interval_seconds*0 );

                BOOST_CHECK_EQUAL( rcv.points[7].value,             0x2122 );
                BOOST_CHECK_EQUAL( rcv.points[7].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[7].quality,           InvalidQuality );
                BOOST_CHECK_EQUAL( rcv.points[7].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[7].time.seconds(),    timestamp + interval_seconds*1 );

                BOOST_CHECK_EQUAL( rcv.points[8].value,             0x3132 );
                BOOST_CHECK_EQUAL( rcv.points[8].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[8].quality,           InvalidQuality );
                BOOST_CHECK_EQUAL( rcv.points[8].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[8].time.seconds(),    timestamp + interval_seconds*2 );

                BOOST_CHECK_EQUAL( rcv.points[9].value,             0x4142 );
                BOOST_CHECK_EQUAL( rcv.points[9].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[9].quality,           InvalidQuality );
                BOOST_CHECK_EQUAL( rcv.points[9].offset,            expected_offset );
                BOOST_CHECK_EQUAL( rcv.points[9].time.seconds(),    timestamp + interval_seconds*3 );
            }
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_LoadProfile_GetLoadProfilePoints_FloatingPoint )
{
    using Cti::Devices::Commands::RfnCommand;

    const auto tz_override = Cti::Test::set_to_central_timezone();

    //  begin == end, before today
    {
        const CtiTime now  (CtiDate(10, 8, 2013), 8, 23, 0);
        const CtiDate begin( 7, 7, 2013);
        const CtiDate end  ( 6, 8, 2013);

        RfnLoadProfileReadPointsCommand command(now, begin, end);

        // execute
        {
            const std::vector< unsigned char > exp {
                    0x68, 0x05, 0x01,
                    0x04,
                    0x00, 0x08, // 8 bytes
                    0x51, 0xd8, 0xf5, 0xd0,    // start timestamp
                    0x52, 0x00, 0x82, 0xd0 };  // end timestamp

            RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

            BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                           exp.begin() , exp.end() );
        }

        // decode
        {
            const std::vector< unsigned char > response {
                   0x69, 0x05, 0x00, 0x01,
                   0x03,
                   0x00, 0x11, // tlv size = 17-byte
                   // report header
                   0x00, // channel number
                   0x10, // uom
                   0x80, 0x00, // uom modifier 1
                   0x01, 0x40, // uom modifier 2
                   0x12, // 18 minute intervals
                   0x01, // Number of profile point records
                   // record 1
                   0x51, 0xd8, 0xf5, 0xd3,
                   0x03, // 16-bit absolute
                   0x01,
                   0x09, 0xd4, 0x00 };

            RfnCommandResult rcv = command.decodeCommand( execute_time, response );

            BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );

            BOOST_REQUIRE_EQUAL( rcv.points.size(), 1 );

            // record 1
            {
                const unsigned *raw_value = reinterpret_cast<const unsigned *>(&rcv.points[0].value);

                BOOST_CHECK_EQUAL( raw_value[0],                    0x33333333 );
                BOOST_CHECK_EQUAL( raw_value[1],                    0x406F7333 );
                BOOST_CHECK_CLOSE( rcv.points[0].value,             251.6, 1e-6 );
                BOOST_CHECK_EQUAL( rcv.points[0].type,              AnalogPointType );
                BOOST_CHECK_EQUAL( rcv.points[0].quality,           NormalQuality );
                BOOST_CHECK_EQUAL( rcv.points[0].offset,            214 );
                BOOST_CHECK_EQUAL( rcv.points[0].time.seconds(),    0x51d8f5d3 );
            }
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()

