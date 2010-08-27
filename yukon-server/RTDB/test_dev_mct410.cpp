/*
 * test Mct410Device
 *
 */

#include "dev_mct410.h"
#include "devicetypes.h"

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test dev_mct410"
#include <boost/test/unit_test.hpp>

#include <limits>

using boost::unit_test_framework::test_suite;

using Cti::Devices::Mct410Device;

struct test_Mct410Device : Mct410Device
{
    test_Mct410Device()
    {
        setType(TYPEMCT410);
        _name = "Test MCT-410";
    }

    typedef Mct410Device::point_info point_info;

    using Mct410Device::getDemandData;
    using Mct410Device::extractDynamicPaoInfo;
    using Mct410Device::executePutConfig;

    point_info getData_DymanicDemand(unsigned char *buf, int len)
    {
        return Mct410Device::getData(buf, len, Mct410Device::ValueType_DynamicDemand);
    }

    point_info getData_FrozenDynamicDemand(unsigned char *buf, int len)
    {
        return Mct410Device::getData(buf, len, Mct410Device::ValueType_FrozenDynamicDemand);
    }

    point_info getData_AccumulatorDelta(unsigned char *buf, int len)
    {
        return Mct410Device::getData(buf, len, Mct410Device::ValueType_AccumulatorDelta);
    }
};

BOOST_AUTO_TEST_CASE(test_dev_mct410_getdata_rounding_dynamic_demand)
{
    test_Mct410Device dev;
    test_Mct410Device::point_info pi;

    unsigned char kwh_read[3] = { 0x00, 0x05, 0x00 };

    pi = dev.getData_DymanicDemand(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      1280 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;
    
    pi = dev.getData_DymanicDemand(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      1280 ); // Still should be 1280, we round down!
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_getdata_rounding_frozen_dynamic_demand)
{
    test_Mct410Device dev;
    test_Mct410Device::point_info pi;

    unsigned char kwh_read[3] = { 0x00, 0x00, 0x80 };

    pi = dev.getData_FrozenDynamicDemand(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      128 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x81;
    
    pi = dev.getData_FrozenDynamicDemand(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      128 ); // Still should be 128, we round down!
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_getdata_rounding_accumulator_delta)
{
    test_Mct410Device dev;
    test_Mct410Device::point_info pi;

    unsigned char kwh_read[3] = { 0x00, 0x04, 0x00 };

    pi = dev.getData_AccumulatorDelta(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      1024 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;
    
    pi = dev.getData_AccumulatorDelta(kwh_read, 3);

    BOOST_CHECK_EQUAL( pi.value,      1024 ); // Still should be 1024, we round down!
    BOOST_CHECK_EQUAL( pi.freeze_bit, true );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}

BOOST_AUTO_TEST_CASE(test_dev_mct410_getDemandData)
{
    test_Mct410Device dev;

    struct demand_checks
    {
        unsigned char raw_value[2];
        bool frozen;
        double value;
        bool freeze_bit;
    };

    demand_checks dc[10] = {{{0x30, 0x05}, false, 0.005, true},
                            {{0x30, 0x05}, true,  0.004, true},
                            {{0x30, 0x04}, false, 0.004, false},
                            {{0x30, 0x04}, true,  0.004, false},
                            {{0x2f, 0x0f}, false, 38.55, true},
                            {{0x2f, 0x0f}, true,  38.54, true},
                            {{0x2f, 0x0e}, false, 38.54, false},
                            {{0x2f, 0x0e}, true,  38.54, false},
                            {{0x01, 0x11}, false, 273,   true},
                            {{0x01, 0x11}, true,  272,   true}};

    test_Mct410Device::point_info pi;

    pi = dev.getDemandData(dc[0].raw_value, 2, dc[0].frozen);
    BOOST_CHECK_EQUAL(pi.value, (dc[0].value - 0.001));
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[0].freeze_bit);

    pi = dev.getDemandData(dc[1].raw_value, 2, dc[1].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[1].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[1].freeze_bit);

    pi = dev.getDemandData(dc[2].raw_value, 2, dc[2].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[2].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[2].freeze_bit);

    pi = dev.getDemandData(dc[3].raw_value, 2, dc[3].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[3].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[3].freeze_bit);

    pi = dev.getDemandData(dc[4].raw_value, 2, dc[4].frozen);
    BOOST_CHECK_EQUAL(pi.value, (dc[4].value - 0.01));
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[4].freeze_bit);

    pi = dev.getDemandData(dc[5].raw_value, 2, dc[5].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[5].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[5].freeze_bit);

    pi = dev.getDemandData(dc[6].raw_value, 2, dc[6].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[6].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[6].freeze_bit);

    pi = dev.getDemandData(dc[7].raw_value, 2, dc[7].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[7].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[7].freeze_bit);

    pi = dev.getDemandData(dc[8].raw_value, 2, dc[8].frozen);
    BOOST_CHECK_EQUAL(pi.value, (dc[8].value - 1));
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[8].freeze_bit);

    pi = dev.getDemandData(dc[9].raw_value, 2, dc[9].frozen);
    BOOST_CHECK_EQUAL(pi.value, dc[9].value);
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[9].freeze_bit);
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_extractDynamicPaoInfo)
{
    test_Mct410Device dev;

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
    im.Return.ProtocolInfo.Emetcon.IO = Cti::Protocols::EmetconProtocol::IO_Read;

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
    im.Return.ProtocolInfo.Emetcon.IO = Cti::Protocols::EmetconProtocol::IO_Function_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate));
    //  outside the collected range
    BOOST_CHECK(!dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable),       0xfffe);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate), 0xfd);
}


/*
*** TESTING: "putconfig emetcon centron...." commands and parameters
*/
BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 test 0s errors disable" );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    2 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 40 display 5x1 test 0s errors disable" );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    3 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
    BOOST_CHECK_EQUAL(   40 , om->Buffer.BSt.Message[2] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x1_1s_enable)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 4x1 test 1s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    2 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x1_1s_enable)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 60 display 4x1 test 1s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    3 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
    BOOST_CHECK_EQUAL(   60 , om->Buffer.BSt.Message[2] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x10_7s_enable)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 4x10 test 7s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    2 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x10_7s_enable)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 200 display 4x10 test 7s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    3 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
    BOOST_CHECK_EQUAL(  200 , om->Buffer.BSt.Message[2] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_display)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                  //  5x3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron display 5x3 test 0s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Test MCT-410 / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_display)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                           //  5x3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron ratio 40 display 5x3 test 0s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Test MCT-410 / Invalid display configuration \"5x3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_test)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                            // 3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 test 3s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Test MCT-410 / Invalid test duration \"3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_test)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                                     // 3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron ratio 40 display 5x1 test 3s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Test MCT-410 / Invalid test duration \"3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_out_of_bounds)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                 // 400 is not a valid option ( 0 <= ratio <= 255 )
    CtiCommandParser        parse( "putconfig emetcon centron ratio 400 display 5x1 test 0s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Test MCT-410 / Invalid transformer ratio (400)" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_errors)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 test 0s" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_test)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 errors enable" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_display)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron test 0 errors enable" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_all_with_ratio)
{
    test_Mct410Device    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 40" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


// These commands are errors and should only return a single error. See YUK-5059
BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor)
{
    test_Mct410Device   mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
    CtiDeviceBase          *basePtr = &mct410;

    CtiCommandParser parse = CtiCommandParser("getvalue lp channel 1 02/02/2010 01/01/2010");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("control disconnect");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon address uniq 2455535535553555");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon centron ratio 10 display 4x5 test 5 errors disable");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon centron ratio 290 display 5x1 test 7 errors disable");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon centron ratio 25 display 5x1 test 10 errors disable");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon disconnect load limit 100 100");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon disconnect cycle 1 1");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon outage threshold 200");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon freeze day 266");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("getvalue daily read detail channel 2 02/02/2000");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("getvalue daily read detail channel 8");
    basePtr->ExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();
}
