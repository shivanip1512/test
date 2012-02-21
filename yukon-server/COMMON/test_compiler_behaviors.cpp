#include <boost/test/unit_test.hpp>

#include <set>

BOOST_AUTO_TEST_SUITE( test_compiler_behaviors )

BOOST_AUTO_TEST_CASE(test_modulus_sign)
{
    BOOST_CHECK_EQUAL( 12 %  10,  2);
    BOOST_CHECK_EQUAL( 12 % -10,  2);
    BOOST_CHECK_EQUAL(-12 %  10, -2);
    BOOST_CHECK_EQUAL(-12 % -10, -2);
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
