#include <boost/test/unit_test.hpp>

#include "queue.h"
#include "dbaccess.h"
#include "boostutil.h"

#include <boost/bind.hpp>
#include <iostream>
#include <set>
#include <thread>

using namespace std;

BOOST_AUTO_TEST_SUITE( test_queue )

struct instance_counter
{
    int instance;
    static int counter;  //  keeps a counter instance to track construction and destruction

    instance_counter()  { instance = ++counter; };
    ~instance_counter() { counter--; };
};

int instance_counter::counter;


struct test_element
{
    long value;
    unsigned long insertOrder;

    test_element(long value_, unsigned long insertOrder_) : value(value_), insertOrder(insertOrder_) { }

    bool operator>(const test_element& rhs) const  {  return value > rhs.value;  }
    bool operator<(const test_element& rhs) const  {  return value < rhs.value;  }
};

void setInsertOrder(test_element *dataStruct, void* d)
{
    dataStruct->insertOrder = (unsigned long)d;
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
    T *element = q.getQueue(60'000);

    BOOST_CHECK_EQUAL(*element, compare);

    delete element;
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

    std::thread t1(read_success<int>, std::ref(q), 3),
                t2(read_success<int>, std::ref(q), 3),
                t3(read_success<int>, std::ref(q), 3);

    q.putQueue(new int(3));
    q.putQueue(new int(3));

    t1.join();
    t2.join();
    t3.join();

    std::thread t4(read_fail<int>, std::ref(q));

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

    BOOST_CHECK_EQUAL( 0, q.getQueue() );
    BOOST_CHECK_EQUAL( 1, q.getQueue() );
    BOOST_CHECK_EQUAL( 2, q.getQueue() );
    BOOST_CHECK_EQUAL( 3, q.getQueue() );
    BOOST_CHECK_EQUAL( 4, q.getQueue() );
    BOOST_CHECK_EQUAL( 5, q.getQueue() );
    BOOST_CHECK_EQUAL( 6, q.getQueue() );
    BOOST_CHECK_EQUAL( 7, q.getQueue() );
    BOOST_CHECK_EQUAL( 8, q.getQueue() );
    BOOST_CHECK_EQUAL( 9, q.getQueue() );
}

BOOST_AUTO_TEST_SUITE_END()
