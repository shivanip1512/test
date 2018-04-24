#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn_LgyrFocus_al.h"
#include "cmd_rfn.h"
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
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x0c, 
        //  TLV 1
        0x00, 0x01,  //  TOU Enable/Disable
        0x00, 0x01,
            //  TOU enable
            0x01,
        //  TLV 2
        0x00, 0x02,  //  TOU Schedule
        0x00, 0x38,
            //  Day table
            0x88, 0x36, 0x05,  //  1, 2, 3, 4, 4, 3, 2, 1
            //  Schedule 1 switch times
            0x00, 0x03, 
            0x00, 0x01, 
            0x00, 0x04, 
            0x00, 0x01, 
            0x00, 0x05, 
            //  Schedule 2 switch times
            0x00, 0x09, 
            0x00, 0x02, 
            0x00, 0x06, 
            0x00, 0x05, 
            0x00, 0x03, 
            //  Schedule 3 switch times
            0x00, 0x05, 
            0x00, 0x08, 
            0x00, 0x09, 
            0x00, 0x07, 
            0x00, 0x09, 
            //  Schedule 4 switch times
            0x00, 0x03, 
            0x00, 0x02, 
            0x00, 0x03, 
            0x00, 0x08, 
            0x00, 0x04, 
            //  Schedule 1 rates
            0xc1, 0x14, 0x00,  //  A B C D A B
            //  Schedule 2 rates
            0x53, 0x30, 0x01,  //  C D A B C D
            //  Schedule 3 rates
            0x0a, 0xa6, 0x00,  //  B C D A B C
            //  Schedule 4 rates
            0x98, 0x82, 0x01,  //  D A B C D A
            //  default rate
            0x01,  //  B
        //  TLV 3
        0x00, 0x03,  //  TOU Holiday
        0x00, 0x0c,
            //  Holiday 1
            0x5A, 0xA9, 0x3B, 0x26, //  
            //  Holiday 2
            0x5B, 0x34, 0x53, 0x9D, //
            //  Holiday 3
            0x5A, 0x7B, 0x45, 0x42, 
        //  TLV 4
        0x00, 0x04,  //  Demand Freeze Day
        0x00, 0x01,
            0x20,  //  32
        //  TLV 5
        0x00, 0x05,  //  Interval recording
        0x00, 0x11,
            0x20, 0x1c, 0x00, 0x00,  //  7200
            0x80, 0x51, 0x01, 0x00,  //  86400
            0x04,  //  4 metrics
            0x01, 0x00,
            0x02, 0x00,
            0x03, 0x00,
            0x04, 0x00,
            //  TLV 6
        0x00, 0x06,  // Channel selection
        0x00, 0x09,
            0x04,
            0x05, 0x00, 
            0x06, 0x00, 
            0x07, 0x00, 
            0x08, 0x00, 
        //  TLV 7
        0x00, 0x07,  //  Disconnect
        0x00, 0x06,
            0x02,  //  Demand threshold
            0x01,  //  Reconnect method
            0x18,  //  Demand interval
            0x1f,  //  Demand threshold
            0x11,  //  Connect delay
            0x07,  //  Max disconnects
        //  TLV 8
        0x00, 0x08,  //  Voltage profile
        0x00, 0x02,
            0x07,  //  Voltage demand interval (15 second increments)
            0x0b,  //  Voltage load profile interval (minutes)
        //  TLV 9
        0x00, 0x09,  //  C2SX Display
        0x00, 0x0d,
            0x06,
            0x00, 0x04,
            0x01, 0x08,
            0x02, 0x0c,
            0xfd, 0x05,
            0xfe, 0x07,
            0xff, 0x00,
        //  TLV 10
        0x00, 0x0a,  //  Focus AL Display
        0x00, 0x0b,
            0x03,
            0x06,
            0x00, 0x3e, 0x55,
            0x02, 0x41, 0x3b,
            0x08, 0x47, 0x47,
        //  TLV 11
        0x00, 0x0b,  //  OV/UV configuration
        0x00, 0x12,
            0x7f,        //  Meter ID
            0x01, 0xff,  //  Event ID
            0x01,        //  OV/UV Enable/disable
            0x0e,        //  New alarm reporting interval
            0x03,        //  Alarm repeat interval
            0x02,        //  Set alarm repeat count
            0x05,        //  Clear alarm repeat count
            0x0d,        //  Severity
            0x0e, 0x0e, 0x0e, 0x0e,  //  Set threshold value
            0x0e,        //  Unit of measure
            0x0e, 0x0e,  //  UOM modifier 1
            0x0e, 0x0e,  //  UOM modifier 2
        //  TLV 12
        0x00, 0x0c,  //  Temperature configuration
        0x00, 0x07,
            0x01,  //  Enable/disable
            0x01, 0x17,  //  high temp threshold
            0x01, 0x03,  //  low temp threshold
            0x07,  //  repeat interval
            0x0b,  //  Max repeats
    };

    auto cmd = Cti::Devices::Commands::RfnCommand::handleUnsolicitedReport(execute_time, payload);

    BOOST_REQUIRE(cmd);

    Cti::Devices::RfnLgyrFocusAlDevice dut;

    cmd->invokeResultHandler(dut);

    using PI = CtiTableDynamicPaoInfo;
    using PIIdx = CtiTableDynamicPaoInfoIndexed;

    auto dpiString = [&dut](PI::PaoInfoKeys k) { 
            std::string s; 
            dut.getDynamicInfo(k, s); 
            return s; };

    auto dpiLong = [&dut](PI::PaoInfoKeys k) { 
            return dut.getDynamicInfo(k); };

    auto dpiDouble = [&dut](PI::PaoInfoKeys k) { 
        double d; 
        dut.getDynamicInfo(k, d); 
        return d; };

    BOOST_CHECK_EQUAL(overrideDynamicPaoInfoManager.dpi->dirtyEntries.begin()->second.size(), 68);

    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem01), "DELIVERED_KWH_6X1");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem02), "DELIVERED_KWH_4X1");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem03), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem04), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem05), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem06), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem07), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem08), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem09), "SLOT_DISABLED");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_DisplayItem10), "SLOT_DISABLED");

    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_TouEnabled), 1);
    
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_MondaySchedule),    "SCHEDULE_2");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_TuesdaySchedule),   "SCHEDULE_3");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_WednesdaySchedule), "SCHEDULE_4");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_ThursdaySchedule),  "SCHEDULE_4");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_FridaySchedule),    "SCHEDULE_3");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_SaturdaySchedule),  "SCHEDULE_2");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_SundaySchedule),    "SCHEDULE_1");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_HolidaySchedule),   "SCHEDULE_1");

    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_DefaultTOURate), "B");

    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule1Rate0), "B");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule1Rate1), "A");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule1Rate2), "D");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule1Rate3), "C");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule1Rate4), "B");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule1Rate5), "A");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule2Rate0), "D");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule2Rate1), "C");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule2Rate2), "B");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule2Rate3), "A");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule2Rate4), "D");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule2Rate5), "C");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule3Rate0), "C");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule3Rate1), "B");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule3Rate2), "A");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule3Rate3), "D");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule3Rate4), "C");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule3Rate5), "B");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule4Rate0), "A");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule4Rate1), "D");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule4Rate2), "C");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule4Rate3), "B");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule4Rate4), "A");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Schedule4Rate5), "D");

    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Holiday1), "03/14/2018");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Holiday2), "06/27/2018");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_Holiday3), "02/07/2018");

    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_VoltageAveragingInterval), 105);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_LoadProfileInterval), 11);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_DemandFreezeDay), 32);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_OvUvEnabled), 1);
    BOOST_CHECK_EQUAL(dpiDouble(PI::Key_RFN_UvThreshold), 235802.126);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_OvUvAlarmReportingInterval), 14);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_OvUvAlarmRepeatInterval), 3);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_OvUvRepeatCount), 2);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_LcdCycleTime), 6);
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_DisconnectMode), "DEMAND_THRESHOLD");
    BOOST_CHECK_EQUAL(dpiString(PI::Key_RFN_ReconnectParam), "IMMEDIATE");
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_DisconnectDemandInterval), 24);
    BOOST_CHECK_EQUAL(dpiDouble(PI::Key_RFN_DemandThreshold), 3.1);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_ConnectDelay), 17);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_MaxDisconnects), 7);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_TempAlarmIsEnabled), 1);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_TempAlarmRepeatInterval), 7);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_TempAlarmRepeatCount), 11);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_TempAlarmHighTempThreshold), 5889);
    
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_RecordingIntervalSeconds), 7200);
    BOOST_CHECK_EQUAL(dpiLong(PI::Key_RFN_ReportingIntervalSeconds), 86400);
    const auto midnightExpected = { 5, 6, 7, 8 };
    const auto midnightActual = dut.findDynamicInfo<unsigned long>(PIIdx::Key_RFN_MidnightMetrics);
    BOOST_REQUIRE(midnightActual);
    BOOST_CHECK_EQUAL_RANGES(midnightActual.value(), midnightExpected);
    const auto intervalExpected = { 1, 2, 3, 4 };
    const auto intervalActual = dut.findDynamicInfo<unsigned long>(PIIdx::Key_RFN_IntervalMetrics);
    BOOST_REQUIRE(intervalActual);
    BOOST_CHECK_EQUAL_RANGES(intervalActual.value(), intervalExpected);
}

BOOST_AUTO_TEST_SUITE_END()