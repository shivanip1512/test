#include <boost/test/unit_test.hpp>

#include "slctdev.h"
#include "devicetypes.h"

#include "boostutil.h"

#include <set>

BOOST_AUTO_TEST_SUITE( test_slctdev )

struct test_CtiDeviceBase : public CtiDeviceBase
{
    using CtiDeviceBase::setType;
};


BOOST_AUTO_TEST_CASE(test_is_carrier_lp_device)
{
    CtiDeviceSPtr dev(new test_CtiDeviceBase);

    int types[] = {
        TYPELMT2,
        TYPEDCT501,
        TYPEMCT240,
        TYPEMCT242,
        TYPEMCT248,
        TYPEMCT250,
        TYPEMCT260,
        TYPEMCT310IL,
        TYPEMCT318L,
        TYPEMCT410CL,
        TYPEMCT410FL,
        TYPEMCT410GL,
        TYPEMCT410IL,
        TYPEMCT420CL,
        TYPEMCT420CD,
        TYPEMCT420FL,
        TYPEMCT420FD,
        TYPEMCT430A,
        TYPEMCT430A3,
        TYPEMCT430S4,
        TYPEMCT430SL,
        TYPEMCT470 };

    size_t type_count = sizeof(types) / sizeof(types[0]);

    std::set<int> type_set;

    type_set.insert(types, types + type_count);

    for( int i = 0; i < 10000; ++i )
    {
        dev->setType(i);

        BOOST_CHECK_INDEXED_EQUAL(i, isCarrierLPDevice(dev), type_set.find(i) != type_set.end());
    }
}

BOOST_AUTO_TEST_SUITE_END()

