#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn410centron.h"
#include "cmd_rfn.h"
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"


struct test_Rfn410CentronDevice : Cti::Devices::Rfn410CentronDevice
{

};

struct test_state_rfn410centron
{
    CtiRequestMsg request;
    Cti::Devices::RfnDevice::ReturnMsgList  returnMsgs;
    Cti::Devices::RfnDevice::RfnCommandList rfnRequests;
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfn410centron() :
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

BOOST_FIXTURE_TEST_SUITE( test_dev_rfn410centron, test_state_rfn410centron )

BOOST_AUTO_TEST_CASE( test_dev_rfn410Centron_putconfig_display )
{
    test_Rfn410CentronDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "0" );
    cfg.insertValue( Cti::Config::RfnStrings::DisconnectDisplayDisabled, "true" );  //  make sure it's ignored, even if available
    cfg.insertValue( Cti::Config::RfnStrings::DisplayDigits, "5" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem01, "PEAK_VOLTAGE_DATE" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem02, "PEAK_VOLTAGE_TIME" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem03, "MINIMUM_VOLTAGE" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem04, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem05, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem06, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem07, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem08, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem09, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem10, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem11, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem12, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem13, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem14, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem15, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem16, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem17, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem18, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem19, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem20, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem21, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem22, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem23, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem24, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem25, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem26, "SLOT_DISABLED" );

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    auto rfnRequest_itr = rfnRequests.begin();
    {
        auto& command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x70)  //  LCD configuration
                    (0x00)  //  write
                    (0x1c)  //  28 metrics (26 + 2 for display digits and cycle delay, but NOT disconnect)
                    (0x00)(0x10)(0x01)(0x11)(0x02)(0x12)(0x03)(0x00)
                    (0x04)(0x00)(0x05)(0x00)(0x06)(0x00)(0x07)(0x00)
                    (0x08)(0x00)(0x09)(0x00)(0x0a)(0x00)(0x0b)(0x00)
                    (0x0c)(0x00)(0x0d)(0x00)(0x0e)(0x00)(0x0f)(0x00)
                    (0x10)(0x00)(0x11)(0x00)(0x12)(0x00)(0x13)(0x00)
                    (0x14)(0x00)(0x15)(0x00)(0x16)(0x00)(0x17)(0x00)
                    (0x18)(0x00)(0x19)(0x00)
                    (0xfd)(0x05)(0xfe)(0x00);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x71)(0x00)(0x00);

            const auto results = command->handleResponse( decode_time, response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            const std::string exp =
                    "Set LCD Configuration Request:"
                    "\nDisplay metrics successfully set:"
                    "\nDisplay metric 1: Date of Peak Voltage"
                    "\nDisplay metric 2: Time of Peak Voltage"
                    "\nDisplay metric 3: Min Voltage (V)"
                    "\nDisplay metric 4: Metric Slot Disabled"
                    "\nDisplay metric 5: Metric Slot Disabled"
                    "\nDisplay metric 6: Metric Slot Disabled"
                    "\nDisplay metric 7: Metric Slot Disabled"
                    "\nDisplay metric 8: Metric Slot Disabled"
                    "\nDisplay metric 9: Metric Slot Disabled"
                    "\nDisplay metric 10: Metric Slot Disabled"
                    "\nDisplay metric 11: Metric Slot Disabled"
                    "\nDisplay metric 12: Metric Slot Disabled"
                    "\nDisplay metric 13: Metric Slot Disabled"
                    "\nDisplay metric 14: Metric Slot Disabled"
                    "\nDisplay metric 15: Metric Slot Disabled"
                    "\nDisplay metric 16: Metric Slot Disabled"
                    "\nDisplay metric 17: Metric Slot Disabled"
                    "\nDisplay metric 18: Metric Slot Disabled"
                    "\nDisplay metric 19: Metric Slot Disabled"
                    "\nDisplay metric 20: Metric Slot Disabled"
                    "\nDisplay metric 21: Metric Slot Disabled"
                    "\nDisplay metric 22: Metric Slot Disabled"
                    "\nDisplay metric 23: Metric Slot Disabled"
                    "\nDisplay metric 24: Metric Slot Disabled"
                    "\nDisplay metric 25: Metric Slot Disabled"
                    "\nDisplay metric 26: Metric Slot Disabled"
                    "\nLCD cycle time: (default)"
                    "\nDisplay digits: 5x1";

            BOOST_CHECK_EQUAL(result.description, exp);
        }

        dut.extractCommandResult( *command );

        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem01), 0x10);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem02), 0x11);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem03), 0x12);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem04), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem05), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem06), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem07), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem08), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem09), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem10), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem11), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem12), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem13), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem14), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem15), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem16), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem17), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem18), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem19), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem20), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem21), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem22), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem23), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem24), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem25), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem26), 0x00);

        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits), 0x05);
        BOOST_CHECK( ! dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled));
    }
}

BOOST_AUTO_TEST_CASE( test_config_notification )
{
    auto cmd = Cti::Devices::Commands::RfnCommand::handleNodeOriginated(execute_time, test_cmd_rfn_ConfigNotification::payload);

    BOOST_REQUIRE(cmd);

    Cti::Devices::Rfn410CentronDevice dut;

    dut.extractCommandResult(*cmd);

    using PI = CtiTableDynamicPaoInfo;
    using PIIdx = CtiTableDynamicPaoInfoIndexed;

    Cti::Test::PaoInfoValidator dpiExpected[] 
    {
        { PI::Key_RFN_LcdCycleTime,     7 },
        { PI::Key_RFN_LcdDisplayDigits, 5 },
        { PI::Key_DisplayItem01, "4" },
        { PI::Key_DisplayItem02, "8" },
        { PI::Key_DisplayItem03, "12" },

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
        { PI::Key_RFN_VoltageProfileEnabled, false },
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
        { PI::Key_RFN_ReportingIntervalSeconds, 86400 }
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