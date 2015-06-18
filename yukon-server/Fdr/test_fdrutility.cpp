#include <boost/test/unit_test.hpp>

#include "fdrutility.h"
#include "boost_test_helpers.h"


BOOST_AUTO_TEST_SUITE( test_fdrutility )

BOOST_AUTO_TEST_CASE( test_fdrutility_translation_core_behavior )
{
    const std::string input
        = "Device:Point Container;Point:Rotation Threshold;Destination/Source:127.0.0.1;POINTTYPE:Analog;";

    Cti::Fdr::Translation translation( input );

    // validation

    BOOST_REQUIRE(      translation[ "Device" ]  );
    BOOST_CHECK_EQUAL( *translation[ "Device" ], "Point Container" );

    BOOST_REQUIRE(      translation[ "Point" ]  );
    BOOST_CHECK_EQUAL( *translation[ "Point" ], "Rotation Threshold" );

    BOOST_REQUIRE(      translation[ "Destination/Source" ]  );
    BOOST_CHECK_EQUAL( *translation[ "Destination/Source" ], "127.0.0.1" );

    BOOST_REQUIRE(      translation[ "POINTTYPE" ]  );
    BOOST_CHECK_EQUAL( *translation[ "POINTTYPE" ], "Analog" );

    // keys are case-insensitive

    BOOST_REQUIRE(      translation[ "PointType" ]  );
    BOOST_CHECK_EQUAL( *translation[ "PointType" ], "Analog" );

    // missing things

    BOOST_REQUIRE(    ! translation[ "GypsyDanger" ]  );
}

BOOST_AUTO_TEST_SUITE_END()
