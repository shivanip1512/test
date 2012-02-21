#include <boost/test/auto_unit_test.hpp>

#include "calc.h"

BOOST_AUTO_TEST_SUITE( test_calc )

BOOST_AUTO_TEST_CASE(test_get_update_type)
{
    CtiCalc calc = CtiCalc(1, "On Timer", 10,  "y");
    BOOST_CHECK_EQUAL(calc.getUpdateType(), periodic);
}

BOOST_AUTO_TEST_SUITE_END()

