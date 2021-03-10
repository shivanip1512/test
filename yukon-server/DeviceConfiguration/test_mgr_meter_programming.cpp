#include <boost/test/unit_test.hpp>

#include "deviceconfig_test_helpers.h"

BOOST_AUTO_TEST_SUITE(test_mgr_meter_programming)

BOOST_AUTO_TEST_CASE(test_getProgram)
{
    Cti::Test::test_MeterProgrammingManager mgr;

	const auto buf = mgr.getProgram(Cti::Test::MeterProgramming_TestProgramGuid);

    BOOST_REQUIRE(buf);

    BOOST_CHECK_EQUAL(buf->size(), 1744);
}

BOOST_AUTO_TEST_SUITE_END()

