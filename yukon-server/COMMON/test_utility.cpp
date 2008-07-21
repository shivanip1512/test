#define BOOST_AUTO_TEST_MAIN "Test utility.h"

#include "yukon.h"
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/unit_test.hpp>
using boost::unit_test_framework::test_suite;

#include <vector>
#include <map>
#include "utility.h"
#include "dsm2.h"
#include "queues.h"

struct test_wrapper
{
    static int counter;  //  keeps a counter instance to track construction and destruction

    test_wrapper()  { counter++; };
    ~test_wrapper() { counter--; };
};

int test_wrapper::counter;

BOOST_AUTO_UNIT_TEST(test_delete_container)
{
    std::vector<test_wrapper *> test_vector;
    const int max_size = 4;
    int i;

    test_wrapper::counter = 0;

    BOOST_CHECK_EQUAL(test_vector.size(), test_wrapper::counter);

    for( i = max_size; i; i-- )
    {
        test_vector.push_back(new test_wrapper);
        BOOST_CHECK_EQUAL(test_vector.size(), test_wrapper::counter);
    }

    delete_container(test_vector);

    BOOST_CHECK_EQUAL(test_vector.size(), max_size);  //  verify the elements are still there...
    BOOST_CHECK_EQUAL(test_wrapper::counter, 0);      //  ... but that they've all been deleted

    std::vector<test_wrapper *>::iterator itr = test_vector.begin();

    //  verify the elements have been zeroed out
    for( i = max_size; i; i-- )
    {
        BOOST_CHECK_EQUAL(*(itr++), (void *)0);
    }
}


BOOST_AUTO_UNIT_TEST(test_delete_assoc_container)
{
    std::map<int, test_wrapper *> test_map;
    const int max_size = 4;
    int i;

    test_wrapper::counter = 0;

    BOOST_CHECK_EQUAL(test_map.size(), test_wrapper::counter);

    for( i = max_size; i; i-- )
    {
        test_map.insert(std::make_pair(i, new test_wrapper));
        BOOST_CHECK_EQUAL(test_map.size(), test_wrapper::counter);
    }

    delete_assoc_container(test_map);

    BOOST_CHECK_EQUAL(test_map.size(), max_size);  //  verify the elements are still there...
    BOOST_CHECK_EQUAL(test_wrapper::counter, 0);   //  ... but that they've all been deleted

    std::map<int, test_wrapper *>::iterator itr = test_map.begin();

    //  verify the elements have been zeroed out
    for( i = max_size; i; i-- )
    {
        BOOST_CHECK_EQUAL((itr++)->second, (void *)0);
    }
}


BOOST_AUTO_UNIT_TEST(test_stringCompareIgnoreCase)
{
    BOOST_CHECK(!stringCompareIgnoreCase("cat", "cat"));
    BOOST_CHECK(!stringCompareIgnoreCase("Cat", "cat"));
    BOOST_CHECK(!stringCompareIgnoreCase("cAt", "cat"));
    BOOST_CHECK(!stringCompareIgnoreCase("CAt", "cat"));
    BOOST_CHECK(!stringCompareIgnoreCase("caT", "cat"));
    BOOST_CHECK(!stringCompareIgnoreCase("CaT", "cat"));
    BOOST_CHECK(!stringCompareIgnoreCase("cAT", "cat"));

    BOOST_CHECK( stringCompareIgnoreCase("CAT", "catt"));
    BOOST_CHECK( stringCompareIgnoreCase("cat", "ca"));

    BOOST_CHECK( stringCompareIgnoreCase("", "CAT"));
    BOOST_CHECK( stringCompareIgnoreCase("CAT", ""));
}

BOOST_AUTO_UNIT_TEST(test_stringContainsIgnoreCase)
{
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("dog",      "cat"), 0);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("doggie",   "cat"), 0);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("do",       "cat"), 0);

    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("catapult",  "cat"), 1);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("CAtapult",  "cat"), 1);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("caTApult",  "cat"), 1);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("caTApult",  "tap"), 1);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("catApult",  "tap"), 1);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("cataPUlt",  "tap"), 1);
    BOOST_CHECK_EQUAL(stringContainsIgnoreCase("newspaper", "paper"), 1);
}


BOOST_AUTO_UNIT_TEST(test_in_place_trim)
{
    string test;

    test = "   I am a space-padded string.  ";

    in_place_trim(test);

    BOOST_CHECK_EQUAL(test, "I am a space-padded string.");

    test = "I am an unpadded string.";

    in_place_trim(test);

    BOOST_CHECK_EQUAL(test, "I am an unpadded string.");

    test = "I am a left-justified string.       ";

    in_place_trim(test);

    BOOST_CHECK_EQUAL(test, "I am a left-justified string.");

    test = "        I am a right-justified string.";

    in_place_trim(test);

    BOOST_CHECK_EQUAL(test, "I am a right-justified string.");
}

BOOST_AUTO_UNIT_TEST(test_find_expired_om)
{
    QUEUEENT ent;
    PQUEUEENT queEnt = &ent;
    OUTMESS outMessage;
    CtiTime futureTime, historicalTime, nowTime;

    futureTime = nowTime;
    historicalTime = nowTime;
    futureTime.addSeconds(1);
    historicalTime.addSeconds(-1);

    queEnt->Data = (void *)&outMessage;
    outMessage.ExpirationTime = 0;
    BOOST_CHECK(!findExpiredOutMessage((void *)&nowTime, queEnt));
    BOOST_CHECK(!findExpiredOutMessage((void *)&futureTime, queEnt));
    BOOST_CHECK(!findExpiredOutMessage((void *)&historicalTime, queEnt));

    outMessage.ExpirationTime = nowTime.seconds();
    BOOST_CHECK(!findExpiredOutMessage((void *)&nowTime, queEnt));
    //Future Time is greater than expiration, we are expired!
    BOOST_CHECK(findExpiredOutMessage((void *)&futureTime, queEnt));
    BOOST_CHECK(!findExpiredOutMessage((void *)&historicalTime, queEnt));
}
