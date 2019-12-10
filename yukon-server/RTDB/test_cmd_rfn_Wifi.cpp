#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_Wifi.h"
#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_WifiCommunicationStatus )

const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );

BOOST_AUTO_TEST_CASE( test_getStatusCommand )
{
    Cti::Devices::Commands::RfnWifiGetCommunicationStatusUpdateCommand command;

    BOOST_CHECK( ! command.isPost());
    BOOST_CHECK(command.isOneWay());

    // execute
    {
        const std::vector< unsigned char > exp = { 0x48, 0x01 };

        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_SUITE_END()