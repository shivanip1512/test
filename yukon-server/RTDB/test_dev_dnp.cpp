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

/*
2019-07-03 13:08:17.167  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) OUT:
05 64 14 c4 01 00 01 00 e1 12 c0 c6 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 aa
d7

2019-07-03 13:08:17.268  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:17.270  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
45 a6 81 12 00 20 04 28 e6 02 2c 03 05 01 00 cb 0d e0 1c 71 22 6b 01 2d 03 05
00 00 cb 1c 71 22 6b 01 4a d4 2e 03 05 01 00 cb 1c 71 22 6b 01 30 03 05 02 00
d7 a3 cb 1c 71 22 6b 01 31 03 05 02 00 cb 1c 71 22 6b 9f 51 01 32 03 05 01 00
cb 1c 71 22 6b 01 33 03 05 00 5f ad 00 cb 1c 71 22 6b 01 34 03 05 01 00 cb 1c
71 22 30 33 6b 01 36 03 05 02 00 cb 1c 71 22 6b 01 37 03 05 37 dc 02 00 cb 1c
71 22 6b 01 38 03 05 01 00 cb 1c 71 f0 a1 22 6b 01 39 03 05 00 00 cb 1c 71 22
6b 01 3a 03 96 ee 05 01 00 cb 1c 71 22 6b 01 32 07 05 01 00 f4 e1 c8 c6 98 22
6b 01 33 07 05 01 00 f4 e1 98 22 6b 01 34 d2 55 07 05 01 00 f4 e1 98 22 6b 01
35 07 05 00 00 f4 f7 98 e1 98 22 6b 01 36 07 05 00 00 f4 e1 98 22 6b 01 8d d1
37 07 05 00 00 f4 e1 98 22 6b 01 38 07 05 01 00 d9 63 f4 e1 98 22 6b 01 39 07
05 01 00 f4 e1 98 22 6b 54 03 01 3a 07 05 01 00 f4 e1 98 22 ab a5

2019-07-03 13:08:17.369  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:17.370  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
06 6b 01 3b 07 05 00 00 f4 e1 98 22 6b 01 3c 07 f8 53 05 00 00 f4 e1 98 22 6b
01 3d 07 05 00 00 f4 e1 73 b2 98 22 6b 01 3e 07 05 00 00 f4 e1 98 22 6b 01 3f
a6 6e 07 05 00 00 f4 e1 98 22 6b 01 40 07 05 00 00 f4 b0 79 e1 98 22 6b 01 41
07 05 00 00 f4 e1 98 22 6b 01 e8 48 42 07 05 00 00 f4 e1 98 22 6b 01 43 07 05
00 00 7c fa f4 e1 98 22 6b 01 84 00 05 02 00 db 61 9d 22 6b f7 13 01 85 00 05
02 00 db 61 9d 22 6b 01 86 00 05 02 67 17 00 db 61 9d 22 6b 01 87 00 05 01 00
db 61 9d 22 5b bb 6b 01 88 00 05 01 00 db 61 9d 22 6b 01 89 00 05 1e 1d 01 00
db 61 9d 22 6b 01 8a 00 05 01 00 db 61 9d c0 b6 22 6b 01 8b 00 05 02 00 db 61
9d 22 6b 01 8c 00 ec 1f 05 01 00 db 61 9d 22 6b 01 8d 00 05 00 00 db 61 9d 7f
9d 22 6b 01 8e 00 05 00 00 db 61 9d 22 6b 01 8f 39 e1 00 05 00 00 db 61 9d 22
6b 01 90 00 05 02 00 db 06 c7 61 9d 22 6b 01 91 00 05 02 00 e8 f0

2019-07-03 13:08:17.471  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:17.572  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
07 db 61 9d 22 6b 01 92 00 05 02 00 db 61 9d 22 56 df 6b 01 93 00 05 00 00 db
61 9d 22 6b 01 94 00 05 93 ae 00 00 db 61 9d 22 6b 01 95 00 05 00 00 db 61 9d
fd 26 22 6b 01 96 00 05 02 00 db 61 9d 22 6b 01 97 00 2b d4 05 02 00 db 61 9d
22 6b 01 98 00 05 02 00 db 61 4b a9 9d 22 6b 01 99 00 05 00 00 db 61 9d 22 6b
01 9a 16 7d 00 05 01 00 db 61 9d 22 6b 01 9b 00 05 00 00 db 0a c3 61 9d 22 6b
01 ba 00 05 02 00 dc 61 9d 22 6b 01 68 18 bb 00 05 02 00 dc 61 9d 22 6b 01 bc
00 05 02 00 34 ef dc 61 9d 22 6b 01 bd 00 05 00 00 dc 61 9d 22 6b d5 d2 01 be
00 05 00 00 dc 61 9d 22 6b 01 bf 00 05 00 35 bf 00 dc 61 9d 22 6b 01 c0 00 05
00 00 dc 61 9d 22 6a be 6b 01 c1 00 05 00 00 dc 61 9d 22 6b 01 c2 00 05 67 84
00 00 dc 61 9d 22 6b 01 c3 00 05 00 00 dc 61 9d 54 3b 22 6b 01 c4 00 05 00 00
dc 61 9d 22 6b 01 c5 00 f7 a4 05 00 00 dc 61 9d 22 6b 01 c6 2e 20

2019-07-03 13:08:17.673  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:17.775  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
08 00 05 01 00 dc 61 9d 22 6b 01 c7 00 05 01 00 e2 40 dc 61 9d 22 6b 01 c8 00
05 01 00 dc 61 9d 22 6b c8 7d 01 c9 00 05 00 00 dc 61 9d 22 6b 01 ca 00 05 00
34 4f 00 dc 61 9d 22 6b 01 cb 00 05 00 00 dc 61 9d 22 37 84 6b 01 32 01 05 02
00 dd 61 9d 22 6b 01 33 01 05 4d 14 02 00 dd 61 9d 22 6b 01 34 01 05 02 00 dd
61 9d 2a 16 22 6b 01 35 01 05 00 00 dd 61 9d 22 6b 01 36 01 58 02 05 00 00 dd
61 9d 22 6b 01 37 01 05 00 00 dd 61 f2 2d 9d 22 6b 01 38 01 05 02 00 dd 61 9d
22 6b 01 39 34 d0 01 05 02 00 dd 61 9d 22 6b 01 3a 01 05 02 00 dd 62 cd 61 9d
22 6b 01 3b 01 05 00 00 dd 61 9d 22 6b 01 f6 35 3c 01 05 01 00 dd 61 9d 22 6b
01 3d 01 05 00 00 41 77 dd 61 9d 22 6b 01 3e 01 05 02 00 dd 61 9d 22 6b 59 1d
01 3f 01 05 02 00 dd 61 9d 22 6b 01 40 01 05 02 93 5f 00 dd 61 9d 22 6b 01 41
01 05 00 00 dd 61 9d 22 40 d1 6b 01 42 01 05 01 00 dd 61 9d 37 d5

2019-07-03 13:08:17.875  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:17.977  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
09 22 6b 01 43 01 05 01 00 dd 61 9d 22 6b 01 6e ce 49 01 05 01 00 df 61 9d 22
6b 01 6f 01 05 01 00 df 84 72 61 9d 22 6b 01 70 01 05 01 00 df 61 9d 22 6b 01
d5 1c 71 01 05 00 00 df 61 9d 22 6b 01 72 01 05 00 00 56 52 df 61 9d 22 6b 01
73 01 05 00 00 df 61 9d 22 6b 79 fa 01 74 01 05 02 00 df 61 9d 22 6b 01 75 01
05 02 22 34 00 df 61 9d 22 6b 01 76 01 05 02 00 df 61 9d 22 89 18 6b 01 77 01
05 00 00 df 61 9d 22 6b 01 78 01 05 d1 85 00 00 df 61 9d 22 6b 01 79 01 05 00
00 df 61 9d 00 ca 22 6b 01 7a 01 05 04 00 df 61 9d 22 6b 01 7b 01 6d d1 05 04
00 df 61 9d 22 6b 01 7c 01 05 04 00 df 61 8c f9 9d 22 6b 01 7d 01 05 01 00 df
61 9d 22 6b 01 7e e1 bb 01 05 01 00 df 61 9d 22 6b 01 7f 01 05 01 00 df 45 0a
61 9d 22 6b 01 98 01 05 02 00 e0 61 9d 22 6b 01 d5 e3 99 01 05 02 00 e0 61 9d
22 6b 01 9a 01 05 02 00 b5 87 e0 61 9d 22 6b 01 9b 01 05 00 14 61

2019-07-03 13:08:18.078  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:18.179  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
0a 00 e0 61 9d 22 6b 01 9c 01 05 00 00 e0 61 9d 99 d3 22 6b 01 9d 01 05 00 00
e0 61 9d 22 6b 01 9e 01 df 7d 05 04 00 e0 61 9d 22 6b 01 9f 01 05 04 00 e0 61
8c 86 9d 22 6b 01 a0 01 05 04 00 e0 61 9d 22 6b 01 a1 16 7e 01 05 01 00 e0 61
9d 22 6b 01 a2 01 05 01 00 e0 87 b6 61 9d 22 6b 01 a3 01 05 01 00 e0 61 9d 22
6b 01 aa 38 a4 01 05 00 00 e0 61 9d 22 6b 01 a5 01 05 00 00 7b 41 e0 61 9d 22
6b 01 a6 01 05 00 00 e0 61 9d 22 6b e8 09 01 a8 01 05 00 00 e0 61 9d 22 6b 01
a9 01 05 00 92 af 00 e0 61 9d 22 6b 01 c4 02 05 01 00 e3 61 9d 22 10 5a 6b 01
c5 02 05 01 00 e3 61 9d 22 6b 01 c6 02 05 da 45 01 00 e3 61 9d 22 6b 01 c7 02
05 00 00 e3 61 9d 7a 6c 22 6b 01 c8 02 05 00 00 e3 61 9d 22 6b 01 c9 02 18 f0
05 00 00 e3 61 9d 22 6b 01 ca 02 05 01 00 e4 61 4d 5c 9d 22 6b 01 cb 02 05 01
00 e4 61 9d 22 6b 01 cc 37 89 02 05 01 00 e4 61 9d 22 6b 01 62 7a

2019-07-03 13:08:18.279  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:18.381  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
0b cd 02 05 00 00 e4 61 9d 22 6b 01 ce 02 05 00 93 31 00 e4 61 9d 22 6b 01 cf
02 05 00 00 e4 61 9d 22 d9 f6 6b 01 d0 02 05 00 00 e4 61 9d 22 6b 01 d1 02 05
8b b6 01 00 e4 61 9d 22 6b 01 d2 02 05 01 00 e4 61 9d 87 d9 22 6b 01 d3 02 05
00 00 e4 61 9d 22 6b 01 d4 02 6d fa 05 00 00 e4 61 9d 22 6b 01 d5 02 05 00 00
e4 61 0c 3d 9d 22 6b 01 54 03 05 01 00 5f 89 9d 22 6b 01 55 2c e1 03 05 01 00
5f 89 9d 22 6b 01 56 03 05 01 00 5f 84 ac 89 9d 22 6b 01 57 03 05 00 00 5f 89
9d 22 6b 01 60 31 58 03 05 00 00 5f 89 9d 22 6b 01 59 03 05 00 00 87 df 5f 89
9d 22 6b 01 5a 03 05 01 00 5f 89 9d 22 6b 09 47 01 5b 03 05 01 00 5f 89 9d 22
6b 01 5c 03 05 01 42 af 00 5f 89 9d 22 6b 01 5d 03 05 00 00 5f 89 9d 22 1e 3e
6b 01 5e 03 05 00 00 5f 89 9d 22 6b 01 5f 03 05 00 04 00 00 5f 89 9d 22 6b 01
88 05 05 01 00 61 89 9d 29 c7 22 6b 01 89 05 05 02 00 61 89 08 19

2019-07-03 13:08:18.481  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:18.582  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
0c 9d 22 6b 01 8a 05 05 01 00 61 89 9d 22 6b 01 c9 65 8b 05 05 00 00 61 89 9d
22 6b 01 8c 05 05 00 00 d2 41 61 89 9d 22 6b 01 8d 05 05 00 00 61 89 9d 22 6b
b9 12 01 8c 07 05 01 00 1c 9f 9d 22 6b 01 8d 07 05 01 8e 11 00 1c 9f 9d 22 6b
01 8e 07 05 01 00 1c 9f 9d 22 0d f1 6b 01 8f 07 05 00 00 1c 9f 9d 22 6b 01 90
07 05 3e e2 00 00 1c 9f 9d 22 6b 01 91 07 05 00 00 1c 9f 9d 39 c3 22 6b 01 92
07 05 01 00 1c 9f 9d 22 6b 01 93 07 eb 7b 05 01 00 1c 9f 9d 22 6b 01 94 07 05
01 00 1c 9f 0d f9 9d 22 6b 01 95 07 05 00 00 1c 9f 9d 22 6b 01 96 4f a4 07 05
00 00 1c 9f 9d 22 6b 01 97 07 05 00 00 1c ca f6 9f 9d 22 6b 01 98 07 05 01 00
1c 9f 9d 22 6b 01 d9 c2 99 07 05 01 00 1c 9f 9d 22 6b 01 9a 07 05 01 00 cd 54
1c 9f 9d 22 6b 01 9b 07 05 00 00 1c 9f 9d 22 6b 75 db 01 9c 07 05 00 00 1c 9f
9d 22 6b 01 9d 07 05 00 ff 6f 00 1c 9f 9d 22 6b 01 50 07 05 8d b0

2019-07-03 13:08:18.683  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:18.784  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
0d 01 00 87 9f 9d 22 6b 01 51 07 05 01 00 87 9f 1c cb 9d 22 6b 01 52 07 05 01
00 87 9f 9d 22 6b 01 53 9a 6b 07 05 00 00 87 9f 9d 22 6b 01 54 07 05 00 00 87
c4 0d 9f 9d 22 6b 01 55 07 05 00 00 87 9f 9d 22 6b 01 16 bb 56 07 05 01 00 87
9f 9d 22 6b 01 57 07 05 01 00 5b 43 87 9f 9d 22 6b 01 58 07 05 01 00 87 9f 9d
22 6b 08 6d 01 59 07 05 00 00 87 9f 9d 22 6b 01 5a 07 05 00 19 bf 00 87 9f 9d
22 6b 01 5b 07 05 00 00 87 9f 9d 22 3a eb 6b 01 5c 07 05 02 00 87 9f 9d 22 6b
01 5d 07 05 7e ad 02 00 87 9f 9d 22 6b 01 5e 07 05 02 00 87 9f 9d 03 7e 22 6b
01 5f 07 05 01 00 87 9f 9d 22 6b 01 60 07 9b cd 05 00 00 87 9f 9d 22 6b 01 61
07 05 01 00 87 9f a3 5a 9d 22 6b 01 1e 09 05 01 00 15 a5 9d 22 6b 01 1f 20 66
09 05 00 00 15 a5 9d 22 6b 01 20 09 05 01 00 15 cb 3a a5 9d 22 6b 01 21 09 05
00 00 15 a5 9d 22 6b 01 4b df 22 09 05 00 00 15 a5 9d 22 6b fd 90

2019-07-03 13:08:18.886  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:18.986  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
0e 01 23 09 05 00 00 15 a5 9d 22 6b 01 24 09 05 0e f3 01 00 15 a5 9d 22 6b 01
25 09 05 01 00 15 a5 9d ae 93 22 6b 01 26 09 05 01 00 15 a5 9d 22 6b 01 27 09
82 9f 05 00 00 15 a5 9d 22 6b 01 28 09 05 00 00 15 a5 24 86 9d 22 6b 01 29 09
05 00 00 15 a5 9d 22 6b 01 2a 52 fa 09 05 00 00 15 a5 9d 22 6b 01 2b 09 05 00
00 15 ff 2a a5 9d 22 6b 01 2c 09 05 00 00 15 a5 9d 22 6b 01 8d 27 2d 09 05 00
00 15 a5 9d 22 6b 01 2e 09 05 00 00 c3 6c 15 a5 9d 22 6b 01 2f 09 05 00 00 15
a5 9d 22 6b c4 26 01 30 09 05 00 00 15 a5 9d 22 6b 01 31 09 05 00 be 00 00 15
a5 9d 22 6b 01 32 09 05 00 00 15 a5 9d 22 77 f2 6b 01 33 09 05 00 00 15 a5 9d
22 6b 01 34 09 05 fc 3d 00 00 15 a5 9d 22 6b 01 35 09 05 00 00 15 a5 9d 7d d1
22 6b 01 8c 0d 05 01 00 27 c5 9d 22 6b 01 8d 0d 7d 38 05 01 00 27 c5 9d 22 6b
01 8e 0d 05 01 00 27 c5 fb 7f 9d 22 6b 01 8f 0d 05 00 00 27 0f ef

2019-07-03 13:08:19.087  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:19.189  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
0f c5 9d 22 6b 01 90 0d 05 00 00 27 c5 9d 22 6b eb d1 01 91 0d 05 00 00 27 c5
9d 22 6b 01 92 0d 05 01 3d 6c 00 27 c5 9d 22 6b 01 93 0d 05 01 00 27 c5 9d 22
ea d0 6b 01 94 0d 05 01 00 27 c5 9d 22 6b 01 95 0d 05 21 a1 00 00 27 c5 9d 22
6b 01 96 0d 05 00 00 27 c5 9d ce ca 22 6b 01 97 0d 05 00 00 27 c5 9d 22 6b 01
98 0d 80 fc 05 00 00 8b c5 9d 22 6b 01 99 0d 05 00 00 8b c5 61 32 9d 22 6b 01
9a 0d 05 00 00 8b c5 9d 22 6b 01 9b ee 7d 0d 05 00 00 8b c5 9d 22 6b 01 9c 0d
05 00 00 8b 70 f6 c5 9d 22 6b 01 9d 0d 05 00 00 8b c5 9d 22 6b 01 ff 42 9e 0d
05 01 00 8b c5 9d 22 6b 01 9f 0d 05 01 00 da dc 8b c5 9d 22 6b 01 a0 0d 05 01
00 8b c5 9d 22 6b 44 ef 01 a1 0d 05 00 00 8b c5 9d 22 6b 01 a2 0d 05 00 95 22
00 8b c5 9d 22 6b 01 a3 0d 05 00 00 8b c5 9d 22 44 70 6b 01 fe 0d 05 01 00 20
1f a1 22 6b 01 ff 0d 05 fd 93 01 00 21 1f a1 22 6b 01 00 0e 39 a3

2019-07-03 13:08:19.290  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:19.390  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
10 05 01 00 21 1f a1 22 6b 01 01 0e 05 00 00 21 f0 9f 1f a1 22 6b 01 02 0e 05
00 00 21 1f a1 22 6b 01 8b 55 03 0e 05 00 00 21 1f a1 22 6b 01 04 0e 05 01 00
09 c5 21 1f a1 22 6b 01 05 0e 05 02 00 21 1f a1 22 6b 8e 92 01 06 0e 05 01 00
21 1f a1 22 6b 01 07 0e 05 00 2c d8 00 21 1f a1 22 6b 01 08 0e 05 00 00 21 1f
a1 22 71 3f 6b 01 09 0e 05 00 00 21 1f a1 22 6b 01 0a 0e 05 f6 00 01 00 21 1f
a1 22 6b 01 0b 0e 05 01 00 21 1f a1 2b 4e 22 6b 01 0c 0e 05 01 00 21 1f a1 22
6b 01 0d 0e 84 c3 05 00 00 21 1f a1 22 6b 01 0e 0e 05 00 00 21 1f 63 62 a1 22
6b 01 0f 0e 05 00 00 21 1f a1 22 6b 01 5e 3d db 02 05 01 00 8b f5 a1 22 6b 01
5f 02 05 01 00 8b a5 da f5 a1 22 6b 01 60 02 05 01 00 8b f5 a1 22 6b 01 d0 da
61 02 05 00 00 8b f5 a1 22 6b 01 62 02 05 00 00 d7 64 8b f5 a1 22 6b 01 63 02
05 00 00 8b f5 a1 22 6b ce b6 01 64 02 05 01 00 8b f5 a1 22 ed f1

2019-07-03 13:08:19.492  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:19.592  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
11 6b 01 65 02 05 02 00 8b f5 a1 22 6b 01 66 02 0b 54 05 01 00 8b f5 a1 22 6b
01 67 02 05 00 00 8b f5 9c eb a1 22 6b 01 68 02 05 00 00 8b f5 a1 22 6b 01 69
cf 7e 02 05 00 00 8b f5 a1 22 6b 01 6a 02 05 01 00 8b 9f 95 f5 a1 22 6b 01 6b
02 05 01 00 8b f5 a1 22 6b 01 32 55 6c 02 05 01 00 8b f5 a1 22 6b 01 6d 02 05
00 00 e0 47 8b f5 a1 22 6b 01 6e 02 05 00 00 8b f5 a1 22 6b d0 56 01 6f 02 05
00 00 8b f5 a1 22 6b 01 70 02 05 01 f1 89 00 8b f5 a1 22 6b 01 71 02 05 01 00
8b f5 a1 22 c3 39 6b 01 72 02 05 01 00 8b f5 a1 22 6b 01 73 02 05 7e c4 00 00
8b f5 a1 22 6b 01 74 02 05 00 00 8b f5 a1 a4 27 22 6b 01 75 02 05 00 00 8b f5
a1 22 6b 01 b0 04 2f d4 05 01 00 4f 1d a2 22 6b 01 b1 04 05 01 00 4f 1d d4 0c
a2 22 6b 01 b2 04 05 01 00 4f 1d a2 22 6b 01 b3 69 59 04 05 00 00 4f 1d a2 22
6b 01 b4 04 05 00 00 4f c5 70 1d a2 22 6b 01 b5 04 05 00 00 81 86

2019-07-03 13:08:19.693  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:19.794  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
12 4f 1d a2 22 6b 01 b6 04 05 01 00 4f 1d a2 22 ad 6a 6b 01 b7 04 05 01 00 4f
1d a2 22 6b 01 b8 04 05 a0 64 01 00 4f 1d a2 22 6b 01 b9 04 05 00 00 4f 1d a2
03 19 22 6b 01 ba 04 05 00 00 4f 1d a2 22 6b 01 bb 04 5c 8c 05 00 00 4f 1d a2
22 6b 01 bc 04 05 00 00 4f 1d 55 ae a2 22 6b 01 bd 04 05 00 00 4f 1d a2 22 6b
01 be 6e 43 04 05 00 00 4f 1d a2 22 6b 01 bf 04 05 00 00 4f 49 79 1d a2 22 6b
01 c0 04 05 00 00 4f 1d a2 22 6b 01 07 76 c1 04 05 00 00 4f 1d a2 22 6b 01 c2
04 05 01 00 39 54 4f 1d a2 22 6b 01 c3 04 05 01 00 4f 1d a2 22 6b 45 e1 01 c4
04 05 00 00 4f 1d a2 22 6b 01 c5 04 05 00 18 77 00 4f 1d a2 22 6b 01 c6 04 05
00 00 4f 1d a2 22 65 3e 6b 01 c7 04 05 00 00 4f 1d a2 22 6b 01 c8 04 05 82 63
04 00 4f 1d a2 22 6b 01 c9 04 05 04 00 4f 1d a2 51 66 22 6b 01 ca 04 05 04 00
4f 1d a2 22 6b 01 cb 04 2b 18 05 01 00 4f 1d a2 22 6b 01 cc ef 4d

2019-07-03 13:08:19.896  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:19.997  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
13 04 05 01 00 4f 1d a2 22 6b 01 cd 04 05 01 00 96 a0 4f 1d a2 22 6b 01 ce 04
05 00 00 4f 1d a2 22 6b 39 5e 01 cf 04 05 00 00 4f 1d a2 22 6b 01 d0 04 05 00
9f db 00 4f 1d a2 22 6b 01 d1 04 05 00 00 4f 1d a2 22 1e 94 6b 01 d6 04 05 00
00 50 1d a2 22 6b 01 da 04 05 6c 3e 01 00 50 1d a2 22 6b 01 db 04 05 01 00 50
1d a2 44 92 22 6b 01 dc 04 05 01 00 50 1d a2 22 6b 01 dd 04 4d 45 05 00 00 50
1d a2 22 6b 01 de 04 05 00 00 50 1d 80 40 a2 22 6b 01 df 04 05 00 00 50 1d a2
22 6b 01 e0 fc 5b 04 05 00 00 50 1d a2 22 6b 01 e1 04 05 01 00 50 7c 80 1d a2
22 6b 01 e2 04 05 01 00 50 1d a2 22 6b 01 18 3a e4 04 05 00 00 51 1d a2 22 6b
01 e5 04 05 00 00 f3 d3 51 1d a2 22 6b 01 d6 05 05 01 00 51 1d a2 22 6b 33 1a
01 d7 05 05 01 00 51 1d a2 22 6b 01 d8 05 05 01 a3 20 00 51 1d a2 22 6b 01 d9
05 05 00 00 51 1d a2 22 6c 5d 6b 01 da 05 05 00 00 51 1d a2 9c b3

2019-07-03 13:08:20.097  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:20.199  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
14 22 6b 01 db 05 05 00 00 51 1d a2 22 6b 01 dc f1 b1 05 05 01 00 51 1d a2 22
6b 01 dd 05 05 01 00 51 4e 54 1d a2 22 6b 01 de 05 05 01 00 51 1d a2 22 6b 01
7e cc df 05 05 00 00 51 1d a2 22 6b 01 e0 05 05 00 00 85 5d 51 1d a2 22 6b 01
e1 05 05 00 00 51 1d a2 22 6b 7d 1a 01 e2 05 05 01 00 52 1d a2 22 6b 01 e3 05
05 01 32 ec 00 52 1d a2 22 6b 01 e4 05 05 01 00 52 1d a2 22 99 4f 6b 01 e5 05
05 00 00 52 1d a2 22 6b 01 e6 05 05 3d ad 00 00 52 1d a2 22 6b 01 e7 05 05 00
00 52 1d a2 0f 71 22 6b 01 e8 05 05 01 00 52 1d a2 22 6b 01 e9 05 0b 9e 05 01
00 52 1d a2 22 6b 01 ea 05 05 01 00 52 1d c0 c5 a2 22 6b 01 eb 05 05 00 00 52
1d a2 22 6b 01 ec 46 9b 05 05 00 00 52 1d a2 22 6b 01 ed 05 05 00 00 52 f7 d8
1d a2 22 6b 01 42 06 05 02 00 52 1d a2 22 6b 01 a7 c7 43 06 05 02 00 52 1d a2
22 6b 01 44 06 05 02 00 56 94 52 1d a2 22 6b 01 45 06 05 00 12 2a

2019-07-03 13:08:20.300  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:20.400  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
15 00 52 1d a2 22 6b 01 46 06 05 00 00 52 1d a2 20 67 22 6b 01 47 06 05 00 00
52 1d a2 22 6b 01 48 06 7f 9e 05 00 00 52 1d a2 22 6b 01 49 06 05 00 00 52 1d
78 2d a2 22 6b 01 4a 06 05 00 00 53 1d a2 22 6b 01 4b ac e3 06 05 00 00 53 1d
a2 22 6b 01 4c 06 05 00 00 53 36 e4 1d a2 22 6b 01 4d 06 05 00 00 53 1d a2 22
6b 01 be 8b 4e 06 05 03 00 53 1d a2 22 6b 01 4f 06 05 03 00 b9 86 53 1d a2 22
6b 01 50 06 05 03 00 53 1d a2 22 6b bb 8f 01 51 06 05 01 00 53 1d a2 22 6b 01
52 06 05 02 2f df 00 53 1d a2 22 6b 01 53 06 05 01 00 53 1d a2 22 b1 29 6b 01
54 06 05 02 00 53 1d a2 22 6b 01 55 06 05 57 b8 02 00 53 1d a2 22 6b 01 56 06
05 02 00 53 1d a2 bd 27 22 6b 01 57 06 05 00 00 53 1d a2 22 6b 01 58 06 11 91
05 00 00 53 1d a2 22 6b 01 59 06 05 00 00 53 1d c9 1a a2 22 6b 01 5a 06 05 01
00 53 1d a2 22 6b 01 5b 23 52 06 05 01 00 53 1d a2 22 6b 01 ee 3a

2019-07-03 13:08:20.501  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:20.602  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
16 5c 06 05 01 00 53 1d a2 22 6b 01 5d 06 05 00 1c a1 00 53 1d a2 22 6b 01 5e
06 05 00 00 53 1d a2 22 7f fa 6b 01 5f 06 05 00 00 53 1d a2 22 6b 01 96 06 05
a2 9f 03 00 54 1d a2 22 6b 01 97 06 05 03 00 54 1d a2 08 35 22 6b 01 98 06 05
02 00 54 1d a2 22 6b 01 99 06 7e 8e 05 01 00 54 1d a2 22 6b 01 9a 06 05 01 00
54 1d f2 24 a2 22 6b 01 9b 06 05 00 00 54 1d a2 22 6b 01 9c 72 6f 06 05 00 00
54 1d a2 22 6b 01 9d 06 05 00 00 54 33 09 1d a2 22 6b 01 9e 06 05 00 00 54 1d
a2 22 6b 01 5e bc 9f 06 05 00 00 54 1d a2 22 6b 01 a0 06 05 00 00 66 82 54 1d
a2 22 6b 01 a1 06 05 00 00 54 1d a2 22 6b 72 c9 01 ae 06 05 01 00 55 1d a2 22
6b 01 af 06 05 00 ec 13 00 55 1d a2 22 6b 01 b0 06 05 00 00 55 1d a2 22 6a 76
6b 01 b1 06 05 00 00 55 1d a2 22 6b 01 b2 06 05 7f 63 00 00 55 1d a2 22 6b 01
b3 06 05 00 00 55 1d a2 24 a1 22 6b 01 b4 06 05 02 00 55 1d 78 5b

2019-07-03 13:08:20.703  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:20.805  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
17 a2 22 6b 01 b5 06 05 02 00 55 1d a2 22 6b 01 c7 8c b6 06 05 02 00 55 1d a2
22 6b 01 b7 06 05 00 00 c3 df 55 1d a2 22 6b 01 b8 06 05 00 00 55 1d a2 22 6b
62 22 01 b9 06 05 00 00 55 1d a2 22 6b 01 6e 07 05 01 69 5c 00 82 33 a2 22 6b
01 6f 07 05 01 00 82 33 a2 22 43 d9 6b 01 70 07 05 01 00 82 33 a2 22 6b 01 71
07 05 7d b9 00 00 82 33 a2 22 6b 01 72 07 05 00 00 82 33 a2 44 43 22 6b 01 73
07 05 00 00 82 33 a2 22 6b 01 74 07 23 91 05 01 00 82 33 a2 22 6b 01 75 07 05
01 00 82 33 7a 77 a2 22 6b 01 76 07 05 01 00 82 33 a2 22 6b 01 77 0c a8 07 05
00 00 82 33 a2 22 6b 01 78 07 05 00 00 82 ba 2e 33 a2 22 6b 01 79 07 05 00 00
82 33 a2 22 6b 01 97 92 80 07 05 01 00 83 33 a2 22 6b 01 81 07 05 01 00 fa 73
83 33 a2 22 6b 01 82 07 05 01 00 83 33 a2 22 6b 1b 2a 01 83 07 05 00 00 83 33
a2 22 6b 01 84 07 05 00 0a 05 00 83 33 a2 22 6b 01 85 07 05 aa 53

2019-07-03 13:08:20.905  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:21.007  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
18 00 00 83 33 a2 22 6b 01 86 07 05 01 00 83 33 5a 41 a2 22 6b 01 87 07 05 02
00 83 33 a2 22 6b 01 88 c8 a5 07 05 01 00 83 33 a2 22 6b 01 89 07 05 00 00 83
a9 49 33 a2 22 6b 01 8a 07 05 00 00 83 33 a2 22 6b 01 9c 85 8b 07 05 00 00 83
33 a2 22 6b 01 fe 07 05 02 00 42 82 84 33 a2 22 6b 01 ff 07 05 02 00 84 33 a2
22 6b 72 86 01 00 08 05 02 00 84 33 a2 22 6b 01 01 08 05 00 ef 7f 00 84 33 a2
22 6b 01 02 08 05 01 00 84 33 a2 22 8b a2 6b 01 03 08 05 01 00 84 33 a2 22 6b
01 04 08 05 ea 93 01 00 84 33 a2 22 6b 01 05 08 05 01 00 84 33 a2 3a e9 22 6b
01 06 08 05 01 00 84 33 a2 22 6b 01 07 08 26 1e 05 00 00 84 33 a2 22 6b 01 08
08 05 00 00 84 33 7b e3 a2 22 6b 01 09 08 05 00 00 84 33 a2 22 6b 01 0a 77 a8
08 05 02 00 84 33 a2 22 6b 01 0b 08 05 02 00 84 34 76 33 a2 22 6b 01 0c 08 05
02 00 84 33 a2 22 6b 01 b0 4a 0d 08 05 00 00 84 33 a2 22 6b eb b7

2019-07-03 13:08:21.107  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:21.209  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
19 01 0e 08 05 00 00 84 33 a2 22 6b 01 0f 08 05 a5 30 00 00 84 33 a2 22 6b 01
10 08 05 01 00 85 33 a2 0d df 22 6b 01 11 08 05 02 00 85 33 a2 22 6b 01 12 08
cb 55 05 01 00 85 33 a2 22 6b 01 13 08 05 01 00 85 33 7e f8 a2 22 6b 01 14 08
05 01 00 85 33 a2 22 6b 01 15 57 7a 08 05 00 00 85 33 a2 22 6b 01 16 08 05 01
00 85 31 e1 33 a2 22 6b 01 17 08 05 01 00 85 33 a2 22 6b 01 1a 6f 18 08 05 01
00 85 33 a2 22 6b 01 19 08 05 00 00 c3 93 85 33 a2 22 6b 01 1a 08 05 00 00 85
33 a2 22 6b ca 46 01 1b 08 05 00 00 85 33 a2 22 6b 01 1c 08 05 01 23 44 00 85
33 a2 22 6b 01 1d 08 05 01 00 85 33 a2 22 67 b0 6b 01 1e 08 05 01 00 85 33 a2
22 6b 01 1f 08 05 98 3b 00 00 85 33 a2 22 6b 01 20 08 05 00 00 85 33 a2 10 b6
22 6b 01 21 08 05 00 00 85 33 a2 22 6b 01 22 08 f2 b5 05 01 00 85 33 a2 22 6b
01 23 08 05 01 00 85 33 4b f1 a2 22 6b 01 24 08 05 01 00 85 d8 f5

2019-07-03 13:08:21.310  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:21.410  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
1a 33 a2 22 6b 01 25 08 05 00 00 85 33 a2 22 6b 87 2c 01 26 08 05 00 00 85 33
a2 22 6b 01 27 08 05 00 78 11 00 85 33 a2 22 6b 01 28 08 05 02 00 86 33 a2 22
6a 22 6b 01 29 08 05 02 00 86 33 a2 22 6b 01 2a 08 05 57 96 02 00 86 33 a2 22
6b 01 2b 08 05 01 00 86 33 a2 70 ed 22 6b 01 2c 08 05 01 00 86 33 a2 22 6b 01
2d 08 d7 a9 05 01 00 86 33 a2 22 6b 01 2e 08 05 01 00 86 33 dc ae a2 22 6b 01
2f 08 05 01 00 86 33 a2 22 6b 01 30 10 c7 08 05 01 00 86 33 a2 22 6b 01 31 08
05 00 00 86 77 cb 33 a2 22 6b 01 32 08 05 00 00 86 33 a2 22 6b 01 dc ed 33 08
05 00 00 86 33 a2 22 6b 01 34 08 05 01 00 eb 93 86 33 a2 22 6b 01 35 08 05 02
00 86 33 a2 22 6b 44 52 01 36 08 05 01 00 86 33 a2 22 6b 01 37 08 05 00 88 51
00 86 33 a2 22 6b 01 38 08 05 00 00 86 33 a2 22 3b 9e 6b 01 39 08 05 00 00 86
33 a2 22 6b 01 3a 08 05 39 55 02 00 87 33 a2 22 6b 01 3b 08 c1 e6

2019-07-03 13:08:21.511  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:21.611  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
1b 05 02 00 87 33 a2 22 6b 01 3c 08 05 02 00 87 32 75 33 a2 22 6b 01 3d 08 05
00 00 87 33 a2 22 6b 01 e1 8d 3e 08 05 00 00 87 33 a2 22 6b 01 3f 08 05 00 00
3f de 87 33 a2 22 6b 01 40 08 05 01 00 88 33 a2 22 6b 4c 91 01 41 08 05 01 00
88 33 a2 22 6b 01 42 08 05 01 16 7f 00 88 33 a2 22 6b 01 43 08 05 00 00 88 33
a2 22 c3 11 6b 01 44 08 05 00 00 88 33 a2 22 6b 01 45 08 05 a7 f2 00 00 88 33
a2 22 6b 01 46 08 05 01 00 88 33 a2 fd c5 22 6b 01 47 08 05 01 00 88 33 a2 22
6b 01 48 08 c0 24 05 02 00 88 33 a2 22 6b 01 49 08 05 00 00 88 33 53 b2 a2 22
6b 01 4a 08 05 00 00 88 33 a2 22 6b 01 4b 2b 47 08 05 00 00 88 33 a2 22 6b 01
4c 08 05 02 00 89 01 c7 33 a2 22 6b 01 4d 08 05 02 00 89 33 a2 22 6b 01 0f a8
4e 08 05 02 00 89 33 a2 22 6b 01 4f 08 05 00 00 70 97 89 33 a2 22 6b 01 50 08
05 00 00 89 33 a2 22 6b 6c 44 01 51 08 05 00 00 89 33 a2 22 f8 27

2019-07-03 13:08:21.713  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:21.814  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
1c 6b 01 52 08 05 01 00 89 33 a2 22 6b 01 53 08 62 21 05 01 00 89 33 a2 22 6b
01 54 08 05 01 00 89 33 53 fb a2 22 6b 01 55 08 05 00 00 89 33 a2 22 6b 01 56
c1 b3 08 05 00 00 89 33 a2 22 6b 01 57 08 05 00 00 89 db 69 33 a2 22 6b 01 58
08 05 02 00 89 33 a2 22 6b 01 20 c1 59 08 05 02 00 89 33 a2 22 6b 01 5a 08 05
02 00 99 84 89 33 a2 22 6b 01 5b 08 05 00 00 89 33 a2 22 6b cc b5 01 5c 08 05
00 00 89 33 a2 22 6b 01 5d 08 05 00 0a dd 00 89 33 a2 22 6b 01 5e 08 05 02 00
89 33 a2 22 63 d5 6b 01 5f 08 05 02 00 89 33 a2 22 6b 01 60 08 05 cb 5f 02 00
89 33 a2 22 6b 01 61 08 05 00 00 89 33 a2 67 60 22 6b 01 62 08 05 00 00 89 33
a2 22 6b 01 63 08 d9 72 05 00 00 89 33 a2 22 6b 01 64 08 05 01 00 89 33 7e 9c
a2 22 6b 01 65 08 05 01 00 89 33 a2 22 6b 01 66 d2 dc 08 05 02 00 89 33 a2 22
6b 01 67 08 05 00 00 89 78 9d 33 a2 22 6b 01 68 08 05 00 00 38 74

2019-07-03 13:08:21.914  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:22.015  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
1d 89 33 a2 22 6b 01 69 08 05 00 00 89 33 a2 22 39 68 6b 01 6a 08 05 03 00 89
33 a2 22 6b 01 6b 08 05 e0 22 03 00 89 33 a2 22 6b 01 6c 08 05 02 00 89 33 a2
2d 95 22 6b 01 6d 08 05 00 00 89 33 a2 22 6b 01 6e 08 3b 32 05 00 00 89 33 a2
22 6b 01 6f 08 05 00 00 89 33 d2 de a2 22 6b 01 70 08 05 01 00 8a 33 a2 22 6b
01 71 50 59 08 05 01 00 8a 33 a2 22 6b 01 72 08 05 01 00 8a 2a 65 33 a2 22 6b
01 73 08 05 00 00 8a 33 a2 22 6b 01 84 ba 74 08 05 00 00 8a 33 a2 22 6b 01 75
08 05 00 00 0a 73 8a 33 a2 22 6b 01 76 08 05 01 00 8a 33 a2 22 6b 59 8b 01 77
08 05 01 00 8a 33 a2 22 6b 01 78 08 05 01 25 00 00 8a 33 a2 22 6b 01 79 08 05
00 00 8a 33 a2 22 94 98 6b 01 7a 08 05 00 00 8a 33 a2 22 6b 01 7b 08 05 3a d9
00 00 8a 33 a2 22 6b 01 7c 08 05 02 00 8a 33 a2 38 06 22 6b 01 7d 08 05 02 00
8a 33 a2 22 6b 01 7e 08 cc da 05 02 00 8a 33 a2 22 6b 01 7f 8f 4f

2019-07-03 13:08:22.116  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:22.217  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
1e 08 05 00 00 8a 33 a2 22 6b 01 80 08 05 00 00 da 44 8a 33 a2 22 6b 01 81 08
05 00 00 8a 33 a2 22 6b 25 26 01 82 08 05 02 00 8a 33 a2 22 6b 01 83 08 05 02
af fc 00 8a 33 a2 22 6b 01 84 08 05 02 00 8a 33 a2 22 69 09 6b 01 85 08 05 01
00 8a 33 a2 22 6b 01 86 08 05 2f b2 01 00 8a 33 a2 22 6b 01 87 08 05 00 00 8a
33 a2 20 6d 22 6b 01 88 08 05 02 00 8a 33 a2 22 6b 01 89 08 97 b6 05 02 00 8a
33 a2 22 6b 01 8a 08 05 02 00 8a 33 1b 1a a2 22 6b 01 8b 08 05 01 00 8a 33 a2
22 6b 01 8c e9 15 08 05 01 00 8a 33 a2 22 6b 01 8d 08 05 01 00 8a 75 17 33 a2
22 6b 01 8e 08 05 02 00 8b 33 a2 22 6b 01 ff 42 8f 08 05 02 00 8b 33 a2 22 6b
01 90 08 05 02 00 88 87 8b 33 a2 22 6b 01 91 08 05 01 00 8b 33 a2 22 6b 4a a8
01 92 08 05 00 00 8b 33 a2 22 6b 01 93 08 05 00 0e 63 00 8b 33 a2 22 6b 01 94
08 05 02 00 8b 33 a2 22 57 8d 6b 01 95 08 05 02 00 8b 33 a2 78 d9

2019-07-03 13:08:22.318  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:22.419  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
1f 22 6b 01 96 08 05 02 00 8b 33 a2 22 6b 01 97 8f b4 08 05 00 00 8b 33 a2 22
6b 01 98 08 05 00 00 8b cc 6a 33 a2 22 6b 01 99 08 05 00 00 8b 33 a2 22 6b 01
e8 2a 9a 08 05 03 00 8c 33 a2 22 6b 01 9b 08 05 02 00 c2 1f 8c 33 a2 22 6b 01
9c 08 05 02 00 8c 33 a2 22 6b 3d ed 01 9d 08 05 01 00 8c 33 a2 22 6b 01 9e 08
05 01 fd 60 00 8c 33 a2 22 6b 01 9f 08 05 01 00 8c 33 a2 22 01 b5 6b 01 a0 08
05 02 00 8c 33 a2 22 6b 01 a1 08 05 4c cd 02 00 8c 33 a2 22 6b 01 a2 08 05 02
00 8c 33 a2 e0 38 22 6b 01 a3 08 05 01 00 8c 33 a2 22 6b 01 a4 08 ab 89 05 01
00 8c 33 a2 22 6b 01 a5 08 05 01 00 8c 33 fa 7b a2 22 6b 01 a6 08 05 00 00 8c
33 a2 22 6b 01 a7 34 7e 08 05 00 00 8c 33 a2 22 6b 01 a8 08 05 00 00 8c 52 7c
33 a2 22 6b 01 a9 08 05 00 00 8c 33 a2 22 6b 01 7d 9a aa 08 05 00 00 8c 33 a2
22 6b 01 ab 08 05 00 00 3b fa 8c 33 a2 22 6b 01 ac 08 05 01 a5 59

2019-07-03 13:08:22.521  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:22.621  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
20 00 8c 33 a2 22 6b 01 ad 08 05 02 00 8c 33 a2 d5 7c 22 6b 01 ae 08 05 02 00
8c 33 a2 22 6b 01 af 08 cf 53 05 00 00 8c 33 a2 22 6b 01 b0 08 05 00 00 8c 33
5d 7b a2 22 6b 01 b1 08 05 00 00 8c 33 a2 22 6b 01 b2 1b e2 08 05 02 00 8c 33
a2 22 6b 01 b3 08 05 02 00 8c 8f 43 33 a2 22 6b 01 b4 08 05 02 00 8c 33 a2 22
6b 01 06 6b b5 08 05 00 00 8c 33 a2 22 6b 01 b6 08 05 00 00 ec 51 8c 33 a2 22
6b 01 b7 08 05 00 00 8c 33 a2 22 6b 2e 90 01 bc 0a 05 01 00 8d 33 a2 22 6b 01
bd 0a 05 01 bf 8d 00 8d 33 a2 22 6b 01 be 0a 05 01 00 8d 33 a2 22 88 ba 6b 01
bf 0a 05 00 00 8d 33 a2 22 6b 01 c0 0a 05 fc d4 00 00 8d 33 a2 22 6b 01 c1 0a
05 00 00 8d 33 a2 98 af 22 6b 01 c2 0a 05 02 00 8d 33 a2 22 6b 01 c3 0a 3f e0
05 02 00 8d 33 a2 22 6b 01 c4 0a 05 02 00 8d 33 59 5c a2 22 6b 01 c5 0a 05 01
00 8d 33 a2 22 6b 01 c6 58 b2 0a 05 01 00 8d 33 a2 22 6b 01 a8 a8

2019-07-03 13:08:22.723  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:22.824  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
21 c7 0a 05 00 00 8d 33 a2 22 6b 01 c8 0a 05 01 c8 ce 00 8d 33 a2 22 6b 01 c9
0a 05 01 00 8d 33 a2 22 57 e2 6b 01 ca 0a 05 01 00 8d 33 a2 22 6b 01 cb 0a 05
fb 4b 00 00 8d 33 a2 22 6b 01 cc 0a 05 00 00 8d 33 a2 52 51 22 6b 01 cd 0a 05
00 00 8d 33 a2 22 6b 01 ce 0a 60 6b 05 01 00 8d 33 a2 22 6b 01 cf 0a 05 02 00
8d 33 35 6e a2 22 6b 01 d0 0a 05 01 00 8d 33 a2 22 6b 01 d1 7c d6 0a 05 00 00
8d 33 a2 22 6b 01 d2 0a 05 00 00 8d aa e0 33 a2 22 6b 01 d3 0a 05 00 00 8d 33
a2 22 6b 01 84 85 d4 0a 05 02 00 8d 33 a2 22 6b 01 d5 0a 05 02 00 4c e0 8d 33
a2 22 6b 01 d6 0a 05 02 00 8d 33 a2 22 6b 60 25 01 d7 0a 05 00 00 8d 33 a2 22
6b 01 d8 0a 05 00 9d 51 00 8d 33 a2 22 6b 01 d9 0a 05 00 00 8d 33 a2 22 7e 7f
6b 01 da 0a 05 02 00 8e 33 a2 22 6b 01 db 0a 05 21 b0 02 00 8e 33 a2 22 6b 01
dc 0a 05 02 00 8e 33 a2 c7 c9 22 6b 01 dd 0a 05 00 00 8e 33 78 bb

2019-07-03 13:08:22.925  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:23.025  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
22 a2 22 6b 01 de 0a 05 00 00 8e 33 a2 22 6b 01 c3 03 df 0a 05 00 00 8e 33 a2
22 6b 01 80 0d 05 00 00 17 bd 60 43 a2 22 6b 01 81 0d 05 00 00 60 43 a2 22 6b
8d eb 01 82 0d 05 00 00 60 43 a2 22 6b 01 83 0d 05 00 21 c9 00 60 43 a2 22 6b
01 84 0d 05 01 00 60 43 a2 22 ad 9d 6b 01 85 0d 05 00 00 60 43 a2 22 6b 01 86
0d 05 35 8e 01 00 60 43 a2 22 6b 01 87 0d 05 01 00 60 43 a2 30 75 22 6b 01 88
0d 05 01 00 60 43 a2 22 6b 01 89 0d e3 52 05 01 00 60 43 a2 22 6b 01 8a 0d 05
01 00 60 43 ad 2e a2 22 6b 01 8b 0d 05 01 00 60 43 a2 22 6b 01 a6 f8 f5 0e 05
02 00 61 43 a2 22 6b 01 a7 0e 05 02 00 61 fd 77 43 a2 22 6b 01 a8 0e 05 02 00
61 43 a2 22 6b 01 2d a9 a9 0e 05 00 00 61 43 a2 22 6b 01 aa 0e 05 00 00 3d 9f
61 43 a2 22 6b 01 ab 0e 05 00 00 61 43 a2 22 6b 88 ab 01 ac 0e 05 03 00 61 43
a2 22 6b 01 ad 0e 05 03 0f 2a 00 61 43 a2 22 6b 01 ae 0e 05 2a 5c

2019-07-03 13:08:23.127  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:23.227  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
23 03 00 61 43 a2 22 6b 01 af 0e 05 00 00 61 43 a0 1f a2 22 6b 01 b0 0e 05 00
00 61 43 a2 22 6b 01 b1 50 e4 0e 05 00 00 61 43 a2 22 6b 01 b4 0c 05 01 00 b7
05 7f 46 a2 22 6b 01 b5 0c 05 01 00 b7 46 a2 22 6b 01 c4 b0 b6 0c 05 01 00 b7
46 a2 22 6b 01 b7 0c 05 00 00 41 c3 b7 46 a2 22 6b 01 b8 0c 05 00 00 b7 46 a2
22 6b 27 34 01 b9 0c 05 00 00 b7 46 a2 22 6b 01 ba 0c 05 02 77 b2 00 b7 46 a2
22 6b 01 bb 0c 05 02 00 b7 46 a2 22 e2 62 6b 01 bc 0c 05 02 00 b7 46 a2 22 6b
01 bd 0c 05 a2 07 00 00 b7 46 a2 22 6b 01 be 0c 05 00 00 b7 46 a2 d4 cc 22 6b
01 bf 0c 05 00 00 b7 46 a2 22 6b 01 c0 0c f5 4c 05 01 00 b7 46 a2 22 6b 01 c1
0c 05 01 00 b7 46 96 44 a2 22 6b 01 c2 0c 05 02 00 b7 46 a2 22 6b 01 c3 83 11
0c 05 00 00 b7 46 a2 22 6b 01 c4 0c 05 00 00 b7 f0 13 46 a2 22 6b 01 c5 0c 05
00 00 b7 46 a2 22 6b 01 19 5e fc 0c 05 00 00 24 47 a2 22 6b b8 ff

2019-07-03 13:08:23.328  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 ff 44 01 00 01 00 65 34

2019-07-03 13:08:23.429  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
24 01 fd 0c 05 00 00 24 47 a2 22 6b 01 fe 0c 05 e3 0b 00 00 24 47 a2 22 6b 01
ff 0c 05 00 00 24 47 a2 fb c4 22 6b 01 00 0d 05 00 00 24 47 a2 22 6b 01 01 0d
02 8f 05 00 00 24 47 a2 22 6b 01 02 0d 05 01 00 24 47 d8 03 a2 22 6b 01 03 0d
05 01 00 24 47 a2 22 6b 01 04 3a 70 0d 05 01 00 24 47 a2 22 6b 01 05 0d 05 00
00 24 1a bf 47 a2 22 6b 01 06 0d 05 00 00 24 47 a2 22 6b 01 a4 c5 07 0d 05 00
00 24 47 a2 22 6b 01 08 0d 05 02 00 49 2f 25 47 a2 22 6b 01 09 0d 05 02 00 25
47 a2 22 6b 3f fe 01 0a 0d 05 02 00 25 47 a2 22 6b 01 0b 0d 05 00 56 cd 00 25
47 a2 22 6b 01 0c 0d 05 00 00 25 47 a2 22 14 26 6b 01 0d 0d 05 00 00 25 47 a2
22 6b 01 0e 0d 05 b4 ac 02 00 25 47 a2 22 6b 01 0f 0d 05 02 00 25 47 a2 fb 8b
22 6b 01 10 0d 05 02 00 25 47 a2 22 6b 01 11 0d d1 4b 05 00 00 25 47 a2 22 6b
01 12 0d 05 00 00 25 47 81 f6 a2 22 6b 01 13 0d 05 00 00 25 10 9b

2019-07-03 13:08:23.530  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
05 64 d1 44 01 00 01 00 e2 34

2019-07-03 13:08:23.632  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) IN:
a5 47 a2 22 6b 01 ec 0d 05 02 00 26 47 a2 22 6b 1d 2a 01 ed 0d 05 01 00 26 47
a2 22 6b 01 ee 0d 05 02 08 96 00 26 47 a2 22 6b 01 ef 0d 05 00 00 26 47 a2 22
ea 49 6b 01 f0 0d 05 00 00 26 47 a2 22 6b 01 f1 0d 05 85 b1 00 00 26 47 a2 22
6b 01 f2 0d 05 02 00 26 47 a2 cf 6d 22 6b 01 f3 0d 05 02 00 26 47 a2 22 6b 01
f4 0d c6 90 05 02 00 26 47 a2 22 6b 01 f5 0d 05 00 00 26 47 25 44 a2 22 6b 01
f6 0d 05 00 00 26 47 a2 22 6b 01 f7 45 2e 0d 05 01 00 26 47 a2 22 6b 01 f8 0d
05 01 00 27 1f 3c 47 a2 22 6b 01 f9 0d 05 01 00 27 47 a2 22 6b 01 42 0c fa 0d
05 01 00 27 47 a2 22 6b 01 fb 0d 05 00 00 4d 97 27 47 a2 22 6b 01 fc 0d 05 00
00 27 47 a2 22 6b 4c a4 01 fd 0d 05 00 00 27 47 a2 22 6b 01 3e b3

2019-07-03 13:08:23.731  P: 34979 / TCP port GE_DSCADA  D: 35009 / GE_SCADA (cltrpdelrp1:20000) OUT:
05 64 08 c4 01 00 01 00 92 bd c0 c6 00 1c ba
*/

BOOST_AUTO_TEST_CASE(test_integrity_scan_8k)
{
    test_DnpDevice dev;

    dev._name = "8k DNP test";
    dev._dnp.setAddresses(1, 1);
    dev._dnp.setName("8k DNP test");

    //  set up the config
    Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    config.insertValue(Cti::Config::DNPStrings::internalRetries, "3");
    config.insertValue(Cti::Config::DNPStrings::timeOffset, "UTC");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs, "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest, "false");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3, "true");
    config.insertValue(Cti::Config::DNPStrings::enableNonUpdatedOnFailedScan, "true");

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("scan integrity");

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList.clear();

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 14 c4 01 00 01 00 e1 12"
            " c0 c1 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    const std::vector<std::pair<byte_str, byte_str>> inbounds = {
        { "05 64 ff 44 01 00 01 00 65 34",
          "45 86 81 02 00 20 04 28 e6 02 2c 03 05 01 00 cb 87 21 1c 71 22 6b 01 2d 03 05 "
          "00 00 cb 1c 71 22 6b 01 4a d4 2e 03 05 01 00 cb 1c 71 22 6b 01 30 03 05 02 00 "
          "d7 a3 cb 1c 71 22 6b 01 31 03 05 02 00 cb 1c 71 22 6b 9f 51 01 32 03 05 01 00 "
          "cb 1c 71 22 6b 01 33 03 05 00 5f ad 00 cb 1c 71 22 6b 01 34 03 05 01 00 cb 1c "
          "71 22 30 33 6b 01 36 03 05 02 00 cb 1c 71 22 6b 01 37 03 05 37 dc 02 00 cb 1c "
          "71 22 6b 01 38 03 05 01 00 cb 1c 71 f0 a1 22 6b 01 39 03 05 00 00 cb 1c 71 22 "
          "6b 01 3a 03 96 ee 05 01 00 cb 1c 71 22 6b 01 32 07 05 01 00 f4 e1 c8 c6 98 22 "
          "6b 01 33 07 05 01 00 f4 e1 98 22 6b 01 34 d2 55 07 05 01 00 f4 e1 98 22 6b 01 "
          "35 07 05 00 00 f4 f7 98 e1 98 22 6b 01 36 07 05 00 00 f4 e1 98 22 6b 01 8d d1 "
          "37 07 05 00 00 f4 e1 98 22 6b 01 38 07 05 01 00 d9 63 f4 e1 98 22 6b 01 39 07 "
          "05 01 00 f4 e1 98 22 6b 54 03 01 3a 07 05 01 00 f4 e1 98 22 ab a5" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "06 6b 01 3b 07 05 00 00 f4 e1 98 22 6b 01 3c 07 f8 53 05 00 00 f4 e1 98 22 6b "
          "01 3d 07 05 00 00 f4 e1 73 b2 98 22 6b 01 3e 07 05 00 00 f4 e1 98 22 6b 01 3f "
          "a6 6e 07 05 00 00 f4 e1 98 22 6b 01 40 07 05 00 00 f4 b0 79 e1 98 22 6b 01 41 "
          "07 05 00 00 f4 e1 98 22 6b 01 e8 48 42 07 05 00 00 f4 e1 98 22 6b 01 43 07 05 "
          "00 00 7c fa f4 e1 98 22 6b 01 84 00 05 02 00 db 61 9d 22 6b f7 13 01 85 00 05 "
          "02 00 db 61 9d 22 6b 01 86 00 05 02 67 17 00 db 61 9d 22 6b 01 87 00 05 01 00 "
          "db 61 9d 22 5b bb 6b 01 88 00 05 01 00 db 61 9d 22 6b 01 89 00 05 1e 1d 01 00 "
          "db 61 9d 22 6b 01 8a 00 05 01 00 db 61 9d c0 b6 22 6b 01 8b 00 05 02 00 db 61 "
          "9d 22 6b 01 8c 00 ec 1f 05 01 00 db 61 9d 22 6b 01 8d 00 05 00 00 db 61 9d 7f "
          "9d 22 6b 01 8e 00 05 00 00 db 61 9d 22 6b 01 8f 39 e1 00 05 00 00 db 61 9d 22 "
          "6b 01 90 00 05 02 00 db 06 c7 61 9d 22 6b 01 91 00 05 02 00 e8 f0" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "07 db 61 9d 22 6b 01 92 00 05 02 00 db 61 9d 22 56 df 6b 01 93 00 05 00 00 db "
          "61 9d 22 6b 01 94 00 05 93 ae 00 00 db 61 9d 22 6b 01 95 00 05 00 00 db 61 9d "
          "fd 26 22 6b 01 96 00 05 02 00 db 61 9d 22 6b 01 97 00 2b d4 05 02 00 db 61 9d "
          "22 6b 01 98 00 05 02 00 db 61 4b a9 9d 22 6b 01 99 00 05 00 00 db 61 9d 22 6b "
          "01 9a 16 7d 00 05 01 00 db 61 9d 22 6b 01 9b 00 05 00 00 db 0a c3 61 9d 22 6b "
          "01 ba 00 05 02 00 dc 61 9d 22 6b 01 68 18 bb 00 05 02 00 dc 61 9d 22 6b 01 bc "
          "00 05 02 00 34 ef dc 61 9d 22 6b 01 bd 00 05 00 00 dc 61 9d 22 6b d5 d2 01 be "
          "00 05 00 00 dc 61 9d 22 6b 01 bf 00 05 00 35 bf 00 dc 61 9d 22 6b 01 c0 00 05 "
          "00 00 dc 61 9d 22 6a be 6b 01 c1 00 05 00 00 dc 61 9d 22 6b 01 c2 00 05 67 84 "
          "00 00 dc 61 9d 22 6b 01 c3 00 05 00 00 dc 61 9d 54 3b 22 6b 01 c4 00 05 00 00 "
          "dc 61 9d 22 6b 01 c5 00 f7 a4 05 00 00 dc 61 9d 22 6b 01 c6 2e 20" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "08 00 05 01 00 dc 61 9d 22 6b 01 c7 00 05 01 00 e2 40 dc 61 9d 22 6b 01 c8 00 "
          "05 01 00 dc 61 9d 22 6b c8 7d 01 c9 00 05 00 00 dc 61 9d 22 6b 01 ca 00 05 00 "
          "34 4f 00 dc 61 9d 22 6b 01 cb 00 05 00 00 dc 61 9d 22 37 84 6b 01 32 01 05 02 "
          "00 dd 61 9d 22 6b 01 33 01 05 4d 14 02 00 dd 61 9d 22 6b 01 34 01 05 02 00 dd "
          "61 9d 2a 16 22 6b 01 35 01 05 00 00 dd 61 9d 22 6b 01 36 01 58 02 05 00 00 dd "
          "61 9d 22 6b 01 37 01 05 00 00 dd 61 f2 2d 9d 22 6b 01 38 01 05 02 00 dd 61 9d "
          "22 6b 01 39 34 d0 01 05 02 00 dd 61 9d 22 6b 01 3a 01 05 02 00 dd 62 cd 61 9d "
          "22 6b 01 3b 01 05 00 00 dd 61 9d 22 6b 01 f6 35 3c 01 05 01 00 dd 61 9d 22 6b "
          "01 3d 01 05 00 00 41 77 dd 61 9d 22 6b 01 3e 01 05 02 00 dd 61 9d 22 6b 59 1d "
          "01 3f 01 05 02 00 dd 61 9d 22 6b 01 40 01 05 02 93 5f 00 dd 61 9d 22 6b 01 41 "
          "01 05 00 00 dd 61 9d 22 40 d1 6b 01 42 01 05 01 00 dd 61 9d 37 d5" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "09 22 6b 01 43 01 05 01 00 dd 61 9d 22 6b 01 6e ce 49 01 05 01 00 df 61 9d 22 "
          "6b 01 6f 01 05 01 00 df 84 72 61 9d 22 6b 01 70 01 05 01 00 df 61 9d 22 6b 01 "
          "d5 1c 71 01 05 00 00 df 61 9d 22 6b 01 72 01 05 00 00 56 52 df 61 9d 22 6b 01 "
          "73 01 05 00 00 df 61 9d 22 6b 79 fa 01 74 01 05 02 00 df 61 9d 22 6b 01 75 01 "
          "05 02 22 34 00 df 61 9d 22 6b 01 76 01 05 02 00 df 61 9d 22 89 18 6b 01 77 01 "
          "05 00 00 df 61 9d 22 6b 01 78 01 05 d1 85 00 00 df 61 9d 22 6b 01 79 01 05 00 "
          "00 df 61 9d 00 ca 22 6b 01 7a 01 05 04 00 df 61 9d 22 6b 01 7b 01 6d d1 05 04 "
          "00 df 61 9d 22 6b 01 7c 01 05 04 00 df 61 8c f9 9d 22 6b 01 7d 01 05 01 00 df "
          "61 9d 22 6b 01 7e e1 bb 01 05 01 00 df 61 9d 22 6b 01 7f 01 05 01 00 df 45 0a "
          "61 9d 22 6b 01 98 01 05 02 00 e0 61 9d 22 6b 01 d5 e3 99 01 05 02 00 e0 61 9d "
          "22 6b 01 9a 01 05 02 00 b5 87 e0 61 9d 22 6b 01 9b 01 05 00 14 61" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "0a 00 e0 61 9d 22 6b 01 9c 01 05 00 00 e0 61 9d 99 d3 22 6b 01 9d 01 05 00 00 "
          "e0 61 9d 22 6b 01 9e 01 df 7d 05 04 00 e0 61 9d 22 6b 01 9f 01 05 04 00 e0 61 "
          "8c 86 9d 22 6b 01 a0 01 05 04 00 e0 61 9d 22 6b 01 a1 16 7e 01 05 01 00 e0 61 "
          "9d 22 6b 01 a2 01 05 01 00 e0 87 b6 61 9d 22 6b 01 a3 01 05 01 00 e0 61 9d 22 "
          "6b 01 aa 38 a4 01 05 00 00 e0 61 9d 22 6b 01 a5 01 05 00 00 7b 41 e0 61 9d 22 "
          "6b 01 a6 01 05 00 00 e0 61 9d 22 6b e8 09 01 a8 01 05 00 00 e0 61 9d 22 6b 01 "
          "a9 01 05 00 92 af 00 e0 61 9d 22 6b 01 c4 02 05 01 00 e3 61 9d 22 10 5a 6b 01 "
          "c5 02 05 01 00 e3 61 9d 22 6b 01 c6 02 05 da 45 01 00 e3 61 9d 22 6b 01 c7 02 "
          "05 00 00 e3 61 9d 7a 6c 22 6b 01 c8 02 05 00 00 e3 61 9d 22 6b 01 c9 02 18 f0 "
          "05 00 00 e3 61 9d 22 6b 01 ca 02 05 01 00 e4 61 4d 5c 9d 22 6b 01 cb 02 05 01 "
          "00 e4 61 9d 22 6b 01 cc 37 89 02 05 01 00 e4 61 9d 22 6b 01 62 7a" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "0b cd 02 05 00 00 e4 61 9d 22 6b 01 ce 02 05 00 93 31 00 e4 61 9d 22 6b 01 cf "
          "02 05 00 00 e4 61 9d 22 d9 f6 6b 01 d0 02 05 00 00 e4 61 9d 22 6b 01 d1 02 05 "
          "8b b6 01 00 e4 61 9d 22 6b 01 d2 02 05 01 00 e4 61 9d 87 d9 22 6b 01 d3 02 05 "
          "00 00 e4 61 9d 22 6b 01 d4 02 6d fa 05 00 00 e4 61 9d 22 6b 01 d5 02 05 00 00 "
          "e4 61 0c 3d 9d 22 6b 01 54 03 05 01 00 5f 89 9d 22 6b 01 55 2c e1 03 05 01 00 "
          "5f 89 9d 22 6b 01 56 03 05 01 00 5f 84 ac 89 9d 22 6b 01 57 03 05 00 00 5f 89 "
          "9d 22 6b 01 60 31 58 03 05 00 00 5f 89 9d 22 6b 01 59 03 05 00 00 87 df 5f 89 "
          "9d 22 6b 01 5a 03 05 01 00 5f 89 9d 22 6b 09 47 01 5b 03 05 01 00 5f 89 9d 22 "
          "6b 01 5c 03 05 01 42 af 00 5f 89 9d 22 6b 01 5d 03 05 00 00 5f 89 9d 22 1e 3e "
          "6b 01 5e 03 05 00 00 5f 89 9d 22 6b 01 5f 03 05 00 04 00 00 5f 89 9d 22 6b 01 "
          "88 05 05 01 00 61 89 9d 29 c7 22 6b 01 89 05 05 02 00 61 89 08 19" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "0c 9d 22 6b 01 8a 05 05 01 00 61 89 9d 22 6b 01 c9 65 8b 05 05 00 00 61 89 9d "
          "22 6b 01 8c 05 05 00 00 d2 41 61 89 9d 22 6b 01 8d 05 05 00 00 61 89 9d 22 6b "
          "b9 12 01 8c 07 05 01 00 1c 9f 9d 22 6b 01 8d 07 05 01 8e 11 00 1c 9f 9d 22 6b "
          "01 8e 07 05 01 00 1c 9f 9d 22 0d f1 6b 01 8f 07 05 00 00 1c 9f 9d 22 6b 01 90 "
          "07 05 3e e2 00 00 1c 9f 9d 22 6b 01 91 07 05 00 00 1c 9f 9d 39 c3 22 6b 01 92 "
          "07 05 01 00 1c 9f 9d 22 6b 01 93 07 eb 7b 05 01 00 1c 9f 9d 22 6b 01 94 07 05 "
          "01 00 1c 9f 0d f9 9d 22 6b 01 95 07 05 00 00 1c 9f 9d 22 6b 01 96 4f a4 07 05 "
          "00 00 1c 9f 9d 22 6b 01 97 07 05 00 00 1c ca f6 9f 9d 22 6b 01 98 07 05 01 00 "
          "1c 9f 9d 22 6b 01 d9 c2 99 07 05 01 00 1c 9f 9d 22 6b 01 9a 07 05 01 00 cd 54 "
          "1c 9f 9d 22 6b 01 9b 07 05 00 00 1c 9f 9d 22 6b 75 db 01 9c 07 05 00 00 1c 9f "
          "9d 22 6b 01 9d 07 05 00 ff 6f 00 1c 9f 9d 22 6b 01 50 07 05 8d b0" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "0d 01 00 87 9f 9d 22 6b 01 51 07 05 01 00 87 9f 1c cb 9d 22 6b 01 52 07 05 01 "
          "00 87 9f 9d 22 6b 01 53 9a 6b 07 05 00 00 87 9f 9d 22 6b 01 54 07 05 00 00 87 "
          "c4 0d 9f 9d 22 6b 01 55 07 05 00 00 87 9f 9d 22 6b 01 16 bb 56 07 05 01 00 87 "
          "9f 9d 22 6b 01 57 07 05 01 00 5b 43 87 9f 9d 22 6b 01 58 07 05 01 00 87 9f 9d "
          "22 6b 08 6d 01 59 07 05 00 00 87 9f 9d 22 6b 01 5a 07 05 00 19 bf 00 87 9f 9d "
          "22 6b 01 5b 07 05 00 00 87 9f 9d 22 3a eb 6b 01 5c 07 05 02 00 87 9f 9d 22 6b "
          "01 5d 07 05 7e ad 02 00 87 9f 9d 22 6b 01 5e 07 05 02 00 87 9f 9d 03 7e 22 6b "
          "01 5f 07 05 01 00 87 9f 9d 22 6b 01 60 07 9b cd 05 00 00 87 9f 9d 22 6b 01 61 "
          "07 05 01 00 87 9f a3 5a 9d 22 6b 01 1e 09 05 01 00 15 a5 9d 22 6b 01 1f 20 66 "
          "09 05 00 00 15 a5 9d 22 6b 01 20 09 05 01 00 15 cb 3a a5 9d 22 6b 01 21 09 05 "
          "00 00 15 a5 9d 22 6b 01 4b df 22 09 05 00 00 15 a5 9d 22 6b fd 90" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "0e 01 23 09 05 00 00 15 a5 9d 22 6b 01 24 09 05 0e f3 01 00 15 a5 9d 22 6b 01 "
          "25 09 05 01 00 15 a5 9d ae 93 22 6b 01 26 09 05 01 00 15 a5 9d 22 6b 01 27 09 "
          "82 9f 05 00 00 15 a5 9d 22 6b 01 28 09 05 00 00 15 a5 24 86 9d 22 6b 01 29 09 "
          "05 00 00 15 a5 9d 22 6b 01 2a 52 fa 09 05 00 00 15 a5 9d 22 6b 01 2b 09 05 00 "
          "00 15 ff 2a a5 9d 22 6b 01 2c 09 05 00 00 15 a5 9d 22 6b 01 8d 27 2d 09 05 00 "
          "00 15 a5 9d 22 6b 01 2e 09 05 00 00 c3 6c 15 a5 9d 22 6b 01 2f 09 05 00 00 15 "
          "a5 9d 22 6b c4 26 01 30 09 05 00 00 15 a5 9d 22 6b 01 31 09 05 00 be 00 00 15 "
          "a5 9d 22 6b 01 32 09 05 00 00 15 a5 9d 22 77 f2 6b 01 33 09 05 00 00 15 a5 9d "
          "22 6b 01 34 09 05 fc 3d 00 00 15 a5 9d 22 6b 01 35 09 05 00 00 15 a5 9d 7d d1 "
          "22 6b 01 8c 0d 05 01 00 27 c5 9d 22 6b 01 8d 0d 7d 38 05 01 00 27 c5 9d 22 6b "
          "01 8e 0d 05 01 00 27 c5 fb 7f 9d 22 6b 01 8f 0d 05 00 00 27 0f ef" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "0f c5 9d 22 6b 01 90 0d 05 00 00 27 c5 9d 22 6b eb d1 01 91 0d 05 00 00 27 c5 "
          "9d 22 6b 01 92 0d 05 01 3d 6c 00 27 c5 9d 22 6b 01 93 0d 05 01 00 27 c5 9d 22 "
          "ea d0 6b 01 94 0d 05 01 00 27 c5 9d 22 6b 01 95 0d 05 21 a1 00 00 27 c5 9d 22 "
          "6b 01 96 0d 05 00 00 27 c5 9d ce ca 22 6b 01 97 0d 05 00 00 27 c5 9d 22 6b 01 "
          "98 0d 80 fc 05 00 00 8b c5 9d 22 6b 01 99 0d 05 00 00 8b c5 61 32 9d 22 6b 01 "
          "9a 0d 05 00 00 8b c5 9d 22 6b 01 9b ee 7d 0d 05 00 00 8b c5 9d 22 6b 01 9c 0d "
          "05 00 00 8b 70 f6 c5 9d 22 6b 01 9d 0d 05 00 00 8b c5 9d 22 6b 01 ff 42 9e 0d "
          "05 01 00 8b c5 9d 22 6b 01 9f 0d 05 01 00 da dc 8b c5 9d 22 6b 01 a0 0d 05 01 "
          "00 8b c5 9d 22 6b 44 ef 01 a1 0d 05 00 00 8b c5 9d 22 6b 01 a2 0d 05 00 95 22 "
          "00 8b c5 9d 22 6b 01 a3 0d 05 00 00 8b c5 9d 22 44 70 6b 01 fe 0d 05 01 00 20 "
          "1f a1 22 6b 01 ff 0d 05 fd 93 01 00 21 1f a1 22 6b 01 00 0e 39 a3" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "10 05 01 00 21 1f a1 22 6b 01 01 0e 05 00 00 21 f0 9f 1f a1 22 6b 01 02 0e 05 "
          "00 00 21 1f a1 22 6b 01 8b 55 03 0e 05 00 00 21 1f a1 22 6b 01 04 0e 05 01 00 "
          "09 c5 21 1f a1 22 6b 01 05 0e 05 02 00 21 1f a1 22 6b 8e 92 01 06 0e 05 01 00 "
          "21 1f a1 22 6b 01 07 0e 05 00 2c d8 00 21 1f a1 22 6b 01 08 0e 05 00 00 21 1f "
          "a1 22 71 3f 6b 01 09 0e 05 00 00 21 1f a1 22 6b 01 0a 0e 05 f6 00 01 00 21 1f "
          "a1 22 6b 01 0b 0e 05 01 00 21 1f a1 2b 4e 22 6b 01 0c 0e 05 01 00 21 1f a1 22 "
          "6b 01 0d 0e 84 c3 05 00 00 21 1f a1 22 6b 01 0e 0e 05 00 00 21 1f 63 62 a1 22 "
          "6b 01 0f 0e 05 00 00 21 1f a1 22 6b 01 5e 3d db 02 05 01 00 8b f5 a1 22 6b 01 "
          "5f 02 05 01 00 8b a5 da f5 a1 22 6b 01 60 02 05 01 00 8b f5 a1 22 6b 01 d0 da "
          "61 02 05 00 00 8b f5 a1 22 6b 01 62 02 05 00 00 d7 64 8b f5 a1 22 6b 01 63 02 "
          "05 00 00 8b f5 a1 22 6b ce b6 01 64 02 05 01 00 8b f5 a1 22 ed f1" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "11 6b 01 65 02 05 02 00 8b f5 a1 22 6b 01 66 02 0b 54 05 01 00 8b f5 a1 22 6b "
          "01 67 02 05 00 00 8b f5 9c eb a1 22 6b 01 68 02 05 00 00 8b f5 a1 22 6b 01 69 "
          "cf 7e 02 05 00 00 8b f5 a1 22 6b 01 6a 02 05 01 00 8b 9f 95 f5 a1 22 6b 01 6b "
          "02 05 01 00 8b f5 a1 22 6b 01 32 55 6c 02 05 01 00 8b f5 a1 22 6b 01 6d 02 05 "
          "00 00 e0 47 8b f5 a1 22 6b 01 6e 02 05 00 00 8b f5 a1 22 6b d0 56 01 6f 02 05 "
          "00 00 8b f5 a1 22 6b 01 70 02 05 01 f1 89 00 8b f5 a1 22 6b 01 71 02 05 01 00 "
          "8b f5 a1 22 c3 39 6b 01 72 02 05 01 00 8b f5 a1 22 6b 01 73 02 05 7e c4 00 00 "
          "8b f5 a1 22 6b 01 74 02 05 00 00 8b f5 a1 a4 27 22 6b 01 75 02 05 00 00 8b f5 "
          "a1 22 6b 01 b0 04 2f d4 05 01 00 4f 1d a2 22 6b 01 b1 04 05 01 00 4f 1d d4 0c "
          "a2 22 6b 01 b2 04 05 01 00 4f 1d a2 22 6b 01 b3 69 59 04 05 00 00 4f 1d a2 22 "
          "6b 01 b4 04 05 00 00 4f c5 70 1d a2 22 6b 01 b5 04 05 00 00 81 86" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "12 4f 1d a2 22 6b 01 b6 04 05 01 00 4f 1d a2 22 ad 6a 6b 01 b7 04 05 01 00 4f "
          "1d a2 22 6b 01 b8 04 05 a0 64 01 00 4f 1d a2 22 6b 01 b9 04 05 00 00 4f 1d a2 "
          "03 19 22 6b 01 ba 04 05 00 00 4f 1d a2 22 6b 01 bb 04 5c 8c 05 00 00 4f 1d a2 "
          "22 6b 01 bc 04 05 00 00 4f 1d 55 ae a2 22 6b 01 bd 04 05 00 00 4f 1d a2 22 6b "
          "01 be 6e 43 04 05 00 00 4f 1d a2 22 6b 01 bf 04 05 00 00 4f 49 79 1d a2 22 6b "
          "01 c0 04 05 00 00 4f 1d a2 22 6b 01 07 76 c1 04 05 00 00 4f 1d a2 22 6b 01 c2 "
          "04 05 01 00 39 54 4f 1d a2 22 6b 01 c3 04 05 01 00 4f 1d a2 22 6b 45 e1 01 c4 "
          "04 05 00 00 4f 1d a2 22 6b 01 c5 04 05 00 18 77 00 4f 1d a2 22 6b 01 c6 04 05 "
          "00 00 4f 1d a2 22 65 3e 6b 01 c7 04 05 00 00 4f 1d a2 22 6b 01 c8 04 05 82 63 "
          "04 00 4f 1d a2 22 6b 01 c9 04 05 04 00 4f 1d a2 51 66 22 6b 01 ca 04 05 04 00 "
          "4f 1d a2 22 6b 01 cb 04 2b 18 05 01 00 4f 1d a2 22 6b 01 cc ef 4d" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "13 04 05 01 00 4f 1d a2 22 6b 01 cd 04 05 01 00 96 a0 4f 1d a2 22 6b 01 ce 04 "
          "05 00 00 4f 1d a2 22 6b 39 5e 01 cf 04 05 00 00 4f 1d a2 22 6b 01 d0 04 05 00 "
          "9f db 00 4f 1d a2 22 6b 01 d1 04 05 00 00 4f 1d a2 22 1e 94 6b 01 d6 04 05 00 "
          "00 50 1d a2 22 6b 01 da 04 05 6c 3e 01 00 50 1d a2 22 6b 01 db 04 05 01 00 50 "
          "1d a2 44 92 22 6b 01 dc 04 05 01 00 50 1d a2 22 6b 01 dd 04 4d 45 05 00 00 50 "
          "1d a2 22 6b 01 de 04 05 00 00 50 1d 80 40 a2 22 6b 01 df 04 05 00 00 50 1d a2 "
          "22 6b 01 e0 fc 5b 04 05 00 00 50 1d a2 22 6b 01 e1 04 05 01 00 50 7c 80 1d a2 "
          "22 6b 01 e2 04 05 01 00 50 1d a2 22 6b 01 18 3a e4 04 05 00 00 51 1d a2 22 6b "
          "01 e5 04 05 00 00 f3 d3 51 1d a2 22 6b 01 d6 05 05 01 00 51 1d a2 22 6b 33 1a "
          "01 d7 05 05 01 00 51 1d a2 22 6b 01 d8 05 05 01 a3 20 00 51 1d a2 22 6b 01 d9 "
          "05 05 00 00 51 1d a2 22 6c 5d 6b 01 da 05 05 00 00 51 1d a2 9c b3" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "14 22 6b 01 db 05 05 00 00 51 1d a2 22 6b 01 dc f1 b1 05 05 01 00 51 1d a2 22 "
          "6b 01 dd 05 05 01 00 51 4e 54 1d a2 22 6b 01 de 05 05 01 00 51 1d a2 22 6b 01 "
          "7e cc df 05 05 00 00 51 1d a2 22 6b 01 e0 05 05 00 00 85 5d 51 1d a2 22 6b 01 "
          "e1 05 05 00 00 51 1d a2 22 6b 7d 1a 01 e2 05 05 01 00 52 1d a2 22 6b 01 e3 05 "
          "05 01 32 ec 00 52 1d a2 22 6b 01 e4 05 05 01 00 52 1d a2 22 99 4f 6b 01 e5 05 "
          "05 00 00 52 1d a2 22 6b 01 e6 05 05 3d ad 00 00 52 1d a2 22 6b 01 e7 05 05 00 "
          "00 52 1d a2 0f 71 22 6b 01 e8 05 05 01 00 52 1d a2 22 6b 01 e9 05 0b 9e 05 01 "
          "00 52 1d a2 22 6b 01 ea 05 05 01 00 52 1d c0 c5 a2 22 6b 01 eb 05 05 00 00 52 "
          "1d a2 22 6b 01 ec 46 9b 05 05 00 00 52 1d a2 22 6b 01 ed 05 05 00 00 52 f7 d8 "
          "1d a2 22 6b 01 42 06 05 02 00 52 1d a2 22 6b 01 a7 c7 43 06 05 02 00 52 1d a2 "
          "22 6b 01 44 06 05 02 00 56 94 52 1d a2 22 6b 01 45 06 05 00 12 2a" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "15 00 52 1d a2 22 6b 01 46 06 05 00 00 52 1d a2 20 67 22 6b 01 47 06 05 00 00 "
          "52 1d a2 22 6b 01 48 06 7f 9e 05 00 00 52 1d a2 22 6b 01 49 06 05 00 00 52 1d "
          "78 2d a2 22 6b 01 4a 06 05 00 00 53 1d a2 22 6b 01 4b ac e3 06 05 00 00 53 1d "
          "a2 22 6b 01 4c 06 05 00 00 53 36 e4 1d a2 22 6b 01 4d 06 05 00 00 53 1d a2 22 "
          "6b 01 be 8b 4e 06 05 03 00 53 1d a2 22 6b 01 4f 06 05 03 00 b9 86 53 1d a2 22 "
          "6b 01 50 06 05 03 00 53 1d a2 22 6b bb 8f 01 51 06 05 01 00 53 1d a2 22 6b 01 "
          "52 06 05 02 2f df 00 53 1d a2 22 6b 01 53 06 05 01 00 53 1d a2 22 b1 29 6b 01 "
          "54 06 05 02 00 53 1d a2 22 6b 01 55 06 05 57 b8 02 00 53 1d a2 22 6b 01 56 06 "
          "05 02 00 53 1d a2 bd 27 22 6b 01 57 06 05 00 00 53 1d a2 22 6b 01 58 06 11 91 "
          "05 00 00 53 1d a2 22 6b 01 59 06 05 00 00 53 1d c9 1a a2 22 6b 01 5a 06 05 01 "
          "00 53 1d a2 22 6b 01 5b 23 52 06 05 01 00 53 1d a2 22 6b 01 ee 3a" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "16 5c 06 05 01 00 53 1d a2 22 6b 01 5d 06 05 00 1c a1 00 53 1d a2 22 6b 01 5e "
          "06 05 00 00 53 1d a2 22 7f fa 6b 01 5f 06 05 00 00 53 1d a2 22 6b 01 96 06 05 "
          "a2 9f 03 00 54 1d a2 22 6b 01 97 06 05 03 00 54 1d a2 08 35 22 6b 01 98 06 05 "
          "02 00 54 1d a2 22 6b 01 99 06 7e 8e 05 01 00 54 1d a2 22 6b 01 9a 06 05 01 00 "
          "54 1d f2 24 a2 22 6b 01 9b 06 05 00 00 54 1d a2 22 6b 01 9c 72 6f 06 05 00 00 "
          "54 1d a2 22 6b 01 9d 06 05 00 00 54 33 09 1d a2 22 6b 01 9e 06 05 00 00 54 1d "
          "a2 22 6b 01 5e bc 9f 06 05 00 00 54 1d a2 22 6b 01 a0 06 05 00 00 66 82 54 1d "
          "a2 22 6b 01 a1 06 05 00 00 54 1d a2 22 6b 72 c9 01 ae 06 05 01 00 55 1d a2 22 "
          "6b 01 af 06 05 00 ec 13 00 55 1d a2 22 6b 01 b0 06 05 00 00 55 1d a2 22 6a 76 "
          "6b 01 b1 06 05 00 00 55 1d a2 22 6b 01 b2 06 05 7f 63 00 00 55 1d a2 22 6b 01 "
          "b3 06 05 00 00 55 1d a2 24 a1 22 6b 01 b4 06 05 02 00 55 1d 78 5b" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "17 a2 22 6b 01 b5 06 05 02 00 55 1d a2 22 6b 01 c7 8c b6 06 05 02 00 55 1d a2 "
          "22 6b 01 b7 06 05 00 00 c3 df 55 1d a2 22 6b 01 b8 06 05 00 00 55 1d a2 22 6b "
          "62 22 01 b9 06 05 00 00 55 1d a2 22 6b 01 6e 07 05 01 69 5c 00 82 33 a2 22 6b "
          "01 6f 07 05 01 00 82 33 a2 22 43 d9 6b 01 70 07 05 01 00 82 33 a2 22 6b 01 71 "
          "07 05 7d b9 00 00 82 33 a2 22 6b 01 72 07 05 00 00 82 33 a2 44 43 22 6b 01 73 "
          "07 05 00 00 82 33 a2 22 6b 01 74 07 23 91 05 01 00 82 33 a2 22 6b 01 75 07 05 "
          "01 00 82 33 7a 77 a2 22 6b 01 76 07 05 01 00 82 33 a2 22 6b 01 77 0c a8 07 05 "
          "00 00 82 33 a2 22 6b 01 78 07 05 00 00 82 ba 2e 33 a2 22 6b 01 79 07 05 00 00 "
          "82 33 a2 22 6b 01 97 92 80 07 05 01 00 83 33 a2 22 6b 01 81 07 05 01 00 fa 73 "
          "83 33 a2 22 6b 01 82 07 05 01 00 83 33 a2 22 6b 1b 2a 01 83 07 05 00 00 83 33 "
          "a2 22 6b 01 84 07 05 00 0a 05 00 83 33 a2 22 6b 01 85 07 05 aa 53" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "18 00 00 83 33 a2 22 6b 01 86 07 05 01 00 83 33 5a 41 a2 22 6b 01 87 07 05 02 "
          "00 83 33 a2 22 6b 01 88 c8 a5 07 05 01 00 83 33 a2 22 6b 01 89 07 05 00 00 83 "
          "a9 49 33 a2 22 6b 01 8a 07 05 00 00 83 33 a2 22 6b 01 9c 85 8b 07 05 00 00 83 "
          "33 a2 22 6b 01 fe 07 05 02 00 42 82 84 33 a2 22 6b 01 ff 07 05 02 00 84 33 a2 "
          "22 6b 72 86 01 00 08 05 02 00 84 33 a2 22 6b 01 01 08 05 00 ef 7f 00 84 33 a2 "
          "22 6b 01 02 08 05 01 00 84 33 a2 22 8b a2 6b 01 03 08 05 01 00 84 33 a2 22 6b "
          "01 04 08 05 ea 93 01 00 84 33 a2 22 6b 01 05 08 05 01 00 84 33 a2 3a e9 22 6b "
          "01 06 08 05 01 00 84 33 a2 22 6b 01 07 08 26 1e 05 00 00 84 33 a2 22 6b 01 08 "
          "08 05 00 00 84 33 7b e3 a2 22 6b 01 09 08 05 00 00 84 33 a2 22 6b 01 0a 77 a8 "
          "08 05 02 00 84 33 a2 22 6b 01 0b 08 05 02 00 84 34 76 33 a2 22 6b 01 0c 08 05 "
          "02 00 84 33 a2 22 6b 01 b0 4a 0d 08 05 00 00 84 33 a2 22 6b eb b7" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "19 01 0e 08 05 00 00 84 33 a2 22 6b 01 0f 08 05 a5 30 00 00 84 33 a2 22 6b 01 "
          "10 08 05 01 00 85 33 a2 0d df 22 6b 01 11 08 05 02 00 85 33 a2 22 6b 01 12 08 "
          "cb 55 05 01 00 85 33 a2 22 6b 01 13 08 05 01 00 85 33 7e f8 a2 22 6b 01 14 08 "
          "05 01 00 85 33 a2 22 6b 01 15 57 7a 08 05 00 00 85 33 a2 22 6b 01 16 08 05 01 "
          "00 85 31 e1 33 a2 22 6b 01 17 08 05 01 00 85 33 a2 22 6b 01 1a 6f 18 08 05 01 "
          "00 85 33 a2 22 6b 01 19 08 05 00 00 c3 93 85 33 a2 22 6b 01 1a 08 05 00 00 85 "
          "33 a2 22 6b ca 46 01 1b 08 05 00 00 85 33 a2 22 6b 01 1c 08 05 01 23 44 00 85 "
          "33 a2 22 6b 01 1d 08 05 01 00 85 33 a2 22 67 b0 6b 01 1e 08 05 01 00 85 33 a2 "
          "22 6b 01 1f 08 05 98 3b 00 00 85 33 a2 22 6b 01 20 08 05 00 00 85 33 a2 10 b6 "
          "22 6b 01 21 08 05 00 00 85 33 a2 22 6b 01 22 08 f2 b5 05 01 00 85 33 a2 22 6b "
          "01 23 08 05 01 00 85 33 4b f1 a2 22 6b 01 24 08 05 01 00 85 d8 f5" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "1a 33 a2 22 6b 01 25 08 05 00 00 85 33 a2 22 6b 87 2c 01 26 08 05 00 00 85 33 "
          "a2 22 6b 01 27 08 05 00 78 11 00 85 33 a2 22 6b 01 28 08 05 02 00 86 33 a2 22 "
          "6a 22 6b 01 29 08 05 02 00 86 33 a2 22 6b 01 2a 08 05 57 96 02 00 86 33 a2 22 "
          "6b 01 2b 08 05 01 00 86 33 a2 70 ed 22 6b 01 2c 08 05 01 00 86 33 a2 22 6b 01 "
          "2d 08 d7 a9 05 01 00 86 33 a2 22 6b 01 2e 08 05 01 00 86 33 dc ae a2 22 6b 01 "
          "2f 08 05 01 00 86 33 a2 22 6b 01 30 10 c7 08 05 01 00 86 33 a2 22 6b 01 31 08 "
          "05 00 00 86 77 cb 33 a2 22 6b 01 32 08 05 00 00 86 33 a2 22 6b 01 dc ed 33 08 "
          "05 00 00 86 33 a2 22 6b 01 34 08 05 01 00 eb 93 86 33 a2 22 6b 01 35 08 05 02 "
          "00 86 33 a2 22 6b 44 52 01 36 08 05 01 00 86 33 a2 22 6b 01 37 08 05 00 88 51 "
          "00 86 33 a2 22 6b 01 38 08 05 00 00 86 33 a2 22 3b 9e 6b 01 39 08 05 00 00 86 "
          "33 a2 22 6b 01 3a 08 05 39 55 02 00 87 33 a2 22 6b 01 3b 08 c1 e6" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "1b 05 02 00 87 33 a2 22 6b 01 3c 08 05 02 00 87 32 75 33 a2 22 6b 01 3d 08 05 "
          "00 00 87 33 a2 22 6b 01 e1 8d 3e 08 05 00 00 87 33 a2 22 6b 01 3f 08 05 00 00 "
          "3f de 87 33 a2 22 6b 01 40 08 05 01 00 88 33 a2 22 6b 4c 91 01 41 08 05 01 00 "
          "88 33 a2 22 6b 01 42 08 05 01 16 7f 00 88 33 a2 22 6b 01 43 08 05 00 00 88 33 "
          "a2 22 c3 11 6b 01 44 08 05 00 00 88 33 a2 22 6b 01 45 08 05 a7 f2 00 00 88 33 "
          "a2 22 6b 01 46 08 05 01 00 88 33 a2 fd c5 22 6b 01 47 08 05 01 00 88 33 a2 22 "
          "6b 01 48 08 c0 24 05 02 00 88 33 a2 22 6b 01 49 08 05 00 00 88 33 53 b2 a2 22 "
          "6b 01 4a 08 05 00 00 88 33 a2 22 6b 01 4b 2b 47 08 05 00 00 88 33 a2 22 6b 01 "
          "4c 08 05 02 00 89 01 c7 33 a2 22 6b 01 4d 08 05 02 00 89 33 a2 22 6b 01 0f a8 "
          "4e 08 05 02 00 89 33 a2 22 6b 01 4f 08 05 00 00 70 97 89 33 a2 22 6b 01 50 08 "
          "05 00 00 89 33 a2 22 6b 6c 44 01 51 08 05 00 00 89 33 a2 22 f8 27" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "1c 6b 01 52 08 05 01 00 89 33 a2 22 6b 01 53 08 62 21 05 01 00 89 33 a2 22 6b "
          "01 54 08 05 01 00 89 33 53 fb a2 22 6b 01 55 08 05 00 00 89 33 a2 22 6b 01 56 "
          "c1 b3 08 05 00 00 89 33 a2 22 6b 01 57 08 05 00 00 89 db 69 33 a2 22 6b 01 58 "
          "08 05 02 00 89 33 a2 22 6b 01 20 c1 59 08 05 02 00 89 33 a2 22 6b 01 5a 08 05 "
          "02 00 99 84 89 33 a2 22 6b 01 5b 08 05 00 00 89 33 a2 22 6b cc b5 01 5c 08 05 "
          "00 00 89 33 a2 22 6b 01 5d 08 05 00 0a dd 00 89 33 a2 22 6b 01 5e 08 05 02 00 "
          "89 33 a2 22 63 d5 6b 01 5f 08 05 02 00 89 33 a2 22 6b 01 60 08 05 cb 5f 02 00 "
          "89 33 a2 22 6b 01 61 08 05 00 00 89 33 a2 67 60 22 6b 01 62 08 05 00 00 89 33 "
          "a2 22 6b 01 63 08 d9 72 05 00 00 89 33 a2 22 6b 01 64 08 05 01 00 89 33 7e 9c "
          "a2 22 6b 01 65 08 05 01 00 89 33 a2 22 6b 01 66 d2 dc 08 05 02 00 89 33 a2 22 "
          "6b 01 67 08 05 00 00 89 78 9d 33 a2 22 6b 01 68 08 05 00 00 38 74" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "1d 89 33 a2 22 6b 01 69 08 05 00 00 89 33 a2 22 39 68 6b 01 6a 08 05 03 00 89 "
          "33 a2 22 6b 01 6b 08 05 e0 22 03 00 89 33 a2 22 6b 01 6c 08 05 02 00 89 33 a2 "
          "2d 95 22 6b 01 6d 08 05 00 00 89 33 a2 22 6b 01 6e 08 3b 32 05 00 00 89 33 a2 "
          "22 6b 01 6f 08 05 00 00 89 33 d2 de a2 22 6b 01 70 08 05 01 00 8a 33 a2 22 6b "
          "01 71 50 59 08 05 01 00 8a 33 a2 22 6b 01 72 08 05 01 00 8a 2a 65 33 a2 22 6b "
          "01 73 08 05 00 00 8a 33 a2 22 6b 01 84 ba 74 08 05 00 00 8a 33 a2 22 6b 01 75 "
          "08 05 00 00 0a 73 8a 33 a2 22 6b 01 76 08 05 01 00 8a 33 a2 22 6b 59 8b 01 77 "
          "08 05 01 00 8a 33 a2 22 6b 01 78 08 05 01 25 00 00 8a 33 a2 22 6b 01 79 08 05 "
          "00 00 8a 33 a2 22 94 98 6b 01 7a 08 05 00 00 8a 33 a2 22 6b 01 7b 08 05 3a d9 "
          "00 00 8a 33 a2 22 6b 01 7c 08 05 02 00 8a 33 a2 38 06 22 6b 01 7d 08 05 02 00 "
          "8a 33 a2 22 6b 01 7e 08 cc da 05 02 00 8a 33 a2 22 6b 01 7f 8f 4f" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "1e 08 05 00 00 8a 33 a2 22 6b 01 80 08 05 00 00 da 44 8a 33 a2 22 6b 01 81 08 "
          "05 00 00 8a 33 a2 22 6b 25 26 01 82 08 05 02 00 8a 33 a2 22 6b 01 83 08 05 02 "
          "af fc 00 8a 33 a2 22 6b 01 84 08 05 02 00 8a 33 a2 22 69 09 6b 01 85 08 05 01 "
          "00 8a 33 a2 22 6b 01 86 08 05 2f b2 01 00 8a 33 a2 22 6b 01 87 08 05 00 00 8a "
          "33 a2 20 6d 22 6b 01 88 08 05 02 00 8a 33 a2 22 6b 01 89 08 97 b6 05 02 00 8a "
          "33 a2 22 6b 01 8a 08 05 02 00 8a 33 1b 1a a2 22 6b 01 8b 08 05 01 00 8a 33 a2 "
          "22 6b 01 8c e9 15 08 05 01 00 8a 33 a2 22 6b 01 8d 08 05 01 00 8a 75 17 33 a2 "
          "22 6b 01 8e 08 05 02 00 8b 33 a2 22 6b 01 ff 42 8f 08 05 02 00 8b 33 a2 22 6b "
          "01 90 08 05 02 00 88 87 8b 33 a2 22 6b 01 91 08 05 01 00 8b 33 a2 22 6b 4a a8 "
          "01 92 08 05 00 00 8b 33 a2 22 6b 01 93 08 05 00 0e 63 00 8b 33 a2 22 6b 01 94 "
          "08 05 02 00 8b 33 a2 22 57 8d 6b 01 95 08 05 02 00 8b 33 a2 78 d9" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "1f 22 6b 01 96 08 05 02 00 8b 33 a2 22 6b 01 97 8f b4 08 05 00 00 8b 33 a2 22 "
          "6b 01 98 08 05 00 00 8b cc 6a 33 a2 22 6b 01 99 08 05 00 00 8b 33 a2 22 6b 01 "
          "e8 2a 9a 08 05 03 00 8c 33 a2 22 6b 01 9b 08 05 02 00 c2 1f 8c 33 a2 22 6b 01 "
          "9c 08 05 02 00 8c 33 a2 22 6b 3d ed 01 9d 08 05 01 00 8c 33 a2 22 6b 01 9e 08 "
          "05 01 fd 60 00 8c 33 a2 22 6b 01 9f 08 05 01 00 8c 33 a2 22 01 b5 6b 01 a0 08 "
          "05 02 00 8c 33 a2 22 6b 01 a1 08 05 4c cd 02 00 8c 33 a2 22 6b 01 a2 08 05 02 "
          "00 8c 33 a2 e0 38 22 6b 01 a3 08 05 01 00 8c 33 a2 22 6b 01 a4 08 ab 89 05 01 "
          "00 8c 33 a2 22 6b 01 a5 08 05 01 00 8c 33 fa 7b a2 22 6b 01 a6 08 05 00 00 8c "
          "33 a2 22 6b 01 a7 34 7e 08 05 00 00 8c 33 a2 22 6b 01 a8 08 05 00 00 8c 52 7c "
          "33 a2 22 6b 01 a9 08 05 00 00 8c 33 a2 22 6b 01 7d 9a aa 08 05 00 00 8c 33 a2 "
          "22 6b 01 ab 08 05 00 00 3b fa 8c 33 a2 22 6b 01 ac 08 05 01 a5 59" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "20 00 8c 33 a2 22 6b 01 ad 08 05 02 00 8c 33 a2 d5 7c 22 6b 01 ae 08 05 02 00 "
          "8c 33 a2 22 6b 01 af 08 cf 53 05 00 00 8c 33 a2 22 6b 01 b0 08 05 00 00 8c 33 "
          "5d 7b a2 22 6b 01 b1 08 05 00 00 8c 33 a2 22 6b 01 b2 1b e2 08 05 02 00 8c 33 "
          "a2 22 6b 01 b3 08 05 02 00 8c 8f 43 33 a2 22 6b 01 b4 08 05 02 00 8c 33 a2 22 "
          "6b 01 06 6b b5 08 05 00 00 8c 33 a2 22 6b 01 b6 08 05 00 00 ec 51 8c 33 a2 22 "
          "6b 01 b7 08 05 00 00 8c 33 a2 22 6b 2e 90 01 bc 0a 05 01 00 8d 33 a2 22 6b 01 "
          "bd 0a 05 01 bf 8d 00 8d 33 a2 22 6b 01 be 0a 05 01 00 8d 33 a2 22 88 ba 6b 01 "
          "bf 0a 05 00 00 8d 33 a2 22 6b 01 c0 0a 05 fc d4 00 00 8d 33 a2 22 6b 01 c1 0a "
          "05 00 00 8d 33 a2 98 af 22 6b 01 c2 0a 05 02 00 8d 33 a2 22 6b 01 c3 0a 3f e0 "
          "05 02 00 8d 33 a2 22 6b 01 c4 0a 05 02 00 8d 33 59 5c a2 22 6b 01 c5 0a 05 01 "
          "00 8d 33 a2 22 6b 01 c6 58 b2 0a 05 01 00 8d 33 a2 22 6b 01 a8 a8" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "21 c7 0a 05 00 00 8d 33 a2 22 6b 01 c8 0a 05 01 c8 ce 00 8d 33 a2 22 6b 01 c9 "
          "0a 05 01 00 8d 33 a2 22 57 e2 6b 01 ca 0a 05 01 00 8d 33 a2 22 6b 01 cb 0a 05 "
          "fb 4b 00 00 8d 33 a2 22 6b 01 cc 0a 05 00 00 8d 33 a2 52 51 22 6b 01 cd 0a 05 "
          "00 00 8d 33 a2 22 6b 01 ce 0a 60 6b 05 01 00 8d 33 a2 22 6b 01 cf 0a 05 02 00 "
          "8d 33 35 6e a2 22 6b 01 d0 0a 05 01 00 8d 33 a2 22 6b 01 d1 7c d6 0a 05 00 00 "
          "8d 33 a2 22 6b 01 d2 0a 05 00 00 8d aa e0 33 a2 22 6b 01 d3 0a 05 00 00 8d 33 "
          "a2 22 6b 01 84 85 d4 0a 05 02 00 8d 33 a2 22 6b 01 d5 0a 05 02 00 4c e0 8d 33 "
          "a2 22 6b 01 d6 0a 05 02 00 8d 33 a2 22 6b 60 25 01 d7 0a 05 00 00 8d 33 a2 22 "
          "6b 01 d8 0a 05 00 9d 51 00 8d 33 a2 22 6b 01 d9 0a 05 00 00 8d 33 a2 22 7e 7f "
          "6b 01 da 0a 05 02 00 8e 33 a2 22 6b 01 db 0a 05 21 b0 02 00 8e 33 a2 22 6b 01 "
          "dc 0a 05 02 00 8e 33 a2 c7 c9 22 6b 01 dd 0a 05 00 00 8e 33 78 bb" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "22 a2 22 6b 01 de 0a 05 00 00 8e 33 a2 22 6b 01 c3 03 df 0a 05 00 00 8e 33 a2 "
          "22 6b 01 80 0d 05 00 00 17 bd 60 43 a2 22 6b 01 81 0d 05 00 00 60 43 a2 22 6b "
          "8d eb 01 82 0d 05 00 00 60 43 a2 22 6b 01 83 0d 05 00 21 c9 00 60 43 a2 22 6b "
          "01 84 0d 05 01 00 60 43 a2 22 ad 9d 6b 01 85 0d 05 00 00 60 43 a2 22 6b 01 86 "
          "0d 05 35 8e 01 00 60 43 a2 22 6b 01 87 0d 05 01 00 60 43 a2 30 75 22 6b 01 88 "
          "0d 05 01 00 60 43 a2 22 6b 01 89 0d e3 52 05 01 00 60 43 a2 22 6b 01 8a 0d 05 "
          "01 00 60 43 ad 2e a2 22 6b 01 8b 0d 05 01 00 60 43 a2 22 6b 01 a6 f8 f5 0e 05 "
          "02 00 61 43 a2 22 6b 01 a7 0e 05 02 00 61 fd 77 43 a2 22 6b 01 a8 0e 05 02 00 "
          "61 43 a2 22 6b 01 2d a9 a9 0e 05 00 00 61 43 a2 22 6b 01 aa 0e 05 00 00 3d 9f "
          "61 43 a2 22 6b 01 ab 0e 05 00 00 61 43 a2 22 6b 88 ab 01 ac 0e 05 03 00 61 43 "
          "a2 22 6b 01 ad 0e 05 03 0f 2a 00 61 43 a2 22 6b 01 ae 0e 05 2a 5c" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "23 03 00 61 43 a2 22 6b 01 af 0e 05 00 00 61 43 a0 1f a2 22 6b 01 b0 0e 05 00 "
          "00 61 43 a2 22 6b 01 b1 50 e4 0e 05 00 00 61 43 a2 22 6b 01 b4 0c 05 01 00 b7 "
          "05 7f 46 a2 22 6b 01 b5 0c 05 01 00 b7 46 a2 22 6b 01 c4 b0 b6 0c 05 01 00 b7 "
          "46 a2 22 6b 01 b7 0c 05 00 00 41 c3 b7 46 a2 22 6b 01 b8 0c 05 00 00 b7 46 a2 "
          "22 6b 27 34 01 b9 0c 05 00 00 b7 46 a2 22 6b 01 ba 0c 05 02 77 b2 00 b7 46 a2 "
          "22 6b 01 bb 0c 05 02 00 b7 46 a2 22 e2 62 6b 01 bc 0c 05 02 00 b7 46 a2 22 6b "
          "01 bd 0c 05 a2 07 00 00 b7 46 a2 22 6b 01 be 0c 05 00 00 b7 46 a2 d4 cc 22 6b "
          "01 bf 0c 05 00 00 b7 46 a2 22 6b 01 c0 0c f5 4c 05 01 00 b7 46 a2 22 6b 01 c1 "
          "0c 05 01 00 b7 46 96 44 a2 22 6b 01 c2 0c 05 02 00 b7 46 a2 22 6b 01 c3 83 11 "
          "0c 05 00 00 b7 46 a2 22 6b 01 c4 0c 05 00 00 b7 f0 13 46 a2 22 6b 01 c5 0c 05 "
          "00 00 b7 46 a2 22 6b 01 19 5e fc 0c 05 00 00 24 47 a2 22 6b b8 ff" },
  
        { "05 64 ff 44 01 00 01 00 65 34",
          "24 01 fd 0c 05 00 00 24 47 a2 22 6b 01 fe 0c 05 e3 0b 00 00 24 47 a2 22 6b 01 "
          "ff 0c 05 00 00 24 47 a2 fb c4 22 6b 01 00 0d 05 00 00 24 47 a2 22 6b 01 01 0d "
          "02 8f 05 00 00 24 47 a2 22 6b 01 02 0d 05 01 00 24 47 d8 03 a2 22 6b 01 03 0d "
          "05 01 00 24 47 a2 22 6b 01 04 3a 70 0d 05 01 00 24 47 a2 22 6b 01 05 0d 05 00 "
          "00 24 1a bf 47 a2 22 6b 01 06 0d 05 00 00 24 47 a2 22 6b 01 a4 c5 07 0d 05 00 "
          "00 24 47 a2 22 6b 01 08 0d 05 02 00 49 2f 25 47 a2 22 6b 01 09 0d 05 02 00 25 "
          "47 a2 22 6b 3f fe 01 0a 0d 05 02 00 25 47 a2 22 6b 01 0b 0d 05 00 56 cd 00 25 "
          "47 a2 22 6b 01 0c 0d 05 00 00 25 47 a2 22 14 26 6b 01 0d 0d 05 00 00 25 47 a2 "
          "22 6b 01 0e 0d 05 b4 ac 02 00 25 47 a2 22 6b 01 0f 0d 05 02 00 25 47 a2 fb 8b "
          "22 6b 01 10 0d 05 02 00 25 47 a2 22 6b 01 11 0d d1 4b 05 00 00 25 47 a2 22 6b "
          "01 12 0d 05 00 00 25 47 81 f6 a2 22 6b 01 13 0d 05 00 00 25 10 9b" },
  
        { "05 64 d1 44 01 00 01 00 e2 34",
          "a5 47 a2 22 6b 01 ec 0d 05 02 00 26 47 a2 22 6b 1d 2a 01 ed 0d 05 01 00 26 47 "
          "a2 22 6b 01 ee 0d 05 02 08 96 00 26 47 a2 22 6b 01 ef 0d 05 00 00 26 47 a2 22 "
          "ea 49 6b 01 f0 0d 05 00 00 26 47 a2 22 6b 01 f1 0d 05 85 b1 00 00 26 47 a2 22 "
          "6b 01 f2 0d 05 02 00 26 47 a2 cf 6d 22 6b 01 f3 0d 05 02 00 26 47 a2 22 6b 01 "
          "f4 0d c6 90 05 02 00 26 47 a2 22 6b 01 f5 0d 05 00 00 26 47 25 44 a2 22 6b 01 "
          "f6 0d 05 00 00 26 47 a2 22 6b 01 f7 45 2e 0d 05 01 00 26 47 a2 22 6b 01 f8 0d "
          "05 01 00 27 1f 3c 47 a2 22 6b 01 f9 0d 05 01 00 27 47 a2 22 6b 01 42 0c fa 0d "
          "05 01 00 27 47 a2 22 6b 01 fb 0d 05 00 00 4d 97 27 47 a2 22 6b 01 fc 0d 05 00 "
          "00 27 47 a2 22 6b 4c a4 01 fd 0d 05 00 00 27 47 a2 22 6b 01 3e b3" },

        { "05 64 0a 44 01 00 01 00 51 fa",
          "e6 47 81 02 00 86 fb" } };

    unsigned packet_num = 0;

    for( const auto packet : inbounds )
    {
        const auto &header = packet.first;
        const auto &body = packet.second;

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

            const bool last_packet = (++packet_num == inbounds.size());

            BOOST_CHECK_EQUAL(last_packet, dev.isTransactionComplete());
        }
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
        "8k DNP test / Internal indications:"
        "\nClass 1 data available"
        "\n"
        "\n8k DNP test / Point data report:"
        "\nAI:   742; AO:     0; DI:     0; DO:     0; Counters:     0; "
        "\nFirst/Last 5 points of each type returned:"
        "\nAnalog inputs:"
        "\n[133:2, 134:2, 135:2, 136:1, 137:1,  ... 3758:3, 3759:3, 3760:0, 3761:0, 3762:0]"
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

