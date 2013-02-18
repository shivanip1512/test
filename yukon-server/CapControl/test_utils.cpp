#include <boost/test/unit_test.hpp>

#include "ccutil.h"

using namespace Cti::CapControl;


BOOST_AUTO_TEST_SUITE( test_utility_functions )


BOOST_AUTO_TEST_CASE( test_cap_control_utilities_power_factor )
{
    // no vars or watts == unity (1.0) power factor
    BOOST_CHECK_CLOSE( 1.0, calculatePowerFactor( 0.0, 0.0 ), 1e-5 );

    // no vars == unity (1.0) power factor - uses absolute value for watts
    BOOST_CHECK_CLOSE( 1.0, calculatePowerFactor( 0.0, 1000.0 ), 1e-5 );
    BOOST_CHECK_CLOSE( 1.0, calculatePowerFactor( 0.0, -1000.0 ), 1e-5 );

    // watts == vars --> pf == 1/sqrt(2) : if vars < 0 then 2 - 1/sqrt(2)
    BOOST_CHECK_CLOSE( 0.7071067811, calculatePowerFactor( 1000.0, 1000.0 ), 1e-5 );
    BOOST_CHECK_CLOSE( 0.7071067811, calculatePowerFactor( 1000.0, -1000.0 ), 1e-5 );
    BOOST_CHECK_CLOSE( 1.2928932188, calculatePowerFactor( -1000.0, 1000.0 ), 1e-5 );
    BOOST_CHECK_CLOSE( 1.2928932188, calculatePowerFactor( -1000.0, -1000.0 ), 1e-5 );

    // watts != vars
    BOOST_CHECK_CLOSE( 0.8, calculatePowerFactor( 3000.0, 4000.0 ), 1e-5 );
    BOOST_CHECK_CLOSE( 1.2, calculatePowerFactor( -3000.0, 4000.0 ), 1e-5 );

    BOOST_CHECK_CLOSE( 0.6, calculatePowerFactor( 4000.0, 3000.0 ), 1e-5 );
    BOOST_CHECK_CLOSE( 1.4, calculatePowerFactor( -4000.0, 3000.0 ), 1e-5 );
}


BOOST_AUTO_TEST_SUITE_END()

