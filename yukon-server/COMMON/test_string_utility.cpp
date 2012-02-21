#include <boost/test/unit_test.hpp>

#include "string_utility.h"

BOOST_AUTO_TEST_SUITE( test_string_utility )

BOOST_AUTO_TEST_CASE(test_filename_only)
{
    const char *test = "c:\\program files\\test.log";

    const char *expected = test + 17;

    BOOST_CHECK_EQUAL(expected, Cti::filename_only(test));
}

BOOST_AUTO_TEST_SUITE_END()
