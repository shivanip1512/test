#include <boost/test/unit_test.hpp>

#include "hashkey.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_hash )

BOOST_AUTO_TEST_CASE(test_hash_integer_operators)
{
    CtiHashKey aHashKey = CtiHashKey(1);
    CtiHashKey bHashKey = CtiHashKey(2);
    CtiHashKey cHashKey = CtiHashKey(3);
    CtiHashKey dHashKey = CtiHashKey(2);

    BOOST_CHECK(bHashKey == dHashKey);
    BOOST_CHECK(!(cHashKey == dHashKey));
    BOOST_CHECK(aHashKey < bHashKey);
    BOOST_CHECK(bHashKey < cHashKey);
    BOOST_CHECK(!(bHashKey < aHashKey));
}

BOOST_AUTO_TEST_CASE(test_hash_string_operators)
{
    CtiHashKey aHashKey = CtiHashKey("abc");
    CtiHashKey bHashKey = CtiHashKey("123");
    CtiHashKey cHashKey = CtiHashKey("abc123");
    CtiHashKey dHashKey = CtiHashKey("123");

    BOOST_CHECK(bHashKey == dHashKey);
    BOOST_CHECK(!(cHashKey == dHashKey));
    BOOST_CHECK(!(aHashKey == dHashKey));
    BOOST_CHECK(!(aHashKey == bHashKey));

    dHashKey.setID(aHashKey.getID());
    BOOST_CHECK(!(aHashKey == dHashKey));

    bHashKey.setID(1);
    cHashKey.setID(2);
    BOOST_CHECK(bHashKey < cHashKey);
}

BOOST_AUTO_TEST_SUITE_END()
