#include "dev_mct470.h"
#include "devicetypes.h"
#include "config_device.h"
#include "boostutil.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

using Cti::Devices::Mct470Device;
using Cti::Protocols::EmetconProtocol;

struct test_Mct470Device : Mct470Device
{
    typedef Mct470Device::point_info point_info;

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct470Device::extractDynamicPaoInfo;
    using Mct470Device::convertTimestamp;
    using Mct470Device::computeResolutionByte;
    using Mct470Device::ResultDecode;
};


//  hack to get BOOST_CHECK_EQUAL_COLLECTIONS to print unsigned char as integer
namespace std {

ostream &operator<<( ostream &os, const unsigned char &uc ) {
    return os << static_cast<unsigned>(uc);
}

}


struct utc34_checker_base_date
{
    unsigned short year;
    unsigned short month;
};

struct utc34_checker_expected_time
{
    unsigned short year;
    unsigned short month;
    unsigned short day;
    unsigned short hour;
    unsigned short minute;
};

struct utc34_checker
{
    unsigned long raw_value;

    utc34_checker_base_date base_date;
    utc34_checker_expected_time expected_time;
};

CtiDate build_base_date(const utc34_checker_base_date &d)
{
    return CtiDate(1, d.month, d.year);
}

unsigned long build_gmt_seconds(const utc34_checker_expected_time &e)
{
    //  This builds us a time in local time (interprets parameters as GMT + local)...
    const CtiTime local_time(CtiDate(e.day, e.month, e.year), e.hour, e.minute);

    //  ...  however, we really wanted the parameters interpreted as if they were GMT.
    //  To adjust the time back, we need the offset between the local_time and the same
    //  time expressed in GMT.
    const int local_offset = local_time.secondOffsetToGMT();

    //  GMT = local_time - local_offset
    return local_time.seconds() - local_offset;
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2009)
{
    test_Mct470Device dev;

    utc34_checker test_cases[] = {
        {0x000000, {2009,  1}, {2009,  1,  1,  0,  0}},
        {0x000001, {2009,  1}, {2008, 12, 31, 23, 59}},
        {0x00003c, {2009,  1}, {2008, 12, 31, 23,  0}},
        {0x0005a0, {2009,  1}, {2008, 12, 31,  0,  0}},
        {0x00ae60, {2009,  1}, {2008, 12,  1,  0,  0}},
        {0x080520, {2009,  1}, {2008,  1,  2,  0,  0}},  //  leap year

        {0x800000, {2009,  1}, {2010,  1,  1,  0,  0}},
        {0x800001, {2009,  1}, {2009, 12, 31, 23, 59}},
        {0x80003c, {2009,  1}, {2009, 12, 31, 23,  0}},
        {0x8005a0, {2009,  1}, {2009, 12, 31,  0,  0}},
        {0x80ae60, {2009,  1}, {2009, 12,  1,  0,  0}},
        {0x880520, {2009,  1}, {2009,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2009, 12}, {2011,  1,  1,  0,  0}},
        {0x000001, {2009, 12}, {2010, 12, 31, 23, 59}},
        {0x00003c, {2009, 12}, {2010, 12, 31, 23,  0}},
        {0x0005a0, {2009, 12}, {2010, 12, 31,  0,  0}},
        {0x00ae60, {2009, 12}, {2010, 12,  1,  0,  0}},
        {0x080520, {2009, 12}, {2010,  1,  1,  0,  0}},  //  not a leap year

        {0x800000, {2009, 12}, {2010,  1,  1,  0,  0}},
        {0x800001, {2009, 12}, {2009, 12, 31, 23, 59}},
        {0x80003c, {2009, 12}, {2009, 12, 31, 23,  0}},
        {0x8005a0, {2009, 12}, {2009, 12, 31,  0,  0}},
        {0x80ae60, {2009, 12}, {2009, 12,  1,  0,  0}},
        {0x880520, {2009, 12}, {2009,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2009,  1}, {2009,  1,  1,  0,  0}}};

    std::vector<unsigned long> expected, results;

    for each( utc34_checker tc in test_cases )
    {
        expected.push_back(build_gmt_seconds(tc.expected_time));

        results.push_back(dev.convertTimestamp(tc.raw_value, build_base_date(tc.base_date)));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2010)
{
    test_Mct470Device dev;

    utc34_checker test_cases[] = {
        {0x000000, {2010,  1}, {2011,  1,  1,  0,  0}},
        {0x000001, {2010,  1}, {2010, 12, 31, 23, 59}},
        {0x00003c, {2010,  1}, {2010, 12, 31, 23,  0}},
        {0x0005a0, {2010,  1}, {2010, 12, 31,  0,  0}},
        {0x00ae60, {2010,  1}, {2010, 12,  1,  0,  0}},
        {0x080520, {2010,  1}, {2010,  1,  1,  0,  0}},  //  not a leap year

        {0x800000, {2010,  1}, {2010,  1,  1,  0,  0}},
        {0x800001, {2010,  1}, {2009, 12, 31, 23, 59}},
        {0x80003c, {2010,  1}, {2009, 12, 31, 23,  0}},
        {0x8005a0, {2010,  1}, {2009, 12, 31,  0,  0}},
        {0x80ae60, {2010,  1}, {2009, 12,  1,  0,  0}},
        {0x880520, {2010,  1}, {2009,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2010, 12}, {2011,  1,  1,  0,  0}},
        {0x000001, {2010, 12}, {2010, 12, 31, 23, 59}},
        {0x00003c, {2010, 12}, {2010, 12, 31, 23,  0}},
        {0x0005a0, {2010, 12}, {2010, 12, 31,  0,  0}},
        {0x00ae60, {2010, 12}, {2010, 12,  1,  0,  0}},
        {0x080520, {2010, 12}, {2010,  1,  1,  0,  0}},  //  not a leap year

        {0x800000, {2010, 12}, {2012,  1,  1,  0,  0}},
        {0x800001, {2010, 12}, {2011, 12, 31, 23, 59}},
        {0x80003c, {2010, 12}, {2011, 12, 31, 23,  0}},
        {0x8005a0, {2010, 12}, {2011, 12, 31,  0,  0}},
        {0x80ae60, {2010, 12}, {2011, 12,  1,  0,  0}},
        {0x880520, {2010, 12}, {2011,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2010,  1}, {2011,  1,  1,  0,  0}}};

    std::vector<unsigned long> expected, results;

    for each( utc34_checker tc in test_cases )
    {
        expected.push_back(build_gmt_seconds(tc.expected_time));

        results.push_back(dev.convertTimestamp(tc.raw_value, build_base_date(tc.base_date)));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2011)
{
    test_Mct470Device dev;

    utc34_checker test_cases[] = {
        {0x000000, {2011,  1}, {2011,  1,  1,  0,  0}},
        {0x000001, {2011,  1}, {2010, 12, 31, 23, 59}},
        {0x00003c, {2011,  1}, {2010, 12, 31, 23,  0}},
        {0x0005a0, {2011,  1}, {2010, 12, 31,  0,  0}},
        {0x00ae60, {2011,  1}, {2010, 12,  1,  0,  0}},
        {0x080520, {2011,  1}, {2010,  1,  1,  0,  0}},  //  not a leap year

        {0x800000, {2011,  1}, {2012,  1,  1,  0,  0}},
        {0x800001, {2011,  1}, {2011, 12, 31, 23, 59}},
        {0x80003c, {2011,  1}, {2011, 12, 31, 23,  0}},
        {0x8005a0, {2011,  1}, {2011, 12, 31,  0,  0}},
        {0x80ae60, {2011,  1}, {2011, 12,  1,  0,  0}},
        {0x880520, {2011,  1}, {2011,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2011, 12}, {2013,  1,  1,  0,  0}},
        {0x000001, {2011, 12}, {2012, 12, 31, 23, 59}},
        {0x00003c, {2011, 12}, {2012, 12, 31, 23,  0}},
        {0x0005a0, {2011, 12}, {2012, 12, 31,  0,  0}},
        {0x00ae60, {2011, 12}, {2012, 12,  1,  0,  0}},
        {0x080520, {2011, 12}, {2012,  1,  2,  0,  0}},  //  leap year

        {0x800000, {2011, 12}, {2012,  1,  1,  0,  0}},
        {0x800001, {2011, 12}, {2011, 12, 31, 23, 59}},
        {0x80003c, {2011, 12}, {2011, 12, 31, 23,  0}},
        {0x8005a0, {2011, 12}, {2011, 12, 31,  0,  0}},
        {0x80ae60, {2011, 12}, {2011, 12,  1,  0,  0}},
        {0x880520, {2011, 12}, {2011,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2011,  1}, {2011,  1,  1,  0,  0}}};

    std::vector<unsigned long> expected, results;

    for each( utc34_checker tc in test_cases )
    {
        expected.push_back(build_gmt_seconds(tc.expected_time));

        results.push_back(dev.convertTimestamp(tc.raw_value, build_base_date(tc.base_date)));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2012)
{
    test_Mct470Device dev;

    utc34_checker test_cases[] = {
        {0x000000, {2012,  1}, {2013,  1,  1,  0,  0}},
        {0x000001, {2012,  1}, {2012, 12, 31, 23, 59}},
        {0x00003c, {2012,  1}, {2012, 12, 31, 23,  0}},
        {0x0005a0, {2012,  1}, {2012, 12, 31,  0,  0}},
        {0x00ae60, {2012,  1}, {2012, 12,  1,  0,  0}},
        {0x080520, {2012,  1}, {2012,  1,  2,  0,  0}},  //  leap year

        {0x800000, {2012,  1}, {2012,  1,  1,  0,  0}},
        {0x800001, {2012,  1}, {2011, 12, 31, 23, 59}},
        {0x80003c, {2012,  1}, {2011, 12, 31, 23,  0}},
        {0x8005a0, {2012,  1}, {2011, 12, 31,  0,  0}},
        {0x80ae60, {2012,  1}, {2011, 12,  1,  0,  0}},
        {0x880520, {2012,  1}, {2011,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2012, 12}, {2013,  1,  1,  0,  0}},
        {0x000001, {2012, 12}, {2012, 12, 31, 23, 59}},
        {0x00003c, {2012, 12}, {2012, 12, 31, 23,  0}},
        {0x0005a0, {2012, 12}, {2012, 12, 31,  0,  0}},
        {0x00ae60, {2012, 12}, {2012, 12,  1,  0,  0}},
        {0x080520, {2012, 12}, {2012,  1,  2,  0,  0}},  //  leap year

        {0x800000, {2012, 12}, {2014,  1,  1,  0,  0}},
        {0x800001, {2012, 12}, {2013, 12, 31, 23, 59}},
        {0x80003c, {2012, 12}, {2013, 12, 31, 23,  0}},
        {0x8005a0, {2012, 12}, {2013, 12, 31,  0,  0}},
        {0x80ae60, {2012, 12}, {2013, 12,  1,  0,  0}},
        {0x880520, {2012, 12}, {2013,  1,  1,  0,  0}},  //  not a leap year

        {0x000000, {2012,  1}, {2013,  1,  1,  0,  0}}};

    std::vector<unsigned long> expected, results;

    for each( utc34_checker tc in test_cases )
    {
        expected.push_back(build_gmt_seconds(tc.expected_time));

        results.push_back(dev.convertTimestamp(tc.raw_value, build_base_date(tc.base_date)));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_extractDynamicPaoInfo)
{
    test_Mct470Device dev;

    INMESS im;

    im.Buffer.DSt.Message[ 0] = 0xff;
    im.Buffer.DSt.Message[ 1] = 0xfe;
    im.Buffer.DSt.Message[ 2] = 0xfd;
    im.Buffer.DSt.Message[ 3] = 0xfc;
    im.Buffer.DSt.Message[ 4] = 0xfb;
    im.Buffer.DSt.Message[ 5] = 0xfa;
    im.Buffer.DSt.Message[ 6] = 0xf9;
    im.Buffer.DSt.Message[ 7] = 0xf8;
    im.Buffer.DSt.Message[ 8] = 0xf7;
    im.Buffer.DSt.Message[ 9] = 0xf6;
    im.Buffer.DSt.Message[10] = 0xf5;
    im.Buffer.DSt.Message[11] = 0xf4;
    im.Buffer.DSt.Message[12] = 0xf3;

    im.Buffer.DSt.Length = 13;
    im.Return.ProtocolInfo.Emetcon.Function = 0x00;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2));
    //  unavailable - only the first byte available in the range 0x00 - 0x0c
    BOOST_CHECK(!dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec),           0xfbff);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision),   0xfe);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options),         0xfd);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration),   0xfc);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1), 0xf7);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2), 0xf6);

    im.Buffer.DSt.Length = 2;
    im.Return.ProtocolInfo.Emetcon.Function = 0x0c;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze));
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze), 0xfe);

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

    im.Buffer.DSt.Message[ 0] = 0x03;  //  3 wire KYZ, channel 1, LP interval 1
    im.Buffer.DSt.Message[ 1] = 0x4a;  //  2 wire KYZ, channel 3, LP interval 2
    im.Buffer.DSt.Message[ 2] = 0x19;  //  IED, channel 7, LP interval 1
    im.Buffer.DSt.Message[ 3] = 0x3c;  //  Unused, channel 15, LP interval 1
    im.Buffer.DSt.Message[ 4] = 0x05;  //  LP interval 1 = 5 mins
    im.Buffer.DSt.Message[ 5] = 0x0f;  //  LP interval 2 = 15 mins
    im.Buffer.DSt.Message[ 6] = 0x1e;  //  IED LP interval = 30 mins

    im.Buffer.DSt.Length = 7;
    im.Return.ProtocolInfo.Emetcon.Function = 0x20;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval));

    string lpconfig;
    dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
    BOOST_CHECK_EQUAL(lpconfig, "300221160000");
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x03);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x4a);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x19);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x3c);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval),       0x05);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2),      0x0f);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval),    1800);
}


struct beginExecuteRequest_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;
    test_Mct470Device mct;

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

struct test_DeviceConfig : public Cti::Config::DeviceConfig
{
    test_DeviceConfig() :
        DeviceConfig(-1, string(), string())
    {
    }

    using DeviceConfig::insertValue;
};


BOOST_FIXTURE_TEST_SUITE(command_executions, beginExecuteRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_alpha)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset alpha");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 3, 0, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_alpha_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x30");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 3, 0, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_430a)
    {
        mct._type = TYPEMCT430A;

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 3, 0, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_s4)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset s4");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 1, 0, 43 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_s4_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x10");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 1, 0, 43 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_430s4)
    {
        mct._type = TYPEMCT430S4;

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 1, 0, 43 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_a3)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset a3");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 2, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_a3_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x20");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 2, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_430a3)
    {
        mct._type = TYPEMCT430A3;

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 2, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2c)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset kv2c");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 8, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2c_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x80");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 8, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset kv2");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 5, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x50");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 5, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset kv");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 4, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x40");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 4, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_sentinel)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset sentinel");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 6, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_sentinel_deviceconfig)
    {
        mct._type = TYPEMCT470;

        test_DeviceConfig config;

        config.insertValue("configuration", "0x60");

        mct.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&config, null_deleter()));  //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 6, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_430sl)
    {
        mct._type = TYPEMCT430SL;

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 6, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kvetch)
    {
        mct._type = TYPEMCT470;

        //  "kvetch" was chosen as a word that contains "kv"
        CtiCommandParser parse("putvalue ied reset kvetch");

        BOOST_CHECK_EQUAL( MISCONFIG, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE( outList.empty() );

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL( retMsg->Status(),       MISCONFIG );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), "NoMethod or invalid command." );
        BOOST_CHECK_EQUAL( retMsg->ExpectMore(),   false );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_no_deviceconfig)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( MISCONFIG, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE( outList.empty() );

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL( retMsg->Status(),       MISCONFIG );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), "NoMethod or invalid command." );
        BOOST_CHECK_EQUAL( retMsg->ExpectMore(),   false );
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2_noqueue)
    {
        mct._type = TYPEMCT470;

        //  make sure it can pick out "kv2" even if there's another parameter after the IED type
        CtiCommandParser parse("putvalue ied reset kv2 noqueue");

        BOOST_CHECK_EQUAL( NoError, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 5, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct470_decodeGetValueIED)
{
    using Cti::Protocols::EmetconProtocol;

    test_Mct470Device dev;

    INMESS im;

    std::list<OUTMESS *> om_list;
    std::list<CtiMessage *> ret_list;
    std::list<CtiMessage *> vg_list;

    //  set up the ied demand inmessage
    {
        strcpy(im.Return.CommandStr, "getvalue ied demand");
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;
        im.Sequence = EmetconProtocol::GetValue_IEDDemand;

        im.Buffer.DSt.Message[0] = 0x00;
        im.Buffer.DSt.Message[1] = 0x00;
        im.Buffer.DSt.Message[2] = 0x00;
        im.Buffer.DSt.Message[3] = 0x00;
        im.Buffer.DSt.Message[4] = 0x00;
        im.Buffer.DSt.Message[5] = 0x00;
        im.Buffer.DSt.Message[6] = 0x00;
        im.Buffer.DSt.Message[7] = 0x00;
        im.Buffer.DSt.Message[8] = 0x00;
        im.Buffer.DSt.Message[9] = 0x00;
        im.Buffer.DSt.Message[10] = 0x00;
        im.Buffer.DSt.Message[11] = 0x00;

        im.Buffer.DSt.Length = 12;
    }

    //  try the decode with no points and no IED config
    {
        dev.ResultDecode(&im, CtiTime(), vg_list, ret_list, om_list);

        BOOST_CHECK_EQUAL(1, ret_list.size());

        BOOST_CHECK(ret_list.front());

        if( ret_list.front() )
        {
            CtiReturnMsg &rm = *(static_cast<CtiReturnMsg *>(ret_list.front()));

            BOOST_CHECK_EQUAL(0, rm.PointData().size());
        }
    }

    //  set up the meter with dynamic pao info for the Alpha Power Plus
    {
        INMESS meter_config;

        meter_config.Buffer.DSt.Message[0] = 0x30;
        meter_config.Buffer.DSt.Length = 1;
        meter_config.Return.ProtocolInfo.Emetcon.Function = 0;
        meter_config.Return.ProtocolInfo.Emetcon.IO = Cti::Protocols::EmetconProtocol::IO_Read;

        dev.extractDynamicPaoInfo(meter_config);
    }

    //  to finish out this unit test, we will need to override getDevicePointOffsetTypeEqual()
    //    to report back voltage points for the decode

}

/**
 * Testing a few cases of the computeResolutionByte function.
 */
BOOST_AUTO_TEST_CASE(test_dev_mct470_computeResolutionByte)
{
    BOOST_CHECK_EQUAL(0x1b, test_Mct470Device::computeResolutionByte(0.1,  1.0, 1.0));

    BOOST_CHECK_EQUAL(0x12, test_Mct470Device::computeResolutionByte(1.0, 10.0, 1.0));

    BOOST_CHECK_EQUAL(0x22, test_Mct470Device::computeResolutionByte(1.0, 10.0, 0.1));
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_getUsageReportDelay)
{
    const test_Mct470Device mct;

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


struct getOperation_helper
{
    test_Mct470Device mct;
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
    //  Overridden by MCT-470
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
    //  MCT-470 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_21)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Accum, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_22)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_KWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_23)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenKWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x91);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_24)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_25)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_26)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_27)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_28)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x93);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_29)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_KYZ, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd5);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_30)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_31)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_32)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x21);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_33)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x88);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_34)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Time, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x28);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_35)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x2d);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_36)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x28);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_37)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_38)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x32);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_39)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PrecannedTable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd3);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_ChannelSetup, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x20);
        BOOST_CHECK_EQUAL(BSt.Length,   7);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_41)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ChannelSetup, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x07);
        BOOST_CHECK_EQUAL(BSt.Length,   7);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_42)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_43)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x97);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_44)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Internal, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_45)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x13);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_46)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_IED, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_47)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_IEDDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd5);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_48)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_IEDDNP, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd4);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_49)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_IEDTime, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd3);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_IEDDNP, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x24);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_IEDDNPAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf6);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_52)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_IEDDNPAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf6);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_IEDDNPAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf6);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_IEDReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x15);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PhaseCurrent, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xda);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_65)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_SPID, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x12);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()
