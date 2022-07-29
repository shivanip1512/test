#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "cmd_rfn_DerPayloadDelivery.h"
#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnDerPayloadDeliveryCommand;


// --- defined in RTDB\test_main.cpp -- so BOOST_CHECK_EQUAL_COLLECTIONS() works for RfnCommand::CommandException
namespace boost         {
namespace test_tools    {
    bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs );
}
}

namespace std   {
    ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );
}
// --


BOOST_AUTO_TEST_SUITE( test_cmd_rfn_DerPayloadDelivery )

const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );

BOOST_AUTO_TEST_CASE( test_cmd_rfn_DerPayloadDelivery_test_1 )
{
    RfnDerPayloadDeliveryCommand  command( 101, 10011001, "000102030405060708090a0b0c0d0e0f" );

    BOOST_CHECK_EQUAL( "DER Payload Delivery Request",  command.getCommandName() );
    BOOST_CHECK_EQUAL( true,                            command.isOscoreEncrypted() );
    BOOST_CHECK_EQUAL( 10011001,                        command.getUserMessageId() );

    {
        const std::vector< unsigned char > exp
        {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };

        RfnCommand::RfnRequestPayload rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_SUITE_END()

