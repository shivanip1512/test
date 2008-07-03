#define BOOST_AUTO_TEST_MAIN "Test fifo_multiset"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>

#include <iostream>
#include "fifo_multiset.h"

using namespace std;

using boost::unit_test_framework::test_suite;

struct fun_pair : public pair<int, int>
{
    fun_pair(int f, int s) : pair<int, int>(f, s) { };

    bool operator<(const fun_pair &rhs) const
    {
        return first < rhs.first;
    };
};

BOOST_AUTO_UNIT_TEST(test_fifo_multiset_order)
{
    fifo_multiset<fun_pair> fifo_multiset_test;

    //  digits of pi, in fifo priority sorted chunks, mixed by hand
    fifo_multiset_test.insert(fun_pair(2, 1));
    fifo_multiset_test.insert(fun_pair(3, 9));
    fifo_multiset_test.insert(fun_pair(1, 3));
    fifo_multiset_test.insert(fun_pair(1, 1));
    fifo_multiset_test.insert(fun_pair(1, 4));
    fifo_multiset_test.insert(fun_pair(3, 2));
    fifo_multiset_test.insert(fun_pair(2, 5));
    fifo_multiset_test.insert(fun_pair(3, 6));

    fifo_multiset<fun_pair>::iterator itr = fifo_multiset_test.begin();

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

BOOST_AUTO_UNIT_TEST(test_fifo_multiset_insert_erase)
{
    fifo_multiset<fun_pair> fifo_multiset_test;

    //  digits of pi, in fifo priority sorted chunks, mixed by hand
    fifo_multiset_test.insert(fun_pair(2, 1));
    fifo_multiset_test.insert(fun_pair(3, 9));
    fifo_multiset_test.insert(fun_pair(1, 3));
    fifo_multiset_test.insert(fun_pair(1, 1));
    fifo_multiset_test.insert(fun_pair(1, 4));
    fifo_multiset_test.insert(fun_pair(3, 2));
    fifo_multiset_test.insert(fun_pair(2, 5));
    fifo_multiset_test.insert(fun_pair(3, 6));

    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 8);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 7);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 6);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 5);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 4);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 3);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 2);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 1);

    fifo_multiset_test.erase(fifo_multiset_test.begin());
    BOOST_CHECK_EQUAL(fifo_multiset_test.size(), 0);

    BOOST_CHECK(fifo_multiset_test.empty());
}

struct priority_t
{
    int value;
    int Priority;
};

BOOST_AUTO_UNIT_TEST(test_ptr_priority_sort)
{
    fifo_multiset<priority_t *, ptr_priority_sort<priority_t> > priority_test;

    priority_t a, b;

    a.value    = 0;
    a.Priority = 15;  //  high priority, should come off first

    b.value    = 1;
    b.Priority = 7;   //  low priority, should come off second

    priority_test.insert(&b);
    priority_test.insert(&a);

    fifo_multiset<priority_t *, ptr_priority_sort<priority_t> >::iterator itr = priority_test.begin();

    BOOST_CHECK_EQUAL(*itr, &a);
    itr++;
    BOOST_CHECK_EQUAL(*itr, &b);
}

