#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_TouConfiguration.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnTouConfigurationCommand;
using Cti::Devices::Commands::RfnTouScheduleConfigurationCommand;
using Cti::Devices::Commands::RfnTouHolidayConfigurationCommand;
using Cti::Devices::Commands::RfnTouEnableConfigurationCommand;

template< typename value_type >
CtiTableDynamicPaoInfo makePaoInfo( CtiTableDynamicPaoInfo::PaoInfoKeys key, value_type value )
{
    CtiTableDynamicPaoInfo paoInfo;
    paoInfo.setKey(key);
    paoInfo.setValue(value);
    return paoInfo;
}


BOOST_AUTO_TEST_SUITE( test_cmd_rfn_TouConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE( test_RfnTouScheduleConfigurationCommand )
{
    RfnTouScheduleConfigurationCommand::Schedule sched;


    // Schedule 1
    {
        RfnTouScheduleConfigurationCommand::dailyTimesT times;

        times[0] = 0x0000;
        times[1] = 0x1111;
        times[2] = 0x2222;
        times[3] = 0x3333;
        times[4] = 0x4444;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule1] = times;

        RfnTouScheduleConfigurationCommand::dailyRatesT rates;

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
        RfnTouScheduleConfigurationCommand::dailyTimesT times;

        times[0] = 0x0123;
        times[1] = 0x3012;
        times[2] = 0x2301;
        times[3] = 0x1230;
        times[4] = 0x0123;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule2] = times;

        RfnTouScheduleConfigurationCommand::dailyRatesT rates;

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
        RfnTouScheduleConfigurationCommand::dailyTimesT times;

        times[0] = 0xAAAA;
        times[1] = 0x5555;
        times[2] = 0xAA55;
        times[3] = 0x55AA;
        times[4] = 0xA55A;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule3] = times;

        RfnTouScheduleConfigurationCommand::dailyRatesT rates;

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
        RfnTouScheduleConfigurationCommand::dailyTimesT times;

        times[0] = 0x0000;
        times[1] = 0xFFFF;
        times[2] = 0x0F0F;
        times[3] = 0xF0F0;
        times[4] = 0x00FF;

        sched._times[RfnTouScheduleConfigurationCommand::Schedule4] = times;

        RfnTouScheduleConfigurationCommand::dailyRatesT rates;

        rates[0] = RfnTouScheduleConfigurationCommand::RateB; // midnight rate
        rates[1] = RfnTouScheduleConfigurationCommand::RateC;
        rates[2] = RfnTouScheduleConfigurationCommand::RateD;
        rates[3] = RfnTouScheduleConfigurationCommand::RateA;
        rates[4] = RfnTouScheduleConfigurationCommand::RateB;
        rates[5] = RfnTouScheduleConfigurationCommand::RateC;

        sched._rates[RfnTouScheduleConfigurationCommand::Schedule4] = rates;
    }

    // day table
    sched._dayTable[0] = RfnTouScheduleConfigurationCommand::Schedule1;
    sched._dayTable[1] = RfnTouScheduleConfigurationCommand::Schedule3;
    sched._dayTable[2] = RfnTouScheduleConfigurationCommand::Schedule2;
    sched._dayTable[3] = RfnTouScheduleConfigurationCommand::Schedule4;
    sched._dayTable[4] = RfnTouScheduleConfigurationCommand::Schedule2;
    sched._dayTable[5] = RfnTouScheduleConfigurationCommand::Schedule3;
    sched._dayTable[6] = RfnTouScheduleConfigurationCommand::Schedule1;
    sched._dayTable[7] = RfnTouScheduleConfigurationCommand::Schedule3;

    // default rate
    sched._defaultRate = RfnTouScheduleConfigurationCommand::RateB;

    // execute
    {
        RfnTouScheduleConfigurationCommand touConfig( sched );

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x04)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 switch times
                (0x00)(0x00)(0x11)(0x11)(0x22)(0x22)(0x33)(0x33)(0x44)(0x44)
                (0x03)(0x0A) // schedule 2 switch times
                (0x23)(0x01)(0x12)(0x30)(0x01)(0x23)(0x30)(0x12)(0x23)(0x01)
                (0x04)(0x0A) // schedule 3 switch times
                (0xAA)(0xAA)(0x55)(0x55)(0x55)(0xAA)(0xAA)(0x55)(0x5A)(0xA5)
                (0x05)(0x0A) // schedule 4 switch time
                (0x00)(0x00)(0xFF)(0xFF)(0x0F)(0x0F)(0xF0)(0xF0)(0xFF)(0x00)
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
        RfnTouScheduleConfigurationCommand touConfig;

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x05)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        RfnTouScheduleConfigurationCommand touConfig;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 switch times
                (0x00)(0x00)(0x11)(0x11)(0x22)(0x22)(0x33)(0x33)(0x44)(0x44)
                (0x03)(0x0A) // schedule 2 switch times
                (0x23)(0x01)(0x12)(0x30)(0x01)(0x23)(0x30)(0x12)(0x23)(0x01)
                (0x04)(0x0A) // schedule 3 switch times
                (0xAA)(0xAA)(0x55)(0x55)(0x55)(0xAA)(0xAA)(0x55)(0x5A)(0xA5)
                (0x05)(0x0A) // schedule 4 switch time
                (0x00)(0x00)(0xFF)(0xFF)(0x0F)(0x0F)(0xF0)(0xF0)(0xFF)(0x00)
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

        RfnCommand::RfnResult rcv = touConfig.decodeCommand(execute_time, response);

        std::string exp =
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

        BOOST_CHECK_EQUAL(rcv.description, exp);

        std::vector<CtiTableDynamicPaoInfo> paoInfoExp = boost::assign::list_of
        // day table
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,        (int)RfnTouScheduleConfigurationCommand::Schedule1 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,       (int)RfnTouScheduleConfigurationCommand::Schedule3 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule,     (int)RfnTouScheduleConfigurationCommand::Schedule2 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,      (int)RfnTouScheduleConfigurationCommand::Schedule4 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,        (int)RfnTouScheduleConfigurationCommand::Schedule2 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,      (int)RfnTouScheduleConfigurationCommand::Schedule3 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,        (int)RfnTouScheduleConfigurationCommand::Schedule1 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,       (int)RfnTouScheduleConfigurationCommand::Schedule3 ))
        // schedule 1 times
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1,        (unsigned short)0x0000 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2,        (unsigned short)0x1111 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3,        (unsigned short)0x2222 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4,        (unsigned short)0x3333 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5,        (unsigned short)0x4444 ))
        // schedule 2 times
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1,        (unsigned short)0x0123 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2,        (unsigned short)0x3012 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3,        (unsigned short)0x2301 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4,        (unsigned short)0x1230 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5,        (unsigned short)0x0123 ))
        // schedule 3 times
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1,        (unsigned short)0xAAAA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2,        (unsigned short)0x5555 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3,        (unsigned short)0xAA55 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4,        (unsigned short)0x55AA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5,        (unsigned short)0xA55A ))
        // schedule 4 times
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1,        (unsigned short)0x0000 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2,        (unsigned short)0xFFFF ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3,        (unsigned short)0x0F0F ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4,        (unsigned short)0xF0F0 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5,        (unsigned short)0x00FF ))
        // schedule 1 rates
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1MidnightRate, (int)RfnTouScheduleConfigurationCommand::RateA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1,        (int)RfnTouScheduleConfigurationCommand::RateB ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2,        (int)RfnTouScheduleConfigurationCommand::RateC ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3,        (int)RfnTouScheduleConfigurationCommand::RateD ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4,        (int)RfnTouScheduleConfigurationCommand::RateA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5,        (int)RfnTouScheduleConfigurationCommand::RateB ))
        // schedule 2 rates
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2MidnightRate, (int)RfnTouScheduleConfigurationCommand::RateD ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1,        (int)RfnTouScheduleConfigurationCommand::RateA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2,        (int)RfnTouScheduleConfigurationCommand::RateB ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3,        (int)RfnTouScheduleConfigurationCommand::RateC ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4,        (int)RfnTouScheduleConfigurationCommand::RateD ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5,        (int)RfnTouScheduleConfigurationCommand::RateA ))
        // schedule 3 rates
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3MidnightRate, (int)RfnTouScheduleConfigurationCommand::RateC ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1,        (int)RfnTouScheduleConfigurationCommand::RateD ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2,        (int)RfnTouScheduleConfigurationCommand::RateA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3,        (int)RfnTouScheduleConfigurationCommand::RateB ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4,        (int)RfnTouScheduleConfigurationCommand::RateC ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5,        (int)RfnTouScheduleConfigurationCommand::RateD ))
        // schedule 4 rates
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4MidnightRate, (int)RfnTouScheduleConfigurationCommand::RateB ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1,        (int)RfnTouScheduleConfigurationCommand::RateC ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2,        (int)RfnTouScheduleConfigurationCommand::RateD ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3,        (int)RfnTouScheduleConfigurationCommand::RateA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4,        (int)RfnTouScheduleConfigurationCommand::RateB ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5,        (int)RfnTouScheduleConfigurationCommand::RateC ))
        // default tou rate
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate,        (int)RfnTouScheduleConfigurationCommand::RateB ));


        std::vector<CtiTableDynamicPaoInfo> paoInfoRcv = touConfig.getPaoInfo();

        BOOST_REQUIRE_EQUAL( paoInfoRcv.size(),  paoInfoExp.size() );

        int index = 0;
        for each( const CtiTableDynamicPaoInfo& item in paoInfoExp )
        {
            BOOST_CHECK_EQUAL( paoInfoRcv[index].getKey(),   item.getKey() );
            BOOST_CHECK_EQUAL( paoInfoRcv[index].getValue(), item.getValue() );
            index++;
        }
    }
}

BOOST_AUTO_TEST_CASE( test_RfnTouHolidayConfigurationCommand )
{
    const RfnTouHolidayConfigurationCommand::holidaysT holidays =
    {
        0x12345678,
        0xAAAAAAAA,
        0x55555555,
    };

    // execute
    {
        RfnTouHolidayConfigurationCommand touConfig( holidays );

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x06)
                (0x01)
                (0x0C)(0x0C) // holiday
                (0x78)(0x56)(0x34)(0x12)(0xAA)(0xAA)(0xAA)(0xAA)(0x55)(0x55)(0x55)(0x55);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                        rcv.begin(), rcv.end(),
                        exp.begin(), exp.end());
    }

    // execute read only
    {
        RfnTouHolidayConfigurationCommand touConfig;

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x07)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        RfnTouHolidayConfigurationCommand touConfig;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x01)
                (0x0C)(0x0C) // holiday
                (0x78)(0x56)(0x34)(0x12)(0xAA)(0xAA)(0xAA)(0xAA)(0x55)(0x55)(0x55)(0x55);

        RfnCommand::RfnResult rcv = touConfig.decodeCommand(execute_time, response);

        std::string exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Disabled\n"
            "Holidays :\n"
            " Date 1 - " + CtiTime(0x12345678).asString() + "\n"
            " Date 2 - " + CtiTime(0xAAAAAAAA).asString() + "\n"
            " Date 3 - " + CtiTime(0x55555555).asString() + "\n";

        BOOST_CHECK_EQUAL(rcv.description, exp);

        std::vector<CtiTableDynamicPaoInfo> paoInfoExp = boost::assign::list_of
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday1, (unsigned long)0x12345678 ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday2, (unsigned long)0xAAAAAAAA ))
                ( makePaoInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday3, (unsigned long)0x55555555 ));

        std::vector<CtiTableDynamicPaoInfo> paoInfoRcv = touConfig.getPaoInfo();

        BOOST_REQUIRE_EQUAL( paoInfoRcv.size(),  paoInfoExp.size() );

        int index = 0;
        for each( const CtiTableDynamicPaoInfo& item in paoInfoExp )
        {
            BOOST_CHECK_EQUAL( paoInfoRcv[index].getKey(),   item.getKey() );
            BOOST_CHECK_EQUAL( paoInfoRcv[index].getValue(), item.getValue() );
            index++;
        }

    }
}

BOOST_AUTO_TEST_CASE( test_RfnTouEnableConfigurationCommand )
{
    // execute enable
    {
        RfnTouEnableConfigurationCommand touConfig( true );

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x01)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute disable
    {
        RfnTouEnableConfigurationCommand touConfig( false );

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x02)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // execute read only
    {
        RfnTouEnableConfigurationCommand touConfig;

        RfnCommand::RfnRequest rcv = touConfig.executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x03)
                (0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode tou state tou enabled
    {
        RfnTouEnableConfigurationCommand touConfig;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x01)
                (0x00);

        RfnCommand::RfnResult rcv = touConfig.decodeCommand(execute_time, response);

        std::string exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Enabled\n";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }

    // decode tou state tou disabled
    {
        RfnTouEnableConfigurationCommand touConfig;

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)(0x00)(0x00)(0x00)(0x00)
                (0x00);

        RfnCommand::RfnResult rcv = touConfig.decodeCommand(execute_time, response);

        std::string exp =
            "Status : Success\n"
            "Additional Status : NO ADDITIONAL STATUS\n"
            "TOU State : Disabled\n";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }

}

BOOST_AUTO_TEST_SUITE_END()
