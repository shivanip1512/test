/*
 * test isDeviceAddressGlobal()
 *
 */

#include <boost/test/unit_test.hpp>

#define _WIN32_WINNT 0x0400

#include "devicetypes.h"
#include "dsm2.h"
#include "dev_single.h"

#define BOOST_AUTO_TEST_MAIN "Testing isDeviceAddressGlobal"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

using namespace std;

#define NONGLOBAL_ADDRESS   100

class Test_DevAddressGlobal : public CtiDeviceSingle
{
private:
    typedef CtiDeviceSingle Inherited;

    LONG    address_;       // this is usually defined in a child of CtiDeviceSingle...

public:

    Test_DevAddressGlobal(const int &type, const LONG &address) : address_(address)
    {
        setType(type);
    }

    LONG getAddress() const                 { return (address_); }
    void setAddress(const LONG &address)    { address_ = address; }

    bool isDeviceAddressGlobal()
    {
        return ( Inherited::isDeviceAddressGlobal() );
    }
};


BOOST_AUTO_UNIT_TEST(test_is_device_address_global)
{
    Test_DevAddressGlobal   test(TYPE_LCU415, CCUGLOBAL); 


    BOOST_CHECK( test.isDeviceAddressGlobal() );

    test.setAddress( RTUGLOBAL );
    BOOST_CHECK( test.isDeviceAddressGlobal() );

    test.setAddress( NONGLOBAL_ADDRESS );
    BOOST_CHECK( ! test.isDeviceAddressGlobal() );


    test.setType(TYPE_ALPHA_A3);
    BOOST_CHECK( ! test.isDeviceAddressGlobal() );

    test.setAddress( RTUGLOBAL );
    BOOST_CHECK( ! test.isDeviceAddressGlobal() );

    test.setAddress( CCUGLOBAL );
    BOOST_CHECK( ! test.isDeviceAddressGlobal() );
}

