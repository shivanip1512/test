#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_MeterProgramming.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices::Commands;
using namespace std::chrono_literals;

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

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_MeterProgramming )

const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );

BOOST_AUTO_TEST_CASE( test_setConfigurationCommand )
{
    RfnMeterProgrammingSetConfigurationCommand command{ "7d444840-9dc0-11d1-b245-5ffdce74fad2", 11235 };

    BOOST_CHECK( command.isPost() );
    BOOST_CHECK( command.isOneWay() );

    // execute
    {
        std::vector<unsigned char> exp { 
            0x90, 
            0x02, 
            0x01, 
                0x00, 0x04, 
                    0x00, 0x00, 0x2b, 0xe3, 
            0x02, 
                0x00, 0x33 };
        const std::string uri = "/meterPrograms/7d444840-9dc0-11d1-b245-5ffdce74fad2";
        exp.insert(exp.end(), uri.begin(), uri.end());
        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response
        { 
            0x92,   // Configuration response
            0,      // Success
            0,      // Success
            1,      // 1 TLV
            3,      // TLV type 3, configuration ID
            37,     // payload length
            'R', '7', 'd', '4', '4', '4', '8', '4', '0', '-',       // payload - R7d444840-9dc0-11d1-b245-5ffdce74fad2
            '9', 'd', 'c', '0', '-',
            '1', '1', 'd', '1', '-',
            'b', '2', '4', '5', '-',
            '5', 'f', 'f', 'd', 'c', 'e', '7', '4', 'f', 'a', 'd', '2'
        };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
        BOOST_CHECK_EQUAL( rcv.description, 
                           "Meter Status: Configured (0)"
                           "\nDetailed Configuration Status: Success (0)"
                           "\nSource: Yukon programmed"
                           "\nMeter Configuration ID: R7d444840-9dc0-11d1-b245-5ffdce74fad2" );

        BOOST_CHECK_EQUAL( command.getStatusCode(), ClientErrors::None );
        BOOST_CHECK_EQUAL( command.getMeterConfigurationID(), "R7d444840-9dc0-11d1-b245-5ffdce74fad2" );
    }
}

BOOST_AUTO_TEST_CASE( test_getConfigurationCommand_success )
{
    RfnMeterProgrammingGetConfigurationCommand  command;

    // execute
    {
        const std::vector<unsigned char> exp
        {
            0x91    // Get Configuration
        };

        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response
        { 
            0x92,   // Configuration response
            0,      // Success
            0,      // Success
            1,      // 1 TLV
            3,      // TLV type 3, configuration ID
            37,     // payload length
            'R', '7', 'd', '4', '4', '4', '8', '4', '0', '-',       // payload - R7d444840-9dc0-11d1-b245-5ffdce74fad2
            '9', 'd', 'c', '0', '-',
            '1', '1', 'd', '1', '-',
            'b', '2', '4', '5', '-',
            '5', 'f', 'f', 'd', 'c', 'e', '7', '4', 'f', 'a', 'd', '2'
        };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
        BOOST_CHECK_EQUAL( rcv.description, 
                           "Meter Status: Configured (0)"
                           "\nDetailed Configuration Status: Success (0)"
                           "\nSource: Yukon programmed"
                           "\nMeter Configuration ID: R7d444840-9dc0-11d1-b245-5ffdce74fad2" );

        BOOST_CHECK_EQUAL( command.getStatusCode(), ClientErrors::None );
        BOOST_CHECK_EQUAL( command.getMeterConfigurationID(), "R7d444840-9dc0-11d1-b245-5ffdce74fad2" );
    }
}

BOOST_AUTO_TEST_CASE( test_getConfigurationCommand_error_decode_bricked )
{
    RfnMeterProgrammingGetConfigurationCommand  command;

    const std::vector< unsigned char > response
    { 
        0x92,   // Configuration response
        3,      // Error -- Bricked
        0,      // Success
        1,      // 1 TLV
        3,      // TLV type 3, configuration ID
        0,      // payload length
    };

    auto rcv = command.decodeCommand( execute_time, response );

    BOOST_CHECK_EQUAL( rcv.status, ClientErrors::MeterBricked );
    BOOST_CHECK_EQUAL( rcv.description, 
                       "Meter Status: Bricked (3)"
                       "\nDetailed Configuration Status: Success (0)" );

    BOOST_CHECK_EQUAL( command.getStatusCode(), ClientErrors::MeterBricked );
    BOOST_CHECK_EQUAL( command.getMeterConfigurationID(), "" );
}

BOOST_AUTO_TEST_CASE( test_getConfigurationCommand_error_decode_device_busy )
{
    RfnMeterProgrammingGetConfigurationCommand  command;

    const std::vector< unsigned char > response
    { 
        0x92,   // Configuration response
        1,      // Unchanged
        6,      // Device Busy
        1,      // 1 TLV
        3,      // TLV type 3, configuration ID
        0,      // payload length
    };

    auto rcv = command.decodeCommand( execute_time, response );

    BOOST_CHECK_EQUAL( rcv.status, ClientErrors::DeviceBusy );
    BOOST_CHECK_EQUAL( rcv.description, 
                       "Meter Status: Unchanged (1)"
                       "\nDetailed Configuration Status: Device Busy (6)" );

    BOOST_CHECK_EQUAL( command.getStatusCode(), ClientErrors::DeviceBusy );
    BOOST_CHECK_EQUAL( command.getMeterConfigurationID(), "" );
}

BOOST_AUTO_TEST_CASE( test_getConfigurationCommand_success_with_unmapped_prefix )
{
    RfnMeterProgrammingGetConfigurationCommand  command;

    const std::vector< unsigned char > response
    { 
        0x92,   // Configuration response
        0,      // Success
        0,      // Success
        1,      // 1 TLV
        3,      // TLV type 3, configuration ID
        37,     // payload length
        'Q', '7', 'd', '4', '4', '4', '8', '4', '0', '-',       // payload - Q7d444840-9dc0-11d1-b245-5ffdce74fad2
        '9', 'd', 'c', '0', '-',
        '1', '1', 'd', '1', '-',
        'b', '2', '4', '5', '-',
        '5', 'f', 'f', 'd', 'c', 'e', '7', '4', 'f', 'a', 'd', '2'
    };

    auto rcv = command.decodeCommand( execute_time, response );

    BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
    BOOST_CHECK_EQUAL( rcv.description, 
                       "Meter Status: Configured (0)"
                       "\nDetailed Configuration Status: Success (0)"
                       "\nSource: Unmapped Prefix 'Q'"
                       "\nMeter Configuration ID: Q7d444840-9dc0-11d1-b245-5ffdce74fad2" );

    BOOST_CHECK_EQUAL( command.getStatusCode(), ClientErrors::None );
    BOOST_CHECK_EQUAL( command.getMeterConfigurationID(), "Q7d444840-9dc0-11d1-b245-5ffdce74fad2" );
}

BOOST_AUTO_TEST_CASE( test_getConfigurationCommand_error_decode_exceptions )
{
    RfnMeterProgrammingGetConfigurationCommand  command;

    const std::vector< RfnCommand::Bytes > responses
    {
        // Code,    Status, Detail, TLV Count,  TLV Type,   Payload Length, Payload
        {  0x92,    0,      0,      1,          3   },
        {  0x92,    0,      0,      1,          3,          6,              'R', '7', 'd' },
        {  0x93,    0,      0,      1,          3,          0   },
        {  0x92,    0,      0,      2,          3,          0   },
        {  0x92,    0,      0,      1,          4,          0   }
    };

    const std::vector< RfnCommand::CommandException > expected 
    {  
        { ClientErrors::InvalidData, "Invalid Response Length (5) - minimum (6)" },
        { ClientErrors::InvalidData, "Invalid Response Length (9) - expected (12)" },
        { ClientErrors::InvalidData, "Invalid Response Command Code (0x93) - expected (0x92)" },
        { ClientErrors::InvalidData, "Invalid TLV Count (2) - expected (1)" },
        { ClientErrors::InvalidData, "Invalid TLV Type (4) - expected (3)" }
    };

    std::vector< RfnCommand::CommandException > actual;

    for ( auto response : responses )
    {
        BOOST_CHECK_THROW( command.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            auto rcv = command.decodeCommand( execute_time, response );
        } 
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_RANGES( expected, actual );
}

BOOST_AUTO_TEST_SUITE_END()