#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_Metrology.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices::Commands;
using MetrologyState = Cti::Devices::Commands::RfnMetrologyCommand::MetrologyState;
using namespace std::chrono_literals;

// --- defined in RTDB\test_main.cpp -- so BOOST_CHECK_EQUAL_COLLECTIONS() works for RfnCommand::CommandException
namespace boost         {
namespace test_tools    {
    bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs );
}
}

namespace std   {
    ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );
    ostream & operator<<( ostream & os, const MetrologyState state ) {
        return os << (
            state == MetrologyState::Enable
                ? "Enable"
                : "Disable");
    }
}
// ---


BOOST_AUTO_TEST_SUITE( test_cmd_rfn_Metrology )

const CtiTime execute_time( CtiDate( 12, 8, 2020 ) , 17 );

struct Response
{
    Cti::Test::byte_str payload;
    YukonError_t        status;
    std::string         description;
};

BOOST_AUTO_TEST_CASE( supported_devices )
{
    const std::map<DeviceTypes, bool> testCases
    {
        //  RFN Focus
        { TYPE_RFN410FL,     false  },
        { TYPE_RFN410FX,     false  },
        { TYPE_RFN410FD,     false  },
        { TYPE_RFN420FL,     false  },
        { TYPE_RFN420FX,     false  },
        { TYPE_RFN420FD,     false  },
        { TYPE_RFN420FRX,    false  },
        { TYPE_RFN420FRD,    false  },
        { TYPE_RFN510FL,     false  },
        //  RFN-500 Focus AX (gen 1)
        { TYPE_RFN520FAX,    true  },
        { TYPE_RFN520FRX,    true  },
        { TYPE_RFN520FAXD,   true  },
        { TYPE_RFN520FRXD,   true  },
        { TYPE_RFN530FAX,    true  },
        { TYPE_RFN530FRX,    true  },
        //  RFN-500 Focus AXe (gen 2)
        { TYPE_RFN520FAXE,   true  },
        { TYPE_RFN520FRXE,   true  },
        { TYPE_RFN520FAXED,  true  },
        { TYPE_RFN520FRXED,  true  },
        { TYPE_RFN530FAXE,   true  },
        { TYPE_RFN530FRXE,   true  },
        //  RFN Centron
        { TYPE_RFN410CL,     false },
        { TYPE_RFN420CL,     false },
        { TYPE_WRL420CL,     false },
        { TYPE_RFN420CD,     false },
        { TYPE_WRL420CD,     false },
        //  RFN A3
        { TYPE_RFN430A3D,    false },
        { TYPE_RFN430A3T,    false },
        { TYPE_RFN430A3K,    false },
        { TYPE_RFN430A3R,    false },
        //  RFN KV
        { TYPE_RFN430KV,     false },
        //  RFN Sentinel
        { TYPE_RFN430SL0,    false },
        { TYPE_RFN430SL1,    false },
        { TYPE_RFN430SL2,    false },
        { TYPE_RFN430SL3,    false },
        { TYPE_RFN430SL4,    false },
        //   RFN Focus S4
        { TYPE_RFN530S4X,    false },
        { TYPE_RFN530S4EAX,  false },
        { TYPE_RFN530S4EAXR, false },
        { TYPE_RFN530S4ERX,  false },
        { TYPE_RFN530S4ERXR, false }
    };

    for ( const auto [deviceType, isSupported] : testCases )
    {
        BOOST_CHECK_EQUAL( RfnMetrologyCommand::isSupportedByDeviceType( deviceType ), isSupported );
    }
}

BOOST_AUTO_TEST_CASE( SetConfiguration_Disable_request )
{
    RfnMetrologySetConfigurationCommand command( MetrologyState::Disable );

    BOOST_CHECK_EQUAL( command.getCommandName(), "METLIB Disable Request" );

    {
        std::vector<unsigned char> exp
        {
            0x57,   // request code
            0x00,   // operation
            0x01    // request type { 0 -- Enable, 1 -- Disable }
        };

        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response
    {
        const std::vector<unsigned char> response
        { 
            0x58,   // response code
            0x00,   // operation
            0x00,   // status
            0x01    // value -- this should match the request type in the outgoing message
        };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
        BOOST_CHECK_EQUAL( rcv.description, 
                           "Status: Successful (0)"
                           "\nValue: Disable (1)" );
    }

    // decode -- non-exceptional non-zero status returns
    {
        const std::vector<Response> responses
        {
            {   "58 00 01 01",  ClientErrors::Abnormal, "Status: No Change in Key Value (1)"
                                                        "\nValue: Disable (1)"                  },
            {   "58 00 05 01",  ClientErrors::Abnormal, "Status: Illegal Request (5)"
                                                        "\nValue: Disable (1)"                  },
            {   "58 00 06 01",  ClientErrors::Abnormal, "Status: Aborted (6)"
                                                        "\nValue: Disable (1)"                  },
            {   "58 00 08 01",  ClientErrors::Abnormal, "Status: Configuration not present (8)"
                                                        "\nValue: Disable (1)"                  }
        };

        for ( const auto & response : responses )
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response.payload.bytes );

            BOOST_CHECK_EQUAL( rcv.status,      response.status );
            BOOST_CHECK_EQUAL( rcv.description, response.description );
        }
    }

    // decode -- exceptional failures
    {
        const std::vector<Response> responses
        {
            { "59 00 00 01", ClientErrors::InvalidData, "Invalid Response Command Code (0x59) - expected (0x58)"   },
            { "58 01 00 01", ClientErrors::InvalidData, "Invalid Response Operation Code (0x01) - expected (0x00)" },
            { "58 02 00 01", ClientErrors::InvalidData, "Invalid Response Operation Code (0x02) - expected (0x00)" },
            { "58 00 02 01", ClientErrors::InvalidData, "Invalid Response Status Code (2)"                         },
            { "58 00 00 00", ClientErrors::InvalidData, "Invalid Response Value (0) - request type mismatch"       },
            { "58 00 00 04", ClientErrors::InvalidData, "Invalid Response Value (4)"                               }
        };

        for ( const auto & response : responses )
        {
            BOOST_CHECK_THROW( command.decodeCommand( execute_time, response.payload.bytes ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = command.decodeCommand( execute_time, response.payload.bytes );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                BOOST_CHECK_EQUAL( ex.error_code, response.status );
                BOOST_CHECK_EQUAL( ex.what(),     response.description );
            }
        }
    }
}

BOOST_AUTO_TEST_CASE( SetConfiguration_Enable_request )
{
    RfnMetrologySetConfigurationCommand command( MetrologyState::Enable );

    BOOST_CHECK_EQUAL( command.getCommandName(), "METLIB Enable Request" );

    {
        std::vector<unsigned char> exp
        {
            0x57,   // request code
            0x00,   // operation
            0x00    // request type { 0 -- Enable, 1 -- Disable }
        };

        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response
    {
        const std::vector<unsigned char> response
        { 
            0x58,   // response code
            0x00,   // operation
            0x00,   // status
            0x00    // value -- this should match the request type in the outgoing message
        };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
        BOOST_CHECK_EQUAL( rcv.description, 
                           "Status: Successful (0)"
                           "\nValue: Enable (0)" );
    }

    // decode -- non-exceptional non-zero status returns
    {
        const std::vector<Response> responses
        {
            {   "58 00 01 00",  ClientErrors::Abnormal, "Status: No Change in Key Value (1)"
                                                        "\nValue: Enable (0)"                   },
            {   "58 00 05 00",  ClientErrors::Abnormal, "Status: Illegal Request (5)"
                                                        "\nValue: Enable (0)"                   },
            {   "58 00 06 00",  ClientErrors::Abnormal, "Status: Aborted (6)"
                                                        "\nValue: Enable (0)"                   },
            {   "58 00 08 00",  ClientErrors::Abnormal, "Status: Configuration not present (8)"
                                                        "\nValue: Enable (0)"                   }
        };

        for ( const auto & response : responses )
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response.payload.bytes );

            BOOST_CHECK_EQUAL( rcv.status,      response.status );
            BOOST_CHECK_EQUAL( rcv.description, response.description );
        }
    }

    // decode -- exceptional failures
    {
        const std::vector<Response> responses
        {
            { "59 00 00 00", ClientErrors::InvalidData, "Invalid Response Command Code (0x59) - expected (0x58)"   },
            { "58 01 00 00", ClientErrors::InvalidData, "Invalid Response Operation Code (0x01) - expected (0x00)" },
            { "58 02 00 00", ClientErrors::InvalidData, "Invalid Response Operation Code (0x02) - expected (0x00)" },
            { "58 00 02 00", ClientErrors::InvalidData, "Invalid Response Status Code (2)"                         },
            { "58 00 00 01", ClientErrors::InvalidData, "Invalid Response Value (1) - request type mismatch"       },
            { "58 00 00 04", ClientErrors::InvalidData, "Invalid Response Value (4)"                               }
        };

        for ( const auto & response : responses )
        {
            BOOST_CHECK_THROW( command.decodeCommand( execute_time, response.payload.bytes ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = command.decodeCommand( execute_time, response.payload.bytes );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                BOOST_CHECK_EQUAL( ex.error_code, response.status );
                BOOST_CHECK_EQUAL( ex.what(),     response.description );
            }
        }
    }
}

BOOST_AUTO_TEST_CASE( GetConfiguration_State_request )
{
    RfnMetrologyGetConfigurationCommand command;

    BOOST_CHECK_EQUAL( command.getCommandName(), "METLIB Get Enable/Disable State Request" );

    {
        std::vector<unsigned char> exp
        {
            0x57,
            0x01
        };

        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response -- enabled
    {
        const std::vector< unsigned char > response
        { 
            0x58,   // response code
            0x01,   // operation
            0x00,   // status
            0x00    // value { 0 -- Enable, 1 -- Disable }
        };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
        BOOST_CHECK_EQUAL( rcv.description, 
                           "Status: Successful (0)"
                           "\nValue: Enable (0)" );

        BOOST_CHECK_EQUAL( *command.getMetrologyState(), MetrologyState::Enable );       
    }

    // decode -- success response -- disabled
    {
        const std::vector< unsigned char > response
        { 
            0x58,   // response code
            0x01,   // operation
            0x00,   // status
            0x01    // value { 0 -- Enable, 1 -- Disable }
        };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.status, ClientErrors::None );
        BOOST_CHECK_EQUAL( rcv.description, 
                           "Status: Successful (0)"
                           "\nValue: Disable (1)" );

        BOOST_CHECK_EQUAL( *command.getMetrologyState(), MetrologyState::Disable );
    }

    // decode -- non-exceptional non-zero status returns
    {
        const std::vector<Response> responses
        {
            {   "58 01 01 00",  ClientErrors::Abnormal, "Status: No Change in Key Value (1)"
                                                        "\nValue: Enable (0)"                   },
            {   "58 01 05 00",  ClientErrors::Abnormal, "Status: Illegal Request (5)"
                                                        "\nValue: Enable (0)"                   },
            {   "58 01 06 00",  ClientErrors::Abnormal, "Status: Aborted (6)"
                                                        "\nValue: Enable (0)"                   },
            {   "58 01 08 00",  ClientErrors::Abnormal, "Status: Configuration not present (8)"
                                                        "\nValue: Enable (0)"                   }
        };

        for ( const auto & response : responses )
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, response.payload.bytes );

            BOOST_CHECK_EQUAL( rcv.status,      response.status );
            BOOST_CHECK_EQUAL( rcv.description, response.description );
        }
    }

    // decode -- exceptional failures
    {
        const std::vector<Response> responses
        {
            { "59 01 00 00", ClientErrors::InvalidData, "Invalid Response Command Code (0x59) - expected (0x58)"   },
            { "58 00 00 00", ClientErrors::InvalidData, "Invalid Response Operation Code (0x00) - expected (0x01)" },
            { "58 02 00 00", ClientErrors::InvalidData, "Invalid Response Operation Code (0x02) - expected (0x01)" },
            { "58 01 02 00", ClientErrors::InvalidData, "Invalid Response Status Code (2)"                         },
            { "58 01 00 04", ClientErrors::InvalidData, "Invalid Response Value (4)"                               }
        };

        for ( const auto & response : responses )
        {
            BOOST_CHECK_THROW( command.decodeCommand( execute_time, response.payload.bytes ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = command.decodeCommand( execute_time, response.payload.bytes );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                BOOST_CHECK_EQUAL( ex.error_code, response.status );
                BOOST_CHECK_EQUAL( ex.what(),     response.description );
            }
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
