/*
 * test CtiDeviceMCT410
 *
 */

#include <boost/test/unit_test.hpp>

#include "dev_mct410.h"

#define BOOST_AUTO_TEST_MAIN "Test MCT_410 Device"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

class test_CtiDeviceMCT410 : public CtiDeviceMCT410
{
public:
    void test_extractDynamicPaoInfo(const INMESS &InMessage)
    {
        extractDynamicPaoInfo(InMessage);
    };
};

BOOST_AUTO_UNIT_TEST(test_dev_mct410_extractDynamicPaoInfo)
{
    test_CtiDeviceMCT410 dev;
    using CtiTableDynamicPaoInfo::Keys;

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
    readKeyStore.insert(read_key_info_t(Memory_OptionsPos,               Memory_OptionsLen,              Keys::Key_MCT_Options));
    readKeyStore.insert(read_key_info_t(Memory_ConfigurationPos,         Memory_ConfigurationLen,        Keys::Key_MCT_Configuration));

    readKeyStore.insert(read_key_info_t(Memory_EventFlagsMask1Pos,       Memory_EventFlagsMask1Len,      Keys::Key_MCT_EventFlagsMask1));
    readKeyStore.insert(read_key_info_t(Memory_EventFlagsMask2Pos,       Memory_EventFlagsMask2Len,      Keys::Key_MCT_EventFlagsMask2));
    readKeyStore.insert(read_key_info_t(Memory_MeterAlarmMaskPos,        Memory_MeterAlarmMaskLen,       Keys::Key_MCT_MeterAlarmMask));
    */

    im.InLength = 13;
    im.Return.ProtocolInfo.Emetcon.Function = 0;
    im.Return.ProtocolInfo.Emetcon.IO = Cti::Protocol::Emetcon::IO_Read;

    dev.test_extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(Keys::Key_MCT_Options));
    BOOST_CHECK(dev.hasDynamicInfo(Keys::Key_MCT_Configuration));
    BOOST_CHECK(dev.hasDynamicInfo(Keys::Key_MCT_EventFlagsMask1));
    BOOST_CHECK(dev.hasDynamicInfo(Keys::Key_MCT_EventFlagsMask2));
    //  unavailable - only the first byte available in the range 0x00 - 0x0c
    BOOST_CHECK(!dev.hasDynamicInfo(Keys::Key_MCT_MeterAlarmMask));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(Keys::Key_MCT_Options),         0xfd);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(Keys::Key_MCT_Configuration),   0xfc);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(Keys::Key_MCT_EventFlagsMask1), 0xf4);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(Keys::Key_MCT_EventFlagsMask2), 0xf3);

    /*
    readKeyStore.insert(read_key_info_t(FuncRead_TOUDaySchedulePos,    0, 2, Keys::Key_MCT_DayTable));
    readKeyStore.insert(read_key_info_t(FuncRead_TOUDaySchedulePos,    2, 1, Keys::Key_MCT_DefaultTOURate));
    readKeyStore.insert(read_key_info_t(FuncRead_TOUDaySchedulePos,   10, 1, Keys::Key_MCT_TimeZoneOffset));
    */

    im.InLength = 3;
    im.Return.ProtocolInfo.Emetcon.Function = 0xad; //  FuncRead_TOUDaySchedulePos;  it's protected, so instead of inheriting it, it's a magic number
    im.Return.ProtocolInfo.Emetcon.IO = Cti::Protocol::Emetcon::IO_Function_Read;

    dev.test_extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(Keys::Key_MCT_DayTable));
    BOOST_CHECK(dev.hasDynamicInfo(Keys::Key_MCT_DefaultTOURate));
    //  outside the collected range
    BOOST_CHECK(!dev.hasDynamicInfo(Keys::Key_MCT_TimeZoneOffset));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(Keys::Key_MCT_DayTable),       0xfffe);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(Keys::Key_MCT_DefaultTOURate), 0xfd);
}

