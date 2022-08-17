#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_DemandFreeze.h"
#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnDemandFreezeConfigurationCommand;
using Cti::Devices::Commands::RfnImmediateDemandFreezeCommand;
using Cti::Devices::Commands::RfnGetDemandFreezeInfoCommand;

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



BOOST_AUTO_TEST_SUITE( test_cmd_rfn_DemandFreeze )


const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_SetFreezeDay )
{
    RfnDemandFreezeConfigurationCommand command( 10 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x02 )( 0x0a );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )( 0x00 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0x00)"
                                            "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_SetFreezeDay_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = {
        { 0x57, 0x00, 0x00, 0x00, 0x00 },
        { 0x56,  0x00,  0x00,  0x00,  0x01 },
        { 0x56,  0x00,  0x00,  0x00 },
        { 0x56,  0x00,  0x00,  0x00,  0x00,  0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response Command Code (0x57)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (1)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (4)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (6)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnDemandFreezeConfigurationCommand command( 10 );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_SetFreezeDay_status_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = {
        { 0x56, 0x01, 0x00, 0x00, 0x00 },
        { 0x56, 0x02, 0x00, 0x00, 0x00 },
        { 0x56, 0x03, 0x00, 0x00, 0x00 },
        { 0x56, 0x04, 0x00, 0x00, 0x00 },
        { 0x56, 0x05, 0x00, 0x00, 0x00 },
        { 0x56, 0x06, 0x00, 0x00, 0x00 },
        { 0x56, 0x07, 0x00, 0x00, 0x00 },
        { 0x56, 0x08, 0x00, 0x00, 0x00 },
        { 0x56, 0x0f, 0x00, 0x01, 0x00 },
        { 0x56, 0x0f, 0x00, 0x02, 0x00 },
        { 0x56, 0x0f, 0x00, 0x03, 0x00 },
        { 0x56, 0x0f, 0x00, 0x04, 0x00 },
        { 0x56, 0x0f, 0x00, 0x05, 0x00 },
        { 0x56, 0x0f, 0x00, 0x06, 0x00 },
        { 0x56, 0x0f, 0x00, 0x07, 0x00 },
        { 0x56, 0x0f, 0x00, 0x08, 0x00 },
        { 0x56, 0x0f, 0x00, 0x09, 0x00 },
        { 0x56, 0x0f, 0x00, 0x0a, 0x00 },
        { 0x56, 0x0f, 0x01, 0x00, 0x00 },
        { 0x56, 0x0f, 0x01, 0x01, 0x00 },
        { 0x56, 0x0f, 0x01, 0x02, 0x00 },
        { 0x56, 0x0f, 0x01, 0x03, 0x00 },
        { 0x56, 0x0f, 0x02, 0x00, 0x00 },
        { 0x56, 0x0f, 0x03, 0x00, 0x00 },
        { 0x56, 0x0f, 0x04, 0x00, 0x00 },
        { 0x56, 0x00, 0x05, 0x00, 0x00 },
        { 0x56, 0x00, 0x00, 0x0f, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Not Ready (0x01)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Busy (0x02)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Protocol Error (0x03)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Meter Error (0x04)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Illegal Request (0x05)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Aborted Command (0x06)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Timeout (0x07)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x08)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE NOT SUPPORTED (ASC: 0x00, ASCQ: 0x01)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, INVALID FIELD IN COMMAND (ASC: 0x00, ASCQ: 0x02)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, INAPPROPRIATE ACTION REQUESTED (ASC: 0x00, ASCQ: 0x03)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, LOAD VOLTAGE HIGHER THAN THRESHOLD (ASC: 0x00, ASCQ: 0x04)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SWITCH IS OPEN (ASC: 0x00, ASCQ: 0x05)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, TEST MODE ENABLED (ASC: 0x00, ASCQ: 0x06)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT BUTTON PRESSED BUT METER NOT ARMED (ASC: 0x00, ASCQ: 0x07)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT NOT ENABLED (ASC: 0x00, ASCQ: 0x08)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT IS CURRENTLY CHARGING (ASC: 0x00, ASCQ: 0x09)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT IN OPERATION (ASC: 0x00, ASCQ: 0x0a)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, INSUFFICIENT SECURITY CLEARANCE (ASC: 0x01, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, DATA LOCKED (ASC: 0x01, ASCQ: 0x01)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, INVALID SERVICE SEQUENCE STATE (ASC: 0x01, ASCQ: 0x02)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, RENEGOTIATE REQUEST (ASC: 0x01, ASCQ: 0x03)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: DATA NOT READY (ASC: 0x02, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: DEVICE BUSY (ASC: 0x03, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Status: Reserved (0x0f)\nAdditional Status: SCHEDULED FOR NEXT RECORD INTERVAL (ASC: 0x04, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Additional Status (ASC: 0x05, ASCQ: 0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Additional Status (ASC: 0x00, ASCQ: 0x0f)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnDemandFreezeConfigurationCommand command( 10 );

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


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_ImmediateFreeze )
{
    RfnImmediateDemandFreezeCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )( 0x00 )( 0x04 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0x00)"
                                            "\nAdditional Status: SCHEDULED FOR NEXT RECORD INTERVAL (ASC: 0x04, ASCQ: 0x00)" );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_GetFreezeInfo_full )
{
    Cti::Test::set_to_central_timezone();

    RfnGetDemandFreezeInfoCommand   command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x03 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )
            ( 0x00 )( 0x00 )( 0x00 )
            ( 0x0e )
            ( 0x01 )( 0x01 )( 0x01 )
            ( 0x02 )( 0x04 )( 0x02 )( 0x03 )( 0x04 )( 0x05 )
            ( 0x03 )( 0x04 )( 0x06 )( 0x07 )( 0x08 )( 0x09 )
            ( 0x04 )( 0x04 )( 0x0a )( 0x0b )( 0x0c )( 0x0d )
            ( 0x05 )( 0x04 )( 0x0e )( 0x0f )( 0x10 )( 0x11 )
            ( 0x06 )( 0x04 )( 0x12 )( 0x13 )( 0x14 )( 0x15 )
            ( 0x07 )( 0x04 )( 0x16 )( 0x17 )( 0x18 )( 0x19 )
            ( 0x08 )( 0x04 )( 0x1a )( 0x1b )( 0x1c )( 0x1d )
            ( 0x09 )( 0x04 )( 0x1e )( 0x1f )( 0x20 )( 0x21 )
            ( 0x0a )( 0x04 )( 0x22 )( 0x23 )( 0x24 )( 0x25 )
            ( 0x0b )( 0x04 )( 0x26 )( 0x27 )( 0x28 )( 0x29 )
            ( 0x0c )( 0x04 )( 0x2a )( 0x2b )( 0x2c )( 0x2d )
            ( 0x0d )( 0x04 )( 0x2e )( 0x2f )( 0x30 )( 0x31 )
            ( 0x0e )( 0x04 )( 0x32 )( 0x33 )( 0x34 )( 0x35 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0x00)"
                                            "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)"
                                            "\nDay of freeze   : 1"
                                            "\nLast freeze time: 01/26/1971 09:34:29"
                                            "\nBase rate Peak Delivered Demand  101124105 @ 05/04/1975 23:29:01"
                                            "\nRate A Peak Delivered Demand  235868177 @ 08/11/1979 12:23:33"
                                            "\nRate B Peak Delivered Demand  370612249 @ 11/18/1983 00:18:05"
                                            "\nRate C Peak Delivered Demand  505356321 @ 02/24/1988 13:12:37"
                                            "\nRate D Peak Delivered Demand  640100393 @ 06/02/1992 03:07:09"
                                            "\nRate E Peak Delivered Demand  774844465 @ 09/08/1996 16:01:41" );
    }

    typedef RfnGetDemandFreezeInfoCommand::DemandFreezeData Dfd;

    Dfd results = command.getDemandFreezeData();

    BOOST_CHECK_EQUAL(        0x01, *results.dayOfFreeze );
    BOOST_CHECK_EQUAL(  0x02030405, *results.lastFreezeTime );
    BOOST_CHECK_EQUAL(  0x06070809, *results.peakValues[ Dfd::DemandRates_Base   ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x0a0b0c0d, *results.peakValues[ Dfd::DemandRates_Base   ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x0e0f1011, *results.peakValues[ Dfd::DemandRates_Rate_A ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x12131415, *results.peakValues[ Dfd::DemandRates_Rate_A ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x16171819, *results.peakValues[ Dfd::DemandRates_Rate_B ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x1a1b1c1d, *results.peakValues[ Dfd::DemandRates_Rate_B ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x1e1f2021, *results.peakValues[ Dfd::DemandRates_Rate_C ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x22232425, *results.peakValues[ Dfd::DemandRates_Rate_C ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x26272829, *results.peakValues[ Dfd::DemandRates_Rate_D ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x2a2b2c2d, *results.peakValues[ Dfd::DemandRates_Rate_D ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x2e2f3031, *results.peakValues[ Dfd::DemandRates_Rate_E ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x32333435, *results.peakValues[ Dfd::DemandRates_Rate_E ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_GetFreezeInfo_supplied_case_1 )
{
    Cti::Test::set_to_central_timezone();

    RfnGetDemandFreezeInfoCommand   command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x03 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )
            ( 0x00 )( 0x00 )( 0x00 )
            ( 0x0c )
            ( 0x01 )( 0x01 )( 0x1c )
            ( 0x02 )( 0x04 )( 0x51 )( 0x54 )( 0x64 )( 0x51 )
            ( 0x03 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x9c )
            ( 0x04 )( 0x04 )( 0x51 )( 0x54 )( 0x5e )( 0x74 )
            ( 0x05 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x06 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x07 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x08 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x09 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x9c )
            ( 0x0a )( 0x04 )( 0x51 )( 0x54 )( 0x5e )( 0x74 )
            ( 0x0b )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x0c )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0x00)"
                                            "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)"
                                            "\nDay of freeze   : 28"
                                            "\nLast freeze time: 03/28/2013 10:40:01"
                                            "\nBase rate Peak Delivered Demand  156 @ 03/28/2013 10:15:00"
                                            "\nRate A Peak Delivered Demand  0 @ not-a-time"
                                            "\nRate B Peak Delivered Demand  0 @ not-a-time"
                                            "\nRate C Peak Delivered Demand  156 @ 03/28/2013 10:15:00"
                                            "\nRate D Peak Delivered Demand  0 @ not-a-time" );
    }

    typedef RfnGetDemandFreezeInfoCommand::DemandFreezeData Dfd;

    Dfd results = command.getDemandFreezeData();

    BOOST_CHECK_EQUAL(        0x1c, *results.dayOfFreeze );
    BOOST_CHECK_EQUAL(  0x51546451, *results.lastFreezeTime );
    BOOST_CHECK_EQUAL(  0x0000009c, *results.peakValues[ Dfd::DemandRates_Base   ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x51545e74, *results.peakValues[ Dfd::DemandRates_Base   ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_A ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_A ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_B ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_B ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x0000009c, *results.peakValues[ Dfd::DemandRates_Rate_C ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x51545e74, *results.peakValues[ Dfd::DemandRates_Rate_C ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_D ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_D ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_GetFreezeInfo_supplied_case_2 )
{
    Cti::Test::set_to_central_timezone();

    RfnGetDemandFreezeInfoCommand   command;
    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x03 );

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )
            ( 0x00 )( 0x00 )( 0x00 )
            ( 0x0c )
            ( 0x01 )( 0x01 )( 0x1c )
            ( 0x02 )( 0x04 )( 0x51 )( 0x54 )( 0x4e )( 0x0c )
            ( 0x03 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x48 )
            ( 0x04 )( 0x04 )( 0x51 )( 0x54 )( 0x4b )( 0xb4 )
            ( 0x05 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x06 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x07 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x08 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )
            ( 0x09 )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x48 )
            ( 0x0a )( 0x04 )( 0x51 )( 0x54 )( 0x4b )( 0xb4 )
            ( 0x0b )( 0x04 )( 0x00 )( 0x00 )( 0x00 )( 0x48 )
            ( 0x0c )( 0x04 )( 0x51 )( 0x53 )( 0xbf )( 0x14 );

        RfnCommandResult rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Status: Success (0x00)"
                                            "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)"
                                            "\nDay of freeze   : 28"
                                            "\nLast freeze time: 03/28/2013 09:05:00"
                                            "\nBase rate Peak Delivered Demand  72 @ 03/28/2013 08:55:00"
                                            "\nRate A Peak Delivered Demand  0 @ not-a-time"
                                            "\nRate B Peak Delivered Demand  0 @ not-a-time"
                                            "\nRate C Peak Delivered Demand  72 @ 03/28/2013 08:55:00"
                                            "\nRate D Peak Delivered Demand  72 @ 03/27/2013 22:55:00"
);
    }

    typedef RfnGetDemandFreezeInfoCommand::DemandFreezeData Dfd;

    Dfd results = command.getDemandFreezeData();

    BOOST_CHECK_EQUAL(        0x1c, *results.dayOfFreeze );
    BOOST_CHECK_EQUAL(  0x51544e0c, *results.lastFreezeTime );
    BOOST_CHECK_EQUAL(  0x00000048, *results.peakValues[ Dfd::DemandRates_Base   ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x51544bb4, *results.peakValues[ Dfd::DemandRates_Base   ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_A ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_A ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_B ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x00000000, *results.peakValues[ Dfd::DemandRates_Rate_B ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000048, *results.peakValues[ Dfd::DemandRates_Rate_C ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x51544bb4, *results.peakValues[ Dfd::DemandRates_Rate_C ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
    BOOST_CHECK_EQUAL(  0x00000048, *results.peakValues[ Dfd::DemandRates_Rate_D ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].value );
    BOOST_CHECK_EQUAL(  0x5153bf14, *results.peakValues[ Dfd::DemandRates_Rate_D ][ Dfd::Metric_FrozenPeak_Demand_Delivered ].timestamp );
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_GetFreezeInfo_decode_exceptions )
{
    const std::vector< RfnCommand::RfnResponsePayload >   responses = {
        { 0x56,
          0x00, 0x00, 0x00,
          0x02,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04, 0x00, 0x00, 0x00, 0x48 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x04,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04, 0x00, 0x00, 0x00, 0x48 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x04,
          0x01, 0x01, 0x1c,
          0x12, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04, 0x00, 0x00, 0x00, 0x48 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x03,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e,/* ( 0x0c, */
          0x03, 0x04, 0x00, 0x00, 0x00, 0x48 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x03,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x03,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x03,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04, 0x00 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x03,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04, 0x00, 0x00 },
        { 0x56,
          0x00, 0x00, 0x00,
          0x03,
          0x01, 0x01, 0x1c,
          0x02, 0x04, 0x51, 0x54, 0x4e, 0x0c,
          0x03, 0x04, 0x00, 0x00, 0x00 }
    };

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (3) expected 2" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (3) expected 4" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Missing decode for TLV type (0x12)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Missing decode for TLV type (0x00)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV count (2) expected 3" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV length (0) expected 4" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV length (1) expected 4" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV length (2) expected 4" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid TLV length (3) expected 4" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfnGetDemandFreezeInfoCommand   command;

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

