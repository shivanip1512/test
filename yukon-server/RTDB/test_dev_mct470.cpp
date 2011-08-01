#include "dev_mct470.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

using Cti::Devices::Mct470Device;
using Cti::Protocols::EmetconProtocol;

using std::list;

struct test_Mct470Device : Mct470Device
{
    typedef Mct470Device::point_info point_info;

    using MctDevice::getOperation;

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
    BOOST_CHECK_EQUAL(0x1b, test_Mct470Device::computeResolutionByte(0.1,  1.0, 1.0));

    BOOST_CHECK_EQUAL(0x12, test_Mct470Device::computeResolutionByte(1.0, 10.0, 1.0));

    BOOST_CHECK_EQUAL(0x22, test_Mct470Device::computeResolutionByte(1.0, 10.0, 0.1));
}


struct getOperation_helper
{
    test_Mct470Device mct;
    BSTRUCT BSt;
};

BOOST_FIXTURE_TEST_SUITE(test_getOperation, getOperation_helper)

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

BOOST_AUTO_TEST_SUITE_END()
