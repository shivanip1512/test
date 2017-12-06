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

    config.insertValue(Cti::Config::DNPStrings::internalRetries,              "3");
    config.insertValue(Cti::Config::DNPStrings::timeOffset,                   "LOCAL");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs,           "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest,              "false");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1,      "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2,      "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3,      "true");
    config.insertValue(Cti::Config::DNPStrings::enableNonUpdatedOnFailedScan, "false");

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

BOOST_AUTO_TEST_CASE(test_dev_dnp_control_child_device_success)
{
    //  set up the config
    Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    config.insertValue(Cti::Config::DNPStrings::internalRetries, "3");
    config.insertValue(Cti::Config::DNPStrings::timeOffset, "LOCAL");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs, "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest, "false");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3, "true");
    config.insertValue(Cti::Config::DNPStrings::enableNonUpdatedOnFailedScan, "false");

    struct : CtiPointManager
    {
        long lastPointId{}, lastPaoId{}, lastControlOffset{};

        ptr_type getPoint(const long pointId, const long paoId) override
        {
            lastControlOffset = 0;
            lastPaoId = paoId;
            lastPointId = pointId;
            return ptr_type{ Cti::Test::makeControlPoint(paoId, pointId, 47, 477, ControlType_Normal) };
        }
        ptr_type getControlOffsetEqual(const long paoId, const int controlOffset) override
        {
            lastControlOffset = controlOffset;
            lastPaoId = paoId;
            lastPointId = 0;
            return ptr_type{ Cti::Test::makeControlPoint(paoId, paoId * 1000 + controlOffset, controlOffset * 2, controlOffset, ControlType_Normal) };
        }
    } pt_mgr;

    {
        test_DnpDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setChildDevices(std::set<long>{1620, 1776});
        dev.setPointManager(&pt_mgr);

        CtiCommandParser parse("control close offset 1997");
        request.setOptionsField(1620);

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(pt_mgr.lastControlOffset, 1997);
        BOOST_CHECK_EQUAL(pt_mgr.lastPaoId, 1620);
        BOOST_CHECK_EQUAL(pt_mgr.lastPointId, 0);

        BOOST_REQUIRE_EQUAL(outList.size(), 1);
        BOOST_CHECK_EQUAL(vgList.size(), 1);  //  LMControlHist msg
        BOOST_CHECK(retList.empty());

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

        delete_container(outList);  outList.clear();
        delete_container(vgList);   vgList.clear();

        CtiXfer xfer;

        {
            BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

            BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
            BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

            //  cc 07 = 1996
            const byte_str expected(
                "05 64 1a c4 d2 04 01 00 d0 22 "
                "c0 c1 05 0c 01 28 01 00 cc 07 41 01 c8 01 00 00 02 93 "
                "00 00 00 00 00 ff ff");

            //  copy them into int vectors so they display nicely
            const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

            BOOST_CHECK_EQUAL_RANGES(expected, output);
        }
    }

    {
        test_DnpDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setChildDevices(std::set<long>{1620, 1776});
        dev.setPointManager(&pt_mgr);

        CtiCommandParser parse("control close select pointid 2001");
        request.setOptionsField(1776);

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(pt_mgr.lastControlOffset, 0);
        BOOST_CHECK_EQUAL(pt_mgr.lastPaoId, 1776);
        BOOST_CHECK_EQUAL(pt_mgr.lastPointId, 2001);

        BOOST_REQUIRE_EQUAL(outList.size(), 1);
        BOOST_CHECK_EQUAL(vgList.size(), 1);  //  LMControlHist msg
        BOOST_CHECK(retList.empty());

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

        delete_container(outList);  outList.clear();
        delete_container(vgList);   vgList.clear();

        CtiXfer xfer;

        {
            BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

            BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
            BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

            //  dc 01 = 476
            const byte_str expected(
                "05 64 1a c4 d2 04 01 00 d0 22 "
                "c0 c1 05 0c 01 28 01 00 dc 01 41 01 c8 01 00 00 6e 31 "
                "00 00 00 00 00 ff ff");

            //  copy them into int vectors so they display nicely
            const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

            BOOST_CHECK_EQUAL_RANGES(expected, output);
        }
    }
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_putvalue_analog_child_device_success)
{
    //  set up the config
    Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    config.insertValue(Cti::Config::DNPStrings::internalRetries, "3");
    config.insertValue(Cti::Config::DNPStrings::timeOffset, "LOCAL");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs, "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest, "false");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3, "true");
    config.insertValue(Cti::Config::DNPStrings::enableNonUpdatedOnFailedScan, "false");

    struct : CtiPointManager
    {
        long lastPointId{}, lastPaoId{};

        ptr_type getPoint(const long pointId, const long paoId) override
        {
            lastPaoId = paoId;
            lastPointId = pointId;
            return ptr_type{ Cti::Test::makeAnalogPoint(paoId, pointId, 10047) };
        }
    } pt_mgr;

    {
        test_DnpDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setChildDevices(std::set<long>{1620, 1776});
        dev.setPointManager(&pt_mgr);

        CtiCommandParser parse("putvalue analog value 1792 select pointid 9876");
        request.setOptionsField(1620);

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(pt_mgr.lastPaoId, 1620);
        BOOST_CHECK_EQUAL(pt_mgr.lastPointId, 9876);

        BOOST_REQUIRE_EQUAL(outList.size(), 1);
        BOOST_CHECK(vgList.empty());
        BOOST_CHECK(retList.empty());

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.recvCommRequest(outList.front()));

        delete_container(outList);  outList.clear();

        CtiXfer xfer;

        {
            BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

            BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
            BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

            //  2e = 46, from 10047 above
            const byte_str expected(
                "05 64 12 c4 d2 04 01 00 0c b8 "
                "c0 c1 05 29 02 28 01 00 2e 00 00 07 00 52 a6");

            //  copy them into int vectors so they display nicely
            const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

            BOOST_CHECK_EQUAL_RANGES(expected, output);
        }
    }
}

BOOST_AUTO_TEST_CASE(test_dev_dnp_control_child_device_fail)
{
    struct : CtiPointManager
    {
        long lastPointId{}, lastPaoId{}, lastControlOffset{};

        ptr_type getPoint(const long pointId, const long paoId) override
        {
            lastControlOffset = 0;
            lastPaoId = paoId;
            lastPointId = pointId;
            return nullptr;
        }
        ptr_type getControlOffsetEqual(const long paoId, const int controlOffset) override
        {
            lastControlOffset = controlOffset;
            lastPaoId = paoId;
            lastPointId = 0;
            return nullptr;
        }
    } pt_mgr;

    {
        test_DnpDevice dev;

        dev.setID(1792, test_tag);
        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setPointManager(&pt_mgr);
        dev.setChildDevices(std::set<long>{1776});

        CtiCommandParser parse("control close select pointid 10");
        request.setOptionsField(9999);

        BOOST_CHECK_EQUAL(ClientErrors::ChildDeviceUnknown, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        //  never even tried
        BOOST_CHECK_EQUAL(pt_mgr.lastControlOffset, 0);
        BOOST_CHECK_EQUAL(pt_mgr.lastPaoId, 0);
        BOOST_CHECK_EQUAL(pt_mgr.lastPointId, 0);

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);
        
        const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::ChildDeviceUnknown);

        BOOST_CHECK_EQUAL(
            retMsg->ResultString(),
            "Test DNP device / Unknown child device ID 9999");
    }
    delete_container(retList);  retList.clear();
    {
        test_DnpDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setPointManager(&pt_mgr);
        dev.setChildDevices(std::set<long>{1776});

        CtiCommandParser parse("control close select pointid 10");
        request.setOptionsField(1776);

        BOOST_CHECK_EQUAL(ClientErrors::PointLookupFailed, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(pt_mgr.lastControlOffset, 0);
        BOOST_CHECK_EQUAL(pt_mgr.lastPaoId, 1776);
        BOOST_CHECK_EQUAL(pt_mgr.lastPointId, 10);

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);
        
        const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::PointLookupFailed);

        BOOST_CHECK_EQUAL(
            retMsg->ResultString(),
            "Test DNP device / The specified point is not on device Test DNP device");
    }
}

BOOST_AUTO_TEST_CASE(test_integrity_scan)
{
    test_DnpDevice dev;

    dev._name = "Test DNP device";
    dev._dnp.setAddresses(39, 1020);
    dev._dnp.setName("Test DNP device");

    //  set up the config
    Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    config.insertValue(Cti::Config::DNPStrings::internalRetries,              "3");
    config.insertValue(Cti::Config::DNPStrings::timeOffset,                   "UTC");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs,           "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest,              "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1,      "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2,      "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3,      "true");
    config.insertValue(Cti::Config::DNPStrings::enableNonUpdatedOnFailedScan, "true");

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

BOOST_AUTO_TEST_CASE(test_processPoints_children)
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
        void getEqualByPAO(const long pao, std::vector<ptr_type>& points) override
        {
            if( pao == 1812 )
            {
                points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 1717, 17) });
                points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 1919, 19) });
            }
        }
    } pt_mgr;

    dev.setPointManager(&pt_mgr);
    dev.setChildDevices(std::set<long>{1812});

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

BOOST_AUTO_TEST_SUITE_END()

