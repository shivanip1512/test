#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_Metrology.h"
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

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_Metrology )

const CtiTime execute_time( CtiDate( 12, 8, 2020 ) , 17 );

BOOST_AUTO_TEST_CASE( test_cmd_rfn_Metrology__supported_devices )
{
    const std::map<DeviceTypes, bool> testCases
    {
        //  RFN Focus
        { TYPE_RFN410FL,     true  },
        { TYPE_RFN410FX,     true  },
        { TYPE_RFN410FD,     true  },
        { TYPE_RFN420FL,     true  },
        { TYPE_RFN420FX,     true  },
        { TYPE_RFN420FD,     true  },
        { TYPE_RFN420FRX,    true  },
        { TYPE_RFN420FRD,    true  },
        { TYPE_RFN510FL,     true  },
        { TYPE_RFN520FAX,    true  },
        { TYPE_RFN520FRX,    true  },
        { TYPE_RFN520FAXD,   true  },
        { TYPE_RFN520FRXD,   true  },
        { TYPE_RFN530FAX,    true  },
        { TYPE_RFN530FRX,    true  },
        //  RFN Centron
        { TYPE_RFN410CL,     true  },
        { TYPE_RFN420CL,     true  },
        { TYPE_WRL420CL,     true  },
        { TYPE_RFN420CD,     true  },
        { TYPE_WRL420CD,     true  },
        //  RFN A3
        { TYPE_RFN430A3D,    true  },
        { TYPE_RFN430A3T,    true  },
        { TYPE_RFN430A3K,    true  },
        { TYPE_RFN430A3R,    true  },
        //  RFN KV
        { TYPE_RFN430KV,     true  },
        //  RFN Sentinel
        { TYPE_RFN430SL0,    true  },
        { TYPE_RFN430SL1,    true  },
        { TYPE_RFN430SL2,    true  },
        { TYPE_RFN430SL3,    true  },
        { TYPE_RFN430SL4,    true  },
        //   RFN Focus S4
        { TYPE_RFN530S4X,    true  },
        { TYPE_RFN530S4EAX,  true  },
        { TYPE_RFN530S4EAXR, true  },
        { TYPE_RFN530S4ERX,  true  },
        { TYPE_RFN530S4ERXR, true  }
    };

    for ( const auto & [deviceType, isSupported] : testCases )
    {
        BOOST_CHECK_EQUAL( RfnMetrologyCommand::isSupportedByDeviceType( deviceType ), isSupported );
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_Metrology__SetConfiguration_Disable_request )
{
    RfnMetrologySetConfigurationCommand command( RfnMetrologyCommand::Disable );

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
        const std::vector<std::vector<unsigned char>> responses
        {
            {   0x58,   0x00,   0x01,   0x01 },
            {   0x58,   0x00,   0x05,   0x01 },
            {   0x58,   0x00,   0x06,   0x01 },
            {   0x58,   0x00,   0x08,   0x01 }
        };

        const std::vector<std::pair<YukonError_t, std::string>> results
        {
            { ClientErrors::Abnormal, "Status: No Change in Key Value (1)"
                                      "\nValue: Disable (1)"                    },
            { ClientErrors::Abnormal, "Status: Illegal Request (5)"
                                      "\nValue: Disable (1)"                    },
            { ClientErrors::Abnormal, "Status: Aborted (6)"
                                      "\nValue: Disable (1)"                    },
            { ClientErrors::Abnormal, "Status: Configuration not present (8)"
                                      "\nValue: Disable (1)"                    }
        };

        BOOST_REQUIRE_EQUAL( responses.size(), results.size() );

        for ( int i = 0; i < responses.size(); ++i )
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, responses[i] );

            BOOST_CHECK_EQUAL( rcv.status, results[i].first );
            BOOST_CHECK_EQUAL( rcv.description,  results[i].second  );
        }
    }

    // decode -- exceptional failures
    {
        const std::vector<std::vector<unsigned char>> responses
        {
            {   0x59,   0x00,   0x00,   0x01 },
            {   0x58,   0x01,   0x00,   0x01 },
            {   0x58,   0x02,   0x00,   0x01 },
            {   0x58,   0x00,   0x02,   0x01 },
            {   0x58,   0x00,   0x00,   0x00 },
            {   0x58,   0x00,   0x00,   0x04 }
        };

        const std::vector<std::pair<YukonError_t, std::string>> results
        {
            { ClientErrors::InvalidData, "Invalid Response Command Code (0x59) - expected (0x58)"   },
            { ClientErrors::InvalidData, "Invalid Response Operation Code (0x01) - expected (0x00)" },
            { ClientErrors::InvalidData, "Invalid Response Operation Code (0x02) - expected (0x00)" },
            { ClientErrors::InvalidData, "Invalid Response Status Code (2)"                         },
            { ClientErrors::InvalidData, "Invalid Response Value (0) - request type mismatch"       },
            { ClientErrors::InvalidData, "Invalid Response Value (4)"                               }
        };

        BOOST_REQUIRE_EQUAL( responses.size(), results.size() );

        for ( int i = 0; i < responses.size(); ++i )
        {
            BOOST_CHECK_THROW( command.decodeCommand( execute_time, responses[i] ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = command.decodeCommand( execute_time, responses[i] );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                BOOST_CHECK_EQUAL( ex.error_code, results[i].first );
                BOOST_CHECK_EQUAL( ex.what(),     results[i].second );
            }
        }
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_Metrology__SetConfiguration_Enable_request )
{
    RfnMetrologySetConfigurationCommand command( RfnMetrologyCommand::Enable );

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
        const std::vector<std::vector<unsigned char>> responses
        {
            {   0x58,   0x00,   0x01,   0x00 },
            {   0x58,   0x00,   0x05,   0x00 },
            {   0x58,   0x00,   0x06,   0x00 },
            {   0x58,   0x00,   0x08,   0x00 }
        };

        const std::vector<std::pair<YukonError_t, std::string>> results
        {
            { ClientErrors::Abnormal, "Status: No Change in Key Value (1)"
                                      "\nValue: Enable (0)"                     },
            { ClientErrors::Abnormal, "Status: Illegal Request (5)"
                                      "\nValue: Enable (0)"                     },
            { ClientErrors::Abnormal, "Status: Aborted (6)"
                                      "\nValue: Enable (0)"                     },
            { ClientErrors::Abnormal, "Status: Configuration not present (8)"
                                      "\nValue: Enable (0)"                     }
        };

        BOOST_REQUIRE_EQUAL( responses.size(), results.size() );

        for ( int i = 0; i < responses.size(); ++i )
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, responses[i] );

            BOOST_CHECK_EQUAL( rcv.status, results[i].first );
            BOOST_CHECK_EQUAL( rcv.description,  results[i].second  );
        }
    }

    // decode -- exceptional failures
    {
        const std::vector<std::vector<unsigned char>> responses
        {
            {   0x59,   0x00,   0x00,   0x00 },
            {   0x58,   0x01,   0x00,   0x00 },
            {   0x58,   0x02,   0x00,   0x00 },
            {   0x58,   0x00,   0x02,   0x00 },
            {   0x58,   0x00,   0x00,   0x01 },
            {   0x58,   0x00,   0x00,   0x04 }
        };

        const std::vector<std::pair<YukonError_t, std::string>> results
        {
            { ClientErrors::InvalidData, "Invalid Response Command Code (0x59) - expected (0x58)"   },
            { ClientErrors::InvalidData, "Invalid Response Operation Code (0x01) - expected (0x00)" },
            { ClientErrors::InvalidData, "Invalid Response Operation Code (0x02) - expected (0x00)" },
            { ClientErrors::InvalidData, "Invalid Response Status Code (2)"                         },
            { ClientErrors::InvalidData, "Invalid Response Value (1) - request type mismatch"       },
            { ClientErrors::InvalidData, "Invalid Response Value (4)"                               }
        };

        BOOST_REQUIRE_EQUAL( responses.size(), results.size() );

        for ( int i = 0; i < responses.size(); ++i )
        {
            BOOST_CHECK_THROW( command.decodeCommand( execute_time, responses[i] ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = command.decodeCommand( execute_time, responses[i] );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                BOOST_CHECK_EQUAL( ex.error_code, results[i].first );
                BOOST_CHECK_EQUAL( ex.what(),     results[i].second );
            }
        }
    }
}

BOOST_AUTO_TEST_CASE( test_test_cmd_rfn_Metrology__GetConfiguration_State_request )
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

        BOOST_CHECK_EQUAL( *command.getMetrologyState(), RfnMetrologyCommand::Enable );       
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

        BOOST_CHECK_EQUAL( *command.getMetrologyState(), RfnMetrologyCommand::Disable );
    }

    // decode -- non-exceptional non-zero status returns
    {
        const std::vector<std::vector<unsigned char>> responses
        {
            {   0x58,   0x01,   0x01,   0x00 },
            {   0x58,   0x01,   0x05,   0x00 },
            {   0x58,   0x01,   0x06,   0x00 },
            {   0x58,   0x01,   0x08,   0x00 }
        };

        const std::vector<std::pair<YukonError_t, std::string>> results
        {
            { ClientErrors::Abnormal, "Status: No Change in Key Value (1)"
                                      "\nValue: Enable (0)"                     },
            { ClientErrors::Abnormal, "Status: Illegal Request (5)"
                                      "\nValue: Enable (0)"                     },
            { ClientErrors::Abnormal, "Status: Aborted (6)"
                                      "\nValue: Enable (0)"                     },
            { ClientErrors::Abnormal, "Status: Configuration not present (8)"
                                      "\nValue: Enable (0)"                     }
        };

        BOOST_REQUIRE_EQUAL( responses.size(), results.size() );

        for ( int i = 0; i < responses.size(); ++i )
        {
            RfnCommandResult rcv = command.decodeCommand( execute_time, responses[i] );

            BOOST_CHECK_EQUAL( rcv.status, results[i].first );
            BOOST_CHECK_EQUAL( rcv.description,  results[i].second  );
        }
    }

    // decode -- exceptional failures
    {
        const std::vector<std::vector<unsigned char>> responses
        {
            {   0x59,   0x01,   0x00,   0x00 },
            {   0x58,   0x00,   0x00,   0x00 },
            {   0x58,   0x02,   0x00,   0x00 },
            {   0x58,   0x01,   0x02,   0x00 },
            {   0x58,   0x01,   0x00,   0x04 }
        };

        const std::vector<std::pair<YukonError_t, std::string>> results
        {
            { ClientErrors::InvalidData, "Invalid Response Command Code (0x59) - expected (0x58)"   },
            { ClientErrors::InvalidData, "Invalid Response Operation Code (0x00) - expected (0x01)" },
            { ClientErrors::InvalidData, "Invalid Response Operation Code (0x02) - expected (0x01)" },
            { ClientErrors::InvalidData, "Invalid Response Status Code (2)"                         },
            { ClientErrors::InvalidData, "Invalid Response Value (4)"                               }
        };

        BOOST_REQUIRE_EQUAL( responses.size(), results.size() );

        for ( int i = 0; i < responses.size(); ++i )
        {
            BOOST_CHECK_THROW( command.decodeCommand( execute_time, responses[i] ), RfnCommand::CommandException );

            try
            {
                RfnCommandResult rcv = command.decodeCommand( execute_time, responses[i] );
            }
            catch ( const RfnCommand::CommandException & ex )
            {
                BOOST_CHECK_EQUAL( ex.error_code, results[i].first );
                BOOST_CHECK_EQUAL( ex.what(),     results[i].second );
            }
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
