#include <boost/test/unit_test.hpp>

#include <vector>
#include <map>
#include <strstream>
#include "utility.h"
#include "ctidate.h"
#include "dsm2.h"
#include "queues.h"
#include "devicetypes.h"

BOOST_AUTO_TEST_SUITE( test_utility )

struct instance_counter
{
    static int counter;  //  keeps a counter instance to track construction and destruction

    instance_counter()  { counter++; };
    ~instance_counter() { counter--; };
};

int instance_counter::counter;

BOOST_AUTO_TEST_CASE(test_delete_container)
{
    std::vector<instance_counter *> test_vector;
    const int max_size = 4;
    int i;

    instance_counter::counter = 0;

    BOOST_CHECK_EQUAL(test_vector.size(), instance_counter::counter);

    for( i = max_size; i; i-- )
    {
        test_vector.push_back(new instance_counter);
        BOOST_CHECK_EQUAL(test_vector.size(), instance_counter::counter);
    }

    delete_container(test_vector);

    BOOST_CHECK_EQUAL(test_vector.size(), max_size);  //  verify the elements are still there...
    BOOST_CHECK_EQUAL(instance_counter::counter, 0);      //  ... but that they've all been deleted

    std::vector<instance_counter *>::iterator itr = test_vector.begin();

    //  verify the elements have been zeroed out
    for( i = max_size; i; i-- )
    {
        BOOST_CHECK_EQUAL(*(itr++), (void *)0);
    }
}


BOOST_AUTO_TEST_CASE(test_delete_assoc_container)
{
    std::map<int, instance_counter *> test_map;
    const int max_size = 4;
    int i;

    instance_counter::counter = 0;

    BOOST_CHECK_EQUAL(test_map.size(), instance_counter::counter);

    for( i = max_size; i; i-- )
    {
        test_map.insert(std::make_pair(i, new instance_counter));
        BOOST_CHECK_EQUAL(test_map.size(), instance_counter::counter);
    }

    delete_assoc_container(test_map);

    BOOST_CHECK_EQUAL(test_map.size(), max_size);  //  verify the elements are still there...
    BOOST_CHECK_EQUAL(instance_counter::counter, 0);   //  ... but that they've all been deleted

    std::map<int, instance_counter *>::iterator itr = test_map.begin();

    //  verify the elements have been zeroed out
    for( i = max_size; i; i-- )
    {
        BOOST_CHECK_EQUAL((itr++)->second, (void *)0);
    }
}


BOOST_AUTO_TEST_CASE(test_ciStringEqual)
{
    BOOST_CHECK(ciStringEqual("cat", "cat"));
    BOOST_CHECK(ciStringEqual("Cat", "cat"));
    BOOST_CHECK(ciStringEqual("cAt", "cat"));
    BOOST_CHECK(ciStringEqual("CAt", "cat"));
    BOOST_CHECK(ciStringEqual("caT", "cat"));
    BOOST_CHECK(ciStringEqual("CaT", "cat"));
    BOOST_CHECK(ciStringEqual("cAT", "cat"));

    BOOST_CHECK( !ciStringEqual("CAT", "catt"));
    BOOST_CHECK( !ciStringEqual("cat", "ca"));

    BOOST_CHECK( !ciStringEqual("", "CAT"));
    BOOST_CHECK( !ciStringEqual("CAT", ""));
}

BOOST_AUTO_TEST_CASE(test_StringEqual)
{
    BOOST_CHECK(StringEqual("cat", "cat"));
    BOOST_CHECK( !StringEqual("Cat", "cat"));
    BOOST_CHECK( !StringEqual("cAt", "cat"));
    BOOST_CHECK( !StringEqual("CAt", "cat"));
    BOOST_CHECK( !StringEqual("caT", "cat"));
    BOOST_CHECK( !StringEqual("CaT", "cat"));
    BOOST_CHECK( !StringEqual("cAT", "cat"));

    BOOST_CHECK( !StringEqual("CAT", "catt"));
    BOOST_CHECK( !StringEqual("cat", "ca"));

    BOOST_CHECK( !StringEqual("", "CAT"));
    BOOST_CHECK( !StringEqual("CAT", ""));
}

BOOST_AUTO_TEST_CASE(test_stringContainsIgnoreCase)
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


BOOST_AUTO_TEST_CASE(test_in_place_trim)
{
    std::string test;

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

BOOST_AUTO_TEST_CASE(test_find_expired_om)
{
    OUTMESS outMessage;
    CtiTime futureTime, historicalTime, nowTime;

    futureTime = nowTime;
    historicalTime = nowTime;
    futureTime.addSeconds(1);
    historicalTime.addSeconds(-1);

    outMessage.ExpirationTime = 0;
    BOOST_CHECK(!findExpiredOutMessage((void *)&nowTime, &outMessage));
    BOOST_CHECK(!findExpiredOutMessage((void *)&futureTime, &outMessage));
    BOOST_CHECK(!findExpiredOutMessage((void *)&historicalTime, &outMessage));

    outMessage.ExpirationTime = nowTime.seconds();
    BOOST_CHECK(!findExpiredOutMessage((void *)&nowTime, &outMessage));
    //Future Time is greater than expiration, we are expired!
    BOOST_CHECK(findExpiredOutMessage((void *)&futureTime, &outMessage));
    BOOST_CHECK(!findExpiredOutMessage((void *)&historicalTime, &outMessage));
}

BOOST_AUTO_TEST_CASE(test_csv_output_iterator)
{
    std::ostringstream ostr;
    std::vector<long> source;

    source.push_back(1);
    source.push_back(1);
    source.push_back(2);
    source.push_back(3);
    source.push_back(5);
    source.push_back(8);

    csv_output_iterator<long, std::ostringstream> csv_itr(ostr);

    std::copy(source.begin(), source.end(), csv_itr);

    BOOST_CHECK_EQUAL(ostr.str(), "1,1,2,3,5,8");
}

BOOST_AUTO_TEST_CASE(test_padded_output_iterator)
{
    {
        std::ostringstream ostr;
        std::vector<long> source;

        std::copy(source.begin(), source.end(), padded_output_iterator<long, std::ostringstream>(ostr, '0', 2));

        BOOST_CHECK_EQUAL(ostr.str(), "");
    }

    {
        std::ostringstream ostr;
        std::vector<long> source;

        source.push_back(1);

        std::copy(source.begin(), source.end(), padded_output_iterator<long, std::ostringstream>(ostr, '0', 2));

        BOOST_CHECK_EQUAL(ostr.str(), "01");
    }

    {
        std::ostringstream ostr;
        std::vector<long> source;

        source.push_back(0);
        source.push_back(1);
        source.push_back(1);
        source.push_back(2);
        source.push_back(3);
        source.push_back(5);
        source.push_back(8);
        source.push_back(13);
        source.push_back(21);
        source.push_back(34);
        source.push_back(55);
        source.push_back(89);
        source.push_back(144);
        source.push_back(233);

        std::copy(source.begin(), source.end(), padded_output_iterator<long, std::ostringstream>(ostr, '0', 4));

        BOOST_CHECK_EQUAL(ostr.str(), "0000 0001 0001 0002 0003 0005 0008 0013 0021 0034 0055 0089 0144 0233");
    }

    {
        std::ostringstream ostr;
        std::vector<long> source;

        source.push_back(0);
        source.push_back(1);
        source.push_back(1);
        source.push_back(2);
        source.push_back(3);
        source.push_back(5);
        source.push_back(8);
        source.push_back(13);
        source.push_back(21);
        source.push_back(34);
        source.push_back(55);
        source.push_back(89);
        source.push_back(144);
        source.push_back(233);

        ostr << std::hex;

        std::copy(source.begin(), source.end(), padded_output_iterator<long, std::ostringstream>(ostr, '0', 2));

        BOOST_CHECK_EQUAL(ostr.str(), "00 01 01 02 03 05 08 0d 15 22 37 59 90 e9");
    }
}

BOOST_AUTO_TEST_CASE(test_isExpresscomGroup)
{
    //True cases
    bool ret = isExpresscomGroup(TYPE_LMGROUP_EXPRESSCOM);
    BOOST_CHECK_EQUAL(ret, true);
    ret = isExpresscomGroup(TYPE_LMGROUP_RFN_EXPRESSCOM);
    BOOST_CHECK_EQUAL(ret, true);

    //False cases
    for (int i = 0; i < 3000; ++i)
    {
        if( i != TYPE_LMGROUP_EXPRESSCOM && i != TYPE_LMGROUP_RFN_EXPRESSCOM )
        {
            ret = isExpresscomGroup(i);
            BOOST_CHECK_EQUAL(ret, false);
        }
    }
}

BOOST_AUTO_TEST_CASE(test_nextScheduledTimeAlignedOnRate_specialValues)
{
    // Remeber ctidate is CtiDate(day, month, year)
    CtiTime originTime = CtiTime::CtiTime(CtiDate::CtiDate(1,1,2000), 1, 1, 0);
    BOOST_CHECK_EQUAL( CtiTime::CtiTime(CtiDate::CtiDate(1,1,2000), 2, 0, 0), nextScheduledTimeAlignedOnRate( originTime, 3600 )); //Aligned on hour
    BOOST_CHECK_EQUAL( CtiTime::CtiTime(CtiDate::CtiDate(2,1,2000), 0, 0, 0), nextScheduledTimeAlignedOnRate( originTime, 86400 )); //Aligned on day
    BOOST_CHECK_EQUAL( CtiTime::CtiTime(CtiDate::CtiDate(2,1,2000), 0, 0, 0), nextScheduledTimeAlignedOnRate( originTime, 604800 )); //Aligned on week = midnight Sunday
    BOOST_CHECK_EQUAL( CtiTime::CtiTime(CtiDate::CtiDate(1,2,2000), 0, 0, 0), nextScheduledTimeAlignedOnRate( originTime, 2592000 )); //Aligned on month = midnight 1st
}

BOOST_AUTO_TEST_SUITE_END()
