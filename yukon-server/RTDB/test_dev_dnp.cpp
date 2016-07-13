#include <boost/test/auto_unit_test.hpp>

#include "dev_dnp.h"
#include "config_data_dnp.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using Cti::Test::byte_str;

struct test_DnpDevice : Cti::Devices::DnpDevice
{
    using CtiTblPAOLite::_name;
    using DnpDevice::_dnp;

    Cti::Test::DevicePointHelper pointHelper;

    CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type) override
    {
        return pointHelper.getCachedPoint(offset, type);
    }

    CtiPointSPtr getDeviceControlPointOffsetEqual(int offset) override
    {
        return pointHelper.getCachedStatusPointByControlOffset(offset);
    }

    void initControlPointOffset(int offset, CtiControlType_t controlType)
    {
        pointHelper.getCachedStatusPointByControlOffset(offset, controlType);
    }
};

BOOST_AUTO_TEST_SUITE( test_dev_dnp )

struct beginExecuteRequest_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    beginExecuteRequest_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(command_executions, beginExecuteRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_dev_dnp_control_sbo)
{
    test_DnpDevice dev;

    dev._name = "Test DNP device";
    dev._dnp.setAddresses(1234, 1);
    dev._dnp.setName("Test DNP device");

    //  set up the config
    Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    config.insertValue(Cti::Config::DNPStrings::internalRetries,         "3");
    config.insertValue(Cti::Config::DNPStrings::timeOffset,              "LOCAL");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs,      "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest,         "false");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3, "true");
    config.insertValue(Cti::Config::DNPStrings::disableFailedScanUpdates,"false");

    //  set up the control point
    dev.initControlPointOffset(10, ControlType_SBOPulse);

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("control close offset 10");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList) );

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK_EQUAL  (vgList .size(), 1);  //  LMControlHist msg
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.recvCommRequest(outList.front()) );

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList .clear();

    CtiXfer xfer;

    //  Select
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 18 C4 D2 04 01 00 67 04 "
                "C0 C1 03 0C 01 17 01 09 41 01 C8 01 00 00 00 00 26 E3 "
                "00 00 00 FF FF");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 1A 44 01 00 D2 04 6F 84");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(25, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "DE C3 81 00 00 0C 01 17 01 09 41 01 00 00 00 00 89 E7 "
                    "00 00 00 00 00 FF FF");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }
    //  Operate
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 18 C4 D2 04 01 00 67 04 "
                "C0 C2 04 0C 01 17 01 09 41 01 C8 01 00 00 00 00 DE E9 "
                "00 00 00 FF FF");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 1A 44 01 00 D2 04 6F 84");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(25, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "DF C4 81 00 00 0C 01 17 01 09 41 01 01 C8 00 00 D4 3B "
                    "00 00 00 00 00 FF FF");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());
    }

    INMESS inmess;

    dev.sendCommResult(inmess);

    dev.ResultDecode(inmess, CtiTime(), vgList, retList, outList);

    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(outList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::None);

    BOOST_CHECK_EQUAL(
            retMsg->ResultString(),
            "Test DNP device / Select successful, sending operate"
            "\nTest DNP device / Control result (0): Request accepted, initiated, or queued."
            "\nTest DNP device / Control result (0): Request accepted, initiated, or queued."
            "\nTest DNP device / "
            "\n");
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

