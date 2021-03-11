#include <boost/test/unit_test.hpp>

#include "dev_rfnCommercial.h"
#include "cmd_rfn.h"
#include "cmd_rfn_Aggregate.h"  //  to reset the global counter
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"

#include "deviceconfig_test_helpers.h"
#include "message_test_helpers.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

namespace Cti {
    std::ostream& operator<<(std::ostream& os, const RfnIdentifier rfnId);  //  defined in RTDB/test_main.cpp
}
namespace Cti::Messaging::Rfn {
    std::ostream& operator<<(std::ostream& os, const ProgrammingStatus s) {
        return os << "[ProgrammingStatus " << as_underlying(s) << "]";
    }
}

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnCommercialDevice : RfnCommercialDevice
{
    using CtiDeviceBase::setDeviceType;
    using MpsArchiveMsg = Cti::Messaging::Rfn::MeterProgramStatusArchiveRequestMsg;
    std::vector<MpsArchiveMsg> mpsArchiveMessages;

    bool e2eServerDisabled = false;

    test_RfnCommercialDevice() {
        _rfnId = { "TEST", "RFN", "COMMERCIAL" };
    }

    bool areAggregateCommandsSupported() const override {
        return false;
    }
    bool isE2eServerDisabled() const override {
        return e2eServerDisabled;
    }
    virtual void sendMeterProgramStatusUpdate(Cti::Messaging::Rfn::MeterProgramStatusArchiveRequestMsg msg) {
        mpsArchiveMessages.emplace_back(std::move(msg));
    }
};

struct test_state_rfnCommercial
{
    std::unique_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RequestMsgList    requestMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfnCommercial() :
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
    ostream& operator<<(ostream& out, const vector<bool> &v);
}

namespace test_cmd_rfn_ConfigNotification {
    extern const std::vector<uint8_t> payload;
}


const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );
const CtiTime decode_time ( CtiDate( 27, 8, 2013 ) , 16 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfnCommercial, test_state_rfnCommercial )

BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_immediate_demand_freeze )
{
    test_RfnCommercialDevice    dev;

    CtiCommandParser    parse("putstatus freeze");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        auto& command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp 
            { 0x55, 0x01 };

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_freezeday_reset )
{
    test_RfnCommercialDevice    dev;

    CtiCommandParser    parse("putconfig freezeday reset");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        auto& command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp { 0x55, 0x02, 0x00 };  //  55 = freezeday, 02 = write, 00 = disabled

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_channel_configuration_alpha )
{
    test_RfnCommercialDevice dut;
    dut.setDeviceType(TYPE_RFN430A3K);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    const std::map<std::string, std::string> configItems {
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "5" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "MIDNIGHT" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "RECEIVED_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "MIDNIGHT" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "SUM_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "NET_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_DEMAND" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" },
            { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
            { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" }};

    cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                    "rfnChannelConfiguration",
                    configItems));

    {
        CtiCommandParser parse("putconfig install channelconfig");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
        }

        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );
        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp {
                    0x78, 0x00, 0x01,
                    0x01, 0x00, 0x0b, 0x05, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05 };

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response {
                    0x79, 0x00, 0X00, 0x01,  // command code + operation + status + 1 tlv
                    0x02,                    // tlv type 2
                    0x00, 0x15,              // tlv size (2-bytes)
                    0x05,                    // number of metrics descriptor
                    0x00, 0x01, 0x00, 0x00,
                    0x00, 0x02, 0x00, 0x00,
                    0x00, 0x03, 0x40, 0x00,
                    0x00, 0x04, 0x40, 0x00,
                    0x00, 0x05, 0x08, 0x00 };

            command->handleResponse( CtiTime::now(), response );

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet {
                    1,
                    2,
                    3,
                    4,
                    5 };

            // use the order provided by the set
            const std::vector<unsigned long> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const auto dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_RANGES( *dynMetricsRcv, dynMetricsExp );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp {
                    0x7a, 0x00, 0x01,  //  command code + operation + 1 TLV
                    0x01,              //  TLV type 1
                    0x0f,              //  length 15
                    0x00, 0x00, 0x1c, 0xd4,  //  123 minute recording interval (7380 seconds)
                    0x00, 0x00, 0x6a, 0xe0,  //  456 minute reporting interval (27360 seconds)
                    0x03,              //  3 metrics
                    0x00, 0x03,
                    0x00, 0x04,
                    0x00, 0x05 };

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response {
                    0x7b, 0x00, 0X00, 0x01,    // command code + operation + status + 1 tlv
                    0x02,                      // tlv type 2
                    0x0d,                      // tlv size (1-byte)
                    0x03,                      // number of metrics descriptor
                    0x00, 0x03, 0x00, 0x00,
                    0x00, 0x04, 0x00, 0x00,
                    0x00, 0x05, 0x00, 0x00 };

            const auto results = command->handleResponse( CtiTime::now(), response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            BOOST_CHECK_EQUAL(
                    result.description,
                    "Channel Interval Recording Request:"
                    "\nStatus: Success (0)"
                    "\nChannel Interval Recording Full Description:"
                    "\nMetric(s) descriptors:"
                    "\nWatt hour total/sum (3): Scaling Factor: 1"
                    "\nWatt hour net (4): Scaling Factor: 1"
                    "\nWatts delivered, current demand (5): Scaling Factor: 1"
                    "\n");

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet {
                    3,
                    4,
                    5 };

            // use the order provided by the set
            const std::vector<int> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const auto dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_RANGES( *dynMetricsRcv, dynMetricsExp );

            const auto recordingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds );
            const auto reportingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds );

            BOOST_REQUIRE( !! recordingIntervalRcv );
            BOOST_REQUIRE( !! reportingIntervalRcv );

            BOOST_CHECK_EQUAL( unsigned(7380),  *recordingIntervalRcv );
            BOOST_CHECK_EQUAL( unsigned(27360), *reportingIntervalRcv );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_channel_configuration_sentinel )
{
    test_RfnCommercialDevice dut;
    dut.setDeviceType(TYPE_RFN430SL3);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    const std::map<std::string, std::string> configItems {
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "5" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "MIDNIGHT" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "RECEIVED_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "MIDNIGHT" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "SUM_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "NET_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "INSTANTANEOUS_KW" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" },
            { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
            { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" }};

    cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                    "rfnChannelConfiguration",
                    configItems));

    {
        CtiCommandParser parse("putconfig install channelconfig");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
        }

        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );
        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            const std::vector<unsigned char> exp {
                    0x78, 0x00, 0x01,
                    0x01, 0x00, 0x0b, 0x05, 
                    0x00, 0x01, 
                    0x00, 0x02, 
                    0x00, 0x03, 
                    0x00, 0x04, 
                    0x00, 0xc8 };  //  DEMAND is metric ID 200 (0xc8) for the Sentinel

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response {
                    0x79, 0x00, 0x00, 0x01,  // command code + operation + status + 1 tlv
                    0x02,                    // tlv type 2
                    0x00, 0x15,              // tlv size (2-bytes)
                    0x05,                    // number of metrics descriptor
                    0x00, 0x01, 0x00, 0x00,
                    0x00, 0x02, 0x00, 0x00,
                    0x00, 0x03, 0x40, 0x00,
                    0x00, 0x04, 0x40, 0x00,
                    0x00, 0xc8, 0x08, 0x00 };  //  DEMAND is metric ID 200 (0xc8) for the Sentinel

            command->handleResponse( CtiTime::now(), response );

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet {
                    1,
                    2,
                    3,
                    4,
                    200 };  //  DEMAND is metric ID 200 (0xc8) for the Sentinel

            // use the order provided by the set
            const std::vector<unsigned long> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const auto dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_RANGES( *dynMetricsRcv, dynMetricsExp );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp {
                    0x7a, 0x00, 0x01,   //  command code + operation + 1 TLV
                    0x01,               //  TLV type 1
                    0x0f,               //  length 15
                    0x00, 0x00, 0x1c, 0xd4,   //  123 minute recording interval 7380 seconds, 
                    0x00, 0x00, 0x6a, 0xe0,   //  456 minute reporting interval 27360 seconds, 
                    0x03,               //  3 metrics
                    0x00, 0x03, 
                    0x00, 0x04, 
                    0x00, 0xc8 };  //  DEMAND is metric ID 200 (0xc8) for the Sentinel

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response {
                    0x7b, 0x00, 0X00, 0x01,     // command code + operation + status + 1 tlv
                    0x02,                       // tlv type 2
                    0x0d,                       // tlv size 1-byte, 
                    0x03,                       // number of metrics descriptor
                    0x00, 0x03, 0x00, 0x00, 
                    0x00, 0x04, 0x00, 0x00, 
                    0x00, 0xc8, 0x00, 0x00 };  //  DEMAND is metric ID 200 (0xc8) for the Sentinel

            const auto results = command->handleResponse( CtiTime::now(), response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            BOOST_CHECK_EQUAL(
                    result.description,
                    "Channel Interval Recording Request:"
                    "\nStatus: Success (0)"
                    "\nChannel Interval Recording Full Description:"
                    "\nMetric(s) descriptors:"
                    "\nWatt hour total/sum (3): Scaling Factor: 1"
                    "\nWatt hour net (4): Scaling Factor: 1"
                    "\nWatts (200): Scaling Factor: 1"
                    "\n");

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet {
                    3,
                    4,
                    200 };

            // use the order provided by the set
            const std::vector<int> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const auto dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_RANGES( *dynMetricsRcv, dynMetricsExp );

            const auto recordingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds );
            const auto reportingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds );

            BOOST_REQUIRE( !! recordingIntervalRcv );
            BOOST_REQUIRE( !! reportingIntervalRcv );

            BOOST_CHECK_EQUAL( unsigned(7380),  *recordingIntervalRcv );
            BOOST_CHECK_EQUAL( unsigned(27360), *reportingIntervalRcv );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_all )
{
    test_RfnCommercialDevice dut;
    dut.setDeviceType(TYPE_RFN430SL3);

    typedef std::map<std::string, std::string>    CategoryItems;
    typedef std::pair<std::string, CategoryItems> CategoryDefinition;
    typedef std::vector<CategoryDefinition>       ConfigInstallItems;

    const ConfigInstallItems configurations {

            CategoryDefinition( // temperature alarming config
                "rfnTempAlarm", {
                    { RfnStrings::TemperatureAlarmEnabled,           "true" },
                    { RfnStrings::TemperatureAlarmRepeatInterval,    "15"   },
                    { RfnStrings::TemperatureAlarmRepeatCount,       "3"    },
                    { RfnStrings::TemperatureAlarmHighTempThreshold, "50"   }} ),

            CategoryDefinition( // channel config
                "rfnChannelConfiguration", {
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" },
                    { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
                    { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" }})
        };

    const std::vector<int> requestMsgsExp {
            0,   // no config data                   -> no request
            1,   // add temperature alarming config  -> +1 request
            3    // add channel config               -> +2 request
        };

    const std::vector< std::vector<bool> > returnExpectMoreExp {
            { true, true, true, true, false }, // no config data             -> 5 error messages, NOTE: last expectMore expected to be false
            { true, true, true, true },  // add temperature alarming config  -> 4 error message + 1 config sent message
            { true }                     // add channel config               -> 1 config sent message
        };

    std::vector<int> requestMsgsRcv;
    std::vector< std::vector<bool> > returnExpectMoreRcv;

    CtiCommandParser parse("putconfig install all");

    ////// empty configuration (no valid configuration) //////

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

    requestMsgsRcv.push_back( rfnRequests.size() );

    returnExpectMoreRcv.push_back( Cti::Test::extractExpectMore( returnMsgs ) );

    ////// add each configuration //////

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    for each( const CategoryDefinition & category in configurations )
    {
        resetTestState(); // note: reset test state does not erase the current configuration

        cfg.addCategory(
                Cti::Config::Category::ConstructCategory(
                        category.first,
                        category.second));

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        requestMsgsRcv.push_back( rfnRequests.size() );

        returnExpectMoreRcv.push_back(Cti::Test::extractExpectMore(returnMsgs));
    }

    BOOST_CHECK_EQUAL_RANGES( requestMsgsRcv, requestMsgsExp );
    BOOST_CHECK_EQUAL_RANGES( returnExpectMoreRcv, returnExpectMoreExp );
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_aggregate)
{
    struct : test_RfnCommercialDevice 
    {
        bool areAggregateCommandsSupported() const override { return true; }
    } 
    dut;

    dut.setDeviceType(TYPE_RFN430SL3);

    typedef std::map<std::string, std::string>    CategoryItems;
    typedef std::pair<std::string, CategoryItems> CategoryDefinition;
    typedef std::vector<CategoryDefinition>       ConfigInstallItems;

    const ConfigInstallItems configurations {

        { "rfnTempAlarm", {
            { RfnStrings::TemperatureAlarmEnabled,           "true" },
            { RfnStrings::TemperatureAlarmRepeatInterval,    "15" },
            { RfnStrings::TemperatureAlarmRepeatCount,       "3" },
            { RfnStrings::TemperatureAlarmHighTempThreshold, "50" } }},

        { "rfnChannelConfiguration", {
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
            { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" },
            { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
            { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" } }} };

    CtiCommandParser parse("putconfig install all");

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    for( auto & category : configurations )
    {
        cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                category.first,
                category.second));
    }

    Commands::RfnAggregateCommand::setGlobalContextId(0x4444, test_tag);

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests));

    BOOST_REQUIRE_EQUAL(rfnRequests.size(), 1);

    auto & command = *rfnRequests.front();

    const auto payload = command.executeCommand(execute_time);

    const std::vector<uint8_t> expected { 
       0x01,
       0x03,
       0x00, 0x32,
       0x44, 0x44, 
       0x02,
       0x00, 0x09, 
       0x78, 0x00, 0x01, 0x01, 0x00, 0x03, 0x01, 0x00, 0x01, 
       0x44, 0x45, 
       0x02,
       0x00, 0x0e,
       0x7a, 0x00, 0x01, 0x01, 0x09, 0x00, 0x00, 0x1c, 0xd4, 0x00, 0x00, 0x6a, 0xe0, 0x00, 
       0x44, 0x46, 
       0x06,
       0x00, 0x0c,
       0x88, 0x00, 0x01, 0x01, 0x07, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x0f, 0x03 };

    BOOST_CHECK_EQUAL_RANGES(expected, payload);

    const std::vector<uint8_t> response {
        0x01,
        0x03,
        0x00, 0x23,
        0x44, 0x44,
        0x02,
        0x00, 0x0c,
        0x79, 0x00, 0x00, 0x01, 0x02, 0x00, 0x05, 0x01, 0x00, 0x01, 0x00, 0x00,
        0x44, 0x45,
        0x02,
        0x00, 0x07,
        0x7b, 0x00, 0x00, 0x01, 0x02, 0x00, 0x00,
        0x44, 0x46,
        0x06,
        0x00, 0x04,
        0x89, 0x00, 0x00, 0x00 };

    const auto results = command.handleResponse(decode_time, response);

    BOOST_REQUIRE_EQUAL(results.size(), 3);

    auto itr = results.begin();

    {
        const auto & result = *itr++;

        BOOST_CHECK_EQUAL(result.description, 
            "Channel Selection Request:"
            "\nStatus: Success (0)"
            "\nChannel Registration Full Description:"
            "\nMetric(s) descriptors:"
            "\nWatt hour delivered (1): Scaling Factor: 1"
            "\n");
        BOOST_CHECK_EQUAL(result.status, 0);
        BOOST_CHECK(result.points.empty());
    }
    {
        const auto & result = *itr++;

        BOOST_CHECK_EQUAL(result.description, "Channel Interval Recording Request:"
                                              "\nNumber of bytes for channel descriptors received 0, expected >= 1");
        BOOST_CHECK_EQUAL(result.status, 264);
        BOOST_CHECK(result.points.empty());
    }
    {
        const auto & result = *itr++;

        BOOST_CHECK_EQUAL(result.description, "Set Temperature Alarm Configuration Request:"
                                              "\nStatus: Success (0)");
        BOOST_CHECK_EQUAL(result.status, 0);
        BOOST_CHECK(result.points.empty());
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_meter_programming )
{
    const Cti::Test::Override_MeterProgrammingManager overrideMeterProgrammingManager;
        
    test_RfnCommercialDevice dut;

    BOOST_CHECK_EQUAL(false, dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress));
    BOOST_CHECK_EQUAL(false, dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID));

    CtiCommandParser    parse("putconfig meter programming");

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests));
    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
    }

    {
        auto& command = rfnRequests.front();

        // execute message and check request bytes

        std::vector<unsigned char> exp {
            0x90,
            0x02,
            0x01,
                0x00, 0x04,
                    0x00, 0x00, 0x06, 0xd0,
            0x02,
                0x00, 0x33 };
        const std::string uri = "/meterPrograms/CBF44FB5-FEBB-451B-9E97-3504A902D0E1";
        exp.insert(exp.end(), uri.begin(), uri.end());

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    {
        double progress;
        BOOST_CHECK_EQUAL(true, dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, progress));
        BOOST_CHECK_EQUAL(0.0, progress);

        std::string configId;
        BOOST_CHECK_EQUAL(true, dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, configId));
        BOOST_CHECK_EQUAL("CBF44FB5-FEBB-451B-9E97-3504A902D0E1", configId);

    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_meter_programming_e2e_server_disabled )
{
    const Cti::Test::Override_MeterProgrammingManager overrideMeterProgrammingManager;

    test_RfnCommercialDevice dut;
    dut.e2eServerDisabled = true;

    CtiCommandParser    parse("putconfig meter programming");

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests));
    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK_EQUAL(true, rfnRequests.empty());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 202);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "No Method or Invalid Command.");
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_meter_programming_cancel )
{
    test_RfnCommercialDevice dut;

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, 0.0);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, "this-doesn't-matter-for-the-cancel");

    CtiCommandParser    parse("putconfig meter programming cancel");

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests));
    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK_EQUAL(true, rfnRequests.empty());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Meter programming canceled");
    }

    BOOST_CHECK_EQUAL(false, dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress));
    BOOST_CHECK_EQUAL(false, dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID));

    BOOST_CHECK_EQUAL(1, dut.mpsArchiveMessages.size());
    {
        const auto& result = dut.mpsArchiveMessages.front();

        BOOST_CHECK_EQUAL("Rthis-doesn't-matter-for-the-cancel", result.configurationId);
        BOOST_CHECK_EQUAL(0, result.error);
        const auto rfnId = Cti::RfnIdentifier{ "TEST", "RFN", "COMMERCIAL" };
        BOOST_CHECK_EQUAL(rfnId, result.rfnIdentifier);
        BOOST_CHECK_EQUAL(Cti::Messaging::Rfn::ProgrammingStatus::Canceled, result.status);
        //BOOST_CHECK_EQUAL("", result.timeStamp);
    }
}

BOOST_AUTO_TEST_CASE(test_putconfig_meter_programming_cancel_not_in_progress)
{
    test_RfnCommercialDevice dut;

    //  Do not set the Meter Programming dynamicPaoInfo entries
    //dut->setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, 0.0);
    //dut->setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, guid);

    CtiCommandParser    parse("putconfig meter programming cancel");

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests));
    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK_EQUAL(true, rfnRequests.empty());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Meter programming not in progress");
    }

    BOOST_CHECK_EQUAL(true, dut.mpsArchiveMessages.empty());
}

BOOST_AUTO_TEST_CASE( test_putconfig_meter_programming_cancel_e2e_server_disabled )
{
    const Cti::Test::Override_MeterProgrammingManager overrideMeterProgrammingManager;

    test_RfnCommercialDevice dut;
    dut.e2eServerDisabled = true;

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, 0.0);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, "this doesn't matter for the cancel");

    CtiCommandParser    parse("putconfig meter programming cancel");

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests));
    BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
    BOOST_CHECK_EQUAL(true, rfnRequests.empty());

    {
        const auto& returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL(returnMsg.Status(), 202);
        BOOST_CHECK_EQUAL(returnMsg.ResultString(), "No Method or Invalid Command.");
    }

    BOOST_CHECK_EQUAL(true, dut.mpsArchiveMessages.empty());
}

BOOST_AUTO_TEST_CASE( test_config_notification )
{
    auto cmd = Cti::Devices::Commands::RfnCommand::handleUnsolicitedReport(execute_time, test_cmd_rfn_ConfigNotification::payload);

    BOOST_REQUIRE(cmd);

    Cti::Devices::RfnCommercialDevice dut;

    dut.extractCommandResult(*cmd);

    using PI = CtiTableDynamicPaoInfo;
    using PIIdx = CtiTableDynamicPaoInfoIndexed;

    Cti::Test::PaoInfoValidator dpiExpected[] 
    {
        { PI::Key_RFN_TempAlarmIsEnabled,            1 },
        { PI::Key_RFN_TempAlarmRepeatInterval,       7 },
        { PI::Key_RFN_TempAlarmRepeatCount,         11 },
        { PI::Key_RFN_TempAlarmHighTempThreshold, 5889 },

        { PI::Key_RFN_RecordingIntervalSeconds,  7200 },
        { PI::Key_RFN_ReportingIntervalSeconds, 86400 },

        { PI::Key_RFN_MetrologyLibraryEnabled, false },
    };

    BOOST_CHECK_EQUAL(overrideDynamicPaoInfoManager.dpi->dirtyEntries[-1].size(), std::size(dpiExpected));

    for( auto expected : dpiExpected )
    {
        BOOST_TEST_CONTEXT("Key " << expected.key)
        {
            BOOST_CHECK( expected.validate(dut) );
        }
    }

    const auto midnightExpected = { 5, 6, 7, 8 };
    const auto midnightActual = dut.findDynamicInfo<unsigned long>(PIIdx::Key_RFN_MidnightMetrics);
    BOOST_REQUIRE(midnightActual);
    BOOST_CHECK_EQUAL_RANGES(midnightActual.value(), midnightExpected);
    const auto intervalExpected = { 1, 2, 3, 4 };
    const auto intervalActual = dut.findDynamicInfo<unsigned long>(PIIdx::Key_RFN_IntervalMetrics);
    BOOST_REQUIRE(intervalActual);
    BOOST_CHECK_EQUAL_RANGES(intervalActual.value(), intervalExpected);

    const auto json = cmd->getDataStreamingJson(dut.getDeviceType());

    BOOST_CHECK_EQUAL(json, 
R"SQUID(DATA_STREAMING_JSON{
"streamingEnabled" : true,
"configuredMetrics" : [
  {
    "attribute" : "DELIVERED_DEMAND",
    "interval" : 5,
    "enabled" : true,
    "status" : "OK"
  },
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false,
    "status" : "METER_ACCESS_ERROR"
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true,
    "status" : "METER_OR_NODE_BUSY"
  }],
"sequence" : 3735928559
})SQUID");
}


BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_channel_configuration_verify_alpha_empty_channel_config )
{
    using CC = RfnStrings::ChannelConfiguration;

    test_RfnCommercialDevice dut;
    dut.setDeviceType(TYPE_RFN430A3K);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    const std::map<std::string, std::string> configItems
    {
        { CC::EnabledChannels_Prefix,   "0"   },
        { CC::RecordingIntervalMinutes, "123" },
        { CC::ReportingIntervalMinutes, "456" }
    };

    cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                    "rfnChannelConfiguration",
                    configItems));

    // set up dynamic pao info
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60 );
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60 );
    }

    // test the cases where we have no channel related dynamic pao info

    {
        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    resetTestState();
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    resetTestState();
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    // test the cases where we have channel related dynamic pao info, but it doesn't match the config

    resetTestState();

    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, { 1, 3 } );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, { 3 } );

    {
        dut.purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    // test the cases where we have channel related dynamic pao info and it matches the config

    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, { } );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, { } );

    resetTestState();

    {
        dut.purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_channel_configuration_verify_alpha_filtering_channel_config )
{
    using CC = RfnStrings::ChannelConfiguration;

    test_RfnCommercialDevice dut;
    dut.setDeviceType(TYPE_RFN430A3K);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    const std::map<std::string, std::string> configItems
    {
        { CC::EnabledChannels_Prefix,   "5" },
        { CC::EnabledChannels_Prefix + ".0." + CC::EnabledChannels::Attribute, "DELIVERED_KWH" },
        { CC::EnabledChannels_Prefix + ".0." + CC::EnabledChannels::Read,      "MIDNIGHT" },
        { CC::EnabledChannels_Prefix + ".1." + CC::EnabledChannels::Attribute, "RECEIVED_KWH" },
        { CC::EnabledChannels_Prefix + ".1." + CC::EnabledChannels::Read,      "MIDNIGHT" },
        { CC::EnabledChannels_Prefix + ".2." + CC::EnabledChannels::Attribute, "SUM_KWH" },
        { CC::EnabledChannels_Prefix + ".2." + CC::EnabledChannels::Read,      "INTERVAL" },
        { CC::EnabledChannels_Prefix + ".3." + CC::EnabledChannels::Attribute, "NET_KWH" },
        { CC::EnabledChannels_Prefix + ".3." + CC::EnabledChannels::Read,      "INTERVAL" },
        { CC::EnabledChannels_Prefix + ".4." + CC::EnabledChannels::Attribute, "DELIVERED_DEMAND" },
        { CC::EnabledChannels_Prefix + ".4." + CC::EnabledChannels::Read,      "INTERVAL" },
        { CC::RecordingIntervalMinutes, "123" },
        { CC::ReportingIntervalMinutes, "456" }
    };

    cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                    "rfnChannelConfiguration",
                    configItems));

    // set up dynamic pao info
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60 );
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60 );
    }

    // test the cases where we have no channel related dynamic pao info

    {
        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    resetTestState();
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    resetTestState();
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    // test the cases where we have channel related dynamic pao info, but it doesn't match the config

    resetTestState();

    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, { 1, 3 } );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, { 3 } );

    {
        dut.purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    // test the cases where we have channel related dynamic pao info and it matches the config

    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, { 1, 2, 3, 4, 5 } );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, { 3, 4, 5 } );

    resetTestState();

    {
        dut.purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_channel_configuration_verify_alpha_filtering_channel_config_midnight_channels_only )
{
    using CC = RfnStrings::ChannelConfiguration;

    test_RfnCommercialDevice dut;
    dut.setDeviceType(TYPE_RFN430A3K);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    const std::map<std::string, std::string> configItems
    {
        { CC::EnabledChannels_Prefix,   "2" },
        { CC::EnabledChannels_Prefix + ".0." + CC::EnabledChannels::Attribute, "DELIVERED_KWH" },
        { CC::EnabledChannels_Prefix + ".0." + CC::EnabledChannels::Read,      "MIDNIGHT" },
        { CC::EnabledChannels_Prefix + ".1." + CC::EnabledChannels::Attribute, "RECEIVED_KWH" },
        { CC::EnabledChannels_Prefix + ".1." + CC::EnabledChannels::Read,      "MIDNIGHT" },
        { CC::RecordingIntervalMinutes, "123" },
        { CC::ReportingIntervalMinutes, "456" }
    };

    cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                    "rfnChannelConfiguration",
                    configItems));

    // set up dynamic pao info
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60 );
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60 );
    }

    // test the cases where we have no channel related dynamic pao info

    {
        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    resetTestState();
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    resetTestState();
    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    // test the cases where we have channel related dynamic pao info, but it doesn't match the config

    resetTestState();

    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, { 1 } );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, { 1 } );    // not match an empty collection

    {
        dut.purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }

    // test the cases where we have channel related dynamic pao info and it matches the config

    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, { 1, 2 } );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, { } );

    resetTestState();

    {
        dut.purgeDynamicPaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, true );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is current." );
    }

    {
        dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ChannelConfigFiltered, false );

        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, requestMsgs, rfnRequests) );

        BOOST_REQUIRE( returnMsgs.size() > 0 );

        const auto & returnMsg = *returnMsgs.back();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config channelconfig is NOT current." );
    }
}

BOOST_AUTO_TEST_SUITE_END()
