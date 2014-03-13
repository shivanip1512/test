#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_RemoteDisconnect.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnRemoteDisconnectCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectSetOnDemandConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectSetThresholdConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectSetCyclingConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectGetConfigurationCommand;

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

const CtiTime execute_time( CtiDate( 6, 3, 2014 ) , 12 );

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_OnDemand_SetConfiguration )
{
    RfnRemoteDisconnectSetOnDemandConfigurationCommand cmd( RfnRemoteDisconnectCommand::Reconnect_Arm );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            (0x82)(0x00)(0x01)(0x01)(0x01)(0x00);

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x83)(0x00)(0x00)(0x01)(0x00);

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x83)(0x00)(0x01)(0x01)(0x00);

        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_DemandThreshold_SetConfiguration )
{
    RfnRemoteDisconnectSetThresholdConfigurationCommand cmd( RfnRemoteDisconnectCommand::Reconnect_Arm,
                                                             RfnRemoteDisconnectCommand::DemandInterval_Five,
                                                             5.0,
                                                             10,
                                                             0 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            (0x82)(0x00)(0x01)(0x02)(0x05)(0x00)(0x05)(0x32)(0x0a)(0x00);

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x83)(0x00)(0x00)(0x02)(0x00);

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x83)(0x00)(0x01)(0x02)(0x00);

        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_Cycling_SetConfiguration )
{
    RfnRemoteDisconnectSetCyclingConfigurationCommand cmd( 60, 256 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            (0x82)(0x00)(0x01)(0x03)(0x05)(0x01)(0x00)(0x3c)(0x01)(0x00);

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x83)(0x00)(0x00)(0x03)(0x00);

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0)" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x83)(0x00)(0x01)(0x03)(0x00);

        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ErrorInvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_DemandThreshold_SetConfiguration_constructor_exceptions )
{
    const std::vector< RfnCommand::CommandException > expected = list_of
        ( RfnCommand::CommandException( BADPARAM, "Invalid Demand Threshold: (-1.0) underflow (minimum 0.0)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Demand Threshold: (13.0) overflow (maximum 12.0)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Connect Delay: (31) overflow (maximum 30)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Max Disconnects: (21) overflow (maximum 20)" ) );

    std::vector< RfnCommand::CommandException > actual;

    // Demand threshold underflow 
    try
    {
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectCommand::DemandInterval_Five,
                                                                     -1.0,
                                                                     0,
                                                                     0);
    }
    catch ( const RfnCommand::CommandException & ex )
    {
        actual.push_back( ex );
    }

    // Demand threshold overflow 
    try
    {
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectCommand::DemandInterval_Five,
                                                                     13.0,
                                                                     0,
                                                                     0);
    }
    catch ( const RfnCommand::CommandException & ex )
    {
        actual.push_back( ex );
    }

    // Connect delay overflow
    try
    {
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectCommand::DemandInterval_Five,
                                                                     0.0,
                                                                     31,
                                                                     0);
    }
    catch ( const RfnCommand::CommandException & ex )
    {
        actual.push_back( ex );
    }

    // Max disconnects overflow
    try
    {
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectCommand::DemandInterval_Five,
                                                                     0.0,
                                                                     0,
                                                                     21);
    }
    catch ( const RfnCommand::CommandException & ex )
    {
        actual.push_back( ex );
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_Cycling_SetConfiguration_constructor_exceptions )
{
    const std::vector< std::pair< unsigned, unsigned > > inputs = pair_list_of
        (   60, 1441 )   // Connect minutes > 1440
        ( 1441,   60 );  // Disconnect minutes > 1440

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( BADPARAM, "Invalid Connect Minutes: (1441) overflow (maximum 1440)" ) )
        ( RfnCommand::CommandException( BADPARAM, "Invalid Disconnect Minutes: (1441) overflow (maximum 1440)" ) );

    std::vector< RfnCommand::CommandException > actual;

    for each ( const std::pair< unsigned, unsigned > & input in inputs )
    {
        try
        {
            RfnRemoteDisconnectSetCyclingConfigurationCommand command( input.first, input.second );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_OnDemand_SetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload > responses = list_of
        ( list_of( 0x8f )( 0x00 )( 0x00 )( 0x01 )( 0x00 ) )
        ( list_of( 0x83 )( 0x01 )( 0x00 )( 0x01 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x02 )( 0x01 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x00 )( 0x02 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x00 )( 0x01 )( 0x01 )( 0x00 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x8f)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid current disconnect mode received (2 != 1)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnRemoteDisconnectSetOnDemandConfigurationCommand command( RfnRemoteDisconnectCommand::Reconnect_Arm );

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandThreshold_SetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload > responses = list_of
        ( list_of( 0x8f )( 0x00 )( 0x00 )( 0x02 )( 0x00 ) )
        ( list_of( 0x83 )( 0x01 )( 0x00 )( 0x02 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x02 )( 0x02 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x00 )( 0x03 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x00 )( 0x02 )( 0x01 )( 0x00 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x8f)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid current disconnect mode received (3 != 2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectCommand::Reconnect_Arm,
                                                                 RfnRemoteDisconnectCommand::DemandInterval_Five,
                                                                 10.0,
                                                                 10,
                                                                 10 );

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_Cycling_SetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload > responses = list_of
        ( list_of( 0x8f )( 0x00 )( 0x00 )( 0x03 )( 0x00 ) )
        ( list_of( 0x83 )( 0x01 )( 0x00 )( 0x03 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x02 )( 0x03 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x00 )( 0x01 )( 0x00 ) )
        ( list_of( 0x83 )( 0x00 )( 0x00 )( 0x03 )( 0x01 )( 0x00 )( 0x00 ) );

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Response Command Code (0x8f)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Operation Code (0x01)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid Status (2)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid current disconnect mode received (1 != 3)" ) )
        ( RfnCommand::CommandException( ErrorInvalidData, "Invalid TLV count (1)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnRemoteDisconnectSetCyclingConfigurationCommand command( 60, 60 );

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_GetConfiguration_OnDemand )
{
    RfnRemoteDisconnectGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x82 )( 0x01 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x83 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x01 )( 0x01 )( 0x01 );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DisconnectMode_OnDemand, command.getDisconnectMode() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::Reconnect_Arm, command.getReconnectParam() );

        // These should be empty to begin
        BOOST_CHECK( ! command.getConnectDelay() );
        BOOST_CHECK( ! command.getMaxDisconnects() );
        BOOST_CHECK( ! command.getDemandInterval() );
        BOOST_CHECK( ! command.getDemandThreshold() );
        BOOST_CHECK( ! command.getConnectMinutes() );
        BOOST_CHECK( ! command.getDisconnectMinutes() );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nDisconnect mode: On Demand"
                                 "\nReconnect param: Immediate reconnect" );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DisconnectMode_OnDemand, command.getDisconnectMode() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::Reconnect_Immediate, command.getReconnectParam() );

        // Still shouldn't have any of these
        BOOST_CHECK( ! command.getConnectDelay() );
        BOOST_CHECK( ! command.getMaxDisconnects() );
        BOOST_CHECK( ! command.getDemandInterval() );
        BOOST_CHECK( ! command.getDemandThreshold() );
        BOOST_CHECK( ! command.getConnectMinutes() );
        BOOST_CHECK( ! command.getDisconnectMinutes() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x83 )( 0x01 )( 0x01 )( 0x01 )( 0x01 )( 0x01 )( 0x01 )( 0x01 );

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_GetConfiguration_DemandThreshold )
{
    RfnRemoteDisconnectGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x82 )( 0x01 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x83 )( 0x01 )( 0x00 )( 0x02 )( 0x01 )( 0x02 )( 0x05 )( 0x01 )( 0x0f )( 0x64 )( 0x0a )( 0x00 );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DisconnectMode_OnDemand, command.getDisconnectMode() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::Reconnect_Arm, command.getReconnectParam() );

        // These should be empty to begin
        BOOST_CHECK( ! command.getConnectDelay() );
        BOOST_CHECK( ! command.getMaxDisconnects() );
        BOOST_CHECK( ! command.getDemandInterval() );
        BOOST_CHECK( ! command.getDemandThreshold() );
        BOOST_CHECK( ! command.getConnectMinutes() );
        BOOST_CHECK( ! command.getDisconnectMinutes() );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nDisconnect mode: Demand Threshold"
                                 "\nReconnect param: Immediate reconnect"
                                 "\nDisconnect demand interval: 15 minutes" 
                                 "\nDisconnect demand threshold: 10.0 kW"
                                 "\nConnect delay: 10 minutes"
                                 "\nMax disconnects: disable" );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DisconnectMode_DemandThreshold, command.getDisconnectMode() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::Reconnect_Immediate, command.getReconnectParam() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DemandInterval_Fifteen,  *command.getDemandInterval() );
        BOOST_CHECK_EQUAL(   10, *command.getConnectDelay() );
        BOOST_CHECK_EQUAL(    0, *command.getMaxDisconnects() );
        BOOST_CHECK_EQUAL( 10.0, *command.getDemandThreshold() );

        // Still shouldn't have any of these
        BOOST_CHECK( ! command.getConnectMinutes() );
        BOOST_CHECK( ! command.getDisconnectMinutes() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x83 )( 0x01 )( 0x01 )( 0x02 )( 0x01 )( 0x02 )( 0x05 )( 0x01 )( 0x0f )( 0x64 )( 0x0a )( 0x00 );

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_GetConfiguration_Cycling )
{
    RfnRemoteDisconnectGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x82 )( 0x01 )( 0x00 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x83 )( 0x01 )( 0x00 )( 0x03 )( 0x01 )( 0x03 )( 0x05 )( 0x01 )( 0x03 )( 0xe8 )( 0x00 )( 0x3c );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DisconnectMode_OnDemand, command.getDisconnectMode() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::Reconnect_Arm, command.getReconnectParam() );

        // These should be empty to begin
        BOOST_CHECK( ! command.getConnectDelay() );
        BOOST_CHECK( ! command.getMaxDisconnects() );
        BOOST_CHECK( ! command.getDemandInterval() );
        BOOST_CHECK( ! command.getDemandThreshold() );
        BOOST_CHECK( ! command.getConnectMinutes() );
        BOOST_CHECK( ! command.getDisconnectMinutes() );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nDisconnect mode: Cycling"
                                 "\nReconnect param: Immediate reconnect"
                                 "\nDisconnect minutes: 1000"
                                 "\nConnect minutes: 60" );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::DisconnectMode_Cycling, command.getDisconnectMode() );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectCommand::Reconnect_Immediate, command.getReconnectParam() );
        BOOST_CHECK_EQUAL( 1000, *command.getDisconnectMinutes() );
        BOOST_CHECK_EQUAL( 60, *command.getConnectMinutes() );

        // Still shouldn't have any of these
        BOOST_CHECK( ! command.getConnectDelay() );
        BOOST_CHECK( ! command.getMaxDisconnects() );
        BOOST_CHECK( ! command.getDemandInterval() );
        BOOST_CHECK( ! command.getDemandThreshold() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x83 )( 0x01 )( 0x01 )( 0x03 )( 0x01 )( 0x03 )( 0x05 )( 0x01 )( 0x03 )( 0xe8 )( 0x00 )( 0x3c );

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

BOOST_AUTO_TEST_SUITE_END()
