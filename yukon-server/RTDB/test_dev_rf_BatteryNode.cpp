#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rf_BatteryNode.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"


struct test_state_rf_BatteryNode
{
    CtiRequestMsg request;
    Cti::Devices::RfnDevice::ReturnMsgList  returnMsgs;
    Cti::Devices::RfnDevice::RequestMsgList requestMsgs;
    Cti::Devices::RfnDevice::RfnCommandList rfnRequests;
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;
    Cti::ConnectionHandle connectionHandle { 74 };
    static constexpr int userMessageId = 876512;

    test_state_rf_BatteryNode() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
        request.setConnectionHandle(connectionHandle);
        request.setUserMessageId(userMessageId);
    }
};


namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char>& v);
}

const CtiTime execute_time(CtiDate(27, 8, 2013), 15);
const CtiTime decode_time(CtiDate(27, 8, 2013), 16);

BOOST_FIXTURE_TEST_SUITE(test_dev_rf_BatteryNode, test_state_rf_BatteryNode)

BOOST_AUTO_TEST_CASE(test_getconfig_install_all_issues_verify)
{
    Cti::Test::test_RfBatteryNodeDevice dev("Test battery device");

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 60;
    msg.reportingInterval = 360;

    dev.channelConfigReplyMsg = msg;

    Cti::Test::test_DeviceConfig& cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    {
        CtiCommandParser parse("getconfig install all");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.ExecuteRequest(&request, parse, returnMsgs, requestMsgs, rfnRequests));
    }

    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK(rfnRequests.empty());
    BOOST_REQUIRE_EQUAL(1, requestMsgs.size());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), false);  //  this will gets overridden to "true" by Pil due to the requestMsg below
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }

    {
        const auto& requestMsg = *requestMsgs.front();

        BOOST_CHECK_EQUAL(requestMsg.CommandString(), "putconfig install all verify");
        BOOST_CHECK_EQUAL(requestMsg.getConnectionHandle().getConnectionId(), connectionHandle.getConnectionId());
        BOOST_CHECK_EQUAL(requestMsg.UserMessageId(), userMessageId);
    }
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_all_issues_verify)
{
    Cti::Test::test_RfBatteryNodeDevice dev("Test battery device");

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 60;
    msg.reportingInterval = 360;

    dev.channelConfigReplyMsg = msg;

    Cti::Test::test_DeviceConfig& cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    {
        CtiCommandParser parse("getconfig install all");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.ExecuteRequest(&request, parse, returnMsgs, requestMsgs, rfnRequests));
    }

    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK(rfnRequests.empty());
    BOOST_REQUIRE_EQUAL(1, requestMsgs.size());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), false);  //  will be overridden by Pil::RequestExecuter::execute(RfnDevice) due to the additional requestMsg
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }

    {
        const auto& requestMsg = *requestMsgs.front();

        BOOST_CHECK_EQUAL(requestMsg.CommandString(), "putconfig install all verify");
        BOOST_CHECK_EQUAL(requestMsg.getConnectionHandle().getConnectionId(), connectionHandle.getConnectionId());
        BOOST_CHECK_EQUAL(requestMsg.UserMessageId(), userMessageId);
    }
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_all_verify_mismatch)
{
    Cti::Test::test_RfBatteryNodeDevice dev("Test battery device");

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 60;
    msg.reportingInterval = 360;

    dev.channelConfigReplyMsg = msg;

    Cti::Test::test_DeviceConfig& cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    {
        CtiCommandParser parse("putconfig install all verify");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.ExecuteRequest(&request, parse, returnMsgs, requestMsgs, rfnRequests));
    }

    BOOST_REQUIRE_EQUAL(3, returnMsgs.size());
    BOOST_CHECK(rfnRequests.empty());
    BOOST_CHECK(requestMsgs.empty());

    auto retMsg_itr = returnMsgs.begin();

    {
        const auto& returnMsg = *(*retMsg_itr++);

        BOOST_CHECK_EQUAL(returnMsg.Status(), 219);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Config Reporting Interval did not match. Config: 86400, Meter: 360");
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), true);
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }
    {
        const auto& returnMsg = *(*retMsg_itr++);

        BOOST_CHECK_EQUAL(returnMsg.Status(), 219);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Config Recording Interval did not match. Config: 1800, Meter: 60");
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), true);
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }
    {
        const auto& returnMsg = *(*retMsg_itr++);

        BOOST_CHECK_EQUAL(returnMsg.Status(), 219);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Config all is NOT current.");
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), false);
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_all_verify_match)
{
    Cti::Test::test_RfBatteryNodeDevice dev("Test battery device");

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::SUCCESS;
    msg.recordingInterval = 1800;
    msg.reportingInterval = 86400;

    dev.channelConfigReplyMsg = msg;

    Cti::Test::test_DeviceConfig& cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    {
        CtiCommandParser parse("putconfig install all verify");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.ExecuteRequest(&request, parse, returnMsgs, requestMsgs, rfnRequests));
    }

    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK(rfnRequests.empty());
    BOOST_CHECK(requestMsgs.empty());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Config all is current.");
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), false);
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_all_failure_issues_verify)
{
    Cti::Test::test_RfBatteryNodeDevice dev("Test battery device");

    Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage msg;
    msg.rfnIdentifier.manufacturer = "Croctober";
    msg.rfnIdentifier.model = "7";
    msg.rfnIdentifier.serialNumber = "2020";
    msg.replyCode = Cti::Messaging::Rfn::RfnGetChannelConfigReplyMessage::FAILURE;
    msg.recordingInterval = 3600;
    msg.reportingInterval = 86400;

    dev.channelConfigReplyMsg = msg;
    dev.channelConfigResultCode = ClientErrors::NetworkManagerTimeout;

    Cti::Test::test_DeviceConfig& cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds, "86400");
    cfg.insertValue(Cti::Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds, "1800");

    {
        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.ExecuteRequest(&request, parse, returnMsgs, requestMsgs, rfnRequests));
    }

    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK(rfnRequests.empty());
    BOOST_REQUIRE_EQUAL(1, requestMsgs.size());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 293);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Test battery device: Did not receive a response from Network Manager.");
        BOOST_CHECK_EQUAL(returnMsg.ExpectMore(), false);
        BOOST_CHECK_EQUAL(returnMsg.UserMessageId(), userMessageId);
    }
    {
        const auto& requestMsg = *requestMsgs.front();

        BOOST_CHECK_EQUAL(requestMsg.CommandString(), "putconfig install all verify");
        BOOST_CHECK_EQUAL(requestMsg.getConnectionHandle().getConnectionId(), connectionHandle.getConnectionId());
        BOOST_CHECK_EQUAL(requestMsg.UserMessageId(), userMessageId);
    }
}

BOOST_AUTO_TEST_SUITE_END()