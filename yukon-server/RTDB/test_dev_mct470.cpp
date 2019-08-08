#include <boost/test/unit_test.hpp>

#include "dev_mct470.h"
#include "dev_ccu.h"
#include "rte_ccu.h"
#include "devicetypes.h"
#include "boostutil.h"
#include "config_data_mct.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;
using std::vector;
typedef CtiTableDynamicPaoInfo Dpi;

struct test_CtiDeviceCCU : CtiDeviceCCU
{
    test_CtiDeviceCCU()
    {
        _paObjectID = 12345;
    }
};

struct test_CtiRouteCCU : CtiRouteCCU
{
    CtiDeviceSPtr ccu;

    test_CtiRouteCCU() : ccu(new test_CtiDeviceCCU)
    {
        _tblPAO.setID(1234, test_tag);
        setDevicePointer(ccu);
    }
};

struct test_Mct470Device : Cti::Devices::Mct470Device
{
    CtiRouteSPtr rte;

    test_Mct470Device() :
        rte(new test_CtiRouteCCU)
    {
    }

    typedef Mct470Device::point_info point_info;

    using CtiTblPAOLite::_type;
    using CtiTblPAOLite::_name;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;
    using MctDevice::getDescriptorForRead;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct470Device::extractDynamicPaoInfo;
    using Mct470Device::convertTimestamp;
    using Mct470Device::computeResolutionByte;
    using Mct470Device::ResultDecode;

    bool test_isSupported_Mct4xxFeature_LoadProfilePeakReport()
            {  return isSupported(Feature_LoadProfilePeakReport);  }
    bool test_isSupported_Mct4xxFeature_TouPeaks()
            {  return isSupported(Feature_TouPeaks);  }

    CtiRouteSPtr getRoute(long routeId) const override
    {
        return rte;
    }
};

namespace std {
    //  defined in rtdb/test_main.cpp
    std::ostream& operator<<(std::ostream& out, const unsigned char &uc);
    std::ostream& operator<<(std::ostream& out, const test_Mct470Device::ReadDescriptor &rd);
    std::ostream& operator<<(std::ostream& out, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct470Device::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct470Device::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
}
}

struct resetGlobals_helper
{
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
};

BOOST_FIXTURE_TEST_SUITE( test_dev_mct470, resetGlobals_helper )
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_isSupported_Mct4xxFeature_LoadProfilePeakReport)
{
    test_Mct470Device mct;

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct4xxFeature_TouPeaks)
{
    test_Mct470Device mct;

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct4xxFeature_TouPeaks());
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
    //  To adjust the time back, we convert the time to localSeconds.
    return local_time.getLocalTimeSeconds();
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

    BOOST_CHECK_EQUAL_RANGES(expected, results);
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

    BOOST_CHECK_EQUAL_RANGES(expected, results);
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

    BOOST_CHECK_EQUAL_RANGES(expected, results);
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

    BOOST_CHECK_EQUAL_RANGES(expected, results);
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

    std::string lpconfig;
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


BOOST_AUTO_TEST_CASE(test_dev_mct470_extractDynamicPaoInfo_MCT_LoadProfileChannelConfigX)
{
    test_Mct470Device dev;

    INMESS im;

    std::string lpconfig;

    //  Verify we start out empty
    BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig));
    BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1));
    BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2));
    BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3));
    BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4));

    //  Make sure a single channel decode doesn't populate the Key_MCT_LoadProfileConfig string
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0x8e;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

        im.Buffer.DSt.Message[ 0] = 0x77;

        dev.extractDynamicPaoInfo(im);

        BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig));
        BOOST_CHECK_EQUAL(true,  dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1));
        BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2));
        BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3));
        BOOST_CHECK_EQUAL(false, dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4));

        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x77);
    }

    //  Verify a full channel config decode
    {
        im.Buffer.DSt.Message[ 0] = 0x03;  //  3 wire KYZ, channel 1, LP interval 1
        im.Buffer.DSt.Message[ 1] = 0x4a;  //  2 wire KYZ, channel 3, LP interval 2
        im.Buffer.DSt.Message[ 2] = 0x19;  //  IED, channel 7, LP interval 1
        im.Buffer.DSt.Message[ 3] = 0x3d;  //  IED, channel 15, LP interval 1
        im.Buffer.DSt.Message[ 4] = 0x05;  //  LP interval 1 = 5 mins
        im.Buffer.DSt.Message[ 5] = 0x0f;  //  LP interval 2 = 15 mins
        im.Buffer.DSt.Message[ 6] = 0x1e;  //  IED LP interval = 30 mins

        im.Buffer.DSt.Length = 7;
        im.Return.ProtocolInfo.Emetcon.Function = 0x20;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "3002211601f0");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x03);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x4a);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x19);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x3d);
    }

    //  Verify a channel 1 function read
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0x21;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

        im.Buffer.DSt.Message[ 0] = 0x11;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "1402211601f0");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x11);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x4a);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x19);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x3d);
    }

    //  Verify a channel 1 and 2 function read
    {
        im.Buffer.DSt.Length = 6;
        im.Return.ProtocolInfo.Emetcon.Function = 0x21;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

        im.Buffer.DSt.Message[ 0] = 0x22;
        im.Buffer.DSt.Message[ 5] = 0x33;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "2803c01601f0");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x22);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x33);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x19);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x3d);
    }

    //  Verify a channel 3 function read
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0x22;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

        im.Buffer.DSt.Message[ 0] = 0x44;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "2803c00001f0");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x22);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x33);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x44);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x3d);
    }

    //  Verify a channel 3 and 4 function read
    {
        im.Buffer.DSt.Length = 6;
        im.Return.ProtocolInfo.Emetcon.Function = 0x22;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Function_Read;

        im.Buffer.DSt.Message[ 0] = 0x55;
        im.Buffer.DSt.Message[ 5] = 0x66;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "2803c0151291");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x22);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x33);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x55);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x66);
    }

    //  Verify a channel 1 memory read
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0x8e;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

        im.Buffer.DSt.Message[ 0] = 0x77;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "3d13c0151291");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x77);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x33);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x55);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x66);
    }

    //  Verify a channel 2 memory read
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0xa8;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

        im.Buffer.DSt.Message[ 0] = 0x8f;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "3d1330151291");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x77);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x8f);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x55);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x66);
    }

    //  Verify a channel 3 memory read
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0xc2;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

        im.Buffer.DSt.Message[ 0] = 0x99;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "3d1330160291");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x77);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x8f);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x99);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0x66);
    }

    //  Verify a channel 4 memory read
    {
        im.Buffer.DSt.Length = 1;
        im.Return.ProtocolInfo.Emetcon.Function = 0xdc;
        im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

        im.Buffer.DSt.Message[ 0] = 0xaa;

        dev.extractDynamicPaoInfo(im);

        dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lpconfig);
        BOOST_CHECK_EQUAL(lpconfig, "3d13301602a0");
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1), 0x77);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2), 0x8f);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3), 0x99);
        BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4), 0xaa);
    }
}


struct beginExecuteRequest_helper : resetGlobals_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;
    test_Mct470Device mct;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    beginExecuteRequest_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};


struct beginExecuteRequest_noConfig_helper : resetGlobals_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;
    test_Mct470Device mct;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    beginExecuteRequest_noConfig_helper() :
        overrideConfigManager(Cti::Config::DeviceConfigSPtr())
    {
    }

    ~beginExecuteRequest_noConfig_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};


BOOST_FIXTURE_TEST_SUITE(command_executions, beginExecuteRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_putconfig_install_precanned_table_mct470_no_meterNumber)
    {
        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("tableReadInterval", "7");
        config.insertValue("tableType", "9");
        config.insertValue("serviceProviderId", "17");

        CtiCommandParser parse("putconfig install precannedtable");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->ResultString(), "ERROR: Invalid config data. Config name:precannedtable" );
        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::NoConfigData );
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_install_precanned_table_mct470_with_meterNumber)
    {
        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("tableReadInterval", "105");     // 7 minutes
        config.insertValue("tableType", "9");
        config.insertValue("serviceProviderId", "17");
        config.insertValue("meterNumber", "3");

        CtiCommandParser parse("putconfig install precannedtable");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 3 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 211 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   4 );

            const unsigned char expected_message[] = { 17, 7, 3, 9 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 35 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   11 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_precanned_table_mct430_no_meterNumber)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("tableReadInterval", "105");     // 7 minutes
        config.insertValue("tableType", "9");
        config.insertValue("serviceProviderId", "17");

        CtiCommandParser parse("putconfig install precannedtable");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 3 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 211 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   4 );

            const unsigned char expected_message[] = { 17, 7, 0, 9 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 35 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   11 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_install_precanned_table_mct430_with_meterNumber)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("tableReadInterval", "105");     // 7 minutes
        config.insertValue("tableType", "9");
        config.insertValue("serviceProviderId", "17");
        config.insertValue("meterNumber", "3");

        CtiCommandParser parse("putconfig install precannedtable");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 3 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 211 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   4 );

            const unsigned char expected_message[] = { 17, 7, 0, 9 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 35 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   11 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct470_no_electronicMeter)
    {
        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->ResultString(), "ERROR: Invalid config data. Config name:configbyte" );
        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::NoConfigData );
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct470_matching_dynamicPaoInfo)
    {
        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");
        config.insertValue("electronicMeter", "GEKV2");

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x51);

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->ResultString(), "Config configbyte is current." );
        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct470_no_dynamicPaoInfo)
    {
        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");
        config.insertValue("electronicMeter", "GEKV2");

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );

            const unsigned char expected_message[] = { 81 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct470_mismatched_dynamicPaoInfo)
    {
        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");
        config.insertValue("electronicMeter", "GEKV2");

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x40);

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );

            const unsigned char expected_message[] = { 81 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_tou_mct470)
    {
        using namespace Cti::Config;

        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        // add TOU config

        // Schedule 1
        config.insertValue( MCTStrings::Schedule1Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule1Time2, "10:06" );
        config.insertValue( MCTStrings::Schedule1Time3, "12:22" );
        config.insertValue( MCTStrings::Schedule1Time4, "23:33" );
        config.insertValue( MCTStrings::Schedule1Time5, "23:44" );

        config.insertValue( MCTStrings::Schedule1Rate0, "A" );
        config.insertValue( MCTStrings::Schedule1Rate1, "B" );
        config.insertValue( MCTStrings::Schedule1Rate2, "C" );
        config.insertValue( MCTStrings::Schedule1Rate3, "D" );
        config.insertValue( MCTStrings::Schedule1Rate4, "A" );
        config.insertValue( MCTStrings::Schedule1Rate5, "B" );

        // Schedule 2
        config.insertValue( MCTStrings::Schedule2Time1, "01:23" );
        config.insertValue( MCTStrings::Schedule2Time2, "03:12" );
        config.insertValue( MCTStrings::Schedule2Time3, "04:01" );
        config.insertValue( MCTStrings::Schedule2Time4, "05:23" );
        config.insertValue( MCTStrings::Schedule2Time5, "16:28" );

        config.insertValue( MCTStrings::Schedule2Rate0, "D" );
        config.insertValue( MCTStrings::Schedule2Rate1, "A" );
        config.insertValue( MCTStrings::Schedule2Rate2, "B" );
        config.insertValue( MCTStrings::Schedule2Rate3, "C" );
        config.insertValue( MCTStrings::Schedule2Rate4, "D" );
        config.insertValue( MCTStrings::Schedule2Rate5, "A" );

        // Schedule 3
        config.insertValue( MCTStrings::Schedule3Time1, "01:02" );
        config.insertValue( MCTStrings::Schedule3Time2, "02:03" );
        config.insertValue( MCTStrings::Schedule3Time3, "04:05" );
        config.insertValue( MCTStrings::Schedule3Time4, "05:06" );
        config.insertValue( MCTStrings::Schedule3Time5, "06:07" );

        config.insertValue( MCTStrings::Schedule3Rate0, "C" );
        config.insertValue( MCTStrings::Schedule3Rate1, "D" );
        config.insertValue( MCTStrings::Schedule3Rate2, "A" );
        config.insertValue( MCTStrings::Schedule3Rate3, "B" );
        config.insertValue( MCTStrings::Schedule3Rate4, "C" );
        config.insertValue( MCTStrings::Schedule3Rate5, "D" );

        // Schedule 4
        config.insertValue( MCTStrings::Schedule4Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule4Time2, "08:59" );
        config.insertValue( MCTStrings::Schedule4Time3, "12:12" );
        config.insertValue( MCTStrings::Schedule4Time4, "23:01" );
        config.insertValue( MCTStrings::Schedule4Time5, "23:55" );

        config.insertValue( MCTStrings::Schedule4Rate0, "B" );
        config.insertValue( MCTStrings::Schedule4Rate1, "C" );
        config.insertValue( MCTStrings::Schedule4Rate2, "D" );
        config.insertValue( MCTStrings::Schedule4Rate3, "A" );
        config.insertValue( MCTStrings::Schedule4Rate4, "B" );
        config.insertValue( MCTStrings::Schedule4Rate5, "C" );

        // day table
        config.insertValue( MCTStrings::SundaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::MondaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::TuesdaySchedule,   "SCHEDULE_3" );
        config.insertValue( MCTStrings::WednesdaySchedule, "SCHEDULE_2" );
        config.insertValue( MCTStrings::ThursdaySchedule,  "SCHEDULE_4" );
        config.insertValue( MCTStrings::FridaySchedule,    "SCHEDULE_2" );
        config.insertValue( MCTStrings::SaturdaySchedule,  "SCHEDULE_3" );
        config.insertValue( MCTStrings::HolidaySchedule,   "SCHEDULE_3" );

        // default rate
        config.insertValue( MCTStrings::DefaultTOURate, "B" );

        // set TOU enabled
        config.insertValue( MCTStrings::touEnabled, "true" );

        CtiCommandParser parse("putconfig install tou");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 6 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        // 3 writes
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            // This is the Enable TOU command
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, EmetconProtocol::IO_Write );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x56 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      0 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x30 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     15 );

            const unsigned char expected_message[] =
            {
                0xa7, // Day table
                0x60, // a760 -> 1010011101100000 -> 10 10 01 11 01 10 00 00
                //  schedule 1
                0x00,  //  switch 1
                0x79,  //  switch 2
                0x1b,  //  switch 3
                0x86,  //  switch 4
                0x02,  //  switch 5
                //  schedule 1+2 rates
                0x34,
                //  schedule 1 rates byte 6
                0xe4,
                //  schedule 2
                0x10,  //  switch 1
                0x16,  //  switch 2
                0x0a,  //  switch 3
                0x10,  //  switch 4
                0x85,  //  switch 5
                //  schedule 2 rates byte 6
                0x93
            };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x31 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     15 );

            const unsigned char expected_message[] =
            {
                //  schedule 3
                0x0c,  //  switch 1
                0x0c,  //  switch 2
                0x19,  //  switch 3
                0x0c,  //  switch 4
                0x0c,  //  switch 5
                //  schedule 3 rates
                0x0e,
                0x4e,
                //  schedule 4
                0x00,  //  switch 1
                0x6b,  //  switch 2
                0x27,  //  switch 3
                0x82,  //  switch 4
                0x0b,  //  switch 5
                //  schedule 4 rates
                0x09,
                0x39,
                //  default TOU rate
                0x01
            };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }

        // 3 reads
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xae );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xaf );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xad );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     11 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_tou_mct470_trailing_midnights)
    {
        using namespace Cti::Config;

        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        // add TOU config

        // Schedule 1
        config.insertValue( MCTStrings::Schedule1Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule1Time2, "10:06" );
        config.insertValue( MCTStrings::Schedule1Time3, "12:22" );
        config.insertValue( MCTStrings::Schedule1Time4, "00:00" );
        config.insertValue( MCTStrings::Schedule1Time5, "00:00" );

        config.insertValue( MCTStrings::Schedule1Rate0, "A" );
        config.insertValue( MCTStrings::Schedule1Rate1, "B" );
        config.insertValue( MCTStrings::Schedule1Rate2, "C" );
        config.insertValue( MCTStrings::Schedule1Rate3, "D" );
        config.insertValue( MCTStrings::Schedule1Rate4, "A" );
        config.insertValue( MCTStrings::Schedule1Rate5, "B" );

        // Schedule 2
        config.insertValue( MCTStrings::Schedule2Time1, "01:23" );
        config.insertValue( MCTStrings::Schedule2Time2, "03:12" );
        config.insertValue( MCTStrings::Schedule2Time3, "00:00" );
        config.insertValue( MCTStrings::Schedule2Time4, "00:00" );
        config.insertValue( MCTStrings::Schedule2Time5, "00:00" );

        config.insertValue( MCTStrings::Schedule2Rate0, "D" );
        config.insertValue( MCTStrings::Schedule2Rate1, "A" );
        config.insertValue( MCTStrings::Schedule2Rate2, "B" );
        config.insertValue( MCTStrings::Schedule2Rate3, "C" );
        config.insertValue( MCTStrings::Schedule2Rate4, "D" );
        config.insertValue( MCTStrings::Schedule2Rate5, "A" );

        // Schedule 3
        config.insertValue( MCTStrings::Schedule3Time1, "01:02" );
        config.insertValue( MCTStrings::Schedule3Time2, "00:00" );
        config.insertValue( MCTStrings::Schedule3Time3, "00:00" );
        config.insertValue( MCTStrings::Schedule3Time4, "00:00" );
        config.insertValue( MCTStrings::Schedule3Time5, "00:00" );

        config.insertValue( MCTStrings::Schedule3Rate0, "C" );
        config.insertValue( MCTStrings::Schedule3Rate1, "D" );
        config.insertValue( MCTStrings::Schedule3Rate2, "A" );
        config.insertValue( MCTStrings::Schedule3Rate3, "B" );
        config.insertValue( MCTStrings::Schedule3Rate4, "C" );
        config.insertValue( MCTStrings::Schedule3Rate5, "D" );

        // Schedule 4
        config.insertValue( MCTStrings::Schedule4Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule4Time2, "08:59" );
        config.insertValue( MCTStrings::Schedule4Time3, "12:12" );
        config.insertValue( MCTStrings::Schedule4Time4, "23:01" );
        config.insertValue( MCTStrings::Schedule4Time5, "23:55" );

        config.insertValue( MCTStrings::Schedule4Rate0, "B" );
        config.insertValue( MCTStrings::Schedule4Rate1, "C" );
        config.insertValue( MCTStrings::Schedule4Rate2, "D" );
        config.insertValue( MCTStrings::Schedule4Rate3, "A" );
        config.insertValue( MCTStrings::Schedule4Rate4, "B" );
        config.insertValue( MCTStrings::Schedule4Rate5, "C" );

        // day table
        config.insertValue( MCTStrings::SundaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::MondaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::TuesdaySchedule,   "SCHEDULE_3" );
        config.insertValue( MCTStrings::WednesdaySchedule, "SCHEDULE_2" );
        config.insertValue( MCTStrings::ThursdaySchedule,  "SCHEDULE_4" );
        config.insertValue( MCTStrings::FridaySchedule,    "SCHEDULE_2" );
        config.insertValue( MCTStrings::SaturdaySchedule,  "SCHEDULE_3" );
        config.insertValue( MCTStrings::HolidaySchedule,   "SCHEDULE_3" );

        // default rate
        config.insertValue( MCTStrings::DefaultTOURate, "B" );

        // set TOU enabled
        config.insertValue( MCTStrings::touEnabled, "true" );

        CtiCommandParser parse("putconfig install tou");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList .empty() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 6 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        // 3 writes
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            // This is the Enable TOU command
            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO, EmetconProtocol::IO_Write );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x56 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      0 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x30 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     15 );

            const unsigned char expected_message[] =
            {
                0xa7, // Day table
                0x60, // a760 -> 1010011101100000 -> 10 10 01 11 01 10 00 00
                //  schedule 1
                0x00,  //  switch 1
                0x79,  //  switch 2
                0x1b,  //  switch 3
                0xff,  //  switch 4
                0xff,  //  switch 5
                //  schedule 1+2 rates byte 5
                0x5f,
                //  schedule 1 rates byte 6
                0xe4,  //  fe4 -> 111111100100 -> 11 11 11 10 01 00 -> A B C D D D
                //  schedule 2
                0x10,  //  switch 1
                0x16,  //  switch 2
                0xff,  //  switch 3
                0xff,  //  switch 4
                0xff,  //  switch 5
                //  schedule 2 rates byte 6
                0x53   //  a92 -> 101010010010 -> 10 10 10 01 00 10 -> C A B C C C
            };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x31 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     15 );

            const unsigned char expected_message[] =
            {
                //  schedule 3
                0x0c,  //  switch 1
                0xff,  //  switch 2
                0xff,  //  switch 3
                0xff,  //  switch 4
                0xff,  //  switch 5
                //  schedule 3 rates
                0x0f,
                0xfe,
                //  schedule 4
                0x00,  //  switch 1
                0x6b,  //  switch 2
                0x27,  //  switch 3
                0x82,  //  switch 4
                0x0b,  //  switch 5
                //  schedule 4 rates
                0x09,
                0x39,
                //  default TOU rate
                0x01
            };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }

        // 3 reads
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xae );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xaf );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xad );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     11 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_tou_mct470_out_of_order_times)
    {
        using namespace Cti::Config;

        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        // add TOU config

        // Schedule 1
        config.insertValue( MCTStrings::Schedule1Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule1Time2, "10:06" );
        config.insertValue( MCTStrings::Schedule1Time3, "12:22" );
        config.insertValue( MCTStrings::Schedule1Time4, "23:44" );
        config.insertValue( MCTStrings::Schedule1Time5, "23:33" );

        config.insertValue( MCTStrings::Schedule1Rate0, "A" );
        config.insertValue( MCTStrings::Schedule1Rate1, "B" );
        config.insertValue( MCTStrings::Schedule1Rate2, "C" );
        config.insertValue( MCTStrings::Schedule1Rate3, "D" );
        config.insertValue( MCTStrings::Schedule1Rate4, "A" );
        config.insertValue( MCTStrings::Schedule1Rate5, "B" );

        // Schedule 2
        config.insertValue( MCTStrings::Schedule2Time1, "01:23" );
        config.insertValue( MCTStrings::Schedule2Time2, "03:12" );
        config.insertValue( MCTStrings::Schedule2Time3, "04:01" );
        config.insertValue( MCTStrings::Schedule2Time4, "05:23" );
        config.insertValue( MCTStrings::Schedule2Time5, "16:28" );

        config.insertValue( MCTStrings::Schedule2Rate0, "D" );
        config.insertValue( MCTStrings::Schedule2Rate1, "A" );
        config.insertValue( MCTStrings::Schedule2Rate2, "B" );
        config.insertValue( MCTStrings::Schedule2Rate3, "C" );
        config.insertValue( MCTStrings::Schedule2Rate4, "D" );
        config.insertValue( MCTStrings::Schedule2Rate5, "A" );

        // Schedule 3
        config.insertValue( MCTStrings::Schedule3Time1, "01:02" );
        config.insertValue( MCTStrings::Schedule3Time2, "02:03" );
        config.insertValue( MCTStrings::Schedule3Time3, "04:05" );
        config.insertValue( MCTStrings::Schedule3Time4, "05:06" );
        config.insertValue( MCTStrings::Schedule3Time5, "06:07" );

        config.insertValue( MCTStrings::Schedule3Rate0, "C" );
        config.insertValue( MCTStrings::Schedule3Rate1, "D" );
        config.insertValue( MCTStrings::Schedule3Rate2, "A" );
        config.insertValue( MCTStrings::Schedule3Rate3, "B" );
        config.insertValue( MCTStrings::Schedule3Rate4, "C" );
        config.insertValue( MCTStrings::Schedule3Rate5, "D" );

        // Schedule 4
        config.insertValue( MCTStrings::Schedule4Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule4Time2, "08:59" );
        config.insertValue( MCTStrings::Schedule4Time3, "12:12" );
        config.insertValue( MCTStrings::Schedule4Time4, "23:01" );
        config.insertValue( MCTStrings::Schedule4Time5, "23:55" );

        config.insertValue( MCTStrings::Schedule4Rate0, "B" );
        config.insertValue( MCTStrings::Schedule4Rate1, "C" );
        config.insertValue( MCTStrings::Schedule4Rate2, "D" );
        config.insertValue( MCTStrings::Schedule4Rate3, "A" );
        config.insertValue( MCTStrings::Schedule4Rate4, "B" );
        config.insertValue( MCTStrings::Schedule4Rate5, "C" );

        // day table
        config.insertValue( MCTStrings::SundaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::MondaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::TuesdaySchedule,   "SCHEDULE_3" );
        config.insertValue( MCTStrings::WednesdaySchedule, "SCHEDULE_2" );
        config.insertValue( MCTStrings::ThursdaySchedule,  "SCHEDULE_4" );
        config.insertValue( MCTStrings::FridaySchedule,    "SCHEDULE_2" );
        config.insertValue( MCTStrings::SaturdaySchedule,  "SCHEDULE_3" );
        config.insertValue( MCTStrings::HolidaySchedule,   "SCHEDULE_3" );

        // default rate
        config.insertValue( MCTStrings::DefaultTOURate, "B" );

        // set TOU enabled
        config.insertValue( MCTStrings::touEnabled, "true" );

        CtiCommandParser parse("putconfig install tou");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( outList.empty() );

        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->ResultString(), "ERROR: Invalid config data. Config name:tou" );
        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::NoConfigData );
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_mct470)
    {
        using namespace Cti::Config;

        mct._type = TYPEMCT470;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("timeZoneOffset", "CHICAGO");

        config.insertValue("timeAdjustTolerance", "3");

        config.insertValue("serviceProviderId", "127");

        config.insertValue("demandInterval",  "87");
        config.insertValue("profileInterval", "98");

        config.insertValue("channel1PhysicalChannel",   "7");
        config.insertValue("channel1Type",              "ELECTRONIC_METER");  //  Electronic
        config.insertValue("channel1PeakKWResolution",  "ONE");
        config.insertValue("channel1LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel1ProfileResolution", "MINUS_ONE");
        //---
        config.insertValue("channel2PhysicalChannel",   "6");
        config.insertValue("channel2Type",              "ELECTRONIC_METER");  //  Electronic
        config.insertValue("channel2PeakKWResolution",  "ONE");
        config.insertValue("channel2LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel2ProfileResolution", "MINUS_ONE");
        //---
        config.insertValue("channel3PhysicalChannel",   "5");
        config.insertValue("channel3Type",              "CHANNEL_NOT_USED");  //  Disabled
        config.insertValue("channel3PeakKWResolution",  "ONE");
        config.insertValue("channel3LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel3ProfileResolution", "MINUS_ONE");
        //---
        config.insertValue("channel4PhysicalChannel",   "4");
        config.insertValue("channel4Type",              "CHANNEL_NOT_USED");  //  Disabled
        config.insertValue("channel4PeakKWResolution",  "ONE");
        config.insertValue("channel4LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel4ProfileResolution", "MINUS_ONE");

        config.insertValue("tableReadInterval", "75");
        config.insertValue("tableType",         "11");
        config.insertValue("meterNumber",       "6");

        // Schedule 1
        config.insertValue( MCTStrings::Schedule1Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule1Time2, "10:06" );
        config.insertValue( MCTStrings::Schedule1Time3, "12:22" );
        config.insertValue( MCTStrings::Schedule1Time4, "23:33" );
        config.insertValue( MCTStrings::Schedule1Time5, "23:44" );

        config.insertValue( MCTStrings::Schedule1Rate0, "A" );
        config.insertValue( MCTStrings::Schedule1Rate1, "B" );
        config.insertValue( MCTStrings::Schedule1Rate2, "C" );
        config.insertValue( MCTStrings::Schedule1Rate3, "D" );
        config.insertValue( MCTStrings::Schedule1Rate4, "A" );
        config.insertValue( MCTStrings::Schedule1Rate5, "B" );

        // Schedule 2
        config.insertValue( MCTStrings::Schedule2Time1, "01:23" );
        config.insertValue( MCTStrings::Schedule2Time2, "03:12" );
        config.insertValue( MCTStrings::Schedule2Time3, "04:01" );
        config.insertValue( MCTStrings::Schedule2Time4, "05:23" );
        config.insertValue( MCTStrings::Schedule2Time5, "16:28" );

        config.insertValue( MCTStrings::Schedule2Rate0, "D" );
        config.insertValue( MCTStrings::Schedule2Rate1, "A" );
        config.insertValue( MCTStrings::Schedule2Rate2, "B" );
        config.insertValue( MCTStrings::Schedule2Rate3, "C" );
        config.insertValue( MCTStrings::Schedule2Rate4, "D" );
        config.insertValue( MCTStrings::Schedule2Rate5, "A" );

        // Schedule 3
        config.insertValue( MCTStrings::Schedule3Time1, "01:02" );
        config.insertValue( MCTStrings::Schedule3Time2, "02:03" );
        config.insertValue( MCTStrings::Schedule3Time3, "04:05" );
        config.insertValue( MCTStrings::Schedule3Time4, "05:06" );
        config.insertValue( MCTStrings::Schedule3Time5, "06:07" );

        config.insertValue( MCTStrings::Schedule3Rate0, "C" );
        config.insertValue( MCTStrings::Schedule3Rate1, "D" );
        config.insertValue( MCTStrings::Schedule3Rate2, "A" );
        config.insertValue( MCTStrings::Schedule3Rate3, "B" );
        config.insertValue( MCTStrings::Schedule3Rate4, "C" );
        config.insertValue( MCTStrings::Schedule3Rate5, "D" );

        // Schedule 4
        config.insertValue( MCTStrings::Schedule4Time1, "00:01" );
        config.insertValue( MCTStrings::Schedule4Time2, "08:59" );
        config.insertValue( MCTStrings::Schedule4Time3, "12:12" );
        config.insertValue( MCTStrings::Schedule4Time4, "23:01" );
        config.insertValue( MCTStrings::Schedule4Time5, "23:55" );

        config.insertValue( MCTStrings::Schedule4Rate0, "B" );
        config.insertValue( MCTStrings::Schedule4Rate1, "C" );
        config.insertValue( MCTStrings::Schedule4Rate2, "D" );
        config.insertValue( MCTStrings::Schedule4Rate3, "A" );
        config.insertValue( MCTStrings::Schedule4Rate4, "B" );
        config.insertValue( MCTStrings::Schedule4Rate5, "C" );

        // day table
        config.insertValue( MCTStrings::SundaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::MondaySchedule,    "SCHEDULE_1" );
        config.insertValue( MCTStrings::TuesdaySchedule,   "SCHEDULE_3" );
        config.insertValue( MCTStrings::WednesdaySchedule, "SCHEDULE_2" );
        config.insertValue( MCTStrings::ThursdaySchedule,  "SCHEDULE_4" );
        config.insertValue( MCTStrings::FridaySchedule,    "SCHEDULE_2" );
        config.insertValue( MCTStrings::SaturdaySchedule,  "SCHEDULE_3" );
        config.insertValue( MCTStrings::HolidaySchedule,   "SCHEDULE_3" );

        // default rate
        config.insertValue( MCTStrings::DefaultTOURate, "B" );

        // set TOU enabled
        config.insertValue( MCTStrings::touEnabled, "true" );

        config.insertValue("enableDst", "true");
        config.insertValue("electronicMeter", "GEKV2");

        config.insertValue("relayATimer", "4");
        config.insertValue("relayBTimer", "8");

        CtiCommandParser parse("putconfig install all");
        request.setConnectionHandle(Cti::ConnectionHandle{ 1 });  //  so the OMs report that they were sent

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );

        {
            const std::vector<std::string> resultString_exp = boost::assign::list_of
                ("Emetcon DLC command sent on route ").repeat(24, "Emetcon DLC command sent on route ");
            const std::vector<long> status_exp = boost::assign::list_of
                (0).repeat(24, 0);

            std::vector<std::string> resultString_rcv;
            std::vector<long> status_rcv;

            for each( const CtiMessage *msg in retList )
            {
                const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(msg);

                BOOST_REQUIRE(retMsg);

                resultString_rcv.push_back( retMsg->ResultString() );
                status_rcv.push_back( retMsg->Status() );
            }

            BOOST_CHECK_EQUAL_RANGES(resultString_exp,
                                     resultString_rcv);
            BOOST_CHECK_EQUAL_RANGES(status_exp,
                                     status_rcv);
        }

        BOOST_REQUIRE_EQUAL( outList.size(), 25 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        //  timezone (OMs 1-2)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install timezone" );
        }
        //  time adjust tolerance (OMs 3-4)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 31 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x03 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 31 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install timeadjusttolerance" );
        }
        //  service provider ID (OMs 5-6)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install spid" );
        }
        //  demand and profile intervals (OMs 7-8)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x57, 0x62 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 50 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install demandlp" );
        }
        //  channel setup (OMs 9-12)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 7 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f, 0x01, 0x19, 0x00, 0x13, 0x00, 0x00, 0x02, 0x15, 0x00, 0x13, 0x00, 0x00 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 33 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   10 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install lpchannel 12" );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 7 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f, 0x03, 0x10, 0x00, 0x00, 0x00, 0x00, 0x04, 0x0c, 0x00, 0x00, 0x00, 0x00 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 34 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   10 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install lpchannel 34" );
        }
        //  precanned table (OMs 13-15)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 211 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   4 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f, 0x05, 0x06, 0x0b };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 35 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   11 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install precannedtable nospid" );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install precannedtable spid" );
        }
        //  config byte (OMs 16-17)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x51 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct430_matching_dynamicPaoInfo)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x51);

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->ResultString(), "Config configbyte is current." );
        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct430_no_dynamicPaoInfo)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );

            const unsigned char expected_message[] = { 1 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_configbyte_mct430_mismatched_dynamicPaoInfo)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "true");

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x40);

        CtiCommandParser parse("putconfig install configbyte");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );

            const unsigned char expected_message[] = { 1 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_timezone_mct430_matching_dynamicPaoInfo)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("timeZoneOffset", "CHICAGO");

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset, 0xe8);

        CtiCommandParser parse("putconfig install timezone");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->ResultString(), "Config timezone is current." );
        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_timezone_mct430_no_dynamicPaoInfo)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("timeZoneOffset", "CHICAGO");

        CtiCommandParser parse("putconfig install timezone");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );

            const unsigned char expected_message[] = { 0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_timezone_mct430_mismatched_dynamicPaoInfo)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("timeZoneOffset", "CHICAGO");

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset, 0xe9);

        CtiCommandParser parse("putconfig install timezone");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );

        BOOST_REQUIRE_EQUAL( outList.size(), 2 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );

            const unsigned char expected_message[] = { 0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putconfig_install_mct430)
    {
        mct._type = TYPEMCT430S4;

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("timeZoneOffset", "CHICAGO");

        config.insertValue("timeAdjustTolerance", "3");

        config.insertValue("serviceProviderId", "127");

        config.insertValue("demandInterval",  "87");

        config.insertValue("channel1PhysicalChannel",   "7");
        config.insertValue("channel1Type",              "ELECTRONIC_METER");  //  Electronic
        config.insertValue("channel1PeakKWResolution",  "ONE");
        config.insertValue("channel1LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel1ProfileResolution", "MINUS_ONE");
        //---
        config.insertValue("channel2PhysicalChannel",   "6");
        config.insertValue("channel2Type",              "ELECTRONIC_METER");  //  Electronic
        config.insertValue("channel2PeakKWResolution",  "ONE");
        config.insertValue("channel2LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel2ProfileResolution", "MINUS_ONE");
        //---
        config.insertValue("channel3PhysicalChannel",   "5");
        config.insertValue("channel3Type",              "CHANNEL_NOT_USED");  //  Disabled
        config.insertValue("channel3PeakKWResolution",  "ONE");
        config.insertValue("channel3LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel3ProfileResolution", "MINUS_ONE");
        //---
        config.insertValue("channel4PhysicalChannel",   "4");
        config.insertValue("channel4Type",              "CHANNEL_NOT_USED");  //  Disabled
        config.insertValue("channel4PeakKWResolution",  "ONE");
        config.insertValue("channel4LastIntervalDemandResolution", "ZERO");
        config.insertValue("channel4ProfileResolution", "MINUS_ONE");

        config.insertValue("tableReadInterval", "75");
        config.insertValue("tableType",         "11");

        config.insertValue("enableDst", "true");

        CtiCommandParser parse("putconfig install all");
        request.setConnectionHandle(Cti::ConnectionHandle{ 1 });  //  so the OMs report that they were sent

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );

        {
            const std::vector<std::string> resultString_exp = boost::assign::list_of
                ("Emetcon DLC command sent on route ").repeat(16, "Emetcon DLC command sent on route ");
            const std::vector<long> status_exp = boost::assign::list_of
                (0).repeat(16, 0);

            std::vector<std::string> resultString_rcv;
            std::vector<long> status_rcv;

            for each( const CtiMessage *msg in retList )
            {
                const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(msg);

                BOOST_REQUIRE(retMsg);

                resultString_rcv.push_back( retMsg->ResultString() );
                status_rcv.push_back( retMsg->Status() );
            }

            BOOST_CHECK_EQUAL_RANGES(resultString_exp,
                                     resultString_rcv);
            BOOST_CHECK_EQUAL_RANGES(status_exp,
                                     status_rcv);
        }

        BOOST_REQUIRE_EQUAL( outList.size(), 17 );

        CtiDeviceBase::OutMessageList::const_iterator om_itr = outList.begin();

        //  timezone (OMs 1-2)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 40 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install timezone" );
        }
        //  time adjust tolerance (OMs 3-4)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 31 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x03 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 31 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install timeadjusttolerance" );
        }
        //  service provider ID (OMs 5-6)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install spid" );
        }
        //  demand interval (OMs 7-8)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x57 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 50 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install demandlp" );
        }
        //  channel setup (OMs 9-12)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 7 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f, 0x01, 0x19, 0x00, 0x13, 0x00, 0x00, 0x02, 0x15, 0x00, 0x13, 0x00, 0x00 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 33 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   10 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install lpchannel 12" );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 7 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f, 0x03, 0x10, 0x00, 0x00, 0x00, 0x00, 0x04, 0x0c, 0x00, 0x00, 0x00, 0x00 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 34 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   10 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install lpchannel 34" );
        }
        //  precanned table (OMs 13-15)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 211 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   4 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x7f, 0x05, 0x00, 0x0b };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 35 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   11 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install precannedtable nospid" );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 18 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "getconfig install precannedtable spid" );
        }
        //  config byte (OMs 16-17)
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
            BOOST_CHECK_EQUAL( om->Request.CommandStr, "" );

            const unsigned char expected_message[] = { 0x01 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length,
                expected_message,
                expected_message + sizeof(expected_message) );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_alpha)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset alpha");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "ALPHA_P_PLUS");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 3, 0, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_alpha_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x30);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "S4");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

        const unsigned char expected_message[] = { 255, 1, 0, 43 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_s4_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x10);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommand );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   Cti::Devices::Mct470Device::FuncWrite_IEDCommandLen );

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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "ALPHA_A3");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 2, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_a3_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x20);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "GEKV2C");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 8, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2c_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x80);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "GEKV2");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 5, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x50);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "GEKV");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 4, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x40);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        config.insertValue("enableDst", "false");
        config.insertValue("electronicMeter", "SENTINEL");

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 6, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }
    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_sentinel_dynamicpaoinfo)
    {
        mct._type = TYPEMCT470;

        mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, 0x60);

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
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

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 6, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_FIXTURE_TEST_CASE(test_putvalue_ied_reset_kvetch, beginExecuteRequest_noConfig_helper)
    {
        mct._type = TYPEMCT470;

        //  "kvetch" was chosen as a word that contains "kv"
        CtiCommandParser parse("putvalue ied reset kvetch");

        BOOST_CHECK_EQUAL( ClientErrors::MissingConfig, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE( outList.empty() );

        BOOST_REQUIRE_EQUAL( retList.size(), 2 );

        {
            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(),       ClientErrors::MissingConfig );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), "Could not determine the IED type" );
            BOOST_CHECK_EQUAL( retMsg->ExpectMore(),   false );
        }

        {
            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.back());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(),       ClientErrors::MissingConfig );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), "NoMethod or invalid command." );
            BOOST_CHECK_EQUAL( retMsg->ExpectMore(),   false );
        }
    }

    BOOST_FIXTURE_TEST_CASE(test_putvalue_ied_reset_no_deviceconfig_no_dynamicpaoinfo, beginExecuteRequest_noConfig_helper)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("putvalue ied reset");

        BOOST_CHECK_EQUAL( ClientErrors::MissingConfig, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE( outList.empty() );

        BOOST_REQUIRE_EQUAL( retList.size(), 2 );

        {
            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(),       ClientErrors::MissingConfig );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), "Could not determine the IED type" );
            BOOST_CHECK_EQUAL( retMsg->ExpectMore(),   false );
        }

        {
            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.back());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(),       ClientErrors::MissingConfig );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), "NoMethod or invalid command." );
            BOOST_CHECK_EQUAL( retMsg->ExpectMore(),   false );
        }
    }

    BOOST_AUTO_TEST_CASE(test_putvalue_ied_reset_kv2_noqueue)
    {
        mct._type = TYPEMCT470;

        //  make sure it can pick out "kv2" even if there's another parameter after the IED type
        CtiCommandParser parse("putvalue ied reset kv2 noqueue");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Write );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, Cti::Devices::Mct470Device::FuncWrite_IEDCommandWithData );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

        const unsigned char expected_message[] = { 255, 5, 0, 9, 1, 1 };

        BOOST_CHECK_EQUAL_COLLECTIONS(
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length,
            expected_message,
            expected_message + sizeof(expected_message) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_ied_dnp_address)
    {
        mct._type = TYPEMCT470;

        CtiCommandParser parse("getconfig ied dnp address");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 246 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   4 );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_tou_schedule_1)
    {
        mct._type = TYPEMCT470;
        mct._name = "Jimmy";

        CtiCommandParser parse("getconfig tou schedule 1");
        request.setCommandString(parse.getCommandStr());

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 174 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13 );

        INMESS  InMessage;

        OutEchoToIN(*om, InMessage);

        delete_container(outList);
        outList.clear();

        unsigned char test_data[] = {
                //  schedule 1
                0x00,  //  switch 1
                0x79,  //  switch 2
                0x1b,  //  switch 3
                0x86,  //  switch 4
                0x02,  //  switch 5
                //  schedule 2+1 rates byte 5, respectively
                0xe3,
                //  schedule 1 rates byte 6
                0x91,
                //  schedule 2
                0x10,  //  switch 1
                0x16,  //  switch 2
                0x0a,  //  switch 3
                0x10,  //  switch 4
                0x85,  //  switch 5
                //  schedule 2 rates byte 6
                0x4c };

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
        InMessage.Buffer.DSt.Length = sizeof(test_data);

        BOOST_CHECK( ! mct.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1));
        BOOST_CHECK( ! mct.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2));

        BOOST_CHECK_EQUAL(ClientErrors::None, mct.ResultDecode(InMessage, CtiTime(), vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);
        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());

        {
            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Jimmy / TOU Schedule 1:"
                "\n00:00: B"
                "\n00:00: A"
                "\n10:05: B"
                "\n12:20: C"
                "\n23:30: D"
                "\n23:40: A"
                "\n- end of day - "
                "\n"
                "\nJimmy / TOU Schedule 2:"
                "\n00:00: A"
                "\n01:20: D"
                "\n03:10: A"
                "\n04:00: B"
                "\n05:20: C"
                "\n16:25: D"
                "\n- end of day - "
                "\n"
                "\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );
        }

        {
            std::string daySchedule;
            BOOST_CHECK(mct.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, daySchedule));
            BOOST_CHECK_EQUAL(daySchedule, "0 121 27 134 2 0 1 2 3 0 1");
        }
        {
            std::string daySchedule;
            BOOST_CHECK(mct.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, daySchedule));
            BOOST_CHECK_EQUAL(daySchedule, "16 22 10 16 133 3 0 1 2 3 0");
        }
    }

    BOOST_AUTO_TEST_CASE(test_getstatus_lp)
    {
        mct._type = TYPEMCT470;
        mct._name = "Jimmy";

        CtiCommandParser parse("getstatus lp");

        BOOST_CHECK_EQUAL( ClientErrors::None, mct.beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE(om);

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 151 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   11 );

        INMESS  InMessage;

        OutEchoToIN(*om, InMessage);

        delete_container(outList);
        outList.clear();

        unsigned char test_data[] = { 0x54, 0xDA, 0x5E, 0x6B, 5, 0, 0, 0, 0, 11, 60 };

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
        InMessage.Buffer.DSt.Length = sizeof(test_data);

        BOOST_CHECK_EQUAL(ClientErrors::None, mct.ResultDecode(InMessage, CtiTime(), vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);
        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());

        {
            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Jimmy / Load Profile Channel 1 Status:"
                "\nLong Load Profile Interval Time: 02/10/2015 13:39:23"
                "\nCurrent Interval Pointer: 5"
                "\n"
                "\nJimmy / Load Profile Channel 2 Status:"
                "\nLong Load Profile Interval Time: (none)"
                "\nCurrent Interval Pointer: 11"
                "\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );
        }
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct470_decodeGetValueIED)
{
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
        dev.ResultDecode(im, CtiTime(), vg_list, ret_list, om_list);

        BOOST_REQUIRE_EQUAL(1, ret_list.size());

        BOOST_REQUIRE(ret_list.front());

        CtiReturnMsg &rm = *(static_cast<CtiReturnMsg *>(ret_list.front()));

        BOOST_CHECK_EQUAL(0, rm.PointData().size());

        delete ret_list.front();
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
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_IEDDNPAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf6);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_IEDReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x15);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PhaseCurrent, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xda);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_SPID, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x12);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct470Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,5,(int)Dpi::Key_MCT_SSpec)(1,1,(int)Dpi::Key_MCT_SSpecRevision)(2,1,(int)Dpi::Key_MCT_Options))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,1,(int)Dpi::Key_MCT_Options)(2,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Options)(1,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration))
        (empty)
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_EventFlagsMask1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask1)(1,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        //  memory read 10
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_AddressBronze))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressBronze)(2,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressLead)(2,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressCollection)(2,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (empty)
        //  memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        //  memory read 30
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(2,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(1,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTStartTime))
        (empty)
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTEndTime))
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        //  memory read 40
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DemandInterval))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DemandInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        //  memory read 50
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(2,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(1,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(2,1,(int)Dpi::Key_MCT_PrecannedMeterNumber))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(1,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(2,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(1,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 60
        .repeat(10, empty)
        //  memory read 70
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_RelayATimer))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_RelayATimer)(2,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_RelayATimer)(1,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_RelayBTimer))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_DayTable))
        //  memory read 80
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule1))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule2))
        //  memory read 90
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule3))
        (empty)
        (empty)
        (empty)
        //  memory read 100
        (empty)
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule4))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DefaultTOURate))
        //  memory read 110
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 120
        .repeat(10, empty)
        //  memory read 130
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (empty)
        //  memory read 140
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 150
        .repeat(10, empty)
        //  memory read 160
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (empty)
        //  memory read 170
        .repeat(10, empty)
        //  memory read 180
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        //  memory read 190
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 200
        .repeat(10, empty)
        //  memory read 210
        (empty)
        (empty)
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (empty)
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        //  memory read 220
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (empty)
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1))
        (empty)
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2))
        (empty)
        //  memory read 230
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 240
        .repeat(16, empty);

    const test_Mct470Device dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_RANGES(results, expected);
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct470Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,5,(int)Dpi::Key_MCT_SSpec)(1,1,(int)Dpi::Key_MCT_SSpecRevision)(2,1,(int)Dpi::Key_MCT_Options)(3,1,(int)Dpi::Key_MCT_Configuration))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,1,(int)Dpi::Key_MCT_Options)(2,1,(int)Dpi::Key_MCT_Configuration)(7,1,(int)Dpi::Key_MCT_EventFlagsMask1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Options)(1,1,(int)Dpi::Key_MCT_Configuration)(6,1,(int)Dpi::Key_MCT_EventFlagsMask1)(7,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(5,1,(int)Dpi::Key_MCT_EventFlagsMask1)(6,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_EventFlagsMask1)(5,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_EventFlagsMask1)(4,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_EventFlagsMask1)(3,1,(int)Dpi::Key_MCT_EventFlagsMask2)(7,1,(int)Dpi::Key_MCT_AddressBronze))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(6,1,(int)Dpi::Key_MCT_AddressBronze)(7,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask1)(1,1,(int)Dpi::Key_MCT_EventFlagsMask2)(5,1,(int)Dpi::Key_MCT_AddressBronze)(6,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask2)(4,1,(int)Dpi::Key_MCT_AddressBronze)(5,2,(int)Dpi::Key_MCT_AddressLead)(7,2,(int)Dpi::Key_MCT_AddressCollection))
        //  memory read 10
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_AddressBronze)(4,2,(int)Dpi::Key_MCT_AddressLead)(6,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_AddressBronze)(3,2,(int)Dpi::Key_MCT_AddressLead)(5,2,(int)Dpi::Key_MCT_AddressCollection)(7,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressBronze)(2,2,(int)Dpi::Key_MCT_AddressLead)(4,2,(int)Dpi::Key_MCT_AddressCollection)(6,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead)(3,2,(int)Dpi::Key_MCT_AddressCollection)(5,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressLead)(2,2,(int)Dpi::Key_MCT_AddressCollection)(4,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_AddressCollection)(3,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressCollection)(2,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (empty)
        //  memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(7,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(6,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(5,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(4,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(3,4,(int)Dpi::Key_MCT_DSTStartTime)(7,4,(int)Dpi::Key_MCT_DSTEndTime))
        //  memory read 30
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(2,4,(int)Dpi::Key_MCT_DSTStartTime)(6,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(1,4,(int)Dpi::Key_MCT_DSTStartTime)(5,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTStartTime)(4,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_DSTEndTime)(7,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_DSTEndTime)(6,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_DSTEndTime)(5,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTEndTime)(4,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        //  memory read 40
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_DemandInterval))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_DemandInterval)(7,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_DemandInterval)(6,1,(int)Dpi::Key_MCT_LoadProfileInterval)(7,1,(int)Dpi::Key_MCT_LoadProfileInterval2))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_DemandInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval)(6,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(7,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_DemandInterval)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(6,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(7,1,(int)Dpi::Key_MCT_PrecannedMeterNumber))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DemandInterval)(3,1,(int)Dpi::Key_MCT_LoadProfileInterval)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(5,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(6,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(7,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DemandInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval)(3,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(4,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(5,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(6,1,(int)Dpi::Key_MCT_PrecannedTableType))
        //  memory read 50
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(3,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(4,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(5,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(2,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(3,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(4,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(1,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(2,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(3,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(1,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(2,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(1,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 60
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_RelayATimer))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_RelayATimer)(7,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_RelayATimer)(6,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_RelayATimer)(5,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_RelayATimer)(4,1,(int)Dpi::Key_MCT_RelayBTimer))
        //  memory read 70
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_RelayATimer)(3,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_RelayATimer)(2,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_RelayATimer)(1,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_RelayBTimer)(7,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_DayTable)(7,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_DayTable)(6,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_DayTable)(5,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_DayTable)(4,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_DayTable)(3,7,(int)Dpi::Key_MCT_DaySchedule1))
        //  memory read 80
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule1)(7,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(6,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(5,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(4,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(3,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule2)(7,7,(int)Dpi::Key_MCT_DaySchedule3))
        //  memory read 90
        (tuple_list_of(6,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(5,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(4,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(3,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule3)(7,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(6,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(5,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(4,7,(int)Dpi::Key_MCT_DaySchedule4))
        //  memory read 100
        (tuple_list_of(3,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule4)(7,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DefaultTOURate))
        //  memory read 110
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 120
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        //  memory read 130
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        //  memory read 140
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        //  memory read 160
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (empty)
        //  memory read 170
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 180
        (empty)
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        //  memory read 190
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 200
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        //  memory read 210
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(7,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(6,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(5,4,(int)Dpi::Key_MCT_Holiday1))
        //  memory read 220
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(4,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday1)(7,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday1)(6,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday1)(5,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1)(4,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday2)(7,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday2)(6,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday2)(5,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2)(4,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday3))
        //  memory read 230
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 240
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  memory read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty);

    const test_Mct470Device dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_RANGES(results, expected);
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct470Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,5,(int)Dpi::Key_MCT_SSpec)(1,1,(int)Dpi::Key_MCT_SSpecRevision)(2,1,(int)Dpi::Key_MCT_Options)(3,1,(int)Dpi::Key_MCT_Configuration)(8,1,(int)Dpi::Key_MCT_EventFlagsMask1)(9,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,1,(int)Dpi::Key_MCT_Options)(2,1,(int)Dpi::Key_MCT_Configuration)(7,1,(int)Dpi::Key_MCT_EventFlagsMask1)(8,1,(int)Dpi::Key_MCT_EventFlagsMask2)(12,1,(int)Dpi::Key_MCT_AddressBronze))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Options)(1,1,(int)Dpi::Key_MCT_Configuration)(6,1,(int)Dpi::Key_MCT_EventFlagsMask1)(7,1,(int)Dpi::Key_MCT_EventFlagsMask2)(11,1,(int)Dpi::Key_MCT_AddressBronze)(12,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(5,1,(int)Dpi::Key_MCT_EventFlagsMask1)(6,1,(int)Dpi::Key_MCT_EventFlagsMask2)(10,1,(int)Dpi::Key_MCT_AddressBronze)(11,2,(int)Dpi::Key_MCT_AddressLead))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_EventFlagsMask1)(5,1,(int)Dpi::Key_MCT_EventFlagsMask2)(9,1,(int)Dpi::Key_MCT_AddressBronze)(10,2,(int)Dpi::Key_MCT_AddressLead)(12,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_EventFlagsMask1)(4,1,(int)Dpi::Key_MCT_EventFlagsMask2)(8,1,(int)Dpi::Key_MCT_AddressBronze)(9,2,(int)Dpi::Key_MCT_AddressLead)(11,2,(int)Dpi::Key_MCT_AddressCollection))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_EventFlagsMask1)(3,1,(int)Dpi::Key_MCT_EventFlagsMask2)(7,1,(int)Dpi::Key_MCT_AddressBronze)(8,2,(int)Dpi::Key_MCT_AddressLead)(10,2,(int)Dpi::Key_MCT_AddressCollection)(12,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(6,1,(int)Dpi::Key_MCT_AddressBronze)(7,2,(int)Dpi::Key_MCT_AddressLead)(9,2,(int)Dpi::Key_MCT_AddressCollection)(11,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask1)(1,1,(int)Dpi::Key_MCT_EventFlagsMask2)(5,1,(int)Dpi::Key_MCT_AddressBronze)(6,2,(int)Dpi::Key_MCT_AddressLead)(8,2,(int)Dpi::Key_MCT_AddressCollection)(10,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_EventFlagsMask2)(4,1,(int)Dpi::Key_MCT_AddressBronze)(5,2,(int)Dpi::Key_MCT_AddressLead)(7,2,(int)Dpi::Key_MCT_AddressCollection)(9,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        // memory read 10
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_AddressBronze)(4,2,(int)Dpi::Key_MCT_AddressLead)(6,2,(int)Dpi::Key_MCT_AddressCollection)(8,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_AddressBronze)(3,2,(int)Dpi::Key_MCT_AddressLead)(5,2,(int)Dpi::Key_MCT_AddressCollection)(7,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressBronze)(2,2,(int)Dpi::Key_MCT_AddressLead)(4,2,(int)Dpi::Key_MCT_AddressCollection)(6,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead)(3,2,(int)Dpi::Key_MCT_AddressCollection)(5,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressLead)(2,2,(int)Dpi::Key_MCT_AddressCollection)(4,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_AddressCollection)(3,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_AddressCollection)(2,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        (tuple_list_of(12,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        // memory read 20
        (tuple_list_of(11,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(12,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(10,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(11,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(9,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(10,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(8,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(9,4,(int)Dpi::Key_MCT_DSTStartTime))
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(8,4,(int)Dpi::Key_MCT_DSTStartTime)(12,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(7,4,(int)Dpi::Key_MCT_DSTStartTime)(11,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(6,4,(int)Dpi::Key_MCT_DSTStartTime)(10,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(5,4,(int)Dpi::Key_MCT_DSTStartTime)(9,4,(int)Dpi::Key_MCT_DSTEndTime))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(4,4,(int)Dpi::Key_MCT_DSTStartTime)(8,4,(int)Dpi::Key_MCT_DSTEndTime)(12,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(3,4,(int)Dpi::Key_MCT_DSTStartTime)(7,4,(int)Dpi::Key_MCT_DSTEndTime)(11,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        // memory read 30
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(2,4,(int)Dpi::Key_MCT_DSTStartTime)(6,4,(int)Dpi::Key_MCT_DSTEndTime)(10,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance)(1,4,(int)Dpi::Key_MCT_DSTStartTime)(5,4,(int)Dpi::Key_MCT_DSTEndTime)(9,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTStartTime)(4,4,(int)Dpi::Key_MCT_DSTEndTime)(8,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_DSTEndTime)(7,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_DSTEndTime)(6,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_DSTEndTime)(5,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_DSTEndTime)(4,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_TimeZoneOffset)(12,1,(int)Dpi::Key_MCT_DemandInterval))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_TimeZoneOffset)(11,1,(int)Dpi::Key_MCT_DemandInterval)(12,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        // memory read 40
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset)(10,1,(int)Dpi::Key_MCT_DemandInterval)(11,1,(int)Dpi::Key_MCT_LoadProfileInterval)(12,1,(int)Dpi::Key_MCT_LoadProfileInterval2))
        (tuple_list_of(9,1,(int)Dpi::Key_MCT_DemandInterval)(10,1,(int)Dpi::Key_MCT_LoadProfileInterval)(11,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(12,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval))
        (tuple_list_of(8,1,(int)Dpi::Key_MCT_DemandInterval)(9,1,(int)Dpi::Key_MCT_LoadProfileInterval)(10,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(11,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(12,1,(int)Dpi::Key_MCT_PrecannedMeterNumber))
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_DemandInterval)(8,1,(int)Dpi::Key_MCT_LoadProfileInterval)(9,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(10,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(11,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(12,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_DemandInterval)(7,1,(int)Dpi::Key_MCT_LoadProfileInterval)(8,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(9,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(10,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(11,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_DemandInterval)(6,1,(int)Dpi::Key_MCT_LoadProfileInterval)(7,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(8,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(9,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(10,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_DemandInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval)(6,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(7,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(8,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(9,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_DemandInterval)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(6,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(7,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(8,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DemandInterval)(3,1,(int)Dpi::Key_MCT_LoadProfileInterval)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(5,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(6,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(7,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DemandInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval)(3,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(4,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(5,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(6,1,(int)Dpi::Key_MCT_PrecannedTableType))
        // memory read 50
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(3,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(4,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(5,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(2,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(3,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(4,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(1,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(2,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(3,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(1,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(2,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(1,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (tuple_list_of(12,1,(int)Dpi::Key_MCT_RelayATimer))
        (tuple_list_of(11,1,(int)Dpi::Key_MCT_RelayATimer)(12,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(10,1,(int)Dpi::Key_MCT_RelayATimer)(11,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(9,1,(int)Dpi::Key_MCT_RelayATimer)(10,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(8,1,(int)Dpi::Key_MCT_RelayATimer)(9,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_RelayATimer)(8,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_RelayATimer)(7,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_RelayATimer)(6,1,(int)Dpi::Key_MCT_RelayBTimer))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_RelayATimer)(5,1,(int)Dpi::Key_MCT_RelayBTimer)(12,2,(int)Dpi::Key_MCT_DayTable))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_RelayATimer)(4,1,(int)Dpi::Key_MCT_RelayBTimer)(11,2,(int)Dpi::Key_MCT_DayTable))
        // memory read 70
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_RelayATimer)(3,1,(int)Dpi::Key_MCT_RelayBTimer)(10,2,(int)Dpi::Key_MCT_DayTable)(12,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_RelayATimer)(2,1,(int)Dpi::Key_MCT_RelayBTimer)(9,2,(int)Dpi::Key_MCT_DayTable)(11,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_RelayATimer)(1,1,(int)Dpi::Key_MCT_RelayBTimer)(8,2,(int)Dpi::Key_MCT_DayTable)(10,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_RelayBTimer)(7,2,(int)Dpi::Key_MCT_DayTable)(9,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_DayTable)(8,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_DayTable)(7,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_DayTable)(6,7,(int)Dpi::Key_MCT_DaySchedule1))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_DayTable)(5,7,(int)Dpi::Key_MCT_DaySchedule1)(12,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_DayTable)(4,7,(int)Dpi::Key_MCT_DaySchedule1)(11,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_DayTable)(3,7,(int)Dpi::Key_MCT_DaySchedule1)(10,7,(int)Dpi::Key_MCT_DaySchedule2))
        // memory read 80
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,7,(int)Dpi::Key_MCT_DaySchedule1)(9,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule1)(8,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule1)(7,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(6,7,(int)Dpi::Key_MCT_DaySchedule2))
        (tuple_list_of(5,7,(int)Dpi::Key_MCT_DaySchedule2)(12,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(4,7,(int)Dpi::Key_MCT_DaySchedule2)(11,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(3,7,(int)Dpi::Key_MCT_DaySchedule2)(10,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule2)(9,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule2)(8,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule2)(7,7,(int)Dpi::Key_MCT_DaySchedule3))
        // memory read 90
        (tuple_list_of(6,7,(int)Dpi::Key_MCT_DaySchedule3))
        (tuple_list_of(5,7,(int)Dpi::Key_MCT_DaySchedule3)(12,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(4,7,(int)Dpi::Key_MCT_DaySchedule3)(11,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(3,7,(int)Dpi::Key_MCT_DaySchedule3)(10,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule3)(9,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule3)(8,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule3)(7,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(6,7,(int)Dpi::Key_MCT_DaySchedule4))
        (tuple_list_of(5,7,(int)Dpi::Key_MCT_DaySchedule4)(12,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(4,7,(int)Dpi::Key_MCT_DaySchedule4)(11,1,(int)Dpi::Key_MCT_DefaultTOURate))
        // memory read 100
        (tuple_list_of(3,7,(int)Dpi::Key_MCT_DaySchedule4)(10,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(2,7,(int)Dpi::Key_MCT_DaySchedule4)(9,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(1,7,(int)Dpi::Key_MCT_DaySchedule4)(8,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(0,7,(int)Dpi::Key_MCT_DaySchedule4)(7,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(6,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_DefaultTOURate))
        // memory read 110
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 120
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(12,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        (tuple_list_of(11,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        (tuple_list_of(10,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(12,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(9,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(11,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(8,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(10,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(9,2,(int)Dpi::Key_MCT_LoadProfileKRatio1))
        // memory read 130
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(8,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(12,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(11,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(10,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(9,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(8,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        // memory read 140
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 150
        (tuple_list_of(12,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(11,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(10,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(12,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(9,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(11,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(8,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(10,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(9,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(8,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(12,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(11,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(10,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(9,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        // memory read 160
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(8,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio2)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2))
        (empty)
        // memory read 170
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(12,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(11,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(10,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(12,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(9,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(11,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        // memory read 180
        (tuple_list_of(8,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(10,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(9,2,(int)Dpi::Key_MCT_LoadProfileKRatio3))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(8,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(12,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(11,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(10,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(9,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(8,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        // memory read 190
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 200
        (empty)
        (empty)
        (tuple_list_of(12,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(11,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(10,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(12,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(9,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(11,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(8,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(10,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(7,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(9,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(8,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(12,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(5,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(7,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(11,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        // memory read 210
        (tuple_list_of(4,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(6,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(10,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(3,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(5,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(9,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4))
        (tuple_list_of(2,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(4,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(8,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(12,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(7,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(11,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(2,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(6,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(10,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(1,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(9,4,(int)Dpi::Key_MCT_Holiday1))
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_LoadProfileKRatio4)(4,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(8,4,(int)Dpi::Key_MCT_Holiday1)(12,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(7,4,(int)Dpi::Key_MCT_Holiday1)(11,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(6,4,(int)Dpi::Key_MCT_Holiday1)(10,4,(int)Dpi::Key_MCT_Holiday2))
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(5,4,(int)Dpi::Key_MCT_Holiday1)(9,4,(int)Dpi::Key_MCT_Holiday2))
        // memory read 220
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(4,4,(int)Dpi::Key_MCT_Holiday1)(8,4,(int)Dpi::Key_MCT_Holiday2)(12,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday1)(7,4,(int)Dpi::Key_MCT_Holiday2)(11,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday1)(6,4,(int)Dpi::Key_MCT_Holiday2)(10,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday1)(5,4,(int)Dpi::Key_MCT_Holiday2)(9,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1)(4,4,(int)Dpi::Key_MCT_Holiday2)(8,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday2)(7,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday2)(6,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday2)(5,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2)(4,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(3,4,(int)Dpi::Key_MCT_Holiday3))
        // memory read 230
        (tuple_list_of(2,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(1,4,(int)Dpi::Key_MCT_Holiday3))
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 240
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty);

    const test_Mct470Device dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_RANGES(results, expected);
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct470Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty).repeat(30-1, empty)
        //  function read 30
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_LoadProfileConfig)(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1)(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2)(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1)(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3)(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(1,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(2,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (empty)
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LLPChannel1Len)(2,1,(int)Dpi::Key_MCT_LLPChannel2Len))
        (empty)
        (empty)
        //  function read 40
        .repeat(130, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate)(2,1,(int)Dpi::Key_MCT_TouEnabled))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(76, empty);

    const test_Mct470Device dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_RANGES(results, expected);
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct470Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty).repeat(30-1, empty)
        //  function read 30
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_LoadProfileConfig)(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1)(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2)(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3)(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(6,1,(int)Dpi::Key_MCT_IEDLoadProfileInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1)(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2)(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3)(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(1,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(2,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (empty)
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LLPChannel1Len)(2,1,(int)Dpi::Key_MCT_LLPChannel2Len)(3,1,(int)Dpi::Key_MCT_LLPChannel3Len)(4,1,(int)Dpi::Key_MCT_LLPChannel4Len)(5,1,(int)Dpi::Key_MCT_LLPChannel4Len)(6,1,(int)Dpi::Key_MCT_LLPChannel4Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 40
        .repeat(110, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_LLPChannel1Len)(5,1,(int)Dpi::Key_MCT_LLPChannel2Len)(6,1,(int)Dpi::Key_MCT_LLPChannel3Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate)(2,1,(int)Dpi::Key_MCT_TouEnabled))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(76, empty);

    const test_Mct470Device dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_RANGES(results, expected);
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct470Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty).repeat(30-1, empty)
        //  function read 30
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_LoadProfileConfig)(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1)(1,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2)(2,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3)(3,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(4,1,(int)Dpi::Key_MCT_LoadProfileInterval)(5,1,(int)Dpi::Key_MCT_LoadProfileInterval2)(6,1,(int)Dpi::Key_MCT_IEDLoadProfileInterval))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig1)(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio1)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio1)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig2)(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio2)(8,2,(int)Dpi::Key_MCT_LoadProfileKRatio2))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig3)(1,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio3)(3,2,(int)Dpi::Key_MCT_LoadProfileKRatio3)(5,1,(int)Dpi::Key_MCT_LoadProfileChannelConfig4)(6,2,(int)Dpi::Key_MCT_LoadProfileMeterRatio4)(8,2,(int)Dpi::Key_MCT_LoadProfileKRatio4))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PrecannedTableReadInterval)(1,1,(int)Dpi::Key_MCT_PrecannedMeterNumber)(2,1,(int)Dpi::Key_MCT_PrecannedTableType))
        (empty)
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_LLPChannel1Len)(2,1,(int)Dpi::Key_MCT_LLPChannel2Len)(3,1,(int)Dpi::Key_MCT_LLPChannel3Len)(4,1,(int)Dpi::Key_MCT_LLPChannel4Len)(5,1,(int)Dpi::Key_MCT_LLPChannel4Len)(6,1,(int)Dpi::Key_MCT_LLPChannel4Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len)(8,1,(int)Dpi::Key_MCT_LLPChannel4Len)(9,1,(int)Dpi::Key_MCT_LLPChannel4Len)(10,1,(int)Dpi::Key_MCT_LLPChannel4Len)(11,1,(int)Dpi::Key_MCT_LLPChannel4Len)(12,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 40
        .repeat(110, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_LLPChannel1Len)(5,1,(int)Dpi::Key_MCT_LLPChannel2Len)(6,1,(int)Dpi::Key_MCT_LLPChannel3Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate)(2,1,(int)Dpi::Key_MCT_TouEnabled)(10,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(76, empty);

    const test_Mct470Device dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_RANGES(results, expected);
}

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

