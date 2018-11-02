#include <boost/test/auto_unit_test.hpp>

#include "dev_dnp.h"
#include "config_data_dnp.h"
#include "mgr_point.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using Cti::Test::byte_str;

struct test_DnpDevice : Cti::Devices::DnpDevice
{
    using CtiTblPAOLite::_name;
    using DnpDevice::_dnp;

    using DnpDevice::processPoints;

    Cti::Test::DevicePointHelper pointHelper;

    CtiPointSPtr selectedPoint;

    CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type) override
    {
        return pointHelper.getCachedPoint(offset, type);
    }

    CtiPointSPtr getDeviceControlPointOffsetEqual(int offset) override
    {
        return pointHelper.getCachedStatusPointByControlOffset(offset);
    }

    CtiPointSPtr getDeviceAnalogOutputPoint(int offset) override
    {
        return pointHelper.getCachedAnalogOutputPointByOffset(offset);
    }

    void initControlPointOffset(int offset, CtiControlType_t controlType)
    {
        pointHelper.getCachedStatusPointByControlOffset(offset, controlType);
    }

    void initAnalogOutput(int offset)
    {
        pointHelper.points[AnalogPointType].emplace(5000 + offset, Cti::Test::makeAnalogPoint(-1, 112358, offset + 10'000));
    }

    void initAnalogOutput(int offset, int analogOutput, bool controlInhibited)
    {
        pointHelper.points[AnalogPointType].emplace(5000 + analogOutput, Cti::Test::makeAnalogOutputPoint(-1, 112358, offset, analogOutput, controlInhibited));
    }

    CtiPointSPtr getDevicePointByID(int pointId) override
    {
        return selectedPoint;
    }
};

BOOST_AUTO_TEST_SUITE( test_dev_dnp )

BOOST_AUTO_TEST_CASE(test_dev_dnp_demand_accumulator)
{
    test_DnpDevice dev;

    struct : CtiPointManager
    {
        void getEqualByPAO(const long pao, std::vector<ptr_type>& points) override
        {
            points.emplace_back(ptr_type{ Cti::Test::makePulseAccumulatorPoint(pao, 1017, 17) });
            points.emplace_back(ptr_type{ Cti::Test::makeDemandAccumulatorPoint(pao, 2017, 17) });
        }
    } pt_mgr;

    dev.setPointManager(&pt_mgr);

    const CtiDate date { 212, 2017 };
    const CtiTime time { date, 12, 34, 56 };

    //  Initial point data
    {
        Cti::Protocols::Interface::pointlist_t points;

        auto msg = std::make_unique<CtiPointDataMsg>(17, 4, NormalQuality, PulseAccumulatorPointType);
        msg->setTime(time);

        points.push_back(msg.release());

        dev.processPoints(points);

        BOOST_REQUIRE_EQUAL(points.size(), 1);
        auto itr = points.cbegin();

        //  Unmodified
        BOOST_CHECK_EQUAL((*itr)->getId(),      1017);
        BOOST_CHECK_EQUAL((*itr)->getValue(),   4);
        BOOST_CHECK_EQUAL((*itr)->getQuality(), NormalQuality);
        BOOST_CHECK_EQUAL((*itr)->getType(),    PulseAccumulatorPointType);
        BOOST_CHECK_EQUAL((*itr)->getTime(),    time);

        delete_container(points);
    }

    //  Additional point data less than 1 minute in the future
    {
        Cti::Protocols::Interface::pointlist_t points;

        auto msg = std::make_unique<CtiPointDataMsg>(17, 5, NormalQuality, PulseAccumulatorPointType);
        msg->setTime(time + 59);

        points.push_back(msg.release());

        dev.processPoints(points);

        BOOST_REQUIRE_EQUAL(points.size(), 1);
        auto itr = points.cbegin();

        //  Unmodified
        BOOST_CHECK_EQUAL((*itr)->getId(),      1017);
        BOOST_CHECK_EQUAL((*itr)->getValue(),   5);
        BOOST_CHECK_EQUAL((*itr)->getQuality(), NormalQuality);
        BOOST_CHECK_EQUAL((*itr)->getType(),    PulseAccumulatorPointType);
        BOOST_CHECK_EQUAL((*itr)->getTime(),    time + 59);

        delete_container(points);
    }

    //  Additional point data exactly 1 minute from the first
    {
        Cti::Protocols::Interface::pointlist_t points;

        auto msg = std::make_unique<CtiPointDataMsg>(17, 6, NormalQuality, PulseAccumulatorPointType);
        msg->setTime(time + 60);

        points.push_back(msg.release());

        dev.processPoints(points);

        BOOST_REQUIRE_EQUAL(points.size(), 2);
        auto itr = points.cbegin();

        //  Unmodified
        BOOST_CHECK_EQUAL((*itr)->getId(),      1017);
        BOOST_CHECK_EQUAL((*itr)->getValue(),   6);
        BOOST_CHECK_EQUAL((*itr)->getQuality(), NormalQuality);
        BOOST_CHECK_EQUAL((*itr)->getType(),    PulseAccumulatorPointType);
        BOOST_CHECK_EQUAL((*itr)->getTime(),    time + 60);
        
        //  New demand accumulator value
        ++itr;
        BOOST_CHECK_EQUAL((*itr)->getId(),      2017);
        BOOST_CHECK_EQUAL((*itr)->getValue(),   120);
        BOOST_CHECK_EQUAL((*itr)->getQuality(), NormalQuality);
        BOOST_CHECK_EQUAL((*itr)->getType(),    DemandAccumulatorPointType);
        BOOST_CHECK_EQUAL((*itr)->getTime(),    time + 60);

        delete_container(points);
    }
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_no_config_data)
{
    std::list<CtiMessage*> vgList, retList;
    std::list<OUTMESS*> outList;

    //  First, test with no config assignment at all
    {
        Cti::Test::Override_ConfigManager overrideConfigManager { nullptr };

        test_DnpDevice dev;
        dev._name = "Test DNP device";

        //  start the request
        BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

        CtiCommandParser parse("scan integrity");

        CtiRequestMsg request;

        BOOST_CHECK_EQUAL(ClientErrors::MissingConfig, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::MissingConfig);

        BOOST_CHECK_EQUAL(
            retMsg->ResultString(),
            "Test DNP device / DNP configuration missing for DNP device");
    }

    delete_container(retList);  retList.clear();

    //  Test with a config with no entries
    {
        auto fixtureConfig = boost::make_shared<Cti::Test::test_DeviceConfig>();
        Cti::Test::Override_ConfigManager overrideConfigManager{ fixtureConfig };

        test_DnpDevice dev;

        //  start the request
        BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

        CtiCommandParser parse("scan integrity");

        CtiRequestMsg request;

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(outList.size(), 1);
        BOOST_CHECK(vgList.empty());
        BOOST_CHECK(retList.empty());

        BOOST_CHECK_EQUAL(ClientErrors::NoConfigData, dev.recvCommRequest(outList.front()));
    }

    delete_container(outList);  outList.clear();
}
    
struct beginExecuteRequest_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    test_DnpDevice dev;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    beginExecuteRequest_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");

        //  set up the config
        fixtureConfig->insertValue(Cti::Config::DNPStrings::internalRetries,              "3");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::timeOffset,                   "UTC");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs,           "true");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::omitTimeRequest,              "true");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1,      "true");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2,      "true");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3,      "true");
        fixtureConfig->insertValue(Cti::Config::DNPStrings::enableNonUpdatedOnFailedScan, "false");
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

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_pointid_pointoffset)
{
    //  set up the control point - rely on the analog output's offset
    dev.selectedPoint.reset(Cti::Test::makeAnalogPoint(-1, 112358, 10099));

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("putvalue analog value 182 select pointid 112358");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList) );

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.recvCommRequest(outList.front()) );

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList .clear();

    CtiXfer xfer;

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

    BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

    const byte_str expected(
        "05 64 12 C4 D2 04 01 00 0c B8 "
        "C0 C1 05 29 02 28 01 00 62 00 B6 00 00 9D FE");

    //  copy them into int vectors so they display nicely
    const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL_RANGES(expected, output);
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_pointid_controloffset)
{
    //  set up the control point - rely on the analog's control offset
    dev.selectedPoint.reset(Cti::Test::makeAnalogOutputPoint(-1, 112358, 13, 99, false));

    CtiCommandParser parse("putvalue analog value 182 select pointid 112358");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList) );

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList .empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.recvCommRequest(outList.front()) );

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList .clear();

    CtiXfer xfer;

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

    BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

    const byte_str expected(
        "05 64 12 C4 D2 04 01 00 0c B8 "
        "C0 C1 05 29 02 28 01 00 62 00 B6 00 00 9D FE");

    //  copy them into int vectors so they display nicely
    const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL_RANGES(expected, output);
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_offset_pointoffset)
{
    //  set up the control point - rely on the analog output's offset
    dev.initAnalogOutput(99);

    CtiCommandParser parse("putvalue analog 99 182");

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList.clear();

    CtiXfer xfer;

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

    BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

    const byte_str expected(
        "05 64 12 C4 D2 04 01 00 0c B8 "
        "C0 C1 05 29 02 28 01 00 62 00 B6 00 00 9D FE");

    //  copy them into int vectors so they display nicely
    const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL_RANGES(expected, output);
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_offset_controloffset)
{
    //  set up the control point - rely on the analog's control offset
    dev.initAnalogOutput(13, 99, false);

    CtiCommandParser parse("putvalue analog 99 182");

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList.clear();

    CtiXfer xfer;

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

    BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

    const byte_str expected(
        "05 64 12 C4 D2 04 01 00 0c B8 "
        "C0 C1 05 29 02 28 01 00 62 00 B6 00 00 9D FE");

    //  copy them into int vectors so they display nicely
    const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL_RANGES(expected, output);
}
/*
BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_offset_no_point)
{
    CtiCommandParser parse("putvalue analog 99 182");

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList.clear();

    CtiXfer xfer;

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

    BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

    const byte_str expected(
        "05 64 12 C4 D2 04 01 00 0c B8 "
        "C0 C1 05 29 02 28 01 00 62 00 B6 00 00 9D FE");

    //  copy them into int vectors so they display nicely
    const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

    BOOST_CHECK_EQUAL_RANGES(expected, output);
}
*/
BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_fail_point_lookup_failed)
{
    CtiCommandParser parse("putvalue analog value 1776 select pointid 112358");

    BOOST_CHECK_EQUAL(ClientErrors::PointLookupFailed, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_CHECK(outList.empty());
    BOOST_CHECK(vgList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const auto msg = retList.front();

    BOOST_REQUIRE(msg);

    const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

    BOOST_REQUIRE(ret);

    BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
    BOOST_CHECK_EQUAL(ret->Status(), ClientErrors::PointLookupFailed);
    BOOST_CHECK_EQUAL(ret->DeviceId(), -1);
    BOOST_CHECK_EQUAL(ret->ResultString(),
        "Test DNP device / The specified point is not on the device");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_fail_no_control_information)
{
    CtiCommandParser parse("putvalue analog value 1776 select pointid 112358");

    dev.selectedPoint.reset(Cti::Test::makeAnalogPoint(-1, 112358, 15));

    BOOST_CHECK_EQUAL(ClientErrors::NoPointControlConfiguration, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_CHECK(outList.empty());
    BOOST_CHECK(vgList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const auto msg = retList.front();

    BOOST_REQUIRE(msg);

    const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

    BOOST_REQUIRE(ret);

    BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
    BOOST_CHECK_EQUAL(ret->Status(), ClientErrors::NoPointControlConfiguration);
    BOOST_CHECK_EQUAL(ret->DeviceId(), -1);
    BOOST_CHECK_EQUAL(ret->ResultString(),
        "Test DNP device / Analog point has no control offset");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_fail_pointid_control_inhibited)
{
    CtiCommandParser parse("putvalue analog value 1776 select pointid 112358");

    dev.selectedPoint.reset(Cti::Test::makeAnalogOutputPoint(-1, 112358, 15, 15, true));

    BOOST_CHECK_EQUAL(ClientErrors::ControlInhibitedOnPoint, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_CHECK(outList.empty());
    BOOST_CHECK(vgList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const auto msg = retList.front();

    BOOST_REQUIRE(msg);

    const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

    BOOST_REQUIRE(ret);

    BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
    BOOST_CHECK_EQUAL(ret->Status(), ClientErrors::ControlInhibitedOnPoint);
    BOOST_CHECK_EQUAL(ret->DeviceId(), -1);
    BOOST_CHECK_EQUAL(ret->ResultString(),
        "Test DNP device / Control is inhibited for the specified analog point"
        "\nPoint ID   : 112358"
        "\nPoint name : Analog15");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_fail_offset_control_inhibited)
{
    CtiCommandParser parse("putvalue analog 99 1776");

    dev.initAnalogOutput(13, 99, true);

    BOOST_CHECK_EQUAL(ClientErrors::ControlInhibitedOnPoint, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_CHECK(outList.empty());
    BOOST_CHECK(vgList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const auto msg = retList.front();

    BOOST_REQUIRE(msg);

    const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

    BOOST_REQUIRE(ret);

    BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
    BOOST_CHECK_EQUAL(ret->Status(), ClientErrors::ControlInhibitedOnPoint);
    BOOST_CHECK_EQUAL(ret->DeviceId(), -1);
    BOOST_CHECK_EQUAL(ret->ResultString(),
        "Test DNP device / Control is inhibited for the specified analog point"
        "\nPoint ID   : 112358"
        "\nPoint name : Analog13");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_control_fail_no_control_information)
{
    //  set up the control point
    dev.selectedPoint.reset(Cti::Test::makeStatusPoint(-1, 112358, 1997));

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("control close select pointid 112358");

    BOOST_CHECK_EQUAL(ClientErrors::NoPointControlConfiguration, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_CHECK(outList.empty());
    BOOST_CHECK(vgList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const auto msg = retList.front();

    BOOST_REQUIRE(msg);

    const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

    BOOST_REQUIRE(ret);

    BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
    BOOST_CHECK_EQUAL(ret->Status(), ClientErrors::NoPointControlConfiguration);
    BOOST_CHECK_EQUAL(ret->DeviceId(), -1);
    BOOST_CHECK_EQUAL(ret->ResultString(),
        "Test DNP device / The specified point has no control information");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_control_fail_not_status_point)
{
    //  set up the control point
    dev.selectedPoint.reset(Cti::Test::makeAnalogPoint(-1, 112358, 1997));

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("control close select pointid 112358");

    BOOST_CHECK_EQUAL(ClientErrors::PointLookupFailed, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_CHECK(outList.empty());
    BOOST_CHECK(vgList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const auto msg = retList.front();

    BOOST_REQUIRE(msg);

    const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

    BOOST_REQUIRE(ret);

    BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
    BOOST_CHECK_EQUAL(ret->Status(), ClientErrors::PointLookupFailed);
    BOOST_CHECK_EQUAL(ret->DeviceId(), -1);
    BOOST_CHECK_EQUAL(ret->ResultString(),
        "Test DNP device / The specified point is not Status type");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_ping)
{
    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("ping");

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList.clear();

    CtiXfer xfer;

    //  Outbound
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 05 C9 D2 04 01 00 A4 E4");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    //  Inbounds
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                "05 64 05 0B 01 00 D2 04 AD 34");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(),
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

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
        "Test DNP device / Loopback successful"
        "\n");
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_getstatus_internal)
{
    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("getstatus internal");

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList.clear();

    CtiXfer xfer;

    //  Outbound
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 08 C4 D2 04 01 00 A6 7C "
            "C0 C1 17 8C 0C");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    //  Inbounds
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                "05 64 10 44 01 00 D2 04 04 38");

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
        BOOST_CHECK_EQUAL(13, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                "DA CF 81 00 00 34 02 07 01 00 00 75 AA");

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
        "Test DNP device / Successfully read internal indications"
        "\nTest DNP device / "
        "\n");
}

BOOST_AUTO_TEST_CASE(test_integrity_scan)
{
    dev._dnp.setAddresses(39, 1020);

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("scan integrity");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList) );

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList .empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.recvCommRequest(outList.front()) );

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList .clear();

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 14 c4 27 00 fc 03 a3 30 "
            "c0 c1 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    const byte_str header { "05 64 2a 44 fc 03 27 00 53 bb" };
    const byte_str body   { "c0 cf 81 00 00 1e 01 28 01 00 11 00 01 4b 01 00 bb 2d "
                            "00 01 02 28 01 00 aa 00 81 14 01 28 01 00 a4 06 f7 f1 "
                            "01 ee 0c 00 00 f1 9f" };
         

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        //  make sure we don't copy more than they expect
        auto inBufItr = stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected());
        std::copy(header.begin(), header.end(), inBufItr);

        xfer.setInCountActual(header.size());

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(body.size(), xfer.getInCountExpected());
    }
    {
        //  make sure we don't copy more than they expect
        auto inBufItr = stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected());
        std::copy(body.begin(), body.end(), inBufItr);

        xfer.setInCountActual(body.size());

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

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
        "Test DNP device / "
        "\nTest DNP device / Point data report:"
        "\nAI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; "
        "\nFirst/Last 5 points of each type returned:"
        "\nAnalog inputs:"
        "\n[18:331]"
        "\nBinary inputs:"
        "\n[171:1]"
        "\nCounters:"
        "\n[1701:3310]"
        "\n"
        "\n");
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_CASE(test_processPoints)
{
    CtiPointDataMsg msg1, msg2;
    Cti::Protocols::Interface::pointlist_t points{ &msg1, &msg2 };

    msg1.setValue(4);
    msg1.setType(AnalogPointType);
    msg1.setId(17);

    msg2.setValue(1);
    msg2.setType(AnalogPointType);
    msg2.setId(19);

    test_DnpDevice dev;

    struct : CtiPointManager
    {
        Cti::Test::DevicePointHelper pointHelper;

        void getEqualByPAO(const long pao, std::vector<ptr_type>& points) override
        {
            points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 1717, 17) });
            points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 1919, 19) });
        }
    } pt_mgr;

    dev.setPointManager(&pt_mgr);

    dev.processPoints(points);

    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_CHECK_EQUAL(points[0]->getValue(), 4);
    BOOST_CHECK_EQUAL(points[0]->getType(), AnalogPointType);
    BOOST_CHECK_EQUAL(points[0]->getId(), 1717);

    BOOST_CHECK_EQUAL(points[1]->getValue(), 1);
    BOOST_CHECK_EQUAL(points[1]->getType(), AnalogPointType);
    BOOST_CHECK_EQUAL(points[1]->getId(), 1919);
}

BOOST_AUTO_TEST_SUITE_END()

