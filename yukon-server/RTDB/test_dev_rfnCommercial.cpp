#include <boost/test/unit_test.hpp>

#include "dev_rfnCommercial.h"
#include "cmd_rfn.h"
#include "cmd_rfn_Aggregate.h"  //  to reset the global counter
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnCommercialDevice : RfnCommercialDevice
{
    using CtiDeviceBase::setDeviceType;

    bool areAggregateCommandsSupported() const override
    {
        return false;
    }
};

struct test_state_rfnCommercial
{
    std::unique_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
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

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

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
            { true, true, true, false }, // no config data             -> 4 error messages, NOTE: last expectMore expected to be false
            { true, true, true },  // add temperature alarming config  -> 3 error message + 1 config sent message
            { true }               // add channel config               -> 1 config sent message
        };

    std::vector<int> requestMsgsRcv;
    std::vector< std::vector<bool> > returnExpectMoreRcv;

    CtiCommandParser parse("putconfig install all");

    ////// empty configuration (no valid configuration) //////

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

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

    BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));

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

BOOST_AUTO_TEST_CASE( test_config_notification )
{
    auto cmd = Cti::Devices::Commands::RfnCommand::handleNodeOriginated(execute_time, test_cmd_rfn_ConfigNotification::payload);

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

BOOST_AUTO_TEST_SUITE_END()
