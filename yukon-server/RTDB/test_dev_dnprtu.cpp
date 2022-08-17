#include <boost/test/auto_unit_test.hpp>

#include "dev_dnprtu.h"
#include "config_data_dnp.h"
#include "mgr_point.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using Cti::Test::byte_str;

struct test_DnpRtuDevice : Cti::Devices::DnpRtuDevice
{
    using CtiTblPAOLite::_name;
    using DnpDevice::_dnp;

    using DnpDevice::processPoints;
};

BOOST_AUTO_TEST_SUITE( test_dev_dnprtu )

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
        test_DnpRtuDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.addChildDevice(1620);
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
        test_DnpRtuDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.addChildDevice(1776);
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
        test_DnpRtuDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.addChildDevice(1620);
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
        test_DnpRtuDevice dev;

        dev.setID(1792, test_tag);
        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setPointManager(&pt_mgr);
        dev.addChildDevice(1776);

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
        test_DnpRtuDevice dev;

        dev._name = "Test DNP device";
        dev._dnp.setAddresses(1234, 1);
        dev._dnp.setName("Test DNP device");
        dev.setPointManager(&pt_mgr);
        dev.addChildDevice(1776);

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
            "Test DNP device / The specified point is not on the device");
    }
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

    test_DnpRtuDevice dev;

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
    CtiPointDataMsg msg1, msg2, msg3, msg4;
    Cti::Protocols::Interface::pointlist_t points{ &msg1, &msg2, &msg3, &msg4 };

    msg1.setValue(4);
    msg1.setType(AnalogPointType);
    msg1.setId(17);

    msg2.setValue(1);
    msg2.setType(AnalogPointType);
    msg2.setId(19);

    msg3.setValue(8);
    msg3.setType(AnalogPointType);
    msg3.setId(23);

    msg4.setValue(3);
    msg4.setType(AnalogPointType);
    msg4.setId(29);

    test_DnpRtuDevice dev;

    struct : CtiPointManager
    {
        void getEqualByPAO(const long pao, std::vector<ptr_type>& points) override
        {
            if( pao == 1812 )
            {
                points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 1717, 17) });
                points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 1919, 19) });
            }
            if( pao == 1917 )
            {
                points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 2323, 23) });
                points.emplace_back(ptr_type{ Cti::Test::makeAnalogPoint(pao, 2929, 29) });
            }
        }
    } pt_mgr;

    dev.setPointManager(&pt_mgr);
    dev.addChildDevice(1812);
    dev.addChildDevice(1917);

    dev.processPoints(points);

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    BOOST_CHECK_EQUAL(points[0]->getValue(), 4);
    BOOST_CHECK_EQUAL(points[0]->getType(), AnalogPointType);
    BOOST_CHECK_EQUAL(points[0]->getId(), 1717);

    BOOST_CHECK_EQUAL(points[1]->getValue(), 1);
    BOOST_CHECK_EQUAL(points[1]->getType(), AnalogPointType);
    BOOST_CHECK_EQUAL(points[1]->getId(), 1919);

    BOOST_CHECK_EQUAL(points[2]->getValue(), 8);
    BOOST_CHECK_EQUAL(points[2]->getType(), AnalogPointType);
    BOOST_CHECK_EQUAL(points[2]->getId(), 2323);

    BOOST_CHECK_EQUAL(points[3]->getValue(), 3);
    BOOST_CHECK_EQUAL(points[3]->getType(), AnalogPointType);
    BOOST_CHECK_EQUAL(points[3]->getId(), 2929);
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

