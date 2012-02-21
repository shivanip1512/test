#include <boost/test/unit_test.hpp>

#include "devicetypes.h"
#include "dsm2.h"
#include "dev_single.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_dev_addr_global )

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


BOOST_AUTO_TEST_CASE(test_is_device_address_global)
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

BOOST_AUTO_TEST_SUITE_END()
