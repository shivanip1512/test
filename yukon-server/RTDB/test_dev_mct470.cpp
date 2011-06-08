#include "dev_mct470.h"

#define BOOST_TEST_MAIN "Test dev_mct470"
#include <boost/test/unit_test.hpp>

#include <limits>

using boost::unit_test_framework::test_suite;

using Cti::Devices::Mct470Device;
using std::list;

struct test_Mct470Device : Mct470Device
{
    typedef Mct470Device::point_info point_info;

    using Mct470Device::extractDynamicPaoInfo;
    using Mct470Device::convertTimestamp;
    using Mct470Device::computeResolutionByte;
    using Mct470Device::ResultDecode;
};


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

    utc34_checker tc[] = {{0x000000, {2009,  1}, {2009,  1,  1,  0,  0}},
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

    utc34_checker c;

    const size_t elements = sizeof(tc) / sizeof(tc[0]);

    for( size_t i = 0; i < elements; ++i )
    {
        c = tc[i];

        BOOST_CHECK_INDEXED_EQUAL(i, dev.convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    }
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2010)
{
    test_Mct470Device dev;

    utc34_checker tc[] = {{0x000000, {2010,  1}, {2011,  1,  1,  0,  0}},
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

    utc34_checker c;

    const size_t elements = sizeof(tc) / sizeof(tc[0]);

    for( size_t i = 0; i < elements; ++i )
    {
        c = tc[i];

        BOOST_CHECK_INDEXED_EQUAL(i, dev.convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    }
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2011)
{
    test_Mct470Device dev;

    utc34_checker tc[] = {{0x000000, {2011,  1}, {2011,  1,  1,  0,  0}},
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

    utc34_checker c;

    const size_t elements = sizeof(tc) / sizeof(tc[0]);

    for( size_t i = 0; i < elements; ++i )
    {
        c = tc[i];

        BOOST_CHECK_INDEXED_EQUAL(i, dev.convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    }
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp_in_2012)
{
    test_Mct470Device dev;

    utc34_checker tc[] = {{0x000000, {2012,  1}, {2013,  1,  1,  0,  0}},
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

    utc34_checker c;

    const size_t elements = sizeof(tc) / sizeof(tc[0]);

    for( size_t i = 0; i < elements; ++i )
    {
        c = tc[i];

        BOOST_CHECK_INDEXED_EQUAL(i, dev.convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    }
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_decodeGetValueIED)
{
    using Cti::Protocols::EmetconProtocol;

    test_Mct470Device dev;

    INMESS im;

    list<OUTMESS *> om_list;
    list<CtiMessage *> ret_list;
    list<CtiMessage *> vg_list;

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
    using Cti::Protocols::EmetconProtocol;
    test_Mct470Device dev;

    double lpResolution = 0.1;
    double peakKwResolution = 1.0;
    double lastIntervalDemandResolution = 1.0;
    unsigned char resultByte = dev.computeResolutionByte(lpResolution, peakKwResolution, lastIntervalDemandResolution);
    BOOST_CHECK_EQUAL(0x1b, resultByte);
    std::cout << (int)resultByte << std::endl;

    lpResolution = 1.0;
    peakKwResolution = 10.0;
    lastIntervalDemandResolution = 1.0;
    resultByte = dev.computeResolutionByte(lpResolution, peakKwResolution, lastIntervalDemandResolution);
    BOOST_CHECK_EQUAL(0x12, resultByte);
    std::cout << (int)resultByte << std::endl;

    lpResolution = 1.0;
    peakKwResolution = 10.0;
    lastIntervalDemandResolution = 0.1;
    resultByte = dev.computeResolutionByte(lpResolution, peakKwResolution, lastIntervalDemandResolution);
    BOOST_CHECK_EQUAL(0x22, resultByte);
    std::cout << (int)resultByte << std::endl;
}
