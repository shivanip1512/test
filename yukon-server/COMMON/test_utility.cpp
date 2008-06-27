#define BOOST_AUTO_TEST_MAIN "Test utility.h"

#include <boost/test/auto_unit_test.hpp>
#include <boost/test/unit_test.hpp>

#include "utility.h"
#include <vector>
#include <map>

using boost::unit_test_framework::test_suite;

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

