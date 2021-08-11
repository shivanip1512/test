#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "std_helper.h"
#include "boost_test_helpers.h"
#include "cmd_rfn_TouConfiguration.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnTouConfigurationCommand;
using Cti::Devices::Commands::RfnTouScheduleConfigurationCommand;
using Cti::Devices::Commands::RfnTouScheduleGetConfigurationCommand;
using Cti::Devices::Commands::RfnTouScheduleSetConfigurationCommand;
using Cti::Devices::Commands::RfnTouHolidayConfigurationCommand;
using Cti::Devices::Commands::RfnTouStateConfigurationCommand;
using Cti::Devices::Commands::RfnTouSetHolidayActiveCommand;
using Cti::Devices::Commands::RfnTouCancelHolidayActiveCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_TouConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE( test_RfnTouScheduleConfigurationCommand )
{
    RfnTouScheduleConfigurationCommand::Schedule sched;

    // SCHEDULE_1
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times(6);

        times[0] = "00:00";
        times[1] = "00:01";
        times[2] = "10:06";
        times[3] = "12:22";
        times[4] = "23:33";
        times[5] = "23:44";

        sched._times[RfnTouScheduleConfigurationCommand::Schedule1] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates(6);

        rates[0] = "A"; // midnight rate
        rates[1] = "B";
        rates[2] = "C";
        rates[3] = "D";
        rates[4] = "A";
        rates[5] = "B";

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule1] = rates;
    }

    // SCHEDULE_2
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times(6);

        times[0] = "00:00";
        times[1] = "01:23";
        times[2] = "03:12";
        times[3] = "04:01";
        times[4] = "05:23";
        times[5] = "16:28";

        sched._times[RfnTouScheduleConfigurationCommand::Schedule2] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates(6);

        rates[0] = "D"; // midnight rate
        rates[1] = "A";
        rates[2] = "B";
        rates[3] = "C";
        rates[4] = "D";
        rates[5] = "A";

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule2] = rates;
    }

    // SCHEDULE_3
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times(6);

        times[0] = "00:00";
        times[1] = "01:02";
        times[2] = "02:03";
        times[3] = "04:05";
        times[4] = "05:06";
        times[5] = "06:07";

        sched._times[RfnTouScheduleConfigurationCommand::Schedule3] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates(6);

        rates[0] = "C"; // midnight rate
        rates[1] = "D";
        rates[2] = "A";
        rates[3] = "B";
        rates[4] = "C";
        rates[5] = "D";

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule3] = rates;
    }

    // SCHEDULE_4
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times(6);

        times[0] = "00:00";
        times[1] = "00:01";
        times[2] = "08:59";
        times[3] = "12:12";
        times[4] = "23:01";
        times[5] = "23:55";

        sched._times[RfnTouScheduleConfigurationCommand::Schedule4] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates(6);

        rates[0] = "B"; // midnight rate
        rates[1] = "C";
        rates[2] = "D";
        rates[3] = "A";
        rates[4] = "B";
        rates[5] = "C";

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule4] = rates;
    }

    // day table
    {
        RfnTouScheduleConfigurationCommand::DayTable dayTable(8);

        dayTable[0] = "SCHEDULE_1";
        dayTable[1] = "SCHEDULE_3";
        dayTable[2] = "SCHEDULE_2";
        dayTable[3] = "SCHEDULE_4";
        dayTable[4] = "SCHEDULE_2";
        dayTable[5] = "SCHEDULE_3";
        dayTable[6] = "SCHEDULE_1";
        dayTable[7] = "SCHEDULE_3";

        sched._dayTable = dayTable;
    }

    // default rate
    {
        sched._defaultRate = "B";
    }

    // execute
    {
        RfnTouScheduleSetConfigurationCommand cmd( sched );

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x04)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 switch times
                (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                (0x03)(0x0A) // schedule 2 switch times
                (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                (0x04)(0x0A) // schedule 3 switch times
                (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                (0x05)(0x0A) // schedule 4 switch time
                (0x00)(0x01)(0x02)(0x1a)(0x00)(0xc1)(0x02)(0x89)(0x00)(0x36)
                (0x06)(0x03) // schedule 1 rates
                (0x88)(0x86)(0x00)
                (0x07)(0x03) // schedule 2 rates
                (0x43)(0x34)(0x00)
                (0x08)(0x03) // schedule 3 rates
                (0x1A)(0xA2)(0x01)
                (0x09)(0x03) // schedule 4 rates
                (0xD1)(0x10)(0x01)
                (0x0A)(0x01) // default TOU rate
                (0x01);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute read only
    {
        RfnTouScheduleGetConfigurationCommand cmd;

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x05)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        RfnTouScheduleGetConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 switch times
                (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                (0x03)(0x0A) // schedule 2 switch times
                (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                (0x04)(0x0A) // schedule 3 switch times
                (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                (0x05)(0x0A) // schedule 4 switch time
                (0x00)(0x01)(0x02)(0x1a)(0x00)(0xc1)(0x02)(0x89)(0x00)(0x36)
                (0x06)(0x03) // schedule 1 rates
                (0x88)(0x86)(0x00)
                (0x07)(0x03) // schedule 2 rates
                (0x43)(0x34)(0x00)
                (0x08)(0x03) // schedule 3 rates
                (0x1A)(0xA2)(0x01)
                (0x09)(0x03) // schedule 4 rates
                (0xD1)(0x10)(0x01)
                (0x0A)(0x01) // default TOU rate
                (0x01);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
                "Status : Success\n"
                "Additional Status : NO ADDITIONAL STATUS\n"
                "TOU State : Disabled\n"
                "Day Table :\n"
                " Sunday    - SCHEDULE_1\n"
                " Monday    - SCHEDULE_3\n"
                " Tuesday   - SCHEDULE_2\n"
                " Wednesday - SCHEDULE_4\n"
                " Thursday  - SCHEDULE_2\n"
                " Friday    - SCHEDULE_3\n"
                " Saturday  - SCHEDULE_1\n"
                " Holiday   - SCHEDULE_3\n"
                "SCHEDULE_1 switch times :\n"
                " Switch time 1 - 00:01\n"
                " Switch time 2 - 10:06\n"
                " Switch time 3 - 12:22\n"
                " Switch time 4 - 23:33\n"
                " Switch time 5 - 23:44\n"
                "SCHEDULE_2 switch times :\n"
                " Switch time 1 - 01:23\n"
                " Switch time 2 - 03:12\n"
                " Switch time 3 - 04:01\n"
                " Switch time 4 - 05:23\n"
                " Switch time 5 - 16:28\n"
                "SCHEDULE_3 switch times :\n"
                " Switch time 1 - 01:02\n"
                " Switch time 2 - 02:03\n"
                " Switch time 3 - 04:05\n"
                " Switch time 4 - 05:06\n"
                " Switch time 5 - 06:07\n"
                "SCHEDULE_4 switch times :\n"
                " Switch time 1 - 00:01\n"
                " Switch time 2 - 08:59\n"
                " Switch time 3 - 12:12\n"
                " Switch time 4 - 23:01\n"
                " Switch time 5 - 23:55\n"
                "SCHEDULE_1 rates :\n"
                " Midnight rate - A\n"
                " Switch 1 rate - B\n"
                " Switch 2 rate - C\n"
                " Switch 3 rate - D\n"
                " Switch 4 rate - A\n"
                " Switch 5 rate - B\n"
                "SCHEDULE_2 rates :\n"
                " Midnight rate - D\n"
                " Switch 1 rate - A\n"
                " Switch 2 rate - B\n"
                " Switch 3 rate - C\n"
                " Switch 4 rate - D\n"
                " Switch 5 rate - A\n"
                "SCHEDULE_3 rates :\n"
                " Midnight rate - C\n"
                " Switch 1 rate - D\n"
                " Switch 2 rate - A\n"
                " Switch 3 rate - B\n"
                " Switch 4 rate - C\n"
                " Switch 5 rate - D\n"
                "SCHEDULE_4 rates :\n"
                " Midnight rate - B\n"
                " Switch 1 rate - C\n"
                " Switch 2 rate - D\n"
                " Switch 3 rate - A\n"
                " Switch 4 rate - B\n"
                " Switch 5 rate - C\n"
                "Default TOU rate : B";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        const boost::optional<RfnTouScheduleConfigurationCommand::Schedule> sched_rcv = cmd.getTouScheduleReceived();

        BOOST_REQUIRE( !! sched_rcv );

        BOOST_CHECK_EQUAL_RANGES( sched_rcv->_dayTable,
                                  sched._dayTable);

        BOOST_CHECK_EQUAL( sched_rcv->_defaultRate,
                           sched._defaultRate );

        {
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesRcv = Cti::mapFind(sched_rcv->_times,  RfnTouScheduleConfigurationCommand::Schedule1);
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesExp = Cti::mapFind(sched._times,       RfnTouScheduleConfigurationCommand::Schedule1);

            BOOST_REQUIRE( !! timesRcv );
            BOOST_REQUIRE( !! timesExp );

            BOOST_CHECK_EQUAL_RANGES( *timesRcv, *timesExp );
        }
        {
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesRcv = Cti::mapFind(sched_rcv->_times,  RfnTouScheduleConfigurationCommand::Schedule2);
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesExp = Cti::mapFind(sched._times,       RfnTouScheduleConfigurationCommand::Schedule2);

            BOOST_REQUIRE( !! timesRcv );
            BOOST_REQUIRE( !! timesExp );

            BOOST_CHECK_EQUAL_RANGES( *timesRcv, *timesExp );
        }
        {
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesRcv = Cti::mapFind(sched_rcv->_times,  RfnTouScheduleConfigurationCommand::Schedule3);
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesExp = Cti::mapFind(sched._times,       RfnTouScheduleConfigurationCommand::Schedule3);

            BOOST_REQUIRE( !! timesRcv );
            BOOST_REQUIRE( !! timesExp );

            BOOST_CHECK_EQUAL_RANGES( *timesRcv, *timesExp );
        }
        {
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesRcv = Cti::mapFind(sched_rcv->_times,  RfnTouScheduleConfigurationCommand::Schedule4);
            const boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> timesExp = Cti::mapFind(sched._times,       RfnTouScheduleConfigurationCommand::Schedule4);

            BOOST_REQUIRE( !! timesRcv );
            BOOST_REQUIRE( !! timesExp );

            BOOST_CHECK_EQUAL_RANGES( *timesRcv, *timesExp );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_RfnTouHolidayConfigurationCommand )
{
    const auto tz_override = Cti::Test::set_to_central_timezone();

    const CtiDate date1 = CtiDate( 01, 02, 2025 ),
                  date2 = CtiDate( 14, 06, 2036 ),
                  date3 = CtiDate( 30, 12, 2050 );

    const std::vector<unsigned char> holiday1 = boost::assign::list_of(0x67)(0x9d)(0xb8)(0x60),
                                     holiday2 = boost::assign::list_of(0x7c)(0xfe)(0x2c)(0xd0),
                                     holiday3 = boost::assign::list_of(0x98)(0x59)(0x5a)(0xe0);

    const RfnTouHolidayConfigurationCommand::Holidays holidays =
    {
        date1,
        date2,
        date3,
    };

    // execute
    {
        RfnTouHolidayConfigurationCommand cmd( holidays );

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x06)
                (0x01)
                (0x0C)(0x0C); // holiday

        exp.insert( exp.end(), holiday1.begin(), holiday1.end() );
        exp.insert( exp.end(), holiday2.begin(), holiday2.end() );
        exp.insert( exp.end(), holiday3.begin(), holiday3.end() );

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute read only
    {
        RfnTouHolidayConfigurationCommand cmd;

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x07)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        RfnTouHolidayConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x01)
                (0x0C)(0x0C); // holiday

        response.insert( response.end(), holiday1.begin(), holiday1.end() );
        response.insert( response.end(), holiday2.begin(), holiday2.end() );
        response.insert( response.end(), holiday3.begin(), holiday3.end() );

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Disabled\n"
            "Holidays :\n"
            " Date 1 - 2025-02-01 (679db860)\n"
            " Date 2 - 2036-06-14 (7cfe2cd0)\n"
            " Date 3 - 2050-12-30 (98595ae0)";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        const boost::optional<RfnTouHolidayConfigurationCommand::Holidays> holidays_rcv = cmd.getHolidaysReceived();

        BOOST_REQUIRE( !! holidays_rcv );

        BOOST_CHECK_EQUAL_RANGES(*holidays_rcv, holidays);
    }
}

BOOST_AUTO_TEST_CASE( test_RfnTouStateConfigurationCommand )
{
    // execute enable
    {
        RfnTouStateConfigurationCommand cmd( RfnTouConfigurationCommand::TouEnable );

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x01)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute disable
    {
        RfnTouStateConfigurationCommand cmd( RfnTouConfigurationCommand::TouDisable );

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x02)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute read only
    {
        RfnTouStateConfigurationCommand cmd;

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x03)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode tou state tou enabled
    {
        RfnTouStateConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x01)
                (0x00);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Enabled";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        const boost::optional<RfnTouConfigurationCommand::TouState> touState_rcv = cmd.getTouStateReceived();

        BOOST_REQUIRE( !! touState_rcv );

        BOOST_CHECK_EQUAL( *touState_rcv, RfnTouConfigurationCommand::TouEnable );

    }

    // decode tou state tou disabled
    {
        RfnTouStateConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x00);

        RfnCommandResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Disabled";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        const boost::optional<RfnTouConfigurationCommand::TouState> touState_rcv = cmd.getTouStateReceived();

        BOOST_REQUIRE( !! touState_rcv );

        BOOST_CHECK_EQUAL( *touState_rcv, RfnTouConfigurationCommand::TouDisable );
    }

}

BOOST_AUTO_TEST_CASE( test_RfnTouSetHolidayActiveCommand )
{
    {
        RfnTouSetHolidayActiveCommand cmd;

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x0C)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }
}


BOOST_AUTO_TEST_CASE( test_RfnTouCancelHolidayActiveCommand )
{
    {
        RfnTouCancelHolidayActiveCommand cmd;

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x0D)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }
}

BOOST_AUTO_TEST_SUITE_END()
