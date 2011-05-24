#define BOOST_AUTO_TEST_MAIN "Test compiler behaviors"

#include <boost/test/unit_test.hpp>
using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_CASE(test_modulus_sign)
{
    BOOST_CHECK_EQUAL( 12 %  10,  2);
    BOOST_CHECK_EQUAL( 12 % -10,  2);
    BOOST_CHECK_EQUAL(-12 %  10, -2);
    BOOST_CHECK_EQUAL(-12 % -10, -2);
}
