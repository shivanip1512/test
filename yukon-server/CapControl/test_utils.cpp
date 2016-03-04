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

BOOST_AUTO_TEST_CASE( test_cap_control_utilities_deserialize_flags )
{
    // individual flags

    BOOST_CHECK_EQUAL(  false, deserializeFlag( "N" ) );
    BOOST_CHECK_EQUAL(  false, deserializeFlag( "n" ) );
    BOOST_CHECK_EQUAL(  false, deserializeFlag( "0" ) );

    BOOST_CHECK_EQUAL(   true, deserializeFlag( "Y" ) );
    BOOST_CHECK_EQUAL(   true, deserializeFlag( "y" ) );
    BOOST_CHECK_EQUAL(   true, deserializeFlag( "1" ) );

    BOOST_CHECK_EQUAL(  false, deserializeFlag( "A" ) );
    BOOST_CHECK_EQUAL(  false, deserializeFlag( "a" ) );
    BOOST_CHECK_EQUAL(  false, deserializeFlag( "2" ) );

    // flag strings

    const std::string flags( "AgNNnYy0013qQZ" );

    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags       ) );    // 'A' -- default index == 0
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    0 ) );    // 'A'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    1 ) );    // 'g'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    2 ) );    // 'N'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    3 ) );    // 'N'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    4 ) );    // 'n'
    BOOST_CHECK_EQUAL(   true, deserializeFlag( flags,    5 ) );    // 'Y'
    BOOST_CHECK_EQUAL(   true, deserializeFlag( flags,    6 ) );    // 'y'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    7 ) );    // '0'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,    8 ) );    // '0'
    BOOST_CHECK_EQUAL(   true, deserializeFlag( flags,    9 ) );    // '1'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   10 ) );    // '3'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   11 ) );    // 'q'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   12 ) );    // 'Q'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   13 ) );    // 'Z'
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   14 ) );    // out of bounds
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   15 ) );    // out of bounds
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags,   -1 ) );    // out of bounds
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags, -100 ) );    // out of bounds
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags, 1807 ) );    // out of bounds
    BOOST_CHECK_EQUAL(  false, deserializeFlag( flags, -999 ) );    // out of bounds
}

BOOST_AUTO_TEST_SUITE_END()

