#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_RemoteDisconnect.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnRemoteDisconnectConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectSetOnDemandConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectSetThresholdConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectSetCyclingConfigurationCommand;
using Cti::Devices::Commands::RfnRemoteDisconnectGetConfigurationCommand;

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
    RfnRemoteDisconnectSetOnDemandConfigurationCommand cmd( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x82, 0x00, 0x01, 0x01, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x83, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x00 };

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nDisconnect mode: On Demand"
                                 "\nReconnect param: Arm reconnect" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x83, 0x00, 0x01, 0x01, 0x00 };

        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_DemandThreshold_SetConfiguration )
{
    RfnRemoteDisconnectSetThresholdConfigurationCommand cmd( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm,
                                                             RfnRemoteDisconnectConfigurationCommand::DemandInterval_Five,
                                                             5.0,
                                                             10,
                                                             0 );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x82, 0x00, 0x01, 0x02, 0x05, 0x00, 0x05, 0x32, 0x0a, 0x00 };

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x83, 0x00, 0x00, 0x02, 0x01, 0x02, 0x05, 0x01, 0x0f, 0x64, 0x0a, 0x00 };

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nDisconnect mode: Demand Threshold"
                                 "\nReconnect param: Immediate reconnect"
                                 "\nDisconnect demand interval: 15 minutes"
                                 "\nDisconnect demand threshold: 10.0 kW"
                                 "\nConnect delay: 10 minutes"
                                 "\nMax disconnects: disable" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x83, 0x00, 0x01, 0x02, 0x00 };

        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_Cycling_SetConfiguration )
{
    RfnRemoteDisconnectSetCyclingConfigurationCommand cmd( 60, 256 );

    // execute
    {
        const std::vector< unsigned char > exp {
            0x82, 0x00, 0x01, 0x03, 0x05, 0x01, 0x00, 0x3c, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x83, 0x00, 0x00, 0x03, 0x01, 0x03, 0x05, 0x01, 0x03, 0xe8, 0x00, 0x3c };

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description,
                                 "Status: Success (0)"
                                 "\nDisconnect mode: Cycling"
                                 "\nReconnect param: Immediate reconnect"
                                 "\nDisconnect minutes: 1000"
                                 "\nConnect minutes: 60" );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x83, 0x00, 0x01, 0x03, 0x00 };

        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::InvalidData );
            BOOST_CHECK_EQUAL( ex.what(),     "Status: Failure (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_DemandThreshold_SetConfiguration_constructor_exceptions )
{
    const std::vector< RfnCommand::CommandException > expected {
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Demand Threshold: (0.0) underflow (minimum 0.5)" ),
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Demand Threshold: (13.0) overflow (maximum 12.0)" ),
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Connect Delay: (31) overflow (maximum 30)" ),
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Max Disconnects: (21) overflow (maximum 20)" ) };

    std::vector< RfnCommand::CommandException > actual;

    // Demand threshold underflow
    try
    {
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectConfigurationCommand::DemandInterval_Five,
                                                                     0.0,
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
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectConfigurationCommand::DemandInterval_Five,
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
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectConfigurationCommand::DemandInterval_Five,
                                                                     0.5,
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
        RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm,
                                                                     RfnRemoteDisconnectConfigurationCommand::DemandInterval_Five,
                                                                     0.5,
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
    const std::vector< std::pair< unsigned, unsigned > > inputs{
        {   60,    4 },   // Connect minutes < 5
        {   60, 1441 },   // Connect minutes > 1440
        {    4,   60 },   // Disconnect minutes < 5
        { 1441,   60 }};  // Disconnect minutes > 1440

    const std::vector< RfnCommand::CommandException >   expected {
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Connect Minutes: (4) underflow (minimum 5)" ),
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Connect Minutes: (1441) overflow (maximum 1440)" ),
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Disconnect Minutes: (4) underflow (minimum 5)" ),
        RfnCommand::CommandException( ClientErrors::BadParameter, "Invalid Disconnect Minutes: (1441) overflow (maximum 1440)" ) };

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
    const std::vector< RfnCommand::RfnResponsePayload > responses {
        { 0x8f, 0x00, 0x00, 0x01, 0x00 },
        { 0x83, 0x01, 0x00, 0x01, 0x00 },
        { 0x83, 0x00, 0x02, 0x01, 0x00 },
        { 0x83, 0x00, 0x00, 0x02, 0x00 },
        { 0x83, 0x00, 0x00, 0x01, 0x01, 0x00, 0x00 } 
    };

    const std::vector< RfnCommand::CommandException >   expected {
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x8f)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x01)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (2)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (0)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type received in response (0)" ) };

    std::vector< RfnCommand::CommandException > actual;

    RfnRemoteDisconnectSetOnDemandConfigurationCommand command( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm );

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
    const std::vector< RfnCommand::RfnResponsePayload > responses {
        { 0x8f, 0x00, 0x00, 0x02, 0x00 },
        { 0x83, 0x01, 0x00, 0x02, 0x00 },
        { 0x83, 0x00, 0x02, 0x02, 0x00 },
        { 0x83, 0x00, 0x00, 0x03, 0x00 },
        { 0x83, 0x00, 0x00, 0x02, 0x01, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x8f)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x01)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (2)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (0)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type received in response (0)" ) };

    std::vector< RfnCommand::CommandException > actual;

    RfnRemoteDisconnectSetThresholdConfigurationCommand command( RfnRemoteDisconnectConfigurationCommand::Reconnect_Arm,
                                                                 RfnRemoteDisconnectConfigurationCommand::DemandInterval_Five,
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
    const std::vector< RfnCommand::RfnResponsePayload > responses {
        { 0x8f, 0x00, 0x00, 0x03, 0x00 },
        { 0x83, 0x01, 0x00, 0x03, 0x00 },
        { 0x83, 0x00, 0x02, 0x03, 0x00 },
        { 0x83, 0x00, 0x00, 0x01, 0x00 },
        { 0x83, 0x00, 0x00, 0x03, 0x01, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x8f)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x01)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (2)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (0)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type received in response (0)" ) };

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
        const std::vector< unsigned char > exp {
            0x82, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x83, 0x01, 0x00, 0x01, 0x01, 0x01, 0x01, 0x01 };

        // These should be empty to begin
        BOOST_CHECK( ! command.getDisconnectMode() );
        BOOST_CHECK( ! command.getReconnectParam() );
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

        const boost::optional<RfnRemoteDisconnectConfigurationCommand::DisconnectMode> disconnectMode = command.getDisconnectMode();
        const boost::optional<RfnRemoteDisconnectConfigurationCommand::Reconnect>      reconnect      = command.getReconnectParam();

        BOOST_REQUIRE( !! disconnectMode );
        BOOST_REQUIRE( !! reconnect );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectConfigurationCommand::DisconnectMode_OnDemand, *disconnectMode );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectConfigurationCommand::Reconnect_Immediate,     *reconnect );

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
        const std::vector< unsigned char > response {
            0x83, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01 };

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_GetConfiguration_DemandThreshold )
{
    RfnRemoteDisconnectGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp {
            0x82, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x83, 0x01, 0x00, 0x02, 0x01, 0x02, 0x05, 0x01, 0x0f, 0x64, 0x0a, 0x00 };

        // These should be empty to begin
        BOOST_CHECK( ! command.getDisconnectMode() );
        BOOST_CHECK( ! command.getReconnectParam() );
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

        const boost::optional<RfnRemoteDisconnectConfigurationCommand::DisconnectMode> disconnectMode = command.getDisconnectMode();
        const boost::optional<RfnRemoteDisconnectConfigurationCommand::Reconnect>      reconnect      = command.getReconnectParam();
        const boost::optional<unsigned> demandInterval  = command.getDemandInterval();
        const boost::optional<unsigned> connectDelay    = command.getConnectDelay();
        const boost::optional<unsigned> maxDisconnects  = command.getMaxDisconnects();
        const boost::optional<double>   demandThreshold = command.getDemandThreshold();

        BOOST_REQUIRE( !! disconnectMode );
        BOOST_REQUIRE( !! reconnect );
        BOOST_REQUIRE( !! demandInterval );
        BOOST_REQUIRE( !! connectDelay );
        BOOST_REQUIRE( !! maxDisconnects );
        BOOST_REQUIRE( !! demandThreshold );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectConfigurationCommand::DisconnectMode_DemandThreshold, *disconnectMode );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectConfigurationCommand::Reconnect_Immediate,            *reconnect );
        BOOST_CHECK_EQUAL(   15, *demandInterval );
        BOOST_CHECK_EQUAL(   10, *connectDelay );
        BOOST_CHECK_EQUAL(    0, *maxDisconnects );
        BOOST_CHECK_EQUAL( 10.0, *demandThreshold );

        // Still shouldn't have any of these
        BOOST_CHECK( ! command.getConnectMinutes() );
        BOOST_CHECK( ! command.getDisconnectMinutes() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x83, 0x01, 0x01, 0x02, 0x01, 0x02, 0x05, 0x01, 0x0f, 0x64, 0x0a, 0x00 };

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_GetConfiguration_Cycling )
{
    RfnRemoteDisconnectGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp {
            0x82, 0x01, 0x00 };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response {
            0x83, 0x01, 0x00, 0x03, 0x01, 0x03, 0x05, 0x01, 0x03, 0xe8, 0x00, 0x3c };

        // These should be empty to begin
        BOOST_CHECK( ! command.getDisconnectMode() );
        BOOST_CHECK( ! command.getReconnectParam() );
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

        const boost::optional<RfnRemoteDisconnectConfigurationCommand::DisconnectMode> disconnectMode = command.getDisconnectMode();
        const boost::optional<RfnRemoteDisconnectConfigurationCommand::Reconnect>      reconnect      = command.getReconnectParam();
        const boost::optional<unsigned> disconnectMinutes = command.getDisconnectMinutes();
        const boost::optional<unsigned> connectMinutes    = command.getConnectMinutes();

        BOOST_REQUIRE( !! disconnectMode );
        BOOST_REQUIRE( !! reconnect );
        BOOST_REQUIRE( !! disconnectMinutes );
        BOOST_REQUIRE( !! connectMinutes );

        BOOST_CHECK_EQUAL( RfnRemoteDisconnectConfigurationCommand::DisconnectMode_Cycling, *disconnectMode );
        BOOST_CHECK_EQUAL( RfnRemoteDisconnectConfigurationCommand::Reconnect_Immediate, *reconnect );
        BOOST_CHECK_EQUAL( 1000, *disconnectMinutes );
        BOOST_CHECK_EQUAL( 60,   *connectMinutes );

        // Still shouldn't have any of these
        BOOST_CHECK( ! command.getConnectDelay() );
        BOOST_CHECK( ! command.getMaxDisconnects() );
        BOOST_CHECK( ! command.getDemandInterval() );
        BOOST_CHECK( ! command.getDemandThreshold() );
    }

    // decode -- failure response
    {
        const std::vector< unsigned char > response {
            0x83, 0x01, 0x01, 0x03, 0x01, 0x03, 0x05, 0x01, 0x03, 0xe8, 0x00, 0x3c };

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

BOOST_AUTO_TEST_CASE( test_cmd_rfn_RemoteDisconnect_GetConfiguration_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload > responses {
        //  invalid response length
        { 0x83, 0x01 },
        //         \/\/ invalid response command code
        { 0xcc, 0x01, 0x00 },
        //                 \/\/ invalid operation code
        { 0x83, 0x00, 0x00 },
        //  invalid status         \/\/
        { 0x83, 0x01, 0x17 },
        //  status: failure        \/\/
        { 0x83, 0x01, 0x01 },
        //  response too small
        { 0x83, 0x01, 0x00 },
        //  invalid TLV count                      \/\/
        { 0x83, 0x01, 0x00, 0x01, 0x02, 0x18, 0x00, 0x19, 0x00 },
        //  invalid TLV type                               \/\/
        { 0x83, 0x01, 0x00, 0x01, 0x01, 0x17, 0x01, 0x01 },
        //  response reconnect param invalid                               \/\/
        { 0x83, 0x01, 0x00, 0x01, 0x01, 0x01, 0x01, 0x17 },
        //  response TLV too small                                 \/\/
        { 0x83, 0x01, 0x00, 0x02, 0x01, 0x02, 0x04, 0x01, 0x0f, 0x64, 0x0a },
        //  response TLV too small                                 \/\/
        { 0x83, 0x01, 0x00, 0x03, 0x01, 0x03, 0x04, 0x01, 0x03, 0xe8, 0x00, 0x3c },
        //  reconnect param invalid                                        \/\/
        { 0x83, 0x01, 0x00, 0x03, 0x01, 0x03, 0x05, 0x02, 0x03, 0xe8, 0x00, 0x3c }
    };

    const std::vector< RfnCommand::CommandException >   expected {
        //  0
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (2)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0xcc)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Operation Code (0x00)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Status (23)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Failure (1)" ),
        //  5
        RfnCommand::CommandException( ClientErrors::InvalidData, "Response too small (3 < 5)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (2 != 1)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV type received in response (23)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Response reconnect param invalid (23) expecting 0 or 1" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Response TLV too small (4 != 5)" ),
        //  10
        RfnCommand::CommandException( ClientErrors::InvalidData, "Response TLV too small (4 != 5)" ),
        RfnCommand::CommandException( ClientErrors::InvalidData, "Response reconnect param invalid (2) expecting 0 or 1" ) };

    std::vector< RfnCommand::CommandException > actual;

    RfnRemoteDisconnectGetConfigurationCommand command;

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
