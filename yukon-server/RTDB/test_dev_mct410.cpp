/*
 * test CtiDeviceMCT410
 *
 */

#include "dev_mct410.h"

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test dev_mct410"
#include <boost/test/unit_test.hpp>

#include <limits>

using boost::unit_test_framework::test_suite;

class test_CtiDeviceMCT410 : public CtiDeviceMCT410
{
public:

    typedef CtiDeviceMCT410::point_info point_info;

    point_info test_getDemandData(unsigned char *buf, int len, bool frozen)
    {
        return getDemandData(buf, len, frozen);
    }

    void test_extractDynamicPaoInfo(const INMESS &InMessage)
    {
        extractDynamicPaoInfo(InMessage);
    };
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

