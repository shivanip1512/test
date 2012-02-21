#include <boost/test/unit_test.hpp>

#include "utility.h"

#include <set>

BOOST_AUTO_TEST_SUITE( test_multiset )

struct fun_pair : public std::pair<int, int>
{
    fun_pair(int f, int s) : std::pair<int, int>(f, s) { };

    bool operator<(const fun_pair &rhs) const
    {
        return first < rhs.first;
    };
};

BOOST_AUTO_TEST_CASE(test_multiset_order)
{
    std::multiset<fun_pair> multiset_test;

    //  digits of pi, in fifo priority sorted chunks, mixed by hand
    multiset_test.insert(fun_pair(2, 1));
    multiset_test.insert(fun_pair(3, 9));
    multiset_test.insert(fun_pair(1, 3));
    multiset_test.insert(fun_pair(1, 1));
    multiset_test.insert(fun_pair(1, 4));
    multiset_test.insert(fun_pair(3, 2));
    multiset_test.insert(fun_pair(2, 5));
    multiset_test.insert(fun_pair(3, 6));

    std::multiset<fun_pair>::iterator itr = multiset_test.begin();

    BOOST_CHECK_EQUAL(itr->first,  1);
    BOOST_CHECK_EQUAL(itr->second, 3);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  1);
    BOOST_CHECK_EQUAL(itr->second, 1);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  1);
    BOOST_CHECK_EQUAL(itr->second, 4);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  2);
    BOOST_CHECK_EQUAL(itr->second, 1);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  2);
    BOOST_CHECK_EQUAL(itr->second, 5);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  3);
    BOOST_CHECK_EQUAL(itr->second, 9);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  3);
    BOOST_CHECK_EQUAL(itr->second, 2);
    itr++;

    BOOST_CHECK_EQUAL(itr->first,  3);
    BOOST_CHECK_EQUAL(itr->second, 6);
}

BOOST_AUTO_TEST_CASE(test_multiset_insert_erase)
{
    std::multiset<fun_pair> multiset_test;

    //  digits of pi, in fifo priority sorted chunks, mixed by hand
    multiset_test.insert(fun_pair(2, 1));
    multiset_test.insert(fun_pair(3, 9));
    multiset_test.insert(fun_pair(1, 3));
    multiset_test.insert(fun_pair(1, 1));
    multiset_test.insert(fun_pair(1, 4));
    multiset_test.insert(fun_pair(3, 2));
    multiset_test.insert(fun_pair(2, 5));
    multiset_test.insert(fun_pair(3, 6));

    BOOST_CHECK_EQUAL(multiset_test.size(), 8);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 7);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 6);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 5);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 4);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 3);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 2);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 1);

    multiset_test.erase(multiset_test.begin());
    BOOST_CHECK_EQUAL(multiset_test.size(), 0);

    BOOST_CHECK(multiset_test.empty());
}

struct priority_t
{
    int value;
    int Priority;
};

BOOST_AUTO_TEST_CASE(test_ptr_priority_sort)
{
    std::multiset<priority_t *, ptr_priority_sort<priority_t> > priority_test;

    priority_t a, b;

    a.value    = 0;
    a.Priority = 15;  //  high priority, should come off first

    b.value    = 1;
    b.Priority = 7;   //  low priority, should come off second

    priority_test.insert(&b);
    priority_test.insert(&a);

    std::multiset<priority_t *, ptr_priority_sort<priority_t> >::iterator itr = priority_test.begin();

    BOOST_CHECK_EQUAL(*itr, &a);
    itr++;
    BOOST_CHECK_EQUAL(*itr, &b);
}

BOOST_AUTO_TEST_SUITE_END()
