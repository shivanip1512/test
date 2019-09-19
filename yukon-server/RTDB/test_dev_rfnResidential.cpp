#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rfnResidential.h"
#include "cmd_rfn.h"
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"
#include <boost/variant.hpp>
#include <boost/range/algorithm/for_each.hpp>

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnResidentialDevice : RfnResidentialDevice
{
    using RfnResidentialDevice::isDisconnectConfigSupported;
    using CtiDeviceBase::setDeviceType;

    test_RfnResidentialDevice()
    {
        _name= "test";
    }

    bool isDemandIntervalConfigSupported() const override
    {
        return true;
    }

    bool areAggregateCommandsSupported() const override 
    {
        return false;
    }
};

struct test_state_rfnResidential
{
    std::unique_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfnResidential() :
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


BOOST_FIXTURE_TEST_SUITE( test_dev_rfnResidential, test_state_rfnResidential )

BOOST_AUTO_TEST_CASE( test_isDisconnectSupported )
{
    test_RfnResidentialDevice dut;

    BOOST_CHECK( ! dut.isDisconnectConfigSupported() );

    dut.setDeviceType(TYPE_RFN520FAXD);

    BOOST_CHECK(   dut.isDisconnectConfigSupported() );

    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN410FL) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN410FX) );
    BOOST_CHECK(   test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN410FD) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420FL) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420FX) );
    BOOST_CHECK(   test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420FD) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420FRX) );
    BOOST_CHECK(   test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420FRD) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN510FL) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN520FAX) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN520FRX) );
    BOOST_CHECK(   test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN520FAXD) );
    BOOST_CHECK(   test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN520FRXD) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530FAX) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530FRX) );

    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN410CL) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420CL) );
    BOOST_CHECK(   test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN420CD) );

    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430A3D) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430A3T) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430A3K) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430A3R) );

    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430KV) );

    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430SL0) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430SL1) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430SL2) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430SL3) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN430SL4) );

    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530S4X) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530S4EAX) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530S4EAXR) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530S4ERX) );
    BOOST_CHECK( ! test_RfnResidentialDevice::isDisconnectConfigSupported(TYPE_RFN530S4ERXR) );
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_schedule )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig tou 13242313 "
                           "schedule 1 a/00:00 b/00:01 c/10:06 d/12:22 a/23:33 b/23:44 "
                           "schedule 2 d/00:00 a/01:23 b/03:12 c/04:01 d/05:23 a/16:28 "
                           "schedule 3 c/00:00 d/01:02 a/02:03 b/04:05 c/05:06 d/06:07 "
                           "schedule 4 b/00:00 c/00:01 d/08:59 a/12:12 b/23:01 c/23:55 "
                           "default b");

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

        std::vector<unsigned char> exp {
                0x60, 0x04,
                0x0A,
                0x01, 0x03, // day table
                0x50, 0x16, 0x41,
                0x02, 0x0A, // schedule 1 times
                0x00, 0x01, 0x02, 0x5d, 0x00, 0x88, 0x02, 0x9f, 0x00, 0x0b,
                0x03, 0x0A, // schedule 2 times
                0x00, 0x53, 0x00, 0x6d, 0x00, 0x31, 0x00, 0x52, 0x02, 0x99,
                0x04, 0x0A, // schedule 3 times
                0x00, 0x3e, 0x00, 0x3d, 0x00, 0x7a, 0x00, 0x3d, 0x00, 0x3d,
                0x05, 0x0A, // schedule 4 times
                0x00, 0x01, 0x02, 0x1a, 0x00, 0xc1, 0x02, 0x89, 0x00, 0x36,
                0x06, 0x03, // schedule 1 rates
                0x88, 0x86, 0x00,
                0x07, 0x03, // schedule 2 rates
                0x43, 0x34, 0x00,
                0x08, 0x03, // schedule 3 rates
                0x1A, 0xA2, 0x01,
                0x09, 0x03, // schedule 4 rates
                0xD1, 0x10, 0x01,
                0x0A, 0x01, // default TOU rate
                0x01 };

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_schedule_badparam )
{
    test_RfnResidentialDevice dut;

    // test wrong switch time order
    {
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:03 c/01:02 d/01:04 a/01:05 " // schedule 2 switch time 2 > switch time 3
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid switch time for SCHEDULE_2 - (01:02, expected > 01:03)";

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test first time is not midnight
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:01 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 " // schedule 3 midnight time 00:01
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid midnight time for SCHEDULE_3 - (00:01, expected 00:00)";

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test switch time out of range
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:60 a/04:03 b/04:04 c/04:05 " // schedule 4 switch time 2 set to 03:60
                               "default b");

        std::string exp = "Invalid switch time for SCHEDULE_4 - (03:60)";

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test invalid rate
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 e/00:04 b/00:05 " // schedule 1 rate 4 set to 'e'
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid rate for SCHEDULE_1 - (e)";

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test invalid change number
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 b/01:06 " // schedule 2 changes == 7
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid number of switch time for SCHEDULE_2 - (7, expected 6)";

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test unexpected schedule
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "schedule 5 a/00:00 b/04:01 c/04:02 d/04:03 a/04:04 b/04:05 " // unexpected schedule 5
                               "default b");

        std::string exp = "Invalid schedule - (SCHEDULE_5)";

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_enable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig tou enable");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_CHECK_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        auto& command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x01)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_disable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig tou disable");

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

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x02)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_getconfig_tou_schedule )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig tou");

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

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x05)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_install )
{
    typedef std::map<CtiDeviceBase::PaoInfoKeys, std::string> TouScheduleCompareKeysMap;

    const TouScheduleCompareKeysMap touScheduleCompareKeys {
    // day table
    { CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,    RfnStrings::MondaySchedule    },
    { CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,   RfnStrings::TuesdaySchedule   },
    { CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule, RfnStrings::WednesdaySchedule },
    { CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,  RfnStrings::ThursdaySchedule  },
    { CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,    RfnStrings::FridaySchedule    },
    { CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,  RfnStrings::SaturdaySchedule  },
    { CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,    RfnStrings::SundaySchedule    },
    { CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,   RfnStrings::HolidaySchedule   },
    // default rate
    { CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate,    RfnStrings::DefaultTouRate    },
    // schedule 1
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0,    RfnStrings::Schedule1Rate0    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1,    RfnStrings::Schedule1Time1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1,    RfnStrings::Schedule1Rate1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2,    RfnStrings::Schedule1Time2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2,    RfnStrings::Schedule1Rate2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3,    RfnStrings::Schedule1Time3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3,    RfnStrings::Schedule1Rate3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4,    RfnStrings::Schedule1Time4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4,    RfnStrings::Schedule1Rate4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5,    RfnStrings::Schedule1Time5    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5,    RfnStrings::Schedule1Rate5    },
    // schedule 2
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0,    RfnStrings::Schedule2Rate0    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1,    RfnStrings::Schedule2Time1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1,    RfnStrings::Schedule2Rate1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2,    RfnStrings::Schedule2Time2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2,    RfnStrings::Schedule2Rate2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3,    RfnStrings::Schedule2Time3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3,    RfnStrings::Schedule2Rate3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4,    RfnStrings::Schedule2Time4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4,    RfnStrings::Schedule2Rate4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5,    RfnStrings::Schedule2Time5    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5,    RfnStrings::Schedule2Rate5    },
    // schedule 3
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0,    RfnStrings::Schedule3Rate0    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1,    RfnStrings::Schedule3Time1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1,    RfnStrings::Schedule3Rate1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2,    RfnStrings::Schedule3Time2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2,    RfnStrings::Schedule3Rate2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3,    RfnStrings::Schedule3Time3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3,    RfnStrings::Schedule3Rate3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4,    RfnStrings::Schedule3Time4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4,    RfnStrings::Schedule3Rate4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5,    RfnStrings::Schedule3Time5    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5,    RfnStrings::Schedule3Rate5    },
    // schedule 4
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0,    RfnStrings::Schedule4Rate0    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1,    RfnStrings::Schedule4Time1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1,    RfnStrings::Schedule4Rate1    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2,    RfnStrings::Schedule4Time2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2,    RfnStrings::Schedule4Rate2    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3,    RfnStrings::Schedule4Time3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3,    RfnStrings::Schedule4Rate3    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4,    RfnStrings::Schedule4Time4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4,    RfnStrings::Schedule4Rate4    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5,    RfnStrings::Schedule4Time5    },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5,    RfnStrings::Schedule4Rate5    }};

    auto & cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    // SCHEDULE_1
    cfg.insertValue( RfnStrings::Schedule1Time1, "00:01" );
    cfg.insertValue( RfnStrings::Schedule1Time2, "10:06" );
    cfg.insertValue( RfnStrings::Schedule1Time3, "12:22" );
    cfg.insertValue( RfnStrings::Schedule1Time4, "23:33" );
    cfg.insertValue( RfnStrings::Schedule1Time5, "23:44" );

    cfg.insertValue( RfnStrings::Schedule1Rate0, "A" );
    cfg.insertValue( RfnStrings::Schedule1Rate1, "B" );
    cfg.insertValue( RfnStrings::Schedule1Rate2, "C" );
    cfg.insertValue( RfnStrings::Schedule1Rate3, "D" );
    cfg.insertValue( RfnStrings::Schedule1Rate4, "A" );
    cfg.insertValue( RfnStrings::Schedule1Rate5, "B" );

    // SCHEDULE_2
    cfg.insertValue( RfnStrings::Schedule2Time1, "01:23" );
    cfg.insertValue( RfnStrings::Schedule2Time2, "03:12" );
    cfg.insertValue( RfnStrings::Schedule2Time3, "04:01" );
    cfg.insertValue( RfnStrings::Schedule2Time4, "05:23" );
    cfg.insertValue( RfnStrings::Schedule2Time5, "16:28" );

    cfg.insertValue( RfnStrings::Schedule2Rate0, "D" );
    cfg.insertValue( RfnStrings::Schedule2Rate1, "A" );
    cfg.insertValue( RfnStrings::Schedule2Rate2, "B" );
    cfg.insertValue( RfnStrings::Schedule2Rate3, "C" );
    cfg.insertValue( RfnStrings::Schedule2Rate4, "D" );
    cfg.insertValue( RfnStrings::Schedule2Rate5, "A" );

    // SCHEDULE_3
    cfg.insertValue( RfnStrings::Schedule3Time1, "01:02" );
    cfg.insertValue( RfnStrings::Schedule3Time2, "02:03" );
    cfg.insertValue( RfnStrings::Schedule3Time3, "04:05" );
    cfg.insertValue( RfnStrings::Schedule3Time4, "05:06" );
    cfg.insertValue( RfnStrings::Schedule3Time5, "06:07" );

    cfg.insertValue( RfnStrings::Schedule3Rate0, "C" );
    cfg.insertValue( RfnStrings::Schedule3Rate1, "D" );
    cfg.insertValue( RfnStrings::Schedule3Rate2, "A" );
    cfg.insertValue( RfnStrings::Schedule3Rate3, "B" );
    cfg.insertValue( RfnStrings::Schedule3Rate4, "C" );
    cfg.insertValue( RfnStrings::Schedule3Rate5, "D" );

    // SCHEDULE_4
    cfg.insertValue( RfnStrings::Schedule4Time1, "00:01" );
    cfg.insertValue( RfnStrings::Schedule4Time2, "08:59" );
    cfg.insertValue( RfnStrings::Schedule4Time3, "12:12" );
    cfg.insertValue( RfnStrings::Schedule4Time4, "23:01" );
    cfg.insertValue( RfnStrings::Schedule4Time5, "23:55" );

    cfg.insertValue( RfnStrings::Schedule4Rate0, "B" );
    cfg.insertValue( RfnStrings::Schedule4Rate1, "C" );
    cfg.insertValue( RfnStrings::Schedule4Rate2, "D" );
    cfg.insertValue( RfnStrings::Schedule4Rate3, "A" );
    cfg.insertValue( RfnStrings::Schedule4Rate4, "B" );
    cfg.insertValue( RfnStrings::Schedule4Rate5, "C" );

    // day table
    cfg.insertValue( RfnStrings::SundaySchedule,    "SCHEDULE_1" );
    cfg.insertValue( RfnStrings::MondaySchedule,    "SCHEDULE_1" );
    cfg.insertValue( RfnStrings::TuesdaySchedule,   "SCHEDULE_3" );
    cfg.insertValue( RfnStrings::WednesdaySchedule, "SCHEDULE_2" );
    cfg.insertValue( RfnStrings::ThursdaySchedule,  "SCHEDULE_4" );
    cfg.insertValue( RfnStrings::FridaySchedule,    "SCHEDULE_2" );
    cfg.insertValue( RfnStrings::SaturdaySchedule,  "SCHEDULE_3" );
    cfg.insertValue( RfnStrings::HolidaySchedule,   "SCHEDULE_3" );

    // default rate
    cfg.insertValue( RfnStrings::DefaultTouRate, "B" );

    // set TOU enabled
    cfg.insertValue( RfnStrings::touEnabled, "true" );

    {
        //
        // Test Put Config Install
        //

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPaoInfo for this scope

        test_RfnResidentialDevice dut;

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou verify");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
            BOOST_REQUIRE_EQUAL( 3, returnMsgs.size()  );

            auto returnMsgItr = returnMsgs.begin();
            {
                auto & returnMsg = **returnMsgItr;
                BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::ConfigNotCurrent );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config TOU Schedule did not match." );
            }
            {
                auto & returnMsg = **(++returnMsgItr);
                BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::ConfigNotCurrent );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config TOU Enable did not match." );
            }
            {
                auto & returnMsg = **(++returnMsgItr);
                BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::ConfigNotCurrent );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is NOT current." );
                }
        }

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
            BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

            {
                const auto & returnMsg = *returnMsgs.front();

                BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
            }

            {
                {
                    auto& command = rfnRequests[0];

                    Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                    std::vector<unsigned char> exp = boost::assign::list_of
                            (0x60)(0x01)    // enable TOU
                            (0x00);

                    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                                   exp.begin() , exp.end() );

                    dut.extractCommandResult( *command );

                    boost::optional<bool>   dynamicInfo,
                                            configValue;

                    BOOST_CHECK( (configValue = cfg.findValue<bool>( RfnStrings::touEnabled ))
                                 && (dynamicInfo = dut.findDynamicInfo<bool>( CtiTableDynamicPaoInfo::Key_RFN_TouEnabled ))
                                 && configValue == dynamicInfo );
                }
                {
                    auto& command = rfnRequests[1];

                    Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                    std::vector<unsigned char> exp = boost::assign::list_of
                            (0x60)(0x04)
                            (0x0A)
                            (0x01)(0x03) // day table
                            (0x80)(0xB2)(0x48)
                            (0x02)(0x0A) // schedule 1 times
                            (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                            (0x03)(0x0A) // schedule 2 times
                            (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                            (0x04)(0x0A) // schedule 3 times
                            (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                            (0x05)(0x0A) // schedule 4 times
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

                    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                                   exp.begin() , exp.end() );

                    dut.extractCommandResult( *command );

                    const std::vector<bool> dynamicInfo_exp( touScheduleCompareKeys.size(), true );
                          std::vector<bool> dynamicInfo_rcv;

                    for( const auto & p : touScheduleCompareKeys )
                    {
                        std::string dynamicInfo;
                        dynamicInfo_rcv.push_back( dut.getDynamicInfo( p.first, dynamicInfo ) &&
                                dynamicInfo == cfg.findValue<std::string>( p.second ) );
                    }

                    BOOST_CHECK_EQUAL_COLLECTIONS( dynamicInfo_rcv.begin(), dynamicInfo_rcv.end(),
                                                   dynamicInfo_exp.begin(), dynamicInfo_exp.end() );
                }
            }
        }

        //
        // NOTE: At this point the configuration is expected to be valid
        //

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size()  );

            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is current." );
        }

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou verify");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size()  );

            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is current." );
        }

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou force");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
            BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

            {
                const auto & returnMsg = *returnMsgs.front();

                BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
            }

            {
                {
                    auto& command = rfnRequests[0];

                    Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                    std::vector<unsigned char> exp = boost::assign::list_of
                            (0x60)(0x01)    // enable TOU
                            (0x00);

                    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                                   exp.begin() , exp.end() );
                }
                {
                    auto& command = rfnRequests[1];

                    Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                    std::vector<unsigned char> exp = boost::assign::list_of
                            (0x60)(0x04)
                            (0x0A)
                            (0x01)(0x03) // day table
                            (0x80)(0xB2)(0x48)
                            (0x02)(0x0A) // schedule 1 times
                            (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                            (0x03)(0x0A) // schedule 2 times
                            (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                            (0x04)(0x0A) // schedule 3 times
                            (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                            (0x05)(0x0A) // schedule 4 times
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

                    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                                   exp.begin() , exp.end() );
                }
            }
        }
    }

    {
        //
        // Test Get Config Install
        //
        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPaoInfo for this scope

        test_RfnResidentialDevice dut;

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou verify");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
            BOOST_REQUIRE_EQUAL( 3, returnMsgs.size()  );

            auto returnMsgItr = returnMsgs.begin();
            {
                auto & returnMsg = **returnMsgItr;
                BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::ConfigNotCurrent );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config TOU Schedule did not match." );
            }
            {
                auto & returnMsg = **( ++returnMsgItr );
                BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::ConfigNotCurrent );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config TOU Enable did not match." );
            }
            {
                auto & returnMsg = **(++returnMsgItr);
                BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::ConfigNotCurrent );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is NOT current." );
            }
        }

        {
            resetTestState();
            CtiCommandParser parse("getconfig install tou");

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

                Commands::RfnCommand::RfnRequestPayload request_rcv = command->executeCommand( execute_time );

                std::vector<unsigned char> request_exp = boost::assign::list_of
                        (0x60)(0x05)(0x00);

                BOOST_CHECK_EQUAL_COLLECTIONS( request_rcv.begin() , request_rcv.end() ,
                                               request_exp.begin() , request_exp.end() );

                std::vector<unsigned char> response = boost::assign::list_of
                        (0x61)(0x00)(0x00)(0x00)
                        (0x01)       // TOU enabled
                        (0x0A)
                        (0x01)(0x03) // day table
                        (0x80)(0xB2)(0x48)
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

                command->handleResponse( decode_time, response );

                dut.extractCommandResult( *command );

                std::string dynamicInfo;
                boost::optional<std::string> configValue;

                for( const auto & p : touScheduleCompareKeys )
                {
                    BOOST_CHECK( (configValue = cfg.findValue<std::string>( p.second ))
                            && dut.getDynamicInfo( p.first, dynamicInfo )
                            && *configValue == dynamicInfo );
                }
            }
        }

        //
        // NOTE: At this point the configuration is expected to be valid
        //

        {
            resetTestState();
            CtiCommandParser parse("putconfig install tou verify");

            BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size()  );

            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ClientErrors::None );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is current." );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_holiday )
{
    Cti::Test::set_to_central_timezone();

    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon holiday 02/01/2025 06/14/2036 12/30/2050");

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

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x06)
                (0x01)
                (0x0C)(0x0C)
                (0x67)(0x9d)(0xb8)(0x60)
                (0x7c)(0xfe)(0x2c)(0xd0)
                (0x98)(0x59)(0x5a)(0xe0);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_holiday_active )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon holiday active");

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

        const std::vector< unsigned char > exp = boost::assign::list_of
                (0x60)(0x0C)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_tou_holiday_cancel )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon holiday cancel");

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

        const std::vector< unsigned char > exp = boost::assign::list_of
                (0x60)(0x0D)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_getconfig_tou_holiday )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig emetcon holiday");

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

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x07)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_disconnect_on_demand )
{
    test_RfnResidentialDevice dut;

    dut.setDeviceType(TYPE_RFN420CD); // Make it a disconnect type.

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::DisconnectMode, "ON_DEMAND" );
    cfg.insertValue( RfnStrings::ReconnectParam, "ARM" );
    {
        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        auto& command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x82) // remote disconnect
                    (0x00) // set configuration
                    (0x01) // 1 tlv
                    (0x01) // on-demand
                    (0x01) // length 1
                    (0x00);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x83)  //  response
                    (0x00)  //  set config
                    (0x00)  //  success
                    (0x01)  //  current disconnect mode (on-demand)
                    (0x01)  //  1 tlv
                    (0x01)  //  type 1 (on-demand)
                    (0x01)  //  length 1
                    (0x00); //  reconnect:  arm

            const auto results = command->handleResponse( decode_time, response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            const std::string exp = "Remote Disconnect Set Configuration Request:"
                                    "\nStatus: Success (0)"
                                    "\nDisconnect mode: On Demand"
                                    "\nReconnect param: Arm reconnect";

            BOOST_CHECK_EQUAL(result.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string disconnectMode, reconnectParam;

        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, disconnectMode );
        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam, reconnectParam );

        BOOST_CHECK_EQUAL( disconnectMode, "ON_DEMAND" );
        BOOST_CHECK_EQUAL( reconnectParam, "ARM" );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_disconnect_demand_threshold )
{
    test_RfnResidentialDevice dut;

    dut.setDeviceType(TYPE_RFN420CD); // Make it a disconnect type.

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::DisconnectMode, "DEMAND_THRESHOLD" );
    cfg.insertValue( RfnStrings::ReconnectParam, "IMMEDIATE" );
    cfg.insertValue( RfnStrings::DisconnectDemandInterval, "5" );
    cfg.insertValue( RfnStrings::DisconnectDemandThreshold, "10.2" );
    cfg.insertValue( RfnStrings::LoadLimitConnectDelay, "15" );
    cfg.insertValue( RfnStrings::MaxDisconnects, "10" );
    {
        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        auto& command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x82) // remote disconnect
                    (0x00) // set configuration
                    (0x01) // 1 tlv
                    (0x02) // demand-threshold
                    (0x05) // length 5
                    (0x01)(0x05)(0x66)(0x0f)(0x0a);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x83)(0x00)(0x00)(0x02)(0x01)(0x02)(0x05)(0x01)(0x05)(0x66)(0x0f)(0x0a);

            const auto results = command->handleResponse( decode_time, response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            BOOST_CHECK_EQUAL( result.description,
                                     "Remote Disconnect Set Configuration Request:"
                                     "\nStatus: Success (0)"
                                     "\nDisconnect mode: Demand Threshold"
                                     "\nReconnect param: Immediate reconnect"
                                     "\nDisconnect demand interval: 5 minutes"
                                     "\nDisconnect demand threshold: 10.2 kW"
                                     "\nConnect delay: 15 minutes"
                                     "\nMax disconnects: 10" );
        }

        dut.extractCommandResult( *command );

        std::string disconnectMode, reconnectParam;
        double threshold;

        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, disconnectMode );
        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam, reconnectParam );
        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandThreshold, threshold );

        BOOST_CHECK_EQUAL( disconnectMode, "DEMAND_THRESHOLD" );
        BOOST_CHECK_EQUAL( reconnectParam, "IMMEDIATE" );
        BOOST_CHECK_CLOSE( threshold, 10.2, 1e-5);
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectDemandInterval ), 5 );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ConnectDelay ), 15 );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_MaxDisconnects ), 10 );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_disconnect_cycling )
{
    test_RfnResidentialDevice dut;

    dut.setDeviceType(TYPE_RFN420CD); // Make it a disconnect type.

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::DisconnectMode, "CYCLING" );
    cfg.insertValue( RfnStrings::DisconnectMinutes, "10" );
    cfg.insertValue( RfnStrings::ConnectMinutes, "20" );
    {
        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        auto& command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x82) // remote disconnect
                    (0x00) // set configuration
                    (0x01) // 1 tlv
                    (0x03) // on-demand
                    (0x05) // length 5
                    (0x01)(0x00)(0x0a)(0x00)(0x14);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x83)(0x00)(0x00)(0x03)(0x01)(0x03)(0x05)(0x01)(0x00)(0x0a)(0x00)(0x14);

            const auto results = command->handleResponse( decode_time, response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            const std::string exp =
                    "Remote Disconnect Set Configuration Request:"
                    "\nStatus: Success (0)"
                    "\nDisconnect mode: Cycling"
                    "\nReconnect param: Immediate reconnect"
                    "\nDisconnect minutes: 10"
                    "\nConnect minutes: 20";

            BOOST_CHECK_EQUAL(result.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string disconnectMode;

        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, disconnectMode );

        BOOST_CHECK_EQUAL( disconnectMode, "CYCLING" );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMinutes ), 10 );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ConnectMinutes ), 20 );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_disconnect_invalid_config )
{
    test_RfnResidentialDevice dut;

    dut.setDeviceType(TYPE_RFN420CD); // Make it a disconnect type.

    {
        ///// Missing config data /////

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 2, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        {
            CtiReturnMsg &returnMsg = *returnMsgs.front();
            BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::NoConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Missing data for config key \"disconnectMode\"." );
        }
        {
            CtiReturnMsg &returnMsg = *returnMsgs.back();
            BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::NoConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "ERROR: Device had no configuration for config:disconnect" );
        }

    }

    {
        resetTestState();

        ///// Invalid config data /////

        Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        // add remote disconnect config
        cfg.insertValue( RfnStrings::DisconnectMode, "NOT_A_DISCONNECT_MODE" ); // invalid disconnect mode

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 2, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        {
            CtiReturnMsg &returnMsg = *returnMsgs.front();
            BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::InvalidConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Invalid data for config key \"disconnectMode\" : invalid value NOT_A_DISCONNECT_MODE, expected [CYCLING; DEMAND_THRESHOLD; ON_DEMAND]." );
        }
        {
            CtiReturnMsg &returnMsg = *returnMsgs.back();
            BOOST_CHECK_EQUAL( returnMsg.Status(), ClientErrors::InvalidConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "ERROR: NoMethod or invalid config. Config name:disconnect" );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_voltage_profile )
{
    test_RfnResidentialDevice dut;

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

BOOST_AUTO_TEST_CASE( test_putconfig_voltage_profile_enable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon voltage profile enable");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       202 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "No Method or Invalid Command." );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_voltage_profile_disable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon voltage profile disable");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       202 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "No Method or Invalid Command." );
    }
}

BOOST_AUTO_TEST_CASE( test_getconfig_voltage_profile )
{
    test_RfnResidentialDevice dut;

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

BOOST_AUTO_TEST_CASE( test_getvalue_voltage_profile_state )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig voltage profile state");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

    {
        const auto & returnMsg = *returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       202 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "No Method or Invalid Command." );
    }
}

BOOST_AUTO_TEST_CASE( test_immediate_demand_freeze )
{
    test_RfnResidentialDevice    dev;

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

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_tou_critical_peak_cancel )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus tou critical peak cancel");

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

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x60 )( 0x09 )( 0x00 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_tou_critical_peak_today )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus tou critical peak rate b until 23:00");

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

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x60 )( 0x08 )( 0x01 )( 0x0b )( 0x05 )( 0x01 )( 0x52 )( 0x1d )( 0x75 )( 0xc0 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_tou_critical_peak_tomorrow )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus tou critical peak rate b until 8:00");

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

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x60 )( 0x08 )( 0x01 )( 0x0b )( 0x05 )( 0x01 )( 0x52 )( 0x1d )( 0xf4 )( 0x50 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_freezeday )
{
    test_RfnResidentialDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::demandFreezeDay, "7" );

    dut.setDeviceType(TYPE_RFN410FX);

    BOOST_CHECK( ! dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay) );

    {
        CtiCommandParser parse("putconfig install freezeday");

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

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x55)(0x02)(0x07);

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response = boost::assign::list_of
                        (0x56)(0x00)(0x00)(0x00)(0x00);

            command->handleResponse( CtiTime::now(), response );

            dut.extractCommandResult( *command );

            BOOST_CHECK( dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay) );

            BOOST_CHECK_EQUAL( 7, dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay) );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_channel_configuration )
{
    test_RfnResidentialDevice dut;

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
            { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" } };

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

            command->handleResponse( CtiTime::now(), response );

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
            auto& command = *rfnRequest_itr++;

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

            const auto results = command->handleResponse( CtiTime::now(), response );

            BOOST_REQUIRE_EQUAL( results.size(), 1 );

            const auto & result = results.front();

            BOOST_CHECK_EQUAL(
                    result.description,
                    "Channel Interval Recording Request:"
                    "\nStatus: Success (0)\n"
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

            const auto dynMetricsRcv = dut.findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics );

            BOOST_REQUIRE( !! dynMetricsRcv );
            BOOST_CHECK_EQUAL_COLLECTIONS( dynMetricsRcv->begin(), dynMetricsRcv->end(), dynMetricsExp.begin(), dynMetricsExp.end() );

            const auto recordingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds );
            const auto reportingIntervalRcv = dut.findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds );

            BOOST_REQUIRE( !! recordingIntervalRcv );
            BOOST_REQUIRE( !! reportingIntervalRcv );

            BOOST_CHECK_EQUAL( unsigned(7380),  *recordingIntervalRcv );
            BOOST_CHECK_EQUAL( unsigned(27360), *reportingIntervalRcv );
        }
    }
}

BOOST_AUTO_TEST_CASE(test_putconfig_install_demand_interval)
{
    fixtureConfig->addCategory(
        Cti::Config::Category::ConstructCategory(
            "rfnDemand",
            std::map<std::string, std::string> { { RfnStrings::demandInterval, "11" } }));

    {
        struct : test_RfnResidentialDevice
        {
            bool isDemandIntervalConfigSupported() const override {  return false;  }
        } dut;

        CtiCommandParser parse("putconfig install demand");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));

        BOOST_CHECK(rfnRequests.empty());

        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 202);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "No Method or Invalid Command.");
        }

        returnMsgs.clear();
    }

    {
        struct : test_RfnResidentialDevice
        {
            bool isDemandIntervalConfigSupported() const override {  return true;  }
        } dut;

        CtiCommandParser parse("putconfig install demand");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));

        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        {
            const auto & returnMsg = *returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }

        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());
        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            auto& command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

            std::vector<unsigned char> exp { 0x62, 0x0b };

            BOOST_CHECK_EQUAL(rcv, exp);

            std::vector<unsigned char> response { 0x63, 0x00 };

            const auto results = command->handleResponse(CtiTime::now(), response);

            BOOST_REQUIRE_EQUAL(results.size(), 1);

            const auto & result = results.front();

            BOOST_CHECK_EQUAL(
                result.description,
                "Set Demand Interval Request:"
                "\nSuccessfully set demand interval to 11 minutes");

            dut.extractCommandResult(*command);

            const auto demandInterval = dut.findDynamicInfo<unsigned long>(CtiTableDynamicPaoInfo::Key_RFN_DemandInterval);

            BOOST_REQUIRE(demandInterval.is_initialized());
            BOOST_CHECK_EQUAL(demandInterval.get(), 11);
        }
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_all_device )
{
    using boost::assign::list_of;

    test_RfnResidentialDevice dut;
    dut.setDeviceType(TYPE_RFN410FX);

    using CategoryItems      = std::map<std::string, std::string>;
    using CategoryDefinition = std::pair<std::string, CategoryItems>;
    using ConfigInstallItems = std::vector<CategoryDefinition>;

    const ConfigInstallItems configurations {

            // demand freeze day config
            CategoryDefinition { "demandFreeze", 
                CategoryItems {
                    { RfnStrings::demandFreezeDay, "7" } } },

            // TOU config
            CategoryDefinition { "tou", 
                CategoryItems {
                    // SCHEDULE_1
                    { RfnStrings::Schedule1Time0, "00:00" },
                    { RfnStrings::Schedule1Time1, "00:01" },
                    { RfnStrings::Schedule1Time2, "10:06" },
                    { RfnStrings::Schedule1Time3, "12:22" },
                    { RfnStrings::Schedule1Time4, "23:33" },
                    { RfnStrings::Schedule1Time5, "23:44" },

                    { RfnStrings::Schedule1Rate0, "A" },
                    { RfnStrings::Schedule1Rate1, "B" },
                    { RfnStrings::Schedule1Rate2, "C" },
                    { RfnStrings::Schedule1Rate3, "D" },
                    { RfnStrings::Schedule1Rate4, "A" },
                    { RfnStrings::Schedule1Rate5, "B" },

                    // SCHEDULE_2
                    { RfnStrings::Schedule2Time0, "00:00" },
                    { RfnStrings::Schedule2Time1, "01:23" },
                    { RfnStrings::Schedule2Time2, "03:12" },
                    { RfnStrings::Schedule2Time3, "04:01" },
                    { RfnStrings::Schedule2Time4, "05:23" },
                    { RfnStrings::Schedule2Time5, "16:28" },

                    { RfnStrings::Schedule2Rate0, "D" },
                    { RfnStrings::Schedule2Rate1, "A" },
                    { RfnStrings::Schedule2Rate2, "B" },
                    { RfnStrings::Schedule2Rate3, "C" },
                    { RfnStrings::Schedule2Rate4, "D" },
                    { RfnStrings::Schedule2Rate5, "A" },

                    // SCHEDULE_3
                    { RfnStrings::Schedule3Time0, "00:00" },
                    { RfnStrings::Schedule3Time1, "01:02" },
                    { RfnStrings::Schedule3Time2, "02:03" },
                    { RfnStrings::Schedule3Time3, "04:05" },
                    { RfnStrings::Schedule3Time4, "05:06" },
                    { RfnStrings::Schedule3Time5, "06:07" },

                    { RfnStrings::Schedule3Rate0, "C" },
                    { RfnStrings::Schedule3Rate1, "D" },
                    { RfnStrings::Schedule3Rate2, "A" },
                    { RfnStrings::Schedule3Rate3, "B" },
                    { RfnStrings::Schedule3Rate4, "C" },
                    { RfnStrings::Schedule3Rate5, "D" },

                    // SCHEDULE_4
                    { RfnStrings::Schedule4Time0, "00:00" },
                    { RfnStrings::Schedule4Time1, "00:01" },
                    { RfnStrings::Schedule4Time2, "08:59" },
                    { RfnStrings::Schedule4Time3, "12:12" },
                    { RfnStrings::Schedule4Time4, "23:01" },
                    { RfnStrings::Schedule4Time5, "23:55" },

                    { RfnStrings::Schedule4Rate0, "B" },
                    { RfnStrings::Schedule4Rate1, "C" },
                    { RfnStrings::Schedule4Rate2, "D" },
                    { RfnStrings::Schedule4Rate3, "A" },
                    { RfnStrings::Schedule4Rate4, "B" },
                    { RfnStrings::Schedule4Rate5, "C" },

                    // day table
                    { RfnStrings::SundaySchedule,    "SCHEDULE_1" },
                    { RfnStrings::MondaySchedule,    "SCHEDULE_1" },
                    { RfnStrings::TuesdaySchedule,   "SCHEDULE_3" },
                    { RfnStrings::WednesdaySchedule, "SCHEDULE_2" },
                    { RfnStrings::ThursdaySchedule,  "SCHEDULE_4" },
                    { RfnStrings::FridaySchedule,    "SCHEDULE_2" },
                    { RfnStrings::SaturdaySchedule,  "SCHEDULE_3" },
                    { RfnStrings::HolidaySchedule,   "SCHEDULE_3" },

                    // default rate
                    { RfnStrings::DefaultTouRate, "B" },

                    // set TOU enabled
                    { RfnStrings::touEnabled, "true" } } },

            // temperature alarming config
            CategoryDefinition { "rfnTempAlarm", 
                CategoryItems {
                    { RfnStrings::TemperatureAlarmEnabled,           "true" },
                    { RfnStrings::TemperatureAlarmRepeatInterval,    "15"   },
                    { RfnStrings::TemperatureAlarmRepeatCount,       "3"    },
                    { RfnStrings::TemperatureAlarmHighTempThreshold, "50"   } } },

            // channel config
            CategoryDefinition { "rfnChannelConfiguration", 
                CategoryItems {
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" },
                    { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
                    { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" } } },
                
            // demand interval
            CategoryDefinition { "rfnDemand", 
                CategoryItems {
                    { RfnStrings::demandInterval, "13" } } }
            };

    const std::vector<int> requestMsgsExp {
            0,   // no config data                   -> no request
            1,   // add demand freeze day config     -> +1 request
            3,   // add TOU config                   -> +2 request
            4,   // add temperature alarming config  -> +1 request
            6,   // add channel config               -> +2 request
            7,   // add demand interval config       -> +1 request
            };

    const std::vector< std::vector<bool> > returnExpectMoreExp {
            { true, true, true, true, true, true, true, false },
                                                      // no config data                   -> 8 error messages, NOTE: last expectMore expected to be false
            { true, true, true, true, true, true, true },
                                                      // add demand freeze day config     -> 7 error messages
            { true, true, true, true, true },         // add TOU config                   -> 5 error messages
            { true, true, true },                     // add temperature alarming config  -> 3 error messages
            { true },                                 // add channel config               -> config sent successfully
            { true },                                 // add demand interval config       -> config sent successfully
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

    for( const auto & category : configurations )
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


BOOST_AUTO_TEST_CASE( test_putconfig_install_groupMessageCount )
{
    using boost::assign::list_of;

    test_RfnResidentialDevice dut;
    dut.setDeviceType(TYPE_RFN410FX);

    typedef std::map<std::string, std::string>    CategoryItems;
    typedef std::pair<std::string, CategoryItems> CategoryDefinition;
    typedef std::vector<CategoryDefinition>       ConfigInstallItems;

    const ConfigInstallItems configurations {

            // demand freeze day config
            CategoryDefinition { "demandFreeze",
                CategoryItems {
                    { RfnStrings::demandFreezeDay, "7" } } },

            // TOU config
            CategoryDefinition { "tou", 
                CategoryItems {
                    // SCHEDULE_1
                    { RfnStrings::Schedule1Time0, "00:00" },
                    { RfnStrings::Schedule1Rate0, "A" },
                    { RfnStrings::Schedule1Time1, "00:01" },
                    { RfnStrings::Schedule1Rate1, "B" },
                    { RfnStrings::Schedule1Time2, "10:06" },
                    { RfnStrings::Schedule1Rate2, "C" },
                    { RfnStrings::Schedule1Time3, "12:22" },
                    { RfnStrings::Schedule1Rate3, "D" },
                    { RfnStrings::Schedule1Time4, "23:33" },
                    { RfnStrings::Schedule1Rate4, "A" },
                    { RfnStrings::Schedule1Time5, "23:44" },
                    { RfnStrings::Schedule1Rate5, "B" },

                    // SCHEDULE_2
                    { RfnStrings::Schedule2Time0, "00:00" },
                    { RfnStrings::Schedule2Rate0, "D" },
                    { RfnStrings::Schedule2Time1, "01:23" },
                    { RfnStrings::Schedule2Rate1, "A" },
                    { RfnStrings::Schedule2Time2, "03:12" },
                    { RfnStrings::Schedule2Rate2, "B" },
                    { RfnStrings::Schedule2Time3, "04:01" },
                    { RfnStrings::Schedule2Rate3, "C" },
                    { RfnStrings::Schedule2Time4, "05:23" },
                    { RfnStrings::Schedule2Rate4, "D" },
                    { RfnStrings::Schedule2Time5, "16:28" },
                    { RfnStrings::Schedule2Rate5, "A" },

                    // SCHEDULE_3
                    { RfnStrings::Schedule3Time0, "00:00" },
                    { RfnStrings::Schedule3Rate0, "C" },
                    { RfnStrings::Schedule3Time1, "01:02" },
                    { RfnStrings::Schedule3Rate1, "D" },
                    { RfnStrings::Schedule3Time2, "02:03" },
                    { RfnStrings::Schedule3Rate2, "A" },
                    { RfnStrings::Schedule3Time3, "04:05" },
                    { RfnStrings::Schedule3Rate3, "B" },
                    { RfnStrings::Schedule3Time4, "05:06" },
                    { RfnStrings::Schedule3Rate4, "C" },
                    { RfnStrings::Schedule3Time5, "06:07" },
                    { RfnStrings::Schedule3Rate5, "D" },

                    // SCHEDULE_4
                    { RfnStrings::Schedule4Time0, "00:00" },
                    { RfnStrings::Schedule4Rate0, "B" },
                    { RfnStrings::Schedule4Time1, "00:01" },
                    { RfnStrings::Schedule4Rate1, "C" },
                    { RfnStrings::Schedule4Time2, "08:59" },
                    { RfnStrings::Schedule4Rate2, "D" },
                    { RfnStrings::Schedule4Time3, "12:12" },
                    { RfnStrings::Schedule4Rate3, "A" },
                    { RfnStrings::Schedule4Time4, "23:01" },
                    { RfnStrings::Schedule4Rate4, "B" },
                    { RfnStrings::Schedule4Time5, "23:55" },
                    { RfnStrings::Schedule4Rate5, "C" },

                    // day table
                    { RfnStrings::SundaySchedule,    "SCHEDULE_1" },
                    { RfnStrings::MondaySchedule,    "SCHEDULE_1" },
                    { RfnStrings::TuesdaySchedule,   "SCHEDULE_3" },
                    { RfnStrings::WednesdaySchedule, "SCHEDULE_2" },
                    { RfnStrings::ThursdaySchedule,  "SCHEDULE_4" },
                    { RfnStrings::FridaySchedule,    "SCHEDULE_2" },
                    { RfnStrings::SaturdaySchedule,  "SCHEDULE_3" },
                    { RfnStrings::HolidaySchedule,   "SCHEDULE_3" },

                    // default rate
                    { RfnStrings::DefaultTouRate, "B" },

                    // set TOU enabled
                    { RfnStrings::touEnabled, "true" } } },

            // temperature alarming config
            CategoryDefinition { "rfnTempAlarm", 
                CategoryItems {
                    { RfnStrings::TemperatureAlarmEnabled,           "true" },
                    { RfnStrings::TemperatureAlarmRepeatInterval,    "15"   },
                    { RfnStrings::TemperatureAlarmRepeatCount,       "3"    },
                    { RfnStrings::TemperatureAlarmHighTempThreshold, "122"  } } },

            // channel config
            CategoryDefinition { "rfnChannelConfiguration", 
                CategoryItems {
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
                      + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" },
                    { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
                    { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" } } },

            // demand interval
            CategoryDefinition { "rfnDemand", 
                CategoryItems {
                    { RfnStrings::demandInterval, "11" } } }
            };

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

    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, "7380");
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, "27360");
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, std::vector<unsigned long>() );
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, std::vector<unsigned long>(1, 1) );

    CtiCommandParser parse("putconfig install all");

    auto & cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    for( const auto & category : configurations )
    {
        cfg.addCategory(
                Cti::Config::Category::ConstructCategory(
                        category.first,
                        category.second));
    }

    request->setUserMessageId(11235);

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

    BOOST_CHECK_EQUAL( 1, rfnRequests.size() );

    std::vector<bool> expectMoreRcv;
    const std::vector<bool> expectMoreExp { true, true, true, true, true };

    std::vector<std::string> resultStringRcv;
    const std::vector<std::string> resultStringExp{
            "Config channelconfig is current.",
            "Config demand is current.",
            "Config freezeday is current.",
            "Config tou is current.",
            "1 command queued for device" };

    std::vector<int> statusRcv;
    const std::vector<int> statusExp { 0, 0, 0, 0, 0 };

    for( const auto & m : returnMsgs )
    {
        expectMoreRcv.push_back( m->ExpectMore() );
        resultStringRcv.push_back( m->ResultString() );
        statusRcv.push_back( m->Status() );
    }

    BOOST_CHECK_EQUAL_RANGES( expectMoreRcv,   expectMoreExp );
    BOOST_CHECK_EQUAL_RANGES( resultStringRcv, resultStringExp );
    BOOST_CHECK_EQUAL_RANGES( statusRcv,       statusExp );

    auto& command = rfnRequests.front();

    BOOST_CHECK_EQUAL( 1, dut.getGroupMessageCount(request->UserMessageId(), request->getConnectionHandle()) );

    {
        // execute
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x88 )( 0x00 )( 0x01 )
                ( 0x01 )( 0x07 )( 0x01 )( 0x00 )( 0x32 )( 0x00 )( 0x28 )( 0x0f )( 0x03 );

        Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );

        // decode -- success response
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x89 )( 0x00 )( 0x00 )( 0x00 );

        const auto results = command->handleResponse( decode_time, response );

        BOOST_REQUIRE_EQUAL( results.size(), 1 );

        const auto & result = results.front();

        BOOST_CHECK_EQUAL( result.description, "Set Temperature Alarm Configuration Request:"
                                               "\nStatus: Success (0)" );

        dut.extractCommandResult(*command);

        //  replicating PIL's decrement
        dut.decrementGroupMessageCount(request->UserMessageId(), request->getConnectionHandle());
    }

    BOOST_CHECK_EQUAL( 0, dut.getGroupMessageCount(request->UserMessageId(), request->getConnectionHandle() ) );
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_all_disconnect_meter )
{
    using boost::assign::list_of;

    test_RfnResidentialDevice dut;
    dut.setDeviceType(TYPE_RFN410FD);

    typedef std::map<std::string, std::string>    CategoryItems;
    typedef std::pair<std::string, CategoryItems> CategoryDefinition;
    typedef std::vector<CategoryDefinition>       ConfigInstallItems;

    const ConfigInstallItems configurations {

            CategoryDefinition { "rfnDisconnectConfiguration", 
                CategoryItems {
                    { RfnStrings::DisconnectMode, "CYCLING" },
                    { RfnStrings::ConnectMinutes, "100" },
                    { RfnStrings::DisconnectMinutes, "60" } } },

            CategoryDefinition { "demandFreeze", 
                CategoryItems {
                    { RfnStrings::demandFreezeDay, "7" } } },

            CategoryDefinition { "tou", 
                CategoryItems {
                    // SCHEDULE_1
                    { RfnStrings::Schedule1Time0, "00:00" },
                    { RfnStrings::Schedule1Time1, "00:01" },
                    { RfnStrings::Schedule1Time2, "10:06" },
                    { RfnStrings::Schedule1Time3, "12:22" },
                    { RfnStrings::Schedule1Time4, "23:33" },
                    { RfnStrings::Schedule1Time5, "23:44" },

                    { RfnStrings::Schedule1Rate0, "A" },
                    { RfnStrings::Schedule1Rate1, "B" },
                    { RfnStrings::Schedule1Rate2, "C" },
                    { RfnStrings::Schedule1Rate3, "D" },
                    { RfnStrings::Schedule1Rate4, "A" },
                    { RfnStrings::Schedule1Rate5, "B" },

                    // SCHEDULE_2
                    { RfnStrings::Schedule2Time0, "00:00" },
                    { RfnStrings::Schedule2Time1, "01:23" },
                    { RfnStrings::Schedule2Time2, "03:12" },
                    { RfnStrings::Schedule2Time3, "04:01" },
                    { RfnStrings::Schedule2Time4, "05:23" },
                    { RfnStrings::Schedule2Time5, "16:28" },

                    { RfnStrings::Schedule2Rate0, "D" },
                    { RfnStrings::Schedule2Rate1, "A" },
                    { RfnStrings::Schedule2Rate2, "B" },
                    { RfnStrings::Schedule2Rate3, "C" },
                    { RfnStrings::Schedule2Rate4, "D" },
                    { RfnStrings::Schedule2Rate5, "A" },

                    // SCHEDULE_3
                    { RfnStrings::Schedule3Time0, "00:00" },
                    { RfnStrings::Schedule3Time1, "01:02" },
                    { RfnStrings::Schedule3Time2, "02:03" },
                    { RfnStrings::Schedule3Time3, "04:05" },
                    { RfnStrings::Schedule3Time4, "05:06" },
                    { RfnStrings::Schedule3Time5, "06:07" },

                    { RfnStrings::Schedule3Rate0, "C" },
                    { RfnStrings::Schedule3Rate1, "D" },
                    { RfnStrings::Schedule3Rate2, "A" },
                    { RfnStrings::Schedule3Rate3, "B" },
                    { RfnStrings::Schedule3Rate4, "C" },
                    { RfnStrings::Schedule3Rate5, "D" },

                    // SCHEDULE_4
                    { RfnStrings::Schedule4Time0, "00:00" },
                    { RfnStrings::Schedule4Time1, "00:01" },
                    { RfnStrings::Schedule4Time2, "08:59" },
                    { RfnStrings::Schedule4Time3, "12:12" },
                    { RfnStrings::Schedule4Time4, "23:01" },
                    { RfnStrings::Schedule4Time5, "23:55" },

                    { RfnStrings::Schedule4Rate0, "B" },
                    { RfnStrings::Schedule4Rate1, "C" },
                    { RfnStrings::Schedule4Rate2, "D" },
                    { RfnStrings::Schedule4Rate3, "A" },
                    { RfnStrings::Schedule4Rate4, "B" },
                    { RfnStrings::Schedule4Rate5, "C" },

                    // day table
                    { RfnStrings::SundaySchedule,    "SCHEDULE_1" },
                    { RfnStrings::MondaySchedule,    "SCHEDULE_1" },
                    { RfnStrings::TuesdaySchedule,   "SCHEDULE_3" },
                    { RfnStrings::WednesdaySchedule, "SCHEDULE_2" },
                    { RfnStrings::ThursdaySchedule,  "SCHEDULE_4" },
                    { RfnStrings::FridaySchedule,    "SCHEDULE_2" },
                    { RfnStrings::SaturdaySchedule,  "SCHEDULE_3" },
                    { RfnStrings::HolidaySchedule,   "SCHEDULE_3" },

                    // default rate
                    { RfnStrings::DefaultTouRate, "B" },

                    // set TOU enabled
                    { RfnStrings::touEnabled, "true" } } },

            CategoryDefinition { "rfnTempAlarm", 
                CategoryItems {
                    { RfnStrings::TemperatureAlarmEnabled,           "true" },
                    { RfnStrings::TemperatureAlarmRepeatInterval,    "15"   },
                    { RfnStrings::TemperatureAlarmRepeatCount,       "3"    },
                    { RfnStrings::TemperatureAlarmHighTempThreshold, "50"   } } },

            CategoryDefinition { "rfnChannelConfiguration", 
                CategoryItems {
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "1" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" },
                    { RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0." +
                      RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" },
                    { RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" },
                    { RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" } } },

            CategoryDefinition { "rfnDemand", 
                CategoryItems {
                    { RfnStrings::demandInterval, "13" } } }
            };

    const std::vector<int> requestMsgsExp {
            0,   // no config data                   -> no request
            1,   // add remote disconnect config     -> +1 request
            2,   // add demand freeze day config     -> +1 request
            4,   // add TOU config                   -> +1 request
            5,   // add temperature alarming config  -> +1 request
            7,   // add channel config               -> +2 request
            8,   // add demand interval config       -> +1 request
            };


    const std::vector< std::vector<bool> > returnExpectMoreExp {
            { true, true, true, true, true, true, true, true, true, false },
                                                              // no config data                   -> 10 error messages, NOTE: last expectMore expected to be false
            { true, true, true, true, true, true, true, true, true },
                                                              // add remote disconnect config     -> 9 error messages
            { true, true, true, true, true, true, true },     // add demand freeze day config     -> 7 error messages
            { true, true, true, true, true },                 // add TOU config                   -> 5 error messages
            { true, true, true },                             // add temperature alarming config  -> 3 error messages
            { true },                                         // add channel config               -> config sent successfully
            { true }                                          // add demand interval config       -> config sent successfully
            };

    std::vector<int> requestMsgsRcv;
    std::vector< std::vector<bool> > returnExpectMoreRcv;

    CtiCommandParser parse("putconfig install all");

    ////// empty configuration (no valid configuration) //////

    BOOST_TEST_MESSAGE( parse.getCommandStr() << ":");

    BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests ) );

    requestMsgsRcv.push_back( rfnRequests.size() );

    returnExpectMoreRcv.push_back( Cti::Test::extractExpectMore( returnMsgs ) );

    ////// add each configuration //////

    auto & cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    for( const auto & category : configurations )
    {
        resetTestState(); // note: reset test state does not erase the current configuration

        cfg.addCategory(
                Cti::Config::Category::ConstructCategory(
                        category.first,
                        category.second));
        
        BOOST_TEST_MESSAGE( parse.getCommandStr() << ":" );

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests ) );

        requestMsgsRcv.push_back( rfnRequests.size() );

        returnExpectMoreRcv.push_back( Cti::Test::extractExpectMore( returnMsgs ) );
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( requestMsgsRcv.begin(), requestMsgsRcv.end(), requestMsgsExp.begin(), requestMsgsExp.end() );
    BOOST_CHECK_EQUAL_COLLECTIONS( returnExpectMoreRcv.begin(), returnExpectMoreRcv.end(), returnExpectMoreExp.begin(), returnExpectMoreExp.end() );
}

BOOST_AUTO_TEST_CASE( test_config_notification )
{
    auto cmd = Cti::Devices::Commands::RfnCommand::handleNodeOriginated(execute_time, test_cmd_rfn_ConfigNotification::payload);

    BOOST_REQUIRE(cmd);

    test_RfnResidentialDevice dut;

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
         
        { PI::Key_RFN_DemandFreezeDay, 32 },
         
        { PI::Key_RFN_DemandInterval, 6 },

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
