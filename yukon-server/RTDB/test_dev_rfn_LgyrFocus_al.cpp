#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn_LgyrFocus_al.h"
#include "cmd_rfn.h"
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"


struct test_state_rfn_lgyr_focus_al
{
    CtiRequestMsg request;
    Cti::Devices::RfnDevice::ReturnMsgList  returnMsgs;
    Cti::Devices::RfnDevice::RfnCommandList rfnRequests;
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfn_lgyr_focus_al() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }
};


namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char> &v);
}

namespace test_cmd_rfn_ConfigNotification {
    extern const std::vector<uint8_t> payload;
}

const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );
const CtiTime decode_time ( CtiDate( 27, 8, 2013 ) , 16 );

BOOST_FIXTURE_TEST_SUITE( test_dev_rfn_lgyr_focus_al, test_state_rfn_lgyr_focus_al )

BOOST_AUTO_TEST_CASE( test_putconfig_display )
{
    Cti::Devices::RfnLgyrFocusAlDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "8" );
    cfg.insertValue( Cti::Config::RfnStrings::DisplayDigits, "5" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem01, "DELIVERED_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem02, "REVERSE_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem03, "TOTAL_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem04, "NET_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem05, "DIAGNOSTIC_FLAGS" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem06, "ALL_SEGMENTS" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem07, "FIRMWARE_VERSION" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem08, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem09, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem10, "SLOT_DISABLED" );

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
    }

    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        auto& command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x72)(0x00)  //  LCD configuration
                    (0x07)  //  7 metrics
                    (0x08)  //  LCD cycle time
                    (0x01)(0x30)(0x31)
                    (0x05)(0x30)(0x32)
                    (0x09)(0x30)(0x33)
                    (0x0d)(0x30)(0x34)
                    (0x10)(0x30)(0x35)
                    (0x11)(0x30)(0x36)
                    (0x12)(0x30)(0x37);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x73)
                    (0x00);

            const auto results = command->handleResponse( decode_time, response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            const std::string exp =
                    "LCD Configuration Write Request:"
                    "\nStatus: Success (0)"
                    "\nDisplay items sent:"
                    "\nLCD cycle time : 8 seconds"
                    "\nDisplay metric 1 : Delivered kWh (5 digit)"
                    "\nDisplay metric 2 : Reverse kWh (5 digit)"
                    "\nDisplay metric 3 : Total kWh (5 digit)"
                    "\nDisplay metric 4 : Net kWh (5 digit)"
                    "\nDisplay metric 5 : Diagnostic flags"
                    "\nDisplay metric 6 : All Segments"
                    "\nDisplay metric 7 : Firmware version";

            BOOST_CHECK_EQUAL(result.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string metric_name;
        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem01, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "DELIVERED_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem02, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "REVERSE_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem03, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "TOTAL_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem04, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "NET_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem05, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "DIAGNOSTIC_FLAGS");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem06, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "ALL_SEGMENTS");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem07, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "FIRMWARE_VERSION");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem08, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "SLOT_DISABLED");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem09, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "SLOT_DISABLED");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem10, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "SLOT_DISABLED");

        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime), 8);
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_display_out_of_order_slot_disabled )
{
    Cti::Devices::RfnLgyrFocusAlDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "8" );
    cfg.insertValue( Cti::Config::RfnStrings::DisplayDigits, "5" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem01, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem02, "DELIVERED_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem03, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem04, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem05, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem06, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem07, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem08, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem09, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem10, "SLOT_DISABLED" );

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
    }

    BOOST_REQUIRE_EQUAL( 2, returnMsgs.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       284 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Device \"\" - Invalid value (DELIVERED_KWH) seen after SLOT_DISABLED for config key \"displayItem2\"" );
    }

    BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
}

BOOST_AUTO_TEST_CASE( test_config_notification )
{
    auto cmd = Cti::Devices::Commands::RfnCommand::handleNodeOriginated(execute_time, test_cmd_rfn_ConfigNotification::payload);

    BOOST_REQUIRE(cmd);

    Cti::Devices::RfnLgyrFocusAlDevice dut;

    dut.extractCommandResult(*cmd);

    using PI = CtiTableDynamicPaoInfo;
    using PIIdx = CtiTableDynamicPaoInfoIndexed;

    Cti::Test::PaoInfoValidator dpiExpected[] 
    {
        { PI::Key_RFN_LcdCycleTime, 6 },
        { PI::Key_DisplayItem01, "DELIVERED_KWH_6X1" },
        { PI::Key_DisplayItem02, "DELIVERED_KWH_4X1" },
        { PI::Key_DisplayItem03, "TOTAL_KWH_6X1" },
        { PI::Key_DisplayItem04, "SLOT_DISABLED" },
        { PI::Key_DisplayItem05, "SLOT_DISABLED" },
        { PI::Key_DisplayItem06, "SLOT_DISABLED" },
        { PI::Key_DisplayItem07, "SLOT_DISABLED" },
        { PI::Key_DisplayItem08, "SLOT_DISABLED" },
        { PI::Key_DisplayItem09, "SLOT_DISABLED" },
        { PI::Key_DisplayItem10, "SLOT_DISABLED" },

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