#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rfnResidentialVoltage.h"
#include "cmd_rfn.h"
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnResidentialVoltageDevice : RfnResidentialVoltageDevice
{
    using CtiDeviceBase::setDeviceType;

    bool areAggregateCommandsSupported() const override 
    {
        return false;
    }
};

struct test_state_rfnResidentialVoltage
{
    std::unique_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfnResidentialVoltage() :
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


BOOST_FIXTURE_TEST_SUITE( test_dev_rfnResidentialVoltage, test_state_rfnResidentialVoltage )

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_getconfig_install_ovuv_meterids )
{
    test_RfnResidentialVoltageDevice    dev;

    const std::vector<DeviceTypes> rfnTypes
    {
        TYPE_RFN420FL,
        TYPE_RFN420FX,
        TYPE_RFN420FD,
        TYPE_RFN420FRX,
        TYPE_RFN420FRD,
        TYPE_RFN410CL,
        TYPE_RFN420CL,
        TYPE_RFN420CLW,
        TYPE_RFN420CD,
        TYPE_RFN420CDW
    };

    CtiCommandParser parse("getconfig install ovuv");

    using ResultType = std::vector<std::vector<unsigned char>>;

    const ResultType expected
    {
        { 0x34, 0x02, 0x07, 0xe6 },
        { 0x34, 0x02, 0x07, 0xe7 },
        { 0x34, 0x03, 0x07, 0xe6 },
        { 0x34, 0x03, 0x07, 0xe7 },
        { 0x34, 0x03, 0x07, 0xe6 },
        { 0x34, 0x03, 0x07, 0xe7 },
        { 0x34, 0x03, 0x07, 0xe6 },
        { 0x34, 0x03, 0x07, 0xe7 },
        { 0x34, 0x03, 0x07, 0xe6 },
        { 0x34, 0x03, 0x07, 0xe7 },
        { 0x34, 0x06, 0x07, 0xe6 },
        { 0x34, 0x06, 0x07, 0xe7 },
        { 0x34, 0x04, 0x07, 0xe6 },
        { 0x34, 0x04, 0x07, 0xe7 },
        { 0x34, 0x04, 0x07, 0xe6 },
        { 0x34, 0x04, 0x07, 0xe7 },
        { 0x34, 0x04, 0x07, 0xe6 },
        { 0x34, 0x04, 0x07, 0xe7 },
        { 0x34, 0x04, 0x07, 0xe6 },
        { 0x34, 0x04, 0x07, 0xe7 }
    };

    ResultType results;

    for ( DeviceTypes type : rfnTypes )
    {
        dev.setDeviceType(type);

        BOOST_CHECK_EQUAL( ClientErrors::None, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

        for ( auto& cmd : rfnRequests )
        {
            results.push_back(cmd->executeCommand(execute_time));
        }

        rfnRequests.clear();
    }

    BOOST_CHECK_EQUAL_RANGES( expected, results );
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_ovuv )
{
    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420FX);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
    cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
    cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "60" );
    cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
    cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
    cfg.insertValue( RfnStrings::UvThreshold,                "78.901" );

    using Bytes = std::vector<unsigned char>;

    {
        CtiCommandParser parse("putconfig install ovuv");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 6, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "6 commands queued for device" );
        }

        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            Bytes exp
            {
                0x24, 0x01
            };

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            long test_ovuvEnabled;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled, test_ovuvEnabled ));
            BOOST_CHECK_EQUAL( static_cast<bool>(test_ovuvEnabled), true );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            Bytes exp
            {
                0x26, 0x05
            };

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            unsigned test_ovuvAlarmReportingInterval;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval, test_ovuvAlarmReportingInterval ));
            BOOST_CHECK_EQUAL( test_ovuvAlarmReportingInterval, 5 );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            Bytes exp
            {
                0x27, 0x3c
            };

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            unsigned test_ovuvAlarmRepeatInterval;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval, test_ovuvAlarmRepeatInterval ));
            BOOST_CHECK_EQUAL( test_ovuvAlarmRepeatInterval, 60 );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            Bytes exp
            {
                0x28, 0x02
            };

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            unsigned test_ovuvAlarmRepeatCount;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount, test_ovuvAlarmRepeatCount ));
            BOOST_CHECK_EQUAL( test_ovuvAlarmRepeatCount, 2 );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            Bytes exp
            {
                0x25, 0x03, 0x07, 0xe6, 0x00, 0x01, 0xe2, 0x40, 0x10, 0x80, 0x00, 0x01, 0xc0
            };

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            double test_ovThreshold;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold, test_ovThreshold ));
            BOOST_CHECK_EQUAL( test_ovThreshold, 123.456 );
        }
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            Bytes exp
            {
                0x25, 0x03, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0
            };

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            double test_uvThreshold;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold,  test_uvThreshold ));
            BOOST_CHECK_EQUAL( test_uvThreshold, 78.901 );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_ovuv_meter_ids )
{
    test_RfnResidentialVoltageDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
    cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
    cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "60" );
    cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
    cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
    cfg.insertValue( RfnStrings::UvThreshold,                "78.901" );

    //  set the dynamic pao info to match
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,                 1);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval,  5);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval,    60);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount,             2);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvThreshold,           123.456);
    //dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_UvThreshold,          78.901);  //  leave this out so it's the only one sent

    {
        const std::vector<DeviceTypes> rfnTypes
        {
            TYPE_RFN420FL,
            TYPE_RFN420FX,
            TYPE_RFN420FD,
            TYPE_RFN420FRX,
            TYPE_RFN420FRD,
            TYPE_RFN410CL,
            TYPE_RFN420CL,
            TYPE_RFN420CLW,
            TYPE_RFN420CD,
            TYPE_RFN420CDW
        };

        using ResultType = std::vector<std::vector<unsigned char>>;

        const ResultType expected
        {
            { 0x25, 0x02, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x03, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x03, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x03, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x03, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x06, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x04, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x04, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x04, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 },
            { 0x25, 0x04, 0x07, 0xe7, 0x00, 0x01, 0x34, 0x35, 0x10, 0x80, 0x00, 0x01, 0xc0 }
        };

        CtiCommandParser parse("putconfig install ovuv");

        ResultType results;

        for ( DeviceTypes type : rfnTypes )
        {
            dut.setDeviceType(type);

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
            BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

            {
                const auto & returnMsg = *returnMsgs.front();

                BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
            }

            results.push_back(rfnRequests.front()->executeCommand( execute_time ));

            returnMsgs.clear();
            rfnRequests.clear();
        }

        BOOST_CHECK_EQUAL_RANGES( expected, results );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_ovuv_invalid_config )
{
    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420FX);

    {
        ///// Missing config data /////

        CtiCommandParser parse("putconfig install ovuv");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 2, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::NoConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Missing data for config key \"ovuvEnabled\"." );
        }
    }

    {
        resetTestState();

        ///// Invalid config data /////

        Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
        cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
        cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "-60" ); // insert invalid negative value
        cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
        cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
        cfg.insertValue( RfnStrings::UvThreshold,                "78.901" );

        CtiCommandParser parse("putconfig install ovuv");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 3, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::InvalidConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(),
                "Invalid data for config key \"alarmRepeatInterval\" : invalid value -60 is out of range, expected [0 - 4294967295]." );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_all_device )
{
    using boost::assign::list_of;
    using boost::assign::map_list_of;

    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420FX);

    using CategoryItems      = std::map<std::string, std::string>;
    using CategoryDefinition = std::pair<std::string, CategoryItems>;
    using ConfigInstallItems = std::vector<CategoryDefinition>;

    const ConfigInstallItems configurations = list_of<CategoryDefinition>

            // demand freeze day config
            ( CategoryDefinition(
                "demandFreeze", map_list_of
                    ( RfnStrings::demandFreezeDay, "7" )))

            // OVUV config
            ( CategoryDefinition(
                "rfnVoltage", map_list_of
                    ( RfnStrings::voltageAveragingInterval,   "60"      )
                    ( RfnStrings::OvUvEnabled,                "true"    )
                    ( RfnStrings::OvUvAlarmReportingInterval, "5"       )
                    ( RfnStrings::OvUvAlarmRepeatInterval,    "60"      )
                    ( RfnStrings::OvUvRepeatCount,            "2"       )
                    ( RfnStrings::OvThreshold,                "123.456" )
                    ( RfnStrings::UvThreshold,                "78.901"  )))

            // TOU config
            ( CategoryDefinition(
                "tou", map_list_of
                    // SCHEDULE_1
                    ( RfnStrings::Schedule1Time0, "00:00" )
                    ( RfnStrings::Schedule1Time1, "00:01" )
                    ( RfnStrings::Schedule1Time2, "10:06" )
                    ( RfnStrings::Schedule1Time3, "12:22" )
                    ( RfnStrings::Schedule1Time4, "23:33" )
                    ( RfnStrings::Schedule1Time5, "23:44" )

                    ( RfnStrings::Schedule1Rate0, "A" )
                    ( RfnStrings::Schedule1Rate1, "B" )
                    ( RfnStrings::Schedule1Rate2, "C" )
                    ( RfnStrings::Schedule1Rate3, "D" )
                    ( RfnStrings::Schedule1Rate4, "A" )
                    ( RfnStrings::Schedule1Rate5, "B" )

                    // SCHEDULE_2
                    ( RfnStrings::Schedule2Time0, "00:00" )
                    ( RfnStrings::Schedule2Time1, "01:23" )
                    ( RfnStrings::Schedule2Time2, "03:12" )
                    ( RfnStrings::Schedule2Time3, "04:01" )
                    ( RfnStrings::Schedule2Time4, "05:23" )
                    ( RfnStrings::Schedule2Time5, "16:28" )

                    ( RfnStrings::Schedule2Rate0, "D" )
                    ( RfnStrings::Schedule2Rate1, "A" )
                    ( RfnStrings::Schedule2Rate2, "B" )
                    ( RfnStrings::Schedule2Rate3, "C" )
                    ( RfnStrings::Schedule2Rate4, "D" )
                    ( RfnStrings::Schedule2Rate5, "A" )

                    // SCHEDULE_3
                    ( RfnStrings::Schedule3Time0, "00:00" )
                    ( RfnStrings::Schedule3Time1, "01:02" )
                    ( RfnStrings::Schedule3Time2, "02:03" )
                    ( RfnStrings::Schedule3Time3, "04:05" )
                    ( RfnStrings::Schedule3Time4, "05:06" )
                    ( RfnStrings::Schedule3Time5, "06:07" )

                    ( RfnStrings::Schedule3Rate0, "C" )
                    ( RfnStrings::Schedule3Rate1, "D" )
                    ( RfnStrings::Schedule3Rate2, "A" )
                    ( RfnStrings::Schedule3Rate3, "B" )
                    ( RfnStrings::Schedule3Rate4, "C" )
                    ( RfnStrings::Schedule3Rate5, "D" )

                    // SCHEDULE_4
                    ( RfnStrings::Schedule4Time0, "00:00" )
                    ( RfnStrings::Schedule4Time1, "00:01" )
                    ( RfnStrings::Schedule4Time2, "08:59" )
                    ( RfnStrings::Schedule4Time3, "12:12" )
                    ( RfnStrings::Schedule4Time4, "23:01" )
                    ( RfnStrings::Schedule4Time5, "23:55" )

                    ( RfnStrings::Schedule4Rate0, "B" )
                    ( RfnStrings::Schedule4Rate1, "C" )
                    ( RfnStrings::Schedule4Rate2, "D" )
                    ( RfnStrings::Schedule4Rate3, "A" )
                    ( RfnStrings::Schedule4Rate4, "B" )
                    ( RfnStrings::Schedule4Rate5, "C" )

                    // day table
                    ( RfnStrings::SundaySchedule,    "SCHEDULE_1" )
                    ( RfnStrings::MondaySchedule,    "SCHEDULE_1" )
                    ( RfnStrings::TuesdaySchedule,   "SCHEDULE_3" )
                    ( RfnStrings::WednesdaySchedule, "SCHEDULE_2" )
                    ( RfnStrings::ThursdaySchedule,  "SCHEDULE_4" )
                    ( RfnStrings::FridaySchedule,    "SCHEDULE_2" )
                    ( RfnStrings::SaturdaySchedule,  "SCHEDULE_3" )
                    ( RfnStrings::HolidaySchedule,   "SCHEDULE_3" )

                    // default rate
                    ( RfnStrings::DefaultTouRate, "B" )

                    // set TOU enabled
                    ( RfnStrings::touEnabled, "true" )))

            // temperature alarming config
            ( CategoryDefinition(
                "rfnTempAlarm", map_list_of
                    ( RfnStrings::TemperatureAlarmEnabled,           "true" )
                    ( RfnStrings::TemperatureAlarmRepeatInterval,    "15"   )
                    ( RfnStrings::TemperatureAlarmRepeatCount,       "3"    )
                    ( RfnStrings::TemperatureAlarmHighTempThreshold, "50"   )))

            // channel config
            ( CategoryDefinition(
                "rfnChannelConfiguration", map_list_of
                    ( RfnStrings::enableDataStreaming, "false" )
                    ( RfnStrings::voltageDataStreamingIntervalMinutes, "2" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" )
                    ( RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" )
                    ( RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" )))
            ;

    const std::vector<int> requestMsgsExp
    {
        0,      // no config data                   -> no request
        1,      // add demand freeze day config     -> +1 request
        7,      // add OVUV config                  -> +6 request
        9,      // add TOU config                   -> +2 request
        10,     // add temperature alarming config  -> +1 request
        14      // add channel config               -> +4 request
    };

    const std::vector< std::vector<bool> > returnExpectMoreExp
    {
        { true, true, true, true, true, true, true, true, true, true, true, true, true, false },
                                                             // no config data -> 14 error messages, NOTE: last expectMore expected to be false
        { true, true, true, true, true, true, true, true, true, true, true, true, true },
                                                             // add demand freeze day config     -> 13 error messages
        { true, true, true, true, true, true, true, true, true, true, true },
                                                             // add OVUV config                  -> 11 error messages
        { true, true, true, true, true, true, true, true, true },
                                                             // add TOU config                   -> 9 error messages
        { true, true, true, true, true, true, true },        // add temperature alarming config  -> 7 error messages
        { true }                                             // add channel config               -> config sent successfully
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

    for ( const CategoryDefinition & category : configurations )
    {
        resetTestState(); // note: reset test state does not erase the current configuration

        cfg.addCategory(
                Cti::Config::Category::ConstructCategory(
                        category.first,
                        category.second));

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        requestMsgsRcv.push_back( rfnRequests.size() );

        returnExpectMoreRcv.push_back( Cti::Test::extractExpectMore( returnMsgs ) );
    }

    BOOST_CHECK_EQUAL_RANGES( requestMsgsRcv, requestMsgsExp );
    BOOST_CHECK_EQUAL_RANGES( returnExpectMoreRcv, returnExpectMoreExp );
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_groupMessageCount )
{
    using boost::assign::list_of;
    using boost::assign::map_list_of;

    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420FX);

    using CategoryItems      = std::map<std::string, std::string>;
    using CategoryDefinition = std::pair<std::string, CategoryItems>;
    using ConfigInstallItems = std::vector<CategoryDefinition>;

    const ConfigInstallItems configurations = list_of<CategoryDefinition>

            // demand freeze day config
            ( CategoryDefinition(
                "demandFreeze", map_list_of
                    ( RfnStrings::demandFreezeDay, "7" )))

            // OVUV config
            ( CategoryDefinition(
                "rfnVoltage", map_list_of
                    ( RfnStrings::voltageAveragingInterval,   "60"      )
                    ( RfnStrings::OvUvEnabled,                "true"    )
                    ( RfnStrings::OvUvAlarmReportingInterval, "5"       )
                    ( RfnStrings::OvUvAlarmRepeatInterval,    "60"      )
                    ( RfnStrings::OvUvRepeatCount,            "2"       )
                    ( RfnStrings::OvThreshold,                "123.456" )
                    ( RfnStrings::UvThreshold,                "78.901"  )))

            // TOU config
            ( CategoryDefinition(
                "tou", map_list_of
                    // SCHEDULE_1
                    ( RfnStrings::Schedule1Time0, "00:00" )
                    ( RfnStrings::Schedule1Rate0, "A" )
                    ( RfnStrings::Schedule1Time1, "00:01" )
                    ( RfnStrings::Schedule1Rate1, "B" )
                    ( RfnStrings::Schedule1Time2, "10:06" )
                    ( RfnStrings::Schedule1Rate2, "C" )
                    ( RfnStrings::Schedule1Time3, "12:22" )
                    ( RfnStrings::Schedule1Rate3, "D" )
                    ( RfnStrings::Schedule1Time4, "23:33" )
                    ( RfnStrings::Schedule1Rate4, "A" )
                    ( RfnStrings::Schedule1Time5, "23:44" )
                    ( RfnStrings::Schedule1Rate5, "B" )

                    // SCHEDULE_2
                    ( RfnStrings::Schedule2Time0, "00:00" )
                    ( RfnStrings::Schedule2Rate0, "D" )
                    ( RfnStrings::Schedule2Time1, "01:23" )
                    ( RfnStrings::Schedule2Rate1, "A" )
                    ( RfnStrings::Schedule2Time2, "03:12" )
                    ( RfnStrings::Schedule2Rate2, "B" )
                    ( RfnStrings::Schedule2Time3, "04:01" )
                    ( RfnStrings::Schedule2Rate3, "C" )
                    ( RfnStrings::Schedule2Time4, "05:23" )
                    ( RfnStrings::Schedule2Rate4, "D" )
                    ( RfnStrings::Schedule2Time5, "16:28" )
                    ( RfnStrings::Schedule2Rate5, "A" )

                    // SCHEDULE_3
                    ( RfnStrings::Schedule3Time0, "00:00" )
                    ( RfnStrings::Schedule3Rate0, "C" )
                    ( RfnStrings::Schedule3Time1, "01:02" )
                    ( RfnStrings::Schedule3Rate1, "D" )
                    ( RfnStrings::Schedule3Time2, "02:03" )
                    ( RfnStrings::Schedule3Rate2, "A" )
                    ( RfnStrings::Schedule3Time3, "04:05" )
                    ( RfnStrings::Schedule3Rate3, "B" )
                    ( RfnStrings::Schedule3Time4, "05:06" )
                    ( RfnStrings::Schedule3Rate4, "C" )
                    ( RfnStrings::Schedule3Time5, "06:07" )
                    ( RfnStrings::Schedule3Rate5, "D" )

                    // SCHEDULE_4
                    ( RfnStrings::Schedule4Time0, "00:00" )
                    ( RfnStrings::Schedule4Rate0, "B" )
                    ( RfnStrings::Schedule4Time1, "00:01" )
                    ( RfnStrings::Schedule4Rate1, "C" )
                    ( RfnStrings::Schedule4Time2, "08:59" )
                    ( RfnStrings::Schedule4Rate2, "D" )
                    ( RfnStrings::Schedule4Time3, "12:12" )
                    ( RfnStrings::Schedule4Rate3, "A" )
                    ( RfnStrings::Schedule4Time4, "23:01" )
                    ( RfnStrings::Schedule4Rate4, "B" )
                    ( RfnStrings::Schedule4Time5, "23:55" )
                    ( RfnStrings::Schedule4Rate5, "C" )

                    // day table
                    ( RfnStrings::SundaySchedule,    "SCHEDULE_1" )
                    ( RfnStrings::MondaySchedule,    "SCHEDULE_1" )
                    ( RfnStrings::TuesdaySchedule,   "SCHEDULE_3" )
                    ( RfnStrings::WednesdaySchedule, "SCHEDULE_2" )
                    ( RfnStrings::ThursdaySchedule,  "SCHEDULE_4" )
                    ( RfnStrings::FridaySchedule,    "SCHEDULE_2" )
                    ( RfnStrings::SaturdaySchedule,  "SCHEDULE_3" )
                    ( RfnStrings::HolidaySchedule,   "SCHEDULE_3" )

                    // default rate
                    ( RfnStrings::DefaultTouRate, "B" )

                    // set TOU enabled
                    ( RfnStrings::touEnabled, "true" )))

            // temperature alarming config
            ( CategoryDefinition(
                "rfnTempAlarm", map_list_of
                    ( RfnStrings::TemperatureAlarmEnabled,           "true" )
                    ( RfnStrings::TemperatureAlarmRepeatInterval,    "15"   )
                    ( RfnStrings::TemperatureAlarmRepeatCount,       "3"    )
                    ( RfnStrings::TemperatureAlarmHighTempThreshold, "122"  )))

            // channel config
            ( CategoryDefinition(
                "rfnChannelConfiguration", map_list_of
                    ( RfnStrings::enableDataStreaming, "false" )
                    ( RfnStrings::voltageDataStreamingIntervalMinutes, "2" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" )
                    ( RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" )
                    ( RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" )))
            ;

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay, "7");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,                 "1");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval,  "5");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval,    "60");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount,             "2");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvThreshold,           "123.456");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_UvThreshold,            "78.901");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1, "00:01");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2, "10:06");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3, "12:22");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4, "23:33");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5, "23:44");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0, "A");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1, "00:01");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1, "B");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2, "10:06");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2, "C");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3, "12:22");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3, "D");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4, "23:33");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4, "A");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5, "23:44");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5, "B");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0, "D");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1, "01:23");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1, "A");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2, "03:12");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2, "B");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3, "04:01");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3, "C");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4, "05:23");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4, "D");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5, "16:28");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5, "A");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0, "C");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1, "01:02");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1, "D");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2, "02:03");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2, "A");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3, "04:05");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3, "B");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4, "05:06");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4, "C");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5, "06:07");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5, "D");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0, "B");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1, "00:01");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1, "C");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2, "08:59");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2, "D");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3, "12:12");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3, "A");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4, "23:01");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4, "B");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5, "23:55");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5, "C");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,    "SCHEDULE_1");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,    "SCHEDULE_1");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,   "SCHEDULE_3");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule, "SCHEDULE_2");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,  "SCHEDULE_4");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,    "SCHEDULE_2");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,  "SCHEDULE_3");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,   "SCHEDULE_3");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate, "B");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TouEnabled, "1");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandInterval, "11");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, "22");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmIsEnabled, "1");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatInterval, "15");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatCount, "3");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmHighTempThreshold, "121");

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled, "false");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, "7380");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, "27360");
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, std::vector<unsigned long>() );
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, std::vector<unsigned long>(1, 1) );

    CtiCommandParser parse("putconfig install all");

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    for ( const CategoryDefinition & category : configurations )
    {
        cfg.addCategory(
                Cti::Config::Category::ConstructCategory(
                        category.first,
                        category.second));
    }

    request->setUserMessageId(11235);

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

    BOOST_CHECK_EQUAL( 2, rfnRequests.size() );

    std::vector<bool>       expectMoreRcv;
    const std::vector<bool> expectMoreExp( 5, true );

    std::vector<std::string>       resultStringRcv;
    const std::vector<std::string> resultStringExp
    {
        "Config channelconfig is current.",
        "Config freezeday is current.",
        "Config ovuv is current.",
        "Config tou is current.",
        "2 commands queued for device"
    };

    std::vector<int>       statusRcv;
    const std::vector<int> statusExp( 5, 0 );

    for ( const auto & m : returnMsgs )
    {
        expectMoreRcv.push_back( m->ExpectMore() );
        resultStringRcv.push_back( m->ResultString() );
        statusRcv.push_back( m->Status() );
    }

    BOOST_CHECK_EQUAL_RANGES( expectMoreRcv, expectMoreExp );
    BOOST_CHECK_EQUAL_RANGES( resultStringRcv, resultStringExp );
    BOOST_CHECK_EQUAL_RANGES( statusRcv, statusExp );

    auto& command = rfnRequests.front();

    BOOST_CHECK_EQUAL( 2, dut.getGroupMessageCount(request->UserMessageId(), request->getConnectionHandle()) );

    {
        // execute
        const std::vector< unsigned char > exp
        {
            0x88, 0x00, 0x01,
                0x01, 0x07, 0x01, 0x00, 0x32, 0x00, 0x28, 0x0f, 0x03
        };

        Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );

        // decode -- success response
        const std::vector< unsigned char > response
        {
            0x89, 0x00, 0x00, 0x00
        };

        const auto results = command->handleResponse(decode_time, response);

        BOOST_REQUIRE_EQUAL(results.size(), 1);

        const auto & result = results.front();

        BOOST_CHECK_EQUAL( result.description, "Set Temperature Alarm Configuration Request:"
                                               "\nStatus: Success (0)" );

        dut.extractCommandResult(*command);

        //  replicating PIL's decrement
        dut.decrementGroupMessageCount(request->UserMessageId(), request->getConnectionHandle());
    }

    BOOST_CHECK_EQUAL( 1, dut.getGroupMessageCount(request->UserMessageId(), request->getConnectionHandle() ) );
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_all_disconnect_meter )
{
    using boost::assign::list_of;
    using boost::assign::map_list_of;

    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420CD);

    using CategoryItems      = std::map<std::string, std::string>;
    using CategoryDefinition = std::pair<std::string, CategoryItems>;
    using ConfigInstallItems = std::vector<CategoryDefinition>;

    const ConfigInstallItems configurations = list_of<CategoryDefinition>

            ( CategoryDefinition( // remote disconnect config
                "rfnDisconnectConfiguration", map_list_of
                    ( RfnStrings::DisconnectMode, "CYCLING" )
                    ( RfnStrings::ConnectMinutes, "100" )
                    ( RfnStrings::DisconnectMinutes, "60" )))

            ( CategoryDefinition( // demand freeze day config
                "demandFreeze", map_list_of
                    ( RfnStrings::demandFreezeDay, "7" )))

            ( CategoryDefinition( // OVUV config
                "rfnVoltage", map_list_of
                    ( RfnStrings::voltageAveragingInterval,   "60"      )
                    ( RfnStrings::OvUvEnabled,                "true"    )
                    ( RfnStrings::OvUvAlarmReportingInterval, "5"       )
                    ( RfnStrings::OvUvAlarmRepeatInterval,    "60"      )
                    ( RfnStrings::OvUvRepeatCount,            "2"       )
                    ( RfnStrings::OvThreshold,                "123.456" )
                    ( RfnStrings::UvThreshold,                "78.901"  )))

            ( CategoryDefinition( // TOU config
                "tou", map_list_of
                    // SCHEDULE_1
                    ( RfnStrings::Schedule1Time0, "00:00" )
                    ( RfnStrings::Schedule1Time1, "00:01" )
                    ( RfnStrings::Schedule1Time2, "10:06" )
                    ( RfnStrings::Schedule1Time3, "12:22" )
                    ( RfnStrings::Schedule1Time4, "23:33" )
                    ( RfnStrings::Schedule1Time5, "23:44" )

                    ( RfnStrings::Schedule1Rate0, "A" )
                    ( RfnStrings::Schedule1Rate1, "B" )
                    ( RfnStrings::Schedule1Rate2, "C" )
                    ( RfnStrings::Schedule1Rate3, "D" )
                    ( RfnStrings::Schedule1Rate4, "A" )
                    ( RfnStrings::Schedule1Rate5, "B" )

                    // SCHEDULE_2
                    ( RfnStrings::Schedule2Time0, "00:00" )
                    ( RfnStrings::Schedule2Time1, "01:23" )
                    ( RfnStrings::Schedule2Time2, "03:12" )
                    ( RfnStrings::Schedule2Time3, "04:01" )
                    ( RfnStrings::Schedule2Time4, "05:23" )
                    ( RfnStrings::Schedule2Time5, "16:28" )

                    ( RfnStrings::Schedule2Rate0, "D" )
                    ( RfnStrings::Schedule2Rate1, "A" )
                    ( RfnStrings::Schedule2Rate2, "B" )
                    ( RfnStrings::Schedule2Rate3, "C" )
                    ( RfnStrings::Schedule2Rate4, "D" )
                    ( RfnStrings::Schedule2Rate5, "A" )

                    // SCHEDULE_3
                    ( RfnStrings::Schedule3Time0, "00:00" )
                    ( RfnStrings::Schedule3Time1, "01:02" )
                    ( RfnStrings::Schedule3Time2, "02:03" )
                    ( RfnStrings::Schedule3Time3, "04:05" )
                    ( RfnStrings::Schedule3Time4, "05:06" )
                    ( RfnStrings::Schedule3Time5, "06:07" )

                    ( RfnStrings::Schedule3Rate0, "C" )
                    ( RfnStrings::Schedule3Rate1, "D" )
                    ( RfnStrings::Schedule3Rate2, "A" )
                    ( RfnStrings::Schedule3Rate3, "B" )
                    ( RfnStrings::Schedule3Rate4, "C" )
                    ( RfnStrings::Schedule3Rate5, "D" )

                    // SCHEDULE_4
                    ( RfnStrings::Schedule4Time0, "00:00" )
                    ( RfnStrings::Schedule4Time1, "00:01" )
                    ( RfnStrings::Schedule4Time2, "08:59" )
                    ( RfnStrings::Schedule4Time3, "12:12" )
                    ( RfnStrings::Schedule4Time4, "23:01" )
                    ( RfnStrings::Schedule4Time5, "23:55" )

                    ( RfnStrings::Schedule4Rate0, "B" )
                    ( RfnStrings::Schedule4Rate1, "C" )
                    ( RfnStrings::Schedule4Rate2, "D" )
                    ( RfnStrings::Schedule4Rate3, "A" )
                    ( RfnStrings::Schedule4Rate4, "B" )
                    ( RfnStrings::Schedule4Rate5, "C" )

                    // day table
                    ( RfnStrings::SundaySchedule,    "SCHEDULE_1" )
                    ( RfnStrings::MondaySchedule,    "SCHEDULE_1" )
                    ( RfnStrings::TuesdaySchedule,   "SCHEDULE_3" )
                    ( RfnStrings::WednesdaySchedule, "SCHEDULE_2" )
                    ( RfnStrings::ThursdaySchedule,  "SCHEDULE_4" )
                    ( RfnStrings::FridaySchedule,    "SCHEDULE_2" )
                    ( RfnStrings::SaturdaySchedule,  "SCHEDULE_3" )
                    ( RfnStrings::HolidaySchedule,   "SCHEDULE_3" )

                    // default rate
                    ( RfnStrings::DefaultTouRate, "B" )

                    // set TOU enabled
                    ( RfnStrings::touEnabled, "true" )))

            ( CategoryDefinition( // temperature alarming config
                "rfnTempAlarm", map_list_of
                    ( RfnStrings::TemperatureAlarmEnabled,           "true" )
                    ( RfnStrings::TemperatureAlarmRepeatInterval,    "15"   )
                    ( RfnStrings::TemperatureAlarmRepeatCount,       "3"    )
                    ( RfnStrings::TemperatureAlarmHighTempThreshold, "50"   )))

            ( CategoryDefinition( // channel config
                "rfnChannelConfiguration", map_list_of
                    ( RfnStrings::enableDataStreaming, "false" )
                    ( RfnStrings::voltageDataStreamingIntervalMinutes, "2" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" )
                    ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" )
                    ( RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" )
                    ( RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" )))
            ;

    const std::vector<int> requestMsgsExp
    {
        0,      // no config data                   -> no request
        1,      // add remote disconnect config     -> +1 request
        2,      // add demand freeze day config     -> +1 request
        8,      // add OVUV config                  -> +6 request
        10,     // add TOU config                   -> +1 request
        11,     // add temperature alarming config  -> +1 request
        15,     // add channel config               -> +4 request
    };

    const std::vector< std::vector<bool> > returnExpectMoreExp
    {
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },
                                                                   // no config data                  -> 16 error messages, NOTE: last expectMore expected to be false
        { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true },
                                                                   // add remote disconnect config    -> 15 error messages
        { true, true, true, true, true, true, true, true, true, true, true, true, true },
                                                                   // add demand freeze day config    -> 13 error messages
        { true, true, true, true, true, true, true, true, true, true, true },
                                                                   // add OVUV config                 -> 11 error messages
        { true, true, true, true, true, true, true, true, true },  // add TOU config                  -> 9 error messages
        { true, true, true, true, true, true, true },              // add temperature alarming config -> 7 error messages
        { true }                                                   // add channel config              -> config sent successfully
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

    for ( const CategoryDefinition & category : configurations )
    {
        resetTestState(); // note: reset test state does not erase the current configuration

        cfg.addCategory(
                Cti::Config::Category::ConstructCategory(
                        category.first,
                        category.second));

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        requestMsgsRcv.push_back( rfnRequests.size() );

        returnExpectMoreRcv.push_back( Cti::Test::extractExpectMore( returnMsgs ) );
    }

    BOOST_CHECK_EQUAL_RANGES( requestMsgsRcv, requestMsgsExp );
    BOOST_CHECK_EQUAL_RANGES( returnExpectMoreRcv, returnExpectMoreExp );
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_voltage_profile )
{
    test_RfnResidentialVoltageDevice    dut;

    CtiCommandParser parse("putconfig emetcon voltage profile demandinterval 17 lpinterval 34");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       202 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "No Method or Invalid Command." );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_voltage_profile_enable )
{
    test_RfnResidentialVoltageDevice    dut;

    CtiCommandParser parse("putconfig emetcon voltage profile enable");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        auto& command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp
        {
            0x68, 0x03, 0x00
        };

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_voltage_profile_disable )
{
    test_RfnResidentialVoltageDevice    dut;

    CtiCommandParser parse("putconfig emetcon voltage profile disable");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        auto& command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp
        {
            0x68, 0x02, 0x00
        };

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_getconfig_voltage_profile )
{
    test_RfnResidentialVoltageDevice    dut;

    CtiCommandParser parse("getconfig voltage profile");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       202 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "No Method or Invalid Command." );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_getvalue_voltage_profile_state )
{
    test_RfnResidentialVoltageDevice    dut;

    CtiCommandParser parse("getconfig voltage profile state");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        auto& command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp
        {
            0x68, 0x04, 0x00
        };

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_voltageprofile_enable )
{
    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420FX);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::enableDataStreaming,   "true" );

    using Bytes = std::vector<unsigned char>;

    {
        CtiCommandParser parse("putconfig install voltageprofile");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            {
                Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                Bytes exp
                {
                    0x68, 0x06, 0x00
                };

                BOOST_CHECK_EQUAL( rcv, exp );
            }

            {
                Bytes response
                {
                    0x69, 0x06, 0x00, 0x00
                };

                const auto results = command->handleResponse( decode_time, response );

                BOOST_REQUIRE_EQUAL( results.size(), 1 );

                const auto & result = results.front();

                BOOST_CHECK_EQUAL( result.description, "Load Profile Set Permanent Recording Request:"
                                                       "\nStatus: Success (0)" );
            }

            dut.extractCommandResult( *command );

            long test_voltageProfileEnabled;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled, test_voltageProfileEnabled ));
            BOOST_CHECK_EQUAL( static_cast<bool>(test_voltageProfileEnabled), true );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidentialVoltage_putconfig_install_voltageprofile_disable )
{
    test_RfnResidentialVoltageDevice    dut;

    dut.setDeviceType(TYPE_RFN420FX);

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::enableDataStreaming,   "false" );

    using Bytes = std::vector<unsigned char>;

    {
        CtiCommandParser parse("putconfig install voltageprofile");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            {
                Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                Bytes exp
                {
                    0x68, 0x02, 0x00
                };

                BOOST_CHECK_EQUAL( rcv, exp );
            }

            {
                Bytes response
                {
                    0x69, 0x02, 0x00, 0x00
                };

                const auto results = command->handleResponse( decode_time, response );

                BOOST_REQUIRE_EQUAL( results.size(), 1 );

                const auto & result = results.front();

                BOOST_CHECK_EQUAL( result.description, "Load Profile Set Permanent Recording Request:"
                                                       "\nStatus: Success (0)" );
            }

            dut.extractCommandResult( *command );

            long test_voltageProfileEnabled;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabled, test_voltageProfileEnabled ));
            BOOST_CHECK_EQUAL( static_cast<bool>(test_voltageProfileEnabled), false );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_config_notification )
{
    auto cmd = Cti::Devices::Commands::RfnCommand::handleUnsolicitedReport(execute_time, test_cmd_rfn_ConfigNotification::payload);

    BOOST_REQUIRE(cmd);

    Cti::Devices::RfnResidentialVoltageDevice dut;

    dut.extractCommandResult(*cmd);

    using PI = CtiTableDynamicPaoInfo;
    using PIIdx = CtiTableDynamicPaoInfoIndexed;

    Cti::Test::PaoInfoValidator dpiExpected[] 
    {
        { PI::Key_RFN_TouEnabled, 1 },

        { PI::Key_RFN_MondaySchedule,    "SCHEDULE_2" },
        { PI::Key_RFN_TuesdaySchedule,   "SCHEDULE_3" },
        { PI::Key_RFN_WednesdaySchedule, "SCHEDULE_4" },
        { PI::Key_RFN_ThursdaySchedule,  "SCHEDULE_4" },
        { PI::Key_RFN_FridaySchedule,    "SCHEDULE_3" },
        { PI::Key_RFN_SaturdaySchedule,  "SCHEDULE_2" },
        { PI::Key_RFN_SundaySchedule,    "SCHEDULE_1" },
        { PI::Key_RFN_HolidaySchedule,   "SCHEDULE_1" },

        { PI::Key_RFN_DefaultTOURate, "B" },

        { PI::Key_RFN_Schedule1Time1, "00:03" },
        { PI::Key_RFN_Schedule1Time2, "00:04" },
        { PI::Key_RFN_Schedule1Time3, "00:08" },
        { PI::Key_RFN_Schedule1Time4, "00:09" },
        { PI::Key_RFN_Schedule1Time5, "00:14" },
        { PI::Key_RFN_Schedule2Time1, "00:09" },
        { PI::Key_RFN_Schedule2Time2, "00:11" },
        { PI::Key_RFN_Schedule2Time3, "00:17" },
        { PI::Key_RFN_Schedule2Time4, "00:22" },
        { PI::Key_RFN_Schedule2Time5, "00:25" },
        { PI::Key_RFN_Schedule3Time1, "00:05" },
        { PI::Key_RFN_Schedule3Time2, "00:13" },
        { PI::Key_RFN_Schedule3Time3, "00:22" },
        { PI::Key_RFN_Schedule3Time4, "00:29" },
        { PI::Key_RFN_Schedule3Time5, "00:38" },
        { PI::Key_RFN_Schedule4Time1, "00:00" },
        { PI::Key_RFN_Schedule4Time2, "00:00" },
        { PI::Key_RFN_Schedule4Time3, "00:00" },
        { PI::Key_RFN_Schedule4Time4, "00:00" },
        { PI::Key_RFN_Schedule4Time5, "00:00" },

        { PI::Key_RFN_Schedule1Rate0, "B" },
        { PI::Key_RFN_Schedule1Rate1, "A" },
        { PI::Key_RFN_Schedule1Rate2, "D" },
        { PI::Key_RFN_Schedule1Rate3, "C" },
        { PI::Key_RFN_Schedule1Rate4, "B" },
        { PI::Key_RFN_Schedule1Rate5, "A" },
        { PI::Key_RFN_Schedule2Rate0, "D" },
        { PI::Key_RFN_Schedule2Rate1, "C" },
        { PI::Key_RFN_Schedule2Rate2, "B" },
        { PI::Key_RFN_Schedule2Rate3, "A" },
        { PI::Key_RFN_Schedule2Rate4, "D" },
        { PI::Key_RFN_Schedule2Rate5, "C" },
        { PI::Key_RFN_Schedule3Rate0, "C" },
        { PI::Key_RFN_Schedule3Rate1, "B" },
        { PI::Key_RFN_Schedule3Rate2, "A" },
        { PI::Key_RFN_Schedule3Rate3, "D" },
        { PI::Key_RFN_Schedule3Rate4, "C" },
        { PI::Key_RFN_Schedule3Rate5, "B" },
        { PI::Key_RFN_Schedule4Rate0, "A" },
        { PI::Key_RFN_Schedule4Rate1, "D" },
        { PI::Key_RFN_Schedule4Rate2, "C" },
        { PI::Key_RFN_Schedule4Rate3, "B" },
        { PI::Key_RFN_Schedule4Rate4, "A" },
        { PI::Key_RFN_Schedule4Rate5, "D" },

        { PI::Key_RFN_Holiday1, "03/14/2018" },
        { PI::Key_RFN_Holiday2, "06/27/2018" },
        { PI::Key_RFN_Holiday3, "02/07/2018" },

        { PI::Key_RFN_VoltageAveragingInterval, 105 },
        { PI::Key_RFN_LoadProfileInterval,       11 },
        { PI::Key_RFN_VoltageProfileEnabled,  false },
        { PI::Key_RFN_VoltageProfileEnabledUntil, 0x51234567 },

        { PI::Key_RFN_DemandFreezeDay, 32 },

        { PI::Key_RFN_DemandInterval, 6 },

        { PI::Key_RFN_OvUvEnabled,                 1 },
        { PI::Key_RFN_OvThreshold,            218959.117 },
        { PI::Key_RFN_UvThreshold,            235802.126 },
        { PI::Key_RFN_OvUvAlarmReportingInterval, 14 },
        { PI::Key_RFN_OvUvAlarmRepeatInterval,     3 },
        { PI::Key_RFN_OvUvRepeatCount,             2 },

        { PI::Key_RFN_DisconnectMode, "DEMAND_THRESHOLD" },
        { PI::Key_RFN_ReconnectParam, "IMMEDIATE" },
        { PI::Key_RFN_DisconnectDemandInterval, 24 },
        { PI::Key_RFN_DemandThreshold,           3.1 },
        { PI::Key_RFN_ConnectDelay,             17 },
        { PI::Key_RFN_MaxDisconnects,            7 },

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

