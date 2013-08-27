#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "std_helper.h"
#include "boost_test_helpers.h"
#include "cmd_rfn_TouConfiguration.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnTouConfigurationCommand;
using Cti::Devices::Commands::RfnTouScheduleConfigurationCommand;
using Cti::Devices::Commands::RfnTouHolidayConfigurationCommand;
using Cti::Devices::Commands::RfnTouEnableConfigurationCommand;
using Cti::Devices::Commands::RfnTouHolidayActiveConfigurationCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_TouConfiguration )

std::vector<unsigned char> convertLongToBytes( unsigned long val )
{
    std::vector<unsigned char> vec;

    vec.push_back( (val >> 24) & 0xff );
    vec.push_back( (val >> 16) & 0xff );
    vec.push_back( (val >>  8) & 0xff );
    vec.push_back( (val >>  0) & 0xff );

    return vec;
}

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE( test_RfnTouScheduleConfigurationCommand )
{
    RfnTouScheduleConfigurationCommand::Schedule sched;

    // Schedule 1
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times;

        times[0] = 0x0000;
        times[1] = 0x1111;
        times[2] = 0x2222;
        times[3] = 0x3333;
        times[4] = 0x4444;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule1] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates;

        rates[0] = RfnTouScheduleConfigurationCommand::RateA; // midnight rate
        rates[1] = RfnTouScheduleConfigurationCommand::RateB;
        rates[2] = RfnTouScheduleConfigurationCommand::RateC;
        rates[3] = RfnTouScheduleConfigurationCommand::RateD;
        rates[4] = RfnTouScheduleConfigurationCommand::RateA;
        rates[5] = RfnTouScheduleConfigurationCommand::RateB;

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule1] = rates;
    }

    // Schedule 2
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times;

        times[0] = 0x0123;
        times[1] = 0x3012;
        times[2] = 0x2301;
        times[3] = 0x1230;
        times[4] = 0x0123;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule2] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates;

        rates[0] = RfnTouScheduleConfigurationCommand::RateD; // midnight rate
        rates[1] = RfnTouScheduleConfigurationCommand::RateA;
        rates[2] = RfnTouScheduleConfigurationCommand::RateB;
        rates[3] = RfnTouScheduleConfigurationCommand::RateC;
        rates[4] = RfnTouScheduleConfigurationCommand::RateD;
        rates[5] = RfnTouScheduleConfigurationCommand::RateA;

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule2] = rates;
    }

    // Schedule 3
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times;

        times[0] = 0xAAAA;
        times[1] = 0x5555;
        times[2] = 0xAA55;
        times[3] = 0x55AA;
        times[4] = 0xA55A;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule3] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates;

        rates[0] = RfnTouScheduleConfigurationCommand::RateC; // midnight rate
        rates[1] = RfnTouScheduleConfigurationCommand::RateD;
        rates[2] = RfnTouScheduleConfigurationCommand::RateA;
        rates[3] = RfnTouScheduleConfigurationCommand::RateB;
        rates[4] = RfnTouScheduleConfigurationCommand::RateC;
        rates[5] = RfnTouScheduleConfigurationCommand::RateD;

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule3] = rates;
    }

    // Schedule 4
    {
        RfnTouScheduleConfigurationCommand::DailyTimes times;

        times[0] = 0x0000;
        times[1] = 0xFFFF;
        times[2] = 0x0F0F;
        times[3] = 0xF0F0;
        times[4] = 0x00FF;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule4] = times;

        RfnTouScheduleConfigurationCommand::DailyRates rates;

        rates[0] = RfnTouScheduleConfigurationCommand::RateB; // midnight rate
        rates[1] = RfnTouScheduleConfigurationCommand::RateC;
        rates[2] = RfnTouScheduleConfigurationCommand::RateD;
        rates[3] = RfnTouScheduleConfigurationCommand::RateA;
        rates[4] = RfnTouScheduleConfigurationCommand::RateB;
        rates[5] = RfnTouScheduleConfigurationCommand::RateC;

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule4] = rates;
    }

    // day table
    {
        RfnTouScheduleConfigurationCommand::DayTable dayTable;

        dayTable[0] = RfnTouScheduleConfigurationCommand::Schedule1;
        dayTable[1] = RfnTouScheduleConfigurationCommand::Schedule3;
        dayTable[2] = RfnTouScheduleConfigurationCommand::Schedule2;
        dayTable[3] = RfnTouScheduleConfigurationCommand::Schedule4;
        dayTable[4] = RfnTouScheduleConfigurationCommand::Schedule2;
        dayTable[5] = RfnTouScheduleConfigurationCommand::Schedule3;
        dayTable[6] = RfnTouScheduleConfigurationCommand::Schedule1;
        dayTable[7] = RfnTouScheduleConfigurationCommand::Schedule3;

        sched._dayTable = dayTable;
    }

    // default rate
    {
        sched._defaultRate = RfnTouScheduleConfigurationCommand::RateB;
    }

    // execute
    {
        RfnTouScheduleConfigurationCommand cmd( sched );

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x04)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 switch times
                (0x00)(0x00)(0x11)(0x11)(0x22)(0x22)(0x33)(0x33)(0x44)(0x44)
                (0x03)(0x0A) // schedule 2 switch times
                (0x01)(0x23)(0x30)(0x12)(0x23)(0x01)(0x12)(0x30)(0x01)(0x23)
                (0x04)(0x0A) // schedule 3 switch times
                (0xAA)(0xAA)(0x55)(0x55)(0xAA)(0x55)(0x55)(0xAA)(0xA5)(0x5A)
                (0x05)(0x0A) // schedule 4 switch time
                (0x00)(0x00)(0xFF)(0xFF)(0x0F)(0x0F)(0xF0)(0xF0)(0x00)(0xFF)
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
        RfnTouScheduleConfigurationCommand cmd;

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x05)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        RfnTouScheduleConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 switch times
                (0x00)(0x00)(0x11)(0x11)(0x22)(0x22)(0x33)(0x33)(0x44)(0x44)
                (0x03)(0x0A) // schedule 2 switch times
                (0x01)(0x23)(0x30)(0x12)(0x23)(0x01)(0x12)(0x30)(0x01)(0x23)
                (0x04)(0x0A) // schedule 3 switch times
                (0xAA)(0xAA)(0x55)(0x55)(0xAA)(0x55)(0x55)(0xAA)(0xA5)(0x5A)
                (0x05)(0x0A) // schedule 4 switch time
                (0x00)(0x00)(0xFF)(0xFF)(0x0F)(0x0F)(0xF0)(0xF0)(0x00)(0xFF)
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

        RfnCommand::RfnResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
                "Status : Success\n"
                "Additional Status : NO ADDITIONAL STATUS\n"
                "TOU State : Disabled\n"
                "Day Table :\n"
                " Sunday    - schedule 1\n"
                " Monday    - schedule 3\n"
                " Tuesday   - schedule 2\n"
                " Wednesday - schedule 4\n"
                " Thursday  - schedule 2\n"
                " Friday    - schedule 3\n"
                " Saturday  - schedule 1\n"
                " Holiday   - schedule 3\n"
                "Schedule 1 switch times :\n"
                " Switch time 1 - 0 minutes\n"
                " Switch time 2 - 4369 minutes\n"
                " Switch time 3 - 8738 minutes\n"
                " Switch time 4 - 13107 minutes\n"
                " Switch time 5 - 17476 minutes\n"
                "Schedule 2 switch times :\n"
                " Switch time 1 - 291 minutes\n"
                " Switch time 2 - 12306 minutes\n"
                " Switch time 3 - 8961 minutes\n"
                " Switch time 4 - 4656 minutes\n"
                " Switch time 5 - 291 minutes\n"
                "Schedule 3 switch times :\n"
                " Switch time 1 - 43690 minutes\n"
                " Switch time 2 - 21845 minutes\n"
                " Switch time 3 - 43605 minutes\n"
                " Switch time 4 - 21930 minutes\n"
                " Switch time 5 - 42330 minutes\n"
                "Schedule 4 switch times :\n"
                " Switch time 1 - 0 minutes\n"
                " Switch time 2 - 65535 minutes\n"
                " Switch time 3 - 3855 minutes\n"
                " Switch time 4 - 61680 minutes\n"
                " Switch time 5 - 255 minutes\n"
                "Schedule 1 rates :\n"
                " Midnight rate - A\n"
                " Switch 1 rate - B\n"
                " Switch 2 rate - C\n"
                " Switch 3 rate - D\n"
                " Switch 4 rate - A\n"
                " Switch 5 rate - B\n"
                "Schedule 2 rates :\n"
                " Midnight rate - D\n"
                " Switch 1 rate - A\n"
                " Switch 2 rate - B\n"
                " Switch 3 rate - C\n"
                " Switch 4 rate - D\n"
                " Switch 5 rate - A\n"
                "Schedule 3 rates :\n"
                " Midnight rate - C\n"
                " Switch 1 rate - D\n"
                " Switch 2 rate - A\n"
                " Switch 3 rate - B\n"
                " Switch 4 rate - C\n"
                " Switch 5 rate - D\n"
                "Schedule 4 rates :\n"
                " Midnight rate - B\n"
                " Switch 1 rate - C\n"
                " Switch 2 rate - D\n"
                " Switch 3 rate - A\n"
                " Switch 4 rate - B\n"
                " Switch 5 rate - C\n"
                "Default TOU rate : B\n";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        BOOST_REQUIRE( cmd.getTouScheduleReceived() );

        RfnTouScheduleConfigurationCommand::Schedule sched_rcv = *cmd.getTouScheduleReceived();

        BOOST_CHECK_EQUAL( sched_rcv._dayTable,    sched._dayTable );
        BOOST_CHECK_EQUAL( sched_rcv._defaultRate, sched._defaultRate );

        BOOST_CHECK_EQUAL( Cti::mapFind(sched_rcv._times,  RfnTouScheduleConfigurationCommand::Schedule1),
                           Cti::mapFind(sched._times,      RfnTouScheduleConfigurationCommand::Schedule1) );

        BOOST_CHECK_EQUAL( Cti::mapFind(sched_rcv._times,  RfnTouScheduleConfigurationCommand::Schedule2),
                           Cti::mapFind(sched._times,      RfnTouScheduleConfigurationCommand::Schedule2) );

        BOOST_CHECK_EQUAL( Cti::mapFind(sched_rcv._times,  RfnTouScheduleConfigurationCommand::Schedule3),
                           Cti::mapFind(sched._times,      RfnTouScheduleConfigurationCommand::Schedule3) );

        BOOST_CHECK_EQUAL( Cti::mapFind(sched_rcv._times,  RfnTouScheduleConfigurationCommand::Schedule4),
                           Cti::mapFind(sched._times,      RfnTouScheduleConfigurationCommand::Schedule4) );
    }
}

BOOST_AUTO_TEST_CASE( test_RfnTouHolidayConfigurationCommand )
{
    Cti::Test::set_to_central_timezone();

    const CtiDate date1 = CtiDate( 01, 02, 1970 ),
                  date2 = CtiDate( 14, 06, 2012 ),
                  date3 = CtiDate( 30, 12, 2050 );

    const std::vector<unsigned char> holiday1 = boost::assign::list_of(0x00)(0x29)(0x32)(0xe0),
                                     holiday2 = boost::assign::list_of(0x4f)(0xd9)(0x6f)(0xd0),
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

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

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

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

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

        RfnCommand::RfnResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Disabled\n"
            "Holidays :\n"
            " Date 1 - 02/01/1970 00:00:00\n"
            " Date 2 - 06/14/2012 00:00:00\n"
            " Date 3 - 12/30/2050 00:00:00\n";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        BOOST_REQUIRE( cmd.getHolidaysReceived() );

        RfnTouHolidayConfigurationCommand::Holidays holidays_rcv = *cmd.getHolidaysReceived();

        //BOOST_CHECK_EQUAL( holidays_rcv, holidays ); // doesnt work??

        int index = 0;
        for each(const CtiDate & date_exp in holidays)
        {
            const CtiDate & date_rcv = holidays_rcv[index++];
            BOOST_CHECK_EQUAL( date_rcv, date_exp );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_RfnTouEnableConfigurationCommand )
{
    // execute enable
    {
        RfnTouEnableConfigurationCommand cmd( RfnTouConfigurationCommand::TouEnable );

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x01)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute disable
    {
        RfnTouEnableConfigurationCommand cmd( RfnTouConfigurationCommand::TouDisable );

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x02)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute read only
    {
        RfnTouEnableConfigurationCommand cmd;

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x03)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode tou state tou enabled
    {
        RfnTouEnableConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x01)
                (0x00);

        RfnCommand::RfnResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Enabled\n";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        BOOST_REQUIRE( cmd.getTouStateReceived() );

        RfnTouConfigurationCommand::TouState touState_rcv = *cmd.getTouStateReceived();

        BOOST_CHECK_EQUAL( touState_rcv, RfnTouConfigurationCommand::TouEnable );

    }

    // decode tou state tou disabled
    {
        RfnTouEnableConfigurationCommand cmd;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x00);

        RfnCommand::RfnResult rcv = cmd.decodeCommand(execute_time, response);

        std::string desc_exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Disabled\n";

        BOOST_CHECK_EQUAL(rcv.description, desc_exp);

        BOOST_REQUIRE( cmd.getTouStateReceived() );

        RfnTouConfigurationCommand::TouState touState_rcv = *cmd.getTouStateReceived();

        BOOST_CHECK_EQUAL( touState_rcv, RfnTouConfigurationCommand::TouDisable );
    }

}

BOOST_AUTO_TEST_CASE( test_RfnTouHolidayActiveConfigurationCommand )
{
    // execute set active
    {
        RfnTouHolidayActiveConfigurationCommand cmd( RfnTouHolidayActiveConfigurationCommand::SetHolidayActive );

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x0C)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute cancel active
    {
        RfnTouHolidayActiveConfigurationCommand cmd( RfnTouHolidayActiveConfigurationCommand::CancelHolidayActive );

        RfnCommand::RfnRequest rcv = cmd.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x0D)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }
}

BOOST_AUTO_TEST_SUITE_END()
