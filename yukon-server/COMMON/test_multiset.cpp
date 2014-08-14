#include <boost/test/unit_test.hpp>
#include <boost/ptr_container/ptr_set.hpp>

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

BOOST_AUTO_TEST_CASE(test_priority_sort)
{
    boost::ptr_multiset<priority_t, priority_sort<priority_t> > priority_test;

    std::auto_ptr<priority_t> a, b, c;

    a.reset(new priority_t);
    a->value    = 1;
    a->Priority = 15;  //  high priority, should come off first

    b.reset(new priority_t);
    b->value    = 2;
    b->Priority = 7;   //  low priority, should come off second

    c.reset(new priority_t);
    c->value    = 3;
    c->Priority = 7;   //  same priority as b, if inserted before it should come off before b

    priority_test.insert(c.release());
    priority_test.insert(b.release());
    priority_test.insert(a.release());

    boost::ptr_multiset<priority_t, priority_sort<priority_t> >::iterator itr = priority_test.begin();

    BOOST_CHECK_EQUAL(itr->value,    1); // a
    BOOST_CHECK_EQUAL(itr->Priority, 15);
    itr++;
    BOOST_CHECK_EQUAL(itr->value,    3); // c
    BOOST_CHECK_EQUAL(itr->Priority, 7);
    itr++;
    BOOST_CHECK_EQUAL(itr->value,    2); // b
    BOOST_CHECK_EQUAL(itr->Priority, 7);
}

BOOST_AUTO_TEST_SUITE_END()
