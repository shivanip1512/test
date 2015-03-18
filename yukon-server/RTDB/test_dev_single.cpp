#include <boost/test/unit_test.hpp>

#include "devicetypes.h"
#include "dsm2.h"
#include "dev_single.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_dev_single )

struct test_CtiDeviceSingle : CtiDeviceSingle
{
    long address;  // this is usually defined in a child of CtiDeviceSingle...

    using CtiDeviceSingle::isDeviceAddressGlobal;
    using CtiDeviceSingle::intervalsPerDay;

    LONG getAddress() const                 { return address; }
    void setAddress(const LONG &address_)   { address = address_; }

    long getScanRate(int rate) const
    {
        switch( rate )
        {
            case ScanRateAccum:         return 3600;
            case ScanRateIntegrity:     return 300;
            case ScanRateGeneral:       return 15;
            case ScanRateLoadProfile:   return 3600 * 3;  //  3 hours
            default:                    return 0;
        }
    }
};


BOOST_AUTO_TEST_CASE(test_is_device_address_global)
{
    const int NONGLOBAL_ADDRESS = 100;

    {
        test_CtiDeviceSingle test;
        test.setType(TYPE_LCU415);

        test.address = CCUGLOBAL;
        BOOST_CHECK( test.isDeviceAddressGlobal() );

        test.address = RTUGLOBAL;
        BOOST_CHECK( test.isDeviceAddressGlobal() );

        test.address = NONGLOBAL_ADDRESS;
        BOOST_CHECK( ! test.isDeviceAddressGlobal() );
    }

    {
        test_CtiDeviceSingle test;
        test.setType(TYPE_ALPHA_A3);

        test.address = NONGLOBAL_ADDRESS;
        BOOST_CHECK( ! test.isDeviceAddressGlobal() );

        test.address = RTUGLOBAL;
        BOOST_CHECK( ! test.isDeviceAddressGlobal() );

        test.address = CCUGLOBAL;
        BOOST_CHECK( ! test.isDeviceAddressGlobal() );
    }
}

BOOST_AUTO_TEST_CASE(test_getTardyInterval)
{
    test_CtiDeviceSingle test;

    BOOST_CHECK_EQUAL( 5400, test.getTardyInterval(ScanRateAccum));
    BOOST_CHECK_EQUAL(  601, test.getTardyInterval(ScanRateIntegrity));
    BOOST_CHECK_EQUAL(   60, test.getTardyInterval(ScanRateGeneral));
    BOOST_CHECK_EQUAL( 7200, test.getTardyInterval(ScanRateLoadProfile));
    BOOST_CHECK_EQUAL(   60, test.getTardyInterval(17));
}

BOOST_AUTO_TEST_CASE(test_intervalsPerDay)
{
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(    0U),      0);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(    1U),  86400);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(    2U),  43200);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(    4U),  21600);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(    8U),  10800);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(   15U),   5760);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(   30U),   2880);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(   60U),   1440);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(   300U),   288);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(   600U),   144);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(   900U),    96);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(  1800U),    48);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(  3600U),    24);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(  7200U),    12);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay( 14400U),     6);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay( 28800U),     3);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay( 86400U),     1);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay( 86401U),     0);
    BOOST_CHECK_EQUAL(test_CtiDeviceSingle::intervalsPerDay(172800U),     0);
}


BOOST_AUTO_TEST_SUITE_END()
