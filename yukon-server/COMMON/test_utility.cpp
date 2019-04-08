#include <boost/test/unit_test.hpp>

#include <vector>
#include <map>
#include "utility.h"
#include "ctidate.h"
#include "dsm2.h"
#include "queues.h"
#include "devicetypes.h"

BOOST_AUTO_TEST_SUITE( test_utility )

BOOST_AUTO_TEST_CASE( test_find_gcd )
{
    using Cti::find_gcd;

    BOOST_CHECK_EQUAL(    1, find_gcd(3, 7));
    BOOST_CHECK_EQUAL(    3, find_gcd(3, 15));
    BOOST_CHECK_EQUAL(   13, find_gcd(13, 13));
    BOOST_CHECK_EQUAL(    1, find_gcd(37, 600));
    BOOST_CHECK_EQUAL(   20, find_gcd(20, 100));
    BOOST_CHECK_EQUAL(18913, find_gcd(624129, 2061517));
}

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

    ostr << Cti::join(source, ",");

    BOOST_CHECK_EQUAL(ostr.str(), "1,1,2,3,5,8");
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

BOOST_AUTO_TEST_CASE(test_convertHexStringToBytes)
{
    // test the empty string produces an empty array
    {
        std::string input;

        std::vector< unsigned char >    result;

        convertHexStringToBytes( input, result );

        BOOST_CHECK_EQUAL(     0, result.size() );
    }

    // normal data
    {
        std::string input( "f9a6d72d7284cc51d4b0379ac30659562d4de98f3b7ce48f13c69f1ec5d939cb"
                           "22435b8511724fe9753b0ba35bf00beaed1f6c250325c6db4ffe1a28605474f9"
                           "6fc3037dd5b24bbd726b8b8a6a34d563b1c0655e0eb67c8c038883810ca92b37"
                           "254d2820ce81d21a7b8b04f7249a18fdb8e2da930459bfef38cb91d6b1b72fa9"
                           "c6f3773e877bf7ab55ec151bd6bed13cd44a0a51e6543a9ea9c8651b0e294e13"
                           "c14a02b384e03ff22dd2774cfb278d40" );

        const unsigned char inputAsArray[] =
        {
            0xf9, 0xa6, 0xd7, 0x2d, 0x72, 0x84, 0xcc, 0x51,
            0xd4, 0xb0, 0x37, 0x9a, 0xc3, 0x06, 0x59, 0x56,
            0x2d, 0x4d, 0xe9, 0x8f, 0x3b, 0x7c, 0xe4, 0x8f,
            0x13, 0xc6, 0x9f, 0x1e, 0xc5, 0xd9, 0x39, 0xcb,
            0x22, 0x43, 0x5b, 0x85, 0x11, 0x72, 0x4f, 0xe9,
            0x75, 0x3b, 0x0b, 0xa3, 0x5b, 0xf0, 0x0b, 0xea,
            0xed, 0x1f, 0x6c, 0x25, 0x03, 0x25, 0xc6, 0xdb,
            0x4f, 0xfe, 0x1a, 0x28, 0x60, 0x54, 0x74, 0xf9,
            0x6f, 0xc3, 0x03, 0x7d, 0xd5, 0xb2, 0x4b, 0xbd,
            0x72, 0x6b, 0x8b, 0x8a, 0x6a, 0x34, 0xd5, 0x63,
            0xb1, 0xc0, 0x65, 0x5e, 0x0e, 0xb6, 0x7c, 0x8c,
            0x03, 0x88, 0x83, 0x81, 0x0c, 0xa9, 0x2b, 0x37,
            0x25, 0x4d, 0x28, 0x20, 0xce, 0x81, 0xd2, 0x1a,
            0x7b, 0x8b, 0x04, 0xf7, 0x24, 0x9a, 0x18, 0xfd,
            0xb8, 0xe2, 0xda, 0x93, 0x04, 0x59, 0xbf, 0xef,
            0x38, 0xcb, 0x91, 0xd6, 0xb1, 0xb7, 0x2f, 0xa9,
            0xc6, 0xf3, 0x77, 0x3e, 0x87, 0x7b, 0xf7, 0xab,
            0x55, 0xec, 0x15, 0x1b, 0xd6, 0xbe, 0xd1, 0x3c,
            0xd4, 0x4a, 0x0a, 0x51, 0xe6, 0x54, 0x3a, 0x9e,
            0xa9, 0xc8, 0x65, 0x1b, 0x0e, 0x29, 0x4e, 0x13,
            0xc1, 0x4a, 0x02, 0xb3, 0x84, 0xe0, 0x3f, 0xf2,
            0x2d, 0xd2, 0x77, 0x4c, 0xfb, 0x27, 0x8d, 0x40
        };

        std::vector< unsigned char >    result;

        convertHexStringToBytes( input, result );

        BOOST_CHECK_EQUAL_COLLECTIONS( result.begin(),  result.end(),
                                       inputAsArray,    inputAsArray + sizeof(inputAsArray) );
    }

    // check odd length string -- zero pad & return true
    {
        std::string input( "9a6d72d7284cc51d4b0379ac30659562d4de98f3b7ce48f13c69f1ec5d939cb" );

        const unsigned char inputAsArray[] =
        {
            0x09, 0xa6, 0xd7, 0x2d, 0x72, 0x84, 0xcc, 0x51,
            0xd4, 0xb0, 0x37, 0x9a, 0xc3, 0x06, 0x59, 0x56,
            0x2d, 0x4d, 0xe9, 0x8f, 0x3b, 0x7c, 0xe4, 0x8f,
            0x13, 0xc6, 0x9f, 0x1e, 0xc5, 0xd9, 0x39, 0xcb
        };

        std::vector< unsigned char >    result;

        convertHexStringToBytes( input, result );

        BOOST_CHECK_EQUAL_COLLECTIONS( result.begin(),  result.end(),
                                       inputAsArray,    inputAsArray + sizeof(inputAsArray) );
    }
}

BOOST_AUTO_TEST_CASE(test_toBase94)
{
    BOOST_CHECK_EQUAL(toBase94(0), "!!!");
    BOOST_CHECK_EQUAL(toBase94(1), "!!\"");
    BOOST_CHECK_EQUAL(toBase94(2), "!!#");
    BOOST_CHECK_EQUAL(toBase94(3), "!!$");

    BOOST_CHECK_EQUAL(toBase94(47), "!!P");

    BOOST_CHECK_EQUAL(toBase94(92), "!!}");
    BOOST_CHECK_EQUAL(toBase94(93), "!!~");
    BOOST_CHECK_EQUAL(toBase94(94), "!\"!");
    BOOST_CHECK_EQUAL(toBase94(95), "!\"\"");
    BOOST_CHECK_EQUAL(toBase94(96), "!\"#");

    BOOST_CHECK_EQUAL(toBase94(141), "!\"P");
    BOOST_CHECK_EQUAL(toBase94(142), "!\"Q");
    BOOST_CHECK_EQUAL(toBase94(143), "!\"R");

    BOOST_CHECK_EQUAL(toBase94(186), "!\"}");
    BOOST_CHECK_EQUAL(toBase94(187), "!\"~");
    BOOST_CHECK_EQUAL(toBase94(188), "!#!");
    BOOST_CHECK_EQUAL(toBase94(189), "!#\"");
    BOOST_CHECK_EQUAL(toBase94(190), "!##");

    BOOST_CHECK_EQUAL(toBase94(830582), "~~}");
    BOOST_CHECK_EQUAL(toBase94(830583), "~~~");
    BOOST_CHECK_EQUAL(toBase94(830584), "\"!!!");
    BOOST_CHECK_EQUAL(toBase94(830585), "\"!!\"");

    BOOST_CHECK_EQUAL(toBase94(78074894), "~~~}");
    BOOST_CHECK_EQUAL(toBase94(78074895), "~~~~");
    BOOST_CHECK_EQUAL(toBase94(78074896), "\"!!!!");
    BOOST_CHECK_EQUAL(toBase94(78074897), "\"!!!\"");

    BOOST_CHECK_EQUAL(toBase94(std::numeric_limits<int16_t>::max()), "$cX");
    BOOST_CHECK_EQUAL(toBase94(std::numeric_limits<int32_t>::max()), "<PP}d");
    BOOST_CHECK_EQUAL(toBase94(std::numeric_limits<int64_t>::max()), "1**0#VEx9D");
}

BOOST_AUTO_TEST_CASE(test_stringCompareIgnoreCase)
{
    std::string s1 = "My Compare";
    std::string s2 = "my cOmParE";
    BOOST_CHECK_EQUAL( ciStringEqual(s1, s2),true );
}

BOOST_AUTO_TEST_SUITE_END()
