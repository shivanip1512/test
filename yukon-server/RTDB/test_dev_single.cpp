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

BOOST_AUTO_TEST_CASE(test_getTardyTime)
{
    test_CtiDeviceSingle test;

    BOOST_CHECK_EQUAL( 5400, test.getTardyTime(ScanRateAccum));
    BOOST_CHECK_EQUAL(  601, test.getTardyTime(ScanRateIntegrity));
    BOOST_CHECK_EQUAL(   60, test.getTardyTime(ScanRateGeneral));
    BOOST_CHECK_EQUAL( 7200, test.getTardyTime(ScanRateLoadProfile));
    BOOST_CHECK_EQUAL(   60, test.getTardyTime(17));
}

BOOST_AUTO_TEST_SUITE_END()
