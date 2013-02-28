#include <boost/test/unit_test.hpp>

#include "slctdev.h"
#include "devicetypes.h"

#include "boostutil.h"

#include <boost/assign/list_of.hpp>

#include <set>

BOOST_AUTO_TEST_SUITE( test_slctdev )

BOOST_AUTO_TEST_CASE(test_is_carrier_lp_device_type)
{
    const bool X = true, _ = false;

    const std::vector<bool> expected = boost::assign::list_of
        (_)(_)(X)(_)(X) (_)(_)(_)(_)(_)  //   0
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  10
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  20
        (X)(X)(X)(_)(_) (_)(_)(_)(_)(_)  //  30
        (X)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  40
        (X)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  50
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(X)  //  60
        (X)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  70
        (_)(_)(X)(X)(X) (X)(X)(X)(X)(X)  //  80
        (X)(X)(X)(X)(X) (X)(X)(_)(_)(_)  //  90
        .repeat(9900, _);

    std::vector<bool> results;

    for( int type = 0; type < 10000; ++type )
    {
        results.push_back(isCarrierLPDeviceType(type));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
}

BOOST_AUTO_TEST_SUITE_END()

