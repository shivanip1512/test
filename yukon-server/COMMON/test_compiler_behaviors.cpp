#include <boost/test/unit_test.hpp>

#include <boost/cstdint.hpp>

#include <set>

BOOST_AUTO_TEST_SUITE( test_compiler_behaviors )

BOOST_AUTO_TEST_CASE(test_modulus_sign)
{
    BOOST_CHECK_EQUAL( 12 %  10,  2);
    BOOST_CHECK_EQUAL( 12 % -10,  2);
    BOOST_CHECK_EQUAL(-12 %  10, -2);
    BOOST_CHECK_EQUAL(-12 % -10, -2);
}

BOOST_AUTO_TEST_CASE(test_sizeof_floats)
{
    //  Boost
    BOOST_CHECK_EQUAL(8, sizeof(boost::uint64_t));
    BOOST_CHECK_EQUAL(4, sizeof(boost::uint32_t));
    BOOST_CHECK_EQUAL(2, sizeof(boost::uint16_t));
    BOOST_CHECK_EQUAL(1, sizeof(boost::uint8_t));
    BOOST_CHECK_EQUAL(8, sizeof(boost::int64_t));
    BOOST_CHECK_EQUAL(4, sizeof(boost::int32_t));
    BOOST_CHECK_EQUAL(2, sizeof(boost::int16_t));
    BOOST_CHECK_EQUAL(1, sizeof(boost::int8_t));

    //  Microsoft extensions
    BOOST_CHECK_EQUAL(8, sizeof(unsigned __int64));
    BOOST_CHECK_EQUAL(4, sizeof(unsigned __int32));
    BOOST_CHECK_EQUAL(2, sizeof(unsigned __int16));
    BOOST_CHECK_EQUAL(1, sizeof(unsigned __int8));
    BOOST_CHECK_EQUAL(8, sizeof(__int64));
    BOOST_CHECK_EQUAL(4, sizeof(__int32));
    BOOST_CHECK_EQUAL(2, sizeof(__int16));
    BOOST_CHECK_EQUAL(1, sizeof(__int8));

    //  C++11 / Microsoft VS2008 extension
    BOOST_CHECK_EQUAL(8, sizeof(long long));
    BOOST_CHECK_EQUAL(8, sizeof(unsigned long long));

    //  C++03 / C++11
    BOOST_CHECK_EQUAL(4, sizeof(float));
    BOOST_CHECK_EQUAL(8, sizeof(double));

    BOOST_CHECK_EQUAL(4, sizeof(unsigned long));
    BOOST_CHECK_EQUAL(4, sizeof(unsigned int));
    BOOST_CHECK_EQUAL(2, sizeof(unsigned short));
    BOOST_CHECK_EQUAL(1, sizeof(unsigned char));

    BOOST_CHECK_EQUAL(4, sizeof(long));
    BOOST_CHECK_EQUAL(4, sizeof(int));
    BOOST_CHECK_EQUAL(2, sizeof(short));
    BOOST_CHECK_EQUAL(1, sizeof(char));

    BOOST_CHECK_EQUAL(1, sizeof(bool));
}

struct ordering_test
{
    const int first;
    const int second;

    ordering_test(int x, int y) :
        first(x),
        second(y)
    {
    }

    bool operator<(const ordering_test &rhs) const
    {
        return first < rhs.first;
    }
};

BOOST_AUTO_TEST_CASE(test_multiset_insert_order)
{
    std::multiset<ordering_test> t;

    t.insert(ordering_test(3,1));
    t.insert(ordering_test(3,2));
    t.insert(ordering_test(3,3));
    t.insert(ordering_test(3,4));
    t.insert(ordering_test(3,5));

    {
        std::multiset<ordering_test>::const_iterator itr = t.begin();

        BOOST_CHECK_EQUAL(itr++->second, 1);
        BOOST_CHECK_EQUAL(itr++->second, 2);
        BOOST_CHECK_EQUAL(itr++->second, 3);
        BOOST_CHECK_EQUAL(itr++->second, 4);
        BOOST_CHECK_EQUAL(itr++->second, 5);
    }

    t.insert(ordering_test(1,6));

    {
        std::multiset<ordering_test>::const_iterator itr = t.begin();

        BOOST_CHECK_EQUAL(itr++->second, 6);
        BOOST_CHECK_EQUAL(itr++->second, 1);
        BOOST_CHECK_EQUAL(itr++->second, 2);
        BOOST_CHECK_EQUAL(itr++->second, 3);
        BOOST_CHECK_EQUAL(itr++->second, 4);
        BOOST_CHECK_EQUAL(itr++->second, 5);
    }

    t.insert(ordering_test(4,7));

    {
        std::multiset<ordering_test>::const_iterator itr = t.begin();

        BOOST_CHECK_EQUAL(itr++->second, 6);
        BOOST_CHECK_EQUAL(itr++->second, 1);
        BOOST_CHECK_EQUAL(itr++->second, 2);
        BOOST_CHECK_EQUAL(itr++->second, 3);
        BOOST_CHECK_EQUAL(itr++->second, 4);
        BOOST_CHECK_EQUAL(itr++->second, 5);
        BOOST_CHECK_EQUAL(itr++->second, 7);
    }

    t.insert(ordering_test(4,8));

    {
        std::multiset<ordering_test>::const_iterator itr = t.begin();

        BOOST_CHECK_EQUAL(itr++->second, 6);
        BOOST_CHECK_EQUAL(itr++->second, 1);
        BOOST_CHECK_EQUAL(itr++->second, 2);
        BOOST_CHECK_EQUAL(itr++->second, 3);
        BOOST_CHECK_EQUAL(itr++->second, 4);
        BOOST_CHECK_EQUAL(itr++->second, 5);
        BOOST_CHECK_EQUAL(itr++->second, 7);
        BOOST_CHECK_EQUAL(itr++->second, 8);
    }
}

BOOST_AUTO_TEST_SUITE_END()
