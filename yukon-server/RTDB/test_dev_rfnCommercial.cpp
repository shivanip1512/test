#include <boost/test/unit_test.hpp>

#include "dev_rfnCommercial.h"
#include "cmd_rfn.h"
#include "cmd_rfn_Aggregate.h"  //  to reset the global counter
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnCommercialDevice : RfnCommercialDevice
{
    using RfnCommercialDevice::handleCommandResult;
    using CtiDeviceBase::setDeviceType;
};

struct test_state_rfnCommercial
{
    std::auto_ptr<CtiRequestMsg> request;
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

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}
/*
BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_getconfig_install_freezeday )
{
    test_RfnCommercialDevice    dev;

    CtiCommandParser    parse("getconfig install freezeday");

    BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_freezeday )
{
    test_RfnCommercialDevice    dev;

    CtiCommandParser    parse("putconfig install freezeday 24");

    BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}
*/
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

            const boost::optional<std::vector<unsigned long>> dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_COLLECTIONS( dynMetricsRcv->begin(), dynMetricsRcv->end(), dynMetricsExp.begin(), dynMetricsExp.end() );
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
                    "Channel Interval Recording Request Status: Success (0)\n"
                    "Channel Interval Recording Full Description:\n"
                    "Metric(s) descriptors:\n"
                    "Watt hour total/sum (3): Scaling Factor: 1\n"
                    "Watt hour net (4): Scaling Factor: 1\n"
                    "Watts delivered, current demand (5): Scaling Factor: 1\n");

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet {
                    3,
                    4,
                    5 };

            // use the order provided by the set
            const std::vector<int> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const boost::optional<std::vector<unsigned long>> dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_RANGES( *dynMetricsRcv, dynMetricsExp );

            const boost::optional<unsigned> recordingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds );
            const boost::optional<unsigned> reportingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds );

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

            const boost::optional<std::vector<unsigned long>> dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_COLLECTIONS( dynMetricsRcv->begin(), dynMetricsRcv->end(), dynMetricsExp.begin(), dynMetricsExp.end() );
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
                    "Channel Interval Recording Request Status: Success (0)\n"
                    "Channel Interval Recording Full Description:\n"
                    "Metric(s) descriptors:\n"
                    "Watt hour total/sum (3): Scaling Factor: 1\n"
                    "Watt hour net (4): Scaling Factor: 1\n"
                    "Watts (200): Scaling Factor: 1\n");

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet {
                    3,
                    4,
                    200 };

            // use the order provided by the set
            const std::vector<int> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const boost::optional<std::vector<unsigned long>> dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_RANGES( *dynMetricsRcv, dynMetricsExp );

            const boost::optional<unsigned> recordingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds );
            const boost::optional<unsigned> reportingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds );

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

    BOOST_CHECK_EQUAL_COLLECTIONS( requestMsgsRcv.begin(), requestMsgsRcv.end(), requestMsgsExp.begin(), requestMsgsExp.end() );
    BOOST_CHECK_EQUAL_COLLECTIONS( returnExpectMoreRcv.begin(), returnExpectMoreRcv.end(), returnExpectMoreExp.begin(), returnExpectMoreExp.end() );
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_aggregate)
{
    struct : test_RfnCommercialDevice 
    {
        bool isAggregateCommandSupported() const override { return true; }
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
       0x2f, 0x00,
       0x44, 0x44, 
       0x09, 0x00, 
       0x78, 0x00, 0x01, 0x01, 0x00, 0x03, 0x01, 0x00, 0x01, 
       0x45, 0x44, 
       0x0e, 0x00, 
       0x7a, 0x00, 0x01, 0x01, 0x09, 0x00, 0x00, 0x1c, 0xd4, 0x00, 0x00, 0x6a, 0xe0, 0x00, 
       0x46, 0x44, 
       0x0c, 0x00, 
       0x88, 0x00, 0x01, 0x01, 0x07, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x0f, 0x03 };

    BOOST_CHECK_EQUAL_RANGES(expected, payload);

    const std::vector<uint8_t> response {
        0x01,
        0x03,
        0x20, 0x00,
        0x44, 0x44,
        0x0c, 0x00,
        0x79, 0x00, 0x00, 0x01, 0x02, 0x00, 0x05, 0x01, 0x00, 0x01, 0x00, 0x00,
        0x45, 0x44,
        0x07, 0x00,
        0x7b, 0x00, 0x00, 0x01, 0x02, 0x00, 0x00,
        0x46, 0x44,
        0x04, 0x00,
        0x89, 0x00, 0x00, 0x00 };

    const auto results = command.handleResponse(decode_time, response);

    BOOST_REQUIRE_EQUAL(results.size(), 3);

    auto itr = results.begin();

    {
        const auto & result = *itr++;

        BOOST_CHECK_EQUAL(result.description, 
            "Channel Selection Request Status: Success (0)"
            "\nChannel Registration Full Description:"
            "\nMetric(s) descriptors:"
            "\nWatt hour delivered (1): Scaling Factor: 1"
            "\n");
        BOOST_CHECK_EQUAL(result.status, 0);
        BOOST_CHECK(result.points.empty());
    }
    {
        const auto & result = *itr++;

        BOOST_CHECK_EQUAL(result.description, "Channel Interval Recording Request Number of bytes for channel descriptors received 0, expected >= 1");
        BOOST_CHECK_EQUAL(result.status, 264);
        BOOST_CHECK(result.points.empty());
    }
    {
        const auto & result = *itr++;

        BOOST_CHECK_EQUAL(result.description, "Set Temperature Alarm Configuration Request Status: Success (0)");
        BOOST_CHECK_EQUAL(result.status, 0);
        BOOST_CHECK(result.points.empty());
    }
}

BOOST_AUTO_TEST_SUITE_END()
