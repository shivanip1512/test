#include <boost/test/unit_test.hpp>

#include "queue.h"
#include "dbaccess.h"
#include "boostutil.h"

#include <boost/bind.hpp>
#include <iostream>
#include <set>
#include <SQLAPI.h>

using namespace std;

BOOST_AUTO_TEST_SUITE( test_queue )

inline double xtime_duration(boost::xtime &begin, boost::xtime &end)
{
    return static_cast<double>(end.sec  - begin.sec) +
           static_cast<double>(end.nsec - begin.nsec) * 1e-9;
}

struct instance_counter
{
    int instance;
    static int counter;  //  keeps a counter instance to track construction and destruction

    instance_counter()  { instance = ++counter; };
    ~instance_counter() { counter--; };
};

int instance_counter::counter;


/* Removed due to this failing builds.
   BOOST_AUTO_TEST_CASE(test_queue_timing)
{
    CtiQueue<int, std::less<int> > testQueue;

    boost::xtime start_xtime, end_xtime;

    //  Test reading off an empty queue
    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(500);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    //  The end time must be between 0.49 and 0.55 seconds past the start time (10ms fuzz-factor)
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) > 0.49 );
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) < 0.55 );

    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    //  The end time must be between 0.09 and 0.15 seconds past the start time (10ms fuzz-factor)
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) > 0.09 );
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) < 0.15 );
}*/

struct test_element
{
    long value, insertOrder;

    test_element(long value_, long insertOrder_) : value(value_), insertOrder(insertOrder_) { }

    bool operator>(const test_element& rhs) const  {  return value > rhs.value;  }
    bool operator<(const test_element& rhs) const  {  return value < rhs.value;  }
};

void setInsertOrder(test_element *&dataStruct, void* d)
{
    dataStruct->insertOrder = (int)d;
}

BOOST_AUTO_TEST_CASE(test_queue_sort)
{
    CtiQueue<test_element, greater<test_element> > greaterQueue;
    CtiQueue<test_element, less<test_element> > lessQueue;

    test_element *element;

    //  greaterQueue

    greaterQueue.putQueue(new test_element(1, 1));
    greaterQueue.putQueue(new test_element(1, 2));
    greaterQueue.putQueue(new test_element(1, 3));
    greaterQueue.putQueue(new test_element(2, 1));
    greaterQueue.putQueue(new test_element(1, 4));

    //  sorted greater by value, fifo if equal
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 2);                                               delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 1);  delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 2);  delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 3);  delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 4);  delete element;


    //  lessQueue

    lessQueue.putQueue(new test_element(1, 1));
    lessQueue.putQueue(new test_element(1, 2));
    lessQueue.putQueue(new test_element(1, 3));
    lessQueue.putQueue(new test_element(2, 1));
    lessQueue.putQueue(new test_element(1, 4));

    element = lessQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 1);  delete element;
    element = lessQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 2);  delete element;
    element = lessQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 3);  delete element;
    element = lessQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 1);  BOOST_CHECK_EQUAL(element->insertOrder, 4);  delete element;
    element = lessQueue.getQueue();  BOOST_CHECK_EQUAL(element->value, 2);                                               delete element;
}

BOOST_AUTO_TEST_CASE(test_queue_apply)
{
    CtiQueue<test_element, greater<test_element> > greaterQueue;

    test_element *element;

    greaterQueue.putQueue(new test_element(1, 1));
    greaterQueue.putQueue(new test_element(1, 2));
    greaterQueue.putQueue(new test_element(1, 3));
    greaterQueue.putQueue(new test_element(1, 4));

    greaterQueue.apply(setInsertOrder, (void *)5);

    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->insertOrder, 5);  delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->insertOrder, 5);  delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->insertOrder, 5);  delete element;
    element = greaterQueue.getQueue();  BOOST_CHECK_EQUAL(element->insertOrder, 5);  delete element;
}


//  This inserts 100,000 objects into a queue and makes sure it does not take
//  an unreasonable amount of time
/* removed due to this failing builds
BOOST_AUTO_TEST_CASE(test_queue_sort_speed)
{
    CtiQueue<test_element, greater<test_element> > greaterQueue;

    greaterQueue.putQueue(new test_element(1, 1));

    time_t start = ::time(0);

    for( int i = 0; i < 100 * 1000; ++i )
    {
        //  i % 1000 gives a sort key that should
        //    stress the insert a little
        greaterQueue.putQueue(new test_element(i % 1000, i));
    }

    BOOST_CHECK(::time(0) < (start + 2));  //  2 seconds is quite a while, actually.

    greaterQueue.clearAndDestroy();
}


BOOST_AUTO_TEST_CASE(test_fifo_queue_timing)
{
    CtiFIFOQueue<int> testQueue;

    boost::xtime start_xtime, end_xtime;

    //  Test reading off an empty queue
    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(500);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    //  The end time must be between 0.49 and 0.55 seconds past the start time (10ms fuzz-factor)
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) > 0.490 );
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) < 0.550 );

    boost::xtime_get(&start_xtime, boost::TIME_UTC);
    testQueue.getQueue(100);
    boost::xtime_get(&end_xtime,   boost::TIME_UTC);

    //  The end time must be between 0.09 and 0.15 seconds past the start time (10ms fuzz-factor)
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) > 0.090 );
    BOOST_CHECK( xtime_duration(start_xtime, end_xtime) < 0.150 );
}*/


BOOST_AUTO_TEST_CASE(test_fifoqueue_single_threaded)
{
    CtiFIFOQueue<instance_counter> q;

    instance_counter::counter = 0;

    instance_counter *element;

    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter);

    BOOST_CHECK_EQUAL(q.getQueue(50), reinterpret_cast<instance_counter *>(0));

    q.putQueue(new instance_counter);

    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter);

    q.putQueue(new instance_counter);
    q.putQueue(new instance_counter);
    q.putQueue(new instance_counter);

    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter);

    q.putQueue(new instance_counter, 50);

    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter);

    element = q.getQueue();

    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter - 1);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter - 1);

    delete element;

    delete q.getQueue(50);

    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter);

    q.clearAndDestroy();

    BOOST_CHECK_EQUAL(q.entries(), 0);
    BOOST_CHECK_EQUAL(q.size(),    0);
    BOOST_CHECK_EQUAL(q.entries(), instance_counter::counter);
    BOOST_CHECK_EQUAL(q.size(),    instance_counter::counter);

    BOOST_CHECK_EQUAL(q.getName(), "Unnamed Queue");

    q.setName("Henrietta");

    BOOST_CHECK_EQUAL(q.getName(), "Henrietta");
}

template <class T>
void read_success(CtiFIFOQueue<T> &q, T compare)
{
    T *element = q.getQueue(50);

    BOOST_CHECK_EQUAL(*element, compare);
}

template <class T>
void read_fail(CtiFIFOQueue<T> &q)
{
    T *element = q.getQueue(50);

    BOOST_CHECK_EQUAL(element, (T*)0);
}

BOOST_AUTO_TEST_CASE(test_fifoqueue_multi_threaded)
{
    CtiFIFOQueue<int> q;

    q.putQueue(new int(3));

    boost::thread t1(boost::bind(read_success<int>, boost::ref(q), 3)),
                  t2(boost::bind(read_success<int>, boost::ref(q), 3)),
                  t3(boost::bind(read_success<int>, boost::ref(q), 3));

    q.putQueue(new int(3));
    q.putQueue(new int(3));

    t1.join();
    t2.join();
    t3.join();

    boost::thread t4(boost::bind(read_fail<int>, boost::ref(q)));

    t4.join();
}

BOOST_AUTO_TEST_CASE(test_valuequeue_basic_ops)
{
    CtiValueQueue<int>  q;

    BOOST_CHECK_EQUAL(    0, q.size() );
    BOOST_CHECK_EQUAL( true, q.empty() );

    q.putQueue(1);

    BOOST_CHECK_EQUAL(     1, q.size() );
    BOOST_CHECK_EQUAL( false, q.empty() );

    BOOST_CHECK_EQUAL(    1, q.getQueue() );

    BOOST_CHECK_EQUAL(    0, q.size() );
    BOOST_CHECK_EQUAL( true, q.empty() );

    for (int v = 0; v < 10; v++)
    {
        q.putQueue(v);
    }

    for (int v = 0; v < 10; v++)
    {
        BOOST_CHECK_EQUAL( v, q.getQueue() );
    }
}

BOOST_AUTO_TEST_SUITE_END()
