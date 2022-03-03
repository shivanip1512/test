#include <boost/test/unit_test.hpp>

#include "dev_rf_cellular_relay.h"
#include "cmd_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include "ctidate.h"

using namespace Cti::Devices;

struct test_state_rfCellularRelay
{
    std::unique_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RequestMsgList    requestMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    test_state_rfCellularRelay() :
        request( new CtiRequestMsg )
    {
    }

    void resetTestState()
    {
        request.reset( new CtiRequestMsg );
        returnMsgs.clear();
        rfnRequests.clear();
    }
};

namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char> &v);
}

const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfCellularRelay, test_state_rfCellularRelay )

BOOST_AUTO_TEST_CASE( test_dev_rfCellularRelay_getstatus_comm_status )
{
    Cti::Devices::RfCellularRelayDevice dut;

    // it should not respond to this, even though the same rfn message would go out on the wifi super meter from this command
    {
        CtiCommandParser parse("getstatus wifi");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::NoMethod );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "No Method or Invalid Command." );
        }
    }

    resetTestState();

    // this is the one it should respond to
    {
        CtiCommandParser parse("getstatus cell");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );
        {
            std::vector<unsigned char> exp
            {
                0x48,       // RfnGetCommunicationStatusUpdateCommand
                0x01
            };

            auto & command = rfnRequests.front();

            auto rcv = command->executeCommand( execute_time );

            BOOST_CHECK_EQUAL( 2, rcv.size() );

            BOOST_CHECK_EQUAL_COLLECTIONS( exp.begin(), exp.end(),
                                           rcv.begin(), rcv.end() );
        }

        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }
}


BOOST_AUTO_TEST_SUITE_END()

