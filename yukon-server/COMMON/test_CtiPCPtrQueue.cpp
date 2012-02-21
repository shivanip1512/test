#include <boost/test/unit_test.hpp>

#include "ctipcptrqueue.h"
#include <boost/thread/thread.hpp>

using namespace std;

BOOST_AUTO_TEST_SUITE( test_CtiPCPtrQueue )

int threadTest = 0;
CtiPCPtrQueue<int> threadQ;
boost::mutex mutex;

BOOST_AUTO_TEST_CASE(test_writeread)
{
    CtiPCPtrQueue<int> *q = new CtiPCPtrQueue<int>();
    int *itemOne, *itemTwo, *temp1, *temp2, *garbage;
    itemOne = new int(1);
    itemTwo = new int(2);
    bool tester;

    //Testing empty on an empty queue.
    BOOST_CHECK_EQUAL( q->empty(), true );

    //Testing canRead on an empty queue.
    BOOST_CHECK_EQUAL( q->canRead(), false );
    q->close();

    //Testing write when queue is closed.
    BOOST_CHECK_EQUAL( q->write(itemOne), false );
    q->open();

    //Testing write when open
    BOOST_CHECK_EQUAL( q->write(itemOne), true);
    BOOST_CHECK_EQUAL( q->write(itemTwo), true);

    //Testing empty when something is on the queue.
    BOOST_CHECK_EQUAL( q->empty(), false );

    //Testing canRead when something is on the queue.
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
    struct boost::xtime startTime, endTime;
    boost::xtime_get(&startTime, boost::TIME_UTC);
    q->read(garbage, 1000);
    boost::xtime_get(&endTime, boost::TIME_UTC);

    //For the purposes of this test, the stop time is not so important.
    //The stop time is set a way out because we wont want busy system false positives.
    BOOST_CHECK_EQUAL(garbage == NULL, true);
    BOOST_CHECK( (unsigned int)startTime.sec < (unsigned int)endTime.sec );
    BOOST_CHECK( (unsigned int)startTime.sec + 4 > (unsigned int)endTime.sec );

    boost::xtime_get(&startTime, boost::TIME_UTC);
    q->read(garbage, 100);
    boost::xtime_get(&endTime, boost::TIME_UTC);

    if( (unsigned int)startTime.sec == (unsigned int)endTime.sec )
    {
        unsigned int startMS = startTime.nsec/1000000;
        unsigned int stopMS = endTime.nsec/1000000;
        BOOST_CHECK( startMS + 75 < stopMS );
        BOOST_CHECK( startMS + 300 > stopMS );
    }
    else
    {
        unsigned int startMS = startTime.nsec/1000000;
        unsigned int stopMS = endTime.nsec/1000000 + 1000;
        BOOST_CHECK( startMS + 75 < stopMS );
        BOOST_CHECK( startMS + 300 > stopMS );
    }

    //Test reading off a closed empty queue
    q->close();
    q->read(garbage, 20);
    BOOST_CHECK_EQUAL(garbage == NULL, true);


}

BOOST_AUTO_TEST_CASE(test_tryRead)
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

BOOST_AUTO_TEST_CASE(test_timeOutMultiThread)
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

BOOST_AUTO_TEST_SUITE_END()
