#include "dev_mct410.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "devicetypes.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

#include "boost_test_helpers.h"

#include <numeric>

using Cti::Devices::Mct410Device;
using Cti::Protocols::EmetconProtocol;
using std::string;
using std::list;

struct test_Mct410Device : Mct410Device
{
protected:
    test_Mct410Device(int type, const string &name)
    {
        setType(type);
        _name = name;
    }

public:
    typedef Mct410Device::point_info point_info;

    using MctDevice::getOperation;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct410Device::getDemandData;
    using Mct410Device::extractDynamicPaoInfo;
    using Mct410Device::executeGetConfig;
    using Mct410Device::executeGetValue;
    using Mct410Device::executePutConfig;
    using Mct410Device::executePutStatus;

    using Mct410Device::decodeDisconnectStatus;
    using Mct410Device::decodeDisconnectConfig;
    using Mct410Device::decodeDisconnectDemandLimitConfig;
    using Mct410Device::decodeDisconnectCyclingConfig;
    using Mct410Device::decodeGetValueDailyRead;

    using Mct410Device::isDailyReadVulnerableToAliasing;

    virtual LONG getDemandInterval()
    {
        return 3600;
    }

    typedef std::map<int, CtiPointSPtr>              PointOffsetMap;
    typedef std::map<CtiPointType_t, PointOffsetMap> PointTypeOffsetMap;

    PointTypeOffsetMap points;

    virtual CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type)
    {
        CtiPointSPtr point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned point_count = 0;

        for each( const std::pair<CtiPointType_t, PointOffsetMap> &p in points )
        {
            point_count += p.second.size();
        }

        switch( type )
        {
            case AnalogPointType:
            {
                Test_CtiPointAnalog *analog = new Test_CtiPointAnalog();
                analog->setPointOffset(offset);
                analog->setDeviceID(reinterpret_cast<long>(&points));
                analog->setID(point_count);
                analog->setMultiplier(1);
                analog->setDataOffset(0);
                point.reset(analog);
            }
            break;

            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            {
                Test_CtiPointAccumulator *accumulator = new Test_CtiPointAccumulator();
                accumulator->setPointOffset(offset);
                accumulator->setDeviceID(reinterpret_cast<long>(&points));
                accumulator->setID(point_count);
                accumulator->setMultiplier(1);
                accumulator->setDataOffset(0);
                point.reset(accumulator);
            }
            break;

            case StatusPointType:
            {
                Test_CtiPointStatus *status = new Test_CtiPointStatus();
                status->setPointOffset(offset);
                status->setDeviceID(reinterpret_cast<long>(&points));
                status->setID(point_count);
                point.reset(status);
            }
            break;
        }

        return point;
    }
};

struct test_Mct410IconDevice : test_Mct410Device
{
    test_Mct410IconDevice() :
        test_Mct410Device(TYPEMCT410IL, "Test MCT-410iL")
    {
    }
};

struct test_Mct410CentronDevice : test_Mct410Device
{
    test_Mct410CentronDevice() :
        test_Mct410Device(TYPEMCT410CL, "Test MCT-410cL")
    {
    }
};

struct test_Mct410FocusDevice : test_Mct410Device
{
    test_Mct410FocusDevice() :
        test_Mct410Device(TYPEMCT410FL, "Test MCT-410fL")
    {
    }
};

BOOST_AUTO_TEST_CASE(test_dev_mct410_getDemandData)
{
    test_Mct410IconDevice dev;

    const unsigned char *none = 0;

    const unsigned char even = 12;
    const unsigned char odd  = 13;

    struct demand_checks
    {
        const unsigned char raw_value[2];
        const unsigned char *freeze_counter;
        const double value;
        const bool freeze_bit;
    };

    demand_checks dc[10] = {
        {{0x30, 0x05},  none, 0.005, 1},
        {{0x30, 0x05}, &odd,  0.004, 1},
        {{0x30, 0x04},  none, 0.004, 0},
        {{0x30, 0x04}, &even, 0.004, 0},
        {{0x2f, 0x0f},  none, 38.55, 1},
        {{0x2f, 0x0f}, &odd,  38.54, 1},
        {{0x2f, 0x0e},  none, 38.54, 0},
        {{0x2f, 0x0e}, &even, 38.54, 0},
        {{0x01, 0x11},  none, 273,   1},
        {{0x01, 0x11}, &odd,  272,   1}};

    test_Mct410Device::point_info pi;

    pi = dev.getDemandData(dc[0].raw_value, 2, dc[0].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[0].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[0].freeze_bit);

    pi = dev.getDemandData(dc[1].raw_value, 2, dc[1].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[1].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[1].freeze_bit);

    pi = dev.getDemandData(dc[2].raw_value, 2, dc[2].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[2].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[2].freeze_bit);

    pi = dev.getDemandData(dc[3].raw_value, 2, dc[3].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[3].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[3].freeze_bit);

    pi = dev.getDemandData(dc[4].raw_value, 2, dc[4].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[4].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[4].freeze_bit);

    pi = dev.getDemandData(dc[5].raw_value, 2, dc[5].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[5].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[5].freeze_bit);

    pi = dev.getDemandData(dc[6].raw_value, 2, dc[6].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[6].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[6].freeze_bit);

    pi = dev.getDemandData(dc[7].raw_value, 2, dc[7].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[7].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[7].freeze_bit);

    pi = dev.getDemandData(dc[8].raw_value, 2, dc[8].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[8].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[8].freeze_bit);

    pi = dev.getDemandData(dc[9].raw_value, 2, dc[9].freeze_counter);
    BOOST_CHECK_EQUAL(pi.value, dc[9].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[9].freeze_bit);
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_extractDynamicPaoInfo)
{
    test_Mct410IconDevice dev;

    INMESS im;

    im.Buffer.DSt.Message[ 0] = 0xff;
    im.Buffer.DSt.Message[ 1] = 0xfe;
    im.Buffer.DSt.Message[ 2] = 0xfd;
    im.Buffer.DSt.Message[ 3] = 0xfc;
    im.Buffer.DSt.Message[ 4] = 0xfb;
    im.Buffer.DSt.Message[ 5] = 0xfa;
    im.Buffer.DSt.Message[ 5] = 0xf9;
    im.Buffer.DSt.Message[ 6] = 0xf8;
    im.Buffer.DSt.Message[ 7] = 0xf7;
    im.Buffer.DSt.Message[ 8] = 0xf6;
    im.Buffer.DSt.Message[ 9] = 0xf5;
    im.Buffer.DSt.Message[10] = 0xf4;
    im.Buffer.DSt.Message[11] = 0xf3;
    im.Buffer.DSt.Message[12] = 0xf2;

    /*
    readKeyStore.insert(read_key_info_t(-1, Memory_OptionsPos,               Memory_OptionsLen,              CtiTableDynamicPaoInfo::Key_MCT_Options));
    readKeyStore.insert(read_key_info_t(-1, Memory_ConfigurationPos,         Memory_ConfigurationLen,        CtiTableDynamicPaoInfo::Key_MCT_Configuration));

    readKeyStore.insert(read_key_info_t(-1, Memory_EventFlagsMask1Pos,       Memory_EventFlagsMask1Len,      CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1));
    readKeyStore.insert(read_key_info_t(-1, Memory_EventFlagsMask2Pos,       Memory_EventFlagsMask2Len,      CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2));
    readKeyStore.insert(read_key_info_t(-1, Memory_MeterAlarmMaskPos,        Memory_MeterAlarmMaskLen,       CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask));
    */

    im.Buffer.DSt.Length = 13;
    im.Return.ProtocolInfo.Emetcon.Function = 0;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2));
    //  unavailable - only the first byte available in the range 0x00 - 0x0c
    BOOST_CHECK(!dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options),         0xfd);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration),   0xfc);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1), 0xf4);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2), 0xf3);

    /*
    readKeyStore.insert(read_key_info_t(FuncRead_TOUDaySchedulePos,    0, 2, CtiTableDynamicPaoInfo::Key_MCT_DayTable));
    readKeyStore.insert(read_key_info_t(FuncRead_TOUDaySchedulePos,    2, 1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate));
    readKeyStore.insert(read_key_info_t(FuncRead_TOUDaySchedulePos,   10, 1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset));
    */

    im.Buffer.DSt.Length = 3;
    im.Return.ProtocolInfo.Emetcon.Function = 0xad; //  FuncRead_TOUDaySchedulePos;  it's protected, so instead of inheriting it, it's a magic number
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate));
    //  outside the collected range
    BOOST_CHECK(!dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable),       0xfffe);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate), 0xfd);
}


struct command_execution_environment
{
    test_Mct410IconDevice    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    command_execution_environment()
    {
        om = new OUTMESS;
    }

    ~command_execution_environment()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        delete om;
    }
};

BOOST_FIXTURE_TEST_SUITE(command_executions, command_execution_environment)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    /*
    *** TESTING: "putconfig emetcon centron...." commands and parameters
    */
    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 0s errors disable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x1 test 0s errors disable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(   40 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x1_1s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 4x1 test 1s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x1_1s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 60 display 4x1 test 1s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(   60 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x10_7s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 4x10 test 7s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  2 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x10_7s_enable)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 200 display 4x10 test 7s errors enable" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  3 , om->Buffer.BSt.Length );
        BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
        BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
        BOOST_CHECK_EQUAL(  200 , om->Buffer.BSt.Message[2] );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x3 test 0s errors disable" );
                                                               //  5x3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x3 test 0s errors disable" );
                                                                        //  5x3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 3s errors disable" );
                                                                         // 3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid test duration \"3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40 display 5x1 test 3s errors disable" );
                                                                                  // 3 is not a valid option
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid test duration \"3\"" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_out_of_bounds)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 400 display 5x1 test 0s errors disable" );
                                                              // 400 is not a valid option ( 0 <= ratio <= 255 )
        BOOST_CHECK_EQUAL(    0 , retList.size() );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL(  1 , retList.size() );

        CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

        BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
        BOOST_CHECK_EQUAL( "Test MCT-410iL / Invalid transformer ratio (400)" , errorMsg->ResultString() );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_errors)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 test 0s" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_test)
    {
        CtiCommandParser parse( "putconfig emetcon centron display 5x1 errors enable" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_display)
    {
        CtiCommandParser parse( "putconfig emetcon centron test 0 errors enable" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_all_with_ratio)
    {
        CtiCommandParser parse( "putconfig emetcon centron ratio 40" );

        BOOST_CHECK_EQUAL( NoMethod, mct410.executePutConfig(&request, parse, om, vgList, retList, outList) );
        BOOST_CHECK_EQUAL(        0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_address_unique)
    {
        CtiCommandParser parse( "getconfig address unique" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x10);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   3);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_thresholds)
    {
        CtiCommandParser parse( "getconfig thresholds" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   5);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_meter_ratio)
    {
        CtiCommandParser parse( "getconfig meter ratio" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x19);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getconfig_freeze)
    {
        CtiCommandParser parse( "getconfig freeze" );

        BOOST_CHECK_EQUAL( NoError , mct410.executeGetConfig(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_putstatus_reset)
    {
        CtiCommandParser parse( "putstatus reset alarms" );

        BOOST_CHECK_EQUAL( NoError , mct410.executePutStatus(&request, parse, om, vgList, retList, outList) );

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x08);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Message[0], 0x00);
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Message[1], 0x00);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_0kwh)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            //  The rest are initialized to 0
            im.Buffer.DSt.Message[2] = 0x01;
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_normal_deltas)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 243 );  //  if rounding were working right, this would be 242
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 255 );  //  if rounding were working right, this would be 254
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 257 );  //  if rounding were working right, this would be 256
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_bad_first_kwh)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 5 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 255 );  //  if rounding were working right, this would be 254
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 257 );  //  if rounding were working right, this would be 256
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_bad_first_delta)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 5 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 255 );  //  if rounding were working right, this would be 254
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 257 );  //  if rounding were working right, this would be 256
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_all_deltas_bad)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x01, 0x02, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 1 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_all_bad)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0xff, 0xff, 0xfa };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            BOOST_CHECK_EQUAL( points.size(), 0 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_getvalue_daily_reads_all_bad_but_last)
    {
        CtiTime timenow;

        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct410.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x12, 0x34, 0x56 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( NoError , mct410.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 1 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x123456 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
            }
        }
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


struct single_error_validator : command_execution_environment
{
    ~single_error_validator()
    {
        BOOST_CHECK(outList.empty());
        BOOST_CHECK_EQUAL(retList.size(), 1);
    }
};


// These commands are errors and should only return a single error. See YUK-5059
BOOST_FIXTURE_TEST_SUITE(test_single_error_executor, single_error_validator)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor01)
    {
        CtiCommandParser parse("getvalue lp channel 1 02/02/2010 01/01/2010");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor02)
    {
        CtiCommandParser parse("control disconnect");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor03)
    {
        CtiCommandParser parse("putconfig emetcon address uniq 2455535535553555");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor04)
    {
        CtiCommandParser parse("putconfig emetcon centron ratio 10 display 4x5 test 5 errors disable");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor05)
    {
        CtiCommandParser parse("putconfig emetcon centron ratio 290 display 5x1 test 7 errors disable");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor06)
    {
        CtiCommandParser parse("putconfig emetcon centron ratio 25 display 5x1 test 10 errors disable");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor07)
    {
        CtiCommandParser parse("putconfig emetcon disconnect load limit 100 100");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor08)
    {
        CtiCommandParser parse("putconfig emetcon disconnect cycle 1 1");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor09)
    {
        CtiCommandParser parse("putconfig emetcon outage threshold 200");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor10)
    {
        CtiCommandParser parse("putconfig emetcon freeze day 266");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor11)
    {
        CtiCommandParser parse("getvalue daily read detail channel 2 02/02/2000");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor12)
    {
        CtiCommandParser parse("getvalue daily read detail channel 8");
        mct410.beginExecuteRequest(&request, parse, vgList, retList, outList);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectStatus)
{
    struct test_case
    {
        const unsigned char dst_message_0;
        const unsigned char dst_message_1;
        const unsigned char dst_message_8;
        const string expected;
    }
    test_cases[] =
    {
        {0x3c, 0x02, 123, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect state uncertain (powerfail during disconnect)\n"
                          "Disconnect error - demand detected after disconnect command sent to collar\n"
                          "Disconnect load limit count: 123\n"},
        {0x3c, 0x02, 123, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect state uncertain (powerfail during disconnect)\n"
                          "Disconnect error - demand detected after disconnect command sent to collar\n"
                          "Disconnect load limit count: 123\n"},
        {0x1c, 0x00, 199, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect load limit count: 199\n"},
        {0x10, 0x00,  17, "Disconnect load limit count: 17\n"},
    };

    std::vector<std::string> expected, results;

    for each(test_case tc in test_cases)
    {
        expected.push_back(tc.expected);

        test_Mct410IconDevice mct410;

        DSTRUCT DSt;

        DSt.Message[0] = tc.dst_message_0;
        DSt.Message[1] = tc.dst_message_1;
        DSt.Message[8] = tc.dst_message_8;

        results.push_back(mct410.decodeDisconnectStatus(DSt));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectDemandLimitConfig)
{
    //  Possibilities:  Has config.  Has demand limit bit.  Has demand threshold.
    struct
    {
        const boost::optional<int> config;
        const double demand_threshold;
        const string expected;
    }
    tc[] =
    {
        {boost::none, 0.00, "Disconnect demand threshold disabled\n"},
        {boost::none, 0.18, "Disconnect demand threshold: 0.180 kW\n"},
        {0x00,        0.00, "Disconnect demand threshold disabled\n"},
        {0x00,        0.19, "Disconnect demand threshold disabled\n"},
        {0x08,        0.00, "Disconnect demand threshold disabled\n"},
        {0x08,        0.20, "Demand limit mode enabled\n"
                            "Disconnect demand threshold: 0.200 kW\n"}
    };

    BOOST_CHECK_EQUAL(tc[0].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[0].config, tc[0].demand_threshold));
    BOOST_CHECK_EQUAL(tc[1].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[1].config, tc[1].demand_threshold));
    BOOST_CHECK_EQUAL(tc[2].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[2].config, tc[2].demand_threshold));
    BOOST_CHECK_EQUAL(tc[3].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[3].config, tc[3].demand_threshold));
    BOOST_CHECK_EQUAL(tc[4].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[4].config, tc[4].demand_threshold));
    BOOST_CHECK_EQUAL(tc[5].expected, test_Mct410Device::decodeDisconnectDemandLimitConfig(tc[5].config, tc[5].demand_threshold));
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectCyclingConfig)
{
    struct
    {
        const boost::optional<int> config;
        const unsigned disconnect_minutes;
        const unsigned connect_minutes;
        const string expected;
    }
    tc[] =
    {
        {boost::none, 0, 0, "Disconnect cycling mode disabled\n"},
        {boost::none, 0, 1, "Disconnect cycling mode disabled\n"},
        {boost::none, 2, 0, "Disconnect cycling mode disabled\n"},
        {boost::none, 3, 4, "Cycling mode - disconnect minutes: 3\n"
                            "Cycling mode - connect minutes   : 4\n"},
        {0x00,        0, 0, "Disconnect cycling mode disabled\n"},
        {0x00,        0, 1, "Disconnect cycling mode disabled\n"},
        {0x00,        2, 0, "Disconnect cycling mode disabled\n"},
        {0x00,        3, 4, "Disconnect cycling mode disabled\n"},
        {0x08,        0, 0, "Disconnect cycling mode disabled\n"},
        {0x08,        0, 1, "Disconnect cycling mode disabled\n"},
        {0x08,        2, 0, "Disconnect cycling mode disabled\n"},
        {0x08,        3, 4, "Disconnect cycling mode disabled\n"},
        {0x10,        0, 0, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 0\n"
                            "Cycling mode - connect minutes   : 0\n"},
        {0x10,        0, 1, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 0\n"
                            "Cycling mode - connect minutes   : 1\n"},
        {0x10,        2, 0, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 2\n"
                            "Cycling mode - connect minutes   : 0\n"},
        {0x10,        3, 4, "Disconnect cycling mode enabled\n"
                            "Cycling mode - disconnect minutes: 3\n"
                            "Cycling mode - connect minutes   : 4\n"},
        {0x18,        0, 0, "Disconnect cycling mode disabled\n"},
        {0x18,        0, 1, "Disconnect cycling mode disabled\n"},
        {0x18,        2, 0, "Disconnect cycling mode disabled\n"},
        {0x18,        3, 4, "Disconnect cycling mode disabled\n"}
    };

    BOOST_CHECK_EQUAL(tc[ 0].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 0].config, tc[ 0].disconnect_minutes, tc[ 0].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 1].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 1].config, tc[ 1].disconnect_minutes, tc[ 1].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 2].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 2].config, tc[ 2].disconnect_minutes, tc[ 2].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 3].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 3].config, tc[ 3].disconnect_minutes, tc[ 3].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 4].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 4].config, tc[ 4].disconnect_minutes, tc[ 4].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 5].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 5].config, tc[ 5].disconnect_minutes, tc[ 5].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 6].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 6].config, tc[ 6].disconnect_minutes, tc[ 6].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 7].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 7].config, tc[ 7].disconnect_minutes, tc[ 7].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 8].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 8].config, tc[ 8].disconnect_minutes, tc[ 8].connect_minutes));
    BOOST_CHECK_EQUAL(tc[ 9].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[ 9].config, tc[ 9].disconnect_minutes, tc[ 9].connect_minutes));
    BOOST_CHECK_EQUAL(tc[10].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[10].config, tc[10].disconnect_minutes, tc[10].connect_minutes));
    BOOST_CHECK_EQUAL(tc[11].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[11].config, tc[11].disconnect_minutes, tc[11].connect_minutes));
    BOOST_CHECK_EQUAL(tc[12].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[12].config, tc[12].disconnect_minutes, tc[12].connect_minutes));
    BOOST_CHECK_EQUAL(tc[13].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[13].config, tc[13].disconnect_minutes, tc[13].connect_minutes));
    BOOST_CHECK_EQUAL(tc[14].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[14].config, tc[14].disconnect_minutes, tc[14].connect_minutes));
    BOOST_CHECK_EQUAL(tc[15].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[15].config, tc[15].disconnect_minutes, tc[15].connect_minutes));
    BOOST_CHECK_EQUAL(tc[16].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[16].config, tc[16].disconnect_minutes, tc[16].connect_minutes));
    BOOST_CHECK_EQUAL(tc[17].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[17].config, tc[17].disconnect_minutes, tc[17].connect_minutes));
    BOOST_CHECK_EQUAL(tc[18].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[18].config, tc[18].disconnect_minutes, tc[18].connect_minutes));
    BOOST_CHECK_EQUAL(tc[19].expected, test_Mct410Device::decodeDisconnectCyclingConfig(tc[19].config, tc[19].disconnect_minutes, tc[19].connect_minutes));
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectConfig)
{
    //  Test case permutations:
    //    SSPEC: < Cycling, < ConfigReadEnhanced, == ConfigReadEnhanced
    //    DSTRUCT: length < 13, length = 13
    //    Config byte: not retrieved, autoreconnect disabled, autoreconnect enabled
    //  Config byte cannot be missing when the SSPEC is ConfigReadEnhanced, since it returns the config byte

    struct test_case
    {
        const int sspec_revision;
        const int dst_length;
        const boost::optional<int> config_byte;
        const string expected;
    }
    test_cases[] =
    {
        //  sspec_revision < Mct410Device::SspecRev_Disconnect_Cycling
        {11, 12, boost::none, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold: 0.129 kW\n"},
        {11, 12, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"},
        {11, 12, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"},
        //  if the message length is >= 13, the config byte is in the message
        {11, 13, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"},
        {11, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"},

        //  sspec_revision < Mct410Device::SspecRev_Disconnect_ConfigReadEnhanced
        {19, 12, boost::none, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold: 0.129 kW\n"
                              "Cycling mode - disconnect minutes: 10\n"
                              "Cycling mode - connect minutes   : 11\n"},
        {19, 12, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {19, 12, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        //  if the message length is >= 13, the config byte is in the message
        {19, 13, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {19, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},

        //  sspec_revision == Mct410Device::SspecRev_Disconnect_ConfigReadEnhanced
        {20, 12, boost::none, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold: 0.129 kW\n"
                              "Cycling mode - disconnect minutes: 10\n"
                              "Cycling mode - connect minutes   : 11\n"},
        {20, 12, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {20, 12, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        //  if the message length is >= 13, the config byte is in the message
        {20, 13, 0x00,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {20, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
    };

    std::vector<std::string> expected, results;

    for each( const test_case &tc in test_cases )
    {
        expected.push_back(tc.expected);

        {
            DSTRUCT DSt;

            DSt.Message[2] = 2;     //  Disconnect address
            DSt.Message[3] = 3;     //     :
            DSt.Message[4] = 4;     //     :
            DSt.Message[5] = 0x35;  //  Disconnect demand threshold
            DSt.Message[6] = 0x08;  //     :
            DSt.Message[7] = 34;    //  Disconnect load limit connect delay

            DSt.Message[9]  = 10;
            DSt.Message[10] = 11;

            DSt.Message[11] = tc.config_byte ? *tc.config_byte : 0xff;
            DSt.Message[12] = 205;

            DSt.Length      = tc.dst_length;

            test_Mct410IconDevice mct410;

            mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, tc.sspec_revision);

            if( tc.config_byte )
            {
                mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, *tc.config_byte);
            }

            results.push_back(mct410.decodeDisconnectConfig(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_canDailyReadDateAlias)
{
    //  test case 1/31 (aliases to 5/31 from 6/01 to 7/30)
    //    we don't need to check dates prior to 6/01, since it's not possible to request 5/31 before then
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 1,  6, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 1,  7, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate(30,  7, 2001)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate(31,  7, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 1,  8, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  5, 2001), CtiDate( 2,  8, 2001)));

    //  test case 11/29 (aliases to 3/29 from 3/30 to 5/28)
    //    we don't need to check dates prior to 3/30, since it's not possible to request 3/29 before then
    //  non-leap-year case
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate(30,  3, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate( 1,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate( 1,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate(28,  5, 2001)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate(29,  5, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2001), CtiDate( 1,  6, 2001)));

    //  leap-year case
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate(30,  3, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate( 1,  4, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate( 1,  5, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate(28,  5, 2004)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate(29,  5, 2004)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(29,  3, 2004), CtiDate( 1,  6, 2004)));

    //  test case 11/30 (aliases to 3/30 from 3/31 to 5/29)
    //    we don't need to check dates prior to 3/31, since it's not possible to request 3/30 before then
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(31,  3, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 1,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 2,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(30,  4, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 1,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 2,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(27,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(28,  5, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(29,  5, 2001)));

    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(30,  5, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate(31,  5, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 1,  6, 2001)));
    BOOST_CHECK( ! test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(30,  3, 2001), CtiDate( 2,  6, 2001)));

    //  test case 8/31 (aliases to 12/31 from 1/1 to 3/31)
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate( 1,  1, 2002)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate( 1,  2, 2002)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate( 1,  3, 2002)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31, 12, 2001), CtiDate(31,  3, 2002)));

    //  test case 3/31 (aliases to 7/31 from 8/1 to 10/31)
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate( 1,  8, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate( 1,  9, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate( 1, 10, 2001)));
    BOOST_CHECK(test_Mct410Device::isDailyReadVulnerableToAliasing(CtiDate(31,  7, 2001), CtiDate(31, 10, 2001)));
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_getUsageReportDelay)
{
    {
        const test_Mct410IconDevice mct;

        //  Calculation is 10s + intervals/day * days * 1ms

        //  24 * 12 *  1 * 0.001 = 0.288s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,   1), 10);

        //  24 * 12 * 30 * 0.001 = 8.640s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  30), 18);

        //  24 * 12 * 60 * 0.001 = 17.280s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  60), 27);

        //  24 *  6 *  1 * 0.001 = 0.144s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,   1), 10);

        //  24 *  6 * 30 * 0.001 = 4.320s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  30), 14);

        //  24 *  6 * 60 * 0.001 = 8.640s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  60), 18);

        //  24 *  1 *  1 * 0.001 = 0.024s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600,  1), 10);

        //  24 *  1 * 30 * 0.001 = 0.720s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 30), 10);

        //  24 *  1 * 60 * 0.001 = 1.440s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 60), 11);
    }

    {
        const test_Mct410FocusDevice mct;

        //  Calculation is 10s + intervals/day * days * 2ms

        //  24 * 12 *  1 * 0.002 = 0.576s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,   1), 10);

        //  24 * 12 * 30 * 0.002 = 17.280s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  30), 27);

        //  24 * 12 * 60 * 0.002 = 34.560s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  60), 44);

        //  24 *  6 *  1 * 0.002 = 0.288s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,   1), 10);

        //  24 *  6 * 30 * 0.002 = 8.640s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  30), 18);

        //  24 *  6 * 60 * 0.002 = 17.280s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  60), 27);

        //  24 *  1 *  1 * 0.002 = 0.048s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600,  1), 10);

        //  24 *  1 * 30 * 0.002 = 1.440s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 30), 11);

        //  24 *  1 * 60 * 0.002 = 2.880s
        BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 60), 12);
    }
}


struct getOperation_helper
{
    test_Mct410IconDevice mct;
    BSTRUCT BSt;
};

BOOST_FIXTURE_TEST_SUITE(test_getOperation, getOperation_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_getOperation_01)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Command_Loop, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_02)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Model, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_03)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Install, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_04)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x54);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_05)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressInhibit, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x53);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_06)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_07)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Shed, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_08)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Restore, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_09)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x42);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_10)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x41);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_11)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARMC, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x62);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_12)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARML, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x60);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    */
    //  MCT-4xx commands
    BOOST_AUTO_TEST_CASE(test_getOperation_14)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_TOUPeak, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xb0);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_15)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_TOUReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5f);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_16)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_ResetPFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x89);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_17)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf0);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_18)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TOUEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x56);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_19)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TOUDisable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x55);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_20)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LoadProfileInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    //  MCT-410 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_21)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Accum, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_22)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_KWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_23)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenKWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x91);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_24)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_TOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_25)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenTOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe1);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_26)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_27)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_28)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_29)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfilePeakReport, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_30)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_31)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x93);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_32)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenPeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x94);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_33)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Voltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x95);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_34)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenVoltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x96);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_35)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_36)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Internal, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_37)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x97);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_38)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x26);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_39)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_41)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_42)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_43)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_44)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_45)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x44);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_46)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Time, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_47)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_FreezeDay, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_48)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_49)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1a);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_OutageThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_52)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Thresholds, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_DailyReadInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x23);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_Reset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x8a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_ResetAlarms, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x08);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeVoltageOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x59);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeVoltageTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf1);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x19);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_MeterParameters, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_65)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_66)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_68)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_VThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_69)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_70)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_71)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_72)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_AutoReconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_73)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_74)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x36);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_75)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_76)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_77)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PhaseDetectClear, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()
