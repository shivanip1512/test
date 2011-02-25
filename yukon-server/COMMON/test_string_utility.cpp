#define BOOST_AUTO_TEST_MAIN "Test utility.h"

#include <boost/test/unit_test.hpp>
using boost::unit_test_framework::test_suite;

#include "string_utility.h"

BOOST_AUTO_TEST_CASE(test_filename_only)
{
    const char *test = "c:\\program files\\test.log";

    const char *expected = test + 17;

    BOOST_CHECK_EQUAL(expected, Cti::filename_only(test));
}

