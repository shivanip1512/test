#include "dev_mct470.h"

#define BOOST_TEST_MAIN "Test dev_mct470"
#include <boost/test/unit_test.hpp>

#include <limits>

using boost::unit_test_framework::test_suite;

class test_CtiDeviceMCT470 : public CtiDeviceMCT470
{
public:

    typedef CtiDeviceMCT470::point_info point_info;

    void test_extractDynamicPaoInfo(const INMESS &InMessage)
    {
        extractDynamicPaoInfo(InMessage);
    };

    unsigned long test_convertTimestamp(const unsigned long timestamp, const CtiDate &current_date) const
    {
        return convertTimestamp(timestamp, current_date);
    }
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
    //  To adjust the time back, we have to play a trick with the underlying seconds.

    //  ::asGMT adds local_offset again to give GMT wall-clock time, which is not
    //    what we want.  However, we can use that to our advantage:
    //        local_offset = local_time.asGMT()           - local_time
    //        local_offset = local_time + local_offset    - local_time
    //        local_offset =              local_offset
    const int local_offset = local_time.asGMT().seconds() - local_time.seconds();

    //  GMT = local_time - local_offset
    return local_time.seconds() - local_offset;
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_convertTimestamp)
{
    test_CtiDeviceMCT470 dev;

    utc34_checker tc[25] = {{0x000000, {2009,  1}, {2009,  1,  1,  0,  0}},
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

    c = tc[ 0];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 1];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 2];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 3];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 4];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 5];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 6];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 7];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 8];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[ 9];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[10];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[11];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[12];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[13];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[14];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[15];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[16];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[17];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[18];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[19];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[20];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[21];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[22];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[23];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
    c = tc[24];  BOOST_CHECK_EQUAL(dev.test_convertTimestamp(c.raw_value, build_base_date(c.base_date)), build_gmt_seconds(c.expected_time));
}


BOOST_AUTO_TEST_CASE(test_dev_mct470_decodeGetValueIED)
{
    using Cti::Protocol::Emetcon;

    test_CtiDeviceMCT470 dev;

    INMESS im;

    list<OUTMESS *> om_list;
    list<CtiMessage *> ret_list;
    list<CtiMessage *> vg_list;

    //  set up the ied demand inmessage
    {
        strcpy(im.Return.CommandStr, "getvalue ied demand");
        im.Return.ProtocolInfo.Emetcon.IO = Emetcon::IO_Function_Read;
        im.Sequence = Emetcon::GetValue_IEDDemand;

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
        meter_config.Return.ProtocolInfo.Emetcon.IO = Cti::Protocol::Emetcon::IO_Read;

        dev.test_extractDynamicPaoInfo(meter_config);
    }

    //  to finish out this unit test, we will need to override getDevicePointOffsetTypeEqual()
    //    to report back voltage points for the decode

}
