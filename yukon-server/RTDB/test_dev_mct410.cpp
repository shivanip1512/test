/*
 * test CtiDeviceMCT410
 *
 */

#include "dev_mct410.h"
#include "devicetypes.h"

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test dev_mct410"
#include <boost/test/unit_test.hpp>

#include <limits>

using boost::unit_test_framework::test_suite;

class test_CtiDeviceMCT410 : public CtiDeviceMCT410
{
public:

    test_CtiDeviceMCT410()
    {
        setType(TYPEMCT410);
    }

    typedef CtiDeviceMCT410::point_info point_info;

    point_info test_getDemandData(unsigned char *buf, int len, bool is_frozen_data)
    {
        return getDemandData(buf, len, is_frozen_data);
    }

    void test_extractDynamicPaoInfo(const INMESS &InMessage)
    {
        extractDynamicPaoInfo(InMessage);
    };

    INT test_executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage*> &vgList, list<CtiMessage*> &retList, list<OUTMESS*> &outList, bool readsOnly)
    {
        return executePutConfig(pReq, parse, OutMessage, vgList, retList, outList, readsOnly);
    }
};

BOOST_AUTO_TEST_CASE(test_dev_mct410_getDemandData)
{
    test_CtiDeviceMCT410 dev;

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

    test_CtiDeviceMCT410::point_info pi;

    pi = dev.test_getDemandData(dc[0].raw_value, 2, dc[0].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[0].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[0].freeze_bit);

    pi = dev.test_getDemandData(dc[1].raw_value, 2, dc[1].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[1].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[1].freeze_bit);

    pi = dev.test_getDemandData(dc[2].raw_value, 2, dc[2].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[2].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[2].freeze_bit);

    pi = dev.test_getDemandData(dc[3].raw_value, 2, dc[3].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[3].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[3].freeze_bit);

    pi = dev.test_getDemandData(dc[4].raw_value, 2, dc[4].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[4].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[4].freeze_bit);

    pi = dev.test_getDemandData(dc[5].raw_value, 2, dc[5].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[5].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[5].freeze_bit);

    pi = dev.test_getDemandData(dc[6].raw_value, 2, dc[6].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[6].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[6].freeze_bit);

    pi = dev.test_getDemandData(dc[7].raw_value, 2, dc[7].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[7].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[7].freeze_bit);

    pi = dev.test_getDemandData(dc[8].raw_value, 2, dc[8].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[8].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[8].freeze_bit);

    pi = dev.test_getDemandData(dc[9].raw_value, 2, dc[9].frozen);
    BOOST_CHECK_SMALL(pi.value - dc[9].value, std::numeric_limits<double>::epsilon());
    BOOST_CHECK_EQUAL(pi.freeze_bit, dc[9].freeze_bit);
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_extractDynamicPaoInfo)
{
    test_CtiDeviceMCT410 dev;

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
    im.Return.ProtocolInfo.Emetcon.IO = Cti::Protocol::Emetcon::IO_Read;

    dev.test_extractDynamicPaoInfo(im);

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
    im.Return.ProtocolInfo.Emetcon.IO = Cti::Protocol::Emetcon::IO_Function_Read;

    dev.test_extractDynamicPaoInfo(im);

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
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 test 0s errors disable" );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    2 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 40 display 5x1 test 0s errors disable" );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    3 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x00 , om->Buffer.BSt.Message[1] );
    BOOST_CHECK_EQUAL(   40 , om->Buffer.BSt.Message[2] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x1_1s_enable)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 4x1 test 1s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    2 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x1_1s_enable)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 60 display 4x1 test 1s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    3 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x15 , om->Buffer.BSt.Message[1] );
    BOOST_CHECK_EQUAL(   60 , om->Buffer.BSt.Message[2] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_4x10_7s_enable)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 4x10 test 7s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    2 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_4x10_7s_enable)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 200 display 4x10 test 7s errors enable" );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    3 , om->Buffer.BSt.Length );
    BOOST_CHECK_EQUAL( 0xFF , om->Buffer.BSt.Message[0] );
    BOOST_CHECK_EQUAL( 0x1E , om->Buffer.BSt.Message[1] );
    BOOST_CHECK_EQUAL(  200 , om->Buffer.BSt.Message[2] );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_display)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                  //  5x3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron display 5x3 test 0s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Invalid display configuration \"5x3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_display)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                           //  5x3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron ratio 40 display 5x3 test 0s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Invalid display configuration \"5x3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_without_ratio_invalid_test)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                            // 3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 test 3s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Invalid test duration \"3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_invalid_test)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                                     // 3 is not a valid option
    CtiCommandParser        parse( "putconfig emetcon centron ratio 40 display 5x1 test 3s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Invalid test duration \"3\"" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_good_with_ratio_out_of_bounds)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;
                                                                 // 400 is not a valid option ( 0 <= ratio <= 255 )
    CtiCommandParser        parse( "putconfig emetcon centron ratio 400 display 5x1 test 0s errors disable" );

    BOOST_CHECK_EQUAL(    0 , retList.size() );

    BOOST_CHECK_EQUAL( NoError , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    1 , retList.size() );

    CtiReturnMsg *errorMsg = static_cast<CtiReturnMsg*>(retList.front());

    BOOST_CHECK_EQUAL( BADPARAM , errorMsg->Status() );
    BOOST_CHECK_EQUAL( "Invalid transformer ratio (400)" , errorMsg->ResultString() );

    delete errorMsg;        // clean up the return message
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_errors)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 test 0s" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_test)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron display 5x1 errors enable" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_display)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron test 0 errors enable" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


BOOST_AUTO_TEST_CASE(test_dev_mct410_centron_parse_bad_missing_all_with_ratio)
{
    test_CtiDeviceMCT410    mct410;

    CtiRequestMsg           request;
    OUTMESS                *om = new OUTMESS;
    list<CtiMessage*>       vgList, retList;
    list<OUTMESS*>          outList;

    CtiCommandParser        parse( "putconfig emetcon centron ratio 40" );

    BOOST_CHECK_EQUAL( NoMethod , mct410.test_executePutConfig(&request, parse, om, vgList , retList, outList, false) );

    BOOST_CHECK_EQUAL(    0 , om->Buffer.BSt.Length );
}


// These commands are errors and should only return a single error. See YUK-5059
BOOST_AUTO_TEST_CASE(test_dev_mct410_single_error_executor)
{
    test_CtiDeviceMCT410   mct410;

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
