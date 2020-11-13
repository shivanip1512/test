#include <boost/test/unit_test.hpp>

#include "pilserver.h"
#include "msg_pcreturn.h"
#include "dev_rfn.h"
#include "cmd_rfn_demandFreeze.h"
#include "config_data_rfn.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_pilserver )

using namespace std;

struct Test_PilServer : Cti::Pil::PilServer
{
    Cti::Test::test_DeviceManager dev_mgr;
    Cti::Test::test_PointManager  pt_mgr;
    Cti::Test::test_RouteManager  rte_mgr;

    Test_PilServer() :
        PilServer(dev_mgr, pt_mgr, rte_mgr)
    {}

    using PilServer::handleRfnDeviceResult;
    using PilServer::handleInMessageResult;
    using PilServer::analyzeWhiteRabbits;
    using RequestQueue = PilServer::RequestQueue;

    std::vector<long> getDeviceGroupMembers(std::string groupname) const override
    {
        if( groupname == "/pi" )
        {
            return { 3,1,4,1,5,9,2,6,5,3 };
        }
        if( groupname == "/meters/collection/testing old-style group" )
        {
            return { 1,2,3,4,5 };
        }
        if( groupname == "/meters/alternate/testing old-style alternate group" )
        {
            return { 8,7,6,5,4 };
        }
        if( groupname == "/meters/billing/testing old-style billing group" )
        {
            return { 10,11,12,13,14 };
        }

        return {};
    }

    std::vector<std::unique_ptr<CtiMessage>> vgList, retList;
    std::vector<std::unique_ptr<OUTMESS>> outList;

    void sendResults(CtiDeviceBase::CtiMessageList &vgList_, CtiDeviceBase::CtiMessageList &retList_, const int priority, Cti::ConnectionHandle connectionHandle) override
    {
        for( auto msg : vgList_ )
        {
            vgList.emplace_back(msg);
        }

        for( auto msg : retList_ )
        {
            retList.emplace_back(msg);
        }
    }

    void sendRequests(CtiDeviceBase::CtiMessageList& vgList_, CtiDeviceBase::CtiMessageList& retList_, CtiDeviceBase::OutMessageList& outList_, Cti::ConnectionHandle connectionHandle) override
    {
        for( auto msg : vgList_ )
        {
            vgList.emplace_back(msg);
        }

        for( auto msg : retList_ )
        {
            retList.emplace_back(msg);
        }

        for( auto msg : outList_ )
        {
            outList.emplace_back(msg);
        }
    }
};

struct pilEnvironment
{
    Test_PilServer testPilServer;

    Test_PilServer::RequestQueue execList;
    std::list<CtiMessage *> retList;
    Test_PilServer::RequestQueue groupRequests;

    ~pilEnvironment()
    {
        delete_container(retList);
    }
};


BOOST_AUTO_TEST_CASE(test_handleRfnDeviceResult)
{
    Test_PilServer pilServer;

    CtiDeviceManager::ptr_type dev = pilServer.dev_mgr.getDeviceByID(123);

    BOOST_REQUIRE(dev);

    CtiDeviceSingle *devSingle = dynamic_cast<CtiDeviceSingle *>(dev.get());

    BOOST_REQUIRE(devSingle);

    const Cti::ConnectionHandle handle{ 55441 };

    devSingle->incrementGroupMessageCount(11235, handle, 2);

    BOOST_CHECK_EQUAL(2, devSingle->getGroupMessageCount(11235, handle));

    Cti::Pil::RfnDeviceRequest::Parameters parameters;

    parameters.deviceId         = 123;
    parameters.userMessageId    = 11235;
    parameters.connectionHandle = handle;

    pilServer.handleRfnDeviceResult({
        { parameters, 9999, std::make_unique<Cti::Devices::Commands::RfnImmediateDemandFreezeCommand>() },
        { { "This was a triumph. I'm making a note here: HUGE SUCCESS." } }});

    BOOST_REQUIRE_EQUAL(1, pilServer.retList.size());

    {
        auto retMsg = dynamic_cast<CtiReturnMsg*>(pilServer.retList.front().get());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->ResultString(), "This was a triumph. I'm making a note here: HUGE SUCCESS.");
        BOOST_CHECK(retMsg->ExpectMore());
    }

    BOOST_CHECK_EQUAL(1, devSingle->getGroupMessageCount(11235, handle));
}


BOOST_AUTO_TEST_CASE(test_handleInMessageResult_getconfig_install_all_mct410)
{
    Test_PilServer pilServer;

    constexpr auto DeviceId = 502;
    constexpr auto UserMessageId = 11235;
    const Cti::ConnectionHandle handle { 55441 };

    auto devBase = pilServer.dev_mgr.getDeviceByID(DeviceId);
    auto dev = dynamic_cast<CtiDeviceSingle*>(devBase.get());

    BOOST_REQUIRE(dev);
    
    auto config = boost::make_shared<Cti::Test::test_DeviceConfig>();
    Cti::Test::Override_ConfigManager overrideConfigManager(config);

    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;

    config->insertValue("disconnectMode", "DEMAND_THRESHOLD");
    config->insertValue("disconnectDemandThreshold", "2.71");
    config->insertValue("disconnectLoadLimitConnectDelay", "4");
    config->insertValue("disconnectMinutes", "7");
    config->insertValue("connectMinutes", "17");
    config->insertValue("reconnectParam", "ARM");
    config->insertValue("demandFreezeDay", "12");
    config->insertValue("timeZoneOffset", "CHICAGO");

    CtiRequestMsg reqMsg(DeviceId, "getconfig install all", UserMessageId);
    reqMsg.setConnectionHandle(handle);
    reqMsg.setSOE(97);  //  prevents us from attempting DB access by calling SystemLogIdGen()

    pilServer.executeRequest(&reqMsg);

    BOOST_REQUIRE_EQUAL(3, pilServer.retList.size());
    BOOST_CHECK(pilServer.vgList.empty());
    BOOST_REQUIRE_EQUAL(3, pilServer.outList.size());

    BOOST_CHECK_EQUAL(dev->getGroupMessageCount(UserMessageId, handle), 3);

    {
        auto retList_itr = pilServer.retList.cbegin();
        {
            auto retMsg = dynamic_cast<CtiReturnMsg*>((*retList_itr++).get());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(retMsg->ResultString(), "Emetcon DLC command sent on route ");
            BOOST_CHECK_EQUAL(retMsg->Status(), 0);
            BOOST_CHECK(retMsg->ExpectMore());
        }
        {
            auto retMsg = dynamic_cast<CtiReturnMsg*>((*retList_itr++).get());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(retMsg->ResultString(), "Emetcon DLC command sent on route ");
            BOOST_CHECK_EQUAL(retMsg->Status(), 0);
            BOOST_CHECK(retMsg->ExpectMore());
        }
        {
            auto retMsg = dynamic_cast<CtiReturnMsg*>((*retList_itr++).get());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(retMsg->ResultString(), "Emetcon DLC command sent on route ");
            BOOST_CHECK_EQUAL(retMsg->Status(), 0);
            BOOST_CHECK(retMsg->ExpectMore());
        }
        pilServer.retList.clear();
    }

    auto outList_itr = pilServer.outList.cbegin();

    {
        auto outmess = (*outList_itr++).get();

        BOOST_REQUIRE(outmess);

        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
        BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xfe);
        BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

        INMESS im;
        OutEchoToIN(*outmess, im);

        const auto data = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        boost::copy(data, stdext::make_checked_array_iterator(im.Buffer.DSt.Message, DSTRUCT::MessageLength_Max));
        im.Buffer.DSt.Length = data.size();

        pilServer.handleInMessageResult(im);

        BOOST_CHECK_EQUAL(dev->getGroupMessageCount(UserMessageId, handle), 2);
    }
    {
        auto outmess = (*outList_itr++).get();

        BOOST_REQUIRE(outmess);

        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
        BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x4f);
        BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

        INMESS im;
        OutEchoToIN(*outmess, im);

        im.Buffer.DSt.Message[0] = 0;
        im.Buffer.DSt.Length = 1;

        pilServer.handleInMessageResult(im);

        BOOST_CHECK_EQUAL(dev->getGroupMessageCount(UserMessageId, handle), 2);
    }
    {
        auto outmess = (*outList_itr++).get();

        BOOST_REQUIRE(outmess);

        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
        BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
        BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x3f);
        BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

        INMESS im;
        OutEchoToIN(*outmess, im);

        im.Buffer.DSt.Message[0] = 0;
        im.Buffer.DSt.Length = 1;

        pilServer.handleInMessageResult(im);

        BOOST_CHECK_EQUAL(dev->getGroupMessageCount(UserMessageId, handle), 1);
    }

    BOOST_REQUIRE_EQUAL(pilServer.retList.size(), 3);

    {
        auto retList_itr = pilServer.retList.cbegin();

        {
            auto retMsg = dynamic_cast<CtiReturnMsg*>((*retList_itr++).get());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(retMsg->ResultString(), "MCT-410fL (502) / "
                "\nConfig data received: 00 00 00 00 00 00 00 00 00 00 00 00 00");
            BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
        }
        {
            auto retMsg = dynamic_cast<CtiReturnMsg*>((*retList_itr++).get());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(retMsg->ResultString(), "MCT-410fL (502) / Scheduled day of freeze: (disabled)\n");
            BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
        }
        {
            auto retMsg = dynamic_cast<CtiReturnMsg*>((*retList_itr++).get());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config data received: 00");
            BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
        }
    }
}


BOOST_AUTO_TEST_CASE(test_rfnExpectMore)
{
    Test_PilServer pilServer;

    constexpr auto DeviceId = 501;
    constexpr auto UserMessageId = 11235;
    const Cti::ConnectionHandle handle { 55441 };

    auto dev = pilServer.dev_mgr.getDeviceByID(DeviceId);

    BOOST_REQUIRE(dev);

    auto devRfBatteryNode = dynamic_cast<Cti::Test::test_RfBatteryNodeDevice*>(dev.get());

    BOOST_REQUIRE(devRfBatteryNode);

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 60;
    msg.reportingInterval = 360;

    devRfBatteryNode->channelConfigReplyMsg = msg;

    CtiRequestMsg reqMsg(DeviceId, "getconfig install all", UserMessageId);
    reqMsg.setConnectionHandle(handle);
    reqMsg.setSOE(97);  //  prevents us from attempting DB access by calling SystemLogIdGen()

    pilServer.executeRequest(&reqMsg);

    BOOST_REQUIRE_EQUAL(2, pilServer.retList.size());
    auto retList_itr = pilServer.retList.cbegin();

    {
        auto retMsg = dynamic_cast<const CtiReturnMsg*>(retList_itr++->get());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK(retMsg->ExpectMore());
    }
    {
        auto reqMsg = dynamic_cast<const CtiRequestMsg*>(retList_itr++->get());

        BOOST_REQUIRE(reqMsg);

        BOOST_CHECK_EQUAL(reqMsg->CommandString(), "putconfig install all verify");
        BOOST_CHECK(reqMsg->getConnectionHandle(), handle);
        BOOST_CHECK(reqMsg->UserMessageId(), UserMessageId);
    }
}


BOOST_AUTO_TEST_CASE(test_rfnBatteryNode_verifyFailure)
{
    Test_PilServer pilServer;
    auto cfg = boost::make_shared<Cti::Test::test_DeviceConfig>();
    Cti::Test::Override_ConfigManager overrideConfigManager { cfg };

    cfg->insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg->insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    constexpr auto DeviceId = 501;
    constexpr auto UserMessageId = 11235;
    const Cti::ConnectionHandle handle{ 55441 };

    auto dev = pilServer.dev_mgr.getDeviceByID(DeviceId);

    BOOST_REQUIRE(dev);

    auto devRfBatteryNode = dynamic_cast<Cti::Test::test_RfBatteryNodeDevice*>(dev.get());

    BOOST_REQUIRE(devRfBatteryNode);

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 60;
    msg.reportingInterval = 360;

    devRfBatteryNode->channelConfigReplyMsg = msg;

    CtiRequestMsg reqMsg(DeviceId, "putconfig install all verify", UserMessageId);
    reqMsg.setConnectionHandle(handle);
    reqMsg.setSOE(97);  //  prevents us from attempting DB access by calling SystemLogIdGen()

    pilServer.executeRequest(&reqMsg);

    BOOST_REQUIRE_EQUAL(3, pilServer.retList.size());
    auto retList_itr = pilServer.retList.cbegin();

    {
        auto retMsg = dynamic_cast<const CtiReturnMsg*>(retList_itr++->get());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
        BOOST_CHECK_EQUAL(retMsg->Status(), 219);
        BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config Reporting Interval did not match. Config: 86400, Meter: 360");
    }
    {
        auto retMsg = dynamic_cast<const CtiReturnMsg*>(retList_itr++->get());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
        BOOST_CHECK_EQUAL(retMsg->Status(), 219);
        BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config Recording Interval did not match. Config: 1800, Meter: 60");
    }
    {
        auto retMsg = dynamic_cast<const CtiReturnMsg*>(retList_itr++->get());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
        BOOST_CHECK_EQUAL(retMsg->Status(), 219);
        BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config all is NOT current.");
    }
}


BOOST_AUTO_TEST_CASE(test_rfnBatteryNode_verifySuccess)
{
    Test_PilServer pilServer;
    auto cfg = boost::make_shared<Cti::Test::test_DeviceConfig>();
    Cti::Test::Override_ConfigManager overrideConfigManager{ cfg };

    cfg->insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg->insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    constexpr auto DeviceId = 501;
    constexpr auto UserMessageId = 11235;
    const Cti::ConnectionHandle handle{ 55441 };

    auto dev = pilServer.dev_mgr.getDeviceByID(DeviceId);

    BOOST_REQUIRE(dev);

    auto devRfBatteryNode = dynamic_cast<Cti::Test::test_RfBatteryNodeDevice*>(dev.get());

    BOOST_REQUIRE(devRfBatteryNode);

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 1800;
    msg.reportingInterval = 86400;

    devRfBatteryNode->channelConfigReplyMsg = msg;

    CtiRequestMsg reqMsg(DeviceId, "putconfig install all verify", UserMessageId);
    reqMsg.setConnectionHandle(handle);
    reqMsg.setSOE(97);  //  prevents us from attempting DB access by calling SystemLogIdGen()

    pilServer.executeRequest(&reqMsg);

    BOOST_REQUIRE_EQUAL(1, pilServer.retList.size());
    auto retList_itr = pilServer.retList.cbegin();

    {
        auto retMsg = dynamic_cast<const CtiReturnMsg*>(retList_itr++->get());

        BOOST_REQUIRE(retMsg);
        
        BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
        BOOST_CHECK_EQUAL(retMsg->Status(), 0);
        BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config all is current.");
    }
}


BOOST_FIXTURE_TEST_SUITE(test_AnalyzeWhiteRabbits, pilEnvironment)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_no_op)
{
    CtiRequestMsg msg(0, "nonsense here");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_device_id)
{
    CtiRequestMsg msg(0, "nonsense here select deviceid 3");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select deviceid 3");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 3);
}

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_device_name)
{
    CtiRequestMsg msg(0, "nonsense here select name 'beetlebrox'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select name 'beetlebrox'");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 42);
}

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_device_name_not_found)
{
    CtiRequestMsg msg(0, "nonsense here select name 'zaphod'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(execList.empty());

    BOOST_CHECK_EQUAL(retList.size(), 1);

    CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::IdNotFound);
    BOOST_CHECK_EQUAL(retMsg->ResultString(), "No device with name 'zaphod' exists in the database.");
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_route_id)
{
    CtiRequestMsg msg(0, "nonsense here select route id 3");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select route id 3");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
    BOOST_CHECK_EQUAL(reqMsg->RouteId(), 3);
}

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_route_name)
{
    CtiRequestMsg msg(0, "nonsense here select route name 'sixty-six'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select route name 'sixty-six'");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
    BOOST_CHECK_EQUAL(reqMsg->RouteId(), 84);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_route_name_not_found)
{
    CtiRequestMsg msg(0, "nonsense here select route name 'sixty-seven'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select route name 'sixty-seven'");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
    BOOST_CHECK_EQUAL(reqMsg->RouteId(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_group_fail)
{
    CtiRequestMsg msg(0, "nonsense here select group '/fiddlesticks'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::IdNotFound);
    BOOST_CHECK_EQUAL(retMsg->ResultString(), "Group '/fiddlesticks' found no target devices.");

    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_group_pi)
{
    CtiRequestMsg msg(0, "trancendental numbers select group '/pi'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());
    BOOST_REQUIRE_EQUAL(groupRequests.size(), 10);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 9);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 2);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 6);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_group_old_style)
{
    CtiRequestMsg msg(0, "getvalue kwh select group 'testing old-style group'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());

    BOOST_REQUIRE_EQUAL(groupRequests.size(), 5);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 2);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_altgroup_old_style)
{
    CtiRequestMsg msg(0, "getvalue kwh select altgroup 'testing old-style alternate group'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());

    BOOST_REQUIRE_EQUAL(groupRequests.size(), 5);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 8);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 7);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 6);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_billgroup_old_style)
{
    CtiRequestMsg msg(0, "getvalue kwh select billgroup 'testing old-style billing group'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());

    BOOST_REQUIRE_EQUAL(groupRequests.size(), 5);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 10);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 11);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 12);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 13);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 14);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

