#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_MeterProgramming.h"
#include "boost_test_helpers.h"

using namespace std::chrono_literals;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_MeterProgramming )

const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );

BOOST_AUTO_TEST_CASE( test_setConfigurationCommand )
{
    Cti::Devices::Commands::RfnMeterProgrammingSetConfigurationCommand command{ "3.14159", 31415 };

    // execute
    {
        std::vector<unsigned char> exp = { 
            0x90, 
            0x02, 
            0x01, 
                0x04, 
                    0x00, 0x00, 0x2b, 0xe3, 
            0x02, 
                0x33 };
        const std::string uri = "/meterPrograms/7d444840-9dc0-11d1-b245-5ffdce74fad2";
        exp.insert(exp.end(), uri.begin(), uri.end());
        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response { 0x63, 0x00 };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "No respondo" );
    }
}

BOOST_AUTO_TEST_SUITE_END()