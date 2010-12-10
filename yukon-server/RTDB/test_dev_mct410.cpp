#include "dev_mct410.h"
#include "devicetypes.h"

#include "boost_test_helpers.h"

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

    using Mct410Device::decodeDisconnectStatus;
    using Mct410Device::decodeDisconnectConfig;
    using Mct410Device::decodeDisconnectDemandLimitConfig;
    using Mct410Device::decodeDisconnectCyclingConfig;

    using Mct410Device::isDailyReadVulnerableToAliasing;

    virtual LONG getDemandInterval()
    {
        return 3600;
    }
};

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
    BOOST_CHECK_EQUAL(pi.value, dc[0].value);
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
    BOOST_CHECK_EQUAL(pi.value, dc[4].value);
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
    BOOST_CHECK_EQUAL(pi.value, dc[8].value);
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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoError , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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

    BOOST_CHECK_EQUAL( NoMethod , mct410.executePutConfig(&request, parse, om, vgList , retList, outList) );

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
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("control disconnect");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon address uniq 2455535535553555");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon centron ratio 10 display 4x5 test 5 errors disable");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon centron ratio 290 display 5x1 test 7 errors disable");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon centron ratio 25 display 5x1 test 10 errors disable");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon disconnect load limit 100 100");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon disconnect cycle 1 1");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon outage threshold 200");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("putconfig emetcon freeze day 266");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("getvalue daily read detail channel 2 02/02/2000");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();

    parse = CtiCommandParser("getvalue daily read detail channel 8");
    basePtr->beginExecuteRequest(&request, parse,  vgList , retList, outList);
    BOOST_CHECK(outList.empty());
    BOOST_CHECK_EQUAL(retList.size(), 1);
    delete_container(retList);
    retList.clear();
    outList.clear();
}

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

    const unsigned count = sizeof(test_cases) / sizeof(*test_cases);

    struct my_test
    {
        typedef test_case test_case_type;
        typedef string    result_type;

        result_type operator()(const test_case_type &tc)
        {
            test_Mct410Device mct410;

            DSTRUCT DSt;

            DSt.Message[0] = tc.dst_message_0;
            DSt.Message[1] = tc.dst_message_1;
            DSt.Message[8] = tc.dst_message_8;

            return mct410.decodeDisconnectStatus(DSt);
        }
    };

    Cti::TestRunner<my_test> tr(test_cases, test_cases + count);

    BOOST_CHECK_EQUAL_COLLECTIONS(tr.expected_begin(), tr.expected_end(),
                                  tr.results_begin(),  tr.results_end());
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
                              "Disconnect verification threshhold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {20, 13, 0x04,        "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshhold: 12.300 kW (205 Wh/minute)\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
    };

    const unsigned count = sizeof(test_cases) / sizeof(*test_cases);

    struct my_test
    {
        typedef test_case test_case_type;
        typedef string    result_type;

        result_type operator()(const test_case_type &tc)
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

            test_Mct410Device mct410;

            mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, tc.sspec_revision);

            if( tc.config_byte )
            {
                mct410.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, *tc.config_byte);
            }

            return mct410.decodeDisconnectConfig(DSt);
        }
    };

    Cti::TestRunner<my_test> tr(test_cases, test_cases + count);

    BOOST_CHECK_EQUAL_COLLECTIONS(tr.expected_begin(), tr.expected_end(),
                                  tr.results_begin(),  tr.results_end());
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

