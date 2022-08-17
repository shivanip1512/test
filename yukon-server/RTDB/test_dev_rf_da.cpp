#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rf_da.h"
#include "cmd_rfn.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include "ctidate.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_state_rfDa
{
    std::unique_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfDa() :
        request( new CtiRequestMsg ),
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
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
const CtiTime decode_time ( CtiDate( 27, 8, 2013 ) , 16 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfDa, test_state_rfDa )

BOOST_AUTO_TEST_CASE( test_dev_rfDa_getconfig_dnp_address )
{
    Cti::Devices::RfDaDevice dut;

    BOOST_CHECK( ! dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress) );

    {
        CtiCommandParser parse("getconfig dnp address");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            BOOST_CHECK_EQUAL( static_cast<unsigned>(command->getApplicationServiceId()), 0x81 );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x35);

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response = boost::assign::list_of
                    (0x36)(0x12)(0x34);

            const auto results = command->handleResponse( decode_time, response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            BOOST_CHECK_EQUAL( result.description, "Read DNP Slave Address Request:"
                                                   "\nOutstation DNP3 address: 4660" );
            BOOST_CHECK_EQUAL( result.points.size(), 0 );

            dut.extractCommandResult( *command );

            BOOST_CHECK( dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress) );

            BOOST_CHECK_EQUAL( 0x1234, dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress) );
        }
    }
}


BOOST_AUTO_TEST_SUITE_END()

