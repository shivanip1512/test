/*
 * File test_CtiPCPtrQueue.cpp
 *  
 * Author: Thain Spar
 * Date: 03/15/2006 16:55:13 
 * 
 * Testing the Cti Producer Consumer Pointer Queue
 *
 */
#define BOOST_AUTO_TEST_MAIN "Test CtiPCPtrQueue"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include "ctipcptrqueue.h"
using namespace std;


using boost::unit_test_framework::test_suite;

int threadTest = 0;
CtiPCPtrQueue<int> threadQ;
boost::mutex mutex;

BOOST_AUTO_UNIT_TEST(test_writeread)
{
    CtiPCPtrQueue<int> *q = new CtiPCPtrQueue<int>();
    int *itemOne, *itemTwo, *temp1, *temp2, *garbage;
    itemOne = new int(1);
    itemTwo = new int(2);
    bool tester;
    //Testing canRead on an empyt queue.
    BOOST_CHECK_EQUAL( q->canRead(), false );
    q->close();

    //Testing write when queue is closed.
    BOOST_CHECK_EQUAL( q->write(itemOne), false );
    q->open();

    //Testing write when open
    BOOST_CHECK_EQUAL( q->write(itemOne), true);
    BOOST_CHECK_EQUAL( q->write(itemTwo), true);

    //Testing canRead when something is onthe queue.
    BOOST_CHECK_EQUAL( q->canRead(), true );
    q->close();

    //Testing canRead on a closed occupied queue;
    BOOST_CHECK_EQUAL( q->canRead(), true );

    //Test reading off a closed queue.
    temp1 = q->read();
    BOOST_CHECK_EQUAL(temp1 , itemOne);
    q->open();

    //Test reading off an open queue
    temp2 = q->read();
    BOOST_CHECK_EQUAL( temp2, itemTwo );

    //Test reading off an empty queue
    q->read(garbage, 20);
    BOOST_CHECK_EQUAL(garbage == NULL, true);

    //Test reading off a closed empty queue
    q->close();
    q->read(garbage, 20);
    BOOST_CHECK_EQUAL(garbage == NULL, true);


}

BOOST_AUTO_UNIT_TEST(test_tryRead)
{
    CtiPCPtrQueue<int> *q = new CtiPCPtrQueue<int>();
    int *itemOne, *itemTwo, *temp1, *temp2, *gar;
    itemOne = new int(1);
    itemTwo = new int(2);
    bool tester;

    //read off empty open queue.
    tester = q->tryRead(gar);
    BOOST_CHECK_EQUAL(gar == NULL, true);
    BOOST_CHECK_EQUAL( tester, false);

    //read off empty closed queue
    q->close();
    tester = q->tryRead(gar);
    BOOST_CHECK_EQUAL(gar == NULL, true);
    BOOST_CHECK_EQUAL(tester, false);

    //read off occupied closed queue
    q->open();
    q->write(itemOne);
    q->write(itemTwo);
    q->close();
    tester = q->tryRead(temp1);
    BOOST_CHECK_EQUAL(temp1,itemOne);
    BOOST_CHECK_EQUAL(tester, true);

    //read off occipied open queue
    q->open();
    tester = q->tryRead(temp2);
    BOOST_CHECK_EQUAL(temp2, itemTwo);
    BOOST_CHECK_EQUAL(tester, true);
}

void readOne()//for MultiThread Test
{
   int *temp;
   bool tester;
   boost::mutex::scoped_lock lock(mutex);
   if ( threadTest < 3) {
       tester = threadQ.read(temp, 1000);
       BOOST_CHECK_EQUAL( *temp == threadTest+1, true);
       BOOST_CHECK_EQUAL( tester, true);
       ++threadTest;
   }else{
       tester = threadQ.read(temp, 1000);
       BOOST_CHECK_EQUAL( temp == NULL, true);
       BOOST_CHECK_EQUAL( tester, false);
       ++threadTest;
   }
}

BOOST_AUTO_UNIT_TEST(test_timeOutMultiThread)
{//first three should read fine, Last should time out.
    boost::thread_group threads;
    int *one   = new int(1), 
        *two   = new int(2), 
        *three = new int(3);
    threadQ.write     (one);
    threadQ.write     (two);
    threadQ.write   (three);
    for ( int i = 0; i < 4; ++i) {
        threads.create_thread( &readOne );
    }
    threads.join_all();

}

