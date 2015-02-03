#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rfnCommercial.h"
#include "cmd_rfn.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnCommercialDevice : RfnCommercialDevice
{
    using RfnCommercialDevice::handleCommandResult;
    using CtiTblPAOLite::_type;
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
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

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
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

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
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}
*/
BOOST_AUTO_TEST_CASE( test_dev_rfnCommercial_putconfig_install_channel_configuration )
{
    test_RfnCommercialDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    const std::map<std::string, std::string> configItems = boost::assign::map_list_of
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "5" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "MIDNIGHT" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "RECEIVED_KWH" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "MIDNIGHT" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "SUM_KWH" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "NET_KWH" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DEMAND" )
            ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
              + RfnStrings::ChannelConfiguration::EnabledChannels::Read,    "INTERVAL" )
            ( RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" )
            ( RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" );

    cfg.addCategory(
            Cti::Config::Category::ConstructCategory(
                    "rfnChannelConfiguration",
                    configItems));

    {
        CtiCommandParser parse("putconfig install channelconfig");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
        }

        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );
        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x78)(0x00)(0x01)
                    (0x01)(0x00)(0x0b)(0x05)(0x00)(0x01)(0x00)(0x02)(0x00)(0x03)(0x00)(0x04)(0x00)(0x05);

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response = boost::assign::list_of
                    (0x79)(0x00)(0X00)(0x01)  // command code + operation + status + 1 tlv
                    (0x02)                    // tlv type 2
                    (0x00)(0x15)              // tlv size (2-bytes)
                    (0x05)                    // number of metrics descriptor
                    (0x00)(0x01)(0x00)(0x00)
                    (0x00)(0x02)(0x00)(0x00)
                    (0x00)(0x03)(0x40)(0x00)
                    (0x00)(0x04)(0x40)(0x00)
                    (0x00)(0x05)(0x08)(0x00);

            command->decodeCommand( CtiTime::now(), response );

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet = boost::assign::list_of
                    ( 1 )
                    ( 2 )
                    ( 3 )
                    ( 4 )
                    ( 5 );

            // use the order provided by the set
            const std::vector<unsigned long> dynMetricsExp( dynMetricsExpSet.begin(), dynMetricsExpSet.end());

            const boost::optional<std::vector<unsigned long>> dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_COLLECTIONS( dynMetricsRcv->begin(), dynMetricsRcv->end(), dynMetricsExp.begin(), dynMetricsExp.end() );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x7a)(0x00)(0x01)  //  command code + operation + 1 TLV
                    (0x01)              //  TLV type 1
                    (0x0f)              //  length 15
                    (0x00)(0x00)(0x1c)(0xd4)  //  123 minute recording interval (7380 seconds)
                    (0x00)(0x00)(0x6a)(0xe0)  //  456 minute reporting interval (27360 seconds)
                    (0x03)              //  3 metrics
                    (0x00)(0x03)
                    (0x00)(0x04)
                    (0x00)(0x05);

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response = boost::assign::list_of
                    (0x7b)(0x00)(0X00)(0x01)    // command code + operation + status + 1 tlv
                    (0x02)                      // tlv type 2
                    (0x0d)                      // tlv size (1-byte)
                    (0x03)                      // number of metrics descriptor
                    (0x00)(0x03)(0x00)(0x00)
                    (0x00)(0x04)(0x00)(0x00)
                    (0x00)(0x05)(0x00)(0x00)
                    ;

            const Cti::Devices::Commands::RfnCommandResult result = command->decodeCommand( CtiTime::now(), response );

            BOOST_CHECK_EQUAL(
                    result.description,
                    "Status: Success (0)\n"
                    "Channel Interval Recording Full Description:\n"
                    "Metric(s) descriptors:\n"
                    "Watt hour total/sum (3): Scaling Factor: 1\n"
                    "Watt hour net (4): Scaling Factor: 1\n"
                    "Watts delivered, current demand (5): Scaling Factor: 1\n");

            dut.extractCommandResult( *command );

            const std::set<unsigned long> dynMetricsExpSet = boost::assign::list_of
                    ( 3 )
                    ( 4 )
                    ( 5 );

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

BOOST_AUTO_TEST_CASE( test_putconfig_install_all_disconnect_meter )
{
    using boost::assign::list_of;
    using boost::assign::map_list_of;

    test_RfnCommercialDevice dut;
    dut._type = TYPE_RFN420CD;

    typedef std::map<std::string, std::string>    CategoryItems;
    typedef std::pair<std::string, CategoryItems> CategoryDefinition;
    typedef std::vector<CategoryDefinition>       ConfigInstallItems;

    const ConfigInstallItems configurations = list_of<CategoryDefinition>

            ( CategoryDefinition( // temperature alarming config
                "rfnTempAlarm", map_list_of
                    ( RfnStrings::TemperatureAlarmEnabled,           "true" )
                    ( RfnStrings::TemperatureAlarmRepeatInterval,    "15"   )
                    ( RfnStrings::TemperatureAlarmRepeatCount,       "3"    )
                    ( RfnStrings::TemperatureAlarmHighTempThreshold, "50"   )))

            ( CategoryDefinition( // channel config
                "rfnChannelConfiguration", map_list_of
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" )
                    ( RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" )
                    ( RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" )))
            ;

    const std::vector<int> requestMsgsExp = list_of
            ( 0 )  // no config data                   -> no request
            ( 1 )  // add temperature alarming config  -> +1 request
            ( 3 )  // add channel config               -> +2 request
            ;

    const std::vector< std::vector<bool> > returnExpectMoreExp = list_of< std::vector<bool> >
            ( list_of<bool>(true)(false) )    // no config data                   -> 2 error messages, NOTE: last expectMore expected to be false
            ( list_of<bool>(true)(true) )     // add temperature alarming config  -> 1 error message + 1 config sent message
            ( list_of<bool>(true) )           // add channel config               -> 2 config sent message
            ;

    std::vector<int> requestMsgsRcv;
    std::vector< std::vector<bool> > returnExpectMoreRcv;

    CtiCommandParser parse("putconfig install all");

    ////// empty configuration (no valid configuration) //////

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

    requestMsgsRcv.push_back( rfnRequests.size() );

    std::vector<bool> expectMoreRcv;
    for each( const CtiReturnMsg &m in returnMsgs )
    {
        expectMoreRcv.push_back( m.ExpectMore() );
    }
    returnExpectMoreRcv.push_back( expectMoreRcv );

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

        std::vector<bool> expectMoreRcv;
        for each( const CtiReturnMsg &m in returnMsgs )
        {
            expectMoreRcv.push_back( m.ExpectMore() );
        }
        returnExpectMoreRcv.push_back( expectMoreRcv );
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( requestMsgsRcv.begin(), requestMsgsRcv.end(), requestMsgsExp.begin(), requestMsgsExp.end() );
    BOOST_CHECK_EQUAL_COLLECTIONS( returnExpectMoreRcv.begin(), returnExpectMoreRcv.end(), returnExpectMoreExp.begin(), returnExpectMoreExp.end() );
}

BOOST_AUTO_TEST_SUITE_END()
