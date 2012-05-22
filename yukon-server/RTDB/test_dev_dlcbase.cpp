#include <boost/test/unit_test.hpp>

#include "dev_dlcbase.h"
#include "dev_mct.h"

BOOST_AUTO_TEST_SUITE( test_dev_dlcbase )

using namespace std;
using Cti::Devices::DlcBaseDevice;
using Cti::Devices::MctDevice;

struct Test_CtiDeviceBase : CtiDeviceBase{
    long address;
    long getAddress() const                 { return address; }
    void setAddress(const long &address_)   { address = address_; }

    std::string name;
    std::string getName()  const    { return name;}
    void setName(const std::string name_) {name = name_;}
};



BOOST_AUTO_TEST_CASE(test_dlc_address_mismatch_no_match)
{
    Test_CtiDeviceBase device;
    device.setName("SomeName");
    device.setAddress(3L);   

    DSTRUCT dst;
    dst.Length = 5;
    dst.Address = 2L; 

    BOOST_CHECK(DlcBaseDevice::dlcAddressMismatch(dst, device));
}

BOOST_AUTO_TEST_CASE(test_dlc_address_mismatch_yes_match)
{
    Test_CtiDeviceBase device;
    device.setName("SomeName");
    device.setAddress(3L);   

    DSTRUCT dst;
    dst.Length = 5;
    dst.Address = 3L; 

    BOOST_CHECK( ! DlcBaseDevice::dlcAddressMismatch(dst, device));
}



BOOST_AUTO_TEST_CASE(test_dlc_address_mismatch_ignore_zero_length)
{
    Test_CtiDeviceBase device;
    device.setName("SomeName");
    device.setAddress(3L);   

    DSTRUCT dst;
    dst.Length = 0;
    dst.Address = 2L; 

    BOOST_CHECK( ! DlcBaseDevice::dlcAddressMismatch(dst, device));
}

BOOST_AUTO_TEST_CASE(test_dlc_address_mismatch_ignore_test_address)
{
    Test_CtiDeviceBase device;
    device.setName("SomeName");
    device.setAddress(MctDevice::TestAddress1);   

    DSTRUCT dst;
    dst.Length = 5;
    dst.Address = 2L; 

    BOOST_CHECK( ! DlcBaseDevice::dlcAddressMismatch(dst, device));

    device.setAddress(MctDevice::TestAddress2);
    BOOST_CHECK( ! DlcBaseDevice::dlcAddressMismatch(dst, device));
}

BOOST_AUTO_TEST_CASE(test_dlc_address_mismatch_only_lower_13_bits)
{
    Test_CtiDeviceBase device;
    device.setName("SomeName");
    device.setAddress(0xFFFF);

    DSTRUCT dst;
    dst.Length = 5;
    dst.Address = 0x1FFF;

    BOOST_CHECK( ! DlcBaseDevice::dlcAddressMismatch(dst, device));
}

BOOST_AUTO_TEST_SUITE_END()
